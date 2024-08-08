package remote.wise.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.LoginRegistrationBO;
import remote.wise.exception.CustomFault;
import remote.wise.handler.SendEmailWithKafka;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.UserAuthenticationReqContract;
import remote.wise.service.datacontract.UserAuthenticationRespContract;
import remote.wise.service.datacontract.UserDetailsRespContract;
import remote.wise.service.implementation.HAJsendSMSpktImpl;
import remote.wise.service.implementation.UserDetailsImpl;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.ResponseObject;
public class NewUserRegistrationDao {

	Logger fLogger = FatalLoggerClass.logger;
	Logger iLogger = InfoLoggerClass.logger;
	public String insertuserDetails(String firstName, String lastName, String emailId, String mobileNumber,
			String zonalCode,String department,String loginTenancyId,String reason,String raisedBy,String approvedStatus,String approvedBy,String language,String countryCode,String userID,String roleID,String roleName,String timezone) {
		
		String status = "SUCCESS";
		
		ConnectMySQL connMySql = new ConnectMySQL();
		try (Connection prodConnection = connMySql.getConnection();
				Statement statement = prodConnection.createStatement()) {
			String query = "CREATE TABLE IF NOT EXISTS New_user_requests ("
				    + "unique_id INT AUTO_INCREMENT PRIMARY KEY, "
				    + "first_name VARCHAR(45) NOT NULL, "
				    + "last_name VARCHAR(45) NOT NULL, "
				    + "email_id VARCHAR(100) NOT NULL, "
				    + "mobile_number VARCHAR(20) NOT NULL, "
				    + "department VARCHAR(20), "
				    + "tenancy_id INT NOT NULL, "
				    + "zonal_code VARCHAR(255) NOT NULL, "
				    + "reason VARCHAR(255), "
				    + "raised_by VARCHAR(4) NOT NULL, "
				    + "raised_at DATETIME NOT NULL, "
				    + "approval_status VARCHAR(50), "
				    + "approved_by VARCHAR(45), "
				    + "approved_at DATETIME, "
				    + "language VARCHAR(45), "
				    + "countryCode VARCHAR(45), "
				    + "loginID VARCHAR(200), "
				    + "roleID VARCHAR(50), "
				    + "roleName VARCHAR(50), "
				    + "timezone VARCHAR(60) "
				    + ")";

        statement.execute(query);
        // Check if mobile number already exists
        String checkMobileQuery = "SELECT * FROM New_user_requests WHERE mobile_number = '" + mobileNumber + "' and approval_status='pending'";
        ResultSet mobileResult = statement.executeQuery(checkMobileQuery);
        if (mobileResult.next()) {
                userID = mobileResult.getString("raised_by");
                status = "FAILURE: Request already raised by "+userID+" for provided mobile number and pending for approval ";
                return status;
        }
        
        String selectUserIdQuery = "SELECT Contact_ID FROM contact WHERE Primary_Moblie_Number = '" + mobileNumber + "' AND status =1";
        ResultSet userIdResult = statement.executeQuery(selectUserIdQuery);
        if (userIdResult.next()) {
            userID = userIdResult.getString("Contact_ID");
            status = "FAILURE: Account already exists with provided mobile number tagged to  UserID: " + userID;
            return status;
        }

        // Check if email already exists
        String checkEmailQuery = "SELECT * FROM New_user_requests WHERE email_id = '" + emailId + "' and approval_status='pending'";
        ResultSet emailResult = statement.executeQuery(checkEmailQuery);
        if (emailResult.next()) {
                userID = emailResult.getString("raised_by");
                status = "FAILURE: Request already raised by "+userID+" for provided mobile number and pending for approval ";
                return status;
        }
        String selectUserIdQuery1 = "SELECT Contact_ID FROM contact WHERE Primary_Email_ID = '" + emailId + "' and status=1";
        ResultSet userIdResult1 = statement.executeQuery(selectUserIdQuery);
        if (userIdResult1.next()) {
                
        	userID = userIdResult.getString("Contact_ID");
            status = "FAILURE: Account already exists with provided email ID tagged to UserID: " + userID;
            return status;
        }
        
          // Insert new user details into the table
           String query1 = "INSERT INTO New_user_requests (first_name, last_name, email_id, mobile_number, department, tenancy_id, zonal_code, reason, raised_by, raised_at, approval_status, approved_by, approved_at,language,countryCode,loginID,roleID,roleName,timezone) " +
                "VALUES ('" + firstName + "', '" + lastName + "', '" + emailId + "', '" + mobileNumber + "', '" + department + "', " + loginTenancyId + ", '" + zonalCode + "', '" + reason + "', '" + raisedBy + "', NOW(), '" + approvedStatus + "', '" + approvedBy + "', NOW(),'" + language + "','" + countryCode + "','"+userID+ "','"+roleID+ "','"+roleName+"','"+timezone+"')";

            iLogger.info("Query: " + query1);
            statement.executeUpdate(query1);
            
            String sourceDir = null;
			Properties prop = new Properties();
			try {
				prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				sourceDir= prop.getProperty("emailid");
				String emailBody = null;
				String emailSubject = "Approval Status for NewUserRegistration";
				Timestamp createdTimestamp = new Timestamp(new Date().getTime());
				String transactionTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdTimestamp.getTime());
				if (userID.length()>0) {
					 emailBody = "Hi Mridul, Gunjan,\n\n" +
			                  "A new user registration request has been raised with login Id \"" + userID + "\" with below details.\n\n" +
			                  "Details-\n" +
			                  "Name: " + firstName + " " + lastName + "\n" +
			                  "Role: " + roleName + "\n" +
			                  "Reason: " + reason + "\n\n" +
			                  "To approve or reject request, kindly login to portal and go to below path.\n\n" +
			                  "Path: login->settings->users->approval\n\n" +
			                  "Regards,\n" +
			                  "LiveLinkIndia";

					   
					  
				}
				//send confirmation mail
				
				SendEmailWithKafka sendEmail = new SendEmailWithKafka();
				iLogger.info("send Email to: emailID= " + sourceDir);
				String result = sendEmail.sendTenancyMail(sourceDir, emailSubject, emailBody, transactionTime);
				if (result.equalsIgnoreCase("FAILURE")) {
					iLogger.info("sendTenancyMail failed");
				}
				iLogger.info("sendTenancyMail success");
			}
			
			 catch (IOException e) {
		            e.printStackTrace();
		            fLogger.info("Failed to load configuration properties file.");
		            status = "FAILURE-Exception occurred in sending email: " + e.getMessage();
		        }
			
    }
		
		catch (Exception e) {
			fLogger.fatal("FAILURE-Exception occured in inserting Client Details:", e);
			status = "FAILURE-Exception occured in inserting Client Details:" + e.getMessage();
			e.printStackTrace();
		}
		iLogger.info(status);
		return status;
	}

	public List<Map<String, String>> getUserDetailsforTenancy(String loginTenancyList)
	{
		List<Map<String, String>> userStatusList = new ArrayList<>();

	        ConnectMySQL connMySql = new ConnectMySQL();
	        try (Connection prodConnection = connMySql.getConnection();
	             Statement statement = prodConnection.createStatement()) {
	            String query ="SELECT * FROM New_user_requests " +
	                    "WHERE tenancy_id IN (" + loginTenancyList + ") " +
	                    "ORDER BY raised_at desc";
	            if (loginTenancyList==null || loginTenancyList.isEmpty())
	            {
	            	query ="SELECT * FROM New_user_requests " +
		                    "ORDER BY raised_at desc";
	            }
	            ResultSet resultSet = statement.executeQuery(query);

	            while (resultSet.next()) {
	            	Map<String, String> userStatus = new LinkedHashMap<>();
	                userStatus.put("unique_id", resultSet.getString("unique_id"));
	                userStatus.put("firstName", resultSet.getString("First_Name"));
	                userStatus.put("lastName", resultSet.getString("Last_Name"));
	                userStatus.put("emailId", resultSet.getString("Email_Id"));
	                userStatus.put("mobile", resultSet.getString("Mobile_Number"));
	                userStatus.put("reason", resultSet.getString("reason"));
	                
	                userStatus.put("department", resultSet.getString("Department"));
	                userStatus.put("zonal_code", resultSet.getString("zonal_code"));
	                userStatus.put("tenancy_id", resultSet.getString("tenancy_id"));
	                userStatus.put("reason", resultSet.getString("Reason"));
	                userStatus.put("status", resultSet.getString("approval_status"));
	                userStatus.put("roleID", resultSet.getString("roleID"));
	                userStatus.put("roleName", resultSet.getString("roleName"));
	                userStatus.put("timezone", resultSet.getString("timezone"));
	             // Fetch firstname and lastname from database
		            String raisedBy = resultSet.getString("raised_by");

		            // Query to fetch firstname and lastname based on raisedby value
		            String contactQuery = "SELECT c.First_Name, c.Last_Name " +
		                    "FROM wise.contact c " +
		                    "JOIN New_user_requests n ON c.Contact_ID = n.raised_by " +
		                    "WHERE n.raised_by = '" + raisedBy + "'";

		            // Execute contactQuery within a new statement and result set
		            try (ResultSet contactResultSet = statement.executeQuery(contactQuery)) {
		                if (contactResultSet.next()) {
		                    String firstName = contactResultSet.getString("First_Name");
		                    String lastName = contactResultSet.getString("Last_Name");

		                    // Combine firstname and lastname into a single variable
		                    String fullName = "";
		                    if (firstName != null && !firstName.isEmpty()) {
		                        fullName = firstName.trim();  // Trim any extra whitespace
		                        if (lastName != null && !lastName.isEmpty()) {
		                            fullName += " " + lastName.trim();  // Concatenate lastName if not null/empty
		                        }
		                        // If lastName is null or empty, do nothing; fullName remains firstName
		                    }
		                    userStatus.put("fullName", fullName);
		                }
		            } catch (SQLException ex) {
		                ex.printStackTrace();
		            }
	                userStatusList.add(userStatus);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return userStatusList;
	    }


	public boolean updateUserDetails(String id, String status,String approvedBy) {
	    boolean success = false;
	    Logger infoLogger = InfoLoggerClass.logger;
	    Logger fLogger = FatalLoggerClass.logger;
	    ConnectMySQL connMySql = new ConnectMySQL();

	    try (Connection prodConnection = connMySql.getConnection();
	         Statement statement = prodConnection.createStatement()) {
	    	
	    	String updateQuery1 = "UPDATE New_user_requests SET approved_by = '" + approvedBy + "' WHERE unique_id ='" + id + "'";
	        infoLogger.info("Update Query: " + updateQuery1);
	        int rowsAffected1 = statement.executeUpdate(updateQuery1);
	        // Query to update approval_status
	        String updateQuery = "UPDATE New_user_requests SET approval_status = '" + status + "' WHERE unique_id ='" + id + "'";
	        infoLogger.info("Update Query: " + updateQuery);

	        int rowsAffected = statement.executeUpdate(updateQuery);
	        if (rowsAffected > 0) {
	            infoLogger.info("User details updated successfully for ID: " + id);

	            // Perform additional operations if status is 'approved' or 'rejected'
	            status = status.replace("\"", "").trim();
	            if ("approved".equalsIgnoreCase(status)) {
	                // Query to fetch user details
	                String selectQuery = "SELECT * FROM New_user_requests WHERE unique_id ='" + id + "'";
	                ResultSet rs = statement.executeQuery(selectQuery);
	                if (rs.next()) {
	                    // Retrieve values from result set
	                    String firstName = rs.getString("first_name");
	                    String lastName = rs.getString("last_name");
	                    int roleID = rs.getInt("roleID");
	                    String countryCode = rs.getString("countryCode");
	                    String emailId = rs.getString("email_id");
	                    String mobileNumber = rs.getString("mobile_number");
	                    String language = rs.getString("language");
	                    int tenancyId = rs.getInt("tenancy_id");
	                    String roleName = rs.getString("roleName");
	                    String timezone = rs.getString("timezone");

	                    // Prepare data for setUserDetails
	                    UserDetailsRespContract userDetails = new UserDetailsRespContract();
	                    //userDetails.setLoginId(userID);
	                    userDetails.setFirst_name(firstName);
	                    userDetails.setLast_name(lastName);
	                    userDetails.setRole_id(roleID);
	                    userDetails.setRole_name(roleName);
	                    userDetails.setPrimaryMobNumber(mobileNumber);
	                    userDetails.setIs_tenancy_admin(0);
	                    userDetails.setCountryCode(countryCode);
	                    userDetails.setTenancy_id(tenancyId);
	                    userDetails.setAsset_group_id(null);
	                    userDetails.setAsset_group_name(null);
	                    userDetails.setLanguage(language);
	                    userDetails.setTimeZone(timezone);
	                    userDetails.setPrimaryEmailId(emailId);

	                    // Call setUserDetails
	                    UserDetailsImpl userDetailsImpl = new UserDetailsImpl();
	                    String setResult = null;
	                    try {
	                        setResult = userDetailsImpl.setUserDetails(userDetails);
	                        if ("FAILURE".equalsIgnoreCase(setResult)) {
	                            fLogger.info("setUserDetails failed for ID: " + id);
	                            success = false;
	                        } else {
	                            infoLogger.info("setUserDetails invoked successfully for ID: " + id);
	                            success = true;
	                        }
	                    } catch (CustomFault | IOException e) {
	                        // Handle specific exceptions
	                        fLogger.error("Exception while setting user details for ID " + id + ": " + e.getMessage());
	                        success = false; // Set success to false on exception
	                        // Optionally, throw or log the exception for further handling
	                        throw new RuntimeException("Failed to update user details", e); // Throw exception if needed
	                    }

	                } else {
	                    infoLogger.info("No user found for ID: " + id);
	                    success = false;
	                }
	            }else if ("rejected".equalsIgnoreCase(status)) {
	            	  String selectDetailsQuery = "SELECT email_id, first_name, last_name FROM New_user_requests WHERE unique_id ='" + id + "'";
	                ResultSet emailRs = statement.executeQuery(selectDetailsQuery);
	                if (emailRs.next()) {
	                	  String emailId = emailRs.getString("email_id");
	                      String firstName = emailRs.getString("first_name");
	                      String lastName = emailRs.getString("last_name");
	                    String emailSubject = "Approval Status for New User Registration";
	                    String emailBody = "Dear " + firstName + " " + lastName + ",\n\n" +
	                            "Your registration request has been rejected.\n\n" +
	                            "Regards,\n" +
	                            "LiveLinkIndia"; 
	                    // Send email
	                    SendEmailWithKafka sendEmail = new SendEmailWithKafka();
	                    iLogger.info("Sending email to: " + emailId);
	                    String transactionTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
	                    String result = sendEmail.sendTenancyMail(emailId, emailSubject, emailBody, transactionTime);
	                    if ("FAILURE".equalsIgnoreCase(result)) {
	                        fLogger.info("Failed to send rejection email to: " + emailId);
	                    } else {
	                        infoLogger.info("Rejection email sent successfully to: " + emailId);
	                    }
	                } else {
	                    infoLogger.info("No email found for ID: " + id);
	                }
	                
	                success = true;
	            } else {
	                infoLogger.info("Status is not 'approved' or 'rejected': " + id);
	                success = true; // Consider this as success if no additional operations are needed
	            } 
	        } else {
	            infoLogger.info("No rows updated for ID: " + id);
	            success = false;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        fLogger.error("SQL Exception: " + e.getMessage());
	        success = false;
	        // Optionally throw or log the exception for further handling
	        throw new RuntimeException("Database error", e); // Throw exception if needed
	    }

	    return success;
	}


	// Utility method to convert Map<String, Object> to UserDetailsRespContract
	private UserDetailsRespContract mapToUserDetailsRespContract(Map<String, Object> userDetailsMap) {
	    UserDetailsRespContract userDetails = new UserDetailsRespContract();
	    userDetails.setFirst_name((String) userDetailsMap.get("first_name"));
	    userDetails.setLast_name((String) userDetailsMap.get("last_name"));
	    userDetails.setRole_id((Integer) userDetailsMap.get("role_id"));
	    userDetails.setRole_name((String) userDetailsMap.get("role_name"));
	    userDetails.setPrimaryMobNumber((String) userDetailsMap.get("primaryMobNumber"));
	    userDetails.setIs_tenancy_admin((Integer) userDetailsMap.get("is_tenancy_admin"));
	    userDetails.setCountryCode((String) userDetailsMap.get("countryCode"));
	    userDetails.setTenancy_id((Integer) userDetailsMap.get("tenancy_id"));
	    userDetails.setAsset_group_id((List<Integer>) userDetailsMap.get("asset_group_id"));
	    userDetails.setAsset_group_name((List<String>) userDetailsMap.get("asset_group_name"));
	    userDetails.setLanguage((String) userDetailsMap.get("language"));
	    userDetails.setTimeZone((String) userDetailsMap.get("timeZone"));
	    userDetails.setPrimaryEmailId((String) userDetailsMap.get("primaryEmailId"));
	    userDetails.setPrimaryEmailId((String) userDetailsMap.get("timezone"));
	    

	    return userDetails;
	}


	
	


	public static boolean storeOTP(String mobileNumber, String otp) {
	    Logger iLogger = InfoLoggerClass.logger;
	    Logger fLogger = FatalLoggerClass.logger;
	    LoginRegistrationBO obj=new LoginRegistrationBO();
        try {
			obj.sendSMStoMobile(mobileNumber, otp);
		} catch (CustomFault e1) {
			 fLogger.error("Failed to send SMS to mobile number: " + mobileNumber, e1);
			e1.printStackTrace();
		}

	    // Step 1: Check if mobile number exists in 'contact' table
	    String checkQuery = "SELECT COUNT(*) AS count FROM contact WHERE Primary_Moblie_Number = '" + mobileNumber + "'";
	    int count = 0;

	    ConnectMySQL connMySql = new ConnectMySQL();

	    try (Connection prodConnection = connMySql.getConnection();
	         PreparedStatement pstmt = prodConnection.prepareStatement(checkQuery)) {
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            count = rs.getInt("count");
	        }

	        if (count > 0) {
	           

	            // Create the table if it doesn't exist
	            String createTableQuery = "CREATE TABLE IF NOT EXISTS otp_register_for_mobile (" +
	                    "mobileNumber VARCHAR(45) ," +
	                    "otp VARCHAR(10)," +
	                    "generated_at TIMESTAMP," +
	                    "expire_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

	            try (Statement statement = prodConnection.createStatement()) {
	                statement.executeUpdate(createTableQuery);

	                // Insert data into the table
	                String insertQuery = "INSERT INTO otp_register_for_mobile (mobileNumber, otp, generated_at, expire_at) " +
	                        "VALUES ('" + mobileNumber + "', '" + otp + "', CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 HOUR))";
	                PreparedStatement insertStmt = prodConnection.prepareStatement(insertQuery);
	                insertStmt.executeUpdate();
	            } catch (SQLException e) {
	                e.printStackTrace();
	                return false;
	            }
	        } else {
	            iLogger.info("Mobile number does not exist in 'contact' table: " + mobileNumber);
	            return false;
	        }

	    } catch (SQLException e) {
	    	 fLogger.error("SQL error occurred while executing database operations", e);
	        return false;
	    }
	    return true;
	}





	public static boolean storeOTPinDB(String emailID, String otp) {
	    Logger iLogger = InfoLoggerClass.logger;
	    Logger fLogger = FatalLoggerClass.logger;
	    String sourceDir = null;
        Properties prop = new Properties();
        
        LoginRegistrationBO obj=new LoginRegistrationBO();
        obj.sendMailWithOTP(emailID, otp);
        
	    // Step 1: Check if email ID exists in 'contact' table
	    String checkQuery = "SELECT COUNT(*) AS count FROM contact WHERE Primary_Email_ID = '" + emailID + "'";
	    int count = 0;

	    ConnectMySQL connMySql = new ConnectMySQL();

	    try (Connection prodConnection = connMySql.getConnection();
	         PreparedStatement pstmt = prodConnection.prepareStatement(checkQuery)) {

	       
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            count = rs.getInt("count");
	        }

	        if (count > 0) {
	            // Send Email OTP
	           

	            // Create the table if it doesn't exist
	            String createTableQuery = "CREATE TABLE IF NOT EXISTS otp_register_for_emailID (" +
	                    "emailID VARCHAR(45)," +
	                    "otp VARCHAR(10)," +
	                    "generated_at TIMESTAMP," +
	                    "expire_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

	            try (Statement statement = prodConnection.createStatement()) {
	                statement.executeUpdate(createTableQuery);

	                // Insert data into the table using PreparedStatement to avoid SQL injection
	                String insertQuery = "INSERT INTO otp_register_for_emailID (emailID, otp, generated_at, expire_at) " +
	                        "VALUES ('" + emailID + "', '" + otp + "', CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 HOUR))";
	                PreparedStatement insertStmt = prodConnection.prepareStatement(insertQuery);
	                insertStmt.executeUpdate();
	            } catch (SQLException e) {
	                e.printStackTrace();
	                iLogger.error("Exception while storing OTP for emailID " + emailID + ": " + e.getMessage());
	                return false;
	            }
	        } else {
	            iLogger.info("Email ID does not exist in 'contact' table: " + emailID);
	            return false;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        fLogger.error("Exception while checking email ID in 'contact' table: " + e.getMessage());
	        return false;
	    }
	    return true;
	}

	public String validateOTP(String mobileNumber, String otp) {
	    String response = "FAILURE";
	    Logger infoLogger = InfoLoggerClass.logger;
	    Logger fLogger = FatalLoggerClass.logger;

	    try {
	        if (mobileNumber == null || otp == null) {
	            infoLogger.info("Invalid input parameters: mobileNumber or otp is null.");
	            return "FAILURE: Invalid input parameters";
	        }
	        ConnectMySQL connMySql = new ConnectMySQL();

	        // Query to check OTP and mobile number match
	        String checkQuery = "SELECT COUNT(*) AS count FROM otp_register_for_mobile " +
	                            "WHERE mobileNumber = '" + mobileNumber + "' AND otp = '" + otp + "'";
	        infoLogger.info("Executing OTP validation query: " + checkQuery);

	        try (Connection prodConnection = connMySql.getConnection();
	             PreparedStatement pstmt = prodConnection.prepareStatement(checkQuery)) {

	            ResultSet rs = pstmt.executeQuery();

	            // Process query result
	            if (rs.next()) {
	                int count = rs.getInt("count");
	                infoLogger.info("Count of matching OTPs: " + count);

	                if (count > 0) {
	                    infoLogger.info("OTP matched successfully for mobile number: " + mobileNumber);
	                    response = "SUCCESS";
	                } else {
	                    infoLogger.info("OTP mismatch for mobile number: " + mobileNumber);
	                    response = "FAILURE: OTP mismatch";
	                }
	            } else {
	                infoLogger.info("No OTP found for mobile number: " + mobileNumber);
	                response = "FAILURE: OTP not found for the mobile number";
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();
	            fLogger.error("Exception while validating OTP: " + e.getMessage());
	            response = "FAILURE: Internal server error";
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        fLogger.error("Exception caught: " + e.getMessage());
	        response = "FAILURE: " + e.getMessage();
	    }

	    return response;
	}


	public String validateOTPtoEmail(String emailID, String otp) {
		String response = "FAILURE";
		 Logger infoLogger = InfoLoggerClass.logger;
		    Logger fLogger = FatalLoggerClass.logger;
		    iLogger.info(emailID);
		    iLogger.info(otp);
		 try {
		        if (emailID == null || otp == null) {
		            infoLogger.info("Invalid input parameters: emailID or otp is null.");
		            return "FAILURE: Invalid input parameters";
		        }

		        ConnectMySQL connMySql = new ConnectMySQL();

		        // Query to check OTP and mobile number match
		        String checkQuery = "SELECT COUNT(*) AS count FROM otp_register_for_emailID " +
                       "WHERE emailID = '" + emailID + "' AND otp = '" + otp + "'";
		        iLogger.info(checkQuery);
		        try (Connection prodConnection = connMySql.getConnection();
		             PreparedStatement pstmt = prodConnection.prepareStatement(checkQuery)) {
		            ResultSet rs = pstmt.executeQuery();

		            if (rs.next()) {
		                int count = rs.getInt("count");
		                if (count > 0) {
		                    infoLogger.info("OTP matched successfully for emailID: " + emailID);
		                    response = "SUCCESS";
		                } else {
		                    infoLogger.info("OTP mismatch for emailID: " + emailID);
		                    response = "FAILURE: OTP mismatch";
		                }
		            } else {
		                infoLogger.info("No OTP found for emailID: " + emailID);
		                response = "FAILURE: OTP not found for the emailID";
		            }

		        } catch (SQLException e) {
		            e.printStackTrace();
		            fLogger.error("Exception while validating OTP: " + e.getMessage());
		            response = "FAILURE: Internal server error";
		        }

		    } catch (Exception e) {
		        e.printStackTrace();
		        fLogger.error("Exception caught: " + e.getMessage());
		        response = "FAILURE: OTP mismatch " + e.getMessage();
		    }

		    return response;
	}

	}
		
