package remote.wise.businessobject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetExtendedDetailsEntity;
import remote.wise.businessentity.AssetGroupEntity;
import remote.wise.businessentity.AssetMonitoringHeaderEntity;
import remote.wise.businessentity.ClientEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.businessentity.LandmarkEntity;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.implementation.DomainServiceImpl;
import remote.wise.service.implementation.MapImpl;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;


/**
 * MapBO  will allow to get Map Details
 * @author jgupta41
 *
 */
public class MapBO {
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("MapBO:","businessError");
	//public static WiseLogger fatalError = WiseLogger.getLogger("MapBO:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("MapBO:","info");*/
	
	
	//*******************************************Get Map Details for given LoginId and List of SerialNumbers ********************
	/**
	 * This method will return List of Map Details for given LoginId and List of SerialNumbers and filters if any
	 * @param LoginId:LoginId
	 * @param SerialNumberList:List of SerialNumbers
	 * @param AlertSeverityList:List of AlertSeverity
	 * @param AlertTypeIdList:List of AlertTypeId
	 * @param Landmark_IdList:List of Landmark_Id
	 * @param LandmarkCategory_IdList:List of LandmarkCategory_Id
	 * @param Tenancy_ID:List of Tenancy_ID
	 * @param machineGroupIdList:List of machineGroupId
	 * @param machineProfileIdList:List of machineProfileId
	 * @param modelIdList:List of modelId
	 * @return mapImplList::Returns List of Map Details
	 * @throws CustomFault :custom exception is thrown when the LoginId ,Period,SerialNumber is not specified, SerialNumber is invalid or not specified
	 * @throws IOException 
	 */
	public List<MapImpl>  getMap(String LoginId ,List<String> SerialNumberList,List<String> AlertSeverityList,List<Integer> AlertTypeIdList ,List<Integer> Landmark_IdList, List<Integer> LandmarkCategory_IdList,List<Integer> Tenancy_ID,List<Integer> machineGroupIdList,List<Integer>  machineProfileIdList,List<Integer> modelIdList)throws CustomFault, IOException
	{
		Logger iLogger = InfoLoggerClass.logger;
    	Logger bLogger = BusinessErrorLoggerClass.logger;
		if(LoginId==null){
			bLogger.error("Please pass a LoginId");
			throw new CustomFault("Please pass a LoginId");
			
		}
		DomainServiceImpl domainService = new DomainServiceImpl();
		ContactEntity contactEntity = domainService.getContactDetails(LoginId);
		if(contactEntity.getContact_id()==null)
		{
			bLogger.error("Please pass  a valid LoginId");
			throw new CustomFault("Please pass a valid LoginId");
		}
		
		LandmarkCategoryBO landmarkCategoryBO=new LandmarkCategoryBO();
		List<MapImpl> mapImplList=new LinkedList<MapImpl>();
		
		
		ListToStringConversion conversionObj = new ListToStringConversion();
		 Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	        session.beginTransaction();
	        boolean flags=false;
	        String childTenancyIdStringList=null;
	        List<Integer>Tenancy_IDs = new LinkedList<Integer>();
	        if(Tenancy_ID!=null)
	        {
	        Tenancy_IDs = getSubTenancyIds(Tenancy_ID); 
	    	childTenancyIdStringList = conversionObj.getIntegerListString(
					Tenancy_IDs).toString();
	        }
		
			 List<String>finalserNumList = new LinkedList<String>();
			 
		try{
			 iLogger.info("*******Get maximum Parameter id for given EngineHours*********************");
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

			 //added by smitha on 13/6/2013	
			
			 
		  //end 13/6/2013
		Query query = session.createQuery("select d.parameterId  from MonitoringParameters d where d.parameterName like 'Hour' order by d.parameterId desc");
		query.setMaxResults(1);
		Iterator itr=query.list().iterator();
		int parameterId=0 ;
		while(itr.hasNext())
		{
			parameterId=(Integer) itr.next();
		}
			
		iLogger.info("*******Get maximum Parameter id for given Latitude*********************");
		Query query1 = session.createQuery("select d.parameterId from MonitoringParameters d where d.parameterName like 'Latitude' order by d.parameterId desc");
		query1.setMaxResults(1);
		Iterator itr1=query1.list().iterator();
		int parameterId1=0 ;
		while(itr1.hasNext())
		{
			parameterId1=(Integer) itr1.next();
		}
		iLogger.info("*******Get maximum Parameter id for given Longitude*********************");

	  
		Query query2 = session.createQuery("select d.parameterId from MonitoringParameters d where d.parameterName like 'Longitude' order by d.parameterId desc");
		query2.setMaxResults(1);
		Iterator itr2=query2.list().iterator();
		int parameterId2=0 ;
		while(itr2.hasNext())
		{
			parameterId2=(Integer) itr2.next();
		}
		
		iLogger.info("*******Get maximum Parameter id for given EngineStatus*********************");

		  
		Query query3 = session.createQuery("select d.parameterId from MonitoringParameters d where d.parameterName like 'ENGINE_ON' order by d.parameterId desc");
		query3.setMaxResults(1);
		Iterator itr3=query3.list().iterator();
		int parameterId3=0 ;
		while(itr3.hasNext())
		{
			parameterId3=(Integer) itr3.next();
		}
		
		int flag=0;
		List<String> SerialNumberList1=new LinkedList<String>();
		List<String> SerialNumberList2=new LinkedList<String>();
		List<String> SerialNumberList3=new LinkedList<String>();
		if(SerialNumberList==null)
		{
			if((! (Landmark_IdList==null || Landmark_IdList.isEmpty()) )||(! (LandmarkCategory_IdList==null || LandmarkCategory_IdList.isEmpty()) ))
			{
				if(! (Landmark_IdList==null || Landmark_IdList.isEmpty()) )
				{
				
				List<LandmarkEntity> landmarkAssetEntity=landmarkCategoryBO.LandmarkEntityList(Landmark_IdList);	
				Query query4= session.createQuery("select a.Serial_number from LandmarkAssetEntity a Join a.Landmark_id b Join a.Serial_number c where a.Landmark_id in (:list)").setParameterList("list", landmarkAssetEntity);
				Iterator itr4= query4.list().iterator();
				while(itr4.hasNext())
				{
					AssetEntity serial=(AssetEntity)itr4.next();;
					SerialNumberList2.add(serial.getSerial_number().getSerialNumber());
					flag=1;
				}
			}
			if(flag==0)
			{
			if(! (LandmarkCategory_IdList==null || LandmarkCategory_IdList.isEmpty()) )
			{
				List<LandmarkEntity> landmarkAssetEntity=landmarkCategoryBO.LandmarkCategoryEntityList1(LandmarkCategory_IdList);	
				
				Query query5= session.createQuery("select a.Serial_number from LandmarkAssetEntity a Join a.Landmark_id b Join a.Serial_number c where a.Landmark_id in (:list)").setParameterList("list", landmarkAssetEntity);
			
				Iterator itr5 = query5.list().iterator();
				while(itr5.hasNext())
				{
					AssetEntity serial=(AssetEntity)itr5.next();;
					SerialNumberList2.add(serial.getSerial_number().getSerialNumber());
					
				}
			}
			}
			}
					
			
	  String tenancyIdStringList = conversionObj.getIntegerListString(Tenancy_ID).toString();
	  if(! (AlertSeverityList==null || AlertSeverityList.isEmpty()) || ( ! (AlertTypeIdList==null ||AlertTypeIdList.isEmpty())) )
	  {
		  flags=true;
//		  String basicQueryString = "select a.SerialNumber from notification_fact_Yearagg a JOIN a.Tenancy_Id b  JOIN a.NotificationDimension_Id e" ;
		  String basicQueryString = "select a.SerialNumber from notification_fact_Yearagg a JOIN a.Notification_Id e,TenancyDimensionEntity b" ;
//		  changes for 08/06/2013
//          String basicWhereQuery = " where b.tenancyId in ( "+tenancyIdStringList+") and b.tenacy_Dimension_Id = a.Tenancy_Id ";
		  String basicWhereQuery = " where b.tenancyId in ( "+tenancyIdStringList+") and b.tenacy_Dimension_Id = a.Tenancy_Id ";
        
          
          if(! (AlertTypeIdList==null ||AlertTypeIdList.isEmpty()))   
          {
        	  String AlertTypeIdListStringList = conversionObj.getIntegerListString(AlertTypeIdList).toString();
        	  basicWhereQuery = basicWhereQuery + " and e.Notification_Type_Id in ( "+ AlertTypeIdListStringList + " )"; 
				
          }
         /* if(! (machineGroupIdList == null || machineGroupIdList.isEmpty()) )
          {
                basicQueryString = basicQueryString + " JOIN a.MachineGroup_Id c ";
                
                String machineGroupIdStringList = conversionObj.getIntegerListString(machineGroupIdList).toString();
                basicWhereQuery = basicWhereQuery + " and c.machineGroupId in ( "+ machineGroupIdStringList + " )"; 
          }*/
          // Added on 2013-06-25 by Juhi
          
   	   if(! (machineGroupIdList == null || machineGroupIdList.isEmpty()) )
       {                         
             String machineGroupIdStringList = conversionObj.getIntegerListString(machineGroupIdList).toString();
        		basicQueryString = basicQueryString
				+ " , CustomAssetGroupEntity c, AssetCustomGroupMapping h ";
				basicWhereQuery = basicWhereQuery
				+ " and c.group_id = h.group_id and c.group_id in ("
				+ machineGroupIdStringList + ") and "
				+ " h.serial_number = a.serialNumber and c.active_status=1 and c.client_id="+clientEntity.getClient_id()+"";
				 
       }
          if( ( !(machineProfileIdList==null || machineProfileIdList.isEmpty()) ) || ( ! (modelIdList==null ||modelIdList.isEmpty())) )
          {
                basicQueryString = basicQueryString + " JOIN a.AssetClass_Id d ";
                
                if(!(machineProfileIdList==null || machineProfileIdList.isEmpty()))
                {
                      String machineProfileIdStringList = conversionObj.getIntegerListString(machineProfileIdList).toString();
                      basicWhereQuery = basicWhereQuery + " and d.assetGroupId in ( "+ machineProfileIdStringList + " )"; 
                }
                
                if(!(modelIdList==null || modelIdList.isEmpty()))
                {
                      String modelIdStringList = conversionObj.getIntegerListString(modelIdList).toString();
                      basicWhereQuery = basicWhereQuery + " and d.assetTypeId in ( "+ modelIdStringList + " )"; 
                }
          }
          
          if(! (AlertSeverityList==null || AlertSeverityList.isEmpty()))
		  {
        	  basicWhereQuery = basicWhereQuery + " and e.Severity  in (:list )"; 
        	  basicQueryString = basicQueryString + basicWhereQuery;
        	  iLogger.info("basic query is: "+basicQueryString);
              Query query6= session.createQuery(basicQueryString).setParameterList("list", AlertSeverityList);;
              Iterator it6 = query6.list().iterator();
    				while(it6.hasNext())
    				{
    					String serial=(String)it6.next();;
    					SerialNumberList1.add(serial);
    				}
    				iLogger.info("basic query value with alert is: "+SerialNumberList1.size());
		  }
          else
          {
          basicQueryString = basicQueryString + basicWhereQuery;
          Query query6= session.createQuery(basicQueryString);
          Iterator itr6 = query6.list().iterator();
				while(itr6.hasNext())
				{
					String serial=(String)itr6.next();;
					SerialNumberList1.add(serial);
				}
				iLogger.info("basic query value without alert is: "+SerialNumberList1.size());
          }
          
          
          
	  }
	  else
	  { 
//		  String basicQueryString = "select a.serialNumber from TenancyBridgeEntity b,AssetMonitoringFactDataYearAgg a " ;
//          String basicWhereQuery = " where b.parentId in ( "+tenancyIdStringList+") and a.tenancyId=b.childId ";
//		  changes for 08/06/2013
		  String basicQueryString = "select a.serialNumber from TenancyDimensionEntity b,AssetMonitoringFactDataYearAgg a " ;
          String basicWhereQuery = " where b.tenancyId in ( "+tenancyIdStringList+") and a.tenancyId=b.tenacy_Dimension_Id ";
         /*    
             if(! (machineGroupIdList == null || machineGroupIdList.isEmpty()) )
             {
                   basicQueryString = basicQueryString + " JOIN a.machineGroupId c ";
                   
                   String machineGroupIdStringList = conversionObj.getIntegerListString(machineGroupIdList).toString();
                   basicWhereQuery = basicWhereQuery + " and c.machineGroupId in ( "+ machineGroupIdStringList + " )"; 
             }*/
   	   if(! (machineGroupIdList == null || machineGroupIdList.isEmpty()) )
       {                         
             String machineGroupIdStringList = conversionObj.getIntegerListString(machineGroupIdList).toString();
        		basicQueryString = basicQueryString
				+ " , CustomAssetGroupEntity c, AssetCustomGroupMapping h ";
				basicWhereQuery = basicWhereQuery
				+ " and c.group_id = h.group_id and c.group_id in ("
				+ machineGroupIdStringList + ") and "
				+ " h.serial_number = a.serialNumber and c.active_status=1 and c.client_id="+clientEntity.getClient_id()+"";
				 
       }
             if( ( !(machineProfileIdList==null || machineProfileIdList.isEmpty()) ) || ( ! (modelIdList==null ||modelIdList.isEmpty())) )
             {
                   basicQueryString = basicQueryString + " JOIN a.assetClassDimensionId d ";
                   
                   if(!(machineProfileIdList==null || machineProfileIdList.isEmpty()))
                   {
                         String machineProfileIdStringList = conversionObj.getIntegerListString(machineProfileIdList).toString();
                         basicWhereQuery = basicWhereQuery + " and d.assetGroupId in ( "+ machineProfileIdStringList + " )"; 
                   }
                   
                   if(!(modelIdList==null || modelIdList.isEmpty()))
                   {
                         String modelIdStringList = conversionObj.getIntegerListString(modelIdList).toString();
                         basicWhereQuery = basicWhereQuery + " and d.assetTypeId in ( "+ modelIdStringList + " )"; 
                   }
             }

                       
             basicQueryString = basicQueryString + basicWhereQuery;
             Query query7= session.createQuery(basicQueryString);
             Iterator itr7 = query7.list().iterator();
				while(itr7.hasNext())
				{
					String serial=(String)itr7.next();;
					SerialNumberList2.add(serial);
					
				}
	  }	
	  
	  if(SerialNumberList1.size()==0)
	  {
		  if(SerialNumberList2.size()!=0)
		  SerialNumberList3=SerialNumberList2;
	  }
	  else
	  {
		  SerialNumberList3=SerialNumberList1;
	  }
	  if((SerialNumberList1.size()!=0) &&(SerialNumberList2.size()!=0))
	  {
	  for(int i=0;i<SerialNumberList1.size();i++)
      {
    	  for(int j=0;j<SerialNumberList2.size();j++)
    	  {	
    		  if((SerialNumberList1.get(i)==SerialNumberList2.get(j)) ||(SerialNumberList1.get(i).equalsIgnoreCase(SerialNumberList2.get(j))))
    		  {
    			  SerialNumberList3.add(SerialNumberList1.get(i));
    		  }
    	  }
    	  
    }
	  }
	  
		}
		else
		{
			SerialNumberList3=SerialNumberList;
			
		}
		iLogger.info("SerialNumberList3 size is "+SerialNumberList3.size());
		iLogger.info("***********************Get Transaction No. for given list of serial no.*******************************************");
		
		if(SerialNumberList3==null ||SerialNumberList3.isEmpty())
		{
			return mapImplList;
		}
		
		
			finalserNumList=SerialNumberList3;
		
		
			//DefectID: - Asset Owner Changes - Rajani Nagaraju - 20130719
			if(Tenancy_ID!=null){ //all serial nos
			
			/*String querys = " select x.serialNumber,x.accountId from AssetAccountMapping x,AccountTenancyMapping y " +
			" where x.accountId=y.account_id and y.tenancy_id in ("
			+ childTenancyIdStringList + " )";*/
	 
			String querys = " select x ,y.account_id from AssetEntity x, AccountTenancyMapping y " +
			" where x.primary_owner_id=y.account_id and y.tenancy_id in ("
			+ childTenancyIdStringList + " )";
			 
			 Iterator itrs=session.createQuery(querys).list().iterator();
			 Object[] results = null;
			 List<String>serNumList = new LinkedList<String>();
			 List<Integer>accIDList = new LinkedList<Integer>();
			 while(itrs.hasNext()){
				 results = (Object[]) itrs.next();
				 AssetEntity serNum=(AssetEntity)results[0];
				 AccountEntity accID=(AccountEntity)results[1];
				 serNumList.add(serNum.getSerial_number().getSerialNumber());
	        	 accIDList.add(accID.getAccount_id());
			 }
			 
			
			
			 //iterate serNumList and add non existing serial nos to final list
			 Iterator iterSerialNoList = serNumList.iterator();
			 for (String serialNo : serNumList) {
			if(!finalserNumList.contains(serialNo))	{
				finalserNumList.add(serialNo);
			}
			}
		}
		
		List<Integer> transactionNumberList=new LinkedList<Integer>();
		List<Timestamp> transactionTimeList=new LinkedList<Timestamp>();
		
//		String serialNumberList3String = conversionObj.getStringList(SerialNumberList3).toString();
		String finalserNumListString = conversionObj.getStringList(finalserNumList).toString();
//		Query query8 = session.createQuery(" select max(transactionNumber),transactionTime from AssetMonitoringHeaderEntity where serialNumber in ("+serialNumberList3String+") group by serialNumber");
		Query query8 = session.createQuery(" select max(transactionNumber),transactionTime from AssetMonitoringHeaderEntity where serialNumber in ("+finalserNumListString+") group by serialNumber");
		Iterator itr8=query8.list().iterator();
		Object result5[]=null;
		int transactionNumber=0;
		while(itr8.hasNext())
		{	
			result5 = (Object[]) itr8.next();
			transactionNumber=(Integer)result5[0];
	
			transactionNumberList.add(transactionNumber);
//			String querytime="select transactionTime from AssetMonitoringHeaderEntity where transactionNumber in ("+transactionNumber+") and serialNumber in ("+serialNumberList3String+") group by serialNumber";
			String querytime="select transactionTime from AssetMonitoringHeaderEntity where transactionNumber in ("+transactionNumber+") and serialNumber in ("+finalserNumListString+") group by serialNumber";
			Iterator itrrr=session.createQuery(querytime).list().iterator();
			Object timeResult=null;
			Timestamp lastReportedTime;
			while(itrrr.hasNext()){
				
								
				timeResult = (Object) itrrr.next();
				lastReportedTime =(Timestamp)timeResult;
				iLogger.info("lastReportedTime in BO "+lastReportedTime);	 							
				transactionTimeList.add(lastReportedTime);
			}
		}		
		
		String transactionNumberStringList = conversionObj.getIntegerListString(transactionNumberList).toString();
	
		
		if(transactionNumberList==null || transactionNumberList.isEmpty())
		{
			return mapImplList;
		}
	/*String queryString="select max(a.transactionNumber),c.parameterId,a.parameterValue " +
			" from AssetMonitoringDetailEntity a , AssetMonitoringHeaderEntity b , MonitoringParameters c " +
			" where a.transactionNumber=b.transactionNumber and " +
			" a.parameterId= c.parameterId and a.transactionNumber in " +
			" (select w.transactionNumber from AssetMonitoringHeaderEntity w where w.serialNumber in " +
			" ("+serialNumberList3String+")) " +
					" and c.parameterId="+parameterId3+" group by b.serialNumber ";*/
	String queryString="select max(a.transactionNumber),c.parameterId,a.parameterValue " +
	" from AssetMonitoringDetailEntity a , AssetMonitoringHeaderEntity b , MonitoringParameters c " +
	" where a.transactionNumber=b.transactionNumber and " +
	" a.parameterId= c.parameterId and a.transactionNumber in " +
	" (select max(w.transactionNumber) from AssetMonitoringHeaderEntity w where w.serialNumber in " +
	" ("+finalserNumListString+") group by w.serialNumber ) " +
			" and c.parameterId="+parameterId3+" group by b.serialNumber ";
		
	iLogger.info("general query is"+queryString);
		Query query9 = session.createQuery(queryString);
		Object[] result=null;
		Iterator itr9=query9.list().iterator();
		List<String> EngineStatusList=new LinkedList<String>();
		while(itr9.hasNext())
		{
			result = (Object[]) itr9.next();
			if((String)result[2]==null)
				EngineStatusList.add(null);
			else
			EngineStatusList.add((String)result[2]);
		/*	MapImpl mapImpl3=new MapImpl();
			mapImpl3.setEngineStatus((String)result[2]);
			mapImplList.add(mapImpl3);*/
		}
		List<Integer> parameterIdList=new LinkedList<Integer>();
		parameterIdList.add(parameterId);
		parameterIdList.add(parameterId1);
		parameterIdList.add(parameterId2);
		String parameterIdStringList = conversionObj.getIntegerListString(parameterIdList).toString();
		String queryString1="select b.serialNumber, a.transactionNumber,c.parameterId,a.parameterValue from AssetMonitoringDetailEntity a,AssetMonitoringHeaderEntity b ,MonitoringParameters c where a.transactionNumber = b.transactionNumber  and a.parameterId=c.parameterId and a.transactionNumber in("+transactionNumberStringList+") and c.parameterId in ("+parameterIdStringList+") order by b.serialNumber";
		Query query10 = session.createQuery(queryString1);
		Object[] result1=null;
		Iterator itr10=query10.list().iterator();
		List<String> LatList=new ArrayList<String>();
		List<String> SerList=new ArrayList<String>();
		
		List<String> LongList=new ArrayList<String>();
		List<String> NickNameList=new ArrayList<String>();
		List<String> TotalMachineHoursList=new ArrayList<String>();
		int index1=0,index2=0,index3=0,index4=0,index5=0;
		while(itr10.hasNext())
		{
			 //MapImpl mapImpl=new MapImpl();
			
			result1 = (Object[]) itr10.next();
			 AssetEntity asset=(AssetEntity)result1[0];
			 if(result1[2]!=null){
				 if((Integer)result1[2]==parameterId)
					{
						if(result1[3]!=null){							
							TotalMachineHoursList.add(index1++,(String)result1[3]);					
						}							
					}
					else if((Integer)result1[2]==parameterId1)
					{
						if(result1[3]!=null){
							LatList.add(index2++,(String)result1[3]);
						}
						
					}
					else if((Integer)result1[2]==parameterId2)
					{
						if(result1[3]!=null){
							LongList.add(index3++,(String)result1[3]);
						}													
					}
			 }
			
			if(!SerList.contains(asset.getSerial_number().getSerialNumber())){
				SerList.add(index4++,(asset.getSerial_number().getSerialNumber()));
			}
			if(!NickNameList.contains(asset.getNick_name())){
				NickNameList.add(index5++,asset.getNick_name());
				iLogger.info("NickNameList  "+NickNameList.size());
			}
			
			
			// mapImpl.setNickname(asset.getNick_name());
			//mapImplList.add(mapImpl);
		}
		List<Timestamp> OperatingEndTimeList=new LinkedList<Timestamp>();
		List<Timestamp> OperatingStartTimeList=new LinkedList<Timestamp>();
//		Query query11 = session.createQuery(" from AssetExtendedDetailsEntity where serial_number in ("+serialNumberList3String+") group by serial_number");
		Query query11 = session.createQuery(" from AssetExtendedDetailsEntity where serial_number in ("+finalserNumListString+") group by serial_number");
		Iterator itr11=query11.list().iterator();
		while(itr11.hasNext())
			{ 
			
			AssetExtendedDetailsEntity assetExtendedDetailsEntity=(AssetExtendedDetailsEntity)itr11.next();	
			if(assetExtendedDetailsEntity==null)
			{
				OperatingEndTimeList.add(null);
			OperatingStartTimeList.add(null);
			}
			else
			{
				OperatingEndTimeList.add(assetExtendedDetailsEntity.getOperatingEndTime());
				OperatingStartTimeList.add(assetExtendedDetailsEntity.getOperatingStartTime());
			}
		/*	MapImpl mapImpl5=new MapImpl();
			mapImpl5.setOperatingEndTime(assetExtendedDetailsEntity.getOperatingEndTime());
			 mapImpl5.setOperatingStartTime(assetExtendedDetailsEntity.getOperatingStartTime());
			 mapImplList.add(mapImpl5);
			*/
			}
		//DF20190204 @abhishek---->add partition key for faster retrieval
//			Query qs2 = session.createQuery(" select a.serialNumber,count(a.activeStatus) from AssetEventEntity a where a.activeStatus=1 and a.serialNumber in ("+serialNumberList3String+") group by a.serialNumber");
			Query qs2 = session.createQuery(" select a.serialNumber,count(a.activeStatus) from AssetEventEntity a where a.activeStatus=1 and a.partitionKey =1 and a.serialNumber in ("+finalserNumListString+") group by a.serialNumber");
			Iterator itr7=qs2.list().iterator();
			Object result2[]=null;
			List<Long> ActiveAlertList=new LinkedList<Long>();
			while(itr7.hasNext())
			{ 	
				result2=(Object[])itr7.next();
				if(result2==null)
					ActiveAlertList.add(null);
				else
					ActiveAlertList.add((Long)result2[1]);
				
				/*MapImpl mapImpl4=new MapImpl();
				mapImpl4.setActiveAlert((Long)result2[1]);
				 mapImplList.add(mapImpl4);*/
				
			}
			/*select a.Asset_Group_ID,a.Asseet_Group_Name from asset_group a
			where a.Asset_Group_ID =(select Asset_Group_ID from products 
			where Product_ID=(select Product_ID from asset where Serial_Number='12345678901234567890'));*/
			
			
		/*	String queryy="select asset_group_id,asset_group_name from AssetGroupEntity " +
					"where asset_group_id in (" +
					"select assetGroupId from ProductEntity" +
					" where productId in (" +
					"select productId from AssetEntity " +
					"where serial_number in ("+serialNumberList3String+")))"; */
			
			/*String queryy=" select a.asset_group_id,a.asset_group_name ,b.productId, c.serial_number from AssetGroupEntity a,ProductEntity b ,AssetEntity c  " +
					"where a.asset_group_id=b.assetGroupId and b.productId=c.productId and c.serial_number in ("+serialNumberList3String+")";*/
			String queryy=" select a.asset_group_id,a.asset_group_name ,b.productId, c.serial_number from AssetGroupEntity a,ProductEntity b ,AssetEntity c  " +
			"where a.asset_group_id=b.assetGroupId and b.productId=c.productId and c.serial_number in ("+finalserNumListString+") and a.client_id="+clientEntity.getClient_id()+" and b.clientId="+clientEntity.getClient_id()+" and c.client_id="+clientEntity.getClient_id()+" and c.active_status=true";
			
			Iterator itrr=session.createQuery(queryy).list().iterator();
			Object result6[]=null;
			List<Integer> assetGroupIdList=new LinkedList<Integer>();
			List<String> assetGroupNameList=new LinkedList<String>();
			List<String> SerialNumList=new LinkedList<String>();
		//	infoLogger.info("iterator size is "+itrr.hasNext());
	         while(itrr.hasNext()){        	
	        	 
	        	 result6 = (Object[]) itrr.next();
	        	 int assetGroupId=(Integer)result6[0];
	        	 String assetGroupName=(String)result6[1];
	        	 assetGroupIdList.add(assetGroupId);
	        	 assetGroupNameList.add(assetGroupName);
	        	 AssetControlUnitEntity SerialNum=(AssetControlUnitEntity)result6[3];
	        	 SerialNumList.add(SerialNum.getSerialNumber());
	        	
	         }
	         //added by smitha on 13/6/2013
	     //    infoLogger.info("finalserNumList "+finalserNumList.size());
//	          infoLogger.info("contains  "+serNumList.containsAll(SerialNumberList3));
	         //end 13/6/2013
	         if(ActiveAlertList!=null)
			for(int i=0;i<ActiveAlertList.size();i++)
			{
			MapImpl mapImpl =new MapImpl();
			if(ActiveAlertList.get(i)!=null){
			mapImpl.setActiveAlert(ActiveAlertList.get(i));
			} else {
				mapImpl.setActiveAlert(0L);
			}
			//if(EngineStatusList.get(i)!=null)
			if(EngineStatusList.size()>=i+1){
			mapImpl.setEngineStatus(EngineStatusList.get(i));
			} else {
				mapImpl.setEngineStatus("");
			}
			if(LatList.get(i)!=null){
			mapImpl.setLatitude(LatList.get(i));
			} else {
				mapImpl.setLatitude("");
			}
			
			if(LongList.get(i)!=null){
			mapImpl.setLongitude(LongList.get(i));
			} else {
				mapImpl.setLongitude("");
			}
			if(NickNameList.get(i)!=null){
			mapImpl.setNickname(NickNameList.get(i));
			} else {
				mapImpl.setNickname("");
			}
			if(OperatingEndTimeList.get(i)!=null)
			{
			mapImpl.setOperatingEndTime(OperatingEndTimeList.get(i));
			}
			else {
				mapImpl.setOperatingEndTime(null);	
			}
			if(OperatingStartTimeList.get(i)!=null){
				mapImpl.setOperatingStartTime(OperatingStartTimeList.get(i));

			}
			else{
				mapImpl.setOperatingStartTime(null);

			}
			
			/*if(SerList.get(i)!=null)
			mapImpl.setSerialNumber(SerList.get(i));*/			
				if(transactionTimeList.get(i)!=null)	{				
			mapImpl.setLastReportedTime(transactionTimeList.get(i).toString());
				} else {
					mapImpl.setLastReportedTime("");
				}
			if(assetGroupIdList !=null && !assetGroupIdList.isEmpty()){
				if( assetGroupIdList.size() <= i){
			mapImpl.setProfileCode(0);
			}else{
			mapImpl.setProfileCode(assetGroupIdList.get(i));
			}
			}
			if(SerialNumList !=null && !SerialNumList.isEmpty()){
				if( SerialNumList.size() <= i){
					mapImpl.setSerialNumber("");
				}else{
				mapImpl.setSerialNumber(SerialNumList.get(i));
				}
				
			}
			if(assetGroupNameList !=null && !assetGroupNameList.isEmpty()){
				if( assetGroupNameList.size() <= i){
					mapImpl.setProfileName("");
				}else{
				mapImpl.setProfileName(assetGroupNameList.get(i));
				}
				
			}
			if(TotalMachineHoursList.get(i)!=null){
			mapImpl.setTotalMachineHours(TotalMachineHoursList.get(i));
			} else {
				mapImpl.setTotalMachineHours("");
			}
			mapImplList.add(mapImpl);
			}
			
		}
		/*catch(Exception e){

        	fatalError.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());

        }*/
		/*catch(Exception e){
			infoLogger.info(e.getMessage());
		}*/
		
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
		return mapImplList;
	}
	//*******************************************End of Get Map Details for given LoginId and List of SerialNumbers ********************

	/**
	 * method to fetch list of child ids for given parent tenancy id list
	 * @param tenancyIdList
	 * @return
	 */
	public List<Integer> getSubTenancyIds(List<Integer> tenancyIdList){	
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		ListToStringConversion conversionObj = new ListToStringConversion();
		String tenancyIdStringList = conversionObj.getIntegerListString(tenancyIdList).toString();
		List<Integer> subTenancyIdList = new ArrayList<Integer>();

		String finalQuery = null;

		try {
			finalQuery = "SELECT t1.childId,t1.parentId FROM  TenancyBridgeEntity  t1 WHERE t1.parentId in("+ tenancyIdStringList+")";
			Query query = session.createQuery(finalQuery);
			Iterator itr = query.list().iterator();
			Object[] result = null;


			while (itr.hasNext()) {
				result = (Object[]) itr.next();
				int childId=(Integer) result[0];
				if (result[0] != null) {
					subTenancyIdList.add((Integer) result[0]);
				}		
			}
		}
		finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		iLogger.info("Total time taken by getSubTenancyIds() is"+ duration);
		if(subTenancyIdList==null || subTenancyIdList.size()==0){
			return tenancyIdList;
		}
		return subTenancyIdList;
	}
}
