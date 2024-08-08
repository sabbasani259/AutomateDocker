package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.CurrentAssetOwnerDetailsImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "CurrentAssetOwnerDetailsService")
public class CurrentAssetOwnerDetailsService 
{
	@WebMethod(operationName = "setCurrentOwnerDetails", action = "setCurrentOwnerDetails")
	public void setCurrentOwnerDetails()
	{
		//WiseLogger infoLogger = WiseLogger.getLogger("CurrentAssetOwnerDetailsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		
		//-------------- Call Implementation Class
		new CurrentAssetOwnerDetailsImpl().setOwnerDetails();
		
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:CurrentAssetOwnerDetailsService~executionTime:"+(endTime-startTime)+"~"+""+"~");
	}
}
