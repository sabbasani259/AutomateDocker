package remote.wise.service.datacontract;

public class NotificationSummaryRespContract {
	
	private int notificationTypeIdList; 
	private String notificationTypeNameList;
	  private int Notificationcountcount;
	  private String serialNumber;
	  private String tenancy_name;
	  private String machineGroupName;
	  private String machineProfileName;
	  
	public int getNotificationTypeIdList() {
		return notificationTypeIdList;
	}
	public void setNotificationTypeIdList(int notificationTypeIdList) {
		this.notificationTypeIdList = notificationTypeIdList;
	}
	public String getNotificationTypeNameList() {
		return notificationTypeNameList;
	}
	public void setNotificationTypeNameList(String notificationTypeNameList) {
		this.notificationTypeNameList = notificationTypeNameList;
	}
	public int getNotificationcountcount() {
		return Notificationcountcount;
	}
	public void setNotificationcountcount(int notificationcountcount) {
		Notificationcountcount = notificationcountcount;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getTenancy_name() {
		return tenancy_name;
	}
	public void setTenancy_name(String tenancy_name) {
		this.tenancy_name = tenancy_name;
	}
	public String getMachineGroupName() {
		return machineGroupName;
	}
	public void setMachineGroupName(String machineGroupName) {
		this.machineGroupName = machineGroupName;
	}
	public String getMachineProfileName() {
		return machineProfileName;
	}
	public void setMachineProfileName(String machineProfileName) {
		this.machineProfileName = machineProfileName;
	}  
}
