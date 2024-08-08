package remote.wise.service.datacontract;

import java.util.List;

public class LandmarkActivityReportReqContract 
{
	
	//Custom Dates (fromDate,toDate) added by Juhi on 21-August-2013 
	private String fromDate;
	private String toDate;
	//added by smitha on 27th Aug 2013
	private List<Integer> machineGroupIdList; 
	
	public List<Integer> getMachineGroupIdList() {
		return machineGroupIdList;
	}
	public void setMachineGroupIdList(List<Integer> machineGroupIdList) {
		this.machineGroupIdList = machineGroupIdList;
	}
	//ended on 27th Aug 2013
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	private String loginId;
	private String Period;
	private List<Integer> LandmarkCategoryIDList;
	private List<Integer>	LandmarkIdList;
	private int loginTenancyId;
	
	
	public String getPeriod() {
		return Period;
	}
	public void setPeriod(String period) {
		Period = period;
	}
	
	
	public List<Integer> getLandmarkCategoryIDList() {
		return LandmarkCategoryIDList;
	}
	public void setLandmarkCategoryIDList(List<Integer> landmarkCategoryIDList) {
		LandmarkCategoryIDList = landmarkCategoryIDList;
	}
	
	
	public List<Integer> getLandmarkIdList() {
		return LandmarkIdList;
	}
	public void setLandmarkIdList(List<Integer> landmarkIdList) {
		LandmarkIdList = landmarkIdList;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public int getLoginTenancyId() {
		return loginTenancyId;
	}
	public void setLoginTenancyId(int loginTenancyId) {
		this.loginTenancyId = loginTenancyId;
	}
	
	
	
}
