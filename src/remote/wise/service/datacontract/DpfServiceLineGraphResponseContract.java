/**
 * CR397 : 20230831 : Dhiraj Kumar : StageV VD development changes
 */
package remote.wise.service.datacontract;

public class DpfServiceLineGraphResponseContract {

	protected String timestamp;
	protected int dpfFillLevel;
	protected double hmr;
	protected int operatorInhibit;
	protected int dpfRegenerationStart;
	protected int dpfRegenerationEnd;
	protected int automaticRegeneration;
	protected int manualRegeneration;
	protected int serviceRegeneration;

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public int getDpfFillLevel() {
		return dpfFillLevel;
	}

	public void setDpfFillLevel(int dpfFillLevel) {
		this.dpfFillLevel = dpfFillLevel;
	}

	public double getHmr() {
		return hmr;
	}

	public void setHmr(double hmr) {
		this.hmr = hmr;
	}

	public int getOperatorInhibit() {
		return operatorInhibit;
	}

	public void setOperatorInhibit(int operatorInhibit) {
		this.operatorInhibit = operatorInhibit;
	}

	public int getDpfRegenerationStart() {
		return dpfRegenerationStart;
	}

	public void setDpfRegenerationStart(int dpfRegenerationStart) {
		this.dpfRegenerationStart = dpfRegenerationStart;
	}

	public int getDpfRegenerationEnd() {
		return dpfRegenerationEnd;
	}

	public void setDpfRegenerationEnd(int dpfRegenerationEnd) {
		this.dpfRegenerationEnd = dpfRegenerationEnd;
	}

	public int getAutomaticRegeneration() {
		return automaticRegeneration;
	}

	public void setAutomaticRegeneration(int automaticRegeneration) {
		this.automaticRegeneration = automaticRegeneration;
	}

	public int getManualRegeneration() {
		return manualRegeneration;
	}

	public void setManualRegeneration(int manualRegeneration) {
		this.manualRegeneration = manualRegeneration;
	}

	public int getServiceRegeneration() {
		return serviceRegeneration;
	}

	public void setServiceRegeneration(int serviceRegeneration) {
		this.serviceRegeneration = serviceRegeneration;
	}

}
