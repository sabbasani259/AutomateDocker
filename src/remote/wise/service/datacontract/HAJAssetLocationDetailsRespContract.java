/**
 * 
 */
package remote.wise.service.datacontract;

import java.io.Serializable;

/**
 * @author sunayak
 *
 */
public class HAJAssetLocationDetailsRespContract implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String vin;
	private String latitude;
	private String longitude;	
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	
}
