package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.EventDetailsBO;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@Path("/UpdateAssetEventAddress")
public class UpdateAssetEventAddressRest {
	
	
	
	@Path("/updateAEAddress")
	@GET()
	@Produces("text/plain")
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
