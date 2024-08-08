package remote.wise.service.datacontract;

public class AlertSummaryRespContract {

	
	private int YellowThresholdValue;
	private int RedThresholdValue;
	public int getYellowThresholdValue() {
		return YellowThresholdValue;
	}
	public void setYellowThresholdValue(int yellowThresholdValue) {
		YellowThresholdValue = yellowThresholdValue;
	}
	public int getRedThresholdValue() {
		return RedThresholdValue;
	}
	public void setRedThresholdValue(int redThresholdValue) {
		RedThresholdValue = redThresholdValue;
	}
	
}
