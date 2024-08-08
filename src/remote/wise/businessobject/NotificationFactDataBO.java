package remote.wise.businessobject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountDimensionEntity;
import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetClassDimensionEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.NotificationDimensionEntity;
import remote.wise.businessentity.NotificationFactEntity;
import remote.wise.businessentity.ProductEntity;
import remote.wise.businessentity.TenancyDimensionEntity;
import remote.wise.businessentity.TenancyEntity;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;
/**
 * NotificationFactDataBO will allow to insert data in NotificationFactEntity_DayAgg table for a current day.
 * @author jgupta41
 *
 */
 
public class NotificationFactDataBO {
	
	/*public static WiseLogger businessError = WiseLogger.getLogger("NotificationFactDataBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("NotificationFactDataBO:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("NotificationFactDataBO:","info");
	*/
	
	/*static Logger fatalError = Logger.getLogger("fatalErrorLogger");
	static Logger businessError = Logger.getLogger("businessErrorLogger");
	static Logger infoLogger = Logger.getLogger("infoLogger");  */
	//******************************************Start of setNotificationFactData************************************************************
	/**
	 *  This method will set to insert data in NotificationFactEntity_DayAgg table for a current day.
	 * @return SUCCESS
	 */
	public String setNotificationFactData()
	{
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
  
		ListToStringConversion listToStringConversion=new ListToStringConversion();
		 Date date = new Date();
		 String TodaysDate=null;
		String Today=null;
		 Timestamp Time_Key =null;
	
		 Session session = HibernateUtil.getSessionFactory().openSession();
	        session.beginTransaction();
	        try{
	        	 Calendar calendar = Calendar.getInstance();
	       	 DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			 TodaysDate= dateFormat.format(date);
			 iLogger.info(TodaysDate);
			 SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 
			 Date myDate = dateFormat.parse(TodaysDate);
			 Today=dateFormat1.format(myDate);
				iLogger.info("simpledate"+Today);
				Time_Key = Timestamp.valueOf(Today);
				iLogger.info("time_key"+Time_Key);
				Timestamp timestamp2=null;
	        	Query query7 = session.createQuery("select max(Time_Key) From NotificationFactEntity_DayAgg");
				Iterator itr7=query7.list().iterator();
				Timestamp ts=null;
				while(itr7.hasNext())
				{
					ts=(Timestamp)itr7.next();
					timestamp2=ts;
				}
				
				// If OLAP is empty, get minimum Date from AssetEventEntity
				// table and take it as Olap Last Entry
				if (ts == null) 
				{

					Query query = session
							.createQuery(" select min(a.eventGeneratedTime) from AssetEventEntity a where a.eventGeneratedTime>=(SELECT min(date) FROM DateDimensionEntity)");
					Iterator itr12 = query.list().iterator();
					if (itr12.hasNext()) 
					{
						Timestamp ts1 = (Timestamp) itr12.next();
						Date date3 = new Date(ts1.getTime());
						Calendar calendar1 = Calendar.getInstance();
						calendar1.setTime(date3);
						calendar1.add(Calendar.DAY_OF_YEAR, -1);
						Date olap = calendar1.getTime();
						Timestamp timestamp = new Timestamp(olap.getTime());

						ts = timestamp;
					}
				}
				if(ts!=null)	 
				{ List<String> serList =new LinkedList<String>();
				 String OlapLastDate=dateFormat1.format(ts);
				 Date OlapLastDate1=dateFormat.parse(OlapLastDate);
				if(OlapLastDate.equalsIgnoreCase(Today))
				{			 
					Today=dateFormat1.format(myDate);
					iLogger.info("simpledate"+Today);
					Time_Key = Timestamp.valueOf(Today);
					iLogger.info("time_key"+Time_Key);		
					 Query q = session.createQuery("From NotificationFactEntity_DayAgg a where a.Time_Key like  '"+TodaysDate+" %'");
					
					 Iterator itr11=q.list().iterator();
					 while(itr11.hasNext())
						{	
				   		NotificationFactEntity notificationFactEntity_DayAgg=(NotificationFactEntity)itr11.next();
				   		String Serial=notificationFactEntity_DayAgg.getSerialNumber();
				   		serList.add(Serial);
				   		NotificationDimensionEntity not_id=notificationFactEntity_DayAgg.getNotification_Id();
				   		int eventId=not_id.getNotification_Id().getEventId();
				   		String queryString1="select a.eventId,count(a.eventId) from AssetEventEntity a where a.serialNumber='"+Serial+"' and a.eventGeneratedTime like '"+TodaysDate+"%' and a.eventId="+eventId+" group by a.eventId ";
						Query query5 = session.createQuery(queryString1);
						Iterator itr5=query5.list().iterator();
						Object result1[]=null;
						long notificationcount=0;
						while(itr5.hasNext())
						{	
							result1= (Object[])itr5.next();
							notificationcount=(Long)result1[1];										
						}
						notificationFactEntity_DayAgg.setNotificationCount((int)notificationcount);
						session.update("NotificationFactEntity_DayAgg", notificationFactEntity_DayAgg);
						}
					 
					 String serStringList=listToStringConversion.getStringList(serList).toString();
					 Query queryyy = session.createQuery(" select distinct a.serialNumber from AssetEventEntity a where a.serialNumber not in ("+serStringList+") and a.eventGeneratedTime like '"+TodaysDate+" %'");
				        Iterator itrr=queryyy.list().iterator();
				       List<String>  Serial_NumberList=new LinkedList<String>();
						while(itrr.hasNext())
						{	
							AssetEntity a= (AssetEntity)itrr.next();
							Serial_NumberList.add(a.getSerial_number().getSerialNumber());
							 iLogger.info("Serial_Number"+a.getSerial_number().getSerialNumber());
						}
						 iLogger.info("list of asset"+Serial_NumberList.size());
						for(int i=0;i<Serial_NumberList.size();i++)
						{
							String SerialNumber=Serial_NumberList.get(i);
							//Code changes done by Juhi on 5-December-2013 ---start
							Date ownershipStartDate=null;
							int accountId1=0;
							Query qry = session
							.createQuery("select max(ownershipStartDate) " 
									+ "FROM AssetAccountMapping where serialNumber='" 
									+ SerialNumber + "' " 
									+ "and  ownershipStartDate <= '" 
									+ TodaysDate + "'");
				
					Iterator itr = qry.list().iterator();
					
					if (itr.hasNext()) {
						ownershipStartDate = (Date) itr.next();
						
						if (ownershipStartDate != null) {
							String owdate = dateFormat.format(ownershipStartDate);
							qry = session.createQuery("select a.accountId "
									+ "from AssetAccountMapping a ,AssetEntity c "
									+ "where a.serialNumber='"
									+ SerialNumber
									+ "'  and c.active_status=true and c.serial_number=a.serialNumber "
									+ " and a.ownershipStartDate like '" + owdate + "'");
							itr = qry.list().iterator();
							
							if (itr.hasNext()) {
								AccountEntity accountEntity= (AccountEntity)itr.next();
								accountId1=accountEntity.getAccount_id();
								 iLogger.info("AccountEntity"+accountEntity.getAccount_id());
								
							}
						}
					}
					
					if (ownershipStartDate == null)
					{
						qry = session.createQuery("select a.primary_owner_id "
								+ "from AssetEntity a "
								+ "where a.serial_number='"
								+ SerialNumber
								+ "'and a.active_status=true ");
						itr = qry.list().iterator();
						
						if (itr.hasNext()) 
						{
							accountId1=(Integer)itr.next();
						}
					}
							/*Query query1 = session.createQuery("select a.accountId from AssetAccountMapping a where a.serialNumber='"+SerialNumber+"' and  a.ownershipStartDate <='"+TodaysDate+"'");
					Iterator itr1=query1.list().iterator();
					
					
					
					if(query1.list().size()>0)
					{
					while(itr1.hasNext())
					{	
						AccountEntity accountEntity= (AccountEntity)itr1.next();
						accountId1=accountEntity.getAccount_id();
						 infoLogger.info("AccountEntity"+accountEntity.getAccount_id());
					}
					}
					else
					{
						Query query = session.createQuery("select a.primary_owner_id from AssetEntity a where a.serial_number='"+ SerialNumber+ "'and a.active_status=true ");
						Iterator itr = query.list().iterator();
						if (itr.hasNext()) 
						{
							accountId1=(Integer)itr.next();
						}
					}*/
					//Code changes done by Juhi on 5-December-2013 ---end
					TenancyDimensionEntity tenancyId = null;
					
					Query sqlQuery=session.createQuery("from AccountTenancyMapping where account_id="+accountId1);
					Iterator iterator = sqlQuery.list().iterator(); 
					if(iterator.hasNext())
					{//Code changes done for latest tenacy_Dimension_Id by Juhi on 6-December-2013	
					Query query3 = session.createQuery("from TenancyDimensionEntity where tenacy_Dimension_Id=(select max(tenacy_Dimension_Id) from TenancyDimensionEntity where tenancyId =(select tenancy_id from AccountTenancyMapping where account_id="+accountId1+"))" );
					Iterator itr3 = query3.list().iterator(); 
					
					if (itr3.hasNext()) {
						TenancyDimensionEntity tenancyDimensionEntity = (TenancyDimensionEntity) itr3
								.next();
						tenancyId = tenancyDimensionEntity;
						iLogger.info("tenancy_dimension_id"
								+ tenancyId);

					}
					}
					Query query2 = session.createQuery("from AccountDimensionEntity  where accountId="+accountId1);
					Iterator itr2=query2.list().iterator();
					int ten=0;
					AccountDimensionEntity accountId=null;
					while(itr2.hasNext())
					{	
						AccountDimensionEntity accountDimensionEntity= (AccountDimensionEntity)itr2.next();
						accountId=accountDimensionEntity;
						 iLogger.info("AccountDimensionEntity"+accountId.getAccountDimensionId());
					}
					
					/*	Query query3 = session.createQuery("from TenancyDimensionEntity  where tenancyId="+ten);
					Iterator itr3=query3.list().iterator();
					TenancyDimensionEntity tenancyId=null;
					while(itr3.hasNext())
					{	
						TenancyDimensionEntity tenancyDimensionEntity= (TenancyDimensionEntity)itr3.next();
						tenancyId=tenancyDimensionEntity;
						 infoLogger.info("TenancyDimensionEntity"+tenancyId.getTenancyId());
					}
					*/
					
					AssetClassDimensionEntity Asset_Class_Id =null;
					//Code change for latest asset class dimension Id DF:2013-01-07
					Query query11= session.createQuery("from AssetClassDimensionEntity b where b.assetClassDimensionId=(select max(a.assetClassDimensionId) from AssetClassDimensionEntity a where a.productId=(select productId from AssetEntity where serial_number='"+SerialNumber+"'))");
					Iterator it1=query11.list().iterator();
					while(it1.hasNext()){
						AssetClassDimensionEntity assetClassDimensionEntity =(AssetClassDimensionEntity)it1.next();
						Asset_Class_Id=assetClassDimensionEntity;
						 iLogger.info("AssetClassDimensionEntity"+Asset_Class_Id.getAssetClassDimensionId());
						}
					String queryString="select b.eventId from EventEntity b where b.eventId in(select distinct eventId from AssetEventEntity where serialNumber='"+SerialNumber+"' and eventGeneratedTime like '"+TodaysDate+"%')";
						Query query4 = session.createQuery(queryString);
						Iterator itr4=query4.list().iterator();
						List<Integer> Event_IdList=new LinkedList<Integer>();
						Object result[]=null;
						while(itr4.hasNext())
						{	
							//result= (Object[])itr4.next();
							Event_IdList.add((Integer)itr4.next());
							iLogger.info("Event_IdList size is "+Event_IdList.size());
						}
						
						List<Integer> Notification_CountList=new LinkedList<Integer>();
						//code commented on 27-jan-2013
				/*		for(int j=0;j<Event_IdList.size();j++)
						{
							int Event_Id=Event_IdList.get(j);*/
						String queryString1="select a.eventId,count(a.eventId) from AssetEventEntity a where a.serialNumber='"+SerialNumber+"' and a.eventGeneratedTime like '"+TodaysDate+"%' group by a.eventId ";
						Query query5 = session.createQuery(queryString1);
						Iterator itr5=query5.list().iterator();
						Object result1[]=null;
						
						while(itr5.hasNext())
						{	
							result1= (Object[])itr5.next();
							long notificationcount=(Long)result1[1];
							Notification_CountList.add((int)notificationcount);
							
						}
					//	}
						 iLogger.info("Notification_CountList"+Notification_CountList.size());
						String Notification_IdStringList=listToStringConversion.getIntegerListString(Event_IdList).toString();
					
						NotificationDimensionEntity notificationDimension=null;
						List<NotificationDimensionEntity> notificationDimensionList=new LinkedList<NotificationDimensionEntity>();
				       // Query query6 = session.createQuery("From NotificationDimensionEntity nde where nde.Notification_Dimension_Id in ("+Notification_IdStringList+") group by nde.Notification_Dimension_Id ");
				       //DefectId:20140411 Notification_Dimension_id @Suprava
						Query query6 = session.createQuery("From NotificationDimensionEntity nde where nde.Notification_Dimension_Id in (select max(Notification_Dimension_Id) from NotificationDimensionEntity where Notification_Id in("+Notification_IdStringList+") and LastUpdated_Date <='"+TodaysDate+"' group by Notification_Id )");
						Iterator itr6=query6.list().iterator();
						while(itr6.hasNext())
						{	
							NotificationDimensionEntity notificationDimensionEntity= (NotificationDimensionEntity)itr6.next();
							notificationDimension=notificationDimensionEntity;
							notificationDimensionList.add(notificationDimension);
						}
						
						
							for(int n=0;n<notificationDimensionList.size();n++)
							{
								NotificationFactEntity notificationFactEntity=new NotificationFactEntity();
								notificationFactEntity.setAccount_Id(accountId);
								notificationFactEntity.setAssetClass_Id(Asset_Class_Id);
								notificationFactEntity.setNotification_Id(notificationDimensionList.get(n));
								notificationFactEntity.setSerialNumber(SerialNumber);
								notificationFactEntity.setNotificationCount(Notification_CountList.get(n));
								notificationFactEntity.setTenancy_Id(tenancyId);
								notificationFactEntity.setTime_Key(Time_Key);
								session.save("NotificationFactEntity_DayAgg", notificationFactEntity);
				        
							}
						
						}
					 
				}
				else if((OlapLastDate1.compareTo(myDate))<0)
				{
					if(timestamp2!=null)
				{
					String ExistingOlapDate = dateFormat.format(OlapLastDate1);

					 Query q = session.createQuery("From NotificationFactEntity_DayAgg a where a.Time_Key like '"+ExistingOlapDate+"%'");
				
					 Iterator itr11=q.list().iterator();
				
					 while(itr11.hasNext())
						{
				   		NotificationFactEntity notificationFactEntity_DayAgg=(NotificationFactEntity)itr11.next();
				   		String Serial=notificationFactEntity_DayAgg.getSerialNumber();
				   		serList.add(Serial);
				   		NotificationDimensionEntity not_id=notificationFactEntity_DayAgg.getNotification_Id();
				   		int eventId=not_id.getNotification_Id().getEventId();
				   		String queryString1="select a.eventId,count(a.eventId) from AssetEventEntity a where a.serialNumber='"+Serial+"' and a.eventGeneratedTime like '"+ExistingOlapDate+"%' and a.eventId="+eventId+" group by a.eventId ";
						Query query5 = session.createQuery(queryString1);
						Iterator itr5=query5.list().iterator();
						Object result1[]=null;
						long notificationcount=0;
						while(itr5.hasNext())
						{	
							result1= (Object[])itr5.next();
							notificationcount=(Long)result1[1];										
						}
						notificationFactEntity_DayAgg.setNotificationCount((int)notificationcount);
						session.update("NotificationFactEntity_DayAgg", notificationFactEntity_DayAgg);
						}
					 
					 String serStringList=listToStringConversion.getStringList(serList).toString();
					 Query queryyy = session.createQuery(" select distinct a.serialNumber from AssetEventEntity a where a.serialNumber not in ("+serStringList+") and a.eventGeneratedTime like '"+ExistingOlapDate+" %'");
				        Iterator itrr=queryyy.list().iterator();
				       List<String>  Serial_NumberList=new LinkedList<String>();
						while(itrr.hasNext())
						{	
							AssetEntity a= (AssetEntity)itrr.next();
							Serial_NumberList.add(a.getSerial_number().getSerialNumber());
							 iLogger.info("Serial_Number"+a.getSerial_number().getSerialNumber());
						}
						 iLogger.info("list of asset"+Serial_NumberList.size());
						for(int i=0;i<Serial_NumberList.size();i++)
						{
							String SerialNumber=Serial_NumberList.get(i);
							Date ownershipStartDate=null;
							int accountId1=0;
							Query qry = session
							.createQuery("select max(ownershipStartDate) " 
									+ "FROM AssetAccountMapping where serialNumber='" 
									+ SerialNumber + "' " 
									+ "and  ownershipStartDate <= '" 
									+ ExistingOlapDate + "'");
				
					Iterator itr = qry.list().iterator();
					
					if (itr.hasNext()) {
						ownershipStartDate = (Date) itr.next();
						
						if (ownershipStartDate != null) {
							String owdate = dateFormat.format(ownershipStartDate);
							qry = session.createQuery("select a.accountId "
									+ "from AssetAccountMapping a ,AssetEntity c "
									+ "where a.serialNumber='"
									+ SerialNumber
									+ "'  and c.active_status=true and c.serial_number=a.serialNumber "
									+ " and a.ownershipStartDate like '" + owdate + "'");
							itr = qry.list().iterator();
							
							if (itr.hasNext()) {
								AccountEntity accountEntity= (AccountEntity)itr.next();
								accountId1=accountEntity.getAccount_id();
								 iLogger.info("AccountEntity"+accountEntity.getAccount_id());
								
							}
						}
					}
					
					if (ownershipStartDate == null)
					{
						qry = session.createQuery("select a.primary_owner_id "
								+ "from AssetEntity a "
								+ "where a.serial_number='"
								+ SerialNumber
								+ "'and a.active_status=true ");
						itr = qry.list().iterator();
						
						if (itr.hasNext()) 
						{
							accountId1=(Integer)itr.next();
						}
					}	
				/*	Query query1 = session.createQuery("select a.accountId from AssetAccountMapping a where a.serialNumber='"+SerialNumber+"' and  a.ownershipStartDate <='"+ExistingOlapDate+"'");
					Iterator itr1=query1.list().iterator();
					int accountId1=0;
					//Code changes done by Juhi on 5-December-2013 ---start
					if(query1.list().size()>0)
					{
					while(itr1.hasNext())
					{	
						AccountEntity accountEntity= (AccountEntity)itr1.next();
						accountId1=accountEntity.getAccount_id();
						 infoLogger.info("AccountEntity"+accountEntity.getAccount_id());
					}
					}
					else
					{
						Query query = session.createQuery("select a.primary_owner_id from AssetEntity a where a.serial_number='"+ SerialNumber+ "'and a.active_status=true ");
						Iterator itr = query.list().iterator();
						if (itr.hasNext()) 
						{
							accountId1=(Integer)itr.next();
						}
					}*/
					//Code changes done by Juhi on 5-December-2013 ---end
					
					TenancyDimensionEntity tenancyId = null;
					
					Query sqlQuery=session.createQuery("from AccountTenancyMapping where account_id="+accountId1);
					Iterator iterator = sqlQuery.list().iterator(); 
					if(iterator.hasNext())
					{//Code changes done for latest tenacy_Dimension_Id by Juhi on 6-December-2013	
					Query query3 = session.createQuery("from TenancyDimensionEntity where tenacy_Dimension_Id=(select max(tenacy_Dimension_Id) from TenancyDimensionEntity where tenancyId =(select tenancy_id from AccountTenancyMapping where account_id="+accountId1+"))" );
					Iterator itr3 = query3.list().iterator(); 
					
					if (itr3.hasNext()) {
						TenancyDimensionEntity tenancyDimensionEntity = (TenancyDimensionEntity) itr3
								.next();
						tenancyId = tenancyDimensionEntity;
						iLogger.info("tenancy_dimension_id"
								+ tenancyId);

					}
					}
					Query query2 = session.createQuery("from AccountDimensionEntity  where accountId="+accountId1);
					Iterator itr2=query2.list().iterator();
					int ten=0;
					AccountDimensionEntity accountId=null;
					while(itr2.hasNext())
					{	
						AccountDimensionEntity accountDimensionEntity= (AccountDimensionEntity)itr2.next();
						accountId=accountDimensionEntity;
						 iLogger.info("AccountDimensionEntity"+accountId.getAccountDimensionId());
					}
					
					/*	Query query3 = session.createQuery("from TenancyDimensionEntity  where tenancyId="+ten);
					Iterator itr3=query3.list().iterator();
					TenancyDimensionEntity tenancyId=null;
					while(itr3.hasNext())
					{	
						TenancyDimensionEntity tenancyDimensionEntity= (TenancyDimensionEntity)itr3.next();
						tenancyId=tenancyDimensionEntity;
						 infoLogger.info("TenancyDimensionEntity"+tenancyId.getTenancyId());
					}
					*/
					
					AssetClassDimensionEntity Asset_Class_Id =null;
					Query query11= session.createQuery("from AssetClassDimensionEntity b where b.assetClassDimensionId=(select max(a.assetClassDimensionId) from AssetClassDimensionEntity a where a.productId=(select productId from AssetEntity where serial_number='"+SerialNumber+"'))");
					Iterator it1=query11.list().iterator();
					while(it1.hasNext()){
						AssetClassDimensionEntity assetClassDimensionEntity =(AssetClassDimensionEntity)it1.next();
						Asset_Class_Id=assetClassDimensionEntity;
						 iLogger.info("AssetClassDimensionEntity"+Asset_Class_Id.getAssetClassDimensionId());
						}
					String queryString="select b.eventId from EventEntity b where b.eventId in(select distinct eventId from AssetEventEntity where serialNumber='"+SerialNumber+"' and eventGeneratedTime like '"+ExistingOlapDate+"%')";
						Query query4 = session.createQuery(queryString);
						Iterator itr4=query4.list().iterator();
						List<Integer> Event_IdList=new LinkedList<Integer>();
						Object result[]=null;
						while(itr4.hasNext())
						{	
							//result= (Object[])itr4.next();
							Event_IdList.add((Integer)itr4.next());
							iLogger.info("Event_IdList size is "+Event_IdList.size());
						}
						
						List<Integer> Notification_CountList=new LinkedList<Integer>();
						//code commented on 27-jan-2013
					/*	for(int j=0;j<Event_IdList.size();j++)
						{
							int Event_Id=Event_IdList.get(j);*/
						String queryString1="select a.eventId,count(a.eventId) from AssetEventEntity a where a.serialNumber='"+SerialNumber+"' and a.eventGeneratedTime like '"+ExistingOlapDate+"%' group by a.eventId ";
						Query query5 = session.createQuery(queryString1);
						Iterator itr5=query5.list().iterator();
						Object result1[]=null;
						
						while(itr5.hasNext())
						{	
							result1= (Object[])itr5.next();
							long notificationcount=(Long)result1[1];
							Notification_CountList.add((int)notificationcount);
							
						}
				//		}
						 iLogger.info("Notification_CountList"+Notification_CountList.size());
						String Notification_IdStringList=listToStringConversion.getIntegerListString(Event_IdList).toString();
					
						NotificationDimensionEntity notificationDimension=null;
						List<NotificationDimensionEntity> notificationDimensionList=new LinkedList<NotificationDimensionEntity>();
				        //Query query6 = session.createQuery("From NotificationDimensionEntity nde where nde.Notification_Dimension_Id in ("+Notification_IdStringList+") group by nde.Notification_Dimension_Id ");
						//DefectId:20140411 Notification_Dimension_id @Suprava
						Query query6 = session.createQuery("From NotificationDimensionEntity nde where nde.Notification_Dimension_Id in (select max(Notification_Dimension_Id) from NotificationDimensionEntity where Notification_Id in("+Notification_IdStringList+") and LastUpdated_Date <='"+TodaysDate+"' group by Notification_Id )");
						Iterator itr6=query6.list().iterator();
						while(itr6.hasNext())
						{	
							NotificationDimensionEntity notificationDimensionEntity= (NotificationDimensionEntity)itr6.next();
							notificationDimension=notificationDimensionEntity;
							notificationDimensionList.add(notificationDimension);
						}
						
						
							for(int n=0;n<notificationDimensionList.size();n++)
							{
								NotificationFactEntity notificationFactEntity=new NotificationFactEntity();
								notificationFactEntity.setAccount_Id(accountId);
								notificationFactEntity.setAssetClass_Id(Asset_Class_Id);
								notificationFactEntity.setNotification_Id(notificationDimensionList.get(n));
								notificationFactEntity.setSerialNumber(SerialNumber);
								notificationFactEntity.setNotificationCount(Notification_CountList.get(n));
								notificationFactEntity.setTenancy_Id(tenancyId);
								notificationFactEntity.setTime_Key(Time_Key);
								session.save("NotificationFactEntity_DayAgg", notificationFactEntity);
				        
							}
						
						}
					 
				
					
				}
				  	//update existing
			   	   	 calendar.setTime(OlapLastDate1);
			   	   	 calendar.add(Calendar.DAY_OF_YEAR,1);
			   	   	 Date newOlapDate = calendar.getTime();
			   	  iLogger.info("check for next date"+newOlapDate);
			   	   	 String newDate= dateFormat.format(newOlapDate);
			   	   	 String TimekeyValue= dateFormat1.format(newOlapDate);
			   	   	 Time_Key = Timestamp.valueOf(TimekeyValue);
			   	   	 do
			   	   	 {	  OlapLastDate1=dateFormat.parse(TimekeyValue);
			   	  Query queryyy = session.createQuery(" select distinct a.serialNumber from AssetEventEntity a where a.eventGeneratedTime like '"+newDate+" %'");
			        Iterator itrr=queryyy.list().iterator();
			       List<String>  Serial_NumberList=new LinkedList<String>();
					while(itrr.hasNext())
					{	
						AssetEntity a= (AssetEntity)itrr.next();
						Serial_NumberList.add(a.getSerial_number().getSerialNumber());
						 iLogger.info("Serial_Number"+a.getSerial_number().getSerialNumber());
					}
					 iLogger.info("list of asset"+Serial_NumberList.size());
					for(int i=0;i<Serial_NumberList.size();i++)
					{
						String SerialNumber=Serial_NumberList.get(i);
							//Code changes done by Juhi on 4-December-2013	
						Date ownershipStartDate=null;
						int accountId1=0;
						Query qry = session
						.createQuery("select max(ownershipStartDate) " 
								+ "FROM AssetAccountMapping where serialNumber='" 
								+ SerialNumber + "' " 
								+ "and  ownershipStartDate <= '" 
								+ newDate + "'");
			
				Iterator itr = qry.list().iterator();
				
				if (itr.hasNext()) {
					ownershipStartDate = (Date) itr.next();
					
					if (ownershipStartDate != null) {
						String owdate = dateFormat.format(ownershipStartDate);
						qry = session.createQuery("select a.accountId "
								+ "from AssetAccountMapping a ,AssetEntity c "
								+ "where a.serialNumber='"
								+ SerialNumber
								+ "'  and c.active_status=true and c.serial_number=a.serialNumber "
								+ " and a.ownershipStartDate like '" + owdate + "'");
						itr = qry.list().iterator();
						
						if (itr.hasNext()) {
							AccountEntity accountEntity= (AccountEntity)itr.next();
							accountId1=accountEntity.getAccount_id();
							 iLogger.info("AccountEntity"+accountEntity.getAccount_id());
							
						}
					}
				}
				
				if (ownershipStartDate == null)
				{
					qry = session.createQuery("select a.primary_owner_id "
							+ "from AssetEntity a "
							+ "where a.serial_number='"
							+ SerialNumber
							+ "'and a.active_status=true ");
					itr = qry.list().iterator();
					
					if (itr.hasNext()) 
					{
						accountId1=(Integer)itr.next();
					}
				}
				/*Query query1 = session.createQuery("select a.accountId from AssetAccountMapping a where a.serialNumber='"+SerialNumber+"' and  a.ownershipStartDate <='"+newDate+"'");
				Iterator itr1=query1.list().iterator();
				int accountId1=0;
				//Code changes done by Juhi on 5-December-2013 ---start
				if(query1.list().size()>0)
				{
				while(itr1.hasNext())
				{	
					AccountEntity accountEntity= (AccountEntity)itr1.next();
					accountId1=accountEntity.getAccount_id();
					 infoLogger.info("AccountEntity"+accountEntity.getAccount_id());
				}
				}
				else
				{
					Query query = session.createQuery("select a.primary_owner_id from AssetEntity a where a.serial_number='"+ SerialNumber+ "'and a.active_status=true ");
					Iterator itr = query.list().iterator();
					if (itr.hasNext()) 
					{
						accountId1=(Integer)itr.next();
					}
				}*/
				//Code changes done by Juhi on 5-December-2013 ---end
				TenancyDimensionEntity tenancyId = null;
				
				Query sqlQuery=session.createQuery("from AccountTenancyMapping where account_id="+accountId1);
				Iterator iterator = sqlQuery.list().iterator(); 
				if(iterator.hasNext())
				{
					//Code changes done for latest tenacy_Dimension_Id by Juhi on 6-December-2013	
				Query query3 = session.createQuery("from TenancyDimensionEntity where tenacy_Dimension_Id=(select max(tenacy_Dimension_Id) from TenancyDimensionEntity where tenancyId =(select tenancy_id from AccountTenancyMapping where account_id="+accountId1+"))" );
				Iterator itr3 = query3.list().iterator(); 
				
				if (itr3.hasNext()) {
					TenancyDimensionEntity tenancyDimensionEntity = (TenancyDimensionEntity) itr3
							.next();
					tenancyId = tenancyDimensionEntity;
					iLogger.info("tenancy_dimension_id"
							+ tenancyId);

				}
				}
				Query query2 = session.createQuery("from AccountDimensionEntity  where accountId="+accountId1);
				Iterator itr2=query2.list().iterator();
				int ten=0;
				AccountDimensionEntity accountId=null;
				while(itr2.hasNext())
				{	
					AccountDimensionEntity accountDimensionEntity= (AccountDimensionEntity)itr2.next();
					accountId=accountDimensionEntity;
					 iLogger.info("AccountDimensionEntity"+accountId.getAccountDimensionId());
				}
			/*	Query query2 = session.createQuery("from AccountDimensionEntity  where accountId="+accountId1);
				Iterator itr2=query2.list().iterator();
				int ten=0;
				AccountDimensionEntity accountId=null;
				while(itr2.hasNext())
				{	
					AccountDimensionEntity accountDimensionEntity= (AccountDimensionEntity)itr2.next();
					ten=accountDimensionEntity.getTenancyId();
					accountId=accountDimensionEntity;
					 infoLogger.info("AccountDimensionEntity"+accountId.getAccountDimensionId());
				}
				Query query3 = session.createQuery("from TenancyDimensionEntity  where tenancyId="+ten);
				Iterator itr3=query3.list().iterator();
				TenancyDimensionEntity tenancyId=null;
				while(itr3.hasNext())
				{	
					TenancyDimensionEntity tenancyDimensionEntity= (TenancyDimensionEntity)itr3.next();
					tenancyId=tenancyDimensionEntity;
					 infoLogger.info("TenancyDimensionEntity"+tenancyId.getTenancyId());
				}
				*/
				
				AssetClassDimensionEntity Asset_Class_Id =null;
				Query query11= session.createQuery("from AssetClassDimensionEntity b where b.assetClassDimensionId=(select max(a.assetClassDimensionId) from AssetClassDimensionEntity a where a.productId=(select productId from AssetEntity where serial_number='"+SerialNumber+"'))");
				Iterator itr11=query11.list().iterator();
				while(itr11.hasNext()){
					AssetClassDimensionEntity assetClassDimensionEntity =(AssetClassDimensionEntity)itr11.next();
					Asset_Class_Id=assetClassDimensionEntity;
					 iLogger.info("AssetClassDimensionEntity"+Asset_Class_Id.getAssetClassDimensionId());
					}
				String queryString="select b.eventId from EventEntity b where b.eventId in(select distinct eventId from AssetEventEntity where serialNumber='"+SerialNumber+"' and eventGeneratedTime like '"+newDate+"%')";
					Query query4 = session.createQuery(queryString);
					Iterator itr4=query4.list().iterator();
					List<Integer> Event_IdList=new LinkedList<Integer>();
					Object result[]=null;
					while(itr4.hasNext())
					{	
						//result= (Object[])itr4.next();
						Event_IdList.add((Integer)itr4.next());		
						iLogger.info("Event_IdList size is "+Event_IdList.size());
				    }
					
					List<Integer> Notification_CountList=new LinkedList<Integer>();
					//code commented on 27-jan-2013
					/*for(int j=0;j<Event_IdList.size();j++)
					{
						int Event_Id=Event_IdList.get(j);*/
					String queryString1="select a.eventId,count(a.eventId) from AssetEventEntity a where a.serialNumber='"+SerialNumber+"' and a.eventGeneratedTime like '"+newDate+"%' group by a.eventId ";
					Query query5 = session.createQuery(queryString1);
					Iterator itr5=query5.list().iterator();
					Object result1[]=null;
					
					while(itr5.hasNext())
					{	
						result1= (Object[])itr5.next();
						long notificationcount=(Long)result1[1];
						Notification_CountList.add((int)notificationcount);
						
					}
					//}
					 iLogger.info("Notification_CountList"+Notification_CountList.size());
					String Notification_IdStringList=listToStringConversion.getIntegerListString(Event_IdList).toString();
				
					NotificationDimensionEntity notificationDimension=null;
					List<NotificationDimensionEntity> notificationDimensionList=new LinkedList<NotificationDimensionEntity>();
			        //Query query6 = session.createQuery("From NotificationDimensionEntity nde where nde.Notification_Dimension_Id in ("+Notification_IdStringList+") group by nde.Notification_Dimension_Id ");
					//DefectId:20140411 Notification_Dimension_id @Suprava
					Query query6 = session.createQuery("From NotificationDimensionEntity nde where nde.Notification_Dimension_Id in (select max(Notification_Dimension_Id) from NotificationDimensionEntity where Notification_Id in("+Notification_IdStringList+") and LastUpdated_Date <='"+TodaysDate+"' group by Notification_Id )");
					Iterator itr6=query6.list().iterator();
					while(itr6.hasNext())
					{	
						NotificationDimensionEntity notificationDimensionEntity= (NotificationDimensionEntity)itr6.next();
						notificationDimension=notificationDimensionEntity;
						notificationDimensionList.add(notificationDimension);
					}
					
					
						for(int n=0;n<notificationDimensionList.size();n++)
						{
							NotificationFactEntity notificationFactEntity=new NotificationFactEntity();
							notificationFactEntity.setAccount_Id(accountId);
							notificationFactEntity.setAssetClass_Id(Asset_Class_Id);
							notificationFactEntity.setNotification_Id(notificationDimensionList.get(n));
							notificationFactEntity.setSerialNumber(SerialNumber);
							notificationFactEntity.setNotificationCount(Notification_CountList.get(n));
							notificationFactEntity.setTenancy_Id(tenancyId);
							notificationFactEntity.setTime_Key(Time_Key);
							session.save("NotificationFactEntity_DayAgg", notificationFactEntity);
			        
						}
					
					} 
					calendar.setTime(OlapLastDate1);
			   	   	 calendar.add(Calendar.DAY_OF_YEAR,1);
			   	   	  newOlapDate = calendar.getTime();
			   	   iLogger.info("check for next date"+newOlapDate);
			   	   	 newDate= dateFormat.format(newOlapDate);
			   	   	  TimekeyValue= dateFormat1.format(newOlapDate);
			   	   	 Time_Key = Timestamp.valueOf(TimekeyValue);
					
			   	   	 
				}while((OlapLastDate1.compareTo(myDate))<0);// end of while loop 
				}//end of else if
				}//end of if olap data exists before				 
				//Asset Class Id Updation on fact table Done on 2013-06-05
				if(timestamp2!=null)
				{
					Query query09 = session.createQuery("select distinct(SerialNumber) From NotificationFactEntity_DayAgg");
					Iterator iterator = query09.list().iterator();
					String OlapSerialNumber=null;
					while (iterator.hasNext())
					{
						OlapSerialNumber = (String) iterator.next();
						//Code change for latest asset class dimension Id DF:2013-01-07
						Query query11 = session
						.createQuery("from AssetClassDimensionEntity b where b.assetClassDimensionId=(select max(a.assetClassDimensionId) from AssetClassDimensionEntity a where a.productId=(select productId from AssetEntity where serial_number='"+OlapSerialNumber+"'))");
						AssetClassDimensionEntity assetClassDimensionId=null;
						Iterator itr11 = query11.list().iterator();
						while (itr11.hasNext()) 
						{
							AssetClassDimensionEntity assetClassDimensionEntity = (AssetClassDimensionEntity) itr11
							.next();
							assetClassDimensionId=assetClassDimensionEntity;
						}
						Query q = session
						.createQuery("From NotificationFactEntity_DayAgg a where a.SerialNumber= '"	+ OlapSerialNumber + "'");
						Iterator itr12 = q.list().iterator();
						while (itr12.hasNext()) 
						{

							NotificationFactEntity notificationFactEntity = (NotificationFactEntity) itr12.next();
							notificationFactEntity.setAssetClass_Id(assetClassDimensionId);
							session.update("NotificationFactEntity_DayAgg",notificationFactEntity);

							
						}
						
					}
				
				}
	        	
	        	
	      
	}
	 catch (Exception e)
	     {
		 fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());
	     		           e.printStackTrace();
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
	        return "SUCCESS";
		
	}
	
	//******************************************End of setNotificationFactData************************************************************
	//****************************************** Code for updation ************************************
	public void updateTable(String queryString,String tableName)
	{
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		try 
		{
			Query query = session.createQuery(queryString);
			Iterator itr = query.list().iterator();
			int exist = 0;
			while (itr.hasNext())
			{
				exist = (Integer) itr.next();
			}
			//If there exist record in Olap Fact Table, then Update AssetClassDimensionId
			if(exist!=0)
			{
				
				AssetClassDimensionEntity assetClassDimensionId1=null;
				Query query09 = session.createQuery("select distinct(SerialNumber) From "+tableName+" where AssetClass_Id ="+assetClassDimensionId1+" ");
				Iterator iterator = query09.list().iterator();
				String OlapSerialNumber=null;
				while (iterator.hasNext())
				{
					OlapSerialNumber = (String) iterator.next();
					int flag=0;
					Query query12 = session.createQuery("select productId from AssetEntity where serial_number='"
							+ OlapSerialNumber + "'");
					ProductEntity product=null;
					Iterator itr122= query12.list().iterator();
					if (itr122.hasNext()) 
					{
							product = (ProductEntity) itr122.next();
					flag=1;
					}
					if(flag==1)
					{
						//Code change for latest asset class dimension Id DF:2013-01-07
						Query query11 = session
						.createQuery("from AssetClassDimensionEntity b where b.assetClassDimensionId=(select max(a.assetClassDimensionId) from AssetClassDimensionEntity a where a.productId=(select productId from AssetEntity where serial_number='"+OlapSerialNumber+"'))");
						AssetClassDimensionEntity assetClassDimensionId=null;
						Iterator itr11 = query11.list().iterator();
						while (itr11.hasNext()) 
						{
							AssetClassDimensionEntity assetClassDimensionEntity = (AssetClassDimensionEntity) itr11
							.next();
							assetClassDimensionId=assetClassDimensionEntity;
						}
			
							Query updateQuery = session.createQuery("UPDATE "+ tableName+" SET AssetClass_Id="+assetClassDimensionId.getAssetClassDimensionId()+"" +
									" WHERE SerialNumber= '" + OlapSerialNumber + "'");
									
							iLogger.info("query for updation :" + updateQuery);
					int rows = updateQuery.executeUpdate();
					
					}
						}
			
			
			}
					
		} catch (Exception e) {
		fLogger.fatal("In Database ,value are not there "
				+ e.getMessage());
		e.printStackTrace();
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
		
	}
	//****************************************** Code for updation ************************************
	
	//**************************************Start of  UpdateNotificationFact  *********************************************************
	
/**
 * This Method will allow to Update AssetClassDimensionId in Fact Tables.
 * @return Success if updated Successfully
 */
	public String updateNotificationFact() {
		// Update AssetClassDimensionId for Week ,Month, Quarter,Year Fact Table
		updateTable("select max(TimeCount) From notification_fact_weekagg","notification_fact_weekagg");
		updateTable("select max(TimeCount) From notification_fact_Monthagg","notification_fact_Monthagg");
		updateTable("select max(TimeCount) From notification_fact_Quarteragg","notification_fact_Quarteragg");
		updateTable("select max(Year) From notification_fact_Yearagg","notification_fact_Yearagg");

		return "Success";
		
	}
	//**************************************End of  UpdateNotificationFact  *********************************************************
	//**************************************Start of  updateTenancyIdInFactTables  *********************************************************
	/**
	 * This Method will allow to Update TenancyId in Fact Tables.
	 * @return Success if updated Successfully
	 */
	public String updateTenancyIdInFactTables() {
		// Update TenancyId for Week ,Month, Quarter,Year Fact Table
		updateTenancyId("select SerialNumber From notification_fact_weekagg","notification_fact_weekagg");
		updateTenancyId("select SerialNumber From notification_fact_Monthagg","notification_fact_Monthagg");
		updateTenancyId("select SerialNumber From notification_fact_Monthagg","notification_fact_Monthagg");
		updateTenancyId("select SerialNumber From notification_fact_Yearagg","notification_fact_Yearagg");

		return "Success";
		
	}
	
	public void updateTenancyId(String queryString,String tableName)
	{
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try 
		{Query query = session.createQuery(queryString);
		Iterator itr = query.list().iterator();
		String exist = null;
		while (itr.hasNext())
		{
			exist = (String) itr.next();
		}
		//If there exist record in Olap Fact Table, then Update Tenancy_ID
		
		if(exist!=null)
		{
			TenancyDimensionEntity tenancyId=null;
			Query query09 = session.createQuery("select a.SerialNumber,a.Account_Id,b.tenancy_id From "+tableName+" a,AccountTenancyMapping b " +
					" where IFNULL( a.Tenancy_Id,0)=0 and NOT IFNULL( b.tenancy_id,0)=0  ");
			Iterator iterator = query09.list().iterator();
			Object result[]=null;
			String OlapSerialNumber=null;
			int Account_Id=0;
			AccountDimensionEntity accountDimension=null;
			while (iterator.hasNext())
			{
				TenancyEntity tenancy=null;
				result=(Object[]) iterator.next();
				OlapSerialNumber = (String)result[0];
				accountDimension=((AccountDimensionEntity)result[1]);
				Account_Id=accountDimension.getAccountId();

				tenancy=(TenancyEntity)result[2];
					
					Query query11 = session
					.createQuery("from TenancyDimensionEntity a where a.tenancyId="+tenancy.getTenancy_id());
					TenancyDimensionEntity tenancyDimensionId=null;
					Iterator itr11 = query11.list().iterator();
					while (itr11.hasNext()) 
					{
						TenancyDimensionEntity tenancyDimensionEntity = (TenancyDimensionEntity) itr11.next();
						tenancyDimensionId=tenancyDimensionEntity;
					}
		
						Query updateQuery = session.createQuery("UPDATE "+tableName+" SET Tenancy_Id="+tenancyDimensionId.getTenacy_Dimension_Id()+
								" WHERE Account_Id= " + accountDimension.getAccountDimensionId()  + " and SerialNumber= '" + OlapSerialNumber + "'");
								
						iLogger.info("query for updation :" + updateQuery);
				int rows = updateQuery.executeUpdate();
				
				
					}
			}
		} catch (Exception e) {
		fLogger.fatal("In Database ,value are not there "
				+ e.getMessage());
		e.printStackTrace();
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
		
	
	}
	//**************************************End of  updateTenancyIdInFactTables  *********************************************************
}
