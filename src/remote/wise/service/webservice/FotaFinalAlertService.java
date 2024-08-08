package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.FotaFinalAlertReqContract;
import remote.wise.service.datacontract.FotaFinalAlertRespContract;
import remote.wise.service.implementation.FotaFinalAlertImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "FotaFinalAlertService")
public class FotaFinalAlertService {
	
	@WebMethod(operationName = "FotaFinalAlert", action = "FotaFinalAlert")
	public FotaFinalAlertRespContract FotaFinalAlert(@WebParam(name="reqObj") FotaFinalAlertReqContract reqObj ) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("FotaFinalAlertService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("Entered : FotaFinalAlertService ");
		iLogger.info("****** INPUTS**************\n");
		iLogger.info("IMEI NO. : "+reqObj.getImei()+"\n");
		iLogger.info("Session ID : "+reqObj.getSessionId()+"\n");
		iLogger.info("Status     : "+reqObj.getStatus()+"\n");
		iLogger.info("***************************\n");
		FotaFinalAlertRespContract respObj = new FotaFinalAlertImpl().getFotaFinalAlert(reqObj);
		iLogger.info("****** OUTPUTS **************\n");
		iLogger.info("Session ID : "+respObj.getSessionId()+"\n");
		iLogger.info("Status     : "+respObj.getStatus()+"\n");
		iLogger.info("***************************\n");
		iLogger.info("Exiting : FotaFinalAlertService ");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:FotaFinalAlertService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respObj;
	}
}
