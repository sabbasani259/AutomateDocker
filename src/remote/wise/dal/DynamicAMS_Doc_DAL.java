/**
 * 
 */
package remote.wise.dal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.AMSDoc_DAO;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.GmtLtTimeConversion;
import remote.wise.util.StaticProperties;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * @author ROOPN5
 *
 */
public class DynamicAMS_Doc_DAL {



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
				
				iLogger.info(">>>>>"+txnKey+":AMS:DAL-AMS-getAMsData"+"AMSSelectQuery::"+AMSSelectQuery);
			}

			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
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
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				fLogger.fatal(txnKey+":AMS:DAL-AMS-getAMsData"+"SQL Exception in fetching data from mysql::"+e.getMessage());
				fLogger.fatal(stack.toString());
			} 

			catch(Exception e)
			{
				e.printStackTrace();
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				fLogger.fatal(txnKey+":AMS:DAL-AS-getAMsData"+"Exception in fetching data from mysql::"+e.getMessage());
				fLogger.fatal(stack.toString());
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

	public static String setAMSData(String txnKey, AMSDoc_DAO newSnapshotObj) {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		Connection prodConnection = null;
		Statement statement = null;

		String AMSInsertQuery=null;

		Properties prop=null;
		try
		{
			prop = CommonUtil.getDepEnvProperties();
		}
		catch(Exception e)
		{
			fLogger.fatal(txnKey+":AMS:DAL-AMS-setAMSData::"+"Error in intializing property File :"+e.getMessage());
		}

		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info(txnKey+":AMS:DAL-AMS-setAMSData::"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{
			if(newSnapshotObj!=null){



				Timestamp currentTime = new Timestamp(new Date().getTime());

				String txnDataMap= new JSONObject(newSnapshotObj.getTxnData()).toString();
				String eventMap= new JSONObject(newSnapshotObj.getEvents()).toString();

				/*<!=========START persisting in In Native Database==============>*/


				if(newSnapshotObj.getTransaction_Timestamp_Log()!=null){

					if(newSnapshotObj.getTransaction_Timestamp_Fuel()!=null){
						AMSInsertQuery="INSERT INTO asset_monitoring_snapshot" + "(Serial_Number, Latest_Transaction_Timestamp, Transaction_Timestamp_Log, Transaction_Timestamp_Fuel, Latest_Created_Timestamp, Events, TxnData) VALUES"
								+ "('"+newSnapshotObj.getSerial_Number()+"','"+newSnapshotObj.getTransaction_Timestamp_Log()+"','"+newSnapshotObj.getTransaction_Timestamp_Log()+"','"+newSnapshotObj.getTransaction_Timestamp_Fuel()+"','"+currentTime+"','"+eventMap+"','"+txnDataMap+"')";
					}
					else
					{
						AMSInsertQuery="INSERT INTO asset_monitoring_snapshot" + "(Serial_Number, Latest_Transaction_Timestamp, Transaction_Timestamp_Log, Latest_Created_Timestamp, Events, TxnData) VALUES"
								+ "('"+newSnapshotObj.getSerial_Number()+"','"+newSnapshotObj.getTransaction_Timestamp_Log()+"','"+newSnapshotObj.getTransaction_Timestamp_Log()+"','"+currentTime+"','"+eventMap+"','"+txnDataMap+"')";

					}
				}
				else{

					if(newSnapshotObj.getTransaction_Timestamp_Fuel()!=null){
						AMSInsertQuery="INSERT INTO asset_monitoring_snapshot" + "(Serial_Number, Latest_Transaction_Timestamp, Transaction_Timestamp_Evt, Transaction_Timestamp_Fuel, Latest_Created_Timestamp, Events, TxnData) VALUES"
								+ "('"+newSnapshotObj.getSerial_Number()+"','"+newSnapshotObj.getTransaction_Timestamp_Evt()+"','"+newSnapshotObj.getTransaction_Timestamp_Evt()+"','"+newSnapshotObj.getTransaction_Timestamp_Fuel()+"','"+currentTime+"','"+eventMap+"','"+txnDataMap+"')";
					}
					else
					{
						AMSInsertQuery="INSERT INTO asset_monitoring_snapshot" + "(Serial_Number, Latest_Transaction_Timestamp, Transaction_Timestamp_Evt, Latest_Created_Timestamp, Events, TxnData) VALUES"
								+ "('"+newSnapshotObj.getSerial_Number()+"','"+newSnapshotObj.getTransaction_Timestamp_Evt()+"','"+newSnapshotObj.getTransaction_Timestamp_Evt()+"','"+currentTime+"','"+eventMap+"','"+txnDataMap+"')";

					}	
				}



				iLogger.info(txnKey+":AMS:DAL-AMS-setAMSData"+"AMS Insert Query::"+AMSInsertQuery);	
				try{

					ConnectMySQL connMySql = new ConnectMySQL();
					prodConnection = connMySql.getConnection();
					statement = prodConnection.createStatement();

					int insertCount= statement.executeUpdate(AMSInsertQuery);

					iLogger.info(txnKey+":AMS:DAL-AMS-setAMSData"+"AMS insert count::"+insertCount);	

				}

				catch (SQLException e) {

					e.printStackTrace();
					fLogger.fatal(txnKey+":AMS:DAL-AMS-setAMSData"+"SQL Exception in inserting recors to table::asset_monitoring_snapshot::"+e.getMessage());
					return "FAILURE";
				} 

				catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal(txnKey+":AMS:DAL-AMS-setAMSData"+"Exception in inserting recors to table::asset_monitoring_snapshot::"+e.getMessage());
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



				/*<!=========END persisting in In Native Database==============>*/
			}

		}


		return "SUCCESS";
	}

	public static String updateAMSData(String txnKey, AMSDoc_DAO snapshotObj, String recordType, Timestamp txnTimestamp,HashMap<String,String> payloadMap, HashMap<String,String> eventCodeValueMap) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		Connection prodConnection = null;
		Statement statement = null;
		String AMSUpdateQuery=null;

		Properties prop=null;
		try
		{
			prop = CommonUtil.getDepEnvProperties();
		}
		catch(Exception e)
		{
			fLogger.fatal(txnKey+":AMS:DAL-AMS-updateAMSData::"+"Error in intializing property File :"+e.getMessage());
		}

		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info(txnKey+":AMS:DAL-AMS-updateAMSData::"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{
			if(snapshotObj!=null){

				Timestamp currentTime = new Timestamp(new Date().getTime());

				/*<!=========START persisting in In Native Database==============>*/

				SimpleDateFormat dateTimeFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				Timestamp prevFuelTxnTimestamp=null;



				String prevFuelTS=snapshotObj.getTransaction_Timestamp_Fuel();

				if(prevFuelTS!=null && ! prevFuelTS.equalsIgnoreCase("NULL")){
					Date txnfuelDate = null;
					try {
						txnfuelDate = dateTimeFormat.parse(prevFuelTS);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					prevFuelTxnTimestamp= new Timestamp(txnfuelDate.getTime());
				}




				if( (recordType.equalsIgnoreCase("Log Packet"))  ||  ( (snapshotObj.getTxnData()!=null) && (snapshotObj.getTxnData().containsKey("EVT_LFL")) ) )
				{


					if(snapshotObj.getTransaction_Timestamp_Fuel()==null || snapshotObj.getTransaction_Timestamp_Fuel().equalsIgnoreCase("null"))
					{

						snapshotObj.setTransaction_Timestamp_Fuel(String.valueOf(txnTimestamp));
					}
					else
					{
						if(prevFuelTxnTimestamp.before(txnTimestamp)) {

							snapshotObj.setTransaction_Timestamp_Fuel(String.valueOf(txnTimestamp));
						}
					}


				}

				if(recordType.equalsIgnoreCase("Log Packet"))
				{
					snapshotObj.setTransaction_Timestamp_Log(String.valueOf(txnTimestamp));
				}
				else
				{
					snapshotObj.setTransaction_Timestamp_Evt(String.valueOf(txnTimestamp));
				}

				snapshotObj.setLatest_Created_Timestamp(String.valueOf(currentTime));


				if(recordType.equalsIgnoreCase("Log Packet"))
				{
					if(snapshotObj.getTransaction_Timestamp_Fuel()!=null && ! snapshotObj.getTransaction_Timestamp_Fuel().equalsIgnoreCase("null")){
						AMSUpdateQuery="UPDATE asset_monitoring_snapshot " + "SET Latest_Transaction_Timestamp='"+snapshotObj.getTransaction_Timestamp_Log()+"', Transaction_Timestamp_Log='"+snapshotObj.getTransaction_Timestamp_Log()+"', Transaction_Timestamp_Fuel='"+snapshotObj.getTransaction_Timestamp_Fuel()+"',Latest_Created_Timestamp='"+snapshotObj.getLatest_Created_Timestamp()+"' ";	



					}
					else{
						AMSUpdateQuery="UPDATE asset_monitoring_snapshot " + "SET Latest_Transaction_Timestamp='"+snapshotObj.getTransaction_Timestamp_Log()+"', Transaction_Timestamp_Log='"+snapshotObj.getTransaction_Timestamp_Log()+"',Latest_Created_Timestamp='"+snapshotObj.getLatest_Created_Timestamp()+"' ";	
					}

				}
				else{
					if(snapshotObj.getTransaction_Timestamp_Fuel()!=null && ! snapshotObj.getTransaction_Timestamp_Fuel().equalsIgnoreCase("null")){
						AMSUpdateQuery="UPDATE asset_monitoring_snapshot " + "SET  Latest_Transaction_Timestamp='"+snapshotObj.getTransaction_Timestamp_Evt()+"', Transaction_Timestamp_Evt='"+snapshotObj.getTransaction_Timestamp_Evt()+"', Transaction_Timestamp_Fuel='"+snapshotObj.getTransaction_Timestamp_Fuel()+"',Latest_Created_Timestamp='"+snapshotObj.getLatest_Created_Timestamp()+"' ";	

					}
					else{
						AMSUpdateQuery="UPDATE asset_monitoring_snapshot " + "SET Latest_Transaction_Timestamp='"+snapshotObj.getTransaction_Timestamp_Evt()+"', Transaction_Timestamp_Evt='"+snapshotObj.getTransaction_Timestamp_Evt()+"',Latest_Created_Timestamp='"+snapshotObj.getLatest_Created_Timestamp()+"' ";	
					}

					//Updating Event Column for Event packet
					for(Map.Entry<String, String> eventData : eventCodeValueMap.entrySet())
					{
						AMSUpdateQuery=AMSUpdateQuery+", Events = JSON_SET(Events, '$."+eventData.getKey()+"', '"+eventData.getValue()+"') ";
					}
				}



				for(Map.Entry<String, String> dataPayload : payloadMap.entrySet())
				{
					AMSUpdateQuery=AMSUpdateQuery+", TxnData = JSON_SET(TxnData, '$."+dataPayload.getKey()+"', '"+dataPayload.getValue()+"') ";
				}

				AMSUpdateQuery=AMSUpdateQuery+" where Serial_Number='"+snapshotObj.getSerial_Number()+"' ";

				iLogger.info(txnKey+":AMS:DAL-AMS-updateAMSData"+"AMS Updae Query::"+AMSUpdateQuery);	

				try{

					ConnectMySQL connMySql = new ConnectMySQL();
					prodConnection = connMySql.getConnection();
					statement = prodConnection.createStatement();

					int updateCount= statement.executeUpdate(AMSUpdateQuery);


					iLogger.info(txnKey+":AMS:DAL-AMS-updateAMSData"+"AMS update count::"+updateCount);	

				}

				catch (SQLException e) {

					e.printStackTrace();
					fLogger.fatal(txnKey+":AMS:DAL-AMS-updateAMSData"+"SQL Exception in updating recors to table::asset_monitoring_snapshot::"+e.getMessage());
					return "FAILURE";
				} 

				catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal(txnKey+":AMS:DAL-AMS-updateAMSData"+"Exception in updating recors to table::asset_monitoring_snapshot::"+e.getMessage());
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


				/*<!=========END persisting in In Native Database==============>*/
			}

		}


		return "SUCCESS";
	}

	public List<AMSDoc_DAO> getAMSDataOnTS(String txnKey, String Serial_Number,String Tran_TS) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;


		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String AMSSelectQuery=null;
		String AMSTx_TSQuery = null;
		List<AMSDoc_DAO>AmsDAOList=new ArrayList<AMSDoc_DAO>();


		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
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



			/*if(Serial_Number!=null){
				AMSSelectQuery="Select * from asset_monitoring_snapshot_new " + "where Serial_Number='"+Serial_Number+"'";
				//iLogger.info(txnKey+":AMS:DAL-AMS-getAMsData"+"AMSSelectQuery::"+AMSSelectQuery);
			}*/
			/*if(Serial_Number!=null && Tran_TS != null){
				AMSSelectQuery = "SELECT ams.* from asset_monitoring_snapshot_new ams,"+
				"( SELECT Serial_Number,Greatest(Transaction_Timestamp_Log,Transaction_Timestamp_Evt,Transaction_Timestamp_Fuel) as Transaction_TS"+ 
						" FROM wise.asset_monitoring_snapshot_new where Serial_Number = '"+Serial_Number+"') ams1"+ 
						" WHERE ams.Serial_number = ams1.Serial_Number and ams1.Transaction_TS >= '"+Tran_TS+"'";
			}*/

			if(Serial_Number!=null && Tran_TS != null){

				//DF20161222 @Roopa assetsnapshot table format changed to json
				/*AMSSelectQuery = "SELECT ams.* from asset_monitoring_snapshot_new ams "+
						" WHERE ams.Serial_number = '"+Serial_Number+"' and ams.Latest_Transaction_Timestamp >= '"+Tran_TS+"'";*/

				AMSSelectQuery = "SELECT ams.* from asset_monitoring_snapshot ams "+
						" WHERE ams.Serial_number = '"+Serial_Number+"' and ams.Latest_Transaction_Timestamp >= '"+Tran_TS+"'";
			}

			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(AMSSelectQuery);

				AMSDoc_DAO amsDAOobject; 

				HashMap<String,String> txnDataMap=new HashMap<String, String>();
				String txnData;

				while(rs.next()){
					amsDAOobject=new AMSDoc_DAO();
					amsDAOobject.setLatest_Created_Timestamp(String.valueOf(rs.getTimestamp("Latest_Created_Timestamp")));
					amsDAOobject.setSerial_Number(rs.getString("Serial_Number"));
					amsDAOobject.setTransaction_Timestamp_Evt(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Evt")));
					amsDAOobject.setTransaction_Timestamp_Fuel(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Fuel")));
					amsDAOobject.setTransaction_Timestamp_Log(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Log")));
					txnData=rs.getObject("TxnData").toString();

					txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());

					amsDAOobject.setTxnData(txnDataMap);

					amsDAOobject.setLatest_Transaction_Timestamp(String.valueOf(rs.getTimestamp("Latest_Transaction_Timestamp")));

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
