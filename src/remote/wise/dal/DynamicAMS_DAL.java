/**
 * ME100008483 : 20230724 : Dhiraj Kumar : Amber Service Alerts handling
 */
package remote.wise.dal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import remote.wise.businessentity.HAJAssetLocationDetailsEntity;
import remote.wise.exception.CustomFault;
import remote.wise.interfaces.AMS;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.AmsDAO;
import remote.wise.pojo.AsseControlUnitDAO;
import remote.wise.pojo.CommunicationMachinesDAO;
import remote.wise.service.implementation.AssetDashboardImpl;
import remote.wise.service.implementation.MapImpl;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.GmtLtTimeConversion;
import remote.wise.util.ListToStringConversion;
import remote.wise.util.StaticProperties;

/**
 * @author roopn5
 *
 */
public class DynamicAMS_DAL implements AMS{

	@Override
	public List<AmsDAO> getAMSData(String txnKey, String Serial_Number) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;


		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String AMSSelectQuery=null;

		List<AmsDAO>AmsDAOList=new ArrayList<AmsDAO>();


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



			if(Serial_Number!=null){
				AMSSelectQuery="Select * from asset_monitoring_snapshot_new " + "where Serial_Number like '%"+Serial_Number+"'";

				//iLogger.info(txnKey+":AMS:DAL-AMS-getAMsData"+"AMSSelectQuery::"+AMSSelectQuery);
			}

			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(AMSSelectQuery);

				AmsDAO amsDAOobject; 

				while(rs.next()){
					amsDAOobject=new AmsDAO();
					amsDAOobject.setFuel_Level(rs.getString("Fuel_Level"));
					amsDAOobject.setLatest_Created_Timestamp(String.valueOf(rs.getTimestamp("Latest_Created_Timestamp")));
					amsDAOobject.setLatest_Event_Transaction(rs.getInt("Latest_Event_Transaction"));
					amsDAOobject.setLatest_Fuel_Transaction(rs.getInt("Latest_Fuel_Transaction"));
					amsDAOobject.setLatest_Log_Transaction(rs.getInt("Latest_Log_Transaction"));
					amsDAOobject.setSerial_Number(rs.getString("Serial_Number"));
					amsDAOobject.setTransaction_Number(rs.getInt("Transaction_Number"));
					amsDAOobject.setTransaction_Timestamp_Evt(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Evt")));
					amsDAOobject.setTransaction_Timestamp_Fuel(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Fuel")));
					amsDAOobject.setTransaction_Timestamp_Log(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Log")));
					amsDAOobject.setLatest_Transaction_Timestamp(String.valueOf(rs.getTimestamp("Latest_Transaction_Timestamp")));
					amsDAOobject.setParameters(rs.getString("parameters"));

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

	@Override
	public String setAMSData(String txnKey, AmsDAO newSnapshotObj, HashMap<String, String> payloadMap, String engineStatus, String evt_HCT, String evt_LOP, String evt_IBL) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Connection prodConnection = null;
		Statement statement = null;
		String AMSInsertQuery=null;

		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
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

				//construct the string with required parameters LAT|LONG|Enginestatus|Machinehours|ExternalBatteryVoltage|HCT|LOP|InternalBatteryLow

				newSnapshotObj.setParameters(payloadMap.get("LAT")+"|"+payloadMap.get("LONG")+"|"+engineStatus+"|"+payloadMap.get("CMH")+"|"+payloadMap.get("EXT_BAT_VOLT")+"|"+evt_HCT+"|"+evt_LOP+"|"+evt_IBL);

				Timestamp currentTime = new Timestamp(new Date().getTime());

				/*<!=========START persisting in In Native Database==============>*/


				if(newSnapshotObj.getTransaction_Timestamp_Log()!=null){

					if(newSnapshotObj.getTransaction_Timestamp_Fuel()!=null){
						AMSInsertQuery="INSERT INTO asset_monitoring_snapshot_new" + "(Serial_Number, Transaction_Timestamp_Log, Transaction_Timestamp_Fuel, Latest_Created_Timestamp, Fuel_Level, Latest_Log_Transaction, Latest_Fuel_Transaction, Transaction_Number, parameters) VALUES"
								+ "('"+newSnapshotObj.getSerial_Number()+"','"+newSnapshotObj.getTransaction_Timestamp_Log()+"','"+newSnapshotObj.getTransaction_Timestamp_Fuel()+"','"+currentTime+"','"+newSnapshotObj.getFuel_Level()+"',"+newSnapshotObj.getLatest_Log_Transaction()+","+newSnapshotObj.getLatest_Fuel_Transaction()+","+newSnapshotObj.getTransaction_Number()+",'"+newSnapshotObj.getParameters()+"')";
					}
					else
					{
						AMSInsertQuery="INSERT INTO asset_monitoring_snapshot_new" + "(Serial_Number, Transaction_Timestamp_Log, Latest_Created_Timestamp, Latest_Log_Transaction, Transaction_Number, parameters) VALUES"
								+ "('"+newSnapshotObj.getSerial_Number()+"','"+newSnapshotObj.getTransaction_Timestamp_Log()+"','"+currentTime+"',"+newSnapshotObj.getLatest_Log_Transaction()+","+newSnapshotObj.getTransaction_Number()+",'"+newSnapshotObj.getParameters()+"')";

					}
				}
				else{

					if(newSnapshotObj.getTransaction_Timestamp_Fuel()!=null){
						AMSInsertQuery="INSERT INTO asset_monitoring_snapshot_new" + "(Serial_Number, Transaction_Timestamp_Evt, Transaction_Timestamp_Fuel, Latest_Created_Timestamp, Fuel_Level, Latest_Event_Transaction, Latest_Fuel_Transaction, Transaction_Number, parameters) VALUES"
								+ "('"+newSnapshotObj.getSerial_Number()+"','"+newSnapshotObj.getTransaction_Timestamp_Evt()+"','"+newSnapshotObj.getTransaction_Timestamp_Fuel()+"','"+currentTime+"','"+newSnapshotObj.getFuel_Level()+"',"+newSnapshotObj.getLatest_Event_Transaction()+","+newSnapshotObj.getLatest_Fuel_Transaction()+","+newSnapshotObj.getTransaction_Number()+",'"+newSnapshotObj.getParameters()+"')";
					}
					else
					{
						AMSInsertQuery="INSERT INTO asset_monitoring_snapshot_new" + "(Serial_Number, Transaction_Timestamp_Evt, Latest_Created_Timestamp, Latest_Event_Transaction, Transaction_Number, parameters) VALUES"
								+ "('"+newSnapshotObj.getSerial_Number()+"','"+newSnapshotObj.getTransaction_Timestamp_Evt()+"','"+currentTime+"',"+newSnapshotObj.getLatest_Event_Transaction()+","+newSnapshotObj.getTransaction_Number()+",'"+newSnapshotObj.getParameters()+"')";

					}	
				}



				//iLogger.info(txnKey+":AMS:DAL-AMS-setAMSData"+"AMS Insert Query::"+AMSInsertQuery);	


				try{

					ConnectMySQL connMySql = new ConnectMySQL();
					prodConnection = connMySql.getConnection();
					statement = prodConnection.createStatement();

					int insertCount= statement.executeUpdate(AMSInsertQuery);

					//iLogger.info(txnKey+":AMS:DAL-AMS-setAMSData"+"AMS insert count::"+insertCount);	

				}

				catch (SQLException e) {

					e.printStackTrace();
					fLogger.fatal(txnKey+":AMS:DAL-AMS-setAMSData"+"SQL Exception in inserting recors to table::asset_monitoring_snapshot_new"+"::"+e.getMessage());
					return "FAILURE";
				} 

				catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal(txnKey+":AMS:DAL-AMS-setAMSData"+"Exception in inserting recors to table::asset_monitoring_snapshot_new::"+e.getMessage());
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

	@Override
	public String updateAMSData(String txnKey, int transactionNumber,
			AmsDAO snapshotObj, String recordType, Timestamp txnTimestamp,
			HashMap<String, String> payloadMap, String currentFuelLevel, String engineStatus, String evt_HCT, String evt_LOP, String evt_IBL) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Connection prodConnection = null;
		Statement statement = null;
		String AMSUpdateQuery=null;




		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
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


				Timestamp prevLogTxnTimestamp=null;
				Timestamp prevEvtTxnTimestamp=null;
				Timestamp prevFuelTxnTimestamp=null;

				String prevLogTS=snapshotObj.getTransaction_Timestamp_Log();
				if(prevLogTS!=null&& ! prevLogTS.equalsIgnoreCase("NULL")){
					Date txnlogDate = null;
					try {
						txnlogDate = dateTimeFormat.parse(prevLogTS);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					prevLogTxnTimestamp = new Timestamp(txnlogDate.getTime());
				}


				String prevEventTS=snapshotObj.getTransaction_Timestamp_Evt();

				if(prevEventTS!=null && ! prevEventTS.equalsIgnoreCase("NULL")){
					Date txnevtDate = null;
					try {
						txnevtDate = dateTimeFormat.parse(prevEventTS);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					prevEvtTxnTimestamp= new Timestamp(txnevtDate.getTime());
				}

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


				Timestamp t1=prevLogTxnTimestamp, t2=prevEvtTxnTimestamp;

				Timestamp prevTS=null;

				/*if(prevLogTxnTimestamp!=null && (txnTimestamp.after(prevLogTxnTimestamp) || txnTimestamp.equals(prevLogTxnTimestamp) ))
					t1=prevLogTxnTimestamp;

				if(prevEvtTxnTimestamp!=null && (txnTimestamp.after(prevEvtTxnTimestamp) || txnTimestamp.equals(prevEvtTxnTimestamp) ))
					t2=prevEvtTxnTimestamp;*/

				if(t1==null && t2!=null){
					prevTS=t2;
				}

				else if(t2==null && t1!=null){
					prevTS=t1;
				}

				else if (t1!=null && t2!=null)
				{
					if(t1.before(t2)){
						prevTS=t2;
					}
					else{
						prevTS=t1;
					}
				}

				if(prevTS!=null && ( (prevTS.before(txnTimestamp)) || (prevTS.equals(txnTimestamp)) ))
				{
					snapshotObj.setTransaction_Number(transactionNumber);
				}	

				if( (recordType.equalsIgnoreCase("Log Packet"))  ||  ( (payloadMap!=null) && (payloadMap.containsKey("EVT_LFL")) ) )
				{


					if(snapshotObj.getLatest_Fuel_Transaction()==0)
					{
						snapshotObj.setFuel_Level(currentFuelLevel);
						snapshotObj.setLatest_Fuel_Transaction(transactionNumber);
						snapshotObj.setTransaction_Timestamp_Fuel(String.valueOf(txnTimestamp));
					}
					else
					{

						if(prevFuelTxnTimestamp.before(txnTimestamp)) {

							snapshotObj.setFuel_Level(currentFuelLevel);
							snapshotObj.setLatest_Fuel_Transaction(transactionNumber);
							snapshotObj.setTransaction_Timestamp_Fuel(String.valueOf(txnTimestamp));
						}

					}


				}

				if(recordType.equalsIgnoreCase("Log Packet"))
				{
					snapshotObj.setLatest_Log_Transaction(transactionNumber);
					snapshotObj.setTransaction_Timestamp_Log(String.valueOf(txnTimestamp));
				}
				else
				{
					snapshotObj.setLatest_Event_Transaction(transactionNumber);
					snapshotObj.setTransaction_Timestamp_Evt(String.valueOf(txnTimestamp));
				}

				snapshotObj.setLatest_Created_Timestamp(String.valueOf(currentTime));

				String currParam=snapshotObj.getParameters();

				String [] currParamList=currParam.split("\\|", -1);

				//parameters format in AMS
				//String currParam= LAT|LONG|Enginestatus|Machinehours|ExternalBatteryVoltage|HCT|LOP|InternalBatteryLow

				//update lat and long only when the received TS is greater than CurrentTS
				if(prevTS==null || prevTS.before(txnTimestamp)){
					//currParam=currParam.replaceFirst(currParam.split("\\|")[0], payloadMap.get("LAT"));
					//currParam=currParam.replaceFirst(currParam.split("\\|")[1], payloadMap.get("LONG"));
					currParamList[0]=payloadMap.get("LAT");
					currParamList[1]=payloadMap.get("LONG");
				}
				//update enginestatus only when it is not null(Bcoz whenever there is a AMD update, may be there might not be a parameter received for enginestatus change) 
				if(engineStatus!=null){

					//currParam=currParam.replaceFirst(currParam.split("\\|")[2], engineStatus);
					currParamList[2]=engineStatus;
				}

				//currParam=currParam.replaceFirst(currParam.split("\\|")[3], payloadMap.get("CMH"));	

				//currParam=currParam.replaceFirst(currParam.split("\\|")[4], payloadMap.get("EXT_BAT_VOLT"));

				currParamList[3]=payloadMap.get("CMH");

				currParamList[4]=payloadMap.get("EXT_BAT_VOLT");

				if(evt_HCT!=null){
					//currParam=currParam.replaceFirst(currParam.split("\\|")[5], evt_HCT);	
					currParamList[5]=evt_HCT;
				}

				if(evt_LOP!=null){
					//currParam=currParam.replaceFirst(currParam.split("\\|")[6], evt_LOP);	
					currParamList[6]=evt_LOP;
				}
				if(evt_IBL!=null){
					//currParam=currParam.replaceFirst(currParam.split("\\|")[7], evt_IBL);	
					currParamList[7]=evt_IBL;
				}

				String newParam=null;

				for(int i=0;i<currParamList.length;i++){
					if(newParam!=null)
						newParam=newParam+currParamList[i]+"|";	
					else
						newParam=currParamList[i]+"|";		
				}
				newParam=newParam.substring(0,newParam.length()-1);

				snapshotObj.setParameters(newParam);

				if(recordType.equalsIgnoreCase("Log Packet"))
				{
					if(snapshotObj.getTransaction_Timestamp_Fuel()!=null && ! snapshotObj.getTransaction_Timestamp_Fuel().equalsIgnoreCase("null")){
						AMSUpdateQuery="UPDATE asset_monitoring_snapshot_new " + "SET parameters='"+snapshotObj.getParameters()+"', Transaction_Timestamp_Log='"+snapshotObj.getTransaction_Timestamp_Log()+"', Transaction_Timestamp_Fuel='"+snapshotObj.getTransaction_Timestamp_Fuel()+"',Latest_Created_Timestamp='"+snapshotObj.getLatest_Created_Timestamp()+"',Fuel_Level='"+snapshotObj.getFuel_Level()+"', Latest_Log_Transaction='"+snapshotObj.getLatest_Log_Transaction()+"', Latest_Fuel_Transaction='"+snapshotObj.getLatest_Fuel_Transaction()+"',Transaction_Number='"+snapshotObj.getTransaction_Number()+"' where Serial_Number='"+snapshotObj.getSerial_Number()+"'";	
					}
					else{
						AMSUpdateQuery="UPDATE asset_monitoring_snapshot_new " + "SET parameters='"+snapshotObj.getParameters()+"', Transaction_Timestamp_Log='"+snapshotObj.getTransaction_Timestamp_Log()+"',Latest_Created_Timestamp='"+snapshotObj.getLatest_Created_Timestamp()+"',Latest_Log_Transaction='"+snapshotObj.getLatest_Log_Transaction()+"',Transaction_Number='"+snapshotObj.getTransaction_Number()+"' where Serial_Number='"+snapshotObj.getSerial_Number()+"'";	
					}

				}
				else{
					if(snapshotObj.getTransaction_Timestamp_Fuel()!=null && ! snapshotObj.getTransaction_Timestamp_Fuel().equalsIgnoreCase("null")){
						AMSUpdateQuery="UPDATE asset_monitoring_snapshot_new " + "SET parameters='"+snapshotObj.getParameters()+"', Transaction_Timestamp_Evt='"+snapshotObj.getTransaction_Timestamp_Evt()+"', Transaction_Timestamp_Fuel='"+snapshotObj.getTransaction_Timestamp_Fuel()+"',Latest_Created_Timestamp='"+snapshotObj.getLatest_Created_Timestamp()+"',Fuel_Level='"+snapshotObj.getFuel_Level()+"', Latest_Event_Transaction='"+snapshotObj.getLatest_Event_Transaction()+"', Latest_Fuel_Transaction='"+snapshotObj.getLatest_Fuel_Transaction()+"',Transaction_Number='"+snapshotObj.getTransaction_Number()+"' where Serial_Number='"+snapshotObj.getSerial_Number()+"'";	

					}
					else{
						AMSUpdateQuery="UPDATE asset_monitoring_snapshot_new " + "SET parameters='"+snapshotObj.getParameters()+"', Transaction_Timestamp_Evt='"+snapshotObj.getTransaction_Timestamp_Evt()+"',Latest_Created_Timestamp='"+snapshotObj.getLatest_Created_Timestamp()+"',Latest_Event_Transaction='"+snapshotObj.getLatest_Event_Transaction()+"',Transaction_Number='"+snapshotObj.getTransaction_Number()+"' where Serial_Number='"+snapshotObj.getSerial_Number()+"'";	
					}
				}

				//iLogger.info(txnKey+":AMS:DAL-AMS-updateAMSData"+"AMS Updae Query::"+AMSUpdateQuery);	


				try{

					ConnectMySQL connMySql = new ConnectMySQL();
					prodConnection = connMySql.getConnection();
					statement = prodConnection.createStatement();

					int updateCount= statement.executeUpdate(AMSUpdateQuery);

					//	iLogger.info(txnKey+":AMS:DAL-AMS-updateAMSData"+"AMS update count::"+updateCount);	

				}

				catch (SQLException e) {

					e.printStackTrace();
					fLogger.fatal(txnKey+":AMS:DAL-AMS-updateAMSData"+"SQL Exception in inserting recors to table::asset_monitoring_snapshot_new"+"::"+e.getMessage());
					return "FAILURE";
				} 

				catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal(txnKey+":AMS:DAL-AMS-updateAMSData"+"Exception in inserting recors to table::asset_monitoring_snapshot_new::"+e.getMessage());
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

	@Override
	public List<MapImpl> getQuerySpecificDetailsForMap(String Query, String loginId) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;


		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String AMSSelectQuery=Query;

		List<MapImpl>MapImplList=new ArrayList<MapImpl>();


		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			fLogger.fatal(":AMS:DAL-AMS-getQuerySpecificDetailsForMap"+"Error in intializing property File :"+e.getMessage());
		}


		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info(":AMS:DAL-AMS-getQuerySpecificDetailsForMap"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{

			/*<!=========START fetching from Native Database==============>*/


			try{

				long startTime = System.currentTimeMillis();
				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				long endTime = System.currentTimeMillis();
				iLogger.info("Map Service:"+Query+":Get DB Connection:"+(endTime-startTime)+"  loginId::"+loginId);
				
				startTime = System.currentTimeMillis();
				statement = prodConnection.createStatement();
				//statement.setFetchSize(10000);
				rs = statement.executeQuery(AMSSelectQuery);
				rs.setFetchSize(1000);
				endTime = System.currentTimeMillis();
				iLogger.info("Map Service:"+Query+":Query Execution time:"+(endTime-startTime)+"  loginId::"+loginId);
				
				MapImpl MapImplObject; 
				HashMap<String,String> txnDataMap=new HashMap<String, String>();
				String txnData;
				String engStatus=null;

				startTime = System.currentTimeMillis();
				int rowCount=0;
				long jsonConversionTime = 0, resStartTime=0, resEndTime=0;
				
				resEndTime=System.currentTimeMillis();
				while(rs.next())
				{
					resStartTime = System.currentTimeMillis();
					rowCount++;
					//if((resStartTime-resEndTime) > 100)
					fLogger.info("Map Service:RowCount:"+rowCount+";Query:"+Query+";VIN:"+rs.getString("Serial_Number")+";Time taken for rs next:"+(resStartTime-resEndTime));
								
					
					MapImplObject=new MapImpl();
					MapImplObject.setSerialNumber(rs.getString("Serial_Number"));
					MapImplObject.setNickname(rs.getString("Engine_Number"));
					
					//DF20170719: SU334449 - Sending Asset Profile Name with the Map response
					MapImplObject.setProfileName(rs.getString("Asseet_Group_Name"));

					//parameters format in AMS
					//String currParam= LAT|LONG|Enginestatus|Machinehours|ExternalBatteryVoltage|HCT|LOP|InternalBatteryLow

					/*String parameters=rs.getString("parameters");
					String [] currParamList=parameters.split("\\|", -1);

					MapImplObject.setLatitude(currParamList[0]);
					MapImplObject.setLongitude(currParamList[1]);
					MapImplObject.setEngineStatus(currParamList[2]);
					MapImplObject.setTotalMachineHours(currParamList[3]);*/

					//DF20161221 @Roopa FEtching Overview map details from the txndata json column in assetmonitoringsnapshot table

					txnData=rs.getObject("TxnData").toString();

					long convStartTime = System.currentTimeMillis();	
					txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());
					long convEndTime = System.currentTimeMillis();
					
					jsonConversionTime = jsonConversionTime+(convEndTime-convStartTime);
					/*if(rowCount<100)
						iLogger.info("Map Service:"+Query+":JSON String to JSON Conversion Time:"+(convEndTime-convStartTime));*/
					
					MapImplObject.setLatitude(txnDataMap.get("LAT"));
					MapImplObject.setLongitude(txnDataMap.get("LONG"));
					engStatus=txnDataMap.get("ENG_STATUS");
					
					if(engStatus==null){
						engStatus=txnDataMap.get("EVT_ENG");	
					}
					if(engStatus==null){
						engStatus="0";
					}
					MapImplObject.setEngineStatus(engStatus);
					MapImplObject.setTotalMachineHours(txnDataMap.get("CMH"));



					MapImplList.add(MapImplObject);
					
					resEndTime = System.currentTimeMillis();
					//if((resEndTime-resStartTime) > 100)
					fLogger.info("Map Service:RowCount:"+rowCount+";Query:"+Query+";VIN:"+rs.getString("Serial_Number")+":Time taken to fill " +
							"Impl obj:"+(resEndTime-resStartTime)+"; JSON String to json conversion time:"+(convEndTime-convStartTime));
				}
				endTime = System.currentTimeMillis();
				iLogger.info("Map Service:"+Query+":Time to fill response Object for :"+rowCount+"rows:"+(endTime-startTime)+"; JSON String to json " +
						"conversion time:"+jsonConversionTime+"  loginId::"+loginId);
			}

			catch (SQLException e) {

				e.printStackTrace();
				fLogger.fatal(":AMS:DAL-AMS-getQuerySpecificDetailsForMap"+"SQL Exception in fetching data from mysql::"+e.getMessage()+"  loginId::"+loginId);
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				fLogger.fatal(stack.toString());
			} 

			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal(":AMS:DAL-AMS-getQuerySpecificDetailsForMap"+"Exception in fetching data from mysql::"+e.getMessage()+"  loginId::"+loginId);
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
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


		return MapImplList;
	}

	public List<AmsDAO> getAMSDataOnTS(String txnKey, String Serial_Number,String Tran_TS) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;


		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String AMSSelectQuery=null;
		String AMSTx_TSQuery = null;
		List<AmsDAO>AmsDAOList=new ArrayList<AmsDAO>();


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
				AMSSelectQuery = "SELECT ams.* from asset_monitoring_snapshot_new ams "+
						" WHERE ams.Serial_number = '"+Serial_Number+"' and ams.Latest_Transaction_Timestamp >= '"+Tran_TS+"'";
			}

			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(AMSSelectQuery);

				AmsDAO amsDAOobject; 

				while(rs.next()){
					amsDAOobject=new AmsDAO();
					amsDAOobject.setFuel_Level(rs.getString("Fuel_Level"));
					amsDAOobject.setLatest_Created_Timestamp(String.valueOf(rs.getTimestamp("Latest_Created_Timestamp")));
					amsDAOobject.setLatest_Event_Transaction(rs.getInt("Latest_Event_Transaction"));
					amsDAOobject.setLatest_Fuel_Transaction(rs.getInt("Latest_Fuel_Transaction"));
					amsDAOobject.setLatest_Log_Transaction(rs.getInt("Latest_Log_Transaction"));
					amsDAOobject.setSerial_Number(rs.getString("Serial_Number"));
					amsDAOobject.setTransaction_Number(rs.getInt("Transaction_Number"));
					amsDAOobject.setTransaction_Timestamp_Evt(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Evt")));
					amsDAOobject.setTransaction_Timestamp_Fuel(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Fuel")));
					amsDAOobject.setTransaction_Timestamp_Log(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Log")));
					amsDAOobject.setParameters(rs.getString("parameters"));
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


	public List<AmsDAO> getAMSDataOn_OwnershipDateList(String txnKey,String ownershipStartDateStringList) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String AMSSelectQuery=null;
		String AMSTx_TSQuery = null;
		List<AmsDAO>AmsDAOList=new ArrayList<AmsDAO>();


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

			if(ownershipStartDateStringList != null ){

				/*AMSSelectQuery = "select a.Serial_Number,a.Rolloff_Date,ac.account_name,ams.parameters,ams.Latest_Created_Timestamp "+
						"from asset_monitoring_snapshot_new ams,asset a,asset_owners ao,account ac "+
						"where ao.Ownership_Start_Date in ("+ownershipStartDateStringList+")and a.Serial_Number = ao.Serial_Number" +
			            " and ams.Serial_Number = a.Serial_Number and ac.account_id = a.Primary_Owner_ID limit 28000";;*/
				AMSSelectQuery = "select a.Serial_Number,a.Rolloff_Date,ac.account_name,ams.TxnData,ams.Latest_Created_Timestamp "+
				"from asset_monitoring_snapshot ams,asset a,asset_owners ao,account ac "+
				"where ao.Ownership_Start_Date in ("+ownershipStartDateStringList+")and a.Serial_Number = ao.Serial_Number" +
				" and ams.Serial_Number = a.Serial_Number and ac.account_id = a.Primary_Owner_ID limit 28000";;
			}

			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(AMSSelectQuery);
				
				String txnData;
				HashMap<String,String> txnDataMap=new HashMap<String, String>();
				
				AmsDAO amsDAOobject; 
				Timestamp CreatedTimestamp = null;
				Timestamp Rolloff_Date = null;
				while(rs.next()){
					amsDAOobject=new AmsDAO();
					txnData=rs.getObject("TxnData").toString();
					txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());
					//amsDAOobject.setFuel_Level(rs.getString("Fuel_Level"));
					amsDAOobject.setLatest_Created_Timestamp(String.valueOf(rs.getTimestamp("Latest_Created_Timestamp")));
					//	amsDAOobject.setLatest_Event_Transaction(rs.getInt("Latest_Event_Transaction"));
					//amsDAOobject.setLatest_Fuel_Transaction(rs.getInt("Latest_Fuel_Transaction"));
					//amsDAOobject.setLatest_Log_Transaction(rs.getInt("Latest_Log_Transaction"));
					amsDAOobject.setSerial_Number(rs.getString("Serial_Number"));
					//amsDAOobject.setTransaction_Number(rs.getInt("Transaction_Number"));
					//amsDAOobject.setTransaction_Timestamp_Evt(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Evt")));
					//amsDAOobject.setTransaction_Timestamp_Fuel(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Fuel")));
					//amsDAOobject.setTransaction_Timestamp_Log(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Log")));
				
					//amsDAOobject.setParameters(rs.getString("parameters"));
					amsDAOobject.setTxnData(txnDataMap);
					amsDAOobject.setAccount_name(rs.getString("account_name"));
					Rolloff_Date=(Timestamp)rs.getTimestamp("Rolloff_Date");

					String transactionTimeInString = dateFormat.format(Rolloff_Date);
					amsDAOobject.setRollOffDate(transactionTimeInString);
					//newimplObj.setLast_Reported(transactionTimeInString);
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



	public List<AmsDAO> getNonCommMachinesFromAMS(String txnKey) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String AMSSelectQuery=null;
		String AMSTx_TSQuery = null;
		List<AmsDAO>AmsDAOList=new ArrayList<AmsDAO>();


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

/*
			AMSSelectQuery = "select acu.Serial_Number,acc.account_name "+
					"from asset_control_unit acu ,account acc,asset_monitoring_snapshot_new ams  "+
					"where acc.status = true and acu.Serial_Number != ams.Serial_Number";*/

			AMSSelectQuery = "select acu.Serial_Number,acc.account_name "+
					"from asset_control_unit acu ,account acc,asset_monitoring_snapshot ams  "+
					"where acc.status = true and acu.Serial_Number != ams.Serial_Number";
			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(AMSSelectQuery);

				AmsDAO amsDAOobject; 
				Timestamp CreatedTimestamp = null;
				Timestamp Rolloff_Date = null;
				while(rs.next()){
					amsDAOobject=new AmsDAO();

					//amsDAOobject.setLatest_Created_Timestamp(String.valueOf(rs.getTimestamp("Latest_Created_Timestamp")));

					amsDAOobject.setSerial_Number(rs.getString("Serial_Number"));

					//amsDAOobject.setParameters(rs.getString("parameters"));
					amsDAOobject.setAccount_name(rs.getString("account_name"));

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
		}
		return AmsDAOList;
	}
	public List<CommunicationMachinesDAO> getDailyCommunicatedMachines(String txnKey,String account_type) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String AMSSelectQuery=null;
		String AMSTx_TSQuery = null;
		List<CommunicationMachinesDAO> AmsDAOList=new ArrayList<CommunicationMachinesDAO>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

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
			if(account_type.equalsIgnoreCase("DEALER")){
				AMSSelectQuery = "select ams.Serial_Number ,aos.account_id as Dealer_AccountID,da.Account_Code as DealerCode,da.Account_Name as dealerName," +
						"addr.state,addr.city,acc.account_name as owner,ams.parameters,ams.Latest_Transaction_Timestamp,ams.Latest_Created_Timestamp,a.Rolloff_Date "+ 
						"from asset_monitoring_snapshot_new ams,asset_owner_snapshot aos,account acc,account da,asset a,address addr "+ 
						"where  ams.Serial_Number = a.Serial_Number and acc.account_id = a.Primary_Owner_ID "+ 
						"and aos.Serial_Number = ams.Serial_Number and aos.account_type = 'Dealer' and da.account_id = aos.account_id and acc.status = true and da.status = true and da.Address_ID = addr.Address_ID order by ams.Latest_Created_Timestamp ";
			}
			if(account_type.equalsIgnoreCase("JCB")){
				AMSSelectQuery = "select ams.Serial_Number ,'' as Dealer_AccountID,'' as DealerCode,'' as dealerName,'' as State,'' as City," +
						"acc.account_name as owner,ams.parameters,ams.Latest_Transaction_Timestamp,ams.Latest_Created_Timestamp,a.Rolloff_Date"+ 
						"from asset_monitoring_snapshot_new ams,asset_owner_snapshot aos,account acc,asset a"+  
						"where  a.Primary_Owner_ID = 2001 and ams.Serial_Number = a.Serial_Number  and aos.Serial_Number = ams.Serial_Number and acc.account_id = aos.account_id and acc.status = true order by ams.Latest_Created_Timestamp";
			}

			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(AMSSelectQuery);

				CommunicationMachinesDAO amsDAOobject; 
				Timestamp CreatedTimestamp = null;
				Timestamp Rolloff_Date = null;
				while(rs.next()){

					amsDAOobject=new CommunicationMachinesDAO();

					if(account_type.equalsIgnoreCase("DEALER")){

						amsDAOobject.setSerialNumber(rs.getString("Serial_Number"));
						amsDAOobject.setDealerAccountId(rs.getInt("Dealer_AccountID"));
						amsDAOobject.setDealerCode(rs.getString("DealerCode"));
						amsDAOobject.setState(rs.getString("state"));
						amsDAOobject.setCity(rs.getString("city"));
						if(rs.getString("owner")!=null)
							amsDAOobject.setOwnerName(rs.getString("owner"));
						//amsDAOobject.set
						if(rs.getTimestamp("Latest_Transaction_Timestamp")!=null)
						{
							//Packet Created Timestamp - i.e., Transaction Time in IST
							Timestamp pktTxnTime = rs.getTimestamp("Latest_Transaction_Timestamp");
							pktTxnTime.setTime(pktTxnTime.getTime()+(330*60*1000));
							amsDAOobject.setPktCreatedTimestamp(dateFormat.format(pktTxnTime));
						}

						if(rs.getTimestamp("Latest_Created_Timestamp")!=null)
						{
							Timestamp pktRcvdTime = rs.getTimestamp("Latest_Created_Timestamp");
							amsDAOobject.setPktReceivedTimestamp(dateFormat.format(pktRcvdTime));
							amsDAOobject.setPktReceivedDate(date.format(pktRcvdTime));
						}
						if(rs.getTimestamp("Rolloff_Date")!=null)
						{
							Timestamp rolledOffDate = rs.getTimestamp("Rolloff_Date");
							amsDAOobject.setRolledOffDate(dateFormat.format(rolledOffDate));
						}

						if(rs.getString("parameters")!=null){
							String parameters = rs.getString("parameters");
							//String currParam= LAT|LONG|Enginestatus|Machinehours|ExternalBatteryVoltage|HCT|LOP|InternalBatteryLow
							//temp = false;

							String [] currParamList=parameters.split("\\|", -1);
							if(currParamList.length>2)
								amsDAOobject.setMachineHours(currParamList[3]);
							if(currParamList.length>7)
								amsDAOobject.setFwVersion(currParamList[8]);

						}
						//amsDAOobject.setParameters(rs.getString("parameters"));
					}
					if(account_type.equalsIgnoreCase("JCB")){
						amsDAOobject.setSerialNumber(rs.getString("Serial_Number"));
						amsDAOobject.setDealerAccountId(0);
						amsDAOobject.setDealerCode("");
						amsDAOobject.setState("");
						amsDAOobject.setCity("");
						if(rs.getString("owner")!=null)
							amsDAOobject.setOwnerName(rs.getString("owner"));
						//amsDAOobject.set
						if(rs.getTimestamp("Latest_Transaction_Timestamp")!=null)
						{
							//Packet Created Timestamp - i.e., Transaction Time in IST
							Timestamp pktTxnTime = rs.getTimestamp("Latest_Transaction_Timestamp");
							pktTxnTime.setTime(pktTxnTime.getTime()+(330*60*1000));
							amsDAOobject.setPktCreatedTimestamp(dateFormat.format(pktTxnTime));
						}

						if(rs.getTimestamp("Latest_Created_Timestamp")!=null)
						{
							Timestamp pktRcvdTime = rs.getTimestamp("Latest_Created_Timestamp");
							amsDAOobject.setPktReceivedTimestamp(dateFormat.format(pktRcvdTime));
							amsDAOobject.setPktReceivedDate(date.format(pktRcvdTime));
						}
						if(rs.getString("parameters")!=null){
							String parameters = rs.getString("parameters");
							//String currParam= LAT|LONG|Enginestatus|Machinehours|ExternalBatteryVoltage|HCT|LOP|InternalBatteryLow
							//temp = false;

							String [] currParamList=parameters.split("\\|", -1);
							if(currParamList.length>2)
								amsDAOobject.setMachineHours(currParamList[3]);
							if(currParamList.length>7)
								amsDAOobject.setFwVersion(currParamList[8]);

						}
						if(rs.getTimestamp("Rolloff_Date")!=null)
						{
							Timestamp rolledOffDate = rs.getTimestamp("Rolloff_Date");
							amsDAOobject.setRolledOffDate(dateFormat.format(rolledOffDate));
						}
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


	public List<CommunicationMachinesDAO> getDailyNonCommunicatedMachines(String txnKey,String account_type) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String AMSSelectQuery=null;
		String AMSTx_TSQuery = null;
		List<CommunicationMachinesDAO> AmsDAOList=new ArrayList<CommunicationMachinesDAO>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

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
			if(account_type.equalsIgnoreCase("DEALER")){

				AMSSelectQuery = "select a.Serial_Number ,aos.account_id as Dealer_AccountID,da.Account_Code as DealerCode,da.Account_Name as dealerName,addr.state,addr.city,acc.account_name as owner,'' as Parameters ," +
						"'' as Pkt_created_TS,'' as Pkt_Recd_TS,a.Rolloff_Date " +
						"from asset_owner_snapshot aos,account acc,account da,asset a,address addr"+ 
						"where  a.Serial_Number not in (select Serial_Number from asset_monitoring_snapshot_new) and a.Status = 1 and acc.account_id = a.Primary_Owner_ID and aos.Serial_Number = a.Serial_Number and aos.account_type = 'Dealer' and da.account_id = aos.account_id and acc.status = true and da.status = true and da.Address_ID = addr.Address_ID";
			}
			if(account_type.equalsIgnoreCase("JCB")){


				AMSSelectQuery = "select a.Serial_Number ,'' as Dealer_AccountID,'' as DealerCode,'' as dealerName,'' as State,'' as City,acc.account_name as owner,'' as Parameters ,"+
						"'' as Pkt_created_TS,'' as Pkt_Recd_TS,a.Rolloff_Date from asset_owner_snapshot aos,account acc,asset a"+  
						"where  a.Primary_Owner_ID = 2001"+ 
						"and a.Serial_Number not in (select Serial_Number from asset_monitoring_snapshot_new ) and a.status = 1 and aos.Serial_Number = a.Serial_Number and acc.account_id = aos.account_id and acc.status = true";
			}

			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(AMSSelectQuery);

				CommunicationMachinesDAO amsDAOobject; 
				Timestamp CreatedTimestamp = null;
				Timestamp Rolloff_Date = null;
				while(rs.next()){

					amsDAOobject=new CommunicationMachinesDAO();

					if(account_type.equalsIgnoreCase("DEALER")){

						amsDAOobject.setSerialNumber(rs.getString("Serial_Number"));
						amsDAOobject.setDealerAccountId(rs.getInt("Dealer_AccountID"));
						amsDAOobject.setDealerCode(rs.getString("DealerCode"));
						amsDAOobject.setState(rs.getString("state"));
						amsDAOobject.setCity(rs.getString("city"));
						if(rs.getString("owner")!=null)
							amsDAOobject.setOwnerName(rs.getString("owner"));
						//amsDAOobject.set

						amsDAOobject.setPktCreatedTimestamp("");



						amsDAOobject.setPktReceivedTimestamp("");
						amsDAOobject.setPktReceivedDate("");

						if(rs.getTimestamp("Rolloff_Date")!=null)
						{
							Timestamp rolledOffDate = rs.getTimestamp("Rolloff_Date");
							amsDAOobject.setRolledOffDate(dateFormat.format(rolledOffDate));
						}

					}
					if(account_type.equalsIgnoreCase("JCB")){
						amsDAOobject.setSerialNumber(rs.getString("Serial_Number"));
						amsDAOobject.setDealerAccountId(0);
						amsDAOobject.setDealerCode("");
						amsDAOobject.setState("");
						amsDAOobject.setCity("");
						if(rs.getString("owner")!=null)
							amsDAOobject.setOwnerName(rs.getString("owner"));
						//amsDAOobject.set
						amsDAOobject.setPktCreatedTimestamp("");



						amsDAOobject.setPktReceivedTimestamp("");
						amsDAOobject.setPktReceivedDate("");
						if(rs.getTimestamp("Rolloff_Date")!=null)
						{
							Timestamp rolledOffDate = rs.getTimestamp("Rolloff_Date");
							amsDAOobject.setRolledOffDate(dateFormat.format(rolledOffDate));
						}
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
	//DF20161222 @Roopa Fetching assetdashboard details from new json from ams
	@Override
	public List<AssetDashboardImpl> getQuerySpecificDetailsForAssetDashBoard(
			String Query,String loginId) throws CustomFault {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;


		iLogger.info("into the DAL Class loginiD"+loginId);
		long startTime = System.currentTimeMillis();
		Connection prodConnection = null;
		Statement statement = null;

		Statement statement1=null;
		ResultSet rs = null;

		ResultSet rs1 = null,rs2=null;;
		String AMSSelectQuery=Query;

		List<AssetDashboardImpl>AssetDashboardImplList=new ArrayList<AssetDashboardImpl>();


		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			fLogger.fatal(":AMS:DAL-AMS-getQuerySpecificDetailsForAssetDashBoard"+"Error in intializing property File :"+e.getMessage());
		}


		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info(":AMS:DAL-AMS-getQuerySpecificDetailsForAssetDashBoard"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{

			/*<!=========START fetching from Native Database==============>*/


			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(AMSSelectQuery);

				AssetDashboardImpl assetDashboardImplObject; 

				HashMap<String,String> txnDataMap=new HashMap<String, String>();
				String txnData;
				String enginestatus=null;
				String ecModeHrs="NA";
				String powerModeHrs="NA";
				ListToStringConversion convert=new ListToStringConversion();
				Calendar cal = Calendar.getInstance();
				List<String> activeEventids=null;
				HashMap<String,String> AlertStatusMap=null;

				while(rs.next()){
					activeEventids=new ArrayList<String>();
					String activeEventidsString=null;
					String AlertStatus=null;
					AlertStatusMap=new HashMap<String, String>();
					long VinstartTime = System.currentTimeMillis();
					assetDashboardImplObject=new AssetDashboardImpl();
					assetDashboardImplObject.setSerialNumber(rs.getString("Serial_Number"));
					assetDashboardImplObject.setEngineTypeName(rs.getString("Engine_Type_Name"));
					assetDashboardImplObject.setNickName(rs.getString("Engine_Number"));
					assetDashboardImplObject.setProfileName(rs.getString("Asseet_Group_Name"));
					assetDashboardImplObject.setModelName(rs.getString("Asset_Type_Name"));
					assetDashboardImplObject.setAssetImage(rs.getString("Asset_ImageFile_Name"));
					assetDashboardImplObject.setNotes(rs.getString("notes"));
					
					//DF20180808-KO369761- Sending asset country code to the UI
					assetDashboardImplObject.setCountryCode(rs.getString("country_code"));
					if(assetDashboardImplObject.getCountryCode() == null)
						assetDashboardImplObject.setCountryCode("+91");

					String transactionTimeInString = String.valueOf(rs.getTimestamp("Latest_Created_Timestamp"));
					String timeZone  = rs.getString("timeZone");

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

					Date newDate = sdf.parse(transactionTimeInString);

					transactionTimeInString=dateFormat.format(newDate);

					assetDashboardImplObject.setLastReportedTime(transactionTimeInString);



					Timestamp TransactionTimeStamp = rs.getTimestamp("transactionTime");

					String transactionTimeInString1 = String.valueOf(rs.getTimestamp("transactionTime"));

					/*Date newDate1 = sdf.parse(transactionTimeInString1);
					transactionTimeInString1=dateFormat.format(newDate1);*/
					
					// DF20170803: SU334449: Time Conversion Utility for GMT and LT Conversions
					GmtLtTimeConversion conversion = new GmtLtTimeConversion();
					// DF20170803: KO369761: If timezone is null we are setting default timzone to 5:30.
					if(timeZone == null)
						timeZone = "(GMT+05:30)";
					String convertedTimeStamp =  conversion.convertGmtToLocal(timeZone, transactionTimeInString1);
					assetDashboardImplObject.setLastPktReceivedTime(convertedTimeStamp);

					/*assetDashboardImplObject.setFuelLevel(rs.getString("Fuel_Level"));*/

					//parameters format in AMS
					//String currParam= LAT|LONG|Enginestatus|Machinehours|ExternalBatteryVoltage|HCT|LOP|InternalBatteryLow

					/*	String parameters=rs.getString("parameters");
					String [] currParamList = null;
					if(parameters!=null){
					currParamList=parameters.split("\\|", -1);

					assetDashboardImplObject.setLatitude(currParamList[0]);
					assetDashboardImplObject.setLongitude(currParamList[1]);
					assetDashboardImplObject.setEngineStatus(currParamList[2]);
					assetDashboardImplObject.setLifeHours(currParamList[3]);
					assetDashboardImplObject.setExternalBatteryInVolts(currParamList[4]);
					assetDashboardImplObject.setHighCoolantTemperature(currParamList[5]);
					assetDashboardImplObject.setLowEngineOilPressure(currParamList[6]);
					assetDashboardImplObject.setExternalBatteryStatus(currParamList[7]);
					}*/


					txnData=rs.getObject("TxnData").toString();

					txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());
					//DF20190923 @Mamatha Time spent in Dual mode will be displayed on the Fleet General tab for a VIN  
					String msg_id =  txnDataMap.get("MSG_ID");
					//DF20190923::Abhishek:null pointer check.
					DecimalFormat df2 = new DecimalFormat("#.##");
					if(msg_id!=null){
						if(msg_id.equals("030") || msg_id.equals("003")){
							ecModeHrs=txnDataMap.get("LOAD_EC_MOD_HRS")!=null? txnDataMap.get("LOAD_EC_MOD_HRS") :"0.0";
							powerModeHrs=txnDataMap.get("LOAD_PWR_MOD_HRS")!=null? txnDataMap.get("LOAD_PWR_MOD_HRS") :"0.0";
							
							//DF20190923::Abhishek:converted time of dual mode from sec to hr.
							ecModeHrs = ecModeHrs.replaceAll("[^0-9]", "");
							powerModeHrs = powerModeHrs.replaceAll("[^0-9]", "");
							ecModeHrs=String.valueOf((df2.format((Double.valueOf(ecModeHrs))/3600)));
							powerModeHrs=String.valueOf((df2.format((Double.valueOf(powerModeHrs))/3600)));
							
						} else{ 
							ecModeHrs="NA";
							powerModeHrs="NA";
						}
					}
					
					assetDashboardImplObject.setEcModeHrs(ecModeHrs);
					assetDashboardImplObject.setPowerModeHrs(powerModeHrs);
					assetDashboardImplObject.setFuelLevel(txnDataMap.get("FUEL_PERCT"));
					assetDashboardImplObject.setLatitude(txnDataMap.get("LAT"));
					assetDashboardImplObject.setLongitude(txnDataMap.get("LONG"));
					 enginestatus=txnDataMap.get("ENG_STATUS");
					if(enginestatus==null){
						enginestatus=txnDataMap.get("EVT_ENG");
					}
					if(enginestatus==null){
						enginestatus="0";
					}
					assetDashboardImplObject.setEngineStatus(enginestatus);
					assetDashboardImplObject.setLifeHours(txnDataMap.get("CMH"));
					assetDashboardImplObject.setExternalBatteryInVolts(txnDataMap.get("EXT_BAT_VOLT"));
					//DF20190327:mani: getting alerts for that vin and taking the active alerts
					if(rs.getObject("AlertStatus")!=null){
						AlertStatus=rs.getObject("AlertStatus").toString();
					if(AlertStatus!=null)
						AlertStatusMap = new Gson().fromJson(AlertStatus, new TypeToken<HashMap<String, String>>() {}.getType());
						if(AlertStatusMap.size()>0)
						{	
							 Iterator it = AlertStatusMap.entrySet().iterator();
							 while (it.hasNext()) {
							        Map.Entry eventids = (Map.Entry)it.next();
							        String activeStatus=(String)eventids.getValue();
							        if(activeStatus.equals("1"))
							        {
							        	activeEventids.add((String) eventids.getKey());
							        }
							        
							 }
							 if(activeEventids.size()>0)
								{
									
									activeEventidsString=convert.getStringWithoutQuoteList(activeEventids).toString();
									activeEventidsString=convert.removeLastComma(activeEventidsString);
								}
						}
					}
					/*assetDashboardImplObject.setHighCoolantTemperature(txnDataMap.get("EVT_HCT"));
					assetDashboardImplObject.setLowEngineOilPressure(txnDataMap.get("EVT_LEOP"));
					//DF20180105:KO369761 - External Battery removal referring to wrong param.
					//assetDashboardImplObject.setExternalBatteryStatus(txnDataMap.get("EVT_EXT_BAT"));
					assetDashboardImplObject.setExternalBatteryStatus(txnDataMap.get("EVT_BAT_REM"));*/




					statement1 = prodConnection.createStatement();
					
					/**
					 * DF20181129 - KO369761
					 * Fetching events data from asset_event instead of asset monitoring snapshot.
					 */
					//rs1=statement1.executeQuery("select GROUP_CONCAT(aee.Event_Severity) as severity, GROUP_CONCAT(aee.Event_Type_ID) as eventType from asset_event aee where aee.Serial_Number='"+rs.getString("Serial_Number")+"' and aee.Active_Status=1");
					//DF20190205-KO369761- asset event partition key added for better performance
					//DF20190327 :mani: introducing asset event snapshot
					/*long startTime_1 = System.currentTimeMillis();
					String query_new="select GROUP_CONCAT(aee.Event_Severity) as severity, GROUP_CONCAT(aee.Event_ID) as eventType from asset_event aee where aee.Serial_Number='"+rs.getString("Serial_Number")+"' and aee.Active_Status=1 and aee.PartitionKey = 1";
					rs1=statement1.executeQuery(query_new);
					long endTime_1=System.currentTimeMillis();
					iLogger.info("DynamicAMS_DAL :: "+query_new+"   ---->Query Execution Time in ms Dashboard:"+(endTime_1-startTime_1));*/
					
					List<String> eventSeverityValues = new LinkedList<String>();
					List<String> eventTypeIdValues = new LinkedList<String>();

					String serviceAlert=null;
					String LEOPAlert = null;
					String HECTAlert = null;
					String EMBRAlert = null;
					String eventType=null;
					String severity=null;
					
					
					//DF20190327:mani:introducing asset event snapshot, fetching the severity from business event
					if(activeEventidsString!=null){
					String severityQuery = "select GROUP_CONCAT(Event_Severity) as severity, GROUP_CONCAT(Event_ID) as eventType from business_event where Event_ID in ("
							+ activeEventidsString + ")";
					rs2=statement1.executeQuery(severityQuery);
					while(rs2.next()){

						eventType=rs2.getString("eventType");

						severity=rs2.getString("severity");

					}
					}
					//eventTypeIdValues=activeEventids;
					if(eventType!=null)
						eventTypeIdValues = Arrays.asList(eventType.split(","));
					else
						eventTypeIdValues=new LinkedList<String>();

					if(severity!=null)
						eventSeverityValues = Arrays.asList(severity.split(","));
					else
						eventSeverityValues=new LinkedList<String>();

					//find whether service notification is sent or not
					String currentServiceAlert=null;
					for(int j=0; j<eventTypeIdValues.size();j++)
					{
						//Checking for Service Alert Severity
						if(Integer.parseInt(eventTypeIdValues.get(j))==(3)
                                || Integer.parseInt(eventTypeIdValues.get(j))==(2)//ME100008483.n
                                || Integer.parseInt(eventTypeIdValues.get(j))==(1))//ME100008483.n
						{
							currentServiceAlert=eventSeverityValues.get(j);
							if(serviceAlert==null)
							{
								serviceAlert=currentServiceAlert;
							}
							else if(currentServiceAlert.compareToIgnoreCase("Red")==0
									|| currentServiceAlert.compareToIgnoreCase("Yellow")==0)//ME100008483.n
							{
								serviceAlert=currentServiceAlert;
							}
						}
						
						//Checking for Low Engine oil pressure Alert Severity
						else if(Integer.parseInt(eventTypeIdValues.get(j))==(7))
						{

							currentServiceAlert=eventSeverityValues.get(j);
							if(LEOPAlert==null)
							{
								LEOPAlert=currentServiceAlert;
							}
							else if(currentServiceAlert.compareToIgnoreCase("Red")==0)
							{
								LEOPAlert=currentServiceAlert;
							}

						}
						
						//Checking for High Engine coolant temperature Alert Severity
						else if(Integer.parseInt(eventTypeIdValues.get(j))==(9))
						{

							currentServiceAlert=eventSeverityValues.get(j);
							if(HECTAlert==null)
							{
								HECTAlert=currentServiceAlert;
							}
							else if(currentServiceAlert.compareToIgnoreCase("Red")==0)
							{
								HECTAlert=currentServiceAlert;
							}

						}
						
						//Checking for Machine battery removal Alert Severity
						else if(Integer.parseInt(eventTypeIdValues.get(j))==(20))
						{

							currentServiceAlert=eventSeverityValues.get(j);
							if(EMBRAlert==null)
							{
								EMBRAlert=currentServiceAlert;
							}
							else if(currentServiceAlert.compareToIgnoreCase("Red")==0)
							{
								EMBRAlert=currentServiceAlert;
							}

						}
					}

					//Setting  ServiceAlert status
					if(serviceAlert==null)
					{
						assetDashboardImplObject.setDueForService("Green");
					}

					else
					{
						assetDashboardImplObject.setDueForService(serviceAlert);
					}
					
					//Setting  Low Engine oil pressure status
					if(LEOPAlert != null && LEOPAlert.equalsIgnoreCase("RED"))
						assetDashboardImplObject.setLowEngineOilPressure("1");
					else
						assetDashboardImplObject.setLowEngineOilPressure("0");
					
					//Setting High Engine coolant temperature status
					if(HECTAlert != null && HECTAlert.equalsIgnoreCase("RED"))
						assetDashboardImplObject.setHighCoolantTemperature("1");
					else
						assetDashboardImplObject.setHighCoolantTemperature("0");
					
					//Setting Machine battery removal status
					if(EMBRAlert != null && EMBRAlert.equalsIgnoreCase("RED"))
						assetDashboardImplObject.setExternalBatteryStatus("1");
					else
						assetDashboardImplObject.setExternalBatteryStatus("0");

					//find whether any notifications has been sent or not for a given serial number
					if( !(eventSeverityValues==null || eventSeverityValues.isEmpty()) )
					{
						if( (eventSeverityValues.contains("Red")) || (eventSeverityValues.contains("RED")) || (eventSeverityValues.contains("red")) )
						{
							assetDashboardImplObject.setMachineStatus("Red");
						}
						else if ( (eventSeverityValues.contains("Yellow")) || (eventSeverityValues.contains("YELLOW")) || (eventSeverityValues.contains("yellow")) )
						{
							assetDashboardImplObject.setMachineStatus("Yellow");
						}
					}
					else
					{
						Timestamp maxTransactionTime = null;
						maxTransactionTime = TransactionTimeStamp;

						//get yesturday date time (past 24 hour)
						Date yesturday = null;
						Date maxTransDate = null;

						cal = Calendar.getInstance();
						cal.add(Calendar.DATE, -1);

						SimpleDateFormat dateFrmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						dateFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
						String str = dateFrmt.format(cal.getTime());
						yesturday = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);

						String maxDateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(maxTransactionTime);
						maxTransDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(maxDateString);

						if(maxTransDate.after(yesturday))
						{
							if(txnDataMap!=null){
								
								enginestatus=txnDataMap.get("ENG_STATUS");
								
								if(enginestatus==null){
									enginestatus=txnDataMap.get("EVT_ENG");	
								}
								if(enginestatus==null){
									enginestatus="0";
								}
								assetDashboardImplObject.setMachineStatus(enginestatus);
								
								//assetDashboardImplObject.setMachineStatus(txnDataMap.get("ENG_STATUS"));
							}
						}

						else
						{
							assetDashboardImplObject.setMachineStatus("Idle");
						}

					}

					Timestamp maxTransactionTime = null;
					maxTransactionTime = TransactionTimeStamp;


					sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
					String currentTime = sdf.format(new Date());
					String maxTxnTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(maxTransactionTime);

					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

					Date currentDate = sf.parse(currentTime);
					Date maxTxnDate = sf.parse(maxTxnTime);

					long t1= currentDate.getTime()/60000 ;
					long t2 = maxTxnDate.getTime()/60000;

					//If the difference is greater than 20 minutes
					if ( (t1-t2) > 20)
					{
						assetDashboardImplObject.setConnectivityStatus("0");
					}
					else
					{
						assetDashboardImplObject.setConnectivityStatus("1");
					}



					AssetDashboardImplList.add(assetDashboardImplObject);

					try{
						if(rs1!=null)
							try {
								rs1.close();
							} catch (SQLException e1) {
								e1.printStackTrace();
							}

						if(statement1!=null)
							try {
								statement1.close();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				
				long VinEndTime = System.currentTimeMillis();
				iLogger.info("for Vin DAL Class :: loginId::"+loginId+" TIME::"+(VinEndTime-VinstartTime));
				}
			}

			/**
			 * KO369761 - DF20181114 Capturing communication link failure
			 * exception and throwing as custom fault exception to UI.
			 ***/
			catch(CommunicationsException e){
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				e.printStackTrace();
				fLogger.fatal("loginId::"+loginId+":AMS:DAL-AMS-getQuerySpecificDetailsForMap"+"SQL Exception in fetching data from mysql::"+e.getMessage());
				fLogger.fatal(stack.toString());
				
				throw new CustomFault("SQL Exception");
			}
			
			catch (SQLException e) {

				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				e.printStackTrace();
				fLogger.fatal("loginId::"+loginId+":AMS:DAL-AMS-getQuerySpecificDetailsForMap"+"SQL Exception in fetching data from mysql::"+e.getMessage());
				fLogger.fatal(stack.toString());
				
				/**
				 * KO369761 - DF20181114 Throwing sql exception as custom fault
				 * exception to UI.
				 * **/
				throw new CustomFault("SQL Exception");
			} 

			catch(Exception e)
			{
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				e.printStackTrace();
				fLogger.fatal("loginId::"+loginId+":AMS:DAL-AMS-getQuerySpecificDetailsForMap"+"Exception in fetching data from mysql::"+e.getMessage());
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

		long endTime = System.currentTimeMillis();
		iLogger.info("end of the DAL Class :: loginId::"+loginId+" TIME::"+(endTime-startTime));
		return AssetDashboardImplList;
	}

	@Override
	public long getAssetDashBoardTotalCount(String Query) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;


		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String AMSSelectQuery=Query;

		long count=0;



		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			fLogger.fatal(":AMS:DAL-AMS-getAssetDashBoardTotalCount"+"Error in intializing property File :"+e.getMessage());
		}


		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info(":AMS:DAL-AMS-getAssetDashBoardTotalCount"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{

			/*<!=========START fetching from Native Database==============>*/


			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(AMSSelectQuery);


				while(rs.next()){
					count=rs.getLong("count");

				}
			}

			catch (SQLException e) {

				e.printStackTrace();
				fLogger.fatal(":AMS:DAL-AMS-getAssetDashBoardTotalCount"+"SQL Exception in fetching data from mysql::"+e.getMessage());
			} 

			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal(":AMS:DAL-AMS-getAssetDashBoardTotalCount"+"Exception in fetching data from mysql::"+e.getMessage());
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


		return count;
	}
	@Override
	public MapImpl getQuerySpecificDetailsForFleetMap(String Query, String loginId) {


		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;


		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String AMSSelectQuery=Query;

		MapImpl MapImplObject=new MapImpl();


		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			fLogger.fatal("login id::"+loginId+":AMS:DAL-AMS-getQuerySpecificDetailsForFleetMap"+"Error in intializing property File :"+e.getMessage());
		}


		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info(":AMS:DAL-AMS-getQuerySpecificDetailsForFleetMap"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{

			/*<!=========START fetching from Native Database==============>*/


			try{
				long startTime = System.currentTimeMillis();
				iLogger.info("Map Service:"+Query+":Get DB Connection - START loginid::"+loginId);
				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				long endTime = System.currentTimeMillis();
				iLogger.info("Map Service:"+Query+":Get DB Connection - END: Total time in ms:"+(endTime-startTime)+" loginid::"+loginId);
				
				startTime = System.currentTimeMillis();
				statement = prodConnection.createStatement();
				iLogger.info("Map Service:"+Query+":Execute Query - START loginid::"+loginId);
				rs = statement.executeQuery(AMSSelectQuery);
				endTime = System.currentTimeMillis();
				iLogger.info("Map Service:"+Query+":Execute Query - END: Total time in ms:"+(endTime-startTime)+" loginid::"+loginId);
					
				HashMap<String,String> txnDataMap=new HashMap<String, String>();
				String txnData;
				String enginestatus=null;

				startTime = System.currentTimeMillis();
				iLogger.info("Map Service:"+Query+":Fill response Object- START: Total time in ms:"+(endTime-startTime)+" loginid::"+loginId);
				while(rs.next()){

					MapImplObject.setSerialNumber(rs.getString("Serial_Number"));
					MapImplObject.setNickname(rs.getString("Engine_Number"));
					MapImplObject.setProfileName(rs.getString("Asseet_Group_Name"));
					MapImplObject.setOperatingStartTime(rs.getTimestamp("operatingStartTime"));
					MapImplObject.setOperatingEndTime(rs.getTimestamp("operatingEndTime"));



					//parameters format in AMS
					//String currParam= LAT|LONG|Enginestatus|Machinehours|ExternalBatteryVoltage|HCT|LOP|InternalBatteryLow

					/*String parameters=rs.getString("parameters");
					if(parameters!=null){
					String [] currParamList=parameters.split("\\|", -1);

					MapImplObject.setLatitude(currParamList[0]);
					MapImplObject.setLongitude(currParamList[1]);
					MapImplObject.setEngineStatus(currParamList[2]);
					MapImplObject.setTotalMachineHours(currParamList[3]);

					}*/

					//DF20161221 @Roopa FEtching fleet map details from the txndata json column in assetmonitoringsnapshot table

					txnData=rs.getObject("TxnData").toString();

					txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());
					MapImplObject.setLatitude(txnDataMap.get("LAT"));
					MapImplObject.setLongitude(txnDataMap.get("LONG"));
					
					enginestatus=txnDataMap.get("ENG_STATUS");
					
					if(enginestatus==null){
						enginestatus=txnDataMap.get("EVT_ENG");	
					}
					if(enginestatus==null){
						enginestatus="0";
					}
					MapImplObject.setEngineStatus(enginestatus);
					//MapImplObject.setEngineStatus(txnDataMap.get("ENG_STATUS"));
					MapImplObject.setTotalMachineHours(txnDataMap.get("CMH"));

				}
				endTime = System.currentTimeMillis();
				iLogger.info("Map Service:"+Query+":Fill response Object- END: Total time in ms:"+(endTime-startTime)+" loginid::"+loginId);
			}

			catch (SQLException e) {
				e.printStackTrace();
				fLogger.fatal("login id::"+loginId+":AMS:DAL-AMS-getQuerySpecificDetailsForFleetMap"+"SQL Exception in fetching data from mysql::"+e.getMessage());
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				fLogger.fatal(stack.toString());
			} 

			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("login id::"+loginId+":AMS:DAL-AMS-getQuerySpecificDetailsForFleetMap"+"Exception in fetching data from mysql::"+e.getMessage());
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
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


		return MapImplObject;

	}
	public List<AmsDAO> getFWversion(String txnKey) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;


		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String AMSSelectQuery=null;
		

		List<AmsDAO>AmsDAOList=new ArrayList<AmsDAO>();


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



			/*AMSSelectQuery = "select ag.asseet_Group_Name,ams.parameters,a.Serial_Number "+ 
					"from asset_monitoring_snapshot_new ams,asset a,products p ,asset_group ag "+ 
					"where ams.Serial_Number = a.Serial_Number and p.product_ID = a.product_ID and ag.Asset_Group_ID = p.Asset_Group_ID ";
*/
			AMSSelectQuery = "select ag.asseet_Group_Name,ams.TxnData,a.Serial_Number "+ 
			"from asset_monitoring_snapshot ams,asset a,products p ,asset_group ag "+ 
			"where ams.Serial_Number = a.Serial_Number and p.product_ID = a.product_ID and ag.Asset_Group_ID = p.Asset_Group_ID ";

			//iLogger.info(txnKey+":AMS:DAL-AMS-getAMsData"+"AMSSelectQuery::"+AMSSelectQuery);
			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(AMSSelectQuery);
				HashMap<String,String> txnDataMap=new HashMap<String, String>();
				String txnData=rs.getObject("TxnData").toString();

				txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());


				AmsDAO amsDAOobject; 

				while(rs.next()){
					amsDAOobject=new AmsDAO();

					amsDAOobject.setSerial_Number(rs.getString("Serial_Number"));
					amsDAOobject.setAsset_group_name(rs.getString("asseet_Group_Name"));
				//  amsDAOobject.setParameters(rs.getString("parameters"));
					amsDAOobject.setTxnData(txnDataMap);
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

	
	//DF20161222 @Roopa changing asset_monitoring_snapshot_new to asset_monitoring_snapshot which is having txndata as json column
	@Override
	public List<HAJAssetLocationDetailsEntity> getQuerySpecificDetailsForHAJAssetDetails(
			String Query) {


		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;


		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String AMSSelectQuery=Query;

		List<HAJAssetLocationDetailsEntity> responseList=new LinkedList<HAJAssetLocationDetailsEntity>();
		HAJAssetLocationDetailsEntity implObj;


		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			fLogger.fatal(":AMS:DAL-AMS-getQuerySpecificDetailsForHAJAssetDetails"+"Error in intializing property File :"+e.getMessage());
		}


		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info(":AMS:DAL-AMS-getQuerySpecificDetailsForHAJAssetDetails"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{

			/*<!=========START fetching from Native Database==============>*/


			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(AMSSelectQuery);

				HashMap<String,String> txnDataMap=new HashMap<String, String>();
				String txnData;

				while(rs.next()){
					implObj=new HAJAssetLocationDetailsEntity();

					implObj.setSerialNumber(rs.getString("Serial_Number"));


					//parameters format in AMS
					//String currParam= LAT|LONG|Enginestatus|Machinehours|ExternalBatteryVoltage|HCT|LOP|InternalBatteryLow

					/*String parameters=rs.getString("parameters");
					String [] currParamList=parameters.split("\\|", -1);

					implObj.setLatitude(currParamList[0]);
					implObj.setLongitude(currParamList[1]);*/
					
					 txnData=rs.getObject("TxnData").toString();

					 txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());
					 
					 implObj.setLatitude(txnDataMap.get("LAT"));
					 implObj.setLongitude(txnDataMap.get("LONG"));
					 
					implObj.setSendFlag(1);

					responseList.add(implObj);
				}
			}

			catch (SQLException e) {

				e.printStackTrace();
				fLogger.fatal(":AMS:DAL-AMS-getQuerySpecificDetailsForHAJAssetDetails"+"SQL Exception in fetching data from mysql::"+e.getMessage());
			} 

			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal(":AMS:DAL-AMS-getQuerySpecificDetailsForHAJAssetDetails"+"Exception in fetching data from mysql::"+e.getMessage());
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


		return responseList;

	}	

	public List<AsseControlUnitDAO> getAMSDataOn_RegistrationDate(String txnKey,String fromDate,String toDate,String serialNumberList) throws CustomFault {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String AMSSelectQuery=null;
		String AMSTx_TSQuery = null;
		List<AsseControlUnitDAO>AmsDAOList=new ArrayList<AsseControlUnitDAO>();
		String fromDate1 = null;
		String toDate1 = null;
		Connection edgeProxyConnection = null;
		ResultSet edgeproxyRs = null;
		 String fwVersion="00.00.00";

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

			/*AMSSelectQuery = "select acu.Serial_Number,acu.SIM_No,acu.IMEI_Number,acu.Registration_Date "+
					"from asset_control_unit acu where acu.Serial_Number not in (select Serial_Number from asset_monitoring_snapshot_new ) ";*/
			
			//DF20161222 @Roopa assetsnapshot table format changed to json
			/*AMSSelectQuery = "select acu.Serial_Number,acu.SIM_No,acu.IMEI_Number,acu.Registration_Date "+
					"from asset_control_unit acu where acu.Serial_Number not in (select Serial_Number from asset_monitoring_snapshot ) ";*/

			//DF20181023-KO369761 - Query changed for performance improvement.
			/*AMSSelectQuery = "select acu.Serial_Number,acu.SIM_No,acu.IMEI_Number,acu.Registration_Date " +
							"from asset_control_unit acu left outer join asset_monitoring_snapshot ams " +
							"on acu.Serial_Number = ams.Serial_Number where ams.Serial_Number is null";*/
			/**
			 * DF20200429 - Zakir
			 * Updating AMSSelectQuery to obtain the reason for the machine
			 * being in New machine tab
			 * New column Name will be Comments 
			 */
		
			/*AMSSelectQuery = "select acu.Serial_Number,acu.SIM_No,acu.IMEI_Number,acu.Registration_Date, ast.Serial_Number as ast_sn,acc.account_Name, "
					+"if(ast.serial_number is null, 'Machine is not yet rolled off','Machine has not communicated after owner movement') as Comment "
					+"from asset_control_unit acu "
					+"left outer join asset_monitoring_snapshot ams "
					+"on acu.Serial_Number = ams.Serial_Number "
					+"left outer join asset ast "
					+"on acu.Serial_number = ast.Serial_number "
					+"left outer join account acc "
					+"on ast.primary_Owner_Id=acc.Account_Id "
					+"where ams.Serial_Number is null";*/
			
			//2021-03-18 : Shajesh : addition of dealer name in NMT
			AMSSelectQuery = "select acu.Serial_Number,acu.SIM_No,acu.IMEI_Number,acu.Registration_Date, ast.Serial_Number as ast_sn,acc.account_Name,croe.Dealer_Name, "
					+"if(ast.serial_number is null, 'Machine is not yet rolled off','Machine has not communicated after owner movement') as Comment "
					+"from asset_control_unit acu "
					+"left outer join asset_monitoring_snapshot ams "
					+"on acu.Serial_Number = ams.Serial_Number "
					+"left outer join asset ast "
					+"on acu.Serial_number = ast.Serial_number "
					+"left outer join account acc "
					+"on ast.primary_Owner_Id=acc.Account_Id "
					+"left outer join com_rep_oem_enhanced croe "
					+"on ast.serial_number=croe.serial_number "
					+"where ams.Serial_Number is null"; 
			
			if(serialNumberList != null ){

				AMSSelectQuery += " and acu.Serial_Number in ("+serialNumberList+") " ; 
				
			}

			if(fromDate!=null && toDate !=null){
				fromDate1 = fromDate + " 00:00:00";
				toDate1 = toDate + " 23:59:59";
				AMSSelectQuery += " and acu.Registration_Date >= '"+fromDate1+"' and acu.Registration_Date<='"+toDate1+"'";
			}
			
			//DF20181023-KO369761 - Added descending order in new machines query.
			AMSSelectQuery +=" order by acu.Registration_Date desc";
			iLogger.info("********* AMSSelectQuery ********* "+AMSSelectQuery);
			//System.out.println(AMSSelectQuery);

			
			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(AMSSelectQuery);
				edgeProxyConnection = connMySql.getEdgeProxyConnection();

				AsseControlUnitDAO amsDAOobject; 
				Timestamp CreatedTimestamp = null;
				Timestamp Rolloff_Date = null;
				while(rs.next()){
					amsDAOobject=new AsseControlUnitDAO();
					//amsDAOobject.setFuel_Level(rs.getString("Fuel_Level"));
					amsDAOobject.setRegistrationDate(rs.getTimestamp("Registration_Date"));
					//	amsDAOobject.setLatest_Event_Transaction(rs.getInt("Latest_Event_Transaction"));
					//amsDAOobject.setLatest_Fuel_Transaction(rs.getInt("Latest_Fuel_Transaction"));
					//amsDAOobject.setLatest_Log_Transaction(rs.getInt("Latest_Log_Transaction"));
					//amsDAOobject.setSerialNumber(rs.getString("Serial_Number")+"~"+rs.getString("account_Name"));
					//2021-03-18 : Shajesh : addition of dealer name in NMT
					if(rs.getString("Dealer_Name") != null){
						amsDAOobject.setSerialNumber(rs.getString("Serial_Number")+"~"+rs.getString("account_Name")+"~"+rs.getString("Dealer_Name"));
					}else{
						amsDAOobject.setSerialNumber(rs.getString("Serial_Number")+"~"+rs.getString("account_Name")+"~"+"NA");
					}	
					//amsDAOobject.setTransaction_Number(rs.getInt("Transaction_Number"));
					//amsDAOobject.setTransaction_Timestamp_Evt(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Evt")));
					//amsDAOobject.setTransaction_Timestamp_Fuel(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Fuel")));
					//amsDAOobject.setTransaction_Timestamp_Log(String.valueOf(rs.getTimestamp("Transaction_Timestamp_Log")));
					amsDAOobject.setSimNo(rs.getString("SIM_No"));
					amsDAOobject.setImeiNo(rs.getString("IMEI_Number"));
					//DF20200429 - Zakir : Capturing the comments for each VIN 
					amsDAOobject.setComment(rs.getString("Comment"));
					//serial no, imsi no, > saarc, 
					// mip, nip > ll2 ll4

					//CR481-20240731-Sai Divya-Status,proposedFWVersion,fWVersion columns are added to NMT
//					String deviceType=null; 
//					String serialNumber =amsDAOobject.getSerialNumber().substring(0, 17);  
//					String imsiNumber = amsDAOobject.getSimNo();        
//					String FWQuery = "SELECT device_MIP_version, device_version FROM Fota_Track WHERE vin_no = '" + serialNumber + "'";
//					try (Statement edgeProxyStatement = edgeProxyConnection.createStatement();
//							ResultSet edgeProxyRs = edgeProxyStatement.executeQuery(FWQuery)) {
//						// Process the result set for each serial number
//						if (edgeProxyRs.next()) {
//							String mipVersion = edgeProxyRs.getString("device_MIP_version");
//							String deviceVersion = edgeProxyRs.getString("device_version");
//							if (deviceVersion==null) {
//								deviceType = "LL4";
//							}
//							else {
//								deviceType = "LL2";
//							} 
//							//Fetch mds_code based on serialNumber
//							String mdsCode = null;
//							mdsCode = serialNumber.substring(3, 8);
//							if (deviceType.equals("LL2")) 
//							{
//								if (imsiNumber != null) {
//									if (imsiNumber.startsWith("404100")) {
//										// Airtel SIM detected
//										updateMachineType(mdsCode, "1"); 
//									} else if (imsiNumber.startsWith("2040")) {
//										// SAARC SIM detected
//										updateMachineType(mdsCode, "3"); 
//									} else {
//										// Other (VI or Jio) SIM detected
//										updateMachineType(mdsCode, "2"); 
//									}
//								}
//
//							}
//							// After processing LL2 or LL4, query fw_versions for mds_code
//							String masterQuery = "SELECT fw_version FROM device_versions WHERE mds_code = '" + mdsCode + "' order by fw_version desc";
//							List<String> fwVersions = new ArrayList<>();
//							try (Statement prodStatement = prodConnection.createStatement();
//									ResultSet fwRs = prodStatement.executeQuery(masterQuery)) {
//								iLogger.info(masterQuery);
//								// Populate fwVersions list with all fw_version values from the query result
//								while (fwRs.next()) {
//									String fwVersion1 = fwRs.getString("fw_version");
//									fwVersions.add(fwVersion1);
//								}
//
//								// Check if mipVersion matches any fw_versions
//								boolean matched = false;
//								if (fwVersions.contains(mipVersion)) {
//									amsDAOobject.setStatus("1");
//									amsDAOobject.setfWVersion(mipVersion);
//									iLogger.info("mipVersion and fw_Version are not matched");
//									matched = true;
//								}
//
//								// If no match found and fwVersions is not empty, set proposed FW version to the last one in the list
//								if (!matched && !fwVersions.isEmpty()) {
//									String lastFWVersion = fwVersions.get(0);
//									amsDAOobject.setfWVersion(mipVersion);
//									amsDAOobject.setStatus("0");
//									iLogger.info("mipVersion and fw_Version are not matched");
//									amsDAOobject.setProposedFWVersion(lastFWVersion);
//								}
//
//							} catch (SQLException e) {
//								e.printStackTrace();
//								// Handle SQLException as needed
//							}
//
//						}
//
//					}
//					catch (SQLException e) {
//						e.printStackTrace();
//						// Handle SQLException as needed
//					}
					AmsDAOList.add(amsDAOobject);

				}


			}

			

			/**
			 * KO369761 - DF20181114 Capturing communication link failure
			 * exception and throwing as custom fault exception to UI.
			 ***/
			catch(CommunicationsException e){
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				e.printStackTrace();
				fLogger.fatal(stack.toString());

				throw new CustomFault("SQL Exception");
			}

			catch (SQLException e) {
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				e.printStackTrace();
				fLogger.fatal(txnKey+":AMS:DAL-AMS-getAMsData"+"SQL Exception in fetching data from mysql::"+e.getMessage());
				fLogger.fatal(stack.toString());
				
				/**
				 * KO369761 - DF20181114 Throwing sql exception as custom fault
				 * exception to UI.
				 ***/
				throw new CustomFault("SQL Exception");
			} 

			catch(Exception e)
			{
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				e.printStackTrace();
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
	
	
	// Method to update machine_type in device_versions table
    private void updateMachineType(String mdsCode, String machineType) {
   	Connection prodConnection=null;
   	ConnectMySQL connMySql = new ConnectMySQL();
	prodConnection = connMySql.getConnection();
   	Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
        String updateQuery = "UPDATE device_versions SET machine_type = ? WHERE mds_code = ?";
        try (PreparedStatement updateStatement = prodConnection.prepareStatement(updateQuery)) {
       	 updateStatement.setString(1, machineType);
       	 updateStatement.setString(2, mdsCode);
            
            int rowsUpdated = updateStatement.executeUpdate();
            if (rowsUpdated > 0) {
                iLogger.info("Updated machine_type for mds_code " + mdsCode + " to " + machineType);
            } else {
                iLogger.warn("Failed to update machine_type for mds_code " + mdsCode);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQLException as needed
        }
    }
}

