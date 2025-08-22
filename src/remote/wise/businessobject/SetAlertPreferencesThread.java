package remote.wise.businessobject;

/*
 * CR500 : 20241128 : Dhiraj Kumar : WHatsApp Integration with LL
 */
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.EventEntity;
import remote.wise.businessentity.EventTypeEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.UserAlertPreferenceRespContract;
import remote.wise.service.implementation.UserAlertPreferenceImpl;
import remote.wise.util.HibernateUtil;
//DF20181102 :: MA369757 :: setting the alert preferences asynchronously after user creation [Timeout Issue in settings->add user]
public class SetAlertPreferencesThread implements Runnable{
	UserAlertPreferenceRespContract response= new UserAlertPreferenceRespContract();
	String rolename,LoginId,role_name;
	String customerCareRoleName=null;
	String jcbAdminRoleName=null;
	String dealerAdminRoleName=null;
	String status=null;
	SetAlertPreferencesThread(String roleName,String LoginId,String role_name)
	{
		//roleName :: from entity
		this.rolename=roleName;
		this.LoginId=LoginId;
		//role_name :: input to service
		this.role_name=role_name;
	}
	@Override
	public void run() 
	{
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		infoLogger.info("SetAlertPreferencesThread ::"+this.LoginId+": In");
		Session session = null;
		try{
		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fLogger.fatal("SetAlertPreferencesThread ::"+this.LoginId+": FATAL ERROR: Exception in reading the properties file. Hence not setting preference" +
					" "+e.getMessage(),e);
			e.printStackTrace();
			return;
		}
		customerCareRoleName= prop.getProperty("CustomerCare");
		jcbAdminRoleName= prop.getProperty("OEMadmin");
		dealerAdminRoleName= prop.getProperty("DealerAdmin");
		List<UserAlertPreferenceRespContract> reqContractObj = new LinkedList<UserAlertPreferenceRespContract>();
		session = HibernateUtil.getSessionFactory().openSession();
		if( rolename.equalsIgnoreCase(customerCareRoleName) || (rolename.equalsIgnoreCase(jcbAdminRoleName)))
		{
			//infoLogger.info("SetAlertPreferencesThread :: In "+rolename);
			 long prefStarttime=System.currentTimeMillis();
			Query eventListQ = session.createQuery(" from EventEntity ");
			Iterator eventListItr = eventListQ.list().iterator();
			while(eventListItr.hasNext())
			{
				EventEntity event = (EventEntity) eventListItr.next();
				UserAlertPreferenceRespContract response= new UserAlertPreferenceRespContract();
				response.setEmailEvent(true);
				response.setEventId(event.getEventId());
				response.setEventName(event.getEventName());
				response.setEventTypeId(event.getEventTypeId().getEventTypeId());
				response.setEventTypeName(event.getEventTypeId().getEventTypeName());
				response.setLoginId(LoginId);
				response.setRoleName(role_name);
				response.setSMSEvent(true);
				response.setWhatsappEvent(true);//CR500.n

				reqContractObj.add(response);
			}
			UserAlertPreferenceImpl implObj = new UserAlertPreferenceImpl();
			status=implObj.setAdminAlertPreference(reqContractObj);
			 long prefendtime=System.currentTimeMillis();
			 infoLogger.info("SetAlertPreferencesThread ::"+this.LoginId+":Set Admin Alert Preference:" +
			 		"Execution Time :"+(prefendtime-prefStarttime));
		}
		else if( (rolename.equalsIgnoreCase(dealerAdminRoleName)))
		{
			//infoLogger.info("SetAlertPreferencesThread :: In "+rolename);
			long DprefStarttime=System.currentTimeMillis();
			Query eventListQ = session.createQuery("from EventEntity where eventTypeId =2");
			List<Integer> eventIds = Arrays.asList(4, 5, 6, 16, 17);
			Iterator eventListItr = eventListQ.list().iterator();
			while(eventListItr.hasNext())
			{
				EventEntity event = (EventEntity) eventListItr.next();	
				int eventId = event.getEventId();
				if(!eventIds.contains(eventId)){
					UserAlertPreferenceRespContract response= new UserAlertPreferenceRespContract();
					response.setEmailEvent(true);
					response.setEventId(event.getEventId());
					response.setEventName(event.getEventName());
					response.setEventTypeId(event.getEventTypeId().getEventTypeId());
					response.setEventTypeName(event.getEventTypeId().getEventTypeName());
					response.setLoginId(LoginId);
					response.setRoleName(role_name);
					response.setSMSEvent(true);
					response.setWhatsappEvent(true);//CR500.n
					reqContractObj.add(response);
				}

			}
			UserAlertPreferenceImpl implObj = new UserAlertPreferenceImpl();
			status=implObj.setDealerAdminAlertPreference(reqContractObj);
			long Dprefendtime=System.currentTimeMillis();
			//infoLogger.info("SetAlertPreferencesThread :: Execution Time :: Set Dealer Admin Alert Preference for Alerts :"+LoginId+" :: "+(Dprefendtime-DprefStarttime));
			infoLogger.info("SetAlertPreferencesThread ::"+this.LoginId+":Set Dealer admin Alert Preference:" +
			 		"Execution Time :"+(Dprefendtime-DprefStarttime));
		}
		else
		{
			
			//infoLogger.info("SetAlertPreferencesThread :: In else");
			long UprefStarttime=System.currentTimeMillis();
			Query eventTypeListQ = session.createQuery(" from EventTypeEntity ");
			Iterator eventTypeListItr = eventTypeListQ.list().iterator();
			while(eventTypeListItr.hasNext())
			{
				EventTypeEntity eventType = (EventTypeEntity) eventTypeListItr.next();
				UserAlertPreferenceRespContract response= new UserAlertPreferenceRespContract();
				response.setEmailEvent(true);
				response.setEventTypeId(eventType.getEventTypeId());
				response.setEventTypeName(eventType.getEventTypeName());
				response.setLoginId(LoginId);
				response.setRoleName(role_name);
				response.setSMSEvent(true);
				response.setWhatsappEvent(true);//CR500.n

				reqContractObj.add(response);
			}
			//infoLogger.info("Set User Alert Preference for Alerts :"+LoginId);
			UserAlertPreferenceImpl implObj = new UserAlertPreferenceImpl();
			status=implObj.setUserAlertPreference(reqContractObj);
			long Uprefendtime=System.currentTimeMillis();
			//infoLogger.info("SetAlertPreferencesThread :: Execution Time :: Set User Alert Preference for Alerts :"+LoginId+" :: "+(Uprefendtime-UprefStarttime));
			infoLogger.info("SetAlertPreferencesThread ::"+this.LoginId+":Set User Alert Preference:" +
			 		"Execution Time :"+(Uprefendtime-UprefStarttime));
		}
		long endTime = System.currentTimeMillis();
		infoLogger.info("SetAlertPreferencesThread ::"+this.LoginId+":Set Alert Preference:status "+status+" : Total Execution Time:"+(endTime-startTime));
		}
		catch(Exception ex)
		{
			fLogger.info("SetAlertPreferencesThread ::"+this.LoginId+":Exception occured. "+ex.getMessage(), ex);
			ex.printStackTrace();
		}
		
		finally
		{
			if (session.isOpen()) {
			    session.flush();
				session.close();
			}
		}

		
	}

}
