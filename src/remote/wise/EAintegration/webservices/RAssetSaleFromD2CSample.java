package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.RSaleFromD2C;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "RAssetSaleFromD2CSample")
public class RAssetSaleFromD2CSample 
{
	@WebMethod(operationName = "reprocessEAsaleFromD2C", action = "reprocessEAsaleFromD2C")
	  public String reprocessEAsaleFromD2C(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Reprocess Asset Sale from D2C");
		RSaleFromD2C reprocessD2CsaleObj = new RSaleFromD2C();
		reprocessD2CsaleObj.reprocessSaleFromD2C();
		
		iLogger.info("Exiting Sample WebService - Reprocess Asset Sale from D2C");
		return "SUCCESS";
	}
}
