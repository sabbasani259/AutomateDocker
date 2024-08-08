package remote.wise.service.datacontract;

import java.util.List;

/**
 * @author Rajani Nagaraju
 *
 */
public class AssetGroupRespContract 
{
	String loginId;
	int assetGroupId;
	String assetGroupName;
	String assetGroupDescription;
	int assetGroupTypeId;
	String assetGroupTypeName;
	List<String> serialNumberList;
	int tenancyId;
	int clientId;
		
	public AssetGroupRespContract()
	{
		loginId = null;
		assetGroupId =0;
		assetGroupName = null;
		assetGroupDescription = null;
		assetGroupTypeId =0;
		assetGroupTypeName = null;
		serialNumberList = null;
		tenancyId=0;
		clientId=0;
	}
	
	/**
	 * @return userLoginId as String
	 */
	public String getLoginId() {
		return loginId;
	}
	/**
	 * @param loginId userLoginId as String input
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	
	/**
	 * @return custom machine groupId as Integer
	 */
	public int getAssetGroupId() {
		return assetGroupId;
	}
	/**
	 * @param assetGroupId customMachineGroupId as Integer input
	 */
	public void setAssetGroupId(int assetGroupId) {
		this.assetGroupId = assetGroupId;
	}
	
	
	/**
	 * @return Name of the custom Machine group as String
	 */
	public String getAssetGroupName() {
		return assetGroupName;
	}
	/**
	 * @param assetGroupName Name of the CustomAssetgroup as String input
	 */
	public void setAssetGroupName(String assetGroupName) {
		this.assetGroupName = assetGroupName;
	}
	
	
	/**
	 * @return assetGroupTypeId as Integer
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
	 * @return List of serialNumbers
	 */
	public List<String> getSerialNumberList() {
		return serialNumberList;
	}
	/**
	 * @param serialNumberList List of serialNumber as String list
	 */
	public void setSerialNumberList(List<String> serialNumberList) {
		this.serialNumberList = serialNumberList;
	}
	
	
	/**
	 * @return tenancyId as Integer
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
	 * @return description of CustomAssetGroup as String
	 */
	public String getAssetGroupDescription() {
		return assetGroupDescription;
	}
	/**
	 * @param assetGroupDescription Description of AssetGroup as String input
	 */
	public void setAssetGroupDescription(String assetGroupDescription) {
		this.assetGroupDescription = assetGroupDescription;
	}
	
	
	/**
	 * @return clientId as Integer
	 */
	public int getClientId() {
		return clientId;
	}
	/**
	 * @param clientId ClientId as Integer input
	 */
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	
	
	/**
	 * @return assetGroupTypeName as String
	 */
	public String getAssetGroupTypeName() {
		return assetGroupTypeName;
	}
	/**
	 * @param assetGroupTypeName Name of the assetGroupType
	 */
	public void setAssetGroupTypeName(String assetGroupTypeName) {
		this.assetGroupTypeName = assetGroupTypeName;
	}
	
			
}
