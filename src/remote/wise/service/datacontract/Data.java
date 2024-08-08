package remote.wise.service.datacontract;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

//20231127 CR452-AirtelApiIntegration-prasad
public class Data {
	        @JsonProperty(value ="baskets")
		    public ArrayList<Basket> baskets;
		    public String totalBaskets;
		    public String totalSims;
		    public String totalActiveSims;
		    public String totalAvailableSims;
		    public String totalTestModeSims;
		    public String totalSafeCustodySims;
		    public String totalInActiveSims;
		    public String totalInProgressSims;
		    public String totalSuspendedSims;
		    public String totalSimsInBaskets;
			public ArrayList<Basket> getBaskets() {
				return baskets;
			}
			public void setBaskets(ArrayList<Basket> baskets) {
				this.baskets = baskets;
			}
			public String getTotalBaskets() {
				return totalBaskets;
			}
			public void setTotalBaskets(String totalBaskets) {
				this.totalBaskets = totalBaskets;
			}
			public String getTotalSims() {
				return totalSims;
			}
			public void setTotalSims(String totalSims) {
				this.totalSims = totalSims;
			}
			public String getTotalActiveSims() {
				return totalActiveSims;
			}
			public void setTotalActiveSims(String totalActiveSims) {
				this.totalActiveSims = totalActiveSims;
			}
			public String getTotalAvailableSims() {
				return totalAvailableSims;
			}
			public void setTotalAvailableSims(String totalAvailableSims) {
				this.totalAvailableSims = totalAvailableSims;
			}
			public String getTotalTestModeSims() {
				return totalTestModeSims;
			}
			public void setTotalTestModeSims(String totalTestModeSims) {
				this.totalTestModeSims = totalTestModeSims;
			}
			public String getTotalSafeCustodySims() {
				return totalSafeCustodySims;
			}
			public void setTotalSafeCustodySims(String totalSafeCustodySims) {
				this.totalSafeCustodySims = totalSafeCustodySims;
			}
			public String getTotalInActiveSims() {
				return totalInActiveSims;
			}
			public void setTotalInActiveSims(String totalInActiveSims) {
				this.totalInActiveSims = totalInActiveSims;
			}
			public String getTotalInProgressSims() {
				return totalInProgressSims;
			}
			public void setTotalInProgressSims(String totalInProgressSims) {
				this.totalInProgressSims = totalInProgressSims;
			}
			public String getTotalSuspendedSims() {
				return totalSuspendedSims;
			}
			public void setTotalSuspendedSims(String totalSuspendedSims) {
				this.totalSuspendedSims = totalSuspendedSims;
			}
			public String getTotalSimsInBaskets() {
				return totalSimsInBaskets;
			}
			public void setTotalSimsInBaskets(String totalSimsInBaskets) {
				this.totalSimsInBaskets = totalSimsInBaskets;
			}
			@Override
			public String toString() {
				return "Data [baskets=" + baskets + ", totalBaskets=" + totalBaskets + ", totalSims=" + totalSims
						+ ", totalActiveSims=" + totalActiveSims + ", totalAvailableSims=" + totalAvailableSims
						+ ", totalTestModeSims=" + totalTestModeSims + ", totalSafeCustodySims=" + totalSafeCustodySims
						+ ", totalInActiveSims=" + totalInActiveSims + ", totalInProgressSims=" + totalInProgressSims
						+ ", totalSuspendedSims=" + totalSuspendedSims + ", totalSimsInBaskets=" + totalSimsInBaskets
						+ ", getBaskets()=" + getBaskets() + ", getTotalBaskets()=" + getTotalBaskets()
						+ ", getTotalSims()=" + getTotalSims() + ", getTotalActiveSims()=" + getTotalActiveSims()
						+ ", getTotalAvailableSims()=" + getTotalAvailableSims() + ", getTotalTestModeSims()="
						+ getTotalTestModeSims() + ", getTotalSafeCustodySims()=" + getTotalSafeCustodySims()
						+ ", getTotalInActiveSims()=" + getTotalInActiveSims() + ", getTotalInProgressSims()="
						+ getTotalInProgressSims() + ", getTotalSuspendedSims()=" + getTotalSuspendedSims()
						+ ", getTotalSimsInBaskets()=" + getTotalSimsInBaskets() + ", getClass()=" + getClass()
						+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
			}
			
			
}
		    