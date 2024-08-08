package remote.wise.handler;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class WhatsappHandler {

	public void putToKafkaProducerQueue(String topicName,
			WhatsappTemplate msg) {
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Producer<String, String> producer = null;
    	long startTime = System.currentTimeMillis();

    	iLogger.info("MSG Body:"+msg.getMsgBody()+": topicName :"+topicName+" Put To Kafka Producer : START");

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
    			iLogger.info("producer was created :"+producer);
    		}
    		System.out.println(msg);
    		String whatsappTemplateJson = null;
    		if(msg !=null){
    			ObjectMapper mapper = new ObjectMapper();
    			whatsappTemplateJson = mapper.writeValueAsString(msg);
    		}
    		iLogger.info("WhatsappHandler:handleWhatappInKafka: WhatsApp Template Json"+whatsappTemplateJson);
    		KeyedMessage<String, String> data = new KeyedMessage<String, String>(topicName, whatsappTemplateJson);
    		iLogger.info("WhatsappHandler:handleWhatappInKafka data : "+data);
    		producer.send(data);
    		producer.close();
    		iLogger.info("Mobile Number: "+msg.getTo());
    		iLogger.info("SMS Body: "+msg.getMsgBody());
    		iLogger.info("Put To WhatsAppQueue : END");
    		long endTime=System.currentTimeMillis();
    		iLogger.info("WhatsappHandler:handlewhatsappInKafka:"+msg.getMsgBody()+":" +"Put To WhatsAppQueue Queue : END :"+(endTime-startTime)); 
    	}catch (Exception e) {
    		fLogger.error("WhatsappHandler:handleWhatappInKafka: "+msg.getTo()+" Cause of Exception :"+e.getMessage());
    	}
		
	}



}
