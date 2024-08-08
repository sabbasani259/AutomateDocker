package remote.wise.service.webservice;

import java.util.Arrays;
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

import com.google.gson.Gson;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.GeoFenceDetailsPOJO;
import remote.wise.pojo.GeoFenceDetailsPOJONew;
import remote.wise.service.implementation.GeoFenceDetailsImpl;
import remote.wise.service.implementation.TimefenceDetailsImpl;
import remote.wise.util.CommonUtil;

@Path("/GeoFenceService")
public class GeoFenceService {
	
	@POST
	@Path("setLandmarkDetails")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({ MediaType.APPLICATION_JSON })
	public String setLandmarkDetails(LinkedHashMap<String, Object> requestObj) throws CustomFault 
	{
	
		Logger iLogger = InfoLoggerClass.logger;
		
		String status = "SUCCESS";
		String LoginID =null;
		if(requestObj.get("LoginID")!=null){
			LoginID=requestObj.get("LoginID").toString();
		}
		
		iLogger.info("GeoFenceService:setLandmarkDetails:WebServiceInput:LoginID:"+LoginID+"; AccountCode:"+requestObj.get("AccountCode")+";" +
				"Landmark_id:"+requestObj.get("Landmark_id")+"; Landmark_Category_ID:"+requestObj.get("Landmark_Category_ID")+"; Landmark_Name:"+requestObj.get("Landmark_Name")+
				"; Landmark_Category_Name:"+requestObj.get("Landmark_Category_Name")+"; Latitude:"+requestObj.get("Latitude")+";" +
						"Longitude:"+requestObj.get("Longitude")+"; Radius:"+requestObj.get("Radius")+";" +
						"Address:"+requestObj.get("Address")+"; IsArrival:"+requestObj.get("IsArrival")+";" +
						"IsDeparture:"+requestObj.get("IsDeparture")+";MobileNumber:"+requestObj.get("MobileNumber")+";"+
						"DayTimeNotification:"+requestObj.get("DayTimeNotification")+";OtherTimeNotification:"+requestObj.get("OtherTimeNotification")+";"+
						"MachineList:"+requestObj.get("MachineList"));
		long startTime = System.currentTimeMillis();
		
		String accountCode=null,Landmark_Name=null,Landmark_Category_Name=null,Latitude=null,Longitude=null,DayTimeNotification=null,OtherTimeNotification=null,
				Address=null,IsArrival=null,IsDeparture=null,MobileNumber=null,machineList = null;
		String Source="WebApp"; //WebApp by default. However, if invoked from API, this parameter will be sent as MobileApp
		int Landmark_id=0,Landmark_Category_ID=0;
		double Radius=0.0;
		
		
		if(requestObj.get("AccountCode")!=null)
			accountCode=requestObj.get("AccountCode").toString();
		
		if(requestObj.get("Landmark_id")!=null)
			Landmark_id=(Integer) requestObj.get("Landmark_id");
		
		if(requestObj.get("Landmark_Category_ID")!=null)
			Landmark_Category_ID=(Integer) requestObj.get("Landmark_Category_ID");
		
		if(requestObj.get("Landmark_Name")!=null)
			Landmark_Name=requestObj.get("Landmark_Name").toString();
		
		if(requestObj.get("Landmark_Category_Name")!=null)
			Landmark_Category_Name=requestObj.get("Landmark_Category_Name").toString();
		
		if(requestObj.get("Latitude")!=null)
			Latitude=requestObj.get("Latitude").toString();
		
		if(requestObj.get("Longitude")!=null)
			Longitude=requestObj.get("Longitude").toString();
		
		if(requestObj.get("Radius")!=null)
		{
			try
			{
				Radius=(Double) requestObj.get("Radius");
			}
			catch(ClassCastException e)
			{
				Radius=(double) ((Integer)requestObj.get("Radius"));
			}
		}
		
		if(requestObj.get("Address")!=null)
			Address=requestObj.get("Address").toString();
		
		if(requestObj.get("IsArrival")!=null)
			IsArrival=requestObj.get("IsArrival").toString();
		
		if(requestObj.get("IsDeparture")!=null)
			IsDeparture=requestObj.get("IsDeparture").toString();
		
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
		
		if(requestObj.get("MachineList")!=null)
			machineList=requestObj.get("MachineList").toString();
		
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
				iLogger.info("GeoFenceService:setLandmarkDetails:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
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
			
			if(requestObj.get("Landmark_id")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Landmark_id").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("Landmark_Category_ID")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Landmark_Category_ID").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("Landmark_Name")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Landmark_Name").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("Landmark_Category_Name")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Landmark_Category_Name").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("Latitude")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Latitude").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(requestObj.get("Longitude")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Longitude").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(requestObj.get("Radius")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Radius").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(requestObj.get("Address")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Address").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("IsArrival")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("IsArrival").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("IsDeparture")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("IsDeparture").toString());
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
		
		status = new GeoFenceDetailsImpl().setLandmarkDetails(LoginID, accountCode, Landmark_id, Landmark_Category_ID, 
				Landmark_Name, Landmark_Category_Name, Latitude, Longitude, Radius, Address, IsArrival, 
				IsDeparture, MobileNumber, DayTimeNotification, OtherTimeNotification, machineList,Source);
		
		long endTime = System.currentTimeMillis();
		iLogger.info("GeoFenceService:setLandmarkDetails:LoginID:"+LoginID+"; AssetID:"+requestObj.get("AssetID")+";" +
					"WebService output:"+status+"; Webservice execution time in ms:"+(endTime-startTime));
		
		return status;
		
	}
	
	//***************************************************************************************************************************
	
	@POST
	@Path("deleteLandmark")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({ MediaType.APPLICATION_JSON })
	public String deleteLandmark(LinkedHashMap<String, String> requestObj) throws CustomFault 
	{
	
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String response = "SUCCESS";
		String LoginID =null;
		long startTime = System.currentTimeMillis();
		
		if(requestObj.get("LoginID")!=null){
			LoginID=requestObj.get("LoginID").toString();
		}
		String Source="WebApp"; //WebApp by default. However, if invoked from API, this parameter will be sent as MobileApp
		if(requestObj.get("Source")!=null)
			Source=requestObj.get("Source").toString();
		
		iLogger.info("GeoFenceService:deleteLandmark:WebServiceInput:LoginID:"+LoginID+"; AccountCode:"+requestObj.get("AccountCode")+";" +
				"Landmark_id:"+requestObj.get("Landmark_id"));
		
		if(requestObj.get("Landmark_id")==null ||requestObj.get("Landmark_id").trim().length()==0)
		{
			fLogger.fatal("GeoFenceService:deleteLandmark:Mandatory parameter Landmark_id is NULL. Hence returning FAILURE");
			return "FAILURE";
		}
		
		
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
				iLogger.info("GeoFenceService:setLandmarkDetails:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
				throw new CustomFault("Invalid request.");
			}
			/*else
			{
				util.deleteANTICSRFTOKENS(LoginID, csrfToken, "one");
			}*/
			
			//--------------------------------------- XSS validation of input for Security Fixes
			String isValidinput = null;
			
			if(requestObj.get("AccountCode")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("AccountCode").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("Landmark_id")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Landmark_id").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			//Get decoded login ID
			LoginID=new CommonUtil().getUserId(LoginID);
		}
		
		response = new GeoFenceDetailsImpl().deleteLandmarkDetails(LoginID,requestObj.get("AccountCode"),requestObj.get("Landmark_id"),Source);
		
		long endTime = System.currentTimeMillis();
		iLogger.info("GeoFenceService:deleteLandmark:LoginID:"+LoginID+"; Landmark_id:"+requestObj.get("Landmark_id")+";" +
				" WebService output:"+response+"; Webservice execution time in ms:"+(endTime-startTime));
		
		return response;
		
	}
	
	//*******************************************************************************************************************************
	
	@GET
	@Path("getLandmarkNotificationDetails")
	@Produces({"application/json"})
	public GeoFenceDetailsPOJO getLandmarkNotificationDetails(@QueryParam("LoginID") String LoginID, 
			@QueryParam("AccountCode") String AccountCode,@QueryParam("Landmark_id") String Landmark_id, @QueryParam("Source") String Source) throws CustomFault 
	{
	
		GeoFenceDetailsPOJO response = new GeoFenceDetailsPOJO();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		iLogger.info("GeoFenceService:getLandmarkNotificationDetails:WebServiceInput:LoginID:"+LoginID+"; AccountCode:"+AccountCode+";" +
				"Landmark_id:"+Landmark_id);
		
		if(Landmark_id==null || Landmark_id.trim().length()==0)
		{
			fLogger.fatal("GeoFenceService:getLandmarkNotificationDetails:Mandatory parameter Landmark_id is NULL. Hence returning empty response");
			return response;
		}
		
		/*response.setLandmarkID("22");
		response.setMobileNumber("9012345674");
		
		HashMap<String,Object> dayTimePreference = new HashMap<String,Object>();
		dayTimePreference.put("SMS", "0");
		dayTimePreference.put("WhatsApp", "0");
		dayTimePreference.put("Voice Call", "1");
		dayTimePreference.put("Push notification", "0");
		response.setDayTimePreference(dayTimePreference);
		
		HashMap<String,Object> otherTimePreference = new HashMap<String,Object>();
		otherTimePreference.put("SMS", "1");
		otherTimePreference.put("WhatsApp", "1");
		otherTimePreference.put("Voice Call", "0");
		otherTimePreference.put("Push notification", "1");
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
			}

			if(csrfToken != null)
			{
				isValidCSRF = util.validateANTICSRFTOKEN(LoginID, csrfToken);
			}
			if (!isValidCSRF) 
			{
				iLogger.info("GeoFenceService:setLandmarkDetails:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
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
			
			if(Landmark_id!=null)
			{
				isValidinput = util.inputFieldValidation(Landmark_id.toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(AccountCode!=null)
			{
				isValidinput = util.inputFieldValidation(AccountCode);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		
		
		response = new GeoFenceDetailsImpl().getLandmarkNotificationDetails(LoginID, AccountCode, Landmark_id);
		
		//---------------------------XSS validation of output response contract
		if(Source.equalsIgnoreCase("WebApp"))
		{
			String isValidinput = null;
			CommonUtil util = new CommonUtil();
			
			if(response.getLandmarkID()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkID());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(response.getLandmarkCategoryID()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkCategoryID());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getLandmarkName()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getLandmarkCategoryName()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkCategoryName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
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
			if(response.getMobileNumber()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getMobileNumber());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		
		long endTime = System.currentTimeMillis();
		iLogger.info("GeoFenceService:getLandmarkNotificationDetails:LoginID:"+LoginID+"; Landmark_id:"+Landmark_id+";" +
				" WebService output:"+response+"; Webservice execution time in ms:"+(endTime-startTime));
		
		
		
		return response;
		
	}
	
	//********************************************************************************************************************************
	
	@GET
	@Path("getLandmarkDetailsForVIN")
	@Produces({"application/json"})
	public GeoFenceDetailsPOJO getLandmarkDetailsForVIN(@QueryParam("LoginID") String LoginID, 
			@QueryParam("AccountCode") String AccountCode,@QueryParam("VIN") String VIN, @QueryParam("Source") String Source) throws CustomFault 
	{
	
		GeoFenceDetailsPOJO response = new GeoFenceDetailsPOJO();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		iLogger.info("GeoFenceService:getLandmarkDetailsForVIN:WebServiceInput:LoginID:"+LoginID+"; AccountCode:"+AccountCode+";" +
				"VIN:"+VIN);
		
		if(VIN==null || VIN.trim().length()==0)
		{
			fLogger.fatal("GeoFenceService:getLandmarkDetailsForVIN:Mandatory parameter VIN is NULL. Hence returning empty response");
			return response;
		}
		/*response.setLandmarkID("22");
		response.setLandmarkCategoryID("42");
		response.setLandmarkName("SiteArea");
		response.setLandmarkCategoryName("BHL Machines");
		response.setVIN("HAR2DXLSC02577560");
		response.setCreatedDate("2021-10-12");
		response.setCustomerCode("100134964");
		response.setMobileNumber("9012345674");
		
		
		HashMap<String,Object> geofenceDetails = new HashMap<String,Object>();
		geofenceDetails.put("LAT", "28.32133333333333");
		geofenceDetails.put("LONG", "77.30983333333334");
		geofenceDetails.put("Radius", "12");
		geofenceDetails.put("Address", "National Highway 2, Sector 58, Faridabad, Haryana 121004, India");
		geofenceDetails.put("IsArrival", "1");
		geofenceDetails.put("IsDeparture", "1");
		response.setGeofenceDetails(geofenceDetails);
		
		HashMap<String,Object> dayTimePreference = new HashMap<String,Object>();
		dayTimePreference.put("SMS", "0");
		dayTimePreference.put("Voice Call", "1");
		dayTimePreference.put("Push notification", "0");
		response.setDayTimePreference(dayTimePreference);
		
		HashMap<String,Object> otherTimePreference = new HashMap<String,Object>();
		otherTimePreference.put("SMS", "1");
		otherTimePreference.put("Voice Call", "0");
		otherTimePreference.put("Push notification", "1");
		response.setOtherTimePreference(otherTimePreference);*/
		
		if(Source==null) 
			Source="WebApp"; //WebApp by default. However, if invoked from API, this parameter will be sent as MobileApp
		
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
					iLogger.info("GeoFenceService:setLandmarkDetails:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
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
			
			if(VIN!=null)
			{
				isValidinput = util.inputFieldValidation(VIN);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(AccountCode!=null)
			{
				isValidinput = util.inputFieldValidation(AccountCode);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		response = new GeoFenceDetailsImpl(). getLandmarkDetailsForVIN(LoginID,AccountCode,VIN);
		
		
		if(Source.equalsIgnoreCase("WebApp"))
		{
			String isValidinput = null;
			CommonUtil util = new CommonUtil();
			
			if(response.getLandmarkID()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkID());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(response.getLandmarkCategoryID()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkCategoryID());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getLandmarkName()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getLandmarkCategoryName()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkCategoryName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
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
			if(response.getMobileNumber()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getMobileNumber());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		
		long endTime = System.currentTimeMillis();
		iLogger.info("GeoFenceService:getLandmarkDetailsForVIN:LoginID:"+LoginID+"; VIN:"+VIN+";" +
				" WebService output:"+response+"; Webservice execution time in ms:"+(endTime-startTime));
		if(response.getLandmarkID()==null){
			response=null;
		}			
		return response;
		
	}


	//****************************************************************************************************************************************
	@GET
	@Path("getAllLandmarksForUser")
	@Produces({"application/json"})
	public List<GeoFenceDetailsPOJO> getAllLandmarksForUser(@QueryParam("LoginID") String LoginID, 
			@QueryParam("AccountCode") String AccountCode, @QueryParam("Source") String Source) throws CustomFault 
	{
	
		List<GeoFenceDetailsPOJO> response = new LinkedList<GeoFenceDetailsPOJO>();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		iLogger.info("GeoFenceService:getAllLandmarksForUser:WebServiceInput:LoginID:"+LoginID+"; AccountCode:"+AccountCode);
		
		if(LoginID==null || LoginID.trim().length()==0)
		{
			fLogger.fatal("GeoFenceService:getAllLandmarksForUser:Mandatory parameter LoginID is NULL. Hence returning empty response");
			return response;
		}
		
		
		if(Source==null) 
			Source="WebApp"; //WebApp by default. However, if invoked from API, this parameter will be sent as MobileApp
		
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
					iLogger.info("GeoFenceService:getAllLandmarksForUser:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
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
			
			if(LoginID!=null)
			{
				isValidinput = util.inputFieldValidation(LoginID);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(AccountCode!=null)
			{
				isValidinput = util.inputFieldValidation(AccountCode);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		response = new GeoFenceDetailsImpl(). getAllLandmarksForUser(LoginID,AccountCode);
		
		
		long endTime = System.currentTimeMillis();
		iLogger.info("GeoFenceService:getAllLandmarksForUser:LoginID:"+LoginID+";" +
				" WebService output:Response list size"+response.size()+"; Webservice execution time in ms:"+(endTime-startTime));
		if(response.size()==0){
			response=null;
		}			
		return response;
		
	}
	
	//*************************************************************************************************************************************
	@POST
	@Path("setLandmarkDetailsNew")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({ MediaType.APPLICATION_JSON })
	public String setLandmarkDetailsNew(LinkedHashMap<String, Object> requestObj) throws CustomFault 
	{
	
		Logger iLogger = InfoLoggerClass.logger;
		
		String status = "SUCCESS";
		String LoginID =null;
		if(requestObj.get("LoginID")!=null){
			LoginID=requestObj.get("LoginID").toString();
		}
		
		iLogger.info("GeoFenceService:setLandmarkDetailsNew:WebServiceInput:LoginID:"+LoginID+"; AccountCode:"+requestObj.get("AccountCode")+";" +
				"Landmark_id:"+requestObj.get("Landmark_id")+"; Landmark_Category_ID:"+requestObj.get("Landmark_Category_ID")+"; Landmark_Name:"+requestObj.get("Landmark_Name")+
				"; Landmark_Category_Name:"+requestObj.get("Landmark_Category_Name")+"; Latitude:"+requestObj.get("Latitude")+";" +
						"Longitude:"+requestObj.get("Longitude")+"; Radius:"+requestObj.get("Radius")+";" +
						"Address:"+requestObj.get("Address")+"; IsArrival:"+requestObj.get("IsArrival")+";" +
						"IsDeparture:"+requestObj.get("IsDeparture")+";MobileNumber:"+requestObj.get("MobileNumber")+";"+
						"NotificationDetails:"+requestObj.get("NotificationDetails")+";"+
						"MachineList:"+requestObj.get("MachineList"));
		long startTime = System.currentTimeMillis();
		
		String accountCode=null,Landmark_Name=null,Landmark_Category_Name=null,Latitude=null,Longitude=null,NotificationDetails=null,
				Address=null,IsArrival=null,IsDeparture=null,MobileNumber=null,machineList = null;
		String Source="WebApp"; //WebApp by default. However, if invoked from API, this parameter will be sent as MobileApp
		int Landmark_id=0,Landmark_Category_ID=0;
		double Radius=0.0;
		
		
		if(requestObj.get("AccountCode")!=null)
			accountCode=requestObj.get("AccountCode").toString();
		
		if(requestObj.get("Landmark_id")!=null)
			Landmark_id=(Integer) requestObj.get("Landmark_id");
		
		if(requestObj.get("Landmark_Category_ID")!=null)
			Landmark_Category_ID=(Integer) requestObj.get("Landmark_Category_ID");
		
		if(requestObj.get("Landmark_Name")!=null)
			Landmark_Name=requestObj.get("Landmark_Name").toString();
		
		if(requestObj.get("Landmark_Category_Name")!=null)
			Landmark_Category_Name=requestObj.get("Landmark_Category_Name").toString();
		
		if(requestObj.get("Latitude")!=null)
			Latitude=requestObj.get("Latitude").toString();
		
		if(requestObj.get("Longitude")!=null)
			Longitude=requestObj.get("Longitude").toString();
		
		if(requestObj.get("Radius")!=null)
		{
			try
			{
				Radius=(Double) requestObj.get("Radius");
			}
			catch(ClassCastException e)
			{
				Radius=(double) ((Integer)requestObj.get("Radius"));
			}
		}
		
		if(requestObj.get("Address")!=null)
			Address=requestObj.get("Address").toString();
		
		if(requestObj.get("IsArrival")!=null)
			IsArrival=requestObj.get("IsArrival").toString();
		
		if(requestObj.get("IsDeparture")!=null)
			IsDeparture=requestObj.get("IsDeparture").toString();
		
		if(requestObj.get("MobileNumber")!=null)
			MobileNumber=requestObj.get("MobileNumber").toString();
		
		if(requestObj.get("NotificationDetails")!=null)
		{
			NotificationDetails=new Gson().toJson(requestObj.get("NotificationDetails"));
		}
		
		if(requestObj.get("MachineList")!=null)
			machineList=requestObj.get("MachineList").toString();
		
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
				iLogger.info("GeoFenceService:setLandmarkDetailsNew:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
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
			
			if(requestObj.get("Landmark_id")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Landmark_id").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("Landmark_Category_ID")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Landmark_Category_ID").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("Landmark_Name")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Landmark_Name").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("Landmark_Category_Name")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Landmark_Category_Name").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("Latitude")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Latitude").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(requestObj.get("Longitude")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Longitude").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(requestObj.get("Radius")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Radius").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(requestObj.get("Address")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("Address").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("IsArrival")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("IsArrival").toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(requestObj.get("IsDeparture")!=null)
			{
				isValidinput = util.inputFieldValidation(requestObj.get("IsDeparture").toString());
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
		
		status = new GeoFenceDetailsImpl().setLandmarkDetailsNew2(LoginID, accountCode, Landmark_id, Landmark_Category_ID, 
				Landmark_Name, Landmark_Category_Name, Latitude, Longitude, Radius, Address, IsArrival, 
				IsDeparture, MobileNumber, NotificationDetails,  machineList,Source);
		
		long endTime = System.currentTimeMillis();
		iLogger.info("GeoFenceService:setLandmarkDetailsNew:LoginID:"+LoginID+"; AssetID:"+requestObj.get("AssetID")+";" +
					"WebService output:"+status+"; Webservice execution time in ms:"+(endTime-startTime));
		
		return status;
		
	}
	
	//********************************************************************************************************************************
	@GET
	@Path("getLandmarkNotificationDetailsNew")
	@Produces({"application/json"})
	public GeoFenceDetailsPOJONew getLandmarkNotificationDetailsNew(@QueryParam("LoginID") String LoginID, 
			@QueryParam("AccountCode") String AccountCode,@QueryParam("Landmark_id") String Landmark_id, @QueryParam("Source") String Source) throws CustomFault 
	{
	
		GeoFenceDetailsPOJONew response = new GeoFenceDetailsPOJONew();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		iLogger.info("GeoFenceService:getLandmarkNotificationDetailsNew:WebServiceInput:LoginID:"+LoginID+"; AccountCode:"+AccountCode+";" +
				"Landmark_id:"+Landmark_id);
		
		if(Landmark_id==null || Landmark_id.trim().length()==0)
		{
			fLogger.fatal("GeoFenceService:getLandmarkNotificationDetailsNew:Mandatory parameter Landmark_id is NULL. Hence returning empty response");
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
				iLogger.info("GeoFenceService:getLandmarkNotificationDetailsNew:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
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
			
			if(Landmark_id!=null)
			{
				isValidinput = util.inputFieldValidation(Landmark_id.toString());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(AccountCode!=null)
			{
				isValidinput = util.inputFieldValidation(AccountCode);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		
		
		response = new GeoFenceDetailsImpl().getLandmarkNotificationDetailsNew(LoginID, AccountCode, Landmark_id);
		
		//---------------------------XSS validation of output response contract
		if(Source.equalsIgnoreCase("WebApp"))
		{
			String isValidinput = null;
			CommonUtil util = new CommonUtil();
			
			if(response.getLandmarkID()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkID());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(response.getLandmarkCategoryID()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkCategoryID());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getLandmarkName()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getLandmarkCategoryName()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkCategoryName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
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
			if(response.getMobileNumber()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getMobileNumber());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		
		long endTime = System.currentTimeMillis();
		iLogger.info("GeoFenceService:getLandmarkNotificationDetailsNew:LoginID:"+LoginID+"; Landmark_id:"+Landmark_id+";" +
				" WebService output:"+response+"; Webservice execution time in ms:"+(endTime-startTime));
		
		
		
		return response;
		
	}
	
	//********************************************************************************************************************************
	
	@GET
	@Path("getLandmarkDetailsForVINNew")
	@Produces({"application/json"})
	public GeoFenceDetailsPOJONew getLandmarkDetailsForVINNew(@QueryParam("LoginID") String LoginID, 
			@QueryParam("AccountCode") String AccountCode,@QueryParam("VIN") String VIN, @QueryParam("Source") String Source) throws CustomFault 
	{
	
		GeoFenceDetailsPOJONew response = new GeoFenceDetailsPOJONew();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		iLogger.info("GeoFenceService:getLandmarkDetailsForVINNew:WebServiceInput:LoginID:"+LoginID+"; AccountCode:"+AccountCode+";" +
				"VIN:"+VIN);
		
		if(VIN==null || VIN.trim().length()==0)
		{
			fLogger.fatal("GeoFenceService:getLandmarkDetailsForVINNew:Mandatory parameter VIN is NULL. Hence returning empty response");
			return response;
		}
		
		
		if(Source==null) 
			Source="WebApp"; //WebApp by default. However, if invoked from API, this parameter will be sent as MobileApp
		
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
					iLogger.info("GeoFenceService:getLandmarkDetailsForVINNew:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
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
			
			if(VIN!=null)
			{
				isValidinput = util.inputFieldValidation(VIN);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(AccountCode!=null)
			{
				isValidinput = util.inputFieldValidation(AccountCode);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		response = new GeoFenceDetailsImpl(). getLandmarkDetailsForVINNew(LoginID,AccountCode,VIN);
		
		
		if(Source.equalsIgnoreCase("WebApp"))
		{
			String isValidinput = null;
			CommonUtil util = new CommonUtil();
			
			if(response.getLandmarkID()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkID());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			
			if(response.getLandmarkCategoryID()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkCategoryID());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getLandmarkName()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(response.getLandmarkCategoryName()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getLandmarkCategoryName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
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
			if(response.getMobileNumber()!=null)
			{
				isValidinput = util.inputFieldValidation(response.getMobileNumber());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		
		long endTime = System.currentTimeMillis();
		iLogger.info("GeoFenceService:getLandmarkDetailsForVINNew:LoginID:"+LoginID+"; VIN:"+VIN+";" +
				" WebService output:"+response+"; Webservice execution time in ms:"+(endTime-startTime));
		if(response.getLandmarkID()==null){
			response=null;
		}			
		return response;
		
	}


	//****************************************************************************************************************************************
	@GET
	@Path("getAllLandmarksForUserNew")
	@Produces({"application/json"})
	public List<GeoFenceDetailsPOJONew> getAllLandmarksForUserNew(@QueryParam("LoginID") String LoginID, 
			@QueryParam("AccountCode") String AccountCode, @QueryParam("Source") String Source) throws CustomFault 
	{
	
		List<GeoFenceDetailsPOJONew> response = new LinkedList<GeoFenceDetailsPOJONew>();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		iLogger.info("GeoFenceService:getAllLandmarksForUserNew:WebServiceInput:LoginID:"+LoginID+"; AccountCode:"+AccountCode);
		
		if(LoginID==null || LoginID.trim().length()==0)
		{
			fLogger.fatal("GeoFenceService:getAllLandmarksForUserNew:Mandatory parameter LoginID is NULL. Hence returning empty response");
			return response;
		}
		
		
		if(Source==null) 
			Source="WebApp"; //WebApp by default. However, if invoked from API, this parameter will be sent as MobileApp
		
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
					iLogger.info("GeoFenceService:getAllLandmarksForUserNew:LoginID:"+LoginID+"; Invalid request. CSRF Validation failed !!");
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
			
			if(LoginID!=null)
			{
				isValidinput = util.inputFieldValidation(LoginID);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			if(AccountCode!=null)
			{
				isValidinput = util.inputFieldValidation(AccountCode);
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		response = new GeoFenceDetailsImpl(). getAllLandmarksForUserNew(LoginID,AccountCode);
		
		
		long endTime = System.currentTimeMillis();
		iLogger.info("GeoFenceService:getAllLandmarksForUserNew:LoginID:"+LoginID+";" +
				" WebService output:Response list size"+response.size()+"; Webservice execution time in ms:"+(endTime-startTime));
		if(response.size()==0){
			response=null;
		}			
		return response;
		
	}
}
