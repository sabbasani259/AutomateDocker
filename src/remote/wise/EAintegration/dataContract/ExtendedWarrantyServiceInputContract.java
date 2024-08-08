package remote.wise.EAintegration.dataContract;

public class ExtendedWarrantyServiceInputContract {

	private String serialNumber;
	private String callTypeId;
	private String monthlyVisit;
	private String cancellationFlag;
	private String fileRef;
	private String process;
	private String reprocessJobCode;
	private String messageId;
	
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getCallTypeId() {
		return callTypeId;
	}
	public void setCallTypeId(String callTypeId) {
		this.callTypeId = callTypeId;
	}
	public String getMonthlyVisit() {
		return monthlyVisit;
	}
	public void setMonthlyVisit(String monthlyVisit) {
		this.monthlyVisit = monthlyVisit;
	}
	public String getCancellationFlag() {
		return cancellationFlag;
	}
	public void setCancellationFlag(String cancellationFlag) {
		this.cancellationFlag = cancellationFlag;
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
