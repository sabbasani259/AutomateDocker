/**
 * 
 */
package remote.wise.service.datacontract;

import java.util.List;

/**
 * @author roopn5
 *
 */
public class CustomerCodeForDealerCodeReqContract {
	
	List<String> dealerCode;

	/**
	 * @return the dealerCode
	 */
	public List<String> getDealerCode() {
		return dealerCode;
	}

	/**
	 * @param dealerCode the dealerCode to set
	 */
	public void setDealerCode(List<String> dealerCode) {
		this.dealerCode = dealerCode;
	}

	

}
