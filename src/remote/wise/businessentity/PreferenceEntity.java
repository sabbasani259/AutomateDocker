package remote.wise.businessentity;

import java.io.Serializable;

public class PreferenceEntity extends BaseBusinessEntity implements Serializable {

	
	CatalogValuesEntity catalogValueId;
	ContactEntity contact;
	
	public CatalogValuesEntity getCatalogValueId() {
		return catalogValueId;
	}
	public void setCatalogValueId(CatalogValuesEntity catalogValueId) {
		this.catalogValueId = catalogValueId;
	}
	public ContactEntity getContact() {
		return contact;
	}
	public void setContact(ContactEntity contact) {
		this.contact = contact;
	}

	
/*public PreferenceEntity() {}
	
	public PreferenceEntity(String loginId){
		
		super.key = new String(loginId);
		PreferenceEntity e=(PreferenceEntity) read(this);
		
		if(e!=null)
		{
			setCatalogValueId(e.getCatalogValueId());
			setContact(e.getContact());
			
		}
			
	}*/
	
	public PreferenceEntity(){
		
	}
	
}
