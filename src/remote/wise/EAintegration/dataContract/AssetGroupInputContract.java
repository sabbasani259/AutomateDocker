package remote.wise.EAintegration.dataContract;

import java.io.Serializable;

public class AssetGroupInputContract implements Serializable
{
	String assetGroupName;
	String assetGroupCode;
	String messageId;
	String fileRef;
	String process;
	String reprocessJobCode;
	
	
	public String getAssetGroupName() {
		return assetGroupName;
	}
	public void setAssetGroupName(String assetGroupName) {
		this.assetGroupName = assetGroupName;
	}
	public String getAssetGroupCode() {
		return assetGroupCode;
	}
	public void setAssetGroupCode(String assetGroupCode) {
		this.assetGroupCode = assetGroupCode;
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
