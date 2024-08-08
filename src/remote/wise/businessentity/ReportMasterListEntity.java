package remote.wise.businessentity;

public class ReportMasterListEntity extends BaseBusinessEntity{
	
	private int reportId;
	private String reportName;
	private String reportDescription;
	public ReportMasterListEntity() {};
	public ReportMasterListEntity(int reportId)
	{
		super.key = new Integer(reportId);
		ReportMasterListEntity e=(ReportMasterListEntity) read(this);
		
		if(e!=null)
		{
			setReportId(e.getReportId());
			setReportName(e.getReportName());
			setReportDescription(e.getReportDescription());
		}
	}
	
	
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
	
	

}
