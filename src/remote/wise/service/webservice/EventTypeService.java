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
import remote.wise.service.datacontract.EventTypeRespContract;
import remote.wise.service.implementation.EventTypeImpl;

/**
 * @author roopn5
 *
 */
@WebService(name = "EventTypeService")
public class EventTypeService {

	
	@WebMethod(operationName = "GetEventTypes", action = "GetEventTypes")
	public List<EventTypeRespContract> GetEventTypes() throws CustomFault{

		
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		
		List<EventTypeRespContract> respObj = new EventTypeImpl().getEventTypes();
		iLogger.info("<-----Webservice Output----->");
		for(int i=0;i<respObj.size();i++){
			iLogger.info(i+"   ROW");
			iLogger.info("EventTypeId:"+respObj.get(i).getEventTypeId()+" , EventTypeName:"+respObj.get(i).getEventTypeName()+"");
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:EventTypeService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respObj;
	}
}
