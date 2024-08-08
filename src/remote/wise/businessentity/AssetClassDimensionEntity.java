package remote.wise.businessentity;

public class AssetClassDimensionEntity  extends BaseBusinessEntity
{
	int assetClassDimensionId;
	int assetClassId;
	String assetClassName;
	int assetGroupId;
	String assetGroupName;
	int assetTypeId;
	String assetTypeName;
	int productId;
	
	
	public int getAssetClassDimensionId() {
		return assetClassDimensionId;
	}
	public void setAssetClassDimensionId(int assetClassDimensionId) {
		this.assetClassDimensionId = assetClassDimensionId;
	}
	public int getAssetClassId() {
		return assetClassId;
	}
	public void setAssetClassId(int assetClassId) {
		this.assetClassId = assetClassId;
	}
	public String getAssetClassName() {
		return assetClassName;
	}
	public void setAssetClassName(String assetClassName) {
		this.assetClassName = assetClassName;
	}
	public int getAssetGroupId() {
		return assetGroupId;
	}
	public void setAssetGroupId(int assetGroupId) {
		this.assetGroupId = assetGroupId;
	}
	public String getAssetGroupName() {
		return assetGroupName;
	}
	public void setAssetGroupName(String assetGroupName) {
		this.assetGroupName = assetGroupName;
	}
	public int getAssetTypeId() {
		return assetTypeId;
	}
	public void setAssetTypeId(int assetTypeId) {
		this.assetTypeId = assetTypeId;
	}
	public String getAssetTypeName() {
		return assetTypeName;
	}
	public void setAssetTypeName(String assetTypeName) {
		this.assetTypeName = assetTypeName;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	
	
}
