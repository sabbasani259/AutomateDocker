package remote.wise.service.datacontract;

public class NonDBMSCustomerEntity {

	private String customerCode;
	private String dealerECCCode;

	public NonDBMSCustomerEntity(){
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getDealerECCCode() {
		return dealerECCCode;
	}

	public void setDealerECCCode(String dealerECCCode) {
		this.dealerECCCode = dealerECCCode;
	}
}
