package remote.wise.service.datacontract;

import java.sql.Timestamp;

public class DownloadAempRespContract {
	private String Date;	
	private String PIN	;
	
	public String getDate() {
		return Date;
	}
	public void setDate(String date) {
		Date = date;
	}
	public String getPIN() {
		return PIN;
	}
	public void setPIN(String pIN) {
		PIN = pIN;
	}
}
