/**
 * 
 */
package remote.wise.service.datacontract;

import java.sql.Timestamp;

/**
 * @author sunayak
 *
 */
public class DetailMachineInfoRespContract {

	private String Current_Owner;
	private String MachineHour;
	private String FW_Version_Number;
	private String SerialNumber;
	private String Last_Reported;
	/**
	 * @return the last_Reported
	 */
	public String getLast_Reported() {
		return Last_Reported;
	}
	/**
	 * @param last_Reported the last_Reported to set
	 */
	public void setLast_Reported(String last_Reported) {
		Last_Reported = last_Reported;
	}
	/**
	 * @return the rollOff_Date
	 */
	public String getRollOff_Date() {
		return RollOff_Date;
	}
	/**
	 * @param rollOff_Date the rollOff_Date to set
	 */
	public void setRollOff_Date(String rollOff_Date) {
		RollOff_Date = rollOff_Date;
	}
	private String RollOff_Date;
	
	public String getCurrent_Owner() {
		return Current_Owner;
	}
	public void setCurrent_Owner(String current_Owner) {
		Current_Owner = current_Owner;
	}
	public String getMachineHour() {
		return MachineHour;
	}
	public void setMachineHour(String machineHour) {
		MachineHour = machineHour;
	}
	public String getFW_Version_Number() {
		return FW_Version_Number;
	}
	public void setFW_Version_Number(String fW_Version_Number) {
		FW_Version_Number = fW_Version_Number;
	}
	public String getSerialNumber() {
		return SerialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}
	
}
