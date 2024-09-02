package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import redis.clients.jedis.Jedis;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.LocationDetails;
import remote.wise.service.datacontract.LocationDetailsMMI;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.GetSetLocationJedis;
import remote.wise.util.IstGmtTimeConversion;

public class HMRDetailsServiceImpl {

	public HashMap<String, Object> getHMRDetails(String vin) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		HashMap<String, Object> HMRDetailsMap = new HashMap<String, Object>();
		CommonUtil util = new CommonUtil();
		if(vin==null||"".equals(vin)||vin.contains(" ")){
			fLogger.info("MESSAGE - Invalid vin number :");	
			HMRDetailsMap.put("MESSAGE", " - Invalid vin number :"+vin);
			 return HMRDetailsMap;
		}
		String isValidinput = util.inputFieldValidation(vin);
		if(!isValidinput.equals("SUCCESS")){
			fLogger.info("MESSAGE - Invalid vin number :");	
			HMRDetailsMap.put("MESSAGE", " - Invalid vin number :"+vin);
			return HMRDetailsMap;
		}
		else if(!checkVinList(vin))
		{
			//HMRDetailsMap.put("Updates not available for the VIN :", vin);
			HMRDetailsMap.put("MESSAGE", " - Vin not registered :"+vin);
			HMRDetailsMap.put("status", false);
			return HMRDetailsMap;
		}
		ConnectMySQL connMySql = new ConnectMySQL();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = connMySql.getConnection();
			stmt = conn.createStatement();
			
			String query = "select * from (select distinct Serial_Number,TxnData ->>'$.CMH' as CMH, txndata ->> '$.LAT' AS LAT,txndata ->> '$.LONG' AS LONGT,transactionTime from "
					+ "( select ams.Serial_Number, ams.Latest_Transaction_Timestamp as transactionTime, ams.Latest_Created_Timestamp, ams.TxnData, "
					+ "a.Engine_Number, a.Product_ID, a.timeZone, a.country_code from asset_monitoring_snapshot ams "
				    + "inner join VINLIST_BOT bot on(ams.Serial_Number=bot.vinList) "
					+ "inner join asset_owner_snapshot aos on(ams.Serial_Number like '%"+ vin + "%'"
					+ " and ams.Latest_Transaction_Timestamp >= aos.Ownership_Start_Date  ) "
					+ "inner join asset a on ( a.Serial_Number = ams.Serial_Number and a.Status= 1 )"
					+ " where ams.Latest_Transaction_Timestamp > '2014-01-01 00:00:00' "
					+ "order by ams.Latest_Transaction_Timestamp desc  LIMIT 0,50 ) as ams1 "
					+ "inner join asset_profile ap on ap.serialNumber = ams1.Serial_Number"
					+ " left outer join ( select p.Product_ID, ag.Asseet_Group_Name, aty.Asset_Type_Name, aty.Asset_ImageFile_Name, "
					+ "et.Engine_Type_Name from products p inner join asset_group ag on ag.Asset_Group_ID = p.Asset_Group_ID inner join "
					+ "asset_type aty on aty.Asset_Type_ID = p.Asset_Type_ID inner join engine_type et on et.Engine_Type_id = p.Engine_Type_id) bb"
					+ " on ams1.Product_ID=bb.Product_ID "
					+ " left outer join asset_event_snapshot aes on ams1.Serial_Number=aes.serialNumber"
					+ " ORDER BY ams1.transactionTime desc) dat "
					+ " left outer join service_details_report sdr on ( dat.serial_number = sdr.serial_number )";
			
			iLogger.info("Query for HMRDetailsService::getHMRDetails: "+query);
			rs = stmt.executeQuery(query);
			
			//20191511: @Mamatha HMR Changes for Bot
			if (rs.next()) {
				String CMH = (String) rs.getObject("CMH");
				String LAT = (String) rs.getObject("LAT");
				String LONG = (String) rs.getObject("LONGT");
				String address = getAddressDetails(vin, LAT, LONG);
				String nextServiceDate = (rs.getString("Next_Service_Date") == null) ? "NA" : rs.getString("Next_Service_Date").substring(0, 10);
				//20192211: @Mamatha HMR Changes for Bot
				IstGmtTimeConversion gmtIstConv = new IstGmtTimeConversion();
				Timestamp latestReceivedTime = gmtIstConv.convertGmtToIst((Timestamp)rs.getObject("transactionTime"));
				String latestReceivedTimeString = latestReceivedTime.toString();
				
				HMRDetailsMap.put("status", true);
				HMRDetailsMap.put("CMH", CMH);
				HMRDetailsMap.put("Address", address);
				HMRDetailsMap.put("NextServiceDate", nextServiceDate);
				HMRDetailsMap.put("LatestTransTimestamp", latestReceivedTimeString);
				iLogger.info("HMRDetailsServiceImpl:getHMRDetails: Serial Number :"
						+ vin
						+ "CMH :"
						+ CMH
						+ ":Address :"
						+ address
						+ ":NextServiceDate :" + nextServiceDate
						+ ":LatestTransTimestamp :" + latestReceivedTimeString);
			}
			else 
			{
				HMRDetailsMap.put("MESSAGE", " - VIN Details not found for :"+ vin);
				HMRDetailsMap.put("status", false);
			}
			return HMRDetailsMap;

		} catch (Exception e) {
			fLogger.fatal("HMRDetailsServiceImpl:getHMRDetails: FromMysql:::Exception"
					+ e.getMessage());
			System.out.println(e.getMessage());
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace()  ;
				}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		return HMRDetailsMap;

	}

	//20191511: @Mamatha method to get the address
	public String getAddressDetails(String vinNo, String lat, String longt) {
		//LocationDetails locObj = null;
		LocationDetailsMMI locObj = null;
		try {
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream(
					"remote/wise/resource/properties/configuration.properties"));
			String redisURL = prop.getProperty("geocodingredisurl");
			String redisPORT = prop.getProperty("geocodingredisport");
			Jedis redisConnection = new Jedis(redisURL,
					Integer.valueOf(redisPORT));
			
			// Leela - Commenting bcz using mmi instead of google
			//locObj = GetSetLocationJedis.getLocationDetails(lat, longt,redisConnection);
			locObj = GetSetLocationJedis.getLocationDetailsMMI(lat, longt);
		} catch (Exception ex) {
			ex.getMessage();
		}
		return locObj.getAddress();
	}

	//20191511: @Mamatha method to get the vinList
	private boolean checkVinList(String vin) {
		
		boolean status = false;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		ConnectMySQL connMySql = new ConnectMySQL();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			conn = connMySql.getConnection();
			stmt = conn.createStatement();
			String query = "select * from VINLIST_BOT where vinList like '%"+ vin + "%'";
			rs = stmt.executeQuery(query);
			status = rs.next();

		} catch (Exception e) {
			fLogger.fatal(" Exception in retriving data from Vin List:" + e);

		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace()  ;
				}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		long endTime = System.currentTimeMillis();
		iLogger.info("Time required to fetch the record from Vin List Vin:" + vin + " in ms :" + (endTime - startTime));
		return status;
	}
	
}
