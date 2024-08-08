package remote.wise.businessentity;

import java.sql.Timestamp;

public class EALogDetailsEntity extends BaseBusinessEntity
{
	String fileName;
	String process;
	Timestamp processedTimestamp;
	int noOfRecords;
	
	
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
	 * @return the processedTimestamp
	 */
	public Timestamp getProcessedTimestamp() {
		return processedTimestamp;
	}
	/**
	 * @param processedTimestamp the processedTimestamp to set
	 */
	public void setProcessedTimestamp(Timestamp processedTimestamp) {
		this.processedTimestamp = processedTimestamp;
	}
	/**
	 * @return the noOfRecords
	 */
	public int getNoOfRecords() {
		return noOfRecords;
	}
	/**
	 * @param noOfRecords the noOfRecords to set
	 */
	public void setNoOfRecords(int noOfRecords) {
		this.noOfRecords = noOfRecords;
	}
	
	
}
