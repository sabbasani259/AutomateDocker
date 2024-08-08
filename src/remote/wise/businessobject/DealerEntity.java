/**
 * 
 */
package remote.wise.businessobject;

import java.util.List;

/**
 * @author kprabhu5
 *
 */
public class DealerEntity {

	int dealerId;
	String dealerName;
	List<CustomerEntity> customersList;
	
	
	/**
	 * @return the customersList
	 */
	public List<CustomerEntity> getCustomersList() {
		return customersList;
	}
	/**
	 * @param customersList the customersList to set
	 */
	public void setCustomersList(List<CustomerEntity> customersList) {
		this.customersList = customersList;
	}
	/**
	 * @return the dealerId
	 */
	public int getDealerId() {
		return dealerId;
	}
	/**
	 * @param dealerId the dealerId to set
	 */
	public void setDealerId(int dealerId) {
		this.dealerId = dealerId;
	}
	/**
	 * @return the dealerName
	 */
	public String getDealerName() {
		return dealerName;
	}
	/**
	 * @param dealerName the dealerName to set
	 */
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
}
