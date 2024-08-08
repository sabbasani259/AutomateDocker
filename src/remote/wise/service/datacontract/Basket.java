package remote.wise.service.datacontract;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

//20231127 CR452-AirtelApiIntegration-prasad
public class Basket{
    public String customerId;
    @JsonProperty(value ="basketId")
    public String basketId;
    public String basketName;
    public String parentBasketId;
    public String creationDate;
    public String managedBy;
    public String emailId;
    public String firstName;
    public String lastName;
    public String mobileNo;
    public String totalSims;
    public String activeSims;
    public String availableSims;
    public String testModeSims;
    public String safeCustodySims;
    public String inActiveSims;
    public String inProgressSims;
    public String suspendedSims;
    public String basketPermission;
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getBasketId() {
		return basketId;
	}
	public void setBasketId(String basketId) {
		this.basketId = basketId;
	}
	public String getBasketName() {
		return basketName;
	}
	public void setBasketName(String basketName) {
		this.basketName = basketName;
	}
	public String getParentBasketId() {
		return parentBasketId;
	}
	public void setParentBasketId(String parentBasketId) {
		this.parentBasketId = parentBasketId;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public String getManagedBy() {
		return managedBy;
	}
	public void setManagedBy(String managedBy) {
		this.managedBy = managedBy;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getTotalSims() {
		return totalSims;
	}
	public void setTotalSims(String totalSims) {
		this.totalSims = totalSims;
	}
	public String getActiveSims() {
		return activeSims;
	}
	public void setActiveSims(String activeSims) {
		this.activeSims = activeSims;
	}
	public String getAvailableSims() {
		return availableSims;
	}
	public void setAvailableSims(String availableSims) {
		this.availableSims = availableSims;
	}
	public String getTestModeSims() {
		return testModeSims;
	}
	public void setTestModeSims(String testModeSims) {
		this.testModeSims = testModeSims;
	}
	public String getSafeCustodySims() {
		return safeCustodySims;
	}
	public void setSafeCustodySims(String safeCustodySims) {
		this.safeCustodySims = safeCustodySims;
	}
	public String getInActiveSims() {
		return inActiveSims;
	}
	public void setInActiveSims(String inActiveSims) {
		this.inActiveSims = inActiveSims;
	}
	public String getInProgressSims() {
		return inProgressSims;
	}
	public void setInProgressSims(String inProgressSims) {
		this.inProgressSims = inProgressSims;
	}
	public String getSuspendedSims() {
		return suspendedSims;
	}
	public void setSuspendedSims(String suspendedSims) {
		this.suspendedSims = suspendedSims;
	}
	public String getBasketPermission() {
		return basketPermission;
	}
	public void setBasketPermission(String basketPermission) {
		this.basketPermission = basketPermission;
	}
	@Override
	public String toString() {
		return "Basket [customerId=" + customerId + ", basketId=" + basketId + ", basketName=" + basketName
				+ ", parentBasketId=" + parentBasketId + ", creationDate=" + creationDate + ", managedBy=" + managedBy
				+ ", emailId=" + emailId + ", firstName=" + firstName + ", lastName=" + lastName + ", mobileNo="
				+ mobileNo + ", totalSims=" + totalSims + ", activeSims=" + activeSims + ", availableSims="
				+ availableSims + ", testModeSims=" + testModeSims + ", safeCustodySims=" + safeCustodySims
				+ ", inActiveSims=" + inActiveSims + ", inProgressSims=" + inProgressSims + ", suspendedSims="
				+ suspendedSims + ", basketPermission=" + basketPermission + ", getCustomerId()=" + getCustomerId()
				+ ", getBasketId()=" + getBasketId() + ", getBasketName()=" + getBasketName() + ", getParentBasketId()="
				+ getParentBasketId() + ", getCreationDate()=" + getCreationDate() + ", getManagedBy()="
				+ getManagedBy() + ", getEmailId()=" + getEmailId() + ", getFirstName()=" + getFirstName()
				+ ", getLastName()=" + getLastName() + ", getMobileNo()=" + getMobileNo() + ", getTotalSims()="
				+ getTotalSims() + ", getActiveSims()=" + getActiveSims() + ", getAvailableSims()=" + getAvailableSims()
				+ ", getTestModeSims()=" + getTestModeSims() + ", getSafeCustodySims()=" + getSafeCustodySims()
				+ ", getInActiveSims()=" + getInActiveSims() + ", getInProgressSims()=" + getInProgressSims()
				+ ", getSuspendedSims()=" + getSuspendedSims() + ", getBasketPermission()=" + getBasketPermission()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
    
    
	
    
}


    


