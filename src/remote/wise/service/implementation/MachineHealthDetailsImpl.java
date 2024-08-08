package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessobject.AssetMonitoringDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.MachineHealthDetailsReqContract;
import remote.wise.service.datacontract.MachineHealthDetailsRespContract;
//import remote.wise.util.WiseLogger;

/** Implementation class that handles Machine Health details
 * @author Rajani Nagaraju
 *
 */
public class MachineHealthDetailsImpl 
{
	//public static WiseLogger businessError = WiseLogger.getLogger("MachineHealthDetailsImpl:","businessError");
	int parameterId;
	String parameterName;
	String parameterValue;
	String recordType;
	
	/**
	 * @return the parameterId
	 */
	public int getParameterId() {
		return parameterId;
	}

	/**
	 * @param parameterId the parameterId to set
	 */
	public void setParameterId(int parameterId) {
		this.parameterId = parameterId;
	}

	/**
	 * @return the parameterName
	 */
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * @param parameterName the parameterName to set
	 */
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	/**
	 * @return the parameterValue
	 */
	public String getParameterValue() {
		return parameterValue;
	}

	/**
	 * @param parameterValue the parameterValue to set
	 */
	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	/**
	 * @return the recordType
	 */
	public String getRecordType() {
		return recordType;
	}

	/**
	 * @param recordType the recordType to set
	 */
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	
	/** This method returns the current details of various health parameters for a given VIN
	 * @param reqObj VIN is specified through this reqObj
	 * @return Returns the Machine health status for a given VIN
	 * @throws CustomFault
	 */
	public List<MachineHealthDetailsRespContract> getMachineHealthDetails(MachineHealthDetailsReqContract reqObj) throws CustomFault
	{
		List<MachineHealthDetailsRespContract> responseList = new LinkedList<MachineHealthDetailsRespContract>();
		
	//	Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		try
		{
			if(reqObj.getSerialNumber()==null)
			{
				throw new CustomFault("Serial Number should be specified");
			}
		}
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		AssetMonitoringDetailsBO assetMonitoringDetails = new AssetMonitoringDetailsBO();
		List<MachineHealthDetailsImpl> implRespList = assetMonitoringDetails.getMachineHealthDetails(reqObj.getLoginId(), reqObj.getSerialNumber());
		
		for(int i=0; i<implRespList.size(); i++)
		{
			MachineHealthDetailsRespContract response = new MachineHealthDetailsRespContract();
			response.setParameterId(implRespList.get(i).getParameterId());
			response.setParameterName(implRespList.get(i).getParameterName());
			response.setParameterValue(implRespList.get(i).getParameterValue());
			response.setRecordType(implRespList.get(i).getRecordType());
			
			responseList.add(response);
			
		}
		
		return responseList;
	}
}
