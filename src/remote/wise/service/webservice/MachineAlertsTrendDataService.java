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
import remote.wise.service.datacontract.MachineAlertsTrendDataReqContract;
import remote.wise.service.datacontract.MachineAlertsTrendDataRespContract;
import remote.wise.service.implementation.MachineAlertsTrendDataImpl;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;

@WebService(name = "MachineAlertsTrendDataService")
public class MachineAlertsTrendDataService {
		
	@WebMethod(operationName = "getMachineAlertsTrendData", action = "getMachineAlertsTrendData")	
	public List<MachineAlertsTrendDataRespContract> getMachineAlertsTrendData(@WebParam(name="reqObj") MachineAlertsTrendDataReqContract reqObj)throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("MachineAlertsTrendDataService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("fromDate:"+reqObj.getFromDate()+"toDate:"+reqObj.getToDate()+"Period:"+reqObj.getPeriod()+"tenancyIdList :"+reqObj.getTenancyIdList());
		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

		
		if(reqObj.getTenancyIdList()!=null && reqObj.getTenancyIdList().size()>0){
			reqObj.setTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getTenancyIdList()));
		}
		
		List<MachineAlertsTrendDataRespContract> response= new MachineAlertsTrendDataImpl().getMachineAlertsTrendData(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("Row "+i+" : ");
			iLogger.info("dealerData:"+response.get(i).getDealerData()+",  "+"zoneName:"+response.get(i).getZoneName());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MachineAlertsTrendDataService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;		
	}

	@WebMethod(operationName = "getMachineAlertsTrendDataForAllZones", action = "getMachineAlertsTrendDataForAllZones")
	public List<MachineAlertsTrendDataRespContract> getMachineAlertsTrendDataForAllZones(@WebParam(name="reqObj") MachineAlertsTrendDataReqContract reqObj) throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("MachineAlertsTrendDataService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		iLogger.info("---- Webservice Input ------");
		iLogger.info("fromDate:"+reqObj.getFromDate()+"toDate:"+reqObj.getToDate()+"Period:"+reqObj.getPeriod());
		long startTime = System.currentTimeMillis();
		List<MachineAlertsTrendDataRespContract> response= new MachineAlertsTrendDataImpl().getMachineAlertsForAllZones(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("Row "+i+" : ");
			iLogger.info("totalAlerts:"+response.get(i).getTotalAlerts()+",  "+"zoneName:"+response.get(i).getZoneName());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MachineAlertsTrendDataService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;		
	}

}


