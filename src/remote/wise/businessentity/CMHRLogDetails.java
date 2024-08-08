package remote.wise.businessentity;
import java.io.Serializable;

import java.sql.Timestamp;

public class CMHRLogDetails extends BaseBusinessEntity implements Serializable {

	private AssetEntity serial_number;
	private Timestamp application_timestamp;
	private Timestamp firmware_timestamp;
	private ContactEntity cmhr_loginID;
	private String refreshCHM;
	private String cmhrflag;
	private String previousCMHR;
	/**
	 * @return the previousCMHR
	 */
	public String getPreviousCMHR() {
		return previousCMHR;
	}
	/**
	 * @param previousCMHR the previousCMHR to set
	 */
	public void setPreviousCMHR(String previousCMHR) {
		this.previousCMHR = previousCMHR;
	}
	int cmhr_log_id;
	/**
	 * @return the serial_number
	 */
	public AssetEntity getSerial_number() {
		return serial_number;
	}
	/**
	 * @param serial_number the serial_number to set
	 */
	public void setSerial_number(AssetEntity serial_number) {
		this.serial_number = serial_number;
	}
	/**
	 * @return the application_timestamp
	 */
	public Timestamp getApplication_timestamp() {
		return application_timestamp;
	}
	/**
	 * @param application_timestamp the application_timestamp to set
	 */
	public void setApplication_timestamp(Timestamp application_timestamp) {
		this.application_timestamp = application_timestamp;
	}
	/**
	 * @return the firmware_timestamp
	 */
	public Timestamp getFirmware_timestamp() {
		return firmware_timestamp;
	}
	/**
	 * @param firmware_timestamp the firmware_timestamp to set
	 */
	public void setFirmware_timestamp(Timestamp firmware_timestamp) {
		this.firmware_timestamp = firmware_timestamp;
	}
	/**
	 * @return the cmhr_loginID
	 */
	public ContactEntity getCmhr_loginID() {
		return cmhr_loginID;
	}
	/**
	 * @param cmhr_loginID the cmhr_loginID to set
	 */
	public void setCmhr_loginID(ContactEntity cmhr_loginID) {
		this.cmhr_loginID = cmhr_loginID;
	}
	/**
	 * @return the refreshCHM
	 */
	public String getRefreshCHM() {
		return refreshCHM;
	}
	/**
	 * @param refreshCHM the refreshCHM to set
	 */
	public void setRefreshCHM(String refreshCHM) {
		this.refreshCHM = refreshCHM;
	}
	/**
	 * @return the cmhrflag
	 */
	public String getCmhrflag() {
		return cmhrflag;
	}
	/**
	 * @param cmhrflag the cmhrflag to set
	 */
	public void setCmhrflag(String cmhrflag) {
		this.cmhrflag = cmhrflag;
	}
	/**
	 * @return the cmhr_log_id
	 */
	public int getCmhr_log_id() {
		return cmhr_log_id;
	}
	/**
	 * @param cmhr_log_id the cmhr_log_id to set
	 */
	public void setCmhr_log_id(int cmhr_log_id) {
		this.cmhr_log_id = cmhr_log_id;
	}
	
}

