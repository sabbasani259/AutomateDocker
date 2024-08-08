package remote.wise.businessobject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import redis.clients.jedis.Jedis;
import remote.wise.businessentity.AccountDimensionEntity;
import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetClassDimensionEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetMonitoringFactDataEntity;
import remote.wise.businessentity.ClientEntity;
import remote.wise.businessentity.MonitoringParameters;
import remote.wise.businessentity.ProductEntity;
import remote.wise.businessentity.RpmBands;
import remote.wise.businessentity.TenancyDimensionEntity;
import remote.wise.businessentity.TenancyEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.LocationDetails;
import remote.wise.util.GetSetLocationJedis;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
import remote.wise.util.LocationByLatLon;

public class ETLfactDataOLDBO {

	// DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static
	// logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger(
			"ETLfactDataBO:", "businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger(
			"ETLfactDataBO:", "fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger(
			"ETLfactDataBO:", "info");*/

	// DF 1409.sn
		private int clientId = 0;

		private int longitudeId = 0;
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
		private int EngineRunningBand8Id = 0;

		private int accountId = 0;	
		
		private String NickName = null;
			
		private SimpleDateFormat simpleDtStr = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss");
		private DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd");
		
		private List <Integer> IdleBand = new LinkedList<Integer>();
		private List <Double> PreviousDayBandList = new LinkedList<Double>();
		
		private AccountEntity account_id = null;
		private TenancyDimensionEntity tenancy_dimension_id = null;
		private AccountDimensionEntity accountDimensionId = null;
		private AssetClassDimensionEntity Asset_Class_Id = null;
		//private static final String HOST = "localhost";
		//private static final int PORT = 29000;
		// DF 1409.en
		
	// **************************************Start of updateAddress
	// *********************************************************
	/**
	 * This Method will allow to Update AssetClassDimensionId in Fact Tables.
	 * 
	 * @return Success if updated Successfully
	 */
	public String updateAddress(int count) {
		// Update updateAddress for Day,Week ,Month, Quarter,Year Fact Table
		/*updateAddressTable(
				"select max(timeKey) From AssetMonitoringFactDataDayAgg",
				"AssetMonitoringFactDataDayAgg","AssetMonitoringFactDataWeekAgg","AssetMonitoringFactDataMonthAgg","AssetMonitoringFactDataQuarterAgg","AssetMonitoringFactDataYearAgg",count);
		
		*/
		
		/*
		 * updateAddressTable(
		 * "select max(timeCount) From AssetMonitoringFactDataWeekAgg"
		 * ,"AssetMonitoringFactDataWeekAgg"); updateAddressTable(
		 * "select max(timeCount) From AssetMonitoringFactDataMonthAgg"
		 * ,"AssetMonitoringFactDataMonthAgg"); updateAddressTable(
		 * "select max(timeCount) From AssetMonitoringFactDataQuarterAgg"
		 * ,"AssetMonitoringFactDataQuarterAgg");
		 * updateAddressTable("select max(year) From AssetMonitoringFactDataYearAgg"
		 * ,"AssetMonitoringFactDataYearAgg");
		 */

		/**
		 * Sequential execution/updation of aggregate tables
		 */
		
		updateAddressHibernate("select max(timeKey) From AssetMonitoringFactDataDayAgg","AssetMonitoringFactDataDayAgg",0);
		updateAddressHibernate("select max(timeKey) From AssetMonitoringFactDataDayAgg","AssetMonitoringFactDataWeekAgg",0);
		updateAddressHibernate("select max(timeKey) From AssetMonitoringFactDataDayAgg","AssetMonitoringFactDataMonthAgg",0);
		updateAddressHibernate("select max(timeKey) From AssetMonitoringFactDataDayAgg","AssetMonitoringFactDataQuarterAgg",0);
		updateAddressHibernate("select max(timeKey) From AssetMonitoringFactDataDayAgg","AssetMonitoringFactDataYearAgg",0);
		return "Success";

	}

	/**
	 * 
	 * @param queryString - querry to select max(time key)
	 * @param dayAggregate - table to be updated/populate address/city/state
	 * @param count - placeholder for limit
	 */
	//public void updateAddressHibernate(String queryString, String dayAggregate, int count) {
	
	public void updateAddressHibernate(String queryString, String aggregate, int count) {

		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Properties prop = new Properties();
		iLogger.info(":::FOR THE TABLE :: "+aggregate);
		try 
		{
			Query query = session.createQuery(queryString);
			Iterator itr = query.list().iterator();
			Timestamp exist = null;
			while (itr.hasNext()) {
				exist = (Timestamp) itr.next();
			}
			
			if (exist != null) 
			{
				boolean isStatus = false;
				String services = null;
				String redisIp=null;
				String redisPort=null;
				prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				services = prop.getProperty("MapService");
				redisIp = prop.getProperty("geocodingredisurl");
				redisPort = prop.getProperty("geocodingredisport");
				Query queryMapService = session.createQuery("select max(configuration_id),isStatus from ConfigAppEntity where services='"
																+ services + "' and isStatus=true");
				Iterator MapServiceIterator = queryMapService.list().iterator();
				Object resultObj[] = null;
				if (MapServiceIterator.hasNext()) 
				{
					resultObj = (Object[]) MapServiceIterator.next();
					if ((Boolean) resultObj[1]) {
						isStatus = (Boolean) resultObj[1];
					}
				}
				
				if (isStatus) 
				{
					int offset = 0;
					int counterNoRowsUpdated= offset;
					iLogger.info("OFFSET VALUE::::"+counterNoRowsUpdated);
					boolean loopTableFlag = true;
					//Jedis redisPool = new Jedis(HOST,PORT);
					Jedis redisPool = new Jedis(redisIp,Integer.parseInt(redisPort));
					int updateneeded = 0;
					// Looping all rows in the table with limit and updating addresses 
					long fullstartTime = System.currentTimeMillis();
					while (loopTableFlag) {
						
						session.beginTransaction();

						long startTimeQuery = System.currentTimeMillis();
						Query locationQuery = session.createQuery(" from "+ aggregate + " where Address is null");
						locationQuery.setMaxResults(1000);
						Iterator locationIterator = locationQuery.list().iterator();
						long endTimeQuery = System.currentTimeMillis();
						
						iLogger.info("SELECT QUERRY :: "+locationQuery+" :::: Time taken "+(endTimeQuery-startTimeQuery)+"size "+locationQuery.list().size());
						
						if(!locationIterator.hasNext()){
							loopTableFlag = false;
						}else{
							long startTime  = System.currentTimeMillis();
							
							while (locationIterator.hasNext()) {
								counterNoRowsUpdated++;

								AssetMonitoringFactDataEntity aggregateTableRow = (AssetMonitoringFactDataEntity) locationIterator.next();
								
								iLogger.info("AssetMonitoringFactDataEntity" + aggregateTableRow);
								iLogger.info("aggregateTableRow.getLocation()" + aggregateTableRow.getLocation());
								
								String location[];
								String latitude = null;
								String longitude = null;
								
								try{
									location = aggregateTableRow.getLocation().toString().split(",");
									latitude = location[0];
									longitude = location[1];
								}catch (Exception e) {
									fLogger.fatal(" Exception when splitting location "+e.getMessage());
								}
								LocationDetails locObj = GetSetLocationJedis.getLocationDetails(latitude,longitude, redisPool);
								
								// exception scenario by osm/google by shrini 201605041040
								if(aggregate.equalsIgnoreCase("AssetMonitoringFactDataDayAgg")){
									if(locObj.getCity()!=null){
										aggregateTableRow.setCity(locObj.getCity());
									}else{
										aggregateTableRow.setCity("undefined");
									}
									
									if(locObj.getState()!=null){
										aggregateTableRow.setState(locObj.getState());
									}else{
										aggregateTableRow.setState("undefined");
									}
								}
								
								if(locObj.getAddress()!=null){
									aggregateTableRow.setAddress(locObj.getAddress());
								}else{
									aggregateTableRow.setAddress("undefined");
								}
								
								updateneeded++;
								session.update(aggregate, aggregateTableRow);
								iLogger.info("Number of updates done::::: "	+ counterNoRowsUpdated);
								iLogger.info("Number of rows updated after the execution ::::: "+ updateneeded);
							}
							offset = offset + 1000;
							if(session.isOpen()){
								session.flush();
							}
							if (session.getTransaction().isActive()) {
								session.getTransaction().commit();
							}
							iLogger.info("Time taken to loop 1000 rows::::: "+(System.currentTimeMillis()-startTime));
						}
					}
					iLogger.info("Full execution Time"+(System.currentTimeMillis()-fullstartTime));
					try{
						redisPool.disconnect();
					}catch (Exception e) {
						fLogger.fatal("Exception while disconnecting redis pool "+e.getMessage());
					}
				}
			}
		} catch (Exception e){ 
			iLogger.info("Exception while connecting Database "+ e.getMessage());
			e.printStackTrace();
		} 
	}
	
	/**
	 * Reading the offset of value of aggregate table as staring point to update tables - RC201603240100
	 * @param aggrFileName - aggregate offset file Name 
	 * @return
	 */
	public static int readOffsetTable(String aggrFileName){
		Logger fLogger = FatalLoggerClass.logger;
		int currentOffset = 0;
		FileInputStream fstream = null;
		BufferedReader br = null;
		try{
			fstream = new FileInputStream(aggrFileName);
			br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				currentOffset = Integer.parseInt(strLine);
			}
		}catch (Exception e) {
			fLogger.error("Exception while closing the outputstream of offset look table "+e.getMessage());
		}finally{
			try{
				fstream.close();
				br.close();
			}catch (Exception e) {
				fLogger.error("Exception while closing the outputstream of offset look table "+e.getMessage());
			}
		}
		return currentOffset;
	}
	
	
	/**
	 * updating the offset with number of rows updated by shrini - RC201603240100
	 * @param aggrFileName - aggregate offset file Name
	 * @param incrementCount - offset to be updated
	 */
	public static void updateOffsetTable(String aggrFileName,long incrementCount) {
		Logger fLogger = FatalLoggerClass.logger;
		try {
			FileWriter fstreamWrite = new FileWriter(aggrFileName);
			BufferedWriter out = new BufferedWriter(fstreamWrite);
			out.write(String.valueOf(incrementCount));
			out.flush();
			try{
				out.close();	
			}catch (Exception e) {
				fLogger.error("Exception while closing the outputstream of offset look table "+e.getMessage());
			}
			
		} catch (Exception e) {
			fLogger.error("Exception while closing the outputstream of offset look table "+e.getMessage());
		}
	}
	
	
	// **************************************End of updateAddress
	// *********************************************************
	// Added Code for Updation of Address
	public void updateAddressTable(String queryString, String dayAggregate, String weekAggregate, String monthAggregate, String quarterAggregate, String yearAggregate, int count) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Properties prop = new Properties();
		LocationByLatLon locationByLatLon = new LocationByLatLon();
		
		try 
		{
			Query query = session.createQuery(queryString);
			Iterator itr = query.list().iterator();
			Timestamp exist = null;
			while (itr.hasNext()) {
				exist = (Timestamp) itr.next();
			}
			// If there exist record in Olap Fact Table, then Update Address

			if (exist != null) 
			{/*
								 * String Address =null; Address
								 * =locationByLatLon
								 * .getLocation("77.54405333333335"
								 * ,"12.921033333333332");
								 * infoLogger.info("Address is"+Address);
				
								 */
				boolean isStatus = false;
				String services = null;
				String redisIp = null;
				String redisPort = null;
				prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				services = prop.getProperty("MapService");
				redisIp = prop.getProperty("geocodingredisurl");
				redisPort = prop.getProperty("geocodingredisport");
				Query queryMapService = session.createQuery("select max(configuration_id),isStatus from ConfigAppEntity where services='"
																+ services + "' and isStatus=true");
				Iterator MapServiceIterator = queryMapService.list().iterator();
				Object resultObj[] = null;
				if (MapServiceIterator.hasNext()) 
				{
					resultObj = (Object[]) MapServiceIterator.next();
					if ((Boolean) resultObj[1]) 
					{
						isStatus = (Boolean) resultObj[1];
					}
				}
				
				if (isStatus) 
				{
					//DF20140915 - Rajani Nagaraju - Added undefined to be also processed in the next run
					List<String> locationList = new LinkedList<String>();
					
					//DF30150710 - Rajani Nagaraju - Populate Address only for the fields with NULL and not for undefined 
					/*Query locationQuery = session.createQuery("select a.location from " + tableName+ " a where a.Address is null OR a.Address='undefined' " +
							" OR a.state is null OR a.state like 'undefined' OR a.city is null OR a.city like 'undefined'");*/
					
					// RC201603181245 update address redis cache changes by Shrini 
					Query locationQuery = session.createQuery("select DISTINCT a.location from " + dayAggregate+ " a where a.Address is null " +
							" OR a.state is null OR a.city is null ");
					locationQuery.setMaxResults(count);
					
					Iterator locationIterator = locationQuery.list().iterator();
					int rowUpdateCounter=0;
					while (locationIterator.hasNext()) 
					{
						String location = (String) locationIterator.next();
						locationList.add(location);
						
					}	
					//Jedis redisPool = new Jedis(HOST,PORT);
					Jedis redisPool = new Jedis(redisIp,Integer.parseInt(redisPort));
					long startTime = System.currentTimeMillis();
					for(int i=0; i<locationList.size(); i++)
					{
						long startTime1 = System.currentTimeMillis();
						iLogger.info("Start Time in ETLfactBO:" + startTime1);
						try
						{
							rowUpdateCounter++;
							
							String location = locationList.get(i);
							String temp[] = location.split(",");
							String Latitude = temp[0];
							String Longitude = temp[1];
	
							String Address = null;
						
							//Address = locationByLatLon.getLocation(Longitude, Latitude);
//							LocationDetails locObj = locationByLatLon.getLocationDetails(Longitude, Latitude);
							LocationDetails locObj = GetSetLocationJedis.getLocationDetails(Latitude,Longitude,redisPool);
							Address=locObj.getAddress();
							iLogger.info("Address is" + Address);
							
							//DF20140915 - Rajani Nagaraju - Addresses with ' in the content was not getting updated because of SQL query exception 
							if(Address!=null)
							{
								Address=Address.replaceAll("'", "");
								Address=Address.replaceAll("\"", "");
							}
							
							//DF20150611 - Rajani Nagaraju - Adding City and State null check in update query
							//DF30150710 - Rajani Nagaraju - Populate Address only for the fields with NULL and not for undefined 
							/*Query updateQuery = session.createQuery("UPDATE "
									+ tableName + " SET Address='" + Address
									+ "', state='"+locObj.getState()+"', city='"+locObj.getCity()+"'" +
									" WHERE location= '" + location + "' and (Address is null or Address='undefined' or state is null OR state like 'undefined' OR city is null OR city like 'undefined')");*/
							iLogger.info("Before executing the query");
							Query updateQuery = session.createQuery("UPDATE "
									+ dayAggregate + " SET Address='" + Address
									+ "', state='"+locObj.getState()+"', city='"+locObj.getCity()+"'" +
									" WHERE location= '" + location + "' and ( Address is null OR state is null OR city is null )");
							
							
							Query updateweekQuery = session.createQuery("UPDATE "
									+ weekAggregate + " SET Address='" + Address +
									"' WHERE location= '" + location + "' and ( Address is null)");
							
							Query updateMonthQuery = session.createQuery("UPDATE "
									+ monthAggregate + " SET Address='" + Address +
									"' WHERE location= '" + location + "' and ( Address is null)");
							
							Query updatequarterQuery = session.createQuery("UPDATE "
									+ quarterAggregate + " SET Address='" + Address +
									"' WHERE location= '" + location + "' and ( Address is null)");
							
							
							Query updateyearQuery = session.createQuery("UPDATE "
									+ yearAggregate + " SET Address='" + Address +
									"' WHERE location= '" + location + "' and ( Address is null)");
							
							iLogger.info("query for updation :" + updateQuery);
							
							int rows = updateQuery.executeUpdate();
							int weekrows = updateweekQuery.executeUpdate();
							int monthrows = updateMonthQuery.executeUpdate();
							int quarterrows = updatequarterQuery.executeUpdate();
							int yearrows = updateyearQuery.executeUpdate();
							iLogger.info("After executing the query");
							iLogger.info("End Time in ETLfactBO - For one row:" + (System.currentTimeMillis()-startTime1));
						}
						catch (Exception e) 
						{
							e.printStackTrace();
						}
						
						if(rowUpdateCounter>5)
						{
							iLogger.info("End Time in ETLfactBO - For 100 rows:" + (System.currentTimeMillis()-startTime1));
							rowUpdateCounter=0;
							try
							{
								if (session.getTransaction().isActive()) 
								{
									session.getTransaction().commit();
								}
							}
							catch(Exception e)
							{
								fLogger.fatal(e);
							}
							
							finally
							{
								if (session.isOpen()) 
								{
									session.flush();
									session.close();
								}
							}
							
							if(! (session.isOpen() ))
				            {
				                        session = HibernateUtil.getSessionFactory().getCurrentSession();
				                        session.getTransaction().begin();
				            }
							
						}
						
					} 
					long endTime = System.currentTimeMillis();
					iLogger.info("*******Total Time To Populate Day Aggregate Table****** "+(endTime-startTime));
					try{
						redisPool.disconnect();
					}catch (Exception e) {
						fLogger.fatal("Exception while disconnecting redis pool "+e.getMessage());
					}
				}

			}

		} 
		
		catch (Exception e) 
		{
			fLogger.fatal("In Database ,value are not there "+ e.getMessage());
			e.printStackTrace();
		} 
		
		finally 
		{
		
			try
			{
				if (session.getTransaction().isActive()) 
					session.getTransaction().commit();
			}
			
			catch(Exception e)
			{
				fLogger.fatal(e);
			}

			finally
			{
				if (session.isOpen()) 
				{
					session.flush();
					session.close();
				}
			}
		}

	}

	public void UpdateFact(String ExistingOlapDate, String Serial,
			AssetMonitoringFactDataEntity assetMonitoringFactDataEntity,
			List<Integer> IdleBand, boolean old) {
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		
		Session session1 = HibernateUtil.getSessionFactory()
				.getCurrentSession();
		session1.beginTransaction();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DecimalFormat df2 = new DecimalFormat("###.#");
		Properties prop = new Properties();
		LocationByLatLon locationByLatLon = new LocationByLatLon();
		AccountEntity account_id = null;
		String NickName = null;
		
		double EngineRunningBand1 = 0;
		double EngineRunningBand2 = 0;
		double EngineRunningBand3 = 0;
		double EngineRunningBand4 = 0;
		double EngineRunningBand5 = 0;
		double EngineRunningBand6 = 0;
		double EngineRunningBand7 = 0;
		double EngineRunningBand8 = 0;
		
		try {
			// get Client Details
			String clientName = null;
			
			prop.load(getClass().getClassLoader().getResourceAsStream(
					"remote/wise/resource/properties/configuration.properties"));
			clientName = prop.getProperty("ClientName");
			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj
					.getClientEntity(clientName);
			// END of get Client Details

			if (!(session1.isOpen())) {
				session1 = HibernateUtil.getSessionFactory()
						.getCurrentSession();
				session1.getTransaction().begin();
			}

			/*
			 * Query quer1 = session .createQuery(
			 * " FROM AssetMonitoringHeaderEntity a where a.transactionTime <'"
			 * + ExistingOlapDate +
			 * "' and a.transactionTime >=(SELECT min(date) FROM DateDimensionEntity)  and a.serialNumber='"
			 * + Serial + "'"); Iterator itr13 = quer1.list().iterator();
			 * infoLogger.info("check-----" + itr13.hasNext()); boolean
			 * status = itr13.hasNext(); infoLogger.info("status   " +
			 * status); if (status == true) {
			 */
			// changes done on 26 June2013
			int accountId = 0;
			// Changes done by Juhi on 25-september-2013
			Query checkaccountQuery = session1
					.createQuery("select max(ownershipStartDate) " 
							+ "FROM AssetAccountMapping where serialNumber='" 
							+ Serial + "' " 
							+ "and  ownershipStartDate <= '" 
							+ ExistingOlapDate + "'");
			Iterator checkaccountIterator = checkaccountQuery.list().iterator();
			Date ownershipStartDate = null;
			if (checkaccountIterator.hasNext()) {
				ownershipStartDate = (Date) checkaccountIterator.next();
				if (ownershipStartDate != null) {
					String owdate = dateFormat.format(ownershipStartDate);
					String queryString = "select max(a.accountId) ,c.nick_name from AssetAccountMapping a ,AssetEntity c "
							+ "where a.serialNumber='"
							+ Serial
							+ "'and c.client_id="
							+ clientEntity.getClient_id()
							+ " and c.active_status=true and c.serial_number=a.serialNumber "
							+ " and a.ownershipStartDate like '" + owdate + "'";
					Object result[] = null;
					Query query = session1.createQuery(queryString);
					Iterator itr = query.list().iterator();
					if (itr.hasNext()) {
						result = (Object[]) itr.next();
						account_id = (AccountEntity) result[0];
						NickName = (String) result[1];
						accountId = account_id.getAccount_id();
					}
				}
			}
			if (ownershipStartDate == null) {
				String queryString = "select a.primary_owner_id ,a.nick_name from AssetEntity a "
						+ "where a.serial_number='"
						+ Serial
						+ "'and a.client_id="
						+ clientEntity.getClient_id()
						+ " and a.active_status=true ";
				Object result[] = null;
				Query query = session1.createQuery(queryString);
				Iterator itr = query.list().iterator();
				if (itr.hasNext()) {
					result = (Object[]) itr.next();
					accountId = (Integer) result[0];
					NickName = (String) result[1];

				}

			}
			
			iLogger.info("accountId" + accountId);
			iLogger.info("NickName" + NickName);

			// Changes based on the dimensionId addition...
			TenancyDimensionEntity tenancy_dimension_id = null;

			Query sqlQuery = session1
					.createQuery("from AccountTenancyMapping " +
							"where account_id="
							+ accountId);
			Iterator iterator = sqlQuery.list().iterator();
			if (iterator.hasNext()) {//Code changes done for latest tenacy_Dimension_Id by Juhi on 6-December-2013	
				Query qu1 = session1
						.createQuery("from TenancyDimensionEntity where tenacy_Dimension_Id=(select max(tenacy_Dimension_Id) from TenancyDimensionEntity where tenancyId =(select tenancy_id from AccountTenancyMapping where account_id="+accountId+"))" );
				Iterator itrr = qu1.list().iterator();

				if (itrr.hasNext()) {
					TenancyDimensionEntity tenancyDimensionEntity = (TenancyDimensionEntity) itrr
							.next();
					tenancy_dimension_id = tenancyDimensionEntity;
					
					iLogger.info("tenancy_dimension_id"
							+ tenancy_dimension_id);
				}
			}

			Query q1 = session1
					.createQuery("from AccountDimensionEntity " +
							"where account_id="
							+ accountId);
			
			Iterator itrr1 = q1.list().iterator();
			AccountDimensionEntity accountDimensionId = null;
			
			if (itrr1.hasNext()) {
				AccountDimensionEntity accountDimensionEntity = (AccountDimensionEntity) itrr1
						.next();
				accountDimensionId = accountDimensionEntity;
				iLogger.info("accountDimensionId " + accountDimensionId);

			}
			
			double PreviousDayCumIdle = 0;
			double PreviousDayCumWorking = 0;
			
			List<Double> PreviousDayBandList = new LinkedList<Double>();
			
			if (old) {
				// Defect Id :- 1409 -- 20131007: Performance issue correction.
				// Instead of transactionNumber in, transactionNumber = is used
				// for matching a.transactionNumber in where clause
				String queryString4 = "select a.parameterId,a.parameterValue "
						+ "from AssetMonitoringDetailEntity a "
						+ "where a.parameterId in " + "("
								+ "select max(b.parameterId) "
								+ "from MonitoringParameters b "
								+ "where b.parameterName in "
										+ "('EngineRunningBand1','EngineRunningBand2',"
										+ "'EngineRunningBand3','EngineRunningBand4',"
										+ "'EngineRunningBand5','EngineRunningBand6',"
										+ "'EngineRunningBand7','EngineRunningBand8') "
								+ "group by b.parameterName " + ")"
						+ "and a.transactionNumber = " 
								+ "("
								+ "select max(c.transactionNumber) "
								+ "from AssetMonitoringHeaderEntity c "
								+ "where c.transactionTime =" 
										+ "("
										+ "select max(transactionTime) "
										+ "from AssetMonitoringHeaderEntity "
										+ "where serialNumber = '" + Serial + "' "
										+ "and transactionTime < '" + ExistingOlapDate + "%' "
										+ "and transactionTime > = " 
												+ "("
												+ "SELECT min(date) " 
												+ "FROM DateDimensionEntity"
												+ ") "
										+ ")" 
								+ "and c.serialNumber='" + Serial + "'" 
								+ ")"
						+ "group by  a.parameterId";
				Query query3 = session1.createQuery(queryString4);
				Iterator itr3 = query3.list().iterator();
				Object result4[] = null;

				while (itr3.hasNext()) {
					result4 = (Object[]) itr3.next();
					double a = Double.parseDouble(result4[1].toString());
					PreviousDayBandList.add(a);
				}
				
				iLogger.info("Previous day max. value for All bands "
						+ PreviousDayBandList);

				for (int p = 0; p < 8; p++) {
					if (p < IdleBand.size()) {
						PreviousDayCumIdle = PreviousDayCumIdle
								+ PreviousDayBandList.get(p);
					} else {
						PreviousDayCumWorking = PreviousDayCumWorking
								+ PreviousDayBandList.get(p);
					}

				}

			}

			// Changes done by Juhi on 1-August-2013 for Defect ID :1083 Code
			// changes to handle scenario when the asset_Extended_detail is not
			// populated with any data
			String queryString1 = "select a.serialNumber, b.transactionNumber,b.parameterId ,b.parameterValue ,c.extendedParameterValue "
					+ " from AssetMonitoringDetailExtended c right outer join c.transactionNumber a , AssetMonitoringDetailEntity b"
					+ " where a.transactionNumber=b.transactionNumber "
					+ "and a.serialNumber='"
					+ Serial
					+ "'"
					+ " and a.transactionTime like '"
					+ ExistingOlapDate
					+ "%'"
					+ " and b.parameterId in (select max(d.parameterId) from MonitoringParameters d where d.parameterName in ('EngineRunningBand1','EngineRunningBand2','EngineRunningBand3','EngineRunningBand4', 'EngineRunningBand5','EngineRunningBand6','EngineRunningBand7', 'EngineRunningBand8') group by d.parameterId )"
					+ " order by b.transactionNumber,b.parameterId";

			Query query4 = session1.createQuery(queryString1);
			List list = query4.list();
			int sizeOfList = list.size();
			Iterator itr4 = list.iterator();
			Object result1[] = null;

			double idle = 0;
			double working = 0;
			double FuelIdeal = 0;
			double FuelWorking = 0;
			double PreviousDayCumIdle1 = 0;
			double PreviousDayCumWorking1 = 0;
			
			iLogger.info("sizeOfList" + sizeOfList);
			
			int count = 1;
			int index = sizeOfList - 8;
			int flag = 0;
			int countt = 0;
			double fuel = 0;
			
			List<Double> ToDayBandList = new LinkedList<Double>();
			
			while (itr4.hasNext()) {
				flag++;
				result1 = (Object[]) itr4.next();
				String e = result1[3].toString();
				double b = Double.parseDouble(e);
				if (result1[4] != null) {
					fuel = Double.parseDouble(result1[4].toString());
					countt++;
				}
				if (flag > index) {
					ToDayBandList.add(b);
				}

				if (count <= IdleBand.size()) {
					iLogger.info("executing idle ");
					idle = idle + b;
					count++;
				} else {
					working = working + b;
					count++;
				}

				if ((countt + 1) % 8 == 0) {
					iLogger.info("idle" + idle);
					iLogger.info("working" + working);
					PreviousDayCumIdle1 = idle;
					PreviousDayCumWorking1 = working;
					idle = idle - PreviousDayCumIdle;
					working = working - PreviousDayCumWorking;
					iLogger.info("idle___" + idle);
					iLogger.info("working_____" + working);
					if (idle > working) {
						FuelIdeal = FuelIdeal + fuel;
						PreviousDayCumIdle = PreviousDayCumIdle1;
						PreviousDayCumWorking = PreviousDayCumWorking1;
						idle = 0;
						working = 0;
						count = 1;
					} else {
						FuelWorking = FuelWorking + fuel;
						PreviousDayCumIdle = PreviousDayCumIdle1;
						PreviousDayCumWorking = PreviousDayCumWorking1;
						idle = 0;
						working = 0;
						count = 1;
					}
				}

			}
			
			iLogger.info("FuelIdeal" + FuelIdeal);
			iLogger.info("FuelWorking" + FuelWorking);
			iLogger.info("ToDayBandList" + ToDayBandList);
			
			if (old) {
				EngineRunningBand1 = ToDayBandList.get(0)
						- PreviousDayBandList.get(0);
				EngineRunningBand2 = ToDayBandList.get(1)
						- PreviousDayBandList.get(1);
				EngineRunningBand3 = ToDayBandList.get(2)
						- PreviousDayBandList.get(2);
				EngineRunningBand4 = ToDayBandList.get(3)
						- PreviousDayBandList.get(3);
				EngineRunningBand5 = ToDayBandList.get(4)
						- PreviousDayBandList.get(4);
				EngineRunningBand6 = ToDayBandList.get(5)
						- PreviousDayBandList.get(5);
				EngineRunningBand7 = ToDayBandList.get(6)
						- PreviousDayBandList.get(6);
				EngineRunningBand8 = ToDayBandList.get(7)
						- PreviousDayBandList.get(7);
			} else {
				EngineRunningBand1 = ToDayBandList.get(0);
				EngineRunningBand2 = ToDayBandList.get(1);
				EngineRunningBand3 = ToDayBandList.get(2);
				EngineRunningBand4 = ToDayBandList.get(3);
				EngineRunningBand5 = ToDayBandList.get(4);
				EngineRunningBand6 = ToDayBandList.get(5);
				EngineRunningBand7 = ToDayBandList.get(6);
				EngineRunningBand8 = ToDayBandList.get(7);
			}
			
			iLogger.info("EngineRunningBand1" + EngineRunningBand1);
			iLogger.info("EngineRunningBand2" + EngineRunningBand2);
			iLogger.info("EngineRunningBand3" + EngineRunningBand3);
			iLogger.info("EngineRunningBand4" + EngineRunningBand4);
			iLogger.info("EngineRunningBand5" + EngineRunningBand5);
			iLogger.info("EngineRunningBand6" + EngineRunningBand6);
			iLogger.info("EngineRunningBand7" + EngineRunningBand7);
			iLogger.info("EngineRunningBand8" + EngineRunningBand8);

			double MachineHours = 0.0D;

			// Changes Done for Machine hours Cumalative value on 2013-06-06
			String MachineHourValue = null;

			Query query04 = session1
					.createQuery("select a.parameterValue from AssetMonitoringDetailEntity a "
							+ "where a.parameterId in ("
							+ "select max(b.parameterId) from MonitoringParameters b "
							+ "where b.parameterName like 'Hour%'"
							+ " group by b.parameterName "
							+ ") "
							+ " and a.transactionNumber =(select max(c.transactionNumber) from AssetMonitoringHeaderEntity c "
							+ "where c.transactionTime = ("
							+ " select max(transactionTime) from AssetMonitoringHeaderEntity where serialNumber='"
							+ Serial
							+ "' and transactionTime like '"
							+ ExistingOlapDate
							+ "%') and c.serialNumber='"
							+ Serial + "')");
			Iterator iterator04 = query04.list().iterator();
			
			if (iterator04.hasNext()) {
				MachineHourValue = (String) iterator04.next();
				MachineHours = Double.parseDouble(MachineHourValue);
			}
			/*
			 * for (int j = 0; j < ToDayBandList.size(); j++) { MachineHours =
			 * MachineHours + ToDayBandList.get(j); }
			 */
			double EngineOffHours = 24 - (EngineRunningBand1
					+ EngineRunningBand2 + EngineRunningBand3
					+ EngineRunningBand4 + EngineRunningBand5
					+ EngineRunningBand6 + EngineRunningBand7 + EngineRunningBand8);
			
			iLogger.info("EngineOffHours" + EngineOffHours);
			iLogger.info("MachineHours" + MachineHours);

			String queryString5 = "select max(a.transactionTime)" 
					+ "from AssetMonitoringHeaderEntity a, AssetMonitoringDetailEntity b "
					+ "where a.transactionTime like '"
							+ ExistingOlapDate + "%'"
					+ "and b.transactionNumber=a.transactionNumber "
					+ "and b.parameterId=("
							+ "select max(parameterId) " 
							+ "from MonitoringParameters "
							+ "where parameterName like 'Engine_On%') "
							+ "and b.parameterValue=1 "
							+ "and a.serialNumber='"
									+ Serial + "'";

			Timestamp Last_Engine_Run = null;
			Object result11[] = null;			
			
			Query query77 = session1.createQuery(queryString5);
			Iterator itr77 = query77.list().iterator();

			if (itr77.hasNext()) {
				Last_Engine_Run = (Timestamp) itr77.next();
			}
			
			iLogger.info("Last_Engine_Run" + Last_Engine_Run);

			String queryString9 = "select d.parameterValue, e.transactionTime " 
					+ "from AssetMonitoringDetailEntity d, AssetMonitoringHeaderEntity e "
					+ "where e.transactionNumber=d.transactionNumber "
					+ "and d.transactionNumber = "
							+ "("
							+ "select max(a.transactionNumber)"
							+ "from AssetMonitoringDetailEntity a "
							+ "where a.parameterId = "
									+ "("
									+ "select max(b.parameterId) "
									+ "from MonitoringParameters b "
									+ "where b.parameterName = 'Latitude' "
									+ ") " 
							+ "and a.transactionNumber = "
									+ "("
									+ "select c.transactionNumber "
									+ "from AssetMonitoringHeaderEntity c "
									+ "where c.transactionTime like '"
											+ ExistingOlapDate + "%' " +
									"and c.serialNumber='"
											+ Serial 
									+ "')"
							+ ") " 
					+ "and d.parameterId = "
							+ "("
							+ "select max(b.parameterId) "
							+ "from MonitoringParameters b "
							+ "where b.parameterName = 'Latitude' "
							+ ")";
			
			Object result3[] = null;
			String Latitude = null;
			Timestamp LastReported = null;
			
			Query query9 = session1.createQuery(queryString9);
			Iterator itr9 = query9.list().iterator();

			if (itr9.hasNext()) {
				result3 = (Object[]) itr9.next();
				Latitude = (String) result3[0];
				LastReported = (Timestamp) result3[1];
			}
			
			iLogger.info("Latitude " + Latitude);

			String queryString10 = "select d.parameterValue ,e.transactionTime from AssetMonitoringDetailEntity d ,AssetMonitoringHeaderEntity e"
					+ " where e.transactionNumber=d.transactionNumber and d.transactionNumber=("
					+ "select max(a.transactionNumber)from AssetMonitoringDetailEntity a "
					+ "where a.parameterId =("
					+ "select max(b.parameterId) from MonitoringParameters b where b.parameterName = 'Longitude' "
					+ ") and a.transactionNumber in ("
					+ "select c.transactionNumber from AssetMonitoringHeaderEntity c "
					+ "where c.transactionTime like '"
					+ ExistingOlapDate
					+ "%' and c.serialNumber='"
					+ Serial
					+ "')"
					+ ") and d.parameterId =("
					+ "select max(b.parameterId) from MonitoringParameters b "
					+ "where b.parameterName = 'Longitude' )";
			Object result5[] = null;
			String Longitude = null;
			Query query10 = session1.createQuery(queryString10);
			Iterator itr10 = query10.list().iterator();
			if (itr10.hasNext()) {
				result5 = (Object[]) itr10.next();
				Longitude = (String) result5[0];
			}
			iLogger.info("Longitude " + Longitude);

			String Location = null;
			Location = Latitude + "," + Longitude;

			String Address = null;
			/*
			 * String services=null; boolean isStatus=false; try {
			 * //prop.load(getClass().getClassLoader().getResourceAsStream(
			 * "remote/wise/resource/properties/configuration.properties"));
			 * services= prop.getProperty("MapService"); Query
			 * queryMapService=session1.createQuery(
			 * "select max(configuration_id),isStatus from ConfigAppEntity where services='"
			 * +services+"' and isStatus=true"); Iterator MapServiceIterator =
			 * queryMapService.list().iterator(); Object resultObj[]=null;
			 * if(MapServiceIterator.hasNext()) {
			 * resultObj=(Object[])MapServiceIterator.next();
			 * if((Boolean)resultObj[1]) { isStatus=(Boolean)resultObj[1]; } }
			 * if(isStatus) { Address
			 * =locationByLatLon.getLocation(Longitude,Latitude);
			 * infoLogger.info("Address is"+Address); } }catch (Exception e) {
			 * e.printStackTrace(); }
			 */

			AssetClassDimensionEntity Asset_Class_Id = null;
			//Code change for latest asset class dimension Id DF:2013-01-07
			Query query11 = session1
					.createQuery("from AssetClassDimensionEntity b where b.assetClassDimensionId=(select max(a.assetClassDimensionId) from AssetClassDimensionEntity a where a.productId=(select productId from AssetEntity where serial_number='"+Serial+"'))");
			Iterator itr11 = query11.list().iterator();
			if (itr11.hasNext()) {
				AssetClassDimensionEntity assetClassDimensionEntity = (AssetClassDimensionEntity) itr11
						.next();
				Asset_Class_Id = assetClassDimensionEntity;
			}
			iLogger.info("Asset_Class_Id " + Asset_Class_Id);

			assetMonitoringFactDataEntity.setMachineHours(Double.valueOf(df2.format((Double)(MachineHours))));
			assetMonitoringFactDataEntity.setFuelUsedIdle(FuelIdeal);
			assetMonitoringFactDataEntity.setFuelUsedWorking(FuelWorking);
			assetMonitoringFactDataEntity.setLastReported(LastReported);

			assetMonitoringFactDataEntity.setLastEngineRun(Last_Engine_Run);
			assetMonitoringFactDataEntity.setLocation(Location);
			assetMonitoringFactDataEntity
					.setEngineOffHours(Double.valueOf(df2.format((Double)(EngineOffHours))));
			assetMonitoringFactDataEntity.setTenancyId(tenancy_dimension_id);
			assetMonitoringFactDataEntity
					.setEngineRunningBand1(EngineRunningBand1);
			assetMonitoringFactDataEntity
					.setEngineRunningBand2(EngineRunningBand2);
			assetMonitoringFactDataEntity
					.setEngineRunningBand3(EngineRunningBand3);
			assetMonitoringFactDataEntity
					.setEngineRunningBand4(EngineRunningBand4);
			assetMonitoringFactDataEntity
					.setEngineRunningBand5(EngineRunningBand5);
			assetMonitoringFactDataEntity
					.setEngineRunningBand6(EngineRunningBand6);
			assetMonitoringFactDataEntity
					.setEngineRunningBand7(EngineRunningBand7);
			assetMonitoringFactDataEntity
					.setEngineRunningBand8(EngineRunningBand8);

			assetMonitoringFactDataEntity
					.setAssetClassDimensionId(Asset_Class_Id);
			assetMonitoringFactDataEntity.setMachineName(NickName);
			assetMonitoringFactDataEntity.setAddress(Address);
			session1.update("AssetMonitoringFactDataDayAgg",
					assetMonitoringFactDataEntity);

			// DF1409.sn
			iLogger.info("ETLfactDataBO:  Record processed: "
					+ Serial
					+ " | "
					+ assetMonitoringFactDataEntity.getTimeKey());
			// DF1409.en
			// }
		} catch (Exception e) {
			fLogger.fatal("In Database ,value are not there "
					+ e.getMessage());
			e.printStackTrace();
		} finally {
			if (session1.getTransaction().isActive()) {
				session1.getTransaction().commit();
			}

			if (session1.isOpen()) {
				session1.flush();
				session1.close();
			}

		}

	}

	// for fact insertion for Old
	public void InsertFact(Timestamp Time_Key, String SerialNumber,
			List<Integer> IdleBand, boolean old) {

		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			DecimalFormat df2 = new DecimalFormat("###.#");
			String Timekey = dateFormat.format(Time_Key);

			// get Client Details
			Properties prop = new Properties();
			String clientName = null;

			prop.load(getClass().getClassLoader().getResourceAsStream(
					"remote/wise/resource/properties/configuration.properties"));
			clientName = prop.getProperty("ClientName");

			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj
					.getClientEntity(clientName);
			// END of get Client Details

			if (!(session.isOpen())) {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			LocationByLatLon locationByLatLon = new LocationByLatLon();
			AccountEntity account_id = null;
			String NickName = null;
			double EngineRunningBand1 = 0;
			double EngineRunningBand2 = 0;
			double EngineRunningBand3 = 0;
			double EngineRunningBand4 = 0;
			double EngineRunningBand5 = 0;
			double EngineRunningBand6 = 0;
			double EngineRunningBand7 = 0;
			double EngineRunningBand8 = 0;

			// changes done on 26 June2013

			int accountId = 0;
			// Changes done by Juhi on 25-september-2013
			Query checkaccountQuery = session
					.createQuery("select max(ownershipStartDate)  FROM AssetAccountMapping where serialNumber='"
							+ SerialNumber
							+ "' and  ownershipStartDate <= '"
							+ Timekey + "'");
			Iterator checkaccountIterator = checkaccountQuery.list().iterator();
			Date ownershipStartDate = null;

			if (checkaccountIterator.hasNext()) {
				ownershipStartDate = (Date) checkaccountIterator.next();
				if (ownershipStartDate != null) {
					String owdate = dateFormat.format(ownershipStartDate);
					String queryString = "select max(a.accountId) ,c.nick_name from AssetAccountMapping a ,AssetEntity c "
							+ "where a.serialNumber='"
							+ SerialNumber
							+ "'and c.client_id="
							+ clientEntity.getClient_id()
							+ " and c.active_status=true and c.serial_number=a.serialNumber "
							+ " and a.ownershipStartDate like '" + owdate + "'";
					Object result[] = null;
					Query query = session.createQuery(queryString);
					Iterator itr = query.list().iterator();
					if (itr.hasNext()) {
						result = (Object[]) itr.next();
						account_id = (AccountEntity) result[0];
						NickName = (String) result[1];
						accountId = account_id.getAccount_id();
					}
				}
			}
			if (ownershipStartDate == null) {
				String queryString = "select a.accountId ,a.nick_name from AssetEntity a "
						+ "where a.serial_number='"
						+ SerialNumber
						+ "'and a.client_id="
						+ clientEntity.getClient_id()
						+ " and a.active_status=true ";
				Object result[] = null;
				Query query = session.createQuery(queryString);
				Iterator itr = query.list().iterator();
				if (itr.hasNext()) {
					result = (Object[]) itr.next();
					accountId = (Integer) result[0];
					NickName = (String) result[1];

				}

			}
			iLogger.info("accountId" + accountId);

			iLogger.info("NickName" + NickName);

			TenancyDimensionEntity tenancy_dimension_id = null;
			Query sqlQuery = session
					.createQuery("from AccountTenancyMapping where account_id="
							+ accountId);
			Iterator iterator = sqlQuery.list().iterator();
			if (iterator.hasNext()) {//Code changes done for latest tenacy_Dimension_Id by Juhi on 6-December-2013	
				Query quer = session
						.createQuery("from TenancyDimensionEntity where tenacy_Dimension_Id=(select max(tenacy_Dimension_Id) from TenancyDimensionEntity where tenancyId =(select tenancy_id from AccountTenancyMapping where account_id="+accountId+"))" );

				Iterator itrr = quer.list().iterator();

				if (itrr.hasNext()) {
					TenancyDimensionEntity tenancyDimensionEntity = (TenancyDimensionEntity) itrr
							.next();
					tenancy_dimension_id = tenancyDimensionEntity;
					iLogger.info("tenancy_dimension_id"
							+ tenancy_dimension_id);

				}

			}
			Query q1 = session
					.createQuery("from AccountDimensionEntity where account_id="
							+ accountId);
			Iterator<?> itrr1 = q1.list().iterator();
			AccountDimensionEntity accountDimensionId = null;
			if (itrr1.hasNext()) {
				AccountDimensionEntity accountDimensionEntity = (AccountDimensionEntity) itrr1
						.next();
				accountDimensionId = accountDimensionEntity;
				iLogger.info("accountDimensionId " + accountDimensionId);

			}
			double PreviousDayCumIdle = 0;
			double PreviousDayCumWorking = 0;
			List<Double> PreviousDayBandList = new LinkedList<Double>();
			if (old) {
				// Defect Id :- 1409 -- 20131007: Performance issue correction.
				// Instead of transactionNumber in, transactionNumber = is used
				// for matching a.transactionNumber in where clause
				String queryString4 = "select a.parameterId,a.parameterValue from AssetMonitoringDetailEntity a "
						+ "where a.parameterId in ("
						+ "select max(b.parameterId) from MonitoringParameters b"
						+ " where b.parameterName in ('EngineRunningBand1','EngineRunningBand2','EngineRunningBand3','EngineRunningBand4','EngineRunningBand5','EngineRunningBand6','EngineRunningBand7','EngineRunningBand8')  group by b.parameterName "
						+ ")and a.transactionNumber = ("
						+ "select max(c.transactionNumber) from AssetMonitoringHeaderEntity c "
						+ "where c.transactionTime =("
						+ " select max(transactionTime) from AssetMonitoringHeaderEntity where serialNumber='"
						+ SerialNumber
						+ "' and transactionTime <'"
						+ Timekey
						+ "%' and transactionTime >=(SELECT min(date) FROM DateDimensionEntity) )and c.serialNumber='"
						+ SerialNumber + "'" + ")group by  a.parameterId";
				Query query3 = session.createQuery(queryString4);
				Iterator<?> itr3 = query3.list().iterator();
				Object result4[] = null;

				while (itr3.hasNext()) {
					result4 = (Object[]) itr3.next();
					double a = Double.parseDouble(result4[1].toString());
					PreviousDayBandList.add(a);
				}
				iLogger.info("Previous day max. value for All bands "
						+ PreviousDayBandList);

				for (int p = 0; p < 8; p++) {
					if (p < IdleBand.size()) {
						PreviousDayCumIdle = PreviousDayCumIdle
								+ PreviousDayBandList.get(p);
					} else {
						PreviousDayCumWorking = PreviousDayCumWorking
								+ PreviousDayBandList.get(p);
					}

				}
			}
			/*
			 * String queryString1 =
			 * "select a.serialNumber, b.transactionNumber,b.parameterId ,b.parameterValue ,c.extendedParameterId,c.extendedParameterValue"
			 * +
			 * " from AssetMonitoringHeaderEntity a,AssetMonitoringDetailEntity b,AssetMonitoringDetailExtended c"
			 * + " where a.serialNumber='" + SerialNumber + "'" +
			 * "and a.transactionTime like '" + TodaysDate + "%'" +
			 * "and b.transactionNumber=a.transactionNumber " +
			 * "and c.transactionNumber=b.transactionNumber" +
			 * " and b.parameterId in " + "(select max(d.parameterId) " +
			 * "from MonitoringParameters d " +
			 * "where d.parameterName  in ('EngineRunningBand1','EngineRunningBand2','EngineRunningBand3','EngineRunningBand4', 'EngineRunningBand5','EngineRunningBand6','EngineRunningBand7', 'EngineRunningBand8') "
			 * + "group by d.parameterId )" + "and c.extendedParameterId =(" +
			 * "select max(extendedParameterId) from ExtendedMonitoringParameters where extendedParameterName like 'FuelConsumedInLitres') "
			 * + "order by b.transactionNumber,b.parameterId";
			 */
			// Changes done by Juhi on 1-August-2013 for Defect ID :1083 Code
			// changes to handle scenario when the asset_Extended_detail is not
			// populated with any data
			String queryString1 = " select a.serialNumber, b.transactionNumber,b.parameterId ,b.parameterValue ,c.extendedParameterValue "
					+ " from AssetMonitoringDetailExtended c right outer join c.transactionNumber a , AssetMonitoringDetailEntity b"
					+ " where a.transactionNumber=b.transactionNumber "
					+ "and a.serialNumber='"
					+ SerialNumber
					+ "'"
					+ " and a.transactionTime like '"
					+ Timekey
					+ "%'"
					+ " and b.parameterId in (select max(d.parameterId) from MonitoringParameters d where d.parameterName in ('EngineRunningBand1','EngineRunningBand2','EngineRunningBand3','EngineRunningBand4', 'EngineRunningBand5','EngineRunningBand6','EngineRunningBand7', 'EngineRunningBand8') group by d.parameterId )"
					+ " order by b.transactionNumber,b.parameterId";

			Query query4 = session.createQuery(queryString1);
			List list = query4.list();
			int sizeOfList = list.size();
			Iterator itr4 = list.iterator();
			Object result1[] = null;

			iLogger.info("no. of rows");

			double idle = 0;
			double working = 0;
			double FuelIdeal = 0;
			double FuelWorking = 0;
			double PreviousDayCumIdle1 = 0;
			double PreviousDayCumWorking1 = 0;
			double fuel = 0;
			iLogger.info("sizeOfList" + sizeOfList);
			int count = 1;
			int index = sizeOfList - 8;
			int flag = 0;
			int countt = 0;
			List<Double> ToDayBandList = new LinkedList<Double>();
			while (itr4.hasNext()) {
				flag++;

				result1 = (Object[]) itr4.next();
				String e = result1[3].toString();
				double b = Double.parseDouble(e);
				if (result1[4] != null) {
					fuel = Double.parseDouble(result1[4].toString());
					countt++;
				}
				if (flag > index) {
					ToDayBandList.add(b);
				}
				if (count <= IdleBand.size()) {
					iLogger.info("executing idle ");
					idle = idle + b;
					count++;
				} else {
					working = working + b;
					count++;
				}
				if ((countt + 1) % 8 == 0) {
					iLogger.info("idle" + idle);
					iLogger.info("working" + working);
					PreviousDayCumIdle1 = idle;
					PreviousDayCumWorking1 = working;
					idle = idle - PreviousDayCumIdle;
					working = working - PreviousDayCumWorking;
					iLogger.info("idle___" + idle);
					iLogger.info("working_____" + working);
					if (idle > working) {
						FuelIdeal = FuelIdeal + fuel;
						PreviousDayCumIdle = PreviousDayCumIdle1;
						PreviousDayCumWorking = PreviousDayCumWorking1;
						idle = 0;
						working = 0;
						count = 1;
						countt = 0;
					} else {
						FuelWorking = FuelWorking + fuel;
						PreviousDayCumIdle = PreviousDayCumIdle1;
						PreviousDayCumWorking = PreviousDayCumWorking1;
						idle = 0;
						working = 0;
						count = 1;
						countt = 0;
					}
				}

			}
			iLogger.info("FuelIdeal" + FuelIdeal);
			iLogger.info("FuelWorking" + FuelWorking);
			iLogger.info("ToDayBandList" + ToDayBandList);
			if (old) {
				EngineRunningBand1 = ToDayBandList.get(0)
						- PreviousDayBandList.get(0);
				EngineRunningBand2 = ToDayBandList.get(1)
						- PreviousDayBandList.get(1);
				EngineRunningBand3 = ToDayBandList.get(2)
						- PreviousDayBandList.get(2);
				EngineRunningBand4 = ToDayBandList.get(3)
						- PreviousDayBandList.get(3);
				EngineRunningBand5 = ToDayBandList.get(4)
						- PreviousDayBandList.get(4);
				EngineRunningBand6 = ToDayBandList.get(5)
						- PreviousDayBandList.get(5);
				EngineRunningBand7 = ToDayBandList.get(6)
						- PreviousDayBandList.get(6);
				EngineRunningBand8 = ToDayBandList.get(7)
						- PreviousDayBandList.get(7);
			} else {
				EngineRunningBand1 = ToDayBandList.get(0);
				EngineRunningBand2 = ToDayBandList.get(1);
				EngineRunningBand3 = ToDayBandList.get(2);
				EngineRunningBand4 = ToDayBandList.get(3);
				EngineRunningBand5 = ToDayBandList.get(4);
				EngineRunningBand6 = ToDayBandList.get(5);
				EngineRunningBand7 = ToDayBandList.get(6);
				EngineRunningBand8 = ToDayBandList.get(7);
			}
			iLogger.info("EngineRunningBand1" + EngineRunningBand1);
			iLogger.info("EngineRunningBand2" + EngineRunningBand2);
			iLogger.info("EngineRunningBand3" + EngineRunningBand3);
			iLogger.info("EngineRunningBand4" + EngineRunningBand4);
			iLogger.info("EngineRunningBand5" + EngineRunningBand5);
			iLogger.info("EngineRunningBand6" + EngineRunningBand6);
			iLogger.info("EngineRunningBand7" + EngineRunningBand7);
			iLogger.info("EngineRunningBand8" + EngineRunningBand8);

			double MachineHours = 0.0D;

			// Changes Done for Machine hours Cumalative value on 2013-06-06
			String MachineHourValue = null;
			Query query04 = session
					.createQuery("select a.parameterValue from AssetMonitoringDetailEntity a "
							+ "where a.parameterId in ("
							+ "select max(b.parameterId) from MonitoringParameters b "
							+ "where b.parameterName like 'Hour%'"
							+ " group by b.parameterName "
							+ ") "
							+ " and a.transactionNumber =(select max(c.transactionNumber) from AssetMonitoringHeaderEntity c "
							+ "where c.transactionTime = ("
							+ " select max(transactionTime) from AssetMonitoringHeaderEntity where serialNumber='"
							+ SerialNumber
							+ "' and transactionTime like '"
							+ Timekey
							+ "%') and c.serialNumber='"
							+ SerialNumber + "')");
			Iterator iterator04 = query04.list().iterator();
			if (iterator04.hasNext()) {
				MachineHourValue = (String) iterator04.next();
				MachineHours = Double.parseDouble(MachineHourValue);
			}

			double EngineOffHours = 24 - (EngineRunningBand1
					+ EngineRunningBand2 + EngineRunningBand3
					+ EngineRunningBand4 + EngineRunningBand5
					+ EngineRunningBand6 + EngineRunningBand7 + EngineRunningBand8);
			iLogger.info("EngineOffHours" + EngineOffHours);

			iLogger.info("MachineHours" + MachineHours);
			String queryString5 = "select max(a.transactionTime)from AssetMonitoringHeaderEntity a,AssetMonitoringDetailEntity b "
					+ "where a.transactionTime like '"
					+ Timekey
					+ "%'"
					+ "and b.transactionNumber=a.transactionNumber "
					+ "and b.parameterId=("
					+ "select max(parameterId) from MonitoringParameters "
					+ "where parameterName like 'Engine_On%') "
					+ "and b.parameterValue=1 "
					+ "and a.serialNumber='"
					+ SerialNumber + "'";

			Timestamp Last_Engine_Run = null;
			Object result11[] = null;
			Query query77 = session.createQuery(queryString5);
			Iterator itr77 = query77.list().iterator();
			if (itr77.hasNext()) {

				Last_Engine_Run = (Timestamp) itr77.next();

			}
			iLogger.info("Last_Engine_Run" + Last_Engine_Run);

			String queryString9 = "select d.parameterValue ,e.transactionTime from AssetMonitoringDetailEntity d ,AssetMonitoringHeaderEntity e"
					+ " where e.transactionNumber=d.transactionNumber and d.transactionNumber=("
					+ "select max(a.transactionNumber)from AssetMonitoringDetailEntity a "
					+ "where a.parameterId =("
					+ "select max(b.parameterId) from MonitoringParameters b where b.parameterName = 'Latitude' "
					+ ") and a.transactionNumber in ("
					+ "select c.transactionNumber from AssetMonitoringHeaderEntity c "
					+ "where c.transactionTime like '"
					+ Timekey
					+ "%' and c.serialNumber='"
					+ SerialNumber
					+ "')"
					+ ") and d.parameterId =("
					+ "select max(b.parameterId) from MonitoringParameters b "
					+ "where b.parameterName = 'Latitude' )";
			Object result3[] = null;
			String Latitude = null;
			Query query9 = session.createQuery(queryString9);
			Timestamp LastReported = null;
			Iterator itr9 = query9.list().iterator();
			if (itr9.hasNext()) {
				result3 = (Object[]) itr9.next();
				Latitude = (String) result3[0];
				LastReported = (Timestamp) result3[1];
			}
			iLogger.info("Latitude " + Latitude);
			String queryString10 = "select d.parameterValue ,e.transactionTime from AssetMonitoringDetailEntity d ,AssetMonitoringHeaderEntity e"
					+ " where e.transactionNumber=d.transactionNumber and d.transactionNumber=("
					+ "select max(a.transactionNumber)from AssetMonitoringDetailEntity a "
					+ "where a.parameterId =("
					+ "select max(b.parameterId) from MonitoringParameters b where b.parameterName = 'Longitude' "
					+ ") and a.transactionNumber in ("
					+ "select c.transactionNumber from AssetMonitoringHeaderEntity c "
					+ "where c.transactionTime like '"
					+ Timekey
					+ "%' and c.serialNumber='"
					+ SerialNumber
					+ "')"
					+ ") and d.parameterId =("
					+ "select max(b.parameterId) from MonitoringParameters b "
					+ "where b.parameterName = 'Longitude' )";
			Object result5[] = null;
			String Longitude = null;
			Query query10 = session.createQuery(queryString10);
			Iterator itr10 = query10.list().iterator();
			if (itr10.hasNext()) {
				result5 = (Object[]) itr10.next();
				Longitude = (String) result5[0];
			}
			iLogger.info("Longitude " + Longitude);
			String Location = null;
			Location = Latitude + "," + Longitude;

			String Address = null;
			/*
			 * String services=null; boolean isStatus=false; try {
			 * prop.load(getClass().getClassLoader().getResourceAsStream(
			 * "remote/wise/resource/properties/configuration.properties"));
			 * services= prop.getProperty("MapService"); Query
			 * queryMapService=session.createQuery(
			 * "select max(configuration_id),isStatus from ConfigAppEntity where services='"
			 * +services+"' and isStatus=true "); Iterator MapServiceIterator =
			 * queryMapService.list().iterator(); Object resultObj[]=null;
			 * if(MapServiceIterator.hasNext()) {
			 * resultObj=(Object[])MapServiceIterator.next();
			 * if((Boolean)resultObj[1]) { isStatus=(Boolean)resultObj[1]; } }
			 * if(isStatus) { Address
			 * =locationByLatLon.getLocation(Longitude,Latitude);
			 * infoLogger.info("Address is"+Address); } }catch (Exception e) {
			 * e.printStackTrace(); }
			 */

			AssetClassDimensionEntity Asset_Class_Id = null;
			//Code change for latest asset class dimension Id DF:2013-01-07
			Query query11 = session
					.createQuery("from AssetClassDimensionEntity b where b.assetClassDimensionId=(select max(a.assetClassDimensionId) from AssetClassDimensionEntity a where a.productId=(select productId from AssetEntity where serial_number='"+SerialNumber+"'))");
			Iterator itr11 = query11.list().iterator();
			if (itr11.hasNext()) {
				AssetClassDimensionEntity assetClassDimensionEntity = (AssetClassDimensionEntity) itr11
						.next();
				Asset_Class_Id = assetClassDimensionEntity;
			}
			iLogger.info("Asset_Class_Id " + Asset_Class_Id);
			AssetMonitoringFactDataEntity assetMonitoringFactDataEntity = new AssetMonitoringFactDataEntity();
			assetMonitoringFactDataEntity.setTimeKey(Time_Key);
			assetMonitoringFactDataEntity.setTenancyId(tenancy_dimension_id);
			assetMonitoringFactDataEntity
					.setAccountDimensionId(accountDimensionId);
			assetMonitoringFactDataEntity.setSerialNumber(SerialNumber);
			assetMonitoringFactDataEntity.setMachineHours(Double.valueOf(df2.format((Double)(MachineHours))));
			assetMonitoringFactDataEntity.setFuelUsedIdle(FuelIdeal);
			assetMonitoringFactDataEntity.setFuelUsedWorking(FuelWorking);
			assetMonitoringFactDataEntity.setLastReported(LastReported);
			assetMonitoringFactDataEntity.setLastEngineRun(Last_Engine_Run);
			assetMonitoringFactDataEntity.setLocation(Location);
			assetMonitoringFactDataEntity
					.setEngineOffHours(Double.valueOf(df2.format((Double)(EngineOffHours))));
			assetMonitoringFactDataEntity
					.setEngineRunningBand1(EngineRunningBand1);
			assetMonitoringFactDataEntity
					.setEngineRunningBand2(EngineRunningBand2);
			assetMonitoringFactDataEntity
					.setEngineRunningBand3(EngineRunningBand3);
			assetMonitoringFactDataEntity
					.setEngineRunningBand4(EngineRunningBand4);
			assetMonitoringFactDataEntity
					.setEngineRunningBand5(EngineRunningBand5);
			assetMonitoringFactDataEntity
					.setEngineRunningBand6(EngineRunningBand6);
			assetMonitoringFactDataEntity
					.setEngineRunningBand7(EngineRunningBand7);
			assetMonitoringFactDataEntity
					.setEngineRunningBand8(EngineRunningBand8);
			assetMonitoringFactDataEntity
					.setAssetClassDimensionId(Asset_Class_Id);
			assetMonitoringFactDataEntity.setMachineName(NickName);
			assetMonitoringFactDataEntity.setAddress(Address);
			session.save("AssetMonitoringFactDataDayAgg",
					assetMonitoringFactDataEntity);

			iLogger.info("I am working well");
		} catch (Exception e) {
			fLogger.fatal("In Database ,value are not there "
					+ e.getMessage());
			e.printStackTrace();
		} finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}

	}

	// Added Code for Updation of tenancy ID

	public void updateTenancyId(String queryString, String tableName) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		
		try {
			Query query = session.createQuery(queryString);
			Iterator itr = query.list().iterator();
			String exist = null;
			while (itr.hasNext()) {
				exist = (String) itr.next();
			}
			// If there exist record in Olap Fact Table, then Update Tenancy_ID

			if (exist != null) {
				TenancyDimensionEntity tenancyId = null;
				Query query09 = session
						.createQuery("select a.serialNumber,a.accountDimensionId,b.tenancy_id From "
								+ tableName
								+ " a,AccountTenancyMapping b "
								+ " where IFNULL( a.tenancyId,0)=0 and NOT IFNULL( b.tenancy_id,0)=0  ");
				Iterator iterator = query09.list().iterator();
				Object result[] = null;
				String OlapSerialNumber = null;
				int Account_Id = 0;
				AccountDimensionEntity accountDimension = null;
				while (iterator.hasNext()) {
					TenancyEntity tenancy = null;
					result = (Object[]) iterator.next();
					OlapSerialNumber = (String) result[0];
					accountDimension = ((AccountDimensionEntity) result[1]);
					Account_Id = accountDimension.getAccountId();

					tenancy = (TenancyEntity) result[2];

					Query query11 = session
							.createQuery("from TenancyDimensionEntity a where a.tenancyId="
									+ tenancy.getTenancy_id());
					TenancyDimensionEntity tenancyDimensionId = null;
					Iterator itr11 = query11.list().iterator();
					while (itr11.hasNext()) {
						TenancyDimensionEntity tenancyDimensionEntity = (TenancyDimensionEntity) itr11
								.next();
						tenancyDimensionId = tenancyDimensionEntity;
					}

					Query updateQuery = session.createQuery("UPDATE "
							+ tableName + " SET tenancyId="
							+ tenancyDimensionId.getTenacy_Dimension_Id()
							+ " WHERE accountDimensionId= "
							+ accountDimension.getAccountDimensionId()
							+ " and serialNumber= '" + OlapSerialNumber + "'");

					iLogger.info("query for updation :" + updateQuery);
					int rows = updateQuery.executeUpdate();

				}
			}
		} catch (Exception e) {
			fLogger.fatal("In Database ,value are not there "
					+ e.getMessage());
			e.printStackTrace();
		} finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}

	}

	// *** Start of updateTenancyIdInFactTables
	/**
	 * This Method will allow to Update TenancyId in Fact Tables.
	 * 
	 * @return Success if updated Successfully
	 */
	public String updateTenancyIdInFactTables() {
		// Update TenancyId for Day,Week ,Month, Quarter,Year Fact Table
		updateTable("select serialNumber From AssetMonitoringFactDataDayAgg",
				"AssetMonitoringFactDataDayAgg");
		updateTenancyId(
				"select serialNumber From AssetMonitoringFactDataWeekAgg",
				"AssetMonitoringFactDataWeekAgg");
		updateTenancyId(
				"select serialNumber From AssetMonitoringFactDataMonthAgg",
				"AssetMonitoringFactDataMonthAgg");
		updateTenancyId(
				"select serialNumber From AssetMonitoringFactDataQuarterAgg",
				"AssetMonitoringFactDataQuarterAgg");
		updateTenancyId(
				"select serialNumber From AssetMonitoringFactDataYearAgg",
				"AssetMonitoringFactDataYearAgg");

		return "Success";

	}

	// *** End of updateTenancyIdInFactTables

	public String setETLfactData() {
		long startTime = 0l;
		long endTime = 0l;
		Session session = HibernateUtil.getSessionFactory().openSession();
		// session.beginTransaction();
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		
		try {
			iLogger.info("ETLfactDataBO: setETL start: "
					+ simpleDtStr.format(Calendar.getInstance().getTime())); // DF1409.n
			startTime = System.currentTimeMillis();

			// Step to find RPM band list
			Query rpmQuery = session
					.createQuery("from RpmBands where Engine_Status like 'Idle'");
			Iterator rpmIterator = rpmQuery.list().iterator();

			List<Integer> IdleBand = new LinkedList<Integer>();

			while (rpmIterator.hasNext()) {
				RpmBands rpmBands = (RpmBands) rpmIterator.next();
				IdleBand.add(rpmBands.getBand_ID());
			}

			// DF1409.o

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			List<String> serailNumberList = new LinkedList<String>();
			ListToStringConversion conversionObj = new ListToStringConversion();

			// Step1: Get all distinct serial Number from Olap

			Query query = session
					.createQuery("select distinct(serialNumber) From AssetMonitoringFactDataDayAgg ");
			Iterator iterator = query.list().iterator();

			if (iterator.hasNext()) {
				while (iterator.hasNext()) {
					serailNumberList.add((String) iterator.next());
				}

				endTime = System.currentTimeMillis();

				// DF1409.so
				/*
				 * infoLogger.info("distinct serial Number from Olap" +
				 * serailNumberList.size()); infoLogger.info(
				 * "Step1: Get all distinct serial Number from Olap Execution Time in ms:"
				 * + (endTime - startTime));
				 */
				// DF1409.eo

				int existingVinListsize = serailNumberList.size();

				// DF1409.sn
				iLogger
						.info("ETLfactDataBO: Distinct serial Numbers from Olap "
								+ existingVinListsize);
				iLogger
						.info("ETLfactDataBO: Get distinct serial Number Time in ms: "
								+ (endTime - startTime));
				iLogger.info("ETLfactDataBO: setETL After unique PIN list: "
						+ simpleDtStr.format(Calendar.getInstance().getTime()));
				// DF1409.en

				String serailNumberStringList = conversionObj.getStringList(
						serailNumberList).toString();
				/*
				 * //Step2 : Get max olap date startTime =
				 * System.currentTimeMillis(); Query maxOlapQuery =
				 * session.createQuery
				 * ("select max(timeKey) From AssetMonitoringFactDataDayAgg ");
				 * Iterator maxOlapIterator=maxOlapQuery.list().iterator();
				 * Timestamp maxOlapDate=null; while(maxOlapIterator.hasNext())
				 * { maxOlapDate=(Timestamp)maxOlapIterator.next(); }
				 * endTime=System.currentTimeMillis();
				 * infoLogger.info("Get max olap date"+maxOlapDate);
				 * System.out
				 * .println("Step2 : Get max olap date Execution Time in ms:"
				 * +(endTime-startTime));
				 */

				// Step3 :Update and insert existing VIN in olap
				startTime = System.currentTimeMillis();

				iLogger.info("ETLfactDataBO: Existing PINs start: "
						+ simpleDtStr.format(Calendar.getInstance().getTime())); // DF1409.n

				int i = 0;
				do {
					// Step3 a) : Get Max olap date for each VIN
					iLogger.info("ETLfactDataBO:  Existing PIN processed: "
							+ serailNumberList.get(i)
							+ " | "
							+ simpleDtStr.format(Calendar.getInstance()
									.getTime()));

					Query maxSerialNumberOlapQuery = session
							.createQuery("select max(timeKey) "
									+ "From AssetMonitoringFactDataDayAgg "
									+ "where serialNumber='"
									+ serailNumberList.get(i) + "'");
					Iterator maxSerialNumberIterator = maxSerialNumberOlapQuery
							.list().iterator();

					Timestamp maxSerialNumberOlapDate = null;

					while (maxSerialNumberIterator.hasNext()) {
						maxSerialNumberOlapDate = (Timestamp) maxSerialNumberIterator
								.next();
					}

					// DF1409.so
					/*
					 * infoLogger.info("Get Max olap date for each VIN" +
					 * maxSerialNumberOlapDate);
					 */
					// DF1409.eo

					Date serialNumberOlapDate = null;

					try {
						serialNumberOlapDate = dateFormat
								.parse(simpleDateFormat
										.format(maxSerialNumberOlapDate));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// serialNumberOlapDate IN yyyy-MM-dd
					String serialNumberOlapDate1 = null;
					serialNumberOlapDate1 = dateFormat
							.format(serialNumberOlapDate);
					Date serialNumberOlapDateWithoutTime = null;

					try {
						serialNumberOlapDateWithoutTime = dateFormat
								.parse(serialNumberOlapDate1);
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// Step3 b) : Get List distinct Transaction Timestamp for
					// given VIN

					Query distinctTransactionTimestampQuery = session
							.createQuery("select distinct(date(a.transactionTime)) "
									+ "from AssetMonitoringHeaderEntity a "
									+ "where a.serialNumber = '"
									+ serailNumberList.get(i)
									+ "' "
									+ "and a.transactionTime >='"
									+ maxSerialNumberOlapDate
									+ "' "
									+ "order by a.transactionTime asc ");

					Iterator distinctTransactionTimestampIterator = distinctTransactionTimestampQuery
							.list().iterator();
					List<Timestamp> distinctTransactionTimestampDate = new LinkedList<Timestamp>();

					while (distinctTransactionTimestampIterator.hasNext()) {
						Timestamp timeStampDate = new Timestamp(
								((Date) distinctTransactionTimestampIterator
										.next()).getTime());
						String Timekey = simpleDateFormat.format(timeStampDate);
						Timestamp ts = Timestamp.valueOf(Timekey);
						distinctTransactionTimestampDate.add(ts);
					}

					// Step3 c) : Update and Insert given VIN in olap for
					// distinct Transaction Timestamp obtained in Step3 b)

					int countTransactionTimestampDate = distinctTransactionTimestampDate
							.size();

					// DF1409.sn
					iLogger
							.info("ETLfactDataBO:  No. of dates to process for: "
									+ serailNumberList.get(i)
									+ " | "
									+ countTransactionTimestampDate);
					// DF1409.en

					// DF1409.so
					/*
					 * infoLogger.info("countTransactionTimestampDate  :" +
					 * countTransactionTimestampDate);
					 */
					// DF1409.so

					int j = 0;
					do {
						// DF1409.o infoLogger.info("j :" + j);

						String Timekey = simpleDateFormat
								.format(distinctTransactionTimestampDate.get(j));

						Date Time_key1 = null;
						try {
							Time_key1 = dateFormat.parse(Timekey);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// distinctTransactionTimestampDate IN yyyy-MM-dd
						String TimekeyUpdate = dateFormat
								.format(distinctTransactionTimestampDate.get(j));

						Date Time_key = null;
						try {
							Time_key = dateFormat.parse(TimekeyUpdate);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// Check if transaction timestamp is equal to
						// maxOlapDate for SerialNumber ,if not then Insert else
						// Update

						// DF1409.so
						/*
						 * infoLogger.info("serialNumberOlapDate :" +
						 * serialNumberOlapDate);
						 */
						// DF1409.eo

						if (Time_key.compareTo(serialNumberOlapDate) > 0) {
							// DF1409.so
							// DF1409.eo
							
							Timestamp timekey = Timestamp.valueOf(Timekey);
							// Logic for Insertion : timekey in yyyy-MM-dd
							// HH:mm:ss format

							// DF1409.sn
							iLogger.info("ETLfactDataBO:  Date to process for: "
									+ serailNumberList.get(i)
									+ " | "
									+ timekey);
							// DF1409.en
							
							InsertFact(timekey, serailNumberList.get(i),
									IdleBand, true);

							serialNumberOlapDateWithoutTime = Time_key;
							serialNumberOlapDate1 = TimekeyUpdate;
							serialNumberOlapDate = Time_key;

						} else {// Logic for Updation
							// DF1409.so
							// DF1409.eo
							
							if (TimekeyUpdate
									.equalsIgnoreCase(serialNumberOlapDate1)) {
								// DF1409.o

								Query queryNew1 = session
										.createQuery(" From AssetMonitoringFactDataDayAgg a "
												+ "where a.serialNumber='"
												+ serailNumberList.get(i)
												+ "' "
												+ "and a.timeKey < '"
												+ TimekeyUpdate + "'");
								Iterator iterator7 = queryNew1.list()
										.iterator();

								if (iterator7.hasNext()) {
									Query queryNew12 = session
											.createQuery(" From AssetMonitoringFactDataDayAgg a "
													+ "where a.serialNumber='"
													+ serailNumberList.get(i)
													+ "' "
													+ "and a.timeKey = '"
													+ TimekeyUpdate + "'");
									Iterator iterator72 = queryNew12.list()
											.iterator();

									if (iterator72.hasNext()) {
										AssetMonitoringFactDataEntity assetMonitoringFactDataEntity = (AssetMonitoringFactDataEntity) iterator72
												.next();
										// TimekeyUpdate in yyyy-MM-dd format
										
										// DF1409.sn
										iLogger.info("ETLfactDataBO:  Date to process for: "
												+ serailNumberList.get(i)
												+ " | "
												+ TimekeyUpdate);
										// DF1409.en
										
										UpdateFact(TimekeyUpdate,
												serailNumberList.get(i),
												assetMonitoringFactDataEntity,
												IdleBand, true);
									}
								} else {
									// DF1409.so
									/*
									System.out
											.println("Logic for Updation with true");
									*/
									// DF1409.eo
									
									Query queryNew = session
											.createQuery(" From AssetMonitoringFactDataDayAgg a "
													+ "where a.serialNumber='"
													+ serailNumberList.get(i)
													+ "' "
													+ "and a.timeKey = '"
													+ TimekeyUpdate + "'");
									Iterator iterators = queryNew.list()
											.iterator();
									if (iterators.hasNext()) {
										AssetMonitoringFactDataEntity assetMonitoringFactDataEntity = (AssetMonitoringFactDataEntity) iterators
												.next();
										// TimekeyUpdate in yyyy-MM-dd format
										
										// DF1409.sn
										iLogger.info("ETLfactDataBO:  Date to process for: "
												+ serailNumberList.get(i)
												+ " | "
												+ TimekeyUpdate);
										// DF1409.en
										
										UpdateFact(TimekeyUpdate,
												serailNumberList.get(i),
												assetMonitoringFactDataEntity,
												IdleBand, false);
									}
									
									serialNumberOlapDateWithoutTime = Time_key;
									serialNumberOlapDate = Time_key;
									serialNumberOlapDate1 = TimekeyUpdate;
								}
								serialNumberOlapDateWithoutTime = Time_key;
								serialNumberOlapDate = Time_key;
								serialNumberOlapDate1 = TimekeyUpdate;

								// DF1409.so
								/*
								infoLogger.info("changed olap date"
										+ serialNumberOlapDate);
								*/
								// DF1409.eo
							}

						}
						j++;
						// countTransactionTimestampDate--;
					} while (j < countTransactionTimestampDate);

					i++;

				} while (i < existingVinListsize);

				endTime = System.currentTimeMillis();

				// DF1409.so
				/*
				 * System.out .println(
				 * "Step3 :Update and insert existing VIN in olap Execution Time in ms:"
				 * + (endTime - startTime));
				 */
				// DF1409.eo

				iLogger.info("ETLfactDataBO: Existing PINs end: "
						+ simpleDtStr.format(Calendar.getInstance().getTime())); // DF1409.n
				iLogger
						.info("ETLfactDataBO: Existing PINs up/in Time in ms: "
								+ (endTime - startTime)); // DF1409.n

				// Step4 : Get VIN list which doesn't exist in OLAP

				iLogger.info("ETLfactDataBO: New Addl PINs start: "
						+ simpleDtStr.format(Calendar.getInstance().getTime())); // DF1409.n

				startTime = System.currentTimeMillis();

				Query newVinsQuery = session
						.createQuery("select distinct(date(a.transactionTime)),a.serialNumber "
								+ "from AssetMonitoringHeaderEntity a "
								+ "where a.serialNumber not in ("
								+ serailNumberStringList
								+ ") "
								+ "order by a.serialNumber asc");
				Iterator newVinsIterator = newVinsQuery.list().iterator();

				Object[] result = null;
				// String serialNumberPrevious=null;
				// String serialNumberOlapDatetemp=null;

				while (newVinsIterator.hasNext()) {
					result = (Object[]) newVinsIterator.next();
					Date newTransactionTimestamp = (Date) result[0];
					// yyyy-MM-dd HH:mm:ss format
					String Timekey = simpleDateFormat.format(new Timestamp(
							newTransactionTimestamp.getTime()));
					AssetEntity asset = (AssetEntity) result[1];
					Timestamp ts = Timestamp.valueOf(Timekey);

					String TimekeyUpdate1 = dateFormat.format(ts);
					Query checkpreQuery = session
							.createQuery(" from AssetMonitoringHeaderEntity a where a.serialNumber='"
									+ asset.getSerial_number()
											.getSerialNumber()
									+ "' and a.transactionTime <'"
									+ TimekeyUpdate1 + "'");
					Iterator checkpreIterator = checkpreQuery.list().iterator();

					if (checkpreIterator.hasNext())
						// Logic for Insertion :serialNumberOlapDate1 in
						// yyyy-MM-dd format and ts in yyyy-MM-dd HH:mm:ss
						// format
						InsertFact(ts, asset.getSerial_number()
								.getSerialNumber(), IdleBand, true);
					else
						InsertFact(ts, asset.getSerial_number()
								.getSerialNumber(), IdleBand, false);

					/*
					 * if((asset.getSerial_number().getSerialNumber()).
					 * equalsIgnoreCase(serialNumberPrevious)) {
					 * 
					 * // insert=0; if(! (session.isOpen() )) { session =
					 * HibernateUtil.getSessionFactory().getCurrentSession();
					 * session.getTransaction().begin(); }
					 * 
					 * 
					 * Query serialNumberPreviousQuery=session.createQuery(
					 * "select b.timeKey,b.serialNumber from AssetMonitoringFactDataDayAgg b where b.timeKey =(select max(a.timeKey) From AssetMonitoringFactDataDayAgg a where a.serialNumber='"
					 * +serialNumberPrevious+"')" ) ;
					 * 
					 * Iterator sn1=serialNumberPreviousQuery.list().iterator();
					 * Object res[]=null; while(sn1.hasNext()) { // Timestamp
					 * maxSerialNumberOlapDate1
					 * =(Timestamp)serialNumberPreviousIterator.next();
					 * res=(Object[])sn1.next(); Timestamp
					 * maxSerialNumberOlapDate1=(Timestamp)res[0];
					 * infoLogger.info
					 * ("maxSerialNumberOlapDate1 : "+maxSerialNumberOlapDate1);
					 * String
					 * TimekeyUpdate1=dateFormat.format(maxSerialNumberOlapDate1
					 * ); Query updateQuery = session.createQuery(
					 * " From AssetMonitoringFactDataDayAgg a where a.serialNumber='"
					 * +
					 * serialNumberPrevious+"' and a.timeKey = '"+TimekeyUpdate1
					 * + "'"); Iterator updateIterator =
					 * updateQuery.list().iterator(); if
					 * (updateIterator.hasNext()) {
					 * AssetMonitoringFactDataEntity
					 * assetMonitoringFactDataEntity =
					 * (AssetMonitoringFactDataEntity) updateIterator .next();
					 * //ts1 in yyyy-MM-dd format
					 * UpdateFact(TimekeyUpdate1,serialNumberPrevious
					 * ,assetMonitoringFactDataEntity,IdleBand,true);
					 * 
					 * } }
					 * 
					 * 
					 * insert=0; InsertFact( ts ,
					 * asset.getSerial_number().getSerialNumber(), IdleBand ,
					 * true);
					 * 
					 * } else { insert=1; }
					 */
					// serialNumberPrevious=asset.getSerial_number().getSerialNumber();
				}

				endTime = System.currentTimeMillis();

				// DF1409.so
				/*
				 * System.out .println(
				 * "Step4 : Get VIN list which doesn't exist in OLAP. Execution Time in ms:"
				 * + (endTime - startTime));
				 */
				// DF1409.eo

				iLogger.info("ETLfactDataBO: New Addl PINs end: "
						+ simpleDtStr.format(Calendar.getInstance().getTime())); // DF1409.n
				iLogger.info("ETLfactDataBO: New Addl PINs Time in ms: "
						+ (endTime - startTime)); // DF1409.n
			}
			// If olap is empty, then From AMH select all VINs
			else {

				iLogger.info("ETLfactDataBO: OLAP empty start: "
						+ simpleDtStr.format(Calendar.getInstance().getTime())); // DF1409.n

				startTime = System.currentTimeMillis();

				Query newVinsQuery = session
						.createQuery("select distinct(date(a.transactionTime)),a.serialNumber "
								+ "from AssetMonitoringHeaderEntity a "
								+ "order by a.serialNumber, date(a.transactionTime) asc");
				Iterator newVinsIterator = newVinsQuery.list().iterator();

				Object[] result = null;
				String serialNumberPrevious = null;
				String serialNumberOlapDatetemp = null;

				int insert = 1;

				while (newVinsIterator.hasNext()) {
					result = (Object[]) newVinsIterator.next();
					Date newTransactionTimestamp = (Date) result[0];
					AssetEntity asset = (AssetEntity) result[1];

					// yyyy-MM-dd HH:mm:ss format
					String Timekey = simpleDateFormat.format(new Timestamp(
							newTransactionTimestamp.getTime()));
					Timestamp ts = Timestamp.valueOf(Timekey);
					String TimekeyUpdate1 = dateFormat.format(ts);

					Query checkpreQuery = session
							.createQuery(" from AssetMonitoringHeaderEntity a where a.serialNumber='"
									+ asset.getSerial_number()
											.getSerialNumber()
									+ "' and a.transactionTime <'"
									+ TimekeyUpdate1 + "'");
					Iterator checkpreIterator = checkpreQuery.list().iterator();

					if (checkpreIterator.hasNext())
						// Logic for Insertion :serialNumberOlapDate1 in
						// yyyy-MM-dd format and ts in yyyy-MM-dd HH:mm:ss
						// format
						InsertFact(ts, asset.getSerial_number()
								.getSerialNumber(), IdleBand, true);
					else
						InsertFact(ts, asset.getSerial_number()
								.getSerialNumber(), IdleBand, false);

					/*
					 * if(insert==1) { //Logic for Insertion
					 * :serialNumberOlapDate1 in yyyy-MM-dd format and ts in
					 * yyyy-MM-dd HH:mm:ss format InsertFact( ts ,
					 * asset.getSerial_number().getSerialNumber(), IdleBand ,
					 * false); }
					 * 
					 * if((asset.getSerial_number().getSerialNumber()).
					 * equalsIgnoreCase(serialNumberPrevious)) {
					 * 
					 * InsertFact( ts ,
					 * asset.getSerial_number().getSerialNumber(), IdleBand ,
					 * true);
					 * 
					 * } else { insert=1; }
					 * serialNumberPrevious=asset.getSerial_number
					 * ().getSerialNumber();
					 */
				}

				endTime = System.currentTimeMillis();
				// DF1409.so
				/*
				 * System.out .println(
				 * "If olap is empty, then From AMH select all VINs. Execution Time in ms:"
				 * + (endTime - startTime));
				 */
				// DF1409.eo

				iLogger.info("ETLfactDataBO: OLAP empty end: "
						+ simpleDtStr.format(Calendar.getInstance().getTime())); // DF1409.n
				iLogger.info("ETLfactDataBO: OLAP empty Time in ms: "
						+ (endTime - startTime)); // DF1409.n

			}

		} finally {
			/*
			 * if (session.getTransaction().isActive()) {
			 * session.getTransaction().commit(); }
			 */

			iLogger.info("ETLfactDataBO: setETL end:"
					+ simpleDtStr.format(Calendar.getInstance().getTime())); // DF1409.n

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}
		return "success";
	}

	// ****************************************** Code for updation
	// ************************************
	public void updateTable(String queryString, String tableName) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		try {
			// get Client Details
			Properties prop = new Properties();
			String clientName = null;

			prop.load(getClass().getClassLoader().getResourceAsStream(
					"remote/wise/resource/properties/configuration.properties"));
			clientName = prop.getProperty("ClientName");

			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj
					.getClientEntity(clientName);
			// END of get Client Details

			if (!(session.isOpen())) {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			Query query = session.createQuery(queryString);
			Iterator itr = query.list().iterator();
			int exist = 0;
			while (itr.hasNext()) {
				exist = (Integer) itr.next();
			}
			// If there exist record in Olap Fact Table, then Update
			// AssetClassDimensionId
			if (exist != 0) {
				AssetClassDimensionEntity assetClassDimensionId1 = null;
				Query query09 = session
						.createQuery("select distinct(serialNumber) From "
								+ tableName + " where assetClassDimensionId ="
								+ assetClassDimensionId1 + " ");
				Iterator iterator = query09.list().iterator();
				String OlapSerialNumber = null;
				while (iterator.hasNext()) {
					OlapSerialNumber = (String) iterator.next();
					int flag = 0;
					Query query12 = session
							.createQuery("select productId from AssetEntity where serial_number='"
									+ OlapSerialNumber
									+ "'and active_status=true and client_id="
									+ clientEntity.getClient_id());
					ProductEntity product = null;
					Iterator itr122 = query12.list().iterator();
					if (itr122.hasNext()) {
						product = (ProductEntity) itr122.next();
						flag = 1;
					}
					if (flag == 1) {

						Query query11 = session
								.createQuery("from AssetClassDimensionEntity a where a.productId=(select productId from AssetEntity where serial_number='"
										+ OlapSerialNumber
										+ "'and active_status=true and client_id="
										+ clientEntity.getClient_id() + ")");
						AssetClassDimensionEntity assetClassDimensionId = null;
						Iterator itr11 = query11.list().iterator();
						while (itr11.hasNext()) {
							AssetClassDimensionEntity assetClassDimensionEntity = (AssetClassDimensionEntity) itr11
									.next();
							assetClassDimensionId = assetClassDimensionEntity;
						}

						Query updateQuery = session.createQuery("UPDATE "
								+ tableName
								+ " SET assetClassDimensionId="
								+ assetClassDimensionId
										.getAssetClassDimensionId() + ""
								+ " WHERE serialNumber= '" + OlapSerialNumber
								+ "'");

						iLogger.info("query for updation :" + updateQuery);
						int rows = updateQuery.executeUpdate();

					}
				}
			}

		} catch (Exception e) {
			fLogger.fatal("In Database ,value are not there "
					+ e.getMessage());
			e.printStackTrace();
		} finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}

	}

	// ****************************************** Code for updation
	// ************************************
	// **************************************Start of UpdateRemoteFact
	// *********************************************************
	/**
	 * This Method will allow to Update AssetClassDimensionId in Fact Tables.
	 * 
	 * @return Success if updated Successfully
	 */
	public String updateRemoteFact() {
		// Update AssetClassDimensionId for Day,Week ,Month, Quarter,Year Fact
		// Table
		updateTable("select max(timeKey) From AssetMonitoringFactDataDayAgg",
				"AssetMonitoringFactDataDayAgg");
		updateTable(
				"select max(timeCount) From AssetMonitoringFactDataWeekAgg",
				"AssetMonitoringFactDataWeekAgg");
		updateTable(
				"select max(timeCount) From AssetMonitoringFactDataMonthAgg",
				"AssetMonitoringFactDataMonthAgg");
		updateTable(
				"select max(timeCount) From AssetMonitoringFactDataQuarterAgg",
				"AssetMonitoringFactDataQuarterAgg");
		updateTable("select max(year) From AssetMonitoringFactDataYearAgg",
				"AssetMonitoringFactDataYearAgg");

		return "Success";

	}
	// **************************************End of UpdateRemoteFact
	// *********************************************************

	//*** start of setETLfactDataNew *** DF1409.sn
	
		public String setETLfactDataNew() {
			
			Logger iLogger = InfoLoggerClass.logger;
	    	Logger fLogger = FatalLoggerClass.logger;
			
			int count2Process = 0;
			int countProcessed = 0;
			long startTime = 0l;
			long endTime = 0l;

			String vinProcessed = "";
			String vin2Process = "";
			
			Timestamp maxOlapDt = null;
			Timestamp lastOlapDt = null;
			Timestamp prevlastOlapDt = null;
			Timestamp tranTime = null;
			Object[] result = null;

			AssetEntity vinIdentified = null;
			
			Session session = HibernateUtil.getSessionFactory().openSession();
			
			try {
				iLogger.info("ETLfactDataBO: setETL start: "
						+ simpleDtStr.format(Calendar.getInstance().getTime()));
			
				startTime = System.currentTimeMillis();
				getGenericinfo( session );
				endTime = System.currentTimeMillis();
				iLogger.info("ETLfactDataBO: Get generic data time in ms: "
						+ (endTime - startTime));

				startTime = System.currentTimeMillis();
				
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
				
				lastOlapDt = maxOlapDt; //Initialize to max OLAP date
				prevlastOlapDt = maxOlapDt; //Initialize to max OLAP date
				//2016-03-23 : Going by the segment ID of the AssetTable  for a better performance - Deepthi
				if ( maxOlapDt.after(Timestamp.valueOf("2011-12-31 23:59:00") ) ) {
					
					 int counter = 1;
				        int maxSegmentID = 0;
				        Query segmentQuery = session.createQuery("select max(segmentId) from AssetEntity");
				        Iterator segmentIterator = segmentQuery.list().iterator();
				        while (segmentIterator.hasNext()) {
				            maxSegmentID = ((Integer)segmentIterator.next()).intValue();
				          }
				        iLogger.info("ETLfactDataBO: Number of segments---" + maxSegmentID);
				        while (counter <= maxSegmentID){
				     // query fix - multiple occurences of where in the below query - Deepthi -2016-03-24
				        	iLogger.info("ETLfactDataBO: Running Segment" + counter+"........");
					qry = session.createQuery( "select a.serialNumber, max(a.transactionTime) " 
											+ " from AssetMonitoringHeaderEntity a "
											+ " where   a.segmentId= "+counter+ " and a.createdTimestamp >= '"
													+ maxOlapDt + "'"
											+ " group by a.serialNumber, date(a.transactionTime) "
											+ " order by a.serialNumber, a.transactionTime");
					lst = qry.list();
					itr = lst.iterator();
					count2Process = lst.size();
					
					endTime = System.currentTimeMillis();

					iLogger.info("ETLfactDataBO: Segment " + counter+" has "+count2Process+" records");
					
					iLogger.info("ETLfactDataBO: Get distinct serial Number for segement "+counter+" query executed time in ms: "
						+ (endTime - startTime));
					iLogger.info("ETLfactDataBO: After unique PIN list: "
						+ simpleDtStr.format(Calendar.getInstance().getTime()));
					iLogger.info("ETLfactDataBO: Last OLAP dt "
						+ lastOlapDt);
					iLogger.info("ETLfactDataBO: Distint VIN count "
						+ count2Process);
		
					while (itr.hasNext()) 
					{
						result = (Object[]) itr.next();
						
						//DF20141211 - Rajani Nagaraju - Continue with next set of VINs if the exception is thrown for a VIN - Hence try-catch block
						try
						{
							vinIdentified = (AssetEntity) result[0];
							tranTime = (Timestamp) result[1];

							vin2Process = vinIdentified.getSerial_number().getSerialNumber();
						
							startTime = System.currentTimeMillis();
							getVINspecificinfo(session, vin2Process, tranTime);
							endTime = System.currentTimeMillis();
							iLogger.info("ETLfactDataBO: Get VIN specific data for "
								+ vin2Process
		 						+ " Time in ms: "
								+ (endTime - startTime));
						
							if ( !(vin2Process.equalsIgnoreCase(vinProcessed)) ) {
								PreviousDayBandList.clear();
							
								qry = session.createQuery(" select a.timeKey  " +
										" from AssetMonitoringFactDataDayAgg a" +
										" where a.serialNumber = '" 
											+ vin2Process + "'"
										+ "order by a.timeKey desc"
										).setFirstResult(0).setMaxResults(2);
								lst = qry.list();
								Iterator itr1 = lst.iterator();
							
								if ( (itr1.hasNext()) && (lst.size() > 0) && (lst.get(0) != null) ) {
									while (itr1.hasNext()) {
										lastOlapDt = (Timestamp) itr1.next();
										if (itr1.hasNext()) {
											prevlastOlapDt = (Timestamp) itr1.next();
										}
									}
								}
							
								//DF20140121 - Rajani Nagaraju - If the Machine is communicating for the first time and Rolled off on the same day (
								//At this point lastOlapDt and tranTime would be the same so it would go inside update branch), then it should insert the record into day fact and not try to update 
								if(lst.size()==0)
								{
									Calendar cal  = Calendar.getInstance();
									cal.setTime(tranTime);
									cal.add(Calendar.DATE, -1);
									//set last OLAP date to 1 day previous to txn date so that it goes to the insertion block
									lastOlapDt = new Timestamp(cal.getTime().getTime());
									
									iLogger.info("ETLfactDataBO: VIN Communicating for the First Time after OLAP Hit. lastOlapDt modified to :"+lastOlapDt);
								}
							}
						
							// If transaction time is before last Olap dt, it should not exist in Asset Monitoring Header.
							// AssetDiagnostics service will reject such records. Hence, below check.
						
							if ( lastOlapDt.before(tranTime) || (lastOlapDt.equals(tranTime)) ) {
								startTime = System.currentTimeMillis();
	
								if ( (dateStr.format(lastOlapDt)).equalsIgnoreCase(dateStr.format(tranTime)) ) {
									iLogger.info("ETLfactDataBO: VIN for update"
											+ vin2Process
											+ " Tran Dt: " 
											+ dateStr.format(tranTime)
											+ " Last Olap Date: "
											+ dateStr.format(lastOlapDt));
									PreviousDayBandList.clear();
									insertOlap(vin2Process, tranTime, lastOlapDt, prevlastOlapDt, true);
								} else {
									iLogger.info("ETLfactDataBO: VIN for insert"
											+ vin2Process
											+ " Tran Dt: " 
											+ dateStr.format(tranTime)
											+ " Last Olap Date: "
											+ dateStr.format(lastOlapDt));
									insertOlap(vin2Process, tranTime, lastOlapDt, prevlastOlapDt, false);					
								}

								vinProcessed = vin2Process;
								endTime = System.currentTimeMillis();
								iLogger.info("ETLfactDataBO: VIN Processed"
									+ vinProcessed
									+ " Tran Time: " 
									+ tranTime
									+ " Time in ms: "
									+ (endTime - startTime)
									+ " | "
									+ simpleDtStr.format(Calendar.getInstance()
											.getTime()));	
							} else {
								vinProcessed = vin2Process;
								endTime = System.currentTimeMillis();
								iLogger.info("ETLfactDataBO: VIN / Tran skipped"
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
								fLogger.fatal("ETLfactDataBO: Fatal error processing VIN: "
										+ vin2Process + " " + tranTime + " "
										+ " Message: "
										+ e.getMessage());
							}
							
						
						} // End of while loop for itrVin2Process.hasNext()
					counter++;
				}
					
					
				} // End of if - lastOlapDt check
			} // End of Try block

			catch (Exception e) {
				fLogger.fatal("ETLfactDataBO: Fatal error processing VIN: "
						+ vin2Process + " " + tranTime + " "
						+ "Total Processed: "
						+ countProcessed
						+ " Message: "
						+ e.getMessage());
				e.printStackTrace();
			} 
			
			finally {
				iLogger.info("ETLfactDataBO: setETL end: "
						+ "Total Processed: "
						+ countProcessed
						+ " End time: "
						+ simpleDtStr.format(Calendar.getInstance().getTime()));
				if (session.isOpen()) {
					session.flush();
					session.close();
				}
			}
			return "success";
		}
		
	//*** end of setETLfactDataNew ***
		
	//*** start of getGenericinfo ***
		public void getGenericinfo(Session session1) {
			
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
							+ " group by a.parameterName"
							+ " order by a.parameterId").list().iterator();

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
				fLogger.fatal("ETLfactDataBO: Fatal error getting generic data"
						+ e.getMessage());
				e.printStackTrace();
			} 
			
			finally {
				session1 = null;
			}
		}
			
	//*** end of getGenericinfo ***
		
	//*** start of getVINspecificinfo ***
		public void getVINspecificinfo(Session session1,
						String Serial, 
						Timestamp runDt) {
			
	    	Logger fLogger = FatalLoggerClass.logger;
			
			int oldAccountId = 0;
			
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
					/*qry = session1.createQuery("from TenancyDimensionEntity " +
									"where tenancyId = " +
									"(select tenancy_id " +
									"from AccountTenancyMapping " +
									"where account_id="
									+ accountId + ")");*/
					itr = qry.list().iterator();

					if (itr.hasNext()) {
						while(itr.hasNext())
						{
						TenancyDimensionEntity tenancyDimensionEntity = (TenancyDimensionEntity) itr.next();
						tenancy_dimension_id = tenancyDimensionEntity;
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
				}
				//Code change for latest asset class dimension Id DF:2013-01-07
				qry = session1.createQuery("from AssetClassDimensionEntity b where b.assetClassDimensionId=(select max(a.assetClassDimensionId) from AssetClassDimensionEntity a where a.productId=(select productId from AssetEntity where serial_number='"+Serial+"'))");
				itr = qry.list().iterator();
				//Code change done by Juhi on 14-11-2013 : Latest value from Asset Class Dimension Id				
				if (itr.hasNext()) {
					while(itr.hasNext())
					Asset_Class_Id = (AssetClassDimensionEntity) itr.next();
				}
			
			} // End of try block
			
			/*catch (HibernateException  e) {
				fatalError.fatal("ETLfactDataBO: Fatal error getting VIN data: "
						+ Serial
						+ "Message: "
						+ e.getMessage());
				e.printStackTrace();
			}*/
			
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
		}
		
	//*** end of getVINspecificinfo ***
		
	//*** start of insertOlap ***

		public void insertOlap(String Serial,
								Timestamp tranTime,
								Timestamp lastOlapDt,
								Timestamp prevlastOlapDt,
								boolean update) {
			
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
			
			Session sessionIns = HibernateUtil.getSessionFactory().openSession();
			
			try
			{
				EngineRunningBandList.clear();
//				Keerthi : rounding engine off hours to 2 places : 10/03/14
				DecimalFormat df2 = new DecimalFormat("###.##");
				//DefectID1989  - 20140122 - Rajani Nagaraju - Engine Running bands received in each packet would not be the cummulative data.. Instead it is for that 10 min duration
				/*if ( PreviousDayBandList.size() == 0 ) {
					if ( update ) {
						minCreatedDt = prevlastOlapDt;
						maxCreatedDt = lastOlapDt;
					} else {
						minCreatedDt = lastOlapDt;
						// For axCreatedDt time part is removed so that engine 
						// band details for previous date can be obtained
						maxCreatedDt = Timestamp.valueOf(dateStr.format(tranTime) + " 00:00:00");
					}
					startTime = System.currentTimeMillis();
					
					Query qry = sessionIns.createQuery(" select b.transactionNumber "
										+ " from AssetMonitoringHeaderEntity b "
										+ " where b.serialNumber = '" + Serial + "'"
										+ " and b.transactionTime >= '" 
												+ minCreatedDt + "'" 
										+ " and b.transactionTime < '" 
												+ maxCreatedDt + "'"
										+ " order by b.transactionTime desc, "
										+ " b.transactionNumber"
										).setFirstResult(0).setMaxResults(1);
					List lst = qry.list();
					Iterator itr = lst.iterator();
					
					if ( (itr.hasNext()) && (lst.size() > 0) && (lst.get(0) != null) ) {
						tranNo = (Integer) itr.next();
					}
									
					itr = sessionIns.createQuery(" select a.parameterValue "
								+ " from AssetMonitoringDetailEntity a  "
								+ " where a.transactionNumber = '"
										+ tranNo + "' " 
								+ " and a.parameterId in "
										+ "('" 
										+ EngineRunningBand1Id + "', '" + EngineRunningBand2Id + "', '"
										+ EngineRunningBand3Id + "', '" + EngineRunningBand4Id + "', '"
										+ EngineRunningBand5Id + "', '" + EngineRunningBand6Id + "', '"
										+ EngineRunningBand7Id + "', '" + EngineRunningBand8Id 
										+ "')"
								+ " order by a.parameterId").list().iterator();
						
					int flag = 0;
					while (itr.hasNext()) {
						PreviousDayBandList.add(Double.parseDouble( itr.next().toString() ) );
						flag++;
					}
					endTime = System.currentTimeMillis();
					infoLogger.info("ETLfactDataBO: Get prev band for VIN "
								+ Serial
								+ " time in ms: "
								+ (endTime - startTime));
				}*/
					
				/*if ( PreviousDayBandList.size() == 0 ) {
					for ( int i = 1; i <= 8; i++){
						PreviousDayBandList.add(0.0);
					}
				}*/
				
				startTime = System.currentTimeMillis();
				
				String transaction_timestamp =dateStr.format(tranTime);
				Query runningBandQuery = sessionIns.createQuery(" select amh.serialNumber," +
		    	 		//" SUM(IF(amd.parameterId = '33', amd.parameterValue, 0)) AS EngineRunningBand1 " +
		    			 " SUM(case when amd.parameterId='33' then amd.parameterValue else 0 end ) AS EngineRunningBand1, " +
		    	 		" SUM(case when amd.parameterId='34' then amd.parameterValue else 0 end ) AS EngineRunningBand2, " +
		    	 		" SUM(case when amd.parameterId='35' then amd.parameterValue else 0 end ) AS EngineRunningBand3, " +
		    	 		" SUM(case when amd.parameterId='36' then amd.parameterValue else 0 end ) AS EngineRunningBand4, " +
		    	 		" SUM(case when amd.parameterId='37' then amd.parameterValue else 0 end ) AS EngineRunningBand5, " +
		    	 		" SUM(case when amd.parameterId='38' then amd.parameterValue else 0 end ) AS EngineRunningBand6, " +
		    	 		" SUM(case when amd.parameterId='39' then amd.parameterValue else 0 end ) AS EngineRunningBand7, " +
		    	 		" SUM(case when amd.parameterId='40' then amd.parameterValue else 0 end ) AS EngineRunningBand8 " +
		    	 		" from AssetMonitoringHeaderEntity amh, AssetMonitoringDetailEntity amd " +
		    	 		" where amh.transactionNumber = amd.transactionNumber " +
		    	 		" and amh.serialNumber= '"+Serial+"' and amh.transactionTime like '"+transaction_timestamp+"%'" +
		    	 				" and amd.parameterValue is not null and amh.recordTypeId=3");
		    	 List runningBandList = runningBandQuery.list();
				 Iterator runningBandItr = runningBandList.iterator();
		    	 Object[] runningBandResult=null;
		    	 if ( (runningBandItr.hasNext()) && (runningBandList.size() > 0) && (runningBandList.get(0) != null) ) 
		    	 //while(runningBandItr.hasNext())
		    	 {
		    		 runningBandResult = (Object[]) runningBandItr.next();
		    		//DefectId:1989 Min to Hour Conversion 2014-02-03
		    		 if(runningBandResult[1]!=null)
		    			 EngineRunningBandList.add( (Double.parseDouble((String) runningBandResult[1]))/60);
		    		 if(runningBandResult[2]!=null)
		    			 EngineRunningBandList.add( (Double.parseDouble((String) runningBandResult[2]))/60);
		    		 if(runningBandResult[3]!=null)
		    			 EngineRunningBandList.add( (Double.parseDouble((String) runningBandResult[3]))/60);
		    		 if(runningBandResult[4]!=null)
		    			 EngineRunningBandList.add( (Double.parseDouble((String) runningBandResult[4]))/60);
		    		 if(runningBandResult[5]!=null)
		    			 EngineRunningBandList.add( (Double.parseDouble((String) runningBandResult[5]))/60);
		    		 if(runningBandResult[6]!=null)
		    			 EngineRunningBandList.add( (Double.parseDouble((String) runningBandResult[6]))/60);
		    		 if(runningBandResult[7]!=null)
		    			 EngineRunningBandList.add( (Double.parseDouble((String) runningBandResult[7]))/60);
		    		 if(runningBandResult[8]!=null)
		    			 EngineRunningBandList.add( (Double.parseDouble((String) runningBandResult[8]))/60);
		    		 
		    	 }
				
		    	//DefectID1989  - 20140122 - Rajani Nagaraju - Engine Running bands received in each packet would not be the cummulative data.. Instead it is for that 10 min duration
				Query qry = sessionIns.createQuery(" select a.parameterId, a.parameterValue "
										+ " from AssetMonitoringDetailEntity a "
										+ " where a.transactionNumber = "
												+ " ( "
												+ "select max(b.transactionNumber) "
												+ " from AssetMonitoringHeaderEntity b "
												+ " where b.serialNumber = '" + Serial + "'"
												+ " and b.transactionTime = '" + tranTime 
												+ "')"
										+ " and a.parameterId in "
												+ "('" 
												+ longitudeId + "', '" + latitudeId + "', '" 
												+ engineHoursId + "', '" + engineONId
												/*+ engineHoursId + "', '" + engineONId + "', '" 
												+ EngineRunningBand1Id + "', '" + EngineRunningBand2Id + "', '"
												+ EngineRunningBand3Id + "', '" + EngineRunningBand4Id + "', '"
												+ EngineRunningBand5Id + "', '" + EngineRunningBand6Id + "', '"
												+ EngineRunningBand7Id + "', '" + EngineRunningBand8Id */
												+ "')"
										+ " order by a.parameterId");
				List lst = qry.list();
				Iterator itr = lst.iterator();
				
				int flag = 0;
				
				if ( (!itr.hasNext()) && (lst.size() == 0) ) {
					// Scenario: No record in Asset Monitoring Details for a transaction number 
					// Not a valid scenario and hence aborting insert / update
					return;
				}
				
				while (itr.hasNext()) 
				{
					flag++;

					result = (Object[]) itr.next();
					
					
						if ( ((MonitoringParameters) result[0]).getParameterId() == longitudeId ){
							Longitude = result[1].toString();
						}
						
						if ( ((MonitoringParameters) result[0]).getParameterId() == latitudeId ){
							Latitude = result[1].toString();
						}
					
						if ( ((MonitoringParameters) result[0]).getParameterId() == engineHoursId ){
							MachineHours = Double.parseDouble(result[1].toString());
						}
						
						if ( ((MonitoringParameters) result[0]).getParameterId() == engineONId ){
							//DefectId:20150115 @Suprava To add Engine status new column to DayfactAgg table .
							engine_Status = result[1].toString();
							//DefectId:20150115 en
							if ( Integer.parseInt( (String) result[1] ) == 1 ) {
								Last_Engine_Run = tranTime;
							} else {
								// Only when latest transaction for the VIN on "tranTime" date 
								// has Engine ON = 0, below logic will find the most recent 
								// transaction where Engine ON is 1
								Query qry1 = sessionIns.createQuery(" select b.transactionTime "
											+ " from AssetMonitoringDetailEntity a, AssetMonitoringHeaderEntity b "
											+ " where b.serialNumber = '" + Serial + "'"
											+ " and b.transactionTime < '" + tranTime + "'"
											+ " and a.transactionNumber = b.transactionNumber" 
											+ " and a.parameterId = '" + engineONId + "'"
											+ " and a.parameterValue = '1'"
											+ " order by b.transactionTime desc").setFirstResult(0).setMaxResults(1);
								Iterator itr1 = qry1.list().iterator();
								if (itr1.hasNext()) {
									Last_Engine_Run = (Timestamp) itr1.next();
								}
							}
						}
					
						//DefectID1989  - 20140122 - Rajani Nagaraju - Engine Running bands received in each packet would not be the cummulative data.. Instead it is for that 10 min duration
						/*if ( (flag >= 5) && (flag <= 12) ){
							EngineRunningBandList.add( Double.parseDouble((String) result[1]) - PreviousDayBandList.get(flag-5) );
							PreviousDayBandList.set( flag-5, EngineRunningBandList.get(flag-5) );
						}*/
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
				iLogger.info("ETLfactDataBO: Get current details for VIN "
						+ Serial
						+ " time in ms: "
						+ (endTime - startTime));
				
				startTime = System.currentTimeMillis();
				
				
				qry = sessionIns.createQuery(" select sum(c.extendedParameterValue) "
						+ " from AssetMonitoringDetailExtended c right outer join c.transactionNumber a "
						+ " where a.serialNumber = '"
								+ Serial
						+ "'"
						+ " and a.transactionTime like '"
								+ dateStr.format(tranTime)
						+ "%'");
				lst = qry.list();
				itr = lst.iterator();
				
				if ( (itr.hasNext()) && (lst.size() > 0) && (lst.get(0) != null) ) {
					TotalFuelUsed = Double.parseDouble(itr.next().toString());
				} else {
					TotalFuelUsed = 0.0;
				}
				
				if ( TotalFuelUsed > 0 && EngineOffHours < 24 ) {
					FuelIdle  = (TotalFuelUsed/(24 - EngineOffHours)) * (EngineRunningBandList.get(0) + EngineRunningBandList.get(1));
					FuelWorking = TotalFuelUsed - FuelIdle;
				} else {
					FuelIdle  = 0.0;
					FuelWorking = 0.0;
				}
				
				
				endTime = System.currentTimeMillis();
				iLogger.info("ETLfactDataBO: Get fuel usage for VIN "
						+ Serial
						+ " time in ms: "
						+ (endTime - startTime));
				
				sessionIns.beginTransaction();
				
				AssetMonitoringFactDataEntity assetMonitoringFactDataEntity = new AssetMonitoringFactDataEntity();
				
				if ( update ){
					assetMonitoringFactDataEntity.setTimeKey( Timestamp.valueOf(dateStr.format(lastOlapDt) + " 00:00:00") );
				} else {
					assetMonitoringFactDataEntity.setTimeKey( Timestamp.valueOf(dateStr.format(tranTime) + " 00:00:00") );
				}

				assetMonitoringFactDataEntity.setTenancyId(tenancy_dimension_id);
				assetMonitoringFactDataEntity.setAccountDimensionId(accountDimensionId);
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
				assetMonitoringFactDataEntity.setAssetClassDimensionId(Asset_Class_Id);
				assetMonitoringFactDataEntity.setMachineName(NickName);
				assetMonitoringFactDataEntity.setAddress(null);
				//DefectId:20150115 @Suprava To add Engine status new column to DayfactAgg table .
				assetMonitoringFactDataEntity.setEngineStatus(engine_Status);
				//DefectId:20150115 en
				if ( update ){
					sessionIns.update("AssetMonitoringFactDataDayAgg", assetMonitoringFactDataEntity);
				} else {
					sessionIns.save("AssetMonitoringFactDataDayAgg", assetMonitoringFactDataEntity);
				}
			} // End of main try block
			
			catch (Exception e) {
				if ( sessionIns.getTransaction().isActive() ) {
					sessionIns.getTransaction().rollback();
				}
				fLogger.fatal("ETLfactDataBO: Fatal error processing VIN: "
					+ Serial 
					+ "Message: "
					+ e.getMessage());
				e.printStackTrace();
			} 
			
			finally {
				if (sessionIns.getTransaction().isActive()) {
					sessionIns.getTransaction().commit();
				}

				if (sessionIns.isOpen()) {
					sessionIns.flush();
					sessionIns.close();
				}
				
				sessionIns = null;
			}

		}//*** end of insertOlap *** DF1409.en
} // End of class ETLfactDataBO