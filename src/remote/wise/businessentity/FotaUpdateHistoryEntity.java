package remote.wise.businessentity;

import java.io.Serializable;
import java.sql.Timestamp;

public class FotaUpdateHistoryEntity extends BaseBusinessEntity  implements Serializable{
	private int history_id;
	private String imeiNo;
	private String versionId;
	private String fileName;
	private String sessionId;
	private Timestamp update_date;
	private String status;
	
	public FotaUpdateHistoryEntity(){}
	
	public FotaUpdateHistoryEntity(String imeiNo)
	{
		
		super.key = new String (imeiNo);
		FotaUpdateHistoryEntity e = (FotaUpdateHistoryEntity)read(this);
		if(e!=null)
		{
			setImeiNo(e.getImeiNo());
			setVersionId(e.getVersionId());
			setFileName(e.getFileName());
			setSessionId(e.getSessionId());
			setUpdate_date(e.getUpdate_date());
			setStatus(e.getStatus());
		}
		
	}
	
	public int getHistory_id() {
		return history_id;
	}

	public void setHistory_id(int history_id) {
		this.history_id = history_id;
	}

	public String getImeiNo(){
		return imeiNo;
	}
	public void setImeiNo(String imeiNo){
		this.imeiNo = imeiNo;
	}
	
	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public String getSessionId(){
		return sessionId;
	}
	public void setSessionId(String sessionId){
		this.sessionId = sessionId;
	}
	public Timestamp getUpdate_date(){
		return update_date;
	}
	public void setUpdate_date(Timestamp update_date){
		this.update_date = update_date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}

