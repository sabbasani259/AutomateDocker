package remote.wise.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AlertReprocessImpl;

@WebService(name = "AlertReprocessService")
public class AlertReprocessService 
{
	@WebMethod(operationName = "reprocessAlerts", action = "reprocessAlerts")
	public String reprocessAlerts(@WebParam(name="serialNumber")String serialNumber, @WebParam(name="createdTimeStartDate" )String createdTimeStartDate,
			@WebParam(name="createdTimeEndDate" )String createdTimeEndDate)
	{
		String status="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		
		iLogger.info("---- Webservice Input ------");
		iLogger.info("SerialNumber: "+serialNumber+", createdTimeStartDate: "+createdTimeStartDate+", createdTimeEndDate: "+createdTimeEndDate);
		
		status = new AlertReprocessImpl().reprocessAlerts(serialNumber, createdTimeStartDate, createdTimeEndDate);
		
		iLogger.info("---- Webservice Output ------");
		iLogger.info("Status: "+status);
		
		return status;
	}
}
