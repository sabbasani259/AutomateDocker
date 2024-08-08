package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.service.implementation.ECCDealersRESTServiceImpl;

/**
 * @author su334449
 *
 */

@Path("/dealersService")
public class ECCDealersRESTService {

	@GET
	@Path("getECCDealers")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRegions() throws CustomFault{
		String result = null;
		Logger flogger = FatalLoggerClass.logger;
		ECCDealersRESTServiceImpl implObj = new ECCDealersRESTServiceImpl();
		try{
			result = implObj.getECCDealerDetails();
		}catch(Exception e){
			flogger.info("Exception in ECCDealersRESTService:"+e);
		}
		return result;
	}
}








