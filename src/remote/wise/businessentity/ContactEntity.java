package remote.wise.businessentity;
/*
 * JCB6622 : 20240805 : Dhiraj Kumar : Email Flooding security fix
 */
import java.sql.Timestamp;
import java.util.HashSet;
import java.lang.reflect.*;
import java.util.Set;

public class ContactEntity extends BaseBusinessEntity 
{
	private String contact_id,password;
	private boolean active_status;
	private String department,primary_email_id,secondary_email_id,first_name,last_name,primary_phone_number,secondary_phone_number,primary_mobile_number, secondary_mobile_number;
	private RoleEntity role;
	private ContactEntity manager_id;
	private int is_tenancy_admin;
	private AddressEntity addressId;
	private String country,dob,nativeState,newPassword;
	private String countryCode, language, timezone;
	private int sysGeneratedPassword;
	private int resetPassCount;
	private Timestamp LastUpdatedTime;
	private int pwdExpired;
	private Timestamp password_creation_date;
	
	
	public Timestamp getLastUpdatedTime() {
		return LastUpdatedTime;
	}

	public void setLastUpdatedTime(Timestamp lastUpdatedTime) {
		LastUpdatedTime = lastUpdatedTime;
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

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountry() {
		return country;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getNativeState() {
		return nativeState;
	}

	public void setNativeState(String nativeState) {
		this.nativeState = nativeState;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	private ClientEntity client_id;
	
	public ContactEntity() {}
	
	public ContactEntity(String contact_id){
		
		super.key = new String(contact_id);
		ContactEntity e=(ContactEntity) read(this);
		
		if(e!=null)
		{
			setContact_id(e.contact_id);
			setPassword(e.password);
			setFirst_name(e.first_name);
			setLast_name(e.last_name);
			setRole(e.role);
			setClient_id(e.client_id);
			setIs_tenancy_admin(e.is_tenancy_admin);
			setAddressId(e.getAddressId());
			setPrimary_email_id(e.getPrimary_email_id());
			setPrimary_mobile_number(e.getPrimary_mobile_number());
			setCountryCode(e.getCountryCode());
			setLanguage(e.getLanguage());
			setTimezone(e.getTimezone());
			setLastUpdatedTime(e.getLastUpdatedTime());
			
		}
			
	}
	
	
	public int getIs_tenancy_admin() {
		return is_tenancy_admin;
	}

	public void setIs_tenancy_admin(int is_tenancy_admin) {
		this.is_tenancy_admin = is_tenancy_admin;
	}

	public String getContact_id() {
		return contact_id;
	}

	public void setContact_id(String contact_id) {
		this.contact_id = contact_id;
	}

	
	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public ClientEntity getClient_id() {
		return client_id;
	}


	public void setClient_id(ClientEntity client_id) {
		this.client_id = client_id;
	}


	public String getPrimary_phone_number() {
		return primary_phone_number;
	}

	public void setPrimary_phone_number(String primary_phone_number) {
		this.primary_phone_number = primary_phone_number;
	}

	public String getSecondary_phone_number() {
		return secondary_phone_number;
	}


	public void setSecondary_phone_number(String secondary_phone_number) {
		this.secondary_phone_number = secondary_phone_number;
	}


	public String getPrimary_mobile_number() {
		return primary_mobile_number;
	}


	public void setPrimary_mobile_number(String primary_mobile_number) {
		this.primary_mobile_number = primary_mobile_number;
	}


	public String getSecondary_mobile_number() {
		return secondary_mobile_number;
	}


	public void setSecondary_mobile_number(String secondary_mobile_number) {
		this.secondary_mobile_number = secondary_mobile_number;
	}


		
	public boolean isActive_status() {
		return active_status;
	}

	public void setActive_status(boolean active_status) {
		this.active_status = active_status;
	}

	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	
	public String getPrimary_email_id() {
		return primary_email_id;
	}
	public void setPrimary_email_id(String primary_email_id) {
		this.primary_email_id = primary_email_id;
	}
	
	public String getSecondary_email_id() {
		return secondary_email_id;
	}
	public void setSecondary_email_id(String secondary_email_id) {
		this.secondary_email_id = secondary_email_id;
	}
	
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	
	public RoleEntity getRole() {
		return role;
	}
	public void setRole(RoleEntity role) {
		this.role = role;
	}


	public ContactEntity getManager_id() {
		return manager_id;
	}


	public void setManager_id(ContactEntity manager_id) {
		this.manager_id = manager_id;
	}

	
	public AddressEntity getAddressId() {
		return addressId;
	}

	public void setAddressId(AddressEntity addressId) {
		this.addressId = addressId;
	}

	public Timestamp getPassword_creation_date() {
		return password_creation_date;
	}

	public void setPassword_creation_date(Timestamp password_creation_date) {
		this.password_creation_date = password_creation_date;
	}

	public int getPwdExpired() {
		return pwdExpired;
	}

	public void setPwdExpired(int pwdExpired) {
		this.pwdExpired = pwdExpired;
	}

	public int getResetPassCount() {
	    return resetPassCount;
	}

	public void setResetPassCount(int resetPassCount) {
	    this.resetPassCount = resetPassCount;
	}

}
