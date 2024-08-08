package remote.wise.businessentity;

import java.util.Set;

public class AssetGroupEntity extends BaseBusinessEntity 
{
	private int asset_group_id;
	private String asset_group_name;
	private ClientEntity client_id;
	
	public AssetGroupEntity(){}
	
	public AssetGroupEntity(int asset_group_id )
	{
		super.key = new Integer(asset_group_id);
		AssetGroupEntity e = (AssetGroupEntity)read(this);
		setAsset_group_name(e.getAsset_group_name());
		setClient_id(e.getClient_id());
	}
	
	public ClientEntity getClient_id() {
		return client_id;
	}
	public void setClient_id(ClientEntity client_id) {
		this.client_id = client_id;
	}
	public int getAsset_group_id() {
		return asset_group_id;
	}
	public void setAsset_group_id(int asset_group_id) {
		this.asset_group_id = asset_group_id;
	}
	public String getAsset_group_name() {
		return asset_group_name;
	}
	public void setAsset_group_name(String asset_group_name) {
		this.asset_group_name = asset_group_name;
	}
	
	
}
