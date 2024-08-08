package remote.wise.businessobject;


import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetEntity;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.UtilizationDetailReportImpl;
import remote.wise.service.implementation.UtilizationDetailServiceImpl;
import remote.wise.util.DateUtil;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;

public class ReportDetailsBO2 {
	/** Implementation class that renders the Machine Utilization Detail Report
	 * @author Smitha
	 *
	 */
	//DF:2013:12:13 : Converision from Long to Double for all the required fields : Suprava
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("ReportDetailsBO2:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("ReportDetailsBO2:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("ReportDetailsBO2:","info");*/
	
	
	public static long toDate1(java.sql.Timestamp timestamp) {
		long milliseconds=0L;
		if(timestamp!=null)
		{
			milliseconds = timestamp.getTime() + (timestamp.getNanos() / 1000000);            	        
		}
		return milliseconds;
        
    }
	
	private String Serial_no;
	private double Engine_Off_Hours_Perct;
	private long Working_Time;
	private double WorkingTimePercentage;

	public String getSerial_no() {
		return Serial_no;
	}

	public void setSerial_no(String serial_no) {
		Serial_no = serial_no;
	}

	public double getEngine_Off_Hours_Perct() {
		return Engine_Off_Hours_Perct;
	}

	public void setEngine_Off_Hours_Perct(double engine_Off_Hours_Perct) {
		Engine_Off_Hours_Perct = engine_Off_Hours_Perct;
	}

	public long getWorking_Time() {
		return Working_Time;
	}

	public void setWorking_Time(long working_Time) {
		Working_Time = working_Time;
	}

	public double getWorkingTimePercentage() {
		return WorkingTimePercentage;
	}

	public void setWorkingTimePercentage(double workingTimePercentage) {
		WorkingTimePercentage = workingTimePercentage;
	}

	//TimeMap to be displayed till current date and system time in 10 mins bucket form---done by smitha ID20131021
	String currentDateFinal=null;
	String time =  null;
	public TreeMap<String,String> getTimeMap(){
		
		TreeMap<String,String> TimeMap = new TreeMap<String, String>();
		int j = 0, min = 0, hour = 0;
//		TimeMap.put("00:00","0"); //Keerthi : 14/11/13 : need only 144 entries
		for (int i = 0; i < 144; i++) {
			if (j == 5) {
				j = 0;
				min = 00;
				hour = hour + 1;
				if (hour <= 9) {
					TimeMap.put("0" + hour + ":" + min + "0","0");
				} else {
					TimeMap.put(hour + ":" + min + "0","0");
				}
			} else {
				j++;
				min = min + 10;
				if (hour <= 9) {
					TimeMap.put("0" + hour + ":" + min,"0");
				} else {
					TimeMap.put(hour + ":" + min,"0");
				}
			}

		}
		return TimeMap;
	}

	
	
	

	// *******************************************************END of Machine Due OverDue Report *****************************************************
	/**
	 * This method returns the Utilization Details for the given period for the
	 * serialNumberList accessible to login user and for the given filter
	 * criteria
	 * 
	 * @param loginId
	 *            userLoginId
	 * @param tenancyIdList
	 *            list of tenancyId
	 * @param machineGroupIdList
	 *            list of customAssetGroupId
	 * @param machineProfileIdList
	 *            list of assetGroupId
	 * @param modelIdList
	 *            list of modelId
	 * @param period
	 *            period-Either one of the following: Today,Week
	 * @param serialNumList
	 *            List of VINs for which utilization details has to be returned
	 * @return Returns the Utilization Details for the List of machines
	 */
	//Logic for Custom Dates (fromDate,toDate) added by Juhi on 19-August-2013
	public List<UtilizationDetailReportImpl> getUtilizationDetailReport(
			String loginId, List<Integer> tenancyIdList,
			List<Integer> machineGroupIdList,
			List<Integer> machineProfileIdList, List<Integer> modelIdList,
			String period, List<String> serialNumList) {
		 
		
		Logger infoLogger = InfoLoggerClass.logger;
		 
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;

		List<UtilizationDetailReportImpl> implRespList = new LinkedList<UtilizationDetailReportImpl>();
		UtilizationDetailReportImpl implObj = null;
		List<String> serialNumberList = new LinkedList<String>();
		String startDateTime = null;		
		TreeMap<String,String> newTimeMap = null;
		TreeMap<String,String> newDateTimeMap = new TreeMap<String, String>();
		TreeMap<String,String> newDateTimeMaptemp = new TreeMap<String, String>();
		TreeMap<String,String> newCurrentDayTimeMap = null;
		TreeMap<String, TreeMap<String, String>> timeMap = null;
		TreeMap<String, TreeMap<String, String>> timeMap2 = null;
		TreeMap<String, TreeMap<String, TreeMap<String, String>>> newSerialNoMap = new TreeMap<String, TreeMap<String,TreeMap<String,String>>>();
		String endDateTime = null;
		//added by smitha...for non communicated VIN's in the given period... on oct 31st 2013...Defect ID 20131031
		List<String>dateRangeList = new LinkedList<String>();
		String firstDay=null;
		String secondDay=null;
		String thirdDay=null;
		String fourthDay=null;
		String fifthDay=null;
		String sixthDay=null;
		String seventhDay=null;
		//ended on oct 31st 2013...Defect ID 20131031
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		try {
			DecimalFormat df2 = new DecimalFormat("###.#");
			// get List of user accessible serial numbers as per the filter
			// criteria
			if (serialNumList == null) {

				ListToStringConversion conversionObj = new ListToStringConversion();
				Date currentDate = new Date();
				DateUtil dateUtilObj = new DateUtil();
				dateUtilObj = dateUtilObj.getCurrentDateUtility(currentDate);
				int year = dateUtilObj.getYear();
				tenancyIdList = getSubTenancyIds(tenancyIdList);
				String tenancyIdStringList = conversionObj
						.getIntegerListString(tenancyIdList).toString();

				String basicQueryString = "select a.serialNumber,a.year from AssetMonitoringFactDataYearAgg a , TenancyDimensionEntity b ";

				String basicWhereQuery = " where a.year = "
						+ year
						+ " "
						+ "  and a.tenancyId = b.tenacy_Dimension_Id and b.tenancyId in ( "
						+ tenancyIdStringList + ") ";

				if ((!(machineProfileIdList == null || machineProfileIdList
						.isEmpty()))
						|| (!(modelIdList == null || modelIdList.isEmpty()))) {
					basicQueryString = basicQueryString
							+ " JOIN a.assetClassDimensionId d ";

					if (!(machineProfileIdList == null || machineProfileIdList
							.isEmpty())) {
						String machineProfileIdStringList = conversionObj
								.getIntegerListString(machineProfileIdList)
								.toString();
						basicWhereQuery = basicWhereQuery
								+ " and d.assetGroupId in ( "
								+ machineProfileIdStringList + " )";
					}

					if (!(modelIdList == null || modelIdList.isEmpty())) {
						String modelIdStringList = conversionObj
								.getIntegerListString(modelIdList).toString();
						basicWhereQuery = basicWhereQuery
								+ " and d.assetTypeId in ( "
								+ modelIdStringList + " )";
					}
				}

				if (!(machineGroupIdList == null || machineGroupIdList
						.isEmpty())) {
					String machineGroupIdStringList = conversionObj
							.getIntegerListString(machineGroupIdList)
							.toString();
					basicQueryString = basicQueryString
							+ " , CustomAssetGroupEntity c, AssetCustomGroupMapping h ";
					basicWhereQuery = basicWhereQuery
							+ " and c.group_id = h.group_id and c.group_id in ("
							+ machineGroupIdStringList + ") and "
							+ " h.serial_number = a.serialNumber ";
				}

				basicQueryString = basicQueryString + basicWhereQuery;
				Query query = session.createQuery(basicQueryString);
				Iterator itr = query.list().iterator();
				Object[] result = null;

				while (itr.hasNext()) {
					result = (Object[]) itr.next();
					serialNumberList.add(result[0].toString());
				}
			}

			else {
				serialNumberList.addAll(serialNumList);
			}
			
			// --------------------------------get the parameter id of
			// 'EngineON' parameter
			int parameterId = 0; // Parameter Id for EngineON - that would be
			// received on-event
			String parameterName = null;

			Properties prop = new Properties();
			try {
				prop.load(getClass()
						.getClassLoader()
						.getResourceAsStream(
								"remote/wise/resource/properties/configuration.properties"));
			} catch (IOException e) {
				// TODO Austo-generated catch block
				e.printStackTrace();
			}
			parameterName = prop.getProperty("EngineON");

			Query query = session
					.createQuery("select parameterId from MonitoringParameters where parameterName ='"
							+ parameterName + "' order by parameterId desc");
			query.setMaxResults(1);
			Iterator itr = query.list().iterator();

			while (itr.hasNext()) {
				parameterId = (Integer) itr.next();
			}

			// -------------------- Calculate the start date based on input
			// period
			long diff=0l;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			DateFormat dayFormat = new SimpleDateFormat("EEEE");
			if (period.equalsIgnoreCase("Week")) {
				Date currentDate = new Date();
				DateUtil dateUtilObj = new DateUtil();
				dateUtilObj = dateUtilObj.getCurrentDateUtility(currentDate);
				int week = dateUtilObj.getWeek();
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.set(Calendar.WEEK_OF_YEAR, week);
				cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				startDateTime = dateFormat.format(cal.getTime());
				
				firstDay = dateFormat.format(cal.getTime());
				endDateTime=dateFormat.format(currentDate);	
				diff=differnceInDays(firstDay, endDateTime);
				
				cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				secondDay = dateFormat.format(cal.getTime());
				cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
				thirdDay = dateFormat.format(cal.getTime());
				cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
				fourthDay = dateFormat.format(cal.getTime());
				cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
				fifthDay = dateFormat.format(cal.getTime());
				cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
				sixthDay = dateFormat.format(cal.getTime());
				
				if(diff==0l){
					dateRangeList.add(firstDay);
				}else if(diff==1l){
					dateRangeList.add(firstDay);
					dateRangeList.add(endDateTime);
				}else if(diff==2l){
					dateRangeList.add(firstDay);
					dateRangeList.add(secondDay);
					dateRangeList.add(endDateTime);
				}else if(diff==3l){
					dateRangeList.add(firstDay);
					dateRangeList.add(secondDay);
					dateRangeList.add(thirdDay);
					dateRangeList.add(endDateTime);
				}else if(diff==4l){
					dateRangeList.add(firstDay);
					dateRangeList.add(secondDay);
					dateRangeList.add(thirdDay);
					dateRangeList.add(fourthDay);
					dateRangeList.add(endDateTime);
				}else if(diff==5l){
					dateRangeList.add(firstDay);
					dateRangeList.add(secondDay);
					dateRangeList.add(thirdDay);
					dateRangeList.add(fourthDay);
					dateRangeList.add(fifthDay);
					dateRangeList.add(endDateTime);
				}else if(diff==6l){
					dateRangeList.add(firstDay);
					dateRangeList.add(secondDay);
					dateRangeList.add(thirdDay);
					dateRangeList.add(fourthDay);
					dateRangeList.add(fifthDay);
					dateRangeList.add(sixthDay);
					dateRangeList.add(endDateTime);
				}
				
			}
			if (period.equalsIgnoreCase("Today")) {
				Date currentDate = new Date();
				DateUtil dateUtilObj = new DateUtil();
				dateUtilObj = dateUtilObj.getCurrentDateUtility(currentDate);
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				//----------------------------				
				startDateTime = dateFormat.format(cal.getTime());
			    infoLogger.info("today's date is "+startDateTime);
			    dateRangeList.add(startDateTime);
			}
			//checked for Last Week...ID20131022....done by smitha on oct 22nd 2013
			if((period.equalsIgnoreCase("Last Week")))
			{
				Date currentDate = new Date();
				DateUtil dateUtilObj = new DateUtil();
				DateUtil dateUtilObj1 = new DateUtil();
				dateUtilObj  = dateUtilObj. getPreviousDateUtility(currentDate);
				dateUtilObj1  = dateUtilObj1. getCurrentDateUtility(currentDate);
				int week = dateUtilObj. getWeek();
							
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.set(Calendar.WEEK_OF_YEAR, week);
				cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				startDateTime = dateFormat.format(cal.getTime());
				
				firstDay = dateFormat.format(cal.getTime());
				cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
				endDateTime = dateFormat.format(cal.getTime());
				seventhDay = dateFormat.format(cal.getTime());
				cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				secondDay = dateFormat.format(cal.getTime());
				cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
				thirdDay = dateFormat.format(cal.getTime());
				cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
				fourthDay = dateFormat.format(cal.getTime());
				cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
				fifthDay = dateFormat.format(cal.getTime());
				cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
				sixthDay = dateFormat.format(cal.getTime());
				dateRangeList.add(firstDay);
				dateRangeList.add(secondDay);
				dateRangeList.add(thirdDay);
				dateRangeList.add(fourthDay);
				dateRangeList.add(fifthDay);
				dateRangeList.add(sixthDay);
				dateRangeList.add(seventhDay);
				
			}
			//ended on 22nd oct 2013...ID20131022
			//current date and current hour calculation.....21st oct 2013....ID20131021
			// Create a Date object set to the current date and time   
			Date now = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm ");   
			  
			// Print the date in the default timezone   
			infoLogger.info(df.format(now));					
			String currentTime=df.format(now);	
			 Date currentTime1=null;
			try {
				currentTime1 = (Date)df.parse(currentTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 Date rounded = new Date(Math.round((currentTime1.getTime() / (1000.0 * 60 * 10))) * (1000 * 60 * 10));
			 String exactTime=df.format(rounded);
			 currentDateFinal = exactTime.substring(0, 10);
			 time=exactTime.substring(11);
			
		    //ended of current date and current hour calculation.... ID20131021

			// get the timeStamp and the corresponding Machine status
			ListToStringConversion conversionObj = new ListToStringConversion();
			String serialNumberAsStringList = conversionObj.getStringList(
					serialNumberList).toString();	
			//checked for Last Week...ID20131022....done by smitha on oct 22nd 2013
			Query query5 = null;
			if(period.equalsIgnoreCase("Week") || period.equalsIgnoreCase("Today")){
			query5 = session
					.createSQLQuery("select distinct DATE_ADD(DATE_FORMAT(b.Transaction_Timestamp,'%Y-%m-%d %H:00:00'),INTERVAL      ( (CAST(((MINUTE(b.Transaction_Timestamp))/10) as DECIMAL))*10 ) MINUTE) as Transaction_Timestamp, a.Parameter_Value, b.Serial_Number from asset_monitoring_detail_data a,asset_monitoring_header b  "
							+ "where b.Transaction_Timestamp >='"
							+ startDateTime
							+ "' and b.Serial_Number in ("
							+ serialNumberAsStringList
							+ ") and a.Transaction_Number=b.Transaction_Number and a.Parameter_ID="
							+ parameterId
							+ " order by b.Serial_Number ,b.Transaction_Timestamp ASC");
		}
			else {
			query5 = session
			.createSQLQuery("select distinct DATE_ADD(DATE_FORMAT(b.Transaction_Timestamp,'%Y-%m-%d %H:00:00'),INTERVAL      ( (CAST(((MINUTE(b.Transaction_Timestamp))/10) as DECIMAL))*10 ) MINUTE) as Transaction_Timestamp, a.Parameter_Value, b.Serial_Number from asset_monitoring_detail_data a,asset_monitoring_header b  "
					+ "where b.Transaction_Timestamp >='"
					+ startDateTime
					+ "' and b.Transaction_Timestamp <='"
					+endDateTime
					+ "' and b.Serial_Number in ("
					+ serialNumberAsStringList
					+ ") and a.Transaction_Number=b.Transaction_Number and a.Parameter_ID="
					+ parameterId
					+ " order by b.Serial_Number ,b.Transaction_Timestamp ASC");
		}
			//ended on 22nd oct 2013...ID20131022
			List resultList = query5.list();
			Iterator itr1 = resultList.iterator();
			double EngineRunningBand1 = 0.0;
			double EngineRunningBand2 = 0.0;
			double EngineRunningBand3 = 0.0;
			double EngineRunningBand4 = 0.0;
			double EngineRunningBand5 = 0.0;
			double EngineRunningBand6 = 0.0;
			double EngineRunningBand7 = 0.0;
			double EngineRunningBand8 = 0.0;
			double workingTime = 0.0;
			double idleTime = 0.0;
			double EngineRunDuration = 0.0;
			double machineUtilizationPerct1 = 0.0;
			String transactionDate = null;
			String transactionDay = null;
			String presentStatus = null,prevStatus="0";
			String serialNumber = null;
			String prevtransactionDate = null;
			Object[] result1 = null;
			String prevSerialNumber = null;
			TreeMap<String, TreeMap<String,ArrayList<String>>> otherValuesMap = new TreeMap<String,TreeMap<String, ArrayList<String>>>();
			TreeMap<String,ArrayList<String>> otherValuesTimeMap = new TreeMap<String, ArrayList<String>>();
			TreeMap<String,ArrayList<String>> otherValuesTimeMaptemp = new TreeMap<String, ArrayList<String>>();
			int index = 0;
			timeMap = new TreeMap<String, TreeMap<String, String>>();
			timeMap2 = new TreeMap<String, TreeMap<String, String>>();
			TreeMap<String, String> updatedMap = null, prevSerUpdatedMap = null;
			String startPoint = null;
			TreeMap<String, LinkedList<String>> PINDates = new TreeMap<String, LinkedList<String>>();
			List<String> nonCommunicatedDates = null;
			ArrayList<String> otherValuesList = new ArrayList<String>();
			List<String> tempDateListRange = null;
			while (itr1.hasNext()) {
				result1 = (Object[]) itr1.next();

				String transactionTimeStamp = (String) result1[0];
				startPoint =  transactionTimeStamp.substring(11, 16);
				Timestamp transactionTimeStamp1 = Timestamp
						.valueOf(transactionTimeStamp);
				transactionDate = dateFormat.format(transactionTimeStamp1);
				transactionDay = dayFormat.format(transactionTimeStamp1);
				presentStatus = result1[1].toString();
				serialNumber = result1[2].toString();
				
				if (!serialNumber.equals(prevSerialNumber)) {
					otherValuesTimeMap = new TreeMap<String, ArrayList<String>>();
					timeMap = new TreeMap<String, TreeMap<String,String>>();
					//TimeMap to be displayed till current date and system time in 10 mins bucket form---done by smitha....ID20131021
					newCurrentDayTimeMap =null;
					if(currentDateFinal.equals(transactionDate) && newCurrentDayTimeMap == null){
						newTimeMap = getCurrentDateMap();
						newCurrentDayTimeMap = newTimeMap;
					}else{
						newTimeMap=getTimeMap();
					}
					
					prevStatus="0";
				} else if(serialNumber.equals(prevSerialNumber)) {
					if (!prevtransactionDate.equals(transactionDate)) {						
						//TimeMap to be displayed till current date and system time in 10 mins bucket form---done by smitha ID20131021
						newCurrentDayTimeMap =null;
						prevStatus="0";
						otherValuesTimeMap = new TreeMap<String, ArrayList<String>>();
						timeMap = new TreeMap<String, TreeMap<String,String>>();						
						if(currentDateFinal.equals(transactionDate) && newCurrentDayTimeMap == null){
							newTimeMap = getCurrentDateMap();
							newCurrentDayTimeMap = newTimeMap;
						}else{
							newTimeMap=getTimeMap();
						}
					}
					else if ((prevtransactionDate.equals(transactionDate))){					
					
							startPoint =  transactionTimeStamp.substring(11, 16);
						    
					}
				}			
				
				if(!presentStatus.equals(prevStatus)){
					//TimeMap to be displayed till current date and system time in 10 mins bucket form---done by smitha.....ID20131021
					if(newTimeMap.equals(newCurrentDayTimeMap)){
						updatedMap = currentDateFillMethod(startPoint, presentStatus, newTimeMap);
					}
					else{
					updatedMap = fillMethod(startPoint, presentStatus, newTimeMap);
					}
				}
				else{
					//Keerthi : 11/12/13 : same PIN, same Day, when no change in status
					updatedMap = newTimeMap;
				}					
				timeMap.put(transactionDate, updatedMap);

				if(newSerialNoMap.containsKey(serialNumber)){
				 if (!prevtransactionDate.equals(transactionDate)){					 
					 timeMap2 = newSerialNoMap.get(serialNumber);
					 timeMap2.put(transactionDate, updatedMap);
					 newSerialNoMap.remove(serialNumber);
					 newSerialNoMap.put(serialNumber, timeMap2);
					// Keerthi :25/11/13 : tracking communicated dates for PIN
					 nonCommunicatedDates = PINDates.get(serialNumber);
					 if(nonCommunicatedDates.contains(transactionDate)){
						 nonCommunicatedDates.remove(transactionDate);
					 }					 
					 PINDates.put(serialNumber, (LinkedList<String>) nonCommunicatedDates);
				 }
				 else{
					 if(updatedMap!=null){
						 timeMap2 = newSerialNoMap.get(serialNumber);
						 timeMap2.put(transactionDate, updatedMap);
						 newSerialNoMap.remove(serialNumber);
						 newSerialNoMap.put(serialNumber, timeMap2);
					 }
					
				 }
				}
				else if(!newSerialNoMap.containsKey(serialNumber)){
					newSerialNoMap.put(serialNumber, timeMap);
					// Keerthi :25/11/13 : tracking communicated dates for PIN
//					creating a new copy of date range list
					tempDateListRange = new LinkedList<String>();
					Iterator<String> rangeIterator = dateRangeList.iterator();
					while(rangeIterator.hasNext()){
						tempDateListRange.add(rangeIterator.next());
					}
					nonCommunicatedDates = tempDateListRange;
					if(nonCommunicatedDates.contains(transactionDate)){
						nonCommunicatedDates.remove(transactionDate);
					}					
					PINDates.put(serialNumber, (LinkedList<String>) nonCommunicatedDates);
				}
				
				
				if (otherValuesMap != null
						&& !otherValuesMap.containsKey(serialNumber) || (!prevtransactionDate.equals(transactionDate))) {

					index = 0;
					otherValuesList = new ArrayList<String>();
					otherValuesList.add(index++, transactionDate);
					
					otherValuesList.add(index++, transactionDay);
					
					String queryNick = "select nick_name from AssetEntity where serial_number='"
							+ serialNumber + "'";
					Iterator itrNick = session.createQuery(queryNick).list()
							.iterator();
					String nickName = null;
					while (itrNick.hasNext()) {
						nickName = itrNick.next().toString();
					}					
					otherValuesList.add(index++, nickName);
					String subtime = transactionTimeStamp.substring(0, 10);
					String queryFact = "select c.engineOffHours,c.EngineRunningBand1,c.EngineRunningBand2,c.EngineRunningBand3,c.EngineRunningBand4,c.EngineRunningBand5,c.EngineRunningBand6,c.EngineRunningBand7,c.EngineRunningBand8 from AssetMonitoringFactDataDayAgg c where  c. serialNumber='"
							+ serialNumber
							+ "' and substring(c.timeKey,1,10) ='"
							+ subtime
							+ "' order by c.timeKey ASC";
					Iterator itrFact = session.createQuery(queryFact).list()
							.iterator();
					Object[] resultFact = null;					
					while (itrFact.hasNext()) {
						resultFact = (Object[]) itrFact.next();						
						if (resultFact[0] != null) {
							double a = (Double) resultFact[0];
							String b = String.valueOf(a);
							otherValuesList.add(index++, b);			
							
						}
						else{
							otherValuesList.add(index++, "0");
						}
						
						EngineRunningBand1 = (Double) resultFact[1];
						EngineRunningBand2 = (Double) resultFact[2];
						EngineRunningBand3 = (Double) resultFact[3];
						EngineRunningBand4 = (Double) resultFact[4];
						EngineRunningBand5 = (Double) resultFact[5];
						EngineRunningBand6 = (Double) resultFact[6];
						EngineRunningBand7 = (Double) resultFact[7];
						EngineRunningBand8 = (Double) resultFact[8];

						workingTime = (EngineRunningBand3 + EngineRunningBand4
								+ EngineRunningBand5 + EngineRunningBand6
								+ EngineRunningBand7 + EngineRunningBand8);
						idleTime = (EngineRunningBand1 + EngineRunningBand2);
						EngineRunDuration = (workingTime + idleTime);
					}
				
					otherValuesList.add(index++,String.valueOf(EngineRunDuration));
					otherValuesList.add(index++, String.valueOf(workingTime));
					machineUtilizationPerct1 = (workingTime / 24) * 100;
					otherValuesList.add(index++,String.valueOf(machineUtilizationPerct1));
					otherValuesTimeMap.put(transactionDate,otherValuesList);
					
					if(!otherValuesMap.containsKey(serialNumber)){
						otherValuesMap.put(serialNumber, otherValuesTimeMap);
					}
					else if(prevtransactionDate!=null){
						
						if(!prevtransactionDate.equals(transactionDate)){
							otherValuesTimeMaptemp=otherValuesMap.get(serialNumber);
							otherValuesTimeMaptemp.put(transactionDate, otherValuesList);
							otherValuesMap.remove(serialNumber);
							otherValuesMap.put(serialNumber, otherValuesTimeMaptemp);
						}
					}
				}
				prevSerialNumber = serialNumber;				
				prevSerUpdatedMap = updatedMap;
				prevtransactionDate = transactionDate;
				prevStatus = presentStatus;
			}
			
//putting data for non-communicated days...the VIN's existing in header table, not in fact table.
			
			AssetDetailsBO assetDetailsBO = new AssetDetailsBO();
			AssetEntity assetEntity = null;			
			String nonCommDate = null;
			if(PINDates.size()>0 && !PINDates.isEmpty() ){
				for (String PIN : PINDates.keySet() ) {
					assetEntity = assetDetailsBO.getAssetEntity(PIN);
					nonCommunicatedDates =  PINDates.get(PIN);					
					Iterator<String> dateIterator = nonCommunicatedDates.iterator();
					while(dateIterator.hasNext()){
						nonCommDate = dateIterator.next();
						infoLogger.info(PIN + " non communicated date : "+nonCommDate);
						index =0;
						otherValuesList = new ArrayList<String>();
						otherValuesList.add(index++, nonCommDate);
						Date nonCommunicatedDate=dateFormat.parse(nonCommDate);
						otherValuesList.add(index++, String.valueOf(nonCommunicatedDate));
						if(assetEntity!=null){
							otherValuesList.add(index++,assetEntity.getNick_name());
						}
						otherValuesList.add(index++,"100.0");
						otherValuesList.add(index++,"0");
						otherValuesList.add(index++,"0");
						otherValuesList.add(index++,"0");
						otherValuesTimeMap = new TreeMap<String, ArrayList<String>>();
						otherValuesTimeMap.put(nonCommDate, otherValuesList);
						
						if(!otherValuesMap.containsKey(PIN)){
							otherValuesMap.put(serialNumber, otherValuesTimeMap);
						}
						else{
							otherValuesTimeMaptemp=otherValuesMap.get(PIN);
							if(otherValuesTimeMaptemp!=null){
								otherValuesTimeMaptemp.put(nonCommDate, otherValuesList);
								otherValuesMap.put(PIN, otherValuesTimeMaptemp);
							}
							
						}				
					
						if(nonCommDate.equals(currentDateFinal)){
							newDateTimeMap=getCurrentDateMap();
						}else {
							newDateTimeMap=getTimeMap();
						}
						if(newSerialNoMap!=null){
							 timeMap2 = newSerialNoMap.get(PIN);
							 if(timeMap2!=null){
								 timeMap2.put(nonCommDate, newDateTimeMap);
								 newSerialNoMap.put(PIN, timeMap2);
							 }
							
						}
											
					}
					
				}
			}
			 otherValuesList = null;
			for (String entry : otherValuesMap.keySet()) {
				otherValuesTimeMap = otherValuesMap.get(entry);
				for (String dateObj : otherValuesTimeMap.keySet()) {
				otherValuesList = otherValuesTimeMap.get(dateObj);
				implObj = new UtilizationDetailReportImpl();

				implObj.setSerialNumber(entry);
				implObj.setDateInString(otherValuesList.get(0));
				implObj.setDayInString(otherValuesList.get(1));
				implObj.setNickName(otherValuesList.get(2));
				Double engineOffDuration = Double.parseDouble(otherValuesList.get(3));
				implObj.setEngineOffDuration(Double.valueOf(df2.format((Double)(engineOffDuration))));
				implObj.setEngineRunDuration(Double.valueOf(df2.format((Double)(Double.parseDouble(otherValuesList.get(4))))));
				implObj.setEngineWorkingDuration(Double.valueOf(df2.format((Double)(Double.parseDouble(otherValuesList.get(5))))));
				//Smitha : 10th oct 2013
				if(otherValuesList.size()==7){
				implObj.setMachineUtilizationPerct(Double.valueOf(df2.format((Double)(Double.parseDouble(otherValuesList.get(6))))));
				}
				//ended 10th oct 2013
				//setting time and status map here
				timeMap = newSerialNoMap.get(entry);
				updatedMap=timeMap.get(dateObj);				
				if(updatedMap==null){
					if(otherValuesList.get(0).equals(currentDateFinal)){
						newDateTimeMaptemp=getCurrentDateMap();						
						implObj.setTimeMachineStatusMap(newDateTimeMaptemp);
					}else {
						newDateTimeMaptemp=getTimeMap();
						implObj.setTimeMachineStatusMap(newDateTimeMaptemp);
					}
				}else{
					implObj.setTimeMachineStatusMap(updatedMap);
				}
				implRespList.add(implObj);
			}
		}
			

			
			//putting data for non-communicated PINs
			
			Iterator iterResult = implRespList.iterator();
			String serNum=null;
			iterResult = serialNumberList.iterator();
			
			while (iterResult.hasNext()) {
				serNum = (String) iterResult.next();
				if(!newSerialNoMap.containsKey(serNum)){
					infoLogger.info(serNum +" not present in neither header nor fact table");
					assetEntity = assetDetailsBO.getAssetEntity(serNum);
					for(String date : dateRangeList){
						implObj = new UtilizationDetailReportImpl();
						implObj.setDateInString(date);	
						Date nonCommunicatedDate=dateFormat.parse(date);
						implObj.setDayInString(dayFormat.format(nonCommunicatedDate));
						implObj.setDaysWithNoEngineRun(0);
//						Keerthi : 03/12/13 : Non communicated pins : engine off % is 100
						implObj.setEngineOffDuration(100.0d);
						implObj.setEngineRunDuration(0.0d);
						implObj.setEngineWorkingDuration(0.0d);
						implObj.setMachineUtilizationPerct(0.0d);						
						if(assetEntity!=null){
						implObj.setNickName(assetEntity.getNick_name());
						}
						implObj.setSerialNumber(serNum);
						if(date.equals(currentDateFinal)){
							newDateTimeMap=getCurrentDateMap();
						}else {
							newDateTimeMap=getTimeMap();
						}
						implObj.setTimeMachineStatusMap(newDateTimeMap);
						implRespList.add(implObj);
						}	
				}
				
			}
			infoLogger.info(" final impl resp list size "+implRespList.size());
		}
		catch (Exception e) {
			e.printStackTrace();
			fatalError.fatal("Exception :" + e);

		}

		finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}

		return implRespList;
	}
	// ******************************************* END of Machine Utilization
	// Detail Report *************************************************

	// ********************************************************* Machine
	// Utilization Detail Service *******************************************
	/**
	 * This method returns the details of Machine Utilization over a given
	 * period of time
	 * 
	 * @param loginId
	 *            userLoginId
	 * @param serialNumber
	 *            VIN as input String
	 * @param period
	 *            Period for the machine utilization details has to be returned
	 * @return
	 */
	public List<UtilizationDetailServiceImpl> getUtilizationDetailService(
			String loginId, String serialNumber, String period) {
		List<UtilizationDetailServiceImpl> implResponseList = new LinkedList<UtilizationDetailServiceImpl>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
			
			
		try {
			Query query = null;

			if ((period.equals("Today")) || (period.equals("Week"))) {
				List<String> serNumList = new LinkedList<String>();
				serNumList.add(serialNumber);

				List<UtilizationDetailReportImpl> implList = getUtilizationDetailReport(
						loginId, null, null, null, null, period, serNumList);

				for (int i = 0; i < implList.size(); i++) {
					UtilizationDetailServiceImpl response = new UtilizationDetailServiceImpl();
					response.setSerialNumber(implList.get(i).getSerialNumber());
					response.setTimeMachineStatusMap(implList.get(i)
							.getTimeMachineStatusMap());
					response.setTimeDuration(implList.get(i).getDateInString());

					implResponseList.add(response);
				}
			}

			if (!(session.isOpen())) {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			else if (period.equals("Month")) {
				Calendar c = Calendar.getInstance();
				// get the current year
				int currentYear = c.get(Calendar.YEAR);
				// get the first date of current month
				c.set(c.get(c.YEAR), c.get(c.MONTH), 1);
				// get the week number corresponding to first date - which will
				// be first week of the current month

				int weekNumber = c.get(Calendar.WEEK_OF_YEAR);
				Date date = new Date();
				c.setTime(date);
				double workingTimePercentage = 0;
				
				String queryString = " select timeCount,EngineRunningBand1,EngineRunningBand2,EngineRunningBand3,EngineRunningBand4,EngineRunningBand5,EngineRunningBand6,EngineRunningBand7,EngineRunningBand8,engineOffHours  from AssetMonitoringFactDataWeekAgg where timeCount >= "
						+ weekNumber
						+ " and year = "
						+ currentYear
						+ " and serialNumber = '" + serialNumber + "' ";				
				query = session.createQuery(queryString);
				Iterator itr = query.list().iterator();
				Object[] result = null;

				while (itr.hasNext()) {
					result = (Object[]) itr.next();
					UtilizationDetailServiceImpl implResp = new UtilizationDetailServiceImpl();
					implResp.setSerialNumber(serialNumber);
					implResp.setPeriod("Week");

					implResp.setTimeDuration(result[0].toString());
					Double workingTime = (Double) result[1]
							+ (Double) result[2] + (Double) result[3]
							+ (Double) result[4] + (Double) result[5]
							+ (Double) result[6] + (Double) result[7]
							+ (Double) result[8];
					
					Double TotalTimeDuration = workingTime
							+ (Integer) result[9];

					workingTimePercentage = (workingTime * 100)
							/ (TotalTimeDuration);
				
					implResp.setWorkingHourPerct(workingTimePercentage);

					implResponseList.add(implResp);
				}
			}

			else if (period.equals("Quarter")) {
				Calendar c = Calendar.getInstance();
				// get the current year
				int currentYear = c.get(Calendar.YEAR);

				Date currentDate = new Date();
				// get the current Quarter
				int quarter = (currentDate.getMonth() / 3) + 1;
				List<Integer> monthValues = new LinkedList<Integer>();

				if (quarter == 1) {
					monthValues.add(1);
					monthValues.add(2);
					monthValues.add(3);
				} else if (quarter == 2) {
					monthValues.add(4);
					monthValues.add(5);
					monthValues.add(6);
				} else if (quarter == 3) {
					monthValues.add(7);
					monthValues.add(8);
					monthValues.add(9);
				} else if (quarter == 4) {
					monthValues.add(10);
					monthValues.add(11);
					monthValues.add(12);
				}

				ListToStringConversion conversion = new ListToStringConversion();
				String monthList = conversion.getIntegerListString(monthValues)
						.toString();

				String queryString = " select timeCount,EngineRunningBand1,EngineRunningBand2,EngineRunningBand3,EngineRunningBand4,EngineRunningBand5,EngineRunningBand6,EngineRunningBand7,EngineRunningBand8,engineOffHours from AssetMonitoringFactDataMonthAgg where timeCount in ("
						+ monthList
						+ " ) and year = "
						+ currentYear
						+ " and serialNumber = '" + serialNumber + "' ";
				
				query = session.createQuery(queryString);
				Iterator itr = query.list().iterator();
				Object[] result = null;

				while (itr.hasNext()) {
					result = (Object[]) itr.next();
					UtilizationDetailServiceImpl implResp = new UtilizationDetailServiceImpl();
					implResp.setSerialNumber(serialNumber);
					implResp.setPeriod("Month");

					implResp.setTimeDuration(result[0].toString());
					Double workingTime = (Double) result[1]
							+ (Double) result[2] + (Double) result[3]
							+ (Double) result[4] + (Double) result[5]
							+ (Double) result[6] + (Double) result[7]
							+ (Double) result[8];
					Double TotalTimeDuration = workingTime
							+ (Integer) result[9];

					double workingTimePercentage = (workingTime * 100)
							/ (TotalTimeDuration);
					implResp.setWorkingHourPerct(workingTimePercentage);

					implResponseList.add(implResp);
				}
			}

			else if (period.equals("Year")) {

				Calendar cal = Calendar.getInstance();
				// get the current year
				int currentYear = cal.get(Calendar.YEAR);

				Date currentDate = new Date();
				// get the current Quarter
				int quarter = (currentDate.getMonth() / 3) + 1;

				String queryString = " select timeCount,EngineRunningBand1,EngineRunningBand2,EngineRunningBand3,EngineRunningBand4,EngineRunningBand5,EngineRunningBand6,EngineRunningBand7,EngineRunningBand8,engineOffHours from AssetMonitoringFactDataQuarterAgg where timeCount <= "
						+ quarter
						+ " and serialNumber = '"
						+ serialNumber
						+ "' and year = " + currentYear;
				
				query = session.createQuery(queryString);
				Iterator itr = query.list().iterator();
				Object[] result = null;

				while (itr.hasNext()) {
					result = (Object[]) itr.next();
					UtilizationDetailServiceImpl implResp = new UtilizationDetailServiceImpl();
					implResp.setSerialNumber(serialNumber);
					implResp.setPeriod("Quarter");

					implResp.setTimeDuration(result[0].toString());
					Double workingTime = (Double) result[1]
							+ (Double) result[2] + (Double) result[3]
							+ (Double) result[4] + (Double) result[5]
							+ (Double) result[6] + (Double) result[7]
							+ (Double) result[8];
					Double TotalTimeDuration = workingTime
							+ (Integer) result[9];

					double workingTimePercentage = (workingTime * 100)
							/ (TotalTimeDuration);
					implResp.setWorkingHourPerct(workingTimePercentage);
					implResponseList.add(implResp);
				}

			}
		}

		catch (Exception e) {
			fatalError.fatal("Exception :" + e);
		}

		finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}

		return implResponseList;
	}


	// ********************************************************* END of Machine
	// Utilization Detail Service *******************************************





	
	
	/**
	 * method to fetch list of child ids for given parent tenancy id list
	 * @param tenancyIdList
	 * @return
	 */
	public List<Integer> getSubTenancyIds(List<Integer> tenancyIdList){	
		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		ListToStringConversion conversionObj = new ListToStringConversion();
		String tenancyIdStringList = conversionObj.getIntegerListString(tenancyIdList).toString();
		List<Integer> subTenancyIdList = new ArrayList<Integer>();

		String finalQuery = null;
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;

		try {
			finalQuery = "SELECT t1.childId,t1.parentId FROM  TenancyBridgeEntity  t1 WHERE t1.parentId in("+ tenancyIdStringList+")";
			Query query = session.createQuery(finalQuery);
			Iterator itr = query.list().iterator();
			Object[] result = null;


			while (itr.hasNext()) {
				result = (Object[]) itr.next();
				if (result[0] != null) {
					subTenancyIdList.add((Integer) result[0]);
				}		
			}
		}
		finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		infoLogger.info("Total time taken by getSubTenancyIds() is"+ duration);
		if(subTenancyIdList==null || subTenancyIdList.size()==0){
			return tenancyIdList;
		}
		return subTenancyIdList;
	}



	
	//to calculate start point
	public String calculateStartPoint(String endPoint) {
		//Smitha : 10th oct 2013
		if(endPoint.equals("24:00")){
			endPoint="00:00";
		}
		//ended on 10th oct 2013
		String startPoint =null;
		if(endPoint.length()==4){
			endPoint="0"+endPoint;
		}
		String hourString = endPoint.substring(3);//minute
		String hour1 = endPoint.substring(0, 2);//hour
		int hour1Add = 0;
		if(!hour1.equals("00")){
			hour1Add = Integer.valueOf(hour1);
		}
		
		String hour=null;		
		int hourMin = Integer.valueOf(hourString);
		if (hourMin == 50) {
			hour1Add = hour1Add + 1;
			if(String.valueOf(hour1Add).length()==1){
				hour =String.valueOf(hour1Add);
				hour = "0"+hour;
				startPoint = hour + ":"+"00";
			} 
			//Smitha : 10th oct 2013
			else {
			startPoint = Integer.toString(hour1Add) + ":"+"00";
			}
			//ended on 10th oct 2013
		} else {
			
			hourMin = hourMin + 10;
			hour =String.valueOf(hour1Add);
			if(String.valueOf(hour1Add).length()==1){
				
				hour = "0"+hour;
				
			}
			
			startPoint =hour + ":"+ Integer.toString(hourMin);

		}		
		/*if(startPoint.equals("24:00")){
			startPoint="00:00";
		}*/
		return startPoint;
	}
	//to fill the data between start and end points
	public TreeMap<String, String> fillMethod(String startPoint,
			String status, TreeMap<String, String> updatedMap) {
//		Keerthi : 10/12/13 : skipping 00:00 to the map
		if(!startPoint.trim().equals("00:00")){
			updatedMap.put(startPoint, status);
		}	
		startPoint = calculateStartPoint(startPoint);
		while(!startPoint.equals("24:00")){			
			updatedMap.put(startPoint, status);
			startPoint = calculateStartPoint(startPoint);
		}
		updatedMap.put("24:00", status);
		return updatedMap;
	}
	//TimeMap to be displayed till current date and system time in 10 mins bucket form---done by smitha....ID20131021
	//to return treeMap with data filled till system time for current date 
	public TreeMap<String,String> getCurrentDateMap(){
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
			
			
		TreeMap<String,String> currentDateMap = null;
		TreeMap<String,String> tempCurrentDateMap = null;
		tempCurrentDateMap = getTimeMap();
		//current date and current hour calculation.....18th oct 2013 
		// Create a Date object set to the current date and time   
		Date now = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm ");   
		  
		// Print the date in the default timezone   
		infoLogger.info(df.format(now));					
		String currentTime=df.format(now);	
		 Date currentTime1=null;
		try {
			currentTime1 = (Date)df.parse(currentTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 Date rounded = new Date(Math.round((currentTime1.getTime() / (1000.0 * 60 * 10))) * (1000 * 60 * 10));
		 String exactTime=df.format(rounded);
		 currentDateFinal = exactTime.substring(0, 10);
		 time=exactTime.substring(11);
		
	    //ended of current date and current hour calculation 	
			String startPoint = null;
			String status = null;
				for(String key :tempCurrentDateMap.keySet()){
					if(key.trim().equals(time.trim())){						
						startPoint=time.trim();
						status="h";
						currentDateMap=fillMethod(startPoint, status, tempCurrentDateMap);
					}										
			}
		return currentDateMap;
		
	}
	//TimeMap to be displayed till current date and system time in 10 mins bucket form---done by smitha....ID20131021
	//to fill the data till system time for current date
	public TreeMap<String, String> currentDateFillMethod(String startPoint,
			String status, TreeMap<String, String> updatedMap) {
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		
		String endPoint=time.trim();		
//		Keerthi : 10/12/13 : skipping 00:00 to the map
		if(!startPoint.trim().equals("00:00")){
			updatedMap.put(startPoint, status);
		}		
		startPoint = calculateStartPoint(startPoint);
		while(!startPoint.equals(endPoint)){			
			updatedMap.put(startPoint, status);
			startPoint = calculateStartPoint(startPoint);
		}
		return updatedMap;
	}
	
	//to calculate the number of days between the two dates given
	public long differnceInDays(String startDate,String endDate){
		
		long diffInMillisec=0;
        long diffInDays=0;
       Calendar firstDate =null;
       Calendar secondDate =null;
       int firstDateYear=0,firstDateMonth=0,firstDateRange=0,secondDateYear=0,secondDateMonth=0,secondDateRange=0;
       secondDateYear=Integer.valueOf(startDate.substring(0, 4));
       secondDateMonth=Integer.valueOf(startDate.substring(5, 7));
       secondDateRange=Integer.valueOf(startDate.substring(8));
       
       firstDateYear=Integer.valueOf(endDate.substring(0, 4));
       firstDateMonth=Integer.valueOf(endDate.substring(5, 7));
       firstDateRange=Integer.valueOf(endDate.substring(8));
        try{
                 // Create two calendars instances
                 firstDate = Calendar.getInstance();
                 secondDate = Calendar.getInstance();
               
                //Set the dates
                firstDate.set(firstDateYear, firstDateMonth, firstDateRange);        
                secondDate.set(secondDateYear, secondDateMonth, secondDateRange);

                // Get the difference between two dates in milliseconds     
               diffInMillisec = firstDate.getTimeInMillis() - secondDate.getTimeInMillis();      

               // Get difference between two dates in days
               diffInDays = diffInMillisec / (24 * 60 * 60 * 1000);              

            }catch(Exception ex)
            {
                        ex.printStackTrace();
             }    
		return diffInDays;
	}
}


