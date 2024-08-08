/**
 * 
 */
package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ApplicationAlertClosureImpl;

/**
 * @author ROOPN5
 *
 */
@Path("/ApplicationAlertClosureRESTService")
public class ApplicationAlertClosureRESTService {
	
	@GET()
	@Path("closeAlerts")
	@Produces("text/plain")
	 public String closeAlerts()  throws CustomFault

	  {
		
		Logger iLogger = InfoLoggerClass.logger;
		
		iLogger.info("ApplicationAlertClosureRESTService"+"Start");
		
		long startTime = System.currentTimeMillis();
		
		String response= new ApplicationAlertClosureImpl().closeAlerts();
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:ApplicationAlertClosureRESTService~executionTime:"+(endTime - startTime)+"~"+""+"~"+response);
		iLogger.info("ApplicationAlertClosureRESTService----- Webservice Output-----"+response);
		return response;
	  }

}
