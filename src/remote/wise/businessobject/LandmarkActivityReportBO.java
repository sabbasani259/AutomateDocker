package remote.wise.businessobject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.ClientEntity;
import remote.wise.businessentity.LandmarkDimensionEntity;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.implementation.LandmarkActivityReportImpl;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;


public class LandmarkActivityReportBO 
{
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger fatalError = WiseLogger.getLogger("LandmarkActivityReportBO:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("EventDetailsBO:","info");*/
	
	//**********************************************Get LandmarkActivityReportImpl Object List******************************************************************************
	public List<LandmarkActivityReportImpl>  getLandmarkActivityReportList( String Period,List<Integer> TenancyIdList, List<Integer> MachineGroupIdList,List<Integer> LandmarkCategoryIDList, List<Integer>	LandmarkIdList) throws CustomFault 
{
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
  
		if(Period==null)
			throw new CustomFault("Please pass a Period");
		if(TenancyIdList==null)
			throw new CustomFault("Please pass a TenancyIdList");
		List<LandmarkActivityReportImpl> landmarkActivityReportImpl = new LinkedList<LandmarkActivityReportImpl>();
		//get the period and hence select the corresponding table
		//Period can be 'Today','Week','Month', 'Quarter', 'Year'
		String basicFromQuery = null;
		String basicSelectQuery = null;
		String basicWhereQuery = null;
		String finalQuery = null;
		
		 // Logger fatalError = Logger.getLogger("fatalErrorLogger");
	        
	        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	        session.beginTransaction();
	        try{
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

		ListToStringConversion conversionObj = new ListToStringConversion();
		String tenancyIdStringList = conversionObj.getIntegerListString(TenancyIdList).toString();
	
	basicSelectQuery = "select a.serialNumber,a.numberOfArrivals,a.numberOfdepartures,a.totalDurationAtLandmark,a.longestDurationAtLandmark,d.landMarkName,d.landMarkCategoryName";

		basicWhereQuery = " where a.tenancyId = b.tenancyId and b.tenancyId in (" + tenancyIdStringList + " )" +
        " and a.landMarkId=e.landMarkId " +

        " and a.serialNumber =f.serial_number" +
   " and cu.group_id = h.group_id" +
   " and h.serial_number = a.serialNumber " +
   " and cu.active_status=1 " +
   " and f.active_status=true " +
   " and f.client_id="+clientEntity.getClient_id()+"";
		
		if(Period.equalsIgnoreCase("Today"))
		{
		basicFromQuery = " from LandmarkAggregateFactDataDayAgg a ";
		
		}
		else if(Period.equalsIgnoreCase("Week"))
		{
		basicFromQuery = " from LandmarkAggregateFactDataWeekAgg a ";
	
		}
		else if(Period.equalsIgnoreCase("Month"))
		{
		basicFromQuery = " from LandmarkAggregateFactDataMonthAgg a ";
	
		}
		else if(Period.equalsIgnoreCase("Quarter"))
		{
		basicFromQuery = " from LandmarkAggregateFactDataQuarterAgg a ";
		
		}
		else if(Period.equalsIgnoreCase("Year"))
		{
		basicFromQuery = " from LandmarkAggregateFactDataYearAgg a ";
		
		}
		basicFromQuery = basicFromQuery +" JOIN a.tenancyId b JOIN a.landMarkId d,CustomAssetGroupEntity cu, AssetCustomGroupMapping h, AssetEntity f ";
		int landmark=0;
		if(! (LandmarkIdList==null || LandmarkIdList.isEmpty()) )
        {
			
              String landmarkIdListStringList = conversionObj.getIntegerListString(LandmarkIdList).toString();
              basicWhereQuery = basicWhereQuery + " and d.landMarkId in ( " + landmarkIdListStringList +" )"; 
              landmark=1;
              
        }
		if(landmark==0)
		{int flag=0;
			if(! (LandmarkCategoryIDList==null || LandmarkCategoryIDList.isEmpty()) )
	        {
				
				
				List<Integer>  landmarkCategory_IDList= new LinkedList<Integer>();
				for(int i=0;i<LandmarkCategoryIDList.size();i++)
				{
					
					Query q = session.createQuery("from LandmarkDimensionEntity where  landMarkCategoryId ="+LandmarkCategoryIDList.get(i));
					Iterator itr = q.list().iterator();
					while(itr.hasNext())
					{
						LandmarkDimensionEntity landmarkDimensionEntity = (LandmarkDimensionEntity)itr.next();
						landmarkCategory_IDList.add(landmarkDimensionEntity.getLandMarkId());
						flag=1;
					}
					
				}
				if(flag==1)
				{
					String landmarkCategoryIDStringList = conversionObj.getIntegerListString(landmarkCategory_IDList).toString();
		              basicWhereQuery = basicWhereQuery + " and d.landMarkId in ( " + landmarkCategoryIDStringList +" )";
				}				
	
	        	}
		}
		if(! (MachineGroupIdList==null || MachineGroupIdList.isEmpty()) )
        {
			
              String machineGroupIdListStringList = conversionObj.getIntegerListString(MachineGroupIdList).toString();
              basicWhereQuery = basicWhereQuery + " and cu.machineGroupId in ( " + machineGroupIdListStringList +" )"; 
              landmark=1;
              
        }
		
		  finalQuery = basicSelectQuery + basicFromQuery + basicWhereQuery;
		 	  
		  
		  Query query = session.createQuery(finalQuery);
			Object[] result=null;
			Iterator itr=query.list().iterator();
			while(itr.hasNext())
			{ 
				LandmarkActivityReportImpl landmarkActivityImpl = new LandmarkActivityReportImpl();
				
				result = (Object[]) itr.next();
				long totalDurationAtLandmarkDay=(Long)(result[3]);
				long totalDurationAtLandmarkDay1=totalDurationAtLandmarkDay/(60*24);
				long totalDurationAtLandmarkHour1=(totalDurationAtLandmarkDay%(24*60))/ 60;
				long totalDurationAtLandmarkMinutes1=(totalDurationAtLandmarkDay%(24*60)) % 60;
				long longestDurationAtLandmarkDay=(Long)(result[4]);
				long longestDurationAtLandmarkDay1=longestDurationAtLandmarkDay/(60*24);
				long longestDurationAtLandmarkHour1=(longestDurationAtLandmarkDay%(24*60))/ 60;
				long longestDurationAtLandmarkMinutes1=(longestDurationAtLandmarkDay%(24*60)) % 60;
				String TotalDayHourMinutes=totalDurationAtLandmarkDay1+"d"+totalDurationAtLandmarkHour1+"h"+totalDurationAtLandmarkMinutes1+"m";	
				String LongestDayHourMinutes=longestDurationAtLandmarkDay1+"d"+longestDurationAtLandmarkHour1+"h"+longestDurationAtLandmarkMinutes1+"m";
				String serialNumber=result[0].toString();
				
				 Query q = session.createQuery("select nick_name from AssetEntity where serial_number='"+serialNumber+"' and active_status=true and client_id="+clientEntity.getClient_id()+"");
				 Iterator itr1=q.list().iterator();
				
				 String nickname=null ;
				while(itr1.hasNext())
				{
			
					 nickname=(String) itr1.next();
					 landmarkActivityImpl.setNickname(nickname);
					 iLogger.info("nickname is"+nickname);
					
					}
				
				landmarkActivityImpl.setSerialNumber(result[0].toString());
				landmarkActivityImpl.setNumberOfArrivals((Integer)result[1]);
				landmarkActivityImpl.setNumberOfdepartures((Integer)result[2]);						
				
				landmarkActivityImpl.setTotalDurationAtLandmarkInMinutes((Long)result[3]);
				landmarkActivityImpl.setLongestDurationAtLandmarkInMinutes((Long)result[4]);
				//landmarkActivityImpl.setTotalDurationAtLandmark(TotalDayHourMinutes);
				//landmarkActivityImpl.setLongestDurationAtLandmark(LongestDayHourMinutes);
				
		
				landmarkActivityImpl.setLandMarkName(result[5].toString());
				landmarkActivityImpl.setLandMarkCategoryName(result[6].toString());
				landmarkActivityReportImpl.add(landmarkActivityImpl);
			}
	        }
			
			catch(Exception e)
			{
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
		return landmarkActivityReportImpl ;
}
}
