package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.CustomerDetailsRESTBO;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.CustomerDetailsRespContract;

public class CustomerDetailsRESTImpl {

	public List<CustomerDetailsRespContract> getCustomerDetails(String customerCode){

		List<CustomerDetailsRespContract>customerList = new ArrayList<CustomerDetailsRespContract>();
		Logger bLogger = BusinessErrorLoggerClass.logger;

		if(customerCode.isEmpty()||customerCode.equals("0")){
			bLogger.error("CustomerDetailsRESTImpl:getCustomerDetails:Mandatory parameter customerCode is 0");
			return null;
		}
		customerList = new CustomerDetailsRESTBO().getCustomerDetails(customerCode);
		return customerList;
	}
}
