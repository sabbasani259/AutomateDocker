/*
 * JCB6336 : 20230214 : Dhiraj k : Fault handling for sending data to MoolDA - ownership and personality update
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

public class AssetOwnershipDetails {

	//JCB6336.sn
	@SuppressWarnings({"unchecked" })
	public void setAssetOwnershipDetails(String serialNumber, String interfaceFlow ) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		int accountId = -1;
		String ownershipStartDate, accountType, finalJsonString, formattedSerialNumber, connIP, connPort, formattedMachineNumber;
		ownershipStartDate = accountType = finalJsonString = connIP = formattedSerialNumber = connPort = formattedMachineNumber = null;
		HashMap<String,String> finalpayLoadMap = new HashMap<String,String>();
		Date ownDate = null;
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
			String aosQ = "select aos.Account_ID,aos.Ownership_Start_Date,aos.Account_Type from asset_owner_snapshot aos where " +
					"aos.Serial_Number='"+formattedSerialNumber+"' order by aos.Ownership_Start_Date asc, aos.Account_Type desc";
			Statement st1 = conn.createStatement();
			ResultSet rs1 = st1.executeQuery(aosQ);
			JSONObject jsonObj = null;
			while(rs1.next()){
				accountId = rs1.getInt("Account_ID");
				ownDate = rs1.getDate("Ownership_Start_Date");
				if(ownDate != null){
					ownershipStartDate = ownDate.toString().substring(0, 10);
					finalpayLoadMap.put("OwnershipDate", ownershipStartDate);
				}
				accountType = rs1.getString("Account_Type");
				String accQ = "SELECT * from account where account_id="+accountId;
				Statement st2 = conn.createStatement();
				ResultSet rs2 = st2.executeQuery(accQ);
				if(rs2.next()){
					if(accountType.equalsIgnoreCase("OEM Global")){
						String globalAccCode = rs2.getString("Account_Code");
						if(globalAccCode!=null){
							finalpayLoadMap.put("GlobalAccCode", globalAccCode);
							finalpayLoadMap.put("OwnerAccCode", globalAccCode);
						}
						String globalAccName = rs2.getString("Account_Name");
						if(globalAccName!=null){
							finalpayLoadMap.put("GlobalAccName", globalAccName);
							finalpayLoadMap.put("OwnerAccName", globalAccName);
						}
						String globalAccContact = rs2.getString("Mobile");
						if(globalAccContact != null){
							finalpayLoadMap.put("GlobalAccContact", globalAccContact);
							finalpayLoadMap.put("OwnerContact", globalAccContact);
						}
						String timeZone = rs2.getString("timeZone");
						if(timeZone != null){
							String UTCOffset = LTtoUTCOffset(timeZone);
							finalpayLoadMap.put("UTCOffset", UTCOffset);
							finalpayLoadMap.put("TimeZone", timeZone);
						}
						String countryCode = rs2.getString("CountryCode");
						if(countryCode != null){
							finalpayLoadMap.put("CountryCode", countryCode);
						}
					}else if(accountType.equalsIgnoreCase("OEM")) {
						String regionCode = rs2.getString("Account_Code");
						if(regionCode != null){
							finalpayLoadMap.put("RegionCode", regionCode);
							finalpayLoadMap.put("OwnerAccCode", regionCode);
						}
						String regionName = rs2.getString("Account_Name");
						if(regionName != null){
							finalpayLoadMap.put("RegionName", regionName);
							finalpayLoadMap.put("OwnerAccName", regionName);
						}
						String regionContact = rs2.getString("Mobile");
						if(regionContact != null){
							finalpayLoadMap.put("RegionContact", regionContact);
							finalpayLoadMap.put("OwnerContact", regionContact);
						}
						String timeZone = rs2.getString("timeZone");
						if(timeZone != null){
							String UTCOffset = LTtoUTCOffset(timeZone);
							finalpayLoadMap.put("UTCOffset", UTCOffset);
							finalpayLoadMap.put("TimeZone", timeZone);
						}
						String countryCode = rs2.getString("CountryCode");
						if(countryCode != null){
							finalpayLoadMap.put("CountryCode", countryCode);
						}
					}else if(accountType.equalsIgnoreCase("OEM RO")){
						String zonalCode = rs2.getString("Account_Code");
						if(zonalCode != null){
							finalpayLoadMap.put("ZonalCode", zonalCode);
							finalpayLoadMap.put("OwnerAccCode", zonalCode);
						}
						String zonalName = rs2.getString("Account_Name");
						if(zonalName != null){
							finalpayLoadMap.put("ZonalName", zonalName);
							finalpayLoadMap.put("OwnerAccName", zonalName);
						}	 
						String zonalContact = rs2.getString("Mobile");
						if(zonalContact != null){
							finalpayLoadMap.put("ZonalContact", zonalContact);
							finalpayLoadMap.put("OwnerContact", zonalContact);
						}
						String timeZone = rs2.getString("timeZone");
						if(timeZone != null){
							String UTCOffset = LTtoUTCOffset(timeZone);
							finalpayLoadMap.put("UTCOffset", UTCOffset);
							finalpayLoadMap.put("TimeZone", timeZone);
						}
						String countryCode = rs2.getString("CountryCode");
						if(countryCode != null){
							finalpayLoadMap.put("CountryCode", countryCode);
						}
					}else if(accountType.equalsIgnoreCase("Dealer")){
						String dealerCode =  rs2.getString("Account_Code");
						if(dealerCode != null){
							finalpayLoadMap.put("DealerCode", dealerCode);
							finalpayLoadMap.put("OwnerAccCode", dealerCode);
						}
						String dealerName = rs2.getString("Account_Name");
						if(dealerName != null){
							finalpayLoadMap.put("DealerName", dealerName);
							finalpayLoadMap.put("OwnerAccName", dealerName);
						}	 
						String dealerContact = rs2.getString("Mobile");
						if(dealerContact != null){
							finalpayLoadMap.put("DealerContact", dealerContact);
							finalpayLoadMap.put("OwnerContact", dealerContact);
						}
						String timeZone = rs2.getString("timeZone");
						if(timeZone != null){
							String UTCOffset = LTtoUTCOffset(timeZone);
							finalpayLoadMap.put("UTCOffset", UTCOffset);
							finalpayLoadMap.put("TimeZone", timeZone);
						}
						String countryCode = rs2.getString("CountryCode");
						if(countryCode != null){
							finalpayLoadMap.put("CountryCode", countryCode);
						}
					}else if(accountType.equalsIgnoreCase("Customer")){
						String custCode = rs2.getString("Account_Code");
						if(custCode != null){
							finalpayLoadMap.put("CustCode", custCode);
							finalpayLoadMap.put("OwnerAccCode", custCode);
						}
						String custName = rs2.getString("Account_Name");
						if(custName != null){
							finalpayLoadMap.put("CustName", custName);
							finalpayLoadMap.put("OwnerAccName", custName);
						}	 
						String custContact = rs2.getString("Mobile");
						if(custContact != null){
							finalpayLoadMap.put("CustContact", custContact);
							finalpayLoadMap.put("OwnerContact", custContact);
						}
						String timeZone = rs2.getString("timeZone");
						if(timeZone != null){
							String UTCOffset = LTtoUTCOffset(timeZone);
							finalpayLoadMap.put("UTCOffset", UTCOffset);
							finalpayLoadMap.put("TimeZone", timeZone);
						}
						String countryCode = rs2.getString("CountryCode");
						if(countryCode != null){
							finalpayLoadMap.put("CountryCode", countryCode);
						}
					}
				}
			}
			jsonObj = new JSONObject();
			jsonObj.putAll(finalpayLoadMap);
			finalJsonString = jsonObj.toString();
			iLogger.info("AssetOwnershipDetails:setAssetOwnershipDetails:finalJsonString:"+finalJsonString);
			//Invoking the REST URL from WISE to MOOL DA.
			try{
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream
						("remote/wise/resource/properties/configuration.properties"));
				connIP = prop.getProperty("MDA_ServerIP");
				connPort = prop.getProperty("MDA_ServerPort");

			}catch(Exception e){
				fLogger.fatal("AssetOwnershipDetails:setAssetOwnershipDetails: " +
						"Exception in getting Server Details for MDA Layer from properties file: " +e);
				throw new Exception("Error reading from properties file");
			}try{
				URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/OwnershipDetails/" +
						"publishSaleDetails?topicName=MDAOwnership&inputMsg="+URLEncoder.encode(finalJsonString, "UTF-8"));
				HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
				httpConn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
				httpConn.setRequestProperty("Accept", "text/plain");
				httpConn.setRequestMethod("GET");
				if (httpConn.getResponseCode() != 200 && httpConn.getResponseCode() != 204) {
					iLogger.info("MDAReports report status:"+ interfaceFlow +": FAILURE for AssetOwnershipDetails Report VIN:"+formattedMachineNumber+" ::Response Code:"+httpConn.getResponseCode());
					throw new Exception("Failed : HTTP error code : "
							+ httpConn.getResponseCode());
				}
				iLogger.info("MDAReports report status:"+ interfaceFlow +": SUCCESS for AssetOwnershipDetails Report VIN:"+formattedMachineNumber+" ::Response Code:"+httpConn.getResponseCode());
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(httpConn.getInputStream())));
				String outputResponse = null;
				while((outputResponse = br.readLine()) != null){
					iLogger.info("AssetOwnershipDetails:setAssetOwnershipDetails:" +
							" Response from {/MoolDA/OwnershipDetails/}is: "+outputResponse+" for VIN:"+formattedSerialNumber);
				}
			}catch(Exception e1){
				fLogger.fatal("AssetOwnershipDetails:setAssetOwnershipDetails:" +
						" Exception in invoking the {/MoolDA/OwnershipDetails/} URL: "+ e1.getMessage()+" for VIN:"+formattedSerialNumber);
				throw new Exception("REST URL not available.");
			}
		}catch (Exception e2) {
			e2.printStackTrace();
			fLogger.fatal("AssetOwnershipDetails:setAssetOwnershipDetails:Exception Cause: " +e2.getMessage());
			String rejectionPoint = "AssetOwnershipDetails";
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String createdTimeStamp = dateFormat.format(date);
			try(Connection conn = connFactory.getConnection();
					Statement st = conn.createStatement()){
				String insertQ = "INSERT INTO MOOLDA_FaultDetails (SerialNumber, ProfileData, RejectionPoint, Created_TimeStamp)"
						+ " VALUES ('" + serialNumber + "','" + finalJsonString + "','" + rejectionPoint + "','" + createdTimeStamp +"')";
				st.execute(insertQ);
				iLogger.info("AssetOwnershipDetails:setAssetOwnershipDetails:: Succesfully persisted" +
						" the faultDetails for assetId: "+formattedSerialNumber);
			}catch(Exception e3){
				fLogger.fatal("AssetOwnershipDetails:setAssetOwnershipDetails:" +
						" Exception in persisting the faults details in " +
						"AssetProfileFaultDetails table "+ e3.getMessage()+" for VIN:"+formattedSerialNumber);
			}
		}
	}
	//JCB6336.en
	//JCB6336.so
	/*@SuppressWarnings({ "rawtypes", "unchecked" })
	//public void setAssetOwnershipDetails(String serialNumber) {//20220921.o.Additional log added
	public void setAssetOwnershipDetails(String serialNumber, String interfaceFlow ) {//20220921.n.Additional log added

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		AccountEntity accountId = null;
		String ownershipStartDate, accountType, finalJsonString, formattedSerialNumber, connIP, connPort, formattedMachineNumber;
		ownershipStartDate = accountType = finalJsonString = connIP = formattedSerialNumber = connPort = formattedMachineNumber = null;
		HashMap<String,String> finalpayLoadMap = new HashMap<String,String>();
		Date ownDate = null;
		Session session = null;
		if(serialNumber != null){
			formattedMachineNumber = serialNumber.substring(serialNumber.length()-7);
		}
		try{
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			String getSerialNumberQ = "from AssetEntity where machineNumber='"+formattedMachineNumber+"' and active_status = 1";
			Query getSerialNumberQuery = session.createQuery(getSerialNumberQ);
			Iterator iterator1 = getSerialNumberQuery.list().iterator();
			AssetEntity assetEntity = new AssetEntity();
			while(iterator1.hasNext()){
				assetEntity =  (AssetEntity) iterator1.next();
				formattedSerialNumber = assetEntity.getSerial_number().getSerialNumber();
				if(formattedSerialNumber!=null){
					finalpayLoadMap.put("AssetID",formattedSerialNumber);
				}
			}
			String aosQ = "select aos.accountId,aos.assetOwnershipDate,aos.accountType from AssetOwnerSnapshotEntity aos where " +
					"aos.serialNumber='"+formattedSerialNumber+"' order by aos.assetOwnershipDate asc, aos.accountType desc";
			Query aosQuery = session.createQuery(aosQ);
			Object[] resultQ = null;
			Iterator outerIterator = aosQuery.list().iterator();

			JSONObject jsonObj = null;
			while(outerIterator.hasNext()){
				resultQ =(Object[]) outerIterator.next();
				accountId = (AccountEntity) resultQ[0];
				ownDate = (Date) resultQ[1];
				if(ownDate != null){
					ownershipStartDate = ownDate.toString().substring(0, 10);
					finalpayLoadMap.put("OwnershipDate", ownershipStartDate);
				}
				accountType = (String) resultQ[2];
				String accQ = "from AccountEntity where account_id="+accountId.getAccount_id();
				Query accQuery = session.createQuery(accQ);
				Iterator innerIterator = accQuery.list().iterator();
				AccountEntity accEntity = new AccountEntity();
				if(innerIterator.hasNext()){
					accEntity = (AccountEntity) innerIterator.next();
					if(accountType.equalsIgnoreCase("OEM Global")){
						String globalAccCode = accEntity.getAccountCode();
						if(globalAccCode!=null){
							finalpayLoadMap.put("GlobalAccCode", globalAccCode);
							finalpayLoadMap.put("OwnerAccCode", globalAccCode);
						}
						String globalAccName = accEntity.getAccount_name();
						if(globalAccName!=null){
							finalpayLoadMap.put("GlobalAccName", globalAccName);
							finalpayLoadMap.put("OwnerAccName", globalAccName);
						}
						String globalAccContact = accEntity.getMobile_no();
						if(globalAccContact != null){
							finalpayLoadMap.put("GlobalAccContact", globalAccContact);
							finalpayLoadMap.put("OwnerContact", globalAccContact);
						}
						String timeZone = accEntity.getTimeZone();
						if(timeZone != null){
							String UTCOffset = LTtoUTCOffset(timeZone);
							finalpayLoadMap.put("UTCOffset", UTCOffset);
							finalpayLoadMap.put("TimeZone", timeZone);
						}
						String countryCode = accEntity.getCountryCode();
						if(countryCode != null){
							finalpayLoadMap.put("CountryCode", countryCode);
						}
					}else if(accountType.equalsIgnoreCase("OEM")) {
						String regionCode = accEntity.getAccountCode();
						if(regionCode != null){
							finalpayLoadMap.put("RegionCode", regionCode);
							finalpayLoadMap.put("OwnerAccCode", regionCode);
						}
						String regionName = accEntity.getAccount_name();
						if(regionName != null){
							finalpayLoadMap.put("RegionName", regionName);
							finalpayLoadMap.put("OwnerAccName", regionName);
						}
						String regionContact = accEntity.getMobile_no();
						if(regionContact != null){
							finalpayLoadMap.put("RegionContact", regionContact);
							finalpayLoadMap.put("OwnerContact", regionContact);
						}
						String timeZone = accEntity.getTimeZone();
						if(timeZone != null){
							String UTCOffset = LTtoUTCOffset(timeZone);
							finalpayLoadMap.put("UTCOffset", UTCOffset);
							finalpayLoadMap.put("TimeZone", timeZone);
						}
						String countryCode = accEntity.getCountryCode();
						if(countryCode != null){
							finalpayLoadMap.put("CountryCode", countryCode);
						}
					}else if(accountType.equalsIgnoreCase("OEM RO")){
						String zonalCode = accEntity.getAccountCode();
						if(zonalCode != null){
							finalpayLoadMap.put("ZonalCode", zonalCode);
							finalpayLoadMap.put("OwnerAccCode", zonalCode);
						}
						String zonalName = accEntity.getAccount_name();
						if(zonalName != null){
							finalpayLoadMap.put("ZonalName", zonalName);
							finalpayLoadMap.put("OwnerAccName", zonalName);
						}	 
						String zonalContact = accEntity.getMobile_no();
						if(zonalContact != null){
							finalpayLoadMap.put("ZonalContact", zonalContact);
							finalpayLoadMap.put("OwnerContact", zonalContact);
						}
						String timeZone = accEntity.getTimeZone();
						if(timeZone != null){
							String UTCOffset = LTtoUTCOffset(timeZone);
							finalpayLoadMap.put("UTCOffset", UTCOffset);
							finalpayLoadMap.put("TimeZone", timeZone);
						}
						String countryCode = accEntity.getCountryCode();
						if(countryCode != null){
							finalpayLoadMap.put("CountryCode", countryCode);
						}
					}else if(accountType.equalsIgnoreCase("Dealer")){
						String dealerCode = accEntity.getAccountCode();
						if(dealerCode != null){
							finalpayLoadMap.put("DealerCode", dealerCode);
							finalpayLoadMap.put("OwnerAccCode", dealerCode);
						}
						String dealerName = accEntity.getAccount_name();
						if(dealerName != null){
							finalpayLoadMap.put("DealerName", dealerName);
							finalpayLoadMap.put("OwnerAccName", dealerName);
						}	 
						String dealerContact = accEntity.getMobile_no();
						if(dealerContact != null){
							finalpayLoadMap.put("DealerContact", dealerContact);
							finalpayLoadMap.put("OwnerContact", dealerContact);
						}
						String timeZone = accEntity.getTimeZone();
						if(timeZone != null){
							String UTCOffset = LTtoUTCOffset(timeZone);
							finalpayLoadMap.put("UTCOffset", UTCOffset);
							finalpayLoadMap.put("TimeZone", timeZone);
						}
						String countryCode = accEntity.getCountryCode();
						if(countryCode != null){
							finalpayLoadMap.put("CountryCode", countryCode);
						}
					}else if(accountType.equalsIgnoreCase("Customer")){
						String custCode = accEntity.getAccountCode();
						if(custCode != null){
							finalpayLoadMap.put("CustCode", custCode);
							finalpayLoadMap.put("OwnerAccCode", custCode);
						}
						String custName = accEntity.getAccount_name();
						if(custName != null){
							finalpayLoadMap.put("CustName", custName);
							finalpayLoadMap.put("OwnerAccName", custName);
						}	 
						String custContact = accEntity.getMobile_no();
						if(custContact != null){
							finalpayLoadMap.put("CustContact", custContact);
							finalpayLoadMap.put("OwnerContact", custContact);
						}
						String timeZone = accEntity.getTimeZone();
						if(timeZone != null){
							String UTCOffset = LTtoUTCOffset(timeZone);
							finalpayLoadMap.put("UTCOffset", UTCOffset);
							finalpayLoadMap.put("TimeZone", timeZone);
						}
						String countryCode = accEntity.getCountryCode();
						if(countryCode != null){
							finalpayLoadMap.put("CountryCode", countryCode);
						}
					}
				}
			}
			jsonObj = new JSONObject();
			jsonObj.putAll(finalpayLoadMap);
			finalJsonString = jsonObj.toString();
			//Invoking the REST URL from WISE to MOOL DA.
			try{
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream
						("remote/wise/resource/properties/configuration.properties"));
				connIP = prop.getProperty("MDA_ServerIP");
				connPort = prop.getProperty("MDA_ServerPort");
			}catch(Exception e){
				fLogger.fatal("AssetOwnershipDetails:setAssetOwnershipDetails: " +
						"Exception in getting Server Details for MDA Layer from properties file: " +e);
				throw new Exception("Error reading from properties file");
			}try{
				URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/OwnershipDetails/" +
						"publishSaleDetails?topicName=MDAOwnership&inputMsg="+URLEncoder.encode(finalJsonString, "UTF-8"));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
				conn.setRequestProperty("Accept", "text/plain");
				conn.setRequestMethod("GET");
				if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) {
					//iLogger.info("MDAReports report status: FAILURE for AssetOwnershipDetails Report VIN:"+formattedMachineNumber+" ::Response Code:"+conn.getResponseCode());//20220921.o.Additional log added
					iLogger.info("MDAReports report status:"+ interfaceFlow +": FAILURE for AssetOwnershipDetails Report VIN:"+formattedMachineNumber+" ::Response Code:"+conn.getResponseCode());//20220921.n.Additional log added
					throw new Exception("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}
				//iLogger.info("MDAReports report status: SUCCESS for AssetOwnershipDetails Report VIN:"+formattedMachineNumber+" ::Response Code:"+conn.getResponseCode());//20220921.o.Additional log added
				iLogger.info("MDAReports report status:"+ interfaceFlow +": SUCCESS for AssetOwnershipDetails Report VIN:"+formattedMachineNumber+" ::Response Code:"+conn.getResponseCode());//20220921.n.Additional log added
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));
				String outputResponse = null;
				while((outputResponse = br.readLine()) != null){
					iLogger.info("AssetOwnershipDetails:setAssetOwnershipDetails:" +
							" Response from {/MoolDA/OwnershipDetails/}is: "+outputResponse+" for VIN:"+formattedSerialNumber);
				}
			}catch(Exception e1){
				fLogger.fatal("AssetOwnershipDetails:setAssetOwnershipDetails:" +
						" Exception in invoking the {/MoolDA/OwnershipDetails/} URL: "+ e1.getMessage()+" for VIN:"+formattedSerialNumber);
				throw new Exception("REST URL not available.");
			}
		}catch (Exception e2) {
			fLogger.fatal("AssetOwnershipDetails:setAssetOwnershipDetails:Exception Cause: " +e2.getMessage());
			String rejectionPoint = "AssetOwnershipDetails";
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String createdTimeStamp = dateFormat.format(date);
			try{
				if(session==null || !(session.isOpen())){
					session = HibernateUtil.getSessionFactory().openSession();
					session.beginTransaction();
				}
				MOOLDAFaultDetailsEntity faultDetailsObj = new MOOLDAFaultDetailsEntity();
				faultDetailsObj.setSerialNumber(serialNumber);
				faultDetailsObj.setProfileData(finalJsonString);
				faultDetailsObj.setRejectionPoint(rejectionPoint);
				faultDetailsObj.setCreatedTimeStamp(createdTimeStamp);
				session.save(faultDetailsObj);
				iLogger.info("AssetOwnershipDetails:setAssetOwnershipDetails:: Succesfully persisted" +
						" the faultDetails for assetId: "+formattedSerialNumber);
			}catch(Exception e3){
				fLogger.fatal("AssetOwnershipDetails:setAssetOwnershipDetails:" +
						" Exception in persisting the faults details in " +
						"AssetProfileFaultDetails table "+ e3.getMessage()+" for VIN:"+formattedSerialNumber);
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
	 *///JCB6336.eo

	public static String LTtoUTCOffset(String timeZone){
		String time = timeZone.replaceAll("[^\\d:]", "");
		int hours = Integer.parseInt(time.split("\\:")[0]);
		int minutes = Integer.parseInt(time.split("\\:")[1]);
		int totalTime = (hours*60)+minutes;
		String convtimeZone = ""+totalTime;
		return convtimeZone;
	}
}
