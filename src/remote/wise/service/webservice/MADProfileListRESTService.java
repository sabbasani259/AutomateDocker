package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.MADMAchineCountImpl;
import remote.wise.service.implementation.MADProfileListImpl;

import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
import remote.wise.util.ListToStringConversion;

@Path("/MADProfileListRESTService")
public class MADProfileListRESTService {

	@POST
	@Path("getProfileCount")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({MediaType.APPLICATION_JSON})
	public HashMap<String,String> getMachineCount(@Context HttpHeaders httpHeaders,LinkedHashMap<String,Object> reqObj ) throws CustomFault
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		HashMap<String,String> response = new HashMap<String,String>();
		String csrfToken = null;
		boolean isValidCSRF=false;
		CommonUtil util = new CommonUtil();
		
		try{
			long startTime = System.currentTimeMillis();
			iLogger.info("Webservice input :"+reqObj);
			
			if(httpHeaders.getRequestHeader("CSRFTOKEN")!=null)
			{
				csrfToken=httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
			}
			iLogger.info("MADProfileListRESTService:getProfileCount ::  received csrftoken :: "+csrfToken);
			if(csrfToken!=null){
				isValidCSRF=util.validateANTICSRFTOKEN((String)reqObj.get("loginId"),csrfToken);
			}
			iLogger.info("MADProfileListRESTService:getProfileCount ::   csrftoken isValidCSRF :: "+isValidCSRF);
			if(!isValidCSRF)
			{
				iLogger.info("MADProfileListRESTService:getProfileCount ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			}
			String isValidinput=null;
			ListToStringConversion convert=new ListToStringConversion();
			String filterListString="";		
			String tenacnyListString="";
			String userId="";
			for(int i=0;i<reqObj.size();i++)
			{
				if(reqObj.get("tenancyIdList")!=null){
					List<Integer> tenacnyList=(List<Integer>) reqObj.get("tenancyIdList");
					tenacnyListString=convert.getIntegerListString(tenacnyList).toString();
					isValidinput = util.inputFieldValidation(tenacnyListString);
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}				
				
				if(reqObj.get("loginId")!=null){
					userId=new CommonUtil().getUserId((String)reqObj.get("loginId"));
					isValidinput = util.inputFieldValidation(userId.toString());
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}

			}
			
		
		List tenancyIdList = (List) reqObj.get("tenancyIdList");
		String accountIdStringList=new DateUtil().getAccountListForTheTenancy(tenancyIdList);
		
		response = new MADProfileListImpl().getMachineCount(accountIdStringList,userId);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MADProfileListRESTService~executionTime:"+(endTime-startTime)+"~"+userId+"~");
		
	}
	catch(Exception e){
		e.printStackTrace();
		fLogger.error("Exception:"+e.getMessage());
	}
	return response;	
	}
}
