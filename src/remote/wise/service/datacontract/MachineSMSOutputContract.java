package remote.wise.service.datacontract;

public class MachineSMSOutputContract {

	String SMSCount;
	String serialNumber;

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
	 * @return the sMSCount
	 */
	public String getSMSCount() {
		return SMSCount;
	}

	/**
	 * @param sMSCount the sMSCount to set
	 */
	public void setSMSCount(String sMSCount) {
		SMSCount = sMSCount;
	}
	
}
