/**
 * CR337 : 20220721 : Dhiraj K : Property file read.
 */
package remote.wise.service.webservice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import redis.clients.jedis.Jedis;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;

import java.net.URL;

@Path("/UpdateCMHForMachinesService")
public class UpdateCMHForMachinesService {

	@GET
	@Path("updateCMHData")
	@Produces(MediaType.TEXT_HTML)
	public String updateCMHData() {

		String status = "SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		iLogger.info("Web Service Start::");
		long startTime = System.currentTimeMillis();
		
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		Jedis jedis=null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date todayDate = new Date();
		Calendar cal = Calendar.getInstance();
		
		try{
			cal.setTime(todayDate);
			cal.add(Calendar.DATE, -1);
			Date yesterDate = cal.getTime();

			String assetQuery = "select a.serial_number,ams.TxnData-> '$.CMH' as cmh,ams.TxnData-> '$.FW_VER' as firmware_version, a.rolloff_date from asset a, asset_monitoring_snapshot ams where date(a.rolloff_date) = '"+sdf.format(yesterDate)+"' and a.serial_number = ams.serial_number";
			
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(assetQuery);
			
			//uptime redis url
			//Dhiraj K : 20220630 : change in IP and prt for aws server
			//String redisURL = "10.179.12.13";
			//String redisURL = "localhost";
			//int redisPORT = 29000;
			//CR337.sn
	    	String redisIp = "localhost";
	    	String redisPort = null;
	    	Properties prop = null;
			try{
				prop = CommonUtil.getDepEnvProperties();
	    		//redisIp = prop.getProperty("geocodingredisurl");
				redisPort = prop.getProperty("geocodingredisport");
				iLogger.info("UpdateCMHForMachinesService:redisIp" + redisIp + " :: redisPort" +redisPort);
	    	}catch(Exception e){
	    		fLogger.fatal("UpdateCMHForMachinesService : updateCMHData : " +
	    				"Exception in getting Redis Host server detail from properties file: " +e.getMessage());
	    	}
	    	//CR337.en
			
			jedis = new Jedis(redisIp,Integer.parseInt(redisPort));
			
			String serial_number = null,CMHString = null;
			double CMH = 0.0;
			String FWVersion = null;
			String rollOffDate = null;
			HashMap<String, String> redisData = new HashMap<String, String>();
			while(rs.next())
			{
				CMHString = rs.getString("CMH");
				serial_number = rs.getString("serial_number");
				FWVersion = rs.getString("firmware_version");
				//rollOffDate = rs.getString("rolloff_date");
				//DF20181220-Removing trailing zero from the time string.
				if(rs.getString("rolloff_date") != null)
					rollOffDate = rs.getString("rolloff_date").toString().substring(0,19);

				if(CMHString != null && FWVersion != null){
					FWVersion = FWVersion.replaceAll("\"", "");
					CMHString = CMHString.replaceAll("\"", "");
					CMH = Double.parseDouble(CMHString);
					
					if(CMH > 20 && FWVersion.equalsIgnoreCase("09.01.02")){
						redisData.put(serial_number, rollOffDate);
					}

					//DF20191204:Abhishek::To add 933 machines.
					if(CMH > 20 && FWVersion.equalsIgnoreCase("09.03.03")){
						OutputStream os = null;
						String output = null;
						BufferedReader br = null;
						String result = null;
						String parameterValue = "002##003##004##005##006##007###008####009##010##011#####012####013#014#####015###016####017####################################################################################################################018#019000000000005020################021###022###023##024#025#026################################################################027################################################################028########################################################029########################################################030####################################031####################################032####################################033####################################034####################################";
						
						redisData.put(serial_number, rollOffDate);
						JSONObject json=new JSONObject();
						json.put("vin", serial_number);
						json.put("value", parameterValue);
						String request=json.toString();
						String URL = "http://localhost:26000/EPUtilities/configurationParameterService/add";
					 	iLogger.info("updateCMHData():UpdateCMHForMachinesService.java::---> Inside 933 code URL : "+URL+" serial_number :"+serial_number+" parameterValue: "+parameterValue+" Request :"+request);

						URL url = new URL(URL);
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					 	connection.setDoOutput(true);
					 	connection.setRequestMethod("POST");
					 	connection.setRequestProperty("Content-Type", "application/json");
					 	os = (OutputStream) connection.getOutputStream();
					 	os.write(request.getBytes());
					 	os.flush();
					 	iLogger.info("updateCMHData():UpdateCMHForMachinesService.java::---> HTTP code from EPUtilites :"+connection.getResponseCode());
					 	if (connection.getResponseCode() != 200) {
					 		output="FAILURE";
					 		fLogger.fatal("updateCMHData():UpdateCMHForMachinesService.java:: Failed : HTTP error code :  Response: "+connection.getResponseCode()+" :Exception caused because of no response from MOOLDA URL:"+URL+" Request:"+request+" output:"+output);
							//throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
							return output;
					 	 }
					 	br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
					 	while ((result = br.readLine()) != null) {
					 		 output = result.toUpperCase();
					 	}
					 	 iLogger.info("updateCMHData():UpdateCMHForMachinesService.java:: serial_number :"+serial_number+"  URL:"+URL+" Request:"+request+" output:"+output);

					}
					
				}
				CMH = 0.0;
			}
			
			long endTime = System.currentTimeMillis();
			iLogger.info("serviceName:UpdateCMHForMachinesService~executionTime:"+(endTime-startTime)+"~"+""+"~"+status);
			if(redisData.size() > 0)
				jedis.hmset("CMHVINDETAILS", redisData);

		}catch (Exception e) {
			// TODO: handle exception
			fLogger.fatal("Exception Caught ::"+e.getMessage());
			e.printStackTrace();
		}
		finally{

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

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			try{
				if(jedis != null && jedis.isConnected()){
					jedis.disconnect();
				}
			}
			catch(Exception e){
				fLogger.fatal("Exception while closing Redis Connection "+e.getMessage());
			} 
		}
		
		return status;
	}
	
}
