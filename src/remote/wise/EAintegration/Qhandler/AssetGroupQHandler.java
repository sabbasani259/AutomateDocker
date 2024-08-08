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

import remote.wise.EAintegration.EAclient.AssetGroupClient;
import remote.wise.EAintegration.dataContract.AssetGroupInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;


public class AssetGroupQHandler implements Callable
{

	String assetGroupInput = null;

	//DF20171212:KO369761-Changing interfaces queues to Kafka from Hornet queues.
	@Override
	public Object call() throws Exception {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		ObjectMapper mapper = new ObjectMapper();
		AssetGroupInputContract inputObj=null;
		HashMap<String,String> assetGroupInputMap = null;

		try{
			assetGroupInputMap = new Gson().fromJson(assetGroupInput, new TypeToken<HashMap<String, String>>(){}.getType());
			inputObj = mapper.convertValue(assetGroupInputMap, AssetGroupInputContract.class);
			iLogger.info("AssetGroup - "+inputObj.getAssetGroupCode()+": Message received from Q");
			if(inputObj!=null)
			{
				iLogger.info("AssetGroup - "+inputObj.getAssetGroupCode()+"Invoke Webservice Client");
				new AssetGroupClient().invokeAssetGroupService(inputObj);
				iLogger.info("AssetGroup - "+inputObj.getAssetGroupCode()+"Returned from Webservice Client");
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			fLogger.fatal("AssetGroup Exception :"+e.getMessage());
			
			if(inputObj!=null)
			{
				String assetGroupName="",assetGroupCode=""; 

				if(inputObj.getAssetGroupName()!=null)
					assetGroupName=inputObj.getAssetGroupName();
				if(inputObj.getAssetGroupCode()!=null)
					assetGroupCode=inputObj.getAssetGroupCode();

				String messageString = assetGroupName+"|"+assetGroupCode;
				ErrorMessageHandler errorHandler = new ErrorMessageHandler();
				//errorHandler.handleErrorMessages(msg.getMessageId(), messageString, msg.getFileRef(), msg.getProcess(), msg.getReprocessJobCode(), e.getMessage());
				errorHandler.handleErrorMessages_new(inputObj.getMessageId(), messageString, inputObj.getFileRef(), inputObj.getProcess(), inputObj.getReprocessJobCode(), e.getMessage(),"0002","Service Invokation");

				//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
				if(inputObj.getMessageId().split("\\|").length<2){
					String uStatus=CommonUtil.updateInterfaceLogDetails(inputObj.getFileRef(), "failureCount", 1);
					iLogger.info("Status on updating data into interface log details table :"+uStatus);
				}

			}
		}

		return null;
	}

	//DF20171212:KO369761-Changing interfaces queues to Kafka from Hornet queues.
	public String handleAssetGroupDetailsToKafkaQueue(String topicName, AssetGroupInputContract msg) throws Exception {

		String qDropStatus="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Producer<String, String> producer = null;


		try{
			iLogger.info("AssetGroup - "+msg.getAssetGroupCode()+" : Send message to Q");

			Properties props = new Properties();
			props.put("metadata.broker.list", "localhost:9092");
			props.put("serializer.class", "kafka.serializer.StringEncoder");
			props.put("request.required.acks", "1");

			ProducerConfig config = new ProducerConfig(props);
			producer = new Producer<String, String>(config);

			if(producer != null){
				iLogger.info("producer was created :"+producer);
			}

			iLogger.info("AssetGroup - "+msg.getAssetGroupCode()+" : Send message to Q");

			String AssetGroupInputJson = null;
			if(msg !=null){
				ObjectMapper mapper = new ObjectMapper();
				AssetGroupInputJson = mapper.writeValueAsString(msg);
			}
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(topicName, AssetGroupInputJson);
			producer.send(data);
			iLogger.info("AssetGroup - "+msg.getAssetGroupCode()+": Message Sent to Q for EngineNum");

		} //DF20141028 - Rajani Nagaraju - Move the packet to Fault Details if the message cannot be put to Queue
		catch(Exception e)
		{
			qDropStatus = "FAILURE";
			String assetGroupName="",assetGroupCode=""; 

			if(msg.getAssetGroupName()!=null)
				assetGroupName=msg.getAssetGroupName();
			if(msg.getAssetGroupCode()!=null)
				assetGroupCode=msg.getAssetGroupCode();

			String messageString = assetGroupName+"|"+assetGroupCode;
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			//errorHandler.handleErrorMessages(msg.getMessageId(), messageString, msg.getFileRef(), msg.getProcess(), msg.getReprocessJobCode(), e.getMessage());
			errorHandler.handleErrorMessages_new(msg.getMessageId(), messageString, msg.getFileRef(), msg.getProcess(), msg.getReprocessJobCode(), e.getMessage(),"0001","Producer");
            
            //DF20180207:KO369761 - Inserting datacount to log details table for tracing.
  			if(msg.getMessageId().split("\\|").length<2){
  				String uStatus=CommonUtil.updateInterfaceLogDetails(msg.getFileRef(), "failureCount", 1);
  				iLogger.info("Status on updating data into interface log details table :"+uStatus);
  			}
		}

		finally
		{         

			// ALWAYS close your connection in a finally block to avoid leaks.
			// Closing connection also takes care of closing its related objects e.g. sessions.
			producer.close();
		}

		return qDropStatus;
	}


	public String handleAssetGroupDetails(String queuePath, AssetGroupInputContract msg) throws Exception
	{
		String destinationName = queuePath;
		String qDropStatus="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Context ic = null;
		ConnectionFactory cf = null;
		Connection connection =  null;
		Session session =null;

		try
		{         
			iLogger.info(msg.getAssetGroupCode()+" : Connect to Q");
			ic = getInitialContext();

			cf = (ConnectionFactory)ic.lookup("jms/RemoteConnectionFactory");
			Queue queue = (Queue)ic.lookup(destinationName);

			connection = cf.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer publisher = session.createProducer(queue);

			//DF20160502 @Roopa AssetGroupDetails queue consumer changes to avoid sending message to consumer each time
			/*MessageConsumer subscriber = session.createConsumer(queue);

            subscriber.setMessageListener(this);*/
			connection.start();
			iLogger.info(msg.getAssetGroupCode()+" : Send message to Q");
			ObjectMessage message = session.createObjectMessage(msg);
			publisher.send(message);
			Thread.currentThread().sleep(3000);
			iLogger.info(msg.getAssetGroupCode()+" : Message sent successfully to Q");
		}

		//DF20141028 - Rajani Nagaraju - Move the packet to Fault Details if the message cannot be put to Queue
		catch(Exception e)
		{
			//qDropStatus = "FAILURE";
			String assetGroupName="",assetGroupCode=""; 

			if(msg.getAssetGroupName()!=null)
				assetGroupName=msg.getAssetGroupName();
			if(msg.getAssetGroupCode()!=null)
				assetGroupCode=msg.getAssetGroupCode();

			String messageString = assetGroupName+"|"+assetGroupCode;
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

		return qDropStatus;
	}


	//Automatically Reads the object from the Queue when an object is added to the Queue
	/*public synchronized void onMessage(Message message)
    {
    	//ClassLoader originalTCCL = Thread.currentThread().getContextClassLoader();
		AssetGroupInputContract inputObj=null;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger; 	
		try 
		{

			//Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			ObjectMessage Obj = (ObjectMessage) message;
			inputObj = (AssetGroupInputContract)Obj.getObject();

			iLogger.info(inputObj.getAssetGroupCode()+": Message received from Q");
			if(inputObj!=null)
			{
				iLogger.info(inputObj.getAssetGroupCode()+"Invoke Webservice Client");
				new AssetGroupClient().invokeAssetGroupService(inputObj);
				iLogger.info(inputObj.getAssetGroupCode()+"Returned from Webservice Client");
			}
		}

		catch (JMSException e) 
		{
			fLogger.fatal("JMS Exception:"+e);

		} 

   }*/


	//DF20160428 @Roopa AssetGroupDetails queue consumer takes all messages from queue at a time
	public String consumeMessage(String queuePath) throws Exception{

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		AssetGroupInputContract inputObj=null;

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

			iLogger.info("START AssetGroupDetails consumerMessage queuePath::"+queuePath);

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

					inputObj = (AssetGroupInputContract)objMsg.getObject();

					iLogger.info(inputObj.getAssetGroupCode()+": Message received from Q");
					if(inputObj!=null)
					{
						iLogger.info(inputObj.getAssetGroupCode()+"Invoke Webservice Client");
						new AssetGroupClient().invokeAssetGroupService(inputObj);
						iLogger.info(inputObj.getAssetGroupCode()+"Returned from Webservice Client");
					}


				}

			}

			iLogger.info("END AssetGroupDetails consumerMessage queuePath::"+queuePath);

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
