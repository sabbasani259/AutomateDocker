package remote.wise.service.webservice;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.UserAuthenticationReqContract;
import remote.wise.service.datacontract.UserAuthenticationRespContract;
import remote.wise.service.implementation.UserAuthenticationImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "UserAuthenticationService")
public class UserAuthenticationService {
		
	@WebMethod(operationName = "UserAuthentication", action = "UserAuthentication")
	public UserAuthenticationRespContract authenticateUserLogin( @WebParam(name="reqobj" ) UserAuthenticationReqContract userAuthContractObj) throws CustomFault, IOException
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("UserAuthenticationService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		// defetc Id1200: QA Server issue**
		
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		UserAuthenticationRespContract response= new UserAuthenticationImpl().authenticateUser(userAuthContractObj);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UserAuthenticationService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		
		return response;
	}	
}


