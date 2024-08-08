package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.ws.rs.GET;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.Logger;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ReProcessVINDetailsToUpdateRedisRestServiceImpl;

@Path("/ReProcessVINDetailsToUpdateRedisRestService")
public class ReProcessVINDetailsToUpdateRedisRestService {
	@GET
	@Path("/ReProcessVINDetailsToUpdateRedis")
	@Produces("text/plain")
	public String ReProcessVINDetailsToUpdateRedis() throws Exception {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = "SUCCESS";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate: " + startDate);
		long startTime = System.currentTimeMillis();		
		try {
			ReProcessVINDetailsToUpdateRedisRestServiceImpl reprocess = new ReProcessVINDetailsToUpdateRedisRestServiceImpl();			
			response = reprocess.moolDAUpdationForVinDetails();
			infoLogger.info("Webservice Output -- ReProcessVINDetailsToUpdateRedisRestService: moolDAUpdationForVinDetails: Response : "
							+ response);
			long endTime = System.currentTimeMillis();
			infoLogger.info("Webservice Execution Time in ms:"
					+ (endTime - startTime));			
		} catch (Exception e) {
			fLogger.info("Error in calling ReProcessVINDetailsToUpdateRedisRestServiceImpl for moolDAUpdationForVinDetails() "
					+ e.getMessage());
			return "FAILURE";
		}
		return response;

	}

}
