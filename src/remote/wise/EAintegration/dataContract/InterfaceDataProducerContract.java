package remote.wise.EAintegration.dataContract;

import java.io.Serializable;

@SuppressWarnings("serial")
public class InterfaceDataProducerContract implements Serializable
{
	String serialNumber;
	String roll_off_date;
	String profile;
	String type;
	String llplus_flag;
	String dealer_code;
	String dealer_name;
	String customer_code;
	String customer_name;
	String zonal_code;
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getRoll_off_date() {
		return roll_off_date;
	}
	public void setRoll_off_date(String roll_off_date) {
		this.roll_off_date = roll_off_date;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLlplus_flag() {
		return llplus_flag;
	}
	public void setLlplus_flag(String llplus_flag) {
		this.llplus_flag = llplus_flag;
	}
	public String getDealer_code() {
		return dealer_code;
	}
	public void setDealer_code(String dealer_code) {
		this.dealer_code = dealer_code;
	}
	public String getDealer_name() {
		return dealer_name;
	}
	public void setDealer_name(String dealer_name) {
		this.dealer_name = dealer_name;
	}
	public String getCustomer_code() {
		return customer_code;
	}
	public void setCustomer_code(String customer_code) {
		this.customer_code = customer_code;
	}
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	public String getZonal_code() {
		return zonal_code;
	}
	public void setZonal_code(String zonal_code) {
		this.zonal_code = zonal_code;
	}
	
	
	
	
}
