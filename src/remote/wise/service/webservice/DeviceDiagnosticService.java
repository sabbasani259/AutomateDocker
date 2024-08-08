package remote.wise.service.webservice;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AssetDiagnosticImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "DeviceDiagnosticService")
public class DeviceDiagnosticService 
{
	@WebMethod(operationName = "setEventData", action = "setEventData")
	public String setEventData(@WebParam(name="xmlInput" ) String xmlInput, @WebParam(name="count" ) int count) throws ParseException
	{
		//WiseLogger infoLogger = WiseLogger.getLogger("DeviceDiagnosticService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Properties prop = new Properties();
		InputStream dataFile;
		String responseStatus = "FAILURE";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		//--------------------------Step1: Forward the request to Asset Diagnostic Impl for given number of times
		for(int i=0; i<count; i++)
		{
			if(i>0)
			{
				int index=xmlInput.lastIndexOf("SnapshotTime=")+14;
				String dateTime=xmlInput.substring(index, index+10)+" "+xmlInput.substring(index+11,index+19);
				Date receivedDate = df.parse(dateTime);
				Calendar cal = Calendar.getInstance();
				cal.setTime(receivedDate);
				cal.add(Calendar.SECOND, 1);
				String newTime = df.format(cal.getTime()); 
				newTime = newTime.substring(0,10)+"T"+newTime.substring(11, 19)+"Z";
				xmlInput = xmlInput.replaceAll(xmlInput.substring(index, index+20), newTime);
			}
			
			
			iLogger.info("Device Diagnostic - ---- Webservice Input ------");
			iLogger.info(" Device Diagnostic - xmlInput:"+xmlInput);
			
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
			String startDate = sdf.format(cal.getTime());
			long startTime = System.currentTimeMillis();
			iLogger.info("Device Diagnostic - Current Startdate: "+startDate);
			
			AssetDiagnosticImpl assetDiagnosticObj = new AssetDiagnosticImpl();
			responseStatus = assetDiagnosticObj.setAssetMonitoringData(xmlInput);
			
			Calendar cal1 = Calendar.getInstance();
			String endDate = sdf.format(cal1.getTime());
			long endTime=System.currentTimeMillis();
			iLogger.info("Device Diagnostic - Current Enddate: "+endDate);
					
			iLogger.info("serviceName:DeviceDiagnosticService~executionTime:"+(endTime - startTime)+"~"+""+"~"+responseStatus);
			iLogger.info("Device Diagnostic - ----- Webservice Output-----");
			iLogger.info("Device Diagnostic - Status:"+responseStatus);
			
		}
		
		//----------------------------Step2: Invoke the Shell Script for Predective Asset Event
		int computePA =0;
		try 
		{
			dataFile = new FileInputStream("/user/Downloads/SAPPHIRE/padata.properties");
			//dataFile = new FileInputStream("E:\\data\\SAPPHIRE\\padata.properties");
			prop.load(dataFile);
		} 
		
		catch (FileNotFoundException e) 
		{
			fLogger.fatal("Device Diagnostic - Predictive Analysis : "+new Date()+" Property File for Sapphire PA data not found. Hence data not extracted");
			return "FAILURE";
		}
		
		catch (Exception e)
		{
			fLogger.fatal("Device Diagnostic - Predictive Analysis: "+new Date()+" Exception in finding Property File for Sapphire PA");
			return "FAILURE";
		}
		
		computePA = Integer.parseInt(prop.getProperty("computePA"));
		if(computePA==1)
		{
			//Invoke the shell script for Predective Asset Event
			 //ProcessBuilder pb= new ProcessBuilder().command("/bin/bash" , "-c", "/user/Downloads/SAPPHIRE/SapphirePA.sh");
			 try 
			 {
				Runtime.getRuntime().exec("/user/Downloads/SAPPHIRE/SapphirePA.sh");
			 } 
			 catch (IOException e) 
			 {
				 fLogger.fatal("Device Diagnostic - Predictive Analysis: "+new Date()+" Exception in executing PA Shell Script");
				 return "FAILURE";
			}
		}
		
		
		//----------------------------Step3: Invoke the Shell Script for AMH, AMD and Asset Event data extraction
		int extractData=0;
		try 
		{
			dataFile = new FileInputStream("/user/Downloads/SAPPHIRE/data.properties");
			//dataFile = new FileInputStream("E:\\data\\SAPPHIRE\\data.properties");
			prop.load(dataFile);
		} 
		
		catch (FileNotFoundException e) 
		{
			fLogger.fatal("Device Diagnostic: "+new Date()+" Property File for Sapphire data not found. Hence data not extracted");
			return "FAILURE";
		}
		
		catch (Exception e)
		{
			fLogger.fatal("Device Diagnostic: "+new Date()+" Exception in finding Property File for Sapphire");
			return "FAILURE";
		}
		
		extractData = Integer.parseInt(prop.getProperty("extractData"));
		if(extractData==1)
		{
			//Invoke the shell script for data extraction
			// ProcessBuilder pb= new ProcessBuilder().command("/bin/bash" , "-c", "/user/Downloads/SAPPHIRE/Sapphire.sh");
			 try 
			 {
				Runtime.getRuntime().exec("/user/Downloads/SAPPHIRE/Sapphire.sh");
			 } 
			 catch (IOException e) 
			 {
				 fLogger.fatal("Device Diagnostic : "+new Date()+" Exception in executing Sapphire Shell Script");
				 return "FAILURE";
			} 
		}
		
		return responseStatus;
	}
	
	
	@WebMethod(operationName = "setDeviceData", action = "setDeviceData")
	public String setDeviceData(@WebParam(name="xmlInput" ) String xmlInput) throws FileNotFoundException
	{
		//WiseLogger infoLogger = WiseLogger.getLogger("DeviceDiagnosticService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Properties prop = new Properties();
		InputStream dataFile;
		
		//--------------------------Step1: Forward the request to Asset Diagnostic Impl
				String responseStatus = "FAILURE";
		iLogger.info("Device Diagnostic ---- Webservice Input ------");
		iLogger.info("Device Diagnostic - xmlInput:"+xmlInput);
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		long startTime = System.currentTimeMillis();
		iLogger.info("Device Diagnostic - Current Startdate: "+startDate);
		
		AssetDiagnosticImpl assetDiagnosticObj = new AssetDiagnosticImpl();
		responseStatus = assetDiagnosticObj.setAssetMonitoringData(xmlInput);
		
		Calendar cal1 = Calendar.getInstance();
		String endDate = sdf.format(cal1.getTime());
		long endTime=System.currentTimeMillis();
		iLogger.info("Device Diagnostic - Current Enddate: "+endDate);
				
		iLogger.info("Device Diagnostic - Webservice Execution Time in ms:"+(endTime-startTime));
		iLogger.info("Device Diagnostic ------ Webservice Output-----");
		iLogger.info("Device Diagnostic - Status:"+responseStatus);
		
		//----------------------------Step2: Invoke the Shell Script for Predective Asset Event
		int computePA =0;
		try 
		{
			dataFile = new FileInputStream("/user/Downloads/SAPPHIRE/padata.properties");
			//dataFile = new FileInputStream("E:\\data\\SAPPHIRE\\padata.properties");
			prop.load(dataFile);
		} 
		
		catch (FileNotFoundException e) 
		{
			fLogger.fatal("Device Diagnostic - Predictive Analysis : "+new Date()+" Property File for Sapphire PA data not found. Hence data not extracted");
			return "FAILURE";
		}
		
		catch (Exception e)
		{
			fLogger.fatal("Device Diagnostic - Predictive Analysis: "+new Date()+" Exception in finding Property File for Sapphire PA");
			return "FAILURE";
		}
		
		computePA = Integer.parseInt(prop.getProperty("computePA"));
		if(computePA==1)
		{
			//Invoke the shell script for Predective Asset Event
			 //ProcessBuilder pb= new ProcessBuilder().command("/bin/bash" , "-c", "/user/Downloads/SAPPHIRE/SapphirePA.sh");
			 try 
			 {
				Runtime.getRuntime().exec("/user/Downloads/SAPPHIRE/SapphirePA.sh");
			 } 
			 catch (IOException e) 
			 {
				 fLogger.fatal("Device Diagnostic - Predictive Analysis: "+new Date()+" Exception in executing PA Shell Script");
				 return "FAILURE";
			}
		}
		
		
		//----------------------------Step3: Invoke the Shell Script for AMH, AMD and Asset Event data extraction
		int extractData=0;
		try 
		{
			dataFile = new FileInputStream("/user/Downloads/SAPPHIRE/data.properties");
			//dataFile = new FileInputStream("E:\\data\\SAPPHIRE\\data.properties");
			prop.load(dataFile);
		} 
		
		catch (FileNotFoundException e) 
		{
			fLogger.fatal("Device Diagnostic: "+new Date()+" Property File for Sapphire data not found. Hence data not extracted");
			return "FAILURE";
		}
		
		catch (Exception e)
		{
			fLogger.fatal("Device Diagnostic: "+new Date()+" Exception in finding Property File for Sapphire");
			return "FAILURE";
		}
		
		extractData = Integer.parseInt(prop.getProperty("extractData"));
		if(extractData==1)
		{
			//Invoke the shell script for data extraction
			// ProcessBuilder pb= new ProcessBuilder().command("/bin/bash" , "-c", "/user/Downloads/SAPPHIRE/Sapphire.sh");
			 try 
			 {
				Runtime.getRuntime().exec("/user/Downloads/SAPPHIRE/Sapphire.sh");
			 } 
			 catch (IOException e) 
			 {
				 fLogger.fatal("Device Diagnostic : "+new Date()+" Exception in executing Sapphire Shell Script");
				 return "FAILURE";
			}
		}
		
		return responseStatus;
	}
}
