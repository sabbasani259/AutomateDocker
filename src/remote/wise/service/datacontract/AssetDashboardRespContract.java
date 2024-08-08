package remote.wise.service.datacontract;

import java.sql.Timestamp;

/**
 * @author Rajani Nagaraju
 *
 */
public class AssetDashboardRespContract 
{
	String serialNumber;
	String nickName;
	String machineStatus;
	String lifeHours;
	String fuelLevel;
	String dueForService;
	String latitude;
	String longitude;
	String notes;
	String connectivityStatus;
	String externalBatteryInVolts;
	String externalBatteryStatus;
	String highCoolantTemperature;
	String lowEngineOilPressure;
	String modelName;
	String profileName;
	String engineTypeName;
	//DefectId:20140206 Engine_Status newParameter added 2014-02-06 @Suprava 
	String engineStatus;
	//DefectId:20140211 assetImage added as new Parameter.
	String assetImage;
	//DefectId:20141208 - Rajani Nagaraju - Return the last Pkt Received time (Either through GPRS / SMS )
	String lastPktReceivedTime;
	
	/**
	 * @return the assetImage
	 */
	public String getAssetImage() {
		return assetImage;
	}
	/**
	 * @param assetImage the assetImage to set
	 */
	public void setAssetImage(String assetImage) {
		this.assetImage = assetImage;
	}
	//DefectID: DF20131212 - Rajani Nagaraju - To return the last communicated timestamp of the machine.
	String lastReportedTime;
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
		
	
	public String getEngineTypeName() {
		return engineTypeName;
	}
	public void setEngineTypeName(String engineTypeName) {
		this.engineTypeName = engineTypeName;
	}
	public String getExternalBatteryStatus() {
		return externalBatteryStatus;
	}
	public void setExternalBatteryStatus(String externalBatteryStatus) {
		this.externalBatteryStatus = externalBatteryStatus;
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
	 * @return the nickName
	 */
	public String getNickName() {
		return nickName;
	}
	/**
	 * @param nickName the nickName to set
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	/**
	 * @return the machineStatus
	 */
	public String getMachineStatus() {
		return machineStatus;
	}
	/**
	 * @param machineStatus the machineStatus to set
	 */
	public void setMachineStatus(String machineStatus) {
		this.machineStatus = machineStatus;
	}
	/**
	 * @return the lifeHours
	 */
	public String getLifeHours() {
		return lifeHours;
	}
	/**
	 * @param lifeHours the lifeHours to set
	 */
	public void setLifeHours(String lifeHours) {
		this.lifeHours = lifeHours;
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
	 * @return the dueForService
	 */
	public String getDueForService() {
		return dueForService;
	}
	/**
	 * @param dueForService the dueForService to set
	 */
	public void setDueForService(String dueForService) {
		this.dueForService = dueForService;
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
	 * @return the engineStatus
	 */
	public String getEngineStatus() {
		return engineStatus;
	}
	/**
	 * @param engineStatus the engineStatus to set
	 */
	public void setEngineStatus(String engineStatus) {
		this.engineStatus = engineStatus;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}
	/**
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	/**
	 * @return the connectivityStatus
	 */
	public String getConnectivityStatus() {
		return connectivityStatus;
	}
	/**
	 * @param connectivityStatus the connectivityStatus to set
	 */
	public void setConnectivityStatus(String connectivityStatus) {
		this.connectivityStatus = connectivityStatus;
	}
	/**
	 * @return the externalBatteryInVolts
	 */
	public String getExternalBatteryInVolts() {
		return externalBatteryInVolts;
	}
	/**
	 * @param externalBatteryInVolts the externalBatteryInVolts to set
	 */
	public void setExternalBatteryInVolts(String externalBatteryInVolts) {
		this.externalBatteryInVolts = externalBatteryInVolts;
	}
	/**
	 * @return the highCoolantTemperature
	 */
	public String getHighCoolantTemperature() {
		return highCoolantTemperature;
	}
	/**
	 * @param highCoolantTemperature the highCoolantTemperature to set
	 */
	public void setHighCoolantTemperature(String highCoolantTemperature) {
		this.highCoolantTemperature = highCoolantTemperature;
	}
	/**
	 * @return the lowEngineOilPressure
	 */
	public String getLowEngineOilPressure() {
		return lowEngineOilPressure;
	}
	/**
	 * @param lowEngineOilPressure the lowEngineOilPressure to set
	 */
	public void setLowEngineOilPressure(String lowEngineOilPressure) {
		this.lowEngineOilPressure = lowEngineOilPressure;
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
	 * @return the lastPktReceivedTime
	 */
	public String getLastPktReceivedTime() {
		return lastPktReceivedTime;
	}
	/**
	 * @param lastPktReceivedTime the lastPktReceivedTime to set
	 */
	public void setLastPktReceivedTime(String lastPktReceivedTime) {
		this.lastPktReceivedTime = lastPktReceivedTime;
	}
	
		
}
