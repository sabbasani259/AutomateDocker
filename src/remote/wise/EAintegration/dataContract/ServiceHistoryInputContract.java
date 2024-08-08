package remote.wise.EAintegration.dataContract;

import java.io.Serializable;

public class ServiceHistoryInputContract implements Serializable
{
	String serialNumber;
	String dealerCode;
	String jobCardNumber;
	String dbmsPartCode;
	String servicedDate;
	String messageId;
	String fileRef;
	String process;
	String reprocessJobCode;
	//DF20190423:IM20018382-Adding additonal field jobCardDetails
	String jobCardDetails;
	
	//DF20191220:Abhishek::added column for Extended Warranty.
	String callTypeId;
	
	public String getCallTypeId() {
		return callTypeId;
	}
	public void setCallTypeId(String callTypeId) {
		this.callTypeId = callTypeId;
	}
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	/**
	 * @return the dealerCode
	 */
	public String getDealerCode() {
		return dealerCode;
	}
	/**
	 * @param dealerCode the dealerCode to set
	 */
	public void setDealerCode(String dealerCode) {
		this.dealerCode = dealerCode;
	}
	/**
	 * @return the jobCardNumber
	 */
	public String getJobCardNumber() {
		return jobCardNumber;
	}
	/**
	 * @param jobCardNumber the jobCardNumber to set
	 */
	public void setJobCardNumber(String jobCardNumber) {
		this.jobCardNumber = jobCardNumber;
	}
	/**
	 * @return the dbmsPartCode
	 */
	public String getDbmsPartCode() {
		return dbmsPartCode;
	}
	/**
	 * @param dbmsPartCode the dbmsPartCode to set
	 */
	public void setDbmsPartCode(String dbmsPartCode) {
		this.dbmsPartCode = dbmsPartCode;
	}
	/**
	 * @return the servicedDate
	 */
	public String getServicedDate() {
		return servicedDate;
	}
	/**
	 * @param servicedDate the servicedDate to set
	 */
	public void setServicedDate(String servicedDate) {
		this.servicedDate = servicedDate;
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
	
	//DF20190423:IM20018382-Adding additonal field jobCardDetails
	/**
	 * @return the jobCardDetails
	 */
	public String getJobCardDetails() {
		return jobCardDetails;
	}
	/**
	 * @param jobCardDetails the jobCardDetails to set
	 */
	public void setJobCardDetails(String jobCardDetails) {
		this.jobCardDetails = jobCardDetails;
	}
	
}
