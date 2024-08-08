package remote.wise.EAintegration.Qhandler;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.naming.Context;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.EAintegration.EAclient.DealerMappingClient;
import remote.wise.EAintegration.EAclient.EngineTypeClient;
import remote.wise.EAintegration.dataContract.DealerMappingInputContract;
import remote.wise.EAintegration.dataContract.EngineTypeInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class EngineTypeQHandler implements Callable
{
	//Logger fatalError = Logger.getLogger("fatalErrorLogger");
	
	String engineTypeInput = null;

	//DF20171212:KO369761-Changing interfaces queues to Kafka from Hornet queues.
	@Override
	public Object call() throws Exception {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		ObjectMapper mapper = new ObjectMapper();
		EngineTypeInputContract inputObj=null;
		HashMap<String,String> engineTypeInputMap = null;

		try{
			engineTypeInputMap = new Gson().fromJson(engineTypeInput, new TypeToken<HashMap<String, String>>(){}.getType());
			inputObj = mapper.convertValue(engineTypeInputMap, EngineTypeInputContract.class);
			iLogger.info("EngineType - "+inputObj.getEngineTypeCode()+": Message received from Q");
			if(inputObj!=null)
			{
				iLogger.info("EngineType - "+inputObj.getEngineTypeCode()+"Invoke Webservice Client");
				new EngineTypeClient().invokeEngineTypeService(inputObj);
				iLogger.info("EngineType - "+inputObj.getEngineTypeCode()+"Returned from Webservice Client");
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			fLogger.fatal("EngineType Exception :"+e.getMessage());
		}

		return null;
	}

	//DF20171212:KO369761-Changing interfaces queues to Kafka from Hornet queues.
	public String handleEngineTypeDetailsToKafkaQueue(String topicName, EngineTypeInputContract msg) throws Exception {

		String qDropStatus="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Producer<String, String> producer = null;


		try{
			iLogger.info("EngineType - "+msg.getEngineTypeCode()+" : Send message to Q");

			Properties props = new Properties();
			props.put("metadata.broker.list", "localhost:9092");
			props.put("serializer.class", "kafka.serializer.StringEncoder");
			props.put("request.required.acks", "1");

			ProducerConfig config = new ProducerConfig(props);
			producer = new Producer<String, String>(config);

			if(producer != null){
				iLogger.info("producer was created :"+producer);
			}

			iLogger.info("EngineType - "+msg.getEngineTypeCode()+" : Send message to Q");

			String engineTypeInputJson = null;
			if(msg !=null){
				ObjectMapper mapper = new ObjectMapper();
				engineTypeInputJson = mapper.writeValueAsString(msg);
			}
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(topicName, engineTypeInputJson);
			producer.send(data);
			iLogger.info("EngineType - "+msg.getEngineTypeCode()+": Message Sent to Q for EngineNum");
		}//DF20141028 - Rajani Nagaraju - Move the packet to Fault Details if the message cannot be put to Queue
		catch(Exception e)
		{
			String engineTypeName="",engineTypeCode=""; 

			if(msg.getEngineTypeName()!=null)
				engineTypeName=msg.getEngineTypeName();
			if(msg.getEngineTypeCode()!=null)
				engineTypeCode=msg.getEngineTypeCode();
			String messageString = engineTypeName+"|"+engineTypeCode;

			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			errorHandler.handleErrorMessages(msg.getMessageId(), messageString, msg.getFileRef(), msg.getProcess(), msg.getReprocessJobCode(), e.getMessage());
		}

		finally
		{         

			// ALWAYS close your connection in a finally block to avoid leaks.
			// Closing connection also takes care of closing its related objects e.g. sessions.
			producer.close();
		}

		return qDropStatus;
	}

	public void handleEngineTypeDetails(String queuePath, EngineTypeInputContract msg) throws Exception
	{
		String destinationName = queuePath;
		Logger iLogger = InfoLoggerClass.logger;
		Context ic = null;
		ConnectionFactory cf = null;
		Connection connection =  null;
		Session session =null;

		try
		{         
			iLogger.info(msg.getEngineTypeName()+" : Connect to Q");
			ic = getInitialContext();

			cf = (ConnectionFactory)ic.lookup("jms/RemoteConnectionFactory");
			Queue queue = (Queue)ic.lookup(destinationName);

			connection = cf.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer publisher = session.createProducer(queue);

			//DF20160502 @Roopa EngineTypeDetails queue consumer changes to avoid sending message to consumer each time
			/*MessageConsumer subscriber = session.createConsumer(queue);

            subscriber.setMessageListener(this);*/
			connection.start();
			iLogger.info(msg.getEngineTypeName()+" : Send message to Q");
			ObjectMessage message = session.createObjectMessage(msg);
			publisher.send(message);
			Thread.currentThread().sleep(3000);
			iLogger.info(msg.getEngineTypeName()+" : Message sent successfully to Q");
		}


		//DF20141028 - Rajani Nagaraju - Move the packet to Fault Details if the message cannot be put to Queue
		catch(Exception e)
		{
			String engineTypeName="",engineTypeCode=""; 

			if(msg.getEngineTypeName()!=null)
				engineTypeName=msg.getEngineTypeName();
			if(msg.getEngineTypeCode()!=null)
				engineTypeCode=msg.getEngineTypeCode();
			String messageString = engineTypeName+"|"+engineTypeCode;

			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			errorHandler.handleErrorMessages(msg.getMessageId(), messageString, msg.getFileRef(), msg.getProcess(), msg.getReprocessJobCode(), e.getMessage());
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
	/*public synchronized void onMessage(Message message)
    {
    	//ClassLoader originalTCCL = Thread.currentThread().getContextClassLoader();
		EngineTypeInputContract inputObj=null;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;   	
		try 
		{

			//Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			ObjectMessage Obj = (ObjectMessage) message;
			inputObj = (EngineTypeInputContract)Obj.getObject();

			iLogger.info(inputObj.getEngineTypeName()+": Message received from Q");
			if(inputObj!=null)
			{
				iLogger.info(inputObj.getEngineTypeName()+"Invoke Webservice Client");
				new EngineTypeClient().invokeEngineTypeService(inputObj);
				iLogger.info(inputObj.getEngineTypeName()+"Returned from Webservice Client");
			}
		}

		catch (JMSException e) 
		{
			fLogger.fatal("JMS Exception:"+e);

		} 

   }*/


	//DF20160502 @Roopa EngineTypeDetails queue consumer takes all messages from queue at a time
	public String consumeMessage(String queuePath) throws Exception{

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		EngineTypeInputContract inputObj=null;

		String destinationName = queuePath;
		Context ic = null;
		ConnectionFactory cf = null;
		Connection connection =  null;
		Session session =null;
		QueueBrowser queueBrowser = null;
		List msgList = null;
		Enumeration e = null;

		MessageConsumer messageConsumer = null;

		try{

			iLogger.info("START EngineTypeDetails consumerMessage queuePath::"+queuePath);

			ic = getInitialContext();

			cf = (ConnectionFactory)ic.lookup("jms/RemoteConnectionFactory");
			Queue queue = (Queue)ic.lookup(destinationName);

			connection = cf.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// create a queue browser
			queueBrowser = session
					.createBrowser(queue);

			// start the connection
			connection.start();

			// pull the messages into an enumeration
			e = queueBrowser.getEnumeration();
			msgList = Collections.list(e);
			int msgCount = 0;

			if (msgList != null)
				msgCount = msgList.size();

			iLogger.info("Queue MessageCount::"+msgCount);

			messageConsumer = session.createConsumer(queue);

			Message message = null;
			ObjectMessage objMsg = null;

			for(int i=0;i<msgCount;i++){

				message = messageConsumer.receive();

				if (message != null) {
					objMsg = (ObjectMessage) message;

					inputObj = (EngineTypeInputContract)objMsg.getObject();

					iLogger.info(inputObj.getEngineTypeName()+": Message received from Q");
					if(inputObj!=null)
					{
						iLogger.info(inputObj.getEngineTypeName()+"Invoke Webservice Client");
						new EngineTypeClient().invokeEngineTypeService(inputObj);
						iLogger.info(inputObj.getEngineTypeName()+"Returned from Webservice Client");
					}


				}

			}

			iLogger.info("END EngineTypeDetails consumerMessage queuePath::"+queuePath);

		}
		catch(Exception e1)
		{
			e1.printStackTrace();
			fLogger.fatal("Error in invoking the client:"+e1.getMessage());


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
				catch(Exception e1)
				{
					throw e1;
				}
			}

		}



		return queuePath;

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
		p.put(Context.PROVIDER_URL, "remote://localhost:4447");
		return new javax.naming.InitialContext(p);
	}
}
