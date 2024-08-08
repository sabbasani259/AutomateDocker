package remote.wise.businessentity;

public class EngineTypeEntity extends BaseBusinessEntity 
{
	public EngineTypeEntity(){}
	public EngineTypeEntity(int engineTypeId) {
		super.key = new Integer(engineTypeId);
		EngineTypeEntity e = (EngineTypeEntity)read(this);
		setEngineTypeId(e.getEngineTypeId());
		setEngineTypeName(e.getEngineTypeName());
	}
	private int engineTypeId;
	private String engineTypeName;
	private String engineTypeCode;
	public int getEngineTypeId() {
		return engineTypeId;
	}
	public void setEngineTypeId(int engineTypeId) {
		this.engineTypeId = engineTypeId;
	}
	public String getEngineTypeName() {
		return engineTypeName;
	}
	public void setEngineTypeName(String engineTypeName) {
		this.engineTypeName = engineTypeName;
	}

	public String getEngineTypeCode() {
		return engineTypeCode;
	}

	public void setEngineTypeCode(String engineTypeCode) {
		this.engineTypeCode = engineTypeCode;
	}

		
}
