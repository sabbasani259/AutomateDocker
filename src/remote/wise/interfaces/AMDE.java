package remote.wise.interfaces;

import java.sql.Timestamp;
import java.util.List;

import remote.wise.businessentity.AssetMonitoringDetailEntity;
import remote.wise.pojo.AmdeDAO;

public interface AMDE {


	public String setAMDEData(int transaction_number,int parameter_id, String param_value, int segmentid);
	
	public String updateAMDEData(int transaction_number,int parameter_id, String param_value, int segmentid);
	
	public List<AmdeDAO> getAMDEData(Timestamp txnTimestamp,int transaction_number, int segmentid,String Serial_Number);
	
}
