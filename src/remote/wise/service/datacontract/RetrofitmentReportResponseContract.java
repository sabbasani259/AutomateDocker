package remote.wise.service.datacontract;

/**
 * @author Dhiraj Kumar
 * @since 20230505:CR352
 * CR352 : 20230505 : Dhiraj K : Retrofitment Changes
 */
public class RetrofitmentReportResponseContract {

	private String vin;
	private String retrofitStartDate;
	private String retrofitEndDate;
	private String customerMobileNo;
	private String customerName;
	private String dealerName;
	private String profile;
	private String model;
	private String zone;
	
	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getRetrofitStartDate() {
		return retrofitStartDate;
	}

	public void setRetrofitStartDate(String retrofitStartDate) {
		this.retrofitStartDate = retrofitStartDate;
	}

	public String getRetrofitEndDate() {
		return retrofitEndDate;
	}

	public void setRetrofitEndDate(String retrofitEndDate) {
		this.retrofitEndDate = retrofitEndDate;
	}

	public String getCustomerMobileNo() {
		return customerMobileNo;
	}

	public void setCustomerMobileNo(String customerMobileNo) {
		this.customerMobileNo = customerMobileNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

}
