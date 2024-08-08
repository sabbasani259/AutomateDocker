/**
 * CR428 : 20230830 : Dhiraj Kumar : Sea Ports (Landmark) Configurations
 */
package remote.wise.service.datacontract;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SetSeaportLandmarkAssetReqContract {

	String loginId;
	int landMarkId;

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public int getLandMarkId() {
		return landMarkId;
	}

	public void setLandMarkId(int landMarkId) {
		this.landMarkId = landMarkId;
	}

}
