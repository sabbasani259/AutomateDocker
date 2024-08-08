package remote.wise.EAintegration.Qhandler;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.Callable;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.EAintegration.EAclient.ExtendedWarrantyServiceClient;
import remote.wise.EAintegration.dataContract.ExtendedWarrantyServiceInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

public class ExtendedWarrantyQHandler implements Callable
{
	//Logger fatalError = Logger.getLogger("fatalErrorLogger");

	String ExtendedWarrantyInput = null;

	//DF20171212:KO369761-Changing interfaces queues to Kafka from Hornet queues.
	@Override
	public Object call() throws Exception {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		ObjectMapper mapper = new ObjectMapper();
		ExtendedWarrantyServiceInputContract inputObj = null;

		try{
			HashMap<String,Object> JcbRollOffInputMap = new Gson().fromJson(ExtendedWarrantyInput, new TypeToken<HashMap<String, Object>>(){}.getType());
			inputObj = mapper.convertValue(JcbRollOffInputMap, ExtendedWarrantyServiceInputContract.class);
			iLogger.info("ExtendedWarrantyService - "+inputObj.getSerialNumber()+": Message received from Q");
			if(inputObj!=null)
			{
				iLogger.info("ExtendedWarrantyService - "+inputObj.getSerialNumber()+"Invoke Webservice Client");
				new ExtendedWarrantyServiceClient().extendedWarrantyService(inputObj);
				iLogger.info("ExtendedWarrantyService - "+inputObj.getSerialNumber()+"Returned from Webservice Client");
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			fLogger.fatal("ExtendedWarrantyService Exception :"+e.getMessage());
			//DF20180208:KO369761 - Putting into fault details table incase of any exception.
			if(inputObj !=null){
				String serialNumber=inputObj.getSerialNumber();
				String callTypeId=inputObj.getCallTypeId();
				String mothlyVisit=inputObj.getMonthlyVisit();
				String cancellationFlag=inputObj.getCancellationFlag();
				

				if(serialNumber==null)
					serialNumber="";
				if(callTypeId==null)
					callTypeId="";
				if(mothlyVisit==null)
					mothlyVisit="";
				if(cancellationFlag==null)
					cancellationFlag="";
				String messageString = serialNumber+"|"+callTypeId+"|"+mothlyVisit+"|"+cancellationFlag;

				fLogger.fatal("ExtendedWarrantyService - "+messageString+" Exception: "+e +": Put the record into fault details");
				ErrorMessageHandler errorHandler = new ErrorMessageHandler();
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
	public String handleExtendedWarrantyToKafkaQueue(String topicName, ExtendedWarrantyServiceInputContract msg) throws Exception {

		String qDropStatus="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Producer<String, String> producer = null;


		try{
			iLogger.info("ExtendedWarrantyService - "+msg.getSerialNumber()+" : Connect to Q");

			Properties props = new Properties();
			props.put("metadata.broker.list", "localhost:9092");
			props.put("serializer.class", "kafka.serializer.StringEncoder");
			props.put("request.required.acks", "1");

			ProducerConfig config = new ProducerConfig(props);
			producer = new Producer<String, String>(config);

			if(producer != null){
				iLogger.info("producer was created :"+producer);
			}

			iLogger.info("ExtendedWarrantyService - "+msg.getSerialNumber()+" : Send message to Q");

			String ExtendedWarrantyInputJson = null;
			if(msg !=null){
				ObjectMapper mapper = new ObjectMapper();
				ExtendedWarrantyInputJson = mapper.writeValueAsString(msg);
			}
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(topicName, ExtendedWarrantyInputJson);
			producer.send(data);
			iLogger.info("ExtendedWarrantyService - "+msg.getSerialNumber()+" : Message sent successfully to Q");

		}catch (Exception e) {

			qDropStatus = "FAILURE";
			e.printStackTrace();

			String serialNumber=msg.getSerialNumber();
			String callTypeId=msg.getCallTypeId();
			String mothlyVisit=msg.getMonthlyVisit();
			String cancellationFlag=msg.getCancellationFlag();
			

			if(serialNumber==null)
				serialNumber="";
			if(callTypeId==null)
				callTypeId="";
			if(mothlyVisit==null)
				mothlyVisit="";
			if(cancellationFlag==null)
				cancellationFlag="";
			
			String messageString = serialNumber+"|"+callTypeId+"|"+mothlyVisit+"|"+cancellationFlag;

			fLogger.fatal("ExtendedWarrantyService - "+messageString+" Exception: "+e +": Put the record into fault details");
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			//errorHandler.handleErrorMessages(msg.getMessageId(), messageString, msg.getFileRef(), msg.getProcess(), msg.getReprocessJobCode(), e.getMessage());
			errorHandler.handleErrorMessages_new(msg.getMessageId(), messageString, msg.getFileRef(), msg.getProcess(), msg.getReprocessJobCode(), e.getMessage(),"0001","Producer");
			
			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			if(msg.getMessageId().split("\\|").length<2){
				String uStatus=CommonUtil.updateInterfaceLogDetails(msg.getFileRef(), "failureCount", 1);
				iLogger.info("Status on updating data into interface log details table :"+uStatus);
			}

		}finally
		{         
			// ALWAYS close your connection in a finally block to avoid leaks.
			// Closing connection also takes care of closing its related objects e.g. sessions.
			producer.close();
		}

		return qDropStatus;
	}


	
}
