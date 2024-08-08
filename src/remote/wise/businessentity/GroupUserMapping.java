package remote.wise.businessentity;
import java.io.Serializable;

public class GroupUserMapping extends BaseBusinessEntity implements Serializable
{
	private CustomAssetGroupEntity group_id;
	private ContactEntity contact_id;
	
	public CustomAssetGroupEntity getGroup_id() {
		return group_id;
	}
	public void setGroup_id(CustomAssetGroupEntity group_id) {
		this.group_id = group_id;
	}
	public ContactEntity getContact_id() {
		return contact_id;
	}
	public void setContact_id(ContactEntity contact_id) {
		this.contact_id = contact_id;
	}
	


}
