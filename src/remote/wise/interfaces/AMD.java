package remote.wise.interfaces;

import java.util.List;

import remote.wise.businessentity.AssetMonitoringDetailEntity;
import remote.wise.pojo.AmdDAO;

public interface AMD {

	public String setAMDData(int transaction_number,int parameter_id, String param_value, int segmentid);
	
	public String updateAMDData(int transaction_number,int parameter_id, String param_value, int segmentid);
	
	public List<AmdDAO> getAMDData(String txnKey, int transaction_number, String txnTimestamp, int seg_id, String parameterListAsString);
	
	
}
