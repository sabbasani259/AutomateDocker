//CR337 : 20220721 : Dhiraj K : Property file read.
//20220801-SMTPNewHost changes : Mahesh Sahu
package remote.wise.handler;

import java.util.*;
import java.util.concurrent.Callable;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import org.apache.logging.log4j.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
//import remote.wise.util.WiseLogger;
import remote.wise.util.CommonUtil;

public class SendEmailWithKafka implements Callable
{
	//public static WiseLogger infoLogger = WiseLogger.getLogger("SendEmail:","info");
	//public static WiseLogger fatalError = WiseLogger.getLogger("SendEmail:","fatalError");

	String emailTo,emailSubject,emailBody,serialNumber,transactionTime;
	public String emailTemplateString = null;
	
	
	public Object call() throws Exception
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		// TODO Auto-generated method stub
		iLogger.info("payload from kafka :"+emailTemplateString);
		try
		{
			HashMap<String,String> emailTemplateMap = new Gson().fromJson(emailTemplateString, new TypeToken<HashMap<String, String>>(){}.getType());

			sendMail(emailTemplateMap.get("to"),emailTemplateMap.get("subject"), emailTemplateMap.get("body"),emailTemplateMap.get("serialNumber"), emailTemplateMap.get("transactionTime"));
		}catch (Exception e) {
			// TODO: handle exception
			fLogger.fatal("SendEmailWithKafka : exception caught :"+e.getMessage());
		}
		return null;
}
	
	public String sendMail(String emailTo, String emailSubject, String emailBody, String serialNumber, String transactionTime)
	{
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	//CR337.sn
    	String host = null;
    	String port = null;
    	String smtpHostAuthUser = null;
		String smtpHostAuthPw = null;
    	Properties prop = null;
		try{
			prop = CommonUtil.getDepEnvProperties();
//    		host = prop.getProperty("KafkaEmailHostIP");	//20220801-SMTPNewHost.o
//    		port = prop.getProperty("KafkaEmailPORT");	//20220801-SMTPNewHost.o
			//20220801-SMTPNewHost.sn
			host = prop.getProperty("SMTPEmailHostIP");  
			port = prop.getProperty("SMTPEmailHostPORT");	
			smtpHostAuthUser = prop.getProperty("SMTPEmailAuthUser");  
			smtpHostAuthPw = prop.getProperty("SMTPEmailAuthPw");	
			//20220801-SMTPNewHost.en
    		iLogger.info("sendMail : ip : "+ host + ":port"+port);
    	}catch(Exception e){
    		fLogger.fatal("SendEmailWithKafka : sendMail : " +
    				"Exception in getting Kafka broker Host server detail from properties file: " +e.getMessage());
    	}
    	//CR337.en
		String status="SUCCESS";
		final String username = smtpHostAuthUser;	//20220801-SMTPNewHost.n
		final String password = smtpHostAuthPw;	//20220801-SMTPNewHost.n
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		//props.put("mail.smtp.host", "10.1.3.128");
		//20220705: Dhiraj K : Changes for AWS ip and port changes
		//props.put("mail.smtp.host", "10.179.12.13"); //DF20171103 @Roopa SMTP host change
		props.put("mail.smtp.host", host);
//		props.put("mail.smtp.auth", "false");	//20220801-SMTPNewHost.o
		props.put("mail.smtp.auth", "true");	//20220801-SMTPNewHost.n
		props.put("mail.smtp.port", port);
		props.put("mail.debug","false");
				
		//Session session = Session.getInstance(props);	//20220801-SMTPNewHost.o
		//20220801-SMTPNewHost.sn
				Session session = Session.getInstance(props, new javax.mail.Authenticator() {

					protected PasswordAuthentication getPasswordAuthentication() {
						

						return new PasswordAuthentication(username,password);

					}

				});
				session.setDebug(true);	
				//20220801-SMTPNewHost.en
		
		try 
		{
			Message message = new MimeMessage(session);
			//message.setFrom(new InternetAddress("livelinkindia@jcb.com"));
			//Df20180104 @Roopa changing sender email id
			message.setFrom(new InternetAddress("livelinkindia@jcblivelink.in"));
		    String toList=emailTo;
		    toList = toList+",lli-support@wipro.com";
		    InternetAddress[] emailToList = getAddressList(toList);
		    
		    message.addRecipients(Message.RecipientType.TO, emailToList);
		    message.setSubject(emailSubject);
		    message.setText(emailBody);
		    
		    //DefectID: DF20131105 - Rajani Nagaraju - Adding Info Loggers to track Alerts
		    iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Send Email");
		    iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Email To:"+emailTo);
		    iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Email Subject:"+emailSubject);
		    iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Email Body:"+emailBody);
		    iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Connect to Email Server");
		    Transport.send(message);
		    iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Email Sent");
		   
		} 
		
		catch (MessagingException e) 
		{
			status= "FAILURE";
			fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Messaging Exception:"+e.getMessage());
		}
		
		catch (Exception e) 
		{
			status= "FAILURE";
			
			fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Unable to connect to Email SMS server - Send Failed: "+e);
			
			//If any error in thrown in sending the message to SMTP Server put back the data packet to the Queue
			EmailTemplate emailObj = new EmailTemplate();
			emailObj.setTo(emailTo);
			emailObj.setSubject(emailSubject);
			emailObj.setBody(emailBody);
			emailObj.setSerialNumber(serialNumber);
			emailObj.setTransactionTime(transactionTime);
			
			iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"  Rethrow Message to Queue : Email Server not available");
			iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"To: "+ emailTo);
			iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Subject: "+ emailSubject);
			iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Body: "+ emailBody);
			
			//new EmailHandler().handleEmail("jms/queue/emailQ", emailObj,0);
			new EmailHandler().handleEmailInKafka("EmailQueue", emailObj,0);
			
		}
		
		return status;
	}
	
	
	//CR353.sn
	public String sendMailToDealer(String emailTo, String emailSubject, String emailBody, String serialNumber, String transactionTime)
	{
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	//CR337.sn
    	String host = null;
    	String port = null;
    	String smtpHostAuthUser = null;
		String smtpHostAuthPw = null;
    	Properties prop = null;
		try{
			prop = CommonUtil.getDepEnvProperties();
//    		host = prop.getProperty("KafkaEmailHostIP");	//20220801-SMTPNewHost.o
//    		port = prop.getProperty("KafkaEmailPORT");	//20220801-SMTPNewHost.o
			//20220801-SMTPNewHost.sn
			host = prop.getProperty("SMTPEmailHostIP");  
			port = prop.getProperty("SMTPEmailHostPORT");	
			smtpHostAuthUser = prop.getProperty("SMTPEmailAuthUser");  
			smtpHostAuthPw = prop.getProperty("SMTPEmailAuthPw");	
			//20220801-SMTPNewHost.en
    		iLogger.info("sendMail : ip : "+ host + ":port"+port);
    	}catch(Exception e){
    		fLogger.fatal("SendEmailWithKafka : sendMail : " +
    				"Exception in getting Kafka broker Host server detail from properties file: " +e.getMessage());
    	}
    	//CR337.en
		String status="SUCCESS";
		final String username = smtpHostAuthUser;	//20220801-SMTPNewHost.n
		final String password = smtpHostAuthPw;	//20220801-SMTPNewHost.n
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		//props.put("mail.smtp.host", "10.1.3.128");
		//20220705: Dhiraj K : Changes for AWS ip and port changes
		//props.put("mail.smtp.host", "10.179.12.13"); //DF20171103 @Roopa SMTP host change
		props.put("mail.smtp.host", host);
//		props.put("mail.smtp.auth", "false");	//20220801-SMTPNewHost.o
		props.put("mail.smtp.auth", "true");	//20220801-SMTPNewHost.n
		props.put("mail.smtp.port", port);
		props.put("mail.debug","false");
				
		//Session session = Session.getInstance(props);	//20220801-SMTPNewHost.o
		//20220801-SMTPNewHost.sn
				Session session = Session.getInstance(props, new javax.mail.Authenticator() {

					protected PasswordAuthentication getPasswordAuthentication() {
						

						return new PasswordAuthentication(username,password);

					}

				});
				session.setDebug(true);	
				//20220801-SMTPNewHost.en
		
		try 
		{
			Message message = new MimeMessage(session);
			//message.setFrom(new InternetAddress("livelinkindia@jcb.com"));
			//Df20180104 @Roopa changing sender email id
			message.setFrom(new InternetAddress("livelinkindia@jcblivelink.in"));
		    String toList=emailTo;
		    toList = toList;
		    InternetAddress[] emailToList = getAddressList(toList);
		    
		    message.addRecipients(Message.RecipientType.TO, emailToList);
		    message.setSubject(emailSubject);
		    message.setText(emailBody);
		    
		    //DefectID: DF20131105 - Rajani Nagaraju - Adding Info Loggers to track Alerts
		    iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Send Email");
		    iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Email To:"+emailTo);
		    iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Email Subject:"+emailSubject);
		    iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Email Body:"+emailBody);
		    iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Connect to Email Server");
		    Transport.send(message);
		    iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Email Sent");
		   
		} 
		
		catch (MessagingException e) 
		{
			status= "FAILURE";
			fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Messaging Exception:"+e.getMessage());
		}
		
		catch (Exception e) 
		{
			status= "FAILURE";
			
			fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Unable to connect to Email SMS server - Send Failed: "+e);
			
			//If any error in thrown in sending the message to SMTP Server put back the data packet to the Queue
			EmailTemplate emailObj = new EmailTemplate();
			emailObj.setTo(emailTo);
			emailObj.setSubject(emailSubject);
			emailObj.setBody(emailBody);
			emailObj.setSerialNumber(serialNumber);
			emailObj.setTransactionTime(transactionTime);
			
			iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"  Rethrow Message to Queue : Email Server not available");
			iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"To: "+ emailTo);
			iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Subject: "+ emailSubject);
			iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Body: "+ emailBody);
			
			//new EmailHandler().handleEmail("jms/queue/emailQ", emailObj,0);
			new EmailHandler().handleEmailInKafka("EmailQueue", emailObj,0);
			
		}
		
		return status;
	}
	//CR353.en
	
	
	//JCB6337.sn
	public String sendTenancyMail(String emailTo, String emailSubject, String emailBody, String transactionTime)
	{
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;

    	String host = null;
    	String port = null;
    	String smtpHostAuthUser = null;
		String smtpHostAuthPw = null;
    	Properties prop = null;
		try{
			prop = CommonUtil.getDepEnvProperties();
			host = prop.getProperty("SMTPEmailHostIP");  
			port = prop.getProperty("SMTPEmailHostPORT");	
			smtpHostAuthUser = prop.getProperty("SMTPEmailAuthUser");  
			smtpHostAuthPw = prop.getProperty("SMTPEmailAuthPw");	
    		iLogger.info("sendMail : ip : "+ host + ":port"+port);
    	}catch(Exception e){
    		fLogger.fatal("SendEmailWithKafka : sendMail : " +
    				"Exception in getting Kafka broker Host server detail from properties file: " +e.getMessage());
    	}
		String status="SUCCESS";
		final String username = smtpHostAuthUser;	
		final String password = smtpHostAuthPw;	
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", port);
		props.put("mail.debug","false");
				

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username,password);
			}
		});
		session.setDebug(true);	

		try{
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("livelinkindia@jcblivelink.in"));
		    String toList=emailTo;
		    //toList = toList+",lli-support@wipro.com, deepthi.rao@wipro.com";
		    InternetAddress[] emailToList = getAddressList(toList);
		    
		    message.addRecipients(Message.RecipientType.TO, emailToList);
		    message.setSubject(emailSubject);
		    message.setText(emailBody);

		    iLogger.info("sendTenancyMail:"+transactionTime+":"+"Send Email");
		    iLogger.info("sendTenancyMail:"+transactionTime+":"+"Email To:"+emailTo);
		    iLogger.info("sendTenancyMail:"+transactionTime+":"+"Email Subject:"+emailSubject);
		    iLogger.info("sendTenancyMail:"+transactionTime+":"+"Email Body:"+emailBody);
		    iLogger.info("sendTenancyMail:"+transactionTime+":"+"Connect to Email Server");
		    Transport.send(message);
		    iLogger.info("sendTenancyMail:"+transactionTime+":"+"Email Sent");
		} 
		
		catch (MessagingException e){
			status= "FAILURE";
			fLogger.fatal("sendTenancyMail:"+transactionTime+":"+"Messaging Exception:"+e.getMessage());
		}
		
		catch (Exception e){
			status= "FAILURE";
			fLogger.fatal("sendTenancyMail:"+transactionTime+":"+"Unable to connect to Email SMS server - Send Failed: "+e);
			
			//If any error in thrown in sending the message to SMTP Server put back the data packet to the Queue
			EmailTemplate emailObj = new EmailTemplate();
			emailObj.setTo(emailTo);
			emailObj.setSubject(emailSubject);
			emailObj.setBody(emailBody);
			emailObj.setSerialNumber(serialNumber);
			emailObj.setTransactionTime(transactionTime);
			
			iLogger.info("sendTenancyMail:"+transactionTime+":"+"Rethrow Message to Queue : Email Server not available");
			iLogger.info("sendTenancyMail:"+transactionTime+":"+"To: "+ emailTo);
			iLogger.info("sendTenancyMail:"+transactionTime+":"+"Subject: "+ emailSubject);
			iLogger.info("sendTenancyMail:"+transactionTime+":"+"Body: "+ emailBody);
			
			new EmailHandler().handleEmailInKafka("EmailQueue", emailObj,0);
		}
		return status;
	}
	//JCB6337.en
	
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
	
	
	public String sendMailWithAttachment(String emailTo, String emailSubject, String emailBody,String fileToBeAttached)
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		iLogger.info("Sending mail with attachment ");
		iLogger.info("email body "+emailBody);
		String status="SUCCESS";
		//CR<xxxx>.sn
    	String host = null;
    	String port = null;
    	String smtpHostAuthUser = null;
		String smtpHostAuthPw = null;
    	Properties prop = null;
		try{
			prop = CommonUtil.getDepEnvProperties();
//    		host = prop.getProperty("KafkaEmailHostIP");	//20220801-SMTPNewHost.o
//    		port = prop.getProperty("KafkaEmailPORT");	//20220801-SMTPNewHost.o
			//20220801-SMTPNewHost.sn
			host = prop.getProperty("SMTPEmailHostIP");  
			port = prop.getProperty("SMTPEmailHostPORT");	
			smtpHostAuthUser = prop.getProperty("SMTPEmailAuthUser");  
			smtpHostAuthPw = prop.getProperty("SMTPEmailAuthPw");	
			//20220801-SMTPNewHost.en
    		iLogger.info("sendMail : ip : "+ host + ":port"+port);
    	}catch(Exception e){
    		fLogger.fatal("SendEmailWithKafka : sendMail : " +
    				"Exception in getting Kafka broker Host server detail from properties file: " +e.getMessage());
    	}
    	//CR<xxxx>.en
		
		try 
		{
			final String username = smtpHostAuthUser;	//20220801-SMTPNewHost.n
			final String password = smtpHostAuthPw;	//20220801-SMTPNewHost.n
			Properties props = new Properties();
			props.setProperty("mail.transport.protocol", "smtp");
//			props.put("mail.smtp.host", "10.1.3.128");
			props.put("mail.smtp.host", host);
			//props.put("mail.smtp.auth", "false");	//20220801-SMTPNewHost.o
			props.put("mail.smtp.auth", "true");	//20220801-SMTPNewHost.n
//			props.put("mail.smtp.port", "25");
			props.put("mail.smtp.port", port);
			props.put("mail.debug","true");
					
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
			//DF20140401 - Rajani Nagaraju - Email From Address should be livelinkindia@jcb.com all across the application
			message.setFrom(new InternetAddress("livelinkindia@jcblivelink.in"));
		    String toList=emailTo;
		    //DF20140401 - Rajani Nagaraju - Adding LLI Support to receive all Mails
		    toList = toList+",lli-support@wipro.com";
		    InternetAddress[] emailToList = getAddressList(toList);
		    
		    message.addRecipients(Message.RecipientType.TO, emailToList);
		    message.setSubject(emailSubject);
//		    message.setText(emailBody);
		    
		    //adding attachment
		    
		 // Create the message part 
	         BodyPart messageBodyPart = new MimeBodyPart();
	         
	         Multipart multipart = new MimeMultipart();  
	         
	         //set text here
	         messageBodyPart.setText(emailBody);
	         multipart.addBodyPart(messageBodyPart);
	         // Create a multipar message
	           

	         messageBodyPart = new MimeBodyPart();

	         DataSource source = new FileDataSource(fileToBeAttached);

	        	 messageBodyPart.setDataHandler(new DataHandler(source));
		         messageBodyPart.setFileName(source.getName());  
		         multipart.addBodyPart(messageBodyPart);

		         // Send the complete message parts
		         message.setContent(multipart );
		    
		    Transport.send(message);
		    
		    iLogger.info("Email Sent");
		   
		} 
		
		catch (MessagingException e) 
		{
			status= "FAILURE";
			fLogger.fatal("Messaging Exception:"+e.getMessage());
		}
		
		return status;
	}
	
	public String sendMailWithMultipleAttachment(String emailTo, String emailSubject, String emailBody,String fileToBeAttached)
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		iLogger.info("Sending mail with MultipleAttachment ");
		iLogger.info("email body "+emailBody);
		String status="SUCCESS";
		//CR<xxxx>.sn
    	String host = null;
    	String port = null;
    	String smtpHostAuthUser = null;
		String smtpHostAuthPw = null;
    	Properties prop = null;
		try{
			prop = CommonUtil.getDepEnvProperties();
//    		host = prop.getProperty("KafkaEmailHostIP");	//20220801-SMTPNewHost.o
//    		port = prop.getProperty("KafkaEmailPORT");	//20220801-SMTPNewHost.o
			//20220801-SMTPNewHost.sn
			host = prop.getProperty("SMTPEmailHostIP");  
			port = prop.getProperty("SMTPEmailHostPORT");	
			smtpHostAuthUser = prop.getProperty("SMTPEmailAuthUser");  
			smtpHostAuthPw = prop.getProperty("SMTPEmailAuthPw");	
			//20220801-SMTPNewHost.en
    		iLogger.info("sendMail : ip : "+ host + ":port"+port);
    	}catch(Exception e){
    		fLogger.fatal("SendEmailWithKafka : sendMail : " +
    				"Exception in getting Kafka broker Host server detail from properties file: " +e.getMessage());
    	}
    	//CR<xxxx>.en
		
		
		try 
		{
			final String username = smtpHostAuthUser;	//20220801-SMTPNewHost.n
			final String password = smtpHostAuthPw;	//20220801-SMTPNewHost.n
			Properties props = new Properties();
			props.setProperty("mail.transport.protocol", "smtp");
//			props.put("mail.smtp.host", "10.1.3.128");
			props.put("mail.smtp.host", host);
//			props.put("mail.smtp.auth", "false");
			props.put("mail.smtp.auth", "true");	//20220801-SMTPNewHost.n
//			props.put("mail.smtp.port", "25");
			props.put("mail.smtp.port", port);
			props.put("mail.debug","true");
					
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
			//DF20140401 - Rajani Nagaraju - Email From Address should be livelinkindia@jcb.com all across the application
			message.setFrom(new InternetAddress("livelinkindia@jcb.com"));
		    String toList=emailTo;
		    //DF20140401 - Rajani Nagaraju - Adding LLI Support to receive all Mails
		    toList = toList+",lli-support@wipro.com";
		    InternetAddress[] emailToList = getAddressList(toList);
		    
		    message.addRecipients(Message.RecipientType.TO, emailToList);
		    message.setSubject(emailSubject);
//		    message.setText(emailBody);
		    
		    //adding attachment
		    
		 // Create the message part 
	         BodyPart messageBodyPart = new MimeBodyPart();
	         
	         Multipart multipart = new MimeMultipart();  
	         
	         //set text here
	         messageBodyPart.setText(emailBody);
	         multipart.addBodyPart(messageBodyPart);
	         // Create a multipar message
	           

	         messageBodyPart = new MimeBodyPart();

	         String[] files=fileToBeAttached.split(",");
				
				
				for(int i=0;i<files.length;i++){
					iLogger.info("SErvice due File:"+files[i]);
				   messageBodyPart = new MimeBodyPart();
					DataSource source = new FileDataSource(files[i]);

		        	 messageBodyPart.setDataHandler(new DataHandler(source));
		        	 messageBodyPart.setFileName(source.getName()); 
		        	 iLogger.info("Service File Name:"+source.getName());
		        	 multipart.addBodyPart(messageBodyPart);
				}
	         		          
		         

		         // Send the complete message parts
		         message.setContent(multipart );
		    
		    Transport.send(message);
		    
		    iLogger.info("Email Sent");
		   
		} 
		
		catch (MessagingException e) 
		{
			status= "FAILURE";
			fLogger.fatal("Sending mail with MultipleAttachment Exception:"+e.getMessage());
		}
		
		return status;
	}

	
}
