package remote.wise.AutoReportScheduler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.ListToStringConversion;

public class ReportDetailsImpl 
{
	String getContactDetailsStatus="SUCCESS";
	
	public HashMap<String,String> getReportSubscription(String runId,int reportId,String accountMappingCodeList, 
			String subscriptionType, String accountType, String value1, String value2)
	{
		HashMap<String,String> accountUserMap = new HashMap<String,String>();
		Logger iLogger = InfoLoggerClass.logger;
		Connection con = null;
		Statement stmt = null;
		Logger fLogger = FatalLoggerClass.logger;
		
		try
		{
			//Get the list of accounts and the corresponding emailIDs for which report has to be sent
			ConnectMySQL connMySql = new ConnectMySQL();
			con = connMySql.getConnection();
			//Class.forName("com.mysql.jdbc.Driver");
			//con =  DriverManager.getConnection("jdbc:mysql://10.106.68.8:3306/wisetest","root", "admin");
			
			stmt = con.createStatement();
			String sqlQuery = "select a.AccountMappingCode as AccountMappingCode, GROUP_CONCAT(b.Primary_Email_ID) as emailIdList from" +
			 		" report_subscription a, contact b where a.ContactID=b.Contact_ID " +
			 		" and b.Status=1 and b.Primary_Email_ID is not null and b.Primary_Email_ID!='null'  and " +
			 		" b.Primary_Email_ID like '%@%.%' and b.Primary_Email_ID not like '%,%' " +
			 		" ";
			
			 if(accountType.equalsIgnoreCase("Dealer"))
				 sqlQuery=sqlQuery+" and a.RoleID in (5,6)";
			 if(accountType.equalsIgnoreCase("Customer"))
				 sqlQuery=sqlQuery+" and a.RoleID in (7,8)";
			 
			 if(subscriptionType.equalsIgnoreCase("Weekly"))
			 {
				 sqlQuery=sqlQuery+" and JSON_EXTRACT(WeeklySubscription, \"$.Report"+reportId+"\") = 1";
			 }
			 else if (subscriptionType.equalsIgnoreCase("Monthly"))
			 {
				 sqlQuery=sqlQuery+" and JSON_EXTRACT(MonthlySubscription, \"$.Report"+reportId+"\") = 1";
			 }
			 
			 if(accountMappingCodeList!=null)
			 {
				// List<String> accountCodeList = Arrays.asList(accountMappingCodeList.split(","));
				 sqlQuery=sqlQuery+" and a.AccountMappingCode in ("+accountMappingCodeList+")";
			 }
			 
			 sqlQuery = sqlQuery+" group by AccountMappingCode";
			 
			 ResultSet rs = stmt.executeQuery(sqlQuery);
			 while(rs.next())
			 {
				 accountUserMap.put(rs.getString("AccountMappingCode"), rs.getString("emailIdList"));
			 }
			 
			 iLogger.info("AutoReports:"+subscriptionType+"Report:"+runId+":getReportSubscription:reportId:"+reportId+":accountUserMap:"+accountUserMap);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			this.getContactDetailsStatus="FAILURE";
			fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":getReportSubscription:reportId:"+reportId+":Exception:"+e.getMessage());
			
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
					fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":getReportSubscription:reportId:"+reportId+":SQLException in closing the " +
							"connection:"+sqlEx.getMessage());
				}
		}
		
		return accountUserMap;
	}
	
	
	//**************************************************************************************************************************************
	//DF20190412 - Rajani Nagaraju - Metaspace OutofMemory Issue since jrxml was getting loaded for every PDF file. Hence sending this as as parameter
	public String generateMongoReport(String mongoReportURL, String runId, 
										String subscriptionType,int reportId, String emailIDList,
										String reportStartDate,String reportEndDate, String outputFileName, String jrxmlFileName,
										String autoReportFileLocation, String accMappingCode, String accountType,String value1, String value2,
										JasperReport jr)
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String pdfFile=null;
		String status="SUCCESS";
		
		try
		{
			 String jsonResponse = "";
			 //When the file is created successfully, it will be placed in input folder
			 pdfFile=autoReportFileLocation+"/input/"+outputFileName;
			 
			 URL url = new URL(mongoReportURL);
			 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			 conn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
			 conn.setRequestProperty("Accept", "application/json");
			 conn.setRequestMethod("GET");
			 
			 if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) 
			 {
				 fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":generateMongoReport:reportId:"+reportId+":" +
				 		"Failure inMongo URL invocation:Response Code:"+conn.getResponseCode() );
				 iLogger.info("MDAReports report status: FAILURE for AutoReports reportId:"+reportId+" :Response Code:"+conn.getResponseCode());
				 throw new Exception("Invalid Response Code from MongoURL:"+conn.getResponseCode());
				 
			 }
			 iLogger.info("MDAReports report status: SUCCESS for AutoReports reportId:"+reportId+" :Response Code:"+conn.getResponseCode());
			 BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			 String outputResponse = null;
			 while((outputResponse = br.readLine()) != null)
			 {
				 jsonResponse=jsonResponse+outputResponse;
			 }
			 
			 ObjectMapper mapper = new ObjectMapper();
			 List<HashMap<String,Object>> responseList = mapper.readValue(jsonResponse, new TypeReference<List<HashMap<String,Object>>>(){});
				
			 Map<String,Object> jasperParams = new HashMap<String,Object>();
			 jasperParams.put("StartDate", reportStartDate);
			 jasperParams.put("EndDate", reportEndDate);
			 
			 //---------------------- Fill in the Jasper report
			//DF20190412 - Rajani Nagaraju - Metaspace OutofMemory Issue since jrxml was getting loaded for every PDF file. Hence sending this as as parameter
			 /*File file = new File(jrxmlFileName);
			 JasperDesign jasperDesign=JRXmlLoader.load(file.getAbsolutePath());
			 JasperReport jr = JasperCompileManager.compileReport(jasperDesign);*/
			   
			 JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(responseList);
			 JasperPrint jprint = JasperFillManager.fillReport(jr,  jasperParams,beanColDataSource);
			 JasperExportManager.exportReportToPdfFile(jprint, pdfFile);
			 iLogger.info("AutoReports:"+subscriptionType+"Report:"+runId+":generateMongoReport:reportId:"+reportId+":PDF File Created successfully" +
			 		":"+pdfFile);
			 
			   
		}
		catch(Exception e)
		{
			e.printStackTrace();
			status="FAILURE";
			fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":generateMongoReport:reportId:"+reportId+":Exception:"+e.getMessage()+";" +
					"Persisting Details to Fault Details table");
			
			//Invoke the Exception for storing the data into fault details table
			new AutoReportAuditTrial().persistToFaultDetails(runId,reportId, accMappingCode, subscriptionType, 
					value1, value2, e.getMessage(), accountType, emailIDList, null);
		}
		
		return status;
	}
	
	
	//**************************************************************************************************************************************
	//DF20190412 - Rajani Nagaraju - Metaspace OutofMemory Issue since jrxml was getting loaded for every PDF file. Hence sending this as as parameter
	public String generateJasperReport(String jasperReportURL, String runId, 
			String subscriptionType,int reportId, String emailIDList,
			String reportStartDate,String reportEndDate, String outputFileName, String jrxmlFileName,
			String autoReportFileLocation, String accMappingCode, String accountType,String value1, String value2, String sessionID,
			JasperReport jr)
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String pdfFile=null;
		
		try
		{
			pdfFile=autoReportFileLocation+"/input/"+outputFileName;
			List<HashMap<String,Object>> responseList = null;
			//Get the response List from MySQL database
			if(reportId==1 || reportId==8)
			{
				responseList = new MySQLDataSet().getServiceMachineHrsData(runId, reportId, subscriptionType, accMappingCode);
			}
			else if(reportId==9)
			{
				responseList = new MySQLDataSet().getServiceDueOverdueData(runId, reportId, subscriptionType, accMappingCode);
			}
			
			if(responseList==null)
				 responseList= new LinkedList<HashMap<String,Object>>();
			 Map<String,Object> jasperParams = new HashMap<String,Object>();
			//DF20180917 - Rajani Nagaraju - AutoReporting - Fixed user to be used where in sessionId for the user should not be deactivated as the job runs for more than half an hour
			 jasperParams.put("sessionID", sessionID);
			 
			 //---------------------- Fill in the Jasper report
			 /*//DF20190412 - Rajani Nagaraju - Metaspace OutofMemory Issue since jrxml was getting loaded for every PDF file. Hence sending this as as parameter
			 File file = new File(jrxmlFileName);
			 JasperDesign jasperDesign=JRXmlLoader.load(file.getAbsolutePath());
			 JasperReport jr = JasperCompileManager.compileReport(jasperDesign);*/
			   
			 JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(responseList);
			 JasperPrint jprint = JasperFillManager.fillReport(jr,  jasperParams,beanColDataSource);
			 JasperExportManager.exportReportToPdfFile(jprint, pdfFile);
			 iLogger.info("AutoReports:"+subscriptionType+"Report:"+runId+":generateJasperReport:reportId:"+reportId+":PDF File Created successfully" +
			 		":"+pdfFile);
		}
		
		catch(Exception e)
		{
			fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":generateJasperReport:reportId:"+reportId+":Exception:"+e.getMessage());
		}
		
		return pdfFile;
	}
	
	
	//************************************************************************************************************************************************
	public HashMap<String,String> getAccCodeFromMappingCode(Set<String> mappingCodeSet, String subscriptionType, int reportId, String runId)
	{
		HashMap<String,String> mappingCodeAccCodeMap = new HashMap<String,String>();
		
		Connection con = null;
		Statement stmt = null;
		Logger fLogger = FatalLoggerClass.logger;
		
		try
		{
			if(mappingCodeSet==null || mappingCodeSet.size()==0)
			{
				fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":getAccCodeFromMappingCode:reportId:"+reportId+":mappingCodeSet is null");
				return mappingCodeAccCodeMap;
			}
			//Get Mapping code list
			String mappingCode = new ListToStringConversion().getStringListFromSet(mappingCodeSet).toString();
			
			ConnectMySQL connMySql = new ConnectMySQL();
			con = connMySql.getConnection();
			//Class.forName("com.mysql.jdbc.Driver");
			//con =  DriverManager.getConnection("jdbc:mysql://10.106.68.8:3306/wisetest","root", "admin");
			
			stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT mapping_code, GROUP_CONCAT(account_code) as accountCodelist " +
					" from account where mapping_code is not null and mapping_code in ("+mappingCode+") " +
							" group by mapping_code;");
			while(rs.next())
			{
				mappingCodeAccCodeMap.put(rs.getString("mapping_code"), rs.getString("accountCodelist"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":getAccCodeFromMappingCode:reportId:"+reportId+":Exception:"+e.getMessage());
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
					fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":getAccCodeFromMappingCode:reportId:"+reportId+":" +
							" SQLException in closing the " +
							"connection:"+sqlEx.getMessage());
				}
		}
		
		return mappingCodeAccCodeMap;
	}
	
}
