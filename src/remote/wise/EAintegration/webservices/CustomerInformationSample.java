package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.CustomerInfo;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "CustomerInformationSample")
public class CustomerInformationSample 
{
	@WebMethod(operationName = "processEACustomerData", action = "processEACustomerData")
	  public String processEACustomerData(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Customer Information");
		CustomerInfo customerInfoObj = new CustomerInfo();
		customerInfoObj.handleCustomerInfo();
		
		iLogger.info("Exiting Sample WebService  - Customer Information");
		return "SUCCESS";
	}
}
