package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.SellerDetailsRESTImpl;

@Path("/SellerDetails")
public class SellerDetailsRESTService {

	@GET
	@Path("getSellerDetails")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<HashMap<String, String>> getSellerDetails() {
		Logger iLogger = InfoLoggerClass.logger;
		List<HashMap<String, String>> response = null;
		iLogger.info("SellerDetailsRESTService::WebService Start ");
		
		long startTime = System.currentTimeMillis();
		
		response = new SellerDetailsRESTImpl().getSellerDetails();

		long endTime=System.currentTimeMillis();

		iLogger.info("serviceName:SellerDetailsRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}
}
