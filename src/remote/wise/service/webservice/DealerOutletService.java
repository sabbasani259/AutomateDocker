package remote.wise.service.webservice;
/**
 * @author Z1007653
 * 23-Nov-2020
 */

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.DealerOutletOutputContract;
import remote.wise.service.implementation.DealerOutletImpl;
import remote.wise.util.CommonUtil;

import org.apache.logging.log4j.Logger;

@Path("/DealerOutletService")
public class DealerOutletService {
	/**
	 * Method getDealerOutlet ->
	 * This method is to provide Dealer and their Outlet location details 
	 * @return Dealer and Dealer Outlet details in JSON format : List of DealerOutletOutputContract
	 */
	@GET
	@Path("/getDealerOutlet")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DealerOutletOutputContract> getDealerOutlet(@QueryParam("loginID") String loginID, @QueryParam("category") List<String> category) {
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		
		//User Validation
		loginID=new CommonUtil().getUserId(loginID);
		iLogger.info("DealerOutletService : getDealerOutlet : Decoded userId::"+loginID);
		
		List<DealerOutletOutputContract> result = new DealerOutletImpl().getDealerOutletLocationData(category);
		long endTime = System.currentTimeMillis();
		iLogger.info("DealerOutletService : getDealerOutlet : Service Complete -> total execution time: " + (startTime - endTime) + " ms");
		return result;
	}

}
