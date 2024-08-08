/**
 * 
 */
package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.CustomerCodeForDealerCodeReqContract;
import remote.wise.service.datacontract.CustomerCodeForDealerCodeRespContract;
import remote.wise.service.implementation.CustomersCodeUnderDealerCodeImpl;

/**
 * @author roopn5
 *
 */
@WebService(name = "CustomersCodeUnderDealerCodeService")
public class CustomersCodeUnderDealerCodeService {

	@WebMethod(operationName = "GetCustomersCodeForDealer", action = "GetCustomersCodeForDealer")	
	public CustomerCodeForDealerCodeRespContract GetCustomersCodeForDealer(@WebParam(name="reqObj") CustomerCodeForDealerCodeReqContract reqObj)throws CustomFault{
		
		Logger iLogger = InfoLoggerClass.logger;
	
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("----- Webservice Input-----");
		iLogger.info("DealerCode:"+reqObj.getDealerCode()+"");
		CustomerCodeForDealerCodeRespContract response= new CustomersCodeUnderDealerCodeImpl().getCustomerForDealer(reqObj);	
		iLogger.info("----- Webservice Output-----");
		iLogger.info("customerMap:"+response.getCustomerMap()+","+"dealerTenancyId:"+response.getDealerTCode()+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:CustomersCodeUnderDealerCodeService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}


}
