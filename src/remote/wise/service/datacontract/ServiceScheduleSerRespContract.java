package remote.wise.service.datacontract;

import java.sql.Timestamp;

public class ServiceScheduleSerRespContract {
	
	private String accountName;
	//private int accountId;
	private String serviceName;
	private String scheduleName;
	
	private int serviceDueDate;
	private long serviceDueHours;
	//private String dealer;
	private float hoursToNextService;
	
	//private int assetGroupId;
	//private int productId;
//imput of set
	private String serialNumber;
    private String instalationDate;
	
	public String getInstalationDate() {
		return instalationDate;
	}

	public void setInstalationDate(String instalationDate) {
		this.instalationDate = instalationDate;
	}
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	
	/*public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getAssetGroupId() {
		return assetGroupId;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public void setAssetGroupId(int assetGroupId) {
		this.assetGroupId = assetGroupId;
	}*/

	public float getHoursToNextService() {
		return hoursToNextService;
	}

	public void setHoursToNextService(float hoursToNextService) {
		this.hoursToNextService = hoursToNextService;
	}

	/*public String getDealer() {
		return dealer;
	}

	public void setDealer(String dealer) {
		this.dealer = dealer;
	}*/

	public int getServiceDueDate() {
		return serviceDueDate;
	}

	public void setServiceDueDate(int serviceDueDate) {
		this.serviceDueDate = serviceDueDate;
	}

	public long getServiceDueHours() {
		return serviceDueHours;
	}

	public void setServiceDueHours(long serviceDueHours) {
		this.serviceDueHours = serviceDueHours;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	
	
}
