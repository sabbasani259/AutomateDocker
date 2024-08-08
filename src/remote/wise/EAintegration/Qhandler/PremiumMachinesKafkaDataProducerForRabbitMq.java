package remote.wise.EAintegration.Qhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.StaticProperties;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class PremiumMachinesKafkaDataProducerForRabbitMq implements Runnable{

	Thread t;
	List<HashMap<String, String>> payloadMap;

	public PremiumMachinesKafkaDataProducerForRabbitMq()
	{

	}
	public PremiumMachinesKafkaDataProducerForRabbitMq(List<HashMap<String, String>> payloadMap)
	{
		t = new Thread(this, "Kafka Publisher");
		this.payloadMap=payloadMap;
		t.start();
	}

	@Override
	public void run() {	
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String jsonDataString = null;
		long startTime = System.currentTimeMillis();
		try {
			jsonDataString = new ObjectMapper().writer().writeValueAsString(this.payloadMap);
		} catch (Exception e) {
			fLogger.fatal("PremiumMachinesKafkaDataProducerForRabbitMq :::KafkaPublisher:"+"Error in jsonstring conversion of payload :"+e.getMessage());
			e.printStackTrace();
		}
		iLogger.info("PremiumMachinesKafkaDataProducerForRabbitMq payload for kafka : "+jsonDataString);
		String publishStatus = publishMachineData(jsonDataString);
		long endTime = System.currentTimeMillis();
		iLogger.info(":PremiumMachinesKafkaDataProducerForRabbitMq :: KafkaPublisher:"+"Publish the Message to required topics: Status:"+publishStatus+"; Turn around Time (in ms):"+(endTime-startTime));
	}

	public String publishMachineData(String payloadMap)
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		String publishStatus = "SUCCESS";		
		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("PremiumMachinesKafkaDataProducerForRabbitMq :::KafkaPublisher:"+"Error in intializing property File :"+e.getMessage());
			return "FAILURE";
		}	
		//add below parameter in configuration property file
		String topicName = prop.getProperty("premiumKafaTopicForRabbitMq");
		try
		{		
			//String payloadMapJson = new Gson().toJson(payloadMap); 
			iLogger.info("PremiumMachinesKafkaDataProducerForRabbitMq ::KafkaPublisher:"+"Topic Name:"+topicName+"; Message:"+payloadMap);
			String status = publishMessage(topicName,payloadMap);
			if(!( status.equalsIgnoreCase("SUCCESS")))
				publishStatus=status;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			publishStatus="FAILURE";
			fLogger.fatal(":PremiumMachinesKafkaDataProducerForRabbitMq :: KafkaPublisher:"+"Exception in pushing the message for publish to Topic:"+topicName+"; Persisting payload in OrientDB:"+e.getMessage());			
			return "FAILURE";	
		}
		return publishStatus;
	}
	public String publishMessage(String topicName, String message)
	{	
		String status="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Producer<String, String> producer = null;
    	long startTime = System.currentTimeMillis();

    	iLogger.info(":PremiumMachinesKafkaDataProducerForRabbitMq ::KafkaPublisher:"+message +": topicName :"+topicName+" Put To Kafka Producer : START");

    	try{
    		Properties props = new Properties();
    		props.put("metadata.broker.list", "localhost:9092");
    		props.put("serializer.class", "kafka.serializer.StringEncoder");
    		props.put("request.required.acks", "0");
    		System.out.println(props);
    		ProducerConfig config = new ProducerConfig(props);
    		System.out.println(config);
    		producer = new Producer<String, String>(config);    		
    		if(producer != null){
    			iLogger.info("PremiumMachinesKafkaDataProducerForRabbitMq :: producer was created :"+producer);
    		}    		
    		iLogger.info(":PremiumMachinesKafkaDataProducerForRabbitMq :: publishMessage to KafkaPublisher:"+message);
    		KeyedMessage<String, String> data = new KeyedMessage<String, String>(topicName, message);
    		iLogger.info(":PremiumMachinesKafkaDataProducerForRabbitMq :: publishMessage to KafkaPublisher data:" +data);
    		producer.send(data);
    		producer.close();    		
    		iLogger.info("PremiumMachinesKafkaDataProducerForRabbitMq :: publishMessage to KafkaPublisher data  ::: END");
    		long endTime=System.currentTimeMillis();
    		iLogger.info("PremiumMachinesKafkaDataProducerForRabbitMq :: publishMessage to KafkaPublisher data:"+message+":" +"publishMessage to KafkaPublisher : END :"+(endTime-startTime)); 
    	}catch (Exception e) {
    		e.printStackTrace();
			status="FAILURE";
			fLogger.fatal("PremiumMachinesKafkaDataProducerForRabbitMq :KafkaPublisher:"+"Exception in publishing the message to Topic:"+topicName+"; Persisting payload in OrientDB:"+e.getMessage());	
    	}
		return status;
	}

}
