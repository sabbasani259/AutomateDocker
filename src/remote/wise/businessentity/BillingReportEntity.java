package remote.wise.businessentity;

import java.io.Serializable;
import java.sql.Timestamp;

public class BillingReportEntity extends BaseBusinessEntity implements Serializable{
	private Long actual_machine_count;
	private Long commulative_Actual_machine_count;
	private Long billing_cal;
	private int year,month;
	
	/**
	 * @return the commulative_Actual_machine_count
	 */
	public Long getCommulative_Actual_machine_count() {
		return commulative_Actual_machine_count;
	}
	/**
	 * @param commulative_Actual_machine_count the commulative_Actual_machine_count to set
	 */
	public void setCommulative_Actual_machine_count(
			Long commulative_Actual_machine_count) {
		this.commulative_Actual_machine_count = commulative_Actual_machine_count;
	}
	/**
	 * @return the billing_cal
	 */
	public Long getBilling_cal() {
		return billing_cal;
	}
	/**
	 * @param billing_cal the billing_cal to set
	 */
	public void setBilling_cal(Long billing_cal) {
		this.billing_cal = billing_cal;
	}
	
	private int billing_id;
	/**
	 * @return the actual_machine_count
	 */
	public Long getActual_machine_count() {
		return actual_machine_count;
	}
	/**
	 * @param actual_machine_count the actual_machine_count to set
	 */
	public void setActual_machine_count(Long actual_machine_count) {
		this.actual_machine_count = actual_machine_count;
	}
	
	public int getBilling_id() {
		return billing_id;
	}
	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}
	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}
	/**
	 * @return the month
	 */
	public int getMonth() {
		return month;
	}
	/**
	 * @param month the month to set
	 */
	public void setMonth(int month) {
		this.month = month;
	}
	/**
	 * @param billing_id the billing_id to set
	 */
	public void setBilling_id(int billing_id) {
		this.billing_id = billing_id;
	}
	
	
}
