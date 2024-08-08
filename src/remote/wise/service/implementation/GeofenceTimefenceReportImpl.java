package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.GeoFenceDetailsPOJO;
import remote.wise.pojo.GeofenceReportPOJO;
import remote.wise.pojo.TimefenceReportPOJO;
import remote.wise.pojo.WhatsappReportPOJO;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.ListToStringConversion;

public class GeofenceTimefenceReportImpl 
{
	public List<GeofenceReportPOJO> getLandmarkList(String loginID, String regionCode,String zonalCode,String dealerCode,String customerCode,
						String profileCode, String modelCode)
	{
		List<GeofenceReportPOJO> responseList = new LinkedList<GeofenceReportPOJO>();
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		
		try
		{
			 //Get DB connection
		    con = new ConnectMySQL().getConnection();
			stmt=con.createStatement();
			
			String baseQuery = "select c.landmark_id as LandmarkID from com_rep_oem_enhanced a, landmark_asset b, landmark c " +
					" where a.serial_number=b.serial_number and b.landmark_id=c.landmark_id" +
					" and c.ActiveStatus=1 and c.Geofence=1 and version is not null" +
					" and ( CAST(SUBSTRING(version,1,2) AS UNSIGNED)) >=30 " +
					" and ( CAST(SUBSTRING(version,1,2) AS UNSIGNED)) < 50 ";
			
			if(customerCode!=null)
			{
				List<String> customerList = Arrays.asList(customerCode.split(","));
				String customerListAsString = new ListToStringConversion().getCommaSeperatedStringList(customerList).toString();
				baseQuery = baseQuery+" and a.BP_code in ("+customerListAsString+")";
			}
			else if (dealerCode!=null)
			{
				List<String> dealerList = Arrays.asList(dealerCode.split(","));
				String dealerListAsString = new ListToStringConversion().getCommaSeperatedStringList(dealerList).toString();
				baseQuery = baseQuery+" and a.DealerCode in ("+dealerListAsString+")";
			}
			else if (zonalCode!=null)
			{
				List<String> zonalList = Arrays.asList(zonalCode.split(","));
				String zonalListAsString = new ListToStringConversion().getCommaSeperatedStringList(zonalList).toString();
				baseQuery = baseQuery+" and a.ZonalCode in ("+zonalListAsString+")";
			}
			else if (regionCode!=null)
			{
				List<String> regionList = Arrays.asList(regionCode.split(","));
				String regionListAsString = new ListToStringConversion().getCommaSeperatedStringList(regionList).toString();
				baseQuery = baseQuery+" and a.Country in ("+regionListAsString+")";
			}
			else
			{
				//If none of the filters is selected, filter the VINs based on the user login
				//Get the role_id and account of the logged in user
				String roleQ = "select a.role_id, c.mapping_code, a.countrycode from contact a, account_contact b, account c" +
						" where a.contact_id=b.contact_id and b.account_id=c.account_id and a.contact_id='"+loginID+"'";
				rs = stmt.executeQuery(roleQ);
				int roleId=0;
				String countryCode=null,mapping_code=null;
				while(rs.next())
				{
					roleId=rs.getInt("role_id");
					countryCode=rs.getString("countrycode");
					mapping_code=rs.getString("mapping_code");
				}
				
				if(roleId==2)
					baseQuery=baseQuery+" and a.Country ='"+countryCode+"'";
				else if (roleId==3)
					baseQuery = baseQuery+" and a.ZonalCode ='"+mapping_code+"'";
				else if(roleId==5 || roleId==6)
					baseQuery = baseQuery+" and a.DealerCode ='"+mapping_code+"'";
				else if(roleId==7 || roleId==8 || roleId==11)
					baseQuery = baseQuery+" and a.BP_code ='"+mapping_code+"'";
			}
			
			iLogger.debug("GeofenceTimefenceReportImpl:getLandmarkList:loginID:"+loginID+":AssetListQ:"+baseQuery);
			
			rs = stmt.executeQuery(baseQuery);
			
			List<Integer> landmarkIDList = new LinkedList<Integer>();
			while(rs.next())
			{
				int landmark=rs.getInt("LandmarkID");
				if(!(landmarkIDList.contains(landmark)))
					landmarkIDList.add(landmark);
			}
			
			
			for(int i=0; i<landmarkIDList.size(); i++)
			{
				GeofenceReportPOJO responseObj = new GeofenceReportPOJO();
				
				int landmarkId=landmarkIDList.get(i);
				
				//STEP2: Get  details using landmark_id as Input parameter			
				String landmarkDetailQ = "select d.serial_number,f.mapping_code,a.landmark_name,a.radius,a.latitude,a.longitude,a.Address,a.IsArrival," +
						"a.IsDeparture,a.CreatedTime,c.MobileNumber," +
						"JSON_EXTRACT(NotificationPreference,'$.SMS') as SMS," +
						"JSON_EXTRACT(NotificationPreference,'$.WhatsApp') as WhatsApp," +
						"JSON_EXTRACT(NotificationPreference,'$.\"Voice Call\"') as VoiceCall ," +
						"JSON_EXTRACT(NotificationPreference,'$.\"Push notification\"') as PushNotification " +
						"from landmark a, landmark_preference c,landmark_asset d , asset e, account f where  " +
						" a.landmark_id=c.landmark_id and  a.landmark_id=d.landmark_id and " +
						" d.serial_number=e.serial_number and e.primary_owner_id=f.account_id and" +
						" a.landmark_id='"+landmarkId+"'";
				rs = stmt.executeQuery(landmarkDetailQ);
				while(rs.next())
				{
					responseObj.setAssetID(rs.getString("serial_number"));
					responseObj.setCustomerCode(rs.getString("mapping_code"));
					responseObj.setLandmarkName(rs.getString("landmark_name"));
					responseObj.setRadius(rs.getDouble("radius"));
					responseObj.setAddress(rs.getString("Address"));
					responseObj.setLatitude(rs.getString("latitude"));
					responseObj.setLongitude(rs.getString("longitude"));
					responseObj.setIsArrival(rs.getString("IsArrival"));
					responseObj.setIsDeparture(rs.getString("IsDeparture"));
					responseObj.setMobileNumber(rs.getString("MobileNumber"));
					
					if(rs.getString("SMS")!=null)
						responseObj.setDayTimeSMSPref(true);
					else
						responseObj.setDayTimeSMSPref(false);
					
					if(rs.getString("WhatsApp")!=null)
						responseObj.setDayTimeWhatsAppPref(true);
					else
						responseObj.setDayTimeWhatsAppPref(false);
					
					if(rs.getString("VoiceCall")!=null)
						responseObj.setDayTimeVoiceCallPref(true);
					else
						responseObj.setDayTimeVoiceCallPref(false);
					
					if(rs.getString("PushNotification")!=null)
						responseObj.setDayTimePushNotification(true);
					else
						responseObj.setDayTimePushNotification(false);
					
					responseList.add(responseObj);
				}
			}
				
		}
		
		catch(Exception e)
		{
			fLogger.fatal("GeofenceTimefenceReportImpl:getLandmarkList:loginID:"+loginID+":Exception:",e.getMessage(),e);

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
				fLogger.fatal("GeofenceTimefenceReportImpl:getLandmarkList:loginID:"+loginID+"; Exception in closing the connection:"+e.getMessage(),e);
			}
			
		}
		
		
		return responseList;
	}
	
	
	//***************************************************************************************************************************
	public List<TimefenceReportPOJO> getTimefenceList(String loginID, String regionCode,String zonalCode,String dealerCode,String customerCode,
			String profileCode, String modelCode)
	{
		List<TimefenceReportPOJO> responseList = new LinkedList<TimefenceReportPOJO>();
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;

		try
		{
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt=con.createStatement();
			
			String baseQuery = "select a.serial_number,e.mapping_code,b.operatingStartTime,b.operatingEndTime," +
								" c.NotificationPattern,c.NotificationDate,c.RecurrencePattern," +
								" f.StartDate as StartDate,f.EndDate as EndDate,c.MobileNumber," +
								" JSON_EXTRACT(NotificationPreference,'$.SMS') as SMS," +
								" JSON_EXTRACT(NotificationPreference,'$.WhatsApp') as WhatsApp," +
								" JSON_EXTRACT(NotificationPreference,'$.\"Voice Call\"') as VoiceCall ," +
								" JSON_EXTRACT(NotificationPreference,'$.\"Push notification\"') as PushNotification" +
								" from com_rep_oem_enhanced a, asset_profile b, timefence_settings c," +
								" asset d, account e, notification_preference f" +
								" where a.serial_number=b.serialNumber and a.serial_number=c.AssetID" +
								" and a.serial_number=d.serial_number and d.primary_owner_id=e.account_id" +
								" and a.serial_number=f.AssetID and f.AlertTypeID=3" +
								" and b.operatingStartTime is not null and b.operatingEndTime is not null" ;
					
			
			if(customerCode!=null)
			{
				List<String> customerList = Arrays.asList(customerCode.split(","));
				String customerListAsString = new ListToStringConversion().getCommaSeperatedStringList(customerList).toString();
				baseQuery = baseQuery+" and a.BP_code in ("+customerListAsString+")";
			}
			else if (dealerCode!=null)
			{
				List<String> dealerList = Arrays.asList(dealerCode.split(","));
				String dealerListAsString = new ListToStringConversion().getCommaSeperatedStringList(dealerList).toString();
				baseQuery = baseQuery+" and a.DealerCode in ("+dealerListAsString+")";
			}
			else if (zonalCode!=null)
			{
				List<String> zonalList = Arrays.asList(zonalCode.split(","));
				String zonalListAsString = new ListToStringConversion().getCommaSeperatedStringList(zonalList).toString();
				baseQuery = baseQuery+" and a.ZonalCode in ("+zonalListAsString+")";
			}
			else if (regionCode!=null)
			{
				List<String> regionList = Arrays.asList(regionCode.split(","));
				String regionListAsString = new ListToStringConversion().getCommaSeperatedStringList(regionList).toString();
				baseQuery = baseQuery+" and a.Country in ("+regionListAsString+")";
			}
			else
			{
				//If none of the filters is selected, filter the VINs based on the user login
				//Get the role_id and account of the logged in user
				String roleQ = "select a.role_id, c.mapping_code, a.countrycode from contact a, account_contact b, account c" +
						" where a.contact_id=b.contact_id and b.account_id=c.account_id and a.contact_id='"+loginID+"'";
				rs = stmt.executeQuery(roleQ);
				int roleId=0;
				String countryCode=null,mapping_code=null;
				while(rs.next())
				{
					roleId=rs.getInt("role_id");
					countryCode=rs.getString("countrycode");
					mapping_code=rs.getString("mapping_code");
				}
				
				if(roleId==2)
					baseQuery=baseQuery+" and a.Country ='"+countryCode+"'";
				else if (roleId==3)
					baseQuery = baseQuery+" and a.ZonalCode ='"+mapping_code+"'";
				else if(roleId==5 || roleId==6)
					baseQuery = baseQuery+" and a.DealerCode ='"+mapping_code+"'";
				else if(roleId==7 || roleId==8 || roleId==11)
					baseQuery = baseQuery+" and a.BP_code ='"+mapping_code+"'";
			}
			
			
			rs = stmt.executeQuery(baseQuery);
			
			while(rs.next())
			{
				TimefenceReportPOJO responseObj = new TimefenceReportPOJO();
				
				responseObj.setAssetID(rs.getString("serial_number"));
				responseObj.setCustomerCode(rs.getString("mapping_code"));
				responseObj.setOperatingStartTime(String.valueOf(rs.getTime("operatingStartTime")));
				responseObj.setOperatingEndTime(String.valueOf(rs.getTime("operatingEndTime")));
				responseObj.setNotificationPattern(rs.getString("NotificationPattern"));
				
				if(rs.getDate("NotificationDate")!=null)
				{
					String notificationDate = new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("NotificationDate"));
					responseObj.setNotificationDate(notificationDate);
				}
				
				if(rs.getString("RecurrencePattern")!=null)
				{
					ObjectMapper mapper = new ObjectMapper();
					HashMap<String,Object> recPatternMap =new Gson().fromJson(rs.getObject("RecurrencePattern").toString(), HashMap.class);
					responseObj.setRecurrencePattern(recPatternMap);
				}
				
				if(rs.getTime("StartDate")!=null)
					responseObj.setStartDate(new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("StartDate")));
				
				if(rs.getTime("EndDate")!=null)
					responseObj.setEndDate(new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("EndDate")));
				
				responseObj.setMobileNumber(rs.getString("MobileNumber"));
				
				if(rs.getString("SMS")!=null)
					responseObj.setDayTimeSMSPref(true);
				else
					responseObj.setDayTimeSMSPref(false);
				
				if(rs.getString("WhatsApp")!=null)
					responseObj.setDayTimeWhatsAppPref(true);
				else
					responseObj.setDayTimeWhatsAppPref(false);
				
				if(rs.getString("VoiceCall")!=null)
					responseObj.setDayTimeVoiceCallPref(true);
				else
					responseObj.setDayTimeVoiceCallPref(false);
				
				if(rs.getString("PushNotification")!=null)
					responseObj.setDayTimePushNotification(true);
				else
					responseObj.setDayTimePushNotification(false);
				
				responseList.add(responseObj);
			}
		}
	
		catch(Exception e)
		{
			fLogger.fatal("GeofenceTimefenceReportImpl:getTimefenceList:loginID:"+loginID+":Exception:",e.getMessage(),e);

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
				fLogger.fatal("GeofenceTimefenceReportImpl:getTimefenceList:loginID:"+loginID+"; Exception in closing the connection:"+e.getMessage(),e);
			}
			
		}
		
		
		return responseList;
	}
	
	
	//***************************************************************************************************************************
	public List<WhatsappReportPOJO> getWhatsAppNotificationList(String loginID, String regionCode,String zonalCode,String dealerCode,String customerCode,
			String profileCode, String modelCode)
	{
		List<WhatsappReportPOJO> responseList = new LinkedList<WhatsappReportPOJO>();
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;

		try
		{
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt=con.createStatement();
			
			String baseQuery = "select a.serial_number,d.mapping_code,b.MobileNumber, count(*) as count" +
					" from com_rep_oem_enhanced a,WhatsApp_Preference b, asset c, account d" +
					" where a.serial_number=b.AssetID and a.serial_number=c.serial_number" +
					" and c.Primary_Owner_ID=d.account_id";
					
			
			if(customerCode!=null)
			{
				List<String> customerList = Arrays.asList(customerCode.split(","));
				String customerListAsString = new ListToStringConversion().getCommaSeperatedStringList(customerList).toString();
				baseQuery = baseQuery+" and a.BP_code in ("+customerListAsString+")";
			}
			else if (dealerCode!=null)
			{
				List<String> dealerList = Arrays.asList(dealerCode.split(","));
				String dealerListAsString = new ListToStringConversion().getCommaSeperatedStringList(dealerList).toString();
				baseQuery = baseQuery+" and a.DealerCode in ("+dealerListAsString+")";
			}
			else if (zonalCode!=null)
			{
				List<String> zonalList = Arrays.asList(zonalCode.split(","));
				String zonalListAsString = new ListToStringConversion().getCommaSeperatedStringList(zonalList).toString();
				baseQuery = baseQuery+" and a.ZonalCode in ("+zonalListAsString+")";
			}
			else if (regionCode!=null)
			{
				List<String> regionList = Arrays.asList(regionCode.split(","));
				String regionListAsString = new ListToStringConversion().getCommaSeperatedStringList(regionList).toString();
				baseQuery = baseQuery+" and a.Country in ("+regionListAsString+")";
			}
			else
			{
				//If none of the filters is selected, filter the VINs based on the user login
				//Get the role_id and account of the logged in user
				String roleQ = "select a.role_id, c.mapping_code, a.countrycode from contact a, account_contact b, account c" +
						" where a.contact_id=b.contact_id and b.account_id=c.account_id and a.contact_id='"+loginID+"'";
				rs = stmt.executeQuery(roleQ);
				int roleId=0;
				String countryCode=null,mapping_code=null;
				while(rs.next())
				{
					roleId=rs.getInt("role_id");
					countryCode=rs.getString("countrycode");
					mapping_code=rs.getString("mapping_code");
				}
				
				if(roleId==2)
					baseQuery=baseQuery+" and a.Country ='"+countryCode+"'";
				else if (roleId==3)
					baseQuery = baseQuery+" and a.ZonalCode ='"+mapping_code+"'";
				else if(roleId==5 || roleId==6)
					baseQuery = baseQuery+" and a.DealerCode ='"+mapping_code+"'";
				else if(roleId==7 || roleId==8 || roleId==11)
					baseQuery = baseQuery+" and a.BP_code ='"+mapping_code+"'";
			}
			
			baseQuery = baseQuery+" group by b.AssetID ";
			rs = stmt.executeQuery(baseQuery);
			
			while(rs.next())
			{
				WhatsappReportPOJO responseObj = new WhatsappReportPOJO();
				
				responseObj.setAssetID(rs.getString("serial_number"));
				responseObj.setCustomerCode(rs.getString("mapping_code"));
				responseObj.setMobileNumber(rs.getString("MobileNumber"));
				responseObj.setNumberOfNotificationsPerDay(rs.getInt("count"));
				
				responseList.add(responseObj);
			}
		}
	
		catch(Exception e)
		{
			fLogger.fatal("GeofenceTimefenceReportImpl:getWhatsAppNotificationList:loginID:"+loginID+":Exception:",e.getMessage(),e);

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
				fLogger.fatal("GeofenceTimefenceReportImpl:getWhatsAppNotificationList:loginID:"+loginID+"; Exception in closing the connection:"+e.getMessage(),e);
			}
			
		}
		
		
		return responseList;
	}
}
