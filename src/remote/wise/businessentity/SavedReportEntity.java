package remote.wise.businessentity;

import java.io.Serializable;

public class SavedReportEntity extends BaseBusinessEntity implements Serializable{
	private ReportMasterListEntity reportId;
	private ContactEntity contactId;
	private String fieldId;
	private String fieldValue;
	
	
	public ReportMasterListEntity getReportId() {
		return reportId;
	}
	public void setReportId(ReportMasterListEntity reportId) {
		this.reportId = reportId;
	}
	public ContactEntity getContactId() {
		return contactId;
	}
	public void setContactId(ContactEntity contactId) {
		this.contactId = contactId;
	}
	public String getFieldId() {
		return fieldId;
	}
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	

}
