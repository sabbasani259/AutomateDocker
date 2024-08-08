package com.wipro.mcoreapp.businessobject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.MoolFaultDetailsEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

public class MoolEADetailsReprocessBO 
{
	public void reprocessFromDB(String process)
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		try
		{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			List<String> failureRecords = new LinkedList<String>();
			
			try
			{
				Query faultDetailsQ = session.createQuery(" from MoolFaultDetailsEntity where process='"+process+"'");
				Iterator faultDetailsItr =  faultDetailsQ.list().iterator();
				while(faultDetailsItr.hasNext())
				{
					MoolFaultDetailsEntity faultDetails = (MoolFaultDetailsEntity)faultDetailsItr.next();
					failureRecords.add(faultDetails.getMessageId()+"MOOLSEP"+faultDetails.getFileName()+"MOOLSEP"+faultDetails.getMessageString());
				}
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("MOOL:EADataPopulation:ReprocessFromDB: process:"+process+": Exception :"+e.getMessage());
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
					fLogger.fatal("MOOL:EADataPopulation:ReprocessFromDB: process:"+process+": Exception in session commit:"+e.getMessage());
					
				}
	
				if(session.isOpen())
				{
					if(session.getTransaction().isActive())
						session.flush();
					session.close();
				}
	
			}
			
			iLogger.info("MOOL:EADataPopulation:ReprocessFromDB: process:"+process+": No. of records to be reprocessed:"+failureRecords.size());
			if(failureRecords.size()>0)
			{
				Class<?> c = Class.forName("com.wipro.mcoreapp.businessobject.EADataPopulationBO");
				Method  method = c.getDeclaredMethod (process, String.class);
				
				for(int i=0; i<failureRecords.size(); i++)
				{
					String[] messageStr = failureRecords.get(i).split("MOOLSEP");
					
					String status =  method.invoke (new EADataPopulationBO(), messageStr[0], messageStr[1], messageStr[2]).toString();
					
					//If the record is successfully processed, delete from mool_fault_details in DB
					if(status.equalsIgnoreCase("SUCCESS"))
						new MoolErrorMsgHandlerBO().deleteErrorMessage(messageStr[0]);
				}
			}
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("MOOL:EADataPopulation:ReprocessFromDB: process:"+process+": Exception in getting Hibernate Session:"+e.getMessage());
		}
	}
	
	
	//***************************************************************************************************************************
	
	public void reprocessFromFile(String fileName)
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("MOOL:EADataPopulation:ReprocessFromFile: fileName:"+fileName+":START");
		new MoolErrorMsgHandlerBO().fileToDBHandler(fileName);
		iLogger.info("MOOL:EADataPopulation:ReprocessFromFile: fileName:"+fileName+":END");
	}
	
}
