package remote.wise.EAintegration.dataContract;

import java.io.Serializable;

/**
 * @author Rajani Nagaraju
 *
 */
public class MasterServiceScheduleInputContract implements Serializable
{
	private String servicePlan;
	private String serviceName;
	private String scheduleName;
	private String dbmsPartCode;
	private String assetGroupCode;
	private String assetTypeCode;
	private String engineTypeCode;
	private String engineHours;
	private String days;
	private String messageId;
	private String fileRef;
	private String process;
	private String reprocessJobCode;
	
	/**
	 * @return the servicePlan
	 */
	public String getServicePlan() {
		return servicePlan;
	}
	/**
	 * @param servicePlan the servicePlan to set
	 */
	public void setServicePlan(String servicePlan) {
		this.servicePlan = servicePlan;
	}
	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}
	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	/**
	 * @return the scheduleName
	 */
	public String getScheduleName() {
		return scheduleName;
	}
	/**
	 * @param scheduleName the scheduleName to set
	 */
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
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
	 * @return the assetGroupCode
	 */
	public String getAssetGroupCode() {
		return assetGroupCode;
	}
	/**
	 * @param assetGroupCode the assetGroupCode to set
	 */
	public void setAssetGroupCode(String assetGroupCode) {
		this.assetGroupCode = assetGroupCode;
	}
	/**
	 * @return the assetTypeCode
	 */
	public String getAssetTypeCode() {
		return assetTypeCode;
	}
	/**
	 * @param assetTypeCode the assetTypeCode to set
	 */
	public void setAssetTypeCode(String assetTypeCode) {
		this.assetTypeCode = assetTypeCode;
	}
	/**
	 * @return the engineTypeCode
	 */
	public String getEngineTypeCode() {
		return engineTypeCode;
	}
	/**
	 * @param engineTypeCode the engineTypeCode to set
	 */
	public void setEngineTypeCode(String engineTypeCode) {
		this.engineTypeCode = engineTypeCode;
	}
	/**
	 * @return the engineHours
	 */
	public String getEngineHours() {
		return engineHours;
	}
	/**
	 * @param engineHours the engineHours to set
	 */
	public void setEngineHours(String engineHours) {
		this.engineHours = engineHours;
	}
	/**
	 * @return the days
	 */
	public String getDays() {
		return days;
	}
	/**
	 * @param days the days to set
	 */
	public void setDays(String days) {
		this.days = days;
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
