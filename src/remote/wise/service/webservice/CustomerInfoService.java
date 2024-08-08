package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;


import remote.wise.EAintegration.handler.CustomerInfo;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

/**
 * @author AJAY
 *
 */

@Path("/customerinfoservice")
public class CustomerInfoService {
	@POST
	@Path("customerinfo")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({MediaType.APPLICATION_JSON})
	public String createOrUpdateCustomerInfo(@Context HttpHeaders httpHeaders, LinkedHashMap<String,String> fields){
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		try
		{
			
			String CustomerCode=fields.get("CustomerCode").toString();
			String CustomerInfo=fields.get("CustomerInfo").toString();
			String loginId = fields.get("loginId").toString();
			String result="FAILURE";
			String csrfToken = null;
			CommonUtil util = new CommonUtil();
			
			iLogger.info("CustomerInfoService:WebService Input : CustomerCode:"+CustomerCode+": Customer Data :"+CustomerInfo);
			
			//DF20181011 - KO369761 - Validating the CSRF Token against login id.
			if(httpHeaders.getRequestHeader("CSRFTOKEN")!=null)
			{
				csrfToken=httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
			}
			iLogger.info("CustomerInfoService :: createOrUpdateCustomerInfo ::  received csrftoken :: "+csrfToken);
			boolean isValidCSRF=false;
			if(csrfToken!=null){
				isValidCSRF=util.validateANTICSRFTOKEN(loginId,csrfToken);
			}
			iLogger.info("customerinfoservice :: createOrUpdateCustomerInfo ::   csrftoken isValidCSRF :: "+isValidCSRF);
			if(!isValidCSRF)
			{
				iLogger.info("customerinfoservice :: createOrUpdateCustomerInfo ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			}
			
			//DF20171011: KO369761 - Security Check added for input text fields.
			String isValidinput=null;
			
			isValidinput = util.inputFieldValidation(CustomerCode);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(CustomerInfo);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			long startTime = System.currentTimeMillis();
			if(CustomerInfo !=null && CustomerCode !=null && (!CustomerInfo.isEmpty())  && (!CustomerCode.isEmpty()) && CustomerInfo.split("\\|",-1).length==13){
				result=new CustomerInfo().createOrUpdateCustomerInfo(CustomerCode,CustomerInfo,1);
			} 
			
			long endTime = System.currentTimeMillis();
			iLogger.info("serviceName:CustomerInfoService~executionTime:"+(endTime-startTime)+"~"+""+"~");
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("CustomerInfoService:WebService:"+fields.get("CustomerCode")+" Exception Caught :"+e.getMessage());
			return "FAILURE";
		}

	}
}