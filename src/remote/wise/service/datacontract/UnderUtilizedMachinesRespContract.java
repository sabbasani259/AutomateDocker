package remote.wise.service.datacontract;

public class UnderUtilizedMachinesRespContract {
	private String Serial_no;
	private double Engine_Off_Hours_Perct;
	private double Working_Time;
	private double WorkingTimePercentage;;
	
	public String getSerial_no() {
		return Serial_no;
	}
	public void setSerial_no(String serial_no) {
		Serial_no = serial_no;
	}	
	public double getEngine_Off_Hours_Perct() {
		return Engine_Off_Hours_Perct;
	}
	public void setEngine_Off_Hours_Perct(double engine_Off_Hours_Perct) {
		Engine_Off_Hours_Perct = engine_Off_Hours_Perct;
	}
	public double getWorking_Time() {
		return Working_Time;
	}
	public void setWorking_Time(double working_Time) {
		Working_Time = working_Time;
	}
	public double getWorkingTimePercentage() {
		return WorkingTimePercentage;
	}
	public void setWorkingTimePercentage(double workingTimePercentage) {
		WorkingTimePercentage = workingTimePercentage;
	}
	
}
