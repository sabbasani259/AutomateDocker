package remote.wise.service.datacontract;

import java.util.HashMap;

public class SavedReportRespContract {
	private int reportId;
	private String reportName;
	private String reportDescription;
	private String contactId;
    HashMap<String,String> filterNameFieldValue=new HashMap<String,String>();
    
    
    
	public int getReportId() {
		return reportId;
	}
	public void setReportId(int reportId) {
		this.reportId = reportId;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getReportDescription() {
		return reportDescription;
	}
	public void setReportDescription(String reportDescription) {
		this.reportDescription = reportDescription;
	}
	public String getContactId() {
		return contactId;
	}
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	public HashMap<String, String> getFilterNameFieldValue() {
		return filterNameFieldValue;
	}
	public void setFilterNameFieldValue(HashMap<String, String> filterNameFieldValue) {
		this.filterNameFieldValue = filterNameFieldValue;
	}
	
	
	

}
