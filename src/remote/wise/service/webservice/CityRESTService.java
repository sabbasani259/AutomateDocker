/**
 * 
 */
package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.CityRespContract;
import remote.wise.service.implementation.CityImpl;

/**
 * @author KI270523
 *
 */
@Path("/CityRESTService")
public class CityRESTService {


	@GET()
	@Produces("text/plain")
	@Path("/GetCities")
	public String GetCities(@QueryParam("stateId") String stateId) throws CustomFault{

		
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		//System.out.println("state id---"+stateId);
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		
		String respObj = new CityImpl().getCitiesForRESTService(stateId);
		iLogger.info("<-----Webservice Output----->");
		
			iLogger.info("City Response from REST Service::"+respObj);
		
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:CityRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respObj;
	}


}
