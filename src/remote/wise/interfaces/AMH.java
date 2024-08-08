package remote.wise.interfaces;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

//import remote.wise.businessentity.AssetMonitoringHeaderEntity;
import remote.wise.pojo.AmhDAO;
import remote.wise.service.implementation.AssetEventLogImpl;
import remote.wise.service.implementation.DownloadAempImpl;
import remote.wise.service.implementation.PricolTransactionDetailImpl;


/*
 * author S Suresh 
 * Date 20160616 
 * creating an interface for AMH DAL Layer 
 */
public interface AMH {
	

	public String setAMHData(String txnKey, HashMap<String,String> payloadMap, Timestamp txnTimestamp, int prevPktTxnNumber,int seg_ID,int record_type);
	public List<AmhDAO> getAMhData(String Serial_Number, Timestamp txnTimestamp,int seg_ID);
	public String updateAMHData(String txnKey,HashMap<String,String> payloadMap, int segmentId, int recordType,int updatecount);
	
	
}
