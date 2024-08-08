package remote.wise.service.webservice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.VinCHMRImpl;
import remote.wise.util.ListToStringConversion;

@Path("/VinCHMRService")
public class VinCHMRService {


	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("getCHMRForVin")
	public String getCHMRForVin(List<String> vinList) throws CustomFault {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = "", tempSessionID = null;
		String vins = null; 
		HashMap<String, String> vinSessionIDMap = new HashMap<String, String>();
		HashMap<String, String> finalVINHMRMap = new HashMap<String, String>();
		List<String> tempvinList = new LinkedList<String>();
		try {
			infoLogger.info("VinCHMRService input : " + vinList.toString());
			for (String vin : vinList) {
				tempSessionID = vin;
				if (vin.length() > 17) {
					vin = vin.substring(0,17);
				}
				tempvinList.add(vin);
				vinSessionIDMap.put(vin, tempSessionID);
			}
			infoLogger.info("VinCHMRService input Vinlist : " + "vin list : " + tempvinList.toString());
			if(vinList != null && !vinList.isEmpty()) {
				ListToStringConversion conversion = new ListToStringConversion();
				vins = conversion.getStringList(tempvinList).toString();
			}
			VinCHMRImpl implObj = new VinCHMRImpl();
			HashMap<String, String> vinHMRMap = implObj.getCHMRForVin(vins);
			 tempSessionID = null;
			if(vinHMRMap != null && vinHMRMap.keySet().size()>0){
				for(String vin : vinHMRMap.keySet()){
					tempSessionID = vinSessionIDMap.get(vin);
					finalVINHMRMap.put(tempSessionID, vinHMRMap.get(vin));
				}
			}
			
			response = new ObjectMapper().writeValueAsString(finalVINHMRMap);
			infoLogger.info("VinCHMRService Output: " + response);
		} catch (Exception e) {
			fLogger.fatal("Exception:" + e.getMessage());
			response = "FAILURE";
		}
		return response;
	}

}
