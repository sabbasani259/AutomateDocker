package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.SessionMapImpl;

@Path("/SessionMapDetails")
public class SessionMapService {
	 Logger iLogger = InfoLoggerClass.logger;
	 
	@GET
	@Path("/GetSessionMap")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String,String> getSessionMapDetails(@QueryParam("clientId") String clientId) throws CustomFault {
		HashMap<String,String> response = null;
		System.out.println("Account code passed is:"+clientId);
		response = new SessionMapImpl().getSessionMap(clientId);
		if(response == null){
			response.put("status", "FAILURE");
		}else{
		response.put("status", "SUCCESS");
		}
		return response;
	}
	
	@POST
	@Path("/SetSessionMap")
	@Consumes(MediaType.APPLICATION_JSON)
	public String setSessionMapDetails(@JsonProperty("sessionMap") Map<String, Object> sessionMap)throws CustomFault {
		
		iLogger.info("clientId :"+sessionMap.get("clientId")+"userCountry :"+sessionMap.get("userCountry")+"tenencyIdList :"+sessionMap.get("tenencyIdList")
				+"isTenancyAdmin :"+sessionMap.get("isTenancyAdmin")+"roleId :"+sessionMap.get("roleId")+"roleName :"+sessionMap.get("roleName")
				+("deCodedUserId :"+sessionMap.get("deCodedUserId")+"loggedInUserTenancyIdList :"+sessionMap.get("loggedInUserTenancyIdList")
						+"loggedInUserTenancyName :"+sessionMap.get("loggedInUserTenancyName")+"isMapService :"+sessionMap.get("isMapService")
						+"isSmsService :"+sessionMap.get("isSmsService")+"isPseudoLogin :"+sessionMap.get("isPseudoLogin")+"headerRoleName :"+sessionMap.get("headerRoleName")
						+"country :"+sessionMap.get("country")+"country_tenancy :"+sessionMap.get("country_tenancy"))+"referrer :"+sessionMap.get("referrer"));

		String response = new SessionMapImpl().setSessionMap(sessionMap);
		return response;
	}
}
