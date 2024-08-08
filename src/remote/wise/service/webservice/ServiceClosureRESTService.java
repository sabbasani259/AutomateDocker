package remote.wise.service.webservice;

import java.util.HashMap;

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
import remote.wise.service.implementation.ServiceClosureImpl;
import remote.wise.util.CommonUtil;

@Path("/ServiceClosureRESTService")
public class ServiceClosureRESTService {

	@GET
	@Path("getServiceDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, Object> getServiceDetails(@QueryParam("loginId") String loginId, @QueryParam("assetEventId") String assetEventId) throws CustomFault
	{
		HashMap<String, Object> result= null;
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();

		CommonUtil utilObj = new CommonUtil();
		String isValidInput = utilObj.inputFieldValidation(assetEventId);
		if(!isValidInput.equalsIgnoreCase("SUCCESS")){
			throw new CustomFault(isValidInput);
		}

		//DF20170919 @Roopa getting decoded UserId
		String userId = utilObj.getUserId(loginId);
		if(userId == null){
			throw new CustomFault("Invalid login id");
		}
		
		result = new ServiceClosureImpl().getServiceCloserDetails(assetEventId);
		long endTime=System.currentTimeMillis();
		iLogger.info("ServiceClouserRESTService:getServiceDetails:WebService Output -----> "+result);
		iLogger.info("serviceName:ServiceClosureRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+result);
		return result;
	}
	
	@POST
	@Path("setServiceDetails")
	@Produces("text/plain")
	@Consumes({MediaType.APPLICATION_JSON})
	public String setServiceDetails(@JsonProperty HashMap<String, String> inputObj) throws CustomFault
	{
		String result;
		Logger iLogger = InfoLoggerClass.logger;
		long startTime=System.currentTimeMillis();
		CommonUtil utilObj = new CommonUtil();
		String loginId = (String)inputObj.get("loginId");
		iLogger.info("ServiceClouserRESTService:setServiceDetails:WebService Input----->  loginId:"+loginId+"; :"+inputObj);
		
		String isValidInput = utilObj.inputFieldValidation(inputObj.get("assetEventId"));
		if(!isValidInput.equalsIgnoreCase("SUCCESS")){
			throw new CustomFault(isValidInput);
		}
		
		isValidInput = utilObj.inputFieldValidation(inputObj.get("assetEventId"));
		if(!isValidInput.equalsIgnoreCase("SUCCESS")){
			throw new CustomFault(isValidInput);
		}
		
		isValidInput = utilObj.inputFieldValidation(inputObj.get("completedBy"));
		if(!isValidInput.equalsIgnoreCase("SUCCESS")){
			throw new CustomFault(isValidInput);
		}
		
		isValidInput = utilObj.inputFieldValidation(inputObj.get("engineHrs"));
		if(!isValidInput.equalsIgnoreCase("SUCCESS")){
			throw new CustomFault(isValidInput);
		}
		
		isValidInput = utilObj.inputFieldValidation(inputObj.get("comments"));
		if(!isValidInput.equalsIgnoreCase("SUCCESS")){
			throw new CustomFault(isValidInput);
		}
		
		//DF20170919 @Roopa getting decoded UserId
		String userId = utilObj.getUserId(loginId);
		if(userId == null){
			throw new CustomFault("Invalid login id");
		}
		
		result = new ServiceClosureImpl().setServiceCloserDetails(inputObj);
		long endTime=System.currentTimeMillis();
		iLogger.info("ServiceClouserRESTService:setServiceDetails:WebService Output -----> "+result);
		iLogger.info("serviceName:ServiceClosureRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+result);
		return result;
	}
}
