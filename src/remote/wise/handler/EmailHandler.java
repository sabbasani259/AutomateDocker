package remote.wise.handler;

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

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;


import remote.wise.businessobject.EventDetailsBO;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

public class EmailHandler implements MessageListener
{
	/**
	 * DefectID:1012 - Rajani Nagaraju - 20130718
	 * HornetQ issue for email Queue
	 */
	//public static WiseLogger fatalError = WiseLogger.getLogger("EmailHandler:","fatalError");
	//public static WiseLogger infoLogger = WiseLogger.getLogger("EmailHandler:","info");
	//Session session = null;
	
	
	public void handleEmailInKafka(String topicName, EmailTemplate msg,int reqId){
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
    	
		iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to SMS Queue");
		String status = null;
		try
		{
			 putToKafkaProducerQueue(topicName, msg);
			 if(reqId!=0)
			 {
				deleteEntry("sms",reqId, msg.getSerialNumber(), msg.getTransactionTime());
			}
		}
		
		catch(Exception e1)
		{
			 //On Exception in connecting to SMS Queue, try three more times
			 iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to Email Q - First attempt failed !!");
			 try
			 {
				 putToKafkaProducerQueue(topicName, msg);
				 if(reqId!=0)
				 {
					deleteEntry("email",reqId, msg.getSerialNumber(), msg.getTransactionTime());
				 }
			 }
			 
			 catch(Exception e2)
			 {
				 iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to Email Q - Second attempt failed !!");
				 try
				 {
					 putToKafkaProducerQueue(topicName, msg);
					 if(reqId!=0)
					 {
						deleteEntry("email",reqId, msg.getSerialNumber(), msg.getTransactionTime());
					 }
				 }
				
				 catch(Exception e3)
				 {
					iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to Email Q - Third attempt failed !!");
					try
					 {
						 putToKafkaProducerQueue(topicName, msg);
						 if(reqId!=0)
						 {
							deleteEntry("email",reqId, msg.getSerialNumber(), msg.getTransactionTime());
						 }
					 }
					
					catch(Exception e4)
					{
						iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to Email Q - Fourth attempt failed !!");
						 if(reqId==0)
						 {
							status = addToPending(msg,null);
							iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Add failed msg to pending table: "+status);
						}
						
					}
				}
				
			 }
		 }
	}
	
	
	public void handleEmail(String queuePath, EmailTemplate msg,int reqId) 
	{
		 Logger iLogger = InfoLoggerClass.logger;
    	
    	 iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to Email Queue");
		 Context ic = null;
		 String status = null;
		 try
		 {
			 ic = getInitialContext(); 
			 putToQueue(ic,queuePath,msg);
			 if(reqId!=0)
			 {
				deleteEntry("email",reqId, msg.getSerialNumber(), msg.getTransactionTime());
			 }
		 }
		 
		 catch(Exception e1)
		 {
			 //On Exception in connecting to SMS Queue, try three more times
			 iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to Email Q - First attempt failed !!");
			 try
			 {
				 ic = getInitialContext(); 
				 putToQueue(ic,queuePath,msg);
				 if(reqId!=0)
				 {
					deleteEntry("email",reqId, msg.getSerialNumber(), msg.getTransactionTime());
				 }
			 }
			 
			 catch(Exception e2)
			 {
				 iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to Email Q - Second attempt failed !!");
				 try
				 {
					 ic = getInitialContext(); 
					 putToQueue(ic,queuePath,msg);
					 if(reqId!=0)
					 {
						deleteEntry("email",reqId, msg.getSerialNumber(), msg.getTransactionTime());
					 }
				 }
				 
				 catch(Exception e3)
				 {
					 iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to Email Q - Third attempt failed !!");
					 try
					 {
						ic = getInitialContext(); 
						putToQueue(ic,queuePath,msg);
						if(reqId!=0)
						{
							deleteEntry("email",reqId, msg.getSerialNumber(), msg.getTransactionTime());
						}
					}
					
					 catch(Exception e4)
					 {
						 iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to Email Q - Fourth attempt failed !!");
						 if(reqId==0)
						 {
							status = addToPending(msg,null);
							iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Add failed msg to pending table: "+status);
						}
					}
				}
				
			 }
		 }
		
	}
	
	
	/**
	 * method to delete an entry from pending table if req id exists
	 * @param msgType
	 * @param reqId
	 * @return
	 */
	//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
	public String deleteEntry(String msgType,int reqId, String serialNumber, String transactionTime)
	{
		String status = null;
		EventDetailsBO eventDetailsBoObj = new EventDetailsBO();
		status = eventDetailsBoObj.deleteEntry(msgType,reqId, serialNumber,transactionTime);
		return status;
	}
	
	
	/**
	 * method to add request to pending table, once connection failed for 4 times
	 * @param emailTemplate
	 * @param smsTemplate
	 * @return String
	 */
	public String addToPending(EmailTemplate emailTemplate,SmsTemplate smsTemplate)
	{
		EventDetailsBO eventDetailsBoObj = new EventDetailsBO();
		String status =eventDetailsBoObj.addToPending(emailTemplate, smsTemplate);
		return status;
	}
	
	/**
	 * method to put request to queue once get initial context is got
	 * @param ic
	 * @param queuePath
	 * @param msg
	 * @throws Exception
	 */
	 //DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
	public void putToQueue(Context ic,String queuePath, EmailTemplate msg) throws Exception 
	{
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
    	iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Put To Email Queue : START");
    	String destinationName = queuePath;

        ConnectionFactory cf = null;
        Connection connection =  null;
        Session session = null;

        try
        {         
        	cf = (ConnectionFactory)ic.lookup("jms/RemoteConnectionFactory");
        	Queue queue = (Queue)ic.lookup(destinationName);
            connection = cf.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            MessageProducer publisher = session.createProducer(queue);
            MessageConsumer subscriber = session.createConsumer(queue);
            subscriber.setMessageListener(this);
            connection.start();

            iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+"----- Publish to Email Queue--------");
            iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+"Email To list: "+msg.getTo());
            iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+"Email Subject: "+msg.getSubject());
            iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+"Email Body: "+msg.getBody());
            
            ObjectMessage message = session.createObjectMessage(msg);
            publisher.send(message);
            
            Thread.currentThread().sleep(5000);
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        	fLogger.fatal("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Failed to connect to Email Queue");
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
                   // e.printStackTrace();
                	throw e;
                }
            }
        }
        iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Put To Email Queue : END");
	}
    
	
	public void putToKafkaProducerQueue(String topicName, EmailTemplate msg) throws Exception {
    	Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Producer<String, String> producer = null;
    	long startTime = System.currentTimeMillis();

    	iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Put To Kafka Email Queue : START");

    	try{
    		Properties props = new Properties();
    		props.put("metadata.broker.list", "localhost:9092");
    		props.put("serializer.class", "kafka.serializer.StringEncoder");
    		props.put("request.required.acks", "0");

    		ProducerConfig config = new ProducerConfig(props);
    		producer = new Producer<String, String>(config);
    		
    		if(producer != null){
    			iLogger.info("producer was created :"+producer);
    		}
    		System.out.println(msg);
    		String emailTemplateJson = null;
    		if(msg !=null){
    			ObjectMapper mapper = new ObjectMapper();
    			emailTemplateJson = mapper.writeValueAsString(msg);
    		}
    		iLogger.info("EmailHandler:handleEmailInKafka: Email Template Json"+emailTemplateJson);
    		KeyedMessage<String, String> data = new KeyedMessage<String, String>(topicName, emailTemplateJson);
    		iLogger.info("EmailHandler:handleEmailInKafka data : "+data);
    		producer.send(data);
    		producer.close();
    		iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+"----- Publish to Email Queue--------");
            iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+"Email To list: "+msg.getTo());
            iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+"Email Subject: "+msg.getSubject());
            iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+"Email Body: "+msg.getBody());
    		long endTime=System.currentTimeMillis();
    		iLogger.info("EmailHandler:handleEmailInKafka:"+msg.getSerialNumber()+":" +
    				"Put To SMS Queue : END :"+(endTime-startTime)); 
    	}catch (Exception e) {
    		fLogger.error("EmailHandler:handleEmailInKafka: "+msg.getSerialNumber()+" Cause of Exception :"+e.getMessage());
    	}
    }
	
	
	//Automatically Reads the object from the Queue when an object is added to the Queue
	public synchronized void onMessage(Message message)
    {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		ClassLoader originalTCCL = Thread.currentThread().getContextClassLoader();
    	EmailTemplate emailTempObj=null;
    	    	
		try 
		{
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
			ObjectMessage Obj = (ObjectMessage) message;
			emailTempObj = (EmailTemplate)Obj.getObject();
						
			if(emailTempObj!=null)
			{
				iLogger.info("AGS:"+emailTempObj.getSerialNumber()+":"+emailTempObj.getTransactionTime()+"  :"+"----- Consumed from Email Queue--------");
				iLogger.info("AGS:"+emailTempObj.getSerialNumber()+":"+emailTempObj.getTransactionTime()+"  :"+"Email To list: "+emailTempObj.getTo());
				iLogger.info("AGS:"+emailTempObj.getSerialNumber()+":"+emailTempObj.getTransactionTime()+"  :"+"Email Subject: "+emailTempObj.getSubject());
				iLogger.info("AGS:"+emailTempObj.getSerialNumber()+":"+emailTempObj.getTransactionTime()+"  :"+"Email Body: "+emailTempObj.getBody());
				
				if(emailTempObj.getFileToBeAttached()==null)
				{
					//new SendEmail().sendMail(emailTempObj.getTo(), emailTempObj.getSubject(), emailTempObj.getBody(), emailTempObj.getSerialNumber(), emailTempObj.getTransactionTime());
					new SendEmail(emailTempObj.getTo(), emailTempObj.getSubject(), emailTempObj.getBody(), emailTempObj.getSerialNumber(), emailTempObj.getTransactionTime());
				}
				
				else if(emailTempObj.getFileToBeAttached()!=null)
				{
					new SendEmail().sendMailWithAttachment(emailTempObj.getTo(), emailTempObj.getSubject(), emailTempObj.getBody(),emailTempObj.getFileToBeAttached());
				}				
			}			
	
			
		}
		
		catch (JMSException e) 
		{
			//e.printStackTrace();
			fLogger.fatal("JMS Exception:"+e.getMessage());
			
		} 
					
		finally
		{
			Thread.currentThread().setContextClassLoader(originalTCCL);
		}
   }

   
	private void closeConnection(Connection con, Session session)
    {      
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		iLogger.info("closeConnection : start");
        try
        {
        	//DefectID:1012 - Rajani Nagaraju - 20130718
        	//HornetQ issue for email Queue
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
        //	jmse.printStackTrace();
        	fLogger.fatal("Could not close connection " + con +" exception was " + jmse);
        }
        iLogger.info("closeConnection : end");
    }

    protected boolean isQueueExample()
    {
        return true;
    }

   
    public static Context getInitialContext( )  throws javax.naming.NamingException 
    {
    	//DF20150421 - Rajani Nagaraju - Connect to the Jboss HornetQ based on the offset value specified in the server startup
    	int offset=4447;
    	String serverOffset = System.getProperty("jboss.socket.binding.port-offset");
    	if(serverOffset!=null)
    		offset = offset+Integer.parseInt(serverOffset);
    	
    	Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY,"org.jboss.naming.remote.client.InitialContextFactory");

		Properties deployEnvProp = new Properties();
		deployEnvProp = CommonUtil.getDepEnvProperties();
		String productionEnv = deployEnvProp.getProperty("deployenvironment");

		if(productionEnv.equalsIgnoreCase("SIT")){
			p.put(Context.PROVIDER_URL, "remote://10.106.68.9:"+offset);//SIT
		}else if(productionEnv.equalsIgnoreCase("DEV")){
			p.put(Context.PROVIDER_URL, "remote://10.106.68.6:"+offset);//Dev
		}else if(productionEnv.equalsIgnoreCase("QA")){
			p.put(Context.PROVIDER_URL, "remote://10.106.68.14:"+offset);//QA
		}else if(productionEnv.equalsIgnoreCase("PROD")){
			p.put(Context.PROVIDER_URL, "remote://localhost:"+offset);//Production
		}else{
			p.put(Context.PROVIDER_URL, "remote://localhost:"+offset);//LOCALHOST
		}

		return new javax.naming.InitialContext(p);
	}
}
