package remote.wise.service.implementation;

import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.jms.*;
import javax.naming.Context;

import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.ClientEntity;
import remote.wise.util.HibernateUtil;

public class WISEMonitoringImpl
{
	public String getWiseStatus()
	{
		String wiseStatus="SUCCESS";
		
		try
		{
			//DF20190830:Abhishek::replaced getCurrentSession() with openSession();
			//Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			//Get the Session from SessionFactory
			try
			{
				Query query = session.createQuery(" from ClientEntity where client_id=1");
				Iterator itr = query.list().iterator();
				while(itr.hasNext())
				{
					ClientEntity client = (ClientEntity)itr.next();
				}
			}
		
			catch(ExceptionInInitializerError e)
			{
				wiseStatus="HibernateUtil Cannot be initialized";
			}
			
			catch(Exception e)
			{
				wiseStatus="Cannot connect to DB";
			}
			
			
			finally
	
			{
				//DF20190827:Abhishek: Not required since using only select query
				/*if (session.getTransaction().isActive()) {
					session.getTransaction().commit();
				}*/
				if (session.isOpen()) {
					//session.flush();
					session.close();
				}
			}
		
		}
		
		catch(ExceptionInInitializerError e)
		{
			wiseStatus="HibernateUtil Cannot be initialized";
		}
		
		catch(Exception e)
		{
			wiseStatus="Cannot connect to DB";
		}
		
		return wiseStatus;
	}
	
	
	
	public String getHornetQStatus()
	{
		String qStatus="SUCCESS";

		Context ic = null;
		Connection connection= null;
		javax.jms.Session session =null;
			
		try
		{
			ic = getInitialContext(); 
			ConnectionFactory cf = (ConnectionFactory)ic.lookup("jms/RemoteConnectionFactory");
			Queue queue = (Queue)ic.lookup("jms/queue/llMonitorQ");
			connection = cf.createConnection();
			//session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			session = connection.createSession(false, javax.jms.Session. DUPS_OK_ACKNOWLEDGE);
			connection.start();
			
			MessageProducer producer  = session.createProducer(queue);
			TextMessage message = session.createTextMessage("vTrac - Monitoring Q Test!");
			producer.setDisableMessageID(true);
			producer.setDisableMessageTimestamp(true);
			producer.send(message);
			System.out.println("vTrac Sent message: " + message.getText());
			
			MessageConsumer consumer = session.createConsumer(queue);
			
			TextMessage messageReceived = (TextMessage)consumer.receive();
			System.out.println("Received message: " + messageReceived.getText());
			
		}
		catch(Exception e1)
		{
			e1.printStackTrace();
			qStatus = "FAILURE";
		}
		
		finally
        {         
            // ALWAYS close your connection in a finally block to avoid leaks.
            // Closing connection also takes care of closing its related objects e.g. sessions.
        	//DefectID:1012 - Rajani Nagaraju - 20130718
        	//HornetQ issue for email Queue
        	closeConnection(connection,session);
            
            if(ic != null)
            {
                try
                {
                    ic.close();
                }
                catch(Exception e)
                {
                	System.out.println("vTrac: "+new Date()+"Error in closing IntialContextFactory");
                	//e.printStackTrace();
                	//queueStatus = "Down";
                	
                }
            }
        }
		
		return qStatus;
	}
	
	public static Context getInitialContext( )  throws javax.naming.NamingException 
    {
    	Properties p = new Properties( );
    	p.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
    	p.put(Context.PROVIDER_URL, "remote://localhost:4447");//Dev
    	
    	return new javax.naming.InitialContext(p);
    }
	
	
	private void closeConnection(Connection con, javax.jms.Session session)
    {      
		try
        {
        	//DefectID:1012 - Rajani Nagaraju - 20130718
        	//HornetQ issue for email Queue 
        	if(session!=null)
             {
        		try
        		{
	        		if(session.getTransacted())
	            		session.commit();
        		}
        		catch(Exception e)
        		{
        			e.printStackTrace();
        		}
             	
        		session.close();
             	
             }
        	 
          	if (con != null)
            {
                con.close();
            }
           
        }
        catch(JMSException jmse)
        {
        	System.out.println("vTrac: "+new Date()+"Could not close the JMS connection");
        	//queueStatus = "Down";
        }
        
               
    }
}
