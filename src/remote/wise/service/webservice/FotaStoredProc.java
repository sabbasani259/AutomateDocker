package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AdminAlertPrefRespContract;
import remote.wise.service.datacontract.FotaStoredProcRespContract;
import remote.wise.service.implementation.AdminAlertPrefImpl;
import remote.wise.service.implementation.setFotaStoredImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "FotaStoredProc")
public class FotaStoredProc {
//	public static WiseLogger infoLogger = WiseLogger.getLogger("FotaStoredProc:","info");
	
	 Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalError = FatalLoggerClass.logger;
		Logger businessError = BusinessErrorLoggerClass.logger;
	@WebMethod(operationName = "FotaStoredProc", action = "FotaStoredProc")
	public String setFotaStoredProc() throws CustomFault
	{
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate : "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("----- Webservice Input-----");
		String response = new setFotaStoredImpl().setFotaStored();
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate : "+endDate);
		long endTime = System.currentTimeMillis();
		infoLogger.info("serviceName:FotaStoredProc~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;

	}

}

