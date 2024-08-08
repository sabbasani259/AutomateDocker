package remote.wise.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wipro.WISE.rabbitmq.RabbitMqConnection;


public class PremiumMachinesRabbitMqDataPublisher implements Runnable {

	Thread t;
	List<HashMap<String, String>> payload;
	
	public PremiumMachinesRabbitMqDataPublisher(List<HashMap<String, String>> payloadMap)
	{
		t = new Thread(this, "RabbitMq Publisher");
		this.payload=payloadMap;
		t.start();
	}

	@Override
	public void run() {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Connection connection = null;
		Channel channel = null;
		String exchangeName = "LLPremium";
		String payloadString = null;

		try {
			payloadString = new ObjectMapper().writer().writeValueAsString(payload);
			iLogger.info("PremiumMachinesRabbitMqDataPublisher payloadString: "+payloadString);
//			connection = RabbitMqConnection.getConnection();
//			if (!(connection != null && connection.isOpen())) {
//				if (connection != null) {
//					connection.close();
//				}
//				iLogger.info("PremiumMachinesRabbitMqDataPublisher:Trying 2nd time to connect RabbitMQ connection");
//				connection = RabbitMqConnection.getConnection();
//			}
//			if (connection != null && connection.isOpen()) {
//				iLogger.info("PremiumMachinesRabbitMqDataPublisher:Rabbit MQ connection established with obj:" + connection.hashCode()
//						+ " ::interface data packet send:" + payloadString);
//				iLogger.info("******************************* DATA PUBLISHING *******************************");
//				channel = connection.createChannel();
//				channel.basicPublish(exchangeName, "", null, payloadString.getBytes());
//				iLogger.info("******************************* DATA PUBLISHED TO RABBITMQ:" + exchangeName
//						+ "*******************************");
//			} else {
//				iLogger.info("PremiumMachinesRabbitMqDataPublisher:Unable to establish RabbitMQ connection:" + exchangeName);
//			}
		} catch (Exception e) {
			fLogger.fatal("PremiumMachinesRabbitMqDataPublisher:NOT able to publish to RabbitMQ", e.getMessage());
		} finally {
			try {
//				if (channel != null) {
//					channel.close();
//				}
			} catch (Exception e) {
				fLogger.fatal("PremiumMachinesRabbitMqDataPublisher:Error while closing channel: " + e.getMessage());
			}
		}
	}
}
