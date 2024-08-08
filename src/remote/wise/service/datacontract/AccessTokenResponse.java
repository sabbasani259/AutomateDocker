package remote.wise.service.datacontract;

import com.fasterxml.jackson.annotation.JsonProperty;

//20231127 CR452-AirtelApiIntegration-prasad
public class AccessTokenResponse {
	

	@JsonProperty(value ="data")
	public AirtelSimAccessTokenResponse data;

	public AirtelSimAccessTokenResponse getData() {
		return data;
	}

	public void setData(AirtelSimAccessTokenResponse data) {
		this.data = data;
	}
}
