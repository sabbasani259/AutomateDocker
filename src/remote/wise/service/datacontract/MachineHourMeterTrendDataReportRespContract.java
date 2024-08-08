package remote.wise.service.datacontract;

import java.util.HashMap;

public class MachineHourMeterTrendDataReportRespContract {

	private HashMap<String,Double> dealerData;
    private double machineHours;
    
    /**
	 * @return the dealerData
	 */
	public HashMap<String, Double> getDealerData() {
		return dealerData;
	}
	/**
	 * @param dealerData the dealerData to set
	 */
	public void setDealerData(HashMap<String, Double> dealerData) {
		this.dealerData = dealerData;
	}
	/**
	 * @return the machineHours
	 */
	public double getMachineHours() {
		return machineHours;
	}
	/**
	 * @param machineHours the machineHours to set
	 */
	public void setMachineHours(double machineHours) {
		this.machineHours = machineHours;
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
