package remote.wise.businessentity;

public class ParameterTypeEntity extends BaseBusinessEntity 
{

	private int parameterTypeId;
	private IndustryStandardEntity standardId;
	private String parameterTypeName;
	
	
	public int getParameterTypeId() {
		return parameterTypeId;
	}
	public void setParameterTypeId(int parameterTypeId) {
		this.parameterTypeId = parameterTypeId;
	}
	
	
	public IndustryStandardEntity getStandardId() {
		return standardId;
	}
	public void setStandardId(IndustryStandardEntity standardId) {
		this.standardId = standardId;
	}
	
	
	public String getParameterTypeName() {
		return parameterTypeName;
	}
	public void setParameterTypeName(String parameterTypeName) {
		this.parameterTypeName = parameterTypeName;
	}
	
	
}
