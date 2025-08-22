package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * DF100001418:20220429:DHIRAJ K:Notification Subscriber Report issue 
 * CR500 : 20241128 : Dhiraj Kumar : WHatsApp Integration with LL
 */

public class SubscriberReportServiceTask implements Runnable {

	Map<String, Object> vinData;
	String vin;
	Object subscribergroupdata;
	ResultSet ns3rs = null, smsItr = null, contactItr = null,
			accountItr = null, accountNameItr = null,
			waItr=null;//CR500.n
	Statement statement2 = null, statement3 = null, statement4 = null;
	Connection prodConnection = null;
	Map<String, Object> subsGroupMap = null;

	public SubscriberReportServiceTask(Map<String, Object> vinData, Connection con) {
		this.prodConnection = con;
		this.vinData = vinData;
	}

	@Override
	public void run() {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		try {
			statement2 = prodConnection.createStatement();
			statement3 = prodConnection.createStatement();

			for (Map.Entry<String, Object> vinMap : vinData.entrySet()) {
				vin = vinMap.getKey();
				
				subscribergroupdata = vinMap.getValue();
				String subscriberJSONString = subscribergroupdata.toString();
				subsGroupMap = new Gson().fromJson(subscriberJSONString,
						new TypeToken<HashMap<String, Object>>() {
						}.getType());
				subsGroupMap.remove("@type");
				subsGroupMap.remove("@version");
				for (Map.Entry<String, Object> entry : subsGroupMap.entrySet()) {
					if (entry.getKey().toString().equalsIgnoreCase("@type"))
						continue;
					if (entry.getKey().toString().equalsIgnoreCase("@version"))
						continue;
					String subscribersData = entry.getValue().toString();
					subscribersData = subscribersData.replace("/", "?");
					HashMap<String, Object> subscribersDataMap = new Gson()
							.fromJson(subscribersData,
									new TypeToken<HashMap<String, Object>>() {
									}.getType());
					HashMap<String, Object> subscribersResultMap = new HashMap<String, Object>();
					subscribersDataMap.remove("@type");
					subscribersDataMap.remove("@version");
					
					for (Map.Entry<String, Object> contactEntry : subscribersDataMap
							.entrySet()) {
						if (contactEntry.getKey().toString()
								.equalsIgnoreCase("@type"))
							continue;
						if (contactEntry.getKey().toString()
								.equalsIgnoreCase("@version"))
							continue;
						String value = contactEntry.getValue().toString()
								.replace("?", "/");
						String accntName = "No data";
						// for account name
						String accountq = "select account_id from account_contact where contact_id ='"
								+ value + "'";
						accountItr = statement2.executeQuery(accountq);
						int accountid = 0;
						while (accountItr.next()) {
							accountid = accountItr.getInt(1);
						}
						if (accountid != 0) {
							String accountName = "select account_name from account where account_id ='"
									+ accountid + "'";
							accountNameItr = statement2
									.executeQuery(accountName);
							while (accountNameItr.next()) {
								accntName = (String) accountNameItr
										.getString(1);
								// replacing special characters which are breaking the hashmap conversion in UI
								accntName = accntName.replace("\'", "");
								accntName = accntName.replace("\"", "");
								accntName = accntName.replace(".", "");
								accntName = accntName.replace("\t", "");
								accntName = accntName.replace(",", "");
								accntName = accntName.replace(":", " ");
								accntName = accntName.replace("-", "");
								// accntName=accName.getAccount_name();
							}
						}
						subscribersResultMap.put("AccountName",
								accntName.trim());
						if (contactEntry.getKey().toString().contains("EMAIL")) {
							// ---------------- Get the EmailId for the specified contact
							String emailId = "No data";
							String contactQ = "select * from contact where contact_id='"
									+ value + "' and status=1";
							contactItr = statement2.executeQuery(contactQ);
							while (contactItr.next()) {
								emailId = contactItr
										.getString("Primary_Email_ID");
							}
							if (emailId == null) {
								emailId = "No data";
								subscribersResultMap.put(contactEntry.getKey()
										.toString().trim(), "NONE");
							}
							subscribersResultMap.put(contactEntry.getKey()
									.toString().trim(), value + "|" + emailId);
						} else if (contactEntry.getKey().toString()
								.contains("SMS")) {
							// ---------------- Get the SMS contact Number for the specified contact
							String mobileNum = "No data";
							String smsQ = "select * from contact where contact_id='"
									+ value + "' and status=1";
							smsItr = statement2.executeQuery(smsQ);
							while (smsItr.next()) {
								mobileNum = smsItr
										.getString("Primary_Moblie_Number");
							}

							if (mobileNum == null) {
								mobileNum = "No data";
								subscribersResultMap.put(contactEntry.getKey()
										.toString().trim(), "NONE");
							}
							subscribersResultMap
									.put(contactEntry.getKey().toString()
											.trim(), value + "|" + mobileNum);
						}

						//CR500.sn
						else if (contactEntry.getKey().toString()
								.contains("WHATSAPP")) {
							// ---------------- Get the SMS contact Number for the specified contact
							String mobileNum = "No data";
							String waQ = "select * from contact where contact_id='"
									+ value + "' and status=1";
							waItr = statement2.executeQuery(waQ);
							while (waItr.next()) {
								mobileNum = waItr
										.getString("Primary_Moblie_Number");
							}

							if (mobileNum == null) {
								mobileNum = "No data";
								subscribersResultMap.put(contactEntry.getKey()
										.toString().trim(), "NONE");
							}
							subscribersResultMap
									.put(contactEntry.getKey().toString()
											.trim(), value + "|" + mobileNum);
						}
						//CR500.en
					}

					String ns3Query = "", ns3select = "";
					ns3select = "select * from MAlertSubsriber_acountdata where AssetID='"
							+ vin + "'";
					ns3rs = statement3.executeQuery(ns3select);
					if (ns3rs.next()) {
						if (entry.getKey().toString()
								.equalsIgnoreCase("Subscriber1")) {
							ns3Query = "update MAlertSubsriber_acountdata set Subscriber1='\""
									+ subscribersResultMap
									+ "\"' where AssetID='" + vin + "'";
						}
						if (entry.getKey().toString()
								.equalsIgnoreCase("Subscriber2")) {
							ns3Query = "update MAlertSubsriber_acountdata set Subscriber2='\""
									+ subscribersResultMap
									+ "\"' where AssetID='" + vin + "'";
						}
						if (entry.getKey().toString()
								.equalsIgnoreCase("Subscriber3")) {
							ns3Query = "update MAlertSubsriber_acountdata set Subscriber3='\""
									+ subscribersResultMap
									+ "\"' where AssetID='" + vin + "'";
						}
					} else {
						if (entry.getKey().toString()
								.equalsIgnoreCase("Subscriber1")) {
							ns3Query = "insert into MAlertSubsriber_acountdata values('"
									+ vin
									+ "','\""
									+ subscribersResultMap
									+ "\"',null,null)";
						}
						if (entry.getKey().toString()
								.equalsIgnoreCase("Subscriber2")) {
							ns3Query = "insert into MAlertSubsriber_acountdata values('"
									+ vin
									+ "',null,'\""
									+ subscribersResultMap
									+ "\"',null)";
						}
						if (entry.getKey().toString()
								.equalsIgnoreCase("Subscriber3")) {
							ns3Query = "insert into MAlertSubsriber_acountdata values('"
									+ vin
									+ "',null,null,'\""
									+ subscribersResultMap + "\"')";
						}
					}
					statement3.addBatch(ns3Query);
					statement3.executeBatch();;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Exception occured in SubscriberReportServiceImpl :"
					+ e.getMessage());
		} finally {
			if (smsItr != null)
				try {
					smsItr.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
					fLogger.fatal("SQLException occured in closing resultset : SubscriberReportServiceImpl :"
							+ e1.getMessage());
				}
			if (ns3rs != null)
				try {
					ns3rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
					fLogger.fatal("SQLException occured in closing resultset : SubscriberReportServiceImpl :"
							+ e1.getMessage());
				}
			if (statement3 != null)
				try {
					statement3.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
					fLogger.fatal("SQLException occured in closing statement2 : SubscriberReportServiceImpl :"
							+ e1.getMessage());
				}
			if (statement2 != null)
				try {
					statement2.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
					fLogger.fatal("SQLException occured in closing statement2 : SubscriberReportServiceImpl :"
							+ e1.getMessage());
				}
		}
	}
}
