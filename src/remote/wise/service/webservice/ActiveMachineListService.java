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
import remote.wise.service.datacontract.MachineCommunicationInputContract;
import remote.wise.service.datacontract.MachineCommunicationOutputContract;
import remote.wise.service.implementation.ActiveMachineListImpl;
import remote.wise.service.implementation.MachineCommunicationReportImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "ActiveMachineListService")
public class ActiveMachineListService {
	@WebMethod(operationName = "GetActiveMachineList", action = "GetActiveMachineList")

	
	public List<ActiveMachineListOutputContract> getActiveMachineList(@WebParam(name="reqObj") ActiveMachineListInputContract reqObj)throws CustomFault{
//WiseLogger infoLogger = WiseLogger.getLogger("ActiveMachineListService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("ToDate:"+reqObj.getToDate());
		List<ActiveMachineListOutputContract> respObj = new ActiveMachineListImpl().getActiveMachine(reqObj);
		for(int i=0; i<respObj.size(); i++){
			iLogger.info("serialNumber:"+respObj.get(i).getSerialNumber());
			iLogger.info("Model:"+respObj.get(i).getModel());
			iLogger.info("Profile:"+respObj.get(i).getProfile());
			iLogger.info("InstallDate:"+respObj.get(i).getInstallDate());
			iLogger.info("RollOffDate:"+respObj.get(i).getRollOffDate());
			
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:ActiveMachineListService~executionTime:"+(endTime - startTime)+"~"+""+"~");
		return respObj;
		
		
	}


}
