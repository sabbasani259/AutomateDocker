package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.SMSAlertsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.SMSAlertDetailReqContract;
import remote.wise.service.datacontract.SMSAlertsReqContract;
import remote.wise.service.datacontract.SMSAlertsRespContract;
//import remote.wise.util.WiseLogger;

public class SMSAlertsImpl 
{
	/*public static WiseLogger businessError = WiseLogger.getLogger("SMSAlertsImpl:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("SMSAlertsImpl:","fatalError");*/
	
	private String serialNumber;
	private String cmhr;
	private String smsReceivedTime;
	private String failureReason;
	private int towawayStatus;
	private int highCoolantTempStatus;
	private int lowEngineOilPressureStatus;
	private int waterInFuelStatus;
	private int blockedAirFilterStatus;
	
		
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
	 * @return the cmhr
	 */
	public String getCmhr() {
		return cmhr;
	}

	/**
	 * @param cmhr the cmhr to set
	 */
	public void setCmhr(String cmhr) {
		this.cmhr = cmhr;
	}

	/**
	 * @return the smsReceivedTime
	 */
	public String getSmsReceivedTime() {
		return smsReceivedTime;
	}

	/**
	 * @param smsReceivedTime the smsReceivedTime to set
	 */
	public void setSmsReceivedTime(String smsReceivedTime) {
		this.smsReceivedTime = smsReceivedTime;
	}

	/**
	 * @return the failureReason
	 */
	public String getFailureReason() {
		return failureReason;
	}

	/**
	 * @param failureReason the failureReason to set
	 */
	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	/**
	 * @return the towawayStatus
	 */
	public int getTowawayStatus() {
		return towawayStatus;
	}

	/**
	 * @param towawayStatus the towawayStatus to set
	 */
	public void setTowawayStatus(int towawayStatus) {
		this.towawayStatus = towawayStatus;
	}

	/**
	 * @return the highCoolantTempStatus
	 */
	public int getHighCoolantTempStatus() {
		return highCoolantTempStatus;
	}

	/**
	 * @param highCoolantTempStatus the highCoolantTempStatus to set
	 */
	public void setHighCoolantTempStatus(int highCoolantTempStatus) {
		this.highCoolantTempStatus = highCoolantTempStatus;
	}

	/**
	 * @return the lowEngineOilPressureStatus
	 */
	public int getLowEngineOilPressureStatus() {
		return lowEngineOilPressureStatus;
	}

	/**
	 * @param lowEngineOilPressureStatus the lowEngineOilPressureStatus to set
	 */
	public void setLowEngineOilPressureStatus(int lowEngineOilPressureStatus) {
		this.lowEngineOilPressureStatus = lowEngineOilPressureStatus;
	}

	/**
	 * @return the waterInFuelStatus
	 */
	public int getWaterInFuelStatus() {
		return waterInFuelStatus;
	}

	/**
	 * @param waterInFuelStatus the waterInFuelStatus to set
	 */
	public void setWaterInFuelStatus(int waterInFuelStatus) {
		this.waterInFuelStatus = waterInFuelStatus;
	}

	/**
	 * @return the blockedAirFilterStatus
	 */
	public int getBlockedAirFilterStatus() {
		return blockedAirFilterStatus;
	}

	/**
	 * @param blockedAirFilterStatus the blockedAirFilterStatus to set
	 */
	public void setBlockedAirFilterStatus(int blockedAirFilterStatus) {
		this.blockedAirFilterStatus = blockedAirFilterStatus;
	}

	
	public List<SMSAlertsRespContract> getSMSAlerts(SMSAlertsReqContract reqContract)
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		List<SMSAlertsRespContract> responseList = new LinkedList<SMSAlertsRespContract>();
		
		try
		{
			if( ! ( (reqContract.getSerialNumber()==null) || (reqContract.getSerialNumber().trim().length()==0)) )
			{
				if(! ((reqContract.getSerialNumber().length()==17) || (reqContract.getSerialNumber().length()==7)) )
				{
					throw new CustomFault("Please enter the complete PIN Number or machine number");
				}
			}
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+e);
			return responseList;
		}
		
		List<SMSAlertsImpl> implObj = new SMSAlertsBO().getSMSAlerts(1, reqContract.getSerialNumber(), null, null);
		for(int i=0; i<implObj.size(); i++)
		{
			SMSAlertsRespContract respObj = new SMSAlertsRespContract();
			respObj.setBlockedAirFilterStatus(implObj.get(i).getBlockedAirFilterStatus());
			respObj.setCmhr(implObj.get(i).getCmhr());
			respObj.setFailureReason(implObj.get(i).getFailureReason());
			respObj.setHighCoolantTempStatus(implObj.get(i).getHighCoolantTempStatus());
			respObj.setLowEngineOilPressureStatus(implObj.get(i).getLowEngineOilPressureStatus());
			respObj.setSerialNumber(implObj.get(i).getSerialNumber());
			respObj.setSmsReceivedTime(implObj.get(i).getSmsReceivedTime());
			respObj.setTowawayStatus(implObj.get(i).getTowawayStatus());
			respObj.setWaterInFuelStatus(implObj.get(i).getWaterInFuelStatus());
			
			responseList.add(respObj);
		}
		
		return responseList;
	}
	
	public List<SMSAlertsRespContract> getSMSAlertDetails(SMSAlertDetailReqContract reqContract)
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		List<SMSAlertsRespContract> responseList = new LinkedList<SMSAlertsRespContract>();
		
		try
		{
			if(reqContract.getSerialNumber()==null || reqContract.getSerialNumber().trim().length()==0)
			{
				throw new CustomFault("Mandatory Parameter serial number is null");
			}
			
			if(! ((reqContract.getSerialNumber().length()==17) || (reqContract.getSerialNumber().length()==7)) )
			{
				throw new CustomFault("Please enter the complete PIN Number or machine number");
			}
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+e);
			return responseList;
		}
		
		List<SMSAlertsImpl> implObj = new SMSAlertsBO().getSMSAlerts(0, reqContract.getSerialNumber(), reqContract.getFromDate(), reqContract.getToDate());
		for(int i=0; i<implObj.size(); i++)
		{
			SMSAlertsRespContract respObj = new SMSAlertsRespContract();
			respObj.setBlockedAirFilterStatus(implObj.get(i).getBlockedAirFilterStatus());
			respObj.setCmhr(implObj.get(i).getCmhr());
			respObj.setFailureReason(implObj.get(i).getFailureReason());
			respObj.setHighCoolantTempStatus(implObj.get(i).getHighCoolantTempStatus());
			respObj.setLowEngineOilPressureStatus(implObj.get(i).getLowEngineOilPressureStatus());
			respObj.setSerialNumber(implObj.get(i).getSerialNumber());
			respObj.setSmsReceivedTime(implObj.get(i).getSmsReceivedTime());
			respObj.setTowawayStatus(implObj.get(i).getTowawayStatus());
			respObj.setWaterInFuelStatus(implObj.get(i).getWaterInFuelStatus());
			
			responseList.add(respObj);
		}
		
		return responseList;
	}
}
