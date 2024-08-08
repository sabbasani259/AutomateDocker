package remote.wise.service.datacontract;

import java.util.List;

public class ReportSubscriptionReqContract {
	private String contactId;
	private List<Integer> reportId;
	
	public String getContactId() {
		return contactId;
	}
	
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	public List<Integer> getReportId() {
		return reportId;
	}
	public void setReportId(List<Integer> reportId) {
		this.reportId = reportId;
	}

}
