package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.SMSAlertDetailReqContract;
import remote.wise.service.datacontract.SMSAlertsRespContract;
import remote.wise.service.implementation.SMSAlertsImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "SMSAlertsDetailService")
public class SMSAlertsDetailService 
{
	@WebMethod(operationName = "getSMSAlertDetails", action = "getSMSAlertDetails")
	public List<SMSAlertsRespContract> getSMSAlertDetails(@WebParam(name="reqObj" ) SMSAlertDetailReqContract reqObj)
	{
		//WiseLogger infoLogger = WiseLogger.getLogger("SMSAlertsDetailService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("SerialNumber: "+reqObj.getSerialNumber()+",  "+"fromDate:"+reqObj.getFromDate()+",   " +
				"toDate:"+reqObj.getToDate());
		
		List<SMSAlertsRespContract> response = new SMSAlertsImpl().getSMSAlertDetails(reqObj);
		
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("serialNumber:"+response.get(i).getSerialNumber()+",  "+"cmhr:"+response.get(i).getCmhr()+",   " +
					"smsReceivedTime:"+response.get(i).getSmsReceivedTime()+",  "+"failureReason:"+response.get(i).getFailureReason()+",  "+
					"towawayStatus:"+response.get(i).getTowawayStatus()+",  " +
					"highCoolantTempStatus: "+response.get(i).getHighCoolantTempStatus()+",  " + 
					"lowEngineOilPressureStatus:"+response.get(i).getLowEngineOilPressureStatus()+",   " +
					"waterInFuelStatus: "+response.get(i).getWaterInFuelStatus()+",  "+"blockedAirFilterStatus:"+response.get(i).getBlockedAirFilterStatus());
		}
		
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:SMSAlertsDetailService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		
		return response;
	}
}
