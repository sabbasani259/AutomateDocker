package remote.wise.EAintegration.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;


import remote.wise.EAintegration.Qhandler.KafkaConsumerLag;
import remote.wise.EAintegration.Qhandler.MasterServiceScheduleQHandler;
import remote.wise.EAintegration.dataContract.MasterServiceScheduleInputContract;
import remote.wise.businessentity.EALogDetailsEntity;
import remote.wise.businessentity.JobDetails;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

public class MasterServiceSchedule 
{
	public MasterServiceSchedule()
	{

	}

	public MasterServiceSchedule(File processingFile, String archivedFolderPath, String process, String reprocessJobCode)
	{
		Logger fLogger = FatalLoggerClass.logger;
		try 
		{
			processMasterServiceSchedule(processingFile, archivedFolderPath, process, reprocessJobCode);
		} 
		catch (Exception e) 
		{
			fLogger.fatal("Exception :"+processingFile+": "+e);
		}
	}


	public void handleMasterServiceSchedule()
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();

		try
		{
			Query query = session.createQuery("from JobDetails where jobCode='MasterServiceSchedule' order by sequence");
			Iterator itr = query.list().iterator();

			while(itr.hasNext())
			{
				JobDetails jobDetailsObj = (JobDetails) itr.next();

				//System.out.println("Job Process: "+jobDetailsObj.getProcess());

				Class<?> theClass  = Class.forName("remote.wise.EAintegration.handler"+"."+jobDetailsObj.getProcess());
				Constructor<?> consToProcessEAdata = theClass.getConstructor(File.class,String.class, String.class, String.class);

				MasterServiceSchedule fileObj = new MasterServiceSchedule();

				//------------------- get the required folder paths from properties file
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));

				//    			String inputfolderPath = prop.getProperty("EA_inputFolderPath");
				//    			String processingFolderPath = prop.getProperty("EA_processingFolderPath");
				//    			String archivedFolderPath = prop.getProperty("EA_archiveFolderPath");

				String inputfolderPath = "";
				String processingFolderPath = "";
				String archivedFolderPath = "";

				if (prop.getProperty("deployenvironment").equalsIgnoreCase("SIT")) {
					inputfolderPath = prop.getProperty("EA_inputFolderPath_SIT");
					processingFolderPath = prop.getProperty("EA_processingFolderPath_SIT");
					archivedFolderPath = prop.getProperty("EA_archiveFolderPath_SIT");
				} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("DEV")) {
					inputfolderPath = prop.getProperty("EA_inputFolderPath_DEV");
					processingFolderPath = prop.getProperty("EA_processingFolderPath_DEV");
					archivedFolderPath = prop.getProperty("EA_archiveFolderPath_DEV");
				} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("QA")) {
					inputfolderPath = prop.getProperty("EA_inputFolderPath_QA");
					processingFolderPath = prop.getProperty("EA_processingFolderPath_QA");
					archivedFolderPath = prop.getProperty("EA_archiveFolderPath_QA");
				} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("PROD")) {
					inputfolderPath = prop.getProperty("EA_inputFolderPath_PROD");
					processingFolderPath = prop.getProperty("EA_processingFolderPath_PROD");
					archivedFolderPath = prop.getProperty("EA_archiveFolderPath_PROD");
				} else {
					inputfolderPath = prop.getProperty("EA_inputFolderPath");
					processingFolderPath = prop.getProperty("EA_processingFolderPath");
					archivedFolderPath = prop.getProperty("EA_archiveFolderPath");
				}

				String pattern = jobDetailsObj.getFileName()+"*.txt";
				//System.out.println("File Pattern:"+pattern);

				//------------------------- Get the files related to the given process
				FileFilter fileFilter = new WildcardFileFilter(pattern);

				File inputFolder = new File(inputfolderPath);
				File processingFolder = new File(processingFolderPath);

				File[] processingFiles = processingFolder.listFiles(fileFilter);
				File[] inputFiles = inputFolder.listFiles(fileFilter);
				
				//DF20180111:KO369761 - Before processing this job , checking whether dependency messages are processed or not.
				int depedencyLag = 0;
				if(jobDetailsObj.getProcess().equalsIgnoreCase("MasterServiceSchedule")){
					depedencyLag = new KafkaConsumerLag().getConsumerLag("AssetGroupQueue", "Client_AssetGroupQueue");
					while(depedencyLag > 0){
						iLogger.info("MasterServiceSchedule is wait state because AssetGroup data was not yet processed. :"+depedencyLag);
						TimeUnit.SECONDS.sleep(30);
						depedencyLag = new KafkaConsumerLag().getConsumerLag("AssetGroupQueue", "Client_AssetGroupQueue");
					}
					
					depedencyLag = new KafkaConsumerLag().getConsumerLag("AssetTypeQueue", "Client_AssetTypeQueue");
					while(depedencyLag > 0){
						iLogger.info("MasterServiceSchedule is wait state because AssetType data was not yet processed. :"+depedencyLag);
						TimeUnit.SECONDS.sleep(30);
						depedencyLag = new KafkaConsumerLag().getConsumerLag("AssetTypeQueue", "Client_AssetTypeQueue");
					}
					
					depedencyLag = new KafkaConsumerLag().getConsumerLag("EngineTypeQueue", "Client_EngineTypeQueue");
					while(depedencyLag > 0){
						iLogger.info("MasterServiceSchedule is wait state because EngineType data was not yet processed. :"+depedencyLag);
						TimeUnit.SECONDS.sleep(30);
						depedencyLag = new KafkaConsumerLag().getConsumerLag("EngineTypeQueue", "Client_EngineTypeQueue");
					}
				}

				try
				{
					//First Process the files in processing folder
					for(int i=0; i<processingFiles.length; i++)
					{
						File processFile = processingFiles[i];
						iLogger.info("File to be processed:"+processFile.getName());
						consToProcessEAdata.newInstance(processFile,archivedFolderPath, jobDetailsObj.getProcess(), jobDetailsObj.getReprocessJobCode());
					}

					//Next Process the files in input folder
					for (int j = 0; j < inputFiles.length; j++) 
					{
						//get each file
						File inputFile = inputFiles[j];

						//move the file to processing folder
						File processingFile = new File(processingFolderPath,inputFile.getName());
						if(processingFile.exists())
							processingFile.delete();
						boolean fileMoveStatus = inputFile.renameTo(processingFile);

						if(fileMoveStatus)
							iLogger.info("File "+inputFile.getName()+" is moved successfully from Input to processing folder");

						consToProcessEAdata.newInstance(processingFile,archivedFolderPath, jobDetailsObj.getProcess(), jobDetailsObj.getReprocessJobCode());	       
					}
				}

				catch(Exception e)
				{
					fLogger.fatal("Exception :"+e);
				}

			}
		}

		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}

		finally
		{
			try
			{
				if(session.getTransaction().isActive())
				{
					session.getTransaction().commit();
				}
			}

			catch(Exception e)
			{
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);
			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}

		}
	}



	/** This method processes the required data - place the same onto the queue and move the file from processing folder to archived folder
	 * @param processingFile File to be processed
	 * @param archivedFolderPath archive path to which the file has to be moved to 
	 * @throws IOException
	 */
	public void processMasterServiceSchedule(File processingFile, String archivedFolderPath, String process, String reprocessJobCode)
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		String queuePath = "jms/queue/MasterServiceScheduleQueue";

		BufferedReader br = null;
		File archivedFile = null;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();

		try
		{
			//DF20150119 - Rajani Nagaraju - If the file is already processed(means if the record exists in EALogDetailsTable, skip the file)
			Query eaLogDetailsQ = session.createQuery(" from EALogDetailsEntity where " +
					"fileName='"+processingFile.getName().substring(0, processingFile.getName().lastIndexOf("."))+"'");
			Iterator eaLogDetailsItr = eaLogDetailsQ.list().iterator();
			int duplicateFile=0;
			if(eaLogDetailsItr.hasNext())
			{
				EALogDetailsEntity eaLog = (EALogDetailsEntity)eaLogDetailsItr.next();
				duplicateFile=1;
			}

			if(duplicateFile==1)
			{
				//Make an entry into FatalErrorLog and move the file to archived folder
				iLogger.info("File "+processingFile.getName()+" is already processed and hence skipping the same");

				//move the file to archived folder
				archivedFile = new File(archivedFolderPath,processingFile.getName());

				if(archivedFile.exists())
					archivedFile.delete();
				boolean moveStatus = processingFile.renameTo(archivedFile);

				if(moveStatus)
					iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");

				return;

			}
			/*DF20150619 - Rajani Nagaraju - WISE going down issue. Closing the session here and opening a new session during the commit to EALogDetails
			 * To avoid Session Closure issue and hence records not getting comitted to EALogdetails 
			 */
		}

		catch(Exception e)
		{
			fLogger.fatal(processingFile.getName()+": Exception :"+e);
		}

		finally
		{

			//DF20150508 - Rajani Nagaraju - Addig try catch around commit
			try
			{
				if(session.isOpen())
					if(session.getTransaction().isActive())
					{
						session.getTransaction().commit();
					}
			}
			catch(Exception e2)
			{
				fLogger.fatal("Exception in commiting the record:"+e2);

			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}
		}

		try
		{

			br = new BufferedReader(new FileReader(processingFile));
			String line = br.readLine();

			int runningNumber =1;
			int failureStatus=0;

			while (line != null) 
			{
				String[] msgString = line.split("\\|");

				MasterServiceScheduleInputContract inputObject = new MasterServiceScheduleInputContract();

				if(msgString.length>0)
					inputObject.setServicePlan(msgString[0]);

				if(msgString.length>1)
					inputObject.setServiceName(msgString[1]);

				if(msgString.length>2)
					inputObject.setScheduleName(msgString[2]);

				if(msgString.length>3)
					inputObject.setDbmsPartCode(msgString[3]);

				if(msgString.length>4)
					inputObject.setAssetGroupCode(msgString[4]);

				if(msgString.length>5)
					inputObject.setAssetTypeCode(msgString[5]);

				if(msgString.length>6)
					inputObject.setEngineTypeCode(msgString[6]);

				if(msgString.length>7)
					inputObject.setEngineHours(msgString[7]);

				if(msgString.length>8)
					inputObject.setDays(msgString[8]);

				inputObject.setFileRef(processingFile.getName().substring(0, processingFile.getName().lastIndexOf(".")));
				inputObject.setProcess(process);
				inputObject.setReprocessJobCode(reprocessJobCode);

				//generate MessageId
				String messageId = "MSG"+processingFile.getName().substring(0, processingFile.getName().lastIndexOf("."))+runningNumber;
				inputObject.setMessageId(messageId);

				//Place the object into MasterServiceScheduleQueue
				iLogger.info(inputObject.getScheduleName()+": Q Object Details - MasterServiceScheduleQueue");
				iLogger.info(inputObject.getScheduleName()+": --------------------------");
				iLogger.info("servicePlan:"+inputObject.getServicePlan()+",  "+"serviceName:"+inputObject.getServiceName()+",  " +
						"scheduleName:"+inputObject.getScheduleName()+",  "+"dbmsPartCode:"+inputObject.getDbmsPartCode()+",  " +
						"assetGroupCode:"+inputObject.getAssetGroupCode()+",  "+"assetTypeCode:"+inputObject.getAssetTypeCode()+",  " +
						"engineTypeCode:"+inputObject.getEngineTypeCode()+",  "+"engineHours:"+inputObject.getEngineHours()+",  "  +
						"days:"+inputObject.getDays() + 
						"FileRef:" +inputObject.getFileRef()+","+"" +
						"MsgID: "+inputObject.getMessageId()+", Process:"+inputObject.getProcess()+", " +
						" ReprocessJobCode: "+inputObject.getReprocessJobCode());

				MasterServiceScheduleQHandler queueObj = new MasterServiceScheduleQHandler();
				//DF20171212:KO369761-Changing interfaces queues to Kafka from Hornet queues.
				//String qDropStatus = queueObj.handleMasterServiceSchedule(queuePath,inputObject);
				String qDropStatus = queueObj.handleMasterServiceScheduleToKafkaQueue("MasterServiceScheduleQueue",inputObject);

				if(qDropStatus.equalsIgnoreCase("FAILURE"))
					failureStatus=1;

				line = br.readLine();
				runningNumber++;
			}

			if(failureStatus==0)
			{
				//move the file to archived folder
				archivedFile = new File(archivedFolderPath,processingFile.getName());

				if(archivedFile.exists())
					archivedFile.delete();
				boolean moveStatus = processingFile.renameTo(archivedFile);

				if(moveStatus)
					iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
			}

			//DF20160502 @Roopa calling MasterServiceScheduleQHandler queue consumer
			//DF20171212:KO369761-Commented because consumer will be continuous listener after changing interfaces queues to Kafka from Hornet queues.
			/*MasterServiceScheduleQHandler queueObj = new MasterServiceScheduleQHandler();

			iLogger.info(processingFile.getName()+"Before calling consumeMessage");
			String consumerStatus=queueObj.consumeMessage(queuePath);

			iLogger.info(processingFile.getName()+"After calling consumeMessage Status::"+consumerStatus);*/
		}
		catch(IOException e)
		{
			fLogger.fatal(processingFile.getName()+": Exception :"+e);
		}

		catch(Exception e)
		{
			fLogger.fatal(processingFile.getName()+": Exception :"+e);
		}

		finally
		{
			if(br!=null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fLogger.fatal(processingFile.getName()+": Exception :"+e);
				}	 
			//DF20150508 - Rajani Nagaraju - Addig try catch around commit
			//DF20150619 - Rajani Nagaraju - WISE Down Issue. Since all the txns are closed before, commenting the below code
			/*try
				{
					if(session.isOpen())
						if(session.getTransaction().isActive())
						{
							session.getTransaction().commit();
						}
				}
				catch(Exception e2)
				{
					fLogger.fatal("Exception :"+e2);
				}

				if(session.isOpen())
				{
					session.flush();
					session.close();
				} */
		}

		//move the file to archived folder
		/*archivedFile = new File(archivedFolderPath,processingFile.getName());

		if(archivedFile.exists())
			archivedFile.delete();
        boolean moveStatus = processingFile.renameTo(archivedFile);

        if(moveStatus)
        	infoLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");*/
	}
}
