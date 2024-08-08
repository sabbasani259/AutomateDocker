package remote.wise.businessentity;

import java.io.Serializable;
import java.util.Date;

public class AssetAccountMapping extends BaseBusinessEntity implements Serializable
{
	AssetEntity serialNumber;
	AccountEntity accountId;
	Date ownershipStartDate;
	
	public AssetEntity getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(AssetEntity serialNumber) {
		this.serialNumber = serialNumber;
	}
	public AccountEntity getAccountId() {
		return accountId;
	}
	public void setAccountId(AccountEntity accountId) {
		this.accountId = accountId;
	}
	public Date getOwnershipStartDate() {
		return ownershipStartDate;
	}
	public void setOwnershipStartDate(Date ownershipStartDate) {
		this.ownershipStartDate = ownershipStartDate;
	}
	
	

}
