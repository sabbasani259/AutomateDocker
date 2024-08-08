package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AlertDetailsRespContract;
import remote.wise.service.implementation.AlertDetailsImpl;
import remote.wise.service.implementation.BillingCalculationImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "BillingCalculationService")
public class BillingCalculationService 
{
	@WebMethod(operationName = "SetBillingCalculation", action = "SetBillingCalculation")
	public String SetBillingCalculation(@WebParam(name="month") int month, @WebParam(name="year") int year) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("BillingCalculationService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate : "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Month: "+month+"  , Year:"+year);
		
		//DF20140403 - Rajani Nagaraju - Adding the capability to update billing data for previous months
		String response = new BillingCalculationImpl().setBillingCalculationImpl(month,year);
		
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response);
		
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate : "+endDate);
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:BillingCalculationService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		
		return response;

	}
}
