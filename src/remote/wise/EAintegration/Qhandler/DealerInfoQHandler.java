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

import remote.wise.EAintegration.EAclient.AssetTypeClient;
import remote.wise.EAintegration.EAclient.DealerInfoClient;
import remote.wise.EAintegration.EAclient.SaleFromD2CClient;
import remote.wise.EAintegration.dataContract.AssetTypeInputContract;
import remote.wise.EAintegration.dataContract.DealerInfoInputContract;
import remote.wise.EAintegration.dataContract.SaleFromD2CInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

public class DealerInfoQHandler implements Callable
{
	/*Logger fatalError = Logger.getLogger("fatalErrorLogger");
	Logger infoLogger = Logger.getLogger("infoLogger");*/

	String dealerInfoInput = null;

	//DF20171212:KO369761-Changing interfaces queues to Kafka from Hornet queues.
	@Override
	public Object call() throws Exception {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		ObjectMapper mapper = new ObjectMapper();
		DealerInfoInputContract inputObj=null;
		HashMap<String,String> dealerInfoInputMap = null;

		try{
			dealerInfoInputMap = new Gson().fromJson(dealerInfoInput, new TypeToken<HashMap<String, String>>(){}.getType());
			inputObj = mapper.convertValue(dealerInfoInputMap, DealerInfoInputContract.class);
			iLogger.info("DealerInfo - "+inputObj.getDealerCode()+": Message received from Q");
			if(inputObj!=null)
			{
				iLogger.info("DealerInfo - "+inputObj.getDealerCode()+"Invoke Webservice Client");
				new DealerInfoClient().invokeDealerInfoService(inputObj);
				iLogger.info("DealerInfo - "+inputObj.getDealerCode()+"Returned from Webservice Client");
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			fLogger.fatal("DealerInfo Exception :"+e.getMessage());
			
			if(inputObj!=null)
			{
				String dealerCode=inputObj.getDealerCode();
				String dealerName=inputObj.getDealerName();
				String addressLine1=inputObj.getAddressLine1();
				String addressLine2=inputObj.getAddressLine2();
				String city=inputObj.getCity();
				String zipCode=inputObj.getZipCode();
				String state=inputObj.getState();
				String zone=inputObj.getZone();
				String country=inputObj.getCountry();
				String email=inputObj.getEmail();
				String contactNumber=inputObj.getContactNumber();
				String fax=inputObj.getFax();
				String JcbRoCode=inputObj.getJcbRoCode();

				if( dealerCode == null || dealerCode.equals("") || (!(dealerCode.replaceAll("\\s","").length()>0)) )
					dealerCode="";
				if( dealerName == null || dealerName.equals("") || (!(dealerName.replaceAll("\\s","").length()>0)) )
					dealerName="";
				if( addressLine1 == null || addressLine1.equals("") || (!(addressLine1.replaceAll("\\s","").length()>0)) )
					addressLine1="";
				if( addressLine2 == null || addressLine2.equals("") || (!(addressLine2.replaceAll("\\s","").length()>0)) )
					addressLine2="";
				if( city == null || city.equals("") || (!(city.replaceAll("\\s","").length()>0)) )
					city="";
				if( zipCode == null || zipCode.equals("") || (!(zipCode.replaceAll("\\s","").length()>0)) )
					zipCode="";
				if( state == null || state.equals("") || (!(state.replaceAll("\\s","").length()>0)) ) 
					state="";
				if( zone == null || zone.equals("") || (!(zone.replaceAll("\\s","").length()>0)) )
					zone="";
				if( country == null || country.equals("") || (!(country.replaceAll("\\s","").length()>0)) )
					country="";
				if( email == null || email.equals("") || (!(email.replaceAll("\\s","").length()>0)) )
					email="";
				if( contactNumber == null || contactNumber.equals("") || (!(contactNumber.replaceAll("\\s","").length()>0)) )
					contactNumber="";
				if( fax == null || fax.equals("") || (!(fax.replaceAll("\\s","").length()>0)) )
					fax="";
				if( JcbRoCode == null || JcbRoCode.equals("") || (!(JcbRoCode.replaceAll("\\s","").length()>0)) )
					JcbRoCode="";

				String messageString = dealerCode+"|"+dealerName+"|"+addressLine1+"|"+addressLine2+"|"+city+"|"+zipCode+"|"+state+"|" +
						zone+"|"+country+"|"+email+"|"+contactNumber+"|"+fax+"|"+JcbRoCode;

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
	public String handleDealerInfoToKafkaQueue(String topicName, DealerInfoInputContract msg) throws Exception {

		String qDropStatus="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Producer<String, String> producer = null;


		try{
			iLogger.info("DealerInfo - "+msg.getDealerCode()+" : Send message to Q");

			Properties props = new Properties();
			props.put("metadata.broker.list", "localhost:9092");
			props.put("serializer.class", "kafka.serializer.StringEncoder");
			props.put("request.required.acks", "1");

			ProducerConfig config = new ProducerConfig(props);
			producer = new Producer<String, String>(config);

			if(producer != null){
				iLogger.info("producer was created :"+producer);
			}

			iLogger.info("DealerInfo - "+msg.getDealerCode()+" : Send message to Q");

			String SaleFromD2CInputJson = null;
			if(msg !=null){
				ObjectMapper mapper = new ObjectMapper();
				SaleFromD2CInputJson = mapper.writeValueAsString(msg);
			}
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(topicName, SaleFromD2CInputJson);
			producer.send(data);
			iLogger.info("DealerInfo - "+msg.getDealerCode()+": Message Sent to Q for EngineNum");
		}//DF20141028 - Rajani Nagaraju - Move the packet to Fault Details if the message cannot be put to Queue
		catch(Exception e)
		{
			//qDropStatus = "FAILURE";

			String dealerCode=msg.getDealerCode();
			String dealerName=msg.getDealerName();
			String addressLine1=msg.getAddressLine1();
			String addressLine2=msg.getAddressLine2();
			String city=msg.getCity();
			String zipCode=msg.getZipCode();
			String state=msg.getState();
			String zone=msg.getZone();
			String country=msg.getCountry();
			String email=msg.getEmail();
			String contactNumber=msg.getContactNumber();
			String fax=msg.getFax();
			String JcbRoCode=msg.getJcbRoCode();

			if( dealerCode == null || dealerCode.equals("") || (!(dealerCode.replaceAll("\\s","").length()>0)) )
				dealerCode="";
			if( dealerName == null || dealerName.equals("") || (!(dealerName.replaceAll("\\s","").length()>0)) )
				dealerName="";
			if( addressLine1 == null || addressLine1.equals("") || (!(addressLine1.replaceAll("\\s","").length()>0)) )
				addressLine1="";
			if( addressLine2 == null || addressLine2.equals("") || (!(addressLine2.replaceAll("\\s","").length()>0)) )
				addressLine2="";
			if( city == null || city.equals("") || (!(city.replaceAll("\\s","").length()>0)) )
				city="";
			if( zipCode == null || zipCode.equals("") || (!(zipCode.replaceAll("\\s","").length()>0)) )
				zipCode="";
			if( state == null || state.equals("") || (!(state.replaceAll("\\s","").length()>0)) ) 
				state="";
			if( zone == null || zone.equals("") || (!(zone.replaceAll("\\s","").length()>0)) )
				zone="";
			if( country == null || country.equals("") || (!(country.replaceAll("\\s","").length()>0)) )
				country="";
			if( email == null || email.equals("") || (!(email.replaceAll("\\s","").length()>0)) )
				email="";
			if( contactNumber == null || contactNumber.equals("") || (!(contactNumber.replaceAll("\\s","").length()>0)) )
				contactNumber="";
			if( fax == null || fax.equals("") || (!(fax.replaceAll("\\s","").length()>0)) )
				fax="";
			if( JcbRoCode == null || JcbRoCode.equals("") || (!(JcbRoCode.replaceAll("\\s","").length()>0)) )
				JcbRoCode="";

			String messageString = dealerCode+"|"+dealerName+"|"+addressLine1+"|"+addressLine2+"|"+city+"|"+zipCode+"|"+state+"|" +
					zone+"|"+country+"|"+email+"|"+contactNumber+"|"+fax+"|"+JcbRoCode;

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

	public String handleDealerInfo(String queuePath, DealerInfoInputContract msg) throws Exception
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
			iLogger.info(msg.getDealerName()+" : Connect to Q");
			ic = getInitialContext();

			cf = (ConnectionFactory)ic.lookup("jms/RemoteConnectionFactory");
			Queue queue = (Queue)ic.lookup(destinationName);

			connection = cf.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer publisher = session.createProducer(queue);

			//DF20160502 @Roopa DealerInfo queue consumer changes to avoid sending message to consumer each time
			/*MessageConsumer subscriber = session.createConsumer(queue);

            subscriber.setMessageListener(this);*/
			connection.start();
			iLogger.info(msg.getDealerName()+" : Send message to Q");
			ObjectMessage message = session.createObjectMessage(msg);
			publisher.send(message);
			Thread.currentThread().sleep(3000);
			iLogger.info(msg.getDealerName()+" : Message sent successfully to Q");
		}


		//DF20141028 - Rajani Nagaraju - Move the packet to Fault Details if the message cannot be put to Queue
		catch(Exception e)
		{
			//qDropStatus = "FAILURE";

			String dealerCode=msg.getDealerCode();
			String dealerName=msg.getDealerName();
			String addressLine1=msg.getAddressLine1();
			String addressLine2=msg.getAddressLine2();
			String city=msg.getCity();
			String zipCode=msg.getZipCode();
			String state=msg.getState();
			String zone=msg.getZone();
			String country=msg.getCountry();
			String email=msg.getEmail();
			String contactNumber=msg.getContactNumber();
			String fax=msg.getFax();
			String JcbRoCode=msg.getJcbRoCode();

			if( dealerCode == null || dealerCode.equals("") || (!(dealerCode.replaceAll("\\s","").length()>0)) )
				dealerCode="";
			if( dealerName == null || dealerName.equals("") || (!(dealerName.replaceAll("\\s","").length()>0)) )
				dealerName="";
			if( addressLine1 == null || addressLine1.equals("") || (!(addressLine1.replaceAll("\\s","").length()>0)) )
				addressLine1="";
			if( addressLine2 == null || addressLine2.equals("") || (!(addressLine2.replaceAll("\\s","").length()>0)) )
				addressLine2="";
			if( city == null || city.equals("") || (!(city.replaceAll("\\s","").length()>0)) )
				city="";
			if( zipCode == null || zipCode.equals("") || (!(zipCode.replaceAll("\\s","").length()>0)) )
				zipCode="";
			if( state == null || state.equals("") || (!(state.replaceAll("\\s","").length()>0)) ) 
				state="";
			if( zone == null || zone.equals("") || (!(zone.replaceAll("\\s","").length()>0)) )
				zone="";
			if( country == null || country.equals("") || (!(country.replaceAll("\\s","").length()>0)) )
				country="";
			if( email == null || email.equals("") || (!(email.replaceAll("\\s","").length()>0)) )
				email="";
			if( contactNumber == null || contactNumber.equals("") || (!(contactNumber.replaceAll("\\s","").length()>0)) )
				contactNumber="";
			if( fax == null || fax.equals("") || (!(fax.replaceAll("\\s","").length()>0)) )
				fax="";
			if( JcbRoCode == null || JcbRoCode.equals("") || (!(JcbRoCode.replaceAll("\\s","").length()>0)) )
				JcbRoCode="";

			String messageString = dealerCode+"|"+dealerName+"|"+addressLine1+"|"+addressLine2+"|"+city+"|"+zipCode+"|"+state+"|" +
					zone+"|"+country+"|"+email+"|"+contactNumber+"|"+fax+"|"+JcbRoCode;

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
		DealerInfoInputContract inputObj=null;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		try 
		{

			//Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			ObjectMessage Obj = (ObjectMessage) message;
			inputObj = (DealerInfoInputContract)Obj.getObject();

			iLogger.info(inputObj.getDealerName()+": Message received from Q");
			if(inputObj!=null)
			{
				iLogger.info(inputObj.getDealerName()+"Invoke Webservice Client");
				new DealerInfoClient().invokeDealerInfoService(inputObj);
				iLogger.info(inputObj.getDealerName()+"Returned from Webservice Client");
			}
		}
		catch (JMSException e) 
		{
			fLogger.fatal("JMS Exception:"+e);

		} 

   }*/


	//DF20160502 @Roopa DealerInfo queue consumer takes all messages from queue at a time
	public String consumeMessage(String queuePath) throws Exception{

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		DealerInfoInputContract inputObj=null;

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

			iLogger.info("START DealerInfo consumerMessage queuePath::"+queuePath);

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

					inputObj = (DealerInfoInputContract)objMsg.getObject();

					iLogger.info(inputObj.getDealerName()+": Message received from Q");
					if(inputObj!=null)
					{
						iLogger.info(inputObj.getDealerName()+"Invoke Webservice Client");
						new DealerInfoClient().invokeDealerInfoService(inputObj);
						iLogger.info(inputObj.getDealerName()+"Returned from Webservice Client");
					}


				}

			}

			iLogger.info("END DealerInfo consumerMessage queuePath::"+queuePath);

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
		// p.put(Context.PROVIDER_URL, "remote://10.106.68.9:4447");
		p.put(Context.PROVIDER_URL, "remote://localhost:4447");
		return new javax.naming.InitialContext(p);
	}
}
