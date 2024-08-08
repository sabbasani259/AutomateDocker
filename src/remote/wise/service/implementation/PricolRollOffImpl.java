package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessobject.PricolMachineDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.PricolRollOffReqContract;
import remote.wise.service.datacontract.PricolRollOffRespContract;
//import remote.wise.util.WiseLogger;



public class PricolRollOffImpl 
{
	//Defect Id 1337 - Logger changes
		//public static WiseLogger businessError = WiseLogger.getLogger("PricolRollOffImpl:","businessError");
	String serialNumber;
	String rollOffStatus;
	
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
	 * @return the rollOffStatus
	 */
	public String getRollOffStatus() {
		return rollOffStatus;
	}
	/**
	 * @param rollOffStatus the rollOffStatus to set
	 */
	public void setRollOffStatus(String rollOffStatus) {
		this.rollOffStatus = rollOffStatus;
	}
	
	public List<PricolRollOffRespContract> rollOffPricolDevice(PricolRollOffReqContract reqObj)
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		List<PricolRollOffRespContract> responseList = new LinkedList<PricolRollOffRespContract>();
		
		//check for mandatory parameters
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		
		try
		{
			if(reqObj.getSerialNumber()==null || reqObj.getSerialNumber().isEmpty())
			{
				throw new CustomFault("VIN not specified");
			}
			
			if(reqObj.getTenancyId()==0)
			{
				throw new CustomFault("TenancyId not specified");
			}
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		//Perform the Business Logic through the BO method
		PricolMachineDetailsBO pricolBOobj = new PricolMachineDetailsBO();
		List<PricolRollOffImpl> response = pricolBOobj.rollOffPricolMachines(reqObj.getTenancyId(),reqObj.getSerialNumber());
		
		for(int i=0; i<response.size(); i++)
		{
			PricolRollOffRespContract respObj = new PricolRollOffRespContract();
			respObj.setSerialNumber(response.get(i).getSerialNumber());
			respObj.setRollOffStatus(response.get(i).getRollOffStatus());
			
			responseList.add(respObj);
		}
		
		return responseList;
	}
}
