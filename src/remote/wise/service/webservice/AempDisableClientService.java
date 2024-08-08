 // CR419 :Santosh : 20230714 :Aemp Changes
package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AempDisableClientImpl;

@Path("/AempDisableClientService")
public class AempDisableClientService {

	@GET
	@Path("Disable")
	@Produces(MediaType.TEXT_PLAIN)
	public String createClient(@Context HttpHeaders httpHeaders, @QueryParam("clientId") int clientId)
			throws CustomFault {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = "Failure";
		iLogger.info("AempDisableClientService:DisableClient:WebService Start : Client Id " + clientId);

		long startTime = System.currentTimeMillis();

		try {
			// Validate client id is not 0
			if(clientId==0) {
				fLogger.error("Failure ClientId should not be zero : "+ clientId);
			}
			AempDisableClientImpl impl = new AempDisableClientImpl();
			response = impl.disableClient(httpHeaders, clientId);

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
