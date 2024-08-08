package remote.wise.businessobject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetMonitoringHeaderEntity;
import remote.wise.dal.DynamicAMH_DAL;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.pojo.AssetMonitoringParametersDAO;
import remote.wise.service.implementation.FuelUtilizationDetailImpl;
import remote.wise.util.HibernateUtil;
import remote.wise.util.IstGmtTimeConversion;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;


/**
 * FuelUtilizationDetailBO  will allow to get Fuel Utilization Details
 * @author jgupta41
 *
 */
public class FuelUtilizationDetailBO {
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("FuelUtilizationDetailBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("FuelUtilizationDetailBO:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("FuelUtilizationDetailBO:","info");*/
	
	 
//*******************************************Get Fuel Utilization Detail for given LoginId and SerialNumber ********************
/**
 * This method will return List of  Fuel Utilization Detail for given LoginId and SerialNumber
 * @param Period:Period can be 'Today','Week','Month', 'Quarter', 'Year'
 * @param LoginId:LoginId
 * @param SerialNumber:SerialNumber
 * @return fuelUtilizationDetailImpl:Returns List of Fuel Utilization Detail
 * @throws CustomFault:custom exception is thrown when the LoginId ,Period,SerialNumber is not specified, SerialNumber is invalid or not specified
 */
	public List<FuelUtilizationDetailImpl>  getFuelUtilizationDetailListOld( String Period,String LoginId ,String SerialNumber )throws CustomFault
	{
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Logger bLogger = BusinessErrorLoggerClass.logger;
    	
		long startTime =System.currentTimeMillis();
		if(LoginId==null)
		{
			bLogger.error("Please pass a LoginId");
			throw new CustomFault("Please pass a LoginId");
		
		}
		if(SerialNumber==null)
		{bLogger.error("Please pass a SerialNumber");
			throw new CustomFault("Please pass a SerialNumber");
		}
		
		List<FuelUtilizationDetailImpl> fuelUtilizationDetailImpl=new LinkedList<FuelUtilizationDetailImpl>();
		iLogger.info("**********************Get current date in format yyyy-MM-dd **************************************************");
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfHour = new SimpleDateFormat("HH");
	    String currentDate = sdfDate.format(cal.getTime());
	    iLogger.info("today's date is"+currentDate);
	    String currentHour = sdfHour.format(cal.getTime());
	    iLogger.info("current hour is"+currentHour);
	    
	    iLogger.info("*******Get maximum Parameter id for given FuelLevel*********************");
	    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
    
	try{
	    	Query q = session.createQuery("select d.parameterId from MonitoringParameters d where d.parameterName like 'FuelLevel' order by d.parameterId desc");
	    	q.setMaxResults(1);
			Iterator itr1=q.list().iterator();
			int parameterId=0 ;
			while(itr1.hasNext())
			{
				parameterId=(Integer) itr1.next();
				
			}
	    
			
			iLogger.info("**************Get TimeStamp and Transaction No. WHERE TimeStamp Is CurrentDate************");
			List<Integer> transactionNumber=new LinkedList<Integer>();
			List<String> transactionTime=new LinkedList<String>();
			Query q1 = session.createQuery("from AssetMonitoringHeaderEntity where transactionTime like'"+currentDate+"%'");
			Iterator itr=q1.list().iterator();
			while(itr.hasNext())
			{
				AssetMonitoringHeaderEntity assetMonitoringHeaderEntity=( AssetMonitoringHeaderEntity)itr.next();
				transactionNumber.add(assetMonitoringHeaderEntity.getTransactionNumber());
				transactionTime.add(assetMonitoringHeaderEntity.getTransactionTime().toString());
						
			}
				
			if(transactionNumber==null || transactionNumber.isEmpty())
			{
				return fuelUtilizationDetailImpl;
			}
			
			ListToStringConversion conversionObj = new ListToStringConversion();
			String transactionNumberStringList = conversionObj.getIntegerListString(transactionNumber).toString();
			
			//DefectID: 20131118- Rajani Nagaraju - Qwery Tweaking - Query was taking too much time - So splitting into two Queries
			SimpleDateFormat dfFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Query txnTimeQuery = session.createQuery("select max(transactionTime) from AssetMonitoringHeaderEntity where serialNumber ='"+SerialNumber+"' " +
							"and transactionTime like '"+currentDate+"%' group by HOUR(transactionTime)");
					//Smitha------DefectID:20140121-----commented part as been removed bcoz it was giving data for previous dates while we are interested only for today.
					/*//Commented above query as it was always taking current day DF:20131223---Juhi
					" group by HOUR(transactionTime)");*/
			List txnTimeList = txnTimeQuery.list();
			Iterator txnTimeItr = txnTimeList.iterator();
			String txnTimeStr = null;
			String finaltxnTime = null;
			List<String> txnTimeStirngList = new LinkedList<String>();
			if(txnTimeList!=null && (!(txnTimeList.isEmpty())) && txnTimeList.get(0)!=null)
			{
				while(txnTimeItr.hasNext())
				{
					Timestamp txnTime = (Timestamp)txnTimeItr.next();
					/*Date date = new Date(txnTime.getTime());
					txnTimeStirngList.add(dfFormat.format(date));*/
					//Smitha-----DefectID:20140121-----to return the correct transaction timestamp for a VIN on 21st jan 2014.
					txnTimeStr = txnTime.toString();
					finaltxnTime = txnTimeStr.substring(0,19);
					txnTimeStirngList.add(finaltxnTime);
					
				}
			}
			
			//DefectID: 20131118- Rajani Nagaraju - Qwery Tweaking - Query was taking too much time - So splitting into two Queries
			if(! (txnTimeStirngList==null || txnTimeStirngList.isEmpty()) )
			{
				String txnNumString = conversionObj.getStringList(txnTimeStirngList).toString();
			
			iLogger.info("**********Get TimeStamp ,Serial_Number,ParameterValue for Each Transaction No. ***");
			//added by smitha on sept 13th 2013
			FuelUtilizationDetailImpl fuelUtilizationImpl=new FuelUtilizationDetailImpl();
			int cHour=Integer.valueOf(currentHour);
			int num=0;
			int n=0;
			String tempValue=null;
			AssetEntity e=null;
			HashMap<Integer,String> fuel = new HashMap<Integer,String>();
//			HashMap<Integer,String> fueltempfinal = new HashMap<Integer,String>();
			TreeMap<Integer,String> fueltempfinal = new TreeMap<Integer,String>();
//			HashMap<Integer,String> finalmap = new HashMap<Integer,String>();
			TreeMap<Integer,String> finalmap = new TreeMap<Integer,String>();
			String basicSelectQuery = " select HOUR(e.transactionTime)+1,e.serialNumber,c.parameterValue ";
			String basicFromQuery = " FROM AssetMonitoringDetailEntity  c,AssetMonitoringHeaderEntity e ";
			String 	basicWhereQuery = " where e.transactionNumber = c.transactionNumber and e.serialNumber ='"+SerialNumber+"' " +
					"and e.transactionTime in ("+txnNumString+") and c.parameterId = "+parameterId+" " +
									"order by e.transactionTime";
							
			/*String basicSelectQuery = " SELECT e.transactionTime,e.serialNumber,c.parameterValue ";
		      
			String basicFromQuery = " FROM AssetMonitoringDetailEntity  c ";
			basicFromQuery = basicFromQuery +" JOIN c.transactionNumber e JOIN c.parameterId d";
			String 	basicWhereQuery = " where c.transactionNumber  = e.transactionNumber  and c. parameterId=d.parameterId" ;
			basicWhereQuery = basicWhereQuery+" and c. parameterId ="+parameterId+"and c.transactionNumber in (" +transactionNumberStringList+")";*/
			
			String finalQuery =basicSelectQuery+ basicFromQuery + basicWhereQuery;
		
			Query q2 = session.createQuery(finalQuery);
		
			Object[] result=null;
			List list1 = q2.list();
			Iterator itr2=q2.list().iterator();
			
			if(list1!=null && !(list1.isEmpty()) && list1.get(0)!=null )
			{
			while(itr2.hasNext())
			{ 
				result = (Object[]) itr2.next();
			fuelUtilizationImpl=new FuelUtilizationDetailImpl();
			 e = (AssetEntity) result[1];
			int temp=(Integer)result[0];			
			String fuelLevel=(String)result[2];
			int r=1;
			fuel.put(temp, fuelLevel);
			for(int h=0;h<fuel.size();h++){
				Set mapSet = (Set) fuel.entrySet();
				Iterator mapIterator = mapSet.iterator();	
				while (mapIterator.hasNext()) {
					Map.Entry mapEntry = (Map.Entry) mapIterator.next(); 
					int keyValue = (Integer) mapEntry.getKey(); 
					String value = (String)mapEntry.getValue();
					for(int v=r;v<=keyValue;v++){
						fueltempfinal.put(v,value);
						r=v+1;
						tempValue=value;
					}						
			}			
			}
			n=r;
			finalmap=fueltempfinal;
	    }
			num=cHour-(n-1);
			for(int z=0;z<num;z++){
				finalmap.put(n,tempValue);
				n=n+1;
			}
			iLogger.info("finalmap "+finalmap);
			fuelUtilizationImpl.setSerialNumber(e.getSerial_number().getSerialNumber());
			fuelUtilizationImpl.setPeriod("Today");
			fuelUtilizationImpl.setHourFuelLevelMap(finalmap);
			fuelUtilizationDetailImpl.add(fuelUtilizationImpl);
			//ended on sept 13th 2013
			long endTime =System.currentTimeMillis();
			 iLogger.info("service time"+(endTime-startTime));
			}
			}
	}
	    catch(Exception e){
	    	e.printStackTrace();
	    	fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());

	    }
	    finally
	    {
	          if(session.getTransaction().isActive())
	          {
	                session.getTransaction().commit();
	          }
	          
	          if(session.isOpen())
	          {
	                session.flush();
	                session.close();
	          }
	          
	    }		
		return fuelUtilizationDetailImpl ;
	}
	//*******************************************End of Get Fuel Utilization Detail for given LoginId and SerialNumber ********************
	
	
	public List<FuelUtilizationDetailImpl>  getFuelUtilizationDetailList( String Period,String LoginId ,String SerialNumber )throws CustomFault
	{
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Logger bLogger = BusinessErrorLoggerClass.logger;
    	HashMap asset2SegID = new HashMap();
		long startTime =System.currentTimeMillis();
		if(LoginId==null)
		{
			bLogger.error("Please pass a LoginId");
			throw new CustomFault("Please pass a LoginId");
		
		}
		if(SerialNumber==null)
		{bLogger.error("Please pass a SerialNumber");
			throw new CustomFault("Please pass a SerialNumber");
		}
		
		List<FuelUtilizationDetailImpl> fuelUtilizationDetailImpl=new LinkedList<FuelUtilizationDetailImpl>();
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfHour = new SimpleDateFormat("HH");
	    String currentDate = sdfDate.format(cal.getTime());
	    String currentDate1 = currentDate;
	    String currentHour = sdfHour.format(cal.getTime());
	    
	    Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
    
	try{
		

		AssetEntity asset = null;
		
			asset = new AssetDetailsBO().getAssetEntity(SerialNumber);
			asset2SegID.put(asset.getSerial_number().getSerialNumber(), asset.getSegmentId());
		
	    Date parsedDate = sdfDate.parse(currentDate);
		cal.setTime(parsedDate);
		
		// get previous day
		cal.add(Calendar.DAY_OF_YEAR, -1);
		String prevDate = sdfDate.format(cal.getTime());
		// data will be taken from previous day 6:30 to today 6:30
		
		if(session == null || !session.isOpen()){
			 session = HibernateUtil.getSessionFactory().openSession();
		        session.beginTransaction();
		}
		prevDate = prevDate + " 18:30:00";
		currentDate = currentDate + " 18:30:00";
	    	Query q = session.createQuery("select d.parameterId from MonitoringParameters d where d.parameterName like 'FuelLevel' order by d.parameterId desc");
	    	q.setMaxResults(1);
			Iterator iterator=q.list().iterator();
			int parameterId=0 ;
			while(iterator.hasNext())
			{
				parameterId=(Integer) iterator.next();
				
			}
			
			List parametreIDList = new LinkedList();
			parametreIDList.add(parameterId);
//	    	getting max transaction time of log packet here
			/*Query txnTimeQuery = session.createQuery("SELECT MAX(transactionTime)" +
							" FROM AssetMonitoringHeaderEntity"+
							" WHERE serial_number = '"+SerialNumber+"'" +
							" AND transactionTime BETWEEN '"+prevDate+"' AND '"+currentDate+"'" +
							" AND recordTypeId=3"+
							//" GROUP BY HOUR(transactionTime) " +
							//DF20140414 - Rajani Nagaraju - Group BY based on IST Time to correct half an hour deviation
							" GROUP BY HOUR(convert_tz(transactionTime,'+00:00','+05:30'))" +
		    				" ORDER BY transactionTime");*/
			
			//DF20161222 @Roopa changing AMH and AMD into single TAssetMOnData table and taking parameters from json column TxnData
			
			List txnTimeList = new DynamicAMH_DAL().getMaxTxsInTxnRange(asset2SegID, currentDate1, parametreIDList);
			
			//List txnTimeList = txnTimeQuery.list();
			iterator = txnTimeList.iterator();
			Timestamp transaction_timestamp = null;
			List<String> timestampList = new ArrayList<String>();			
			
			timestampList = new ArrayList<String>();
			String lowerLimit = null, upperLimit = null;
			Iterator alertQueryIterator = null;
			while(iterator.hasNext()){
				AssetMonitoringParametersDAO daoObject = (AssetMonitoringParametersDAO) iterator.next();
				transaction_timestamp =(Timestamp)daoObject.getTransactionTS();
//				check whether we hv any lfl event wth grater of this and lesser for that hour
				List alertQueryList = null;
				if(transaction_timestamp!=null){
					lowerLimit = transaction_timestamp.toString().substring(0,19);
//					get hour of this and get upper limit (adding 59 mins and 59 sec)
					upperLimit = transaction_timestamp.toString().substring(0,13)+":59:59";
					Query alertQuery = session.createQuery("SELECT eventGeneratedTime" +
							" FROM AssetEventEntity"+
							" WHERE serialNumber = '"+SerialNumber+"'" +
							" AND eventGeneratedTime BETWEEN '"+lowerLimit+"' AND '"+upperLimit+"'" +
							" AND (eventId=16 OR eventId=17)");
					alertQueryList = alertQuery.list();
					if(alertQueryList.size()>0){
						alertQueryIterator = alertQueryList.iterator();
						while(alertQueryIterator.hasNext()){
							transaction_timestamp = (Timestamp)alertQueryIterator.next();
						}
					}
					timestampList.add(transaction_timestamp.toString().substring(0,19));
				}
				
			}
			
//			check in Asset Event for LFL event with transaction time >= of the above timestamp
			
			
			//DF20140210 for adding serial number
			
			if(timestampList.size()>0){
				String timestampString = new ListToStringConversion().getStringList(timestampList).toString();
			
			/*txnTimeQuery = session.createQuery(
								"SELECT amh.transactionTime,amd.parameterValue"+
								" FROM AssetMonitoringHeaderEntity amh, AssetMonitoringDetailEntity amd" +
								" WHERE amh.transactionTime IN ("+timestampString+")"+
								" AND amh.transactionNumber=amd.transactionNumber" +
								" AND amh.serialNumber='"+SerialNumber+"'" +
								" AND amd.parameterId =" + parameterId +
							    " ORDER BY amh.transactionTime");*/
				
				//DF20161222 @Roopa changing AMH and AMD into single TAssetMOnData table and taking parameters from json column TxnData
				
				txnTimeList = new DynamicAMH_DAL().getAMhDataOnTxnTS(asset2SegID, timestampList, parametreIDList);	
			//txnTimeList = txnTimeQuery.list();
			iterator = txnTimeList.iterator();
			FuelUtilizationDetailImpl fuelUtilizationImpl = new FuelUtilizationDetailImpl();
			if(txnTimeList!=null && (!(txnTimeList.isEmpty())) && txnTimeList.get(0)!=null)
			{
				IstGmtTimeConversion conversionTimeObj =new IstGmtTimeConversion();
				Object[] result=null;
				Timestamp transactionTime = null,gmtTimestamp = null;
				String fuelLevel = null;
				int istHour = 0;
				TreeMap<Integer,String> hourFuelMap = new TreeMap<Integer,String>();
				TreeMap<Integer,String> hourFuelFinalMap = new TreeMap<Integer,String>();
				int cHour=Integer.valueOf(currentHour);
				int startHour = -1;
				
				while(iterator.hasNext())
				{
					AssetMonitoringParametersDAO daoObject = (AssetMonitoringParametersDAO) iterator.next();
					if(daoObject.getTransactionTS()!=null){
						transactionTime = (Timestamp)daoObject.getTransactionTS();		
						gmtTimestamp = conversionTimeObj.convertGmtToIst(transactionTime);
						if(gmtTimestamp!=null){
							if(gmtTimestamp.toString().length()>=13){
								istHour = Integer.parseInt(gmtTimestamp.toString().substring(11,13));
								
								//DF20190109-KO369761-Not considering the data if the machine has not communicated in the past.
								if(startHour == -1 || startHour > istHour)
									startHour = istHour;
							}
						}
						
					}
					if(daoObject.getParameterValue()!=null){
						fuelLevel = (String)daoObject.getParameterValue();
					}
					
					if(istHour+1<=cHour){// chart will show upto only last hour
						hourFuelMap.put(istHour+1,fuelLevel);
					}
				}
				
				//DF20190109-KO369761-Not considering the data if the machine has not communicated in the past.
				for(int i = 1;i<=startHour;i++){
					hourFuelFinalMap.put(i, null);
				}
				startHour++;

//				machine may not communicated for some hours, so putting previous hours
				if(hourFuelMap.size()>0){
					for(Integer hour : hourFuelMap.keySet()){
						fuelLevel = hourFuelMap.get(hour);
						if(hour!=startHour){
							while(startHour<hour){
								hourFuelFinalMap.put(startHour,  hourFuelFinalMap.get(hourFuelFinalMap.lastKey()));
								++startHour;
							}
						}
						hourFuelFinalMap.put(hour, fuelLevel);
						startHour = hour+1;
					}
					while(startHour<=Integer.valueOf(currentHour)){
						hourFuelFinalMap.put(startHour, fuelLevel);
						++startHour;
					}
				}
				
				fuelUtilizationImpl.setSerialNumber(SerialNumber);
				fuelUtilizationImpl.setPeriod("Today");
				fuelUtilizationImpl.setHourFuelLevelMap(hourFuelFinalMap);
				fuelUtilizationDetailImpl.add(fuelUtilizationImpl);
				long endTime =System.currentTimeMillis();
				 iLogger.info("fuel chart service time"+(endTime-startTime));
			}
	   }
	}
	    catch(Exception e){
	    	e.printStackTrace();
	    	fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());

	    }
	    finally
	    {
	          if(session !=null && session.isOpen() && session.getTransaction().isActive())
	          {
	                session.getTransaction().commit();
	          }
	          
	          if(session !=null && session.isOpen())
	          {
	                session.flush();
	                session.close();
	          }
	          
	    }		
		return fuelUtilizationDetailImpl ;
	}


	public HashMap<String, TreeMap<Integer, String>> getFuelUtilizationDetailListForWeek(
			String period, String loginId, String serialNumber)
					throws CustomFault {

		Logger fLogger = FatalLoggerClass.logger;
		HashMap<String, Integer> asset2SegID = new HashMap<String, Integer>();
		List<String> dateRangeList = new LinkedList<String>();
		HashMap<String, TreeMap<Integer, String>> DayWiseFuelFinalMap = new HashMap<String, TreeMap<Integer,String>>();
		TreeMap<Integer, String> hourFuelFinalMap = null;

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfHour = new SimpleDateFormat("HH");

		String todayDate = sdfDate.format(cal.getTime());
		String currentHour = sdfHour.format(cal.getTime());

		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		try {
			//DF20190107 @ Zakir edited the condition if period contains "|" for Monthly charts
			if (period.equalsIgnoreCase("Week") || period.contains("|")) {
				dateRangeList = ReportDetailsBO.getDateRangeList(period);
			} else if (period.equalsIgnoreCase("Today")) {
				dateRangeList.add(todayDate);
			}

			AssetEntity asset = new AssetDetailsBO()
			.getAssetEntity(serialNumber);
			asset2SegID.put(asset.getSerial_number().getSerialNumber(),
					asset.getSegmentId());

			Query q = session
					.createQuery("select d.parameterId from MonitoringParameters d where d.parameterName like 'FuelLevel' order by d.parameterId desc");
			q.setMaxResults(1);
			Iterator iterator = q.list().iterator();
			int parameterId = 0;
			while (iterator.hasNext()) {
				parameterId = (Integer) iterator.next();

			}

			List<Integer> parametreIDList = new LinkedList<Integer>();
			parametreIDList.add(parameterId);

			for (String currDate : dateRangeList) {

				List<AssetMonitoringParametersDAO> txnTimeList = new DynamicAMH_DAL()
				.getMaxTxsInTxnRange(asset2SegID, currDate,
						parametreIDList);

				Timestamp transaction_timestamp = null;
				List<String> timestampList = new ArrayList<String>();			
				currentHour = sdfHour.format(cal.getTime());

				iterator = txnTimeList.iterator();
				timestampList = new ArrayList<String>();
				String lowerLimit = null, upperLimit = null;
				Iterator alertQueryIterator = null;
				while(iterator.hasNext()){
					AssetMonitoringParametersDAO daoObject = (AssetMonitoringParametersDAO) iterator.next();
					transaction_timestamp =(Timestamp)daoObject.getTransactionTS();
					//					check whether we hv any lfl event wth grater of this and lesser for that hour
					List alertQueryList = null;
					if(transaction_timestamp!=null){
						lowerLimit = transaction_timestamp.toString().substring(0,19);
						//						get hour of this and get upper limit (adding 59 mins and 59 sec)
						upperLimit = transaction_timestamp.toString().substring(0,13)+":59:59";
						Query alertQuery = session.createQuery("SELECT eventGeneratedTime" +
								" FROM AssetEventEntity"+
								" WHERE serialNumber = '"+serialNumber+"'" +
								" AND eventGeneratedTime BETWEEN '"+lowerLimit+"' AND '"+upperLimit+"'" +
								" AND (eventId=16 OR eventId=17)");
						alertQueryList = alertQuery.list();
						if(alertQueryList.size()>0){
							alertQueryIterator = alertQueryList.iterator();
							while(alertQueryIterator.hasNext()){
								transaction_timestamp = (Timestamp)alertQueryIterator.next();
							}
						}
						timestampList.add(transaction_timestamp.toString().substring(0,19));
					}

				}

				if(timestampList.size()>0){
					txnTimeList = new DynamicAMH_DAL().getAMhDataOnTxnTS(asset2SegID, timestampList, parametreIDList);	

					iterator = txnTimeList.iterator();
					if (txnTimeList != null && (!(txnTimeList.isEmpty()))
							&& txnTimeList.get(0) != null) {
						IstGmtTimeConversion conversionTimeObj = new IstGmtTimeConversion();
						Timestamp transactionTime = null, gmtTimestamp = null;
						String fuelLevel = null;
						int istHour = 0;
						TreeMap<Integer, String> hourFuelMap = new TreeMap<Integer, String>();
						hourFuelFinalMap = new TreeMap<Integer, String>();
						int cHour = Integer.valueOf(currentHour);
						int startHour = -1;

						while (iterator.hasNext()) {
							AssetMonitoringParametersDAO daoObject = (AssetMonitoringParametersDAO) iterator
									.next();
							if (daoObject.getTransactionTS() != null) {
								transactionTime = (Timestamp) daoObject
										.getTransactionTS();
								gmtTimestamp = conversionTimeObj
										.convertGmtToIst(transactionTime);
								if (gmtTimestamp != null) {
									if (gmtTimestamp.toString().length() >= 13) {
										istHour = Integer.parseInt(gmtTimestamp
												.toString().substring(11, 13));
										
										//DF20190109-KO369761-Not considering the data if the machine has not communicated in the past.
										if(startHour == -1 || startHour > istHour)
											startHour = istHour;
									}
								}

							}
							if (daoObject.getParameterValue() != null) {
								fuelLevel = (String) daoObject.getParameterValue();
							}

							if (todayDate.equals(currDate)){
								if(istHour + 1 <= cHour) {// chart will show upto only last hour
									hourFuelMap.put(istHour + 1, fuelLevel);
								}
							}
							else
								hourFuelMap.put(istHour + 1, fuelLevel);
						}
						
						//DF20190109-KO369761-Not considering the data if the machine has not communicated in the past(filling null resposne).
						for(int i = 1;i<=startHour;i++){
							hourFuelFinalMap.put(i, null);
						}
						startHour++;
						
						// machine may not communicated for some hours, so putting
						// previous hours
						if (hourFuelMap.size() > 0) {
							for (Integer hour : hourFuelMap.keySet()) {
								fuelLevel = hourFuelMap.get(hour);
								if (hour != startHour) {
									while (startHour < hour) {
										hourFuelFinalMap.put(startHour,  hourFuelFinalMap.get(hourFuelFinalMap.lastKey()));
										++startHour;
									}
								}
								hourFuelFinalMap.put(hour, fuelLevel);
								startHour = hour + 1;
							}
							
							if(!todayDate.equals(currDate)){
								currentHour = "24";
							}
							
							while (startHour <= Integer.valueOf(currentHour)) {
								hourFuelFinalMap.put(startHour, fuelLevel);
								++startHour;
							}
						}
					}
					DayWiseFuelFinalMap.put(currDate, hourFuelFinalMap);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"
					+ e.getMessage());

		} finally {
			if (session != null && session.isOpen()
					&& session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}
			if (session != null && session.isOpen()) {
				session.flush();
				session.close();
			}
		}
		return DayWiseFuelFinalMap;
	}
}
