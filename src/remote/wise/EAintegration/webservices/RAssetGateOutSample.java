package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.RAssetGateOut;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "RAssetGateOutSample")
public class RAssetGateOutSample 
{
	@WebMethod(operationName = "reprocessEAassetGateOut", action = "reprocessEAassetGateOut")
	  public String reprocessEAassetGateOut(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Reprocess Asset GateOut");
		RAssetGateOut reprocessAssetGateOutObj = new RAssetGateOut();
		reprocessAssetGateOutObj.reprocessAssetGateOut();
		
		iLogger.info("Exiting Sample WebService - Reprocess Asset GateOut");
		return "SUCCESS";
	}
}
