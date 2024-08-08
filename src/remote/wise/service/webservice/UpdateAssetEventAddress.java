package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.EventDetailsBO;
import remote.wise.log.InfoLogging.InfoLoggerClass;
//import remote.wise.util.WiseLogger;

@WebService(name = "UpdateAssetEventAddress")
public class UpdateAssetEventAddress 
{
	@WebMethod(operationName = "updateAEAddress", action = "updateAEAddress")
	public String updateAEAddress()
	{
		//WiseLogger infoLogger = WiseLogger.getLogger("UpdateAssetEventAddress:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		String response= new EventDetailsBO().updateAddess();
		iLogger.info("status:"+response+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UpdateAssetEventAddress~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		
		return response;	
	}
}
