/**
 * 
 */
package remote.wise.service.webservice;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.Logger;

import redis.clients.jedis.Jedis;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;

/**
 * @author KPRABHU5
 *
 */

@Path("/DataAnonimizationService")
public class DataAnonimizationService {
	
	Logger fLogger = FatalLoggerClass.logger;
	Logger iLogger = InfoLoggerClass.logger;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	@GET()
	@Path("/anonimizeData")
	@Produces("text/plain")
	public String anonimizeData(){
		String status = "anonimized !!!!!";
		HashMap<String, String> dataMap = null;
		Connection connection = null, connectionEP = null;; Statement statement = null, statement2 = null, statementEP = null; ResultSet rs =null, rs2 =null;
		
		String serialNumber = null, machineNumber = null, morphedVIN = null, engineNumber = null, chasisNumber = null, make = null, builtDate = null ;
		String SIMNumber = null, IMEINumber = null, registrationDate = null, ICCID = null;
		String currentTimestamp = null; boolean found = false;String processingStatus = null;
		
		int count = 0;
//		read table where added_status = 1; 
		ConnectMySQL connFactory = new ConnectMySQL();
		try {
			connection = connFactory.getConnection();
			connectionEP = connFactory.getEdgeProxyConnection();
			
			statement = connection.createStatement();
			statement2 = connection.createStatement();
			statementEP = connectionEP.createStatement();
			
			currentTimestamp = sdf.format(new Date());
			
			String selectQuery = "SELECT serial_number, SIM_No, IMEI_Number, Registration_Date, ICCID, machine_number," +
					"morphed_VIN, engine_number, chasis_number, make, built_date FROM prod_anonimize_data WHERE added_status = 1";
			rs = statement.executeQuery(selectQuery) ;
			iLogger.info("selectQuery:"+selectQuery);
			
			while (rs.next()) {
				serialNumber = null; machineNumber = null; morphedVIN = null; engineNumber = null; chasisNumber = null; make = null; builtDate = null;
				SIMNumber = "NA"; IMEINumber = "NA"; registrationDate = "NA"; ICCID = "NA"; found = false; processingStatus = "FAILURE"; count = 0;
				serialNumber = rs.getString("serial_number");				
				if(serialNumber != null && serialNumber.length()==17){
					
					if(dataMap == null){
						dataMap = new HashMap<String, String>();
					}
//					insert to ACU
					if(rs.getObject("SIM_No") != null){
						SIMNumber = rs.getString("SIM_No");
					}
					if(rs.getObject("IMEI_Number") != null){
						IMEINumber = rs.getString("IMEI_Number");
					}
					if(rs.getObject("Registration_Date") != null){
						registrationDate = rs.getString("Registration_Date");
					}
					else{
						registrationDate = currentTimestamp;
					}
					if(rs.getObject("ICCID") != null){
						ICCID = rs.getString("ICCID");
					}
					if(rs.getObject("morphed_VIN") != null){
						morphedVIN = rs.getString("morphed_VIN");
					}
					else{
						morphedVIN = "BLR"+ serialNumber.substring(3);
					}
					String query = "SELECT * FROM asset_control_unit WHERE Serial_Number='"+ morphedVIN +"'";
					rs2 = statement2.executeQuery(query);
					while(rs2.next()){
						found = true;
					}
					if(found){
						query = "UPDATE asset_control_unit SET SIM_No='"+SIMNumber+"', IMEI_Number='"+IMEINumber+"', Registration_Date='"+registrationDate+"',ICCID='"+ICCID+"'" +
								" WHERE Serial_Number='"+morphedVIN+"'";
					}
					else{
						query = "INSERT INTO asset_control_unit(Serial_Number, Control_Unit_ID, SIM_No, IMEI_Number, Registration_Date, ICCID)"+
								" VALUES('"+morphedVIN+"',1,'"+SIMNumber+"','"+IMEINumber+"','"+registrationDate+"','"+ICCID+"')";
					}
								
					iLogger.info(serialNumber+"--ACU query:"+query);
					count = statement2.executeUpdate(query);
					iLogger.info(serialNumber+"--inserted/updated to ACU--count:"+count);
					
//					insert/update to 4306 device_status_info
					found = false; count = 0;
					query = "SELECT * FROM device_status_info WHERE vin_no='"+ morphedVIN +"'";
					rs2 = statementEP.executeQuery(query);
					while(rs2.next()){
						found = true;
					}
					if(found){
						query = "UPDATE device_status_info SET Registration_Date='"+registrationDate+"',IMEI_number='"+IMEINumber
						         +"',SIM_No='"+SIMNumber+"',ICCID='"+ICCID+"', update_wise_status = 1 WHERE vin_no='"+morphedVIN+"'";
					}
					else{
						query = "INSERT INTO device_status_info(vin_no,Registration_Date,IMEI_number,SIM_No,ICCID,update_wise_status) VALUES" +
								"('"+morphedVIN+"','"+registrationDate+"','"+IMEINumber+"','"+SIMNumber+"','"+ICCID+"',1)";
					}
								
					iLogger.info(serialNumber+"--DSI query:"+query);
					count = statementEP.executeUpdate(query);
					iLogger.info(serialNumber+"--inserted/updated to DSI--count:"+count);
					
					if(rs.getObject("machine_number") != null){
						machineNumber = rs.getString("machine_number");
					}
					else{
						machineNumber = serialNumber.substring(10);
					}					
					
					if(rs.getObject("engine_number") != null){
						engineNumber = rs.getString("engine_number");
					}
					else{
						engineNumber = "H"+ serialNumber.substring(10);
					}
					if(rs.getObject("chasis_number") != null){
						chasisNumber = rs.getString("chasis_number");
					}
					else{
						chasisNumber = morphedVIN;
					}
					if(rs.getObject("make") != null){
						make = rs.getString("make");
					}
					else{
						make = "2020";
					}
					if(rs.getObject("built_date") != null){
						builtDate = rs.getString("built_date");
					}
					else{
						builtDate = sdf.format(new Date());
					}
					
//					invoke roll-off service
					String processingTime = sdf.format(new Date());
					String fileRef = "JCBRollOff_anonimization_"+sdf2.format(new Date());
					
					try{
						processingStatus = new JcbRollOffService().setVinMachineNameMapping(morphedVIN, engineNumber, chasisNumber, "MSG"+fileRef, fileRef, "JCBRollOff", "RJCBRollOff", make, builtDate, machineNumber);
						iLogger.info(serialNumber+"--"+morphedVIN+"--called roll off--processingStatus:"+processingStatus);
					}
					catch(Exception e){
						fLogger.error(serialNumber+"--"+morphedVIN+"--error during roll off--",e);
					}					
					dataMap.put(serialNumber, morphedVIN+"~~"+fileRef+"~~"+processingTime+"~~"+processingStatus);
				}	
			}
			if(dataMap != null){
				statement = connection.createStatement();
				for(String VIN : dataMap.keySet()){
					if(dataMap.get(VIN) != null){
						String[] proessedValuesArr = dataMap.get(VIN).split("~~");
						statement.executeUpdate("UPDATE prod_anonimize_data SET added_status=0, description = 'processed', morphed_VIN='"+proessedValuesArr[0]+							
								"', fileRef = '" + proessedValuesArr[1] + "', " +
								"processing_time='" + proessedValuesArr[2] + "', processing_status ='" + proessedValuesArr[3] +
								"' WHERE serial_number = '" +VIN + "'");
					}
						
				}
					
			}
		} catch (Exception e) {
			status = "failed";
			e.printStackTrace();
			fLogger.fatal("error in data anonimization ", e);
		}
		finally{
			try{
				if(rs != null){rs.close();}
				if(rs2 != null){rs2.close();}
				if(statement != null){statement.close();}
				if(statement2 != null){statement2.close();}
				if(statementEP != null){statementEP.close();}
				if(connection != null){connection.close();}
				if(connectionEP != null){connectionEP.close();}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
		}
		return status;
	}
	
	@GET()
	@Path("/populateRedisWithAnonimizedData")
	@Produces("text/plain")
	public String populateRedisWithAnonimizedData(){
		String status = "populatedRedisWithAnonimizedData";
		iLogger.info(sdf2.format(new Date())+"--publish anonimized data to redis--START");
		Connection connection = null; Statement statement = null; ResultSet rs =null;
		HashMap<String, String> VINDetails = new HashMap<String, String>();
		
		//CR337.sn
		String ep1host=null; String ep2host=null; String ep3host=null; 
		String ep4host=null; String ep5host=null; String epPort = null;
		Properties prop = null;
		try{
			prop = CommonUtil.getDepEnvProperties();
			ep1host = prop.getProperty("EP1ServerIP");
			ep2host = prop.getProperty("EP2ServerIP");
			ep3host = prop.getProperty("EP3ServerIP");
			ep4host = prop.getProperty("EP4ServerIP");
			ep5host = prop.getProperty("EP6ServerIP");
			epPort = prop.getProperty("EPServerPort");
		}catch(Exception e){
			fLogger.fatal("SMSTrigger : sendEmail : " +
					"Exception in getting SMTP Host server detail from properties file: " +e.getMessage());
		}
		//CR337.en
				
		Jedis jedis = null;
		
		try{
						
				ConnectMySQL connFactory = new ConnectMySQL();
				
					connection = connFactory.getConnection();
					statement = connection.createStatement();
					
					rs = statement.executeQuery("SELECT serial_number, morphed_VIN FROM prod_anonimize_data WHERE added_status = 0 and processing_status LIKE '%SUCCESS%'") ;
					while (rs.next()) {
						if(rs.getObject("serial_number") != null && rs.getObject("morphed_VIN") != null){
							VINDetails.put(rs.getString("serial_number"), rs.getString("morphed_VIN"));							
						}
						
					}
//					connect to local and put data
					// Dhiraj K : 20220630 : change in IP and prt for aws server
					//jedis = new Jedis("localhost", 6379);
					//jedis = new Jedis("10.210.196.206", 29000);
					jedis = new Jedis(ep1host, Integer.parseInt(epPort));//CR337
					if(jedis != null){
						for(String VIN: VINDetails.keySet()){
							jedis.hset("prod_anonimize_data", VIN, VINDetails.get(VIN));
						}
						jedis.disconnect();
						jedis = null;
					}	
					jedis = null;
					iLogger.info(sdf2.format(new Date())+"--published anonimized data to local redis");
					
//					connect to EP1 and put data
					// Dhiraj K : 20220630 : change in IP and prt for aws server
					//jedis = new Jedis("10.179.12.4", 6379);
					//jedis = new Jedis("10.210.196.206", 29000);
					jedis = new Jedis(ep1host, Integer.parseInt(epPort));//CR337
					if(jedis != null){
						for(String VIN: VINDetails.keySet()){
							jedis.hset("prod_anonimize_data", VIN, VINDetails.get(VIN));
						}
						jedis.disconnect();
						jedis = null;
					}	
					jedis = null;
					iLogger.info(sdf2.format(new Date())+"--published anonimized data to EP1 redis");
										
//					connect to EP2 and put data
					// Dhiraj K : 20220630 : change in IP and prt for aws server
					//jedis = new Jedis("10.179.12.5", 6379);
					//jedis = new Jedis("10.210.196.206", 29000);
					jedis = new Jedis(ep2host, Integer.parseInt(epPort));
					if(jedis != null){
						for(String VIN: VINDetails.keySet()){
							jedis.hset("prod_anonimize_data", VIN, VINDetails.get(VIN));
						}
						jedis.disconnect();
						jedis = null;
					}	
					jedis = null;
					iLogger.info(sdf2.format(new Date())+"--published anonimized data to EP2 redis");
					
//					connect to EP3 and put data
					// Dhiraj K : 20220630 : change in IP and prt for aws server
					//jedis = new Jedis("10.179.12.10", 6379);
					//jedis = new Jedis("10.210.196.206", 29000);
					jedis = new Jedis(ep3host, Integer.parseInt(epPort));//CR337
					if(jedis != null){
						for(String VIN: VINDetails.keySet()){
							jedis.hset("prod_anonimize_data", VIN, VINDetails.get(VIN));
						}
						jedis.disconnect();
						jedis = null;
					}	
					jedis = null;
					iLogger.info(sdf2.format(new Date())+"--published anonimized data to EP3 redis");
					
//					connect to EP4 and put data
					// Dhiraj K : 20220630 : change in IP and prt for aws server
					//jedis = new Jedis("10.179.12.11", 6379);
					//jedis = new Jedis("10.210.196.206", 29000);
					jedis = new Jedis(ep4host, Integer.parseInt(epPort));//CR337
					if(jedis != null){
						for(String VIN: VINDetails.keySet()){
							jedis.hset("prod_anonimize_data", VIN, VINDetails.get(VIN));
						}
						jedis.disconnect();
						jedis = null;
					}	
					jedis = null;
					iLogger.info(sdf2.format(new Date())+"--published anonimized data to EP4 redis");
				
//					connect to EP5 and put data
					// Dhiraj K : 20220630 : change in IP and prt for aws server
					//jedis = new Jedis("10.179.12.12", 6379);
					//jedis = new Jedis("10.210.196.206", 29000);
					jedis = new Jedis(ep5host, Integer.parseInt(epPort));//CR337
					if(jedis != null){
						for(String VIN: VINDetails.keySet()){
							jedis.hset("prod_anonimize_data", VIN, VINDetails.get(VIN));
						}
						jedis.disconnect();
						jedis = null;
					}	
					jedis = null;
					iLogger.info(sdf2.format(new Date())+"--published anonimized data to EP5 redis");
					iLogger.info(sdf2.format(new Date())+"--publish anonimized data to redis--END");
				
		}	
		catch(Exception e){
			status = "failed";
			e.printStackTrace();
		}
		finally{
			try{
				if(rs != null){rs.close();}
				if(statement != null){statement.close();}
				if(connection != null){connection.close();}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			if(jedis != null){
				jedis.disconnect();
			}
		}
		return status;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
