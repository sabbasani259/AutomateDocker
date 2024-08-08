/**
 * JCB6337 : Dhiraj Kumar : 20230405 : Tenancy Bridge population logic into WISE
 */

package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.TenancyBridgeImpl;

@Path("/TenancyBridgeService")
public class TenancyBridgeService {

	@GET
	@Path("PopulateTenancyBridge")
	@Produces({ MediaType.APPLICATION_JSON })
	public String populateTenancyBridge() {
		Logger iLogger = InfoLoggerClass.logger;
		String response = "FAILURE";
		iLogger.info("TenancyBridgeService::WebService Start ");
		long startTime = System.currentTimeMillis();
		
		TenancyBridgeImpl impl = new TenancyBridgeImpl();
		//response = impl.populateTenancyBridge();
		response = impl.setTenancyBridge();
		
		long endTime=System.currentTimeMillis();
		iLogger.info("ServiceName:TenancyBridgeService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}
}
