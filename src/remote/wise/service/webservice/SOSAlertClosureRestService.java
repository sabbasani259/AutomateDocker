/**
 * 
 */
package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.SOSAlertClosureImpl;

/**
 * @author koti
 *
 */
@Path("/AlertClosure")
public class SOSAlertClosureRestService {
	
	@GET
	@Path("SOS")
	@Produces(MediaType.TEXT_HTML)
	public String getAlertDetails(){
		String response= null;
		Logger iLogger = InfoLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		response = new SOSAlertClosureImpl().closeSOSAlerts();
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:SOSAlertClosureRestService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		
		return response;
		
		
	}

}
