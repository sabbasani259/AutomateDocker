package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.Logger;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

import remote.wise.service.implementation.DownloadMonitoringParameterRESTImpl;

/**
 * @author SU334449
 *
 */

@Path("/MachineReportParameters")
public class DownloadMonitoringParameterRESTService {

	@GET
	@Path("getdetails")
	@Produces(MediaType.TEXT_HTML)
	public String getDownloadMonitoringParam(){
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalLogger = FatalLoggerClass.logger;
		String status = "FAILURE";
		DownloadMonitoringParameterRESTImpl implObj = new DownloadMonitoringParameterRESTImpl();
		try{
			long startTime = System.currentTimeMillis();
			status = implObj.getMachineParameterDetails();			
			long endTime = System.currentTimeMillis();
			infoLogger.info("serviceName:DownloadMonitoringParameterRESTService~executionTime:"+(endTime - startTime)+"~"+""+"~"+status);
		}catch(Exception e){
			fatalLogger.info("Exception in DownloadMonitoringParameterRESTService: "+ e);
		}
		return status;
	}
}
