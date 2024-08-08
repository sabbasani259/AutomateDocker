package remote.wise.handler;

import java.io.Serializable;

public class EmailTemplate implements Serializable
{
	String to;
	String subject;
	String body;
	String fileToBeAttached;
	
	//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
	String serialNumber;
	String transactionTime;
	
	public EmailTemplate()
	{
		
	}
	
	public EmailTemplate(String to,String subject,String body,String fileToBeAttached){
		this.to = to;
		this.subject = subject;
		this.body = body;
		this.fileToBeAttached = fileToBeAttached;
		
	}
	
	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}
	/**
	 * @param to the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	public String getFileToBeAttached() {
		return fileToBeAttached;
	}

	public void setFileToBeAttached(String fileToBeAttached) {
		this.fileToBeAttached = fileToBeAttached;
	}
	
	//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
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
	 * @return the transactionTime
	 */
	public String getTransactionTime() {
		return transactionTime;
	}
	/**
	 * @param transactionTime the transactionTime to set
	 */
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}
}
