package remote.wise.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.*;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Session;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.businessentity.SmsLogDetailsEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;

public class SendSMSWithKafka implements Callable
{
	List<String> mobileNum,smsBody;
	String serialNumber,transactionTime;
	int assetEventId=0;
	public String smsTemplateString = null;

	@SuppressWarnings("unchecked")
	@Override
	public Object call() throws Exception {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		// TODO Auto-generated method stub
		iLogger.info("payload from kafka :"+this.smsTemplateString);
		try
		{
			int assetEventId=0;
			List<String> toList = null;
			List<String> msgBody = null;
			String serialNumber = null;
			String transactionTime = null;

			HashMap<String,Object> smsTemplateMap = new Gson().fromJson(this.smsTemplateString, new TypeToken<HashMap<String, Object>>(){}.getType());
			//iLogger.info("hashmap mobile numbers :"+ smsTemplateMap.get("to")+"smsBody: "+smsTemplateMap.get("msgBody")+" SerialNumber:"+smsTemplateMap.get("serialNumber")+" getTransactionTime:"+smsTemplateMap.get("transactionTime")+"getAssetEventId"+smsTemplateMap.get("assetEventId"));

			msgBody = (List<String>)smsTemplateMap.get("msgBody");
			toList = (List<String>)smsTemplateMap.get("to");
			serialNumber = (String)smsTemplateMap.get("serialNumber");
			transactionTime = (String)smsTemplateMap.get("transactionTime");
			
			if(smsTemplateMap.get("assetEventId") != null){
				Double d = new Double((Double) smsTemplateMap.get("assetEventId"));
				assetEventId = d.intValue();
			}
			
			//TaskID 1481 - JCB4908 - Rajani Nagaraju - contact creation failure - New Implementation
			//sendMessage(toList,msgBody,serialNumber,transactionTime,assetEventId);
			sendMessageWithJDBC(toList,msgBody,serialNumber,transactionTime,assetEventId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("SendSMSWithKafka:"+e.getMessage(),e);
		}
		return null;
	}


	public void sendMessage(List<String> mobileNum, List<String> smsBody, String serialNumber, String transactionTime, int assetEventId)
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		DefaultHttpClient httpclient = new DefaultHttpClient();

		try
		{
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();

			try 
			{
				String transactionTimeInIST ="";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();

				if (transactionTime!=null)
				{
					cal.setTime(sdf.parse(transactionTime));
					cal.add(Calendar.MINUTE, 330);
					transactionTimeInIST=sdf.format(cal.getTime());
				}

				// HttpHost targetHost = new HttpHost("alerts.kapsystem.com");
				HttpHost targetHost = new HttpHost("unicel.in");
				for(int i=0; i<mobileNum.size(); i++)
				{
					int responseCode=3333;
					String smsBodyContent = smsBody.get(i);
					try
					{
						httpclient = new DefaultHttpClient();

						smsBodyContent = smsBodyContent.replaceAll("&", "AND");

						if(transactionTimeInIST!=null)
							smsBodyContent = smsBodyContent+"; Dt:"+transactionTimeInIST;

						// String urlString = "http://alerts.kapsystem.com/api/web2sms.php?workingkey=9500x1h68760787862az&to="+mobileNum.get(i)+"&sender=JCBLLI&message="+smsBodyContent;
						//String urlString = "http://www.unicel.in/SendSMS/sendmsg.php?uname=jcbwt&pass=a$1Tj~5O&dest="+mobileNum.get(i)+"&msg="+smsBodyContent+"&prty=1&vp=30&unicode=1";
						//DF20190426 :mani:removing the unicode,as for english language messages unicode is not reguired,through app servers only registration and pull sms are sent,which are in english only
						//DF20220131: Deepthi - Removed the vp=30 in the SMS URL as per the suggestion from the Gateway team as many users were not receiving the SMS. 
						//JCB6554-Sai Divya:20240226:change of Unicel URL http://www.unicel.in/SendSMS/sendmsg.php to https://api.instaalerts.zone/SendSMS/sendmsg.php
						//MEID100012615-Sai Divya:20240805:Unicel password change String String String urlString = "https://api.instaalerts.zone/SendSMS/sendmsg.php?uname=jcbwt&pass=a$1Tj~5O&dest="+mobileNum.get(i)+"&msg="+smsBodyContent+"&prty=1";
						String urlString = "https://api.instaalerts.zone/SendSMS/sendmsg.php?uname=jcbwt&pass=Wipro@2024&dest="+mobileNum.get(i)+"&msg="+smsBodyContent+"&prty=1";
						urlString = urlString.replaceAll("%", "%25");
						urlString = urlString.replaceAll("\\s", "%20");
						urlString = urlString.replaceAll("#", "%23");
						urlString = urlString.replaceAll("@", "%40");
						urlString = urlString.replaceAll("!", "%21");
						//DF20150810 - If there is a miss to replace < or > from template for <VIN>/<Profile>/<ContactNumber> , instead of throwing error, do url encoding for the same
						urlString = urlString.replaceAll(">", "%3E");
						urlString = urlString.replaceAll("<", "%3C");

						iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" Final SMS urlString: "+urlString); 

						HttpGet httpget = new HttpGet(urlString);

						//	 iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" Send SMS - Connect to KAPSYS ");
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" Send SMS - Connect to UNICELL ");
						HttpResponse response = httpclient.execute(targetHost, httpget);
						HttpEntity entity = response.getEntity();
						responseCode = response.getStatusLine().getStatusCode();
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+"  :"+"HTTP Response Code:"+responseCode);

						String smsResponseID="";

						if (entity != null) 
						{
							//DF20140105 - Rajani Nagaraju - Capturing Response ID sent from KAPSYS on receival of HTTP message
							InputStream in = entity.getContent();
							smsResponseID = IOUtils.toString(in);
							if(smsResponseID.contains(" ID="))
							{
								String[] strArr = smsResponseID.split(" ID=");
								if(strArr.length>1)
									smsResponseID = strArr[1];
							}
						}

						entity.consumeContent();

						//If the SMS packet is moved out of the SMS Queue but not reached the SMS server, reassign the packet to the SMS Queue
						if(responseCode!=200)
						{
							//fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Packet to KAPSYS SMS server - Send Failed");
							fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Packet to UNICELL SMS server - Send Failed");
							//DF20150317 - Rajani Nagaraju - To Link the sent SMS wih the corresponding asset event
							SmsTemplate smsObj = new SmsTemplate();

							List<String> resendMobileNum = new LinkedList<String>();
							resendMobileNum.add(mobileNum.get(i));
							smsObj.setTo(resendMobileNum);

							List<String> resendMsgBody = new LinkedList<String>();
							resendMsgBody.add(smsBody.get(i));
							smsObj.setMsgBody(resendMsgBody);
							smsObj.setSerialNumber(serialNumber);
							smsObj.setTransactionTime(transactionTime);

							smsObj.setAssetEventId(assetEventId);

							//iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" Rethrow Message to Queue : KAPSYS not available");
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" Rethrow Message to Queue : UNICELL not available");
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"SMS To : "+resendMobileNum);
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"SMS Body: "+resendMsgBody);
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"AssetEventId: "+assetEventId);

							//new SmsHandler().handleSms("smsQ", smsObj,0);
							new SmsHandler().handleSmsInKafka("SMSQueue", smsObj,0);

						}

						//DF20131223 - Rajani Nagaraju - To log the details of the SMS that are being sent to KAPSYS
						else
						{
							//iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" SMS Sent to KAPSYS server ");
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" SMS Sent to UNICELL server ");
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" Insert into SMS Log details table ");

							Timestamp currentTimestamp = new Timestamp((new Date()).getTime());

							SmsLogDetailsEntity smsLogDetails = new SmsLogDetailsEntity();
							smsLogDetails.setSerialNumber(serialNumber);
							smsLogDetails.setMobileNumber(mobileNum.get(i));
							smsLogDetails.setSmsSentTime(currentTimestamp);
							smsLogDetails.setSmsBody(smsBodyContent);
							smsLogDetails.setResponseId(smsResponseID);
							smsLogDetails.setAssetEventId(assetEventId);
							session.save(smsLogDetails);
						}

						httpclient.getConnectionManager().shutdown();
					}

					catch(Exception e)
					{
						if(responseCode==3333)
						{
							//If any error in thrown in sending the message to KAPSYS put back the data packet to the Queue
							//	fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Unable to connect to KAPSYS SMS server - Send Failed: "+e);
							fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Unable to connect to UNICELL SMS server - Send Failed: "+e);
							SmsTemplate smsObj = new SmsTemplate();
							List<String> resendMobileNum = new LinkedList<String>();
							resendMobileNum.add(mobileNum.get(i));
							smsObj.setTo(resendMobileNum);

							List<String> resendMsgBody = new LinkedList<String>();
							resendMsgBody.add(smsBody.get(i));
							smsObj.setMsgBody(resendMsgBody);
							smsObj.setSerialNumber(serialNumber);
							smsObj.setTransactionTime(transactionTime);

							smsObj.setAssetEventId(assetEventId);

							//iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" Rethrow Message to Queue : KAPSYS not available");
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" Rethrow Message to Queue : UNICELL not available");
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"SMS To : "+resendMobileNum);
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"SMS Body: "+resendMsgBody);
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"AssetEventId: "+assetEventId);

							//new SmsHandler().handleSms("jms/queue/smsQ", smsObj,0);
							new SmsHandler().handleSmsInKafka("SMSQueue", smsObj,0);

						}
					}

				}

			} 

			catch(Exception e)
			{
				fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Exception :"+e.getMessage(),e);
				/*Writer result = new StringWriter();
				PrintWriter printWriter = new PrintWriter(result);
				e.printStackTrace(printWriter);
				String err = result.toString();
				fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Exception trace: "+err);
				try 
				{
					printWriter.close();
					result.close();
				} 

				catch (IOException e1) 
				{
					e1.printStackTrace();
				}*/
			}

			finally 
			{
				//DF20131223 - Rajani Nagaraju - To log the details of the SMS that are being sent to KAPSYS
				if(session.getTransaction().isActive())
				{
					session.getTransaction().commit();
				}

				if(session.isOpen())
				{
					session.flush();
					session.close();
				}

				// When HttpClient instance is no longer needed, shut down the connection manager to ensure immediate deallocation of all system resources
				httpclient.getConnectionManager().shutdown();

			}
		}

		catch(Exception e)
		{
			fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Exception :"+e.getMessage(),e);
			/*Writer result = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			String err = result.toString();
			fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Exception trace: "+err);
			try 
			{
				printWriter.close();
				result.close();
			} 

			catch (IOException e1) 
			{
				e1.printStackTrace();
			}*/
		}
	}

	//TaskID 1481 - JCB4908 - Rajani Nagaraju - contact creation failure - New Implementation
	public void sendMessageWithJDBC(List<String> mobileNum, List<String> smsBody, String serialNumber, String transactionTime, int assetEventId)
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		iLogger.info("SendSMSWithKafka:sendMessageWithJDBC:Input:mobileNum:"+mobileNum+"; smsBody:"+smsBody+"; serialNumber:"+serialNumber+";" +
				"transactionTime:"+transactionTime+"; assetEventId:"+assetEventId);
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		iLogger.info("SendSMSWithKafka:sendMessageWithJDBC:After DefaultHttpClient initialization");
		
		Connection con=null;
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		
		//-------------------------- STEP1: Send SMS by invoking the end point URL of Unicel
		try
		{
			String transactionTimeInIST ="";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();

			if (transactionTime!=null)
			{
				cal.setTime(sdf.parse(transactionTime));
				cal.add(Calendar.MINUTE, 330);
				transactionTimeInIST=sdf.format(cal.getTime());
			}
			
			HttpHost targetHost = new HttpHost("unicel.in");
			for(int i=0; i<mobileNum.size(); i++)
			{
				int responseCode=3333;
				String smsBodyContent = smsBody.get(i);
				try
				{
					httpclient = new DefaultHttpClient();

					smsBodyContent = smsBodyContent.replaceAll("&", "AND");

					if(transactionTimeInIST!=null)
						smsBodyContent = smsBodyContent+"; Dt:"+transactionTimeInIST;
					// ME100005796-ForForgotLoginID-20230321-prasad.sn
					if (smsBodyContent.contains("login id") && !smsBodyContent.contains("new password"))
						smsBodyContent = smsBodyContent + " -JCB LiveLink"; // ME100005796.en
					
					iLogger.info("SendSMSWithKafka:sendMessageWithJDBC:smsBodyContent Modified:"+smsBodyContent);
					//JCB6554-Sai Divya:20240724:change of Unicel URL String String urlString = "http://www.unicel.in/SendSMS/sendmsg.php?uname=jcbwt&pass=a$1Tj~5O&dest="+mobileNum.get(i)+"&msg="+smsBodyContent+"&prty=1";
					//MEID100012615-Sai Divya:20240805:Unicel password change String String String urlString="https://api.instaalerts.zone/SendSMS/sendmsg.php?uname=jcbwt&pass=a$1Tj~5O&dest="+mobileNum.get(i)+"&msg="+smsBodyContent+"&prty=1";
					String urlString="https://api.instaalerts.zone/SendSMS/sendmsg.php?uname=jcbwt&pass=Wipro@2024&dest="+mobileNum.get(i)+"&msg="+smsBodyContent+"&prty=1";
					urlString = urlString.replaceAll("%", "%25");
					urlString = urlString.replaceAll("\\s", "%20");
					urlString = urlString.replaceAll("#", "%23");
					urlString = urlString.replaceAll("@", "%40");
					urlString = urlString.replaceAll("!", "%21");
					urlString = urlString.replaceAll(">", "%3E");
					urlString = urlString.replaceAll("<", "%3C");

					iLogger.info("SendSMSWithKafka:sendMessageWithJDBC:mobileNum"+mobileNum.get(i)+"; Final SMS urlString: "+urlString); 
					HttpGet httpget = new HttpGet(urlString);
					HttpResponse response = httpclient.execute(targetHost, httpget);
					HttpEntity entity = response.getEntity();
					responseCode = response.getStatusLine().getStatusCode();
					iLogger.info("SendSMSWithKafka:sendMessageWithJDBC:mobileNum:"+mobileNum.get(i)+";"+"HTTP Response Code from Unicel:"+responseCode);
					
					String smsResponseID="";

					if (entity != null) 
					{
						InputStream in = entity.getContent();
						smsResponseID = IOUtils.toString(in);
						if(smsResponseID.contains(" ID="))
						{
							String[] strArr = smsResponseID.split(" ID=");
							if(strArr.length>1)
								smsResponseID = strArr[1];
						}
					}
					
					entity.consumeContent();
					
					//If the SMS packet is moved out of the SMS Queue but not reached the SMS server, reassign the packet to the SMS Queue
					if(responseCode!=200)
					{
						fLogger.fatal("SendSMSWithKafka:sendMessageWithJDBC:mobileNum:"+mobileNum.get(i)+";"+"Packet to UNICELL SMS server - Send Failed." +
								"Rethrow the message to Queue");
						SmsTemplate smsObj = new SmsTemplate();

						List<String> resendMobileNum = new LinkedList<String>();
						resendMobileNum.add(mobileNum.get(i));
						smsObj.setTo(resendMobileNum);

						List<String> resendMsgBody = new LinkedList<String>();
						resendMsgBody.add(smsBody.get(i));
						smsObj.setMsgBody(resendMsgBody);
						smsObj.setSerialNumber(serialNumber);
						smsObj.setTransactionTime(transactionTime);

						smsObj.setAssetEventId(assetEventId);
						new SmsHandler().handleSmsInKafka("SMSQueue", smsObj,0);

					}
					
					//Log the message to SMS log details table
					else
					{
						int Sms_Logger_ID=0;
						Timestamp currentTimestamp = new Timestamp((new Date()).getTime());
						String query = "INSERT INTO sms_log_details(Serial_Number,Mobile_Number,SmsSent_Timestamp,Sms_Body,Response_ID,Asset_Event_ID)" +
								" VALUES (?,?,?,?,?,?) ";
						con = new ConnectMySQL().getConnection();
						pstmt = con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
						pstmt.setString(1, serialNumber);
						pstmt.setString(2, mobileNum.get(i));
						pstmt.setTimestamp(3, currentTimestamp);
						pstmt.setString(4,smsBodyContent);
						pstmt.setString(5, smsResponseID);
						pstmt.setInt(6, assetEventId);
						int rowAffected = pstmt.executeUpdate();
						if(rowAffected == 1)
				        {
							rs = pstmt.getGeneratedKeys();
							
				        }
						if (rs.next()) 
						{
							Sms_Logger_ID = rs.getInt(1);
						}
						
						iLogger.info("SendSMSWithKafka:sendMessageWithJDBC:mobileNum:"+Arrays.toString(mobileNum.toArray())+";" +
								"Logging under Sms_log_details is successful with Sms_Logger_ID:"+Sms_Logger_ID);
					}
					
				}
				catch(Exception e)
				{
					fLogger.fatal("SendSMSWithKafka:sendMessageWithJDBC:mobileNum:"+Arrays.toString(mobileNum.toArray())+"; Exception in sending/logging SMS:"+e.getMessage(),e);
				}
			}
		}
		catch(Exception e)
		{
			fLogger.fatal("SendSMSWithKafka:sendMessageWithJDBC:mobileNum:"+Arrays.toString(mobileNum.toArray())+"; Exception in sending SMS:"+e.getMessage(),e);
		}
		
		finally
		{
			try
			{
				if(pstmt!=null)
					pstmt.close();
				if(con!=null)
					con.close();
				
			}
			catch(Exception e)
			{
				fLogger.fatal("SendSMSWithKafka:sendMessageWithJDBC:mobileNum:"+Arrays.toString(mobileNum.toArray())+";Exception in closing the connection:"+e.getMessage(),e);
			}
		}
	}
}
