package remote.wise.service.datacontract;

/**
 * @author Rajani Nagaraju
 *
 */
public class AssetOwnershipReqContract 
{
	String serialNumber;

	public AssetOwnershipReqContract()
	{
		serialNumber=null;
	}
	/**
	 * @return Returns serialNumber as String
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
	
	
}
