package remote.wise.businessobject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.EventEntity;
import remote.wise.businessentity.LandmarkDimensionEntity;
import remote.wise.businessentity.LandmarkFactEntity;
import remote.wise.businessentity.TenancyDimensionEntity;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

/**
 * @author kprabhu5
 * 
 */
public class LandmarkFactDataBO2 {

	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("LandmarkFactDataBO:","businessError");
	/*public static WiseLogger fatalError = WiseLogger.getLogger("LandmarkFactDataBO2:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("LandmarkFactDataBO2:","info");*/
	
	public String setLandmarkFactData() {
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    
		String message = null;
		Date date = new Date();
		String todaysDate = null;
		String today = null;
		Timestamp time_key = null;

		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		try {

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			todaysDate = dateFormat.format(date);
			iLogger.info("TodaysDate " + todaysDate);
			SimpleDateFormat dateFormat1 = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");

			Date todayDate = dateFormat.parse(todaysDate);
			today = dateFormat1.format(todayDate);
			iLogger.info("simpledate   " + today);
			time_key = Timestamp.valueOf(today);
			iLogger.info("time_key  " + time_key);

			Query queryDayAgg = session.createQuery("select max(time_key) From LandmarkFactEntity_DayAgg");
			Iterator itrDayAgg = queryDayAgg.list().iterator();
			Timestamp maxDate = null;
			while (itrDayAgg.hasNext()) {
				maxDate = (Timestamp) itrDayAgg.next();
			}
			if (maxDate == null) {
				// OLAP is null, chk for todays date in asset event, if exists
				// data then insert
				
				 /* select ae.Serial_Number,be.Event_ID,
				  ae.Event_Generated_Time,ld.landmark_id from business_event
				  be,asset_event ae, landmark_asset la, landmark_dimension ld,
				  asset_owners ao, account_dimension ad, tenancy_dimension td
				  where be.Event_Name in ('Arrival','Departure') and
				  be.Event_ID =ae.Event_ID and ae.Serial_Number =
				  la.serial_number and ld.LandMark_ID =la.landmark_id and
				  ae.Serial_Number = ao.Serial_Number and ao.Account_ID =
				  ad.Account_ID and ad.Tenancy_ID = td.Tenancy_ID and
				  ae.Event_Generated_Time like '2013-05-20%'*/
				 
				//DF20190204 @abhishek---->add partition key for faster retrieval
				String queryForLandmarkFact =
					  "SELECT aee.serialNumber,aee.eventGeneratedTime,aee.eventId,aeee.landmarkId,lce.Tenancy_ID"
					+ " FROM EventEntity ee,AssetEventEntity aee, AssetEventExtendedEntity aeee,"
					+ " LandmarkDimensionEntity lde, LandmarkCategoryEntity lce"
					+ " WHERE ee.eventName in ('Arrival','Departure') AND ee.eventId = aee.eventId"
					+ " AND aee.activeStatus=1 AND aee.partitionKey =1 AND aee.assetEventId = aeee.assetEventId AND aeee.landmarkId = lde.landMarkId"
					+ " AND lde.landMarkCategoryId = lce.Landmark_Category_ID AND lce.ActiveStatus=1"
					+ " AND aee.eventGeneratedTime like '"+ todaysDate+ "%'"
					+ " ORDER BY aee.serialNumber,aeee.landmarkId";

				message = populateLandmarkFact(session, queryForLandmarkFact,time_key, "save");
			}
			else {
				// OLAP contains some data
				String OlapLastDate = dateFormat1.format(maxDate);
				if (OlapLastDate.equalsIgnoreCase(today)) {
					
					//  updating part for existing serial no.s
					//DF20190204 @abhishek---->add partition key for faster retrieval
					String queryForLandmarkFactToUpdate = 
							  "SELECT aee.serialNumber,aee.eventGeneratedTime,aee.eventId"
							+ " FROM EventEntity ee,AssetEventEntity aee "
							+ " WHERE ee.eventName in ('Arrival','Departure') AND ee.eventId = aee.eventId AND aee.serialNumber IN"
							+ " (SELECT lfe.serialNumber FROM LandmarkFactEntity_DayAgg lfe WHERE lfe.time_key like '"+ todaysDate+ "%')"
							+ " AND aee.eventGeneratedTime like '"+ todaysDate+ "%' AND aee.activeStatus=1 and aee.partitionKey =1"
							+ " ORDER BY aee.serialNumber";

					message = populateLandmarkFact(session,queryForLandmarkFactToUpdate, time_key, "update");
					
					// todays date matches with max date from fact table
					// insert into fact table except for those serial no in fact table but exists in asset event table
					//DF20190204 @abhishek---->add partition key for faster retrieval
					String queryForLandmarkFactToInsert = 
						  "SELECT aee.serialNumber,aee.eventGeneratedTime,aee.eventId,aeee.landmarkId,lce.Tenancy_ID"
						+ " FROM EventEntity ee,AssetEventEntity aee, AssetEventExtendedEntity aeee,"
						+ " LandmarkDimensionEntity lde, LandmarkCategoryEntity lce"
						+ " WHERE ee.eventName in ('Arrival','Departure') AND ee.eventId = aee.eventId AND aee.serialNumber NOT IN"
						+ " (SELECT lfe.serialNumber FROM LandmarkFactEntity_DayAgg lfe WHERE lfe.time_key like '"+ todaysDate+ "%')"
						+ " AND aee.assetEventId = aeee.assetEventId AND aee.activeStatus=1 and aee.partitionKey =1 AND aeee.landmarkId = lde.landMarkId"
						+ " AND lde.landMarkCategoryId = lce.Landmark_Category_ID AND lce.activeStatus=1"
						+ " AND aee.eventGeneratedTime like '"+ todaysDate+ "%'"
						+ " ORDER BY aee.serialNumber";

					message = populateLandmarkFact(session,queryForLandmarkFactToInsert, time_key, "save");
					
				} else if ((dateFormat.parse(OlapLastDate).compareTo(date)) < 0) {
//					OLAP has some max date less than current date, insert into OLAP for a date range
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(maxDate);
					calendar.add(Calendar.DAY_OF_YEAR, 1);
					Date nextMaxDate = calendar.getTime();
					iLogger.info("check for next date" + nextMaxDate);
					String newMaxDate = dateFormat.format(nextMaxDate);
					String strOLAPMaxLL = newMaxDate + " 00:00:00";
					String strTodayUL = todaysDate + " 23:59:59";
					
					//DF20190204 @abhishek---->add partition key for faster retrieval
					String queryForLandmarkFact =
						  "SELECT aee.serialNumber,aee.eventGeneratedTime,aee.eventId,aeee.landmarkId,lce.Tenancy_ID"
						+ " FROM EventEntity ee,AssetEventEntity aee, AssetEventExtendedEntity aeee,"
						+ " LandmarkDimensionEntity lde, LandmarkCategoryEntity lce"
						+ " WHERE ee.eventName in ('Arrival','Departure') AND ee.eventId = aee.eventId"
						+ " AND aee.assetEventId = aeee.assetEventId AND aeee.landmarkId = lde.landMarkId AND aee.activeStatus=1 and aee.partitionKey =1"
						+ " AND lde.landMarkCategoryId = lce.Landmark_Category_ID AND lce.activeStatus=1"
						+ " AND aee.eventGeneratedTime between '"+ strOLAPMaxLL+ "' AND '"+ strTodayUL+ "'"
						+ " ORDER BY aee.serialNumber,aee.eventGeneratedTime";

					message = populateLandmarkFactForDateRange(session,queryForLandmarkFact);
				}
			}
		} catch (Exception e) {
			fLogger.fatal(e.getMessage());
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
		return message;
	}
	
	/**
	 * method to populate landmark fact entity with necessary data based on query
	 * @param session
	 * @param query
	 * @param time_key
	 * @param action
	 * @return String
	 */
	public String populateLandmarkFact(Session session, String query,Timestamp time_key, String action) {

		
		Query queryForLandmark = session.createQuery(query);
		Iterator iterLandmark = queryForLandmark.list().iterator();
		Object[] result = null;
		int tenancyId = 0, landmarkID = 0,prevLandmarkID=0,eventId = 0,index = 0,departureCount = 0, arrivalCount = 0;
		long toatlDurationAtLandmark=0,longestDurationAtLandmark=0;
		boolean addOtherData = false,resetCount=false;
		String serialNumber = null,message = null,prevSerialNo = null;		
		EventEntity ee = null;
		List<Object> objectList = new ArrayList<Object>();
		Timestamp eventGeneratedTime=null;
		Timestamp arrivalTimestamp =null,departureTimestamp=null;
		HashMap<String, Integer> countMap = null;
		HashMap<String, HashMap<String, Integer>> serialCountMap = new HashMap<String, HashMap<String, Integer>>();
		ArrayList<Integer> otherValuesList = null;
		HashMap<String, ArrayList<Integer>> otherValuesMap = new HashMap<String, ArrayList<Integer>>();
		while (iterLandmark.hasNext()) {
			result = (Object[]) iterLandmark.next();
			if (result[0] != null) {
				serialNumber = ((AssetEntity) result[0]).getSerial_number().getSerialNumber();
			}
			if(result[2]!=null){
				eventGeneratedTime =(Timestamp)result[2];
			}
			if(result[3]!=null){
				landmarkID =(Integer)result[3];
			}
			
			if (serialCountMap == null) {
				serialCountMap = new HashMap<String, HashMap<String, Integer>>();
				serialCountMap.put(serialNumber, null);
			}
			if(serialNumber.equals(prevSerialNo)){
				if(landmarkID!=prevLandmarkID){
					resetCount=true;
				}
			}
			else if (!serialNumber.equals(prevSerialNo)) {
				resetCount=true;
			}
			if(resetCount){				
				departureCount = 0;
				arrivalCount = 0;
				addOtherData = true;
			} 
			if (result[2] != null) {
				ee = (EventEntity) result[2];
				eventId = ee.getEventId();
			}
			if (eventId == 22) {
				++arrivalCount;
				arrivalTimestamp = eventGeneratedTime;
			} else if (eventId == 23) {
				++departureCount;
				departureTimestamp = eventGeneratedTime;
			}
			toatlDurationAtLandmark = getTimeDiffInMilliseconds(arrivalTimestamp, departureTimestamp);

			if (serialCountMap.containsKey(serialNumber)) {
				serialCountMap.remove(serialNumber);				
			}
			
			countMap = new HashMap<String, Integer>();				
			countMap.put("ArrivalCount", arrivalCount);
			countMap.put("DepartureCount", departureCount);
			serialCountMap.put(serialNumber, countMap);	
			
			prevSerialNo = serialNumber;
			prevLandmarkID = landmarkID;
				
			/*if (serialNumber == prevSerialNo) {
				
					countMap = new HashMap<String, Integer>();
					countMap.put("ArrivalCount", arrivalCount);
					countMap.put("DepartureCount", departureCount);
					serialCountMap.put(serialNumber, countMap);				
			}

			else 
				if (serialNumber != prevSerialNo) {
				flag = true;
				countMap = new HashMap<String, Integer>();
				countMap.put("ArrivalCount", arrivalCount);
				countMap.put("DepartureCount", departureCount);
				serialCountMap.put(serialNumber, countMap);
			}*/

			
		
		/*while (iterLandmark.hasNext()) {
			result = (Object[]) iterLandmark.next();
			if (result[0] != null) {
				serialNumber = ((AssetEntity) result[0]).getSerial_number().getSerialNumber();
			}
			if (prevSerialNo == null) {
				prevSerialNo = serialNumber;
			}
			if (result[2] != null) {
				ee = (EventEntity) result[2];
				eventId = ee.getEventId();
			}
			if (result[3] != null) {
				landmarkID = (Integer) result[3];
			}
			if (prevSerialNo != serialNumber) {
				countMap = new HashMap<String, Integer>();
				countMap.put("ArrivalCount", arrivalCount);
				countMap.put("DepartureCount", departureCount);
				serialCountMap.put(prevSerialNo, countMap);
				departureCount = 0;
				arrivalCount = 0;
			}
			if (eventId == 22) {
				++arrivalCount;
			} else if (eventId == 23) {
				++departureCount;
			}
			prevSerialNo = serialNumber;*/

			if (action.equalsIgnoreCase("save")) {
				if (otherValuesMap != null && !otherValuesMap.containsKey(serialNumber)) {

					index = 0;
					otherValuesList = new ArrayList<Integer>();
					
					if (result[4] != null) {
						tenancyId = (Integer) result[4];
					}
					otherValuesList.add(index++, landmarkID);
					otherValuesList.add(index++, tenancyId);

					otherValuesMap.put(serialNumber, otherValuesList);
				}
			}
		 }	
		
		/*if (prevSerialNo != null) {
			countMap = new HashMap<String, Integer>();
			countMap.put("ArrivalCount", arrivalCount);
			countMap.put("DepartureCount", departureCount);
			serialCountMap.put(prevSerialNo, countMap);
		}*/

		if (action.equalsIgnoreCase("save")) {
			message = saveToFactTable(session, serialCountMap, otherValuesMap,
					time_key);
		} else if (action.equalsIgnoreCase("update")) {
			message = updateFactTable(session, serialCountMap, time_key);
		}

		return message;
	}
	
	/**
	 * method to update the landmark fact table with data for existing serial no.s
	 * @param session
	 * @param serialCountMap
	 * @param time_key
	 * @return String
	 */
	public String updateFactTable(Session session,HashMap<String, HashMap<String, Integer>> serialCountMap,Timestamp time_key) {
		Logger iLogger = InfoLoggerClass.logger;

		String message = null,today=null;
		int aCount = 0, dCount = 0,rows = 0;
		Query updateQuery = null;
		
		try {
			// update here
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			today = dateFormat.format(time_key);
			for (String key : serialCountMap.keySet()) {
				aCount = serialCountMap.get(key).get("ArrivalCount");
				dCount = serialCountMap.get(key).get("DepartureCount");
				updateQuery = session.createQuery(
								  "UPDATE LandmarkFactEntity_DayAgg lfeda "
								+ " SET lfeda.noOfArrivals=" + aCount+ ",lfeda.noOfDepartures = " + dCount
								+ " WHERE lfeda.serialNumber = '" + key	+ "' AND " + " lfeda.time_key like '" + today+ "%'");
				iLogger.info("query for updation :" + updateQuery);
				rows = updateQuery.executeUpdate();
				if(rows>0){
					message = "SUCCESS";
				}
				else{
					message = "FAILURE";
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			message = "ERROR";
		}
		return message;
	}
	
	/**
	 * method to populate landmark fact data for a date range
	 * @param session
	 * @param query
	 * @return String
	 */
	public String populateLandmarkFactForDateRange(Session session, String query) {

		String message = null,serialNumber = null,prevSerialNo = null,prevEventGeneratedTime = null,eventGeneratedString = null;
		int landmarkID = 0,eventId = 0, departureCount = 0, arrivalCount = 0,tenancyId = 0,index = 0;
		EventEntity ee = null;
		ArrayList<Integer> otherValuesList = null;
		Timestamp eventGeneratedTime = null;
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		boolean flag = false;
		HashMap<String, Integer> countMap = null;
		// key ->serialno->eventgeneratedtime->arrivalCount->0
		HashMap<String, HashMap<String, HashMap<String, Integer>>> serialCountMap = null;			
		HashMap<String, HashMap<String, Integer>> dateMap = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String, ArrayList<Integer>> otherValuesMap = new HashMap<String, ArrayList<Integer>>();
		
		Query queryForLandmark = session.createQuery(query);
		Iterator iterLandmark = queryForLandmark.list().iterator();
		Object[] result = null;
		
		while (iterLandmark.hasNext()) {
			result = (Object[]) iterLandmark.next();
			if (result[0] != null) {
				serialNumber = ((AssetEntity) result[0]).getSerial_number().getSerialNumber();
			}
			if (result[1] != null) {
				eventGeneratedTime = (Timestamp) result[1];
				eventGeneratedString = dateFormat1.format(eventGeneratedTime);
			}

			if (serialCountMap == null) {
				serialCountMap = new HashMap<String, HashMap<String, HashMap<String, Integer>>>();
				serialCountMap.put(serialNumber, null);
			}
			if (!serialNumber.equals(prevSerialNo)) {
				departureCount = 0;
				arrivalCount = 0;

			} else {
				if (!eventGeneratedString.equals(prevEventGeneratedTime)) {
					departureCount = 0;
					arrivalCount = 0;
				}
			}

			if (result[2] != null) {
				ee = (EventEntity) result[2];
				eventId = ee.getEventId();
			}
			if (eventId == 22) {
				++arrivalCount;
			} else if (eventId == 23) {
				++departureCount;
			}

			if (serialNumber == prevSerialNo) {
				if (!eventGeneratedString.equals(prevEventGeneratedTime)) {

					if (!serialCountMap.containsKey(serialNumber)) {
						serialCountMap.put(serialNumber, null);
					}
					dateMap = serialCountMap.get(serialNumber);
					countMap = new HashMap<String, Integer>();
					countMap.put("ArrivalCount", arrivalCount);
					countMap.put("DepartureCount", departureCount);
					if (dateMap == null) {
						dateMap = new HashMap<String, HashMap<String, Integer>>();
					}
					dateMap.put(eventGeneratedString, countMap);
					serialCountMap.remove(serialNumber);
					serialCountMap.put(serialNumber, dateMap);
				} else {
					dateMap = serialCountMap.get(serialNumber);
					dateMap.remove(eventGeneratedString);
					countMap = new HashMap<String, Integer>();
					countMap.put("ArrivalCount", arrivalCount);
					countMap.put("DepartureCount", departureCount);
					dateMap.put(eventGeneratedString, countMap);
					serialCountMap.put(serialNumber, dateMap);

				}
			}

			else if (serialNumber != prevSerialNo) {
				flag = true;
				dateMap = new HashMap<String, HashMap<String, Integer>>();
				countMap = new HashMap<String, Integer>();
				countMap.put("ArrivalCount", arrivalCount);
				countMap.put("DepartureCount", departureCount);
				dateMap.put(eventGeneratedString, countMap);
				serialCountMap.put(serialNumber, dateMap);
			}

			prevSerialNo = serialNumber;
			prevEventGeneratedTime = eventGeneratedString;

			if (flag) {

				index = 0;
				otherValuesList = new ArrayList<Integer>();

				if (result[3] != null) {
					landmarkID = (Integer) result[3];
				}
				if (result[4] != null) {
					tenancyId = (Integer) result[4];
				}
				otherValuesList.add(index++, landmarkID);
				otherValuesList.add(index++, tenancyId);

				otherValuesMap.put(serialNumber, otherValuesList);
				flag = false;
			}

		}

		message = saveToFactTableForDateRange(session, serialCountMap,otherValuesMap);
		return message;
	}
	
	/**
	 * method to save data for landmark fact table
	 * @param session
	 * @param serialCountMap
	 * @param otherValuesMap
	 * @param time_key
	 * @return String
	 */
	public String saveToFactTable(Session session,HashMap<String, HashMap<String, Integer>> serialCountMap,
			HashMap<String, ArrayList<Integer>> otherValuesMap,
			Timestamp time_key) {
		
		String message = "SUCCESS";
		ArrayList<Integer> otherValuesList = null;
		LandmarkFactEntity landmarkFactEntity = null;
		
		TenancyDimensionEntity tdEntity = null;
		LandmarkDimensionEntity ldEntity = null;
		if(otherValuesMap!=null){
			for (String key : otherValuesMap.keySet()) {
				
				otherValuesList = otherValuesMap.get(key);
				landmarkFactEntity = new LandmarkFactEntity();
				landmarkFactEntity.setSerialNumber(key);
				ldEntity = new LandmarkDimensionEntity();
				ldEntity.setLandMarkId(otherValuesList.get(0));
//				landmarkFactEntity.setLandmarkId(ldEntity);
				landmarkFactEntity.setTime_key(time_key);
				tdEntity = new TenancyDimensionEntity();
				tdEntity.setTenancyId(otherValuesList.get(1));
//				landmarkFactEntity.setTenancyId(tdEntity);
				if(serialCountMap!=null){
					landmarkFactEntity.setNoOfArrivals(serialCountMap.get(key).get("ArrivalCount"));
					landmarkFactEntity.setNoOfDepartures(serialCountMap.get(key).get("DepartureCount"));
				}
				
				session.save("LandmarkFactEntity_DayAgg", landmarkFactEntity);
			}
		}
		
		return message;
	}
	
	/**
	 * method to save data for landmark fact table for a date range
	 * @param session
	 * @param serialCountMap
	 * @param otherValuesMap
	 * @param time_key
	 * @return String
	 */
	public String saveToFactTableForDateRange(Session session,HashMap<String, HashMap<String, HashMap<String, Integer>>> serialCountMap,
			HashMap<String, ArrayList<Integer>> otherValuesMap) {
		
		String message = "SUCCESS";
		HashMap<String, HashMap<String, Integer>> dateMap;
		LandmarkFactEntity landmarkFactEntity = null;
		ArrayList<Integer> otherValuesList = null;
		TenancyDimensionEntity tdEntity = null;
		LandmarkDimensionEntity ldEntity = null;
		if(otherValuesMap!=null){
			for (String key : otherValuesMap.keySet()) {
				otherValuesList = otherValuesMap.get(key);
				if(serialCountMap!=null){
					dateMap = serialCountMap.get(key);
					if (dateMap != null) {
						for (String time_key : dateMap.keySet()) {
							landmarkFactEntity = new LandmarkFactEntity();
							landmarkFactEntity.setSerialNumber(key);
							ldEntity = new LandmarkDimensionEntity();
							ldEntity.setLandMarkId(otherValuesList.get(0));
//							landmarkFactEntity.setLandmarkId(ldEntity);
							tdEntity = new TenancyDimensionEntity();
							tdEntity.setTenancyId(otherValuesList.get(1));
//							landmarkFactEntity.setTenancyId(tdEntity);							
							landmarkFactEntity.setNoOfArrivals(dateMap.get(time_key).get("ArrivalCount"));
							landmarkFactEntity.setNoOfDepartures(dateMap.get(time_key).get("DepartureCount"));
							landmarkFactEntity.setTime_key(Timestamp.valueOf(time_key+ " 00:00:00"));
							session.save("LandmarkFactEntity_DayAgg",landmarkFactEntity);
						}
					}
				}				
			}
		}
		
		return message;
	}
	
	public long getTimeDiffInMilliseconds(Timestamp arrivalTimestamp, Timestamp departureTimestamp){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		long diffInMilliseconds = 0;
		if(arrivalTimestamp!=null && departureTimestamp!=null){
			String arrivalTimeString = dateFormat.format(arrivalTimestamp);
			String departureTimeString = dateFormat.format(departureTimestamp);
			
			try{
				Date arrivalDate = dateFormat.parse(arrivalTimeString);
				Date departureDate = dateFormat.parse(departureTimeString);			   
				diffInMilliseconds = Math.abs(departureDate.getTime() - arrivalDate.getTime()); 
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return diffInMilliseconds;
	}
}
