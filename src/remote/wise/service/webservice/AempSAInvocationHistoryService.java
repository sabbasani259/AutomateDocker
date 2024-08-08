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
import remote.wise.service.implementation.AempSAInvocationHistoryServiceImpl;
import remote.wise.service.implementation.AempViewClientImpl;

@Path("/AempSAViewInvocationHistory")
public class AempSAInvocationHistoryService {

	@GET
	@Path("ViewSA")
	@Produces(MediaType.APPLICATION_JSON)
	public String viewSAInvocation(@Context HttpHeaders httpHeaders) throws CustomFault {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String result = null;
		iLogger.info("AempSAInvocationHistoryService:viewSAInvocation:WebService Start ");
		long startTime = System.currentTimeMillis();

		try {
			AempSAInvocationHistoryServiceImpl impl = new AempSAInvocationHistoryServiceImpl();
			System.out.println("from service ");
			result = impl.viewSAInvocation(httpHeaders);

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
