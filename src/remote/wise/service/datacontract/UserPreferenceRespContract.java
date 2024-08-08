package remote.wise.service.datacontract;

import remote.wise.businessentity.CatalogValuesEntity;

public class UserPreferenceRespContract {
	
	private int catalogId;
	private int CatalogValueId;
	private String catalogValue;
	private String catalogName;
	private String contactId;
	
	
	
	public String getContactId() {
		return contactId;
	}
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	public int getCatalogValueId() {
		return CatalogValueId;
	}
	public void setCatalogValueId(int catalogValueId) {
		CatalogValueId = catalogValueId;
	}
	public String getCatalogName() {
		return catalogName;
	}
	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}
	public int getCatalogId() {
		return catalogId;
	}
	public void setCatalogId(int catalogId) {
		this.catalogId = catalogId;
	}
	public String getCatalogValue() {
		return catalogValue;
	}
	public void setCatalogValue(String catalogValue) {
		this.catalogValue = catalogValue;
	}
	
	
	
	

}
