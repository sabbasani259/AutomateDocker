/**
 * CR308 : 20220613 : Dhiraj K : Code Fix for BW service closures from Portal
 * CR337 : 20220721 : Dhiraj K : Property file read.
 */
package remote.wise.businessobject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Collections;
import java.util.Comparator;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.JDBCConnectionException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/*import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.wipro.mcoreapp.util.OrientAppDbDatasource;*/

import redis.clients.jedis.Jedis;
import remote.wise.businessentity.*;
import remote.wise.dal.DynamicAMH_DAL;
import remote.wise.dal.DynamicAMS_DAL;
import remote.wise.dal.DynamicAMS_Doc_DAL;
import remote.wise.exception.CustomFault;
import remote.wise.handler.EmailHandler;
import remote.wise.handler.EmailTemplate;
import remote.wise.handler.NotificationHandler;
import remote.wise.handler.SmsHandler;
import remote.wise.handler.SmsTemplate;
import remote.wise.handler.UserAlertSubscription;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.AMSDoc_DAO;
import remote.wise.pojo.AmsDAO;
import remote.wise.pojo.AssetMonitoringParametersDAO;
import remote.wise.service.datacontract.AdminAlertPrefRespContract;
import remote.wise.service.datacontract.AlertSummaryRespContract;
import remote.wise.service.datacontract.AlertThresholdRespContract;
import remote.wise.service.datacontract.LocationDetails;
import remote.wise.service.datacontract.LocationDetailsMMI;
import remote.wise.service.datacontract.ServiceDueOverDueRespContract;
import remote.wise.service.implementation.AdminAlertPrefImpl;
import remote.wise.service.implementation.AlertDetailsImpl;
import remote.wise.service.implementation.AlertThresholdImpl;
import remote.wise.service.implementation.DomainServiceImpl;
import remote.wise.service.implementation.EventSubscriptionImpl;
import remote.wise.service.implementation.FleetSummaryImpl;
import remote.wise.service.implementation.NotificationSummaryImpl;
import remote.wise.service.implementation.ServiceDueOverDueImpl;
import remote.wise.service.implementation.UserAlertsImpl;
import remote.wise.util.AssetUtil;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;
import remote.wise.util.GeocodingLibrary;
import remote.wise.util.GetSetLocationJedis;
import remote.wise.util.GmtLtTimeConversion;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
import remote.wise.util.LocationByLatLon;
import remote.wise.util.StaticProperties;
//import remote.wise.util.WiseLogger;

////import org.apache.log4j.Logger;
public class EventDetailsBO extends BaseBusinessObject 
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("EventDetailsBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("EventDetailsBO:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("EventDetailsBO:","info");*/
	
	
	String serialNumber;
	String eventTypeName;
	String eventDescription;
	Timestamp eventGeneratedTime;
	String eventSeverity;
	Long eventCounter;
	
	
	public String getSerialNumber() {
		return serialNumber;
	}


	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}


	public String getEventTypeName() {
		return eventTypeName;
	}


	public void setEventTypeName(String eventTypeName) {
		this.eventTypeName = eventTypeName;
	}


	public String getEventDescription() {
		return eventDescription;
	}


	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}


	public Timestamp getEventGeneratedTime() {
		return eventGeneratedTime;
	}


	public void setEventGeneratedTime(Timestamp eventGeneratedTime) {
		this.eventGeneratedTime = eventGeneratedTime;
	}


	public String getEventSeverity() {
		return eventSeverity;
	}


	public void setEventSeverity(String eventSeverity) {
		this.eventSeverity = eventSeverity;
	}


	public Long getEventCounter() {
		return eventCounter;
	}


	public void setEventCounter(Long eventCounter) {
		this.eventCounter = eventCounter;
	}


	//************************************ get Event Type Entity for the given Event type Id ****************************
	public EventTypeEntity getEventTypeDetails(int eventTypeId)
	{
		EventTypeEntity eventTypeEntityObj=new EventTypeEntity(eventTypeId);
		if(eventTypeEntityObj!=null && eventTypeEntityObj.getEventTypeId()!=0)
			return eventTypeEntityObj;
		else
			return null;
	}
	//************************************ END of get Event Type Entity for the given Event type Id ****************************
	
	
	//******************************get all the notifications sent for the specified user - the active notifications********************
	public void getUserEvents(String contactId, String serialNumber, List<Integer> eventTypeIdList, List<String> eventSeverityList) throws CustomFault
	{
		DomainServiceImpl domainService = new DomainServiceImpl();
		String queryString;
		 //Logger businessError = Logger.getLogger("businessErrorLogger");
	      //  Logger fatalError = Logger.getLogger("fatalErrorLogger");
		
    	Logger fLogger = FatalLoggerClass.logger;
    	
	        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	        session.beginTransaction();
		try{
		//validate userId
		ContactEntity contact = domainService.getContactDetails(contactId);
		if(contact==null || contact.getContact_id()==null)
		{
			throw new CustomFault("Invalid LoginId");
		}
		
		//basic query if no filters is selected
		//DF20190204 @abhishek---->add partition key for faster retrieval
		queryString = "select a.serialNumber,b.eventTypeName, b.eventDescription,a.eventGeneratedTime,a.eventSeverity,count(*) as eventCount" +
					  " from EventUsersEntity c"+
					  " RIGHT OUTER JOIN c.assetEventId a"+
					  " INNER JOIN a.eventId b " +
					  "where a.activeStatus=1 and a.partitionKey =1 and c.contactId='"+contactId+"'"+
					  "group by a.serialNumber,b.eventTypeName, b.eventDescription";
		
		
		Object[] result=null;
		
		Query query = session.createQuery(queryString);
		Iterator itr = query.list().iterator();
		
		while(itr.hasNext())
		{
			result = (Object[]) itr.next();
			EventDetailsBO eventDetails = new EventDetailsBO();
			AssetEntity asset = (AssetEntity)result[0];
			eventDetails.setSerialNumber(asset.getSerial_number().getSerialNumber());
			eventDetails.setEventTypeName(result[1].toString());
			eventDetails.setEventDescription(result[2].toString());
			eventDetails.setEventGeneratedTime((Timestamp)result[3]);
			eventDetails.setEventSeverity(result[4].toString());
			eventDetails.setEventCounter((Long)result[5]);
			
		}}catch(Exception e)
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
		
	}
	
	//******************************END of get all the notifications sent for the specified user - the active notifications********************
	
	
	//****************************************** SET Primary SMS contact for a machine *****************************************************
	/** This method sets the SMS subscribers for the given serialNumber
	 * DefectId:950,1015 - Modified by Rajani Nagaraju - Receiver1,2 swapping issue
	 * @param serialNumber AssetEntity for the given serialNumber
	 * @param primaryContactId contactId list for SMS subscribers
	 * @return Returns the status string
	 * @throws CustomFault
	 */
	public String setEventSubscription(AssetEntity serialNumber,List<String> primaryContactId)
	{
		String status = "SUCCESS";
		
        //Logger fatalError = Logger.getLogger("fatalErrorLogger");
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Logger iLogger = InfoLoggerClass.logger;
      //DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
        if(session.getTransaction().isActive() && session.isDirty())
        {
           	iLogger.info("Opening a new session");
           	session = HibernateUtil.getSessionFactory().openSession();
        }
      session.beginTransaction();
        Logger fLogger = FatalLoggerClass.logger;
    	
		
		try
		{
			//primary contacts for SMS can be only maximum of three
			Query query2 = session.createQuery("delete from EventSubscriptionMapping where serialNumber='"+serialNumber.getSerial_number().getSerialNumber()+"'");
			int rows = query2.executeUpdate();
			iLogger.info("No of rows deleted : "+rows);		
			
		
			int index=0;
			
	
			while(index < primaryContactId.size())
			{
				//New Change: primaryContactId is a list of string in the following format: userId,priority
				//DefectId:950,1015 - Modified by Rajani Nagaraju - Receiver1,2 swapping issue
				String[] contactPriority = primaryContactId.get(index).split(",");
				
				EventSubscriptionMapping newEventSubscriber = new EventSubscriptionMapping();
				//ContactEntity contact = new ContactEntity(primaryContactId.get(index));
				//DF20150113 - Rajani Nagaraju - Adding .trim(), since for ex., 'aae00044   ' was getting set as contact in event subscription 
				// and throws NonUniqueObjectException for AssetEventContact during alert generation
				ContactEntity contact = new ContactEntity(contactPriority[0].trim());
				
				newEventSubscriber.setContactId(contact);
				newEventSubscriber.setSerialNumber(serialNumber);
				//newEventSubscriber.setPriority(index+1);
				if(contactPriority.length>1)
					newEventSubscriber.setPriority(Integer.valueOf(contactPriority[1]));
				
				session.save(newEventSubscriber);
				index++;
			}
		}
		
			
		catch(Exception e)
		{
			status = "FAILURE";
			fLogger.fatal("Exception :"+e);
			e.printStackTrace();
		}
        
        finally
        {
            try
            {
        	if(session.getTransaction().isActive())
              {
        		session.flush();   
        		session.getTransaction().commit();
              }
            }
            
            catch(Exception e)
            {
            	//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
            	fLogger.fatal("Exception in commiting the record:"+e);
            }
              if(session.isOpen())
              {
                 
                    session.close();
              }
              
        }
		
		return status;
		
	}
	//****************************************** End of SET Primary SMS contact for a machine ***********************************************
	
	//****************************************** SET Primary SMS contact for a machine *****************************************************
	/** This method sets the SMS subscribers for the given serialNumber
	 * @param serialNumber AssetEntity for the given serialNumber
	 * @param primaryContactId contactId list for SMS subscribers
	 * @return Returns the status string
	 * @throws CustomFault
	 */
	public String setEventSubscription2(AssetEntity serialNumber,List<ContactEntity> primaryContactId) throws CustomFault
	{
		String status = "SUCCESS";
		
		//Logger businessError = Logger.getLogger("businessErrorLogger");
       // Logger fatalError = Logger.getLogger("fatalErrorLogger");
		
    	Logger fLogger = FatalLoggerClass.logger;
    	
    	Logger bLogger = BusinessErrorLoggerClass.logger;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
		
		try
		{
			//primary contacts for SMS can be only maximum of three
			int primaryContactListSize = primaryContactId.size();
			if(primaryContactListSize>3)
			{
				throw new CustomFault("Maximum of three users can be assigned as Primary contact for SMS");
			}
			
			HashMap<String,ContactEntity> contactIdEntityMap = new HashMap<String,ContactEntity>();
			
			Query query = session.createQuery("from EventSubscriptionMapping where serialNumber='"+serialNumber.getSerial_number().getSerialNumber()+"'");
			Iterator itr = query.list().iterator();
			
			//get the list of primaryContactId String since retainAll, removeAll requires the comparison of data and not the objects
			List<String> primaryContact = new LinkedList<String>();
			for(int y=0; y<primaryContactId.size(); y++)
			{
				primaryContact.add(primaryContactId.get(y).getContact_id());
				contactIdEntityMap.put(primaryContactId.get(y).getContact_id(), primaryContactId.get(y));
			}
			
			List<ContactEntity> contactEntityList = new LinkedList<ContactEntity>();
			List<String> contactEntity = new LinkedList<String>();
					
			while(itr.hasNext())
			{
				EventSubscriptionMapping eventSubscription = (EventSubscriptionMapping) itr.next();
				contactEntityList.add(eventSubscription.getContactId());
				contactEntity.add(eventSubscription.getContactId().getContact_id());
			}
			
			
			List<String> oldSubscribers = new LinkedList<String>();
			oldSubscribers.addAll(primaryContact);
			
					
			if( ! ( contactEntity==null || contactEntity.isEmpty()) )
			{
				oldSubscribers.retainAll(contactEntity);
						
				//remove the contacts to be retained
				primaryContact.removeAll(oldSubscribers);
				contactEntity.removeAll(oldSubscribers);
				
			}
			int index=0;
			int i=0;
			
			for(i=0; i<contactEntity.size(); i++)
			{
				if(index <= i && primaryContact.size()>1)
				{
					Query query1 = session.createQuery("update EventSubscriptionMapping set contactId='"+primaryContact.get(index)+"' where " +
							"serialNumber='"+serialNumber.getSerial_number().getSerialNumber()+"' and contactId='"+contactEntity.get(i)+"'");
					query1.executeUpdate();
					index++;
					     
				}
				else
					break;
			}
			
			while(i < contactEntity.size())
			{
				Query query2 = session.createQuery("delete from EventSubscriptionMapping where contactId='"+contactEntity.get(i)+
													"' and serialNumber='"+serialNumber.getSerial_number().getSerialNumber()+"'");
				query2.executeUpdate();
				i++;
			}
	
			while(index < primaryContact.size())
			{
				EventSubscriptionMapping newEventSubscriber = new EventSubscriptionMapping();
				ContactEntity contact = contactIdEntityMap.get(primaryContact.get(index));
				
				newEventSubscriber.setContactId(contact);
				newEventSubscriber.setSerialNumber(serialNumber);
				session.save(newEventSubscriber);
				index++;
			}
		}
		
		catch(CustomFault e)
		{
			status = "FAILURE";
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		catch(Exception e)
		{
			status = "FAILURE";
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
		
		return status;
		
	}
	//****************************************** End of SET Primary SMS contact for a machine ***********************************************
	
	
	
	/** This method returns the SMS subscribers for the given serialNumber
	 * Defect Id: 902 - Rajani Nagaraju - 20130705 - DealerUser as first PrimarySMS contact.
	 * @param serialNumber serialNumber as String input
	 * @return Returns the SMS subscriber contacts
	 */
	public List<EventSubscriptionImpl> getEventSubscribers(String serialNumber)
	{
		List<EventSubscriptionImpl> eventSubscriberList = new LinkedList<EventSubscriptionImpl>();
		
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		
    	Logger fLogger = FatalLoggerClass.logger;
    	
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
		try
		{
			//Defect Id: 902 - Rajani Nagaraju - 20130705 - DealerUser as first PrimarySMS contact.
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			String dealerTenancyType = prop.getProperty("DealerTenancyType");
			
			//DF20140327 - Rajani Nagaraju - Order by priority
			Query query = session.createQuery("select a.contactId,a.priority from EventSubscriptionMapping a where a.serialNumber='"+serialNumber+"' order by a.priority");
			Iterator itr = query.list().iterator();
			Object result[]=null;
			EventSubscriptionImpl eventSubscriber  = null;
			ContactEntity contact = null;
			while(itr.hasNext())
			{
				result = (Object[])itr.next();
				eventSubscriber = new EventSubscriptionImpl();
				if(result[0]!=null){
					contact = (ContactEntity) result[0];
					if(contact != null){
						eventSubscriber.setContactId(contact.getContact_id());
						eventSubscriber.setPrimaryEmailId(contact.getPrimary_email_id());
						eventSubscriber.setPrimaryMobileNumber(contact.getPrimary_mobile_number());
						eventSubscriber.setSerialNumber(serialNumber);
						String firstName = contact.getFirst_name();
						if(firstName==null)
							firstName="";
						String lastName = contact.getLast_name();
						if(lastName==null)
							lastName="";
						eventSubscriber.setUserName(firstName+" "+lastName);
					}				
				}
				
				if(result[1]!=null){
					eventSubscriber.setPriority((Integer)result[1]);
				}
				//Defect Id: 902 - Rajani Nagaraju - 20130705 - DealerUser as first PrimarySMS contact.
				//Find the tenancyType of the User - To find whether he is a dealer user
				Query userTenancyQuery = session.createQuery(" select b.tenancy_id from AccountContactMapping a, AccountTenancyMapping b " +
						" where a.account_id = b.account_id and a.contact_id='"+contact.getContact_id()+"' ");
				Iterator userTenancyItr = userTenancyQuery.list().iterator();
				while(userTenancyItr.hasNext())
				{
					TenancyEntity userTenancy = (TenancyEntity)userTenancyItr.next();
					
					if(userTenancy.getTenancy_type_id().getTenancy_type_name().equalsIgnoreCase(dealerTenancyType))
					{
						eventSubscriber.setDealerUser(true);
					}
				}
				
				eventSubscriberList.add(eventSubscriber);
			}
		}
		
		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
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
		
		
		
		return eventSubscriberList;
	}
	
		
	//********************************************************** End of Get primary SMS contact details***************************************
	
	/** This method returns the SMS subscribers for the given serialNumber
	 * Defect Id: 902 - Rajani Nagaraju - 20130705 - DealerUser as first PrimarySMS contact.
	 * @param serialNumber serialNumber as String input
	 * @return Returns the SMS subscriber contacts
	 */
	public List<EventSubscriptionImpl> getEventSubscribers2(String serialNumber)
	{
		List<EventSubscriptionImpl> eventSubscriberList = new LinkedList<EventSubscriptionImpl>();
		
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		
    	Logger fLogger = FatalLoggerClass.logger;
    	
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
		try
		{
			//Defect Id: 902 - Rajani Nagaraju - 20130705 - DealerUser as first PrimarySMS contact.
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			String dealerTenancyType = prop.getProperty("DealerTenancyType");
			
			Query query = session.createQuery("select a.contactId from EventSubscriptionMapping a where a.serialNumber='"+serialNumber+"'");
			Iterator itr = query.list().iterator();
		
			while(itr.hasNext())
			{
				ContactEntity contact = (ContactEntity) itr.next();
				
				EventSubscriptionImpl eventSubscriber = new EventSubscriptionImpl();
				eventSubscriber.setContactId(contact.getContact_id());
				eventSubscriber.setPrimaryEmailId(contact.getPrimary_email_id());
				eventSubscriber.setPrimaryMobileNumber(contact.getPrimary_mobile_number());
				eventSubscriber.setSerialNumber(serialNumber);
				String firstName = contact.getFirst_name();
				if(firstName==null)
					firstName="";
				String lastName = contact.getLast_name();
				if(lastName==null)
					lastName="";
				eventSubscriber.setUserName(firstName+" "+lastName);
				
				//Defect Id: 902 - Rajani Nagaraju - 20130705 - DealerUser as first PrimarySMS contact.
				//Find the tenancyType of the User - To find whether he is a dealer user
				Query userTenancyQuery = session.createQuery(" select b.tenancy_id from AccountContactMapping a, AccountTenancyMapping b " +
						" where a.account_id = b.account_id and a.contact_id='"+contact.getContact_id()+"' ");
				Iterator userTenancyItr = userTenancyQuery.list().iterator();
				while(userTenancyItr.hasNext())
				{
					TenancyEntity userTenancy = (TenancyEntity)userTenancyItr.next();
					
					if(userTenancy.getTenancy_type_id().getTenancy_type_name().equalsIgnoreCase(dealerTenancyType))
					{
						eventSubscriber.setDealerUser(true);
					}
				}
				
				eventSubscriberList.add(eventSubscriber);
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
		
		
		
		return eventSubscriberList;
	}
	
		
	//********************************************************** End of Get primary SMS contact details***************************************
	
	//******************************************** get Alert details for a given alertId *****************************************************
	
	/** This method returns the Alert Details for the specified asset Event for a given serial number
	 * @param serialNumber VIN as String input - mandatory parameter
	 * @param assetEventId AssetEventId as integer input - mandatory parameter
	 * @return returns the details of specified Alert for a given serial number
	 */
	public AlertDetailsImpl getAlertDetails(String serialNumber, int assetEventId)
	{
		AlertDetailsImpl alertDetails = new AlertDetailsImpl();
		
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		
    	Logger fLogger = FatalLoggerClass.logger;
    	//Session session=null;
       
		try
		{
	        //get the current life hours and location of the machine
			AssetMonitoringDetailsBO assetMonitoringData = new AssetMonitoringDetailsBO();
			assetMonitoringData = assetMonitoringData.getAssetMonitoringDetails(serialNumber);
			
			//get the required parameter names from property file 
			
			//get the LAT and LONG of the Serialnumber where the event got generated(Fetch from asset_event table)
			
			String selectQuery="SELECT * FROM asset_event where Serial_Number='"+serialNumber+"' and asset_event_id="+assetEventId+"";
		
	         ResultSet rs =null;  
	         Connection prodConnection=null;
	         Statement statement =null;
	         String Lat=null;
	         String Long=null;
	         String notes=null;
			try{
				
				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(selectQuery);

				while(rs.next()){
					String loc=rs.getString("Location");
					notes=rs.getString("Comments");
					
					if(loc!=null){
					Lat=loc.split(",")[0];
					Long=loc.split(",")[1];
					}
					
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("Exception :"+e.getMessage());
			}

			finally
			{
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}

				if(statement!=null)
					try {
						statement.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				if (prodConnection != null) {
					try {
						prodConnection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}


			}
			//DF20160718 @Roopa Dynamic AMD changes
			
			//set the asset monitoring data 
			alertDetails.setSerialNumber(serialNumber);
			alertDetails.setLifeHours(assetMonitoringData.getParameterNameValueMap().get("CMH"));
			
			
			if(Lat!=null)
				alertDetails.setLatitude(Lat);
			else
			alertDetails.setLatitude(assetMonitoringData.getParameterNameValueMap().get("LAT"));
			
			if(Long!=null)
				alertDetails.setLongitude(Long);
			else
			alertDetails.setLongitude(assetMonitoringData.getParameterNameValueMap().get("LONG"));
			
			
			alertDetails.setProfileName(assetMonitoringData.getProfileName());
			
			alertDetails.setNotes(notes);
			alertDetails.setAssetEventId(assetEventId);
		
			//If the current session is closed, open a new session
			
			/* Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		        session.beginTransaction();
			if(! (session.isOpen() ))
			{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
			}
			
			//get notes corresponding to the alertId
			Query query = session.createQuery("from AssetEventEntity where assetEventId="+assetEventId+" and activeStatus=1");
			Iterator itr = query.list().iterator();
			
			while(itr.hasNext())
			{
				AssetEventEntity assetEvent = (AssetEventEntity)itr.next();
				alertDetails.setNotes(assetEvent.getComments());
				alertDetails.setAssetEventId(assetEventId);
			}*/
			
		}
		
		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}
		
        finally
        {
           /*   if(session.getTransaction().isActive())
              {
                    session.getTransaction().commit();
              }
              
              if(session.isOpen())
              {
                    session.flush();
                    session.close();
              }*/
              
        }

		return alertDetails;
	}
	
	//********************************************END of get Alert details for a given alertId *****************************************************
	
	
	//***************************************************** set Alert Comments ***************************************************
	/** This method set the user Comments for the Alert
	 * @param assetEventId assetEventId of eventHistory
	 * @param comments user comments for the Alert, If not specified null be set as comment
	 * @return status message of alert comment update
	 * @throws CustomFault custom exception is thrown when assetEventId is not specified
	 */
	public String setAlertComments(int assetEventId, String comments) throws CustomFault
	{
		String status = "SUCCESS";
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

       // Logger businessError = Logger.getLogger("businessErrorLogger");
       // Logger fatalError = Logger.getLogger("fatalErrorLogger");
       
    	Logger fLogger = FatalLoggerClass.logger;
    	
    	Logger bLogger = BusinessErrorLoggerClass.logger;
        try
        {
			//validate alert Id
			Query query = session.createQuery("from AssetEventEntity where assetEventId="+assetEventId+" and activeStatus=1 and partitionKey =1");
			
			int validAssetEvent=0;
			Iterator itr = query.list().iterator();
			while(itr.hasNext())
			{
				validAssetEvent = 1;
				AssetEventEntity assetEvent = (AssetEventEntity)itr.next();
				assetEvent.setComments(comments);
				session.update(assetEvent);
				
			}
			
			if(validAssetEvent==0)
			{
				throw new CustomFault("Invalid AssetEventId specified");
			}
        }
        
        catch(CustomFault e)
        {
        	status = "FAILURE";
        	bLogger.error("AssetEventId specified is Invalid");
        }
        catch(Exception e)
		{
        	status = "FAILURE";
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
		
		return status;
	}
		
	//***************************************************** END of set Alert Comments ***************************************************
	
	
//************************************************** get event Alert preference ***********************************************
	
	/** This method returns the notification mode that has been set for each Alert
	 * @param loginId User LoginId
	 * @param eventName Name of the Alert
	 * @param eventTypeId Name of the Alert Type Id
	 * @return notification mode for the list of alerts is returned as a list of implementation object
	 * @throws CustomFault custom Exception is thrown when the event Name is invalid - If specified.
	 */
	public List<AdminAlertPrefImpl> getEventAlertMode(String loginId, String eventName, int eventTypeId) throws CustomFault
	{
		List<AdminAlertPrefImpl> adminAlertPref = new LinkedList<AdminAlertPrefImpl>();
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		// Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;

		//DF20170130 @Roopa for Role based alert implementation
		DateUtil utilObj=new DateUtil();

		List<String> alertCodeList= utilObj.roleAlertMapDetails(loginId,0, "Display");

		ListToStringConversion conversion = new ListToStringConversion();

		StringBuilder alertCodeListAsString=conversion.getStringList(alertCodeList);


		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try
		{
			Query query=null;

			if(eventName == null && eventTypeId==0)
			{
				query= session.createQuery("select distinct e.eventName,t.eventTypeId, t.eventTypeName ,e.isSms,e.isEmail from EventEntity e join e.eventTypeId t where e.eventCode in ("+alertCodeListAsString+")");
			}

			else if(eventName!=null)
			{
				query= session.createQuery("select distinct e.eventName,t.eventTypeId,t.eventTypeName ,e.isSms,e.isEmail from EventEntity e join e.eventTypeId t where e.eventCode in ("+alertCodeListAsString+") and e.eventName='"+eventName+"'");
				if(query.list().isEmpty())
				{
					bLogger.error("Event Name specified is Invalid");
					throw new CustomFault("Invalid Event Name");
				}
			}
			else if(eventTypeId!=0)
			{
				query= session.createQuery("select distinct e.eventName,t.eventTypeId,t.eventTypeName,e.isSms,e.isEmail from EventEntity e join e.eventTypeId t where e.eventCode in ("+alertCodeListAsString+") and e.eventTypeId="+eventTypeId);
			}

			if(query!=null)
			{
				Iterator itr = query.list().iterator();

				while(itr.hasNext())
				{
					Object[] result = (Object[]) itr.next();

					AdminAlertPrefImpl alertPref = new AdminAlertPrefImpl();
					if(result[0]!=null)
						alertPref.setEventName(result[0].toString());
					alertPref.setEventTypeId((Integer)result[1]);
					if(result[2]!=null)
						alertPref.setEventTypeName(result[2].toString());
					if(result[3]!=null)
						alertPref.setSMS((Boolean)result[3]);
					if(result[4]!=null)
						alertPref.setEmail((Boolean)result[4]);

					adminAlertPref.add(alertPref);
				}
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

		return adminAlertPref;
	}
	
	//************************************************** END of get event Alert preference ***********************************************
	
	
	
//****************************************** set notification Mode for all Alerts ******************************************
	
	/** Set the Notification mode for each Alert
	 * @param reqObjList List of ALerts with their notification modes 
	 * @return status String as SUCCESS/FAILURE
	 * @throws CustomFault custom exception is thrown when neither alert /Alert type is not specified OR invalid if specified
	 */
	public String setEventAlertMode(List<AdminAlertPrefRespContract> reqObjList) throws CustomFault
	{
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
        //Logger businessError = Logger.getLogger("businessErrorLogger");
        //Logger fatalError = Logger.getLogger("fatalErrorLogger");
     
    	Logger fLogger = FatalLoggerClass.logger;
    	
    	Logger bLogger = BusinessErrorLoggerClass.logger;
        try
        {
        	//This hashmap stores EventEntity as Key and the corresponding event name as Value
        	HashMap<EventEntity,String> eventNameEntityMap = new HashMap<EventEntity,String>();
        	//This hashmap stores EventEntity as Key and the corresponding event Type Id as Value
        	HashMap<EventEntity,Integer> eventTypeIDeventEntity = new HashMap<EventEntity,Integer>();
        	
        	       	
        	//Get all EventEntity metadata and fill the  HashMaps
        	Query query = session.createQuery("from EventEntity");
			Iterator eventItr = query.list().iterator();
			while(eventItr.hasNext())
			{
				EventEntity eventEntity = (EventEntity)eventItr.next();
				eventNameEntityMap.put(eventEntity,eventEntity.getEventName());
				eventTypeIDeventEntity.put(eventEntity,eventEntity.getEventTypeId().getEventTypeId());
			}
        	
        	
			for(int i=0; i<reqObjList.size(); i++)
			{
				
				if(reqObjList.get(i).getEventName()==null && reqObjList.get(i).getEventTypeId()==0)
				{
					bLogger.error("Either event Name of event type Id has to be specified. Both are NULL");
					throw new CustomFault("Either Event Name or Event Type Id should be specified for setting the mode");
				}
				
				
				if(reqObjList.get(i).getEventName()!=null)
				{
					if(eventNameEntityMap.containsValue(reqObjList.get(i).getEventName()))
					{
						for(int j=0; j<eventNameEntityMap.size(); j++)
						{
							if( ( (String)eventNameEntityMap.values().toArray()[j]).equalsIgnoreCase(reqObjList.get(i).getEventName()) )
							{
								EventEntity updateEventEntity = (EventEntity) eventNameEntityMap.keySet().toArray()[j];
								updateEventEntity.setEmail(reqObjList.get(i).isEmail());
								updateEventEntity.setSms(reqObjList.get(i).isSMS());
								
								session.update(updateEventEntity);
							}
						}
						
					}
					else
					{
						bLogger.error("Alert Name specified is Invalid");
						throw new CustomFault("Invalid Alert Name");
					}
				}
				
				
				else if (reqObjList.get(i).getEventTypeId()!=0)
				{
					
					if(eventTypeIDeventEntity.containsValue(reqObjList.get(i).getEventTypeId()))
					{
						for(int j=0; j<eventTypeIDeventEntity.size(); j++)
						{
							if( ( (Integer)eventTypeIDeventEntity.values().toArray()[j])== (reqObjList.get(i).getEventTypeId()) )
							{
								EventEntity event = (EventEntity) eventTypeIDeventEntity.values().toArray()[j];
								event.setEmail(reqObjList.get(i).isEmail());
								event.setSms(reqObjList.get(i).isSMS());
								
								session.update(event);
							}
						}
					}
					else
					{
						bLogger.error("Alert Type Id specified is Invalid");
						throw new CustomFault("Invalid Alert Type Id");
					}
				}
				
			}	
        }
        
        catch(Exception e)
        {
        	fLogger.fatal("Exception:"+e);
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
	
	//****************************************** END of set notification Mode for all Alerts ******************************************
	
	
	//*******************************************get all the notifications sent for the user accessible Machines ******************************
	/** This method returns the List of Alerts generated for the User accessible machines
	 * DefectId 912 - Rajani Nagaraju - 2013/07/08 -  Replace direct SQL query to Hibernate
	 * DefectId 927 - Rajani Nagaraju - 2013/07/09 - LandmarkAlert Bug fix on User Alert Screen
	 * @param loginId userLoginId
	 * @param userRole Role of the logged in user
	 * @param userTenancyIdList List of UserTenancyId
	 * @param tenancyIdList List of tenancyId
	 * @param serialNumber VIN
	 * @param startDate alert generated start date
	 * @param alertTypeId alertTypeId as Integer input
	 * @param alertSeverity alert Severity as String input
	 * @param isOwnTenancyAlerts Alerts to be returned for machines under own tenancy or not
	 * @param isHistory is Active Alerts/ History of alerts to be returned
	 * @return List of Alerts with their details
	 * @throws CustomFault
	 */
	public List<UserAlertsImpl> getUserAlerts (ContactEntity loginId, String userRole, 	List<Integer> userTenancyIdList, List<Integer> tenancyIdList,
												String serialNumber, String startDate, List<Integer> alertTypeId, List<String> alertSeverity,
												boolean isOwnTenancyAlerts, boolean isHistory) throws CustomFault
	{
		List<UserAlertsImpl> userAlertsList = new LinkedList<UserAlertsImpl>();
		//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks
		List<UserAlertsImpl> userAlertsList2 = new LinkedList<UserAlertsImpl>();
		
		TenancyBO tenancyBO = new TenancyBO();
		List<AccountEntity> accountEntityList = new LinkedList<AccountEntity>();
		
		//Logger businessError = Logger.getLogger("businessErrorLogger");
        //Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
    	Logger bLogger = BusinessErrorLoggerClass.logger;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
		try
		{
			
//			Keerthi : 12/02/14 : SEARCH : if m/c no.(7 digits) provided , get corresponding PIN 
			if(serialNumber!=null){
				if(serialNumber.trim().length()==7){
					String machineNumber = serialNumber;
					serialNumber = new AssetDetailsBO().getSerialNumberMachineNumber(machineNumber);
					if(serialNumber==null){//invalid machine number
						iLogger.info("Machine number "+ machineNumber + "does not exist !!!");
						return userAlertsList ;
					}
				}
			}
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

			List<Integer> selectedTenancy = new LinkedList<Integer>();
			if(!(tenancyIdList==null || tenancyIdList.isEmpty()))
			{
				selectedTenancy.addAll(tenancyIdList);
			}
			else
			{
				selectedTenancy.addAll(userTenancyIdList);
			}
			
			//check whether to look up for alerts of own tenancy
			if(isOwnTenancyAlerts==false)
			{
				accountEntityList = tenancyBO.getAccountEntity(tenancyIdList);
			}
			else
			{
				accountEntityList = tenancyBO.getAccountEntity(userTenancyIdList);
			}
		
			List<String> serialNumberList = new LinkedList<String>();
			List<String> finalSerialNumberList = new LinkedList<String>();
			boolean machineGroupUser=false;
			
			
			String customerCare = null;
			String admin = null;
		//	prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			customerCare= prop.getProperty("CustomerCare");
			admin= prop.getProperty("OEMadmin");
			          
		
			////////////////////////////////////// Logic to get the alert generated for the machines of specified Tenancy ////////////////////////////////
		
			if(! (session.isOpen() ))
            {
                session = HibernateUtil.getSessionFactory().getCurrentSession();
                session.getTransaction().begin();
            }

			//get a list of serial numbers for the accountEntitylist
			AssetDetailsBO assetDetails = new AssetDetailsBO();
					
			List<String> accountAssetList = new LinkedList<String>();
			for(int j=0; j<accountEntityList.size(); j++)
			{
				List<AssetEntity> assetEntityList = assetDetails.getAccountAssets(accountEntityList.get(j).getAccount_id());
				for(int k=0; k<assetEntityList.size(); k++)
				{
					accountAssetList.add(assetEntityList.get(k).getSerial_number().getSerialNumber());
				}
			}
		
		
		
			//if the user is tenancy admin get the list of serialNumbers he is accessible to
			if( (loginId.getIs_tenancy_admin()==1) ||
					( (userRole.equalsIgnoreCase(customerCare) || userRole.equalsIgnoreCase(admin)) ) )
							//&& (isOwnTenancyAlerts==false))	)
			{
				
				//get the list of all serial numbers under the tenancy specified
				if(serialNumber!=null)
				{
					if(accountAssetList.contains(serialNumber))
					{
						finalSerialNumberList.add(serialNumber);
					}
					
				}
				else
				{
					finalSerialNumberList.addAll(accountAssetList);
				}
				
			}
		
		
			//if the user is not tenancy admin get the list of serialNumbers he is accessible to
			else if ( (loginId.getIs_tenancy_admin()==0)) 
					//|| 
				    //( (userRole.equalsIgnoreCase(customerCare) || userRole.equalsIgnoreCase(admin)) && (isOwnTenancyAlerts==true) && (loginId.getIs_tenancy_admin()==0) ) )
			{
				//get the list of machines under the machine group to which the user belongs to
				AssetCustomGroupDetailsBO assetCustomGroup = new AssetCustomGroupDetailsBO();
				List<AssetCustomGroupDetailsBO> BoObj = assetCustomGroup.getAssetGroup(loginId.getContact_id(),0,0,null,userTenancyIdList,false);
				if(! (BoObj==null || BoObj.isEmpty()) )
				{
					machineGroupUser = true;
					for(int u=0; u<BoObj.size(); u++)
					{
						if(userTenancyIdList.contains(BoObj.get(u).getTenancyId()) )
								serialNumberList.addAll(BoObj.get(u).getSerialNumberList());
					}
				}
			
				if(serialNumber!=null)
				{
					if(serialNumberList.contains(serialNumber))
					{
						finalSerialNumberList.add(serialNumber);
					}
				}
				else
					
				{
					finalSerialNumberList.addAll(serialNumberList);
				}
				
				if(machineGroupUser==false && finalSerialNumberList.isEmpty())
				{
					if( (serialNumber!=null) )
					{
						if(accountAssetList.contains(serialNumber)) 
							finalSerialNumberList.add(serialNumber);
					}
						
					
					else
						finalSerialNumberList.addAll(accountAssetList);
				}
			}
			
			
		
			//Convert the SerialNumberList to comma seperated string
			ListToStringConversion conversionObj = new ListToStringConversion();
			String commaSeperatedString = conversionObj.getStringList(finalSerialNumberList).toString();
			iLogger.info("commaSeperatedString "+commaSeperatedString);
		
				
			if(! (finalSerialNumberList==null || finalSerialNumberList.isEmpty()) )
			{
				//Getting the List of Alerts other than Landmark Alerts
				userAlertsList2 = executeUserAlertQuery(commaSeperatedString, startDate, alertTypeId, alertSeverity, isOwnTenancyAlerts, isHistory, false,selectedTenancy, false);
				userAlertsList.addAll(userAlertsList2);
				//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks
				//Getting the List of Landmark Alerts
				userAlertsList2 = executeUserAlertQuery(commaSeperatedString, startDate, alertTypeId, alertSeverity, isOwnTenancyAlerts, isHistory, false,selectedTenancy, true);
				userAlertsList.addAll(userAlertsList2);
			}
		
			
			//////////////////////////////////////END of Logic to get the alert generated for the machines of specified Tenancy ////////////////////////////////
		
		
			/////////////////////////// Logic to get the Service,Emergency and Health Alerts of the Machines down the hierarachy ///////////////////////
			
			if(! (session.isOpen() ))
            {
                session = HibernateUtil.getSessionFactory().getCurrentSession();
                session.getTransaction().begin();
            }
			
			List<UserAlertsImpl> userAlertsList1 = new LinkedList<UserAlertsImpl>();
		
			//get the parent tenancy Id
			List<Integer> parentTenancyIdList = new LinkedList<Integer>();
			//DefectId:20140305 @ If Ownstock True
			if(isOwnTenancyAlerts==true)
			{
				return userAlertsList;
			}
			if((userRole.equalsIgnoreCase(customerCare) || userRole.equalsIgnoreCase(admin)))
			{
				/*if(isOwnTenancyAlerts==true)
				{
					return userAlertsList;
				}*/
				
				if(isOwnTenancyAlerts==false)
				{
					parentTenancyIdList.addAll(tenancyIdList);
				}
				else
				{
					parentTenancyIdList.addAll(userTenancyIdList);
				}
			}
			
			else
			{
				parentTenancyIdList.addAll(userTenancyIdList);
			}
		
		
			//Query to get SerialNumber
			String serialNumberQueryString = null;
			String finalSerialNumQueryString = null;
			
			//Convert parentTenancyIdList to comma seperated string
			ListToStringConversion conversionObj1 = new ListToStringConversion();
			String commaSeperatedString1 = conversionObj.getIntegerListString(parentTenancyIdList).toString();
			
			
			/*serialNumberQueryString = "select Serial_Number from asset_owners where Account_ID in " +
										"( select Account_ID from account_tenancy where Tenancy_ID in " +
											"( select Child_ID from tenancy_bridge where Parent_Id in (" +commaSeperatedString1 +" ) and " +
												"Child_ID not in ( " +commaSeperatedString1+"  ) ) )";*/
			
			//DefectId 912 - Rajani Nagaraju - 2013/07/08 -  Replace direct SQL query to Hibernate
			/*serialNumberQueryString = " select serialNumber from AssetAccountMapping where accountId in " +
					" (select account_id from AccountTenancyMapping where tenancy_id in " +
					" ( select childId from TenancyBridgeEntity where parentId in ("+commaSeperatedString1+") and " +
							" childId not in ("+commaSeperatedString1+")))";*/
			//DefectID: - Asset Owner Changes - 2013-07-19 - Rajani Nagaraju
			serialNumberQueryString = " select serial_number from AssetEntity where active_status=true and client_id="+clientEntity.getClient_id()+" and primary_owner_id in " +
			" (select account_id from AccountTenancyMapping where tenancy_id in " +
			" ( select childId from TenancyBridgeEntity where parentId in ("+commaSeperatedString1+") and " +
					" childId not in ("+commaSeperatedString1+")))";
					
			
			if( (loginId.getIs_tenancy_admin()==1) || ((userRole.equalsIgnoreCase(customerCare) || userRole.equalsIgnoreCase(admin))) )
			{
				
				finalSerialNumQueryString = serialNumberQueryString;
				
				if(serialNumber!=null)
				{
					finalSerialNumQueryString = finalSerialNumQueryString + "and serial_number = '"+serialNumber+"'";
					
				}
				
			}
		
			else
			{
				boolean assetGroupUser = false;
				List<String> serNumList = new LinkedList<String>();
				String commaSeperatedString2 = null;
				
				//check whether the user does not belongs to any machine group
				AssetCustomGroupDetailsBO assetCustomGroup1 = new AssetCustomGroupDetailsBO();
				List<AssetCustomGroupDetailsBO> BoObj1 = assetCustomGroup1.getAssetGroup(loginId.getContact_id(),0,0,null,parentTenancyIdList,false);
				if(! (BoObj1==null || BoObj1.isEmpty()) )
				{
					assetGroupUser = true;
					for(int u=0; u<BoObj1.size(); u++)
					{
						if(!(parentTenancyIdList.contains(BoObj1.get(u).getTenancyId())))
							serNumList.addAll(BoObj1.get(u).getSerialNumberList());
					}
				
					if(! (serNumList==null || serNumList.isEmpty()) )
					{
						//Convert serNumList to comma seperated string
						ListToStringConversion conversionObj2 = new ListToStringConversion();
						commaSeperatedString2 = conversionObj.getStringList(serNumList).toString();
					}
				}
				
			
				if(assetGroupUser==false)
				{
					finalSerialNumQueryString = serialNumberQueryString;
					if(serialNumber!=null)
					{
						finalSerialNumQueryString = finalSerialNumQueryString + "and serial_number = '"+serialNumber+"'";
					}
				}
				
				else
				{
					finalSerialNumQueryString = serialNumberQueryString+" and serial_number in ( "+commaSeperatedString2+")" ;
					if(serialNumber!=null)
					{
						finalSerialNumQueryString = finalSerialNumQueryString+" and serial_number = '"+serialNumber+"'";
					}
				}
			}
		
			//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks
			//Getting the List of Alerts other than Landmark Alerts
			userAlertsList1 = executeUserAlertQuery(finalSerialNumQueryString, startDate, alertTypeId, alertSeverity, isOwnTenancyAlerts, isHistory, true,selectedTenancy,false);		
			userAlertsList.addAll(userAlertsList1);
			
			//Getting the List of Landmark Alerts
			userAlertsList1 = executeUserAlertQuery(finalSerialNumQueryString, startDate, alertTypeId, alertSeverity, isOwnTenancyAlerts, isHistory, true,selectedTenancy, true);
			userAlertsList.addAll(userAlertsList1);
			
			if(! (session.isOpen() ))
            {
                session = HibernateUtil.getSessionFactory().getCurrentSession();
                session.getTransaction().begin();
            }
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
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
		/////////////////////////// END of Logic to get the Service,Emergency and Health Alerts of the Machines down the hierarachy ///////////////////////
		
		return userAlertsList;
	}
	
	//******************************************* END of get all the notifications sent for the user accessible Machines ******************************
	
	
	//************************************** Create and Execute Query to get User Alerts ***********************************************
	/** This method queries the database through hibernate and gets the Alert details
	 * DefectId 912 - Rajani Nagaraju - 2013/07/08 -  Replace direct SQL query to Hibernate
	 * DefectId 927 - Rajani Nagaraju - 2013/07/09 - LandmarkAlert Bug fix on User Alert Screen
	 * 20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks - Considering diff query for Landmark Alert when compared to other alerts
	 * @param serialNumberString Comma delimited string of serailNumbers
	 * @param startDate startDate from when the alert data has to be retrieved
	 * @param alertTypeId alertTypeId as Integer input
	 * @param alertSeverity alert Severity
	 * @param isOwnTenancyAlerts Alerts to be returned for machines under own tenancy or not
	 * @param isHistory is Active Alerts/ History of alerts to be returned
	 * @param isBelowTenancyAlerts is the alert generated for the machines down the hierarchy to be displayed
	 * @return List of Alerts with their details
	 * @throws SQLException
	 */
	public List<UserAlertsImpl> executeUserAlertQuery(String serialNumberString,String startDate, 
														List<Integer> alertTypeId, List<String> alertSeverity,boolean isOwnTenancyAlerts, boolean isHistory,
														boolean isBelowTenancyAlerts, List<Integer> userTenancyIdList, boolean isLandmarkAlert ) throws SQLException
	{
		List<UserAlertsImpl> userAlertsList = new LinkedList<UserAlertsImpl>();
		
		// Logger businessError = Logger.getLogger("businessErrorLogger");
	     //Logger fatalError = Logger.getLogger("fatalErrorLogger");
		//Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
    	Logger bLogger = BusinessErrorLoggerClass.logger;
	     Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	     session.beginTransaction();
	        
	     ListToStringConversion conversionObj = new ListToStringConversion();
			
	     
	     try
	     {
	    	/* //End date is always today
			Date currentDate = new java.util.Date();
			Timestamp currentTime = new Timestamp(currentDate.getTime());
			String endDate = currentTime.toString();*/
		
			int overdueEventId =0;
			int dueInWeekId =0;
			int dueInDayId =0;
			int LandmarkEventTypeId=0;
			
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			overdueEventId= Integer.parseInt(prop.getProperty("OverdueEventId"));
			dueInWeekId= Integer.parseInt(prop.getProperty("DueInWeek"));
			dueInDayId= Integer.parseInt(prop.getProperty("DueInDay"));
			LandmarkEventTypeId = Integer.parseInt(prop.getProperty("LandmarkEventTypeId"));
		
			String basicQueryString = null;
			String groupByQueryString = null;
			String finalQueryString = null;
			String orderByQueryString =null;
			//DefectId 912 - Rajani Nagaraju - 2013/07/08 -  Replace direct SQL query to Hibernate
			
			//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks
			if(!(isLandmarkAlert))
			{
				
				//	" a.assetEventId=y.assetEventId ";
				
				//DefectId 912 - Rajani Nagaraju - 2013/07/08 -  Replace direct SQL query to Hibernate
				if(isHistory){//if history,show all closed events irrespective of event id :Keerthi : 13/03/14
					basicQueryString = " select a.serialNumber, a.assetEventId as asset_event_id , c.eventId, c.eventDescription, b.eventTypeId, " +
					" b.eventTypeName, a.eventGeneratedTime, a.eventSeverity , c.eventName as Event_Name, " +
					" a.comments " +
					" from AssetEventEntity a, EventTypeEntity b, EventEntity c " +
					" where a.eventTypeId = b.eventTypeId and a.eventId=c.eventId " +
					" and a.eventTypeId != "+LandmarkEventTypeId;
//					groupByQueryString = " group by a.serialNumber ";
					orderByQueryString = " order by a.eventSeverity asc,a.eventGeneratedTime desc";
				}
				else{
					basicQueryString = " select a.serialNumber, max(a.assetEventId) as asset_event_id , c.eventId, c.eventDescription, b.eventTypeId, " +
					" b.eventTypeName, max(a.eventGeneratedTime), a.eventSeverity , c.eventName as Event_Name, " +
					" a.comments " +
					" from AssetEventEntity a, EventTypeEntity b, EventEntity c " +
					" where a.eventTypeId = b.eventTypeId and a.eventId=c.eventId " +
					" and a.eventTypeId != "+LandmarkEventTypeId;
					groupByQueryString = " group by a.serialNumber, a.eventId ";
					orderByQueryString = " order by a.eventSeverity asc,max(a.eventGeneratedTime) desc";
				}
				
			}
			
			//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks - Complete else part
			else
			{
				String userTenStringList = conversionObj.getIntegerListString(userTenancyIdList).toString();
				
				
				
				if(isHistory){
					basicQueryString = " select a.serialNumber, a.assetEventId as asset_event_id , c.eventId, c.eventDescription, b.eventTypeId, " +
					" b.eventTypeName, a.eventGeneratedTime, a.eventSeverity ,  c.eventName as Event_Name, " +
					" a.comments, land.Landmark_Name " +
					" from AssetEventEntity a, EventTypeEntity b, EventEntity c, LandmarkLogDetailsEntity ll, LandmarkEntity land, LandmarkCategoryEntity lc " +
					" where a.eventTypeId = b.eventTypeId and a.eventId=c.eventId " +
					" and a.eventTypeId = "+LandmarkEventTypeId + " and  a.assetEventId=ll.assetEventId and ll.landmarkId=land.Landmark_id " +
					" and land.Landmark_Category_ID = lc.Landmark_Category_ID and lc.Tenancy_ID in ("+userTenStringList+")";
//					groupByQueryString = " group by a.serialNumber, land.Landmark_id ";
					orderByQueryString = " order by a.eventSeverity asc,a.eventGeneratedTime desc";
				}
				else{
					basicQueryString = " select a.serialNumber, max(a.assetEventId) as asset_event_id , c.eventId, c.eventDescription, b.eventTypeId, " +
					" b.eventTypeName, max(a.eventGeneratedTime), a.eventSeverity ,  c.eventName as Event_Name, " +
					" a.comments, land.Landmark_Name " +
					" from AssetEventEntity a, EventTypeEntity b, EventEntity c, LandmarkLogDetailsEntity ll, LandmarkEntity land, LandmarkCategoryEntity lc " +
					" where a.eventTypeId = b.eventTypeId and a.eventId=c.eventId " +
					" and a.eventTypeId = "+LandmarkEventTypeId + " and  a.assetEventId=ll.assetEventId and ll.landmarkId=land.Landmark_id " +
					" and land.Landmark_Category_ID = lc.Landmark_Category_ID and lc.Tenancy_ID in ("+userTenStringList+")";
					groupByQueryString = " group by a.serialNumber, a.eventId, land.Landmark_id ";
					orderByQueryString = " order by a.eventSeverity asc,max(a.eventGeneratedTime) desc";
				}
				
			}
					
			/*basicQueryString = "select a.serial_number,max(a.asset_event_id) as asset_event_id,a.Event_ID, c.Event_Description, a.Event_Type_ID," +
			"b.Event_Type_Name, max(a.Event_Generated_Time),a.Event_Severity, count(*) as alertcount, c.Event_Name as Event_Name from asset_event a,event_type b, " +
			"business_event c where a.Event_Type_ID = b.Event_Type_ID and a.Event_ID = c.Event_ID ";
			*/
			
			
			//groupByQueryString = " group by a.serial_number, a.event_id ";
			// Defect ID : 490 - Suprava - 29/07/13 - ordering based on seveirty
			// and event generated time
			
			
			if(serialNumberString!=null)
			{
				basicQueryString = basicQueryString+" and a.serialNumber in ( "+ serialNumberString + " )";
			}
			
			if(isHistory==false)
			{
				basicQueryString = basicQueryString+" and a.activeStatus=1 and a.partitionKey =1";
			}
			
			else
			{
				basicQueryString = basicQueryString+" and a.activeStatus=0 and  a.eventGeneratedTime >= '"+startDate+"'";
			}
		
			if(alertTypeId!=null)
			{
				String alertTypeIdString = conversionObj.getIntegerListString(alertTypeId).toString();
				basicQueryString= basicQueryString+" and a.eventTypeId IN ("+alertTypeIdString+") ";
			}
			
			if(alertSeverity!=null)
			{
				String alertSeverityString = conversionObj.getStringList(alertSeverity).toString();
				basicQueryString = basicQueryString+" and a.eventSeverity IN ("+alertSeverityString.toUpperCase()+") ";
			}
			
			if(isBelowTenancyAlerts==true)
			{
				basicQueryString = basicQueryString+ " and a.eventTypeId in (1,2,4,5) ";
			}
		
		
			
			if(isHistory){
				basicQueryString = basicQueryString + orderByQueryString;
			}
			else{
				basicQueryString = basicQueryString + groupByQueryString+ orderByQueryString;
			}
			
			Object[] resultSet = null;
			HashMap<String,List<String>> assetEventData = new HashMap<String,List<String>>();
			
			Query query = session.createQuery(basicQueryString);
			Iterator itr = query.list().iterator();
		/*
			finalQueryString = "select x.*, y.Comments from	( "+basicQueryString+" ) x, asset_event y where x.asset_event_id = y.asset_event_id";
			
		
			HashMap<String,List<String>> assetEventData = new HashMap<String,List<String>>();
			
			ConnectMySQL connectionObj = new ConnectMySQL();
			Connection con = connectionObj.getConnection();
			
			Statement statement = con.createStatement(); 
			ResultSet resultSet=null;
			
			resultSet = statement.executeQuery(finalQueryString);
			
			if(resultSet==null)
			{
				return userAlertsList;
			}*/
			String landmarkName = null;
			if(isHistory){//not putting in hashmap, taking all rows
				while(itr.hasNext())
				{
					resultSet = (Object[]) itr.next();
					
					String alertComment = null;
					//DF20150408 - Rajani Nagaraju - Commenting the below line for performance improvement as it is not required for main page.
					//Same will be returned from new Service UserAlertDetailService
					/*Query commentQuery = session.createQuery(" select a.comments from AssetEventEntity a where a.assetEventId="+(Integer) resultSet[1]);
					Iterator commentItr = commentQuery.list().iterator();
					while(commentItr.hasNext())
					{
						alertComment = (String)commentItr.next();
					}*/
					UserAlertsImpl implObj = new UserAlertsImpl();
					if(resultSet[0]!=null){
						implObj.setSerialNumber(((AssetEntity)resultSet[0]).getSerial_number().getSerialNumber());
					}
					
					if(resultSet[1]!=null){
						implObj.setAssetEventId((Integer)resultSet[1]);
					}
					
					if(resultSet[3]!=null){
						implObj.setAlertDescription((String)resultSet[3]);
					}
					if(resultSet[4]!=null){
						implObj.setAlertTypeId((Integer)resultSet[4]);
					}
					if(resultSet[5]!=null){
						implObj.setAlertTypeName((String)resultSet[5]);
					}
					if(resultSet[6]!=null){
						implObj.setLatestReceivedTime(String.valueOf((Timestamp)resultSet[6]));
					}
					if(resultSet[7]!=null){
						implObj.setAlertSeverity((String)resultSet[7]);
					}
					if(resultSet[8]!=null){
						implObj.setAlertDescription((String)resultSet[8]);
					}
					if(isLandmarkAlert)
					{
						if(resultSet[10]!=null)
							landmarkName = resultSet[10].toString();
						String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
						implObj.setAlertDescription(desc);
						
					}
					userAlertsList.add(implObj);
				}
			}	
			else{
		
			while(itr.hasNext())
			{
				resultSet = (Object[]) itr.next();
				
				String alertComment = null;
				//DF20150408 - Rajani Nagaraju - Commenting the below line for performance improvement as it is not required for main page.
				//Same will be returned from new Service UserAlertDetailService
				/*Query commentQuery = session.createQuery(" select a.comments from AssetEventEntity a where a.assetEventId="+(Integer) resultSet[1]);
				Iterator commentItr = commentQuery.list().iterator();
				while(commentItr.hasNext())
				{
					alertComment = (String)commentItr.next();
				}*/
				
				//Check for the LandmarkAlert
				//DefectId 927 - Rajani Nagaraju - 2013/07/09 - LandmarkAlert Bug fix on User Alert Screen
				//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks - Commenting the below code as it is already handled in Query
				
				landmarkName = null;
				/*int eventTypeId= (Integer)resultSet[4];
				if(eventTypeId==LandmarkEventTypeId)
				{
					Query landmarkQuery = session.createQuery("from LandmarkLogDetailsEntity where assetEventId="+(Integer)resultSet[1]);
					Iterator landmarkItr = landmarkQuery.list().iterator();
					int landmarkTenancyId=0;
					
					while(landmarkItr.hasNext())
					{
						LandmarkLogDetailsEntity logDetails = (LandmarkLogDetailsEntity)landmarkItr.next();
						landmarkTenancyId = logDetails.getLandmarkId().getLandmark_Category_ID().getTenancy_ID().getTenancy_id();
						landmarkName = logDetails.getLandmarkId().getLandmark_Name();
					}
					
					if(!(userTenancyIdList.contains(landmarkTenancyId)))
					{
						continue;
					}
				}*/
				
				String serNumString = ((AssetEntity)resultSet[0]).getSerial_number().getSerialNumber();
				
				//First record to be inserted to hashmap
				if(assetEventData.isEmpty())
				{
					//First element is always Count
					List<String> assetEventDetails = new LinkedList<String>();
					String hashmapKey = null; // SerialNumber+"*"+eventDescription
									
					//20140113: 1931  : Rajani Nagaraju
					if(isLandmarkAlert)
					{
						if(resultSet[10]!=null)
							landmarkName = resultSet[10].toString();
						hashmapKey = serNumString+":"+(String) resultSet[8]+":"+landmarkName;
					}
					else
					{
						hashmapKey = serNumString+":"+(String) resultSet[8];// SerialNumber+":"+eventName
					}
					assetEventDetails.add("0");//List{0} - alertCount
					assetEventDetails.add(String.valueOf((Integer) resultSet[1]));//List{1} - assetEventId
					assetEventDetails.add(String.valueOf((Integer) resultSet[2]));//List{2} - eventId
					assetEventDetails.add(String.valueOf((Integer) resultSet[4]));//List{3} - eventTypeId
					assetEventDetails.add((String) resultSet[5]);//List{4} - eventTypeName
					assetEventDetails.add(String.valueOf((Timestamp) resultSet[6]));//List{5} - eventGeneratedTime
					assetEventDetails.add((String) resultSet[7]);//List{6} - eventSeverity
					assetEventDetails.add(alertComment);//List{7} - Comments
					
					//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks - Commenting below block and adding new block
					/*if(eventTypeId==LandmarkEventTypeId)
					{
						String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
						assetEventDetails.add(desc);
					}*/
					

					if(isLandmarkAlert)
					{
						if(resultSet[10]!=null)
							landmarkName = resultSet[10].toString();
						String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
						assetEventDetails.add(desc);
						
					}
					else
					{
						assetEventDetails.add((String) resultSet[3]);//List{8} - eventDescription
					}
					assetEventData.put(hashmapKey, assetEventDetails);
				}
			
				else
				{
					String hashmapKey =null;
					
					//20140113: 1931  : Rajani Nagaraju
					if(isLandmarkAlert)
					{
						if(resultSet[10]!=null)
							landmarkName = resultSet[10].toString();
						hashmapKey = serNumString+":"+(String) resultSet[8]+":"+landmarkName;
					}
					
					else
					{
						hashmapKey = serNumString+":"+(String) resultSet[8];// SerialNumber+":"+eventName
					}
					
					if(assetEventData.containsKey(hashmapKey))
					{
						List<String> assetEventDetails = assetEventData.get(hashmapKey);
						int alertCount = Integer.parseInt(assetEventDetails.get(0));
//						String count = String.valueOf((Long) resultSet[8]);
//						alertCount = alertCount+Integer.parseInt(count);
						String alertCountString = Integer.toString(alertCount);
						assetEventDetails.set(0, alertCountString);
					
										
						//also update eventGeneratedTime, Severity, assetEventId, Comments
						if(((String) resultSet[7]).equalsIgnoreCase("RED"))
						{
							assetEventDetails.set(5, String.valueOf((Timestamp) resultSet[6]));//eventGeneratedTime
							assetEventDetails.set(6, (String) resultSet[7]);//Severity
							assetEventDetails.set(1, String.valueOf((Integer) resultSet[1]));//assetEventId
							assetEventDetails.set(7, alertComment);//Comments
							
							//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks - Commenting below block and adding new block
							/*if(eventTypeId==LandmarkEventTypeId)
							{
								String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
								assetEventDetails.set(8, desc); //Description
							}*/
							if(isLandmarkAlert)
							{
								if(resultSet[10]!=null)
									landmarkName = resultSet[10].toString();
								String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
								assetEventDetails.add(desc);
								
							}
							else
							{
								assetEventDetails.set(8, (String) resultSet[3]); //Description
							}
					
						}
					
					
						else if( (((String) resultSet[7]).equalsIgnoreCase("YELLOW")) && (assetEventDetails.get(6).equalsIgnoreCase("YELLOW")) )
						{
													
							//if( (Integer.parseInt((String) resultSet[1])) > (Integer.parseInt(assetEventDetails.get(1))) )
							if( ((Integer) resultSet[1]) > (Integer.parseInt(assetEventDetails.get(1))) )
							{
								assetEventDetails.set(5, String.valueOf((Timestamp) resultSet[6]));//eventGeneratedTime
								assetEventDetails.set(1, String.valueOf((Integer) resultSet[1]));//assetEventId
								assetEventDetails.set(7, alertComment);//Comments
								
								//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks - Commenting below block and adding new block
								/*if(eventTypeId==LandmarkEventTypeId)
								{
									String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
									assetEventDetails.set(8, desc); //Description
								}*/
								if(isLandmarkAlert)
								{
									if(resultSet[10]!=null)
										landmarkName = resultSet[10].toString();
									String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
									assetEventDetails.add(desc);
									
								}
								else
								{
									assetEventDetails.set(8, (String) resultSet[3]); //Description
								}
							}
						}
						assetEventData.put(hashmapKey, assetEventDetails);
					}
				
					else
					{
						//First element is always Count
						List<String> assetEventDetails = new LinkedList<String>();
						
						assetEventDetails.add("0");//List{0} - alertCount
						assetEventDetails.add(String.valueOf((Integer) resultSet[1]));//List{1} - assetEventId
						assetEventDetails.add(String.valueOf((Integer) resultSet[2]));//List{2} - eventId
						assetEventDetails.add(String.valueOf((Integer) resultSet[4]));//List{3} - eventTypeId
						assetEventDetails.add((String) resultSet[5]);//List{4} - eventTypeName
						assetEventDetails.add(String.valueOf((Timestamp) resultSet[6]));//List{5} - eventGeneratedTime
						assetEventDetails.add((String) resultSet[7]);//List{6} - eventSeverity
						assetEventDetails.add(alertComment);//List{7} - Comments
						// Defect ID : 490 - Suprava - 29/07/13- checking
						//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks - Commenting below block and adding new block
						/*if(eventTypeId==LandmarkEventTypeId && !(assetEventData.containsKey(hashmapKey)))
						{
							String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
							assetEventDetails.add(desc);
						}*/
						if(isLandmarkAlert && !(assetEventData.containsKey(hashmapKey)) )
						{
							if(resultSet[10]!=null)
								landmarkName = resultSet[10].toString();
							String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
							assetEventDetails.add(desc);
							
						}
						else
						{
							assetEventDetails.add((String) resultSet[3]);//List{8} - eventDescription
						}
						
						assetEventData.put(hashmapKey, assetEventDetails);
					}
				}
		
		
			}
		
		
		    //set the values in HashMap into Impl class fields
			for(int j=0; j<assetEventData.size(); j++)
			{
				//get the key
				String key = (String)assetEventData.keySet().toArray()[j];
				String[] keyArray = key.split(":");
			
				UserAlertsImpl implObj = new UserAlertsImpl();
				implObj.setSerialNumber(keyArray[0]);
			
			
				//get the values
				List<String> mapValues = (List<String>)assetEventData.values().toArray()[j];
				implObj.setAssetEventId(Integer.parseInt(mapValues.get(1)));
				implObj.setAlertTypeId(Integer.parseInt(mapValues.get(3)));
				implObj.setAlertTypeName(mapValues.get(4));
				implObj.setLatestReceivedTime(mapValues.get(5));
				implObj.setAlertSeverity(mapValues.get(6));
				implObj.setRemarks(mapValues.get(7));
				
				
				if(Integer.parseInt(mapValues.get(2)) == overdueEventId)
					implObj.setAlertCounter(3);
				else if(Integer.parseInt(mapValues.get(2)) == dueInWeekId)
					implObj.setAlertCounter(2);
				else if(Integer.parseInt(mapValues.get(2)) == dueInDayId)
					implObj.setAlertCounter(1);
				else
					implObj.setAlertCounter(Integer.parseInt(mapValues.get(0)));
				
				implObj.setAlertDescription(mapValues.get(8));
				
				userAlertsList.add(implObj);
			}
		   }
	     }
	     
	    catch(Exception e)
		{
	    	e.printStackTrace();
			fLogger.fatal("Exception :"+e);
		}
        
	    //DF-20131115 session not getting closed
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
	    
        return userAlertsList;
	
	}
	//************************************** END of Create and Execute Query to get User Alerts ***********************************************
	
	/**
	 * method to get service due overdue details 
	 * @param LoginId, Period, TenancyIdList, AssetGroupIdList, AssetTypeIdList, customAssetGroupIdList
	 * @param LandmarkCategoryList, LandmarkIdList, flag
	 * @throws CustomFault
	 * @return ServiceDueOverDueImpl
	 */
	//--
	public ServiceDueOverDueImpl getServiceDueOverDueDetails(String LoginId, String Period, List<Integer> TenancyIdList, List<Integer> AssetGroupIdList, 
			List<Integer> AssetTypeIdList,List<Integer> customAssetGroupIdList, List<Integer> LandmarkCategoryList, List<Integer> LandmarkIdList,boolean flag,List<Integer> eventTypeIdList,List<String> eventSeverityList,
			boolean isOwnStock,boolean isActiveAlerts)	throws CustomFault{
		Logger iLogger = InfoLoggerClass.logger;
    	
		long startTime = System.currentTimeMillis();	
		int duecount = 0;
		int overduecount = 0;
		String basicSelectQuery=null;
		String basicWhereQuery=null;
		String basicFromQuery=null;
		String finalQuery = null;		
		Calendar c=Calendar.getInstance();
		int currentYear = c.get(Calendar.YEAR);

		ListToStringConversion conversionObj = new ListToStringConversion();
	    String tenancyIdStringList = conversionObj.getIntegerListString(TenancyIdList).toString();
	    if(isActiveAlerts){
	    	return getActiveAlerts(tenancyIdStringList,eventTypeIdList,eventSeverityList,flag,isOwnStock);
	    }
		basicSelectQuery ="select e.Notification_Id,e.Notification_Name, e.Severity,a.NotificationCount ";		

		basicWhereQuery = " where a.Tenancy_Id=t.tenacy_Dimension_Id and f.parentId in ( " + tenancyIdStringList +" ) and a.Notification_Id=e.Notification_Dimension_Id and f.childId=t.tenancyId " ;
		//Changes Done by Juhi on 6 May2013
		
		 if ((Period.equalsIgnoreCase("Week"))||(Period.equalsIgnoreCase("Last Week"))) {
		 	   Date currentDate = new Date();
		 	   DateUtil dateUtilObj = new DateUtil();
		 	  DateUtil dateUtilObj1 = new DateUtil();
		 	   if (Period.equalsIgnoreCase("Week")){
		 	    dateUtilObj  = dateUtilObj. getCurrentDateUtility(currentDate);
		 	    int week = dateUtilObj. getWeek();
		 	    int year = dateUtilObj. getYear();
		 	    basicFromQuery = " from  notification_fact_weekagg a ";
		 	    basicWhereQuery = basicWhereQuery+ " and a.TimeCount = "+week+" and a.Year = "+year+"";
		 	   }
		 	   if(Period.equalsIgnoreCase("Last Week")){
		 	    dateUtilObj  = dateUtilObj. getPreviousDateUtility(currentDate);
		 	   dateUtilObj1  = dateUtilObj1. getCurrentDateUtility(currentDate);
		 	    int week = dateUtilObj. getWeek();
		 	    int year=0;
		 	    if(dateUtilObj1.getWeek()==1)
		 	     year = dateUtilObj.getYear();
		 	    else
		 	     year = dateUtilObj.getCurrentYear();
		 	    basicFromQuery = " from  notification_fact_weekagg a ";
		 	    basicWhereQuery = basicWhereQuery+ " and a.TimeCount = "+week+" and a.Year = "+year+"";
		 	   }
		 	  }
		 	  else if ((Period.equalsIgnoreCase("Month"))||(Period.equalsIgnoreCase("Last Month"))) {
		 	   Date currentDate = new Date();
		 	   DateUtil dateUtilObj = new DateUtil(); 
		 	  DateUtil dateUtilObj1 = new DateUtil(); 
		 	   if(Period.equalsIgnoreCase("Month")){
		 	    dateUtilObj  = dateUtilObj. getCurrentDateUtility(currentDate);
		 	    int month = dateUtilObj. getMonth ();
		 	    int year = dateUtilObj. getYear();
		 	    basicFromQuery = " from  notification_fact_Monthagg a ";
		 	    basicWhereQuery = basicWhereQuery+ " and a.TimeCount = "+month+" and a.Year = "+year+"";
		 	   }
		 	   if(Period.equalsIgnoreCase("Last Month")){
		 		  dateUtilObj1  = dateUtilObj1. getCurrentDateUtility(currentDate);
		 	    dateUtilObj  = dateUtilObj. getPreviousDateUtility (currentDate);
		 	    int month = dateUtilObj. getMonth ();
		 	    int year=0;
		 	    if(dateUtilObj1.getMonth() ==1)
		 	     year = dateUtilObj.getYear();
		 	    else
		 	     year = dateUtilObj.getCurrentYear();
		 	    basicFromQuery = " from  notification_fact_Monthagg a ";
		 	    basicWhereQuery = basicWhereQuery+ " and a.TimeCount = "+month+" and a.Year = "+year+"";
		 	   }
		 	  } 
		 	  else if((Period.equalsIgnoreCase("Quarter"))||(Period.equalsIgnoreCase("Last Quarter"))) {
		 	   Date currentDate = new Date();
		 	   DateUtil dateUtilObj = new DateUtil();
		 	  DateUtil dateUtilObj1 = new DateUtil();
		 	   if(Period.equalsIgnoreCase("Quarter")){
		 	    dateUtilObj  = dateUtilObj. getCurrentDateUtility(currentDate);
		 	    int quarter = dateUtilObj. getQuarter ();
		 	    int year = dateUtilObj. getYear();
		 	    basicFromQuery = " from notification_fact_Quarteragg a ";
		 	    basicWhereQuery = basicWhereQuery+ " and a.TimeCount = "+quarter+" and a.Year = "+year+"";
		 	   }
		 	   if(Period.equalsIgnoreCase("Last Quarter")){
		 		  dateUtilObj1  = dateUtilObj1. getCurrentDateUtility(currentDate);
		 	    dateUtilObj  = dateUtilObj. getPreviousDateUtility(currentDate);
		 	    int quarter = dateUtilObj. getQuarter ();
		 	    int year=0;
		 	    if(dateUtilObj1.getQuarter() ==1)
		 	     year = dateUtilObj.getYear();
		 	    else
		 	     year = dateUtilObj.getCurrentYear();
		 	    basicFromQuery = " from notification_fact_Quarteragg a ";
		 	    basicWhereQuery = basicWhereQuery+ " and a.TimeCount = "+quarter+" and a.Year = "+year+"";
		 	   }
		 	  }
		 	  else if ((Period.equalsIgnoreCase("Year"))||(Period.equalsIgnoreCase("Last Year")))
		 	  {
		 	   Date currentDate = new Date();
		 	   DateUtil dateUtilObj = new DateUtil();
		 	   if(Period.equalsIgnoreCase("Year")){
		 	    dateUtilObj  = dateUtilObj. getCurrentDateUtility(currentDate);
		 	    int year = dateUtilObj. getYear();
		 	    basicFromQuery = " from notification_fact_Yearagg a ";
		 	    basicWhereQuery = basicWhereQuery+ " and a.Year = "+year+"";
		 	   }
		 	   if(Period.equalsIgnoreCase("Last Year")){
		 	    dateUtilObj  = dateUtilObj. getPreviousDateUtility(currentDate);
		 	    int year = dateUtilObj. getYear();
		 	    basicFromQuery = " from notification_fact_Yearagg a ";
		 	    basicWhereQuery = basicWhereQuery+ " and a.Year = "+year+"";
		 	   }
		 	  }
		 	  else if(Period.equalsIgnoreCase("Today")){
		 		  
			 	    basicFromQuery = " from NotificationFactEntity_DayAgg a ";	
					basicWhereQuery = basicWhereQuery+ " and  a.Time_Key = (select max(Time_Key) from a) ";			
					
			 	  
		 	  }
		basicFromQuery=basicFromQuery+" , NotificationDimensionEntity e , TenancyDimensionEntity t ";
		basicFromQuery = basicFromQuery +"  JOIN a.AssetClass_Id d , TenancyBridgeEntity f ";
		
		
		if(!(customAssetGroupIdList==null || customAssetGroupIdList.isEmpty())){
			  String customAssetGroupStringList = conversionObj.getIntegerListString(customAssetGroupIdList).toString();
			  basicFromQuery = basicFromQuery
				+ " , CustomAssetGroupEntity c, AssetCustomGroupMapping h ";
		basicWhereQuery = basicWhereQuery
				+ " and c.group_id = h.group_id and c.group_id in ("
				+ customAssetGroupStringList + ") and "
				+ " h.serial_number = a.SerialNumber ";
	    }
	    if(!(AssetGroupIdList==null || AssetGroupIdList.isEmpty())){
	          String assetGroupStringList = conversionObj.getIntegerListString(AssetGroupIdList).toString();
	          basicWhereQuery = basicWhereQuery + " and d.assetGroupId in ( "+ assetGroupStringList +" )"; 
	    }
          if(flag==true){	    
	    	basicWhereQuery = basicWhereQuery + "and e.Notification_Type_Name like:Service";
	    }
       
          if(!(eventTypeIdList==null || eventTypeIdList.isEmpty())){
        	  String eventTypeIdStringList = conversionObj.getIntegerListString(eventTypeIdList).toString();
        	  basicWhereQuery = basicWhereQuery + " and e.Notification_Type_Id in ("+ eventTypeIdStringList +")";
        	  
          }
          if(! (eventSeverityList==null || eventSeverityList.isEmpty())) {
        	  String eventSeverityListAsString = conversionObj.getStringList(eventSeverityList).toString();
        	  basicWhereQuery = basicWhereQuery + " and e.Severity in ("+ eventSeverityListAsString +")";
          }
          
			if(!(AssetTypeIdList==null || AssetTypeIdList.isEmpty())){
		          String assetTypeStringList = conversionObj.getIntegerListString(AssetTypeIdList).toString();
		          basicWhereQuery = basicWhereQuery + " and d.assetTypeId in ( "+ assetTypeStringList +" )"; 
		    }
			
			//check for own stock
			if(isOwnStock){
				basicWhereQuery = basicWhereQuery + " AND f.childId IN ("+tenancyIdStringList+")";
			}
			
	    finalQuery = basicSelectQuery + basicFromQuery + basicWhereQuery;
		 iLogger.info("Final Query : " + finalQuery);
		 Session session= null;
		 ServiceDueOverDueImpl implObj = new ServiceDueOverDueImpl();
		 try{
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction(); 		    
		    Query query = session.createQuery(finalQuery);
		    if(flag==true){
		    String Service = "Service";
		    query.setString("Service", Service);
		    }
		    Iterator itr = query.list().iterator();
		    Object[] result=null;
		    
		    while(itr.hasNext()){
		    	result = (Object[]) itr.next();
		    	
		    	if(result[2]!=null){
		    		if(result[2].toString().equalsIgnoreCase("Yellow")){
		    			if(result[3]!=null){
			    			duecount=duecount+(Integer)result[3];
			    		}	
		    		}	    		    		
			    	else if(result[2].toString().equalsIgnoreCase("Red")){
			    		if(result[3]!=null){
			    			overduecount=overduecount+(Integer)result[3];
			    		}		    		
			    	}    	
		    	}
		    }
		   
			implObj.setServiceDueCount(duecount);
			implObj.setServiceOverduecount(overduecount);
			iLogger.info("Due count in BO : " + duecount);  
			iLogger.info("Overdue count in BO:" + implObj.getServiceOverduecount());
		}
		finally{
			if(session.getTransaction().isActive()){      
				session.getTransaction().commit();
			}
			if(session.isOpen()){
				session.flush();
			    session.close();
			 } 
		}
		long endTime=System.currentTimeMillis();
		iLogger.info("Total execution time "+ String.valueOf(endTime-startTime)+ "(ms)");
		return implObj;
	}
		
		/**
		 * method to get active alerts from OLTP
		 * @param tenancyIdStringList
		 * @param AssetTypeIdList
		 * @param eventSeverityList
		 * @param flag
		 * @return impl object
		 */
		public ServiceDueOverDueImpl getActiveAlerts(String tenancyIdStringList,List<Integer> eventTypeIdList,List<String> eventSeverityList,boolean flag,
				boolean isOwnStock){
			ServiceDueOverDueImpl implObj = new ServiceDueOverDueImpl();
			Logger iLogger = InfoLoggerClass.logger;
	    
			long startTime = System.currentTimeMillis();	
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction(); 
			int dueCount = 0;
			int overdueCount = 0;
					
			try{
				String basicSelectQuery = "SELECT a.eventSeverity";	    	
		    	String basicFromQuery =" FROM AssetEventEntity a,AccountTenancyMapping atm, AssetEntity asset,TenancyBridgeEntity tb";
		    	String basicWhereQuery = " WHERE a.serialNumber = asset.serial_number" +
 				" AND tb.parentId IN ("+tenancyIdStringList+") AND tb.childId = atm.tenancy_id AND atm.account_id = asset.primary_owner_id AND asset.active_status=1" +
 				" AND a.activeStatus = 1";
//		    	Keerthi : 05/02/14 : grouping on serial no. and event id : taking individual count.
//		    	this fix to match alerts tab count with overview chart count
		    	String groupBy = " GROUP BY a.serialNumber,a.eventId" ;
		    	
		    	//apply filters, if any
		    	ListToStringConversion conversionObj = new ListToStringConversion();
		    	if(! (eventSeverityList==null || eventSeverityList.isEmpty())) {
		        	  String eventSeverityListAsString = conversionObj.getStringList(eventSeverityList).toString();
		        	  basicWhereQuery = basicWhereQuery + " AND a.eventSeverity IN ("+ eventSeverityListAsString +")";
		         }		          
				if(!(eventTypeIdList==null || eventTypeIdList.isEmpty())){
				      String eventTypeIdString = conversionObj.getIntegerListString(eventTypeIdList).toString();
				      basicWhereQuery = basicWhereQuery + " AND a.eventTypeId IN ( "+ eventTypeIdString +" )"; 
				 }
				if(flag){//only service alerts					
					basicWhereQuery = basicWhereQuery + " AND a.eventTypeId IN " +
							"(SELECT et.eventTypeId FROM EventTypeEntity et WHERE et.eventTypeName='Service')";
				}
//				Keerthi : 28/10/13 : own stock changes
				//check for own stock
				if(isOwnStock){
					basicWhereQuery = basicWhereQuery + " AND tb.childId IN ("+tenancyIdStringList+")";
				}
		    	String finalQuery = basicSelectQuery+basicFromQuery+basicWhereQuery+groupBy;
		    			    
			    Query query = session.createQuery(finalQuery);
			    Iterator itr = query.list().iterator();
			   String severity = null;
			    
			    while(itr.hasNext()){
			    	severity = (String) itr.next();
			    	if(severity!=null){
			    		if(severity.equalsIgnoreCase("Yellow")){
			    			++dueCount;
			    		}	    		    		
				    	else if(severity.equalsIgnoreCase("Red")){
				    		++overdueCount;
				    	}    	
			    	}
			    }
			    implObj.setServiceDueCount(dueCount);
				implObj.setServiceOverduecount(overdueCount);
			    iLogger.info("Active Alerts : dueCount = "+ dueCount);
			    iLogger.info("Active Alerts : overdueCount = "+ overdueCount);
			}
			finally{
				if(session.getTransaction().isActive()){      
					session.getTransaction().commit();
				}
				if(session.isOpen()){
					session.flush();
				    session.close();
				 } 
			}
			long endTime=System.currentTimeMillis();
			iLogger.info("Total execution time "+ String.valueOf(endTime-startTime)+ "(ms)");
			return implObj;
		}
		
		/**
		 * method to get active alerts for alert type wise from OLTP
		 * @param tenancyIdStringList
		 * @param AssetTypeIdList
		 * @param eventSeverityList
		 * @param flag
		 * @return impl list object
		 */
		public List<NotificationSummaryImpl> getActiveAlertsAlertTypeWise(String tenancyIdStringList,List<String> eventSeverityList, List<Integer> notificationTypeIdList,boolean isOwnStock){
			List<NotificationSummaryImpl> implList = new ArrayList<NotificationSummaryImpl>();
			Logger iLogger = InfoLoggerClass.logger;
	    	
			long startTime = System.currentTimeMillis();	
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction(); 
			
			/*select ae.Event_Type_ID,et.Event_Type_Name,ae.Event_Severity,count(*)
			from asset_event ae,event_type et
			where ae.Serial_Number IN
			(
			  select ao.Serial_Number
			  from account_tenancy ate, asset_owners ao,tenancy_bridge tb
			  where tb.Parent_Id IN (101) and tb.Child_ID = ate.Tenancy_ID
					and ate.Account_ID = ao.Account_ID
			)
			AND ae.Active_Status=1
			AND ae.Event_Type_ID = et.Event_Type_ID
			group by et.Event_Type_Name*/
			
			try{
				String basicSelectQuery = "SELECT a.eventTypeId,et.eventTypeName,count(*)";	    	
		    	String basicFromQuery =" FROM AssetEventEntity a,EventTypeEntity et,AccountTenancyMapping atm, AssetEntity asset,TenancyBridgeEntity tb";
		    	/*String basicWhereQuery = " WHERE a.serialNumber IN (" +
			 				"SELECT aam.serialNumber" +
			 				" FROM AccountTenancyMapping atm, AssetAccountMapping aam,TenancyBridgeEntity tb" +
			 				" WHERE tb.parentId IN ("+tenancyIdStringList+") AND tb.childId = atm.tenancy_id AND atm.account_id = aam.accountId)" +
			 		" AND a.activeStatus = 1 AND a.eventTypeId = et.eventTypeId";*/
		    	//DefectID: - Asset Owner Changes - 2013-07-19 - Rajani Nagaraju
		    	String basicWhereQuery = " WHERE a.serialNumber = asset.serial_number" +
 				" AND tb.parentId IN ("+tenancyIdStringList+") AND tb.childId = atm.tenancy_id AND atm.account_id = asset.primary_owner_id" +
 				" AND a.activeStatus = 1 AND a.eventTypeId = et.eventTypeId AND asset.active_status=1 and a.partitionKey =1 ";
		    	String groupBy = " GROUP BY et.eventTypeName";
		    	
		    	//apply filters, if any
		    	ListToStringConversion conversionObj = new ListToStringConversion();
		    	if(! (eventSeverityList==null || eventSeverityList.isEmpty())) {
		        	  String eventSeverityListAsString = conversionObj.getStringList(eventSeverityList).toString();
		        	  basicWhereQuery = basicWhereQuery + " AND a.eventSeverity IN ("+ eventSeverityListAsString +")";
		         }		          
							
				// Defect ID : 1246 : Juhi
				if(!(notificationTypeIdList==null || notificationTypeIdList.isEmpty())){
		        	  String eventTypeIdStringList1 = conversionObj.getIntegerListString(notificationTypeIdList).toString();
		        	  basicWhereQuery = basicWhereQuery + " and a.eventTypeId in ("+ eventTypeIdStringList1 +")";
		        	  
		          }
//				Keerthi : 28/10/13 : own stock changes
				if(isOwnStock){
					basicWhereQuery = basicWhereQuery + " AND tb.childId IN ("+tenancyIdStringList+")";
				}
		    	String finalQuery = basicSelectQuery+basicFromQuery+basicWhereQuery+groupBy;
		    			    
			    Query query = session.createQuery(finalQuery);
			    Iterator itr = query.list().iterator();
			    Object[] result=null;
			    EventTypeEntity etEntity = null;
			    NotificationSummaryImpl impObj = null;
				while(itr.hasNext()){				    	
					impObj = new NotificationSummaryImpl();
					result = (Object[]) itr.next();			
					if(result[0]!=null){
						etEntity = (EventTypeEntity)result[0];
						impObj.setNotificationTypeIdList(etEntity.getEventTypeId());
					}
					if(result[1]!=null){
						impObj.setNotificationTypeNameList((String)result[1]);
					}
					if(result[2]!=null){
						impObj.setNotificationcount(((Long)result[2]).intValue());
					}					
					implList.add(impObj);		
				}	
			    
			}
			finally{
				if(session.getTransaction().isActive()){      
					session.getTransaction().commit();
				}
				if(session.isOpen()){
					session.flush();
				    session.close();
				 } 
			}
			long endTime=System.currentTimeMillis();
			iLogger.info("Total execution time "+ String.valueOf(endTime-startTime)+ "(ms)");
			return implList;
		}
	
		/**
		 * method to get alert threshold settings for the user
		 * @param loginId
		 * @return List<AlertThresholdImpl>
		 */
	public List<AlertThresholdImpl> getAlertThresholdSettings(String loginId)
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		long startTime = System.currentTimeMillis();
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		List<AlertThresholdImpl> alertThresholdList = new LinkedList<AlertThresholdImpl>();
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

		//DF20170130 @Roopa for Role based alert implementation
		DateUtil utilObj=new DateUtil();

		List<String> alertCodeList= utilObj.roleAlertMapDetails(loginId,0, "Display");

		ListToStringConversion conversion = new ListToStringConversion();

		StringBuilder alertCodeListAsString=conversion.getStringList(alertCodeList);


		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		HashMap<String,AlertThresholdImpl> eventNameImplObjMap = new HashMap<String,AlertThresholdImpl>();

		Query query =null;
		try{

			query = session.createQuery("from EventEntity a where a.eventCode in ("+alertCodeListAsString+")");
			Iterator itr = query.list().iterator();

			AlertThresholdImpl implObj = null;

			while(itr.hasNext())
			{
				EventEntity event = (EventEntity)itr.next();

				if( !(eventNameImplObjMap.containsKey(event.getEventName())) )
				{
					implObj = new AlertThresholdImpl();
					implObj.setEventTypeId(event.getEventTypeId().getEventTypeId());
					implObj.setEventTypeName(event.getEventTypeId().getEventTypeName());
					implObj.setEventId(event.getEventId());
					implObj.setEventName(event.getEventName());
				}

				else
				{
					implObj = eventNameImplObjMap.get(event.getEventName());

				}

				if(event.getEventSeverity().equalsIgnoreCase("RED"))
				{
					implObj.setRedThreshold(true);
					implObj.setRedThresholdVal(event.getFrequency());
				}
				else if (event.getEventSeverity().equalsIgnoreCase("YELLOW"))
				{
					implObj.setYellowThreshold(true);
					implObj.setYellowThresholdVal(event.getFrequency());
				}

				eventNameImplObjMap.put(event.getEventName(), implObj);

			}


			for(int i=0; i<eventNameImplObjMap.size(); i++)
			{
				AlertThresholdImpl impl = (AlertThresholdImpl) eventNameImplObjMap.values().toArray()[i];

				alertThresholdList.add(impl);
			}
			/*query = session.createQuery("from EventEntity where eventTypeId=1");
			EventEntity businessEvents = null; AlertThresholdImpl alertThresholdImpl = null;
			Iterator iter=query.list().iterator();
			while(iter.hasNext()){				
				 businessEvents = (EventEntity)iter.next();
				 alertThresholdImpl = new AlertThresholdImpl();
				 alertThresholdImpl.setEventName(businessEvents.getEventName());
				 alertThresholdImpl.setEventId(businessEvents.getEventId());
				 alertThresholdImpl.setEventTypeId(businessEvents.getEventTypeId().getEventTypeId());
				 alertThresholdImpl.setEventTypeName(businessEvents.getEventTypeId().getEventTypeName());

				 if(businessEvents.getEventSeverity().equalsIgnoreCase("Red")){
						 alertThresholdImpl.setRedThreshold(true);
						 alertThresholdImpl.setRedThresholdVal(businessEvents.getFrequency());
				 }
				 else if(businessEvents.getEventSeverity().equalsIgnoreCase("Yellow")){
						 alertThresholdImpl.setYellowThreshold(true);
						 alertThresholdImpl.setYellowThresholdVal(businessEvents.getFrequency());
				 }	
				 alertThresholdList.add(alertThresholdImpl);
			}
			query = session.createQuery("SELECT t1.eventId,t1.eventName, t1.eventTypeId,t1.frequency, t1.eventSeverity,"+
									"t2.ConditionId FROM EventEntity t1, EventCondition t2 where t1.eventTypeId !=1"+
									"and t1.eventId=t2.EventId");
			iter=query.list().iterator();
			String severity=null; EventTypeEntity ete=null;ConditionsEntity ce=null;
			while(iter.hasNext())
			{
				Object[] result = (Object[]) iter.next();
				 alertThresholdImpl = new AlertThresholdImpl();
				 alertThresholdImpl.setEventId((Integer)result[0]);
				 alertThresholdImpl.setEventName((String)result[1]);
				 ete=(EventTypeEntity)result[2];
				 alertThresholdImpl.setEventTypeId(ete.getEventTypeId());
				 alertThresholdImpl.setEventTypeName(ete.getEventTypeName());
				 severity=(String)result[4];

				ce=(ConditionsEntity)result[5];
					 if(severity.equalsIgnoreCase("Red")){
						 alertThresholdImpl.setRedThreshold(true);
						 alertThresholdImpl.setRedThresholdVal(ce.getConditionValue());
					 }
					 if(severity.equalsIgnoreCase("Yellow")){
						 alertThresholdImpl.setYellowThreshold(true);
						 alertThresholdImpl.setYellowThresholdVal(ce.getConditionValue());
					 }
					 alertThresholdList.add(alertThresholdImpl);
			}*/
		}	

		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}

		finally{
			if(session.getTransaction().isActive()){      
				session.getTransaction().commit();
			}
			if(session.isOpen()){
				session.flush();
				session.close();
			} 
		}
		iLogger.info("Total execution time "+String.valueOf(System.currentTimeMillis()-startTime + "(ms)"));
		return alertThresholdList;		
	}



		
		/**
		 * method to set alert threshold settings
		 * @param respObj
		 * @return String
		 * @throws CustomFault
		 */
		public String setAlertThresholdSettings(List<AlertThresholdRespContract> respObj) throws CustomFault{
			Logger iLogger = InfoLoggerClass.logger;
	    	
			long startTime = System.currentTimeMillis();
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();  
			int update = 0;Query query = null;AlertThresholdRespContract alertData=null;
			String eventName=null; boolean isYellowThreshold; boolean isRedThreshold; int yellowThresholdVal; int redThresholdVal;
			try{
				Iterator<AlertThresholdRespContract> iter=respObj.iterator();
				while(iter.hasNext()){
					alertData = iter.next();
					eventName=alertData.getEventName();
					isYellowThreshold=alertData.isYellowThreshold();
					isRedThreshold=alertData.isRedThreshold();
					yellowThresholdVal=alertData.getYellowThresholdVal();
					redThresholdVal=alertData.getRedThresholdVal();
				
				query = session.createQuery("from EventEntity where eventName = '"+eventName+"'");
				
				Iterator itr = query.list().iterator();		
				while(itr.hasNext())
				{
					update=1;
					EventEntity businessEvents = (EventEntity)itr.next();			
					
					
					if(businessEvents.getEventSeverity().equalsIgnoreCase("Yellow")){
						if(isYellowThreshold==true){
							 if(businessEvents.getEventTypeId().getEventTypeName().equalsIgnoreCase("Service Alerts"))
							 {
								 businessEvents.setFrequency(yellowThresholdVal);
								 session.update(businessEvents);
							 }
							 else{
						
								 Query q1= session.createQuery("from EventCondition where EventId = " + businessEvents.getEventId());
								 Iterator it2 = q1.list().iterator();
								 while(it2.hasNext()){
									 EventCondition eventCondition = (EventCondition) it2.next();
									 ConditionsEntity condition =eventCondition.getConditionId();
									 //Merge here ....
									  condition.setConditionValue(yellowThresholdVal);
									  session.update(condition);
									  
								}
							 }
						}
					else{
							if(businessEvents.getEventTypeId().getEventTypeName().equalsIgnoreCase("Service Alerts"))
							 {
								businessEvents.setFrequency(-1);
								 session.update(businessEvents);
							 }else{
								 Query query1= session.createQuery("from EventCondition where EventId = " + businessEvents.getEventId());
								 
								 Iterator ittr2 = query1.list().iterator();
								 while(ittr2.hasNext()){								 
									 EventCondition eventCondition = (EventCondition) ittr2.next();
									 ConditionsEntity condition =eventCondition.getConditionId();
									 
									 //Merge here ....
									 ConditionsEntity condition1 = (ConditionsEntity)session.merge(condition);
									 condition1.setConditionValue(-1);
									 session.update(condition1);									 
							 }
						}
					}				
				}else if(businessEvents.getEventSeverity().equalsIgnoreCase("Red")){

					if(isRedThreshold==true){
						 if(businessEvents.getEventTypeId().getEventTypeName().equalsIgnoreCase("Service Alerts"))
						 {						 
							 businessEvents.setFrequency(redThresholdVal);						
							 session.getTransaction().begin();
							 EventEntity editBusinessEntity= (EventEntity) session.merge(businessEvents);		 
								 
							 session.save(editBusinessEntity);
						 }
						 else{
							 
							 Query qu1= session.createQuery("from EventCondition where EventId = " + businessEvents.getEventId());
							 Iterator itlist = qu1.list().iterator();
							 while(itlist.hasNext()){
								 EventCondition eventCondition = (EventCondition) itlist.next();
								
								 ConditionsEntity condition =eventCondition.getConditionId();
								
								 //Merge here ....
								 condition.setConditionValue(redThresholdVal);
								 session.update(condition);
							}
						 }
					}else{
						if(businessEvents.getEventTypeId().getEventTypeName().equalsIgnoreCase("Service Alerts"))
						 {
							
							 businessEvents.setFrequency(-1);
							 session.update(businessEvents);
						 }else{
							 Query eventquery= session.createQuery("from EventCondition where EventId = " + businessEvents.getEventId());
							 Iterator eventIdItr = eventquery.list().iterator();
							 while(eventIdItr.hasNext()){
								 EventCondition eventCondition = (EventCondition) eventIdItr.next();
								 ConditionsEntity condition =eventCondition.getConditionId();
								 //Merge here ....							
								 condition.setConditionValue(-1);
								 session.update(condition);
						 }
					}
				}
				}
				if(update==0)
				{
					throw new CustomFault("Event Name is not present in the Business Events");
				}	
			
		}}
			}
			finally{
				if(session.getTransaction().isActive()){      
					session.getTransaction().commit();
				 }
				if(session.isOpen()){ 
					session.flush();
					session.close();
				 } 
			}
			long endTime=System.currentTimeMillis();
			iLogger.info("Total execution time "+ String.valueOf(endTime-startTime)+ "(ms)");
			
				return "Success";
		}
	
		/**
	 * 
	 * @param period which may be month,year,Quater,Today
	 * @param notificationIdList list of id's for notification
	 * @param notificationTypeIdList list of typeId for notification
	 * @param assetTypeIdList list of typeId of Asset
	 * @param assetGroupIdList list of groupId for Asset
	 * @param landmarkIdList List of landmark Id's
	 * @param landmarkCategoryIdList List of landmarkCategory Id's
	 * @param tenancyIdList List of tenancyId
	 * @return listNotificationSummary which return the list of summary on notification
	 * 
	 */
	
		public List<NotificationSummaryImpl> getNotificationSummary(String period, String contactId, List<Integer> notificationIdList,List<Integer> notificationTypeIdList,List<Integer> assetTypeIdList,List<Integer> assetGroupIdList,List<Integer> tenancyIdList,List<Integer> eventTypeIdList,List<String> eventSeverityList,
				boolean ownStock,boolean activeAlerts)	{
			NotificationSummaryImpl impObj = null;
			List<NotificationSummaryImpl> listNotificationSummary = new LinkedList<NotificationSummaryImpl>();
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;

			String MDAassetTypeCodeList=null;
			String MDAassetGroupCodeList=null;
			String MDAeventTypeCodeList=null;
			String MDAeventSeverityList=null;
			String MDAaccountIdListAsString=null;
			String MDAoutput=null;
			String MDAresult=null;
			Connection prodConnection = null;
			Statement statement = null;
			ResultSet rs = null;

			String basicQueryString=null;
			String fromQuery=null;
			String whereQuery=null;
			String groupByQuery=null;

			StringBuilder tenancyListAsString=null;
			StringBuilder eventTypeIdListAsString=null;
			StringBuilder eventSeverityListAsString=null;
			StringBuilder alertCodeListAsString=null;
			
			
			StringBuilder assetTypeIdListAsString = null;
			StringBuilder assetGroupIdListAsString = null;
			
			
			String finalQuery=null;

			ListToStringConversion utilClass=new ListToStringConversion();
			
			//CR337.sn
			String connIP=null;
			String connPort=null;
			Properties prop = null;
			try{
				prop = CommonUtil.getDepEnvProperties();
				connIP = prop.getProperty("MDA_ServerIP");
				connPort = prop.getProperty("MDA_ServerPort");
				iLogger.info("EventDetailsBO : MDA_ServerIP:" +connIP +" :: MDA_ServerPort:"+connPort);
			}catch(Exception e){
				fLogger.fatal("EventDetailsBO : getNotificationSummary : " +
						"Exception in getting Server Details for MDA Layer from properties file: " +e);
			}
			//CR337.en
			

			if (tenancyIdList != null && tenancyIdList.size() > 0) {

				tenancyListAsString = utilClass.getIntegerListString(tenancyIdList);

			}

			if (eventTypeIdList != null && eventTypeIdList.size() > 0) {

				eventTypeIdListAsString = utilClass
						.getIntegerListString(eventTypeIdList);
				
			}

			if (eventSeverityList != null && eventSeverityList.size() > 0) {

				eventSeverityListAsString = utilClass
						.getStringList(eventSeverityList);
			}

			if (assetTypeIdList != null && assetTypeIdList.size() > 0) {

				assetTypeIdListAsString = utilClass
						.getIntegerListString(assetTypeIdList);
			}

			if (assetGroupIdList != null && assetGroupIdList.size() > 0) {

				assetGroupIdListAsString = utilClass
						.getIntegerListString(assetGroupIdList);
			}

			Session session = HibernateUtil.getSessionFactory().openSession();

			try {
				List<Integer> AccountIdList = new ArrayList<Integer>();

				List<String> AccountCodeList = new ArrayList<String>();

				AccountEntity account = null;

				Query accountQ = session
						.createQuery(" select at.account_id from AccountTenancyMapping at where at.tenancy_id in("
								+ tenancyListAsString + ") ");
				Iterator accItr = accountQ.list().iterator();
				while (accItr.hasNext()) {
					account = (AccountEntity) accItr.next();
					AccountIdList.add(account.getAccount_id());

					//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

					//AccountCodeList.add(account.getAccountCode());
					AccountCodeList.add(account.getMappingCode());
				}

				if (session != null && session.isOpen()) {
					session.close();
				}

				StringBuilder AccountIdListAsString = utilClass
						.getIntegerListString(AccountIdList);
				StringBuilder AccountCodelistAsString = utilClass
						.getStringList(AccountCodeList);
				StringBuilder MDAaccountIdListAsStringBuilder=utilClass.getStringWithoutQuoteList(AccountCodeList);
				MDAaccountIdListAsString=utilClass.removeLastComma(MDAaccountIdListAsStringBuilder.toString());
				// DF20170130 @Roopa for Role based alert implementation
				DateUtil utilObj = new DateUtil();

				List<String> alertCodeList = utilObj.roleAlertMapDetails(contactId,
						0, "Display");

				if (alertCodeList != null && alertCodeList.size() > 0)
					alertCodeListAsString = utilClass.getStringList(alertCodeList);

				if (period == null) {

					basicQueryString = "select ae.Event_Type_ID, et.Event_Type_Name, count(*) as notificationcount";
					fromQuery = " from asset_event ae, asset_owner_snapshot aos, event_type et ";

					whereQuery = " where aos.Serial_Number=ae.Serial_Number and ae.Event_Type_ID=et.Event_Type_ID and aos.account_ID in ("
							+ AccountIdListAsString + ") ";
					if (activeAlerts == true) {
						whereQuery = whereQuery + " and ae.Active_Status=1 and ae.PartitionKey =1";
					} else {
						whereQuery = whereQuery + " and ae.Active_Status=0 ";
					}

					groupByQuery = " group by ae.Event_Type_ID";

					if (alertCodeListAsString != null) {
						fromQuery = fromQuery + ", business_event be ";
						whereQuery = whereQuery
								+ " and ae.Event_ID=be.Event_ID and be.Alert_Code in("
								+ alertCodeListAsString + ")";

					}

					if (ownStock == true) {
						fromQuery = fromQuery + ", asset am ";
						whereQuery = whereQuery
								+ " and aos.Serial_Number=am.Serial_Number and aos.account_ID=am.Primary_Owner_ID ";
					}

					if (eventTypeIdListAsString != null) {

						whereQuery = whereQuery + " and ae.Event_Type_ID in ("
								+ eventTypeIdListAsString + ") ";
					}

					if (eventSeverityListAsString != null) {

						whereQuery = whereQuery + " and ae.Event_Severity in ("
								+ eventSeverityListAsString + ") ";
					}

					//DF20181227 - KO369761 - Commented reference to remote_monitoring_fact_data_dayagg_json_new.
					/*if ((!(assetGroupIdList == null || assetGroupIdList.isEmpty()))
							|| (!((assetTypeIdList == null) || (assetTypeIdList
									.isEmpty())))) {
						
						//Df20170606 @Roopa pointing right remote day agg table

						fromQuery = fromQuery
								+ " , remote_monitoring_fact_data_dayagg_json_new g, asset_class_dimension a ";
						whereQuery = whereQuery
								+ " and g.Asset_Class_ID=a.Asset_Class_Dimension_ID";
					}*/
					
					/**DF20181227 - KO369761 - for asset group id filter directly we
					 * are checking aos table instead of asset_class dimenstion.
					 */
					if (!((assetGroupIdList == null) || (assetGroupIdList.isEmpty()))) {
						/*whereQuery = whereQuery + " and a.Asset_Group_ID in ( "
								+ assetGroupIdListAsString + " )";*/
						whereQuery = whereQuery + " and aos.asset_group_id in ( "
								+ assetGroupIdListAsString + " )";
						
					}
					
					/** DF20181227 - KO369761 - for asset type id filter directly we
					 * are checking aos table instead of asset_class dimenstion.
					 */
					if (!((assetTypeIdList == null) || (assetTypeIdList.isEmpty()))) {
						/*whereQuery = whereQuery + " and a.Asset_Type_ID in ( "
								+ assetTypeIdListAsString + " )";*/
						whereQuery = whereQuery + " and aos.asset_type_id in ( "
								+ assetTypeIdListAsString + " )";
					}

					finalQuery = basicQueryString + fromQuery + whereQuery
							+ groupByQuery;

					// System.out.println("finalQuery ::"+finalQuery);

					ConnectMySQL connMySql = new ConnectMySQL();
					prodConnection = connMySql.getConnection();
					statement = prodConnection.createStatement();
					rs = statement.executeQuery(finalQuery);

					while (rs.next()) {

						impObj = new NotificationSummaryImpl();

						impObj.setNotificationTypeIdList(rs.getInt("Event_Type_ID"));
						impObj.setNotificationTypeNameList(rs
								.getString("Event_Type_Name"));
						impObj.setNotificationcount(rs.getInt("notificationcount"));

						listNotificationSummary.add(impObj);

					}

				} else {
					if(period.equalsIgnoreCase("Year")){
					basicQueryString = "select ai.AlertTypeCode, ai.AlertType, count(*) as notificationcount";
					fromQuery = " from alertInsight_detail ai ";

					whereQuery = " where (ai.ZonalCode in ("
							+ AccountCodelistAsString + ") or ai.DealerCode in("
							+ AccountCodelistAsString + ") or ai.CustCode in("
							+ AccountCodelistAsString + ")) ";

					groupByQuery = " group by ai.AlertTypeCode";

					if (alertCodeListAsString != null) {

						List<Integer> eventIdList = utilObj
								.getEventIdListForAlertCodes(alertCodeListAsString);

						StringBuilder EventIdListAsString = utilClass
								.getIntegerListString(eventIdList);

						whereQuery = whereQuery + " and ai.AlertCode in("
								+ EventIdListAsString + ")";

					}

					int dateAsNum = 0;
					String Timeperiod = null;
					Calendar cal = Calendar.getInstance();

					if (period.equalsIgnoreCase("Week")) {
						Timeperiod = "Week";
						dateAsNum = cal.get(Calendar.WEEK_OF_YEAR);
					} else if (period.equalsIgnoreCase("Last Week")) {
						Timeperiod = "Week";
						cal.set(Calendar.WEEK_OF_YEAR,
								cal.get(Calendar.WEEK_OF_YEAR) - 1);
						dateAsNum = cal.get(Calendar.WEEK_OF_YEAR);
					}

					else if (period.equalsIgnoreCase("Month")) {
						Timeperiod = "Month";
						dateAsNum = cal.get(Calendar.MONTH);
					} else if (period.equalsIgnoreCase("Last Month")) {
						Timeperiod = "Month";
						cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
						dateAsNum = cal.get(Calendar.MONTH);
					}

					else if (period.equalsIgnoreCase("Quarter")) {
						Timeperiod = "Quarter";

						dateAsNum = ((cal.get(Calendar.MONTH))) / 3 + 1;
					}

					else if (period.equalsIgnoreCase("Last Quarter")) {
						Timeperiod = "Quarter";
						cal.set((cal.get(Calendar.MONTH)), -1);
						dateAsNum = ((cal.get(Calendar.MONTH))) / 3 + 1;
					} else if (period.equalsIgnoreCase("Year")) {
						Timeperiod = "Year";
						dateAsNum = cal.get(Calendar.YEAR);
					} else if (period.equalsIgnoreCase("Last Year")) {
						Timeperiod = "Year";
						cal.set((cal.get(Calendar.YEAR)), -1);
						dateAsNum = cal.get(Calendar.YEAR);
					}

					if (dateAsNum != 0) {

						if (Timeperiod != null) {
							if (Timeperiod.equalsIgnoreCase("Week")) {

								whereQuery = whereQuery + " and ai.TxnWeek="
										+ dateAsNum + "";
							}
							if (Timeperiod.equalsIgnoreCase("Month")) {

								whereQuery = whereQuery + " and ai.TxnMonth="
										+ dateAsNum + "";
							}
							if (Timeperiod.equalsIgnoreCase("Quarter")) {

								whereQuery = whereQuery + " and ai.TxnQuarter="
										+ dateAsNum + "";
							}
							if (Timeperiod.equalsIgnoreCase("Year")) {

								whereQuery = whereQuery + " and ai.TxnYear="
										+ dateAsNum + "";
							}
						}

					}

					ListToStringConversion conversionObj = new ListToStringConversion();
					DateUtil utl = new DateUtil();
					if (assetTypeIdListAsString != null) {
						StringBuilder assetTypeCodeListAsString = null;

						List<String> assetTypeCodeList = utl
								.getAssetTypeIdList(assetTypeIdListAsString);
						if (assetTypeCodeList != null
								&& assetTypeCodeList.size() > 0)
							assetTypeCodeListAsString = conversionObj
									.getStringList(assetTypeCodeList);
						whereQuery = whereQuery + " and ai.ModelCode in ("
								+ assetTypeCodeListAsString + ") ";
					}

					if (assetGroupIdListAsString != null) {
						List<String> assetGroupCodeList = utl
								.getAssetGroupIdList(assetGroupIdListAsString);
						StringBuilder assetGroupCodeListAsString = null;
						if (assetGroupCodeList != null
								&& assetGroupCodeList.size() > 0)
							assetGroupCodeListAsString = conversionObj
									.getStringList(assetGroupCodeList);
						whereQuery = whereQuery + " and ai.ProfileCode in ("
								+ assetGroupCodeListAsString + ") ";

					}

					if (eventTypeIdListAsString != null) {

						whereQuery = whereQuery + " and ai.AlertTypeCode in ("
								+ eventTypeIdListAsString + ") ";
					}

					if (eventSeverityListAsString != null) {
						whereQuery = whereQuery + " and ai.AlertSeverity in ("
								+ eventSeverityListAsString + ") ";
					}

					finalQuery = basicQueryString + fromQuery + whereQuery
							+ groupByQuery;

					 System.out.println("finalQuery ::"+finalQuery);

					ConnectMySQL connMySql = new ConnectMySQL();
					prodConnection = connMySql.getProdDb2Connection();
					statement = prodConnection.createStatement();

					rs = statement.executeQuery(finalQuery);

					while (rs.next()) {

						impObj = new NotificationSummaryImpl();

						impObj.setNotificationTypeIdList(rs.getInt("AlertTypeCode"));
						impObj.setNotificationTypeNameList(rs
								.getString("AlertType"));
						impObj.setNotificationcount(rs.getInt("notificationcount"));

						listNotificationSummary.add(impObj);
					}
					}
					else{

						//MOOL DB changes
						Connection prodConn = null;
						Statement stmnt = null;
						ResultSet res = null;
						try{
						ConnectMySQL connSql = new ConnectMySQL();
						prodConn = connSql.getConnection();
						stmnt = prodConn.createStatement();
						HashMap<String,Integer> MDAOutputMap = null;
						DateUtil dateUtil1 = new DateUtil();
						ListToStringConversion conversionObj = new ListToStringConversion();
						//For accountFilter filling
						if(AccountIdList!=null && AccountIdList.size()>0){
								String acntTncyQuery = "select Tenancy_Type_Name from tenancy_type where Tenancy_Type_ID=(select Tenancy_Type_ID from tenancy where Tenancy_ID=(select Tenancy_ID from account_tenancy where Account_ID="
										+ AccountIdList.get(0) + "))";
						res=stmnt.executeQuery(acntTncyQuery);
						String tenancy_type_name="";
						if(res.next())
						{
							tenancy_type_name=res.getString("Tenancy_Type_Name");
						}
						String accountFilter="";
						if(tenancy_type_name!=null && !tenancy_type_name.equalsIgnoreCase(""))
						{
							if(tenancy_type_name.equalsIgnoreCase("Global"))
							{
								accountFilter=null;
							}
							else if(tenancy_type_name.equalsIgnoreCase("Regional"))
							{
								accountFilter="RegionCode";
							}
							else if(tenancy_type_name.equalsIgnoreCase("Zonal"))
							{
								accountFilter="ZonalCode";
							}
							else if(tenancy_type_name.equalsIgnoreCase("Dealer"))
							{
								accountFilter="DealerCode";
							}
							else if(tenancy_type_name.equalsIgnoreCase("Customer"))
							{
								accountFilter="CustCode";
							}
							else
							{
								accountFilter="";
							}
							int MDAstock=0;
							if(ownStock)
							{
								MDAstock=1;
							}
							if (assetTypeIdListAsString != null) {
								StringBuilder assetTypeCodeListAsString = null;

								List<String> assetTypeCodeList = dateUtil1
										.getAssetTypeIdList(assetTypeIdListAsString);
								//System.out.println("assetTypeCodeList "+assetTypeCodeList);
								if (assetTypeCodeList != null
										&& assetTypeCodeList.size() > 0){
									assetTypeCodeListAsString = conversionObj
											.getStringList(assetTypeCodeList);
									StringBuilder MDAassetTypeCodeListBuilder=conversionObj.getStringWithoutQuoteList(assetTypeCodeList);
									MDAassetTypeCodeList=MDAassetTypeCodeListBuilder.toString();
									MDAassetTypeCodeList=ListToStringConversion.removeLastComma(MDAassetTypeCodeList);
									//System.out.println("MDAassetTypeCodeList "+MDAassetTypeCodeList);
								}
								/*whereQuery = whereQuery + " and fi.ModelCode in ("
										+ assetTypeCodeListAsString + ") ";*/
							}

							if (assetGroupIdListAsString != null) {
								List<String> assetGroupCodeList = dateUtil1
										.getAssetGroupIdList(assetGroupIdListAsString);
								//System.out.println("assetGroupCodeList "+assetGroupCodeList);
								StringBuilder assetGroupCodeListAsString = null;
								if (assetGroupCodeList != null
										&& assetGroupCodeList.size() > 0){
									assetGroupCodeListAsString = conversionObj
											.getStringList(assetGroupCodeList);
									StringBuilder MDAassetGroupCodeListBuilder=conversionObj.getStringWithoutQuoteList(assetGroupCodeList);
									MDAassetGroupCodeList=MDAassetGroupCodeListBuilder.toString();
									MDAassetGroupCodeList=ListToStringConversion.removeLastComma(MDAassetGroupCodeList);
									//System.out.println("MDAassetGroupCodeList "+MDAassetGroupCodeList);
									}
							
								/*whereQuery = whereQuery + " and fi.ProfileCode in ("
										+ assetGroupCodeListAsString + ") ";*/

							}
							if(eventSeverityList != null && eventSeverityList.size() > 0)
							{	//System.out.println("in");
								StringBuilder MDAeventSeverityListBuilder=conversionObj.getStringWithoutQuoteList(eventSeverityList);
								MDAeventSeverityList=MDAeventSeverityListBuilder.toString();
								MDAeventSeverityList=ListToStringConversion.removeLastComma(MDAeventSeverityList);
							}
							NotificationSummaryImpl MDAResponse=new NotificationSummaryImpl();
							ObjectMapper mapper = new ObjectMapper();
							

							try{
										String url=
												// Dhiraj K : 20220630 : Change IP for AWS server
												//"http://10.179.12.25:26030/MoolDAReports/LLNotificationSummaryService/getLLNotificationSummary?accountFilter="
												//"http://10.210.196.206:26030/MoolDAReports/LLNotificationSummaryService/getLLNotificationSummary?accountFilter=" //CR337.o
												"http://"+connIP+":"+connPort+"/MoolDAReports/LLNotificationSummaryService/getLLNotificationSummary?accountFilter=" //CR337.n
														+ accountFilter
														+ "&accountIDList="
														+ MDAaccountIdListAsString
														+ "&period="
														+ period
														+ "&modelCodeList="
														+ MDAassetTypeCodeList
														+ "&profileCodeList="
														+ MDAassetGroupCodeList
														+"&alertTypeCodeList="
														+eventTypeIdListAsString
														+"&alertSeverity="
														+MDAeventSeverityList
														+ "&isOwnStock="
														+ MDAstock
														+ "&loginID="
														+ contactId
														+ "&countryCode=null";
										//System.out.println("MDA Notification summary  URL : "+url);
										iLogger.info("MDA Notification summary URL : "+url);
										URL MDAUrl = new URL(url);
										 HttpURLConnection conn =(HttpURLConnection) MDAUrl.openConnection();
										    conn.setRequestMethod("GET"); 
										    conn.setRequestProperty("Accept", "application/json");
											  if (conn.getResponseCode() != 200  && conn.getResponseCode() != 204) {
												  iLogger.info("MDAReports report status: FAILURE for Notification summary Report loginID:"+contactId+" ::Response Code:"+conn.getResponseCode());
												  throw new RuntimeException("Failed : HTTP error code : " +conn.getResponseCode()); 
												  }
											  iLogger.info("MDAReports report status: SUCCESS for Notification summary Report loginID:"+contactId+" ::Response Code:"+conn.getResponseCode());
											  BufferedReader br = new BufferedReader(new  InputStreamReader((conn.getInputStream())));
											  
											  System.out.println("MDA Notification summary Output from Server .... \n");
											  while ((MDAoutput = br.readLine()) != null) { 
												  //System.out.println("MDA json output "+MDAoutput); 
												  iLogger.info("MDA json output "+MDAoutput);
												  MDAresult =MDAoutput; } 
											  MDAOutputMap = new Gson().fromJson(MDAresult, new TypeToken<HashMap<String, Integer>>(){}.getType());
											  //MDAResponse = mapper.convertValue(MDAOutputMap, FleetSummaryImpl.class);
											/*  MDAResponse.setTotalIdleHours(MDAOutputMap.get("idletime"));
											  MDAResponse.setTotalOffHours(MDAOutputMap.get("EngineOffTime"));
											  MDAResponse.setTotalWorkingHours(MDAOutputMap.get("WorkingTime"));*/
											  MDAResponse.setNotificationTypeNameList("Service");
											  MDAResponse.setNotificationcount(MDAOutputMap.get("Service"));
											  MDAResponse.setNotificationTypeIdList(1);
											  MDAResponse.setNotificationTypeNameList("Health");
											  MDAResponse.setNotificationcount(MDAOutputMap.get("Health"));
											  MDAResponse.setNotificationTypeIdList(2);
											  MDAResponse.setNotificationTypeNameList("Utilization");
											  MDAResponse.setNotificationcount(MDAOutputMap.get("Utilization"));
											  MDAResponse.setNotificationTypeIdList(3);
											  MDAResponse.setNotificationTypeNameList("Security");
											  MDAResponse.setNotificationcount(MDAOutputMap.get("Security"));
											  MDAResponse.setNotificationTypeIdList(4);
											  MDAResponse.setNotificationTypeNameList("Landmark");
											  MDAResponse.setNotificationcount(MDAOutputMap.get("Landmark"));
											  MDAResponse.setNotificationTypeIdList(5);
											  //System.out.println("MDAResponse outmap : "+MDAResponse);
											  iLogger.info("MDAResponse outmap : "+MDAResponse);
											  listNotificationSummary.add(MDAResponse);
											  conn.disconnect();
							}catch(Exception e)
							{
								e.printStackTrace();
								fLogger.fatal("Error occured while connecting to Mongo DB "+e.getMessage());
							}
							
							}
						
						
						}
						else{
							fLogger.fatal("AccountIdList is empty");
							throw new CustomFault("AccountIdList is empty");
						}
							
					}catch(Exception e)
					{
						fLogger.fatal("Exception occured "+e.getMessage());
					}
						finally {
							if (res != null)
								try {
									res.close();
								} catch (SQLException e1) {
									e1.printStackTrace();
								}

							if (stmnt != null)
								try {
									stmnt.close();
								} catch (SQLException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}

							if (prodConn != null) {
								try {
									prodConn.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}

						}
					
					}
					}//end of else
			} catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("Exception :" + e.getMessage());
			}

			finally {
				if (rs != null)
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}

				if (statement != null)
					try {
						statement.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				if (prodConnection != null) {
					try {
						prodConnection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

				if (session != null && session.isOpen()) {
					session.close();
				}

			}

			return listNotificationSummary;

		}			

  //************************************************END OF GET NOTIFICATION SUMMARY *********************************************


 
	public String alertGenerationLogic(String serialNumber, Timestamp transactionTime, HashMap<EventEntity, String> eventIdValueMap, boolean getChildAlerts, 
				boolean isEmergencyAlert, String latValue, String longValue, LandmarkEntity landmark, int serviceScheduleId, String currentGPSfix)
	{
		
		Logger iLogger = InfoLoggerClass.logger;
		
    	Logger fLogger = FatalLoggerClass.logger;
    	
    	Logger bLogger = BusinessErrorLoggerClass.logger;
		String status = "SUCCESS";

		List<ContactEntity> smsUsers = new LinkedList<ContactEntity>();
		List<ContactEntity> emailUsers = new LinkedList<ContactEntity>();
		
		String smsNotification = null;
		String emailNotification = null;
		int LandmarkEventTypeId=0;
		
		/*for (EventEntity ee : eventIdValueMap.keySet() ) {
			infoLogger.info(" event name : "+ee.getEventName() +" value :"+eventIdValueMap.get(ee));
		}*/
		
		//Added by Rajani Nagaraju 20130701 - DefectId: 790 - To determine whether the SMS service is enabled for the application or not.
		String SmsServiceCofiguration = null;

		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();


		try
		{
			//get Client Details
			Properties prop = new Properties();
			String clientName=null;
			//DefectId: DF20131031 - Rajani Nagaraju - To Supress the Tow-away SMS/Emails alert, if the alert is already sent in the same KeyOff Cycle
			String TowAwayEventId=null;
			
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

			boolean isSms = false; 
			boolean isEmail = false;
			smsNotification = prop.getProperty("SmsPreferenceCatalog");
			emailNotification = prop.getProperty("EmailPrefrenceCatalog");
			//DefectId: DF20131031 - Rajani Nagaraju - To Supress the Tow-away SMS/Emails alert, if the alert is already sent in the same KeyOff Cycle
			TowAwayEventId=prop.getProperty("TowAwayEventId");
			//Added by Rajani Nagaraju 20130701 - DefectId: 790 - To determine whether the SMS service is enabled for the application or not.
			SmsServiceCofiguration = prop.getProperty("SmsServiceCofiguration");
			LandmarkEventTypeId = Integer.parseInt(prop.getProperty("LandmarkEventTypeId"));


			 if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        session.getTransaction().begin();
            }
			  
			 //get Asset Entity for the given serial Number
			AssetEntity assetEntity = null;
			Query query = session.createQuery("from AssetEntity where serial_number ='"+serialNumber+"' and active_status=true and  client_id="+clientEntity.getClient_id()+"");
			Iterator itr = query.list().iterator();
			while(itr.hasNext())
			{
				assetEntity = (AssetEntity) itr.next();
			}

			//DefectId: DF20131031 - Rajani Nagaraju - To Supress the Tow-away SMS/Emails alert, if the alert is already sent in the same KeyOff Cycle
			for(int w=0; w<eventIdValueMap.size(); w++)
			{
				EventEntity event = (EventEntity) eventIdValueMap.keySet().toArray()[w];
				int generateAlert=1;
				
				if(event.getEventId()== (Integer.parseInt(TowAwayEventId)) )
				{
					//Determine if the tow-away is already present in the given KeyOff Cycle
					Query towAwayQuery = session.createQuery(" from AssetEventEntity where serialNumber='"+serialNumber+"' and activeStatus=1 and partitionKey =1" +
							" and eventId="+event.getEventId());
					Iterator towAwayItr = towAwayQuery.list().iterator();
					while(towAwayItr.hasNext())
					{
						AssetEventEntity assetEvent = (AssetEventEntity)towAwayItr.next();
						generateAlert=0;
						iLogger.info(serialNumber+":"+transactionTime+"  :"+"Supress Tow-away SMS/Email, since it is in the same KeyOff Cycle");
					}
				}
				
				if(generateAlert==0)
				{
					iLogger.info(serialNumber+":"+transactionTime+"  :"+" Generate Tow -away Alert");
					//Only add the data to AssetEvent - Dont generate SMS/Email
					AssetEventEntity assetEvent = new AssetEventEntity();
					assetEvent.setActiveStatus(1);
					assetEvent.setEventGeneratedTime(transactionTime);
					assetEvent.setEventId(event);
					assetEvent.setEventSeverity(event.getEventSeverity());
					assetEvent.setEventTypeId(event.getEventTypeId());
					assetEvent.setSerialNumber(assetEntity);
					//Changes done by Rajani Nagaraju - 20130822 - DefectID:1163
					assetEvent.setServiceScheduleId(serviceScheduleId);
					session.save(assetEvent);
					
					eventIdValueMap.remove(event);
					
					if(eventIdValueMap.size()==0)
						return "SUCCESS";
										
					else
						break;
				}
			}
			//END of DefectId: DF20131031 - Rajani Nagaraju - To Supress the Tow-away SMS/Emails alert, if the alert is already sent in the same KeyOff Cycle
			
			//Determine the mode of the alert the Admin has set for the received eventList
			for(int i=0; i<eventIdValueMap.size(); i++)
			{
				
				EventEntity eventEntity = (EventEntity)eventIdValueMap.keySet().toArray()[i];
				if(eventEntity.isSms()==true)
				{
					isSms = true;
				}
				if(eventEntity.isEmail()==true)
				{
					isEmail = true;
				}
			
			}
			
			if(! (session.isOpen() ))
			{
			    session = HibernateUtil.getSessionFactory().getCurrentSession();
			    session.getTransaction().begin();
			}

			//Added by Rajani Nagaraju 20130701 - DefectId: 790 - To determine whether the SMS service is enabled for the application or not.
			Query configQuery = session.createQuery(" from ConfigAppEntity where services like '"+SmsServiceCofiguration+"'");
			Iterator configItr = configQuery.list().iterator();
			while(configItr.hasNext())
			{
			            ConfigAppEntity configAppObj = (ConfigAppEntity)configItr.next();
			            if(!(configAppObj.isStatus()))
			            {
			                        isSms=false;
			            }
			}
			
			

			//get the contacts for SMS
			if(isSms)
			{
				//get the data from EventSubscribers for the given serial number
				Query query1 = session.createQuery("from EventSubscriptionMapping where serialNumber = '"+serialNumber+"' ");
				Iterator itr1 = query1.list().iterator();
				while(itr1.hasNext())
				{
					EventSubscriptionMapping eventSubscribers = (EventSubscriptionMapping)itr1.next();
					smsUsers.add(eventSubscribers.getContactId());
				}
				
			//	for(int j=0; j<smsUsers.size(); j++)
			//	{
					//infoLogger.info(j+1+" : "+smsUsers.get(j).getContact_id());
			//	}
			}


			if(isEmail)
			{
				//check whether the serial Number is a part of machine Group
				TenancyEntity userTenancy = null;
				List<Integer> finalTenancyIdList = new LinkedList<Integer>();

				//Defect ID : 881: Rajani nagaraju 2013-07-01 get the tenancy of the landmark if it is a landmarAlert
				if(landmark!=null)
				{
					 if(! (session.isOpen() ))
	                  {
	                              session = HibernateUtil.getSessionFactory().getCurrentSession();
	                              session.getTransaction().begin();
	                  }
					Query getLandmarkTenancyQry = session.createQuery(" from LandmarkCategoryEntity where Landmark_Category_ID="+landmark.getLandmark_Category_ID().getLandmark_Category_ID()+" and ActiveStatus=1 ");
					
					Iterator landmarkCatItr = getLandmarkTenancyQry.list().iterator();
					while(landmarkCatItr.hasNext())
					{
						LandmarkCategoryEntity landCatEntity = (LandmarkCategoryEntity)landmarkCatItr.next();
						finalTenancyIdList.add(landCatEntity.getTenancy_ID().getTenancy_id());
						userTenancy = landCatEntity.getTenancy_ID();
					}
				}

				//get the tenancyId of the serialNumber
				else
				{
					/*Query query2 = session.createQuery(" select a.tenancy_id from AccountTenancyMapping a, AssetAccountMapping b" +
									" where a.account_id = b.accountId and b.serialNumber = '"+serialNumber+"' ");*/
					//DefectID: - Asset Owner Changes - 2013-07-19 - Rajani Nagaraju
					Query query2 = session.createQuery(" select a.tenancy_id from AccountTenancyMapping a, AssetEntity b" +
							" where a.account_id = b.primary_owner_id and b.serial_number = '"+serialNumber+"' and b.active_status=true and b.client_id="+clientEntity.getClient_id()+"");
					
					
					Iterator itr2 = query2.list().iterator();
					while(itr2.hasNext())
					{
						userTenancy = (TenancyEntity)  itr2.next();
					}

					//DF20150107 - Rajani Nagaraju - Handle NullPointerException if the tenancy is not created for the current machine owner
					//DF20150109 - Rajani Nagarajau - If the owner tenancy is not crreated, could not find the hierarachcal parent and hence email
					//will not be sent to any user (all ablove the hierarchy also). However fix is done to create a tenancy even without the mobile number wherein contact will not be created
					//Hence only for the existing tenancies, this problem can occur
					if(userTenancy!=null)
					{
						finalTenancyIdList.add(userTenancy.getTenancy_id());

						if(getChildAlerts==true)
						{
							//get the list of tenancy Ids of up above the hierarchy to whom the email has to be sent
							Query query3 = session.createQuery("select a.parentId from TenancyBridgeEntity a, TenancyDimensionEntity b where " +
										" a.parentId = b.tenancyId and a.childId = "+userTenancy.getTenancy_id()+" and a.parentId != a.childId and " +
														" b.tenancyTypeName in ('Dealer','Zonal','Regional') ");
							Iterator itr3 = query3.list().iterator();
							while(itr3.hasNext())
							{
								Integer tenancyId = (Integer)itr3.next();
								finalTenancyIdList.add(tenancyId);
							}
						}
					}

				}
				

				List<Integer> noCustomGroupTenancy = new LinkedList<Integer>();
				noCustomGroupTenancy.addAll(finalTenancyIdList);
				
				List<CustomAssetGroupEntity> customAssetGroupEntity = new LinkedList<CustomAssetGroupEntity>();

				//check whether the serialNumber belongs to MachineGroup
				//DF20150109 - Rajani Nagaraju
				if(userTenancy!=null)
				{
				Query query4 = session.createQuery(" from AssetCustomGroupMapping where serial_number = '"+serialNumber+"' ");
				Iterator itr4 = query4.list().iterator();
				while(itr4.hasNext())
				{
					AssetCustomGroupMapping assetCustomGroup = (AssetCustomGroupMapping) itr4.next();
					//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
					TenancyEntity assetCustomGroupTenancy = assetCustomGroup.getGroup_id().getTenancy_id();
					Query tenQuery = session.createQuery(" from TenancyEntity a where a.tenancyCode = (select b.tenancyCode from TenancyEntity b where " +
							" b.tenancy_id='"+assetCustomGroupTenancy.getTenancy_id()+"')");
					Iterator tenItr = tenQuery.list().iterator();
					while(tenItr.hasNext())
					{
						TenancyEntity ten = (TenancyEntity) tenItr.next();
						int assetGrTen = ten.getTenancy_id();
						if(assetCustomGroup.getGroup_id().getActive_status()==1)
						{
							//if(noCustomGroupTenancy.contains(assetCustomGroup.getGroup_id().getTenancy_id().getTenancy_id()))
							if(noCustomGroupTenancy.contains(assetGrTen))
							{
								//infoLogger.info("Machine Group Tenancy: "+assetCustomGroup.getGroup_id().getTenancy_id().getTenancy_id());
								//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
								//int index = noCustomGroupTenancy.indexOf(assetCustomGroup.getGroup_id().getTenancy_id().getTenancy_id());
								int index = noCustomGroupTenancy.indexOf(assetGrTen);
								noCustomGroupTenancy.remove(index);
							}
	
							//if(finalTenancyIdList.contains(assetCustomGroup.getGroup_id().getTenancy_id().getTenancy_id()))
							if(finalTenancyIdList.contains(assetGrTen))
							{
								customAssetGroupEntity.add(assetCustomGroup.getGroup_id());
							}
						}
					}
				}


				if(! (customAssetGroupEntity== null || customAssetGroupEntity.isEmpty()) )
				{
					Query query5 = session.createQuery(" from GroupUserMapping where group_id in (:list5)").setParameterList("list5", customAssetGroupEntity);
					Iterator itr5 = query5.list().iterator();
					while(itr5.hasNext())
					{
						GroupUserMapping groupUser = (GroupUserMapping) itr5.next();
						emailUsers.add(groupUser.getContact_id());
					}
				}
				
				/*
				for(int k=0; k<emailUsers.size(); k++)
				{
					infoLogger.info(k+1+" : "+emailUsers.get(k).getContact_id());
				}*/

				
				//infoLogger.info("Tenancy with No Machine Groups: "+noCustomGroupTenancy);
				
				//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
				TenancyEntity newUsrMappedTenancy = null;
				Query tenQuery = session.createQuery(" from TenancyEntity a where a.tenancyCode = (select b.tenancyCode from TenancyEntity b where " +
						" b.tenancy_id='"+userTenancy.getTenancy_id()+"') order by a.tenancy_id ");
				Iterator tenItr = tenQuery.list().iterator();
				if(tenItr.hasNext())
				{
					TenancyEntity tenancyEnt = (TenancyEntity) tenItr.next();
					newUsrMappedTenancy = tenancyEnt;
				}
				
				//infoLogger.info("userTenancy: "+userTenancy);
				for(int j=0; j<noCustomGroupTenancy.size(); j++)
				{
					//infoLogger.info("Tenancy :"+noCustomGroupTenancy.get(j));
					if(noCustomGroupTenancy.get(j)==userTenancy.getTenancy_id())
					{
						    //get the Tenancy Admin of the tenancy
						    //DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
							Query queryA = session.createQuery(" select a.contact_id  from AccountContactMapping a, AccountTenancyMapping b," +
									" ContactEntity c where a.account_id = b.account_id and a.contact_id = c. contact_id " +
									" and b.tenancy_id = "+newUsrMappedTenancy.getTenancy_id()+" and c.is_tenancy_admin = 1 and c.active_status=true");
							Iterator itrA = queryA.list().iterator();
							while(itrA.hasNext())
							{
								ContactEntity contact = (ContactEntity) itrA.next();
								emailUsers.add(contact);
							}
						
							/*
							for(int y=0; y<emailUsers.size(); y++)
							{
								infoLogger.info(y+1+" : "+emailUsers.get(y).getContact_id());
							}*/

					}

					else
					{
						//get the Tenancy Admin of the tenancy
						Query query6 = session.createQuery(" select a.contact_id  from AccountContactMapping a, AccountTenancyMapping b," +
								" ContactEntity c where a.account_id = b.account_id and a.contact_id = c. contact_id " +
								" and b.tenancy_id = "+noCustomGroupTenancy.get(j)+" and c.is_tenancy_admin = 1 and c.active_status=true");
						Iterator itr6 = query6.list().iterator();
						while(itr6.hasNext())
						{
							ContactEntity contact = (ContactEntity) itr6.next();
							emailUsers.add(contact);
						}
						/*
						for(int y=0; y<emailUsers.size(); y++)
						{
							infoLogger.info(y+1+" : "+emailUsers.get(y).getContact_id());
						}*/
						
					}
					

				}
				}
			}
			
	
			List<ContactEntity> finalContactEntity = new LinkedList<ContactEntity>();
			finalContactEntity.addAll(smsUsers);

			List<ContactEntity> finalEmailContactEntity = new LinkedList<ContactEntity>();
			finalEmailContactEntity.addAll(emailUsers);

			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

					
			//For SMS Users
			//DefectID : 985 - AssetDiagnostic fix to handle the alert generation based on event wise preference for JCB Admin and CC.
			HashMap<ContactEntity, List<Integer>> smsContactEventId = new HashMap<ContactEntity,List<Integer>>();
			HashMap<ContactEntity, List<Integer>> smsContactEventTypeId = new HashMap<ContactEntity,List<Integer>>();
			if( ! (finalContactEntity==null || finalContactEntity.isEmpty()) )
			{
				for(int t=0; t<finalContactEntity.size(); t++)
				{
					//If the user is CC/Admin - Event wise preference
					if( (finalContactEntity.get(t).getRole().getRole_name().equalsIgnoreCase("Customer Care"))
							|| (finalContactEntity.get(t).getRole().getRole_name().equalsIgnoreCase("JCB Admin")) )
					{
						Query q = session.createQuery(" select d.eventId " +
								" from PreferenceEntity a, CatalogValuesEntity b, PreferenceCatalogEntity c, EventEntity d " +
								" where a.catalogValueId = b.catalogValueId and b.catalogId = c.catalogId" +
								" and b.catalogValue = d.eventName and c.catalogName like 'SMS'" +
								" and a.contact='"+finalContactEntity.get(t).getContact_id()+"' ");
						Iterator smsItr = q.list().iterator();
						List<Integer> eventIdList = new LinkedList<Integer>();
						while(smsItr.hasNext())
						{
							int eventId = (Integer)smsItr.next();
							eventIdList.add(eventId);
						}
						
						if(!(eventIdList.isEmpty()))
						{
							smsContactEventId.put(finalContactEntity.get(t), eventIdList);
						}
					}
					
					//Any users other than CC/Admin - Event Type wise preference
					else
					{
						Query q = session.createQuery(" select d.eventTypeId " +
								" from PreferenceEntity a, CatalogValuesEntity b, PreferenceCatalogEntity c, EventTypeEntity d " +
								" where a.catalogValueId = b.catalogValueId and b.catalogId = c.catalogId" +
								" and b.catalogValue = d.eventTypeName and c.catalogName like '"+smsNotification+"'" +
								" and a.contact='"+finalContactEntity.get(t).getContact_id()+"' ");
						Iterator smsItr = q.list().iterator();
						List<Integer> eventTypeIdList = new LinkedList<Integer>();
						while(smsItr.hasNext())
						{
							int eventTypeId = (Integer)smsItr.next();
							eventTypeIdList.add(eventTypeId);
						}
						
						if(!(eventTypeIdList.isEmpty()))
						{
							smsContactEventTypeId.put(finalContactEntity.get(t), eventTypeIdList);
						}
					}
				}
			}
			
			/*
			for(int k=0; k<smsContactEventId.size(); k++)
			{
				ContactEntity contactList = (ContactEntity)smsContactEventId.keySet().toArray()[k];
				List<Integer> eventIdList = (List<Integer>)smsContactEventId.values().toArray()[k];
				infoLogger.info("Contact:"+contactList.getContact_id());
				infoLogger.info("EventId List: "+eventIdList);
			}	
				
				
			for(int k=0; k<smsContactEventTypeId.size(); k++)
			{
				ContactEntity contactList = (ContactEntity)smsContactEventTypeId.keySet().toArray()[k];
				List<Integer> eventTypeIdList = (List<Integer>)smsContactEventTypeId.values().toArray()[k];
				infoLogger.info("Contact:"+contactList.getContact_id());
				infoLogger.info("EventTypeId List: "+eventTypeIdList);
			}	*/
				
			
			//For Email Users
			HashMap<ContactEntity, List<Integer>> emailContactEventId = new HashMap<ContactEntity,List<Integer>>();
			HashMap<ContactEntity, List<Integer>> emailContactEventTypeId = new HashMap<ContactEntity,List<Integer>>();
			
			//HashMap<ContactEntity, String> emailAdminContact = new HashMap<ContactEntity>
			List<ContactEntity> emailAdminUsers= new LinkedList<ContactEntity>();
			List<ContactEntity> emailOtherUers = new LinkedList<ContactEntity>();
			
			if(!(finalEmailContactEntity==null || finalEmailContactEntity.isEmpty()))
			{
				for(int y=0; y<finalEmailContactEntity.size(); y++)
				{
					if( (finalEmailContactEntity.get(y).getRole().getRole_name().equalsIgnoreCase("Customer Care")) ||
							(finalEmailContactEntity.get(y).getRole().getRole_name().equalsIgnoreCase("JCB Admin")) )
					{
						emailAdminUsers.add(finalEmailContactEntity.get(y));
					}
					else
					{
						emailOtherUers.add(finalEmailContactEntity.get(y));
					}
				}
			}
			
			if(!(emailAdminUsers.isEmpty()))
			{
				Query q = session.createQuery(" select a.contact, CAST(GROUP_CONCAT(d.eventId) As string) " +
						" from PreferenceEntity a, CatalogValuesEntity b, PreferenceCatalogEntity c, EventEntity d " +
						" where a.catalogValueId = b.catalogValueId and b.catalogId = c.catalogId" +
						" and b.catalogValue = d.eventName and c.catalogName like 'Email'" +
						" and a.contact in (:list) group by a.contact ").setParameterList("list", emailAdminUsers);
				Iterator emailItr = q.list().iterator();
				Object[] result= null;
				while(emailItr.hasNext())
				{
					result = (Object[])emailItr.next();
					ContactEntity contact = (ContactEntity) result[0];
					List<Integer> eventIdIntList = new LinkedList<Integer>();
					List<String> eventIdList = Arrays.asList(result[1].toString().split(","));
					for(String s:eventIdList)
						eventIdIntList.add(Integer.parseInt(s));
					
					emailContactEventId.put(contact, eventIdIntList);
				}
				
				
			}
			
			if(!(emailOtherUers.isEmpty()))
			{
				Query q = session.createQuery(" select a.contact, CAST(GROUP_CONCAT(d.eventTypeId) As string) " +
						" from PreferenceEntity a, CatalogValuesEntity b, PreferenceCatalogEntity c, EventTypeEntity d " +
						" where a.catalogValueId = b.catalogValueId and b.catalogId = c.catalogId" +
						" and b.catalogValue = d.eventTypeName and c.catalogName like '"+emailNotification+"'" +
						" and a.contact in (:list) group by a.contact ").setParameterList("list", emailOtherUers);
				Iterator emailItr = q.list().iterator();
				Object[] result= null;
				while(emailItr.hasNext())
				{
					result = (Object[])emailItr.next();
					ContactEntity contact = (ContactEntity) result[0];
					List<Integer> eventTypeIdIntList = new LinkedList<Integer>();
					List<String> eventTypeIdList = Arrays.asList(result[1].toString().split(","));
					for(String s:eventTypeIdList)
						eventTypeIdIntList.add(Integer.parseInt(s));
					
					emailContactEventTypeId.put(contact, eventTypeIdIntList);
				}
				
			}
			
			/*for(int k=0; k<emailContactEventId.size(); k++)
			{
				ContactEntity contactList = (ContactEntity)emailContactEventId.keySet().toArray()[k];
				List<Integer> eventIdList = (List<Integer>)emailContactEventId.values().toArray()[k];
			//	infoLogger.info("Contact:"+contactList.getContact_id());
			//	infoLogger.info("EventId List: "+eventIdList);
			}*/	
				
				
			/*for(int k=0; k<emailContactEventTypeId.size(); k++)
			{
				ContactEntity contactList = (ContactEntity)emailContactEventTypeId.keySet().toArray()[k];
				List<Integer> eventTypeIdList = (List<Integer>)emailContactEventTypeId.values().toArray()[k];
			//	infoLogger.info("Contact:"+contactList.getContact_id());
			//	infoLogger.info("EventTypeId List: "+eventTypeIdList);
			}*/
			
			
				
			//for each event create two threads 
			//1) Form SMS and Email Template and push the data into Queue 
			//2) Insert data into asset event and event users
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			for(int b=0; b<eventIdValueMap.size(); b++)
			{
				//DF20140522 - Rajani Nagaraju - Adding try catch block for handling SQL Exception. Since VIN+EventID+TransactionTime combination is made as Unique in DB
				//If multiple times, same event is received it should not generate alert multiple times
				try
				{
					EventEntity event = (EventEntity) eventIdValueMap.keySet().toArray()[b];
					String mapValue = (String)eventIdValueMap.values().toArray()[b];
	
					//Insert the data into assetEvent
					iLogger.info(serialNumber+":"+transactionTime+"  :"+"Insert into Asset Event :"+event.getEventName());
					AssetEventEntity assetEvent = new AssetEventEntity();
					assetEvent.setActiveStatus(1);
					assetEvent.setEventGeneratedTime(transactionTime);
					assetEvent.setEventId(event);
					assetEvent.setEventSeverity(event.getEventSeverity());
					assetEvent.setEventTypeId(event.getEventTypeId());
					assetEvent.setSerialNumber(assetEntity);
					//Changes done by Rajani Nagaraju - 20130822 - DefectID:1163
					assetEvent.setServiceScheduleId(serviceScheduleId);
					session.save(assetEvent);


					List<String> smsUsersToInsert = new LinkedList<String>();
					List<String> emailUsersToInsert = new LinkedList<String>();
	
					//Final SMS users
					for(int i=0; i<smsContactEventId.size(); i++)
					{
						ContactEntity contactList = (ContactEntity)smsContactEventId.keySet().toArray()[i];
						List<Integer> eventIdList = (List<Integer>)smsContactEventId.values().toArray()[i];
						if(eventIdList.contains(event.getEventId()))
						{
							//DF20150113 - Rajani Nagaraju - Adding trim to avoid .NonUniqueObjectException for AssetEventContact
							//Ex: 'anku00343 ' in SMS users and 'anku00343' in email users will not match for common users	
							smsUsersToInsert.add(contactList.getContact_id().trim());
						}
					}
				
					for(int j=0; j<smsContactEventTypeId.size(); j++)
					{
						ContactEntity contactList = (ContactEntity)smsContactEventTypeId.keySet().toArray()[j];
						List<Integer> eventTypeIdList = (List<Integer>)smsContactEventTypeId.values().toArray()[j];
						if(eventTypeIdList.contains(event.getEventTypeId().getEventTypeId()) )
						{
							//DF20150113 - Rajani Nagaraju - Adding trim to avoid .NonUniqueObjectException for AssetEventContact
							//Ex: 'anku00343 ' in SMS users and 'anku00343' in email users will not match for common users	
							smsUsersToInsert.add(contactList.getContact_id().trim());
						}
					}
				
					//Final Email Users
					for(int i=0; i<emailContactEventId.size(); i++)
					{
						ContactEntity contactList = (ContactEntity)emailContactEventId.keySet().toArray()[i];
						List<Integer> eventIdList = (List<Integer>)emailContactEventId.values().toArray()[i];
						if(eventIdList.contains(event.getEventId()))
						{
							//DF20150113 - Rajani Nagaraju - Adding trim to avoid .NonUniqueObjectException for AssetEventContact
							//Ex: 'anku00343 ' in SMS users and 'anku00343' in email users will not match for common users	
							emailUsersToInsert.add(contactList.getContact_id().trim());
						}
					}
				
					for(int j=0; j<emailContactEventTypeId.size(); j++)
					{
						ContactEntity contactList = (ContactEntity)emailContactEventTypeId.keySet().toArray()[j];
						List<Integer> eventTypeIdList = (List<Integer>)emailContactEventTypeId.values().toArray()[j];
						if(eventTypeIdList.contains(event.getEventTypeId().getEventTypeId()) )
						{
							//DF20150113 - Rajani Nagaraju - Adding trim to avoid .NonUniqueObjectException for AssetEventContact
							//Ex: 'anku00343 ' in SMS users and 'anku00343' in email users will not match for common users	
							emailUsersToInsert.add(contactList.getContact_id().trim());
						}
					}
				
					//	infoLogger.info("Final SMS Users: "+smsUsersToInsert);
					//	infoLogger.info("Final Email Users: "+emailUsersToInsert);
				
					//----------------------------------Add the data to AssetEventContact
					List<String> commonUsers = new LinkedList<String>();
					List<String> actualSMSUsers = new LinkedList<String>();
					List<String> actualEmailUsers = new LinkedList<String>();
				
					//DefectId: 1322 - Rajani Nagaraju - To handle case insensitive in determining the event contacts
					actualSMSUsers.addAll(smsUsersToInsert);
					actualEmailUsers.addAll(emailUsersToInsert);
				
					//commonUsers.addAll(smsUsersToInsert);
					//commonUsers.retainAll(emailUsersToInsert);
					for(int i=0; i<smsUsersToInsert.size(); i++)
					{
						for(int j=0; j<emailUsersToInsert.size(); j++)
						{
							if(smsUsersToInsert.get(i).equalsIgnoreCase(emailUsersToInsert.get(j)))
							{
								commonUsers.add(smsUsersToInsert.get(i));
							}
						}
					}
				
					//DF201501223 - Rajani Nagaraju - To avoid IndexOutOfBoundException
					List<String> deleteSMSUsersList = new LinkedList<String>();
					//smsUsersToInsert.removeAll(commonUsers);
					for(int i=0; i<smsUsersToInsert.size(); i++)
					{
						for(int j=0; j<commonUsers.size(); j++)
						{
							if(smsUsersToInsert.get(i).equalsIgnoreCase(commonUsers.get(j)))
							{
								//smsUsersToInsert.remove(smsUsersToInsert.get(i));
								deleteSMSUsersList.add(smsUsersToInsert.get(i));
							}
						}
					}
					if((!(smsUsersToInsert==null && smsUsersToInsert.isEmpty())) && (!(deleteSMSUsersList==null && deleteSMSUsersList.isEmpty())))
						smsUsersToInsert.removeAll(deleteSMSUsersList);
				
					//emailUsersToInsert.removeAll(commonUsers);
					List<String> deleteEmailUsersList = new LinkedList<String>();
					for(int i=0; i<emailUsersToInsert.size(); i++)
					{
						for(int j=0; j<commonUsers.size(); j++)
						{
							if(emailUsersToInsert.get(i).equalsIgnoreCase(commonUsers.get(j)))
							{
								//emailUsersToInsert.remove(emailUsersToInsert.get(i));
								deleteEmailUsersList.add(emailUsersToInsert.get(i));
							}
							
						}
					}
					if((!(emailUsersToInsert==null && emailUsersToInsert.isEmpty())) && (!(deleteEmailUsersList==null && deleteEmailUsersList.isEmpty())))
						emailUsersToInsert.removeAll(deleteEmailUsersList);
				
					ListToStringConversion conversion = new ListToStringConversion();
					iLogger.info(serialNumber+":"+transactionTime+"  :"+"Insert into AssetEventContact for :"+event.getEventName());
					iLogger.info(serialNumber+":"+transactionTime+"  :"+"smsUsersToInsert :"+smsUsersToInsert);
					iLogger.info(serialNumber+":"+transactionTime+"  :"+"emailUsersToInsert :"+emailUsersToInsert);
					iLogger.info(serialNumber+":"+transactionTime+"  :"+"commonUsers :"+commonUsers);
					
					if(!(smsUsersToInsert.isEmpty()))
					{
						Query smsUsersQuery = session.createQuery(" from ContactEntity where active_status=true and contact_id in ("+conversion.getStringList(smsUsersToInsert).toString()+")");
						Iterator smsUsersItr = smsUsersQuery.list().iterator();
						while(smsUsersItr.hasNext())
						{
							ContactEntity contact = (ContactEntity)smsUsersItr.next();
							EventUsersEntity eventUsers = new EventUsersEntity();
							eventUsers.setAssetEventId(assetEvent);
							eventUsers.setContactId(contact);
							eventUsers.setIsSms(1);
							session.save(eventUsers);
						}
					}
				
					if(!(emailUsersToInsert.isEmpty()))
					{
						Query emailUsersQuery = session.createQuery(" from ContactEntity where active_status=true and contact_id in ("+conversion.getStringList(emailUsersToInsert).toString()+")");
						Iterator emailUsersItr = emailUsersQuery.list().iterator();
						while(emailUsersItr.hasNext())
						{
							ContactEntity contact = (ContactEntity)emailUsersItr.next();
							EventUsersEntity eventUsers = new EventUsersEntity();
							eventUsers.setAssetEventId(assetEvent);
							eventUsers.setContactId(contact);
							eventUsers.setIsEmail(1);
							session.save(eventUsers);
						}
					}
				
					if(!(commonUsers.isEmpty()))
					{
						Query commonUsersQuery = session.createQuery(" from ContactEntity where active_status=true and contact_id in ("+conversion.getStringList(commonUsers).toString()+")");
						Iterator commonUsersItr = commonUsersQuery.list().iterator();
						while(commonUsersItr.hasNext())
						{
							ContactEntity contact = (ContactEntity)commonUsersItr.next();
							EventUsersEntity eventUsers = new EventUsersEntity();
							eventUsers.setAssetEventId(assetEvent);
							eventUsers.setContactId(contact);
							eventUsers.setIsSms(1);
							eventUsers.setIsEmail(1);
							session.save(eventUsers);
						}
					}
				
				
					//Call the Handler for SMS and Email
					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}
					//for the given serial number get the engine number
					Query query14 = session.createQuery("from AssetEntity where serial_number='"+serialNumber+"' and active_status=true and client_id="+clientEntity.getClient_id()+"");
					Iterator itr14 = query14.list().iterator();
					AssetEntity asset = null;
					while(itr14.hasNext())
					{
						asset = (AssetEntity) itr14.next();
					}
					NotificationHandler handler = new NotificationHandler();
					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}
				
					iLogger.info(serialNumber+":"+transactionTime+"  :"+"------ Generated Alert Details--------");
					iLogger.info(serialNumber+":"+transactionTime+"  :"+"Event: "+event.getEventId()+";  "+event.getEventName());
					iLogger.info(serialNumber+":"+transactionTime+"  :"+"SMS Users:"+actualSMSUsers);
					iLogger.info(serialNumber+":"+transactionTime+"  :"+"Email Users:"+actualEmailUsers);
				
					if(!(actualSMSUsers==null || actualSMSUsers.isEmpty()))
					{
						//Defect ID: 983 - Rajani Nagaraju - AssetDiagnostic fix to generate SMS and Email 
						List<String> mobileNum = new LinkedList<String>();
						//DefectID:  - 1288 - Rajani Nagaraju - Custom SMS implementation
						List<String> userName = new LinkedList<String>();
						Query qw = session.createQuery("from ContactEntity where active_status=true and contact_id in (:list)").setParameterList("list", actualSMSUsers);
						Iterator itrr= qw.list().iterator();
						while(itrr.hasNext())
						{
							ContactEntity contact = (ContactEntity)itrr.next();
							mobileNum.add(contact.getPrimary_mobile_number());
							if(contact.getLast_name()!=null){
								userName.add(contact.getFirst_name()+" "+contact.getLast_name());
							}
							else{
								userName.add(contact.getFirst_name());
							}
						}
						
						//call the sms handler
						iLogger.info(serialNumber+":"+transactionTime+"  :"+"------ EventDetailsBO - Send SMS to SMSusers--------");
						handler.smsHandler(mobileNum, userName, serialNumber, assetEvent.getEventSeverity(), transactionTime.toString(), event.getEventId(), isEmergencyAlert, currentGPSfix);
					//	infoLogger.info("SMS Users :"+mobileNum);
					}
					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}
					if( !(actualEmailUsers==null || actualEmailUsers.isEmpty()))
					{
						//Defect ID: 983 - Rajani Nagaraju - AssetDiagnostic fix to generate SMS and Email 
						List<String> emailNum = new LinkedList<String>();
						Query qw = session.createQuery("from ContactEntity where active_status=true and contact_id in (:list)").setParameterList("list", actualEmailUsers);
						Iterator itrr= qw.list().iterator();
						while(itrr.hasNext())
						{
							ContactEntity contact = (ContactEntity)itrr.next();
							emailNum.add(contact.getPrimary_email_id());
						}
						//call email handler
						//DefectID: DF20131031 - Ra
						iLogger.info(serialNumber+":"+transactionTime+"  :"+"------ EventDetailsBO - Send Email to Email users--------");
						handler.emailHandler(emailNum, serialNumber, assetEvent.getEventSeverity(), transactionTime.toString(), event.getEventId(), event.getEventDescription(), mapValue, asset.getNick_name(), latValue, longValue, currentGPSfix);
					//	infoLogger.info("Email Users :"+emailNum);
					}
		

					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}
				}
				
				catch(Exception e)
				{
					fLogger.fatal(serialNumber+":"+transactionTime+"  :"+"Exception :"+e);
					
					Writer result = new StringWriter();
		    	    PrintWriter printWriter = new PrintWriter(result);
		    	    e.printStackTrace(printWriter);
		    	    String err = result.toString();
		    	    fLogger.fatal("Exception trace: "+err);
		    	    try 
		    	    {
		    	    	printWriter.close();
		        	    result.close();
					} 
		    	    
		    	    catch (IOException e1) 
		    	    {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		    	    
		    	  //DF20150112 - Rajani Nagaraju - If the Alert is not generated because of SQL Connection Exception, retain the file so that it would 
		    	    // be processed again in the next run.. Hence Returning FAILURE, which will not move the file.
		    	    if(e instanceof JDBCConnectionException || e instanceof SQLException || e instanceof GenericJDBCException)
		    	    {
		    	    	if(e.getMessage().contains("Cannot open connection"))
		    	    	{
		    	    		status="FAILURE";
		    	    	}
		    	    }
		    	    
					e.printStackTrace();
				}
			}

			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			
		}

		catch(Exception e)
		{
			fLogger.fatal(serialNumber+":"+transactionTime+"  :"+"Exception :"+e);
			
			Writer result = new StringWriter();
    	    PrintWriter printWriter = new PrintWriter(result);
    	    e.printStackTrace(printWriter);
    	    String err = result.toString();
    	    fLogger.fatal(serialNumber+":"+transactionTime+"  :"+"Exception trace: "+err);
    	    try 
    	    {
    	    	printWriter.close();
        	    result.close();
			} 
    	    
    	    catch (IOException e1) 
    	    {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	    
    	    //DF20150112 - Rajani Nagaraju - If the Alert is not generated because of SQL Connection Exception, retain the file so that it would 
    	    // be processed again in the next run.. Hence Returning FAILURE, which will not move the file.
    	    if(e instanceof JDBCConnectionException || e instanceof SQLException || e instanceof GenericJDBCException)
    	    {
    	    	if(e.getMessage().contains("Cannot open connection"))
    	    	{
    	    		status="FAILURE";
    	    	}
    	    }
    	    
			e.printStackTrace();

		}

		finally
		{
			 if(session!=null && session.isOpen())  
					if(session.getTransaction().isActive())
					{
						session.getTransaction().commit();
					}

			 if(session.isOpen())
			{
				 if(session.getTransaction().isActive())
					 session.flush();
				session.close();
			}

		}

		return status;

	}
	
	
	//*********************************************** Get the details of the VIN to be sent for PULL SMS *********************************************
	/** This method derives the required information to be sent as a response to Pull Sms
	 * DefectId: 1364 - 20130926 - Rajani Nagaraju - Pull SMS feature implementation
	 * @param mobileNumber Mobile Number of the user who initiated the Pull SMS
	 * @param serialNumber VIN for which the details are requested
	 * @return Returns the processing status
	 */
	public String getPullSmsDetails(String mobileNumber, String serialNumber)
	{
		//DF20140605 - Rajani Nagaraju - Return the VIN details in the same call to KAPSYS instead of sending new request to KAPSYS and returning "SUCCESS" in the current request
		String status="";
		String mobileNum = "";
		String smsContact1 = null;
		String smsContact2 = null;
		String subscriberJSONArray=null;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Logger iLogger = InfoLoggerClass.logger;

		Logger fLogger = FatalLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		SmsTemplate smsObj = new SmsTemplate();
		try
		{
			//Validate the VIN
			AssetEntity asset =null;
			Query query = session.createQuery("from AssetEntity where serial_number='"+serialNumber+"'");
			Iterator itr = query.list().iterator();
			while(itr.hasNext())
			{
				asset = (AssetEntity) itr.next();
			}

			if(asset==null)
			{
				//DF20140605 - Rajani Nagaraju - Return the VIN details in the same call to KAPSYS instead of sending new request to KAPSYS and returning "SUCCESS" in the current request
				status="Invalid PIN";
				throw new CustomFault("Invalid PIN");
			}
			//Validate MobileNumber - Whether the provided mobile number is a part of Receiver List defined for the VIN
			//DF20170629 - SU33449 - Validating the provided mobile number with the OrientDB data
			//OrientAppDbDatasource connObj = new OrientAppDbDatasource();
			//DF20171114: KO369761 - Changed the DB Connection from OrienDB to MySQL.
			ConnectMySQL connObj = new ConnectMySQL();
			HashMap<String,String> subsGroupContactMap = new HashMap<String,String>();
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;
			boolean matchingFlag = false;
			try
			{
				conn = connObj.getConnection();
				stmt = conn.createStatement();
				rs = stmt.executeQuery("select * from MAlertSubscribers where AssetID='"+serialNumber+"'"+"and CommMode='SMS'");
				while(rs.next()){
					Object subscriberDetails = (Object)rs.getObject("Details");
					iLogger.info("Subscriber details="+subscriberDetails);
					if(subscriberDetails==null){
						throw new CustomFault("Mobile Number is not registered for the specified VIN: " +serialNumber);
					}else{
						/*ODocument subsDetailsObj = (ODocument)subscriberDetails;
						ORecord orecord = subsDetailsObj.getRecord();
						String subscriberJSONString = detailsObj.toString();*/
						String subscriberJSONString = subscriberDetails.toString();
						subsGroupContactMap = new Gson().fromJson(subscriberJSONString, new TypeToken<HashMap<String, Object>>() {}.getType());
						iLogger.info("Subscriber Map:"+subsGroupContactMap);
					}
				}
			}catch(Exception e){
				fLogger.fatal("EventDetailsBO:getPullSmsDetails:VIN:"+serialNumber+":Exception"+e.getMessage());
				return subscriberJSONArray;
			}finally{
				try{
					if(rs!=null)
						rs.close();
					if(stmt!=null)
						stmt.close();
					if(conn!=null)
						conn.close();
				}
				catch(Exception e){
					fLogger.fatal("EventDetailsBO:getPullSmsDetails:VIN:"+serialNumber+":Exception in closing OrientDB Connection:"+e.getMessage());
				}
			}
			//****************** STEP2: Comparing the Mobile Number from OrientDB with the incoming parameter
			if(subsGroupContactMap!=null && subsGroupContactMap.size()>0)
			{
				for(Map.Entry entry: subsGroupContactMap.entrySet())
				{
					if(entry.getKey().toString().equalsIgnoreCase("@type"))
						continue;
					if(entry.getKey().toString().equalsIgnoreCase("@version"))
						continue;
					String modeContactJSON = "{"+entry.getKey().toString()+":"+entry.getValue().toString()+"}";
					iLogger.info("json string = "+modeContactJSON);
					HashMap<String,String> modeContactMap = new Gson().fromJson(modeContactJSON, new TypeToken<HashMap<String, String>>() {}.getType());
					iLogger.info("Mode Contact Map = "+modeContactMap);
					for(Map.Entry contactEntry : modeContactMap.entrySet())
					{
						if(contactEntry.getKey().toString().equalsIgnoreCase("@type"))
							continue;
						if(contactEntry.getKey().toString().equalsIgnoreCase("@version"))
							continue;
						if(contactEntry.getKey().toString().contains("1")||contactEntry.getKey().toString().contains("2")||contactEntry.getKey().toString().contains("3")){
							//Fetching the SMS contact Number for the specified contact
							iLogger.info("within if condition i.e matches with 1 or 2 or 3");
							if(contactEntry.getValue().toString().contains("|")){
								String [] contactId = contactEntry.getValue().toString().split("\\|");
								smsContact1 = contactId[0];
								smsContact2 = contactId[1];
							}else{
								smsContact1 = contactEntry.getValue().toString();
								smsContact2 = null;
							}
							iLogger.info("Query for contact: "+"from ContactEntity where contact_id in('"+smsContact1+"',"+"'"+smsContact2+"')");
							Query contactQ = session.createQuery("from ContactEntity where contact_id in('"+smsContact1+"',"+"'"+smsContact2+"')");
							Iterator contactItr = contactQ.list().iterator();
							while(contactItr.hasNext()){
								ContactEntity contact = (ContactEntity)contactItr.next();
								if(contact.getPrimary_mobile_number()!=null){
									mobileNum = contact.getPrimary_mobile_number();
									if((mobileNum.equals(mobileNumber))){
										iLogger.info("Mobile Number Matched"+mobileNum);
										break;
									}
								}
							}
							if((mobileNum.equals(mobileNumber))){
								matchingFlag = true;
								break;
							}
						}
					}
				}
				if(!matchingFlag){
					status="Mobile Number is not registered for the specified PIN";
					throw new CustomFault("Mobile Number is not registered for the specified PIN");
				}
			}
			else{
				status="Mobile Number is not registered for the specified PIN";
				throw new CustomFault("Mobile Number is not registered for the specified PIN");
			}
			/*ContactEntity contact = null;
			Query query1 = session.createQuery(" select a.contactId from EventSubscriptionMapping a, ContactEntity b where" +
					" a.contactId = b.contact_id and a.serialNumber='"+serialNumber+"' and b.primary_mobile_number like '%"+mobileNumber+"'");
			Iterator itr1 = query1.list().iterator();
			while(itr1.hasNext())
			{
				contact = (ContactEntity)itr1.next();
			}
			if(contact==null)
			{
				//DF20140605 - Rajani Nagaraju - Return the VIN details in the same call to KAPSYS instead of sending new request to KAPSYS and returning "SUCCESS" in the current request
				status="Mobile Number is not registered for the specified PIN";
				throw new CustomFault("Mobile Number is not registered for the specified PIN");
			}*/

			//---- Get the required key values( Here - Parameter Names) from property file
			Properties prop = new Properties();
			String pCumOperatingHours=null;
			String pFuelLevel =null;
			String pLatitude=null;
			String pLongitude=null;
			String pVin = null;
			String pLocation=null;

			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			pCumOperatingHours= prop.getProperty("TotalEngineHours");
			pFuelLevel= prop.getProperty("FuelLevel");
			pLatitude= prop.getProperty("Latitude");
			pLongitude= prop.getProperty("Longitude");
			pVin = prop.getProperty("SmsTemp_VIN");
			pLocation = prop.getProperty("Location");

			//Get the required details for the VIN - Cummulative operating hours, Fuel Level, LatLong
			String cummOpHours = "";
			String fuelLevel="";
			String locationAddress="";
			String latitude="";
			String longitude="";
			Timestamp transactionTime=null;
			String transactionTimeInString =null;
			String timeStampLog = null;

			//2014-06-20 : Removed the extra and from the query - Deepthi
			//DF20140609 - Rajani Nagaraju - To take the fuel level from Snapshot table and not the max(txn_time) from AMD, since the fuel level will be
			//populated correctly only for Low fuel event and Log packet. In rest all packets it would be 110 (Junk value - which should not be displayed) 
			/* Query amdQuery = session.createQuery("select c.parameterName, b.parameterValue from AssetMonitoringHeaderEntity a , AssetMonitoringDetailEntity b, MonitoringParameters c" +
	    	 		" where a.transactionNumber = b.transactionNumber and b.parameterId= c.parameterId and" +
	    	 		" a.serialNumber='"+serialNumber+"' and " +
	    	 		" a.transactionTime = ( select max(x.transactionTime) from AssetMonitoringHeaderEntity x where x.serialNumber='"+serialNumber+"') " +
	    			" and c.parameterName in ('"+pCumOperatingHours+"', '"+pFuelLevel+"', '"+pLatitude+"', '"+pLongitude+"' )");*/


			//20160718 - @suresh DAL layer implemntation for ams where latest cmh for a VIN can be found in parameter coloumn

			//DF20161222 @Roopa changing asset_monitoring_snapshot_new to asset_monitoring_snapshot which is having txndata as json column

			String txnKey = "EventDetailsBO:getPullSmsDetails";

			List<AMSDoc_DAO> snapshotObj=new ArrayList<AMSDoc_DAO> ();

			DynamicAMS_Doc_DAL amsDaoObj=new DynamicAMS_Doc_DAL();

			snapshotObj=amsDaoObj.getAMSData(txnKey, serialNumber);

			//iLogger.debug(txnKey+"::"+"AMS:persistDetailsToDynamicMySql::AMS DAL::getAMSData Size:"+snapshotObj.size());

			if(snapshotObj.size()>0){

				//parameters format in AMS
				//String currParam= LAT|LONG|Enginestatus|Machinehours|ExternalBatteryVoltage|HCT|LOP|InternalBatteryLow
				//temp = false;
				/*String parameters=snapshotObj.get(0).getParameters();
				String [] currParamList=parameters.split("\\|", -1);


				 //CMH
				 cummOpHours = currParamList[3];

				 //latitude
				  latitude = currParamList[0];


				  longitude = currParamList[1];

				fuelLevel = snapshotObj.get(0).getFuel_Level();*/

				HashMap<String,String> txnDataMap = snapshotObj.get(0).getTxnData();
				cummOpHours=txnDataMap.get("CMH");
				latitude=txnDataMap.get("LAT");
				longitude=txnDataMap.get("LONG");
				fuelLevel=txnDataMap.get("FUEL_PERCT");


				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				transactionTime = new Timestamp(df.parse(snapshotObj.get(0).getLatest_Transaction_Timestamp()).getTime());

				String transactionTimeConversion = String.valueOf(transactionTime);
				//DF20141031: SU334449 - TimeZone implementation for SAARC changes
				AssetEntity assetTime =null;
				String timeZone = null;
				Query queryTime = session.createQuery("from AssetEntity where serial_number='"+serialNumber+"'");
				Iterator itrTime = queryTime.list().iterator();
				if(itrTime.hasNext()){
					assetTime = (AssetEntity) itrTime.next();
					timeZone = assetTime.getTimeZone();
				}
				GmtLtTimeConversion convertedTime = new GmtLtTimeConversion();
				timeStampLog = convertedTime.convertGmtToLocal(timeZone, transactionTimeConversion);
				
				/*Calendar cal = Calendar.getInstance();
				cal.setTime(transactionTime);
				cal.add(Calendar.MINUTE, 30);
				cal.add(Calendar.HOUR, 5);
				transactionTimeInString = df.format(cal.getTime().getTime());*/
			}

			/* Query amdQuery = session.createQuery("select c.parameterName, b.parameterValue, a.fuelLevel, a.transactionTime " +
	    	 		" from AssetMonitoringSnapshotEntity a , AssetMonitoringDetailEntity b, MonitoringParameters c " +
	    	 		" where a.transactionNumber = b.transactionNumber and b.parameterId= c.parameterId and " +
	    	 		" a.serialNumber='"+serialNumber+"' and " +
	    	 		"  c.parameterName in ('"+pCumOperatingHours+"', '"+pLatitude+"', '"+pLongitude+"' )" );

	    	 Iterator amdItr = amdQuery.list().iterator();
	    	 Object[] result=null;
	    	 while(amdItr.hasNext())
	    	 {
	    		 result = (Object[])amdItr.next();
	    		 String paramName = (String)result[0];
	    		 String paramValue = (String) result[1];
	    		 if(result[2]!=null)
	    			 fuelLevel=(String) result[2];

	    		 //DF20141031 - Rajani Nagaraju - Adding Last Reported Time in Pull SMS Content
	    		 if(result[3]!=null)
	    		 {
	    			 transactionTime = (Timestamp)result[3];

	    			 Calendar cal = Calendar.getInstance();
	    			 cal.setTime(transactionTime);
	    			 cal.add(Calendar.MINUTE, 30);
	    			 cal.add(Calendar.HOUR, 5);
	    			 transactionTimeInString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime().getTime());
	    		 }

	    		 if(paramName.equalsIgnoreCase(pCumOperatingHours))
	    		 {
	    			 cummOpHours = paramValue;
	    		 }

	    		 if(paramName.equalsIgnoreCase(pLatitude))
	    		 {
	    			 latitude = paramValue;
	    		 }

	    		 if(paramName.equalsIgnoreCase(pLongitude))
	    		 {
	    			 longitude = paramValue;
	    		 }
	    	 }
			 */

			//Get the Location Name from the obtained Latitude and Longitude
			//String address = LocationByLatLon.getLocation(longitude, latitude); 
			//DF20170821 - SU334449 - GeoCoding Library changed from google api to MapMyIndia. 
			GeocodingLibrary lib = new  GeocodingLibrary();
			@SuppressWarnings("static-access")
			//Leela - Commenting bcz using mmi instead of google
			//LocationDetails locationDetails = lib.getLocationDetails(latitude, longitude);
			LocationDetailsMMI locationDetails = lib.getLocationDetailsMMI(latitude, longitude);
			String address = locationDetails.getAddress();
			if(address==null || address.equalsIgnoreCase("undefined"))
			{
				NumberFormat formatter = new DecimalFormat("0.##"); 
				latitude = formatter.format(Double.parseDouble(latitude));
				longitude = formatter.format(Double.parseDouble(longitude));
				locationAddress=""+latitude+","+longitude+" Address Undefined by Google Maps";

			}

			else
			{
				//Prase the address obtained to the required format
				String[] addressList = address.split(",");
				String newAddress ="";

				if(addressList.length>2)
				{
					//First obtain the Street and City details
					for(int i=(addressList.length)-3; i>=0; i--)
					{
						String concatAddress = newAddress;
						if(newAddress.equalsIgnoreCase(""))
							concatAddress= addressList[i]+newAddress;
						else
							concatAddress= addressList[i]+","+newAddress;

						if(concatAddress.length()>=90)
						{
							break;
						}
						else
						{
							newAddress = concatAddress;
						}
					}

					//If the Address length is not greater than 90, add state, zipcode and country as well (If it fits)
					if(newAddress.length()<90)
					{
						for(int i=(addressList.length)-2; i<addressList.length; i++)
						{
							String concatAddress = newAddress;
							if(newAddress.equalsIgnoreCase(""))
								concatAddress= newAddress + addressList[i];
							else
								concatAddress= newAddress+","+addressList[i];

							if(concatAddress.length()>=90)
							{
								break;
							}
							else
							{
								newAddress = concatAddress;
							}
						}
					}
				}

				//If only the State and Country information is returned from Google Maps
				else
				{
					for(int i=0; i<addressList.length; i++)
					{
						String concatAddress = newAddress;

						if(newAddress.equalsIgnoreCase(""))
							concatAddress= newAddress + addressList[i];
						else
							concatAddress= newAddress+","+addressList[i];

						if(addressList.length >= 90)
						{
							break;
						}

						else
						{
							newAddress = concatAddress;
						}
					}
				}

				locationAddress = newAddress;
				//	 infoLogger.info("Pull SMS Address: "+locationAddress+" for the VIN:"+serialNumber);
				iLogger.info("Pull SMS Address: "+locationAddress+" for the VIN:"+serialNumber);

			}

			String finalFuelLevel =null;
			if(fuelLevel==null || fuelLevel.equalsIgnoreCase("") || fuelLevel.equalsIgnoreCase("0"))
			{
				finalFuelLevel="";
			}
			else
			{
				finalFuelLevel = "FuelLevel:"+fuelLevel+"%";
			}

			/*//Split the message into 30 charecters 
	    	Matcher m = Pattern.compile(".{1,30}").matcher(locationAddress);
	 	    String locationAddress1 = m.find() ? locationAddress.substring(m.start(), m.end()) : "";
	 	    String locationAddress2 = m.find() ? locationAddress.substring(m.start(), m.end()) : "";
	 	    String locationAddress3 = m.find() ? locationAddress.substring(m.start(), m.end()) : "";*/

			//Fill in the SMS Queue Object
			String templateID =null;
			templateID= prop.getProperty("PullSmsTemplateID");

			/*		 
	    	 List<String> mobileNum = new LinkedList<String>();
			 mobileNum.add(mobileNumber);
	    	 smsObj.setTo(mobileNum);*/

			// List<String> msgList = new LinkedList<String>();
			// String msgBody = templateID+"&f1="+serialNumber+"&f2="+cummOpHours+"&f3="+finalFuelLevel+"&f4="+locationAddress1+"&f5="+locationAddress2+"&f6="+locationAddress3;


			//DefectId: Not provided - Rajani Nagaraju - 20131011 - SMS HTTP URL changed from KAPSYSTEM
			// String msgBody = "JCBM: "+serialNumber+";CMHR: "+cummOpHours+"; FuelLevel:"+finalFuelLevel+"; Loc:"+locationAddress;

			//DF20140605 - Rajani Nagaraju - Return the VIN details in the same call to KAPSYS instead of sending new request to KAPSYS and returning "SUCCESS" in the current request
			String msgBody = "JCBM: "+serialNumber+";CHMR: "+cummOpHours+"; "+finalFuelLevel+"; Loc:"+locationAddress+"; Last Reported on: "+timeStampLog;

			// msgList.add(msgBody);
			// smsObj.setMsgBody(msgList);

			//	 infoLogger.info("To: "+ mobileNum);
			//	 infoLogger.info("Body: "+ msgList);
			//DefectId:20131111 @Suprava	
			//new SmsHandler().handleSms("jms/queue/smsQ", smsObj,0);

			//DF20140605 - Rajani Nagaraju - Return the VIN details in the same call to KAPSYS instead of sending new request to KAPSYS and returning "SUCCESS" in the current request
			status=msgBody;

			//DF20140605 - Rajani Nagaraju - Return the VIN details in the same call to KAPSYS instead of sending new request to KAPSYS and returning "SUCCESS" in the current request
			//Insert the record into SMS log details table
			Timestamp currentTimestamp = new Timestamp((new Date()).getTime());
			SmsLogDetailsEntity smsLog = new SmsLogDetailsEntity();
			smsLog.setMobileNumber(mobileNumber);
			smsLog.setSerialNumber(serialNumber);
			smsLog.setSmsBody(msgBody);
			smsLog.setSmsSentTime(currentTimestamp);
			session.save(smsLog);

		}


		catch(CustomFault e)
		{
			e.printStackTrace();
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
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
		return status;
	}
	
	//*********************************************** END of get the details of the VIN to be sent for PULL SMS *********************************************
	
	/*//************************************************** Update Event Generated Location *****************************************************
	*//** DefectID: 20131024 - Rajani Nagaraju - Update Adress in AssetEvent 
	 * This method updates the Adrress of the machine Location for when the Event got generated based on Event Generated Time and packet Snapshot time
	 * @return
	 *//*
	public String updateAddess()
	{
		String status="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		
    	Logger fLogger = FatalLoggerClass.logger;
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
        List<String> assetMonitoringParameters = null;
		List<String> assetMonitoringValues = null;
		
        try
        {
        	//Check whether the MapService is enabled for the Application
        	Properties prop = new Properties();
        	prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			String mapService = prop.getProperty("MapService");
        	int mapServiceConfig =0;
        	
        	Query q = session.createQuery(" from ConfigAppEntity where services='"+mapService+"'");
        	Iterator it = q.list().iterator();
        	while(it.hasNext())
        	{
        		ConfigAppEntity configApp = (ConfigAppEntity)it.next();
        		if(configApp.isStatus())
        		{
        			mapServiceConfig =1;
        		}
        		
        	}
        	
        	if(mapServiceConfig==1)
        	{
        		iLogger.info("Asset Event Address Update Scheduler");
        		iLogger.info("-------------------------------------");
        		iLogger.info("AssetEventId, Latitude, Longitude, Address");
        		iLogger.info("-------------------------------------");
        		//DF30150710 - Rajani Nagaraju - Populate Address only for the fields with NULL and not for undefined 
        		Query query = session.createQuery(" select a, CAST(GROUP_CONCAT(d.parameterName) As string ) as parameterNames," +
	        			" CAST(GROUP_CONCAT(c.parameterValue) As string ) as parameterValues" +
	        			" from AssetEventEntity a, AssetMonitoringHeaderEntity b, AssetMonitoringDetailEntity c," +
	        			" MonitoringParameters d " +
	        			" where a.serialNumber= b.serialNumber" +
	        			" and a.eventGeneratedTime = b.transactionTime " +
	        			" and b.transactionNumber = c.transactionNumber " +
	        			" and c.parameterId = d.parameterId" +
	        			" and d.parameterName in ('Latitude','Longitude')" +
	        			" and ( a.address is null OR a.address like 'undefined' OR a.state is null OR a.state like 'undefined' OR a.city is null OR a.city like 'undefined' )" +
	        			" group by a.assetEventId");
        		Query query = session.createQuery(" select a, CAST(GROUP_CONCAT(d.parameterName) As string ) as parameterNames," +
	        			" CAST(GROUP_CONCAT(c.parameterValue) As string ) as parameterValues" +
	        			" from AssetEventEntity a, AssetMonitoringHeaderEntity b, AssetMonitoringDetailEntity c," +
	        			" MonitoringParameters d " +
	        			" where a.serialNumber= b.serialNumber" +
	        			" and a.eventGeneratedTime = b.transactionTime " +
	        			" and b.transactionNumber = c.transactionNumber " +
	        			" and c.parameterId = d.parameterId" +
	        			" and d.parameterName in ('Latitude','Longitude')" +
	        			" and ( a.address is null OR a.state is null OR a.city is null )" +
	        			" group by a.assetEventId");
	        	Iterator itr = query.list().iterator();
	        	Object[] result=null;
	        	
	        	int rowUpdateCounter=0;
	        	while(itr.hasNext())
	        	{
	        		result = (Object[])itr.next();
	        		
	        		rowUpdateCounter++;
	        		
	        		
	        		try
	        		{
	        			String address =null;
	        			String latitude=null;
		        		String longitude = null;
		        		assetMonitoringParameters = new LinkedList<String>();
		        		assetMonitoringValues = new LinkedList<String>();
		        		
		        		AssetEventEntity assetEvent = (AssetEventEntity)result[0];
		        		
		        		if(result[1]!=null)
							assetMonitoringParameters = Arrays.asList(result[1].toString().split(","));
						
						if(result[2] !=null)
							assetMonitoringValues = Arrays.asList(result[2].toString().split(","));
					
						for(int i=0; i<assetMonitoringParameters.size();i++)
						{
							if(assetMonitoringParameters.get(i).equalsIgnoreCase("Latitude"))
								latitude = assetMonitoringValues.get(i);
							else if(assetMonitoringParameters.get(i).equalsIgnoreCase("Longitude"))
								longitude = assetMonitoringValues.get(i);
						}
						
						if(latitude!=null  && longitude!=null)
						{
							address = LocationByLatLon.getLocation(longitude, latitude);
						}
					
					
						if(address!=null)
						{
							iLogger.info("Update AE Address for :"+assetEvent.getAssetEventId()+","+latitude+","+longitude+","+address);
							assetEvent.setAddress(address);
							session.update(assetEvent);
						}
	        		}
	        		
	        		catch(Exception e)
	        		{
	        			fLogger.fatal(e);
	        		}
					
	        		if(rowUpdateCounter>100)
					{
	        			iLogger.info("Update AE Address for :"+rowUpdateCounter+" rows");
	        			rowUpdateCounter=0;
						try
						{
							if (session.getTransaction().isActive()) 
							{
								session.getTransaction().commit();
							}
						}
						catch(Exception e)
						{
							fLogger.fatal(e);
							
							if (session.isOpen()) 
							{
								session.clear();
							}
							if (session.isOpen()) 
							{
								session.close();
							}
						}
						
						finally
						{
							if (session.isOpen()) 
							{
								session.flush();
								session.close();
							}
						}
						
						if(! (session.isOpen() ))
			            {
			                        session = HibernateUtil.getSessionFactory().getCurrentSession();
			                        session.getTransaction().begin();
			            }
					}
	        		
	        	}
        	}
        }
        
        catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
			
			if (session.isOpen()) 
			{
				session.clear();
			}
			if (session.isOpen()) 
			{
				session.close();
			}
		}
        
        finally
        {
        	try
			{
				if (session.getTransaction().isActive()) 
					session.getTransaction().commit();
			}
			
			catch(Exception e)
			{
				fLogger.fatal(e);
			}

			finally
			{
				if (session.isOpen()) 
				{
					session.flush();
					session.close();
				}
			}
              
        }
		
		return status;
	}*/
	
	
	//************************************************** Update Event Generated Location *****************************************************
			/** DefectID: 20131024 - Rajani Nagaraju - Update Adress in AssetEvent 
			 * This method updates the Adrress of the machine Location for when the Event got generated based on Event Generated Time and packet Snapshot time
			 * @return
			 */
			public String updateAddess()
			{
				String status="SUCCESS";
				Logger iLogger = InfoLoggerClass.logger;

				Logger fLogger = FatalLoggerClass.logger;
				//Logger fatalError = Logger.getLogger("fatalErrorLogger");

				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.beginTransaction();

				List<String> assetMonitoringParameters = null;
				List<String> assetMonitoringValues = null;

				try
				{
					//Check whether the MapService is enabled for the Application
					Properties prop = new Properties();
					prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
					String mapService = prop.getProperty("MapService");
					int mapServiceConfig =0;

					Query q = session.createQuery(" from ConfigAppEntity where services='"+mapService+"'");
					Iterator it = q.list().iterator();
					while(it.hasNext())
					{
						ConfigAppEntity configApp = (ConfigAppEntity)it.next();
						if(configApp.isStatus())
						{
							mapServiceConfig =1;
						}

					}

					if(mapServiceConfig==1)
					{
						iLogger.info("Asset Event Address Update Scheduler");
						iLogger.info("-------------------------------------");
						iLogger.info("AssetEventId, Latitude, Longitude, Address");
						iLogger.info("-------------------------------------");
						//DF30150710 - Rajani Nagaraju - Populate Address only for the fields with NULL and not for undefined 
						/*Query query = session.createQuery(" select a, CAST(GROUP_CONCAT(d.parameterName) As string ) as parameterNames," +
					        			" CAST(GROUP_CONCAT(c.parameterValue) As string ) as parameterValues" +
					        			" from AssetEventEntity a, AssetMonitoringHeaderEntity b, AssetMonitoringDetailEntity c," +
					        			" MonitoringParameters d " +
					        			" where a.serialNumber= b.serialNumber" +
					        			" and a.eventGeneratedTime = b.transactionTime " +
					        			" and b.transactionNumber = c.transactionNumber " +
					        			" and c.parameterId = d.parameterId" +
					        			" and d.parameterName in ('Latitude','Longitude')" +
					        			" and ( a.address is null OR a.address like 'undefined' OR a.state is null OR a.state like 'undefined' OR a.city is null OR a.city like 'undefined' )" +
					        			" group by a.assetEventId");*/

						/*Query query = session.createQuery(" select a, CAST(GROUP_CONCAT(d.parameterName) As string ) as parameterNames," +
					        			" CAST(GROUP_CONCAT(c.parameterValue) As string ) as parameterValues" +
					        			" from AssetEventEntity a, AssetMonitoringHeaderEntity b, AssetMonitoringDetailEntity c," +
					        			" MonitoringParameters d " +
					        			" where a.serialNumber= b.serialNumber" +
					        			" and a.eventGeneratedTime = b.transactionTime " +
					        			" and b.transactionNumber = c.transactionNumber " +
					        			" and c.parameterId = d.parameterId" +
					        			" and d.parameterName in ('Latitude','Longitude')" +
					        			" and ( a.address is null OR a.state is null OR a.city is null )" +
					        			" group by a.assetEventId");*/
						boolean flagContinue = true;
						int recordsCount = 0;
						while(flagContinue){
							if(session ==null || !(session.isOpen() ))
							{
								session = HibernateUtil.getSessionFactory().getCurrentSession();
								session.getTransaction().begin();
							}
							Query query = session.createQuery(" select a"+
									" from AssetEventEntity a"+
									" where ( a.address is null OR a.state is null OR a.city is null )  and Location is not null" +
									" group by a.assetEventId order by a.assetEventId desc");
							query.setMaxResults(500);
							Iterator itr = query.list().iterator();
							Object[] result=null;
							if(!itr.hasNext()){
								flagContinue = false;
							}
							int rowUpdateCounter=0;
							/*DynamicAMH_DAL amhDAL = new DynamicAMH_DAL();
					        	List parameterIDList = new LinkedList();
					        	parameterIDList.add(1);
					        	parameterIDList.add(2);*/


							while(itr.hasNext())
							{
								//result = (Object[])itr.next();


								AssetEventEntity assetEvent = (AssetEventEntity)itr.next();;
								rowUpdateCounter++;

								//LocationDetails loc = null;
								LocationDetailsMMI loc = null;
								try
								{
									String address =null;
									String latitude=null;
									String longitude = null;
									assetMonitoringParameters = new LinkedList<String>();
									assetMonitoringValues = new LinkedList<String>();


									AssetEntity serial_number = assetEvent.getSerialNumber();

									/*AssetMonitoringParametersDAO daoObject = amhDAL.getLocationONTxn(serial_number.getSerial_number().getSerialNumber(), 
						        				assetEvent.getEventGeneratedTime(), serial_number.getSegmentId(), parameterIDList);
						        		if(daoObject == null)
						        			continue;*/
									if(assetEvent.getLocation() == null){
										continue;
									}
									/*if(result[1]!=null)	
											assetMonitoringParameters = Arrays.asList(result[1].toString().split(","));

										if(result[2] !=null)
											assetMonitoringValues = Arrays.asList(result[2].toString().split(","));*/

									/*if(daoObject.getParameterName()!=null)
						        			assetMonitoringParameters = Arrays.asList(daoObject.getParameterName().split(","));
						        		if(daoObject.getParameterValue()!=null)
						        			assetMonitoringValues = Arrays.asList(daoObject.getParameterValue().toString().split(","));

										for(int i=0; i<assetMonitoringParameters.size();i++)
										{
											if(assetMonitoringParameters.get(i).equalsIgnoreCase("Latitude"))
												latitude = assetMonitoringValues.get(i);
											else if(assetMonitoringParameters.get(i).equalsIgnoreCase("Longitude"))
												longitude = assetMonitoringValues.get(i);
										}*/

									String[] location = assetEvent.getLocation().split(",");
									if(location[0]!=null)
										latitude = location[0];
									if(location[1]!=null){
										longitude = location[1];
									}

									if(latitude!=null  && longitude!=null)
									{
										
										//loc = new LocationByLatLon().getLocationDetails(longitude, latitude);
										// Leela - commenting Bcz of using mmi instead og google
										//loc = new LocationByLatLon().getLocationDetails(longitude, latitude);
										loc = GetSetLocationJedis.getLocationDetailsMMI(latitude, longitude);
									}


									if(loc.getAddress()!=null)
									{
										iLogger.info("Update AE Address for :"+assetEvent.getAssetEventId()+","+latitude+","+longitude+","+loc.getAddress());
										assetEvent.setAddress(loc.getAddress());
									}
									if(loc.getState()!=null)
									{
										//iLogger.info("Update AE State for :"+assetEvent.getAssetEventId()+","+latitude+","+longitude+","+loc.getState());
										assetEvent.setState(loc.getState());
									}
									if(loc.getCity()!=null)
									{
										//iLogger.info("Update AE City for :"+assetEvent.getAssetEventId()+","+latitude+","+longitude+","+loc.getCity());
										assetEvent.setCity(loc.getCity());
									}
									if(session ==null || !(session.isOpen() ))
									{
										session = HibernateUtil.getSessionFactory().getCurrentSession();
										session.getTransaction().begin();
									}
									if(loc.getAddress()!=null)
										session.update(assetEvent);
								}

								catch(Exception e)
								{
									fLogger.fatal(e);

								}
								if(recordsCount > 15000){
									flagContinue = false;
								}
								if(session ==null || !(session.isOpen() ))
								{
									session = HibernateUtil.getSessionFactory().getCurrentSession();
									session.getTransaction().begin();
								}
								if(rowUpdateCounter%500 == 0 )
								{
									iLogger.info("Update AE Address for :"+rowUpdateCounter+" rows");
									//rowUpdateCounter=0;
									recordsCount = recordsCount + rowUpdateCounter;
									if (session !=null && session.isOpen()) 
									{
										session.flush();
										if (session.getTransaction().isActive()) 
										{
											session.getTransaction().commit();
										}
									}

								}

							}
						}
						if (session.isOpen()) 
						{
							session.flush();
							//session.close();
						}
						if (session.getTransaction().isActive()) 
						{
							session.getTransaction().commit();
						}
					}
				}

				catch(Exception e)
				{
					fLogger.fatal("Exception :"+e);

					/*if (session.isOpen()) 
							{
								session.clear();
							}*/
					if (session !=null && session.isOpen()) 
					{
						session.close();
					}
				}

				finally
				{
					try
					{
						if (session !=null && session.getTransaction().isActive()) 
							session.getTransaction().commit();
					}

					catch(Exception e)
					{
						fLogger.fatal(e);
					}

					finally
					{
						if (session !=null && session.isOpen()) 
						{
							session.flush();
							session.close();
						}
					}

				}

				return status;
			}
			
			
			//DF20170208 @Ajay for Asset event address updation(Logic change-> Get all the records for the given day)
			
			 public String updateAddess_assetEvent(String startDate, String endDate)
			  {
			    String status = "SUCCESS";
			    Query qurey = null;
			    Logger iLogger = InfoLoggerClass.logger;
			    Logger fLogger = FatalLoggerClass.logger;

			    //CR337.sn
				String redisIp="localhost";
				String redisPort=null;
				//CR337.en
				
			    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			    session.beginTransaction();

			    List assetMonitoringParameters = null;
			    List assetMonitoringValues = null;

			    //Jedis redisPool = new Jedis("localhost", Integer.parseInt(redisPort)); //CR337.o
			    try
			    {
			      Properties prop = new Properties();
			      prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			      String mapService = prop.getProperty("MapService");
			      int mapServiceConfig = 0;
			      //CR337.sn
			      //redisIp = prop.getProperty("geocodingredisurl");
			      redisPort = prop.getProperty("geocodingredisport");
			      iLogger.info("EventDetailBO : RedisIP:" + redisIp + "::RedisPort:" + redisPort );
			      Jedis redisPool = new Jedis(redisIp, Integer.parseInt(redisPort));
			      //CR337.en
			      Query q = session.createQuery(" from ConfigAppEntity where services='" + mapService + "'");
			      Iterator it = q.list().iterator();
			      while (it.hasNext())
			      {
			        ConfigAppEntity configApp = (ConfigAppEntity)it.next();
			        if (configApp.isStatus())
			        {
			          mapServiceConfig = 1;
			        }

			      }

			      Query query = null;
			      if (mapServiceConfig == 1)
			      {
			        iLogger.info("Asset Event Address Update Scheduler");
			        iLogger.info("-------------------------------------");
			        iLogger.info("AssetEventId, Latitude, Longitude, Address");
			        iLogger.info("-------------------------------------");

			        boolean flagContinue = true;
			        int recordsCount = 0;

			        if ((session == null) || (!session.isOpen()))
			        {
			          session = HibernateUtil.getSessionFactory().getCurrentSession();
			          session.getTransaction().begin();
			        }

			        String selectQuery = "select a from AssetEventEntity a";
			        String whereClause = " where (a.address is null OR a.state is null OR a.city is null) and a.Location is not null";
			        String groupbyClause = " group by a.assetEventId order by a.assetEventId desc";

			        String innerWhereClause = null;

			        if ((startDate != null) && (endDate != null))
			        {
			          innerWhereClause = " and a.created_timestamp between '" + startDate + "' and '" + endDate + "' ";
			        }
			        else if ((startDate != null) && (endDate == null))
			        {
			          innerWhereClause = " and a.created_timestamp like '" + startDate + "%' ";
			        } else {
			          DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			          Calendar calobj = Calendar.getInstance();
			          calobj.add(calobj.DAY_OF_YEAR,-1);
			          String prevDate = df.format(calobj.getTime());

			          innerWhereClause = " and a.created_timestamp like '" + prevDate + "%' ";
			        }

			        iLogger.info("AELocationDetailsService ::Final Query->" + selectQuery + whereClause + innerWhereClause + groupbyClause);

			        query = session
			          .createQuery(selectQuery + whereClause + innerWhereClause + groupbyClause);
			        
			        iLogger.info("AELocationDetailsService ::Final Query Count :"+query.list().size());

			        Iterator itr = query.list().iterator();
			        Object[] result = (Object[])null;

			        int rowUpdateCounter = 0;

			        while (itr.hasNext())
			        {
			          AssetEventEntity assetEvent = (AssetEventEntity)itr.next();
			          rowUpdateCounter++;

			          //LocationDetails loc = null;
			          LocationDetailsMMI loc = null;
			          try
			          {
			            String address = null;
			            String latitude = null;
			            String longitude = null;
			            assetMonitoringParameters = new LinkedList();
			            assetMonitoringValues = new LinkedList();

			            AssetEntity serial_number = assetEvent.getSerialNumber();

			            if (assetEvent.getLocation() == null)
			            {
			              continue;
			            }

			            String[] location = assetEvent.getLocation().split(",");
			            if (location[0] != null)
			              latitude = location[0];
			            if (location[1] != null) {
			              longitude = location[1];
			            }

			            if ((latitude != null) && (longitude != null))
			            {
			            	// Leela - commenting Bcz of using mmi instead og google
			              //loc = GetSetLocationJedis.getLocationDetails(latitude, longitude, redisPool);
			            	loc = GetSetLocationJedis.getLocationDetailsMMI(latitude, longitude);

			              if (loc == null) {
			                //loc = new LocationByLatLon().getLocationDetails(longitude, latitude);
			            	  loc = GetSetLocationJedis.getLocationDetailsMMI(latitude, longitude);
			              }

			            }

			            if (loc.getAddress() != null)
			            {
			              iLogger.info("Update AE Address for :" + assetEvent.getAssetEventId() + "," + latitude + "," + longitude + "," + loc.getAddress());
			              assetEvent.setAddress(loc.getAddress());
			            }
			            if (loc.getState() != null)
			            {
			              assetEvent.setState(loc.getState());
			            }
			            if (loc.getCity() != null)
			            {
			              assetEvent.setCity(loc.getCity());
			            }

			            if ((session == null) || (!session.isOpen()))
			            {
			              session = HibernateUtil.getSessionFactory().getCurrentSession();
			              session.getTransaction().begin();
			            }
			            if (loc.getAddress() != null) {
			              session.update(assetEvent);
			            }
			          }
			          catch (Exception e)
			          {
			            fLogger.fatal(e);
			          }

			          if ((session == null) || (!session.isOpen()))
			          {
			            session = HibernateUtil.getSessionFactory().getCurrentSession();
			            session.getTransaction().begin();
			          }
			          if (rowUpdateCounter % 500 == 0)
			          {
			            iLogger.info("AELocationDetailsService rowUpdateCounter for :" + rowUpdateCounter + " rows");

			            rowUpdateCounter = 0;

			            if ((session != null) && (session.isOpen()))
			            {
			              session.flush();
			              if (session.getTransaction().isActive())
			              {
			                session.getTransaction().commit();
			              }
			            }

			          }

			        }

			        if (session.isOpen())
			        {
			          session.flush();
			        }

			        if (session.getTransaction().isActive())
			        {
			        	iLogger.info("AELocationDetailsService Final Commit if records not under 500");
			          session.getTransaction().commit();
			        }
			      }

			    }
			    catch (Exception e)
			    {
			      fLogger.fatal("Exception :" + e);

			      if ((session != null) && (session.isOpen()))
			      {
			        session.close();
			      }

			    }
			    finally
			    {
			      try
			      {
			        if ((session != null) && (session.getTransaction().isActive())) {
			          session.getTransaction().commit();
			        }
			      }
			      catch (Exception e)
			      {
			        fLogger.fatal(e);
			      }
			      finally
			      {
			        if ((session != null) && (session.isOpen()))
			        {
			          session.flush();
			          session.close();
			        }
			      }

			    }

			    return status;
			  }
			
			
			//************************************************** Update Event Generated Location *****************************************************
			/** DefectID: 20131024 - Rajani Nagaraju - Update Adress in AssetEvent 
			 * This method updates the Adrress of the machine Location for when the Event got generated based on Event Generated Time and packet Snapshot time
			 * @return
			 */
			public String migrateAELocation()
			{
				String status="SUCCESS";
				Logger iLogger = InfoLoggerClass.logger;
				
		    	Logger fLogger = FatalLoggerClass.logger;
				//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		        
		        Session session = HibernateUtil.getSessionFactory().openSession();
		        //session.beginTransaction();
		        Session session1 = null;
		        session1 = HibernateUtil.getSessionFactory().openSession();
		        session1.beginTransaction();
		        List<String> assetMonitoringParameters = null;
				List<String> assetMonitoringValues = null;
				Calendar cal = Calendar.getInstance();
		        try
		        {
		        	//Check whether the MapService is enabled for the Application
		        	Properties prop = new Properties();
		        	prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
					String mapService = prop.getProperty("MapService");
		        	int mapServiceConfig =0;
		        	
		        	Query q = session.createQuery(" from ConfigAppEntity where services='"+mapService+"'");
		        	Iterator it = q.list().iterator();
		        	while(it.hasNext())
		        	{
		        		ConfigAppEntity configApp = (ConfigAppEntity)it.next();
		        		if(configApp.isStatus())
		        		{
		        			mapServiceConfig =1;
		        		}
		        		
		        	}
		        	
		        	if(mapServiceConfig==1)
		        	{
		        		iLogger.info("Asset Event Address Update Scheduler");
		        		iLogger.info("-------------------------------------");
		        		iLogger.info("AssetEventId, Latitude, Longitude, Address");
		        		iLogger.info("-------------------------------------");
		        		//DF30150710 - Rajani Nagaraju - Populate Address only for the fields with NULL and not for undefined 
		        		/*Query query = session.createQuery(" select a, CAST(GROUP_CONCAT(d.parameterName) As string ) as parameterNames," +
			        			" CAST(GROUP_CONCAT(c.parameterValue) As string ) as parameterValues" +
			        			" from AssetEventEntity a, AssetMonitoringHeaderEntity b, AssetMonitoringDetailEntity c," +
			        			" MonitoringParameters d " +
			        			" where a.serialNumber= b.serialNumber" +
			        			" and a.eventGeneratedTime = b.transactionTime " +
			        			" and b.transactionNumber = c.transactionNumber " +
			        			" and c.parameterId = d.parameterId" +
			        			" and d.parameterName in ('Latitude','Longitude')" +
			        			" and ( a.address is null OR a.address like 'undefined' OR a.state is null OR a.state like 'undefined' OR a.city is null OR a.city like 'undefined' )" +
			        			" group by a.assetEventId");*/
		        		
		        		/*Query query = session.createQuery(" select a, CAST(GROUP_CONCAT(d.parameterName) As string ) as parameterNames," +
			        			" CAST(GROUP_CONCAT(c.parameterValue) As string ) as parameterValues" +
			        			" from AssetEventEntity a, AssetMonitoringHeaderEntity b, AssetMonitoringDetailEntity c," +
			        			" MonitoringParameters d " +
			        			" where a.serialNumber= b.serialNumber" +
			        			" and a.eventGeneratedTime = b.transactionTime " +
			        			" and b.transactionNumber = c.transactionNumber " +
			        			" and c.parameterId = d.parameterId" +
			        			" and d.parameterName in ('Latitude','Longitude')" +
			        			" and ( a.address is null OR a.state is null OR a.city is null )" +
			        			" group by a.assetEventId");*/
		        		boolean loopFlag = true;
		        		//int firstResult = 0;
		        		int maxResult = 1000;
		        		int rowCount =0;
		        		while(loopFlag){
		        		Query query = session.createQuery(" select a"+
			        			" from AssetEventEntity a"+
			        			" where Location is null order by eventGeneratedTime desc");
		        		//query.setFirstResult(firstResult);
		        		query.setMaxResults(maxResult);
			        	Iterator itr = query.list().iterator();
			        	Object[] result=null;
			        	
			        	//int rowUpdateCounter=0;
			        	DynamicAMH_DAL amhDAL = new DynamicAMH_DAL();
			        	List parameterIDList = new LinkedList();
			        	parameterIDList.add(1);
			        	parameterIDList.add(2);
		
			        	int cur_week = cal.get(Calendar.WEEK_OF_YEAR);
			        	int transactionalWeek = 0;
			        	while(itr.hasNext())
			        	{
			        		
			        		
			        		
			        		if(session1 ==null || !session1.isOpen())
				            {     
			        			 session1 = HibernateUtil.getSessionFactory().getCurrentSession();
			        			 session1.beginTransaction();
				            }
			        		//result = (Object[])itr.next();
			        		AssetEventEntity assetEvent = (AssetEventEntity)itr.next();
			        		
			        		if(assetEvent == null)
			        			continue;
			        		System.out.println("rowUpdateCounter: "+(++rowCount) +" event_generated_time "+assetEvent.getEventGeneratedTime() );
			        		cal.setTime(assetEvent.getEventGeneratedTime());
			        		transactionalWeek = cal.get(Calendar.WEEK_OF_YEAR);
			        		if(cur_week - transactionalWeek > 14){
			        			System.out.println("breaking loop : transactionalWeek "+transactionalWeek );
			        			loopFlag = false;
			        			break;
			        			
			        		}
			        		
			        		//rowUpdateCounter++;
			        		
			        		
			        		try
			        		{
			        			/*String address =null;
			        			String latitude=null;
				        		String longitude = null;*/
				        		/*assetMonitoringParameters = new LinkedList<String>();
				        		assetMonitoringValues = new LinkedList<String>();*/
				        		
				        		
				        		AssetEntity serial_number = assetEvent.getSerialNumber();
				        		iLogger.info("EventDetailsBO: migrateAELocation : before DAL call ");
				        		AssetMonitoringParametersDAO daoObject = amhDAL.getLocationONTxn(serial_number.getSerial_number().getSerialNumber(), 
				        				assetEvent.getEventGeneratedTime(), serial_number.getSegmentId(), parameterIDList);
				        		iLogger.info("EventDetailsBO: migrateAELocation : after DAL call ");
				        		if(daoObject == null){
				        			iLogger.info("EventDetailsBO: migrateAELocation : "+assetEvent.getEventGeneratedTime()+" packet is purged from AMD");
				        			continue;
				        		}
				        		/*if(result[1]!=null)	
									assetMonitoringParameters = Arrays.asList(result[1].toString().split(","));
								
								if(result[2] !=null)
									assetMonitoringValues = Arrays.asList(result[2].toString().split(","));*/
							
				        		/*if(daoObject.getParameterName()!=null)
				        			assetMonitoringParameters = Arrays.asList(daoObject.getParameterName().split(","));*/
				        		if(daoObject.getParameterValue()!=null){
				        			assetEvent.setLocation(daoObject.getParameterValue().toString());
				        			session1.update(assetEvent);
				        			iLogger.info("EventDetailsBO: migrateAELocation : location is : "+daoObject.getParameterValue().toString());
				        		}
				        		
			        		}
			        	
			        		catch(Exception e)
			        		{
			        			e.printStackTrace();
			        			fLogger.fatal(e);
			        		}
							
			        		if(rowCount % 1000 == 0)
							{
			        			iLogger.info("Update AE Address for :"+rowCount+" rows");
			        			//rowUpdateCounter=0;
								try
								{ 
									
									if (session1.getTransaction().isActive()) 
									{
										
										session1.getTransaction().commit();
									}
								}
								catch(Exception e)
								{
									fLogger.fatal(e);
									
									if (session1.isOpen()) 
									{
										session1.clear();
									}
									if (session1.isOpen()) 
									{
										session1.close();
									}
								}
								
								finally
								{
									if (session1.isOpen()) 
									{
										session1.flush();
										session1.close();
									}
								}
								
							}
			        	}
			        		
			        	//firstResult += maxResult;
			        	}
		        	}
		        }
		        
		        catch(Exception e)
				{
		        	e.printStackTrace();
					fLogger.fatal("Exception :"+e);
					
					/*if (session.isOpen()) 
					{
						session.clear();
					}*/
					if (session !=null && session.isOpen()) 
					{
						session.close();
					}
				}
		        
		        finally
		        {
		        	try
					{
						if (session1 != null && session1.getTransaction().isActive()) 
							session1.getTransaction().commit();
					}
					
					catch(Exception e)
					{
						fLogger.fatal(e);
					}

					finally
					{
						if (session.isOpen()) 
						{
							//session.flush();
							session.close();
						}
						if (session1.isOpen()) 
						{
							session1.flush();
							session1.close();
						}
					}
		              
		        }
				
				return status;
			}
			
	/**
	 * method to add pending request to respective table
	 * @param emailTemplate
	 * @param smsTemplate
	 * @return status
	 */
	public String addToPending(EmailTemplate emailTemplate,SmsTemplate smsTemplate){
		String status = null;
		Logger iLogger = InfoLoggerClass.logger;
		
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
        try
        {
        	if(emailTemplate !=null)
        	{
        		//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
        		iLogger.info(emailTemplate.getSerialNumber()+":"+emailTemplate.getTransactionTime()+"  :"+"Insert into EmailPending ");
        		EmailPendingEntity emailPending = new EmailPendingEntity();
        		emailPending.setEmailId(emailTemplate.getTo());
        		emailPending.setEmailSubject(emailTemplate.getSubject());
        		emailPending.setEmailBody(emailTemplate.getBody());
        		emailPending.setGeneratedTime(new Timestamp(new java.util.Date().getTime()));
        		session.save(emailPending);
        		status="request added to Email Pending ";
        	}
        	else if(smsTemplate!=null){
        		List<String> smsTo = smsTemplate.getTo();
        		List<String> smsBody = smsTemplate.getMsgBody();
        		SmsPendingEntity smsPending = null;
        		for(int index=0;index<smsTo.size();index++)
        		{
        			//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
        			iLogger.info(smsTemplate.getSerialNumber()+":"+smsTemplate.getTransactionTime()+"  :"+"Insert into SMS Pending for :"+smsTo.get(index));
        			smsPending = new SmsPendingEntity();
            		smsPending.setMobileNumber(smsTo.get(index));
            		smsPending.setSmsBody(smsBody.get(index));
            		smsPending.setGeneratedTime(new Timestamp(new java.util.Date().getTime()));
            		session.save(smsPending);
            		status="request added to SMS Pending ";
        		}
        		
        	}
        }
        
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        finally{
        	if(session.getTransaction().isActive()){
                  session.getTransaction().commit();
            }            
            if(session.isOpen()){
                  session.flush();
                  session.close();
            }
        }
		return status;
	}
	/**
	 * method to delete an entry from pending table 
	 * @param msgType
	 * @param reqId
	 * @return String
	 */
	//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
	public String deleteEntry(String msgType,int reqId, String serialNumber, String transactionTime)
	{
		Logger iLogger = InfoLoggerClass.logger;
		
    	
		iLogger.info(serialNumber+":"+transactionTime+"  :"+"deleteEntry  from Pending Table");
		String status = null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
        try{
        	int rows = 0;
        	if(msgType.equals("email")){
        		Query deleteQuery = session.createQuery("DELETE FROM EmailPendingEntity WHERE reqId ="+reqId);
        		rows = deleteQuery.executeUpdate();
        		if(rows>0){
        			status = "SUCCESS";
        			iLogger.info(serialNumber+":"+transactionTime+"  :"+rows +" got deleted from table email_pending");
        		}
        		
        	}
        	else if(msgType.equals("sms")){
        		Query deleteQuery = session.createQuery("DELETE FROM SmsPendingEntity WHERE reqId ="+reqId);
        		rows = deleteQuery.executeUpdate();
        		if(rows>0){
        			status = "SUCCESS";
        			iLogger.info(serialNumber+":"+transactionTime+"  :"+rows +" got deleted from table sms_pending");
        		}
        	//	infoLogger.info(rows +" got deleted from table sms_pending");
        	}
        	
        }
        
        catch(Exception e)
        {
        	iLogger.info(serialNumber+":"+transactionTime+"  :"+" Error while deleting rows from Pending table!!");
        	status ="failure";
        	e.printStackTrace();
        }
        
        finally
        {
        	if(session.getTransaction().isActive())
        	{
                  session.getTransaction().commit();
            }            
            if(session.isOpen()){
                  session.flush();
                  session.close();
            }
        }
       return status;
	}
	/**
	 * method to read message pending tables and send messages
	 * @return String
	 * @throws CustomFault
	 */
	public String sendPendingMessage() throws CustomFault{
		Logger iLogger = InfoLoggerClass.logger;
		
    	
		iLogger.info("sendPendingMessage : start");
		String status = "Success";
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = null;
        Object[] resultListArray = null;
        List resultList = null;
        Iterator iterator = null;
        EmailTemplate emailTemplate = null;
        SmsTemplate smsTemplate = null;
        int reqId =0;
        List<String> toList = null;
        List<String> smsBodyList = null;
        try{
        	
        	//read from sms pending table
        	query = session.createQuery("SELECT reqId,mobileNumber,smsBody FROM SmsPendingEntity");
        	resultList = query.list();
        	iLogger.info("Selected rows size from SmsPending : "+resultList.size());
        	iterator = resultList.iterator();
        	while(iterator.hasNext()){
        		resultListArray = (Object[])iterator.next();
        		smsTemplate = new SmsTemplate();
        		if(resultListArray[0]!=null){
        			reqId = (Integer)resultListArray[0];
        		}
        		if(resultListArray[1]!=null){
        			toList = new ArrayList<String>();
        			toList.add((String)resultListArray[1]);
        			smsTemplate.setTo(toList);
        		}
        		
        		if(resultListArray[2]!=null){
        			smsBodyList = new ArrayList<String>();
        			smsBodyList.add((String)resultListArray[2]);
        			smsTemplate.setMsgBody(smsBodyList);
        		}
        		//new SmsHandler().handleSms("jms/queue/smsQ", smsTemplate,reqId);
        		//DF20171016 - KO369761 : Changed SMSQ Service to Kafka Queue.
        		new SmsHandler().handleSmsInKafka("SMSQueue", smsTemplate,reqId);
        	}
        	
        	if(!(session.isOpen() )){
        		session = HibernateUtil.getSessionFactory().openSession();
				session.beginTransaction();
			}
        	//read from email pending table
        	query = session.createQuery("SELECT reqId,emailId,emailSubject,emailBody FROM EmailPendingEntity");
        	resultList = query.list();
        	iLogger.info("Selected rows size from EmailPending : "+resultList.size());
        	iterator = resultList.iterator();
        	while(iterator.hasNext()){
        		resultListArray = (Object[])iterator.next();
        		emailTemplate = new EmailTemplate();
        		if(resultListArray[0]!=null){
        			reqId = (Integer)resultListArray[0];
        		}
        		if(resultListArray[1]!=null){
        			emailTemplate.setTo((String)resultListArray[1]);
        		}
        		if(resultListArray[2]!=null){
        			emailTemplate.setSubject((String)resultListArray[2]);
        		}
        		if(resultListArray[3]!=null){
        			emailTemplate.setBody((String)resultListArray[3]);
        		}
        		//new EmailHandler().handleEmail("jms/queue/emailQ", emailTemplate,reqId);
        		//DF20171016 - KO369761 : Changed EmailQ Service from hornet to Kafka Queue.
        		new EmailHandler().handleEmailInKafka("EmailQueue", emailTemplate,reqId);
        	}   	
        	
        }
        catch(Exception e){
        	iLogger.info("Problem while sending message !");
        	status ="failure";
        	e.printStackTrace();
        	throw new remote.wise.exception.CustomFault("Problem while sending message !! ");
        }
        finally{
        	if(session.getTransaction().isActive()){
                  session.getTransaction().commit();
            }            
            if(session.isOpen()){
                  session.flush();
                  session.close();
            }
        }
        iLogger.info("sendPendingMessage : end");
		return status;
	}
	
	public String setSmsListernerDetails(String gPRS_NA,
			String gPRS_Service_NA, String gPRS_Sever_comm, String hMR,
			String ext_battery_vol, String int_battery_vol,
			String serial_number, String alert1, String alert2, String alert3,
			String alert4, String tow_away, String sms_date,String GPS_Fix) {
		// TODO Auto-generated method stub
		Logger iLogger = InfoLoggerClass.logger;
		
    	Logger fLogger = FatalLoggerClass.logger;
    	
		String status = "SUCCESS";

		List<ContactEntity> smsUsers = new LinkedList<ContactEntity>();

		String smsNotification = null;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try{

			String SmsServiceCofiguration = null;
			String sms1_Date=null;
			int lowengoil1=0;
			int highColTemp1=0;
			int waterFuel1=0;
			int blkAirFilter1=0;
			int towAlert1=0;
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			SmsServiceCofiguration = prop.getProperty("SmsServiceCofiguration");
			smsNotification = prop.getProperty("SmsPreferenceCatalog");
			lowengoil1=Integer.parseInt(prop.getProperty("lowEngineOil"));
			highColTemp1=Integer.parseInt(prop.getProperty("highCoolantTemp"));
			waterFuel1=Integer.parseInt(prop.getProperty("waterinFuel"));
			blkAirFilter1=Integer.parseInt(prop.getProperty("blockedAirFilter"));
			towAlert1=Integer.parseInt(prop.getProperty("towawayalert"));
			sms1_Date=prop.getProperty("smsdate");
			String sms_Year=sms_date.substring(0, 4);
			String currentTime=null;
			Timestamp currentTime1 = new Timestamp(new Date().getTime());
			
			if(sms_date!=null && (!sms_date.isEmpty())){
				
				if(sms_Year!=null && sms1_Date!=null)
				{
					if(sms_Year.equalsIgnoreCase(sms1_Date))
					{
						currentTime = currentTime1.toString();
					}
					else
					{
						SimpleDateFormat gmtDateFormat = new SimpleDateFormat(
						"yyyyMMddHHmmss");
						Date x = gmtDateFormat.parse(sms_date);
						gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:Ss");
						currentTime = gmtDateFormat.format(x);  
						//System.out.println("datestring"+currentTime);
					}
				}
			}
			String low_engine_oil=null;
			String high_Coolant_Temp=null;
			String water_in_Fuel=null;
			String blocked_Air_Filter=null;
			String tow_away_alert=null;
			String lop_status=null; 
			String hct_status=null;
			String wif_status=null;
			String baf_status=null;
			String tow_away_status=null;
			
			List<String> alertList=new LinkedList<String>();
			List<String> alertIdList=new LinkedList<String>();
			List<Integer> smsalertIdList=new LinkedList<Integer>();
			alertList.add(alert4);
			alertList.add(alert3);
			alertList.add(alert2);
			alertList.add(alert1);
			alertList.add(tow_away);
			
            for(int i=0;i<alertList.size();i++){

        		if(alertList.get(i).equalsIgnoreCase("100") ||alertList.get(i).equalsIgnoreCase("101"))
        		{
        		String alt1=alertList.get(i);
        		low_engine_oil=alt1.substring(0, 2);
        		lop_status=alt1.substring(2);
        		if(low_engine_oil!=null)
        		{
        			alertIdList.add(low_engine_oil);
        		}
        		}
        		else if(alertList.get(i).equalsIgnoreCase("110") ||alertList.get(i).equalsIgnoreCase("111"))
        		{
            		String alt1=alertList.get(i);
            		high_Coolant_Temp=alt1.substring(0, 2);
            		hct_status=alt1.substring(2);
            		if(high_Coolant_Temp!=null)
            		{
            			alertIdList.add(high_Coolant_Temp);
            		}
            		}
        		else if(alertList.get(i).equalsIgnoreCase("120") ||alertList.get(i).equalsIgnoreCase("121"))
        		{
            		String alt1=alertList.get(i);
            		water_in_Fuel=alt1.substring(0, 2);
            		wif_status=alt1.substring(2);
            		if(water_in_Fuel!=null)
            		{
            			alertIdList.add(water_in_Fuel);
            		}
            		}
        		else if(alertList.get(i).equalsIgnoreCase("130") ||alertList.get(i).equalsIgnoreCase("131"))
        		{
            		String alt1=alertList.get(i);
            		blocked_Air_Filter=alt1.substring(0, 2);
            		baf_status=alt1.substring(2);
            		if(blocked_Air_Filter!=null)
            		{
            			alertIdList.add(blocked_Air_Filter);
            		}
            		}
        		else if(alertList.get(i).equalsIgnoreCase("220") ||alertList.get(i).equalsIgnoreCase("221"))
        		{
            		String alt1=alertList.get(i);
            		tow_away_alert=alt1.substring(0, 2);
            		tow_away_status=alt1.substring(2);
            		if(tow_away_alert!=null)
            		{
            			alertIdList.add(tow_away_alert);
            		}
            		}
            }
			
			/*low_engine_oil=alert1.substring(0, 2);
			lop_status=alert1.substring(2);
			high_Coolant_Temp=alert2.substring(0, 2);
			hct_status=alert2.substring(2);
			water_in_Fuel=alert3.substring(0, 2);
			wif_status=alert3.substring(2);
			blocked_Air_Filter=alert4.substring(0, 2);
			baf_status=alert4.substring(2);
			tow_away_alert=tow_away.substring(0, 2);
			tow_away_status=tow_away.substring(2);
			
			alertIdList.add(tow_away_alert);
			alertIdList.add(low_engine_oil);
			alertIdList.add(blocked_Air_Filter);
			alertIdList.add(water_in_Fuel);
			alertIdList.add(high_Coolant_Temp);*/

			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			SmsEventDetailsEntity smsEventDetails = null;
			//Only add the data to SMSEventDetalis 

			if(gPRS_NA!=null && gPRS_Sever_comm!=null && gPRS_Service_NA!=null && hMR!=null && int_battery_vol!=null && serial_number!=null ){

				smsEventDetails = new SmsEventDetailsEntity();
				smsEventDetails.setGprsNotAvailable(Integer.parseInt(gPRS_NA));
				smsEventDetails.setGprsServerCommFailed(Integer.parseInt(gPRS_Sever_comm));
				smsEventDetails.setGrpsServiceNotAllowed(Integer.parseInt(gPRS_Service_NA));
				smsEventDetails.setCmhr(hMR);
				smsEventDetails.setInternalBatteryLevel(int_battery_vol);
				smsEventDetails.setBatteryVoltage(ext_battery_vol);
				smsEventDetails.setSerialNumber(serial_number);
				smsEventDetails.setEventGeneratedTime(Timestamp.valueOf(currentTime));	
				session.save(smsEventDetails);

			}

			//Add the alert Info to sms_event table
			if(alertIdList!=null){

				for(int i=0;i<alertIdList.size();i++){

					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}

					SmsEventEntity smsevent = new SmsEventEntity();
					smsevent.setSmsEventId(smsEventDetails);

					if(alertIdList.get(i).equalsIgnoreCase("10"))
					{
						if(lop_status!=null && lop_status.equalsIgnoreCase("1"))
						{
							smsevent.setEventId(lowengoil1);
							smsevent.setStatus("1");
							smsalertIdList.add(lowengoil1);
						}
						else
						{
							smsevent.setEventId(lowengoil1);
							smsevent.setStatus("0");
						}
					}
					else if(alertIdList.get(i).equalsIgnoreCase("11"))
					{
						if(hct_status!=null && hct_status.equalsIgnoreCase("1"))
						{
							smsevent.setEventId(highColTemp1);
							smsevent.setStatus("1");
							smsalertIdList.add(highColTemp1);
						}
						else
						{
							smsevent.setEventId(highColTemp1);
							smsevent.setStatus("0");
						}
					}
					else if(alertIdList.get(i).equalsIgnoreCase("12"))
					{
						if(wif_status!=null && wif_status.equalsIgnoreCase("1"))
						{
							smsevent.setEventId(waterFuel1);
							smsevent.setStatus("1");
							smsalertIdList.add(waterFuel1);
						}
						else
						{
							smsevent.setEventId(waterFuel1);
							smsevent.setStatus("0");
						}
					}
					else if(alertIdList.get(i).equalsIgnoreCase("13"))
					{
						if(baf_status!=null && baf_status.equalsIgnoreCase("1"))
						{
							smsevent.setEventId(blkAirFilter1);
							smsevent.setStatus("1");
							smsalertIdList.add(blkAirFilter1);
						}
						else
						{
							smsevent.setEventId(blkAirFilter1);
							smsevent.setStatus("0");
						}
					}
					else if(alertIdList.get(i).equalsIgnoreCase("22"))
					{
						if(tow_away_status!=null && tow_away_status.equalsIgnoreCase("1"))
						{
							smsevent.setEventId(towAlert1);
							smsevent.setStatus("1");
							smsalertIdList.add(towAlert1);
						}
						else
						{
							smsevent.setEventId(towAlert1);
							smsevent.setStatus("0");
						}	
					}
					session.save(smsevent);
				}
			}
			boolean isSms = false; 
			boolean isEmergencyAlert = false;
			String eventSeverity =null;
			EventTypeEntity eventTypeId_ee =null;
			if(smsalertIdList!=null){

				for(int i=0;i<smsalertIdList.size();i++){

					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}
					Query queryEvt = session.createQuery("select a.eventSeverity,a.isSms,a.eventTypeId from EventEntity a where a.eventId = '"+smsalertIdList.get(i)+"' ");
					Iterator itr = queryEvt.list().iterator();
					Object[] evtResultSet = null;
					while(itr.hasNext())
					{
						evtResultSet = (Object[])itr.next();
						if(evtResultSet[0]!=null){
							eventSeverity = (String)evtResultSet[0];
						}
						if(evtResultSet[1]!=null)
						{
							if((Boolean) evtResultSet[1])
							{
								isSms = true;
							}
						}
						if(evtResultSet[2]!=null){
							eventTypeId_ee = (EventTypeEntity)evtResultSet[2];
						}
                      //  System.out.println("eventSeverity"+eventSeverity); 
                       // System.out.println("smsalertIdList.get(i)"+smsalertIdList.get(i)+"+"+isSms);
                        
					}
					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}
					Query configQuery = session.createQuery(" from ConfigAppEntity where services like '"+SmsServiceCofiguration+"'");
					Iterator configItr = configQuery.list().iterator();
					while(configItr.hasNext())
					{
						ConfigAppEntity configAppObj = (ConfigAppEntity)configItr.next();
						if(!(configAppObj.isStatus()))
						{
							isSms=false;
						}
					}
					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}
					//get the contacts for SMS
					if(isSms)
					{
						//get the data from EventSubscribers for the given serial number
						Query query1 = session.createQuery("from EventSubscriptionMapping where serialNumber = '"+serial_number+"' ");
						Iterator itr1 = query1.list().iterator();
						while(itr1.hasNext())
						{
							EventSubscriptionMapping eventSubscribers = (EventSubscriptionMapping)itr1.next();
							smsUsers.add(eventSubscribers.getContactId());
							//System.out.println("smsUsers"+smsUsers);
						}
					}
					List<ContactEntity> finalContactEntity = new LinkedList<ContactEntity>();
					finalContactEntity.addAll(smsUsers);

					HashMap<ContactEntity, List<Integer>> smsContactEventId = new HashMap<ContactEntity,List<Integer>>();
					HashMap<ContactEntity, List<Integer>> smsContactEventTypeId = new HashMap<ContactEntity,List<Integer>>();

					if( ! (finalContactEntity==null || finalContactEntity.isEmpty()) )
					{
						for(int t=0; t<finalContactEntity.size(); t++)
						{
							if(! (session.isOpen() ))
							{
								session = HibernateUtil.getSessionFactory().getCurrentSession();
								session.getTransaction().begin();
							}
							//System.out.println("final contact"+finalContactEntity.get(t).getContact_id());
							//If the user is CC/Admin - Event wise preference 
							if( (finalContactEntity.get(t).getRole().getRole_name().equalsIgnoreCase("Customer Care"))
									|| (finalContactEntity.get(t).getRole().getRole_name().equalsIgnoreCase("JCB Admin")) )
							{
								//System.out.println("Inside final event id contact list");
								Query q = session.createQuery(" select d.eventId " +
										" from PreferenceEntity a, CatalogValuesEntity b, PreferenceCatalogEntity c, EventEntity d " +
										" where a.catalogValueId = b.catalogValueId and b.catalogId = c.catalogId" +
										" and b.catalogValue = d.eventName and c.catalogName like 'SMS'" +
										" and a.contact='"+finalContactEntity.get(t).getContact_id()+"' ");
								Iterator smsItr = q.list().iterator();
								List<Integer> eventIdList = new LinkedList<Integer>();
								while(smsItr.hasNext())
								{
									int eventId = (Integer)smsItr.next();
									eventIdList.add(eventId);
								}

								if(!(eventIdList.isEmpty()))
								{
									smsContactEventId.put(finalContactEntity.get(t), eventIdList);
								}
							}

							//Any users other than CC/Admin - Event Type wise preference
							else
							{
								//System.out.println("Inside final event type id contact list");
								Query q = session.createQuery(" select d.eventTypeId " +
										" from PreferenceEntity a, CatalogValuesEntity b, PreferenceCatalogEntity c, EventTypeEntity d " +
										" where a.catalogValueId = b.catalogValueId and b.catalogId = c.catalogId" +
										" and b.catalogValue = d.eventTypeName and c.catalogName like '"+smsNotification+"'" +
										" and a.contact='"+finalContactEntity.get(t).getContact_id()+"' ");
								Iterator smsItr = q.list().iterator();
								List<Integer> eventTypeIdList = new LinkedList<Integer>();
								while(smsItr.hasNext())
								{
									int eventTypeId = (Integer)smsItr.next();
									eventTypeIdList.add(eventTypeId);
								}

								if(!(eventTypeIdList.isEmpty()))
								{
									smsContactEventTypeId.put(finalContactEntity.get(t), eventTypeIdList);
								}
							}
						}
					}
					List<String> smsUsersToInsert = new LinkedList<String>();

					//Final SMS users
					for(int k=0; k<smsContactEventId.size(); k++)
					{
						//System.out.println("Inside final sms users");
						ContactEntity contactList = (ContactEntity)smsContactEventId.keySet().toArray()[k];
						List<Integer> eventIdList = (List<Integer>)smsContactEventId.values().toArray()[k];
						if(eventIdList.contains(smsalertIdList.get(i)))
						{
							smsUsersToInsert.add(contactList.getContact_id());
							//System.out.println("size"+smsUsersToInsert.size());
						}
					}

					for(int j=0; j<smsContactEventTypeId.size(); j++)
					{
						//System.out.println("Inside final sms users");
						ContactEntity contactList = (ContactEntity)smsContactEventTypeId.keySet().toArray()[j];
						List<Integer> eventTypeIdList = (List<Integer>)smsContactEventTypeId.values().toArray()[j];
						if(eventTypeIdList.contains(eventTypeId_ee.getEventTypeId()) )
						{
							smsUsersToInsert.add(contactList.getContact_id());
							//System.out.println("size"+smsUsersToInsert.size());
						}
					}
					//List<String> commonUsers = new LinkedList<String>();
					List<String> actualSMSUsers = new LinkedList<String>();
					//System.out.println("smsUserToInsert:"+smsUsersToInsert.get(i-1));
					actualSMSUsers.addAll(smsUsersToInsert);
					
					NotificationHandler handler = new NotificationHandler();
					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}

					iLogger.info(serialNumber+":"+serial_number+"  :"+"------ Generated Alert Details--------");
					//System.out.println(serialNumber+":"+serial_number+"  :"+"------ Generated Alert Details--------");
					iLogger.info(serialNumber+":"+serial_number+"  :"+"Event Id: "+smsalertIdList.get(i));
					//System.out.println(serialNumber+":"+serial_number+"  :"+"Event Id: "+smsalertIdList.get(i));
					iLogger.info(serialNumber+":"+serial_number+"  :"+"SMS Users:"+actualSMSUsers);
					//System.out.println(serialNumber+":"+serial_number+"  :"+"SMS Users:"+actualSMSUsers);
					
					if(!(actualSMSUsers==null || actualSMSUsers.isEmpty()))
					{
						//System.out.println("if actualsmsUser is not null");
						//System.out.println("actualSMSUsers"+actualSMSUsers.get(i));
						//To generate SMS and Email 
						List<String> mobileNum = new LinkedList<String>();
						// Custom SMS implementation
						List<String> userName = new LinkedList<String>();
						Query qw = session.createQuery("from ContactEntity where active_status=true and contact_id in (:list)").setParameterList("list", actualSMSUsers);
						Iterator itrr= qw.list().iterator();
						while(itrr.hasNext())
						{
							ContactEntity contact = (ContactEntity)itrr.next();
							mobileNum.add(contact.getPrimary_mobile_number());
							//System.out.println("mobileNum"+mobileNum);
							if(contact.getLast_name()!=null){
								userName.add(contact.getFirst_name()+" "+contact.getLast_name());
							}
							else{
								userName.add(contact.getFirst_name());
							}
							//System.out.println("userName"+userName);
						}

						//call the sms handler
						iLogger.info(serialNumber+":"+serial_number+"  :"+"------ EventDetailsBO - Send SMS to SMSusers--------");
						//System.out.println(serialNumber+":"+serial_number+"  :"+"------ EventDetailsBO - Send SMS to SMSusers--------");
						handler.smsHandler(mobileNum, userName, serial_number, eventSeverity, currentTime, smsalertIdList.get(i), isEmergencyAlert, GPS_Fix);
						//	infoLogger.info("SMS Users :"+mobileNum);
					}


					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}
				}
			}

		}
		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
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

		return status;
	}
	//************************************************** END of Update Event Generated Location *****************************************************
	
	//*********************************************** ALERT GENERATION LOGIC - START *************************************************
	public String alertGenerationLogicNew(String serialNumber, Timestamp transactionTime, HashMap<EventEntity, String> eventIdValueMap, 
										boolean getChildAlerts, boolean isEmergencyAlert, String latValue, String longValue, 
										LandmarkEntity landmark, int serviceScheduleId, String currentGPSfix, String landmarkStatus)
	{
		
		String status = "SUCCESS";
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		iLogger.info("AGS:"+serialNumber+":"+transactionTime+" AGS Processing Logic - EventDetailsBO - START");
		
		try
		{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			try
			{
				String location = null;
				Properties prop=null;
				
				//20160808 - addded by suresh - Location has been addded in the asset event table no need to go for amh , amd for location 
				if(latValue!=null && longValue!=null){
					location = latValue+","+longValue;
					iLogger.info("AGS:"+serialNumber+":"+transactionTime+": Location :"+location);
				}
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Read from property file - START");
				try
				{
					prop= StaticProperties.getConfProperty();
				}
				catch(Exception e)
				{
					fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+" -Error in intializing property File :"+e);
					return "FAILURE";
				}
				
				String TowAwayEventId = prop.getProperty("TowAwayEventId");
				String SmsServiceCofiguration = prop.getProperty("SmsServiceCofiguration");
				
				boolean smsEnabled=true;
				ListToStringConversion conversion = new ListToStringConversion();
				
				//******************************* STEP1: Get the AssetEntity of the VIN
				AssetEntity assetEntity =null;
				Query assetQ = session.createQuery(" from AssetEntity where serial_number='"+serialNumber+"'");
				Iterator assetItr = assetQ.list().iterator();
				if(assetItr.hasNext())
				{
					assetEntity = (AssetEntity) assetItr.next();
				}
				
				//******************************* STEP2: Supress the Tow-away alert if it is sent in the same Key OFF cycle (Generate the alert but no communication via SMS/Email)
				for(int i=0; i<eventIdValueMap.size(); i++)
				{
					EventEntity event = (EventEntity) eventIdValueMap.keySet().toArray()[i];
					int generateAlert=1;
					
					if(event.getEventId()== (Integer.parseInt(TowAwayEventId)) )
					{
						//Determine if the tow-away is already present in the given KeyOff Cycle
						Query towAwayEventQ = session.createQuery(" from AssetEventEntity where serialNumber='"+serialNumber+"' and activeStatus=1 and partitionKey =1 " +
								" and eventId="+event.getEventId());
						Iterator towAwayEventItr = towAwayEventQ.list().iterator();
						while(towAwayEventItr.hasNext())
						{
							AssetEventEntity assetEvent = (AssetEventEntity)towAwayEventItr.next();
							generateAlert=0;
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+assetEvent.getEventId().getEventId()+": Supress Tow-away SMS/Email, since it is in the same KeyOff Cycle");
						}
						
						if(generateAlert==0)
						{
							//Only add the data to AssetEvent - Dont generate SMS/Email and hence no entry into asset event contact
							AssetEventEntity assetEvent = new AssetEventEntity();
							assetEvent.setActiveStatus(1);
							assetEvent.setEventGeneratedTime(transactionTime);
							assetEvent.setEventId(event);
							assetEvent.setEventSeverity(event.getEventSeverity());
							assetEvent.setEventTypeId(event.getEventTypeId());
							assetEvent.setSerialNumber(assetEntity);
							assetEvent.setServiceScheduleId(serviceScheduleId);
							//DF20160803 - addded by suresh for migration of alerts to db2
							Timestamp currentTime = new Timestamp(new Date().getTime());
							assetEvent.setCreated_timestamp(currentTime);
							if(location!=null)
								assetEvent.setLocation(location);
							session.save(assetEvent);
							
							eventIdValueMap.remove(event);
							
							if(eventIdValueMap.size()==0)
								return "SUCCESS";
												
							else
								break;
						}
					}
				}
				
				
				//************************************* STEP3: Determine whether the SMS service is enabled for the application or not.
				Query smsConfigQ = session.createQuery(" from ConfigAppEntity where services like '"+SmsServiceCofiguration+"'");
				Iterator smsConfigItr = smsConfigQ.list().iterator();
				if(smsConfigItr.hasNext())
				{
				     ConfigAppEntity configAppObj = (ConfigAppEntity)smsConfigItr.next();
				     if(!(configAppObj.isStatus()))
				     {
				          smsEnabled=false;
				     }
				}
				
				
				
				for(int i=0; i<eventIdValueMap.size(); i++)
				{
					boolean isSms=true;
					boolean isEmail=true;
									
					List<ContactEntity> smsSubscribers = new LinkedList<ContactEntity>();
					List<ContactEntity> emailSubscribers = new LinkedList<ContactEntity>();
					
					//************************************ STEP4: Check the SMS and Email Communication Settings at the event level (As set by JCB Admin)
					EventEntity event = (EventEntity) eventIdValueMap.keySet().toArray()[i];
					String eventValue = (String)eventIdValueMap.values().toArray()[i];
					
					iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+": Alert Generation - START");
					
					if(event.isSms()==false)
						isSms=false;
					if(event.isEmail()==false)
						isEmail=false;
					
					//************************************ STEP5: Check for the duplicate Alert Generation - This check is there in Impl only for Health alerts
					//But to handle the same for Landmark, Fuel theft alert when the same packet is reprocessed
					//Supress the Multiple alert generation (Same event with same transaction timestamp)
					String duplicatePkt = "SELECT ae.serialNumber FROM AssetEventEntity ae WHERE ae.serialNumber='"+serialNumber+"'" +
										" AND ae.eventGeneratedTime = '"+transactionTime+"' and ae.activeStatus=1 and ae.partitionKey =1 and ae.eventId="+event.getEventId();
					Query duplicatePktQ = session.createQuery(duplicatePkt);
					if(duplicatePktQ.list().size()>0)
					{
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+": Event with active status 1 already exists - Duplicate Packet Received");
						continue;
					}
					
					
					//************************************* STEP6: Get the List of SMS users 
					//DF20160418 - Rajani Nagaraju - Communication Module - Getting the Subscriber details from OrientDB - START
					
					if(isSms==true && smsEnabled==true)
					{
						//DF20160418 - Rajani Nagaraju - Communication Module - Getting the Subscriber details from OrientDB
						//Hence commenting the below part of code
						
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+": Get SMS Subscribers - START");
						
						/*//Get the list of users subscribed for the machine and has set the SMS preference for the event
						Query smsUsersQ = session.createQuery(" select distinct eventSubs.contactId from EventSubscriptionMapping eventSubs," +
								" PreferenceEntity userPref, CatalogValuesEntity catVal " +
								" where eventSubs.serialNumber='"+serialNumber+"' " +
								" and eventSubs.contactId=userPref.contact and userPref.catalogValueId=catVal.catalogValueId" +
								" and catVal.catalogId in (1,9) " +
								" and catVal.catalogValue in ('"+event.getEventName()+"','"+event.getEventTypeId().getEventTypeName()+"')");
						Iterator smsUsersItr = smsUsersQ.list().iterator();
						while(smsUsersItr.hasNext())
						{
							ContactEntity smsContact = (ContactEntity)smsUsersItr.next();
							smsSubscribers.add(smsContact);
						}*/
						
						UserAlertSubscription alertSubs = new UserAlertSubscription();
						
						List<String> smsUserList = null;
						//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
						/*List<String> smsUserList = alertSubs.getSubscribedUsersForAlert(serialNumber, 
								transactionTime, event.getEventCode(), event.getEventTypeId().getEventTypeCode(), "SMS");*/
						
						if(alertSubs.status.equalsIgnoreCase("FAILURE"))
						{
							//Returning Failure from here so that the packet remains in alert_packet_details table that can be re-processed later
							return "FAILURE";
						}
						
						if(smsUserList!=null && smsUserList.size()>0)
						{
							String smsUserListAsString = new ListToStringConversion().getStringList(smsUserList).toString();
							Query smsContactQ = session.createQuery("from ContactEntity where contact_id in ("+smsUserListAsString+")");
							Iterator smsContactItr = smsContactQ.list().iterator();
							while(smsContactItr.hasNext())
							{
								ContactEntity smsContact = (ContactEntity)smsContactItr.next();
								smsSubscribers.add(smsContact);
							}
						}	
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+": Get SMS Subscribers - END");
					}
					
					//************************************* STEP7: Get the List of Email users
					if(isEmail==true)
					{
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+": Get Email Subscribers - START");
						
						//DF20160418 - Rajani Nagaraju - Communication Module - Getting the Subscriber details from OrientDB
						//Hence commenting the below part of code
						
						/*List<String> accCodeList = new LinkedList<String>();
						List<Integer> machineGroupIdList = new LinkedList<Integer>();
						
						String accCodeOwners = null;
						
						//Step a: Get the list of accountIds corresponding to assetownership details
						//--------------- Case1: If it is the landmark alert then the alert should go to the landmark tenancy users only
						if(landmark!=null)
						{
							accCodeOwners=" select acc.accountCode from AccountEntity acc, AssetOwnerSnapshotEntity aos, AccountTenancyMapping accTen," +
										  " LandmarkCategoryEntity lcat where aos.serialNumber='"+serialNumber+"' " +
										  " and acc.status=true and acc.account_id=aos.accountId and acc.account_id=accTen.account_id " +
										  " and accTen.tenancy_id=lcat.Tenancy_ID and lcat.ActiveStatus=1 " +
										  " and lcat.Landmark_Category_ID="+landmark.getLandmark_Category_ID().getLandmark_Category_ID();
						}
						
						//---------------- Case2: If child alerts is true, get the users up above the hierarachy, otherwise only the owner of the machine
						else
						{
							accCodeOwners = " select acc.accountCode from AccountEntity acc, AssetOwnerSnapshotEntity aos " +
												" where aos.serialNumber='"+serialNumber+"' and acc.account_id=aos.accountId and acc.status=true";
							if(getChildAlerts==false)
								accCodeOwners=accCodeOwners+" and acc.account_id="+assetEntity.getPrimary_owner_id();
						
						}
						
						Query accCodeOwnerQ = session.createQuery(accCodeOwners);
						Iterator accCodeOwnerItr = accCodeOwnerQ.list().iterator();
						while(accCodeOwnerItr.hasNext())
						{
							String accCode= (String)accCodeOwnerItr.next();
							accCodeList.add(accCode);
						}
						
						if( (accCodeList==null || accCodeList.isEmpty()) )
						{
							continue;
						}
						
						//Step b: Get the list of MachineGroup Tenancy to which the machine belong to
						String accCodeListAsString = conversion.getStringList(accCodeList).toString();
						
						if(! (accCodeList==null || accCodeList.isEmpty()) )
						{
							Query machineGroupAccQ = session.createQuery(" select accTen.account_id,cag.group_id  from " +
									" AssetCustomGroupMapping cagm, CustomAssetGroupEntity cag, AccountTenancyMapping accTen, AccountEntity acc " +
									" where cagm.serial_number='"+serialNumber+"'" +
									" and cagm.group_id=cag.group_id and cag.level=2 and cag.active_status=1 " +
									" and cag.tenancy_id=accTen.tenancy_id and accTen.account_id=acc.account_id " +
									" and acc.accountCode in ("+accCodeListAsString+") and acc.status=true");
							Iterator machineGroupAccItr = machineGroupAccQ.list().iterator();
							Object[] result=null;
							while(machineGroupAccItr.hasNext())
							{
								result = (Object[]) machineGroupAccItr.next();
								AccountEntity account= (AccountEntity) result[0];
								int groupId = (Integer)result[1];
								if(!machineGroupIdList.contains(groupId))
									machineGroupIdList.add(groupId);
								
								if(accCodeList!=null && accCodeList.contains(account.getAccountCode()))
									accCodeList.remove(accCodeList.indexOf(account.getAccountCode()));
							}
						}
						
						//Step c: Get the list of tenancy admin's of accCodeList who has set the email preference for the received event
						if(! (accCodeList==null || accCodeList.isEmpty()) )
						{
							accCodeListAsString = conversion.getStringList(accCodeList).toString();
							Query emailTAUsersQ = session.createQuery(" select distinct accCon.contact_id from AccountContactMapping accCon," +
									" PreferenceEntity userPref, CatalogValuesEntity catVal, AccountEntity acc, ContactEntity contact " +
									" where accCon.account_id= acc.account_id and accCon.contact_id=contact.contact_id and contact.is_tenancy_admin=1" +
									" and contact.active_status=1 " +
									" and acc.accountCode in ("+accCodeListAsString+")"+
									" and acc.status=true and accCon.contact_id=userPref.contact and userPref.catalogValueId=catVal.catalogValueId" +
									" and catVal.catalogId in (2,10) " +
									" and catVal.catalogValue in ('"+event.getEventName()+"','"+event.getEventTypeId().getEventTypeName()+"')");
							Iterator emailTAUsersItr = emailTAUsersQ.list().iterator();
							while(emailTAUsersItr.hasNext())
							{
								ContactEntity emailContact = (ContactEntity)emailTAUsersItr.next();
								emailSubscribers.add(emailContact);
							}
						}
						
						//Step d: Get the list of MachineGroupUsers of machineGroupAccCodeList who has set the email preference for the received event
						if(!(machineGroupIdList==null || machineGroupIdList.isEmpty()))
						{
							String machineGroupIdListAsString = conversion.getIntegerListString(machineGroupIdList).toString();
							Query emailMGUsersQ = session.createQuery(" select distinct groupUser.contact_id from GroupUserMapping groupUser," +
									" PreferenceEntity userPref, CatalogValuesEntity catVal, ContactEntity contact " +
									" where contact.active_status=1 " +
									" and groupUser.group_id in ("+machineGroupIdListAsString+")"+
									" and groupUser.contact_id=contact.contact_id " +
									" and contact.contact_id=userPref.contact and userPref.catalogValueId=catVal.catalogValueId" +
									" and catVal.catalogId in (2,10) " +
									" and catVal.catalogValue in ('"+event.getEventName()+"','"+event.getEventTypeId().getEventTypeName()+"')");
							Iterator emailMGUsersItr = emailMGUsersQ.list().iterator();
							while(emailMGUsersItr.hasNext())
							{
								ContactEntity emailContact = (ContactEntity)emailMGUsersItr.next();
								emailSubscribers.add(emailContact);
							}
						}*/

						
						UserAlertSubscription alertSubs = new UserAlertSubscription();
						List<String> emailUserList = null;
						//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
						/*List<String> emailUserList = alertSubs.getSubscribedUsersForAlert(serialNumber, 
								transactionTime, event.getEventCode(), event.getEventTypeId().getEventTypeCode(), "Email");*/
						
						if(alertSubs.status.equalsIgnoreCase("FAILURE"))
						{
							//Returning Failure from here so that the packet remains in alert_packet_details table that can be re-processed later
							return "FAILURE";
						}
						
						if(emailUserList!=null && emailUserList.size()>0)
						{
							String accCodeOwners = null;
							List<String> accCodeList = new LinkedList<String>();
							//NOTE: Landmark Alerts will be sent as an Email to the Users of the Account to which the Landmark belongs to
							if(landmark!=null)
							{
								accCodeOwners=" select acc.accountCode from AccountEntity acc, AssetOwnerSnapshotEntity aos, AccountTenancyMapping accTen," +
											  " LandmarkCategoryEntity lcat where aos.serialNumber='"+serialNumber+"' " +
											  " and acc.status=true and acc.account_id=aos.accountId and acc.account_id=accTen.account_id " +
											  " and accTen.tenancy_id=lcat.Tenancy_ID and lcat.ActiveStatus=1 " +
											  " and lcat.Landmark_Category_ID="+landmark.getLandmark_Category_ID().getLandmark_Category_ID();
								
								Query accCodeOwnerQ = session.createQuery(accCodeOwners);
								Iterator accCodeOwnerItr = accCodeOwnerQ.list().iterator();
								while(accCodeOwnerItr.hasNext())
								{
									String accCode= (String)accCodeOwnerItr.next();
									accCodeList.add(accCode);
								}
							}
						
							//NOTE: Utilization Alerts will be sent as an Email only to the Owner(Users belonging to the owner account) of the Machine
							else if(event.getEventTypeId().getEventTypeId()==3) 
							{
								Query currOwnerQ = session.createQuery("from AccountEntity where account_id='"+assetEntity.getPrimary_owner_id()+"'");
								Iterator currOwnerItr = currOwnerQ.list().iterator();
								while(currOwnerItr.hasNext())
								{
									AccountEntity acc = (AccountEntity)currOwnerItr.next();
									accCodeList.add(acc.getAccountCode());
								}
								
							}
						
							//If it is a landmark alert and no users are subscribed for receiving the landmark alert, skip the email subscription
							if(landmark==null || ( landmark!=null &&accCodeList.size()>0) )
							{
								String emailUserListAsString = new ListToStringConversion().getStringList(emailUserList).toString();
								
								if(accCodeList.size()>0) //Picking specific Users for Utilization and Landmark Alerts
								{
									String accCodeListAsString = conversion.getStringList(accCodeList).toString();
									Query emailContactQ = session.createQuery("select contact from ContactEntity contact, AccountContactMapping accCon, AccountEntity acc " +
											" where contact.contact_id=accCon.contact_id and " +
											" accCon.account_id= acc.account_id and " +
											" contact.contact_id in ("+emailUserListAsString+") and " +
											" acc.accountCode in ("+accCodeListAsString+")");
									Iterator emailContactItr = emailContactQ.list().iterator();
									while(emailContactItr.hasNext())
									{
										ContactEntity emailContact = (ContactEntity)emailContactItr.next();
										emailSubscribers.add(emailContact);
									}
								}
							
							
								else
								{
									Query emailContactQ = session.createQuery("from ContactEntity where contact_id in ("+emailUserListAsString+")");
									Iterator emailContactItr = emailContactQ.list().iterator();
									while(emailContactItr.hasNext())
									{
										ContactEntity emailContact = (ContactEntity)emailContactItr.next();
										emailSubscribers.add(emailContact);
									}
								}
								
								iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+": Get Email Subscribers - END");
							}
						}
					}
					//DF20160418 - Rajani Nagaraju - Communication Module - Getting the Subscriber details from OrientDB - END
					
					// Adding try catch block for handling SQL Exception. Since VIN+EventID+TransactionTime combination is made as Unique in DB
					//If multiple times, same event is received it should not generate alert multiple times
					
					try
					{
						//************************ Step8: Insert the event details into AssetEvent
						//Insert the data into assetEvent
						
						//if(serviceScheduleId)
						if(event.getEventTypeId().getEventTypeId() == 1 || event.getEventTypeId().getEventTypeId() == 2){
							
							int max = 100;
							int min = 50;
							int rand = min+(int)(Math.random()*((max-min)+1));
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+ "slept for "+rand+" milli seconds");
							try{
							Thread.sleep(rand);
							}catch(InterruptedException ex){
								ex.printStackTrace();
							}
							if(checkDuplicateAlert(assetEntity.getSerial_number().getSerialNumber(), event.getEventId(),0,1).size()>0){
								bLogger.error("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+": Exception :duplicate alert: an alert with the same event has been already been generated ");
								//iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+": Insert into Asset Event :"+event.getEventName());
								return "DUPLICATE_ALERT";
							}
						}
						
						Session session1 = new HibernateUtil().getSessionFactory().openSession();
						AssetEventEntity assetEvent = new AssetEventEntity();
						try{
							session1.beginTransaction();
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+": Insert into Asset Event :"+event.getEventName());
						
						assetEvent.setActiveStatus(1);
						assetEvent.setEventGeneratedTime(transactionTime);
						assetEvent.setEventId(event);
						assetEvent.setEventSeverity(event.getEventSeverity());
						assetEvent.setEventTypeId(event.getEventTypeId());
						assetEvent.setSerialNumber(assetEntity);
						assetEvent.setServiceScheduleId(serviceScheduleId);
						//DF20160803 - addded by suresh for migration of alerts to db2
						Timestamp currentTime = new Timestamp(new Date().getTime());
						assetEvent.setCreated_timestamp(currentTime);
						if(location!=null)
							assetEvent.setLocation(location);
						session1.save(assetEvent);
						
						}catch(Exception e){
							fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Exception :"+e);
						}
						finally{
							if(session1 !=null && session1.getTransaction().isActive()){
								iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+": COMMIT START");
								session1.flush();
								session1.getTransaction().commit();
								iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+": COMMIT END");
							}
							if(session1 !=null && session1.isOpen()){
								session1.close();
							}

						}
						//************************** Step9: If it is a landmark alert, insert the record into landmark log details table
						
						if(session !=null && !session.isOpen()){
							session = new HibernateUtil().getSessionFactory().openSession();
							session.beginTransaction();
						}
						if(landmark!=null)
						{
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+": Insert into Landmark Log details for :"+event.getEventName()+" and landmark:"+landmark.getLandmark_id());
							LandmarkLogDetailsEntity landmarkLogs = new LandmarkLogDetailsEntity();
							landmarkLogs.setSerialNumber(assetEntity);
							landmarkLogs.setLandmarkId(landmark);
							landmarkLogs.setTransactionTimestamp(transactionTime);
							landmarkLogs.setMachineLandmarkStatus(landmarkStatus);
							landmarkLogs.setAssetEventId(assetEvent);
							session.save(landmarkLogs);
						}
						
						//************************ Step10: Insert the sms and email subscribers into AssetEvent contact
						//Insert the record for Email Subscribers into assetEvent contact
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+": Insert into Asset Event Contact :"+event.getEventName());
						List<String> actualSMSUsers = new LinkedList<String>();
						List<String> actualEmailUsers = new LinkedList<String>();
						
						List<String> emailIdList = new LinkedList<String>();
						List<String> mobileNumList = new LinkedList<String>();
						List<String> smsUserNameList = new LinkedList<String>();
						
						if( ! (emailSubscribers==null || emailSubscribers.isEmpty()) )
						{
							for(int j=0; j<emailSubscribers.size(); j++)
							{
								actualEmailUsers.add(emailSubscribers.get(j).getContact_id());
								
								EventUsersEntity eventUsers = new EventUsersEntity();
								eventUsers.setAssetEventId(assetEvent);
								eventUsers.setContactId(emailSubscribers.get(j));
								eventUsers.setIsEmail(1);
								session.save(eventUsers);
								
								if(emailSubscribers.get(j).getPrimary_email_id()!=null)
									emailIdList.add(emailSubscribers.get(j).getPrimary_email_id());
								
							}
						}
						
						//Insert the record for SMS subscribers into asset event contact
						if( ! (smsSubscribers==null || smsSubscribers.isEmpty()) )
						{
							for(int j=0; j<smsSubscribers.size(); j++)
							{
								actualSMSUsers.add(smsSubscribers.get(j).getContact_id());
								Query assetEventContactQ = session.createQuery(" from EventUsersEntity where assetEventId="+assetEvent.getAssetEventId()+" and " +
										" contactId like '"+smsSubscribers.get(j).getContact_id()+"'");
								Iterator assetEventContactItr = assetEventContactQ.list().iterator();
								if(assetEventContactItr.hasNext())
								{
									EventUsersEntity eventUsers = (EventUsersEntity)assetEventContactItr.next();
									eventUsers.setIsSms(1);
									session.update(eventUsers);
									
								}
								else
								{
									EventUsersEntity eventUsers = new EventUsersEntity();
									eventUsers.setAssetEventId(assetEvent);
									eventUsers.setContactId(smsSubscribers.get(j));
									eventUsers.setIsSms(1);
									session.save(eventUsers);
								}
								
								if(smsSubscribers.get(j).getPrimary_mobile_number()!=null)
								{
									mobileNumList.add(smsSubscribers.get(j).getPrimary_mobile_number());
									if(smsSubscribers.get(j).getLast_name()!=null)
										smsUserNameList.add(smsSubscribers.get(j).getFirst_name()+" "+smsSubscribers.get(j).getLast_name());
									else
										smsUserNameList.add(smsSubscribers.get(j).getFirst_name());
								}
							}
						}
						
						//******************************************* Step11: Send SMS Notification
						NotificationHandler handler = new NotificationHandler();
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+":------ Generated Alert Details--------");
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+":Event: "+event.getEventId()+";  "+event.getEventName());
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+":SMS Users:"+actualSMSUsers);
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+":Email Users:"+actualEmailUsers);
						
						//Call the SMS handler
						if( ! (smsSubscribers==null || smsSubscribers.isEmpty()) )
						{
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+":Send data to SMS Handler");
							
							//DF20150317 - Rajani Nagaraju - To Link the sent SMS wih the corresponding asset event
							int assetEventId=0;
							if(assetEvent!=null)
								assetEventId=assetEvent.getAssetEventId();
							
							handler.smsHandlerNew(mobileNumList, smsUserNameList, serialNumber, assetEvent.getEventSeverity(), transactionTime.toString(), 
												event.getEventId(), isEmergencyAlert, currentGPSfix, assetEventId);
						}
						
						//******************************************* Step12: Send Email Notification
						if(! (session.isOpen() ))
						{
							session = HibernateUtil.getSessionFactory().getCurrentSession();
							session.getTransaction().begin();
						}
						if( ! (emailSubscribers==null || emailSubscribers.isEmpty()) )
						{
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+":Send data to Email Handler");
						    handler.emailHandlerNew(emailIdList, serialNumber, assetEvent.getEventSeverity(), transactionTime.toString(), 
												event.getEventId(), event.getEventDescription(), eventValue, assetEntity.getNick_name(), 
												latValue, longValue, currentGPSfix);
						}
						
						
						if(! (session.isOpen() ))
						{
							session = HibernateUtil.getSessionFactory().getCurrentSession();
							session.getTransaction().begin();
						}
					}
					
					catch(Exception e)
					{
						e.printStackTrace();
						status="FAILURE";
						fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+": Exception :"+e);
						
						Writer result = new StringWriter();
			    	    PrintWriter printWriter = new PrintWriter(result);
			    	    e.printStackTrace(printWriter);
			    	    String err = result.toString();
			    	    fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+": Exception trace: "+err);
			    	    try 
			    	    {
			    	    	printWriter.close();
			        	    result.close();
						} 
			    	    
			    	    catch (IOException e1) 
			    	    {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
			    	    
			    	    //DF20150112 - Rajani Nagaraju - If the Alert is not generated because of SQL Connection Exception, retain the file so that it would 
			    	    // be processed again in the next run.. Hence Returning FAILURE, which will not move the file.
			    	    if(e instanceof JDBCConnectionException || e instanceof SQLException || e instanceof GenericJDBCException)
			    	    {
			    	    	if(e.getMessage().contains("Cannot open connection"))
			    	    	{
			    	    		status="FAILURE";
			    	    	}
			    	    }
			    	    
					}
					
					iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+event.getEventId()+": Alert Generation - END");
					
				}
				
				
				
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				status = "FAILURE";
											
				fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Exception :" + e.getMessage());
				Writer result = new StringWriter();
	    	    PrintWriter printWriter = new PrintWriter(result);
	    	    e.printStackTrace(printWriter);
	    	    String err = result.toString();
	    	    fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Exception trace: "+err);
	    	    try 
	    	    {
	    	    	printWriter.close();
	        	    result.close();
				} 
	    	    
	    	    catch (IOException e1) 
	    	    {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
			finally
			{
				try
				{
					if(session!=null && session.isOpen()) 
					{
							if(session.getTransaction().isActive())
							{
								session.getTransaction().commit();
							}
					}
				}
				catch(Exception e)
				{
					status= "FAILURE";
					
					e.printStackTrace();
					fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Exception in DB Transaction commit:" + e.getMessage());
					Writer result = new StringWriter();
		    	    PrintWriter printWriter = new PrintWriter(result);
		    	    e.printStackTrace(printWriter);
		    	    String err = result.toString();
		    	    fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Exception in DB Transaction commit trace: "+err);
		    	    try 
		    	    {
		    	    	printWriter.close();
		        	    result.close();
					} 
		    	    
		    	    catch (IOException e1) 
		    	    {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				if(session.isOpen())
				{
					 if(session.getTransaction().isActive())
						 session.flush();
					session.close();
				}
			}
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Exception :"+e);
			
			Writer result = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			String err = result.toString();
			fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Exception trace: "+err);
			try 
			{
				printWriter.close();
				result.close();
			} 

			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			status= "FAILURE";
		}
		
		iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"AGS Processing Logic - EventDetailsBO - END");
		
		return status;
	}
	//*********************************************** ALERT GENERATION LOGIC - END *************************************************
	public List<UserAlertsImpl> getUserAlertsNew(ContactEntity loginId, String userRole, List<Integer> userTenancyIdList, List<Integer> tenancyIdList,
			String serialNumber, String startDate, List<Integer> alertTypeId, List<String> alertSeverity,
			boolean isOwnTenancyAlerts, boolean isHistory, int pageNumber) throws CustomFault{
		List<UserAlertsImpl> userAlertsList = new LinkedList<UserAlertsImpl>();
		//- Landmark Alerts should be returned for each individual Landmarks
		List<UserAlertsImpl> userAlertsList2 = new LinkedList<UserAlertsImpl>();

		TenancyBO tenancyBO = new TenancyBO();
		ReportDetailsBO reportDetailsBO = new ReportDetailsBO();
		List<AccountEntity> accountEntityList = new LinkedList<AccountEntity>();

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try
		{

			//DF20170130 @Roopa for Role based alert implementation
			DateUtil utilObj=new DateUtil();

			List<String> alertCodeList= utilObj.roleAlertMapDetails(null,userTenancyIdList.get(0), "Display");

			ListToStringConversion conversion = new ListToStringConversion();

			StringBuilder alertCodeListAsString=conversion.getStringList(alertCodeList);


			if(serialNumber!=null){
				if(serialNumber.trim().length()==7){
					String machineNumber = serialNumber;
					serialNumber = new AssetDetailsBO().getSerialNumberMachineNumber(machineNumber);
					if(serialNumber==null){//invalid machine number
						iLogger.info("Machine number "+ machineNumber + "does not exist !!!");
						return userAlertsList ;
					}
				}
			}
			//get Client Details
			Properties prop = new Properties();
			String clientName=null;
			String connIP=null; //CR337.n
			String connPort = null; //CR337.n
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");
			connIP = prop.getProperty("MDA_ServerIP");//CR337.n
			connPort = prop.getProperty("MDA_ServerPort");//CR337.n

			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
			//END of get Client Details			 
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			} 

			List<Integer> selectedTenancy = new LinkedList<Integer>();
			if(!(tenancyIdList==null || tenancyIdList.isEmpty())){
				selectedTenancy.addAll(tenancyIdList);
			}
			else
			{
				selectedTenancy.addAll(userTenancyIdList);
			}
			String customerCare = null;
			String admin = null;
			//	prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			customerCare= prop.getProperty("CustomerCare");
			admin= prop.getProperty("OEMadmin");
			ListToStringConversion conversionObj = new ListToStringConversion();
			if(isOwnTenancyAlerts==true)
			{

				//	System.out.println(" isOwnTenancyAlerts is True ");
				//check whether to look up for alerts of own tenancy

				accountEntityList = tenancyBO.getAccountEntity(userTenancyIdList);

				List<String> serialNumberList = new LinkedList<String>();
				List<String> finalSerialNumberList = new LinkedList<String>();
				boolean machineGroupUser=false;

				////////////////////////////////////// Logic to get the alert generated for the machines of specified Tenancy ////////////////////////////////

				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}

				//get a list of serial numbers for the accountEntitylist
				AssetDetailsBO assetDetails = new AssetDetailsBO();

				List<String> accountAssetList = new LinkedList<String>();
				for(int j=0; j<accountEntityList.size(); j++)
				{
					List<AssetEntity> assetEntityList = assetDetails.getAccountAssets(accountEntityList.get(j).getAccount_id());
					for(int k=0; k<assetEntityList.size(); k++)
					{
						accountAssetList.add(assetEntityList.get(k).getSerial_number().getSerialNumber());
					}
				}


				//if the user is tenancy admin get the list of serialNumbers he is accessible to
				if( (loginId.getIs_tenancy_admin()==1) || ( (userRole.equalsIgnoreCase(customerCare) || userRole.equalsIgnoreCase(admin)) ) )
				{

					//get the list of all serial numbers under the tenancy specified
					if(serialNumber!=null)
					{
						if(accountAssetList.contains(serialNumber))
						{
							finalSerialNumberList.add(serialNumber);
						}

					}
					else
					{
						finalSerialNumberList.addAll(accountAssetList);
					}

				}


				//if the user is not tenancy admin get the list of serialNumbers he is accessible to
				else if ( (loginId.getIs_tenancy_admin()==0)) 
				{
					//get the list of machines under the machine group to which the user belongs to
					AssetCustomGroupDetailsBO assetCustomGroup = new AssetCustomGroupDetailsBO();
					List<AssetCustomGroupDetailsBO> BoObj = assetCustomGroup.getAssetGroup(loginId.getContact_id(),0,0,null,userTenancyIdList,false);
					if(! (BoObj==null || BoObj.isEmpty()) )
					{
						machineGroupUser = true;
						for(int u=0; u<BoObj.size(); u++)
						{
							if(userTenancyIdList.contains(BoObj.get(u).getTenancyId()) )
								serialNumberList.addAll(BoObj.get(u).getSerialNumberList());
						}
					}

					if(serialNumber!=null)
					{
						if(serialNumberList.contains(serialNumber))
						{
							finalSerialNumberList.add(serialNumber);
						}
					}
					else

					{
						finalSerialNumberList.addAll(serialNumberList);
					}

					if(machineGroupUser==false && finalSerialNumberList.isEmpty())
					{
						if( (serialNumber!=null) )
						{
							if(accountAssetList.contains(serialNumber)) 
								finalSerialNumberList.add(serialNumber);
						}


						else
							finalSerialNumberList.addAll(accountAssetList);
					}
				}



				//Convert the SerialNumberList to comma seperated string
				String commaSeperatedString = conversionObj.getStringList(finalSerialNumberList).toString();
				iLogger.info("commaSeperatedString "+commaSeperatedString);
				iLogger.info("Call Execute Alert Query for Is Tenancy is True");

				if(! (finalSerialNumberList==null || finalSerialNumberList.isEmpty()) )
				{
					//Getting the List of Alerts
					if(pageNumber==0){
						userAlertsList = getAlertSeverityReport(commaSeperatedString, startDate, alertTypeId, alertSeverity, isOwnTenancyAlerts, isHistory, false,selectedTenancy, pageNumber,serialNumber,null,alertCodeListAsString);	
					}
					else {
						userAlertsList = executeUserAlertQueryNew(commaSeperatedString, startDate, alertTypeId, alertSeverity, isOwnTenancyAlerts, isHistory, false,selectedTenancy, pageNumber,serialNumber,null,alertCodeListAsString);
					}
				}
			}

			//////////////////////////////////////END of Logic to get the alert generated for the machines of specified Tenancy ////////////////////////////////


			/////////////////////////// Logic to get the Service,Emergency and Health Alerts of the Machines down the hierarachy ///////////////////////
			else{
				
				//DF20181102 :MA369757: getting data from MongoDB for alerts history
				if(isHistory)
				{	
				Connection prodConn = null,prodConn1=null;
				Statement stmnt = null,stmnt1=null;
				ResultSet res = null,res1=null;
				String MDAoutput=null;
				String MDAresult=null;
				HashMap<String,Object> MDAOutputMap = null;
				String AccountIdListString=null;
				int historyPeriodInDays=0;//0 for today
				UserAlertsImpl userAlertsimpl=null;
				String alertSeverityString=null, alertTypeIdString=null,selectedTenancyString=null;
				try{
					if(alertSeverity!=null && alertSeverity.size()>0){
						alertSeverityString= conversion.getStringWithoutQuoteList(alertSeverity).toString();
						alertSeverityString=ListToStringConversion.removeLastComma(alertSeverityString);}
					if(alertTypeId!=null && alertTypeId.size()>0){
						alertTypeIdString=conversion.getIntegerListString(alertTypeId).toString();}
					if(selectedTenancy!=null && selectedTenancy.size()>0)
					{
						selectedTenancyString=conversion.getIntegerListString(selectedTenancy).toString();
					}
					ConnectMySQL connSql = new ConnectMySQL();
					prodConn1 = connSql.getConnection();
					stmnt1 = prodConn1.createStatement();
					List<String> AccountIdList = new ArrayList<String>();
					String countryCode="",accountCode="";
					byte[] bytesEncoded = Base64.encodeBase64(loginId.getContact_id().getBytes());
					String encryptedLoginId=new String(bytesEncoded);
					Date inputDate=null;
					String dateString=null;
					if(selectedTenancy!=null && selectedTenancy.size()>0)
					{
						if(startDate==null)
						{
							historyPeriodInDays=7;// 7 for weekly
						}
						if(startDate!=null){
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						inputDate=dateFormat.parse(startDate);
						Date dateWithoutTime = null;
						try {
							dateWithoutTime = dateFormat.parse(dateFormat.format(new Date()));
						} catch (ParseException e1) {
							fLogger.fatal("Fatal Error :: Invoking MongoDB for alerts history :: ParseException occured while parsing dates "+e1.getMessage(),e1);
							e1.printStackTrace();
						}
						//if today
						if(inputDate.compareTo(dateWithoutTime)==0)
						{
							historyPeriodInDays=0;
						}
						else
						{//if not today based on dates
							historyPeriodInDays=7;
						}
						}
						String accountCodeQuery = "select Account_Code,CountryCode from account where Account_ID in (select Account_ID from account_tenancy where Tenancy_ID in ("
								+ selectedTenancyString + "))";
						iLogger.info("Invoking MongoDB for alerts history :: accountCodeQuery :: "+accountCodeQuery);
						res1=stmnt1.executeQuery(accountCodeQuery);
						while(res1.next())
						{
							accountCode=res1.getString("Account_Code");
							AccountIdList.add(accountCode);
							countryCode=res1.getString("CountryCode");
						}
						String tenancyTypeQuery="select Tenancy_Type_Name from tenancy_type where Tenancy_Type_ID =(select Tenancy_Type_ID from tenancy where Tenancy_ID="+selectedTenancy.get(0)+")";
						iLogger.info("Invoking MongoDB for alerts history :: tenancyTypeQuery :: "+tenancyTypeQuery);
						prodConn = connSql.getConnection();
						stmnt = prodConn.createStatement();
						res=stmnt.executeQuery(tenancyTypeQuery);
						String tenancy_type_name="";
						if(res.next())
						{
							tenancy_type_name=res.getString("Tenancy_Type_Name");
						}
						AccountIdListString=conversion.getStringWithoutQuoteList(AccountIdList).toString();
						AccountIdListString=ListToStringConversion.removeLastComma(AccountIdListString);
						String accountFilter="";
						if(tenancy_type_name!=null && !tenancy_type_name.equalsIgnoreCase(""))
						{
							if(tenancy_type_name.equalsIgnoreCase("Global"))
							{
								accountFilter=null;
								AccountIdListString=null;
								countryCode=null;
							}
							else if(tenancy_type_name.equalsIgnoreCase("Regional"))
							{
								accountFilter="RegionCode";
							}
							else if(tenancy_type_name.equalsIgnoreCase("Zonal"))
							{
								accountFilter="ZonalCode";
							}
							else if(tenancy_type_name.equalsIgnoreCase("Dealer"))
							{
								accountFilter="DealerCode";
							}
							else if(tenancy_type_name.equalsIgnoreCase("Customer"))
							{
								accountFilter="CustCode";
							}
							else
							{
								accountFilter=null;
							}
						
					
					}
						if (countryCode!=null)
						{
							countryCode=URLEncoder.encode(countryCode,"UTF-8");
						}
					try{
							// http://10.179.12.25:26030/MoolDAReports/AlertHistoryService/getClosedAlerts
						//?accountFilter=null&accountIDList=null&historyPeriodInDays=7&isOwnTenancyAlerts=false&
						//alertCodeList=null&alertSeverityList=null&alertTypeList=null&countryCode=%2B91&assetID=null&
						//pageNumber=0&sessionID=null&loginID=null
						String url=
								// Dhiraj K : 20220630 : Change IP for AWS server
								//"http://10.179.12.25:26030/MoolDAReports/AlertHistoryService/getClosedAlerts?accountFilter="
								//"http://10.210.196.206:26030/MoolDAReports/AlertHistoryService/getClosedAlerts?accountFilter="//CR337.o
								"http://"+connIP+":"+connPort+"/MoolDAReports/AlertHistoryService/getClosedAlerts?accountFilter="//CR337.n
										+ accountFilter
										+ "&accountIDList="
										+ AccountIdListString
										+ "&historyPeriodInDays="
										+ historyPeriodInDays
										+ "&isOwnTenancyAlerts=false"
										+ "&alertCodeList=null"
										+ "&alertSeverityList="
										+ alertSeverityString
										+ "&alertTypeList="
										+ alertTypeIdString
										+ "&countryCode="
										+ countryCode
										+"&assetID="
										+serialNumber
										+"&pageNumber="
										+pageNumber
										+"&sessionID=null"
										+"&loginID="
										+encryptedLoginId;
						//System.out.println("Invoking MongoDB for alerts history URL : "+url);
						iLogger.info("Invoking MongoDB for alerts history URL : "+url);
						URL MDAUrl = new URL(url);
						 HttpURLConnection conn =(HttpURLConnection) MDAUrl.openConnection();
						    conn.setRequestMethod("GET"); 
						    conn.setRequestProperty("Accept", "application/json");
							  if (conn.getResponseCode() != 200  && conn.getResponseCode() != 204) {
								  iLogger.info("MDAReports report status: FAILURE for alerts history Report loginID:"+encryptedLoginId+" ::Response Code:"+conn.getResponseCode());
								  throw new RuntimeException("Invoking MongoDB for alerts history :: Failed : HTTP error code : " +conn.getResponseCode()); 
								  }
							  iLogger.info("MDAReports report status: SUCCESS for alerts history Report loginID:"+encryptedLoginId+" ::Response Code:"+conn.getResponseCode());
								
							  BufferedReader br = new BufferedReader(new  InputStreamReader((conn.getInputStream())));
							  
							  System.out.println("Invoking MongoDB for alerts history Output from Server .... \n");
							  iLogger.info("Invoking MongoDB for alerts history Output from Server .... \n");
							  while ((MDAoutput = br.readLine()) != null) { 
								  //System.out.println("MDA json output "+MDAoutput); 
								  iLogger.info("Invoking MongoDB for alerts history json output : "+MDAoutput);
								  MDAresult =MDAoutput; } 
							  conn.disconnect();
							 // iLogger.info("Invoking MongoDB for alerts history alertCodeList : "+alertCodeListAsString);
								List<HashMap> hashMap = new Gson().fromJson(MDAresult, new TypeToken<List<HashMap<String, Object>>>(){}.getType());
								if(hashMap!=null && hashMap.size()>0)
								{
									for(int i=0;i<hashMap.size();i++)
									{	userAlertsimpl=new UserAlertsImpl();
										MDAOutputMap=hashMap.get(i);
										//iLogger.info("Invoking MongoDB for alerts history :: MDAOutputMap "+MDAOutputMap);
										 String eventId=MDAOutputMap.get("AlertCode").toString();
										 if(eventId.length()==1)
											 eventId="00"+eventId;
										 else if (eventId.length()==2)
											 eventId="0"+eventId;
										 iLogger.info("Invoking MongoDB for alerts history :: AlertCode ::"+eventId);
										 if(alertCodeList.contains(eventId))
										{
											 //iLogger.info("Invoking MongoDB for alerts history :: Matched :: AlertCode ::"+eventId);
											//Yogee- Changes for removing null values getting filled
											 if(MDAOutputMap.get("AssetID")!=null)
												 userAlertsimpl.setSerialNumber(MDAOutputMap.get("AssetID").toString());
											 else
												 continue;
											 if(MDAOutputMap.get("AlertTypeCode")!=null)
												 userAlertsimpl.setAlertTypeId(Integer.parseInt((String) MDAOutputMap.get("AlertTypeCode")));
											 else
												 continue;
											 if(MDAOutputMap.get("AlertTypeName")!=null)
												 userAlertsimpl.setAlertTypeName(MDAOutputMap.get("AlertTypeName").toString());
											 else
												 continue;
											 if(MDAOutputMap.get("AlertDescription")!=null)
												 userAlertsimpl.setAlertDescription(MDAOutputMap.get("AlertDescription").toString());
											 else
												 continue;
											 if(MDAOutputMap.get("AlertSeverity")!=null)
												 userAlertsimpl.setAlertSeverity(MDAOutputMap.get("AlertSeverity").toString());
											 else
												 continue;
											 if(MDAOutputMap.get("AlertGenerationTime")!=null)
												 userAlertsimpl.setLatestReceivedTime(MDAOutputMap.get("AlertGenerationTime").toString());
											 else
												 continue;
											userAlertsList.add(userAlertsimpl);
										}
									}
								}
								/*for(int i=0;i<userAlertsList.size();i++)
								{
									 iLogger.info("Invoking MongoDB for alerts history :: Matched :: userAlertsList ::"+userAlertsList.get(i));
								}*/
								Collections.sort(userAlertsList, new Compare());

				}catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("Fatal Error :: Invoking MongoDB for alerts history :: Error occured while connecting to Mongo DB "+e.getMessage(),e);
				}
				}
				}catch(Exception e)
				{
				e.printStackTrace();
				fLogger.fatal("Fatal Error :: Invoking MongoDB for alerts history:: Exception occured "+e.getMessage(),e);
				}
				finally
				{
					if(res!=null)
						try {
							res.close();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}

					if(stmnt!=null)
						try {
							stmnt.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					if (prodConn != null) {
						try {
							prodConn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					if(res1!=null)
						try {
							res1.close();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}

					if(stmnt1!=null)
						try {
							stmnt1.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					if (prodConn1 != null) {
						try {
							prodConn1.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
				}
				//DF20181102 :: Invoking through mongoDB
				
			else{
				
				//	System.out.println(" isOwnTenancyAlerts is false ");
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}

				List<UserAlertsImpl> userAlertsList1 = new LinkedList<UserAlertsImpl>();

				//get the parent tenancy Id
				List<Integer> parentTenancyIdList = new LinkedList<Integer>();
				List<Integer> parentAccountEntityList = new LinkedList<Integer>();

				if((userRole.equalsIgnoreCase(customerCare) || userRole.equalsIgnoreCase(admin)))
				{				
					if(isOwnTenancyAlerts==false)
					{
						parentTenancyIdList.addAll(tenancyIdList);
					}
					else
					{
						parentTenancyIdList.addAll(userTenancyIdList);
					}
				}

				else
				{
					parentTenancyIdList.addAll(userTenancyIdList);
				}
				//Convert parentTenancyIdList to comma seperated string
				String commaSeperatedString1 = conversionObj.getIntegerListString(parentTenancyIdList).toString();
				Query query = session.createQuery("from AccountTenancyMapping where tenancy_id in ("+commaSeperatedString1+")");
				Iterator itr = query.list().iterator();

				while(itr.hasNext())
				{
					AccountTenancyMapping accountTenancy = (AccountTenancyMapping) itr.next();
					parentAccountEntityList.add(accountTenancy.getAccount_id().getAccount_id());

				}
				String parentAccountEntityListString = conversionObj.getIntegerListString(parentAccountEntityList).toString();
				//Query to get SerialNumber
				String serialNumberQueryString = null;
				String finalSerialNumQueryString = null;

				/* serialNumberQueryString = " select serial_number from AssetEntity where active_status=true and client_id="+clientEntity.getClient_id()+" and primary_owner_id in " +
				" (select account_id from AccountTenancyMapping where tenancy_id in " +
				" ( select childId from TenancyBridgeEntity where parentId in ("+commaSeperatedString1+")))";*/
				serialNumberQueryString =" select distinct a.serialNumber from AssetOwnerSnapshotEntity a " +
						" where a.accountId in("+parentAccountEntityListString+")";

				if( (loginId.getIs_tenancy_admin()==1) || ((userRole.equalsIgnoreCase(customerCare) || userRole.equalsIgnoreCase(admin))) )
				{

					finalSerialNumQueryString = serialNumberQueryString;

					if(serialNumber!=null)
					{
						finalSerialNumQueryString = finalSerialNumQueryString + "and a.serialNumber = '"+serialNumber+"'";

					}

				}

				else
				{
					boolean assetGroupUser = false;
					List<String> serNumList = new LinkedList<String>();
					String commaSeperatedString2 = null;

					//check whether the user does not belongs to any machine group
					AssetCustomGroupDetailsBO assetCustomGroup1 = new AssetCustomGroupDetailsBO();
					List<AssetCustomGroupDetailsBO> BoObj1 = assetCustomGroup1.getAssetGroup(loginId.getContact_id(),0,0,null,parentTenancyIdList,false);
					if(! (BoObj1==null || BoObj1.isEmpty()) )
					{
						assetGroupUser = true;
						for(int u=0; u<BoObj1.size(); u++)
						{
							if(!(parentTenancyIdList.contains(BoObj1.get(u).getTenancyId())))
								serNumList.addAll(BoObj1.get(u).getSerialNumberList());
						}

						if(! (serNumList==null || serNumList.isEmpty()) )
						{
							//Convert serNumList to comma seperated string
							ListToStringConversion conversionObj2 = new ListToStringConversion();
							commaSeperatedString2 = conversionObj.getStringList(serNumList).toString();
						}
					}


					if(assetGroupUser==false)
					{
						finalSerialNumQueryString = serialNumberQueryString;
						if(serialNumber!=null)
						{
							finalSerialNumQueryString = finalSerialNumQueryString + "and a.serialNumber = '"+serialNumber+"'";
						}
					}

					else
					{
						finalSerialNumQueryString = serialNumberQueryString+" and a.serialNumber in ( "+commaSeperatedString2+")" ;
						if(serialNumber!=null)
						{
							finalSerialNumQueryString = finalSerialNumQueryString+" and a.serialNumber = '"+serialNumber+"'";
						}
					}
				}
				iLogger.info("Call Execute Alert Query for Is Tenancy is False");
				isOwnTenancyAlerts =false;
				//Getting the List of Alerts 
				if(pageNumber==0){
					userAlertsList = getAlertSeverityReport(finalSerialNumQueryString, startDate, alertTypeId, alertSeverity, isOwnTenancyAlerts, isHistory, true,selectedTenancy,pageNumber,serialNumber,parentAccountEntityListString,alertCodeListAsString);		
				}
				else
				{
					userAlertsList = executeUserAlertQueryNew(finalSerialNumQueryString, startDate, alertTypeId, alertSeverity, isOwnTenancyAlerts, isHistory, true,selectedTenancy,pageNumber,serialNumber,parentAccountEntityListString,alertCodeListAsString);	
				}
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
			}
			}
		}

		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}

		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}

		finally
		{
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
		/////////////////////////// END of Logic to get the Service,Emergency and Health Alerts of the Machines down the hierarachy ///////////////////////

		return userAlertsList;
	}
//***********************************************************************************************************
	
/*private List<UserAlertsImpl> executeUserAlertQueryNew(String serialNumberString,String startDate, 
			List<Integer> alertTypeId, List<String> alertSeverity,boolean isOwnTenancyAlerts, boolean isHistory,
			boolean isBelowTenancyAlerts, List<Integer> userTenancyIdList, int pageNumber, String serialNumber,String parentAccountListString, StringBuilder alertCodeListAsString) {

		List<UserAlertsImpl> userAlertsList = new LinkedList<UserAlertsImpl>();

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		ListToStringConversion conversionObj = new ListToStringConversion();


		try
		{
			int overdueEventId =0;
			int dueInWeekId =0;
			int dueInDayId =0;
			int LandmarkEventTypeId=0;
			boolean isLandmarkAlert =true; 

			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			overdueEventId= Integer.parseInt(prop.getProperty("OverdueEventId"));
			dueInWeekId= Integer.parseInt(prop.getProperty("DueInWeek"));
			dueInDayId= Integer.parseInt(prop.getProperty("DueInDay"));
			LandmarkEventTypeId = Integer.parseInt(prop.getProperty("LandmarkEventTypeId"));

			String basicQueryString = null;
			String finalQueryString = null;
			String orderByQueryString =null;
			String utilizationQueryString =null;
			String landmarkQueryString =null;
			String groupByQueryString = null;
			String basicSelectQueryString = null;
			String basicFromQueryString =null;
			String basicWhereQueryString =null;
			String AlertQueryString =null;
			//to get only the 50 records, according to the page number calculate X for Limit X,Y - 
			//Y is always 50: number of records to be retrieved.
			//X: start point of the record to be retrieved. (first record is 0)
			iLogger.info("pageNumber :"+pageNumber);
			int pge;
			if((pageNumber%5)==0)
			{
				pge = pageNumber/5;
			}

			else if(pageNumber==1)
			{
				pge=1;
			}

			else
			{
				while( ((pageNumber)%5) != 0 )
				{
					pageNumber = pageNumber-1;
				}

				pge = ((pageNumber)/5)+1;
			}
			int startLimit = (pge-1)*50;
			List<Integer> childAccountEntityList = new LinkedList<Integer>();
			List<Integer> parentAccountEntityList = new LinkedList<Integer>();

			String userTenStringList = conversionObj.getIntegerListString(userTenancyIdList).toString();

			Query queryAccount1 = session.createQuery("from AccountTenancyMapping where tenancy_id in " +
					" ("+userTenStringList+")");
			Iterator itrAccount1 = queryAccount1.list().iterator();

			while(itrAccount1.hasNext())
			{
				AccountTenancyMapping accountTenancy = (AccountTenancyMapping) itrAccount1.next();
				parentAccountEntityList.add(accountTenancy.getAccount_id().getAccount_id());
			}
			String parentAccountEntityListString = conversionObj.getIntegerListString(parentAccountEntityList).toString();
			//Get the Machine Alert other then utilization/landmark Alert
			if(parentAccountListString!=null){
				AlertQueryString ="select b.assetEventId from AssetOwnerSnapshotEntity a,AssetEventEntity b " +
						" where a.accountId in("+parentAccountListString+") and a.serialNumber=b.serialNumber and b.eventTypeId in (1,2,4) ";
				if(isHistory){
					AlertQueryString= AlertQueryString + " and b.activeStatus = 0 and  b.eventGeneratedTime >= '"+startDate+"' ";
				}
				else{
					AlertQueryString= AlertQueryString +" and b.activeStatus = 1 ";
				}
				if(serialNumber!=null && (!serialNumber.isEmpty())){
					AlertQueryString= AlertQueryString + " and b.serialNumber ='"+serialNumber+"'";
				}
			}
			else if(parentAccountListString==null){
				AlertQueryString ="select b.assetEventId AssetEventEntity b " +
						" where b.serialNumber in("+serialNumberString+") and b.eventTypeId in (1,2,4) ";
				if(isHistory){
					AlertQueryString= AlertQueryString + " and b.activeStatus = 0 and  b.eventGeneratedTime >= '"+startDate+"' ";
				}
				else{
					AlertQueryString= AlertQueryString +" and b.activeStatus = 1 ";
				}
				if(serialNumber!=null && (!serialNumber.isEmpty())){
					AlertQueryString= AlertQueryString + " and b.serialNumber ='"+serialNumber+"'";
				}	
			}
			else{
				AlertQueryString ="select b.assetEventId from AssetOwnerSnapshotEntity a,AssetEventEntity b " +
						" where a.accountId in("+parentAccountEntityListString+") and a.serialNumber=b.serialNumber and b.eventTypeId in (1,2,4) ";
				if(isHistory){
					AlertQueryString= AlertQueryString + " and b.activeStatus = 0 and  b.eventGeneratedTime >= '"+startDate+"' ";
				}
				else{
					AlertQueryString= AlertQueryString +" and b.activeStatus = 1 ";
				}
				if(serialNumber!=null && (!serialNumber.isEmpty())){
					AlertQueryString= AlertQueryString + " and b.serialNumber ='"+serialNumber+"'";
				}
			}


			//Get the Machine utilization Alert for My own Teancy
			utilizationQueryString =" select a.assetEventId from AssetEntity b,AssetEventEntity a where " +
					" b.primary_owner_id in("+parentAccountEntityListString+") and a.serialNumber=b.serial_number and a.eventTypeId = 3 ";

			if(isHistory){
				utilizationQueryString=utilizationQueryString +" and a.activeStatus = 0 and  a.eventGeneratedTime >= '"+startDate+"' ";
			}
			else{
				utilizationQueryString=utilizationQueryString +" and a.activeStatus = 1 ";
			}
			if(serialNumber!=null && (!serialNumber.isEmpty())){
				utilizationQueryString=utilizationQueryString + " and a.serialNumber ='"+serialNumber+"'";
			}

			//Get the Machine landmark Alert for My own Teancy
			landmarkQueryString = "select a.assetEventId from LandmarkLogDetailsEntity ll,LandmarkEntity land,LandmarkCategoryEntity lc ,AssetEventEntity a " +
					" where ll.landmarkId=land.Landmark_id and land.Landmark_Category_ID = lc.Landmark_Category_ID" +
					" and lc.Tenancy_ID in ("+userTenStringList+") and lc.ActiveStatus =1 and a.assetEventId=ll.assetEventId and a.eventTypeId=5";

			if(isHistory){
				landmarkQueryString=landmarkQueryString +" and a.activeStatus = 0 and  a.eventGeneratedTime >= '"+startDate+"' ";
			}
			else{
				landmarkQueryString=landmarkQueryString +" and a.activeStatus = 1 ";
			}

			if(serialNumber!=null && (!serialNumber.isEmpty())){
				landmarkQueryString=landmarkQueryString + " and a.serialNumber ='"+serialNumber+"'";
			}
			List<Integer> alertTypeIdList1 = new LinkedList<Integer>();
			List<Integer> alertTypeIdList2 = new LinkedList<Integer>();
			List<Integer> alertTypeIdList3 = new LinkedList<Integer>();
			String alertTypeIdString1 =null;
			String alertTypeIdString2 =null;
			String alertTypeIdString3 =null;
			if(alertTypeId!=null && (!alertTypeId.isEmpty()))
			{
				int i=0;
				for(i=0;i<alertTypeId.size();i++)
				{
					if(alertTypeId.get(i)== 1 || alertTypeId.get(i)== 2 || alertTypeId.get(i)== 4)
					{
						alertTypeIdList1.add(alertTypeId.get(i));
					}
					else if(alertTypeId.get(i)== 3)
					{
						alertTypeIdList2.add(alertTypeId.get(i));	
					}
					else if(alertTypeId.get(i)== 5)
					{
						alertTypeIdList3.add(alertTypeId.get(i));
					}
				}
				if(alertTypeIdList1.size()>0)
				{
					alertTypeIdString1 = conversionObj.getIntegerListString(alertTypeIdList1).toString();	
				}
				if(alertTypeIdList2.size()>0)
				{
					alertTypeIdString2 = conversionObj.getIntegerListString(alertTypeIdList2).toString();
				}
				if(alertTypeIdList3.size()>0)
				{
					alertTypeIdString3 = conversionObj.getIntegerListString(alertTypeIdList3).toString();
				}
			}

			System.out.println("alertTypeIdString1:"+alertTypeIdString1);
			System.out.println("alertTypeIdString2:"+alertTypeIdString2);
			System.out.println("alertTypeIdString3:"+alertTypeIdString3);			
			if(isHistory){
				//DF20180803: SU334449 - timeZone is now passed through the query from Asset table for SAARC machines.
				basicSelectQueryString="  select a, a.assetEventId, a.eventGeneratedTime, asset.timeZone "	;
				basicFromQueryString =" FROM AssetEventEntity a, EventEntity be, AssetEntity asset ";
				basicWhereQueryString =" where a.activeStatus = 0 and a.eventGeneratedTime >= '"+startDate+"' and asset.serial_number=a.serialNumber ";
				orderByQueryString = " order by a.eventSeverity asc,a.eventGeneratedTime desc";	
			}
			else{
				//DF20180803: SU334449 - timeZone is now passed through the query from Asset table for SAARC machines.
				basicSelectQueryString="  select a, max(a.assetEventId), max(a.eventGeneratedTime), asset.timeZone "	;
				basicFromQueryString =" FROM AssetEventEntity a, EventEntity be, AssetEntity asset ";
				basicWhereQueryString =" where a.activeStatus = 1  and asset.serial_number=a.serialNumber ";
				groupByQueryString = " group by a.serialNumber,a.eventId ";
				orderByQueryString = " order by a.eventSeverity asc,max(a.eventGeneratedTime) desc";	
			}

			//DF20170207 @Roopa role based alert implementation

			basicWhereQueryString=basicWhereQueryString+" and be.eventId=a.eventId and (be.eventCode in ("+alertCodeListAsString+")) ";

			if(serialNumber!=null && (!serialNumber.isEmpty())){
				basicWhereQueryString=basicWhereQueryString + " and a.serialNumber ='"+serialNumber+"'";
			}
			if(alertSeverity!=null)
			{
				String alertSeverityString = conversionObj.getStringList(alertSeverity).toString();
				basicWhereQueryString = basicWhereQueryString+" and a.eventSeverity IN ("+alertSeverityString.toUpperCase()+") ";
			}	
			if(alertTypeId!=null){
				String alertTypeIdString = conversionObj.getIntegerListString(alertTypeId).toString();
				basicWhereQueryString = basicWhereQueryString+" and a.eventTypeId IN ("+alertTypeIdString+") ";
			}

			if((alertTypeIdList1.size()>0 && alertTypeIdList2.size()>0 && alertTypeIdList3.size()>0) || (alertTypeId==null)){
				basicWhereQueryString=basicWhereQueryString+" and (a.assetEventId in("+AlertQueryString+") or a.assetEventId in("+utilizationQueryString+") or a.assetEventId in("+landmarkQueryString+")) ";
			}
			else if(alertTypeIdList1.size()>0 && alertTypeIdList2.size()>0 && alertTypeIdList3.size()==0)
			{
				basicWhereQueryString=basicWhereQueryString+" and (a.assetEventId in("+AlertQueryString+") or a.assetEventId in("+utilizationQueryString+")) ";
			}
			else if(alertTypeIdList1.size()>0 && alertTypeIdList2.size()==0 && alertTypeIdList3.size()>0)
			{
				basicWhereQueryString=basicWhereQueryString+" and (a.assetEventId in("+AlertQueryString+") or a.assetEventId in("+landmarkQueryString+")) ";
			}
			else if(alertTypeIdList1.size()==0 && alertTypeIdList2.size()>0 && alertTypeIdList3.size()>0)
			{
				basicWhereQueryString=basicWhereQueryString+" and (a.assetEventId in("+utilizationQueryString+") or a.assetEventId in("+landmarkQueryString+")) ";
			}
			else if(alertTypeIdList1.size()>0 && alertTypeIdList2.size()==0 && alertTypeIdList3.size()==0)
			{
				basicWhereQueryString=basicWhereQueryString+" and (a.assetEventId in("+AlertQueryString+")) ";
			}
			else if(alertTypeIdList1.size()==0 && alertTypeIdList2.size()>0 && alertTypeIdList3.size()==0)
			{
				basicWhereQueryString=basicWhereQueryString+" and (a.assetEventId in("+utilizationQueryString+")) ";
			}
			else if(alertTypeIdList1.size()==0 && alertTypeIdList2.size()==0 && alertTypeIdList3.size()>0){
				basicWhereQueryString=basicWhereQueryString+" and (a.assetEventId in("+landmarkQueryString+")) ";
			}




			if(isHistory){
				basicQueryString = basicSelectQueryString + basicFromQueryString + basicWhereQueryString + orderByQueryString;
			}
			else{
				basicQueryString = basicSelectQueryString + basicFromQueryString + basicWhereQueryString + groupByQueryString + orderByQueryString;
			}

			iLogger.info("Role based alert Testing:: UserAlertsService Final query ::"+basicQueryString);

			Object[] resultSet = null;
			Query query = session.createQuery(basicQueryString);
			query.setFirstResult(startLimit);
			query.setMaxResults(51);

			Iterator itr = query.list().iterator();
			String landmarkName = null;
			EventEntity eventEntity =null;
			EventTypeEntity eventTypeEntity =null;
			AssetEventEntity assetEvent =null;			
			while(itr.hasNext())
			{
				resultSet = (Object[]) itr.next();
				UserAlertsImpl implObj = new UserAlertsImpl();
				if(resultSet[0]!=null)
				{					
					assetEvent = (AssetEventEntity)resultSet[0];						
					implObj.setSerialNumber(assetEvent.getSerialNumber().getSerial_number().getSerialNumber());
					if(assetEvent.getEventTypeId().getEventTypeId()==5){
						if(resultSet[1]!=null){
							int asseteventId = (Integer)resultSet[1];
							if (asseteventId!=0){
								Query landmarkquery = session.createQuery("select land.Landmark_Name,land.Landmark_id from LandmarkLogDetailsEntity ll,LandmarkEntity land " +
										" where ll.landmarkId=land.Landmark_id " +
										" and ll.assetEventId = "+asseteventId+" ");
								Iterator landmarkitr = landmarkquery.list().iterator();
								Object[] landmarkresultSet = null;
								while(landmarkitr.hasNext())
								{
									landmarkresultSet = (Object[]) landmarkitr.next();
									if(landmarkresultSet[0]!=null)
									{
										landmarkName = (String)landmarkresultSet[0];
									}
								}
							}
						}
						String desc = (assetEvent.getEventId().getEventDescription()).substring(0, (assetEvent.getEventId().getEventDescription()).indexOf("<"))+" "+landmarkName;
						implObj.setAlertDescription(desc);	
					}
					else {
						implObj.setAlertDescription(assetEvent.getEventId().getEventDescription());
					}
					implObj.setAlertTypeId(assetEvent.getEventTypeId().getEventTypeId());
					implObj.setAlertTypeName(assetEvent.getEventTypeId().getEventTypeName());
					implObj.setAlertSeverity(assetEvent.getEventSeverity());
				}

				if(resultSet[1]!=null){
					implObj.setAssetEventId((Integer)resultSet[1]);
				}
				//DF20170803: SU334449 - Converting GMT to local time of respective SAARC countries
				if(resultSet[2]!=null && resultSet[3]!=null ){
					String timeZone = (String) resultSet[3];
					String convReceivedTime = new GmtLtTimeConversion().convertGmtToLocal(timeZone, String.valueOf((Timestamp)resultSet[2]));
					implObj.setLatestReceivedTime(convReceivedTime);
				}	
				//CR308.sn
				// service name to response
				implObj.setServiceName(getEventsServiceName(assetEvent.getAssetEventId()));
				//CR308.en
				userAlertsList.add(implObj);
			}							
		}

		catch(Exception e)
		{
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
			fLogger.fatal(stack.toString());
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

		return userAlertsList;
	}
	*/
	
	private List<UserAlertsImpl> executeUserAlertQueryNew(String serialNumberString,String startDate, 
			List<Integer> alertTypeId, List<String> alertSeverity,boolean isOwnTenancyAlerts, boolean isHistory,
			boolean isBelowTenancyAlerts, List<Integer> userTenancyIdList, int pageNumber, String serialNumber,String parentAccountListString, StringBuilder alertCodeListAsString) {

		List<UserAlertsImpl> userAlertsList = new LinkedList<UserAlertsImpl>();

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
	      ConnectMySQL connFactory = new ConnectMySQL();
          Connection connection = null;
          Statement stmt = null;
  		  Statement stmt1  = null;
  		Statement stmt2 = null;
		ListToStringConversion conversionObj = new ListToStringConversion();


		try
		{
			 connection = connFactory.getConnection();
			int overdueEventId =0;
			int dueInWeekId =0;
			int dueInDayId =0;
			int LandmarkEventTypeId=0;
			boolean isLandmarkAlert =true; 

			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			overdueEventId= Integer.parseInt(prop.getProperty("OverdueEventId"));
			dueInWeekId= Integer.parseInt(prop.getProperty("DueInWeek"));
			dueInDayId= Integer.parseInt(prop.getProperty("DueInDay"));
			LandmarkEventTypeId = Integer.parseInt(prop.getProperty("LandmarkEventTypeId"));

			String basicQueryString = null;
			String finalQueryString = null;
			String orderByQueryString =null;
			String utilizationQueryString =null;
			String landmarkQueryString =null;
			String groupByQueryString = null;
			String basicSelectQueryString = null;
			String basicFromQueryString =null;
			String basicWhereQueryString =null;
			String AlertQueryString =null;
			//to get only the 50 records, according to the page number calculate X for Limit X,Y - 
			//Y is always 50: number of records to be retrieved.
			//X: start point of the record to be retrieved. (first record is 0)
			iLogger.info("pageNumber :"+pageNumber);
			int pge;
			if((pageNumber%5)==0)
			{
				pge = pageNumber/5;
			}

			else if(pageNumber==1)
			{
				pge=1;
			}

			else
			{
				while( ((pageNumber)%5) != 0 )
				{
					pageNumber = pageNumber-1;
				}

				pge = ((pageNumber)/5)+1;
			}
			int startLimit = (pge-1)*50;
			List<Integer> childAccountEntityList = new LinkedList<Integer>();
			List<Integer> parentAccountEntityList = new LinkedList<Integer>();

			String userTenStringList = conversionObj.getIntegerListString(userTenancyIdList).toString();

			
			 String queryAccount1 = "select Account_ID from account_tenancy where Tenancy_ID in " +
						" ("+userTenStringList+")";
			 stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery(queryAccount1);
			
			//Iterator itrAccount1 = queryAccount1.list().iterator();

			while(rs.next())
			{
				//AccountTenancyMapping accountTenancy = (AccountTenancyMapping) itrAccount1.next();
				int accountId = rs.getInt("Account_ID");
				parentAccountEntityList.add(accountId);
			}
			String parentAccountEntityListString = conversionObj.getIntegerListString(parentAccountEntityList).toString();
			//Get the Machine Alert other then utilization/landmark Alert
			if(parentAccountListString!=null){
				AlertQueryString ="select b.Asset_Event_ID from asset_owner_snapshot a,asset_event b " +
						" where a.Account_ID in("+parentAccountListString+") and a.Serial_Number=b.Serial_Number and b.Event_Type_ID in (1,2,4) ";
				if(isHistory){
					AlertQueryString= AlertQueryString + " and b.partitionkey != 1 and  b.Event_Generated_Time >= '"+startDate+"' ";
				}
				else{
					AlertQueryString= AlertQueryString +" and b.partitionkey = 1 ";
				}
				if(serialNumber!=null && (!serialNumber.isEmpty())){
					AlertQueryString= AlertQueryString + " and b.Serial_Number ='"+serialNumber+"'";
				}
			}
			else if(parentAccountListString==null){
				AlertQueryString ="select b.Asset_Event_ID from asset_event b " +
						" where b.Serial_Number in("+serialNumberString+") and b.Event_Type_ID in (1,2,4) ";
				if(isHistory){
					AlertQueryString= AlertQueryString + " and b.partitionkey != 1 and  b.Event_Generated_Time >= '"+startDate+"' ";
				}
				else{
					AlertQueryString= AlertQueryString +" and b.partitionkey = 1 ";
				}
				if(serialNumber!=null && (!serialNumber.isEmpty())){
					AlertQueryString= AlertQueryString + " and b.Serial_Number ='"+serialNumber+"'";
				}	
			}
			else{
				AlertQueryString ="select b.Asset_Event_ID from asset_owner_snapshot a,asset_event b " +
						" where a.Account_ID in("+parentAccountEntityListString+") and a.Serial_Number=b.Serial_Number and b.Event_Type_ID in (1,2,4) ";
				if(isHistory){
					AlertQueryString= AlertQueryString + " and b.partitionkey  != 1 and  b.Event_Generated_Time >= '"+startDate+"' ";
				}
				else{
					AlertQueryString= AlertQueryString +" and b.partitionkey = 1 ";
				}
				if(serialNumber!=null && (!serialNumber.isEmpty())){
					AlertQueryString= AlertQueryString + " and b.Serial_Number ='"+serialNumber+"'";
				}
			}


			//Get the Machine utilization Alert for My own Teancy
			utilizationQueryString =" select a.Asset_Event_ID from asset b,asset_event a where " +
					" b.Primary_Owner_ID in("+parentAccountEntityListString+") and a.Serial_Number=b.Serial_Number and a.Event_Type_ID = 3 ";

			if(isHistory){
				utilizationQueryString=utilizationQueryString +" and a.partitionkey  != 1 and  a.Event_Generated_Time >= '"+startDate+"' ";
			}
			else{
				utilizationQueryString=utilizationQueryString +" and a.partitionkey = 1 ";
			}
			if(serialNumber!=null && (!serialNumber.isEmpty())){
				utilizationQueryString=utilizationQueryString + " and a.Serial_Number ='"+serialNumber+"'";
			}

			//Get the Machine landmark Alert for My own Teancy
			landmarkQueryString = "select a.Asset_Event_ID from landmark_log_details ll,landmark land,landmark_catagory lc ,asset_event a " +
					" where ll.Landmark_Id=land.landmark_id and land.landmark_category_id = lc.Landmark_Category_ID" +
					" and lc.Tenancy_ID in ("+userTenStringList+") and lc.ActiveStatus =1 and a.Asset_Event_ID=ll.Asset_Event_ID and a.Event_Type_ID=5";

			if(isHistory){
				landmarkQueryString=landmarkQueryString +" and a.partitionkey != 1 and  a.Event_Generated_Time >= '"+startDate+"' ";
			}
			else{
				landmarkQueryString=landmarkQueryString +" and a.partitionkey = 1 ";
			}

			if(serialNumber!=null && (!serialNumber.isEmpty())){
				landmarkQueryString=landmarkQueryString + " and a.Serial_Number ='"+serialNumber+"'";
			}
			List<Integer> alertTypeIdList1 = new LinkedList<Integer>();
			List<Integer> alertTypeIdList2 = new LinkedList<Integer>();
			List<Integer> alertTypeIdList3 = new LinkedList<Integer>();
			String alertTypeIdString1 =null;
			String alertTypeIdString2 =null;
			String alertTypeIdString3 =null;
			if(alertTypeId!=null && (!alertTypeId.isEmpty()))
			{
				int i=0;
				for(i=0;i<alertTypeId.size();i++)
				{
					if(alertTypeId.get(i)== 1 || alertTypeId.get(i)== 2 || alertTypeId.get(i)== 4)
					{
						alertTypeIdList1.add(alertTypeId.get(i));
					}
					else if(alertTypeId.get(i)== 3)
					{
						alertTypeIdList2.add(alertTypeId.get(i));	
					}
					else if(alertTypeId.get(i)== 5)
					{
						alertTypeIdList3.add(alertTypeId.get(i));
					}
				}
				if(alertTypeIdList1.size()>0)
				{
					alertTypeIdString1 = conversionObj.getIntegerListString(alertTypeIdList1).toString();	
				}
				if(alertTypeIdList2.size()>0)
				{
					alertTypeIdString2 = conversionObj.getIntegerListString(alertTypeIdList2).toString();
				}
				if(alertTypeIdList3.size()>0)
				{
					alertTypeIdString3 = conversionObj.getIntegerListString(alertTypeIdList3).toString();
				}
			}

			System.out.println("alertTypeIdString1:"+alertTypeIdString1);
			System.out.println("alertTypeIdString2:"+alertTypeIdString2);
			System.out.println("alertTypeIdString3:"+alertTypeIdString3);			
			if(isHistory){
				//DF20180803: SU334449 - timeZone is now passed through the query from Asset table for SAARC machines.
				basicSelectQueryString="  select a, a.Asset_Event_ID, a.eventGeneratedTime, asset.timeZone "	;
				basicFromQueryString =" FROM asset_event a, business_event be, asset asset ";
				basicWhereQueryString =" where a.partitionkey != 1 and a.Event_Generated_Time >= '"+startDate+"' and asset.serial_number=a.serial_number ";
				orderByQueryString = " order by a.Event_Severity asc,a.Event_Generated_Time desc";	
			}
			else{
				//DF20180803: SU334449 - timeZone is now passed through the query from Asset table for SAARC machines.
				
				basicSelectQueryString="  select ANY_VALUE(a.Serial_Number) AS Serial_Number,ANY_VALUE(a.Event_Type_ID) AS Event_Type_ID ,ANY_VALUE(a.Event_ID) AS Event_ID, ANY_VALUE(a.Event_Severity) AS Event_Severity ,"
						+ "ANY_VALUE(a.Asset_Event_ID) AS Asset_Event_ID, ANY_VALUE(et.Event_Type_Name) AS Event_Type_Name, max(a.assetEventId), max(a.eventGeneratedTime), asset.timeZone "	;
				//basicSelectQueryString="  select a, max(a.assetEventId), max(a.eventGeneratedTime), asset.timeZone "	;
				basicFromQueryString ="FROM asset_event a, business_event be, asset asset ,event_type et";
				basicWhereQueryString =" where a.partitionkey = 1  and asset.Serial_Number=a.Serial_Number  and a.Event_Type_ID = et.Event_Type_ID";
				groupByQueryString = " group by a.Serial_Number,a.Event_ID ";
				orderByQueryString = " order by ANY_VALUE(a.Event_Severity) asc,max(a.Event_Generated_Time) desc";	
			}

			//DF20170207 @Roopa role based alert implementation

			basicWhereQueryString=basicWhereQueryString+" and be.Event_ID=a.Event_ID and (be.Alert_Code in ("+alertCodeListAsString+")) ";

			if(serialNumber!=null && (!serialNumber.isEmpty())){
				basicWhereQueryString=basicWhereQueryString + " and a.Serial_Number ='"+serialNumber+"'";
			}
			if(alertSeverity!=null)
			{
				String alertSeverityString = conversionObj.getStringList(alertSeverity).toString();
				basicWhereQueryString = basicWhereQueryString+" and a.Event_Severity IN ("+alertSeverityString.toUpperCase()+") ";
			}	
			if(alertTypeId!=null){
				String alertTypeIdString = conversionObj.getIntegerListString(alertTypeId).toString();
				basicWhereQueryString = basicWhereQueryString+" and a.Event_Type_ID IN ("+alertTypeIdString+") ";
			}

			if((alertTypeIdList1.size()>0 && alertTypeIdList2.size()>0 && alertTypeIdList3.size()>0) || (alertTypeId==null)){
				basicWhereQueryString=basicWhereQueryString+" and (a.Asset_Event_ID in("+AlertQueryString+") or a.Asset_Event_ID in("+utilizationQueryString+") or a.Asset_Event_ID in("+landmarkQueryString+")) ";
			}
			else if(alertTypeIdList1.size()>0 && alertTypeIdList2.size()>0 && alertTypeIdList3.size()==0)
			{
				basicWhereQueryString=basicWhereQueryString+" and (a.Asset_Event_ID in("+AlertQueryString+") or a.Asset_Event_ID in("+utilizationQueryString+")) ";
			}
			else if(alertTypeIdList1.size()>0 && alertTypeIdList2.size()==0 && alertTypeIdList3.size()>0)
			{
				basicWhereQueryString=basicWhereQueryString+" and (a.Asset_Event_ID in("+AlertQueryString+") or a.Asset_Event_ID in("+landmarkQueryString+")) ";
			}
			else if(alertTypeIdList1.size()==0 && alertTypeIdList2.size()>0 && alertTypeIdList3.size()>0)
			{
				basicWhereQueryString=basicWhereQueryString+" and (a.Asset_Event_ID in("+utilizationQueryString+") or a.Asset_Event_ID in("+landmarkQueryString+")) ";
			}
			else if(alertTypeIdList1.size()>0 && alertTypeIdList2.size()==0 && alertTypeIdList3.size()==0)
			{
				basicWhereQueryString=basicWhereQueryString+" and (a.Asset_Event_ID in("+AlertQueryString+")) ";
			}
			else if(alertTypeIdList1.size()==0 && alertTypeIdList2.size()>0 && alertTypeIdList3.size()==0)
			{
				basicWhereQueryString=basicWhereQueryString+" and (a.Asset_Event_ID in("+utilizationQueryString+")) ";
			}
			else if(alertTypeIdList1.size()==0 && alertTypeIdList2.size()==0 && alertTypeIdList3.size()>0){
				basicWhereQueryString=basicWhereQueryString+" and (a.Asset_Event_ID in("+landmarkQueryString+")) ";
			}




			if(isHistory){
				basicQueryString = basicSelectQueryString + basicFromQueryString + basicWhereQueryString + orderByQueryString;
			}
			else{
				basicQueryString = basicSelectQueryString + basicFromQueryString + basicWhereQueryString + groupByQueryString + orderByQueryString;
			}

			iLogger.info("Role based alert Testing:: UserAlertsService Final query ::"+basicQueryString);

//			Object[] resultSet = null;
//			Query query = session.createQuery(basicQueryString);
//			query.setFirstResult(startLimit);
//			query.setMaxResults(51);
			
			 stmt1 = connection.createStatement();
			 ResultSet rs2 = stmt1.executeQuery(basicQueryString);
				
//			Iterator itr = query.list().iterator();
			String landmarkName = null;
			EventEntity eventEntity =null;
			EventTypeEntity eventTypeEntity =null;
			AssetEventEntity assetEvent =null;			
			while(rs2.next())
			{
//				resultSet = (Object[]) itr.next();
				
				UserAlertsImpl implObj = new UserAlertsImpl();
				String serialNumber1 = rs2.getString("Serial_Number");
				int alertTypeId1 = rs2.getInt("Event_Type_ID");
				String alertTypeName = rs2.getString("Event_Type_Name");
				String alertDescription = rs2.getString("Event_Description");
				String latestReceivedTime;
				String alertSeverity1 = rs2.getString("Event_Severity");
				int alertCounter;
				String remarks;
				int assetEventId = rs2.getInt("Asset_Event_ID");;
				if(serialNumber1!=null)
				{					
					//assetEvent = (AssetEventEntity)resultSet[0];						
					implObj.setSerialNumber(serialNumber1);
					if(alertTypeId1==5){
						if(assetEventId != 0){
						
								String landmarkquery = "select land.Landmark_Name,land.Landmark_id from landmark_log_details ll,landmark land "
										+ " where ll.Landmark_Id=land.Landmark_id  and ll.Asset_Event_ID = "+assetEventId+" ";
							
								 stmt2 = connection.createStatement();
								 ResultSet rs3 = stmt1.executeQuery(landmarkquery);
								//Object[] landmarkresultSet = null;
								while(rs3.next())
								{
						           
									if(rs3.getString("Landmark_Name") !=null)
									{
										landmarkName = rs3.getString("Landmark_Name");
									}
								}
							
						}
						String desc = alertDescription.substring(0, alertDescription.indexOf("<"))+" "+landmarkName;
						implObj.setAlertDescription(desc);	
					}
					else {
						implObj.setAlertDescription(alertDescription);
					}
					implObj.setAlertTypeId(alertTypeId1);
					implObj.setAlertTypeName(alertTypeName);
					implObj.setAlertSeverity(alertSeverity1);
				}

				if(assetEventId!= 0){
					implObj.setAssetEventId(assetEventId);
				}
				//DF20170803: SU334449 - Converting GMT to local time of respective SAARC countries
				if(rs2.getString("max(a.Event_Generated_Time)") !=null && rs2.getString("timeZone")!=null ){
					String timeZone = rs2.getString("timeZone");
					String convReceivedTime = new GmtLtTimeConversion().convertGmtToLocal(timeZone, rs2.getString("max(a.Event_Generated_Time)"));
					implObj.setLatestReceivedTime(convReceivedTime);
				}	
				//CR308.sn
				// service name to response
				implObj.setServiceName(getEventsServiceName(assetEventId));
				//CR308.en
				userAlertsList.add(implObj);
			}							
		}

		catch(Exception e)
		{
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
			fLogger.fatal(stack.toString());
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

		return userAlertsList;
	}
	
	
	
//*************************************************************End of UserAlert Service***********************************************************
	private List<UserAlertsImpl> getAlertSeverityReport(String serialNumberString,String startDate, 
			List<Integer> alertTypeId, List<String> alertSeverity,boolean isOwnTenancyAlerts, boolean isHistory,
			boolean isBelowTenancyAlerts, List<Integer> userTenancyIdList, int pageNumber, String serialNumber,String parentAccountListString, StringBuilder alertCodeListAsString) {

		List<UserAlertsImpl> userAlertsList = new LinkedList<UserAlertsImpl>();

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		ListToStringConversion conversionObj = new ListToStringConversion();


		try
		{
			int overdueEventId =0;
			int dueInWeekId =0;
			int dueInDayId =0;
			int LandmarkEventTypeId=0;
			boolean isLandmarkAlert =true; 

			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			overdueEventId= Integer.parseInt(prop.getProperty("OverdueEventId"));
			dueInWeekId= Integer.parseInt(prop.getProperty("DueInWeek"));
			dueInDayId= Integer.parseInt(prop.getProperty("DueInDay"));
			LandmarkEventTypeId = Integer.parseInt(prop.getProperty("LandmarkEventTypeId"));

			String basicQueryString = null;
			String finalQueryString = null;
			String orderByQueryString =null;
			String utilizationQueryString =null;
			String landmarkQueryString =null;
			String groupByQueryString = null;
			String basicSelectQueryString = null;
			String basicFromQueryString =null;
			String basicWhereQueryString =null;
			String AlertQueryString =null;
			List<Integer> childAccountEntityList = new LinkedList<Integer>();
			List<Integer> parentAccountEntityList = new LinkedList<Integer>();

			String userTenStringList = conversionObj.getIntegerListString(userTenancyIdList).toString();

			Query queryAccount1 = session.createQuery("from AccountTenancyMapping where tenancy_id in " +
					" ("+userTenStringList+")");
			Iterator itrAccount1 = queryAccount1.list().iterator();

			while(itrAccount1.hasNext())
			{
				AccountTenancyMapping accountTenancy = (AccountTenancyMapping) itrAccount1.next();
				parentAccountEntityList.add(accountTenancy.getAccount_id().getAccount_id());
			}
			String parentAccountEntityListString = conversionObj.getIntegerListString(parentAccountEntityList).toString();
			//Get the Machine Alert other then utilization/landmark Alert
			if(parentAccountListString!=null){
				AlertQueryString ="select b.assetEventId from AssetOwnerSnapshotEntity a,AssetEventEntity b " +
						" where a.accountId in("+parentAccountListString+") and a.serialNumber=b.serialNumber and b.eventTypeId in (2,4) ";
				if(isHistory){
					AlertQueryString= AlertQueryString + " and b.activeStatus = 0 and  b.eventGeneratedTime >= '"+startDate+"' ";
				}
				else{
					AlertQueryString= AlertQueryString +" and b.activeStatus = 1 ";
				}
				if(serialNumber!=null && (!serialNumber.isEmpty())){
					AlertQueryString= AlertQueryString + " and b.serialNumber ='"+serialNumber+"'";
				}
			}
			else if(parentAccountListString==null){
				AlertQueryString ="select b.assetEventId from AssetEventEntity b " +
						" where b.serialNumber in("+serialNumberString+") and b.eventTypeId in (2,4) ";
				if(isHistory){
					AlertQueryString= AlertQueryString + " and b.activeStatus = 0 and  b.eventGeneratedTime >= '"+startDate+"' ";
				}
				else{
					AlertQueryString= AlertQueryString +" and b.activeStatus = 1 ";
				}
				if(serialNumber!=null && (!serialNumber.isEmpty())){
					AlertQueryString= AlertQueryString + " and b.serialNumber ='"+serialNumber+"'";
				}	
			}
			else{
				AlertQueryString ="select b.assetEventId from AssetOwnerSnapshotEntity a,AssetEventEntity b " +
						" where a.accountId in("+parentAccountEntityListString+") and a.serialNumber=b.serialNumber and b.eventTypeId in (2,4) ";
				if(isHistory){
					AlertQueryString= AlertQueryString + " and b.activeStatus = 0 and  b.eventGeneratedTime >= '"+startDate+"' ";
				}
				else{
					AlertQueryString= AlertQueryString +" and b.activeStatus = 1 ";
				}
				if(serialNumber!=null && (!serialNumber.isEmpty())){
					AlertQueryString= AlertQueryString + " and b.serialNumber ='"+serialNumber+"'";
				}
			}


			//Get the Machine utilization Alert for My own Teancy
			utilizationQueryString =" select a.assetEventId from AssetEntity b,AssetEventEntity a where " +
					" b.primary_owner_id in("+parentAccountEntityListString+") and a.serialNumber=b.serial_number and a.eventTypeId = 3 ";

			if(isHistory){
				utilizationQueryString=utilizationQueryString +" and a.activeStatus = 0 and  a.eventGeneratedTime >= '"+startDate+"' ";
			}
			else{
				utilizationQueryString=utilizationQueryString +" and a.activeStatus = 1 ";
			}
			if(serialNumber!=null && (!serialNumber.isEmpty())){
				utilizationQueryString=utilizationQueryString + " and a.serialNumber ='"+serialNumber+"'";
			}

			//Get the Machine landmark Alert for My own Teancy
			landmarkQueryString = "select a.assetEventId from LandmarkLogDetailsEntity ll,LandmarkEntity land,LandmarkCategoryEntity lc ,AssetEventEntity a " +
					" where ll.landmarkId=land.Landmark_id and land.Landmark_Category_ID = lc.Landmark_Category_ID" +
					" and lc.Tenancy_ID in ("+userTenStringList+") and lc.ActiveStatus =1 and a.assetEventId=ll.assetEventId and a.eventTypeId=5";

			if(isHistory){
				landmarkQueryString=landmarkQueryString +" and a.activeStatus = 0 and  a.eventGeneratedTime >= '"+startDate+"' ";
			}
			else{
				landmarkQueryString=landmarkQueryString +" and a.activeStatus = 1 ";
			}

			if(serialNumber!=null && (!serialNumber.isEmpty())){
				landmarkQueryString=landmarkQueryString + " and a.serialNumber ='"+serialNumber+"'";
			}
			List<Integer> alertTypeIdList1 = new LinkedList<Integer>();
			List<Integer> alertTypeIdList2 = new LinkedList<Integer>();
			List<Integer> alertTypeIdList3 = new LinkedList<Integer>();
			String alertTypeIdString1 =null;
			String alertTypeIdString2 =null;
			String alertTypeIdString3 =null;
			if(alertTypeId!=null && (!alertTypeId.isEmpty()))
			{
				int i=0;
				for(i=0;i<alertTypeId.size();i++)
				{
					if(alertTypeId.get(i)== 1 || alertTypeId.get(i)== 2 || alertTypeId.get(i)== 4)
					{
						alertTypeIdList1.add(alertTypeId.get(i));
					}
					else if(alertTypeId.get(i)== 3)
					{
						alertTypeIdList2.add(alertTypeId.get(i));	
					}
					else if(alertTypeId.get(i)== 5)
					{
						alertTypeIdList3.add(alertTypeId.get(i));
					}
				}
				if(alertTypeIdList1.size()>0)
				{
					alertTypeIdString1 = conversionObj.getIntegerListString(alertTypeIdList1).toString();	
				}
				if(alertTypeIdList2.size()>0)
				{
					alertTypeIdString2 = conversionObj.getIntegerListString(alertTypeIdList2).toString();
				}
				if(alertTypeIdList3.size()>0)
				{
					alertTypeIdString3 = conversionObj.getIntegerListString(alertTypeIdList3).toString();
				}
			}
			if(isHistory){
				//DF20170803: SU334449 - timeZone is now passed through the query from Asset table for SAARC machines.
				basicSelectQueryString="  select a, a.assetEventId, a.eventGeneratedTime,t.tenancy_name,t.parent_tenancy_name,tt.tenancy_type_id, asset.timeZone "	;
				basicFromQueryString =" FROM AssetEventEntity a ,AssetEntity asset,AccountTenancyMapping at,TenancyEntity t,TenancyTypeEntity tt, EventEntity be ";
				basicWhereQueryString =" where a.activeStatus = 0 and a.eventGeneratedTime >= '"+startDate+"' and asset.serial_number=a.serialNumber and asset.primary_owner_id=at.account_id and at.tenancy_id=t.tenancy_id and t.tenancy_type_id=tt.tenancy_type_id ";
				orderByQueryString = " order by a.eventSeverity asc,a.eventGeneratedTime desc";	
			}
			else{
				//DF20170803: SU334449 - timeZone is now passed through the query from Asset table for SAARC machines.
				basicSelectQueryString="  select a, max(a.assetEventId), max(a.eventGeneratedTime),t.tenancy_name,t.parent_tenancy_name,tt.tenancy_type_id, asset.timeZone "	;
				basicFromQueryString =" FROM AssetEventEntity a,AssetEntity asset,AccountTenancyMapping at,TenancyEntity t,TenancyTypeEntity tt, EventEntity be ";
				basicWhereQueryString =" where a.activeStatus = 1 and asset.serial_number=a.serialNumber and asset.primary_owner_id=at.account_id and at.tenancy_id=t.tenancy_id and t.tenancy_type_id=tt.tenancy_type_id ";
				groupByQueryString = " group by a.serialNumber,a.eventId ";
				orderByQueryString = " order by a.eventSeverity asc,max(a.eventGeneratedTime) desc";	
			}


			//DF20170207 @Roopa role based alert implementation

			basicWhereQueryString=basicWhereQueryString+" and be.eventId=a.eventId and (be.eventCode in ("+alertCodeListAsString+")) ";


			if(serialNumber!=null && (!serialNumber.isEmpty())){
				basicWhereQueryString=basicWhereQueryString + " and a.serialNumber ='"+serialNumber+"'";
			}
			if(alertSeverity!=null)
			{
				String alertSeverityString = conversionObj.getStringList(alertSeverity).toString();
				basicWhereQueryString = basicWhereQueryString+" and a.eventSeverity IN ("+alertSeverityString.toUpperCase()+") ";
			}	
			if(alertTypeId!=null){
				String alertTypeIdString = conversionObj.getIntegerListString(alertTypeId).toString();
				basicWhereQueryString = basicWhereQueryString+" and a.eventTypeId IN ("+alertTypeIdString+") ";
			}

			if((alertTypeIdList1.size()>0 && alertTypeIdList2.size()>0 && alertTypeIdList3.size()>0) || (alertTypeId==null)){
				basicWhereQueryString=basicWhereQueryString+" and (a.assetEventId in("+AlertQueryString+") or a.assetEventId in("+utilizationQueryString+") or a.assetEventId in("+landmarkQueryString+")) ";
			}
			else if(alertTypeIdList1.size()>0 && alertTypeIdList2.size()>0 && alertTypeIdList3.size()==0)
			{
				basicWhereQueryString=basicWhereQueryString+" and (a.assetEventId in("+AlertQueryString+") or a.assetEventId in("+utilizationQueryString+")) ";
			}
			else if(alertTypeIdList1.size()>0 && alertTypeIdList2.size()==0 && alertTypeIdList3.size()>0)
			{
				basicWhereQueryString=basicWhereQueryString+" and (a.assetEventId in("+AlertQueryString+") or a.assetEventId in("+landmarkQueryString+")) ";
			}
			else if(alertTypeIdList1.size()==0 && alertTypeIdList2.size()>0 && alertTypeIdList3.size()>0)
			{
				basicWhereQueryString=basicWhereQueryString+" and (a.assetEventId in("+utilizationQueryString+") or a.assetEventId in("+landmarkQueryString+")) ";
			}
			else if(alertTypeIdList1.size()>0 && alertTypeIdList2.size()==0 && alertTypeIdList3.size()==0)
			{
				basicWhereQueryString=basicWhereQueryString+" and (a.assetEventId in("+AlertQueryString+")) ";
			}
			else if(alertTypeIdList1.size()==0 && alertTypeIdList2.size()>0 && alertTypeIdList3.size()==0)
			{
				basicWhereQueryString=basicWhereQueryString+" and (a.assetEventId in("+utilizationQueryString+")) ";
			}
			else if(alertTypeIdList1.size()==0 && alertTypeIdList2.size()==0 && alertTypeIdList3.size()>0){
				basicWhereQueryString=basicWhereQueryString+" and (a.assetEventId in("+landmarkQueryString+")) ";
			}

			if(isHistory){
				basicQueryString = basicSelectQueryString + basicFromQueryString + basicWhereQueryString + orderByQueryString;
			}
			else{
				basicQueryString = basicSelectQueryString + basicFromQueryString + basicWhereQueryString + groupByQueryString + orderByQueryString;
			}

			iLogger.info("Role based alert Testing:: AlertsSeverityReport Final query ::"+basicQueryString);

			Object[] resultSet = null;
			Query query = session.createQuery(basicQueryString);

			Iterator itr = query.list().iterator();
			String landmarkName = null;
			EventEntity eventEntity =null;
			EventTypeEntity eventTypeEntity =null;
			AssetEventEntity assetEvent =null;			
			while(itr.hasNext())
			{
				resultSet = (Object[]) itr.next();
				UserAlertsImpl implObj = new UserAlertsImpl();
				if(resultSet[0]!=null)
				{					
					assetEvent = (AssetEventEntity)resultSet[0];	
					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}
					implObj.setSerialNumber(assetEvent.getSerialNumber().getSerial_number().getSerialNumber());
					implObj.setAlertDescription(assetEvent.getEventId().getEventDescription());
					implObj.setAlertSeverity(assetEvent.getEventSeverity());
				}

				if(resultSet[5]!=null){
					String dealerName = null;
					String customerName = null;
					int tenancyTypeId =(Integer) resultSet[5];
					//If the Machine is with customer Get the parentTeancyId
					if(tenancyTypeId==4)
					{
						if(resultSet[3]!=null)
						{
							customerName =resultSet[3].toString();
						}
						if(resultSet[4]!=null){
							dealerName =resultSet[4].toString();
						}
					}
					//If the Machine is with Dealer
					else if(tenancyTypeId==3) 
					{
						if(resultSet[3]!=null)
						{
							dealerName =(String)resultSet[3];
						}
						customerName ="No Customer Tagging";
					}
					//If the Machine is with JCB/RO/HO
					else
					{
						dealerName ="No Dealer Taggging";
						customerName ="No Customer Tagging";
					}
					//For Resuablity of the code remark field used as dealerName and AlertTypename used as customerName
					implObj.setRemarks(dealerName);
					implObj.setAlertTypeName(customerName);

				}
				//DF20170803: SU334449 - Converting GMT to local time of respective SAARC countries
				if(resultSet[2]!=null && resultSet[6]!=null ){
					String timeZone = (String) resultSet[6];
					String convReceivedTime = new GmtLtTimeConversion().convertGmtToLocal(timeZone, String.valueOf((Timestamp)resultSet[2]));
					implObj.setLatestReceivedTime(convReceivedTime);
				}
				/*if(resultSet[2]!=null){
					implObj.setLatestReceivedTime(String.valueOf((Timestamp)resultSet[2]));
				}*/	
				//CR308.sn
				// service name to response
				implObj.setServiceName(getEventsServiceName(assetEvent.getAssetEventId()));
				//CR308.en
				userAlertsList.add(implObj);
			}							
		}

		catch(Exception e)
		{
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
			fLogger.fatal(stack.toString());
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

		return userAlertsList;
	}
	
	//*********************************************** START of Updating the language preference for PULL SMS *********************************************
			/** This method derives the required information to be sent as a response to Pull Sms
			 * DefectId: 1364 - 20150629 - Suresh Soorneedi - Pull SMS feature implementation
			 * dest_mobileno is the Mobile Number of user to pull sms
			 * @param mobileNumber is the Mobile Number of user whose language is to set
			 * @param lang is the language to be updated as prefered language
			 * @return Returns the processing status
			 */
		


			public String setLanguagePreference(String mobileNumber,
					String dest_mobileno, String lang) {
				// TODO Auto-generated method stub
				String status="";
				
				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			    session.beginTransaction();
			    Logger iLogger = InfoLoggerClass.logger;
				
		    	Logger fLogger = FatalLoggerClass.logger;
		    	
		    	Logger bLogger = BusinessErrorLoggerClass.logger;
			    
			    ContactEntity contact = null;
			    String language = null;
			    
			    try{
			    	//reading language properties to get the language for the keyword ex:HIN = Hindi
			    	if(! (session.isOpen() ))
		            {
		                        session = HibernateUtil.getSessionFactory().getCurrentSession();
		                        session.getTransaction().begin();
		            }

			    	/*Properties properties = new Properties();
			    	properties.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/languages.properties"));
			    	if(!properties.containsKey(lang))
			    		throw new CustomFault("Please provide the correct language code ex: for Hindi code is HIN");
			    	language = properties.getProperty(lang);*/
			    	
			    	Query langQuery = session.createQuery("from LanguagesEntity where lang_code='"+lang+"'");
			    	Iterator langItr = langQuery.list().iterator();
			    	while(langItr.hasNext())
			    	{
			    		LanguagesEntity langEntity = (LanguagesEntity) langItr.next();
			    		language = langEntity.getLanguage();
			    	}
			    	if(language == null){
			    		status="Please provide the correct language code ex: for Hindi code is HIN";
			    		throw new CustomFault("Please provide the correct language code ex: for Hindi code is HIN");
			    	}
			    	/*if(mobileNumber!=null)
			    		contact = getContactByNumber(mobileNumber);
			    	else*/
			    	contact = getContactByNumber(dest_mobileno);
			    	/*Query contactQuery = session.createQuery("from ContactEntity where primary_mobile_number='"+dest_mobileno+"'");
			    	Iterator contactItr = contactQuery.list().iterator();
			    	while(contactItr.hasNext())
			    	{
			    		contact = (ContactEntity)contactItr.next();
			    	}*/
			    	 if(contact==null)
			    	 {
			    		//DF20150629 - Suresh Soorneedi - Return language is not set in the same call to KAPSYS instead of sending new request to KAPSYS and returning "SUCCESS" in the current request
			    		 status="Mobile Number is not registered";
			    		 fLogger.fatal("setLanguagePreference : Mobile Number :"+dest_mobileno+" is not registered");
			    		 throw new CustomFault("Mobile Number is not registered for the specified PIN");
			    	 }
			    	/* else if(contact!=null && !mobileNumber.equals(dest_mobileno))
			    	 {
			    		 //get the contact for the particular mobile number to set the language preference
			    		 contact = getContactByNumber(mobileNumber);
			    		 if(contact == null)
			    		 {
			    			 status="Mobile Number is not registered";
				    		 throw new CustomFault("Mobile Number is not registered for the specified PIN");
			    		 }
			    	 }*/
			    	 contact.setLanguage(language);
			    	 session.update(contact);
			    	 //2015-08-14: Changed the default text 
			    	 status = "Dear User, Your Preferred Language has been set to "+language;
			    }
			    catch(CustomFault e){
			    	 e.printStackTrace();
			    	 bLogger.error("Custom Fault: "+ e.getFaultInfo());
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
			   
				return status;
			}
			
			//*********************************************** END of Updating the language preference for PULL SMS *********************************************

			
			/*================================Start of retriving contact for a mobile number=========================*/
			
			/** This method derives the required information to be sent as a response to Pull Sms
			 * DefectId: 1364 - 20150629 - Suresh Soorneedi - retrieving contact for a mobile number
			 * @param mobileNumber is the Mobile Number of user
			 * @param session 
			 
			 * @return Returns the ContactEntity
			 */
			public ContactEntity getContactByNumber(String mobileNumber)
			{
				Logger fLogger = FatalLoggerClass.logger;
				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			    session.beginTransaction();

			    ContactEntity contact = null;
			    try{
			    	Query contactQuery = session.createQuery("from ContactEntity where primary_mobile_number='"+mobileNumber+"'");
			    	Iterator contactItr = contactQuery.list().iterator();
			    	while(contactItr.hasNext())
			    	{
			    		contact = (ContactEntity)contactItr.next();
			    	}
			    } 
				catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("Exception :"+e);
				}
		         
			    return contact;
			}
			/*================================End of retriving contact for a mobile number=========================*/

			
			/**
			 * @author SO301743 20151209 Query tweaking to improve the performance
			 * @param loginId userLoginId
			 * @param userRole Role of the logged in user
			 * @param userTenancyIdList List of UserTenancyId
			 * @param tenancyIdList List of tenancyId
			 * @param serialNumber VIN
			 * @param startDate alert generated start date
			 * @param alertTypeId alertTypeId as Integer input
			 * @param alertSeverity alert Severity as String input
			 * @param isOwnTenancyAlerts Alerts to be returned for machines under own tenancy or not
			 * @param isHistory is Active Alerts/ History of alerts to be returned
			 * @return List of Alerts with their details
			 * @throws CustomFault
			 */
			public List<UserAlertsImpl> newGetUserAlerts (ContactEntity loginId, String userRole, 	List<Integer> userTenancyIdList, List<Integer> tenancyIdList,
														String serialNumber, String startDate, List<Integer> alertTypeId, List<String> alertSeverity,
														boolean isOwnTenancyAlerts, boolean isHistory) throws CustomFault
			{
				//String currentDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
				
				List<UserAlertsImpl> userAlertsList = new LinkedList<UserAlertsImpl>();
				//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks
				List<UserAlertsImpl> userAlertsList2 = new LinkedList<UserAlertsImpl>();
				
				TenancyBO tenancyBO = new TenancyBO();
				List<AccountEntity> accountEntityList = new LinkedList<AccountEntity>();
				
				//Logger businessError = Logger.getLogger("businessErrorLogger");
		        //Logger fatalError = Logger.getLogger("fatalErrorLogger");
				Logger iLogger = InfoLoggerClass.logger;
		    	Logger fLogger = FatalLoggerClass.logger;
		    	
		    	Logger bLogger = BusinessErrorLoggerClass.logger;
		        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		        session.beginTransaction();
		       // iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -6 ");
				try
				{
					
//					Keerthi : 12/02/14 : SEARCH : if m/c no.(7 digits) provided , get corresponding PIN 
					if(serialNumber!=null){
						if(serialNumber.trim().length()==7){
							String machineNumber = serialNumber;
							serialNumber = new AssetDetailsBO().getSerialNumberMachineNumber(machineNumber);
							if(serialNumber==null){//invalid machine number
								iLogger.info("Machine number "+ machineNumber + "does not exist !!!");
								return userAlertsList ;
							}
						}
					}
					//get Client Details
					Properties prop = new Properties();
					String clientName=null;
						
					prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
					clientName= prop.getProperty("ClientName");
		      
					IndustryBO industryBoObj = new IndustryBO();
					//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -6 a");
					ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
					//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -6 b");
					
					//END of get Client Details			 
					if(! (session.isOpen() ))
		                  {
		                              session = HibernateUtil.getSessionFactory().getCurrentSession();
		                              session.getTransaction().begin();
		                  } 

					List<Integer> selectedTenancy = new LinkedList<Integer>();
					if(!(tenancyIdList==null || tenancyIdList.isEmpty()))
					{
						selectedTenancy.addAll(tenancyIdList);
					}
					else
					{
						selectedTenancy.addAll(userTenancyIdList);
					}
					
					//check whether to look up for alerts of own tenancy
					//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -6 c");
					if(isOwnTenancyAlerts==false)
					{
						accountEntityList = tenancyBO.getAccountEntity(tenancyIdList);
					}
					else
					{
						accountEntityList = tenancyBO.getAccountEntity(userTenancyIdList);
					}
					//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -6 d");
				
					List<String> serialNumberList = new LinkedList<String>();
					List<String> finalSerialNumberList = new LinkedList<String>();
					boolean machineGroupUser=false;
					
					
					String customerCare = null;
					String admin = null;
				//	prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
					customerCare= prop.getProperty("CustomerCare");
					admin= prop.getProperty("OEMadmin");
					          
				
					////////////////////////////////////// Logic to get the alert generated for the machines of specified Tenancy ////////////////////////////////
				
					if(! (session.isOpen() ))
		            {
		                session = HibernateUtil.getSessionFactory().getCurrentSession();
		                session.getTransaction().begin();
		            }

					//get a list of serial numbers for the accountEntitylist
					AssetDetailsBO assetDetails = new AssetDetailsBO();
							
					List<String> accountAssetList = new LinkedList<String>();
					//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -7 ");
					for(int j=0; j<accountEntityList.size(); j++)
					{
						List<AssetEntity> assetEntityList = assetDetails.getAccountAssets(accountEntityList.get(j).getAccount_id());
						for(int k=0; k<assetEntityList.size(); k++)
						{
							accountAssetList.add(assetEntityList.get(k).getSerial_number().getSerialNumber());
						}
					}
					//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -8 ");
				
				
					//if the user is tenancy admin get the list of serialNumbers he is accessible to
					if( (loginId.getIs_tenancy_admin()==1) ||
							( (userRole.equalsIgnoreCase(customerCare) || userRole.equalsIgnoreCase(admin)) ) )
									//&& (isOwnTenancyAlerts==false))	)
					{
						
						//get the list of all serial numbers under the tenancy specified
						if(serialNumber!=null)
						{
							if(accountAssetList.contains(serialNumber))
							{
								finalSerialNumberList.add(serialNumber);
							}
							
						}
						else
						{
							finalSerialNumberList.addAll(accountAssetList);
						}
						//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -9 ");
					}
				
					
					//if the user is not tenancy admin get the list of serialNumbers he is accessible to
					else if ( (loginId.getIs_tenancy_admin()==0)) 
							//|| 
						    //( (userRole.equalsIgnoreCase(customerCare) || userRole.equalsIgnoreCase(admin)) && (isOwnTenancyAlerts==true) && (loginId.getIs_tenancy_admin()==0) ) )
					{
						//get the list of machines under the machine group to which the user belongs to
						AssetCustomGroupDetailsBO assetCustomGroup = new AssetCustomGroupDetailsBO();
						//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -9 a");
						List<AssetCustomGroupDetailsBO> BoObj = assetCustomGroup.getAssetGroup(loginId.getContact_id(),0,0,null,userTenancyIdList,false);
						//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -9 b");
						if(! (BoObj==null || BoObj.isEmpty()) )
						{
							machineGroupUser = true;
							for(int u=0; u<BoObj.size(); u++)
							{
								if(userTenancyIdList.contains(BoObj.get(u).getTenancyId()) )
										serialNumberList.addAll(BoObj.get(u).getSerialNumberList());
							}
						}
						
						if(serialNumber!=null)
						{
							if(serialNumberList.contains(serialNumber))
							{
								finalSerialNumberList.add(serialNumber);
							}
						}
						else
							
						{
							finalSerialNumberList.addAll(serialNumberList);
						}
						
						if(machineGroupUser==false && finalSerialNumberList.isEmpty())
						{
							if( (serialNumber!=null) )
							{
								if(accountAssetList.contains(serialNumber)) 
									finalSerialNumberList.add(serialNumber);
							}
								
							
							else
								finalSerialNumberList.addAll(accountAssetList);
						}
						//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -10 ");
					}
					
					//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -11 ");
				
					//Convert the SerialNumberList to comma seperated string
					ListToStringConversion conversionObj = new ListToStringConversion();
					String commaSeperatedString = conversionObj.getStringList(finalSerialNumberList).toString();
					iLogger.info("commaSeperatedString "+commaSeperatedString);
				
						
					if(! (finalSerialNumberList==null || finalSerialNumberList.isEmpty()) )
					{
						//Getting the List of Alerts other than Landmark Alerts
						//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -12 ");
						userAlertsList2 = this.newExecuteUserAlertQuery(commaSeperatedString, startDate, alertTypeId, alertSeverity, isOwnTenancyAlerts, isHistory, false,selectedTenancy, false);
						//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -24 ");
						userAlertsList.addAll(userAlertsList2);
						//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks
						//Getting the List of Landmark Alerts
						//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -25 ");
						userAlertsList2 = this.newExecuteUserAlertQuery(commaSeperatedString, startDate, alertTypeId, alertSeverity, isOwnTenancyAlerts, isHistory, false,selectedTenancy, true);
						userAlertsList.addAll(userAlertsList2);
						//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -26 ");
					}
					//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -27 ");
					
					//////////////////////////////////////END of Logic to get the alert generated for the machines of specified Tenancy ////////////////////////////////
				
				
					/////////////////////////// Logic to get the Service,Emergency and Health Alerts of the Machines down the hierarachy ///////////////////////
					
					if(! (session.isOpen() ))
		            {
		                session = HibernateUtil.getSessionFactory().getCurrentSession();
		                session.getTransaction().begin();
		            }
					
					List<UserAlertsImpl> userAlertsList1 = new LinkedList<UserAlertsImpl>();
				
					//get the parent tenancy Id
					List<Integer> parentTenancyIdList = new LinkedList<Integer>();
					//DefectId:20140305 @ If Ownstock True
					if(isOwnTenancyAlerts==true)
					{
						return userAlertsList;
					}
					if((userRole.equalsIgnoreCase(customerCare) || userRole.equalsIgnoreCase(admin)))
					{
						/*if(isOwnTenancyAlerts==true)
						{
							return userAlertsList;
						}*/
						
						if(isOwnTenancyAlerts==false)
						{
							parentTenancyIdList.addAll(tenancyIdList);
						}
						else
						{
							parentTenancyIdList.addAll(userTenancyIdList);
						}
					}
					
					else
					{
						parentTenancyIdList.addAll(userTenancyIdList);
					}
				
				
					//Query to get SerialNumber
					String serialNumberQueryString = null;
					String finalSerialNumQueryString = null;
					
					//Convert parentTenancyIdList to comma seperated string
					ListToStringConversion conversionObj1 = new ListToStringConversion();
					String commaSeperatedString1 = conversionObj.getIntegerListString(parentTenancyIdList).toString();
					
					
					/*serialNumberQueryString = "select Serial_Number from asset_owners where Account_ID in " +
												"( select Account_ID from account_tenancy where Tenancy_ID in " +
													"( select Child_ID from tenancy_bridge where Parent_Id in (" +commaSeperatedString1 +" ) and " +
														"Child_ID not in ( " +commaSeperatedString1+"  ) ) )";*/
					
					//DefectId 912 - Rajani Nagaraju - 2013/07/08 -  Replace direct SQL query to Hibernate
					/*serialNumberQueryString = " select serialNumber from AssetAccountMapping where accountId in " +
							" (select account_id from AccountTenancyMapping where tenancy_id in " +
							" ( select childId from TenancyBridgeEntity where parentId in ("+commaSeperatedString1+") and " +
									" childId not in ("+commaSeperatedString1+")))";*/
					//DefectID: - Asset Owner Changes - 2013-07-19 - Rajani Nagaraju
					//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -27 a");
					serialNumberQueryString = " select serial_number from AssetEntity where active_status=true and client_id="+clientEntity.getClient_id()+" and primary_owner_id in " +
					" (select account_id from AccountTenancyMapping where tenancy_id in " +
					" ( select childId from TenancyBridgeEntity where parentId in ("+commaSeperatedString1+") and " +
							" childId not in ("+commaSeperatedString1+")))";
							
					
					if( (loginId.getIs_tenancy_admin()==1) || ((userRole.equalsIgnoreCase(customerCare) || userRole.equalsIgnoreCase(admin))) )
					{
						
						finalSerialNumQueryString = serialNumberQueryString;
						
						if(serialNumber!=null)
						{
							finalSerialNumQueryString = finalSerialNumQueryString + "and serial_number = '"+serialNumber+"'";
							
						}
						//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -27 b");
					}
					
					else
					{
						boolean assetGroupUser = false;
						List<String> serNumList = new LinkedList<String>();
						String commaSeperatedString2 = null;
						
						//check whether the user does not belongs to any machine group
						AssetCustomGroupDetailsBO assetCustomGroup1 = new AssetCustomGroupDetailsBO();
						//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -27 c");
						List<AssetCustomGroupDetailsBO> BoObj1 = assetCustomGroup1.getAssetGroup(loginId.getContact_id(),0,0,null,parentTenancyIdList,false);
						//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -27 d");
						if(! (BoObj1==null || BoObj1.isEmpty()) )
						{
							assetGroupUser = true;
							for(int u=0; u<BoObj1.size(); u++)
							{
								if(!(parentTenancyIdList.contains(BoObj1.get(u).getTenancyId())))
									serNumList.addAll(BoObj1.get(u).getSerialNumberList());
							}
						
							if(! (serNumList==null || serNumList.isEmpty()) )
							{
								//Convert serNumList to comma seperated string
								ListToStringConversion conversionObj2 = new ListToStringConversion();
								commaSeperatedString2 = conversionObj.getStringList(serNumList).toString();
							}
						}
						//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -28 ");
					
						if(assetGroupUser==false)
						{
							finalSerialNumQueryString = serialNumberQueryString;
							if(serialNumber!=null)
							{
								finalSerialNumQueryString = finalSerialNumQueryString + "and serial_number = '"+serialNumber+"'";
							}
						}
						
						else
						{
							finalSerialNumQueryString = serialNumberQueryString+" and serial_number in ( "+commaSeperatedString2+")" ;
							if(serialNumber!=null)
							{
								finalSerialNumQueryString = finalSerialNumQueryString+" and serial_number = '"+serialNumber+"'";
							}
						}
					}
				
					//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks
					//Getting the List of Alerts other than Landmark Alerts
					//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -29 ");
					userAlertsList1 = this.newExecuteUserAlertQuery(finalSerialNumQueryString, startDate, alertTypeId, alertSeverity, isOwnTenancyAlerts, isHistory, true,selectedTenancy,false);		
					userAlertsList.addAll(userAlertsList1);
					//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -30 ");
					//Getting the List of Landmark Alerts
					userAlertsList1 = this.newExecuteUserAlertQuery(finalSerialNumQueryString, startDate, alertTypeId, alertSeverity, isOwnTenancyAlerts, isHistory, true,selectedTenancy, true);
					userAlertsList.addAll(userAlertsList1);
				//	iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -31 ");
					if(! (session.isOpen() ))
		            {
		                session = HibernateUtil.getSessionFactory().getCurrentSession();
		                session.getTransaction().begin();
		            }
				//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -32 ");
				}
				
				catch(CustomFault e)
				{
					bLogger.error("Custom Fault: "+ e.getFaultInfo());
				}
				
				catch(Exception e)
				{
					fLogger.fatal("Exception :"+e);
				}
		        
		        finally
		        {
		              if(session.getTransaction().isActive())
		              {
		            	  session.flush();  
		            	  session.getTransaction().commit();
		              }
		              
		              if(session.isOpen())
		              {
		                  
		                    session.close();
		              }
		              
		        }
				/////////////////////////// END of Logic to get the Service,Emergency and Health Alerts of the Machines down the hierarachy ///////////////////////
				
				return userAlertsList;
			}
			
			
			//************************************** Create and Execute Query to get User Alerts ***********************************************
		/** This method queries the database through hibernate and gets the Alert details
		 * 20151214 : s suresh new code changes for query tweaking 
		* 20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks - Considering diff query for Landmark Alert when compared to other alerts
		* @param serialNumberString Comma delimited string of serailNumbers
		* @param startDate startDate from when the alert data has to be retrieved
		* @param alertTypeId alertTypeId as Integer input
		* @param alertSeverity alert Severity
		* @param isOwnTenancyAlerts Alerts to be returned for machines under own tenancy or not
		* @param isHistory is Active Alerts/ History of alerts to be returned
		* @param isBelowTenancyAlerts is the alert generated for the machines down the hierarchy to be displayed
		* @return List of Alerts with their details
		* @throws SQLException
		*/
		public List<UserAlertsImpl> newExecuteUserAlertQuery(String serialNumberString,String startDate, 
														List<Integer> alertTypeId, List<String> alertSeverity,boolean isOwnTenancyAlerts, boolean isHistory,
														boolean isBelowTenancyAlerts, List<Integer> userTenancyIdList, boolean isLandmarkAlert ) throws SQLException
		{
		Logger iLogger = InfoLoggerClass.logger;
		List<UserAlertsImpl> userAlertsList = new LinkedList<UserAlertsImpl>();
		//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -13 ");
		// Logger businessError = Logger.getLogger("businessErrorLogger");
		 //Logger fatalError = Logger.getLogger("fatalErrorLogger");
		//Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		 Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		 session.beginTransaction();
		    
		 ListToStringConversion conversionObj = new ListToStringConversion();
			
		 
		 try
		 {
			/* //End date is always today
			Date currentDate = new java.util.Date();
			Timestamp currentTime = new Timestamp(currentDate.getTime());
			String endDate = currentTime.toString();*/

			int overdueEventId =0;
			int dueInWeekId =0;
			int dueInDayId =0;
			int LandmarkEventTypeId=0;
			
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			overdueEventId= Integer.parseInt(prop.getProperty("OverdueEventId"));
			dueInWeekId= Integer.parseInt(prop.getProperty("DueInWeek"));
			dueInDayId= Integer.parseInt(prop.getProperty("DueInDay"));
			LandmarkEventTypeId = Integer.parseInt(prop.getProperty("LandmarkEventTypeId"));

			String basicQueryString = null;
			String groupByQueryString = null;
			String finalQueryString = null;
			String orderByQueryString =null;
			//DefectId 912 - Rajani Nagaraju - 2013/07/08 -  Replace direct SQL query to Hibernate
			
			//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks
			//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -14 ");
			if(!(isLandmarkAlert))
			{
				
				//	" a.assetEventId=y.assetEventId ";
				/*iLogger.info(this.loginId+":"+this.currentDate+"AlertSummaryService -15 ");*/
				//DefectId 912 - Rajani Nagaraju - 2013/07/08 -  Replace direct SQL query to Hibernate
				if(isHistory){//if history,show all closed events irrespective of event id :Keerthi : 13/03/14
					basicQueryString = " select a.serialNumber, a.assetEventId as asset_event_id , c.eventId, c.eventDescription, b.eventTypeId, " +
					" b.eventTypeName, a.eventGeneratedTime, a.eventSeverity , c.eventName as Event_Name, " +
					" a.comments " +
					" from AssetEventEntity a, EventTypeEntity b, EventEntity c " +
					" where a.eventTypeId = b.eventTypeId and a.eventId=c.eventId " +
					" and a.eventTypeId != "+LandmarkEventTypeId;
//					groupByQueryString = " group by a.serialNumber ";
					orderByQueryString = " order by a.eventSeverity asc,a.eventGeneratedTime desc";
				}
				else{
					basicQueryString = " select a.serialNumber, max(a.assetEventId) as asset_event_id , c.eventId, c.eventDescription, b.eventTypeId, " +
					" b.eventTypeName, max(a.eventGeneratedTime), a.eventSeverity , c.eventName as Event_Name, " +
					" a.comments " +
					" from AssetEventEntity a, EventTypeEntity b, EventEntity c " +
					" where a.eventTypeId = b.eventTypeId and a.eventId=c.eventId " +
					" and a.eventTypeId != "+LandmarkEventTypeId;
					groupByQueryString = " group by a.serialNumber, a.eventId ";
					orderByQueryString = " order by a.eventSeverity asc,max(a.eventGeneratedTime) desc";
				}
				//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -15 a");
			}
			
			//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks - Complete else part
			else
			{
				//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -16 ");
				String userTenStringList = conversionObj.getIntegerListString(userTenancyIdList).toString();
				
				
				
				if(isHistory){
					basicQueryString = " select a.serialNumber, a.assetEventId as asset_event_id , c.eventId, c.eventDescription, b.eventTypeId, " +
					" b.eventTypeName, a.eventGeneratedTime, a.eventSeverity ,  c.eventName as Event_Name, " +
					" a.comments, land.Landmark_Name " +
					" from AssetEventEntity a, EventTypeEntity b, EventEntity c, LandmarkLogDetailsEntity ll, LandmarkEntity land, LandmarkCategoryEntity lc " +
					" where a.eventTypeId = b.eventTypeId and a.eventId=c.eventId " +
					" and a.eventTypeId = "+LandmarkEventTypeId + " and  a.assetEventId=ll.assetEventId and ll.landmarkId=land.Landmark_id " +
					" and land.Landmark_Category_ID = lc.Landmark_Category_ID and lc.Tenancy_ID in ("+userTenStringList+")";
//					groupByQueryString = " group by a.serialNumber, land.Landmark_id ";
					orderByQueryString = " order by a.eventSeverity asc,a.eventGeneratedTime desc";
				}
				else{
					basicQueryString = " select a.serialNumber, max(a.assetEventId) as asset_event_id , c.eventId, c.eventDescription, b.eventTypeId, " +
					" b.eventTypeName, max(a.eventGeneratedTime), a.eventSeverity ,  c.eventName as Event_Name, " +
					" a.comments, land.Landmark_Name " +
					" from AssetEventEntity a, EventTypeEntity b, EventEntity c, LandmarkLogDetailsEntity ll, LandmarkEntity land, LandmarkCategoryEntity lc " +
					" where a.eventTypeId = b.eventTypeId and a.eventId=c.eventId " +
					" and a.eventTypeId = "+LandmarkEventTypeId + " and  a.assetEventId=ll.assetEventId and ll.landmarkId=land.Landmark_id " +
					" and land.Landmark_Category_ID = lc.Landmark_Category_ID and lc.Tenancy_ID in ("+userTenStringList+")";
					groupByQueryString = " group by a.serialNumber, a.eventId, land.Landmark_id ";
					orderByQueryString = " order by a.eventSeverity asc,max(a.eventGeneratedTime) desc";
				}
				//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -16 a");
			}
					
			/*basicQueryString = "select a.serial_number,max(a.asset_event_id) as asset_event_id,a.Event_ID, c.Event_Description, a.Event_Type_ID," +
			"b.Event_Type_Name, max(a.Event_Generated_Time),a.Event_Severity, count(*) as alertcount, c.Event_Name as Event_Name from asset_event a,event_type b, " +
			"business_event c where a.Event_Type_ID = b.Event_Type_ID and a.Event_ID = c.Event_ID ";
			*/
			
			
			//groupByQueryString = " group by a.serial_number, a.event_id ";
			// Defect ID : 490 - Suprava - 29/07/13 - ordering based on seveirty
			// and event generated time
			
			
			if(serialNumberString!=null)
			{
				basicQueryString = basicQueryString+" and a.serialNumber in ( "+ serialNumberString + " )";
			}
			
			if(isHistory==false)
			{
				basicQueryString = basicQueryString+" and a.activeStatus=1 and a.partitionKey =1";
			}
			
			else
			{
				basicQueryString = basicQueryString+" and a.activeStatus=0 and  a.eventGeneratedTime >= '"+startDate+"'";
			}

			if(alertTypeId!=null)
			{
				String alertTypeIdString = conversionObj.getIntegerListString(alertTypeId).toString();
				basicQueryString= basicQueryString+" and a.eventTypeId IN ("+alertTypeIdString+") ";
			}
			
			if(alertSeverity!=null)
			{
				String alertSeverityString = conversionObj.getStringList(alertSeverity).toString();
				basicQueryString = basicQueryString+" and a.eventSeverity IN ("+alertSeverityString.toUpperCase()+") ";
			}
			
			if(isBelowTenancyAlerts==true)
			{
				basicQueryString = basicQueryString+ " and a.eventTypeId in (2,4,5) ";
			}


			
			if(isHistory){
				basicQueryString = basicQueryString + orderByQueryString;
			}
			else{
				basicQueryString = basicQueryString + groupByQueryString+ orderByQueryString;
			}
			//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -17 ");
			
			Object[] resultSet = null;
			HashMap<String,List<String>> assetEventData = new HashMap<String,List<String>>();
			//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -17 a-Query :" + basicQueryString);
			
			Query query = session.createQuery(basicQueryString);
			Iterator itr = query.list().iterator();
			//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -18 ");
		/*
			finalQueryString = "select x.*, y.Comments from	( "+basicQueryString+" ) x, asset_event y where x.asset_event_id = y.asset_event_id";
			

			HashMap<String,List<String>> assetEventData = new HashMap<String,List<String>>();
			
			ConnectMySQL connectionObj = new ConnectMySQL();
			Connection con = connectionObj.getConnection();
			
			Statement statement = con.createStatement(); 
			ResultSet resultSet=null;
			
			resultSet = statement.executeQuery(finalQueryString);
			
			if(resultSet==null)
			{
				return userAlertsList;
			}*/
			String landmarkName = null;
			if(isHistory){//not putting in hashmap, taking all rows
				//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -19 ");
				while(itr.hasNext())
				{
					resultSet = (Object[]) itr.next();
					
					String alertComment = null;
					//DF20150408 - Rajani Nagaraju - Commenting the below line for performance improvement as it is not required for main page.
					//Same will be returned from new Service UserAlertDetailService
					/*Query commentQuery = session.createQuery(" select a.comments from AssetEventEntity a where a.assetEventId="+(Integer) resultSet[1]);
					Iterator commentItr = commentQuery.list().iterator();
					while(commentItr.hasNext())
					{
						alertComment = (String)commentItr.next();
					}*/
					UserAlertsImpl implObj = new UserAlertsImpl();
					if(resultSet[0]!=null){
						implObj.setSerialNumber(((AssetEntity)resultSet[0]).getSerial_number().getSerialNumber());
					}
					
					if(resultSet[1]!=null){
						implObj.setAssetEventId((Integer)resultSet[1]);
					}
					
					if(resultSet[3]!=null){
						implObj.setAlertDescription((String)resultSet[3]);
					}
					if(resultSet[4]!=null){
						implObj.setAlertTypeId((Integer)resultSet[4]);
					}
					if(resultSet[5]!=null){
						implObj.setAlertTypeName((String)resultSet[5]);
					}
					if(resultSet[6]!=null){
						implObj.setLatestReceivedTime(String.valueOf((Timestamp)resultSet[6]));
					}
					if(resultSet[7]!=null){
						implObj.setAlertSeverity((String)resultSet[7]);
					}
					if(resultSet[8]!=null){
						implObj.setAlertDescription((String)resultSet[8]);
					}
					if(isLandmarkAlert)
					{
						if(resultSet[10]!=null)
							landmarkName = resultSet[10].toString();
						String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
						implObj.setAlertDescription(desc);
						
					}
					userAlertsList.add(implObj);
				}
				//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -20 ");
			}	
			else{
				//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -21 ");
			while(itr.hasNext())
			{
				resultSet = (Object[]) itr.next();
				
				String alertComment = null;
				//DF20150408 - Rajani Nagaraju - Commenting the below line for performance improvement as it is not required for main page.
				//Same will be returned from new Service UserAlertDetailService
				/*Query commentQuery = session.createQuery(" select a.comments from AssetEventEntity a where a.assetEventId="+(Integer) resultSet[1]);
				Iterator commentItr = commentQuery.list().iterator();
				while(commentItr.hasNext())
				{
					alertComment = (String)commentItr.next();
				}*/
				
				//Check for the LandmarkAlert
				//DefectId 927 - Rajani Nagaraju - 2013/07/09 - LandmarkAlert Bug fix on User Alert Screen
				//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks - Commenting the below code as it is already handled in Query
				
				landmarkName = null;
				/*int eventTypeId= (Integer)resultSet[4];
				if(eventTypeId==LandmarkEventTypeId)
				{
					Query landmarkQuery = session.createQuery("from LandmarkLogDetailsEntity where assetEventId="+(Integer)resultSet[1]);
					Iterator landmarkItr = landmarkQuery.list().iterator();
					int landmarkTenancyId=0;
					
					while(landmarkItr.hasNext())
					{
						LandmarkLogDetailsEntity logDetails = (LandmarkLogDetailsEntity)landmarkItr.next();
						landmarkTenancyId = logDetails.getLandmarkId().getLandmark_Category_ID().getTenancy_ID().getTenancy_id();
						landmarkName = logDetails.getLandmarkId().getLandmark_Name();
					}
					
					if(!(userTenancyIdList.contains(landmarkTenancyId)))
					{
						continue;
					}
				}*/
				
				String serNumString = ((AssetEntity)resultSet[0]).getSerial_number().getSerialNumber();
				
				//First record to be inserted to hashmap
				if(assetEventData.isEmpty())
				{
					//First element is always Count
					List<String> assetEventDetails = new LinkedList<String>();
					String hashmapKey = null; // SerialNumber+"*"+eventDescription
									
					//20140113: 1931  : Rajani Nagaraju
					if(isLandmarkAlert)
					{
						if(resultSet[10]!=null)
							landmarkName = resultSet[10].toString();
						hashmapKey = serNumString+":"+(String) resultSet[8]+":"+landmarkName;
					}
					else
					{
						hashmapKey = serNumString+":"+(String) resultSet[8];// SerialNumber+":"+eventName
					}
					assetEventDetails.add("0");//List{0} - alertCount
					assetEventDetails.add(String.valueOf((Integer) resultSet[1]));//List{1} - assetEventId
					assetEventDetails.add(String.valueOf((Integer) resultSet[2]));//List{2} - eventId
					assetEventDetails.add(String.valueOf((Integer) resultSet[4]));//List{3} - eventTypeId
					assetEventDetails.add((String) resultSet[5]);//List{4} - eventTypeName
					assetEventDetails.add(String.valueOf((Timestamp) resultSet[6]));//List{5} - eventGeneratedTime
					assetEventDetails.add((String) resultSet[7]);//List{6} - eventSeverity
					assetEventDetails.add(alertComment);//List{7} - Comments
					
					//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks - Commenting below block and adding new block
					/*if(eventTypeId==LandmarkEventTypeId)
					{
						String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
						assetEventDetails.add(desc);
					}*/
					

					if(isLandmarkAlert)
					{
						if(resultSet[10]!=null)
							landmarkName = resultSet[10].toString();
						String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
						assetEventDetails.add(desc);
						
					}
					else
					{
						assetEventDetails.add((String) resultSet[3]);//List{8} - eventDescription
					}
					assetEventData.put(hashmapKey, assetEventDetails);
				}
			
				else
				{
					String hashmapKey =null;
					
					//20140113: 1931  : Rajani Nagaraju
					if(isLandmarkAlert)
					{
						if(resultSet[10]!=null)
							landmarkName = resultSet[10].toString();
						hashmapKey = serNumString+":"+(String) resultSet[8]+":"+landmarkName;
					}
					
					else
					{
						hashmapKey = serNumString+":"+(String) resultSet[8];// SerialNumber+":"+eventName
					}
					
					if(assetEventData.containsKey(hashmapKey))
					{
						List<String> assetEventDetails = assetEventData.get(hashmapKey);
						int alertCount = Integer.parseInt(assetEventDetails.get(0));
//						String count = String.valueOf((Long) resultSet[8]);
//						alertCount = alertCount+Integer.parseInt(count);
						String alertCountString = Integer.toString(alertCount);
						assetEventDetails.set(0, alertCountString);
					
										
						//also update eventGeneratedTime, Severity, assetEventId, Comments
						if(((String) resultSet[7]).equalsIgnoreCase("RED"))
						{
							assetEventDetails.set(5, String.valueOf((Timestamp) resultSet[6]));//eventGeneratedTime
							assetEventDetails.set(6, (String) resultSet[7]);//Severity
							assetEventDetails.set(1, String.valueOf((Integer) resultSet[1]));//assetEventId
							assetEventDetails.set(7, alertComment);//Comments
							
							//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks - Commenting below block and adding new block
							/*if(eventTypeId==LandmarkEventTypeId)
							{
								String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
								assetEventDetails.set(8, desc); //Description
							}*/
							if(isLandmarkAlert)
							{
								if(resultSet[10]!=null)
									landmarkName = resultSet[10].toString();
								String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
								assetEventDetails.add(desc);
								
							}
							else
							{
								assetEventDetails.set(8, (String) resultSet[3]); //Description
							}
					
						}
					
					
						else if( (((String) resultSet[7]).equalsIgnoreCase("YELLOW")) && (assetEventDetails.get(6).equalsIgnoreCase("YELLOW")) )
						{
													
							//if( (Integer.parseInt((String) resultSet[1])) > (Integer.parseInt(assetEventDetails.get(1))) )
							if( ((Integer) resultSet[1]) > (Integer.parseInt(assetEventDetails.get(1))) )
							{
								assetEventDetails.set(5, String.valueOf((Timestamp) resultSet[6]));//eventGeneratedTime
								assetEventDetails.set(1, String.valueOf((Integer) resultSet[1]));//assetEventId
								assetEventDetails.set(7, alertComment);//Comments
								
								//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks - Commenting below block and adding new block
								/*if(eventTypeId==LandmarkEventTypeId)
								{
									String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
									assetEventDetails.set(8, desc); //Description
								}*/
								if(isLandmarkAlert)
								{
									if(resultSet[10]!=null)
										landmarkName = resultSet[10].toString();
									String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
									assetEventDetails.add(desc);
									
								}
								else
								{
									assetEventDetails.set(8, (String) resultSet[3]); //Description
								}
							}
						}
						assetEventData.put(hashmapKey, assetEventDetails);
					}
				
					else
					{
						//First element is always Count
						List<String> assetEventDetails = new LinkedList<String>();
						
						assetEventDetails.add("0");//List{0} - alertCount
						assetEventDetails.add(String.valueOf((Integer) resultSet[1]));//List{1} - assetEventId
						assetEventDetails.add(String.valueOf((Integer) resultSet[2]));//List{2} - eventId
						assetEventDetails.add(String.valueOf((Integer) resultSet[4]));//List{3} - eventTypeId
						assetEventDetails.add((String) resultSet[5]);//List{4} - eventTypeName
						assetEventDetails.add(String.valueOf((Timestamp) resultSet[6]));//List{5} - eventGeneratedTime
						assetEventDetails.add((String) resultSet[7]);//List{6} - eventSeverity
						assetEventDetails.add(alertComment);//List{7} - Comments
						// Defect ID : 490 - Suprava - 29/07/13- checking
						//20140110: 1931  : Rajani Nagaraju - Landmark Alerts should be returned for each individual Landmarks - Commenting below block and adding new block
						/*if(eventTypeId==LandmarkEventTypeId && !(assetEventData.containsKey(hashmapKey)))
						{
							String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
							assetEventDetails.add(desc);
						}*/
						if(isLandmarkAlert && !(assetEventData.containsKey(hashmapKey)) )
						{
							if(resultSet[10]!=null)
								landmarkName = resultSet[10].toString();
							String desc = ((String) resultSet[3]).substring(0, ((String) resultSet[3]).indexOf("<"))+" "+landmarkName;
							assetEventDetails.add(desc);
							
						}
						else
						{
							assetEventDetails.add((String) resultSet[3]);//List{8} - eventDescription
						}
						
						assetEventData.put(hashmapKey, assetEventDetails);
					}
				}

				
			}
			//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -22 ");

			//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -22 a -Size : "  + assetEventData.size());
		    //set the values in HashMap into Impl class fields
			for(int j=0; j<assetEventData.size(); j++)
			{
				//get the key
				String key = (String)assetEventData.keySet().toArray()[j];
				String[] keyArray = key.split(":");
			
				UserAlertsImpl implObj = new UserAlertsImpl();
				implObj.setSerialNumber(keyArray[0]);
			
			
				//get the values
				List<String> mapValues = (List<String>)assetEventData.values().toArray()[j];
				implObj.setAssetEventId(Integer.parseInt(mapValues.get(1)));
				implObj.setAlertTypeId(Integer.parseInt(mapValues.get(3)));
				implObj.setAlertTypeName(mapValues.get(4));
				implObj.setLatestReceivedTime(mapValues.get(5));
				implObj.setAlertSeverity(mapValues.get(6));
				implObj.setRemarks(mapValues.get(7));
				
				
				if(Integer.parseInt(mapValues.get(2)) == overdueEventId)
					implObj.setAlertCounter(3);
				else if(Integer.parseInt(mapValues.get(2)) == dueInWeekId)
					implObj.setAlertCounter(2);
				else if(Integer.parseInt(mapValues.get(2)) == dueInDayId)
					implObj.setAlertCounter(1);
				else
					implObj.setAlertCounter(Integer.parseInt(mapValues.get(0)));
				
				implObj.setAlertDescription(mapValues.get(8));
				
				userAlertsList.add(implObj);
			}
			//iLogger.info(this.loginId+":"+this.currentDate+":AlertSummaryService -23 ");
		   }
		 }
		 
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
		}

		//DF-20131115 session not getting closed
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

		return userAlertsList;

		}


			/**
			 * method to get service due overdue details 
			 * @param LoginId, Period, TenancyIdList, AssetGroupIdList, AssetTypeIdList, customAssetGroupIdList
			 * @param LandmarkCategoryList, LandmarkIdList, flag
			 * @throws CustomFault
			 * @return ServiceDueOverDueImpl
			 */
			//--
			
			//20151119 - S Suresh new method implementation for getting User Alerts 
			public ServiceDueOverDueImpl getNonActiveUserAlerts(String LoginId, String Period, List<Integer> TenancyIdList, List<Integer> AssetGroupIdList, 
					List<Integer> AssetTypeIdList,List<Integer> customAssetGroupIdList, List<Integer> LandmarkCategoryList, List<Integer> LandmarkIdList,boolean flag,List<Integer> eventTypeIdList,List<String> eventSeverityList,
					boolean isOwnStock,boolean isActiveAlerts)	throws CustomFault{
				Logger iLogger = InfoLoggerClass.logger;
		    	
				long startTime = System.currentTimeMillis();	
				long duecount = 0;
				long overduecount = 0;
				String basicSelectQuery=null;
				String basicWhereQuery=null;
				String basicFromQuery=null;
				String finalQuery = null;		
				Calendar c=Calendar.getInstance();
				int currentYear = c.get(Calendar.YEAR);

				ListToStringConversion conversionObj = new ListToStringConversion();
			    String tenancyIdStringList = conversionObj.getIntegerListString(TenancyIdList).toString();
			    if(isActiveAlerts){
			    	return getActiveAlerts(tenancyIdStringList,eventTypeIdList,eventSeverityList,flag,isOwnStock);
			    }
			    
			  //20151119 - S Suresh query tweaking to improve the performance
				basicSelectQuery ="select e.Notification_Id,e.Notification_Name, e.Severity,a.NotificationCount,sum(a.NotificationCount) ";		

				basicWhereQuery = " where a.Tenancy_Id=t.tenacy_Dimension_Id and f.parentId in ( " + tenancyIdStringList +" ) and a.Notification_Id=e.Notification_Dimension_Id and f.childId=t.tenancyId " ;
				//Changes Done by Juhi on 6 May2013
				
				 if ((Period.equalsIgnoreCase("Week"))||(Period.equalsIgnoreCase("Last Week"))) {
				 	   Date currentDate = new Date();
				 	   DateUtil dateUtilObj = new DateUtil();
				 	  DateUtil dateUtilObj1 = new DateUtil();
				 	   if (Period.equalsIgnoreCase("Week")){
				 	    dateUtilObj  = dateUtilObj. getCurrentDateUtility(currentDate);
				 	    int week = dateUtilObj. getWeek();
				 	    int year = dateUtilObj. getYear();
				 	    basicFromQuery = " from  notification_fact_weekagg a ";
				 	    basicWhereQuery = basicWhereQuery+ " and a.TimeCount = "+week+" and a.Year = "+year+"";
				 	   }
				 	   if(Period.equalsIgnoreCase("Last Week")){
				 	    dateUtilObj  = dateUtilObj. getPreviousDateUtility(currentDate);
				 	   dateUtilObj1  = dateUtilObj1. getCurrentDateUtility(currentDate);
				 	    int week = dateUtilObj. getWeek();
				 	    int year=0;
				 	    if(dateUtilObj1.getWeek()==1)
				 	     year = dateUtilObj.getYear();
				 	    else
				 	     year = dateUtilObj.getCurrentYear();
				 	    basicFromQuery = " from  notification_fact_weekagg a ";
				 	    basicWhereQuery = basicWhereQuery+ " and a.TimeCount = "+week+" and a.Year = "+year+"";
				 	   }
				 	  }
				 	  else if ((Period.equalsIgnoreCase("Month"))||(Period.equalsIgnoreCase("Last Month"))) {
				 	   Date currentDate = new Date();
				 	   DateUtil dateUtilObj = new DateUtil(); 
				 	  DateUtil dateUtilObj1 = new DateUtil(); 
				 	   if(Period.equalsIgnoreCase("Month")){
				 	    dateUtilObj  = dateUtilObj. getCurrentDateUtility(currentDate);
				 	    int month = dateUtilObj. getMonth ();
				 	    int year = dateUtilObj. getYear();
				 	    basicFromQuery = " from  notification_fact_Monthagg a ";
				 	    basicWhereQuery = basicWhereQuery+ " and a.TimeCount = "+month+" and a.Year = "+year+"";
				 	   }
				 	   if(Period.equalsIgnoreCase("Last Month")){
				 		  dateUtilObj1  = dateUtilObj1. getCurrentDateUtility(currentDate);
				 	    dateUtilObj  = dateUtilObj. getPreviousDateUtility (currentDate);
				 	    int month = dateUtilObj. getMonth ();
				 	    int year=0;
				 	    if(dateUtilObj1.getMonth() ==1)
				 	     year = dateUtilObj.getYear();
				 	    else
				 	     year = dateUtilObj.getCurrentYear();
				 	    basicFromQuery = " from  notification_fact_Monthagg a ";
				 	    basicWhereQuery = basicWhereQuery+ " and a.TimeCount = "+month+" and a.Year = "+year+"";
				 	   }
				 	  } 
				 	  else if((Period.equalsIgnoreCase("Quarter"))||(Period.equalsIgnoreCase("Last Quarter"))) {
				 	   Date currentDate = new Date();
				 	   DateUtil dateUtilObj = new DateUtil();
				 	  DateUtil dateUtilObj1 = new DateUtil();
				 	   if(Period.equalsIgnoreCase("Quarter")){
				 	    dateUtilObj  = dateUtilObj. getCurrentDateUtility(currentDate);
				 	    int quarter = dateUtilObj. getQuarter ();
				 	    int year = dateUtilObj. getYear();
				 	    basicFromQuery = " from notification_fact_Quarteragg a ";
				 	    basicWhereQuery = basicWhereQuery+ " and a.TimeCount = "+quarter+" and a.Year = "+year+"";
				 	   }
				 	   if(Period.equalsIgnoreCase("Last Quarter")){
				 		  dateUtilObj1  = dateUtilObj1. getCurrentDateUtility(currentDate);
				 	    dateUtilObj  = dateUtilObj. getPreviousDateUtility(currentDate);
				 	    int quarter = dateUtilObj. getQuarter ();
				 	    int year=0;
				 	    if(dateUtilObj1.getQuarter() ==1)
				 	     year = dateUtilObj.getYear();
				 	    else
				 	     year = dateUtilObj.getCurrentYear();
				 	    basicFromQuery = " from notification_fact_Quarteragg a ";
				 	    basicWhereQuery = basicWhereQuery+ " and a.TimeCount = "+quarter+" and a.Year = "+year+"";
				 	   }
				 	  }
				 	  else if ((Period.equalsIgnoreCase("Year"))||(Period.equalsIgnoreCase("Last Year")))
				 	  {
				 	   Date currentDate = new Date();
				 	   DateUtil dateUtilObj = new DateUtil();
				 	   if(Period.equalsIgnoreCase("Year")){
				 	    dateUtilObj  = dateUtilObj. getCurrentDateUtility(currentDate);
				 	    int year = dateUtilObj. getYear();
				 	    basicFromQuery = " from notification_fact_Yearagg a ";
				 	    basicWhereQuery = basicWhereQuery+ " and a.Year = "+year+"";
				 	   }
				 	   if(Period.equalsIgnoreCase("Last Year")){
				 	    dateUtilObj  = dateUtilObj. getPreviousDateUtility(currentDate);
				 	    int year = dateUtilObj. getYear();
				 	    basicFromQuery = " from notification_fact_Yearagg a ";
				 	    basicWhereQuery = basicWhereQuery+ " and a.Year = "+year+"";
				 	   }
				 	  }
				 	  else if(Period.equalsIgnoreCase("Today")){
				 		  
					 	    basicFromQuery = " from NotificationFactEntity_DayAgg a ";	
							basicWhereQuery = basicWhereQuery+ " and  a.Time_Key = (select max(Time_Key) from a) ";			
							
					 	  
				 	  }
				basicFromQuery=basicFromQuery+" , NotificationDimensionEntity e , TenancyDimensionEntity t ";
				basicFromQuery = basicFromQuery +"  JOIN a.AssetClass_Id d , TenancyBridgeEntity f ";
				
				
				if(!(customAssetGroupIdList==null || customAssetGroupIdList.isEmpty())){
					  String customAssetGroupStringList = conversionObj.getIntegerListString(customAssetGroupIdList).toString();
					  basicFromQuery = basicFromQuery
						+ " , CustomAssetGroupEntity c, AssetCustomGroupMapping h ";
				basicWhereQuery = basicWhereQuery
						+ " and c.group_id = h.group_id and c.group_id in ("
						+ customAssetGroupStringList + ") and "
						+ " h.serial_number = a.SerialNumber ";
			    }
			    if(!(AssetGroupIdList==null || AssetGroupIdList.isEmpty())){
			          String assetGroupStringList = conversionObj.getIntegerListString(AssetGroupIdList).toString();
			          basicWhereQuery = basicWhereQuery + " and d.assetGroupId in ( "+ assetGroupStringList +" )"; 
			    }
		          if(flag==true){	    
			    	basicWhereQuery = basicWhereQuery + "and e.Notification_Type_Name like:Service";
			    }
		       
		          if(!(eventTypeIdList==null || eventTypeIdList.isEmpty())){
		        	  String eventTypeIdStringList = conversionObj.getIntegerListString(eventTypeIdList).toString();
		        	  basicWhereQuery = basicWhereQuery + " and e.Notification_Type_Id in ("+ eventTypeIdStringList +")";
		        	  
		          }
		          if(! (eventSeverityList==null || eventSeverityList.isEmpty())) {
		        	  String eventSeverityListAsString = conversionObj.getStringList(eventSeverityList).toString();
		        	  basicWhereQuery = basicWhereQuery + " and e.Severity in ("+ eventSeverityListAsString +")";
		          }
		          
					if(!(AssetTypeIdList==null || AssetTypeIdList.isEmpty())){
				          String assetTypeStringList = conversionObj.getIntegerListString(AssetTypeIdList).toString();
				          basicWhereQuery = basicWhereQuery + " and d.assetTypeId in ( "+ assetTypeStringList +" )"; 
				    }
					
					//check for own stock
					if(isOwnStock){
						basicWhereQuery = basicWhereQuery + " AND f.childId IN ("+tenancyIdStringList+")";
					}
					String groupQuery = " group by e.Severity";
			    finalQuery = basicSelectQuery + basicFromQuery + basicWhereQuery + groupQuery;
				 iLogger.info("Final Query : " + finalQuery);
				 Session session= null;
				 ServiceDueOverDueImpl implObj = new ServiceDueOverDueImpl();
				 try{
					session = HibernateUtil.getSessionFactory().openSession();
					session.beginTransaction(); 		    
				    Query query = session.createQuery(finalQuery);
				    if(flag==true){
				    String Service = "Service";
				    query.setString("Service", Service);
				    }
				    Iterator itr = query.list().iterator();
				    Object[] result=null;
				    
				    while(itr.hasNext()){
				    	result = (Object[]) itr.next();
				    	
				    	if(result[2]!=null){
				    		if(result[2].toString().equalsIgnoreCase("Yellow")){
				    			if(result[4]!=null){
					    			duecount=duecount+(Long)result[4];
					    		}	
				    		}	    		    		
					    	else if(result[2].toString().equalsIgnoreCase("Red")){
					    		if(result[4]!=null){
					    			overduecount=overduecount+(Long)result[4];
					    		}		    		
					    	}    	
				    	}
				    }
				   
					implObj.setServiceDueCount((int)duecount);
					implObj.setServiceOverduecount((int)overduecount);
					iLogger.info("Due count in BO : " + duecount);  
					iLogger.info("Overdue count in BO:" + implObj.getServiceOverduecount());
				}
				finally{
					if(session.getTransaction().isActive()){      
						session.getTransaction().commit();
					}
					if(session.isOpen()){
						session.flush();
					    session.close();
					 } 
				}
				long endTime=System.currentTimeMillis();
				iLogger.info("Total execution time "+ String.valueOf(endTime-startTime)+ "(ms)");
				return implObj;
			}
			
			public List<AssetEventEntity> checkDuplicateAlert(String VIN,int eventID,int eventTypeID,int active_status){
				Logger fLogger = FatalLoggerClass.logger;
				boolean duplicate = false;
				Session session = null;
				List<AssetEventEntity> eventList = new LinkedList<AssetEventEntity>();
				try{
				
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.beginTransaction();
				String eventQuery = " from AssetEventEntity where activeStatus= "+active_status+" and partitionKey =1";
				if(VIN!=null){
					//eventQuery += "serialNumber='"+VIN.getSerial_number().getSerialNumber()+"' ";
					eventQuery += " and serialNumber='"+VIN+"' ";
				}
				if(eventID!=0)
				{
					eventQuery += "and eventId="+eventID;
				}
				if(eventTypeID!=0){
					eventQuery += " and eventTypeId = "+eventTypeID;
				}
					//----------------- STEP1: If any other active service alert is there for the VIN, nullify  the same before generating the new service alert
					Query serviceAlertQuery = session.createQuery(eventQuery);
					Iterator serviceAlertItr = serviceAlertQuery.list().iterator();
					while(serviceAlertItr.hasNext()){
						AssetEventEntity eventEntity = (AssetEventEntity) serviceAlertItr.next();
						eventList.add(eventEntity);
					}
				
				
				}catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("Exception :"+e);
				}

				//DF-20131115 session not getting closed
				finally
				{
				     
				      
				      if(session.isOpen())
				      {
				            //session.flush();
				            session.close();
				      }
				      
				}
				return eventList;
			}


			//DF20170216 @Roopa Performance improvement + RoleBased Alert implementation

			public AlertSummaryRespContract getOverviewAlertSummaryDetails(String period, List<Integer> loginTenancyIdList, List<Integer> TenancyIdList, List<Integer> eventTypeIdList, List<String> eventSeverityList, List<Integer> assetTypeIdList, List<Integer> assetGroupIdList, boolean ownStock, boolean activeAlerts, List<Integer> customAssetGroupIdList,String contactId){
				AlertSummaryRespContract response = new AlertSummaryRespContract();
				Logger fLogger = FatalLoggerClass.logger;
				Logger iLogger = InfoLoggerClass.logger;
				String MDAassetTypeCodeList=null;
				String MDAassetGroupCodeList=null;
				String MDAeventTypeCodeList=null;
				String MDAeventSeverityList=null;
				String MDAaccountIdListAsString=null;
				String MDAoutput=null;
				String MDAresult=null;
				Connection prodConnection = null;
				Statement statement = null;
				ResultSet rs = null;

				String basicQueryString = null;
				String fromQuery = null;
				String whereQuery = null;
				String groupByQuery = null;

				//StringBuilder tenancyListAsString = null;
				StringBuilder eventTypeIdListAsString = null;
				StringBuilder eventSeverityListAsString = null;
				StringBuilder alertCodeListAsString = null;

				StringBuilder assetTypeIdListAsString = null;
				StringBuilder assetGroupIdListAsString = null;

				//CR337.sn
				String connIP=null;
				String connPort=null;
				Properties prop = null;
				try{
					prop = CommonUtil.getDepEnvProperties();
					connIP = prop.getProperty("MDA_ServerIP");
					connPort = prop.getProperty("MDA_ServerPort");
					iLogger.info("EventDetailsBO : MDA_ServerIP:" +connIP +" :: MDA_ServerPort:"+connPort);
				}catch(Exception e){
					fLogger.fatal("EventDetailsBO : getOverviewAlertSummaryDetails : " +
							"Exception in getting Server Details for MDA Layer from properties file: " +e);
				}
				//CR337.en
				
				String finalQuery = null;
				
				List<Integer> selectedTenancy = new LinkedList<Integer>();

				ListToStringConversion utilClass = new ListToStringConversion();

				if (TenancyIdList != null && TenancyIdList.size() > 0) {

					//tenancyListAsString = utilClass.getIntegerListString(TenancyIdList);
					selectedTenancy.addAll(TenancyIdList);

				} else {
					/*tenancyListAsString = utilClass
							.getIntegerListString(loginTenancyIdList);*/
					selectedTenancy.addAll(loginTenancyIdList);
				}

				if (eventTypeIdList != null && eventTypeIdList.size() > 0) {

					eventTypeIdListAsString = utilClass
							.getIntegerListString(eventTypeIdList);
				}

				if (eventSeverityList != null && eventSeverityList.size() > 0) {

					eventSeverityListAsString = utilClass
							.getStringList(eventSeverityList);
				}

				if (assetTypeIdList != null && assetTypeIdList.size() > 0) {

					assetTypeIdListAsString = utilClass
							.getIntegerListString(assetTypeIdList);
				}

				if (assetGroupIdList != null && assetGroupIdList.size() > 0) {

					assetGroupIdListAsString = utilClass
							.getIntegerListString(assetGroupIdList);
				}

				Session session = HibernateUtil.getSessionFactory().openSession();

				try {
					List<Integer> AccountIdList = new ArrayList<Integer>();

					List<String> AccountCodeList = new ArrayList<String>();

				/*	AccountEntity account = null;

					Query accountQ = session
							.createQuery(" select at.account_id from AccountTenancyMapping at where at.tenancy_id in("
									+ tenancyListAsString + ") ");
					Iterator accItr = accountQ.list().iterator();
					if (accItr.hasNext()) {
						account = (AccountEntity) accItr.next();
						AccountIdList.add(account.getAccount_id());

						AccountCodeList.add(account.getAccountCode());
					}

					if (session != null && session.isOpen()) {
						session.close();
					}

					StringBuilder AccountIdListAsString = utilClass
							.getIntegerListString(AccountIdList);
					StringBuilder AccountCodelistAsString = utilClass
							.getStringList(AccountCodeList);*/
					//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
					
					
					
					String AccountIdListAsString=new DateUtil().getAccountListForTheTenancy(selectedTenancy);
					String AccountCodelistAsString=new DateUtil().getAccountCodeListForTheTenancy(selectedTenancy);
					AccountCodeList.add(AccountCodelistAsString.replace("\'", "").split(",")[0]);
					MDAaccountIdListAsString=AccountCodelistAsString.replace("\'", "");

					// DF20170130 @Roopa for Role based alert implementation
					DateUtil utilObj = new DateUtil();

					List<String> alertCodeList = utilObj.roleAlertMapDetails(null,
							loginTenancyIdList.get(0), "Display");

					if (alertCodeList != null && alertCodeList.size() > 0)
						alertCodeListAsString = utilClass.getStringList(alertCodeList);

					if(customAssetGroupIdList==null || (customAssetGroupIdList != null && customAssetGroupIdList.isEmpty()))
					{
					if (period == null) {
						basicQueryString = "select ae.Event_Severity, count(*) as alertCount";
						fromQuery = " from asset_event ae, asset_owner_snapshot aos ";

						whereQuery = " where aos.Serial_Number=ae.Serial_Number and aos.account_ID in ("
								+ AccountIdListAsString + ") ";
						if (activeAlerts == true) {
							whereQuery = whereQuery + " and ae.Active_Status=1 and ae.PartitionKey =1 ";
						} else {
							whereQuery = whereQuery + " and ae.Active_Status=0 ";
						}

						groupByQuery = " group by ae.Event_Severity";

						if (alertCodeListAsString != null) {
							fromQuery = fromQuery + ", business_event be ";
							whereQuery = whereQuery
									+ " and ae.Event_ID=be.Event_ID and be.Alert_Code in("
									+ alertCodeListAsString + ")";

						}

						if (ownStock == true) {
							fromQuery = fromQuery + ", asset a ";
							whereQuery = whereQuery
									+ " and aos.Serial_Number=a.Serial_Number and aos.account_ID=a.Primary_Owner_ID ";
						}

						if (eventTypeIdListAsString != null) {

							whereQuery = whereQuery + " and ae.Event_Type_ID in ("
									+ eventTypeIdListAsString + ") ";
						}

						if (eventSeverityListAsString != null) {

							whereQuery = whereQuery + " and ae.Event_Severity in ("
									+ eventSeverityListAsString + ") ";
						}

						/*if ((!(assetGroupIdList == null || assetGroupIdList.isEmpty()))
								|| (!((assetTypeIdList == null) || (assetTypeIdList
										.isEmpty())))) {

							fromQuery = fromQuery
									+ " , AssetMonitoringFactDataDayAgg g, AssetClassDimensionEntity a ";
							whereQuery = whereQuery
									+ " and g.assetClassDimensionId=a.assetClassDimensionId";
						}
						if (!((assetGroupIdList == null) || (assetGroupIdList.isEmpty()))) {
							whereQuery = whereQuery + " and a.assetGroupId in ( "
									+ assetGroupIdListAsString + " )";
						}
						if (!((assetTypeIdList == null) || (assetTypeIdList.isEmpty()))) {
							whereQuery = whereQuery + " and a.assetTypeId in ( "
									+ assetTypeIdListAsString + " )";
						}*/
						
						if ((!(assetGroupIdList == null || assetGroupIdList.isEmpty()))
								|| (!((assetTypeIdList == null) || (assetTypeIdList
										.isEmpty())))) {
							//Df20170606 @Roopa pointing to right table

							fromQuery = fromQuery + " , remote_monitoring_fact_data_dayagg_json_new g, asset_class_dimension a ";
							whereQuery = whereQuery
							+ " and g.Asset_Class_ID=a.Asset_Class_Dimension_ID";
						}
						if (!((assetGroupIdList == null) || (assetGroupIdList.isEmpty()))) {
							whereQuery = whereQuery + " and a.Asset_Group_ID in ( "
							+ assetGroupIdListAsString + " )";
						}
						if (!((assetTypeIdList == null) || (assetTypeIdList.isEmpty()))) {
							whereQuery = whereQuery + " and a.Asset_Type_ID in ( "
							+ assetTypeIdListAsString + " )";
						}

						finalQuery = basicQueryString + fromQuery + whereQuery
								+ groupByQuery;

						//System.out.println("finalQuery ::" + finalQuery);

						ConnectMySQL connMySql = new ConnectMySQL();
						prodConnection = connMySql.getConnection();
						statement = prodConnection.createStatement();
						rs = statement.executeQuery(finalQuery);

						int redCount = 0;

						int yellowCount = 0;

						while (rs.next()) {

							String severity = rs.getString("Event_Severity");

							int count = rs.getInt("alertCount");

							if (severity.equalsIgnoreCase("RED")) {

								redCount = redCount + count;
							}

							// total alert yellow count

							if (severity.equalsIgnoreCase("YELLOW")) {

								yellowCount = yellowCount + count;
							}

						}

						response.setRedThresholdValue(redCount);
						response.setYellowThresholdValue(yellowCount);
					} else {
						if(period.equalsIgnoreCase("Year"))
						{

						basicQueryString = "select ai.AlertSeverity, count(*) as alertCount";
						fromQuery = " from alertInsight_detail ai ";

						whereQuery = " where (ai.ZonalCode in ("
								+ AccountCodelistAsString + ") or ai.DealerCode in("
								+ AccountCodelistAsString + ") or ai.CustCode in("
								+ AccountCodelistAsString + ")) ";

						groupByQuery = " group by ai.AlertSeverity";

						if (alertCodeListAsString != null) {

							List<Integer> eventIdList = utilObj
									.getEventIdListForAlertCodes(alertCodeListAsString);

							StringBuilder EventIdListAsString = utilClass
									.getIntegerListString(eventIdList);

							whereQuery = whereQuery + " and ai.AlertCode in("
									+ EventIdListAsString + ")";

						}

						int dateAsNum = 0;
						int calYear = 0;
						String Timeperiod = null;
						Calendar cal = Calendar.getInstance();

						if (period.equalsIgnoreCase("Week")) {
							Timeperiod = "Week";
							calYear = cal.get(Calendar.YEAR);
							dateAsNum = cal.get(Calendar.WEEK_OF_YEAR);
						} else if (period.equalsIgnoreCase("Last Week")) {
							Timeperiod = "Week";
							calYear = cal.get(Calendar.YEAR);
							cal.set(Calendar.WEEK_OF_YEAR,
									cal.get(Calendar.WEEK_OF_YEAR) - 1);
							dateAsNum = cal.get(Calendar.WEEK_OF_YEAR);
						}

						else if (period.equalsIgnoreCase("Month")) {
							Timeperiod = "Month";
							calYear = cal.get(Calendar.YEAR);
							dateAsNum = cal.get(Calendar.MONTH);
						} else if (period.equalsIgnoreCase("Last Month")) {
							Timeperiod = "Month";
							calYear = cal.get(Calendar.YEAR);
							cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
							dateAsNum = cal.get(Calendar.MONTH);
						}

						else if (period.equalsIgnoreCase("Quarter")) {
							Timeperiod = "Quarter";
							calYear = cal.get(Calendar.YEAR);
							dateAsNum = ((cal.get(Calendar.MONTH))) / 3 + 1;
						}

						else if (period.equalsIgnoreCase("Last Quarter")) {
							Timeperiod = "Quarter";
							calYear = cal.get(Calendar.YEAR);
							cal.set((cal.get(Calendar.MONTH)), -1);
							dateAsNum = ((cal.get(Calendar.MONTH))) / 3 + 1;
						} else if (period.equalsIgnoreCase("Year")) {
							Timeperiod = "Year";
							dateAsNum = cal.get(Calendar.YEAR);
						} else if (period.equalsIgnoreCase("Last Year")) {
							Timeperiod = "Year";
							cal.set((cal.get(Calendar.YEAR)), -1);
							dateAsNum = cal.get(Calendar.YEAR);
						}

						if (dateAsNum != 0) {

							if (Timeperiod != null) {
								if (Timeperiod.equalsIgnoreCase("Week")) {

									whereQuery = whereQuery + " and ai.TxnWeek="
											+ dateAsNum + " and ai.TxnYear=" + calYear;
								}
								if (Timeperiod.equalsIgnoreCase("Month")) {

									whereQuery = whereQuery + " and ai.TxnMonth="
											+ dateAsNum + " and ai.TxnYear=" + calYear;
								}
								if (Timeperiod.equalsIgnoreCase("Quarter")) {

									whereQuery = whereQuery + " and ai.TxnQuarter="
											+ dateAsNum + " and ai.TxnYear=" + calYear;
								}
								if (Timeperiod.equalsIgnoreCase("Year")) {

									whereQuery = whereQuery + " and ai.TxnYear="
											+ dateAsNum + "";
								}
							}

						}

						ListToStringConversion conversionObj = new ListToStringConversion();
						DateUtil utl = new DateUtil();
						if (assetTypeIdListAsString != null) {
							StringBuilder assetTypeCodeListAsString = null;

							List<String> assetTypeCodeList = utl
									.getAssetTypeIdList(assetTypeIdListAsString);
							if (assetTypeCodeList != null
									&& assetTypeCodeList.size() > 0)
								assetTypeCodeListAsString = conversionObj
										.getStringList(assetTypeCodeList);
							whereQuery = whereQuery + " and ai.ModelCode in ("
									+ assetTypeCodeListAsString + ") ";
						}

						if (assetGroupIdListAsString != null) {
							List<String> assetGroupCodeList = utl
									.getAssetGroupIdList(assetGroupIdListAsString);
							StringBuilder assetGroupCodeListAsString = null;
							if (assetGroupCodeList != null
									&& assetGroupCodeList.size() > 0)
								assetGroupCodeListAsString = conversionObj
										.getStringList(assetGroupCodeList);
							whereQuery = whereQuery + " and ai.ProfileCode in ("
									+ assetGroupCodeListAsString + ") ";

						}

						if (eventTypeIdListAsString != null) {

							whereQuery = whereQuery + " and ai.AlertTypeCode in ("
									+ eventTypeIdListAsString + ") ";
						}

						if (eventSeverityListAsString != null) {
							whereQuery = whereQuery + " and ai.AlertSeverity in ("
									+ eventSeverityListAsString + ") ";
						}

						finalQuery = basicQueryString + fromQuery + whereQuery
								+ groupByQuery;

						System.out.println("finalQuery ::" + finalQuery);

						ConnectMySQL connMySql = new ConnectMySQL();
						prodConnection = connMySql.getProdDb2Connection();
						statement = prodConnection.createStatement();
						rs = statement.executeQuery(finalQuery);

						int redCount = 0;

						int yellowCount = 0;

						while (rs.next()) {

							String severity = rs.getString("AlertSeverity");

							int count = rs.getInt("alertCount");

							if (severity.equalsIgnoreCase("RED")) {

								redCount = redCount + count;
							}

							// total alert yellow count

							if (severity.equalsIgnoreCase("YELLOW")) {

								yellowCount = yellowCount + count;
							}

						}

						response.setRedThresholdValue(redCount);
						response.setYellowThresholdValue(yellowCount);

						}
						else
						{
							//MOOL DB changes
							Connection prodConn = null;
							Statement stmnt = null;
							ResultSet res = null;
							try{
							ConnectMySQL connSql = new ConnectMySQL();
							prodConn = connSql.getConnection();
							stmnt = prodConn.createStatement();
							HashMap<String,Integer> MDAOutputMap = null;
							DateUtil dateUtil1 = new DateUtil();
							ListToStringConversion conversionObj = new ListToStringConversion();
							//For accountFilter filling
							if(AccountCodeList!=null && AccountCodeList.size()>0){
									String acntTncyQuery = "select Tenancy_Type_Name from tenancy_type where Tenancy_Type_ID=(select Tenancy_Type_ID from tenancy where Tenancy_ID=(select Tenancy_ID from account_tenancy where Account_ID="
											+ AccountCodeList.get(0) + "))";
							res=stmnt.executeQuery(acntTncyQuery);
							String tenancy_type_name="";
							if(res.next())
							{
								tenancy_type_name=res.getString("Tenancy_Type_Name");
							}
							String accountFilter="";
							if(tenancy_type_name!=null && !tenancy_type_name.equalsIgnoreCase(""))
							{
								if(tenancy_type_name.equalsIgnoreCase("Global"))
								{
									accountFilter=null;
								}
								else if(tenancy_type_name.equalsIgnoreCase("Regional"))
								{
									accountFilter="RegionCode";
								}
								else if(tenancy_type_name.equalsIgnoreCase("Zonal"))
								{
									accountFilter="ZonalCode";
								}
								else if(tenancy_type_name.equalsIgnoreCase("Dealer"))
								{
									accountFilter="DealerCode";
								}
								else if(tenancy_type_name.equalsIgnoreCase("Customer"))
								{
									accountFilter="CustCode";
								}
								else
								{
									accountFilter="";
								}
								int MDAstock=0;
								if(ownStock)
								{
									MDAstock=1;
								}
								if (assetTypeIdListAsString != null) {
									StringBuilder assetTypeCodeListAsString = null;

									List<String> assetTypeCodeList = dateUtil1
											.getAssetTypeIdList(assetTypeIdListAsString);
									if (assetTypeCodeList != null
											&& assetTypeCodeList.size() > 0){
										assetTypeCodeListAsString = conversionObj
												.getStringList(assetTypeCodeList);
										StringBuilder MDAassetTypeCodeListBuilder=conversionObj.getStringWithoutQuoteList(assetTypeCodeList);
										MDAassetTypeCodeList=MDAassetTypeCodeListBuilder.toString();
										MDAassetTypeCodeList=ListToStringConversion.removeLastComma(MDAassetTypeCodeList);
									}
									/*whereQuery = whereQuery + " and fi.ModelCode in ("
											+ assetTypeCodeListAsString + ") ";*/
								}

								if (assetGroupIdListAsString != null) {
									List<String> assetGroupCodeList = dateUtil1
											.getAssetGroupIdList(assetGroupIdListAsString);
									StringBuilder assetGroupCodeListAsString = null;
									if (assetGroupCodeList != null
											&& assetGroupCodeList.size() > 0){
										assetGroupCodeListAsString = conversionObj
												.getStringList(assetGroupCodeList);
										StringBuilder MDAassetGroupCodeListBuilder=conversionObj.getStringWithoutQuoteList(assetGroupCodeList);
										MDAassetGroupCodeList=MDAassetGroupCodeListBuilder.toString();
										MDAassetGroupCodeList=ListToStringConversion.removeLastComma(MDAassetGroupCodeList);
										}
								
									/*whereQuery = whereQuery + " and fi.ProfileCode in ("
											+ assetGroupCodeListAsString + ") ";*/

								}
								if(eventSeverityList != null && eventSeverityList.size() > 0)
								{	
									StringBuilder MDAeventSeverityListBuilder=conversionObj.getStringWithoutQuoteList(eventSeverityList);
									MDAeventSeverityList=MDAeventSeverityListBuilder.toString();
									MDAeventSeverityList=ListToStringConversion.removeLastComma(MDAeventSeverityList);
								}
								AlertSummaryRespContract MDAResponse=new AlertSummaryRespContract();
								ObjectMapper mapper = new ObjectMapper();
								

								try{
											String url=
													// Dhiraj K : 20220630 : Change IP for AWS server
													// "http://10.179.12.25:26030/MoolDAReports/LLAlertSummaryService/getLLAlertSeveritySummary?accountFilter="
													//"http://10.210.196.206:26030/MoolDAReports/LLAlertSummaryService/getLLAlertSeveritySummary?accountFilter="//CR337.o
													"http://"+connIP+":"+connPort+"/MoolDAReports/LLAlertSummaryService/getLLAlertSeveritySummary?accountFilter="//CR337.n
															+ accountFilter
															+ "&accountIDList="
															+ MDAaccountIdListAsString
															+ "&period="
															+ period
															+ "&modelCodeList="
															+ MDAassetTypeCodeList
															+ "&profileCodeList="
															+ MDAassetGroupCodeList
															+"&alertTypeCodeList="
															+eventTypeIdListAsString
															+"&alertSeverity="
															+MDAeventSeverityList
															+ "&isOwnStock="
															+ MDAstock
															+ "&loginID=null"
															+ "&countryCode=null";
											System.out.println("MDA LLAlertSummaryService  URL : "+url);
											iLogger.info("MDA LLAlertSummaryService URL : "+url);
											URL MDAUrl = new URL(url);
											 HttpURLConnection conn =(HttpURLConnection) MDAUrl.openConnection();
											    conn.setRequestMethod("GET"); 
											    conn.setRequestProperty("Accept", "application/json");
												  if (conn.getResponseCode() != 200  && conn.getResponseCode() != 204) {
													  iLogger.info("MDAReports report status: FAILURE for LLAlertSummaryService Report ::Response Code:"+conn.getResponseCode());
														throw new RuntimeException("Failed : HTTP error code : " +conn.getResponseCode()); 
													  }
												  iLogger.info("MDAReports report status: SUCCESS for LLAlertSummaryService Report ::Response Code:"+conn.getResponseCode());
												  BufferedReader br = new BufferedReader(new  InputStreamReader((conn.getInputStream())));
												  
												  System.out.println("MDA LLAlertSummaryService Output from Server .... \n");
												  while ((MDAoutput = br.readLine()) != null) { 
													  //System.out.println("MDA AlertSummary json output "+MDAoutput); 
													  iLogger.info("MDA LLAlertSummaryService json output "+MDAoutput);
													  MDAresult =MDAoutput; } 
												  MDAOutputMap = new Gson().fromJson(MDAresult, new TypeToken<HashMap<String, Integer>>(){}.getType());
												  response.setRedThresholdValue(MDAOutputMap.get("RED"));
												  response.setYellowThresholdValue(MDAOutputMap.get("YELLOW"));
												  //System.out.println("MDAResponse LLAlertSummaryService outmap : "+MDAOutputMap);
												  iLogger.info("MDAResponse LLAlertSummaryService outmap : "+MDAOutputMap);
												  conn.disconnect();
								}catch(Exception e)
								{
									e.printStackTrace();
									fLogger.fatal("Error occured while connecting to Mongo DB "+e.getMessage());
								}
								
								}
							
							
							}
							else{
								fLogger.fatal("AccountCodeList is empty");
								throw new CustomFault("AccountCodeList is empty");
							}
								
						}catch(Exception e)
						{	e.printStackTrace();
							fLogger.fatal("Exception occured "+e.getMessage());
						}
							finally {
								if (res != null)
									try {
										res.close();
									} catch (SQLException e1) {
										e1.printStackTrace();
									}

								if (stmnt != null)
									try {
										stmnt.close();
									} catch (SQLException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

								if (prodConn != null) {
									try {
										prodConn.close();
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}

							}
						
						
						}
						}//end of else
					}else{
						String customAssetGroupIdListAsString=null;
						customAssetGroupIdListAsString=AssetUtil.getIDListAsCommaSeperated(customAssetGroupIdList);
						HashMap<String,Integer> MDAOutputMap = null;
						DateUtil dateUtil1 = new DateUtil();
						ListToStringConversion conversionObj = new ListToStringConversion();
						try{
							String accountFilter = null;
							
							int MDAstock=1;
							if(ownStock==false)
								MDAstock=0;
							if (assetTypeIdListAsString != null) {
								StringBuilder assetTypeCodeListAsString = null;

								List<String> assetTypeCodeList = dateUtil1
										.getAssetTypeIdList(assetTypeIdListAsString);
								if (assetTypeCodeList != null
										&& assetTypeCodeList.size() > 0){
									assetTypeCodeListAsString = conversionObj
											.getStringList(assetTypeCodeList);
									StringBuilder MDAassetTypeCodeListBuilder=conversionObj.getStringWithoutQuoteList(assetTypeCodeList);
									MDAassetTypeCodeList=MDAassetTypeCodeListBuilder.toString();
									MDAassetTypeCodeList=ListToStringConversion.removeLastComma(MDAassetTypeCodeList);
								}
								/*whereQuery = whereQuery + " and fi.ModelCode in ("
										+ assetTypeCodeListAsString + ") ";*/
							}

							if (assetGroupIdListAsString != null) {
								List<String> assetGroupCodeList = dateUtil1
										.getAssetGroupIdList(assetGroupIdListAsString);
								StringBuilder assetGroupCodeListAsString = null;
								if (assetGroupCodeList != null
										&& assetGroupCodeList.size() > 0){
									assetGroupCodeListAsString = conversionObj
											.getStringList(assetGroupCodeList);
									StringBuilder MDAassetGroupCodeListBuilder=conversionObj.getStringWithoutQuoteList(assetGroupCodeList);
									MDAassetGroupCodeList=MDAassetGroupCodeListBuilder.toString();
									MDAassetGroupCodeList=ListToStringConversion.removeLastComma(MDAassetGroupCodeList);
									}
							
								/*whereQuery = whereQuery + " and fi.ProfileCode in ("
										+ assetGroupCodeListAsString + ") ";*/

							}
							if(eventSeverityList != null && eventSeverityList.size() > 0)
							{	
								StringBuilder MDAeventSeverityListBuilder=conversionObj.getStringWithoutQuoteList(eventSeverityList);
								MDAeventSeverityList=MDAeventSeverityListBuilder.toString();
								MDAeventSeverityList=ListToStringConversion.removeLastComma(MDAeventSeverityList);
							}
							String url=
									"http://localhost:26030/MoolDAReports/LLAlertSummaryService/getLLAlertSeveritySummaryMachineGroup?accountFilter="
											+ "null"
											+ "&accountIDList="
											+ "null"
											+ "&period=week"
											+ "&modelCodeList="
											+ MDAassetTypeCodeList
											+ "&profileCodeList="
											+ MDAassetGroupCodeList
											+"&alertTypeCodeList="
											+eventTypeIdListAsString
											+"&alertSeverity="
											+MDAeventSeverityList
											+ "&isOwnStock="
											+ MDAstock
											+ "&loginID="
											+contactId
											+ "&countryCode=null"
											+"&MachineGrpIDList=" 
											+customAssetGroupIdListAsString;
											
							System.out.println("MDA LLAlertSummaryService  URL : "+url);
							iLogger.info("MDA LLAlertSummaryService URL : "+url);
							URL MDAUrl = new URL(url);
							 HttpURLConnection conn =(HttpURLConnection) MDAUrl.openConnection();
							 conn.setConnectTimeout(60000); //set timeout to 1 min
							 conn.setReadTimeout(60000); 
							    conn.setRequestMethod("GET"); 
							    conn.setRequestProperty("Accept", "application/json");
								  if (conn.getResponseCode() != 200  && conn.getResponseCode() != 204) {
									  throw new RuntimeException("Failed : HTTP error code : " +conn.getResponseCode()); 
									  }
								  BufferedReader br = new BufferedReader(new  InputStreamReader((conn.getInputStream())));
								  
								  System.out.println("MDA LLAlertSummaryService Output from Server .... \n");
								  while ((MDAoutput = br.readLine()) != null) { 
									  //System.out.println("MDA AlertSummary json output "+MDAoutput); 
									  iLogger.info("MDA LLAlertSummaryService json output "+MDAoutput);
									  MDAresult =MDAoutput; } 
								  MDAOutputMap = new Gson().fromJson(MDAresult, new TypeToken<HashMap<String, Integer>>(){}.getType());
								  response.setRedThresholdValue(MDAOutputMap.get("RED"));
								  response.setYellowThresholdValue(MDAOutputMap.get("YELLOW"));
								  //System.out.println("MDAResponse LLAlertSummaryService outmap : "+MDAOutputMap);
								  iLogger.info("MDAResponse LLAlertSummaryService outmap : "+MDAOutputMap);
								  conn.disconnect();
				}catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("Error occured while connecting to Mongo DB "+e.getMessage());
				}
					
				}} catch (Exception e) {
					e.printStackTrace();
					fLogger.fatal("Exception :" + e.getMessage());
				}

				finally {
					if (rs != null)
						try {
							rs.close();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}

					if (statement != null)
						try {
							statement.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					if (prodConnection != null) {
						try {
							prodConnection.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}

					if (session != null && session.isOpen()) {
						session.close();
					}

				}

				return response;

			}


			public ServiceDueOverDueRespContract getServicedueOverdueDetails(String period, List<Integer> loginTenancyIdList, List<Integer> TenancyIdList, List<Integer> eventTypeIdList, List<String> eventSeverityList, List<Integer> assetTypeIdList, List<Integer> assetGroupIdList, boolean ownStock, boolean activeAlerts, List<Integer> customAssetGroupIdList){
				ServiceDueOverDueRespContract response = new ServiceDueOverDueRespContract();	


				Logger fLogger = FatalLoggerClass.logger;
				Logger iLogger = InfoLoggerClass.logger;

				String MDAassetTypeCodeList=null;
				String MDAassetGroupCodeList=null;
				String MDAeventTypeCodeList=null;
				String MDAeventSeverityList=null;
				String MDAaccountIdListAsString=null;
				String MDAoutput=null;
				String MDAresult=null;
				Connection prodConnection = null;
				Statement statement = null;
				ResultSet rs = null;

				String basicQueryString=null;
				String fromQuery=null;
				String whereQuery=null;
				String whereQuery1=null;
				String groupByQuery=null;

				StringBuilder tenancyListAsString=null;
				StringBuilder eventTypeIdListAsString=null;
				StringBuilder eventSeverityListAsString=null;
				StringBuilder alertCodeListAsString=null;


				StringBuilder assetTypeIdListAsString = null;
				StringBuilder assetGroupIdListAsString = null;

				String finalQuery=null;

				ListToStringConversion utilClass=new ListToStringConversion();

				//CR337.sn
				String connIP=null;
				String connPort=null;
				Properties prop = null;
				try{
					prop = CommonUtil.getDepEnvProperties();
					connIP = prop.getProperty("MDA_ServerIP");
					connPort = prop.getProperty("MDA_ServerPort");
					iLogger.info("EventDetailsBO : MDA_ServerIP:" +connIP +" :: MDA_ServerPort:"+connPort);
				}catch(Exception e){
					fLogger.fatal("EventDetailsBO : getServicedueOverdueDetails : " +
							"Exception in getting Server Details for MDA Layer from properties file: " +e);
				}
				//CR337.en

				if(TenancyIdList!=null && TenancyIdList.size()>0){

					tenancyListAsString=utilClass.getIntegerListString(TenancyIdList);

				}
				else{
					tenancyListAsString=utilClass.getIntegerListString(loginTenancyIdList);
				}


				if(eventSeverityList!=null && eventSeverityList.size()>0){

					eventSeverityListAsString=	utilClass.getStringList(eventSeverityList);
				}

				if(assetTypeIdList!=null && assetTypeIdList.size()>0){

					assetTypeIdListAsString=	utilClass.getIntegerListString(assetTypeIdList);
				}

				if(assetGroupIdList!=null && assetGroupIdList.size()>0){

					assetGroupIdListAsString=	utilClass.getIntegerListString(assetGroupIdList);
				}

				Session session = HibernateUtil.getSessionFactory().openSession();

				try{
					if(customAssetGroupIdList==null || (customAssetGroupIdList != null && customAssetGroupIdList.isEmpty()))
					{
					List<Integer> AccountIdList=new ArrayList<Integer>();

					List<String> AccountCodeList=new ArrayList<String>();

					AccountEntity account=null;


					Query accountQ = session.createQuery(" select at.account_id from AccountTenancyMapping at where at.tenancy_id in("+tenancyListAsString+") ");
					Iterator accItr = accountQ.list().iterator();
					while(accItr.hasNext())
					{
						account = (AccountEntity)accItr.next();
						AccountIdList.add(account.getAccount_id());

						//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

						//AccountCodeList.add(account.getAccountCode());
						AccountCodeList.add(account.getMappingCode());
					}

					if(session!=null && session.isOpen())
					{
						session.close();
					}

					StringBuilder AccountIdListAsString=utilClass.getIntegerListString(AccountIdList);
					StringBuilder AccountCodelistAsString=utilClass.getStringList(AccountCodeList);
					StringBuilder MDAaccountIdListAsStringBuilder=utilClass.getStringWithoutQuoteList(AccountCodeList);
					MDAaccountIdListAsString=utilClass.removeLastComma(MDAaccountIdListAsStringBuilder.toString());
					//DF20170130 @Roopa for Role based alert implementation
					DateUtil utilObj=new DateUtil();

					List<String> alertCodeList= utilObj.roleAlertMapDetails(null,loginTenancyIdList.get(0), "Display");

					if(alertCodeList!=null && alertCodeList.size()>0)
						alertCodeListAsString=utilClass.getStringList(alertCodeList);


					if(period==null){



						basicQueryString="select ae.Event_Severity, count(*) as alertCount"; 
						fromQuery=" from asset_event ae, asset_owner_snapshot aos ";
						whereQuery=" where aos.Serial_Number=ae.Serial_Number and aos.account_ID in ("+AccountIdListAsString+") and ae.Event_Type_ID =1  ";
						if(activeAlerts==true){
							whereQuery=whereQuery+" and ae.Active_Status=1 and aee.PartitionKey =1 ";
						}
						else{
							whereQuery=whereQuery+" and ae.Active_Status=0 ";
						}

						groupByQuery=" group by ae.Event_Severity";

						/*if(alertCodeListAsString!=null){
							fromQuery=fromQuery+", business_event be where Event_Type_ID = '1' ";
							whereQuery=whereQuery+" and ae.Event_ID=be.Event_ID and be.Alert_Code in("+alertCodeListAsString+")";

						}*/



						if(ownStock==true){
							fromQuery=fromQuery+", asset a ";
							whereQuery=whereQuery +" and aos.Serial_Number=a.Serial_Number and aos.account_ID=a.Primary_Owner_ID ";
						}

						if(eventSeverityListAsString!=null){

							whereQuery=whereQuery +" and ae.Event_Severity in ("+eventSeverityListAsString+") ";
						}



						if ((!(assetGroupIdList == null || assetGroupIdList.isEmpty()))
								|| (!((assetTypeIdList == null) || (assetTypeIdList
										.isEmpty())))) {
							//Df20170606 @Roopa pointing to right table

							fromQuery = fromQuery + " , remote_monitoring_fact_data_dayagg_json_new g, asset_class_dimension a ";
							whereQuery = whereQuery
							+ " and g.Asset_Class_ID=a.Asset_Class_Dimension_ID";
						}
						if (!((assetGroupIdList == null) || (assetGroupIdList.isEmpty()))) {
							whereQuery = whereQuery + " and a.Asset_Group_ID in ( "
							+ assetGroupIdListAsString + " )";
						}
						if (!((assetTypeIdList == null) || (assetTypeIdList.isEmpty()))) {
							whereQuery = whereQuery + " and a.Asset_Type_ID in ( "
							+ assetTypeIdListAsString + " )";
						}


						finalQuery=basicQueryString+fromQuery+whereQuery+groupByQuery;



					//	System.out.println("finalQuery ::"+finalQuery);

						ConnectMySQL connMySql = new ConnectMySQL();
						prodConnection = connMySql.getConnection();
						statement = prodConnection.createStatement();
						rs = statement.executeQuery(finalQuery);

						int dueCount=0;

						int overdueCount=0;



						while(rs.next()){

							String severity=rs.getString("Event_Severity");

							int count = rs.getInt("alertCount");
							System.out.println("Severity: "+severity);
							System.out.println("Count: "+count);



							if(severity.equalsIgnoreCase("RED")){

								overdueCount = overdueCount + count;
							}

							//total alert yellow count

							if(severity.equalsIgnoreCase("YELLOW")){

								dueCount = dueCount + count;
							}

						}

						response.setServiceDueCount(dueCount);
						response.setServiceOverDueCount(overdueCount);
					}
					else{

						if(period.equalsIgnoreCase("Year"))
						{
						basicQueryString="select ai.AlertSeverity, count(*) as alertCount"; 
						fromQuery=" from alertInsight_detail ai ";

						whereQuery=" where (ai.ZonalCode in ("+AccountCodelistAsString+") or ai.DealerCode in("+AccountCodelistAsString+") or ai.CustCode in("+AccountCodelistAsString+")) ";

						groupByQuery=" group by ai.AlertSeverity";

						if(alertCodeListAsString!=null){

							List<Integer>eventIdList=utilObj.getEventIdListForAlertCodes(alertCodeListAsString);

							StringBuilder EventIdListAsString=utilClass.getIntegerListString(eventIdList);

							whereQuery= whereQuery+" and ai.AlertCode in("+EventIdListAsString+")";

						}

						int dateAsNum=0;
						int calYear = 0;
						String Timeperiod=null;
						Calendar cal = Calendar.getInstance();

						if(period.equalsIgnoreCase("Week")){
							Timeperiod="Week";
							calYear=cal.get(Calendar.YEAR);
							dateAsNum = cal.get(Calendar.WEEK_OF_YEAR);
						}
						else if(period.equalsIgnoreCase("Last Week")){
							Timeperiod="Week";
							calYear=cal.get(Calendar.YEAR);
							cal.set(Calendar.WEEK_OF_YEAR, cal.get(Calendar.WEEK_OF_YEAR)-1);
							dateAsNum = cal.get(Calendar.WEEK_OF_YEAR);
						}

						else if(period.equalsIgnoreCase("Month")){
							Timeperiod="Month";
							calYear=cal.get(Calendar.YEAR);
							dateAsNum = cal.get(Calendar.MONTH);
						}
						else if(period.equalsIgnoreCase("Last Month")){
							Timeperiod="Month";
							calYear=cal.get(Calendar.YEAR);
							cal.set(Calendar.MONTH,cal.get(Calendar.MONTH)-1);
							dateAsNum = cal.get(Calendar.MONTH);
						}

						else if(period.equalsIgnoreCase("Quarter")){
							Timeperiod="Quarter";
							calYear=cal.get(Calendar.YEAR);
							dateAsNum = ((cal.get(Calendar.MONTH))) / 3 + 1;
						}

						else if(period.equalsIgnoreCase("Last Quarter")){
							Timeperiod="Quarter";
							calYear=cal.get(Calendar.YEAR);
							cal.set((cal.get(Calendar.MONTH)), -1);
							dateAsNum = ((cal.get(Calendar.MONTH))) / 3 + 1;
						}
						else if(period.equalsIgnoreCase("Year")){
							Timeperiod="Year";
							dateAsNum=cal.get(Calendar.YEAR);  
						}
						else if(period.equalsIgnoreCase("Last Year")){
							Timeperiod="Year";
							cal.set((cal.get(Calendar.YEAR)), -1);
							dateAsNum=cal.get(Calendar.YEAR);  
						}

						if(dateAsNum!=0){

							if(Timeperiod!=null){
								if(Timeperiod.equalsIgnoreCase("Week")){

									whereQuery=whereQuery +" and ai.TxnWeek="+dateAsNum+" and ai.TxnYear="+calYear ;
								}
								if(Timeperiod.equalsIgnoreCase("Month")){

									whereQuery=whereQuery +" and ai.TxnMonth="+dateAsNum+" and ai.TxnYear="+calYear ;
								}
								if(Timeperiod.equalsIgnoreCase("Quarter")){

									whereQuery=whereQuery +" and ai.TxnQuarter="+dateAsNum+" and ai.TxnYear="+calYear ;
								}
								if(Timeperiod.equalsIgnoreCase("Year")){

									whereQuery=whereQuery +" and ai.TxnYear="+dateAsNum+"";	
								}
							}

						}

						ListToStringConversion conversionObj = new ListToStringConversion();
						DateUtil utl=new DateUtil();
						if (assetTypeIdListAsString != null) {
							StringBuilder assetTypeCodeListAsString =null;


							List<String> assetTypeCodeList=utl.getAssetTypeIdList(assetTypeIdListAsString);
							if(assetTypeCodeList!=null && assetTypeCodeList.size()>0)						
								assetTypeCodeListAsString=conversionObj.getStringList(assetTypeCodeList);
							whereQuery = whereQuery + " and ai.ModelCode in ("
							+ assetTypeCodeListAsString + ") ";
						}

						if (assetGroupIdListAsString != null) {
							List<String> assetGroupCodeList=utl.getAssetGroupIdList(assetGroupIdListAsString);
							StringBuilder assetGroupCodeListAsString=null;
							if(assetGroupCodeList!=null && assetGroupCodeList.size()>0)						
								assetGroupCodeListAsString=conversionObj.getStringList(assetGroupCodeList);
							whereQuery = whereQuery + " and ai.ProfileCode in ("
							+ assetGroupCodeListAsString + ") ";

						}



						if(eventTypeIdListAsString!=null){

							whereQuery=whereQuery +" and ai.AlertTypeCode in ("+eventTypeIdListAsString+") ";
						}

						if(eventSeverityListAsString!=null){
							whereQuery=whereQuery +" and ai.AlertSeverity in ("+eventSeverityListAsString+") ";
						}


						finalQuery=basicQueryString+fromQuery+whereQuery+groupByQuery;


						System.out.println("finalQuery ::"+finalQuery);
						System.out.println("Came to else part");

						ConnectMySQL connMySql = new ConnectMySQL();
						prodConnection = connMySql.getProdDb2Connection();
						statement = prodConnection.createStatement();
						rs = statement.executeQuery(finalQuery);
						System.out.println("Came after execute of final ");

						int dueCount=0;

						int overdueCount=0;

						while(rs.next()){

							String severity=rs.getString("AlertSeverity");

							int count = rs.getInt("alertCount");
							System.out.println("Severity: "+severity);
							System.out.println("Count: "+count);


							if(severity.equalsIgnoreCase("RED")){

								overdueCount = overdueCount + count;
							}

							//total alert yellow count

							if(severity.equalsIgnoreCase("YELLOW")){

								dueCount = dueCount + count;
							}

						}

						response.setServiceDueCount(dueCount);
						response.setServiceOverDueCount(overdueCount);


					}
						else
						{

							//MOOL DB changes
							Connection prodConn = null;
							Statement stmnt = null;
							ResultSet res = null;
							try{
							ConnectMySQL connSql = new ConnectMySQL();
							prodConn = connSql.getConnection();
							stmnt = prodConn.createStatement();
							HashMap<String,Integer> MDAOutputMap = null;
							DateUtil dateUtil1 = new DateUtil();
							ListToStringConversion conversionObj = new ListToStringConversion();
							//For accountFilter filling
							if(AccountIdList!=null && AccountIdList.size()>0){
									String acntTncyQuery = "select Tenancy_Type_Name from tenancy_type where Tenancy_Type_ID in (select Tenancy_Type_ID from tenancy where Tenancy_ID in (select Tenancy_ID from account_tenancy where Account_ID="
											+ AccountIdList.get(0) + "))";
							res=stmnt.executeQuery(acntTncyQuery);
							String tenancy_type_name="";
							if(res.next())
							{
								tenancy_type_name=res.getString("Tenancy_Type_Name");
							}
							String accountFilter="";
							if(tenancy_type_name!=null && !tenancy_type_name.equalsIgnoreCase(""))
							{
								if(tenancy_type_name.equalsIgnoreCase("Global"))
								{
									accountFilter=null;
								}
								else if(tenancy_type_name.equalsIgnoreCase("Regional"))
								{
									accountFilter="RegionCode";
								}
								else if(tenancy_type_name.equalsIgnoreCase("Zonal"))
								{
									accountFilter="ZonalCode";
								}
								else if(tenancy_type_name.equalsIgnoreCase("Dealer"))
								{
									accountFilter="DealerCode";
								}
								else if(tenancy_type_name.equalsIgnoreCase("Customer"))
								{
									accountFilter="CustCode";
								}
								else
								{
									accountFilter="";
								}
								int MDAstock=0;
								if(ownStock)
								{
									MDAstock=1;
								}
								if (assetTypeIdListAsString != null) {
									StringBuilder assetTypeCodeListAsString = null;

									List<String> assetTypeCodeList = dateUtil1
											.getAssetTypeIdList(assetTypeIdListAsString);
									//System.out.println("assetTypeCodeList "+assetTypeCodeList);
									if (assetTypeCodeList != null
											&& assetTypeCodeList.size() > 0){
										assetTypeCodeListAsString = conversionObj
												.getStringList(assetTypeCodeList);
										StringBuilder MDAassetTypeCodeListBuilder=conversionObj.getStringWithoutQuoteList(assetTypeCodeList);
										MDAassetTypeCodeList=MDAassetTypeCodeListBuilder.toString();
										MDAassetTypeCodeList=ListToStringConversion.removeLastComma(MDAassetTypeCodeList);
										//System.out.println("MDAassetTypeCodeList "+MDAassetTypeCodeList);
									}
									/*whereQuery = whereQuery + " and fi.ModelCode in ("
											+ assetTypeCodeListAsString + ") ";*/
								}

								if (assetGroupIdListAsString != null) {
									List<String> assetGroupCodeList = dateUtil1
											.getAssetGroupIdList(assetGroupIdListAsString);
									//System.out.println("assetGroupCodeList "+assetGroupCodeList);
									StringBuilder assetGroupCodeListAsString = null;
									if (assetGroupCodeList != null
											&& assetGroupCodeList.size() > 0){
										assetGroupCodeListAsString = conversionObj
												.getStringList(assetGroupCodeList);
										StringBuilder MDAassetGroupCodeListBuilder=conversionObj.getStringWithoutQuoteList(assetGroupCodeList);
										MDAassetGroupCodeList=MDAassetGroupCodeListBuilder.toString();
										MDAassetGroupCodeList=ListToStringConversion.removeLastComma(MDAassetGroupCodeList);
										//System.out.println("MDAassetGroupCodeList "+MDAassetGroupCodeList);
										}
								
									/*whereQuery = whereQuery + " and fi.ProfileCode in ("
											+ assetGroupCodeListAsString + ") ";*/

								}
								if(eventSeverityList != null && eventSeverityList.size() > 0)
								{	
									StringBuilder MDAeventSeverityListBuilder=conversionObj.getStringWithoutQuoteList(eventSeverityList);
									MDAeventSeverityList=MDAeventSeverityListBuilder.toString();
									MDAeventSeverityList=ListToStringConversion.removeLastComma(MDAeventSeverityList);
								}
								AlertSummaryRespContract MDAResponse=new AlertSummaryRespContract();
								ObjectMapper mapper = new ObjectMapper();
								

								try{
											String url=
													// Dhiraj K : 20220630 : Change IP for AWS server
													//"http://10.179.12.25:26030/MoolDAReports/LLAlertSummaryService/getLLServiceDueOverDueSummary?accountFilter="
													//"http://10.210.196.206:26030/MoolDAReports/LLAlertSummaryService/getLLServiceDueOverDueSummary?accountFilter="//CR337.o
													"http://"+connIP+":"+connPort+"/MoolDAReports/LLAlertSummaryService/getLLServiceDueOverDueSummary?accountFilter="//CR337.n
															+ accountFilter
															+ "&accountIDList="
															+ MDAaccountIdListAsString
															+ "&period="
															+ period
															+ "&modelCodeList="
															+ MDAassetTypeCodeList
															+ "&profileCodeList="
															+ MDAassetGroupCodeList
															+"&alertSeverity="
															+MDAeventSeverityList
															+ "&isOwnStock="
															+ MDAstock
															+ "&loginID=null"
															+ "&countryCode=null";
											System.out.println("MDA ServiceDueOverdue Service  URL : "+url);
											iLogger.info("MDA ServiceDueOverdue Service URL : "+url);
											URL MDAUrl = new URL(url);
											 HttpURLConnection conn =(HttpURLConnection) MDAUrl.openConnection();
											    conn.setRequestMethod("GET"); 
											    conn.setRequestProperty("Accept", "application/json");
												  if (conn.getResponseCode() != 200  && conn.getResponseCode() != 204) {
													  iLogger.info("MDAReports report status: FAILURE for ServiceDueOverdue Service Report ::Response Code:"+conn.getResponseCode());
													  throw new RuntimeException("Failed : HTTP error code : " +conn.getResponseCode()); 
													  }
												  iLogger.info("MDAReports report status: SUCCESS for ServiceDueOverdue Service Report ::Response Code:"+conn.getResponseCode());
												  BufferedReader br = new BufferedReader(new  InputStreamReader((conn.getInputStream())));
												  
												  System.out.println("MDA ServiceDueOverdue Service Output from Server .... \n");
												  while ((MDAoutput = br.readLine()) != null) { 
													  //System.out.println("MDA ServiceDueOverdue Service json output "+MDAoutput); 
													  iLogger.info("MDA ServiceDueOverdue Service json output "+MDAoutput);
													  MDAresult =MDAoutput; } 
												  MDAOutputMap = new Gson().fromJson(MDAresult, new TypeToken<HashMap<String, Integer>>(){}.getType());
												  response.setServiceOverDueCount(MDAOutputMap.get("RED"));
												  response.setServiceDueCount(MDAOutputMap.get("YELLOW"));
												  //System.out.println("MDAResponse ServiceDueOverdue Service outmap : "+MDAOutputMap);
												  iLogger.info("MDAResponseServiceDueOverdue Service outmap : "+MDAOutputMap);
												  conn.disconnect();
								}catch(Exception e)
								{
									e.printStackTrace();
									fLogger.fatal("Error occured while connecting to Mongo DB "+e.getMessage());
								}
								
								}
							
							
							}
							else{
								fLogger.fatal("AccountIdList is empty");
								throw new CustomFault("AccountCodeList is empty");
							}
								
						}catch(Exception e)
						{
							fLogger.fatal("Exception occured "+e.getMessage());
						}
							finally {
								if (res != null)
									try {
										res.close();
									} catch (SQLException e1) {
										e1.printStackTrace();
									}

								if (stmnt != null)
									try {
										stmnt.close();
									} catch (SQLException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

								if (prodConn != null) {
									try {
										prodConn.close();
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}

							}
						
						
						
						}
					}//end of else
				  }//end of main if
					else{
						String customAssetGroupIdListAsString=null;
						customAssetGroupIdListAsString=AssetUtil.getIDListAsCommaSeperated(customAssetGroupIdList);
						HashMap<String,Integer> MDAOutputMap = null;
						DateUtil dateUtil1 = new DateUtil();
						ListToStringConversion conversionObj = new ListToStringConversion();
						try{
							String accountFilter = null;
							
							int MDAstock=1;
							if(ownStock==false)
								MDAstock=0;
							if (assetTypeIdListAsString != null) {
								StringBuilder assetTypeCodeListAsString = null;

								List<String> assetTypeCodeList = dateUtil1
										.getAssetTypeIdList(assetTypeIdListAsString);
								if (assetTypeCodeList != null
										&& assetTypeCodeList.size() > 0){
									assetTypeCodeListAsString = conversionObj
											.getStringList(assetTypeCodeList);
									StringBuilder MDAassetTypeCodeListBuilder=conversionObj.getStringWithoutQuoteList(assetTypeCodeList);
									MDAassetTypeCodeList=MDAassetTypeCodeListBuilder.toString();
									MDAassetTypeCodeList=ListToStringConversion.removeLastComma(MDAassetTypeCodeList);
								}
								/*whereQuery = whereQuery + " and fi.ModelCode in ("
										+ assetTypeCodeListAsString + ") ";*/
							}

							if (assetGroupIdListAsString != null) {
								List<String> assetGroupCodeList = dateUtil1
										.getAssetGroupIdList(assetGroupIdListAsString);
								StringBuilder assetGroupCodeListAsString = null;
								if (assetGroupCodeList != null
										&& assetGroupCodeList.size() > 0){
									assetGroupCodeListAsString = conversionObj
											.getStringList(assetGroupCodeList);
									StringBuilder MDAassetGroupCodeListBuilder=conversionObj.getStringWithoutQuoteList(assetGroupCodeList);
									MDAassetGroupCodeList=MDAassetGroupCodeListBuilder.toString();
									MDAassetGroupCodeList=ListToStringConversion.removeLastComma(MDAassetGroupCodeList);
									}
							
								/*whereQuery = whereQuery + " and fi.ProfileCode in ("
										+ assetGroupCodeListAsString + ") ";*/

							}
							if(eventSeverityList != null && eventSeverityList.size() > 0)
							{	
								StringBuilder MDAeventSeverityListBuilder=conversionObj.getStringWithoutQuoteList(eventSeverityList);
								MDAeventSeverityList=MDAeventSeverityListBuilder.toString();
								MDAeventSeverityList=ListToStringConversion.removeLastComma(MDAeventSeverityList);
							}
							String url=
									"http://localhost:26030/MoolDAReports/LLAlertSummaryService/getLLServiceDueOverDueSummaryMachineGroup?accountFilter="
											+ accountFilter
											+ "&accountIDList="
											+ MDAaccountIdListAsString
											+ "&period=week"
											+ "&modelCodeList="
											+ MDAassetTypeCodeList
											+ "&profileCodeList="
											+ MDAassetGroupCodeList
											+"&alertSeverity="
											+MDAeventSeverityList
											+ "&isOwnStock="
											+ MDAstock
											+ "&loginID=null"
											+ "&countryCode=null"
											+"&MachineGrpIDList="
											+customAssetGroupIdListAsString;
							System.out.println("MDA ServiceDueOverdue Service  URL : "+url);
							iLogger.info("MDA ServiceDueOverdue Service URL : "+url);
							URL MDAUrl = new URL(url);
							 HttpURLConnection conn =(HttpURLConnection) MDAUrl.openConnection();
							 conn.setConnectTimeout(60000); //set timeout to 1 min
							 conn.setReadTimeout(60000); 
							    conn.setRequestMethod("GET"); 
							    conn.setRequestProperty("Accept", "application/json");
								  if (conn.getResponseCode() != 200  && conn.getResponseCode() != 204) {
									  throw new RuntimeException("Failed : HTTP error code : " +conn.getResponseCode()); 
									  }
								  BufferedReader br = new BufferedReader(new  InputStreamReader((conn.getInputStream())));
								  
								  System.out.println("MDA ServiceDueOverdue Service Output from Server .... \n");
								  while ((MDAoutput = br.readLine()) != null) { 
									  //System.out.println("MDA ServiceDueOverdue Service json output "+MDAoutput); 
									  iLogger.info("MDA ServiceDueOverdue Service json output "+MDAoutput);
									  MDAresult =MDAoutput; } 
								  MDAOutputMap = new Gson().fromJson(MDAresult, new TypeToken<HashMap<String, Integer>>(){}.getType());
								  response.setServiceOverDueCount(MDAOutputMap.get("RED"));
								  response.setServiceDueCount(MDAOutputMap.get("YELLOW"));
								  //System.out.println("MDAResponse ServiceDueOverdue Service outmap : "+MDAOutputMap);
								  iLogger.info("MDAResponseServiceDueOverdue Service outmap : "+MDAOutputMap);
								  conn.disconnect();
				}catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("Error occured while connecting to Mongo DB "+e.getMessage());
				}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("Exception :"+e.getMessage());
				}

				finally
				{
					if(rs!=null)
						try {
							rs.close();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}

						if(statement!=null)
							try {
								statement.close();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							if (prodConnection != null) {
								try {
									prodConnection.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}

							if(session!=null && session.isOpen())
							{
								session.close();
							}

				}



				return response;

			}

	//CR308.sn
	/**This method will return event service name of input asset evenyt id
	 * @param assetEventId
	 * @return String serviceName
	 */
	public String getEventsServiceName(int assetEventId){

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		ConnectMySQL connMySql = new ConnectMySQL();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String serviceName="NA";
		try{
			conn = connMySql.getConnection();
			stmt = conn.createStatement();
			iLogger.info("ServiceDetailsBO:getServiceCloserDetailsFromMysql::"+assetEventId+" START");
			String query="select * from asset_event ae,service_schedule ss where ae.Event_Type_ID=1 and ae.Asset_Event_ID='"+assetEventId+"' and ae.Service_Schedule_ID=ss.serviceScheduleId";
			rs = stmt.executeQuery(query);

			while(rs.next()){
				serviceName = (String)rs.getObject("serviceName");
			}
			iLogger.info("ServiceDetailsBO:getServiceCloserDetailsFromMysql::"+assetEventId+" END");
		}
		catch(Exception e){
			fLogger.fatal("ServiceDetailsBO:getServiceCloserDetailsFromMysql::"+assetEventId+":Exception"+e.getMessage());
			return serviceName;
		}
		finally{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if(stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}		
		return serviceName;
	}
	//CR308.en
}

//DF20181109 :: MA369757 :: To sort the useralerts based on timestamp
class Compare implements Comparator<UserAlertsImpl>
{
//	public static WiseLogger infoLogger = WiseLogger.getLogger("AssetDiagnosticImpl:","info");
	
	 Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalError = FatalLoggerClass.logger;
		Logger businessError = BusinessErrorLoggerClass.logger;
	@Override
	public int compare(UserAlertsImpl arg0, UserAlertsImpl arg1) 
	{
		// TODO Auto-generated method stub
		long t1=0;
		long t2=0;
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = format.parse(arg0.getLatestReceivedTime());
			Date date2 = format.parse(arg1.getLatestReceivedTime());
			t1 = date1.getTime();
			t2 = date2.getTime();
			
		}
		
		catch(ParseException e)
		{
			infoLogger.info(e);
		}
		return (int) (t2 - t1);
	}
	
	
}



