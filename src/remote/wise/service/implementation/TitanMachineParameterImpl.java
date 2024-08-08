package remote.wise.service.implementation;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;


//import javax.persistence.criteria.CriteriaBuilder.In;

import org.hibernate.Query;
import org.hibernate.Session;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.businessobject.TitanMachineParameterBO;
import remote.wise.dal.DynamicAMH_DAL;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.AssetMonitoringParametersDAO;
import remote.wise.service.implementation.UtilizationDetailReportImpl;
import remote.wise.util.DateUtil;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;

import org.apache.logging.log4j.Logger;

public class TitanMachineParameterImpl {


	
	String currentDateFinal = null;
	String time = null;
	public HashMap<String, HashMap<String, Double>> getFleetUtilizationDetails(
			String loginId, 
					String vinNumber, String period) {
		
			HashMap<String,HashMap<String,Double>> response = new HashMap<String,HashMap<String,Double>>();
			Logger iLogger = InfoLoggerClass.logger;
			
			HashMap asset2SegID = new HashMap();
			
			List<String> dateRangeList = new LinkedList<String>();
		
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			Iterator iterator = null;
			new SimpleDateFormat("yyyy-MM-dd");
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			new SimpleDateFormat("EEEE");
			dateRangeList = getDateRangeList(period);
			Calendar.getInstance();
			Date now = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm ");
			try {
				new DecimalFormat("###.#");
				/*if (serialNumList == null) {}
				else{
					AssetEntity asset = null;
					for(String assetID : serialNumList){*/
					AssetEntity asset = new AssetDetailsBO().getAssetEntity(vinNumber);
						asset2SegID.put(asset.getSerial_number().getSerialNumber(), asset.getSegmentId());
					//}
				//}
				
			
				iLogger.info(df.format(now));
				String currentTime = df.format(now);
				Date currentTime1 = null;
				try {
					currentTime1 = (Date) df.parse(currentTime);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Date rounded = new Date(
						Math.round((currentTime1.getTime() / (1000.0 * 60 * 10)))
						* (1000 * 60 * 10));
				String exactTime = df.format(rounded);
				if (exactTime != null) {
					currentDateFinal = exactTime.substring(0, 10);
					time = exactTime.substring(11);
				}
		
				iterator = dateRangeList.iterator();
				
				
				String currDate = null;	
				//List resultList = null;
				HashMap<String,Double> resultList = null;
				
				
				
				/*List parametreIDList = new LinkedList();
				parametreIDList.add(parameterId);*/
				
				while (iterator.hasNext()) {
					currDate =(String)iterator.next();
		
					
					
					resultList = new TitanMachineParameterBO().getAMPValuesInTxnRange(asset2SegID, currDate, period);
					
					response.put(currDate,resultList);
					
								
				}
		
		}
		catch(Exception e){
			System.out.println(e);
		}
		return response;
			
	}
	
	public static List<String> getDateRangeList(String period) {
		List<String> dateRangeList = new LinkedList<String>();
		String firstDay = null, secondDay = null, thirdDay = null, fourthDay = null, fifthDay = null, sixthDay = null, seventhDay = null;
		String startDateTime = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = new Date();
		DateUtil dateUtilObj = new DateUtil();
		String stringWeekDate=null; //Declared by Zakir
		Calendar cal = Calendar.getInstance();
		
		if (period.equalsIgnoreCase("Week")) {
			dateUtilObj = dateUtilObj.getCurrentDateUtility(currentDate);
		} else if (period.equalsIgnoreCase("Last Week")) {
			dateUtilObj = dateUtilObj.getPreviousDateUtility(currentDate);
		}
		
		//DF20190107 @ Zakir created the logic to get fleet utilization charts-Monthly (4 weeks)
		//period format will be in Week|yyyy-MM-dd
		else if(period.contains("|")) {
			//splits the string based on pipe
			stringWeekDate = period.split("\\|")[1];
			period = period.split("\\|")[0];
			Date WeekDate = null;
			try {
				WeekDate = dateFormat.parse(stringWeekDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			//System.out.println(period + "\n" + WeekDate);
			dateUtilObj = dateUtilObj.getCurrentDateUtility(WeekDate);
			cal.setTime(WeekDate); 
		}
		
		int week = dateUtilObj.getWeek();
		//System.out.println("Week number::"+week);
		/*Calendar cal = Calendar.getInstance();   Commented by Zakir and created this variable at the beginning of this method*/
		if (period.equalsIgnoreCase("Week")) {
			cal.set(Calendar.WEEK_OF_YEAR, week);
			int currDay=0;
			//DF20190107 @ Zakir created below logic to set currDay as 7 if stringWeekDate is not empty/null so as to get report of 7 days in that week
			if(stringWeekDate==null){
				cal.setTime(new Date());
				 currDay = cal.get(Calendar.DAY_OF_WEEK); //gives the cuurent day number in the week
			}
			else{
				 currDay=7;
			}
		
			//*************************************DF20190107-END******************************************
			
/*				int currDay = cal.get(Calendar.DAY_OF_WEEK); //gives the cuurent day number in the week
* 				Above code needs to be removed 
*/				
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			firstDay = dateFormat.format(cal.getTime());
			
			//System.out.println("First day::"+firstDay);
			
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			secondDay = dateFormat.format(cal.getTime());
			
			//System.out.println("secondDay day::"+secondDay);
			
			cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
			thirdDay = dateFormat.format(cal.getTime());
			//System.out.println("thirdDay day::"+thirdDay);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
			fourthDay = dateFormat.format(cal.getTime());
			//System.out.println("fourthDay day::"+fourthDay);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
			fifthDay = dateFormat.format(cal.getTime());
			//System.out.println("fifthDay day::"+fifthDay);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
			sixthDay = dateFormat.format(cal.getTime());
			//System.out.println("sixthDay day::"+sixthDay);
			
			//DF20190107 @ Zakir added the Day of week for SATURDAY as well for monthly charts
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			seventhDay = dateFormat.format(cal.getTime());
			
            //System.out.println("Cuurent day::"+currDay);
			//DF20170822: SU334449 - Currentday was also included in the daterange list.
			switch (currDay) {
			    case Calendar.SUNDAY:
			       dateRangeList.add(firstDay);
			       break;

			    case Calendar.MONDAY:
			        dateRangeList.add(firstDay);
			        dateRangeList.add(secondDay);
			    	break;
			    case Calendar.TUESDAY:
			    	dateRangeList.add(firstDay);
					dateRangeList.add(secondDay);
					dateRangeList.add(thirdDay);
					break;
			    case Calendar.WEDNESDAY:
			    	dateRangeList.add(firstDay);
					dateRangeList.add(secondDay);
					dateRangeList.add(thirdDay);
					dateRangeList.add(fourthDay);
					break;
			    case Calendar.THURSDAY:
			    	dateRangeList.add(firstDay);
					dateRangeList.add(secondDay);
					dateRangeList.add(thirdDay);
					dateRangeList.add(fourthDay);
					dateRangeList.add(fifthDay);
					break;
			    case Calendar.FRIDAY:
			    	dateRangeList.add(firstDay);
					dateRangeList.add(secondDay);
					dateRangeList.add(thirdDay);
					dateRangeList.add(fourthDay);
					dateRangeList.add(fifthDay);
					dateRangeList.add(sixthDay);
					break;
			    case Calendar.SATURDAY:	
					dateRangeList.add(firstDay);
					dateRangeList.add(secondDay);
					dateRangeList.add(thirdDay);
					dateRangeList.add(fourthDay);
					dateRangeList.add(fifthDay);
					dateRangeList.add(sixthDay);
					dateRangeList.add(seventhDay);
					break;
			}
	

		} else if (period.equalsIgnoreCase("Today")) {
			cal.setTime(new Date());
			startDateTime = dateFormat.format(cal.getTime());
			dateRangeList.add(startDateTime);
		} else if ((period.equalsIgnoreCase("Last Week"))) {
			cal.set(Calendar.WEEK_OF_YEAR, week);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			startDateTime = dateFormat.format(cal.getTime());

			firstDay = dateFormat.format(cal.getTime());
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
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
		return dateRangeList;
	}
	
public TreeMap<String, String> getCurrentDateMap() {
		
		Logger iLogger = InfoLoggerClass.logger;
		
		TreeMap<String, String> currentDateMap = null;
		//tempCurrentDateMap = getTimeMap();
		// current date and current hour calculation.....18th oct 2013
		// Create a Date object set to the current date and time
		Date now = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm ");

		// Print the date in the default timezone
		iLogger.info(df.format(now));
		String currentTime = df.format(now);
		Date currentTime1 = null;
		try {
			java.util.Date date = new java.util.Date();
			new Timestamp(date.getTime());

			currentTime1 = (Date) df.parse(currentTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date rounded = new Date(
				Math.round((currentTime1.getTime() / (1000.0 * 60 * 10)))
				* (1000 * 60 * 10));
		/*currentTimestamp = new Timestamp(
				Math.round((currentTime1.getTime() / (1000.0 * 60 * 10)))
		 * (1000 * 60 * 10));*/
		String exactTime = df.format(rounded);

		currentDateFinal = exactTime.substring(0, 10);
		time = exactTime.substring(11);

		//currentDateMap = fillForToday(currentTimestamp, status,
				//tempCurrentDateMap);
		return currentDateMap;

	}


}
