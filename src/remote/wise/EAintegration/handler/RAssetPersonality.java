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

import remote.wise.EAintegration.EAclient.AssetPersonalityClient;
import remote.wise.EAintegration.Qhandler.AssetPersonalityQHandler;
import remote.wise.EAintegration.dataContract.AssetPersonalityInputContract;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

public class RAssetPersonality 
{
	public void reprocessAssetPersonality()
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
		String engineNumber = null;
		String assetGroupCode = null;
		String assetTypeCode = null;
		String engineTypeCode = null;
		String assetBuiltDate = null;
		String make = null;
		String fuelCapacity = null;
		String serialNumber = null;
		String response = null;
		String fileRef;
		String[] messageSplit = null;
		String process = "AssetPersonality";
		String reprocess = "RAssetPersonality";

		try
		{
			ErrorMessageHandler messageHandlerObj = new ErrorMessageHandler();
			List<AssetPersonalityInputContract> inputContractList = new LinkedList<AssetPersonalityInputContract>();


			//get the currentDate
			Date currentDate = new Date();
			Timestamp currentTimestamp = new Timestamp(currentDate.getTime());

			//List all the messages scheduled to be reprocessed for the current time - Reprocess the same in the order of defined sequence of processes
			//DF20180226:KO369761 - group by messageString added in the query because its giving multiple rows with same message.
			Query query = session.createQuery("select a.messageId, a.messageString, a.fileName, a.process," +
					" a.reprocessJobCode from FaultDetails a, JobDetails b where a.reprocessJobCode = b.reprocessJobCode and " +
					" a.process = b.process and a.reprocessJobCode = 'RAssetPersonality' and a.reprocessTimeStamp <= '"+currentTimestamp+"' " +
					" group by a.messageString order by b.process, b.sequence, a.reprocessTimeStamp ");

			Iterator itr = query.list().iterator();
			Object[] result = null;

			while(itr.hasNext())
			{
				result = (Object[]) itr.next();

				engineNumber = null;
				assetGroupCode = null;
				assetTypeCode = null;
				engineTypeCode = null;
				assetBuiltDate = null;
				make = null;
				fuelCapacity = null;
				serialNumber = null;

				String messageString = result[1].toString();
				messageSplit = messageString.split("\\|");
				int paramSize =messageSplit.length;

				if(paramSize>0)
					engineNumber = messageSplit[0];
				if(paramSize>1)
					assetGroupCode = messageSplit[1];
				if(paramSize>2)
					assetTypeCode = messageSplit[2];
				if(paramSize>3)
					engineTypeCode = messageSplit[3];
				if(paramSize>4)
					assetBuiltDate = messageSplit[4];
				if(paramSize>5)
					make = messageSplit[5];
				if(paramSize>6)
					fuelCapacity = messageSplit[6];
				if(paramSize>7)
					serialNumber = messageSplit[7];

				//Fill the input contract Object
				AssetPersonalityInputContract inputContractObj = new AssetPersonalityInputContract();
				inputContractObj.setEngineNumber(engineNumber);
				inputContractObj.setAssetGroupCode(assetGroupCode);
				inputContractObj.setAssetTypeCode(assetTypeCode);
				inputContractObj.setEngineTypeCode(engineTypeCode);
				inputContractObj.setAssetBuiltDate(assetBuiltDate);
				inputContractObj.setMake(make);
				inputContractObj.setFuelCapacity(fuelCapacity);
				inputContractObj.setSerialNumber(serialNumber);

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

				inputContractList.add(inputContractObj);
			}

			for(int i=0; i<inputContractList.size(); i++)
			{
				/*AssetPersonalityClient clientObj = new AssetPersonalityClient();
        		response = clientObj.invokeAssetPersonalityService(inputContractList.get(i));*/
				//DF20180209 :@Maniratnam ## Reprocess jobs :: Adding reprocess job to kafka topic
				AssetPersonalityQHandler obj = new AssetPersonalityQHandler();
				obj.handleAssetPersonalityToKafkaQueue("AssetPersonalityQueue", inputContractList.get(i));


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
			fLogger.fatal("EA ReProcessing: RAssetPersonality: "+messgId+ " Fatal Exception :"+e);
		}

		try {

			//DF20180209 :@Maniratnam ## Reprocess jobs :: handling from file
			String reprocessFolderPath = "";

			String messageString = null; 
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			reprocessFolderPath = prop.getProperty("EA_reprocessingFolderPath"); 

			String pattern = "AssetPersonality" + "*.txt";

			FileFilter fileFilter = new WildcardFileFilter(pattern);

			File reprocessFolder = new File(reprocessFolderPath);

			File[] reprocessFiles = reprocessFolder.listFiles(fileFilter);

			FileReader fileReader = null;

			BufferedReader bufferedReader = null;

			for (int j = 0; j < reprocessFiles.length; j++)

			{

				int i = 0;

				// get each file

				File reprocessFile = reprocessFiles[j];
				iLogger.info("RAssetPersonality:: from files with file name:"+reprocessFile);
				if (reprocessFile.getName().split("_").length > 2)

					fileRef = reprocessFile.getName().split("_")[0]
							+ reprocessFile.getName().split("_")[1];

				else

					fileRef = reprocessFile.getName().substring(0,
							reprocessFile.getName().lastIndexOf("."));

				fileReader = new FileReader(reprocessFile);

				bufferedReader = new BufferedReader(fileReader);

				while ((messageString = bufferedReader.readLine()) != null) {

					messageSplit = messageString.split("\\|");

					int paramSize = messageSplit.length;
					if(paramSize>0)
						engineNumber = messageSplit[0];
					if(paramSize>1)
						assetGroupCode = messageSplit[1];
					if(paramSize>2)
						assetTypeCode = messageSplit[2];
					if(paramSize>3)
						engineTypeCode = messageSplit[3];
					if(paramSize>4)
						assetBuiltDate = messageSplit[4];
					if(paramSize>5)
						make = messageSplit[5];
					if(paramSize>6)
						fuelCapacity = messageSplit[6];
					if(paramSize>7)
						serialNumber = messageSplit[7];

					i++;

					// Fill the input contract Object
					AssetPersonalityInputContract inputContractObj = new AssetPersonalityInputContract();
					inputContractObj.setEngineNumber(engineNumber);
					inputContractObj.setAssetGroupCode(assetGroupCode);
					inputContractObj.setAssetTypeCode(assetTypeCode);
					inputContractObj.setEngineTypeCode(engineTypeCode);
					inputContractObj.setAssetBuiltDate(assetBuiltDate);
					inputContractObj.setMake(make);
					inputContractObj.setFuelCapacity(fuelCapacity);
					inputContractObj.setSerialNumber(serialNumber);
					inputContractObj.setMessageId("MSG" + fileRef + i + "|R");
					inputContractObj.setFileRef(fileRef);
					inputContractObj.setProcess(process);
					inputContractObj.setReprocessJobCode(reprocess);
					AssetPersonalityQHandler obj = new AssetPersonalityQHandler();
					obj.handleAssetPersonalityToKafkaQueue("PersonalityQ",inputContractObj);
				}
				bufferedReader.close();
				reprocessFile.delete();

			}

		} catch (Exception e)

		{

			fLogger.fatal("EA ReProcessing: RAssetPersonality: " + messgId
					+ " Fatal Exception :" + e);

		}

		finally
		{
			//DF20140715 - Rajani Nagaraju - To Avoid Session Close Exception
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
