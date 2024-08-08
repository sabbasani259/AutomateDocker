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
import remote.wise.service.datacontract.ActiveMachineListInputContract;
import remote.wise.service.datacontract.ActiveMachineListOutputContract;
import remote.wise.service.datacontract.MachineBillingReportInputContract;
import remote.wise.service.datacontract.MachineBillingReportOutputContract;
import remote.wise.service.implementation.ActiveMachineListImpl;
import remote.wise.service.implementation.MachineBillingReportImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "MachineBillingReportService")
public class MachineBillingReportService {

	@WebMethod(operationName = "GetMachineBillingReport", action = "GetMachineBillingReport")

	
	public List<MachineBillingReportOutputContract> getMachineBillingReport(@WebParam(name="reqObj") MachineBillingReportInputContract reqObj)throws CustomFault{
//WiseLogger infoLogger = WiseLogger.getLogger("MachineBillingReportService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("FromDate:"+reqObj.getFromDate()+","+"ToDate:"+reqObj.getToDate());
		List<MachineBillingReportOutputContract> respObj = new MachineBillingReportImpl().getMachineReport(reqObj);
		for(int i=0; i<respObj.size(); i++){
			iLogger.info("serialNumber:"+respObj.get(i).getSerialNumber());
			iLogger.info("Model:"+respObj.get(i).getModel());
			iLogger.info("Profile:"+respObj.get(i).getProfile());
			iLogger.info("InstallDate:"+respObj.get(i).getInstallDate());
			iLogger.info("RollOffDate:"+respObj.get(i).getRollOffDate());
			iLogger.info("NewRollOffMachine:"+respObj.get(i).getNewRolledMachine());
			iLogger.info("TotalRollOffMachine:"+respObj.get(i).getActualMachineCount());
			iLogger.info("PreviousBilledCount:"+respObj.get(i).getPreviousBilledCount());
			iLogger.info("BilledMachineCount:"+respObj.get(i).getBillingCalculation());
			iLogger.info("InvoicedAmount:"+respObj.get(i).getInvoicedAmount());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MachineBillingReportService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respObj;
		
		
	}



}
