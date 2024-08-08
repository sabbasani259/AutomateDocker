package remote.wise.businessentity;

import java.io.Serializable;

public class TenancyDelegationEntity extends BaseBusinessEntity implements Serializable
{
	private TenancyEntity tenancyId;
	private ContactEntity contactId;
	
	public TenancyEntity getTenancyId() {
		return tenancyId;
	}
	public void setTenancyId(TenancyEntity tenancyId) {
		this.tenancyId = tenancyId;
	}
	
	public ContactEntity getContactId() {
		return contactId;
	}
	public void setContactId(ContactEntity contactId) {
		this.contactId = contactId;
	}
	
	
}
