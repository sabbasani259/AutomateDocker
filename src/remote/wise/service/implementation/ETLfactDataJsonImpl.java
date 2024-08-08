/**
 * 
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessobject.ETLfactDataBO;
import remote.wise.businessobject.ETLfactDataJsonBO;
import remote.wise.dal.DynamicRemoteFactDay_DAL;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;

/**
 * @author roopn5
 *
 */
public class ETLfactDataJsonImpl {
	private static Timestamp maxOlapDt = getMaxOlapDate();

	private static List<String> aggregateParameters = getAggregateParamters();
	private static List<String> accumulatedParameters = getAccumulatedParameters();
	private static Map common_parameters_map = getCommonMonitoringParameters();

	/**
	 *  This method will set to insert data in RemoteMonitoringFactDataDayAgg table for a current day.
	 * @return SUCCESS
	 */
	public String setETLfactJsonDetails() throws CustomFault
	{

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		long startTime = 0l;
		long endTime = 0l;
		SimpleDateFormat simpleDtStr = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss");
		ETLfactDataBO etlFactDataBO=new ETLfactDataBO();

		ExecutorService executors = null;
		Future<String> taskResult = null;
		CompletionService<String> taskCompletionService = null;

		int maxNoOfThreads = 0;
		try {
			Callable<String> callable = null;

			Session session = HibernateUtil.getSessionFactory().openSession();

			//try {
			iLogger.info("ETLfactDataJsonImpl: setETL start: "
					+ simpleDtStr.format(Calendar.getInstance().getTime()));



			startTime = System.currentTimeMillis();
			//STEP 1 geting the maxOlap Date from the Day Aggregate Table

			iLogger.info("ETLfactDataJsonImpl: setETL STEP 1");

			//STEP 2 : getting max segmentID from asset table ,NO. of Threads = No. of Segments 
			Query maxSegmentQuery = session.createQuery("select max(segmentId) from AssetEntity");  
			Iterator itr = maxSegmentQuery.list().iterator();
			if(itr.hasNext()){

				maxNoOfThreads = (Integer) itr.next();
			}


			if (session!=null && session.isOpen()) {
				session.close();
			}

			if(maxNoOfThreads!=0)
			{
				iLogger.info("ETLfactDataJsonImpl: setETL No. of threads spawn:"+maxNoOfThreads);
				executors = Executors.newFixedThreadPool(maxNoOfThreads);
				taskCompletionService = new ExecutorCompletionService<String>(executors);
			}


			long start = System.currentTimeMillis();
			int segementIDThread = 0;
			Timestamp maxOlapDate = maxOlapDt;

			while(segementIDThread <= maxNoOfThreads ){
				iLogger.info("ETLfactDataJsonImpl: setETL spawning the thread:"+segementIDThread);

				//S Suresh  

				/* passing two extra parameters aggregateParameters,common_parameters_map which got received from the constructor 
				 *  new ETLfactDataBO(segementIDThread,maxOlapDate,aggregateParameters,accumulatedParameters,common_parameters_map  )
				 *  since the Transaction table data population has chenged so as ETLDesign has changed according to the new Transaction table approach
				 *  aggregateParameters - is a Map which has newly addded Aggregated Parameters which can be used in reports 
				 *  accumulatedParameters - is a Map which has newly addded Accumulated Parameters which can be used in reports 
				 *  common_parameters_map - is map which has both common Aggregated and Accumulated parameter list 
				 *  new coloumn has added in the remote__fact_day_agg which is a json datatype for newly added parameters (both aggregated and accumulated parameters)
				 *  so we follow the same old approach for ETL population inaddition we create a json for new parameters and store it in new column along with old paramters 
				 */

				//loading the master data key parameters for both aggregated and accumulated on class loading 
				//which is given after class declaration of ETLfactDataJsonImpl and check coresponding method definitions for clarity  
				//private static List<String> aggregateParameters = getAggregateParamters();
				//private static List<String> accumulatedParameters = getAccumulatedParameters();
				//private static Map common_parameters_map = getCommonMonitoringParameters();

				callable = new ETLfactDataJsonBO(segementIDThread,maxOlapDate,aggregateParameters,accumulatedParameters,common_parameters_map);

				taskCompletionService.submit(callable);

				segementIDThread++;

			}

			for(int i=1;i<=maxNoOfThreads;i++){
				taskResult = taskCompletionService.take();
				if(taskResult.get() != null){
					iLogger.info("Thread "+i+" has succefully completed and returned "+taskResult.get());
				}
			}
			executors.shutdown();
			iLogger.info("ETLfactDataJsonImpl: setETL exiting from the multithread:");
			executors.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

		}

		catch(Exception e){
			e.printStackTrace();
		}

		return "SUCCESS";
	}

	private static Timestamp getMaxOlapDate() {
		// TODO Auto-generated method stub

		if(maxOlapDt == null){
			try{

				DynamicRemoteFactDay_DAL remoteFactDayObj=new DynamicRemoteFactDay_DAL();

				String query="select max(Time_Key) as Time_Key from remote_monitoring_fact_data_dayagg_json_new";


				String Time_Key= remoteFactDayObj.getTimeKey(query);
				if (Time_Key != null && Time_Key.length()!=0)  {
					maxOlapDt=Timestamp.valueOf(Time_Key);
				}
				else{
					maxOlapDt=Timestamp.valueOf("2017-04-25 00:00:01");
				}

			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}


		return maxOlapDt;
	}

	//loading the master data for newly added aggregated paramters  
	private static List<String> getAggregateParamters() {
		// TODO Auto-generated method stub
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String parameter_key = null;
		List<String> parameter_key_list = new LinkedList<String>();
		try {
			connection = connFactory.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery("select Parameter_Key from aggregated_monitoring_parameters");
			while(rs.next()){
				parameter_key = rs.getString("Parameter_Key");
				parameter_key_list.add(parameter_key);
			}
		}catch (SQLException e) {
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
		return parameter_key_list;
	}


	//loading the master data for newly added accumulated paramters  
	private static List<String> getAccumulatedParameters() {
		// TODO Auto-generated method stub
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String parameter_key = null;
		List<String> parameter_key_list = new LinkedList<String>();
		try {
			connection = connFactory.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery("select Parameter_Key from accumulated_monitoring_parameters");
			while(rs.next()){
				parameter_key = rs.getString("Parameter_Key");
				parameter_key_list.add(parameter_key);
			}
		}catch (SQLException e) {
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
		return parameter_key_list;
	}


	//loading the master data for common Monitoring parameters both aggregated and accumulated 
	private static Map getCommonMonitoringParameters() {
		// TODO Auto-generated method stub
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String parameter_key = null;
		String parameter_type = null;
		//List<String> parameter_key_list = new LinkedList<String>();
		Map common_parameters_map = new HashMap();
		try {
			connection = connFactory.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery("select Parameter_Key,Parameter_Type from common_aggregated_parameters");
			List common_aggregated_param_list = new LinkedList();
			List common_accumulated_param_list = new LinkedList();
			while(rs.next()){
				parameter_type = rs.getString("Parameter_Type");
				parameter_key = rs.getString("Parameter_Key");
				if(parameter_type.equalsIgnoreCase("Aggregate"))
					common_aggregated_param_list.add(parameter_key);
				else
					common_accumulated_param_list.add(parameter_key);

				//parameter_key_list.add(parameter_key);
			}
			common_parameters_map.put("Common_Aggregated_Parameters",common_aggregated_param_list );
			common_parameters_map.put("Common_Accumulated_Parameters",common_accumulated_param_list );
		}catch (SQLException e) {
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
		return common_parameters_map;
	}

}
