package remote.wise.EAintegration.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.EAintegration.Qhandler.AssetInstallationDetailsQHandler;
import remote.wise.EAintegration.Qhandler.AssetPersonalityQHandler;
import remote.wise.EAintegration.Qhandler.KafkaConsumerLag;
import remote.wise.EAintegration.dataContract.AssetInstallationDetailsInputContract;
import remote.wise.businessentity.EALogDetailsEntity;
import remote.wise.businessentity.JobDetails;
import remote.wise.businessobject.EAFileDetailsBO;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;

public class AssetInstallationDetails 
{
	public AssetInstallationDetails()
	{

	}

	public AssetInstallationDetails(File processingFile, String archivedFolderPath, String process, String reprocessJobCode)
	{
		Logger fLogger = FatalLoggerClass.logger;
		try 
		{
			processAssetInstallationDetails(processingFile, archivedFolderPath, process, reprocessJobCode);
		} 
		catch (Exception e) 
		{
			fLogger.fatal("Exception :"+processingFile+": "+e);
		}
	}

	public void handleAssetInstallation()
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Asset Installation - Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();

		try
		{
			Query query = session.createQuery("from JobDetails where jobCode='AssetInstallationDetails' order by sequence");
			Iterator itr = query.list().iterator();

			while(itr.hasNext())
			{
				JobDetails jobDetailsObj = (JobDetails) itr.next();

				//System.out.println("Job Process: "+jobDetailsObj.getProcess());

				Class<?> theClass  = Class.forName("remote.wise.EAintegration.handler"+"."+jobDetailsObj.getProcess());
				Constructor<?> consToProcessEAdata = theClass.getConstructor(File.class,String.class, String.class, String.class);


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
				iLogger.info("Asset Installation - "+"File Pattern:"+pattern);

				//------------------------- Get the files related to the given process
				FileFilter fileFilter = new WildcardFileFilter(pattern);

				File inputFolder = new File(inputfolderPath);
				File processingFolder = new File(processingFolderPath);

				File[] processingFiles = processingFolder.listFiles(fileFilter);
				File[] inputFiles = inputFolder.listFiles(fileFilter);

				//DF20180111:KO369761 - Before processing this job , checking whether dependency messages are processed or not.
				int depedencyLag = 0;
				try{
					if(jobDetailsObj.getProcess().equalsIgnoreCase("AssetInstallationDetails")){
						depedencyLag = new KafkaConsumerLag().getConsumerLag("AssetPersonalityQueue", "Client_AssetPersonalityQueue");
						while(depedencyLag > 0){
							iLogger.info("AssetInstallationDetails is in wait state because AssetPersonality data was not yet processed. :"+depedencyLag);
							TimeUnit.SECONDS.sleep(30);
							depedencyLag = new KafkaConsumerLag().getConsumerLag("AssetPersonalityQueue", "Client_AssetPersonalityQueue");
						}

						depedencyLag = new KafkaConsumerLag().getConsumerLag("SaleFromD2CQueue", "Client_SaleFromD2CQueue");
						while(depedencyLag > 0){
							iLogger.info("AssetInstallationDetails is in wait state because SaleFromD2C data was not yet processed. :"+depedencyLag);
							TimeUnit.SECONDS.sleep(30);
							depedencyLag = new KafkaConsumerLag().getConsumerLag("SaleFromD2CQueue", "Client_SaleFromD2CQueue");
						}

					}
				}catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("AssetInstallationDetails ::Exception in finding the depedencyLag:"+e.getMessage());
				}

				try
				{
					//First Process the files in processing folder
					for(int i=0; i<processingFiles.length; i++)
					{
						File processFile = processingFiles[i];
						iLogger.info("Asset Installation - "+"File to be processed:"+processFile.getName());
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
							iLogger.info("Asset Installation - "+"File "+inputFile.getName()+" is moved successfully from Input to processing folder");

						consToProcessEAdata.newInstance(processingFile,archivedFolderPath, jobDetailsObj.getProcess(), jobDetailsObj.getReprocessJobCode());

					}
				}

				catch(Exception e)
				{
					fLogger.fatal("Asset Installation - "+"Exception :"+e);
				}

			}
		}

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Asset Installation - "+"Exception :"+e);
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
				fLogger.fatal("Asset Installation - "+"Exception in commiting the record:"+e);
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
	public void processAssetInstallationDetails(File processingFile, String archivedFolderPath, String process, String reprocessJobCode) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		String queuePath = "jms/queue/AssetInstallationQueue";

		BufferedReader br = null;
		File archivedFile = null;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Asset Installation - "+"Opening a new session");
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
				iLogger.info("Asset Installation - "+"File "+processingFile.getName()+" is already processed and hence skipping the same");

				//move the file to archived folder
				archivedFile = new File(archivedFolderPath,processingFile.getName());

				if(archivedFile.exists())
					archivedFile.delete();
				boolean moveStatus = processingFile.renameTo(archivedFile);

				if(moveStatus)
					iLogger.info("Asset Installation - "+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");

				return;

			}
			/*DF20150619 - Rajani Nagaraju - WISE going down issue. Closing the session here and opening a new session during the commit to EALogDetails
			 * To avoid Session Closure issue and hence records not getting comitted to EALogdetails 
			 */
		}
		catch(Exception e)
		{
			fLogger.fatal("Asset Installation - "+processingFile.getName()+": Exception :"+e);
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
				fLogger.fatal("Asset Installation - "+"Exception in commiting the record:"+e2);

			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}
		}
		iLogger.info("Asset Installation - "+"Closed a  session");

		try
		{
			br = new BufferedReader(new FileReader(processingFile));
			String line = br.readLine();

			int runningNumber =1;
			int failureStatus=0;
			int fileDataCount=0;
			int failureCount=0;
			List<String> serialNumberList = new LinkedList<String>();
			List<String> serialNumberList1 = new LinkedList<String>();
			String fileName = processingFile.getName().substring(0, processingFile.getName().lastIndexOf("."));
			//Shajesh :28-09-2020 : if Filename length is >60 , truncate the file name and save it to the corresponding table
			if(fileName != null & fileName.length() >60){
				iLogger.info("---------------- Before truncate FileName --------------------"+fileName);
				fileName = fileName.substring(0, 60);
				iLogger.info("---------------- After truncate FileName --------------------"+fileName);
			}
			String processName="AssetInstallationDetails";
			while (line != null) 
			{
				try{
					//DF20140811 - Rajani Nagaraju - To log the number of records received per each file into a table
					if(!(line.trim()!=null && line.trim().length()>0))
					{
						line = br.readLine();
						continue;
					} 

					String[] msgString = line.split("\\|");

					AssetInstallationDetailsInputContract inputObject = new AssetInstallationDetailsInputContract();

					if(msgString.length>0)
						inputObject.setSerialNumber(msgString[0]);

					if(msgString.length>1)
						inputObject.setInstallationDate(msgString[1]);

					if(msgString.length>2)
						inputObject.setDealerCode(msgString[2]);

					if(msgString.length>3)
						inputObject.setCustomerCode(msgString[3]);


					inputObject.setFileRef(processingFile.getName().substring(0, processingFile.getName().lastIndexOf(".")));
					inputObject.setProcess(process);
					inputObject.setReprocessJobCode(reprocessJobCode);

					//generate MessageId
					String messageId = "MSG"+processingFile.getName().substring(0, processingFile.getName().lastIndexOf("."))+runningNumber;
					inputObject.setMessageId(messageId);

					//Place the object into AssetGroupQueue
					iLogger.info(inputObject.getSerialNumber()+": Q Object Details - AssetInstallationQueue");
					iLogger.info(inputObject.getSerialNumber()+": --------------------------");
					iLogger.info("Asset Installation - processAssetInstallationDetails Input "+inputObject.getSerialNumber()+","+inputObject.getInstallationDate()+"," +
							inputObject.getDealerCode()+","+inputObject.getCustomerCode()+"," +
							inputObject.getFileRef()+","+"" +
							inputObject.getMessageId()+","+inputObject.getProcess()+","+inputObject.getReprocessJobCode());

					AssetInstallationDetailsQHandler queueObj = new AssetInstallationDetailsQHandler();

					iLogger.info("Asset Installation - processAssetInstallationDetails Before Calling AssetInstallation Q Handler");
					//DF20171212:KO369761-Changing interfaces queues to Kafka from Hornet queues.
					//String qDropStatus = queueObj.handleAssetInstallationDetails(queuePath,inputObject);
					String qDropStatus = queueObj.handleAssetInstallationToKafkaQueue("AssetInstallationQueue",inputObject);

					iLogger.info("Asset Installation - processAssetInstallationDetails After Calling AssetInstallation Q Handler Status::"+qDropStatus);

					if(qDropStatus.equalsIgnoreCase("FAILURE")){
						failureStatus=1;
						failureCount++;
					}

					line = br.readLine();
					runningNumber++;
					fileDataCount++;
					//DefectId:20150616 @Suprava -EAInterface "File not found" status changes in Admin tab.

					if(runningNumber > 1){
						String machineNumber=msgString[0];
						if(runningNumber <= 51){
							serialNumberList.add(machineNumber);
						}
						else{
							serialNumberList1.add(machineNumber)	;
						}
						if(serialNumberList!=null && serialNumberList.size()==50 && serialNumberList1!=null && serialNumberList1.size()==0){
							EAFileDetailsBO eaFileDetailsBO = new EAFileDetailsBO();
							eaFileDetailsBO.setEAFileDetailsEntity(serialNumberList, fileName, processName);
						}
					}
				}catch (Exception e) {
					e.printStackTrace();
					fLogger.fatal("Exception in reading the message:"+line+" from file:"+e.getMessage());
				}
			}
			if((serialNumberList!=null && serialNumberList.size() < 50) || (serialNumberList1!=null && serialNumberList1.size()>1))
			{
				EAFileDetailsBO eaFileDetailsBO = new EAFileDetailsBO();
				if(serialNumberList.size() < 50){
					eaFileDetailsBO.setEAFileDetailsEntity(serialNumberList, fileName, processName);
				}
				else if(serialNumberList1.size() > 1){
					eaFileDetailsBO.setEAFileDetailsEntity(serialNumberList1, fileName, processName);  
				}
			}
			//End:20150616

			//START - DF20140811 - Rajani Nagaraju - To log the number of records received per each file into a table
			if(runningNumber>1)
			{
				//DF20150618 - Rajani Nagaraju - WISE Down issue - Since the Session is closed earlier, need to open the session here. Hence commenting IF condition
				//if(! (session.isOpen() ))
				// {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
				if(session.getTransaction().isActive() && session.isDirty())
				{
					iLogger.info("Asset Installation - Opening a new session");
					session = HibernateUtil.getSessionFactory().openSession();
				}

				session.getTransaction().begin();
				// }

				try
				{
					EALogDetailsEntity eaLogDetails= new EALogDetailsEntity();
					eaLogDetails.setFileName(processingFile.getName().substring(0, processingFile.getName().lastIndexOf(".")));
					eaLogDetails.setProcess("AssetInstallationDetails");
					eaLogDetails.setProcessedTimestamp(new Timestamp(new Date().getTime()));
					eaLogDetails.setNoOfRecords(runningNumber-1);

					session.save(eaLogDetails);

					//DF20180207 :@Maniratnam ## Interfaces backTracking--inserting into interface log details table total data count and successfully put to q count
					CommonUtil commonutilobj = new CommonUtil();
					String iStatus=commonutilobj.insertInterfaceLogDetails(fileName,fileDataCount,fileDataCount-failureCount);
					iLogger.info("AssetInstallationDetails ::Status on inserting data into interface log details table :"+iStatus);

				}

				catch(Exception e)
				{
					fLogger.fatal("Asset Installation - "+processingFile.getName()+": Exception :"+e);
				}

				//DF20150618 - Rajani Nagaraju - WISE Down issue - Adding finally block and closing the session
				finally
				{
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
						fLogger.fatal("Asset Installation - "+"Exception in commiting the record:"+e2);

					}

					if(session.isOpen())
					{
						session.flush();
						session.close();
					}
				}
			}
			//END - DF20140811 - Rajani Nagaraju - To log the number of records received per each file into a table

			if(failureStatus==0)
			{
				//move the file to archived folder
				archivedFile = new File(archivedFolderPath,processingFile.getName());

				if(archivedFile.exists())
					archivedFile.delete();
				boolean moveStatus = processingFile.renameTo(archivedFile);

				if(moveStatus)
					iLogger.info("Asset Installation - "+"File "+processingFile.getName()+" is moved successfully from processing to archived folder");
			}

			//DF20171212:KO369761-Commented because consumer will be continuous listener after changing interfaces queues to Kafka from Hornet queues.
			//DF20160502 @Roopa calling AssetInstallationDetails queue consumer
			/*AssetInstallationDetailsQHandler queueObj = new AssetInstallationDetailsQHandler();

			iLogger.info(processingFile.getName()+"Before calling consumeMessage");
			String consumerStatus=queueObj.consumeMessage(queuePath);

			iLogger.info(processingFile.getName()+"After calling consumeMessage Status::"+consumerStatus);*/
		}

		catch(IOException e)
		{
			fLogger.fatal("Asset Installation - "+processingFile.getName()+": Exception :"+e);
		}

		catch(Exception e)
		{
			fLogger.fatal("Asset Installation - "+processingFile.getName()+": Exception :"+e);
		}

		finally
		{
			if(br!=null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("Asset Installation - "+processingFile.getName()+": Exception :"+e);
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
				}*/
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
