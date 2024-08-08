package remote.wise.service.datacontract;

import java.util.List;

public class SavedReportReqContract {
	private List<Integer> reportId;
	private List<String> contactId;
	
	
	public List<Integer> getReportId() {
		return reportId;
	}
	public void setReportId(List<Integer> reportId) {
		this.reportId = reportId;
	}
	public List<String> getContactId() {
		return contactId;
	}
	public void setContactId(List<String> contactId) {
		this.contactId = contactId;
	}
}
