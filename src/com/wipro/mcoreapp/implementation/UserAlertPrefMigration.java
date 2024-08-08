package com.wipro.mcoreapp.implementation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.EventEntity;
import remote.wise.businessentity.EventTypeEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

public class UserAlertPrefMigration 
{
	public String migrateUserPref(String userId)
	{
		String status="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		HashMap<String,String> contactRoleMap = new HashMap<String,String>();
		HashMap<String,String> eventIdCodeMap = new HashMap<String,String>();
		HashMap<String,String> eventTypeIdCodeMap = new HashMap<String,String>();
		
		long startTime = System.currentTimeMillis();
		iLogger.info("MCoreApp:"+"UserAlertPrefMigration:START");
		
		
		try
		{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			iLogger.info("MCoreApp:"+"UserAlertPrefMigration:Get the list of all active contacts from MySQL:START");
			
			try
			{
				//------------------------- STEP1: Get the ContactList
				String contactQString = " select a.contact_id, b.role_name from ContactEntity a, RoleEntity b " +
						" where a.role=b.role_id and a.active_status=1 ";
				if(userId!=null)
					contactQString=contactQString+" and contact_id='"+userId+"'";
				
				Query contactQ = session.createQuery(contactQString);
				Iterator contactItr = contactQ.list().iterator();
				Object[] result = null;
				while(contactItr.hasNext())
				{
					result = (Object[])contactItr.next();
					
					if(result[0]!=null && result[1]!=null)
						contactRoleMap.put(result[0].toString(), result[1].toString());
					
				}
				
				//------------------------- STEP2: Get the List of Event Codes and Eventtype Codes
				Query eventquery = session.createQuery("from EventEntity");
				Iterator eventItr = eventquery.list().iterator();
				while(eventItr.hasNext())
				{
					EventEntity event = (EventEntity) eventItr.next();
					eventIdCodeMap.put(String.valueOf(event.getEventId()), event.getEventCode());
				}
				
				
				Query eventTypequery = session.createQuery("from EventTypeEntity");
				Iterator eventTypeItr = eventTypequery.list().iterator();
				while(eventTypeItr.hasNext())
				{
					EventTypeEntity eventType = (EventTypeEntity) eventTypeItr.next();
					eventTypeIdCodeMap.put(String.valueOf(eventType.getEventTypeId()), eventType.getEventTypeCode());
				}
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("MCoreApp:"+"UserAlertPrefMigration:Get Contact details from MySQL:Exception in getting details from MySQL :"+e.getMessage());
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
					e.printStackTrace();
					fLogger.fatal("MCoreApp:"+"UserAlertPrefMigration:Get Contact details from MySQL:Exception in comitting the hibernate session:"+e);
				}
	
				if(session.isOpen())
				{
					if(session.getTransaction().isActive())
						session.flush();
					session.close();
				}

			}
			iLogger.info("MCoreApp:"+"UserAlertPrefMigration:Get the list of all active contacts from MySQL:END: Total Contacts to be updated:"+contactRoleMap.size());
			
			int noOfThreads=100;
			if(contactRoleMap.size()<100)
			{
				noOfThreads=contactRoleMap.size();
			}
			
			ExecutorService executor = Executors.newFixedThreadPool(noOfThreads);
			int i=0;
			
			for(Map.Entry<String,String> entry: contactRoleMap.entrySet())
			{
				//-------------Once all the 100 threads are spawned, wait for all 100 to finish before spawning another set of 100 threads	
				if(i!=0 && i%noOfThreads==0)
				{
					executor.shutdown();  
//					iLogger.debug("MCoreApp:UserAlertPrefMigration:userId:"+userId+":Before spawning thread "+i+", wait for current Executor Threads to finish");
					while (!executor.isTerminated()) {   }  
				
					executor = Executors.newFixedThreadPool(noOfThreads);
				}
					
//				iLogger.debug("MCoreApp:UserAlertPrefMigration:userId:"+userId+":Spawning thread:"+i);
				
				UserAlertPrefExecutor execObj = new UserAlertPrefExecutor();
				execObj.userId=entry.getKey();
				execObj.roleName=entry.getValue();
				execObj.eventIdCodeMap=eventIdCodeMap;
				execObj.eventTypeIdCodeMap=eventTypeIdCodeMap;
					
				Callable<String> worker = execObj;
				Future<String> submit =executor.submit(worker);
				
				i++;
			}
				
			executor.shutdown();  
			while (!executor.isTerminated()) {   }  	
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("MCoreApp:"+"UserAlertPrefMigration:Get Contact details from MySQL:Exception in getting Hibernate Session:"+e.getMessage());
			return "FAILURE";
		}
		
		long endTime = System.currentTimeMillis();
		iLogger.info("MCoreApp:"+"UserAlertPrefMigration:END: Total Number of Contacts:"+contactRoleMap.size()+": Total Time in ms:"+(endTime-startTime));
		
		
		return status;
		
	}
}
