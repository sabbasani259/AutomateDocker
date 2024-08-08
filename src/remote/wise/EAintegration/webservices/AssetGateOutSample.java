package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.AssetGateOut;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "AssetGateOutSample")
public class AssetGateOutSample 
{
	@WebMethod(operationName = "processEAassetGateOut", action = "processEAassetGateOut")
	  public String processEAassetGateOut(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Asset GateOut");
		AssetGateOut assetGateOutObj = new AssetGateOut();
		assetGateOutObj.handleAssetGateOut();
		
		iLogger.info("Exiting Sample WebService  - Asset GateOut");
		return "SUCCESS";
	}
}
