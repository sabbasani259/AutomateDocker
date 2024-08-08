package remote.wise.service.datacontract;

import java.util.Map;

public class MachineRPMBandDataReportRespContract {

	private Map<String,String> dealerData;
    private int maxIdleRPMBand;
    private int maxWorkingRPMBand;        	

	public Map<String, String> getDealerData() {
		return dealerData;
	}
	public void setDealerData(Map<String, String> dealerData) {
		this.dealerData = dealerData;
	}	
	public int getMaxIdleRPMBand() {
		return maxIdleRPMBand;
	}
	public void setMaxIdleRPMBand(int maxIdleRPMBand) {
		this.maxIdleRPMBand = maxIdleRPMBand;
	}
	public int getMaxWorkingRPMBand() {
		return maxWorkingRPMBand;
	}
	public void setMaxWorkingRPMBand(int maxWorkingRPMBand) {
		this.maxWorkingRPMBand = maxWorkingRPMBand;
	}
	public String getZoneName() {
		return zoneName;
	}	
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	private String zoneName;
}
