package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AempApprovelClientImpl;

@Path("/AempApprovalClient")
public class AempApprovelClient {
	
	@GET
	@Path("ApproveSaClient")
	@Produces(MediaType.TEXT_PLAIN)
	public String createClient(@Context HttpHeaders httpHeaders)
			throws CustomFault {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = "Failure";
		iLogger.info("AempApprovelClient:ApprovalClient:WebService Start " );

		long startTime = System.currentTimeMillis();

		try {
			
			AempApprovelClientImpl impl = new AempApprovelClientImpl();
			response = impl.approveClient(httpHeaders);

			long endTime = System.currentTimeMillis();
			iLogger.info("AempClientService:DisableClient:WebService End:status:" + response + ":ExecutionTime:"
					+ (endTime - startTime) + "~" + "" + "~");
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception caught : " + e.getMessage());
		}
		return response;
	}

}
