package remote.wise.service.datacontract;
/**
 * @author Z1007653
 */

public class NonCommunicatingMachinesReportContract {
	private String key;			//can be any zone name or dealer name
	private String warranty; 	//can be 'YES' or 'NO'
	private String count; 		//can be any number
	private String countryCode;
	
	
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getWarranty() {
		return warranty;
	}
	public void setWarranty(String warranty) {
		this.warranty = warranty;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "NonCommunicatingMachinesReportContract [key=" + key
				+ ", warranty=" + warranty + ", count=" + count
				+ ", countryCode=" + countryCode + "]";
	}
	
	
	
}
