package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.RDealerMapping;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "RDealerMappingSample")
public class RDealerMappingSample 
{
	@WebMethod(operationName = "reprocessEADealerMappingData", action = "reprocessEADealerMappingData")
	  public String reprocessEADealerMappingData(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Reprocess Dealer Mapping");
		RDealerMapping reprocessDealerMapObj = new RDealerMapping();
		reprocessDealerMapObj.reprocessDealerMapping();
		
		iLogger.info("Exiting Sample WebService - Reprocess Dealer Mapping");
		return "SUCCESS";
	}
}
