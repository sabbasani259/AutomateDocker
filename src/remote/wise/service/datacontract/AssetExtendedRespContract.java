package remote.wise.service.datacontract;

public class AssetExtendedRespContract 
{
	private String OperatingStartTime;
	private String OperatingEndTime;
	private String UsageCategory;
	private String Offset;
	private String DriverName;
	private String DriverContactNumber;
	private String SerialNumber;
	private String notes;
	private int primaryOwnerId;
	//DF20131108 - Rajani Nagaraju - To update Edge Proxy table on the status(TRANSIT/NORMAL) of the machine
	private String deviceStatus;
	//DefectId:20141029 @Suprava Refresh CN
	private String cmhLoginId;
	private String application_timestamp;
	private String firmware_timestamp;
	private String cmhrflag;
	private String previousCMHR;
	//FW Version Number : To be displayed in the portal: 2015-03-04 : Deepthi
	private String FWVersionNumber;
	public String getFWVersionNumber() {
		return FWVersionNumber;
	}


	public void setFWVersionNumber(String fWVersionNumber) {
		FWVersionNumber = fWVersionNumber;
	}


	/**
	 * @return the application_timestamp
	 */
	public String getApplication_timestamp() {
		return application_timestamp;
	}


	/**
	 * @param application_timestamp the application_timestamp to set
	 */
	public void setApplication_timestamp(String application_timestamp) {
		this.application_timestamp = application_timestamp;
	}


	/**
	 * @return the firmware_timestamp
	 */
	public String getFirmware_timestamp() {
		return firmware_timestamp;
	}


	/**
	 * @param firmware_timestamp the firmware_timestamp to set
	 */
	public void setFirmware_timestamp(String firmware_timestamp) {
		this.firmware_timestamp = firmware_timestamp;
	}


	/**
	 * @return the cmhrflag
	 */
	public String getCmhrflag() {
		return cmhrflag;
	}


	/**
	 * @param cmhrflag the cmhrflag to set
	 */
	public void setCmhrflag(String cmhrflag) {
		this.cmhrflag = cmhrflag;
	}


	/**
	 * @return the previousCMHR
	 */
	public String getPreviousCMHR() {
		return previousCMHR;
	}


	/**
	 * @param previousCMHR the previousCMHR to set
	 */
	public void setPreviousCMHR(String previousCMHR) {
		this.previousCMHR = previousCMHR;
	}


	
	public AssetExtendedRespContract()
	{
		OperatingStartTime = null;
		OperatingEndTime = null;
		UsageCategory = null;
		Offset = null;
		DriverName = null;
		DriverContactNumber = null;
		SerialNumber = null;
		notes = null;
		primaryOwnerId = 0;
		deviceStatus =null;
	}
	
	
	/**
	 * @return the cmhLoginId
	 */
	public String getCmhLoginId() {
		return cmhLoginId;
	}


	/**
	 * @param cmhLoginId the cmhLoginId to set
	 */
	public void setCmhLoginId(String cmhLoginId) {
		this.cmhLoginId = cmhLoginId;
	}


	public int getPrimaryOwnerId() {
		return primaryOwnerId;
	}

	public void setPrimaryOwnerId(int primaryOwnerId) {
		this.primaryOwnerId = primaryOwnerId;
	}

	public String getSerialNumber() {
		return SerialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}
	public String getOperatingStartTime() {
		return OperatingStartTime;
	}
	public void setOperatingStartTime(String operatingStartTime) {
		OperatingStartTime = operatingStartTime;
	}
	public String getOperatingEndTime() {
		return OperatingEndTime;
	}
	public void setOperatingEndTime(String operatingEndTime) {
		OperatingEndTime = operatingEndTime;
	}
	public String getUsageCategory() {
		return UsageCategory;
	}
	public void setUsageCategory(String usageCategory) {
		UsageCategory = usageCategory;
	}
	public String getOffset() {
		return Offset;
	}
	public void setOffset(String offset) {
		Offset = offset;
	}
	public String getDriverName() {
		return DriverName;
	}
	public void setDriverName(String driverName) {
		DriverName = driverName;
	}
	public String getDriverContactNumber() {
		return DriverContactNumber;
	}
	public void setDriverContactNumber(String driverContactNumber) {
		DriverContactNumber = driverContactNumber;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}


	/**
	 * @return the deviceStatus
	 */
	public String getDeviceStatus() {
		return deviceStatus;
	}


	/**
	 * @param deviceStatus the deviceStatus to set
	 */
	public void setDeviceStatus(String deviceStatus) {
		this.deviceStatus = deviceStatus;
	}
	
}
