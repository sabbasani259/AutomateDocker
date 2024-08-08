package remote.wise.businessentity;

import java.sql.Timestamp;

public class AssetMonitoringEntity extends BaseBusinessEntity 
{
	private int transactionNumber;
	private AssetEntity serialNumber;
	private  MonitoringParameters parameterId;
	private String parameterValue;
	private Timestamp transactionTime;
	

	public AssetMonitoringEntity(){
		
	}
	
	
	public AssetMonitoringEntity(String SerialNumber){
		super.key = new String(SerialNumber);
		AssetMonitoringEntity e = (AssetMonitoringEntity)read(this);
	setParameterId(e.parameterId);
	setParameterValue(e.parameterValue);
	setSerialNumber(e.serialNumber);
	setTransactionNumber(e.transactionNumber);
	setTransactionTime(e.transactionTime);
	}


	public AssetEntity getSerialNumber() {
		return serialNumber;
	}


	public void setSerialNumber(AssetEntity serialNumber) {
		this.serialNumber = serialNumber;
	}


	public MonitoringParameters getParameterId() {
		return parameterId;
	}


	public void setParameterId(MonitoringParameters parameterId) {
		this.parameterId = parameterId;
	}


	public int getTransactionNumber() {
		return transactionNumber;
	}


	public void setTransactionNumber(int transactionNumber) {
		this.transactionNumber = transactionNumber;
	}


	public String getParameterValue() {
		return parameterValue;
	}


	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}


	public Timestamp getTransactionTime() {
		return transactionTime;
	}


	public void setTransactionTime(Timestamp transactionTime) {
		this.transactionTime = transactionTime;
	}
}
