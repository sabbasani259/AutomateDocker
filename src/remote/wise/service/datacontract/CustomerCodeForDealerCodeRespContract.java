/**
 * 
 */
package remote.wise.service.datacontract;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author roopn5
 *
 */
public class CustomerCodeForDealerCodeRespContract {
	
	List<String> dealerTCode;
	
	LinkedHashMap<String, String> customerMap;

	

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
