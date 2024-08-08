/**
 * 
 */
package remote.wise.service.datacontract;

/**
 * @author sunayak
 *
 */
public class RegConfirmationRespContract {



	String IMEINumber;
	String phoneNumber;
	boolean flag;
	String userID;
	/**
	 * @return the iMEINumber
	 */
	public String getIMEINumber() {
		return IMEINumber;
	}
	/**
	 * @param iMEINumber the iMEINumber to set
	 */
	public void setIMEINumber(String iMEINumber) {
		IMEINumber = iMEINumber;
	}
	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}
	/**
	 * @param userID the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}
	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	/**
	 * @return the flag
	 */
	public boolean isFlag() {
		return flag;
	}
	/**
	 * @param flag the flag to set
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	

}
