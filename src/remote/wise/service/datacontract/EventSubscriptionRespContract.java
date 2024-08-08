package remote.wise.service.datacontract;

/**
 * @author Rajani Nagaraju
 *
 */
public class EventSubscriptionRespContract 
{
	String serialNumber;
	String contactId;
	String primaryMobileNumber;
	String primaryEmailId;
	String userName;
	//Defect Id: 902 - Rajani Nagaraju - 20130705 - DealerUser as first PrimarySMS contact.
	boolean isDealerUser;
//	Keerthi : Defect ID : 1177 :Receiver 1,2,3
	int priority;
	
	public EventSubscriptionRespContract()
	{
		serialNumber = null;
		contactId = null;
		primaryMobileNumber = null;
		primaryEmailId = null;
		userName = null;
		isDealerUser = false;
	}
	
	/**
	 * @return serialNumber as String
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
	 * @return contactId as String
	 */
	public String getContactId() {
		return contactId;
	}
	/**
	 * @param contactId contactId as String input
	 */
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	
	
	/**
	 * @return primaryMobileNumber of the contact as String
	 */
	public String getPrimaryMobileNumber() {
		return primaryMobileNumber;
	}
	/**
	 * @param primaryMobileNumber contactNumber as String input
	 */
	public void setPrimaryMobileNumber(String primaryMobileNumber) {
		this.primaryMobileNumber = primaryMobileNumber;
	}
	
	
	/**
	 * @return PrimaryEmailId of the contact as String
	 */
	public String getPrimaryEmailId() {
		return primaryEmailId;
	}
	/**
	 * @param primaryEmailId emailId of the contact as String input
	 */
	public void setPrimaryEmailId(String primaryEmailId) {
		this.primaryEmailId = primaryEmailId;
	}
	
	
	/**
	 * @return Name of the contact as String
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName Name of the contact as String input
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	//Defect Id: 902 - Rajani Nagaraju - 20130705 - DealerUser as first PrimarySMS contact.
	/**
	 * @return the isDealerUser
	 */
	public boolean isDealerUser() {
		return isDealerUser;
	}

	/**
	 * @param isDealerUser the isDealerUser to set
	 */
	public void setDealerUser(boolean isDealerUser) {
		this.isDealerUser = isDealerUser;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	
}
