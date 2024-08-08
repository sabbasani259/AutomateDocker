package remote.wise.businessentity;

import java.io.Serializable;

public class AccountDimensionEntity extends BaseBusinessEntity 
{
	int accountDimensionId;
	int accountId;
	int tenancyId;
	String accountName;
	
	public int getAccountDimensionId() {
		return accountDimensionId;
	}
	public void setAccountDimensionId(int accountDimensionId) {
		this.accountDimensionId = accountDimensionId;
	}
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	public int getTenancyId() {
		return tenancyId;
	}
	public void setTenancyId(int tenancyId) {
		this.tenancyId = tenancyId;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	
}
