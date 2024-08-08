package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.CmhRefreshOffsetImpl;

/**
 * @author MANI
 * To refresh the CMH Offset for every 20 mins
 * 20 minutes old CMH offset will be set to null 
 *
 */
@Path("/CmhRefreshOffset")
public class CmhRefreshOffsetService {
	@GET()
	@Path("SetCmhOffset")
	@Produces("text/plain")
public String SetCmhOffset(){
		
	long startTime = System.currentTimeMillis();
	Logger iLogger = InfoLoggerClass.logger;
		String response_msg= new CmhRefreshOffsetImpl().cmhRefreshOffset();
		long endTime = System.currentTimeMillis();;
		iLogger.info("serviceName:CmhRefreshOffset~executionTime:"+(endTime-startTime)+"~"+""+"~"+response_msg);
		return response_msg;
	}
}
