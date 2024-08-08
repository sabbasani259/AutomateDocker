package remote.wise.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import remote.wise.EAintegration.Qhandler.UpdateAlertClosersProducer;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;

//CR366 - 20221114 - Prasad - Closure of Service Alerts
public class UpdateServiceAlertsDAO {

	public String closeServiceAlerts(int month) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String moolStatus = null  ;
		String output = "No Alerts Are Open beyond 3 months!";


		String query = "select * from asset_event where  Event_Type_ID = 1 and event_closed_time is null and Event_Generated_Time< CURDATE() - INTERVAL "
				+ month + " month and event_id in (1,2,3)";

		String updateQuery = "update asset_event set Active_Status=0,Event_Closed_Time=CURDATE() ,PartitionKey=?,UpdateSource ='WISE' where event_id in (1,2,3) and Serial_Number =? and Active_Status=1";

		String updateQueryInServiceHistory = "insert into service_history  (serviceTicketNumber, serialNumber  , dealerId  ,"
				+ "serviceDate , serviceName  ,scheduleName  ,DBMS_partCode ,serviceScheduleId  , CMH ,call_type_id ) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		Timestamp currentTime = new Timestamp(new Date().getTime());
		String currentTimeInString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime);
		iLogger.info("query : " + query);
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		PreparedStatement preparedStatementSelectQuery = null;
		PreparedStatement psUpdateQuery = null;
		Statement stmt = null;
		Statement stmt1  = null;
		Statement stmt2 = null;
		Statement stmt3 = null;
		PreparedStatement preparedStatementUpdateQuery = null;
		try {
			 connection = connFactory.getConnection();
			if(connection == null){
				iLogger.info(" mysql connection" + connection);
				return "Fail Update the Records due to sql connection issues.!";}
			else {
				 preparedStatementSelectQuery = connection.prepareStatement(query);
				ResultSet rs = preparedStatementSelectQuery.executeQuery(query); 
				while (rs.next()) {

					String serialNumber = rs.getString("Serial_Number");
					String serviceTicketNumber = rs.getString("Asset_Event_ID");
					String serviceScheduleID = rs.getString("Service_Schedule_ID");
					String assetEventId = rs.getString("Asset_Event_ID");
					String dbms_partCode = rs.getString("Asset_Event_ID");
					String eventId = rs.getString("Event_ID");
					String eventGeneratedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.format(rs.getTimestamp("Event_Generated_Time"));
					String eventSeverity = rs.getString("Event_Severity");
					String closeLocation = rs.getString("EventClosedLocation");
					System.out.println(
							Integer.parseInt(eventGeneratedTime.split("\\-")[0] + eventGeneratedTime.split("\\-")[1]));

					if (null != serialNumber && "" != serialNumber) {
						 psUpdateQuery = connection.prepareStatement(updateQuery);
						psUpdateQuery.setInt(1,
								Integer.parseInt(currentTimeInString.split("\\-")[0] + currentTimeInString.split("\\-")[1])); // update
																															// PartitionKey
					//	psUpdateQuery.setString(2, assetEventId);
						psUpdateQuery.setString(2, serialNumber);
						int updateAssetEventResult =	psUpdateQuery.executeUpdate();
						 if(updateAssetEventResult >0) {
						String updateEventSnapShotHistory = "update asset_event_snapshot set AlertStatus = JSON_SET(AlertStatus, "
								+ "'$.\"" + eventId + "\"', \"0\") where SerialNumber = '" + serialNumber + "'";
						System.out.println(updateEventSnapShotHistory);
						 stmt = connection.createStatement();
					 	stmt.executeUpdate(updateEventSnapShotHistory);
					
						
						String getDealerId = "select Account_ID from  asset_owner_snapshot where Serial_Number = '"
								+ serialNumber + "' and Account_Type = 'Dealer'";
						System.out.println(getDealerId);
						 stmt1 = connection.createStatement();
						ResultSet rs1 = stmt1.executeQuery(getDealerId);

						while (rs1.next()) {
							String dealerId = rs1.getString("Account_ID");

							String getServiceAndScheduleNameQuery = "select * from  service_schedule where serviceScheduleId = " + serviceScheduleID;
						
							 stmt2 = connection.createStatement();
							ResultSet rs2 = stmt2.executeQuery(getServiceAndScheduleNameQuery);
							while (rs2.next()) {
								String serviceName = rs2.getString("serviceName");
								String scheduleName = rs2.getString("scheduleName");
								String callType = rs2.getString("call_type");
								
								String getCMHquery = "select TxnData -> '$.CMH' as CMH from  asset_monitoring_snapshot where Serial_Number = '" + serialNumber + "'" ;
							
								 stmt3 = connection.createStatement();
								ResultSet rs3 = stmt3.executeQuery(getCMHquery);

								while (rs3.next()) {
									String cmh = rs3.getString("CMH").replace("\"", "");
									iLogger.info("insert into Service is querry :: "  +updateQueryInServiceHistory);
									 preparedStatementUpdateQuery = connection
											.prepareStatement(updateQueryInServiceHistory);
									preparedStatementUpdateQuery.setString(1, serviceTicketNumber);
									preparedStatementUpdateQuery.setString(2, serialNumber);
									preparedStatementUpdateQuery.setString(3, dealerId);
									preparedStatementUpdateQuery.setString(4, currentTimeInString);
									preparedStatementUpdateQuery.setString(5, serviceName);
									preparedStatementUpdateQuery.setString(6, scheduleName);
									preparedStatementUpdateQuery.setString(7, dbms_partCode);
									preparedStatementUpdateQuery.setString(8, serviceScheduleID);
		     						preparedStatementUpdateQuery.setString(9, cmh);
									preparedStatementUpdateQuery.setString(10, callType);
								int insertResult =	preparedStatementUpdateQuery.executeUpdate();
								 
							        if (insertResult > 0) {
							        	iLogger.info("insert into Service is sucuss :: "  +insertResult);
							        } else {
							        	iLogger.info("insert into Service is FAIL ::"  +insertResult);
							        }
									
									 moolStatus = updateServiceAlertrsMoolDAReports(serialNumber,assetEventId, eventId, eventSeverity, eventGeneratedTime, currentTimeInString, closeLocation, cmh);
									if((moolStatus.equalsIgnoreCase("FAILURE") || moolStatus.contains("FAILURE")) && assetEventId != "0") { 
										iLogger.info( "UpdateServiceAlertsDAO:closeServiceAlerts:AssetEventID :" + assetEventId +
									  " : Inserting Record in MoolDA  is Failure  ");
									  
									 }
									 

								}
							}
						}
						iLogger.info("Update the Records In SQL ..!" + "AND MOOLDa Status::"+ moolStatus);
						output = "Update the Records In SQL ..!" + "AND MOOLDa Status::"+ moolStatus;
						
					}
					else{
						iLogger.info("No Alerts Are Open beyond 3 months!" );
						output= "No Alerts Are Open beyond 3 months!";
					}
					}
				}

				
			}
			
			return output;

		} catch (SQLException se) {
			se.printStackTrace();
			fLogger.fatal("SQLException "  + se.getMessage());
			return se.getMessage();

		} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Exception e " + e.getMessage());
			return e.getMessage();
		}
		finally
		{
			 try
			 { 	
				 if (preparedStatementSelectQuery != null) 
					 preparedStatementSelectQuery.close(); 
				 				 
				 if (psUpdateQuery != null) 
					 psUpdateQuery.close(); 

				 if (stmt != null) 
					 stmt.close(); 
				 				 
				 if (stmt1 != null) 
					 stmt1.close(); 
				 if (stmt2 != null) 
					 stmt2.close(); 

				 if (stmt3 != null) 
					 stmt3.close(); 
				 				 
				 if (preparedStatementUpdateQuery != null) 
					 preparedStatementUpdateQuery.close(); 
				 
				 if(connection!=null)
					 connection.close();
			 
			 }
			 
			 catch(SQLException e)
			 {
				fLogger.fatal("SQLException :"+e);
			 }
		}

	}

	public String updateServiceAlertrsMoolDAReports(String serialNumber, String assetEventId, String eventId,
			String eventSeverity, String eventGeneratedTime, String serviceDate, String alertClosureLocation,
			String alertClosureCMH) {

		OutputStream os = null;
		String output = "SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		BufferedReader br = null;
		String result = null;
		
		iLogger.info("UpdateServiceAlertsDAO:updateServiceAlertrsMoolDAReports::Input for closing MoolDA reports: serialNumber: "
				+ serialNumber + " assetEventId: " + assetEventId + "  eventID: " + eventId + " eventSeverity: "
				+ eventSeverity + " eventGeneratedTime: " + eventGeneratedTime + " eventClosureTime: " + serviceDate
				+ " alertClosureLocation: " + alertClosureLocation + "alertClosureCMH:" + alertClosureCMH + "");

		// CRCR337.sn
		String connIP = null;
		String connPort = null;
		Properties prop = null;
		try {
			prop = CommonUtil.getDepEnvProperties();
			connIP = prop.getProperty("MDA_ServerIP");
			connPort = prop.getProperty("MDA_ServerPort");
			iLogger.info("UpdateServiceAlertsDAO: MDA_ServerIP:" + connIP + " :: MDA_ServerPort:" + connPort);
		} catch (Exception e) {
			fLogger.fatal("UpdateServiceAlertsDAO: updateServiceAlertrsMoolDAReports : "
					+ "Exception in getting Server Details for MDA Layer from properties file: " + e);
		}

		try {

			JSONObject json = new JSONObject();
			 json.put("uniqueIdentifier",assetEventId);

			json.put("assetID", serialNumber);
			json.put("eventSeverity", eventSeverity);
			json.put("eventID", eventId);
			json.put("eventGenerationTime", eventGeneratedTime);
			json.put("eventClosureTime", serviceDate + " 00:00:00");
			json.put("alertClosureCMH", alertClosureCMH);
			json.put("alertClosureLocation", alertClosureLocation);
			json.put("loginID", "Dummy");
			String request = json.toString();
			
//			String URL=	 "http://10.210.196.206:20000/MoolDAReports/AlertClosureService/triggerAlertClosure";//PROD
			
			String URL = "http://" + connIP + ":" + connPort + "/MoolDAReports/AlertClosureService/triggerAlertClosure";// PROD
//							10.210.196.206:26030																							// //CR337.n
			// String URL="http://10.106.68.9:8112/MoolDAReports/AlertClosureService/triggerAlertClosure";//SIT
			iLogger.info("MoolDAReports UrL:" + URL);
			URL url = new URL(URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			os = (OutputStream) connection.getOutputStream();
			os.write(request.getBytes());
			os.flush();
			iLogger.info("UpdateServiceAlertsDAO:updateServiceAlertrsMoolDAReports:AssetEventID::---> HTTP code from MoolDA :"
					+ connection.getResponseCode());
			if (connection.getResponseCode() != 200 ) {
				HashMap<String, String> alertPoducerDataPayloadMap = new HashMap<String, String>();
				alertPoducerDataPayloadMap.put("assetID", serialNumber);
				alertPoducerDataPayloadMap.put("eventSeverity", eventSeverity);
				alertPoducerDataPayloadMap.put("eventID", eventId);
				alertPoducerDataPayloadMap.put("eventGenerationTime", eventGeneratedTime);
				alertPoducerDataPayloadMap.put("eventClosureTime", serviceDate + " 00:00:00");
				alertPoducerDataPayloadMap.put("alertClosureCMH", alertClosureCMH);
				alertPoducerDataPayloadMap.put("alertClosureLocation", alertClosureLocation);
				alertPoducerDataPayloadMap.put("uniqueIdentifier", assetEventId);
				alertPoducerDataPayloadMap.put("loginID", "Dummy");
				
				
				iLogger.info("Calling UpdateAlertClosersProducer for publishing the data to kafka topic for  -------> START");
				
				new UpdateAlertClosersProducer(alertPoducerDataPayloadMap);
				iLogger.info("Calling UpdateAlertClosersProducer for publishing the data to kafka topic for -------> END");

				output = "FAILURE";
				iLogger.info(
						"MDAReports report status: FAILURE for updateServiceAlertrsMoolDAReports Service Report ::Response Code:"
								+ connection.getResponseCode());
				fLogger.fatal("UpdateServiceAlertsDAO:updateServiceAlertrsMoolDAReports:AssetEventID :" + assetEventId
						+ " Failed : HTTP error code :  Response: " + connection.getResponseCode()
						+ " :Exception caused because of no response from MOOLDA URL:" + url + " Request:" + request
						+ " output:" + output);
			
				return output;
			}
			iLogger.info("MDAReports report status: SUCCESS for updateServiceAlertrsMoolDAReports Service Report ::Response Code:"
					+ connection.getResponseCode());

			br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
			while ((result = br.readLine()) != null) {
				output = result.toUpperCase();
			}
			iLogger.info("updateServiceAlertrsMoolDAReports:updateServiceAlertrsMoolDAReports:AssetEventID :" + assetEventId
					+ " MoolDA Report response :" + output);

		} catch (Exception e) {
			HashMap<String, String> alertPoducerDataPayloadMap = new HashMap<String, String>();
			alertPoducerDataPayloadMap.put("assetID", serialNumber);
			alertPoducerDataPayloadMap.put("eventSeverity", eventSeverity);
			alertPoducerDataPayloadMap.put("eventID", eventId);
			alertPoducerDataPayloadMap.put("eventGenerationTime", eventGeneratedTime);
			alertPoducerDataPayloadMap.put("eventClosureTime", serviceDate + " 00:00:00");
			alertPoducerDataPayloadMap.put("alertClosureCMH", alertClosureCMH);
			alertPoducerDataPayloadMap.put("alertClosureLocation", alertClosureLocation);
			alertPoducerDataPayloadMap.put("uniqueIdentifier", assetEventId);
			alertPoducerDataPayloadMap.put("loginID", "Dummy");
			
			
			iLogger.info("Calling UpdateAlertClosersProducer for publishing the data to kafka topic for  -------> START");
			
			new UpdateAlertClosersProducer(alertPoducerDataPayloadMap);
			iLogger.info("Calling UpdateAlertClosersProducer for publishing the data to kafka topic for -------> END");

			output = "FAILURE";
			fLogger.fatal("UpdateServiceAlertsDAO:updateServiceAlertrsMoolDAReports:AssetEventID :" + assetEventId + ":Exception"
					+ e.getMessage());
			return output;
		} finally {
			if (os != null)
				try {
					os.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			if (br != null)
				try {
					br.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

		}
		return output;
	}

}
