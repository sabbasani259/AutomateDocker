package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.Qhandler.UserDeactivationProducer;
import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.DeleteUserReqContract;
import remote.wise.service.implementation.DeleteUserImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

/*
 * CR271 - VidyaSagar -20220225 - send deactivated users data to kafka producer 
 */
@WebService(name = "DeleteUserService")
public class DeleteUserService {
	
	
	/**
	 * 
	 * method to make user inactive in contact table
	 * @param EmailAddress
	 * @return String
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "DeleteUser", action = "DeleteUser")
	public String deleteUser(@WebParam(name="reqObj") DeleteUserReqContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("DeleteUserService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("loginId:"+reqObj.getLoginId()+"");
		String encodedUserId = null;
		
		CommonUtil util = new CommonUtil();
		//DF20181025 -  - Extracting CSRF Token from login field.
		String csrfToken = null;
		boolean isValidCSRF = false;
		if(reqObj.getLoginId() != null){
		if(reqObj.getLoginId().split("\\|").length > 2){
			csrfToken=reqObj.getLoginId().split("\\|")[2];
			reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]+"|"+reqObj.getLoginId().split("\\|")[1]);
		}
		}
		//DF20181025 -  - Validating the CSRF Token against login id.
		if(csrfToken != null)
			isValidCSRF=util.validateANTICSRFTOKEN(reqObj.getLoginId().split("\\|")[1],csrfToken);
		if(!isValidCSRF){
			iLogger.info("DeleteUserService :: deleteUser ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}
		
		//DF20180521-KO369761:Sending logged in  user id along with deleting user id for security audit check.
		if(reqObj.getLoginId() != null){
			if(reqObj.getLoginId().split("\\|").length > 1){
				encodedUserId = reqObj.getLoginId().split("\\|")[1];
				reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]);
				String UserID=new CommonUtil().getUserId(encodedUserId);
				if(UserID == null){
					throw new CustomFault("Invalid Login ID");
				}
			}
		}
		
		/*//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());*/
		
		String message= new DeleteUserImpl().deleteUser(reqObj);
		//CR271.n
		new UserDeactivationProducer(reqObj.getLoginId());
		iLogger.info("---- Webservice Output ------");
		iLogger.info("status:"+message+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:DeleteUserService~executionTime:"+(endTime - startTime)+"~"+""+"~"+message);
		return message;
	}


}
