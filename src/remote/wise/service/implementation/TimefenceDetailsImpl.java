package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;

import com.google.gson.Gson;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.TimeFenceDetailsPOJO;
import remote.wise.pojo.TimeFenceDetailsPOJONew;
import remote.wise.util.ConnectMySQL;

public class TimefenceDetailsImpl 
{
	public TimeFenceDetailsPOJO getTimefenceDetails(String loginID, String accountCode,String assetID)
	{
		TimeFenceDetailsPOJO responseObj = new TimeFenceDetailsPOJO();
		Logger fLogger = FatalLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		
		try
		{
			
			//Get the Operating start time and end time of the machine
			String query ="select a.operatingStartTime, a.operatingEndTime, c.Account_Code as AccountCode" +
					" from asset_profile a, asset_owner_snapshot b, account c" +
					" where a.serialnumber=b.serial_Number and b.account_id=c.account_id and b.account_type='Customer' and " +
					" a.serialNumber='"+assetID+"'";
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			rs = stmt.executeQuery(query);
			
			while(rs.next())
            {
				responseObj.setVIN(assetID);
				responseObj.setCustomerCode(rs.getString("AccountCode"));
				if(rs.getTime("operatingStartTime")!=null)
				{
					responseObj.setOperatingStartTime(String.valueOf(rs.getTime("operatingStartTime")));
				}
				if(rs.getTime("operatingEndTime")!=null)
				{
					responseObj.setOperatingEndtime(String.valueOf(rs.getTime("operatingEndTime")));
				}
            }
			
			//Get time fence settings for the given VIN
			String timeFenceDetailsQuery = "select NotificationPattern, NotificationDate, RecurrencePattern, RecurrenceRange, LastUpdatedTime,MobileNumber from timefence_settings" +
					" where AssetID='"+assetID+"'";
			rs = stmt.executeQuery(timeFenceDetailsQuery);
			while(rs.next())
			{
				HashMap<String,Object> TimefenceDetails = new HashMap<String,Object>();
				TimefenceDetails.put("NotificationPattern", rs.getString("NotificationPattern"));
				if(rs.getDate("NotificationDate")!=null)
				{
					String notificationDate = new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("NotificationDate"));
					TimefenceDetails.put("NotificationDate", notificationDate);
				}
				
				if(rs.getString("NotificationPattern")!=null && rs.getString("NotificationPattern").equalsIgnoreCase("Recurrence"))
				{
					ObjectMapper mapper = new ObjectMapper();
					HashMap<String,Object> recPatternMap =new Gson().fromJson(rs.getObject("RecurrencePattern").toString(), HashMap.class);
					TimefenceDetails.put("RecurrencePattern",recPatternMap);
					
					//TimefenceDetails.put("RecurrencePattern", rs.getObject("RecurrencePattern"));
					
					HashMap<String,Object> recRangeMap =new Gson().fromJson(rs.getObject("RecurrenceRange").toString(), HashMap.class);
					TimefenceDetails.put("RecurrenceRange",recRangeMap);
					//TimefenceDetails.put("RecurrenceRange",rs.getObject("RecurrenceRange"));
				}
				responseObj.setTimefenceDetails(TimefenceDetails);
				
				if(rs.getDate("LastUpdatedTime")!=null)
				{
					String createdDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getDate("LastUpdatedTime"));
					responseObj.setCreatedDate(createdDate);
				}
				
				if(rs.getString("MobileNumber")!=null)
				{
					responseObj.setMobileNumber(rs.getString("MobileNumber"));
				}
				
			}
			
			
			//Get notification preference for Timefence alert
			String notificationPrefQ = "select JSON_EXTRACT(NotificationPreference,'$.SMS') as SMS," +
					" JSON_EXTRACT(NotificationPreference,'$.WhatsApp') as WhatsApp, " +
					" JSON_EXTRACT(NotificationPreference,'$.\"Voice Call\"') as VoiceCall ," +
					" JSON_EXTRACT(NotificationPreference,'$.\"Push notification\"') as PushNotification," +
					" StartTime, EndTime " +
					" from notification_preference where AlertTypeID=3 and AssetID='"+assetID+"'";
			rs = stmt.executeQuery(notificationPrefQ);
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
							mobileNumber =  rs.getString("VoiceCall").replaceAll("\"", "").replaceAll("\\\\", "");
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
							mobileNumber =  rs.getString("WhatsApp").replaceAll("\"", "").replaceAll("\\\\", "");
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
						//responseObj.setMobileNumber(mobileNumber);
					}
				}
			}
		}
		
		catch(Exception e)
		{
			fLogger.fatal("TimefenceDetailsImpl:getTimefenceDetails:DAL:loginID:"+loginID+"; assetID:"+assetID+"; Exception:",e.getMessage(),e);
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
				fLogger.fatal("TimefenceDetailsImpl:getTimefenceDetails:DAL:loginID:"+loginID+"; assetID:"+assetID+"; Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		
		return responseObj;
	}
	
	//****************************************************************************************************************************************
	public String deleteTimefence(String loginID, String accountCode,String assetID, String Source)
	{
		String status="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		
		try
		{
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			//STEP1: Nullify operating start time and end time in asset_profile table
			String updateQ1= "update asset_profile set operatingStartTime=null, operatingEndTime=null where serialnumber='"+assetID+"'";
			stmt.executeUpdate(updateQ1);
			
			//STEP2: Delete the entry for the VIN in timefence_settings table
			String updateQ2 = "delete from timefence_settings where AssetID='"+assetID+"'";
			stmt.executeUpdate(updateQ2);
			
			//STEP3: Delete the entry for the VIN in notification_preference table
			String updateQ3 = "delete from notification_preference where AssetID='"+assetID+"' and AlertTypeID=3";
			stmt.executeUpdate(updateQ3);
			
			//STEP4: Insert details to Timefence_change_log table
			String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			int partitionKey = Integer.valueOf(currentTime.substring(0,7).replace("-", ""));
			String InsertQ= "INSERT INTO Timefence_change_log(AssetID,UpdatedTime,UserID,Status,PartitionKey,Source) VALUES ('"+assetID+"'," +
					"'"+currentTime+"', '"+loginID+"', 0,"+partitionKey+", '"+Source+"' )";
			
			stmt.executeUpdate(InsertQ);
		}
		
		catch(Exception e)
		{
			status="FAILURE";
			fLogger.fatal("TimefenceDetailsImpl:deleteTimefence:DAL:loginID:"+loginID+":assetID:"+assetID+"; Exception:",e.getMessage(),e);
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
				fLogger.fatal("TimefenceDetailsImpl:deleteTimefence:DAL:loginID:"+loginID+"; assetID:"+assetID+"; Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		
		return status;
	}
	
	
	//************************************************************************************************************************************
	public String setTimefenceDetails(String loginID, String accountCode,String assetID, String operatingStartTime, String operatingEndtime,
										String NotificationPattern, String NotificationDate, String RecurrencePattern, String RecurrenceRange,
										String MobileNumber, String DayTimeNotification, String OtherTimeNotification, String Source)
	{
		String status="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		
		try
		{
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			//Validate mandatory parameters
			if(assetID==null || operatingStartTime==null || operatingEndtime==null )
			{
				fLogger.fatal("TimefenceDetailsImpl:setTimefenceDetails:DAL:loginID:"+loginID+";Mandatory parameter is NULL");
				return "FAILURE";
			}
			
			//STEP1: Validate operating start time and end time
			if(	operatingStartTime==null ||	
					(  !( ( operatingStartTime.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) 
							|| ( operatingStartTime.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$"))
						)
					)
			   )
			{
				fLogger.fatal("TimefenceDetailsImpl:setTimefenceDetails:DAL:loginID:"+loginID+"; assetID:"+assetID+"; Invalid format of Operating Start Time");
				return "FAILURE";
			} 
			
			if(	operatingEndtime==null ||	
					(  !( ( operatingEndtime.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) 
							|| ( operatingEndtime.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$"))
						)
					)
			   )
			{
				fLogger.fatal("TimefenceDetailsImpl:setTimefenceDetails:DAL:loginID:"+loginID+"; assetID:"+assetID+"; Invalid format of Operating End Time");
				return "FAILURE";
			} 
			
			
				//STEP2: Update Operating start time and Operating end time in asset_profile table
			String updateQ = "update asset_profile set operatingStartTime='"+operatingStartTime+"', operatingEndTime='"+operatingEndtime+"'," +
					" TimeFence=1,Source='"+Source+"' where" +
					" serialnumber='"+assetID+"'";
			stmt.executeUpdate(updateQ);
			
			
			if(NotificationPattern==null) //FOR LL2 machines
			{
				iLogger.info("TimefenceDetailsImpl:setTimefenceDetails:DAL:loginID:"+loginID+"; AssetID:"+assetID+"; NotificationPattern is NULL " +
						"and hence updating only asset_profile table");
				return "SUCCESS";
			}
			
			//Validate whether the received machine is LL4 or not - As this functionality is applicable only for LL4 machines
			int ll4Machine=0;
			String ll4machineQ = "select serial_number from asset_monitoring_snapshot where serial_number='"+assetID+"' " +
					" and JSON_EXTRACT(TxnData,'$.FW_VER') is not null and ( CAST(SUBSTRING(JSON_EXTRACT(TxnData,'$.FW_VER'),2,2) AS UNSIGNED) >=30 and " +
					"  CAST(SUBSTRING(JSON_EXTRACT(TxnData,'$.FW_VER'),2,2) AS UNSIGNED) < 50)";
			ResultSet rs1 = stmt.executeQuery(ll4machineQ);
			while(rs1.next())
			{
				ll4Machine=1;
			}
			if(ll4Machine==0)
			{
				iLogger.info("TimefenceDetailsImpl:setTimefenceDetails:DAL:loginID:"+loginID+"; AssetID:"+assetID+"; Input VIN is not LL4 machine " +
						"and hence updating only asset_profile table");
				return "SUCCESS";
			}
			
			//STEP3: Insert timefence details to timefence_settings table
			String timefenceSettingsUpdateQ = null;
			String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			
			if(NotificationPattern.equalsIgnoreCase("OneTime"))
			{
				timefenceSettingsUpdateQ = "update timefence_settings set NotificationPattern='"+NotificationPattern+"'," +
						" NotificationDate='"+NotificationDate+"', RecurrencePattern=NULL,  RecurrenceRange=NULL, LastUpdatedTime='"+currentDate+"' "+
						" , MobileNumber='"+MobileNumber+"' where AssetID='"+assetID+"'";
			}
			else if(NotificationPattern.equalsIgnoreCase("Recurrence"))
			{
				timefenceSettingsUpdateQ = "update timefence_settings set NotificationPattern='"+NotificationPattern+"', NotificationDate=NULL, " +
						" RecurrencePattern='"+RecurrencePattern+"', RecurrenceRange='"+RecurrenceRange+"', LastUpdatedTime='"+currentDate+"' "+
						" , MobileNumber='"+MobileNumber+"' where AssetID='"+assetID+"'";
			}
			
			int updateCount = stmt.executeUpdate(timefenceSettingsUpdateQ);
			
			if(updateCount==0)
			{
				String insertQ = null;
				if(NotificationPattern.equalsIgnoreCase("OneTime"))
				{
					insertQ = "INSERT INTO timefence_settings(AssetID,NotificationPattern,NotificationDate,RecurrencePattern,RecurrenceRange," +
							"LastUpdatedTime,MobileNumber) VALUES " +
						" ('"+assetID+"', '"+NotificationPattern+"', '"+NotificationDate+"', NULL, NULL,'"+currentDate+"','"+MobileNumber+"')";
				}
				else if(NotificationPattern.equalsIgnoreCase("Recurrence"))
				{
					insertQ = "INSERT INTO timefence_settings(AssetID,NotificationPattern,NotificationDate,RecurrencePattern,RecurrenceRange," +
							"LastUpdatedTime,MobileNumber) VALUES " +
							" ('"+assetID+"', '"+NotificationPattern+"', NULL, '"+RecurrencePattern+"', '"+RecurrenceRange+"','"+currentDate+"'," +
									"'"+MobileNumber+"')";
				}
				stmt.executeUpdate(insertQ);
			}
			
			
			//STEP4: Insert details to notification preference table for day time preference
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
			
			String startTime = "08:00:00";
			String endTime = "20:00:00";
			
			HashMap<String,Object> recurrenceRangeMap = new HashMap<String,Object>();
			if(RecurrenceRange!=null)
				recurrenceRangeMap =new Gson().fromJson(RecurrenceRange, HashMap.class);
			String startDate=null, endDate=null;
			
			
			if(NotificationPattern.equalsIgnoreCase("OneTime"))
			{
				startDate=NotificationDate;
				endDate= NotificationDate;
			}
			else if(NotificationPattern.equalsIgnoreCase("Recurrence"))
			{
				if(recurrenceRangeMap.get("StartDate")!=null)
					startDate = recurrenceRangeMap.get("StartDate").toString();
				if(recurrenceRangeMap.containsKey("EndDate") && recurrenceRangeMap.get("EndDate")!=null && !(recurrenceRangeMap.get("EndDate").toString().equalsIgnoreCase("NULL")))
					endDate = recurrenceRangeMap.get("EndDate").toString();
			}
			
			if(endDate==null || endDate.equalsIgnoreCase("NULL") || endDate.trim().length()==0)
			{
				endDate=null;
			}
			else
			{
				endDate="'"+endDate+"'";
			}
			
			
			HashMap<String,Object> recurrencePattern = new HashMap<String,Object>();
			if(RecurrencePattern!=null)
				recurrencePattern =new Gson().fromJson(RecurrencePattern, HashMap.class);
			String DayOfTheWeek=null;
			if(recurrencePattern.containsKey("Daily"))
			{
				DayOfTheWeek="Mon,Tue,Wed,Thu,Fri,Sat,Sun";
			}
			else if (recurrencePattern.containsKey("Weekly"))
			{
				String[] daysList =(recurrencePattern.get("Weekly")).toString().split(",");
				for(int i=0; i< daysList.length; i++)
				{
					if(DayOfTheWeek==null)
						DayOfTheWeek=daysList[i].toString().substring(0, 3);
					else
						DayOfTheWeek=DayOfTheWeek+","+daysList[i].toString().substring(0, 3);
				}
			}
			
			String updateQuery = null;
			if(NotificationPattern.equalsIgnoreCase("OneTime"))
			{
				updateQuery = "update notification_preference set NotificationPreference='"+dayTimePreferenceJSON+"'," +
					" StartTime='"+startTime+"', EndTime='"+endTime+"', StartDate='"+startDate+"', EndDate="+endDate+", DayOfTheWeek=NULL, UserID='"+loginID+"'"+
					" where AssetID='"+assetID+"' and AlertTypeID=3 and StartTime='08:00:00'";
			}
			else if(NotificationPattern.equalsIgnoreCase("Recurrence"))
			{
				updateQuery = "update notification_preference set NotificationPreference='"+dayTimePreferenceJSON+"'," +
						" StartTime='"+startTime+"', EndTime='"+endTime+"', StartDate='"+startDate+"', EndDate="+endDate+", DayOfTheWeek='" +DayOfTheWeek+"' , UserID='"+loginID+"'"+
						" where AssetID='"+assetID+"' and AlertTypeID=3 and StartTime='08:00:00'";
			}
			
			int updateNPCount = stmt.executeUpdate(updateQuery);
			
			if(updateNPCount==0)
			{
				String insertQ = null;
				if(NotificationPattern.equalsIgnoreCase("OneTime"))
				{
					insertQ = "INSERT INTO notification_preference(AssetID,AlertTypeID,NotificationPreference,StartTime,EndTime," +
						"StartDate,EndDate,DayOfTheWeek,UserID) VALUES " +
						" ('"+assetID+"', 3, '"+dayTimePreferenceJSON+"', '"+startTime+"', '"+endTime+"', '"+startDate+"', "+endDate+", NULL"+",'"+loginID+"'"+
								")";
				}
				else if(NotificationPattern.equalsIgnoreCase("Recurrence"))
				{
					insertQ = "INSERT INTO notification_preference(AssetID,AlertTypeID,NotificationPreference,StartTime,EndTime," +
							"StartDate,EndDate,DayOfTheWeek,UserID) VALUES " +
							" ('"+assetID+"', 3, '"+dayTimePreferenceJSON+"', '"+startTime+"', '"+endTime+"', '"+startDate+"', "+endDate+", '"+DayOfTheWeek+"' ,'"+loginID+"'"+
									")";
				}
				stmt.executeUpdate(insertQ);
			}
			
				
			//STEP5: Insert details to notification preference table for other time preferences
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
			
			startTime = "20:01:00";
			endTime = "07:59:00";
			
			String updateOthertimePrefQuery = null;
			if(NotificationPattern.equalsIgnoreCase("OneTime"))
			{
				updateOthertimePrefQuery = "update notification_preference set NotificationPreference='"+otherTimePreferenceJSON+"'," +
					" StartTime='"+startTime+"', EndTime='"+endTime+"', StartDate='"+startDate+"', EndDate="+endDate+" , DayOfTheWeek=NULL , UserID='"+loginID+"'"+
					" where AssetID='"+assetID+"' and AlertTypeID=3 and StartTime='20:01:00'";
			}
			else if(NotificationPattern.equalsIgnoreCase("Recurrence"))
			{
				updateOthertimePrefQuery = "update notification_preference set NotificationPreference='"+otherTimePreferenceJSON+"'," +
						" StartTime='"+startTime+"', EndTime='"+endTime+"', StartDate='"+startDate+"', EndDate="+endDate+", DayOfTheWeek='" +DayOfTheWeek+"', UserID='"+loginID+"'"+
						" where AssetID='"+assetID+"' and AlertTypeID=3 and StartTime='20:01:00'";
			}
			int updateNPOtherTimeCount = stmt.executeUpdate(updateOthertimePrefQuery);
			
			if(updateNPOtherTimeCount==0)
			{
				String insertQ = null;
				if(NotificationPattern.equalsIgnoreCase("OneTime"))
				{
					insertQ = "INSERT INTO notification_preference(AssetID,AlertTypeID,NotificationPreference,StartTime,EndTime," +
						"StartDate,EndDate,DayOfTheWeek,UserID) VALUES " +
						" ('"+assetID+"', 3, '"+otherTimePreferenceJSON+"', '"+startTime+"', '"+endTime+"', '"+startDate+"', "+endDate+",NULL"+",'"+loginID+"'"+
								")";
				}
				else if(NotificationPattern.equalsIgnoreCase("Recurrence"))
				{
					insertQ = "INSERT INTO notification_preference(AssetID,AlertTypeID,NotificationPreference,StartTime,EndTime," +
							"StartDate,EndDate,DayOfTheWeek,UserID) VALUES " +
							" ('"+assetID+"', 3, '"+otherTimePreferenceJSON+"', '"+startTime+"', '"+endTime+"', '"+startDate+"', "+endDate+", '"+DayOfTheWeek+"' ,'"+loginID+"'"+
									")";
				}
				stmt.executeUpdate(insertQ);
			}
			
			
			//STEP6: Insert details to Timefence change log
			String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			int partitionKey = Integer.valueOf(currentTime.substring(0,7).replace("-", ""));
			String InsertQ= null;
			if(NotificationPattern.equalsIgnoreCase("OneTime"))
			{
				InsertQ = "INSERT INTO Timefence_change_log(AssetID,UpdatedTime,UserID,OperatingStartTime,OperatingEndTime,NotificationPattern," +
					"NotificationDate,RecurrencePattern,RecurrenceRange,NotificationPreference,StartTime,EndTime,Status,PartitionKey,Source) VALUES " +
					"('"+assetID+"'," +	"'"+currentTime+"', '"+loginID+"', '"+operatingStartTime+"', '"+operatingEndtime+"'," +
					"'"+NotificationPattern+"', '"+NotificationDate+"', NULL, NULL, '"+dayTimePreferenceJSON+"'," +
					"'08:00:00', '20:00:00', 1,"+partitionKey+", '"+Source+"')";
			}
			else if(NotificationPattern.equalsIgnoreCase("Recurrence"))
			{
				InsertQ = "INSERT INTO Timefence_change_log(AssetID,UpdatedTime,UserID,OperatingStartTime,OperatingEndTime,NotificationPattern," +
						"NotificationDate,RecurrencePattern,RecurrenceRange,NotificationPreference,StartTime,EndTime,Status,PartitionKey,Source) VALUES " +
						"('"+assetID+"'," +	"'"+currentTime+"', '"+loginID+"', '"+operatingStartTime+"', '"+operatingEndtime+"'," +
						"'"+NotificationPattern+"', NULL, '"+RecurrencePattern+"', '"+RecurrenceRange+"', '"+dayTimePreferenceJSON+"'," +
						"'08:00:00', '20:00:00', 1,"+partitionKey+", '"+Source+"')";
			}
			
			stmt.executeUpdate(InsertQ);
			
			String InsertQ1= null;
			if(NotificationPattern.equalsIgnoreCase("OneTime"))
			{
				InsertQ1 = "INSERT INTO Timefence_change_log(AssetID,UpdatedTime,UserID,OperatingStartTime,OperatingEndTime,NotificationPattern," +
					"NotificationDate,RecurrencePattern,RecurrenceRange,NotificationPreference,StartTime,EndTime,Status,PartitionKey,Source) VALUES " +
					"('"+assetID+"'," +	"'"+currentTime+"', '"+loginID+"', '"+operatingStartTime+"', '"+operatingEndtime+"'," +
					"'"+NotificationPattern+"', '"+NotificationDate+"', NULL, NULL, '"+otherTimePreferenceJSON+"'," +
					"'20:01:00', '07:59:00', 1,"+partitionKey+", '"+Source+"')";
			}
			else if(NotificationPattern.equalsIgnoreCase("Recurrence"))
			{
				InsertQ1 = "INSERT INTO Timefence_change_log(AssetID,UpdatedTime,UserID,OperatingStartTime,OperatingEndTime,NotificationPattern," +
						"NotificationDate, RecurrencePattern,RecurrenceRange,NotificationPreference,StartTime,EndTime,Status,PartitionKey,Source) VALUES " +
						"('"+assetID+"'," +	"'"+currentTime+"', '"+loginID+"', '"+operatingStartTime+"', '"+operatingEndtime+"'," +
						"'"+NotificationPattern+"', NULL, '"+RecurrencePattern+"', '"+RecurrenceRange+"', '"+otherTimePreferenceJSON+"'," +
						"'20:01:00', '07:59:00', 1,"+partitionKey+", '"+Source+"')";
			}
			
			stmt.executeUpdate(InsertQ1);
			
		}
		
		catch(Exception e)
		{
			status="FAILURE";
			fLogger.fatal("TimefenceDetailsImpl:setTimefenceDetails:DAL:loginID:"+loginID+":assetID:"+assetID+"; Exception:",e.getMessage(),e);
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
				fLogger.fatal("TimefenceDetailsImpl:setTimefenceDetails:DAL:loginID:"+loginID+"; assetID:"+assetID+"; Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		
		return status;
	}
	
	
	//************************************************************************************************************************************
	public String setTimefenceDetailsNew(String loginID, String accountCode,String assetID, String operatingStartTime, String operatingEndtime,
										String NotificationPattern, String NotificationDate, String RecurrencePattern, String RecurrenceRange,
										String MobileNumber, String NotificationDetails, String Source)
	{
		String status="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		
		try
		{
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			//Validate mandatory parameters
			if(assetID==null || operatingStartTime==null || operatingEndtime==null )
			{
				fLogger.fatal("TimefenceDetailsImpl:setTimefenceDetailsNew:DAL:loginID:"+loginID+";Mandatory parameter is NULL");
				return "FAILURE";
			}
			
			//STEP1: Validate operating start time and end time
			if(	operatingStartTime==null ||	
					(  !( ( operatingStartTime.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) 
							|| ( operatingStartTime.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$"))
						)
					)
			   )
			{
				fLogger.fatal("TimefenceDetailsImpl:setTimefenceDetailsNew:DAL:loginID:"+loginID+"; assetID:"+assetID+"; Invalid format of Operating Start Time");
				return "FAILURE";
			} 
			
			if(	operatingEndtime==null ||	
					(  !( ( operatingEndtime.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) 
							|| ( operatingEndtime.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$"))
						)
					)
			   )
			{
				fLogger.fatal("TimefenceDetailsImpl:setTimefenceDetailsNew:DAL:loginID:"+loginID+"; assetID:"+assetID+"; Invalid format of Operating End Time");
				return "FAILURE";
			} 
			
			
				//STEP2: Update Operating start time and Operating end time in asset_profile table
			String updateQ = "update asset_profile set operatingStartTime='"+operatingStartTime+"', operatingEndTime='"+operatingEndtime+"'," +
					" TimeFence=1,Source='"+Source+"' where" +
					" serialnumber='"+assetID+"'";
			stmt.executeUpdate(updateQ);
			
			
			if(NotificationPattern==null) //FOR LL2 machines
			{
				iLogger.info("TimefenceDetailsImpl:setTimefenceDetails:DAL:loginID:"+loginID+"; AssetID:"+assetID+"; NotificationPattern is NULL " +
						"and hence updating only asset_profile table");
				return "SUCCESS";
			}
			
			//Validate whether the received machine is LL4 or not - As this functionality is applicable only for LL4 machines
			int ll4Machine=0;
			String ll4machineQ = "select serial_number from asset_monitoring_snapshot where serial_number='"+assetID+"' " +
					" and JSON_EXTRACT(TxnData,'$.FW_VER') is not null and ( CAST(SUBSTRING(JSON_EXTRACT(TxnData,'$.FW_VER'),2,2) AS UNSIGNED) >=30 and " +
					"  CAST(SUBSTRING(JSON_EXTRACT(TxnData,'$.FW_VER'),2,2) AS UNSIGNED) < 50)";
			ResultSet rs1 = stmt.executeQuery(ll4machineQ);
			while(rs1.next())
			{
				ll4Machine=1;
			}
			if(ll4Machine==0)
			{
				iLogger.info("TimefenceDetailsImpl:setTimefenceDetailsNew:DAL:loginID:"+loginID+"; AssetID:"+assetID+"; Input VIN is not LL4 machine " +
						"and hence updating only asset_profile table");
				return "SUCCESS";
			}
			
			//STEP3: Insert timefence details to timefence_settings table
			String timefenceSettingsUpdateQ = null;
			String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			
			if(NotificationPattern.equalsIgnoreCase("OneTime"))
			{
				timefenceSettingsUpdateQ = "update timefence_settings set NotificationPattern='"+NotificationPattern+"'," +
						" NotificationDate='"+NotificationDate+"', RecurrencePattern=NULL,  RecurrenceRange=NULL, LastUpdatedTime='"+currentDate+"' "+
						" , MobileNumber='"+MobileNumber+"' where AssetID='"+assetID+"'";
			}
			else if(NotificationPattern.equalsIgnoreCase("Recurrence"))
			{
				timefenceSettingsUpdateQ = "update timefence_settings set NotificationPattern='"+NotificationPattern+"', NotificationDate=NULL, " +
						" RecurrencePattern='"+RecurrencePattern+"', RecurrenceRange='"+RecurrenceRange+"', LastUpdatedTime='"+currentDate+"' "+
						" , MobileNumber='"+MobileNumber+"' where AssetID='"+assetID+"'";
			}
			
			int updateCount = stmt.executeUpdate(timefenceSettingsUpdateQ);
			
			if(updateCount==0)
			{
				String insertQ = null;
				if(NotificationPattern.equalsIgnoreCase("OneTime"))
				{
					insertQ = "INSERT INTO timefence_settings(AssetID,NotificationPattern,NotificationDate,RecurrencePattern,RecurrenceRange," +
							"LastUpdatedTime,MobileNumber) VALUES " +
						" ('"+assetID+"', '"+NotificationPattern+"', '"+NotificationDate+"', NULL, NULL,'"+currentDate+"','"+MobileNumber+"')";
				}
				else if(NotificationPattern.equalsIgnoreCase("Recurrence"))
				{
					insertQ = "INSERT INTO timefence_settings(AssetID,NotificationPattern,NotificationDate,RecurrencePattern,RecurrenceRange," +
							"LastUpdatedTime,MobileNumber) VALUES " +
							" ('"+assetID+"', '"+NotificationPattern+"', NULL, '"+RecurrencePattern+"', '"+RecurrenceRange+"','"+currentDate+"'," +
									"'"+MobileNumber+"')";
				}
				stmt.executeUpdate(insertQ);
			}
			
			
			//STEP4: Insert details to notification preference table for day time preference
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
			
			String startTime = "00:00:00";
			String endTime = "23:59:59";
			
			HashMap<String,Object> recurrenceRangeMap = new HashMap<String,Object>();
			if(RecurrenceRange!=null)
				recurrenceRangeMap =new Gson().fromJson(RecurrenceRange, HashMap.class);
			String startDate=null, endDate=null;
			
			
			if(NotificationPattern.equalsIgnoreCase("OneTime"))
			{
				startDate=NotificationDate;
				endDate= NotificationDate;
			}
			else if(NotificationPattern.equalsIgnoreCase("Recurrence"))
			{
				if(recurrenceRangeMap.get("StartDate")!=null)
					startDate = recurrenceRangeMap.get("StartDate").toString();
				if(recurrenceRangeMap.containsKey("EndDate") && recurrenceRangeMap.get("EndDate")!=null && !(recurrenceRangeMap.get("EndDate").toString().equalsIgnoreCase("NULL")))
					endDate = recurrenceRangeMap.get("EndDate").toString();
			}
			
			if(endDate==null || endDate.equalsIgnoreCase("NULL") || endDate.trim().length()==0)
			{
				endDate=null;
			}
			else
			{
				endDate="'"+endDate+"'";
			}
			
			
			HashMap<String,Object> recurrencePattern = new HashMap<String,Object>();
			if(RecurrencePattern!=null)
				recurrencePattern =new Gson().fromJson(RecurrencePattern, HashMap.class);
			String DayOfTheWeek=null;
			if(recurrencePattern.containsKey("Daily"))
			{
				DayOfTheWeek="Mon,Tue,Wed,Thu,Fri,Sat,Sun";
			}
			else if (recurrencePattern.containsKey("Weekly"))
			{
				String[] daysList =(recurrencePattern.get("Weekly")).toString().split(",");
				for(int i=0; i< daysList.length; i++)
				{
					if(DayOfTheWeek==null)
						DayOfTheWeek=daysList[i].toString().substring(0, 3);
					else
						DayOfTheWeek=DayOfTheWeek+","+daysList[i].toString().substring(0, 3);
				}
			}
			
			String updateQuery = null;
			if(NotificationPattern.equalsIgnoreCase("OneTime"))
			{
				updateQuery = "update notification_preference set NotificationPreference='"+dayTimePreferenceJSON+"'," +
					" StartTime='"+startTime+"', EndTime='"+endTime+"', StartDate='"+startDate+"', EndDate="+endDate+", DayOfTheWeek=NULL, UserID='"+loginID+"'"+
					" where AssetID='"+assetID+"' and AlertTypeID=3 and StartTime='00:00:00'";
			}
			else if(NotificationPattern.equalsIgnoreCase("Recurrence"))
			{
				updateQuery = "update notification_preference set NotificationPreference='"+dayTimePreferenceJSON+"'," +
						" StartTime='"+startTime+"', EndTime='"+endTime+"', StartDate='"+startDate+"', EndDate="+endDate+", DayOfTheWeek='" +DayOfTheWeek+"' , UserID='"+loginID+"'"+
						" where AssetID='"+assetID+"' and AlertTypeID=3 and StartTime='00:00:00'";
			}
			
			int updateNPCount = stmt.executeUpdate(updateQuery);
			
			if(updateNPCount==0)
			{
				String insertQ = null;
				if(NotificationPattern.equalsIgnoreCase("OneTime"))
				{
					insertQ = "INSERT INTO notification_preference(AssetID,AlertTypeID,NotificationPreference,StartTime,EndTime," +
						"StartDate,EndDate,DayOfTheWeek,UserID) VALUES " +
						" ('"+assetID+"', 3, '"+dayTimePreferenceJSON+"', '"+startTime+"', '"+endTime+"', '"+startDate+"', "+endDate+", NULL"+",'"+loginID+"'"+
								")";
				}
				else if(NotificationPattern.equalsIgnoreCase("Recurrence"))
				{
					insertQ = "INSERT INTO notification_preference(AssetID,AlertTypeID,NotificationPreference,StartTime,EndTime," +
							"StartDate,EndDate,DayOfTheWeek,UserID) VALUES " +
							" ('"+assetID+"', 3, '"+dayTimePreferenceJSON+"', '"+startTime+"', '"+endTime+"', '"+startDate+"', "+endDate+", '"+DayOfTheWeek+"' ,'"+loginID+"'"+
									")";
				}
				stmt.executeUpdate(insertQ);
			}
			
				
			//STEP6: Insert details to Timefence change log
			String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			int partitionKey = Integer.valueOf(currentTime.substring(0,7).replace("-", ""));
			String InsertQ= null;
			if(NotificationPattern.equalsIgnoreCase("OneTime"))
			{
				InsertQ = "INSERT INTO Timefence_change_log(AssetID,UpdatedTime,UserID,OperatingStartTime,OperatingEndTime,NotificationPattern," +
					"NotificationDate,RecurrencePattern,RecurrenceRange,NotificationPreference,StartTime,EndTime,Status,PartitionKey,Source) VALUES " +
					"('"+assetID+"'," +	"'"+currentTime+"', '"+loginID+"', '"+operatingStartTime+"', '"+operatingEndtime+"'," +
					"'"+NotificationPattern+"', '"+NotificationDate+"', NULL, NULL, '"+dayTimePreferenceJSON+"'," +
					"'00:00:00', '23:59:59', 1,"+partitionKey+", '"+Source+"')";
			}
			else if(NotificationPattern.equalsIgnoreCase("Recurrence"))
			{
				InsertQ = "INSERT INTO Timefence_change_log(AssetID,UpdatedTime,UserID,OperatingStartTime,OperatingEndTime,NotificationPattern," +
						"NotificationDate,RecurrencePattern,RecurrenceRange,NotificationPreference,StartTime,EndTime,Status,PartitionKey,Source) VALUES " +
						"('"+assetID+"'," +	"'"+currentTime+"', '"+loginID+"', '"+operatingStartTime+"', '"+operatingEndtime+"'," +
						"'"+NotificationPattern+"', NULL, '"+RecurrencePattern+"', '"+RecurrenceRange+"', '"+dayTimePreferenceJSON+"'," +
						"'00:00:00', '23:59:59', 1,"+partitionKey+", '"+Source+"')";
			}
			
			stmt.executeUpdate(InsertQ);
			
			
			
		}
		
		catch(Exception e)
		{
			status="FAILURE";
			fLogger.fatal("TimefenceDetailsImpl:setTimefenceDetailsNew:DAL:loginID:"+loginID+":assetID:"+assetID+"; Exception:",e.getMessage(),e);
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
				fLogger.fatal("TimefenceDetailsImpl:setTimefenceDetailsNew:DAL:loginID:"+loginID+"; assetID:"+assetID+"; Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		
		return status;
	}
	
	//******************************************************************************************************************************
	
	public TimeFenceDetailsPOJONew getTimefenceDetailsNew(String loginID, String accountCode,String assetID)
	{
		TimeFenceDetailsPOJONew responseObj = new TimeFenceDetailsPOJONew();
		Logger fLogger = FatalLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		
		try
		{
			
			//Get the Operating start time and end time of the machine
			String query ="select a.operatingStartTime, a.operatingEndTime, c.Account_Code as AccountCode" +
					" from asset_profile a, asset_owner_snapshot b, account c" +
					" where a.serialnumber=b.serial_Number and b.account_id=c.account_id and b.account_type='Customer' and " +
					" a.serialNumber='"+assetID+"'";
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			rs = stmt.executeQuery(query);
			
			while(rs.next())
            {
				responseObj.setVIN(assetID);
				responseObj.setCustomerCode(rs.getString("AccountCode"));
				if(rs.getTime("operatingStartTime")!=null)
				{
					responseObj.setOperatingStartTime(String.valueOf(rs.getTime("operatingStartTime")));
				}
				if(rs.getTime("operatingEndTime")!=null)
				{
					responseObj.setOperatingEndtime(String.valueOf(rs.getTime("operatingEndTime")));
				}
            }
			
			//Get time fence settings for the given VIN
			String timeFenceDetailsQuery = "select NotificationPattern, NotificationDate, RecurrencePattern, RecurrenceRange, LastUpdatedTime,MobileNumber from timefence_settings" +
					" where AssetID='"+assetID+"'";
			rs = stmt.executeQuery(timeFenceDetailsQuery);
			while(rs.next())
			{
				HashMap<String,Object> TimefenceDetails = new HashMap<String,Object>();
				TimefenceDetails.put("NotificationPattern", rs.getString("NotificationPattern"));
				if(rs.getDate("NotificationDate")!=null)
				{
					String notificationDate = new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("NotificationDate"));
					TimefenceDetails.put("NotificationDate", notificationDate);
				}
				
				if(rs.getString("NotificationPattern")!=null && rs.getString("NotificationPattern").equalsIgnoreCase("Recurrence"))
				{
					ObjectMapper mapper = new ObjectMapper();
					HashMap<String,Object> recPatternMap =new Gson().fromJson(rs.getObject("RecurrencePattern").toString(), HashMap.class);
					TimefenceDetails.put("RecurrencePattern",recPatternMap);
					
					//TimefenceDetails.put("RecurrencePattern", rs.getObject("RecurrencePattern"));
					
					HashMap<String,Object> recRangeMap =new Gson().fromJson(rs.getObject("RecurrenceRange").toString(), HashMap.class);
					TimefenceDetails.put("RecurrenceRange",recRangeMap);
					//TimefenceDetails.put("RecurrenceRange",rs.getObject("RecurrenceRange"));
				}
				responseObj.setTimefenceDetails(TimefenceDetails);
				
				if(rs.getDate("LastUpdatedTime")!=null)
				{
					String createdDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getDate("LastUpdatedTime"));
					responseObj.setCreatedDate(createdDate);
				}
				
				if(rs.getString("MobileNumber")!=null)
				{
					responseObj.setMobileNumber(rs.getString("MobileNumber"));
				}
				
			}
			
			
			//Get notification preference for Timefence alert
			String notificationPrefQ = "select JSON_EXTRACT(NotificationPreference,'$.SMS') as SMS," +
					" JSON_EXTRACT(NotificationPreference,'$.WhatsApp') as WhatsApp, " +
					" JSON_EXTRACT(NotificationPreference,'$.\"Voice Call\"') as VoiceCall ," +
					" JSON_EXTRACT(NotificationPreference,'$.\"Push notification\"') as PushNotification," +
					" StartTime, EndTime " +
					" from notification_preference where AlertTypeID=3 and AssetID='"+assetID+"'";
			rs = stmt.executeQuery(notificationPrefQ);
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
							mobileNumber =  rs.getString("VoiceCall").replaceAll("\"", "").replaceAll("\\\\", "");
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
		}
		
		catch(Exception e)
		{
			fLogger.fatal("TimefenceDetailsImpl:getTimefenceDetailsNew:DAL:loginID:"+loginID+"; assetID:"+assetID+"; Exception:",e.getMessage(),e);
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
				fLogger.fatal("TimefenceDetailsImpl:getTimefenceDetailsNew:DAL:loginID:"+loginID+"; assetID:"+assetID+"; Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		
		return responseObj;
	}
	
}
