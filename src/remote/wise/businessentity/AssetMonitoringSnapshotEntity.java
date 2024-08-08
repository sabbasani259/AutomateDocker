package remote.wise.businessentity;

import java.io.Serializable;
import java.sql.Timestamp;

public class AssetMonitoringSnapshotEntity extends BaseBusinessEntity implements Serializable
{
	AssetMonitoringHeaderEntity transactionNumber;
	Timestamp transactionTime;
	AssetEntity serialNumber;
	String fuelLevel;
	//DF20150119 - Rajani Nagaraju - To capture the latest timestamp of when any packet is received to the server from device for the VIN
	Timestamp latestCreatedTimestamp;
	//DF20150104 - Rajani Nagaraju - To capture the latest Event and log transaction seperately
	AssetMonitoringHeaderEntity latestLogTxn;
	AssetMonitoringHeaderEntity latestEventTxn;
	AssetMonitoringHeaderEntity latestFuelTxn;
	String parameters;

	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public AssetMonitoringHeaderEntity getTransactionNumber() {
		return transactionNumber;
	}
	public void setTransactionNumber(AssetMonitoringHeaderEntity transactionNumber) {
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
	public String getFuelLevel() {
		return fuelLevel;
	}
	public void setFuelLevel(String fuelLevel) {
		this.fuelLevel = fuelLevel;
	}
	/**
	 * @return the latestCreatedTimestamp
	 */
	public Timestamp getLatestCreatedTimestamp() {
		return latestCreatedTimestamp;
	}
	/**
	 * @param latestCreatedTimestamp the latestCreatedTimestamp to set
	 */
	public void setLatestCreatedTimestamp(Timestamp latestCreatedTimestamp) {
		this.latestCreatedTimestamp = latestCreatedTimestamp;
	}
	public AssetMonitoringHeaderEntity getLatestLogTxn() {
		return latestLogTxn;
	}
	public void setLatestLogTxn(AssetMonitoringHeaderEntity latestLogTxn) {
		this.latestLogTxn = latestLogTxn;
	}
	public AssetMonitoringHeaderEntity getLatestEventTxn() {
		return latestEventTxn;
	}
	public void setLatestEventTxn(AssetMonitoringHeaderEntity latestEventTxn) {
		this.latestEventTxn = latestEventTxn;
	}
	public AssetMonitoringHeaderEntity getLatestFuelTxn() {
		return latestFuelTxn;
	}
	public void setLatestFuelTxn(AssetMonitoringHeaderEntity latestFuelTxn) {
		this.latestFuelTxn = latestFuelTxn;
	}
	
	
}
