package remote.wise.businessentity;

import java.io.Serializable;
import java.sql.Timestamp;

public class BillingReportDetailEntity extends BaseBusinessEntity implements Serializable
{
	private BillingReportEntity billing_id;
	private AssetEntity serial_number;
	private Timestamp rolledOffDate;
	
	public BillingReportEntity getBilling_id() {
		return billing_id;
	}
	public void setBilling_id(BillingReportEntity billing_id) {
		this.billing_id = billing_id;
	}
	public Timestamp getRolledOffDate() {
		return rolledOffDate;
	}
	public void setRolledOffDate(Timestamp rolledOffDate) {
		this.rolledOffDate = rolledOffDate;
	}
	public AssetEntity getSerial_number() {
		return serial_number;
	}
	public void setSerial_number(AssetEntity serial_number) {
		this.serial_number = serial_number;
	}
	
	
	
}
