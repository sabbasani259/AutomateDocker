//CR337 : 20220721 : Dhiraj K : Property file read.
//LLOPS-94 :20250403 : Sai Divya : password from configuration file
package remote.wise.businessobject;


import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ResponseObject;
//import remote.wise.util.WiseLogger;

public class SMSTrigger {

	public ArrayList<ResponseObject> SendSMS(String phoneNumber, String otpMessageBody, String emailD) throws Exception {
		ArrayList<ResponseObject> responseObjects = null;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
//		 httpclient.getCredentialsProvider().setCredentials( new  AuthScope("proxy1.wipro.com", 8080), new UsernamePasswordCredentials("", "Tauro@"));
		//WiseLogger infoLogger = WiseLogger.getLogger("HAJSendSMSService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
	//	HttpHost targetHost = new HttpHost("alerts.kapsystem.com");
		 HttpHost targetHost = new HttpHost("unicel.in");
		int responseCode = 3333;
		try {
			//LLOPS-94 : password from configuration file.sn
			String sourceUname = null;
        	String sourcePass = null;
			Properties prop = new Properties();
			try {
				prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				sourceUname= prop.getProperty("SMSUserName");
				sourcePass= prop.getProperty("SMSPassWord");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				fLogger.fatal("issue in while getting path from configuration path"
						+ e1.getMessage());
			}
			//LLOPS-94 : password from configuration file.en
			iLogger.info("sourceUname: "+sourceUname+"sourcePass: "+sourcePass);
			httpclient = new DefaultHttpClient();
			/*String urlString = "http://alerts.kapsystem.com/api/web2sms.php?workingkey=9500x1h68760787862az&to="
					+ phoneNumber + "&sender=JCBLLI&message=" + otpMessageBody;*/
			//DefectId:20150513 @Suprava GateWay changed to unicell
			//JCB6554-Sai Divya:20240724:change of Unicel URL String urlString = "http://www.unicel.in/SendSMS/sendmsg.php?uname=jcbwt&pass=a$1Tj~5O&dest="+phoneNumber+"&msg="+otpMessageBody+"&prty=1&vp=30";	
			//MEID100012615-Sai Divya:20240805:Unicel password change String urlString = "http://api.instaalerts.zone/SendSMS/sendmsg.php?uname=jcbwt&pass=a$1Tj~5O&dest="+phoneNumber+"&msg="+otpMessageBody+"&prty=1&vp=30";	
			//String urlString = "http://api.instaalerts.zone/SendSMS/sendmsg.php?uname=jcbwt&pass=Wipro@2024&dest="+phoneNumber+"&msg="+otpMessageBody+"&prty=1&vp=30";//LLOPS-94.o		
			String urlString = "http://api.instaalerts.zone/SendSMS/sendmsg.php?uname="+sourceUname+"&pass="+sourcePass+"&dest="+phoneNumber+"&msg="+otpMessageBody+"&prty=1&vp=30";//LLOPS-94.n
			urlString = urlString.replaceAll("%", "%25");
			urlString = urlString.replaceAll("\\s", "%20");
			urlString = urlString.replaceAll("#", "%23");
			urlString = urlString.replaceAll("@", "%40");
			urlString = urlString.replaceAll("!", "%21");
			HttpGet httpget = new HttpGet(urlString);
			HttpResponse response = httpclient.execute(targetHost, httpget);
			HttpEntity entity = response.getEntity();
			responseCode = response.getStatusLine().getStatusCode();
			entity.consumeContent();
			iLogger.info("responseCode:"+responseCode);
			if (responseCode == 200) {
				ResponseObject responseObject = new ResponseObject();
				responseObject.setCode("103");
				responseObject.setMessage("SMS sent successfully");
				responseObjects = new ArrayList<ResponseObject>();
				responseObjects.add(responseObject);
			} else {
				ResponseObject responseObject = new ResponseObject();
				responseObject.setCode("104");
				responseObject.setMessage("Failed to send SMS");
				responseObjects = new ArrayList<ResponseObject>();
				responseObjects.add(responseObject);
			}
			httpclient.getConnectionManager().shutdown();
		} catch (Exception e) {
			iLogger.info(e.getLocalizedMessage());
			if(responseCode==3333){
				ResponseObject responseObject = new ResponseObject();
				responseObject.setCode("104");
				responseObject.setMessage("Failed to send SMS");
				responseObjects = new ArrayList<ResponseObject>();
				responseObjects.add(responseObject);
			}
			else{
				ResponseObject responseObject = new ResponseObject();
				responseObject.setCode("104");
				responseObject.setMessage("Failed to send SMS");
				responseObjects = new ArrayList<ResponseObject>();
				responseObjects.add(responseObject);
				iLogger.info("Failed to send HireAJCB message :"+responseCode);
			}
		}
		return responseObjects;
	}

	public ArrayList<ResponseObject> sendEmail(String otp_msg,
			String user_EmailId) {
		// TODO Auto-generated method stub
		
	//	WiseLogger infoLogger = WiseLogger.getLogger("HAJSendEmailService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		ArrayList<ResponseObject> responseObjects = null;
		String status = "success";
		//CR337.sn
		String smtpHost = null;
		String smtpPort = null;
		Properties prop = null;
		try{
			prop = CommonUtil.getDepEnvProperties();
			smtpHost = prop.getProperty("SMTPHostIP");
			smtpPort = prop.getProperty("SMTPHostPORT");
			iLogger.info("SMSTrigger : smtp host :" + smtpHost + ":smtpport:"+smtpPort);
		}catch(Exception e){
			fLogger.fatal("SMSTrigger : sendEmail : " +
					"Exception in getting SMTP Host server detail from properties file: " +e.getMessage());
		}
		//CR337.en

		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		//props.put("mail.smtp.host", "10.1.3.128");
		//20220705: Dhiraj K : Changes for AWS ip and port changes
		//props.put("mail.smtp.host", "10.179.12.13");
		//props.put("mail.smtp.host", "10.210.196.206"); //CR337.o
		props.put("mail.smtp.host", smtpHost);//CR337.n
		props.put("mail.smtp.auth", "false");
		//props.put("mail.smtp.port", "25");//CR337.o
		props.put("mail.smtp.port", smtpPort);//CR337.n
		props.put("mail.debug", "true");

		Session session = Session.getInstance(props);

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("suprava.nayak@wipro.com"));
			InternetAddress[] emailToList = getAddressList(user_EmailId);
			message.addRecipients(Message.RecipientType.TO, emailToList);
			message.setSubject("OTP For Hire-A-JCB");
			message.setText(otp_msg);
			Transport.send(message);

			ResponseObject responseObject = new ResponseObject();
			responseObject.setCode("203");
			responseObject.setMessage("Email sent successfully");
			responseObjects = new ArrayList<ResponseObject>();
			responseObjects.add(responseObject);
		}

		catch (MessagingException e) {
			ResponseObject responseObject = new ResponseObject();
			responseObject.setCode("204");
			responseObject.setMessage("Failed to send Email");
			responseObjects = new ArrayList<ResponseObject>();
			responseObjects.add(responseObject);
			iLogger.info("Messaging Exception:" + e.getMessage());
		}

		catch (Exception e) {
			ResponseObject responseObject = new ResponseObject();
			responseObject.setCode("204");
			responseObject.setMessage("Failed to send Email");
			responseObjects = new ArrayList<ResponseObject>();
			responseObjects.add(responseObject);
			iLogger.info("Exception while sending Email:"
					+ e.getMessage());
		}

		return responseObjects;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public InternetAddress[] getAddressList(String toList)
	{
	//	WiseLogger infoLogger = WiseLogger.getLogger("AssetExtendedService:","info");
		Logger iLogger = InfoLoggerClass.logger;
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
					iLogger.info("Exception:"+e.getMessage());
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
			iLogger.info("RunTime Exception:"+e.getMessage());
		}
		
		return emailAddress;
	}
}