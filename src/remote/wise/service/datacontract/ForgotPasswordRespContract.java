/**
 * 
 */
package remote.wise.service.datacontract;

/**
 * @author kprabhu5
 *
 */
public class ForgotPasswordRespContract {

	String password;
	String primaryEmailID;
	String message;
	
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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
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
