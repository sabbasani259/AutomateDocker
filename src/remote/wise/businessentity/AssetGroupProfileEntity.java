package remote.wise.businessentity;

import java.io.Serializable;
import java.sql.Timestamp;



public class AssetGroupProfileEntity extends BaseBusinessEntity implements Serializable {

	
	private AssetGroupEntity asset_grp_id;
	private Timestamp operating_Start_Time;
	private Timestamp Operating_end_time;
	private String asset_grp_code;
	public AssetGroupEntity getAsset_grp_id() {
		return asset_grp_id;
	}
	public void setAsset_grp_id(AssetGroupEntity asset_grp_id) {
		this.asset_grp_id = asset_grp_id;
	}
	public Timestamp getOperating_Start_Time() {
		return operating_Start_Time;
	}
	public void setOperating_Start_Time(Timestamp operating_Start_Time) {
		this.operating_Start_Time = operating_Start_Time;
	}
	public Timestamp getOperating_end_time() {
		return Operating_end_time;
	}
	public void setOperating_end_time(Timestamp operating_end_time) {
		Operating_end_time = operating_end_time;
	}
	public String getAsset_grp_code() {
		return asset_grp_code;
	}
	public void setAsset_grp_code(String asset_grp_code) {
		this.asset_grp_code = asset_grp_code;
	}
	
	
}
