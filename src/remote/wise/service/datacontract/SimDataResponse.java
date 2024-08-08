package remote.wise.service.datacontract;

import com.fasterxml.jackson.annotation.JsonProperty;

//20231127 CR452-AirtelApiIntegration-prasad
public class SimDataResponse {
	
    @JsonProperty(value ="data")
	public SimDetailsData data;

	public SimDetailsData getData() {
		return data;
	}

	public void setData(SimDetailsData data) {
		this.data = data;
	}
	
	

}
