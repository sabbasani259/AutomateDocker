package remote.wise.service.implementation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessobject.PricolMachineDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.PricolTransactionDetailReqContract;
import remote.wise.service.datacontract.PricolTransactionDetailRespContract;
import remote.wise.service.datacontract.PricolTransactionSummaryRespContract;
//import remote.wise.util.WiseLogger;

public class PricolTransactionDetailImpl 
{
	//Defect Id 1337 - Logger changes
		//public static WiseLogger businessError = WiseLogger.getLogger("PricolTransactionDetailImpl:","businessError");
	String transactionTimeStamp;
	String serialNumber;
	HashMap<String, String> transactionData;
	
	/**
	 * @return the transactionTimeStamp
	 */
	public String getTransactionTimeStamp() {
		return transactionTimeStamp;
	}
	/**
	 * @param transactionTimeStamp the transactionTimeStamp to set
	 */
	public void setTransactionTimeStamp(String transactionTimeStamp) {
		this.transactionTimeStamp = transactionTimeStamp;
	}
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	/**
	 * @return the transactionData
	 */
	public HashMap<String, String> getTransactionData() {
		return transactionData;
	}
	/**
	 * @param transactionData the transactionData to set
	 */
	public void setTransactionData(HashMap<String, String> transactionData) {
		this.transactionData = transactionData;
	}
	
	public List<PricolTransactionDetailRespContract> getTransactionDetails(PricolTransactionDetailReqContract reqObj)
	{
		List<PricolTransactionDetailRespContract> responseList = new LinkedList<PricolTransactionDetailRespContract>();
		
		//Check for the mandatory parameters
	//	Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
		try
		{
			if(reqObj.getSerialNumber()==null)
				throw new CustomFault("VIN not specified");
			
			if(reqObj.getTransactionTimestamp()==null)
				throw new CustomFault("Transaction time not specified");
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		//Perform the Business Logic through the BO method
		PricolMachineDetailsBO pricolBOobj = new PricolMachineDetailsBO();
		List<PricolTransactionDetailImpl> response = pricolBOobj.getTransactionDetails(reqObj.getSerialNumber(), reqObj.getTransactionTimestamp());
		
		//Set the response Parameters
		for(int i=0; i<response.size(); i++)
		{
			PricolTransactionDetailRespContract respObj = new PricolTransactionDetailRespContract();
			respObj.setSerialNumber(response.get(i).getSerialNumber());
			respObj.setTransactionTimeStamp(response.get(i).getTransactionTimeStamp());
			respObj.setTransactionData(response.get(i).getTransactionData());
			
			responseList.add(respObj);
		}
		
		return responseList;
	}
}
