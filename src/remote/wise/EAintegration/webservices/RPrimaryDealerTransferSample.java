package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.RPrimaryDealerTransfer;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "RPrimaryDealerTransferSample")
public class RPrimaryDealerTransferSample 
{
	@WebMethod(operationName = "reprocessEADealerTransfer", action = "reprocessEADealerTransfer")
	  public String reprocessEADealerTransfer(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Reprocess Primary Dealer Transfer");
		RPrimaryDealerTransfer reprocessDealerTransferObj = new RPrimaryDealerTransfer();
		reprocessDealerTransferObj.reprocessPrimaryDealerTransfer();
		
		iLogger.info("Exiting Sample WebService - Reprocess Primary Dealer Transfer");
		return "SUCCESS";
	}
}
