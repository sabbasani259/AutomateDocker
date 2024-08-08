package remote.wise.businessentity;

import java.io.Serializable;
import java.util.Date;

public class AssetOwnerSnapshotEntity extends BaseBusinessEntity implements Serializable
{
	private String serialNumber;
	private AccountEntity accountId;
	private Date assetOwnershipDate;
	private String accountType;
	private AssetGroupEntity assetGroupId;
	private AssetTypeEntity assetTypeId;
	
	
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public AccountEntity getAccountId() {
		return accountId;
	}
	public void setAccountId(AccountEntity accountId) {
		this.accountId = accountId;
	}
	public Date getAssetOwnershipDate() {
		return assetOwnershipDate;
	}
	public void setAssetOwnershipDate(Date assetOwnershipDate) {
		this.assetOwnershipDate = assetOwnershipDate;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
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
	
	
	
}
