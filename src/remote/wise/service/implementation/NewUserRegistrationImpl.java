package remote.wise.service.implementation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.apache.logging.log4j.Logger;
import remote.wise.dao.NewUserRegistrationDao;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.UserAuthenticationRespContract;
import remote.wise.service.datacontract.UserDetailsRespContract;
import remote.wise.util.ResponseObject;

public class NewUserRegistrationImpl {

	public String createUser(HashMap<String, String> userDetails) {
		Logger iLogger = InfoLoggerClass.logger;
		String status=" ";
		String firstName=userDetails.get("firstName");
		String lastName=userDetails.get("lastName");
		String emailId=userDetails.get("emailId");
		String mobileNumber=userDetails.get("mobileNumber");
		String zonalCode=userDetails.get("zonalCode");
		String department=userDetails.get("department");
		String loginTenancyId=userDetails.get("loginTenancyId");
		String reason=userDetails.get("reason");
		String raisedBy=userDetails.get("raisedBy");
		String approvedStatus=userDetails.get("approvedStatus");
		String approvedBy=userDetails.get("approvedBy");
		String language=userDetails.get("language");
		String countryCode=userDetails.get("countryCode");
		String userID=userDetails.get("userID");
		String roleID=userDetails.get("roleID");
		String roleName=userDetails.get("roleName");
		String timezone=userDetails.get("timezone");
		 if (userDetails != null) {
		        // Check if mobileNumber is valid
		        if (mobileNumber != null && mobileNumber.matches("\\d{10}")) {
		            if ("JCB RO".equals(roleName) || "JCB HO".equals(roleName)) {
		                if (emailId != null && emailId.endsWith("@jcb.com")) {
		                    String insertStatus = new NewUserRegistrationDao().insertuserDetails(firstName, lastName, emailId, mobileNumber, zonalCode, department, loginTenancyId, reason, raisedBy, approvedStatus, approvedBy, language, countryCode, userID, roleID, roleName, timezone);
		                    if (insertStatus.startsWith("FAILURE:")) {
		                        return insertStatus;
		                    }
		                    return "SUCCESS : Request raised successfully";
		                } else {
		                    return "FAILURE: Invalid email domain. For 'JCB RO' or 'JCB HO' roles, email must end with '@jcb.com'.";
		                }
		            } else if ("Dealer".equals(roleName)) {
		            	String insertStatus = new NewUserRegistrationDao().insertuserDetails(firstName, lastName, emailId, mobileNumber, zonalCode, department, loginTenancyId, reason, raisedBy, approvedStatus, approvedBy, language, countryCode, userID, roleID, roleName, timezone);
	                    if (insertStatus.startsWith("FAILURE:")) {
	                        return insertStatus;
	                    }
	                    return "SUCCESS : Request raised successfully";
		            } else {
		                return "FAILURE: Invalid role name. Only 'JCB RO', 'JCB HO', or 'Dealer' roles are allowed.";
		            }
		        } else {
		            return "FAILURE: Invalid mobile number format. Mobile number must be 10 digits.";
		        }
		    }

		    return status;
	}
	

	public String generateOTPMobile(String mobileNumber) {
	    // Validate mobile number
	    if (!isValidMobileNumber(mobileNumber)) {
	        InfoLoggerClass.logger.info("Invalid mobile number provided: " + mobileNumber);
	        return "FAILURE: Invalid mobile number";
	    }

	    // Generate OTP
	    String otp = generateRandomOTP();
	   
	    // Store OTP in the database
	    try {
	        boolean otpStored = NewUserRegistrationDao.storeOTP(mobileNumber, otp);
	        if (!otpStored) {
	            InfoLoggerClass.logger.error("Failed to store OTP for mobile number: " + mobileNumber);
	            return "FAILURE: Failed to store OTP";
	        }
	    } catch (Exception e) {
	        // Log exception details
	        InfoLoggerClass.logger.error("Exception while storing OTP for mobile number " + mobileNumber + ": " + e.getMessage());
	        return "FAILURE: Internal server error";
	    }

	    InfoLoggerClass.logger.info("OTP generated and stored successfully for mobile number: " + mobileNumber);
	    return "SUCCESS";
	}

	private boolean isValidMobileNumber(String mobileNumber) {
	    boolean isValid = mobileNumber != null && mobileNumber.matches("^\\d{10}$"); // Example validation: 10 digits
	    InfoLoggerClass.logger.info("Mobile number validation result: " + isValid);
	    return isValid;
	}

	private String generateRandomOTP() {
	    // Generate a random 6-digit OTP
	    return String.format("%06d", new Random().nextInt(1000000));
	}
	
	public String generateOTPemailID(String emailID) {
	    // Validate mobile number
	    if (!isValidemailID(emailID)) {
	        InfoLoggerClass.logger.info("Invalid emailID provided: " + emailID);
	        return "FAILURE: Invalid emailID";
	    }

	    // Generate OTP
	    String otp = generateRandomOTP1();
	   
	    // Store OTP in the database
	    try {
	        boolean otpStored = NewUserRegistrationDao.storeOTPinDB(emailID, otp);
	        if (!otpStored) {
	            InfoLoggerClass.logger.error("Failed to store OTP for emailID: " + emailID);
	            return "FAILURE: Failed to store OTP";
	        }
	    } catch (Exception e) {
	        // Log exception details
	        InfoLoggerClass.logger.error("Exception while storing OTP for emailID " + emailID + ": " + e.getMessage());
	        return "FAILURE: Internal server error";
	    }

	    InfoLoggerClass.logger.info("OTP generated and stored successfully for emailID: " + emailID);
	    return "SUCCESS";
	}

	private boolean isValidemailID(String emailID) {
	    boolean isValid = emailID != null && emailID.endsWith("@wipro.com");
	    InfoLoggerClass.logger.info("EmailID validation result: " + isValid);
	    return isValid;
	}

	private String generateRandomOTP1() {
	    // Generate a random 6-digit OTP
	    return String.format("%06d", new Random().nextInt(1000000));
	}
    
}
