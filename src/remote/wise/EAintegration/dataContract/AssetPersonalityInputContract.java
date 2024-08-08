package remote.wise.EAintegration.dataContract;

import java.io.Serializable;

/**
 * @author Rajani Nagaraju
 *
 */
public class AssetPersonalityInputContract implements Serializable
{
	private String engineNumber;
	private String assetGroupCode;
	private String assetTypeCode;
	private String engineTypeCode;
	private String assetBuiltDate;
	private String make;
	private String fuelCapacity;
	private String serialNumber;
	private String messageId;
	private String fileRef;
	private String process;
	private String reprocessJobCode;
	
	
	/**
	 * @return the engineNumber
	 */
	public String getEngineNumber() {
		return engineNumber;
	}
	/**
	 * @param engineNumber the engineNumber to set
	 */
	public void setEngineNumber(String engineNumber) {
		this.engineNumber = engineNumber;
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
	 * @return the assetBuiltDate
	 */
	public String getAssetBuiltDate() {
		return assetBuiltDate;
	}
	/**
	 * @param assetBuiltDate the assetBuiltDate to set
	 */
	public void setAssetBuiltDate(String assetBuiltDate) {
		this.assetBuiltDate = assetBuiltDate;
	}
	/**
	 * @return the make
	 */
	public String getMake() {
		return make;
	}
	/**
	 * @param make the make to set
	 */
	public void setMake(String make) {
		this.make = make;
	}
	/**
	 * @return the fuelCapacity
	 */
	public String getFuelCapacity() {
		return fuelCapacity;
	}
	/**
	 * @param fuelCapacity the fuelCapacity to set
	 */
	public void setFuelCapacity(String fuelCapacity) {
		this.fuelCapacity = fuelCapacity;
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
