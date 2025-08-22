/*
 * JCB6336 : 20230214 : Dhiraj k : Fault handling for sending data to MoolDA - ownership and personality update
 * CR415 : 20230626 : Dhiraj k : To send the Personality data from the interface call to  MDA
 * 
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

public class AssetProfileDetails {

	//JCB6336.sn
	@SuppressWarnings({ "unchecked" })
	public void setAssetProfileRollOffDetails(String serialNumber, String engineNumber,String machineCategory,String rollOffDate){
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String simNo, iccidNo, finalJsonString, connIP, connPort;
		simNo = iccidNo = finalJsonString = connIP = connPort = null;
		ConnectMySQL connFactory = new ConnectMySQL();
		
		String acuQ = ("select SIM_No, ICCID from asset_control_unit where" +
				" Serial_Number='"+serialNumber+"'");
		try(Connection conn = connFactory.getConnection();
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(acuQ)){
			HashMap<String,String> finalProfileDetailsMap = new HashMap<String,String>();
			finalProfileDetailsMap.put("AssetID", serialNumber);
			JSONObject jsonObj = null;
			while(rs.next()){
				simNo = rs.getString("SIM_No");
				iccidNo = rs.getString("ICCID");
				if(simNo != null)
					finalProfileDetailsMap.put("SIM", simNo);
					finalProfileDetailsMap.put("RenewalFlag", "1");
				if(iccidNo != null)
					finalProfileDetailsMap.put("ICCID_No", iccidNo);
			}
			if(engineNumber != null)
				finalProfileDetailsMap.put("EngineNumber", engineNumber);
			//CR512.n
			if(machineCategory!=null)
				finalProfileDetailsMap.put("MachineCategory", machineCategory);
			if(rollOffDate!=null)
				finalProfileDetailsMap.put("RollOffDate", rollOffDate);
			jsonObj = new JSONObject();
			jsonObj.putAll(finalProfileDetailsMap);
			finalJsonString = jsonObj.toString();
			iLogger.info("AssetProfileDetails:setAssetProfileRollOffDetails:"+finalJsonString);
			//Invoking the REST URL from WISE to MOOL DA.
			try{
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream
						("remote/wise/resource/properties/configuration.properties"));
				connIP = prop.getProperty("MDA_ServerIP");
				connPort = prop.getProperty("MDA_ServerPort");
			}catch(Exception e){
				fLogger.fatal("AssetProfileDetails:setAssetProfileRollOffDetails: " +
						"Exception in getting Server Details for MDA Layer from properties file: " +e);
				throw new Exception("Error reading from properties file");
			}try{
				URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/assetProfile/" +
						"inputPayloadDetails?inputPayloadMap="+URLEncoder.encode(finalJsonString, "UTF-8"));
				HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
				httpConn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
				httpConn.setRequestProperty("Accept", "text/plain");
				httpConn.setRequestMethod("GET");
				if (httpConn.getResponseCode() != 200 && httpConn.getResponseCode() != 204) {
					iLogger.info("MDAReports report status: FAILURE for setAssetProfileRollOffDetails Report VIN:"+serialNumber+" ::Response Code:"+httpConn.getResponseCode());
					throw new Exception("Failed : HTTP error code : "
							+ httpConn.getResponseCode());
				}
				iLogger.info("MDAReports report status: SUCCESS for setAssetProfileRollOffDetails Report VIN:"+serialNumber+" ::Response Code:"+httpConn.getResponseCode());
				
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(httpConn.getInputStream())));
				String outputResponse = null;
				while((outputResponse = br.readLine()) != null){
					iLogger.info("AssetProfileDetails:setAssetProfileRollOffDetails:" +
							" Response from {/MoolDA/assetProfile/}is: "+outputResponse+" for VIN:"+serialNumber);
				}
			}catch(Exception e){
				fLogger.fatal("AssetProfileDetails:setAssetProfileRollOffDetails:" +
						" Exception in invoking the {/MoolDA/assetProfile/} URL: "+ e.getMessage()+" for VIN:"+serialNumber);
				throw new Exception("REST URL not available");
			}
		}catch(Exception e){
			fLogger.fatal("AssetProfileDetails:setAssetProfileRollOffDetails:Exception Cause: " +e.getMessage());
			String rejectionPoint = "AssetProfileRollOffDetails";
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String createdTimeStamp = dateFormat.format(date);
			try(Connection conn = connFactory.getConnection();
					Statement st = conn.createStatement()){
				String insertQ = "INSERT INTO MOOLDA_FaultDetails (SerialNumber, ProfileData, RejectionPoint, Created_TimeStamp)"
						+ " VALUES ('" + serialNumber + "','" + finalJsonString + "','" + rejectionPoint + "','" + createdTimeStamp +"')";
				st.execute(insertQ);
				iLogger.info("AssetProfileDetails:setAssetProfileRollOffDetails: Succesfully persisted" +
						" the faultDetails for assetId: "+serialNumber);
			}catch(Exception e1){
				fLogger.fatal("AssetProfileDetails:setAssetProfileRollOffDetails:" +
						" Exception in persisting the faults details in " +
						"AssetProfileFaultDetails table "+ e1.getMessage());
			}
		}
	}

	@SuppressWarnings({ "unchecked" })
	public void setAssetProfilePersonalityDetails(String serialNumber,
			String assetGroupCode, String assetTypeCode) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String profileName, modelName, finalJsonString, connIP, connPort, formattedSerialNumber, formattedMachineNumber, modelMasterName, modelMasterCode;
		profileName = modelName = finalJsonString = connIP = connPort = formattedSerialNumber = formattedMachineNumber=modelMasterName=modelMasterCode = null;
		HashMap<String,String> finalProfileDetailsMap = new HashMap<String,String>();
		if(serialNumber != null){
			formattedMachineNumber = serialNumber.substring(serialNumber.length()-7);
		}
		ConnectMySQL connFactory = new ConnectMySQL();
		try(Connection conn = connFactory.getConnection();
				Statement st = conn.createStatement();
				Statement st1 = conn.createStatement();
				Statement st2 = conn.createStatement()){
			//Step 1 : Fetching SerialNumber from asset table based on Machine number
			String getSerialNumberQuery = "SELECT * from asset where Machine_Number='"+formattedMachineNumber+"'";
			ResultSet rs = st.executeQuery(getSerialNumberQuery);
			while(rs.next()){
				formattedSerialNumber = rs.getString("Serial_Number");
				if(formattedSerialNumber!=null){
					finalProfileDetailsMap.put("AssetID",formattedSerialNumber);
				}
			}
			//Step 2 : Fetching Profile Name from asset_group based on the assetGroupCode
			String profileNameQuery = "select Asseet_Group_Name from asset_group where " +
					"asset_group_id in (select asset_grp_id from asset_group_profile where" +
					" asset_grp_code='"+assetGroupCode+"')";
			ResultSet rs1 = st1.executeQuery(profileNameQuery);
			while(rs1.next()){
				profileName =  rs1.getString("Asseet_Group_Name");
				if(profileName !=null)
					finalProfileDetailsMap.put("ProfileName", profileName);
			}
			if(assetGroupCode!=null)
				finalProfileDetailsMap.put("ProfileCode", assetGroupCode);
			//Step 3 : Fetching Model Name from asset_type based on the assetTypeCode
			//String modelNameQuery = "select asset_type_name from asset_type" +//CR415.o
			String modelNameQuery = "select asset_type_name , asset_type_group_name, Asset_Type_Master_Code from asset_type" +//CR415.n
					" where Asset_Type_Code='"+assetTypeCode+"'";
			ResultSet rs2 = st2.executeQuery(modelNameQuery);
			while(rs2.next()){
				modelName = rs2.getString("asset_type_name");
				if(modelName != null)
					finalProfileDetailsMap.put("ModelName",modelName);
				//CR415.sn
				modelMasterName =  rs2.getString("asset_type_group_name");
				if(modelMasterName !=null)
					finalProfileDetailsMap.put("ModelMasterName", modelMasterName);
				modelMasterCode =  rs2.getString("Asset_Type_Master_Code");
				if(modelMasterCode !=null)
					finalProfileDetailsMap.put("ModelMasterCode", modelMasterCode);
				//CR415.en
			}
			if(assetTypeCode!=null)
				finalProfileDetailsMap.put("ModelCode", assetTypeCode);
			JSONObject jsonObj = new JSONObject();
			jsonObj.putAll(finalProfileDetailsMap);
			finalJsonString = jsonObj.toString();
			iLogger.info("AssetProfileDetails:setAssetProfilePersonalityDetails:"+finalJsonString);
			//Invoking the REST URL from WISE to MOOL DA.
			try{
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream
						("remote/wise/resource/properties/configuration.properties"));
				connIP = prop.getProperty("MDA_ServerIP");
				connPort = prop.getProperty("MDA_ServerPort");
			}catch(Exception e){
				fLogger.fatal("AssetProfileDetails:setAssetProfilePersonalityDetails: " +
						"Exception in getting Server Details for MDA Layer from properties file: " +e+" for VIN:"+formattedSerialNumber);
				
				throw new Exception("Error reading from properties file");
			}try{
				URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/assetProfile/" +
						"inputPayloadDetails?inputPayloadMap="+URLEncoder.encode(finalJsonString, "UTF-8"));
				HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
				httpConn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
				httpConn.setRequestProperty("Accept", "text/plain");
				httpConn.setRequestMethod("GET");
				if (httpConn.getResponseCode() != 200 && httpConn.getResponseCode() != 204) {
					iLogger.info("MDAReports report status: FAILURE for setAssetProfilePersonalityDetails Report VIN:"+serialNumber+" ::Response Code:"+httpConn.getResponseCode());
					
					throw new Exception("Failed : HTTP error code : "
							+ httpConn.getResponseCode());
				}
				iLogger.info("MDAReports report status: SUCCESS for setAssetProfilePersonalityDetails Report VIN:"+serialNumber+" ::Response Code:"+httpConn.getResponseCode());
				
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(httpConn.getInputStream())));
				String outputResponse = null;
				while((outputResponse = br.readLine()) != null){
					iLogger.info("AssetProfileDetails:setAssetProfileRollOffDetails:" +
							" Response from {/MoolDA/assetProfile/}is: "+outputResponse+" for VIN:"+formattedSerialNumber);
				}
			}catch(Exception e){
				fLogger.fatal("AssetProfileDetails:setAssetProfilePersonalityDetails: " +
						" Exception in invoking the {/MoolDA/assetProfile/} URL: "+ e.getMessage()+" for VIN:"+formattedSerialNumber);
				throw new Exception("REST URL not available");
			}
		}catch (Exception e) {
			fLogger.fatal("AssetProfileDetails:setAssetProfileRollOffDetails:Exception Cause: " +e.getMessage());
			String rejectionPoint = "AssetProfileRollOffDetails";
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String createdTimeStamp = dateFormat.format(date);
			try(Connection conn = connFactory.getConnection();
						Statement st = conn.createStatement()){
					String insertQ = "INSERT INTO MOOLDA_FaultDetails (SerialNumber, ProfileData, RejectionPoint, Created_TimeStamp)"
							+ " VALUES ('" + serialNumber + "','" + finalJsonString + "','" + rejectionPoint + "','" + createdTimeStamp +"')";
					st.execute(insertQ);
					iLogger.info("AssetProfileDetails:setAssetProfileRollOffDetails: Succesfully persisted" +
							" the faultDetails for assetId: "+serialNumber);
			}catch(Exception e1){
				fLogger.fatal("AssetProfileDetails:setAssetProfilePersonalityDetails:" +
						" Exception in persisting the faults details in " +
						"AssetProfileFaultDetails table "+ e.getMessage());
			}
		}
	}

	@SuppressWarnings({ "unchecked" })
	public void setAssetProfileInstallationDateDetails(String serialNumber,
			String installationDate) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String connIP, connPort, finalJsonString, formattedSerialNumber, formattedMachineNumber;
		connIP = connPort = finalJsonString = formattedSerialNumber = formattedMachineNumber = null;
		if(serialNumber != null){
			formattedMachineNumber = serialNumber.substring(serialNumber.length()-7);
		}
		ConnectMySQL connFactory = new ConnectMySQL();
		try(Connection conn = connFactory.getConnection();
				Statement st = conn.createStatement()){
			HashMap<String,String> finalProfileDetailsMap = new HashMap<String,String>();
			//Step 1 : Fetching SerialNumber from asset table based on Machine number
			String getSerialNumberQuery = "SELECT * from asset where Machine_Number='"+formattedMachineNumber+"'";
			ResultSet rs = st.executeQuery(getSerialNumberQuery);
			while(rs.next()){
				formattedSerialNumber = rs.getString("Serial_Number");
				if(formattedSerialNumber!=null){
					finalProfileDetailsMap.put("AssetID",formattedSerialNumber);
				}
			}
			finalProfileDetailsMap.put("InstallationDate", installationDate);
			JSONObject jsonObj = new JSONObject();
			jsonObj.putAll(finalProfileDetailsMap);
			finalJsonString = jsonObj.toString();
			iLogger.info("AssetProfileDetails:setAssetProfileInstallationDateDetails:"+finalJsonString);
			try{
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream
						("remote/wise/resource/properties/configuration.properties"));
				connIP = prop.getProperty("MDA_ServerIP");
				connPort = prop.getProperty("MDA_ServerPort");
			}catch(Exception e){
				fLogger.fatal("AssetProfileDetails:setAssetProfileInstallationDateDetails: " +
						"Exception in getting Server Details for MDA Layer from properties file: " +e+" for VIN:"+formattedSerialNumber);
				throw new Exception("Error reading from properties file");
			}try{
				//Invoking the REST URL from WISE to MOOL DA.
				URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/assetProfile/" +
						"inputPayloadDetails?inputPayloadMap="+URLEncoder.encode(finalJsonString, "UTF-8"));
				HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
				httpConn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
				httpConn.setRequestProperty("Accept", "text/plain");
				httpConn.setRequestMethod("GET");
				if (httpConn.getResponseCode() != 200 && httpConn.getResponseCode() != 204) {
					iLogger.info("MDAReports report status: FAILURE for setAssetProfileInstallationDateDetails Report VIN:"+serialNumber+" ::Response Code:"+httpConn.getResponseCode());
					
					throw new Exception("Failed : HTTP error code : "
							+ httpConn.getResponseCode());
				}
				iLogger.info("MDAReports report status: SUCCESS for setAssetProfileInstallationDateDetails Report VIN:"+serialNumber+" ::Response Code:"+httpConn.getResponseCode());
				
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(httpConn.getInputStream())));
				String outputResponse = null;
				while((outputResponse = br.readLine()) != null){
					iLogger.info("AssetProfileDetails:setAssetProfileInstallationDateDetails:" +
							" Response from {/MoolDA/assetProfile/}is: "+outputResponse+" for VIN:"+formattedSerialNumber);
				}
			}catch(Exception e){
				fLogger.fatal("AssetProfileDetails:setAssetProfileInstallationDateDetails: " +
						" Exception in invoking the {/MoolDA/assetProfile/} URL: "+ e.getMessage()+" for VIN:"+formattedSerialNumber);
				throw new Exception("REST URL not available");
			}
		}catch (Exception e) {
			fLogger.fatal("AssetProfileDetails:setAssetProfileRollOffDetails:Exception Cause: " +e.getMessage());
			String rejectionPoint = "AssetProfileRollOffDetails";
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String createdTimeStamp = dateFormat.format(date);
			try(Connection conn = connFactory.getConnection();
					Statement st = conn.createStatement()){
				String insertQ = "INSERT INTO MOOLDA_FaultDetails (SerialNumber, ProfileData, RejectionPoint, Created_TimeStamp)"
						+ " VALUES ('" + serialNumber + "','" + finalJsonString + "','" + rejectionPoint + "','" + createdTimeStamp +"')";
				st.execute(insertQ);
				iLogger.info("AssetProfileDetails:setAssetProfileRollOffDetails: Succesfully persisted" +
						" the faultDetails for assetId: "+serialNumber);
			}catch(Exception e1){
				fLogger.fatal("AssetProfileDetails:setAssetProfilePersonalityDetails:" +
					" Exception in persisting the faults details in " +
					"AssetProfileFaultDetails table "+ e.getMessage());
			}
		}
	}

	//DF20200714 Avinash Xavier A :Updating Renewal Details in Mongo DB:
		@SuppressWarnings({ "unchecked" })
		public void setAssetRenewalDateDetails(String serialNumber,
				String renewalDate ,String renewalFlag ) {

			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
			String connIP, connPort, finalJsonString ;
			connIP = connPort = finalJsonString =  null;
			iLogger.info("MoolDA:setAssetRenewalDateDetails : Input Params serialNumber:"+serialNumber +" renewalDate:"+renewalDate+" renewalFlag:"+renewalFlag);
			ConnectMySQL connFactory = new ConnectMySQL();
			try(Connection conn = connFactory.getConnection();
			Statement st = conn.createStatement()){
				HashMap<String,String> finalProfileDetailsMap = new HashMap<String,String>();
				if(serialNumber!=null && !serialNumber.equalsIgnoreCase("null")){
					finalProfileDetailsMap.put("AssetID", serialNumber);
				}
				if(renewalDate!=null && !renewalDate.equalsIgnoreCase("null")){
					finalProfileDetailsMap.put("RenewalDate", renewalDate);
				}
				if(renewalFlag!=null && !renewalFlag.equalsIgnoreCase("null")){
				finalProfileDetailsMap.put("RenewalFlag", renewalFlag);
				}
				JSONObject jsonObj = new JSONObject();
				jsonObj.putAll(finalProfileDetailsMap);
				finalJsonString = jsonObj.toString();
				iLogger.info("AssetProfileDetails:setAssetRenewalDateDetails:"+finalJsonString);
				try{
					Properties prop = new Properties();
					prop.load(getClass().getClassLoader().getResourceAsStream
							("remote/wise/resource/properties/configuration.properties"));
					connIP = prop.getProperty("MDA_ServerIP");
					connPort = prop.getProperty("MDA_ServerPort");
				}catch(Exception e){
					fLogger.fatal("AssetProfileDetails:setAssetRenewalDateDetails: " +
							"Exception in getting Server Details for MDA Layer from properties file: " +e+" for VIN:"+serialNumber);
					throw new Exception("Error reading from properties file");
				}try{
					//Invoking the REST URL from WISE to MOOL DA.
					URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/assetProfile/" +
							"inputPayloadDetails?inputPayloadMap="+URLEncoder.encode(finalJsonString, "UTF-8"));
					HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
					httpConn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
					httpConn.setRequestProperty("Accept", "text/plain");
					httpConn.setRequestMethod("GET");
					if (httpConn.getResponseCode() != 200 && httpConn.getResponseCode() != 204) {
						throw new Exception("Failed : HTTP error code : "
								+ httpConn.getResponseCode());
					}
					BufferedReader br = new BufferedReader(new InputStreamReader(
							(httpConn.getInputStream())));
					String outputResponse = null;
					while((outputResponse = br.readLine()) != null){
						iLogger.info("AssetProfileDetails:setAssetRenewalDateDetails:" +
								" Response from {/MoolDA/assetProfile/}is: "+outputResponse+" for VIN:"+serialNumber);
					}
				}catch(Exception e){
					fLogger.fatal("AssetProfileDetails:setAssetRenewalDateDetails: " +
							" Exception in invoking the {/MoolDA/assetProfile/} URL: "+ e.getMessage()+" for VIN:"+serialNumber);
					throw new Exception("REST URL not available");
				}
			}catch (Exception e) {
				fLogger.fatal("AssetProfileDetails:setAssetRenewalDateDetails:Exception Cause: " +e.getMessage());
				String rejectionPoint = "AssetProfileRollOffDetails";
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				String createdTimeStamp = dateFormat.format(date);
				try(Connection conn = connFactory.getConnection();
						Statement st = conn.createStatement()){
					String insertQ = "INSERT INTO MOOLDA_FaultDetails (SerialNumber, ProfileData, RejectionPoint, Created_TimeStamp)"
							+ " VALUES ('" + serialNumber + "','" + finalJsonString + "','" + rejectionPoint + "','" + createdTimeStamp +"')";
					st.execute(insertQ);
					iLogger.info("AssetProfileDetails:setAssetProfileRollOffDetails: Succesfully persisted" +
							" the faultDetails for assetId: "+serialNumber);
				}catch(Exception e1){
					fLogger.fatal("AssetProfileDetails:setAssetProfilePersonalityDetails:" +
						" Exception in persisting the faults details in " +
						"AssetProfileFaultDetails table "+ e.getMessage());
				}
			}
		}
		//JCB6336.en
		//JCB6336.so
		/*@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setAssetProfileRollOffDetails(String serialNumber, String engineNumber){
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Session session = null;
		String simNo, iccidNo, finalJsonString, connIP, connPort;
		simNo = iccidNo = finalJsonString = connIP = connPort = null;
		try{
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			//20220921: Dhiraj K : Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
			if(session.getTransaction().isActive() && session.isDirty())
			{
			   	iLogger.info("Opening a new session");
			   	session = HibernateUtil.getSessionFactory().openSession();
			}
			session.beginTransaction();
			String acuQ = ("select simNo, iccidNo from AssetControlUnitEntity where" +
					" serialNumber='"+serialNumber+"'");
			Query query = session.createQuery(acuQ);
			Iterator iterator = query.list().iterator();
			Object[] resultQ = null;
			HashMap<String,String> finalProfileDetailsMap = new HashMap<String,String>();
			finalProfileDetailsMap.put("AssetID", serialNumber);
			JSONObject jsonObj = null;
			while(iterator.hasNext()){
				resultQ = (Object[]) iterator.next();
				simNo = (String) resultQ[0];
				iccidNo = (String) resultQ[1];
				if(simNo != null)
					finalProfileDetailsMap.put("SIM", simNo);
					finalProfileDetailsMap.put("RenewalFlag", "1");
				if(iccidNo != null)
					finalProfileDetailsMap.put("ICCID_No", iccidNo);
			}
			if(engineNumber != null)
				finalProfileDetailsMap.put("EngineNumber", engineNumber);
			jsonObj = new JSONObject();
			jsonObj.putAll(finalProfileDetailsMap);
			finalJsonString = jsonObj.toString();
			//Invoking the REST URL from WISE to MOOL DA.
			try{
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream
						("remote/wise/resource/properties/configuration.properties"));
				connIP = prop.getProperty("MDA_ServerIP");
				connPort = prop.getProperty("MDA_ServerPort");
			}catch(Exception e){
				fLogger.fatal("AssetProfileDetails:setAssetProfileRollOffDetails: " +
						"Exception in getting Server Details for MDA Layer from properties file: " +e);
				throw new Exception("Error reading from properties file");
			}try{
				URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/assetProfile/" +
						"inputPayloadDetails?inputPayloadMap="+URLEncoder.encode(finalJsonString, "UTF-8"));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
				conn.setRequestProperty("Accept", "text/plain");
				conn.setRequestMethod("GET");
				if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) {
					iLogger.info("MDAReports report status: FAILURE for setAssetProfileRollOffDetails Report VIN:"+serialNumber+" ::Response Code:"+conn.getResponseCode());
					throw new Exception("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}
				iLogger.info("MDAReports report status: SUCCESS for setAssetProfileRollOffDetails Report VIN:"+serialNumber+" ::Response Code:"+conn.getResponseCode());
				
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));
				String outputResponse = null;
				while((outputResponse = br.readLine()) != null){
					iLogger.info("AssetProfileDetails:setAssetProfileRollOffDetails:" +
							" Response from {/MoolDA/assetProfile/}is: "+outputResponse+" for VIN:"+serialNumber);
				}
			}catch(Exception e){
				fLogger.fatal("AssetProfileDetails:setAssetProfileRollOffDetails:" +
						" Exception in invoking the {/MoolDA/assetProfile/} URL: "+ e.getMessage()+" for VIN:"+serialNumber);
				throw new Exception("REST URL not available");
			}
		}catch(Exception e){
			fLogger.fatal("AssetProfileDetails:setAssetProfileRollOffDetails:Exception Cause: " +e.getMessage());
			String rejectionPoint = "AssetProfileRollOffDetails";
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String createdTimeStamp = dateFormat.format(date);
			try{
				if(session==null || !(session.isOpen())){
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.beginTransaction();
				}
				MOOLDAFaultDetailsEntity faultDetailsObj = new MOOLDAFaultDetailsEntity();
				faultDetailsObj.setSerialNumber(serialNumber);
				faultDetailsObj.setProfileData(finalJsonString);
				faultDetailsObj.setRejectionPoint(rejectionPoint);
				faultDetailsObj.setCreatedTimeStamp(createdTimeStamp);
				session.save(faultDetailsObj);
				iLogger.info("AssetProfileDetails:setAssetProfileRollOffDetails: Succesfully persisted" +
						" the faultDetails for assetId: "+serialNumber);
			}catch(Exception e1){
				fLogger.fatal("AssetProfileDetails:setAssetProfileRollOffDetails:" +
						" Exception in persisting the faults details in " +
						"AssetProfileFaultDetails table "+ e1.getMessage());
			}
		}finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}if(session.isOpen()){
				session.flush();
				session.close();
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setAssetProfilePersonalityDetails(String serialNumber,
			String assetGroupCode, String assetTypeCode) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Session session = null;
		String profileName, modelName, finalJsonString, connIP, connPort, formattedSerialNumber, formattedMachineNumber;
		profileName = modelName = finalJsonString = connIP = connPort = formattedSerialNumber = formattedMachineNumber = null;
		HashMap<String,String> finalProfileDetailsMap = new HashMap<String,String>();
		if(serialNumber != null){
			formattedMachineNumber = serialNumber.replaceFirst("^0*", "");
		}
		try{
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			//Step 1 : Fetching SerialNumber from asset table based on Machine number
			String getSerialNumberQuery = "from AssetEntity where machineNumber='"+formattedMachineNumber+"'";
			Query query1 = session.createQuery(getSerialNumberQuery);
			Iterator iterator1 = query1.list().iterator();
			AssetEntity assetEntity = new AssetEntity();
			while(iterator1.hasNext()){
				assetEntity =  (AssetEntity) iterator1.next();
				formattedSerialNumber = assetEntity.getSerial_number().getSerialNumber();
				if(formattedSerialNumber!=null){
					finalProfileDetailsMap.put("AssetID",formattedSerialNumber);
				}
			}
			//Step 2 : Fetching Profile Name from asset_group based on the assetGroupCode
			String profileNameQuery = "select asset_group_name from AssetGroupEntity where " +
					"asset_group_id in (select asset_grp_id from AssetGroupProfileEntity where" +
					" asset_grp_code='"+assetGroupCode+"')";
			Query query2 = session.createQuery(profileNameQuery);
			Iterator iterator2 = query2.list().iterator();
			while(iterator2.hasNext()){
				profileName =  (String) iterator2.next();
				if(profileName !=null)
					finalProfileDetailsMap.put("ProfileName", profileName);
			}
			if(assetGroupCode!=null)
				finalProfileDetailsMap.put("ProfileCode", assetGroupCode);
			//Step 3 : Fetching Model Name from asset_type based on the assetTypeCode
			String modelNameQuery = "select asset_type_name from AssetTypeEntity" +
					" where assetTypeCode='"+assetTypeCode+"'";
			Query query3 = session.createQuery(modelNameQuery);
			Iterator iterator3 = query3.list().iterator();
			while(iterator3.hasNext()){
				modelName = (String) iterator3.next();
				if(modelName != null)
					finalProfileDetailsMap.put("ModelName",modelName);
			}
			if(assetTypeCode!=null)
				finalProfileDetailsMap.put("ModelCode", assetTypeCode);
			JSONObject jsonObj = new JSONObject();
			jsonObj.putAll(finalProfileDetailsMap);
			finalJsonString = jsonObj.toString();
			//Invoking the REST URL from WISE to MOOL DA.
			try{
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream
						("remote/wise/resource/properties/configuration.properties"));
				connIP = prop.getProperty("MDA_ServerIP");
				connPort = prop.getProperty("MDA_ServerPort");
			}catch(Exception e){
				fLogger.fatal("AssetProfileDetails:setAssetProfilePersonalityDetails: " +
						"Exception in getting Server Details for MDA Layer from properties file: " +e+" for VIN:"+formattedSerialNumber);
				
				throw new Exception("Error reading from properties file");
			}try{
				URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/assetProfile/" +
						"inputPayloadDetails?inputPayloadMap="+URLEncoder.encode(finalJsonString, "UTF-8"));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
				conn.setRequestProperty("Accept", "text/plain");
				conn.setRequestMethod("GET");
				if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) {
					iLogger.info("MDAReports report status: FAILURE for setAssetProfilePersonalityDetails Report VIN:"+serialNumber+" ::Response Code:"+conn.getResponseCode());
					
					throw new Exception("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}
				iLogger.info("MDAReports report status: SUCCESS for setAssetProfilePersonalityDetails Report VIN:"+serialNumber+" ::Response Code:"+conn.getResponseCode());
				
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));
				String outputResponse = null;
				while((outputResponse = br.readLine()) != null){
					iLogger.info("AssetProfileDetails:setAssetProfileRollOffDetails:" +
							" Response from {/MoolDA/assetProfile/}is: "+outputResponse+" for VIN:"+formattedSerialNumber);
				}
			}catch(Exception e){
				fLogger.fatal("AssetProfileDetails:setAssetProfilePersonalityDetails: " +
						" Exception in invoking the {/MoolDA/assetProfile/} URL: "+ e.getMessage()+" for VIN:"+formattedSerialNumber);
				throw new Exception("REST URL not available");
			}
		}catch (Exception e) {
			fLogger.fatal("AssetProfileDetails:setAssetProfileRollOffDetails:Exception Cause: " +e.getMessage());
			String rejectionPoint = "AssetProfileRollOffDetails";
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String createdTimeStamp = dateFormat.format(date);
			try{
				if(session==null || !(session.isOpen())){
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.beginTransaction();
					MOOLDAFaultDetailsEntity faultDetailsObj = new MOOLDAFaultDetailsEntity();
					faultDetailsObj.setSerialNumber(formattedSerialNumber);
					faultDetailsObj.setProfileData(finalJsonString);
					faultDetailsObj.setRejectionPoint(rejectionPoint);
					faultDetailsObj.setCreatedTimeStamp(createdTimeStamp);
					session.save(faultDetailsObj);
					iLogger.info("AssetProfileDetails:setAssetProfilePersonalityDetails:: Succesfully persisted" +
							" the faultDetails for assetId: "+formattedSerialNumber);
				}
			}catch(Exception e1){
				fLogger.fatal("AssetProfileDetails:setAssetProfilePersonalityDetails:" +
						" Exception in persisting the faults details in " +
						"AssetProfileFaultDetails table "+ e.getMessage());
			}
		}finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}if(session.isOpen()){
				session.flush();
				session.close();
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setAssetProfileInstallationDateDetails(String serialNumber,
			String installationDate) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String connIP, connPort, finalJsonString, formattedSerialNumber, formattedMachineNumber;
		connIP = connPort = finalJsonString = formattedSerialNumber = formattedMachineNumber = null;
		Session session = null;
		if(serialNumber != null){
			formattedMachineNumber = serialNumber.replaceFirst("^0*", "");
		}
		try{
			HashMap<String,String> finalProfileDetailsMap = new HashMap<String,String>();
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			//Step 1 : Fetching SerialNumber from asset table based on Machine number
			String getSerialNumberQuery = "from AssetEntity where machineNumber='"+formattedMachineNumber+"'";
			Query query1 = session.createQuery(getSerialNumberQuery);
			Iterator iterator1 = query1.list().iterator();
			AssetEntity assetEntity = new AssetEntity();
			while(iterator1.hasNext()){
				assetEntity =  (AssetEntity) iterator1.next();
				formattedSerialNumber = assetEntity.getSerial_number().getSerialNumber();
				if(formattedSerialNumber!=null){
					finalProfileDetailsMap.put("AssetID",formattedSerialNumber);
				}
			}
			finalProfileDetailsMap.put("InstallationDate", installationDate);
			JSONObject jsonObj = new JSONObject();
			jsonObj.putAll(finalProfileDetailsMap);
			finalJsonString = jsonObj.toString();
			try{
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream
						("remote/wise/resource/properties/configuration.properties"));
				connIP = prop.getProperty("MDA_ServerIP");
				connPort = prop.getProperty("MDA_ServerPort");
			}catch(Exception e){
				fLogger.fatal("AssetProfileDetails:setAssetProfileInstallationDateDetails: " +
						"Exception in getting Server Details for MDA Layer from properties file: " +e+" for VIN:"+formattedSerialNumber);
				throw new Exception("Error reading from properties file");
			}try{
				//Invoking the REST URL from WISE to MOOL DA.
				URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/assetProfile/" +
						"inputPayloadDetails?inputPayloadMap="+URLEncoder.encode(finalJsonString, "UTF-8"));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
				conn.setRequestProperty("Accept", "text/plain");
				conn.setRequestMethod("GET");
				if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) {
					iLogger.info("MDAReports report status: FAILURE for setAssetProfileInstallationDateDetails Report VIN:"+serialNumber+" ::Response Code:"+conn.getResponseCode());
					
					throw new Exception("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}
				iLogger.info("MDAReports report status: SUCCESS for setAssetProfileInstallationDateDetails Report VIN:"+serialNumber+" ::Response Code:"+conn.getResponseCode());
				
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));
				String outputResponse = null;
				while((outputResponse = br.readLine()) != null){
					iLogger.info("AssetProfileDetails:setAssetProfileInstallationDateDetails:" +
							" Response from {/MoolDA/assetProfile/}is: "+outputResponse+" for VIN:"+formattedSerialNumber);
				}
			}catch(Exception e){
				fLogger.fatal("AssetProfileDetails:setAssetProfileInstallationDateDetails: " +
						" Exception in invoking the {/MoolDA/assetProfile/} URL: "+ e.getMessage()+" for VIN:"+formattedSerialNumber);
				throw new Exception("REST URL not available");
			}
		}catch (Exception e) {
			fLogger.fatal("AssetProfileDetails:setAssetProfileRollOffDetails:Exception Cause: " +e.getMessage());
			String rejectionPoint = "AssetProfileRollOffDetails";
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String createdTimeStamp = dateFormat.format(date);
			try{
				if((session == null) || (!(session.isOpen()))){
					session = HibernateUtil.getSessionFactory().openSession();
					session.beginTransaction();
				}
				MOOLDAFaultDetailsEntity faultDetailsObj = new MOOLDAFaultDetailsEntity();
				faultDetailsObj.setSerialNumber(formattedSerialNumber);
				faultDetailsObj.setProfileData(finalJsonString);
				faultDetailsObj.setRejectionPoint(rejectionPoint);
				faultDetailsObj.setCreatedTimeStamp(createdTimeStamp);
				session.save(faultDetailsObj);
				iLogger.info("AssetProfileDetails:setAssetProfileInstallationDateDetails: Succesfully persisted" +
						" the faultDetails for assetId: "+formattedSerialNumber);

			}catch(Exception e1){
				fLogger.fatal("AssetProfileDetails:setAssetProfileInstallationDateDetails:" +
						" Exception in persisting the faults details in " +
						"AssetProfileFaultDetails table "+ e.getMessage()+" for VIN:"+formattedSerialNumber);
			}
			finally
			{
				if(session.getTransaction().isActive()){
					session.getTransaction().commit();
				}if(session.isOpen()){
					session.flush();
					session.close();
				}
			}
		}finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}if(session.isOpen()){
				session.flush();
				session.close();
			}
		}
	}

	//DF20200714 Avinash Xavier A :Updating Renewal Details in Mongo DB:
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void setAssetRenewalDateDetails(String serialNumber,
				String renewalDate ,String renewalFlag ) {

			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
			String connIP, connPort, finalJsonString ;
			connIP = connPort = finalJsonString =  null;
			Session session = null;
			iLogger.info("MoolDA:setAssetRenewalDateDetails : Input Params serialNumber:"+serialNumber +" renewalDate:"+renewalDate+" renewalFlag:"+renewalFlag);
			try{
				HashMap<String,String> finalProfileDetailsMap = new HashMap<String,String>();
				if(serialNumber!=null && !serialNumber.equalsIgnoreCase("null")){
					finalProfileDetailsMap.put("AssetID", serialNumber);
				}
				if(renewalDate!=null && !renewalDate.equalsIgnoreCase("null")){
					finalProfileDetailsMap.put("RenewalDate", renewalDate);
				}
				if(renewalFlag!=null && !renewalFlag.equalsIgnoreCase("null")){
				finalProfileDetailsMap.put("RenewalFlag", renewalFlag);
				}
				JSONObject jsonObj = new JSONObject();
				jsonObj.putAll(finalProfileDetailsMap);
				finalJsonString = jsonObj.toString();
				
				try{
					Properties prop = new Properties();
					prop.load(getClass().getClassLoader().getResourceAsStream
							("remote/wise/resource/properties/configuration.properties"));
					connIP = prop.getProperty("MDA_ServerIP");
					connPort = prop.getProperty("MDA_ServerPort");
				}catch(Exception e){
					fLogger.fatal("AssetProfileDetails:setAssetRenewalDateDetails: " +
							"Exception in getting Server Details for MDA Layer from properties file: " +e+" for VIN:"+serialNumber);
					throw new Exception("Error reading from properties file");
				}try{
					//Invoking the REST URL from WISE to MOOL DA.
					URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/assetProfile/" +
							"inputPayloadDetails?inputPayloadMap="+URLEncoder.encode(finalJsonString, "UTF-8"));
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
					conn.setRequestProperty("Accept", "text/plain");
					conn.setRequestMethod("GET");
					if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) {
						throw new Exception("Failed : HTTP error code : "
								+ conn.getResponseCode());
					}
					BufferedReader br = new BufferedReader(new InputStreamReader(
							(conn.getInputStream())));
					String outputResponse = null;
					while((outputResponse = br.readLine()) != null){
						iLogger.info("AssetProfileDetails:setAssetRenewalDateDetails:" +
								" Response from {/MoolDA/assetProfile/}is: "+outputResponse+" for VIN:"+serialNumber);
					}
				}catch(Exception e){
					fLogger.fatal("AssetProfileDetails:setAssetRenewalDateDetails: " +
							" Exception in invoking the {/MoolDA/assetProfile/} URL: "+ e.getMessage()+" for VIN:"+serialNumber);
					throw new Exception("REST URL not available");
				}
			}catch (Exception e) {
				fLogger.fatal("AssetProfileDetails:setAssetRenewalDateDetails:Exception Cause: " +e.getMessage());
				String rejectionPoint = "AssetProfileRollOffDetails";
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				String createdTimeStamp = dateFormat.format(date);
				try{
					session = HibernateUtil.getSessionFactory().openSession();
					session.beginTransaction();
					
					if((session == null) || (!(session.isOpen()))){
						session = HibernateUtil.getSessionFactory().openSession();
						session.beginTransaction();
					}
					MOOLDAFaultDetailsEntity faultDetailsObj = new MOOLDAFaultDetailsEntity();
					faultDetailsObj.setSerialNumber(serialNumber);
					faultDetailsObj.setProfileData(finalJsonString);
					faultDetailsObj.setRejectionPoint(rejectionPoint);
					faultDetailsObj.setCreatedTimeStamp(createdTimeStamp);
					session.save(faultDetailsObj);
					iLogger.info("AssetProfileDetails:setAssetRenewalDateDetails: Succesfully persisted" +
							" the faultDetails for assetId: "+serialNumber);

				}catch(Exception e1){
					fLogger.fatal("AssetProfileDetails:setAssetRenewalDateDetails" +
							" Exception in persisting the faults details in " +
							"AssetProfileFaultDetails table "+ e.getMessage()+" for VIN:"+serialNumber);
				}
				finally
				{
					if(session.getTransaction().isActive()){
						session.getTransaction().commit();
					}if(session.isOpen()){
						session.flush();
						session.close();
					}
				}
			}
		}*/
		//JCB6336.eo
}