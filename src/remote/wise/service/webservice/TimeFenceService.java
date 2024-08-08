package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import com.google.gson.Gson;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.TimeFenceDetailsPOJO;
import remote.wise.pojo.TimeFenceDetailsPOJONew;
import remote.wise.service.implementation.TimefenceDetailsImpl;
import remote.wise.util.CommonUtil;

@Path("/TimeFenceService")
public class TimeFenceService 
{
	@POST
	@Path("setTimeFenceDetails")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({ MediaType.APPLICATION_JSON })
	public String setTimeFenceDetails(LinkedHashMap<String, Object> requestObj) throws CustomFault 
	{
	
		Logger iLogger = InfoLoggerClass.logger;
		
		String status = "SUCCESS";
		long startTime = System.currentTimeMillis();
		
		String LoginID =null;
		if(requestObj.get("LoginID")!=null){
			LoginID=requestObj.get("LoginID").toString();
		}
		
		iLogger.info("TimeFenceService:setTimeFenceDetails:WebService Input:LoginID:"+LoginID+"; AccountCode:"+requestObj.get("AccountCode")+";" +
				"VIN_id:"+requestObj.get("VIN_id")+"; OperatingStartTime:"+requestObj.get("OperatingStartTime")+"; " +
						"OperatingEndtime:"+requestObj.get("OperatingEndtime")+
				"; NotificationPattern:"+requestObj.get("NotificationPattern")+"; NotificationDate:"+requestObj.get("NotificationDate")+";" +
						"RecurrencePattern:"+requestObj.get("RecurrencePattern")+"; RecurrenceRange:"+requestObj.get("RecurrenceRange")+";" +
						"MobileNumber:"+requestObj.get("MobileNumber")+"; DayTimeNotification:"+requestObj.get("DayTimeNotification")+";" +
						"OtherTimeNotification:"+requestObj.get("OtherTimeNotification"));
		
		String accountCode=null, assetID=null, opStartTime=null, opEndTime=null,NotificationPattern=null, NotificationDate=null,MobileNumber=null,
				RecurrencePattern=null,RecurrenceRange=null,DayTimeNotification=null,OtherTimeNotification=null;
		String Source="WebApp"; //WebApp by default. However, if invoked from API, this parameter will be sent as MobileApp
		
		if(requestObj.get("AccountCode")!=null)
			accountCode=requestObj.get("AccountCode").toString();
		
		if(requestObj.get("VIN_id")!=null)
			assetID=requestObj.get("VIN_id").toString();
		
		if(requestObj.get("OperatingStartTime")!=null)
			opStartTime=requestObj.get("OperatingStartTime").toString();
		
		if(requestObj.get("OperatingEndtime")!=null)
			opEndTime=requestObj.get("OperatingEndtime").toString();
		
		if(requestObj.get("NotificationDate")!=null)
			NotificationDate=requestObj.get("NotificationDate").toString();
		
		if(requestObj.get("NotificationPattern")!=null)
			NotificationPattern=requestObj.get("NotificationPattern").toString();
		
		if(requestObj.get("RecurrencePattern")!=null)
		{
			RecurrencePattern=new Gson().toJson(requestObj.get("RecurrencePattern"));
		}
		
		if(requestObj.get("RecurrenceRange")!=null)
		{
			RecurrenceRange=new Gson().toJson(requestObj.get("RecurrenceRange"));
		}
		
		if(requestObj.get("MobileNumber")!=null)
			MobileNumber=requestObj.get("MobileNumber").toString();
		
		if(requestObj.get("DayTimeNotification")!=null)
		{
			DayTimeNotification=new Gson().toJson(requestObj.get("DayTimeNotification"));
		}
		
		if(requestObj.get("OtherTimeNotification")!=null)
		{
			OtherTimeNotification=new Gson().toJson(requestObj.get("OtherTimeNotification"));
		}
		
		if(requestObj.get("Source")!=null)
			Source=requestObj.get("Source").toString();
		
		//*********************************************** Security Checks for call from WebApp
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
				iLogger.info("TimeFenceService:setTimeFenceDetails:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
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
			
			if(requestObj.get("AccountCode")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("AccountCode").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("VIN_id")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("VIN_id").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("NotificationDate")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("NotificationDate").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("NotificationPattern")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("NotificationPattern").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("OperatingStartTime")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("OperatingStartTime").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(requestObj.get("OperatingEndtime")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("OperatingEndtime").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(requestObj.get("MobileNumber")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("MobileNumber").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(requestObj.get("Source")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Source").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		
		
		status = new TimefenceDetailsImpl().setTimefenceDetails(LoginID, accountCode, assetID, 
				opStartTime, opEndTime, NotificationPattern,NotificationDate,RecurrencePattern,RecurrenceRange,MobileNumber,DayTimeNotification,
				OtherTimeNotification,Source);
		
		long endTime = System.currentTimeMillis();
		iLogger.info("TimeFenceService:setTimeFenceDetails:LoginID:"+LoginID+"; AssetID:"+requestObj.get("AssetID")+";" +
					"WebService output:"+status+"; Webservice execution time in ms:"+(endTime-startTime));
		
		return status;
		
	}
	
	
	
	//*****************************************************************************************************************
	@POST
	@Path("deleteTimeFence")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({ MediaType.APPLICATION_JSON })
	public String deleteTimeFence(LinkedHashMap<String, String> requestObj) throws CustomFault 
	{
	
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String status = "SUCCESS";
		long startTime = System.currentTimeMillis();
		
		String LoginID =null;
		if(requestObj.get("LoginID")!=null){
			LoginID=requestObj.get("LoginID");
		}
		
		iLogger.info("TimeFenceService:deleteTimeFence:WebServiceInput:LoginID:"+LoginID+"; AccountCode:"+requestObj.get("AccountCode")+";" +
				"AssetID:"+requestObj.get("AssetID"));
		
		if(requestObj.get("AssetID")==null || requestObj.get("AssetID").trim().length()==0)
		{
			fLogger.fatal("TimeFenceService:deleteTimeFence:Mandatory parameter AssetID is NULL. Hence returning FAILURE");
			return "FAILURE";
		}
		
		
		String Source="WebApp"; //WebApp by default. However, if invoked from API, this parameter will be sent as MobileApp
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
				
				if(csrfToken != null)
				{
					isValidCSRF = util.validateANTICSRFTOKEN(LoginID, csrfToken);
				}
				if (!isValidCSRF) 
				{
					iLogger.info("TimeFenceService:deleteTimeFence:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
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
			
			if(requestObj.get("AccountCode")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("AccountCode").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("AssetID")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("AssetID").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			//Get decoded login ID
			LoginID=new CommonUtil().getUserId(LoginID);
		}
		
		status = new TimefenceDetailsImpl().deleteTimefence(LoginID, requestObj.get("AccountCode"), requestObj.get("AssetID"),Source);
		
		long endTime = System.currentTimeMillis();
		iLogger.info("TimeFenceService:deleteTimeFence:LoginID:"+LoginID+"; AssetID:"+requestObj.get("AssetID")+";" +
				" WebService output:"+status+"; Webservice execution time in ms:"+(endTime-startTime));
		
		return status;
		
	}
	
	
	//*****************************************************************************************************************

	@GET
	@Path("getTimeFenceDetails")
	@Produces({"application/json"})
	public TimeFenceDetailsPOJO getTimeFenceDetails(@QueryParam("LoginID") String LoginID, 
			@QueryParam("AccountCode") String AccountCode,@QueryParam("AssetID") String AssetID, @QueryParam("Source") String Source) throws CustomFault
	{
		TimeFenceDetailsPOJO response = new TimeFenceDetailsPOJO();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		iLogger.info("TimeFenceService:getTimeFenceDetails:WebService Input:LoginID:"+LoginID+"; AccountCode:"+AccountCode+";" +
				"AssetID:"+AssetID);
		
		
		if(AssetID==null || AssetID.trim().length()==0)
		{
			fLogger.fatal("TimeFenceService:getTimeFenceDetails:Mandatory parameter AssetID is NULL. Hence returning empty response");
			return response;
		}
		
		/*response.setVIN("HAR2DXLSC02577560");
		response.setCreatedDate("2021-10-12");
		response.setCustomerCode("100134964");
		response.setOperatingStartTime("08:00");
		response.setOperatingEndtime("18:30");
		response.setMobileNumber("9012345674");
		
		HashMap<String,Object> TimefenceDetails = new HashMap<String,Object>();
		TimefenceDetails.put("NotificationPattern", "Recurrence");
		
		HashMap<String,String> recurrencePattern = new HashMap<String,String>();
		recurrencePattern.put("Daily", "1");
		TimefenceDetails.put("RecurrencePattern", recurrencePattern);
		
		HashMap<String,String> recurrenceRange = new HashMap<String,String>();
		recurrenceRange.put("StartDate", "2021-10-25");
		recurrenceRange.put("EndDate", "2021-12-31");
		TimefenceDetails.put("RecurrenceRange", recurrenceRange);
		
		response.setTimefenceDetails(TimefenceDetails);
		
		HashMap<String,Object> dayTimePreference = new HashMap<String,Object>();
		dayTimePreference.put("SMS", "1");
		dayTimePreference.put("WhatsApp", "0");
		dayTimePreference.put("Voice Call", "0");
		dayTimePreference.put("Push notification", "1");
		response.setDayTimePreference(dayTimePreference);
		
		HashMap<String,Object> otherTimePreference = new HashMap<String,Object>();
		otherTimePreference.put("SMS", "0");
		otherTimePreference.put("WhatsApp", "1");
		otherTimePreference.put("Voice Call", "0");
		otherTimePreference.put("Push notification", "0");
		response.setOtherTimePreference(otherTimePreference);*/
		
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
					iLogger.info("TimeFenceService:getTimeFenceDetails:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
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
		
		response = new TimefenceDetailsImpl().getTimefenceDetails(LoginID, AccountCode, AssetID);
		
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
		iLogger.info("TimeFenceService:deleteTimeFence:LoginID:"+LoginID+"; AssetID:"+AssetID+";" +
				" WebService output:"+response+"; Webservice execution time in ms:"+(endTime-startTime));
		
		if(response!=null && response.getVIN()==null)
			response=null;
		
		return response;
	}
	
	//********************************************************************************************************************************
	@POST
	@Path("setTimeFenceDetailsNew")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({ MediaType.APPLICATION_JSON })
	public String setTimeFenceDetailsNew(LinkedHashMap<String, Object> requestObj) throws CustomFault 
	{
	
		Logger iLogger = InfoLoggerClass.logger;
		
		String status = "SUCCESS";
		long startTime = System.currentTimeMillis();
		
		String LoginID =null;
		if(requestObj.get("LoginID")!=null){
			LoginID=requestObj.get("LoginID").toString();
		}
		
		iLogger.info("TimeFenceService:setTimeFenceDetailsNew:WebService Input:LoginID:"+LoginID+"; AccountCode:"+requestObj.get("AccountCode")+";" +
				"VIN_id:"+requestObj.get("VIN_id")+"; OperatingStartTime:"+requestObj.get("OperatingStartTime")+"; " +
						"OperatingEndtime:"+requestObj.get("OperatingEndtime")+
				"; NotificationPattern:"+requestObj.get("NotificationPattern")+"; NotificationDate:"+requestObj.get("NotificationDate")+";" +
						"RecurrencePattern:"+requestObj.get("RecurrencePattern")+"; RecurrenceRange:"+requestObj.get("RecurrenceRange")+";" +
						"MobileNumber:"+requestObj.get("MobileNumber")+"; NotificationDetails:"+requestObj.get("NotificationDetails"));
		
		String accountCode=null, assetID=null, opStartTime=null, opEndTime=null,NotificationPattern=null, NotificationDate=null,MobileNumber=null,
				RecurrencePattern=null,RecurrenceRange=null,NotificationDetails=null;
		String Source="WebApp"; //WebApp by default. However, if invoked from API, this parameter will be sent as MobileApp
		
		if(requestObj.get("AccountCode")!=null)
			accountCode=requestObj.get("AccountCode").toString();
		
		if(requestObj.get("VIN_id")!=null)
			assetID=requestObj.get("VIN_id").toString();
		
		if(requestObj.get("OperatingStartTime")!=null)
			opStartTime=requestObj.get("OperatingStartTime").toString();
		
		if(requestObj.get("OperatingEndtime")!=null)
			opEndTime=requestObj.get("OperatingEndtime").toString();
		
		if(requestObj.get("NotificationDate")!=null)
			NotificationDate=requestObj.get("NotificationDate").toString();
		
		if(requestObj.get("NotificationPattern")!=null)
			NotificationPattern=requestObj.get("NotificationPattern").toString();
		
		if(requestObj.get("RecurrencePattern")!=null)
		{
			RecurrencePattern=new Gson().toJson(requestObj.get("RecurrencePattern"));
		}
		
		if(requestObj.get("RecurrenceRange")!=null)
		{
			RecurrenceRange=new Gson().toJson(requestObj.get("RecurrenceRange"));
		}
		
		if(requestObj.get("MobileNumber")!=null)
			MobileNumber=requestObj.get("MobileNumber").toString();
		
		if(requestObj.get("NotificationDetails")!=null)
		{
			NotificationDetails=new Gson().toJson(requestObj.get("NotificationDetails"));
		}
		
		/*if(requestObj.get("OtherTimeNotification")!=null)
		{
			OtherTimeNotification=new Gson().toJson(requestObj.get("OtherTimeNotification"));
		}
		*/
		if(requestObj.get("Source")!=null)
			Source=requestObj.get("Source").toString();
		
		//*********************************************** Security Checks for call from WebApp
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
				iLogger.info("TimeFenceService:setTimeFenceDetailsNew:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
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
			
			if(requestObj.get("AccountCode")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("AccountCode").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("VIN_id")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("VIN_id").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("NotificationDate")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("NotificationDate").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("NotificationPattern")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("NotificationPattern").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("OperatingStartTime")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("OperatingStartTime").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(requestObj.get("OperatingEndtime")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("OperatingEndtime").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(requestObj.get("MobileNumber")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("MobileNumber").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(requestObj.get("Source")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Source").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		
		
		status = new TimefenceDetailsImpl().setTimefenceDetailsNew(LoginID, accountCode, assetID, 
				opStartTime, opEndTime, NotificationPattern,NotificationDate,RecurrencePattern,RecurrenceRange,MobileNumber,NotificationDetails,
				Source);
		
		long endTime = System.currentTimeMillis();
		iLogger.info("TimeFenceService:setTimeFenceDetailsNew:LoginID:"+LoginID+"; AssetID:"+requestObj.get("AssetID")+";" +
					"WebService output:"+status+"; Webservice execution time in ms:"+(endTime-startTime));
		
		return status;
		
	}
	
	//****************************************************************************************************************************************
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
		iLogger.info("TimeFenceService:getTimeFenceDetailsNew:WebService Input:LoginID:"+LoginID+"; AccountCode:"+AccountCode+";" +
				"AssetID:"+AssetID);
		
		
		if(AssetID==null || AssetID.trim().length()==0)
		{
			fLogger.fatal("TimeFenceService:getTimeFenceDetailsNew:Mandatory parameter AssetID is NULL. Hence returning empty response");
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
					iLogger.info("TimeFenceService:getTimeFenceDetailsNew:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
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
		
		response = new TimefenceDetailsImpl().getTimefenceDetailsNew(LoginID, AccountCode, AssetID);
		
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
		iLogger.info("TimeFenceService:getTimeFenceDetailsNew:LoginID:"+LoginID+"; AssetID:"+AssetID+";" +
				" WebService output:"+response+"; Webservice execution time in ms:"+(endTime-startTime));
		
		if(response!=null && response.getVIN()==null)
			response=null;
		
		return response;
	}
}
