/**
 * 
 */
package remote.wise.service.datacontract;

/**
 * @author sunayak
 *
 */
public class EAInterfaceRespContract {
	String InterfaceFileName;
	int processedRecord ;
	int rejectedRecord ;
	
	//DF20140813 - Rajani Nagaraju - Web based search for EA File Processing Status
	String interfaceName;
	String status;
	String reasonForRejection;
	String fileName;
	
	
	/**
	 * @return the interfaceFileName
	 */
	public String getInterfaceFileName() {
		return InterfaceFileName;
	}
	/**
	 * @param interfaceFileName the interfaceFileName to set
	 */
	public void setInterfaceFileName(String interfaceFileName) {
		InterfaceFileName = interfaceFileName;
	}
	/**
	 * @return the processedRecord
	 */
	public int getProcessedRecord() {
		return processedRecord;
	}
	/**
	 * @param processedRecord the processedRecord to set
	 */
	public void setProcessedRecord(int processedRecord) {
		this.processedRecord = processedRecord;
	}
	/**
	 * @return the rejectedRecord
	 */
	public int getRejectedRecord() {
		return rejectedRecord;
	}
	/**
	 * @param rejectedRecord the rejectedRecord to set
	 */
	public void setRejectedRecord(int rejectedRecord) {
		this.rejectedRecord = rejectedRecord;
	}
	/**
	 * @return the interfaceName
	 */
	public String getInterfaceName() {
		return interfaceName;
	}
	/**
	 * @param interfaceName the interfaceName to set
	 */
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the reasonForRejection
	 */
	public String getReasonForRejection() {
		return reasonForRejection;
	}
	/**
	 * @param reasonForRejection the reasonForRejection to set
	 */
	public void setReasonForRejection(String reasonForRejection) {
		this.reasonForRejection = reasonForRejection;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
}
