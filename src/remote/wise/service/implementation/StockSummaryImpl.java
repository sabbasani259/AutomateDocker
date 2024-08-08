package remote.wise.service.implementation;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.StockSummaryReqContract;
import remote.wise.service.datacontract.StockSummaryRespContract;

/** Implementation class that handles Stock summary details
 *  DefectID: 912 - Rajani Nagaraju - Replace direct SQL Query to Hibernate
 * @author Rajani Nagaraju
 *
 */
public class StockSummaryImpl 
{
	int zonalTenancyId;
	String zonalTenancyName;
	// DefectID: 912 - Rajani Nagaraju - Replace direct SQL Query to Hibernate
	long zonalMachineCount;
	HashMap<Integer, HashMap<String,Long>> dealerIdNameCountMap;
	
	
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
	
	/**
	 * @return the dealerIdNameCountMap
	 */
	public HashMap<Integer, HashMap<String, Long>> getDealerIdNameCountMap() {
		return dealerIdNameCountMap;
	}
	/**
	 * @param dealerIdNameCountMap the dealerIdNameCountMap to set
	 */
	public void setDealerIdNameCountMap(
			HashMap<Integer, HashMap<String, Long>> dealerIdNameCountMap) {
		this.dealerIdNameCountMap = dealerIdNameCountMap;
	}
	

	//*********************************************************get Stock summary details ***************************************************
	
	
	/** This method returns the Stock balance at each stake holder
	 * DefectId 912 - Rajani Nagaraju - 2013/07/08 -  Replace direct SQL query to Hibernate
	 * @param reqObj userLoginId and tenancyId is specified through this reqObj
	 * @return the StockCount at each stakeholder
	 * @throws CustomFault
	 */
	public List<StockSummaryRespContract> getStockSummaryDetails(StockSummaryReqContract reqObj) throws CustomFault
	{
		List<StockSummaryRespContract> responseList = new LinkedList<StockSummaryRespContract>();
		
		AssetDetailsBO assetDetails = new AssetDetailsBO();
		List<StockSummaryImpl> implObjList = assetDetails.getStockSummaryDetails(reqObj.getLoginId(), reqObj.getTenancyId());
		
		for(int i=0; i<implObjList.size(); i++)
		{
			StockSummaryRespContract response = new StockSummaryRespContract();
			response.setZonalTenancyId(implObjList.get(i).getZonalTenancyId());
			response.setZonalTenancyName(implObjList.get(i).getZonalTenancyName());
			response.setZonalMachineCount(implObjList.get(i).getZonalMachineCount());
		
			
			List<String> idNameCountStringList = new LinkedList<String>();
			HashMap<Integer,HashMap<String,Long>> idNameCountMap = implObjList.get(i).getDealerIdNameCountMap();
			for(int j=0; j< idNameCountMap.size(); j++)
			{
				String finalIdNameCountString = null;
				Integer dealerId = (Integer)idNameCountMap.keySet().toArray()[j];
				finalIdNameCountString = dealerId+"";
				HashMap<String,Long> nameCount = (HashMap<String,Long>)idNameCountMap.values().toArray()[j];
				for(int k=0; k<nameCount.size(); k++)
				{
					String name = (String) nameCount.keySet().toArray()[k];
					Long count = (Long)nameCount.values().toArray()[k];
					finalIdNameCountString = finalIdNameCountString+","+name+","+count;
					
				}
				idNameCountStringList.add(finalIdNameCountString);
			}
			
			response.setDealerIdNameCountMap(idNameCountStringList);
			
			responseList.add(response);
		}
		
		return responseList;
		
	}
}
