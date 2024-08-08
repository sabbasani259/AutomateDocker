/**
 * 
 */
package remote.wise.service.datacontract;

/**
 * @author kprabhu5
 *
 */
public class ForgotLoginIDRespContract {
	String primaryEmailID;
	String message;
	String loginID;
	/**
	 * @return the loginID
	 */
	public String getLoginID() {
		return loginID;
	}
	/**
	 * @param loginID the loginID to set
	 */
	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the primaryEmailID
	 */
	public String getPrimaryEmailID() {
		return primaryEmailID;
	}
	/**
	 * @param primaryEmailID the primaryEmailID to set
	 */
	public void setPrimaryEmailID(String primaryEmailID) {
		this.primaryEmailID = primaryEmailID;
	}


}
