// CR419 :Santosh : 20230714 :Aemp Changes
package remote.wise.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.ClientDetailsDAO;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.ListToStringConversion;

public class AempClientDao {

	Logger fLogger = FatalLoggerClass.logger;
	Logger iLogger = InfoLoggerClass.logger;

	public String getUserRole(String userId) {
		String role = "NA";
		String query = "SELECT r.role_name from contact c inner join role r on c.role_id=r.role_id where contact_id='"
				+ userId + "' and c.status=1";
		ConnectMySQL connMySql = new ConnectMySQL();
		try (Connection prodConnection = connMySql.getConnection();
				Statement statement = prodConnection.createStatement();
				ResultSet rs = statement.executeQuery(query)) {
			while (rs.next()) {
				role = rs.getString("role_name");
			}
		} catch (Exception e) {
			fLogger.fatal("Exception occured to get role:", e);
		}
		return role;
	}

	public boolean checkIfPresent(String emailId) {

		boolean ifPresent = false;
		String email = null;
		String query = "select Email_Id from aemp_user_details where Active_Status=1";
		ConnectMySQL connMySql = new ConnectMySQL();
		try (Connection prodConnection = connMySql.getConnection();
				Statement statement = prodConnection.createStatement();
				ResultSet rs = statement.executeQuery(query)) {
			while (rs.next()) {
				email = rs.getString("Email_Id");
				if (email.equals(emailId))
					ifPresent = true;
			}
		} catch (Exception e) {
			fLogger.fatal("Exception occured to get email:", e);
		}
		return ifPresent;
	}

	public boolean ifMobileNoPresent(String mobileNo) {

		boolean ifPresent = false;
		String mobile = null;
		String query = "select Mobile_No from aemp_user_details where Active_Status=1";
		ConnectMySQL connMySql = new ConnectMySQL();
		try (Connection prodConnection = connMySql.getConnection();
				Statement statement = prodConnection.createStatement();
				ResultSet rs = statement.executeQuery(query)) {
			while (rs.next()) {
				mobile = rs.getString("Mobile_No");
				if (mobile.equals(mobileNo))
					ifPresent = true;
			}
		} catch (Exception e) {
			fLogger.fatal("Exception occured to get email:", e);
		}
		return ifPresent;
	}

	public String insertClientDetails(String userID, String clientName, String tenancyId, String mobile_No,
			String secretCode, String emailId) {
		String status = "SUCCESS";
		Timestamp createdTimestamp = new Timestamp(new Date().getTime());
		String createdDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdTimestamp.getTime());
		ConnectMySQL connMySql = new ConnectMySQL();
		try (Connection prodConnection = connMySql.getConnection();
				Statement statement = prodConnection.createStatement()) {
			String query = "INSERT INTO aemp_user_details (User_Id,Client_Name,Tenancy_Id,Mobile_No,SecretCode,Email_Id,Created_Date)"
					+ " values('" + userID + "','" + clientName + "','" + tenancyId + "','" + mobile_No + "',"
					+ "AES_ENCRYPT('" + secretCode + "','" + emailId + "'),'" + emailId + "','" + createdDate + "')";
			iLogger.info("Query : " + query);

			System.out.println("userID  : " + userID);
			System.out.println("query  : " + query);
			statement.execute(query);

		} catch (Exception e) {
			fLogger.fatal("FAILURE-Exception occured in inserting Client Details:", e);
			status = "FAILURE-Exception occured in inserting Client Details:" + e.getMessage();
			e.printStackTrace();
		}
		return status;
	}

	public int getClientId(String emailId) {

		int clientId = 0;
		String query = "select Client_Id from aemp_user_details where Email_Id='" + emailId + "'";
		ConnectMySQL connMySql = new ConnectMySQL();
		try (Connection prodConnection = connMySql.getConnection();
				Statement statement = prodConnection.createStatement();
				ResultSet rs = statement.executeQuery(query)) {
			while (rs.next()) {
				clientId = rs.getInt("Client_Id");
			}
		} catch (Exception e) {
			fLogger.fatal("Exception occured to get client:", e);
		}
		return clientId;
	}

	public String getEmailId(int clientId) {

		String emailId = null;
		String query = "select Email_Id from aemp_user_details where Client_Id='" + clientId + "'";
		ConnectMySQL connMySql = new ConnectMySQL();
		try (Connection prodConnection = connMySql.getConnection();
				Statement statement = prodConnection.createStatement();
				ResultSet rs = statement.executeQuery(query)) {
			while (rs.next()) {
				emailId = rs.getString("Email_Id");
			}
		} catch (Exception e) {
			fLogger.fatal("Exception occured to get email:", e);
		}
		return emailId;
	}

	//
	public String getSubscriptionStartDates(int clientId) {

		String startDate = null;
		String query = "select * From aemp_user_subscriptions_history where ClientId ='" + clientId + "'";
		ConnectMySQL connMySql = new ConnectMySQL();
		try (Connection prodConnection = connMySql.getConnection();
				Statement statement = prodConnection.createStatement();
				ResultSet rs = statement.executeQuery(query)) {
			while (rs.next()) {
				startDate = rs.getString("SubscriptionStartDate");
			}
		} catch (Exception e) {
			fLogger.fatal("Exception occured to get email:", e);
		}
		return startDate;
	}

	public String getSubscriptionEndDates(int clientId) {

		String endDate = null;
		String query = "select * From aemp_user_subscriptions_history where ClientId ='" + clientId + "'";
		ConnectMySQL connMySql = new ConnectMySQL();
		try (Connection prodConnection = connMySql.getConnection();
				Statement statement = prodConnection.createStatement();
				ResultSet rs = statement.executeQuery(query)) {
			while (rs.next()) {
				endDate = rs.getString("SubscriptionsEndDate");
			}
		} catch (Exception e) {
			fLogger.fatal("Exception occured to get email:", e);
		}
		return endDate;
	}

	public String getMachineCount(int clientId) {

		String machineCount = null;

		String query = "SELECT " + 
				"    COUNT(aos.serial_number)" + 
				" FROM" + 
				"    asset_owner_snapshot aos" + 
				"        INNER JOIN" + 
				"    (SELECT " + 
				"        account_id" + 
				"    FROM" + 
				"        account" + 
				"    WHERE" + 
				"        mapping_code IN (SELECT " + 
				"                mapping_code" + 
				"            FROM" + 
				"                account" + 
				"            WHERE" + 
				"                account_code IN (SELECT " + 
				"                        t.tenancy_code" + 
				"                    FROM" + 
				"                        aemp_user_details aud, tenancy t" + 
				"                    WHERE" + 
				"                        aud.tenancy_id = t.tenancy_id" + 
				"                            AND aud.client_id = " + clientId + "))) b ON aos.account_id = b.account_id" + 
				"        INNER JOIN" + 
				"    asset a ON aos.serial_number = a.serial_number" + 
				" WHERE" + 
				"    a.status = 1";
		ConnectMySQL connMySql = new ConnectMySQL();
		try (Connection prodConnection = connMySql.getConnection();
				Statement statement = prodConnection.createStatement();
				ResultSet rs = statement.executeQuery(query)) {
			while (rs.next()) {
				machineCount = rs.getString("count(aos.serial_number)");
			}
		} catch (Exception e) {
			fLogger.fatal("Exception occured to get email:", e);
		}
		System.out.println("in dao machineCount " + machineCount);
		return machineCount;
	}


	// View Client data
	public List<ClientDetailsDAO> viewClientData() {
		System.out.println("inside dao------------");
		List<ClientDetailsDAO> list = new ArrayList<ClientDetailsDAO>();

		String query = "select * from aemp_user_details where approved_status=1";
		AempClientDao acd = new AempClientDao();

		ConnectMySQL connMySql = new ConnectMySQL();

//		for (String q : queries) {
			try (Connection prodConnection1 = connMySql.getConnection();
					Statement statement1 = prodConnection1.createStatement();
					Statement statement2 = prodConnection1.createStatement();
					ResultSet rs = statement1.executeQuery(query)){
				while (rs.next()) {
					ClientDetailsDAO clientDetailsDAO = new ClientDetailsDAO();
					AempClientDao aempClientDao = new AempClientDao();

					clientDetailsDAO.setClientId(rs.getInt("Client_Id"));
					clientDetailsDAO.setClientName(rs.getString("Client_Name"));
					clientDetailsDAO.setEmail(rs.getString("Email_Id"));
					clientDetailsDAO.setMobile_No(rs.getString("Mobile_No"));
					clientDetailsDAO.setStatus(rs.getInt("Active_Status"));
					clientDetailsDAO.setAggrementDate(rs.getString("Created_Date").split("\\.")[0]);
					clientDetailsDAO.setSubScriptionStartDate(rs.getString("SubscriptionStartDate").split("\\.")[0]);
					clientDetailsDAO.setSubScriptionEndDate(rs.getString("SubscriptionEndDate").split("\\.")[0]);

					int clientId = getClientId(rs.getString("Email_Id"));

					String machineCount = aempClientDao.getMachineCount(clientId);
					clientDetailsDAO.setMachineCount(machineCount);

					
					
					list.add(clientDetailsDAO);
				}
			}catch (Exception e) {
				fLogger.fatal("Exception occured to get email:", e);
				e.printStackTrace();
			}
		
		return list;
	}

	//view data for SA Invocation history
	// View Client data
		public List<ClientDetailsDAO> viewDataSAInvocation() {
			System.out.println("inside dao------------");
			List<ClientDetailsDAO> list = new ArrayList<ClientDetailsDAO>();

			
			String query = "select * from aemp_api_invokation_history";
			System.out.println("query for sa ,......" + query);
	

			ConnectMySQL connMySql = new ConnectMySQL();
				try (Connection prodConnection1 = connMySql.getConnection();
						Statement statement1 = prodConnection1.createStatement();
						ResultSet rs = statement1.executeQuery(query)){
					while (rs.next()) {
						ClientDetailsDAO clientDetailsDAO = new ClientDetailsDAO();
						if (rs.getInt("Client_Id")==0) {
							clientDetailsDAO.setClientId(0);
						}else {
						clientDetailsDAO.setClientId(rs.getInt("Client_Id"));
						}
						if (rs.getString("TS_Auth_Latest_Timestamp") == null) {
							clientDetailsDAO.setLastAccessedTSForTSAuth("NA");
						} else {
							clientDetailsDAO.setLastAccessedTSForTSAuth(rs.getString("TS_Auth_Latest_Timestamp").split("\\.")[0]);
						}
						if (rs.getString("SS_Auth_Latest_Timestamp") == null) {
							clientDetailsDAO.setLastAccessedTSForSSAuth("NA");
						} else {
							clientDetailsDAO.setLastAccessedTSForSSAuth(rs.getString("SS_Auth_Latest_Timestamp").split("\\.")[0]);
						}

						if (rs.getString("TS_Single_Fleet_Latest_Timestamp") == null) {
							clientDetailsDAO.setLastAccessedTSForTS("NA");
						} else {
							clientDetailsDAO.setLastAccessedTSForTS(rs.getString("TS_Single_Fleet_Latest_Timestamp").split("\\.")[0]);
						}
						if (rs.getString("SS_Fleet_Latest_Timestamp") == null) {
							clientDetailsDAO.setLastAccessedTSForSSFleet("NA");
						} else {
							clientDetailsDAO.setLastAccessedTSForSSFleet(rs.getString("SS_Fleet_Latest_Timestamp").split("\\.")[0]);
						}
						if (rs.getString("SS_Single_Fleet_Latest_Timestamp") == null) {
							clientDetailsDAO.setLastAccessedTSForSSFleetSingle("NA");
						} else {
							clientDetailsDAO
									.setLastAccessedTSForSSFleetSingle(rs.getString("SS_Single_Fleet_Latest_Timestamp").split("\\.")[0]);
						}
						//System.out.println("hi----------------------------------------"+rs.getInt("Client_Id"));
					
						
						
						list.add(clientDetailsDAO);
					}
				}catch (Exception e) {
					fLogger.fatal("Exception occured to get email:", e);
					e.printStackTrace();
				}
			
			return list;
		}
	
	// Enable update Active status to 1
	public boolean updateEnableActiveStatus(int clientId, String secretCode) {
		boolean result = true;
		ConnectMySQL connMySql = new ConnectMySQL();
		String query = "update aemp_user_details set Active_Status = 1,SecretCode=" + "AES_ENCRYPT('" + secretCode
				+ "','" + getEmailId(clientId) + "')" + " where Client_Id=" + clientId;
		try (Connection prodConnection1 = connMySql.getConnection();
				Statement statement1 = prodConnection1.createStatement()) {

			statement1.executeUpdate(query);
		} catch (Exception e) {
			fLogger.fatal("Exception occured to update:", e);
			result = false;
		}
		return result;
	}

	// Disable update Active status to 0
	public boolean updateDisableActiveStatus(int clientId) {
		boolean result = true;
		ConnectMySQL connMySql = new ConnectMySQL();
		String query = "update aemp_user_details set Active_Status = 0 where Client_Id=" + clientId;
		iLogger.info(query);
		try (Connection prodConnection1 = connMySql.getConnection();
				Statement statement1 = prodConnection1.createStatement()) {
			statement1.executeUpdate(query);
		} catch (Exception e) {
			fLogger.fatal("Exception occured to update:", e);
			result = false;
		}
		return result;
	}

	public boolean checkIfTenancyIdPresent(String tenancyId) {
		boolean ifPresent = false;
		String tenancyIdLocal = null;
		String query = "select Tenancy_Id from aemp_user_details where Active_Status=1";
		ConnectMySQL connMySql = new ConnectMySQL();
		try (Connection prodConnection = connMySql.getConnection();
				Statement statement = prodConnection.createStatement();
				ResultSet rs = statement.executeQuery(query)) {
			while (rs.next()) {
				tenancyIdLocal = rs.getString("Tenancy_Id");
				if (tenancyId.equals(tenancyIdLocal))
					ifPresent = true;
			}
		} catch (Exception e) {
			fLogger.fatal("Exception occured to get Tenancy Id:", e);
		}
		return ifPresent;
	}

	public String getClientName(int clientId) {
		String clientName = null;
		String query = "select Client_Name from aemp_user_details where Client_Id='" + clientId + "'";
		ConnectMySQL connMySql = new ConnectMySQL();
		try (Connection prodConnection = connMySql.getConnection();
				Statement statement = prodConnection.createStatement();
				ResultSet rs = statement.executeQuery(query)) {
			while (rs.next()) {
				clientName = rs.getString("Client_Name");
			}
		} catch (Exception e) {
			fLogger.fatal("Exception occured to get client:", e);
		}
		return clientName;
	}

	public String toCheckWatingForApproval(String tenancyId) {
		System.out.println("checking result is in DB empty......for pending account");
		// String result = null;
		String tenancyName = null;
		String query = "select aud.Client_Id , t.Tenancy_Name, t.mapping_code from aemp_user_details aud inner join tenancy t on aud.tenancy_id=t.tenancy_id where approved_status is NULL and t.mapping_code in (select mapping_code from tenancy where tenancy_id= "
				+ tenancyId + "" + ");";
		// String query = "select * from aemp_user_details";
		ConnectMySQL connMySql = new ConnectMySQL();
		System.out.println("query before..." + query);
		try (Connection prodConnection = connMySql.getConnection();
				Statement statement = prodConnection.createStatement();
				ResultSet rs = statement.executeQuery(query)) {
			System.out.println("query ..." + query);
			while (rs.next()) {
				tenancyName = rs.getString("Tenancy_Name");
				System.out.println("result ...." + tenancyName);
			}
			System.out.println("outside while");
			// result = "An account is already created and pending for approval for your
			// organisation " + tenancyName;
		} catch (Exception e) {
			fLogger.fatal("Exception occured to get email:", e);
			System.out.println("exception");
			e.printStackTrace();
		}
		return tenancyName;
	}

	public String toCheckAlreadyCreated(String tenancyId) {
		System.out.println("checking result is in DB empty......for account creat");
		// String result = null;
		String tenancyName = null;
		String query = "select aud.Client_Id , t.Tenancy_Name, t.mapping_code from aemp_user_details aud inner join tenancy t on aud.tenancy_id=t.tenancy_id where approved_status is not NULL and approved_status!=0 and t.mapping_code in (select mapping_code from tenancy where Tenancy_Id= "
				+ tenancyId + "" + ");";
		ConnectMySQL connMySql = new ConnectMySQL();
		try (Connection prodConnection = connMySql.getConnection();
				Statement statement = prodConnection.createStatement();
				ResultSet rs = statement.executeQuery(query)) {
			while (rs.next()) {
				tenancyName = rs.getString("Tenancy_Name");
			}
			// result = "An account is already created for your organisation " +
			// tenancyName;
		} catch (Exception e) {
			fLogger.fatal("Exception occured to get email:", e);
		}
		return tenancyName;
	}

	public List<ClientDetailsDAO> viewClientByTenancyId(List<Integer> tenancyIdList, String loginId) {

		List<ClientDetailsDAO> list = new ArrayList<ClientDetailsDAO>();

		String tenancyIds = new ListToStringConversion().getIntegerListString(tenancyIdList).toString();

		String query = "select * from aemp_user_details a left outer join aemp_api_invokation_history b on a.client_id=b.client_id where a.Tenancy_Id in (" + tenancyIds + ") and a.user_id = '"
				+ loginId + "' and a.active_status=1 and a.approved_status=1";

//		String query1="select * from aemp_user_details a left outer join aemp_api_invokation_history b on a.client_id=b.client_id where a.approved_status=1";
		System.out.println("query print "+query);
		ConnectMySQL connMySql = new ConnectMySQL();
		try (Connection prodConnection = connMySql.getConnection();
				Statement statement = prodConnection.createStatement();
				ResultSet rs = statement.executeQuery(query)) {
			while (rs.next()) {
				int tenancyIdLocal = Integer.parseInt(rs.getString("Tenancy_Id"));
				if (tenancyIdList.contains(tenancyIdLocal)) {
					ClientDetailsDAO clientDetailsDAO = new ClientDetailsDAO();
					AempClientDao aempClientDao = new AempClientDao();
					clientDetailsDAO.setClientId(rs.getInt("Client_Id"));
					clientDetailsDAO.setClientName(rs.getString("Client_Name"));
					clientDetailsDAO.setEmail(rs.getString("Email_Id"));
					clientDetailsDAO.setMobile_No(rs.getString("Mobile_No"));
					clientDetailsDAO.setStatus(rs.getInt("Active_Status"));
					clientDetailsDAO.setSubScriptionStartDate(rs.getString("SubscriptionStartDate").split("\\.")[0]);
					clientDetailsDAO.setSubScriptionEndDate(rs.getString("SubscriptionEndDate").split("\\.")[0]);
					int clientId = getClientId(rs.getString("Email_Id"));
					String machineCount = aempClientDao.getMachineCount(clientId);
					clientDetailsDAO.setMachineCount(machineCount);

					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					String ssAuthTS = "NA";
					String tsAuthTS = "NA";
					LocalDateTime ssAuthDateTime = null;
					LocalDateTime tsAuthDateTime = null;
					if (rs.getString("SS_TokenCreatedDate") != null) {
						ssAuthTS = rs.getString("SS_TokenCreatedDate").split("\\.")[0];
						ssAuthDateTime = LocalDateTime.parse(ssAuthTS, dtf);
					}
					if (rs.getString("TS_TokenCreatedDate") != null) {
						tsAuthTS = rs.getString("TS_TokenCreatedDate").split("\\.")[0];
						tsAuthDateTime = LocalDateTime.parse(tsAuthTS, dtf);
					}

					if (ssAuthDateTime != null && tsAuthDateTime != null) {
						if (tsAuthDateTime.isAfter(ssAuthDateTime))
							clientDetailsDAO.setLastAuthenticationTS(tsAuthTS);
						else
							clientDetailsDAO.setLastAuthenticationTS(ssAuthTS);
					}
					if (ssAuthDateTime == null) {
						clientDetailsDAO.setLastAuthenticationTS(tsAuthTS);
					}
					if (tsAuthDateTime == null) {
						clientDetailsDAO.setLastAuthenticationTS(ssAuthTS);
					}
					
					if (rs.getString("TS_Auth_Latest_Timestamp") == null) {
						clientDetailsDAO.setLastAccessedTSForTSAuth("NA");
					} else {
						clientDetailsDAO.setLastAccessedTSForTSAuth(rs.getString("TS_Auth_Latest_Timestamp").split("\\.")[0]);
					}
					if (rs.getString("SS_Auth_Latest_Timestamp") == null) {
						clientDetailsDAO.setLastAccessedTSForSSAuth("NA");
					} else {
						clientDetailsDAO.setLastAccessedTSForSSAuth(rs.getString("SS_Auth_Latest_Timestamp").split("\\.")[0]);
					}

					if (rs.getString("TS_Single_Fleet_Latest_Timestamp") == null) {
						clientDetailsDAO.setLastAccessedTSForTS("NA");
					} else {
						clientDetailsDAO.setLastAccessedTSForTS(rs.getString("TS_Single_Fleet_Latest_Timestamp").split("\\.")[0]);
					}
					if (rs.getString("SS_Fleet_Latest_Timestamp") == null) {
						clientDetailsDAO.setLastAccessedTSForSSFleet("NA");
					} else {
						clientDetailsDAO.setLastAccessedTSForSSFleet(rs.getString("SS_Fleet_Latest_Timestamp").split("\\.")[0]);
					}
					if (rs.getString("SS_Single_Fleet_Latest_Timestamp") == null) {
						clientDetailsDAO.setLastAccessedTSForSSFleetSingle("NA");
					} else {
						clientDetailsDAO
								.setLastAccessedTSForSSFleetSingle(rs.getString("SS_Single_Fleet_Latest_Timestamp").split("\\.")[0]);
					}

					list.add(clientDetailsDAO);
				}
			}
		} catch (Exception e) {
			fLogger.fatal("Exception occured:", e);
		}
		return list;
	}

	public List<ClientDetailsDAO> viewApprovedClient() {
		List<ClientDetailsDAO> list = new ArrayList<ClientDetailsDAO>();

		String query = "select * from aemp_user_details where Approved_Status is NULL";
		ConnectMySQL connMySql = new ConnectMySQL();
		try (Connection prodConnection = connMySql.getConnection();
				Statement statement = prodConnection.createStatement();
				ResultSet rs = statement.executeQuery(query)) {
			while (rs.next()) {

				ClientDetailsDAO clientDetailsDAO = new ClientDetailsDAO();

				clientDetailsDAO.setClientId(rs.getInt("Client_Id"));
				clientDetailsDAO.setClientName(rs.getString("Client_Name"));
				clientDetailsDAO.setEmail(rs.getString("Email_Id"));
				clientDetailsDAO.setMobile_No(rs.getString("Mobile_No"));
				clientDetailsDAO.setUserId(rs.getString("User_Id"));

				list.add(clientDetailsDAO);

			}
		} catch (Exception e) {
			fLogger.fatal("Exception occured :", e);
		}
		return list;
	}

	// update Approve status to 1
	public boolean updateApproveStatus(String approvedBy, int clientId) {
		boolean result = true;
		Timestamp createdTimestamp = new Timestamp(new Date().getTime());
		String subscriptionStartDate = new SimpleDateFormat("yyyy-MM-dd").format(createdTimestamp.getTime())
				+ " 00:00:00";
		int noOfYrs = 1;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.YEAR, noOfYrs);
		Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
		String subscriptionsEndDate = new SimpleDateFormat("yyyy-MM-dd").format(timestamp.getTime()) + " 00:00:00";

		String approvedOn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdTimestamp.getTime());
		ConnectMySQL connMySql = new ConnectMySQL();
		String query = "INSERT INTO aemp_user_subscriptions_history (ClientId,SubscriptionStartDate,SubscriptionsEndDate,NoOfYrs,ApprovedBy,ApprovedOn)"
				+ " values('" + clientId + "','" + subscriptionStartDate + "','" + subscriptionsEndDate + "','"
				+ noOfYrs + "','" + approvedBy + "','" + approvedOn + "')";

		System.out.println("query    :" + query);
		String query1 = "update aemp_user_details set Active_Status=1, Approved_Status=1,SubscriptionStartDate='"
				+ subscriptionStartDate + "',SubscriptionEndDate='" + subscriptionsEndDate + "' where Client_Id= "
				+ clientId;
		System.out.println("query1   :" + query1);
		iLogger.info(query);
		try (Connection prodConnection1 = connMySql.getConnection();
				Statement statement1 = prodConnection1.createStatement();
				Statement statement2 = prodConnection1.createStatement()) {
			int insertCheck = statement1.executeUpdate(query);
			if (insertCheck > 0) {
				iLogger.info("Insert status  :: Sucuss :" + insertCheck);
			}
			int updateCheck = statement2.executeUpdate(query1);
			if (updateCheck > 0) {
				iLogger.info("updateCheck status  :: Sucuss :" + updateCheck);
			}

		} catch (Exception e) {
			fLogger.fatal("Exception occured to update:", e);
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public boolean updateDisApproveStatus(int clientId) {
		boolean result = true;
		ConnectMySQL connMySql = new ConnectMySQL();
		String query = "update aemp_user_details set Approved_Status = 0,Active_Status=0 where Client_Id=" + clientId;
		iLogger.info(query);
		try (Connection prodConnection1 = connMySql.getConnection();
				Statement statement1 = prodConnection1.createStatement()) {
			statement1.executeUpdate(query);
		} catch (Exception e) {
			fLogger.fatal("Exception occured to update:", e);
			e.printStackTrace();
			result = false;
		}
		return result;
	}

}
