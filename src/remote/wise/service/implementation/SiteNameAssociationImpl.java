package remote.wise.service.implementation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.simple.JSONObject;

import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.SiteNameAssociationForMACustomerEntity;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.SiteNameAssociationForMACustomerReqContract;

import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;

public class SiteNameAssociationImpl {
	Logger fLogger = FatalLoggerClass.logger;
	Logger infoLogger = InfoLoggerClass.logger;

	public String SiteNameAssociation(
			SiteNameAssociationForMACustomerReqContract request)
			throws CustomFault {
		String selectQuery = null;
		String queryForSiteNameCheck = null;
		String accountId = null;
		List<String> accountIdList = new ArrayList<String>();
		String mappingCode = null;
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String SiteName = null;
		String result = "FAILURE";
		Session session = null;
		if (request.getUserId() == null) {
			throw new CustomFault("Login Id should be specified");
		}

		if (request.getSiteName() == null) {
			throw new CustomFault("siteName should be specified");
		}
		try {
			infoLogger
					.info("-- SiteNameAssociationImpl::: SiteNameAssociation()-- START");
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			SiteNameAssociationForMACustomerEntity associationForMACustomerEntity = new SiteNameAssociationForMACustomerEntity();
			// Fetching AccountID corresponding to Login UserID
			selectQuery = "select Account_ID from account_contact where Contact_ID ='"
					+ request.getUserId() + "' ";
			infoLogger
					.info("SiteNameAssociationImpl::: SiteNameAssociation() --- selectQuery------ "
							+ selectQuery);
			try {
				rs = statement.executeQuery(selectQuery);
				while (rs.next()) {
					accountId = rs.getString("Account_ID");
				}
			} catch (Exception e) {
				fLogger.fatal("SiteNameAssociationImpl::: SiteNameAssociation() -- issue with selectQuery "
						+ selectQuery + e.getMessage());
			}
			infoLogger
					.info(" ============== Fetching AccountID corresponding to Login UserID ==============  "
							+ accountId);
			// Fetching mappingCode corresponding to account ID
			selectQuery = "select mapping_code from account where Account_ID ='"
					+ accountId + "' ";
			try {
				rs = statement.executeQuery(selectQuery);
				while (rs.next()) {
					mappingCode = rs.getString("mapping_code");
				}
			} catch (Exception e) {
				fLogger.fatal("SiteNameAssociationImpl::: SiteNameAssociation() -- issue with selectQuery "
						+ selectQuery + e.getMessage());
			}
			infoLogger
					.info("============ Fetching mappingCode corresponding to account ID ========== "
							+ mappingCode);
			// Fetching all Account ID corresponding to Mapping code
			selectQuery = "select Account_ID from account where mapping_code ='"
					+ mappingCode + "' ";
			try {
				rs = statement.executeQuery(selectQuery);
				while (rs.next()) {
					accountIdList.add(rs.getString("Account_ID"));

				}
			} catch (Exception e) {
				fLogger.fatal("SiteNameAssociationImpl::: SiteNameAssociation() -- issue with selectQuery "
						+ selectQuery + e.getMessage());
			}
			infoLogger
					.info("============ Fetching all Account ID corresponding to Mapping code ========== "
							+ accountIdList);
			System.out.println("--------- accountIdList -----------"
					+ accountIdList);
			Iterator<String> iterator = accountIdList.iterator();
			while (iterator.hasNext()) {
				session = HibernateUtil.getSessionFactory().openSession();
				session.beginTransaction();
				accountId = iterator.next();
				associationForMACustomerEntity.setAccountId(accountId);
				// Setting UserID to the table come from input
				associationForMACustomerEntity.setUserId(request.getUserId());
				queryForSiteNameCheck = "Select SiteName from SiteMaster where UserId ='"
						+ request.getUserId()
						+ "' and AccountId ='"
						+ associationForMACustomerEntity.getAccountId() + "' ";
				infoLogger
						.info("SiteNameAssociationImpl::: SiteNameAssociation() ::: queryForSiteNameCheck------ "
								+ queryForSiteNameCheck);
				try {
					connection = connMySql.getConnection();
					statement = connection.createStatement();
					rs = statement.executeQuery(queryForSiteNameCheck);
					while (rs.next()) {
						if (rs.getString("SiteName") == null
								|| rs.getString("SiteName").equalsIgnoreCase(
										"null")) {
							associationForMACustomerEntity.setSiteName(request
									.getSiteName());
						} else {
							SiteName = rs.getString("SiteName");
							if (SiteName == request.getSiteName()
									|| SiteName.equals(request.getSiteName())) {
								return "sitename already exists";
							}
						}
					}
					associationForMACustomerEntity.setSiteName(request
							.getSiteName());
					infoLogger
							.info("associationForMACustomerEntity.getSiteName :: "
									+ associationForMACustomerEntity
											.getSiteName());
				} catch (Exception e) {
					fLogger.fatal("SiteNameAssociationImpl::: SiteNameAssociation() ::: issue with queryForSiteNameCheck "
							+ queryForSiteNameCheck + e.getMessage());
				}

				Calendar c = Calendar.getInstance();
				// Current Time Inserting into the table
				associationForMACustomerEntity
						.setLastUpdatedTime(new Timestamp(c.getTime().getTime()));
				// set as true for insertion
				associationForMACustomerEntity.setFlag(true);
				session.save(associationForMACustomerEntity);
				session.getTransaction().commit();
				// session.close();
				infoLogger
						.info("SiteNameAssociationImpl::: SiteNameAssociation()-- END");
			}
			session.close();
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception:" + e.getMessage());
			return result;
		}
		finally {
		    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }}
	}

	public List<String> displaySiteNameAssociatedWithUser(String userId)
			throws CustomFault {
		if (userId == null) {
			throw new CustomFault("Login Id should be specified");
		}
		String selectQuerySiteName = null;
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		List<String> siteNameList = new ArrayList<String>();
		infoLogger
				.info("-- SiteNameAssociationImpl::: displaySiteNameAssociatedWithUser()-- START");
		selectQuerySiteName = "Select distinct(SiteName) from SiteMaster where UserId ='"
				+ userId + "' ";
		infoLogger
				.info("SiteNameAssociationImpl ::: displaySiteNameAssociatedWithUser() ::: selectQuerySiteName "
						+ selectQuerySiteName);
		try {
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(selectQuerySiteName);
			while (rs.next()) {
				if (rs.getString("SiteName") == null
						|| rs.getString("SiteName").equalsIgnoreCase("null")) {
					siteNameList.add("NA");
				} else {
					siteNameList.add(rs.getString("SiteName"));
				}
			}

		} catch (Exception e) {
			fLogger.fatal("SiteNameAssociationImpl::: displaySiteNameAssociatedWithUser() :: issue with selectQuerySiteName "
					+ selectQuerySiteName + e.getMessage());
		}
		// Shivam : 20211125 :  Awake ID: 5686 -start
		finally {
		    try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
		    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
		}
		// Shivam : 20211125 :  Awake ID: 5686 -end
		infoLogger
				.info("-- SiteNameAssociationImpl::: displaySiteNameAssociatedWithUser()-- END");
		return siteNameList;
	}

	@SuppressWarnings("rawtypes")
	public List<HashMap<String, String>> displayVinAssociatedWithUser(
			String userId) throws CustomFault {
		if (userId == null) {
			throw new CustomFault("Login Id should be specified");
		}
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String selectQuery = null;
		String accountId = null;
		HashMap<String, String> mappings = null;
		List<HashMap<String, String>> vinSiteAssociation = new LinkedList<HashMap<String, String>>();
		List<String> accountIdList = new ArrayList<String>();
		String vinList = null;
		String siteNameList = null;
		Query assetQuery = null;
		/* String assetQuerysql = null; */
		String mappingCode = null;
		/* String siteId = null; */
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		infoLogger
				.info("-- SiteNameAssociationImpl::: displayVinAssociatedWithUser()-- START");
		ConnectMySQL connMySql = new ConnectMySQL();
		connection = connMySql.getConnection();

		// Fetching AccountID corresponding to Login UserID
		selectQuery = "select Account_ID from account_contact where Contact_ID ='"
				+ userId + "' ";
		infoLogger
				.info("SiteNameAssociationImpl::: displayVinAssociatedWithUser() --- selectQuery------ "
						+ selectQuery);
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery(selectQuery);
			while (rs.next()) {
				accountId = rs.getString("Account_ID");
			}
		} catch (Exception e) {
			fLogger.fatal("SiteNameAssociationImpl::: displayVinAssociatedWithUser() -- issue with selectQuery "
					+ selectQuery + e.getMessage());
		}
		infoLogger
				.info(" ============== Fetching AccountID corresponding to Login UserID ::  ==============  "
						+ accountId);
		// Fetching mappingCode corresponding to account ID
		selectQuery = "select mapping_code from account where Account_ID ='"
				+ accountId + "' ";
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery(selectQuery);
			while (rs.next()) {
				mappingCode = rs.getString("mapping_code");
			}
		} catch (Exception e) {
			fLogger.fatal("SiteNameAssociationImpl::: displayVinAssociatedWithUser() -- issue with selectQuery "
					+ selectQuery + e.getMessage());
		}
		infoLogger
				.info("============ Fetching mappingCode corresponding to account ID ========== "
						+ mappingCode);
		// Fetching all Account ID corresponding to Mapping code
		selectQuery = "select Account_ID from account where mapping_code ='"
				+ mappingCode + "' ";
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery(selectQuery);
			while (rs.next()) {
				accountIdList.add(rs.getString("Account_ID"));

			}
		} catch (Exception e) {
			fLogger.fatal("SiteNameAssociationImpl::: displayVinAssociatedWithUser() -- issue with selectQuery "
					+ selectQuery + e.getMessage());
		}
		infoLogger
				.info("============ Fetching all Account ID corresponding to Mapping code ========== "
						+ accountIdList);
		System.out.println("--------- accountIdList -----------"
				+ accountIdList);
		ListToStringConversion convert = new ListToStringConversion();
		accountId = convert.getStringList(accountIdList).toString();
		infoLogger
				.info("-- -SiteNameAssociationImpl::: displayVinAssociatedWithUser()--accountId----------- \n"
						+ accountId);
		try {
			/* This is for PROD */
			/*
			 * assetQuery = session.createQuery(
			 * " select a.serial_number,a.siteId from AssetEntity a,AccountEntity ac where a.primary_owner_id in ("
			 * + accountId + ") and ac.account_id = a.primary_owner_id ");
			 * infoLogger .info(
			 * "-- SiteNameAssociationImpl::: displayVinAssociatedWithUser()-- assetQuery \n"
			 * + assetQuery); Iterator accItr = assetQuery.list().iterator();
			 * Object[] resultQ = null; while (accItr.hasNext()) {
			 * infoLogger.info("-- Inside While for sitename and vinnumber");
			 * mappings = new HashMap<String, String>(); resultQ = (Object[])
			 * accItr.next(); // AssetEntity assetEntity = (AssetEntity)
			 * accItr.next(); // vinList =
			 * assetEntity.getSerial_number().getSerialNumber(); vinList =
			 * ((AssetEntity) resultQ[0]).getSerial_number().getSerialNumber();
			 * infoLogger.info("-----------   vinList  -------------------   "+
			 * vinList); // int siteIdva = assetEntity.getSiteId().getSiteId();
			 * int siteIdva = ((AssetEntity)
			 * resultQ[1]).getSiteId().getSiteId();
			 * infoLogger.info("-----------   siteIdva  -------------------   "+
			 * siteIdva); if (siteIdva == 0) { mappings.put(vinList, "NA");
			 * infoLogger .info(
			 * "-- -SiteNameAssociationImpl::: displayVinAssociatedWithUser()--mappings if siteId is null----------- \n"
			 * + mappings); } else { siteNameList = ((AssetEntity)
			 * resultQ[1]).getSiteId().getSiteName(); mappings.put(vinList,
			 * siteNameList); infoLogger .info(
			 * "-- -SiteNameAssociationImpl::: displayVinAssociatedWithUser()--mappings if siteId is not null----------- \n"
			 * + mappings); } vinSiteAssociation.add(mappings); }
			 * 
			 * infoLogger .info(
			 * "-- SiteNameAssociationImpl::: displayVinAssociatedWithUser()-- mappings"
			 * + mappings);
			 */
			/* This is for Staging - */
			assetQuery = session
					.createQuery("select a from AssetEntity a,AccountEntity ac where a.primary_owner_id in ("
							+ accountId
							+ ") and ac.account_id = a.primary_owner_id ");
			infoLogger
					.info("-- SiteNameAssociationImpl::: displayVinAssociatedWithUser()-- assetQuery"
							+ assetQuery);
			Iterator accItr = assetQuery.list().iterator();
			while (accItr.hasNext()) {
				mappings = new HashMap<String, String>();
				AssetEntity assetEntity = (AssetEntity) accItr.next();
				vinList = assetEntity.getSerial_number().getSerialNumber();
				infoLogger.info("-----------   vinList  -------------------   "
						+ vinList);
				int siteIdva = assetEntity.getSiteId().getSiteId();
				infoLogger
						.info("-----------   siteIdva  -------------------   "
								+ siteIdva);
				if (siteIdva == 0) {
					mappings.put(vinList, "NA");
					infoLogger
							.info("-- -SiteNameAssociationImpl::: displayVinAssociatedWithUser()--mappings if siteId is null----------- \n"
									+ mappings);
				} else {
					siteNameList = assetEntity.getSiteId().getSiteName();
					mappings.put(vinList, siteNameList);
				}
				vinSiteAssociation.add(mappings);
			}
			infoLogger
					.info("-- SiteNameAssociationImpl::: displayVinAssociatedWithUser()-- mappings"
							+ mappings);
		} catch (Exception e) {
			fLogger.fatal("SiteNameAssociationImpl::: displayVinAssociatedWithUser() :: issue with assetQuery "
					+ assetQuery + e.getMessage());
			e.printStackTrace();

		} finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}
			// Shivam : 20211125 :  Awake ID: 5686 -start
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (connection!= null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			// Shivam : 20211125 :  Awake ID: 5686 -end
		}

		infoLogger
				.info("-- SiteNameAssociationImpl::: displayVinAssociatedWithUser()-- END");
		return vinSiteAssociation;
	}

	public String editSiteVinMapping(String userId, String siteName,
			List<String> serialNumberList) throws CustomFault {
		String result = "FAILURE";
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String selectQuery = null;
		List<String> accountId = new ArrayList<String>();
		if (userId == null) {
			throw new CustomFault("Login Id should be specified");
		}

		if (siteName == null) {
			throw new CustomFault("siteName should be specified");
		}
		if (serialNumberList == null || serialNumberList.size() == 0) {
			throw new CustomFault("serialNumber should be specified");
		}
		infoLogger
				.info("-- SiteNameAssociationImpl::: editSiteVinMapping()-- START");

		selectQuery = "select AccountId  from SiteMaster where UserId ='"
				+ userId + "' and SiteName ='" + siteName + "'";
		try {
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(selectQuery);
			while (rs.next()) {
				accountId.add(rs.getString("AccountId"));

			}
			infoLogger
					.info("SiteNameAssociationImpl: accountId before updating in asset table"
							+ accountId);
		} catch (Exception e) {
			fLogger.fatal("SiteNameAssociationImpl::: displayVinAssociatedWithUser() :: Error in BD connection /issue with the query "
					+ selectQuery + e.getMessage());
		}
		// Shivam : 20211125 :  Awake ID: 5686 -start
		finally {
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (connection!= null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	// Shivam : 20211125 :  Awake ID: 5686 -end
		result = updateSiteNameForVin(accountId, serialNumberList);
		return result;
	}

	private String updateSiteNameForVin(List<String> accountId,
			List<String> serialNumberList) {
		String response = "SUCCESS";
		String serialNumber = null;
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		int siteId = 0;
		try {
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			for (String accountIdList : accountId) {
				String query = "select SiteId from SiteMaster where AccountId='"
						+ accountIdList + "'  ";
				statement = connection.createStatement();
				rs = statement.executeQuery(query);
				while (rs.next()) {
					siteId = rs.getInt("SiteId");
				}
				ListToStringConversion convert = new ListToStringConversion();
				serialNumber = convert.getStringList(serialNumberList)
						.toString();
				String updateQuery = "update asset set SiteId=" + "'" + siteId
						+ "'" + " where Serial_Number in (" + serialNumber
						+ ") and Primary_Owner_ID in('" + accountIdList + "')";
				infoLogger
						.info("SiteNameAssociationImpl: updateQueryForMACustomer : "
								+ updateQuery);
				statement = connection.createStatement();
				statement.executeUpdate(updateQuery);
				try {
					response = updateSiteNameForVinInMoolDA(serialNumberList,
							siteId);

				} catch (Exception e) {
					fLogger.fatal("Exception in updating  the {/MoolDA/assetProfile/} URL for NickName"
							+ e.getMessage() + " for VIN:" + serialNumber);
					return "FAILURE";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Shivam : 20211125 :  Awake ID: 5686 -start	
		finally {

			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (connection!= null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
	// Shivam : 20211125 :  Awake ID: 5686 -End
		return response;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String updateSiteNameForVinInMoolDA(List<String> serialNumber,
			int siteId) throws Exception {
		String connIP, connPort, finalJsonString;
		connIP = connPort = finalJsonString = null;
		String result = "SUCCESS";
		String SiteName = null;
		/*
		 * ListToStringConversion convert=new ListToStringConversion(); String
		 * serial_Number =convert.getStringList(serialNumber).toString();
		 */
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			for (String serial_Number : serialNumber) {
				infoLogger.info("-------- Calling MoolDa for " + serial_Number
						+ " START ------------------");
				Query assetQuery = session
						.createQuery("select a from AssetEntity a where a.serial_number in('"
								+ serial_Number + "')");
				infoLogger
						.info("SiteNameAssociationImpl:updateSiteNameForVinInMoolDA : assetQuery"
								+ assetQuery);
				Iterator assetItr = assetQuery.list().iterator();
				while (assetItr.hasNext()) {
					infoLogger
							.info("SiteNameAssociationImpl:updateSiteNameForVinInMoolDA : while start");
					AssetEntity assetEntity = (AssetEntity) assetItr.next();
					int siteIdva = assetEntity.getSiteId().getSiteId();
					if (siteIdva == 0) {
						SiteName = "NA";
					} else {
						SiteName = assetEntity.getSiteId().getSiteName();
					}
					serial_Number = assetEntity.getSerial_number()
							.getSerialNumber();
					infoLogger
							.info("SiteNameAssociationImpl:updateSiteNameForVinInMoolDA : SiteName "
									+ SiteName);
					infoLogger
							.info("SiteNameAssociationImpl:updateSiteNameForVinInMoolDA : serial_Number "
									+ serial_Number);
					HashMap<String, String> finalDetailsMap = new HashMap<String, String>();
					if (serial_Number != null
							&& !serial_Number.equalsIgnoreCase("null")) {
						finalDetailsMap.put("AssetID", serial_Number);
					}
					if (SiteName != null && !SiteName.equalsIgnoreCase("null")) {
						finalDetailsMap.put("SiteName", SiteName);
					}
					JSONObject jsonObj = new JSONObject();
					jsonObj.putAll(finalDetailsMap);
					finalJsonString = jsonObj.toString();
					try {
						Properties prop = new Properties();
						prop.load(getClass()
								.getClassLoader()
								.getResourceAsStream(
										"remote/wise/resource/properties/configuration.properties"));
						connIP = prop.getProperty("MDA_ServerIP");
						connPort = prop.getProperty("MDA_ServerPort");
					} catch (Exception e) {
						fLogger.fatal("SiteNameAssociationImpl:updateSiteNameForVinInMoolDA: "
								+ "Exception in getting Server Details for MDA Layer from properties file: "
								+ e + " for VIN:" + serial_Number);
						throw new Exception(
								"Error reading from properties file");
					}
					try {
						// Invoking the REST URL from WISE to MOOL DA.
						URL url = new URL("http://" + connIP + ":" + connPort
								+ "/MoolDA/assetProfile/"
								+ "inputPayloadDetails?inputPayloadMap="
								+ URLEncoder.encode(finalJsonString, "UTF-8"));
						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.setRequestProperty("Content-Type",
								"text/plain; charset=utf8");
						conn.setRequestProperty("Accept", "text/plain");
						conn.setRequestMethod("GET");
						if (conn.getResponseCode() != 200
								&& conn.getResponseCode() != 204) {
							throw new Exception("Failed : HTTP error code : "
									+ conn.getResponseCode());
						}
						BufferedReader br = new BufferedReader(
								new InputStreamReader((conn.getInputStream())));
						String outputResponse = null;
						while ((outputResponse = br.readLine()) != null) {
							infoLogger
									.info("SiteNameAssociationImpl:updateSiteNameForVinInMoolDA:"
											+ " Response from {/MoolDA/assetProfile/}is: "
											+ outputResponse
											+ " for VIN:"
											+ serial_Number);
						}
						infoLogger
								.info("SiteNameAssociationImpl:updateSiteNameForVinInMoolDA: Mool DA updated succesfully for vin "
										+ serial_Number);
						infoLogger
								.info("SiteNameAssociationImpl:updateSiteNameForVinInMoolDA : while END");
						infoLogger.info("-------- Calling MoolDa for "
								+ serial_Number + " END ------------------");
					} catch (Exception e) {
						fLogger.fatal("SiteNameAssociationImpl:updateSiteNameForVinInMoolDA: "
								+ " Exception in invoking the {/MoolDA/assetProfile/} URL: "
								+ e.getMessage() + " for VIN:" + serial_Number);

					}
				}
			}
		} catch (Exception e) {
			fLogger.fatal("SiteNameAssociationImpl:updateSiteNameForVinInMoolDA: "
					+ "Exception in getting SiteName : "
					+ e
					+ " for VIN:"
					+ serialNumber);
			return "FAILURE";
		}

		return result;
	}

	/*// Shajesh : 20211005 :Updating Nick name and notes Details in MoolDA DB:

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String moolDAUpdationForNickNameandNote(String serialNumber)
			throws Exception {
		Logger fLogger = FatalLoggerClass.logger;
		Logger infoLogger = InfoLoggerClass.logger;
		String connIP, connPort, finalJsonString;
		connIP = connPort = finalJsonString = null;
		String selectQuery = null;
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String NickName = null;
		String Notes = null;
		String SiteName = null;
		String result = "FAILURE";
		infoLogger
				.info("SiteNameAssociationImpl:moolDAUpdationForNickNameandNote : Input Params serialNumber:"
						+ serialNumber);
		try {
			Session session = HibernateUtil.getSessionFactory()
					.getCurrentSession();
			session.beginTransaction();
			Query assetQuery = session
					.createQuery("select a from AssetEntity a where a.serial_number ='"
							+ serialNumber + "'");
			infoLogger
					.info("SiteNameAssociationImpl:moolDAUpdationForNickNameandNote : assetQuery"
							+ assetQuery);
			Iterator assetItr = assetQuery.list().iterator();
			while (assetItr.hasNext()) {
				AssetEntity assetEntity = (AssetEntity) assetItr.next();
				SiteName = assetEntity.getSiteId().getSiteName();
				NickName = assetEntity.getNick_name();
			}
			if (SiteName == null) {
				SiteName = "NA";
			}
			if (NickName == null) {
				NickName = "NA";
			}
			infoLogger.info("Site Name -----------------" + SiteName);
			infoLogger.info("Nick Name -----------------" + NickName);
			selectQuery = "select  notes from  asset_profile where serialNumber ='"
					+ serialNumber + "'";
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(selectQuery);
			while (rs.next()) {
				if (rs.getString("notes") != null) {
					Notes = rs.getString("notes");
				} else {
					Notes = "NA";
				}
			}
			infoLogger.info("Notes -----------------" + Notes);
			HashMap<String, String> finalDetailsMap = new HashMap<String, String>();
			if (serialNumber != null && !serialNumber.equalsIgnoreCase("null")) {
				finalDetailsMap.put("AssetID", serialNumber);
			}
			if (NickName != null && !NickName.equalsIgnoreCase("null")) {
				finalDetailsMap.put("NickName", NickName);
			}
			if (Notes != null && !Notes.equalsIgnoreCase("null")) {
				finalDetailsMap.put("Notes", Notes);
			}
			JSONObject jsonObj = new JSONObject();
			jsonObj.putAll(finalDetailsMap);
			finalJsonString = jsonObj.toString();
			try {
				Properties prop = new Properties();
				prop.load(getClass()
						.getClassLoader()
						.getResourceAsStream(
								"remote/wise/resource/properties/configuration.properties"));
				connIP = prop.getProperty("MDA_ServerIP");
				connPort = prop.getProperty("MDA_ServerPort");
			} catch (Exception e) {
				fLogger.fatal("SiteNameAssociationImpl:moolDAUpdationForNickNameandNote: "
						+ "Exception in getting Server Details for MDA Layer from properties file: "
						+ e + " for VIN:" + serialNumber);
				throw new Exception("Error reading from properties file");
			}
			try { // Invoking the
				// REST URL from WISE to MOOL DA.
				URL url = new URL("http://" + connIP + ":" + connPort
						+ "/MoolDA/assetProfile/"
						+ "inputPayloadDetails?inputPayloadMap="
						+ URLEncoder.encode(finalJsonString, "UTF-8"));
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestProperty("Content-Type",
						"text/plain; charset=utf8");
				conn.setRequestProperty("Accept", "text/plain");
				conn.setRequestMethod("GET");
				if (conn.getResponseCode() != 200
						&& conn.getResponseCode() != 204) {
					throw new Exception("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));
				String outputResponse = null;
				while ((outputResponse = br.readLine()) != null) {
					infoLogger
							.info("SiteNameAssociationImpl:moolDAUpdationForNickNameandNote:"
									+ " Response from {/MoolDA/assetProfile/}is: "
									+ outputResponse
									+ " for VIN:"
									+ serialNumber);
				}
				return "SUCCESS";
			} catch (Exception e) {
				fLogger.fatal("SiteNameAssociationImpl:moolDAUpdationForNickNameandNote: "
						+ " Exception in invoking the {/MoolDA/assetProfile/} URL: "
						+ e.getMessage() + " for VIN:" + serialNumber);
				throw new Exception("REST URL not available");

			}
		} catch (Exception e) {
			fLogger.fatal("SiteNameAssociationImpl:moolDAUpdationForNickNameandNote:Exception Cause: "
					+ e.getMessage());
		}
		return result;
	}*/

}
