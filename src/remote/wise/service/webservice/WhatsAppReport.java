package remote.wise.service.webservice;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.WhatsappReportPOJO;
import remote.wise.service.implementation.GeofenceTimefenceReportImpl;
import remote.wise.util.CommonUtil;

@Path("/WhatsAppReport")
public class WhatsAppReport 
{
	@POST
	@Path("getWhatsAppReportDetails")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({"application/json"})
	public List<WhatsappReportPOJO> getWhatsAppReportDetails(LinkedHashMap<String, Object> requestObj) throws CustomFault 
	{
		List<WhatsappReportPOJO> response = new LinkedList<WhatsappReportPOJO>();
		Logger iLogger = InfoLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		String LoginID =null;
		if(requestObj.get("LoginID")!=null){
			LoginID=requestObj.get("LoginID").toString();
		}
		
		iLogger.info("WhatsAppReport:getWhatsAppReportDetails:WebServiceInput:LoginID:"+LoginID+"; AccountCode:"+requestObj.get("AccountCode")+"" +
				";Region:"+requestObj.get("Region")+"; ZonalCode:"+requestObj.get("ZonalCode")+"; DealerCode:"+requestObj.get("DealerCode")+";" +
						"CustomerCode:"+requestObj.get("CustomerCode")+"; ProfileCode:"+requestObj.get("ProfileCode")+"; ModelCode:"+requestObj.get("ModelCode"));
		String accountCode=null, Source=null,Region=null,ZonalCode=null,DealerCode=null,CustomerCode=null,ProfileCode=null,ModelCode=null;
		
		if(requestObj.get("AccountCode")!=null){
			accountCode=requestObj.get("AccountCode").toString();
		}
		if(requestObj.get("Source")!=null){
			Source=requestObj.get("Source").toString();
		}
		
		if(requestObj.get("Region")!=null){
			Region=requestObj.get("Region").toString();
		}
		
		if(requestObj.get("ZonalCode")!=null){
			ZonalCode=requestObj.get("ZonalCode").toString();
		}
		
		if(requestObj.get("DealerCode")!=null){
			DealerCode=requestObj.get("DealerCode").toString();
		}
		
		if(requestObj.get("CustomerCode")!=null){
			CustomerCode=requestObj.get("CustomerCode").toString();
		}
		if(requestObj.get("ProfileCode")!=null){
			ProfileCode=requestObj.get("ProfileCode").toString();
		}
		if(requestObj.get("ModelCode")!=null){
			ModelCode=requestObj.get("ModelCode").toString();
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
			}

			if(csrfToken != null)
			{
				isValidCSRF = util.validateANTICSRFTOKEN(LoginID, csrfToken);
			}
			if (!isValidCSRF) 
			{
				iLogger.info("WhatsAppReport:getWhatsAppReportDetails:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
				throw new CustomFault("Invalid request.");
			}
			/*else
			{
				util.deleteANTICSRFTOKENS(LoginID, csrfToken, "one");
			}*/
			
			//Get decoded login ID
			LoginID=new CommonUtil().getUserId(LoginID);
			
			//--------------------------------------- XSS validation of input for Security Fixes
			String isValidinput = null;
			
			if(accountCode!=null)
			{
				isValidinput = util.inputFieldValidation(accountCode);
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
			if(Region!=null)
			{
				isValidinput = util.inputFieldValidation(Region);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(ZonalCode!=null)
			{
				isValidinput = util.inputFieldValidation(ZonalCode);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(DealerCode!=null)
			{
				isValidinput = util.inputFieldValidation(DealerCode);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(CustomerCode!=null)
			{
				isValidinput = util.inputFieldValidation(CustomerCode);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(ProfileCode!=null)
			{
				isValidinput = util.inputFieldValidation(ProfileCode);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(ModelCode!=null)
			{
				isValidinput = util.inputFieldValidation(ModelCode);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		/*WhatsappReportPOJO responseObj = new WhatsappReportPOJO();
		responseObj.setAssetID("HAR135WSC00010016");
		responseObj.setCustomerCode("106302901");
		responseObj.setMobileNumber("9905678911");
		responseObj.setNumberOfNotificationsPerDay(3);
		
		
		WhatsappReportPOJO responseObj1 = new WhatsappReportPOJO();
		responseObj1.setAssetID("HAR135WSC00010015");
		responseObj1.setCustomerCode("106302902");
		responseObj1.setMobileNumber("9905678912");
		responseObj1.setNumberOfNotificationsPerDay(5);
		
		response.add(responseObj);
		response.add(responseObj1);*/
		
		response = new GeofenceTimefenceReportImpl().getWhatsAppNotificationList(LoginID, Region, ZonalCode, DealerCode, CustomerCode, ProfileCode, ModelCode);
		
		long endTime = System.currentTimeMillis();
		iLogger.info("WhatsAppReport:getWhatsAppReportDetails:LoginID:"+LoginID+
				"; Webservice execution time in ms:"+(endTime-startTime));
		
		return response;
		
	}
}
