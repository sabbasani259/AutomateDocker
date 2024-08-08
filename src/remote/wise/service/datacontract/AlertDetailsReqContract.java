package remote.wise.service.datacontract;

/** Request Contract for AlertDetailsService
 * @author Rajani Nagaraju
 *
 */
public class AlertDetailsReqContract 
{
	String serialNumber;
	int assetEventId;
	
	public AlertDetailsReqContract()
	{
		serialNumber = null;
		assetEventId=0;
	}
	
	/**
	 * @return returns SerialNumber as String
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber VIN as String input
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	
	/**
	 * @return assetEventId as integer
	 */
	public int getAssetEventId() {
		return assetEventId;
	}
	
	/**
	 * @param assetEventId assetEventId of eventHistory records
	 */
	public void setAssetEventId(int assetEventId) {
		this.assetEventId = assetEventId;
	}
		
}
