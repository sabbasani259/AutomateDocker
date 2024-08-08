package remote.wise.service.datacontract;

import java.sql.Timestamp;

public class AuditLogReqContract {

	private String TenancyId;
	private String FromDate;
	private String ToDate;
	
	
	
	public String getFromDate() {
		return FromDate;
	}
	public void setFromDate(String fromDate) {
		FromDate = fromDate;
	}
	public String getToDate() {
		return ToDate;
	}
	public void setToDate(String toDate) {
		ToDate = toDate;
	}
	public String getTenancyId() {
		return TenancyId;
	}
	public void setTenancyId(String tenancyId) {
		TenancyId = tenancyId;
	}
	
}
