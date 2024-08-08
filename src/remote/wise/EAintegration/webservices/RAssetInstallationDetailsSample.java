package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.RAssetInstallationDetails;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "RAssetInstallationDetailsSample")
public class RAssetInstallationDetailsSample 
{
	@WebMethod(operationName = "reprocessEAassetInstData", action = "reprocessEAassetInstData")
	  public String reprocessEAassetInstData(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Reprocess Asset Installation Service");
		RAssetInstallationDetails reprocessAssetInstObj = new RAssetInstallationDetails();
		reprocessAssetInstObj.reprocessAssetInstallationDetails();
		
		iLogger.info("Exiting Sample WebService - Reprocess Asset Installation Service");
		return "SUCCESS";
	}
}
