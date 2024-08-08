package remote.wise.service.datacontract;

public class PricolTransactionSummaryReqContract 
{
	String searchCriteria;
	String searchText;
	int tenancyId;
	boolean isPricolTenancy;
	String registrationDate;
	
	/**
	 * @return the searchCriteria
	 */
	public String getSearchCriteria() {
		return searchCriteria;
	}
	/**
	 * @param searchCriteria the searchCriteria to set
	 */
	public void setSearchCriteria(String searchCriteria) {
		this.searchCriteria = searchCriteria;
	}
	/**
	 * @return the searchText
	 */
	public String getSearchText() {
		return searchText;
	}
	/**
	 * @param searchText the searchText to set
	 */
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	/**
	 * @return the tenancyId
	 */
	public int getTenancyId() {
		return tenancyId;
	}
	/**
	 * @param tenancyId the tenancyId to set
	 */
	public void setTenancyId(int tenancyId) {
		this.tenancyId = tenancyId;
	}
	/**
	 * @return the isPricolTenancy
	 */
	public boolean isPricolTenancy() {
		return isPricolTenancy;
	}
	/**
	 * @param isPricolTenancy the isPricolTenancy to set
	 */
	public void setPricolTenancy(boolean isPricolTenancy) {
		this.isPricolTenancy = isPricolTenancy;
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
