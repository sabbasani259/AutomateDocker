package remote.wise.service.datacontract;
//DF20190307 :Z1007653: New contract file for interfaces
public class InterfaceDetailsPOJOForEmail {
	private String fileName;
	private String interfaceFileName;
	private String interfaceName;
	private int processedRecord;
	private String reasonForRejection;
	private int rejectedRecord;
	private String status;
	private String interfaceFileNameprocessedTime;
	private String interfaceNameprocessedTime;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getInterfaceFileName() {
		return interfaceFileName;
	}
	public void setInterfaceFileName(String interfaceFileName) {
		this.interfaceFileName = interfaceFileName;
	}
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public int getProcessedRecord() {
		return processedRecord;
	}
	public void setProcessedRecord(int processedRecord) {
		this.processedRecord = processedRecord;
	}
	public String getReasonForRejection() {
		return reasonForRejection;
	}
	public void setReasonForRejection(String reasonForRejection) {
		this.reasonForRejection = reasonForRejection;
	}
	public int getRejectedRecord() {
		return rejectedRecord;
	}
	public void setRejectedRecord(int rejectedRecord) {
		this.rejectedRecord = rejectedRecord;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getInterfaceFileNameprocessedTime() {
		return interfaceFileNameprocessedTime;
	}
	public void setInterfaceFileNameprocessedTime(
			String interfaceFileNameprocessedTime) {
		this.interfaceFileNameprocessedTime = interfaceFileNameprocessedTime;
	}
	public String getInterfaceNameprocessedTime() {
		return interfaceNameprocessedTime;
	}
	public void setInterfaceNameprocessedTime(String interfaceNameprocessedTime) {
		this.interfaceNameprocessedTime = interfaceNameprocessedTime;
	}
}
