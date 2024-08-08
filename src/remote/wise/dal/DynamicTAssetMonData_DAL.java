//CR290-20220309-Balaji-Download7Day report for BHLBS4 Machine communicating with 050 Msg_id
package remote.wise.dal;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.TAssetMonDataDAO;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;

import org.json.simple.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author ROOPN5
 *
 */
public class DynamicTAssetMonData_DAL{

	public List<TAssetMonDataDAO> getPrevTAssetMonData(String txnKey,
			String Serial_Number, Timestamp txnTimestamp, int seg_ID, String event) {



		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		String TAssetMonDataSelectQuery=null;

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;

		List<TAssetMonDataDAO>tAssetMonDaoList=new ArrayList<TAssetMonDataDAO>();

		Properties prop=null;
		try
		{
			prop= CommonUtil.getDepEnvProperties();
		}
		catch(Exception e)
		{
			fLogger.fatal(txnKey+":AMS:DAL:TAssetMonData-getTAssetMonData"+"Error in intializing property File :"+e.getMessage());
		}

		String TAssetMonTable = prop.getProperty("default_TAssetMonData_Table");


		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*	<!=========START persisting in In memory==============>*/
			iLogger.info(txnKey+":AMS:DAL:TAssetMonData-getTAssetMonData"+"Persist to In Memory storage");
			/*	<!=========END persisting in In memory==============>*/
		}
		else{
			if(txnTimestamp!=null){

				/*	<!=========START persisting in In Native Database==============>*/


				String DynamicTAssetMonTable = new DateUtil().getDynamicTable(txnKey, txnTimestamp);

				if(DynamicTAssetMonTable!=null){

					TAssetMonTable = DynamicTAssetMonTable;
				}
				iLogger.info(txnKey+":AMS:DAL:TAssetMonData-getTAssetMonData"+"TAssetMonData Table::"+TAssetMonTable);

				if(TAssetMonTable!=null){
					TAssetMonDataSelectQuery="Select * from "+TAssetMonTable+" where Segment_ID_TxnDate="+seg_ID+" and Serial_Number='"+Serial_Number+"' and Transaction_Timestamp < '"+txnTimestamp+"' and Events -> '$."+event+"' is not null order by Transaction_Timestamp desc limit 1";
						iLogger.info(txnKey+":AMS:DAL:TAssetMonData-getTAssetMonData"+" Select Query::"+TAssetMonDataSelectQuery);

					try{

						ConnectMySQL connMySql = new ConnectMySQL();
						prodConnection = connMySql.getDatalakeConnection_3309();
						
						statement = prodConnection.createStatement();
						rs = statement.executeQuery(TAssetMonDataSelectQuery);

						TAssetMonDataDAO tAssetMonDAOobject; 

						String messageID;
						String txnData;
						String events;

						HashMap<String,String> messageIDMap=new HashMap<String, String>();

						HashMap<String,String> txnDataMap=new HashMap<String, String>();

						HashMap<String,String> eventsMap = new HashMap<String, String>();

						while(rs.next()){
							tAssetMonDAOobject=new TAssetMonDataDAO();
							tAssetMonDAOobject.setCreated_Timestamp(String.valueOf(rs.getTimestamp("Created_Timestamp")));
							tAssetMonDAOobject.setFW_Version_Number(rs.getString("FW_Version_Number"));
							tAssetMonDAOobject.setLast_Updated_Timestamp(String.valueOf(rs.getTimestamp("Last_Updated_Timestamp")));
							tAssetMonDAOobject.setSegment_ID_TxnDate(rs.getInt("Segment_ID_TxnDate"));
							tAssetMonDAOobject.setSerial_Number(rs.getString("Serial_Number"));
							tAssetMonDAOobject.setTransaction_Timestamp(String.valueOf(rs.getTimestamp("Transaction_Timestamp")));
							tAssetMonDAOobject.setUpdate_Count(rs.getInt("Update_Count"));

							messageID=rs.getObject("Message_ID").toString();

							txnData=rs.getObject("TxnData").toString();

							events=rs.getObject("Events").toString();

							messageIDMap = new Gson().fromJson(messageID, new TypeToken<HashMap<String, Object>>() {}.getType());

							txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());

							eventsMap = new Gson().fromJson(events, new TypeToken<HashMap<String, Object>>() {}.getType());

							tAssetMonDAOobject.setMessage_ID(messageIDMap);
							tAssetMonDAOobject.setTxnData(txnDataMap);
							tAssetMonDAOobject.setEvents(eventsMap);

							tAssetMonDaoList.add(tAssetMonDAOobject);
						}
					}

					catch (SQLException e) {

						e.printStackTrace();
						fLogger.fatal(txnKey+":AMS:DAL:TAssetMonData-getTAssetMonData"+"SQL Exception in fetching data from mysql::"+e.getMessage());
					} 

					catch(Exception e)
					{
						e.printStackTrace();
						fLogger.fatal(txnKey+":AMS:DAL:TAssetMonData-getTAssetMonData"+"Exception in fetching data from mysql::"+e.getMessage());
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





				/*<!=========END persisting in In Native Database==============>*/
			}

		}


		return tAssetMonDaoList;

	}

	public static List<TAssetMonDataDAO> getTAssetMonData(String txnKey,
			String Serial_Number, String txnTimestamp, int seg_ID) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;

		String TAssetMonDataSelectQuery=null;

		List<TAssetMonDataDAO>tAssetMonDaoList=new ArrayList<TAssetMonDataDAO>();

		Properties prop=null;
		try
		{
			prop = CommonUtil.getDepEnvProperties();
		}
		catch(Exception e)
		{
			fLogger.fatal(txnKey+":AMS:DAL:TAssetMonData-getTAssetMonData"+"Error in intializing property File :"+e.getMessage());
		}

		String TAssetMonTable = prop.getProperty("default_TAssetMonData_Table");


		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*	<!=========START persisting in In memory==============>*/
			iLogger.info(txnKey+":AMS:DAL:TAssetMonData-getTAssetMonData"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{
			if(txnTimestamp!=null){

				/*<!=========START persisting in In Native Database==============>*/

				SimpleDateFormat dateTimeFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


				Date txnDate = null;
				try {
					txnDate = dateTimeFormat.parse(txnTimestamp);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String DynamicTAssetMonTable = new DateUtil().getDynamicTable(txnKey, txnDate);

				if(DynamicTAssetMonTable!=null){

					TAssetMonTable = DynamicTAssetMonTable;
				}

				iLogger.info(txnKey+":AMS:DAL:TAssetMonData-getTAssetMonData"+"TAssetMonData Table::"+TAssetMonTable);

				if(TAssetMonTable!=null){
					TAssetMonDataSelectQuery="Select * from "+TAssetMonTable+" where Segment_ID_TxnDate="+seg_ID+" and Serial_Number='"+Serial_Number+"' and Transaction_Timestamp='"+txnTimestamp+"'";
					iLogger.info(txnKey+":AMS:DAL:TAssetMonData-getTAssetMonData"+" Select Query::"+TAssetMonDataSelectQuery);
				}

				try{

					ConnectMySQL connMySql = new ConnectMySQL();
					prodConnection = connMySql.getDatalakeConnection_3309();
					statement = prodConnection.createStatement();
					rs = statement.executeQuery(TAssetMonDataSelectQuery);

					TAssetMonDataDAO tAssetMonDAOobject; 

					String messageID;
					String txnData;
					String events;

					HashMap<String,String> messageIDMap=new HashMap<String, String>();

					HashMap<String,String> txnDataMap=new HashMap<String, String>();

					HashMap<String,String> eventsMap = new HashMap<String, String>();

					while(rs.next()){
						tAssetMonDAOobject=new TAssetMonDataDAO();
						tAssetMonDAOobject.setCreated_Timestamp(String.valueOf(rs.getTimestamp("Created_Timestamp")));
						tAssetMonDAOobject.setFW_Version_Number(rs.getString("FW_Version_Number"));
						tAssetMonDAOobject.setLast_Updated_Timestamp(String.valueOf(rs.getTimestamp("Last_Updated_Timestamp")));
						tAssetMonDAOobject.setSegment_ID_TxnDate(rs.getInt("Segment_ID_TxnDate"));
						tAssetMonDAOobject.setSerial_Number(rs.getString("Serial_Number"));
						tAssetMonDAOobject.setTransaction_Timestamp(String.valueOf(rs.getTimestamp("Transaction_Timestamp")));
						tAssetMonDAOobject.setUpdate_Count(rs.getInt("Update_Count"));

						messageID=rs.getObject("Message_ID").toString();

						txnData=rs.getObject("TxnData").toString();

						events=rs.getObject("Events").toString();

						messageIDMap = new Gson().fromJson(messageID, new TypeToken<HashMap<String, Object>>() {}.getType());

						txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());

						eventsMap = new Gson().fromJson(events, new TypeToken<HashMap<String, Object>>() {}.getType());

						tAssetMonDAOobject.setMessage_ID(messageIDMap);
						tAssetMonDAOobject.setTxnData(txnDataMap);
						tAssetMonDAOobject.setEvents(eventsMap);

						tAssetMonDaoList.add(tAssetMonDAOobject);
					}
				}

				catch (SQLException e) {

					e.printStackTrace();
					fLogger.fatal(txnKey+":AMS:DAL:TAssetMonData-getTAssetMonData"+"SQL Exception in fetching data from mysql::"+e.getMessage());
				} 

				catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal(txnKey+":AMS:DAL:TAssetMonData-getTAssetMonData"+"Exception in fetching data from mysql::"+e.getMessage());
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



				/*<!=========END persisting in In Native Database==============>*/
			}

		}


		return tAssetMonDaoList;
	}

	public static String setTAssetMonData( String txnKey, String Serial_Number,
			String txnTimestamp, Timestamp currentTS,
			HashMap<String, String> messageIDMap,
			HashMap<String, String> Events, HashMap<String, String> payloadMap,
			int seg_ID, String FW_Version_Number) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		Connection prodConnection = null;
		Statement statement = null;
		String TAssetMonDataInsertQuery=null;




		Properties prop=null;
		try
		{
			prop = CommonUtil.getDepEnvProperties();
		}
		catch(Exception e)
		{
			fLogger.fatal(txnKey+":AMS:DAL:TAssetMonData-setTAssetMonData::"+"Error in intializing property File :"+e.getMessage());
		}

		String TAssetMonTable = prop.getProperty("default_TAssetMonData_Table");


		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info(txnKey+":AMS:DAL:TAssetMonData-setTAssetMonData::"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{
			if(txnTimestamp!=null){

				Timestamp currentTime = currentTS;

				/*<!=========START persisting in In Native Database==============>*/

				SimpleDateFormat dateTimeFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


				Date txnDate = null;
				try {
					txnDate = dateTimeFormat.parse(txnTimestamp);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String DynamicTAssetMonTable = new DateUtil().getDynamicTable(txnKey, txnDate);

				if(DynamicTAssetMonTable!=null){

					TAssetMonTable = DynamicTAssetMonTable;
				}
				iLogger.info(txnKey+":AMS:DAL:TAssetMonData-setTAssetMonData"+"TAssetMonData Table::"+TAssetMonTable);

				String message_IDMap= new JSONObject(messageIDMap).toString();
				String txnDataMap= new JSONObject(payloadMap).toString();
				String eventMap= new JSONObject(Events).toString();

				if(TAssetMonTable!=null){
					TAssetMonDataInsertQuery="INSERT INTO "+TAssetMonTable+"" + "(Transaction_Timestamp, Serial_Number, Created_Timestamp, FW_Version_Number, Last_Updated_Timestamp, Update_Count, Segment_ID_TxnDate, Message_ID, Events, TxnData) VALUES"
							+ "('"+txnTimestamp+"','"+Serial_Number+"','"+currentTime+"','"+FW_Version_Number+"','"+currentTime+"',0,"+seg_ID+",'"+message_IDMap+"','"+eventMap+"','"+txnDataMap+"')";

					iLogger.info(txnKey+":AMS:DAL:TAssetMonData-setTAssetMonData"+" Insert Query::"+TAssetMonDataInsertQuery);	
				}

				try{

					ConnectMySQL connMySql = new ConnectMySQL();
					prodConnection = connMySql.getDatalakeConnection_3309();
					statement = prodConnection.createStatement();

					int insertCount= statement.executeUpdate(TAssetMonDataInsertQuery);

					iLogger.info(txnKey+":AMS:DAL:TAssetMonData-setTAssetMonData"+" insert count::"+insertCount);	

				}

				catch (SQLException e) {

					e.printStackTrace();
					fLogger.fatal("SQL Exception in inserting records to table::"+TAssetMonTable+""+"::"+e.getMessage());
					return "FAILURE";
				} 

				catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("Exception in inserting records to table::"+TAssetMonTable+""+"::"+e.getMessage());
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

	public static String updateTAssetMonData(String txnKey, String Serial_Number,
			String txnTimestamp, Timestamp currentTS,
			String messageId,
			HashMap<String, String> Events, HashMap<String, String> payloadMap,
			int seg_ID, String FW_Version_Number, int Update_Count) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		Connection prodConnection = null;
		Statement statement = null;

		String TAssetMonDataUpdateQuery=null;




		Properties prop=null;
		try
		{
			prop = CommonUtil.getDepEnvProperties();
		}
		catch(Exception e)
		{
			fLogger.fatal(txnKey+":AMS:DAL:TAssetMonData-updateTAssetMonData::"+"Error in intializing property File :"+e.getMessage());
		}

		String TAssetMonTable = prop.getProperty("default_TAssetMonData_Table");


		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info(txnKey+":AMS:DAL:TAssetMonData-updateTAssetMonData::"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{
			if(txnTimestamp!=null){

				Timestamp currentTime = currentTS;

				/*<!=========START persisting in In Native Database==============>*/

				SimpleDateFormat dateTimeFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


				Date txnDate = null;
				try {
					txnDate = dateTimeFormat.parse(txnTimestamp);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String DynamicTAssetMonTable = new DateUtil().getDynamicTable(txnKey, txnDate);

				if(DynamicTAssetMonTable!=null){

					TAssetMonTable = DynamicTAssetMonTable;
				}
				iLogger.info(txnKey+":AMS:DAL:TAssetMonData-updateTAssetMonData"+"TAssetMonData Table::"+TAssetMonTable);


				if(TAssetMonTable!=null){

					TAssetMonDataUpdateQuery = "Update "+TAssetMonTable+" set Last_Updated_Timestamp='"+currentTime+"', Update_Count="+Update_Count+", Message_ID = JSON_SET(Message_ID, '$."+messageId+"', '1') ";

					//Updating Event Column for Event packet
					for(Map.Entry<String, String> eventData : Events.entrySet())
					{
						TAssetMonDataUpdateQuery=TAssetMonDataUpdateQuery+", Events = JSON_SET(Events, '$."+eventData.getKey()+"', '"+eventData.getValue()+"') ";
					}

					//Updating the received parameter list
					for(Map.Entry<String, String> dataPayload : payloadMap.entrySet())
					{
						if( (dataPayload.getKey().equalsIgnoreCase("FUEL_PERCT")) && (dataPayload.getValue()!=null) && (dataPayload.getValue().contains("110")) )
						{
							//Do nothing
						}
						else
							TAssetMonDataUpdateQuery=TAssetMonDataUpdateQuery+", TxnData = JSON_SET(TxnData, '$."+dataPayload.getKey()+"', '"+dataPayload.getValue()+"') ";
					}

					TAssetMonDataUpdateQuery=TAssetMonDataUpdateQuery+" where Segment_ID_TxnDate="+seg_ID+" and Serial_Number='"+Serial_Number+"' and Transaction_Timestamp='"+txnTimestamp+"'";

					iLogger.info(txnKey+":AMS:DAL:TAssetMonData-updateTAssetMonData"+" Update Query::"+TAssetMonDataUpdateQuery);	
				}

				try{

					ConnectMySQL connMySql = new ConnectMySQL();
					prodConnection = connMySql.getDatalakeConnection_3309();					
					statement = prodConnection.createStatement();

					int insertCount= statement.executeUpdate(TAssetMonDataUpdateQuery);

					iLogger.info(txnKey+":AMS:DAL:TAssetMonData-updateTAssetMonData"+" update count::"+insertCount);	

				}

				catch (SQLException e) {

					e.printStackTrace();
					fLogger.fatal("SQL Exception in updating records to table::"+TAssetMonTable+""+"::"+e.getMessage());
					return "FAILURE";
				} 

				catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("Exception in updating records to table::"+TAssetMonTable+""+"::"+e.getMessage());
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

	//DF20190731:Abhishek::Changed logic for CAN machines.
	public HashMap<String, String> getMachinesHoursData(String txnKey, String Serial_Number,
			String txnDate, int seg_ID, String assetTypeName) {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		//CR-290 s.n
		double workingHour = 0L;
		double lowIdleHour = 0L;
		double highIdleHour = 0L;
		double loadJobHour = 0L;
		double exaStdModHour = 0L;
		double exaPlusModHour = 0L;
		double atchOpHour = 0L;
		double roadJobHour = 0L;
		double exaEcoModHour = 0L;
		//CR-290 e.n

		String TAssetMonDataSelectQuery = null;
		HashMap<String, String> machineHoursDataMap = new HashMap<String, String>();
		HashMap<String, String> txnDataMap = new HashMap<String, String>();
		//CR290.sn
		Map<String, ArrayList<Double>> idleHourMap = new LinkedHashMap<String, ArrayList<Double>>();
		ArrayList<Double> lowIdleList= new ArrayList<>();
		ArrayList<Double> highIdleList= new ArrayList<>();
		ArrayList<Double> workingList= new ArrayList<>();
		ArrayList<Double> LoadJobHrs=new ArrayList<>();
		ArrayList<Double> ExaStdModHrs =new ArrayList<>();
		ArrayList<Double> ExaEcoModHrs = new ArrayList<>();
		ArrayList<Double> ExaPlusModHrs = new ArrayList<>();
		ArrayList<Double> AtchOpHrs = new ArrayList<>();
		ArrayList<Double> RoadJobHrs= new ArrayList<>();

		int workingDataHourCount=0;
		//CR290.en
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;

		Properties prop = null;
		try {
			prop = CommonUtil.getDepEnvProperties();
		} catch (Exception e) {
			fLogger.fatal(txnKey + ":AMS:DAL:TAssetMonData-getTAssetMonData"
					+ "Error in intializing property File :" + e.getMessage());
		}

		try {

			String TAssetMonTable = prop
					.getProperty("default_TAssetMonData_Table");
			String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

			HashMap<String, Double> ERBHashMap = new HashMap<String, Double>();
			TreeMap<String, Double> PeriodHMRMap = new TreeMap<String, Double>();
			double CMH = 0.0;
			String lat = null,lng = null;
			int dataFlag = 0;

			if (Boolean.parseBoolean(PersistToInMemory)) {
				/* <!=========START persisting in In memory==============> */
				iLogger.info(txnKey + ":AMS:DAL:TAssetMonData-getTAssetMonData"
						+ "Persist to In Memory storage");
				/* <!=========END persisting in In memory==============> */
			} else {
				Calendar calendar = Calendar.getInstance();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String startDatetAssetMonTable = TAssetMonTable;
				String endDatetAssetMonTable = TAssetMonTable;
				String txnData = null;
				String txnTimeData = null;

				String strCurrDate = txnDate;

				Date date = dateFormat.parse(strCurrDate);
				calendar.setTime(date);
				// get previous day
				calendar.add(Calendar.DAY_OF_YEAR, -1);

				String prevDate = dateFormat.format(calendar.getTime());
				// data will be take from previous day 6:30 to today 6:20
				prevDate = prevDate + " 18:30:00";
				strCurrDate = strCurrDate + " 18:30:00";

				calendar.setTime(dateStr.parse(strCurrDate));
				Timestamp endtxnTimestamp = new Timestamp(
						calendar.getTimeInMillis());

				calendar.setTime(dateStr.parse(prevDate));
				Timestamp starttxnTimestamp = new Timestamp(
						calendar.getTimeInMillis());

				startDatetAssetMonTable = new DateUtil().getDynamicTable(
						txnKey, starttxnTimestamp);
				endDatetAssetMonTable = new DateUtil().getDynamicTable(txnKey,
						endtxnTimestamp);

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getDatalakeConnection_3309();				
				statement = prodConnection.createStatement();

				for(int k = 1;k<=7;k++){
					ERBHashMap.put("ERB"+k, 0.0);
				}
				ERBHashMap.put("IEPB", 0.0);
				ERBHashMap.put("MEPB", 0.0);
				ERBHashMap.put("HEPB", 0.0);
				ERBHashMap.put("LEPB", 0.0);
				ERBHashMap.put("Engine_Power_band_0", 0.0);
				ERBHashMap.put("EnginePowerband1", 0.0);
				ERBHashMap.put("EnginePowerband2", 0.0);
				ERBHashMap.put("EnginePowerband3", 0.0);

				//DF20191004: @Mamatha AEMP Download Issue :(Removed the check for MSG_ID when it is LOG= 1) 
				if (!startDatetAssetMonTable.equals(endDatetAssetMonTable)) {
					TAssetMonDataSelectQuery = "Select TxnData,Transaction_Timestamp from "
							+ startDatetAssetMonTable
							+ " where Segment_ID_TxnDate="
							+ seg_ID
							+ " and Serial_Number='"
							+ Serial_Number
							+ "' and Transaction_Timestamp >= '"
							+ starttxnTimestamp + "'" ;
					//"and (Message_ID -> '$.LOG'='1')";

					rs = statement.executeQuery(TAssetMonDataSelectQuery);
					while(rs.next()) {
						txnData = rs.getObject("TxnData").toString();
						txnTimeData = rs.getObject("Transaction_Timestamp").toString();
						txnDataMap = new Gson().fromJson(txnData,
								new TypeToken<HashMap<String, Object>>() {
						}.getType());

						if(txnDataMap.get("CMH") !=null)
							CMH = Double.parseDouble(txnDataMap.get("CMH"));



						if(txnDataMap.get("LAT") != null && txnDataMap.get("LONG")!=null){
							lat = txnDataMap.get("LAT");
							lng = txnDataMap.get("LONG");
						}

						if(txnDataMap.get("MSG_ID").equals("040")|| txnDataMap.get("MSG_ID").equals("004")){
							if(txnTimeData !=null){
								PeriodHMRMap.put(txnTimeData, CMH);
							}
						}
						//CR290.sn  Reusing same for - 062
						if(txnDataMap.get("MSG_ID").equals("050") || txnDataMap.get("MSG_ID").equals("062")){					

							if(idleHourMap == null || idleHourMap.isEmpty()){
								idleHourMap.put("HIGHIDLEHOUR", new ArrayList<Double>());
								idleHourMap.put("LOWIDLEHOUR", new ArrayList<Double>());
								idleHourMap.put("WORKINGHOUR",new ArrayList<Double>());
								idleHourMap.put("LoadJobHrs", new ArrayList<Double>());
								idleHourMap.put("ExaStdModHrs", new ArrayList<Double>());
								idleHourMap.put("ExaPlusModHrs", new ArrayList<Double>());
								idleHourMap.put("AtchOpHrs", new ArrayList<Double>());
								idleHourMap.put("RoadJobHrs",new ArrayList<Double>());
								idleHourMap.put("ExaEcoModHrs",new ArrayList<Double>());
							}
							else
								workingDataHourCount++;
							highIdleList.add(Double.parseDouble(txnDataMap.get("EVT_TSAHI1")));
							idleHourMap.put("HIGHIDLEHOUR",highIdleList);
							lowIdleList.add(Double.parseDouble(txnDataMap.get("EVT_TSALI1")));
							idleHourMap.put("LOWIDLEHOUR", lowIdleList);
							workingList.add(Double.parseDouble(txnDataMap.get("EVT_TSALJ1")));
							idleHourMap.put("WORKINGHOUR", workingList);
							LoadJobHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSALJ1")));
							idleHourMap.put("LoadJobHrs", LoadJobHrs);
							ExaStdModHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSIEISM1")));
							idleHourMap.put("ExaStdModHrs",ExaStdModHrs);
							ExaPlusModHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSIEIPM1")));
							idleHourMap.put("ExaPlusModHrs", ExaPlusModHrs);
							ExaEcoModHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSIEIEM1")));
							idleHourMap.put("ExaEcoModHrs", ExaEcoModHrs);
							AtchOpHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSAO2")));
							idleHourMap.put("AtchOpHrs", AtchOpHrs);
							RoadJobHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSIRJ1")));
							idleHourMap.put("RoadJobHrs", RoadJobHrs);
						}
						// Leela - 
						if(txnDataMap.get("MSG_ID").equals("066")) {
							
							ERBHashMap.put("Engine_Power_band_0", ERBHashMap.get("Engine_Power_band_0")+Double.parseDouble(txnDataMap.get("Engine_Power_band_0")));
							ERBHashMap.put("EnginePowerband1", ERBHashMap.get("EnginePowerband1")+Double.parseDouble(txnDataMap.get("EnginePowerband1")));
							ERBHashMap.put("EnginePowerband2", ERBHashMap.get("EnginePowerband2")+Double.parseDouble(txnDataMap.get("EnginePowerband2")));
							ERBHashMap.put("EnginePowerband3", ERBHashMap.get("EnginePowerband3")+Double.parseDouble(txnDataMap.get("EnginePowerband3")));
							
						}
						//CR290.en
						if(txnDataMap.get("MSG_ID").equals("020") && !(assetTypeName.equalsIgnoreCase("VM116"))){

							ERBHashMap.put("IEPB", ERBHashMap.get("IEPB")+Double.parseDouble(txnDataMap.get("IEPB")));
							System.out.println("IEPB :"+ERBHashMap.get("IEPB") +"txnDate :"+txnDate);
							ERBHashMap.put("MEPB", ERBHashMap.get("MEPB")+Double.parseDouble(txnDataMap.get("MEPB")));
							ERBHashMap.put("HEPB", ERBHashMap.get("HEPB")+Double.parseDouble(txnDataMap.get("HEPB")));
							ERBHashMap.put("LEPB", ERBHashMap.get("LEPB")+Double.parseDouble(txnDataMap.get("LEPB")));
						}
						if(txnDataMap.get("MSG_ID").equals("040") ){

						}
						else{
							for(int k = 1;k<=7;k++){
								if(txnDataMap.get("ERB"+k) != null)
									ERBHashMap.put("ERB"+k, ERBHashMap.get("ERB"+k)+Double.parseDouble(txnDataMap.get("ERB"+k)));
							}
						}
						dataFlag = 1;
					}

					//DF20191004: @Mamatha AEMP Download Issue :(Removed the check for MSG_ID when it is LOG= 1)
					TAssetMonDataSelectQuery = "Select TxnData,Transaction_Timestamp from "
							+ endDatetAssetMonTable
							+ " where Segment_ID_TxnDate="
							+ seg_ID
							+ " and Serial_Number='"
							+ Serial_Number
							+ "' and Transaction_Timestamp <= '"
							+ endtxnTimestamp + "'" ;
					//"and (Message_ID -> '$.LOG'='1')";

					rs = statement.executeQuery(TAssetMonDataSelectQuery);
					while(rs.next()) {
						txnData = rs.getObject("TxnData").toString();
						txnTimeData = rs.getObject("Transaction_Timestamp").toString();
						txnDataMap = new Gson().fromJson(txnData,
								new TypeToken<HashMap<String, Object>>() {
						}.getType());

						if(txnDataMap.get("CMH") !=null)
							CMH = Double.parseDouble(txnDataMap.get("CMH"));

						if(txnDataMap.get("LAT") != null && txnDataMap.get("LONG")!=null){
							lat = txnDataMap.get("LAT");
							lng = txnDataMap.get("LONG");
						}

						if(txnDataMap.get("MSG_ID").equals("040")|| txnDataMap.get("MSG_ID").equals("004")){
							if(txnTimeData !=null){
								PeriodHMRMap.put(txnTimeData, CMH);
							}
						}

						//CR290.sn  //Leela Reusing - 062
						if(txnDataMap.get("MSG_ID").equals("050") || txnDataMap.get("MSG_ID").equals("062") ){					

							if(idleHourMap == null || idleHourMap.isEmpty()){
								idleHourMap.put("HIGHIDLEHOUR", new ArrayList<Double>());
								idleHourMap.put("LOWIDLEHOUR", new ArrayList<Double>());
								idleHourMap.put("WORKINGHOUR",new ArrayList<Double>());
								idleHourMap.put("LoadJobHrs", new ArrayList<Double>());
								idleHourMap.put("ExaStdModHrs", new ArrayList<Double>());
								idleHourMap.put("ExaPlusModHrs", new ArrayList<Double>());
								idleHourMap.put("AtchOpHrs", new ArrayList<Double>());
								idleHourMap.put("RoadJobHrs",new ArrayList<Double>());
								idleHourMap.put("ExaEcoModHrs",new ArrayList<Double>());
							}
							else
								workingDataHourCount++;
							highIdleList.add(Double.parseDouble(txnDataMap.get("EVT_TSAHI1")));
							idleHourMap.put("HIGHIDLEHOUR",highIdleList);
							lowIdleList.add(Double.parseDouble(txnDataMap.get("EVT_TSALI1")));
							idleHourMap.put("LOWIDLEHOUR", lowIdleList);
							workingList.add(Double.parseDouble(txnDataMap.get("EVT_TSALJ1")));
							idleHourMap.put("WORKINGHOUR", workingList);
							LoadJobHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSALJ1")));
							idleHourMap.put("LoadJobHrs", LoadJobHrs);
							ExaStdModHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSIEISM1")));
							idleHourMap.put("ExaStdModHrs",ExaStdModHrs);
							ExaPlusModHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSIEIPM1")));
							idleHourMap.put("ExaPlusModHrs", ExaPlusModHrs);
							ExaEcoModHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSIEIEM1")));
							idleHourMap.put("ExaEcoModHrs", ExaEcoModHrs);
							AtchOpHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSAO2")));
							idleHourMap.put("AtchOpHrs", AtchOpHrs);
							RoadJobHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSIRJ1")));
							idleHourMap.put("RoadJobHrs", RoadJobHrs);
						}

						// Leela - 
						if(txnDataMap.get("MSG_ID").equals("066")) {
							
							ERBHashMap.put("Engine_Power_band_0", ERBHashMap.get("Engine_Power_band_0")+Double.parseDouble(txnDataMap.get("Engine_Power_band_0")));
							ERBHashMap.put("EnginePowerband1", ERBHashMap.get("EnginePowerband1")+Double.parseDouble(txnDataMap.get("EnginePowerband1")));
							ERBHashMap.put("EnginePowerband2", ERBHashMap.get("EnginePowerband2")+Double.parseDouble(txnDataMap.get("EnginePowerband2")));
							ERBHashMap.put("EnginePowerband3", ERBHashMap.get("EnginePowerband3")+Double.parseDouble(txnDataMap.get("EnginePowerband3")));
						
						}						
						//CR290.en
						if(txnDataMap.get("MSG_ID").equals("020") && !("VM116".equalsIgnoreCase(assetTypeName))){

							ERBHashMap.put("IEPB", ERBHashMap.get("IEPB")+Double.parseDouble(txnDataMap.get("IEPB")));
							System.out.println("IEPB :"+ERBHashMap.get("IEPB") +"txnDate :"+txnDate);
							ERBHashMap.put("MEPB", ERBHashMap.get("MEPB")+Double.parseDouble(txnDataMap.get("MEPB")));
							ERBHashMap.put("HEPB", ERBHashMap.get("HEPB")+Double.parseDouble(txnDataMap.get("HEPB")));
							ERBHashMap.put("LEPB", ERBHashMap.get("LEPB")+Double.parseDouble(txnDataMap.get("LEPB")));
						}
						else{
							for(int k = 1;k<=7;k++){
								if(txnDataMap.get("ERB"+k) != null)
									ERBHashMap.put("ERB"+k, ERBHashMap.get("ERB"+k)+Double.parseDouble(txnDataMap.get("ERB"+k)));
							}
						}
						dataFlag = 1;
					}

				} else {

					//DF20191004: @Mamatha AEMP Download Issue :(Removed the check for MSG_ID when it is LOG= 1)
					TAssetMonDataSelectQuery = "Select TxnData,Transaction_Timestamp from "
							+ startDatetAssetMonTable
							+ " where Segment_ID_TxnDate="
							+ seg_ID
							+ " and Serial_Number='"
							+ Serial_Number
							+ "' and Transaction_Timestamp between '"
							+ starttxnTimestamp
							+ "' and '"
							+ endtxnTimestamp + "'"; 
					//+" and (Message_ID -> '$.LOG'='1')";

					rs = statement.executeQuery(TAssetMonDataSelectQuery);
					
					
					while(rs.next()) {
						txnData = rs.getObject("TxnData").toString();
						txnTimeData = rs.getObject("Transaction_Timestamp").toString();
						txnDataMap = new Gson().fromJson(txnData,
								new TypeToken<HashMap<String, Object>>() {
						}.getType());

						if(txnDataMap.get("CMH") !=null)
							CMH = Double.parseDouble(txnDataMap.get("CMH"));

						if(txnDataMap.get("LAT") != null && txnDataMap.get("LONG")!=null){
							lat = txnDataMap.get("LAT");
							lng = txnDataMap.get("LONG");
						}
						if(txnDataMap.get("MSG_ID").equals("040")|| txnDataMap.get("MSG_ID").equals("004")){
							if(txnTimeData !=null){
								PeriodHMRMap.put(txnTimeData, CMH);
							}
						}
						//CR290.sn   //Leela Reusing same for - 062
						if(txnDataMap.get("MSG_ID").equals("050") || txnDataMap.get("MSG_ID").equals("062") ){					

							if(idleHourMap == null || idleHourMap.isEmpty()){
								idleHourMap.put("HIGHIDLEHOUR", new ArrayList<Double>());
								idleHourMap.put("LOWIDLEHOUR", new ArrayList<Double>());
								idleHourMap.put("WORKINGHOUR",new ArrayList<Double>());
								idleHourMap.put("LoadJobHrs", new ArrayList<Double>());
								idleHourMap.put("ExaStdModHrs", new ArrayList<Double>());
								idleHourMap.put("ExaPlusModHrs", new ArrayList<Double>());
								idleHourMap.put("AtchOpHrs", new ArrayList<Double>());
								idleHourMap.put("RoadJobHrs",new ArrayList<Double>());
								idleHourMap.put("ExaEcoModHrs",new ArrayList<Double>());
							}
							else
								workingDataHourCount++;
							highIdleList.add(Double.parseDouble(txnDataMap.get("EVT_TSAHI1"))); 
							idleHourMap.put("HIGHIDLEHOUR",highIdleList);
							lowIdleList.add(Double.parseDouble(txnDataMap.get("EVT_TSALI1")));
							idleHourMap.put("LOWIDLEHOUR", lowIdleList);
							workingList.add(Double.parseDouble(txnDataMap.get("EVT_TSALJ1")));
							idleHourMap.put("WORKINGHOUR", workingList);
							LoadJobHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSALJ1")));
							idleHourMap.put("LoadJobHrs", LoadJobHrs);
							ExaStdModHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSIEISM1")));
							idleHourMap.put("ExaStdModHrs",ExaStdModHrs);
							ExaPlusModHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSIEIPM1")));
							idleHourMap.put("ExaPlusModHrs", ExaPlusModHrs);
							ExaEcoModHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSIEIEM1")));
							idleHourMap.put("ExaEcoModHrs", ExaEcoModHrs);
							AtchOpHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSAO2")));
							idleHourMap.put("AtchOpHrs", AtchOpHrs);
							RoadJobHrs.add(Double.parseDouble(txnDataMap.get("EVT_TSIRJ1")));
							idleHourMap.put("RoadJobHrs", RoadJobHrs);
						}
						
						// Leela - 
						if(txnDataMap.get("MSG_ID").equals("066")) {
							
							ERBHashMap.put("Engine_Power_band_0", ERBHashMap.get("Engine_Power_band_0")+Double.parseDouble(txnDataMap.get("Engine_Power_band_0")));
							ERBHashMap.put("EnginePowerband1", ERBHashMap.get("EnginePowerband1")+Double.parseDouble(txnDataMap.get("EnginePowerband1")));
							ERBHashMap.put("EnginePowerband2", ERBHashMap.get("EnginePowerband2")+Double.parseDouble(txnDataMap.get("EnginePowerband2")));
							ERBHashMap.put("EnginePowerband3", ERBHashMap.get("EnginePowerband3")+Double.parseDouble(txnDataMap.get("EnginePowerband3")));

						}						
						//CR290.en
						if(txnDataMap.get("MSG_ID").equals("020") && !("VM116".equalsIgnoreCase(assetTypeName))){//assetTypeName

							ERBHashMap.put("IEPB", ERBHashMap.get("IEPB")+Double.parseDouble(txnDataMap.get("IEPB")));
							System.out.println("IEPB :"+ERBHashMap.get("IEPB") +"txnDate :"+txnDate);
							ERBHashMap.put("MEPB", ERBHashMap.get("MEPB")+Double.parseDouble(txnDataMap.get("MEPB")));
							ERBHashMap.put("HEPB", ERBHashMap.get("HEPB")+Double.parseDouble(txnDataMap.get("HEPB")));
							ERBHashMap.put("LEPB", ERBHashMap.get("LEPB")+Double.parseDouble(txnDataMap.get("LEPB")));
						}
						else{
							for(int k = 1;k<=7;k++){
								if(txnDataMap.get("ERB"+k) != null)
									ERBHashMap.put("ERB"+k, ERBHashMap.get("ERB"+k)+Double.parseDouble(txnDataMap.get("ERB"+k)));
							}
						}
						dataFlag = 1;
					}
				}

				//Converting ERB Data from minutes into Hours

				if(dataFlag == 1){
					if(PeriodHMRMap!=null && !PeriodHMRMap.isEmpty()){
						machineHoursDataMap.put("PeriodHMR", String.valueOf(PeriodHMRMap.get(PeriodHMRMap.lastKey())-PeriodHMRMap.get(PeriodHMRMap.firstKey())));
					}else{
						machineHoursDataMap.put("PeriodHMR","0.0");
					}
					
					//CR290.sn Reusing with MSG_ID check (if else case) - 062
						if(idleHourMap.size() > 0)
						{
							workingHour = (idleHourMap.get("WORKINGHOUR").get(workingDataHourCount)-idleHourMap.get("WORKINGHOUR").get(0))/3600;
							lowIdleHour = (idleHourMap.get("LOWIDLEHOUR").get(workingDataHourCount)-idleHourMap.get("LOWIDLEHOUR").get(0))/3600;
							highIdleHour = (idleHourMap.get("HIGHIDLEHOUR").get(workingDataHourCount)-idleHourMap.get("HIGHIDLEHOUR").get(0))/3600;
							loadJobHour=(idleHourMap.get("LoadJobHrs").get(workingDataHourCount)-idleHourMap.get("LoadJobHrs").get(0))/3600;
							exaStdModHour=(idleHourMap.get("ExaStdModHrs").get(workingDataHourCount)-idleHourMap.get("ExaStdModHrs").get(0))/3600;
							exaPlusModHour =(idleHourMap.get("ExaPlusModHrs").get(workingDataHourCount)-idleHourMap.get("ExaPlusModHrs").get(0))/3600;
							atchOpHour =(idleHourMap.get("AtchOpHrs").get(workingDataHourCount)-idleHourMap.get("AtchOpHrs").get(0))/3600;
							roadJobHour =(idleHourMap.get("RoadJobHrs").get(workingDataHourCount)-idleHourMap.get("RoadJobHrs").get(0))/3600;
							exaEcoModHour =(idleHourMap.get("ExaEcoModHrs").get(workingDataHourCount)-idleHourMap.get("ExaEcoModHrs").get(0))/3600;

						}
					//CR290.en

					for(int k = 1;k<=7;k++){
						if(ERBHashMap.get("ERB"+k) != null)
							machineHoursDataMap.put("ERB"+k, String.valueOf(ERBHashMap.get("ERB"+k)/60.0));
					}
					machineHoursDataMap.put("IEPB", String.valueOf(ERBHashMap.get("IEPB")/60.0));
					machineHoursDataMap.put("MEPB", String.valueOf(ERBHashMap.get("MEPB")/60.0));
					machineHoursDataMap.put("HEPB", String.valueOf(ERBHashMap.get("HEPB")/60.0));
					machineHoursDataMap.put("LEPB", String.valueOf(ERBHashMap.get("LEPB")/60.0));
					
					// Leela  MSG_ID = 066 - for EnginePowerband 
					machineHoursDataMap.put("EnginePowerbandHr0", String.valueOf(ERBHashMap.get("Engine_Power_band_0")));
					machineHoursDataMap.put("EnginePowerbandHr1", String.valueOf(ERBHashMap.get("EnginePowerband1")));
					machineHoursDataMap.put("EnginePowerbandHr2", String.valueOf(ERBHashMap.get("EnginePowerband2")));
					machineHoursDataMap.put("EnginePowerbandHr3", String.valueOf(ERBHashMap.get("EnginePowerband3")));
					
					//CR290.sn
					if(idleHourMap.size() > 0)
					{
						machineHoursDataMap.put("LOWIDLEHOUR", String.valueOf(lowIdleHour));
						machineHoursDataMap.put("HIGHIDLEHOUR", String.valueOf(highIdleHour));
						machineHoursDataMap.put("WORKINGHOUR", String.valueOf(workingHour));
						machineHoursDataMap.put("LoadJobHour",String.valueOf(loadJobHour));
						machineHoursDataMap.put("RoadJobHour", String.valueOf(roadJobHour));
						machineHoursDataMap.put("ExaStdModHour",String.valueOf(exaStdModHour));
						machineHoursDataMap.put("ExaPlusModHour",String.valueOf(exaPlusModHour));
						machineHoursDataMap.put("ExaEcoModHour",String.valueOf(exaEcoModHour));
						machineHoursDataMap.put("AtchOpHour",String.valueOf(atchOpHour));	
					}
					
					
					//CR290.en
					machineHoursDataMap.put("CMH", String.valueOf(CMH));
					machineHoursDataMap.put("LAT", lat);
					machineHoursDataMap.put("LONG", lng);
					machineHoursDataMap.put("MSG_ID", txnDataMap.get("MSG_ID"));					
				}
			}
		}

		catch (SQLException e) {

			e.printStackTrace();
			fLogger.fatal(txnKey
					+ ":AMS:DAL:TAssetMonData-getTAssetMonData"
					+ "SQL Exception in fetching data from mysql::"
					+ e.getMessage());
		}

		catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal(txnKey
					+ ":AMS:DAL:TAssetMonData-getTAssetMonData"
					+ "Exception in fetching data from mysql::"
					+ e.getMessage());
		}

		finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if (statement != null)
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

		return machineHoursDataMap;
	}

}
