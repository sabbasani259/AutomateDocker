package remote.wise.service.datacontract;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

//20231127 CR452-AirtelApiIntegration-prasad
public class SimDetailsData {

	    @JsonProperty(value ="sims")
	    public ArrayList<SimDetailsResponse> sims;
	    public String total;
	    
	    
		public ArrayList<SimDetailsResponse> getSims() {
			return sims;
		}
		public void setSims(ArrayList<SimDetailsResponse> sims) {
			this.sims = sims;
		}
		public String getTotal() {
			return total;
		}
		public void setTotal(String total) {
			this.total = total;
		}
	    
	    
	    
	    
}
