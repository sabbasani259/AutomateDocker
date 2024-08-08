/**
 * CR308 : 20220613 : Dhiraj K : Code Fix for BW service closures from Portal
 */
package remote.wise.service.webservice;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;


import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AssetExtendedRespContract;
import remote.wise.service.datacontract.UserAlertsCloserRespContract;
import remote.wise.service.datacontract.UserAlertsReqContract;
import remote.wise.service.datacontract.UserAlertsRespContract;
import remote.wise.service.implementation.AlertDashBoardRESTImpl;
import remote.wise.service.implementation.AssetExtendedImpl;
import remote.wise.service.implementation.ServiceHistoryImpl;
import remote.wise.service.implementation.UserAlertsImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;

/** Webservice method to get the generated Alerts
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "UserAlertsService")
public class UserAlertsService 
{	
	/** This method returns the List of Alerts that are accessible to the Logged in user
	 * @param reqObj Input filters based on which the list of alerts would be returned
	 * @return List of Alert with their details
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetUserAlerts", action = "GetUserAlerts")
	public List<UserAlertsRespContract> getUserAlerts(@WebParam(name="reqObj" ) UserAlertsReqContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("UserAlertsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("loginId:"+reqObj.getLoginId()+",  "+"roleId:"+reqObj.getRoleId()+",   " +
				"userTenancyIdList:"+reqObj.getUserTenancyIdList()+",  "+"tenancyIdList:"+reqObj.getTenancyIdList()+",  "+
				"serialNumber:"+reqObj.getSerialNumber()+",  " +"startDate: "+reqObj.getStartDate()+
				"alertTypeId:"+reqObj.getAlertTypeId()+",  " +"alertSeverity: "+reqObj.getAlertSeverity()+
				"isOwnTenancyAlerts:"+reqObj.isOwnTenancyAlerts()+",  " +"isHistory: "+reqObj.isHistory());
		
		//DF20181011 - KO369761 - Extracting CSRF Token from login field.
		CommonUtil utilObj = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
		if(reqObj.getLoginId().split("\\|").length > 1){
			csrfToken=reqObj.getLoginId().split("\\|")[1];
			reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]);

			//DF20181011 - KO369761 - Validating the CSRF Token against login id.
			if(csrfToken != null)
				isValidCSRF=utilObj.validateANTICSRFTOKEN(reqObj.getLoginId(),csrfToken);
			if(!isValidCSRF){
				iLogger.info("getUserAlerts ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			}
		}
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=utilObj.getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());

		//DF20181005-KO369761-XSS validation of input req contract
		String isValidinput=null;
		ListToStringConversion convObj = new ListToStringConversion();
		isValidinput = utilObj.inputFieldValidation(reqObj.getSerialNumber());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.inputFieldValidation(reqObj.getStartDate());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.inputFieldValidation(String.valueOf(reqObj.getPageNumber()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.inputFieldValidation(String.valueOf(reqObj.isHistory()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.inputFieldValidation(String.valueOf(reqObj.isOwnTenancyAlerts()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.inputFieldValidation(String.valueOf(convObj.getStringList(reqObj.getAlertSeverity())));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.inputFieldValidation(String.valueOf(convObj.getIntegerListString(reqObj.getAlertTypeId())));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.inputFieldValidation(String.valueOf(convObj.getIntegerListString(reqObj.getTenancyIdList())));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.inputFieldValidation(String.valueOf(convObj.getIntegerListString(reqObj.getUserTenancyIdList())));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.inputFieldValidation(String.valueOf(reqObj.getRoleId()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		//check whether the Login Id is specified
		if(reqObj.getLoginId()==null)
		{
			throw new CustomFault("Login Id not specified");
		}
		
		/*//DF20180809:KO369761-Role validation against login id
		if(!utilObj.roleValidationAgainstLoginId(UserID, reqObj.getRoleId())){
			throw new CustomFault("Unauthorized request.");
		}*/
		
		//DF20180806:KO369761 - Validating VIN hierarchy against login id/tenancy id
		if(reqObj.getSerialNumber() != null){
			int tenancyId = utilObj.getTenancyIdFromLoginId(UserID);
			String serialNumber = utilObj.validateVIN(tenancyId, reqObj.getSerialNumber());
			if(serialNumber == null || serialNumber.equalsIgnoreCase("FAILURE")){
				throw new CustomFault("Invalid VIN Number");
			}
		}
		
		List<UserAlertsRespContract> response =new ArrayList<UserAlertsRespContract>();
		
		//DF20160728 @Roopa invoking new method for alerts(when no filters are applied)
		
		if((reqObj.getTenancyIdList().get(0).equals(reqObj.getUserTenancyIdList().get(0))) && (reqObj.getAlertTypeId()==null || reqObj.getAlertTypeId().size()==0) && (reqObj.getAlertSeverity()==null || reqObj.getAlertSeverity().size()==0) && (reqObj.isHistory()==false)){
		
			
			AlertDashBoardRESTImpl implObj=new AlertDashBoardRESTImpl();
			
		
			int pageNumber = reqObj.getPageNumber();
			//check whether the page number is specified //DF20160803 @Roopa
			if(pageNumber==0)
			{
				pageNumber=1;
			}
			
			response = implObj.getUserAlerts(reqObj.getUserTenancyIdList().get(0), pageNumber, reqObj.getSerialNumber(),reqObj.getLoginId());
		}
		else{
			
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

			if(reqObj.getUserTenancyIdList()!=null && reqObj.getUserTenancyIdList().size()>0){
				reqObj.setUserTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getUserTenancyIdList()));
			}
			
			if(reqObj.getTenancyIdList()!=null && reqObj.getTenancyIdList().size()>0){
				reqObj.setTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getTenancyIdList()));
			}
			
	     response = new UserAlertsImpl().getUserAlerts(reqObj);
		}
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("serialNumber:"+response.get(i).getSerialNumber()+",  "+"alertTypeId:"+response.get(i).getAlertTypeId()+",   " +
					"alertTypeName:"+response.get(i).getAlertTypeName()+",  "+"alertDescription:"+response.get(i).getAlertDescription()+",  "+
					"latestReceivedTime:"+response.get(i).getLatestReceivedTime()+",  " +"alertSeverity: "+response.get(i).getAlertSeverity()+
					"alertCounter:"+response.get(i).getAlertCounter()+",  " +"remarks:"+response.get(i).getRemarks()+
					//"assetEventId:"+response.get(i).getAssetEventId() + ",  "); //CR308.o
					"assetEventId:"+response.get(i).getAssetEventId() + ",  " +  "serviceName:"+response.get(i).getServiceName());//CR308.n

			//DF20181005-KO369761-XSS validation of output resp contract
			isValidinput = utilObj.responseValidation(response.get(i).getAlertDescription());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(response.get(i).getAlertSeverity());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(response.get(i).getAlertTypeName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(response.get(i).getLatestReceivedTime());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(response.get(i).getRemarks());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(response.get(i).getSerialNumber());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(response.get(i).getAlertCounter()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(response.get(i).getAlertTypeId()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(response.get(i).getAssetEventId()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UserAlertsService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		return response;
	}
	
	
	@WebMethod(operationName = "setUserAlerts", action = "setUserAlerts")
	public String setUserAlertsCloser(@WebParam(name="reqObj" ) UserAlertsCloserRespContract respObj) throws CustomFault
	{

		//WiseLogger infoLogger = WiseLogger.getLogger("setUserAlertsCloser:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("serialNumber:"+respObj.getSerialNumber()+" , " +" EventgeneratedTime:"+respObj.getEventGeneratedTime()+" ,"+
				" LogInId:"+respObj.getLoginID()+" , "+" , JobCardNumber:"+respObj.getJobCardNumber()+" ,"+" , MessageId:"+respObj.getMessageId()+" ,"+"ServicedDate:"+respObj.getServicedDate());
		
		//DF20181005-KO369761-XSS validation of output resp contract
		String isValidinput = null;
		CommonUtil utilObj = new CommonUtil();
		isValidinput = utilObj.responseValidation(respObj.getEventGeneratedTime());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.responseValidation(respObj.getJobCardNumber());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.responseValidation(respObj.getMessageId());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.responseValidation(respObj.getSerialNumber());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.responseValidation(respObj.getServicedDate());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(respObj.getLoginID());
		respObj.setLoginID(UserID);
		iLogger.info("Decoded userId::"+respObj.getLoginID());
		
		String response_msg= new UserAlertsImpl().setUserAlertsCloserDetails(respObj.getSerialNumber(), respObj.getLoginID(), respObj.getJobCardNumber(), respObj.getServicedDate(), respObj.getMessageId(),respObj.getEventGeneratedTime());
		iLogger.info("----- Webservice Output-----");
		iLogger.info("status:"+response_msg+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UserAlertsService~executionTime:"+(endTime-startTime)+"~"+respObj.getLoginID()+"~"+response_msg);
		return response_msg;		
	}
}
