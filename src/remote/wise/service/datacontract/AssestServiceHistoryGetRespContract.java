package remote.wise.service.datacontract;

//CR373 - 20230125 - Prasad - Service History Api
public class AssestServiceHistoryGetRespContract {

	private String serviceDoneAt;
	private String serviceDone;
	private String jobCardNumber;
	private String comments;
	

	@Override
	public String toString() {
		return "AssestServiceHistoryGetRespContract [serviceDoneAt=" + serviceDoneAt + ", serviceDone=" + serviceDone
				+ ", jobCardNumber=" + jobCardNumber + ", comments=" + comments + "]";
	}

	public String getServiceDoneAt() {
		return serviceDoneAt;
	}

	public void setServiceDoneAt(String serviceDoneAt) {
		if (null != serviceDoneAt)
			this.serviceDoneAt = serviceDoneAt;
		else
			this.serviceDoneAt = "NA";
	}

	public String getServiceDone() {
		return serviceDone;
	}

	public void setServiceDone(String serviceDone) {
		if (null == serviceDone)
			this.serviceDone = "NA";
		else
			this.serviceDone = serviceDone;
	}

	public String getJobCardNumber() {
		return jobCardNumber;
	}

	public void setJobCardNumber(String jobCardNumber) {
		if (null == jobCardNumber)
			this.jobCardNumber = "NA";
		else
			
		this.jobCardNumber = jobCardNumber;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		if (null == comments)
			this.comments = "NA";
		else
			
		this.comments = comments;
	}


}
