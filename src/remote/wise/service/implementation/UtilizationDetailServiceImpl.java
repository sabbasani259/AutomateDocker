package remote.wise.service.implementation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessobject.ReportDetailsBO;
//import remote.wise.businessobject.ReportDetailsBO2;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.UtilizationDetailServiceReqContract;
import remote.wise.service.datacontract.UtilizationDetailServiceRespContract;
//import remote.wise.util.WiseLogger;

/** Implementation class that provides the details of machine utilization over a given period
 * @author Rajani Nagaraju
 *
 */
public class UtilizationDetailServiceImpl 
{
	//Defect Id 1337 - Logger changes
	//public static WiseLogger businessError = WiseLogger.getLogger("UtilizationDetailServiceImpl:","businessError");
	String serialNumber;
	String period;
	String timeDuration;
	double workingHourPerct;
	TreeMap<String,String> timeMachineStatusMap;
	
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
	 * @return the period
	 */
	public String getPeriod() {
		return period;
	}

	/**
	 * @param period the period to set
	 */
	public void setPeriod(String period) {
		this.period = period;
	}

	/**
	 * @return the timeDuration
	 */
	public String getTimeDuration() {
		return timeDuration;
	}

	/**
	 * @param timeDuration the timeDuration to set
	 */
	public void setTimeDuration(String timeDuration) {
		this.timeDuration = timeDuration;
	}

	/**
	 * @return the workingHourPerct
	 */
	public double getWorkingHourPerct() {
		return workingHourPerct;
	}

	/**
	 * @param workingHourPerct the workingHourPerct to set
	 */
	public void setWorkingHourPerct(double workingHourPerct) {
		this.workingHourPerct = workingHourPerct;
	}

	/**
	 * @return the timeMachineStatusMap
	 */
	

	
	/** This method returns the details of Machine Utilization over a given period of time
	 * @param reqObj period and serialNumber is specified through reqObj
	 * @return Returns the list of period for a given machine and the utilization details of the same.
	 * @throws CustomFault
	 */
	public List<UtilizationDetailServiceRespContract> getUtilizationDetailService(UtilizationDetailServiceReqContract reqObj) throws CustomFault
	{
		List<UtilizationDetailServiceRespContract> responseList = new LinkedList<UtilizationDetailServiceRespContract>();
		
		ReportDetailsBO reportsBO = new ReportDetailsBO();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		try
		{
			if(reqObj.getPeriod()==null)
			{
				throw new CustomFault("Period is not specified");
			}
			
			if(reqObj.getSerialNumber()==null)
			{
				throw new CustomFault("Serial number should be specified");
			}
			
			//DF20190107 @ Zakir added condition to check if getPeriod() contains "|"
			if( ! ( (reqObj.getPeriod().equalsIgnoreCase("Today")) ||  (reqObj.getPeriod().equalsIgnoreCase("Week")) ||  (reqObj.getPeriod().equalsIgnoreCase("Month"))
					||  (reqObj.getPeriod().equalsIgnoreCase("Quarter")) ||  (reqObj.getPeriod().equalsIgnoreCase("Year")) || (reqObj.getPeriod().contains("|"))))
			{
				throw new CustomFault("Invalid period");
			}
		}
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		
		List<UtilizationDetailServiceImpl> implList = reportsBO.getUtilizationDetailService(reqObj.getLoginId(),reqObj.getSerialNumber(),reqObj.getPeriod());
		
		
		for(int i=0; i<implList.size(); i++)
		{
			UtilizationDetailServiceRespContract response = new UtilizationDetailServiceRespContract();
			
			response.setPeriod(implList.get(i).getPeriod());
			response.setSerialNumber(implList.get(i).getSerialNumber());
			response.setTimeDuration(implList.get(i).getTimeDuration());
			response.setTimeMachineStatusMap(implList.get(i).getTimeMachineStatusMap());
			response.setWorkingHourPerct(implList.get(i).getWorkingHourPerct());
			
			responseList.add(response);
		}
		
		return responseList;
	}

	public TreeMap<String, String> getTimeMachineStatusMap() {
		return timeMachineStatusMap;
	}

	public void setTimeMachineStatusMap(TreeMap<String, String> timeMachineStatusMap) {
		this.timeMachineStatusMap = timeMachineStatusMap;
	}
}
