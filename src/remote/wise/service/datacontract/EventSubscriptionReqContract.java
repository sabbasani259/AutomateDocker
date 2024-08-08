package remote.wise.service.datacontract;

import java.util.List;

/**
 * @author Rajani Nagaraju
 *
 */
public class EventSubscriptionReqContract 
{
	String serialNumber;
	List<String> primaryContactList;
	
	public EventSubscriptionReqContract()
	{
		serialNumber=null;
		primaryContactList = null;
	}
	
	
	/**
	 * @return Returns SerialNumber as String
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
	 * @return contactList as String
	 */
	public List<String> getPrimaryContactList() {
		return primaryContactList;
	}
	
	/**
	 * @param primaryContactList contactList as String input
	 */
	public void setPrimaryContactList(List<String> primaryContactList) {
		this.primaryContactList = primaryContactList;
	}
	
	
}
