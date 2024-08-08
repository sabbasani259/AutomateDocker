package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessobject.PricolMachineDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.PricolTransactionSummaryReqContract;
import remote.wise.service.datacontract.PricolTransactionSummaryRespContract;
//import remote.wise.util.WiseLogger;

/** Implementation Class to get the list of registered VINs
 * @author Rajani Nagaraju
 *
 */
public class PricolTransactionSummaryImpl 
{
	//Defect Id 1337 - Logger changes
		//public static WiseLogger businessError = WiseLogger.getLogger("PricolTransactionSummaryImpl:","businessError");
	String serialNumber;
	String imeiNumber;
	String simNumber;
	boolean rollOffStatus;
	String registrationDate;
	
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
	 * @return the imeiNumber
	 */
	public String getImeiNumber() {
		return imeiNumber;
	}
	/**
	 * @param imeiNumber the imeiNumber to set
	 */
	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}
	/**
	 * @return the simNumber
	 */
	public String getSimNumber() {
		return simNumber;
	}
	/**
	 * @param simNumber the simNumber to set
	 */
	public void setSimNumber(String simNumber) {
		this.simNumber = simNumber;
	}
	/**
	 * @return the rollOffStatus
	 */
	public boolean isRollOffStatus() {
		return rollOffStatus;
	}
	/**
	 * @param rollOffStatus the rollOffStatus to set
	 */
	public void setRollOffStatus(boolean rollOffStatus) {
		this.rollOffStatus = rollOffStatus;
	}
	/**
	 * @return the registrationDate
	 */
	public String getRegistrationDate() {
		return registrationDate;
	}
	/**
	 * @param registrationDate the registrationDate to set
	 */
	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}
	
	
	/** This method returns the List of Registered VINs 
	 * @param reqObj specifies the search criteria to get the List of VINs either based on VIN/IMEI/SIM number
	 * @return returns the List of VINs with their IMEI,SIM number and also the RollOff status of the VIN
	 */
	public List<PricolTransactionSummaryRespContract> getTransactionSummary(PricolTransactionSummaryReqContract reqObj)
	{
		List<PricolTransactionSummaryRespContract> responseList = new LinkedList<PricolTransactionSummaryRespContract>();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		//Check for the mandatory parameters
	//	Logger businessError = Logger.getLogger("businessErrorLogger");
		
		try
		{
			if(reqObj.getSearchCriteria()!=null && 
				(!(	(reqObj.getSearchCriteria().equalsIgnoreCase("VIN")) || (reqObj.getSearchCriteria().equalsIgnoreCase("SIM")) || (reqObj.getSearchCriteria().equalsIgnoreCase("IMEI"))))
			   )
			{
				throw new CustomFault("Invalid Search Criteria");
			}
			
			if(reqObj.getSearchCriteria()!=null && reqObj.getSearchText()==null)
			{
				throw new CustomFault("Search Text is NULL");
			}
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
			
		//Perform the Business Logic through the BO method
		PricolMachineDetailsBO pricolBOobj = new PricolMachineDetailsBO();
		List<PricolTransactionSummaryImpl> response = pricolBOobj.getVinList(reqObj.getSearchCriteria(), reqObj.getSearchText(), reqObj.getTenancyId(), reqObj.isPricolTenancy(), reqObj.getRegistrationDate());
		
		//Set the response Parameters
		for(int i=0; i<response.size(); i++)
		{
			PricolTransactionSummaryRespContract respObj = new PricolTransactionSummaryRespContract();
			respObj.setSerialNumber(response.get(i).getSerialNumber());
			respObj.setImeiNumber(response.get(i).getImeiNumber());
			respObj.setSimNumber(response.get(i).getSimNumber());
			respObj.setRegistrationDate(response.get(i).getRegistrationDate());
			respObj.setRollOffStatus(response.get(i).isRollOffStatus());
			
			responseList.add(respObj);
		}
		
		return responseList;
	}
}
