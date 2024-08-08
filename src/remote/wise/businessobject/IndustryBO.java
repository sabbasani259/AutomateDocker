package remote.wise.businessobject;

import java.util.Iterator;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import remote.wise.businessentity.ClientEntity;
import remote.wise.businessentity.CustomAssetGroupEntity;
import remote.wise.businessentity.IndustryEntity;

import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class IndustryBO extends BaseBusinessObject
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger fatalError = WiseLogger.getLogger("IndustryBO:","fatalError");

	Logger fLogger = FatalLoggerClass.logger;
	
	public ClientEntity getClientEntity(int client_id)
	{
		ClientEntity clientEntityobj=new ClientEntity(client_id);
		return clientEntityobj;
	} 
	public IndustryEntity getIndustryEntity (int industryId)
	{
		IndustryEntity industryEntityObj = new IndustryEntity(industryId);
		return industryEntityObj;
	}
	
	public ClientEntity getClientEntity(String clientName)
	{
		ClientEntity clientEntity = null;
		
		   Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		   Logger iLogger = InfoLoggerClass.logger;
		 //DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		   if(session.getTransaction().isActive() && session.isDirty())
		   {
		      	iLogger.info("Opening a new session");
		      	session = HibernateUtil.getSessionFactory().openSession();
		   }
	        session.beginTransaction();
	    
	        try{
		
		Query query = session.createQuery("from ClientEntity where client_name = '"+clientName+"'");
		Iterator itr = query.list().iterator();
		
		while(itr.hasNext())
		{
			clientEntity = (ClientEntity) itr.next();
		}
	        }catch(Exception e){

	    	fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());

	    }
	        finally
		    {
		        try
		        {
	        	if(session.getTransaction().isActive())
		          {
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
		                session.flush();
		                session.close();
		          }
		          
		    }	
		return clientEntity;
	}
}

