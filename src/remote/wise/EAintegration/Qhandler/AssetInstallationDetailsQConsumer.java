package remote.wise.EAintegration.Qhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

//CR330.so
/*import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;*/
//CR330.eo

//CR330.sn
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
//CR330.en
import org.apache.logging.log4j.Logger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.StaticProperties;

public class AssetInstallationDetailsQConsumer implements Runnable {

	Thread t;
	String topicName = "AssetInstallationQueue";
	//static int numberOfThreads = 5;//CR330.o
	static int numberOfThreads = 5;//CR330.n
	static int listenerFlag = 1;
	public static KafkaConsumer<String, String> consumer = null;//CR330.n
	public static ExecutorService executor;//CR330.n

	public AssetInstallationDetailsQConsumer() {
		t = new Thread(this, "AssetInstallationDetailsQConsumer");
		t.start();
	}

	//CR330.sn
	@Override
	public void run() {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		//--------- Step1: Get the number of threads that can run in parallel after consuming the message from Kafka
		/*try {
			Properties prop = new StaticProperties().getConfProperty();
			numberOfThreads = Integer.valueOf(prop.getProperty("AssetInstallationDetailsQThreadCount"));
		} catch (Exception e) {
			fLogger.fatal("AssetInstallationDetailsQConsumer:Error in intializing property File:" + e.getMessage());
		}*/

		//Need to stop the thread if there is an error in reading from the property file
		if (numberOfThreads != 0) {
			try {
				//------------ Step2: Provide the connection parameters
				String groupId = "Client_" + topicName;
				Properties props = new Properties();
				props.put("bootstrap.servers", "localhost:9092");
				props.put("group.id", groupId);
				props.put("enable.auto.commit", "true");
				props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
				props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

				ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
						.setNameFormat("AssetInstallationDetailsQConsumer-thread-%d").build();
				List<TopicPartition> partitionList = new LinkedList<TopicPartition>();
				TopicPartition partition = new TopicPartition(topicName, 0);
				partitionList.add(partition);
				AssetInstallationDetailsQConsumer.consumer = new KafkaConsumer<>(props);
				AssetInstallationDetailsQConsumer.consumer.assign(partitionList);

				outerLoop: while (numberOfThreads != 0) {
					int msgsProcessed = 0;
					int ActiveCount = 0;

					if (AssetInstallationDetailsQConsumer.listenerFlag == 1) {
						ConsumerRecords<String, String> streams = AssetInstallationDetailsQConsumer.consumer.poll(100);

						if (streams != null && !streams.isEmpty()) {
							//We are using Fixed Pool Executor and given numberOfThreads as fixed and not the number of messages in Kafka,
							//since there is no method available to find the number of messages to be consumed yet from Kafka. 
							//Kafka Topic is considered to be of infinite messages. 

							executor = Executors.newFixedThreadPool(numberOfThreads, namedThreadFactory);

							//Important Note: Kafka stream will not come out of this for loop even after consuming all the messages. 
							//It would be waiting for the next set of messages to be consumed and hence it acts like a continuous listener as long as this thread exists.	
							for (ConsumerRecord<String, String> stream : streams) {
								if (AssetInstallationDetailsQConsumer.listenerFlag == 0) {
									int infiniteCounter = 0;
									iLogger.info(
											"AssetInstallationDetailsQConsumer:ForKafkaStreamStart:Listener Flag is set to 0. Hence stop consuming messages from Topic");
									executor.shutdown();
									while (!executor.isTerminated()) {
										if (infiniteCounter == 0) {
											iLogger.info(
													"AssetInstallationDetailsQConsumer:ForKafkaStreamStart:Waiting for Termination");
											infiniteCounter++;
										}
										Thread.sleep(100);
									}
									executor.shutdownNow();

									infiniteCounter = 0;
									while (AssetInstallationDetailsQConsumer.listenerFlag == 0) {
										if (infiniteCounter == 0) {
											iLogger.info(
													"AssetInstallationDetailsQConsumer:ForKafkaStreamStart:Listener Flag ==0 ");
											infiniteCounter++;
										}
										Thread.sleep(1000);
									}

									iLogger.info(
											"AssetInstallationDetailsQConsumer:Control Came out of Infinite Loop when Lister Falg Set to 1");
									continue outerLoop;
								}

								if (executor instanceof ThreadPoolExecutor) {
									Thread.sleep(10);

									ActiveCount = ((ThreadPoolExecutor) executor).getActiveCount();
									iLogger.debug("AssetInstallationDetailsQConsumer: ActiveCount :" + ActiveCount);
									activeCountCheck: while (ActiveCount >= numberOfThreads) {
										Thread.sleep(500);
										ActiveCount = ((ThreadPoolExecutor) executor).getActiveCount();
										if (AssetInstallationDetailsQConsumer.listenerFlag == 0) {
											break activeCountCheck;
										}
									}
								}

								if (AssetInstallationDetailsQConsumer.listenerFlag == 0) {
									int infiniteCounter = 0;
									iLogger.info(
											"AssetInstallationDetailsQConsumer:It.hasNext():Listener Flag is set to 0. Hence stop consuming messages from Topic");
									executor.shutdown();
									while (!executor.isTerminated()) {
										if (infiniteCounter == 0) {
											iLogger.info(
													"AssetInstallationDetailsQConsumer:It.hasNext():Waiting for Termination");
											infiniteCounter++;
										}
										Thread.sleep(100);
									}
									executor.shutdownNow();

									infiniteCounter = 0;
									while (AssetInstallationDetailsQConsumer.listenerFlag == 0) {
										if (infiniteCounter == 0) {
											iLogger.info("AssetInstallationDetailsQConsumer:It.hasNext():Listener Flag ==0 ");
											infiniteCounter++;
										}
										Thread.sleep(1000);
									}

									iLogger.info(
											"AssetInstallationDetailsQConsumer:Control Came out of Infinite Loop when Lister Falg Set to 1");
									continue outerLoop;
								}

								msgsProcessed++;
								iLogger.debug(
										"AssetInstallationDetailsQConsumer:Number of Messages processed  =" + msgsProcessed);
								String msgReceived = stream.value();
								iLogger.info("AssetInstallationDetailsQConsumer:ActiveCount : " + ActiveCount + " msgReceived ="
										+ msgReceived);

								AssetInstallationDetailsQHandler installationObj = new AssetInstallationDetailsQHandler();
								installationObj.assetInstallationInput = msgReceived;

								Callable<String> worker = installationObj;
								executor.submit(worker);

							}

							executor.shutdown();

							//--------- Introducing while loop here to wait for all the threads to be completed, so that next set of packets can be consumed from Kafka
							//Otherwise it would just bombard the executor where in executor can't open up new threads until it is released from other
							iLogger.debug("AssetInstallationDetailsQConsumer:Wait for Executor to terminate");
							int infiniteCounter1 = 0;
							while (!executor.isTerminated()) {
								if (infiniteCounter1 == 0) {
									iLogger.info(
											"AssetInstallationDetailsQConsumer:It.When For loops Ended :Waiting for Termination");
									infiniteCounter1++;
								}
							}

							executor.shutdownNow();
							iLogger.debug("AssetInstallationDetailsQConsumer:Executor terminated");
						}

					}

					else {
						fLogger.debug(
								"AssetInstallationDetailsQConsumer:Listener Flag is set to 0. Hence not consuming messages from Topic");
						break outerLoop;
					}
				}
			}

			catch (Exception e) {
				fLogger.fatal(
						"AssetInstallationDetailsQConsumer:Unable to subscribe kafka consumer:Exception:" + e.getMessage());
			}

			finally {
				AssetInstallationDetailsQConsumer.consumer.close();
				iLogger.info("AssetInstallationDetailsQConsumer:End on Thread Run ");
			}
		}
	}
	//CR330.en
	
	//CR330.so
	/*@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void run() {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;


		try {
			Properties prop = new Properties();
			prop.load(getClass()
					.getClassLoader()
					.getResourceAsStream(
							"remote/wise/resource/properties/configuration.properties"));
			numberOfThreads = Integer.valueOf(prop.getProperty("SMSQThreadCount"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fLogger.fatal("AssetInstallationDetailsQConsumer:Error in fetching data property File:"+e.getMessage());
		}


		if(numberOfThreads!=0)
		{
			try{
				String groupId="Client_"+topicName;
				Properties props = new Properties();
				props.put("zookeeper.connect","localhost:2181");
				props.put("group.id", groupId);
				props.put("zookeeper.session.timeout.ms", "30000");
				props.put("zookeeper.sync.time.ms", "200");
				props.put("auto.commit.interval.ms", "1000"); 

				while(numberOfThreads!=0)
				{
					int msgsProcessed =0;

					if(AssetInstallationDetailsQConsumer.listenerFlag==1)
					{
						ConsumerConnector consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
						iLogger.info("AssetInstallationDetailsQConsumer:Initialized with ConsumerGroupId:"+groupId);

						iLogger.info("AssetInstallationDetailsQConsumer:Topic:"+topicName+":"+"Listener Flag is 1: Start Consumption from Topic"); 
						HashMap<String, Integer> topicCount = new HashMap<String, Integer>();
						// Define single thread for topic
						topicCount.put(topicName, 1);

						Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams = consumer.createMessageStreams(topicCount);
						List<KafkaStream<byte[], byte[]>> streams = consumerStreams.get(topicName);

						if(streams.size() > 0 && streams != null){

							ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

							for (KafkaStream stream : streams) {
								if(AssetInstallationDetailsQConsumer.listenerFlag == 0)
								{
									break;
								}
								ConsumerIterator<byte[], byte[]> consumerIterator = stream.iterator();

								while (consumerIterator.hasNext()){
									if(AssetInstallationDetailsQConsumer.listenerFlag == 0)
										break;
									//Wait for the child threads to finish which would otherwise continues with the parent program and consumes all the messages from kafka,
									//however opens only max of "numberOfThreads". In the process if there is an exception, then the messages consumed from kafka should be 
									//stored somewhere for re-processing. Rather, don't consume the msg from kafka until "numberOfThreads" completes execution.
									if(msgsProcessed >= numberOfThreads){
										iLogger.info("AssetInstallationDetailsQConsumer:ThreadPoolCount:"+numberOfThreads+":Total Messages processed in current run:"+msgsProcessed);
										iLogger.info("AssetInstallationDetailsQConsumer:Wait for all the spawned threads to complete");
										executor.shutdown(); 
										while (!executor.isTerminated()) {   }
										executor.shutdownNow();
										msgsProcessed=0;
										executor = Executors.newFixedThreadPool(numberOfThreads);	
									}

									if(AssetInstallationDetailsQConsumer.listenerFlag == 0)
										break;

									msgsProcessed++;
									String msgReceived=new String(consumerIterator.next().message());// Until this statement is executed, consumer offset will not be moved
									iLogger.info("AssetInstallationDetailsQConsumer:msgReceived ="+msgReceived);

									AssetInstallationDetailsQHandler installationObj = new AssetInstallationDetailsQHandler();
									installationObj.assetInstallationInput = msgReceived;

									Callable<String> worker = installationObj;
									executor.submit(worker);

								}
							}
							executor.shutdown();

//							iLogger.debug("AssetInstallationDetailsQConsumer:Wait for Executor to terminate");
							while (!executor.isTerminated()) {   }  
//							iLogger.debug("AssetInstallationDetailsQConsumer:Executor terminated"); 
						}
						consumer.shutdown();
					}
				}
			}catch (Exception e) {
				// TODO: handle exception
				fLogger.error("AssetInstallationDetailsQConsumer: Cause of Exception :"+e.getMessage());
			}
		}
	}*/
	//CR330.eo
}
