package remote.wise.service.datacontract;

/**
 * @author kprabhu5
 *
 */
public class ReportMailSubscriptionListRespContract {

	String contactId;
	int reportId;
	String reportName;
	String primaryEmailId;
	int tenancyId;
	int tenancyTypeId;
	
	public int getTenancyTypeId() {
		return tenancyTypeId;
	}
	public void setTenancyTypeId(int tenancyTypeId) {
		this.tenancyTypeId = tenancyTypeId;
	}
	public int getReportId() {
		return reportId;
	}
	public void setReportId(int reportId) {
		this.reportId = reportId;
	}
	public String getContactId() {
		return contactId;
	}
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}	
	
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getPrimaryEmailId() {
		return primaryEmailId;
	}
	public void setPrimaryEmailId(String primaryEmailId) {
		this.primaryEmailId = primaryEmailId;
	}
	public int getTenancyId() {
		return tenancyId;
	}
	public void setTenancyId(int tenancyId) {
		this.tenancyId = tenancyId;
	}
	
}
