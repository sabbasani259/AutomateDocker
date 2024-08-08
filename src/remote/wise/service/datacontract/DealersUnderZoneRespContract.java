/**
 * 
 */
package remote.wise.service.datacontract;

import java.util.List;
import java.util.Map;

import remote.wise.businessobject.DealerEntity;

/**
 * @author kprabhu5
 *
 */
public class DealersUnderZoneRespContract {

	int zoneId;
	String zoneName;
	List<DealerEntity> dealer;
	/**
	 * @return the dealer
	 */
	public List<DealerEntity> getDealer() {
		return dealer;
	}
	/**
	 * @param dealer the dealer to set
	 */
	public void setDealer(List<DealerEntity> dealer) {
		this.dealer = dealer;
	}
	
	/**
	 * @return the zoneId
	 */
	public int getZoneId() {
		return zoneId;
	}
	/**
	 * @param zoneId the zoneId to set
	 */
	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}
	/**
	 * @return the zoneName
	 */
	public String getZoneName() {
		return zoneName;
	}
	/**
	 * @param zoneName the zoneName to set
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	Map<Integer, String> dealerMap;
	/**
	 * @return the dealerMap
	 */
	public Map<Integer, String> getDealerMap() {
		return dealerMap;
	}
	/**
	 * @param dealerMap the dealerMap to set
	 */
	public void setDealerMap(Map<Integer, String> dealerMap) {
		this.dealerMap = dealerMap;
	}
}
