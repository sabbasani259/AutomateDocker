package remote.wise.EAintegration.Qhandler;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.EAclient.CustomerInfoClient;
import remote.wise.EAintegration.dataContract.CustomerInfoInputContract;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class CustomerInfoQHandler implements MessageListener
{
	/*Logger fatalError = Logger.getLogger("fatalErrorLogger");
	Logger infoLogger = Logger.getLogger("infoLogger");*/
	
	public void handleCustomerInfo(String queuePath, CustomerInfoInputContract msg) throws Exception
    {
        String destinationName = queuePath;
        Logger iLogger = InfoLoggerClass.logger;
        Context ic = null;
        ConnectionFactory cf = null;
        Connection connection =  null;
        Session session =null;

        try
        {         
        	iLogger.info(msg.getCustomerName()+" : Connect to Q");
        	ic = getInitialContext();

            cf = (ConnectionFactory)ic.lookup("jms/RemoteConnectionFactory");
            Queue queue = (Queue)ic.lookup(destinationName);

            connection = cf.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer publisher = session.createProducer(queue);
            MessageConsumer subscriber = session.createConsumer(queue);

            subscriber.setMessageListener(this);
            connection.start();
            iLogger.info(msg.getCustomerName()+" : Send message to Q");
            ObjectMessage message = session.createObjectMessage(msg);
            publisher.send(message);
            Thread.currentThread().sleep(3000);
            iLogger.info(msg.getCustomerName()+" : Message sent successfully to Q");
        }
        
        finally
        {         
            
            // ALWAYS close your connection in a finally block to avoid leaks.
            // Closing connection also takes care of closing its related objects e.g. sessions.
            closeConnection(connection, session);
            
            if(ic != null)
            {
                try
                {
                    ic.close();
                }
                catch(Exception e)
                {
                    throw e;
                }
            }

        }
    }
	
    
	//Automatically Reads the object from the Queue when an object is added to the Queue
	public synchronized void onMessage(Message message)
    {
    	//ClassLoader originalTCCL = Thread.currentThread().getContextClassLoader();
		CustomerInfoInputContract inputObj=null;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger; 	
		try 
		{
			
			//Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			ObjectMessage Obj = (ObjectMessage) message;
			inputObj = (CustomerInfoInputContract)Obj.getObject();
					
			iLogger.info(inputObj.getCustomerName()+": Message received from Q");
			if(inputObj!=null)
			{
				iLogger.info(inputObj.getCustomerName()+"Invoke Webservice Client");
				new CustomerInfoClient().invokeCustomerInfoService(inputObj);
				iLogger.info(inputObj.getCustomerName()+"Returned from Webservice Client");
			}
		}
		
		catch (JMSException e) 
		{
			fLogger.fatal("JMS Exception:"+e);
			
		} 
		
   }

   
	private void closeConnection(Connection con, Session session)
    {    
		Logger fLogger = FatalLoggerClass.logger;
        try
        {
            if(session!=null)
            {
            	if(session.getTransacted())
            		session.commit();
            	session.close();
            }
        	
        	if (con != null)
            {
                con.close();
            }         
        }
        catch(JMSException jmse)
        {
        	fLogger.fatal("Could not close connection " + con +" exception was " + jmse);
        }
    }

    protected boolean isQueueExample()
    {
        return true;
    }

   
    public static Context getInitialContext( )  throws javax.naming.NamingException 
    {
    	Properties p = new Properties( );
    	p.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
    	//p.put(Context.PROVIDER_URL, "remote://10.106.68.6:4447");
    	// p.put(Context.PROVIDER_URL, "remote://10.106.68.9:4447");
    	p.put(Context.PROVIDER_URL, "remote://localhost:4447");
       	return new javax.naming.InitialContext(p);
    }
}
