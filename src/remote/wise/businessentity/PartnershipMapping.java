package remote.wise.businessentity;

import java.io.Serializable;

public class PartnershipMapping extends BaseBusinessEntity implements Serializable
{
	AccountEntity accountFromId;
	AccountEntity accountToId;
	PartnerRoleEntity partnerId;
	
	
	public AccountEntity getAccountFromId() {
		return accountFromId;
	}
	public void setAccountFromId(AccountEntity accountFromId) {
		this.accountFromId = accountFromId;
	}
	
	
	public AccountEntity getAccountToId() {
		return accountToId;
	}
	public void setAccountToId(AccountEntity accountToId) {
		this.accountToId = accountToId;
	}
	
	
	public PartnerRoleEntity getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(PartnerRoleEntity partnerId) {
		this.partnerId = partnerId;
	}
	
	
}
