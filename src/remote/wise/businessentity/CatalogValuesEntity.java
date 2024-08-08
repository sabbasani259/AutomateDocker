package remote.wise.businessentity;

public class CatalogValuesEntity   extends BaseBusinessEntity
{
	private int catalogValueId;
	private PreferenceCatalogEntity catalogId;
	private String catalogValue;
	
	
	public int getCatalogValueId() {
		return catalogValueId;
	}
	public void setCatalogValueId(int catalogValueId) {
		this.catalogValueId = catalogValueId;
	}
	public PreferenceCatalogEntity getCatalogId() {
		return catalogId;
	}
	public void setCatalogId(PreferenceCatalogEntity catalogId) {
		this.catalogId = catalogId;
	}
	public String getCatalogValue() {
		return catalogValue;
	}
	public void setCatalogValue(String catalogValue) {
		this.catalogValue = catalogValue;
	}
	
	public CatalogValuesEntity(int eventTypeId)
	{
		super.key = new Integer(eventTypeId);
		CatalogValuesEntity e = (CatalogValuesEntity) read(this);
		if(e != null)
		{
			setCatalogId(e.getCatalogId());
			setCatalogValue(e.getCatalogValue());
			setCatalogValueId(e.getCatalogValueId());
		
		}
	}
	
	public CatalogValuesEntity()
	{
		
	}
	
}
