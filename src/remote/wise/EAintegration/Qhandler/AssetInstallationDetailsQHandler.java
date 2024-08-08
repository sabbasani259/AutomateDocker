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

import remote.wise.EAintegration.EAclient.AssetInstallationDetailsClient;
import remote.wise.EAintegration.dataContract.AssetInstallationDetailsInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

public class AssetInstallationDetailsQHandler implements Callable
{
	/*Logger fatalError = Logger.getLogger("fatalErrorLogger");
	Logger infoLogger = Logger.getLogger("infoLogger");*/

	String assetInstallationInput = null;

	//DF20171212:KO369761-Changing interfaces queues to Kafka from Hornet queues.
	@Override
	public Object call() throws Exception {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		ObjectMapper mapper = new ObjectMapper();

		AssetInstallationDetailsInputContract inputObj=null;
		HashMap<String,String> assetInstallationInputMap = null;

		try{
			assetInstallationInputMap = new Gson().fromJson(assetInstallationInput, new TypeToken<HashMap<String, String>>(){}.getType());
			inputObj = mapper.convertValue(assetInstallationInputMap, AssetInstallationDetailsInputContract.class);
			iLogger.info("AssetInstallation - "+inputObj.getSerialNumber()+": Message received from Q");
			if(inputObj!=null)
			{
				iLogger.info("AssetInstallation - "+inputObj.getSerialNumber()+"Invoke Webservice Client");
				new AssetInstallationDetailsClient().invokeAssetInstallationService(inputObj);
				iLogger.info("AssetInstallation - "+inputObj.getSerialNumber()+"Returned from Webservice Client");
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			fLogger.fatal("AssetInstallation Exception :"+e.getMessage());

			if(inputObj !=null){
				String serialNumber="",installationDate="", dealerCode="",customerCode="";
				if(inputObj.getSerialNumber()!=null)
					serialNumber=inputObj.getSerialNumber();
				if(inputObj.getInstallationDate()!=null)
					installationDate=inputObj.getInstallationDate();
				if(inputObj.getDealerCode()!=null)
					dealerCode=inputObj.getDealerCode();
				if(inputObj.getCustomerCode()!=null)
					customerCode=inputObj.getCustomerCode();

				String messageString = serialNumber+"|"+installationDate+"|"+dealerCode+"|"+customerCode;
				fLogger.fatal("AssetInstallation - "+"Error in AssetInstallationDetailsQHandler::messageString:"+messageString);
				ErrorMessageHandler errorHandler = new ErrorMessageHandler();
				//errorHandler.handleErrorMessages(inputObj.getMessageId(), messageString, inputObj.getFileRef(), inputObj.getProcess(), inputObj.getReprocessJobCode(), e.getMessage());
				//DF20180207--@Maniratnam #Interfaces Backtracking :inserting into fault details table with new parameters errorcode and rejection point
				errorHandler.handleErrorMessages_new(inputObj.getMessageId(),
						messageString, inputObj.getFileRef(),
						inputObj.getProcess(), inputObj.getReprocessJobCode(),
						e.getMessage(), "0002", "Service Invocation");
				
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
	public String handleAssetInstallationToKafkaQueue(String topicName, AssetInstallationDetailsInputContract msg) throws Exception {

		String qDropStatus="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Producer<String, String> producer = null;


		try{
			iLogger.info("Asset Installation - "+msg.getSerialNumber()+" : Send message to Q");

			Properties props = new Properties();
			props.put("metadata.broker.list", "localhost:9092");
			props.put("serializer.class", "kafka.serializer.StringEncoder");
			//KO369761 - setting acknowledgment as 1 because for the first message was being ignored by the consumer.
			props.put("request.required.acks", "1");

			ProducerConfig config = new ProducerConfig(props);
			producer = new Producer<String, String>(config);

			if(producer != null){
				iLogger.info("producer was created :"+producer);
			}

			iLogger.info("Asset Installation - "+msg.getSerialNumber()+" : Send message to Q");

			String AssetInstallationInputJson = null;
			if(msg !=null){
				ObjectMapper mapper = new ObjectMapper();
				AssetInstallationInputJson = mapper.writeValueAsString(msg);
			}
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(topicName, AssetInstallationInputJson);
			producer.send(data);
			iLogger.info("Asset Installation - "+msg.getSerialNumber()+": Message Sent to Q for EngineNum");

		}        //DF20141028 - Rajani Nagaraju - Move the packet to Fault Details if the message cannot be put to Queue
		catch(Exception e)
		{
			qDropStatus = "FAILURE";
			String serialNumber="",installationDate="", dealerCode="",customerCode="";


			if(msg.getSerialNumber()!=null)
				serialNumber=msg.getSerialNumber();
			if(msg.getInstallationDate()!=null)
				installationDate=msg.getInstallationDate();
			if(msg.getDealerCode()!=null)
				dealerCode=msg.getDealerCode();
			if(msg.getCustomerCode()!=null)
				customerCode=msg.getCustomerCode();

			String messageString = serialNumber+"|"+installationDate+"|"+dealerCode+"|"+customerCode;

			fLogger.fatal("Asset Installation - "+messageString+" Exception: "+e +": Put the record into fault details");
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			//errorHandler.handleErrorMessages(msg.getMessageId(), messageString, msg.getFileRef(), msg.getProcess(), msg.getReprocessJobCode(), e.getMessage());
			//DF20180207--@Maniratnam #Interfaces Backtracking :inserting into fault details table with new parameters errorcode and rejection point
			errorHandler.handleErrorMessages_new(msg.getMessageId(),
					messageString, msg.getFileRef(), msg.getProcess(),
					msg.getReprocessJobCode(), e.getMessage(), "0001", "Producer");
			
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


	public String handleAssetInstallationDetails(String queuePath, AssetInstallationDetailsInputContract msg) throws Exception
	{
		String destinationName = queuePath;
		String qDropStatus="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Context ic = null;
		ConnectionFactory cf = null;
		Connection connection =  null;
		Session session =null;

		try
		{         
			iLogger.info("Asset Installation - "+msg.getSerialNumber()+" : Connect to Q");
			ic = getInitialContext();

			cf = (ConnectionFactory)ic.lookup("jms/RemoteConnectionFactory");
			Queue queue = (Queue)ic.lookup(destinationName);

			connection = cf.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer publisher = session.createProducer(queue);

			//DF20160428 @Roopa AssetInstallationDetails queue consumer changes to avoid sending message to consumer each time
			/*MessageConsumer subscriber = session.createConsumer(queue);

            subscriber.setMessageListener(this);*/

			connection.start();
			iLogger.info("Asset Installation - "+msg.getSerialNumber()+" : Send message to Q");
			ObjectMessage message = session.createObjectMessage(msg);
			publisher.send(message);
			Thread.currentThread().sleep(3000);
			iLogger.info("Asset Installation - "+msg.getSerialNumber()+" : Message sent successfully to Q");
		}

		//DF20141028 - Rajani Nagaraju - Move the packet to Fault Details if the message cannot be put to Queue
		catch(Exception e)
		{
			//	qDropStatus = "FAILURE";
			String serialNumber="",installationDate="", dealerCode="",customerCode="";


			if(msg.getSerialNumber()!=null)
				serialNumber=msg.getSerialNumber();
			if(msg.getInstallationDate()!=null)
				installationDate=msg.getInstallationDate();
			if(msg.getDealerCode()!=null)
				dealerCode=msg.getDealerCode();
			if(msg.getCustomerCode()!=null)
				customerCode=msg.getCustomerCode();

			String messageString = serialNumber+"|"+installationDate+"|"+dealerCode+"|"+customerCode;

			fLogger.fatal("Asset Installation - "+messageString+" Exception: "+e +": Put the record into fault details");
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
		AssetInstallationDetailsInputContract inputObj=null;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger; 	
		try 
		{

			//Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			ObjectMessage Obj = (ObjectMessage) message;
			inputObj = (AssetInstallationDetailsInputContract)Obj.getObject();

			iLogger.info("Asset Installation - "+inputObj.getSerialNumber()+": Message received from Q");
			if(inputObj!=null)
			{
				iLogger.info("Asset Installation - "+inputObj.getSerialNumber()+"Invoke Webservice Client");
				new AssetInstallationDetailsClient().invokeAssetInstallationService(inputObj);
				iLogger.info("Asset Installation - "+inputObj.getSerialNumber()+"Returned from Webservice Client");
			}
		}

		catch (JMSException e) 
		{
			fLogger.fatal("Asset Installation - "+"JMS Exception:"+e);

		} 

   }*/


	//DF20160502 @Roopa AssetInstallationDetails queue consumer takes all messages from queue at a time
	public String consumeMessage(String queuePath) throws Exception{

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		AssetInstallationDetailsInputContract inputObj=null;

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

			iLogger.info("START AssetInstallationDetails consumerMessage queuePath::"+queuePath);

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

					inputObj = (AssetInstallationDetailsInputContract)objMsg.getObject();

					iLogger.info("Asset Installation - "+inputObj.getSerialNumber()+": Message received from Q");
					if(inputObj!=null)
					{
						iLogger.info("Asset Installation - "+inputObj.getSerialNumber()+"Invoke Webservice Client");
						new AssetInstallationDetailsClient().invokeAssetInstallationService(inputObj);
						iLogger.info("Asset Installation - "+inputObj.getSerialNumber()+"Returned from Webservice Client");
					}


				}

			}

			iLogger.info("END AssetInstallationDetails consumerMessage queuePath::"+queuePath);

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
			fLogger.fatal("Asset Installation - "+"Could not close connection " + con +" exception was " + jmse);
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
