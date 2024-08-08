package remote.wise.businessentity;

import java.io.Serializable;

import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import java.lang.reflect.*;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;


public class BaseBusinessEntity implements IBusinessEntity 
{

	Object key;
		
	public void save()
	{
		save(this);
	}
	
	public void save(IBusinessEntity entity)
	{
        //get fully qualified class name of the entity
       // String entityClass=entity.getClass().getName();
        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
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
			session.save(entity);
			
			
		}
        catch(Exception e)
        {
        	fLogger.fatal("Couldn't save the Entity. Exception:"+e);
        }
        
        finally
        {
        	try
        	{
        	if(session.getTransaction().isActive())
        		session.getTransaction().commit();
        	}
        	
        	catch(Exception e)
        	{
        		//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
        		fLogger.fatal("Exception in commiting the record:"+e);
        	}
        	if(session.isOpen())
        	{
        		session.flush();
        		session.close();
        	}
        }
	} 
	
	
	
	
	
	public IBusinessEntity read(IBusinessEntity entity)
	{
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
		   	iLogger.info("Opening a new session");
		   	session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();		
		
		try
		{
			Class entity_class= entity.getClass();
			entity=(IBusinessEntity) session.get(entity_class, (Serializable) key);
		}
		
		 finally
	     {
	       	
			 try
			 {if(session.getTransaction().isActive())
	       		session.getTransaction().commit();
			 }
			 
			 catch(Exception e)
			 {
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				 fLogger.fatal("Exception in commiting the record:"+e);
			 }
	       	if(session.isOpen())
	       	{
	       		session.flush();
	       		session.close();
	       	}
	       	
	     }
		 return entity;
		
	}
	
} 
