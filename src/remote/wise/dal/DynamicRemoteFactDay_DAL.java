/**
 * 
 */
package remote.wise.dal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.businessentity.AssetMonitoringFactDataEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.AMSDoc_DAO;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.GmtLtTimeConversion;
import remote.wise.util.StaticProperties;

/**
 * @author ROOPN5
 *
 */
public class DynamicRemoteFactDay_DAL {

	public String getTimeKey(String Query) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;


		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;

		String timeKey=null;



		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			fLogger.fatal("DynamicRemoteFactDay_DAL:getTimeKey "+e.getMessage());
		}


		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info("DynamicRemoteFactDay_DAL:getTimeKey"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{

			/*<!=========START fetching from Native Database==============>*/

			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getETLConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(Query);


				while(rs.next()){
					timeKey=rs.getObject("Time_Key").toString();
				}
			}

			catch (SQLException e) {

				e.printStackTrace();
				fLogger.fatal("DynamicRemoteFactDay_DAL:getTimeKey"+e.getMessage());
			} 

			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("DynamicRemoteFactDay_DAL:getTimeKey"+e.getMessage());
			}

			finally {
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

			}



			/*<!=========END fetching from Native Database==============>*/


		}


		return timeKey;
	}


	public String updateRemoteFactDetails(AssetMonitoringFactDataEntity assetMonitoringFactDataEntity, boolean update, int seg_ID, int tenancy_id,int acc_id,int asset_class_id) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;


		Connection prodConnection = null;
		Statement statement = null;

		String remoteFactQuery=null;

		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			fLogger.fatal("DynamicRemoteFactDay_DAL:updateRemoteFactDetails "+e.getMessage());
		}


		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info("DynamicRemoteFactDay_DAL:updateRemoteFactDetails"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{

			/*<!=========START fetching from Native Database==============>*/
			try{
			if(update){
				remoteFactQuery="UPDATE remote_monitoring_fact_data_dayagg_json_new" +
						" SET Last_Engine_Run='"+assetMonitoringFactDataEntity.getLastEngineRun()+"',"
						+ "Last_Reported='"+assetMonitoringFactDataEntity.getLastReported()+"',"
						+ "Location='"+assetMonitoringFactDataEntity.getLocation()+"',"
						+ "Machine_Hours='"+assetMonitoringFactDataEntity.getMachineHours()+"',"
						+ "Engine_Off_Hours='"+assetMonitoringFactDataEntity.getEngineOffHours()+"',"
						+ "EngineRunningBand1='"+assetMonitoringFactDataEntity.getEngineRunningBand1()+"',"
						+ "EngineRunningBand2='"+assetMonitoringFactDataEntity.getEngineRunningBand2()+"',"
						+ "EngineRunningBand3='"+assetMonitoringFactDataEntity.getEngineRunningBand3()+"',"
						+ "EngineRunningBand4='"+assetMonitoringFactDataEntity.getEngineRunningBand4()+"',"
						+ "EngineRunningBand5='"+assetMonitoringFactDataEntity.getEngineRunningBand5()+"',"
						+ "EngineRunningBand6='"+assetMonitoringFactDataEntity.getEngineRunningBand6()+"',"
						+ "EngineRunningBand7='"+assetMonitoringFactDataEntity.getEngineRunningBand7()+"',"
						+ "EngineRunningBand8='"+assetMonitoringFactDataEntity.getEngineRunningBand8()+"',"
						+ "FuelUsedIdle='"+assetMonitoringFactDataEntity.getFuelUsedIdle()+"',"
						+ "FuelUsedWorking='"+assetMonitoringFactDataEntity.getFuelUsedWorking()+"',"
						+ "Machine_Name='"+assetMonitoringFactDataEntity.getMachineName()+"',"
					    + "Engine_Status='"+assetMonitoringFactDataEntity.getEngineStatus()+"',"
						+ "Created_Timestamp='"+assetMonitoringFactDataEntity.getCreated_Timestamp()+"',"
						+ "agg_param_data='"+assetMonitoringFactDataEntity.getAggregate_param_data()+"'"
						+ " where Segment_ID_TxnDate="+seg_ID+" and Serial_Number='"+assetMonitoringFactDataEntity.getSerialNumber()+"' and Time_Key='"+assetMonitoringFactDataEntity.getTimeKey()+"'";

			}
			else{
				remoteFactQuery="Insert into remote_monitoring_fact_data_dayagg_json_new(Tenancy_ID,Time_Key,Account_ID,Serial_Number,Last_Engine_Run,Last_Reported,Location,Machine_Hours,Engine_Off_Hours,Asset_Class_ID,EngineRunningBand1,EngineRunningBand2,EngineRunningBand3,EngineRunningBand4,EngineRunningBand5,EngineRunningBand6,EngineRunningBand7,EngineRunningBand8,FuelUsedIdle,FuelUsedWorking,Machine_Name,Engine_Status,Created_Timestamp,agg_param_data,Segment_ID_TxnDate)"+
						" VALUES("+tenancy_id+","
						+ "'"+assetMonitoringFactDataEntity.getTimeKey()+"',"
						+ ""+acc_id+","
						+ "'"+assetMonitoringFactDataEntity.getSerialNumber()+"',"
						+ "'"+assetMonitoringFactDataEntity.getLastEngineRun()+"',"
						+ "'"+assetMonitoringFactDataEntity.getLastReported()+"',"
						+ "'"+assetMonitoringFactDataEntity.getLocation()+"',"
						+ "'"+assetMonitoringFactDataEntity.getMachineHours()+"',"
						+ "'"+assetMonitoringFactDataEntity.getEngineOffHours()+"',"
						+ ""+asset_class_id+","
						+ "'"+assetMonitoringFactDataEntity.getEngineRunningBand1()+"',"
						+ "'"+assetMonitoringFactDataEntity.getEngineRunningBand2()+"',"
						+ "'"+assetMonitoringFactDataEntity.getEngineRunningBand3()+"',"
						+ "'"+assetMonitoringFactDataEntity.getEngineRunningBand4()+"',"
						+ "'"+assetMonitoringFactDataEntity.getEngineRunningBand5()+"',"
						+ "'"+assetMonitoringFactDataEntity.getEngineRunningBand6()+"',"
						+ "'"+assetMonitoringFactDataEntity.getEngineRunningBand7()+"',"
						+ "'"+assetMonitoringFactDataEntity.getEngineRunningBand8()+"',"
						+ "'"+assetMonitoringFactDataEntity.getFuelUsedIdle()+"',"
						+ "'"+assetMonitoringFactDataEntity.getFuelUsedWorking()+"',"
						+ "'"+assetMonitoringFactDataEntity.getMachineName()+"',"
						//+ "'"+assetMonitoringFactDataEntity.getAddress()+"',"
						+ "'"+assetMonitoringFactDataEntity.getEngineStatus()+"',"
						//+ "'"+assetMonitoringFactDataEntity.getState()+"',"
						//+ "'"+assetMonitoringFactDataEntity.getCity()+"',"
						+ "'"+assetMonitoringFactDataEntity.getCreated_Timestamp()+"',"
						+ "'"+assetMonitoringFactDataEntity.getAggregate_param_data()+"',"
						+ ""+seg_ID+")";

			}

			//System.out.println("DynamicRemoteFactDay_DAL query::"+remoteFactQuery);

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getETLConnection();
				statement = prodConnection.createStatement();

				int insertCount= statement.executeUpdate(remoteFactQuery);


			}

			catch (SQLException e) {

				e.printStackTrace();
				fLogger.fatal("DynamicRemoteFactDay_DAL:updateRemoteFactDetails"+e.getMessage());
				return "FAILURE";
			} 

			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("DynamicRemoteFactDay_DAL:updateRemoteFactDetails"+e.getMessage());
				return "FAILURE";
			}

			finally {


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

			}



			/*<!=========END fetching from Native Database==============>*/


		}


		return "SUCCESS";
	}
	
	public static List<AMSDoc_DAO> getAMSData(String txnKey, String Serial_Number) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;

		String AMSSelectQuery=null;

		List<AMSDoc_DAO>AmsDAOList=new ArrayList<AMSDoc_DAO>();


		Properties prop=null;
		try
		{
			prop = CommonUtil.getDepEnvProperties();
		}
		catch(Exception e)
		{
			fLogger.fatal(txnKey+":AMS:DAL-AMS-getAMsData"+"Error in intializing property File :"+e.getMessage());
		}


		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info(txnKey+":AMS:DAL-AMS-getAMsData"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{

			/*<!=========START fetching from Native Database==============>*/



			if(Serial_Number!=null){
				//AMSSelectQuery="Select * from asset_monitoring_snapshot " + "where Serial_Number='"+Serial_Number+"'";
				
				//DF20170803: SU334449 - TimeZone is passed from Asset table for the VIN depending on the SAARC regions
				AMSSelectQuery="SELECT ams.TxnData, ams.Events, a.timeZone, ams.Latest_Created_Timestamp, ams.Serial_Number, ams.Transaction_Timestamp_Evt, " +
							   "ams.Transaction_Timestamp_Fuel, ams.Transaction_Timestamp_Log, " +
						       "ams.Latest_Transaction_Timestamp FROM asset_monitoring_snapshot " +
						       "ams join asset a on ams.Serial_Number = a.Serial_Number " +
						       "where ams.Serial_Number ='"+Serial_Number+"'";
				
				iLogger.info(txnKey+":AMS:DAL-AMS-getAMsData"+"AMSSelectQuery::"+AMSSelectQuery);
			}

			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getETLConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(AMSSelectQuery);

				String txnData = null;
				String events = null;
				
				GmtLtTimeConversion convertedTime = new GmtLtTimeConversion();

				HashMap<String,String> txnDataMap=new HashMap<String, String>();

				HashMap<String,String> eventsMap = new HashMap<String, String>();

				AMSDoc_DAO amsDAOobject;
				while(rs.next()){

					amsDAOobject=new AMSDoc_DAO();
					
					if(rs.getObject("TxnData")!=null)
                    txnData=rs.getObject("TxnData").toString();
					
					if(rs.getObject("Events")!=null){

					events=rs.getObject("Events").toString();
					}

					if(txnData!=null)
					txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());

					if(events!=null)
					eventsMap = new Gson().fromJson(events, new TypeToken<HashMap<String, Object>>() {}.getType());

					amsDAOobject.setTxnData(txnDataMap);
					amsDAOobject.setEvents(eventsMap);

					String timeZone = rs.getString("timeZone");
					amsDAOobject.setLatest_Created_Timestamp(String.valueOf(rs.getTimestamp("Latest_Created_Timestamp")));

					amsDAOobject.setSerial_Number(rs.getString("Serial_Number"));

					amsDAOobject.setTransaction_Timestamp_Evt(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Evt")));
					amsDAOobject.setTransaction_Timestamp_Fuel(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Fuel")));
					
					// DF20170724: SU334449 - Converting Latest Transaction Time-stamp to local timeZones of the SAARC countries for the VIN 
					/*String latestCreatedTimestampLog = String.valueOf(rs.getTimestamp("Transaction_Timestamp_Log"));
					GmtLtTimeConversion convertedTime = new GmtLtTimeConversion();
					String timeStampLog = convertedTime.convertGmtToLocal(timeZone, latestCreatedTimestampLog);
					amsDAOobject.setTransaction_Timestamp_Log(timeStampLog);*/
					
					
					amsDAOobject.setTransaction_Timestamp_Log(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Log")));
					
					//Df20171115 @Roopa doing Timezone based conversion for LatestTS
					
					String latestTS=String.valueOf(rs.getTimestamp("Latest_Transaction_Timestamp"));
					
					if(latestTS!=null && !latestTS.equalsIgnoreCase("null")){
						amsDAOobject.setLatest_Transaction_Timestamp(convertedTime.convertGmtToLocal(timeZone, latestTS));
					}
					else{

					amsDAOobject.setLatest_Transaction_Timestamp(String.valueOf(rs.getTimestamp("Latest_Transaction_Timestamp")));
					}

					AmsDAOList.add(amsDAOobject);
				}
			}

			catch (SQLException e) {

				e.printStackTrace();
				fLogger.fatal(txnKey+":AMS:DAL-AMS-getAMsData"+"SQL Exception in fetching data from mysql::"+e.getMessage());
			} 

			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal(txnKey+":AMS:DAL-AS-getAMsData"+"Exception in fetching data from mysql::"+e.getMessage());
			}

			finally {
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

			}



			/*<!=========END fetching from Native Database==============>*/


		}


		return AmsDAOList;
	}

}
