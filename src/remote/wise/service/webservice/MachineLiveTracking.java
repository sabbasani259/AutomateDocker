package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.MachineLiveTrackingImpl;
import remote.wise.util.CommonUtil;

@Path("/MachineLiveTracking")
public class MachineLiveTracking 
{
	@GET
	@Path("startLiveTracking")
	@Produces(MediaType.TEXT_PLAIN)
	public String startLiveTracking(@QueryParam("LoginID") String LoginID, @QueryParam("AccountCode") String AccountCode,
			@QueryParam("AssetID") String AssetID, @QueryParam("Source") String Source) throws CustomFault 
	{
		String status="SUCCESS";
		
		Logger iLogger = InfoLoggerClass.logger;		
		long startTime = System.currentTimeMillis();
			
		iLogger.info("MachineLiveTracking:startLiveTracking:LoginID:"+LoginID+"; AccountCode:"+AccountCode+";" +
					"AssetID:"+AssetID+"; Source:"+Source);
			
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
			}

			if(csrfToken != null)
			{
				isValidCSRF = util.validateANTICSRFTOKEN(LoginID, csrfToken);
			}
			if (!isValidCSRF) 
			{
				iLogger.info("MachineLiveTracking:startLiveTracking:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
				throw new CustomFault("Invalid request.");
			}
			/*else
			{
				util.deleteANTICSRFTOKENS(LoginID, csrfToken, "one");
			}*/
			
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
				isValidinput = util.inputFieldValidation(AssetID.toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(Source!=null)
			{
				isValidinput = util.inputFieldValidation(Source);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			//Get decoded login ID
			LoginID=new CommonUtil().getUserId(LoginID);
		}
		
		status = new MachineLiveTrackingImpl().startLiveTracking(LoginID, AccountCode, AssetID, Source);
		
		long endTime = System.currentTimeMillis();
		iLogger.info("MachineLiveTracking:startLiveTracking:LoginID:"+LoginID+";Source:" +Source+
					"; WebService output:"+status+"; Webservice execution time in ms:"+(endTime-startTime));
		
		
		return status;
	}
}
