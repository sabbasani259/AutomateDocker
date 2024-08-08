package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.RCustomerInfo;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "RCustomerInformationSample")
public class RCustomerInformationSample 
{
	@WebMethod(operationName = "reprocessEACustomerData", action = "reprocessEACustomerData")
	  public String reprocessEACustomerData(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Reprocess Customer Information");
		RCustomerInfo reprocessCustomerInfoObj = new RCustomerInfo();
		reprocessCustomerInfoObj.reprocessCustomerInfo();
		
		iLogger.info("Exiting Sample WebService - Reprocess Customer Information");
		return "SUCCESS";
	}
}
