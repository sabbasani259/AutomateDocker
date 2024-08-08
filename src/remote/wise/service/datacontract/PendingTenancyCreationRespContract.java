package remote.wise.service.datacontract;

/**
 * @author Rajani Nagaraju
 *
 */
public class PendingTenancyCreationRespContract 
{	
	int accountId;
	String accountName;
	int parentTenancyId;
	String parentTenancyName;
	
	public PendingTenancyCreationRespContract()
	{
		accountId =0;
		accountName = null;
		parentTenancyId =0;
		parentTenancyName =null;
	}

	/**
	 * @return the accountId
	 */
	public int getAccountId() {
		return accountId;
	}

	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * @param accountName the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	/**
	 * @return the parentTenancyId
	 */
	public int getParentTenancyId() {
		return parentTenancyId;
	}

	/**
	 * @param parentTenancyId the parentTenancyId to set
	 */
	public void setParentTenancyId(int parentTenancyId) {
		this.parentTenancyId = parentTenancyId;
	}

	/**
	 * @return the parentTenancyName
	 */
	public String getParentTenancyName() {
		return parentTenancyName;
	}

	/**
	 * @param parentTenancyName the parentTenancyName to set
	 */
	public void setParentTenancyName(String parentTenancyName) {
		this.parentTenancyName = parentTenancyName;
	}
	
		
}
