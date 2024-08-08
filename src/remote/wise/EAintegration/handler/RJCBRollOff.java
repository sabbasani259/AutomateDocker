package remote.wise.EAintegration.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.EAintegration.EAclient.JcbRollOffClient;
import remote.wise.EAintegration.Qhandler.JcbRollOffQHandler;
import remote.wise.EAintegration.dataContract.JcbRollOffInputContract;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

public class RJCBRollOff 
{
	public void reprocessJCBRollOff()
	{
		Logger fLogger = FatalLoggerClass.logger;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Logger iLogger = InfoLoggerClass.logger;
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();
		String messgId=null;
		String serialNumber =null;
		String engineNumber = null;
		String chasisNumber =null;
		String response = null;
		//DF20140715 - Rajani Nagaraju - New Parameters added for JCB Roll Off Service to be handled while reprocessing
		String make=null;
		String buildDate=null;
		String machineNumber=null;
		String process = "RollOff";
		String reprocess = "RJCBRollOff";
		String fileRef = null;
		String[] messageSplit = null;

		try
		{
			ErrorMessageHandler messageHandlerObj = new ErrorMessageHandler();
			List<JcbRollOffInputContract> inputContractList = new LinkedList<JcbRollOffInputContract>();


			//get the currentDate
			Date currentDate = new Date();
			Timestamp currentTimestamp = new Timestamp(currentDate.getTime());

			//List all the messages scheduled to be reprocessed for the current time - Reprocess the same in the order of defined sequence of processes
			//DF20180226:KO369761 - group by messageString added in the query because its giving multiple rows with same message.
			Query query = session.createQuery("select a.messageId, a.messageString, a.fileName, a.process," +
					" a.reprocessJobCode from FaultDetails a, JobDetails b where a.reprocessJobCode = b.reprocessJobCode and " +
					" a.process = b.process and a.reprocessJobCode = 'RJCBRollOff' and a.reprocessTimeStamp <= '"+currentTimestamp+"' " +
					" group by a.messageString order by b.process, b.sequence, a.reprocessTimeStamp ");
			
			iLogger.info("query to be executed ::"+query.getQueryString());

			Iterator itr = query.list().iterator();
			Object[] result = null;


			while(itr.hasNext())
			{
				serialNumber =null;
				engineNumber = null;
				chasisNumber =null;
				make=null;
				buildDate=null;
				machineNumber=null;

				result = (Object[]) itr.next();

				String messageString = result[1].toString();
				messageSplit = messageString.split("\\|");
				int paramSize =messageSplit.length;

				if(paramSize>0)
					serialNumber = messageSplit[0];

				if(paramSize>1)
					engineNumber = messageSplit[1];

				if(paramSize>2)
					chasisNumber =  messageSplit[2];

				//DF20140715 - Rajani Nagaraju - New Parameters added for JCB Roll Off Service to be handled while reprocessing
				if(paramSize>3)
					make=messageSplit[3];

				if(paramSize>4)
					buildDate=messageSplit[4];

				if(paramSize>5)
					machineNumber=messageSplit[5];

				//Fill the input contract Object
				JcbRollOffInputContract inputContractObj = new JcbRollOffInputContract();
				inputContractObj.setChasisNumber(chasisNumber);
				inputContractObj.setEngineNumber(engineNumber);
				if(result[2]!=null)
					inputContractObj.setFileRef(result[2].toString());
				if(result[0]!=null)
				{
					inputContractObj.setMessageId(result[0].toString()+"|R");
					messgId=result[0].toString()+"|R";
				}

				if(result[3]!=null){
					inputContractObj.setProcess(result[3].toString());
					process = result[3].toString();
				}
				if(result[4]!=null){
					inputContractObj.setReprocessJobCode(result[4].toString());
					reprocess = result[4].toString();
				}
				inputContractObj.setSerialNumber(serialNumber);

				//DF20140715 - Rajani Nagaraju - New Parameters added for JCB Roll Off Service to be handled while reprocessing
				inputContractObj.setMake(make);
				inputContractObj.setBuiltDate(buildDate);
				inputContractObj.setMachineNumber(machineNumber);

				inputContractList.add(inputContractObj);
			}
			iLogger.info("reprocessing records size ::"+inputContractList.size());

			for(int i=0; i<inputContractList.size(); i++)
			{
				//DF20180208:KO369761 - Adding the reprocess msgs to the Kafka topic.
				/*JcbRollOffClient clientObj = new JcbRollOffClient();
        		response = clientObj.invokeJcbRollOffService(inputContractList.get(i));*/
				iLogger.info("Before putting into the Q::"+inputContractList.get(i).getSerialNumber()+"::"+inputContractList.get(i).getMessageId());
				JcbRollOffQHandler qObj = new JcbRollOffQHandler();
				qObj.handleJcbRollOffToKafkaQueue("JcbRollOffQueue",inputContractList.get(i));

				/*if(response.equalsIgnoreCase("FAILURE"))
        		{
        			messageHandlerObj.handleErrorMessages(result[0].toString(), result[1].toString(), result[2].toString(), result[3].toString(), result[4].toString());
        		}*/

				//DF20140715 - Rajani Nagaraju - Handle Robust Logging
				/*if(response.split("-").length>1)
        		{
        			response = response.split("-")[0].trim();

        		}

        		if(response.equalsIgnoreCase("SUCCESS"))
        		{
        			messageHandlerObj.deleteErrorMessage(inputContractList.get(i).getMessageId());
        		}*/
			}

			/* if(! (session.isOpen() ))
              {
                          session = HibernateUtil.getSessionFactory().getCurrentSession();
                          session.getTransaction().begin();
              }*/

		}

		catch(Exception e)
		{
			fLogger.fatal("EA ReProcessing: RAssetRolloffFromJCB: "+messgId+ " Fatal Exception :"+e);
		}

		//DF20180207:KO369761 - reprocessing the data which were stored in files.
		try{
			String reprocessFolderPath = "";
			String messageString = null;

			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			reprocessFolderPath = prop.getProperty("EA_reprocessingFolderPath"); 

			String pattern = "AssetRollOff"+"*.txt";
			FileFilter fileFilter = new WildcardFileFilter(pattern);
			File reprocessFolder = new File(reprocessFolderPath);
			File[] reprocessFiles = reprocessFolder.listFiles(fileFilter);
			FileReader fileReader = null;
			BufferedReader bufferedReader = null;

			for (int j = 0; j < reprocessFiles.length; j++) 
			{
				int i=0;
				//get each file
				File reprocessFile = reprocessFiles[j];
				iLogger.info("RJCBRollOff:: from files with file name:"+reprocessFile);
				if(reprocessFile.getName().split("_").length>2)
					fileRef = reprocessFile.getName().split("_")[0]+reprocessFile.getName().split("_")[1];
				else
					fileRef = reprocessFile.getName().substring(0, reprocessFile.getName().lastIndexOf("."));
				fileReader = new FileReader(reprocessFile);
				bufferedReader = new BufferedReader(fileReader);

				while((messageString=bufferedReader.readLine())!=null){
					messageSplit = messageString.split("\\|");
					int paramSize =messageSplit.length;

					if(paramSize>0)
						serialNumber = messageSplit[0];

					if(paramSize>1)
						engineNumber = messageSplit[1];

					if(paramSize>2)
						chasisNumber =  messageSplit[2];

					//DF20140715 - Rajani Nagaraju - New Parameters added for JCB Roll Off Service to be handled while reprocessing
					if(paramSize>3)
						make=messageSplit[3];

					if(paramSize>4)
						buildDate=messageSplit[4];

					if(paramSize>5)
						machineNumber=messageSplit[5];
					i++;
					//Fill the input contract Object
					JcbRollOffInputContract inputContractObj = new JcbRollOffInputContract();
					inputContractObj.setChasisNumber(chasisNumber);
					inputContractObj.setEngineNumber(engineNumber);
					inputContractObj.setSerialNumber(serialNumber);
					inputContractObj.setMake(make);
					inputContractObj.setBuiltDate(buildDate);
					inputContractObj.setMachineNumber(machineNumber);
					inputContractObj.setMessageId("MSG"+fileRef+i+"|R");
					inputContractObj.setProcess(process);
					inputContractObj.setReprocessJobCode(reprocess);
					inputContractObj.setFileRef(fileRef);

					JcbRollOffQHandler qObj = new JcbRollOffQHandler();
					qObj.handleJcbRollOffToKafkaQueue("JcbRollOffQueue",inputContractObj);
				}
				bufferedReader.close();
				reprocessFile.delete();
			}

		}catch(Exception e)
		{
			fLogger.fatal("EA ReProcessing: RJCBRollOff: "+messgId+ " Fatal Exception :"+e);
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
				fLogger.fatal("Exception :"+e2);
			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}

		}
	}
}
