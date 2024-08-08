/*
 * ME100005771 : 20230315 : Dhiraj K : Password Decryption Issue
 * ME100008212 : 20230704 : Dhiraj K : Customer Master table not getting updated while customer creation is being done from the portal
 * JCB6501 : 20231206 : Dhiraj K : Customer Info processing
 */
package remote.wise.EAintegration.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.EAintegration.Qhandler.CustomerInfoQHandler;
import remote.wise.EAintegration.dataContract.CustomerInfoInputContract;
import remote.wise.businessentity.AccountContactMapping;
import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.businessentity.CustomerMasterEntity;
import remote.wise.businessentity.EALogDetailsEntity;
import remote.wise.businessentity.FaultDetails;
import remote.wise.businessentity.InterfaceLogDetails;
import remote.wise.businessentity.JobDetails;
import remote.wise.businessentity.TenancyEntity;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;

public class CustomerInfo 
{
	public CustomerInfo()
	{

	}

	public CustomerInfo(File processingFile, String archivedFolderPath, String process, String reprocessJobCode)
	{
		Logger fLogger = FatalLoggerClass.logger;
		try 
		{
			processCustomerInfo(processingFile, archivedFolderPath, process, reprocessJobCode);
		} 
		catch (Exception e) 
		{
			fLogger.fatal("Exception :"+processingFile+": "+e);
		}
	}


	public void handleCustomerInfo()
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
			Query query = session.createQuery("from JobDetails where jobCode='CustomerInfo' order by sequence");
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
				if(session.isOpen())
				{
					if(session.getTransaction().isActive())
					{
						session.getTransaction().commit();
					}
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
	public void processCustomerInfo(File processingFile, String archivedFolderPath, String process, String reprocessJobCode) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;

		String queuePath = "jms/queue/CustomerInfoQueue";

		BufferedReader br = null;
		File archivedFile = null;
		//DefectId:20140609 CustomerInfo Changes @Suprava sn
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
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
				if(session.getTransaction().isActive() && session.isDirty())
				{
					iLogger.info("Opening a new session");
					session = HibernateUtil.getSessionFactory().openSession();
				}
				session.getTransaction().begin();
			}


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


			br = new BufferedReader(new FileReader(processingFile));
			String line = br.readLine();

			//DF20150514 - Rajani Nagaraju - If \ is present as part of customer details, customer account not getting created as \ was taken as escape sequence
			//line=line.replaceAll("\\\\", "");
			//DF20150527 - Rajani Nagaraju - If ' is present as part of customer details, customer account not getting created as ' is taken as end of string
			//line=line.replaceAll("\'", "");
			
			//DF20190228:AB369654::To remove unwanted characters
			line = line.replaceAll("[^A-Za-z0-9+@,#:|]", " ");
				
			String customerCode=null;
			String customerCode1=null;
			int runningNumber =1;
			int packet =0;
			String preMobileNo = null;
			int accPacket = 0;
			int fileDataCount = 0;
			int failureCount = 0;
			String messageId = null;
			String messageString = null;
			String fileRef = null;

			while (line != null) 
			{
				packet =0;

				//2015-07-10: Reinitialize the variables as the mobile number for the customer accounts was having an incorrect reference - Rajani Nagaraj
				accPacket = 0;
				preMobileNo = null;

				//DF20190228:AB369654::To remove unwanted characters
				line = line.replaceAll("[^A-Za-z0-9+@,#:|]", " ");
				
				//DF20140811 - Rajani Nagaraju - To log the number of records received per each file into a table
				if(!(line.trim()!=null && line.trim().length()>0))
				{
					line = br.readLine();
					continue;
				} 

				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
					if(session.getTransaction().isActive() && session.isDirty())
					{
						iLogger.info("Opening a new session");
						session = HibernateUtil.getSessionFactory().openSession();
					}
					session.beginTransaction();
				}

				//AwakeID: JCB0327 - Customer record received with blank at the end was getting rejected since the split length will be 12 (trimming off the last empty charecter)
				//Adding -1 at the end will include empty character at the end
				String[] msgString = line.split("\\|",-1);
				messageString = line;
				messageId = "MSG"+processingFile.getName().substring(0, processingFile.getName().lastIndexOf("."))+runningNumber;
				fileRef = processingFile.getName().substring(0, processingFile.getName().lastIndexOf("."));
				//ME100005771.sn
				int pwdUpdate=0;
				String decryptedPass=null;
				String changeContact=null;
				String newMobileNumber=null;
				//ME100005771.en
				try{
					//CustomerInfoInputContract inputObject = new CustomerInfoInputContract();
					CustomerMasterEntity custMasterEntity= null;
					//DF20150817 - Rajani Nagaraju - Customer File not moving to archived folder if an error is thrown at one Customer record,
					//Identifier modification error on hibernate - Handling invalid records
					if(line !=null && (!line.isEmpty()) && msgString.length>0 && line.split("\\|",-1).length==13)
					{
						CustomerMasterEntity CustomerMasterObj = null;
						customerCode1=msgString[0];
						Query query = session.createQuery("from CustomerMasterEntity where customerCode='"+customerCode1+"'");
						Iterator itr = query.list().iterator();
						Object[] ResultSet1 = null;
						while(itr.hasNext())
						{
							packet=1;
							CustomerMasterObj = (CustomerMasterEntity)itr.next();
							CustomerMasterObj.setCustomerdetail(line);
							//DF20150817 - Rajani Nagaraju - Customer File not moving to archived folder is an error is thrown at one Customer record,
							//Identifier modification error on hibernate - Commenting the below line
							//CustomerMasterObj.setCustomerCode(customerCode1);
							runningNumber++;
							session.update(CustomerMasterObj);
							iLogger.info("EA Processing: CustomerInfo: Table Updated"+customerCode1+" Updated Successfully");
						}
						//JCB6501.sn
						AccountEntity accObj = null;
						String contactId = null;
						//JCB6501.en
						//DF20150514 - @suresh updating account and contact entites when updated cutomer master data is received
						if(packet == 1)
						{
							//AccountEntity accObj = null;//JCB6501.o
							Query accQuery = session.createQuery("from AccountEntity where accountCode ='"+customerCode1+"'");
							Iterator accItr = accQuery.list().iterator();
							while(accItr.hasNext())
							{
								accObj = (AccountEntity)accItr.next();
								preMobileNo = accObj.getMobile_no();
								if(line.split("\\|",-1).length > 10)
								{
									accPacket = 1;
									accObj.setMobile_no(msgString[10]);
									accObj.setAccount_name(msgString[1]);
									//DF20190312 :mani: account creation or updation tracebility
									Date currentDate = new Date();
									Timestamp currentTimeStamp = new Timestamp(currentDate.getTime());
									accObj.setUpdatedOn(currentTimeStamp);
									session.update(accObj);
								}

							}
							//JCB6501.sn
							/*AccountContactMapping  accContactMap = null;
							Query accContactQuery = session.createQuery("from AccountContactMapping where account_id ='"+accObj.getAccount_id()+"'");
							Iterator accContactItr = accContactQuery.list().iterator();
							while(accContactItr.hasNext()) {
								accContactMap = (AccountContactMapping)accContactItr.next();
								contactId = accContactMap.getContact_id().getContact_id();
							}*/
							//JCB6501.en
							//DF20190221 :MANI: updating the tenancy name when updated customer master data is received
							TenancyEntity tenancyentity=null;
							Query tenQuery = session.createQuery("from TenancyEntity where mappingCode ='"+customerCode1+"'");
							iLogger.info("EA Processing: CustomerInfo: TenancyEntity for mapping code "+customerCode1+" updating query "+tenQuery);
							Iterator tenItr = tenQuery.list().iterator();
							while(tenItr.hasNext())
							{
								tenancyentity=(TenancyEntity) tenItr.next();
								tenancyentity.setTenancy_name(msgString[1]+"-"+customerCode1);
								session.update(tenancyentity);
								iLogger.info("EA Processing: CustomerInfo: TenancyEntity for mapping code "+customerCode1+" updated to "+msgString[1]+"-"+customerCode1);
							}
						}
						/*if(accPacket == 1)
						{
							ContactEntity contactObj = null;
							Query conQuery = session.createQuery("from ContactEntity where primary_mobile_number='"+preMobileNo+"'");
							Iterator conItr = conQuery.list().iterator();
							while(conItr.hasNext())
							{
								contactObj = (ContactEntity)conItr.next();
								contactObj.setPrimary_mobile_number(msgString[10]);
								session.update(contactObj);
							}
						}*/
						//DefectId:20150806 @Suprava: As the SAP data received for customer mobile no. update, LL application should not update the details if the same mobile no. already exists in the system.
						if(accPacket == 1)
						{
							String newMobileNum = msgString[10];
							int present =0;
							Query contactQuery = session.createQuery("from ContactEntity where primary_mobile_number ='"+newMobileNum+"' and active_status=true");
							List list = contactQuery.list();
							Iterator iterator = list.iterator();
							if ((iterator.hasNext()) && (list.size() > 0)&& (list.get(0) != null))
							{
								while (iterator.hasNext()) 
								{
									present =1;
									ContactEntity contact = (ContactEntity) iterator.next();
									bLogger.error(" Mobile Number: "+newMobileNum+" already exists for Contact: " +contact.getContact_id());
								}
							}

							if(present==0){
								iLogger.info("Update contact details");//ME100005771.n
								ContactEntity contactObj = null;
								//Query conQuery = session.createQuery("from ContactEntity where primary_mobile_number='"+preMobileNo+"'");//ME100005771.o
								Query conQuery = session.createQuery("from ContactEntity where primary_mobile_number='"+preMobileNo+"' and active_status=true");//ME100005771.n
								//Query conQuery = session.createQuery("from ContactEntity where contact_id='"+contactId+"' active_status=true");//JCB6501.n
								Iterator conItr = conQuery.list().iterator();
								//while(conItr.hasNext())//ME100005771.o
								if(conItr.hasNext())//ME100005771.n
								{
									pwdUpdate=1;//ME100005771.n
									contactObj = (ContactEntity)conItr.next();
									contactObj.setPrimary_mobile_number(msgString[10]);
									//ME100005771.sn
									//Get decrypted password
									decryptedPass = new CommonUtil().getDecryptedPassword(contactObj.getContact_id());//ME100005771.n
									changeContact= contactObj.getContact_id();
									newMobileNumber=msgString[10];
									//ME100005771.en
									session.update(contactObj);
								}
							}
						}

						//DF20150514 - @suresh updating account and contact entites when updated cutomer master data is received
					}

					//DF20150817 - Rajani Nagaraju - Customer File not moving to archived folder is an error is thrown at one Customer record,
					//Identifier modification error on hibernate - Handling invalid records
					if(packet==0 && line.split("\\|",-1).length==13 ){
						iLogger.info("packe=0 Insert into CustomerMaster table"+packet);
						custMasterEntity= new CustomerMasterEntity();
						//End DefectId:20140915
						if(msgString.length>0)
						{
							custMasterEntity.setCustomerCode(msgString[0]);
							customerCode=msgString[0];
						}
						if(line !=null && (!line.isEmpty()))
						{
							custMasterEntity.setCustomerdetail(line);
							//DefectId:20140822 To log the number of records received per each file into a table
							runningNumber++;
							iLogger.info("runningNumber"+runningNumber);
						}
						iLogger.info("runningNumber after if condition:"+runningNumber);
						custMasterEntity.setProcessFlag(1);
						session.save("CustomerMasterEntity",custMasterEntity);
						iLogger.info("EA Processing: CustomerInfo: Table Uplooad"+customerCode+" Uploaded Successfully");
					}


					//DF20150817 - Rajani Nagaraju - Customer File not moving to archived folder is an error is thrown at one Customer record,
					//Identifier modification error on hibernate
					//If the record is received with invalid dataset, store the same into Customer Master table but in a unique way so that such
					//records as easily recognized for future action on the same
					if(line.split("\\|",-1).length < 13 || line.split("\\|",-1).length > 13)
					{
						custMasterEntity= new CustomerMasterEntity();
						custMasterEntity.setCustomerCode(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"_"+processingFile.getName());
						if(line !=null && (!line.isEmpty()))
						{
							custMasterEntity.setCustomerdetail(line);
							runningNumber++;

						}
						//DF20150817 - Rajani Nagaraju - Setting processFlag to 99 so as to easily identify the erroneous records
						custMasterEntity.setProcessFlag(99);
						session.save("CustomerMasterEntity",custMasterEntity);
					}

					/*if(msgString.length>1)
			      		inputObject.setCustomerName(msgString[1]);

			      	if(msgString.length>2)
			      		inputObject.setAddressLine1(msgString[2]);

			      	if(msgString.length>3)
			      		inputObject.setAddressLine2(msgString[3]);

			      	if(msgString.length>4)
			      		inputObject.setCity(msgString[4]);

			      	if(msgString.length>5)
			      		inputObject.setZipCode(msgString[5]);

			      	if(msgString.length>6)
			      		inputObject.setState(msgString[6]);

			      	if(msgString.length>7)
			      		inputObject.setZone(msgString[7]);

			      	if(msgString.length>8)
			      		inputObject.setCountry(msgString[8]);

			      	if(msgString.length>9)
			      		inputObject.setEmail(msgString[9]);

			      	if(msgString.length>10)
			      		inputObject.setContactNumber(msgString[10]);

			      	if(msgString.length>11)
			      		inputObject.setFax(msgString[11]);

			      	if(msgString.length>12)
			      		inputObject.setPrimaryDealerCode(msgString[12]);

			      	inputObject.setFileRef(processingFile.getName().substring(0, processingFile.getName().lastIndexOf(".")));
			      	inputObject.setProcess(process);
			      	inputObject.setReprocessJobCode(reprocessJobCode);

			      	//generate MessageId
			      	String messageId = "MSG"+processingFile.getName().substring(0, processingFile.getName().lastIndexOf("."))+runningNumber;
			      	inputObject.setMessageId(messageId);

			      	//Place the object into AssetGroupQueue
			      	infoLogger.info("Q Object Details - Customer Info Q");
			    	infoLogger.info("--------------------------");
			    	infoLogger.info("Customer Code:"+inputObject.getCustomerCode()+",  "+"Customer Name:"+inputObject.getCustomerName() + ",  "+"zipCode:"+inputObject.getZipCode() +
							",  "+"addressLine1:"+inputObject.getAddressLine1()+",  "+"addressLine2:"+inputObject.getAddressLine2()+",  "+"city:"+inputObject.getCity()+",  "+"state"+inputObject.getState() +
							",  "+"zone:"+inputObject.getZone()+",  "+"country:"+inputObject.getCountry()+",  "+"email:"+inputObject.getEmail()+",  "+"contactNumber:"+inputObject.getContactNumber() +
							",  "+"fax:"+inputObject.getFax()+",  "+"Primary Dealer Code:"+inputObject.getPrimaryDealerCode() +
							",  "+"Message Id:"+inputObject.getMessageId()+",  "+"File Reference:"+inputObject.getFileRef()+
							",  "+"Process:"+inputObject.getProcess()+",  "+"Reprocess JobCode:"+inputObject.getReprocessJobCode());

			    	CustomerInfoQHandler queueObj = new CustomerInfoQHandler();
			      	queueObj.handleCustomerInfo(queuePath,inputObject);

			        line = br.readLine();
			        runningNumber++;

					 */	
					if(session.isOpen())
					{
						if(session.getTransaction().isActive())
						{
							// infoLogger.info("EA Processing: CustomerInfo: Table Uplooad"+customerCode+"Transaction Active");	
							session.getTransaction().commit();
						}
					}
					//JCB6501.sn
					//Now update encrypted password if required.
					if(pwdUpdate==1) {
						//Now update encrypted password
						iLogger.info("Update contact Encrypted pwd details for " + changeContact + " with new Mobile no: " +  newMobileNumber);
						String passwordUpdateQuery="UPDATE contact SET password=AES_ENCRYPT('"+ decryptedPass +"', '"+newMobileNumber+"')"
								+ " where contact_id ='"+changeContact+"'";
						String result=new CommonUtil().insertData(passwordUpdateQuery);
						if(result.equalsIgnoreCase("FAILURE")) {
							fLogger.fatal("Error occured while updating password for " + changeContact + " with new Mobile no: " +  newMobileNumber);
						}
					}
					//JCB6501.en
					fileDataCount++;

					//line = br.readLine();

				}   
				catch(Exception ex)
				{
					failureCount++;
					//DF20150817 - Rajani Nagaraju - Customer File not moving to archived folder is an error is thrown at one Customer record,
					//Identifier modification error on hibernate
					if(session.isOpen() && session.getTransaction().isActive())
						session.getTransaction().rollback();

					iLogger.info("EA Processing: CustomerInfo: Table Uplooad"+customerCode+" Upload Failed!!!"+ex.getMessage());
					fLogger.fatal("EA Processing: CustomerInfo: Table Uplooad"+customerCode+" Upload Failed!!!"+ex.getMessage());
					
					//DF20180208:KO369761 - Putting into fault details table incase of any exception.
					ErrorMessageHandler errorHandler = new ErrorMessageHandler();
					errorHandler.handleErrorMessages_new(messageId, messageString, fileRef, process, reprocessJobCode, ex.getMessage(), "0002", "Service Invokation");
					// line = br.readLine();
					pwdUpdate=0;//ME100005771.en
				}

				finally
				{
					line = br.readLine();

					if(session.isOpen())
					{
						session.flush();
						session.close();
					}


				}
				//JCB6501.so
				/*//ME100005771.sn
				//Now update encrypted password if required.
				if(pwdUpdate==1) {
					//Now update encrypted password
					iLogger.info("Update contact Encrypted pwd details for " + changeContact + " with new Mobile no: " +  newMobileNumber);
					String passwordUpdateQuery="UPDATE contact SET password=AES_ENCRYPT('"+ decryptedPass +"', '"+newMobileNumber+"')"
							+ " where contact_id ='"+changeContact+"'";
					String result=new CommonUtil().insertData(passwordUpdateQuery);
					if(result.equalsIgnoreCase("FAILURE")) {
						fLogger.fatal("Error occured while updating password for " + changeContact + " with new Mobile no: " +  newMobileNumber);
					}
				}
				//ME100005771.en
				*/
				//JCB6501.eo
			}
			//DefectId:20140609 en

			//DefectId:20140822 To log the number of records received per each file into a table
			iLogger.info("runningNumber Value after update to customermaster table:"+runningNumber);
			if(runningNumber>1)  {

				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
					if(session.getTransaction().isActive() && session.isDirty())
					{
						iLogger.info("Opening a new session");
						session = HibernateUtil.getSessionFactory().openSession();
					}
					session.getTransaction().begin();
				}
				try
				{
					EALogDetailsEntity eaLogDetails= new EALogDetailsEntity();
					eaLogDetails.setFileName(processingFile.getName().substring(0, processingFile.getName().lastIndexOf(".")));
					eaLogDetails.setProcess("CustomerInfo");
					eaLogDetails.setProcessedTimestamp(new Timestamp(new Date().getTime()));
					eaLogDetails.setNoOfRecords(runningNumber-1);
					session.save(eaLogDetails);
					
					//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
					InterfaceLogDetails interfacedata = new InterfaceLogDetails();
					interfacedata.setFilename(processingFile.getName().substring(0, processingFile.getName().lastIndexOf(".")));
					interfacedata.setTotalCount(fileDataCount);
					interfacedata.setSuccessfullyputtoqueue(0);
					interfacedata.setSuccessfullServiceInvocation(fileDataCount-failureCount);
					interfacedata.setSucessCount(fileDataCount-failureCount);
					interfacedata.setFailureCount(failureCount);
					session.save(interfacedata);
					iLogger.info("Inserted into Interface_Log_Details table with the file name "
							+ interfacedata.getFilename());

				}

				catch(Exception e)
				{
					fLogger.fatal("Exception :"+e);
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

			archivedFile = new File(archivedFolderPath,processingFile.getName());

			if(archivedFile.exists())
				archivedFile.delete();
			boolean moveStatus = processingFile.renameTo(archivedFile);

			if(moveStatus)
				iLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");

		}

		catch(IOException e)
		{
			fLogger.fatal("Exception :"+e);
		}

		catch(Exception e)
		{
			fLogger.fatal("EA Processing: CustomerInfo: Table Uplooad File Processing Failed : Exception :"+e);
		}

		finally
		{
			if(br!=null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("Exception :"+e);
				}	 


			if(session.isOpen())
			{
				session.flush();
				session.close();
			}


		}

		//move the file to archived folder
		/*archivedFile = new File(archivedFolderPath,processingFile.getName());

		if(archivedFile.exists())
			archivedFile.delete();
		boolean moveStatus = processingFile.renameTo(archivedFile);

		if(moveStatus)
			infoLogger.info("File "+processingFile.getName()+" is moved successfully from processing to archived folder");*/
	}
	
	//@AJ286551: Added to process CustomerInfo to customer_master table from UI
	@SuppressWarnings({ "unchecked", "unused" })
	public String createOrUpdateCustomerInfo(String customerCode, String customerInfo, int processFlag) {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;

		String result = "FAILURE";
		
		String primaryDealerCode = null;
		String nonDbmsDealers = null;
		//DF20170809: SU334449 - Reading from the properties file for NON-DBMS Dealer ECC codes
		Properties reader = new Properties();
		//String path = "/user/JCBLiveLink/Non-DbmsUpdateFile/dealerList/dealerList.properties";//ME100008212.o
		String path = "/user1/JCBLiveLink/Non-DbmsUpdateFile/dealerList/dealerList.properties";//ME100008212.n
		try {
			reader.load(new FileInputStream(path));
			nonDbmsDealers = reader.getProperty("dealers");
		} catch (Exception e) {
			fLogger.info("Not able to find file and read");
		}
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (session.getTransaction().isActive() && session.isDirty()) {
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();
		//JCB6501.sn
		int pwdUpdate=0;
		String decryptedPass=null;
		String changeContact=null;
		String newMobileNumber=null;
		String customerCode1=null;
		int packet =0;
		String preMobileNo = null;
		int accPacket = 0;
		int failureCount = 0;
		String messageId = null;
		String messageString = null;
		String[] msgString = customerInfo.split("\\|",-1);
		messageString = customerInfo;
		//JCB6501.en
		
		try {
			if (!(session.isOpen())) {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				if (session.getTransaction().isActive() && session.isDirty()) {
					iLogger.info("Opening a new session");
					session = HibernateUtil.getSessionFactory().openSession();
				}
				session.getTransaction().begin();
			}
			
			//JCB6501.so
			//DF20171208:KO369761 MobileNo Check added, If mobile number already exists, it will not store in customermaster table.
			/*try{
				String mobileNum = customerInfo.split("\\|")[10];
				Query contactQ = session.createQuery(" from ContactEntity where primary_mobile_number = '"+mobileNum+"'");
				Iterator contactItr = contactQ.list().iterator();
				if(contactItr.hasNext()){
					result =  "FAILURE - Mobile number is already registered with another customer";
					bLogger.error("CustomerInfoService:WebService:"+customerCode+":Mobile number:"+mobileNum+" is already registered with another customer");
					return result;
				}
			}catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("CustomerInfoService:WebService:CustomerCode:"+customerCode+" Exception Caught :"+e.getMessage());
			}

			if (customerInfo != null && !(customerInfo.isEmpty())) {
				Query query = session.createQuery("from CustomerMasterEntity where customerCode='"
						+ customerCode + "'");
				ArrayList<CustomerMasterEntity> customerInfoList = new ArrayList<CustomerMasterEntity>();
				customerInfoList = (ArrayList<CustomerMasterEntity>) query.list();

				if (customerInfoList.size() > 0) {
					customerInfoList.get(0).setCustomerdetail(customerInfo);
					customerInfoList.get(0).setProcessFlag(processFlag);
					//DF20170615 - SU334449 - Adding dealerCode in dealerECC_Code column of customer_master table
					String[] customerDetails = customerInfo.split("\\|");
					if(customerDetails.length>12)
						primaryDealerCode=customerDetails[12];
					if((!primaryDealerCode.isEmpty())&& nonDbmsDealers.contains(primaryDealerCode)){
						customerInfoList.get(0).setDealerCode(primaryDealerCode);
					}
					session.saveOrUpdate(customerInfoList.get(0));
					iLogger.info("CustomerInfo: Table Updated: " + customerCode
							+ " Updated Successfully");
					result = "SUCCESS";
				} else {
					CustomerMasterEntity CustomerMasterObj = new CustomerMasterEntity();
					CustomerMasterObj.setCustomerCode(customerCode);
					CustomerMasterObj.setCustomerdetail(customerInfo);
					CustomerMasterObj.setProcessFlag(processFlag);
					//DF20170615 - SU334449 - Adding dealerCode in dealerECC_Code column of customer_master table
					String[] customerDetails = customerInfo.split("\\|");
					if(customerDetails.length>12)
						primaryDealerCode=customerDetails[12];
					if((!primaryDealerCode.isEmpty()) && nonDbmsDealers!=null &&nonDbmsDealers.contains(primaryDealerCode)){
						CustomerMasterObj.setDealerCode(primaryDealerCode);
					}
					session.save(CustomerMasterObj);
					iLogger.info("CustomerInfo: Table Updated: " + customerCode
							+ " Added Successfully");
					result = "SUCCESS";
				}
			}*/
			//JCB6501.eo
			//JCB6501.sn
			CustomerMasterEntity custMasterEntity= null;
			String contactId=null;
			//ME100010461-20240322-Sai Divya-for updating dealerCode in dealerECC_Code column of customer_master table
			if (customerInfo != null && !(customerInfo.isEmpty())) {  
				Query query1 = session.createQuery("from CustomerMasterEntity where customerCode='"
						+ customerCode + "'");
				ArrayList<CustomerMasterEntity> customerInfoList = new ArrayList<CustomerMasterEntity>();
				customerInfoList = (ArrayList<CustomerMasterEntity>) query1.list();
				
				if (customerInfoList.size() > 0) {
					
					//Adding dealerCode in dealerECC_Code column of customer_master table
					String[] customerDetails = customerInfo.split("\\|");
					if(customerDetails.length>12)
						primaryDealerCode=customerDetails[12];
					if(primaryDealerCode!=null && !primaryDealerCode.isEmpty()){
						customerInfoList.get(customerInfoList.size()-1).setDealerCode(primaryDealerCode);
					}
					session.saveOrUpdate(customerInfoList.get(0));
					iLogger.info("CustomerInfo: Table Updated: " + customerCode
							+ " Updated Successfully");
					result = "SUCCESS";
				} 
				}
			
			//Identifier modification error on hibernate - Handling invalid records
			if(customerInfo !=null && (!customerInfo.isEmpty()) && msgString.length>0 && customerInfo.split("\\|",-1).length==13){
				CustomerMasterEntity CustomerMasterObj = null;
				customerCode1=msgString[0];
				Query query = session.createQuery("from CustomerMasterEntity where customerCode='"+customerCode1+"'");
				Iterator itr = query.list().iterator();
				Object[] ResultSet1 = null;
				while(itr.hasNext())
				{
					packet=1;
					CustomerMasterObj = (CustomerMasterEntity)itr.next();
					CustomerMasterObj.setCustomerdetail(customerInfo);
					//runningNumber++;
					session.update(CustomerMasterObj);
					iLogger.info("EA Processing: CustomerInfo: Table Updated"+customerCode1+" Updated Successfully");
				}
				//String prevMobileNo=null;
				if(packet == 1){
					AccountEntity accObj = null;
					Query accQuery = session.createQuery("from AccountEntity where accountCode ='"+customerCode1+"'");
					Iterator accItr = accQuery.list().iterator();
					while(accItr.hasNext()){
						accObj = (AccountEntity)accItr.next();
						preMobileNo = accObj.getMobile_no();
						if(customerInfo.split("\\|",-1).length > 10)
						{
							accPacket = 1;
							accObj.setMobile_no(msgString[10]);
							accObj.setAccount_name(msgString[1]);
							Date currentDate = new Date();
							Timestamp currentTimeStamp = new Timestamp(currentDate.getTime());
							accObj.setUpdatedOn(currentTimeStamp);
							session.update(accObj);
						}
					}
					
					//
					/*AccountContactMapping  accContactMap = null;
					Query accContactQuery = session.createQuery("from AccountContactMapping where account_id ='"+accObj.getAccount_id()+"'");
					Iterator accContactItr = accContactQuery.list().iterator();
					while(accContactItr.hasNext()) {
						accContactMap = (AccountContactMapping)accContactItr.next();
						contactId = accContactMap.getContact_id().getContact_id();
					}*/
					
					//updating the tenancy name when updated customer master data is received
					TenancyEntity tenancyentity=null;
					Query tenQuery = session.createQuery("from TenancyEntity where mappingCode ='"+customerCode1+"'");
					iLogger.info("EA Processing: CustomerInfo: TenancyEntity for mapping code "+customerCode1+" updating query "+tenQuery);
					Iterator tenItr = tenQuery.list().iterator();
					while(tenItr.hasNext())
					{
						tenancyentity=(TenancyEntity) tenItr.next();
						tenancyentity.setTenancy_name(msgString[1]+"-"+customerCode1);
						session.update(tenancyentity);
						iLogger.info("EA Processing: CustomerInfo: TenancyEntity for mapping code "+customerCode1+" updated to "+msgString[1]+"-"+customerCode1);
					}
				}
				//As the SAP data received for customer mobile no. update, LL application should not update the details if the same mobile no. already exists in the system.
				if(accPacket == 1)
				{
					String newMobileNum = msgString[10];
					int present =0;
					Query contactQuery = session.createQuery("from ContactEntity where primary_mobile_number ='"+newMobileNum+"' and active_status=true");
					List list = contactQuery.list();
					Iterator iterator = list.iterator();
					
//					try {
//					if ((list.get(0) != null)&&(iterator.hasNext()) && (list.size() > 0)){
//						iLogger.info("-------------------+hello");
//						while (iterator.hasNext()){
//							present =1;
//							ContactEntity contact = (ContactEntity) iterator.next();
//							bLogger.error(" Mobile Number: "+newMobileNum+" already exists for Contact: " +contact.getContact_id());
//						}
//					}
//					
//					}
//					catch(ArrayIndexOutOfBoundsException e){
//						e.printStackTrace();
//					}
					try{
						if(list !=null && !list.isEmpty()) 
						{ 
							while(iterator.hasNext()) 
							{ 
								present =1;
								ContactEntity contact=(ContactEntity) iterator.next(); 
								bLogger.error(" Mobile Number: "+ newMobileNum +" already exists for Contact: "+ contact.getContact_id()); 
							}
						}
						else
						{ 
							iLogger.info("No existing contacts found for Mobile Number: "+ newMobileNum);
						}
						}
						catch(ArrayIndexOutOfBoundsException e) {
							// Handle the exception
							iLogger.info("Array index is out of bounds.");
							e.printStackTrace();
						}
					
					if(present==0){
						iLogger.info("Update contact details");
						ContactEntity contactObj = null;
						//Query conQuery = session.createQuery("from ContactEntity where contact_id='"+contactId+"' and active_status=true");
						Query conQuery = session.createQuery("from ContactEntity where primary_mobile_number='"+preMobileNo+"' and active_status=true");
						Iterator conItr = conQuery.list().iterator();
						if(conItr.hasNext()){
							pwdUpdate=1;
							contactObj = (ContactEntity)conItr.next();
							contactObj.setPrimary_mobile_number(msgString[10]);
							//Get decrypted password
							decryptedPass = new CommonUtil().getDecryptedPassword(contactObj.getContact_id());//ME100005771.n
							changeContact= contactObj.getContact_id();
							newMobileNumber=msgString[10];
							session.update(contactObj);
						}
					}
				}
			}
			
			if(packet==0 && customerInfo.split("\\|",-1).length==13 ){
				iLogger.info("packet=0: Insert into CustomerMaster table"+packet);
				custMasterEntity= new CustomerMasterEntity();

				if(msgString.length>0){
					custMasterEntity.setCustomerCode(msgString[0]);
					customerCode=msgString[0];
				}
				if(customerInfo !=null && (!customerInfo.isEmpty())){
					custMasterEntity.setCustomerdetail(customerInfo);
				}
				custMasterEntity.setProcessFlag(1);
				String[] customerDetails = customerInfo.split("\\|");//ME100010461.sn
				if(customerDetails.length>12)
					primaryDealerCode=customerDetails[12];
				if((!primaryDealerCode.isEmpty())){
					custMasterEntity.setDealerCode(primaryDealerCode);
					iLogger.info("CustomerInfo: Table Updated: " + primaryDealerCode
							+ " Updated Successfully");
					result = "SUCCESS";
					
				}//ME100010461.en
				session.save("CustomerMasterEntity",custMasterEntity);
				iLogger.info("EA Processing: CustomerInfo: Table Upload : "+customerCode+" Uploaded Successfully");
			}

			if(session.isOpen()){
				if(session.getTransaction().isActive()){
					session.getTransaction().commit();
				}
			}
			
			//Now update encrypted password if required.
			if(pwdUpdate==1) {
				//Now update encrypted password
				iLogger.info("Update contact Encrypted pwd details for " + changeContact + " with new Mobile no: " +  newMobileNumber);
				String passwordUpdateQuery="UPDATE contact SET password=AES_ENCRYPT('"+ decryptedPass +"', '"+newMobileNumber+"')"
						+ " where contact_id ='"+changeContact+"'";
				result=new CommonUtil().insertData(passwordUpdateQuery);
				if(result.equalsIgnoreCase("FAILURE")) {
					fLogger.fatal("Error occured while updating password for " + changeContact + " with new Mobile no: " +  newMobileNumber);
				}
			}
			//JCB6501.en
			if (session.isOpen()) {
				if (session.getTransaction().isActive()) {
					session.getTransaction().commit();
				}
			}

		} catch (Exception ex) {
			if (session.isOpen() && session.getTransaction().isActive())
				session.getTransaction().rollback();
			result = "FAILURE";
			ex.printStackTrace();
			iLogger.info("CustomerInfo: Table Uplooad" + customerCode
					+ " Upload Failed!!!" + ex.getMessage());
			fLogger.fatal("CustomerInfo: Table Uplooad" + customerCode
					+ " Upload Failed!!!" + ex.getMessage());
		} finally {
			if (session.isOpen()) {
				session.flush();
				session.close();
			}
		}
		return result;
	}
}


