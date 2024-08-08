/**
 * 
 */
package remote.wise.service.implementation;

import java.util.HashMap;

import remote.wise.businessobject.OAssetSnapshotBO;

/**
 * @author roopn5
 *
 */
public class OAssetSnapshotImpl {
	
	public String setOAssetSnapshotDetails(String OserialNumber, HashMap<String,String> OAssetOwnerSnapshotMap, String OCurrentOwner){
		
		//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
		String response = "SUCCESS"; 
		//String response=new OAssetSnapshotBO().setOrientAssetSnapshotDetails(OserialNumber, OAssetOwnerSnapshotMap, OCurrentOwner);
		
		return response;
		
	}

}
