/**
 * 
 */
package remote.wise.businessentity;

/**
 * @author sunayak
 *
 */
public class HAJassetSnapshotEntity extends BaseBusinessEntity{

	private String serial_number;
	private int primary_owner_id;
	private String asset_type_name;
	private String asset_group_name;
	private int sendFlag;
	/**
	 * @return the sendFlag
	 */
	public int getSendFlag() {
		return sendFlag;
	}
	/**
	 * @param sendFlag the sendFlag to set
	 */
	public void setSendFlag(int sendFlag) {
		this.sendFlag = sendFlag;
	}
	/**
	 * @return the serial_number
	 */
	public String getSerial_number() {
		return serial_number;
	}
	/**
	 * @param serial_number the serial_number to set
	 */
	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}
	/**
	 * @return the primary_owner_id
	 */
	public int getPrimary_owner_id() {
		return primary_owner_id;
	}
	/**
	 * @param primary_owner_id the primary_owner_id to set
	 */
	public void setPrimary_owner_id(int primary_owner_id) {
		this.primary_owner_id = primary_owner_id;
	}
	/**
	 * @return the asset_type_name
	 */
	public String getAsset_type_name() {
		return asset_type_name;
	}
	/**
	 * @param asset_type_name the asset_type_name to set
	 */
	public void setAsset_type_name(String asset_type_name) {
		this.asset_type_name = asset_type_name;
	}
	/**
	 * @return the asset_group_name
	 */
	public String getAsset_group_name() {
		return asset_group_name;
	}
	/**
	 * @param asset_group_name the asset_group_name to set
	 */
	public void setAsset_group_name(String asset_group_name) {
		this.asset_group_name = asset_group_name;
	}
}
