package remote.wise.service.webservice;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;


import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.FotaAuthenticationReqContract;
import remote.wise.service.datacontract.FotaAuthenticationRespContract;

import remote.wise.service.implementation.FotaAuthenticationImpl;
//import remote.wise.util.WiseLogger;


@WebService(name = "FotaAuthenticationService")
public class FotaAuthenticationService {
	
	@WebMethod(operationName = "FotaAuthentication", action = "FotaAuthentication")
	public FotaAuthenticationRespContract FotaAuthentication(@WebParam(name="reqObj") FotaAuthenticationReqContract reqObj ) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("FotaAuthenticationService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("Entered : FotaAuthenticationService ");
		iLogger.info("****** INPUTS**************\n");
		iLogger.info("Session ID : "+reqObj.getFotasessionId()+"\n");
		iLogger.info("IMEI No. : "+reqObj.getFotaimeiNumber()+"\n");
		iLogger.info("***************************\n");
		FotaAuthenticationRespContract respObj = new FotaAuthenticationImpl().getFotaAuthentication(reqObj);
		iLogger.info("****** OUTPUTS **************\n");
		iLogger.info("Session ID : "+respObj.getSessionId()+"\n");
		iLogger.info("Status     : "+respObj.getStatus()+"\n");
		iLogger.info("***************************\n");
		iLogger.info("Exiting : FotaAuthenticationService ");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:FotaAuthenticationService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respObj;
	}


}
