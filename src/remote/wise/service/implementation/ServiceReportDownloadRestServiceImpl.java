package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.AssetUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;

public class ServiceReportDownloadRestServiceImpl {
	//aj20119610 changes done as a part of group based view
	public List<HashMap<String,Object>> getServiceCloserDetails(String loginTenancyId, List<Integer> customGroupIdList){

		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	
    	HashMap<String,Object> serviceDetails =null;
    	    	
    	ConnectMySQL connMySql = new ConnectMySQL();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<HashMap<String,Object>> finalOutput= new LinkedList<HashMap<String,Object>>();
		
		ListToStringConversion conversion = new ListToStringConversion();
		Session session = HibernateUtil.getSessionFactory().openSession();
		String query=null;
		try
		{
			conn = connMySql.getConnection();
			stmt = conn.createStatement();
			if(customGroupIdList!=null ||(customGroupIdList!=null && !(customGroupIdList.isEmpty()))){
				String customGroupIdListAsString = AssetUtil.getFormatedVinsToQuery(customGroupIdList);
				//ME100011178-20240415-Sai Divya-changed mobile to customer number and added customer name 
				query="select aes.Serial_Number, aes.Event_Severity, aes.event_generated_time, aes.event_closed_time," +
						"aes.serviceScheduleId,aes.ServiceName,aes.Service_type,aes.call_type,croe.Zone,croe.Profile,croe.Model,croe.tmh,croe.Pkt_Recd_TS,croe.Roll_Off_Date,croe.Installed_date,croe.Customer_Mobile,croe.Dealer_Name,croe.CustomerName from com_rep_oem_enhanced croe,(select * from asset_event ae , service_schedule ss   " +
						"where ae.service_schedule_id =ss.servicescheduleid and  ae.PartitionKey=1) aes inner join custom_asset_group_snapshot cags" +
						" ON cags.Asset_Id=aes.Serial_Number  and cags.Group_ID in("+customGroupIdListAsString+")  where aes.Active_Status=1 and aes.Event_type_ID=1 and cags.Asset_Id=croe.Serial_Number" +
						" group by aes.Serial_Number, aes.Event_Severity, aes.event_generated_time, aes.event_closed_time,aes.Service_type," +
						"aes.serviceScheduleId,aes.ServiceName,aes.Service_type";
			}else{
			List<Integer> userAccList = new LinkedList<Integer>();
			Query accountQ=session.createQuery("select a.account_id from AccountTenancyMapping a, AccountEntity b where b.status=true and a.account_id=b.account_id " +
					"and b.mappingCode = (select c.mappingCode from AccountEntity c where c.status=true and c.account_id=(select account_id from AccountTenancyMapping where tenancy_id in ("+loginTenancyId+")))");
			
			Iterator accItr = accountQ.list().iterator();
			/*	if(accItr.hasNext())
				{
					account = (AccountEntity)accItr.next();
				}*/
				
				while(accItr.hasNext())
				{
					AccountEntity account = (AccountEntity)accItr.next();
					
					userAccList.add(account.getAccount_id());
				}

				if(session!=null && session.isOpen())
				{
					session.close();
				}
				
			String userAccListAsString = conversion.getIntegerListString(userAccList).toString();
			
		/*
			String query="select aes.Serial_Number, aes.Event_Severity, aes.event_generated_time, aes.event_closed_time," +
					"aes.serviceScheduleId,aes.ServiceName,aes.Service_type,aes.call_type from (select * from asset_event ae , service_schedule ss   " +
					"where ae.service_schedule_id =ss.servicescheduleid and ss.service_type ='Free' and ae.PartitionKey=1) aes inner join asset_owner_snapshot aos" +
					" ON aos.Serial_Number=aes.Serial_Number  and aos.account_ID in("+userAccListAsString+")  where aes.Active_Status=1 and aes.Event_type_ID=1" +
					" group by aes.Serial_Number, aes.Event_Severity, aes.event_generated_time, aes.event_closed_time,aes.Service_type," +
					"aes.serviceScheduleId,aes.ServiceName,aes.Service_type";*/
			

		/*	String query="select aes.Serial_Number, aes.Event_Severity, aes.event_generated_time, aes.event_closed_time," +
					"aes.serviceScheduleId,aes.ServiceName,aes.Service_type,aes.call_type from (select * from asset_event ae , service_schedule ss   " +
					"where ae.service_schedule_id =ss.servicescheduleid and  ae.PartitionKey=1) aes inner join asset_owner_snapshot aos" +
					" ON aos.Serial_Number=aes.Serial_Number  and aos.account_ID in("+userAccListAsString+")  where aes.Active_Status=1 and aes.Event_type_ID=1" +
					" group by aes.Serial_Number, aes.Event_Severity, aes.event_generated_time, aes.event_closed_time,aes.Service_type," +
					"aes.serviceScheduleId,aes.ServiceName,aes.Service_type";*/
			//ME100011178-20240415-Sai Divya-changed mobile to customer number and added customer name 
			 query="select aes.Serial_Number, aes.Event_Severity, aes.event_generated_time, aes.event_closed_time," +
					"aes.serviceScheduleId,aes.ServiceName,aes.Service_type,aes.call_type,croe.Zone,croe.Profile,croe.Model,croe.tmh,croe.Pkt_Recd_TS,croe.Roll_Off_Date,croe.Installed_date,croe.Customer_Mobile,croe.Dealer_Name,croe.CustomerName from com_rep_oem_enhanced croe,(select * from asset_event ae , service_schedule ss   " +
					"where ae.service_schedule_id =ss.servicescheduleid and  ae.PartitionKey=1) aes inner join asset_owner_snapshot aos" +
					" ON aos.Serial_Number=aes.Serial_Number  and aos.account_ID in("+userAccListAsString+")  where aes.Active_Status=1 and aes.Event_type_ID=1 and aos.Serial_Number=croe.Serial_Number" +
					" group by aes.Serial_Number, aes.Event_Severity, aes.event_generated_time, aes.event_closed_time,aes.Service_type," +
					"aes.serviceScheduleId,aes.ServiceName,aes.Service_type";
			}//end of else
			iLogger.info("Executing query: "+query);
			rs = stmt.executeQuery(query);
			
			while(rs.next())
			{
				
					serviceDetails= new HashMap<String,Object>();
					
					String Serial_Number = null;
					if(rs.getObject("Serial_Number") != null){Serial_Number = (String) rs.getObject("Serial_Number");}				
					
					String Event_Severity = null;
					if(rs.getObject("Event_Severity") != null){Event_Severity = (String)rs.getObject("Event_Severity");}				
					
					String event_generated_time = null;
					if(rs.getTimestamp("event_generated_time") != null){
					event_generated_time= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("event_generated_time"));}				
					
					String event_closed_time = null;
					if(rs.getTimestamp("event_closed_time") != null){
						event_closed_time = (rs.getTimestamp("event_closed_time")).toString();
					}else{
						event_closed_time= "NA";
					}		
					
					String serviceScheduleId = null;
					if(rs.getString("serviceScheduleId") != null){ serviceScheduleId= String.valueOf(rs.getInt("serviceScheduleId"));}	
					
					String ServiceName = null;
					if(rs.getObject("ServiceName") != null){ServiceName= (String)(rs.getObject("ServiceName"));}
					
					String Service_type= null;
					if(rs.getObject("Service_type") != null){Service_type= (String)(rs.getObject("Service_type"));}					
					
					String warrantyType= null;
					if(rs.getObject("call_type") != null){warrantyType = (String)(rs.getObject("call_type"));}					
					
					String Zone = null;
					if(rs.getObject("Zone") != null){Zone = (String)rs.getObject("Zone");}							
					
					String Profile = null;
					if(rs.getObject("Profile") !=null){Profile=(String)rs.getObject("Profile");}					
					
					String Model= null;
					if(rs.getObject("Model") != null){Model=(String)rs.getObject("Model");}						
					
					String Machine_Hrs=null;
					if(rs.getObject("tmh") != null){ Machine_Hrs=(rs.getObject("tmh")).toString();}						
					
					String Pkt_Received_Date=null;
					if(rs.getObject("Pkt_Recd_TS") != null){Pkt_Received_Date=(rs.getObject("Pkt_Recd_TS")).toString();}					
					
					String Machine_Roll_off_Date=null;
					if(rs.getObject("Roll_Off_Date") != null){ Machine_Roll_off_Date=(rs.getObject("Roll_Off_Date")).toString();}					
					
					String Machine_Installation_Date=null;
					if(rs.getObject("Installed_date") != null){Machine_Installation_Date=(rs.getObject("Installed_date")).toString();}					
					
					String Customer_No=null;
					if(rs.getObject("Customer_Mobile") !=null){ Customer_No=(String)rs.getObject("Customer_Mobile");}	
					
					String Dealer_Name = null;
					if(rs.getObject("Dealer_Name") !=null){ Dealer_Name=(String)rs.getObject("Dealer_Name");}	
					
					String CustomerName=null;
					if(rs.getObject("CustomerName") !=null){ CustomerName=(String)rs.getObject("CustomerName");}	
					
					serviceDetails.put("Serial_Number", Serial_Number);
					serviceDetails.put("Event_Severity", Event_Severity);
					serviceDetails.put("event_generated_time", event_generated_time);
					serviceDetails.put("event_closed_time", event_closed_time);
					serviceDetails.put("serviceScheduleId", serviceScheduleId);
					serviceDetails.put("ServiceName", ServiceName);
					serviceDetails.put("Service_type", Service_type);
					serviceDetails.put("extendedWarrantyType", warrantyType);
					
					serviceDetails.put("Zone", Zone);
					serviceDetails.put("Model", Model);
					serviceDetails.put("Profile", Profile);
					serviceDetails.put("tmh", Machine_Hrs);
					serviceDetails.put("Pkt_Recd_TS", Pkt_Received_Date);
					serviceDetails.put("Roll_Off_Date", Machine_Roll_off_Date);
					serviceDetails.put("Installed_date", Machine_Installation_Date);
					serviceDetails.put("Customer_Mobile", Customer_No);	
					serviceDetails.put("Dealer_Name", Dealer_Name);
					serviceDetails.put("CustomerName", CustomerName);
					
					finalOutput.add(serviceDetails);
					
					
			}
			
			
		}
		catch(Exception e)
		{
			fLogger.fatal("ServiceDetailsBO:getServiceCloserDetailsFromMysql:::Exception"+e.getMessage());
			System.out.println(e.getMessage());
		}
		finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			
			if(stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		}		
		
		return finalOutput;
	}
}
