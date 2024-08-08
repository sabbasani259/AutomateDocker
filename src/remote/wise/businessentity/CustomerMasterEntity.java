package remote.wise.businessentity;

public class CustomerMasterEntity {

	private String customerCode;
	private String customerdetail;
	private int processFlag;
	//DF20170616 - SU334449 - Adding new field variable for dealerECCCode in Customer Master table
	private String dealerCode;

	/**
	 * @return the dealerCode
	 */
	public String getDealerCode() {
		return dealerCode;
	}
	public void setDealerCode(String dealerCode) {
		this.dealerCode = dealerCode;
	}
	/**
	 * @return the customerCode
	 */
	public String getCustomerCode() {
		return customerCode;
	}
	/**
	 * @param customerCode the customerCode to set
	 */
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	
	public String getCustomerdetail() {
		return customerdetail;
	}
	public void setCustomerdetail(String customerdetail) {
		this.customerdetail = customerdetail;
	}
	/**
	 * @return the processFlag
	 */
	public int getProcessFlag() {
		return processFlag;
	}
	/**
	 * @param processFlag the processFlag to set
	 */
	public void setProcessFlag(int processFlag) {
		this.processFlag = processFlag;
	}
}
