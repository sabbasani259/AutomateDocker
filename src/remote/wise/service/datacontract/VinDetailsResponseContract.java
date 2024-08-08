package remote.wise.service.datacontract;

public class VinDetailsResponseContract {

	private int serviceScheduleId;
	private String serviceName;
	private String DBMSPartCode;

	public VinDetailsResponseContract(){
	}

	public int getServiceScheduleId() {
		return serviceScheduleId;
	}
	public void setServiceScheduleId(int serviceScheduleId) {
		this.serviceScheduleId = serviceScheduleId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getDBMSPartCode() {
		return DBMSPartCode;
	}
	public void setDBMSPartCode(String dBMSPartCode) {
		DBMSPartCode = dBMSPartCode;
	}
}
