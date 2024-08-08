package remote.wise.service.implementation;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.GeoFenceDetailsPOJO;
import remote.wise.pojo.GeoFenceDetailsPOJONew;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.ListToStringConversion;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

/*
* JCB6246 - Rajani Nagaraju - 20220511 - Removing the machine association from Landmark if no machine list is passed to setLandmarkDetails API call
 */
public class GeoFenceDetailsImpl {
	
	public String deleteLandmarkDetails(String loginID, String accountCode, String landmark_id,String Source) {
		
		String status="SUCCESS";
		Logger fLoger=FatalLoggerClass.logger;
				
		Connection con=null;
		Statement stmt=null;
		ResultSet rs=null;
		
		try{		    		    
		    //Get DB connection
		    con = new ConnectMySQL().getConnection();
			stmt=con.createStatement();
			
			
			//Validate Landmark for deletion
			int validLandmark=0;
			String landmarkQ ="select landmark_id from landmark where landmark_id='"+landmark_id+"' and ActiveStatus=1";
			rs = stmt.executeQuery(landmarkQ);
			while(rs.next())
			{
				validLandmark=1;
			}
			
			if(validLandmark==0)
			{
				throw new Exception("Invalid LandmarkID :"+landmark_id);
			}
			//STEP1: Delete the entry for the VIN in notification_preference table
	        String selectQ ="select serial_number from landmark_asset where landmark_id='"+landmark_id+"'";
	        rs=stmt.executeQuery(selectQ);
	        List<String> asstIDList= new LinkedList<String>();
	        while(rs.next()){
	        	asstIDList.add(rs.getString("serial_number"));
	        	
	        }
	        String listToString=new ListToStringConversion().getStringList(asstIDList).toString(); 
	        if(listToString.length() != 0){
	        	 String updateQ1="delete from notification_preference where AssetID in ("+listToString+") and AlertTypeID=5";
	 	        stmt.executeUpdate(updateQ1);
	        }  
	        
			//STEP2: Update ActiveStatus value to 0 in landmark table
			String updateQ2="update landmark set ActiveStatus=0 where landmark_id='"+landmark_id+"'";
			stmt.executeUpdate(updateQ2);
			
			//STEP3: Delete the entry for landmark_id in landmark_asset table
			String updateQ3 = "delete from landmark_asset where landmark_id ='"+landmark_id+"'";
			stmt.executeUpdate(updateQ3);
			
			//STEP4: Delete the entry from landmark_preference table
			String update4Q="delete from landmark_preference where landmark_id="+landmark_id;
			stmt.executeUpdate(update4Q);
			
			//STEP5: Insert details to landmark_change_log table
			int landmarkId=Integer.parseInt(landmark_id);
			String UpdatedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			int partitionKey = Integer.valueOf(UpdatedTime.substring(0,7).replace("-", ""));
			String InsertQ="INSERT INTO landmark_change_log(landmark_id,UpdatedTime,UserID,Status,PartitionKey,Source) VALUES ("+landmarkId+",'"+UpdatedTime+"','"+loginID+"',0,"+partitionKey+", '"+Source+"')";
			
			stmt.executeUpdate(InsertQ);
		    		  
			
		}catch(Exception e){
			status="FAILURE";
			fLoger.fatal("GeoFenceDetailsImpl:deleteLandmarkDetails:DAL:loginID:"+loginID+":landmark_id:"+landmark_id+"; Exception:",e.getMessage(),e);			
		}finally{
			try{
				if(stmt!=null){
					stmt.close();
				}
				if(con!=null){
					stmt.close();
				}
			}catch(Exception e){
				fLoger.fatal("GeoFenceDetailsImpl:deleteLandmarkDetails:DAL:loginID:"+loginID+"; landmark_id:"+landmark_id+"; Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		
				
		return status;
	}
	
	//*************************************************************************************************************************************************

	public GeoFenceDetailsPOJO getLandmarkNotificationDetails(String loginID, String accountCode, String landmark_id) {
		
		GeoFenceDetailsPOJO responseObj = new GeoFenceDetailsPOJO();
        Logger fLogger = FatalLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		
		try{		
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			//Get the mobile number
			String landmarkPreferenceQ="Select MobileNumber from landmark_preference where landmark_id="+landmark_id+" and MobileNumber is not null";
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
			
			//Get Landmark preference for GeoFence alert
			String landmarkPrefQ = "select JSON_EXTRACT(NotificationPreference,'$.SMS') as SMS," +
					" JSON_EXTRACT(NotificationPreference,'$.WhatsApp') as WhatsApp, " +
					" JSON_EXTRACT(NotificationPreference,'$.\"Voice Call\"') as VoiceCall ," +
					" JSON_EXTRACT(NotificationPreference,'$.\"Push notification\"') as PushNotification," +
					" StartTime, EndTime " +
					" from landmark_preference where landmark_id='"+landmark_id+"'";
			rs = stmt.executeQuery(landmarkPrefQ);
			while(rs.next())
			{
				if(rs.getTime("StartTime")!=null && rs.getTime("EndTime")!=null)
				{
					String startTime = rs.getTime("StartTime").toString();
					String endTime =  rs.getTime("EndTime").toString();
					
					if(startTime.equalsIgnoreCase("08:00:00") && endTime.equalsIgnoreCase("20:00:00"))
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
						
						if(rs.getString("WhatsApp")!=null)
						{
							dayTimePreference.put("WhatsApp", "1");
							mobileNumber = rs.getString("WhatsApp").replaceAll("\"", "").replaceAll("\\\\", "");
						}
						else
							dayTimePreference.put("WhatsApp", "0");
						
						if(rs.getString("VoiceCall")!=null)
						{
							dayTimePreference.put("Voice Call", "1");
							mobileNumber = rs.getString("VoiceCall").replaceAll("\"", "").replaceAll("\\\\", "");
						}
						else
							dayTimePreference.put("Voice Call", "0");
						
						
						if(rs.getString("PushNotification")!=null)
						{
							dayTimePreference.put("Push notification", "1");
							mobileNumber = rs.getString("PushNotification").replaceAll("\"", "").replaceAll("\\\\", "");
						}
						else
							dayTimePreference.put("Push notification", "0");
						
						
						responseObj.setDayTimePreference(dayTimePreference);
						responseObj.setLandmarkID(landmark_id);
						//responseObj.setMobileNumber(mobileNumber);						
					}
					
					else
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
							otherTimePreference.put("Push notification", "1");
							mobileNumber = rs.getString("PushNotification").replaceAll("\"", "").replaceAll("\\\\", "");
						}
						else
							otherTimePreference.put("Push notification", "0");
						
						
						responseObj.setOtherTimePreference(otherTimePreference);
						responseObj.setLandmarkID(landmark_id);
						//responseObj.setMobileNumber(mobileNumber);
					}
				}
			}		
		}catch(Exception e){
			fLogger.fatal("GeoFenceDetailsImpl:getLandmarkNotificationDetails:DAL:landmark_id:"+landmark_id+":Exception:",e.getMessage(),e);

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
				fLogger.fatal("GeoFenceDetailsImpl:getLandmarkNotificationDetails:DAL:landmark_id:"+landmark_id+"; Exception in closing the connection:"+e.getMessage(),e);
			}
			
		}
		
		return responseObj;
	}
	
	//***************************************************************************************************************************

	public GeoFenceDetailsPOJO getLandmarkDetailsForVIN(String loginID, String accountCode, String vIN) {
		GeoFenceDetailsPOJO responseObj = new GeoFenceDetailsPOJO();
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
					
					if(startTime.equalsIgnoreCase("08:00:00") && endTime.equalsIgnoreCase("20:00:00"))
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
						
						if(rs.getString("WhatsApp")!=null)
						{
							dayTimePreference.put("WhatsApp", "1");
							mobileNumber = rs.getString("WhatsApp").replaceAll("\"", "").replaceAll("\\\\", "");
						}
						else
							dayTimePreference.put("WhatsApp", "0");
						
						if(rs.getString("VoiceCall")!=null)
						{
							dayTimePreference.put("Voice Call", "1");
							mobileNumber = rs.getString("VoiceCall").replaceAll("\"", "").replaceAll("\\\\", "");
						}
						else
							dayTimePreference.put("Voice Call", "0");
						
						
						if(rs.getString("PushNotification")!=null)
						{
							dayTimePreference.put("Push notification", "1");
							mobileNumber = rs.getString("PushNotification").replaceAll("\"", "").replaceAll("\\\\", "");
						}
						else
							dayTimePreference.put("Push notification", "0");
						
						
						responseObj.setDayTimePreference(dayTimePreference);						
						//responseObj.setMobileNumber(mobileNumber);						
					}
					
					else
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
						/*if(mobileNumber!=null)
							responseObj.setMobileNumber(mobileNumber);*/
					}
				}
			}
		}catch(Exception e){
			fLogger.fatal("GeoFenceDetailsImpl:getLandmarkDetailsForVIN:DAL:vIN:"+vIN+":Exception:",e.getMessage(),e);

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
				fLogger.fatal("GeoFenceDetailsImpl:getLandmarkDetailsForVIN:DAL:vIN:"+vIN+"; Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		return responseObj;
	}

	
	//**********************************************************************************************************************
	public String setLandmarkDetails(String loginID,String AccountCode,int Landmark_id,int Landmark_Category_ID, String Landmark_Name, 
			String Landmark_Category_Name, String Latitude, String Longitude, double Radius, String Address, String IsArrival,
			String IsDeparture, String MobileNumber, String DayTimeNotification, String OtherTimeNotification, String machineList, String Source)
	{
		String status="SUCCESS";
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		PreparedStatement psInsert = null;
		PreparedStatement psUpdate =null;
		
		try
		{
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			//Validate mandatory parameters
			if(loginID==null || Latitude==null || Longitude==null || Landmark_Name==null || Radius < 0.5 || IsArrival==null || IsDeparture==null)
			{
				fLogger.fatal("GeofenceDetailsImpl:setLandmarkDetails:DAL:loginID:"+loginID+";Mandatory parameters is NULL");
				return "FAILURE";
			}
			
			//STEP1: Get the country code from loginID
			String countryCode="+91";
			
			
			//Validate :LandmarkID
			if(Landmark_id!=0)
			{
				int validLandmark=0;
				String landmarkQ ="select landmark_id from landmark where landmark_id='"+Landmark_id+"' and ActiveStatus=1";
				rs = stmt.executeQuery(landmarkQ);
				while(rs.next())
				{
					validLandmark=1;
				}
				
				if(validLandmark==0)
				{
					throw new Exception("Invalid LandmarkID :"+Landmark_id);
				}
			}
			
			//If Landmark Category is not passed for landmark creation, create a category and assign to the user account
			if(Landmark_Category_ID==0 && Landmark_id==0)
			{
				//Get the user tenancy
				int tenancyId=0;
				String tenancyQ="select c.tenancy_id as TenancyID from contact a, account_contact b, account_tenancy c" +
						" where a.contact_id=b.contact_id and b.account_id=c.account_id and a.contact_id='"+loginID+"'";
				rs = stmt.executeQuery(tenancyQ);
				while(rs.next())
				{
					tenancyId = rs.getInt("TenancyID");
				}
				
				if(tenancyId!=0)
				{
					//Check if landmark category exists
					String landmarkCatQ="select Landmark_Category_ID from landmark_catagory where Tenancy_ID="+tenancyId;
					rs = stmt.executeQuery(landmarkCatQ);
					while(rs.next())
					{
						Landmark_Category_ID = rs.getInt("Landmark_Category_ID");
					}
					
					//If landmark category is not defined for a account
					if(Landmark_Category_ID==0)
					{
						String landmarkCategoryName=Landmark_Name+"001";
						String insertQ="INSERT INTO landmark_catagory(Landmark_Category_Name,Color_Code,Tenancy_ID,ActiveStatus,Landmark_Category_Color_Code)" +
								" VALUES ('"+landmarkCategoryName+"', 001, "+tenancyId+", 1, 1)";
						stmt.execute(insertQ,Statement.RETURN_GENERATED_KEYS);
						rs = stmt.getGeneratedKeys();
						if(rs.next())
						{
							Landmark_Category_ID = rs.getInt(1);
						}
					}
				}
			}
			
			//------------ STEP1: Insert/Update the details to Landmark table
			if(Landmark_id==0)
			{
				String insertQ="INSERT INTO landmark(landmark_category_id,landmark_name,latitude,longitude,radius,Address," +
								"IsArrival,IsDeparture,ActiveStatus,Login_Id,country_code,CreatedTime,Source,Geofence) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				 psInsert = con.prepareStatement(insertQ,Statement.RETURN_GENERATED_KEYS);
				 psInsert.setInt(1, Landmark_Category_ID);
				 psInsert.setString(2, Landmark_Name);
				 psInsert.setString(3, Latitude);
				 psInsert.setString(4, Longitude);
				 psInsert.setDouble(5, Radius);
				 psInsert.setString(6, Address);
				 psInsert.setInt(7, Integer.valueOf(IsArrival));
				 psInsert.setInt(8, Integer.valueOf(IsDeparture));
				 psInsert.setInt(9, 1);
				 psInsert.setString(10, loginID);
				 psInsert.setString(11, countryCode);
				 psInsert.setTimestamp(12, new Timestamp(new Date().getTime()));
				 psInsert.setString(13, Source);
				 psInsert.setInt(14, 1);
				 
				 psInsert.executeUpdate();
				 
				 rs = psInsert.getGeneratedKeys();
				 if (rs.next()) 
				 {
					 Landmark_id = rs.getInt(1);
				 } 
			}
			else
			{
				String updateQ=null;
				
				if(Landmark_Category_ID!=0)
				{
					updateQ="update landmark set landmark_category_id=?, landmark_name=?, latitude=?, longitude=?,radius=?, Address=?," +
						"IsArrival=?,IsDeparture=?,ActiveStatus=?,Login_Id=?,country_code=?,LastUpdatedTime=?, Source=?, Geofence=? where landmark_id=?" ;
				
					psUpdate = con.prepareStatement(updateQ);
					psUpdate.setInt(1, Landmark_Category_ID);
					psUpdate.setString(2, Landmark_Name);
					psUpdate.setString(3, Latitude);
					psUpdate.setString(4, Longitude);
					psUpdate.setDouble(5, Radius);
					psUpdate.setString(6, Address);
					psUpdate.setInt(7, Integer.valueOf(IsArrival));
					psUpdate.setInt(8, Integer.valueOf(IsDeparture));
					psUpdate.setInt(9, 1);
					psUpdate.setString(10, loginID);
					psUpdate.setString(11, countryCode);
					psUpdate.setTimestamp(12, new Timestamp(new Date().getTime()));
					psUpdate.setString(13, Source);
					psUpdate.setInt(14, 1);
					psUpdate.setInt(15, Landmark_id);
				}
				
				else
				{
					updateQ="update landmark set landmark_name=?, latitude=?, longitude=?,radius=?, Address=?," +
							"IsArrival=?,IsDeparture=?,ActiveStatus=?,Login_Id=?,country_code=?,LastUpdatedTime=?, Source=?, Geofence=? where landmark_id=?" ;
					
						psUpdate = con.prepareStatement(updateQ);
						psUpdate.setString(1, Landmark_Name);
						psUpdate.setString(2, Latitude);
						psUpdate.setString(3, Longitude);
						psUpdate.setDouble(4, Radius);
						psUpdate.setString(5, Address);
						psUpdate.setInt(6, Integer.valueOf(IsArrival));
						psUpdate.setInt(7, Integer.valueOf(IsDeparture));
						psUpdate.setInt(8, 1);
						psUpdate.setString(9, loginID);
						psUpdate.setString(10, countryCode);
						psUpdate.setTimestamp(11, new Timestamp(new Date().getTime()));
						psUpdate.setString(12, Source);
						psUpdate.setInt(13, 1);
						psUpdate.setInt(14, Landmark_id);
				}
				int rowsUpdated = psUpdate.executeUpdate();
				
				if(rowsUpdated==0)
				{
					fLogger.fatal("GeofenceDetailsImpl:setLandmarkDetails:DAL:loginID:"+loginID+"; No landmark exists with the given landmarkId:"+Landmark_id);
					return "FAILURE";
				}
			}
			
			
			//------------------------ STEP2: Update landmark_asset mapping
			String[] machineListArray =null;
			String VINList=null;
			List<String> updatedVinList = new LinkedList<String>();
			
			if(machineList!=null)
			{
				machineListArray = machineList.split(",");
				for(int i=0; i<machineListArray.length; i++)
				{
					if(VINList==null)
						VINList="'"+machineListArray[i]+"'";
					else
						VINList=VINList+", '"+machineListArray[i]+"'";
					
					updatedVinList.add(machineListArray[i]);
				}
			}
			//JCB6246.sn
			else if (machineList==null && Landmark_id!=0)
			{
				String landmarkAssetDeleteQ ="delete from landmark_asset where landmark_id="+Landmark_id;
				stmt.executeUpdate(landmarkAssetDeleteQ);
			}
			//JCB6246.en
			
			//----------------------------- Filter out the machines which is already assigned to some other Geofence - as there is a requirement 
			//that a machine has to be tagged to only one Geofence
			if(VINList!=null)
			{
				List<String> duplicateLandmarkMachines = new LinkedList<String>();
				String landmarkDuplicateQ="select a.serial_number from landmark_asset a, landmark b where a.landmark_id=b.landmark_id and b.Geofence=1 and " +
						" a.serial_number in ("+VINList+") and b.ActiveStatus=1 and b.landmark_id !="+Landmark_id;
				rs = stmt.executeQuery(landmarkDuplicateQ);
				while(rs.next())
				{
					duplicateLandmarkMachines.add(rs.getString("serial_number"));
				}
				if(duplicateLandmarkMachines.size()>0)
				{
					fLogger.fatal("GeofenceDetailsImpl:setLandmarkDetails:DAL:loginID:"+loginID+";Machines already assigned to some other landmark:" +
							"Current Machine List:"+VINList+"; duplicateLandmarkMachines:"+duplicateLandmarkMachines);
					updatedVinList.removeAll(duplicateLandmarkMachines);
				}
			}
			
			if(updatedVinList!=null && updatedVinList.size()>0)
			{
				if(Landmark_id!=0)
				{
					String landmarkAssetDeleteQ ="delete from landmark_asset where landmark_id="+Landmark_id;
					stmt.executeUpdate(landmarkAssetDeleteQ);
				}
				
				for(int i=0; i<updatedVinList.size();i++)
				{
					String landmarkAssetQ = "INSERT INTO landmark_asset(landmark_id,serial_number) values ("+Landmark_id+", '"+updatedVinList.get(i)+"')";
					stmt.addBatch(landmarkAssetQ);
				}
				stmt.executeBatch();
			
			}
			
			
			//-------------------------------- Filter out only LL4 machines for setting the notification preferences
			List<String> ll4MachineList = new LinkedList<String>();
			if(updatedVinList!=null && updatedVinList.size()>0)
			{
				String updatedVinListAsString = new ListToStringConversion().getStringList(updatedVinList).toString();
				String ll4machineQ = "select serial_number from asset_monitoring_snapshot where serial_number in ("+updatedVinListAsString+") " +
						" and  JSON_EXTRACT(TxnData,'$.FW_VER') is not null and ( CAST(SUBSTRING(JSON_EXTRACT(TxnData,'$.FW_VER'),2,2) AS UNSIGNED) >=30 and " +
						"  CAST(SUBSTRING(JSON_EXTRACT(TxnData,'$.FW_VER'),2,2) AS UNSIGNED) < 50)";
				ResultSet rs1 = stmt.executeQuery(ll4machineQ);
				while(rs1.next())
				{
					ll4MachineList.add(rs1.getString("serial_number"));
				}
				
			}
			
			
			//------------------------------- STEP3: Insert details into landmark_preference table for Day time preferences
			if(DayTimeNotification!=null)
			{
				String startTime = "08:00:00";
				String endTime = "20:00:00";
				
				
				ObjectMapper mapper = new ObjectMapper();
				HashMap<String,Object> dayTimePrefMap =new Gson().fromJson(DayTimeNotification, HashMap.class);
				Map<String,String> dayTimePreference = new HashMap<String,String>();
				for(Map.Entry<String,Object> dayTimePref : dayTimePrefMap.entrySet())
				{
					if(dayTimePref.getValue()==null || dayTimePref.getValue().toString().equalsIgnoreCase("0"))
						continue;
					
					else
						dayTimePreference.put(dayTimePref.getKey(), MobileNumber);
				}
				JSONObject dayTimePreferenceJSON = new JSONObject(dayTimePreference);
				
				
				String updateQ ="update landmark_preference set NotificationPreference='"+dayTimePreferenceJSON+"'," +
					" StartTime='"+startTime+"', EndTime='"+endTime+"', MobileNumber='"+MobileNumber+"' where landmark_id='"+Landmark_id+"' and  StartTime='"+startTime+"'";
				int updateCount = stmt.executeUpdate(updateQ);
				if(updateCount==0)
				{
					String insertQ = "INSERT INTO landmark_preference(landmark_id,NotificationPreference,StartTime,EndTime,MobileNumber) values " +
							"("+Landmark_id+", '"+dayTimePreferenceJSON+"', '"+startTime+"', '"+endTime+"', '"+MobileNumber+"')";
					stmt.executeUpdate(insertQ);
				}
				
				
				//Insert details into notification preference table
				for(int i=0; i< ll4MachineList.size(); i++)
				{
					String updateNPQ = "update notification_preference set NotificationPreference='"+dayTimePreferenceJSON+"', UserID='"+loginID+"' "+
							"where AssetID='"+ll4MachineList.get(i)+"'  and AlertTypeID=5 and StartTime='"+startTime+"'";
					updateCount = stmt.executeUpdate(updateNPQ);
					if(updateCount==0)
					{
						String insertNPQuery ="INSERT INTO notification_preference(AssetID,AlertTypeID,NotificationPreference,StartTime," +
								"EndTime,UserID) VALUES ('"+ll4MachineList.get(i)+"', 5, '"+dayTimePreferenceJSON+"', '" +startTime+"', '"+endTime+"', '"+loginID+"')";
						stmt.executeUpdate(insertNPQuery);
					}
				}
				
				//Insert details to landmark_change_log
				String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				int partitionKey = Integer.valueOf(currentTime.substring(0,7).replace("-", ""));
				String InsertQ= null;
				InsertQ = "INSERT INTO landmark_change_log(landmark_id,UpdatedTime,UserID,Radius,NotificationPreference" +
						",StartTime,EndTime,Status,PartitionKey, Source) VALUES " +
						"("+Landmark_id+"," +	"'"+currentTime+"', '"+loginID+"', "+Radius+", '"+dayTimePreferenceJSON+"', '"+startTime+"'," +
						"'"+endTime+"', 1,"+partitionKey+", '"+Source+"' )";
				stmt.executeUpdate(InsertQ);
				
			}
			
			//------------------------------- STEP4: Insert details into landmark_preference table for Other time preferences
			if(OtherTimeNotification!=null)
			{
				String startTime = "20:01:00";
				String endTime = "07:59:00";
				
				HashMap<String,Object> otherTimePrefMap =new Gson().fromJson(OtherTimeNotification, HashMap.class);
				Map<String,String> otherTimePreference = new HashMap<String,String>();
				for(Map.Entry<String,Object> otherTimePref : otherTimePrefMap.entrySet())
				{
					if(otherTimePref.getValue()==null || otherTimePref.getValue().toString().equalsIgnoreCase("0"))
						continue;
					
					else
						otherTimePreference.put(otherTimePref.getKey(), MobileNumber);
				}
				JSONObject otherTimePreferenceJSON = new JSONObject(otherTimePreference);
				
				String updateQ ="update landmark_preference set NotificationPreference='"+otherTimePreferenceJSON+"'," +
						" StartTime='"+startTime+"', EndTime='"+endTime+"' , MobileNumber='"+MobileNumber+"' where landmark_id='"+Landmark_id+"' and  StartTime='"+startTime+"'";
				int updateCount = stmt.executeUpdate(updateQ);
				if(updateCount==0)
				{
					String insertQ = "INSERT INTO landmark_preference(landmark_id,NotificationPreference,StartTime,EndTime,MobileNumber) values " +
								"("+Landmark_id+", '"+otherTimePreferenceJSON+"', '"+startTime+"', '"+endTime+"', '"+MobileNumber+"')";
					stmt.executeUpdate(insertQ);
				}
				//Insert details into notification preference table
				for(int i=0; i< ll4MachineList.size(); i++)
				{
					String updateNPQ = "update notification_preference set NotificationPreference='"+otherTimePreferenceJSON+"', UserID='" +loginID+"' "+
							"where AssetID='"+ll4MachineList.get(i)+"'  and AlertTypeID=5 and StartTime='"+startTime+"'";
					updateCount = stmt.executeUpdate(updateNPQ);
					if(updateCount==0)
					{
						String insertNPQuery ="INSERT INTO notification_preference(AssetID,AlertTypeID,NotificationPreference,StartTime," +
								"EndTime,UserID) VALUES ('"+ll4MachineList.get(i)+"', 5, '"+otherTimePreferenceJSON+"', '" +startTime+"', '"+endTime+"', '"+loginID+"')";
						stmt.executeUpdate(insertNPQuery);
					}
				}
				
				//Insert details to landmark_change_log
				String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				int partitionKey = Integer.valueOf(currentTime.substring(0,7).replace("-", ""));
				String InsertQ= null;
				InsertQ = "INSERT INTO landmark_change_log(landmark_id,UpdatedTime,UserID,Radius,NotificationPreference" +
						",StartTime,EndTime,Status,PartitionKey,Source) VALUES " +
						"("+Landmark_id+"," +	"'"+currentTime+"', '"+loginID+"', "+Radius+", '"+otherTimePreferenceJSON+"', '"+startTime+"'," +
						"'"+endTime+"', 1,"+partitionKey+", '"+Source+"')";
				stmt.executeUpdate(InsertQ);
			}
			
			
		}
		
		catch(Exception e)
		{
			status="FAILURE";
			fLogger.fatal("GeofenceDetailsImpl:setLandmarkDetails:DAL:loginID:"+loginID+"; Exception:",e.getMessage(),e);
		}
		
		finally
		{
			try
			{
				if(stmt!=null)
					stmt.close();
				if(psInsert!=null)
					psInsert.close();
				if(psUpdate!=null)
					psUpdate.close();
				if(con!=null)
					con.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("GeofenceDetailsImpl:setLandmarkDetails:DAL:loginID:"+loginID+"; Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		return status;
	}
	
	//***************************************************************************************************************************
	public List<GeoFenceDetailsPOJO> getAllLandmarksForUser(String loginID, String accountCode) 
	{
		List<GeoFenceDetailsPOJO> responseList = new LinkedList<GeoFenceDetailsPOJO>();
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
					" c.tenancy_id=d.tenancy_id and d.account_id=e.account_id" +
					" and a.Activestatus=1 and a.GeoFence=1 and e.contact_id='"+loginID+"'";
			rs= stmt.executeQuery(landmarkQ);
			while(rs.next())
			{
				landmarkIDList.add(rs.getInt("landmarkID"));
			}
			
			
			for(int i=0; i<landmarkIDList.size(); i++)
			{
				GeoFenceDetailsPOJO responseObj = new GeoFenceDetailsPOJO();
				
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
						
						if(startTime.equalsIgnoreCase("08:00:00") && endTime.equalsIgnoreCase("20:00:00"))
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
							
							if(rs.getString("WhatsApp")!=null)
							{
								dayTimePreference.put("WhatsApp", "1");
								mobileNumber = rs.getString("WhatsApp").replaceAll("\"", "").replaceAll("\\\\", "");
							}
							else
								dayTimePreference.put("WhatsApp", "0");
							
							if(rs.getString("VoiceCall")!=null)
							{
								dayTimePreference.put("Voice Call", "1");
								mobileNumber = rs.getString("VoiceCall").replaceAll("\"", "").replaceAll("\\\\", "");
							}
							else
								dayTimePreference.put("Voice Call", "0");
							
							
							if(rs.getString("PushNotification")!=null)
							{
								dayTimePreference.put("Push notification", "1");
								mobileNumber = rs.getString("PushNotification").replaceAll("\"", "").replaceAll("\\\\", "");
							}
							else
								dayTimePreference.put("Push notification", "0");
							
							
							responseObj.setDayTimePreference(dayTimePreference);						
							//responseObj.setMobileNumber(mobileNumber);						
						}
						
						else
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
							/*if(mobileNumber!=null)
								responseObj.setMobileNumber(mobileNumber);*/
						}
					}
				}
				
				responseList.add(responseObj);
				
			}
		}
		
		catch(Exception e)
		{
			fLogger.fatal("GeoFenceDetailsImpl:getAllLandmarksForUser:DAL:LoginID:"+loginID+":Exception:",e.getMessage(),e);

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
				fLogger.fatal("GeoFenceDetailsImpl:getAllLandmarksForUser:DAL:LoginID:"+loginID+"; Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		return responseList;
	}
	
	
	//************************************************************************************************************************************
	public String setLandmarkDetailsNew(String loginID,String AccountCode,int Landmark_id,int Landmark_Category_ID, String Landmark_Name, 
			String Landmark_Category_Name, String Latitude, String Longitude, double Radius, String Address, String IsArrival,
			String IsDeparture, String MobileNumber, String NotificationDetails,String machineList, String Source)
	{
		String status="SUCCESS";
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		PreparedStatement psInsert = null;
		PreparedStatement psUpdate =null;
		
		try
		{
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			//Validate mandatory parameters
			if(loginID==null || Latitude==null || Longitude==null || Landmark_Name==null || Radius < 0.5 || IsArrival==null || IsDeparture==null)
			{
				fLogger.fatal("GeofenceDetailsImpl:setLandmarkDetailsNew:DAL:loginID:"+loginID+";Mandatory parameters is NULL");
				return "FAILURE";
			}
			
			//STEP1: Get the country code from loginID
			String countryCode="+91";
			
			
			//Validate :LandmarkID
			if(Landmark_id!=0)
			{
				int validLandmark=0;
				String landmarkQ ="select landmark_id from landmark where landmark_id='"+Landmark_id+"' and ActiveStatus=1";
				rs = stmt.executeQuery(landmarkQ);
				while(rs.next())
				{
					validLandmark=1;
				}
				
				if(validLandmark==0)
				{
					throw new Exception("Invalid LandmarkID :"+Landmark_id);
				}
			}
			
			//If Landmark Category is not passed for landmark creation, create a category and assign to the user account
			if(Landmark_Category_ID==0 && Landmark_id==0)
			{
				//Get the user tenancy
				int tenancyId=0;
				String tenancyQ="select c.tenancy_id as TenancyID from contact a, account_contact b, account_tenancy c" +
						" where a.contact_id=b.contact_id and b.account_id=c.account_id and a.contact_id='"+loginID+"'";
				rs = stmt.executeQuery(tenancyQ);
				while(rs.next())
				{
					tenancyId = rs.getInt("TenancyID");
				}
				
				if(tenancyId!=0)
				{
					//Check if landmark category exists
					String landmarkCatQ="select Landmark_Category_ID from landmark_catagory where Tenancy_ID="+tenancyId;
					rs = stmt.executeQuery(landmarkCatQ);
					while(rs.next())
					{
						Landmark_Category_ID = rs.getInt("Landmark_Category_ID");
					}
					
					//If landmark category is not defined for a account
					if(Landmark_Category_ID==0)
					{
						String landmarkCategoryName=Landmark_Name+"001";
						String insertQ="INSERT INTO landmark_catagory(Landmark_Category_Name,Color_Code,Tenancy_ID,ActiveStatus,Landmark_Category_Color_Code)" +
								" VALUES ('"+landmarkCategoryName+"', 001, "+tenancyId+", 1, 1)";
						stmt.execute(insertQ,Statement.RETURN_GENERATED_KEYS);
						rs = stmt.getGeneratedKeys();
						if(rs.next())
						{
							Landmark_Category_ID = rs.getInt(1);
						}
					}
				}
			}
			
			//------------ STEP1: Insert/Update the details to Landmark table
			if(Landmark_id==0)
			{
				String insertQ="INSERT INTO landmark(landmark_category_id,landmark_name,latitude,longitude,radius,Address," +
								"IsArrival,IsDeparture,ActiveStatus,Login_Id,country_code,CreatedTime,Source,Geofence) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				 psInsert = con.prepareStatement(insertQ,Statement.RETURN_GENERATED_KEYS);
				 psInsert.setInt(1, Landmark_Category_ID);
				 psInsert.setString(2, Landmark_Name);
				 psInsert.setString(3, Latitude);
				 psInsert.setString(4, Longitude);
				 psInsert.setDouble(5, Radius);
				 psInsert.setString(6, Address);
				 psInsert.setInt(7, Integer.valueOf(IsArrival));
				 psInsert.setInt(8, Integer.valueOf(IsDeparture));
				 psInsert.setInt(9, 1);
				 psInsert.setString(10, loginID);
				 psInsert.setString(11, countryCode);
				 psInsert.setTimestamp(12, new Timestamp(new Date().getTime()));
				 psInsert.setString(13, Source);
				 psInsert.setInt(14, 1);
				 
				 psInsert.executeUpdate();
				 
				 rs = psInsert.getGeneratedKeys();
				 if (rs.next()) 
				 {
					 Landmark_id = rs.getInt(1);
				 } 
			}
			else
			{
				String updateQ=null;
				
				if(Landmark_Category_ID!=0)
				{
					updateQ="update landmark set landmark_category_id=?, landmark_name=?, latitude=?, longitude=?,radius=?, Address=?," +
						"IsArrival=?,IsDeparture=?,ActiveStatus=?,Login_Id=?,country_code=?,LastUpdatedTime=?, Source=?, Geofence=? where landmark_id=?" ;
				
					psUpdate = con.prepareStatement(updateQ);
					psUpdate.setInt(1, Landmark_Category_ID);
					psUpdate.setString(2, Landmark_Name);
					psUpdate.setString(3, Latitude);
					psUpdate.setString(4, Longitude);
					psUpdate.setDouble(5, Radius);
					psUpdate.setString(6, Address);
					psUpdate.setInt(7, Integer.valueOf(IsArrival));
					psUpdate.setInt(8, Integer.valueOf(IsDeparture));
					psUpdate.setInt(9, 1);
					psUpdate.setString(10, loginID);
					psUpdate.setString(11, countryCode);
					psUpdate.setTimestamp(12, new Timestamp(new Date().getTime()));
					psUpdate.setString(13, Source);
					psUpdate.setInt(14, 1);
					psUpdate.setInt(15, Landmark_id);
				}
				
				else
				{
					updateQ="update landmark set landmark_name=?, latitude=?, longitude=?,radius=?, Address=?," +
							"IsArrival=?,IsDeparture=?,ActiveStatus=?,Login_Id=?,country_code=?,LastUpdatedTime=?, Source=?, Geofence=? where landmark_id=?" ;
					
						psUpdate = con.prepareStatement(updateQ);
						psUpdate.setString(1, Landmark_Name);
						psUpdate.setString(2, Latitude);
						psUpdate.setString(3, Longitude);
						psUpdate.setDouble(4, Radius);
						psUpdate.setString(5, Address);
						psUpdate.setInt(6, Integer.valueOf(IsArrival));
						psUpdate.setInt(7, Integer.valueOf(IsDeparture));
						psUpdate.setInt(8, 1);
						psUpdate.setString(9, loginID);
						psUpdate.setString(10, countryCode);
						psUpdate.setTimestamp(11, new Timestamp(new Date().getTime()));
						psUpdate.setString(12, Source);
						psUpdate.setInt(13, 1);
						psUpdate.setInt(14, Landmark_id);
				}
				int rowsUpdated = psUpdate.executeUpdate();
				
				if(rowsUpdated==0)
				{
					fLogger.fatal("GeofenceDetailsImpl:setLandmarkDetailsNew:DAL:loginID:"+loginID+"; No landmark exists with the given landmarkId:"+Landmark_id);
					return "FAILURE";
				}
			}
			
			
			//------------------------ STEP2: Update landmark_asset mapping
			String[] machineListArray =null;
			String VINList=null;
			List<String> updatedVinList = new LinkedList<String>();
			
			if(machineList!=null)
			{
				machineListArray = machineList.split(",");
				for(int i=0; i<machineListArray.length; i++)
				{
					if(VINList==null)
						VINList="'"+machineListArray[i]+"'";
					else
						VINList=VINList+", '"+machineListArray[i]+"'";
					
					updatedVinList.add(machineListArray[i]);
				}
			}
			//JCB6246.sn
			else if (machineList==null && Landmark_id!=0)
			{
				String landmarkAssetDeleteQ ="delete from landmark_asset where landmark_id="+Landmark_id;
				stmt.executeUpdate(landmarkAssetDeleteQ);
			}
			//JCB6246.en
			
			//----------------------------- Filter out the machines which is already assigned to some other Geofence - as there is a requirement 
			//that a machine has to be tagged to only one Geofence
			if(VINList!=null)
			{
				List<String> duplicateLandmarkMachines = new LinkedList<String>();
				String landmarkDuplicateQ="select a.serial_number from landmark_asset a, landmark b where a.landmark_id=b.landmark_id and b.Geofence=1 and " +
						" a.serial_number in ("+VINList+") and b.ActiveStatus=1 and b.landmark_id !="+Landmark_id;
				rs = stmt.executeQuery(landmarkDuplicateQ);
				while(rs.next())
				{
					duplicateLandmarkMachines.add(rs.getString("serial_number"));
				}
				if(duplicateLandmarkMachines.size()>0)
				{
					fLogger.fatal("GeofenceDetailsImpl:setLandmarkDetailsNew:DAL:loginID:"+loginID+";Machines already assigned to some other landmark:" +
							"Current Machine List:"+VINList+"; duplicateLandmarkMachines:"+duplicateLandmarkMachines);
					updatedVinList.removeAll(duplicateLandmarkMachines);
					//CR439.sn
					if(updatedVinList ==null || updatedVinList.size() == 0 ||updatedVinList.isEmpty() ){
						status = "FAILURE – Geofence Has Been Already Created For This Machine";
					}
					//CR439.en
				}
			}
			
			if(updatedVinList!=null && updatedVinList.size()>0)
			{
				if(Landmark_id!=0)
				{
					String landmarkAssetDeleteQ ="delete from landmark_asset where landmark_id="+Landmark_id;
					stmt.executeUpdate(landmarkAssetDeleteQ);
				}
				
				for(int i=0; i<updatedVinList.size();i++)
				{
					String landmarkAssetQ = "INSERT INTO landmark_asset(landmark_id,serial_number) values ("+Landmark_id+", '"+updatedVinList.get(i)+"')";
					stmt.addBatch(landmarkAssetQ);
				}
				stmt.executeBatch();
			
			}
			
			
			//-------------------------------- Filter out only LL4 machines for setting the notification preferences
			List<String> ll4MachineList = new LinkedList<String>();
			if(updatedVinList!=null && updatedVinList.size()>0)
			{
				String updatedVinListAsString = new ListToStringConversion().getStringList(updatedVinList).toString();
				String ll4machineQ = "select serial_number from asset_monitoring_snapshot where serial_number in ("+updatedVinListAsString+") " +
						" and  JSON_EXTRACT(TxnData,'$.FW_VER') is not null and ( CAST(SUBSTRING(JSON_EXTRACT(TxnData,'$.FW_VER'),2,2) AS UNSIGNED) >=30 and " +
						"  CAST(SUBSTRING(JSON_EXTRACT(TxnData,'$.FW_VER'),2,2) AS UNSIGNED) < 50)";
				ResultSet rs1 = stmt.executeQuery(ll4machineQ);
				while(rs1.next())
				{
					ll4MachineList.add(rs1.getString("serial_number"));
				}
				
			}
			
			
			//------------------------------- STEP3: Insert details into landmark_preference table for Day time preferences
			if(NotificationDetails!=null)
			{
				String startTime = "00:00:00";
				String endTime = "23:59:59";
				
				
				ObjectMapper mapper = new ObjectMapper();
				HashMap<String,Object> dayTimePrefMap =new Gson().fromJson(NotificationDetails, HashMap.class);
				Map<String,String> dayTimePreference = new HashMap<String,String>();
				for(Map.Entry<String,Object> dayTimePref : dayTimePrefMap.entrySet())
				{
					if(dayTimePref.getValue()==null || dayTimePref.getValue().toString().equalsIgnoreCase("0"))
						continue;
					
					else
						dayTimePreference.put(dayTimePref.getKey(), MobileNumber);
				}
				JSONObject dayTimePreferenceJSON = new JSONObject(dayTimePreference);
				
				
				String updateQ ="update landmark_preference set NotificationPreference='"+dayTimePreferenceJSON+"'," +
					" StartTime='"+startTime+"', EndTime='"+endTime+"', MobileNumber='"+MobileNumber+"' where landmark_id='"+Landmark_id+"' and  StartTime='"+startTime+"'";
				int updateCount = stmt.executeUpdate(updateQ);
				if(updateCount==0)
				{
					String insertQ = "INSERT INTO landmark_preference(landmark_id,NotificationPreference,StartTime,EndTime,MobileNumber) values " +
							"("+Landmark_id+", '"+dayTimePreferenceJSON+"', '"+startTime+"', '"+endTime+"', '"+MobileNumber+"')";
					stmt.executeUpdate(insertQ);
				}
				
				
				//Insert details into notification preference table
				for(int i=0; i< ll4MachineList.size(); i++)
				{
					String updateNPQ = "update notification_preference set NotificationPreference='"+dayTimePreferenceJSON+"', UserID='"+loginID+"' "+
							"where AssetID='"+ll4MachineList.get(i)+"'  and AlertTypeID=5 and StartTime='"+startTime+"'";
					updateCount = stmt.executeUpdate(updateNPQ);
					if(updateCount==0)
					{
						String insertNPQuery ="INSERT INTO notification_preference(AssetID,AlertTypeID,NotificationPreference,StartTime," +
								"EndTime,UserID) VALUES ('"+ll4MachineList.get(i)+"', 5, '"+dayTimePreferenceJSON+"', '" +startTime+"', '"+endTime+"', '"+loginID+"')";
						stmt.executeUpdate(insertNPQuery);
					}
				}
				
				//Insert details to landmark_change_log
				String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				int partitionKey = Integer.valueOf(currentTime.substring(0,7).replace("-", ""));
				String InsertQ= null;
				InsertQ = "INSERT INTO landmark_change_log(landmark_id,UpdatedTime,UserID,Radius,NotificationPreference" +
						",StartTime,EndTime,Status,PartitionKey, Source) VALUES " +
						"("+Landmark_id+"," +	"'"+currentTime+"', '"+loginID+"', "+Radius+", '"+dayTimePreferenceJSON+"', '"+startTime+"'," +
						"'"+endTime+"', 1,"+partitionKey+", '"+Source+"' )";
				stmt.executeUpdate(InsertQ);
				
			}
		}
		
		catch(Exception e)
		{
			status="FAILURE";
			fLogger.fatal("GeofenceDetailsImpl:setLandmarkDetailsNew:DAL:loginID:"+loginID+"; Exception:",e.getMessage(),e);
		}
		
		finally
		{
			try
			{
				if(stmt!=null)
					stmt.close();
				if(psInsert!=null)
					psInsert.close();
				if(psUpdate!=null)
					psUpdate.close();
				if(con!=null)
					con.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("GeofenceDetailsImpl:setLandmarkDetailsNew:DAL:loginID:"+loginID+"; Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		return status;
	}
	
	public String setLandmarkDetailsNew2(String loginID,String AccountCode,int Landmark_id,int Landmark_Category_ID, String Landmark_Name, 
			String Landmark_Category_Name, String Latitude, String Longitude, double Radius, String Address, String IsArrival,
			String IsDeparture, String MobileNumber, String NotificationDetails,String machineList, String Source)
	{
		String status="SUCCESS";
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		PreparedStatement psInsert = null;
		PreparedStatement psUpdate =null;
		
		try
		{
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			//Validate mandatory parameters
			if(loginID==null || Latitude==null || Longitude==null || Landmark_Name==null || Radius < 0.5 || IsArrival==null || IsDeparture==null)
			{
				fLogger.fatal("GeofenceDetailsImpl:setLandmarkDetailsNew:DAL:loginID:"+loginID+";Mandatory parameters is NULL");
				return "FAILURE";
			}
			
			//STEP1: Get the country code from loginID
			String countryCode="+91";
			
			
			//Validate :LandmarkID
			if(Landmark_id!=0)
			{
				int validLandmark=0;
				String landmarkQ ="select landmark_id from landmark where landmark_id='"+Landmark_id+"' and ActiveStatus=1";
				rs = stmt.executeQuery(landmarkQ);
				while(rs.next())
				{
					validLandmark=1;
				}
				
				if(validLandmark==0)
				{
					throw new Exception("Invalid LandmarkID :"+Landmark_id);
				}
			}
			
			//If Landmark Category is not passed for landmark creation, create a category and assign to the user account
			if(Landmark_Category_ID==0 && Landmark_id==0)
			{
				//Get the user tenancy
				int tenancyId=0;
				String tenancyQ="select c.tenancy_id as TenancyID from contact a, account_contact b, account_tenancy c" +
						" where a.contact_id=b.contact_id and b.account_id=c.account_id and a.contact_id='"+loginID+"'";
				rs = stmt.executeQuery(tenancyQ);
				while(rs.next())
				{
					tenancyId = rs.getInt("TenancyID");
				}
				
				if(tenancyId!=0)
				{
					//Check if landmark category exists
					String landmarkCatQ="select Landmark_Category_ID from landmark_catagory where Tenancy_ID="+tenancyId;
					rs = stmt.executeQuery(landmarkCatQ);
					while(rs.next())
					{
						Landmark_Category_ID = rs.getInt("Landmark_Category_ID");
					}
					
					//If landmark category is not defined for a account
					if(Landmark_Category_ID==0)
					{
						String landmarkCategoryName=Landmark_Name+"001";
						String insertQ="INSERT INTO landmark_catagory(Landmark_Category_Name,Color_Code,Tenancy_ID,ActiveStatus,Landmark_Category_Color_Code)" +
								" VALUES ('"+landmarkCategoryName+"', 001, "+tenancyId+", 1, 1)";
						stmt.execute(insertQ,Statement.RETURN_GENERATED_KEYS);
						rs = stmt.getGeneratedKeys();
						if(rs.next())
						{
							Landmark_Category_ID = rs.getInt(1);
						}
					}
				}
			}
			
			
			//------------------------ STEP1: checking vin has intery or not 		
			String VINList1=null;
			List<String> updatedVinList1 = new LinkedList<String>();
			
			if(machineList!=null)
			{
				String[] machineListArray = machineList.split(",");
				for(int i=0; i<machineListArray.length; i++)
				{
					if(VINList1==null)
						VINList1="'"+machineListArray[i]+"'";
					else
						VINList1=VINList1+", '"+machineListArray[i]+"'";
					
					updatedVinList1.add(machineListArray[i]);
				}
			}
			
			//----------------------------- Filter out the machines which is already assigned to some other Geofence - as there is a requirement 
			//that a machine has to be tagged to only one Geofence
			if(VINList1!=null)
			{
				List<String> duplicateLandmarkMachines = new LinkedList<String>();
				String landmarkDuplicateQ="select a.serial_number from landmark_asset a, landmark b where a.landmark_id=b.landmark_id and b.Geofence=1 and " +
						" a.serial_number in ("+VINList1+") and b.ActiveStatus=1 and b.landmark_id !="+Landmark_id;
				rs = stmt.executeQuery(landmarkDuplicateQ);
				while(rs.next())
				{
					duplicateLandmarkMachines.add(rs.getString("serial_number"));
				}
				if(duplicateLandmarkMachines.size()>0)
				{
					fLogger.fatal("GeofenceDetailsImpl:setLandmarkDetailsNew:DAL:loginID:"+loginID+";Machines already assigned to some other landmark:" +
							"Current Machine List:"+VINList1+"; duplicateLandmarkMachines:"+duplicateLandmarkMachines);
					updatedVinList1.removeAll(duplicateLandmarkMachines);
					//CR439.sn
					if(updatedVinList1 ==null || updatedVinList1.size() == 0 ||updatedVinList1.isEmpty() ){
						fLogger.fatal("FAILURE – Geofence Has Been Already Created For This Machine");
						return "FAILURE – Geofence Has Been Already Created For This Machine";
					}
					//CR439.en
				}
			}
			
			
			
			//------------ STEP1: Insert/Update the details to Landmark table
			if(Landmark_id==0)
			{
				String insertQ="INSERT INTO landmark(landmark_category_id,landmark_name,latitude,longitude,radius,Address," +
								"IsArrival,IsDeparture,ActiveStatus,Login_Id,country_code,CreatedTime,Source,Geofence) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				 psInsert = con.prepareStatement(insertQ,Statement.RETURN_GENERATED_KEYS);
				 psInsert.setInt(1, Landmark_Category_ID);
				 psInsert.setString(2, Landmark_Name);
				 psInsert.setString(3, Latitude);
				 psInsert.setString(4, Longitude);
				 psInsert.setDouble(5, Radius);
				 psInsert.setString(6, Address);
				 psInsert.setInt(7, Integer.valueOf(IsArrival));
				 psInsert.setInt(8, Integer.valueOf(IsDeparture));
				 psInsert.setInt(9, 1);
				 psInsert.setString(10, loginID);
				 psInsert.setString(11, countryCode);
				 psInsert.setTimestamp(12, new Timestamp(new Date().getTime()));
				 psInsert.setString(13, Source);
				 psInsert.setInt(14, 1);
				 
				 psInsert.executeUpdate();
				 
				 rs = psInsert.getGeneratedKeys();
				 if (rs.next()) 
				 {
					 Landmark_id = rs.getInt(1);
				 } 
			}
			else
			{
				String updateQ=null;
				
				if(Landmark_Category_ID!=0)
				{
					updateQ="update landmark set landmark_category_id=?, landmark_name=?, latitude=?, longitude=?,radius=?, Address=?," +
						"IsArrival=?,IsDeparture=?,ActiveStatus=?,Login_Id=?,country_code=?,LastUpdatedTime=?, Source=?, Geofence=? where landmark_id=?" ;
				
					psUpdate = con.prepareStatement(updateQ);
					psUpdate.setInt(1, Landmark_Category_ID);
					psUpdate.setString(2, Landmark_Name);
					psUpdate.setString(3, Latitude);
					psUpdate.setString(4, Longitude);
					psUpdate.setDouble(5, Radius);
					psUpdate.setString(6, Address);
					psUpdate.setInt(7, Integer.valueOf(IsArrival));
					psUpdate.setInt(8, Integer.valueOf(IsDeparture));
					psUpdate.setInt(9, 1);
					psUpdate.setString(10, loginID);
					psUpdate.setString(11, countryCode);
					psUpdate.setTimestamp(12, new Timestamp(new Date().getTime()));
					psUpdate.setString(13, Source);
					psUpdate.setInt(14, 1);
					psUpdate.setInt(15, Landmark_id);
				}
				
				else
				{
					updateQ="update landmark set landmark_name=?, latitude=?, longitude=?,radius=?, Address=?," +
							"IsArrival=?,IsDeparture=?,ActiveStatus=?,Login_Id=?,country_code=?,LastUpdatedTime=?, Source=?, Geofence=? where landmark_id=?" ;
					
						psUpdate = con.prepareStatement(updateQ);
						psUpdate.setString(1, Landmark_Name);
						psUpdate.setString(2, Latitude);
						psUpdate.setString(3, Longitude);
						psUpdate.setDouble(4, Radius);
						psUpdate.setString(5, Address);
						psUpdate.setInt(6, Integer.valueOf(IsArrival));
						psUpdate.setInt(7, Integer.valueOf(IsDeparture));
						psUpdate.setInt(8, 1);
						psUpdate.setString(9, loginID);
						psUpdate.setString(10, countryCode);
						psUpdate.setTimestamp(11, new Timestamp(new Date().getTime()));
						psUpdate.setString(12, Source);
						psUpdate.setInt(13, 1);
						psUpdate.setInt(14, Landmark_id);
				}
				int rowsUpdated = psUpdate.executeUpdate();
				
				if(rowsUpdated==0)
				{
					fLogger.fatal("GeofenceDetailsImpl:setLandmarkDetailsNew:DAL:loginID:"+loginID+"; No landmark exists with the given landmarkId:"+Landmark_id);
					return "FAILURE";
				}
			}
			
			
			//------------------------ STEP2: Update landmark_asset mapping
			String[] machineListArray =null;
			String VINList=null;
			List<String> updatedVinList = new LinkedList<String>();
			
			if(machineList!=null)
			{
				machineListArray = machineList.split(",");
				for(int i=0; i<machineListArray.length; i++)
				{
					if(VINList==null)
						VINList="'"+machineListArray[i]+"'";
					else
						VINList=VINList+", '"+machineListArray[i]+"'";
					
					updatedVinList.add(machineListArray[i]);
				}
			}
			//JCB6246.sn
			else if (machineList==null && Landmark_id!=0)
			{
				String landmarkAssetDeleteQ ="delete from landmark_asset where landmark_id="+Landmark_id;
				stmt.executeUpdate(landmarkAssetDeleteQ);
			}
			//JCB6246.en
			
			//----------------------------- Filter out the machines which is already assigned to some other Geofence - as there is a requirement 
			//that a machine has to be tagged to only one Geofence
			if(VINList!=null)
			{
				List<String> duplicateLandmarkMachines = new LinkedList<String>();
				String landmarkDuplicateQ="select a.serial_number from landmark_asset a, landmark b where a.landmark_id=b.landmark_id and b.Geofence=1 and " +
						" a.serial_number in ("+VINList+") and b.ActiveStatus=1 and b.landmark_id !="+Landmark_id;
				rs = stmt.executeQuery(landmarkDuplicateQ);
				while(rs.next())
				{
					duplicateLandmarkMachines.add(rs.getString("serial_number"));
				}
				if(duplicateLandmarkMachines.size()>0)
				{
					fLogger.fatal("GeofenceDetailsImpl:setLandmarkDetailsNew:DAL:loginID:"+loginID+";Machines already assigned to some other landmark:" +
							"Current Machine List:"+VINList+"; duplicateLandmarkMachines:"+duplicateLandmarkMachines);
					updatedVinList.removeAll(duplicateLandmarkMachines);
					//CR439.sn
					if(updatedVinList ==null || updatedVinList.size() == 0 ||updatedVinList.isEmpty() ){
						status = "FAILURE – Geofence Has Been Already Created For This Machine";
					}
					//CR439.en
				}
			}
			
			if(updatedVinList!=null && updatedVinList.size()>0)
			{
				if(Landmark_id!=0)
				{
					String landmarkAssetDeleteQ ="delete from landmark_asset where landmark_id="+Landmark_id;
					stmt.executeUpdate(landmarkAssetDeleteQ);
				}
				
				for(int i=0; i<updatedVinList.size();i++)
				{
					String landmarkAssetQ = "INSERT INTO landmark_asset(landmark_id,serial_number) values ("+Landmark_id+", '"+updatedVinList.get(i)+"')";
					stmt.addBatch(landmarkAssetQ);
				}
				stmt.executeBatch();
			
			}
			
			
			//-------------------------------- Filter out only LL4 machines for setting the notification preferences
			List<String> ll4MachineList = new LinkedList<String>();
			if(updatedVinList!=null && updatedVinList.size()>0)
			{
				String updatedVinListAsString = new ListToStringConversion().getStringList(updatedVinList).toString();
				String ll4machineQ = "select serial_number from asset_monitoring_snapshot where serial_number in ("+updatedVinListAsString+") " +
						" and  JSON_EXTRACT(TxnData,'$.FW_VER') is not null and ( CAST(SUBSTRING(JSON_EXTRACT(TxnData,'$.FW_VER'),2,2) AS UNSIGNED) >=30 and " +
						"  CAST(SUBSTRING(JSON_EXTRACT(TxnData,'$.FW_VER'),2,2) AS UNSIGNED) < 50)";
				ResultSet rs1 = stmt.executeQuery(ll4machineQ);
				while(rs1.next())
				{
					ll4MachineList.add(rs1.getString("serial_number"));
				}
				
			}
			
			
			//------------------------------- STEP3: Insert details into landmark_preference table for Day time preferences
			String startTime = "00:00:00";
			String endTime = "23:59:59";
			
			
			ObjectMapper mapper = new ObjectMapper();
			JSONObject dayTimePreferenceJSON = null;
			if(NotificationDetails!=null)
			{
			HashMap<String,Object> dayTimePrefMap =new Gson().fromJson(NotificationDetails, HashMap.class);
			Map<String,String> dayTimePreference = new HashMap<String,String>();
			for(Map.Entry<String,Object> dayTimePref : dayTimePrefMap.entrySet())
			{
				if(dayTimePref.getValue()==null || dayTimePref.getValue().toString().equalsIgnoreCase("0"))
					continue;
				
				else
					dayTimePreference.put(dayTimePref.getKey(), MobileNumber);
			}
			 dayTimePreferenceJSON = new JSONObject(dayTimePreference);
			}
			
			String updateQ ="update landmark_preference set NotificationPreference='"+dayTimePreferenceJSON+"'," +
				" StartTime='"+startTime+"', EndTime='"+endTime+"', MobileNumber='"+MobileNumber+"' where landmark_id='"+Landmark_id+"' and  StartTime='"+startTime+"'";
			int updateCount = stmt.executeUpdate(updateQ);
			if(updateCount==0)
			{
				String insertQ = "INSERT INTO landmark_preference(landmark_id,NotificationPreference,StartTime,EndTime,MobileNumber) values " +
						"("+Landmark_id+", '"+dayTimePreferenceJSON+"', '"+startTime+"', '"+endTime+"', '"+MobileNumber+"')";
				stmt.executeUpdate(insertQ);
			}
			
			if(NotificationDetails!=null)
			{
			
				
				//Insert details into notification preference table
				for(int i=0; i< ll4MachineList.size(); i++)
				{
					String updateNPQ = "update notification_preference set NotificationPreference='"+dayTimePreferenceJSON+"', UserID='"+loginID+"' "+
							"where AssetID='"+ll4MachineList.get(i)+"'  and AlertTypeID=5 and StartTime='"+startTime+"'";
					updateCount = stmt.executeUpdate(updateNPQ);
					if(updateCount==0)
					{
						String insertNPQuery ="INSERT INTO notification_preference(AssetID,AlertTypeID,NotificationPreference,StartTime," +
								"EndTime,UserID) VALUES ('"+ll4MachineList.get(i)+"', 5, '"+dayTimePreferenceJSON+"', '" +startTime+"', '"+endTime+"', '"+loginID+"')";
						stmt.executeUpdate(insertNPQuery);
					}
				}
				
				//Insert details to landmark_change_log
				String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				int partitionKey = Integer.valueOf(currentTime.substring(0,7).replace("-", ""));
				String InsertQ= null;
				InsertQ = "INSERT INTO landmark_change_log(landmark_id,UpdatedTime,UserID,Radius,NotificationPreference" +
						",StartTime,EndTime,Status,PartitionKey, Source) VALUES " +
						"("+Landmark_id+"," +	"'"+currentTime+"', '"+loginID+"', "+Radius+", '"+dayTimePreferenceJSON+"', '"+startTime+"'," +
						"'"+endTime+"', 1,"+partitionKey+", '"+Source+"' )";
				stmt.executeUpdate(InsertQ);
				
			}
		}
		
		catch(Exception e)
		{
			status="FAILURE";
			fLogger.fatal("GeofenceDetailsImpl:setLandmarkDetailsNew:DAL:loginID:"+loginID+"; Exception:",e.getMessage(),e);
		}
		
		finally
		{
			try
			{
				if(stmt!=null)
					stmt.close();
				if(psInsert!=null)
					psInsert.close();
				if(psUpdate!=null)
					psUpdate.close();
				if(con!=null)
					con.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("GeofenceDetailsImpl:setLandmarkDetailsNew:DAL:loginID:"+loginID+"; Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		return status;
	}
	
	//************************************************************************************************************************************
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
					" c.tenancy_id=d.tenancy_id and d.account_id=e.account_id" +
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
							
							if(rs.getString("WhatsApp")!=null)
							{
								dayTimePreference.put("WhatsApp", "1");
								mobileNumber = rs.getString("WhatsApp").replaceAll("\"", "").replaceAll("\\\\", "");
							}
							else
								dayTimePreference.put("WhatsApp", "0");
							
							if(rs.getString("VoiceCall")!=null)
							{
								dayTimePreference.put("Voice Call", "1");
								mobileNumber = rs.getString("VoiceCall").replaceAll("\"", "").replaceAll("\\\\", "");
							}
							else
								dayTimePreference.put("Voice Call", "0");
							
							
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
				
				responseList.add(responseObj);
				
			}
		}
		
		catch(Exception e)
		{
			fLogger.fatal("GeoFenceDetailsImpl:getAllLandmarksForUserNew:DAL:LoginID:"+loginID+":Exception:",e.getMessage(),e);

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
				fLogger.fatal("GeoFenceDetailsImpl:getAllLandmarksForUserNew:DAL:LoginID:"+loginID+"; Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		return responseList;
	}
	
	//********************************************************************************************************************************
	public GeoFenceDetailsPOJONew getLandmarkNotificationDetailsNew(String loginID, String accountCode, String landmark_id) {
		
		GeoFenceDetailsPOJONew responseObj = new GeoFenceDetailsPOJONew();
        Logger fLogger = FatalLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		
		try{		
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			//Get the mobile number
			String landmarkPreferenceQ="Select MobileNumber from landmark_preference where landmark_id="+landmark_id+" and MobileNumber is not null";
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
			
			//Get Landmark preference for GeoFence alert
			String landmarkPrefQ = "select JSON_EXTRACT(NotificationPreference,'$.SMS') as SMS," +
					" JSON_EXTRACT(NotificationPreference,'$.WhatsApp') as WhatsApp, " +
					" JSON_EXTRACT(NotificationPreference,'$.\"Voice Call\"') as VoiceCall ," +
					" JSON_EXTRACT(NotificationPreference,'$.\"Push notification\"') as PushNotification," +
					" StartTime, EndTime " +
					" from landmark_preference where landmark_id='"+landmark_id+"'";
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
						
						if(rs.getString("WhatsApp")!=null)
						{
							dayTimePreference.put("WhatsApp", "1");
							mobileNumber = rs.getString("WhatsApp").replaceAll("\"", "").replaceAll("\\\\", "");
						}
						else
							dayTimePreference.put("WhatsApp", "0");
						
						if(rs.getString("VoiceCall")!=null)
						{
							dayTimePreference.put("Voice Call", "1");
							mobileNumber = rs.getString("VoiceCall").replaceAll("\"", "").replaceAll("\\\\", "");
						}
						else
							dayTimePreference.put("Voice Call", "0");
						
						
						if(rs.getString("PushNotification")!=null)
						{
							dayTimePreference.put("Push notification", "1");
							mobileNumber = rs.getString("PushNotification").replaceAll("\"", "").replaceAll("\\\\", "");
						}
						else
							dayTimePreference.put("Push notification", "0");
						
						
						responseObj.setNotificationDetails(dayTimePreference);
						responseObj.setLandmarkID(landmark_id);
					}
					
					
				}
			}		
		}catch(Exception e){
			fLogger.fatal("GeoFenceDetailsImpl:getLandmarkNotificationDetailsNew:DAL:landmark_id:"+landmark_id+":Exception:",e.getMessage(),e);

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
				fLogger.fatal("GeoFenceDetailsImpl:getLandmarkNotificationDetailsNew:DAL:landmark_id:"+landmark_id+"; Exception in closing the connection:"+e.getMessage(),e);
			}
			
		}
		
		return responseObj;
	}
	
	//***************************************************************************************************************************

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
						
						if(rs.getString("WhatsApp")!=null)
						{
							dayTimePreference.put("WhatsApp", "1");
							mobileNumber = rs.getString("WhatsApp").replaceAll("\"", "").replaceAll("\\\\", "");
						}
						else
							dayTimePreference.put("WhatsApp", "0");
						
						if(rs.getString("VoiceCall")!=null)
						{
							dayTimePreference.put("Voice Call", "1");
							mobileNumber = rs.getString("VoiceCall").replaceAll("\"", "").replaceAll("\\\\", "");
						}
						else
							dayTimePreference.put("Voice Call", "0");
						
						
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
			fLogger.fatal("GeoFenceDetailsImpl:getLandmarkDetailsForVINNew:DAL:vIN:"+vIN+":Exception:",e.getMessage(),e);

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
				fLogger.fatal("GeoFenceDetailsImpl:getLandmarkDetailsForVINNew:DAL:vIN:"+vIN+"; Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		return responseObj;
	}

}
