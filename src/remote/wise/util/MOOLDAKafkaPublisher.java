/**
 * CR337 : 20220721 : Dhiraj K : Property file read.
 */
package remote.wise.util;

import java.util.HashMap;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author ROOPN5
 *
 */
public class MOOLDAKafkaPublisher implements Runnable{

	Thread t;
	String txnKey;
	HashMap<String,String> payloadMap;

	public MOOLDAKafkaPublisher()
	{

	}

	public MOOLDAKafkaPublisher(String txnKey,HashMap<String,String> payloadMap)
	{
		t = new Thread(this, "Kafka Publisher");
		this.txnKey=txnKey;
		this.payloadMap=payloadMap;

		t.start();
	}

	@Override
	public void run() {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		//iLogger.info(this.txnKey+":MOOLDA_Alerts:KafkaPublisher:"+"Publish the Message to required topics");
		long startTime = System.currentTimeMillis();
		String publishStatus = publishMachineData(this.txnKey,this.payloadMap);
		long endTime = System.currentTimeMillis();
		iLogger.info(this.txnKey+":MOOLDA_Alerts:KafkaPublisher:"+"Publish the Message to required topics: Status:"+publishStatus+"; Turn around Time (in ms):"+(endTime-startTime));
	}

	public String publishMachineData(String txnKey,HashMap<String,String> payloadMap)
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		String publishStatus = "SUCCESS";

		long startTime = System.currentTimeMillis();
//		iLogger.debug(txnKey+":MOOLDA_Alerts:KafkaPublisher:"+"Publish Message - START");

		//--------------------------- STEP1: Initializing Property file
		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal(txnKey+":MOOLDA_Alerts:KafkaPublisher:"+"Error in intializing property File :"+e.getMessage());
			return "FAILURE";
		}

		String messageId = payloadMap.get("MSG_ID");

		String topicName = prop.getProperty("MOOLDATOPIC");


		try
		{

			payloadMap.put("TXN_KEY", txnKey);

			//Build a JSON String of Field-value map
			String payloadMapJson = new Gson().toJson(payloadMap); 

			iLogger.info(txnKey+":MOOLDA_Alerts:KafkaPublisher:"+"Topic Name:"+topicName+"; Message:"+payloadMapJson);
			String status = publishMessage(txnKey,topicName,payloadMapJson);
			if(!( status.equalsIgnoreCase("SUCCESS")))
				publishStatus=status;

		}
		catch(Exception e)
		{
			e.printStackTrace();
			publishStatus="FAILURE";
			fLogger.fatal(txnKey+":MOOLDA_Alerts:KafkaPublisher:"+"Exception in pushing the message for publish to Topic:"+topicName+"; Persisting payload in OrientDB:"+e.getMessage());

			String txnDataMap= new JSONObject(payloadMap).toString();

			new MOOLDARejectedRecordsBO().persistPacketDetails(txnKey, payloadMap.get("ASSET_ID"), payloadMap.get("TXN_TIME"), txnDataMap, "Exception in pushing the message to Kafka", "KafkaProducer");
			return "FAILURE";	
		}


		return publishStatus;
	}


	public String publishMessage(String txnKey, String topicName, String message)
	{
		String status="SUCCESS";
		Producer<String, String> producer = null;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		try
		{
			Properties props = new Properties();
			
			Properties deployEnvProp=null;
			try
			{
				deployEnvProp= StaticProperties.getConfProperty();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal(txnKey+":MOOLDA_Alerts:KafkaPublisher:"+"Error in intializing property File :"+e.getMessage());
				return "FAILURE";
			}
			String APPPEnv = deployEnvProp.getProperty("APPenvironment");
			String kafkaBrokerAP1 = deployEnvProp.getProperty("KafkaBrokerAP1"); //CR337.n
			String kafkaBrokerAP2 = deployEnvProp.getProperty("KafkaBrokerAP2"); //CR337.n
			
			if (APPPEnv.equalsIgnoreCase("AP1")) {
				//props.put("metadata.broker.list", "10.179.12.20:9092");//APP1 //CR337.o
				props.put("metadata.broker.list", kafkaBrokerAP1);//APP1 //CR337.n
			}
			if (APPPEnv.equalsIgnoreCase("AP2")) {
				//props.put("metadata.broker.list", "10.179.12.21:9092");//APP2 //CR337.o
				props.put("metadata.broker.list", kafkaBrokerAP2);//APP2 //CR337.n
			}
			
			//Staging server
			
		//	props.put("metadata.broker.list", "localhost:9092");
			
			props.put("serializer.class", "kafka.serializer.StringEncoder");
			props.put("request.required.acks", "0");

			ProducerConfig config = new ProducerConfig(props);
			producer = new Producer<String, String>(config);

			KeyedMessage<String, String> data = new KeyedMessage<String, String>(topicName, message);
			producer.send(data);
			iLogger.info(txnKey+":MOOLDA_Alerts:KafkaPublisher:"+"Message Published successfully to the Topic:"+topicName+": Message:"+message);
		}

		catch(Exception e)
		{
			//---------------------------------- Zookeeper/Kafka Sever/Topic is not reachable, persist the data into mysql for reprocessing
			e.printStackTrace();
			status="FAILURE";
			fLogger.fatal(txnKey+":MOOLDA_Alerts:KafkaPublisher:"+"Exception in publishing the message to Topic:"+topicName+"; Persisting payload in OrientDB:"+e.getMessage());

			HashMap map = new Gson().fromJson(message, new TypeToken<HashMap>(){}.getType());

			String txnDataMap = new JSONObject(map).toString();
			new MOOLDARejectedRecordsBO().persistPacketDetails(txnKey, map.get("ASSET_ID").toString(), map.get("TXN_TIME").toString(), txnDataMap, "Kafka not available", "KafkaProducer");

		}

		finally
		{
			if(producer!=null)
				producer.close();
		}

		return status;
	}

}
