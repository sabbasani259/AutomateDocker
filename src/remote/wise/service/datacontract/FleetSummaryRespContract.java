package remote.wise.service.datacontract;

public class FleetSummaryRespContract {
	
	private double totalIdleHours;
	private double totalWorkingHours;
//	Keerthi : 19/12/2013 : ID: 1828 
	private double totalOffHours;
	//DF20190308:Abhishek:To display date in FleetUtilization chart in UI
	private String resp_Date;
	
	public String getResp_Date() {
		return resp_Date;
	}
	public void setResp_Date(String resp_Date) {
		this.resp_Date = resp_Date;
	}
	public double getTotalIdleHours() {
		return totalIdleHours;
	}
	public void setTotalIdleHours(double totalIdleHours) {
		this.totalIdleHours = totalIdleHours;
	}
	public double getTotalWorkingHours() {
		return totalWorkingHours;
	}
	public void setTotalWorkingHours(double totalWorkingHours) {
		this.totalWorkingHours = totalWorkingHours;
	}
	public double getTotalOffHours() {
		return totalOffHours;
	}
	public void setTotalOffHours(double totalOffHours) {
		this.totalOffHours = totalOffHours;
	}
}
