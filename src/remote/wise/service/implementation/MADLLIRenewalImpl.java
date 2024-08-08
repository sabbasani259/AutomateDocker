
/**
 * ME100003464 : 20220921 : Dhiraj K : MaDashboard machine count issue when a machine group user logs in
 * CR334 : 20221118 : Dhiraj K : Changes for Billing and ARD table update
 */

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

public class MADLLIRenewalImpl {

	
	public List<HashMap<String,String>>  getRenewalCount(String accountIdList,String filter, String dateFilter, String downloadFlag, String userId){
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
		String mainOrderByQuery="";
		String mainGroupByQuery="";
		String mainQuery="";
		List<HashMap<String,String>> respList = new LinkedList<HashMap<String,String>>();
		LinkedHashMap<String, String> recordMap = new LinkedHashMap<String, String>();
		try
		{
			conn = connMySql.getConnection();
			stmt = conn.createStatement();
			
			
			//if(!checkGroupUser(userId)){//ME100003464.o
			query=" (select a.serial_number,ac.account_name as customer_name,ac1.account_name as dealer_name,a.Renewal_Date,a.Product_ID from asset a, "+
					" asset_renewal_data ard,account ac,account ac1,asset_owner_snapshot aos where aos.account_id in ("+accountIdList+") and a.status =1 "+
					" and a.Retrofit_Flag = 0 and a.serial_number = aos.serial_number and a.primary_owner_id = ac.account_id and ac1.account_id = ifnull(ac.parent_id,ac.account_id) and ard.serial_number = a.serial_number and a.renewal_flag=1 ";
			//DF20200904 : Zakir : Checking if Date Filter if applicable--------------------------------------------------------
			/**
			 * Note:- Previously, the date(ard.subscribed_from) was set to <= '2019-08-14
			 * Reason for this is unknown
			 */
			if(dateFilter != null) {
				//apply dateFilter
				//query = query + " and date(ard.subscribed_from) < '" + dateFilter + "'"; //CR334.o
				query = query + " and date(ard.SubsStartDate) < '" + dateFilter + "'"; //CR334.n
			}
			//----------------------------------------------------------------------------------------------------------------
			query = query + " group by ard.serial_number) a "
					+" LEFT OUTER JOIN "
					+ " (SELECT p.Product_ID, ag.Asseet_Group_Name, aty.Asset_Type_Name FROM products p "
					+ " INNER JOIN asset_group ag ON ag.Asset_Group_ID = p.Asset_Group_ID "
					+ " INNER JOIN asset_type aty ON aty.Asset_Type_ID = p.Asset_Type_ID "
					+ " ) bb ON a.Product_ID = bb.Product_ID ";
			//ME100003464.so
			/*}
			else{
			query=" (select a.serial_number,ac.account_name as customer_name,ac1.account_name as dealer_name,a.Renewal_Date,a.Product_ID from asset a, "+
					" asset_renewal_data ard,account ac,custom_asset_group_snapshot cgas where cgas.user_Id in ('"+userId+"') and a.status =1 "+
					" and a.Retrofit_Flag = 0 and a.serial_number = cgas.Asset_Id and a.primary_owner_id = ac.account_id and ac1.account_id = ifnull(ac.parent_id,ac.account_id) and ard.serial_number = a.serial_number and a.renewal_flag=1 ";
			//DF20200904 : Zakir : Checking if Date Filter if applicable--------------------------------------------------------
			if(dateFilter != null) {
				//apply dateFilter
				query = query + " and date(ard.subscribed_from) < '" + dateFilter + "'";
			}
			//----------------------------------------------------------------------------------------------------------------
			query = query + " group by ard.serial_number) a "
			
					+" LEFT OUTER JOIN "
					+ " (SELECT p.Product_ID, ag.Asseet_Group_Name, aty.Asset_Type_Name FROM products p "
					+ " INNER JOIN asset_group ag ON ag.Asset_Group_ID = p.Asset_Group_ID "
					+ " INNER JOIN asset_type aty ON aty.Asset_Type_ID = p.Asset_Type_ID "
					+ " ) bb ON a.Product_ID = bb.Product_ID  ";
			}*///ME100003464.eo
			
			mainGroupByQuery = " group by Asseet_Group_Name ";
			//mainOrderByQuery=	" ORDER BY ams1.transactionTime DESC";
			
			if(downloadFlag.equalsIgnoreCase("true")){
				topSelectQuery="select serial_number,customer_name,dealer_name,Asseet_Group_Name,Asset_Type_Name,Renewal_Date from ";
				mainQuery =topSelectQuery+query;
				iLogger.info("MADLLIRenewalImpl:getRenewedMachine: Query "+mainQuery);
				rs=stmt.executeQuery(mainQuery);
				while(rs.next()){
					recordMap = new LinkedHashMap<String, String>();
					recordMap.put("Serial_Number",rs.getString("Serial_Number"));
					recordMap.put("customer_name",rs.getString("customer_name"));
					recordMap.put("dealer_name",rs.getString("dealer_name"));
					recordMap.put("Asseet_Group_Name",rs.getString("Asseet_Group_Name"));
					recordMap.put("Asset_Type_Name",rs.getString("Asset_Type_Name"));
					recordMap.put("Renewal_Date",rs.getString("Renewal_Date"));
					respList.add(recordMap);
				}
				
			}
			else if(filter==null || filter.isEmpty()){
				/*topSelectQuery=" SELECT Count(*) as count FROM ";
				mainQuery =topSelectQuery+query+mainOrderByQuery;
				rs=stmt.executeQuery(mainQuery);
				while(rs.next()){
					recordMap.put("All",String.valueOf(rs.getInt("count")));
				}*/
				rs=null;
				mainQuery=null;
				topSelectQuery=" select count(*) as count,ifnull(Asseet_Group_Name,'NA') as Asseet_Group_Name from  ";
				mainQuery =topSelectQuery+query+mainGroupByQuery;
				iLogger.info("MADLLIRenewalImpl:getRenewedMachine: Query "+mainQuery);
				int count=0;
				rs=stmt.executeQuery(mainQuery);
				while(rs.next()){
					recordMap.put(rs.getString("Asseet_Group_Name"),String.valueOf(rs.getInt("count")));
					count += rs.getInt("count");
				}
				recordMap.put("All",String.valueOf(count));
				respList.add(recordMap);
			}
			else{
				topSelectQuery="SELECT count(*) as count, ifnull(Asseet_Group_Name,'NA') as Asseet_Group_Name FROM ";
				mainwhereQuery="where Asseet_Group_Name in("+filter+")";
				mainQuery =topSelectQuery+query+mainwhereQuery+mainGroupByQuery;
				iLogger.info("MADLLIRenewalImpl:getRenewedMachine: Query "+mainQuery);
				rs=stmt.executeQuery(mainQuery);
				while(rs.next()){
					recordMap.put(rs.getString("Asseet_Group_Name"),String.valueOf(rs.getInt("count")));
				}
				respList.add(recordMap);
			}
				
			
		}
		catch(Exception e)
		{
			fLogger.fatal("MADLLIRenewalImpl:getRenewedMachine::Exception"+e.getMessage());
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
