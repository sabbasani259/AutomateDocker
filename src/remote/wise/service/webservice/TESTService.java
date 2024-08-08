package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;

@Path("/TESTService")
public class TESTService {

	@GET
	@Path("/testMethod")
	@Produces(MediaType.TEXT_PLAIN)
	public String testMethod()
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("invoked test method");
		iLogger.info("invoked test method1");
		return "SUCCESS";
	}
}
