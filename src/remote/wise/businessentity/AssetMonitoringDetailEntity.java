package remote.wise.businessentity;

import java.io.Serializable;

public class AssetMonitoringDetailEntity extends BaseBusinessEntity implements Serializable
{
	AssetMonitoringHeaderEntity transactionNumber;
	MonitoringParameters parameterId;
	String parameterValue;
	
	
	public AssetMonitoringHeaderEntity getTransactionNumber() {
		return transactionNumber;
	}
	public void setTransactionNumber(AssetMonitoringHeaderEntity transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
	
	public MonitoringParameters getParameterId() {
		return parameterId;
	}
	public void setParameterId(MonitoringParameters parameterId) {
		this.parameterId = parameterId;
	}
	
	public String getParameterValue() {
		return parameterValue;
	}
	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}
	
}
