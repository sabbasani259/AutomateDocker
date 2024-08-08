package remote.wise.service.webservice;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.Logger;


import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;

import remote.wise.service.datacontract.UserDetailsReqContract;
import remote.wise.service.datacontract.UserDetailsRespContract;

import remote.wise.service.implementation.UserDetailsImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;
/**
 *  WebService class to get and set User Details
 * @author jgupta41
 *
 */
@WebService(name = "UserDetailsService")
public class UserDetailsService {
		
	/**
	 * This method will get user Details for given user_Id,List of tenancy_id
	 * @param userReqContract:Get all user details for a given List of tenancy_id
	 * @return userRespObj:Returns list of user details attached for the tenancy_id  defined.
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetUserDetails", action = "GetUserDetails")

	public List<UserDetailsRespContract> getUserDetails(@WebParam(name="userReqContract") UserDetailsReqContract userReqContract)
	throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("UserDetailsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("tenancy_id:"+userReqContract.getTenancy_id()+",  "+"userId:"+userReqContract.getUserId());
		
		//DF20181008 - XSS validation of input for Security Fixes.
		CommonUtil util = new CommonUtil();
		String isValidinput=null;

		isValidinput = util.inputFieldValidation(String.valueOf(userReqContract.getAssetGroupId()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(String.valueOf(userReqContract.getTenancy_id()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		//DF20170919 @Roopa getting decoded UserId
		//DF20180614 -MA369757
		//adding split because in settings->user->user_id is being used to handle fname and mobile number search.userid="searchBy|value"
		if(userReqContract.getUserId()!=null && !(userReqContract.getUserId().contains("|"))){
				String UserID=new CommonUtil().getUserId(userReqContract.getUserId());
				userReqContract.setUserId(UserID);
				iLogger.info("Decoded userId::"+userReqContract.getUserId());
		}
				
		List<UserDetailsRespContract> userRespObj =  new UserDetailsImpl().getUserDetails(userReqContract);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<userRespObj.size(); i++)
		{
			iLogger.info("First Name:"+userRespObj.get(i).getFirst_name()+",    "+"Last Name:"+userRespObj.get(i).getLast_name()+",   " +
					"CountryCode: "+userRespObj.get(i).getCountryCode()+", role_name: "+userRespObj.get(i).getRole_name()+",   " +
					"role_id: "+userRespObj.get(i).getRole_id()+", PrimaryEmailId: "+userRespObj.get(i).getPrimaryEmailId()+",   " +
					"PrimaryMobNumber: "+userRespObj.get(i).getPrimaryMobNumber()+", Tenancy_id: "+userRespObj.get(i).getTenancy_id()+",   " +
					"LoginId: "+userRespObj.get(i).getLoginId()+",country_code:"+ userRespObj.get(i).getCountryCode()+", TimeZone: "+userRespObj.get(i).getTimeZone()+",   " +
					"Language: "+userRespObj.get(i).getLanguage());
			
			isValidinput = util.inputFieldValidation(userRespObj.get(i).getFirst_name());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(userRespObj.get(i).getLast_name());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(userRespObj.get(i).getPrimaryEmailId());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(userRespObj.get(i).getPrimaryMobNumber());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(userRespObj.get(i).getLoginId());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(userRespObj.get(i).getLanguage());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(userRespObj.get(i).getRole_name());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(userRespObj.get(i).getTimeZone());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			ListToStringConversion convObj = new ListToStringConversion();
			String asssetGrpIdList = convObj.getIntegerListString(userRespObj.get(i).getAsset_group_id()).toString();
			isValidinput = util.inputFieldValidation(asssetGrpIdList);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}

			String asssetGrpNameList = convObj.getStringList(userRespObj.get(i).getAsset_group_name()).toString();
			isValidinput = util.inputFieldValidation(asssetGrpNameList);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(String.valueOf(userRespObj.get(i).getIs_tenancy_admin()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(String.valueOf(userRespObj.get(i).getRole_id()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(String.valueOf(userRespObj.get(i).getTenancy_id()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = util.inputFieldValidation(String.valueOf(userRespObj.get(i).getTenancyAdminCount()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UserDetailsService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return userRespObj;


	}
	/**
	 * This method sets all user details that belongs specified LoginId
	 * @param setUserDetails:Get Contact and set User Details to the corresponding to it
	 * @return response_msg:Return the status String as either Success/Failure.
	 * @throws CustomFault
	 * @throws IOException 
	 */

	@WebMethod(operationName = "SetUserDetails", action = "SetUserDetails")


	public String setUserDetails(@WebParam(name="setUserDetails" )UserDetailsRespContract setUserDetails)  throws CustomFault, IOException

	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("UserDetailsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		String encodedUserId = null;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("First Name:"+setUserDetails.getFirst_name()+",    "+"Last Name:"+setUserDetails.getLast_name()+"Tenancy Id:"+setUserDetails.getTenancy_id()
				+"role_id: "+setUserDetails.getRole_id()+", role_name: "+setUserDetails.getRole_name()+",PrimaryMobNumber: "+setUserDetails.getPrimaryMobNumber()+", country_code:"+setUserDetails.getCountryCode()+",TimeZone: "+setUserDetails.getTimeZone()+", login ID: "+setUserDetails.getLoginId());
		
		//DF20180518-KO369761:Sending login user id along with creating user id.
		if(setUserDetails.getLoginId() != null){
			if(setUserDetails.getLoginId().split("\\|").length > 1){
				encodedUserId = setUserDetails.getLoginId().split("\\|")[1];
				setUserDetails.setLoginId(setUserDetails.getLoginId().split("\\|")[0]);
				String UserID=new CommonUtil().getUserId(encodedUserId);
				if(UserID == null){
					throw new CustomFault("Invalid Login ID");
				}
				else{
					//DF20190102-KO369761-Sending edited user id to BO Class for logging edited user data.
					setUserDetails.setLoginId(setUserDetails.getLoginId().split("\\|")[0]+"|"+UserID);
				}
			}
			else{
				String UserID=new CommonUtil().getUserId(setUserDetails.getLoginId());
				if(UserID == null){
					throw new CustomFault("Invalid Login ID");
				}
				setUserDetails.setLoginId(UserID);
			}
		}
		
		//DF20170919 @Roopa getting decoded UserId
		//String UserID=new CommonUtil().getUserId(encodedUserId);
		//setUserDetails.setLoginId(UserID);
		//iLogger.info("Decoded userId::"+setUserDetails.getLoginId());
		
		String response_msg= new UserDetailsImpl().setUserDetails(setUserDetails);
		iLogger.info("----- Webservice Output-----");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UserDetailsService~executionTime:"+(endTime-startTime)+"~"+setUserDetails.getLoginId()+"~"+response_msg);
		return response_msg;
	}
	
	/** 
	 * @author suresh soorneedi
	 * this method will get all the languages that supports sms translation
	 * @return responseList: returns the list of languages
	 * throws exception CustomFault
	 */
	@WebMethod(operationName = "getLanguages" , action = "getLanguages")
	public List<String> getLanguages() throws CustomFault
	{
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("-----webservice input-----");
		
		CommonUtil util = new CommonUtil();
		String isValidinput = null;
		
		List<String> responseList = new UserDetailsImpl().getLanguages();
		
		iLogger.info("----- Webservice Output-----");
		for(int i=0;i<responseList.size();i++)
		{
			iLogger.info("Language:"+responseList.get(i));
			
			isValidinput = util.inputFieldValidation(responseList.get(i));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UserDetailsService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return responseList;
	}
}
