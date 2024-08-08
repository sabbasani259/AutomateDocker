package remote.wise.service.implementation;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;

import remote.wise.handler.WhatsappHandler;
import remote.wise.handler.WhatsappTemplate;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.pojo.WhatsAppPrefPOJO;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.ListToStringConversion;

public class WhatsAppUpdateDetailsImpl {

	public String setWhatsAppPreference(String loginID, String accountCode,
			String assetID, String mobileNumber, String notificationTimeSlot,
			String machineDetails) {

		String status="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		
		try{
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			if(assetID==null )
			{
				fLogger.fatal("WhatsAppUpdateDetailsImpl:setWhatsAppPreference:DAL:loginID:"+loginID+":notificationTimeSlot:"+notificationTimeSlot+";Mandatory parameter is NULL");
				return "FAILURE";
			}			
			
			if(mobileNumber!=null & !mobileNumber.contains("+"))
			{
				mobileNumber="+91"+mobileNumber;
			}
			
			//Validate Machine details
			if(machineDetails!=null)
			{
				List<String> machineDetailsList1 = Arrays.asList(machineDetails.split(","));
				List<String> validMachineDetails = new LinkedList<String>();
				validMachineDetails.add("CMH");
				validMachineDetails.add("Location");
				validMachineDetails.add("Fuel level");
				validMachineDetails.add("Active Health Alerts");
				
				if(!(validMachineDetails.containsAll(machineDetailsList1)))
				{
					fLogger.fatal("WhatsAppUpdateDetailsImpl:setWhatsAppPreference:DAL:loginID:"+loginID+":notificationTimeSlot:"+notificationTimeSlot+";Valid parameters for Machine Details are CMH,Location,Fuel level,Active Health Alerts");
					return "FAILURE";
				}
			}
			
			//Validate Notification timeslots
			if(notificationTimeSlot!=null)
			{
				HashMap<String,Integer> notificationTimeMap=new Gson().fromJson(notificationTimeSlot, HashMap.class);	
				Set<String> notificationTimeSlotSet = notificationTimeMap.keySet();
				
				List<String> validTimeSlots = new LinkedList<String>();
				validTimeSlots.add("11AM");
				validTimeSlots.add("3PM");
				validTimeSlots.add("7PM");
				
				if(!(validTimeSlots.containsAll(notificationTimeSlotSet)))
				{
					fLogger.fatal("WhatsAppUpdateDetailsImpl:setWhatsAppPreference:DAL:loginID:"+loginID+":notificationTimeSlot:"+notificationTimeSlot+";" +
							"Valid parameters for NotificationTimeSlot are 11AM,3PM and 7PM");
					return "FAILURE";
				}
			}
			
			//Not to set whatsapp preference for LL2 machines
			int ll4Machine =0;
			String ll4machineQ = "select serial_number from asset_monitoring_snapshot where serial_number ='"+assetID+"'"  +
					" and  JSON_EXTRACT(TxnData,'$.FW_VER') is not null and ( CAST(SUBSTRING(JSON_EXTRACT(TxnData,'$.FW_VER'),2,2) AS UNSIGNED) >=30 and " +
					"  CAST(SUBSTRING(JSON_EXTRACT(TxnData,'$.FW_VER'),2,2) AS UNSIGNED) < 50)";
			ResultSet rs1 = stmt.executeQuery(ll4machineQ);
			while(rs1.next())
			{
				ll4Machine=1;
			}
			
			if(ll4Machine==0)
			{
				fLogger.fatal("WhatsAppUpdateDetailsImpl:setWhatsAppPreference:DAL:loginID:"+loginID+":assetID:"+assetID+";" +
						"Not a LL4 machine and hence not setting the preference");
				return "FAILURE";
			}
			
			if(notificationTimeSlot==null)
			{
				String deleteQ="delete from WhatsApp_Preference where AssetID='"+assetID+"'";
				stmt.executeUpdate(deleteQ);
			}
			
			else
			{
				HashMap<String,Integer> notificationTimeMap=new Gson().fromJson(notificationTimeSlot, HashMap.class);					
				for(Map.Entry<String,Integer> notificationTime : notificationTimeMap.entrySet())
				{				
					if(String.valueOf(notificationTime.getValue()).equalsIgnoreCase("0.0")){
						String notiTimeFormat=null;
						if(notificationTime.getKey().equalsIgnoreCase("11AM")){
							notiTimeFormat="11:00:00";
							//Delete record from WhatsAppPreference table
							String deleteQ="delete from WhatsApp_Preference where AssetID='"+assetID+"' and NotificationTimeSlot='"+notiTimeFormat+"'";
							stmt.executeUpdate(deleteQ);
						}
						if(notificationTime.getKey().equalsIgnoreCase("3PM")){
							notiTimeFormat="15:00:00";
							//Delete record from WhatsAppPreference table
							String deleteQ="delete from WhatsApp_Preference where AssetID='"+assetID+"' and NotificationTimeSlot='"+notiTimeFormat+"'";
							stmt.executeUpdate(deleteQ);
						}
						if(notificationTime.getKey().equalsIgnoreCase("7PM")){
							notiTimeFormat="19:00:00";
							//Delete record from WhatsAppPreference table
							String deleteQ="delete from WhatsApp_Preference where AssetID='"+assetID+"' and NotificationTimeSlot='"+notiTimeFormat+"'";
							stmt.executeUpdate(deleteQ);
						}			
					}else{
						List<String> machineDetailsList = new ArrayList<String>(Arrays.asList(machineDetails.split(",")));						
						String notiTimeFormat=null;
						if(notificationTime.getKey().equalsIgnoreCase("11AM") && String.valueOf(notificationTime.getValue()).equalsIgnoreCase("1.0")){
	
							notiTimeFormat="11:00:00";
							//Select Query for AssetID
							String selectQ="select AssetID from WhatsApp_Preference where AssetID='"+assetID+"' and NotificationTimeSlot='"+notiTimeFormat+"'";					
							rs = stmt.executeQuery(selectQ);
							List<String> asstIDList= new LinkedList<String>();
							while(rs.next()){
						        	asstIDList.add(rs.getString("AssetID"));					        	
						        }
							String listToString=new ListToStringConversion().getStringList(asstIDList).toString(); 
							 if(listToString.length() != 0){
								 //update WhatsApp_Preference table
								 String updateQ="update WhatsApp_Preference set NotificationTimeSlot='"+notiTimeFormat+"',UserID='"+loginID+"',MobileNumber='"+mobileNumber+"'," +
								 		"MachineDetails='"+machineDetails+"' where AssetID in ("+listToString+") and NotificationTimeSlot='"+notiTimeFormat+"'";
								 stmt.executeUpdate(updateQ);
							 } else{
								//Insert record WhatsApp_Preference table
								 String InsertQ= "INSERT INTO WhatsApp_Preference(AssetID,NotificationTimeSlot,UserID,MobileNumber,MachineDetails) VALUES ('"+assetID+"'," +
											"'"+notiTimeFormat+"', '"+loginID+"','"+mobileNumber+"','"+machineDetails+"')";			
									stmt.executeUpdate(InsertQ);							 
							 }														
						}	
						if(notificationTime.getKey().equalsIgnoreCase("3PM") && String.valueOf(notificationTime.getValue()).equalsIgnoreCase("1.0")){
							
							notiTimeFormat="15:00:00";						
							//Select Query for AssetID						
							String selectQ="select AssetID from WhatsApp_Preference where AssetID='"+assetID+"' and NotificationTimeSlot='"+notiTimeFormat+"'";					
							rs = stmt.executeQuery(selectQ);
							List<String> asstIDList= new LinkedList<String>();
							while(rs.next()){
						        	asstIDList.add(rs.getString("AssetID"));					        	
						        }
							String listToString=new ListToStringConversion().getStringList(asstIDList).toString(); 
							 if(listToString.length() != 0){
								//update WhatsApp_Preference table
								 String updateQ="update WhatsApp_Preference set NotificationTimeSlot='"+notiTimeFormat+"',UserID='"+loginID+"'," +
								 		"MobileNumber='"+mobileNumber+"',MachineDetails='"+machineDetails+"' where AssetID in ("+listToString+") and NotificationTimeSlot='"+notiTimeFormat+"'";
								 stmt.executeUpdate(updateQ);
							 } else{
								//Insert record WhatsApp_Preference table
								 String InsertQ= "INSERT INTO WhatsApp_Preference(AssetID,NotificationTimeSlot,UserID,MobileNumber,MachineDetails) VALUES ('"+assetID+"'," +
											"'"+notiTimeFormat+"', '"+loginID+"','"+mobileNumber+"','"+machineDetails+"')";			
									stmt.executeUpdate(InsertQ);							 
							 }														
						}
						if(notificationTime.getKey().equalsIgnoreCase("7PM") && String.valueOf(notificationTime.getValue()).equalsIgnoreCase("1.0")){
							
							notiTimeFormat="19:00:00";						
							//Select Query for AssetID
							String selectQ="select AssetID from WhatsApp_Preference where AssetID='"+assetID+"' and NotificationTimeSlot='"+notiTimeFormat+"'";					
							rs = stmt.executeQuery(selectQ);
							List<String> asstIDList= new LinkedList<String>();
							while(rs.next()){
						        	asstIDList.add(rs.getString("AssetID"));					        	
						        }
							String listToString=new ListToStringConversion().getStringList(asstIDList).toString(); 
							 if(listToString.length() != 0){
								//update WhatsApp_Preference table
								 String updateQ="update WhatsApp_Preference set NotificationTimeSlot='"+notiTimeFormat+"',UserID='"+loginID+"'," +
								 		"MobileNumber='"+mobileNumber+"',MachineDetails='"+machineDetails+"' where AssetID in ("+listToString+") and NotificationTimeSlot='"+notiTimeFormat+"'";
								 stmt.executeUpdate(updateQ);
							 } else{
								//Insert record WhatsApp_Preference table
								 String InsertQ= "INSERT INTO WhatsApp_Preference(AssetID,NotificationTimeSlot,UserID,MobileNumber,MachineDetails) VALUES ('"+assetID+"'," +
											"'"+notiTimeFormat+"', '"+loginID+"','"+mobileNumber+"','"+machineDetails+"')";			
									stmt.executeUpdate(InsertQ);							 
							 }														
						}	
					}					
				}	
			}
		}catch (Exception e) {
			status="FAILURE";
			fLogger.fatal("WhatsAppUpdateDetailsImpl:setWhatsAppPreference:DAL:loginID:"+loginID+":assetID:"+assetID+"; Exception:",e.getMessage(),e);
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
				fLogger.fatal("WhatsAppUpdateDetailsImpl:setWhatsAppPreference:DAL:loginID:"+loginID+"; assetID:"+assetID+"; Exception in closing the connection:"+e.getMessage(),e);
			}			
		}
		
		return status;
	}
	
	
	//*************************************************************************************************************************************
	public WhatsAppPrefPOJO getWhatsAppPreference(String loginID, String VIN)
	{
		WhatsAppPrefPOJO responseObj = new WhatsAppPrefPOJO();
		Logger fLogger = FatalLoggerClass.logger;
		
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		
		try
		{
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			String query = "select NotificationTimeSlot,MobileNumber,MachineDetails from WhatsApp_Preference where AssetID='"+VIN+"'";
			rs = stmt.executeQuery(query);
			
			List<String> masterTimeSlots = new LinkedList<String>();
			masterTimeSlots.add("11AM");
			masterTimeSlots.add("3PM");
			masterTimeSlots.add("7PM");
			
			HashMap<String,String> notificationTimeSlot = new HashMap<String,String>();
			while(rs.next())
			{
				responseObj.setAssetID(VIN);
				//responseObj.setNotificationTimeSlot(String.valueOf(rs.getTime("NotificationTimeSlot")));
				if(String.valueOf(rs.getTime("NotificationTimeSlot")).equalsIgnoreCase("11:00:00"))
					notificationTimeSlot.put("11AM", "1");
				else if (String.valueOf(rs.getTime("NotificationTimeSlot")).equalsIgnoreCase("15:00:00"))
					notificationTimeSlot.put("3PM", "1");
				else if (String.valueOf(rs.getTime("NotificationTimeSlot")).equalsIgnoreCase("19:00:00"))
					notificationTimeSlot.put("7PM", "1");
					
				responseObj.setMobileNumber(rs.getString("MobileNumber"));
				responseObj.setMachineDetails(rs.getString("MachineDetails"));
			}
			
			//Get the timeslots for which 0 has to be set to
			for(int i=0; i<masterTimeSlots.size(); i++)
			{
				if(!(notificationTimeSlot.containsKey(masterTimeSlots.get(i))))
					notificationTimeSlot.put(masterTimeSlots.get(i), "0");
			}
			
			responseObj.setNotificationTimeSlot(notificationTimeSlot);
		}
		catch(Exception e)
		{
			fLogger.fatal("WhatsAppUpdateDetailsImpl:getWhatsAppPreference:DAL:VIN:"+VIN+":Exception:",e.getMessage(),e);
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
				fLogger.fatal("WhatsAppUpdateDetailsImpl:getWhatsAppPreference:DAL:loginID:"+loginID+"; VIN:"+VIN+"; " +
						"Exception in closing the connection:"+e.getMessage(),e);
			}			
		}
		return responseObj;
	}
	//**********************************************************************************************************
	
	public String sendWhatsAppNotification(String notificationTimeSlot) {
		
		String status="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		
		Connection con=null;
		Statement stmt1=null,stmt2=null,stmt3=null,stmt4=null;
		ResultSet rs1 = null,rs2 = null,rs3 = null,rs4 = null;
		try{
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt1 = con.createStatement();
		
			
			if(notificationTimeSlot==null)
			{
				fLogger.fatal("WhatsAppUpdateDetailsImpl:sendWhatsAppNotification:DAL:notificationTimeSlot:"+notificationTimeSlot+";Mandatory parameter is NULL");
				return "FAILURE";
			}
			
			String selectQ1="select AssetID,MachineDetails,MobileNumber from WhatsApp_Preference where NotificationTimeSlot='"+notificationTimeSlot+"'";
			rs1=stmt1.executeQuery(selectQ1);
			String serialNumber=null;
			
			while(rs1.next())
			{
				LinkedHashMap<String,Object> queryResponse = new LinkedHashMap<String,Object>();				
				serialNumber=rs1.getString("AssetID");
				queryResponse.put("Machine", serialNumber);
				
				String mobileNumber=null;
				List<String> machineDetailsList=null;
				String machineDetails=rs1.getString("MachineDetails");
				String machineString = machineDetails.replace("[", "").replace("]", "").trim();
				List<String> machineLoopList = new ArrayList<String>();
				if(machineString != null){
					machineDetailsList = new ArrayList<String>(Arrays.asList(machineString.split(",")));
				}
				if(!(machineDetailsList.isEmpty())){
					for(String machine:machineDetailsList){					
						machineLoopList.add(machine.trim())	;				
					}
					
				}				
				stmt2 = con.createStatement();
				String selectQ2="select serial_number,JSON_EXTRACT(TxnData,'$.CMH') as CMH,JSON_EXTRACT(TxnData,'$.FUEL_PERCT') as FUEL_PERCT," +
						"JSON_EXTRACT(TxnData,'$.LAT') as Latitude, JSON_EXTRACT(TxnData,'$.LONG') as longitude " +
						"from asset_monitoring_snapshot where serial_number='"+serialNumber+"'";
				rs2=stmt2.executeQuery(selectQ2);
				
				while(rs2.next()){
					if(machineLoopList.contains("CMH") && rs2.getString("CMH")!=null){
						queryResponse.put("HMR",truncateDecimal(Double.valueOf(rs2.getString("CMH").replaceAll("\"", "")),3));
					}
					if(machineLoopList.contains("Fuel level")&& rs2.getString("FUEL_PERCT")!=null){
						queryResponse.put("Fuel Percentage",Double.valueOf(rs2.getString("FUEL_PERCT").replaceAll("\"", "")));
					}
					if(machineLoopList.contains("Location") && rs2.getString("Latitude")!=null && rs2.getString("longitude")!=null){
						queryResponse.put("Location",
								truncateDecimal(Double.valueOf(rs2.getString("Latitude").replaceAll("\"", "")),3)+", "+truncateDecimal(Double.valueOf(rs2.getString("longitude").replaceAll("\"", "")),3));
					}
					
				}
				
				stmt3 = con.createStatement();
				if(machineLoopList.contains("Active Health Alerts")){
					
				String selectQ3="select a.serial_number, a.event_id,b.event_name, b.alert_code from asset_event a, business_event b " +
						        "where a.event_id=b.event_id and a.Serial_Number='"+serialNumber+"' and a.active_status=1 and a.PartitionKey=1 " +
								"and b.event_type_id=2";
				rs3=stmt3.executeQuery(selectQ3);
				
				String alertCode=null;
				List<String> eventNameList= new LinkedList<String>();
				String eventName=null;
				
				stmt4=con.createStatement();
				while(rs3.next()){
					eventName=rs3.getString("event_name");
					alertCode=rs3.getString("alert_code");					
			
					String selectQ4="select *from alert_role_mapping where role_id =7 and JSON_EXTRACT(alertmap_display, '$.\""+alertCode+"\"')=\"1\"";
					rs4=stmt4.executeQuery(selectQ4);
					while(rs4.next()){
						eventNameList.add(eventName);
					}					
				}	
				if(!(eventNameList.isEmpty()))
				{
					String eventNameListAsString=null;
					for(int i=0; i<eventNameList.size(); i++)
					{
						if(eventNameListAsString==null)
							eventNameListAsString=eventNameList.get(i);
						else
							eventNameListAsString=eventNameListAsString+"\n"+eventNameList.get(i);
						
					}
					queryResponse.put("Active Health Alerts",eventNameListAsString);
				}				
			}	
				mobileNumber=rs1.getString("MobileNumber");
				if(!(queryResponse.isEmpty())){
					String queryResponseJson = new ObjectMapper().writeValueAsString(queryResponse);
					WhatsappTemplate whatsappTemplate = new WhatsappTemplate();				
					whatsappTemplate.setTo(mobileNumber);
					whatsappTemplate.setMsgBody(queryResponseJson);
					whatsappTemplate.setMsgType("WhatsAppUpdate");
					new WhatsappHandler().putToKafkaProducerQueue("WhatsAppQueue", whatsappTemplate);
				}				
		}			
		}catch(Exception e){
			status="FAILURE";
			fLogger.fatal("WhatsAppUpdateDetailsImpl:sendWhatsAppNotification:DAL:notificationTimeSlot:"+notificationTimeSlot+"; Exception:",e.getMessage(),e);
		}finally{
			try
			{
				if(stmt1!=null)
					stmt1.close();
				if(stmt2!=null)
					stmt2.close();
				if(stmt3!=null)
					stmt3.close();
				if(stmt4!=null)
					stmt4.close();
				if(con!=null)
					con.close();
				
			}
			catch(Exception e)
			{
				fLogger.fatal("WhatsAppUpdateDetailsImpl:sendWhatsAppNotification:DAL:notificationTimeSlot:"+notificationTimeSlot+"; Exception in closing the connection:"+e.getMessage(),e);
			}	
			
		}
		return status;
	}
	
	private static BigDecimal truncateDecimal(final double x, final int numberofDecimals) {
	    return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_DOWN);
	}
}
