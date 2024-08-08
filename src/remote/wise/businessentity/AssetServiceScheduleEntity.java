package remote.wise.businessentity;
import java.sql.*;

public class AssetServiceScheduleEntity extends BaseBusinessEntity {

	private int assetServiceScheduleId;
	private AssetEntity serialNumber;
	private ServiceScheduleEntity serviceScheduleId;
	private Timestamp scheduledDate;
	private AccountEntity dealerId;
	//DF20160502 - Rajani Nagaraju - Adding EngineHoursSchedule - Required for Service alert generation
	private long engineHoursSchedule; 
	
	//DF20191220:Abhishek::Added new column for extendede Warranty.
	private int alertGenFlag;
	
	public int getAssetServiceScheduleId() {
		return assetServiceScheduleId;
	}
	public void setAssetServiceScheduleId(int assetServiceScheduleId) {
		this.assetServiceScheduleId = assetServiceScheduleId;
	}
	public AssetEntity getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(AssetEntity serialNumber) {
		this.serialNumber = serialNumber;
	}
	public ServiceScheduleEntity getServiceScheduleId() {
		return serviceScheduleId;
	}
	public void setServiceScheduleId(ServiceScheduleEntity serviceScheduleId) {
		this.serviceScheduleId = serviceScheduleId;
	}
	public Timestamp getScheduledDate() {
		return scheduledDate;
	}
	public void setScheduledDate(Timestamp scheduledDate) {
		this.scheduledDate = scheduledDate;
	}
	public AccountEntity getDealerId() {
		return dealerId;
	}
	public void setDealerId(AccountEntity dealerId) {
		this.dealerId = dealerId;
	}
	public long getEngineHoursSchedule() {
		return engineHoursSchedule;
	}
	public void setEngineHoursSchedule(long engineHoursSchedule) {
		this.engineHoursSchedule = engineHoursSchedule;
	}
	public int getAlertGenFlag() {
		return alertGenFlag;
	}
	public void setAlertGenFlag(int alertGenFlag) {
		this.alertGenFlag = alertGenFlag;
	}
}
