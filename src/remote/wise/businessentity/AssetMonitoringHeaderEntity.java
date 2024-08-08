package remote.wise.businessentity;

import java.sql.Timestamp;

public class AssetMonitoringHeaderEntity extends BaseBusinessEntity
{
	int transactionNumber;
	Timestamp transactionTime;
	AssetEntity serialNumber;
	//Change Made by Rajani - 20130626 - New Column CreatedDate added to AMH to populate the data correctly to OLAP
	Timestamp createdTimestamp;
	RecordTypeEntity recordTypeId;
	String fwVersionNumber;
		
	//DF20141110 - Rajani Nagaraju - To Monitor the same packets being created again and again
	Timestamp lastUpdatedTime;
	int updateCount;
	
	//DF20150330 - Rajani Nagaraju - Adding this column to drive partitioning
	private int segmentId;
	
	/**
	 * @return the segmentId
	 */
	public int getSegmentId() {
		return segmentId;
	}
	/**
	 * @param segmentId the segmentId to set
	 */
	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}
	public int getTransactionNumber() {
		return transactionNumber;
	}
	public void setTransactionNumber(int transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
	public Timestamp getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(Timestamp transactionTime) {
		this.transactionTime = transactionTime;
	}
	public AssetEntity getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(AssetEntity serialNumber) {
		this.serialNumber = serialNumber;
	}
	public Timestamp getCreatedTimestamp() {
		return createdTimestamp;
	}
	public void setCreatedTimestamp(Timestamp createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	public RecordTypeEntity getRecordTypeId() {
		return recordTypeId;
	}
	public void setRecordTypeId(RecordTypeEntity recordTypeId) {
		this.recordTypeId = recordTypeId;
	}
	public String getFwVersionNumber() {
		return fwVersionNumber;
	}
	public void setFwVersionNumber(String fwVersionNumber) {
		this.fwVersionNumber = fwVersionNumber;
	}
	public Timestamp getLastUpdatedTime() {
		return lastUpdatedTime;
	}
	public void setLastUpdatedTime(Timestamp lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}
	public int getUpdateCount() {
		return updateCount;
	}
	public void setUpdateCount(int updateCount) {
		this.updateCount = updateCount;
	}	
	
	
}
