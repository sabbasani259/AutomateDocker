package remote.wise.EAintegration.dataContract;

import java.io.Serializable;

public class ZonalInformationInputContract implements Serializable {
	
	private String zonalName;
	private String zonalCode;
	private String messageId;
	private String fileRef;
	private String process;
	private String reprocessJobCode;
	
	public String getZonalCode() {
		return zonalCode;
	}
	public void setZonalCode(String zonalCode) {
		this.zonalCode = zonalCode;
	}
	public String getZonalName() {
		return zonalName;
	}
	public void setZonalName(String zonalName) {
		this.zonalName = zonalName;
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
