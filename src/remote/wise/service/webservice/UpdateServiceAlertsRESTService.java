package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.Logger;

import remote.wise.dao.UpdateServiceAlertsDAO;
import remote.wise.log.InfoLogging.InfoLoggerClass;

//CR366 - 20221114 - Prasad - Closure of Service Alerts
@Path("/UpdateServiceAlertsRESTService")
public class UpdateServiceAlertsRESTService {

	
	@GET()
	@Path("UpdateServiceAlerts")
	@Produces("text/plain")
	public String UpdateServiceAlerts(){
		
	   
		Logger iLogger = InfoLoggerClass.logger;
		int months = 3 ;
		String response_msg= new UpdateServiceAlertsDAO().closeServiceAlerts(months);
		
		iLogger.info("Webservice Output: " + response_msg );
		return response_msg;
		
	}

}



