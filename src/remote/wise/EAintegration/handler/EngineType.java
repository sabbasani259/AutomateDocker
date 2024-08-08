package remote.wise.EAintegration.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.EAintegration.Qhandler.AssetPersonalityQHandler;
import remote.wise.EAintegration.Qhandler.EngineTypeQHandler;
import remote.wise.EAintegration.dataContract.EngineTypeInputContract;
import remote.wise.businessentity.JobDetails;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

public class EngineType 
{
	public EngineType()
	{

	}

	public EngineType(File processingFile, String archivedFolderPath, String process, String reprocessJobCode)
	{
		Logger fLogger = FatalLoggerClass.logger;
		try 
		{
			processEngineTypeData(processingFile, archivedFolderPath, process, reprocessJobCode);
		} 
		catch (IOException e) 
		{
			fLogger.fatal("Exception :"+processingFile+": "+e);
		}
	}

	public void handleEngineTypeDetails()
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
			Query query = session.createQuery("from JobDetails where jobCode='EngineType' order by sequence");
			Iterator itr = query.list().iterator();

			while(itr.hasNext())
			{
				JobDetails jobDetailsObj = (JobDetails) itr.next();

				//System.out.println("Job Process: "+jobDetailsObj.getProcess());
				EngineType fileObj = new EngineType();

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

				//------------------------- Get the files related to Engine Type
				FileFilter fileFilter = new WildcardFileFilter(pattern);

				File inputFolder = new File(inputfolderPath);
				File processingFolder = new File(processingFolderPath);

				File[] processingFiles = processingFolder.listFiles(fileFilter);
				File[] inputFiles = inputFolder.listFiles(fileFilter);

				try
				{
					//First Process the files in processing folder
					for(int i=0; i<processingFiles.length; i++)
					{
						File processFile = processingFiles[i];
						iLogger.info("File to be processed:"+processFile.getName());
						fileObj.processEngineTypeData(processFile,archivedFolderPath, jobDetailsObj.getProcess(), jobDetailsObj.getReprocessJobCode());
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

						fileObj.processEngineTypeData(processingFile,archivedFolderPath, jobDetailsObj.getProcess(), jobDetailsObj.getReprocessJobCode());	       
					}
				}

				catch(IOException e)
				{
					e.printStackTrace();
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
	public void processEngineTypeData(File processingFile, String archivedFolderPath, String process, String reprocessJobCode) throws IOException
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		String queuePath = "jms/queue/EngineTypeQueue";

		BufferedReader br = null;
		File archivedFile = null;

		try
		{
			br = new BufferedReader(new FileReader(processingFile));
			String line = br.readLine();

			int runningNumber =1;

			while (line != null) 
			{
				String[] msgString = line.split("\\|");

				EngineTypeInputContract inputObject = new EngineTypeInputContract();

				if(msgString.length>0)
					inputObject.setEngineTypeName(msgString[0]);

				if(msgString.length>1)
					inputObject.setEngineTypeCode(msgString[1]);

				inputObject.setFileRef(processingFile.getName().substring(0, processingFile.getName().lastIndexOf(".")));
				inputObject.setProcess(process);
				inputObject.setReprocessJobCode(reprocessJobCode);

				//generate MessageId
				String messageId = "MSG"+processingFile.getName().substring(0, processingFile.getName().lastIndexOf("."))+runningNumber;
				inputObject.setMessageId(messageId);

				//Place the object into AssetTypeQueue
				iLogger.info(inputObject.getEngineTypeCode()+": Q Object Details - EngineTypeQueue");
				iLogger.info(inputObject.getEngineTypeCode()+" --------------------------");
				iLogger.info(inputObject.getEngineTypeName()+","+inputObject.getEngineTypeCode()+","+inputObject.getFileRef()+","+"" +
						inputObject.getMessageId()+","+inputObject.getProcess()+","+inputObject.getReprocessJobCode());

				EngineTypeQHandler queueObj = new EngineTypeQHandler();
				//DF20171212:KO369761-Changing interfaces queues to Kafka from Hornet queues.
				//queueObj.handleEngineTypeDetails(queuePath,inputObject);
				queueObj.handleEngineTypeDetailsToKafkaQueue("EngineTypeQueue",inputObject);

				line = br.readLine();
				runningNumber++;
			}
		}

		catch(Exception e)
		{
			fLogger.fatal(processingFile.getName()+": Exception :"+e);
		}

		finally
		{
			if(br!=null)
				br.close();	 
		}

		//move the file to archived folder
		archivedFile = new File(archivedFolderPath,processingFile.getName());

		if(archivedFile.exists())
			archivedFile.delete();
		boolean moveStatus = processingFile.renameTo(archivedFile);

		if(moveStatus)
			iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");

		//DF20171212:KO369761-Commented because consumer will be continuous listener after changing interfaces queues to Kafka from Hornet queues.
		//DF20160502 @Roopa calling EngineType queue consumer
		/*EngineTypeQHandler queueObj = new EngineTypeQHandler();

		iLogger.info(processingFile.getName()+"Before calling consumeMessage");
		String consumerStatus = null;
		try {
			consumerStatus = queueObj.consumeMessage(queuePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		iLogger.info(processingFile.getName()+"After calling consumeMessage Status::"+consumerStatus);*/

	}
}
