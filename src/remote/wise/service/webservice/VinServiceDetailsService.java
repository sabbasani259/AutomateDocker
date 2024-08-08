package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.VinServiceDetailsImpl;
//import remote.wise.util.WiseLogger;

/** Web service to set the service details of all VINs as a reporting table
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "VinServiceDetailsService")
public class VinServiceDetailsService 
{
	/** Web Method to set the service details to the reporting table
	 * @return Returns the status String
	 */ 
	@WebMethod(operationName = "setVinServiceDetails", action = "setVinServiceDetails")
	public String setVinServiceDetails()
	{
		//WiseLogger infoLogger = WiseLogger.getLogger("VinServiceDetailsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("---- Entered Webservice: VinServiceDetailsService ------");
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		
		String response = new VinServiceDetailsImpl().setServiceDetails();
		
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response);
		
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		
		iLogger.info("serviceName:VinServiceDetailsService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		
		return response;
	}
	
	}
