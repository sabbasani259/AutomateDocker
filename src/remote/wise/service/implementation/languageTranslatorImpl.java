package remote.wise.service.implementation;

import java.io.UnsupportedEncodingException;


import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.SmsTemplateEntity;
import remote.wise.util.HibernateUtil;



public class languageTranslatorImpl {


               	public String SendlanguageTranslatorSMS(String mobilenumber, String smsBody) {
                                // TODO Auto-generated method stub

                                DefaultHttpClient httpclient = new DefaultHttpClient();
                                String msgBody =null;
                                Session session = HibernateUtil.getSessionFactory().getCurrentSession();
                                session.beginTransaction();
                                try{
                                                HttpHost targetHost = new HttpHost("unicel.in");
                                                SmsTemplateEntity smsTemplate =null;
                                                String msgBody1=null;
                                                Query smsTempQ = session.createQuery("from SmsTemplateEntity where eventId='"+10+"'");       
                                                Iterator smsTempItr = smsTempQ.list().iterator();
                                                while(smsTempItr.hasNext())
                                                {
                                                                smsTemplate = (SmsTemplateEntity) smsTempItr.next();
                                                                msgBody1 = smsTemplate.getSmsBody();
                                                                System.out.println("msgBody1"+msgBody1);
                                                }
                                                msgBody =UniConvertHindi(msgBody1);
                                                System.out.println("msgBody to Hindi Conversion:"+msgBody);
                                                if(mobilenumber!=null)
                                                {
                                                                System.out.println("Send SMS using Unicell");                   
                                                                httpclient = new DefaultHttpClient();
                                                                //httpclient.getCredentialsProvider().setCredentials( new  AuthScope("proxy4.wipro.com", 8080), new UsernamePasswordCredentials("sunayak", "Ruby#12345"));
                                                               //JCB6554-Sai Divya:20240724:change of Unicel URL String String urlString = "http://www.unicel.in/SendSMS/sendmsg.php?uname=WiproDe&pass=d8y(u~d$&dest="+mobilenumber+"&msg="+msgBody+"&prty=1&vp=30";
                                                                //MEID100012615-Sai Divya:20240805:Unicel password change String String urlString = "http://api.instaalerts.zone/SendSMS/sendmsg.php?uname=WiproDe&pass=d8y(u~d$&dest="+mobilenumber+"&msg="+msgBody+"&prty=1&vp=30";
                                                                String urlString = "http://api.instaalerts.zone/SendSMS/sendmsg.php?uname=jcbwt&pass=Wipro@2024&dest="+mobilenumber+"&msg="+msgBody+"&prty=1&vp=30";
                                                                urlString = urlString.replaceAll("%", "%25");
                                                                urlString = urlString.replaceAll("\\s", "%20");
                                                                urlString = urlString.replaceAll("#", "%23");
                                                                urlString = urlString.replaceAll("@", "%40");
                                                                urlString = urlString.replaceAll("!", "%21");
                                                                urlString = urlString.replaceAll("\\", "%2F");
                                                                System.out.println(" Final SMS urlString: "+urlString); 
                                                                //specify the get request
                                                                HttpGet httpget = new HttpGet(urlString);

                                                                System.out.println(" Send SMS - Connect to Unicel ");
                                                                HttpResponse response = httpclient.execute(targetHost, httpget);
                                                                HttpEntity entity = response.getEntity();

                                                                System.out.println("SMS Response Status");
                                                                System.out.println("-------------------------------------");
                                                                System.out.println(response.getStatusLine());

                                                                int responseCode = response.getStatusLine().getStatusCode();
                                                                System.out.println("HTTP Response Code:"+responseCode);

                                                                if (entity != null) 
                                                                {
                                                                                System.out.println("Response content length: " + entity.getContentLength());
                                                                }

                                                                entity.consumeContent();
                                                                if(responseCode!=200)
                                                                {
                                                                                System.out.println("Packet to Unicel SMS server - Send Failed");
                                                                }

                                                                else
                                                                {
                                                                                System.out.println(" SMS Sent to Unicel server ");
                                                                }

                                                                httpclient.getConnectionManager().shutdown();

                                                }
                                                /*if(mobilenumber!=null && msgBody!=null)
                                                {
                                                                System.out.println("Send SMS using KapSystem");          
                                                                httpclient = new DefaultHttpClient();
                                                                String urlString = "http://alerts.kapsystem.com/api/web2sms.php?workingkey=9500x1h68760787862az&to="+mobilenumber+"&sender=JCBLLI&message="+msgBody;
                                                                urlString = urlString.replaceAll("%", "%25");
                                                                urlString = urlString.replaceAll("\\s", "%20");
                                                                urlString = urlString.replaceAll("#", "%23");
                                                                urlString = urlString.replaceAll("@", "%40");
                                                                urlString = urlString.replaceAll("!", "%21");
                                                                System.out.println(" Final SMS urlString: "+urlString); 

                                                                HttpGet httpget = new HttpGet(urlString);


                                                                HttpResponse response = httpclient.execute(targetHost, httpget);
                                                                HttpEntity entity = response.getEntity();
                                                                System.out.println(" Send SMS - Connecting.. to KapSystem");
                                                                System.out.println(response.getStatusLine());

                                                                int responseCode = response.getStatusLine().getStatusCode();
                                                                System.out.println("HTTP Response Code:"+responseCode);

                                                                if (entity != null) 
                                                                {
                                                                                System.out.println("Response content length: " + entity.getContentLength());
                                                                }
                                                                if(responseCode!=200)
                                                                {
                                                                                System.out.println("Packet to Unicel SMS server - Send Failed");
                                                                }

                                                                else
                                                                {
                                                                                System.out.println(" SMS Sent to Unicel server ");
                                                                }
                                                                entity.consumeContent();

                                                                httpclient.getConnectionManager().shutdown();

                                                }*/
                                }
                                catch(Exception e)
                                {
                                                e.printStackTrace();

                                }
                                finally
                                {
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
                                return msgBody;
                }              
                
                public String UniConvertHindi(String str) {
                                byte[] b = str.getBytes();
                                String actualCharacter = null;
                                try {
                                                actualCharacter = new String(b, "UTF-8");
                                } catch (UnsupportedEncodingException ex) {
                                                //   Logger.getLogger(HindiUnicode.class.getName()).log(Level.SEVERE, null, ex);
                                                ex.printStackTrace();
                                }
                                return actualCharacter;
                }
}
