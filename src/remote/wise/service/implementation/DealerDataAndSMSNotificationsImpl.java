package remote.wise.service.implementation;
/*
 * CR498 : Sai Divya : 20250403 : APIs for Dealer master data & SMS Notifications 
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class DealerDataAndSMSNotificationsImpl {

	public String createNotificationDealer(HashMap<String, String> dealerDetails, String pflag) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		String status = "SUCCESS";
		ConnectMySQL connMySql = new ConnectMySQL();
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String dealerCode = dealerDetails.get("dealerCode");
		String timeZone = dealerDetails.get("timeZone");
		String dealerPrincipalName = dealerDetails.get("dealerPrincipalName");
		String address = dealerDetails.get("address");
		String mobileNumber = dealerDetails.get("mobileNumber");
		String email = dealerDetails.get("email");
		String city = dealerDetails.get("city");
		String zipCode = dealerDetails.get("zipCode");
		String countryName = dealerDetails.get("countryName");
		String dealerAccountName = dealerDetails.get("dealerAccountName");
		String state = dealerDetails.get("state");
		iLogger.info(dealerCode);
		String query = null;
		String query1 = null;
		String llCode = getDealerCodeFromLLCode(dealerCode);
		iLogger.info(dealerPrincipalName);
		if (llCode == null) {
			iLogger.info("FAILURE: llCode not found for DealerCode: " + dealerCode);
			return "FAILURE: llCode not found for DealerCode: " + dealerCode;
		}
		int dealerCount = getDealerCount(dealerCode);
		if (dealerCount >= 27) {
			return "FAILURE: Cannot add more than 27 dealers for DealerCode: " + dealerCode;
		} else {
			try {
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				if (pflag.equals("1")) {
					// Check if DealerPrincipalName is unique for the given DealerCode in
					// Principal_Dealers table
					String checkQueryPrincipal = "SELECT COUNT(*) FROM Principal_Dealers WHERE DealerCode = '" + dealerCode + "'";
					iLogger.info(checkQueryPrincipal);
					rs = statement.executeQuery(checkQueryPrincipal);
					if (rs.next() && rs.getInt(1) > 0) {
						return "FAILURE: Dealer Principal is already created.";
					}
					query = "INSERT INTO Principal_Dealers (DealerCode, DealerPricipalName, PrincipleDealerAddress, PrincipleDealerMobileNumber, PrincipleDealerEmail, City,timezone, State, ZipCode, CountryName, DealerAccountName,Status) "
							+ "VALUES ('" + dealerCode + "', '" + dealerPrincipalName + "', '" + address + "', '"
							+ mobileNumber + "', '" + email + "', '" + city + "','" + timeZone + "', '" + state + "', '"
							+ zipCode + "',  '" + countryName + "', '" + dealerAccountName + "',1)";
					statement.executeUpdate(query);
				} else {
					// Check if DealerNotificationName is unique for the given DealerCode
					String checkQuery = "SELECT COUNT(*) FROM Notification_Dealers WHERE DealerCode = '" + dealerCode
							+ "' AND DealerNotificationName = '" + dealerPrincipalName + "'";
					iLogger.info(checkQuery);
					rs = statement.executeQuery(checkQuery);
					if (rs.next() && rs.getInt(1) > 0) {
						return "FAILURE: DealerNotificationName must be unique for DealerCode: " + dealerCode;
					}
					query1 = "INSERT INTO Notification_Dealers (DealerCode, DealerNotificationName, DealerAddress, DealerMobileNumber, DealerEmail,timezone, City, State, ZipCode, CountryName, DealerAccountName,Status) "
							+ "VALUES ('" + dealerCode + "', '" + dealerPrincipalName + "', '" + address + "', '"
							+ mobileNumber + "', '" + email + "','" + timeZone + "', '" + city + "', '" + state + "', '"
							+ zipCode + "',  '" + countryName + "', '" + dealerAccountName + "',1)";
					statement.executeUpdate(query1);
				}
				iLogger.info("Query1: " + query1);
				iLogger.info("Query :" + query);

			}

			catch (Exception e) {
				fLogger.fatal("FAILURE-Exception occured in inserting Client Details:", e);
				status = "FAILURE-Exception occured in inserting Client Details:" + e.getMessage();
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (statement != null) {
						statement.close();
					}
					if (prodConnection != null) {
					}
				} catch (Exception e) {
					fLogger.error("Exception occurred while closing resources: " + e.getMessage());
				}
			}
		}

		return status;
	}

	private String getDealerCodeFromLLCode(String DealerCode) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		// String dealerCode = null;
		String query = "SELECT LL_Code FROM account_mapping WHERE LL_Code = '" + DealerCode + "'";

		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs1 = null;
		String dealerCode = null;
		try {
			connection = connFactory.getConnection();
			if (connection == null) {
				iLogger.info(" mysql connection" + connection);
				return "Fail Update the Records due to sql connection issues.!";
			}

			connection = connFactory.getConnection();
			stmt = connection.createStatement();
			rs1 = stmt.executeQuery(query);
			iLogger.info("Executing Query:" + query);
			while (rs1.next()) {
				dealerCode = rs1.getString("LL_Code");
			}
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Exception e " + e.getMessage());
			return e.getMessage();
		} finally {
			try {
				if (rs1 != null) {
					rs1.close();
				}
			} catch (Exception e) {
				fLogger.error("Exception occurred while closing resources: " + e.getMessage());
			}
		}
		return dealerCode;
	}

	private int getDealerCount(String dealerCode) {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String query = "SELECT COUNT(*) AS dealerCount FROM Notification_Dealers WHERE DealerCode = '" + dealerCode
				+ "'";
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		ResultSet rs = null;
		Statement stmt = null;
		int count = 0;
		try {
			connection = connFactory.getConnection();
			if (connection == null) {
				iLogger.info(" mysql connection" + connection);
				return 0;
			}

			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			iLogger.info("Executing Query:" + query);
			if (rs.next()) {
				count = rs.getInt("dealerCount");
			}
		} catch (Exception e) {
			e.printStackTrace();
			iLogger.fatal("Exception e " + e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				fLogger.error("Exception occurred while closing resources: " + e.getMessage());
			}
		}
		return count;
	}

	@SuppressWarnings({ "unused" })
	public String createSubscriberDetails(String dealerCode, Map<String, Object> subscriberDetails) {
		String response = "FAILURE";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		ConnectMySQL connMySql = new ConnectMySQL();

		try (Connection prodConnection = connMySql.getConnection()) {
			// Get the dealer type from the input
			String dealerType = (String) subscriberDetails.get("dealerType");
			int dealerTypeId = getDealerTypeId(prodConnection, dealerType);

			// Insert or update details for each communication mode
			insertOrUpdateDealerDetails(prodConnection, dealerCode, "SMS", subscriberDetails, dealerTypeId);
			insertOrUpdateDealerDetails(prodConnection, dealerCode, "Email", subscriberDetails, dealerTypeId);

			response = "SUCCESS";
		} catch (SQLException e) {
			// Log the exception
			fLogger.error("Error inserting dealer details: " + e.getMessage(), e);
			response = "FAILURE: " + e.getMessage();
		}

		return response;
	}

	private void insertOrUpdateDealerDetails(Connection connection, String dealerCode, String commMode,
			Map<String, Object> dealerDetails, int dealerTypeId) throws SQLException {
		Logger iLogger = InfoLoggerClass.logger;
		// Check if Subscriber1, Subscriber2, and Subscriber3 are unique
		String subscriber1 = (String) dealerDetails.get(commMode.equals("SMS") ? "s1_sms" : "s1_email");
		String subscriber2 = (String) dealerDetails.get(commMode.equals("SMS") ? "s2_sms" : "s2_email");
		String subscriber3 = (String) dealerDetails.get(commMode.equals("SMS") ? "s3_sms" : "s3_email");

		// Check if subscriber1 is "NONE" and throw an exception if it is
		if ("NONE".equals(subscriber1)) {
			throw new SQLException("Subscriber1 values should not be NONE for " + commMode + ".");
		}

		// Set subscribers to null if they are "NONE"
		subscriber1 = "NONE".equals(subscriber1) ? null : subscriber1;
		subscriber2 = "NONE".equals(subscriber2) ? null : subscriber2;
		subscriber3 = "NONE".equals(subscriber3) ? null : subscriber3;

		if (subscriber1 != null && (subscriber1.equals(subscriber2) || subscriber1.equals(subscriber3))) {
			throw new SQLException("Subscriber1 must be unique for " + commMode + ".");
		} else if (subscriber2 != null && (subscriber2.equals(subscriber1) || subscriber2.equals(subscriber3))) {
			throw new SQLException("Subscriber2 must be unique for " + commMode + ".");
		} else if (subscriber3 != null && (subscriber3.equals(subscriber1) || subscriber3.equals(subscriber2))) {
			throw new SQLException("Subscriber3 must be unique for " + commMode + ".");
		}

		// Check if the record already exists
		String checkQuery = "SELECT COUNT(*) FROM DealerSubscriberDetails WHERE DealerCode = ? AND DealerType = ? AND CommMode = ?";
		try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
			checkStmt.setString(1, dealerCode);
			checkStmt.setInt(2, dealerTypeId);
			checkStmt.setString(3, commMode);
			try (ResultSet rs = checkStmt.executeQuery()) {
				if (rs.next() && rs.getInt(1) > 0) {
					// Update the existing record
					String updateQuery = "UPDATE DealerSubscriberDetails SET Subscriber1 = ?, Subscriber2 = ?, Subscriber3 = ? WHERE DealerCode = ? AND DealerType = ? AND CommMode = ?";
					iLogger.info("updateQuery :" + updateQuery);
					try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
						updateStmt.setString(1, subscriber1);
						updateStmt.setString(2, subscriber2);
						updateStmt.setString(3, subscriber3);
						updateStmt.setString(4, dealerCode);
						updateStmt.setInt(5, dealerTypeId);
						updateStmt.setString(6, commMode);
						updateStmt.executeUpdate();
					}
				} else {
					// Insert a new record
					String insertQuery = "INSERT INTO DealerSubscriberDetails (DealerCode, DealerType, CommMode, Subscriber1, Subscriber2, Subscriber3) VALUES (?, ?, ?, ?, ?, ?)";
					iLogger.info("insertQuery :" + insertQuery);
					try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
						insertStmt.setString(1, dealerCode);
						insertStmt.setInt(2, dealerTypeId);
						insertStmt.setString(3, commMode);
						insertStmt.setString(4, subscriber1);
						insertStmt.setString(5, subscriber2);
						insertStmt.setString(6, subscriber3);
						insertStmt.executeUpdate();
					}
				}
			}
		}
	}

	private int getDealerTypeId(Connection connection, String dealerType) throws SQLException {
		Logger iLogger = InfoLoggerClass.logger;
		String query = "SELECT dealer_type_id FROM notification_dealer_types WHERE dealer_type_name = ?";
		iLogger.info("query :" + query);
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, dealerType);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("dealer_type_id");
				} else {
					throw new SQLException("DealerType not found: " + dealerType);
				}
			}
		}
	}

	public Map<String, Map<String, Map<String, String>>> getNotifications(String dealerCode) {
		Map<String, Map<String, Map<String, String>>> notifications = new HashMap<>();
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		ResultSet resultSet = null;
		Statement statement = null;
		try {
			connection = connFactory.getConnection();
			if (connection == null) {
				iLogger.info(" mysql connection" + connection);
			}

			statement = connection.createStatement();

			// Execute the query to get dealer details
			String query = "SELECT dsd.DealerType,nd.DealerNotificationName,nd.NotificationDealerID,dsd.CommMode,nd.DealerEmail, dsd.Subscriber1,dsd.Subscriber2,dsd.Subscriber3, nd.DealerMobileNumber "
					+ "FROM Notification_Dealers nd, DealerSubscriberDetails dsd "
					+ "WHERE nd.NotificationDealerID IN (dsd.Subscriber1, dsd.Subscriber2, dsd.Subscriber3) "
					+ "AND dsd.DealerCode = '" + dealerCode + "'";
			resultSet = statement.executeQuery(query);
			iLogger.info("query" + query);
			// Map dealer types to their names
			Map<Integer, String> dealerTypeMap = new HashMap<>();
			dealerTypeMap.put(1, "LLChampion");
			dealerTypeMap.put(2, "ServiceNotification");
			dealerTypeMap.put(3, "PartNotification");

			// Initialize the JSON structure
			Map<String, Map<String, String>> llChampion = new HashMap<>();
			Map<String, Map<String, String>> serviceNotification = new HashMap<>();
			Map<String, Map<String, String>> partNotification = new HashMap<>();

			// Process the result set and create the JSON structure
			while (resultSet.next()) {
				String commMode = resultSet.getString("CommMode");
				Integer [] subscribers = { resultSet.getInt("Subscriber1"), resultSet.getInt("Subscriber2"),
						resultSet.getInt("Subscriber3") };
				String dealerEmail = resultSet.getString("DealerEmail");
				String dealerMobileNumber = resultSet.getString("DealerMobileNumber");
				int dealerType = resultSet.getInt("DealerType");
				String dealerTypeName = dealerTypeMap.get(dealerType);
				int notificationDealerID = resultSet.getInt("NotificationDealerID");
				String dealerPrincipleName = resultSet.getString("DealerNotificationName").trim().replace(" ", "");

				for (int i = 0; i < subscribers.length; i++) {
						String subscriberKey = "S" + (i + 1);
						Map<String, String> details = new HashMap<>();
						if (subscribers[i] == null || subscribers[i] == 0) {
							details.put("sms","NONE" + "|" + "NONE" + "|" + "NONE");
							details.put("email", "NONE" + "|" + "NONE" + "|" + "NONE");
						} else if (subscribers[i] == notificationDealerID){
							if ("SMS".equals(commMode)) {
								details.put("sms",dealerMobileNumber + "|" + subscribers[i] + "|" + dealerPrincipleName);
							}
							if ("Email".equals(commMode)) {
								details.put("email", dealerEmail + "|" + subscribers[i] + "|" + dealerPrincipleName);
							}
						}

						if ("LLChampion".equals(dealerTypeName)) {
							llChampion.putIfAbsent(subscriberKey, new HashMap<>());
							llChampion.get(subscriberKey).putAll(details);
						} else if ("ServiceNotification".equals(dealerTypeName)) {
							serviceNotification.putIfAbsent(subscriberKey, new HashMap<>());
							serviceNotification.get(subscriberKey).putAll(details);
						} else if ("PartNotification".equals(dealerTypeName)) {
							partNotification.putIfAbsent(subscriberKey, new HashMap<>());
							partNotification.get(subscriberKey).putAll(details);
						}
					
				}
			}
			notifications.put("LLChampion", llChampion);
			notifications.put("ServiceNotification", serviceNotification);
			notifications.put("PartNotification", partNotification);

			// Close the connection
			connection.close();
		} catch (Exception e) {
			fLogger.fatal("FAILURE-Exception occurred in fetching Dealer Details:", e);
			e.printStackTrace();
		}

		return notifications;

	}

	public boolean deleteDealerByCode(String dealerCode, String NotificationDEalerID) {
		Logger iLogger = InfoLoggerClass.logger;
		// query to update the Status to 0 where DealerCode matches
		String query = "UPDATE Notification_Dealers SET Status = 0 WHERE DealerCode = '" + dealerCode
				+ "' and NotificationDealerID='" + NotificationDEalerID + "'";

		iLogger.info("Executing SQL query: " + query);
		ConnectMySQL connMySql = new ConnectMySQL();
		boolean isDeleted = false;

		try (Connection prodConnection = connMySql.getConnection();
				PreparedStatement preparedStatement = prodConnection.prepareStatement(query)) {
			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0) {
				// updated the status to 0
				isDeleted = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isDeleted;
	}

	public List<Map<String, Object>> fetchNotificationDealerDetails(String tenancyId) {
		List<Map<String, Object>> dealerDetailsList = new ArrayList<>();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		ResultSet rs = null;
		Statement statement = null;
		ConnectMySQL connMySql = new ConnectMySQL();
		Connection connection = null;
		String query = null;

		try {
			connection = connMySql.getConnection();
			if (connection == null) {
				iLogger.info("MySQL connection is null");
				return dealerDetailsList;
			}

			statement = connection.createStatement();
			query = "select * from Notification_Dealers where DealerCode in (select Tenancy_Code from tenancy where Tenancy_ID='" +tenancyId+ "') and Status=1 ";
				rs = statement.executeQuery(query);
				iLogger.info("query"+query);
				while (rs.next()) {
					Map<String, Object> dealerDetails = new HashMap<>();
					dealerDetails.put("notificationDealerID", rs.getInt("NotificationDealerID"));
					dealerDetails.put("dealerCode", rs.getString("DealerCode"));
					dealerDetails.put("dealerNotificationName", rs.getString("DealerNotificationName"));
					dealerDetails.put("dealerAddress", rs.getString("DealerAddress"));
					dealerDetails.put("state", rs.getString("State"));
					dealerDetails.put("dealerAccountName", rs.getString("DealerAccountName"));
					dealerDetails.put("dealerMobileNumber", rs.getString("DealerMobileNumber"));
					dealerDetails.put("dealerEmail", rs.getString("DealerEmail"));
					dealerDetails.put("city", rs.getString("City"));
					dealerDetails.put("zipCode", rs.getString("ZipCode"));
					dealerDetails.put("CountryName", rs.getString("CountryName"));
					dealerDetailsList.add(dealerDetails);
				}
			
		} catch (SQLException e) {
			fLogger.info("FAILURE - Exception occurred in fetching Dealer Details: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				fLogger.info("Exception occurred while closing resources: " + e.getMessage());
			}
		}
		return dealerDetailsList;
	}

	public boolean updatedealerDetails(HashMap<String, String> dealerDetails, String notificationDealerID) {
		boolean success = false;
		ConnectMySQL connMySql = new ConnectMySQL();
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String updateQuery = "UPDATE Notification_Dealers SET DealerNotificationName = ?, DealerAddress = ?, DealerMobileNumber = ?, DealerEmail = ?, ZipCode = ?, City = ?, State = ?, DealerAccountName = ?, CountryName = ? WHERE NotificationDealerID = ?";

		try (Connection prodConnection = connMySql.getConnection();
				PreparedStatement preparedStatement = prodConnection.prepareStatement(updateQuery)) {

			preparedStatement.setString(1, dealerDetails.get("dealerPrincipalName"));
			preparedStatement.setString(2, dealerDetails.get("address"));
			preparedStatement.setString(3, dealerDetails.get("mobileNumber"));
			preparedStatement.setString(4, dealerDetails.get("email"));
			preparedStatement.setString(5, dealerDetails.get("zipCode"));
			preparedStatement.setString(6, dealerDetails.get("city"));
			preparedStatement.setString(7, dealerDetails.get("state"));
			preparedStatement.setString(8, dealerDetails.get("dealerAccountName"));
			preparedStatement.setString(9, dealerDetails.get("countryName"));
			preparedStatement.setString(10, notificationDealerID);

			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0) {
				success = true;
				infoLogger.info("Record updated successfully");
			}

		} catch (SQLException e) {
			fLogger.error("SQL Exception: " + e.getMessage(), e);
			throw new RuntimeException("Database error", e);
		}

		return success;
	}
	
	public boolean updatePrincipleDealerDetails(HashMap<String, String> principledealerDetails, String principleDealerID) {
		boolean success = false;
		ConnectMySQL connMySql = new ConnectMySQL();
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String updateQuery = "UPDATE Principal_Dealers SET DealerPricipalName = ?, PrincipleDealerAddress = ?, PrincipleDealerMobileNumber = ?, PrincipleDealerEmail = ?, ZipCode = ?, City = ?, State = ?, DealerAccountName = ?, CountryName = ? WHERE PrincipalDealerID = ?";
		infoLogger.info("updateQuery"+updateQuery);
		try (Connection prodConnection = connMySql.getConnection();
				PreparedStatement preparedStatement = prodConnection.prepareStatement(updateQuery)) {

			preparedStatement.setString(1, principledealerDetails.get("dealerPrincipalName"));
			preparedStatement.setString(2, principledealerDetails.get("address"));
			preparedStatement.setString(3, principledealerDetails.get("mobileNumber"));
			preparedStatement.setString(4, principledealerDetails.get("email"));
			preparedStatement.setString(5, principledealerDetails.get("zipCode"));
			preparedStatement.setString(6, principledealerDetails.get("city"));
			preparedStatement.setString(7, principledealerDetails.get("state"));
			preparedStatement.setString(8, principledealerDetails.get("dealerAccountName"));
			preparedStatement.setString(9, principledealerDetails.get("countryName"));
			preparedStatement.setString(10, principleDealerID);

			int rowsAffected = preparedStatement.executeUpdate();
			infoLogger.info("rowsAffected"+rowsAffected);
			if (rowsAffected > 0) {
				success = true;
				infoLogger.info("Record updated successfully");
			}

		} catch (SQLException e) {
			fLogger.error("SQL Exception: " + e.getMessage(), e);
			throw new RuntimeException("Database error", e);
		}

		return success;
	}

	public List<Map<String, Object>> fetchPrincipalDetails(String tenancyId) {
		List<Map<String, Object>> dealerDetailsList = new ArrayList<>();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		ResultSet rs = null;
		Statement statement = null;
		ConnectMySQL connMySql = new ConnectMySQL();
		Connection connection = null;
		String query = null;

		try {
			connection = connMySql.getConnection();
			if (connection == null) {
				iLogger.info("MySQL connection is null");
				return dealerDetailsList;
			}

			statement = connection.createStatement();
			
			query = "select * from Principal_Dealers where DealerCode in (select Tenancy_Code from tenancy where Tenancy_ID='" +tenancyId+ "') and Status=1";
			rs = statement.executeQuery(query);
			iLogger.info("query"+query);
				while (rs.next()) {
					Map<String, Object> dealerDetails = new HashMap<>();
					dealerDetails.put("principalDealerID", rs.getInt("PrincipalDealerID"));
					dealerDetails.put("dealerCode", rs.getString("DealerCode"));
					dealerDetails.put("dealerPrincipalName", rs.getString("DealerPricipalName"));
					dealerDetails.put("dealerAddress", rs.getString("PrincipleDealerAddress"));
					dealerDetails.put("state", rs.getString("State"));
					dealerDetails.put("dealerAccountName", rs.getString("DealerAccountName"));
					dealerDetails.put("dealerMobileNumber", rs.getString("PrincipleDealerMobileNumber"));
					dealerDetails.put("dealerEmail", rs.getString("PrincipleDealerEmail"));
					dealerDetails.put("city", rs.getString("City"));
					dealerDetails.put("zipCode", rs.getString("ZipCode"));
					dealerDetails.put("CountryName", rs.getString("CountryName"));
					dealerDetailsList.add(dealerDetails);
				}
			
		} catch (SQLException e) {
			fLogger.info("FAILURE - Exception occurred in fetching Dealer Details: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				fLogger.info("Exception occurred while closing resources: " + e.getMessage());
			}
		}
		return dealerDetailsList;
	}

	public List<Map<String, Object>> getSubscriberDetailsUnderDealership(String roleName, String tenancyId) {
		List<Map<String, Object>> notificationsList = new ArrayList<>();
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		ResultSet resultSet = null;
		Statement statement = null;
		//String roleName = null;
		String query = null;
		try {
			connection = connFactory.getConnection();
			if (connection == null) {
				iLogger.info("MySQL connection is null");
				return notificationsList;
			}

			statement = connection.createStatement();
			//query = "Select Role_Name from role where Role_ID='" + roleId + "'";
			//resultSet = statement.executeQuery(query);
			//iLogger.info("query" + query);
			//while (resultSet.next()) {
			//	roleName = resultSet.getString("Role_Name");
			//}
			iLogger.info("roleName" + roleName);
			if ("JCBHO".equalsIgnoreCase(roleName.replace(" ", ""))) {
				query = "select distinct(Tenancy_Code) from tenancy t inner join tenancy_bridge tb on t.tenancy_id=tb.child_id where  tb.parent_id='"
						+ tenancyId + "' and level=2";
			} else if ("JCBRO".equalsIgnoreCase(roleName.replace(" ", ""))) {
				query = "select distinct(Tenancy_Code) from tenancy t inner join tenancy_bridge tb on t.tenancy_id=tb.child_id where  tb.parent_id='"
						+ tenancyId + "' and level=1";
			}else if ("Dealer".equalsIgnoreCase(roleName.replace(" ", ""))) {
						query = "select Tenancy_Code from tenancy  where  tenancy_id='"
								+ tenancyId + "'";
			} else if ("SuperAdmin".equalsIgnoreCase(roleName.replace(" ", "")) || "JCBAdmin".equalsIgnoreCase(roleName.replace(" ", ""))) {
				query = "SELECT a.Account_Name as dealer_name, zone.account_name as zone,dsd.DealerType as userType, nd.NotificationDealerID, pd.DealerPricipalName, pd.PrincipleDealerAddress, pd.PrincipleDealerEmail, pd.PrincipleDealerMobileNumber, pd.DealerCode, dsd.CommMode, nd.DealerEmail, dsd.Subscriber1, dsd.Subscriber2, dsd.Subscriber3, nd.DealerMobileNumber "
						+ "FROM Notification_Dealers nd, Principal_Dealers pd, account a,account zone , DealerSubscriberDetails dsd "
						+ "WHERE nd.NotificationDealerID IN (dsd.Subscriber1, dsd.Subscriber2, dsd.Subscriber3) AND dsd.DealerCode = pd.DealerCode  and a.account_code=dsd.DealerCode and a.parent_id = zone.account_id";

			}
			iLogger.info("query :" + query);
			if (query != null) {
				resultSet = statement.executeQuery(query);
				if ("Super Admin".equals(roleName)) {
					Map<Integer, String> dealerTypeMap = new HashMap<>();
					dealerTypeMap.put(1, "LLChampion");
					dealerTypeMap.put(2, "ServiceNotification");
					dealerTypeMap.put(3, "PartNotification");

					resultSet = statement.executeQuery(query);
					Map<Integer, String> notificationDealerEmailMap = new HashMap<>();
					Map<Integer, String> notificationDealerMobileMap = new HashMap<>();
					List<Integer> notificationDealerIDs = new ArrayList<>();
					while (resultSet.next()) {
						int notificationDealerID = resultSet.getInt("NotificationDealerID");
						notificationDealerIDs.add(notificationDealerID);
						String commMode = resultSet.getString("CommMode");
						String dealerEmail = resultSet.getString("DealerEmail");
						String dealerMobileNumber = resultSet.getString("DealerMobileNumber");
						notificationDealerEmailMap.put(notificationDealerID, dealerEmail);
						notificationDealerMobileMap.put(notificationDealerID, dealerMobileNumber);
					}

					resultSet.beforeFirst(); // Reset the cursor to the beginning

					while (resultSet.next()) {
						Integer[] subscribers = { resultSet.getInt("Subscriber1"), resultSet.getInt("Subscriber2"),
								resultSet.getInt("Subscriber3") };
						String principleDealerEmail = resultSet.getString("PrincipleDealerEmail");
						String principleDealerMobileNumber = resultSet.getString("PrincipleDealerMobileNumber");
						int dealerType = resultSet.getInt("userType");
						String commMode = resultSet.getString("CommMode");
						String dealerTypeName = dealerTypeMap.get(dealerType);
						String dealerPrincipleName = resultSet.getString("DealerPricipalName").trim().replace(" ", "");
						String principleDealerAddress = resultSet.getString("PrincipleDealerAddress");
						String dealerCode = resultSet.getString("DealerCode");
						String dealerName = resultSet.getString("dealer_name");
						String zone = resultSet.getString("zone");

						// Find or create the dealer details map
						Map<String, Object> dealerDetails = notificationsList.stream()
								.filter(map -> dealerCode.equals(map.get("DealerCode"))).findFirst().orElseGet(() -> {
									Map<String, Object> newDealerDetails = new HashMap<>();
									newDealerDetails.put("DealerCode", dealerCode);
									newDealerDetails.put("PrincipalDealerName", dealerPrincipleName);
									newDealerDetails.put("PrincipalDealerMobile", principleDealerMobileNumber);
									newDealerDetails.put("PrincipalDealerEmail", principleDealerEmail);
									newDealerDetails.put("PrincipalDealerAddress", principleDealerAddress);
									newDealerDetails.put("dealerName", dealerName);
									newDealerDetails.put("zone", zone);
									notificationsList.add(newDealerDetails);
									return newDealerDetails;
								});

						dealerDetails.putIfAbsent(dealerTypeName, new HashMap<>());
						Map<String, Map<String, Map<String, String>>> typeNotifications = (Map<String, Map<String, Map<String, String>>>) dealerDetails
								.get(dealerTypeName);

						for (int i = 0; i < subscribers.length; i++) {
							String subscriberKey = "S" + (i + 1);
							Map<String, String> smsDetails = new HashMap<>();
							Map<String, String> emailDetails = new HashMap<>();
							if (subscribers[i] == null || subscribers[i] == 0) {
								smsDetails.put("mobileNumber", "NA");
								emailDetails.put("emailid", "NA");
							} else if (notificationDealerIDs.contains(subscribers[i])) {
								if ("SMS".equals(commMode)) {
									smsDetails.put("mobileNumber", notificationDealerMobileMap.get(subscribers[i]));
								}
								if ("Email".equals(commMode)) {
									emailDetails.put("emailid", notificationDealerEmailMap.get(subscribers[i]));
								}
							}

							typeNotifications.putIfAbsent(subscriberKey, new HashMap<>());
							Map<String, Map<String, String>> subscriberNotifications = typeNotifications
									.get(subscriberKey);

							if (!smsDetails.isEmpty()) {
								subscriberNotifications.put("sms", smsDetails);
							}
							if (!emailDetails.isEmpty()) {
								subscriberNotifications.put("email", emailDetails);
							}
						}
					}
				} else {

					List<String> dealerCodes = new ArrayList<>();

					while (resultSet.next()) {
						dealerCodes.add(resultSet.getString("Tenancy_Code"));
					}

					iLogger.info("Dealer codes: " + dealerCodes);
					for (String dealerCode1 : dealerCodes) {
						query = "SELECT acc.account_name, dsd.DealerType, nd.NotificationDealerID, pd.DealerPricipalName, pd.PrincipleDealerAddress, pd.PrincipleDealerEmail, pd.PrincipleDealerMobileNumber, pd.DealerCode, dsd.CommMode, nd.DealerEmail, dsd.Subscriber1, dsd.Subscriber2, dsd.Subscriber3, nd.DealerMobileNumber "
								+ "FROM Notification_Dealers nd, Principal_Dealers pd, DealerSubscriberDetails dsd, account acc "
								+ "WHERE nd.NotificationDealerID IN (dsd.Subscriber1, dsd.Subscriber2, dsd.Subscriber3) and dsd.DealerCode = nd.DealerCode and pd.DealerCode = dsd.DealerCode AND  acc.account_code= pd.DealerCode and dsd.DealerCode='"
								+ dealerCode1 + "' ";

						Map<Integer, String> dealerTypeMap = new HashMap<>();
						dealerTypeMap.put(1, "LLChampion");
						dealerTypeMap.put(2, "ServiceNotification");
						dealerTypeMap.put(3, "PartNotification");

						resultSet = statement.executeQuery(query);
						iLogger.info("Executing query: " + query);
						Map<Integer, String> notificationDealerEmailMap = new HashMap<>();
						Map<Integer, String> notificationDealerMobileMap = new HashMap<>();
						List<Integer> notificationDealerIDs = new ArrayList<>();
						while (resultSet.next()) {
							int notificationDealerID = resultSet.getInt("NotificationDealerID");
							notificationDealerIDs.add(notificationDealerID);
							String commMode = resultSet.getString("CommMode");
							String dealerEmail = resultSet.getString("DealerEmail");
							String dealerMobileNumber = resultSet.getString("DealerMobileNumber");
							
							notificationDealerEmailMap.put(notificationDealerID, dealerEmail);
							notificationDealerMobileMap.put(notificationDealerID, dealerMobileNumber);
							
						}

						resultSet.beforeFirst(); // Reset the cursor to the beginning

						while (resultSet.next()) {
							Integer [] subscribers = { resultSet.getInt("Subscriber1"), resultSet.getInt("Subscriber2"),
									resultSet.getInt("Subscriber3") };
							String principleDealerEmail = resultSet.getString("PrincipleDealerEmail");
							String principleDealerMobileNumber = resultSet.getString("PrincipleDealerMobileNumber");
							int dealerType = resultSet.getInt("DealerType");
							String commMode = resultSet.getString("CommMode");
							String dealerTypeName = dealerTypeMap.get(dealerType);
							String dealerPrincipleName = resultSet.getString("DealerPricipalName").trim().replace(" ",
									"");
							String principleDealerAddress = resultSet.getString("PrincipleDealerAddress");
							String dealerCode = resultSet.getString("DealerCode");
							String dealerEmail = resultSet.getString("DealerEmail");
							String dealerMobileNumber = resultSet.getString("DealerMobileNumber");
							String accountName = resultSet.getString("account_name");
							// Find or create the dealer details map
							Map<String, Object> dealerDetails = notificationsList.stream()
									.filter(map -> dealerCode.equals(map.get("DealerCode"))).findFirst()
									.orElseGet(() -> {
										Map<String, Object> newDealerDetails = new HashMap<>();
										newDealerDetails.put("DealerCode", dealerCode);
										newDealerDetails.put("PrincipalDealerName", dealerPrincipleName);
										newDealerDetails.put("PrincipalDealerMobile", principleDealerMobileNumber);
										newDealerDetails.put("PrincipalDealerEmail", principleDealerEmail);
										newDealerDetails.put("PrincipalDealerAddress", principleDealerAddress);
										newDealerDetails.put("dealerName", accountName);
										notificationsList.add(newDealerDetails);
										return newDealerDetails;
									});

							dealerDetails.putIfAbsent(dealerTypeName, new HashMap<>());
							@SuppressWarnings("unchecked")
							Map<String, Map<String, Map<String, String>>> typeNotifications = (Map<String, Map<String, Map<String, String>>>) dealerDetails
									.get(dealerTypeName);

							for (int i = 0; i < subscribers.length; i++) {
								String subscriberKey = "S" + (i + 1);
								Map<String, String> smsDetails = new HashMap<>();
								Map<String, String> emailDetails = new HashMap<>();
								if (subscribers[i] == null || subscribers[i] == 0) {
									smsDetails.put("mobileNumber", "NA");
									emailDetails.put("emailid", "NA");
								} else if (notificationDealerIDs.contains(subscribers[i])) {
									if ("SMS".equals(commMode)) {
										smsDetails.put("mobileNumber", notificationDealerMobileMap.get(subscribers[i]));
									}
									if ("Email".equals(commMode)) {
										emailDetails.put("emailid", notificationDealerEmailMap.get(subscribers[i]));
									}
								}

								typeNotifications.putIfAbsent(subscriberKey, new HashMap<>());
								Map<String, Map<String, String>> subscriberNotifications = typeNotifications
										.get(subscriberKey);

								if (!smsDetails.isEmpty()) {
									subscriberNotifications.put("sms", smsDetails);
								}
								if (!emailDetails.isEmpty()) {
									subscriberNotifications.put("email", emailDetails);
								}
							}
						}
					}

				}
			}
		} catch (Exception e) {
			fLogger.info("FAILURE - Exception occurred in fetching Dealer Details: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (Exception e) {
				fLogger.info("FAILURE - Exception occurred in closing resources: " + e.getMessage());
				e.printStackTrace();
			}
		}

		return notificationsList;
	}
	  
	  public List<Map<String, Object>> fetchDealerDetailsbyDealerCode(String dealerCode) {
			List<Map<String, Object>> dealerDetailsList = new ArrayList<>();
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
			ResultSet rs = null;
			String query = "SELECT * FROM Notification_Dealers WHERE DealerCode = ?";

			ConnectMySQL connMySql = new ConnectMySQL();

			try (Connection prodConnection = connMySql.getConnection();
					PreparedStatement preparedStatement = prodConnection.prepareStatement(query)) {
				preparedStatement.setString(1, dealerCode);
				rs = preparedStatement.executeQuery();
				iLogger.info("Executing Query:" + query);
				while (rs.next()) {
					Map<String, Object> dealerDetails = new HashMap<>();
					dealerDetails.put("NotificationDealerID", rs.getInt("NotificationDealerID"));
					dealerDetails.put("dealerCode", rs.getString("DealerCode"));
					dealerDetails.put("dealerNotificationName", rs.getString("DealerNotificationName").trim().replace(" ", ""));
					dealerDetails.put("dealerAddress", rs.getString("DealerAddress"));
					dealerDetails.put("dealerMobileNumber", rs.getString("DealerMobileNumber"));
					dealerDetails.put("dealerEmail", rs.getString("DealerEmail"));

					dealerDetailsList.add(dealerDetails);
				}
			} catch (SQLException e) {
				fLogger.fatal("FAILURE-Exception occurred in fetching Dealer Details:", e);
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (Exception e) {
					fLogger.error("Exception occurred while closing resources: " + e.getMessage());
				}
			}
			return dealerDetailsList;
		}
}
