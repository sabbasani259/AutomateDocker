package remote.wise.service.webservice;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.UserDetailsReqContract;
import remote.wise.service.datacontract.UserDetailsRespContract;
import remote.wise.service.implementation.UserDetailsImpl;
import remote.wise.util.CommonUtil;

@Path("/UserDetailsRESTService")
public class UserDetailsRESTService 
{
	@GET
	@Path("getUserDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserDetailsRespContract> getUserDetails(@Context HttpHeaders httpHeaders, @QueryParam("loginId") String loginId, @QueryParam("contactId") String contactId) throws CustomFault
	{
		List<UserDetailsRespContract> response = new LinkedList<UserDetailsRespContract>();
		Logger iLogger = InfoLoggerClass.logger;
		CommonUtil utilObj = new CommonUtil();
		String csrfToken = null;
		
		iLogger.info("UserDetailsRESTService:WebService Input-----> loginId:"+loginId+"; contact id:"+contactId);
		
		long startTime = System.currentTimeMillis();
		
		if(httpHeaders.getRequestHeader("CSRFTOKEN")!=null)
		{
			csrfToken=httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
		}
		iLogger.info("UserDetailsRESTService :: getUserDetails ::  received csrftoken :: "+csrfToken);
		boolean isValidCSRF=false;
		if(csrfToken!=null){
			isValidCSRF=utilObj.validateANTICSRFTOKEN(loginId,csrfToken);
		}
		iLogger.info("UserDetailsRESTService :: getUserDetails ::   csrftoken isValidCSRF :: "+isValidCSRF);
		if(!isValidCSRF)
		{
			iLogger.info("UserDetailsRESTService :: getUserDetails ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}
		
		String isValidinput = utilObj.inputFieldValidation(contactId);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		UserDetailsReqContract inputObj = new UserDetailsReqContract();
		inputObj.setUserId(contactId);
		response = new UserDetailsImpl().getUserDetails(inputObj);
		
		long endTime=System.currentTimeMillis();
		iLogger.info("UserDetailsRESTService :: getUserDetails ::WebServiceOutput:"+response);
		iLogger.info("serviceName:UserDetailsService~executionTime:"+(endTime-startTime)+"~"+loginId+"~");
		
		return response;
	}	
}