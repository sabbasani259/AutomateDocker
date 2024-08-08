/**
 * 
 */
package remote.wise.businessobject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.simple.JSONObject;

import remote.wise.businessentity.AccountDimensionEntity;
import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetClassDimensionEntity;
import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetMonitoringFactDataEntity;
import remote.wise.businessentity.MonitoringParameters;
import remote.wise.businessentity.RpmBands;
import remote.wise.businessentity.TenancyDimensionEntity;
import remote.wise.dal.DynamicRemoteFactDay_DAL;
import remote.wise.dal.DynamicTAMD_DAL;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.AmhDAO;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;

/**
 * @author roopn5
 *
 */
public class ETLfactDataJsonBO implements Callable<String>{
	private SimpleDateFormat simpleDtStr = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm:ss");

	private int clientId = 0;

	/*private int longitudeId = 0;
	private int latitudeId = 0;
	private int fuelLevelId = 0;
	private int engineHoursId = 0;
	private int engineONId = 0;
	private int EngineRunningBand1Id = 0;
	private int EngineRunningBand2Id = 0;
	private int EngineRunningBand3Id = 0;
	private int EngineRunningBand4Id = 0;
	private int EngineRunningBand5Id = 0;
	private int EngineRunningBand6Id = 0;
	private int EngineRunningBand7Id = 0;
	private int EngineRunningBand8Id = 0;*/

	private int accountId = 0;	

	private String NickName = null;

	private DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd");

	private List <Integer> IdleBand = new LinkedList<Integer>();
	private List <Double> PreviousDayBandList = new LinkedList<Double>();

	private AccountEntity account_id = null;
	private TenancyDimensionEntity tenancy_dimension_id = null;
	private AccountDimensionEntity accountDimensionId = null;
	private AssetClassDimensionEntity Asset_Class_Id = null;
	//private static final String HOST = "localhost";
	//private static final int PORT = 6379;

	private int segment_ID;
	private Timestamp maxOlapDt;

	List<String> aggregateParameters;
	List<String> accumulatedParameters;
	Map common_parameters_map;

	int tenancy_dimension_id_json=0;
	int acc_id_json=0;
	int asset_class_id_json=0;

	public ETLfactDataJsonBO(){}

	public ETLfactDataJsonBO(int segment_ID,Timestamp maxOlapDt, List<String> aggregateParameters, List<String> accumulatedParameters, Map common_parameters_map){
		this.segment_ID = segment_ID;
		this.maxOlapDt = maxOlapDt;
		this.aggregateParameters = aggregateParameters;
		this.accumulatedParameters = accumulatedParameters;
		this.common_parameters_map = common_parameters_map;
	}

	@Override
	public String call() throws Exception {
		// TODO Auto-generated method stub
		String result  = setETLfactJsonData();
		return result;
	}

	public String setETLfactJsonData() {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		int count2Process = 0;
		int countProcessed = 0;
		long startTime = 0l;
		long endTime = 0l;

		String vinProcessed = "";
		String vin2Process = "";

		Timestamp lastOlapDt = null;
		Timestamp prevlastOlapDt = null;
		Timestamp tranTime = null;
		Object[] result = null;

		String vinIdentified = null;

		Session session = null;
		
		
		
		

		try {
			iLogger.info("ETLfactDataJsonBO: setETL start: "
					+ simpleDtStr.format(Calendar.getInstance().getTime()));
			
			//DF20170421 @Roopa eliminating the scenario of taking the next day records when ETL starts running
			Calendar cal  = Calendar.getInstance();
			//Df20170601 @Roopa ETL is scheduled after 12 so changing the query to not take the records for curr day
			//cal.add(Calendar.DATE, +1);
			Timestamp etlstartDateNext = new Timestamp(cal.getTime().getTime());
			

			//System.out.println("Running for segment:"+segment_ID+" for OlapDate :"+maxOlapDt);
			session = HibernateUtil.getSessionFactory().openSession();
			
			//startTime = System.currentTimeMillis();
			
			
			//DF20170516 @Roopa Commenting calling below method bcoz no longer required(There is no param id concept in the transaction tables)
			
			//getGenericinfo( session );
			
			/*endTime = System.currentTimeMillis();
			iLogger.info("ETLfactDataJsonBO: setETL Get generic data time in ms: "
					+ (endTime - startTime));*/
			
			List lst = null;
			Iterator itr = null;
			Query qry = null;
			startTime = System.currentTimeMillis();
			//STEP 1 geting the maxOlap Date from the Day Aggregate Table


			lastOlapDt = maxOlapDt; //Initialize to max OLAP date
			prevlastOlapDt = maxOlapDt; //Initialize to max OLAP date
			//2016-03-23 : Going by the segment ID of the AssetTable  for a better performance - Deepthi
			if ( maxOlapDt.after(Timestamp.valueOf("2011-12-31 23:59:00") ) ) {
				//STEP 2 GET the Number of segments 
				iLogger.info("ETLfactDataJsonBO: setETL STEP 2 Segments Phase");


				//STEP 3 loop through the segemnts and get the Vins for each segment
				iLogger.info("ETLfactDataJsonBO: setETL Number of segments---" + segment_ID);

				iLogger.info("ETLfactDataJsonBO: setETL Running Segment" + segment_ID+"........");


				//S Suresh DAL Layer START   	
				//getting transcation data from new table approach (TMonData) instead of transaction table (AMH & AMD) since mysql has been 
				//upgraded from 5.5 to 5.7 which supports json data type 
				DynamicTAMD_DAL dalObject = new DynamicTAMD_DAL();

				if(session==null || !session.isOpen())
					session = HibernateUtil.getSessionFactory().openSession();
				// System.out.println("Running Thread : "+segment_ID);
				iLogger.info("ETLfactDataJsonBO: setETL STEP 3 VINS for Segment"+segment_ID+" Phase");
				Query VINS_PER_SEGID_Query = session.createQuery("select a.serial_number from AssetEntity a where a.segmentId = "+segment_ID);
				Iterator VINS_PER_SEGID_Iterator = VINS_PER_SEGID_Query.list().iterator();
				iLogger.info("ETLfactDataJsonBO: Number of assets in segment "+segment_ID+" are "+VINS_PER_SEGID_Query.list().size());

				if(session!=null || session.isOpen()){
					try{
						session.close();
					}catch(Exception e){
						e.printStackTrace();
					}
				}

				int vinCount = 1;
				while (VINS_PER_SEGID_Iterator.hasNext()) {


					String serial_number = ((AssetControlUnitEntity)VINS_PER_SEGID_Iterator.next()).getSerialNumber();
					iLogger.info("ETLfactDataJsonBO: "+segment_ID+" Processing Vin "+serial_number+" vinCount "+vinCount++);
					//System.out.println("Running for segment :"+segment_ID+" for VIN:"+serial_number);
					//STEP 4 AMHTables for VIN On CreatedTS 

					iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+serial_number+" setETL STEP 4 AMHTables for each vin Phase");

					iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+serial_number+" setETL STEP 4 AMHTables before calling getAMHTablesPerVIN_N_OLAPDate");

					startTime = System.currentTimeMillis();
					
					DynamicRemoteFactDay_DAL remoteFactDayObj=new DynamicRemoteFactDay_DAL();


					String query="select Time_Key from remote_monitoring_fact_data_dayagg_json_new where Segment_ID_TxnDate = "+segment_ID+" and Serial_number = '"+ serial_number + "' order by Time_Key desc limit 1";


					String Time_Key= remoteFactDayObj.getTimeKey(query);
					
					//maxOlapDt=Timestamp.valueOf(Time_Key);
					
					String manualOlapDate="false";
					
					
					if (Time_Key != null && Time_Key.length()!=0)  {
						maxOlapDt=	Timestamp.valueOf(Time_Key);
					}
					
					else{

						Calendar cal1  = Calendar.getInstance();
						cal1.add(Calendar.DATE, -2); //running the ETL on the next day of transaction, so giving going 2 day's back to get actual prev day of given transaction day.
						//set last OLAP date to 1 day previous to curr date
						maxOlapDt = new Timestamp(cal1.getTime().getTime());

						iLogger.info("ETLfactDataJsonBO: VIN Communicating for the First Time after OLAP Hit. lastOlapDt modified to :"+lastOlapDt);
						manualOlapDate="true";
					}
					
					endTime = System.currentTimeMillis();
					
					iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+serial_number+" getting maxolpdate for the VIN executed in "+(endTime - startTime)+" ms");
					
					startTime = System.currentTimeMillis();
					

					//get all the Transaction tables which an entry for that olap date (by created_timestamp) is there on each Transaction table   

					List<String> amhTables = dalObject.getAMHTablesPerVIN_N_OLAPDate(serial_number, maxOlapDt,segment_ID, etlstartDateNext);
					endTime = System.currentTimeMillis();

					iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+serial_number+" setETL STEP 4 AMHTables getAMHTablesPerVIN_N_OLAPDate executed in "+(endTime - startTime)+" ms");

					if(amhTables == null || amhTables.size() == 0){
						continue;
					}

					//STEP 5 Getting List of Transaction TS for each VIN after OLAPDate

					iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+serial_number+" setETL STEP 5 AMHTables before calling getAMhDataOnCreatedTS ");

					for(String amhTable : amhTables){
						
						
						new ETLfactDataJsonBOExtended(serial_number, maxOlapDt, segment_ID, amhTable, etlstartDateNext,Time_Key,aggregateParameters,accumulatedParameters,common_parameters_map,manualOlapDate);
						
						/*
						startTime = System.currentTimeMillis();
						lst = dalObject.getAMhDataOnCreatedTS(serial_number, maxOlapDt, segment_ID, amhTable, etlstartDateNext);
						endTime = System.currentTimeMillis();
						iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+serial_number+" setETL STEP 5 AMHTables executed  getAMhDataOnCreatedTS in :"+(endTime - startTime)+" ms");
						if(lst == null || lst.size()==0)
						{
							iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+serial_number+" setETL STEP 5 AMHTables after calling getAMhDataOnCreatedTS size: 0");
							continue;
						}

						itr = lst.iterator();


						iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+serial_number+" setETL STEP 5 AMHTables after calling getAMhDataOnCreatedTS size:"+lst.size());




						count2Process = lst.size();

						endTime = System.currentTimeMillis();

						iLogger.info("ETLfactDataJsonBO: Segment " + segment_ID+" has "+count2Process+" records");

						iLogger.info("ETLfactDataJsonBO: Get distinct serial Number for segement "+segment_ID+" query executed time in ms: "
								+ (endTime - startTime));
						iLogger.info("ETLfactDataJsonBO: After unique PIN list: "
								+ simpleDtStr.format(Calendar.getInstance().getTime()));
						iLogger.info("ETLfactDataJsonBO: Last OLAP dt "
								+ lastOlapDt);
						iLogger.info("ETLfactDataJsonBO: Distint VIN count "
								+ count2Process);

						while (itr.hasNext()) 
						{
							//S Suresh DAL Layer inclusion

							try
							{
								//

								AmhDAO AmhDaoObject = (AmhDAO)itr.next(); 
								vinIdentified = AmhDaoObject.getSerial_Number();

								tranTime = AmhDaoObject.getTransaction_Timestamp();

								iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+vinIdentified+" on TransTS "+tranTime+" insertOlapNew before  getEngineRunningBands");
								vin2Process = vinIdentified;

								startTime = System.currentTimeMillis();
								
								//DF20170516 @Roopa Query tweaking to get the VIN specific information(Removing hibernate)

								getVINspecificinfo(vin2Process, tranTime);
								
								endTime = System.currentTimeMillis();
								iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+vin2Process+" Get VIN specific data for "
										+ vin2Process
										+ " Time in ms: "
										+ (endTime - startTime));


								//Opening session 

								if(session == null || !session.isOpen()){
									session = HibernateUtil.getSessionFactory().openSession();
								}
								
								
								if ( !(vin2Process.equalsIgnoreCase(vinProcessed)) ) {
									PreviousDayBandList.clear();

									DynamicRemoteFactDay_DAL remoteFactDayObj=new DynamicRemoteFactDay_DAL();


									String query="select Time_Key from remote_monitoring_fact_data_dayagg_json_new where Segment_ID_TxnDate = "+segment_ID+" and Serial_number = '"+ vin2Process + "' order by Time_Key desc limit 1";


									String Time_Key= remoteFactDayObj.getTimeKey(query);
									
									
									if (Time_Key != null && Time_Key.length()!=0)  {
										lastOlapDt=	Timestamp.valueOf(Time_Key);
									}
									
									else{

										Calendar cal1  = Calendar.getInstance();
										cal1.setTime(tranTime);
										cal1.add(Calendar.DATE, -1);
										//set last OLAP date to 1 day previous to txn date so that it goes to the insertion block
										lastOlapDt = new Timestamp(cal1.getTime().getTime());

										iLogger.info("ETLfactDataJsonBO: VIN Communicating for the First Time after OLAP Hit. lastOlapDt modified to :"+lastOlapDt);
									}


								}



								if ( lastOlapDt.before(tranTime) || (lastOlapDt.equals(tranTime)) ) {
									startTime = System.currentTimeMillis();

									//if (lastOlapDt!=null && (lastOlapDt.after(tranTime) || ((dateStr.format(lastOlapDt)).equalsIgnoreCase(dateStr.format(tranTime)))) ) {
									if ((dateStr.format(lastOlapDt)).equalsIgnoreCase(dateStr.format(tranTime)) ) {	
										iLogger.info("ETLfactDataJsonBO: VIN for update"
												+ vin2Process
												+ " Tran Dt: " 
												+ dateStr.format(tranTime)
												+ " Last Olap Date: "
												+ dateStr.format(lastOlapDt));
										PreviousDayBandList.clear();

										insertOlapUsingJSON(vin2Process, tranTime, lastOlapDt, prevlastOlapDt, true,segment_ID);
									} else {
										iLogger.info("ETLfactDataJsonBO: VIN for insert"
												+ vin2Process
												+ " Tran Dt: " 
												+ dateStr.format(tranTime)
												+ " Last Olap Date: "
												+ dateStr.format(lastOlapDt));

										insertOlapUsingJSON(vin2Process, tranTime, lastOlapDt, prevlastOlapDt, false,segment_ID);					
									}

									vinProcessed = vin2Process;
									endTime = System.currentTimeMillis();
									iLogger.info("ETLfactDataJsonBO: VIN Processed"
											+ vinProcessed
											+ " Tran Time: " 
											+ tranTime
											+ " Time in ms: "									+ (endTime - startTime)
											+ " | "
											+ simpleDtStr.format(Calendar.getInstance()
													.getTime()));	
								} else {
									vinProcessed = vin2Process;
									endTime = System.currentTimeMillis();
									iLogger.info("ETLfactDataJsonBO: VIN / Tran skipped"
											+ vinProcessed
											+ " Tran Time: " 
											+ tranTime
											+ " Last Olap Dt: " 
											+ lastOlapDt
											+ " Time in ms: "
											+ (endTime - startTime)
											+ " | "
											+ simpleDtStr.format(Calendar.getInstance()
													.getTime()));
								}

								countProcessed++;
							} // End of Try block inside while

							//DF20141211 - Rajani Nagaraju - Continue with next set of VINs if the exception is thrown for a VIN - Hence try-catch block
							catch(Exception e) 
							{
								fLogger.fatal("ETLfactDataJsonBO: setETLfactData inner catch Fatal error processing VIN: "
										+ vin2Process + " " + tranTime + " "
										+ " Message: "
										+ e.getMessage());

							}


						} // End of while loop for itrVin2Process.hasNext()

					*/}//END OF amhTable FOR LOOP


				} //END OF VINS per Segment Loop
				// counter++;
				//} //END of Segment While LOOP 

			}// End of if - lastOlapDt check
		}    	// End of outer Try block
		catch (Exception e) {
			fLogger.fatal("ETLfactDataJsonBO: setETLfactData outer catch Fatal error processing VIN: "
					+ vin2Process + " " + tranTime + " "
					+ "Total Processed: "
					+ countProcessed
					+ " Message: "
					+ e.getMessage());
			e.printStackTrace();
			return "FAILURE";
		} 

		finally {
			iLogger.info("ETLfactDataJsonBO: setETL end: "
					+ "Total Processed: "
					+ countProcessed
					+ " End time: "
					+ simpleDtStr.format(Calendar.getInstance().getTime()));
			if (session!=null && session.isOpen()) {
				session.flush();
				session.close();
			}
		}
		return "success";
	}
	//DF20170516 @Roopa Commenting below method bcoz no longer required(There is no param id concept in the transaction tables)

/*	public void getGenericinfo(Session session1) {

		Logger fLogger = FatalLoggerClass.logger;

		String longitude=null;
		String latitude = null;
		String fuelLevel= null;
		String engineHours = null;
		String engineON = null;

		Properties prop1 = new Properties();
		Properties prop2 = new Properties();

		try {
			prop1.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			IndustryBO industryBoObj = new IndustryBO();
			clientId = industryBoObj.getClientEntity(prop1.getProperty("ClientName")).getClient_id();

			prop2.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			longitude= prop2.getProperty("Longitude");
			latitude= prop2.getProperty("Latitude");
			fuelLevel= prop2.getProperty("FuelLevel");
			engineHours= prop2.getProperty("TotalEngineHours");
			engineON= prop2.getProperty("EngineON");

			Iterator itr = session1.createQuery("from RpmBands where Engine_Status like 'Idle'").list().iterator();
			while (itr.hasNext()) {
				RpmBands rpmBands = (RpmBands) itr.next();
				IdleBand.add(rpmBands.getBand_ID());
			}

			itr = session1.createQuery("select max(a.parameterId), a.parameterName " +
					" from MonitoringParameters a " +
					" where parameterName in " +
					"( '"
					+ longitude + "', '" + latitude + "', '" 
					+ fuelLevel + "', '" + engineHours + "', '" 
					+ engineON + "', "
					+ "'EngineRunningBand1','EngineRunningBand2',"
					+ "'EngineRunningBand3','EngineRunningBand4', "
					+ "'EngineRunningBand5','EngineRunningBand6',"
					+ "'EngineRunningBand7', 'EngineRunningBand8'"
					+ ")"
					+ " group by a.parameterName").list().iterator();

			Object[] resultObj = null;

			while (itr.hasNext()) {
				resultObj = (Object[]) itr.next();
				if ( ((String)resultObj[1]).equalsIgnoreCase(longitude) ) {
					longitudeId = (Integer) resultObj[0]; 
				}
				if ( ((String)resultObj[1]).equalsIgnoreCase(latitude) ) {
					latitudeId = (Integer) resultObj[0]; 
				}
				if ( ((String)resultObj[1]).equalsIgnoreCase(fuelLevel) ) {
					fuelLevelId = (Integer) resultObj[0]; 
				}
				if ( ((String)resultObj[1]).equalsIgnoreCase(engineHours) ) {
					engineHoursId = (Integer) resultObj[0]; 
				}
				if ( ((String)resultObj[1]).equalsIgnoreCase(engineON) ) {
					engineONId = (Integer) resultObj[0]; 
				}
				if ( ((String)resultObj[1]).equalsIgnoreCase("EngineRunningBand1") ) {
					EngineRunningBand1Id = (Integer) resultObj[0]; 
				}
				if ( ((String)resultObj[1]).equalsIgnoreCase("EngineRunningBand2") ) {
					EngineRunningBand2Id = (Integer) resultObj[0]; 
				}
				if ( ((String)resultObj[1]).equalsIgnoreCase("EngineRunningBand3") ) {
					EngineRunningBand3Id = (Integer) resultObj[0]; 
				}
				if ( ((String)resultObj[1]).equalsIgnoreCase("EngineRunningBand4") ) {
					EngineRunningBand4Id = (Integer) resultObj[0]; 
				}
				if ( ((String)resultObj[1]).equalsIgnoreCase("EngineRunningBand5") ) {
					EngineRunningBand5Id = (Integer) resultObj[0]; 
				}
				if ( ((String)resultObj[1]).equalsIgnoreCase("EngineRunningBand6") ) {
					EngineRunningBand6Id = (Integer) resultObj[0]; 
				}
				if ( ((String)resultObj[1]).equalsIgnoreCase("EngineRunningBand7") ) {
					EngineRunningBand7Id = (Integer) resultObj[0]; 
				}
				if ( ((String)resultObj[1]).equalsIgnoreCase("EngineRunningBand8") ) {
					EngineRunningBand8Id = (Integer) resultObj[0]; 
				}
			} // End of itr

		} // End of try block

		catch (Exception e) {
			fLogger.fatal("ETLfactDataJsonBO: Fatal error getting generic data"
					+ e.getMessage());
			e.printStackTrace();
		} 

		finally {
			if (session1!=null && session1.isOpen()) {
				session1.close();
			}
			session1 = null;
		}
	}*/

	//*** start of getVINspecificinfo ***
	
	//DF20170516 @Roopa query performance improvement to get vin specific info
	
	/*public void getVINspecificinfo(Session session1,
			String Serial, 
			Timestamp runDt) {
		
    	Logger fLogger = FatalLoggerClass.logger;
		
		int oldAccountId = 0;
		if(session1 == null || !session1.isOpen())
		{
  		  session1 = HibernateUtil.getSessionFactory().openSession();
		}
		accountId = 0;
		NickName = null;
		tenancy_dimension_id = null;
		accountDimensionId = null;
		account_id = null;				
		Asset_Class_Id = null;

		Date ownershipStartDate = null;
		Object result[] = null;
		
		try {
			oldAccountId = accountId;
			Query qry = session1
					.createQuery("select max(ownershipStartDate) " 
							+ "FROM AssetAccountMapping where serialNumber='" 
							+ Serial + "' " 
							+ "and  ownershipStartDate <= '" 
							+ runDt + "'");
		
			Iterator itr = qry.list().iterator();
			
			if (itr.hasNext()) {
				ownershipStartDate = (Date) itr.next();
				
				if (ownershipStartDate != null) {
					String owdate = dateStr.format(ownershipStartDate);
					qry = session1.createQuery("select max(a.accountId) ,c.nick_name "
							+ "from AssetAccountMapping a ,AssetEntity c "
							+ "where a.serialNumber='"
							+ Serial
							+ "'and c.client_id="
							+ clientId
							+ " and c.active_status=true and c.serial_number=a.serialNumber "
							+ " and a.ownershipStartDate like '" + owdate + "'");
					itr = qry.list().iterator();
					
					if (itr.hasNext()) {
						result = (Object[]) itr.next();
						account_id = (AccountEntity) result[0];
						NickName = (String) result[1];
						accountId = account_id.getAccount_id();
					}
				}
			}
			
			if (ownershipStartDate == null) {
				qry = session1.createQuery("select a.primary_owner_id ,a.nick_name "
						+ "from AssetEntity a "
						+ "where a.serial_number='"
						+ Serial
						+ "'and a.client_id="
						+ clientId
						+ " and a.active_status=true ");
				itr = qry.list().iterator();
				
				if (itr.hasNext()) {
					result = (Object[]) itr.next();
					accountId = (Integer) result[0];
					NickName = (String) result[1];

				}
			}

			if (oldAccountId == accountId) {
				return;
			}
			
			// Changes based on the dimensionId addition...
			//Code change done by Juhi on 14-11-2013 : Latest value from Tenancy Dimension Id
			qry = session1.createQuery("from AccountTenancyMapping " +
							"where account_id="
							+ accountId);
			itr = qry.list().iterator();
			
			if (itr.hasNext()) {
				//Code changes done for latest tenacy_Dimension_Id by Juhi on 6-December-2013	
			//	qry = session1.createQuery("from TenancyDimensionEntity where tenacy_Dimension_Id=(select max(tenacy_Dimension_Id) from TenancyDimensionEntity where tenancyId =(select tenancy_id from AccountTenancyMapping where account_id="+accountId+"))" );
				
				//DF20141211 - Rajani Nagaraju - Inner subquery was returning more than one row if two tenancy is tagged to same accountId - Hence throwing SQL Exception and hence all the further VINs were not processed
				qry = session1.createQuery("from TenancyDimensionEntity where tenacy_Dimension_Id=(select max(tenacy_Dimension_Id) from TenancyDimensionEntity where tenancyId =(select min(tenancy_id) from AccountTenancyMapping where account_id="+accountId+"))" );
				qry = session1.createQuery("from TenancyDimensionEntity " +
								"where tenancyId = " +
								"(select tenancy_id " +
								"from AccountTenancyMapping " +
								"where account_id="
								+ accountId + ")");
				itr = qry.list().iterator();

				if (itr.hasNext()) {
					while(itr.hasNext())
					{
					TenancyDimensionEntity tenancyDimensionEntity = (TenancyDimensionEntity) itr.next();
					tenancy_dimension_id = tenancyDimensionEntity;
					
					tenancy_dimension_id_json=tenancy_dimension_id.getTenacy_Dimension_Id();
					}
				}
			}
//Code change done by Juhi on 14-11-2013 : Latest value from Account Dimension Id
			qry = session1.createQuery("from AccountDimensionEntity " +
							"where account_id="
							+ accountId);
			
			itr = qry.list().iterator();
			
			while(itr.hasNext()) {
				accountDimensionId = (AccountDimensionEntity) itr.next();
				
				acc_id_json=accountDimensionId.getAccountDimensionId();
			}
			//Code change for latest asset class dimension Id DF:2013-01-07
			qry = session1.createQuery("from AssetClassDimensionEntity b where b.assetClassDimensionId=(select max(a.assetClassDimensionId) from AssetClassDimensionEntity a where a.productId=(select productId from AssetEntity where serial_number='"+Serial+"'))");
			itr = qry.list().iterator();
			//Code change done by Juhi on 14-11-2013 : Latest value from Asset Class Dimension Id				
			if (itr.hasNext()) {
				while(itr.hasNext())
				Asset_Class_Id = (AssetClassDimensionEntity) itr.next();
				asset_class_id_json=Asset_Class_Id.getAssetClassDimensionId();
			}
		
		} // End of try block
		
		catch (HibernateException  e) {
			fatalError.fatal("ETLfactDataBO: Fatal error getting VIN data: "
					+ Serial
					+ "Message: "
					+ e.getMessage());
			e.printStackTrace();
		}
		
		catch (Exception e) {
			fLogger.fatal("ETLfactDataBO: Fatal error getting VIN data: "
					+ Serial
					+ "Message: "
					+ e.getMessage());
			e.printStackTrace();
		}
		
		finally {
			session1 = null;
		}
	}*/
	
	
	//DF20170516 @Roopa query performance improvement to get vin specific info

	public void getVINspecificinfo(String Serial,Timestamp runDt) {
		
    	Logger fLogger = FatalLoggerClass.logger;
    	
    	Logger iLogger = InfoLoggerClass.logger;


		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
    	
    	String assetOwnersQuery=null;
    	String dimensionsQuery=null;
    	
    	int account_id_assetowner=0;
    	
    	int account_id_final=0;
		
		try{
			
			
			
			assetOwnersQuery="select a.primary_owner_id, b.Account_ID from (select primary_owner_id from asset where serial_number='"+Serial+"')a left outer join (select Account_ID from wise.asset_owners where serial_number='"+Serial+"' and Ownership_Start_Date=(SELECT max(Ownership_Start_Date) FROM wise.asset_owners where serial_number='"+Serial+"' and  Ownership_Start_Date <='"+runDt+"')) b on a.primary_owner_id=b.Account_ID";
			
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getETLConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(assetOwnersQuery);


			while(rs.next()){
				account_id_final=rs.getInt("primary_owner_id");
				account_id_assetowner=rs.getInt("Account_ID");
			}
			
			if(account_id_assetowner!=0){
				account_id_final=account_id_assetowner;	
			}
			
			if(account_id_final==0){
				return;
			}
			
			dimensionsQuery="SELECT ad.Account_Dimension_ID,td.Tenancy_Dimension_ID,ac.Asset_Class_Dimension_ID,a.Engine_Number FROM account_dimension ad, asset_class_dimension ac,tenancy_dimension td, asset a where ad.Account_ID="+account_id_final+" and td.Tenancy_ID=ad.Tenancy_ID and a.serial_number='"+Serial+"' and ac.Product_ID=a.Product_ID";
			
			rs = statement.executeQuery(dimensionsQuery);
			
			while(rs.next()){
				acc_id_json=rs.getInt("Account_Dimension_ID");
				tenancy_dimension_id_json=rs.getInt("Tenancy_Dimension_ID");
				asset_class_id_json=rs.getInt("Asset_Class_Dimension_ID");
				NickName=rs.getString("Engine_Number");
				
			}
			
		} 
		
		catch (SQLException e) {

			fLogger.fatal("ETLfactDataJsonBO: SQL error getting VIN data: "
					+ Serial
					+ "Message: "
					+ e.getMessage());
			e.printStackTrace();
		} 

		catch(Exception e)
		{
			fLogger.fatal("ETLfactDataJsonBO: Fatal error getting VIN data: "
					+ Serial
					+ "Message: "
					+ e.getMessage());
			e.printStackTrace();
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

	public void insertOlapUsingJSON(String Serial,
			Timestamp tranTime,
			Timestamp lastOlapDt,
			Timestamp prevlastOlapDt,
			boolean update,int segmentId) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		int tranNo = 0;
		long startTime = 0l;
		long endTime = 0l;

		double FuelIdle = 0.0;
		double FuelWorking = 0.0;
		double TotalFuelUsed = 0.0;
		double MachineHours = 0.0;
		double EngineOffHours = 0.0;

		String Longitude = null;
		String Latitude = null;
		//DefectId:2015-01-05 @Suprava 
		String engine_Status =null;
		List<Double> EngineRunningBandList = new LinkedList<Double>();

		Timestamp Last_Engine_Run = null;
		Timestamp minCreatedDt = null;
		Timestamp maxCreatedDt = null;
		Object result[] = null;
		Map new_aggregated_parameter_map = new HashMap();;
		Map commom_aggregated_parameter_map = new HashMap();;
		Map new_accumulated_parameter_map = new HashMap();;
		Map commom_accumulated_parameter_map = new HashMap();;



		try
		{
			iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" insertOlapNew start ");
			EngineRunningBandList.clear();
			//	Keerthi : rounding engine off hours to 2 places : 10/03/14
			DecimalFormat df2 = new DecimalFormat("###.##");


			startTime = System.currentTimeMillis();

			String transaction_timestamp =dateStr.format(tranTime);
			//S Suresh  

			//instead of query, calling the DAL layer method to do the same job 
			//as we have seperated the database operations from  business logic 

			//DynamicAMH_DAL amhDAL = new DynamicAMH_DAL();

			//S Suresh  
			//get the partitioned segementID which is combination of segmentID of serialNumber and transaction data
			//eg: 120160617 which is a partition key of all the dynamic tables amh,amd and amde 
			//if we want use the functionality of partitioning we have to query the partitioned table on partition table
			//other wise there is no use of partitioning so thats why getting the partitioned key of that serialnumber on that trasanctional date
			iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" insertOlapNew before  getEngineRunningBands");
			iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" on TransTS "+transaction_timestamp+" insertOlapNew before  getEngineRunningBands");
			int seg_ID = new CommonUtil().convertSegment_TxnDate(segmentId+"", transaction_timestamp);
			startTime = System.currentTimeMillis();
			//EngineRunningBandList = amhDAL.getEngineRunningBands(Serial, tranTime, seg_ID);

			//S Suresh  

			/* passing two extra parameters aggregateParameters,common_parameters_map which got received from the constructor 
			 *  new ETLfactDataJsonBO(segementIDThread,maxOlapDate,aggregateParameters,accumulatedParameters,common_parameters_map  )
			 *  since the Transaction table data population has chenged so as ETLDesign has changed according to the new Transaction table approach
			 *  aggregateParameters - is a Map which has newly addded Aggregated Parameters which can be used in reports 
			 *  accumulatedParameters - is a Map which has newly addded Accumulated Parameters which can be used in reports 
			 *  common_parameters_map - is map which has both common Aggregated and Accumulated parameter list 
			 *  new coloumn has added in the remote__fact_day_agg which is a json datatype for newly added parameters (both aggregated and accumulated parameters)
			 *  so we follow the same old approach for ETL population inaddition we create a json for new parameters and store it in new column along with old paramters 
			 */

			Map aggregated_txn_data_map = new DynamicTAMD_DAL().getAggregatedDataOnTxnDate(Serial, tranTime, seg_ID, aggregateParameters, common_parameters_map, accumulatedParameters);

			//aggregated_txn_data_map is a TxnData map which has both new aggregated and common aggregated values for all parameters is stored with 
			//keys New_Aggregated , Common 
			new_aggregated_parameter_map = (Map)aggregated_txn_data_map.get("New_Aggregated");
			
			new_accumulated_parameter_map = (Map)aggregated_txn_data_map.get("New_Accumulated");


			commom_aggregated_parameter_map = (Map)aggregated_txn_data_map.get("Common");

			//common_parameters_map is a map for both common accumulated and aggregated parameter keys  which is the master data with keys 
			//Common_Aggregated_Parameters for common aggregated paramters 
			Iterator common_parameters_keys_itr = ((List)common_parameters_map.get("Common_Aggregated_Parameters")).iterator();

			while(common_parameters_keys_itr.hasNext()){
				String parameter_key = (String)(common_parameters_keys_itr.next());
				EngineRunningBandList.add((Double)commom_aggregated_parameter_map.get(parameter_key));
			}

			endTime = System.currentTimeMillis();

			iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" insertOlapNew getEngineRunningBands executed in :"+(endTime - startTime));
			if(EngineRunningBandList != null)
				iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" insertOlapNew after  getEngineRunningBands size :"+EngineRunningBandList.size());

			/*List ampParameterIDList = new LinkedList();
			ampParameterIDList.add(longitudeId);
			ampParameterIDList.add(latitudeId);
			ampParameterIDList.add(engineHoursId);
			ampParameterIDList.add(engineONId);*/

			iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" insertOlapNew before getEngineRunningBands :");
			startTime = System.currentTimeMillis();

			//List<AssetMonitoringParametersDAO> lst = amhDAL.getAMPValues(Serial, tranTime, seg_ID, ampParameterIDList);

			Map accumulated_txn_data_map = new DynamicTAMD_DAL().getAccumulatedDataOnTxnDate(Serial, tranTime, seg_ID, common_parameters_map);



			//aggregated_txn_data_map is a TxnData map which has both new aggregated and common aggregated values for all parameters is stored with 
			//keys New_Aggregated , Common 
			
			//Df20170522 @Roopa commenting the below logic because the accululated parameters comes only in last log pkt for BHL and CAN parameters

			//new_accumulated_parameter_map = (Map)accumulated_txn_data_map.get("New_Accumulated");


			commom_accumulated_parameter_map = (Map)accumulated_txn_data_map.get("Common");

			//common_parameters_map is a map for both common accumulated and aggregated parameter keys  which is the master data with keys 
			//Common_Accumulated_Parameters for common accumulated paramters 
			common_parameters_keys_itr = ((List)common_parameters_map.get("Common_Accumulated_Parameters")).iterator();

			if(new_aggregated_parameter_map!=null && new_accumulated_parameter_map!=null)
				new_aggregated_parameter_map.putAll(new_accumulated_parameter_map);

			String newTxndetails=null;

			//start the server side calculation's for the CAN parameters and add it to json column(new_aggregated_parameter_map)
			double FuelUsedInLPB=0.0;
			double FuelUsedInMPB=0.0;
			double FuelUsedInHPB=0.0;
			double AvgFuelConsumption=0.0;
			double FuelLoss=0.0;
			double FuelUsedInWorking=0.0;
			String Fuel_rate=null;
			String LEPB=null;
			String MEPB=null;
			String HEPB=null;
			String IEPB=null;
			String TFL=null;

			if(new_aggregated_parameter_map!=null && new_aggregated_parameter_map.size()>0){

				if(new_aggregated_parameter_map.get("TFL")!=null){
					TFL=new_aggregated_parameter_map.get("TFL").toString();
					
					AvgFuelConsumption=(Double.parseDouble(TFL)/24.0)*100; 
				}

				if(new_aggregated_parameter_map.get("Fuel_rate")!=null)
					Fuel_rate=new_aggregated_parameter_map.get("Fuel_rate").toString();

				if(new_aggregated_parameter_map.get("LEPB")!=null)
					LEPB=new_aggregated_parameter_map.get("LEPB").toString();

				if(new_aggregated_parameter_map.get("MEPB")!=null)
					MEPB=new_aggregated_parameter_map.get("MEPB").toString();

				if(new_aggregated_parameter_map.get("HEPB")!=null)
					HEPB=new_aggregated_parameter_map.get("HEPB").toString();

				if(new_aggregated_parameter_map.get("IEPB")!=null)
					IEPB=new_aggregated_parameter_map.get("IEPB").toString();

				if(Fuel_rate!=null){
					if(LEPB!=null){
						FuelUsedInLPB=Double.parseDouble(Fuel_rate)*Double.parseDouble(LEPB);
					}
					if(MEPB!=null){
						FuelUsedInMPB=Double.parseDouble(Fuel_rate)*Double.parseDouble(MEPB);
					}
					if(HEPB!=null){
						FuelUsedInHPB=Double.parseDouble(Fuel_rate)*Double.parseDouble(HEPB);
					}
					if(IEPB!=null){
						FuelLoss=Double.parseDouble(Fuel_rate)*Double.parseDouble(IEPB);
					}

					FuelUsedInWorking=FuelUsedInLPB+FuelUsedInMPB+FuelUsedInHPB;
				}

				//AvgFuelConsumption

				//yet to get the business logic

				new_aggregated_parameter_map.put("FuelUsedInLPB", FuelUsedInLPB);
				new_aggregated_parameter_map.put("FuelUsedInMPB", FuelUsedInMPB);
				new_aggregated_parameter_map.put("FuelUsedInHPB", FuelUsedInHPB);
				new_aggregated_parameter_map.put("AvgFuelConsumption", AvgFuelConsumption);
				new_aggregated_parameter_map.put("FuelLoss", FuelLoss);
				new_aggregated_parameter_map.put("FuelUsedInWorking", FuelUsedInWorking);

				//EngineOnCount(No of times the Eng ON has come)

				String eng_onoff_count=new DynamicTAMD_DAL().getEngineONAndOFFCount(Serial, tranTime, seg_ID);

				new_aggregated_parameter_map.put("EngineOnCount", eng_onoff_count.split(",")[0]);
				//EngineOffCount (No of times the Eng OFF has come)
				new_aggregated_parameter_map.put("EngineOffCount", eng_onoff_count.split(",")[1]);
			}

			//end

			if(new_aggregated_parameter_map!=null && new_aggregated_parameter_map.size()>0)
				newTxndetails= new JSONObject(new_aggregated_parameter_map).toString();


			endTime = System.currentTimeMillis();

			iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" insertOlapNew -> getEngineRunningBands executed in :"+(endTime - startTime));

			//Iterator itr = lst.iterator();

			int flag = 0;


			while (common_parameters_keys_itr.hasNext()) 
			{
				flag++;


				String parameter_key = (String)(common_parameters_keys_itr.next());

				if ( parameter_key.equals("LONG")  ){
					if(commom_accumulated_parameter_map!=null){
						Longitude = ((Double)commom_accumulated_parameter_map.get(parameter_key)).toString();
					}
				}



				if ( parameter_key.equals("LAT")  ){
					if(commom_accumulated_parameter_map!=null){
						Latitude = ((Double)commom_accumulated_parameter_map.get(parameter_key)).toString();
					}
				}



				if ( parameter_key.equals("CMH")  ){
					if(commom_accumulated_parameter_map!=null){
						MachineHours = ((Double)commom_accumulated_parameter_map.get(parameter_key));
					}
				}

				if ( parameter_key.equals("ENG_STATUS")  ){

					//DF20170317 @Roopa ENG_STATUS will be not there for old machines instead we have to check for EVT_ENG

					if(commom_accumulated_parameter_map!=null){
						if(commom_accumulated_parameter_map.get(parameter_key)!=null){
							engine_Status = (((Double)commom_accumulated_parameter_map.get(parameter_key)).intValue())+"";

						}
						else if(commom_accumulated_parameter_map.get("EVT_ENG")!=null){
							engine_Status = (((Double)commom_accumulated_parameter_map.get("EVT_ENG")).intValue())+"";
						}

						else{
							engine_Status="0";
						}

					}
					else{
						engine_Status="0";
					}
					if ( Integer.parseInt( engine_Status ) == 1 ) {
						Last_Engine_Run = tranTime;
					} else {


						Last_Engine_Run = new DynamicTAMD_DAL().getLatestEngineONTxn(Serial, tranTime, seg_ID);
						if(Last_Engine_Run==null){
							Last_Engine_Run= Timestamp.valueOf("1000-01-01 00:00:00"); //setting default value to avoid the Incorrect datetime value: 'null' for column 'Last_Engine_Run' eror	
						}

					}
				}

			}

			for (int i = 0; i < EngineRunningBandList.size(); i++) {
				EngineOffHours = EngineOffHours + EngineRunningBandList.get(i);
			}
			iLogger.info("EngineOffHours "+ EngineOffHours);
			EngineOffHours = 24 - EngineOffHours;
			iLogger.info("24 - EngineOffHours  "+ EngineOffHours);
			if ( EngineRunningBandList.size() == 0) {
				for ( int i = 1; i <= 8; i++){
					EngineRunningBandList.add(0.0);
				}
			}

			endTime = System.currentTimeMillis();
			iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" Get current details for VIN "
					+ Serial
					+ " time in ms: "
					+ (endTime - startTime));

			startTime = System.currentTimeMillis();


			TotalFuelUsed = (Double)(commom_accumulated_parameter_map.get("FUEL_PERCT"));



			if ( TotalFuelUsed > 0 && EngineOffHours < 24 ) {
				FuelIdle  = (TotalFuelUsed/(24 - EngineOffHours)) * (EngineRunningBandList.get(0) + EngineRunningBandList.get(1));
				FuelWorking = TotalFuelUsed - FuelIdle;
			} else {
				FuelIdle  = 0.0;
				FuelWorking = 0.0;
			}


			endTime = System.currentTimeMillis();
			iLogger.info("ETLfactDataJsonBO: SegmentID "+segment_ID+" VIN "+Serial+" Get fuel usage for VIN "
					+ Serial
					+ " time in ms: "
					+ (endTime - startTime));



			SimpleDateFormat simpleDtStr = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			Timestamp created_timestamp = new Timestamp(cal.getTime().getTime());



			//Start check for the record exist for the given vin_transaction time

			//to avoid the session update error, if the record doesn't exist for the given date

			DynamicRemoteFactDay_DAL remoteFactDayObj=new DynamicRemoteFactDay_DAL();

			//lastOlapDt=Timestamp.valueOf(dateStr.format(lastOlapDt) + " 00:00:00");

		/*if (update){
				String query="select Time_Key from remote_monitoring_fact_data_dayagg_json_new where Segment_ID_TxnDate = "+seg_ID+" and Serial_number = '"+ Serial + "' and Time_Key='"+Timestamp.valueOf(dateStr.format(tranTime) + " 00:00:00")+"'";


				String Time_Key= remoteFactDayObj.getTimeKey(query);
				if (Time_Key != null && Time_Key.length()!=0)  {
					update=true;	
				}
				else{
					update=false;
				}

		}*/



			//End

			AssetMonitoringFactDataEntity assetMonitoringFactDataEntity = new AssetMonitoringFactDataEntity();
			assetMonitoringFactDataEntity.setCreated_Timestamp(created_timestamp);
			/*if ( update ){
				assetMonitoringFactDataEntity.setTimeKey( Timestamp.valueOf(dateStr.format(lastOlapDt) + " 00:00:00") );
			} else {*/
			assetMonitoringFactDataEntity.setTimeKey( Timestamp.valueOf(dateStr.format(tranTime) + " 00:00:00") );
			//}

			//assetMonitoringFactDataEntity.setTenancyId(tenancy_dimension_id);
			//assetMonitoringFactDataEntity.setAccountDimensionId(accountDimensionId);
			assetMonitoringFactDataEntity.setSerialNumber(Serial);

			assetMonitoringFactDataEntity.setMachineHours(Double.valueOf(df2.format((Double)(MachineHours))));
			assetMonitoringFactDataEntity.setFuelUsedIdle(FuelIdle);
			assetMonitoringFactDataEntity.setFuelUsedWorking(FuelWorking);
			assetMonitoringFactDataEntity.setLastReported(tranTime);
			assetMonitoringFactDataEntity.setLastEngineRun(Last_Engine_Run);
			assetMonitoringFactDataEntity.setLocation(Latitude + "," + Longitude);
			iLogger.info("Double.valueOf(df2.format((Double)(EngineOffHours))) "+Double.valueOf(df2.format((Double)(EngineOffHours))));
			assetMonitoringFactDataEntity.setEngineOffHours(Double.valueOf(df2.format((Double)(EngineOffHours))));
			assetMonitoringFactDataEntity.setEngineRunningBand1(EngineRunningBandList.get(0));
			assetMonitoringFactDataEntity.setEngineRunningBand2(EngineRunningBandList.get(1));
			assetMonitoringFactDataEntity.setEngineRunningBand3(EngineRunningBandList.get(2));
			assetMonitoringFactDataEntity.setEngineRunningBand4(EngineRunningBandList.get(3));
			assetMonitoringFactDataEntity.setEngineRunningBand5(EngineRunningBandList.get(4));
			assetMonitoringFactDataEntity.setEngineRunningBand6(EngineRunningBandList.get(5));
			assetMonitoringFactDataEntity.setEngineRunningBand7(EngineRunningBandList.get(6));
			assetMonitoringFactDataEntity.setEngineRunningBand8(EngineRunningBandList.get(7));
			//assetMonitoringFactDataEntity.setAssetClassDimensionId(Asset_Class_Id);
			assetMonitoringFactDataEntity.setMachineName(NickName);
			assetMonitoringFactDataEntity.setAddress(null);
			assetMonitoringFactDataEntity.setEngineStatus(engine_Status);
			assetMonitoringFactDataEntity.setAggregate_param_data(newTxndetails);

			String status= remoteFactDayObj.updateRemoteFactDetails(assetMonitoringFactDataEntity, update,seg_ID,tenancy_dimension_id_json,acc_id_json,asset_class_id_json);

			iLogger.info("ETLfactDataJsonBO: insertorUpdateOlapNew for SegmentID "+segment_ID+" VIN "+Serial+" TxnKey "+tranTime+" Status::"+status);

		} // End of main try bloock

		catch (Exception e) {


			fLogger.fatal("ETLfactDataJsonBO: insertOlapNew SegmentID"+segmentId+" VIN "+Serial+" Fatal error processing VIN: "
					+ Serial 
					+ "Message: "
					+ e.getMessage());

			Writer result1 = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result1);
			e.printStackTrace(printWriter);
			String err = result1.toString();
			fLogger.fatal("ETLfactDataJsonBO: insertOlapNew SegmentID"+segmentId+" VIN "+Serial+" TxnKey "+tranTime+" Exception trace: "+err);
		} 

		finally {

		}

	}//*** end of insertOlap ***

}
