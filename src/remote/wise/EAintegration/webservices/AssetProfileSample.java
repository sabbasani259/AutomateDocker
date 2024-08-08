package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.AssetGroup;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "AssetProfileSample")
public class AssetProfileSample 
{
	@WebMethod(operationName = "processEAassetProfileData", action = "processEAassetProfileData")
	  public String processEAassetProfileData(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Asset Profile");
		AssetGroup assetGroupObj = new AssetGroup();
		assetGroupObj.handleAssetGroupDetails();
		
		iLogger.info("Exiting Sample WebService  - Asset Profile");
		return "SUCCESS";
	}
}
