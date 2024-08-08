package remote.wise.service.datacontract;

/** This class specifies the filters for getting message fault details
 * @author Rajani Nagaraju
 *
 */
public class EAfaultMsgDetailsReqContract 
{
	String reprocessTimeStamp;
	String reprocessJobCode;
	String messageId;
	
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
	
	
}
