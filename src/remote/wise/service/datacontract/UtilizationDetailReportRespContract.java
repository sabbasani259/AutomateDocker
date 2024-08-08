package remote.wise.service.datacontract;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * @author Rajani Nagaraju
 *
 */
public class UtilizationDetailReportRespContract 
{
	String serialNumber;
	String nickName;
	String dateInString;
	String dayInString;
	TreeMap<String,String> timeMachineStatusMap;
	double EngineRunDuration;
//	Long EngineOffDuration;
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
	public int getDaysWithNoEngineRun() {
		return daysWithNoEngineRun;
	}
	public void setDaysWithNoEngineRun(int daysWithNoEngineRun) {
		this.daysWithNoEngineRun = daysWithNoEngineRun;
	}
	
}
