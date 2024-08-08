package remote.wise.service.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import remote.wise.handler.WhatsappHandler;
import remote.wise.handler.WhatsappTemplate;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.BreakfastReportPOJO;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.StaticProperties;

public class SendBreakfastReport implements Runnable
{
	Thread t;
	String UserID;
	
	public SendBreakfastReport()
	{
		
	}
	
	public SendBreakfastReport(String UserID)
	{
		t = new Thread(this, "SendBreakfastReport");
		this.UserID=UserID;
		t.start();
	}
	
	public void run()
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Connection con=null;
		Statement stmt=null;
		ResultSet rs = null;
		
		
		try
		{
			String batchReportPath = "/user/JCBLiveLink/BreakfastReport";
			
			
			//--------------------------- Get the number of records to be executed in each thread
			Double recordLimit = 0.0d;
			try
	    	{
	    		Properties prop= StaticProperties.getConfProperty();
	    		recordLimit = Double.valueOf(prop.getProperty("BreakfastReportRecPerThread"));
	    	}
	    	catch(Exception e)
	    	{
	    		fLogger.fatal("BreakfastReport:SendBreakfastReport:run():Exception in reading Record limit per thread " +
	    				" from property file:"+e.getMessage());
	    		return;
	    	}
			
			
			//Get DB connection
			con = new ConnectMySQL().getConnection();
			stmt = con.createStatement();
			
			//Get the list of users who have subscribed for breakfast report
			HashMap<String,String> userMobMappingCodeMap = new HashMap<String,String>();
			String userQ = "select a.UserID,b.Primary_Moblie_Number,d.mapping_code" +
					" from BreakfastReportPreference a, contact b, account_contact c, account d" +
					" where a.UserID=b.contact_id and b.status=1 and a.Preference=1 and b.contact_id=c.contact_id" +
					" and c.account_id=d.account_id";
			if(this.UserID!=null)
				userQ=userQ+" and b.contact_id='"+this.UserID+"'";
			
			rs = stmt.executeQuery(userQ);
			while(rs.next())
			{
				userMobMappingCodeMap.put(rs.getString("UserID"), rs.getString("Primary_Moblie_Number")+"|"+rs.getString("mapping_code"));
			}
			
			
			
			int numberOfRecords = userMobMappingCodeMap.size();
			Double numberOfThreadsD =  (Math.ceil(numberOfRecords/recordLimit));
			int numberOfThreads = numberOfThreadsD.intValue();
			ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("BreakfastReport-thread-%d").build();
			
			CountDownLatch latch = new CountDownLatch(numberOfThreads);
			ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads,namedThreadFactory);
			
			int i=0;
			for(Map.Entry<String,String> userDetailsMap: userMobMappingCodeMap.entrySet())
			{
				i++;
				
				String userID = userDetailsMap.getKey();
				String mobileNum=null, mappingCode=null;
				if(userDetailsMap.getValue().split("\\|").length>0)
				{
					mobileNum = userDetailsMap.getValue().split("\\|")[0];
					mappingCode = userDetailsMap.getValue().split("\\|")[1];
				}
				
				if(mappingCode==null || mobileNum==null)
					continue;
				
				BreakfastReportSingleThread singleThreadObj = new BreakfastReportSingleThread();
				singleThreadObj.threadNumber=i;
				singleThreadObj.userID=userID;
				singleThreadObj.mobileNumber=mobileNum;
				singleThreadObj.mappingCode=mappingCode;
				singleThreadObj.batchReportPath=batchReportPath;
				singleThreadObj.latch=latch;
				
				Callable<String> worker = singleThreadObj;
        		executor.submit(worker);
        		
        		
			}
			
			/*executor.shutdown(); 
    		while (!executor.isTerminated()) {   }  
	       	executor.shutdownNow();*/
			
			try 
			{
				//executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
				latch.await();
			} 
			
			catch (InterruptedException e) 
			{
				fLogger.fatal("BreakfastReport:SendBreakfastReport:run():Executor termination exception:"+e.getMessage(),e);
			}
			
		}  	
	       	
		catch(Exception e)
		{
			fLogger.fatal("BreakfastReport:SendBreakfastReport:run():Exception:"+e.getMessage(),e);
		}
		finally
		{
			try
			{
				if(stmt!=null)
					stmt.close();
				if(con!=null)
					con.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("BreakfastReport:SendBreakfastReport:run:Exception in closing the connection:"+e.getMessage(),e);
			}			
		}
	}
		
	
	private static BigDecimal truncateDecimal(final double x, final int numberofDecimals) {
	    return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_DOWN);
	}
	
	public void test() throws IOException
	{
		String path = "D:\\Rajani\\Pers\\Documents\\Mythili\\PrayagMontessori\\M1\\Practise_Workseets\\22887.pdf";
		File file = new File(path);  
		/*  ResponseBuilder response = Response.ok((Object) file);  
        response.header("Content-Disposition","attachment; filename=\""+fileName+".pdf\""); 
        response.type(MediaType.APPLICATION_OCTET_STREAM_TYPE);
        return response.build(); */
        
        InputStream in =  new FileInputStream(file);
        System.out.println(in);
        System.out.println( IOUtils.toByteArray(in));
	}
}
