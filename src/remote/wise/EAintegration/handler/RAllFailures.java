package remote.wise.EAintegration.handler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class RAllFailures 
{
	/*public static WiseLogger fatalError = WiseLogger.getLogger("RAllFailures:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("RAllFailures:","info");*/
	
	public void reprocessAllFailureRecords()
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
		   	iLogger.info("Opening a new session");
		   	session = HibernateUtil.getSessionFactory().openSession();
		}
        session.beginTransaction();
        
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try
        {
        	Query query = session.createQuery(" Update FaultDetails set failureCounter=1, reprocessTimeStamp='"+currentTime+"' where failureCounter>9");
        	int rows = query.executeUpdate();
        	iLogger.info("Reset Reprocess Counter. Number of records updated: "+rows);
        }
        
        catch(Exception e)
		{
        	fLogger.fatal("EA ReProcessing: All Failure Records: Fatal Exception :"+e);
		}
        
        finally
        {
        	//DF20150508 - Rajani Nagaraju - Addig try catch around commit
			try
			{
				if(session.isOpen())
					if(session.getTransaction().isActive())
					{
						session.getTransaction().commit();
					}
			}
			catch(Exception e2)
			{
				fLogger.fatal("Exception :"+e2);
			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}
              
        }
	}
}
