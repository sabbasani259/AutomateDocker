package remote.wise.businessentity;

import java.sql.Timestamp;

public class FotaVersionEntity extends BaseBusinessEntity{
	private String versionId;
	private Timestamp version_date;
	private String file_path;
	private String level;
	private int value;
	
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public FotaVersionEntity(){}
	
	public String getVersionId(){
		return versionId;
	}
	public void setVersionId(String versionId){
		this.versionId = versionId;
	}
	public Timestamp getVersion_date(){
		return version_date;
	}
	public void setVersion_date(Timestamp version_date){
		this.version_date = version_date;
	}
	public String getFilePath(){
		return file_path;
	}
	public void setFilePath(String file_path){
		this.file_path = file_path;
	}

}
