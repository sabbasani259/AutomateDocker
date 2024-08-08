package remote.wise.service.datacontract;
/**
 * @author Z1007653
 */
public class DealerOutletOutputContract {

	private String dealerName;
	private String outletType;
	private String outletTypeCode;
	private String address;
	private String location;
	private String lat;
	private String lon;
	
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	public String getOutletType() {
		return outletType;
	}
	public void setOutletType(String outletType) {
		this.outletType = outletType;
	}
	public String getOutletTypeCode() {
		return outletTypeCode;
	}
	public void setOutletTypeCode(String outletTypeCode) {
		this.outletTypeCode = outletTypeCode;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	@Override
	public String toString() {
		return "DealerOutletOutputContract [dealerName=" + dealerName
				+ ", outletType=" + outletType + ", outletTypeCode="
				+ outletTypeCode + ", address=" + address + ", location="
				+ location + ", lat=" + lat + ", lon=" + lon + "]";
	}
}