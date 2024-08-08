//ME100003464 : 20220921 : Dhiraj K : MaDashboard machine count issue when a machine group user logs in
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

public class MADREDAlertCountImpl {

	
	public List<HashMap<String,String>>  getRedAlertCount(String accountIdList,String filter,String dateFilter, String downloadFlag, String userId){
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	ConnectMySQL connMySql = new ConnectMySQL();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String CMH="VIN Not Registered";
		String topSelectQuery="";
		String query="";	
		String mainwhereQuery="";
		String mainJoinQuery="";
		String mainGroupByQuery="";
		String mainQuery="";
		List<HashMap<String,String>> respList = new LinkedList<HashMap<String,String>>();
		LinkedHashMap<String, String> recordMap = new LinkedHashMap<String, String>();
		try
		{
			conn = connMySql.getConnection();
			stmt = conn.createStatement();
			
			
			//if(!checkGroupUser(userId)){//ME100003464.o
			query=" from asset_event ae  inner join asset_owner_snapshot aos ON aos.Serial_Number=ae.Serial_Number  and aos.account_ID in("+accountIdList+") inner Join account acc on(aos.Account_ID=acc.account_ID)  inner join account acc1 on (acc1.account_ID=ifnull(acc.Parent_ID,acc.account_ID))  where ae.Active_Status=1 and ae.PartitionKey =1 and ae.Event_Severity='RED' ";
			//ME100003464.so
			/*}
			else{
				query=" from asset_event ae inner join custom_asset_group_snapshot aos ON aos.Asset_Id=ae.Serial_Number  and aos.user_Id in('"+userId+"')  where ae.Active_Status=1 and ae.PartitionKey =1 and ae.Event_Severity='RED' ";
			}*///ME100003464.eo
			
			//DF20200904 : Zakir : Checking if Date Filter if applicable--------------------------------------------------------
			if(dateFilter != null) {
				//apply dateFilter
				query = query + " and ae.Event_Generated_Time < '" + dateFilter + "'";
			}
			//----------------------------------------------------------------------------------------------------------------
			
			mainGroupByQuery = " group by  ae.Event_Severity ";
			mainJoinQuery=") ae LEFT OUTER JOIN asset_group ag ON ag.Asset_Group_ID = ae.Asset_Group_ID " +
					"LEFT OUTER JOIN asset_type aty ON aty.Asset_Type_ID = ae.Asset_Type_ID " +
					"Left outer join business_event be on be.Event_ID=ae.Event_ID " +
					"Left Outer join event_type et on et.Event_Type_ID=ae.Event_Type_ID ";
			if(downloadFlag.equalsIgnoreCase("true")){
				topSelectQuery="select Serial_Number,Asseet_Group_Name,Asset_Type_Name,Event_Type_Name,Event_Name,customer_name,dealer_name,Event_Generated_Time  from  (select  ae.Event_Generated_Time,aos.Serial_Number,ae.Event_Severity, aos.Asset_Group_ID,aos.Asset_Type_ID,Event_Type_ID ,Event_ID, acc.account_name as customer_name , acc1.account_name as dealer_name ";
				mainQuery =topSelectQuery+query+mainJoinQuery;
				iLogger.info("MADREDAlertCountImpl:getRedAlertCount:: Query: "+mainQuery);
				rs=stmt.executeQuery(mainQuery);
				while(rs.next()){
					recordMap = new LinkedHashMap<String, String>();
					recordMap.put("Serial_Number",rs.getString("Serial_Number"));
					recordMap.put("Asseet_Group_Name",rs.getString("Asseet_Group_Name"));
					recordMap.put("Asset_Type_Name",rs.getString("Asset_Type_Name"));
					recordMap.put("Event_Type_Name",rs.getString("Event_Type_Name"));
					recordMap.put("Event_Name",rs.getString("Event_Name"));
					recordMap.put("customer_name",rs.getString("customer_name"));
					recordMap.put("dealer_name",rs.getString("dealer_name"));
					recordMap.put("Event_Generated_Time",rs.getString("Event_Generated_Time"));
					respList.add(recordMap);
				}
				
			}
			else if(filter==null || filter.isEmpty()){
				int count =0;
				topSelectQuery="select ifnull(asseet_group_name,'NA') as Asseet_Group_Name, alertCount  from (select  Event_Type_ID,Event_ID,ae.Event_Severity, aos.Asset_Group_ID, aos.Asset_Type_ID,count(*) as alertCount ";
				mainQuery =topSelectQuery+query+mainGroupByQuery+" ,aos.Asset_Group_ID "+mainJoinQuery;
				iLogger.info("MADREDAlertCountImpl:getRedAlertCount:: Query: "+mainQuery);
				rs=stmt.executeQuery(mainQuery);
				while(rs.next()){
					recordMap.put(rs.getString("Asseet_Group_Name"),String.valueOf(rs.getInt("alertCount")));
					count +=rs.getInt("alertCount"); 
				}
				recordMap.put("All",String.valueOf(count));
				respList.add(recordMap);
			}
			else{
				topSelectQuery="select ifnull(asseet_group_name,'NA') as Asseet_Group_Name, alertCount  from (select ae.Event_Severity, ae.Event_ID, ae.Event_Type_ID, aos.Asset_Type_ID, aos.Asset_Group_ID, count(*) as alertCount ";
				mainwhereQuery="where Asseet_Group_Name in("+filter+")";
				mainQuery =topSelectQuery+query+mainGroupByQuery+" ,aos.Asset_Group_ID "+mainJoinQuery+mainwhereQuery;
				iLogger.info("MADREDAlertCountImpl:getRedAlertCount:: Query: "+mainQuery);
				rs=stmt.executeQuery(mainQuery);
				while(rs.next()){
					recordMap.put(rs.getString("Asseet_Group_Name"),String.valueOf(rs.getInt("alertCount")));
				}
				respList.add(recordMap);
			}
				
			
		}
		catch(Exception e)
		{
			fLogger.fatal("MADREDAlertCountImpl:getRedAlertCount:::Exception"+e.getMessage());
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
		return respList;
		
	}
	
		//DF20190523:Abhishek::Metod added to check the user in group user table.
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
