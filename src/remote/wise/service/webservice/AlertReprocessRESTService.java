/**
 * 
 */
package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AlertReprocessImpl;

/**
 * @author ROOPN5
 *
 */

	@Path("/AlertReprocessRESTService")
	public class AlertReprocessRESTService {
		
		@GET
		@Path("reprocessAlerts")
		@Produces("text/plain")
		public String reprocessAlerts(@QueryParam("serialNumber") String serialNumber,@QueryParam("createdTimeStartDate") String createdTimeStartDate,@QueryParam("createdTimeEndDate") String createdTimeEndDate) 
		{
			String status="SUCCESS";
			Logger iLogger = InfoLoggerClass.logger;
			
			iLogger.info("---- Webservice Input ------");
			iLogger.info("SerialNumber: "+serialNumber+", createdTimeStartDate: "+createdTimeStartDate+", createdTimeEndDate: "+createdTimeEndDate);
			
			status = new AlertReprocessImpl().reprocessAlerts(serialNumber, createdTimeStartDate, createdTimeEndDate);
			
			iLogger.info("---- Webservice Output ------");
			iLogger.info("Status: "+status);
			
			return status;
			
		}
}
