package remote.wise.service.datacontract;

/**
 * @author Rajani Nagaraju
 *
 */
public class AssetOwnershipRespContract 
{
	int OemAccountId;
	int dealerAccountId;
	int customerAccountId;
	String serialNumber;
	String dealerName;
	String dealerPhoneNumber;
	String dealerEmail;
	String customerName;
	String customerPhoneNumber;
	String customerEmail;
	String driverName;
	String driverPhoneNumber;
	
	public AssetOwnershipRespContract()
	{
		OemAccountId=0;
		dealerAccountId=0;
		customerAccountId=0;
		serialNumber = null;
		dealerName=null;
		dealerPhoneNumber=null;
		dealerEmail=null;
		customerName=null;
		customerPhoneNumber=null;
		customerEmail=null;
		driverName=null;
		driverPhoneNumber=null;
		
	}
	
	/**
	 * @return AccountId of OEM
	 */
	public int getOemAccountId() {
		return OemAccountId;
	}
	/**
	 * @param oemAccountId AccountId of OEM as integer input
	 */
	public void setOemAccountId(int oemAccountId) {
		OemAccountId = oemAccountId;
	}
	
	
	/**
	 * @return AccountId of Dealer
	 */
	public int getDealerAccountId() {
		return dealerAccountId;
	}
	/**
	 * @param dealerAccountId AccountId of Dealer as integer input
	 */
	public void setDealerAccountId(int dealerAccountId) {
		this.dealerAccountId = dealerAccountId;
	}
	
	
	/**
	 * @return AccountId of Customer
	 */
	public int getCustomerAccountId() {
		return customerAccountId;
	}
	/**
	 * @param customerAccountId AccountId of Customer as integer input
	 */
	public void setCustomerAccountId(int customerAccountId) {
		this.customerAccountId = customerAccountId;
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
	 * @return Name of the dealer as String
	 */
	public String getDealerName() {
		return dealerName;
	}
	/**
	 * @param dealerName Name of the dealer as String input
	 */
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	
	/**
	 * @return contact Number (Primary Mobile Number) of dealer
	 */
	public String getDealerPhoneNumber() {
		return dealerPhoneNumber;
	}
	/**
	 * @param dealerPhoneNumber contact Number (Primary Mobile Number) of dealer as String input
	 */
	public void setDealerPhoneNumber(String dealerPhoneNumber) {
		this.dealerPhoneNumber = dealerPhoneNumber;
	}

	
	/**
	 * @return EmailId of dealer
	 */
	public String getDealerEmail() {
		return dealerEmail;
	}
	/**
	 * @param dealerEmail EmailId of dealer as String input
	 */
	public void setDealerEmail(String dealerEmail) {
		this.dealerEmail = dealerEmail;
	}

	
	/**
	 * @return Name of the Customer as String
	 */
	public String getCustomerName() {
		return customerName;
	}
	/**
	 * @param customerName Name of the Customer as String input
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	
	/**
	 * @return contact Number (Primary Mobile Number) of Customer
	 */
	public String getCustomerPhoneNumber() {
		return customerPhoneNumber;
	}
	/**
	 * @param customerPhoneNumber contact Number (Primary Mobile Number) of Customer as String input
	 */
	public void setCustomerPhoneNumber(String customerPhoneNumber) {
		this.customerPhoneNumber = customerPhoneNumber;
	}

	
	/**
	 * @return EmailId of Customer
	 */
	public String getCustomerEmail() {
		return customerEmail;
	}
	/**
	 * @param customerEmail EmailId of Customer as String input
	 */
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	
	
	/**
	 * @return Name of the Operator of machine as String
	 */
	public String getDriverName() {
		return driverName;
	}
	/**
	 * @param driverName Name of the Operator of machine as String input
	 */
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	
	
	/**
	 * @return contact Number of the Operator of machine
	 */
	public String getDriverPhoneNumber() {
		return driverPhoneNumber;
	}
	/**
	 * @param driverPhoneNumber contact Number of the Operator of machine as String input
	 */
	public void setDriverPhoneNumber(String driverPhoneNumber) {
		this.driverPhoneNumber = driverPhoneNumber;
	}
	
	
}
