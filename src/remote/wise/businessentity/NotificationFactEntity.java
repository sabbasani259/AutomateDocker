package remote.wise.businessentity;

import java.io.Serializable;
import java.sql.Timestamp;

public class NotificationFactEntity extends BaseBusinessEntity implements Serializable {

	private Timestamp Time_Key;
	private String SerialNumber;
	private int Year;
	
	public int getYear() {
		return Year;
	}

	public void setYear(int year) {
		Year = year;
	}

	private NotificationDimensionEntity Notification_Id;
	private TenancyDimensionEntity  Tenancy_Id;
	private AccountDimensionEntity Account_Id;	
	private AssetClassDimensionEntity AssetClass_Id;
	private int TimeCount;
	private LandmarkDimensionEntity landmarkId;
	
	
	
	public LandmarkDimensionEntity getLandmarkId() {
		return landmarkId;
	}

	public void setLandmarkId(LandmarkDimensionEntity landmarkId) {
		this.landmarkId = landmarkId;
	}

	public int getTimeCount() {
		return TimeCount;
	}

	public void setTimeCount(int timeCount) {
		TimeCount = timeCount;
	}

	public Timestamp getTime_Key() {
		return Time_Key;
	}

	public void setTime_Key(Timestamp time_Key) {
		Time_Key = time_Key;
	}

	public String getSerialNumber() {
		return SerialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}



	public NotificationDimensionEntity getNotification_Id() {
		return Notification_Id;
	}

	public void setNotification_Id(NotificationDimensionEntity notification_Id) {
		Notification_Id = notification_Id;
	}

	public TenancyDimensionEntity getTenancy_Id() {
		return Tenancy_Id;
	}

	public void setTenancy_Id(TenancyDimensionEntity tenancy_Id) {
		Tenancy_Id = tenancy_Id;
	}

	public AccountDimensionEntity getAccount_Id() {
		return Account_Id;
	}

	public void setAccount_Id(AccountDimensionEntity account_Id) {
		Account_Id = account_Id;
	}

	

	
	public AssetClassDimensionEntity getAssetClass_Id() {
		return AssetClass_Id;
	}

	public void setAssetClass_Id(AssetClassDimensionEntity assetClass_Id) {
		AssetClass_Id = assetClass_Id;
	}

	public int getNotificationCount() {
		return NotificationCount;
	}

	public void setNotificationCount(int notificationCount) {
		NotificationCount = notificationCount;
	}

	private int NotificationCount;

	
	
	
	
}
