package remote.wise.service.implementation;
/**
 * DownloadAempImpl  will allow to  Download details in Aemp Format for given Serial Number and Period
 * @author jgupta41
 *
 */

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import remote.wise.businessobject.DownloadAempBO;
//import remote.wise.businessobject.DownloadBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.DownloadAempReqContract;
import remote.wise.service.datacontract.DownloadAempRespContract;
import remote.wise.service.datacontract.DownloadReqContract;
import remote.wise.service.datacontract.DownloadRespContract;

public class DownloadAempImpl {
	
	/*DF20160718 @Roopa Fetch aemp details from new dynamic AMH and AMD tables 
	 * New parameters added to return response from DAL layer*/
	
	private int transaction_number;
	private int param_ID;
	private String param_value;
	
	 HashMap<String,String> txnDataMap=new HashMap<String, String>();
	
	/* END*/
	
	
	private String Date;	
	public HashMap<String, String> getTxnDataMap() {
		return txnDataMap;
	}
	public void setTxnDataMap(HashMap<String, String> txnDataMap) {
		this.txnDataMap = txnDataMap;
	}
	public int getTransaction_number() {
		return transaction_number;
	}
	public void setTransaction_number(int transaction_number) {
		this.transaction_number = transaction_number;
	}
	public int getParam_ID() {
		return param_ID;
	}
	public void setParam_ID(int param_ID) {
		this.param_ID = param_ID;
	}
	
	public String getParam_value() {
		return param_value;
	}
	public void setParam_value(String param_value) {
		this.param_value = param_value;
	}

	private String PIN	;
	
	public String getPIN() {
		return PIN;
	}
	public String getDate() {
		return Date;
	}
	public void setDate(String date) {
		Date = date;
	}
	public void setPIN(String pIN) {
		PIN = pIN;
	}
	//************************** Download details in Aemp Format for given Serial Number and Period****************************************
	/**
	 * This method Download details in Aemp Format for given Serial Number and Period
	 * @param downloadReq:Serial Nummber and Period
	 * @return respList:Download File 
	 * @throws CustomFault
	 */
	public List<DownloadAempRespContract> getDownloadAemp(DownloadAempReqContract downloadReq)throws CustomFault
	{
		List<DownloadAempRespContract> respList = new LinkedList<DownloadAempRespContract>();
		DownloadAempBO downloadBO=new DownloadAempBO();
		
		List<DownloadAempImpl> downloadImpl = new LinkedList<DownloadAempImpl>();
		//downloadImpl=downloadBO.getDownloadAemp(downloadReq.getSerialNumber(),downloadReq.getPeriod());
		
		//DF20180920-KO369761-Created a new method for changing db to TAssetMonData from remote_fact_dayagg tables.
		downloadImpl = downloadBO.getDownloadAemp_New(downloadReq.getSerialNumber(),downloadReq.getPeriod());		
	
		for(int i=0;i<downloadImpl.size();i++)
		{DownloadAempRespContract  respContractObj=new DownloadAempRespContract();
		respContractObj.setDate(downloadImpl.get(i).getDate());
		respContractObj.setPIN(downloadImpl.get(i).getPIN());
		
						respList.add(respContractObj);				
		}return respList;
	}
	//**************************End of Download details in Aemp Format for given Serial Number and Period****************************************
}
