package remote.wise.AutoReportScheduler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.util.ConnectMySQL;

public class MySQLDataSet 
{
	public List<HashMap<String,Object>> getServiceMachineHrsData(String runId, int reportId, String subscriptionType,
										String accMappingCode)
	{
		List<HashMap<String,Object>> responseList = new LinkedList<HashMap<String,Object>>();
		Logger fLogger = FatalLoggerClass.logger;
		Connection con = null;
		Statement stmt = null;
		
		try
		{
			ConnectMySQL connMySql = new ConnectMySQL();
			con = connMySql.getConnection();
			//Class.forName("com.mysql.jdbc.Driver");
			//con =  DriverManager.getConnection("jdbc:mysql://10.179.12.66:3306/wise","root", "admin");
			
			stmt = con.createStatement();
			String sqlQuery ="select aa.Serial_Number,aa.Service_Schedule_Name as Schedule_Name,aa.Total_Engine_Hours,aa.Last_Service_Name,"+
							" aa.Last_Service_Date,aa.Last_Service_Hours,aa.Next_Service_Name,aa.Next_Service_Date,"+
							" (case when aa.Next_Service_Name = '1st Service' then aa.Next_Service_Hours else " +
							" round((aa.Next_Service_Hours - aa.Total_Engine_Hours)) END)as HoursToNxtService,aa.DealerName,"+
							" bb.Address from "+
							"(SELECT a.account_name as DealerName,sdr.* FROM wise.service_details_report sdr,asset_owner_snapshot aos,account a"+
							" WHERE a.mapping_code in ('"+accMappingCode+"') and aos.account_id = a.account_id " +
									"and sdr.Serial_Number = aos.Serial_Number) aa"+
							" LEFT OUTER JOIN"+
							" (SELECT a.* FROM(select ae.Serial_Number,ae.Address,Event_Generated_time FROM asset_event ae," +
							" asset_owner_snapshot aos,account a"+
							" WHERE a.mapping_code in ('"+accMappingCode+"') and aos.account_id = a.account_id and " +
							" ae.Serial_Number = aos.Serial_Number and ae.event_id =3"+
							" order by Event_Generated_Time desc) a"+
							" GROUP BY Serial_Number) bb on bb.Serial_Number = aa.Serial_Number";
			ResultSet rs = stmt.executeQuery(sqlQuery);
			while(rs.next())
			{
				HashMap<String,Object> response = new HashMap<String,Object>();
				response.put("Serial_Number", rs.getString("Serial_Number"));
				response.put("Schedule_Name", rs.getString("Schedule_Name"));
				Double value=0.0;
				if(rs.getString("Total_Engine_Hours")!=null)
				 value = Double.parseDouble(rs.getString("Total_Engine_Hours"));
				response.put("Total_Engine_Hours", value);
				response.put("Last_Service_Name", rs.getString("Last_Service_Name"));
				response.put("Last_Service_Date", rs.getTimestamp("Last_Service_Date"));
				response.put("Last_Service_Hours", rs.getString("Last_Service_Hours"));
				response.put("Next_Service_Name", rs.getString("Next_Service_Name"));
				response.put("Next_Service_Date", rs.getTimestamp("Next_Service_Date"));
				response.put("HoursToNxtService", rs.getObject("HoursToNxtService"));
				response.put("DealerName", rs.getString("DealerName"));
				response.put("Address", rs.getString("Address"));
				
				responseList.add(response);
			}
				
		}
		catch(Exception e)
		{
			fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":MySQLDataSet:getServiceMachineHrsData:reportId:"+reportId+":" +
					"Exception:"+e.getMessage());
		}
		finally
		{
			try
			{
				if(stmt!=null)
					stmt.close();
				if(con != null)
					con.close();
			}
			catch(SQLException sqlEx)
			{
				fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":MySQLDataSet:getServiceMachineHrsData:reportId:"+reportId+"" +
						":SQLException in closing the " +
						"connection:"+sqlEx.getMessage());
			}
		}
		return responseList;
	}
	
	
	//**************************************************************************************************************************************
	public List<HashMap<String,Object>> getServiceDueOverdueData(String runId, int reportId, String subscriptionType,
			String accMappingCode)
	{

		List<HashMap<String,Object>> responseList = new LinkedList<HashMap<String,Object>>();
		Logger fLogger = FatalLoggerClass.logger;
		Connection con = null;
		Statement stmt = null;
		
		try
		{
			ConnectMySQL connMySql = new ConnectMySQL();
			con = connMySql.getConnection();
			//Class.forName("com.mysql.jdbc.Driver");
			//con =  DriverManager.getConnection("jdbc:mysql://10.179.12.66:3306/wise","root", "admin");
			//DF20190204 @abhishek---->add partition key for faster retrieval
			stmt = con.createStatement();
			String sqlQuery ="SELECT a.*,b.* FROM(select aa.Serial_Number,aa.asset_type_name,aa.Schedule_Name,aa.tmh," +
					" (aa.Next_Service_Hours - aa.tmh) as dueByHours,aa.Last_Service_Date,aa.Next_Service_Name,aa.Next_Service_Date," +
					" aa.Next_Service_Hours,aa.customer as CustomerName,aa.cust_mob as CustomerNumber,aa.Dealer as DealerName,aa.deal_mob as DealerNumber," +
					" aa.service_type,DATEDIFF(DATE(aa.Next_Service_Date),CURDATE()) as diff_by_date,event.address " +
					" FROM (select ae.Serial_Number,ae.address FROM asset_event ae,asset_owner_snapshot aos,account a WHERE " +
					" a.mapping_code in ('"+accMappingCode+"') and aos.account_id = a.account_id and ae.Serial_Number = aos.Serial_Number " +
					" and ae.event_id in (1,2,3) and ae.active_status = 1 and ae.PartitionKey =1 group by ae.Serial_Number) event,(SELECT a.tmh,b.* " +
					" from (select serial_number,CONVERT(tmh, CHAR(50)) as tmh from Asset_snapshot_v) a," +
					" (select sdr.Serial_Number,sdr.Service_Schedule_Name as Schedule_Name,sdr.Last_Service_Date,sdr.Last_Service_Hours," +
					" sdr.Next_Service_Name,ss.service_type,sdr.Next_Service_Date,sdr.Next_Service_Hours ,a_t.asset_type_name," +
					" ca.account_name customer,ca.Mobile cust_mob,da.account_name dealer,da.Mobile deal_mob FROM wise.service_details_report sdr," +
					" asset_owner_snapshot aos,asset a,products p,asset_type a_t,service_schedule ss,account da,account ca WHERE " +
					" da.mapping_code in  ('"+accMappingCode+"')  and aos.account_id = da.account_id and sdr.Serial_Number = aos.Serial_Number " +
					" and a.Serial_Number = aos.Serial_Number and ca.account_id = a.primary_owner_id and p.product_id = a.product_id " +
					" and a_t.asset_type_id = p.asset_type_id and ss.asset_group_id = p.asset_group_id and ss.asset_type_id =  p.asset_type_id " +
					" and ss.serviceName = sdr.Next_Service_Name group by serial_number) b where a.serial_number=b.serial_number) aa " +
					" where aa.Serial_Number = event.Serial_Number group by serial_number) a LEFT OUTER JOIN (SELECT  e2.serialnumber," +
					" e2.servicedate,e2.serviceTicketNumber serviceTicketNumber,cnt1.serviceTicketNumber serviceTicketNumber1,cnt1.colcount colcount " +
					" FROM service_history e2 INNER JOIN (SELECT MAX(e3.servicedate) AS servicedate,COUNT(e3.serialnumber) AS colcount," +
					" e3.serialnumber,e3.serviceTicketNumber FROM service_history e3 " +
					" WHERE e3.serviceticketnumber LIKE '8%' AND LENGTH(e3.serviceticketnumber) = 10 GROUP BY serialnumber)" +
					" AS cnt1 ON cnt1.servicedate = e2.servicedate AND cnt1.serialnumber = e2.serialnumber) b ON a.serial_Number = b.SerialNumber";
			ResultSet rs = stmt.executeQuery(sqlQuery);
			while(rs.next())
			{
				HashMap<String,Object> response = new HashMap<String,Object>();
				response.put("Serial_Number", rs.getString("Serial_Number"));
				response.put("asset_type_name", rs.getString("asset_type_name"));
				response.put("Schedule_Name", rs.getString("Schedule_Name"));
				
				Double tmhValue = 0.0;
				if(rs.getString("tmh")!=null)
					tmhValue = Double.parseDouble(rs.getString("tmh"));
				response.put("tmh", tmhValue);
				Double dueByHoursValue = 0.0;
				if(rs.getString("dueByHours")!=null)
					dueByHoursValue = Double.parseDouble(rs.getString("dueByHours"));
				response.put("dueByHours", dueByHoursValue);
				
				response.put("Last_Service_Date", rs.getTimestamp("Last_Service_Date"));
				response.put("Next_Service_Name", rs.getString("Next_Service_Name"));
				response.put("Next_Service_Date", rs.getTimestamp("Next_Service_Date"));
				response.put("Next_Service_Hours", rs.getObject("Next_Service_Hours"));
				response.put("CustomerName", rs.getString("CustomerName"));
				response.put("CustomerNumber", rs.getString("CustomerNumber"));
				response.put("DealerName", rs.getString("DealerName"));
				response.put("DealerNumber", rs.getString("DealerNumber"));
				response.put("service_type", rs.getString("service_type"));
				response.put("diff_by_date", rs.getObject("diff_by_date"));
				response.put("address", rs.getString("address"));
				response.put("serialnumber", rs.getString("serialnumber"));
				response.put("servicedate", rs.getTimestamp("servicedate"));
				response.put("serviceTicketNumber", rs.getString("serviceTicketNumber"));
				response.put("serviceTicketNumber1", rs.getString("serviceTicketNumber1"));
				response.put("colcount", rs.getObject("colcount"));
				
				responseList.add(response);
			}
				
		}
		catch(Exception e)
		{
			fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":MySQLDataSet:getServiceDueOverdueData:reportId:"+reportId+":" +
					"Exception:"+e.getMessage());
		}
		finally
		{
			try
			{
				if(stmt!=null)
					stmt.close();
				if(con != null)
					con.close();
			}
			catch(SQLException sqlEx)
			{
				fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":MySQLDataSet:getServiceDueOverdueData:reportId:"+reportId+"" +
						":SQLException in closing the " +
						"connection:"+sqlEx.getMessage());
			}
		}
		return responseList;
	
	}
}
