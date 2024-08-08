package remote.wise.service.datacontract;

public class MachineBillingReportInputContract {

	String ToDate;
	String FromDate;
	/**
	 * @return the toDate
	 */
	public String getToDate() {
		return ToDate;
	}
	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(String toDate) {
		ToDate = toDate;
	}
	/**
	 * @return the fromDate
	 */
	public String getFromDate() {
		return FromDate;
	}
	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(String fromDate) {
		FromDate = fromDate;
	}
}
