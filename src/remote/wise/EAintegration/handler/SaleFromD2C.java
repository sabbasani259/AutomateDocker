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

import remote.wise.EAintegration.Qhandler.KafkaConsumerLag;
import remote.wise.EAintegration.Qhandler.SaleFromD2CQHandler;
import remote.wise.EAintegration.dataContract.SaleFromD2CInputContract;
import remote.wise.businessentity.EALogDetailsEntity;
import remote.wise.businessentity.JobDetails;
import remote.wise.businessobject.EAFileDetailsBO;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;

public class SaleFromD2C 
{
	public SaleFromD2C()
	{

	}

	public SaleFromD2C(File processingFile, String archivedFolderPath, String process, String reprocessJobCode)
	{
		Logger fLogger = FatalLoggerClass.logger;
		try 
		{
			processSaleFromD2C(processingFile, archivedFolderPath, process, reprocessJobCode);
		} 
		catch (Exception e) 
		{
			fLogger.fatal("Exception :"+processingFile+": "+e);
		}
	}


	public void handleSaleFromD2C()
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

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
			Query query = session.createQuery("from JobDetails where jobCode='SaleFromD2C' order by sequence");
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
				//System.out.println("File Pattern:"+pattern);

				//------------------------- Get the files related to the given process
				FileFilter fileFilter = new WildcardFileFilter(pattern);

				File inputFolder = new File(inputfolderPath);
				File processingFolder = new File(processingFolderPath);

				File[] processingFiles = processingFolder.listFiles(fileFilter);
				File[] inputFiles = inputFolder.listFiles(fileFilter);

				//DF20180111:KO369761 - Before processing this job , checking whether dependency messages are processed or not.
				int depedencyLag = 0;
				try{
					if(jobDetailsObj.getProcess().equalsIgnoreCase("SaleFromD2C")){
						depedencyLag = new KafkaConsumerLag().getConsumerLag("AssetGateOutQueue", "Client_AssetGateOutQueue");
						while(depedencyLag > 0){
							iLogger.info("SaleFromD2C is in wait state because GateOut data was not yet processed. :"+depedencyLag);
							TimeUnit.SECONDS.sleep(30);
							depedencyLag = new KafkaConsumerLag().getConsumerLag("AssetGateOutQueue", "Client_AssetGateOutQueue");
						}
						depedencyLag = new KafkaConsumerLag().getConsumerLag("DealerInfoQueue", "Client_DealerInfoQueue");
						while(depedencyLag > 0){
							iLogger.info("SaleFromD2C is in wait state because DealerInfo data was not yet processed. :"+depedencyLag);
							TimeUnit.SECONDS.sleep(30);
							depedencyLag = new KafkaConsumerLag().getConsumerLag("DealerInfoQueue", "Client_DealerInfoQueue");
						}
					}
				}catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("SaleFromD2C ::Exception in finding the depedencyLag:"+e.getMessage());
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
			e.printStackTrace();
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
	public void processSaleFromD2C(File processingFile, String archivedFolderPath, String process, String reprocessJobCode) 
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		String queuePath = "jms/queue/SaleFromD2CQueue";

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
			String processName="SaleFromD2C";
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

					SaleFromD2CInputContract inputObject = new SaleFromD2CInputContract();

					if(msgString.length>0)
						inputObject.setSellerCode(msgString[0]);

					if(msgString.length>1)
						inputObject.setBuyerCode(msgString[1]);

					if(msgString.length>2)
						inputObject.setDealerCode(msgString[2]);

					if(msgString.length>3)
						inputObject.setSerialNumber(msgString[3]);

					if(msgString.length>4)
						inputObject.setTransferDate(msgString[4]);


					inputObject.setFileRef(processingFile.getName().substring(0, processingFile.getName().lastIndexOf(".")));
					inputObject.setProcess(process);
					inputObject.setReprocessJobCode(reprocessJobCode);

					//generate MessageId
					String messageId = "MSG"+processingFile.getName().substring(0, processingFile.getName().lastIndexOf("."))+runningNumber;
					inputObject.setMessageId(messageId);

					//Place the object into SaleFromD2CQueue
					iLogger.info(inputObject.getSerialNumber()+": Q Object Details - Sale From D2C");
					iLogger.info(inputObject.getSerialNumber()+" --------------------------");
					iLogger.info(inputObject.getSellerCode()+","+inputObject.getBuyerCode()+","+inputObject.getDealerCode()+"," +
							inputObject.getSerialNumber()+","+inputObject.getSerialNumber()+"," +
							inputObject.getFileRef()+","+"" +
							inputObject.getMessageId()+","+inputObject.getProcess()+","+inputObject.getReprocessJobCode());

					SaleFromD2CQHandler queueObj = new SaleFromD2CQHandler();
					//DF20171212:KO369761-Changing interfaces queues to Kafka from Hornet queues.
					//String qDropStatus = queueObj.handleSaleFromD2C(queuePath,inputObject);
					String qDropStatus = queueObj.handleSaleFromD2CToKafkaQueue("SaleFromD2CQueue",inputObject);

					if(qDropStatus.equalsIgnoreCase("FAILURE")){
						failureStatus=1;
						failureCount++;
					}

					line = br.readLine();
					runningNumber++;
					fileDataCount++;
					//DefectId:20150616 @Suprava -EAInterface "File not found" status changes in Admin tab.

					if(runningNumber > 1){
						String machineNumber=msgString[3];
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
				// if(! (session.isOpen() ))
				//  {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
				if(session.getTransaction().isActive() && session.isDirty())
				{
					iLogger.info("Opening a new session");
					session = HibernateUtil.getSessionFactory().openSession();
				}
				session.getTransaction().begin();
				//     }

				try
				{
					EALogDetailsEntity eaLogDetails= new EALogDetailsEntity();
					eaLogDetails.setFileName(processingFile.getName().substring(0, processingFile.getName().lastIndexOf(".")));
					eaLogDetails.setProcess("SaleFromD2C");
					eaLogDetails.setProcessedTimestamp(new Timestamp(new Date().getTime()));
					eaLogDetails.setNoOfRecords(runningNumber-1);

					session.save(eaLogDetails);

					//DF20180214 :@Maniratnam ## Interfaces backTracking--inserting into interface log details table total data count and successfully put to q count
					CommonUtil commonutilobj = new CommonUtil();
					String iStatus=commonutilobj.insertInterfaceLogDetails(fileName,fileDataCount,fileDataCount-failureCount);
					iLogger.info("SaleFromD2C ::Status on inserting data into interface log details table :"+iStatus);

				}

				catch(Exception e)
				{
					fLogger.fatal(processingFile.getName()+": Exception :"+e);
				}

				//DF20150619 - Rajani Nagaraju - WISE Down issue - Adding finally block and closing the session
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
						fLogger.fatal("Exception in commiting the record:"+e2);

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
					iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");
			}

			//DF20171212:KO369761-Commented because consumer will be continuous listener after changing interfaces queues to Kafka from Hornet queues.
			//DF20160428 @Roopa calling SaleFromD2C queue consumer
			/*SaleFromD2CQHandler queueObj = new SaleFromD2CQHandler();

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
				}*/
		}

		//move the file to archived folder
		/*	archivedFile = new File(archivedFolderPath,processingFile.getName());

		if(archivedFile.exists())
			archivedFile.delete();
        boolean moveStatus = processingFile.renameTo(archivedFile);

        if(moveStatus)
        	infoLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");*/
	}
}
