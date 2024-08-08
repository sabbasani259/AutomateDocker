package remote.wise.businessentity;

import java.io.Serializable;
import java.sql.Timestamp;

public class AssetExtendedDetailsEntity extends BaseBusinessEntity implements Serializable {

	private AssetEntity serial_number;
	private Timestamp OperatingStartTime;
	private Timestamp OperatingEndTime;
	private String UsageCategory;
	private String Offset;
	private String DriverName;
	private String DriverContactNumber;
	private String notes;
	private CustomAssetClassEntity customAssetClass;
	//DF20131108 - Rajani Nagaraju - To update Edge Proxy table on the status(TRANSIT/NORMAL) of the machine
	private String Device_Status;
	//DF20181211 :Mani : Adding new column 
	private Timestamp CmhUpdatedTime;
	public AssetExtendedDetailsEntity()
	{
		Device_Status = "NORMAL";
	}
	
	public AssetExtendedDetailsEntity(String serial_number)
	{
		super.key = new String(serial_number);
		AssetExtendedDetailsEntity e= (AssetExtendedDetailsEntity)read(this);
		if(e!= null)
		{
			setSerial_number(e.getSerial_number());
			setOperatingStartTime(e.getOperatingStartTime());
			setOperatingEndTime(e.getOperatingEndTime());
			setOffset(e.getOffset());
			setDriverContactNumber(e.getDriverContactNumber());
			setDriverName(e.getDriverName());
			setUsageCategory(e.getUsageCategory());
			setNotes(e.getNotes());
			setCustomAssetClass(e.getCustomAssetClass());
		}
	}
	

	public AssetEntity getSerial_number() {
		return serial_number;
	}

	public void setSerial_number(AssetEntity serial_number) {
		this.serial_number = serial_number;
	}

	public Timestamp getOperatingStartTime() {
		return OperatingStartTime;
	}

	public void setOperatingStartTime(Timestamp operatingStartTime) {
		OperatingStartTime = operatingStartTime;
	}

	public Timestamp getOperatingEndTime() {
		return OperatingEndTime;
	}

	public void setOperatingEndTime(Timestamp operatingEndTime) {
		OperatingEndTime = operatingEndTime;
	}

	public String getUsageCategory() {
		return UsageCategory;
	}
	public void setUsageCategory(String usageCategory) {
		UsageCategory = usageCategory;
	}
	public String getOffset() {
		return Offset;
	}
	public void setOffset(String offset) {
		Offset = offset;
	}
	public String getDriverName() {
		return DriverName;
	}
	public void setDriverName(String driverName) {
		DriverName = driverName;
	}
	public String getDriverContactNumber() {
		return DriverContactNumber;
	}
	public void setDriverContactNumber(String driverContactNumber) {
		DriverContactNumber = driverContactNumber;
	}
	
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public CustomAssetClassEntity getCustomAssetClass() {
		return customAssetClass;
	}

	public void setCustomAssetClass(CustomAssetClassEntity customAssetClass) {
		this.customAssetClass = customAssetClass;
	}

	/**
	 * @return the device_Status
	 */
	public String getDevice_Status() {
		return Device_Status;
	}

	/**
	 * @param device_Status the device_Status to set
	 */
	public void setDevice_Status(String device_Status) {
		Device_Status = device_Status;
	}

	public Timestamp getCmhUpdatedTime() {
		return CmhUpdatedTime;
	}

	public void setCmhUpdatedTime(Timestamp cmhUpdatedTime) {
		CmhUpdatedTime = cmhUpdatedTime;
	}
	
	
}
