package remote.wise.service.webservice;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.InstallationDateDetailsImpl;
import remote.wise.service.implementation.PullSmsImpl;

/*
 * web service for updating asset service schedule whenever the services are extended in service schedules
 * @author S Suresh Babu
 */

@Path("/ServiceScheduleUpdate")
public class AssetServiceScheduleRestService {

	@Path("/updateAssetServiceSchedule")
	@GET()
	@Produces("text/plain")
	public String updateAssetServiceSchedule(@QueryParam("carePlus")String carePlus,@QueryParam("asset_group_id")String asset_group_id,
			@QueryParam("asset_type_id")String asset_type_id,@QueryParam("engine_type_id")String engine_type_id) {
		Logger iLogger = InfoLoggerClass.logger;
		
		iLogger.info("---- Webservice Input ------");
		iLogger.info("CarePlus:"+carePlus);
		
		String response = new InstallationDateDetailsImpl().updateAssetServiceSchedule(carePlus);
		iLogger.info("---- Webservice Output ------");
		iLogger.info("Output Status:"+response);
		
		return response;
	}
	
	@Path("/closeOutdatedServiceAlerts")
	@GET()
	@Produces("text/plain")
	public String closeOutdatedServiceAlerts(){
		Logger iLogger = InfoLoggerClass.logger;
		
		iLogger.info("---- Webservice Input ------");
		
		String response = new InstallationDateDetailsImpl().closeOutdatedServiceAlerts();
		iLogger.info("---- Webservice Output ------");
		iLogger.info("Output Status:"+response);
		
		return response;
	}
}
