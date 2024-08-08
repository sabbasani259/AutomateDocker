package remote.wise.service.datacontract;

public class AssetEventRespContract
{	
	private String ParameterName;
	private String TransactionTime;
	private int SequenceId;
	private String ParameterValue;
	private String latitude;
	private String longitude;
	private String alertSeverity;
	
	public AssetEventRespContract()
	{
		ParameterName=null;
		TransactionTime=null;
		SequenceId=0;
		ParameterValue=null;
		latitude=null;
		longitude=null;
		alertSeverity=null;
	}
	
	
	

	/**
	 * @return the parameterName
	 */
	public String getParameterName() {
		return ParameterName;
	}
	/**
	 * @param parameterName the parameterName to set
	 */
	public void setParameterName(String parameterName) {
		ParameterName = parameterName;
	}
	/**
	 * @return the transactionTime
	 */
	public String getTransactionTime() {
		return TransactionTime;
	}
	/**
	 * @param transactionTime the transactionTime to set
	 */
	public void setTransactionTime(String transactionTime) {
		TransactionTime = transactionTime;
	}
	/**
	 * @return the sequenceId
	 */
	public int getSequenceId() {
		return SequenceId;
	}
	/**
	 * @param sequenceId the sequenceId to set
	 */
	public void setSequenceId(int sequenceId) {
		SequenceId = sequenceId;
	}
	/**
	 * @return the parameterValue
	 */
	public String getParameterValue() {
		return ParameterValue;
	}
	/**
	 * @param parameterValue the parameterValue to set
	 */
	public void setParameterValue(String parameterValue) {
		ParameterValue = parameterValue;
	}
	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
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
	
}
