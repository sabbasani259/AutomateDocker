/**
 * 
 */
package remote.wise.service.implementation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;

/**
 * @author roopn5
 *
 */
@Path("/ETLfactDataJsonService")
public class ETLfactDataJsonService {
	
	@GET()
	@Path("setETLfactJsonData")
	@Produces("text/plain")
	 public String setETLfactJsonData()  throws CustomFault

	  {
		
		
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("---- Webservice Input ------");
		long startTime = System.currentTimeMillis();
		
		String response_msg= new ETLfactDataJsonImpl().setETLfactJsonDetails();
		long endTime = System.currentTimeMillis();
		iLogger.info("Webservice Execution Time in ms:"+(endTime-startTime));
		iLogger.info("----- Webservice Output-----");
		return response_msg;
	  }

}
