package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessobject.TenancyBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.PendingTenancyCreationReqContract;
import remote.wise.service.datacontract.PendingTenancyCreationRespContract;
//import remote.wise.util.WiseLogger;

/** Implementation class to get the List of accounts with no tenancy
 * @author Rajani Nagaraju
 *
 */
public class PendingTenancyCreationImpl 
{
	
	//Defect Id 1337 - Logger changes
		//public static WiseLogger businessError = WiseLogger.getLogger("PendingTenancyCreationImpl:","businessError");
	int accountId;
	String accountName;
	int parentTenancyId;
	String parentTenancyName;
	
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


	/** This method returns the list of child Accounts that are pending for tenancy creation
	 * @param reqObj UserLoginId and the list of parent tenancy is specified through reqObj
	 * @return Returns the list of Accounts
	 * @throws CustomFault
	 */
	public List<PendingTenancyCreationRespContract> getPendingAccounts(PendingTenancyCreationReqContract reqObj) throws CustomFault
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		List<PendingTenancyCreationRespContract> responseList = new LinkedList<PendingTenancyCreationRespContract>();
		
	//	Logger businessError = Logger.getLogger("businessErrorLogger");
		
		try
		{
			if( (reqObj.getTenancyIdList()==null || reqObj.getTenancyIdList().isEmpty()) )
			{
				throw new CustomFault("Tenancy Id should be passed");
			}
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		
		TenancyBO tenancyBo = new TenancyBO();
		List<PendingTenancyCreationImpl> implRespList = tenancyBo.getPendingTenancies(reqObj.getLoginId(), reqObj.getTenancyIdList());
		
		for(int i=0; i<implRespList.size(); i++)
		{
			PendingTenancyCreationRespContract response = new PendingTenancyCreationRespContract();
			response.setAccountId(implRespList.get(i).getAccountId());
			response.setAccountName(implRespList.get(i).getAccountName());
			response.setParentTenancyId(implRespList.get(i).getParentTenancyId());
			response.setParentTenancyName(implRespList.get(i).getParentTenancyName());
			
			responseList.add(response);
		}
		
		return responseList;
	}
	
}
