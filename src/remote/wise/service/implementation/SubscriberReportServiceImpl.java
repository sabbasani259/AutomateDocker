package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.businessentity.AccountContactMapping;
import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;
import remote.wise.util.HibernateUtil;


/**
 * DF100001418:20220429:DHIRAJ K:Notification Subscriber Report issue 
 */
public class SubscriberReportServiceImpl {

	public String getNotificationSubscriberReport()
	{
		//DF100001418.sn
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String query = null;
		String vin="";
		Object subscribergroupdata=null;
		String response="SUCCESS";

		try{
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();

			query="SELECT * FROM MAlertSubsriberGroup where SubscriberGroup ->'$.Subscriber3' is not null ";
			iLogger.info("SubscriberReportService : query "+query);
			rs = statement.executeQuery(query);
			List<Map<String, Object>> subscriberObj = new ArrayList<Map<String,Object>>();
			while(rs.next()){
				Map<String,Object> vinSubsciberDataMap =new HashMap<>();
				vin=rs.getString("AssetID");
				subscribergroupdata=rs.getObject("SubscriberGroup");
				String subscriberJSONString =subscribergroupdata.toString();
				vinSubsciberDataMap.put(vin, subscriberJSONString);
				subscriberObj.add(vinSubsciberDataMap);
			}
			iLogger.info("SubscriberReportService:SubscriberReportImpl:Total Subscribed Vins:" + subscriberObj.size());

			//Starting 10 threads
			ExecutorService executor = Executors.newFixedThreadPool(10);
			for (Map<String, Object> vinData : subscriberObj){
				SubscriberReportServiceTask task = new SubscriberReportServiceTask(vinData, prodConnection);
				executor.submit(task);
			}
			try{
				executor.shutdown();
				if(!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)){
					executor.shutdownNow();
					response="FAILURE";
				}
			}catch(InterruptedException e){
				executor.shutdownNow();
				response="FAILURE";
				fLogger.error("SubscriberReportService:SubscriberReportImpl:Error while closing threads."+e.getMessage());
			}
		}catch(Exception e){
			e.printStackTrace();
			fLogger.fatal("Exception occured in SubscriberReportServiceImpl :"+e.getMessage());
			response="FAILURE";
		}
		finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
					fLogger.fatal("SQLException occured in closing resultset : SubscriberReportServiceImpl :"+e1.getMessage());
					response="FAILURE";
				}
			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
					fLogger.fatal("SQLException occured in closing prodConnection : SubscriberReportServiceImpl :"+e.getMessage());
					response="FAILURE";
				}
			}
		}
		return response;
		//DF100001418.en
		
		//DF100001418.so
		/*Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null,statement2=null,statement3=null,statement4=null;
		ResultSet rs = null;
		ResultSet ns3rs=null,smsItr=null,contactItr=null,accountItr=null,accountNameItr=null;
		String query = null;
		String vin="";
		Session session=null;
		Object subscribergroupdata=null;
    	HashMap<String,String> subsGroupMap = new HashMap<String,String>();
    	String response=null;
    	List<HashMap<String,Object>> responseList =new ArrayList<HashMap<String,Object>>();

		try{
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			statement2=prodConnection.createStatement();
			statement3=prodConnection.createStatement();
			//statement4=prodConnection.createStatement();

			
			query="SELECT * FROM MAlertSubsriberGroup where SubscriberGroup ->'$.Subscriber3' is not null ";
			//query="SELECT * FROM MAlertSubsriberGroup where SubscriberGroup ->'$.Subscriber3' is not null and SubscriberGroup ->'$.Subscriber2' is not null and SubscriberGroup ->'$.Subscriber1' is not null;";
			iLogger.info("SubscriberReportService : query "+query);
			rs = statement.executeQuery(query);
			
			while(rs.next())
			{
				try{
				HashMap<String,Object> responseObj=new HashMap<String, Object>();
				vin=rs.getString("AssetID");
				responseObj.put("VIN", vin);
				subscribergroupdata=rs.getObject("SubscriberGroup");
				String subscriberJSONString =subscribergroupdata.toString();
				subsGroupMap = new Gson().fromJson(subscriberJSONString, new TypeToken<HashMap<String, Object>>() {}.getType());
				subsGroupMap.remove("@type");
				subsGroupMap.remove("@version");
				if(subsGroupMap!=null && subsGroupMap.size()>0)
				{
					session = HibernateUtil.getSessionFactory().openSession();
					
						//iLogger.info("SubscriberReportService VIN:"+vin+":Get EmailID and ContactNumber for Subscriber List");
						for(Map.Entry entry: subsGroupMap.entrySet())
						{
							if(entry.getKey().toString().equalsIgnoreCase("@type"))
								continue;
							
							if(entry.getKey().toString().equalsIgnoreCase("@version"))
								continue;
							
							String subscribersData = entry.getValue().toString();
							subscribersData = subscribersData.replace("/", "?");
							HashMap<String,Object> subscribersDataMap = new Gson().fromJson(subscribersData, new TypeToken<HashMap<String, Object>>() {}.getType());
							HashMap<String,Object> subscribersResultMap=new HashMap<String,Object>();
							subscribersDataMap.remove("@type");
							subscribersDataMap.remove("@version");
							for(Map.Entry contactEntry : subscribersDataMap.entrySet())
							{
								if(contactEntry.getKey().toString().equalsIgnoreCase("@type"))
									continue;
								
								if(contactEntry.getKey().toString().equalsIgnoreCase("@version"))
									continue;
								String value=contactEntry.getValue().toString().replace("?", "/");
								String accntName="No data";
								//for account name
                                String accountq="select account_id from account_contact where contact_id ='"+value+"'";
                                //ResultSet accountItr = null;
                                //System.out.println("accountq :"+accountq);
                                accountItr=statement2.executeQuery(accountq);
								int accountid=0;
								while(accountItr.next())
								{
									//accountenty=(AccountEntity) accountItr.next();
									//accountid=accountenty.getAccount_id()
                                	accountid=accountItr.getInt(1);

								}
								if(accountid!=0)
								{
									 String accountName="select account_name from account where account_id ='"+accountid+"'";
                                     //ResultSet accountNameItr = null;
		                                //System.out.println("accountName :"+accountName);

                                     accountNameItr= statement2.executeQuery(accountName);
                                     while(accountNameItr.next())
                                     {
                                           accntName=(String) accountNameItr.getString(1);
                                           //replacing special characters which are breaking the hashmap conversion in UI
                                           accntName=accntName.replace("\'", "");
                                           accntName=accntName.replace("\"", "");
                                           accntName=accntName.replace(".", "");
                                           accntName=accntName.replace("\t", "");
                                           accntName=accntName.replace(",", "");
                                           accntName=accntName.replace(":", " ");
                                           accntName=accntName.replace("-", "");


										//accntName=accName.getAccount_name();  
									}
								}
								//value=value+"|"+accntName;
								subscribersResultMap.put("AccountName", accntName.trim());
								if(contactEntry.getKey().toString().contains("EMAIL"))
								{
									//---------------- Get the EmailId for the specified contact
									String emailId = "No data";
								      String contactQ = "select * from contact where contact_id='"+value+"' and status=1";
                                     // ResultSet contactItr = null;
                                      contactItr=statement2.executeQuery(contactQ);
                                      ContactEntity contact = null;
                                      while(contactItr.next())
                                      {
                                            
                                                  emailId = contactItr.getString("Primary_Email_ID");
									}
									
                                      if(emailId == null){emailId="No data";
										subscribersResultMap.put(contactEntry.getKey().toString().trim(),"NONE");}
									
									subscribersResultMap.put(contactEntry.getKey().toString().trim(), value+"|"+emailId);
								}
								
								else if(contactEntry.getKey().toString().contains("SMS"))
								{
									//---------------- Get the SMS contact Number for the specified contact
									String mobileNum = "No data";
									 String smsQ = "select * from contact where contact_id='"+value+"' and status=1";
                                     //ResultSet smsItr = null;
									 //System.out.println("smsQ : "+smsQ);
                                     smsItr=statement2.executeQuery(smsQ);
                                     //ContactEntity contact = null;
                                     while(smsItr.next())
                                     {
                                           //contact = (ContactEntity)contactItr.next();
                                           //if(contact.getPrimary_mobile_number()!=null)
                                                 mobileNum = smsItr.getString("Primary_Moblie_Number");
									}
									
									if(mobileNum == null){ mobileNum = "No data";
										subscribersResultMap.put(contactEntry.getKey().toString().trim(),"NONE");}
									
									subscribersResultMap.put(contactEntry.getKey().toString().trim(), value+"|"+mobileNum);
								}
							}
					
							responseObj.put(entry.getKey().toString().trim(), subscribersResultMap);
							String ns3Query="",ns3select="";
							ns3select="select * from MAlertSubsriber_acountdata where AssetID='"+vin+"'";
							ns3rs=statement3.executeQuery(ns3select);
							if(ns3rs.next())
							{
								if(entry.getKey().toString().equalsIgnoreCase("Subscriber1"))
								{
									ns3Query="update MAlertSubsriber_acountdata set Subscriber1='\""+subscribersResultMap+"\"' where AssetID='"+vin+"'";
								}
								if(entry.getKey().toString().equalsIgnoreCase("Subscriber2"))
								{
									ns3Query="update MAlertSubsriber_acountdata set Subscriber2='\""+subscribersResultMap+"\"' where AssetID='"+vin+"'";
								}
								if(entry.getKey().toString().equalsIgnoreCase("Subscriber3"))
								{
									ns3Query="update MAlertSubsriber_acountdata set Subscriber3='\""+subscribersResultMap+"\"' where AssetID='"+vin+"'";
								}
							}
							else
							{
							if(entry.getKey().toString().equalsIgnoreCase("Subscriber1"))
							{
								ns3Query="insert into MAlertSubsriber_acountdata values('"+vin+"','\""+subscribersResultMap+"\"',null,null)";
							}
							if(entry.getKey().toString().equalsIgnoreCase("Subscriber2"))
							{
								ns3Query="insert into MAlertSubsriber_acountdata values('"+vin+"',null,'\""+subscribersResultMap+"\"',null)";
							}
							if(entry.getKey().toString().equalsIgnoreCase("Subscriber3"))
							{
								ns3Query="insert into MAlertSubsriber_acountdata values('"+vin+"',null,null,'\""+subscribersResultMap+"\"')";
							}
							}
							int[] ns3Result;
							//System.out.println("ns3Query :"+ns3Query);
							statement3.addBatch(ns3Query);
							ns3Result=statement3.executeBatch();
							
							//ns3Result=statement2.executeUpdate(ns3Query);
							//System.out.println("updated records : "+ns3Result);
						}responseList.add(responseObj);
						
					}
			}catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("Exception occured for the vin :"+vin+" :"+e.getMessage());
			}
			}
			Gson gson = new Gson(); 
			if(responseList!=null)
				response = gson.toJsonTree(
						responseList,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();
		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception occured in SubscriberReportServiceImpl :"+e.getMessage());
			//System.out.println("Exception "+e.getMessage());
		}
		 finally {
			 if(session.isOpen()){
				 session.close();
			 }
			 
				if (smsItr != null)
					try {
						smsItr.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
						fLogger.fatal("SQLException occured in closing resultset : SubscriberReportServiceImpl :"+e1.getMessage());

					}
				if (ns3rs != null)
					try {
						ns3rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
						fLogger.fatal("SQLException occured in closing resultset : SubscriberReportServiceImpl :"+e1.getMessage());

					}
				if (rs != null)
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
						fLogger.fatal("SQLException occured in closing resultset : SubscriberReportServiceImpl :"+e1.getMessage());

					}
				if (statement3 != null)
					try {
						statement3.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
						fLogger.fatal("SQLException occured in closing statement2 : SubscriberReportServiceImpl :"+e1.getMessage());

					}
				if (statement2 != null)
					try {
						statement2.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
						fLogger.fatal("SQLException occured in closing statement2 : SubscriberReportServiceImpl :"+e1.getMessage());

					}
				if (statement != null)
					try {
						statement.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
						fLogger.fatal("SQLException occured in closing statement : SubscriberReportServiceImpl :"+e1.getMessage());

					}

				if (prodConnection != null) {
					try {
						prodConnection.close();
					} catch (SQLException e) {
						e.printStackTrace();
						fLogger.fatal("SQLException occured in closing prodConnection : SubscriberReportServiceImpl :"+e.getMessage());

					}
				}
				

			}
		return response;*/
		//DF100001418.eo
	}


	public String downloadNotificationSubscriberReport(int tenancy,String userId){

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs=null;
		List<HashMap<String,Object>> responseList =new ArrayList<HashMap<String,Object>>();
		String response=null;


		try{
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			List<Integer> tenancyIdList = new LinkedList<Integer>();
			tenancyIdList.add(tenancy);
			String accountIdStringList=new DateUtil().getAccountListForTheTenancy(tenancyIdList);
			String loginId=new CommonUtil().getUserId(userId);
			//String loginId=userId;
			String Query="";
			iLogger.info("downloadNotificationSubscriberReport impl input "+tenancy+" login_Id:"+loginId);
			//DF20190704:Abhishek::added new logic to include custom asset machine group in report.
			if(!checkGroupUser(loginId))
			{	
				Query = "SELECT * FROM MAlertSubsriber_acountdata where AssetId in (select a.serial_number from asset a,account ac,asset_owner_snapshot aos where aos.account_Id in ("+accountIdStringList+") and a.status =1 and a.serial_number = aos.serial_Number)";
			}
			else{
				Query = "SELECT * FROM MAlertSubsriber_acountdata where AssetId in (select a.serial_number from asset a,account ac,custom_asset_group_snapshot cags where cags.user_Id in('"+loginId+"') and a.status =1 and a.serial_number = cags.Asset_Id)";
			}
			iLogger.info(Query);
			rs=statement.executeQuery(Query);
			String vin="";

			while(rs.next())
			{
				try{
					HashMap<String,Object> responseObj=new HashMap<String, Object>();
					String s1="No data",s2="No data",s3="No data";
					vin=rs.getString("AssetID");
					responseObj.put("vin", vin);
					if(rs.getObject("Subscriber1")!=null){
						s1=rs.getObject("Subscriber1").toString();
						responseObj.put("Subscriber1",s1);

					}
					if(rs.getObject("Subscriber2")!=null){
						s2=rs.getObject("Subscriber2").toString();
						responseObj.put("Subscriber2",s2);

					}
					if(rs.getObject("Subscriber3")!=null){
						s3=rs.getObject("Subscriber3").toString();
						responseObj.put("Subscriber3",s3);

					}
					responseList.add(responseObj);
				}catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("Exception occured in SubscriberReportServiceImpl :"+e.getMessage());	
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception occured in SubscriberReportServiceImpl :"+e.getMessage());
			//System.out.println("Exception "+e.getMessage());
		}
		Gson gson = new Gson(); 
		if(responseList!=null)
			response = gson.toJsonTree(
					responseList,
					new TypeToken<List<HashMap<String,Object>>>() {
					}.getType()).toString();
		return response;

	}
	//DF20190523:Abhishek::Metod added to check the user in group user table.
	private boolean checkGroupUser(String loginId) {
		Session session=null;
		boolean status=false;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		try{
			List<Integer> groupIdList = new LinkedList<Integer>();

			session = HibernateUtil.getSessionFactory().openSession();
			Query queryGroupUser = session.createQuery("from GroupUserMapping where contact_id ='"+loginId+"'");
			Iterator groupUserItr = queryGroupUser.list().iterator();
			status= groupUserItr.hasNext();
		}
		catch(Exception e)
		{
			fLogger.fatal(" Exception in retriving data from Group user:"+e);

		}
		finally
		{
			try
			{
				if(session!=null && session.isOpen())
				{
					session.close();
				}
			}
			catch(Exception e){
				fLogger.fatal("Exception in closing the AssetDashboard session::"+e);
			}

		}
		long endTime = System.currentTimeMillis();
		iLogger.info("Time required to fetch the record from group user for loginId::"+loginId+" in ms :"+(endTime-startTime));
		return status;
	}
}
