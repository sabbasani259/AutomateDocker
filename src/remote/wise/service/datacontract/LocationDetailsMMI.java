package remote.wise.service.datacontract;

import java.io.Serializable;

public class LocationDetailsMMI implements Serializable
{
	 	private static final long serialVersionUID = 1L;
		private String city;
		private String state;
		private String country;
		private String pin;
		private String address;
		private String district;
		private String village;
		
		
		public LocationDetailsMMI() {
			city = "undefined";
			state = "undefined";
			country = "undefined";
			pin = "undefined";
			address="undefined";
			district="undefined";
			village="undefined";
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

		public String getDistrict() {
			return district;
		}

		public void setDistrict(String district) {
			this.district = district;
		}

		public String getVillage() {
			return village;
		}

		public void setVillage(String village) {
			this.village = village;
		}
		
}
