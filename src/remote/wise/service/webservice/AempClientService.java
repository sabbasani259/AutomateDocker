 // CR419 :Santosh : 20230714 :Aemp Changes
package remote.wise.service.webservice;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AempClientImpl;
import remote.wise.util.CommonUtil;

@Path("/AempClientService")
public class AempClientService {

	@POST
	@Path("Create")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String createClient(@Context HttpHeaders httpHeaders, HashMap<String, String> clientDetails) throws CustomFault {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = "FAILURE";
		iLogger.info("AempClientService:createClient:WebService Start ");
		long startTime = System.currentTimeMillis();
		
		try {
			iLogger.info("Webservice input : " + clientDetails);

			CommonUtil util = new CommonUtil();
			String isValidinput = null;

			isValidinput = util.inputFieldValidation(clientDetails.get("clientName"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(clientDetails.get("email"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(clientDetails.get("mobileNo"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(clientDetails.get("accountCode"));
			if (!isValidinput.equals("SUCCESS")) {
				throw new CustomFault(isValidinput);
			}
			AempClientImpl impl = new AempClientImpl();
			response = impl.createClient(httpHeaders, clientDetails);
			
			long endTime=System.currentTimeMillis();
			iLogger.info("AempClientService:createClient:WebService End:status:"+response+":ExecutionTime:"+(endTime-startTime)+"~"+""+"~");
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception caught : " + e.getMessage());
		}
		return response;
	}
}
