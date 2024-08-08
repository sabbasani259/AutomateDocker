package remote.wise.businessentity;

public class PreferenceCatalogEntity extends BaseBusinessEntity
{
	private int catalogId;
	private String catalogName;
	
	public int getCatalogId() {
		return catalogId;
	}
	public void setCatalogId(int catalogId) {
		this.catalogId = catalogId;
	}
	public String getCatalogName() {
		return catalogName;
	}
	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}
	public PreferenceCatalogEntity(int catalogId)
	{
		super.key = new Integer(catalogId);
		PreferenceCatalogEntity e = (PreferenceCatalogEntity) read(this);
		if(e != null)
		{
			setCatalogId(e.getCatalogId());
			setCatalogName(e.getCatalogName());
			
		
		}
	}
	public PreferenceCatalogEntity(){
	}
}
