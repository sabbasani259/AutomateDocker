/**
 * 
 */
package remote.wise.service.datacontract;

import java.util.List;
import java.util.Map;

import remote.wise.businessobject.DealerCodeEntity;

/**
 * @author roopn5
 *
 */
public class ZonalAccountRespContract {
	String zoneAccountCode;
	String zoneAccountName;
	List<DealerCodeEntity> dealer;
	
	Map<String, String> dealerMap;
	/**
	 * @return the zoneAccountCode
	 */
	public String getZoneAccountCode() {
		return zoneAccountCode;
	}
	/**
	 * @param zoneAccountCode the zoneAccountCode to set
	 */
	public void setZoneAccountCode(String zoneAccountCode) {
		this.zoneAccountCode = zoneAccountCode;
	}
	/**
	 * @return the zoneAccountName
	 */
	public String getZoneAccountName() {
		return zoneAccountName;
	}
	/**
	 * @param zoneAccountName the zoneAccountName to set
	 */
	public void setZoneAccountName(String zoneAccountName) {
		this.zoneAccountName = zoneAccountName;
	}
	/**
	 * @return the dealer
	 */
	public List<DealerCodeEntity> getDealer() {
		return dealer;
	}
	/**
	 * @param dealer the dealer to set
	 */
	public void setDealer(List<DealerCodeEntity> dealer) {
		this.dealer = dealer;
	}
	/**
	 * @return the dealerMap
	 */
	public Map<String, String> getDealerMap() {
		return dealerMap;
	}
	/**
	 * @param dealerMap the dealerMap to set
	 */
	public void setDealerMap(Map<String, String> dealerMap) {
		this.dealerMap = dealerMap;
	}

	
	
	

}
