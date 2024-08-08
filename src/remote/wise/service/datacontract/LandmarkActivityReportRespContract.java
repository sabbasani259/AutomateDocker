package remote.wise.service.datacontract;

public class LandmarkActivityReportRespContract 
{
	private int landmarkId;
	private String landMarkName;
	private int landmarkCategoryId;
	private String landMarkCategoryName;
	private String serialNumber;
	private String nickname;
	private int numberOfArrivals;
	private int numberOfdepartures;
	private long totalDurationAtLandmarkInMinutes;
	private long longestDurationAtLandmarkInMinutes;
	//added by smitha on Aug 27th 2013
	private int machineGroupId;
	private String machineGroupName;	
	private String dealerName;
	
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	public int getMachineGroupId() {
		return machineGroupId;
	}
	public void setMachineGroupId(int machineGroupId) {
		this.machineGroupId = machineGroupId;
	}
	public String getMachineGroupName() {
		return machineGroupName;
	}
	public void setMachineGroupName(String machineGroupName) {
		this.machineGroupName = machineGroupName;
	}
	//ended on Aug 27th 2013
	public int getLandmarkId() {
		return landmarkId;
	}
	public void setLandmarkId(int landmarkId) {
		this.landmarkId = landmarkId;
	}
	public String getLandMarkName() {
		return landMarkName;
	}
	public void setLandMarkName(String landMarkName) {
		this.landMarkName = landMarkName;
	}
	public int getLandmarkCategoryId() {
		return landmarkCategoryId;
	}
	public void setLandmarkCategoryId(int landmarkCategoryId) {
		this.landmarkCategoryId = landmarkCategoryId;
	}
	public String getLandMarkCategoryName() {
		return landMarkCategoryName;
	}
	public void setLandMarkCategoryName(String landMarkCategoryName) {
		this.landMarkCategoryName = landMarkCategoryName;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getNumberOfArrivals() {
		return numberOfArrivals;
	}
	public void setNumberOfArrivals(int numberOfArrivals) {
		this.numberOfArrivals = numberOfArrivals;
	}
	public int getNumberOfdepartures() {
		return numberOfdepartures;
	}
	public void setNumberOfdepartures(int numberOfdepartures) {
		this.numberOfdepartures = numberOfdepartures;
	}
	public long getTotalDurationAtLandmarkInMinutes() {
		return totalDurationAtLandmarkInMinutes;
	}
	public void setTotalDurationAtLandmarkInMinutes(
			long totalDurationAtLandmarkInMinutes) {
		this.totalDurationAtLandmarkInMinutes = totalDurationAtLandmarkInMinutes;
	}
	public long getLongestDurationAtLandmarkInMinutes() {
		return longestDurationAtLandmarkInMinutes;
	}
	public void setLongestDurationAtLandmarkInMinutes(
			long longestDurationAtLandmarkInMinutes) {
		this.longestDurationAtLandmarkInMinutes = longestDurationAtLandmarkInMinutes;
	}

	
}
