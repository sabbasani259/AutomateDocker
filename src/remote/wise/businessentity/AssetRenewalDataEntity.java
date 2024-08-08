/**
 * CR334 : 20221118 : Dhiraj K : Changes for Billing and ARD table update
 */
package remote.wise.businessentity;

import java.io.Serializable;
import java.sql.Timestamp;

public class AssetRenewalDataEntity extends BaseBusinessEntity implements Serializable{

	private AssetEntity serial_number;
	private int mode_of_subscription;
	private Timestamp updated_on;
	private ContactEntity updated_by;
	private Timestamp subscribed_from;
	//CR334.sn
	private Timestamp subscribed_to;
	
	public Timestamp getSubscribed_to() {
		return subscribed_to;
	}
	public void setSubscribed_to(Timestamp subscribed_to) {
		this.subscribed_to = subscribed_to;
	}
	//CR334.en
	public AssetEntity getSerial_number() {
		return serial_number;
	}
	public void setSerial_number(AssetEntity serial_number) {
		this.serial_number = serial_number;
	}
	public int getMode_of_subscription() {
		return mode_of_subscription;
	}
	public void setMode_of_subscription(int mode_of_subscription) {
		this.mode_of_subscription = mode_of_subscription;
	}
	public Timestamp getUpdated_on() {
		return updated_on;
	}
	public void setUpdated_on(Timestamp updated_on) {
		this.updated_on = updated_on;
	}
	public ContactEntity getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(ContactEntity updated_by) {
		this.updated_by = updated_by;
	}
	public Timestamp getSubscribed_from() {
		return subscribed_from;
	}
	public void setSubscribed_from(Timestamp subscribed_from) {
		this.subscribed_from = subscribed_from;
	}
}
