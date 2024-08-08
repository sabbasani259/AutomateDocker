package remote.wise.service.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.RetrofitmentReportResponseContract;
import remote.wise.service.implementation.RetrofitmentImpl;
import remote.wise.util.CommonUtil;

/**
 * @author Dhiraj Kumar
 * @since 20230505:CR352
 * CR352 : 20230505 : Dhiraj K : Retrofitment Changes
 */
@Path("/RetrofitmentService")
public class RetrofitmentService {

	@POST
	@Path("/setRetrofitment")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String setRetrofitment(@JsonProperty HashMap<String, String> inputObj) {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String result = "FAILURE";
		long startTime = System.currentTimeMillis();
		try {
			infoLogger.info("Webservice input : " + inputObj);

			CommonUtil util = new CommonUtil();
			String isValidinput = null;

			isValidinput = util.inputFieldValidation(inputObj.get("retrofitPeriod"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(inputObj.get("retrofitStartDate"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(inputObj.get("retrofitEndDate"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(inputObj.get("vin"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(inputObj.get("roleName"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(inputObj.get("tenancyIdList"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			RetrofitmentImpl implObj = new RetrofitmentImpl();
			infoLogger.info("Webservice Input : " + inputObj);
			result = implObj.doRetrofitment(inputObj);
			infoLogger.info("Webservice output : " + result);
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception caught : " + e.getMessage());
		}
		long endTime = System.currentTimeMillis();
		infoLogger.info(
				"serviceName:RetrofitInstallationservice~executionTime:" + (endTime - startTime) + "~" + "" + "~");
		
		return result;
	}
	
	@POST
	@Path("/getRetrofitment")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public HashMap<String, String> getRetrofitmentSubscription(@JsonProperty HashMap<String, String> inputObj) {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		HashMap<String, String> result= new HashMap<String, String>();
		long startTime = System.currentTimeMillis();
		try {
			infoLogger.info("Webservice input : " + inputObj);
			
			CommonUtil util = new CommonUtil();
			String isValidinput = null;
			isValidinput = util.inputFieldValidation(inputObj.get("vin"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			
			RetrofitmentImpl implObj = new RetrofitmentImpl();
			result = implObj.getRetrofitmentSubscription(inputObj);
			infoLogger.info("Webservice output : " + result);
		}catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception caught : " + e.getMessage());
		}
		long endTime = System.currentTimeMillis();
		infoLogger.info(
				"serviceName:RetrofitInstallationservice~executionTime:" + (endTime - startTime) + "~" + "" + "~");
		
		return result;
	}
	
	@POST
	@Path("/getRetrofitmentReport")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String getRetrofitmentReport(@JsonProperty HashMap<String, String> inputObj) {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<RetrofitmentReportResponseContract> result= new ArrayList<RetrofitmentReportResponseContract>();
		String response= "FAILURE";
		long startTime = System.currentTimeMillis();
		try {
			infoLogger.info("Webservice input : " + inputObj);
			
			CommonUtil util = new CommonUtil();
			String isValidinput = null;
			isValidinput = util.inputFieldValidation(inputObj.get("tenancyIdList"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}

			isValidinput = util.inputFieldValidation(inputObj.get("roleName"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(inputObj.get("startDate"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(inputObj.get("endDate"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			
			RetrofitmentImpl implObj = new RetrofitmentImpl();
			result = implObj.getRetrofitmentReport(inputObj);
			
			if (result!=null) {
				Gson gson = new GsonBuilder().create();
				response = gson.toJson(result);
			}
			infoLogger.info("Webservice output : " + response);
		}catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception caught : " + e.getMessage());
		}
		long endTime = System.currentTimeMillis();
		infoLogger.info(
				"serviceName:RetrofitInstallationservice~executionTime:" + (endTime - startTime) + "~" + "" + "~");
		
		return response;
	}
	
	@POST
	@Path("/validateEligibity")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public HashMap<String, String> validateEligibity(@JsonProperty HashMap<String, String> inputObj) {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		HashMap<String, String> result= new HashMap<>();
		long startTime = System.currentTimeMillis();
		try {
			infoLogger.info("Webservice input : " + inputObj);
			
			CommonUtil util = new CommonUtil();
			String isValidinput = null;
			isValidinput = util.inputFieldValidation(inputObj.get("vin"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			RetrofitmentImpl implObj = new RetrofitmentImpl();
			result = implObj.validateEligibilty(inputObj);
			infoLogger.info("Webservice output : " + result);
		}catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception caught : " + e.getMessage());
		}
		long endTime = System.currentTimeMillis();
		infoLogger.info(
				"serviceName:RetrofitInstallationservice~executionTime:" + (endTime - startTime) + "~" + "" + "~");
		
		return result;
	}

}
