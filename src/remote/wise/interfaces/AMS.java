/**
 * 
 */
package remote.wise.interfaces;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import remote.wise.businessentity.HAJAssetLocationDetailsEntity;
import remote.wise.exception.CustomFault;
import remote.wise.pojo.AmsDAO;
import remote.wise.service.implementation.AssetDashboardImpl;
import remote.wise.service.implementation.MapImpl;

/**
 * @author roopn5
 *
 */
public interface AMS {
	
	public List<AmsDAO>getAMSData(String txnKey, String Serial_Number);
	
	public String setAMSData(String txnKey, AmsDAO newSnapshotObj, HashMap<String, String> payloadMap, String engineStatus, String evt_HCT, String evt_LOP, String evt_IBL);
	
	public String updateAMSData(String txnKey, int transactionNumber, AmsDAO snapshotObj, String recordType, Timestamp txnTimestamp, HashMap<String,String>payloadMap, String currentFuelLevel, String engineStatus, String evt_HCT, String evt_LOP, String evt_IBL);

	//DF20181031-ko369761-LOGIN ID passing as parameter in the below functions for the tracking. 
	public List<MapImpl> getQuerySpecificDetailsForMap(String Query,String loginId);
	
	public MapImpl getQuerySpecificDetailsForFleetMap(String Query,String loginId);
	
	public List<AssetDashboardImpl> getQuerySpecificDetailsForAssetDashBoard(String Query,String loginId) throws CustomFault;
	
	public long getAssetDashBoardTotalCount(String Query);
	
	public List<HAJAssetLocationDetailsEntity> getQuerySpecificDetailsForHAJAssetDetails(String Query);
}
