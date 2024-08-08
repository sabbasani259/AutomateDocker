package remote.wise.businessentity;

import java.util.HashSet;
import java.util.Set;

public class CustomAssetGroupEntity extends BaseBusinessEntity 
{
	private int group_id,level;
	private String group_name,parent_group_name;
	private String group_description;
	private CustomAssetGroupEntity asset_group_type;
	
	private ClientEntity client_id;
	private TenancyEntity tenancy_id;
	private int active_status;
	
	public CustomAssetGroupEntity(){}
	
	public CustomAssetGroupEntity(int group_id){
		
		super.key = new Integer(group_id);
		CustomAssetGroupEntity e=(CustomAssetGroupEntity) read(this);
		
		if(e!=null)
		{
			setGroup_id(e.group_id);
			setLevel(e.level);
			setGroup_name(e.group_name);
			setGroup_description(e.group_description);
			setParent_group_name(e.parent_group_name);
			setAsset_group_type(e.asset_group_type);
			setClient_id(e.client_id);
			setTenancy_id(e.tenancy_id);
			setActive_status(e.active_status);
		}
				
	}
	public int getGroup_id() {
		return group_id;
	}

	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}
		
	public int getActive_status() {
		return active_status;
	}

	public void setActive_status(int active_status) {
		this.active_status = active_status;
	}

	public String getGroup_description() {
		return group_description;
	}

	public void setGroup_description(String group_description) {
		this.group_description = group_description;
	}

	
	
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	
	
	public String getParent_group_name() {
		return parent_group_name;
	}
	public void setParent_group_name(String parent_group_name) {
		this.parent_group_name = parent_group_name;
	}
	
	
	public CustomAssetGroupEntity getAsset_group_type() {
		return asset_group_type;
	}
	public void setAsset_group_type(CustomAssetGroupEntity asset_group_type) {
		this.asset_group_type = asset_group_type;
	}
	
	public ClientEntity getClient_id() {
		return client_id;
	}
	public void setClient_id(ClientEntity client_id) {
		this.client_id = client_id;
	}
	
	
	
	public TenancyEntity getTenancy_id() {
		return tenancy_id;
	}
	public void setTenancy_id(TenancyEntity tenancy_id) {
		this.tenancy_id = tenancy_id;
	}
	
			
}
