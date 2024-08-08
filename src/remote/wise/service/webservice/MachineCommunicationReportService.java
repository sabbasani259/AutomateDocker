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
import remote.wise.service.datacontract.MachineCommunicationInputContract;
import remote.wise.service.datacontract.MachineCommunicationOutputContract;
import remote.wise.service.datacontract.MachineSMSInputContract;
import remote.wise.service.datacontract.MachineSMSOutputContract;
import remote.wise.service.implementation.MachineCommunicationReportImpl;
import remote.wise.service.implementation.MachineSMSDetailsImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "MachineCommunicationReportService")
public class MachineCommunicationReportService {
	
	@WebMethod(operationName = "GetMachineCommunicationReport", action = "GetMachineCommunicationReport")
	public List<MachineCommunicationOutputContract> getMachineCommunicationReport(@WebParam(name="reqObj") MachineCommunicationInputContract reqObj)throws CustomFault{
//WiseLogger infoLogger = WiseLogger.getLogger("MachineCommunicationReportService:","info");
		Logger iLogger = InfoLoggerClass.logger;	
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("FromDate:"+reqObj.getFromDate()+","+"ToDate:"+reqObj.getToDate());
		List<MachineCommunicationOutputContract> respObj = new MachineCommunicationReportImpl().getMachineCommunication(reqObj);
		for(int i=0; i<respObj.size(); i++){
			iLogger.info("serialNumber:"+respObj.get(i).getSerialNumber());
			iLogger.info("AlertType:"+respObj.get(i).getAlertType());
			iLogger.info("DateTime:"+respObj.get(i).getDateTime());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MachineDueOverdueReportService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respObj;
		
		
	}

}
