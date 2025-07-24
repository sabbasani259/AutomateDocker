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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.interfaces.AMH;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.AMSDoc_DAO;
import remote.wise.pojo.AmhDAO;
import remote.wise.pojo.AssetMonitoringParametersDAO;
import remote.wise.pojo.TAssetMonDataDAO;
import remote.wise.service.implementation.AssetEventLogImpl;
import remote.wise.service.implementation.DownloadAempImpl;
import remote.wise.service.implementation.MapImpl;
import remote.wise.service.implementation.PricolTransactionDetailImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;
import remote.wise.util.GmtLtTimeConversion;
import remote.wise.util.ListToStringConversion;
import remote.wise.util.StaticProperties;


/*
 * author S Suresh 
 * DT 20160616 
 * Introducing new layer DAL in between BO and Data Storage 
 * by writing core business logic in BO and data access logic in DAL    
 */
public class DynamicAMH_DAL implements AMH{



	@Override
	public String setAMHData(String txnKey, HashMap<String, String> payloadMap,
			Timestamp txnTimestamp, int prevPktTxnNumber, int seg_ID,
			int record_type) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<AmhDAO> getAMhData(String Serial_Number,
			Timestamp txnTimestamp, int seg_ID) {
		// TODO Auto-generated method stub

		return null;
	}



	public List<AmhDAO> getAMhDataOnCreatedTS(String Serial_Number,
			Timestamp createdTS, int seg_ID,String amhTable) {
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
			amhWhereQuery = " where a.Segment_ID_TxnDate = "+seg_ID +" and a.Serial_Number = '"+Serial_Number+"' and " +
					" a.Last_Updated_Timestamp >= '"+createdTS+"' ";
			amhGroupNyQuery = "group by a.Serial_Number ,Date(a.Transaction_Timestamp) ";
			amhOrderByQuery = " order by a.Serial_Number,a.Transaction_Timestamp";

			String mainQuery = amhSelectQuery+amhFromQuery+amhWhereQuery+amhGroupNyQuery+amhOrderByQuery;
			iLogger.info("AMH_DAL getAMhDataOnCreatedTS mainQuery "+mainQuery);
			//System.out.println("AMH_DAL getAMhDataOnCreatedTS mainQuery "+mainQuery);

			try {
				connection = connFactory.getConnection_percona();
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

	public List<Double> getEngineRunningBands(String Serial_Number,
			Timestamp txnTimestamp, int seg_ID) {
		// TODO Auto-generated method stub

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		iLogger.info("AMH_DAL getEngineRunningBands: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" START");

		DateFormat dateStr = null;
		Properties prop = null;

		String amhSelectQuery = "";
		String amhFromQuery = "";
		String amhWhereQuery = "";
		String amhGroupNyQuery = "";
		String amhOrderByQuery = "";
		long startTime = 0;
		long endTime =0;
		//Session session = HibernateUtil.getSessionFactory().openSession();

		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;

		try{
			dateStr = new SimpleDateFormat("yyyy-MM-dd");
			prop = CommonUtil.getDepEnvProperties();
		}catch(IllegalArgumentException iag){
			//System.out.println("AMH_DAL Exception in formatting the Date "+iag.getMessage());
			fLogger.fatal("AMH_DAL getEngineRunningBands Exception in formatting the Date "+iag.getMessage());
		}catch(Exception e){
			e.printStackTrace();
			fLogger.fatal("AMH_DAL getEngineRunningBands generic Exceptio:"+e.getMessage());
		}

		//setting the transactional table to the default 
		//when the week of transactional date does not fall in any of the last week data will be fetched from the
		//default tables (considered as backdated data)

		String amhTable = prop.getProperty("default_AMH_Table");
		String amdTable = prop.getProperty("default_AMD_Table");
		String amdETable = prop.getProperty("default_AMDE_Table");


		List<Double> EngineRunningBandList = new ArrayList<Double>();


		//property which tells whether to persist or retrieve data in InMemory database like redis or not 
		//if we persist or retrieve data to or from inMemory DB no need to Query on legacy DB like MySql 

		String PersistTo_InMemory = prop.getProperty("PersistTo_InMemory");

		iLogger.info("AMH_DAL getEngineRunningBands PersistTo_InMemory"+PersistTo_InMemory);

		if(Boolean.parseBoolean(PersistTo_InMemory)){
			//System.out.println("Persist to In Memory storage");
		}
		else{

			//Retrieval from Relational DB 
			iLogger.info("AMH_DAL getEngineRunningBands AMH retrieval START");
			try{
				if(txnTimestamp!=null){
					//returns the HashMap with key as category name and value is corresponding transactional week table name
					// this one is mandatory since we maintaning the 8 weeks transactional table data for amh,amd,amde 
					//this method will give the tables to query for that week of transaction date.

					iLogger.info("");
					HashMap<String, String> dynamicTables = new DateUtil().getCurrentWeekDifference(txnTimestamp);

					amhTable = dynamicTables.get("AMH");

					iLogger.info("AMH_DAL getEngineRunningBands AMH Table Name "+amhTable);

					//System.out.println("AMH_DAL getEngineRunningBands AMH Table Name "+amhTable);
					amdTable = dynamicTables.get("AMD");

					iLogger.info("AMH_DAL getEngineRunningBands AMD Table Name "+amdTable);
					//System.out.println("AMH_DAL getEngineRunningBands AMD Table Name "+amdTable);
					amdETable = dynamicTables.get("AMDE");
					iLogger.info("AMH_DAL getEngineRunningBands AMD Table Name "+amdETable);
					//System.out.println("AMH_DAL getEngineRunningBands AMD Table Name "+amdETable);
				}


				startTime = System.currentTimeMillis();
				iLogger.info("AMH_DAL getEngineRunningBands Query starts ");
				String transaction_timestamp =dateStr.format(txnTimestamp);
				amhSelectQuery = " select amh.Serial_Number," +
						//" SUM(IF(amd.Parameter_ID = '33', amd.Parameter_Value, 0)) AS EngineRunningBand1 " +
						" SUM(case when amd.Parameter_ID='33' then amd.Parameter_Value else 0 end ) AS EngineRunningBand1, " +
						" SUM(case when amd.Parameter_ID='34' then amd.Parameter_Value else 0 end ) AS EngineRunningBand2, " +
						" SUM(case when amd.Parameter_ID='35' then amd.Parameter_Value else 0 end ) AS EngineRunningBand3, " +
						" SUM(case when amd.Parameter_ID='36' then amd.Parameter_Value else 0 end ) AS EngineRunningBand4, " +
						" SUM(case when amd.Parameter_ID='37' then amd.Parameter_Value else 0 end ) AS EngineRunningBand5, " +
						" SUM(case when amd.Parameter_ID='38' then amd.Parameter_Value else 0 end ) AS EngineRunningBand6, " +
						" SUM(case when amd.Parameter_ID='39' then amd.Parameter_Value else 0 end ) AS EngineRunningBand7, " +
						" SUM(case when amd.Parameter_ID='40' then amd.Parameter_Value else 0 end ) AS EngineRunningBand8 ";

				amhFromQuery = " from "+amhTable+" as amh,"+amdTable+" as amd ";

				amhWhereQuery = " where amh.Segment_ID_TxnDate = "+seg_ID+" and amh.Serial_Number= '"+Serial_Number+"' " +
						" and amh.Transaction_Timestamp like '"+transaction_timestamp+"%' and amd.Segment_ID_TxnDate = "+seg_ID+" and amh.Transaction_Number = amd.Transaction_Number " +
						" and amd.Parameter_Value is not null and amh.Record_Type_Id=3";

				connection = connFactory.getConnection_percona();
				statement = connection.createStatement();

				String mainQuery =amhSelectQuery+amhFromQuery+amhWhereQuery;

				iLogger.info("AMH_DAL getEngineRunningBands mainQuery "+mainQuery);
				//	System.out.println("AMH_DAL getEngineRunningBands mainQuery "+mainQuery);

				rs = statement.executeQuery(mainQuery);
				//Iterator runningBandItr = engineBandsQuery.list().iterator();
				//Object[] runningBandResult=null;

				while(rs.next()){
					// runningBandResult = (Object[]) runningBandItr.next();
					//	AssetMonitoringParametersDAO ampDAO = new AssetMonitoringParametersDAO();

					if(rs.getString("EngineRunningBand1")!=null)
						EngineRunningBandList.add( (Double.parseDouble( rs.getString("EngineRunningBand1")))/60);
					if(rs.getString("EngineRunningBand2")!=null)
						EngineRunningBandList.add( (Double.parseDouble(rs.getString("EngineRunningBand2")))/60);
					if(rs.getString("EngineRunningBand3")!=null)
						EngineRunningBandList.add( (Double.parseDouble(rs.getString("EngineRunningBand3")))/60);
					if(rs.getString("EngineRunningBand4")!=null)
						EngineRunningBandList.add( (Double.parseDouble(rs.getString("EngineRunningBand4")))/60);
					if(rs.getString("EngineRunningBand5")!=null)
						EngineRunningBandList.add( (Double.parseDouble(rs.getString("EngineRunningBand5")))/60);
					if(rs.getString("EngineRunningBand6")!=null)
						EngineRunningBandList.add( (Double.parseDouble(rs.getString("EngineRunningBand6")))/60);
					if(rs.getString("EngineRunningBand7")!=null)
						EngineRunningBandList.add( (Double.parseDouble(rs.getString("EngineRunningBand7")))/60);
					if(rs.getString("EngineRunningBand8")!=null)
						EngineRunningBandList.add( (Double.parseDouble(rs.getString("EngineRunningBand8")))/60);

				}
				endTime = System.currentTimeMillis();
				iLogger.info("AMH_DAL getEngineRunningBands: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" Query ends in "+(endTime-startTime));	

				iLogger.info("AMH_DAL getEngineRunningBands Query ends ");
			}catch(Exception ex){
				ex.printStackTrace();
				fLogger.fatal("AMH_DAL getEngineRunningBands generic Exceptio:"+ex.getMessage());
			}
			finally{
				try {
					if(statement != null && !statement.isClosed()){
						statement.close();

					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getEngineRunningBands exception "+e.getMessage());
					e.printStackTrace();
				}
				try {
					if(connection!= null && !connection.isClosed()){
						connection.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getEngineRunningBands exception "+e.getMessage());
					e.printStackTrace();
				}
			}
		}

		iLogger.info("AMH_DAL getEngineRunningBands: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" END");
		return EngineRunningBandList;
	}

	public String getLatestCMH(String Serial_Number,Timestamp txnTimestamp, int seg_ID,List parametreIDList){



		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		iLogger.info("AMH_DAL getLatestCMH: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" START");
		//List<AssetMonitoringParametersDAO> AMPList = new LinkedList<AssetMonitoringParametersDAO>();

		String CMHValue = null;
		String ampSelectQuery = "";
		String ampFromQuery = "";
		String ampWhereQuery = "";
		String ampGroupNyQuery = "";
		String ampOrderByQuery = "";
		String amhTable = null;
		String amdTable = null;
		String amdETable = null;
		String PersistTo_InMemory = null;
		StringBuilder parameterListString = null;

		long startTime = 0;
		long endTime = 0;

		DateFormat dateStr = null;
		Properties prop = null;
		//Session session = HibernateUtil.getSessionFactory().openSession();

		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;

		try{
			dateStr = new SimpleDateFormat("yyyy-MM-dd");
			prop = CommonUtil.getDepEnvProperties();
			amhTable = prop.getProperty("default_AMH_Table");
			amdTable = prop.getProperty("default_AMD_Table");
			amdETable = prop.getProperty("default_AMDE_Table");

			//property which tells whether to persist or retrieve data in InMemory database like redis or not 
			//if we persist or retrieve data to or from inMemory DB no need to Query on legacy DB like MySql 
			PersistTo_InMemory = prop.getProperty("PersistTo_InMemory");
		}catch(Exception e){
			fLogger.fatal("AMH_DAL getLatestCMH: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" exception while retrieving properties file"+e.getMessage());
		}


		if(Boolean.parseBoolean(PersistTo_InMemory)){
			iLogger.info("Persist to In Memory storage");
		}
		else{

			//use Query Param to determine which query to execute   

			try{

				if(txnTimestamp!=null){
					HashMap<String, String> dynamicTables = new DateUtil().getCurrentWeekDifference(txnTimestamp);

					amhTable = dynamicTables.get("AMH");
					amdTable = dynamicTables.get("AMD");
					amdETable = dynamicTables.get("AMDE");
				}
				parameterListString = new ListToStringConversion().getIntegerListString(parametreIDList);

				startTime = System.currentTimeMillis();
				iLogger.info("AMH_DAL getLatestCMH Query starts ");

				String transaction_timestamp =dateStr.format(txnTimestamp);
				ampSelectQuery = " select a.Parameter_ID, a.Parameter_Value ";
				ampFromQuery = " from "+amdTable+" as a, ( "
						+ "select max(b.Transaction_Number) as Transaction_Number "
						+ " from "+amhTable+" as b "
						+ " where b.Segment_ID_TxnDate = "+seg_ID+" and b.Serial_Number = '" + Serial_Number + "'"
						+ " and b.Transaction_Timestamp like '" + transaction_timestamp 
						+ "%') amh";
				ampWhereQuery = " where a.Segment_ID_TxnDate = "+seg_ID+" and a.Transaction_Number = amh.Transaction_Number and " +
						"a.Parameter_ID in (" +parameterListString+")";
				ampOrderByQuery =  " order by a.Parameter_ID";


				String mainQuery = ampSelectQuery+ampFromQuery+ampWhereQuery+ampOrderByQuery;
				//System.out.println(mainQuery);

				connection = connFactory.getConnection_percona();
				statement = connection.createStatement();
				rs = statement.executeQuery(mainQuery);

				//Iterator itr = ampQuery.list().iterator();

				//Object[] result = null;

				while(rs.next()){
					//result = (Object[])itr.next();

					//AssetMonitoringParametersDAO daoObject = new AssetMonitoringParametersDAO();

					/*daoObject.setParameterID(rs.getInt("Parameter_ID"));
				daoObject.setParameterValue(rs.getString("Parameter_Value"));
				AMPList.add(daoObject);*/

					if(rs.getInt("Parameter_ID") == 4){
						CMHValue = rs.getString("Parameter_Value");
					}
				}

				endTime = System.currentTimeMillis();
				iLogger.info("AMH_DAL getLatestCMH: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" Query ends in "+(endTime-startTime));	
			}catch(Exception ex){
				ex.printStackTrace();
				fLogger.fatal("AMH_DAL getLatestCMH: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" exception "+ex.getMessage());
			}
			finally{
				try {
					if(statement != null && !statement.isClosed()){
						statement.close();

					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					if(connection !=null && !connection.isClosed()){
						connection.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		//endTime = System.currentTimeMillis();

		iLogger.info("AMH_DAL getLatestCMH: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" END");
		iLogger.info("AMH_DAL getLatestCMH: CMH for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" is "+CMHValue);
		return CMHValue;


	}
	public List<AssetMonitoringParametersDAO> getAMPValues(String Serial_Number,Timestamp txnTimestamp, int seg_ID,List parametreIDList){


		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		iLogger.info("AMH_DAL getAMPValues: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" START");
		List<AssetMonitoringParametersDAO> AMPList = new LinkedList<AssetMonitoringParametersDAO>();


		String ampSelectQuery = "";
		String ampFromQuery = "";
		String ampWhereQuery = "";
		String ampGroupNyQuery = "";
		String ampOrderByQuery = "";
		String amhTable = null;
		String amdTable = null;
		String amdETable = null;
		String PersistTo_InMemory = null;
		StringBuilder parameterListString = null;

		long startTime = 0;
		long endTime = 0;

		DateFormat dateStr = null;
		Properties prop = null;
		//Session session = HibernateUtil.getSessionFactory().openSession();

		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;

		try{
			prop = CommonUtil.getDepEnvProperties();
			amhTable = prop.getProperty("default_AMH_Table");
			amdTable = prop.getProperty("default_AMD_Table");
			amdETable = prop.getProperty("default_AMDE_Table");

			//property which tells whether to persist or retrieve data in InMemory database like redis or not 
			//if we persist or retrieve data to or from inMemory DB no need to Query on legacy DB like MySql 
			PersistTo_InMemory = prop.getProperty("PersistTo_InMemory");
		}catch(Exception e){
			fLogger.fatal("AMH_DAL getAMPValues: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" exception while retrieving properties file"+e.getMessage());
		}


		if(Boolean.parseBoolean(PersistTo_InMemory)){
			//System.out.println("Persist to In Memory storage");
		}
		else{

			//use Query Param to determine which query to execute   

			try{

				if(txnTimestamp!=null){
					HashMap<String, String> dynamicTables = new DateUtil().getCurrentWeekDifference(txnTimestamp);

					amhTable = dynamicTables.get("AMH");
					amdTable = dynamicTables.get("AMD");
					amdETable = dynamicTables.get("AMDE");
				}
				parameterListString = new ListToStringConversion().getIntegerListString(parametreIDList);

				startTime = System.currentTimeMillis();
				iLogger.info("AMH_DAL getAMPValues Query starts ");


				ampSelectQuery = " select a.Parameter_ID, a.Parameter_Value ";
				ampFromQuery = " from "+amdTable+" as a, ( "
						+ "select max(b.Transaction_Number) as Transaction_Number "
						+ " from "+amhTable+" as b "
						+ " where b.Segment_ID_TxnDate = "+seg_ID+" and b.Serial_Number = '" + Serial_Number + "'"
						+ " and b.Transaction_Timestamp = '" + txnTimestamp 
						+ "') amh";
				ampWhereQuery = " where a.Segment_ID_TxnDate = "+seg_ID+" and a.Transaction_Number = amh.Transaction_Number and " +
						"a.Parameter_ID in (" +parameterListString+")";
				ampOrderByQuery =  " order by a.Parameter_ID";


				String mainQuery = ampSelectQuery+ampFromQuery+ampWhereQuery+ampOrderByQuery;
				//System.out.println(mainQuery);

				connection = connFactory.getConnection_percona();
				statement = connection.createStatement();
				rs = statement.executeQuery(mainQuery);

				//Iterator itr = ampQuery.list().iterator();

				//Object[] result = null;

				while(rs.next()){
					//result = (Object[])itr.next();

					AssetMonitoringParametersDAO daoObject = new AssetMonitoringParametersDAO();

					daoObject.setParameterID(rs.getInt("Parameter_ID"));
					daoObject.setParameterValue(rs.getString("Parameter_Value"));
					AMPList.add(daoObject);
				}

				endTime = System.currentTimeMillis();
				iLogger.info("AMH_DAL getAMPValues: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" Query ends in "+(endTime-startTime));	
			}catch(Exception ex){
				ex.printStackTrace();
				fLogger.fatal("AMH_DAL getAMPValues: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" exception "+ex.getMessage());
			}
			finally{
				try {
					if(statement != null && !statement.isClosed()){
						statement.close();

					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getAMPValues exception "+e.getMessage());
					e.printStackTrace();
				}
				try {
					if(connection!= null && !connection.isClosed()){
						connection.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getAMPValues exception "+e.getMessage());
					e.printStackTrace();
				}
			}
		}

		//endTime = System.currentTimeMillis();

		iLogger.info("AMH_DAL getAMPValues: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" END");
		return AMPList;

	}


	public Timestamp getLatestEngineONTxn(String Serial_Number,Timestamp txnTimestamp, int seg_ID,List parametreIDList){

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<AssetMonitoringParametersDAO> AMPList = new LinkedList<AssetMonitoringParametersDAO>();

		iLogger.info("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" START");

		String amhTable = null;
		String amdTable = null;
		String amdETable = null;
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
			amdTable = prop.getProperty("default_AMD_Table");
			amdETable = prop.getProperty("default_AMDE_Table");

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
					HashMap<String, String> dynamicTables = new DateUtil().getCurrentWeekDifference(txnTimestamp);

					amhTable = dynamicTables.get("AMH");
					amdTable = dynamicTables.get("AMD");
					amdETable = dynamicTables.get("AMDE");
				}
				parameterListString = new ListToStringConversion().getIntegerListString(parametreIDList);

				startTime = System.currentTimeMillis();
				iLogger.info("AMH_DAL getLatestEngineONTxn Query starts ");

				ampSelectQuery = " select b.Transaction_Timestamp ";
				ampFromQuery = " from "+amdTable+" as a,"+amhTable+" as b ";

				ampWhereQuery = " where b.Segment_ID_TxnDate = "+seg_ID+" and b.Serial_Number = '" + Serial_Number + "'"
						+ " and b.Transaction_Timestamp < '" + txnTimestamp +"' and a.Segment_ID_TxnDate = "+seg_ID+" and a.Transaction_Number = b.Transaction_Number and " +
						"a.Parameter_ID in (" +parameterListString+") and a.Parameter_Value = 1";
				ampOrderByQuery =  " order by b.Transaction_Timestamp desc ";

				String mainQuery =ampSelectQuery+ampFromQuery+ampWhereQuery+ampOrderByQuery;

				iLogger.info("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" main Query: "+mainQuery);

				//System.out.println("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" main Query: "+mainQuery);

				connection = connFactory.getConnection_percona();
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


	@Override
	public String updateAMHData(String txnKey,
			HashMap<String, String> payloadMap, int segmentId, int recordType,
			int updatecount) {
		// TODO Auto-generated method stub
		return null;
	}


	public List<String> getAMHTablesPerVIN_N_OLAPDate(String serial_Number,Timestamp maxOlapDt,int segID){
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		List<String> dynamicAMHTables = new LinkedList<String>();
		Calendar cal = Calendar.getInstance();
		int weekNo = cal.get(Calendar.WEEK_OF_YEAR);
		Properties prop = CommonUtil.getDepEnvProperties();
		int transactionYear = cal.get(Calendar.YEAR);
		String amhTable = null;
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null; 


		Timestamp ts = new Timestamp(cal.getTimeInMillis());
		String JCB_AMH_TableKey = prop.getProperty("JCB_AMH_TableKey");

		amhTable = JCB_AMH_TableKey.replace("week", weekNo+"");
		amhTable =amhTable.replace("year", transactionYear+"");
		boolean recordNotFound = true;
		connection = connFactory.getConnection_percona();
		try {
			statement = connection.createStatement();

			for(int i=1;i<=8;i++){
				try{

					String query = "select * from "+amhTable +" where Segment_ID_TxnDate = "+segID+" and Serial_Number = '"+serial_Number+"' and Last_Updated_Timestamp >= '"+maxOlapDt+"'";
					//System.out.println(query);
					rs = statement.executeQuery(query);
					if(rs.next()){
						recordNotFound = false;
						dynamicAMHTables.add(amhTable);
						//break;
					}
					amhTable = JCB_AMH_TableKey.replace("week", (weekNo-i)+"");
					amhTable =amhTable.replace("year", transactionYear+"");
				}catch(SQLException se){
					//	System.out.println(se.getErrorCode());

					//System.out.println(""+(se instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException));
					if(se instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException)
					{
						//se.printStackTrace();
						dynamicAMHTables.remove(amhTable);
						return dynamicAMHTables;
					}
					else
						return null;
				}
			}
			//HashMap<String, String> dynamicTables = new DateUtil().getCurrentWeekDifference(txnTimestamp);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			fLogger.fatal("getAMHTablesPerVIN_N_OLAPDate exception "+e.getMessage());
			e.printStackTrace();
		}
		finally{
			try {
				if(statement != null && !statement.isClosed()){
					statement.close();

				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				fLogger.fatal("getAMHTablesPerVIN_N_OLAPDate exception "+e.getMessage());
				e.printStackTrace();
			}
			try {
				if(connection!= null && !connection.isClosed()){
					connection.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				fLogger.fatal("getAMHTablesPerVIN_N_OLAPDate exception "+e.getMessage());
				e.printStackTrace();
			}
		}
		if(recordNotFound)
			return dynamicAMHTables;
		return dynamicAMHTables;
	}


	//DF20161222 @Roopa changing AMH and AMD into single TAssetMOnData table and taking parameters from json column TxnData
	public List<AssetEventLogImpl> getAMHAMDListForEventMap(String query, int seg_id, String serial_number, String startQuery,String endQuery, String timeZone) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;


		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;


		List<AssetEventLogImpl> assetEventLogImplList =new ArrayList<AssetEventLogImpl>();

		String engineOn = null;
		String ignitionOn = null;

		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			fLogger.fatal(":AMH:DAL-AMH-getAMHAMDListForEventMap"+"Error in intializing property File :"+e.getMessage());
		}

		engineOn = prop.getProperty("EngineON");
		ignitionOn = prop.getProperty("Ignition_ON");

		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		String engineOn_ams=null;
		String ignitionOn_ams=null;




		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info(":AMH:DAL-AMH-getAMHAMDListForEventMap"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{

			/*<!=========START fetching from Native Database==============>*/


			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getDatalakeConnection_3309();
				statement = prodConnection.createStatement();

				HashMap<String,String> txnDataMap=new HashMap<String, String>();

				HashMap<String,String> messageIDMap=new HashMap<String, String>();

				String txnData;
				String messageID;
				int recType=0;
				String Events1;
				HashMap<String,String> Events1Map=new HashMap<String, String>();

				GmtLtTimeConversion convertedTime = new GmtLtTimeConversion();
				String convertedTimestamp=null;
//				iLogger.info("TAssetMon Query:::::::>" + query);
				if(query!=null){
					rs = statement.executeQuery(query);
					while(rs.next()){

//DF20171005 @Roopa Fix for Multiple Events with same TS are not visible in the eventmap
						
						System.out.println(rs.getRow());
						
						//DF20170809: SU334449 - Specific TimeZone changes corresponding to VIN.
						if(String.valueOf(rs.getTimestamp("Transaction_Timestamp"))!=null)
							convertedTimestamp = convertedTime.convertGmtToLocal(timeZone, String.valueOf(rs.getTimestamp("Transaction_Timestamp")));


						txnData=rs.getObject("TxnData").toString();

						if(txnData!=null)
							txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());

						Events1=rs.getObject("Events").toString();

						if(Events1!=null)
							Events1Map = new Gson().fromJson(Events1, new TypeToken<HashMap<String, Object>>() {}.getType());

						DynamicTAssetMonData_DAL obj=new DynamicTAssetMonData_DAL();

						messageID=rs.getObject("Message_ID").toString();

						messageIDMap = new Gson().fromJson(messageID, new TypeToken<HashMap<String, Object>>() {}.getType());

						String recordType=messageIDMap.get("EVT");

						String logRecordType=messageIDMap.get("LOG");

						String logPTRecordType=messageIDMap.get("LOG_PT");
						//Df20170905 @roopa Handling both log and EVT received for the same Transaction TS.
						/*
						if(recordType!=null && logRecordType!=null){
							AssetEventLogImpl implObj1 = new AssetEventLogImpl();

							implObj1.setParamName("10 Minute Report");
							implObj1.setParameterValue("1");
							implObj1.setLatitude(txnDataMap.get("LAT"));
							implObj1.setLongitude(txnDataMap.get("LONG"));
							implObj1.setAlertSeverity("NA");
							implObj1.setRecord_Type_Id(3);
							implObj1.setEventGeneratedTime(convertedTimestamp);
							assetEventLogImplList.add(implObj1);

						}*/

						if(logRecordType!=null){

							AssetEventLogImpl implObj1 = new AssetEventLogImpl();

							implObj1.setParamName("10 Minute Report"+"#"+txnDataMap.get("CMH"));
							implObj1.setParameterValue("1");
							implObj1.setLatitude(txnDataMap.get("LAT"));
							implObj1.setLongitude(txnDataMap.get("LONG"));
							implObj1.setAlertSeverity("NA");
							implObj1.setRecord_Type_Id(3);
							implObj1.setEventGeneratedTime(convertedTimestamp);
							implObj1.setIsengineOn("1");
							assetEventLogImplList.add(implObj1);


						}
						if(logPTRecordType!=null){

							AssetEventLogImpl implObj1 = new AssetEventLogImpl();

							implObj1.setParamName("PT Log"+"#"+txnDataMap.get("CMH"));
							implObj1.setParameterValue("1");
							implObj1.setLatitude(txnDataMap.get("LAT"));
							implObj1.setLongitude(txnDataMap.get("LONG"));
							implObj1.setAlertSeverity("NA");
							implObj1.setRecord_Type_Id(3);
							implObj1.setEventGeneratedTime(convertedTimestamp);
							implObj1.setIsengineOn("1");
							assetEventLogImplList.add(implObj1);


						}
						if(recordType!=null){
							for(Entry entry:Events1Map.entrySet()){

								AssetEventLogImpl implObj = new AssetEventLogImpl();
//	                            System.out.println(entry.getKey());
								//DF20210524 Avinash Xavier A Events Map :Delete the alert that is not in asset event from the list instead of showing as engine on
								if(!entry.getKey().equals("EVT_ENG") && !entry.getKey().equals("EVT_IGN") && !entry.getKey().equals("EVT_HELLO") ){
//									System.out.println("non engine on alert ");
									implObj.setIsengineOn("0");
								}else{
//									System.out.println(entry.getKey());
									implObj.setIsengineOn("1");
								}
								//if(((String)entry.getKey()).contains("EVT_")){
								

								implObj.setEventGeneratedTime(convertedTimestamp);


								implObj.setRecord_Type_Id(2);




								//Df20170608 @Roopa taking the eng ang ign status from the received packet if not exist taking from the prev pkt the corresponding ign/eng status


								if(txnDataMap.get("ENG_STATUS")!=null && ! txnDataMap.get("ENG_STATUS").equalsIgnoreCase("null")){
									engineOn_ams=txnDataMap.get("ENG_STATUS");
								}
								else{
									engineOn_ams=	txnDataMap.get("EVT_ENG");
								}


								if(engineOn_ams==null){

									DynamicTAssetMonData_DAL amdDaoObj=new DynamicTAssetMonData_DAL();
									List<TAssetMonDataDAO> amdDaoList=new LinkedList<TAssetMonDataDAO>();
									try{
										amdDaoList=amdDaoObj.getPrevTAssetMonData("EventMapService",serial_number,rs.getTimestamp("Transaction_Timestamp"), seg_id,"EVT_ENG");

										if(amdDaoList!=null && amdDaoList.size()>0){
											HashMap<String,String> txnDataMap1=amdDaoList.get(0).getTxnData();
											engineOn_ams=txnDataMap1.get("EVT_ENG");

										}

									}

									catch(Exception e){
										e.printStackTrace();
									}

								}


								implObj.setLatitude(txnDataMap.get("LAT"));
								implObj.setLongitude(txnDataMap.get("LONG"));
								implObj.setAlertSeverity("NA");



								if( (txnDataMap.containsKey("EVT_HELLO")) && (txnDataMap.get("EVT_HELLO").equalsIgnoreCase("1")) ) 
								{
									implObj.setParamName("Hello"+"#"+txnDataMap.get("CMH"));
									implObj.setParameterValue("1");
								}
								else if(entry.getKey().equals("Unknown_ErrorCode")){ //DF20170407 @Roopa Fetching unknown dtc codes also if any

									//key=Unknown_ErrorCode, value=dtccode_status

									//Df20170525 @Roopa assigning yellow severity to unknown dtc codes.

//									implObj.setParamName((Events1Map.get("Unknown_ErrorCode").split("_")[0])+"-Unknown Error"+"#"+txnDataMap.get("CMH")); //Unknown_ErrorCode_dtccode
//									implObj.setParameterValue(Events1Map.get("Unknown_ErrorCode").split("_")[1]); //dtc status 0/1
//									implObj.setAlertSeverity("YELLOW");
									if (Events1Map.get("Unknown_ErrorCode").split("_")[0].equals("99999999")){
										implObj.setParamName("DTC refreshed");
										implObj.setParameterValue(Events1Map.get("Unknown_ErrorCode").split("_")[1]); 
										implObj.setAlertSeverity("GREEN");
									}
									else{
										implObj.setParamName((Events1Map.get("Unknown_ErrorCode").split("_")[0])+"-Unknown Error"+"#"+txnDataMap.get("CMH")); //Unknown_ErrorCode_dtccode
										implObj.setParameterValue(Events1Map.get("Unknown_ErrorCode").split("_")[1]); //dtc status 0/1
										implObj.setAlertSeverity("YELLOW");
									}

								}
								else if(entry.getKey().equals("EVT_IGN")) //DF20170704 @Roopa not considering ignition event for eventmap
								{
									continue;
								}
								else
								{

									if(engineOn_ams.equalsIgnoreCase("1"))
									{
										//commenting below check bcoz anyway we have to set it to engineon


										implObj.setParamName(engineOn+"#"+txnDataMap.get("CMH"));
										implObj.setParameterValue("1");

									}
									else{
										//DF20170627 @Roopa changing the matrix, considering only engine on/off for eventmap display
										implObj.setParamName(engineOn+"#"+txnDataMap.get("CMH")); 
										implObj.setParameterValue("0");	
									}


								}



								assetEventLogImplList.add(implObj);
								/*for(AssetEventLogImpl a:assetEventLogImplList)
								System.out.println("Output::"+a.getParamName());*/
							}
							//}
						}
					}
				}
				else{



					rs = statement.executeQuery(startQuery);

					while(rs.next()){


						//DF20170809: SU334449 - Specific TimeZone changes corresponding to VIN.
						if(String.valueOf(rs.getTimestamp("Transaction_Timestamp"))!=null)
							convertedTimestamp = convertedTime.convertGmtToLocal(timeZone, String.valueOf(rs.getTimestamp("Transaction_Timestamp")));


						txnData=rs.getObject("TxnData").toString();

						if(txnData!=null)
							txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());

						Events1=rs.getObject("Events").toString();

						if(Events1!=null)
							Events1Map = new Gson().fromJson(Events1, new TypeToken<HashMap<String, Object>>() {}.getType());

						DynamicTAssetMonData_DAL obj=new DynamicTAssetMonData_DAL();

						messageID=rs.getObject("Message_ID").toString();

						messageIDMap = new Gson().fromJson(messageID, new TypeToken<HashMap<String, Object>>() {}.getType());

						String recordType=messageIDMap.get("EVT");

						String logRecordType=messageIDMap.get("LOG");
						
						String logPTRecordType=messageIDMap.get("LOG_PT");

						//Df20170905 @roopa Handling both log and EVT received for the same Transaction TS.
						/*
						if(recordType!=null && logRecordType!=null){
							AssetEventLogImpl implObj1 = new AssetEventLogImpl();

							implObj1.setParamName("10 Minute Report");
							implObj1.setParameterValue("1");
							implObj1.setLatitude(txnDataMap.get("LAT"));
							implObj1.setLongitude(txnDataMap.get("LONG"));
							implObj1.setAlertSeverity("NA");
							implObj1.setRecord_Type_Id(3);
							implObj1.setEventGeneratedTime(convertedTimestamp);
							assetEventLogImplList.add(implObj1);

						}*/

						if(logRecordType!=null){

							AssetEventLogImpl implObj1 = new AssetEventLogImpl();

							implObj1.setParamName("10 Minute Report"+"#"+txnDataMap.get("CMH"));
							implObj1.setParameterValue("1");
							implObj1.setLatitude(txnDataMap.get("LAT"));
							implObj1.setLongitude(txnDataMap.get("LONG"));
							implObj1.setAlertSeverity("NA");
							implObj1.setRecord_Type_Id(3);
							implObj1.setEventGeneratedTime(convertedTimestamp);
							implObj1.setIsengineOn("1");
							assetEventLogImplList.add(implObj1);


						}
						if(logPTRecordType!=null){

							AssetEventLogImpl implObj1 = new AssetEventLogImpl();

							implObj1.setParamName("PT Log"+"#"+txnDataMap.get("CMH"));
							implObj1.setParameterValue("1");
							implObj1.setLatitude(txnDataMap.get("LAT"));
							implObj1.setLongitude(txnDataMap.get("LONG"));
							implObj1.setAlertSeverity("NA");
							implObj1.setRecord_Type_Id(3);
							implObj1.setEventGeneratedTime(convertedTimestamp);
							implObj1.setIsengineOn("1");
							assetEventLogImplList.add(implObj1);


						}
						if(recordType!=null){
							for(Entry entry:Events1Map.entrySet()){

								AssetEventLogImpl implObj = new AssetEventLogImpl();
								if(!entry.getKey().equals("EVT_ENG") && !entry.getKey().equals("EVT_IGN") && !entry.getKey().equals("EVT_HELLO") ){
//									System.out.println("not engine one alert ");
									implObj.setIsengineOn("0");
								}else{
									implObj.setIsengineOn("1");
								}

								implObj.setEventGeneratedTime(convertedTimestamp);


								implObj.setRecord_Type_Id(2);




								//Df20170608 @Roopa taking the eng ang ign status from the received packet if not exist taking from the prev pkt the corresponding ign/eng status


								if(txnDataMap.get("ENG_STATUS")!=null && ! txnDataMap.get("ENG_STATUS").equalsIgnoreCase("null")){
									engineOn_ams=txnDataMap.get("ENG_STATUS");
								}
								else{
									engineOn_ams=	txnDataMap.get("EVT_ENG");
								}


								if(engineOn_ams==null){

									DynamicTAssetMonData_DAL amdDaoObj=new DynamicTAssetMonData_DAL();
									List<TAssetMonDataDAO> amdDaoList=new LinkedList<TAssetMonDataDAO>();
									try{
										amdDaoList=amdDaoObj.getPrevTAssetMonData("EventMapService",serial_number,rs.getTimestamp("Transaction_Timestamp"), seg_id,"EVT_ENG");

										if(amdDaoList!=null && amdDaoList.size()>0){
											HashMap<String,String> txnDataMap1=amdDaoList.get(0).getTxnData();
											engineOn_ams=txnDataMap1.get("EVT_ENG");

										}

									}

									catch(Exception e){
										e.printStackTrace();
									}

								}


								implObj.setLatitude(txnDataMap.get("LAT"));
								implObj.setLongitude(txnDataMap.get("LONG"));
								implObj.setAlertSeverity("NA");



								if( (txnDataMap.containsKey("EVT_HELLO")) && (txnDataMap.get("EVT_HELLO").equalsIgnoreCase("1")) ) 
								{
									implObj.setParamName("Hello"+"#"+txnDataMap.get("CMH"));
									implObj.setParameterValue("1");
								}
								else if(entry.getKey().equals("Unknown_ErrorCode")){ //DF20170407 @Roopa Fetching unknown dtc codes also if any

									//key=Unknown_ErrorCode, value=dtccode_status

									//Df20170525 @Roopa assigning yellow severity to unknown dtc codes.

									implObj.setParamName((Events1Map.get("Unknown_ErrorCode").split("_")[0])+"-Unknown Error"+"#"+txnDataMap.get("CMH")); //Unknown_ErrorCode_dtccode
									implObj.setParameterValue(Events1Map.get("Unknown_ErrorCode").split("_")[1]); //dtc status 0/1
									implObj.setAlertSeverity("YELLOW");

								}
								else if(entry.getKey().equals("EVT_IGN")) //DF20170704 @Roopa not considering ignition event for eventmap
								{
									continue;
								}
								else
								{

									if(engineOn_ams.equalsIgnoreCase("1"))
									{
										//commenting below check bcoz anyway we have to set it to engineon


										implObj.setParamName(engineOn+"#"+txnDataMap.get("CMH"));
										implObj.setParameterValue("1");

									}
									else{
										//DF20170627 @Roopa changing the matrix, considering only engine on/off for eventmap display
										implObj.setParamName(engineOn+"#"+txnDataMap.get("CMH")); 
										implObj.setParameterValue("0");	
									}


								}



								assetEventLogImplList.add(implObj);
							}
						}
					}

					//end table

					rs = statement.executeQuery(endQuery);

					while(rs.next()){


						//DF20170809: SU334449 - Specific TimeZone changes corresponding to VIN.
						if(String.valueOf(rs.getTimestamp("Transaction_Timestamp"))!=null)
							convertedTimestamp = convertedTime.convertGmtToLocal(timeZone, String.valueOf(rs.getTimestamp("Transaction_Timestamp")));


						txnData=rs.getObject("TxnData").toString();

						if(txnData!=null)
							txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());

						Events1=rs.getObject("Events").toString();

						if(Events1!=null)
							Events1Map = new Gson().fromJson(Events1, new TypeToken<HashMap<String, Object>>() {}.getType());

						DynamicTAssetMonData_DAL obj=new DynamicTAssetMonData_DAL();

						messageID=rs.getObject("Message_ID").toString();

						messageIDMap = new Gson().fromJson(messageID, new TypeToken<HashMap<String, Object>>() {}.getType());

						String recordType=messageIDMap.get("EVT");

						String logRecordType=messageIDMap.get("LOG");

						//Df20170905 @roopa Handling both log and EVT received for the same Transaction TS.
						/*
						if(recordType!=null && logRecordType!=null){
							AssetEventLogImpl implObj1 = new AssetEventLogImpl();

							implObj1.setParamName("10 Minute Report");
							implObj1.setParameterValue("1");
							implObj1.setLatitude(txnDataMap.get("LAT"));
							implObj1.setLongitude(txnDataMap.get("LONG"));
							implObj1.setAlertSeverity("NA");
							implObj1.setRecord_Type_Id(3);
							implObj1.setEventGeneratedTime(convertedTimestamp);
							assetEventLogImplList.add(implObj1);

						}*/

						if(logRecordType!=null){

							AssetEventLogImpl implObj1 = new AssetEventLogImpl();

							implObj1.setParamName("10 Minute Report"+"#"+txnDataMap.get("CMH"));
							implObj1.setParameterValue("1");
							implObj1.setLatitude(txnDataMap.get("LAT"));
							implObj1.setLongitude(txnDataMap.get("LONG"));
							implObj1.setAlertSeverity("NA");
							implObj1.setRecord_Type_Id(3);
							implObj1.setEventGeneratedTime(convertedTimestamp);
							implObj1.setIsengineOn("1");
							assetEventLogImplList.add(implObj1);


						}
						if(recordType!=null){
							for(Entry entry:Events1Map.entrySet()){
								
								AssetEventLogImpl implObj = new AssetEventLogImpl();
								if(!entry.getKey().equals("EVT_ENG") && !entry.getKey().equals("EVT_IGN") && !entry.getKey().equals("EVT_HELLO") ){
//									System.out.println("not engine one alert ");
									implObj.setIsengineOn("0");
								}else{
									implObj.setIsengineOn("1");
								}

								implObj.setEventGeneratedTime(convertedTimestamp);


								implObj.setRecord_Type_Id(2);




								//Df20170608 @Roopa taking the eng ang ign status from the received packet if not exist taking from the prev pkt the corresponding ign/eng status


								if(txnDataMap.get("ENG_STATUS")!=null && ! txnDataMap.get("ENG_STATUS").equalsIgnoreCase("null")){
									engineOn_ams=txnDataMap.get("ENG_STATUS");
								}
								else{
									engineOn_ams=	txnDataMap.get("EVT_ENG");
								}


								if(engineOn_ams==null){

									DynamicTAssetMonData_DAL amdDaoObj=new DynamicTAssetMonData_DAL();
									List<TAssetMonDataDAO> amdDaoList=new LinkedList<TAssetMonDataDAO>();
									try{
										amdDaoList=amdDaoObj.getPrevTAssetMonData("EventMapService",serial_number,rs.getTimestamp("Transaction_Timestamp"), seg_id,"EVT_ENG");

										if(amdDaoList!=null && amdDaoList.size()>0){
											HashMap<String,String> txnDataMap1=amdDaoList.get(0).getTxnData();
											engineOn_ams=txnDataMap1.get("EVT_ENG");

										}

									}

									catch(Exception e){
										e.printStackTrace();
									}

								}


								implObj.setLatitude(txnDataMap.get("LAT"));
								implObj.setLongitude(txnDataMap.get("LONG"));
								implObj.setAlertSeverity("NA");



								if( (txnDataMap.containsKey("EVT_HELLO")) && (txnDataMap.get("EVT_HELLO").equalsIgnoreCase("1")) ) 
								{
									implObj.setParamName("Hello"+"#"+txnDataMap.get("CMH"));
									implObj.setParameterValue("1");
								}
								else if(entry.getKey().equals("Unknown_ErrorCode")){ //DF20170407 @Roopa Fetching unknown dtc codes also if any

									//key=Unknown_ErrorCode, value=dtccode_status

									//Df20170525 @Roopa assigning yellow severity to unknown dtc codes.

									implObj.setParamName((Events1Map.get("Unknown_ErrorCode").split("_")[0])+"-Unknown Error"+"#"+txnDataMap.get("CMH")); //Unknown_ErrorCode_dtccode
									implObj.setParameterValue(Events1Map.get("Unknown_ErrorCode").split("_")[1]); //dtc status 0/1
									implObj.setAlertSeverity("YELLOW");

								}
								else if(entry.getKey().equals("EVT_IGN")) //DF20170704 @Roopa not considering ignition event for eventmap
								{
									continue;
								}
								else
								{

									if(engineOn_ams.equalsIgnoreCase("1"))
									{
										//commenting below check bcoz anyway we have to set it to engineon


										implObj.setParamName(engineOn+"#"+txnDataMap.get("CMH"));
										implObj.setParameterValue("1");

									}
									else{
										//DF20170627 @Roopa changing the matrix, considering only engine on/off for eventmap display
										implObj.setParamName(engineOn+"#"+txnDataMap.get("CMH")); 
										implObj.setParameterValue("0");	
									}


								}



								assetEventLogImplList.add(implObj);
							}
						}
					}





				}
			}
			catch (SQLException e) {

				e.printStackTrace();
				fLogger.fatal(":AMH:DAL-AMH-getAMHAMDListForEventMap"+"SQL Exception in fetching data from mysql::"+e.getMessage());
			} 

			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal(":AMH:DAL-AMH-getAMHAMDListForEventMap"+"Exception in fetching data from mysql::"+e.getMessage());
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

		return assetEventLogImplList;
	}	

	//DF20161222 @Roopa changing AMH and AMD into single TAssetMOnData table and taking parameters from json column TxnData
	public List<AssetMonitoringParametersDAO> getAMPValuesInTxnRange(Map asset2SegID,String txnTimestamp, String period){


		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		iLogger.info("AMH_DAL getAMPValuesInTxnRange: for START");
		List<AssetMonitoringParametersDAO> AMPList = new LinkedList<AssetMonitoringParametersDAO>();

		iLogger.info(">>>>>asset2SegID ::"+asset2SegID+"txnTimestamp ::"+txnTimestamp+"period ::"+period);
		String ampSelectQuery = "";
		String ampFromQuery = "";
		/*String ampWhereQuery = "";
		String ampGroupNyQuery = "";
		String ampOrderByQuery = "";

		String amhTable = null;
		String amdTable = null;
		String amdETable = null;*/

		String PersistTo_InMemory = null;

		long startTime = 0;
		long endTime = 0;

		Properties prop = null;
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;

		/*String startDateAMHTable = null;
		String endDateAMHTable = null;
		String startDateAMDTable = null;
		String endDateAMDTable = null;*/

		String startTAssetMonTable=null;
		String endTAssetMonTable=null;


		try{
			prop = CommonUtil.getDepEnvProperties();


			//property which tells whether to persist or retrieve data in InMemory database like redis or not 
			//if we persist or retrieve data to or from inMemory DB no need to Query on legacy DB like MySql 
			PersistTo_InMemory = prop.getProperty("PersistTo_InMemory");
		}catch(Exception e){
			fLogger.fatal("AMH_DAL getAMPValuesInTxnRange: for  exception while retrieving properties file"+e.getMessage());
		}


		if(Boolean.parseBoolean(PersistTo_InMemory)){
			//System.out.println("Persist to In Memory storage");
		}
		else{


			try{

				Calendar calendar1 = Calendar.getInstance();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//dateStr.setLenient(false);
				String currDate =txnTimestamp;
				String strCurrDate = currDate;

				Date date3 = dateFormat.parse(strCurrDate);
				Timestamp currTimestamp=new Timestamp(date3.getTime());
				//				Date date4 = dateFormat1.parse(currDate);
				calendar1.setTime(date3);
				// get previous day
				calendar1.add(Calendar.DAY_OF_YEAR, -1);

				String prevDate = dateFormat.format(calendar1.getTime());
				// data will be take from previous day 6:30 to today 6:20
				prevDate = prevDate + " 18:30:00";
				strCurrDate = strCurrDate + " 18:20:00";



				//System.out.println(prevDate+" currnetDate "+strCurrDate);
				Calendar cal = Calendar.getInstance();
				// Date st = dateStr.parse(strCurrDate);
				//System.out.println(st);
				cal.setTime(dateStr.parse(strCurrDate));

				Timestamp starttxnTimestamp = new Timestamp(cal.getTimeInMillis());

				String dynamicTable=new DateUtil().getDynamicTable("Fleet Utilization Service", starttxnTimestamp);

				if(dynamicTable!=null){
					startTAssetMonTable=dynamicTable;

				}

				/*HashMap<String, String> dynamicTables = new DateUtil().getCurrentWeekDifference(starttxnTimestamp);
				 if(dynamicTables!=null){
					 startDateAMHTable = dynamicTables.get("AMH");
					 startDateAMDTable = dynamicTables.get("AMD");
				 }*/


				//Calendar cal = Calendar.getInstance();
				cal.setTime(dateStr.parse(prevDate));

				Timestamp endtxnTimestamp = new Timestamp(cal.getTimeInMillis());

				/* HashMap<String, String> dynamicTables2 = new DateUtil().getCurrentWeekDifference(endtxnTimestamp);
				 if(dynamicTables2!=null){
					 endDateAMHTable = dynamicTables2.get("AMH");
				   endDateAMDTable = dynamicTables2.get("AMD");;
			 }
				 */

				String endDynamicTable=new DateUtil().getDynamicTable("Fleet Utilization Service", endtxnTimestamp);

				if(endDynamicTable!=null){
					endTAssetMonTable=endDynamicTable;

				}


				/*if(txnTimestamp!=null){
				HashMap<String, String> dynamicTables = new DateUtil().getCurrentWeekDifference(txnTimestamp);

					amhTable = dynamicTables.get("AMH");
					amdTable = dynamicTables.get("AMD");
					amdETable = dynamicTables.get("AMDE");
			}*/


				// parameterListString = new ListToStringConversion().getIntegerListString(parametreIDList);

				startTime = System.currentTimeMillis();
				iLogger.info("AMH_DAL getAMPValuesInTxnRange Query starts ");
				connection = connFactory.getDatalakeConnection_3309();
				//special condition when the current date is the first date of the week sunday 
				//then previous date will be found in last week dynamic amh n amd table while curent date in current week amh and amd tables

				Iterator it = asset2SegID.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry entry = (Map.Entry) it.next();
					String Serial_Number = (String) entry.getKey();
					int seg_ID = (Integer) entry.getValue();

					//get previous eng status from AMS start

					DynamicAMS_Doc_DAL amsDAL=new DynamicAMS_Doc_DAL();

					List<AMSDoc_DAO> amsList=amsDAL.getAMSData("AssetEventLogService", Serial_Number);

					final HashMap<String,String> Events= amsList.get(0).getEvents();

					String engStatus=null;
					String evtengstatus_ams=null;

					if(Events!=null && Events.size()!=0){
						evtengstatus_ams=Events.get("EVT_ENG");
					}

					if(evtengstatus_ams==null || evtengstatus_ams.equalsIgnoreCase("null")){
						evtengstatus_ams="0";
					}

					//get previous eng status from AMS end

					HashMap<String,String> txnDataMap=new HashMap<String, String>();
					String txnData=null;


					if(!startTAssetMonTable.equals(endTAssetMonTable)){


						/*	ampSelectQuery = " select amh.Transaction_Timestamp as Transaction_Timestamp, a.Parameter_Value ,amh.Serial_Number ";
				ampFromQuery = " from "+endDateAMDTable+" as a, ( "
						+ "select Serial_Number,Transaction_Timestamp,b.Transaction_Number as Transaction_Number "
						+ " from "+endDateAMHTable+" as b "
						+ " where b.Segment_ID_TxnDate = "+seg_ID+" and b.Serial_Number = '" + Serial_Number + "'"
						+ " and b.Transaction_Timestamp >= '" + endtxnTimestamp 
						+ "') amh";
				ampWhereQuery = " where a.Segment_ID_TxnDate = "+seg_ID+" and a.Transaction_Number = amh.Transaction_Number and " +
						"a.Parameter_ID in (" +parameterListString+")";
				ampOrderByQuery =  " order by a.Parameter_ID";*/

						//ampSelectQuery = " select t.Transaction_Timestamp ,t.TxnData -> '$.ENG_STATUS' as Parameter_Value, t.Serial_Number ";

						ampSelectQuery = " select t.Transaction_Timestamp ,t.TxnData , t.Serial_Number ";
						ampFromQuery = " from "+endTAssetMonTable+" t"
								+ " where t.Segment_ID_TxnDate = "+seg_ID+" and t.Serial_Number = '" + Serial_Number + "'"
								+ " and t.Transaction_Timestamp >= '" + endtxnTimestamp + "' ";



						//	String mainQuery = ampSelectQuery+ampFromQuery+ampWhereQuery+ampOrderByQuery;

						String mainQuery = ampSelectQuery+ampFromQuery;
						iLogger.info(">>>>>mainQuery ::"+mainQuery);
						

						statement = connection.createStatement();
						rs = statement.executeQuery(mainQuery);


						int i =1;
						while(rs.next()){
							AssetMonitoringParametersDAO daoObject = new AssetMonitoringParametersDAO();
							daoObject.setTransactionTS(rs.getTimestamp("Transaction_Timestamp"));

							txnData=rs.getObject("TxnData").toString();

							if(txnData!=null)
								txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());

							if(txnDataMap.get("ENG_STATUS")!=null && ! txnDataMap.get("ENG_STATUS").equalsIgnoreCase("null")){
								engStatus=txnDataMap.get("ENG_STATUS");
							}

							if(txnDataMap.get("EVT_ENG")!=null && ! txnDataMap.get("EVT_ENG").equalsIgnoreCase("null")){
								engStatus=txnDataMap.get("EVT_ENG");
							}

							if(engStatus==null || engStatus.equalsIgnoreCase("null")){
								engStatus="0";
							}
							daoObject.setParameterValue(engStatus);

							//daoObject.setParameterValue(rs.getString("Parameter_Value"));
							daoObject.setSerial_Number(rs.getString("Serial_Number"));
							AMPList.add(daoObject);
							i++;
						}


						/*ampSelectQuery = " select amh.Transaction_Timestamp as Transaction_Timestamp, a.Parameter_Value ,amh.Serial_Number ";
				ampFromQuery = " from "+startDateAMDTable+" as a, ( "
						+ "select Serial_Number,Transaction_Timestamp,b.Transaction_Number as Transaction_Number "
						+ " from "+startDateAMHTable+" as b "
						+ " where b.Segment_ID_TxnDate = "+seg_ID+" and b.Serial_Number = '" + Serial_Number + "'"
						+ " and b.Transaction_Timestamp <= '" + starttxnTimestamp 
						+ "') amh";
				ampWhereQuery = " where a.Segment_ID_TxnDate = "+seg_ID+" and a.Transaction_Number = amh.Transaction_Number and " +
						"a.Parameter_ID in (" +parameterListString+")";
				ampOrderByQuery =  " order by a.Parameter_ID";

				mainQuery = ampSelectQuery+ampFromQuery+ampWhereQuery+ampOrderByQuery;*/

						ampSelectQuery = " select t.Transaction_Timestamp, t.TxnData , t.Serial_Number ";
						ampFromQuery = " from "+startTAssetMonTable+" t "
								+ " where t.Segment_ID_TxnDate = "+seg_ID+" and t.Serial_Number = '" + Serial_Number + "'"
								+ " and t.Transaction_Timestamp <= '" + starttxnTimestamp + "' ";

						mainQuery = ampSelectQuery+ampFromQuery;
						iLogger.info(">>>>>mainQuery ::"+mainQuery);

						statement = connection.createStatement();
						rs = statement.executeQuery(mainQuery);
						//System.out.println(mainQuery);
						//Iterator itr = ampQuery.list().iterator();

						//Object[] result = null;

						while(rs.next()){
							//result = (Object[])itr.next();

							AssetMonitoringParametersDAO daoObject = new AssetMonitoringParametersDAO();
							daoObject.setTransactionTS(rs.getTimestamp("Transaction_Timestamp"));

							txnData=rs.getObject("TxnData").toString();

							if(txnData!=null)
								txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());

							if(txnDataMap.get("ENG_STATUS")!=null && ! txnDataMap.get("ENG_STATUS").equalsIgnoreCase("null")){
								engStatus=txnDataMap.get("ENG_STATUS");
							}

							if(txnDataMap.get("EVT_ENG")!=null && ! txnDataMap.get("EVT_ENG").equalsIgnoreCase("null")){
								engStatus=txnDataMap.get("EVT_ENG");
							}

							if(engStatus==null || engStatus.equalsIgnoreCase("null")){
								engStatus="0";
							}
							daoObject.setParameterValue(engStatus);
							//daoObject.setParameterValue(rs.getString("Parameter_Value"));
							daoObject.setSerial_Number(rs.getString("Serial_Number"));
							AMPList.add(daoObject);
							i++;
						}

						//System.out.println("number of records : "+i);

					}
					else{
						ampSelectQuery = " select t.Transaction_Timestamp, t.TxnData , t.Serial_Number ";
						ampFromQuery = " from "+startTAssetMonTable+" t "
								+ " where t.Segment_ID_TxnDate = "+seg_ID+" and t.Serial_Number = '" + Serial_Number + "'"
								+ " and t.Transaction_Timestamp between '" + endtxnTimestamp 
								+ "' and '"+starttxnTimestamp+"' ";

						/*ampWhereQuery = " where a.Segment_ID_TxnDate = "+seg_ID+" and a.Transaction_Number = amh.Transaction_Number and " +
					"a.Parameter_ID in (" +parameterListString+")";
			ampOrderByQuery =  " order by a.Parameter_ID";


			String mainQuery = ampSelectQuery+ampFromQuery+ampWhereQuery+ampOrderByQuery;*/

						String mainQuery = ampSelectQuery+ampFromQuery;
						iLogger.info(">>>>>mainQuery ::"+mainQuery);
						//System.out.println("else part:"+mainQuery);
						//System.out.println(mainQuery);

						//connection = connFactory.getDatalakeConnection_3309();
						statement = connection.createStatement();
						rs = statement.executeQuery(mainQuery);

						//Iterator itr = ampQuery.list().iterator();

						//Object[] result = null;

						while(rs.next()){
							//result = (Object[])itr.next();

							AssetMonitoringParametersDAO daoObject = new AssetMonitoringParametersDAO();

							daoObject.setTransactionTS(rs.getTimestamp("Transaction_Timestamp"));

							txnData=rs.getObject("TxnData").toString();

							if(txnData!=null)
								txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());

							if(period.equalsIgnoreCase("Today")){

								if(txnDataMap.get("ENG_STATUS")!=null && ! txnDataMap.get("ENG_STATUS").equalsIgnoreCase("null")){
									evtengstatus_ams=txnDataMap.get("ENG_STATUS");
								}

								if(txnDataMap.get("EVT_ENG")!=null && ! txnDataMap.get("EVT_ENG").equalsIgnoreCase("null")){
									evtengstatus_ams=txnDataMap.get("EVT_ENG");
								}


								daoObject.setParameterValue(evtengstatus_ams);

							}
							else{

								if(txnDataMap.get("ENG_STATUS")!=null && ! txnDataMap.get("ENG_STATUS").equalsIgnoreCase("null")){
									engStatus=txnDataMap.get("ENG_STATUS");
								}

								if(txnDataMap.get("EVT_ENG")!=null && ! txnDataMap.get("EVT_ENG").equalsIgnoreCase("null")){
									engStatus=txnDataMap.get("EVT_ENG");
								}
								if(engStatus==null || engStatus.equalsIgnoreCase("null")){
									engStatus="0";
								}


								daoObject.setParameterValue(engStatus);
							}
							//daoObject.setParameterValue(rs.getString("Parameter_Value"));
							daoObject.setSerial_Number(rs.getString("Serial_Number"));
							AMPList.add(daoObject);
						}
					}
					endTime = System.currentTimeMillis();
					iLogger.info("AMH_DAL getAMPValuesInTxnRange: for  on "+txnTimestamp+" Query ends in "+(endTime-startTime));	

				}//end of while
			}//end of try 
			catch(Exception ex){
				ex.printStackTrace();
				fLogger.fatal("AMH_DAL getAMPValuesInTxnRange: for  on "+txnTimestamp+" exception "+ex.getMessage());
			}
			finally{
				try {
					if(statement != null && !statement.isClosed()){
						statement.close();

					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getAMPValuesInTxnRange exception "+e.getMessage());
					e.printStackTrace();
				}
				try {
					if(connection!= null && !connection.isClosed()){
						connection.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getAMPValuesInTxnRange exception "+e.getMessage());
					e.printStackTrace();
				}
			}
		}

		//endTime = System.currentTimeMillis();

		iLogger.info("AMH_DAL getAMPValuesInTxnRange: for on "+txnTimestamp+" END");
		return AMPList;

	}


	public List<AssetMonitoringParametersDAO> getMaxTxsInTxnRange(Map asset2SegID,String txnTimestamp,List parametreIDList){


		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		iLogger.info("AMH_DAL getAMPValuesInTxnRange: for START");
		List<AssetMonitoringParametersDAO> AMPList = new LinkedList<AssetMonitoringParametersDAO>();


		String ampSelectQuery = "";
		String ampFromQuery = "";
		String ampWhereQuery = "";
		String ampGroupNyQuery = "";
		String ampOrderByQuery = "";
		/*String amhTable = null;
	String amdTable = null;
	String amdETable = null;*/

		//DF20161222 @Roopa changing AMH and AMD into single TAssetMOnData table and taking parameters from json column TxnData


		String PersistTo_InMemory = null;
		StringBuilder parameterListString = null;

		long startTime = 0;
		long endTime = 0;

		//DateFormat dateStr = null;
		Properties prop = null;
		//Session session = HibernateUtil.getSessionFactory().openSession();

		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		/*String startDateAMHTable = null;
	String endDateAMHTable = null;
	String startDateAMDTable = null;
	String endDateAMDTable = null;*/

		String startDatetAssetMonTable=null;
		String endDatetAssetMonTable=null;
		String prevDate1 = null;

		try{
			prop = CommonUtil.getDepEnvProperties();

			/*amhTable = prop.getProperty("default_AMH_Table");
		amdTable = prop.getProperty("default_AMD_Table");
		amdETable = prop.getProperty("default_AMDE_Table");*/

			//property which tells whether to persist or retrieve data in InMemory database like redis or not 
			//if we persist or retrieve data to or from inMemory DB no need to Query on legacy DB like MySql 
			PersistTo_InMemory = prop.getProperty("PersistTo_InMemory");
		}catch(Exception e){
			fLogger.fatal("AMH_DAL getAMPValuesInTxnRange: for  exception while retrieving properties file"+e.getMessage());
		}


		if(Boolean.parseBoolean(PersistTo_InMemory)){
			//System.out.println("Persist to In Memory storage");
		}
		else{

			//use Query Param to determine which query to execute   
			/*Iterator it = asset2SegID.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Entry) it.next();
			String Serial_Number = (String) entry.getKey();
			int seg_ID = (Integer) entry.getValue();*/


			try{



				Calendar calendar1 = Calendar.getInstance();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//dateStr.setLenient(false);
				String currDate =txnTimestamp;
				String strCurrDate = currDate;

				Date date3 = dateFormat.parse(strCurrDate);
				Timestamp currTimestamp=new Timestamp(date3.getTime());
				//				Date date4 = dateFormat1.parse(currDate);
				calendar1.setTime(date3);
				// get previous day
				calendar1.add(Calendar.DAY_OF_YEAR, -1);

				String prevDate = dateFormat.format(calendar1.getTime());
				// data will be take from previous day 6:30 to today 6:20
				prevDate = prevDate + " 18:30:00";
				strCurrDate = strCurrDate + " 18:20:00";



				//System.out.println(prevDate+" currnetDate "+strCurrDate);
				Calendar cal = Calendar.getInstance();
				// Date st = dateStr.parse(strCurrDate);
				//System.out.println(st);
				cal.setTime(dateStr.parse(strCurrDate));

				Timestamp endtxnTimestamp = new Timestamp(cal.getTimeInMillis());
				/* HashMap<String, String> dynamicTables = new DateUtil().getCurrentWeekDifference(starttxnTimestamp);
			 if(dynamicTables!=null){
				 startDateAMHTable = dynamicTables.get("AMH");
					//String endDateAMHTable = null;
					startDateAMDTable = dynamicTables.get("AMD");
					//System.out.println(dynamicTables.get("AMH")); 
					//String endDateAMDTable = null;
			 }*/

				//DF20161222 @Roopa changing AMH and AMD into single TAssetMOnData table and taking parameters from json column TxnData
				
				cal.setTime(dateStr.parse(prevDate));
				
				Timestamp starttxnTimestamp = new Timestamp(cal.getTimeInMillis());
				
				

				startDatetAssetMonTable=new DateUtil().getDynamicTable("Fuel Utilization Service", starttxnTimestamp);


				//Calendar cal = Calendar.getInstance();
				/*cal.setTime(dateStr.parse(prevDate));

				Timestamp endtxnTimestamp = new Timestamp(cal.getTimeInMillis());*/

				/* HashMap<String, String> dynamicTables2 = new DateUtil().getCurrentWeekDifference(endtxnTimestamp);
			 if(dynamicTables2!=null){
				 endDateAMHTable = dynamicTables2.get("AMH");
					//String endDateAMHTable = null;
					endDateAMDTable = dynamicTables2.get("AMD");;
					//String endDateAMDTable = null;
					 //System.out.println(dynamicTables2.get("AMH")); 
			 }*/

				endDatetAssetMonTable=new DateUtil().getDynamicTable("Fuel Utilization Service", endtxnTimestamp);


				parameterListString = new ListToStringConversion().getIntegerListString(parametreIDList);

				startTime = System.currentTimeMillis();
				iLogger.info("AMH_DAL getAMPValuesInTxnRange Query starts ");
				connection = connFactory.getDatalakeConnection_3309();
				//special condition when the current date is the first date of the week sunday 
				//then previous date will be found in last week dynamic amh n amd table while curent date in current week amh and amd tables

				Iterator it = asset2SegID.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry entry = (Map.Entry) it.next();
					String Serial_Number = (String) entry.getKey();
					int seg_ID = (Integer) entry.getValue();

					if(!startDatetAssetMonTable.equals(endDatetAssetMonTable)){



						/*String mainQuery = "SELECT MAX(Transaction_timestamp) as Transaction_Timestamp "+
							"FROM "+endDateAMHTable +" where "+
							"Segment_ID_TxnDate = "+seg_ID+" and Serial_Number = '" + Serial_Number + "'"+
							" AND Transaction_timestamp >= '" + endtxnTimestamp +
							" AND record_Type_Id=3"+
							" GROUP BY HOUR(convert_tz(Transaction_timestamp,'+00:00','+05:30'))"+ 
		    				" ORDER BY Transaction_timestamp";*/
//DF20210706 Avinash Xavier Fuel utilization chart bug ,to include the event LFL alert in the query 
						String mainQuery = "SELECT MAX(Transaction_timestamp) as Transaction_Timestamp "+
								"FROM "+endDatetAssetMonTable +" where "+
								"Segment_ID_TxnDate = "+seg_ID+" and Serial_Number = '" + Serial_Number + "'"+
								" AND Transaction_timestamp <= '" + endtxnTimestamp +
								"' AND (Message_ID -> '$.LOG'='1' or  Events -> '$.EVT_LFL'='1' or  Events -> '$.EVT_LFL'='0') "+
								" GROUP BY HOUR(convert_tz(Transaction_timestamp,'+00:00','+05:30'))"+ 
								" ORDER BY Transaction_timestamp";

						//System.out.println(mainQuery);

						statement = connection.createStatement();
						rs = statement.executeQuery(mainQuery);

						//Iterator itr = ampQuery.list().iterator();

						//Object[] result = null;
						int i =1;
						while(rs.next()){
							//result = (Object[])itr.next();

							AssetMonitoringParametersDAO daoObject = new AssetMonitoringParametersDAO();
							daoObject.setTransactionTS(rs.getTimestamp("Transaction_Timestamp"));
							System.out.println(rs.getTimestamp("Transaction_Timestamp"));
							AMPList.add(daoObject);
							i++;
						}



						/* mainQuery = "SELECT MAX(Transaction_timestamp) as Transaction_Timestamp "+
					"FROM "+startDateAMHTable +" where "+
					"Segment_ID_TxnDate = "+seg_ID+" and Serial_Number = '" + Serial_Number + "'"+
					" AND Transaction_timestamp >= '" + starttxnTimestamp +
					" AND record_Type_Id=3"+
					" GROUP BY HOUR(convert_tz(Transaction_timestamp,'+00:00','+05:30'))"+ 
    				" ORDER BY Transaction_timestamp";*/

						mainQuery = "SELECT MAX(Transaction_timestamp) as Transaction_Timestamp "+
								"FROM "+startDatetAssetMonTable +" where "+
								"Segment_ID_TxnDate = "+seg_ID+" and Serial_Number = '" + Serial_Number + "'"+
								" AND Transaction_timestamp >= '" + starttxnTimestamp +
								"' AND (Message_ID -> '$.LOG'='1' or  Events -> '$.EVT_LFL'='1' or  Events -> '$.EVT_LFL'='0') "+
								" GROUP BY HOUR(convert_tz(Transaction_timestamp,'+00:00','+05:30'))"+ 
								" ORDER BY Transaction_timestamp";

						statement = connection.createStatement();
						rs = statement.executeQuery(mainQuery);
						//System.out.println(mainQuery);
						//Iterator itr = ampQuery.list().iterator();

						//Object[] result = null;

						while(rs.next()){
							//result = (Object[])itr.next();

							AssetMonitoringParametersDAO daoObject = new AssetMonitoringParametersDAO();
							daoObject.setTransactionTS(rs.getTimestamp("Transaction_Timestamp"));
							//System.out.println(rs.getTimestamp("Transaction_Timestamp"));
							AMPList.add(daoObject);
							i++;
						}

						//System.out.println("number of records : "+i);

					}
					else{


						/*String mainQuery = "SELECT MAX(Transaction_timestamp) as Transaction_Timestamp "+
					"FROM "+startDateAMHTable +" where "+
					"Segment_ID_TxnDate = "+seg_ID+" and Serial_Number = '" + Serial_Number + "'"+
					" AND Transaction_timestamp between '" + endtxnTimestamp +
					"' and '"+starttxnTimestamp+"'"+
					" AND record_Type_Id=3"+
					" GROUP BY HOUR(convert_tz(Transaction_timestamp,'+00:00','+05:30'))"+ 
    				"ORDER BY Transaction_timestamp";*/

						String mainQuery = "SELECT MAX(Transaction_timestamp) as Transaction_Timestamp "+
								"FROM "+startDatetAssetMonTable +" where "+
								"Segment_ID_TxnDate = "+seg_ID+" and Serial_Number = '" + Serial_Number + "'"+
								" AND Transaction_timestamp between '"+starttxnTimestamp+"' and '" + endtxnTimestamp +
								"' AND (Message_ID -> '$.LOG'='1' or  Events -> '$.EVT_LFL'='1' or  Events -> '$.EVT_LFL'='0') "+
								" GROUP BY HOUR(convert_tz(Transaction_timestamp,'+00:00','+05:30'))"+ 
								"ORDER BY Transaction_timestamp";

						//System.out.println(mainQuery);
						//System.out.println("else part:"+mainQuery);
						//System.out.println(mainQuery);

						//connection = connFactory.getDatalakeConnection_3309();
						statement = connection.createStatement();
						rs = statement.executeQuery(mainQuery);

						//Iterator itr = ampQuery.list().iterator();

						//Object[] result = null;

						while(rs.next()){
							//result = (Object[])itr.next();

							AssetMonitoringParametersDAO daoObject = new AssetMonitoringParametersDAO();

							//AssetMonitoringParametersDAO daoObject = new AssetMonitoringParametersDAO();
							daoObject.setTransactionTS(rs.getTimestamp("Transaction_Timestamp"));
							//System.out.println(rs.getTimestamp("Transaction_Timestamp"));
							AMPList.add(daoObject);
						}
					}
					endTime = System.currentTimeMillis();
					iLogger.info("AMH_DAL getAMPValuesInTxnRange: for  on "+txnTimestamp+" Query ends in "+(endTime-startTime));	

				}//end of while
			}//end of try 
			catch(Exception ex){
				ex.printStackTrace();
				fLogger.fatal("AMH_DAL getAMPValuesInTxnRange: for  on "+txnTimestamp+" exception "+ex.getMessage());
			}
			finally{
				try {
					if(statement != null && !statement.isClosed()){
						statement.close();

					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getAMPValuesInTxnRange exception "+e.getMessage());
					e.printStackTrace();
				}
				try {
					if(connection!= null && !connection.isClosed()){
						connection.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getAMPValuesInTxnRange exception "+e.getMessage());
					e.printStackTrace();
				}
			}
		}

		//endTime = System.currentTimeMillis();

		iLogger.info("AMH_DAL getAMPValuesInTxnRange: for on "+txnTimestamp+" END");
		return AMPList;

	}



	public List<AssetMonitoringParametersDAO> getAMhDataOnTxnTS(Map asset2SegID,
			List<String> TxnTSList,List parameterList) {
		// TODO Auto-generated method stub
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		iLogger.info("AMH_DAL getAMhDataOnTxnTS: for and SegmentID START");
		/*String amhTable = null;
	String amdTable = null;
	String amdETable = null;*/
		String tAssetMonTable=null;

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
		List<AssetMonitoringParametersDAO> amhDaoList = new LinkedList<AssetMonitoringParametersDAO>();
		HashMap<String, List<String>> tAssetMonTableData = new HashMap<String, List<String>>(); 

		try {
			dateStr = new SimpleDateFormat("yyyy-MM-dd");
			prop = CommonUtil.getDepEnvProperties();

			prop = CommonUtil.getDepEnvProperties();
			/*
			 * amhTable = prop.getProperty("default_AMH_Table"); amdTable =
			 * prop.getProperty("default_AMD_Table"); amdETable =
			 * prop.getProperty("default_AMDE_Table");
			 */
			tAssetMonTable = prop.getProperty("default_TAssetMonData_Table");

		}catch(IllegalArgumentException iag){
			//System.out.println("AMH_DAL Exception in formatting the Date "+iag.getMessage());
			fLogger.fatal("AMH_DAL getAMhDataOnTxnTS Exception in formatting the Date "+iag.getMessage());
		}catch(Exception e){
			e.printStackTrace();
			fLogger.fatal("AMH_DAL getAMhDataOnTxnTS generic Exceptio:"+e.getMessage());
		}

		String PersistTo_InMemory = prop.getProperty("PersistTo_InMemory");

		iLogger.info("AMH_DAL getAMhDataOnTxnTS PersistTo_InMemory"+PersistTo_InMemory);

		if(Boolean.parseBoolean(PersistTo_InMemory)){
			//System.out.println("Persist to In Memory storage");
		}
		else{
			Iterator it = asset2SegID.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry entry = (Map.Entry) it.next();
				String Serial_Number = (String) entry.getKey();
				int seg_ID = (Integer) entry.getValue();
				String TxnTS = (String)TxnTSList.get(TxnTSList.size()-1);
				//DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd");
				//dateStr.setLenient(false);
				
				DateUtil dateUtilObj = new DateUtil();
				String tAssetTable = null;
				List<String> txnTimeList = null;
				for(int i = 0;i<TxnTSList.size();i++){
					String txn = (String)TxnTSList.get(i);
					try {
						Date dt = dateStr.parse(txn);
						tAssetTable = dateUtilObj.getDynamicTable("FuelUtilization Service", dt);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if(tAssetMonTableData.containsKey(tAssetTable))
						txnTimeList = tAssetMonTableData.get(tAssetTable);
					else
						txnTimeList = new LinkedList<String>();
					txnTimeList.add(TxnTSList.get(i));
					tAssetMonTableData.put(tAssetTable, txnTimeList);
				}

				/*Date date3=null;
				try {
					date3 = dateStr.parse(TxnTS);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					HashMap<String, String> dynamicTables = new DateUtil().getCurrentWeekDifference(date3);
		 if(dynamicTables!=null){
			 amhTable = dynamicTables.get("AMH");
				//String endDateAMHTable = null;
				amdTable = dynamicTables.get("AMD");
				//System.out.println(dynamicTables.get("AMH")); 
				//String endDateAMDTable = null;
		 }
				tAssetMonTable=new DateUtil().getDynamicTable("FuelUtilization Service", date3);

				startTime = System.currentTimeMillis();
				iLogger.info("AMH_DAL getAMhDataOnTxnTS Query starts ");

				String TxnTSListString = new ListToStringConversion().getStringList(TxnTSList).toString();
				//String parameterListString = new ListToStringConversion().getIntegerListString(parameterList).toString();

				//DF20161222 @Roopa changing AMH and AMD into single TAssetMOnData table and taking parameters from json column TxnData

				amhSelectQuery = "select a.Transaction_Timestamp,b.Parameter_Value " ;
		amhFromQuery = " from "+amhTable+" a ,"+amdTable +" b ";
		amhWhereQuery = " where a.Segment_ID_TxnDate = "+seg_ID +" and a.Serial_Number = '"+Serial_Number+"' and " +
				" a.Transaction_Timestamp in  ("+TxnTSListString+") and b.Segment_ID_TxnDate = a.Segment_ID_TxnDate and " +
						"b.Transaction_Number = a.Transaction_Number and b.Parameter_ID in (" +parameterListString+")";

				amhSelectQuery = "select t.Transaction_Timestamp,t.TxnData " ;
				amhFromQuery = " from "+tAssetMonTable+" t ";
				amhWhereQuery = " where t.Segment_ID_TxnDate = "+seg_ID +" and t.Serial_Number = '"+Serial_Number+"' and " +
						" t.Transaction_Timestamp in  ("+TxnTSListString+") ";

				//amhGroupNyQuery = "group by a.Serial_Number ,Date(a.Transaction_Timestamp) ";
				amhOrderByQuery = " order by t.Transaction_Timestamp";

				String mainQuery = amhSelectQuery+amhFromQuery+amhWhereQuery+amhGroupNyQuery+amhOrderByQuery;
				iLogger.info("AMH_DAL getAMhDataOnCreatedTS mainQuery "+mainQuery);
				//System.out.println("AMH_DAL getAMhDataOnCreatedTS mainQuery "+mainQuery);

				try {
					connection = connFactory.getDatalakeConnection_3309();
					statement = connection.createStatement();
					//System.out.println(mainQuery);
					rs = statement.executeQuery(mainQuery);

					HashMap<String,String> txnDataMap=new HashMap<String, String>();
					String txnData;

					while(rs.next()){
						AssetMonitoringParametersDAO daoObject = new AssetMonitoringParametersDAO();

						txnData=rs.getObject("TxnData").toString();

						txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());


						//daoObject.setSerial_Number(rs.getString("Serial_Number"));
						daoObject.setTransactionTS(rs.getTimestamp("Transaction_Timestamp"));
						daoObject.setParameterValue(txnDataMap.get("FUEL_PERCT"));
						amhDaoList.add(daoObject);

					}


				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				// DF20180629 - KO369761 - Fetching data from TAssetMon table
				// logic was changed. So that it can fetch from multiple tables
				// for multiple dates
				try{
					iLogger.info("AMH_DAL getAMhDataOnTxnTS Query starts ");
					
					connection = connFactory.getDatalakeConnection_3309();
					statement = connection.createStatement();
					
					for(String table:tAssetMonTableData.keySet()){
						tAssetMonTable = table;
						txnTimeList = tAssetMonTableData.get(tAssetMonTable);
						String TxnTSListString = new ListToStringConversion().getStringList(txnTimeList).toString();

						amhSelectQuery = "select t.Transaction_Timestamp,t.TxnData " ;
						amhFromQuery = " from "+tAssetMonTable+" t ";
						amhWhereQuery = " where t.Segment_ID_TxnDate = "+seg_ID +" and t.Serial_Number = '"+Serial_Number+"' and " +
								" t.Transaction_Timestamp in  ("+TxnTSListString+") ";
						amhOrderByQuery = " order by t.Transaction_Timestamp";

						String mainQuery = amhSelectQuery+amhFromQuery+amhWhereQuery+amhGroupNyQuery+amhOrderByQuery;
						iLogger.info("AMH_DAL getAMhDataOnCreatedTS mainQuery "+mainQuery);
						
						rs = statement.executeQuery(mainQuery);
						
						HashMap<String,String> txnDataMap=new HashMap<String, String>();
						String txnData;

						while(rs.next()){
							txnData=rs.getObject("TxnData").toString();
							txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());
							//DF06022020: Ramu B: excluding if machine's fuel percentage is more than 100
							if(Integer.parseInt(txnDataMap.get("FUEL_PERCT")) <=100)
							{
							AssetMonitoringParametersDAO daoObject = new AssetMonitoringParametersDAO();
							//daoObject.setSerial_Number(rs.getString("Serial_Number"));
							daoObject.setTransactionTS(rs.getTimestamp("Transaction_Timestamp"));
							daoObject.setParameterValue(txnDataMap.get("FUEL_PERCT"));
							amhDaoList.add(daoObject);
							}
						}
					}
					
					
				}catch (Exception e) {
					// TODO: handle exception
				}
				
				finally{
					try {
						if(statement != null && !statement.isClosed()){
							statement.close();

						}

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						fLogger.fatal("getAMhDataOnTxnTS exception "+e.getMessage());
						e.printStackTrace();
					}
					try {
						if(connection!= null && !connection.isClosed()){
							connection.close();
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						fLogger.fatal("getAMhDataOnTxnTS exception "+e.getMessage());
						e.printStackTrace();
					}
				}

			}	
		}

		return amhDaoList;
	}

	public AssetMonitoringParametersDAO getLocationONTxn(String Serial_Number,Timestamp txnTimestamp, int seg_ID,List parametreIDList){

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<AssetMonitoringParametersDAO> AMPList = new LinkedList<AssetMonitoringParametersDAO>();

		iLogger.info("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" START");

		String amhTable = null;
		String amdTable = null;
		String amdETable = null;
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
		AssetMonitoringParametersDAO daoObject = null;
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		try{


			prop = CommonUtil.getDepEnvProperties();
			amhTable = prop.getProperty("default_AMH_Table");
			amdTable = prop.getProperty("default_AMD_Table");
			amdETable = prop.getProperty("default_AMDE_Table");

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
					HashMap<String, String> dynamicTables = new DateUtil().getCurrentWeekDifference(txnTimestamp);

					amhTable = dynamicTables.get("AMH");
					amdTable = dynamicTables.get("AMD");
					amdETable = dynamicTables.get("AMDE");
				}
				if(amhTable == null || amdTable == null )
					return daoObject;
				parameterListString = new ListToStringConversion().getIntegerListString(parametreIDList);

				startTime = System.currentTimeMillis();
				iLogger.info("AMH_DAL getLatestEngineONTxn Query starts ");

				ampSelectQuery = " select cast(group_concat(a.parameter_id) as char) as ParameterNames,cast(group_concat(a.parameter_value) as char) as ParameterValues ";
				ampFromQuery = " from "+amdTable+" as a,"+amhTable+" as b ";

				ampWhereQuery = " where b.Segment_ID_TxnDate = "+seg_ID+" and b.Serial_Number = '" + Serial_Number + "'"
						+ " and b.Transaction_Timestamp = '" + txnTimestamp +"' and a.Segment_ID_TxnDate = "+seg_ID+" and a.Transaction_Number = b.Transaction_Number and " +
						"a.Parameter_ID in (" +parameterListString+") ";
				//ampOrderByQuery =  " order by b.Transaction_Timestamp desc ";

				String mainQuery =ampSelectQuery+ampFromQuery+ampWhereQuery;

				iLogger.info("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" main Query: "+mainQuery);

				//System.out.println("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" main Query: "+mainQuery);

				connection = connFactory.getConnection_percona();
				statement = connection.createStatement();
				rs = statement.executeQuery(mainQuery);

				//Iterator itr = ampQuery.list().iterator();

				//	Object[] result = null;
				HashMap parameterNames = new HashMap();
				parameterNames.put("1", "Latitude");
				parameterNames.put("2","Longitude");
				while(rs.next()){
					//	result = (Object[])itr.next();
					//String parameterName = "";
					daoObject = new AssetMonitoringParametersDAO();
					//TxnTime = rs.getTimestamp("Transaction_Timestamp");

					String parameterID = rs.getString("ParameterNames");
					String parameterValues = rs.getString("ParameterValues");
					if(parameterID == null)
						continue;
					String[] p_ids = parameterID.split(",");
					String[] p_values = parameterValues.split(",");

					parameterID = (String)parameterNames.get(p_ids[0])+","+(String)parameterNames.get(p_ids[1]);
					parameterValues = p_values[0]+","+p_values[1];
					daoObject.setParameterName(parameterID);
					daoObject.setParameterValue(parameterValues);
				}
				endTime = System.currentTimeMillis();
				iLogger.info("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" Query ends in "+(endTime-startTime));
			}catch(Exception ex){
				ex.printStackTrace();
				fLogger.fatal("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" exception "+ex.getMessage());
			}
			finally{
				try {
					if(!statement.isClosed()){
						statement.close();

					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					if(!connection.isClosed()){
						connection.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		iLogger.info("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+seg_ID+" on "+txnTimestamp+" END");

		return daoObject;

	}


	public List<DownloadAempImpl> getAMHAMDListForDownloadAemp(String query) {

		List<DownloadAempImpl> downloadAempImplList=new LinkedList<DownloadAempImpl>();

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;


		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String AMHAMDSelectQuery=query;

		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			fLogger.fatal(":AMH:DAL-AMH-getAMHAMDListForDownloadAemp"+"Error in intializing property File :"+e.getMessage());
		}



		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info(":AMH:DAL-AMH-getAMHAMDListForDownloadAemp"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{

			/*<!=========START fetching from Native Database==============>*/


			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getDatalakeConnection_3309();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(AMHAMDSelectQuery);

				DownloadAempImpl downloadAempImplObj;

				HashMap<String,String> txnDataMap=new HashMap<String, String>();
				String txnData;
				while(rs.next()){
					downloadAempImplObj =new DownloadAempImpl();

					downloadAempImplObj.setPIN(rs.getString("Serial_Number"));

					txnData=rs.getObject("TxnData").toString();

					txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());
					downloadAempImplObj.setTxnDataMap(txnDataMap);

					/*downloadAempImplObj.setTransaction_number(rs.getInt("Transaction_Number"));
				downloadAempImplObj.setParam_ID(rs.getInt("Parameter_ID"));
				downloadAempImplObj.setParam_value(rs.getString("Parameter_Value"));*/
					downloadAempImplList.add(downloadAempImplObj);

				}
			}
			catch (SQLException e) {

				e.printStackTrace();
				fLogger.fatal(":AMH:DAL-AMH-getAMHAMDListForDownloadAemp"+"SQL Exception in fetching data from mysql::"+e.getMessage());
			} 

			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal(":AMH:DAL-AMH-getAMHAMDListForDownloadAemp"+"Exception in fetching data from mysql::"+e.getMessage());
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

		return downloadAempImplList;
	}



	public List<PricolTransactionDetailImpl> getAMHAMDListForPricolTransaction(
			String query) {

		List<PricolTransactionDetailImpl> responseList=new LinkedList<PricolTransactionDetailImpl>();

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;


		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String AMHAMDSelectQuery=query;

		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			fLogger.fatal(":AMH:DAL-AMH-getAMHAMDListForPricolTransaction"+"Error in intializing property File :"+e.getMessage());
		}



		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info(":AMH:DAL-AMH-getAMHAMDListForPricolTransaction"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{

			/*<!=========START fetching from Native Database==============>*/


			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getDatalakeConnection_3309();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(AMHAMDSelectQuery);

				PricolTransactionDetailImpl implObj;
				HashMap<String,String> txnDataMap=new HashMap<String, String>();
				String txnData;


				while(rs.next()){


					/*	int tranNum = rs.getInt("Transaction_Number");


        		if(tranNum!=transactionNum)
        		{

        			if(implObj!=null)
        			{
        				responseList.add(implObj);
        			}

        			implObj = new PricolTransactionDetailImpl();
        			implObj.setSerialNumber(rs.getString("Serial_Number"));

        			implObj.setTransactionTimeStamp(String.valueOf(rs.getTimestamp("Transaction_Timestamp")));


        			transactionDataMap.put(String.valueOf(rs.getInt("Parameter_ID")), rs.getString("Parameter_Value"));

        			implObj.setTransactionData(transactionDataMap);

        			transactionNum = tranNum;
        		}

        		else
        		{

        			transactionDataMap.put(String.valueOf(rs.getInt("Parameter_ID")), rs.getString("Parameter_Value"));

        			implObj.setTransactionData(transactionDataMap);
        		}*/






					/*paramIdList = new LinkedList<String>();
			    paramValueList = new LinkedList<String>();*/


					/*	String paramId= rs.getString("Parameter_ID");
				String paramValue= rs.getString("Parameter_Value");

				if(paramId!=null)
					paramIdList = Arrays.asList(paramId.split(","));
					else
						paramIdList=new LinkedList<String>();

					if(paramValue!=null)
						paramValueList = Arrays.asList(paramValue.split(","));
						else
							paramValueList=new LinkedList<String>();

					for(int i=0;i<paramIdList.size();i++){
						transactionDataMap.put(paramIdList.get(i), paramValueList.get(i));	
					}

					 */

					//DF20161222 @Roopa changing AMH and AMD into single TAssetMOnData table and taking parameters from json column TxnData

					txnData=rs.getObject("TxnData").toString();

					txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());

					implObj =new PricolTransactionDetailImpl();
					implObj.setSerialNumber(rs.getString("Serial_Number"));
					implObj.setTransactionTimeStamp(String.valueOf(rs.getTimestamp("Transaction_Timestamp")));
					implObj.setTransactionData(txnDataMap);

					responseList.add(implObj);


				}

				/*if(implObj != null)
        		responseList.add(implObj);*/
			}
			catch (SQLException e) {

				e.printStackTrace();
				fLogger.fatal(":AMH:DAL-AMH-getAMHAMDListForPricolTransaction"+"SQL Exception in fetching data from mysql::"+e.getMessage());
			} 

			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal(":AMH:DAL-AMH-getAMHAMDListForPricolTransaction"+"Exception in fetching data from mysql::"+e.getMessage());
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

		return responseList;
	}	
}
