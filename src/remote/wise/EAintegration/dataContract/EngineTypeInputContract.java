package remote.wise.EAintegration.dataContract;

import java.io.Serializable;

public class EngineTypeInputContract implements Serializable
{
	
	String engineTypeName;
	String engineTypeCode;
	String messageId;
	String fileRef;
	String process;
	String reprocessJobCode;
	
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
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getFileRef() {
		return fileRef;
	}
	public void setFileRef(String fileRef) {
		this.fileRef = fileRef;
	}
	public String getProcess() {
		return process;
	}
	public void setProcess(String process) {
		this.process = process;
	}
	public String getReprocessJobCode() {
		return reprocessJobCode;
	}
	public void setReprocessJobCode(String reprocessJobCode) {
		this.reprocessJobCode = reprocessJobCode;
	}
	
}
