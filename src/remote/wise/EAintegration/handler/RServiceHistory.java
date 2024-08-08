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

import remote.wise.EAintegration.EAclient.ServiceHistoryClient;
import remote.wise.EAintegration.Qhandler.ServiceHistoryQHandler;
import remote.wise.EAintegration.dataContract.ServiceHistoryInputContract;
import remote.wise.businessentity.JobDetails;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

public class RServiceHistory 
{
	public void reprocessServiceHistory()
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
        String serialNumber = null;
    	String dealerCode = null;
    	String jobCardNumber = null;
    	String dbmsPartCode = null;
    	String servicedDate = null;
    	String jobCardDetails = null;
    	String callTypeId = null;
    	String response = null;
    	String fileRef;
		String process = "ServiceHistory";
		String reprocess = "RServiceHistory";
		String[] messageSplit = null;
        
        try
        {
        	ErrorMessageHandler messageHandlerObj = new ErrorMessageHandler();
        	List<ServiceHistoryInputContract> inputContractList = new LinkedList<ServiceHistoryInputContract>();
        	
        	//get the currentDate
        	Date currentDate = new Date();
        	Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
        	
        	//List all the messages scheduled to be reprocessed for the current time - Reprocess the same in the order of defined sequence of processes
        	Query query = session.createQuery("select a.messageId, a.messageString, a.fileName, a.process," +
        			" a.reprocessJobCode from FaultDetails a, JobDetails b where a.reprocessJobCode = b.reprocessJobCode and " +
        			" a.process = b.process and a.reprocessJobCode = 'RServiceHistory' and a.reprocessTimeStamp <= '"+currentTimestamp+"' " +
        					" group by a.messageString order by b.process, b.sequence, a.reprocessTimeStamp ");
        	
        	Iterator itr = query.list().iterator();
        	Object[] result = null;
        	
        	while(itr.hasNext())
        	{
        		result = (Object[]) itr.next();
        		
        		serialNumber = null;
        		dealerCode = null;
        		jobCardNumber = null;
        		dbmsPartCode = null;
        		servicedDate = null;
        		jobCardDetails=null;
        		callTypeId=null;
            	        		
        		String messageString = result[1].toString();
        		messageSplit = messageString.split("\\|");
        		int paramSize =messageSplit.length;
        		
        		if(paramSize>0)
        			serialNumber = messageSplit[0];
        		if(paramSize>1)
        			dealerCode = messageSplit[1];
        		if(paramSize>2)
        			jobCardNumber = messageSplit[2];
        		if(paramSize>3)
        			dbmsPartCode = messageSplit[3];
        		if(paramSize>4)
        			servicedDate = messageSplit[4];
        		if(paramSize>5)
        			jobCardDetails = messageSplit[5];
        		if(paramSize>6)
        			callTypeId = messageSplit[6];
        		        		
        		//Fill the input contract Object
        		ServiceHistoryInputContract inputContractObj = new ServiceHistoryInputContract();
        		inputContractObj.setSerialNumber(serialNumber);
        		inputContractObj.setDealerCode(dealerCode);
        		inputContractObj.setJobCardNumber(jobCardNumber);
        		inputContractObj.setDbmsPartCode(dbmsPartCode);
        		inputContractObj.setServicedDate(servicedDate);
        		inputContractObj.setJobCardDetails(servicedDate);
        		inputContractObj.setCallTypeId(servicedDate);
        		
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
        		//DF20180214 :@KO369761 ## Reprocess jobs :: Adding reprocess job to kafka topic
        		/*ServiceHistoryClient clientObj = new ServiceHistoryClient();
        		response = clientObj.invokeServiceHistory(inputContractList.get(i));*/
        		
        		ServiceHistoryQHandler queObj = new ServiceHistoryQHandler();
        		queObj.handleServiceHistoryDetailsToKafkaQueue("ServiceHistoryQueue", inputContractList.get(i));
        		
        		
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
        	fLogger.fatal("EA ReProcessing: RAssetServiceDetails: "+messgId+ " Fatal Exception :"+e);
		}
        
        try {
			//DF20180214 :@Maniratnam ## Reprocess jobs :: handling from file
			String reprocessFolderPath = "";

			String messageString = null;
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			reprocessFolderPath = prop.getProperty("EA_reprocessingFolderPath");
			String pattern = "AssetServiceDetails" + "*.txt";

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
				iLogger.info("RServiceHistory:: from files with file name:"+reprocessFile);
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

					int paramSize =messageSplit.length;
	        		
	        		if(paramSize>0)
	        			serialNumber = messageSplit[0];
	        		if(paramSize>1)
	        			dealerCode = messageSplit[1];
	        		if(paramSize>2)
	        			jobCardNumber = messageSplit[2];
	        		if(paramSize>3)
	        			dbmsPartCode = messageSplit[3];
	        		if(paramSize>4)
	        			servicedDate = messageSplit[4];
	        		if(paramSize>5)
	        			jobCardDetails = messageSplit[5];
	        		if(paramSize>6)
	        			callTypeId = messageSplit[6];
	        		        		
	        		//Fill the input contract Object
	        		ServiceHistoryInputContract inputContractObj = new ServiceHistoryInputContract();
	        		inputContractObj.setSerialNumber(serialNumber);
	        		inputContractObj.setDealerCode(dealerCode);
	        		inputContractObj.setJobCardNumber(jobCardNumber);
	        		inputContractObj.setDbmsPartCode(dbmsPartCode);
	        		inputContractObj.setServicedDate(servicedDate);
	        		inputContractObj.setJobCardDetails(jobCardDetails);
	        		inputContractObj.setCallTypeId(callTypeId);
					inputContractObj.setMessageId("MSG" + fileRef + i + "|R");
					inputContractObj.setFileRef(fileRef);
					inputContractObj.setProcess(process);
					inputContractObj.setReprocessJobCode(reprocess);

	        		ServiceHistoryQHandler queObj = new ServiceHistoryQHandler();
	        		queObj.handleServiceHistoryDetailsToKafkaQueue("ServiceHistoryQueue", inputContractObj);

					i++;
				}
				bufferedReader.close();
				reprocessFile.delete();

			}

		} catch (Exception e)

		{

			fLogger.fatal("EA ReProcessing: RServiceHistory: " + messgId
					+ " Fatal Exception :" + e);

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
