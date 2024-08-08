package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.AssetType;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "AssetTypeSample")
public class AssetTypeSample 
{
	@WebMethod(operationName = "processEAassetTypeData", action = "processEAassetTypeData")
	  public String processEAassetTypeData(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Asset Type");
		AssetType assetTypeObj = new AssetType();
		assetTypeObj.handleAssetTypeDetails();
		
		iLogger.info("Exiting Sample WebService  - Asset Type");
		return "SUCCESS";
	}
}
