package remote.wise.service.implementation;

import java.util.Iterator;

import org.apache.logging.log4j.Logger;


import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.businessobject.TenancyBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;


/**Implementation class to handle sale of asset from dealer to customer
 * @author Rajani Nagaraju
 *
 */
public class AssetSaleFromD2CImpl 
{
	/*public static WiseLogger fatalError = WiseLogger.getLogger("AssetSaleFromD2CImpl:","fatalError");
	public static WiseLogger businessError = WiseLogger.getLogger("AssetSaleFromD2CImpl:","businessError");*/
	
	/** DefectId:839 - Rajani Nagaraju - 20131114 - To enable Machine Movement between tenancies
	 * This method updates the required data when the sale happens from dealer to customer
	 * @param dealerCode dealerCode as String input
	 * @param customerCode customerCode as String input
	 * @param serialNumber VIN
	 * @param transferDate Date of transfer of Asset
	 * @param messageId MessageID of the received message to append to error if raised
	 * @return Returns status string
	 * @throws CustomFault
	 */
	public String assetSaleFromDealerToCust(String sellerCode, String buyerCode, String dealerCode, String serialNumber, String transferDate, String messageId,String saleDate) throws CustomFault
	{
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetSaleFromD2Cservice:","info");
		String status = "SUCCESS-Record Processed";
		AssetDetailsBO assetDetails = new AssetDetailsBO();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
    	
		//Check for Mandatory Parameters
		if(buyerCode==null || buyerCode.trim()==null ||  buyerCode.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter BuyerCode is NULL";
			bLogger.error("EA Processing: SaleFromD2C: "+messageId+" : Mandatory Parameter BuyerCode is NULL");
			return status;
		}
		
		if( dealerCode==null || dealerCode.trim()==null || dealerCode.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter dealerCode is NULL";
			bLogger.error("EA Processing: SaleFromD2C: "+messageId+" : Mandatory Parameter dealerCode is NULL");
			return status;
		}
		
		if(serialNumber==null || serialNumber.trim()==null || serialNumber.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter serialNumber is NULL";
			bLogger.error("EA Processing: SaleFromD2C: "+messageId+" : Mandatory Parameter serialNumber is NULL");
			return status;
		}
		
		if(transferDate==null ||  transferDate.trim()==null || transferDate.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter transferDate is NULL";
			bLogger.error("EA Processing: SaleFromD2C: "+messageId+" : Mandatory Parameter transferDate is NULL");
			return status;
		}
		//DF20171011: KO369761 - Security Check added for input text fields.
		CommonUtil util = new CommonUtil();
		String isValidinput=null;
		
		isValidinput = util.inputFieldValidation(serialNumber);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		try
		{
			TenancyBO tenancyBoObj = new TenancyBO();
			//DF20140325 - Rajani Nagaraju - Get the Corresponding LL Account Code from ECC/CRM Dealer Code from Mapping table
			String llAccountCode = tenancyBoObj.getLLAccountCode(dealerCode);
			
			if(llAccountCode==null)
			{
				throw new CustomFault("Data not found in Mapping table for the Delaer AccountCode:"+dealerCode);
			}
			else
			{
				dealerCode = llAccountCode;
			}
			
			//DF20140325 - Rajani Nagaraju - Get the data from Mapping table for BuyerCode/SellerCode - At this point of time not sure whether the
			//Buyer or seller is a customer/dealer. So just check whether the corresponding entry is present in Mapping table. If present take the ll delaer code
			//else just retain the input buyer or seller code.
			String llSellerCode = tenancyBoObj.getLLAccountCode(sellerCode);
			if(llSellerCode==null)
			{
				bLogger.error("EA Processing: SaleFromD2C: "+messageId+": Data not found in Mapping table for the Seller AccountCode:"+sellerCode);
			}
			else
			{
				sellerCode = llSellerCode;
			}
			
			String llBuyerCode = tenancyBoObj.getLLAccountCode(buyerCode);
			if(llBuyerCode==null)
			{
				bLogger.error("EA Processing: SaleFromD2C: "+messageId+":Data not found in Mapping table for the Buyer AccountCode:"+buyerCode);
			}
			else
			{
				buyerCode = llBuyerCode;
			}
			
		}
		
		catch(CustomFault e)
		{
			status = "FAILURE-"+e.getFaultInfo();
			bLogger.error("EA Processing: SaleFromD2C: "+messageId+" : "+e.getFaultInfo());
			return status;
		}

		dealerCode = dealerCode.replaceAll("\\s","") ;
		buyerCode = buyerCode.replaceAll("\\s","") ;
		serialNumber = serialNumber.replaceAll("\\s","") ;
		transferDate = transferDate.replaceAll("\\s","") ;
		String SerialNumber =null;
		

		//To handle blank space as input to sellerCode
		if(sellerCode!=null)
		{
			String chkSellerCode = sellerCode.replaceAll("\\s","") ;
			if(chkSellerCode.length()==0)
			{
				sellerCode=null;
			}
		}

		//update the asset owner from dealer to customer
		/*if(serialNumber!=null)
		{
			if(serialNumber.length()==7)
			{
				AssetDetailsBO assetManagement = new AssetDetailsBO();
				SerialNumber = assetManagement.getSerialNumberMachineNumber(serialNumber);
				status = assetDetails.assetSaleFromDealerToCustomer(sellerCode, buyerCode, dealerCode, SerialNumber, transferDate, messageId);
			}
			else 
				status = assetDetails.assetSaleFromDealerToCustomer(sellerCode, buyerCode, dealerCode, serialNumber, transferDate, messageId);
		}*/
		//2014-06-18 : Checking for 7 digit Machine Number - Deepthi
		iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber + sellerCode + buyerCode + dealerCode + transferDate+saleDate);
		status = assetDetails.assetSaleFromDealerToCustomer(sellerCode, buyerCode, dealerCode, serialNumber, transferDate, messageId,saleDate);
		
		iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber+"Status" + status);
		//DF20150311 - Rajani Nagaraju - Updating AssetOwnerSnapshot for a VIN on real time
		if(status.contains("SUCCESS")){
			new CurrentAssetOwnerDetailsImpl().setVinOwnerDetails(serialNumber);
		}
		
		return status;

	}
}
