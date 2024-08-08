package remote.wise.handler;

import java.util.Properties;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class PublishQueue implements MessageListener
{
	public void publishSMSqueue(String queuePath, SmsTemplate smsObj ) throws Exception
	{
		String destinationName = queuePath;
		 
		Context ic = null;
		ConnectionFactory cf = null;
		Connection connection =  null;
		 
		try 
		{        
			ic = getInitialContext();
		 
			cf = (ConnectionFactory)ic.lookup("/ConnectionFactory");
			Queue queue = (Queue)ic.lookup(destinationName);
		 
			connection = cf.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer publisher = session.createProducer(queue);
			
			connection.start();
		 
			ObjectMessage message = session.createObjectMessage(smsObj);
			
			publisher.send(message);
			 
		}
		finally
		{        
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
	 
			closeConnection(connection);
		}
	}
	
	
	public void publishEmailqueue(String queuePath, EmailTemplate emailObj ) throws Exception
	{
		String destinationName = queuePath;
		 
		Context ic = null;
		ConnectionFactory cf = null;
		Connection connection =  null;
		 
		try 
		{        
			ic = getInitialContext();
		 
			cf = (ConnectionFactory)ic.lookup("/ConnectionFactory");
			Queue queue = (Queue)ic.lookup(destinationName);
		 
			connection = cf.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer publisher = session.createProducer(queue);
			
			connection.start();
		 
			ObjectMessage message = session.createObjectMessage(emailObj);
			
			publisher.send(message);
			 
		}
		finally
		{        
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
	 
			closeConnection(connection);
		}
	}
	
	
	public synchronized void onMessage(Message message) 
	{
		Logger fLogger = FatalLoggerClass.logger;
		TextMessage text = (TextMessage)message;
		String strMessage = null;
		try 
		{
			strMessage = text.getText();
		} 
		catch (JMSException e) 
		{
			e.printStackTrace();
		}
		fLogger.fatal("Message received: "+strMessage);
	}
		 
	
	private void closeConnection(Connection con)
	{     
		Logger fLogger = FatalLoggerClass.logger;
		try
		{
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
	 
	
	public static Context getInitialContext( ) throws javax.naming.NamingException 
	{
	 	Properties p = new Properties( );
		p.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
		p.put(Context.URL_PKG_PREFIXES," org.jboss.naming:org.jnp.interfaces");
		p.put(Context.PROVIDER_URL, "jnp://localhost:1099");
		return new javax.naming.InitialContext(p);
	} 
}
