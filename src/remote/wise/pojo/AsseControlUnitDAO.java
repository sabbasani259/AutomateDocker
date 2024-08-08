package remote.wise.pojo;

import java.sql.Timestamp;

import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.ControlUnitEntity;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class AsseControlUnitDAO {

	private String serialNumber;
	private ControlUnitEntity controlUnitId;
	private String simNo;
	private String imeiNo;
	private Timestamp registrationDate;
	//CR481-20240731-Sai Divya-Status,proposedFWVersion,fWVersion columns are added to NMT
//	private String status;
//	private String proposedFWVersion;
//	private String fWVersion;
	//DF20200429 - Zakir : New coloumn update over new machine tab
    private String comment;

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
//    public String getStatus() {
//		return status;
//	}
//	public void setStatus(String status) {
//		
//		this.status = status;
//		System.out.println(status);
//	}
//	public String getProposedFWVersion() {
//		return proposedFWVersion;
//		
//	}
//	public void setProposedFWVersion(String proposedFWVersion) {
//		this.proposedFWVersion = proposedFWVersion;
//		
//	}
//	
//	public String getfWVersion() {
//		return fWVersion;
//	}
//	public void setfWVersion(String fWVersion) {
//		this.fWVersion = fWVersion;
//		
//	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public ControlUnitEntity getControlUnitId() {
		return controlUnitId;
	}
	public void setControlUnitId(ControlUnitEntity controlUnitId) {
		this.controlUnitId = controlUnitId;
	}
	public String getSimNo() {
		return simNo;
	}
	public void setSimNo(String simNo) {
		this.simNo = simNo;
	}
	public String getImeiNo() {
		return imeiNo;
	}
	public void setImeiNo(String imeiNo) {
		this.imeiNo = imeiNo;
	}

	public Timestamp getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Timestamp registrationDate) {
		this.registrationDate = registrationDate;
	}
//	@Override
//	public String toString() {
//		return "AsseControlUnitDAO [status=" + status + ", proposedFWVersion=" + proposedFWVersion + ", fWVersion="
//				+ fWVersion + ", comment=" + comment + "]";
//	}
	
	
}
