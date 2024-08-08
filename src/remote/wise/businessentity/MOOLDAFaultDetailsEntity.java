package remote.wise.businessentity;

public class MOOLDAFaultDetailsEntity {

	private int faultID;
	private String serialNumber;
	private String profileData;
	private String rejectionPoint;
	private String createdTimeStamp;

	public int getFaultID() {
		return faultID;
	}
	public void setFaultID(int faultID) {
		this.faultID = faultID;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getProfileData() {
		return profileData;
	}
	public void setProfileData(String profileData) {
		this.profileData = profileData;
	}
	public String getRejectionPoint() {
		return rejectionPoint;
	}
	public void setRejectionPoint(String rejectionPoint) {
		this.rejectionPoint = rejectionPoint;
	}
	public String getCreatedTimeStamp() {
		return createdTimeStamp;
	}
	public void setCreatedTimeStamp(String createdTimeStamp) {
		this.createdTimeStamp = createdTimeStamp;
	}
}
