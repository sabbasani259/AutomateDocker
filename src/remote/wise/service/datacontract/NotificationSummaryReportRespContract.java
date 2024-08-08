package remote.wise.service.datacontract;

import java.util.HashMap;

public class NotificationSummaryReportRespContract {

	
/*	private String MachineProfileName;
	private String MachineGroupName;
	private int performanceCount;
	private int HealthCount;
	private int UtilzationCount;
	private Long ServiceCount;*/
	private String serialNumber;
	private String tenancy_name;
	private int machineGroupId;//Now added
	private String machineGroupName;
	//private String machineProfileName;
	private Long Count;
	private int AssetGroupId;
	private String AssetGroupName;
	private int AssetTypeId;//Now added
	private String AssetTypeName;//Now added
	private int TenancyId;
	private String NotificationTypeName;
	private String NickName;
	HashMap<String,Long> nameCount;
	private String dealerName;
	
	
	/**
	 * @return the dealerName
	 */
	public String getDealerName() {
		return dealerName;
	}
	/**
	 * @param dealerName the dealerName to set
	 */
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	public HashMap<String, Long> getNameCount() {
		return nameCount;
	}
	public void setNameCount(HashMap<String, Long> nameCount) {
		this.nameCount = nameCount;
	}
	public int getMachineGroupId() {
		return machineGroupId;
	}
	public void setMachineGroupId(int machineGroupId) {
		this.machineGroupId = machineGroupId;
	}


	
	public int getAssetTypeId() {
		return AssetTypeId;
	}
	public void setAssetTypeId(int assetTypeId) {
		AssetTypeId = assetTypeId;
	}
	public String getAssetTypeName() {
		return AssetTypeName;
	}
	public void setAssetTypeName(String assetTypeName) {
		AssetTypeName = assetTypeName;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getTenancy_name() {
		return tenancy_name;
	}
	public void setTenancy_name(String tenancy_name) {
		this.tenancy_name = tenancy_name;
	}
	public String getMachineGroupName() {
		return machineGroupName;
	}
	public void setMachineGroupName(String machineGroupName) {
		this.machineGroupName = machineGroupName;
	}
	/*public String getMachineProfileName() {
		return machineProfileName;
	}
	public void setMachineProfileName(String machineProfileName) {
		this.machineProfileName = machineProfileName;
	}*/
	public String getNickName() {
		return NickName;
	}
	public void setNickName(String nickName) {
		NickName = nickName;
	}
	public String getNotificationTypeName() {
		return NotificationTypeName;
	}
	public void setNotificationTypeName(String notificationTypeName) {
		NotificationTypeName = notificationTypeName;
	}
	/*	public String getMachineProfileName() {
		return MachineProfileName;
	}
	public void setMachineProfileName(String machineProfileName) {
		MachineProfileName = machineProfileName;
	}
	public String getMachineGroupName() {
		return MachineGroupName;
	}
	public void setMachineGroupName(String machineGroupName) {
		MachineGroupName = machineGroupName;
	}
	public int getPerformanceCount() {
		return performanceCount;
	}
	public void setPerformanceCount(int performanceCount) {
		this.performanceCount = performanceCount;
	}
	public int getHealthCount() {
		return HealthCount;
	}
	public void setHealthCount(int healthCount) {
		HealthCount = healthCount;
	}
	public int getUtilzationCount() {
		return UtilzationCount;
	}
	public void setUtilzationCount(int utilzationCount) {
		UtilzationCount = utilzationCount;
	}
	public Long getServiceCount() {
		return ServiceCount;
	}
	public void setServiceCount(Long ServiceCount ) {
		ServiceCount = ServiceCount;
	}
	*/
	
	public int getAssetGroupId() {
		return AssetGroupId;
	}
	public Long getCount() {
		return Count;
	}
	public void setCount(Long count) {
		Count = count;
	}
	public void setAssetGroupId(int assetGroupId) {
		AssetGroupId = assetGroupId;
	}
	public String getAssetGroupName() {
		return AssetGroupName;
	}
	public void setAssetGroupName(String assetGroupName) {
		AssetGroupName = assetGroupName;
	}
	public int getTenancyId() {
		return TenancyId;
	}
	public void setTenancyId(int tenancyId) {
		TenancyId = tenancyId;
	}
	
}
