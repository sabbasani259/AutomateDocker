package remote.wise.service.datacontract;

import java.util.List;

public class ReportSubscriptionRespContract {
	
	//private List<Integer> reportId;
	private int reportId;
	
	private String reportName;
	private String contactId;	
	
	private boolean weeklyReportSubscription;
     private boolean monthlyReportSubscription;
	
     public int getReportId() {
 		return reportId;
 	}
 	public void setReportId(int reportId) {
 		this.reportId = reportId;
 	}
	/*public List<Integer> getReportId() {
		return reportId;
	}
	public void setReportId(List<Integer> reportId) {
		this.reportId = reportId;
	}*/
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	public String getContactId() {
		return contactId;
	}
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	public boolean isWeeklyReportSubscription() {
		return weeklyReportSubscription;
	}
	public void setWeeklyReportSubscription(boolean weeklyReportSubscription) {
		this.weeklyReportSubscription = weeklyReportSubscription;
	}
	public boolean isMonthlyReportSubscription() {
		return monthlyReportSubscription;
	}
	public void setMonthlyReportSubscription(boolean monthlyReportSubscription) {
		this.monthlyReportSubscription = monthlyReportSubscription;
	}

}
