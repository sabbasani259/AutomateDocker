package remote.wise.service.datacontract;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * @author Rajani Nagaraju
 *
 */
public class UtilizationDetailServiceRespContract 
{
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
	public TreeMap<String, String> getTimeMachineStatusMap() {
		return timeMachineStatusMap;
	}
	public void setTimeMachineStatusMap(TreeMap<String, String> timeMachineStatusMap) {
		this.timeMachineStatusMap = timeMachineStatusMap;
	}
	
}
