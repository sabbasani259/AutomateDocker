package remote.wise.service.implementation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.businessobject.EventDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.EventSubscriptionReqContract;
import remote.wise.service.datacontract.EventSubscriptionRespContract;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;

/** Implementation class to handle SMS subscribers for a given serialNumber
 * @author Rajani Nagaraju
 *
 */
public class EventSubscriptionImpl 
{
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("EventSubscriptionImpl:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("EventSubscriptionImpl:","fatalError");*/
	
	
	String serialNumber;
	String contactId;
	String primaryMobileNumber;
	String primaryEmailId;
	String userName;
	//Defect Id: 902 - Rajani Nagaraju - 20130705 - DealerUser as first PrimarySMS contact.
	boolean isDealerUser;
	
//	Keerthi : Defect ID : 1177 :Receiver 1,2,3
	int priority;
	
	/**
	 * @return serialNumber as String
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber VIN as String input
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	
	/**
	 * @return contactId as String
	 */
	public String getContactId() {
		return contactId;
	}
	/**
	 * @param contactId contactId as String input
	 */
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	
	
	/**
	 * @return primaryMobileNumber of the contact as String
	 */
	public String getPrimaryMobileNumber() {
		return primaryMobileNumber;
	}
	/**
	 * @param primaryMobileNumber contactNumber as String input
	 */
	public void setPrimaryMobileNumber(String primaryMobileNumber) {
		this.primaryMobileNumber = primaryMobileNumber;
	}
	
	
	/**
	 * @return PrimaryEmailId of the contact as String
	 */
	public String getPrimaryEmailId() {
		return primaryEmailId;
	}
	/**
	 * @param primaryEmailId emailId of the contact as String input
	 */
	public void setPrimaryEmailId(String primaryEmailId) {
		this.primaryEmailId = primaryEmailId;
	}
	
	
	/**
	 * @return Name of the contact as String
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName Name of the contact as String input
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	//Defect Id: 902 - Rajani Nagaraju - 20130705 - DealerUser as first PrimarySMS contact.
	/**
	 * @return the isDealerUser
	 */
	public boolean isDealerUser() {
		return isDealerUser;
	}
	/**
	 * @param isDealerUser the isDealerUser to set
	 */
	public void setDealerUser(boolean isDealerUser) {
		this.isDealerUser = isDealerUser;
	}
	
	
	//**************************************** Set event Subscribers **********************************************
	/** This method sets the SMS subscribers for a given SerialNumber
	 * DefectId:950,1015 - Modified by Rajani Nagaraju - Receiver1,2 swapping issue
	 * @param reqObj serialNumber and the list of SMS subscriber contacts
	 * @return Returns the status String
	 * @throws CustomFault
	 */
	public String setEventSubscription(EventSubscriptionReqContract reqObj) throws CustomFault
	{
		String flag= "SUCCESS";
		
		List<ContactEntity> contactEntityList = new LinkedList<ContactEntity>();
		
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
		//Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
	    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	    session.beginTransaction();
		
		try
		{
			//validate the serial number
			if(reqObj.getSerialNumber()==null)
			{
				throw new CustomFault("Provide Serial Number");
			}
			
			DomainServiceImpl domainService = new DomainServiceImpl();
			AssetEntity asset = domainService.getAssetDetails(reqObj.getSerialNumber());
			if(asset==null || asset.getSerial_number()==null)
			{
				bLogger.error("Invalid Serial Number");
				throw new CustomFault("Invalid Serial Number");
			}
			
			if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        session.getTransaction().begin();
            }
			int contactListSize = 0;
			
			if(reqObj.getPrimaryContactList()!=null && !reqObj.getPrimaryContactList().isEmpty())
			{
				contactListSize = reqObj.getPrimaryContactList().size();
				
				if(contactListSize>3)
				{
					bLogger.error("Maximum of three users can be assigned as Primary contact for SMS");
					throw new CustomFault("Maximum of three users can be assigned as Primary contact for SMS");
				}
				
				//DefectId:950,1015 - Modified by Rajani Nagaraju - Receiver1,2 swapping issue
				ListToStringConversion conversion = new ListToStringConversion();
	        	List<String> contactList = new LinkedList<String>();
				for(int i=0; i<reqObj.getPrimaryContactList().size(); i++)
				{
					contactList.add( (reqObj.getPrimaryContactList().get(i).split(","))[0]);
				}
				String primaryContactListAsString = conversion.getStringList(contactList).toString();
				
				Query query = session.createQuery("from ContactEntity where contact_id in ("+primaryContactListAsString+")");
				List resultList = query.list();
				int resultSize = resultList.size();
				if(resultSize!=contactListSize){
					bLogger.error("Primary contact list is invalid !");
					throw new CustomFault("Primary contact list is invalid !");					
				}
				//call the set method in BO
				EventDetailsBO eventDetails = new EventDetailsBO();
				flag = eventDetails.setEventSubscription(asset,reqObj.getPrimaryContactList());
				
			}
			else{
				bLogger.error("Provide Primary contact list !");
				throw new CustomFault("Provide Primary contact list !");			
			}
			
			
			
		}
		catch(CustomFault e)
		{
			flag = "FAILURE";
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		catch(Exception e)
		{
			flag = "FAILURE";
			fLogger.fatal("Exception :"+e);
			e.printStackTrace();
		}
        
        finally
        {
             
              if(session.isOpen())
              {
            	  if(session.getTransaction().isActive())
                  {
                        session.getTransaction().commit();
                  }
                  
                    session.flush();
                    session.close();
              }
              
        }
        
		return flag;
		
	}
	
	//****************************************END of  Set event Subscribers **********************************************
	
	//**************************************** Set event Subscribers **********************************************
	/** This method sets the SMS subscribers for a given SerialNumber
	 * @param reqObj serialNumber and the list of SMS subscriber contacts
	 * @return Returns the status String
	 * @throws CustomFault
	 */
	public String setEventSubscription2(EventSubscriptionReqContract reqObj) throws CustomFault
	{
		String flag= "SUCCESS";
		
		List<ContactEntity> contactEntityList = new LinkedList<ContactEntity>();
		
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
		//Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
	    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	    session.beginTransaction();
		
		try
		{
			//validate the serial number
			if(reqObj.getSerialNumber()==null)
			{
				throw new CustomFault("Provide Serial Number");
			}
			
			DomainServiceImpl domainService = new DomainServiceImpl();
			AssetEntity asset = domainService.getAssetDetails(reqObj.getSerialNumber());
			if(asset==null || asset.getSerial_number()==null)
			{
				throw new CustomFault("Invalid Serial Number");
			}
			
			if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        session.getTransaction().begin();
            }
			
			if(reqObj.getPrimaryContactList()!=null)
			{
				ListToStringConversion conversion = new ListToStringConversion();
	        	String primaryContactListAsString = conversion.getStringList(reqObj.getPrimaryContactList()).toString();
				
				Query query = session.createQuery("from ContactEntity where contact_id in ("+primaryContactListAsString+")");
				Iterator itr = query.list().iterator();
				while(itr.hasNext())
				{
					ContactEntity contact = (ContactEntity)itr.next();
					contactEntityList.add(contact);
				}
			}
			
			//call the set method in BO
			EventDetailsBO eventDetails = new EventDetailsBO();
			flag = eventDetails.setEventSubscription2(asset,contactEntityList);
			
		}
		catch(CustomFault e)
		{
			flag = "FAILURE";
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
			e.printStackTrace();
		}
        
        finally
        {
             
              if(session.isOpen())
              {
            	  if(session.getTransaction().isActive())
                  {
                        session.getTransaction().commit();
                  }
                  
                    session.flush();
                    session.close();
              }
              
        }
        
		return flag;
		
	}
	
	//****************************************END of  Set event Subscribers **********************************************
	
	
	
	//**************************************** Get event Subscribers **********************************************
	public List<EventSubscriptionRespContract> getEventSubscription(EventSubscriptionReqContract reqObj) throws CustomFault
	{
		List<EventSubscriptionRespContract> responseList = new LinkedList<EventSubscriptionRespContract>();
		
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
		//Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
		try
		{
			//validate Serial Number
			if(reqObj.getSerialNumber()==null)
			{
				throw new CustomFault("Provide Serial Number");
			}
			
			DomainServiceImpl domainService = new DomainServiceImpl();
			AssetEntity asset = domainService.getAssetDetails(reqObj.getSerialNumber());
			if(asset==null || asset.getSerial_number()==null)
			{
				throw new CustomFault("Invalid Serial Number");
			}
		
			EventDetailsBO eventDetails = new EventDetailsBO();
			List<EventSubscriptionImpl> eventSubscribersList = eventDetails.getEventSubscribers(reqObj.getSerialNumber());
			
			for(int j=0; j<eventSubscribersList.size(); j++)
			{
				EventSubscriptionRespContract response = new EventSubscriptionRespContract();
				response.setContactId(eventSubscribersList.get(j).getContactId());
				response.setPrimaryEmailId(eventSubscribersList.get(j).getPrimaryEmailId());
				response.setPrimaryMobileNumber(eventSubscribersList.get(j).getPrimaryMobileNumber());
				response.setSerialNumber(eventSubscribersList.get(j).getSerialNumber());
				response.setUserName(eventSubscribersList.get(j).getUserName());
				//Defect Id: 902 - Rajani Nagaraju - 20130705 - DealerUser as first PrimarySMS contact.
				response.setDealerUser(eventSubscribersList.get(j).isDealerUser());
				response.setPriority(eventSubscribersList.get(j).getPriority());
				responseList.add(response);
			}
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
			e.printStackTrace();
		}
        
        return responseList;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
		
	//****************************************END of  Get event Subscribers **********************************************
	
}
