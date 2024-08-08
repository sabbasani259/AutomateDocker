/**
 * CR308 : 20220613 : Dhiraj K : Code Fix for BW service closures from Portal
 */
package remote.wise.service.datacontract;

/**
 * @author Rajani Nagaraju
 *
 */
public class UserAlertsRespContract 
{
	String serialNumber;
	int alertTypeId;
	String alertTypeName;
	String alertDescription;
	String latestReceivedTime;
	String alertSeverity;
	int alertCounter;
	String remarks;
	int assetEventId;

	//CR308.sn
	String serviceName;
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String string) {
		this.serviceName = string;
	}
	//CR308.en

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
	 * @return the alertTypeId
	 */
	public int getAlertTypeId() {
		return alertTypeId;
	}
	/**
	 * @param alertTypeId the alertTypeId to set
	 */
	public void setAlertTypeId(int alertTypeId) {
		this.alertTypeId = alertTypeId;
	}
	/**
	 * @return the alertTypeName
	 */
	public String getAlertTypeName() {
		return alertTypeName;
	}
	/**
	 * @param alertTypeName the alertTypeName to set
	 */
	public void setAlertTypeName(String alertTypeName) {
		this.alertTypeName = alertTypeName;
	}
	/**
	 * @return the alertDescription
	 */
	public String getAlertDescription() {
		return alertDescription;
	}
	/**
	 * @param alertDescription the alertDescription to set
	 */
	public void setAlertDescription(String alertDescription) {
		this.alertDescription = alertDescription;
	}
	/**
	 * @return the latestReceivedTime
	 */
	public String getLatestReceivedTime() {
		return latestReceivedTime;
	}
	/**
	 * @param latestReceivedTime the latestReceivedTime to set
	 */
	public void setLatestReceivedTime(String latestReceivedTime) {
		this.latestReceivedTime = latestReceivedTime;
	}
	/**
	 * @return the alertSeverity
	 */
	public String getAlertSeverity() {
		return alertSeverity;
	}
	/**
	 * @param alertSeverity the alertSeverity to set
	 */
	public void setAlertSeverity(String alertSeverity) {
		this.alertSeverity = alertSeverity;
	}
	/**
	 * @return the alertCounter
	 */
	public int getAlertCounter() {
		return alertCounter;
	}
	/**
	 * @param alertCounter the alertCounter to set
	 */
	public void setAlertCounter(int alertCounter) {
		this.alertCounter = alertCounter;
	}
	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}
	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	/**
	 * @return the assetEventId
	 */
	public int getAssetEventId() {
		return assetEventId;
	}
	/**
	 * @param assetEventId the assetEventId to set
	 */
	public void setAssetEventId(int assetEventId) {
		this.assetEventId = assetEventId;
	}
	
	
}
