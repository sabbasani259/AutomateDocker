package remote.wise.businessentity;

public class ProductEntity extends BaseBusinessEntity
{
	private int productId;
	private String productName;
	private ClientEntity clientId;
	private AssetClassEntity assetClassId;
	private AssetGroupEntity assetGroupId;
	private AssetTypeEntity assetTypeId;
	private int make;
	private EngineTypeEntity engineTypeId;	
	
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public ClientEntity getClientId() {
		return clientId;
	}
	public void setClientId(ClientEntity clientId) {
		this.clientId = clientId;
	}
	public AssetClassEntity getAssetClassId() {
		return assetClassId;
	}
	public void setAssetClassId(AssetClassEntity assetClassId) {
		this.assetClassId = assetClassId;
	}
	public AssetGroupEntity getAssetGroupId() {
		return assetGroupId;
	}
	public void setAssetGroupId(AssetGroupEntity assetGroupId) {
		this.assetGroupId = assetGroupId;
	}
	public AssetTypeEntity getAssetTypeId() {
		return assetTypeId;
	}
	public void setAssetTypeId(AssetTypeEntity assetTypeId) {
		this.assetTypeId = assetTypeId;
	}
	public int getMake() {
		return make;
	}
	public void setMake(int make) {
		this.make = make;
	}
	public EngineTypeEntity getEngineTypeId() {
		return engineTypeId;
	}
	public void setEngineTypeId(EngineTypeEntity engineTypeId) {
		this.engineTypeId = engineTypeId;
	}
	
	
}
