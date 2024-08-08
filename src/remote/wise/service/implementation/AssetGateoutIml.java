/*
 * JCB6341 : 20230306 : Dhiraj K : Sale date is not getting updated as part of Direct Gateout sale 
 */
package remote.wise.service.implementation;

import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.SmsTemplateEntity;
import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.businessobject.TenancyBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.AssetExtendedRespContract;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;


/** Implementation class to handle Asset Gate out details
 * @author Rajani Nagaraju
 *
 */
public class AssetGateoutIml 
{
	/*public static WiseLogger businessError = WiseLogger.getLogger("AssetGateoutIml:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("AssetGateoutIml:","fatalError");*/
	private AssetEntity SerialNumber;
	private AccountEntity AccountId;

	public AssetEntity getSerialNumber() {
		return SerialNumber;
	}
	public void setSerialNumber(AssetEntity serialNumber) {
		SerialNumber = serialNumber;
	}
	public AccountEntity getAccountId() {
		return AccountId;
	}
	public void setAccountId(AccountEntity accountId) {
		AccountId = accountId;
	}


	/** Impl method to record the sale to dealer
	 * @param dealerCode DealerCode
	 * @param customerCode CustomerCode if it is a direct sale to Customer
	 * @param engineNumber engineNumber of the Asset
	 * @param serialNumber VIN - Optional parameter
	 * @return Return the process status
	 */
	//public String setAssetGateoutDetails(String dealerCode, String customerCode, String engineNumber, String serialNumber, String messageId) //JCB6341.o
	public String setAssetGateoutDetails(String dealerCode, String customerCode, String engineNumber, String serialNumber, String messageId, String gateoutDateString) //JCB6341.n
	{
		String status ="SUCCESS-Record Processed";
		AssetDetailsBO assetBO=new AssetDetailsBO();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger fLogger = BusinessErrorLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		/*Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
*/
		if(dealerCode==null  ||	dealerCode.trim()==null || dealerCode.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter Dealer Code is NULL";
			bLogger.error("EA Processing: AssetGateOut: "+messageId+" : Mandatory Parameter Dealer Code is NULL");
			return status;
		}
		
		//DefectID:20140120 @suprava
		if((engineNumber==null || engineNumber.trim()==null || engineNumber.replaceAll("\\s","").length()==0 ) && (serialNumber==null || serialNumber.trim()==null ||  serialNumber.replaceAll("\\s","").length()==0 ))
		{
			status = "FAILURE-Engine number and Serial Number both are NULL";
			bLogger.error("EA Processing: AssetGateOut: "+messageId+" : Engine number and Serial Number both are NULL");
			return status;
		}
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		//DF20170905:KO369761 - Checking If the VIN was already with the customer or not. If a sale is already done, then the gateout shoudnt be processed.
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}

		session.beginTransaction();

		try
		{
			Query aosQuery = session.createQuery("from AssetOwnerSnapshotEntity where serialNumber='"+serialNumber+"' and accountType='Customer'");
			Iterator iterator = aosQuery.list().iterator();			
			if(iterator.hasNext()){
				status = "SUCCESS-Asset is already with Customer.";
				bLogger.error("EA Processing: AssetGateOut: "+messageId+" : this asset is already with Customer.");
				return status;
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			status = "FAILURE-"+e.getMessage();
			fLogger.fatal("EA Processing: AssetGateOut: "+messageId+ " Fatal Exception :"+e);
		}
		finally{
			if(session.isOpen())
            {
          	  session.flush();
                session.close();
            }
		}
		
		//DF20141209 - Rajani Nagaraju - Removing getting asset based on Engine Number
		//DefectID:20140120 @suprava
		/*if (engineNumber==null || engineNumber.replaceAll("\\s","").length()==0 )
		{
			try
			{
				Query q1=session.createQuery("select a.nick_name from AssetEntity a where a.serial_number='"+serialNumber+"'");
				Iterator itr1=q1.list().iterator();
				while(itr1.hasNext())
				{
					engineNumber = (String) itr1.next();				
				}
				//2014-06-18 : Checking for 7 digit Machine Number - Deepthi
				if(engineNumber == null)
				{
					if(serialNumber.trim().length() >7){
						serialNumber = serialNumber.substring(serialNumber.length()-7 , serialNumber.length());
					}
					//20140715 - Rajani Nagaraju - Removing preceeding zeros from Machine Number
					serialNumber=serialNumber.replaceFirst("^0+(?!$)", "");
					
					Query qryMacNo =session.createQuery("select a.nick_name from AssetEntity a where a.machineNumber='"+serialNumber+"'");
					Iterator itrMacNo=qryMacNo.list().iterator();
					while(itrMacNo.hasNext())
					{
						engineNumber = (String) itrMacNo.next();				
					}
				}
				//2014-06-18 : Checking for 7 digit Machine Number - Deepthi
				
				//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
				if(engineNumber == null)
				{
					status = "FAILURE-Invalid Serial Number/Machine Number";
					businessError.error("EA Processing: AssetGateOut: "+messageId+" : Invalid Serial Number/Machine Number");
					return status;
					
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
						businessError.error("EA Processing: AssetGateOut: "+messageId+" : PIN Registration Data not received");
						return status;
					}
					else
					{
						status = "FAILURE-Roll off Data not received";
						businessError.error("EA Processing: AssetGateOut: "+messageId+" : Roll off Data not received");
						return status;
					}
				}
			}
			 
			catch(Exception e)
			{
				status = "FAILURE-"+e.getMessage();
				fatalError.fatal("EA Processing: AssetGateOut: "+messageId+ " Fatal Exception :"+e);
			}
			
			finally
		    {
				   if(session.getTransaction().isActive())
	              {
	                    session.getTransaction().commit();
	              }
	              
	        	//DF20140715 - Rajani Nagaraju - To Avoid Session Close Exception
	              if(session.isOpen())
	              {
	            	  if(session.getTransaction().isActive())
	                  {
	                        session.getTransaction().commit();
	                  }
	            	  
	            	 
	              }
	              
	              if(session.isOpen())
	              {
	            	  session.flush();
	                  session.close();
	              }
		              
		    }
		}*/
		
		//Update the ownership of the machine from JCB to dealer/ Customer (In case of direct sale)
		//Handling Null chk for CustCode - deepthi : DF 20140217
		if(customerCode!=null)
		{
			String chkcustomerCode = customerCode.replaceAll("\\s","") ;
			if (!(chkcustomerCode.length()>0))
				customerCode=null;
		}
		
		try
		{
			TenancyBO tenancyBoObj = new TenancyBO();
			//DF20140325 - Rajani Nagaraju - Get the Corresponding LL Account Code from ECC/CRM Dealer Code from Mapping table
			String llAccountCode = tenancyBoObj.getLLAccountCode(dealerCode);
			
			iLogger.info("AssetGateOutLog: "+serialNumber+": llAccountCode from TenancyBO:"+llAccountCode);
			
			if(llAccountCode==null)
			{
				throw new CustomFault("Data not found in Mapping table for the AccountCode:"+dealerCode);
			}
			else
			{
				dealerCode = llAccountCode;
			}
			
			iLogger.info("AssetGateOutLog: "+serialNumber+": Before calling AssetDetailsBO:setAssetGateoutDetails");
			//status=assetBO.setAssetGateoutDetails(dealerCode,customerCode, engineNumber, serialNumber, messageId);//JCB6341.o
			status=assetBO.setAssetGateoutDetails(dealerCode,customerCode, engineNumber, serialNumber, messageId, gateoutDateString);//JCB6341.n
			
			iLogger.info("AssetGateOutLog: "+serialNumber+": After calling AssetDetailsBO:setAssetGateoutDetails:"+status);
			
			iLogger.info("AssetGateOutLog: "+serialNumber+": Before calling AssetDetailsBO:setVinOwnerDetails");
			//DF20150311 - Rajani Nagaraju - Updating AssetOwnerSnapshot for a VIN on real time
			new CurrentAssetOwnerDetailsImpl().setVinOwnerDetails(serialNumber);
			
			iLogger.info("AssetGateOutLog: "+serialNumber+": After calling AssetDetailsBO:setVinOwnerDetails");
		
			
		}
		
		catch(CustomFault e)
		{
			status = "FAILURE-"+e.getFaultInfo();
			bLogger.error("EA Processing: AssetGateOut: "+messageId+" : "+e.getFaultInfo());
		}
		
		return status;
	}
}
