package remote.wise.dal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.AmhDAO;
import remote.wise.pojo.AssetMonitoringParametersDAO;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;
import remote.wise.util.ListToStringConversion;

public class DynamicTAMD_DAL {

	public Map getAggregatedDataOnTxnDate(String Serial_Number,
			Timestamp TxnTS, int seg_ID,List aggregateParameters,Map common_parameters_map, List accumulatedParameters ){
		List<HashMap> list_of_Transactional_data = new LinkedList<HashMap>();
		
		Map accumulatedLogDataMap = null;
		Map aggregatedDataMap = null;
		Map commonAggregatedDataMap = null;
		Map aggregated_txn_data_map = new HashMap();
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	iLogger.info("DynamicTAMD_DAL getAggregatedDataOnTxnDate: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+TxnTS+" START");
    	
    	//String amhTable = null;
		long startTime = 0;
		long endTime =0;
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		DateFormat dateStr = null;
		Properties prop = null;
		String TxnData = null;
		int rowCount=0;
		//DateFormat dateStr = null;
		try{
			
			iLogger.info("AMH_DAL getEngineRunningBands AMH retrieval START");
			dateStr = new SimpleDateFormat("yyyy-MM-dd");
		/*	if(TxnTS!=null){
				//returns the HashMap with key as category name and value is corresponding transactional week table name
				// this one is mandatory since we maintaning the 8 weeks transactional table data for amh,amd,amde 
				//this method will give the tables to query for that week of transaction date.
				
				iLogger.info("");
				HashMap<String, String> dynamicTables = new DateUtil().getTxnCurrentWeekDifference(TxnTS);
				
					amhTable = dynamicTables.get("AMH");
					
					iLogger.info("AMH_DAL getEngineRunningBands AMH Table Name "+amhTable);
					
					
					//System.out.println("AMH_DAL getEngineRunningBands AMD Table Name "+amdETable);
			}*/
			
			connection = connFactory.getDatalakeConnection_3309();
			statement = connection.createStatement();
			String transaction_timestamp =dateStr.format(TxnTS);
			
			String endTS=transaction_timestamp+" 18:30:00";
			
			String startTAssetMonTable=null;
			String endTAssetMonTable=null;
			
			 String dynamicTable=new DateUtil().getDynamicTable("ETL", TxnTS);
			 
			 if(dynamicTable!=null){
				 endTAssetMonTable=dynamicTable;
				 
			 }
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(TxnTS);
			cal.add(Calendar.DAY_OF_YEAR, -1);
			
			String startTS = dateStr.format(cal.getTime());
			// data will be take from previous day 6:30 to today 6:30
			startTS = startTS + " 18:30:00";
			
			Timestamp starttxnTimestamp = new Timestamp(cal.getTimeInMillis());
			
			  String startDynamicTable=new DateUtil().getDynamicTable("ETL", starttxnTimestamp);
				 
				 if(startDynamicTable!=null){
					 startTAssetMonTable=startDynamicTable;
					 
				 }
				 //DF20170607 @Roopa GMT changes taking the transaction pkts from prev day 18:30 to curr transaction day 18:30
				 
				 if(startTAssetMonTable.equals(endTAssetMonTable)){
					 
					 rs = statement.executeQuery("SELECT TxnData FROM "+startTAssetMonTable+" where Segment_ID_TxnDate = "+seg_ID+
								" and Serial_Number = '"+Serial_Number+"' and Transaction_Timestamp between '"+startTS+"' and '"+endTS+"' and Message_ID -> '$.LOG'='1' order by Transaction_Timestamp");
						
						
						//DF20170517 @Roopa taking no of recors, bcoz for some parameters the logic will be sum of all log pkt data/no of log pkts
						if(rs != null){
							rs.last();
							 rowCount = rowCount+rs.getRow();
							rs.beforeFirst();
							}
						
						while(rs.next()){
							TxnData = rs.getObject("TxnData").toString();
							HashMap<String,String> TxnDataMap = new Gson().fromJson(TxnData, new TypeToken<HashMap<String, Object>>() {}.getType());
							list_of_Transactional_data.add(TxnDataMap);
							
						}
					 
				 }
				 else{
					 
					 //prev day of txn day table
					 
					 rs = statement.executeQuery("SELECT TxnData FROM "+startTAssetMonTable+" where Segment_ID_TxnDate = "+seg_ID+
								" and Serial_Number = '"+Serial_Number+"' and Transaction_Timestamp >= '"+startTS+"' and Message_ID -> '$.LOG'='1' order by Transaction_Timestamp");
						
						
						//DF20170517 @Roopa taking no of recors, bcoz for some parameters the logic will be sum of all log pkt data/no of log pkts
						if(rs != null){
							rs.last();
							 rowCount = rowCount+rs.getRow();
							rs.beforeFirst();
							}
						
						while(rs.next()){
							TxnData = rs.getObject("TxnData").toString();
							HashMap<String,String> TxnDataMap = new Gson().fromJson(TxnData, new TypeToken<HashMap<String, Object>>() {}.getType());
							list_of_Transactional_data.add(TxnDataMap);
							
						}
						
						
						//curr txn day table
						
						 rs = statement.executeQuery("SELECT TxnData FROM "+endTAssetMonTable+" where Segment_ID_TxnDate = "+seg_ID+
									" and Serial_Number = '"+Serial_Number+"' and Transaction_Timestamp <= '"+endTS+"' and Message_ID -> '$.LOG'='1' order by Transaction_Timestamp");
							
							
							//DF20170517 @Roopa taking no of recors, bcoz for some parameters the logic will be sum of all log pkt data/no of log pkts
							if(rs != null){
								rs.last();
								 rowCount = rowCount+rs.getRow();
								rs.beforeFirst();
								}
							
							while(rs.next()){
								TxnData = rs.getObject("TxnData").toString();
								HashMap<String,String> TxnDataMap = new Gson().fromJson(TxnData, new TypeToken<HashMap<String, Object>>() {}.getType());
								list_of_Transactional_data.add(TxnDataMap);
								
							}
					 
				 }
			
			
			
		/*	rs = statement.executeQuery("SELECT TxnData FROM "+amhTable+" where Segment_ID_TxnDate = "+seg_ID+
					" and Serial_Number = '"+Serial_Number+"' and Date(Transaction_Timestamp) = '"+transaction_timestamp+"' and Message_ID -> '$.LOG'='1' order by Transaction_Timestamp");
			
			
			//DF20170517 @Roopa taking no of recors, bcoz for some parameters the logic will be sum of all log pkt data/no of log pkts
			if(rs != null){
				rs.last();
				 rowCount = rs.getRow();
				rs.beforeFirst();
				}
			
			while(rs.next()){
				TxnData = rs.getObject("TxnData").toString();
				HashMap<String,String> TxnDataMap = new Gson().fromJson(TxnData, new TypeToken<HashMap<String, Object>>() {}.getType());
				list_of_Transactional_data.add(TxnDataMap);
				
			}*/
		}catch(SQLException se){
			se.printStackTrace();
		}
		finally{
			
			try {
				if(rs != null && !rs.isClosed()){
					rs.close();
					
				}
				
				if(statement != null && !statement.isClosed()){
					statement.close();
					
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				//fLogger.fatal("getAMhDataOnCreatedTS exception "+e.getMessage());
				e.printStackTrace();
			}
			try {
				if(connection!= null && !connection.isClosed()){
					connection.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				//fLogger.fatal("getAMhDataOnCreatedTS exception "+e.getMessage());
				e.printStackTrace();
			}
		}
		if(list_of_Transactional_data!=null){
			commonAggregatedDataMap = new CommonUtil().getAggregatedTxnData(list_of_Transactional_data, (List)common_parameters_map.get("Common_Aggregated_Parameters"),rowCount);
			if(commonAggregatedDataMap !=null && !commonAggregatedDataMap.isEmpty()){
				aggregated_txn_data_map.put("Common", commonAggregatedDataMap);
			}
			aggregatedDataMap = new CommonUtil().getAggregatedTxnData(list_of_Transactional_data, aggregateParameters,rowCount);
			if(aggregatedDataMap !=null && !aggregatedDataMap.isEmpty()){
				aggregated_txn_data_map.put("New_Aggregated", aggregatedDataMap);
			}
			
			
//Df20170522 @Roopa below logic because the accumulated parameters comes only in last log pkt for BHL and CAN parameters
			
			int length=list_of_Transactional_data.size();
			
			if(length!=0)
			accumulatedLogDataMap = new CommonUtil().getAccumulatedTxnData(list_of_Transactional_data.get(length-1), accumulatedParameters);
			if(accumulatedLogDataMap !=null && !accumulatedLogDataMap.isEmpty()){
				aggregated_txn_data_map.put("New_Accumulated", accumulatedLogDataMap);
			}
		}
		
		
		return aggregated_txn_data_map;
	}
	
	
	public Map getAccumulatedDataOnTxnDate(String Serial_Number,
			Timestamp TxnTS, int seg_ID,Map common_parameters_map ){
		//List<HashMap> list_of_Transactional_data = new LinkedList<HashMap>();
		Map accumulatedDataMap = null;
		//Map aggregatedDataMap = null;
		Map commonAggregatedDataMap = null;
		Map aggregated_txn_data_map = new HashMap();;
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	iLogger.info("DynamicTAMD_DAL getAggregatedDataOnTxnDate: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+TxnTS+" START");
    	
    	String amhSelectQuery = "";
		String amhFromQuery = "";
		String amhWhereQuery = "";
		String amhGroupNyQuery = "";
		String amhOrderByQuery = "";
		long startTime = 0;
		long endTime =0;
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		DateFormat dateStr = null;
		Properties prop = null;
		String TxnData = null;
		HashMap<String,String> TxnDataMap = null;
		String amhTable = null;
		try{
			
			iLogger.info("AMH_DAL getEngineRunningBands AMH retrieval START");
			dateStr = new SimpleDateFormat("yyyy-MM-dd");
			if(TxnTS!=null){
				//returns the HashMap with key as category name and value is corresponding transactional week table name
				// this one is mandatory since we maintaning the 8 weeks transactional table data for amh,amd,amde 
				//this method will give the tables to query for that week of transaction date.
				
				iLogger.info("");
				amhTable = new DateUtil().getDynamicTable("ETL",TxnTS);
				
					
					
					iLogger.info("AMH_DAL getEngineRunningBands AMH Table Name "+amhTable);
					
					
					//System.out.println("AMH_DAL getEngineRunningBands AMD Table Name "+amdETable);
			}
			connection = connFactory.getDatalakeConnection_3309();
			statement = connection.createStatement();
			rs = statement.executeQuery("SELECT TxnData FROM "+amhTable+" where Segment_ID_TxnDate = "+seg_ID+
					" and Serial_Number = '"+Serial_Number+"' and Transaction_Timestamp = '"+TxnTS+"'");
			while(rs.next()){
				TxnData = rs.getObject("TxnData").toString();
				TxnDataMap = new Gson().fromJson(TxnData, new TypeToken<HashMap<String, Object>>() {}.getType());
				//list_of_Transactional_data.add(TxnDataMap);
				
			}
		}catch(SQLException se){
			se.printStackTrace();
		}
		finally{
			try {
				if(rs != null && !rs.isClosed()){
					rs.close();
					
				}
				if(statement != null && !statement.isClosed()){
					statement.close();
					
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				//fLogger.fatal("getAMhDataOnCreatedTS exception "+e.getMessage());
				e.printStackTrace();
			}
			try {
				if(connection!= null && !connection.isClosed()){
					connection.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				//fLogger.fatal("getAMhDataOnCreatedTS exception "+e.getMessage());
				e.printStackTrace();
			}
		}
		if(TxnDataMap!=null){
			commonAggregatedDataMap = new CommonUtil().getAccumulatedTxnData(TxnDataMap, (List)common_parameters_map.get("Common_Accumulated_Parameters"));
			if(commonAggregatedDataMap !=null && !commonAggregatedDataMap.isEmpty()){
				aggregated_txn_data_map.put("Common", commonAggregatedDataMap);
			}
			//Df20170522 @Roopa commenting the below logic because the accululated parameters comes only in last log pkt for BHL and CAN parameters
			
			/*accumulatedDataMap = new CommonUtil().getAccumulatedTxnData(TxnDataMap, accumulatedParameters);
			if(accumulatedDataMap !=null && !accumulatedDataMap.isEmpty()){
				aggregated_txn_data_map.put("New_Accumulated", accumulatedDataMap);
			}*/
		}
			
		
		
		
		return aggregated_txn_data_map;
	}
	
	
	public Timestamp getLatestEngineONTxn(String Serial_Number,Timestamp txnTimestamp, int seg_ID){
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<AssetMonitoringParametersDAO> AMPList = new LinkedList<AssetMonitoringParametersDAO>();
		
		iLogger.info("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" START");
		
		String amhTable = null;
		
		String PersistTo_InMemory = null;
		StringBuilder parameterListString = null;
		
		long startTime = 0;
		long endTime = 0;
		
		DateFormat dateStr = null;
		Properties prop = null;
		
		
		String ampSelectQuery = "";
		String ampFromQuery = "";
		String ampWhereQuery = "";
		String ampGroupNyQuery = "";
		String ampOrderByQuery = "";
		//StringBuilder parameterListString = null;
		//Session session = HibernateUtil.getSessionFactory().openSession();
		
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		try{
			
		
		 prop = CommonUtil.getDepEnvProperties();
		 amhTable = prop.getProperty("default_AMH_Table");
		 
		
		 PersistTo_InMemory = prop.getProperty("PersistTo_InMemory");
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			fLogger.fatal("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" exception while retrieving properties file");
		}
		
		Timestamp TxnTime = null;
		
		if(Boolean.parseBoolean(PersistTo_InMemory)){
			//System.out.println("Persist to In Memory storage");
		}
		else{
			
			//use Query Param to determine which query to execute   
			
			try{
			if(txnTimestamp!=null){
				amhTable = new DateUtil().getDynamicTable("ETL",txnTimestamp);
				
					
					
			}
			//parameterListString = new ListToStringConversion().getIntegerListString(parametreIDList);
			
			startTime = System.currentTimeMillis();
			iLogger.info("AMH_DAL getLatestEngineONTxn Query starts ");
			
			 //DF20170607 @Roopa GMT changes taking the latest engine on transaction >=18:30 for the fiven transaction day
			
			 dateStr = new SimpleDateFormat("yyyy-MM-dd");
			String transaction_timestamp =dateStr.format(txnTimestamp);
						
						String endTS=transaction_timestamp+" 18:30:00";
			
			
			String mainQuery ="SELECT max(transaction_timestamp) as Transaction_Timestamp FROM " +
					amhTable + 
					" where " +
					"Segment_ID_TxnDate ="+seg_ID+" and Serial_Number = '" + Serial_Number + "' and Transaction_Timestamp <= '" + endTS +"'" +
							" and (TxnData -> '$.ENG_STATUS' = '1' OR  TxnData -> '$.EVT_ENG'='1') ;";
			
			iLogger.info("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" main Query: "+mainQuery);
			
			//System.out.println("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" main Query: "+mainQuery);
			
			connection = connFactory.getDatalakeConnection_3309();
			statement = connection.createStatement();
			rs = statement.executeQuery(mainQuery);
			
			//Iterator itr = ampQuery.list().iterator();
			
		//	Object[] result = null;
			
			while(rs.next()){
			//	result = (Object[])itr.next();
				TxnTime = rs.getTimestamp("Transaction_Timestamp");
			
			}
			endTime = System.currentTimeMillis();
			iLogger.info("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" Query ends in "+(endTime-startTime));
			}catch(Exception ex){
				ex.printStackTrace();
				fLogger.fatal("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" exception "+ex.getMessage());
			}
			finally{
				try {
					if(rs != null && !rs.isClosed()){
						rs.close();
						
					}
					if(statement != null && !statement.isClosed()){
						statement.close();
						
					}
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getLatestEngineONTxn exception "+e.getMessage());
					e.printStackTrace();
				}
				try {
					if(connection!= null && !connection.isClosed()){
						connection.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getLatestEngineONTxn exception "+e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
		iLogger.info("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" END");
		
		return TxnTime;
		
	}
	
	public List<String> getAMHTablesPerVIN_N_OLAPDate(String serial_Number,Timestamp maxOlapDt,int segID, Timestamp nextday){
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		List<String> dynamicAMHTables = new LinkedList<String>();
		Calendar cal = Calendar.getInstance();
		int weekNo = cal.get(Calendar.WEEK_OF_YEAR);
		
		int currweekNo = cal.get(Calendar.WEEK_OF_YEAR);
		
		int currYear=cal.get(Calendar.YEAR);
		
		cal.setTime(maxOlapDt);
		
		int lastolapdateweek=cal.get(Calendar.WEEK_OF_YEAR);;
		int transactionYear = cal.get(Calendar.YEAR);
		int noofweeksintransactionYear=cal.getActualMaximum(Calendar.WEEK_OF_YEAR);
		
		//DF20170522 @Roopa handling year change scenario where txndate is diff year and curr year is different
		
		if(currYear!=transactionYear){

			weekNo =noofweeksintransactionYear+weekNo;
			
			iLogger.info("ETLfactDataJsonBO: SegmentID "+segID+" VIN "+serial_Number+":maxOlapDt:"+maxOlapDt+":year Change:: incrementing the weekNum to match loop"+weekNo);

		}
		
		Calendar futurecal = Calendar.getInstance();
		
		futurecal.add(Calendar.DAY_OF_YEAR, 6);
		int futureYear=futurecal.get(Calendar.YEAR);
		
		
		
		if(lastolapdateweek==1 && futureYear==transactionYear+1){
			lastolapdateweek=noofweeksintransactionYear;
		}
		
		String amhTable = null;
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null; 
		
		
		Timestamp ts = new Timestamp(cal.getTimeInMillis());

		Properties prop = CommonUtil.getDepEnvProperties();
		String JCB_AMH_TableKey = prop.getProperty("JCB_TAMD_TableKey");
		
		//DF20170512 @Roopa Taking maxolapdate for the given VIn and iterating from that maxolapdate table onwards to the current week table.
		
		//amhTable = JCB_AMH_TableKey.replace("week", weekNo+"");
		
		
		amhTable = JCB_AMH_TableKey.replace("week", lastolapdateweek+"");
		
		
        amhTable =amhTable.replace("year", transactionYear+"");
        
        boolean recordNotFound = true;
        
   	connection = connFactory.getDatalakeConnection_3309();
		try {
			statement = connection.createStatement();
			
			 for(int i=lastolapdateweek;i<=weekNo;i++){
		        	try{
		        	
		        	//String query = "select * from "+amhTable +" where Segment_ID_TxnDate = "+segID+" and Serial_Number = '"+serial_Number+"' and Last_Updated_Timestamp >= '"+maxOlapDt+"' and Last_Updated_Timestamp < Date('"+nextday+"')";
		        	
		        		
		        		//Df20170601 @Roopa ETL is scheduled after 12 so changing the query to not take the update records for maxolapdate condition
		        		String query = "select * from "+amhTable +" where Segment_ID_TxnDate = "+segID+" and Serial_Number = '"+serial_Number+"' and Date(Last_Updated_Timestamp) > Date('"+maxOlapDt+"') and Date(Last_Updated_Timestamp) < Date('"+nextday+"')";
		        		
		        		iLogger.info("ETLfactDataJsonBO: SegmentID "+segID+" VIN "+serial_Number+":maxOlapDt:"+maxOlapDt+":query::"+query);
		        		try{
		        		rs = statement.executeQuery(query);
		        		
		        		if(rs.next()){
			        		recordNotFound = false;
			        		dynamicAMHTables.add(amhTable);
			        		
			        		 iLogger.info("ETLfactDataJsonBO: SegmentID "+segID+" VIN "+serial_Number+":maxOlapDt:"+maxOlapDt+":amhTable"+amhTable);
			        	}
		        		}
		        		catch(SQLException se){
		        			dynamicAMHTables.remove(amhTable);
		        			
		        			//fLogger.fatal("getAMHTablesPerVIN_N_OLAPDate exception "+se.getMessage());
		        		}
		        	
		        	//amhTable = JCB_AMH_TableKey.replace("week", (weekNo-i)+"");
		        	
		        	if((currYear==transactionYear) || (i<weekNo && i!=noofweeksintransactionYear)){
		        	
		        	amhTable = JCB_AMH_TableKey.replace("week", (i+1)+"");
		        	
		        	amhTable =amhTable.replace("year", transactionYear+"");
		        	}
		        	else{
		        		amhTable = JCB_AMH_TableKey.replace("week", currweekNo+"");
			        	
			        	amhTable =amhTable.replace("year", currYear+"");
			        	
			        	//DF20180205 Expected fix for to iterate test it
			        	
                        /*amhTable = JCB_AMH_TableKey.replace("week", 1+"");
			        	
			        	amhTable =amhTable.replace("year", currYear+"");
			        	
			        	i=1;
			        	weekNo=currweekNo;*/
		        		
		        	}
		        	
		        	}catch(Exception se){
		        		
		        		fLogger.fatal("getAMHTablesPerVIN_N_OLAPDate exception "+se.getMessage());
		        
		        		/*if(se instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException)
		        		{
		        			dynamicAMHTables.remove(amhTable);
		        			
		        			return dynamicAMHTables;
		        		}
		        		else
		        			return null;*/
		        	}
		        }
	
		} catch (SQLException e) {
			fLogger.fatal("getAMHTablesPerVIN_N_OLAPDate exception "+e.getMessage());
			e.printStackTrace();
		}
		finally{
			try {
				
				if(rs != null && !rs.isClosed()){
					rs.close();
					
				}
				
				if(statement != null && !statement.isClosed()){
					statement.close();
					
				}
				if(connection!= null && !connection.isClosed()){
					connection.close();
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				fLogger.fatal("getAMHTablesPerVIN_N_OLAPDate exception "+e.getMessage());
				e.printStackTrace();
			}
			
		}
		
		return dynamicAMHTables;
	}
	
	public List<AmhDAO> getAMhDataOnCreatedTS(String Serial_Number,
			Timestamp createdTS, int seg_ID,String amhTable, Timestamp nextday) {
		// TODO Auto-generated method stub
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	iLogger.info("AMH_DAL getAMhDataOnCreatedTS: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+createdTS+" START");
    	
    	String amhSelectQuery = "";
		String amhFromQuery = "";
		String amhWhereQuery = "";
		String amhGroupNyQuery = "";
		String amhOrderByQuery = "";
		long startTime = 0;
		long endTime =0;
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		DateFormat dateStr = null;
		Properties prop = null;
		List<AmhDAO> amhDaoList = new LinkedList<AmhDAO>();
		try{
			dateStr = new SimpleDateFormat("yyyy-MM-dd");
			prop = CommonUtil.getDepEnvProperties();
		}catch(IllegalArgumentException iag){
				//System.out.println("AMH_DAL Exception in formatting the Date "+iag.getMessage());
				fLogger.fatal("AMH_DAL getAMhDataOnCreatedTS Exception in formatting the Date "+iag.getMessage());
		}catch(Exception e){
			e.printStackTrace();
			fLogger.fatal("AMH_DAL getAMhDataOnCreatedTS generic Exceptio:"+e.getMessage());
		}
		
String PersistTo_InMemory = prop.getProperty("PersistTo_InMemory");
		
		iLogger.info("AMH_DAL getAMhDataOnCreatedTS PersistTo_InMemory"+PersistTo_InMemory);
		
		if(Boolean.parseBoolean(PersistTo_InMemory)){
			//System.out.println("Persist to In Memory storage");
		}
		else{
			startTime = System.currentTimeMillis();
			iLogger.info("AMH_DAL getAMhDataOnCreatedTS Query starts ");
		
			amhSelectQuery = "select a.Serial_Number,max(a.Transaction_Timestamp) transactionTimestamp" ;
			amhFromQuery = " from "+amhTable+" a ";
			/*amhWhereQuery = " where a.Segment_ID_TxnDate = "+seg_ID +" and a.Serial_Number = '"+Serial_Number+"' and " +
					" a.Last_Updated_Timestamp >= '"+createdTS+"' and Last_Updated_Timestamp < Date('"+nextday+"')";*/
			
			//Df20170601 @Roopa ETL is scheduled after 12 so changing the query to not take the update records for maxolapdate condition
			//takinh the max TS upto 18:30 only..bcoz TS is in GMT format
			
			amhWhereQuery = " where a.Segment_ID_TxnDate = "+seg_ID +" and a.Serial_Number = '"+Serial_Number+"' and " +
					" Date(a.Last_Updated_Timestamp) > Date('"+createdTS+"') and Date(a.Last_Updated_Timestamp) < Date('"+nextday+"') and Time(a.Transaction_Timestamp) <= '18:30:00' ";
			
			amhGroupNyQuery = "group by a.Serial_Number ,Date(a.Transaction_Timestamp) ";
			amhOrderByQuery = " order by a.Serial_Number,transactionTimestamp";
		
			String mainQuery = amhSelectQuery+amhFromQuery+amhWhereQuery+amhGroupNyQuery+amhOrderByQuery;
			iLogger.info("AMH_DAL getAMhDataOnCreatedTS mainQuery "+mainQuery);
			//System.out.println("AMH_DAL getAMhDataOnCreatedTS mainQuery "+mainQuery);
			
			try {
				connection = connFactory.getDatalakeConnection_3309();
				statement = connection.createStatement();
				rs = statement.executeQuery(mainQuery);
				while(rs.next()){
					AmhDAO daoObject = new AmhDAO();
					if(rs.getString("Serial_Number")==null){
						continue;
					}
					daoObject.setSerial_Number(rs.getString("Serial_Number"));
					daoObject.setTransaction_Timestamp(rs.getTimestamp("transactionTimestamp"));
					amhDaoList.add(daoObject);
					
				}
				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				try {
					
					if(rs != null && !rs.isClosed()){
						rs.close();
						
					}
					
					if(statement != null && !statement.isClosed()){
						statement.close();
						
					}
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getAMhDataOnCreatedTS exception "+e.getMessage());
					e.printStackTrace();
				}
				try {
					if(connection!= null && !connection.isClosed()){
						connection.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getAMhDataOnCreatedTS exception "+e.getMessage());
					e.printStackTrace();
				}
			}
			
			
		}
		
		return amhDaoList;
	}
	
	public String getEngineONAndOFFCount(String Serial_Number, Timestamp txnTimestamp, int seg_ID){

		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
	
		iLogger.info("AMH_DAL getLatestEngineONCount and off count: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" START");
		
		//String amhTable = null;
		
		String PersistTo_InMemory = null;
		
		long startTime = 0;
		long endTime = 0;
		
		DateFormat dateStr =  new SimpleDateFormat("yyyy-MM-dd");
		Properties prop = null;
		
	
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		try{
			
		
		 prop = CommonUtil.getDepEnvProperties();
		// amhTable = prop.getProperty("default_AMH_Table");
		 
		
		 PersistTo_InMemory = prop.getProperty("PersistTo_InMemory");
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			fLogger.fatal("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" exception while retrieving properties file");
		}
	int eng_on_count=0;
	int eng_off_count=0;
	String enf_onoff_count=null;
		if(Boolean.parseBoolean(PersistTo_InMemory)){
			//System.out.println("Persist to In Memory storage");
		}
		else{
			
			//use Query Param to determine which query to execute   
			
			try{
			/*if(txnTimestamp!=null){
				HashMap<String, String> dynamicTables = new DateUtil().getTxnCurrentWeekDifference(txnTimestamp);
				
					amhTable = dynamicTables.get("AMH");
					
			}*/
				
				
			String transaction_timestamp =dateStr.format(txnTimestamp);
			
				String endTS=transaction_timestamp+" 18:30:00";
				
				String startTAssetMonTable=null;
				String endTAssetMonTable=null;
				
				 String dynamicTable=new DateUtil().getDynamicTable("ETL", txnTimestamp);
				 
				 if(dynamicTable!=null){
					 endTAssetMonTable=dynamicTable;
					 
				 }
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(txnTimestamp);
				cal.add(Calendar.DAY_OF_YEAR, -1);
				
				String startTS = dateStr.format(cal.getTime());
				// data will be take from previous day 6:30 to today 6:30
				startTS = startTS + " 18:30:00";
				
				Timestamp starttxnTimestamp = new Timestamp(cal.getTimeInMillis());
				
				  String startDynamicTable=new DateUtil().getDynamicTable("ETL", starttxnTimestamp);
					 
					 if(startDynamicTable!=null){
						 startTAssetMonTable=startDynamicTable;
						 
					 }
					 //DF20170607 @Roopa GMT changes taking the transaction pkts from prev day 18:30 to curr transaction day 18:30
					 
					 startTime = System.currentTimeMillis();
					 
					 if(startTAssetMonTable.equals(endTAssetMonTable)){
						 
						 iLogger.info("AMH_DAL getLatestEngineONCount and off count Query starts ");
							
							String onQuery ="SELECT count(*) as engineon_count FROM " +
									startDynamicTable + 
									" where " +
									"Segment_ID_TxnDate ="+seg_ID+" and Serial_Number = '" + Serial_Number + "' and Transaction_Timestamp between '"+startTS+"' and '"+endTS+"' " +
											" and  TxnData -> '$.EVT_ENG'='1'";
							
							String offQuery ="SELECT count(*) as engineoff_count FROM " +
									startDynamicTable + 
									" where " +
									"Segment_ID_TxnDate ="+seg_ID+" and Serial_Number = '" + Serial_Number + "' and Transaction_Timestamp between '"+startTS+"' and '"+endTS+"' " +
											" and  TxnData -> '$.EVT_ENG'='0'";
							
							
							
							//iLogger.info("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" main Query: "+onQuery);
							
						
							connection = connFactory.getDatalakeConnection_3309();
							statement = connection.createStatement();
							rs = statement.executeQuery(onQuery);
							
							
							while(rs.next()){
								eng_on_count = rs.getInt("engineon_count");
							
							}
							
							
							rs = statement.executeQuery(offQuery);
							
							
							while(rs.next()){
								eng_off_count = rs.getInt("engineoff_count");
							
							}
							
							enf_onoff_count=eng_on_count+","+eng_off_count;
						 
					 }
					 else{
						 
						 iLogger.info("AMH_DAL getLatestEngineONCount and off count Query starts ");
							
							String onQuery ="SELECT count(*) as engineon_count FROM " +
									startDynamicTable + 
									" where " +
									"Segment_ID_TxnDate ="+seg_ID+" and Serial_Number = '" + Serial_Number + "' and Transaction_Timestamp >= '"+startTS+"' " +
											" and  TxnData -> '$.EVT_ENG'='1'";
							
							connection = connFactory.getDatalakeConnection_3309();
							statement = connection.createStatement();
							rs = statement.executeQuery(onQuery);
							
							
							while(rs.next()){
								eng_on_count =eng_on_count+ rs.getInt("engineon_count");
							
							}
							
							String onQuery1 ="SELECT count(*) as engineon_count FROM " +
									endTAssetMonTable + 
									" where " +
									"Segment_ID_TxnDate ="+seg_ID+" and Serial_Number = '" + Serial_Number + "' and Transaction_Timestamp <= '"+endTS+"' " +
											" and  TxnData -> '$.EVT_ENG'='1'";
							
                           rs = statement.executeQuery(onQuery1);
							
							
							while(rs.next()){
								eng_on_count =eng_on_count+ rs.getInt("engineon_count");
							
							}
							
							
							String offQuery ="SELECT count(*) as engineoff_count FROM " +
									startDynamicTable + 
									" where " +
									"Segment_ID_TxnDate ="+seg_ID+" and Serial_Number = '" + Serial_Number + "' and Transaction_Timestamp >= '"+startTS+"' " +
											" and  TxnData -> '$.EVT_ENG'='0'";
							
	                      rs = statement.executeQuery(offQuery);
							
							
							while(rs.next()){
								eng_off_count = eng_off_count+rs.getInt("engineoff_count");
							
							}
							
							
							String offQuery1 ="SELECT count(*) as engineoff_count FROM " +
									endTAssetMonTable + 
									" where " +
									"Segment_ID_TxnDate ="+seg_ID+" and Serial_Number = '" + Serial_Number + "' and Transaction_Timestamp <= '"+endTS+"' " +
											" and  TxnData -> '$.EVT_ENG'='0'";
						 
							 rs = statement.executeQuery(offQuery1);
								
								
								while(rs.next()){
									eng_off_count = eng_off_count+rs.getInt("engineoff_count");
								
								}
					 
								enf_onoff_count=eng_on_count+","+eng_off_count;
					 }
			
			
			/*iLogger.info("AMH_DAL getLatestEngineONCount and off count Query starts ");
			
			String onQuery ="SELECT count(*) as engineon_count FROM " +
					amhTable + 
					" where " +
					"Segment_ID_TxnDate ="+seg_ID+" and Serial_Number = '" + Serial_Number + "' and Date(Transaction_Timestamp)='" + transaction_timestamp +"'" +
							" and  TxnData -> '$.EVT_ENG'='1'";
			
			String offQuery ="SELECT count(*) as engineoff_count FROM " +
					amhTable + 
					" where " +
					"Segment_ID_TxnDate ="+seg_ID+" and Serial_Number = '" + Serial_Number + "' and Date(Transaction_Timestamp)='" + transaction_timestamp +"'" +
							" and  TxnData -> '$.EVT_ENG'='0'";
			
			
			
			//iLogger.info("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" main Query: "+onQuery);
			
		
			connection = connFactory.getDatalakeConnection_3309();
			statement = connection.createStatement();
			rs = statement.executeQuery(onQuery);
			
			
			while(rs.next()){
				eng_on_count = rs.getInt("engineon_count");
			
			}
			
			
			rs = statement.executeQuery(offQuery);
			
			
			while(rs.next()){
				eng_off_count = rs.getInt("engineoff_count");
			
			}
			
			enf_onoff_count=eng_on_count+","+eng_off_count;*/
			
			endTime = System.currentTimeMillis();
			iLogger.info("AMH_DAL getLatestEngineONCount and off count: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" Query ends in "+(endTime-startTime));
			}catch(Exception ex){
				ex.printStackTrace();
				fLogger.fatal("AMH_DAL getLatestEngineONCount and off count: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" exception "+ex.getMessage());
			}
			finally{
				try {
					if(rs != null && !rs.isClosed()){
						rs.close();
						
					}
					if(statement != null && !statement.isClosed()){
						statement.close();
						
					}
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getLatestEngineONCount and off count exception "+e.getMessage());
					e.printStackTrace();
				}
				try {
					if(connection!= null && !connection.isClosed()){
						connection.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getLatestEngineONCount and off count exception "+e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
		iLogger.info("AMH_DAL getLatestEngineONCount and off count: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" END");
		
		return enf_onoff_count;
		
	
	}
	
}
