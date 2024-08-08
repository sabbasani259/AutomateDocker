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

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.LandmarkDimensionEntity;
import remote.wise.businessentity.LandmarkEntity;
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
public class LandmarkFactDataBO {

	/*public static WiseLogger fatalError = WiseLogger.getLogger("LandmarkFactDataBO:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("LandmarkFactDataBO:","info");*/
	
	
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
			iLogger.info("todaysDate : "+todaysDate);
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Date todayDate = dateFormat.parse(todaysDate);
			today = dateFormat1.format(todayDate);
			iLogger.info("simpledate   " + today);
			time_key = Timestamp.valueOf(today);
			iLogger.info("time_key  " + time_key);

			Query queryDayAgg = session
					.createQuery("select max(time_key) From LandmarkFactEntity_DayAgg");
			Iterator itrDayAgg = queryDayAgg.list().iterator();
			Timestamp maxDate = null,minDate = null;
			while (itrDayAgg.hasNext()) {
				maxDate = (Timestamp) itrDayAgg.next();
			}
			Query minDateQuery=null;Iterator minDayIter = null;
			if (maxDate == null) {
				iLogger.info("Max date from landmark day agg is null !!!");
				// OLAP max date is null, take min. date from log details
				// insert into OLAP for this date to today's date range
				minDateQuery = session.createQuery("SELECT MIN(transactionTimestamp) FROM LandmarkLogDetailsEntity");
				minDayIter= minDateQuery.list().iterator();
				
				while (minDayIter.hasNext()) {
					minDate = (Timestamp) minDayIter.next();
				}
				Calendar calendar = Calendar.getInstance();
				if(minDate!=null){
					calendar.setTime(minDate);
				}				
				Date newMinDate = calendar.getTime();
				String strMinDate = dateFormat.format(newMinDate);
				String strOLAPMaxLL = strMinDate + " 00:00:00";
				iLogger.info("Log details table min date : "+strOLAPMaxLL);
				String strTodayUL = todaysDate + " 23:59:59";
				iLogger.info("todays date : "+strTodayUL);
				
				String queryForLandmarkFact = "SELECT llde.serialNumber,llde.transactionTimestamp,llde.landmarkId,llde.machineLandmarkStatus,tde.tenacy_Dimension_Id,lde.landmarkDimensionId"
						+ " FROM LandmarkLogDetailsEntity llde,LandmarkDimensionEntity lde, LandmarkCategoryEntity lce, TenancyDimensionEntity tde"
						+ " WHERE llde.landmarkId = lde.landMarkId AND lde.landMarkCategoryId = lce.Landmark_Category_ID AND lce.Tenancy_ID = tde.tenancyId"
						+ " AND tde.last_updated = (SELECT MAX(tDim.last_updated) FROM TenancyDimensionEntity tDim WHERE tDim.tenancyId=lce.Tenancy_ID)"
						+ " AND llde.transactionTimestamp BETWEEN '"
						+ strOLAPMaxLL
						+ "' AND '"
						+ strTodayUL
						+ "' AND lce.ActiveStatus=1"
						+ " ORDER BY llde.serialNumber,llde.transactionTimestamp";

				message = populateLandmarkFactForDateRange(session,	queryForLandmarkFact,"save");
			} else {
				// OLAP contains some data
				String OlapLastDate = dateFormat1.format(maxDate);
				if (OlapLastDate.equalsIgnoreCase(today)) {
					
					iLogger.info("OLAP max date is today !!");
					
					// updating part for existing serial no.s
					String queryForLandmarkFactToUpdate = "SELECT llde.serialNumber,llde.transactionTimestamp,llde.landmarkId,llde.machineLandmarkStatus,tde.tenacy_Dimension_Id,lde.landmarkDimensionId"
							+ " FROM LandmarkLogDetailsEntity llde,LandmarkDimensionEntity lde, LandmarkCategoryEntity lce, TenancyDimensionEntity tde"
							+ " WHERE llde.landmarkId = lde.landMarkId AND lde.landMarkCategoryId = lce.Landmark_Category_ID AND lce.Tenancy_ID = tde.tenancyId AND lce.ActiveStatus=1"
							+ " AND tde.last_updated = (SELECT MAX(tDim.last_updated) FROM TenancyDimensionEntity tDim WHERE tDim.tenancyId=lce.Tenancy_ID)"
							+ " AND llde.serialNumber IN"
							+ " (SELECT lfe.serialNumber FROM LandmarkFactEntity_DayAgg lfe WHERE lfe.time_key like '"
							+ todaysDate
							+ "%')"
							+ " AND llde.transactionTimestamp like '"
							+ todaysDate
							+ "%'"
							+ " ORDER BY llde.serialNumber,llde.transactionTimestamp";

					message = populateLandmarkFact(session, queryForLandmarkFactToUpdate, time_key, "update");

					// todays date matches with max date from fact table
					// insert into fact table except for those serial no in
					// fact table but exists in asset event table

					String queryForLandmarkFactToInsert = "SELECT llde.serialNumber,llde.transactionTimestamp,llde.landmarkId,llde.machineLandmarkStatus,tde.tenacy_Dimension_Id,lde.landmarkDimensionId"
							+ " FROM LandmarkLogDetailsEntity llde,LandmarkDimensionEntity lde, LandmarkCategoryEntity lce, TenancyDimensionEntity tde"
							+ " WHERE llde.landmarkId = lde.landMarkId AND lde.landMarkCategoryId = lce.Landmark_Category_ID AND lce.Tenancy_ID = tde.tenancyId AND lce.ActiveStatus=1"
							+ " AND tde.last_updated = (SELECT MAX(tDim.last_updated) FROM TenancyDimensionEntity tDim WHERE tDim.tenancyId=lce.Tenancy_ID)"
							+ " AND llde.serialNumber NOT IN"
							+ " (SELECT lfe.serialNumber FROM LandmarkFactEntity_DayAgg lfe WHERE lfe.time_key like '"
							+ todaysDate
							+ "%')"
							+ " AND llde.transactionTimestamp like '"
							+ todaysDate
							+ "%'"
							+ " ORDER BY llde.serialNumber,llde.transactionTimestamp";

					message = populateLandmarkFact(session, queryForLandmarkFactToInsert, time_key, "save");

				} else if ((dateFormat.parse(OlapLastDate).compareTo(date)) < 0) {
					// OLAP has some max date less than current date, insert
					// into OLAP for a date range

					String strOLAPMaxLL = dateFormat.format(dateFormat.parse(maxDate.toString()))+" 00:00:00";
					iLogger.info("OLAP max date "+ strOLAPMaxLL +" is less than current date ");
					String strTodayUL = todaysDate + " 23:59:59";
					iLogger.info("today's date "+strTodayUL);
					
					//updating for existing serial no.s
					String queryForLandmarkFact = "SELECT llde.serialNumber,llde.transactionTimestamp,llde.landmarkId,llde.machineLandmarkStatus,tde.tenacy_Dimension_Id,lde.landmarkDimensionId"
							+ " FROM LandmarkLogDetailsEntity llde,LandmarkDimensionEntity lde, LandmarkCategoryEntity lce, TenancyDimensionEntity tde"
							+ " WHERE llde.landmarkId = lde.landMarkId AND lde.landMarkCategoryId = lce.Landmark_Category_ID AND lce.Tenancy_ID = tde.tenancyId AND lce.ActiveStatus=1"
							+ " AND tde.last_updated = (SELECT MAX(tDim.last_updated) FROM TenancyDimensionEntity tDim WHERE tDim.tenancyId=lce.Tenancy_ID)"
							+ " AND llde.serialNumber IN"
							+ " (SELECT lfe.serialNumber FROM LandmarkFactEntity_DayAgg lfe WHERE lfe.time_key BETWEEN '"
							+ strOLAPMaxLL
							+ "' AND '"
							+ strTodayUL
							+ "')"
							+ " AND llde.transactionTimestamp BETWEEN '"
							+ strOLAPMaxLL
							+ "' AND '"
							+ strTodayUL
							+ "'"
							+ " ORDER BY llde.serialNumber,llde.transactionTimestamp";

					message = populateLandmarkFactForDateRange(session,	queryForLandmarkFact,"update");
					
					//insertion here for other serial no.s
					queryForLandmarkFact = "SELECT llde.serialNumber,llde.transactionTimestamp,llde.landmarkId,llde.machineLandmarkStatus,tde.tenacy_Dimension_Id,lde.landmarkDimensionId"
						+ " FROM LandmarkLogDetailsEntity llde,LandmarkDimensionEntity lde, LandmarkCategoryEntity lce, TenancyDimensionEntity tde"
						+ " WHERE llde.landmarkId = lde.landMarkId AND lde.landMarkCategoryId = lce.Landmark_Category_ID AND lce.Tenancy_ID = tde.tenancyId"
						+ " AND tde.last_updated = (SELECT MAX(tDim.last_updated) FROM TenancyDimensionEntity tDim WHERE tDim.tenancyId=lce.Tenancy_ID)"
						+ " AND llde.serialNumber NOT IN"
						+ " (SELECT lfe.serialNumber FROM LandmarkFactEntity_DayAgg lfe WHERE lfe.time_key BETWEEN '"
						+ strOLAPMaxLL
						+ "' AND '"
						+ strTodayUL
						+ "')"
						+ " AND llde.transactionTimestamp BETWEEN '"
						+ strOLAPMaxLL
						+ "' AND '"
						+ strTodayUL
						+ "'"
						+ " ORDER BY llde.serialNumber,llde.transactionTimestamp";
					
					message = populateLandmarkFactForDateRange(session,	queryForLandmarkFact,"save");
					
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

		Object[] result = null;
		int tenancyDimensionId = 0, landmarkID = 0, landmarkDimensionID=0,prevLandmarkID = 0, departureCount = 0, arrivalCount = 0,aCount=0,dCount=0;
		long toatlDurationAtLandmark = 0, longestDurationAtLandmarkFromMap=0, longestDurationAtLandmark = 0, newDurationAtLandmark = 0, durationAtLandmark = 0, prevDurationAtLandmark = 0;
		long prevLongestDurationAtLandmark = 0;
		boolean resetCount = false,alreadyExistisInList=false;
		String serialNumber = null, message = null, prevSerialNo = null;
		ArrayList<Object> objectList = new ArrayList<Object>();
		Timestamp eventGeneratedTime = null;
		Timestamp arrivalTimestamp = null, departureTimestamp = null;
		HashMap<String, HashMap<Integer, ArrayList<Object>>> serialCountMap = new HashMap<String, HashMap<Integer, ArrayList<Object>>>();
		HashMap<Integer, ArrayList<Object>> landmarkMap = null;
		String status = null,prevStatus = null;;
		List<Integer> landmarkIdList = null;
		Query queryForLandmark = session.createQuery(query);
		Iterator iterLandmark = queryForLandmark.list().iterator();
		HashMap<Integer,Integer> checkDeparture = new HashMap<Integer, Integer>();
		while (iterLandmark.hasNext()) {
			resetCount = false;
			result = (Object[]) iterLandmark.next();
			if (result[0] != null) {
				serialNumber = ((AssetEntity) result[0]).getSerial_number().getSerialNumber();
			}
			if (result[1] != null) {
				eventGeneratedTime = (Timestamp) result[1];
			}
			if (result[2] != null) {
				landmarkID = ((LandmarkEntity) result[2]).getLandmark_id();
			}
			if (result[3] != null) {
				status = (String) result[3];
			}
			if (result[4] != null) {
				tenancyDimensionId = (Integer) result[4];
			}
			if(result[5]!=null){
				landmarkDimensionID = (Integer) result[5];
			}
			if (serialCountMap == null) {
				serialCountMap = new HashMap<String, HashMap<Integer, ArrayList<Object>>>();
				serialCountMap.put(serialNumber, null);
			}
			if (!serialNumber.equals(prevSerialNo)) {
				resetCount = true;
				landmarkIdList = new ArrayList<Integer>();	
				checkDeparture = new HashMap<Integer, Integer>();
				checkDeparture.put(landmarkID, 0);//no departure occured yet.
			} else {
				if (landmarkID != prevLandmarkID) {
					if(!landmarkIdList.contains(landmarkID)){
						landmarkIdList.add(landmarkID);
//						checkDeparture = new HashMap<Integer, Integer>();
						checkDeparture.put(landmarkID, 0);//no departure occured yet.
						alreadyExistisInList = false;
					}
					else{
						alreadyExistisInList = true;
					}
					resetCount = true;
				}
				else if(landmarkID == prevLandmarkID){
					if(!landmarkIdList.contains(landmarkID)){
						landmarkIdList.add(landmarkID);
						checkDeparture.put(landmarkID, 0);//no departure occured yet.
						alreadyExistisInList = false;
					}
					else{
						alreadyExistisInList = true;
					}
					
				}
			}
			if (resetCount) {
				departureCount = 0;
				arrivalCount = 0;
				toatlDurationAtLandmark = 0;
				longestDurationAtLandmark = 0;
				prevLongestDurationAtLandmark = 0;
				arrivalTimestamp = departureTimestamp = null;
			}

			if (status != null) {
				if (status.equalsIgnoreCase("Arrival")) {
					++arrivalCount;
					arrivalTimestamp = eventGeneratedTime;
				} else if (status.equalsIgnoreCase("Departure")) {
					++departureCount;
					departureTimestamp = eventGeneratedTime;
				}
			}
			durationAtLandmark = getTimeDiffInSeconds(arrivalTimestamp,
					departureTimestamp);
			newDurationAtLandmark = durationAtLandmark;
			toatlDurationAtLandmark = toatlDurationAtLandmark
					+ durationAtLandmark;

			if (durationAtLandmark > longestDurationAtLandmark) {
				prevLongestDurationAtLandmark = longestDurationAtLandmark;
				longestDurationAtLandmark = durationAtLandmark;
			}
			if (resetCount) {
				if (arrivalTimestamp == null) {
					if (departureTimestamp != null) {
						// first event for a serial no and landmark is departure
						departureTimestamp = null;
					}
				}

			} else if (arrivalTimestamp != null && departureTimestamp != null) {
				arrivalTimestamp = departureTimestamp = null;
				if (prevDurationAtLandmark != 0) {
					toatlDurationAtLandmark = toatlDurationAtLandmark
							- prevDurationAtLandmark;
				}
				if (durationAtLandmark > prevLongestDurationAtLandmark) {
					longestDurationAtLandmark = durationAtLandmark;
				}

			}

			if (serialNumber == prevSerialNo) {
//				if (landmarkID != prevLandmarkID) {
					
					// create a new landmark map and add it to existing date map
					if (!serialCountMap.containsKey(serialNumber)) {
						serialCountMap.put(serialNumber, null);
					}
					landmarkMap = serialCountMap.get(serialNumber);
					if (landmarkMap == null) {
						landmarkMap = new HashMap<Integer, ArrayList<Object>>();
					}
					else if(landmarkMap.containsKey(landmarkID)){
						
						//for concentric landmarks
						objectList =landmarkMap.get(landmarkID);
						
						//set count here
						if (status.equalsIgnoreCase("Arrival")) {
							aCount = (Integer)objectList.get(0);
							objectList.set(0, aCount+1);
							//set duration at landmark
							toatlDurationAtLandmark=(Long)objectList.get(2);
							longestDurationAtLandmark = (Long)objectList.get(3);
							longestDurationAtLandmarkFromMap = (Long)objectList.get(3);
							durationAtLandmark = getTimeDiffInSeconds(eventGeneratedTime,null);
//							durationAtLandmark = (Long)objectList.get(2)-durationAtLandmark;
							toatlDurationAtLandmark = toatlDurationAtLandmark + durationAtLandmark;
							if (durationAtLandmark > longestDurationAtLandmark) {
								prevLongestDurationAtLandmark = longestDurationAtLandmark;
								longestDurationAtLandmark = durationAtLandmark;
							}
							objectList.set(3, longestDurationAtLandmark);
							objectList.set(2, toatlDurationAtLandmark);
						} else if (status.equalsIgnoreCase("Departure")) {
							if(checkDeparture.get(landmarkID) == 0){
									//first departure for concentric landmarks happened
								toatlDurationAtLandmark=(Long)objectList.get(2);
								longestDurationAtLandmark = (Long)objectList.get(3);
							}
							
							else if(checkDeparture.get(landmarkID) !=0){
								if(!alreadyExistisInList){
									//first departure for concentric landmarks happened
									toatlDurationAtLandmark=0;
									longestDurationAtLandmark = 0;
								}
								else{
									toatlDurationAtLandmark=(Long)objectList.get(2);
									longestDurationAtLandmark = (Long)objectList.get(3);
								}
								
							}
//							else{
//								//take from list
//								toatlDurationAtLandmark=(Long)objectList.get(2);
//								longestDurationAtLandmark = (Long)objectList.get(3);
//							}
							dCount = (Integer)objectList.get(1);
							objectList.set(1, dCount+1);
							//set duration at landmark
							durationAtLandmark = getTimeDiffInSeconds(null,eventGeneratedTime);
							if(toatlDurationAtLandmark!=0){
								durationAtLandmark = Math.abs(86400-toatlDurationAtLandmark-durationAtLandmark);
								toatlDurationAtLandmark = toatlDurationAtLandmark - prevDurationAtLandmark;
							}	
							if(checkDeparture.get(landmarkID) == 0){
								toatlDurationAtLandmark = 0;
								longestDurationAtLandmark = 0;
							}
							toatlDurationAtLandmark = toatlDurationAtLandmark + durationAtLandmark;
							
//							concentric A & D happened,now its normal A & D
							if(prevStatus.equals("Arrival") && landmarkID==prevLandmarkID){
								if(newDurationAtLandmark>longestDurationAtLandmarkFromMap){
									longestDurationAtLandmark = newDurationAtLandmark;
								}
								else{
									longestDurationAtLandmark = longestDurationAtLandmarkFromMap;
								}
							}
							else{
								if (durationAtLandmark > longestDurationAtLandmark) {
									prevLongestDurationAtLandmark = longestDurationAtLandmark;
									longestDurationAtLandmark = durationAtLandmark;
								}
							}
							objectList.set(3, longestDurationAtLandmark);
							objectList.set(2, toatlDurationAtLandmark);
							checkDeparture.put(landmarkID,checkDeparture.get(landmarkID)+1);//first departure happened
						}
						landmarkMap.put(landmarkID, objectList);
						serialCountMap.remove(serialNumber);
						serialCountMap.put(serialNumber, landmarkMap);
					}
					else{//does not contain , add new one
						objectList = new ArrayList<Object>();
						objectList.add(0, arrivalCount);
						objectList.add(1, departureCount);
						objectList.add(2, toatlDurationAtLandmark);
						objectList.add(3, longestDurationAtLandmark);
						objectList.add(4, tenancyDimensionId);
						objectList.add(5, landmarkDimensionID);
						landmarkMap.put(landmarkID, objectList);
						serialCountMap.remove(serialNumber);
						serialCountMap.put(serialNumber, landmarkMap);
					}				
//				} else {
//					landmarkMap = serialCountMap.get(serialNumber);
//					objectList = new ArrayList<Object>();
//					objectList.add(0, arrivalCount);
//					objectList.add(1, departureCount);
//					objectList.add(2, toatlDurationAtLandmark);
//					objectList.add(3, longestDurationAtLandmark);
//					objectList.add(4, tenancyDimensionId);
//					objectList.add(5, landmarkDimensionID);
//					landmarkMap.put(landmarkID, objectList);
//					serialCountMap.remove(landmarkID);
//					serialCountMap.put(serialNumber, landmarkMap);
//
//				}
			}

			else if (serialNumber != prevSerialNo) {
				landmarkMap = new HashMap<Integer, ArrayList<Object>>();
				objectList = new ArrayList<Object>();
				objectList.add(0, arrivalCount);
				objectList.add(1, departureCount);
				objectList.add(2, toatlDurationAtLandmark);
				objectList.add(3, longestDurationAtLandmark);
				objectList.add(4, tenancyDimensionId);
				objectList.add(5, landmarkDimensionID);
				landmarkMap.put(landmarkID, objectList);
				serialCountMap.put(serialNumber, landmarkMap);
			}

			prevSerialNo = serialNumber;
			prevLandmarkID = landmarkID;
			prevDurationAtLandmark = durationAtLandmark;
			prevStatus = status;
		}

		if (action.equalsIgnoreCase("save")) {
			message = saveToFactTable(session, serialCountMap, time_key);
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
	public String updateFactTable(Session session,HashMap<String, HashMap<Integer, ArrayList<Object>>> serialCountMap,
			Timestamp time_key) {

		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;

		String message = null, today = null;
		int aCount = 0, dCount = 0, rows = 0,landmarkDimensionId=0;
		long longestDurationAtLandmark = 0, totalDurationAtLandmark = 0;
		Query updateQuery = null;
		HashMap<Integer, ArrayList<Object>> landmarkMap = null;
		ArrayList<Object> objectList = null;
		try {
			HashMap<String, HashMap<Integer, ArrayList<Object>>> tempSerialCountMap = new HashMap<String, HashMap<Integer, ArrayList<Object>>>();
			HashMap<Integer, ArrayList<Object>> tempLandmarkMap = new HashMap<Integer, ArrayList<Object>>();
			// tempSerialCountMap = serialCountMap;
			// update here
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			today = dateFormat.format(time_key);
			for (String key : serialCountMap.keySet()) {
				landmarkMap = serialCountMap.get(key);
				tempLandmarkMap = landmarkMap;
				if (landmarkMap != null) {
					for (Integer landmarkId : landmarkMap.keySet()) {
						objectList = landmarkMap.get(landmarkId);
						aCount = (Integer) objectList.get(0);
						dCount = (Integer) objectList.get(1);
						totalDurationAtLandmark = (Long) objectList.get(2);
						longestDurationAtLandmark = (Long) objectList.get(3);
						landmarkDimensionId = (Integer)objectList.get(5);
						updateQuery = session
								.createQuery("UPDATE LandmarkFactEntity_DayAgg lfeda "
										+ " SET lfeda.noOfArrivals="
										+ aCount
										+ ",lfeda.noOfDepartures = "
										+ dCount
										+ " ,totalDurationAtLandmark = "
										+ totalDurationAtLandmark
										+ ",longestDurationAtLandmark ="
										+ longestDurationAtLandmark
										+ " WHERE lfeda.serialNumber = '"
										+ key
										+ "' AND "
										+ " lfeda.time_key like '"
										+ today
										+ "%'"
										+ " AND lfeda.landmarkDimensionId = "
										+ landmarkDimensionId);
						iLogger.info("query for updation :" + updateQuery);
						rows = updateQuery.executeUpdate();
						if (rows == 0) {
							// row for this landkark id does not exist. then add
							// it to temp map and insert into fact table
							if (tempSerialCountMap.containsKey(key)) {
								tempLandmarkMap = tempSerialCountMap.get(key);
								tempSerialCountMap.remove(key);

							} else {
								tempLandmarkMap = new HashMap<Integer, ArrayList<Object>>();
							}
							tempLandmarkMap.put(landmarkId, objectList);
							tempSerialCountMap.put(key, tempLandmarkMap);
						}
					}
				}
			}
			if (tempSerialCountMap != null) {
				// save those landmark ids which does not exist in fact table ,
				// but newly added in log table with same serial no.
				message = saveToFactTable(session, tempSerialCountMap, time_key);
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
	public String populateLandmarkFactForDateRange(Session session, String query, String action) {
		
		
		String prevEventGeneratedTime = null, eventGeneratedString = null;
		String serialNumber = null, message = null, prevSerialNo = null;
		Object[] result = null;
		int tenancyDimensionId = 0, landmarkID = 0, landmarkDimensionID = 0, prevLandmarkID = 0, departureCount = 0, arrivalCount = 0,aCount=0,dCount=0;
		long toatlDurationAtLandmark = 0, longestDurationAtLandmarkFromMap=0,longestDurationAtLandmark = 0,newDurationAtLandmark = 0, durationAtLandmark = 0, prevDurationAtLandmark = 0;
		long prevLongestDurationAtLandmark = 0;
		boolean resetCount = false,alreadyExistisInList=false;
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		ArrayList<Object> objectList = new ArrayList<Object>();
		Timestamp eventGeneratedTime = null;
		Timestamp arrivalTimestamp = null, departureTimestamp = null;
		HashMap<String, HashMap<String, HashMap<Integer, ArrayList<Object>>>> serialCountMap = new HashMap<String, HashMap<String, HashMap<Integer, ArrayList<Object>>>>();
		HashMap<Integer, ArrayList<Object>> landmarkMap = null;
		String status = null,prevStatus = null;
		HashMap<String, HashMap<Integer, ArrayList<Object>>> dateMap = new HashMap<String, HashMap<Integer, ArrayList<Object>>>();
		List<Integer> landmarkIdList = null;
		Query queryForLandmark = session.createQuery(query);
		Iterator iterLandmark = queryForLandmark.list().iterator();
		HashMap<Integer,Integer> checkDeparture = new HashMap<Integer, Integer>();
		int flag = 0;
		while (iterLandmark.hasNext()) {
			resetCount = false;
			result = (Object[]) iterLandmark.next();
			if (result[0] != null) {
				serialNumber = ((AssetEntity) result[0]).getSerial_number()
						.getSerialNumber();
			}
			if (result[1] != null) {
				eventGeneratedTime = (Timestamp) result[1];
				eventGeneratedString = dateFormat1.format(eventGeneratedTime);
			}
			if (result[2] != null) {
				landmarkID = ((LandmarkEntity) result[2]).getLandmark_id();
			}
			if (result[3] != null) {
				status = (String) result[3];
			}
			if (result[4] != null) {
				tenancyDimensionId = (Integer) result[4];
			}
			if(result[5]!=null){
				landmarkDimensionID = (Integer) result[5];
			}
			if (serialCountMap == null) {
				serialCountMap = new HashMap<String, HashMap<String, HashMap<Integer, ArrayList<Object>>>>();
				serialCountMap.put(serialNumber, null);
			}
			if (!serialNumber.equals(prevSerialNo)) {
				resetCount = true;
				landmarkIdList = new ArrayList<Integer>();	
				checkDeparture = new HashMap<Integer, Integer>();
				checkDeparture.put(landmarkID, 0);//no departure occured yet.
				
			} else {
				if (landmarkID != prevLandmarkID) {
					if(!landmarkIdList.contains(landmarkID)){
						landmarkIdList.add(landmarkID);
//						checkDeparture = new HashMap<Integer, Integer>();
						checkDeparture.put(landmarkID, 0);//no departure occured yet.
						alreadyExistisInList = true;
					}
					resetCount = true;
				} 
				else if(landmarkID == prevLandmarkID){
					if(!landmarkIdList.contains(landmarkID)){
						landmarkIdList.add(landmarkID);
						checkDeparture.put(landmarkID, 0);//no departure occured yet.
						alreadyExistisInList = true;
					}
					
				}
				if (!eventGeneratedString.equals(prevEventGeneratedTime)) {
					resetCount = true;
				}
				if (eventGeneratedString.equals(prevEventGeneratedTime)) {
//					checkDeparture = new HashMap<Integer, Integer>();
					checkDeparture.put(landmarkID, 0);//no departure occured yet.
				}
			}
			if (resetCount) {
				flag = 0;
				departureCount = 0;
				arrivalCount = 0;
				toatlDurationAtLandmark = 0;
				longestDurationAtLandmark = 0;
				prevLongestDurationAtLandmark = 0;
				arrivalTimestamp = departureTimestamp = null;
			}
			
			if (status != null) {
				if (status.equalsIgnoreCase("Arrival")) {
					++arrivalCount;
					arrivalTimestamp = eventGeneratedTime;
				} else if (status.equalsIgnoreCase("Departure")) {
					++departureCount;
					departureTimestamp = eventGeneratedTime;
				}
			}
			
			durationAtLandmark = getTimeDiffInSeconds(arrivalTimestamp,
					departureTimestamp);
			newDurationAtLandmark = durationAtLandmark;
			toatlDurationAtLandmark = toatlDurationAtLandmark + durationAtLandmark;

			if (durationAtLandmark > longestDurationAtLandmark) {
				prevLongestDurationAtLandmark = longestDurationAtLandmark;
				longestDurationAtLandmark = durationAtLandmark;
			}
			if (resetCount) {
				if (arrivalTimestamp == null) {
					if (departureTimestamp != null) {
						// first event for a serial no and landmark is departure
						departureTimestamp = null;
					}
				}
			} else if (arrivalTimestamp != null && departureTimestamp != null) {
				arrivalTimestamp = departureTimestamp = null;
				if (prevDurationAtLandmark != 0) {
					toatlDurationAtLandmark = toatlDurationAtLandmark - prevDurationAtLandmark;
				}
				if (durationAtLandmark > prevLongestDurationAtLandmark) {
					longestDurationAtLandmark = durationAtLandmark;
				}
			}

			if (serialNumber == prevSerialNo) {
				if (!eventGeneratedString.equals(prevEventGeneratedTime)) {
					// create a new datemap and add it to existing serial no.
					// map
					if (!serialCountMap.containsKey(serialNumber)) {
						serialCountMap.put(serialNumber, null);
					}
					dateMap = serialCountMap.get(serialNumber);
					if (dateMap == null) {
						dateMap = new HashMap<String, HashMap<Integer, ArrayList<Object>>>();
					}
					landmarkMap = new HashMap<Integer, ArrayList<Object>>();
					objectList = new ArrayList<Object>();
					objectList.add(0, arrivalCount);
					objectList.add(1, departureCount);
					objectList.add(2, toatlDurationAtLandmark);
					objectList.add(3, longestDurationAtLandmark);
					objectList.add(4, tenancyDimensionId);
					objectList.add(5, landmarkDimensionID);
					landmarkMap.put(landmarkID, objectList);
					dateMap.put(eventGeneratedString, landmarkMap);
					serialCountMap.remove(serialNumber);
					serialCountMap.put(serialNumber, dateMap);
				} else {
					// eventGeneratedString and prevEventGeneratedTime are equal
//					if (landmarkID != prevLandmarkID) {						
						// create a new landmark map and add it to existing date map
						if (!serialCountMap.containsKey(serialNumber)) {
							serialCountMap.put(serialNumber, null);
						}
						dateMap = serialCountMap.get(serialNumber);
						if (dateMap == null) {
							dateMap = new HashMap<String, HashMap<Integer, ArrayList<Object>>>();
						}
						landmarkMap = dateMap.get(eventGeneratedString);
						if (landmarkMap == null) {
							landmarkMap = new HashMap<Integer, ArrayList<Object>>();
						}
						else if(landmarkMap.containsKey(landmarkID)){
							
							//for concentric landmarks
							objectList =landmarkMap.get(landmarkID);
							
							//set count here
							if (status.equalsIgnoreCase("Arrival")) {
								aCount = (Integer)objectList.get(0);
								objectList.set(0, aCount+1);
								//set duration at landmark
								toatlDurationAtLandmark=(Long)objectList.get(2);
								longestDurationAtLandmark = (Long)objectList.get(3);
								longestDurationAtLandmarkFromMap = (Long)objectList.get(3);
								durationAtLandmark = getTimeDiffInSeconds(eventGeneratedTime,null);
//								durationAtLandmark = (Long)objectList.get(2)-durationAtLandmark;
								toatlDurationAtLandmark = toatlDurationAtLandmark + durationAtLandmark;
								if (durationAtLandmark > longestDurationAtLandmark) {
									prevLongestDurationAtLandmark = longestDurationAtLandmark;
									longestDurationAtLandmark = durationAtLandmark;
								}
								objectList.set(3, longestDurationAtLandmark);
								objectList.set(2, toatlDurationAtLandmark);
							} else if (status.equalsIgnoreCase("Departure")) {
								if(checkDeparture.get(landmarkID) == 0){
										//first departure for concentric landmarks happened
									toatlDurationAtLandmark=(Long)objectList.get(2);
									longestDurationAtLandmark = (Long)objectList.get(3);
								}
								
								else if(checkDeparture.get(landmarkID) !=0){
									if(!alreadyExistisInList){
										//first departure for concentric landmarks happened
										toatlDurationAtLandmark=0;
										longestDurationAtLandmark = 0;
									}
									else{
										toatlDurationAtLandmark=(Long)objectList.get(2);
										longestDurationAtLandmark = (Long)objectList.get(3);
									}
									
								}
//								else{
//									//take from list
//									toatlDurationAtLandmark=(Long)objectList.get(2);
//									longestDurationAtLandmark = (Long)objectList.get(3);
//								}
								dCount = (Integer)objectList.get(1);
								objectList.set(1, dCount+1);
								//set duration at landmark
								durationAtLandmark = getTimeDiffInSeconds(null,eventGeneratedTime);
								if(toatlDurationAtLandmark!=0){
									durationAtLandmark = Math.abs(86400-toatlDurationAtLandmark-durationAtLandmark);
								}	
								if(checkDeparture.get(landmarkID) == 0){
									toatlDurationAtLandmark = 0;
									longestDurationAtLandmark = 0;
								}
								toatlDurationAtLandmark = toatlDurationAtLandmark + durationAtLandmark;
								
								
//								concentric A & D happened,now its normal A & D
								if(prevStatus.equals("Arrival") && landmarkID==prevLandmarkID){
									if(newDurationAtLandmark>longestDurationAtLandmarkFromMap){
										longestDurationAtLandmark = newDurationAtLandmark;
									}
									else{
										longestDurationAtLandmark = longestDurationAtLandmarkFromMap;
									}
								}
								else{
									if (durationAtLandmark > longestDurationAtLandmark) {
										prevLongestDurationAtLandmark = longestDurationAtLandmark;
										longestDurationAtLandmark = durationAtLandmark;
									}
								}
								objectList.set(3, longestDurationAtLandmark);
								objectList.set(2, toatlDurationAtLandmark);
								checkDeparture.put(landmarkID,checkDeparture.get(landmarkID)+1);//first departure happened
							}
							landmarkMap.put(landmarkID, objectList);
							dateMap.put(eventGeneratedString, landmarkMap);
							serialCountMap.remove(serialNumber);
							serialCountMap.put(serialNumber, dateMap);
						}
						else{//does not contain , add new one
							objectList = new ArrayList<Object>();
							objectList.add(0, arrivalCount);
							objectList.add(1, departureCount);
							objectList.add(2, toatlDurationAtLandmark);
							objectList.add(3, longestDurationAtLandmark);
							objectList.add(4, tenancyDimensionId);
							objectList.add(5, landmarkDimensionID);
							landmarkMap.put(landmarkID, objectList);
							dateMap.put(eventGeneratedString, landmarkMap);
							serialCountMap.remove(serialNumber);
							serialCountMap.put(serialNumber, dateMap);
						}
						
//					} else {
//						dateMap = serialCountMap.get(serialNumber);
//						landmarkMap = dateMap.get(eventGeneratedString);
//						objectList = new ArrayList<Object>();
//						objectList.add(0, arrivalCount);
//						objectList.add(1, departureCount);
//						objectList.add(2, toatlDurationAtLandmark);
//						objectList.add(3, longestDurationAtLandmark);
//						objectList.add(4, tenancyDimensionId);
//						objectList.add(5, landmarkDimensionID);
//						landmarkMap.put(landmarkID, objectList);
//						dateMap.remove(eventGeneratedString);
//						dateMap.put(eventGeneratedString, landmarkMap);
//						serialCountMap.put(serialNumber, dateMap);
//
//					}
				}
			}

			else if (serialNumber != prevSerialNo) {
				dateMap = new HashMap<String, HashMap<Integer, ArrayList<Object>>>();
				landmarkMap = new HashMap<Integer, ArrayList<Object>>();
				objectList = new ArrayList<Object>();
				objectList.add(0, arrivalCount);
				objectList.add(1, departureCount);
				objectList.add(2, toatlDurationAtLandmark);
				objectList.add(3, longestDurationAtLandmark);
				objectList.add(4, tenancyDimensionId);
				objectList.add(5, landmarkDimensionID);
				landmarkMap.put(landmarkID, objectList);
				dateMap.put(eventGeneratedString, landmarkMap);
				serialCountMap.put(serialNumber, dateMap);
			}

			prevSerialNo = serialNumber;
			prevLandmarkID = landmarkID;
			prevDurationAtLandmark = durationAtLandmark;
			prevEventGeneratedTime = eventGeneratedString;
			prevStatus = status;
		}
		if(action.equalsIgnoreCase("save")){
		message = saveToFactTableForDateRange(session, serialCountMap);
		}
		else if(action.equalsIgnoreCase("update")){
			message = updateFactTableForDateRange(session, serialCountMap);
		}
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
	public String saveToFactTable(Session session,HashMap<String, HashMap<Integer, ArrayList<Object>>> serialCountMap,
			Timestamp time_key) {

		String message = "SUCCESS";
		ArrayList<Object> objectList = null;
		LandmarkFactEntity landmarkFactEntity = null;
		HashMap<Integer, ArrayList<Object>> landmarkMap = null;

		TenancyDimensionEntity tdEntity = null;
		LandmarkDimensionEntity ldEntity = null;
		if (serialCountMap != null) {
			for (String key : serialCountMap.keySet()) {
				landmarkMap = serialCountMap.get(key);
				if (landmarkMap != null) {
					for (Integer landmarkId : landmarkMap.keySet()) {
						objectList = landmarkMap.get(landmarkId);
						if (objectList != null) {
							landmarkFactEntity = new LandmarkFactEntity();
							landmarkFactEntity.setSerialNumber(key);
							
							landmarkFactEntity.setTime_key(time_key);
							landmarkFactEntity.setNoOfArrivals((Integer) objectList.get(0));
							landmarkFactEntity.setNoOfDepartures((Integer) objectList.get(1));
							landmarkFactEntity.setTotalDurationAtLandmark((Long) objectList.get(2));
							landmarkFactEntity.setLongestDurationAtLandmark((Long) objectList.get(3));
							tdEntity = new TenancyDimensionEntity();
							tdEntity.setTenacy_Dimension_Id((Integer) objectList.get(4));
							landmarkFactEntity.setTenacyDimensionId(tdEntity);
							ldEntity = new LandmarkDimensionEntity();
							ldEntity.setLandmarkDimensionId((Integer)objectList.get(5));
							landmarkFactEntity.setLandmarkDimensionId(ldEntity);
							
							session.save("LandmarkFactEntity_DayAgg",landmarkFactEntity);
						}

					}
				}
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
	public String saveToFactTableForDateRange(Session session,HashMap<String, HashMap<String, HashMap<Integer, ArrayList<Object>>>> serialCountMap) {
		
		String message = "SUCCESS";
		HashMap<String, HashMap<Integer, ArrayList<Object>>> dateMap;
		HashMap<Integer, ArrayList<Object>> landmarkMap;
		LandmarkFactEntity landmarkFactEntity = null;
		ArrayList<Object> objectList = null;
		TenancyDimensionEntity tdEntity = null;
		LandmarkDimensionEntity ldEntity = null;
		
				if(serialCountMap!=null && !serialCountMap.isEmpty()){
					for (String key : serialCountMap.keySet()) {
						dateMap = serialCountMap.get(key);
						if (dateMap != null) {
							for (String time_key : dateMap.keySet()) {
								landmarkMap = dateMap.get(time_key);
								if(landmarkMap !=null){
									for (Integer landmarkId : landmarkMap.keySet()) {
										objectList = landmarkMap.get(landmarkId);
										landmarkFactEntity = new LandmarkFactEntity();
										landmarkFactEntity.setSerialNumber(key);
										landmarkFactEntity.setTime_key(Timestamp.valueOf(time_key+ " 00:00:00"));						
										landmarkFactEntity.setNoOfArrivals((Integer)objectList.get(0));
										landmarkFactEntity.setNoOfDepartures((Integer)objectList.get(1));
										landmarkFactEntity.setTotalDurationAtLandmark((Long)objectList.get(2));
										landmarkFactEntity.setLongestDurationAtLandmark((Long)objectList.get(3));
										tdEntity = new TenancyDimensionEntity();
										tdEntity.setTenacy_Dimension_Id((Integer)objectList.get(4));
										landmarkFactEntity.setTenacyDimensionId(tdEntity);	
										ldEntity = new LandmarkDimensionEntity();
										ldEntity.setLandmarkDimensionId((Integer)objectList.get(5));
										landmarkFactEntity.setLandmarkDimensionId(ldEntity);
										
										session.save("LandmarkFactEntity_DayAgg", landmarkFactEntity);
									}									
								}								
							}
						}
					}
					
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
	public String updateFactTableForDateRange(Session session,HashMap<String, HashMap<String, HashMap<Integer, ArrayList<Object>>>> serialCountMap) {
		Logger iLogger = InfoLoggerClass.logger;
		String message = null, today = null;
		int aCount = 0, dCount = 0, rows = 0,landmarkDimensionId=0;
		long longestDurationAtLandmark = 0, totalDurationAtLandmark = 0;
		Query updateQuery = null;
		HashMap<String, HashMap<Integer, ArrayList<Object>>> dateMap;
		HashMap<Integer, ArrayList<Object>> landmarkMap;
		ArrayList<Object> objectList = null;
		try {
			HashMap<String, HashMap<String, HashMap<Integer, ArrayList<Object>>>> tempSerialCountMap = new HashMap<String, HashMap<String, HashMap<Integer, ArrayList<Object>>>>();
			HashMap<String, HashMap<Integer, ArrayList<Object>>> tempDateMap = new HashMap<String, HashMap<Integer,ArrayList<Object>>>();
			HashMap<Integer, ArrayList<Object>> tempLandmarkMap = new HashMap<Integer, ArrayList<Object>>();
			// tempSerialCountMap = serialCountMap;
			// update here
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//			today = dateFormat.format(time_key);
			if(serialCountMap!=null && !serialCountMap.isEmpty()){
				for (String key : serialCountMap.keySet()) {
					dateMap = serialCountMap.get(key);
					if (dateMap != null) {
						for (String time_key : dateMap.keySet()) {
							landmarkMap = dateMap.get(time_key);
							if(landmarkMap !=null){
								for (Integer landmarkId : landmarkMap.keySet()) {
									objectList = landmarkMap.get(landmarkId);
									aCount = (Integer) objectList.get(0);
									dCount = (Integer) objectList.get(1);
									totalDurationAtLandmark = (Long) objectList.get(2);
									longestDurationAtLandmark = (Long) objectList.get(3);
									landmarkDimensionId = (Integer)objectList.get(5);
									updateQuery = session
											.createQuery("UPDATE LandmarkFactEntity_DayAgg lfeda "
													+ " SET lfeda.noOfArrivals="
													+ aCount
													+ ",lfeda.noOfDepartures = "
													+ dCount
													+ " ,totalDurationAtLandmark = "
													+ totalDurationAtLandmark
													+ ",longestDurationAtLandmark ="
													+ longestDurationAtLandmark
													+ " WHERE lfeda.serialNumber = '"
													+ key
													+ "' AND "
													+ " lfeda.time_key like '"
													+ time_key
													+ "%'"
													+ " AND lfeda.landmarkDimensionId = "
													+ landmarkDimensionId);
									iLogger.info("query for updation :" + updateQuery);
									rows = updateQuery.executeUpdate();
									if (rows == 0) {
										// row for this landkark id does not exist. then add
										// it to temp map and insert into fact table
										if (tempSerialCountMap.containsKey(key)) {
											tempDateMap = tempSerialCountMap.get(key);
											if(tempDateMap.containsKey(time_key)){
												tempLandmarkMap = tempDateMap.get(time_key);
												tempLandmarkMap.put(landmarkId, objectList);
												tempDateMap.remove(time_key);
												tempDateMap.put(time_key, tempLandmarkMap);
												 tempSerialCountMap.remove(key);
												 tempSerialCountMap.put(key, tempDateMap);
											}
											else{
												tempLandmarkMap = new HashMap<Integer, ArrayList<Object>>();
												 tempLandmarkMap.put(landmarkId, objectList);
												tempDateMap.put(time_key, tempLandmarkMap);
												tempSerialCountMap.remove(key);
												 tempSerialCountMap.put(key, tempDateMap);
											}									
										} else {
											tempDateMap = new HashMap<String, HashMap<Integer,ArrayList<Object>>>();
											tempLandmarkMap = new HashMap<Integer, ArrayList<Object>>();
											tempLandmarkMap.put(landmarkId, objectList);
											tempDateMap.put(time_key, tempLandmarkMap);
											tempSerialCountMap.put(key, tempDateMap);
										}
										
									}
								}
							}
					}
				}
				if (tempSerialCountMap != null && tempSerialCountMap.size()>0) {
					// save those landmark ids which does not exist in fact table ,
					// but newly added in log table with same serial no.
					message = saveToFactTableForDateRange(session, tempSerialCountMap);
				}
				
			}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			message = "ERROR";
				}			
		return message;
	}
	
	public long getTimeDiffInSeconds(Timestamp arrivalTimestamp,Timestamp departureTimestamp) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
		String arrivalTimeString = null;
		String departureTimeString = null;
		long diffInMilliseconds = 0,diffInSeconds=0;
		Date arrivalDate = null;
		Date departureDate = null;
		try {
			if (arrivalTimestamp != null) {
				if (departureTimestamp == null) {
					arrivalTimeString = dateFormat.format(arrivalTimestamp);
					departureTimeString = dateFormat2.format(arrivalTimestamp);
					departureTimeString = departureTimeString + " 23:59:59";
					arrivalDate = dateFormat.parse(arrivalTimeString);
					departureDate = dateFormat.parse(departureTimeString);
					diffInMilliseconds = Math.abs(departureDate.getTime()
							- arrivalDate.getTime());

				} else {
					// both timestamp are available
					arrivalTimeString = dateFormat.format(arrivalTimestamp);
					departureTimeString = dateFormat.format(departureTimestamp);
					arrivalDate = dateFormat.parse(arrivalTimeString);
					departureDate = dateFormat.parse(departureTimeString);
					diffInMilliseconds = Math.abs(departureDate.getTime()- arrivalDate.getTime());
				}
			} else {
				if (departureTimestamp != null) {
					departureTimeString = dateFormat.format(departureTimestamp);
					arrivalTimeString = dateFormat2.format(departureTimestamp);
					arrivalTimeString = arrivalTimeString + " 00:00:00";
					arrivalDate = dateFormat.parse(arrivalTimeString);
					departureDate = dateFormat.parse(departureTimeString);
					diffInMilliseconds = Math.abs(departureDate.getTime()
							- arrivalDate.getTime());
				}
			}
			if(diffInMilliseconds!=0){
				diffInSeconds = diffInMilliseconds/1000;
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		
		return diffInSeconds;
	}
}
