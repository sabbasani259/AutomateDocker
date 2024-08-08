package remote.wise.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.FileProcessAssetDiagnosticImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "FileProcessAssetDiagnosticService")
public class FileProcessAssetDiagnosticService 
{
	@WebMethod(operationName = "processDataPackets", action = "processDataPackets")
	  public String processDataPackets(@WebParam(name="msg" ) String msg) 
	{
		//WiseLogger infoLogger = WiseLogger.getLogger("FileProcessAssetDiagnosticService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		//DF20140225 - Rajani Nagaraju - Commenting sysout statement
		//infoLogger.info("Entering WebService - File Process Asset Diagnostic Data");
		FileProcessAssetDiagnosticImpl fileProcessObj = new FileProcessAssetDiagnosticImpl();
		fileProcessObj.handleDataPackets();
		
		//DF20140225 - Rajani Nagaraju - Commenting sysout statement
		//infoLogger.info("Exiting WebService  - File Process Asset Diagnostic Data");
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:FileProcessAssetDiagnosticService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return "SUCCESS";
	}
}
