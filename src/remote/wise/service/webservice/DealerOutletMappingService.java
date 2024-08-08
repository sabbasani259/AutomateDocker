package remote.wise.service.webservice;

import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.DealerOutletMappingImpl;
import remote.wise.util.CommonUtil;
/*
 * Author : Mani
 * input :rolename,tenancyid,loginid
 * based on the the tenancy hierarchy[customer or Dealer],the service gives the dealer outlet mapping data
 */

@Path("/DealerOutletMappingService")
public class DealerOutletMappingService {
	public static String csrfToken;
	@POST
	@Path("getDealerOutletMapping")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({MediaType.APPLICATION_JSON})
	public String getmappingdetails(@Context HttpHeaders httpHeaders,final @JsonProperty("reqObj")LinkedHashMap<String,Object> reqObj) throws CustomFault{
		//20181116 ::MA369757 :: Service to get dealer outlets
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		String result = null;
		DealerOutletMappingImpl impl=new DealerOutletMappingImpl();
		String userId="";
		List<Integer> tenancyIdList=null;
		if(reqObj.get("tenancyIdList")!=null)
			tenancyIdList=(List<Integer>) reqObj.get("tenancyIdList");
		String roleName=null;
		if(reqObj.get("roleName")!=null)
			roleName=(String) reqObj.get("roleName");
		String loginId=null;
		if(reqObj.get("loginId")!=null)
			loginId=(String) reqObj.get("loginId");
		if(httpHeaders.getRequestHeader("CSRFTOKEN")!=null)
		{
			csrfToken=httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
		}
		infoLogger.info("DealerOutletMappingService :: received csrftoken :: "+csrfToken);
		boolean isValidCSRF=false;
		if(csrfToken!=null){
			isValidCSRF=new CommonUtil().validateANTICSRFTOKEN(loginId,csrfToken);
		}
		infoLogger.info("DealerOutletMappingService ::csrftoken isValidCSRF :: "+isValidCSRF);
		if(!isValidCSRF)
		{
			infoLogger.info("DealerOutletMappingService ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}
		try{
			infoLogger.info("DealerOutletMappingService :: input :: roleName "+roleName+" tenancyIdList "+tenancyIdList+" loginId "+loginId);
			if(!(roleName==null||roleName.isEmpty()||tenancyIdList.size()==0||loginId==null||loginId.isEmpty()))
			{
				userId=new CommonUtil().getUserId(loginId);	
				if(userId==null||userId.equalsIgnoreCase("FAILURE"))
				{
					infoLogger.info("DealerOutletMappingService : Invalid request for loginId ::"+loginId);
					throw new CustomFault("Invalid request.");
				}
				infoLogger.info("DealerOutletMappingService : Decrypted login id :"+userId+" for received login id : "+loginId);
				CommonUtil util = new CommonUtil();
				String isValidinput=null;
				if(roleName!=null){
					isValidinput = util.inputFieldValidation(roleName);
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
					}
				result=impl.getmappingdata(roleName,tenancyIdList,userId);
			}
			else
			{
				throw new CustomFault("Invalid/Null inputs received :roleName :"+roleName+" tenancyIdList :"+tenancyIdList+" loginId :"+loginId);
			}
			long endTime = System.currentTimeMillis();
			infoLogger.info("serviceName:DealerOutletMappingService~executionTime:"+(endTime-startTime)+"~"+loginId+"~");
			infoLogger.info("Webservice output :: DealerOutletMappingService :"+result);
		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("DealerOutletMappingService :: Exception in webservice :: "+e.getMessage(),e);
		}
		return result;
	}

}
