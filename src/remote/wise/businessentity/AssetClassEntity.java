package remote.wise.businessentity;

import java.util.HashSet;
import java.util.Set;

public class AssetClassEntity extends BaseBusinessEntity 
{
	private int AssetClassId;
	private String AssetClassName;
	private ClientEntity client_id;
	
	public AssetClassEntity(){}
	
	public AssetClassEntity(int AssetClassId )
	{
		super.key = new Integer(AssetClassId);
		AssetClassEntity e = (AssetClassEntity)read(this);
		setAssetClassName(e.getAssetClassName());
		setClient_id(e.getClient_id());
	}
	
	
	public ClientEntity getClient_id() {
		return client_id;
	}
	public void setClient_id(ClientEntity client_id) {
		this.client_id = client_id;
	}
	public int getAssetClassId() {
		return AssetClassId;
	}
	public void setAssetClassId(int assetClassId) {
		AssetClassId = assetClassId;
	}
	public String getAssetClassName() {
		return AssetClassName;
	}
	public void setAssetClassName(String assetClassName) {
		AssetClassName = assetClassName;
	}
	

}
