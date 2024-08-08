package remote.wise.service.webservice;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.MappingLLFlagForVINRestServiceImpl;

@Path("/MappingLLFlagForVINRestService")
public class MappingLLFlagForVINRestService {
	@POST
	@Path("/MappingLLFlagForVIN")	
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateLLFlagForMachine(final @JsonProperty("reqObj") LinkedHashMap<String, Object> reqObj)
			throws JsonGenerationException, JsonMappingException, IOException,
			CustomFault {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = "SUCCESS";
		String serialNumber = null;
		String LLFlag = null;
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate: " + startDate);
		long startTime = System.currentTimeMillis();
		if (reqObj.get("serialNumber") != null ) {
			serialNumber = (String)reqObj.get("serialNumber");
					}
		if (reqObj.get("LLFlag") != null) {
			LLFlag = (String)reqObj.get("LLFlag");			
		}
		try {
			infoLogger.info("Webservice Input -- MappingLLFlagForVINRestService: updateLLFlagForMachine:  "
					+"serialNumber "+ serialNumber+"LLFlag" +LLFlag );
			MappingLLFlagForVINRestServiceImpl mappingLLFlag = new MappingLLFlagForVINRestServiceImpl();			
			response = mappingLLFlag.updateLLFlagForMachine(serialNumber,LLFlag);
			infoLogger.info("Webservice Output -- MappingLLFlagForVINRestService: updateLLFlagForMachine: Response : "
							+ response);
			long endTime = System.currentTimeMillis();
			infoLogger.info("Webservice Execution Time in ms:"
					+ (endTime - startTime));			
		} catch (Exception e) {
			fLogger.info("Error in calling MappingLLFlagForVINRestService for updateLLFlagForMachine() "
					+ e.getMessage());
			return "FAILURE";
		}
		return response;

	}
	/*public static void main(String[] args) throws CustomFault  {
		MappingLLFlagForVINRestServiceImpl mappingLLflagImpl = new MappingLLFlagForVINRestServiceImpl();	
		  Logger iLogger = InfoLoggerClass.logger;
		  MappingLLFlagForVINRestServiceReqContract request = new MappingLLFlagForVINRestServiceReqContract();
		  request.setSerialNumber("HAR135WSC00010016");
		  request.setFlag(0);
		  String result = null;
		  result = mappingLLflagImpl.updateLLFlagForMachine(request);
		  iLogger.info("Webservice Output -- MappingLLFlagForVINRestService: updateLLFlagForMachine: result : "
					+ result);
		  }*/

}
