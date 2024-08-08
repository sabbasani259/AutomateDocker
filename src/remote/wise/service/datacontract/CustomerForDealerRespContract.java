package remote.wise.service.datacontract;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public class CustomerForDealerRespContract {
	
		int dealerTenancyId;
		//DF23/03/2015 for alphabetical order of customer name
		LinkedHashMap<Integer, String> customerMap;		
		
		
		
		public LinkedHashMap<Integer, String> getCustomerMap() {
			return customerMap;
		}

		public void setCustomerMap(LinkedHashMap<Integer, String> customerMap) {
			this.customerMap = customerMap;
		}

		public int getDealerTenancyId() {
			return dealerTenancyId;
		}
		
		public void setDealerTenancyId(int dealerTenancyId) {
			this.dealerTenancyId = dealerTenancyId;
		}
}
