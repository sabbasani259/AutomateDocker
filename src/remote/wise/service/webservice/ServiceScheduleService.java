package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;


import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.ServiceScheduleReqContract;
import remote.wise.service.datacontract.ServiceScheduleRespContract;
import remote.wise.service.implementation.ServiceDetailsImpl;
//import remote.wise.util.WiseLogger;
/**
 * 
 * @author tejgm
 * This method will get details on Scheduling services on machine
 */
@WebService(name = "ServiceScheduleService")
public class ServiceScheduleService {
		
	/**
	 * 
	 * @param reqObj
	 * @return serviceSchedule
	 * @throws CustomFault
	 */
	/*
	@WebMethod(operationName = "GetServiceScheduleService", action = "GetServiceScheduleService")	
	 public List<ServiceScheduleRespContract> getServiceScheduleDetails(@WebParam(name="reqObj") ServiceScheduleReqContract reqObj) throws CustomFault{
 	    Logger infoLogger = Logger.getLogger("infoLogger");
 	    Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
 	    List<ServiceScheduleRespContract> serviceSchedule = new ServiceDetailsImpl().getServiceScheduleDetails(reqObj);		
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		infoLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		infoLogger.info("Webservice Execution Time in ms:"+(endTime-startTime));
		return serviceSchedule;
	}	*/
}
