package remote.wise.handler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
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

import org.apache.logging.log4j.*;
import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.businessobject.EventDetailsBO;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

public class SmsHandler implements MessageListener
{
	/**
	 * DefectID:1012 - Rajani Nagaraju - 20130718
	 * HornetQ issue for email Queue
	 * DefectID:  - 1288 - Rajani Nagaraju - Custom SMS implementation
	 */
	//private static final long serialVersionUID = 877950060752702227L;
	//public static WiseLogger fatalError = WiseLogger.getLogger("SmsHandler:","fatalError");
	//public static WiseLogger infoLogger = WiseLogger.getLogger("SmsHandler:","info");
	//Session session = null;
	
	public void handleSmsInKafka(String topicName, SmsTemplate msg,int reqId){
		
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
			 iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to KafkaProducer - First attempt failed !!");
			 try
			 {
				 putToKafkaProducerQueue(topicName, msg);
				 if(reqId!=0)
				 {
					deleteEntry("sms",reqId, msg.getSerialNumber(), msg.getTransactionTime());
				}
			 }
			 
			 catch(Exception e2)
			 {
				 iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to KafkaProducer - Second attempt failed !!");
				 try
				 {
					 putToKafkaProducerQueue(topicName, msg);
					 if(reqId!=0)
					 {
						deleteEntry("sms",reqId, msg.getSerialNumber(), msg.getTransactionTime());
					}
				 }
				
				 catch(Exception e3)
				 {
					iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to KafkaProducer - Third attempt failed !!");
					try
					{
						
						putToKafkaProducerQueue(topicName, msg);
						if(reqId!=0)
						{
							deleteEntry("sms",reqId, msg.getSerialNumber(), msg.getTransactionTime());
						}
					}
					
					catch(Exception e4)
					{
						iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to KafkaProducer - Fourth attempt failed !!");
						//insert to table if reqid==0
						if(reqId==0)
						{
							status = addToPending(null,msg);
							iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Add failed msg to pending table: "+status);
						}
						
					}
				}
				
			 }
		 }
	}
   
	

	
	
	
	public void handleSms(String queuePath, SmsTemplate msg,int reqId)
    {
		Logger iLogger = InfoLoggerClass.logger;
    	
    	iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to SMS Queue");
		Context ic = null;
		String status = null;
		try
		{
			 ic = getInitialContext(); 
			 putToQueue(ic,queuePath,msg);
			 if(reqId!=0)
			 {
				deleteEntry("sms",reqId, msg.getSerialNumber(), msg.getTransactionTime());
			}
		}
		
		catch(Exception e1)
		{
			 //On Exception in connecting to SMS Queue, try three more times
			 iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to SMS Q - First attempt failed !!");
			 try
			 {
				 ic = getInitialContext(); 
				 putToQueue(ic,queuePath,msg);
				 if(reqId!=0)
				 {
					deleteEntry("sms",reqId, msg.getSerialNumber(), msg.getTransactionTime());
				}
			 }
			 
			 catch(Exception e2)
			 {
				 iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to SMS Q - Second attempt failed !!");
				 try
				 {
					 ic = getInitialContext(); 
					 putToQueue(ic,queuePath,msg);
					 if(reqId!=0)
					 {
						deleteEntry("sms",reqId, msg.getSerialNumber(), msg.getTransactionTime());
					}
				 }
				
				 catch(Exception e3)
				 {
					iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to SMS Q - Third attempt failed !!");
					try
					{
						ic = getInitialContext();
						putToQueue(ic,queuePath,msg);
						if(reqId!=0)
						{
							deleteEntry("sms",reqId, msg.getSerialNumber(), msg.getTransactionTime());
						}
					}
					
					catch(Exception e4)
					{
						iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Connect to SMS Q - Fourth attempt failed !!");
						//insert to table if reqid==0
						if(reqId==0)
						{
							status = addToPending(null,msg);
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
	public String deleteEntry(String msgType,int reqId, String serialNumber, String transactionTime)
	{
		String status = null;
		EventDetailsBO eventDetailsBoObj = new EventDetailsBO();
		status = eventDetailsBoObj.deleteEntry(msgType,reqId, serialNumber,transactionTime );
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
    public void putToQueue(Context ic,String queuePath, SmsTemplate msg) throws Exception 
    {
    	Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
    	iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Put To SMS Queue : START");
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

            //DefectID: DF20131105 - Rajani Nagaraju - Adding Info Loggers to track Alerts
            iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+"----- Publish to SMS Queue--------");
            iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+"SMS To list: "+msg.getTo());
            iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+"SMS Body: "+msg.getMsgBody());
            
            ObjectMessage message = session.createObjectMessage(msg);
            publisher.send(message); 
            
            Thread.currentThread().sleep(5000);
        }
        
        catch(Exception e)
        {
        	e.printStackTrace();
        	fLogger.fatal("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Failed to connect to SMS Queue");
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
                    e.printStackTrace();
                }
            }
        }
        
        iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Put To SMS Queue : END");
    }
    
    
    //KO369761
    public void putToKafkaProducerQueue(String topicName, SmsTemplate msg) throws Exception {
    	Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Producer<String, String> producer = null;
    	long startTime = System.currentTimeMillis();

    	iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+": topicName :"+topicName+" Put To Kafka Producer : START");
    	Properties prop = null;
    	String IP = null;
    	String port = null;
    	
    	try{
    		//ME100005792.sn - changing the kafka ip port to app1 ip and port and also feting ip from property file
    		prop = CommonUtil.getDepEnvProperties();
    		IP = prop.getProperty("SMSKafkaIP");  
			port = prop.getProperty("SMSKafkaPort");
			//ME100005792.en
    		Properties props = new Properties();
    		//props.put("metadata.broker.list","localhost:9092");	ME100005792.o
    		props.put("metadata.broker.list", IP+":"+port);		//ME100005792.n
    		props.put("serializer.class", "kafka.serializer.StringEncoder");
    		props.put("request.required.acks", "0");

    		ProducerConfig config = new ProducerConfig(props);
    		producer = new Producer<String, String>(config);
    		
    		if(producer != null){
    			iLogger.info("producer was created :"+producer);
    		}
    		System.out.println(msg);
    		String smsTemplateJson = null;
    		if(msg !=null){
    			ObjectMapper mapper = new ObjectMapper();
    			smsTemplateJson = mapper.writeValueAsString(msg);
    		}
    		iLogger.info("SMSHandler:handleSmsInKafka: SMS Template Json"+smsTemplateJson);
    		KeyedMessage<String, String> data = new KeyedMessage<String, String>(topicName, smsTemplateJson);
    		iLogger.info("SMSHandler:handleSmsInKafka data : "+data);
    		producer.send(data);
    		producer.close();
    		iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+"SMS To list: "+msg.getTo());
    		iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+"SMS Body: "+msg.getMsgBody());
    		iLogger.info("AGS:"+msg.getSerialNumber()+":"+msg.getTransactionTime()+":"+" Put To SMS Queue : END");
    		long endTime=System.currentTimeMillis();
    		iLogger.info("SMSHandler:handleSmsInKafka:"+msg.getSerialNumber()+":" +
    				"Put To SMS Queue : END :"+(endTime-startTime)); 
    	}catch (Exception e) {
    		fLogger.error("SMSHandler:handleSmsInKafka: "+msg.getSerialNumber()+" Cause of Exception :"+e.getMessage());
    	}
    }

    
	
    //Automatically Reads the object from the Queue when an object is added to the Queue
	public synchronized void onMessage(Message message)
    {
		Logger iLogger = InfoLoggerClass.logger;
		
		Logger fLogger = FatalLoggerClass.logger;
    	SmsTemplate smsTempObj=null;
    	List<String> smsTo = new LinkedList<String>();
    	List<String> smsBody = new LinkedList<String>();
    	
		try 
		{
			ObjectMessage Obj = (ObjectMessage) message;
			smsTempObj = (SmsTemplate)Obj.getObject();
			smsTo.addAll(smsTempObj.getTo());
			smsBody.addAll(smsTempObj.getMsgBody());
			
			if(smsTo!=null)
			{
				iLogger.info("AGS:"+smsTempObj.getSerialNumber()+":"+smsTempObj.getTransactionTime()+":"+"----- Consumed from SMS Queue--------");
				iLogger.info("AGS:"+smsTempObj.getSerialNumber()+":"+smsTempObj.getTransactionTime()+":"+"SMS To list: "+smsTo);
				iLogger.info("AGS:"+smsTempObj.getSerialNumber()+":"+smsTempObj.getTransactionTime()+":"+"SMS Body: "+smsBody);
				iLogger.info("AGS:"+smsTempObj.getSerialNumber()+":"+smsTempObj.getTransactionTime()+":"+"AssetEventId: "+smsTempObj.getAssetEventId());
				//new SendSMS().sendMessage(smsTo, smsBody, smsTempObj.getSerialNumber(), smsTempObj.getTransactionTime());
				
				new SendSMS(smsTo, smsBody, smsTempObj.getSerialNumber(), smsTempObj.getTransactionTime(), smsTempObj.getAssetEventId());
			}
			
		
		}
		
		catch (JMSException e) 
		{
			e.printStackTrace();
			fLogger.fatal("JMS Exception:"+e.getMessage());
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
        	fLogger.fatal("Could not close connection " + con +" exception was " + jmse.getMessage());
        	
        	
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
    	
    	Properties p = new Properties( );
    	p.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
    	
		
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
