package remote.wise.service.datacontract;

public class PricolTransactionSummaryRespContract 
{
	String serialNumber;
	String imeiNumber;
	String simNumber;
	boolean rollOffStatus;
	String registrationDate;
	
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
	 * @return the imeiNumber
	 */
	public String getImeiNumber() {
		return imeiNumber;
	}
	/**
	 * @param imeiNumber the imeiNumber to set
	 */
	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}
	/**
	 * @return the simNumber
	 */
	public String getSimNumber() {
		return simNumber;
	}
	/**
	 * @param simNumber the simNumber to set
	 */
	public void setSimNumber(String simNumber) {
		this.simNumber = simNumber;
	}
	/**
	 * @return the rollOffStatus
	 */
	public boolean isRollOffStatus() {
		return rollOffStatus;
	}
	/**
	 * @param rollOffStatus the rollOffStatus to set
	 */
	public void setRollOffStatus(boolean rollOffStatus) {
		this.rollOffStatus = rollOffStatus;
	}
	/**
	 * @return the registrationDate
	 */
	public String getRegistrationDate() {
		return registrationDate;
	}
	/**
	 * @param registrationDate the registrationDate to set
	 */
	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}
	
	
}
