/**
 * 
 */
package remote.wise.service.datacontract;

import java.io.Serializable;

/**
 * @author sunayak
 *
 */
public class HAJAssetContactDetailsRespContract implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int accountid;
	private String contactid;
	private String firstname;
	private String lastname;
	private String phonenumber;
	private String emailid;
	private String password;

	
	
	public int getAccountid() {
		return accountid;
	}
	public void setAccountid(int accountid) {
		this.accountid = accountid;
	}
	public String getContactid() {
		return contactid;
	}
	public void setContactid(String contactid) {
		this.contactid = contactid;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getEmailid() {
		return emailid;
	}
	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	

}
