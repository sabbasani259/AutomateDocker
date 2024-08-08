package remote.wise.businessentity;

import java.io.Serializable;

public class AssetCustomGroupMapping extends BaseBusinessEntity implements Serializable
{
	CustomAssetGroupEntity group_id;
	AssetEntity serial_number;
	

		
	public CustomAssetGroupEntity getGroup_id() {
		return group_id;
	}
	public void setGroup_id(CustomAssetGroupEntity group_id) {
		this.group_id = group_id;
	}
	
	public AssetEntity getSerial_number() {
		return serial_number;
	}
	public void setSerial_number(AssetEntity serial_number) {
		this.serial_number = serial_number;
	}
}
