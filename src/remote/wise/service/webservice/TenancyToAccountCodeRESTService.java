package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AccountTenancyImpl;

import remote.wise.util.CommonUtil;
import remote.wise.util.ListToStringConversion;

//DF20190816-Abhishek::To fetch account code based on tenancyIdList

@Path("/TenancyToAccountCodeRESTService")
public class TenancyToAccountCodeRESTService {

	@POST
	@Path("getAccountCode")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({MediaType.APPLICATION_JSON})
	public List<String> getAccountCode( HashMap<String,Object> reqObj) throws CustomFault
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<HashMap<String,String>> response = new LinkedList<HashMap<String,String>>();
		String csrfToken = null;
		boolean isValidCSRF=false;
		CommonUtil util = new CommonUtil();
		List<String> result =null;
		try{
			long startTime = System.currentTimeMillis();
			iLogger.info("TenancyToAccountCodeRESTService:getAccountCode:WebService"+reqObj);
			
			/*if(httpHeaders.getRequestHeader("CSRFTOKEN")!=null)
			{
				csrfToken=httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
			}
			iLogger.info("GensetTrendsDataService ::  received csrftoken :: "+csrfToken);
			if(csrfToken!=null){
				isValidCSRF=util.validateANTICSRFTOKEN((String)reqObj.get("loginId"),csrfToken);
			}
			iLogger.info("GensetTrendsDataService ::   csrftoken isValidCSRF :: "+isValidCSRF);
			if(!isValidCSRF)
			{
				iLogger.info("GensetTrendsDataService ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			}*/
			String isValidinput=null;
			ListToStringConversion convert=new ListToStringConversion();
			String filterListString="";		
			String tenacnyListString="";
			String userId="";
			if(reqObj.get("tenancyIdList")!=null){
					List<Integer> tenacnyList=(List<Integer>) reqObj.get("tenancyIdList");
					tenacnyListString=convert.getIntegerListString(tenacnyList).toString();
					isValidinput = util.inputFieldValidation(tenacnyListString);
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
				}
					
				//userId=(String)reqObj.get("loginId");
				/*if(reqObj.get("loginId")!=null){
					userId=new CommonUtil().getUserId((String)reqObj.get("loginId"));
					isValidinput = util.inputFieldValidation(userId.toString());
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}*/

			}
			
		
		result=new AccountTenancyImpl().getAccountCodeFromTenancy(tenacnyListString);
		
		//response = new MADMAchineCountImpl().getMachineCount(accountIdStringList,filterListString,reqObj.get("downloadFlag").toString(),userId);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:TenancyToAccountCodeRESTService~executionTime:"+(endTime-startTime)+"~"+userId+"~");
		
	}
	catch(Exception e){
		e.printStackTrace();
		fLogger.error("Exception:"+e.getMessage());
	}
	return result;	
	}
}

