package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONObject;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.ReportSubscriptionReqContract;
import remote.wise.service.datacontract.ReportSubscriptionRespContract;
import remote.wise.util.ConnectMySQL;

public class ReportSubscriptionImplNew 
{
	//Set User Preference for Weekly and Monthly Subscription for reports
	public String setreportSubscriptionDetails(List<ReportSubscriptionRespContract> reqObjList)
	{
		String status="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String contactId ="";
		
		Connection con = null;
		Statement stmt = null;
		
		try
		{
			if(reqObjList!=null && reqObjList.size() > 0)
			{
				contactId = reqObjList.get(0).getContactId();
			}
			
			if(contactId==null || contactId.equals(""))
			{
				fLogger.fatal("ReportSubscription:ReportSubscriptionImplNew:setreportSubscriptionService:"+contactId+":contactId is null;" +
						"Hence preference cannot be set");
				return "FAILURE";
			}
			
			//-------------------- Form Weekly and Monthly subscription details in the form of JSON String
			HashMap<String,Integer> weeklySubs = new HashMap<String,Integer>();
			HashMap<String,Integer> monthlySubs = new HashMap<String,Integer>();
			
			for(int i=0; i<reqObjList.size(); i++)
			{
				String reportId = "Report"+reqObjList.get(i).getReportId();
				weeklySubs.put(reportId, ( reqObjList.get(i).isWeeklyReportSubscription() ? 1 :0));
				monthlySubs.put(reportId, ( reqObjList.get(i).isMonthlyReportSubscription() ? 1 :0));
			}
			
			String weeklySubsJSON = new JSONObject(weeklySubs).toString();
			String monthlySubsJSON = new JSONObject(monthlySubs).toString();
			
			ConnectMySQL connMySql = new ConnectMySQL();
			con = connMySql.getConnection();
			//Class.forName("com.mysql.jdbc.Driver");
			//con =  DriverManager.getConnection("jdbc:mysql://10.106.68.8:3306/wisetest","root", "admin");
			
			stmt = con.createStatement();
			
			//------------------------- Get the UserType (Dealer/Customer) from role data - Also validate the input contact
			int roleId=0;
			String accountMappingCode="";
			
			ResultSet rs = stmt.executeQuery("Select a.Role_ID as Role_ID, c.mapping_code as mapping_code from contact a, account_contact b, account c " +
					" where a.Contact_ID = b.Contact_ID and b.Account_ID=c.Account_ID and a.Contact_ID='"+contactId+"' and a.Status=1");
			if(rs.next())
			{
				roleId = rs.getInt("Role_ID");
				accountMappingCode = rs.getString("mapping_code");
			}
			
			if(accountMappingCode.equals("") || roleId==0)
			{
				fLogger.fatal("ReportSubscription:ReportSubscriptionImplNew:setreportSubscriptionService:"+contactId+":Not a valid User " +
						" for setting the preference:roleId:"+roleId+";accountMappingCode:" +accountMappingCode+
						" ; Hence preference cannot be set");
				return "FAILURE";
			}
			
			//--------------------------- Insert/Update the details for the user
			String sqlQuery = "INSERT INTO report_subscription (ContactID,RoleID,AccountMappingCode,WeeklySubscription,MonthlySubscription) VALUES " +
					"('"+contactId+"', "+roleId+", '"+accountMappingCode+"', '"+weeklySubsJSON+"', '"+monthlySubsJSON+"') ON DUPLICATE KEY UPDATE " +
					"RoleID="+roleId+", AccountMappingCode='"+accountMappingCode+"', WeeklySubscription='"+weeklySubsJSON+"', MonthlySubscription='"+monthlySubsJSON+"' ;";
			int updateCount = stmt.executeUpdate(sqlQuery);
			if(updateCount==0)
			{
				iLogger.info("ReportSubscription:ReportSubscriptionImplNew:setreportSubscriptionService:"+contactId+":Record Inserted Successfully:" +
						"WeeklySubscription:"+weeklySubsJSON+"; MonthlySubscription:"+monthlySubsJSON);
			}
			else 
			{
				iLogger.info("ReportSubscription:ReportSubscriptionImplNew:setreportSubscriptionService:"+contactId+":Record Updated Successfully:" +
						"WeeklySubscription:"+weeklySubsJSON+"; MonthlySubscription:"+monthlySubsJSON);
			}
			
			
		}
		catch(Exception e)
		{
			status="FAILURE";
			fLogger.fatal("ReportSubscription:ReportSubscriptionImplNew:setreportSubscriptionService:"+contactId+":Exception:"+e.getMessage());
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
				fLogger.fatal("ReportSubscription:ReportSubscriptionImplNew:setreportSubscriptionService:"+contactId+":SQLException in closing the " +
						"connection:"+sqlEx.getMessage());
			}
		}
		return status;
	}
	
	
	public List<ReportSubscriptionRespContract> getreportSubscriptionDetails(ReportSubscriptionReqContract reqObj)
	{
		List<ReportSubscriptionRespContract> responseList = new LinkedList<ReportSubscriptionRespContract>();
		Logger fLogger = FatalLoggerClass.logger;
		
		Connection con = null;
		Statement stmt = null;
		
		if(reqObj==null || reqObj.getContactId()==null || reqObj.getContactId().trim().length()==0)
		{
			fLogger.fatal("ReportSubscription:ReportSubscriptionImplNew:getreportSubscriptionDetails:"+reqObj.getContactId()+":Mandatory parameter " +
					"contactid is not received");
		}
		
		String contactId= reqObj.getContactId();
		
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String,Integer> weeklyMap = new HashMap<String,Integer>();
			HashMap<String,Integer> monthlyMap = new HashMap<String,Integer>();
			int roleId=0;
			
			ConnectMySQL connMySql = new ConnectMySQL();
			con = connMySql.getConnection();
			//Class.forName("com.mysql.jdbc.Driver");
			//con =  DriverManager.getConnection("jdbc:mysql://10.106.68.8:3306/wisetest","root", "admin");
			
			stmt = con.createStatement();
		
			//---------------------------------- Get the RoleID of the User
			ResultSet rs = stmt.executeQuery("Select Role_ID as Role_ID from contact where Contact_ID='"+contactId+"' and Status=1");
			if(rs.next())
			{
				roleId = rs.getInt("Role_ID");
			}
			
			//----------------------------------- Get the User Subscription details for Reports
			rs = stmt.executeQuery("Select WeeklySubscription,MonthlySubscription from report_subscription " +
					"where ContactID='"+contactId+"'");
			if(rs.next())
			{
				String weeklySubsJSONString = rs.getString("WeeklySubscription");
				String monthlySubsJSONString = rs.getString("MonthlySubscription");
				
				weeklyMap = mapper.readValue(weeklySubsJSONString, new TypeReference<Map<String, Integer>>(){});
				monthlyMap = mapper.readValue(monthlySubsJSONString, new TypeReference<Map<String, Integer>>(){});
			}
			
			//----------------------------------- Get the Master list of Reports
			HashMap<Integer,String> reportIDNameMap = new HashMap<Integer,String>();
			String accountType = "";
			if(roleId==5 || roleId==6)
				accountType="Dealer";
			else if(roleId==7 || roleId==8)
				accountType="Customer";
			else if(roleId==12)
				accountType="Super Admin";
			
			rs = stmt.executeQuery("Select ReportID,ReportName from report_master where AccountType='"+accountType+"'");
			while(rs.next())
			{
				reportIDNameMap.put(rs.getInt("ReportID"), rs.getString("ReportName"));
			}
			
			for(Map.Entry<Integer,String> reportMaster : reportIDNameMap.entrySet())
			{
				ReportSubscriptionRespContract responseObj = new ReportSubscriptionRespContract();
				responseObj.setContactId(contactId);
				responseObj.setReportId(reportMaster.getKey());
				responseObj.setReportName(reportMaster.getValue());	
				if(weeklyMap!=null && weeklyMap.size()>0)
				{
					int weeklySubscription = weeklyMap.get("Report"+reportMaster.getKey());
					if(weeklySubscription==1)
						responseObj.setWeeklyReportSubscription(true);
					else
						responseObj.setWeeklyReportSubscription(false);
				}
				else
					responseObj.setWeeklyReportSubscription(false);
				
				
				if(monthlyMap!=null && monthlyMap.size()>0)
				{
					int monthlySubscription = monthlyMap.get("Report"+reportMaster.getKey());
					if(monthlySubscription==1)
						responseObj.setMonthlyReportSubscription(true);
					else
						responseObj.setMonthlyReportSubscription(false);
				}
				else
					responseObj.setMonthlyReportSubscription(false);
				
				responseList.add(responseObj);
			}
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("ReportSubscription:ReportSubscriptionImplNew:getreportSubscriptionDetails:"+contactId+":Exception:"+e.getMessage());
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
				fLogger.fatal("ReportSubscription:ReportSubscriptionImplNew:setreportSubscriptionService:"+contactId+":SQLException in closing the " +
						"connection:"+sqlEx.getMessage());
			}
		}
		
		return responseList;
	}
}
