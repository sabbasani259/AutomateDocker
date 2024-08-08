package remote.wise.service.webservice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.Qhandler.ServiceHistoryQHandler;
import remote.wise.EAintegration.dataContract.ServiceHistoryInputContract;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@Path("/ServiceClouserKafkaBacklogService")
public class ServiceClouserKafkaBacklogService {

	@GET
	@Path("kafkaBacklogClear")
	@Produces(MediaType.APPLICATION_JSON)
	public String kafkaBacklogClear(){
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
						//System.out.println("Job Process: "+jobDetailsObj.getProcess());
		ServiceClouserKafkaBacklogService fileObj = new ServiceClouserKafkaBacklogService();
		BufferedWriter bw=null;
		String status="SUCCESS";
		try
		{
				//------------------- get the required folder paths from properties file
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));

				String inputfolderPath = "";
				String archivedFolderPath = "";

				
				if (prop.getProperty("deployenvironment").equalsIgnoreCase("SIT")) {
					inputfolderPath = prop.getProperty("ServiceClouser_inputFolderPath_SIT");
					
				} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("DEV")) {
					inputfolderPath = prop.getProperty("ServiceClouser_inputFolderPath_DEV");
					
				} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("QA")) {
					inputfolderPath = prop.getProperty("ServiceClouser_inputFolderPath_QA");
					
				} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("PROD")) {
					inputfolderPath = prop.getProperty("ServiceClouser_inputFolderPath_PROD");
					
				} else {
					inputfolderPath = prop.getProperty("ServiceClouser_inputFolderPath");
					
				}

				
				File inputFile = new File(inputfolderPath+"/ServiceClouserFailure.txt");
				
				HashSet<String> hs = fileObj.processKafkaServiceClouserData(inputFile);	
				inputFile.delete();
				inputFile.createNewFile();
				bw = new BufferedWriter(new FileWriter(inputFile));
				Iterator<String> itr = hs.iterator(); 
		        while (itr.hasNext()) {
		        	bw.write(itr.next());
		        	bw.newLine();
		        	status="FAILURE";
		        }
		        
		}
		catch(Exception e)
		{
				fLogger.fatal("Exception :"+e);
				status= "FAILURE :"+e;
		}
		finally
		{
			if(bw!=null)
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("processKafkaServiceClouserData():ServiceClouserKafkaBACKlogservice.java - :Exception :"+e);
			}

		}
		return status;
	}

	public HashSet<String> processKafkaServiceClouserData(File processingFile)
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		BufferedReader br = null;
		HashSet<String> hs=null;
		try
		{
			
			br = new BufferedReader(new FileReader(processingFile));
			hs = new HashSet<String>();
			String line = br.readLine();
			
			while (line != null) 
			{
				try{

					String[] msgString = line.split("\\|");

					ServiceHistoryInputContract inputObject = new ServiceHistoryInputContract();

					if(msgString.length>0)
						inputObject.setSerialNumber(msgString[0]);

					if(msgString.length>1)
						inputObject.setDealerCode(msgString[1]);

					if(msgString.length>2)
						inputObject.setJobCardNumber(msgString[2]);

					if(msgString.length>3)
						inputObject.setDbmsPartCode(msgString[3]);

					if(msgString.length>4)
						inputObject.setServicedDate(msgString[4]);

					if(msgString.length>5)
						inputObject.setProcess(msgString[5]);
					
					if(msgString.length>6)
						inputObject.setReprocessJobCode(msgString[6]);


					inputObject.setFileRef(processingFile.getName().substring(0, processingFile.getName().lastIndexOf(".")));
					
					//Place the object into ServiceHistoryQueue
					iLogger.info("processKafkaServiceClouserData():ServiceClouserKafkaBACKlogservice.java : - "+inputObject.getSerialNumber()+": Q Object Details");
					iLogger.info(inputObject.getSerialNumber()+" :--------------------------");
					iLogger.info("processKafkaServiceClouserData():ServiceClouserKafkaBACKlogservice.java Input to the Q::"+inputObject.getSerialNumber()+","+inputObject.getDealerCode()+","+inputObject.getFileRef()+","+"" +
							inputObject.getMessageId()+","+inputObject.getProcess()+","+inputObject.getReprocessJobCode()+","+inputObject.getSerialNumber()+","+"" +
							inputObject.getJobCardNumber()+","+inputObject.getDbmsPartCode()+","+inputObject.getServicedDate());

					ServiceHistoryQHandler queueObj = new ServiceHistoryQHandler();
					iLogger.info("processKafkaServiceClouserData():ServiceClouserKafkaBACKlogservice.java - Before Calling ServiceHistoryQueue Handler");

					//DF20171212:KO369761-Changing interfaces queues to Kafka from Hornet queues.
					//String qDropStatus = queueObj.handleJcbRollOff(queuePath,inputObject);
					String qDropStatus = queueObj.handleServiceHistoryDetailsToKafkaQueue("ServiceHistoryQueue",inputObject);
					iLogger.info("processKafkaServiceClouserData():ServiceClouserKafkaBACKlogservice.java - After Calling ServiceHistoryQueue Handler Status::"+qDropStatus);

					if(qDropStatus.equalsIgnoreCase("FAILURE")||qDropStatus==null){
						hs.add(line);
					}

				}catch (Exception e) {
					fLogger.fatal("Exception in reading the message:"+line+" from file:"+e.getMessage());
				}
				line = br.readLine();
			}
		}
		catch(Exception e)
		{
			fLogger.fatal("processKafkaServiceClouserData():ServiceClouserKafkaBACKlogservice.java - "+processingFile.getName()+" :Exception :"+e);
		}

		finally
		{
			if(br!=null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("processKafkaServiceClouserData():ServiceClouserKafkaBACKlogservice.java - "+processingFile.getName()+" :Exception :"+e);
			}

		}
		return hs;
	}
}



		
