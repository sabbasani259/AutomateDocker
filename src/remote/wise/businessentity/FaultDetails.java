package remote.wise.businessentity;

import java.sql.Timestamp;

public class FaultDetails extends BaseBusinessEntity
{
	private String messageId;
	private String messageString;
	private String fileName;
	private String process;
	private String reprocessJobCode;
	private Timestamp reprocessTimeStamp;
	private int failureCounter;
	private String failureCause;
	
	//DF20180205:KO369761 - rejection point and error code were introduced in fault details table.
	private String rejectionPoint;
	private InterfaceErrorCodes errorCode;

	//DF20181004-KO369761 - Deletion flag field added.
	private int deletionFlag;

	public int getDeletionFlag() {
		return deletionFlag;
	}
	public void setDeletionFlag(int deletionFlag) {
		this.deletionFlag = deletionFlag;
	}

	public String getRejectionPoint() {
		return rejectionPoint;
	}
	public void setRejectionPoint(String rejectionPoint) {
		this.rejectionPoint = rejectionPoint;
	}
	public InterfaceErrorCodes getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(InterfaceErrorCodes errorCode) {
		this.errorCode = errorCode;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getMessageString() {
		return messageString;
	}
	public void setMessageString(String messageString) {
		this.messageString = messageString;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
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
	public Timestamp getReprocessTimeStamp() {
		return reprocessTimeStamp;
	}
	public void setReprocessTimeStamp(Timestamp reprocessTimeStamp) {
		this.reprocessTimeStamp = reprocessTimeStamp;
	}
	public int getFailureCounter() {
		return failureCounter;
	}
	public void setFailureCounter(int failureCounter) {
		this.failureCounter = failureCounter;
	}
	public String getFailureCause() {
		return failureCause;
	}
	public void setFailureCause(String failureCause) {
		this.failureCause = failureCause;
	}


}
