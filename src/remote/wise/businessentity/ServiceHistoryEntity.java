package remote.wise.businessentity;

import java.io.Serializable;
import java.sql.Timestamp;

public class ServiceHistoryEntity  extends BaseBusinessEntity implements Serializable{

	public ServiceHistoryEntity(){
		
	}
	
	private String serviceTicketNumber;
    private AssetEntity serialNumber;
    private Timestamp serviceDate;
    private AccountEntity dealerId;
    private String attendedBy;
    private Timestamp nextServiceDate;
    private String comments;
    private String actionTaken;
    private ServiceTypeEntity serviceTypeId;
    private String serviceName, ScheduleName;
    private String dbmsPartCode;
    //added by S Suresh to insert newly added column service schedule ID into the service history table
    private int serviceScheduleId;
    private String CMH;
    
  //DF20191220:Abhishek:: added callTypeId to close service alert.
    private String callTypeId;
    
    
    
	public String getCallTypeId() {
		return callTypeId;
	}
	public void setCallTypeId(String callTypeId) {
		this.callTypeId = callTypeId;
	}
    
    
    
	/**
	 * @return the cMH
	 */
	public String getCMH() {
		return CMH;
	}
	/**
	 * @param cMH the cMH to set
	 */
	public void setCMH(String cMH) {
		CMH = cMH;
	}
	public int getServiceScheduleId() {
		return serviceScheduleId;
	}
	public void setServiceScheduleId(int serviceScheduleId) {
		this.serviceScheduleId = serviceScheduleId;
	}
    
    public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getScheduleName() {
		return ScheduleName;
	}
	public void setScheduleName(String scheduleName) {
		ScheduleName = scheduleName;
	}
	public String getServiceTicketNumber() {
		return serviceTicketNumber;
	}
	public void setServiceTicketNumber(String serviceTicketNumber) {
		this.serviceTicketNumber = serviceTicketNumber;
	}

	public AssetEntity getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(AssetEntity serialNumber) {
		this.serialNumber = serialNumber;
	}
	public Timestamp getServiceDate() {
		return serviceDate;
	}
	public void setServiceDate(Timestamp serviceDate) {
		this.serviceDate = serviceDate;
	}
	public AccountEntity getDealerId() {
		return dealerId;
	}
	public void setDealerId(AccountEntity dealerId) {
		this.dealerId = dealerId;
	}
	public String getAttendedBy() {
		return attendedBy;
	}
	public void setAttendedBy(String attendedBy) {
		this.attendedBy = attendedBy;
	}
	public Timestamp getNextServiceDate() {
		return nextServiceDate;
	}
	public void setNextServiceDate(Timestamp nextServiceDate) {
		this.nextServiceDate = nextServiceDate;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getActionTaken() {
		return actionTaken;
	}
	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}
	public ServiceTypeEntity getServiceTypeId() {
		return serviceTypeId;
	}
	public void setServiceTypeId(ServiceTypeEntity serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}
	public AssetFaultEntity getFaultId() {
		return faultId;
	}
	public void setFaultId(AssetFaultEntity faultId) {
		this.faultId = faultId;
	}
	
	/**
	 * @return the dbmsPartCode
	 */
	public String getDbmsPartCode() {
		return dbmsPartCode;
	}
	/**
	 * @param dbmsPartCode the dbmsPartCode to set
	 */
	public void setDbmsPartCode(String dbmsPartCode) {
		this.dbmsPartCode = dbmsPartCode;
	}

	private AssetFaultEntity faultId;
    
	/*public ServiceHistoryEntity(String SerialNumber)
	{
		super.key=new String(SerialNumber);
		AssetControlUnitEntity Obj = (AssetControlUnitEntity)read(this);
		if(Obj!=null)
		{
			setSerialNumber(Obj.getSerial_Number());
			
			
		}

}*/
}
