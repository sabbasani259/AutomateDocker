package remote.wise.service.datacontract;

public class MachineBillingReportOutputContract {

	String SerialNumber;
	String RollOffDate;
	String Profile;
	String Model;
	String InstallDate;
	Long NewRolledMachine;
	Long ActualMachineCount;
	String OldSerialNumber;
	String oldNew;
	Long billingCalculation;
	Long InvoicedAmount;
	Long previousBilledCount;
	/**
	 * @return the previousBilledCount
	 */
	public Long getPreviousBilledCount() {
		return previousBilledCount;
	}
	/**
	 * @param previousBilledCount the previousBilledCount to set
	 */
	public void setPreviousBilledCount(Long previousBilledCount) {
		this.previousBilledCount = previousBilledCount;
	}
	/**
	 * @return the invoicedAmount
	 */
	public Long getInvoicedAmount() {
		return InvoicedAmount;
	}
	/**
	 * @param invoicedAmount the invoicedAmount to set
	 */
	public void setInvoicedAmount(Long invoicedAmount) {
		InvoicedAmount = invoicedAmount;
	}
	/**
	 * @return the oldNew
	 */
	public String getOldNew() {
		return oldNew;
	}
	/**
	 * @param oldNew the oldNew to set
	 */
	public void setOldNew(String oldNew) {
		this.oldNew = oldNew;
	}
	
	/**
	 * @return the oldSerialNumber
	 */
	public String getOldSerialNumber() {
		return OldSerialNumber;
	}
	/**
	 * @param oldSerialNumber the oldSerialNumber to set
	 */
	public void setOldSerialNumber(String oldSerialNumber) {
		OldSerialNumber = oldSerialNumber;
	}
	
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return SerialNumber;
	}
	
	/**
	 * @return the newRolledMachine
	 */
	public Long getNewRolledMachine() {
		return NewRolledMachine;
	}
	/**
	 * @param newRolledMachine the newRolledMachine to set
	 */
	public void setNewRolledMachine(Long newRolledMachine) {
		NewRolledMachine = newRolledMachine;
	}
	/**
	 * @return the billingCalculation
	 */
	public Long getBillingCalculation() {
		return billingCalculation;
	}
	/**
	 * @param billingCalculation the billingCalculation to set
	 */
	public void setBillingCalculation(Long billingCalculation) {
		this.billingCalculation = billingCalculation;
	}
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}
	/**
	 * @return the rollOffDate
	 */
	public String getRollOffDate() {
		return RollOffDate;
	}
	/**
	 * @param rollOffDate the rollOffDate to set
	 */
	public void setRollOffDate(String rollOffDate) {
		RollOffDate = rollOffDate;
	}
	/**
	 * @return the profile
	 */
	public String getProfile() {
		return Profile;
	}
	/**
	 * @param profile the profile to set
	 */
	public void setProfile(String profile) {
		Profile = profile;
	}
	/**
	 * @return the model
	 */
	public String getModel() {
		return Model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		Model = model;
	}
	/**
	 * @return the installDate
	 */
	public String getInstallDate() {
		return InstallDate;
	}
	/**
	 * @param installDate the installDate to set
	 */
	public void setInstallDate(String installDate) {
		InstallDate = installDate;
	}
	/**
	 * @return the actualMachineCount
	 */
	public Long getActualMachineCount() {
		return ActualMachineCount;
	}
	/**
	 * @param actualMachineCount the actualMachineCount to set
	 */
	public void setActualMachineCount(Long actualMachineCount) {
		ActualMachineCount = actualMachineCount;
	}
}
