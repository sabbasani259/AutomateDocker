/**
 * CR300: 20220720 : Dhiraj K : SOS closure and Report Changes
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetEventEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.businessentity.SOSAlertsHistoryEntity;
import remote.wise.businessentity.SOSCategoryMasterEntity;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.SOSAlertResponseContract;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;
import remote.wise.util.IstGmtTimeConversion;
import remote.wise.util.MOOLDAKafkaPublisher;

/**
 * @author KO369761
 *
 */
public class SOSAlertsImpl {
	
	public HashMap<String, Object> viewSOSAlert(int assetEventId){

		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();
		HashMap<String, Object> response = new HashMap<String, Object>();
		List<HashMap<String, String>> commentsList = new LinkedList<HashMap<String,String>>();
		HashMap<String, String> comment = null;
		String comments = null;
		AssetEventEntity assetEvent = null;
		int alertCategory = 0;
		SOSCategoryMasterEntity sosCategory = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		try{
			session.beginTransaction();
			Query aeQuery = session.createQuery(" from AssetEventEntity ae  where assetEventId ="+assetEventId);
			Iterator aeItr = aeQuery.list().iterator();
			if(aeItr.hasNext()){
				assetEvent = (AssetEventEntity) aeItr.next();
				comments = assetEvent.getComments();
			}

			if(comments != null){
				if(comments.split("\\|").length>1){
					alertCategory = Integer.parseInt(comments.split("\\|")[0]);
					comments = comments.split("\\|")[1];
				}
			}

			Query categoryQuery = session.createQuery(" from SOSCategoryMasterEntity where categoryId = "+alertCategory);
			Iterator queryItr = categoryQuery.list().iterator();
			if(queryItr.hasNext()){
				sosCategory = (SOSCategoryMasterEntity)queryItr.next();
			}

			SOSAlertsHistoryEntity alertHistoryObj = null;
			Query alertHistoryQ = session.createQuery(" from SOSAlertsHistoryEntity where assetEventId = "+assetEventId);
			Iterator alertHistoryQItr = alertHistoryQ.list().iterator();
			
			while(alertHistoryQItr.hasNext()){
				comment = new HashMap<String, String>();
				alertHistoryObj = (SOSAlertsHistoryEntity) alertHistoryQItr.next();
				if(alertHistoryObj.getUpdatedBy() != null)
					comment.put("user_name", alertHistoryObj.getUpdatedBy().getFirst_name()+" "+alertHistoryObj.getUpdatedBy().getLast_name());
				else
					comment.put("user_name", null);

				comment.put("updated_time", sdf.format(alertHistoryObj.getUpdatedTime()));
				comment.put("comment", alertHistoryObj.getComments());
				commentsList.add(comment);
				
			}

			response.put("serial_number", assetEvent.getSerialNumber().getSerial_number().getSerialNumber());
			response.put("asset_event_id", String.valueOf(assetEventId));
			response.put("comments_list", commentsList);

			if(sosCategory != null){
				response.put("category_name", sosCategory.getCategoryName());
				response.put("category_id", String.valueOf(sosCategory.getCategoryId()));
			}
			else{
				response.put("category_name", null);
				response.put("category_id", null);
			}

		}catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception Caught while commiting: "+e.getMessage());
		}
		return response;		
	}

	public String editSOSAlerts(LinkedHashMap<String, Object> reqObj){

		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();
		String response = "SUCCESS";

		try{
			String loginId = (String) reqObj.get("login_id");
			int assetEventId = Integer.parseInt((String) reqObj.get("asset_event_id"));
			String comments = (String) reqObj.get("comments");
			int categoryId = Integer.parseInt((String) reqObj.get("category_id"));
			Date currTime = new Date();
			Timestamp currTS = new Timestamp(currTime.getTime());
			
			CommonUtil util = new CommonUtil();
			String isUserValid = util.inputFieldValidation(comments);
			if(!isUserValid.equals("SUCCESS")){
				throw new CustomFault(isUserValid);
			}

			session.beginTransaction();
			Query query = session.createQuery(" from SOSCategoryMasterEntity where categoryId = "+categoryId);
			Iterator queryItr = query.list().iterator();
			SOSCategoryMasterEntity SOSCategory = null;
			if(queryItr.hasNext()){
				SOSCategory = (SOSCategoryMasterEntity) queryItr.next();
			}

			String userId=new CommonUtil().getUserId(loginId);
			ContactEntity contact = null;
			Query contactQ = session.createQuery("from ContactEntity where contact_id = '"+userId+"'");
			Iterator contactItr = contactQ.list().iterator();
			if(contactItr.hasNext()){
				contact = (ContactEntity) contactItr.next();
			}
			AssetEventEntity assetEvent = null;
			Query aeQuery = session.createQuery(" from AssetEventEntity ae  where assetEventId ="+assetEventId);
			Iterator aeItr = aeQuery.list().iterator();
			if(aeItr.hasNext()){
				assetEvent = (AssetEventEntity) aeItr.next();
			}

			SOSAlertsHistoryEntity historyObj = new SOSAlertsHistoryEntity();
			historyObj.setUpdatedBy(contact);
			historyObj.setAssetEventId(assetEvent.getAssetEventId());
			historyObj.setComments(comments);
			historyObj.setUpdatedTime(currTS);
			session.save(historyObj);
			
			if(SOSCategory != null)
				comments=SOSCategory.getCategoryId()+"|"+comments;

			assetEvent.setComments(comments);
			session.update(assetEvent);

		}catch(Exception e){
			e.printStackTrace();
			fLogger.error("Exception Caught: "+e.getMessage());
			response = "FAILURE";
		}finally{
			try{
				if(session.getTransaction().isActive()){
					session.getTransaction().commit();
				}if(session.isOpen()){
					session.flush();
					session.close();
				}
			}catch (Exception e) {
				fLogger.error("Exception Caught while commiting: "+e.getMessage());
			}
		}

		return response;
	}

	public String closeSOSAlerts(LinkedHashMap<String, Object> reqObj) {
		Logger iLogger=InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();
		String response = "SUCCESS";
		String partitionkey=null;
		
		try {
			String loginId = (String) reqObj.get("login_id");
			int assetEventId = Integer.parseInt((String)reqObj.get("asset_event_id"));
			String comments = (String) reqObj.get("comments");
			int categoryId = Integer.parseInt((String) reqObj.get("category_id"));
			
			CommonUtil util = new CommonUtil();
			String isUserValid = util.inputFieldValidation(comments);
			if(!isUserValid.equals("SUCCESS")){
				throw new CustomFault(isUserValid);
			}
			
			Date currTime = new Date();
			Timestamp currTS = new Timestamp(currTime.getTime());
			IstGmtTimeConversion timeConvObj = new IstGmtTimeConversion();
			Timestamp gmtTS = timeConvObj.convertIstToGmt(currTS);
			//CR300.sn
			if(gmtTS!=null){
				Date parsedDate=gmtTS;
			    SimpleDateFormat dateformatter = new SimpleDateFormat("yyyyMM");  
			    partitionkey = dateformatter.format(parsedDate);
			}
			//CR300.en

			session.beginTransaction();
			Query query = session
					.createQuery(" from SOSCategoryMasterEntity where categoryId = "
							+ categoryId);
			Iterator queryItr = query.list().iterator();
			SOSCategoryMasterEntity SOSCategory = null;
			if (queryItr.hasNext()) {
				SOSCategory = (SOSCategoryMasterEntity) queryItr.next();
			}

			String userId = new CommonUtil().getUserId(loginId);			
			ContactEntity contact = null;
			Query contactQ = session
					.createQuery("from ContactEntity where contact_id = '"
							+ userId + "'");
			Iterator contactItr = contactQ.list().iterator();
			if (contactItr.hasNext()) {
				contact = (ContactEntity) contactItr.next();
			}
			AssetEventEntity assetEvent = null;
			Query aeQuery = session.createQuery(" from AssetEventEntity ae  where assetEventId ="
							+ assetEventId);
			Iterator aeItr = aeQuery.list().iterator();
			if (aeItr.hasNext()) {
				assetEvent = (AssetEventEntity) aeItr.next();
			}
			
			SOSAlertsHistoryEntity historyObj = new SOSAlertsHistoryEntity();
	        //CR300.sn		
	    	DecimalFormat df = new DecimalFormat("0.00");	    	
	    	long resolutionTimeInMillis=gmtTS.getTime()- assetEvent.getEventGeneratedTime().getTime();
	    	double resolutionTimeInHr = ((resolutionTimeInMillis * 1.0) / (1000 * 60 * 60)); 
	    	String resolutionTimeInHrRoundedOff= df.format(resolutionTimeInHr);	    	
	        Query queryforSOS = session.createSQLQuery("SELECT ae.Serial_Number,ae.Event_Generated_Time, ae.Event_Closed_Time,"
	        		+ " be.Event_Description, be.Event_Name, ae.Event_Severity, json_extract(ams.TxnData,'$.CMH') as CMH,"
	        		+ " et.Event_Type_Name, et.Event_Type_ID from asset_event ae, business_event be, asset_monitoring_snapshot ams,"
	        		+ " event_type et where ae.Serial_Number=ams.Serial_Number AND be.Event_ID=ae.Event_ID"
	        		+ " AND ae.Event_Type_ID = et.Event_Type_ID AND ae.Asset_Event_ID = " + "'" + assetEventId + "'");

	        Iterator baseItr = queryforSOS.list().iterator();
	        if (baseItr.hasNext()) {
	        	Object obj[]  = (Object[]) baseItr.next();
	        	historyObj.setSerialNumber((String)obj[0]);
	        	historyObj.setAlertGenerationTime((Timestamp)obj[1]);
				historyObj.setAlertClosureTime(gmtTS);
				historyObj.setAlertDescription((String)obj[3]);
				historyObj.setAlert((String)obj[4]);
				historyObj.setAlertSeverity((String)obj[5]);
				historyObj.setCmh(((String)obj[6]).replace("\"", ""));
				historyObj.setAlertCategory((String)obj[7]);
			}
	        historyObj.setCategory(SOSCategory.getCategoryName());
	        //CR300.en	
			historyObj.setUpdatedBy(contact);
			historyObj.setAssetEventId(assetEvent.getAssetEventId());
			historyObj.setComments(comments);
			historyObj.setUpdatedTime(currTS);

			//CR300.sn
			historyObj.setResolutionTime(resolutionTimeInHrRoundedOff);			
			historyObj.setPartitionKey(partitionkey);			
			//CR300.en
			session.save(historyObj);
			
			if (SOSCategory != null)
				comments = SOSCategory.getCategoryId() + "|" + comments;

			assetEvent.setActiveStatus(0);
			assetEvent.setEventClosedTime(gmtTS);
			assetEvent.setComments(comments);
			assetEvent.setCreated_timestamp(currTS);
			session.update(assetEvent);
			
			//Df20180416 @Roopa Pushing the SOS closure records to KAFKA queue for MOOLDA_Alerts
			
			try{
				
				HashMap<String,String> MOOLDADataPayloadMap = new HashMap<String,String>();
				
				MOOLDADataPayloadMap.put("ASSET_ID",assetEvent.getSerialNumber().getSerial_number().getSerialNumber());
				MOOLDADataPayloadMap.put("EVT_GEN_TIME",String.valueOf(assetEvent.getEventGeneratedTime()));
				MOOLDADataPayloadMap.put("EVT_CLOSE_TIME",String.valueOf(gmtTS));
				MOOLDADataPayloadMap.put("EVT_CREATED_TIME",String.valueOf(currTS));
				//MOOLDADataPayloadMap.put(String.valueOf(assetEvent.getEventId().getEventId()), "0");
				MOOLDADataPayloadMap.put("EVENT_ID",String.valueOf(assetEvent.getEventId().getEventId()));
				MOOLDADataPayloadMap.put("EVENT_STATUS","0");
				MOOLDADataPayloadMap.put("TXN_TIME", String.valueOf(gmtTS));
				MOOLDADataPayloadMap.put("TXN_KEY","SOS"+"_"+assetEvent.getSerialNumber().getSerial_number().getSerialNumber()+"_"+gmtTS);
				
//	    		iLogger.debug(MOOLDADataPayloadMap.get("TXN_KEY")+":AGS:DataProcessing::invoking MOOLDA_Alerts Kafka publisher for EventID closue:"+assetEvent.getEventId().getEventId());
	    		new MOOLDAKafkaPublisher(MOOLDADataPayloadMap.get("TXN_KEY"),MOOLDADataPayloadMap);
	    		}
	    		catch(Exception e){
	    		e.printStackTrace();
	    		fLogger.fatal("SOS:Alert Closure Exception in invoking MOOLDA_Alerts Kafka publisher for EventID closue:"+assetEvent.getEventId().getEventId()+":"+e.getMessage());
	    		}
			
			//Df20171003 @Roopa Pushing the records to KAFKA queue for MOOLDA_Alerts END

		} catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception Caught: " + e.getMessage());
			response = "FAILURE";
		} finally {
			try {
				if (session.getTransaction().isActive()) {
					session.getTransaction().commit();
				}
				if (session.isOpen()) {
					session.flush();
					session.close();
				}
			} catch (Exception e) {
				fLogger.error("Exception Caught while commiting: "
						+ e.getMessage());
			}
		}
		return response;
	}

	//CR300.sn
	public List<SOSAlertResponseContract> getSOSAlert(String accountFilter, String accountIds, String profileCodes, String modelCodes, String startDate, String endDate, String countryCode) {
		Logger iLogger=InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		List<SOSAlertResponseContract> responseList = new LinkedList<SOSAlertResponseContract>();

		String query="SELECT " + 
				"    croe.Serial_Number, " + 
				"    croe.Profile, " + 
				"    croe.Model, " + 
				"    croe.CustomerName, " + 
				"    croe.Customer_Mobile, " + 
				"    croe.Dealer_Name, " + 
				"    croe.Zone, " + 
				"    croe.Comm_State, " + 
				"    croe.Comm_District, " + 
				"    croe.Comm_City, " + 
				"    sah.Alert, " + 
				"    sah.Alert_Category, " + 
				"    sah.Alert_Generation_Time, " + 
				"    sah.Alert_Closure_Time, " + 
				"    sah.Resolution_Time, " + 
				"    sah.CMH, " + 
				"    sah.Alert_Description, " + 
				"    sah.Alert_Severity, " + 
				"    croe.Installed_date, " + 
				"    croe.Comm_Address, " + 
				"    sah.Category, " + 
				"    sah.Comments, " + 
				"    sah.partitionKey " + 
				"FROM " + 
				"    com_rep_oem_enhanced croe, " + 
				"    SOS_Alerts_History sah " + 
				"WHERE " + 
				"    croe.Serial_Number = sah.Serial_Number";
		if(accountFilter != null) {
			if (accountFilter.equalsIgnoreCase("RegionCode")) {
				query += " AND croe.RegionCode in (" + accountIds + ")";
			}else if(accountFilter.equalsIgnoreCase("ZonalCode")) {
				query += " AND croe.ZonalCode in (" + accountIds + ")";
			}else if(accountFilter.equalsIgnoreCase("DealerCode")){
				query += " AND croe.DealerCode in (" + accountIds + ")";
			}else if(accountFilter.equalsIgnoreCase("CustCode")){
				query += " AND croe.CustomerCode in (" + accountIds + ")";
			}
		}
		if (countryCode !=null) {
			query += " AND croe.CountryCode='" + countryCode + "'";
		}
		if (profileCodes != null && !profileCodes.equalsIgnoreCase("null")) {
				query += " AND croe.ProfileMasterCode in (" + profileCodes + ")";
		}
		if (modelCodes != null && !modelCodes.equalsIgnoreCase("null")) {
				query += " AND croe.ModelMasterCode in (" + modelCodes + ")";
		}
		query += " AND sah.partitionKey BETWEEN (DATE_FORMAT('" + startDate + "', '%Y%m'))" +
				" AND (DATE_FORMAT('" + endDate + "', '%Y%m'))" + 
				" AND sah.Alert_Closure_Time BETWEEN '" + startDate + "' AND '" + endDate + "'";
		
		iLogger.info("Final Query : " +query);
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		try{
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(query);
			while(rs.next()){
				SOSAlertResponseContract respObj = new SOSAlertResponseContract();
				respObj.setSerialNumber(rs.getString("Serial_Number"));
				respObj.setProfile(rs.getString("Profile"));
				respObj.setModel(rs.getString("Model"));
				respObj.setCustomerName(rs.getString("CustomerName"));
				respObj.setCustomerMobile(rs.getString("Customer_Mobile"));
				respObj.setDealerName(rs.getString("Dealer_Name"));
				respObj.setCommState(rs.getString("Comm_State"));
				respObj.setCommDistrict(rs.getString("Comm_District"));
				respObj.setCommCity(rs.getString("Comm_City"));
				respObj.setAlert(rs.getString("Alert"));
				respObj.setAlertCategory(rs.getString("Alert_Category"));
				respObj.setAlertGenerationTime(String.valueOf(rs.getTimestamp("Alert_Generation_Time")));
				respObj.setAlertClosureTime(String.valueOf(rs.getTimestamp("Alert_Closure_Time")));
				respObj.setResolutionTime(rs.getString("Resolution_Time"));
				respObj.setcMH(rs.getString("CMH"));
				respObj.setAlertDescription(rs.getString("Alert_Description"));
				respObj.setInstalledDate(String.valueOf(rs.getTimestamp("Installed_date")));
				respObj.setCommAddress(rs.getString("Comm_Address"));
				respObj.setCategory(rs.getString("Category"));
				respObj.setPartitionKey(rs.getString("partitionKey"));
				respObj.setComments(rs.getString("Comments"));
				respObj.setZone(rs.getString("Zone"));
				respObj.setAlertSeverity(rs.getString("Alert_Severity"));
				
				responseList.add(respObj);
			}
				
		}catch(Exception e){
			 fLogger.error("Error while fetching data for SOS alert!!!");
		}
		return responseList;
	}//CR300.en
}
