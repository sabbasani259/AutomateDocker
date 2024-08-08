package remote.wise.AutoReportScheduler;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class ReprocessFaultDetails 
{
	public String reprocessFailureRecords(int priority, String subscriptionType, int reportingPeriod, int reportingYear,
			String accountMappingCodeList, int reportId, String accountType, String runId)
	{
		String status="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		String autoReportFileLocation ="/user/JCBLiveLink/AutoReports";
		Logger fLogger = FatalLoggerClass.logger;
		
		try
		{
			//STEP1: Get the list of records to be processed from faultDetails table
			List<AutoReportFaultDetailsPojo> faultDetailsList = getFailureRecords(priority, subscriptionType, 
					reportingPeriod, reportingYear, accountMappingCodeList, reportId, accountType);
			
			if(faultDetailsList==null || faultDetailsList.size()==0)
			{
				iLogger.info("RAutoReports:"+subscriptionType+"Report:"+"reprocessFailureRecords:No matching records found!");
				return "SUCCESS";
			}
			//STEP2: For each record invoke the reprocessing logic
			for(int i=0; i<faultDetailsList.size(); i++)
			{
				String reprocessStatus="SUCCESS";
				
				if(faultDetailsList.get(i).getFilePath()==null)
				{
					reprocessStatus = new AutoSchedulerImpl().sendSubscribedReport(faultDetailsList.get(i).getSubscriptionType(), 
							runId, faultDetailsList.get(i).getReportID(), faultDetailsList.get(i).getAccountMappingCode(), 
							faultDetailsList.get(i).getAccountType());
					iLogger.info("RAutoReports:"+subscriptionType+"Report:"+"RecordIdentifier:"+faultDetailsList.get(i).getRecordIdentifier()+":" +
							"ReprocessStatus:"+reprocessStatus);
				}
				else
				{
					String filePath=faultDetailsList.get(i).getFilePath();
					//--------------Check whether the file exists
					File file =new File(filePath);
					if(!file.exists())
					{
						iLogger.info("RAutoReports:"+subscriptionType+"Report:"+"RecordIdentifier:"+faultDetailsList.get(i).getRecordIdentifier()+":" +
								" File doesnot exists: Hence generating again:"+filePath);
						reprocessStatus = new AutoSchedulerImpl().sendSubscribedReport(faultDetailsList.get(i).getSubscriptionType(), 
								runId, faultDetailsList.get(i).getReportID(), faultDetailsList.get(i).getAccountMappingCode(), 
								faultDetailsList.get(i).getAccountType());
						iLogger.info("RAutoReports:"+subscriptionType+"Report:"+"RecordIdentifier:"+faultDetailsList.get(i).getRecordIdentifier()+":" +
								"ReprocessStatus:"+reprocessStatus);
					}
					
					else
					{
						String[] fileLoc = filePath.split("/");
						filePath = fileLoc[fileLoc.length-1];
						
						String reportName = filePath.split("_")[0];
						String dateFilter="";
						if(faultDetailsList.get(i).getSubscriptionType()!=null && faultDetailsList.get(i).getSubscriptionType().equalsIgnoreCase("Weekly"))
							dateFilter="Week";
						if(faultDetailsList.get(i).getSubscriptionType()!=null && faultDetailsList.get(i).getSubscriptionType().equalsIgnoreCase("Monthly"))
							dateFilter="Month";
						
						reprocessStatus=new SendEmail().sendMail(runId, faultDetailsList.get(i).getReportID(), autoReportFileLocation, filePath, faultDetailsList.get(i).getEmailIDList(),
								reportName, dateFilter, String.valueOf(faultDetailsList.get(i).getReportingPeriod()), 
								String.valueOf(faultDetailsList.get(i).getReportingYear()), 
								faultDetailsList.get(i).getSubscriptionType(), faultDetailsList.get(i).getAccountType(), faultDetailsList.get(i).getAccountMappingCode());
						
						iLogger.info("RAutoReports:"+subscriptionType+"Report:"+"RecordIdentifier:"+faultDetailsList.get(i).getRecordIdentifier()+":fileName" +filePath+":" +
								"ReprocessStatus:"+reprocessStatus);
					}
				}
				
				if( ! (reprocessStatus==null || reprocessStatus.equalsIgnoreCase("FAILURE")) )
				{
					//Delete the record from from fault details table as it is successfully processed
					String deletionStatus=deleteFromFaultDetails(faultDetailsList.get(i).getRecordIdentifier(), faultDetailsList.get(i).getSubscriptionType());
					if(deletionStatus==null || deletionStatus.equalsIgnoreCase("FAILURE"))
					{
						fLogger.fatal("RAutoReports:"+subscriptionType+"Report:"+"RecordIdentifier:"+faultDetailsList.get(i).getRecordIdentifier()+"" +
								":Failure in deleting the  record from autoReports_faultDetails table ");
					}
				}
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			status="FAILURE";
			fLogger.fatal("RAutoReports:"+subscriptionType+"Report:reprocessFailureRecords:Exception:"+e.getMessage());
		}
		
		return status;
	}
	
	public List<AutoReportFaultDetailsPojo> getFailureRecords(int priority, String subscriptionType, int reportingPeriod, int reportingYear,
			String accountMappingCode, int reportId, String accountType)
	{
		List<AutoReportFaultDetailsPojo> responseObjList = new LinkedList<AutoReportFaultDetailsPojo>();
		Connection con = null;
		Statement stmt = null;
		Logger fLogger = FatalLoggerClass.logger;
		
		try
		{
			ConnectMySQL connMySql = new ConnectMySQL();
			con = connMySql.getConnection();
			//Class.forName("com.mysql.jdbc.Driver");
			//con =  DriverManager.getConnection("jdbc:mysql://10.106.68.8:3306/wisetest","root", "admin");
			
			stmt = con.createStatement();
			String sqlQuery = "select RecordIdentifier,ReportID,SubscriptionType,ReportingPeriod,ReportingYear,AccountType," +
					"AccountMappingCode,EmailIDList,FilePath from autoReports_faultDetails where RecordIdentifier is not null ";
			
			if(priority>0)
				sqlQuery = sqlQuery+" and ReprocessPriority="+priority;
			if(subscriptionType!=null)
				sqlQuery = sqlQuery+" and SubscriptionType='"+subscriptionType+"'";
			if(reportingPeriod>0)
				sqlQuery = sqlQuery+" and ReportingPeriod="+reportingPeriod;
			if(reportingYear>0)
				sqlQuery = sqlQuery+" and ReportingYear="+reportingYear;
			if(accountMappingCode!=null)
				sqlQuery = sqlQuery+" and AccountMappingCode='"+accountMappingCode+"'";
			if(accountType!=null)
				sqlQuery = sqlQuery+" and AccountType='"+accountType+"'";
			if(reportId>0)
				sqlQuery = sqlQuery+" and ReportID="+reportId;
			
			 ResultSet rs = stmt.executeQuery(sqlQuery);
			 
			 while(rs.next())
			 {
				 AutoReportFaultDetailsPojo responseObj = new AutoReportFaultDetailsPojo();
				 responseObj.setRecordIdentifier(rs.getString("RecordIdentifier"));
				 responseObj.setReportID(rs.getInt("ReportID"));
				 responseObj.setSubscriptionType(rs.getString("SubscriptionType"));
				 responseObj.setReportingPeriod(rs.getInt("ReportingPeriod"));
				 responseObj.setReportingYear(rs.getInt("ReportingYear"));
				 responseObj.setAccountType(rs.getString("AccountType"));
				 responseObj.setAccountMappingCode(rs.getString("AccountMappingCode"));
				 responseObj.setEmailIDList(rs.getString("EmailIDList"));
				 responseObj.setFilePath(rs.getString("FilePath"));
				 
				 responseObjList.add(responseObj);
			 }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("RAutoReports:"+subscriptionType+"Report:"+"getFailureRecords:reportId:"+reportId+":Exception:"+e.getMessage());
			
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
					fLogger.fatal("RAutoReports:"+subscriptionType+"Report:"+"getFailureRecords:reportId:"+reportId+":SQLException in closing the " +
							"connection:"+sqlEx.getMessage());
				}
		}
		
		return responseObjList;
	}
	
	public String deleteFromFaultDetails(String recordIdentifier, String subscriptionType)
	{
		String status="SUCCESS";
		
		Connection con = null;
		Statement stmt = null;
		Logger fLogger = FatalLoggerClass.logger;
		
		try
		{
			if(recordIdentifier==null || recordIdentifier.trim().length()==0)
			{
				fLogger.fatal("RAutoReports:"+subscriptionType+"Report:"+":deleteFromFaultDetails:RecordIdentifier is null. Hence cannot delete the record");
				return "FAILURE";
			}
			
			ConnectMySQL connMySql = new ConnectMySQL();
			con = connMySql.getConnection();
				
			stmt = con.createStatement();
			String sqlQuery = "delete from autoReports_faultDetails where RecordIdentifier='"+recordIdentifier+"'";
			stmt.executeUpdate(sqlQuery);
			
		}
		catch(Exception e)
		{
			status="FAILURE";
			e.printStackTrace();
			fLogger.fatal("RAutoReports:"+subscriptionType+"Report:"+":deleteFromFaultDetails:recordIdentifier:"+recordIdentifier+":" +
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
					fLogger.fatal("RAutoReports:"+subscriptionType+"Report:"+":deleteFromFaultDetails:recordIdentifier:"+recordIdentifier+"" +
							":SQLException in closing the " +
							"connection:"+sqlEx.getMessage());
				}
		}
		
		return status;
	}
}
