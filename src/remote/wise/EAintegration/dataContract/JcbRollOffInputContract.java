package remote.wise.EAintegration.dataContract;

import java.io.Serializable;

public class JcbRollOffInputContract implements Serializable
{
	String serialNumber;
	String engineNumber;
	String chasisNumber;
	String messageId;
	String fileRef;
	String process;
	String reprocessJobCode;
	String make;
	String builtDate;
	String machineNumber;
	
	
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
	 * @return the builtDate
	 */
	public String getBuiltDate() {
		return builtDate;
	}
	/**
	 * @param builtDate the builtDate to set
	 */
	public void setBuiltDate(String builtDate) {
		this.builtDate = builtDate;
	}
	/**
	 * @return the machineNumber
	 */
	public String getMachineNumber() {
		return machineNumber;
	}
	/**
	 * @param machineNumber the machineNumber to set
	 */
	public void setMachineNumber(String machineNumber) {
		this.machineNumber = machineNumber;
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
	 * @return the chasisNumber
	 */
	public String getChasisNumber() {
		return chasisNumber;
	}
	/**
	 * @param chasisNumber the chasisNumber to set
	 */
	public void setChasisNumber(String chasisNumber) {
		this.chasisNumber = chasisNumber;
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
