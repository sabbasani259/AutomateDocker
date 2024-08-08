package remote.wise.service.datacontract;

public class FotaUpgradedHistoryDetailsRespContract {
	
	private String serialNumber;
	private String upgradedVersion;
	private String FWVersion;
	
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getUpgradedVersion() {
		return upgradedVersion;
	}
	public void setUpgradedVersion(String upgradedVersion) {
		this.upgradedVersion = upgradedVersion;
	}
	public String getFWVersion() {
		return FWVersion;
	}
	public void setFWVersion(String fWVersion) {
		FWVersion = fWVersion;
	}	
}
