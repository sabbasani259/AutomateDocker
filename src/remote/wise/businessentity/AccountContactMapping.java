package remote.wise.businessentity;

import java.io.Serializable;

public class AccountContactMapping extends BaseBusinessEntity implements Serializable
{
	private AccountEntity account_id;
	private ContactEntity contact_id;
	
	public AccountEntity getAccount_id() {
		return account_id;
	}
	public void setAccount_id(AccountEntity account_id) {
		this.account_id = account_id;
	}
	public ContactEntity getContact_id() {
		return contact_id;
	}
	public void setContact_id(ContactEntity contact_id) {
		this.contact_id = contact_id;
	}

		
}
