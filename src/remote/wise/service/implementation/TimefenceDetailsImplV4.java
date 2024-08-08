/*
 *  20230720 : CR432 : Prasanna Lakshmi : GeoFence/TimeFence API Changes 
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.pojo.TimeFenceDetailsPOJONew;
import remote.wise.util.ConnectMySQL;

public class TimefenceDetailsImplV4 {

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
//						
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
//							mobileNumber =  rs.getString("VoiceCall").replaceAll("\"", "").replaceAll("\\\\", "");
//						}
//						else
//							dayTimePreference.put("Voice Call", "0");
						
						
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
			fLogger.fatal("TimefenceDetailsImplV4:getTimefenceDetailsNew:DAL:loginID:"+loginID+"; assetID:"+assetID+"; Exception:",e.getMessage(),e);
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
				fLogger.fatal("TimefenceDetailsImplV4:getTimefenceDetailsNew:DAL:loginID:"+loginID+"; assetID:"+assetID+"; Exception in closing the connection:"+e.getMessage(),e);
			}
		}
		
		return responseObj;
	}
}
