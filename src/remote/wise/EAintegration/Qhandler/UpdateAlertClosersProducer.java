package remote.wise.EAintegration.Qhandler;

import java.util.HashMap;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.StaticProperties;

//CR366 - 20221114 - Prasad - Closure of Service Alerts
public class UpdateAlertClosersProducer implements  Runnable {

	Thread t;
	
	HashMap<String,String> payloadMap;

	public UpdateAlertClosersProducer()
	{

	}
	public UpdateAlertClosersProducer(HashMap<String,String> payloadMap)
	{
		t = new Thread(this, "Kafka Publisher");
		this.payloadMap=payloadMap;
		t.start();
	}

	@Override
	public void run() {	
		Logger iLogger = InfoLoggerClass.logger;

		//iLogger.info(this.txnKey+":KafkaPublisher:"+"Publish the Message to required topics");
		long startTime = System.currentTimeMillis();
		String publishStatus = publishServiceAlertCloserData(this.payloadMap);
		long endTime = System.currentTimeMillis();
		iLogger.info(":UpdateAlertClosersProducer :: KafkaPublisher:"+"Publish the Message to required topics: Status:"+publishStatus+"; Turn around Time (in ms):"+(endTime-startTime));

	}

	public String publishServiceAlertCloserData(HashMap<String,String> payloadMap)
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
			fLogger.fatal("UpdateAlertClosersProducer :::KafkaPublisher:"+"Error in intializing property File :"+e.getMessage());
			return "FAILURE";
		}	
//		String topicName = prop.getProperty("ServiceAlertClosureTopic");
		String topicName = "ServiceAlertClosureTopic";
		try
		{
				
			String payloadMapJson = new Gson().toJson(payloadMap); 
			iLogger.info("UpdateAlertClosersProducer ::KafkaPublisher:"+"Topic Name:"+topicName+"; Message:"+payloadMapJson);
			String status = publishMessage(topicName,payloadMapJson);
			if(!( status.equalsIgnoreCase("SUCCESS")))
				publishStatus=status;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			publishStatus="FAILURE";
			fLogger.fatal(":UpdateAlertClosersProducer :: KafkaPublisher:"+"Exception in pushing the message for publish to Topic:"+topicName+"; Persisting payload in OrientDB:"+e.getMessage());			
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

    	iLogger.info(":UpdateAlertClosersProducer ::KafkaPublisher:"+message +": topicName :"+topicName+" Put To Kafka Producer : START");

    	try{
    		Properties props = new Properties();
    		props.put("metadata.broker.list", "10.210.196.206:9092");
    		props.put("serializer.class", "kafka.serializer.StringEncoder");
    		props.put("request.required.acks", "0");
    		System.out.println(props);
    		ProducerConfig config = new ProducerConfig(props);
    		System.out.println(config);
    		producer = new Producer<String, String>(config);    		
    		if(producer != null){
    			iLogger.info("UpdateAlertClosersProducer :: producer was created :"+producer);
    		}    		
    		iLogger.info(":UpdateAlertClosersProducer :: publishMessage to KafkaPublisher:"+message);
    		KeyedMessage<String, String> data = new KeyedMessage<String, String>(topicName, message);
    		iLogger.info(":UpdateAlertClosersProducer :: publishMessage to KafkaPublisher data:" +data);
    		producer.send(data);
    		producer.close();    		
    		iLogger.info("UpdateAlertClosersProducer :: publishMessage to KafkaPublisher data  ::: END");
    		long endTime=System.currentTimeMillis();
    		iLogger.info("UpdateAlertClosersProducer :: publishMessage to KafkaPublisher data:"+message+":" +"publishMessage to KafkaPublisher : END :"+(endTime-startTime)); 
    	}catch (Exception e) {
    		e.printStackTrace();
			status="FAILURE";
			fLogger.fatal("UpdateAlertClosersProducer :KafkaPublisher:"+"Exception in publishing the message to Topic:"+topicName+"; Persisting payload in OrientDB:"+e.getMessage());	
    	}
		return status;
	}

}
