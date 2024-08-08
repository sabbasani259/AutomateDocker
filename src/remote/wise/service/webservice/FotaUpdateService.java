package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.FotaUpdateReqContract;
import remote.wise.service.datacontract.FotaUpdateRespContract;

import remote.wise.service.implementation.FotaUpdateImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "FotaUpdateService")
public class FotaUpdateService {

	@WebMethod(operationName = "FotaUpdate", action = "FotaUpdate")
	public FotaUpdateRespContract FotaUpdate(@WebParam(name="reqObj") FotaUpdateReqContract reqObj ) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("FotaUpdateService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("Entered : FotaUpdateService ");
		iLogger.info("****** INPUTS**************\n");
		iLogger.info("Session ID : "+reqObj.getFotaSessionId()+"\n");
		iLogger.info("IMEI No.   : "+reqObj.getFotaimeiNo()+"\n");
		iLogger.info("Version ID : "+reqObj.getFotaVersionId()+"\n");
		iLogger.info("***************************\n");
		FotaUpdateRespContract respObj = new FotaUpdateImpl().FotaUpdate(reqObj);
		iLogger.info("****** OUTPUTS **************\n");
		iLogger.info("Session ID : "+respObj.getSessionId()+"\n");
		iLogger.info("Version ID : "+respObj.getVersionId()+"\n");
		iLogger.info("File Name  : "+respObj.getFile_path());
		iLogger.info("Status     : "+respObj.getStatus()+"\n");
		iLogger.info("***************************\n");
		iLogger.info("Exiting : FotaUpdateService ");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:FotaUpdateService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respObj;
	}

}

