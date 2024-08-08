package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.CustomerForDealerReqContract;
import remote.wise.service.datacontract.CustomerForDealerRespContract;
import remote.wise.service.implementation.CustomersUnderDealerImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "CustomersUnderDealerService")
public class CustomersUnderDealerService {
	
	
	@WebMethod(operationName = "GetCustomersForDealer", action = "GetCustomersForDealer")	
	public CustomerForDealerRespContract getCustomersForDealer(@WebParam(name="reqObj") CustomerForDealerReqContract reqObj)throws CustomFault{
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("CustomersUnderDealerService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("----- Webservice Input-----");
		iLogger.info("dealerTenancyId:"+reqObj.getDealerTenancyId()+"");
		CustomerForDealerRespContract response= new CustomersUnderDealerImpl().getCustomerForDealer(reqObj);	
		iLogger.info("----- Webservice Output-----");
		iLogger.info("customerMap:"+response.getCustomerMap()+","+"dealerTenancyId:"+response.getDealerTenancyId()+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:CustomersUnderDealerService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}
}
