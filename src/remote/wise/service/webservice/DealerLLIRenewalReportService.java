package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.Logger;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.DealerLLIRenewalReportImpl;
import remote.wise.exception.CustomFault;

/**
 * @author KO369761
 *
 */

@Path("/RenewalMachinesReport")
public class DealerLLIRenewalReportService {

	@POST
	@Path("getRenewalMachinesReport")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({MediaType.APPLICATION_JSON})
	public List<HashMap<String,String>> getRenewalApproachingMachinesReport(LinkedHashMap<String,Object> reqObj) throws CustomFault{
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<HashMap<String,String>> response = new LinkedList<HashMap<String,String>>();

		try{
			long startTime = System.currentTimeMillis();
			infoLogger.info("Webservice input : "+reqObj);
			DealerLLIRenewalReportImpl implObj = new DealerLLIRenewalReportImpl();
			response = implObj.getRenewalApproachingMachines(reqObj);
			infoLogger.info("Webservice ouput : "+response);
			long endTime = System.currentTimeMillis();
			infoLogger.info("serviceName:DealerLLIRenewalReportService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		}catch(Exception e){
			e.printStackTrace();
			fLogger.error("Exception :"+e.getMessage());
		}
		return response;
	}
}

