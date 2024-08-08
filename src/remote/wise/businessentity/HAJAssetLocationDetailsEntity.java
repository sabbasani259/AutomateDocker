/**
 * 
 */
package remote.wise.businessentity;

/**
 * @author sunayak
 *
 */
public class HAJAssetLocationDetailsEntity {

	private String serialNumber;
	private String Latitude;
	private String Longitude;
	private int sendFlag;
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @return the sendFlag
	 */
	public int getSendFlag() {
		return sendFlag;
	}
	/**
	 * @param sendFlag the sendFlag to set
	 */
	public void setSendFlag(int sendFlag) {
		this.sendFlag = sendFlag;
	}
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return Latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		Latitude = latitude;
	}
	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return Longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		Longitude = longitude;
	}
}
