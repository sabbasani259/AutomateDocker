package remote.wise.service.datacontract;

/** Response contract for AlertDetailsService
 * @author Rajani Nagaraju
 *
 */
public class AlertDetailsRespContract 
{
	String serialNumber;
	int assetEventId;
	String lifeHours;
	String latitude;
	String longitude;
	String notes;
	String profileName;
	
	
	/**
	 * @return assetEventId from eventHistory returned as integer
	 */
	public int getAssetEventId() {
		return assetEventId;
	}
	/**
	 * @param assetEventId assetEventId as Integer Input
	 */
	public void setAssetEventId(int assetEventId) {
		this.assetEventId = assetEventId;
	}
	
		
	/**
	 * @return Serial Number returned as String
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber VIN as String input
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	
	/**
	 * @return Cumulative Engine Hours returned as String
	 */
	public String getLifeHours() {
		return lifeHours;
	}
	/**
	 * @param lifeHours CumulativeEngineHours as String input
	 */
	public void setLifeHours(String lifeHours) {
		this.lifeHours = lifeHours;
	}
	
	
	/**
	 * @return Latitude Coordinate returned as String
	 */
	public String getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude Latitude Coordinate as String input
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	
	/**
	 * @return Longitude Coordinate returned as String
	 */
	public String getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude Longitude Coordinate as String input
	 */ 
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	
	/**
	 * @return Alert Comments returned as String
	 */
	public String getNotes() {
		return notes;
	}
	/**
	 * @param notes AlertComments as String input
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	
	
	
}
