package remote.wise.service.datacontract;

import java.util.HashMap;
import java.util.List;

/**
 * @author Rajani Nagaraju
 * DefectId 912 - Rajani Nagaraju - 2013/07/08 -  Replace direct SQL query to Hibernate
 */
public class StockSummaryRespContract 
{
	int zonalTenancyId;
	String zonalTenancyName;
	//DefectId 912 - Rajani Nagaraju - 2013/07/08 -  Replace direct SQL query to Hibernate
	long zonalMachineCount;
	List<String> dealerIdNameCountMap;
	
	/**
	 * @return the zonalTenancyId
	 */
	public int getZonalTenancyId() {
		return zonalTenancyId;
	}
	/**
	 * @param zonalTenancyId the zonalTenancyId to set
	 */
	public void setZonalTenancyId(int zonalTenancyId) {
		this.zonalTenancyId = zonalTenancyId;
	}
	/**
	 * @return the zonalTenancyName
	 */
	public String getZonalTenancyName() {
		return zonalTenancyName;
	}
	/**
	 * @param zonalTenancyName the zonalTenancyName to set
	 */
	public void setZonalTenancyName(String zonalTenancyName) {
		this.zonalTenancyName = zonalTenancyName;
	}
	/**
	 * @return List of String in the form dealerId, dealerName, stockCount
	 */
	public List<String> getDealerIdNameCountMap() {
		return dealerIdNameCountMap;
	}
	/**
	 * @param dealerIdNameCountMap the List of String in the form dealerId, dealerName, stockCount to set
	 */
	public void setDealerIdNameCountMap(List<String> dealerIdNameCountMap) {
		this.dealerIdNameCountMap = dealerIdNameCountMap;
	}
	/**
	 * @return the zonalMachineCount
	 */
	public long getZonalMachineCount() {
		return zonalMachineCount;
	}
	/**
	 * @param zonalMachineCount the zonalMachineCount to set
	 */
	public void setZonalMachineCount(long zonalMachineCount) {
		this.zonalMachineCount = zonalMachineCount;
	}
	
	
	
	
}
