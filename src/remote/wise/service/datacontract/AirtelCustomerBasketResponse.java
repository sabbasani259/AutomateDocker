package remote.wise.service.datacontract;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

//20231127 CR452-AirtelApiIntegration-prasad
public class AirtelCustomerBasketResponse {
	
	 @JsonProperty(value ="data")
	public Data data;

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}
	
	

}

