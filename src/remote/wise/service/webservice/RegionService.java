package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.Logger;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;

import remote.wise.service.implementation.RegionServiceImpl;

/**
 * @author SU334449
 *
 */

@Path("/regionService")
public class RegionService {

	@GET
	@Path("getRegions")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRegions(@QueryParam("parentTenancyID") int parentTenancyID) throws CustomFault{
		Logger fLogger = FatalLoggerClass.logger;
		String result = null;
		RegionServiceImpl implObj = new RegionServiceImpl();
		if(parentTenancyID!=0){
			try{
				result = implObj.getRegionServiceDetails(parentTenancyID);
			}catch(Exception e){
				fLogger.error("Exception: "+e);
			}
		}
		return result;
	}
}