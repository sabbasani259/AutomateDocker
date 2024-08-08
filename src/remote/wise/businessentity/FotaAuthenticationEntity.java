package remote.wise.businessentity;

import java.io.Serializable;
import java.sql.Timestamp;

public class FotaAuthenticationEntity extends BaseBusinessEntity  implements Serializable{
	private int fotaId;
	private String imeiNo;
	private FotaVersionEntity versionId;
	private String sessionId;
	private Timestamp update_date;
	private int status;
	
	public FotaAuthenticationEntity(){}
	
	public FotaAuthenticationEntity(String imeiNo)
	{
		
		super.key = new String (imeiNo);
		FotaAuthenticationEntity e = (FotaAuthenticationEntity)read(this);
		if(e!=null)
		{
			setImeiNo(e.getImeiNo());
			setVersionId(e.getVersionId());
			setSessionId(e.getSessionId());
			setUpdate_date(e.getUpdate_date());
			setStatus(e.getStatus());
		}
		
	}
	public int getFotaId(){
		return fotaId;
	}
	public void setFotaId(int fotaId){
		this.fotaId = fotaId;
	}
	public String getImeiNo(){
		return imeiNo;
	}
	public void setImeiNo(String imeiNo){
		this.imeiNo = imeiNo;
	}
	
	public FotaVersionEntity getVersionId() {
		return versionId;
	}

	public void setVersionId(FotaVersionEntity versionId) {
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	

}
