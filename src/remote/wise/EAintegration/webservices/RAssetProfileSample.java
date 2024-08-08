package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.RAssetGroup;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "RAssetProfileSample")
public class RAssetProfileSample 
{
	@WebMethod(operationName = "reprocessEAassetProfielData", action = "reprocessEAassetProfielData")
	  public String reprocessEAassetProfielData(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Reprocess Asset Profile");
		RAssetGroup reprocessAssetGroupObj = new RAssetGroup();
		reprocessAssetGroupObj.reprocessAssetGroup();
		
		iLogger.info("Exiting Sample WebService - Reprocess Asset Profile");
		return "SUCCESS";
	}
}
