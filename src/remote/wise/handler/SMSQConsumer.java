package remote.wise.handler;

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

public class SMSQConsumer implements Runnable {

	Thread t;
	String topicName = "SMSQueue";
	static int numberOfThreads = 0;
	static int listenerFlag = 1;
	public static KafkaConsumer<String, String> consumer = null;//CR330.n
	public static ExecutorService executor;//CR330.n
	//public static HashMap<String, Integer> assetThreadCount = new HashMap<String, Integer>();//CR330.o

	public SMSQConsumer() {
		t = new Thread(this, "SMSQConsumer");
		t.start();
	}

	//CR330.sn
	@Override
	public void run() {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		//--------- Step1: Get the number of threads that can run in parallel after consuming the message from Kafka
		try {
			Properties prop = new StaticProperties().getConfProperty();
			numberOfThreads = Integer.valueOf(prop.getProperty("SMSQThreadCount"));
		} catch (Exception e) {
			fLogger.fatal("SMSHandler:SMSQConsumer:Error in intializing property File:" + e.getMessage());
		}

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

				ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("SMSQConsumer-thread-%d")
						.build();
				List<TopicPartition> partitionList = new LinkedList<TopicPartition>();
				TopicPartition partition = new TopicPartition(topicName, 0);
				partitionList.add(partition);
				SMSQConsumer.consumer = new KafkaConsumer<>(props);
				SMSQConsumer.consumer.assign(partitionList);

				outerLoop: while (numberOfThreads != 0) {
					int msgsProcessed = 0;
					int ActiveCount = 0;

					if (SMSQConsumer.listenerFlag == 1) {
						ConsumerRecords<String, String> streams = SMSQConsumer.consumer.poll(100);

						if (streams != null && !streams.isEmpty()) {
							//We are using Fixed Pool Executor and given numberOfThreads as fixed and not the number of messages in Kafka,
							//since there is no method available to find the number of messages to be consumed yet from Kafka. 
							//Kafka Topic is considered to be of infinite messages. 

							executor = Executors.newFixedThreadPool(numberOfThreads, namedThreadFactory);

							//Important Note: Kafka stream will not come out of this for loop even after consuming all the messages. 
							//It would be waiting for the next set of messages to be consumed and hence it acts like a continuous listener as long as this thread exists.	
							for (ConsumerRecord<String, String> stream : streams) {
								if (SMSQConsumer.listenerFlag == 0) {
									int infiniteCounter = 0;
									iLogger.info(
											"SMSHandler:SMSQConsumer:ForKafkaStreamStart:Listener Flag is set to 0. Hence stop consuming messages from Topic");
									executor.shutdown();
									while (!executor.isTerminated()) {
										if (infiniteCounter == 0) {
											iLogger.info(
													"SMSHandler:SMSQConsumer:ForKafkaStreamStart:Waiting for Termination");
											infiniteCounter++;
										}
										Thread.sleep(100);
									}
									executor.shutdownNow();

									infiniteCounter = 0;
									while (SMSQConsumer.listenerFlag == 0) {
										if (infiniteCounter == 0) {
											iLogger.info(
													"SMSHandler:SMSQConsumer:ForKafkaStreamStart:Listener Flag ==0 ");
											infiniteCounter++;
										}
										Thread.sleep(1000);
									}

									iLogger.info(
											"SMSHandler:SMSQConsumer:Control Came out of Infinite Loop when Lister Falg Set to 1");
									continue outerLoop;
								}

								if (executor instanceof ThreadPoolExecutor) {
									Thread.sleep(10);

									ActiveCount = ((ThreadPoolExecutor) executor).getActiveCount();
									iLogger.debug("SMSHandler:SMSQConsumer: ActiveCount :" + ActiveCount);
									activeCountCheck: while (ActiveCount >= numberOfThreads) {
										Thread.sleep(500);
										ActiveCount = ((ThreadPoolExecutor) executor).getActiveCount();
										if (SMSQConsumer.listenerFlag == 0) {
											break activeCountCheck;
										}
									}
								}

								if (SMSQConsumer.listenerFlag == 0) {
									int infiniteCounter = 0;
									iLogger.info(
											"SMSHandler:SMSQConsumer:It.hasNext():Listener Flag is set to 0. Hence stop consuming messages from Topic");
									executor.shutdown();
									while (!executor.isTerminated()) {
										if (infiniteCounter == 0) {
											iLogger.info(
													"SMSHandler:SMSQConsumer:It.hasNext():Waiting for Termination");
											infiniteCounter++;
										}
										Thread.sleep(100);
									}
									executor.shutdownNow();

									infiniteCounter = 0;
									while (SMSQConsumer.listenerFlag == 0) {
										if (infiniteCounter == 0) {
											iLogger.info("SMSHandler:SMSQConsumer:It.hasNext():Listener Flag ==0 ");
											infiniteCounter++;
										}
										Thread.sleep(1000);
									}

									iLogger.info(
											"SMSHandler:SMSQConsumer:Control Came out of Infinite Loop when Lister Falg Set to 1");
									continue outerLoop;
								}

								msgsProcessed++;
								iLogger.debug(
										"SMSHandler:SMSQConsumer:Number of Messages processed  =" + msgsProcessed);
								String msgReceived = stream.value();
								iLogger.info("SMSHandler:SMSQConsumer:ActiveCount : " + ActiveCount + " msgReceived ="
										+ msgReceived);

								SendSMSWithKafka smsObj = new SendSMSWithKafka();
								smsObj.smsTemplateString = msgReceived;

								Callable<String> worker = smsObj;
								executor.submit(worker);
							}

							executor.shutdown();

							//--------- Introducing while loop here to wait for all the threads to be completed, so that next set of packets can be consumed from Kafka
							//Otherwise it would just bombard the executor where in executor can't open up new threads until it is released from other
							iLogger.debug("SMSHandler:SMSQConsumer:Wait for Executor to terminate");
							int infiniteCounter1 = 0;
							while (!executor.isTerminated()) {
								if (infiniteCounter1 == 0) {
									iLogger.info(
											"SMSHandler:SMSQConsumer:It.When For loops Ended :Waiting for Termination");
									infiniteCounter1++;
								}
							}

							executor.shutdownNow();
							iLogger.debug("SMSHandler:SMSQConsumer:Executor terminated");
						}

					}

					else {
						fLogger.debug(
								"SMSHandler:SMSQConsumer:Listener Flag is set to 0. Hence not consuming messages from Topic");
						break outerLoop;
					}

				}

			}

			catch (Exception e) {
				fLogger.fatal("SMSHandler:SMSQConsumer:Unable to subscribe kafka consumer:Exception:" + e.getMessage());
			}

			finally {
				SMSQConsumer.consumer.close();
				iLogger.info("SMSHandler:SMSQConsumer:End on Thread Run ");
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
			fLogger.fatal("SMSHandler:SMSQConsumer:Error in fetching data property File:"+e.getMessage());
		}
	
	
		if(numberOfThreads!=0)
		{
			try{
				String groupId="ClientNew_"+topicName;
				Properties props = new Properties();
				props.put("zookeeper.connect","localhost:2181");
				props.put("group.id", groupId);
				props.put("zookeeper.session.timeout.ms", "30000");
				props.put("zookeeper.sync.time.ms", "200");
				props.put("auto.commit.interval.ms", "1000"); 
	
				outerLoop: while(numberOfThreads!=0)
				{
					int msgsProcessed =0;
					int ActiveCount =0;
					
					if(SMSQConsumer.listenerFlag==1)
					{
						ConsumerConnector consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
						iLogger.info("SMSHandler:SMSQConsumer:Initialized with ConsumerGroupId:"+groupId);
	
						iLogger.info("SMSHandler:SMSQConsumer:Topic:"+topicName+":"+"Listener Flag is 1: Start Consumption from Topic"); 
						HashMap<String, Integer> topicCount = new HashMap<String, Integer>();
						// Define single thread for topic
						topicCount.put(topicName, 1);
	
						Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams = consumer.createMessageStreams(topicCount);
						List<KafkaStream<byte[], byte[]>> streams = consumerStreams.get(topicName);
	
						if(streams != null && streams.size() > 0 ){
	
							ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
	
							for (KafkaStream stream : streams) 
							{
								if(SMSQConsumer.listenerFlag == 0)
								{	
									int infiniteCounter =0;
									executor.shutdown(); 
				        			while (!executor.isTerminated()) 
				        			{  
				        				if (infiniteCounter ==0)
				        				{
					        				iLogger.debug("SMSHandler:SMSQConsumer:For Loop of KafkaStream Start:Waiting for Termination");
					        				infiniteCounter++;
				        				}
				        				Thread.sleep(100);
				        			}  
				        			executor.shutdownNow();
				        			//DF20190304 - Rajani Nagaraju - Reusing the threads from Thread Pool Executor for better performance - END
				        			
				        			consumer.shutdown();
				        			infiniteCounter=0;
				        			
				        			while(SMSQConsumer.listenerFlag==0)
				        			{
				        				if (infiniteCounter ==0)
				        				{
					        				iLogger.debug("SMSHandler:SMSQConsumer:For Loop of KafkaStream Start:Listener Flag ==0");
					        				infiniteCounter++;
				        				}
				        				Thread.sleep(1000);
				        				
				        			}
					        		continue outerLoop;
									
								}
								ConsumerIterator<byte[], byte[]> consumerIterator = stream.iterator();
	
								while (consumerIterator.hasNext())
								{
								
									if (executor instanceof ThreadPoolExecutor) 
					        		{
					       				Thread.sleep(10);
	
					       				ActiveCount= ((ThreadPoolExecutor) executor).getActiveCount();
					       				iLogger.debug("SMSHandler:SMSQConsumer: ActiveCount :"+ActiveCount);
					       				activeCountCheck: while(ActiveCount >= numberOfThreads )
					       				{
					       					Thread.sleep(500);
					       					ActiveCount= ((ThreadPoolExecutor) executor).getActiveCount();
					       					if(SMSQConsumer.listenerFlag == 0)
					       					{
					       						break activeCountCheck; 
					       					}
					       				}
					       			}
									
									
									
									if(SMSQConsumer.listenerFlag == 0)
									{
										int infiniteCounter =0;
					        			iLogger.info("SMSHandler:SMSQConsumer:run():Listener Flag is set to 0. Hence stop consuming messages from Topic:"+topicName);
					        			//DF20190304 - Rajani Nagaraju - Reusing the threads from Thread Pool Executor for better performance - START
					        			//--As new thread spawning is taking considerably long time when taken in cumulative for 100 threads
					        			executor.shutdown(); 
					        			while (!executor.isTerminated()) 
					        			{   
					        				if (infiniteCounter ==0)
					        				{
						        				iLogger.debug("SMSHandler:SMSQConsumer:while iterating through messages:Waiting for Termination");
						        				infiniteCounter++;
					        				}
					        				Thread.sleep(100);
					        			}  
					        			executor.shutdownNow();
					        			//DF20190304 - Rajani Nagaraju - Reusing the threads from Thread Pool Executor for better performance - END
					        			consumer.shutdown();
					        			infiniteCounter=0;
					        			
					        			while(SMSQConsumer.listenerFlag==0)
					        			{
					        				if (infiniteCounter ==0)
					        				{
						        				iLogger.debug("SMSHandler:SMSQConsumer:while iterating through messages:Listener Flag ==0");
						        				infiniteCounter++;
					        				}
					        				Thread.sleep(100);
					        			}
					        			continue outerLoop;
									}
									//Wait for the child threads to finish which would otherwise continues with the parent program and consumes all the messages from kafka,
									//however opens only max of "numberOfThreads". In the process if there is an exception, then the messages consumed from kafka should be 
									//stored somewhere for re-processing. Rather, don't consume the msg from kafka until "numberOfThreads" completes execution.
									if(msgsProcessed >= numberOfThreads){
										iLogger.info("SMSHandler:SMSQConsumer:ThreadPoolCount:"+numberOfThreads+":Total Messages processed in current run:"+msgsProcessed);
										iLogger.info("SMSHandler:SMSQConsumer:Wait for all the spawned threads to complete");
										executor.shutdown(); 
										while (!executor.isTerminated()) {   }
										executor.shutdownNow();
										msgsProcessed=0;
										executor = Executors.newFixedThreadPool(numberOfThreads);	
									}
	
									if(SMSQConsumer.listenerFlag == 0)
										break;
	
									msgsProcessed++;
									String msgReceived=new String(consumerIterator.next().message());// Until this statement is executed, consumer offset will not be moved
									iLogger.info("SMSHandler:SMSQConsumer:msgReceived ="+msgReceived);
	
									SendSMSWithKafka smsObj = new SendSMSWithKafka();
									smsObj.smsTemplateString = msgReceived;
	
									Callable<String> worker = smsObj;
									executor.submit(worker);
	
								}
							}
							//executor.shutdown();
	
	//							iLogger.debug("SMSQConsumer:Wait for Executor to terminate");
							while (!executor.isTerminated()) {   } 
							executor.shutdownNow();
	//							iLogger.debug("SMSQConsumer:Executor terminated"); 
							long eStartTime = System.currentTimeMillis();
					        int infiniteCounter1=0;
					        while (!executor.isTerminated()) 
					        {   
					        	if (infiniteCounter1 ==0)
					        	{
					       			iLogger.debug("SMSHandler:SMSQConsumer:After ending the for loop :Waiting for Termination");
					       			infiniteCounter1++;
					       		}
					        }  
					       	executor.shutdownNow();
					       	long eEndTime = System.currentTimeMillis();
					       	assetThreadCount = new HashMap<String,Integer>();
					        iLogger.info("SMSHandler:SMSQConsumer:run():All messages consumed:No. of messages Processed:"+msgsProcessed+":" +
					        		"TotalTime in ms:"+(eEndTime-eStartTime));
						}
						consumer.shutdown();
					}
					
					else
					{
						iLogger.info("SMSHandler:SMSQConsumer:run():Listener Flag is set to 0. Hence not consuming messages from Topic");
			      		//Introducing while loop which would other keep printing the logger in else block
			      		int infiniteCounters=0;
			      		while(SMSQConsumer.listenerFlag==0)
			      		{
			      			if (infiniteCounters ==0){
			    				iLogger.debug("SMSHandler:SMSQConsumer:Else block of Listener Flag ==1 ");
			    				infiniteCounters++;
			    			}
			    			Thread.sleep(1000);
			      		}
			      		continue outerLoop;
					}
				}
			}catch (Exception e) {
				// TODO: handle exception
				fLogger.error("SMSHandler:SMSQConsumer: Cause of Exception :"+e.getMessage());
			}
		}
	}*/
	//CR330.eo
}
