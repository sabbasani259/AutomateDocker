package remote.wise.EAintegration.Qhandler;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.StaticProperties;

/*
 * CR271 - VidyaSagar -20220225 - send deactivated users data to kafka producer 
 */

//CR271.sn
public class UserDeactivationProducer implements Runnable{

	Thread t;
	String loginId;

	public UserDeactivationProducer()
	{

	}
	public UserDeactivationProducer(String loginId)
	{
		t = new Thread(this, "Kafka Publisher for UserDeactivationProducer");
		this.loginId=loginId;
		t.start();
	}

	@Override
	public void run() {	
		Logger iLogger = InfoLoggerClass.logger;

		//iLogger.info(this.txnKey+":KafkaPublisher:"+"Publish the Message to required topics");
		long startTime = System.currentTimeMillis();
		String publishStatus = publishMachineData(this.loginId);
		long endTime = System.currentTimeMillis();
		iLogger.info(this.loginId+":UserDeactivationProducer :: KafkaPublisher:"+"Publish the Message to required topics: Status:"+publishStatus+"; Turn around Time (in ms):"+(endTime-startTime));
	}

	public String publishMachineData(String loginId)
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
			fLogger.fatal(loginId+"UserDeactivationProducer :::KafkaPublisher:"+"Error in intializing property File :"+e.getMessage());
			return "FAILURE";
		}	
		String topicName = prop.getProperty("UserDeactivateTopic");
		try
		{
			
			iLogger.info(loginId+"UserDeactivationProducer ::KafkaPublisher:"+"Topic Name:"+topicName);
			String status = publishMessage(loginId,topicName);
			if(!( status.equalsIgnoreCase("SUCCESS")))
				publishStatus=status;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			publishStatus="FAILURE";
			fLogger.fatal(loginId+":UserDeactivationProducer :: KafkaPublisher:"+"Exception in pushing the message for publish to Topic:"+topicName+"; Persisting payload in OrientDB:"+e.getMessage());			
			return "FAILURE";	
		}
		return publishStatus;
	}
	public String publishMessage(String loginId, String topicName)
	{	
		String status="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Producer<String, String> producer = null;
    	long startTime = System.currentTimeMillis();

    	iLogger.info(loginId+":UserDeactivationProducer ::KafkaPublisher: topicName :"+topicName+" Put To Kafka Producer : START");

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
    			iLogger.info("UserDeactivationProducer :: producer was created :"+producer);
    		}    		
    		iLogger.info(loginId+":UserDeactivationProducer :: publishMessage to KafkaPublisher:"+loginId);
    		KeyedMessage<String, String> data = new KeyedMessage<String, String>(topicName, loginId);
    		iLogger.info(loginId+":UserDeactivationProducer :: publishMessage to KafkaPublisher data:" +data);
    		producer.send(data);
    		producer.close();    		
    		iLogger.info("UserDeactivationProducer :: publishMessage to KafkaPublisher data  ::: END");
    		long endTime=System.currentTimeMillis();
    		iLogger.info("UserDeactivationProducer :: publishMessage to KafkaPublisher data:publishMessage to KafkaPublisher : END :"+(endTime-startTime)); 
    	}catch (Exception e) {
    		e.printStackTrace();
			status="FAILURE";
			fLogger.fatal(loginId+"UserDeactivationProducer :KafkaPublisher:"+"Exception in publishing the message to Topic:"+topicName+"; Persisting payload in OrientDB:"+e.getMessage());	
    	}
		return status;
	}

}

//CR271.en
