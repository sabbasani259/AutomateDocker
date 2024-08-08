package remote.wise.AutoReportScheduler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.util.ConnectMySQL;

public class AutoReportAuditTrial 
{
	public void persistToFaultDetails(String runId,int reportId, String accountMappingCode, String subscriptionType,
			String value1, String value2, String failureCause, String accountType,String emailIDList,String filePath)
	{
		//NOTE: recordIdentifier is in the format <reportId>_<accountMappingCode>_<Month/Week><value1><value2>
		Connection con = null;
		Statement stmt = null;
		Logger fLogger = FatalLoggerClass.logger;
		
		try
		{
			String recordIdentifier="R"+reportId+"_"+accountMappingCode+"_"+subscriptionType.replaceAll("ly", "")+value1+value2;
			String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			
			ConnectMySQL connMySql = new ConnectMySQL();
			con = connMySql.getConnection();
			//Class.forName("com.mysql.jdbc.Driver");
			//con =  DriverManager.getConnection("jdbc:mysql://10.106.68.8:3306/wisetest","root", "admin");
			stmt = con.createStatement();
			
			int reprocessCounter=0;
			ResultSet rs = stmt.executeQuery("select ReprocessCounter from autoReports_faultDetails where RecordIdentifier='"+recordIdentifier+"'");
			while(rs.next())
			{
				reprocessCounter = rs.getInt("ReprocessCounter");
			}
			
			if(reprocessCounter>0)
			{
				//update the record
				stmt.executeUpdate("udpate autoReports_faultDetails set EmailIDList='"+emailIDList+"', FailureCause='"+failureCause+"', " +
						"FilePath='"+filePath+"', ReprocessCounter="+(reprocessCounter+1)+", LastUpdatedTime='"+currentDate+"' " +
								" where RecordIdentifier='"+recordIdentifier+"'");
			}
			else
			{
				//Insert the new record
				stmt.execute("INSERT INTO autoReports_faultDetails(RecordIdentifier,ReportID,SubscriptionType,ReportingPeriod,ReportingYear," +
						"AccountType,AccountMappingCode,EmailIDList,FailureCause,FilePath,ReprocessCounter,CreatedTimestmap,LastUpdatedTime) VALUES " +
						"('"+recordIdentifier+"', "+reportId+", '"+subscriptionType+"', "+value1+", "+value2+", '"+accountType+"', '"+accountMappingCode+"" +
								"', '"+emailIDList+"', '"+failureCause+"', '"+filePath+"', 1, '"+currentDate+"', '"+currentDate+"' )");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":persistToFaultDetails:accountMappingCode:"+accountMappingCode+":reportId:"+reportId+":Exception:"+e.getMessage());
			
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
					fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":persistToFaultDetails:accountMappingCode:"+accountMappingCode+":reportId:"+reportId+":" +
							"SQLException in closing the " +
							"connection:"+sqlEx.getMessage());
				}
		}
	}
	
	public void persistToAuditTrial(String runId, int reportId, String accountMappingCode, String subscriptionType,
			String value1, String value2, String accountType,String emailIDList,String fileName)
	{

		//NOTE: recordIdentifier is in the format <reportId>_<accountMappingCode>_<Month/Week><value1><value2>
		Connection con = null;
		Statement stmt = null;
		Logger fLogger = FatalLoggerClass.logger;
		
		try
		{
			String recordIdentifier="R"+reportId+"_"+accountMappingCode+"_"+subscriptionType.replaceAll("ly", "")+value1+value2;
			String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			
			ConnectMySQL connMySql = new ConnectMySQL();
			con = connMySql.getConnection();
			//Class.forName("com.mysql.jdbc.Driver");
			//con =  DriverManager.getConnection("jdbc:mysql://10.106.68.8:3306/wisetest","root", "admin");
			stmt = con.createStatement();
			
			String AccountType=null;
			ResultSet rs = stmt.executeQuery("select AccountType from autoReport_AuditTrial where RecordIdentifier='"+recordIdentifier+"'");
			while(rs.next())
			{
				AccountType = rs.getString("AccountType");
			}
			
			if(AccountType!=null)
			{
				//update the record
				stmt.executeUpdate("update autoReport_AuditTrial set LastUpdatedTime='"+currentDate+"' " +
								" where RecordIdentifier='"+recordIdentifier+"'");
			}
			else
			{
				//Insert the new record
				stmt.execute("INSERT INTO autoReport_AuditTrial(RecordIdentifier,ReportID,SubscriptionType,ReportingPeriod,ReportingYear," +
						"AccountType,AccountMappingCode,EmailIDList,FileName,CreatedTimestmap) VALUES " +
						"('"+recordIdentifier+"', "+reportId+", '"+subscriptionType+"', "+value1+", "+value2+", '"+accountType+"', '"+accountMappingCode+"" +
								"', '"+emailIDList+"', '"+fileName+"', '"+currentDate+"' )");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":persistToAuditTrial:accountMappingCode:"+accountMappingCode+":reportId:"+reportId+":Exception:"+e.getMessage());
			
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
					fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":persistToAuditTrial:accountMappingCode"+accountMappingCode+":reportId:"+reportId+":" +
							"SQLException in closing the " +
							"connection:"+sqlEx.getMessage());
				}
		}
	
	}
}
