package remote.wise.service.datacontract;

import java.sql.Timestamp;

public class DownloadAempReqContract {
	private String serialNumber;
	private String Period;
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getPeriod() {
		return Period;
	}
	public void setPeriod(String period) {
		Period = period;
	}
	
}
