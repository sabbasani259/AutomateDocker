package remote.wise.service.datacontract;

public class ZoneOrDealerOrInstallationWiseSummaryOutputContract {
	private String key;
	private String oneToSeven;
	private String eightToFifteen;
	private String sixteenToThirty;
	private String thirtyOneToSixty;
	private String sixtyPlux;
	private String grandTotal;
	private String warranty;
	private String countryCode;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getOneToSeven() {
		return oneToSeven;
	}
	public void setOneToSeven(String oneToSeven) {
		this.oneToSeven = oneToSeven;
	}
	public String getEightToFifteen() {
		return eightToFifteen;
	}
	public void setEightToFifteen(String eightToFifteen) {
		this.eightToFifteen = eightToFifteen;
	}
	public String getSixteenToThirty() {
		return sixteenToThirty;
	}
	public void setSixteenToThirty(String sixteenToThirty) {
		this.sixteenToThirty = sixteenToThirty;
	}
	public String getThirtyOneToSixty() {
		return thirtyOneToSixty;
	}
	public void setThirtyOneToSixty(String thirtyOneToSixty) {
		this.thirtyOneToSixty = thirtyOneToSixty;
	}
	public String getSixtyPlux() {
		return sixtyPlux;
	}
	public void setSixtyPlux(String sixtyPlux) {
		this.sixtyPlux = sixtyPlux;
	}
	public String getGrandTotal() {
		return grandTotal;
	}
	public void setGrandTotal(String grandTotal) {
		this.grandTotal = grandTotal;
	}
	public String getWarranty() {
		return warranty;
	}
	public void setWarranty(String warranty) {
		this.warranty = warranty;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	@Override
	public String toString() {
		return "ZoneAndDealerWiseSummaryOutputContract [key=" + key
				+ ", oneToSeven=" + oneToSeven + ", eightToFifteen="
				+ eightToFifteen + ", sixteenToThirty=" + sixteenToThirty
				+ ", thirtyOneToSixty=" + thirtyOneToSixty + ", sixtyPlux="
				+ sixtyPlux + ", grandTotal=" + grandTotal + ", warranty="
				+ warranty + ", countryCode=" + countryCode + "]";
	}
}
