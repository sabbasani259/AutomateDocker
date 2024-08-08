/*
 *  20230720 : CR432 : Prasanna Lakshmi : GeoFence/TimeFence API Changes 
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.pojo.GeoFenceDetailsPOJONew;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.ListToStringConversion;

public class GeofenceDetailsImplV4 {


	public GeoFenceDetailsPOJONew getLandmarkDetailsForVINNew(String loginID, String accountCode, String vIN) {
		GeoFenceDetailsPOJONew responseObj = new GeoFenceDetailsPOJONew();
        Logger fLogger = FatalLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		
		try{			
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
					
			//STEP1: Get landmark_id using vIN as Input parameter
			String landmarkIDQ = "select landmark_id from landmark_asset where serial_number='"+vIN+"'";
			rs = stmt.executeQuery(landmarkIDQ);
			
			String landmarkId=null;
			String landmarkCategoryId=null;
			while(rs.next()){
				if(rs.getString("landmark_id")!=null){
					responseObj.setLandmarkID(rs.getString("landmark_id") );
					landmarkId=rs.getString("landmark_id");
					responseObj.setVIN(vIN);
				}								
			}
			
			//STEP2: Get landmark details using landmark_id as Input parameter			
			String landmarkDetailQ = "select landmark_category_id,landmark_name,radius,latitude,longitude,Address,IsArrival,IsDeparture,CreatedTime from landmark where landmark_id='"+landmarkId+"'";
			rs = stmt.executeQuery(landmarkDetailQ);
			while(rs.next()){
				if(rs.getString("landmark_category_id") !=null){
					responseObj.setLandmarkCategoryID(rs.getString("landmark_category_id"));
					landmarkCategoryId=rs.getString("landmark_category_id");
				}
				if(rs.getString("landmark_name") !=null){
					responseObj.setLandmarkName(rs.getString("landmark_name"));
				}
				if(rs.getString("CreatedTime") !=null){
					responseObj.setCreatedDate(rs.getString("CreatedTime"));
				}				
				HashMap<String,Object> geofenceDetails = new HashMap<String,Object>();
				if(rs.getString("latitude") != null){
					geofenceDetails.put("LAT", rs.getString("latitude"));
				}
				if(rs.getString("longitude") != null){
					geofenceDetails.put("LONG", rs.getString("longitude"));
				}
				if(rs.getString("radius") != null){
					geofenceDetails.put("Radius", rs.getString("radius"));
				}
				if(rs.getString("Address") != null){
					geofenceDetails.put("Address", rs.getString("Address"));
				}
				if(rs.getString("IsArrival") != null){
					geofenceDetails.put("IsArrival", rs.getString("IsArrival"));
				}
				if(rs.getString("IsDeparture") != null){
					geofenceDetails.put("IsDeparture", rs.getString("IsDeparture"));
				}				
				responseObj.setGeofenceDetails(geofenceDetails);
				
			}
			
			//STEP3: Get Landmark_Category_Name using landmark_category_id as Input parameter	
			String landmarkCategoryQ="select Landmark_Category_Name from landmark_catagory where landmark_category_id='"+landmarkCategoryId+"'";
			rs = stmt.executeQuery(landmarkCategoryQ);
			while(rs.next()){
				
				responseObj.setLandmarkCategoryName(rs.getString("Landmark_Category_Name"));
			}
			
			//STEP3: Get account_code using VIN and account_type as Input parameter
			String customerCodeQ="select b.account_code as account_code from asset_owner_snapshot a, account b where a.account_id=b.account_id and serial_number='"+vIN+"' and account_type='customer'";
			rs = stmt.executeQuery(customerCodeQ);
			while(rs.next()){
				responseObj.setCustomerCode(rs.getString("account_code"));
			}
			
			
			//Get the mobile number
			String landmarkPreferenceQ="Select MobileNumber from landmark_preference where landmark_id="+landmarkId+" and MobileNumber is not null";
			rs =  stmt.executeQuery(landmarkPreferenceQ);
			String mobileNum=null;
			while(rs.next())
			{
				mobileNum = rs.getString("MobileNumber");
			}
			if(mobileNum!=null)
			{
				responseObj.setMobileNumber(mobileNum);	
			}
			
			//Get Landmark preference for GeoFence alert only if notification preference is set for the VIN
			int preferenceSet =0;
			String notificationPrefQ = "select AssetID from notification_preference where AlertTypeID=5 and AssetID='"+vIN+"'";
			rs = stmt.executeQuery(notificationPrefQ);
			while(rs.next())
			{
				preferenceSet=1;
			}
			
			if(preferenceSet==0)
			{
				fLogger.fatal("Notification preference not set the VIN and hence not returning the notification preference details");
				return responseObj;
			}
			
			
			String landmarkPrefQ = "select JSON_EXTRACT(NotificationPreference,'$.SMS') as SMS," +
					" JSON_EXTRACT(NotificationPreference,'$.WhatsApp') as WhatsApp, " +
					" JSON_EXTRACT(NotificationPreference,'$.\"Voice Call\"') as VoiceCall ," +
					" JSON_EXTRACT(NotificationPreference,'$.\"Push notification\"') as PushNotification," +
					" StartTime, EndTime " +
					" from landmark_preference where landmark_id='"+landmarkId+"'";
			rs = stmt.executeQuery(landmarkPrefQ);
			while(rs.next())
			{
				if(rs.getTime("StartTime")!=null && rs.getTime("EndTime")!=null)
				{
					String startTime = rs.getTime("StartTime").toString();
					String endTime =  rs.getTime("EndTime").toString();
					
					if(startTime.equalsIgnoreCase("00:00:00") && endTime.equalsIgnoreCase("23:59:59"))
					{
						HashMap<String,Object> dayTimePreference = new HashMap<String,Object>();
						String mobileNumber=null;
						if(rs.getString("SMS")!=null)
						{
							dayTimePreference.put("SMS", "1");
							mobileNumber = rs.getString("SMS").replaceAll("\"", "").replaceAll("\\\\", "");
						}
						else
							dayTimePreference.put("SMS", "0");
						
//						if(rs.getString("WhatsApp")!=null)
//						{
//							dayTimePreference.put("WhatsApp", "1");
//							mobileNumber = rs.getString("WhatsApp").replaceAll("\"", "").replaceAll("\\\\", "");
//						}
//						else
//							dayTimePreference.put("WhatsApp", "0");
//						
//						if(rs.getString("VoiceCall")!=null)
//						{
//							dayTimePreference.put("Voice Call", "1");
//							mobileNumber = rs.getString("VoiceCall").replaceAll("\"", "").replaceAll("\\\\", "");
//						}
//						else
//							dayTimePreference.put("Voice Call", "0");
//						
//						
						if(rs.getString("PushNotification")!=null)
						{
							dayTimePreference.put("Push notification", "1");
							mobileNumber = rs.getString("PushNotification").replaceAll("\"", "").replaceAll("\\\\", "");
						}
						else
							dayTimePreference.put("Push notification", "0");
						
						
						responseObj.setNotificationDetails(dayTimePreference);						
					}
				}
			}
		}catch(Exception e){
			fLogger.fatal("GeoFenceDetailsImplv4:getLandmarkDetailsForVINNew:DAL:vIN:"+vIN+":Exception:",e.getMessage(),e);

		}finally{
			try
			{
				if(stmt!=null)
					stmt.close();
				if(con!=null)
					con.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("GeoFenceDetailsImplv4:getLandmarkDetailsForVINNew:DAL:vIN:"+vIN+"; Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		return responseObj;
	}	
	
	
	/**************************************************************************************************************************/
	
	
	public List<GeoFenceDetailsPOJONew> getAllLandmarksForUserNew(String loginID, String accountCode) 
	{
		List<GeoFenceDetailsPOJONew> responseList = new LinkedList<GeoFenceDetailsPOJONew>();
        Logger fLogger = FatalLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		
		try
		{			
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
					
			
			//STEP1: Get the list of landmarks associated for a user
			List<Integer> landmarkIDList = new LinkedList<Integer>();
			String landmarkQ="select a.landmark_id as landmarkID from landmark a, landmark_catagory b, tenancy c, account_tenancy d, account_contact e" +
					" where a.landmark_category_id=b.landmark_category_id and b.tenancy_id=c.tenancy_id and " +
					" c.tenancy_id=d.tenancy_id and d.account_id in (select account_id from account where mapping_code in (select mapping_code from account where account_id in (select account_id from account_contact where contact_id = '"+loginID+"')))" +
					" and a.Activestatus=1 and a.GeoFence=1 and e.contact_id='"+loginID+"'";
			rs= stmt.executeQuery(landmarkQ);
			while(rs.next())
			{
				landmarkIDList.add(rs.getInt("landmarkID"));
			}
			
			
			for(int i=0; i<landmarkIDList.size(); i++)
			{
				GeoFenceDetailsPOJONew responseObj = new GeoFenceDetailsPOJONew();
				
				int landmarkId=landmarkIDList.get(i);
				String landmarkCategoryId=null;
				
				//STEP2: Get landmark details using landmark_id as Input parameter			
				String landmarkDetailQ = "select landmark_category_id,landmark_name,radius,latitude,longitude,Address,IsArrival," +
						"IsDeparture,CreatedTime from landmark where landmark_id='"+landmarkId+"'";
				rs = stmt.executeQuery(landmarkDetailQ);
				while(rs.next())
				{
					responseObj.setLandmarkID(String.valueOf(landmarkId));
					
					if(rs.getString("landmark_category_id") !=null){
						responseObj.setLandmarkCategoryID(rs.getString("landmark_category_id"));
						landmarkCategoryId=rs.getString("landmark_category_id");
					}
					if(rs.getString("landmark_name") !=null){
						responseObj.setLandmarkName(rs.getString("landmark_name"));
					}
					if(rs.getString("CreatedTime") !=null){
						responseObj.setCreatedDate(rs.getString("CreatedTime"));
					}				
					HashMap<String,Object> geofenceDetails = new HashMap<String,Object>();
					if(rs.getString("latitude") != null){
						geofenceDetails.put("LAT", rs.getString("latitude"));
					}
					if(rs.getString("longitude") != null){
						geofenceDetails.put("LONG", rs.getString("longitude"));
					}
					if(rs.getString("radius") != null){
						geofenceDetails.put("Radius", rs.getString("radius"));
					}
					if(rs.getString("Address") != null){
						geofenceDetails.put("Address", rs.getString("Address"));
					}
					if(rs.getString("IsArrival") != null){
						geofenceDetails.put("IsArrival", rs.getString("IsArrival"));
					}
					if(rs.getString("IsDeparture") != null){
						geofenceDetails.put("IsDeparture", rs.getString("IsDeparture"));
					}				
					responseObj.setGeofenceDetails(geofenceDetails);
					
				}
				
				//STEP3: Get Landmark_Category_Name using landmark_category_id as Input parameter	
				if(landmarkCategoryId!=null)
				{
					String landmarkCategoryQ="select Landmark_Category_Name from landmark_catagory where landmark_category_id='"+landmarkCategoryId+"'";
					rs = stmt.executeQuery(landmarkCategoryQ);
					while(rs.next()){
						
						responseObj.setLandmarkCategoryName(rs.getString("Landmark_Category_Name"));
					}
				}
				
				//Get the mobile number
				String landmarkPreferenceQ="Select MobileNumber from landmark_preference " +
						"where landmark_id="+landmarkId+" and MobileNumber is not null";
				rs =  stmt.executeQuery(landmarkPreferenceQ);
				String mobileNum=null;
				while(rs.next())
				{
					mobileNum = rs.getString("MobileNumber");
				}
				if(mobileNum!=null)
				{
					responseObj.setMobileNumber(mobileNum);	
				}
				
				//Get the list of machines associated to the landmark
				String landmarkAssetQ="select serial_number from landmark_asset where landmark_id="+landmarkId;
				rs=stmt.executeQuery(landmarkAssetQ);
				String VIN=null;
				while(rs.next())
				{
					if(VIN==null)
						VIN=rs.getString("serial_number");
					else
						VIN=VIN+","+rs.getString("serial_number");
				}
				responseObj.setVIN(VIN);
				int preferenceSet =0;
				if(VIN != null){
					String sqlInStamtFormateVins = ListToStringConversion.vinsToSQlInVinsList(VIN);
				
					String notificationPrefQ = "select AssetID from notification_preference where AlertTypeID=5 and AssetID in ("+sqlInStamtFormateVins+")";
					rs = stmt.executeQuery(notificationPrefQ);
					while(rs.next())
					{
						preferenceSet=1;
					}
					
					if(preferenceSet==0)
					{
						fLogger.fatal("Notification preference not set the VIN and hence not returning the notification preference details");
						responseObj.setNotificationDetails(null);

					}
				}
				
				if(preferenceSet==1 || VIN == null){
				String landmarkPrefQ = "select JSON_EXTRACT(NotificationPreference,'$.SMS') as SMS," +
						" JSON_EXTRACT(NotificationPreference,'$.WhatsApp') as WhatsApp, " +
						" JSON_EXTRACT(NotificationPreference,'$.\"Voice Call\"') as VoiceCall ," +
						" JSON_EXTRACT(NotificationPreference,'$.\"Push notification\"') as PushNotification," +
						" StartTime, EndTime " +
						" from landmark_preference where landmark_id='"+landmarkId+"'";
				rs = stmt.executeQuery(landmarkPrefQ);
				while(rs.next())
				{
					if(rs.getTime("StartTime")!=null && rs.getTime("EndTime")!=null)
					{
						String startTime = rs.getTime("StartTime").toString();
						String endTime =  rs.getTime("EndTime").toString();
						
						if(startTime.equalsIgnoreCase("00:00:00") && endTime.equalsIgnoreCase("23:59:59"))
						{
							HashMap<String,Object> dayTimePreference = new HashMap<String,Object>();
							String mobileNumber=null;
							if(rs.getString("SMS")!=null)
							{
								dayTimePreference.put("SMS", "1");
								mobileNumber = rs.getString("SMS").replaceAll("\"", "").replaceAll("\\\\", "");
							}
							else
								dayTimePreference.put("SMS", "0");
							
//							if(rs.getString("WhatsApp")!=null)
//							{
//								dayTimePreference.put("WhatsApp", "1");
//								mobileNumber = rs.getString("WhatsApp").replaceAll("\"", "").replaceAll("\\\\", "");
//							}
//							else
//								dayTimePreference.put("WhatsApp", "0");
//							
//							if(rs.getString("VoiceCall")!=null)
//							{
//								dayTimePreference.put("Voice Call", "1");
//								mobileNumber = rs.getString("VoiceCall").replaceAll("\"", "").replaceAll("\\\\", "");
//							}
//							else
//								dayTimePreference.put("Voice Call", "0");
							
							
							if(rs.getString("PushNotification")!=null)
							{
								dayTimePreference.put("Push notification", "1");
								mobileNumber = rs.getString("PushNotification").replaceAll("\"", "").replaceAll("\\\\", "");
							}
							else
								dayTimePreference.put("Push notification", "0");
							
							
							responseObj.setNotificationDetails(dayTimePreference);						
							//responseObj.setMobileNumber(mobileNumber);						
						}
						
						/*else
						{
							HashMap<String,Object> otherTimePreference = new HashMap<String,Object>();
							String mobileNumber=null;
							if(rs.getString("SMS")!=null)
							{
								otherTimePreference.put("SMS", "1");
								mobileNumber = rs.getString("SMS").replaceAll("\"", "").replaceAll("\\\\", "");
							}
							else
								otherTimePreference.put("SMS", "0");
							
							if(rs.getString("WhatsApp")!=null)
							{
								otherTimePreference.put("WhatsApp", "1");
								mobileNumber = rs.getString("WhatsApp").replaceAll("\"", "").replaceAll("\\\\", "");
							}
							else
								otherTimePreference.put("WhatsApp", "0");
							
							if(rs.getString("VoiceCall")!=null)
							{
								otherTimePreference.put("Voice Call", "1");
								mobileNumber = rs.getString("VoiceCall").replaceAll("\"", "").replaceAll("\\\\", "");
							}
							else
								otherTimePreference.put("Voice Call", "0");
							
							
							if(rs.getString("PushNotification")!=null)
							{
								otherTimePreference.put("Push Notification", "1");
								mobileNumber = rs.getString("PushNotification").replaceAll("\"", "").replaceAll("\\\\", "");
							}
							else
								otherTimePreference.put("Push notification", "0");
							
							
							responseObj.setOtherTimePreference(otherTimePreference);						
							if(mobileNumber!=null)
								responseObj.setMobileNumber(mobileNumber);
						}*/
					}
				}
				}
				responseList.add(responseObj);
				
			}
		}
		
		catch(Exception e)
		{
			fLogger.fatal("GeoFenceDetailsImplv4:getAllLandmarksForUserNew:DAL:LoginID:"+loginID+":Exception:",e.getMessage(),e);

		}
		
		finally
		{
			try
			{
				if(stmt!=null)
					stmt.close();
				if(con!=null)
					con.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("GeoFenceDetailsImplv4:getAllLandmarksForUserNew:DAL:LoginID:"+loginID+"; Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		return responseList;
	}
	
	
}
