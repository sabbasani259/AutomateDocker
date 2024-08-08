package remote.wise.service.implementation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.TenancyEntity;
import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.businessobject.TenancyBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AssetGroupReqContract;
import remote.wise.service.datacontract.AssetGroupRespContract;
import remote.wise.service.datacontract.UserAssetDetailsReqContract;
import remote.wise.service.datacontract.UserAssetDetailsRespContract;

public class UserAssetDetailsImpl 
{
	//Defect Id 1337 - Logger changes
	//public static WiseLogger businessError = WiseLogger.getLogger("UserAssetDetailsImpl:","businessError");
	//static Logger businessError = Logger.getLogger("businessErrorLogger");
	public UserAssetDetailsRespContract getUserAssetDetails(UserAssetDetailsReqContract reqObj) throws CustomFault
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		if(reqObj.getSerialNumber()==null)
		{
			bLogger.error("No Serial Number is passed");
			throw new CustomFault("No Serial Number is passed");
		}
		if(reqObj.getUserTenancyId()==0)
		{
			bLogger.error("User Tenancy Id is not passed");
			throw new CustomFault("User Tenancy Id should be passed");
		}
		if(reqObj.getLoginId()==null)
		{
			bLogger.error("Login Id is not passed");
			throw new CustomFault("Login Id should be passed");
		}
				
		//get asset details
		AssetDetailsBO assetDetails = new AssetDetailsBO();
		assetDetails = assetDetails.getAssetProfile(reqObj.getSerialNumber());
		
		UserAssetDetailsRespContract response = new UserAssetDetailsRespContract();
		//response.setSerialNumber(assetDetails.getSerialNumber());
	
		response.setAssetClassName(assetDetails.getAssetClassName());
		//CR256: Added sale date for dislaying on Fleet General - Deepthi
		response.setAssetGroupName(assetDetails.getAssetGroupName()+"#"+assetDetails.getSaleDate());
		response.setAssetTypeName(assetDetails.getAssetTypeName());
		response.setLifeHours(reqObj.getLifeHours());
		response.setMake(assetDetails.getMake());
		response.setAssetTypeCode(assetDetails.getAssetTypeCode());	//CR353.n
		response.setSubscription(assetDetails.getSubscription());	//CR353.n
		String iccid = null;
		// DF20170525 - @SU334449: Updated ICCID to solve the number format exception
		if(assetDetails.getIccidNumber()==null || (assetDetails.getIccidNumber().isEmpty())){
			iccid = "NA";
		}else{
			iccid=assetDetails.getIccidNumber();
		}
		String exWarrantyType=null;
		if(assetDetails.getExtendedWarrantytype()==null || (assetDetails.getExtendedWarrantytype().isEmpty()) )
		{
			exWarrantyType="NA";
		}else
		{
			exWarrantyType=assetDetails.getExtendedWarrantytype();
		}
		
		response.setSerialNumber(assetDetails.getSerialNumber()+"|"+exWarrantyType);
		iLogger.info("response...SN.."+response.getSerialNumber());
		
		//Shajesh :05-10-2020: CR- Including the installatin_date and Customer code on the Fleet General
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String installationDate=null;
		if(assetDetails.getInstall_date()!=null){
		installationDate = dateFormat.format(assetDetails.getInstall_date());			
		}else{
			installationDate ="NA";
		}

		String saleDate=null;
		if(assetDetails.getSaleDate()!=null){
			saleDate = dateFormat.format(assetDetails.getSaleDate());			
			}else{
				saleDate ="NA";
			}
		
		response.setRenewalDate(assetDetails.getRenewalDate()+"|"+installationDate);
		//added by smitha on june 26th 2013....DefectID 136
		
		response.setDriverContactNumber(assetDetails.getDriverContactNumber());
		response.setDriverName(assetDetails.getDriverName());
		
		response.setImeiNumber(assetDetails.getImeiNumber()+"|"+iccid);
		response.setSimNumber(assetDetails.getSimNumber());
		
		//end [june 26th 2013]
		//get the asset owner account details - customer and dealer details
		AssetDetailsBO assetDetailsObj = new AssetDetailsBO();
		HashMap<String,Integer> assetOwners = assetDetailsObj.getAssetOwners(reqObj.getSerialNumber());
		
		//set asset owner details to response Obj
		DomainServiceImpl domainService = new DomainServiceImpl();
		//Shajesh :22-09-2020: CR- Including the installatin_date and Customer code on the Fleet General
		if(assetOwners.get("Dealer")!=null)
		{
			AccountEntity accountEntity = domainService.getAccountObj(assetOwners.get("Dealer"));
			response.setDealerName(accountEntity.getAccount_name()+"-"+accountEntity.getAccountCode());
			response.setDealerPhoneNumber(accountEntity.getMobile_no());
			response.setDealerEmailId(accountEntity.getEmailId());
			
			TenancyBO tenancyBO = new TenancyBO();
			TenancyEntity tenancyEntity = tenancyBO.getTenancyId(assetOwners.get("Dealer"));
			//DF20141219 - Rajani Nagraaju - Null Pointer Check if the tenancy is not created for the account
			if(tenancyEntity!=null)
				response.setDealerTenancyId(tenancyEntity.getTenancy_id());
		}	
		//Shajesh :22-09-2020: CR- Including the installatin_date and Customer code on the Fleet General	
		if(assetOwners.get("Customer")!=null)
		{
			AccountEntity accountEntity = domainService.getAccountObj(assetOwners.get("Customer"));
			response.setCustomerName(accountEntity.getAccount_name()+"-"+accountEntity.getMappingCode());
			response.setCustomerPhoneNumber(accountEntity.getMobile_no());
			response.setCustomerEmailId(accountEntity.getEmailId());
			
		}
		
		
		//get user machine group details
		AssetGroupImpl assetCustomGroup = new AssetGroupImpl();
		List<String> serialNumberList = new LinkedList<String>();
		serialNumberList.add(reqObj.getSerialNumber());
		List<Integer> tenancyIdList= new LinkedList<Integer>();
		tenancyIdList.add(reqObj.getUserTenancyId());
		
		AssetGroupReqContract reqObject = new AssetGroupReqContract();
		reqObject.setLoginId(reqObj.getLoginId());
		reqObject.setSerialNumberList(serialNumberList);
		reqObject.setTenancyIdList(tenancyIdList);
		
		ArrayList<AssetGroupRespContract> customGroupList = assetCustomGroup.getAssetGroup(reqObject);
				
		//set custom asset group details in response obj for the given serial number and user combination
		if(customGroupList!=null)
		{
			List<String> customAssetGroupNameList = new LinkedList<String>();
			for(int i=0; i<customGroupList.size(); i++)
			{
				customAssetGroupNameList.add(customGroupList.get(i).getAssetGroupName());
			}
			response.setAssetCustomGroupName(customAssetGroupNameList);
		}
		iLogger.info("response...."+response);
		return response;
	}
}
