package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.RAssetType;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "RAssetTypeSample")
public class RAssetTypeSample 
{
	@WebMethod(operationName = "reprocessEAassetTypeData", action = "reprocessEAassetTypeData")
	  public String reprocessEAassetTypeData(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Reprocess Asset Type");
		RAssetType reprocessAssetTypeObj = new RAssetType();
		reprocessAssetTypeObj.reprocessAssetType();
		
		iLogger.info("Exiting Sample WebService - Reprocess Asset Type");
		return "SUCCESS";
	}
}
