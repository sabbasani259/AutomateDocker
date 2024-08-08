package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.RDealerInfo;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "RDealerInformationSample")
public class RDealerInformationSample 
{
	@WebMethod(operationName = "reprocessEADealerData", action = "reprocessEADealerData")
	  public String reprocessEADealerData(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Reprocess Dealer Information");
		RDealerInfo reprocessDelaerInfoObj = new RDealerInfo();
		reprocessDelaerInfoObj.reprocessDealerInfo();
		
		iLogger.info("Exiting Sample WebService - Reprocess Dealer Information");
		return "SUCCESS";
	}
}
