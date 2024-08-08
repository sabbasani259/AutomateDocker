package remote.wise.service.datacontract;

/** 
 * @author Rajani Nagaraju
 *
 */
public class AssetGroupTypeRespContract 
{
	int tenancyId;
	int assetGroupTypeId;
	String assetGroupTypeName;
	String assetGroupTypeDescription;
	int clientId;
	
	
	public AssetGroupTypeRespContract()
	{
		tenancyId=0;
		assetGroupTypeId=0;
		assetGroupTypeName = null;
		assetGroupTypeDescription = null;
		clientId=0;
	}
	
	
	/**
	 * @return Returns ClientId as Integer
	 */
	public int getClientId() {
		return clientId;
	}
	/**
	 * @param clientId ClientId as integer input
	 */
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	
	
	/**
	 * @return Returns tenancyId as Integer
	 */
	public int getTenancyId() {
		return tenancyId;
	}
	/**
	 * @param tenancyId TenancyId as Integer input
	 */
	public void setTenancyId(int tenancyId) {
		this.tenancyId = tenancyId;
	}
	
	
	/**
	 * @return Returns AssetGroupId as Integer
	 */
	public int getAssetGroupTypeId() {
		return assetGroupTypeId;
	}
	/**
	 * @param assetGroupTypeId AssetGroupTypeId as Integer input
	 */
	public void setAssetGroupTypeId(int assetGroupTypeId) {
		this.assetGroupTypeId = assetGroupTypeId;
	}
	
	
	/**
	 * @return Returns AssetGroupTypeName as String
	 */
	public String getAssetGroupTypeName() {
		return assetGroupTypeName;
	}
	/**
	 * @param assetGroupTypeName Name of the AssetGroupType as input string
	 */
	public void setAssetGroupTypeName(String assetGroupTypeName) {
		this.assetGroupTypeName = assetGroupTypeName;
	}
	
	
	/**
	 * @return Returns the description of AssetGroupType as String
	 */
	public String getAssetGroupTypeDescription() {
		return assetGroupTypeDescription;
	}
	/**
	 * @param assetGroupTypeDescription Description of Asset Group type as String input
	 */
	public void setAssetGroupTypeDescription(String assetGroupTypeDescription) {
		this.assetGroupTypeDescription = assetGroupTypeDescription;
	}
	
	
}
