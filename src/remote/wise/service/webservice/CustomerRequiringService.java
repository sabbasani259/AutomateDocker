package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.CustomerRequiringReqContract;
import remote.wise.service.datacontract.CustomerRequiringRespContract;
import remote.wise.service.implementation.CustomerRequiringImpl;
//import remote.wise.util.WiseLogger;
/**
 *  WebService class to get Customers and related  assets that pending for Service under given Dealer 
 * @author jgupta41
 *
 */


@WebService(name = "CustomerRequiringService")
public class CustomerRequiringService {
	
	/**
	 * This method gets Customers and related  assets that pending for Service under given Dealer Tenancy ID
	 * @param reqObj:Get all Customers and related  assets pending for Service under given Dealer Tenancy ID
	 * @return respObj:Returns List of Customers and related  assets that pending for Service
	 * @throws CustomFault
	 */

	@WebMethod(operationName = "GetCustomerRequiring", action = "GetCustomerRequiring")
	public List<CustomerRequiringRespContract> getCustomerRequiring
	(@WebParam(name="reqObj") CustomerRequiringReqContract reqObj) 
	throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("CustomerRequiringService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("LoginTenancy_ID:"+reqObj.getLoginTenancy_ID()+"");

		List<CustomerRequiringRespContract> respObj=new CustomerRequiringImpl().getCustomerRequiring(reqObj);
		iLogger.info("----- Webservice Output-----");
//		for(int i=0; i<respObj.size();i++){
//			iLogger.info("CustomerName:"+respObj.get(i).getCustomerName()+","+"DealerName:"+respObj.get(i).getDealerName()+","+"CustomerContactNumber:"+respObj.get(i).getCustomerContactNumber()+","+"MachineServiceDetails:"+respObj.get(i).getMachineServiceDetails()+","+"NumberOfMachineDueForService:"+respObj.get(i).getNumberOfMachineDueForService()+"");	
//			for(int j=0;j<respObj.get(i).getMachineServiceDetails().size();j++){
//				infoLogger.info("MachineServiceDetails:"+respObj.get(j).getMachineServiceDetails()+"");
//			}
//		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:CustomerRequiringService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respObj;	
	}
}
