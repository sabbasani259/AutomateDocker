package remote.wise.service.datacontract;

import java.util.List;

/**
 * @author kprabhu5
 *
 */
public class RolledOffMachinesReqContract {
	
private String loginId;		
//DefectId:20140919 @ Suprava Search in New Machine Tab
private String vin;	
private String toDate;
private String fromDate;
/**
 * @return the vin
 */
public String getVin() {
	return vin;
}

/**
 * @return the toDate
 */
public String getToDate() {
	return toDate;
}

/**
 * @param toDate the toDate to set
 */
public void setToDate(String toDate) {
	this.toDate = toDate;
}

/**
 * @return the fromDate
 */
public String getFromDate() {
	return fromDate;
}

/**
 * @param fromDate the fromDate to set
 */
public void setFromDate(String fromDate) {
	this.fromDate = fromDate;
}

/**
 * @param vin the vin to set
 */
public void setVin(String vin) {
	this.vin = vin;
}

private List<Integer> tenancyIdList;

/**
 * @return the tenancyIdList
 */
public List<Integer> getTenancyIdList() {
	return tenancyIdList;
}

/**
 * @param tenancyIdList the tenancyIdList to set
 */
public void setTenancyIdList(List<Integer> tenancyIdList) {
	this.tenancyIdList = tenancyIdList;
}

/**
 * @return the loginId
 */
public String getLoginId() {
	return loginId;
}

/**
 * @param loginId the loginId to set
 */
public void setLoginId(String loginId) {
	this.loginId = loginId;
}


}
