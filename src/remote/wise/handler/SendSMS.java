package remote.wise.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.*;
import org.hibernate.Session;

import remote.wise.businessentity.SmsLogDetailsEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

public class SendSMS implements Runnable
{
	Thread t;
	List<String> mobileNum,smsBody;
	String serialNumber,transactionTime;
	int assetEventId=0;
	
	public SendSMS()
	{
		
	}
	
	public SendSMS(List<String> mobileNum, List<String> smsBody, String serialNumber, String transactionTime, int assetEventId)
	{
		t = new Thread(this, "SMS Thread");
		this.mobileNum=mobileNum;
		this.smsBody=smsBody;
		this.serialNumber=serialNumber;
		this.transactionTime=transactionTime;
		this.assetEventId=assetEventId;
		
		// Start the thread
		t.start();
	}
	
	public void run() 
	{
		sendMessage(this.mobileNum, this.smsBody,this.serialNumber, this.transactionTime, this.assetEventId);
	}
	
	public void sendMessage(List<String> mobileNum, List<String> smsBody, String serialNumber, String transactionTime, int assetEventId)
	{
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		try
		{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
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
			    	 System.out.println("In SMS Body Content"+smsBodyContent);
			    	 try
			    	 {
			    		 httpclient = new DefaultHttpClient();
	            	
			    		 smsBodyContent = smsBodyContent.replaceAll("&", "AND");
	            	
			    		 if(transactionTimeInIST!=null)
			    			 smsBodyContent = smsBodyContent+"; Dt:"+transactionTimeInIST;
		            	
			    		// String urlString = "http://alerts.kapsystem.com/api/web2sms.php?workingkey=9500x1h68760787862az&to="+mobileNum.get(i)+"&sender=JCBLLI&message="+smsBodyContent;
			    		 //String urlString = "http://www.unicel.in/SendSMS/sendmsg.php?uname=jcbwt&pass=a$1Tj~5O&dest="+mobileNum.get(i)+"&msg="+smsBodyContent+"&prty=1&vp=30&unicode=1";
						//DF20190426 :mani:removing the unicode,as for english language messages unicode is not reguired,through app servers only registration and pull sms are sent,which are in english only
			    		// String urlString = "http://www.unicel.in/SendSMS/sendmsg.php?uname=jcbwt&pass=a$1Tj~5O&dest="+mobileNum.get(i)+"&msg="+smsBodyContent+"&prty=1&vp=30";
			    		 //Deepthi: 20220127: Remove the vp =30 as the Reset SMS was not being sent to the customers. This is coming as a recommendation from the Gateway provider. 
			    		//JCB6554-Sai Divya:20240226:change of Unicel URL http://www.unicel.in/SendSMS/sendmsg.php to https://api.instaalerts.zone/SendSMS/sendmsg.php
			    		 //MEID100012615-Sai Divya:20240805:Unicel password change String String urlString = "https://api.instaalerts.zone/SendSMS/sendmsg.php?uname=jcbwt&pass=a$1Tj~5O&dest="+mobileNum.get(i)+"&msg="+smsBodyContent+"&prty=1&vp=30";
			    		 String urlString = "https://api.instaalerts.zone/SendSMS/sendmsg.php?uname=jcbwt&pass=Wipro@2024&dest="+mobileNum.get(i)+"&msg="+smsBodyContent+"&prty=1&vp=30";
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
			            	
			            	new SmsHandler().handleSms("jms/queue/smsQ", smsObj,0);
			                        	
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
			    			
			            	new SmsHandler().handleSms("jms/queue/smsQ", smsObj,0);
			            	
			            }
			    	 }
            	
			     }

			} 
		
			catch(Exception e)
			{
				fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Exception :"+e);
				Writer result = new StringWriter();
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
				}
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
			fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Exception :"+e);
			Writer result = new StringWriter();
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
			}
		}
	}
	
}
