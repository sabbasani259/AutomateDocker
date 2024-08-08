package remote.wise.service.datacontract;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

//20231127 CR452-AirtelApiIntegration-prasad
public class AirtelSimAccessTokenResponse {
	
	
	
	@JsonProperty(value ="refresh_token")
	 public String refresh_token;
	    public String token_type;
	    @JsonProperty(value ="access_token")
	    public String access_token;
	    public int expires_in;
	    
	    
	    
	    
		public String getRefresh_token() {
			return refresh_token;
		}
		public void setRefresh_token(String refresh_token) {
			this.refresh_token = refresh_token;
		}
		public String getToken_type() {
			return token_type;
		}
		public void setToken_type(String token_type) {
			this.token_type = token_type;
		}
		public String getAccess_token() {
			return access_token;
		}
		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}
		public int getExpires_in() {
			return expires_in;
		}
		public void setExpires_in(int expires_in) {
			this.expires_in = expires_in;
		}
	    
	    
	
	

}
