package remote.wise.businessentity;

public class IndustryStandardEntity extends BaseBusinessEntity 
{
	private int standardId;
	private IndustryEntity industryId;
	private String standardName;
	private String versionNumber;
	private boolean incompatibleVersion;
	
	
	public boolean isIncompatibleVersion() {
		return incompatibleVersion;
	}
	public void setIncompatibleVersion(boolean incompatibleVersion) {
		this.incompatibleVersion = incompatibleVersion;
	}
	
	
	public int getStandardId() {
		return standardId;
	}
	public void setStandardId(int standardId) {
		this.standardId = standardId;
	}
	
	
	public IndustryEntity getIndustryId() {
		return industryId;
	}
	public void setIndustryId(IndustryEntity industryId) {
		this.industryId = industryId;
	}
	
	
	public String getStandardName() {
		return standardName;
	}
	public void setStandardName(String standardName) {
		this.standardName = standardName;
	}
	
	
	public String getVersionNumber() {
		return versionNumber;
	}
	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}
	
}
