package remote.wise.businessobject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AccountTenancyMapping;
import remote.wise.businessentity.AssetAccountMapping;
import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetExtendedDetailsEntity;
import remote.wise.businessentity.ClientEntity;
import remote.wise.businessentity.MonitoringParameters;
import remote.wise.dal.DynamicAMH_DAL;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.implementation.PricolRollOffImpl;
import remote.wise.service.implementation.PricolTransactionDetailImpl;
import remote.wise.service.implementation.PricolTransactionSummaryImpl;
import remote.wise.util.DateUtil;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

/**
 * @author Rajani Nagaraju
 *
 */
public class PricolMachineDetailsBO 
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger fatalError = WiseLogger.getLogger("PricolMachineDetailsBO:","fatalError");*/
	
	
	public List<PricolTransactionSummaryImpl> getVinList(String searchCriteria, String searchText, int tenancyId, boolean isPricolTenancy,
														String registrationTimeStamp)
	{
		
    	Logger fLogger = FatalLoggerClass.logger;
    	
		List<PricolTransactionSummaryImpl> responseList = new LinkedList<PricolTransactionSummaryImpl>();
		
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
		try
		{
			//get Client Details
			Properties prop = new Properties();
			String clientName=null;
				
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");
      
			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
			//END of get Client Details	 
          if(! (session.isOpen() ))
                  {
                              session = HibernateUtil.getSessionFactory().getCurrentSession();
                              session.getTransaction().begin();
                  }

			/* String queryString = " select a.serialNumber, a.simNo, a.imeiNo, a.registrationDate, b from AssetEntity b " +
								" right outer join b.serial_number a where a.serialNumber!=null ";
	       */
			 /*String queryString = " SELECT a.serialNumber, a.simNo, a.imeiNo, a.registrationDate" +
			 		" FROM AssetAccountMapping b, AssetControlUnitEntity a, AccountTenancyMapping c,TenancyEntity d" +
			 		" WHERE d.tenancy_id = " +tenancyId+" AND d.tenancy_id = c.tenancy_id AND c.account_id = b.accountId" +
			 	    " AND b.serialNumber = a.serialNumber";*/
			//DefectID: Asset Owner Changes - Rajani Nagaraju - 20130719
			String queryString = " SELECT a.serialNumber, a.simNo, a.imeiNo, a.registrationDate" +
	 		" FROM AssetEntity b, AssetControlUnitEntity a, AccountTenancyMapping c,TenancyEntity d" +
	 		" WHERE d.tenancy_id = " +tenancyId+" AND d.tenancy_id = c.tenancy_id AND b.client_id="+clientEntity.getClient_id()+" AND b.client_id=d.client_id AND c.account_id = b.primary_owner_id" +
	 	    " AND b.serial_number = a.serialNumber" ;
			 		
			 String queryString2 = " SELECT a.serialNumber, a.simNo, a.imeiNo, a.registrationDate" +
		 		" FROM AssetControlUnitEntity a "+
		 		" WHERE a.serialNumber NOT IN (SELECT serialNumber FROM AssetAccountMapping)";
			 
			if(searchCriteria!=null)
			{
				if(searchCriteria.equalsIgnoreCase("VIN"))
				{
					queryString = queryString+" and a.serialNumber like '%"+searchText+"%'";
					queryString2 = queryString2+" and a.serialNumber like '%"+searchText+"%'";
				}
				
				else if (searchCriteria.equalsIgnoreCase("IMEI"))
				{
					queryString = queryString+" and a.imeiNo like '%"+searchText+"%'";
					queryString2 = queryString2+" and a.imeiNo like '%"+searchText+"%'";
				}
				else if (searchCriteria.equalsIgnoreCase("SIM"))
				{
					queryString = queryString+" and a.simNo like '%"+searchText+"%'";
					queryString2 = queryString2+" and a.simNo like '%"+searchText+"%'";
				}
			}
			
			if(!isPricolTenancy)
			{
				queryString = queryString + " and a.serialNumber not like 'PRICOL%'";
				queryString2 = queryString2 + " and a.serialNumber not like 'PRICOL%'";
			}			
			else
			{
//				queryString = queryString + " and a.serialNumber like 'PRICOL%'";
				queryString2 = queryString2 + " and a.serialNumber like 'PRICOL%'";
			}
			
			if(registrationTimeStamp!=null)
			{
				queryString = queryString + " and a.registrationDate like '"+registrationTimeStamp+"%'";
				queryString2 = queryString2 + " and a.registrationDate like '"+registrationTimeStamp+"%'";
			}
			
			queryString = queryString +" order by a.registrationDate desc ";
			queryString2 = queryString2 +" order by a.registrationDate desc ";
			
			Query query = session.createQuery(queryString);
			Iterator itr = query.list().iterator();
			Object[] result =null;
			String serialNo = null,querySerilaNo=null;
			Query query2 =null;
			PricolTransactionSummaryImpl implObj =null;
			while(itr.hasNext())
			{
				result = (Object[]) itr.next();
				
				//set the response Object
				implObj = new PricolTransactionSummaryImpl();
				serialNo = (String)result[0];
				implObj.setSerialNumber(result[0].toString());
				if(result[2]!=null)
					implObj.setImeiNumber(result[2].toString());
				if(result[1]!=null)
					implObj.setSimNumber(result[1].toString());
				
				if(result[3]!=null)
				{
					Timestamp registrationDate = (Timestamp)result[3];
					implObj.setRegistrationDate(registrationDate.toString());
				}
				querySerilaNo = "SELECT z.serial_number FROM AssetEntity z WHERE z.serial_number='"+serialNo+"'and z.active_status=true and z.client_id="+clientEntity.getClient_id()+"";
				
				query2 = session.createQuery(querySerilaNo);
				if(query2.list().size()>0){
					implObj.setRollOffStatus(true);
				}
				else{
					implObj.setRollOffStatus(false);
				}
				
				/*if(result[4]!=null)
					implObj.setRollOffStatus(true);
				else
					implObj.setRollOffStatus(false);
				*/
				responseList.add(implObj);
			}
			
			//also get vin no.s which exists in account control unit but does not exist in asset_owners
			query = session.createQuery(queryString2);
			itr = query.list().iterator();
			
			while(itr.hasNext())
			{
				result = (Object[]) itr.next();
				
				//set the response Object
				implObj = new PricolTransactionSummaryImpl();
				serialNo = (String)result[0];
				implObj.setSerialNumber(result[0].toString());
				if(result[2]!=null)
					implObj.setImeiNumber(result[2].toString());
				if(result[1]!=null)
					implObj.setSimNumber(result[1].toString());
				
				if(result[3]!=null)
				{
					Timestamp registrationDate = (Timestamp)result[3];
					implObj.setRegistrationDate(registrationDate.toString());
				}
				
				implObj.setRollOffStatus(false);
//				Commented by Keerthi 17/09/13: NO need to check for roll of status for VINs in ACU table
//				querySerilaNo = "SELECT z.serial_number FROM AssetEntity z WHERE z.serial_number='"+serialNo+"' and z.active_status=true and z.client_id="+clientEntity.getClient_id()+"";
//				
//				query2 = session.createQuery(querySerilaNo);
//				if(query2.list().size()>0){
//					implObj.setRollOffStatus(true);
//				}
//				else{
//					implObj.setRollOffStatus(false);
//				}
			    responseList.add(implObj);
			}
		}
		
		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e.getMessage());
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
		
		
		return responseList;
	}
	
	//DF20160719 @ Roopa Fetching dta from new dynamic AMH and AMD tables
	
	public List<PricolTransactionDetailImpl> getTransactionDetails(String serialNumber, String transactionTimestamp)
	{
		List<PricolTransactionDetailImpl> responseList = new LinkedList<PricolTransactionDetailImpl>();
		
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		
		String transactionTS=transactionTimestamp;
		
		/*String amdTable=null;
		String amhTable=null;*/
		String tAssetMonTable=null;
		
    	Logger fLogger = FatalLoggerClass.logger;
    	
        Session session = HibernateUtil.getSessionFactory().openSession();
      //  session.beginTransaction();
        
       // if transactionTimestamp comes in yyyy-mm-dd format convert in into  yyyy-MM-dd hh:mm:ss
        
        if(transactionTimestamp.length()<19){
        	transactionTS=transactionTimestamp+ " 00:00:00";	
        	
        }
        
        if(transactionTS!=null){

			/*<!=========START persisting in In Native Database==============>*/

			SimpleDateFormat dateTimeFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


			Date txnDate = null;
			try {
				txnDate = dateTimeFormat.parse(transactionTS);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			 tAssetMonTable = new DateUtil().getDynamicTable("PricolMachineDetails::getTransactionDetails", txnDate);

			/*if(dynamicTables!=null && dynamicTables.size()>0){

				amdTable = dynamicTables.get("AMD");
				amhTable  = dynamicTables.get("AMH");

				//iLogger.info(txnKey+":AMS:DAL-AMD-getAMDData"+"AMD Table::"+amdTable);
			}*/
        }
        
      //DF20160718 @Roopa Dynamic AMD changes
		

		
		AssetEntity asset=null;
		
		int seg_ID=0;
		
		try{
			
			Query assetQ = session.createQuery(" from AssetEntity where serial_number='"+serialNumber+"'");
			Iterator assetItr = assetQ.list().iterator();
			if(assetItr.hasNext())
			{
				asset = (AssetEntity)assetItr.next();
			}
			
		}
		catch(Exception e)
		{
			fLogger.fatal("PricolTransactionDetailService::getTransactionDetails::Exception::"+e.getMessage());
		}
		
              if(session!=null && session.isOpen())
              {
                   
                    session.close();
              }
       
		if(asset!=null){
			
			seg_ID=asset.getSegmentId();
		}
        
       /* String query="select a.Transaction_Number, a.Serial_Number, a.Transaction_Timestamp, GROUP_CONCAT(b.Parameter_ID) as Parameter_ID, GROUP_CONCAT(b.Parameter_Value) as Parameter_Value " +
                     " from "+amhTable+" a, "+amdTable+" b " +
        		     " where a.Transaction_Number=b.Transaction_Number and a.Transaction_Timestamp like '"+transactionTimestamp+"%' and a.Serial_Number='"+serialNumber+"' " +
                     " and a.Segment_ID_TxnDate="+seg_ID+" and b.Segment_ID_TxnDate="+seg_ID+" " +
        		     " group by a.Transaction_Number " +
                     " order by a.Transaction_Number desc ";*/
		
		//DF20161222 @Roopa changing AMH and AMD into single TAssetMOnData table and taking parameters from json column TxnData
		
		 String query="select t.Serial_Number, t.Transaction_Timestamp, t.TxnData " +
                 " from "+tAssetMonTable+" t " +
    		     " where t.Transaction_Timestamp like '"+transactionTimestamp+"%' and t.Serial_Number='"+serialNumber+"' " +
                 " and t.Segment_ID_TxnDate="+seg_ID+" ";
        
       // System.out.println("PricolTransactionDetailService::getTransactionDetails::Query::"+query);
        
        
        
        try
        {
        	
        	
        	
        	DynamicAMH_DAL amhamdObj=new DynamicAMH_DAL();
        	
        	responseList=amhamdObj.getAMHAMDListForPricolTransaction(query);
        	
        	if(session==null || ! session.isOpen()){
        		
        		session = HibernateUtil.getSessionFactory().openSession();
        	}
        	
        	MonitoringParameters monitorParam=null;
        	
        	HashMap<String,String> MastermonitorParamNameMap = new HashMap<String, String>();
			
			Query monQ = session.createQuery(" from MonitoringParameters");
			Iterator monItr = monQ.list().iterator();
			while(monItr.hasNext())
			{
				monitorParam = (MonitoringParameters)monItr.next();
				MastermonitorParamNameMap.put(String.valueOf(monitorParam.getParameterKey()), monitorParam.getParameterName());
			}
			//System.out.println("responseList size Before::"+responseList.size());
			
        	for(int i=0;i<responseList.size();i++){
        		
        		
        		HashMap<String,String> monitorParamIdMap = new HashMap<String, String>();
            	HashMap<String,String> monitorParamNameMap = new HashMap<String, String>();
        		
        		monitorParamIdMap=	responseList.get(i).getTransactionData();
        		for(Entry<String, String> entry:monitorParamIdMap.entrySet()){
        			monitorParamNameMap.put(MastermonitorParamNameMap.get(entry.getKey()), entry.getValue());
        		}
        		
        	
				
				responseList.get(i).setTransactionData(monitorParamNameMap);
        	}
        	
        //	System.out.println("responseList size after::"+responseList.size());
        	
        }
        
        catch(Exception e)
		{
			fLogger.fatal("Exception :"+e.getMessage());
		}
		
		finally{
			
			if(session!=null && session.isOpen())
            {
                 
                  session.close();
            }
		}
        
		return responseList;
	}
	//commenting old method
	/*public List<PricolTransactionDetailImpl> getTransactionDetails(String serialNumber, String transactionTimestamp)
	{
		List<PricolTransactionDetailImpl> responseList = new LinkedList<PricolTransactionDetailImpl>();
		
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		
    	Logger fLogger = FatalLoggerClass.logger;
    	
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
        try
        {
        	Query query = session.createQuery("select a.transactionNumber, a.serialNumber, a.transactionTime, b.parameterId, b.parameterValue " +
        			" from AssetMonitoringHeaderEntity a, AssetMonitoringDetailEntity b  where a.transactionNumber=b.transactionNumber " +
        			" and a.transactionTime like '"+transactionTimestamp+"%' and a.serialNumber='"+serialNumber+"' " +
        			" order by a.transactionNumber desc ");
        	
        	Iterator itr = query.list().iterator();
        	Object[] result=null;
        	int transactionNum =0;
        	
        	PricolTransactionDetailImpl implObj = null;
        	HashMap<String,String> transactionDataMap = new HashMap<String, String>();
        	
        	while(itr.hasNext())
        	{
        		result = (Object[])itr.next();
        		int tranNum = (Integer) result[0];
        		
        		 		
        		if(tranNum!=transactionNum)
        		{
        			
        			if(implObj!=null)
        			{
        				responseList.add(implObj);
        			}
        			
        			implObj = new PricolTransactionDetailImpl();
        			AssetEntity asset = (AssetEntity)result[1];
        			implObj.setSerialNumber(asset.getSerial_number().getSerialNumber());
        			
        			Timestamp transTimestamp = (Timestamp)result[2];
        			implObj.setTransactionTimeStamp(transTimestamp.toString());
        			
        			transactionDataMap =  new HashMap<String, String>();
        			MonitoringParameters parameterObj = (MonitoringParameters) result[3];
        			transactionDataMap.put(parameterObj.getParameterName(), result[4].toString());
        			
        			implObj.setTransactionData(transactionDataMap);
        			
        			transactionNum = tranNum;
        		}
        		
        		else
        		{
        			
        			MonitoringParameters parameterObj = (MonitoringParameters) result[3];
        			transactionDataMap.put(parameterObj.getParameterName(), result[4].toString());
        			
        			implObj.setTransactionData(transactionDataMap);
        		}
        		
        		
        	}
        	if(implObj != null)
        		responseList.add(implObj);
        }
        
        catch(Exception e)
		{
			fLogger.fatal("Exception :"+e.getMessage());
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
        
		return responseList;
	}*/
	
	
	
	public List<PricolRollOffImpl> rollOffPricolMachines(int tenancyId, List<String> serialNumber)
	{
		List<PricolRollOffImpl> responseList = new LinkedList<PricolRollOffImpl>();
		
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
        
		
    	Logger fLogger = FatalLoggerClass.logger;
    	
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
        AssetDetailsBO assetBOobj = new AssetDetailsBO();
        
        try
        {
        	//get Client Details
			Properties prop = new Properties();
			String clientName=null;
				
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");
      
			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
			//END of get Client Details
          if(! (session.isOpen() ))
                  {
                              session = HibernateUtil.getSessionFactory().getCurrentSession();
                              session.getTransaction().begin();
                  }

        	AccountEntity assetOwner = null;
        	Query accountQuery = session.createQuery("from AccountTenancyMapping where tenancy_id='"+tenancyId+"'");
        	Iterator itr = accountQuery.list().iterator();
        	
        	while(itr.hasNext())
        	{
        		AccountTenancyMapping assetTenancy = (AccountTenancyMapping) itr.next();
        		assetOwner = assetTenancy.getAccount_id();
        	}
        	
        	for(int i=0; i<serialNumber.size(); i++)
        	{
        		PricolRollOffImpl implObj = new PricolRollOffImpl();
        		
        		AssetControlUnitEntity assetControl=null;
        		Query assetControlUnit = session.createQuery("from AssetControlUnitEntity where serialNumber='"+serialNumber.get(i)+"'");
        		Iterator assetControlUnitItr = assetControlUnit.list().iterator();
        		while(assetControlUnitItr.hasNext())
        		{
        			assetControl = (AssetControlUnitEntity) assetControlUnitItr.next();
        		}
        			
	        	if(assetControl!=null && assetControl.getSerialNumber()!=null)
	        	{
	        		//Check for the existence of AssetEntity
	        		AssetEntity assetEntity =null;
	        		Query assetQuery = session.createQuery("from AssetEntity where serial_number='"+serialNumber.get(i)+"' and active_status=true and client_id="+clientEntity.getClient_id());
	        		Iterator assetItr = assetQuery.list().iterator();
	        		
	        		while(assetItr.hasNext())
	        		{
	        			assetEntity = (AssetEntity)assetItr.next();
	        		}
				
	        		
	        		if(assetEntity==null || assetEntity.getSerial_number()==null)
	        		{
	        			/*Properties prop = new Properties();
	        			String clientName=null;
	        								
	        			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
	        			clientName= prop.getProperty("ClientName");
	        			IndustryBO industryBoObj = new IndustryBO();
	        			ClientEntity client = industryBoObj.getClientEntity(clientName);*/
					
	        			  if(! (session.isOpen() ))
	                      {
	                                  session = HibernateUtil.getSessionFactory().getCurrentSession();
	                                  session.getTransaction().begin();
	                      }
	        			  
	        			AssetEntity newAssetEntity = new AssetEntity();
	        			newAssetEntity.setSerial_number(assetControl);
						newAssetEntity.setPrimary_owner_id(assetOwner.getAccount_id());
						newAssetEntity.setActive_status(true);
						newAssetEntity.setClient_id(clientEntity);
						session.save(newAssetEntity);
										
						//Set primary Contact to the given tenancy
						/*Query query2 = session.createQuery("from AssetAccountMapping where serialNumber='"+assetControl.getSerialNumber()+"'");*/
						//DefectID: Asset Owner Changes - Rajani Nagaraju - 20130719
						Query query2 = session.createQuery("from AssetAccountMapping where serialNumber='"+assetControl.getSerialNumber()+"'" +
								" and accountId="+assetOwner.getAccount_id());
						
						Iterator itr2 = query2.list().iterator();
						boolean present=false;
						while(itr2.hasNext())
						{
							AssetAccountMapping assetAccount = (AssetAccountMapping)itr2.next();
							//assetAccount.setAccountId(assetOwner);
							//session.update(assetAccount);
							present = true;
						}
						
						if(present==false)
						{
							AssetAccountMapping assetAccountMap = new AssetAccountMapping();
							assetAccountMap.setAccountId(assetOwner);
							
							if(assetEntity!=null)
								assetAccountMap.setSerialNumber(assetEntity);
							else
								assetAccountMap.setSerialNumber(newAssetEntity);

							//DefectID: Asset Owner Changes - Rajani Nagaraju - 20130719
							//get the current Date
							Date currentDate = new Date();
							assetAccountMap.setOwnershipStartDate(currentDate);
							
							session.save(assetAccountMap);
						}
						
						//set AssetExtended details
						Query query3 = session.createQuery("from AssetExtendedDetailsEntity where serial_number='"+assetControl.getSerialNumber()+"'");
						Iterator itr3 = query3.list().iterator();
						boolean extendedPresent = false;
						while(itr3.hasNext())
						{
							AssetExtendedDetailsEntity assetExtended = (AssetExtendedDetailsEntity)itr3.next();
							extendedPresent = true;
							break;
						}
						
						if(extendedPresent==false)
						{
							AssetExtendedDetailsEntity extendedDetails = new AssetExtendedDetailsEntity();
							if(assetEntity!=null)
								extendedDetails.setSerial_number(assetEntity);
							else
								extendedDetails.setSerial_number(newAssetEntity);
							
							session.save(extendedDetails);
						} 
					}
	        		
	        		implObj.setSerialNumber(serialNumber.get(i));
	        		implObj.setRollOffStatus("SUCCESS");
	        		
				}
				
	        	else
	        	{
	        		implObj.setSerialNumber(serialNumber.get(i));
	        		implObj.setRollOffStatus("FAILURE");
	        	}
	        	
	        	responseList.add(implObj);
        	}
        	
        }
        
        catch(Exception e)
		{
			fLogger.fatal("Exception :"+e.getMessage());
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
		return responseList;
	}
}
