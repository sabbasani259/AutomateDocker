/**
 * 
 */
package remote.wise.service.datacontract;

import java.io.Serializable;

/**
 * @author sunayak
 *
 */
public class HAJAssetDetailsRespContract implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String vin;
	private String profilecode;
	private String modelcode;
	private int accountid;	
	
	public int getAccountid() {
		return accountid;
	}
	public void setAccountid(int accountid) {
		this.accountid = accountid;
	}
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public String getProfilecode() {
		return profilecode;
	}
	public void setProfilecode(String profilecode) {
		this.profilecode = profilecode;
	}
	public String getModelcode() {
		return modelcode;
	}
	public void setModelcode(String modelcode) {
		this.modelcode = modelcode;
	}
	
}
