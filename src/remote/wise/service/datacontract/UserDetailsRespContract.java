package remote.wise.service.datacontract;

import java.sql.Timestamp;
import java.util.List;

public class UserDetailsRespContract {
	int tenancy_id;
	private String loginId;
	public int getTenancy_id() {
		return tenancy_id;
	}
	public void setTenancy_id(int tenancy_id) {
		this.tenancy_id = tenancy_id;
	}
	private String first_name;
	private String last_name;
	private int role_id;
	private String role_name;
	private List<Integer> asset_group_id;
	private List<String> asset_group_name;
	private int Is_tenancy_admin;
	
	private String primaryMobNumber;
	private String countryCode;
	private String language;
	private String timeZone;
	private String primaryEmailId;
	//Keerthi : Defect ID : 1069 : Tenancy admin count : Deleting user
	private int tenancyAdminCount;
	
	
	public String getPrimaryEmailId() {
		return primaryEmailId;
	}
	public void setPrimaryEmailId(String primaryEmailId) {
		this.primaryEmailId = primaryEmailId;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public String getPrimaryMobNumber() {
		return primaryMobNumber;
	}
	public void setPrimaryMobNumber(String primaryMobNumber) {
		this.primaryMobNumber = primaryMobNumber;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
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
	public int getRole_id() {
		return role_id;
	}
	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}
	public String getRole_name() {
		return role_name;
	}
	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}
	
	
	
	public List<Integer> getAsset_group_id() {
		return asset_group_id;
	}
	public void setAsset_group_id(List<Integer> asset_group_id) {
		this.asset_group_id = asset_group_id;
	}
	public List<String> getAsset_group_name() {
		return asset_group_name;
	}
	public void setAsset_group_name(List<String> asset_group_name) {
		this.asset_group_name = asset_group_name;
	}
	public int getIs_tenancy_admin() {
		return Is_tenancy_admin;
	}
	public void setIs_tenancy_admin(int is_tenancy_admin) {
		Is_tenancy_admin = is_tenancy_admin;
	}
	public int getTenancyAdminCount() {
		return tenancyAdminCount;
	}
	public void setTenancyAdminCount(int tenancyAdminCount) {
		this.tenancyAdminCount = tenancyAdminCount;
	}
	
	
	
}
