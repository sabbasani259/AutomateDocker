package remote.wise.service.datacontract;

import java.io.Serializable;

public class LocationDetails implements Serializable
{
	 	private static final long serialVersionUID = 1L;
		private String city;
		private String state;
		private String country;
		private String pin;
		private String address;
		
		// RC201603181245 update address redis cache changes by Shrini
		
		public LocationDetails() {
			city = "undefined";
			state = "undefined";
			country = "undefined";
			pin = "undefined";
			address="undefined";
		}
		
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}
		public String getPin() {
			return pin;
		}
		public void setPin(String pin) {
			this.pin = pin;
		}

		/**
		 * @return the address
		 */
		public String getAddress() {
			return address;
		}

		/**
		 * @param address the address to set
		 */
		public void setAddress(String address) {
			this.address = address;
		}
		
		@Override
		public String toString() {
			return getCity()+"|"+getState()+"|"+getCountry()+"|"+getAddress();
		}
		
}
