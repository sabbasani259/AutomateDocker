package remote.wise.service.datacontract;

public class MachineActivityReportRespContract {
	//DF:2013:12:13 : Conversion from Long to Double for all the required fields : Suprava
	private String serialNumber;
	private int MachineGroup_Id;
	private String MachineGroup_Name;
	private int AssetGroup_ID;
	
	private String ProfileName;      //  AssetGroup_name
	private double TotalMachineLifeHours;
	private int AssetTypeId;
	
	private String AssetTypeName;
	private double MachineHours;
	private String Status;
	
	private long Duration_in_status;
	private String Location;
	private int Tenancy_ID;
	private String tenancyName;
	//DefectID:20150223 @Suprava Added DealerName as additional Parameter
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
	public String getTenancyName() {
		return tenancyName;
	}
	public void setTenancyName(String tenancyName) {
		this.tenancyName = tenancyName;
	}
	public int getTenancy_ID() {
		return Tenancy_ID;
	}
	public void setTenancy_ID(int tenancy_ID) {
		Tenancy_ID = tenancy_ID;
	}

	public int getMachineGroup_Id() {
		return MachineGroup_Id;
	}
	public void setMachineGroup_Id(int machineGroup_Id) {
		MachineGroup_Id = machineGroup_Id;
	}
	public String getMachineGroup_Name() {
		return MachineGroup_Name;
	}
	public void setMachineGroup_Name(String machineGroup_Name) {
		MachineGroup_Name = machineGroup_Name;
	}
	public int getAssetGroup_ID() {
		return AssetGroup_ID;
	}
	public void setAssetGroup_ID(int assetGroup_ID) {
		AssetGroup_ID = assetGroup_ID;
	}
	public String getProfileName() {
		return ProfileName;
	}
	public void setProfileName(String profileName) {
		ProfileName = profileName;
	}
	public double getTotalMachineLifeHours() {
		return TotalMachineLifeHours;
	}
	public void setTotalMachineLifeHours(double totalMachineLifeHours) {
		TotalMachineLifeHours = totalMachineLifeHours;
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
	public double getMachineHours() {
		return MachineHours;
	}
	public void setMachineHours(double machineHours) {
		MachineHours = machineHours;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public long getDuration_in_status() {
		return Duration_in_status;
	}
	public void setDuration_in_status(long duration_in_status) {
		Duration_in_status = duration_in_status;
	}
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		Location = location;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}


}
