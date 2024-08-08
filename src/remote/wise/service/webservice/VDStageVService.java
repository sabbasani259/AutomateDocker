/**
 * CR397 : 20230831 : Dhiraj Kumar : StageV VD development changes
 */

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
import remote.wise.service.implementation.VDStageVServiceImpl;
import remote.wise.util.CommonUtil;

@Path("/VDStageVService")
public class VDStageVService {

	@GET
	@Path("DpfFillLevelPTBarGraph")
	@Produces(MediaType.APPLICATION_JSON)
	public String dpfFillLevelPowerTrain(@QueryParam(value = "vin") String vin,
			@QueryParam(value = "profile") String profile, @QueryParam(value = "model") String model) {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalLogger = FatalLoggerClass.logger;
		long startTimeMillis = System.currentTimeMillis();
		infoLogger.info("Webservice Input: VIN:" + vin + ":profile:" + profile + ":model:" + model);
		String response = "FAILURE";
		try {
			CommonUtil util = new CommonUtil();
			String isValidinput = null;
			isValidinput = util.inputFieldValidation(vin);
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault("Invalid Serial Number");
			}
			isValidinput = util.inputFieldValidation(profile);
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault("Invalid Profile:" + profile);
			}
			isValidinput = util.inputFieldValidation(model);
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault("Invalid Model:" + model);
			}
			VDStageVServiceImpl impl = new VDStageVServiceImpl();
			response = impl.getDpfFillLevelBarGraphPT(vin, profile, model);
		} catch (Exception e) {
			fatalLogger.fatal("Exception occurred:" + e.getMessage());
		}

		long endTimeMillis = System.currentTimeMillis();
		infoLogger
				.info("Webservice Output :" + response + ": Time taken:" + (endTimeMillis - startTimeMillis) + "ms~~");
		return response;
	}

	@GET
	@Path("DpfServicePTLineGraph")
	@Produces(MediaType.APPLICATION_JSON)
	public String dpfServiceGraphPowerTrain(@QueryParam(value = "vin") String vin,
			@QueryParam(value = "startDate") String startDate, @QueryParam(value = "endDate") String endDate,
			@QueryParam(value = "profile") String profile, @QueryParam(value = "model") String model) {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalLogger = FatalLoggerClass.logger;
		long startTimeMillis = System.currentTimeMillis();
		infoLogger.info("Webservice Input:vin:" + vin + ":startDate:" + startDate + ":endDate:" + endDate + ":profile:"
				+ profile + ":model:" + model);
		String response = "FAILURE";
		try {
			CommonUtil util = new CommonUtil();
			String isValidinput = null;
			isValidinput = util.inputFieldValidation(vin);
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault("Invalid Serial Number:" + vin);
			}
			isValidinput = util.inputFieldValidation(startDate);
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault("Invalid StartDate:" + startDate);
			}
			isValidinput = util.inputFieldValidation(endDate);
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault("Invalid EndDate:" + endDate);
			}
			isValidinput = util.inputFieldValidation(profile);
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault("Invalid Profile:" + profile);
			}
			isValidinput = util.inputFieldValidation(model);
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault("Invalid Model:" + model);
			}
			VDStageVServiceImpl impl = new VDStageVServiceImpl();
			response = impl.getDpfServiceLineGraphDataPT(vin, startDate, endDate, profile, model);
		} catch (Exception e) {
			fatalLogger.fatal("Exception occurred:" + e.getMessage());
		}

		long endTimeMillis = System.currentTimeMillis();
		infoLogger.info("Webservice: response" + response +":Time taken:" + (endTimeMillis - startTimeMillis) + "ms~~");
		return response;

	}

}
