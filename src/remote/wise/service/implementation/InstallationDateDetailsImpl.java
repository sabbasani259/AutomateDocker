package remote.wise.service.implementation;

import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountTenancyMapping;
import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetOwnerSnapshotEntity;
import remote.wise.businessentity.PartnershipMapping;
import remote.wise.businessentity.TenancyEntity;
import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.businessobject.ServiceDetailsBO;
import remote.wise.businessobject.TenancyBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

/** Implementation class to set Asset Service schedule details
 * @author Rajani Nagaraju
 *
 */
public class InstallationDateDetailsImpl 
{

	//public static WiseLogger businessError = WiseLogger.getLogger("InstallationDateDetailsImpl:","businessError");
	/** This method sets the service schedule details for a Machine
	 * @param serialNumber - VIN
	 * @param installationDate - Date of installation of machine at Customer end
	 * @param dealerCode - Delaer of the Machine
	 * @param customerCode - Customer who owns the machine
	 * @return Returns the status String
	 */
	//public String setAssetserviceSchedule(String serialNumber, String installationDate, String dealerCode, String customerCode, String messageId)
	public String setAssetserviceSchedule(String serialNumber, String installationDate, String dealerCode, String customerCode, String messageId, boolean retrofitFlag)
	{
		String status = "SUCCESS-Record Processed";
		ServiceDetailsBO serviceBO=new ServiceDetailsBO();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		try
		{
			//Check for Mandatory Parameters
			if(serialNumber==null || serialNumber.trim()==null || serialNumber.replaceAll("\\s","").length()==0)
			{
				//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
				status = "FAILURE-Mandatory Parameter SerialNumber is NULL";
				bLogger.error("EA Processing: AssetInstallation: "+messageId+" : Mandatory Parameter SerialNumber is NULL");
				return status;
			}

			if(installationDate==null || installationDate.trim()==null || installationDate.replaceAll("\\s","").length()==0)
			{
				//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
				status = "FAILURE-Mandatory Parameter installationDate is NULL";
				bLogger.error("EA Processing: AssetInstallation: "+messageId+" : Mandatory Parameter installationDate is NULL");
				return status;
			}

			if(dealerCode==null || dealerCode.trim()==null ||  dealerCode.replaceAll("\\s","").length()==0)
			{
				//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
				status = "FAILURE-Mandatory Parameter dealerCode is NULL";
				bLogger.error("EA Processing: AssetInstallation: "+messageId+" : Mandatory Parameter dealerCode is NULL");
				return status;
			}
			//DF20171011: KO369761 - Security Check added for input text fields.
			CommonUtil util = new CommonUtil();
			String isValidinput=null;

			isValidinput = util.inputFieldValidation(serialNumber);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}

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
				Query query = session.createQuery("from AssetEntity where serial_number ='"+serialNumber+"'");
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
					serialNumber=serialNumber.replaceFirst("^0+(?!$)", "");
					Query qryMacNo =session.createQuery("from AssetEntity a where a.machineNumber='"+serialNumber+"'");
					Iterator itrMacNo=qryMacNo.list().iterator();
					while(itrMacNo.hasNext())
					{
						asset= (AssetEntity) itrMacNo.next();	
						serialNumber=asset.getSerial_number().getSerialNumber();
					}


				}
				if(asset==null)
				{
					AssetControlUnitEntity assetControl=null;
					Query assetControlQ = session.createQuery(" from AssetControlUnitEntity where serialNumber like '%"+serialNumber+"%'");
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
				else
				{
					//Keerthi : 13/10/2014 : checking whether PIN is with customer.
					//					if its with customer, then only continue processing asset installation.
					/*int primary_owner_id = asset.getPrimary_owner_id();
					Query accountQuery =  session.createQuery(" from AccountTenancyMapping where account_id = "+primary_owner_id);
					Iterator accountIterator = accountQuery.iterate();
					AccountTenancyMapping accountTenancy = null;
					TenancyEntity tenancyEntity = null;
					while(accountIterator.hasNext()){
						accountTenancy = (AccountTenancyMapping)accountIterator.next();
						if(accountTenancy!=null){
							tenancyEntity = accountTenancy.getTenancy_id();
						}						
					}
					int tenacy_type_id = 0;
					if(tenancyEntity!=null){
						tenacy_type_id = tenancyEntity.getTenancy_type_id().getTenancy_type_id();
					}
					if(tenacy_type_id!=4){
						status = "FAILURE-Machine "+serialNumber+" is not with Customer";
						businessError.error("EA Processing: AssetInstallation: "+messageId+" : Machine "+serialNumber+" is not with Customer");
						return status;
					}*/

					//DF20171106 - KO339761 - Checking if a machine is with customer or not through AOS (checking through partnership was eliminated because it's not working if machine is with OEM.)
					AssetOwnerSnapshotEntity aosEntity = null;
					Query accountTypeQ = session.createQuery(" from AssetOwnerSnapshotEntity where serialNumber = '"+serialNumber+"' and accountType= 'Customer'");
					Iterator accountTypeItr = accountTypeQ.list().iterator();

					while(accountTypeItr.hasNext())
					{
						aosEntity = (AssetOwnerSnapshotEntity)accountTypeItr.next();
					}

					if(aosEntity==null)
					{
						status = "FAILURE-Machine "+serialNumber+" is not with Customer";
						bLogger.error("EA Processing: AssetInstallation: "+messageId+" : Machine "+serialNumber+" is not with Customer");

						iLogger.info("AssetInstallationLog: "+serialNumber+": Machine "+serialNumber+" is not with Customer");
						return status;
					}

					/*//DF20141210 - Rajani Nagaraju - Check on Customer Type should be based on account and not on tenancy
					int primary_owner_id = asset.getPrimary_owner_id();
					PartnershipMapping partnership = null;
					Query accountTypeQ = session.createQuery(" from PartnershipMapping where accountToId='"+primary_owner_id+"'");
					Iterator accountTypeItr = accountTypeQ.list().iterator();
					while(accountTypeItr.hasNext())
					{
						partnership = (PartnershipMapping)accountTypeItr.next();
					}
					//Df20160909 @Roopa handling partnership null check
					if(partnership==null){
						iLogger.info("AssetInstallationLog: "+serialNumber+": Customer mapping not exists in PartnershipMapping");
						status = "FAILURE-Machine "+serialNumber+" Customer mapping not exists in PartnershipMapping";
					}


					if(partnership!=null && ! (partnership.getPartnerId().getPartnerId()==2 || partnership.getPartnerId().getPartnerId()==3) )
					{
						status = "FAILURE-Machine "+serialNumber+" is not with Customer";
						bLogger.error("EA Processing: AssetInstallation: "+messageId+" : Machine "+serialNumber+" is not with Customer");

						iLogger.info("AssetInstallationLog: "+serialNumber+": Machine "+serialNumber+" is not with Customer");
						return status;
					}*/
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
					if(session !=null && session.getTransaction().isActive())
					{
						session.getTransaction().commit();
					}
				}
				catch(Exception e)
				{
					status="FAILURE-"+e.getMessage();
					//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
					fLogger.fatal("Exception in commiting the record:"+e);
					return status;
				}
				if(session !=null && session.isOpen())
				{
					session.flush();
					session.close();
				}

			}

			TenancyBO tenancyBoObj = new TenancyBO();
			//DF20140325 - Rajani Nagaraju - Get the Corresponding LL Account Code from ECC/CRM Dealer Code from Mapping table
			String llAccountCode = tenancyBoObj.getLLAccountCode(dealerCode);

			iLogger.info("AssetInstallationLog: "+serialNumber+":llAccountCode for the dealer:"+dealerCode+":"+llAccountCode);

			if(llAccountCode==null)
			{
				throw new CustomFault("Data not found in Mapping table for the Dealer AccountCode:"+dealerCode);
			}
			else
			{
				dealerCode = llAccountCode;
			}

			serialNumber = serialNumber.replaceAll("\\s","") ;
			dealerCode = dealerCode.replaceAll("\\s","") ;


			//added by smitha to get the serialNumber for a given machineNumber-----20th jan 2014-----DefectID:20140120.
			/*AssetDetailsBO assetDetailsbo = new AssetDetailsBO();
			serialNumber=assetDetailsbo.getSerialNumberMachineNumber(serialNumber);*/
			//end ------DefectID:20140120
			//2014-06-18 : Checking for 7 digit Machine Number - Deepthi

			iLogger.info("AssetInstallationLog: "+serialNumber+":Before calling setAssetServiceScheduleBO");

			//status =serviceBO.setAssetServiceScheduleBO(serialNumber,installationDate, dealerCode, customerCode, messageId);//CR352.o
			status =serviceBO.setAssetServiceScheduleBO(serialNumber,installationDate, dealerCode, customerCode, messageId, retrofitFlag);//CR52.n

			iLogger.info("AssetInstallationLog: "+serialNumber+":After calling setAssetServiceScheduleBO:"+status);
		}

		catch(CustomFault e)
		{
			status = "FAILURE-"+e.getFaultInfo();
			bLogger.error("EA Processing: AssetInstallation: "+messageId+" : "+e.getFaultInfo());
		}

		return status;	
	}

	//implementation for updating the serviceSchedules for all the assets in asset service schedule table
	public String updateAssetServiceSchedule(String carePlus) {
		// TODO Auto-generated method stub
		int careplus = 0;
		String status = "FAILURE";
		Logger bLogger = BusinessErrorLoggerClass.logger;
		try{
			if(carePlus == null)
				throw new CustomFault("Care Plus is not specified");
		}catch(CustomFault e)
		{
			status = "FAILURE-"+e.getFaultInfo();
			bLogger.error(e.getFaultInfo());
		}
		if(carePlus!=null)
			careplus = new Integer(carePlus);
		if(careplus==1)
		{
			status = new ServiceDetailsBO().updateAssetServiceSchedule();
		}
		return status;
	}

	public String closeOutdatedServiceAlerts() {
		// TODO Auto-generated method stub
		String status = new ServiceDetailsBO().closeOutdatedServiceAlerts();
		return status;
	}

}
