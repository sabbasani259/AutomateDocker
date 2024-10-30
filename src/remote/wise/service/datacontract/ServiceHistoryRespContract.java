package remote.wise.service.datacontract;

public class ServiceHistoryRespContract {

	private String serviceName;
	private String scheduleName;
	private String jobCardNumber;
	private String serviceDate;
	//DF20180423:IM20018382 - Adding additional field jobCardDetails.
	private String jobCardDetails;
	// CR488.sn 
		private String completedBy;
		// CR488.en
		public String getCompletedBy() {
			return completedBy;
		}
		public void setCompletedBy(String completedBy) {
			this.completedBy = completedBy;
		}
	//DF20180423:IM20018382 - Validating additional field jobCardDetails.
	public String getJobCardDetails() {
		return jobCardDetails;
	}
	public void setJobCardDetails(String jobCardDetails) {
		this.jobCardDetails = jobCardDetails;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getScheduleName() {
		return scheduleName;
	}
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}
	public String getJobCardNumber() {
		return jobCardNumber;
	}
	public void setJobCardNumber(String jobCardNumber) {
		this.jobCardNumber = jobCardNumber;
	}
	public String getServiceDate() {
		return serviceDate;
	}
	public void setServiceDate(String serviceDate) {
		this.serviceDate = serviceDate;
	}
	
	
}
