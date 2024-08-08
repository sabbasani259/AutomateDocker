package remote.wise.service.webservice;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ServiceClouserReportUpdateImpl;
@Path("/ServiceClouserReportUpdateRESTService")
public class ServiceClouserReportUpdateRESTService {

	@GET
	@Path("setServiceClouserDetails")
	@Produces("text/plain")
	public String setServiceClouserDetails() throws CustomFault
	{
		String result;
		Logger iLogger = InfoLoggerClass.logger;
		long startTime=System.currentTimeMillis();
		iLogger.info("ServiceClouserReportUpdateRESTService:setServiceClouserDetails:WebService Start ");
		result = new ServiceClouserReportUpdateImpl().setServiceCloserDetails();
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:ServiceClouserReportUpdateRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+result);
		return result;
	}
}
