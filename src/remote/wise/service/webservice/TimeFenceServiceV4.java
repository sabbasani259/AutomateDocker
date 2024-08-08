/*
 *  20230720 : CR432 : Prasanna Lakshmi : GeoFence/TimeFence API Changes 
 */
package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.TimeFenceDetailsPOJONew;
import remote.wise.service.implementation.TimefenceDetailsImplV4;
import remote.wise.util.CommonUtil;
@Path("/TimeFenceServiceV4")
public class TimeFenceServiceV4 {
	@GET
	@Path("getTimeFenceDetailsNew")
	@Produces({"application/json"})
	public TimeFenceDetailsPOJONew getTimeFenceDetailsNew(@QueryParam("LoginID") String LoginID, 
			@QueryParam("AccountCode") String AccountCode,@QueryParam("AssetID") String AssetID, @QueryParam("Source") String Source) throws CustomFault
	{
		TimeFenceDetailsPOJONew response = new TimeFenceDetailsPOJONew();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		iLogger.info("TimeFenceServiceV4:getTimeFenceDetailsNew:WebService Input:LoginID:"+LoginID+"; AccountCode:"+AccountCode+";" +
				"AssetID:"+AssetID);
		
		
		if(AssetID==null || AssetID.trim().length()==0)
		{
			fLogger.fatal("TimeFenceServiceV4:getTimeFenceDetailsNew:Mandatory parameter AssetID is NULL. Hence returning empty response");
			return response;
		}
		
		if(Source==null) 
			Source="WebApp"; //WebApp by default. However, if invoked from API, this parameter will be sent as MobileApp
		
		//****************************************** Security Checks for the call from WebApp***************************
		if(Source.equalsIgnoreCase("WebApp"))
		{
			CommonUtil util = new CommonUtil();
			String csrfToken = null;
			boolean isValidCSRF = false;
			
			if (LoginID != null) 
			{
				if (LoginID.split("\\|").length > 1) 
				{
					csrfToken = LoginID.split("\\|")[1];
					LoginID = LoginID.split("\\|")[0];
				}
				if(csrfToken != null)
				{
					isValidCSRF = util.validateANTICSRFTOKEN(LoginID, csrfToken);
				}
				if (!isValidCSRF) 
				{
					iLogger.info("TimeFenceServicev4:getTimeFenceDetailsNew:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
					throw new CustomFault("Invalid request.");
				}
				/*else
				{
					util.deleteANTICSRFTOKENS(LoginID, csrfToken, "one");
				}*/
				
				//Get decoded login ID
				LoginID=new CommonUtil().getUserId(LoginID);
			}

			
			
			//--------------------------------------- XSS validation of input for Security Fixes
			String isValidinput = null;
			
			if(AccountCode!=null)
			{
				isValidinput = util.inputFieldValidation(AccountCode);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(AssetID!=null)
			{
				isValidinput = util.inputFieldValidation(AssetID);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		
		response = new TimefenceDetailsImplV4().getTimefenceDetailsNew(LoginID, AccountCode, AssetID);
		
		//---------------------------XSS validation of output response contract
		if(Source.equalsIgnoreCase("WebApp"))
		{
			String isValidinput = null;
			CommonUtil util = new CommonUtil();
			
			if(response.getVIN()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getVIN());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(response.getCreatedDate()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getCreatedDate());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getCustomerCode()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getCustomerCode());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getOperatingStartTime()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getOperatingStartTime());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getOperatingEndtime()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getOperatingEndtime());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getMobileNumber()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getMobileNumber());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
		}
		
		long endTime = System.currentTimeMillis();
		iLogger.info("TimeFenceServicev4:getTimeFenceDetailsNew:LoginID:"+LoginID+"; AssetID:"+AssetID+";" +
				" WebService output:"+response+"; Webservice execution time in ms:"+(endTime-startTime));
		
		if(response!=null && response.getVIN()==null)
			response=null;
		
		return response;
	}

}
