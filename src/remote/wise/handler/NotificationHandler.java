package remote.wise.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.*;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetGroupProfileEntity;
import remote.wise.businessentity.AssetOwnerSnapshotEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.businessentity.EmailTemplateEntity;
import remote.wise.businessentity.SmsTemplateEntity;
import remote.wise.businessentity.SmsTemplateTranslatorEntity;
import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AssetOwnershipRespContract;
import remote.wise.service.implementation.DomainServiceImpl;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
import remote.wise.util.StaticProperties;
//import remote.wise.util.WiseLogger;

/** This class handles SMS and Email notifications to be placed into their respective Queues
 * @author Rajani Nagaraju
 *
 */
public class NotificationHandler 
{
	String emailTo;
	String emailSubject;
	String emailBody;
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger fatalError = WiseLogger.getLogger("NotificationHandler:","fatalError");
	//public static WiseLogger infoLogger = WiseLogger.getLogger("NotificationHandler:","info");
	
	/** SMS handler that places the SMS notification into SMS hornet Queue
	 * DefectID:  - 1288 - Rajani Nagaraju - Custom SMS implementation
	 * @param smsUsers List of Users to whom the SMS has to be sent
	 * @param serialNumber VIN
	 * @param eventSeverity severity of the notification
	 * @param transactionTime time when the event was generated
	 * @param eventId corresponding eventId
	 * @param isEmergencyAlert to determine whether it is emergency Alert
	 * @return Returns Status String
	 */
	public String smsHandler(List<String> smsUsers, List<String> userName, String serialNumber, String eventSeverity, String transactionTime, 
							int eventId, boolean isEmergencyAlert, String currentGPSfix)
	{
		//DefectID:  - 1288 - Rajani Nagaraju - Custom SMS implementation
		List<String> smsTo = new LinkedList<String>();
		List<String> smsMsgBody =  new LinkedList<String>();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String status = "SUCCESS";
		
		//DefectID:  - 1288 - Rajani Nagaraju - Custom SMS implementation
		String SmsTemp_VIN = null;
		String SmsTemp_SmsUserName =null;
		String SmsTemp_MachineProfile=null;
		String SmsTemp_ContactNum =null;
		 //Defect: DF20131031 - Rajani Nagaraju - To decide SMS body based on GPS Fix for Tow away alert
		String TowAwayEventName =null;
		String TowAwayRemovalStr=null;
		
		 Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
		try
		{
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			SmsTemp_VIN = prop.getProperty("SmsTemp_VIN");
			SmsTemp_SmsUserName = prop.getProperty("SmsTemp_SmsUserName");
			SmsTemp_MachineProfile= prop.getProperty("SmsTemp_MachineProfile");
			SmsTemp_ContactNum =  prop.getProperty("SmsTemp_ContactNum");
			 //Defect: DF20131031 - Rajani Nagaraju - To decide SMS body based on GPS Fix for Tow away alert
			TowAwayEventName=prop.getProperty("TowAwayEventName");
			TowAwayRemovalStr = prop.getProperty("TowAwayRemovalStr");				
			smsTo = smsUsers;
		
			//set the message body
			String msgBody =null;
			String templateId =null;
			
			//Get the sms body template and templateId
			SmsTemplateEntity smsTemplate =null;
			Query query = session.createQuery("from SmsTemplateEntity where eventId='"+eventId+"'");       	
        	Iterator itr = query.list().iterator();
        	while(itr.hasNext())
        	{
        		smsTemplate = (SmsTemplateEntity) itr.next();
				msgBody = smsTemplate.getSmsBody();
				templateId = smsTemplate.getTemplateId();
        	}
		
        	
        	if(smsTemplate!=null)
        	{
        		//get the Machine Profile
        		AssetEntity asset =null;
	        	String machineProfile = "";
	        	int assetGroupId=0;
	        	Query q = session.createQuery(" from AssetEntity where serial_number='"+serialNumber+"'");
	        	Iterator itrtr = q.list().iterator();
	        	while(itrtr.hasNext())
	        	{
	        		asset = (AssetEntity) itrtr.next();
	        		assetGroupId = asset.getProductId().getAssetGroupId().getAsset_group_id();
	        		//DefectId:20140917 @Suprava To Dispaly asset_group_name in place of asset_group_code
	        		machineProfile = asset.getProductId().getAssetGroupId().getAsset_group_name();
	        		//DefectId:20140917  End
	        	}
	        	/*Query q1 = session.createQuery(" from AssetGroupProfileEntity where asset_grp_id='"+assetGroupId+"'");
	        	Iterator itr1= q1.list().iterator();
	        	while(itr1.hasNext())
	        	{
	        		AssetGroupProfileEntity assetGroupProfile = (AssetGroupProfileEntity)itr1.next();
	        		machineProfile = assetGroupProfile.getAsset_grp_code();
	        	}*/
	        	if(machineProfile==null)
	        	{
	        		machineProfile="";
	        	}
	        	String chkProfile = machineProfile.replaceAll("\\s","") ;
	        	if(! (chkProfile.length()>0))
	        		machineProfile="";
	        	
	        	//get the dealer ContactNumber
	        	String contactNumber ="";
	        	String accountName ="";
	        	DomainServiceImpl domainService = new DomainServiceImpl();
	        	AssetDetailsBO assetManagement = new AssetDetailsBO();
	        	HashMap<String,Integer> assetOwners = assetManagement.getAssetOwners(serialNumber);
	        	if(assetOwners.get("Customer") != null)
	    		{
	        		AccountEntity account = domainService.getAccountObj(assetOwners.get("Dealer"));
	        		accountName = account.getAccount_name();
	        		contactNumber = account.getMobile_no();
	    		}
	        	else if(assetOwners.get("Dealer") != null)
	        	{
	        		AccountEntity account = domainService.getAccountObj(assetOwners.get("Dealer"));
	        		accountName = account.getAccount_name();
	        		contactNumber = account.getMobile_no();
	        	}
	        	
	        	else
	        	{
	        		AccountEntity account = domainService.getAccountObj(assetOwners.get("OEM"));
	        		accountName = account.getAccount_name();
	        		contactNumber = account.getMobile_no();
	        	}
	        	if(contactNumber==null || contactNumber.equals(" "))
	        	{
	        		contactNumber=accountName;
	        	}
        	
	        	//DefectId: Not provided - Rajani Nagaraju - 20131011 - SMS HTTP URL changed from KAPSYSTEM
	        	for(int i=0; i< userName.size(); i++)
	        	{
	        		String encodedUser = userName.get(i);
	        		Pattern MY_PATTERN = Pattern.compile("\\<(.*?)\\>");
	        		Matcher m = MY_PATTERN.matcher(msgBody);
	        		StringBuffer sb = new StringBuffer();
	        		//int counter =1;
	        		while (m.find()) 
	        		{
					    String s1 = "<"+m.group(1)+">";
					    if(m.group(1).equalsIgnoreCase(SmsTemp_SmsUserName))
					    	m.appendReplacement(sb,encodedUser);
					    if(m.group(1).equalsIgnoreCase(SmsTemp_MachineProfile))
					    	m.appendReplacement(sb,machineProfile);
					    if(m.group(1).equalsIgnoreCase(SmsTemp_VIN))
					    	m.appendReplacement(sb,serialNumber);
					    if(m.group(1).equalsIgnoreCase(SmsTemp_ContactNum))
					    	m.appendReplacement(sb,contactNumber);
					    if(m.group(1).equalsIgnoreCase(" "))
					    	m.appendReplacement(sb,"");
					    
					}
				
	        		m.appendTail(sb);
				
				    String body = sb.toString();
				    
				    //Defect: DF20131031 - Rajani Nagaraju - To decide SMS body based on GPS Fix for Tow away alert
				    if(body.contains(TowAwayEventName))
				    {
				    	//Determine the body content based on GPS Fix
				    	if(currentGPSfix.equalsIgnoreCase("1"))
				    	{
				    		body = body.replace(TowAwayRemovalStr, "");
				    	}
				    }
				    
				    smsMsgBody.add(body);
				
				   // System.out.println("Final SMS Body: "+body);
	        	}
	        	
			
				//Publish into SMS Queue
				SmsTemplate smsObj = new SmsTemplate();
				smsObj.setTo(smsTo);
				smsObj.setMsgBody(smsMsgBody);
				
				//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
				smsObj.setSerialNumber(serialNumber);
				smsObj.setTransactionTime(transactionTime);
			
				//DefectID: DF20131105 - Rajani Nagaraju - Adding Info Loggers to track Alerts
				iLogger.info(serialNumber+":"+transactionTime+"  :"+"---- SMS Notification ------");
				iLogger.info(serialNumber+":"+transactionTime+"  :"+"To: "+ smsTo);
				iLogger.info(serialNumber+":"+transactionTime+"  :"+"Body: "+ smsMsgBody);
				
				//DefectID:1012 - Rajani Nagaraju - 20130718
	        	//HornetQ issue for email Queue
				//new SmsHandler().handleSms("jms/queue/smsQ", smsObj,0);
				//DF20171016 - KO369761 : Changed SMSQ Service to Kafka Queue.
				new SmsHandler().handleSmsInKafka("SMSQueue", smsObj,0);
				//new SmsHandler().handleSms("queue/smsQueue", smsObj);
				
				if(! (session.isOpen() ))
	            {
	                        session = HibernateUtil.getSessionFactory().getCurrentSession();
	                        session.getTransaction().begin();
	            }
        	}
	   
		}
		
		catch(Exception e)
		{
			fLogger.fatal(serialNumber+":"+transactionTime+"  :"+"Exception :"+e);
			

			Writer result = new StringWriter();
    	    PrintWriter printWriter = new PrintWriter(result);
    	    e.printStackTrace(printWriter);
    	    String err = result.toString();
    	    fLogger.fatal(serialNumber+":"+transactionTime+"  :"+"Exception trace: "+err);
    	    try 
    	    {
    	    	printWriter.close();
        	    result.close();
			} 
    	    
    	    catch (IOException e1) 
    	    {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	    
			e.printStackTrace();
		}
        
        finally
        {
        	if(session.isOpen())
              if(session.getTransaction().isActive())
              {
                    session.getTransaction().commit();
              }
              
              if(session.isOpen())
              {
                    session.flush();
                    session.close();
              }
              
        }
		return status;
	}
	
	
	
	/**  Email handler that places the notification into Email hornet Queue
	 * DefectID:1320 - Rajani Nagaraju - 20130920 - Null Check for email alerts
	 * @param emailUsers List of Users to whom the Email has to be sent
	 * @param serialNumber VIN 
	 * @param eventSeverity severity of the notification
	 * @param transactionTime time when the event was generated
	 * @param eventId corresponding eventId
	 * @param eventName Name of the event
	 * @param value parameter value
	 * @param engineNumber engine Number of the VIN
	 * @param latValue latitude value
	 * @param longValue longitude value
	 * @return Returns Status String
	 */
	public String emailHandler(List<String> emailUsers, String serialNumber, String eventSeverity, String transactionTime, int eventId, 
							String eventName, String value, String engineNumber, String latValue, String longValue, String currentGPSfix)
	{
		String status = "SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String Email_NotificationName = null;
		String Email_Severity = null;
		String Email_PIN = null;
		String Email_EngineNumber = null;
		String Email_Value = null;
		String Email_SnapshotTime = null;
		String LandmarkArrival = null;
		String LandmarkDeparture = null;
		String LandmarkName = null;
		String Email_Location = null;
		String Email_CustomerName = null;
		String Email_Description = null;
		String Email_DealerName = null;
		String Email_DealerphoneNumber = null;
		String Alert_Description = null;
		String Email_MachineProfile = null;
		//Defect: DF20131031 - Rajani Nagaraju - To decide SMS body based on GPS Fix for Tow away alert
		String TowAwayEventName =null;
		String TowAwayRemovalStr=null;
		
		/*Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger infoLogger = Logger.getLogger("infoLogger"); */
        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
		try
		{
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			Email_NotificationName = prop.getProperty("Email_NotificationName");
			Email_Severity = prop.getProperty("Email_Severity");
			Email_PIN = prop.getProperty("Email_PIN");
			Email_EngineNumber = prop.getProperty("Email_EngineNumber");
			Email_Value = prop.getProperty("Email_Value");
			Email_SnapshotTime = prop.getProperty("Email_SnapshotTime");
			LandmarkArrival = prop.getProperty("LandmarkArrival");
			LandmarkDeparture = prop.getProperty("LandmarkDeparture");
			LandmarkName = prop.getProperty("LandmarkName");
			Email_Location = prop.getProperty("Email_Location");
			Email_CustomerName = prop.getProperty("Email_CustomerName");
			Email_Description = prop.getProperty("Email_Description");
			Email_DealerphoneNumber = prop.getProperty("Email_DealerphoneNumber");
			Email_DealerName = prop.getProperty("Email_DealerName");
			Alert_Description = prop.getProperty("Alert_Description");
			Email_MachineProfile = prop.getProperty("Email_MachineProfile");
			//Defect: DF20131031 - Rajani Nagaraju - To decide SMS body based on GPS Fix for Tow away alert
			TowAwayEventName=prop.getProperty("TowAwayEventName");
			TowAwayRemovalStr = prop.getProperty("TowAwayRemovalStr");
			
			//Defect id:1146 Added Email alerts time in gmt+5:30 format and in 12 hour format-am/pm @ suprava on 28/8/2013 
			Date date=null;
	        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	        datetimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
				try {
					date = datetimeFormat.parse(transactionTime);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SimpleDateFormat datetimeFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
				datetimeFormat1.setTimeZone(TimeZone.getTimeZone("IST"));
				String GmtTransactionTime = datetimeFormat1.format(date);
			
			ListToStringConversion conversion = new ListToStringConversion();
			String toList = conversion.getCommaSeperatedStringList(emailUsers).toString();
			this.emailTo = toList;
		   
		   //Defect id 883:Added Email body content - Suprava Nayak - 29/08/2013
		   //Get CustomerName
			//DefectID:1320 - Rajani Nagaraju - 20130920 - Null Check for email alerts
			String CustomerName = " ";
			
			Query query1 = session.createQuery("select a.account_name from AccountEntity a,AssetAccountMapping b where a.status=true and a.account_id=b.accountId and b.serialNumber='"+serialNumber+"'");
			Iterator itr1 = query1.list().iterator();
			if(itr1.hasNext())
			{
				CustomerName = (String)itr1.next();
			}
			//Get Notification Description
			String Description = " ";
			Query query2 = session.createQuery("select eventDescription from EventEntity where eventId="+eventId);
			Iterator itr2 = query2.list().iterator();
			if(itr2.hasNext())
			{
				Description = (String)itr2.next();
			}
			
			//Defect: DF20131031 - Rajani Nagaraju - To decide Email body based on GPS Fix for Tow away alert
			if(Description.contains(TowAwayEventName))
			{
				//Determine the body content based on GPS Fix
		    	if(currentGPSfix.equalsIgnoreCase("1"))
		    	{
		    		Description = Description.replace(TowAwayRemovalStr, "");
		    		eventName = eventName.replace(TowAwayRemovalStr, "");
		    	}
			}
			
			//Get Dealer primary contact and Dealer Name 
			//DefectID:1320 - Rajani Nagaraju - 20130920 - Null Check for email alerts
			String DealerName = " ";
			String DealerPhoneNumber = " ";
			DomainServiceImpl domainService = new DomainServiceImpl();
			AssetDetailsBO assetManagement = new AssetDetailsBO();
			HashMap<String,Integer> assetOwners = assetManagement.getAssetOwners(serialNumber);
			if(assetOwners.get("Dealer") != null)
			{
				AccountEntity account = domainService.getAccountObj(assetOwners.get("Dealer"));
				DealerName=account.getAccount_name();
				DealerPhoneNumber=account.getMobile_no();
			}
			//Get Machine Profile 
			String MachineProfile = " ";
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			Query query3 = session.createQuery("select a.assetGroupName from AssetClassDimensionEntity a,AssetEntity b where b.serial_number='"+serialNumber+"' and a.productId=b.productId");
			Iterator itr3 = query3.list().iterator();
			if(itr3.hasNext())
			{
				MachineProfile = (String)itr3.next();
			}
			//set the message body
			Query query = session.createQuery("from EmailTemplateEntity where eventId='"+eventId+"'");
			Iterator itr = query.list().iterator();
			String emailMsgBody = null;
			String emailSubject = null;
			
				while(itr.hasNext())
			{
				EmailTemplateEntity emailTemplate = (EmailTemplateEntity) itr.next();
				emailMsgBody = emailTemplate.getEmailBody();
				emailSubject = emailTemplate.getEmailSubject();
			}
				//DefectId: DF20131111-@ suprava Null check
				if(value==null)
				{
					value=" ";
				}
				if(CustomerName==null)
				{
					CustomerName=" ";
				}
				if(Description==null)
				{
					Description=" ";
				}
				if(MachineProfile==null)
				{
					MachineProfile=" ";
				}
				if(DealerPhoneNumber==null)
				{
					DealerPhoneNumber=" ";
				}
				if(DealerName==null)
				{
					DealerName=" ";
				}
				if(eventName==null)
				{
					eventName=" ";
				}
				if(eventSeverity==null)
				{
					eventSeverity=" ";
				}
				if(serialNumber==null)
				{
					serialNumber=" ";
				}
				if(GmtTransactionTime==null)
				{
					GmtTransactionTime=" ";
				}
				if(engineNumber==null)
				{
					engineNumber=" ";
				}
				if(latValue==null)
				{
					latValue=" ";
				}
				if(longValue==null)
				{
					longValue=" ";
				}
			//DefectId: DF20131014 - Rajani Nagaraju - To fix the defect <Landmark Name> not getting displayed in Subject and Body in Email
			/*String eventLandmark = "<"+LandmarkName+">";
			if(eventName.contains(eventLandmark))
			{
				eventName = eventName.replaceAll(eventLandmark, value);
			}*/
				
			//for Message body in the Email
			Pattern MY_PATTERN = Pattern.compile("\\<(.*?)\\>");
			Matcher m = MY_PATTERN.matcher(emailMsgBody);
			StringBuffer sb = new StringBuffer();
			while (m.find()) 
			{
			  	String s1 = "<"+m.group(1)+">";
			    if(m.group(1).equalsIgnoreCase(LandmarkName))
			    	m.appendReplacement(sb,value);
			    if(m.group(1).equalsIgnoreCase(Email_CustomerName))
			    	m.appendReplacement(sb, CustomerName);
			    if(m.group(1).equalsIgnoreCase(Email_Description))
			    	m.appendReplacement(sb, Description);
			    if(m.group(1).equalsIgnoreCase(Email_MachineProfile))
			    	m.appendReplacement(sb,MachineProfile);
			  	if(m.group(1).equalsIgnoreCase(Email_DealerphoneNumber))
			    	m.appendReplacement(sb,DealerPhoneNumber);
			  	if(m.group(1).equalsIgnoreCase(Email_DealerName))
			    	m.appendReplacement(sb,DealerName);
			  	if(m.group(1).equalsIgnoreCase(Email_NotificationName))
			    	m.appendReplacement(sb,eventName);
			    if(m.group(1).equalsIgnoreCase(Email_Severity))
			    	m.appendReplacement(sb,eventSeverity);
			    if(m.group(1).equalsIgnoreCase(Email_PIN))
			    	m.appendReplacement(sb,serialNumber);
			    if(m.group(1).equalsIgnoreCase(Email_Value))
			    	m.appendReplacement(sb,value);
			    if(m.group(1).equalsIgnoreCase(Email_SnapshotTime))
			    	m.appendReplacement(sb,GmtTransactionTime);
			    if(m.group(1).equalsIgnoreCase(Email_EngineNumber))
			    	m.appendReplacement(sb,engineNumber);
			    if(m.group(1).equalsIgnoreCase(Email_Location))
			    	m.appendReplacement(sb,latValue+","+longValue);
			    
			}
			m.appendTail(sb);
			String body = sb.toString().replaceAll(";", "\n");
			
			//DefectId: DF20131014 - Rajani Nagaraju - To fix the defect <Landmark Name> not getting displayed in Subject and Body in Email
			String eventLandmark = "<"+LandmarkName+">";
			if(body.contains(eventLandmark))
			{
				body = body.replaceAll(eventLandmark, value);
			}
			
			this.emailBody = body;
				
			//for the Subject of the message in the Email
			Pattern MY_PATTERN_SUBJECT = Pattern.compile("\\<(.*?)\\>");
			Matcher m1 = MY_PATTERN_SUBJECT.matcher(emailSubject);
			StringBuffer sb1 = new StringBuffer();
			while (m1.find()) 
			{
			    String s1 = "<"+m1.group(1)+">";
			    if(m1.group(1).equalsIgnoreCase(Alert_Description))
			    	m1.appendReplacement(sb1,Description);
			    
			    if(m1.group(1).equalsIgnoreCase(Email_NotificationName))
			    	m1.appendReplacement(sb1,eventName);
			    if(m1.group(1).equalsIgnoreCase(Email_Severity))
			    	m1.appendReplacement(sb1,eventSeverity);
			    if(m1.group(1).equalsIgnoreCase(Email_PIN))
			    	m1.appendReplacement(sb1,serialNumber);
			    if(m1.group(1).equalsIgnoreCase(Email_EngineNumber))
			    	m1.appendReplacement(sb1,engineNumber);
			    

			    if(m1.group(1).equalsIgnoreCase(LandmarkName))
			    	m1.appendReplacement(sb1,value);
			    
			}
			m1.appendTail(sb1);
			
			//DefectId: DF20131014 - Rajani Nagaraju - To fix the defect <Landmark Name> not getting displayed in Subject and Body in Email
			String sub = sb1.toString();
			if(sub.contains(eventLandmark))
			{
				sub = sub.replaceAll(eventLandmark, value);
			}
			
			this.emailSubject = sub;
		
			
			//place the data into email queue
			EmailTemplate emailObj = new EmailTemplate();
			emailObj.setTo(this.emailTo);
			emailObj.setSubject(this.emailSubject);
			emailObj.setBody(this.emailBody);
			//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
			emailObj.setSerialNumber(serialNumber);
			emailObj.setTransactionTime(transactionTime);
			
			//DefectID: DF20131105 - Rajani Nagaraju - Adding Info Loggers to track Alerts
			//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
			iLogger.info(serialNumber+":"+transactionTime+"  :"+"---- Email Notification ------");
			iLogger.info(serialNumber+":"+transactionTime+"  :"+"To: "+ this.emailTo);
			iLogger.info(serialNumber+":"+transactionTime+"  :"+"Subject: "+ this.emailSubject);
			iLogger.info(serialNumber+":"+transactionTime+"  :"+"Body: "+ this.emailBody);
			//System.out.println("Body: "+ this.emailBody);
			
			//DefectID:1012 - Rajani Nagaraju - 20130718
        	//HornetQ issue for email Queue
			//new EmailHandler().handleEmail("jms/queue/emailQ", emailObj,0);
			//DF20171016 - KO369761 : Changed EmailQ Service from Hornet to Kafka Queue.
			new EmailHandler().handleEmailInKafka("EmailQueue", emailObj,0);
			//new EmailHandler().handleEmail("queue/emailQueue", emailObj);
		  
		}
		
		catch(Exception e)
		{
			fLogger.fatal(serialNumber+":"+transactionTime+"  :"+"Exception :"+e);
			

			Writer result = new StringWriter();
    	    PrintWriter printWriter = new PrintWriter(result);
    	    e.printStackTrace(printWriter);
    	    String err = result.toString();
    	    fLogger.fatal(serialNumber+":"+transactionTime+"  :"+"Exception trace: "+err);
    	    try 
    	    {
    	    	printWriter.close();
        	    result.close();
			} 
    	    
    	    catch (IOException e1) 
    	    {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	    
			e.printStackTrace();
		}
        
        finally
        {
        	if(session.isOpen())  
	        	if(session.getTransaction().isActive())
	              {
	                    session.getTransaction().commit();
	              }
              
              if(session.isOpen())
              {
                    session.flush();
                    session.close();
              }
              
        }
		    
		return status;
	}
	
	//******************************************************* SMS Handler - New *************************************************************
	public String smsHandlerNew(List<String> smsUsers, List<String> userName, String serialNumber, String eventSeverity, String transactionTime, 
			int eventId, boolean isEmergencyAlert, String currentGPSfix, int assetEventId)
	{
		String status = "SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
    	iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventId+":SMS Handler - START");
		try
		{
			//Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			try
			{
				Properties prop=null;
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventId+":Read from property file - START");
				try
				{
					prop= StaticProperties.getConfProperty();
				}
				catch(Exception e)
				{
					fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+eventId+":Error in intializing property File :"+e);
					return "FAILURE";
				}
				
				String SmsTemp_VIN = prop.getProperty("SmsTemp_VIN");
				String SmsTemp_SmsUserName = prop.getProperty("SmsTemp_SmsUserName");
				String SmsTemp_MachineProfile= prop.getProperty("SmsTemp_MachineProfile");
				String SmsTemp_ContactNum =  prop.getProperty("SmsTemp_ContactNum");
				String TowAwayEventName=prop.getProperty("TowAwayEventName");
				String TowAwayRemovalStr = prop.getProperty("TowAwayRemovalStr");
				
				
				List<String> smsMsgBody =  new LinkedList<String>();
				
				//------------- STEP1: Get the SMS body template object
				SmsTemplateEntity smsTemplate =null;
				Query smsTempQ = session.createQuery("from SmsTemplateEntity where eventId='"+eventId+"'");       	
	        	Iterator smsTempItr = smsTempQ.list().iterator();
	        
	        	while(smsTempItr.hasNext())
	        	{
	        		smsTemplate = (SmsTemplateEntity) smsTempItr.next();
	        		iLogger.info(smsTemplate.getSmsBody());
	        		System.out.println("SMS Template"+smsTemplate.getSmsBody());
				}
				
	        	if(smsTemplate!=null)
	        	{
	        		AssetEntity asset=null;
	        		//----------------- STEP2: Get the Machine Profile
	        		String machineProfile="";
	        		Query assetQ = session.createQuery(" from AssetEntity where serial_number='"+serialNumber+"'");
		        	Iterator assetItr = assetQ.list().iterator();
		        	if(assetItr.hasNext())
		        	{
		        		asset = (AssetEntity) assetItr.next();
		        		
		        		if(asset.getProductId()!=null && asset.getProductId().getAssetGroupId()!=null)
		        			machineProfile=asset.getProductId().getAssetGroupId().getAsset_group_name();
		        	}
		        	
		        	
		        	//------------------ STEP3: Get the Dealer Contact Number for Customer machine
		        	String contactNum="";
		        	HashMap<String,String> accTypeContactMap = new HashMap<String,String>();
		        	
		        	Query dealerContactQ = session.createQuery(" from AssetOwnerSnapshotEntity where serialNumber='"+serialNumber+"'");
		        	Iterator dealerContactItr = dealerContactQ.list().iterator();
		        	while(dealerContactItr.hasNext())
		        	{
		        		AssetOwnerSnapshotEntity assetOwnerSnap = (AssetOwnerSnapshotEntity)dealerContactItr.next();
		        		accTypeContactMap.put(assetOwnerSnap.getAccountType(),assetOwnerSnap.getAccountId().getMobile_no());
		        	}
		        	
		        	if(accTypeContactMap.containsKey("Customer"))
		        		contactNum=accTypeContactMap.get("Dealer");
		        	else if(accTypeContactMap.containsKey("Dealer"))
		        		contactNum=accTypeContactMap.get("Dealer");
		        	else 
			        	contactNum=accTypeContactMap.get("OEM");
		        	//String hindi ="à¤ªà¥�à¤°à¤¿à¤¯ à¤¸à¤° / à¤®à¥ˆà¤¡à¤®, à¤œà¥‡à¤¸à¥€à¤¬à¥€ Backhoe HAR3DXSSE01869929à¤�à¤¯à¤° à¤«à¤¿à¤²à¥�à¤Ÿà¤° à¤¸à¤¾à¤« à¤¹à¥‹. à¤†à¤ªà¤•à¥€ à¤®à¤¶à¥€à¤¨ à¤•à¥€ à¤œà¤¾à¤�à¤š à¤¹à¥‹ Call: 0141-3027000";	
		        	//--------------------- STEP4: Build the SMS Content
		        	//START - defId @20150526 
		        	/*for(int i=0; i< userName.size(); i++)
		        	{
		        		String encodedUser = userName.get(i);
		        		Pattern MY_PATTERN = Pattern.compile("\\<(.*?)\\>");
		        		Matcher m = MY_PATTERN.matcher(smsTemplate.getSmsBody());
		        		StringBuffer sb = new StringBuffer();
		        		
		        		while (m.find()) 
		        		{
						    String s1 = "<"+m.group(1)+">";
						    if(m.group(1).equalsIgnoreCase(SmsTemp_SmsUserName))
						    	m.appendReplacement(sb,encodedUser);
						    if(m.group(1).equalsIgnoreCase(SmsTemp_MachineProfile))
						    	m.appendReplacement(sb,machineProfile);
						    if(m.group(1).equalsIgnoreCase(SmsTemp_VIN))
						    	m.appendReplacement(sb,serialNumber);
						    if(m.group(1).equalsIgnoreCase(SmsTemp_ContactNum))
						    	m.appendReplacement(sb,contactNum);
						    if(m.group(1).equalsIgnoreCase(" "))
						    	m.appendReplacement(sb,"");
						}
		        		
		        		m.appendTail(sb);
						String body = sb.toString();
			        	
						//Decide SMS body based on GPS Fix for Tow away alert
					    if(body.contains(TowAwayEventName))
					    {
					    	//Determine the body content based on GPS Fix
					    	if(currentGPSfix.equalsIgnoreCase("1"))
					    	{
					    		body = body.replace(TowAwayRemovalStr, "");
					    	}
					    }
					    smsMsgBody.add(body);
		        	}*/
		        	String body = null;
		        	for(int i=0;i<smsUsers.size();i++)
		        	{
		        		String contactNo = smsUsers.get(i);
		        		
		        		Query langQuery = session.createQuery("from ContactEntity where primary_mobile_number='"+contactNo+"'");
		        	
		        		Iterator langItr = langQuery.list().iterator();
		        		ContactEntity userLang = null;
		        		String language = null;
		        		while(langItr.hasNext())
		        		{
		        			userLang = (ContactEntity)langItr.next();
		        			language = userLang.getLanguage();
		        			System.out.println("language"+language);
		        		}
		        		
		        		Query smsBodyQuery = session.createQuery("from SmsTemplateTranslatorEntity where eventId="+eventId+" and unicode='"+language+"'");
		        		System.out.println(smsBodyQuery);
		        		Iterator smsBodyItr = smsBodyQuery.list().iterator();
		        		String smsMessage = null;
		        		SmsTemplateTranslatorEntity smsTranslator = null;
		        		while(smsBodyItr.hasNext())
		        		{
		        			smsTranslator = (SmsTemplateTranslatorEntity)smsBodyItr.next();
		        			String unicode = smsTranslator.getSmsBody();
		        			System.out.println("unicode"+unicode);
		        			//smsTemplate.setSmsBody(UniConvertHindi(unicode));
		        			//smsTemplate.setSmsBody(StringEscapeUtils.unescapeJava(unicode));
		        			smsMessage = StringEscapeUtils.unescapeJava(unicode);
		        		}
		        		System.out.println("Encoded Message:"+smsTemplate.getSmsBody());
		        		Pattern MY_PATTERN = Pattern.compile("\\<(.*?)\\>");
		        		if(smsMessage == null || smsMessage.isEmpty() || smsMessage.equalsIgnoreCase(""))
		        			smsMessage = smsTemplate.getSmsBody();
		        		Matcher m = MY_PATTERN.matcher(smsMessage);
		        		StringBuffer sb = new StringBuffer();
		        		
		        		while (m.find()) 
		        		{
						    String s1 = "<"+m.group(1)+">";
						    if(m.group(1).equalsIgnoreCase(SmsTemp_MachineProfile))
						    	m.appendReplacement(sb,machineProfile);
						    if(m.group(1).equalsIgnoreCase(SmsTemp_VIN))
						    	m.appendReplacement(sb,serialNumber);
						    if(m.group(1).equalsIgnoreCase(SmsTemp_ContactNum))
						    	m.appendReplacement(sb,contactNum);
						    if(m.group(1).equalsIgnoreCase(" "))
						    	m.appendReplacement(sb,"");
						}
		        		
		        		m.appendTail(sb);
						body = sb.toString();
			        	//System.out.println("Correct SMS Body:"+body);
						//Decide SMS body based on GPS Fix for Tow away alert
					    if(body.contains(TowAwayEventName))
					    {
					    	//Determine the body content based on GPS Fix
					    	if(currentGPSfix.equalsIgnoreCase("1"))
					    	{
					    		body = body.replace(TowAwayRemovalStr, "");
					    	}
					    }
					    smsMsgBody.add(body);
					   // System.out.println("smsMsgBody:"+smsMsgBody);
		        	}
		        	 
		        	
		        	//---------------------- STEP5: Publish to SMS Queue
		        /*	SmsTemplate smsObj = new SmsTemplate();
					smsObj.setTo(smsUsers);
					smsObj.setMsgBody(smsMsgBody);
					smsObj.setSerialNumber(serialNumber);
					smsObj.setTransactionTime(transactionTime);
				
					iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventId+": ---- SMS Notification Content ------");
					iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventId+": To: "+ smsUsers);
					iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventId+": Body: "+ smsMsgBody);
					
					new SmsHandler().handleSms("jms/queue/smsQ", smsObj,0);*/
		        	
		        	//Df20150317 - Rajani Nagaraju - Make to call to KAPSYS directly on first call, if KAPSYS is not available then put to Queue
		        	iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventId+": ---- SMS Notification Content ------");
					iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventId+": To: "+ smsUsers);
					iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventId+": Body: "+ smsMsgBody);
		        	new SendSMS(smsUsers,smsMsgBody,serialNumber,transactionTime, assetEventId);
					
					if(! (session.isOpen() ))
		            {
		                       /* session = HibernateUtil.getSessionFactory().getCurrentSession();
		                        session.getTransaction().begin();*/
						
						session = HibernateUtil.getSessionFactory().openSession();
                        session.beginTransaction();
		            }
	        	}
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+eventId+":Exception :"+e);
				Writer result = new StringWriter();
	    	    PrintWriter printWriter = new PrintWriter(result);
	    	    e.printStackTrace(printWriter);
	    	    String err = result.toString();
	    	    fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+eventId+":Exception trace: "+err);
	    	    try 
	    	    {
	    	    	printWriter.close();
	        	    result.close();
				} 
	    	    
	    	    catch (IOException e1) 
	    	    {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	    
	    	    finally
	            {
	            	if(session.isOpen())
	                  if(session.getTransaction().isActive())
	                  {
	                        session.getTransaction().commit();
	                  }
	                  
	                  if(session.isOpen())
	                  {
	                        session.flush();
	                        session.close();
	                  }
	                  
	            }
	    	    
	    	    String emailTo = "deepthi.rao@wipro.com";
				String emailSubject = "AGS: "+serialNumber+":"+transactionTime+":"+eventId+": Error in Creating SMS Content";
				String emailBody = " PIN: "+serialNumber+";Time & Date: " +transactionTime+"; EventId: "+eventId+"; Error in Creating SMS Content; Exception: "+err;
				emailBody = emailBody.replaceAll(";", "\n");
				
				EmailTemplate emailObj = new EmailTemplate();
				emailObj.setTo(emailTo);
				emailObj.setSubject(emailSubject);
				emailObj.setBody(emailBody);
				emailObj.setSerialNumber(serialNumber);
				emailObj.setTransactionTime(transactionTime);
				//new EmailHandler().handleEmail("jms/queue/emailQ", emailObj,0);
				//DF20171016 - KO369761 : Changed EmailQ Service from Hornet to Kafka Queue.
				new EmailHandler().handleEmailInKafka("EmailQueue", emailObj,0);
				
				e.printStackTrace();
			}
		}	
		
		catch(Exception e)
		{
			fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+eventId+":Exception :"+e);
			Writer result = new StringWriter();
    	    PrintWriter printWriter = new PrintWriter(result);
    	    e.printStackTrace(printWriter);
    	    String err = result.toString();
    	    fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+eventId+":Exception trace: "+err);
    	    try 
    	    {
    	    	printWriter.close();
        	    result.close();
			} 
    	    
    	    catch (IOException e1) 
    	    {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	    
    	    String emailTo = "deepthi.rao@wipro.com";
			String emailSubject = "AGS: "+serialNumber+":"+transactionTime+":"+eventId+":Error in Creating SMS Content";
			String emailBody = " PIN: "+serialNumber+";Time & Date: " +transactionTime+"; EventId: "+eventId+"; Error in Creating SMS Content; Exception: "+err;
			emailBody = emailBody.replaceAll(";", "\n");
			
			EmailTemplate emailObj = new EmailTemplate();
			emailObj.setTo(emailTo);
			emailObj.setSubject(emailSubject);
			emailObj.setBody(emailBody);
			emailObj.setSerialNumber(serialNumber);
			emailObj.setTransactionTime(transactionTime);
			//new EmailHandler().handleEmail("jms/queue/emailQ", emailObj,0);
			//DF20171016 - KO369761 : Changed EmailQ Service from Hornet to Kafka Queue.
			new EmailHandler().handleEmailInKafka("EmailQueue", emailObj,0);
    	    
			e.printStackTrace();
		}
		
		iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventId+" SMS Handler - END");
		
		return status;
	}
	
	
	//******************************************************* SMS Handler - New *************************************************************
	
	//******************************************************* Email Handler - New *************************************************************
	public String emailHandlerNew(List<String> emailUsers, String serialNumber, String eventSeverity, String transactionTime, int eventId, 
			String eventName, String value, String engineNumber, String latValue, String longValue, String currentGPSfix)
	{
		String status="SUCCESS";
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
    	iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventId+":Email Handler - START");
		try
		{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			try
			{
				Properties prop=null;
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventId+":Read from property file - START");
				try
				{
					prop= StaticProperties.getConfProperty();
				}
				catch(Exception e)
				{
					fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+eventId+":Error in intializing property File :"+e);
					return "FAILURE";
				}
				
				String Email_NotificationName = prop.getProperty("Email_NotificationName");
				String Email_Severity = prop.getProperty("Email_Severity");
				String Email_PIN = prop.getProperty("Email_PIN");
				String Email_EngineNumber = prop.getProperty("Email_EngineNumber");
				String Email_Value = prop.getProperty("Email_Value");
				String Email_SnapshotTime = prop.getProperty("Email_SnapshotTime");
				String LandmarkName = prop.getProperty("LandmarkName");
				String Email_Location = prop.getProperty("Email_Location");
				String Email_CustomerName = prop.getProperty("Email_CustomerName");
				String Email_Description = prop.getProperty("Email_Description");
				String Email_DealerphoneNumber = prop.getProperty("Email_DealerphoneNumber");
				String Email_DealerName = prop.getProperty("Email_DealerName");
				String Alert_Description = prop.getProperty("Alert_Description");
				String Email_MachineProfile = prop.getProperty("Email_MachineProfile");
				String TowAwayEventName=prop.getProperty("TowAwayEventName");
				String TowAwayRemovalStr = prop.getProperty("TowAwayRemovalStr");
				
				ListToStringConversion conversion = new ListToStringConversion();
				String toList = conversion.getCommaSeperatedStringList(emailUsers).toString();
				this.emailTo = toList;
				
				//------------------- Get the Customer and Dealer Details
				String dealerName="";
				String dealerContactNum="";
				String customerName="";
				Query ownerContactQ = session.createQuery(" from AssetOwnerSnapshotEntity where serialNumber='"+serialNumber+"' order by assetOwnershipDate");
	        	Iterator ownerContactItr = ownerContactQ.list().iterator();
	        	AssetOwnerSnapshotEntity assetOwnerSnap=null;
	        	while(ownerContactItr.hasNext())
	        	{
	        		assetOwnerSnap = (AssetOwnerSnapshotEntity)ownerContactItr.next();
	        		if(assetOwnerSnap.getAccountType().equalsIgnoreCase("Dealer"))
	        		{
	        			dealerName=assetOwnerSnap.getAccountId().getAccount_name();
	        			dealerContactNum=assetOwnerSnap.getAccountId().getMobile_no();
	        		}
	        		
	        	}
	        	customerName=assetOwnerSnap.getAccountId().getAccount_name();
	        	
	        	//------------------- Decide Email body based on GPS Fix for Tow away alert
				if( (eventName.contains(TowAwayEventName)) && (currentGPSfix.equalsIgnoreCase("1")) )
					eventName = eventName.replace(TowAwayRemovalStr, "");
			    
				
				//--------------------- Get the Machine Profile
        		String machineProfile="";
        		Query assetQ = session.createQuery(" from AssetEntity where serial_number='"+serialNumber+"'");
	        	Iterator assetItr = assetQ.list().iterator();
	        	if(assetItr.hasNext())
	        	{
	        		AssetEntity asset = (AssetEntity) assetItr.next();
	        		
	        		if(asset.getProductId()!=null && asset.getProductId().getAssetGroupId()!=null)
	        			machineProfile=asset.getProductId().getAssetGroupId().getAsset_group_name();
	        	}
	        	
	        	//--------------------- Get the Email Template for the received Event
	        	Query emailTempQ = session.createQuery("from EmailTemplateEntity where eventId='"+eventId+"'");
				Iterator emailTempItr = emailTempQ.list().iterator();
				EmailTemplateEntity emailTemp=null;
				while(emailTempItr.hasNext())
				{
					emailTemp = (EmailTemplateEntity)emailTempItr.next();
				}
				
				if(emailTemp==null)
					return "FAILURE";
					
				if(value==null)
					value="";
				
				if(eventName==null)
					eventName="";
				
				if(eventSeverity==null)
					eventSeverity="";
				
				if(engineNumber==null)
					engineNumber="";
				
				if(latValue==null)
					latValue="";
				
				if(longValue==null)
					longValue="";
				
				//------------------ Structure the Message body of the Email
				Date date=null;
		        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		        datetimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
				date = datetimeFormat.parse(transactionTime);
				SimpleDateFormat datetimeFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
				datetimeFormat1.setTimeZone(TimeZone.getTimeZone("IST"));
				String ISTTransactionTime = datetimeFormat1.format(date);
				
				Pattern MY_PATTERN = Pattern.compile("\\<(.*?)\\>");
				Matcher m = MY_PATTERN.matcher(emailTemp.getEmailBody());
				StringBuffer sb = new StringBuffer();
				while (m.find()) 
				{
				  	String s1 = "<"+m.group(1)+">";
				    if(m.group(1).equalsIgnoreCase(LandmarkName))
				    	m.appendReplacement(sb,value);
				    if(m.group(1).equalsIgnoreCase(Email_CustomerName))
				    	m.appendReplacement(sb, customerName);
				    if(m.group(1).equalsIgnoreCase(Email_Description))
				    	m.appendReplacement(sb, eventName);
				    if(m.group(1).equalsIgnoreCase(Email_MachineProfile))
				    	m.appendReplacement(sb,machineProfile);
				  	if(m.group(1).equalsIgnoreCase(Email_DealerphoneNumber))
				    	m.appendReplacement(sb,dealerContactNum);
				  	if(m.group(1).equalsIgnoreCase(Email_DealerName))
				    	m.appendReplacement(sb,dealerName);
				  	if(m.group(1).equalsIgnoreCase(Email_NotificationName))
				    	m.appendReplacement(sb,eventName);
				    if(m.group(1).equalsIgnoreCase(Email_Severity))
				    	m.appendReplacement(sb,eventSeverity);
				    if(m.group(1).equalsIgnoreCase(Email_PIN))
				    	m.appendReplacement(sb,serialNumber);
				    if(m.group(1).equalsIgnoreCase(Email_Value))
				    	m.appendReplacement(sb,value);
				    if(m.group(1).equalsIgnoreCase(Email_SnapshotTime))
				    	m.appendReplacement(sb,ISTTransactionTime);
				    if(m.group(1).equalsIgnoreCase(Email_EngineNumber))
				    	m.appendReplacement(sb,engineNumber);
				    if(m.group(1).equalsIgnoreCase(Email_Location))
				    	m.appendReplacement(sb,latValue+","+longValue);
				    
				}
				m.appendTail(sb);
				String body = sb.toString().replaceAll(";", "\n");
				
				String eventLandmark = "<"+LandmarkName+">";
				if(body.contains(eventLandmark))
				{
					body = body.replaceAll(eventLandmark, value);
				}
				this.emailBody = body;
				
				
				//------------------ Structure the Email Subject
				Pattern MY_PATTERN_SUBJECT = Pattern.compile("\\<(.*?)\\>");
				Matcher m1 = MY_PATTERN_SUBJECT.matcher(emailTemp.getEmailSubject());
				StringBuffer sb1 = new StringBuffer();
				while (m1.find()) 
				{
				    String s1 = "<"+m1.group(1)+">";
				    if(m1.group(1).equalsIgnoreCase(Alert_Description))
				    	m1.appendReplacement(sb1,eventName);
				    if(m1.group(1).equalsIgnoreCase(Email_NotificationName))
				    	m1.appendReplacement(sb1,eventName);
				    if(m1.group(1).equalsIgnoreCase(Email_Severity))
				    	m1.appendReplacement(sb1,eventSeverity);
				    if(m1.group(1).equalsIgnoreCase(Email_PIN))
				    	m1.appendReplacement(sb1,serialNumber);
				    if(m1.group(1).equalsIgnoreCase(Email_EngineNumber))
				    	m1.appendReplacement(sb1,engineNumber);
				    if(m1.group(1).equalsIgnoreCase(LandmarkName))
				    	m1.appendReplacement(sb1,value);
				    
				}
				m1.appendTail(sb1);
				String sub = sb1.toString();
				if(sub.contains(eventLandmark))
				{
					sub = sub.replaceAll(eventLandmark, value);
				}
				
				this.emailSubject = sub;
				
				//----------------------- Place the data into email queue
				/*EmailTemplate emailObj = new EmailTemplate();
				emailObj.setTo(this.emailTo);
				emailObj.setSubject(this.emailSubject);
				emailObj.setBody(this.emailBody);
				emailObj.setSerialNumber(serialNumber);
				emailObj.setTransactionTime(transactionTime);
				
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventId+": ---- Email Notification Content ------");
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventId+": To: "+ this.emailTo);
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventId+": Subject: "+ this.emailSubject);
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventId+": Body: "+ this.emailBody);
				
				new EmailHandler().handleEmail("jms/queue/emailQ", emailObj,0);*/
				
				//Df20150317 - Rajani Nagaraju - Make to call to SMTP server directly on first call, if SMTP server is not available then put to Queue
				new SendEmail(this.emailTo, this.emailSubject, this.emailBody, serialNumber, transactionTime);
				
				if(! (session.isOpen() ))
	            {
	                        session = HibernateUtil.getSessionFactory().getCurrentSession();
	                        session.getTransaction().begin();
	            }
			}
			
			catch(Exception e)
			{
				fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+eventId+":Exception :"+e);
				Writer result = new StringWriter();
	    	    PrintWriter printWriter = new PrintWriter(result);
	    	    e.printStackTrace(printWriter);
	    	    String err = result.toString();
	    	    fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+eventId+":Exception trace: "+err);
	    	    try 
	    	    {
	    	    	printWriter.close();
	        	    result.close();
				} 
	    	    
	    	    catch (IOException e1) 
	    	    {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	    
	    	    finally
	            {
	            	if(session.isOpen())
	                  if(session.getTransaction().isActive())
	                  {
	                        session.getTransaction().commit();
	                  }
	                  
	                  if(session.isOpen())
	                  {
	                        session.flush();
	                        session.close();
	                  }
	                  
	            }
	    	    
	    	    String emailTo = "deepthi.rao@wipro.com";
				String emailSubject = "AGS: "+serialNumber+":"+transactionTime+":"+eventId+": Error in Creating Email Content";
				String emailBody = " PIN: "+serialNumber+";Time & Date: " +transactionTime+"; EventId: "+eventId+"; Error in Creating Email Content; Exception: "+err;
				emailBody = emailBody.replaceAll(";", "\n");
				
				EmailTemplate emailObj = new EmailTemplate();
				emailObj.setTo(emailTo);
				emailObj.setSubject(emailSubject);
				emailObj.setBody(emailBody);
				emailObj.setSerialNumber(serialNumber);
				emailObj.setTransactionTime(transactionTime);
				//new EmailHandler().handleEmail("jms/queue/emailQ", emailObj,0);
				//DF20171016 - KO369761 : Changed EmailQ Service from Hornet to Kafka Queue.
				new EmailHandler().handleEmailInKafka("EmailQueue", emailObj,0);
				
				e.printStackTrace();
			}
		}	
		
		catch(Exception e)
		{
			fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+eventId+":Exception :"+e);
			Writer result = new StringWriter();
    	    PrintWriter printWriter = new PrintWriter(result);
    	    e.printStackTrace(printWriter);
    	    String err = result.toString();
    	    fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+eventId+":Exception trace: "+err);
    	    try 
    	    {
    	    	printWriter.close();
        	    result.close();
			} 
    	    
    	    catch (IOException e1) 
    	    {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	    
    	    String emailTo = "deepthi.rao@wipro.com";
			String emailSubject = "AGS: "+serialNumber+":"+transactionTime+":"+eventId+":Error in Creating Email Content";
			String emailBody = " PIN: "+serialNumber+";Time & Date: " +transactionTime+"; EventId: "+eventId+"; Error in Creating Email Content; Exception: "+err;
			emailBody = emailBody.replaceAll(";", "\n");
			
			EmailTemplate emailObj = new EmailTemplate();
			emailObj.setTo(emailTo);
			emailObj.setSubject(emailSubject);
			emailObj.setBody(emailBody);
			emailObj.setSerialNumber(serialNumber);
			emailObj.setTransactionTime(transactionTime);
			//new EmailHandler().handleEmail("jms/queue/emailQ", emailObj,0);
			//DF20171016 - KO369761 : Changed EmailQ Service from Hornet to Kafka Queue.
			new EmailHandler().handleEmailInKafka("EmailQueue", emailObj,0);
    	    
			e.printStackTrace();
		}
		
		iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventId+" Email Handler - END");
		
		return status;
	}
	//******************************************************* SMS Handler - New *************************************************************
	public static String UniConvertHindi(String str) {
        byte[] b = str.getBytes();
        String actualCharacter = null;
        try {
                        actualCharacter = new String(b, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
                        //   Logger.getLogger(HindiUnicode.class.getName()).log(Level.SEVERE, null, ex);
                        ex.printStackTrace();
        }
        System.out.println("actualCharacter:"+actualCharacter);
        return actualCharacter;
}
}
