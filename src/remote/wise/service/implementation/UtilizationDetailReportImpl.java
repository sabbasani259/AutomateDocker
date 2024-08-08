package remote.wise.service.implementation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessobject.ReportDetailsBO;
//import remote.wise.businessobject.ReportDetailsBO2;
//import remote.wise.businessobject.ReportDetailsBO3;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.UtilizationDetailReportReqContract;
import remote.wise.service.datacontract.UtilizationDetailReportRespContract;
//import remote.wise.util.WiseLogger;

/** Implementation class that renders the Machine Utilization Detail Report
 * @author Rajani Nagaraju
 *
 */
public class UtilizationDetailReportImpl 
{
	
	//Defect Id 1337 - Logger changes
	//public static WiseLogger businessError = WiseLogger.getLogger("UtilizationDetailReportImpl:","businessError");
	String serialNumber;
	String nickName;
	String dateInString;
	String dayInString;
	TreeMap<String,String> timeMachineStatusMap;
	double EngineRunDuration;
	double EngineOffDuration;
	double EngineWorkingDuration;
	double machineUtilizationPerct;
	int daysWithNoEngineRun;
	
	/**
	 * @return the serialNumber as String
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber the VIN as String input
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	/**
	 * @return the nickName
	 */
	public String getNickName() {
		return nickName;
	}
	/**
	 * @param nickName the MachineName to set as String input
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	
	/**
	 * @return the dateInString
	 */
	public String getDateInString() {
		return dateInString;
	}
	/**
	 * @param dateInString the dateInString to set
	 */
	public void setDateInString(String dateInString) {
		this.dateInString = dateInString;
	}
	
	/**
	 * @return the dayInString
	 */
	public String getDayInString() {
		return dayInString;
	}
	/**
	 * @param dayInString the dayInString to set
	 */
	public void setDayInString(String dayInString) {
		this.dayInString = dayInString;
	}
	
	
	/**
	 * @return the timeMachineStatusMap
	 */
	
	
	/**
	 * @return the engineRunDuration
	 */
	public double getEngineRunDuration() {
		return EngineRunDuration;
	}
	public TreeMap<String, String> getTimeMachineStatusMap() {
		return timeMachineStatusMap;
	}
	public void setTimeMachineStatusMap(TreeMap<String, String> timeMachineStatusMap) {
		this.timeMachineStatusMap = timeMachineStatusMap;
	}
	/**
	 * @param engineRunDuration the engineRunDuration to set
	 */
	public void setEngineRunDuration(double engineRunDuration) {
		EngineRunDuration = engineRunDuration;
	}
	
	
	public int getDaysWithNoEngineRun() {
		return daysWithNoEngineRun;
	}
	public void setDaysWithNoEngineRun(int daysWithNoEngineRun) {
		this.daysWithNoEngineRun = daysWithNoEngineRun;
	}
	/**
	 * @return the engineOffDuration
	 */
	
	
	
	/**
	 * @return the engineWorkingDuration
	 */
	public double getEngineWorkingDuration() {
		return EngineWorkingDuration;
	}
	public double getEngineOffDuration() {
		return EngineOffDuration;
	}
	public void setEngineOffDuration(double engineOffDuration) {
		EngineOffDuration = engineOffDuration;
	}
	/**
	 * @param engineWorkingDuration the engineWorkingDuration to set
	 */
	public void setEngineWorkingDuration(double engineWorkingDuration) {
		EngineWorkingDuration = engineWorkingDuration;
	}
	
	
	/**
	 * @return the machineUtilizationPerct
	 */
	public double getMachineUtilizationPerct() {
		return machineUtilizationPerct;
	}
	/**
	 * @param machineUtilizationPerct the machineUtilizationPerct to set
	 */
	public void setMachineUtilizationPerct(double machineUtilizationPerct) {
		this.machineUtilizationPerct = machineUtilizationPerct;
	}
	
	
	
	
	/** This method returns the Machine Utilization Details for the user accessible list of serial Numbers
	 * @param reqObj LoginId, period and other filters are specified through this reqObj
	 * @return Returns the utilization details for the user accessible list of serial Numbers and for the given filter criteria
	 * @throws CustomFault
	 */
	public List<UtilizationDetailReportRespContract>  getMachineUtilizationReportDetails ( UtilizationDetailReportReqContract reqObj) throws CustomFault
	{
		List<UtilizationDetailReportRespContract> responseList = new LinkedList<UtilizationDetailReportRespContract>();
		
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		ReportDetailsBO reportDetails = new ReportDetailsBO();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		try
		{
			if(reqObj.getPeriod()==null)
			{
				throw new CustomFault("Period is not specified");
			}
			
			if(reqObj.getTenancyIdList()==null)
			{
				throw new CustomFault("Tenancy Id List should be specified");
			}
			
			
			if( ! ( (reqObj.getPeriod().equalsIgnoreCase("Today")) ||  (reqObj.getPeriod().equalsIgnoreCase("Week")) ) )
			{
				throw new CustomFault("Invalid period");
			}
		}
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		
		List<UtilizationDetailReportImpl> implObjList = reportDetails.getUtilizationDetailReport(reqObj.getLoginId(), reqObj.getTenancyIdList(),
				reqObj.getMachineGroupIdList(), reqObj.getMachineProfileIdList(), reqObj.getModelIdList(), reqObj.getPeriod(),reqObj.getSerialNumList());
		UtilizationDetailReportRespContract respList =null;
		
		for(int i=0; i<implObjList.size(); i++)
		{
			respList = new UtilizationDetailReportRespContract();
			respList.setSerialNumber(implObjList.get(i).getSerialNumber());
			respList.setNickName(implObjList.get(i).getNickName());
			respList.setDateInString(implObjList.get(i).getDateInString());
			respList.setDayInString(implObjList.get(i).getDayInString());
			respList.setTimeMachineStatusMap(implObjList.get(i).getTimeMachineStatusMap());
			//Changes Done by Juhi On 6 May 2013
			if (implObjList.get(i).getEngineOffDuration()<0) 
			{
				respList.setEngineOffDuration(new Long(0));
			} else {
				respList.setEngineOffDuration(implObjList.get(i).getEngineOffDuration());
			}
			if (implObjList.get(i).getEngineRunDuration()<0) 
			{
				respList.setEngineRunDuration(new Long(0));
			} else {
				respList.setEngineRunDuration(implObjList.get(i).getEngineRunDuration());
			}
			if (implObjList.get(i).getEngineWorkingDuration()<0) {
				respList.setEngineWorkingDuration(0);

			} else {
				respList.setEngineWorkingDuration(implObjList.get(i).getEngineWorkingDuration());

			}
			if (implObjList.get(i).getMachineUtilizationPerct()<0) {
				respList.setMachineUtilizationPerct(0);

			} else {
				respList.setMachineUtilizationPerct(implObjList.get(i).getMachineUtilizationPerct());

			}
			//Changes Done by Juhi On 6 May 2013
			responseList.add(respList);
		}
		
		return responseList;
	}
}
