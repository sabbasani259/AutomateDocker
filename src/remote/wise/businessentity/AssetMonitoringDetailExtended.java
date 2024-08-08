package remote.wise.businessentity;

import java.io.Serializable;

/**
 * @author Rajani Nagaraju
 *
 */
public class AssetMonitoringDetailExtended extends BaseBusinessEntity implements Serializable
{
	AssetMonitoringHeaderEntity transactionNumber;
	ExtendedMonitoringParameters extendedParameterId;
	String extendedParameterValue;
	
	/**
	 * @return the transactionNumber
	 */
	public AssetMonitoringHeaderEntity getTransactionNumber() {
		return transactionNumber;
	}
	/**
	 * @param transactionNumber the transactionNumber to set
	 */
	public void setTransactionNumber(AssetMonitoringHeaderEntity transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
	/**
	 * @return the extendedParameterId
	 */
	public ExtendedMonitoringParameters getExtendedParameterId() {
		return extendedParameterId;
	}
	/**
	 * @param extendedParameterId the extendedParameterId to set
	 */
	public void setExtendedParameterId(
			ExtendedMonitoringParameters extendedParameterId) {
		this.extendedParameterId = extendedParameterId;
	}
	/**
	 * @return the extendedParameterValue
	 */
	public String getExtendedParameterValue() {
		return extendedParameterValue;
	}
	/**
	 * @param extendedParameterValue the extendedParameterValue to set
	 */
	public void setExtendedParameterValue(String extendedParameterValue) {
		this.extendedParameterValue = extendedParameterValue;
	}
	
	
}
