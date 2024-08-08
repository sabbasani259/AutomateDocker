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

import remote.wise.EAintegration.Qhandler.DealerInfoQHandler;
import remote.wise.EAintegration.dataContract.DealerInfoInputContract;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

public class RDealerInfo 
{
	public void reprocessDealerInfo()
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
        String dealerCode=null;
    	String dealerName=null;
    	String addressLine1=null;
    	String addressLine2=null;
    	String city=null;
    	String zipCode=null;
    	String state=null;
    	String zone=null;
    	String country=null;
    	String email=null;
    	String contactNumber=null;
    	String fax=null;
    	String JcbRoCode=null;
    	String response = null;
    	String[] messageSplit = null;
		String process = "DealerInfo";
		String reprocess = "RDealerInfo";
		String fileRef = null;
        
        try
        {
        	ErrorMessageHandler messageHandlerObj = new ErrorMessageHandler();
        	List<DealerInfoInputContract> inputContractList = new LinkedList<DealerInfoInputContract>();
        	
        	
        	//get the currentDate
        	Date currentDate = new Date();
        	Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
        	
        	//List all the messages scheduled to be reprocessed for the current time - Reprocess the same in the order of defined sequence of processes
        	Query query = session.createQuery("select a.messageId, a.messageString, a.fileName, a.process," +
        			" a.reprocessJobCode from FaultDetails a, JobDetails b where a.reprocessJobCode = b.reprocessJobCode and " +
        			" a.process = b.process and a.reprocessJobCode = 'RDealerInfo' and a.reprocessTimeStamp <= '"+currentTimestamp+"' " +
        					" group by a.messageString order by b.process, b.sequence, a.reprocessTimeStamp ");
        	
        	Iterator itr = query.list().iterator();
        	Object[] result = null;
        	
        	while(itr.hasNext())
        	{
        		result = (Object[]) itr.next();
        		
        		dealerCode=null;
            	dealerName=null;
            	addressLine1=null;
            	addressLine2=null;
            	city=null;
            	zipCode=null;
            	state=null;
            	zone=null;
            	country=null;
            	email=null;
            	contactNumber=null;
            	fax=null;
            	JcbRoCode=null;
        		
        		String messageString = result[1].toString();
        		messageSplit = messageString.split("\\|");
        		int paramSize =messageSplit.length;
        		
        		if(paramSize>0)
        			dealerCode = messageSplit[0];
        		if(paramSize>1)
        			dealerName = messageSplit[1];
        		if(paramSize>2)
        			addressLine1 = messageSplit[2];
        		if(paramSize>3)
        			addressLine2 = messageSplit[3];
        		if(paramSize>4)
        			city = messageSplit[4];
        		if(paramSize>5)
        			zipCode = messageSplit[5];
        		if(paramSize>6)
        			state = messageSplit[6];
        		if(paramSize>7)
        			zone = messageSplit[7];
        		if(paramSize>8)
        			country = messageSplit[8];
        		if(paramSize>9)
        			email = messageSplit[9];
        		if(paramSize>10)
        			contactNumber = messageSplit[10];
        		if(paramSize>11)
        			fax = messageSplit[11];
        		if(paramSize>12)
        			JcbRoCode = messageSplit[12];
        		
        		//Fill the input contract Object
        		DealerInfoInputContract inputContractObj = new DealerInfoInputContract();
        		inputContractObj.setDealerCode(dealerCode);
        		inputContractObj.setDealerName(dealerName);
        		inputContractObj.setAddressLine1(addressLine1);
        		inputContractObj.setAddressLine2(addressLine2);
        		inputContractObj.setCity(city);
        		inputContractObj.setZipCode(zipCode);
        		inputContractObj.setState(state);
        		inputContractObj.setZone(zone);
        		inputContractObj.setCountry(country);
        		inputContractObj.setEmail(email);
        		inputContractObj.setContactNumber(contactNumber);
        		inputContractObj.setFax(fax);
        		inputContractObj.setJcbRoCode(JcbRoCode);
        		
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
        		//DF20180208:KO369761 - Adding the reprocess msgs to the Kafka topic.
        		/*DealerInfoClient clientObj = new DealerInfoClient();
        		response = clientObj.invokeDealerInfoService(inputContractList.get(i));*/
        		DealerInfoQHandler queObj = new DealerInfoQHandler();
        		queObj.handleDealerInfoToKafkaQueue("DealerInfoQueue", inputContractList.get(i));
        		
        		
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
        	fLogger.fatal("EA ReProcessing: RDealerInfo: "+messgId+ " Fatal Exception :"+e);
		}
        
      //DF20180207:KO369761 - reprocessing the data which were stored in files.
      		try{
      			String reprocessFolderPath = "";
      			String messageString = null;

      			Properties prop = new Properties();
      			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
      			reprocessFolderPath = prop.getProperty("EA_reprocessingFolderPath");

      			String pattern = "DealerInfo"+"*.txt";
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
      				iLogger.info("RDealerInfo:: from files with file name:"+reprocessFile);
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
      						dealerCode = messageSplit[0];
      					if(paramSize>1)
      						dealerName = messageSplit[1];
      					if(paramSize>2)
      						addressLine1 = messageSplit[2];
      					if(paramSize>3)
      						addressLine2 = messageSplit[3];
      					if(paramSize>4)
      						city = messageSplit[4];
      					if(paramSize>5)
      						zipCode = messageSplit[5];
      					if(paramSize>6)
      						state = messageSplit[6];
      					if(paramSize>7)
      						zone = messageSplit[7];
      					if(paramSize>8)
      						country = messageSplit[8];
      					if(paramSize>9)
      						email = messageSplit[9];
      					if(paramSize>10)
      						contactNumber = messageSplit[10];
      					if(paramSize>11)
      						fax = messageSplit[11];
      					if(paramSize>12)
      						JcbRoCode = messageSplit[12];

      					//Fill the input contract Object
      					DealerInfoInputContract inputContractObj = new DealerInfoInputContract();
      					inputContractObj.setDealerCode(dealerCode);
      					inputContractObj.setDealerName(dealerName);
      					inputContractObj.setAddressLine1(addressLine1);
      					inputContractObj.setAddressLine2(addressLine2);
      					inputContractObj.setCity(city);
      					inputContractObj.setZipCode(zipCode);
      					inputContractObj.setState(state);
      					inputContractObj.setZone(zone);
      					inputContractObj.setCountry(country);
      					inputContractObj.setEmail(email);
      					inputContractObj.setContactNumber(contactNumber);
      					inputContractObj.setFax(fax);
      					inputContractObj.setJcbRoCode(JcbRoCode);
      					inputContractObj.setFileRef(fileRef);
      					inputContractObj.setMessageId("MSG"+fileRef+i+"|R");
      					inputContractObj.setProcess(process);
      					inputContractObj.setReprocessJobCode(reprocess);

      					DealerInfoQHandler queObj = new DealerInfoQHandler();
      					queObj.handleDealerInfoToKafkaQueue("DealerInfoQueue",inputContractObj);
      				}
      				bufferedReader.close();
      				reprocessFile.delete();
      			}

      		}catch(Exception e)
      		{
      			fLogger.fatal("EA ReProcessing: RDealerInfo: "+messgId+ " Fatal Exception :"+e);
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
