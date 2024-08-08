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

import remote.wise.EAintegration.EAclient.CustomerInfoClient;
import remote.wise.EAintegration.dataContract.CustomerInfoInputContract;
import remote.wise.businessentity.InterfaceLogDetails;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;

public class RCustomerInfo 
{
	public void reprocessCustomerInfo()
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
        
        try
        {
        	String customerCode=null;
        	String customerName=null;
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
        	String primaryDealerCode=null;
        	String response = null;
        	String fileRef = null;
        	ErrorMessageHandler messageHandlerObj = new ErrorMessageHandler();
        	
        	//get the currentDate
        	Date currentDate = new Date();
        	Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
        	List<CustomerInfoInputContract> inputContractList = new LinkedList<CustomerInfoInputContract>();
        	
        	//List all the messages scheduled to be reprocessed for the current time - Reprocess the same in the order of defined sequence of processes
        	//DF20180226:KO369761 - group by messageString added in the query because its giving multiple rows with same message.
        	Query query = session.createQuery("select a.messageId, a.messageString, a.fileName, a.process," +
        			" a.reprocessJobCode from FaultDetails a, JobDetails b where a.reprocessJobCode = b.reprocessJobCode and " +
        			" a.process = b.process and a.reprocessJobCode = 'RCustomerInfo' and a.reprocessTimeStamp <= '"+currentTimestamp+"' " +
        			" group by a.messageString order by b.process, b.sequence, a.reprocessTimeStamp ");
        	
        	Iterator itr = query.list().iterator();
			Object[] result = null;

			while(itr.hasNext()){
				result = (Object[]) itr.next();
				String messageString = result[1].toString();
				messgId=result[0].toString();
				customerCode = messageString.split("\\|")[0];
				response = new CustomerInfo().createOrUpdateCustomerInfo(customerCode,messageString,1);

				if(response.contains("SUCCESS")){
					messageHandlerObj.deleteErrorMessage(messgId);
					
					//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
					String uStatus=CommonUtil.updateInterfaceLogDetails(result[2].toString(), "sucessCount", 1);
					CommonUtil.updateInterfaceLogDetails(result[2].toString(), "failureCount", -1);
					iLogger.info("Status on updating data into interface log details table :"+uStatus);
				}
				else{
					if(response.equalsIgnoreCase("FAILURE"))
					{
						messageHandlerObj.handleErrorMessages_new(result[0].toString(), result[1].toString(), result[2].toString(), result[3].toString(), result[4].toString(),"Failure","0003","Service Completion");
					}
				}
			}

			//DF20180209:KO369761 - reprocessing the data which were stored in files.
			try{
				String reprocessFolderPath = "";
				String messageString = null;
				
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				reprocessFolderPath = prop.getProperty("EA_reprocessingFolderPath");

				String pattern = "CustomerInfo"+"*.txt";
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
					if(reprocessFile.getName().split("_").length>2)
						fileRef = reprocessFile.getName().split("_")[0]+reprocessFile.getName().split("_")[1];
					else
						fileRef = reprocessFile.getName().substring(0, reprocessFile.getName().lastIndexOf("."));
					fileReader = new FileReader(reprocessFile);
					bufferedReader = new BufferedReader(fileReader);

					int failureCount = 0;
					int dataCount = 0;
					while((messageString=bufferedReader.readLine())!=null){
						customerCode = messageString.split("\\|")[0];
						response = new CustomerInfo().createOrUpdateCustomerInfo(customerCode,messageString,1);
						messgId = "MSG"+fileRef+i;
						dataCount++;

						if(response.equalsIgnoreCase("FAILURE"))
						{
							failureCount++;
							messageHandlerObj.handleErrorMessages_new(messgId, messageString, fileRef, "CustomerInfo", "RCustomerInfo","Failure","0003","Service Completion");
						}
					}
					bufferedReader.close();
					reprocessFile.delete();

					//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
					Query EAlogQ = session.createQuery("from InterfaceLogDetails where filename ='"+ fileRef + "'");
					Iterator EAlogItr = EAlogQ.list().iterator();
					if(EAlogItr.hasNext()){
						InterfaceLogDetails interfacedata = (InterfaceLogDetails)EAlogItr.next();
						interfacedata.setTotalCount(interfacedata.getTotalCount()+dataCount);
						interfacedata.setSucessCount(interfacedata.getSucessCount()+dataCount-failureCount);
						interfacedata.setFailureCount(interfacedata.getFailureCount()+failureCount); 
						session.update(interfacedata);
					}
					else{
						InterfaceLogDetails interfacedata = new InterfaceLogDetails();
						interfacedata.setFilename(fileRef);
						interfacedata.setTotalCount(dataCount);
						interfacedata.setSuccessfullyputtoqueue(0);
						interfacedata.setSuccessfullServiceInvocation(dataCount-failureCount);
						interfacedata.setSucessCount(dataCount-failureCount);
						interfacedata.setFailureCount(failureCount);
						session.save(interfacedata);
						iLogger.info("Inserted into Interface_Log_Details table with the file name "
								+ interfacedata.getFilename());
						session.save(interfacedata);
					}
				}
			}catch(Exception e)
			{
				fLogger.fatal("EA ReProcessing: CustomerInfo: "+messgId+ " Fatal Exception :"+e);
			}
        	
        	//DF20180214: KO369761 - Below code commented because it's not going through the customer master table.
        	/*Iterator itr = query.list().iterator();
        	Object[] result = null;
        	
        	while(itr.hasNext())
        	{
        		result = (Object[]) itr.next();
        		
        		customerCode=null;
        		customerName=null;
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
            	primaryDealerCode=null;
        		
        		String messageString = result[1].toString();
        		String[] messageSplit = messageString.split("\\|");
        		int paramSize =messageSplit.length;
        		
        		if(paramSize>0)
        			customerCode = messageSplit[0];
        		if(paramSize>1)
        			customerName = messageSplit[1];
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
        			primaryDealerCode = messageSplit[12];
        		
        		//Fill the input contract Object
        		CustomerInfoInputContract inputContractObj = new CustomerInfoInputContract();
        		inputContractObj.setCustomerCode(customerCode);
        		inputContractObj.setCustomerName(customerName);
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
        		inputContractObj.setPrimaryDealerCode(primaryDealerCode);
        		
        		if(result[2]!=null)
        			inputContractObj.setFileRef(result[2].toString());
        		if(result[0]!=null)
        		{
        			inputContractObj.setMessageId(result[0].toString());
        			messgId=result[0].toString();
        		}
        		if(result[3]!=null)
        			inputContractObj.setProcess(result[3].toString());
        		if(result[4]!=null)
        			inputContractObj.setReprocessJobCode(result[4].toString());
        		
        		inputContractList.add(inputContractObj);
        		
        	}
        	
        	for(int i=0; i<inputContractList.size(); i++)
        	{
        		CustomerInfoClient clientObj = new CustomerInfoClient();
        		response = clientObj.invokeCustomerInfoService(inputContractList.get(i));
        		
        		
        		if(response.equalsIgnoreCase("FAILURE"))
        		{
        			messageHandlerObj.handleErrorMessages(result[0].toString(), result[1].toString(), result[2].toString(), result[3].toString(), result[4].toString());
        		}
        		
        		//DF20140715 - Rajani Nagaraju - Handle Robust Logging
        		if(response.split("-").length>1)
        		{
        			response = response.split("-")[0].trim();
        					
        		}
        		
        		if(response.equalsIgnoreCase("SUCCESS"))
        		{
        			messageHandlerObj.deleteErrorMessage(inputContractList.get(i).getMessageId());
        		}
        	}*/
        	
        	  /*if(! (session.isOpen() ))
              {
                          session = HibernateUtil.getSessionFactory().getCurrentSession();
                          session.getTransaction().begin();
              }*/

        }
        
        catch(Exception e)
		{
        	fLogger.fatal("EA ReProcessing: CustomerInfo: "+messgId+ " Fatal Exception :"+e);
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
