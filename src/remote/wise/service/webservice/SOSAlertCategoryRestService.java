/**
 * 
 */
package remote.wise.service.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.SOSAlertCategoryImpl;

/**
 * @author KO369761
 * 
 */
@Path("/SOSAlert")
public class SOSAlertCategoryRestService {

	@GET
	@Path("getSOSAlertCategories")
	@Produces(MediaType.APPLICATION_JSON)
	public List<HashMap<String, String>> getSOSAlertCategories(@QueryParam("asset_event_id")int assetEventId ) {

		Logger iLogger = InfoLoggerClass.logger;
		List<HashMap<String, String>> response = new ArrayList<HashMap<String, String>>();

		iLogger.info("SOSAlertyCategoryRestService:getSOSAlertCategories WebService START");
		long startTime = System.currentTimeMillis();

		response = new SOSAlertCategoryImpl().getSOSAlertCategories(assetEventId);

		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:SOSAlertCategoryRestService~executionTime:"+(endTime-startTime)+"~"+""+"~");

		return response;
	}
}
