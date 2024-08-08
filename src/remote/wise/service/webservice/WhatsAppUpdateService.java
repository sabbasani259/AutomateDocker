package remote.wise.service.webservice;


import java.util.LinkedHashMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.WhatsAppPrefPOJO;
import remote.wise.service.implementation.GeoFenceDetailsImpl;
import remote.wise.service.implementation.WhatsAppUpdateDetailsImpl;
import remote.wise.service.implementation.WhatsAppUpdateInterimThread;
import remote.wise.util.CommonUtil;

@Path("/WhatsAppUpdateService")
public class WhatsAppUpdateService {
	
	@POST
	@Path("setWhatsAppPreference")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({ MediaType.APPLICATION_JSON })
	public String setWhatsAppPreference(LinkedHashMap<String, Object> requestObj) throws CustomFault 
	{
        Logger iLogger = InfoLoggerClass.logger;		
		String status = "SUCCESS";
		
        long startTime = System.currentTimeMillis();
		
		String LoginID =null, Source="WebApp";
		if(requestObj.get("LoginID")!=null){
			LoginID=requestObj.get("LoginID").toString();
		}
		
		iLogger.info("WhatsAppUpdateService:setWhatsAppPreference:WebService Input:LoginID:"+LoginID+"; AccountCode:"+requestObj.get("AccountCode")+";" +
				"VIN:"+requestObj.get("VIN")+"; MobileNumber:"+requestObj.get("MobileNumber")+"; " +
						"NotificationTimeSlot:"+requestObj.get("NotificationTimeSlot")+
				"; MachineDetails:"+requestObj.get("MachineDetails"));
		
		String accountCode=null, assetID=null, MobileNumber=null, NotificationTimeSlot=null,MachineDetails =null; 
		if(requestObj.get("AccountCode")!=null){
			accountCode=requestObj.get("AccountCode").toString();
		}
		if(requestObj.get("VIN")!=null){
			assetID=requestObj.get("VIN").toString();
		}
		if(requestObj.get("MobileNumber")!=null){
			MobileNumber=requestObj.get("MobileNumber").toString();
		}
		if(requestObj.get("NotificationTimeSlot")!=null)
		{
			NotificationTimeSlot=requestObj.get("NotificationTimeSlot").toString();
		}
		
		if(requestObj.get("MachineDetails")!=null)
		{
			MachineDetails=requestObj.get("MachineDetails").toString();			
		}	
		
		if(requestObj.get("Source")!=null)
			Source=requestObj.get("Source").toString();
		
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
						iLogger.info("WhatsAppUpdateService:setWhatsAppPreference:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
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
					if(assetID!=null)
					{
						isValidinput = util.inputFieldValidation(assetID);
						if(!isValidinput.equals("SUCCESS")){
							throw new CustomFault(isValidinput);
						}
					}
					if(MobileNumber!=null)
					{
						isValidinput = util.inputFieldValidation(MobileNumber);
						if(!isValidinput.equals("SUCCESS")){
							throw new CustomFault(isValidinput);
						}
					}
				}
				
		status = new WhatsAppUpdateDetailsImpl().setWhatsAppPreference(LoginID,accountCode,assetID,MobileNumber,NotificationTimeSlot,MachineDetails);
		long endTime = System.currentTimeMillis();
		iLogger.info("WhatsAppUpdateService:setWhatsAppPreference:LoginID:"+LoginID+"; VIN:"+requestObj.get("VIN")+";" +
					"WebService output:"+status+"; Webservice execution time in ms:"+(endTime-startTime));
		

		return status;
		
	}
	
	

	@GET
	@Path("getWhatsAppPreference")
	@Produces({"application/json"})
	public WhatsAppPrefPOJO getWhatsAppPreference(@QueryParam("LoginID") String LoginID, 
			@QueryParam("VIN") String VIN, @QueryParam("Source") String Source) throws CustomFault 
	{
		WhatsAppPrefPOJO response = new WhatsAppPrefPOJO();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		iLogger.info("WhatsAppUpdateService:getWhatsAppPreference:WebServiceInput:LoginID:"+LoginID+"; VIN:"+VIN+";Source:"+Source);
		
		if(VIN==null || VIN.trim().length()==0)
		{
			fLogger.fatal("WhatsAppUpdateService:getWhatsAppPreference:Mandatory parameter VIN is NULL. Hence returning empty response");
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
			}

			if(csrfToken != null)
			{
				isValidCSRF = util.validateANTICSRFTOKEN(LoginID, csrfToken);
			}
			if (!isValidCSRF) 
			{
				iLogger.info("WhatsAppUpdateService:getWhatsAppPreference:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
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
			
			if(VIN!=null)
			{
				isValidinput = util.inputFieldValidation(VIN.toString());
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
		}
		
		
		response = new WhatsAppUpdateDetailsImpl().getWhatsAppPreference(LoginID, VIN);
		
		//---------------------------XSS validation of output response contract
		if(Source.equalsIgnoreCase("WebApp"))
		{
			String isValidinput = null;
			CommonUtil util = new CommonUtil();
			
			if(response.getAssetID()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getAssetID());
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
			if(response.getMachineDetails()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getMachineDetails());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
		}
		
		long endTime = System.currentTimeMillis();
		iLogger.info("WhatsAppUpdateService:getWhatsAppPreference:LoginID:"+LoginID+"; VIN:"+VIN+";" +
				" WebService output:"+response+"; Webservice execution time in ms:"+(endTime-startTime));
		
		return response;
		
	}
	//***********************************************************************************************************************************************
	
	@GET
	@Path("sendWhatsAppNotification")
	@Produces(MediaType.TEXT_PLAIN)
	public String sendWhatsAppNotification(@QueryParam("NotificationTimeSlot") String NotificationTimeSlot) 	
	{
		 Logger iLogger = InfoLoggerClass.logger;		
		 String status = "SUCCESS";
			
	     long startTime = System.currentTimeMillis();
	     
	     iLogger.info("WhatsAppUpdateService:sendWhatsAppNotification:WebService Input:NotificationTimeSlot:"+NotificationTimeSlot);
	     
	    // status = new WhatsAppUpdateDetailsImpl().sendWhatsAppNotification(NotificationTimeSlot);
	     new WhatsAppUpdateInterimThread(NotificationTimeSlot);
		 long endTime = System.currentTimeMillis();
		 iLogger.info("WhatsAppUpdateService:sendWhatsAppNotification:NotificationTimeSlot:"+NotificationTimeSlot+";" +
		 		"WebService output:"+status+"; Webservice execution time in ms:"+(endTime-startTime));
			
		return status;
	}
}
