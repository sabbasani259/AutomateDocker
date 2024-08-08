package remote.wise.EAintegration.dataContract;

import java.io.Serializable;

public class DealerMappingInputContract implements Serializable
{
	private String eccCode;
	private String crmCode;
	private String llCode;
	private String dealerName;
	private String messageId;
	private String fileRef;
	private String process;
	private String reprocessJobCode;
	
	/**
	 * @return the eccCode
	 */
	public String getEccCode() {
		return eccCode;
	}
	/**
	 * @param eccCode the eccCode to set
	 */
	public void setEccCode(String eccCode) {
		this.eccCode = eccCode;
	}
	/**
	 * @return the crmCode
	 */
	public String getCrmCode() {
		return crmCode;
	}
	/**
	 * @param crmCode the crmCode to set
	 */
	public void setCrmCode(String crmCode) {
		this.crmCode = crmCode;
	}
	/**
	 * @return the llCode
	 */
	public String getLlCode() {
		return llCode;
	}
	/**
	 * @param llCode the llCode to set
	 */
	public void setLlCode(String llCode) {
		this.llCode = llCode;
	}
	/**
	 * @return the dealerName
	 */
	public String getDealerName() {
		return dealerName;
	}
	/**
	 * @param dealerName the dealerName to set
	 */
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		return messageId;
	}
	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	/**
	 * @return the fileRef
	 */
	public String getFileRef() {
		return fileRef;
	}
	/**
	 * @param fileRef the fileRef to set
	 */
	public void setFileRef(String fileRef) {
		this.fileRef = fileRef;
	}
	/**
	 * @return the process
	 */
	public String getProcess() {
		return process;
	}
	/**
	 * @param process the process to set
	 */
	public void setProcess(String process) {
		this.process = process;
	}
	/**
	 * @return the reprocessJobCode
	 */
	public String getReprocessJobCode() {
		return reprocessJobCode;
	}
	/**
	 * @param reprocessJobCode the reprocessJobCode to set
	 */
	public void setReprocessJobCode(String reprocessJobCode) {
		this.reprocessJobCode = reprocessJobCode;
	}
	
	
}
