package remote.wise.service.webservice;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.VinDisconnectedStatusImpl;


/**
 * @author AJ20119610
 *
 */
@Path("/VinDisconnectedStatusRESTService")
public class VinDisconnectedStatusRESTService {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/vinDisconnectedStatus")
	public String updateDisconnectedVINSFromVF(@QueryParam("vin") String vin) {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		infoLogger.info("Webservice input : "
				+ "vin: "+vin);
		String response = "FAILURE";
		try {

			VinDisconnectedStatusImpl implObj = new VinDisconnectedStatusImpl();
			response=implObj.updateDisconnectedVINSFromVF(vin);
			infoLogger.info("Webservice Output: " + response);
			
		} catch (Exception e) {
			response="FAILURE";
			fLogger.fatal("Exception:" + e.getMessage());
		}
		return response;
		
	}
/*	public static void main(String[] args)  {
		String sourceDir="D:\\input\\";
		String destinationDir="D:\\archive\\";
		new DisconnectedVINSFromVFRESTService().updateDisconnectedVINSFromVF(sourceDir, destinationDir);
}
*/
@GET
@Produces(MediaType.TEXT_PLAIN)
@Path("/getCommunicationStatus")
public String blockingMachineCommunication(@QueryParam("vin") String vin) {
	Logger infoLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;
	infoLogger.info("Webservice for BlockingMachineCommunication input : "
			+ "vin: "+vin);
	String response = "NOT COMMUNICATED";
	try {

		VinDisconnectedStatusImpl implObj = new VinDisconnectedStatusImpl();
		response=implObj.blockingMachineCommunication(vin);
		infoLogger.info("Webservice Output: " + response);
		
	} catch (Exception e) {
		response="NOT COMMUNICATED";
		fLogger.fatal("Exception:" + e.getMessage());
	}
	return response;
	
}
}

	
