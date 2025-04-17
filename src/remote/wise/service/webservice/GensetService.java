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
import remote.wise.service.implementation.GensetServiceImpl;
import remote.wise.util.CommonUtil;

//CR424 : 20231127 : prasad : Genset CPCB4+ for Stage V machines 
//LL98: 04172025: Prapoorna: DefLevel value for Genset Machines
@Path("/GensetService")
public class GensetService {

	@GET
	@Path("getCurrentData")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCurrentData(@QueryParam(value = "vin") String vin) {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalLogger = FatalLoggerClass.logger;
		long startTimeMillis = System.currentTimeMillis();
		infoLogger.info("Webservice Input : VIN :" + vin);
		String response = "FAILURE";
		try {
			CommonUtil util = new CommonUtil();
			String isValidinput = null;
			isValidinput = util.inputFieldValidation(vin);
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault("Invalid Serial Number");
			}
			GensetServiceImpl impl = new GensetServiceImpl();
			response = impl.getCurrentData(vin);
		}catch (CustomFault e) {
		    fatalLogger.fatal("Exception occurred:" + e.getFaultInfo());
		    return e.getFaultInfo();
		}
		catch (Exception e) {
			fatalLogger.fatal("Exception occurred:" + e.getMessage());
		}

		long endTimeMillis = System.currentTimeMillis();
		infoLogger
				.info("Webservice Output :" + response + ": Time taken:" + (endTimeMillis - startTimeMillis) + "ms~~");
		return response;
	}
//LL98.sn
	@GET
	@Path("getDefFillLevel")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDefFillLevel(@QueryParam(value = "vin") String vin) {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalLogger = FatalLoggerClass.logger;
		long startTimeMillis = System.currentTimeMillis();
		infoLogger.info("Webservice Input : VIN :" + vin);
		String response = "FAILURE";
		try {
			CommonUtil util = new CommonUtil();
			String isValidinput = null;
			isValidinput = util.inputFieldValidation(vin);
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault("Invalid Serial Number");
			}
			GensetServiceImpl impl = new GensetServiceImpl();
			response = impl.getDefFillLevel(vin);
		}catch (CustomFault e) {
		    fatalLogger.fatal("Exception occurred:" + e.getFaultInfo());
		    return e.getFaultInfo();
		}
		catch (Exception e) {
			fatalLogger.fatal("Exception occurred:" + e.getMessage());
		}

		long endTimeMillis = System.currentTimeMillis();
		infoLogger
				.info("Webservice Output :" + response + ": Time taken:" + (endTimeMillis - startTimeMillis) + "ms~~");
		return response;
	}
//LL98.en
	
	
	@GET
	@Path("getTrendsChartData")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTrendsChartData(@QueryParam(value = "vin") String vin,
			@QueryParam(value = "date") String date) throws CustomFault {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalLogger = FatalLoggerClass.logger;
		long startTimeMillis = System.currentTimeMillis();
		infoLogger.info("Webservice Input : VIN :" + vin + ":date:" + date );
		String response = "FAILURE";
		
		
		try {
			CommonUtil util = new CommonUtil();
			String isValidinput = null;
			isValidinput = util.inputFieldValidation(vin);
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault("Invalid Serial Number");
			}
			isValidinput = util.inputFieldValidation(date);
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault("Invalid Date:" + date);
			}
			response = new GensetServiceImpl().getTrendsChartData(vin,date);
		} catch (CustomFault e) {
		    fatalLogger.fatal("Exception occurred:" + e.getFaultInfo());
		    return e.getFaultInfo();
		}
		catch (Exception e) {
			fatalLogger.fatal("Exception occurred:" + e.getMessage());
		}

		long endTimeMillis = System.currentTimeMillis();
		infoLogger
				.info("Webservice Output :" + response + ": Time taken:" + (endTimeMillis - startTimeMillis) + "ms~~");
		return response;
		
	}

	@GET
	@Path("getDummyLoadBankData")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDummyLoadBankData(@QueryParam(value = "vin") String vin,
			@QueryParam(value = "startDate") String startDate, @QueryParam(value = "endDate") String endDate) {

		Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalLogger = FatalLoggerClass.logger;
		long startTimeMillis = System.currentTimeMillis();
		infoLogger.info("Webservice Input : VIN :" + vin + ":startDate:" + startDate + ":endDate:" + endDate );
		String response = "FAILURE";


		try {
			CommonUtil util = new CommonUtil();
			String isValidinput = null;
			isValidinput = util.inputFieldValidation(vin);
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault("Invalid Serial Number");
			}
			isValidinput = util.inputFieldValidation(startDate);
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault("Invalid Date:" + startDate);
			}
			isValidinput = util.inputFieldValidation(endDate);
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault("Invalid Date:" + endDate);
			}
			response = new GensetServiceImpl().getDummyLoadBankData(vin,startDate,endDate);
		}catch (CustomFault e) {
		    fatalLogger.fatal("Exception occurred:" + e.getFaultInfo());
		    return e.getFaultInfo();
		}
		catch (Exception e) {
			fatalLogger.fatal("Exception occurred:" + e.getMessage());
		}

		long endTimeMillis = System.currentTimeMillis();
		infoLogger
		.info("Webservice Output :" + response + ": Time taken:" + (endTimeMillis - startTimeMillis) + "ms~~");
		return response;
	}

	
	public static void main(String[] args)  {
		System.out.println(new GensetService().getDummyLoadBankData("HAREBA8ZCP2323069", "2024-01-04", "2024-02-12"));
		//System.out.println(new GensetService().getTrendsChartData("JCBEBJ3ZA02220125", "2024-01-04"));
		//System.out.println(new GensetService().getCurrentData("JCBEBJ3ZA02220125"));
	}
	
}
