package remote.wise.service.implementation;

import java.util.Iterator;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.PartnershipMapping;
import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.businessobject.TenancyBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

/**Implementation class to handle primary dealer transfer
 * @author Rajani Nagaraju
 *
 */
public class PrimaryDealerTransferImpl 
{
	//public static WiseLogger businessError = WiseLogger.getLogger("PrimaryDealerTransferImpl:","businessError");
	
	/** This method updates the required data when the primary dealer transfer happens
	 * @param customerCode customerCode as String input
	 * @param dealerCode dealerCode as String input
	 * @param transferDate Date of transfer of primary dealer
	 * @return Returns status string
	 * @throws CustomFault
	 */
	public String primaryDealerTransfer(String customerCode, String dealerCode, String transferDate,String SerialNumber,String messageId)
	{
		String status = "SUCCESS-Record Processed";
		AssetDetailsBO assetDetails = new AssetDetailsBO();
		AssetSaleFromD2CImpl AssetSaleFromD2C = new AssetSaleFromD2CImpl();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		try
		{
			//Check for Mandatory Parameters
			if(dealerCode==null ||	dealerCode.trim()==null ||  dealerCode.replaceAll("\\s","").length()==0 )
			{
				status = "FAILURE-Mandatory Parameter DealerCode is NULL";
				bLogger.error("EA Processing: PrimaryDealerTransfer: "+messageId+" : Mandatory Parameter DealerCode is NULL");
				return status;
			}
			
			if(customerCode==null || customerCode.trim()==null || customerCode.replaceAll("\\s","").length()==0)
			{
				status = "FAILURE-Mandatory Parameter customerCode is NULL";
				bLogger.error("EA Processing: PrimaryDealerTransfer: "+messageId+" : Mandatory Parameter customerCode is NULL");
				return status;
			}
			
			if(transferDate==null ||  transferDate.trim()==null || transferDate.replaceAll("\\s","").length()==0)
			{
				status = "FAILURE-Mandatory Parameter transferDate is NULL";
				bLogger.error("EA Processing: PrimaryDealerTransfer: "+messageId+" : Mandatory Parameter transferDate is NULL");
				return status;
			}
			//DF20171011: KO369761 - Security Check added for input text fields.
			CommonUtil util = new CommonUtil();
			String isValidinput=null;
			
			isValidinput = util.inputFieldValidation(SerialNumber);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			if(SerialNumber!=null && SerialNumber.trim().length()>0)
			{
				SerialNumber = SerialNumber.trim();
				
				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
				 Logger iLogger = InfoLoggerClass.logger;
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
				if(session.getTransaction().isActive() && session.isDirty())
				{
				   	iLogger.info("Opening a new session");
				   	session = HibernateUtil.getSessionFactory().openSession();
				}
			    session.beginTransaction();
			       
				try
				{
					AssetEntity asset = null;
					//Validate the VIN
		        	Query query = session.createQuery("from AssetEntity where serial_number ='"+SerialNumber+"'");
		        	Iterator itr = query.list().iterator();
		        	while(itr.hasNext())
		        	{
		        		asset = (AssetEntity)itr.next();
		        	}
		        	
		        	if(asset==null)
		        	{
						//2014-06-18 : Machine Number check for 7 digits - Deepthi
		        		/*if(serialNumber.trim().length() >7)
						{
									serialNumber = serialNumber.substring(serialNumber.length()-7 , serialNumber.length());
						}*/
		        		
		        		//DF20140715 - Rajani Nagaraju - Removing preceeding zeros in Machine Number
		        		SerialNumber=SerialNumber.replaceFirst("^0+(?!$)", "");
						Query qryMacNo =session.createQuery("from AssetEntity a where a.machineNumber='"+SerialNumber+"'");
						Iterator itrMacNo=qryMacNo.list().iterator();
						while(itrMacNo.hasNext())
						{
							asset= (AssetEntity) itrMacNo.next();	
							SerialNumber=asset.getSerial_number().getSerialNumber();
						}
						
						
					}
					if(asset==null)
					{
						AssetControlUnitEntity assetControl=null;
						Query assetControlQ = session.createQuery(" from AssetControlUnitEntity where serialNumber like '%"+SerialNumber+"%'");
						Iterator assetControlItr = assetControlQ.list().iterator();
						while(assetControlItr.hasNext())
						{
							assetControl= (AssetControlUnitEntity)assetControlItr.next();
						}
						
						if(assetControl==null)
						{
							status = "FAILURE-PIN Registration Data not received";
							bLogger.error("EA Processing: AssetInstallation: "+messageId+" : PIN Registration Data not received");
							return status;
						}
						else
						{
							status = "FAILURE-Roll off Data not received";
							bLogger.error("EA Processing: AssetInstallation: "+messageId+" : Roll off Data not received");
							return status;
						}
						
					
					}
				}
				
				
				catch(Exception e)
				{
					status="FAILURE-"+e.getMessage();
					bLogger.error("Exception :"+e);
					return status;
				}

				finally
				{
					try
					{
					if(session.getTransaction().isActive())
					{
						session.getTransaction().commit();
					}
					}
					catch(Exception e)
					{
						//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
						Logger fLogger = FatalLoggerClass.logger;
						fLogger.fatal("Exception in commiting the record:"+e);
						status="FAILURE-"+e.getMessage();
						return status;
					}
					if(session.isOpen())
					{
						session.flush();
						session.close();
					}

				}
			}
			
			TenancyBO tenancyBoObj = new TenancyBO();
			//DF20140325 - Rajani Nagaraju - Get the Corresponding LL Account Code from ECC/CRM Dealer Code from Mapping table
			String llAccountCode = tenancyBoObj.getLLAccountCode(dealerCode);
			
			if(llAccountCode==null)
			{
				throw new CustomFault("Data not found in Mapping table for the Dealer AccountCode:"+dealerCode);
			}
			else
			{
				dealerCode = llAccountCode;
			}
			
			dealerCode = dealerCode.replaceAll("\\s","") ;
			customerCode = customerCode.replaceAll("\\s","") ;
			if(SerialNumber!=null)
				SerialNumber = SerialNumber.replaceAll("\\s","") ;
			
			
			if(SerialNumber==null)
			{
				//update the asset owner from dealer to customer
				status = assetDetails.setPrimaryDealerTransfer(customerCode, dealerCode, transferDate, messageId);
			}
			else
			{
				//DF20141202 - Rajani Nagaraju - Wrong parameters getting passed
				// status = AssetSaleFromD2C.assetSaleFromDealerToCust(customerCode, customerCode, dealerCode, SerialNumber, transferDate, messageId);
				
				
				//Get the account type of customerCode. 
				//1. If it is customer then the sale will not change, instead customer is tagged to different dealer for the machine. But owner remains same
				//2. If it is dealer then actually machine is sold from one dealer to another - D2D Sale
				
				int dealer=0;
				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
				//20220921: Dhiraj K : Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
				if(session.getTransaction().isActive() && session.isDirty())
				{
				   	session = HibernateUtil.getSessionFactory().openSession();
				}
			    session.beginTransaction();
				
				try
				{
					
					Properties prop = new Properties();
					String dealerAccountType=null;
					String custAccountType=null;
						
					prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
					dealerAccountType= prop.getProperty("DealerAccount");
										
					Query sellerAccTypeQuery = session.createQuery(" select a from PartnershipMapping a, AccountEntity b where " +
							" b.status=true and a.accountToId = b.account_id and b.accountCode='"+customerCode+"'");
					Iterator sellerAccTypeItr = sellerAccTypeQuery.list().iterator();
					while(sellerAccTypeItr.hasNext())
					{
						PartnershipMapping  partnership = (PartnershipMapping)sellerAccTypeItr.next();
						String sellerAccountType = partnership.getPartnerId().getReversePartnerRole();
						
						if(sellerAccountType.equalsIgnoreCase(dealerAccountType))
						{
							dealer=1;
						}
								
					}
				}
				
				catch(Exception e)
				{
					bLogger.error("Exception :"+e);
				}

				finally
				{
					if(session.getTransaction().isActive())
					{
						session.getTransaction().commit();
					}
					if(session.isOpen())
					{
						session.flush();
						session.close();
					}

				}
				
				if(dealer==1)
					status = AssetSaleFromD2C.assetSaleFromDealerToCust(customerCode, dealerCode, dealerCode, SerialNumber, transferDate, messageId, null);
				else
					status = AssetSaleFromD2C.assetSaleFromDealerToCust(customerCode, customerCode, dealerCode, SerialNumber, transferDate, messageId,null);
			}
			
			if(SerialNumber!=null)
			{
				//DF20150311 - Rajani Nagaraju - Updating AssetOwnerSnapshot for a VIN on real time
				new CurrentAssetOwnerDetailsImpl().setVinOwnerDetails(SerialNumber);
			}
		}
		
		catch(CustomFault e)
		{
			status = "FAILURE-"+e.getFaultInfo();
			bLogger.error("EA Processing: PrimaryDealerTransfer: "+messageId+" : "+e.getFaultInfo());
		}
		
		return status;
		
	}
}
