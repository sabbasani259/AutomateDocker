package com.wipro.mda;

/*
 *  ME100005235 : 20230306 : Dhiraj K : WS to send the renewal date to MoolDA 
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class AssetSubscriptionDetails {

	@SuppressWarnings("unchecked")
	public void setAssetSubscriptionDetails(String serialNumber) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String formattedMachineNumber = null;
		String formattedSerialNumber = null;
		String finalJsonString = null;
		String subsStartDate = null;
		String subsEndDate = null;
		String connIP = null;
		String connPort = null;
		
		HashMap<String, String> finalpayLoadMap = new HashMap<String, String>();
		ConnectMySQL connFactory = new ConnectMySQL();

		if(serialNumber != null){
			formattedMachineNumber = serialNumber.substring(serialNumber.length()-7);
		}
		try(Connection conn = connFactory.getConnection()){
			String getSerialNumberQ = "SELECT serial_number from asset where machine_number='"+formattedMachineNumber+"' and status = 1";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(getSerialNumberQ);
			while(rs.next()){
				formattedSerialNumber = rs.getString("serial_number");
				if(formattedSerialNumber!=null){
					finalpayLoadMap.put("AssetID",formattedSerialNumber);
				}
			}
			String query = "SELECT SubsStartDate, SubsEndDate FROM asset_renewal_data WHERE serial_number='"+formattedSerialNumber+"' ORDER BY Updated_On DESC LIMIT 1";
			Statement st1 = conn.createStatement();
			ResultSet rs1 = st1.executeQuery(query);
			JSONObject jsonObj = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			while(rs1.next()){
				subsStartDate = sdf.format(rs1.getDate("SubsStartDate"));
				subsEndDate = sdf.format(rs1.getDate("SubsEndDate"));
			}
			finalpayLoadMap.put("SubsStartDate",subsStartDate);
			finalpayLoadMap.put("SubsEndDate",subsEndDate);
			
			jsonObj = new JSONObject();
			jsonObj.putAll(finalpayLoadMap);
			finalJsonString = jsonObj.toString();
			iLogger.info("AssetSubscriptionDetails:setAssetSubscriptionDetails:finalJsonString:"+finalJsonString);
			//Invoking the REST URL from WISE to MOOL DA.
			try{
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream
						("remote/wise/resource/properties/configuration.properties"));
				connIP = prop.getProperty("MDA_ServerIP");
				connPort = prop.getProperty("MDA_ServerPort");

			}catch(Exception e){
				fLogger.fatal("AssetSubscriptionDetails:setAssetSubscriptionDetails:" +
						"Exception in getting Server Details for MDA Layer from properties file: " +e);
				throw new Exception("Error reading from properties file");
			}try{
				URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/RenewalDetailsService/" +
						"persistRenewalDetails?inputPayloadMap="+URLEncoder.encode(finalJsonString, "UTF-8"));
				HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
				httpConn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
				httpConn.setRequestProperty("Accept", "text/plain");
				httpConn.setRequestMethod("GET");
				if (httpConn.getResponseCode() != 200 && httpConn.getResponseCode() != 204) {
					iLogger.info("MDAReports report status:FAILURE for AssetSubscriptionDetails Report VIN:"+formattedMachineNumber+" ::Response Code:"+httpConn.getResponseCode());//20220921.n.Additional log added
					throw new Exception("Failed : HTTP error code : "
							+ httpConn.getResponseCode());
				}
				iLogger.info("MDAReports report status SUCCESS for AssetSubscriptionDetails Report VIN:"+formattedMachineNumber+" ::Response Code:"+httpConn.getResponseCode());//20220921.n.Additional log added
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(httpConn.getInputStream())));
				String outputResponse = null;
				while((outputResponse = br.readLine()) != null){
					iLogger.info("AssetSubscriptionDetails:setAssetSubscriptionDetails:" +
							" Response from {/MoolDA/RenewalDetailsService/RenewalDetailsService}is: "+outputResponse+" for VIN:"+formattedSerialNumber);
				}
			}catch(Exception e1){
				fLogger.fatal("AssetSubscriptionDetails:setAssetSubscriptionDetails:" +
						" Exception in invoking the { /MoolDA/RenewalDetailsService/RenewalDetailsService} URL: "+ e1.getMessage()+" for VIN:"+formattedSerialNumber);
				throw new Exception("REST URL not available.");
			}
		}catch (Exception e2) {
			e2.printStackTrace();
			fLogger.fatal("AssetSubscriptionDetails:setAssetSubscriptionDetails:Exception Cause: " +e2.getMessage());
			String rejectionPoint = "AssetSubscriptionDetails";
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String createdTimeStamp = dateFormat.format(date);
			try(Connection conn = connFactory.getConnection();
					Statement st = conn.createStatement()){
				String insertQ = "INSERT INTO MOOLDA_FaultDetails (SerialNumber, ProfileData, RejectionPoint, Created_TimeStamp)"
						+ " VALUES ('" + serialNumber + "','" + finalJsonString + "','" + rejectionPoint + "','" + createdTimeStamp +"')";
				st.execute(insertQ);
				iLogger.info("AssetSubscriptionDetails:setAssetSubscriptionDetails:: Succesfully persisted" +
						" the MOOLDA_FaultDetails for assetId: "+formattedSerialNumber);
			}catch(Exception e3){
				fLogger.fatal("AssetSubscriptionDetails:setAssetSubscriptionDetails:" +
						" Exception in persisting the faults details in " +
						"MOOLDA_FaultDetails table "+ e3.getMessage()+" for VIN:"+formattedSerialNumber);
			}
		}
	}
}
