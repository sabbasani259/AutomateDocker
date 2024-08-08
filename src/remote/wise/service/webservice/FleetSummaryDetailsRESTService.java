package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import org.apache.logging.log4j.Logger;

import remote.wise.service.implementation.FleetSummaryDetailsRESTImpl;

/**
 * @author KO369761
 *
 */

@Path("/FleetSummary")
public class FleetSummaryDetailsRESTService {

	@GET
	@Path("getdetails")
	@Produces(MediaType.TEXT_HTML)
	public String getFleetSummaryService() throws CustomFault{

		Logger infoLogger = InfoLoggerClass.logger;
		String result = "FAILURE";
		FleetSummaryDetailsRESTImpl implObj = new FleetSummaryDetailsRESTImpl();
		try{
			long startTime = System.currentTimeMillis();
			//result = implObj.getFleetSummaryServiceDetails();	
			//DF20190201 :: Mani :: Populating the fleet_summary_chart_tempdata from MOOL
			result = implObj.getFleetSummaryServiceDetails_New();			
			long endTime = System.currentTimeMillis();
			infoLogger.info("serviceName:FleetSummaryDetailsRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+result);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
}
