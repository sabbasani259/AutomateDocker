package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.AssetPersonality;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "AssetPersonalitySample")
public class AssetPersonalitySample 
{
	@WebMethod(operationName = "processEAassetPersData", action = "processEAassetPersData")
	  public String processEAassetPersData(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Asset Personality");
		AssetPersonality assetPersonalityObj = new AssetPersonality();
		assetPersonalityObj.handleAssetPersonality();
		
		iLogger.info("Exiting Sample WebService  - Asset Personality");
		return "SUCCESS";
	}
}
