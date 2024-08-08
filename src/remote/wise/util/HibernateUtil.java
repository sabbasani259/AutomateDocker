package remote.wise.util;

import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import remote.wise.log.InfoLogging.InfoLoggerClass;

public class HibernateUtil 
{
	private static SessionFactory sessionFactory = buildSessionFactory();
	

    private static SessionFactory buildSessionFactory() 
    {
        try 
        {
            // Create the SessionFactory from hibernate.cfg.xml
            return new Configuration().configure("/remote/wise/businessentity/hibernate.cfg.xml").buildSessionFactory();
        }
        
        catch (Throwable ex) 
        {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    
    public static SessionFactory getSessionFactory() 
    {
    	Logger iLogger = InfoLoggerClass.logger;
    	Session currSession =null;
    	try
        {
        	currSession = sessionFactory.openSession();
        	
        }
        
        catch(Exception e)
        {
        	e.printStackTrace();
        	iLogger.info(" HibernateUtil: Rebuilding the session factory after exception");
        	
        	if(sessionFactory!=null && ! sessionFactory.isClosed()){
        		sessionFactory.close();
        	}
        	sessionFactory=buildSessionFactory() ;
        }
        
        finally
        {
        	if(currSession!=null && currSession.isOpen())
        	{
        		currSession.flush();
        		currSession.close();
        	}
        }
    	
    	return sessionFactory;
    }
}
