package remote.wise.businessobject;

import java.sql.Timestamp;
import java.util.Iterator;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.FotaAuthenticationImpl;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class FotaAuthenticationBO {
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("FotaAuthenticationBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("FotaAuthenticationBO:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("FotaAuthenticationBO:","info");*/
	
	
	
	public FotaAuthenticationImpl imeiAuthentication(String imeiNo,String sessionId)
	{
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		
		FotaAuthenticationImpl fotaAuthenticationImpl = new FotaAuthenticationImpl();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
//        Keerthi : 02/01/15 : fix to connection closed
        if(!(session.isOpen() ))
		{
				session = HibernateUtil.getSessionFactory().openSession();
				session.beginTransaction();
		}
		try
		{
			String basicSelectQuery = " SELECT imeiNo ";
			String basicFromQuery = " FROM AssetControlUnitEntity  ";
			String 	basicWhereQuery = " where imeiNo  = '"+imeiNo+"'" ;
			String finalQuery =basicSelectQuery+ basicFromQuery + basicWhereQuery;
			iLogger.info("FotaAuthenticationQ :"+finalQuery);
			Query q = session.createQuery(finalQuery);
            Iterator itr = q.list().iterator();
            int count=0;
			if(itr.hasNext())
			{
				itr.next();
				count++;
				
			}
			if(count == 0)
			{
				iLogger.info("finalQuery: "+finalQuery);
				iLogger.info("Authentication Failed for the combination: "+imeiNo+","+sessionId);
				fotaAuthenticationImpl.setSessionId(sessionId);
				fotaAuthenticationImpl.setStatus("AUTHENTICATION_FAILED");				
				
			}
			else
			{
				fotaAuthenticationImpl.setSessionId(sessionId);
				fotaAuthenticationImpl.setStatus("AUTHENTICATION_PASSED");
			}
			
		}
		
		catch (Exception e)
		 {
			fotaAuthenticationImpl.setSessionId(sessionId);
			fotaAuthenticationImpl.setStatus("AUTHENTICATION_FAILED_ERR");	
			fLogger.fatal("Exception :" +e+" message : "+e.getMessage());
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
			 }
	          
	          if(session.isOpen())
	          {
	                session.flush();
	                session.close();
	          }
	          
	    }
		return fotaAuthenticationImpl;	
	}

}
