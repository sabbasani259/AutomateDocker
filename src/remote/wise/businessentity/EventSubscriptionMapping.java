package remote.wise.businessentity;

import java.io.Serializable;

public class EventSubscriptionMapping extends BaseBusinessEntity implements Serializable
{
	ContactEntity contactId;
	AssetEntity serialNumber;
	int priority;
	
	
	public ContactEntity getContactId() {
		return contactId;
	}
	public void setContactId(ContactEntity contactId) {
		this.contactId = contactId;
	}
	public AssetEntity getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(AssetEntity serialNumber) {
		this.serialNumber = serialNumber;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
}
