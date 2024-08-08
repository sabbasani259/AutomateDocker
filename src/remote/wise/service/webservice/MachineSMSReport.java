package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.LandmarkCategoryReqContract;
import remote.wise.service.datacontract.LandmarkCategoryRespContract;
import remote.wise.service.datacontract.MachineSMSInputContract;
import remote.wise.service.datacontract.MachineSMSOutputContract;
import remote.wise.service.datacontract.SecretQuestionsRespContract;
import remote.wise.service.implementation.LandmarkCategoryImpl;
import remote.wise.service.implementation.MachineSMSDetailsImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "MachineSMSReport")
public class MachineSMSReport {
	/**
	 * 
	 * @param reqObj is passed to get report on SMSService
	 * @return respObj as a result
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetMachineSMSDetails", action = "GetMachineSMSDetails")
	public List<MachineSMSOutputContract> getMachineSMSDetails(@WebParam(name="reqObj") MachineSMSInputContract reqObj)throws CustomFault{
//WiseLogger infoLogger = WiseLogger.getLogger("MachineSMSReport:","info");
		Logger iLogger = InfoLoggerClass.logger;	
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("FromDate:"+reqObj.getFromDate()+","+"ToDate:"+reqObj.getToDate());
		List<MachineSMSOutputContract> respObj = new MachineSMSDetailsImpl().getMachineSMSReport(reqObj);
		for(int i=0; i<respObj.size(); i++){
			iLogger.info("SMSCount:"+respObj.get(i).getSMSCount());
			iLogger.info("serialNumber:"+respObj.get(i).getSerialNumber());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MachineSMSReport~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respObj;
		
		
	}
}
