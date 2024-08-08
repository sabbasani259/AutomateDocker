/**
 * 
 */
package remote.wise.service.implementation;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.CustomerCodeForDealerCodeReqContract;
import remote.wise.service.datacontract.CustomerCodeForDealerCodeRespContract;
import remote.wise.service.datacontract.CustomerForDealerReqContract;
import remote.wise.service.datacontract.CustomerForDealerRespContract;

/**
 * @author roopn5
 *
 */
public class CustomersCodeUnderDealerCodeImpl {
	
	/**
	 * method to get the customers under a dealer
	 * @param reqObj
	 * @return CustomerForDealerRespContract
	 * @throws CustomFault
	 * 
	 * 
	 */
         List<String> dealerTCode;
	
	LinkedHashMap<String, String> customerMap;
	public CustomerCodeForDealerCodeRespContract getCustomerForDealer(CustomerCodeForDealerCodeReqContract reqObj) throws CustomFault{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		
		
		for(int i=0;i<reqObj.getDealerCode().size();i++){
			
			if( reqObj.getDealerCode().get(i)==null){
				bLogger.error("Provide valid Dealer code.");
	    		throw new CustomFault("Provide valid Dealer code.");    		
	    	}	
		}
		
		CustomerCodeForDealerCodeRespContract response=new CustomerCodeForDealerCodeRespContract();
		UserDetailsBO bo=new UserDetailsBO();
		CustomersCodeUnderDealerCodeImpl impl=bo.getCustomersCodeForDealer(reqObj.getDealerCode());
		response.setDealerTCode(impl.getDealerTCode());
		response.setCustomerMap(impl.getCustomerMap());
		return response;
	}
	
	/**
	 * @return the dealerTCode
	 */
	public List<String> getDealerTCode() {
		return dealerTCode;
	}

	/**
	 * @param dealerTCode the dealerTCode to set
	 */
	public void setDealerTCode(List<String> dealerTCode) {
		this.dealerTCode = dealerTCode;
	}

	/**
	 * @return the customerMap
	 */
	public LinkedHashMap<String, String> getCustomerMap() {
		return customerMap;
	}
	/**
	 * @param customerMap the customerMap to set
	 */
	public void setCustomerMap(LinkedHashMap<String, String> customerMap) {
		this.customerMap = customerMap;
	}
	

}
