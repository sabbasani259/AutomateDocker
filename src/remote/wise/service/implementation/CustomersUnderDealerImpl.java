package remote.wise.service.implementation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;
import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.CustomerForDealerReqContract;
import remote.wise.service.datacontract.CustomerForDealerRespContract;
//import remote.wise.util.WiseLogger;

public class CustomersUnderDealerImpl {
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("CustomersUnderDealerImpl:","businessError");
	
	//DF23/03/2015 for alphabetical order of customer name
	LinkedHashMap<Integer, String> customerMap;
	int dealerID;
	

	
	
	public LinkedHashMap<Integer, String> getCustomerMap() {
		return customerMap;
	}

	public void setCustomerMap(LinkedHashMap<Integer, String> customerMap) {
		this.customerMap = customerMap;
	}

	public int getDealerID() {
		return dealerID;
	}

	public void setDealerID(int dealerID) {
		this.dealerID = dealerID;
	}
	
	/**
	 * method to get the customers under a dealer
	 * @param reqObj
	 * @return CustomerForDealerRespContract
	 * @throws CustomFault
	 */
	public CustomerForDealerRespContract getCustomerForDealer(CustomerForDealerReqContract reqObj) throws CustomFault{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		if( reqObj.getDealerTenancyId()==0){
			bLogger.error("Provide Dealer tenancy ID.");
    		throw new CustomFault("Provide Dealer tenancy ID.");    		
    	}
		
		CustomerForDealerRespContract response=new CustomerForDealerRespContract();
		UserDetailsBO bo=new UserDetailsBO();
		CustomersUnderDealerImpl impl=bo.getCustomersForDealer(reqObj.getDealerTenancyId());
		response.setDealerTenancyId(impl.getDealerID());
		response.setCustomerMap(impl.getCustomerMap());
		return response;
	}
	
	public LinkedHashMap<Integer, String> getCustomerForDealerForRest(int dealerTenancyId) throws CustomFault{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		if( dealerTenancyId==0){
			bLogger.error("Provide Dealer tenancy ID.");
    		throw new CustomFault("Provide Dealer tenancy ID.");    		
    	}
		
		CustomerForDealerRespContract response=new CustomerForDealerRespContract();
		UserDetailsBO bo=new UserDetailsBO();
		CustomersUnderDealerImpl impl=bo.getCustomersForDealerForRest(dealerTenancyId);
		return impl.getCustomerMap();
	}
}
