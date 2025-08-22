package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.SoftwareIdLogServiceImpl;
import remote.wise.util.CommonUtil;

@Path("/SoftwareIdLogService")
public class SoftwareIdLogService {

	@GET
	@Path("/getSoftwareIdLog")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSoftwareIdLog(@QueryParam("vin") String vin, @QueryParam("date") String date) {

		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		long start = System.currentTimeMillis();
		String jsonResponse = "FAILURE";
		try {
			infoLogger.info("--------Webservice input------");
			infoLogger.info("Input: vin: " + vin + " date: " + date);
			CommonUtil util = new CommonUtil();
			String isValidinput1 = util.inputFieldValidation(String.valueOf(vin));
			if (!isValidinput1.equals("SUCCESS")) {
				fLogger.info("Invalid input parameter: " + vin);
				throw new CustomFault(isValidinput1);
			}
			String isValidinput2 = util.inputFieldValidation(String.valueOf(date));
			if (!isValidinput2.equals("SUCCESS")) {
				fLogger.info("Invalid input parameter: " + date);
				throw new CustomFault(isValidinput1);
			}
			jsonResponse = new SoftwareIdLogServiceImpl().getSoftwareIdLog(vin, date);
		} catch (CustomFault e) {
			jsonResponse = "FAILURE";
			fLogger.fatal("FAILURE : Invalid Input" + e.getFaultInfo());
		} catch (Exception e) {
			jsonResponse = "FAILURE";
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		infoLogger.info("Service Execution Completed: Time taken:" + (end - start) + " ms");
		return jsonResponse;
	}
}
