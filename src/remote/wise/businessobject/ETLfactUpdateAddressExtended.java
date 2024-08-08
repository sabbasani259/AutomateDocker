/**
 * 
 */
package remote.wise.businessobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.LocationDetails;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.GetSetLocationJedis;

/**
 * @author ROOPN5
 *
 */
public class ETLfactUpdateAddressExtended implements Runnable{

	int seg_ID=0;
	Thread t;
/*	private static final String HOST = "localhost";
	private static final int PORT = 29000;*/
	List<String> stateList;
	public ETLfactUpdateAddressExtended(){

	}

	public ETLfactUpdateAddressExtended(int segID,List<String> stateList){

		t= new Thread(this, "Segment Specific UpdateAddress Call");
		this.seg_ID=segID;
		this.stateList=stateList;

		t.start();

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		processSegmentSpecificData();
	}

	public String processSegmentSpecificData(){

		String response="SUCCESS";


		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;


		Connection prodConnection = null;
		Statement statement = null;
		Statement statement1 = null;
		ResultSet rs = null;
		String serial_Number=null;
		String time_key=null;
		String loc=null;
		String city=null;
		String state=null;
		String address=null;
		//CR<xxxx>.sn
		String redisIp=null;
		String redisPort=null;
		Properties prop = null;
		try{
			prop = CommonUtil.getDepEnvProperties();
			redisIp = prop.getProperty("geocodingredisurl");
			redisPort = prop.getProperty("geocodingredisport");
		}catch(Exception e){
			fLogger.fatal("ETLfactUpdateAddressExtended : processSegmentSpecificData : " +
					"Exception in getting Server detail for Redis from properties file: " +e);
		}
		//CR<xxxx>.en

		try{

			long conStartTime = System.currentTimeMillis();
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getETLConnection();
			statement = prodConnection.createStatement();
			statement1 = prodConnection.createStatement();
			long conEndTime = System.currentTimeMillis();
			iLogger.info("ETLFactBO : updateAddress :MySQL Connection time:" +
					""+(conEndTime-conStartTime));

			//int offset = 0;
			//int counterNoRowsUpdated= offset;
			Jedis redisPool = null;
			try
			{
				//redisPool = new Jedis(HOST,PORT);
				redisPool = new Jedis(redisIp,Integer.parseInt(redisPort));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("ETLFactBO : updateAddress :Jedis Connection Exception:"+e.getMessage());
			}
			int updateneeded = 0;



			if(prodConnection==null || prodConnection.isClosed()){
				prodConnection = connMySql.getETLConnection();
				statement = prodConnection.createStatement();
				statement1 = prodConnection.createStatement();
			}
			//Df20171213 @Roopa taking the records by each segment and commiting it
			//String updateAddressQuery="select * from remote_monitoring_fact_data_dayagg_json_new where address is null limit 1000";
			
			
			//DF20180212 @Roopa iterating individual segment loop until the null count is zero.
			
			boolean loopTableFlag = true;
			
			iLogger.info("segementIDThread::loopTableFlag::"+seg_ID+":"+loopTableFlag);
			
			while (loopTableFlag) {
				if(prodConnection==null || prodConnection.isClosed()){
					prodConnection = connMySql.getETLConnection();
					statement = prodConnection.createStatement();
					statement1 = prodConnection.createStatement();
				}
				
			String updateAddressQuery="select * from remote_monitoring_fact_data_dayagg_json_new where Segment_ID_TxnDate="+seg_ID+" and address is null";

			rs = statement.executeQuery(updateAddressQuery);

			if(!rs.next()){
					loopTableFlag = false;
				}else{
					rs.beforeFirst();}

			long startTime  = System.currentTimeMillis();
			while (rs.next()) {

				//counterNoRowsUpdated++;


				serial_Number=rs.getString("Serial_Number");
				time_key=rs.getString("Time_Key");
				loc=rs.getString("Location");

				String location[];
				String latitude = null;
				String longitude = null;
				iLogger.info("ETLFactBO : updateAddress "+" Serial_Number "+serial_Number+" time_key "+time_key);
				try{
					if(loc!=null){
						location = loc.split(",");
						latitude = location[0];
						longitude = location[1];
					}
				}catch (Exception e) {
					fLogger.fatal("ETLFactBO : updateAddress "+" Serial_Number "+serial_Number+" time_key "+time_key+" Exception when splitting location "+e.getMessage());
				}
				LocationDetails locObj=null;

				try{
					locObj = GetSetLocationJedis.getLocationDetails(latitude,longitude, redisPool);
				}
				catch (Exception e) {
					fLogger.fatal("ETLFactBO : updateAddress "+" Serial_Number "+serial_Number+" time_key "+time_key+" Exception while getting the location from GEOCoding service"+e.getMessage());
				}

				if(locObj!=null){

					iLogger.info("Address is:::::"+locObj+" for "+latitude+" "+longitude);
					// exception scenario by osm/google by shrini 201605041040
					//if(aggregate.equalsIgnoreCase("AssetMonitoringFactDataDayAgg_json")){

					if(locObj.getCity()!=null){
						city=locObj.getCity();
					}else{
						loc="undefined";
					}

					if(locObj.getState()!=null){
						if(stateList.contains(locObj.getState())){
							state=locObj.getState();
						}else{
							state="outside india";
						}
					}else{
						state="undefined";
					}
					//}

					if(locObj.getAddress()!=null){
						address=locObj.getAddress();

						if(address.contains("'")){
							address=address.replace("'", "");	
						}
					}else{
						address="undefined";
					}


					updateneeded++;


					statement1.addBatch("update remote_monitoring_fact_data_dayagg_json_new set Address='"+address+"',City='"+city+"',State='"+state+"' where Serial_Number='"+serial_Number+"' and Time_Key='"+time_key+"'");
				}
				else{
					iLogger.info("ETLFactBO : updateAddress "+" Serial_Number "+serial_Number+" time_key "+time_key+" locObj is null");
				}

				//iLogger.info("Number of updates done::::: "	+ counterNoRowsUpdated);
				iLogger.info("Number of rows updated after the execution for segment:::::"+seg_ID+":: "+ updateneeded);
			}
			//offset = offset + 500;

			try{
				int[] updateCount=statement1.executeBatch();

				statement1.clearBatch();
			}
			catch (Exception e) {
				fLogger.fatal("Exception in executing the batch "+e.getMessage());
			}
			
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			if(statement1!=null){
				try {
					statement1.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			iLogger.info("Time taken to loop Segment::"+seg_ID+ "is"+(System.currentTimeMillis()-startTime));
			}

			//segementIDThread++;
			
		
			try{
				
				if(redisPool!=null)
					redisPool.disconnect();

			}catch (Exception e) {
				fLogger.fatal("Exception while disconnecting redis pool "+e.getMessage());
			}
		

		}

		catch (Exception e){ 
			fLogger.fatal("Exception while connecting Database "+ e.getMessage());
			e.printStackTrace();
		} 

		finally{

			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			if(statement1!=null){
				try {
					statement1.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}


		return response;
	}
}
