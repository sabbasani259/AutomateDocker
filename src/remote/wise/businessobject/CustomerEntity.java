/**
 * 
 */
package remote.wise.businessobject;

/**
 * @author kprabhu5
 *
 */
public class CustomerEntity {
	
	int customerTenancyId;
	String customerTenancyName;
	
	/**
	 * @return the customerTenancyId
	 */
	public int getCustomerTenancyId() {
		return customerTenancyId;
	}
	/**
	 * @param customerTenancyId the customerTenancyId to set
	 */
	public void setCustomerTenancyId(int customerTenancyId) {
		this.customerTenancyId = customerTenancyId;
	}
	/**
	 * @return the customerTenancyName
	 */
	public String getCustomerTenancyName() {
		return customerTenancyName;
	}
	/**
	 * @param customerTenancyName the customerTenancyName to set
	 */
	public void setCustomerTenancyName(String customerTenancyName) {
		this.customerTenancyName = customerTenancyName;
	}
}
