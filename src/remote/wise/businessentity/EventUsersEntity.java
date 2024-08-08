package remote.wise.businessentity;

import java.io.Serializable;

public class EventUsersEntity extends BaseBusinessEntity implements Serializable
{
	private AssetEventEntity assetEventId;
	private ContactEntity contactId;
	private int isSms;
	private int isEmail;
	
	
	public EventUsersEntity(){}
	
	/*public EventUsersEntity(int eventUserId)
	{
		super.key = new Integer(eventUserId);
		EventUsersEntity e = (EventUsersEntity)read(this);
		if(e!=null)
		{
			setContactId(e.getContactId());
			setSms(e.isSms());
			setAssetEventId(e.getAssetEventId());
			setEmail(e.isEmail());
		}
	}*/

	public AssetEventEntity getAssetEventId() {
		return assetEventId;
	}

	public void setAssetEventId(AssetEventEntity assetEventId) {
		this.assetEventId = assetEventId;
	}

	public ContactEntity getContactId() {
		return contactId;
	}

	public void setContactId(ContactEntity contactId) {
		this.contactId = contactId;
	}

	public int getIsSms() {
		return isSms;
	}

	public void setIsSms(int isSms) {
		this.isSms = isSms;
	}

	public int getIsEmail() {
		return isEmail;
	}

	public void setIsEmail(int isEmail) {
		this.isEmail = isEmail;
	}

	
	
	
	
}
