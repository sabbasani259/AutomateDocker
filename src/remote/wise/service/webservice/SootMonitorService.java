package remote.wise.service.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

import remote.wise.service.implementation.LLPremiumServiceImpl;
import remote.wise.service.implementation.ReturnMAFlagImpl;
import remote.wise.service.implementation.SootMonitorServiceImpl;
import remote.wise.util.CommonUtil;

@Path("/SootMonitorService")
public class SootMonitorService {
	
	@GET
	@Path("/getSootMonitorReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSootMonitorReport(
			@QueryParam("vin") String vin,@QueryParam("date") String date, 
			@QueryParam("machineGroupIdList") String machineGroupIdList) {//CR471.n
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String jsonResponse = "FAILURE";
		try {
		    infoLogger.info("--------Webservice input------");
		    infoLogger.info("getSootMonitorReport vin: " + vin + " date: "+date + "machineGroupIdList" + machineGroupIdList);
		    CommonUtil util = new CommonUtil();
		    String isValidinput1 = util.inputFieldValidation(String.valueOf(vin));
		    if(!isValidinput1.equals("SUCCESS")){
			fLogger.info("Invalid input parameter: "+vin);
			throw new CustomFault(isValidinput1);
		    }
		    String isValidinput2 = util.inputFieldValidation(String.valueOf(date));
		    if(!isValidinput2.equals("SUCCESS")){
			fLogger.info("Invalid input parameter: "+date);
			throw new CustomFault(isValidinput1);
		    }
		    //CR471.sn
		    if (machineGroupIdList !=null) {
			String isValidinput3 = util.inputFieldValidation(String.valueOf(machineGroupIdList));
			if(!isValidinput3.equals("SUCCESS")){
			    fLogger.info("Invalid input parameter: "+machineGroupIdList);
			    throw new CustomFault(isValidinput1);
			}
		    }//CR471.en

		    //List<HashMap<String, String>> response = new SootMonitorServiceImpl().getSootMonitorReport(vin,date);//CR471.o
		    List<HashMap<String, String>> response = new SootMonitorServiceImpl().getSootMonitorReport(vin,date,machineGroupIdList);//CR471.n
		    if (response.isEmpty()) {
			jsonResponse=  "FAILURE";
		    }else {
			jsonResponse=  new ObjectMapper().writeValueAsString(response);
		    }
		}catch (CustomFault e) {
		    jsonResponse = "FAILURE";
		    fLogger.fatal("FAILURE : Invalid Input" + e.getFaultInfo());
		}
		
		catch (Exception e) {
		    jsonResponse = "FAILURE";
		    e.printStackTrace();
		}
		return jsonResponse;
	}
}
