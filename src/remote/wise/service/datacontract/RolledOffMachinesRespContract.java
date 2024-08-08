package remote.wise.service.datacontract;



/**
 * @author kprabhu5
 *
 */
public class RolledOffMachinesRespContract {
	
	private String serialNumber;
	private String IMEINumber;
	private String simNumber;
	private String machineName;
	private int assetGroupId;
	private int assetTypeId;
	private int engineTypeId;
	
	private String profileName;
	private String modelName;
	private String engineName;
	
	private String latitude;
	private String longitude;
	//DefectId:20140919 @ Suprava Search in New Machine Tab
	private String regDate;
	
	//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
	private String fuelLevel;
	private String lastReportedTime;
	private String enginehours;
	//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
	
	//CR481-20240731-Sai Divya-Status,proposedFWVersion,fWVersion columns are added to NMT
//	private String status;
//	private String proposedFWVersion;
//	private String fWVersion;
//	public String getStatus() {
//		return status;
//	}
//
//	public void setStatus(String status) {
//		this.status = status;
//	}
//
//	public String getProposedFWVersion() {
//		return proposedFWVersion;
//	}
//
//	public void setProposedFWVersion(String proposedFWVersion) {
//		this.proposedFWVersion = proposedFWVersion;
//	}
//
//	
//	public String getfWVersion() {
//		return fWVersion;
//	}
//
//	public void setfWVersion(String fWVersion) {
//		this.fWVersion = fWVersion;
//	}


	// DF20200429 - Zakir : Adding comment variable for new column option in New
	// machines tab
	private String comment;

	// Zakir: Adding getters and setters for Comment
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * @return the regDate
	 */
	public String getRegDate() {
		return regDate;
	}
	/**
	 * @param regDate the regDate to set
	 */
	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	/**
	 * @return the iMEINumber
	 */
	public String getIMEINumber() {
		return IMEINumber;
	}
	/**
	 * @param iMEINumber the iMEINumber to set
	 */
	public void setIMEINumber(String iMEINumber) {
		IMEINumber = iMEINumber;
	}
	/**
	 * @return the simNumber
	 */
	public String getSimNumber() {
		return simNumber;
	}
	/**
	 * @param simNumber the simNumber to set
	 */
	public void setSimNumber(String simNumber) {
		this.simNumber = simNumber;
	}
	
	/**
	 * @return the machineName
	 */
	public String getMachineName() {
		return machineName;
	}
	/**
	 * @param machineName the machineName to set
	 */
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	/**
	 * @return the assetGroupId
	 */
	public int getAssetGroupId() {
		return assetGroupId;
	}
	/**
	 * @param assetGroupId the assetGroupId to set
	 */
	public void setAssetGroupId(int assetGroupId) {
		this.assetGroupId = assetGroupId;
	}
	/**
	 * @return the assetTypeId
	 */
	public int getAssetTypeId() {
		return assetTypeId;
	}
	/**
	 * @param assetTypeId the assetTypeId to set
	 */
	public void setAssetTypeId(int assetTypeId) {
		this.assetTypeId = assetTypeId;
	}
	/**
	 * @return the engineTypeId
	 */
	public int getEngineTypeId() {
		return engineTypeId;
	}
	/**
	 * @param engineTypeId the engineTypeId to set
	 */
	public void setEngineTypeId(int engineTypeId) {
		this.engineTypeId = engineTypeId;
	}
	/**
	 * @return the profileName
	 */
	public String getProfileName() {
		return profileName;
	}
	/**
	 * @param profileName the profileName to set
	 */
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	/**
	 * @return the modelName
	 */
	public String getModelName() {
		return modelName;
	}
	/**
	 * @param modelName the modelName to set
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	/**
	 * @return the engineName
	 */
	public String getEngineName() {
		return engineName;
	}
	/**
	 * @param engineName the engineName to set
	 */
	public void setEngineName(String engineName) {
		this.engineName = engineName;
	}
	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	/**
	 * @return the fuelLevel
	 */
	public String getFuelLevel() {
		return fuelLevel;
	}
	/**
	 * @param fuelLevel the fuelLevel to set
	 */
	public void setFuelLevel(String fuelLevel) {
		this.fuelLevel = fuelLevel;
	}
	/**
	 * @return the lastReportedTime
	 */
	public String getLastReportedTime() {
		return lastReportedTime;
	}
	/**
	 * @param lastReportedTime the lastReportedTime to set
	 */
	public void setLastReportedTime(String lastReportedTime) {
		this.lastReportedTime = lastReportedTime;
	}
	/**
	 * @return the enginehours
	 */
	public String getEnginehours() {
		return enginehours;
	}
	/**
	 * @param enginehours the enginehours to set
	 */
	public void setEnginehours(String enginehours) {
		this.enginehours = enginehours;
	}

//	@Override
//	public String toString() {
//		return "RolledOffMachinesRespContract [status=" + status + ", proposedFWVersion=" + proposedFWVersion
//				+ ", fWVersion=" + fWVersion + ", comment=" + comment + "]";
//	}

	
	

}
