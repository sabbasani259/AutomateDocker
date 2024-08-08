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
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.GeofenceReportPOJO;
import remote.wise.pojo.TimefenceReportPOJO;
import remote.wise.service.implementation.GeofenceTimefenceReportImpl;
import remote.wise.util.CommonUtil;

@Path("/TimefenceReport")
public class TimefenceReport 
{

	@POST
	@Path("getTimeFenceReportDetails")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({"application/json"})
	public List<TimefenceReportPOJO> getTimeFenceReportDetails(LinkedHashMap<String, Object> requestObj) throws CustomFault 
	{
		List<TimefenceReportPOJO> response = new LinkedList<TimefenceReportPOJO>();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		String LoginID =null;
		if(requestObj.get("LoginID")!=null){
			LoginID=requestObj.get("LoginID").toString();
		}
		
		iLogger.info("TimefenceReport:getTimeFenceReportDetails:WebServiceInput:LoginID:"+LoginID+"; AccountCode:"+requestObj.get("AccountCode")+"" +
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
				iLogger.info("TimefenceReport:getTimeFenceReportDetails:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
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
	/*	TimefenceReportPOJO responseObj = new TimefenceReportPOJO();
		responseObj.setAssetID("HAR135WSC00010016");
		responseObj.setCustomerCode("106302901");
		responseObj.setEndDate("2021-11-30");
		responseObj.setMobileNumber("9905678911");
		responseObj.setNotificationDate("2021-11-30");
		responseObj.setNotificationPattern("OneTime");
		responseObj.setOperatingEndTime("18:30:00");
		responseObj.setOperatingStartTime("09:00:00");
		responseObj.setRecurrencePattern(null);
		responseObj.setStartDate("2021-11-30");
		
		
		TimefenceReportPOJO responseObj1 = new TimefenceReportPOJO();
		responseObj1.setAssetID("HAR135WSC00010015");
		responseObj1.setCustomerCode("106302902");
		responseObj1.setEndDate("2021-12-31");
		responseObj1.setMobileNumber("9905678912");
		responseObj1.setNotificationDate(null);
		responseObj1.setNotificationPattern("Recurrence");
		responseObj1.setOperatingEndTime("15:30:00");
		responseObj1.setOperatingStartTime("07:00:00");
		responseObj1.setRecurrencePattern("{\"Weekly\":\"Saturday,Monday,Wednesday\"}");
		responseObj1.setStartDate("2021-11-30");
		
		response.add(responseObj);
		response.add(responseObj1);*/
		response = new GeofenceTimefenceReportImpl().getTimefenceList(LoginID, Region, ZonalCode, DealerCode, CustomerCode, ProfileCode, ModelCode);
		
		long endTime = System.currentTimeMillis();
		iLogger.info("TimefenceReport:getTimeFenceReportDetails:LoginID:"+LoginID+
				"; Webservice execution time in ms:"+(endTime-startTime));
		
		return response;
		
	}
	
	

}
