package remote.wise.service.implementation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountTenancyMapping;
import remote.wise.businessentity.TenancyEntity;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.service.datacontract.TenancyAssetsReqContract;
import remote.wise.service.datacontract.TenancyAssetsRespContract;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;

/** Implementation class to get List of VINs under the specified tenancy
 * @author Rajani Nagaraju
 *
 */
public class TenancyAssetsImpl 
{
	//Defect Id 1337 - Logger changes
		//public static WiseLogger businessError = WiseLogger.getLogger("TenancyAssetsImpl:","businessError");
		//public static WiseLogger fatalError = WiseLogger.getLogger("AssetDetailsBO:","fatalError");
	List<String> serialNumberList;

	/**
	 * @return VIN List as String
	 */
	public List<String> getSerialNumberList() {
		return serialNumberList;
	}

	/**
	 * @param serialNumberList List of VINs as String input
	 */
	public void setSerialNumberList(List<String> serialNumberList) {
		this.serialNumberList = serialNumberList;
	}
	
	
	public TenancyAssetsRespContract getTenancyAssets(TenancyAssetsReqContract reqObj) throws CustomFault
	{
		TenancyAssetsRespContract response = new TenancyAssetsRespContract();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		try
		{
			if(reqObj.getLoginId()==null)
			{
				throw new CustomFault("Login Id should be specified");
			}
			
			if(reqObj.getTenancyIdList()== null || reqObj.getTenancyIdList().isEmpty())
			{
				throw new CustomFault("Tenancy Id should be specified");
			}
			//DefectId:20150410 @Suprava machine Group Vin/Machine Number search
			if(reqObj.getSerialNumber()!=null){
				if(!(reqObj.getSerialNumber().trim().length()>=7 && reqObj.getSerialNumber().trim().length()<=17)){
					bLogger.error("Custom Fault: Invalid Serial Number "+reqObj.getSerialNumber()+". Length should be between 7 and 17");
					throw new CustomFault("Invalid Serial Number "+reqObj.getSerialNumber()+". Length should be between 7 and 17");
				}
			}
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		List<String> serialNumberList = new LinkedList<String>();
		List<Integer> tenancyIdLst = new LinkedList<Integer>();
		tenancyIdLst.addAll(reqObj.getTenancyIdList());
		//DefectId:20150902 @Suprava Performance improvement - Service taking too long time to return due to In condition
		//TenancyBO tenancyBo = new TenancyBO();
		
		//get the list of Child tenancies for the given tenancyId
		/*if(reqObj.isChildTenancyAssetsRequired()==true)
		{
			tenancyIdLst = tenancyBo.getChildTenancyId(reqObj.getTenancyIdList());
		}*/
		
		//tenancyIdLst.addAll(reqObj.getTenancyIdList());
		//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
		//Get the List of all tenancyIds - Including Pseudo Tenancies
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
        try
        {
        	//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
        	List<Integer> newTenancyIdList = new LinkedList<Integer>();
           	ListToStringConversion conversion = new ListToStringConversion();
           	String tenancyIdListString = conversion.getIntegerListString(reqObj.getTenancyIdList()).toString();
           	Query q = session.createQuery(" from TenancyEntity where tenancyCode in ( select a.tenancyCode from TenancyEntity a where a.tenancy_id in ("+tenancyIdListString+"))");
           	Iterator it = q.list().iterator();
           	while(it.hasNext())
           	{
           		TenancyEntity tenancy = (TenancyEntity)it.next();
           		newTenancyIdList.add(tenancy.getTenancy_id());
           	}
           	tenancyIdLst.addAll(newTenancyIdList);
           	
			//Df20150403 - Rajani Nagaraju - Performance improvement - Service taking too long time to return
           	//get the list of accounts corresponding to the tenancyId List
           	/*List<AccountEntity> accountEntity = new LinkedList<AccountEntity>();
           	accountEntity = tenancyBo.getAccountEntity(tenancyIdLst);*/
           	
           	List<Integer> accIdList = new LinkedList<Integer>();
           	String tenancyIdListAsString = conversion.getIntegerListString(tenancyIdLst).toString();
           	Query accQuery = session.createQuery("from AccountTenancyMapping where tenancy_id in ("+tenancyIdListAsString+")");
        	Iterator accItr = accQuery.list().iterator();
		   	while(accItr.hasNext())
        	{
        		AccountTenancyMapping accountTenancy = (AccountTenancyMapping) accItr.next();
        		accIdList.add(accountTenancy.getAccount_id().getAccount_id());
			
        	}
		
		  //Df20150403 - Rajani Nagaraju - Performance improvement - Service taking too long time to return
           	//get the list of serialNumbers corresponding to the accountEntity list
           /*	AssetDetailsBO assetDetailsBo = new AssetDetailsBO();
           	for(int i=0; i<accountEntity.size(); i++)
           	{
           		List<AssetEntity> assetEntityList = assetDetailsBo.getAccountAssets(accountEntity.get(i).getAccount_id());
           		for(int j=0; j< assetEntityList.size(); j++)
           		{
           			serialNumberList.add(assetEntityList.get(j).getSerial_number().getSerialNumber());
           		}
           	}*/
		   	String accIdListAsString = conversion.getIntegerListString(accIdList).toString();
		   	String finalQuery =null;
			String selectQuery =null;
			String fromQuery =null;
			String whereQuery=null;
		   	String serialNum =reqObj.getSerialNumber();
		   	//DefectId:20150902 @Suprava Performance improvement - Service taking too long time to return due to In condition
		   	/*if(reqObj.getSerialNumber()!=null){
		   		if(reqObj.getSerialNumber().trim().length()==17)
		   		{
		    	finalQuery = "select a.serial_number from AssetEntity a where a.serial_number='"+serialNum+"' and a.primary_owner_id in ("+accIdListAsString+")";	
		   		}
		   		else if(reqObj.getSerialNumber().trim().length()==7)
		   		{
		   		finalQuery = "select a.serial_number from AssetEntity a where a.machineNumber='"+serialNum+"' and a.primary_owner_id in ("+accIdListAsString+")";
		   		}
		   	}
		   	else
			{
		   		finalQuery ="select serial_number from AssetEntity where primary_owner_id in ("+accIdListAsString+")";
			}*/
		   	if(reqObj.getSerialNumber()!=null){
		   		if(reqObj.getSerialNumber().trim().length()==17)
		   		{
		    	finalQuery = "select distinct a.serialNumber from AssetOwnerSnapshotEntity a where a.serialNumber='"+serialNum+"' and a.accountId in ("+accIdListAsString+")";	
		   		}
		   		else if(reqObj.getSerialNumber().trim().length()==7)
		   		{
		   		finalQuery = "select distinct a.serialNumber from AssetOwnerSnapshotEntity a,AssetEntity b where b.machineNumber='"+serialNum+"' and b.serial_number=a.serialNumber and a.accountId in ("+accIdListAsString+")";
		   		}
		   	}
		   	else
			{
		   		finalQuery ="select distinct a.serialNumber from AssetOwnerSnapshotEntity a where a.accountId in ("+accIdListAsString+")";
			}
		   /*	 
		   	{
		   	  Query  vinListQ = session.createQuery(" select serial_number from AssetEntity where primary_owner_id in ("+accIdListAsString+")");	
		   	}*/
		   	Iterator itr = session.createQuery(finalQuery).list().iterator();
		   	while(itr.hasNext())
		   	{
		   		String asset = (String) itr.next();
		   		if(asset!=null){
		   		serialNumberList.add(asset);
		   		}
		   	}
           	response.setSerialNumberList(serialNumberList);
           	
           	
          //DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
           	if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        session.getTransaction().begin();
            }
        }
        
        catch(Exception e)
		{
			e.printStackTrace();
        	fLogger.fatal("Exception :"+e);
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
        
        return response;
		
	}
	
}
