package remote.wise.service.datacontract;

import java.util.HashMap;

public class MachineAlertsTrendDataRespContract {

	private HashMap<String,Long> dealerData;
    private Long totalAlerts;
    
    /**
	 * @return the dealerData
	 */
	public HashMap<String, Long> getDealerData() {
		return dealerData;
	}
	/**
	 * @param dealerData the dealerData to set
	 */
	public void setDealerData(HashMap<String, Long> dealerData) {
		this.dealerData = dealerData;
	}
	
	/**
	 * @return the totalAlerts
	 */
	public Long getTotalAlerts() {
		return totalAlerts;
	}
	/**
	 * @param totalAlerts the totalAlerts to set
	 */
	public void setTotalAlerts(Long totalAlerts) {
		this.totalAlerts = totalAlerts;
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
	private String zoneName;
}

