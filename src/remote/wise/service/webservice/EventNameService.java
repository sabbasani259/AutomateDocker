/**
 * 
 */
package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.EventNameReqContract;
import remote.wise.service.datacontract.EventNameRespContract;
import remote.wise.service.implementation.EventNameImpl;

/**
 * @author roopn5
 *
 */
@WebService(name = "EventNameService")
public class EventNameService {

	
	@WebMethod(operationName = "GetReportEvents", action = "GetReportEvents")
	public List<EventNameRespContract> GetReportEvents(EventNameReqContract reqObj) throws CustomFault{

		
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		
		List<EventNameRespContract> respObj = new EventNameImpl().getEventNames(reqObj);
		iLogger.info("<-----Webservice Output----->");
		for(int i=0;i<respObj.size();i++){
			iLogger.info(i+"   ROW");
			iLogger.info("EventTyeId:"+respObj.get(i).getEventTypeId()+" , Event Id:"+respObj.get(i).getEventId()+", Event Name:"+respObj.get(i).getEventName()+"");
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:EventNameService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respObj;
	}
}
