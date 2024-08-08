//20220801-SMTPNewHost changes : Mahesh Sahu
package remote.wise.AutoReportScheduler;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
//CR337 : 20220721 : Dhiraj K : Property file read.
import remote.wise.util.CommonUtil;

public class SendEmail 
{
	public String sendMail(String runId, int reportId, String autoReportFileLocation, String fileName, String emailToList, String reportName,String dateFilter, 
			String value1, String value2,String subscriptionType, String accountType, String accMappingCode)
	{
		String emailSentStatus="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		
		String inputfilePath=autoReportFileLocation+"/input/"+fileName;
		String outputFilePath=autoReportFileLocation+"/archived/"+fileName;
		String errorFilePath=autoReportFileLocation+"/failure/"+fileName;
		
		try
		{
			//-------------------------- STEP1: Validate File Existence
			if(fileName==null)
			{
				throw new Exception("AutoReports:"+subscriptionType+"Report:"+runId+":sendMail:reportId:"+reportId+":Input file is null:"+fileName);
			}
			
			File file =new File(inputfilePath);
			
			if(!file.exists())
			{
				throw new Exception("AutoReports:"+subscriptionType+"Report:"+runId+":sendMail:reportId:"+reportId+":Input file doesn't exists:"+inputfilePath);
			}
			
			//Set the Email Subject
			String emailSubject = dateFilter+"ly Report:"+reportName+"-"+dateFilter+":"+value1+" "+value2;
			String emailBody="Dear User,;;PFA "+reportName+" Report for the "+dateFilter+" "+value1+" of "+value2+".;;To Unsubscribe, Please login to" +
					" \"www.jcblivelink.in\" and Unselect the same under Preferences -> Report Tab.;;Thanks,;;In event of any queries regarding this email," +
					"please contact Customer care at 1800-2000-522.";
			String attachmentFile=null;
			int isZipped=0;
			
			//--------------------------- STEP2: Check the size of the file
			double byteValue = file.length();
			double kilobytes = (byteValue / 1024);
			double megabytes = (kilobytes / 1024);
						
			//--------------------------- STEP3: If the size of the file > 5MB, zip the file
			String zipFileName=null;
			if(megabytes > 5)
			{
				zipFileName = autoReportFileLocation+"/input/"+fileName.replaceAll(".pdf", "").concat(".zip");
				FileOutputStream fos=null;
				ZipOutputStream zos=null;
				
				try
				{
					File zfile =new File(zipFileName);
					fos = new FileOutputStream(zfile);
		            zos = new ZipOutputStream(fos);
		 
		            zos.putNextEntry(new ZipEntry(file.getName()));
		 
		            byte[] bytes = Files.readAllBytes(Paths.get(inputfilePath));
		            zos.write(bytes, 0, bytes.length);
		            isZipped=1;
				}
				catch(Exception e)
				{
					throw new Exception("AutoReports:"+subscriptionType+"Report:"+runId+":sendMail:reportId:"+reportId+":" +
							"Exception in zipping the input file:"+e.getMessage()+";File size in MB:"+megabytes);
					
				}
				finally
				{
					if(zos!=null)
					{
						zos.closeEntry();
			            zos.close();
					}
					
					if(fos!=null)
						fos.close();
				}
				
				
				if(zipFileName!=null)
				{
					File zfile1 =new File(zipFileName);
					
					if(!zfile1.exists())
					{
						throw new Exception("AutoReports:"+subscriptionType+"Report:"+runId+":sendMail:reportId:"+reportId+":Zipped file doesn't exists:"+zipFileName);
					}
					
					double zbyteValue = zfile1.length();
					double zkilobytes = (zbyteValue / 1024);
					double zmegabytes = (zkilobytes / 1024);
					
					if(zmegabytes > 5)
					{
						emailBody = "Dear User,;;"+reportName+" Report for the "+dateFilter+" "+value1+" of "+value2+" cannot be sent via email " +
								" as the size of the file is too huge:"+zmegabytes+" MB. To download the same,please login to" +
								" \"www.jcblivelink.in\" and run the report under Report Tab.;;Thanks,;;In event of any queries regarding this email," +
								"please contact Customer care at 1800-2000-522.";
					}
					else
					{
						attachmentFile=zipFileName;
						outputFilePath=autoReportFileLocation+"/archived/"+fileName.replaceAll(".pdf", "").concat(".zip");
					}
				}
			}
			else
			{
				attachmentFile =inputfilePath;
			}
			
			String status = sendEmailWithAttachment(runId, attachmentFile, outputFilePath, emailToList, emailSubject, emailBody, 
					subscriptionType, reportId,accMappingCode);
			
			if(status==null || status.equalsIgnoreCase("FAILURE"))
			{
				//If there is any exception in sending email, then move the files from input to failure folder
				 Path temp = Files.move(Paths.get(inputfilePath),Paths.get(errorFilePath));
				 if(temp==null)
				 {
					 fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendMail:reportId:"+reportId+":Failed to move file from: "+
							 inputfilePath+" to: "+errorFilePath);
				 }
				 //if(attachmentFile!=null && attachmentFile.contains(".zip")) //Move the Zip file to failure folder
				 if(isZipped==1)
				 {
					 String inputZipFile = inputfilePath.replaceAll(".pdf", "").concat(".zip");
					 String errorZipFile= errorFilePath.replaceAll(".pdf", "").concat(".zip");
					 Path temp1 = Files.move(Paths.get(inputZipFile),Paths.get(errorZipFile));
					 if(temp1==null)
					 {
						 fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendMail:reportId:"+reportId+":Failed to move file from: "+
								 inputZipFile+" to: "+errorZipFile);
					 }
				 }
				
				throw new Exception("Exception in sending email with attachment");
			}
			
			//--------------------------------If the mail is sent successfully, then move the files from input to archive folder
			Path temp = Files.move(Paths.get(inputfilePath),Paths.get(outputFilePath));
			if(temp==null)
			{
				fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendMail:reportId:"+reportId+":Failed to move file from: "+
						 inputfilePath+" to: "+outputFilePath);
			}
			if(attachmentFile!=null && attachmentFile.contains(".zip")) //Move the Zip file to failure folder
			{
				 String inputZipFile = inputfilePath.replaceAll(".pdf", "").concat(".zip");
				 String outputZipFile= outputFilePath.replaceAll(".pdf", "").concat(".zip");
				 Path temp1 = Files.move(Paths.get(inputZipFile),Paths.get(outputZipFile));
				 if(temp1==null)
				 {
					 fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendMail:reportId:"+reportId+":Failed to move file from: "+
							 inputZipFile+" to: "+outputZipFile);
				 }
			}
			
			//------------------------------- Persist the details for Audit Trial
			new AutoReportAuditTrial().persistToAuditTrial(runId, reportId, accMappingCode, subscriptionType, value1, value2, 
					accountType, emailToList, fileName);
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendMail:reportId:"+reportId+":Exception:"+e.getMessage()+";" +
					"Persisting Details to Fault Details table");
			emailSentStatus="FAILURE";
			
			//Invoke the Exception for storing the data into fault details table
			new AutoReportAuditTrial().persistToFaultDetails(runId,reportId, accMappingCode, subscriptionType, value1, value2, 
					e.getMessage(), accountType, emailToList, errorFilePath);
		}
		
		return emailSentStatus;
	}
	
	//***********************************************************************************************************************************
	
	public String sendEmailWithAttachment(String runId,String inputfilePath, String outputFilePath, 
			String toList, String emailSubject, String emailBody, String subscriptionType,int reportId, String accMappingCode)
	{
		String emailStatus="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		//DF20200109 - Rajani Nagaraju - removing bcc list as per the request from Gunjan
		//String bccList ="Amitav.SARKAR@jcb.com,Gunjan.CHOPRA@jcb.com";
		
		//CR337.sn
		String smtpHost = null;
		String smtpPort = null;
		String smtpHostAuthUser = null;
		String smtpHostAuthPw = null;
		Properties prop = null;
		try{
			prop = CommonUtil.getDepEnvProperties();
//			smtpHost = prop.getProperty("SMTPHostIP");  //20220801-SMTPNewHost.o
//			smtpPort = prop.getProperty("SMTPHostPORT");	//20220801-SMTPNewHost.o
			//20220801-SMTPNewHost.sn
			smtpHost = prop.getProperty("SMTPEmailHostIP");  
			smtpPort = prop.getProperty("SMTPEmailHostPORT");	
			smtpHostAuthUser = prop.getProperty("SMTPEmailAuthUser");  
			smtpHostAuthPw = prop.getProperty("SMTPEmailAuthPw");	
			//20220801-SMTPNewHost.en
		}catch(Exception e){
			fLogger.fatal("SendEmail : sendEmailWithAttachment : " +
					"Exception in getting SMTP Host server detail from properties file: " +e.getMessage());
		}
		//CR337.en
		
		try
		{
			/*if(inputfilePath==null || outputFilePath==null)
			{
				throw new Exception("AutoReports:"+subscriptionType+"Report:"+runId+":sendEmailWithAttachment:reportId:"+reportId+":accMappingCode:"+accMappingCode+"" +
						":Input file is null:"+inputfilePath);
			}*/
			emailBody =  emailBody.replaceAll(";", "\n");
			final String username = smtpHostAuthUser;	//20220801-SMTPNewHost.n
			final String password = smtpHostAuthPw;	//20220801-SMTPNewHost.n
			
			Properties props = new Properties();
			props.setProperty("mail.transport.protocol", "smtp");
			//20220705: Dhiraj K : Changes for AWS ip and port changes
			//props.put("mail.smtp.host", "10.179.12.13");
			props.put("mail.smtp.host", smtpHost);
//			props.put("mail.smtp.auth", "false");	//20220801-SMTPNewHost.o
			props.put("mail.smtp.auth", "true");	//20220801-SMTPNewHost.n
			//props.put("mail.smtp.port","25");
			props.put("mail.smtp.port",smtpPort);
			props.put("mail.debug","false");
			
//			Session session = Session.getInstance(props);	//20220801-SMTPNewHost.o
			
			//20220801-SMTPNewHost.sn
			Session session = Session.getInstance(props, new javax.mail.Authenticator() {

				protected PasswordAuthentication getPasswordAuthentication() {
					

					return new PasswordAuthentication(username,password);

				}

			});
			session.setDebug(true);	
			//20220801-SMTPNewHost.en
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("livelinkindia@jcblivelink.in"));
		    InternetAddress[] emailToList = getAddressList(toList);
		    //DF20200109 - Rajani Nagaraju - removing bcc list as per the request from Gunjan
		    //InternetAddress[] emailbccList = getAddressList(bccList);
		    
		    message.addRecipients(Message.RecipientType.TO, emailToList);
		    //message.addRecipients(Message.RecipientType.BCC, emailbccList);
		    message.setSubject(emailSubject);
		    
		    BodyPart textMessage = new MimeBodyPart();
		    textMessage.setText(emailBody);
	        
		    BodyPart attachment = new MimeBodyPart();
		    if(inputfilePath!=null)
		    {
		    	DataSource source = new FileDataSource(inputfilePath);
		    	attachment.setDataHandler(new DataHandler(source));
		    	attachment.setFileName(source.getName());  
		    }
		    
	        Multipart multipart = new MimeMultipart();  
	        multipart.addBodyPart(textMessage);
	        if(inputfilePath!=null)
	        	multipart.addBodyPart(attachment);

		    // Send the complete message parts
		    message.setContent(multipart );		    
		    
		    Transport.send(message);
		    iLogger.info("AutoReports:"+subscriptionType+"Report:"+runId+":sendEmailWithAttachment:reportId:"+reportId+":accMappingCode:"+accMappingCode+":" +
		    		"Email Sent successfully");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":sendEmailWithAttachment:reportId:"+reportId+":accMappingCode:"+accMappingCode+"" +
					":Exception:"+e.getMessage());
			emailStatus="FAILURE";
		}
		
		return emailStatus;
	}
	
	//************************************************************************************************************************************
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public InternetAddress[] getAddressList(String toList)
	{
		Logger fLogger = FatalLoggerClass.logger;
		List emails = new LinkedList();
		String[] address=toList.split(",");
		for(int i=0; i<address.length; i++)
		{
			String tempAddress = address[i].trim();
			if ( 0 < tempAddress.length()) 
			{
				try 
				{
					emails.add(new InternetAddress(tempAddress));
				}
				catch (AddressException e) 
				{
				//	System.out.println("Exception:"+e.getMessage());
					fLogger.fatal("Exception:"+e.getMessage());
				}
			}
		
		}
		
		InternetAddress[] emailAddress = null;
		try 
		{
			emailAddress = (InternetAddress[])emails.toArray(new InternetAddress[0]);
		}
		catch (RuntimeException e) 
		{
			fLogger.fatal("RunTime Exception:"+e.getMessage());
		}
		
		return emailAddress;
	}
}
