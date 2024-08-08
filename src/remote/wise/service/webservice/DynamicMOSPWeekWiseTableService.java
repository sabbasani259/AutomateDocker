package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.DynamicMOSPWeekWiseTableServiceImpl;

/**
 * CR434 : 20231006 : Dhiraj K : MOSP Software Id Report changes
 */

@Path("/DynamicMOSPWeekWiseTableService")
public class DynamicMOSPWeekWiseTableService {

	@GET
	@Path("createTable")
	@Produces("text/plain")
	public String createTable(@QueryParam("week") int week, @QueryParam("year") int year) {

		String status = "FAILURE";
		Logger iLogger = InfoLoggerClass.logger;

		long startTime = System.currentTimeMillis();
		status = new DynamicMOSPWeekWiseTableServiceImpl().createTable(week, year);
		long endTime = System.currentTimeMillis();
		iLogger.info("ExecutionTime:" + (endTime - startTime) + "~" + "" + "~" + status);

		return status;
	}

	@GET
	@Path("purgeTable")
	@Produces("text/plain")
	public String purgeTable(@QueryParam("week") int week, @QueryParam("year") int year) {
		String status = "FAILURE";
		Logger iLogger = InfoLoggerClass.logger;

		long startTime = System.currentTimeMillis();
		status = new DynamicMOSPWeekWiseTableServiceImpl().purgeTable(week, year);
		long endTime = System.currentTimeMillis();
		iLogger.info("ExecutionTime:" + (endTime - startTime) + "~" + "" + "~" + status);

		return status;
	}
}
