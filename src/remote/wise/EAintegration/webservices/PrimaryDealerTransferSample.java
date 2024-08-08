package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.PrimaryDealerTransfer;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "PrimaryDealerTransferSample")
public class PrimaryDealerTransferSample 
{
	@WebMethod(operationName = "processEADealerTransfer", action = "processEADealerTransfer")
	  public String processEADealerTransfer(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Primary Dealer Transfer");
		PrimaryDealerTransfer dealerTransferObj = new PrimaryDealerTransfer();
		dealerTransferObj.handlePrimaryDealerTransfer();
		
		iLogger.info("Exiting Sample WebService  - Primary Dealer Transfer");
		return "SUCCESS";
	}
}
