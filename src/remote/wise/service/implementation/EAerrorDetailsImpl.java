package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

////import org.apache.log4j.Logger;

import remote.wise.businessobject.EnterpriseApplicationDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.EAfaultMsgDetailsReqContract;
import remote.wise.service.datacontract.EAfaultMsgDetailsRespContract;

public class EAerrorDetailsImpl 
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
	
	
	public List<EAfaultMsgDetailsRespContract> getFaultMessageDetails(EAfaultMsgDetailsReqContract reqObj)
	{
		List<EAfaultMsgDetailsRespContract> responseList = new LinkedList<EAfaultMsgDetailsRespContract>();
		
		List<EAerrorDetailsImpl>  respObj = new EnterpriseApplicationDetailsBO().getFaultMessageDetails(reqObj.getReprocessTimeStamp(), 
				reqObj.getReprocessJobCode(), reqObj.getMessageId());
		
		for(int i=0; i<respObj.size(); i++)
		{
			EAfaultMsgDetailsRespContract response = new EAfaultMsgDetailsRespContract();
			response.setFailureCounter(respObj.get(i).getFailureCounter());
			response.setFileName(respObj.get(i).getFileName());
			response.setMessageId(respObj.get(i).getMessageId());
			response.setMessageString(respObj.get(i).getMessageString());
			response.setProcess(respObj.get(i).getProcess());
			response.setReprocessJobCode(respObj.get(i).getReprocessJobCode());
			response.setReprocessTimeStamp(respObj.get(i).getReprocessTimeStamp());
			response.setSequence(respObj.get(i).getSequence());
			
			responseList.add(response);
		}
		
		return responseList;
	}
	
	
	public String setFaultMsgReprocessDate(List<EAfaultMsgDetailsReqContract> reqObj)
	{
		String status = "FAILURE";
		
		status = new EnterpriseApplicationDetailsBO().setFaultReprocessDate(reqObj);
		
		
		return status;
	}
}
