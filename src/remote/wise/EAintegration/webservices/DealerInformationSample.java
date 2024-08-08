package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.DealerInfo;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "DealerInformationSample")
public class DealerInformationSample 
{
	@WebMethod(operationName = "processEADealerData", action = "processEADealerData")
	  public String processEADealerData(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Dealer Information");
		DealerInfo dealerInfoObj = new DealerInfo();
		dealerInfoObj.handleDealerInfo();
		
		iLogger.info("Exiting Sample WebService  - Dealer Information");
		return "SUCCESS";
	}
}
