package remote.wise.service.datacontract;

/** This class specifies the details of EA fault messages
 * @author Rajani Nagaraju
 *
 */
public class EAfaultMsgDetailsRespContract 
{
	String messageId;
	String messageString;
	String reprocessJobCode;
	String process;
	String fileName;
	int failureCounter;
	String reprocessTimeStamp;
	int sequence;
	
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
	 * @return the messageString
	 */
	public String getMessageString() {
		return messageString;
	}
	/**
	 * @param messageString the messageString to set
	 */
	public void setMessageString(String messageString) {
		this.messageString = messageString;
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
	
	/**
	 * @return the reprocessTimeStamp
	 */
	public String getReprocessTimeStamp() {
		return reprocessTimeStamp;
	}
	/**
	 * @param reprocessTimeStamp the reprocessTimeStamp to set
	 */
	public void setReprocessTimeStamp(String reprocessTimeStamp) {
		this.reprocessTimeStamp = reprocessTimeStamp;
	}
	/**
	 * @return the failureCounter
	 */
	public int getFailureCounter() {
		return failureCounter;
	}
	/**
	 * @param failureCounter the failureCounter to set
	 */
	public void setFailureCounter(int failureCounter) {
		this.failureCounter = failureCounter;
	}
	/**
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}
	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	
	
}
