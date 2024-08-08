package remote.wise.service.implementation;

import java.util.HashMap;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetExtendedDetailsEntity;
import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.AssetOwnershipReqContract;
import remote.wise.service.datacontract.AssetOwnershipRespContract;
//import remote.wise.util.WiseLogger;

/** Implementation class to handle Asser Ownerships
 * @author Rajani Nagaraju
 *
 */
public class AssetOwnershipManagementImpl 
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("AssetOwnershipManagementImpl:","businessError");
	
	
	/** This method returns the ownership details of the given serialNumber
	 * @param reqObj serialNumber is specified through this reqObj
	 * @return Returns the ownership details of the specified serialNumber
	 * @throws CustomFault
	 */
	public AssetOwnershipRespContract getAssetOwnership(AssetOwnershipReqContract reqObj) throws CustomFault
	{
		AssetOwnershipRespContract responseObj = new AssetOwnershipRespContract();
		
		DomainServiceImpl domainService = new DomainServiceImpl();
		AssetDetailsBO assetManagement = new AssetDetailsBO();
		HashMap<String,Integer> assetOwners = assetManagement.getAssetOwners(reqObj.getSerialNumber());
		
		responseObj.setOemAccountId(assetOwners.get("OEM"));
		if(assetOwners.get("Dealer") != null)
		{
			responseObj.setDealerAccountId(assetOwners.get("Dealer"));
			//set dealer details
			AccountEntity account = domainService.getAccountObj(assetOwners.get("Dealer"));
			responseObj.setDealerName(account.getAccount_name());
			responseObj.setDealerPhoneNumber(account.getMobile_no());
			responseObj.setDealerEmail(account.getEmailId());
		}
		if(assetOwners.get("Customer") != null)
		{
			responseObj.setCustomerAccountId(assetOwners.get("Customer"));
			//set customer details
			AccountEntity account = domainService.getAccountObj(assetOwners.get("Customer"));
			responseObj.setCustomerName(account.getAccount_name());
			responseObj.setCustomerPhoneNumber(account.getMobile_no());
			responseObj.setCustomerEmail(account.getEmailId());
		}
		responseObj.setSerialNumber(reqObj.getSerialNumber());
		
		
		//get the driver information of the serial number from AssetExtendedDetails
		AssetDetailsBO assetDetailsBO = new AssetDetailsBO();
		AssetExtendedImpl assetExtendedEntity = assetDetailsBO.getAssetExtendedDetails(reqObj.getSerialNumber()); 
		if(assetExtendedEntity!=null)
		{
			responseObj.setDriverName(assetExtendedEntity.getDriverName());
			responseObj.setDriverPhoneNumber(assetExtendedEntity.getDriverContactNumber());
		}
		
		return responseObj;
	}
	
	
	
	
	/** This method sets the AccountOwner for a given serialNumber
	 * @param reqObj serialNumber and the new owner to be set is specified through this reqObj
	 * @return Returns the status String
	 * @throws CustomFault
	 */
	public String setAssetOwnership(AssetOwnershipRespContract reqObj) throws CustomFault
	{
		int ownerAccountId;
		String status = null;
		AssetDetailsBO assetManagement = new AssetDetailsBO();
		
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		try
		{
			if(reqObj.getCustomerAccountId() != 0)
				ownerAccountId = reqObj.getCustomerAccountId();
			else if (reqObj.getDealerAccountId()!= 0)
				ownerAccountId = reqObj.getDealerAccountId();
			else if (reqObj.getOemAccountId() != 0)
				ownerAccountId = reqObj.getOemAccountId();
			else
				throw new CustomFault("Account ID is not specified");
			
			if(reqObj.getSerialNumber()== null)
				throw new CustomFault("Serial Number is not specified");
			
			status = assetManagement.setAssetOwner(reqObj.getSerialNumber(),ownerAccountId);
		}
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		return status;
	}
	
}
