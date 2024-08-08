package remote.wise.businessentity;

import java.io.Serializable;

public class AccountTenancyMapping extends BaseBusinessEntity implements Serializable
{
	private AccountEntity account_id;
	private TenancyEntity tenancy_id;
	
	public AccountEntity getAccount_id() {
		return account_id;
	}
	public void setAccount_id(AccountEntity account_id) {
		this.account_id = account_id;
	}
	public TenancyEntity getTenancy_id() {
		return tenancy_id;
	}
	public void setTenancy_id(TenancyEntity tenancy_id) {
		this.tenancy_id = tenancy_id;
	}
	
	
	
}
