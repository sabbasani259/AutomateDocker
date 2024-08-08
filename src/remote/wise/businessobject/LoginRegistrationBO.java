/*
 * JCB6622 : 20240805 : Dhiraj Kumar : Email Flooding security fix
 */
package remote.wise.businessobject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import remote.wise.businessentity.ClientEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.businessentity.ContactLoginInfoEntity;
import remote.wise.businessentity.SecretQuestionEntity;
import remote.wise.exception.CustomFault;
import remote.wise.handler.EmailHandler;
import remote.wise.handler.EmailTemplate;
import remote.wise.handler.SmsHandler;
import remote.wise.handler.SmsTemplate;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.ForgotLoginIDReqContract;
import remote.wise.service.implementation.ForgotLoginIDImpl;
import remote.wise.service.implementation.ForgotPasswordImpl;
import remote.wise.service.implementation.LoginRegistrationImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;

public class LoginRegistrationBO {

	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("LoginRegistrationBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("LoginRegistrationBO:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("LoginRegistrationBO:","info");*/

	/** 
	 * method to get security questions
	 * @return List<LoginRegistrationImpl> 
	 */

	public List<LoginRegistrationImpl> getSecretQs(){
		List<LoginRegistrationImpl> questionsList = null;

		Logger iLogger = InfoLoggerClass.logger;

		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();  
		try
		{       
			String basicQuery = null;        
			basicQuery ="SELECT question_Id,question FROM SecretQuestionEntity";   
			iLogger.info("Query : "+basicQuery);
			Query query = session.createQuery(basicQuery);
			Iterator itr = query.list().iterator();
			Object[] result=null; 
			questionsList=new ArrayList<LoginRegistrationImpl>();
			LoginRegistrationImpl dataObj = null;
			while(itr.hasNext())
			{
				result = (Object[]) itr.next();
				dataObj = new LoginRegistrationImpl();

				if(result[0]!=null){
					dataObj.setQID((Integer)result[0]);
				}
				if(result[1]!=null){
					dataObj.setQuestion((String)result[1]);
				}          
				questionsList.add(dataObj);
			}
		}
		finally{
			if(session.getTransaction().isActive()){      
				session.getTransaction().commit();
			}
			if(session.isOpen()){ 
				session.flush();
				session.close();
			} 
		}
		long endTime=System.currentTimeMillis();
		iLogger.info("Total execution time "+ String.valueOf(endTime-startTime)+ "(ms)");
		return questionsList;
	}
	/**
	 * 
	 * @param loginId
	 * @param questionId1
	 * @param answer1
	 * @param questionId2
	 * @param answer2
	 * @param DOB
	 * @param nativeState
	 * @throws CustomFault
	 * @return String
	 */
	public String setSecretQuestions(String loginId, int questionId1, String answer1,int questionId2, String answer2,
			String DOB, String nativeState) throws CustomFault{
		Logger iLogger = InfoLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		// Defect Id 1179 by Manish on 02-09-2013
		//ME100010860-Sai Divya-20240419-for intermittent Security questions issue changed currentSession to openSession
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction(); 
		try
		{
			//get Client Details
			Properties prop = new Properties();
			String clientName=null;

			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");

			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
			//END of get Client Details	
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().openSession();
				session.getTransaction().begin();
			}

			//			insert into contact_login_info with secret q's and answers
			ContactLoginInfoEntity login=null;
			login = new ContactLoginInfoEntity(); 
			ContactEntity ce = new ContactEntity();
			ce.setContact_id(loginId);
			login.setContactId(ce);
			SecretQuestionEntity sqe=new SecretQuestionEntity();
			sqe.setQuestion_Id(questionId1);		
			login.setQuestionId(sqe);
			login.setAnswer(answer1);			
			login.save();	 

			login = new ContactLoginInfoEntity(); 
			login.setContactId(ce);
			sqe.setQuestion_Id(questionId2);
			login.setQuestionId(sqe);
			login.setAnswer(answer2);			
			login.save();
			if(! (session.isOpen() )){
				session = HibernateUtil.getSessionFactory().openSession();
				session.beginTransaction();  
			}		

			String queryString = "from ContactEntity where contact_id='"+ loginId+"' and active_status=true";
			Iterator itr=session.createQuery(queryString).list().iterator();

			while(itr.hasNext())
			{
				ContactEntity contact = (ContactEntity) itr.next();
				contact.setDob(DOB);
				contact.setNativeState(nativeState);
				session.update(contact);
			} 
		}
		catch(Exception e){
			bLogger.error("Questions and answers already exixt for the user "+loginId);
			throw new CustomFault("Questions and answers already exixt for this user !!");
		}
		finally{
			if(session != null){
				if(session.getTransaction().isActive()){    
					session.getTransaction().commit();
				}

				if(session.isOpen()){ 
					session.flush();
					session.close();
				} 
			}

		}
		long endTime=System.currentTimeMillis();
		iLogger.info("Total execution time "+ String.valueOf(endTime-startTime)+ "(ms)");
		return "SUCCESS";
	}
	/**
	 * method to get the secret questions set by user
	 * @param contactID
	 * @return List<LoginRegistrationImpl>
	 */

	public List<LoginRegistrationImpl> getSecretQsForUser(String contactID){
		Logger iLogger = InfoLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		List<LoginRegistrationImpl> questionsList = new ArrayList<LoginRegistrationImpl>();
		LoginRegistrationImpl impl=null;
		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();  
		try
		{        
			String basicQuery = null;      

			basicQuery ="select t2.question_Id,t2.question  from ContactLoginInfoEntity t1,"+
					"SecretQuestionEntity t2 where t1.questionId=t2.question_Id and t1.contactId ='"+contactID+"'";
			iLogger.info("Query : "+basicQuery);

			//iterate over result
			Query query = session.createQuery(basicQuery);
			Iterator itr = query.list().iterator();
			Object[] result=null; boolean flag=false;
			while(itr.hasNext()){
				flag =true;
				result = (Object[]) itr.next();
				impl = new LoginRegistrationImpl();
				if(result[0]!=null){
					impl.setQID((Integer)result[0]);
				}
				if(result[1]!=null){
					impl.setQuestion((String)result[1]);
				}

				questionsList.add(impl);

			}
			if(!flag){
				impl = new LoginRegistrationImpl();
				bLogger.error("Login Id does not exist.");
				impl.setMessage("Login Id does not exist.");
			}
			else{
				impl.setMessage("SUCCESS");
			}
		}
		finally{
			if(session.getTransaction().isActive()){      
				session.getTransaction().commit();
			}
			if(session.isOpen()){ 
				session.flush();
				session.close();
			} 
		}
		long endTime=System.currentTimeMillis();
		iLogger.info("Total execution time "+ String.valueOf(endTime-startTime)+ "(ms)");
		return questionsList;

	}

	/**
	 * method to get the forgotten password for the user
	 * @param loginId
	 * @throws CustomFault
	 * @return ForgotPasswordImpl
	 */
	public  ForgotPasswordImpl getForgottenPassword(String loginId) throws CustomFault{
		Logger iLogger = InfoLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().openSession();
	//	session.beginTransaction();  

		ForgotPasswordImpl userDetails = new ForgotPasswordImpl();	;
		//	        generate a new password to the user

		String validChars = "abcdefghijklmnopqrstuvwxyz";
		String validCapital="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String password = "";
		//DF20140806 - Rajani Nagaraju - Aviod trimming of password in SMS
		String validSpecial="*#@!";
		String numeric="1234567890";
		Random generator = new Random();
		for (int i=0; i<7; i++) {
			password+=validChars.charAt(generator.nextInt(validChars.length()));
		}
		password+=validSpecial.charAt(generator.nextInt(validSpecial.length()));

		password+=numeric.charAt(generator.nextInt(numeric.length()));
		password+=validCapital.charAt(generator.nextInt(validCapital.length()));
		for (int i=0; i<2; i++) {
			password+=validChars.charAt(generator.nextInt(validChars.length()));
		}
		
		//DF20170113 - Supriya -  password simplification changes
		/*String validSpecial="@_";
		String numeric="1234567890";
		Random generator = new Random();
		password += loginId.substring(0, 4);
		for (int i=0; i<7; i++) {
			password+=validChars.charAt(generator.nextInt(validChars.length()));
		}

		password+=validSpecial.charAt(generator.nextInt(validSpecial.length()));

		for(int i=0;i<4;i++){
			password+=numeric.charAt(generator.nextInt(numeric.length()));
		}*/
		
		

		try{



			String queryString = "from ContactEntity where contact_id='"+ loginId+"' and active_status=true";
			iLogger.info("Query : "+queryString);     
			Iterator itr=session.createQuery(queryString).list().iterator();
			boolean flag=false;
			String primaryEmailId = null,primaryMobileNumber=null, contryCode = null;
			int forgotPassCount = 0;//JCB6622.n
			while(itr.hasNext()){
				flag=true;
				ContactEntity contact = (ContactEntity) itr.next();
				primaryEmailId = contact.getPrimary_email_id();
				primaryMobileNumber = contact.getPrimary_mobile_number();
				contryCode = contact.getCountryCode();
				userDetails.setPrimaryEmailID(primaryEmailId);
				forgotPassCount = contact.getResetPassCount();//JCB6622.n
				forgotPassCount++;//JCB6622.n

				//Df20170921 @Roopa  AES password encryption changes

				try{
				    	//String updatQuery="update contact set  password=AES_ENCRYPT('"+password+"',primary_moblie_number),sys_gen_password=1 where contact_id='"+loginId+"'";//JCB6622.o
				    	//JCB6622.n
					String updatQuery="update contact set  password=AES_ENCRYPT('"+password+"',primary_moblie_number),"
						+ " sys_gen_password=1"
						+ " reset_pass_count=" + forgotPassCount
						+ " where contact_id='"+loginId+"'";

					String result=new CommonUtil().insertData(updatQuery);

					iLogger.info("Forgot Password Service:: update encrypted password into contact table status::"+result);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				//Df20170921 @Roopa AES password encryption changes END
				
				
				if(primaryEmailId!=null && !primaryEmailId.equals("")){
					sendMailWithPassword(primaryEmailId,password);
				}	
				if(primaryMobileNumber!=null && !primaryMobileNumber.equals("")){
					sendSMSWithLoginIDOrPassword(primaryMobileNumber,contryCode,null,password);
				}
				
				
				
				//contact.setPassword(password);
				//contact.setSysGeneratedPassword(1);
				//session.update(contact);
			} 	      

			if(!flag){
				userDetails.setMessage("Sorry ! Login id does not exists");
				bLogger.error("Login id does not exists");
				// Defect Id 1512 by Manish on 14-11-2013 Starts
				//throw new CustomFault("Login id does not exists !");
			}
			else{
				userDetails.setMessage("SUCCESS");
				userDetails.setPassword(password);
			}
			// Defect Id 1512 by Manish on 14-11-2013 Ends
		}
		finally{
			/*if(session.getTransaction().isActive()){      
				session.getTransaction().commit();
			}*/
			if(session.isOpen()){ 
				//session.flush();
				session.close();
			} 
		}
		long endTime=System.currentTimeMillis();
		iLogger.info("Total execution time "+ String.valueOf(endTime-startTime)+ "(ms)");

		return userDetails;
	}
	/**
	 * method to authenticate email id or mobile no.
	 * @param request
	 * @return
	 * @throws CustomFault
	 */
	public String authenicateLoginIDOrMobileNo(ForgotLoginIDReqContract request)throws CustomFault{
		Logger iLogger = InfoLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();  
		String message =null;
		try{			
			String queryString = null;
			if(request.getEmailID()!=null){
				if(!request.getEmailID().equals("")){
					queryString = "from ContactEntity where primary_email_id='"+ request.getEmailID()+"' and active_status=true";
				}
			}
			else
				if(request.getMobileNumber()!=null){
					if(!request.getMobileNumber().equals("")){
						queryString = "from ContactEntity where primary_mobile_number='"+ request.getMobileNumber()+"' and active_status=true";
					}
				}
			iLogger.info("Query : "+queryString);     
			int rows=session.createQuery(queryString).list().size();
			if(rows<=0){	
				message ="Sorry ! Either email id or mobile no does not exists";
				bLogger.error("Sorry ! Either email id or mobile no does not exists");
				throw new CustomFault("Sorry ! Either email id or mobile no does not exists!");	        	
			}
			else{
				message ="Email id or mobile no. exists";
			}
		}
		finally{
			if(session.getTransaction().isActive()){      
				session.getTransaction().commit();
			}
			if(session.isOpen()){ 
				session.flush();
				session.close();
			} 
		}
		long endTime=System.currentTimeMillis();
		iLogger.info("Total execution time "+ String.valueOf(endTime-startTime)+ "(ms)");
		return message;


	}
	/**
	 * method to get forgotten login id
	 * @param request
	 * @throws CustomFault 
	 * @return ForgotLoginIDImpl
	 */

	public ForgotLoginIDImpl getForgottenLoginId(ForgotLoginIDReqContract request)throws CustomFault{
		ForgotLoginIDImpl impl= new ForgotLoginIDImpl();
		Logger iLogger = InfoLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		String basicQuery = null;String mailOrSMS=null;
		if(request.getEmailID()!= null && !request.getEmailID().equals("") ){
			mailOrSMS="MAIL";
			basicQuery ="SELECT  t1.questionId, t1.answer, t2.contact_id,t2.primary_email_id,t2.countryCode FROM ContactLoginInfoEntity t1, ContactEntity t2 WHERE t2.primary_email_id ='"+request.getEmailID()+"' and t2.contact_id = t1.contactId";
		}
		else
			if(request.getMobileNumber()!= null && !request.getMobileNumber().equals("")){
				mailOrSMS="SMS";
				basicQuery ="SELECT  t1.questionId, t1.answer,t2.contact_id,t2.primary_email_id,t2.countryCode FROM ContactLoginInfoEntity t1, ContactEntity t2 WHERE t2.primary_mobile_number ='"+request.getMobileNumber()+"' and t2.contact_id = t1.contactId";
			}      
		iLogger.info("Query : "+basicQuery);
		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();  

		try{
			//iterate over result
			Query query = session.createQuery(basicQuery);
			Iterator itr = query.list().iterator();
			Object[] result=null; 
			String loginID=null;String primaryEmailID=null,countryCode = null;
			Map<Integer, String> dbValues = new java.util.HashMap<Integer, String>();

			boolean flag=false;
			SecretQuestionEntity sqe=null;
			while(itr.hasNext())
			{
				flag=true;
				result = (Object[]) itr.next();
				if(result[0]!=null){
					sqe=(SecretQuestionEntity)(result[0]);
				}
				int id=sqe.getQuestion_Id();

				dbValues.put(id, (String)(result[1]));
				if(result[2]!=null){
					loginID=(String)(result[2]);
				}
				if(result[3]!=null){
					primaryEmailID=(String)(result[3]); 
				}         
				if(result[4]!=null){
					countryCode=(String)(result[4]); 
				} 
			}
			if(session.getTransaction().isActive()){      
				session.getTransaction().commit();
			}
			if(session.isOpen()){ 
				session.flush();
				session.close();
			} 
			if(!flag){
				impl.setMessage("Either email id or mobile number does not exist");
				bLogger.error("Either email id or mobile number does not exist");
				throw new CustomFault("Either email id or mobile number does not exist");
			}
			else{            	
				if(dbValues.containsKey(request.getQuestionId1()) && request.getAnswer1().equals(dbValues.get(request.getQuestionId1())) &&
						dbValues.containsKey(request.getQuestionId2()) && request.getAnswer2().equals(dbValues.get(request.getQuestionId2()))){
					//            	qIds and answers are matching. set login id and message as success
					impl.setLoginId(loginID);
					impl.setPrimaryEmailId(primaryEmailID);

					if(mailOrSMS!=null){
						if(mailOrSMS.equals("MAIL")){
							iLogger.info("Trying to send mail with details !!");
							impl.setMessage(sendMailWithLoginID(primaryEmailID,loginID)); // Defect Id 1848 by Keerthi
						}
						else if(mailOrSMS.equals("SMS")){
							iLogger.info("Trying to send SMS with details !!");
							impl.setMessage(sendSMSWithLoginIDOrPassword(request.getMobileNumber(),countryCode,loginID,null)); // Defect Id 1848 by Keerthi
						}
					}

				}
				else{
					impl.setMessage("Questions and answers are not matching.");
					bLogger.error("Questions and answers are not matching.");
					throw new CustomFault("Questions and answers are not matching.");
				}
			}
		}
		catch(CustomFault c){
			throw c;
		}	

		long endTime=System.currentTimeMillis();
		iLogger.info("Total execution time "+ String.valueOf(endTime-startTime)+ "(ms)");     

		return impl;
	}

	/**
	 * method to reset the password for the user
	 * @param loginId
	 * @param newPassword
	 * @throws CustomFault
	 * @return String
	 */
	public String resetPassword(String loginId, String newPassword) throws CustomFault{
		String message=null;
		Logger iLogger = InfoLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().openSession();
		//session.beginTransaction();	
		
		
		
		try{
			String queryString = "from ContactEntity where contact_id= '"+ loginId+"' and active_status=true";
			Iterator itr=session.createQuery(queryString).list().iterator();
			boolean flag=false;String primaryMobileNumber = null,primaryEmailID=null,countryCode=null;
			while(itr.hasNext()){
				flag=true;
				ContactEntity contact = (ContactEntity) itr.next();
				primaryEmailID = contact.getPrimary_email_id();
				primaryMobileNumber = contact.getPrimary_mobile_number();
				countryCode = contact.getCountryCode();
				
				//Df20170921 @Roopa AES password encryption changes
				
				try{
					String updatQuery="update contact set  password=AES_ENCRYPT('"+newPassword+"',primary_moblie_number),sys_gen_password=0 where contact_id='"+loginId+"'";

					String result=new CommonUtil().insertData(updatQuery);

					iLogger.info("Reset Password Service:: update encrypted password into contact table status::"+result);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				//Df20170921 @Roopa AES password encryption changes END
				
				//contact.setPassword(newPassword);	
				//contact.setSysGeneratedPassword(0);
				//session.update(contact);
				if(primaryEmailID!=null && !primaryEmailID.equals("")){
					sendMailWithPassword(primaryEmailID, newPassword);
				}
				if(primaryMobileNumber!=null && !primaryMobileNumber.equals("")){
					sendSMSWithLoginIDOrPassword(primaryMobileNumber,countryCode,null,newPassword);
				}

			} 
			if(flag){
				message="Successfully reset password";
			}
			else{
				message="Login ID does not exist!. Password is not reset.";
				bLogger.error("Login ID does not exist!.");
				throw new CustomFault("Login ID does not exist!.");
			}
		}
		finally{
			/*if(session.getTransaction().isActive()){      
				session.getTransaction().commit();
			}*/
			if(session.isOpen()){ 
				//session.flush();
				session.close();
			} 
		}			

		long endTime=System.currentTimeMillis();
		iLogger.info("Total execution time "+ String.valueOf(endTime-startTime)+ "(ms)");
		return message;

	}
	/**
	 * method to send e-mail to user with forgotten password
	 * @param primaryEmailId
	 * @param LoginId
	 * @param password
	 * @return String
	 */
	public String sendMailWithPassword(String primaryEmailId, String password){
		String message =null;
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		try{	
			//get LiveLink URL Details
			Properties prop = new Properties();
			String liveLink_URL=null;

			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			//			liveLink_URL= prop.getProperty("LiveLink_URL");
			if (prop.getProperty("deployenvironment").equalsIgnoreCase("SIT")) {
				liveLink_URL= prop.getProperty("LiveLink_URL_SIT");
			} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("DEV")) {
				liveLink_URL= prop.getProperty("LiveLink_URL_DEV");
			} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("QA")) {
				liveLink_URL= prop.getProperty("LiveLink_URL_QA");
			} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("PROD")) {
				liveLink_URL= prop.getProperty("LiveLink_URL_PROD");
			} else {
				liveLink_URL= prop.getProperty("LiveLink_URL");
			}


			String subject = "Your new password for JCB LiveLink";
			StringBuffer body = new StringBuffer();
			body.append("Hello,\n");
			//			Keerthi : 17/12/2013 : Defect ID : 1831 : email content changed for forgot password
			body.append("Please find the new password below :\n\n");			
			body.append("Password  : "+password + "\n");
			//			Keerthi : 17/12/2013 : ID : 1833 : URL is taken from properties file
			//			Keerthi : 19/12/2013 : ID : 1847 : application spelling mistake
			body.append("Please use this password when you log in to LiveLink application next time.\n");
			body.append("Application URL : "+liveLink_URL+" \n\n\n");
			body.append("With regards, \n");
			body.append("JCB LiveLink Team.");

			EmailTemplate emailTemplate = new EmailTemplate(primaryEmailId,subject,body.toString(),null);
			//new EmailHandler().handleEmail("jms/queue/emailQ", emailTemplate,0);
			//DF20171016 - KO369761 : Changed SMSQ Service from Hornet to Kafka Queue.
			new EmailHandler().handleEmailInKafka("EmailQueue", emailTemplate,0);
			message ="Email with password details has been sent to user !";
			iLogger.info("Email with password details has been sent to user !");
		}
		catch (Exception e) {
			message ="Failed while sending email to user !!";
			fLogger.error("Failed while sending email to user !!" + e.getMessage());

		}
		return message;
	}
	
	
	
	/**
	 * method to send e-mail to user with forgotten password
	 * @param primaryEmailId
	 * @param OTP
	 * @return String
	 */
	//CR469.sn
	public String sendMailWithOTP(String primaryEmailId, String otp){
		String message =null;
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		try{	
			//get LiveLink URL Details
			Properties prop = new Properties();
			String liveLink_URL=null;

			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			if (prop.getProperty("deployenvironment").equalsIgnoreCase("SIT")) {
				liveLink_URL= prop.getProperty("LiveLink_URL_SIT");
			} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("DEV")) {
				liveLink_URL= prop.getProperty("LiveLink_URL_DEV");
			} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("QA")) {
				liveLink_URL= prop.getProperty("LiveLink_URL_QA");
			} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("PROD")) {
				liveLink_URL= prop.getProperty("LiveLink_URL_PROD");
			} else {
				liveLink_URL= prop.getProperty("LiveLink_URL");
			}


			String subject = "Your otp for New UserRegistration";
			StringBuffer body = new StringBuffer();
			body.append("Hello,\n");
			body.append("Please find the otp below :\n\n");			
			body.append("OTP  : "+otp + "\n");
			
			
			body.append("With regards, \n");
			body.append("JCB LiveLink Team.");

			EmailTemplate emailTemplate = new EmailTemplate(primaryEmailId,subject,body.toString(),null);
			new EmailHandler().handleEmailInKafka("EmailQueue", emailTemplate,0);
			message ="Email with otp has been sent to user !";
			iLogger.info("Email with otp has been sent to user !");
		}
		catch (Exception e) {
			message ="Failed while sending email to user !!";
			fLogger.error("Failed while sending email to user !!" + e.getMessage());

		}
		return message;
	}//CR469.en

	    // Determine LiveLink URL based on deployment environment
	    private String determineLiveLinkURL(Properties prop) {
	        String deployEnvironment = prop.getProperty("deployenvironment", "DEFAULT");
	        switch (deployEnvironment.toUpperCase()) {
	            case "SIT":
	                return prop.getProperty("LiveLink_URL_SIT", prop.getProperty("LiveLink_URL"));
	            case "DEV":
	                return prop.getProperty("LiveLink_URL_DEV", prop.getProperty("LiveLink_URL"));
	            case "QA":
	                return prop.getProperty("LiveLink_URL_QA", prop.getProperty("LiveLink_URL"));
	            case "PROD":
	                return prop.getProperty("LiveLink_URL_PROD", prop.getProperty("LiveLink_URL"));
	            default:
	                return prop.getProperty("LiveLink_URL", "DEFAULT_URL");
	        }
	    }

	  
	
	
	
	/**
	 * method to send SMS to mobile 
	 * @return otp
	 */
	    //CR469.sn
	public String sendSMStoMobile(String mobileNumber,String otp) throws CustomFault{
		String message =null,result=null;
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		List<String> toList = new ArrayList<String>();
		
		if(mobileNumber!=null)
		{
			toList.add(mobileNumber);
		}
		
		String body = null;
		if(otp!=null){
			body = "OTP for the registered Mobile Number with JCB LiveLink is "+otp;	
			result = "otp successfully sent to registered mobile number."+mobileNumber;
		}
		body = body + " - JCB LiveLink Team.";
		List<String> msgBody = new ArrayList<String>();
		msgBody.add(body);

		SmsTemplate smsTemplate = new SmsTemplate();			
		smsTemplate.setTo(toList);
		smsTemplate.setMsgBody(msgBody);
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try{
			String ResetService="Registration SMS";
			boolean isRegStatus = false;
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			} 
			Query query = session.createQuery("select a.isStatus from ConfigAppEntity a where a.services='"+ResetService+"'");
			Iterator itr1=query.list().iterator();
			while(itr1.hasNext()){
				isRegStatus = (Boolean) itr1.next();				
			}
			iLogger.info ("registration status from DB : "+ isRegStatus);
			if(isRegStatus==true)
			{
				iLogger.info ("sending SMS : START");
				new SmsHandler().handleSmsInKafka("SMSQueue", smsTemplate,0);
				message = result; // Defect Id 1848 by Keerthi
				iLogger.info(mobileNumber+":SMS with otp details has been sent to user !");
			}
			else 
			{
				bLogger.error(mobileNumber+":SMS with mobilenumber details cannot be sent : SMS Service turned OFF");
				message ="SMS with mobilenumber details cannot be sent : SMS Service turned OFF";
			}
		}
		catch (Exception e) {
			message ="Failed while sending SMS to user !!";
			fLogger.error("Failed while sending SMS to user !!" + e.getMessage());	
			throw new remote.wise.exception.CustomFault("Problem while sending SMS. Please try again after some time."); // Defect Id 1848 by Keerthi
		}
		return message;
	}//CR469.en
	
	
	

	/**
	 * method to send e-mail to user with forgotten login id
	 * @param primaryEmailId
	 * @param LoginId
	 * @param password
	 * @return String
	 */
	public String sendMailWithLoginID(String primaryEmailId,String LoginId)  throws CustomFault{
		String message =null;

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		try{
			//get LiveLink URL Details
			Properties prop = new Properties();
			String liveLink_URL=null;

			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));

			if (prop.getProperty("deployenvironment").equalsIgnoreCase("SIT")) {
				liveLink_URL= prop.getProperty("LiveLink_URL_SIT");
			} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("DEV")) {
				liveLink_URL= prop.getProperty("LiveLink_URL_DEV");
			} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("QA")) {
				liveLink_URL= prop.getProperty("LiveLink_URL_QA");
			} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("PROD")) {
				liveLink_URL= prop.getProperty("LiveLink_URL_PROD");
			} else {
				liveLink_URL= prop.getProperty("LiveLink_URL");
			}


			//			liveLink_URL= prop.getProperty("LiveLink_URL");


			String subject = "Your login details registered wtih JCB LiveLink";
			StringBuffer body = new StringBuffer();
			body.append("Hello "+LoginId+",\n");
			body.append("Please find your user name registered with JCB LiveLink :\n\n");
			body.append("User Name : "+LoginId + "\n");
			body.append("Please use this login ID when you log in to LiveLink application next time.\n");
			//			Keerthi : 17/12/2013 : ID : 1833 : URL is taken from properties file
			body.append("Application URL : "+liveLink_URL+" \n\n\n");
			body.append("With regards, \n");
			body.append("JCB LiveLink Team.");

			EmailTemplate emailTemplate = new EmailTemplate(primaryEmailId,subject,body.toString(),null);

			//new EmailHandler().handleEmail("jms/queue/emailQ", emailTemplate,0);
			//DF20171016 - KO369761 : Changed SMSQ Service from Hornet to Kafka Queue.
			new EmailHandler().handleEmailInKafka("EmailQueue", emailTemplate,0);
			message ="Login ID successfully sent to registered Email Id.";   // Defect Id 1848 by Keerthi
			iLogger.info("Email with login id details has been sent to user !");
		}		 

		catch (Exception e) {
			message ="Failed while sending email to user !!";
			fLogger.error("Failed while sending email to user !!" + e.getMessage());
			throw new remote.wise.exception.CustomFault("Problem while sending mail. Please try again after some time."); // Defect Id 1848 by Keerthi
		}
		return message;
	}
	/**
	 * method to send SMS to user with login id or password
	 * @param primaryEmailId
	 * @param LoginId
	 * @param password
	 * @return String
	 */
	public String sendSMSWithLoginIDOrPassword(String mobileNumber,String country,String loginId,String password) throws CustomFault{
		String message =null,result=null;
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		List<String> toList = new ArrayList<String>();

		if(country !=null){
			country = new CommonUtil().getCountryCode(country);
		}	
		else{
			country = "";
		}
		toList.add(country+mobileNumber);

		String body = null;
		if(loginId!=null){
			body = "Your login id registered with JCB LiveLink is "+loginId;	
			result = "Login ID successfully sent to registered mobile number.";
		}
		else if(password!=null){
			body = "Your new password registered with JCB LiveLink is "+password;	
			result = "Password successfully sent to registered mobile number.";
		}
		body = body + " . JCB LiveLink Team.";
		List<String> msgBody = new ArrayList<String>();
		msgBody.add(body);

		SmsTemplate smsTemplate = new SmsTemplate();			
		smsTemplate.setTo(toList);
		smsTemplate.setMsgBody(msgBody);
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try{
			//DefectID:20140207 Registration SMS @Suprava
			String ResetService="Registration SMS";
			boolean isRegStatus = false;
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			} 
			Query query = session.createQuery("select a.isStatus from ConfigAppEntity a where a.services='"+ResetService+"'");
			Iterator itr1=query.list().iterator();
			while(itr1.hasNext()){
				isRegStatus = (Boolean) itr1.next();				
			}
			iLogger.info ("registration status from DB : "+ isRegStatus);
			if(isRegStatus==true)
			{
				iLogger.info ("sending SMS : START");
				//new SmsHandler().handleSms("jms/queue/smsQ", smsTemplate,0);
				//DF20171016 - KO369761 : Changed SMSQ Service to Kafka Queue.
				new SmsHandler().handleSmsInKafka("SMSQueue", smsTemplate,0);
				message = result; // Defect Id 1848 by Keerthi
				iLogger.info(country+mobileNumber+":SMS with login id details has been sent to user !");
			}
			else 
			{
				bLogger.error(country+mobileNumber+":SMS with login id details cannot be sent : SMS Service turned OFF");
				message ="SMS with login id details cannot be sent : SMS Service turned OFF";
			}
		}
		catch (Exception e) {
			message ="Failed while sending SMS to user !!";
			fLogger.error("Failed while sending SMS to user !!" + e.getMessage());	
			throw new remote.wise.exception.CustomFault("Problem while sending SMS. Please try again after some time."); // Defect Id 1848 by Keerthi
		}
		return message;
	}
	
	
	//************************* DF20180730 - Rajani Nagaraju - Security Audit IssueID: JCBX-061-1-14 - START
	public HashMap<String,String> getUserName(String loginID)
	{
		HashMap<String,String> contactDetails = new HashMap<String,String>();
		Logger fLogger = FatalLoggerClass.logger;
			
		Session session = HibernateUtil.getSessionFactory().openSession();
		
		try
		{
			String queryString = "from ContactEntity where contact_id= '"+ loginID+"' and active_status=true";
			Iterator itr=session.createQuery(queryString).list().iterator();
			if(itr.hasNext())
			{
				ContactEntity contact = (ContactEntity) itr.next();
				contactDetails.put("first_name", contact.getFirst_name());
				contactDetails.put("last_name", contact.getLast_name());
			}
		
		}
		catch(Exception e)
		{
			fLogger.fatal("LoginRegistrationBO:getUserName from loginID: Exception:"+e.getMessage());
		}
			
		finally
		{
			if(session.isOpen()){ 
					//session.flush();
				session.close();
			} 
		}		
		
		return contactDetails;
	}
		
		
	public boolean isDuplicatePassword(String loginId, String password)
	{
		boolean isDuplicate = false;
		Logger fLogger = FatalLoggerClass.logger;
			
		Connection con = null;
		PreparedStatement pstmt = null;
			
		try
		{
			if(loginId==null || password==null || password.trim().length()==0)
			{
				fLogger.fatal("LoginRegistrationBO:isDuplicatePassword: loginId:"+loginId+"; Invalid password");
				return true;
			}
				
			ConnectMySQL connMySql = new ConnectMySQL();
			con = connMySql.getConnection();
			
				
			//Get the last 5 passwords for the given login
			String queryString = "select CAST(FROM_BASE64(PWD) AS CHAR(50)) as decryptedPwd from password_history where" +
						" CAST(FROM_BASE64(ID) AS CHAR(50)) like ? ";
			PreparedStatement preparedStatement = con.prepareStatement(queryString);
			preparedStatement.setString(1, loginId+"%");
				
			ResultSet rs = preparedStatement.executeQuery();
			while(rs.next())
			{
				String dbPwd = rs.getString(1);
				if(password.equalsIgnoreCase(dbPwd))
				{
					return true;
				}
			}
		}
			
		catch(Exception e)
		{
			fLogger.fatal("LoginRegistrationBO:isDuplicatePassword: loginId:"+loginId+"; Exception:"+e.getMessage());
			return true;
		}
			
		finally
		{
			try
			{
				if(pstmt!=null)
					pstmt.close();
				if(con != null)
					con.close();
			}
			catch(SQLException sqlEx)
			{
				fLogger.fatal("LoginRegistrationBO:isDuplicatePassword: loginId:"+loginId+"; Exception in closing SQL connection:"
							+sqlEx.getMessage());
			}
		}
		return isDuplicate;
	}
		
		
		
	public String updatePwdHistory(String loginId, String newPassword)
	{
		String status="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
			
		Connection con = null;
		PreparedStatement pstmt1 = null, pstmt2=null, pstmt3=null,pstmt4=null,pstmt5=null,pstmt6=null;
		ResultSet rs = null;
			
		try
		{
			if(loginId==null || newPassword==null || newPassword.trim().length()==0)
			{
				fLogger.fatal("LoginRegistrationBO:updatePwdHistory: loginId:"+loginId+"; Invalid password");
				return "FAILURE";
			}
			
			ConnectMySQL connMySql = new ConnectMySQL();
			con = connMySql.getConnection();
			
				
			//Update the Sequence ID of the existing passwords
			String updateQuery = "update password_history set SequenceId=SequenceId+1 where" +
						" CAST(FROM_BASE64(ID) AS CHAR(50)) like ? ";
			pstmt1 = con.prepareStatement(updateQuery);
			pstmt1.setString(1, loginId+"%");
			pstmt1.executeUpdate();
				
			//Delete any record with sequenceId > 5 - Store only last 5 passwords
			String deleteQuery = "delete from password_history where " +
							" CAST(FROM_BASE64(ID) AS CHAR(50)) like ? and SequenceId > 5";
			pstmt2 = con.prepareStatement(deleteQuery);
			pstmt2.setString(1, loginId+"%");
			pstmt2.executeUpdate();
				
			//Insert the new password details into password history table
			Date currentDate = new Date();
			String formattedDate= new SimpleDateFormat("yyyyMMssHHmmss").format(currentDate);
				String encodedPwd = new String(Base64.encodeBase64(newPassword.getBytes()));
				String id=loginId+"|"+formattedDate;
				String encodedLoginId=new String(Base64.encodeBase64(id.getBytes()));
				
				String insertStatement = "INSERT INTO password_history"
						+ "(ID, SequenceId, PWD, CreatedTime) VALUES"
						+ "(?,?,?,?)";
				
				pstmt3 = con.prepareStatement(insertStatement);
				pstmt3.setString(1, encodedLoginId);
				pstmt3.setInt(2, 1);
				pstmt3.setString(3, encodedPwd);
				pstmt3.setTimestamp(4, new Timestamp(currentDate.getTime()));
				pstmt3 .executeUpdate();
				
				//CR469.sn
				 // Retrieve CreatedTime from password_history for the latest password
		        String selectCreationTimeQuery = "SELECT CreatedTime " +
		                                         "FROM password_history " +
		                                         "WHERE ID = ? " +
		                                         "ORDER BY CreatedTime DESC LIMIT 1";
		        System.out.print(selectCreationTimeQuery);
		        pstmt4 = con.prepareStatement(selectCreationTimeQuery);
		        pstmt4.setString(1, encodedLoginId);
		        rs = pstmt4.executeQuery();

		        Timestamp latestCreationTime = null;
		        if (rs.next()) {
		            latestCreationTime = rs.getTimestamp("CreatedTime");
		        } else {
		            fLogger.fatal("LoginRegistrationBO:updatePwdHistory: loginId:" + loginId + "; Failed to retrieve CreatedTime from password_history");
		            return "FAILURE";
		        }

		        // Update password_creation_date in contact table with the latest CreatedTime
		        String updateContactQuery = "UPDATE contact SET password_creation_date = '" + latestCreationTime + "' WHERE Contact_ID = '" + loginId + "'";
		        pstmt5 = con.prepareStatement(updateContactQuery);
		        pstmt5.executeUpdate();
		        String updateQueryContact = "UPDATE contact SET pwd_Expired = 0 WHERE contact_ID = '" + loginId + "'";
		        pstmt6 = con.prepareStatement(updateQueryContact);
		        pstmt6.executeUpdate();
		        System.out.print(updateQueryContact);
		        System.out.print("contact table updated successfully");//CR469.sn

			}
			
			catch(Exception e)
			{
				fLogger.fatal(" LoginRegistrationBO:isDuplicatePassword: loginId:"+loginId+"; Exception:"+e.getMessage());
				return "FAILURE";
			}
			
			finally
			{
				try
				{
					if(pstmt1!=null)
						pstmt1.close();
					if(pstmt2!=null)
						pstmt2.close();
					if(pstmt3!=null)
						pstmt3.close();
					if(con != null)
						con.close();
				}
				catch(SQLException sqlEx)
				{
					fLogger.fatal("LoginRegistrationBO:isDuplicatePassword: loginId:"+loginId+"; Exception in closing SQL connection:"
							+sqlEx.getMessage());
				}
			}
			return status;
		}
	
	//JCB6622.sn
	public int getCountForgotPasswordForUser(String loginId) {
	   
	    Logger fLogger = FatalLoggerClass.logger;
	    
	    int count = 0;
	    String sql = "SELECT reset_pass_count FROM contact where contact_id ='"+loginId+"' and status=1";
	    ConnectMySQL factory = new ConnectMySQL();
	    try (Connection conn = factory.getConnection();
		    Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery(sql)){
		if(rs.next())
		    count = rs.getInt("reset_pass_count");
	    }catch (Exception e) {
		fLogger.fatal("LoginId:"+loginId+"; Exception occured : ", e.getMessage());
	    }
	    return count;
	}
	public void incrementCountForgotPasswordForUser(String loginId, int count) {
	    Logger fLogger = FatalLoggerClass.logger;
	    Logger iLogger = InfoLoggerClass.logger;
	    String query = "UPDATE contact set reset_pass_count="+count+" where contact_id='"+loginId+"' and status=1";
	    ConnectMySQL factory = new ConnectMySQL();
	    try (Connection conn = factory.getConnection();
		    Statement st = conn.createStatement()){
		st.executeUpdate(query);
	    }catch (Exception e) {
		fLogger.fatal("LoginId:"+loginId+"; Exception occured : ", e.getMessage());
	    }
	    iLogger.info("Reset pass count for user " + loginId + " changed to " + count);
	    
	}//JCB6622.en
		
		//************************* DF20180730 - Rajani Nagaraju - Security Audit IssueID: JCBX-061-1-14 - END
}
