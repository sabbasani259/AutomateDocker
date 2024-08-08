/*
 * JCB6336 : 20230214 : Dhiraj k : Fault handling for sending data to MoolDA - ownership and personality update
 * ME100005235 : 20230306 : Dhiraj K : WS to send the renewal date to MoolDA 
 */
package com.wipro.mda;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.ListToStringConversion;

public class ReprocessDAIngestionImpl {
	//JCB6336.sn
	public String reprocessMoolDAFaultDetails(String rejectionPoint){

		String result = "SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String connIP, connPort, faultData;
		connIP = connPort = faultData = null;
		ConnectMySQL connFactory = new ConnectMySQL();
		int faultID = 0;
		String mdaQ = "SELECT * from MOOLDA_FaultDetails where RejectionPoint='" +rejectionPoint+"'";
		try(Connection conn = connFactory.getConnection();
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(mdaQ)) {
			try{
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream
						("remote/wise/resource/properties/configuration.properties"));
				connIP = prop.getProperty("MDA_ServerIP");
				connPort = prop.getProperty("MDA_ServerPort");
			}catch(Exception e){
				fLogger.fatal("ReprocessDAIngestionImpl:reprocessMoolDAFaultDetails: " +
						"Exception in getting Server Details for MDA Layer from properties file: "+e.getMessage());
				result = "FAILURE";
				throw new Exception("Exception in reading details from properties file");
			}

			List<Integer> toBeDeletedList = new ArrayList<>();
			
			while(rs.next()){
				faultID = rs.getInt("FaultID");
				faultData = rs.getString("ProfileData");
				if(faultData != null){
					switch (rejectionPoint) {
					//ME100005235.sn
					case "AssetSubscriptionDetails":
						try{
							URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/RenewalDetailsService/" +
									"persistRenewalDetails?inputPayloadMap="+URLEncoder.encode(faultData, "UTF-8"));
							HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
							httpConn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
							httpConn.setRequestProperty("Accept", "text/plain");
							httpConn.setRequestMethod("GET");
							if (httpConn.getResponseCode() != 200 && httpConn.getResponseCode() != 204) {
								iLogger.info("MDAReports report status: FAILURE for reprocessMoolDAFaultDetails for rejectionPoint: "+rejectionPoint+" ::Response Code:"+httpConn.getResponseCode());
								throw new RuntimeException("Failed : HTTP error code : "
										+ httpConn.getResponseCode());
							}
							iLogger.info("MDAReports report status: SUCCESS for reprocessMoolDAFaultDetails rejectionPoint: "+rejectionPoint+"  ::Response Code:"+httpConn.getResponseCode());
							BufferedReader br = new BufferedReader(new InputStreamReader(
									(httpConn.getInputStream())));
							String outputResponse = null;
							while((outputResponse = br.readLine()) != null){
								iLogger.info("ReprocessDAIngestionImpl:reprocessMoolDAFaultDetails:" +
										" Response from {/MoolDA/assetProfile/} is: "+outputResponse);
								if(outputResponse.equalsIgnoreCase("SUCCESS")){
									toBeDeletedList.add(faultID);
								}
							}
						}catch(Exception e){
							fLogger.fatal("ReprocessDAIngestionImpl:reprocessProfileDetails:" +
									" Exception in invoking the {/MoolDA/assetProfile/} URL: "+ e.getMessage());
							result = "FAILURE";
							throw new Exception("REST URL not available for invoking AssetProfileRollOffDetails from WISE to MDA");
						}
						break;
					//ME100005235.en
					case "AssetProfileRollOffDetails":
						try{
							URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/assetProfile/" +
									"inputPayloadDetails?inputPayloadMap="+URLEncoder.encode(faultData, "UTF-8"));
							HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
							httpConn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
							httpConn.setRequestProperty("Accept", "text/plain");
							httpConn.setRequestMethod("GET");
							if (httpConn.getResponseCode() != 200 && httpConn.getResponseCode() != 204) {
								iLogger.info("MDAReports report status: FAILURE for reprocessOwnerDetails for rejectionPoint: "+rejectionPoint+" ::Response Code:"+httpConn.getResponseCode());
								throw new RuntimeException("Failed : HTTP error code : "
										+ httpConn.getResponseCode());
							}
							iLogger.info("MDAReports report status: SUCCESS for reprocessOwnerDetails rejectionPoint: "+rejectionPoint+"  ::Response Code:"+httpConn.getResponseCode());
							BufferedReader br = new BufferedReader(new InputStreamReader(
									(httpConn.getInputStream())));
							String outputResponse = null;
							while((outputResponse = br.readLine()) != null){
								iLogger.info("ReprocessDAIngestionImpl:reprocessOwnerDetails:" +
										" Response from {/MoolDA/assetProfile/} is: "+outputResponse);
								if(outputResponse.equalsIgnoreCase("SUCCESS")){
									toBeDeletedList.add(faultID);
								}
							}
						}catch(Exception e){
							fLogger.fatal("ReprocessDAIngestionImpl:reprocessOwnerDetails:" +
									" Exception in invoking the {/MoolDA/assetProfile/} URL: "+ e.getMessage());
							result = "FAILURE";
							throw new Exception("REST URL not available for invoking AssetProfileRollOffDetails from WISE to MDA");
						}
						break;
					case "AssetOwnershipDetails":
						try{
							URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/OwnershipDetails/" +
									"publishSaleDetails?topicName=MDAOwnership&inputMsg="+URLEncoder.encode(faultData, "UTF-8"));
							HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
							httpConn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
							httpConn.setRequestProperty("Accept", "text/plain");
							httpConn.setRequestMethod("GET");
							if (httpConn.getResponseCode() != 200 && httpConn.getResponseCode() != 204) {
								throw new RuntimeException("Failed : HTTP error code : "
										+ httpConn.getResponseCode());
							}
							BufferedReader br = new BufferedReader(new InputStreamReader(
									(httpConn.getInputStream())));
							String outputResponse = null;
							while((outputResponse = br.readLine()) != null){
								iLogger.info("ReprocessDAIngestionImpl:reprocessOwnerDetails:" +
										" Response from {/MoolDA/OwnershipDetails/}is: "+outputResponse);
								if(outputResponse.equalsIgnoreCase("SUCCESS")){
									toBeDeletedList.add(faultID);
								}
							}
						}catch(Exception e){
							fLogger.fatal("ReprocessDAIngestionImpl:reprocessOwnerDetails:" +
									" Exception in invoking the {/MoolDA/OwnershipDetails/} URL: "+ e.getMessage());
							result = "FAILURE";
							throw new Exception("REST URL not available for invoking OwnershipDetails from WISE to MDA");
						}
						break;
					default:
						iLogger.info("ReprocessDAIngestionImpl:reprocessMoolDAFaultDetails:Invalid rejectionPoint");
						result = "FAILURE";
						break;
					}
				}
			}
			ListToStringConversion util = new ListToStringConversion();
			String deleteQ = "DELETE FROM MOOLDA_FaultDetails WHERE FaultID in (" + util.getIntegerListString(toBeDeletedList) + ")" ;
			iLogger.info("Delete Query" + deleteQ);
			st.execute(deleteQ);
		}catch (Exception e) {
			fLogger.fatal("ReprocessDAIngestionImpl:reprocessMoolDAFaultDetails:" +
					" Exception in invoking the URL: "+ e.getMessage());
			result = "FAILURE";
		}
		return result;
	}
	//JCB6336.en
	//JCB6336.so
	/*
	 @SuppressWarnings("rawtypes")
	public String reprocessOwnerDetails(String rejectionPoint){

		String result = "SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String connIP, connPort, profileData;
		connIP = connPort = profileData = null;
		Session session = null;
		int faultID = 0;
		try{
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			String mdaQ = ("from MOOLDAFaultDetailsEntity where rejectionPoint='" +rejectionPoint+"'");
			Query query = session.createQuery(mdaQ);
			Iterator iterator = query.list().iterator();
			MOOLDAFaultDetailsEntity mdaEntity = null;
			try{
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream
						("remote/wise/resource/properties/configuration.properties"));
				connIP = prop.getProperty("MDA_ServerIP");
				connPort = prop.getProperty("MDA_ServerPort");
			}catch(Exception e){
				fLogger.fatal("ReprocessDAIngestionImpl:reprocessOwnerDetails: " +
						"Exception in getting Server Details for MDA Layer from properties file: "+e.getMessage());
				result = "FAILURE";
				throw new Exception("Exception in reading details from properties file");
			}
			while(iterator.hasNext()){
				mdaEntity = (MOOLDAFaultDetailsEntity) iterator.next();
				faultID = mdaEntity.getFaultID();
				profileData = mdaEntity.getProfileData();
				if(profileData != null){
					if(rejectionPoint.equalsIgnoreCase("AssetProfileRollOffDetails")){
						try{
							URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/assetProfile/" +
									"inputPayloadDetails?inputPayloadMap="+URLEncoder.encode(profileData, "UTF-8"));
							HttpURLConnection conn = (HttpURLConnection) url.openConnection();
							conn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
							conn.setRequestProperty("Accept", "text/plain");
							conn.setRequestMethod("GET");
							if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) {
								iLogger.info("MDAReports report status: FAILURE for reprocessOwnerDetails for rejectionPoint: "+rejectionPoint+" ::Response Code:"+conn.getResponseCode());
								throw new RuntimeException("Failed : HTTP error code : "
										+ conn.getResponseCode());
							}
							iLogger.info("MDAReports report status: SUCCESS for reprocessOwnerDetails rejectionPoint: "+rejectionPoint+"  ::Response Code:"+conn.getResponseCode());
							BufferedReader br = new BufferedReader(new InputStreamReader(
									(conn.getInputStream())));
							String outputResponse = null;
							while((outputResponse = br.readLine()) != null){
								iLogger.info("ReprocessDAIngestionImpl:reprocessOwnerDetails:" +
										" Response from {/MoolDA/assetProfile/}is: "+outputResponse);
								if(outputResponse.equalsIgnoreCase("SUCCESS")){
									mdaEntity =(MOOLDAFaultDetailsEntity) session.load(MOOLDAFaultDetailsEntity.class, faultID);
									session.delete(mdaEntity);
								}
							}
						}catch(Exception e){
							fLogger.fatal("ReprocessDAIngestionImpl:reprocessOwnerDetails:" +
									" Exception in invoking the {/MoolDA/assetProfile/} URL: "+ e.getMessage());
							result = "FAILURE";
							throw new Exception("REST URL not available for invoking AssetProfileRollOffDetails from WISE to MDA");
						}
					}else{
						try{
							URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/OwnershipDetails/" +
									"publishSaleDetails?topicName=MDAOwnership&inputMsg="+URLEncoder.encode(profileData, "UTF-8"));
							HttpURLConnection conn = (HttpURLConnection) url.openConnection();
							conn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
							conn.setRequestProperty("Accept", "text/plain");
							conn.setRequestMethod("GET");
							if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) {
								throw new RuntimeException("Failed : HTTP error code : "
										+ conn.getResponseCode());
							}
							BufferedReader br = new BufferedReader(new InputStreamReader(
									(conn.getInputStream())));
							String outputResponse = null;
							while((outputResponse = br.readLine()) != null){
								iLogger.info("ReprocessDAIngestionImpl:reprocessOwnerDetails:" +
										" Response from {/MoolDA/OwnershipDetails/}is: "+outputResponse);
								if(outputResponse.equalsIgnoreCase("SUCCESS")){
									mdaEntity =(MOOLDAFaultDetailsEntity) session.load(MOOLDAFaultDetailsEntity.class, faultID);
									session.delete(mdaEntity);
								}
							}
						}catch(Exception e){
							fLogger.fatal("ReprocessDAIngestionImpl:reprocessOwnerDetails:" +
									" Exception in invoking the {/MoolDA/OwnershipDetails/} URL: "+ e.getMessage());
							result = "FAILURE";
							throw new Exception("REST URL not available for invoking OwnershipDetails from WISE to MDA");
						}
					}
				}
			}
		}catch (Exception e) {
			fLogger.fatal("ReprocessDAIngestionImpl:reprocessOwnerDetails:" +
					" Exception in invoking the URL: "+ e.getMessage());
			result = "FAILURE";
		}finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}if(session.isOpen()){
				session.flush();
				session.close();
			}
		}
		return result;
	}
	 */
	//JCB6336.eo
}
