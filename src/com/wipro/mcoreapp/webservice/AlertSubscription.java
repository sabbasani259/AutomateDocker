package com.wipro.mcoreapp.webservice;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wipro.mcoreapp.implementation.AlertSubscriptionImpl;
import com.wipro.mcoreapp.implementation.UserAlertPrefMigration;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

@Path("/AlertSubscription")
public class AlertSubscription 
{
	@GET
	@Path("getSubscriptionDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSubscriptionDetails(@Context HttpHeaders httpHeaders, @QueryParam("VIN") String VIN, @QueryParam("loginId") String loginId, @QueryParam("loginTenancyId") String loginTenancyId) throws CustomFault
	{
		String subscriberJSONArray= null;
		Logger iLogger = InfoLoggerClass.logger;
		
		//DF20170919 @Roopa getting decoded UserId
		CommonUtil utilObj = new CommonUtil();
		//loginId= utilObj.getUserId(loginId);
		String csrfToken = null;
		
		iLogger.info("MCoreApp:AlertSubscription:getSubscriptionDetails:WebService Input-----> VIN:"+VIN+"; loginId:"+loginId+"; loginTenancyId:"+loginTenancyId);
		
		//DF20180806:KO369761 - Validating VIN hierarchy against login id/tenancy id
		String serialNum = utilObj.validateVIN(Integer.parseInt(loginTenancyId), VIN);
		if(serialNum == null || serialNum.equalsIgnoreCase("FAILURE")){
			throw new CustomFault("Invalid VIN Number");
		}
		
		long startTime = System.currentTimeMillis();
		
		if(httpHeaders.getRequestHeader("CSRFTOKEN")!=null)
		{
			csrfToken=httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
		}
		iLogger.info("AlertSubscription :: getSubscriberDetails ::  received csrftoken :: "+csrfToken);
		boolean isValidCSRF=false;
		if(csrfToken!=null){
			isValidCSRF=utilObj.validateANTICSRFTOKEN(loginId,csrfToken);
		}
		iLogger.info("AlertSubscription :: getSubscriberDetails ::   csrftoken isValidCSRF :: "+isValidCSRF);
		if(!isValidCSRF)
		{
			iLogger.info("AlertSubscription :: getSubscriberDetails ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}
		
		//DF20181005 ::: MA369757 :: Security checks for all input fields	
		String isValidinput=null;
		if(VIN!=null){
			isValidinput = utilObj.inputFieldValidation(VIN);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		
		subscriberJSONArray = new AlertSubscriptionImpl().getSubscriberGroupDetails(VIN);
		
		long endTime=System.currentTimeMillis();
		iLogger.info("MCoreApp:AlertSubscription:getSubscriptionDetails:WebService Output -----> subscriberJSONArray:"+subscriberJSONArray+"; Total Time taken in ms:"+(endTime - startTime));
		
		return subscriberJSONArray;
	}
	
	
	@POST
	@Path("setSubscriberDetails")
	@Produces("text/plain")
	@Consumes({MediaType.APPLICATION_JSON})
	public String setSubscriberDetails(@Context HttpHeaders httpHeaders, String subscriberJSONArray) throws CustomFault
	{
		String status="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		String csrfToken = null;
		
		iLogger.info("MCoreApp:AlertSubscription:setSubscriberDetails:WebService Input-----> subscriberJSONArray:"+subscriberJSONArray);
		long startTime = System.currentTimeMillis();
		
		HashMap hashMap = new Gson().fromJson(subscriberJSONArray, new TypeToken<HashMap<String, Object>>() {}.getType());
		
		//DF20181004 :: csrf
		CommonUtil utilObj = new CommonUtil();
		if(httpHeaders.getRequestHeader("CSRFTOKEN")!=null)
		{
			csrfToken=httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
		}
		iLogger.info("AlertSubscription :: setSubscriberDetails ::  received csrftoken :: "+csrfToken);
		boolean isValidCSRF=false;
		if(csrfToken!=null){
			isValidCSRF=utilObj.validateANTICSRFTOKEN(hashMap.get("loginId").toString(),csrfToken);
		}
		iLogger.info("AlertSubscription :: setSubscriberDetails ::   csrftoken isValidCSRF :: "+isValidCSRF);
		if(!isValidCSRF)
		{
			iLogger.info("AlertSubscription :: setSubscriberDetails ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}
		else
		{
			//delete the validated token
			utilObj.deleteANTICSRFTOKENS(hashMap.get("loginId").toString(),csrfToken,"one");
		}

		// DF20170919 @Roopa getting decoded UserId
		CommonUtil util = new  CommonUtil();
		String UserID = util.getUserId(hashMap.get("loginId").toString());
		hashMap.put("loginId",UserID);

		//DF20180827-KO369761-Security Fix
		if(UserID == null)
			throw new CustomFault("Invalid Login ID");

		status = new AlertSubscriptionImpl().setSubscriberGroupDetails(hashMap.get("VIN").toString(), hashMap.get("loginId").toString(), 
																		hashMap.get("subscriberJSONArray").toString());
		
		long endTime = System.currentTimeMillis();
		iLogger.info("MCoreApp:AlertSubscription:setSubscriberDetails:WebService Output-----> setStatus:"+status+"; Total Time taken in ms:"+(endTime - startTime));
		
		return status;
	}
	
	
	@GET
	@Path("setDefaultSubscribers")
	@Produces("text/plain")
	public String setDefaultSubscribers(@QueryParam("VIN") String VIN)
	{
		String status= "SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		
		iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:WebService Input-----> VIN:"+VIN);
		long startTime = System.currentTimeMillis();
		
		status = new AlertSubscriptionImpl().setDefaultSubscribers(VIN);
		
		long endTime=System.currentTimeMillis();
		iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:WebService Output -----> status:"+status+"; Total Time taken in ms:"+(endTime - startTime));
		
		return status;
	}
	
	
	@GET
	@Path("migrateSubsFromMySQL")
	@Produces("text/plain")
	public String migrateSubsFromMySQL(@QueryParam("VIN") String VIN, @QueryParam("SubscriberGroup") String SubscriberGroup)
	{
		String status="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		
		iLogger.info("MCoreApp:AlertSubscription:migrateSubscribersFromMySQL:WebService Input-----> VIN:"+VIN+"; SubscriberGroup:"+SubscriberGroup);
		long startTime = System.currentTimeMillis();
		
		status = new AlertSubscriptionImpl().migrateSubscribersFromMySQL(VIN, SubscriberGroup);
		
		long endTime=System.currentTimeMillis();
		iLogger.info("MCoreApp:AlertSubscription:migrateSubscribersFromMySQL:WebService Output -----> status:"+status+"; Total Time taken in ms:"+(endTime - startTime));
		
		
		return status;
	}
	
	@GET
	@Path("migrateUserPrefFromMySQL")
	@Produces("text/plain")
	public String migrateUserPrefFromMySQL(@QueryParam("UserID") String UserID)
	{
		String status="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		
		iLogger.info("MCoreApp:UserAlertPrefMigration:migrateUserPrefFromMySQL:WebService Input-----> UserID:"+UserID);
		long startTime = System.currentTimeMillis();
		
		//DF20170919 @Roopa getting decoded UserId
		UserID=new CommonUtil().getUserId(UserID);
		
		status = new UserAlertPrefMigration().migrateUserPref(UserID);
		
		long endTime=System.currentTimeMillis();
		iLogger.info("MCoreApp:UserAlertPrefMigration:migrateUserPrefFromMySQL:WebService Output -----> status:"+status+"; Total Time taken in ms:"+(endTime - startTime));
		
		
		return status;
	}
}
