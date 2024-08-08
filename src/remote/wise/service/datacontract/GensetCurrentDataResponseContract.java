package remote.wise.service.datacontract;

public class GensetCurrentDataResponseContract {

	String voltageLN_L1;
	String voltageLN_L2;
	String voltageLN_L3;
	String voltageLL_L1;
	String voltageLL_L2;
	String voltageLL_L3;
	String current_L1;
	String current_L2;
	String current_L3;
	String frequency;
	String batteryVolt;

	public String getVoltageLN_L1() {
		return voltageLN_L1;
	}

	public void setVoltageLN_L1(String voltageLN_L1) {
		this.voltageLN_L1 = voltageLN_L1;
	}

	public String getVoltageLN_L2() {
		return voltageLN_L2;
	}

	public void setVoltageLN_L2(String voltageLN_L2) {
		this.voltageLN_L2 = voltageLN_L2;
	}

	public String getVoltageLN_L3() {
		return voltageLN_L3;
	}

	public void setVoltageLN_L3(String voltageLN_L3) {
		this.voltageLN_L3 = voltageLN_L3;
	}

	public String getVoltageLL_L1() {
		return voltageLL_L1;
	}

	public void setVoltageLL_L1(String voltageLL_L1) {
		this.voltageLL_L1 = voltageLL_L1;
	}

	public String getVoltageLL_L2() {
		return voltageLL_L2;
	}

	public void setVoltageLL_L2(String voltageLL_L2) {
		this.voltageLL_L2 = voltageLL_L2;
	}

	public String getVoltageLL_L3() {
		return voltageLL_L3;
	}

	public void setVoltageLL_L3(String voltageLL_L3) {
		this.voltageLL_L3 = voltageLL_L3;
	}

	public String getCurrent_L1() {
		return current_L1;
	}

	public void setCurrent_L1(String current_L1) {
		this.current_L1 = current_L1;
	}

	public String getCurrent_L2() {
		return current_L2;
	}

	public void setCurrent_L2(String current_L2) {
		this.current_L2 = current_L2;
	}

	public String getCurrent_L3() {
		return current_L3;
	}

	public void setCurrent_L3(String current_L3) {
		this.current_L3 = current_L3;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getBatteryVolt() {
		return batteryVolt;
	}

	public void setBatteryVolt(String batteryVolt) {
		this.batteryVolt = batteryVolt;
	}

}
