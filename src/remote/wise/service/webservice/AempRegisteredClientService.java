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
import remote.wise.service.implementation.AempRegisteredClientImpl;

@Path("/AempRegisteredClientService")
public class AempRegisteredClientService {
		
		@GET
		@Path("ViewClient")
		@Produces(MediaType.APPLICATION_JSON)
		public String viewClientForTenancyId(@Context HttpHeaders httpHeaders,@QueryParam("tenancyId") String tenancyId) throws CustomFault {
			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
			String result = null;
			iLogger.info("AempRegisteredClientService:registeredClient:WebService Start ");
			long startTime = System.currentTimeMillis();

			try {
				AempRegisteredClientImpl impl = new AempRegisteredClientImpl();
				result = impl.viewClient(httpHeaders,tenancyId);

				long endTime = System.currentTimeMillis();
				iLogger.info("AempClientService:createClient:WebService End:status:" + result + ":ExecutionTime:"
						+ (endTime - startTime) + "~" + "" + "~");
			} catch (Exception e) {
				e.printStackTrace();
				fLogger.error("Exception caught : " + e.getMessage());
			}
			return result;
		}
}
