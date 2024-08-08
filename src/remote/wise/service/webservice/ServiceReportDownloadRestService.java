package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ServiceReportDownloadRestServiceImpl;
import remote.wise.util.CommonUtil;

@Path("/ServiceReportDownloadRestService")
public class ServiceReportDownloadRestService {
	
	@POST
	@Path("getServiceDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public List<HashMap<String,Object>> getServiceDetails(final @JsonProperty("reqObj")LinkedHashMap<String,Object> reqObj) throws CustomFault
	{
		List<HashMap<String,Object>> result= null;
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		String loginTenancyId=null;
		if(reqObj.get("loginTenancyId")!=null)
			loginTenancyId=(String) reqObj.get("loginTenancyId");
		List<Integer> customGroupIdList=null;
		if(reqObj.get("machineGroupIdList")!=null)
			customGroupIdList=(List<Integer>) reqObj.get("machineGroupIdList");
		CommonUtil utilObj = new CommonUtil();
		String isValidInput = utilObj.inputFieldValidation(loginTenancyId);
		if(!isValidInput.equalsIgnoreCase("SUCCESS")){
			throw new CustomFault(isValidInput);
		}

		
		result = new ServiceReportDownloadRestServiceImpl().getServiceCloserDetails(loginTenancyId,customGroupIdList);
		long endTime=System.currentTimeMillis();
		iLogger.info("ServiceClouserRESTService:getServiceDetails:WebService Output -----> "+result+"; Total Time taken in ms:"+(endTime - startTime));
		return result;
	}

}
