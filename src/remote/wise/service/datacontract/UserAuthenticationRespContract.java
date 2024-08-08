package remote.wise.service.datacontract;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserAuthenticationRespContract 
{
	String user_name;
	String loginId;
	String last_login_date;
	String role_name;
	int roleId;
	int isTenancyAdmin;
	int sysGeneratedPassword;
	boolean isSMS;
	boolean isMap;
	//CR469.n
	int pwdExpired;
	public int getPwdExpired() {
		return pwdExpired;
	}
	public void setPwdExpired(int pwdExpired) {
		this.pwdExpired = pwdExpired;
	}
	/**
	 * @return the sysGeneratedPassword
	 */
	public int getSysGeneratedPassword() {
		return sysGeneratedPassword;
	}
	/**
	 * @param sysGeneratedPassword the sysGeneratedPassword to set
	 */
	public void setSysGeneratedPassword(int sysGeneratedPassword) {
		this.sysGeneratedPassword = sysGeneratedPassword;
	}
	public int getIsTenancyAdmin() {
		return isTenancyAdmin;
	}
	public void setIsTenancyAdmin(int isTenancyAdmin) {
		this.isTenancyAdmin = isTenancyAdmin;
	}
	//HashMap<String, HashMap<Integer,String>> tenancyNameIDProxyUser = new HashMap<String, HashMap<Integer,String>>();
	List<String> tenancyNameIDProxyUser ;
	
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getLast_login_date() {
		return last_login_date;
	}
	public void setLast_login_date(String last_login_date) {
		this.last_login_date = last_login_date;
	}
	public String getRole_name() {
		return role_name;
	}
	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public List<String> getTenancyNameIDProxyUser() {
		return tenancyNameIDProxyUser;
	}
	public void setTenancyNameIDProxyUser(List<String> tenancyNameIDProxyUser) {
		this.tenancyNameIDProxyUser = tenancyNameIDProxyUser;
	}
	public boolean isSMS() {
		return isSMS;
	}
	public void setSMS(boolean isSMS) {
		this.isSMS = isSMS;
	}
	
	
	public boolean isMap() {
		return isMap;
	}
	public void setMap(boolean isMap) {
		this.isMap = isMap;
	}		
	
}
