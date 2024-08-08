package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.UnblockVinsImpl;


/**
 * @author AJ20119610
 *
 */
//DisconnectedVINSFromVF for blocking
@Path("/UnblockVINsServiceRESTService")
public class UnblockVINsServiceRESTService {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/unblockVINsService")
	public String updateDisconnectedVINSFromVF(@QueryParam("vin") String vin,@QueryParam("loginId") String loginId) {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		infoLogger.info("Webservice input : "
				+ "vin : "+vin+" loginId : "+loginId);
		String response = null;
			try{
				UnblockVinsImpl implObj = new UnblockVinsImpl();
			response=implObj.unblockVin(vin,loginId);
			infoLogger.info("Webservice Output: " + response);
			
		} catch (Exception e) {
			response="FAILURE";
			fLogger.fatal("Exception:" + e.getMessage());
		}
		return response;
	}
}
	
