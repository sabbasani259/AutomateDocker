package remote.wise.service.implementation;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessobject.ETLfactDataBO;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import org.apache.logging.log4j.Logger;
import remote.wise.exception.CustomFault;
import remote.wise.util.HibernateUtil;
/**
 * This Implementation class will allow to insert data in RemoteMonitoringFactDataDayAgg table for a current day.
 * @author jgupta41
 *
 */
public class ETLfactDataImpl {
	//******************************************Start of setETLfactData************************************************************
	/**
	 *  This method will set to insert data in RemoteMonitoringFactDataDayAgg table for a current day.
	 * @return SUCCESS
	 */
private static Timestamp maxOlapDt = getMaxOlapDate();
	
	//******************************************Start of setETLfactData************************************************************
	/**
	 *  This method will set to insert data in RemoteMonitoringFactDataDayAgg table for a current day.
	 * @return SUCCESS
	 */
	public String setETLfactDataOld()throws CustomFault
	{
		//ETLfactDataBO etlFactDataBO=new ETLfactDataBO();
		ETLfactDataBO etlFactDataBO=new ETLfactDataBO();
		//String flag=etlFactDataBO.setETLfactData();				//DF1409.o
		String flag=etlFactDataBO.setETLfactData(); //DF1409.n
		return flag;
	}
	
	/**
	 *  This method will set to insert data in RemoteMonitoringFactDataDayAgg table for a current day.
	 * @return SUCCESS
	 */
	public String setETLfactData() throws CustomFault
	{
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	long startTime = 0l;
		long endTime = 0l;
		SimpleDateFormat simpleDtStr = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss");
		//ETLfactDataBO etlFactDataBO=new ETLfactDataBO();
		ETLfactDataBO etlFactDataBO=new ETLfactDataBO();
		//String flag=etlFactDataBO.setETLfactData();				//DF1409.o
		//String flag=etlFactDataBO.setETLfactData(); //DF1409.n

	ExecutorService executors = null;
	//Calendar cal = Calendar.getInstance();
	Future<String> taskResult = null;
	CompletionService<String> taskCompletionService = null;
	//List<Future<Integer>> futureList = null;
	//Date nxtDate = sdf.parse(startDate);
	int maxNoOfThreads = 0;
	try {
		//iLogger.info("ETLfactDataImpl: setETL START ");
		Callable<String> callable = null;
		
		//Collection threads = new ArrayList();
Session session = HibernateUtil.getSessionFactory().openSession();
		
		//try {
			iLogger.info("ETLfactDataImpl: setETL start: "
					+ simpleDtStr.format(Calendar.getInstance().getTime()));
		
			

			startTime = System.currentTimeMillis();
			//STEP 1 geting the maxOlap Date from the Day Aggregate Table
			
			iLogger.info("ETLfactDataImpl: setETL STEP 1");
		
			//iLogger.info();
			//Date nxtDay = Cal
		//Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		//STEP 2 : getting max segmentID from asset table ,NO. of Threads = No. of Segments 
		Query maxSegmentQuery = session.createQuery("select max(segmentId) from AssetEntity");  
		Iterator itr = maxSegmentQuery.list().iterator();
		if(itr.hasNext()){
			
			maxNoOfThreads = (Integer) itr.next();
			//System.out.println(maxNoOfThreads);
		}
		if(maxNoOfThreads!=0)
		{
			iLogger.info("ETLfactDataImpl: setETL No. of threads spawn:"+maxNoOfThreads);
			executors = Executors.newFixedThreadPool(maxNoOfThreads);
			taskCompletionService = new ExecutorCompletionService<String>(executors);
		}
		

		//ExecutorCompletionService executors = Executors.new;
		// futureList = new LinkedList<Future<Integer>>();
		long start = System.currentTimeMillis();
		int segementIDThread = 0;
		Timestamp maxOlapDate = maxOlapDt;
		
		while(segementIDThread <= maxNoOfThreads ){
			iLogger.info("ETLfactDataImpl: setETL spawning the thread:"+segementIDThread);
			callable = new ETLfactDataBO(segementIDThread,maxOlapDate);
			
			taskCompletionService.submit(callable);
			
			segementIDThread++;
			//iLogger.info(nxtDateString);
		//	period++;
			}
		
		//Thread.sleep(1000);
		

		for(int i=1;i<=maxNoOfThreads;i++){
			taskResult = taskCompletionService.take();
			if(taskResult.get() != null){
				iLogger.info("Thread "+i+" has succefully completed and returned "+taskResult.get());
			}
		}
		executors.shutdown();
		iLogger.info("ETLfactDataImpl: setETL exiting from the multithread:");
		executors.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		
	}
	
	catch(Exception e){
		e.printStackTrace();
	}
	
	return "SUCCESS";
}
	//******************************************End of setETLfactData************************************************************

	//**************************************Start of  updateRemoteFact  *********************************************************

	/**
	 * This method will update AssetClassId in All fact tables
	 * @return SUCCESS
	 * @throws CustomFault
	 */
	public String updateRemoteFact()throws CustomFault
	{
		//ETLfactDataBO etlFactDataBO=new ETLfactDataBO();
		ETLfactDataBO etlFactDataBO=new ETLfactDataBO();
		String flag=etlFactDataBO.updateRemoteFact();
		return flag;
	}
	//******************************************End of updateRemoteFact************************************************************
	//**************************************Start of  updateTenancyIdInFactTables  *********************************************************

	/**
	 * This method will update TenancyId in All fact tables
	 * @return SUCCESS
	 * @throws CustomFault
	 */
	public String updateTenancyIdInFactTables()throws CustomFault
	{
		//ETLfactDataBO etlFactDataBO=new ETLfactDataBO();
		ETLfactDataBO etlFactDataBO=new ETLfactDataBO();
		String flag=etlFactDataBO.updateTenancyIdInFactTables();
		return flag;
	}
	//******************************************End of updateTenancyIdInFactTables************************************************************
	//**************************************Start of  updateAddress  *********************************************************
	/**
	 * This method will update Address in All fact tables
	 * @return SUCCESS
	 * @throws CustomFault
	 */
	public String updateAddress(int count)throws CustomFault
	{
		ETLfactDataBO etlFactDataBO=new ETLfactDataBO();
		String flag=etlFactDataBO.updateAddress(count);
		return flag;
	}
	//******************************************End of updateAddress************************************************************

	private static Timestamp getMaxOlapDate() {
		// TODO Auto-generated method stub
		Session session = HibernateUtil.getSessionFactory().openSession();
		//System.out.println("inside getMaxOlapDate");
		if(maxOlapDt == null){
			try{
				//System.out.println("inside getMaxOlapDate if(maxOlapDt == null)");
			Query qry = session.createQuery("select max(a.timeKey)  " +
					" from AssetMonitoringFactDataDayAgg a");
			List lst = qry.list();
			Iterator itr = lst.iterator();

			if ( (itr.hasNext()) && (lst.size() > 0) && (lst.get(0) != null) ) {
				maxOlapDt = (Timestamp) itr.next(); 
			} else {
				qry = session.createQuery("select min(b.date) from DateDimensionEntity b");
				lst = qry.list();
				itr = lst.iterator();
				if ( (itr.hasNext()) && (lst.size() > 0) && (lst.get(0) != null) ) {
					maxOlapDt = (Timestamp) itr.next();
				} else {
					maxOlapDt = Timestamp.valueOf("2012-01-01 00:00:01");
				}					
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		}
		
		
		return maxOlapDt;
	}
	
}
