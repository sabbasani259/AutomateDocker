package remote.wise.handler;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.StaticProperties;

public class WeatherDataProducer implements Runnable {

	Thread t;
	String serialNumber;

	public WeatherDataProducer() {

	}

	public WeatherDataProducer(String serialNumber) {
		t = new Thread(this, "Weather Kafka Publisher");
		this.serialNumber = serialNumber;
		t.start();
	}

	@Override
	public void run() {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		long startTime = System.currentTimeMillis();
		//Publish data to kafka queue
		//String publishStatus = publishWeatherData(this.serialNumber);
		String publishStatus ="";
        for(int i=0;i<3;i++) {
        	 iLogger.info("WeatherDataProducer :: KafkaPublisher: " +  i + " attempt" );
            publishStatus = publishWeatherData(this.serialNumber);
            if(publishStatus.equalsIgnoreCase("SUCCESS")) {
                break;
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                fLogger.fatal("Exception occured"+ e.getMessage());
            }
        }
		String reprocessEntryStatus = null;
		if(!publishStatus.equalsIgnoreCase("SUCCESS")) {
			//if failure, add entry to weather_data_reprocess
			iLogger.info("WeatherDataProducer :: KafkaPublisher: Unable to publish after 3 attempts for vin)" + this.serialNumber +".Adding to weather_data_reprocess" );
			reprocessEntryStatus = addToReprocessData(this.serialNumber);
		}
		long endTime = System.currentTimeMillis();
		iLogger.info("WeatherDataProducer :: KafkaPublisher:" + "Publish the Message to required topics: Status:"
				+ publishStatus + "; Turn around Time (in ms):" + (endTime - startTime));
		if(null != reprocessEntryStatus) {
			iLogger.info("WeatherDataProducer :: addToReprocessData:" + "Add data to weather data reprocess table: Status:"
					+ reprocessEntryStatus + "; Turn around Time (in ms):" + (endTime - startTime));
		}
	}
	
	public String addToReprocessData(String vin) {
		String status = "SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		String query = "INSERT INTO weather_data_reprocess (serial_number, processed_flag) VALUES ('"+ vin +"', 0)";
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection.createStatement()) {
			statement.execute(query); 
		}catch (Exception e) {
			status = "FAILURE";
			fLogger.fatal("WeatherDataProducer :: addToReprocessData :: Unable add " + vin + " to weather_data_reprocess after failure.");
		}
		return status;
	}

	public String publishWeatherData(String serialNumber) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		String publishStatus = "SUCCESS";
		Properties prop = null;
		try {
			prop = StaticProperties.getConfProperty();
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal(
					"WeatherDataProducer :: KafkaPublisher:" + "Error in intializing property File :" + e.getMessage());
			return "FAILURE";
		}
		String topicName = prop.getProperty("WeatherDataKafkaTopic");
		try {
			iLogger.info(
					"WeatherDataProducer ::KafkaPublisher:" + "Topic Name:" + topicName + "; Message:" + serialNumber);
			String status = publishMessage(topicName, serialNumber);
			if (!(status.equalsIgnoreCase("SUCCESS")))
				publishStatus = status;
		} catch (Exception e) {
			e.printStackTrace();
			publishStatus = "FAILURE";
			fLogger.fatal(
					":WeatherDataProducer :: KafkaPublisher:" + "Exception in pushing the message for publish to Topic:"
							+ topicName + ":" + e.getMessage());
			return "FAILURE";
		}
		return publishStatus;
	}

	public String publishMessage(String topicName, String message) {
		String status = "SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Producer<String, String> producer = null;
		long startTime = System.currentTimeMillis();

		iLogger.info("WeatherDataProducer ::KafkaPublisher:" + message + ": topicName :" + topicName
				+ " Put To Kafka Producer : START");

		try {
			Properties props = new Properties();
			props.put("metadata.broker.list", "localhost:9092");
			props.put("serializer.class", "kafka.serializer.StringEncoder");
			props.put("request.required.acks", "0");
//			props.put("request.timeout.ms", "10000");
//			props.put("retry.backoff.ms", "10000");
//			props.put("delivery.timeout.ms", "30001");
			ProducerConfig config = new ProducerConfig(props);
			System.out.println(config);
			producer = new Producer<String, String>(config);
			if (producer != null) {
				iLogger.info("WeatherDataProducer :: producer was created :" + producer);
			}
			iLogger.info("WeatherDataProducer :: publishMessage to KafkaPublisher:" + message);
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(topicName, message);
			iLogger.info("WeatherDataProducer :: publishMessage to KafkaPublisher data:" + data);
			producer.send(data);
			producer.close();
			iLogger.info("WeatherDataProducer :: publishMessage to KafkaPublisher data  ::: END");
			long endTime = System.currentTimeMillis();
			iLogger.info("WeatherDataProducer :: publishMessage to KafkaPublisher data:" + message + ":"
					+ "publishMessage to KafkaPublisher : END :" + (endTime - startTime));
		} catch (Exception e) {
			e.printStackTrace();
			status = "FAILURE";
			fLogger.fatal("WeatherDataProducer :KafkaPublisher:" + "Exception in publishing the message to Topic:"
					+ topicName + ":" + e.getMessage());
		}
		return status;
	}

}
