package remote.wise.AutoReportScheduler;

import java.io.File;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
//CR337 : 20220721 : Dhiraj K : Property file read.
import java.util.Properties;
import java.util.Set;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;

public class AutoSchedulerImpl 
{
	HashMap<Integer,String> mongoURLMap = new HashMap<Integer,String>();
	HashMap<Integer,String> jasperURLMap = new HashMap<Integer,String>();
	HashMap<Integer,String> reportIDFileNameMap = new HashMap<Integer,String>();
	HashMap<Integer,String> reportIDNameMap = new HashMap<Integer,String>();
	HashMap<Integer,String> reportIDJRXMLMap = new HashMap<Integer,String>();
	
	public String sendSubscribedReport(String subscriptionType, String runId,int reportId,String accountMappingCodeList, String accountType)
	{
		String status="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String autoReportFileLocation ="/user/JCBLiveLink/AutoReports";
		//DF20190412 - Rajani Nagaraju - Metaspace OutofMemory Issue since jrxml was getting loaded for every PDF file. Hence sending this as as parameter
		HashMap<Integer,JasperReport> reportIdJasperReportMap = new HashMap<Integer,JasperReport>();
		ReportDetailsImpl implObj = new ReportDetailsImpl();
		SendEmail sendEmail = new SendEmail();
		
		try
		{
			//----------------------------- STEP1: Validate input parameters
			String accountFilter=null;
			if(subscriptionType==null || !(subscriptionType.equalsIgnoreCase("Weekly") || subscriptionType.equalsIgnoreCase("Monthly")) )
			{
				fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Invalid Parameter value for subscriptionType:" +
						""+subscriptionType+"; Can only be Weekly / Monthly. Aborting the thread");
				return "FAILURE";
			}
			
			if(accountMappingCodeList!=null && accountMappingCodeList.equalsIgnoreCase("null"))
				accountMappingCodeList=null;
			
			if(accountType==null || !(accountType.equalsIgnoreCase("Dealer") || accountType.equalsIgnoreCase("Customer")))
			{
				fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Invalid Parameter acountType:"+accountType+";" +
						"Can only be Dealer / Customer. Aborting the thread");
				return "FAILURE";
			}
			
			if(accountType.equalsIgnoreCase("Dealer"))
				accountFilter="DealerCode";
			else if(accountType.equalsIgnoreCase("Customer"))
				accountFilter="CustCode";
			
		
			String dateFilter=null,value1=null,value2=null,reportStartDate=null,reportEndDate=null;
			if(subscriptionType.equalsIgnoreCase("Weekly"))
			{
				dateFilter="Week";
				//get previous week number
				Calendar cal1 = Calendar.getInstance();
				cal1.add(Calendar.WEEK_OF_YEAR, -1); 
				value1 = String.valueOf(cal1.get(Calendar.WEEK_OF_YEAR));
				value2=String.valueOf(cal1.get(Calendar.YEAR));
				
				cal1.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				reportStartDate = new SimpleDateFormat("yyyy-MM-dd").format(cal1.getTime());
	            cal1.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
	            reportEndDate = new SimpleDateFormat("yyyy-MM-dd").format(cal1.getTime());

				
			}
			else if (subscriptionType.equalsIgnoreCase("Monthly"))
			{
				dateFilter="Month";
				Calendar cal3 = Calendar.getInstance();
				cal3.add(Calendar.MONTH, -1);
				value1 = String.valueOf(cal3.get(Calendar.MONTH)+1);
				value2 = String.valueOf(cal3.get(Calendar.YEAR));
				
				cal3.set(Calendar.DAY_OF_MONTH, 1);
			    reportStartDate = new SimpleDateFormat("yyyy-MM-dd").format(cal3.getTime());
			    cal3.set(Calendar.MONTH, cal3.get(Calendar.MONTH)+ 1);  
		        cal3.set(Calendar.DAY_OF_MONTH, 0);
		        reportEndDate = new SimpleDateFormat("yyyy-MM-dd").format(cal3.getTime());
			}
			
			if(dateFilter==null || value1==null || value2==null)
			{
				fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Invalid Date Filter; dateFilter:"+dateFilter+";" +
						"value1:"+value1+"; value2:"+value2);
				return "FAILURE";
			}
			
			//------------------------------- Encrypt the loginID
			String userID="repo00265"; 
			byte[] encodedBytes = Base64.encodeBase64(userID.getBytes());
            String encodedUserId= new String(encodedBytes);

            //----------------------- Get the SessionID for the given loginId
            //DF20180917 - Rajani Nagaraju - AutoReporting - Fixed user to be used where in sessionId for the user should not be deactivated as the job runs for more than half an hour
            String sessionId= getSessionId("repo00265");
            if(sessionId==null)
            {
            	fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:sessionId is null for the user: repo00265. Hence aborting the transaction");
				return "FAILURE";
            }
            
			//------------------------------ STEP2: Get the list of report ids for which weekly report has to be sent 
			long startTime = System.currentTimeMillis();
            String reportMasterResp = getReportMasterData(runId,reportId,accountType,subscriptionType);
            long endTime = System.currentTimeMillis();
            iLogger.info("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:getReportMasterData:"+reportMasterResp+";Total Time in ms:"+(endTime-startTime));
			
            if(reportMasterResp.equalsIgnoreCase("FAILURE"))
			{
				fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Couldn't get details from Report Master" +
						".Aborting the Thread");
				return "FAILURE";
			}
			if(this.reportIDFileNameMap==null || reportIDFileNameMap.size()==0)
			{
				fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:No records found in Report Master for the given input filters" +
						".Aborting the Thread");
				return "FAILURE";
			}
			
			//DF20190412 - Rajani Nagaraju - Metaspace OutOfmemory issue - For every PDF file jrxml was getting loaded and hence the issue
			if(this.reportIDJRXMLMap!=null && this.reportIDJRXMLMap.size()>0)
			{
				for(Map.Entry<Integer, String> reportIdJrxml: reportIDJRXMLMap.entrySet())
				{
					File file = new File(reportIdJrxml.getValue());
					JasperDesign jasperDesign=JRXmlLoader.load(file.getAbsolutePath());
					JasperReport jr = JasperCompileManager.compileReport(jasperDesign);
					reportIdJasperReportMap.put(reportIdJrxml.getKey(), jr);
				}
			}
			
			//------------------------------- STEP3: For each MongoDB report,Get the list of Accounts and the corresponding Email IDs 
			//------------------------------------------for which the report has to be sent
			List<Integer> failedReports = new LinkedList<Integer>();//If the transaction gets aborted in the middle, need to display the list of accounts for which report is not generated
			for(Map.Entry<Integer,String> reportIDmongoURL : this.mongoURLMap.entrySet())
			{
				long startTime1= System.currentTimeMillis();
				//Get the List of AccounCode with the corresponding comma separated EmailID List for whom report has to be sent
				implObj = new ReportDetailsImpl();
				HashMap<String,String> accountUserMap =implObj.getReportSubscription(runId, reportIDmongoURL.getKey(), 
						accountMappingCodeList, subscriptionType, accountType,value1,value2);
				
				long endTime1= System.currentTimeMillis();
				iLogger.info("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Mongo Reports:getReportSubscription:"+implObj.getContactDetailsStatus+";Total Time in ms:"+(endTime1-startTime1));
					
				 
				if(implObj.getContactDetailsStatus==null || implObj.getContactDetailsStatus.equalsIgnoreCase("FAILURE"))
				{
					//If Subscription details cannot be retrieved, abort the transaction
					fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Mongo Reports:Exception in getting details from report_subscription table" +
							".Aborting the Thread. ");
					failedReports.add(reportIDmongoURL.getKey());
					continue;
				}
				
				reportId=reportIDmongoURL.getKey();
				
				if(accountUserMap==null || accountUserMap.size()==0)
				{
					fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Mongo Reports:No records found in report_subscription table" +
							" for the given input filters:reportId:"+reportId);
					continue;
				}
				
				//------------------------------- Get the List of Account codes corresponding to the account mapping code retrieved from previous step
				//This is to handle BP Code merging implementation
				Set<String> accountMappingCodes = accountUserMap.keySet();
				HashMap<String,String> mappingCodeAccCodeMap = new ReportDetailsImpl().getAccCodeFromMappingCode(accountMappingCodes, 
						subscriptionType, reportId, runId);
				
				//------------------------------ STEP4: For each account,invoke the specific data URL and generate the report
				long startTime2= System.currentTimeMillis();
				//CR337.sn
				String connIP=null;
				String connPort=null;
				Properties prop = null;
				try{
					prop = CommonUtil.getDepEnvProperties();
					connIP = prop.getProperty("MDA_ServerIP");
					connPort = prop.getProperty("MDA_ServerPort");
				}catch(Exception e){
					fLogger.fatal("AssetDetailsBO : getFleetSummaryService : " +
							"Exception in getting Server Details for MDA Layer from properties file: " +e);
				}
				//CR337.en
				for(Map.Entry<String,String> accountUser : accountUserMap.entrySet())
				{
					if(accountUser.getValue()==null)
					{
						fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Mongo Reports:No Users subscribed" +
								" for the given reportId:"+reportId);
						continue;
					}
					
					if(reportIDmongoURL.getValue()==null)
					{
						fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Mongo Reports:No Mongo URL defined" +
								" for the given reportId:"+reportId);
						continue;
					}
					if(mappingCodeAccCodeMap==null || mappingCodeAccCodeMap.get(accountUser.getKey())==null)
					{
						fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Mongo Reports:reportId:" +reportId+
								"No account code defined for the mapping code:"+accountUser.getKey());
						continue;
					}
					
					//DF20180917 - Rajani Nagaraju - AutoReporting - Fixed user to be used where in sessionId for the user should not be deactivated as the job runs for more than half an hour
					String mongoSessionId = URLEncoder.encode(sessionId, "UTF-8");
					// Dhiraj K : 20220630 : Change IP for AWS server
					// String mongodbURL = "http://10.179.12.25:26030/MoolDAReports/"+reportIDmongoURL.getValue()+"?accountFilter="+accountFilter+"" +
					//String mongodbURL = "http://10.210.196.206:26030/MoolDAReports/"+reportIDmongoURL.getValue()+"?accountFilter="+accountFilter+"" +
					String mongodbURL = "http://"+connIP+":"+connPort+"/MoolDAReports/"+reportIDmongoURL.getValue()+"?accountFilter="+accountFilter+"" +
							"&accountIDList="+mappingCodeAccCodeMap.get(accountUser.getKey())+"&dateFilter="+dateFilter+"&value1="+value1+"" +
									"&value2="+value2+"" +
							"&loginID="+encodedUserId+"&sessionID="+mongoSessionId;
					
					String outputFileName = this.reportIDFileNameMap.get(reportId)+"_"+accountUser.getKey()+"_"+dateFilter+value1+value2+".pdf";
					
					if(this.reportIDJRXMLMap==null || this.reportIDJRXMLMap.get(reportId)==null)
					{
						fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Mongo Reports:No Jasper URL defined" +
								" for the given reportId:"+reportId);
						continue;
					}
					//DF20190412 - Rajani Nagaraju - Metaspace OutofMemory Issue since jrxml was getting loaded for every PDF file. Hence sending this as as parameter
					/*String reportGenStatus = new ReportDetailsImpl().generateMongoReport(mongodbURL, runId, subscriptionType, 
							reportId, accountUser.getValue(), reportStartDate, reportEndDate, outputFileName, 
							this.reportIDJRXMLMap.get(reportId), autoReportFileLocation, accountUser.getKey(), accountType, value1,value2);*/
					String reportGenStatus = implObj.generateMongoReport(mongodbURL, runId, subscriptionType, 
							reportId, accountUser.getValue(), reportStartDate, reportEndDate, outputFileName, 
							this.reportIDJRXMLMap.get(reportId), autoReportFileLocation, accountUser.getKey(), accountType, value1,value2,
							reportIdJasperReportMap.get(reportId));
					
					
					//------------------------------ STEP5: Send the generated report to the specific users
					if(reportGenStatus==null || reportGenStatus.equalsIgnoreCase("FAILURE"))
					{
						fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":Account:"+accountUser.getKey()+":sendSubscribedReport:Mongo Reports:" +
								"Report not generated");
						continue;
					}
					
					//DF20190412 - Rajani Nagaraju - Metaspace OutofMemory Issue 
					/*String emailSentStatus = new SendEmail().sendMail(runId,reportId, autoReportFileLocation, outputFileName, accountUser.getValue(), 
							this.reportIDNameMap.get(reportId), dateFilter, value1, value2, subscriptionType, accountType, accountUser.getKey());*/
					String emailSentStatus = sendEmail.sendMail(runId,reportId, autoReportFileLocation, outputFileName, accountUser.getValue(), 
							this.reportIDNameMap.get(reportId), dateFilter, value1, value2, subscriptionType, accountType, accountUser.getKey());
					
					if(emailSentStatus==null || emailSentStatus.equalsIgnoreCase("FAILURE"))
					{
						fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":Account:"+accountUser.getKey()+":sendEmail:Mongo Reports:" +
								"Report not Sent: FAILURE");
						continue;
					}
					
				}
				
				long endTime2 = System.currentTimeMillis();
				iLogger.info("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Generate Report from MongoDB " +
						"and send Email:Total Time in ms:"+(endTime2-startTime2));
				
			
			}
			
			if(failedReports!=null && failedReports.size()>0)
			{
				fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Mongo Reports:" +
						"Exception in getting details from report_subscription table" +
						" for the report IDs:"+failedReports);
				status="FAILURE";
			}
			
			
			//------------------------------- STEP6: For each Jasper report,Get the list of Accounts and the corresponding Email IDs 
			//------------------------------------------for which the report has to be sent
			List<Integer> JfailedReports = new LinkedList<Integer>();//If the transaction gets aborted in the middle, need to display the list of accounts for which report is not generated
			for(Map.Entry<Integer,String> reportIDJasperURL : this.jasperURLMap.entrySet())
			{
				long startTime3= System.currentTimeMillis();
				//Get the List of AccounCode with the corresponding comma separated EmailID List for whom report has to be sent
				implObj = new ReportDetailsImpl();
				HashMap<String,String> accountUserMap =implObj.getReportSubscription(runId, reportIDJasperURL.getKey(), 
						accountMappingCodeList, subscriptionType, accountType,value1,value2);
				
				long endTime3= System.currentTimeMillis();
				iLogger.info("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Jasper Reports:getReportSubscription:"+implObj.getContactDetailsStatus+";Total Time in ms:"+(endTime3-startTime3));
					
				 
				if(implObj.getContactDetailsStatus==null || implObj.getContactDetailsStatus.equalsIgnoreCase("FAILURE"))
				{
					//If Subscription details cannot be retrieved, abort the transaction
					fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Jasper Reports:" +
							" Exception in getting details from report_subscription table" +
							".Aborting the Thread. ");
					JfailedReports.add(reportIDJasperURL.getKey());
					continue;
				}
				
				reportId=reportIDJasperURL.getKey();
				
				if(accountUserMap==null || accountUserMap.size()==0)
				{
					fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Jasper Reports:" +
							" No records found in report_subscription table" +
							" for the given input filters:reportId:"+reportId);
					continue;
				}
				
				//------------------------------ STEP7: For each account,invoke the specific data URL and generate the report
				long startTime4= System.currentTimeMillis();
				for(Map.Entry<String,String> accountUser : accountUserMap.entrySet())
				{
					if(accountUser.getValue()==null)
					{
						fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Jasper Reports:No Users subscribed" +
								" for the given reportId:"+reportId);
						continue;
					}
					
					if(reportIDJasperURL.getValue()==null)
					{
						fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Jasper Reports:No Jasper URL defined" +
								" for the given reportId:"+reportId);
						continue;
					}
					
					/*String jasperURL = "http://localhost:8082/MoolDAReports/"+reportIDmongoURL.getValue()+"?accountFilter="+accountFilter+"" +
							"&accountIDList="+accountUser.getKey()+"&dateFilter="+dateFilter+"&value1="+value1+"&value2="+value2+"" +
							"&loginID="+encodedUserId;*/
					String jasperURL=reportIDJasperURL.getValue();
					
					String outputFileName = this.reportIDFileNameMap.get(reportId)+"_"+accountUser.getKey()+"_"+dateFilter+value1+value2+".pdf";
					
					if(this.reportIDJRXMLMap==null || this.reportIDJRXMLMap.get(reportId)==null)
					{
						fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Jasper Reports:No Jasper URL defined" +
								" for the given reportId:"+reportId);
						continue;
					}
					//DF20180917 - Rajani Nagaraju - AutoReporting - Fixed user to be used where in sessionId for the user should not be deactivated as the job runs for more than half an hour
					//DF20190412 - Rajani Nagaraju - Metaspace OutofMemory Issue since jrxml was getting loaded for every PDF file. Hence sending this as as parameter
					/*String reportGenStatus = new ReportDetailsImpl().generateJasperReport(jasperURL, runId, subscriptionType, 
							reportId, accountUser.getValue(), reportStartDate, reportEndDate, outputFileName, 
							this.reportIDJRXMLMap.get(reportId), autoReportFileLocation, accountUser.getKey(), accountType, value1,value2, sessionId);*/
					String reportGenStatus = implObj.generateJasperReport(jasperURL, runId, subscriptionType, 
							reportId, accountUser.getValue(), reportStartDate, reportEndDate, outputFileName, 
							this.reportIDJRXMLMap.get(reportId), autoReportFileLocation, accountUser.getKey(), accountType, value1,value2, sessionId,
							reportIdJasperReportMap.get(reportId));
					
					
					//------------------------------ STEP5: Send the generated report to the specific users
					if(reportGenStatus==null || reportGenStatus.equalsIgnoreCase("FAILURE"))
					{
						fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":Account:"+accountUser.getKey()+":sendSubscribedReport:Jasper Reports:" +
								" Report not generated");
						continue;
					}
					
					//DF20190412 - Rajani Nagaraju - Metaspace OutofMemory Issue
					/*String emailSentStatus = new SendEmail().sendMail(runId,reportId, autoReportFileLocation, outputFileName, accountUser.getValue(), 
							this.reportIDNameMap.get(reportId), dateFilter, value1, value2, subscriptionType, accountType, accountUser.getKey());*/
					String emailSentStatus = sendEmail.sendMail(runId,reportId, autoReportFileLocation, outputFileName, accountUser.getValue(), 
							this.reportIDNameMap.get(reportId), dateFilter, value1, value2, subscriptionType, accountType, accountUser.getKey());
					
					if(emailSentStatus==null || emailSentStatus.equalsIgnoreCase("FAILURE"))
					{
						fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":Account:"+accountUser.getKey()+":sendEmail:Jasper Reports:" +
								" Report not Sent: FAILURE");
						continue;
					}
					
				}
				
				long endTime4 = System.currentTimeMillis();
				iLogger.info("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Generate Report from Jasper Server " +
						"and send Email:Total Time in ms:"+(endTime4-startTime4));
				
			
			}
			
			if(JfailedReports!=null && JfailedReports.size()>0)
			{
				fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Jasper Reports:" +
						" Exception in getting details from report_subscription table" +
						" for the report IDs:"+JfailedReports);
				status="FAILURE";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			status="FAILURE";
			fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendSubscribedReport:Exception:"+e.getMessage());
		}
		return status;
	}
	
	public String getReportMasterData(String runId,int reportId, String accountType,String subscriptionType)
	{
		String status="SUCCESS";
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
			String sqlQuery = "Select ReportID,ReportURL,DataSource,ReportFileName,JasperPath,ReportName " +
					" from report_master where ReportURL is not null";
			if(accountType.equalsIgnoreCase("Dealer"))
				sqlQuery=sqlQuery+" and AccountType='Dealer' ";
			else if(accountType.equalsIgnoreCase("Customer"))
				sqlQuery=sqlQuery+" and AccountType='Customer' ";
			
			if(reportId!=0)
				sqlQuery=sqlQuery+" and ReportID="+reportId;
			
			ResultSet rs = stmt.executeQuery(sqlQuery);
			while(rs.next())
			{
				String dataSource = rs.getString("DataSource");
				if(dataSource!=null && dataSource.equalsIgnoreCase("MongoDB"))
				{
					this.mongoURLMap.put(rs.getInt("ReportID"), rs.getString("ReportURL"));
				}
				else if (dataSource!=null && dataSource.equalsIgnoreCase("Jasper"))
				{
					this.jasperURLMap.put(rs.getInt("ReportID"), rs.getString("ReportURL"));
				}
				
				this.reportIDFileNameMap.put(rs.getInt("ReportID"), rs.getString("ReportFileName"));
				this.reportIDJRXMLMap.put(rs.getInt("ReportID"), rs.getString("JasperPath"));
				this.reportIDNameMap.put(rs.getInt("ReportID"), rs.getString("ReportName"));
			}
		 }
		 
		 catch(Exception e)
		 {
			e.printStackTrace();
			 status="FAILURE";
			 fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":getReportMasterData:reportId:"+reportId+":Exception:"+e.getMessage());
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
					fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":getReportMasterData:reportId:"+reportId+":SQLException in closing the " +
							"connection:"+sqlEx.getMessage());
				}
		 }
		 
		 return status;
	}
	
	
	//Get SessionId for the given loginId
	public String getSessionId(String userId)
	{
		Connection con = null;
		Statement stmt = null;
		Logger fLogger = FatalLoggerClass.logger;
		String sessionId=null;
		
		 try
		 {
			ConnectMySQL connMySql = new ConnectMySQL();
			con = connMySql.getConnection();
			//Class.forName("com.mysql.jdbc.Driver");
			//con =  DriverManager.getConnection("jdbc:mysql://10.106.68.8:3306/wisetest","root", "admin");
				
			stmt = con.createStatement();
			String sqlQuery = "Select Id FROM idMaster where CAST(FROM_BASE64(userId) AS CHAR(10000) ) = '"+userId+"'";
			
			ResultSet rs = stmt.executeQuery(sqlQuery);
			while(rs.next())
			{
				sessionId = rs.getString("Id");
			}
		 }
		 
		 catch(Exception e)
		 {
			e.printStackTrace();
			fLogger.fatal("AutoReports:Exception in getting sessionId:"+e.getMessage());
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
					fLogger.fatal("AutoReports:Exception in closing sql connection while fetchin sessionId from IdMaster:"+sqlEx.getMessage());
				}
		 }
		 
		 return sessionId;
	}
	
	
}
