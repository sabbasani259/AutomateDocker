package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.AssetInstallationDetails;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "AssetInstallationDetailsSample")
public class AssetInstallationDetailsSample 
{
	@WebMethod(operationName = "processEAassetInstData", action = "processEAassetInstData")
	  public String processEAassetInstData(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Asset Installation Service");
		AssetInstallationDetails assetInstallationObj = new AssetInstallationDetails();
		assetInstallationObj.handleAssetInstallation();
		
		iLogger.info("Exiting Sample WebService  - Asset Installation Service");
		return "SUCCESS";
	}
}
