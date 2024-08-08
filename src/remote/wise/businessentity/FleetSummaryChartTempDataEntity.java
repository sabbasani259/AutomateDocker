package remote.wise.businessentity;


public class FleetSummaryChartTempDataEntity extends BaseBusinessEntity
{
	private int accountId;
	private double engineOffHours;
	private double engineIdleHours;
	private double engineWorkingHours;
	private String timeStamp;
	
	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public FleetSummaryChartTempDataEntity(){
		//Default Constructor
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public double getEngineOffHours() {
		return engineOffHours;
	}

	public void setEngineOffHours(double engineOffHours) {
		this.engineOffHours = engineOffHours;
	}

	public double getEngineIdleHours() {
		return engineIdleHours;
	}

	public void setEngineIdleHours(double engineIdleHours) {
		this.engineIdleHours = engineIdleHours;
	}

	public double getEngineWorkingHours() {
		return engineWorkingHours;
	}

	public void setEngineWorkingHours(double engineWorkingHours) {
		this.engineWorkingHours = engineWorkingHours;
	}

}
