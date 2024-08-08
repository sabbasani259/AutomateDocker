//ME100003464 : 20220921 : Dhiraj K : MaDashboard machine count issue when a machine group user logs in
package remote.wise.businessobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;

public class MAServiceDueOverDueCountBO {
	
	public List<HashMap<String,String>>  getServiceDueCount(String accountIdList,String filter,String dateFilter, String downloadFlag,String userId)
	{
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;    	
    	ConnectMySQL connMySql = new ConnectMySQL();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String topSelectQuery="";
		String query="";
		String mainwhereQuery="";
		String mainOrderByQuery="";
		String mainGroupByQuery="";
		String mainQuery="";
		List<HashMap<String,String>> respList = new LinkedList<HashMap<String,String>>();
		LinkedHashMap<String, String> recordMap =new LinkedHashMap<String, String>();
		try
		{
			conn = connMySql.getConnection();
			stmt = conn.createStatement();
			
			//if(!checkGroupUser(userId)){//ME100003464.o
				query=" (select ae.Serial_Number,ae.Event_Type_ID,ae.Event_ID,acc.Account_Name as Customer_Name," +
						" acc1.account_name as Dealer_Name,ss.serviceName as Service_Name,ae.Event_Generated_Time as Alert_Generated_Time," +
						" a.Product_ID from asset_event ae"+
						" INNER JOIN asset_owner_snapshot aos ON aos.Serial_Number = ae.Serial_Number AND aos.account_ID IN ("+accountIdList+")"+
						" INNER JOIN asset a ON (a.Serial_Number = aos.Serial_Number AND a.Status = 1 And a.Product_ID is not null)"+
						" INNER JOIN account acc on(aos.Account_ID=acc.account_ID) "+
						" INNER JOIN account acc1 on (acc1.account_ID=ifnull( acc.Parent_ID,acc.account_ID)) "+
						" INNER JOIN service_schedule ss on (ae.Service_Schedule_ID = ss.serviceScheduleId AND a.Serial_Number = ae.Serial_Number)"+
						" WHERE ae.Active_Status = 1 AND ae.PartitionKey = 1 AND ae.Event_ID in(1,2)";
				
				//DF20200904 : Zakir : Checking if Date Filter if applicable--------------------------------------------------------
				if(dateFilter != null) {
					//apply dateFilter
					query = query + " AND ae.Event_Generated_Time < '" + dateFilter + "'";
				}
				//----------------------------------------------------------------------------------------------------------------
				query = query +	") aa"+
				
						" LEFT OUTER JOIN"+
						" (SELECT p.Product_ID, ag.Asseet_Group_Name,aty.Asset_Type_Name FROM products p"+
						" INNER JOIN asset_group ag ON ag.Asset_Group_ID = p.Asset_Group_ID"+
						" INNER JOIN asset_type aty ON aty.Asset_Type_ID = p.Asset_Type_ID"+
						" INNER JOIN engine_type et ON et.Engine_Type_id = p.Engine_Type_id) bb"+
						" ON aa.Product_ID = bb.Product_ID";
				//ME100003464.so
				//}
			/*else {		
				query=" (select ae.Serial_Number,ae.Event_Type_ID,ae.Event_ID,acc.Account_Name as Customer_Name,acc1.account_name as Dealer_Name," +
						" ss.serviceName as Service_Name,ae.Event_Generated_Time as Alert_Generated_Time,a.Product_ID from asset_event ae"+
						" INNER JOIN custom_asset_group_snapshot cags ON ae.Serial_Number = cags.Asset_Id AND cags.user_Id IN('"+userId+"')"+
						" INNER JOIN asset a ON (a.Serial_Number = ae.Serial_Number AND a.Status = 1 And a.Product_ID is not null)"+
						" INNER JOIN account acc on(aos.Account_ID=acc.account_ID)"+
						" INNER JOIN account acc1 on (acc1.account_ID=ifnull( acc.Parent_ID,acc.account_ID))"+
						" INNER JOIN service_schedule ss on (ae.Service_Schedule_ID = ss.serviceScheduleId AND a.Serial_Number = ae.Serial_Number)"+
						" WHERE ae.Active_Status = 1 AND ae.PartitionKey = 1 AND ae.Event_ID in(1,2)) aa"+
						" LEFT OUTER JOIN"+
						" (SELECT p.Product_ID, ag.Asseet_Group_Name,aty.Asset_Type_Name FROM products p"+
						" INNER JOIN asset_group ag ON ag.Asset_Group_ID = p.Asset_Group_ID"+
						" INNER JOIN asset_type aty ON aty.Asset_Type_ID = p.Asset_Type_ID"+
						" INNER JOIN engine_type et ON et.Engine_Type_id = p.Engine_Type_id) bb"+
						" ON aa.Product_ID = bb.Product_ID";
			
			}*///ME100003464.eo
			mainGroupByQuery = " group by Asseet_Group_Name ";
			mainOrderByQuery=	" ORDER BY aa.Event_ID ASC";
			
			if(downloadFlag.equalsIgnoreCase("true")){
				topSelectQuery="SELECT aa.*, bb.Asseet_Group_Name,bb.Asset_Type_Name FROM ";
				mainQuery =topSelectQuery+query+mainOrderByQuery;
				iLogger.info("MAServiceDueOverDueBO:getServiceDueCountFromMysql: Query "+mainQuery);
				rs=stmt.executeQuery(mainQuery);
				while(rs.next()){
					recordMap = new LinkedHashMap<String, String>();
					recordMap.put("Serial_Number",rs.getString("Serial_Number"));
					recordMap.put("Asseet_Group_Name",rs.getString("Asseet_Group_Name"));
					recordMap.put("Asset_Type_Name",rs.getString("Asset_Type_Name"));
					recordMap.put("Dealer_Name",rs.getString("Dealer_Name"));
					recordMap.put("Customer_Name",rs.getString("Customer_Name"));	
					recordMap.put("Service_Name",rs.getString("Service_Name"));
					recordMap.put("Alert_Generated_Time",rs.getString("Alert_Generated_Time"));				
					respList.add(recordMap);
				}
				
			} 
			else if(filter==null || filter.isEmpty()){
				topSelectQuery="SELECT Count(*) as count FROM ";
				mainQuery =topSelectQuery+query;
				iLogger.info("MAServiceDueOverDueBO:getServiceDueCountFromMysql: Query "+mainQuery);
				rs=stmt.executeQuery(mainQuery);
				while(rs.next()){
					recordMap.put("All",String.valueOf(rs.getInt("count")));					
				}
				rs=null;
				mainQuery=null;
				topSelectQuery="SELECT count(*) as count, ifnull(Asseet_Group_Name,'NA') as Asseet_Group_Name FROM ";
				mainQuery =topSelectQuery+query+mainGroupByQuery;
				iLogger.info("MAServiceDueOverDueBO:getServiceDueCountFromMysql: Query "+mainQuery);
				rs=stmt.executeQuery(mainQuery);
				while(rs.next()){
					recordMap.put(rs.getString("Asseet_Group_Name"),String.valueOf(rs.getInt("count")));
				}
				respList.add(recordMap);
			}
			else{
				topSelectQuery="SELECT count(*) as count, Asseet_Group_Name FROM ";
				mainwhereQuery=" where Asseet_Group_Name in("+filter+")";
				mainQuery =topSelectQuery+query+mainwhereQuery+mainGroupByQuery;
				iLogger.info("MAServiceDueOverDueBO:getServiceDueCountFromMysql: Query "+mainQuery);
				rs=stmt.executeQuery(mainQuery);
				while(rs.next()){
					recordMap = new LinkedHashMap<String, String>();
					recordMap.put(rs.getString("Asseet_Group_Name"),String.valueOf(rs.getInt("count")));
					respList.add(recordMap);
				}
				
			}
		}
		catch(Exception e)
		{
			fLogger.fatal("MAServiceDueOverDueBO:getServiceDueCountFromMysql:::Exception"+e.getMessage());			
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
		return respList;		
	}
	
	public List<HashMap<String,String>>  getServiceOverDueCount(String accountIdList,String filter,String dateFilter, String downloadFlag,String userId)
	{
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;    	
    	ConnectMySQL connMySql = new ConnectMySQL();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String topSelectQuery="";
		String query="";
		String mainwhereQuery="";
		String mainOrderByQuery="";
		String mainGroupByQuery="";
		String mainQuery="";
		List<HashMap<String,String>> respList = new LinkedList<HashMap<String,String>>();
		LinkedHashMap<String, String> recordMap =new LinkedHashMap<String, String>();
		try
		{
			conn = connMySql.getConnection();
			stmt = conn.createStatement();
			//if(!checkGroupUser(userId)){//ME100003464.o
				query=" (select ae.Serial_Number,ae.Event_Type_ID,ae.Event_ID,acc.Account_Name as Customer_Name,acc1.account_name as Dealer_Name," +
						" ss.serviceName as Service_Name,ae.Event_Generated_Time as Alert_Generated_Time,a.Product_ID from asset_event ae"+
						" INNER JOIN asset_owner_snapshot aos ON aos.Serial_Number = ae.Serial_Number AND aos.account_ID IN ("+accountIdList+")"+
						" INNER JOIN asset a ON (a.Serial_Number = aos.Serial_Number AND a.Status = 1 And a.Product_ID is not null)"+
						" INNER JOIN account acc on(aos.Account_ID=acc.account_ID)"+
						" INNER JOIN account acc1 on (acc1.account_ID=ifnull( acc.Parent_ID,acc.account_ID))"+
						" INNER JOIN service_schedule ss on (ae.Service_Schedule_ID = ss.serviceScheduleId AND a.Serial_Number = ae.Serial_Number)"+
						" WHERE ae.Active_Status = 1 AND ae.PartitionKey = 1 AND ae.Event_ID in(3)";
						
				
				//DF20200904 : Zakir : Checking if Date Filter if applicable--------------------------------------------------------
				if(dateFilter != null) {
					//apply dateFilter
					query = query + " AND ae.Event_Generated_Time < '" + dateFilter + "'";
				}
				//----------------------------------------------------------------------------------------------------------------
						
						
				query = query + ") aa"+
						" LEFT OUTER JOIN"+
						" (SELECT p.Product_ID, ag.Asseet_Group_Name,aty.Asset_Type_Name FROM products p"+
						" INNER JOIN asset_group ag ON ag.Asset_Group_ID = p.Asset_Group_ID"+
						" INNER JOIN asset_type aty ON aty.Asset_Type_ID = p.Asset_Type_ID"+
						" INNER JOIN engine_type et ON et.Engine_Type_id = p.Engine_Type_id) bb"+
						" ON aa.Product_ID = bb.Product_ID";
				//ME100003464.so
				/*}else {
				query=" (select ae.Serial_Number,ae.Event_Type_ID,ae.Event_ID,acc.Account_Name as Customer_Name,acc1.account_name as Dealer_Name," +
						" ss.serviceName as Service_Name,ae.Event_Generated_Time as Alert_Generated_Time,a.Product_ID from asset_event ae"+
						" INNER JOIN custom_asset_group_snapshot cags ON ae.Serial_Number = cags.Asset_Id AND cags.user_Id IN('"+userId+"')"+
						" INNER JOIN asset a ON (a.Serial_Number = ae.Serial_Number AND a.Status = 1 And a.Product_ID is not null)"+
						" INNER JOIN account acc on(aos.Account_ID=acc.account_ID)"+
						" INNER JOIN account acc1 on (acc1.account_ID=ifnull( acc.Parent_ID,acc.account_ID))"+
						" INNER JOIN service_schedule ss on (ae.Service_Schedule_ID = ss.serviceScheduleId AND a.Serial_Number = ae.Serial_Number)"+
						" WHERE ae.Active_Status = 1 AND ae.PartitionKey = 1 AND ae.Event_ID in(3)) aa"+
						" LEFT OUTER JOIN"+
						" (SELECT p.Product_ID, ag.Asseet_Group_Name,aty.Asset_Type_Name FROM products p"+
						" INNER JOIN asset_group ag ON ag.Asset_Group_ID = p.Asset_Group_ID"+
						" INNER JOIN asset_type aty ON aty.Asset_Type_ID = p.Asset_Type_ID"+
						" INNER JOIN engine_type et ON et.Engine_Type_id = p.Engine_Type_id) bb"+
						" ON aa.Product_ID = bb.Product_ID";
			
			}*///ME100003464.eo
			mainGroupByQuery = " group by Asseet_Group_Name ";
			mainOrderByQuery=	" ORDER BY aa.event_id ASC";
			
			if(downloadFlag.equalsIgnoreCase("true")){
				topSelectQuery="SELECT aa.*, bb.Asseet_Group_Name,bb.Asset_Type_Name FROM ";
				mainQuery =topSelectQuery+query+mainOrderByQuery;
				rs=stmt.executeQuery(mainQuery);
				iLogger.info("MAServiceDueOverDueBO:getServiceOverDueCountFromMysql: Query "+mainQuery);
				while(rs.next()){
					recordMap = new LinkedHashMap<String, String>();
					recordMap.put("Serial_Number",rs.getString("Serial_Number"));
					recordMap.put("Asseet_Group_Name",rs.getString("Asseet_Group_Name"));
					recordMap.put("Asset_Type_Name",rs.getString("Asset_Type_Name"));
					recordMap.put("Dealer_Name",rs.getString("Dealer_Name"));
					recordMap.put("Customer_Name",rs.getString("Customer_Name"));	
					recordMap.put("Service_Name",rs.getString("Service_Name"));
					recordMap.put("Alert_Generated_Time",rs.getString("Alert_Generated_Time"));				
					respList.add(recordMap);
				}
				
			}
			else if(filter.isEmpty()||filter==null){
				topSelectQuery="SELECT Count(*) as count FROM ";
				mainQuery =topSelectQuery+query;
				rs=stmt.executeQuery(mainQuery);
				iLogger.info("MAServiceDueOverDueBO:getServiceOverDueCountFromMysql: Query "+mainQuery);
				while(rs.next()){
					recordMap.put("All",String.valueOf(rs.getInt("count")));					
				}
				rs=null;
				mainQuery=null;
				topSelectQuery="SELECT count(*) as count, ifnull(Asseet_Group_Name,'NA') as Asseet_Group_Name FROM ";
				mainQuery =topSelectQuery+query+mainGroupByQuery;
				rs=stmt.executeQuery(mainQuery);
				iLogger.info("MAServiceDueOverDueBO:getServiceOverDueCountFromMysql: Query "+mainQuery);
				while(rs.next()){
					recordMap.put(rs.getString("Asseet_Group_Name"),String.valueOf(rs.getInt("count")));
				}
				respList.add(recordMap);
			}
			else{
				topSelectQuery="SELECT count(*) as count,Asseet_Group_Name FROM ";
				mainwhereQuery=" where Asseet_Group_Name in("+filter+")";
				mainQuery =topSelectQuery+query+mainwhereQuery+mainGroupByQuery;
				rs=stmt.executeQuery(mainQuery);
				iLogger.info("MAServiceDueOverDueBO:getServiceOverDueCountFromMysql: Query "+mainQuery);
				while(rs.next()){
					recordMap = new LinkedHashMap<String, String>();
					recordMap.put(rs.getString("Asseet_Group_Name"),String.valueOf(rs.getInt("count")));
					respList.add(recordMap);
				}
				
			}
		}
		catch(Exception e)
		{
			fLogger.fatal("MAServiceDueOverDueBO:getServiceOverDueCountFromMysql:::Exception"+e.getMessage());			
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
		return respList;		
	}
	
	//20190809:Jayanthi::Metod added to check the user in group user table.
	private boolean checkGroupUser(String loginId) {
		Session session=null;
		boolean status=false;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		try{
		List<Integer> groupIdList = new LinkedList<Integer>();		 
		session = HibernateUtil.getSessionFactory().openSession();
		Query queryGroupUser = session.createQuery("from GroupUserMapping where contact_id ='"+loginId+"'");
		Iterator groupUserItr = queryGroupUser.list().iterator();
		status= groupUserItr.hasNext();
		}
		catch(Exception e)
		{
			fLogger.fatal(" Exception in retriving data from Group user:"+e);
			
		}
		finally
		{
			try
			{
			if(session!=null && session.isOpen())
			{
				session.close();
			}
			}
			catch(Exception e){
				fLogger.fatal("Exception in closing the AssetDashboard session::"+e);
			}
		
		}
		long endTime = System.currentTimeMillis();
		iLogger.info("Time required to fetch the record from group user for loginId::"+loginId+" in ms :"+(endTime-startTime));
		return status;
	}
}
