package remote.wise.service.datacontract;

public class PricolRollOffRespContract 
{
	String serialNumber;
	String rollOffStatus;
	
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
	 * @return the rollOffStatus
	 */
	public String getRollOffStatus() {
		return rollOffStatus;
	}
	/**
	 * @param rollOffStatus the rollOffStatus to set
	 */
	public void setRollOffStatus(String rollOffStatus) {
		this.rollOffStatus = rollOffStatus;
	}
	
	
}
