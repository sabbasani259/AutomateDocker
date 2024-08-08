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

public class MADMAchineCountImpl {

	public List<HashMap<String, String>> getMachineCount(String accountIdList, String filter, String dateFilter,
			String downloadFlag, String userId) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		ConnectMySQL connMySql = new ConnectMySQL();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String topSelectQuery = "";
		String query = "";
		String mainwhereQuery = "";
		String mainGroupByQuery = "";
		String mainQuery = "";
		List<HashMap<String, String>> respList = new LinkedList<HashMap<String, String>>();
		LinkedHashMap<String, String> recordMap = new LinkedHashMap<String, String>();
		try {
			conn = connMySql.getConnection();
			stmt = conn.createStatement();

			//DF20190927:Abhishek::Modified the query as per the logic of Fleet tab Total count query.
			//if(!checkGroupUser(userId)){//ME100003464.o
			/*query=" (SELECT ams.Serial_Number,ams.Latest_Transaction_Timestamp AS transactionTime, "+
					" a.Product_ID,aos.account_id ,acc.Account_Name as customer_name,acc.parent_id,acc1.account_name as dealer_name "+
					" FROM asset_monitoring_snapshot ams INNER JOIN asset_owner_snapshot aos ON (ams.Serial_Number = aos.Serial_Number"+
							" AND aos.Account_ID IN ("+accountIdList+")"+
							" AND ams.Latest_Transaction_Timestamp >= aos.Ownership_Start_Date)"+
					" inner Join account acc on(aos.Account_ID=acc.account_ID) "+
					" inner join account acc1 on (acc1.account_ID=ifnull(acc.Parent_ID,acc.account_ID)) "+
					 " INNER JOIN asset a ON (a.Serial_Number = ams.Serial_Number AND a.Status = 1 And a.Product_ID is not null)"+
			" WHERE ams.Latest_Transaction_Timestamp > '2014-01-01 00:00:00'"+
			" ORDER BY ams.Latest_Transaction_Timestamp DESC) AS ams1"+
			" INNER JOIN asset_profile ap ON ap.serialNumber = ams1.Serial_Number"+
			" LEFT OUTER JOIN"+
			" (SELECT p.Product_ID, ag.Asseet_Group_Name,aty.Asset_Type_Name FROM products p"+
				" INNER JOIN asset_group ag ON ag.Asset_Group_ID = p.Asset_Group_ID"+
				" INNER JOIN asset_type aty ON aty.Asset_Type_ID = p.Asset_Type_ID"+
				" INNER JOIN engine_type et ON et.Engine_Type_id = p.Engine_Type_id) bb ON ams1.Product_ID = bb.Product_ID"+
			" LEFT OUTER JOIN asset_event_snapshot aes ON ams1.Serial_Number = aes.serialNumber ";*/

			query = "select aos.Serial_Number,acc.Account_Name as customer_name, acc1.account_name as dealer_name,ifnull(bb.Asseet_Group_Name,'NA') "
					+ "as Asseet_Group_Name,ifnull(bb.Asset_Type_Name,'NA') as Asset_Type_Name from "
					+ "(  (select Serial_Number,Account_ID from asset_owner_snapshot where account_id in " + "("
					+ accountIdList + ") ";

			//DF20200904 : Zakir : Checking if Date Filter if applicable--------------------------------------------------------
			if (dateFilter != null) {
				//query = query + "AND YEAR(Ownership_Start_Date) = " + year + " AND MONTH(Ownership_Start_Date) = " + month;
				query = query + "AND Ownership_Start_Date < '" + dateFilter + "'";
			}
			//------------------------------------------------------------------------------------------------------------------		

			query = query + ") " + " aos  inner join  "
					+ " (Select Serial_Number,ifnull(Product_ID,'NA') as Product_ID from asset where status =1) a  "
					+ " on aos.serial_number = a.serial_number ) inner Join account acc on(aos.Account_ID=acc.account_ID) "
					+ " inner join account acc1 on (acc1.account_ID=ifnull(acc.Parent_ID,acc.account_ID)) "
					+ " LEFT OUTER JOIN (SELECT p.Product_ID, ifnull(ag.Asseet_Group_Name,'NA') "
					+ " as Asseet_Group_Name,ifnull(aty.Asset_Type_Name,'NA') as Asset_Type_Name FROM products p "
					+ " INNER JOIN asset_group ag ON ag.Asset_Group_ID = p.Asset_Group_ID INNER JOIN asset_type aty "
					+ " ON aty.Asset_Type_ID = p.Asset_Type_ID INNER JOIN engine_type et ON et.Engine_Type_id = p.Engine_Type_id) bb "
					+ " ON a.Product_ID = bb.Product_ID";
			//ME100003464.so
			//}
			/*else{
			query="(SELECT ams.Serial_Number,ams.Latest_Transaction_Timestamp AS transactionTime, "+
					" a.Product_ID,aos.account_id ,acc.Account_Name as customer_name,acc.parent_id,acc1.account_name as dealer_name "+
						" from asset_monitoring_snapshot ams  inner join custom_asset_group_snapshot cags on(ams.Serial_Number = cags.Asset_Id "+
								" and cags.user_Id in('"+userId+"')) "+
						" inner Join account acc on(aos.Account_ID=acc.account_ID) "+
						" inner join account acc1 on (acc1.account_ID=ifnull(acc.Parent_ID,acc.account_ID)) "+
						 " INNER JOIN asset a ON (a.Serial_Number = ams.Serial_Number AND a.Status = 1 And a.Product_ID is not null)"+
				" WHERE ams.Latest_Transaction_Timestamp > '2014-01-01 00:00:00'"+
				" ORDER BY ams.Latest_Transaction_Timestamp DESC) AS ams1"+
				" INNER JOIN asset_profile ap ON ap.serialNumber = ams1.Serial_Number"+
				" LEFT OUTER JOIN"+
				" (SELECT p.Product_ID, ag.Asseet_Group_Name,aty.Asset_Type_Name FROM products p"+
					" INNER JOIN asset_group ag ON ag.Asset_Group_ID = p.Asset_Group_ID"+
					" INNER JOIN asset_type aty ON aty.Asset_Type_ID = p.Asset_Type_ID"+
					" INNER JOIN engine_type et ON et.Engine_Type_id = p.Engine_Type_id) bb ON ams1.Product_ID = bb.Product_ID"+
				" LEFT OUTER JOIN asset_event_snapshot aes ON ams1.Serial_Number = aes.serialNumber ";
				query="select cags.Asset_Id,acc.Account_Name as customer_name, acc1.account_name as dealer_name," +
						"ifnull(bb.Asseet_Group_Name,'NA') as Asseet_Group_Name,ifnull(bb.Asset_Type_Name,'NA') as Asset_Type_Name " +
						"from (  (select Asset_Id from custom_asset_group_snapshot where user_Id in('"+userId+"'))cags " +
						" inner join  (Select Serial_Number,ifnull(Product_ID,'NA') as Product_ID,Primary_Owner_ID from asset where status =1) a " +
						" on cags.Asset_Id = a.serial_number ) ";
				//DF20200904 : Zakir : Checking if Date Filter if applicable--------------------------------------------------------
				if(dateFilter != null) {
					query = query + "INNER JOIN( SELECT Serial_Number,Account_ID,Account_Type, Ownership_Start_Date FROM asset_owner_snapshot "+
							"where Ownership_Start_Date < '"+dateFilter+"' "+
							") aos ON a.Primary_Owner_ID = aos.Account_ID and aos.serial_number = cags.Asset_Id";
				}
				//------------------------------------------------------------------------------------------------------------------		
				query = query + " ) " +	
						"inner Join account acc on(a.Primary_Owner_ID=acc.account_ID)" +
						"  inner join account acc1 on (acc1.account_ID=ifnull(acc.Parent_ID,acc.account_ID)) "+
						" LEFT OUTER JOIN (SELECT p.Product_ID, ifnull(ag.Asseet_Group_Name,'NA') as Asseet_Group_Name," +
						"ifnull(aty.Asset_Type_Name,'NA') as Asset_Type_Name FROM products p " +
						"INNER JOIN asset_group ag ON ag.Asset_Group_ID = p.Asset_Group_ID INNER JOIN asset_type aty " +
						"ON aty.Asset_Type_ID = p.Asset_Type_ID INNER JOIN engine_type et ON et.Engine_Type_id = p.Engine_Type_id) bb " +
						"ON a.Product_ID = bb.Product_ID";
			}*///ME100003464.eo

			mainGroupByQuery = " )a group by Asseet_Group_Name ";
			//mainOrderByQuery=	" ORDER BY ams1.transactionTime DESC";

			if (downloadFlag.equalsIgnoreCase("true")) {
				//topSelectQuery="SELECT ams1.*, bb.Asseet_Group_Name,bb.Asset_Type_Name FROM ";
				//mainQuery =topSelectQuery+query+mainOrderByQuery;
				mainQuery = query;
				iLogger.info("MADMAchineCountImpl:getMachineCount:: Query: " + mainQuery);
				rs = stmt.executeQuery(mainQuery);
				while (rs.next()) {
					recordMap = new LinkedHashMap<String, String>();
					recordMap.put("Serial_Number", rs.getString("Serial_Number"));
					//recordMap.put("transactionTime",rs.getString("transactionTime"));
					//recordMap.put("Product_ID",rs.getString("Product_ID"));
					recordMap.put("customer_name", rs.getString("customer_name"));
					recordMap.put("dealer_name", rs.getString("dealer_name"));
					recordMap.put("Asseet_Group_Name", rs.getString("Asseet_Group_Name"));
					recordMap.put("Asset_Type_Name", rs.getString("Asset_Type_Name"));
					respList.add(recordMap);
				}

			} else if (filter == null || filter.isEmpty()) {

				int count = 0;
				topSelectQuery = "SELECT count(*) as count, ifnull(Asseet_Group_Name,'NA') as Asseet_Group_Name FROM (";
				mainQuery = topSelectQuery + query + mainGroupByQuery;
				iLogger.info("MADMAchineCountImpl:getMachineCount:: Query: " + mainQuery);
				rs = stmt.executeQuery(mainQuery);
				while (rs.next()) {
					recordMap.put(rs.getString("Asseet_Group_Name"), String.valueOf(rs.getInt("count")));
					count += rs.getInt("count");
				}
				recordMap.put("All", String.valueOf(count));
				respList.add(recordMap);
			} else {
				topSelectQuery = "SELECT count(*) as count,ifnull(Asseet_Group_Name,'NA') as Asseet_Group_Name FROM (";
				mainwhereQuery = " where Asseet_Group_Name in(" + filter + ") ";
				mainQuery = topSelectQuery + query + mainwhereQuery + mainGroupByQuery;
				iLogger.info("MADMAchineCountImpl:getMachineCount:: Query: " + mainQuery);
				rs = stmt.executeQuery(mainQuery);
				while (rs.next()) {
					recordMap.put(rs.getString("Asseet_Group_Name"), String.valueOf(rs.getInt("count")));
				}
				respList.add(recordMap);
			}

		} catch (Exception e) {
			fLogger.fatal("MADMAchineCountImpl:getMachineCount:::Exception" + e.getMessage());
			System.out.println(e.getMessage());
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if (stmt != null)
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
		Session session = null;
		boolean status = false;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		try {
			List<Integer> groupIdList = new LinkedList<Integer>();

			session = HibernateUtil.getSessionFactory().openSession();
			Query queryGroupUser = session.createQuery("from GroupUserMapping where contact_id ='" + loginId + "'");
			Iterator groupUserItr = queryGroupUser.list().iterator();
			status = groupUserItr.hasNext();
		} catch (Exception e) {
			fLogger.fatal(" Exception in retriving data from Group user:" + e);

		} finally {
			try {
				if (session != null && session.isOpen()) {
					session.close();
				}
			} catch (Exception e) {
				fLogger.fatal("Exception in closing the AssetDashboard session::" + e);
			}

		}
		long endTime = System.currentTimeMillis();
		iLogger.info("Time required to fetch the record from group user for loginId::" + loginId + " in ms :"
				+ (endTime - startTime));
		return status;
	}

}
