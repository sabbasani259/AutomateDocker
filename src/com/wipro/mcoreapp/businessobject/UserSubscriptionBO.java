/*
 * JCB6239, JCB6240 : 20220829 : Dhiraj K : AssetSaleFromD2C session issue
 */
package com.wipro.mcoreapp.businessobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.simple.JSONObject;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wipro.mcoreapp.implementation.AlertSubscriptionImpl;
import com.wipro.mcoreapp.implementation.AlertSubscriptionProxy;

import remote.wise.businessentity.AccountContactMapping;
import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetOwnerSnapshotEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;

public class UserSubscriptionBO 
{
	//Df20171025 @Roopa Changing from orientDB to mysql tables for subscriber details
	public String getAlertSubscriberGroupFromMysql(String assetId)
	{
		String subscriberJSONArray=null;
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	
    	//********************************** STEP1: Get the Subscriber Group for the given assetID from Orient App DB
    	HashMap<String,String> subsGroupContactMap = new HashMap<String,String>();
    	HashMap newGroupContactMap = new HashMap();
    	
    	ConnectMySQL connMySql = new ConnectMySQL();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			conn = connMySql.getConnection();
			stmt = conn.createStatement();
			
			iLogger.info("MCoreApp:AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Get Subscriber Group from OrientDB - START");
			rs = stmt.executeQuery("select * from MAlertSubsriberGroup where AssetID='"+assetId+"'");
			while(rs.next())
			{
				Object subscriberGroup = rs.getObject("SubscriberGroup");
				if(subscriberGroup==null)
				{
					iLogger.info("MCoreApp:AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":No Subscriber Group defined for the VIN");
					return null;
				}
				
				else
				{
					String subscriberJSONString = rs.getObject("SubscriberGroup").toString();
					subsGroupContactMap = new Gson().fromJson(subscriberJSONString, new TypeToken<HashMap<String, Object>>() {}.getType());
					
					subsGroupContactMap.remove("@type");
					subsGroupContactMap.remove("@version");
										
				}
			}
			
			iLogger.info("MCoreApp:AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Get Subscriber Group from OrientDB - END");
		}
		
		catch(Exception e)
		{
			fLogger.fatal("MCoreApp:"+"AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Exception"+e.getMessage());
			return subscriberJSONArray;
		}
		
		finally
		{
			try
			{
				if(rs!=null)
					rs.close();
			
				if(stmt!=null)
					stmt.close();
				
				if(conn!=null)
					conn.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("MCoreApp:"+"AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Exception in closing OrientDB Connection:"+e.getMessage());
			}
			
		}
		
		
		//********************************** STEP2: Get the EmailID and the SMS contact numbers for the contact list of subscribers
		if(subsGroupContactMap!=null && subsGroupContactMap.size()>0)
		{
			//Connect to MySQL
			
			try
			{
				Session session = HibernateUtil.getSessionFactory().openSession();
				
				try
				{
					iLogger.info("MCoreApp:AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Get EmailID and ContactNumber for Subscriber List from MySQL - START");
					for(Map.Entry entry: subsGroupContactMap.entrySet())
					{
						if(entry.getKey().toString().equalsIgnoreCase("@type"))
							continue;
						
						if(entry.getKey().toString().equalsIgnoreCase("@version"))
							continue;
						
						String modeContactJSON = entry.getValue().toString();
						
						
						/*modeContactJSON=modeContactJSON.replaceAll("\\s+","");
						modeContactJSON= modeContactJSON.replaceAll("\\{", "\\{\"");
						modeContactJSON =modeContactJSON.replaceAll("\\=", "\":\"");
						modeContactJSON=modeContactJSON.replaceAll(",", "\",\"");
						modeContactJSON=modeContactJSON.replaceAll("\\}", "\"\\}");*/
						
						/*
						JsonReader reader = new JsonReader(new StringReader(modeContactJSON));
						reader.setLenient(true);*/
						
						HashMap<String,Object> modeContactMap = new Gson().fromJson(modeContactJSON, new TypeToken<HashMap<String, Object>>() {}.getType());
						
						modeContactMap.remove("@type");
						modeContactMap.remove("@version");
						
						for(Map.Entry contactEntry : modeContactMap.entrySet())
						{
							if(contactEntry.getKey().toString().equalsIgnoreCase("@type"))
								continue;
							
							if(contactEntry.getKey().toString().equalsIgnoreCase("@version"))
								continue;
							
							if(contactEntry.getKey().toString().contains("EMAIL"))
							{
								//---------------- Get the EmailId for the specified contact
								//DF20171116: KO369761 - Considering contacts which are active only.
								String emailId = "";
								Query contactQ = session.createQuery("from ContactEntity where contact_id='"+contactEntry.getValue().toString()+"' and active_status=true");
								Iterator contactItr = contactQ.list().iterator();
								ContactEntity contact = null;
								while(contactItr.hasNext())
								{
									contact = (ContactEntity)contactItr.next();
									if(contact.getPrimary_email_id()!=null)
										emailId = contact.getPrimary_email_id();
								}
								
								if(contact == null)
									modeContactMap.put(contactEntry.getKey().toString(),"NONE");
								
								modeContactMap.put(contactEntry.getKey().toString(), contactEntry.getValue().toString()+"|"+emailId);
							}
							
							else if(contactEntry.getKey().toString().contains("SMS"))
							{
								//---------------- Get the SMS contact Number for the specified contact
								String mobileNum = "";
								Query contactQ = session.createQuery("from ContactEntity where contact_id='"+contactEntry.getValue().toString()+"' and active_status=true");
								Iterator contactItr = contactQ.list().iterator();
								ContactEntity contact = null;
								while(contactItr.hasNext())
								{
									contact = (ContactEntity)contactItr.next();
									if(contact.getPrimary_mobile_number()!=null)
										mobileNum = contact.getPrimary_mobile_number();
								}
								
								if(contact == null)
									modeContactMap.put(contactEntry.getKey().toString(),"NONE");
								
								modeContactMap.put(contactEntry.getKey().toString(), contactEntry.getValue().toString()+"|"+mobileNum);
							}
						}
						
						newGroupContactMap.put(entry.getKey().toString(), modeContactMap);
					}
					
					iLogger.info("MCoreApp:AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Get EmailID and ContactNumber for Subscriber List from MySQL - END");
					subscriberJSONArray = new JSONObject(newGroupContactMap).toString();
				}
				
				catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("MCoreApp:"+"AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Exception in getting details from MySQL :"+e.getMessage());
				}
				
				finally
				{
					try
					{
						if(session!=null && session.isOpen())  
							session.close();
					}
					catch(Exception e)
					{
						fLogger.fatal("MCoreApp:"+"AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Exception in closing the hibernate session:"+e);
					}
		

				}
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("MCoreApp:"+"AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Exception in getting Hibernate Session:"+e.getMessage());
				return subscriberJSONArray;
			}
		}
		
		
		return subscriberJSONArray;
	}
	
	//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
	/*public String getAlertSubscriberGroup(String assetId)
	{
		String subscriberJSONArray=null;
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	
    	//********************************** STEP1: Get the Subscriber Group for the given assetID from Orient App DB
    	HashMap<String,String> subsGroupContactMap = new HashMap<String,String>();
    	HashMap newGroupContactMap = new HashMap();
    	
    	OrientAppDbDatasource connObj = new OrientAppDbDatasource();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			conn = connObj.getConnection();
			stmt = conn.createStatement();
			
			iLogger.info("MCoreApp:AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Get Subscriber Group from OrientDB - START");
			rs = stmt.executeQuery("select from MAlertSubsriberGroup where AssetID='"+assetId+"'");
			while(rs.next())
			{
				Object subscriberGroup = (Object)rs.getObject("SubscriberGroup");
				if(subscriberGroup==null)
				{
					iLogger.info("MCoreApp:AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":No Subscriber Group defined for the VIN");
					return null;
				}
				
				else
				{
					ODocument subsGrpObj = (ODocument)subscriberGroup;
					ORecord orecord = subsGrpObj.getRecord();
					String subscriberJSONString = orecord.toJSON();
					subsGroupContactMap = new Gson().fromJson(subscriberJSONString, new TypeToken<HashMap<String, Object>>() {}.getType());
										
				}
			}
			
			iLogger.info("MCoreApp:AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Get Subscriber Group from OrientDB - END");
		}
		
		catch(Exception e)
		{
			fLogger.fatal("MCoreApp:"+"AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Exception"+e.getMessage());
			return subscriberJSONArray;
		}
		
		finally
		{
			try
			{
				if(rs!=null)
					rs.close();
			
				if(stmt!=null)
					stmt.close();
				
				if(conn!=null)
					conn.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("MCoreApp:"+"AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Exception in closing OrientDB Connection:"+e.getMessage());
			}
			
		}
		
		
		//********************************** STEP2: Get the EmailID and the SMS contact numbers for the contact list of subscribers
		if(subsGroupContactMap!=null && subsGroupContactMap.size()>0)
		{
			//Connect to MySQL
			
			try
			{
				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.beginTransaction();
				
				try
				{
					iLogger.info("MCoreApp:AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Get EmailID and ContactNumber for Subscriber List from MySQL - START");
					for(Map.Entry entry: subsGroupContactMap.entrySet())
					{
						if(entry.getKey().toString().equalsIgnoreCase("@type"))
							continue;
						
						if(entry.getKey().toString().equalsIgnoreCase("@version"))
							continue;
						
						String modeContactJSON = entry.getValue().toString();
						modeContactJSON=modeContactJSON.replaceAll("\\s+","");
						modeContactJSON= modeContactJSON.replaceAll("\\{", "\\{\"");
						modeContactJSON =modeContactJSON.replaceAll("\\=", "\":\"");
						modeContactJSON=modeContactJSON.replaceAll(",", "\",\"");
						modeContactJSON=modeContactJSON.replaceAll("\\}", "\"\\}");
						HashMap<String,Object> modeContactMap = new Gson().fromJson(modeContactJSON, new TypeToken<HashMap<String, Object>>() {}.getType());
						
						for(Map.Entry contactEntry : modeContactMap.entrySet())
						{
							if(contactEntry.getKey().toString().equalsIgnoreCase("@type"))
								continue;
							
							if(contactEntry.getKey().toString().equalsIgnoreCase("@version"))
								continue;
							
							if(contactEntry.getKey().toString().contains("EMAIL"))
							{
								//---------------- Get the EmailId for the specified contact
								String emailId = "";
								Query contactQ = session.createQuery("from ContactEntity where contact_id='"+contactEntry.getValue().toString()+"'");
								Iterator contactItr = contactQ.list().iterator();
								while(contactItr.hasNext())
								{
									ContactEntity contact = (ContactEntity)contactItr.next();
									if(contact.getPrimary_email_id()!=null)
										emailId = contact.getPrimary_email_id();
								}
								
								modeContactMap.put(contactEntry.getKey().toString(), contactEntry.getValue().toString()+"|"+emailId);
							}
							
							else if(contactEntry.getKey().toString().contains("SMS"))
							{
								//---------------- Get the SMS contact Number for the specified contact
								String mobileNum = "";
								Query contactQ = session.createQuery("from ContactEntity where contact_id='"+contactEntry.getValue().toString()+"'");
								Iterator contactItr = contactQ.list().iterator();
								while(contactItr.hasNext())
								{
									ContactEntity contact = (ContactEntity)contactItr.next();
									if(contact.getPrimary_mobile_number()!=null)
										mobileNum = contact.getPrimary_mobile_number();
								}
								
								modeContactMap.put(contactEntry.getKey().toString(), contactEntry.getValue().toString()+"|"+mobileNum);
							}
						}
						
						newGroupContactMap.put(entry.getKey().toString(), modeContactMap);
					}
					
					iLogger.info("MCoreApp:AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Get EmailID and ContactNumber for Subscriber List from MySQL - END");
					subscriberJSONArray = new JSONObject(newGroupContactMap).toString();
				}
				
				catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("MCoreApp:"+"AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Exception in getting details from MySQL :"+e.getMessage());
				}
				
				finally
				{
					try
					{
						if(session!=null && session.isOpen())  
							if(session.getTransaction().isActive())
							{
								session.getTransaction().commit();
							}
					}
					catch(Exception e)
					{
						fLogger.fatal("MCoreApp:"+"AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Exception in comitting the hibernate session:"+e);
					}
		
					if(session.isOpen())
					{
						if(session.getTransaction().isActive())
							session.flush();
						session.close();
					}

				}
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("MCoreApp:"+"AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Exception in getting Hibernate Session:"+e.getMessage());
				return subscriberJSONArray;
			}
		}
		
		
		return subscriberJSONArray;
	}*/
	
	
	//**************************************************************************************************************************************************
	//Df20171025 @Roopa Changing from orientDB to mysql tables for subscriber details
	//DF20190321:mani:notification subscriber traceability, adding the loginId parameter,to invoke traceability method from within
	public String setSubscriberGroupDetailsToMysql(String assetId, String subscriberGroupJSON,String loginId)
	{
		String setStatus="SUCCESS";
		
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		
    	ConnectMySQL connObj = new ConnectMySQL();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String prevSubcriberMapJSON="";
		
		try
		{
			conn = connObj.getConnection();
			stmt = conn.createStatement();
			
			iLogger.info("MCoreApp:AlertSubscription:setSubscriberGroupDetails:VIN:"+assetId+":Set Subscriber Group in MAlertSubsriberGroup - START");
			
			HashMap<String,Object> groupContactMap = new Gson().fromJson(subscriberGroupJSON, new TypeToken<HashMap<String, Object>>() {}.getType());
			
			for(Map.Entry entry : groupContactMap.entrySet())
			{
				//To handle the Malformed JSON Exception when contact id includes special characters like / or .
				String value=entry.getValue().toString();
				
				value=value.replaceAll("\\s+","");
				value= value.replaceAll("\\{", "\\{\"");
				value =value.replaceAll("\\=", "\":\"");
				value=value.replaceAll(",", "\",\"");
				value=value.replaceAll("\\}", "\"\\}");
				
				HashMap<String,String> modeContactMap = new Gson().fromJson(value, new TypeToken<HashMap<String, String>>() {}.getType());
				
				
				int update=0;
				rs = stmt.executeQuery("select * from MAlertSubsriberGroup where AssetID='"+assetId+"'");
				if(rs.next())
				{
					// DF20190321:mani:notification subscriber traceability, to hold
					// the previous data and compare with the new data before
					// updating the traceability
					prevSubcriberMapJSON=rs.getObject("SubscriberGroup").toString();
					update=1;
				
					String jsonString = new JSONObject(modeContactMap).toString();
					
					jsonString=jsonString.replaceAll("\\\\","");
					
					String updatequery="Update MAlertSubsriberGroup set SubscriberGroup = JSON_SET(SubscriberGroup, '$."+entry.getKey().toString()+"', '"+new JSONObject(modeContactMap)+
								"') where AssetID='"+assetId+"'";
					
					stmt.executeUpdate(updatequery);
					
//					iLogger.debug("MCoreApp:AlertSubscription:setSubscriberGroupDetails:VIN:"+assetId+":Update Subscriber Group in MAlertSubsriberGroup for :"+entry.getKey().toString());
				}
				
				if(update==0)//New Row to be inserted for the given VIN
				{
					HashMap subscriberGroup = new HashMap();
					subscriberGroup.put(entry.getKey().toString(), modeContactMap);
				
					stmt.executeUpdate("INSERT INTO MAlertSubsriberGroup(AssetID,SubscriberGroup) values('"+assetId+"','"+new JSONObject(subscriberGroup).toString()+"')");
					
//					iLogger.debug("MCoreApp:AlertSubscription:setSubscriberGroupDetails:VIN:"+assetId+":INSERT Subscriber Group into MAlertSubsriberGroup for :"+entry.getKey().toString());
					
				}
			}
			
			iLogger.info("MCoreApp:AlertSubscription:setSubscriberGroupDetails:VIN:"+assetId+":Set Subscriber Group in MAlertSubsriberGroup - END");
			iLogger.info("AlertSubscription:setSubscriberGroupDetails:Tracebility:updating Notification subscriber tracebility :-calling for vin "+assetId);
			String status=new UserSubscriptionBO().updateNSTraceability(assetId, loginId, subscriberGroupJSON,prevSubcriberMapJSON);
			iLogger.info("AlertSubscription:setSubscriberGroupDetails:Tracebility:updating Notification subscriber tracebility status :"+status);
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("MCoreApp:"+"AlertSubscription:setSubscriberGroupDetails:VIN:"+assetId+":Exception"+e.getMessage());
			return "FAILURE";
		}
		
		finally
		{
			try
			{
				if(rs!=null && !rs.isClosed())
					rs.close();
			
				if(stmt!=null && !stmt.isClosed())
					stmt.close();
				
				if(conn!=null && !conn.isClosed())
					conn.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("MCoreApp:"+"AlertSubscription:setSubscriberGroupDetails:VIN:"+assetId+":Exception in closing OrientDB Connection:"+e.getMessage());
			}
			
		}
		
		return setStatus;
		
	}
	
	//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
	/*public String setSubscriberGroupDetails(String assetId, String subscriberGroupJSON)
	{
		String setStatus="SUCCESS";
		
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		
		OrientAppDbDatasource connObj = new OrientAppDbDatasource();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		
		try
		{
			conn = connObj.getConnection();
			stmt = conn.createStatement();
			
			iLogger.info("MCoreApp:AlertSubscription:setSubscriberGroupDetails:VIN:"+assetId+":Set Subscriber Group in MAlertSubsriberGroup - START");
			
			HashMap<String,Object> groupContactMap = new Gson().fromJson(subscriberGroupJSON, new TypeToken<HashMap<String, Object>>() {}.getType());
			
			for(Map.Entry entry : groupContactMap.entrySet())
			{
				//To handle the Malformed JSON Exception when contact id includes special characters like / or .
				String value=entry.getValue().toString();
				value=value.replaceAll("\\s+","");
				value= value.replaceAll("\\{", "\\{\"");
				value =value.replaceAll("\\=", "\":\"");
				value=value.replaceAll(",", "\",\"");
				value=value.replaceAll("\\}", "\"\\}");
				
				HashMap<String,String> modeContactMap = new Gson().fromJson(value, new TypeToken<HashMap<String, String>>() {}.getType());
				modeContactMap.put("@type", "d");
				
				int update=0;
				rs = stmt.executeQuery("select from MAlertSubsriberGroup where AssetID='"+assetId+"'");
				while(rs.next())
				{
					update=1;
					Object subsGroupObj = (Object) rs.getObject("SubscriberGroup");
					
					if(subsGroupObj==null)
					{
						HashMap<String,String> detailsMap = new HashMap<String,String>();
						detailsMap.put("@type", "d");
						
						stmt.executeUpdate("Update MAlertSubsriberGroup set SubscriberGroup="+(new JSONObject(detailsMap)).toString()+" where AssetID='"+assetId+"'");
					}
					String jsonString = new JSONObject(modeContactMap).toString();
					jsonString=jsonString.replaceAll("\\\\","");
					stmt.executeUpdate("Update MAlertSubsriberGroup set SubscriberGroup."+entry.getKey().toString()+"="+jsonString+" where AssetID='"+assetId+"'");
					
					iLogger.debug("MCoreApp:AlertSubscription:setSubscriberGroupDetails:VIN:"+assetId+":Update Subscriber Group in MAlertSubsriberGroup for :"+entry.getKey().toString());
				}
				
				if(update==0)//New Row to be inserted for the given VIN
				{
					HashMap subscriberGroup = new HashMap();
					subscriberGroup.put(entry.getKey().toString(), modeContactMap);
					subscriberGroup.put("@type", "d");
									
					HashMap subscriberGroupMap = new HashMap();
					subscriberGroupMap.put("AssetID", assetId);
					subscriberGroupMap.put("SubscriberGroup", subscriberGroup);
					
					String insertQuery = new JSONObject(subscriberGroupMap).toString();
					insertQuery=insertQuery.replaceAll("\\\\","");
					stmt.execute("INSERT INTO MAlertSubsriberGroup content "+insertQuery);
					
					iLogger.debug("MCoreApp:AlertSubscription:setSubscriberGroupDetails:VIN:"+assetId+":INSERT Subscriber Group into MAlertSubsriberGroup for :"+entry.getKey().toString());
					
				}
			}
			
			iLogger.info("MCoreApp:AlertSubscription:setSubscriberGroupDetails:VIN:"+assetId+":Set Subscriber Group in MAlertSubsriberGroup - END");
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("MCoreApp:"+"AlertSubscription:setSubscriberGroupDetails:VIN:"+assetId+":Exception"+e.getMessage());
			return "FAILURE";
		}
		
		finally
		{
			try
			{
				if(rs!=null)
					rs.close();
			
				if(stmt!=null)
					stmt.close();
				
				if(conn!=null)
					conn.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("MCoreApp:"+"AlertSubscription:setSubscriberGroupDetails:VIN:"+assetId+":Exception in closing OrientDB Connection:"+e.getMessage());
			}
			
		}
		
		return setStatus;
		
	}*/
	
	
	//**************************************************************************************************************************************************
	//Df20171025 @Roopa Changing from orientDB to mysql tables for subscriber details
	
	public String setSubscriptionAuditTrialToMysql(String assetId, String loginId, String subscriberGroupJSON)
	{
			String status="SUCCESS";
			
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
	    	
			ConnectMySQL connObj = new ConnectMySQL();
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;
			
			
			try
			{
				iLogger.info("MCoreApp:AlertSubscription:setSubscriptionAuditTrial:VIN:"+assetId+":Audit Trial Entry for setting/updating subsciber groups - START");
				
				Timestamp createdTimestamp = new Timestamp(new Date().getTime());
				String createdTimeInString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdTimestamp.getTime());
			    HashMap<String,Object> subscriberGroupMap = new Gson().fromJson(subscriberGroupJSON, new TypeToken<HashMap<String, Object>>() {}.getType());
				
				conn = connObj.getConnection();
				stmt = conn.createStatement();
			
				
				stmt.executeUpdate("INSERT INTO MSubscriptionAuditTrial(AssetID,UserID,CreatedTS,SubscriberGroup) values('"+assetId+"','"+loginId+"','"+createdTimeInString+"','"+new JSONObject(subscriberGroupMap).toString()+"')");
				
				iLogger.info("MCoreApp:AlertSubscription:setSubscriptionAuditTrial:VIN:"+assetId+":Audit Trial Entry for setting/updating subsciber groups - END");
				
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("MCoreApp:"+"AlertSubscription:setSubscriberGroupDetails:VIN:"+assetId+":Exception"+e.getMessage());
				return "FAILURE";
			}
			
			finally
			{
				try
				{
					if(rs!=null)
						rs.close();
				
					if(stmt!=null)
						stmt.close();
					
					if(conn!=null)
						conn.close();
				}
				catch(Exception e)
				{
					fLogger.fatal("MCoreApp:"+"AlertSubscription:setSubscriberGroupDetails:VIN:"+assetId+":Exception in closing OrientDB Connection:"+e.getMessage());
				}
				
			}
			
			return status;
	}
	
	//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
	/*public String setSubscriptionAuditTrial(String assetId, String loginId, String subscriberGroupJSON)
	{
			String status="SUCCESS";
			
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
	    	
	    	OrientAppDbDatasource connObj = new OrientAppDbDatasource();
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;
			
			
			try
			{
				iLogger.info("MCoreApp:AlertSubscription:setSubscriptionAuditTrial:VIN:"+assetId+":Audit Trial Entry for setting/updating subsciber groups - START");
				
				Timestamp createdTimestamp = new Timestamp(new Date().getTime());
				String createdTimeInString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdTimestamp.getTime());
				String fieldTypeDeclaration = "CreatedTS=t";
				
				HashMap<String,Object> subscriberGroupMap = new Gson().fromJson(subscriberGroupJSON, new TypeToken<HashMap<String, Object>>() {}.getType());
				subscriberGroupMap.put("@type", "d");
				
				conn = connObj.getConnection();
				stmt = conn.createStatement();
				
				HashMap auditTrialMap = new HashMap();
				auditTrialMap.put("AssetID", assetId);
				auditTrialMap.put("UserID", loginId);
				auditTrialMap.put("CreatedTS", createdTimeInString);
				
				auditTrialMap.put("SubscriberGroup", subscriberGroupMap);
				auditTrialMap.put("@fieldTypes", fieldTypeDeclaration);
				
				
				String insertQuery = new JSONObject(auditTrialMap).toString();
				insertQuery=insertQuery.replaceAll("\\\\","");
				insertQuery = "INSERT INTO MSubscriptionAuditTrial content "+insertQuery;
				
				stmt.execute(insertQuery);
				
				iLogger.info("MCoreApp:AlertSubscription:setSubscriptionAuditTrial:VIN:"+assetId+":Audit Trial Entry for setting/updating subsciber groups - END");
				
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("MCoreApp:"+"AlertSubscription:setSubscriberGroupDetails:VIN:"+assetId+":Exception"+e.getMessage());
				return "FAILURE";
			}
			
			finally
			{
				try
				{
					if(rs!=null)
						rs.close();
				
					if(stmt!=null)
						stmt.close();
					
					if(conn!=null)
						conn.close();
				}
				catch(Exception e)
				{
					fLogger.fatal("MCoreApp:"+"AlertSubscription:setSubscriberGroupDetails:VIN:"+assetId+":Exception in closing OrientDB Connection:"+e.getMessage());
				}
				
			}
			
			return status;
	}*/
	
	//**************************************************************************************************************************************************
	
	//Df20171025 @Roopa Changing from orientDB to mysql tables for subscriber details
	
	public String setCommSubscriptionToMysql(String assetId, String subscriberGroupJSON)
	{
		String status="SUCCESS";
		
		ConnectMySQL connObj = new ConnectMySQL();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		
		try
		{
			iLogger.info("MCoreApp:AlertSubscription:setCommSubscription:VIN:"+assetId+":Set Suscribers for Alert Communication in OrientDB - START");
			
			HashMap<String,Object> groupContactMap = new Gson().fromJson(subscriberGroupJSON, new TypeToken<HashMap<String, Object>>() {}.getType());
			HashMap<String,String> smsSubscribers = new HashMap<String,String>();
			HashMap<String,String> emailSubscribers = new HashMap<String,String>();
			
			
			//**************************** STEP1: Iterate through the Subscriber group to fill in the sms and email subscriber map
			//Ex: 1,deve0011; 2,deve0011|gunj0002
			iLogger.info("MCoreApp:AlertSubscription:setCommSubscription:VIN:"+assetId+":Fill SMS and Email subscribers map - START");
			for(Map.Entry entry : groupContactMap.entrySet())
			{
				//To handle the Malformed JSON Exception when contact id includes special characters like / or .
				String value=entry.getValue().toString();
				value=value.replaceAll("\\s+","");
				value= value.replaceAll("\\{", "\\{\"");
				value =value.replaceAll("\\=", "\":\"");
				value=value.replaceAll(",", "\",\"");
				value=value.replaceAll("\\}", "\"\\}");
				
				HashMap<String,Object> modeContactMap = new Gson().fromJson(value, new TypeToken<HashMap<String, Object>>() {}.getType());
				String subscriberGroup = entry.getKey().toString().trim().replaceAll("Subscriber", "");
				for(Map.Entry modeContact : modeContactMap.entrySet())
				{
					if(modeContact.getKey().toString().contains("SMS"))
					{
						if(smsSubscribers.containsKey(subscriberGroup))
						{
							String newValue = smsSubscribers.get(subscriberGroup)+"|"+modeContact.getValue().toString();
							smsSubscribers.put(subscriberGroup,newValue);
						}
						else
						{
							smsSubscribers.put(subscriberGroup,modeContact.getValue().toString());
						}
					}
					
					if(modeContact.getKey().toString().contains("EMAIL"))
					{
						if(emailSubscribers.containsKey(subscriberGroup))
						{
							String newValue = emailSubscribers.get(subscriberGroup)+"|"+modeContact.getValue().toString();
							emailSubscribers.put(subscriberGroup,newValue);
						}
						else
						{
							emailSubscribers.put(subscriberGroup,modeContact.getValue().toString());
						}
					}
				}
			}
			
			System.out.println("smsSubscribers:"+smsSubscribers);
			System.out.println("emailSubscribers:"+emailSubscribers);
			
			iLogger.info("MCoreApp:AlertSubscription:setCommSubscription:VIN:"+assetId+":Fill SMS and Email subscribers map - END");
			
			
			//************************************* STEP2: Insert the details for SMS and Email Subscribers into MAlertSubscribers in OrientAPP DB
			conn = connObj.getConnection();
			stmt = conn.createStatement();
			
			//------------------- Set the List of SMS Subscribers
			iLogger.info("MCoreApp:AlertSubscription:setCommSubscription:VIN:"+assetId+":Set the Subscriber Details into MAlertSubscribers for SMS - START");
			if(smsSubscribers!=null && smsSubscribers.size()>0)
			{
				rs = stmt.executeQuery("select * from MAlertSubscribers where AssetID='"+assetId+"' and CommMode='SMS'");
				int update=0;
				String subscriberJSONString=null;
				HashMap<String,String >subsGroupContactMap=new HashMap<String, String>();
				if(rs.next())
				{
					update=1;
					
					Object detailsObj = rs.getObject("Details");
				
					
					if(detailsObj!=null)
						 subscriberJSONString = detailsObj.toString();
				
						
					if(subscriberJSONString!=null){
						subsGroupContactMap = new Gson().fromJson(subscriberJSONString, new TypeToken<HashMap<String, Object>>() {}.getType());
					}
						for(Map.Entry entry : smsSubscribers.entrySet())
						{
							subsGroupContactMap.put(entry.getKey().toString(), entry.getValue().toString());
						}
						
					subsGroupContactMap.remove("@version");
					subsGroupContactMap.remove("@type");
						
						stmt.executeUpdate("Update MAlertSubscribers set Details='"+new JSONObject(subsGroupContactMap).toString().replaceAll("\\\\","")+
								"' where AssetID='"+assetId+"' and CommMode='SMS'");
					
				}
				
				if(update==0)
				{
				
					
					stmt.execute("INSERT INTO MAlertSubscribers(AssetID,CommMode,Details) values('"+assetId+"','SMS','"+(new JSONObject(smsSubscribers).toString()).replaceAll("\\\\","")+"')");
				}
			}
			iLogger.info("MCoreApp:AlertSubscription:setCommSubscription:VIN:"+assetId+":Set the Subscriber Details into MAlertSubscribers for SMS - END");
			
			
			//------------------- Set the List of Email Subscribers
			iLogger.info("MCoreApp:AlertSubscription:setCommSubscription:VIN:"+assetId+":Set the Subscriber Details into MAlertSubscribers for Email - START");
			if(emailSubscribers!=null && emailSubscribers.size()>0)
			{
				rs = stmt.executeQuery("select * from MAlertSubscribers where AssetID='"+assetId+"' and CommMode='Email'");
				int update=0;
				String subscriberJSONString=null;
				HashMap<String,String >subsGroupContactMap=new HashMap<String, String>();
				if(rs.next())
				{
					update=1;
					
					Object detailsObj = rs.getObject("Details");
					
						if(detailsObj!=null)
						 subscriberJSONString = detailsObj.toString();
						
						
						if(subscriberJSONString!=null){
						subsGroupContactMap = new Gson().fromJson(subscriberJSONString, new TypeToken<HashMap<String, Object>>() {}.getType());
						}
						for(Map.Entry entry : emailSubscribers.entrySet())
						{
							subsGroupContactMap.put(entry.getKey().toString(), entry.getValue().toString());
						}
						
						stmt.executeUpdate("Update MAlertSubscribers set Details='"+new JSONObject(subsGroupContactMap).toString().replaceAll("\\\\","")+
								"' where AssetID='"+assetId+"' and CommMode='Email'");
					
				}
				
				if(update==0)
				{
					
					
					stmt.executeUpdate("INSERT INTO MAlertSubscribers(AssetID,CommMode,Details) values('"+assetId+"','Email','"+(new JSONObject(emailSubscribers).toString()).replaceAll("\\\\","")+"')");
				}
			}
			iLogger.info("MCoreApp:AlertSubscription:setCommSubscription:VIN:"+assetId+":Set the Subscriber Details into MAlertSubscribers for Email - END");
			
			iLogger.info("MCoreApp:AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Set Suscribers for Alert Communication in OrientDB - END");
			
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("MCoreApp:"+"AlertSubscription:setCommSubscription:VIN:"+assetId+":Exception in Setting details to MAlertSubscribers in OrientDB:"+e.getMessage());
		}
		
		finally
		{
			try
			{
				if(rs!=null)
					rs.close();
			
				if(stmt!=null)
					stmt.close();
				
				if(conn!=null)
					conn.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("MCoreApp:"+"AlertSubscription:setCommSubscription:VIN:"+assetId+":Exception in closing OrientDB Connection:"+e.getMessage());
			}
		}
		
		return status;
	}
	
	//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
	/*public String setCommSubscription(String assetId, String subscriberGroupJSON)
	{
		String status="SUCCESS";
		
		OrientAppDbDatasource connObj = new OrientAppDbDatasource();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		
		try
		{
			iLogger.info("MCoreApp:AlertSubscription:setCommSubscription:VIN:"+assetId+":Set Suscribers for Alert Communication in OrientDB - START");
			
			HashMap<String,Object> groupContactMap = new Gson().fromJson(subscriberGroupJSON, new TypeToken<HashMap<String, Object>>() {}.getType());
			HashMap<String,String> smsSubscribers = new HashMap<String,String>();
			HashMap<String,String> emailSubscribers = new HashMap<String,String>();
			
			
			//**************************** STEP1: Iterate through the Subscriber group to fill in the sms and email subscriber map
			//Ex: 1,deve0011; 2,deve0011|gunj0002
			iLogger.info("MCoreApp:AlertSubscription:setCommSubscription:VIN:"+assetId+":Fill SMS and Email subscribers map - START");
			for(Map.Entry entry : groupContactMap.entrySet())
			{
				//To handle the Malformed JSON Exception when contact id includes special characters like / or .
				String value=entry.getValue().toString();
				value=value.replaceAll("\\s+","");
				value= value.replaceAll("\\{", "\\{\"");
				value =value.replaceAll("\\=", "\":\"");
				value=value.replaceAll(",", "\",\"");
				value=value.replaceAll("\\}", "\"\\}");
				
				HashMap<String,Object> modeContactMap = new Gson().fromJson(value, new TypeToken<HashMap<String, Object>>() {}.getType());
				String subscriberGroup = entry.getKey().toString().trim().replaceAll("Subscriber", "");
				for(Map.Entry modeContact : modeContactMap.entrySet())
				{
					if(modeContact.getKey().toString().contains("SMS"))
					{
						if(smsSubscribers.containsKey(subscriberGroup))
						{
							String newValue = smsSubscribers.get(subscriberGroup)+"|"+modeContact.getValue().toString();
							smsSubscribers.put(subscriberGroup,newValue);
						}
						else
						{
							smsSubscribers.put(subscriberGroup,modeContact.getValue().toString());
						}
					}
					
					if(modeContact.getKey().toString().contains("EMAIL"))
					{
						if(emailSubscribers.containsKey(subscriberGroup))
						{
							String newValue = emailSubscribers.get(subscriberGroup)+"|"+modeContact.getValue().toString();
							emailSubscribers.put(subscriberGroup,newValue);
						}
						else
						{
							emailSubscribers.put(subscriberGroup,modeContact.getValue().toString());
						}
					}
				}
			}
			
			System.out.println("smsSubscribers:"+smsSubscribers);
			System.out.println("emailSubscribers:"+emailSubscribers);
			
			iLogger.info("MCoreApp:AlertSubscription:setCommSubscription:VIN:"+assetId+":Fill SMS and Email subscribers map - END");
			
			
			//************************************* STEP2: Insert the details for SMS and Email Subscribers into MAlertSubscribers in OrientAPP DB
			conn = connObj.getConnection();
			stmt = conn.createStatement();
			
			//------------------- Set the List of SMS Subscribers
			iLogger.info("MCoreApp:AlertSubscription:setCommSubscription:VIN:"+assetId+":Set the Subscriber Details into MAlertSubscribers for SMS - START");
			if(smsSubscribers!=null && smsSubscribers.size()>0)
			{
				rs = stmt.executeQuery("select from MAlertSubscribers where AssetID='"+assetId+"' and CommMode='SMS'");
				int update=0;
				while(rs.next())
				{
					update=1;
					
					Object detailsObj = rs.getObject("Details");
					
					
					if(detailsObj==null)
					{
						HashMap<String,String> detailsMap = new HashMap<String,String>();
						detailsMap.put("@type", "d");
						stmt.executeUpdate("Update MAlertSubscribers set Details="+(new JSONObject(detailsMap)).toString()+" where " +
											"AssetID='"+assetId+"' and CommMode='SMS'");
					}
					
					else
					{
						ODocument detailsDoc = (ODocument)detailsObj;
						ORecord orecord = detailsDoc.getRecord();
						String subscriberJSONString = orecord.toJSON();
						
						//To handle the Malformed JSON Exception when contact id includes special characters like / or .
						subscriberJSONString= subscriberJSONString.replaceAll("\\{", "\\{\"");
						subscriberJSONString =subscriberJSONString.replaceAll("\\=", "\":\"");
						subscriberJSONString=subscriberJSONString.replaceAll(",", "\",\"");
						subscriberJSONString=subscriberJSONString.replaceAll("\\}", "\"\\}");
						
						HashMap<String,String >subsGroupContactMap = new Gson().fromJson(subscriberJSONString, new TypeToken<HashMap<String, Object>>() {}.getType());
						
						for(Map.Entry entry : smsSubscribers.entrySet())
						{
							subsGroupContactMap.put(entry.getKey().toString(), entry.getValue().toString());
						}
						
						subsGroupContactMap.remove("@version");
						
						stmt.executeUpdate("Update MAlertSubscribers set Details="+new JSONObject(subsGroupContactMap).toString().replaceAll("\\\\","")+
								" where AssetID='"+assetId+"' and CommMode='SMS'");
					}
				}
				
				if(update==0)
				{
					smsSubscribers.put("@type", "d");
					
					HashMap alertSubscribers = new HashMap();
					alertSubscribers.put("AssetID", assetId);
					alertSubscribers.put("CommMode", "SMS");
					alertSubscribers.put("Details", smsSubscribers);
					
					stmt.execute("INSERT INTO MAlertSubscribers content "+(new JSONObject(alertSubscribers).toString()).replaceAll("\\\\",""));
				}
			}
			iLogger.info("MCoreApp:AlertSubscription:setCommSubscription:VIN:"+assetId+":Set the Subscriber Details into MAlertSubscribers for SMS - END");
			
			
			//------------------- Set the List of Email Subscribers
			iLogger.info("MCoreApp:AlertSubscription:setCommSubscription:VIN:"+assetId+":Set the Subscriber Details into MAlertSubscribers for Email - START");
			if(emailSubscribers!=null && emailSubscribers.size()>0)
			{
				rs = stmt.executeQuery("select from MAlertSubscribers where AssetID='"+assetId+"' and CommMode='Email'");
				int update=0;
				while(rs.next())
				{
					update=1;
					
					Object detailsObj = rs.getObject("Details");
					if(detailsObj==null)
					{
						HashMap<String,String> detailsMap = new HashMap<String,String>();
						detailsMap.put("@type", "d");
						stmt.executeUpdate("Update MAlertSubscribers set Details="+(new JSONObject(detailsMap)).toString().replaceAll("\\\\","")+" where " +
											"AssetID='"+assetId+"' and CommMode='Email'");
					}
					
					for(Map.Entry entry : emailSubscribers.entrySet())
					{
						stmt.executeUpdate("Update MAlertSubscribers set Details.'"+entry.getKey().toString()+"'='"+entry.getValue().toString()+"'" +
								" where AssetID='"+assetId+"' and CommMode='Email'");
					}
					
					else
					{
						ODocument detailsDoc = (ODocument)detailsObj;
						ORecord orecord = detailsDoc.getRecord();
						String subscriberJSONString = orecord.toJSON();
						
						//To handle the Malformed JSON Exception when contact id includes special characters like / or .
						subscriberJSONString= subscriberJSONString.replaceAll("\\{", "\\{\"");
						subscriberJSONString =subscriberJSONString.replaceAll("\\=", "\":\"");
						subscriberJSONString=subscriberJSONString.replaceAll(",", "\",\"");
						subscriberJSONString=subscriberJSONString.replaceAll("\\}", "\"\\}");
						
						HashMap<String,String >subsGroupContactMap = new Gson().fromJson(subscriberJSONString, new TypeToken<HashMap<String, Object>>() {}.getType());
						
						for(Map.Entry entry : emailSubscribers.entrySet())
						{
							subsGroupContactMap.put(entry.getKey().toString(), entry.getValue().toString());
						}
						
						subsGroupContactMap.remove("@version");
						
						stmt.executeUpdate("Update MAlertSubscribers set Details="+new JSONObject(subsGroupContactMap).toString().replaceAll("\\\\","")+
								" where AssetID='"+assetId+"' and CommMode='Email'");
					}
				}
				
				if(update==0)
				{
					emailSubscribers.put("@type", "d");
					
					HashMap alertSubscribers = new HashMap();
					alertSubscribers.put("AssetID", assetId);
					alertSubscribers.put("CommMode", "Email");
					alertSubscribers.put("Details", emailSubscribers);
					
					stmt.execute("INSERT INTO MAlertSubscribers content "+(new JSONObject(alertSubscribers).toString()).replaceAll("\\\\",""));
				}
			}
			iLogger.info("MCoreApp:AlertSubscription:setCommSubscription:VIN:"+assetId+":Set the Subscriber Details into MAlertSubscribers for Email - END");
			
			iLogger.info("MCoreApp:AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Set Suscribers for Alert Communication in OrientDB - END");
			
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("MCoreApp:"+"AlertSubscription:setCommSubscription:VIN:"+assetId+":Exception in Setting details to MAlertSubscribers in OrientDB:"+e.getMessage());
		}
		
		finally
		{
			try
			{
				if(rs!=null)
					rs.close();
			
				if(stmt!=null)
					stmt.close();
				
				if(conn!=null)
					conn.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("MCoreApp:"+"AlertSubscription:setCommSubscription:VIN:"+assetId+":Exception in closing OrientDB Connection:"+e.getMessage());
			}
		}
		
		return status;
	}*/
	
	
	
	//**************************************************************************************************************************************************
	public List<SubscriberContactEntity> getContactDetails(String assetId, String subscriberBlock)
	{
		List<SubscriberContactEntity> contactList = new LinkedList<SubscriberContactEntity>();
		
		//NOTE: Here we are going with the Assumption that Subscriber1 will be the OEM Users+RO Users corresponding to the zone to which the machine belongs to
		//Subscriber2 will be the Dealer Users corresponding to the Dealer to whom the machine is tagged to
		//Subscriber3 will be the Customer Users corresponding to the Customer to whom the machine is tagged to
		Logger fLogger = FatalLoggerClass.logger;
    	
		try
		{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			List<AccountEntity> accountIdList = new LinkedList<AccountEntity>();
			
			List<AccountEntity> accountIdList1 = new LinkedList<AccountEntity>();
			String accountType=null;
			
			String accountCode=null;
			
			try
			{
				//DF20170901: KO369761 - OEM Global included in accountType.
				if(subscriberBlock.equalsIgnoreCase("Subscriber1"))
					accountType = "'OEM Global','OEM','OEM RO'";
							
				else if(subscriberBlock.equalsIgnoreCase("Subscriber2"))
					accountType = "'Dealer'";
				
				else if(subscriberBlock.equalsIgnoreCase("Subscriber3"))
					accountType = "'Customer'";
				
				Query accountListQ = session.createQuery("from AssetOwnerSnapshotEntity where serialNumber='"+assetId+"' and accountType in ("+accountType+")");
				Iterator accountListItr = accountListQ.list().iterator();
				while(accountListItr.hasNext())
				{
					AssetOwnerSnapshotEntity assetOwner = (AssetOwnerSnapshotEntity)accountListItr.next();
					accountIdList.add(assetOwner.getAccountId());
				}
				
				
				if(accountIdList.size()==0)
					return contactList;
				
				
				//DF20160526 @Roopa N  : Retrieving based on the account code and not the account id.
				int accId=accountIdList.get(0).getAccount_id();
				
				//-----------------------To get the accountCode from the account table for the given account id
				Query accountCodeQ = session.createQuery("from AccountEntity a where a.account_id="+accId+"");
				Iterator actCodeItr = accountCodeQ.list().iterator();
				while(actCodeItr.hasNext())
				{
					AccountEntity act= (AccountEntity)actCodeItr.next();
					
					//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
					//accountCode=act.getAccountCode();
					accountCode=act.getMappingCode();
				}
				
				//-----------------------To get the first account id that is mapped for the given accountCode from the account table(Because contact id is mapped for the first account id)
				Query accountIdListQ = session.createQuery("from AccountEntity a where a.mappingCode='"+accountCode+"'");
				//accountIdListQ.setMaxResults(1);
				Iterator actListItr = accountIdListQ.list().iterator();
				while(actListItr.hasNext())
				{
					AccountEntity act= (AccountEntity)actListItr.next();
					accountIdList1.add(act);
				}
				
				//--------------------------- Get the list of contacts that are tagged to the obtained account list
				Query contactListQ = session.createQuery("select b from AccountContactMapping a, ContactEntity b where a.account_id in (:list)" +
						" and a.contact_id=b.contact_id and b.active_status=1").setParameterList("list", accountIdList1);
				Iterator contactListItr = contactListQ.list().iterator();
				while(contactListItr.hasNext())
				{
					ContactEntity contact = (ContactEntity)contactListItr.next();

					SubscriberContactEntity subscriberContact = new SubscriberContactEntity();
					subscriberContact.setContactID(contact.getContact_id());
						
					if(contact.getPrimary_email_id()!=null)
						subscriberContact.setEMAIL(contact.getPrimary_email_id());
					else
						subscriberContact.setEMAIL("NULL");
							
					subscriberContact.setSMS(contact.getPrimary_mobile_number());
						
					contactList.add(subscriberContact);
					
				}
				
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("MCoreApp:"+"AlertSubscription:getContactDetails:VIN:"+assetId+":subscriberBlock:"+subscriberBlock+":Exception in getting details from MySQL :"+e.getMessage());
			}
			
			finally
			{
				try
				{
					if(session!=null && session.isOpen())  
						if(session.getTransaction().isActive())
						{
							session.getTransaction().commit();
						}
				}
				catch(Exception e)
				{
					fLogger.fatal("MCoreApp:"+"AlertSubscription:getContactDetails:VIN:"+assetId+":subscriberBlock:"+subscriberBlock+":Exception in comitting the hibernate session:"+e);
				}
	
				if(session.isOpen())
				{
					if(session.getTransaction().isActive())
						session.flush();
					session.close();
				}

			}
		}
		
		catch(Exception e)
		{
			fLogger.fatal("MCoreApp:"+"AlertSubscription:getContactDetails:VIN:"+assetId+":subscriberBlock:"+subscriberBlock+":Exception in getting Hibernate Session:"+e.getMessage());
		}
		
		return contactList;
	}
	
	
	
	//**************************************************************************************************************************************************
	
	
	//Df20171025 @Roopa Changing from orientDB to mysql tables for subscriber details
	
	public String setDefaultSubscribersToMysql(String assetId)
	{
		String status = "SUCCESS";
		
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	
    	//***************************************************** STEP1: Get the Subscriber Group for the given assetID from Orient App DB
    	ConnectMySQL connObj = new ConnectMySQL();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		HashMap<String,String> subGroupContactMap = new HashMap<String,String>();
		
		iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Get Subscriber Group from OrientDB - START");
		
		try
		{
			conn = connObj.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("select * from MAlertSubsriberGroup where AssetID='"+assetId+"'");
			while(rs.next())
			{
				Object subscriberGroup = rs.getObject("SubscriberGroup");
				if(subscriberGroup==null)
					break;
				
				/*ODocument subsGrpObj = (ODocument)subscriberGroup;
				ORecord orecord = subsGrpObj.getRecord();
				String subscriberJSONString = orecord.toJSON();*/
				String subscriberJSONString =subscriberGroup.toString();
				HashMap<String,Object> subscriberGroupMap = new Gson().fromJson(subscriberJSONString, new TypeToken<HashMap<String, Object>>() {}.getType());
				
				
				for(Map.Entry entry: subscriberGroupMap.entrySet())
				{
					if(entry.getKey().toString().equalsIgnoreCase("@type"))
						continue;
					
					if(entry.getKey().toString().equalsIgnoreCase("@version"))
						continue;
					
					HashMap<String,String> modeContactMap = new Gson().fromJson(entry.getValue().toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
					modeContactMap.remove("@type");
					modeContactMap.remove("@version");
					//subGroupContactMap.put(entry.getKey().toString(), modeContactMap.values().toArray()[0].toString());
					
					//DF20160802 @Roopa Fix for swapping of contact id's 
					
					for(int i=0;i<modeContactMap.size();i++){
						String contactId= modeContactMap.values().toArray()[i].toString();
						
						if(! contactId.equalsIgnoreCase("NONE")){
							subGroupContactMap.put(entry.getKey().toString(), contactId);
							continue;
						}
					}
					
				}
			}
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("MCoreApp:"+"AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Exception"+e.getMessage());
			return "FAILURE";
		}
		
		finally
		{
			try
			{
				if(rs!=null)
					rs.close();
			
				if(stmt!=null)
					stmt.close();
				
				if(conn!=null)
					conn.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("MCoreApp:"+"AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Exception in closing OrientDB Connection:"+e.getMessage());
			}
		}
		iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Get Subscriber Group from OrientDB - END : subGroupContactMap:"+subGroupContactMap);
		
		
		
		//***************************************************** STEP2: Get the Ownership details and the corresponding default contact from MySQL
		
		try
		{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			//JCB6239.sn
	        if(session.getTransaction().isActive() && session.isDirty())
	        {
	            iLogger.info("Opening a new session");
	            session = HibernateUtil.getSessionFactory().openSession();
	        }
	        //JCB6239.en
			session.beginTransaction();
			
			try
			{
				Query currentOwnerQ = session.createQuery(" from AssetOwnerSnapshotEntity where serialNumber='"+assetId+"'");
				Iterator currentOwnerItr = currentOwnerQ.list().iterator();
				while(currentOwnerItr.hasNext())
				{
					AssetOwnerSnapshotEntity ownerSnapshot = (AssetOwnerSnapshotEntity)currentOwnerItr.next();
					
					//----------------------------------------------- CASE1: For Subscriber1 
					//DF20170901: KO369761 - OEM Global included in accountType.
					if(ownerSnapshot.getAccountType().equalsIgnoreCase("OEM")||ownerSnapshot.getAccountType().equalsIgnoreCase("OEM Global"))
					{
//						iLogger.debug("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":SET subscriber for OEM - START");
						
						//------- If Subscriber1 is set for the machine, don't modify else add an OEM user to Subscriber1 -- Email1
						if(subGroupContactMap.containsKey("Subscriber1"))
						{
							//---------- To handle re-processing of JCB Roll Off
							iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Subscriber1 already set to :"+subGroupContactMap.get("Subscriber1"));
							continue;
						}
						
						else
						{
							//Get an OEM user who is an tenancy admin with emailId not null and is first in the chronological sequence of contactId
							Query oemContactQ = session.createQuery("select b.contact_id from AccountContactMapping a, ContactEntity b " +
									" where a.contact_id=b.contact_id and a.account_id='"+ownerSnapshot.getAccountId().getAccount_id()+"'" +
									" and b.active_status=1 and b.is_tenancy_admin=1 and b.primary_email_id is not null order by b.contact_id");
							Iterator oemContactItr = oemContactQ.list().iterator();
							if(oemContactItr.hasNext())
							{
								String subscriber1Contact = (String)oemContactItr.next();
								HashMap<String,String> internalJson = new HashMap<String,String>();
								internalJson.put("EMAIL1", subscriber1Contact);
								//DF20180424 @Roopa Adding Default SMS1 for OEM user.
							     internalJson.put("SMS1", subscriber1Contact); 
								HashMap subscriberJson = new HashMap();
								subscriberJson.put("Subscriber1", internalJson);
								
								iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Set Email1 of Subscriber1 to :"+subscriber1Contact);
								
								new AlertSubscriptionImpl().setSubscriberGroupDetails(assetId, "Batch", new JSONObject(subscriberJson).toString());
							}
						}
						
//						iLogger.debug("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":SET subscriber for OEM - END");
						
					}
					
					
					//--------------------------------------------------- CASE2: For Subscriber2
					if(ownerSnapshot.getAccountType().equalsIgnoreCase("Dealer"))
					{
//						iLogger.debug("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":SET subscriber for Dealer - START");
						
						//------- If Subscriber2 is set for the machine, check whether it is the current dealer account user. If yes, dont modify the same
						//(To handle re-processing of Gateout/Sale details, where in already set subscribers should not be reset to default).
						if(subGroupContactMap.containsKey("Subscriber2"))
						{
							//Get the accountId of the set subscriber
							Query accQ = session.createQuery(" from AccountContactMapping where contact_id='"+subGroupContactMap.get("Subscriber2").toString()+"'");
							Iterator accItr = accQ.list().iterator();
							int subscriberAccId=0;
							
							while(accItr.hasNext())
							{
								AccountContactMapping accContact = (AccountContactMapping)accItr.next();
								subscriberAccId = accContact.getAccount_id().getAccount_id();
							}
							
							if(ownerSnapshot.getAccountId().getAccount_id()== subscriberAccId)
							{
								iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Subscriber2 already set to :"+subGroupContactMap.get("Subscriber2")+" : User of the current Dealer account:"+subscriberAccId);
								continue;
							}
							
						
						}
						
						//--------------------- If the Subsriber2 is not set (First time Gateout/Diret sale) / Subscriber2 is set with the user account 
						//which is different from the current dealer account, then reset the Subscriber2 (To handle PrimaryDelaerTransfer and D2D/C2C sale)
						//Get an dealer user who is an tenancy admin with emailId not null and is first in the chronological sequence of contactId
						Query dealerContactQ = session.createQuery("select b.contact_id from AccountContactMapping a, ContactEntity b " +
								" where a.contact_id=b.contact_id and a.account_id='"+ownerSnapshot.getAccountId().getAccount_id()+"'" +
								" and b.active_status=1 and b.is_tenancy_admin=1 and b.primary_email_id is not null order by b.contact_id");
						Iterator dealerContactItr = dealerContactQ.list().iterator();
						String subscriber2Contact = null;
						if(dealerContactItr.hasNext())
						{
							subscriber2Contact = (String)dealerContactItr.next();
							HashMap<String,String> internalJson = new HashMap<String,String>();
							internalJson.put("SMS1", subscriber2Contact);
							internalJson.put("EMAIL1", subscriber2Contact);
							HashMap subscriberJson = new HashMap();
							subscriberJson.put("Subscriber2", internalJson);
							
							iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Set SMS1 and Email1 of Subscriber2 to :"+subscriber2Contact);
							
							new AlertSubscriptionImpl().setSubscriberGroupDetails(assetId, "Batch", new JSONObject(subscriberJson).toString());
						}
						
						if(subscriber2Contact==null)
						{
							//If there is no Admin User with valid email Id, then set only the SMS subscriber for the same
							dealerContactQ = session.createQuery("select b.contact_id from AccountContactMapping a, ContactEntity b " +
									" where a.contact_id=b.contact_id and a.account_id='"+ownerSnapshot.getAccountId().getAccount_id()+"'" +
									" and b.active_status=1 and b.is_tenancy_admin=1 order by b.contact_id");
							dealerContactItr = dealerContactQ.list().iterator();
							if(dealerContactItr.hasNext())
							{
								subscriber2Contact = (String)dealerContactItr.next();
								HashMap<String,String> internalJson = new HashMap<String,String>();
								internalJson.put("SMS1", subscriber2Contact);
								HashMap subscriberJson = new HashMap();
								subscriberJson.put("Subscriber2", internalJson);
								
								iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Set SMS1 of Subscriber2 to :"+subscriber2Contact+"; Email1 not set since no TA exists with valid email ID");
								
								new AlertSubscriptionImpl().setSubscriberGroupDetails(assetId, "Batch", new JSONObject(subscriberJson).toString());
							}
						}
						
//						iLogger.debug("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":SET subscriber for Dealer - END");
						
					}
					
					
					//--------------------------------------------------- CASE3: For Subscriber3
					if(ownerSnapshot.getAccountType().equalsIgnoreCase("Customer"))
					{

//						iLogger.debug("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":SET subscriber for Customer - START");
						
						//------- If Subscriber3 is set for the machine, check whether it is the current Customer account user. If yes, don't modify the same
						//(To handle re-processing of Gate-out(Direct Sale)/Sale details, where in already set subscribers should not be reset to default).
						if(subGroupContactMap.containsKey("Subscriber3"))
						{
							//Get the accountId of the set subscriber
							Query accQ = session.createQuery(" from AccountContactMapping where contact_id='"+subGroupContactMap.get("Subscriber3").toString()+"'");
							Iterator accItr = accQ.list().iterator();
							int subscriberAccId=0;
							
							while(accItr.hasNext())
							{
								AccountContactMapping accContact = (AccountContactMapping)accItr.next();
								subscriberAccId = accContact.getAccount_id().getAccount_id();
							}
							
							if(ownerSnapshot.getAccountId().getAccount_id()== subscriberAccId)
							{
								iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Subscriber3 already set to :"+subGroupContactMap.get("Subscriber3")+" : User of the current Customer account:"+subscriberAccId);
								continue;
							}
							
						
						}
						
						//--------------------- If the Subscriber3 is not set (First time Gateout/Diret sale) / Subscriber2 is set with the user account 
						//which is different from the current customer account, then reset the Subscriber3 (To handle C2C sale)
						//Get an customer user who is an tenancy admin with emailId not null and is first in the chronological sequence of contactId
						Query custContactQ = session.createQuery("select b.contact_id from AccountContactMapping a, ContactEntity b " +
								" where a.contact_id=b.contact_id and a.account_id='"+ownerSnapshot.getAccountId().getAccount_id()+"'" +
								" and b.active_status=1 and b.is_tenancy_admin=1 and b.primary_email_id is not null order by b.contact_id");
						Iterator custContactItr = custContactQ.list().iterator();
						String subscriber3Contact = null;
						if(custContactItr.hasNext())
						{
							subscriber3Contact = (String)custContactItr.next();
							HashMap<String,String> internalJson = new HashMap<String,String>();
							internalJson.put("SMS1", subscriber3Contact);
							internalJson.put("EMAIL1", subscriber3Contact);
							HashMap subscriberJson = new HashMap();
							subscriberJson.put("Subscriber3", internalJson);
							
							iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Set SMS1 and Email1 of Subscriber3 to :"+subscriber3Contact);
							
							new AlertSubscriptionImpl().setSubscriberGroupDetails(assetId, "Batch", new JSONObject(subscriberJson).toString());
						}
						
						if(subscriber3Contact==null)
						{
							//If there is no Admin User with valid email Id, then set only the SMS subscriber for the same
							custContactQ = session.createQuery("select b.contact_id from AccountContactMapping a, ContactEntity b " +
									" where a.contact_id=b.contact_id and a.account_id='"+ownerSnapshot.getAccountId().getAccount_id()+"'" +
									" and b.active_status=1 and b.is_tenancy_admin=1 order by b.contact_id");
							custContactItr = custContactQ.list().iterator();
							if(custContactItr.hasNext())
							{
								subscriber3Contact = (String)custContactItr.next();
								HashMap<String,String> internalJson = new HashMap<String,String>();
								internalJson.put("SMS1", subscriber3Contact);
								HashMap subscriberJson = new HashMap();
								subscriberJson.put("Subscriber3", internalJson);
								
								iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Set SMS1 of Subscriber3 to :"+subscriber3Contact+"; Email1 not set since no TA exists with valid email ID");
								
								new AlertSubscriptionImpl().setSubscriberGroupDetails(assetId, "Batch", new JSONObject(subscriberJson).toString());
							}
						}
						
						//Df20180124 @Roopa Multiple BP code changes, if the subscriber3 contact is not created, (because of duplicate mobile num, than checking and taking, if the given customer code is already mapped to some different cusomer, than taking the user's contact)
						  
						if(subscriber3Contact==null)
						{
							
							custContactQ = session.createQuery("select c.contact_id from AccountContactMapping ac ,ContactEntity c " +
									"where ac.account_id in (select account_id from AccountEntity where mappingCode " +
									"in(select mappingCode from AccountEntity where account_id='"+ownerSnapshot.getAccountId().getAccount_id()+"')) " +
									"and ac.contact_id=c.contact_id  and c.active_status=1 and c.is_tenancy_admin=1 and c.primary_email_id is not null order by c.contact_id");
							
							custContactItr = custContactQ.list().iterator();
							if(custContactItr.hasNext())
							{
								subscriber3Contact = (String)custContactItr.next();
								HashMap<String,String> internalJson = new HashMap<String,String>();
								internalJson.put("SMS1", subscriber3Contact);
								internalJson.put("EMAIL1", subscriber3Contact);
								HashMap subscriberJson = new HashMap();
								subscriberJson.put("Subscriber3", internalJson);
								
								iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Set SMS1 and Email1 of Subscriber3 to :"+subscriber3Contact);
								
								new AlertSubscriptionImpl().setSubscriberGroupDetails(assetId, "Batch", new JSONObject(subscriberJson).toString());
							}
						}
						
						//if primary email id is not there fill only SMS contact
						
						if(subscriber3Contact==null)
						{
							
							custContactQ = session.createQuery("select c.contact_id from AccountContactMapping ac ,ContactEntity c " +
									"where ac.account_id in (select account_id from AccountEntity where mappingCode " +
									"in(select mappingCode from AccountEntity where account_id='"+ownerSnapshot.getAccountId().getAccount_id()+"')) " +
									"and ac.contact_id=c.contact_id  and c.active_status=1 and c.is_tenancy_admin=1 order by c.contact_id");
							
							custContactItr = custContactQ.list().iterator();
							if(custContactItr.hasNext())
							{
								subscriber3Contact = (String)custContactItr.next();
								HashMap<String,String> internalJson = new HashMap<String,String>();
								internalJson.put("SMS1", subscriber3Contact);
								HashMap subscriberJson = new HashMap();
								subscriberJson.put("Subscriber3", internalJson);
								
								iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Set SMS1 of Subscriber3 to :"+subscriber3Contact+"; Email1 not set since no TA exists with valid email ID");
								
								new AlertSubscriptionImpl().setSubscriberGroupDetails(assetId, "Batch", new JSONObject(subscriberJson).toString());
							}
						}
						
//						iLogger.debug("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":SET subscriber for Customer - END");
									
					}
				}
				
				
			}
			
			catch(Exception e)
			{
				fLogger.fatal("MCoreApp:"+"AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Exception in getting details from MySQL :"+e.getMessage());
			}
			
			finally
			{
				try
				{
					if(session!=null && session.isOpen())  
						if(session.getTransaction().isActive())
						{
							session.getTransaction().commit();
						}
				}
				catch(Exception e)
				{
					fLogger.fatal("MCoreApp:"+"AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Exception in comitting the hibernate session:"+e);
				}
	
				if(session.isOpen())
				{
					if(session.getTransaction().isActive())
						session.flush();
					session.close();
				}

			}
			
		}
		
		catch(Exception e)
		{
			fLogger.fatal("MCoreApp:"+"AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Exception in getting Hibernate Session:"+e.getMessage());
			return "FAILURE";
		}
		
		return status;
	}
	
	//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
	/*public String setDefaultSubscribers(String assetId)
	{
		String status = "SUCCESS";
		
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	
    	//***************************************************** STEP1: Get the Subscriber Group for the given assetID from Orient App DB
    	OrientAppDbDatasource connObj = new OrientAppDbDatasource();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		HashMap<String,String> subGroupContactMap = new HashMap<String,String>();
		
		iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Get Subscriber Group from OrientDB - START");
		
		try
		{
			conn = connObj.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("select from MAlertSubsriberGroup where AssetID='"+assetId+"'");
			while(rs.next())
			{
				Object subscriberGroup = (Object)rs.getObject("SubscriberGroup");
				if(subscriberGroup==null)
					break;
				
				ODocument subsGrpObj = (ODocument)subscriberGroup;
				ORecord orecord = subsGrpObj.getRecord();
				String subscriberJSONString = orecord.toJSON();
				HashMap<String,Object> subscriberGroupMap = new Gson().fromJson(subscriberJSONString, new TypeToken<HashMap<String, Object>>() {}.getType());
				
				
				for(Map.Entry entry: subscriberGroupMap.entrySet())
				{
					if(entry.getKey().toString().equalsIgnoreCase("@type"))
						continue;
					
					if(entry.getKey().toString().equalsIgnoreCase("@version"))
						continue;
					
					HashMap<String,String> modeContactMap = new Gson().fromJson(entry.getValue().toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
					modeContactMap.remove("@type");
					modeContactMap.remove("@version");
					//subGroupContactMap.put(entry.getKey().toString(), modeContactMap.values().toArray()[0].toString());
					
					//DF20160802 @Roopa Fix for swapping of contact id's 
					
					for(int i=0;i<modeContactMap.size();i++){
						String contactId= modeContactMap.values().toArray()[i].toString();
						
						if(! contactId.equalsIgnoreCase("NONE")){
							subGroupContactMap.put(entry.getKey().toString(), contactId);
							continue;
						}
					}
					
				}
			}
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("MCoreApp:"+"AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Exception"+e.getMessage());
			return "FAILURE";
		}
		
		finally
		{
			try
			{
				if(rs!=null)
					rs.close();
			
				if(stmt!=null)
					stmt.close();
				
				if(conn!=null)
					conn.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("MCoreApp:"+"AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Exception in closing OrientDB Connection:"+e.getMessage());
			}
		}
		iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Get Subscriber Group from OrientDB - END : subGroupContactMap:"+subGroupContactMap);
		
		
		
		//***************************************************** STEP2: Get the Ownership details and the corresponding default contact from MySQL
		
		try
		{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			try
			{
				Query currentOwnerQ = session.createQuery(" from AssetOwnerSnapshotEntity where serialNumber='"+assetId+"'");
				Iterator currentOwnerItr = currentOwnerQ.list().iterator();
				while(currentOwnerItr.hasNext())
				{
					AssetOwnerSnapshotEntity ownerSnapshot = (AssetOwnerSnapshotEntity)currentOwnerItr.next();
					
					//----------------------------------------------- CASE1: For Subscriber1 
					//DF20170901: KO369761 - OEM Global included in accountType.
					if(ownerSnapshot.getAccountType().equalsIgnoreCase("OEM")||ownerSnapshot.getAccountType().equalsIgnoreCase("OEM Global"))
					{
						iLogger.debug("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":SET subscriber for OEM - START");
						
						//------- If Subscriber1 is set for the machine, don't modify else add an OEM user to Subscriber1 -- Email1
						if(subGroupContactMap.containsKey("Subscriber1"))
						{
							//---------- To handle re-processing of JCB Roll Off
							iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Subscriber1 already set to :"+subGroupContactMap.get("Subscriber1"));
							continue;
						}
						
						else
						{
							//Get an OEM user who is an tenancy admin with emailId not null and is first in the chronological sequence of contactId
							Query oemContactQ = session.createQuery("select b.contact_id from AccountContactMapping a, ContactEntity b " +
									" where a.contact_id=b.contact_id and a.account_id='"+ownerSnapshot.getAccountId().getAccount_id()+"'" +
									" and b.active_status=1 and b.is_tenancy_admin=1 and b.primary_email_id is not null order by b.contact_id");
							Iterator oemContactItr = oemContactQ.list().iterator();
							if(oemContactItr.hasNext())
							{
								String subscriber1Contact = (String)oemContactItr.next();
								HashMap<String,String> internalJson = new HashMap<String,String>();
								internalJson.put("EMAIL1", subscriber1Contact);
								HashMap subscriberJson = new HashMap();
								subscriberJson.put("Subscriber1", internalJson);
								
								iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Set Email1 of Subscriber1 to :"+subscriber1Contact);
								
								new AlertSubscriptionImpl().setSubscriberGroupDetails(assetId, "Batch", new JSONObject(subscriberJson).toString());
							}
						}
						
						iLogger.debug("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":SET subscriber for OEM - END");
						
					}
					
					
					//--------------------------------------------------- CASE2: For Subscriber2
					if(ownerSnapshot.getAccountType().equalsIgnoreCase("Dealer"))
					{
						iLogger.debug("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":SET subscriber for Dealer - START");
						
						//------- If Subscriber2 is set for the machine, check whether it is the current dealer account user. If yes, dont modify the same
						//(To handle re-processing of Gateout/Sale details, where in already set subscribers should not be reset to default).
						if(subGroupContactMap.containsKey("Subscriber2"))
						{
							//Get the accountId of the set subscriber
							Query accQ = session.createQuery(" from AccountContactMapping where contact_id='"+subGroupContactMap.get("Subscriber2").toString()+"'");
							Iterator accItr = accQ.list().iterator();
							int subscriberAccId=0;
							
							while(accItr.hasNext())
							{
								AccountContactMapping accContact = (AccountContactMapping)accItr.next();
								subscriberAccId = accContact.getAccount_id().getAccount_id();
							}
							
							if(ownerSnapshot.getAccountId().getAccount_id()== subscriberAccId)
							{
								iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Subscriber2 already set to :"+subGroupContactMap.get("Subscriber2")+" : User of the current Dealer account:"+subscriberAccId);
								continue;
							}
							
						
						}
						
						//--------------------- If the Subsriber2 is not set (First time Gateout/Diret sale) / Subscriber2 is set with the user account 
						//which is different from the current dealer account, then reset the Subscriber2 (To handle PrimaryDelaerTransfer and D2D/C2C sale)
						//Get an dealer user who is an tenancy admin with emailId not null and is first in the chronological sequence of contactId
						Query dealerContactQ = session.createQuery("select b.contact_id from AccountContactMapping a, ContactEntity b " +
								" where a.contact_id=b.contact_id and a.account_id='"+ownerSnapshot.getAccountId().getAccount_id()+"'" +
								" and b.active_status=1 and b.is_tenancy_admin=1 and b.primary_email_id is not null order by b.contact_id");
						Iterator dealerContactItr = dealerContactQ.list().iterator();
						String subscriber2Contact = null;
						if(dealerContactItr.hasNext())
						{
							subscriber2Contact = (String)dealerContactItr.next();
							HashMap<String,String> internalJson = new HashMap<String,String>();
							internalJson.put("SMS1", subscriber2Contact);
							internalJson.put("EMAIL1", subscriber2Contact);
							HashMap subscriberJson = new HashMap();
							subscriberJson.put("Subscriber2", internalJson);
							
							iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Set SMS1 and Email1 of Subscriber2 to :"+subscriber2Contact);
							
							new AlertSubscriptionImpl().setSubscriberGroupDetails(assetId, "Batch", new JSONObject(subscriberJson).toString());
						}
						
						if(subscriber2Contact==null)
						{
							//If there is no Admin User with valid email Id, then set only the SMS subscriber for the same
							dealerContactQ = session.createQuery("select b.contact_id from AccountContactMapping a, ContactEntity b " +
									" where a.contact_id=b.contact_id and a.account_id='"+ownerSnapshot.getAccountId().getAccount_id()+"'" +
									" and b.active_status=1 and b.is_tenancy_admin=1 order by b.contact_id");
							dealerContactItr = dealerContactQ.list().iterator();
							if(dealerContactItr.hasNext())
							{
								subscriber2Contact = (String)dealerContactItr.next();
								HashMap<String,String> internalJson = new HashMap<String,String>();
								internalJson.put("SMS1", subscriber2Contact);
								HashMap subscriberJson = new HashMap();
								subscriberJson.put("Subscriber2", internalJson);
								
								iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Set SMS1 of Subscriber2 to :"+subscriber2Contact+"; Email1 not set since no TA exists with valid email ID");
								
								new AlertSubscriptionImpl().setSubscriberGroupDetails(assetId, "Batch", new JSONObject(subscriberJson).toString());
							}
						}
						
						iLogger.debug("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":SET subscriber for Dealer - END");
						
					}
					
					
					//--------------------------------------------------- CASE3: For Subscriber3
					if(ownerSnapshot.getAccountType().equalsIgnoreCase("Customer"))
					{

						iLogger.debug("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":SET subscriber for Customer - START");
						
						//------- If Subscriber3 is set for the machine, check whether it is the current Customer account user. If yes, don't modify the same
						//(To handle re-processing of Gate-out(Direct Sale)/Sale details, where in already set subscribers should not be reset to default).
						if(subGroupContactMap.containsKey("Subscriber3"))
						{
							//Get the accountId of the set subscriber
							Query accQ = session.createQuery(" from AccountContactMapping where contact_id='"+subGroupContactMap.get("Subscriber3").toString()+"'");
							Iterator accItr = accQ.list().iterator();
							int subscriberAccId=0;
							
							while(accItr.hasNext())
							{
								AccountContactMapping accContact = (AccountContactMapping)accItr.next();
								subscriberAccId = accContact.getAccount_id().getAccount_id();
							}
							
							if(ownerSnapshot.getAccountId().getAccount_id()== subscriberAccId)
							{
								iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Subscriber3 already set to :"+subGroupContactMap.get("Subscriber3")+" : User of the current Customer account:"+subscriberAccId);
								continue;
							}
							
						
						}
						
						//--------------------- If the Subscriber3 is not set (First time Gateout/Diret sale) / Subscriber2 is set with the user account 
						//which is different from the current customer account, then reset the Subscriber3 (To handle C2C sale)
						//Get an customer user who is an tenancy admin with emailId not null and is first in the chronological sequence of contactId
						Query custContactQ = session.createQuery("select b.contact_id from AccountContactMapping a, ContactEntity b " +
								" where a.contact_id=b.contact_id and a.account_id='"+ownerSnapshot.getAccountId().getAccount_id()+"'" +
								" and b.active_status=1 and b.is_tenancy_admin=1 and b.primary_email_id is not null order by b.contact_id");
						Iterator custContactItr = custContactQ.list().iterator();
						String subscriber3Contact = null;
						if(custContactItr.hasNext())
						{
							subscriber3Contact = (String)custContactItr.next();
							HashMap<String,String> internalJson = new HashMap<String,String>();
							internalJson.put("SMS1", subscriber3Contact);
							internalJson.put("EMAIL1", subscriber3Contact);
							HashMap subscriberJson = new HashMap();
							subscriberJson.put("Subscriber3", internalJson);
							
							iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Set SMS1 and Email1 of Subscriber3 to :"+subscriber3Contact);
							
							new AlertSubscriptionImpl().setSubscriberGroupDetails(assetId, "Batch", new JSONObject(subscriberJson).toString());
						}
						
						if(subscriber3Contact==null)
						{
							//If there is no Admin User with valid email Id, then set only the SMS subscriber for the same
							custContactQ = session.createQuery("select b.contact_id from AccountContactMapping a, ContactEntity b " +
									" where a.contact_id=b.contact_id and a.account_id='"+ownerSnapshot.getAccountId().getAccount_id()+"'" +
									" and b.active_status=1 and b.is_tenancy_admin=1 order by b.contact_id");
							custContactItr = custContactQ.list().iterator();
							if(custContactItr.hasNext())
							{
								subscriber3Contact = (String)custContactItr.next();
								HashMap<String,String> internalJson = new HashMap<String,String>();
								internalJson.put("SMS1", subscriber3Contact);
								HashMap subscriberJson = new HashMap();
								subscriberJson.put("Subscriber3", internalJson);
								
								iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Set SMS1 of Subscriber3 to :"+subscriber3Contact+"; Email1 not set since no TA exists with valid email ID");
								
								new AlertSubscriptionImpl().setSubscriberGroupDetails(assetId, "Batch", new JSONObject(subscriberJson).toString());
							}
						}
						
						iLogger.debug("MCoreApp:AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":SET subscriber for Customer - END");
									
					}
				}
				
				
			}
			
			catch(Exception e)
			{
				fLogger.fatal("MCoreApp:"+"AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Exception in getting details from MySQL :"+e.getMessage());
			}
			
			finally
			{
				try
				{
					if(session!=null && session.isOpen())  
						if(session.getTransaction().isActive())
						{
							session.getTransaction().commit();
						}
				}
				catch(Exception e)
				{
					fLogger.fatal("MCoreApp:"+"AlertSubscription:getSubscriptionDetails:VIN:"+assetId+":Exception in comitting the hibernate session:"+e);
				}
	
				if(session.isOpen())
				{
					if(session.getTransaction().isActive())
						session.flush();
					session.close();
				}

			}
			
		}
		
		catch(Exception e)
		{
			fLogger.fatal("MCoreApp:"+"AlertSubscription:setDefaultSubscribers:VIN:"+assetId+":Exception in getting Hibernate Session:"+e.getMessage());
			return "FAILURE";
		}
		
		return status;
	}*/
	
	
	//**************************************************************************************************************************************************
	public String migrateSubscribersFromMySQL(String assetId, String subscriberGroup)
	{
		String status="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		
    	//***************************************************** Setting the default OEM subscribers
		if(subscriberGroup.equalsIgnoreCase("Subscriber1"))
		{
			long startTime = System.currentTimeMillis();
			iLogger.debug("MCoreApp:AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": for Subscriber1 - START");
			List<String> assetList = new LinkedList<String>();
			String subscriberJsonString  = null;
			
			try
			{
				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.beginTransaction();
					
				try
				{
					//-------------------------------- STEP1: Get the List of all active VINs from asset table
					iLogger.debug("MCoreApp:AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": for Subscriber1: Get the List of Active Assets");
					if(assetId==null)
					{
						Query assetQ = session.createQuery("from AssetEntity where active_status=1");
						Iterator assetItr = assetQ.list().iterator();
						while(assetItr.hasNext())
						{
							AssetEntity asset = (AssetEntity)assetItr.next();
							assetList.add(asset.getSerial_number().getSerialNumber());
						}
					}
					
					else
					{
						assetList.add(assetId);
					}
					
					//-------------------------------- STEP2: Get the contactId of the OEM user to be set as default subscriber
//					iLogger.debug("MCoreApp:AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": for Subscriber1: Get the OEM ContactId for Subscriber1");
					
					Query oemContactQ = session.createQuery("select contact_id from ContactEntity  " +
							" where role in (1,2,4) " +
							" and active_status=1 and is_tenancy_admin=1 and primary_email_id is not null order by contact_id");
					Iterator oemContactItr = oemContactQ.list().iterator();
					String subscriber1Contact = null;
					if(oemContactItr.hasNext())
					{
						subscriber1Contact = (String)oemContactItr.next();
					}
					
					HashMap<String,String> internalJson = new HashMap<String,String>();
					internalJson.put("EMAIL1", subscriber1Contact);
					HashMap subscriberJson = new HashMap();
					subscriberJson.put("Subscriber1", internalJson);
					subscriberJsonString = new JSONObject(subscriberJson).toString();
					
 				}
				catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("MCoreApp:"+"AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": subscriberGroup:"+subscriberGroup+":" +
							"Exception in getting details from MySQL :"+e.getMessage());
				}
					
				finally
				{
					try
					{
						if(session!=null && session.isOpen())  
							if(session.getTransaction().isActive())
							{
								session.getTransaction().commit();
							}
					}
					catch(Exception e)
					{
						fLogger.fatal("MCoreApp:"+"AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": subscriberGroup:"+subscriberGroup+":" +
								":Exception in comitting the hibernate session:"+e);
					}
			
					if(session.isOpen())
					{
						if(session.getTransaction().isActive())
							session.flush();
							session.close();
					}

				}
					
			}
				
			catch(Exception e)
			{
				fLogger.fatal("MCoreApp:"+"AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": subscriberGroup:"+subscriberGroup+":" +
						" Exception in getting Hibernate Session:"+e.getMessage());
				return "FAILURE";
			}
		
//			iLogger.debug("MCoreApp:AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": for Subscriber1: Insert Subscribers to OrientDB - START ");
			
			//-------------------- Spawn 100 threads in parallel Using Executor service- One thread each for a VIN
			ExecutorService executor = Executors.newFixedThreadPool(100);
			
			for(int i=0; i<assetList.size(); i++)
			{
				//-------------Once all the 100 threads are spawned, wait for all 100 to finish before spawning another set of 100 threads	
				if(i!=0 && i%100==0)
				{
					executor.shutdown();  
//					iLogger.debug("MCoreApp:AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": for Subscriber1: Before spawning thread "+i+", wait for current Executor Threads to finish");
					while (!executor.isTerminated()) {   }  
				
					executor = Executors.newFixedThreadPool(100);
				}
					
//				iLogger.debug("MCoreApp:AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": for Subscriber1: Spawning thread:"+i);
				
				AlertSubscriptionProxy proxyObj = new AlertSubscriptionProxy();
				proxyObj.assetId=assetList.get(i);
				proxyObj.subscriberJsonString=subscriberJsonString;
					
				Callable<String> worker = proxyObj;
				Future<String> submit =executor.submit(worker);
			}
				
			executor.shutdown();  
			while (!executor.isTerminated()) {   }  	
				
			iLogger.info("MCoreApp:AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": for Subscriber1: Insert Subscribers to OrientDB - END : Total Assets:"+assetList.size());
			
			
			long endTime = System.currentTimeMillis();
			iLogger.info("MCoreApp:AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": for Subscriber1 - END : Total Assets:"+assetList.size()+": Total Time in ms:"+(endTime-startTime));
			
		}
		
		else 
		{
			iLogger.info("MCoreApp:AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": for subscriberGroup:"+subscriberGroup+": Insert Subscribers to OrientDB - START");
			List<Integer> priorityList = new LinkedList<Integer>();
			String roleId=null;
			if (subscriberGroup.equalsIgnoreCase("Subscriber2"))
			{
				priorityList.add(1);
				priorityList.add(2);
				roleId="5,6";
			}
			
			else if (subscriberGroup.equalsIgnoreCase("Subscriber3"))
			{
				priorityList.add(2);
				priorityList.add(3);
				roleId="7,8";
			}
			
			else
			{
				//Invalid Subscriber Group Specified 
				fLogger.fatal("MCoreApp:"+"AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": subscriberGroup:"+subscriberGroup+":Invalid Subsriber Group specified:"+subscriberGroup);
				return "FAILURE";
			}
			
			HashMap<String,HashMap<String,String>> assetIdmodeContactMap = new HashMap<String,HashMap<String,String>>();
			for(int i=0; i<priorityList.size(); i++)
			{
				
				//------------------------------ STEP1: Get the details from Event Subscription table for the selected priority
				try
				{
					Session session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.beginTransaction();
					
					try
					{
						String eventSubsQString = " select a.serialNumber, b.contact_id, b.primary_email_id " +
											" from EventSubscriptionMapping a, ContactEntity b " +
											" where a.contactId=b.contact_id and b.active_status=1 " +
											" and b.role in (" +roleId+") " +
											" and a.priority="+priorityList.get(i);
						
						if(assetId!=null)
						{
							eventSubsQString=eventSubsQString+" and a.serialNumber='"+assetId+"'";
						}
						Query eventSubsQ = session.createQuery(eventSubsQString);
						Iterator eventSubsItr = eventSubsQ.list().iterator();
						Object[] result = null;
						while(eventSubsItr.hasNext())
						{
							result = (Object[]) eventSubsItr.next();
							AssetEntity asset = (AssetEntity) result[0];
							
							HashMap<String,String> modeContactMap = new HashMap<String,String>();
							
							if (subscriberGroup.equalsIgnoreCase("Subscriber2"))
							{
								//Then priority1 is SMS1 and EMAIL1 ; priority2 is SMS2 and EMAIL2
								if(priorityList.get(i)==1)
								{
									modeContactMap.put("SMS1", result[1].toString());
									if(result[2]!=null)
										modeContactMap.put("EMAIL1", result[1].toString());
								}
								else if (priorityList.get(i)==2)
								{
									modeContactMap.put("SMS2", result[1].toString());
									if(result[2]!=null)
										modeContactMap.put("EMAIL2", result[1].toString());
								}
							}
							
							else if (subscriberGroup.equalsIgnoreCase("Subscriber3"))
							{
								//Then priority2 is SMS1 and EMAIL1 ; priority3 is SMS2 and EMAIL2
								if(priorityList.get(i)==2)
								{
									modeContactMap.put("SMS1", result[1].toString());
									if(result[2]!=null)
										modeContactMap.put("EMAIL1", result[1].toString());
								}
								else if (priorityList.get(i)==3)
								{
									modeContactMap.put("SMS2", result[1].toString());
									if(result[2]!=null)
										modeContactMap.put("EMAIL2", result[1].toString());
								}
							}
							
							if(assetIdmodeContactMap.containsKey(asset.getSerial_number().getSerialNumber()))
							{
								HashMap<String,String> prevMap = assetIdmodeContactMap.get(asset.getSerial_number().getSerialNumber());
								prevMap.putAll(modeContactMap);
							}
							
							else
							{
								assetIdmodeContactMap.put(asset.getSerial_number().getSerialNumber(), modeContactMap);
							}
						}
						
					}
					
					catch(Exception e)
					{
						e.printStackTrace();
						fLogger.fatal("MCoreApp:"+"AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": subscriberGroup:"+subscriberGroup+":" +
								"Exception in getting details from MySQL :"+e.getMessage());
					}
					
					finally
					{
						try
						{
							if(session!=null && session.isOpen())  
								if(session.getTransaction().isActive())
								{
									session.getTransaction().commit();
								}
						}
						catch(Exception e)
						{
							fLogger.fatal("MCoreApp:"+"AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": subscriberGroup:"+subscriberGroup+":" +
									":Exception in comitting the hibernate session:"+e);
						}
			
						if(session.isOpen())
						{
							if(session.getTransaction().isActive())
								session.flush();
							session.close();
						}

					}
				}
				
				catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("MCoreApp:"+"AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": subscriberGroup:"+subscriberGroup+":" +
							" Exception in getting Hibernate Session:"+e.getMessage());
				
				}
			}
			
			//---------------------------------------------- STEP2: Insert the details into OrientDB 
			
			//-------------------- Spawn 100 threads in parallel Using Executor service- One thread each for a VIN
			ExecutorService executor = Executors.newFixedThreadPool(100);
			int i=0;
			
			for(Map.Entry entry: assetIdmodeContactMap.entrySet())
			{
				if(i!=0 && i%100==0)
				{
					executor.shutdown();  
//					iLogger.debug("MCoreApp:AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": for subscriberGroup:"+subscriberGroup+": Before spawning thread "+i+", wait for current Executor Threads to finish");
					while (!executor.isTerminated()) {   }  
				
					executor = Executors.newFixedThreadPool(100);
				}
				
//				iLogger.debug("MCoreApp:AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": for subscriberGroup:"+subscriberGroup+": Spawning thread:"+i);
				
				HashMap<String,String> modeContactMap = (HashMap<String,String>)entry.getValue();
				HashMap finalMap = new HashMap();
				finalMap.put(subscriberGroup, modeContactMap);
				
				AlertSubscriptionProxy proxyObj = new AlertSubscriptionProxy();
				proxyObj.assetId=entry.getKey().toString();
				proxyObj.subscriberJsonString=new JSONObject(finalMap).toString();
					
				Callable<String> worker = proxyObj;
				Future<String> submit =executor.submit(worker);
				
				i++;
				
			}
			
			executor.shutdown();  
			while (!executor.isTerminated()) {   }  	
				
			iLogger.info("MCoreApp:AlertSubscription: migrateSubscribersFromMySQL:VIN:"+assetId+": for subscriberGroup:"+subscriberGroup+": Insert Subscribers to OrientDB - END : Total Assets:"+assetIdmodeContactMap.size());
		}
				
		return status;
	}
	//DF20190321:mani:notification subscriber traceability
		public String updateNSTraceability(String assetId, String loginId, String subscriberGroupJSON,String prevSubcriberMapJSON){
			String status="SUCCESS";
			Connection conn=null,conn1=null;
			Statement stmt=null,stmt1=null;
			ResultSet res=null,res1=null;
			Logger fLogger = FatalLoggerClass.logger;
	    	Logger iLogger = InfoLoggerClass.logger;
			try{
				ConnectMySQL connObj = new ConnectMySQL();
				conn=connObj.getConnection();
				conn1=connObj.getConnection_tracebility();
				iLogger.info("AlertSubscription:setSubscriberGroupDetails:Tracebility::VIN:"+assetId+":tracebility - START");
				HashMap<String,Object> groupContactMap = new Gson().fromJson(subscriberGroupJSON, new TypeToken<HashMap<String, Object>>() {}.getType());
				String subscriberType="";
				boolean traceFlag=false;
				HashMap<String,Object> prevSubscriberMap=null;
				HashMap<String,String> prevContactMap=null;
				if(!prevSubcriberMapJSON.equalsIgnoreCase("")){
				prevSubscriberMap = new Gson().fromJson(prevSubcriberMapJSON, new TypeToken<HashMap<String, Object>>() {}.getType());
				
				for(Map.Entry<String, Object> subscribertype:groupContactMap.entrySet())
				{
					//@Shajesh : 20201210 : Handling the null pointer excepion in  subscribertype
					String value1= "";
					String key1="";
					if(subscribertype != null && subscribertype.getValue() !=null ){
						 value1=subscribertype.getValue().toString();
						 key1=subscribertype.getKey();
					}
					/*String value1=subscribertype.getValue().toString();*/
					/*	String key1=subscribertype.getKey();*/
					//@Shajesh : 20201210 : //@Shajesh : 20201210 : Handling the null pointer excepion in  prevSubscriberMap
					String value2 = "";
					/*String value2=prevSubscriberMap.get(key1).toString();*/
					if(prevSubscriberMap != null && prevSubscriberMap.get(key1)!=null){
					value2=prevSubscriberMap.get(key1).toString();
					}
					
					value1=value1.replaceAll("\\s+","");
					value1= value1.replaceAll("\\{", "\\{\"");
					value1 =value1.replaceAll("\\=", "\":\"");
					value1=value1.replaceAll(",", "\",\"");
					value1=value1.replaceAll("\\}", "\"\\}");
					HashMap<String,String> newContactMap = new Gson().fromJson(value1, new TypeToken<HashMap<String, String>>() {}.getType());
					prevContactMap = new Gson().fromJson(value2, new TypeToken<HashMap<String, String>>() {}.getType());
					if(prevSubscriberMap.containsKey(key1))
					{
						for(Map.Entry<String, String> insubscriber:prevContactMap.entrySet())
						{
							String contactType=insubscriber.getKey();
							if(newContactMap.containsKey(contactType))
							{
								if(!(insubscriber.getValue().equals(newContactMap.get(contactType))))
								{
									traceFlag=true;
									subscriberType=subscriberType+key1;break;
								}
							}
						}
					}
				}
				}
				else{
					traceFlag=true;
				}
				if(traceFlag)
				{
					String monthYear="";
					Date date = new Date();
					Calendar cal=Calendar.getInstance();
					cal.setTime(date);
					int year=cal.get(Calendar.YEAR);
					int month=cal.get(Calendar.MONTH);
					
					/*String monthYear=year+"0"+(month+1);*/
					//@Shajesh : 2020-12-09 : notification_subscriber_traceability partition key  getting populated incorrectly , modified the code
					if(month < 9){
						monthYear=year+"0"+(month+1);						
						}else{
							monthYear =year+""+(month+1);
							}
					int updatedMonth=Integer.valueOf(monthYear);
					Timestamp currentTimeStamp = new Timestamp(date.getTime());
					/*String jsonobject="";*/
					//@Shajesh : 2020-12-09 : notification_subscriber_traceability insertion/updation query for previous_data cannot be blank string so initalized it as null
					String jsonobject=null;
					if(prevSubscriberMap!=null){
					jsonobject=new JSONObject(prevSubscriberMap).toString();
					jsonobject= jsonobject.replaceAll("\\\"\\{", "\\{");
					jsonobject=jsonobject.replaceAll("\\}\"", "\\}");
					}
					String selectIntrace="select * from notification_subscriber_traceability where serial_number=\'"+assetId+"\'";
					stmt1=conn1.createStatement();
					res1=stmt1.executeQuery(selectIntrace);
					if(res1.next())
					{
						String updateQuery="update notification_subscriber_traceability " +
								"set updated_on=\'"+currentTimeStamp+
									"\',modified_by=\'"+loginId+
									"\',updated_data=\'"+new JSONObject(groupContactMap).toString()+
											"\',previous_data=\'"+jsonobject+
													"\',updating_for=\'"+subscriberType+
													"\',updated_month="+updatedMonth+" where serial_number=\'"+assetId+"\'";
						iLogger.info("AlertSubscription:setSubscriberGroupDetails:Tracebility::VIN:"+assetId+":update tracebility - query :"+updateQuery);
						stmt1.executeUpdate(updateQuery);
					}
					else
					{
						String insert="INSERT INTO notification_subscriber_traceability" +
								" (serial_number,created_on,updated_on,modified_by,updated_data,previous_data,updating_for,updated_month)" +
								"values(\'"+assetId+"\',\'"+currentTimeStamp+"\',\'"+currentTimeStamp+"\',\'"+loginId+"\',\'"+new JSONObject(groupContactMap).toString()+"\',\'"+jsonobject+"\',\'"+subscriberType+"\',"+updatedMonth+")";
						iLogger.info("AlertSubscription:setSubscriberGroupDetails:Tracebility::VIN:"+assetId+":insert into tracebility - query :"+insert);
						stmt1.executeUpdate(insert);
					}
					
				}
				
			}catch(Exception e)
			{
				status="FAILURE";
				e.printStackTrace();
				fLogger.fatal("Exception occured :: AlertSubscription:setSubscriberGroupDetails:Tracebility::VIN:"+assetId+":"+e.getMessage());

			}
			finally{
				if(res!=null)
				{
					try {
						res.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if(stmt!=null)
				{
					try {
						stmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if(conn!=null)
				{
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if(res1!=null)
				{
					try {
						res1.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if(stmt1!=null)
				{
					try {
						stmt1.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if(conn1!=null)
				{
					try {
						conn1.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				
			}
			return status;
			
		}
	
}
