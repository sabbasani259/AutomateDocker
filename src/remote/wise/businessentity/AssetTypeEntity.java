package remote.wise.businessentity;

import java.util.Set;

public class AssetTypeEntity extends BaseBusinessEntity
{
	public String getAssetTypeCode() {
		return assetTypeCode;
	}

	public void setAssetTypeCode(String assetTypeCode) {
		this.assetTypeCode = assetTypeCode;
	}
	private int asset_type_id;
	private String asset_type_name;
	private ClientEntity client_id;
	private String assetTypeCode;
	private String assetImage;
	
	
	public AssetTypeEntity(){}
	
	public AssetTypeEntity(int asset_type_id )
	{
		super.key = new Integer(asset_type_id);
		AssetTypeEntity e = (AssetTypeEntity)read(this);
		setAsset_type_name(e.getAsset_type_name());
		setClient_id(e.getClient_id());
	}
	
	
	public String getAssetImage() {
		return assetImage;
	}

	public void setAssetImage(String assetImage) {
		this.assetImage = assetImage;
	}

	public ClientEntity getClient_id() {
		return client_id;
	}
	public void setClient_id(ClientEntity client_id) {
		this.client_id = client_id;
	}
	public String getAsset_type_name() {
		return asset_type_name;
	}
	public void setAsset_type_name(String asset_type_name) {
		this.asset_type_name = asset_type_name;
	}
	
	public int getAsset_type_id() {
		return asset_type_id;
	}
	public void setAsset_type_id(int asset_type_id) {
		this.asset_type_id = asset_type_id;
	}

	
}
