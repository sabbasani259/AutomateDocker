package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.DealerMapping;
import remote.wise.log.InfoLogging.InfoLoggerClass;


@WebService(name = "DealerMappingSample")
public class DealerMappingSample 
{
	@WebMethod(operationName = "processEAdealerMappingData", action = "processEAdealerMappingData")
	  public String processEAdealerMappingData(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Dealer Mapping");
		DealerMapping dealerMappingObj = new DealerMapping();
		dealerMappingObj.handleDealerMappingDetails();
		
		iLogger.info("Exiting Sample WebService  - Dealer Mapping");
		return "SUCCESS";
	}
}
