package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.SaleFromD2C;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "AssetSaleFromD2CSample")
public class AssetSaleFromD2CSample 
{
	@WebMethod(operationName = "processEAsaleFromD2C", action = "processEAsaleFromD2C")
	  public String processEAsaleFromD2C(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Asset Sale from D2C");
		SaleFromD2C saleFromD2CObj = new SaleFromD2C();
		saleFromD2CObj.handleSaleFromD2C();
		
		iLogger.info("Exiting Sample WebService  - Asset Sale from D2C");
		return "SUCCESS";
	}
}
