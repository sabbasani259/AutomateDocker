package remote.wise.businessentity;

public class RegConfirmationEntity extends BaseBusinessEntity {
	private String phone_number;
	private String IMEI_number;
	private String user_ID;
	/**
	 * @return the user_ID
	 */
	public String getUser_ID() {
		return user_ID;
	}
	/**
	 * @param user_ID the user_ID to set
	 */
	public void setUser_ID(String user_ID) {
		this.user_ID = user_ID;
	}
	/**
	 * @return the phone_number
	 */
	public String getPhone_number() {
		return phone_number;
	}
	/**
	 * @param phone_number the phone_number to set
	 */
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	/**
	 * @param iMEI_number the iMEI_number to set
	 */
	public void setIMEI_number(String iMEI_number) {
		IMEI_number = iMEI_number;
	}
	/**
	 * @return the iMEI_number
	 */
	public String getIMEI_number() {
		return IMEI_number;
	}
}
