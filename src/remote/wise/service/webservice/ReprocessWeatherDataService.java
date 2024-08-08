package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ReprocessWeatherDataImpl;

@Path("/ReprocessWeatherDataService")
public class ReprocessWeatherDataService {
	
	@GET
	@Path("reprocessWeatherData")
	@Produces("text/plain")
	public String reprocessWeatherData() {
		Logger iLogger = InfoLoggerClass.logger;
		String response = "SUCCESS";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: " + startDate);
		long startTime = System.currentTimeMillis();
		
		ReprocessWeatherDataImpl impl = new ReprocessWeatherDataImpl();
		response = impl.reprocessWeatherData();
		long endTime = System.currentTimeMillis();
		iLogger.info("ReprocessWeatherDataService :: reprocessWeatherData: Status:"
				+ response + "; Turn around Time (in ms):" + (endTime - startTime));
		return response;
	}

}
