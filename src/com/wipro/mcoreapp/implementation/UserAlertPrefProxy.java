/*
 * CR500 : 20241128 : Dhiraj Kumar : WHatsApp Integration with LL
 */
package com.wipro.mcoreapp.implementation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.EventEntity;
import remote.wise.businessentity.EventTypeEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

public class UserAlertPrefProxy implements Runnable
{
	Thread t;
	String userId, prefLevel;
	HashMap<String,String> smsPrefMap ;
	HashMap<String,String> emailPrefMap;
	HashMap<String,String> whatsappPrefMap;//CR500.n
	
	public UserAlertPrefProxy()
	{
		
	}
	
	//CR500.so
		/*public UserAlertPrefProxy(String userId, String prefLevel, HashMap<String,String> smsPrefMap, HashMap<String,String> emailPrefMap)
		{
			t = new Thread(this, "UserAlertPref-ODB");
			this.userId=userId;
			this.prefLevel=prefLevel;
			this.smsPrefMap=smsPrefMap;
			this.emailPrefMap=emailPrefMap;

			t.start();
		}*/
		//CR500.eo
		//CR500.sn
		public UserAlertPrefProxy(String userId, String prefLevel, HashMap<String,String> smsPrefMap, 
			HashMap<String,String> emailPrefMap, HashMap<String,String> whatsappPrefMap)
		{
			t = new Thread(this, "UserAlertPref-ODB");
			this.userId=userId;
			this.prefLevel=prefLevel;
			this.smsPrefMap=smsPrefMap;
			this.emailPrefMap=emailPrefMap;
			this.whatsappPrefMap=whatsappPrefMap;
			
			t.start();
		}//CR500.en
	
	public void run()
	{
		//setUserAlertPref(this.userId, this.prefLevel, this.smsPrefMap, this.emailPrefMap);//CR500.o
		setUserAlertPref(this.userId, this.prefLevel, this.smsPrefMap, this.emailPrefMap, this.whatsappPrefMap);//CR500.n
	}
	
	//CR500.sn
	private String setUserAlertPref(String userId, String prefLevel, HashMap<String,String> smsPrefMap,
			HashMap<String, String> emailPrefMap, HashMap<String, String> whatsappPrefMap) {
		String status = "SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		HashMap<String, String> idCodeMap = new HashMap<String, String>();
		iLogger.info(
				"MCoreApp:UserAlertPrefProxy:setUserAlertPref:Get the Event Code/ Event type code from MySQL - Start");
		// -------------------------------------- SETP1: Get the EvenCode/EventTypeCode
		// from MySQL
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try {
				if (prefLevel != null && prefLevel.equalsIgnoreCase("Alert")) {
					Query query = session.createQuery("from EventEntity");
					Iterator itr = query.list().iterator();
					while (itr.hasNext()) {
						EventEntity event = (EventEntity) itr.next();
						idCodeMap.put(String.valueOf(event.getEventId()), event.getEventCode());
					}
				} else if (prefLevel != null && prefLevel.equalsIgnoreCase("AlertType")) {
					Query query = session.createQuery("from EventTypeEntity");
					Iterator itr = query.list().iterator();
					while (itr.hasNext()) {
						EventTypeEntity eventType = (EventTypeEntity) itr.next();
						idCodeMap.put(String.valueOf(eventType.getEventTypeId()), eventType.getEventTypeCode());
					}
				} else {
					fLogger.fatal(
							"MCoreApp:UserAlertPrefProxy:setUserAlertPref:Get the Event Code/ Event type code from MySQL: Invalid prefLevel:"
									+ prefLevel);
					return "FAILURE";
				}
			} catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("MCoreApp:"
						+ "UserAlertPrefProxy:setUserAlertPref:Get the Event Code/ Event type code :Exception in getting details from MySQL :"
						+ e.getMessage());
				return "FAILURE";
			} finally {
				try {
					if (session != null && session.isOpen())
						if (session.getTransaction().isActive()) {
							session.getTransaction().commit();
						}
				} catch (Exception e) {
					fLogger.fatal("MCoreApp:"
							+ "UserAlertPrefProxy:setUserAlertPref:Get the Event Code/ Event type code:Exception in comitting the hibernate session:"
							+ e);
				}
				if (session.isOpen()) {
					if (session.getTransaction().isActive())
						session.flush();
					session.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("MCoreApp:"
					+ "UserAlertPrefProxy:setUserAlertPref:Get the Event Code/ Event type code:Exception in getting Hibernate Session:"
					+ e.getMessage());
			return "FAILURE";
		}
		iLogger.info(
				"MCoreApp:UserAlertPrefProxy:setUserAlertPref:Get the Event Code/ Event type code from MySQL - END");
		// -------------------------------------- SETP2: Replace IDs in smsPrefMap with
		// Codes and Invoke the method for storing details into OrientDB
		iLogger.info(
				"MCoreApp:UserAlertPrefProxy:setUserAlertPref:Invoke UserAlertPrefImpl for storing SMS Preference in OrientDB - START");
		HashMap<String, String> newSmsPrefMap = new HashMap<String, String>();
		for (Map.Entry<String,String> entry : smsPrefMap.entrySet()) {
			newSmsPrefMap.put(idCodeMap.get(entry.getKey().toString()), entry.getValue().toString());
		}
		iLogger.info("MCoreApp:UserAlertPrefProxy:userId: " + userId + " SMS" + " prefLevel: " + prefLevel
				+ " newEmailPrefMap " + newSmsPrefMap);
		String smsStatus = new UserAlertPrefImpl().setUserAlertPref(userId, "SMS", prefLevel, newSmsPrefMap);
		iLogger.info(
				"MCoreApp:UserAlertPrefProxy:setUserAlertPref:Invoke UserAlertPrefImpl for storing SMS Preference in OrientDB - END");
		// -------------------------------------- SETP3: Replace IDs in emailPrefMap
		// with Codes and Invoke the method for storing details into OrientDB
		iLogger.info(
				"MCoreApp:UserAlertPrefProxy:setUserAlertPref:Invoke UserAlertPrefImpl for storing Email Preference in OrientDB - START");
		HashMap<String, String> newEmailPrefMap = new HashMap<String, String>();
		for (Map.Entry<String,String> entry : emailPrefMap.entrySet()) {
			newEmailPrefMap.put(idCodeMap.get(entry.getKey().toString()), entry.getValue().toString());
		}
		iLogger.info("MCoreApp:UserAlertPrefProxy:userId: " + userId + " Email" + " prefLevel: " + prefLevel
				+ " newEmailPrefMap " + newEmailPrefMap);
		String emailsStatus = new UserAlertPrefImpl().setUserAlertPref(userId, "Email", prefLevel, newEmailPrefMap);
		iLogger.info(
				"MCoreApp:UserAlertPrefProxy:setUserAlertPref:Invoke UserAlertPrefImpl for storing Email Preference in OrientDB - END");
		// -------------------------------------- SETP3: Replace IDs in Whatsapp pref
		// map with Codes and Invoke the method
		iLogger.info(
				"MCoreApp:UserAlertPrefProxy:setUserAlertPref:Invoke UserAlertPrefImpl for storing Whatsapp Preference in OrientDB - START");
		HashMap<String, String> newWhatsappPrefMap = new HashMap<String, String>();
		for (Map.Entry<String,String> entry : whatsappPrefMap.entrySet()) {
			newWhatsappPrefMap.put(idCodeMap.get(entry.getKey().toString()), entry.getValue().toString());
		}
		iLogger.info("MCoreApp:UserAlertPrefProxy:userId: " + userId + " WhatsApp" + " prefLevel: " + prefLevel
				+ " newWhatsappPrefMap " + newWhatsappPrefMap);
		String whatsappStatus = new UserAlertPrefImpl().setUserAlertPref(userId, "WhatsApp", prefLevel,
				newWhatsappPrefMap);
		iLogger.info(
				"MCoreApp:UserAlertPrefProxy:setUserAlertPref:Invoke UserAlertPrefImpl for storing Email Preference in OrientDB - END");
		if (smsStatus.equalsIgnoreCase("FAILURE") || emailsStatus.equalsIgnoreCase("FAILURE")
				|| whatsappStatus.equalsIgnoreCase("FAILURE")) {
			status = "FAILURE";
		}
		return status;
	}//CR500.en
	
	//CR500.so
	/*public String setUserAlertPref(String userId, String prefLevel, HashMap<String,String> smsPrefMap, HashMap<String,String> emailPrefMap)
	{
		String status="SUCCESS";
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		HashMap<String,String> idCodeMap = new HashMap<String,String>();
		
		iLogger.info("MCoreApp:UserAlertPrefProxy:setUserAlertPref:Get the Event Code/ Event type code from MySQL - Start");
		//-------------------------------------- SETP1: Get the EvenCode/EventTypeCode from MySQL
		try
		{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			
			try
			{
				if(prefLevel!=null && prefLevel.equalsIgnoreCase("Alert"))
				{
					Query query = session.createQuery("from EventEntity");
					Iterator itr = query.list().iterator();
					while(itr.hasNext())
					{
						EventEntity event = (EventEntity) itr.next();
						idCodeMap.put(String.valueOf(event.getEventId()), event.getEventCode());
					}
					
				}
				else if (prefLevel!=null && prefLevel.equalsIgnoreCase("AlertType"))
				{
					Query query = session.createQuery("from EventTypeEntity");
					Iterator itr = query.list().iterator();
					while(itr.hasNext())
					{
						EventTypeEntity eventType = (EventTypeEntity) itr.next();
						idCodeMap.put(String.valueOf(eventType.getEventTypeId()), eventType.getEventTypeCode());
					}
				}
				else
				{
					fLogger.fatal("MCoreApp:UserAlertPrefProxy:setUserAlertPref:Get the Event Code/ Event type code from MySQL: Invalid prefLevel:"+prefLevel);
					return "FAILURE";
				}
				
				
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("MCoreApp:"+"UserAlertPrefProxy:setUserAlertPref:Get the Event Code/ Event type code :Exception in getting details from MySQL :"+e.getMessage());
				return "FAILURE";
				
			}
			
			finally
			{
				try
				{
					if(session!=null && session.isOpen())  
						if(session.getTransaction().isActive())
						{
							session.getTransaction().commit();
						}
				}
				catch(Exception e)
				{
					fLogger.fatal("MCoreApp:"+"UserAlertPrefProxy:setUserAlertPref:Get the Event Code/ Event type code:Exception in comitting the hibernate session:"+e);
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
			fLogger.fatal("MCoreApp:"+"UserAlertPrefProxy:setUserAlertPref:Get the Event Code/ Event type code:Exception in getting Hibernate Session:"+e.getMessage());
			return "FAILURE";
		}
		iLogger.info("MCoreApp:UserAlertPrefProxy:setUserAlertPref:Get the Event Code/ Event type code from MySQL - END");
		
		//-------------------------------------- SETP2: Replace IDs in smsPrefMap with Codes and Invoke the method for storing details into OrientDB
		iLogger.info("MCoreApp:UserAlertPrefProxy:setUserAlertPref:Invoke UserAlertPrefImpl for storing SMS Preference in OrientDB - START");
		HashMap<String,String> newSmsPrefMap = new HashMap<String,String>();
		for(Map.Entry entry : smsPrefMap.entrySet())
		{
			newSmsPrefMap.put(idCodeMap.get(entry.getKey().toString()), entry.getValue().toString());
		}
		String smsStatus = new UserAlertPrefImpl().setUserAlertPref(userId, "SMS", prefLevel, newSmsPrefMap);
		iLogger.info("MCoreApp:UserAlertPrefProxy:setUserAlertPref:Invoke UserAlertPrefImpl for storing SMS Preference in OrientDB - END");
		
		//-------------------------------------- SETP3: Replace IDs in emailPrefMap with Codes and Invoke the method for storing details into OrientDB
		iLogger.info("MCoreApp:UserAlertPrefProxy:setUserAlertPref:Invoke UserAlertPrefImpl for storing Email Preference in OrientDB - START");
		HashMap<String,String> newEmailPrefMap = new HashMap<String,String>();
		for(Map.Entry entry : emailPrefMap.entrySet())
		{
			newEmailPrefMap.put(idCodeMap.get(entry.getKey().toString()), entry.getValue().toString());
		}
		String emailsStatus= new UserAlertPrefImpl().setUserAlertPref(userId, "Email", prefLevel, newEmailPrefMap);
		iLogger.info("MCoreApp:UserAlertPrefProxy:setUserAlertPref:Invoke UserAlertPrefImpl for storing Email Preference in OrientDB - END");
		
		if(smsStatus.equalsIgnoreCase("FAILURE") || emailsStatus.equalsIgnoreCase("FAILURE"))
		{
			status="FAILURE";
		}
		
		return status;
	}*/
	//CR500.eo
}
