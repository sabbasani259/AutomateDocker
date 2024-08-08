package remote.wise.handler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/*import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.wipro.mcoreapp.util.OrientAppDbDatasource;*/

import remote.wise.businessentity.ContactEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class UserAlertSubscription 
{
	public String status="SUCCESS";
	
	//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
	/*public List<String> getSubscribedUsersForAlert(String serialNumber, Timestamp transactionTime, String eventCode, String eventTypeCode,String commMode)
	{
		List<String> finalUserList = new LinkedList<String>();
		
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	
		OrientAppDbDatasource connObj = new OrientAppDbDatasource();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		List<String> subscribedUsers = new LinkedList<String>();
			
		long startTime = System.currentTimeMillis();
		iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventCode+":"+commMode+":Get the SubscriberList from OrientDB - START");
		
		
		try
		{
			conn = connObj.getConnection();
			stmt = conn.createStatement();
			
			//------------------------------- STEP1: Get the List of users subscribed for SMS notification for the obtained Machine
			//These details will be maintained in OrientDB (Application Data) a
			rs = stmt.executeQuery(" select Details from MAlertSubscribers where AssetID='"+serialNumber+"' " +
						"and CommMode='"+commMode+"'");
			//Details will have JSON object like 	{"1":"deve0011|mahe0123","2":"aaha0112|arka0012","@type":"document"}
			while(rs.next())
			{
				Object subscriberDetails = rs.getObject("Details");
				
				if(subscriberDetails==null)
				{
					iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventCode+":"+commMode+":No subscribers defined");
					return finalUserList;
					
				}
				ODocument subsGrpObj = (ODocument)subscriberDetails;
				ORecord orecord = subsGrpObj.getRecord();
				String subscriberDetailsJSON = orecord.toJSON();
				
				if(subscriberDetailsJSON!=null && subscriberDetailsJSON.trim().length()>0)
				{
					HashMap subsLevelUserMap = new Gson().fromJson(subscriberDetailsJSON.toString(), new TypeToken<HashMap>(){}.getType());
					
					if(subsLevelUserMap.containsKey("@type"))
						subsLevelUserMap.remove("@type");
					
					Set<Entry> subscriberSet = subsLevelUserMap.entrySet();
					
					for(Map.Entry subscribers: subscriberSet)
					{
						subscribedUsers.addAll( Arrays.asList(subscribers.getValue().toString().split("\\|")) );
					}
				}
				
				if(subscribedUsers.size()>0 && subscribedUsers.contains(null))
				{
					subscribedUsers.remove(null);
					subscribedUsers.remove("NONE");
				}
			}
			
			if(subscribedUsers.size()==0)
			{
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventCode+":"+commMode+":No SMS Subscribers defined for the Asset");
				return finalUserList;
			}
			iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventCode+":"+commMode+":Get the SubscriberList from OrientDB - END");
			
			
			
			//------------------------------- STEP2: Get the User preference set for the obtained Alert for the machine and filter the Un-subscribed Users
			iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventCode+":"+commMode+":Get the User Preference for the subscribers :"+subscribedUsers+" - START");
			
			outer:
			for(int i=0; i<subscribedUsers.size(); i++)
			{
				int pref=0;
				int prefSet=0;
				String frequency = null;
				
				//---------------- STEP2.1: Check for the User Subscription at the Asset Level
				//Sample record from MUserAlertPref
				//('deve0011','SMS','AssetID',{"@type":"d","HAR3DX001":[{"@type":"d","Alert":"001","Pref":1,"Freq":"1440"},{"@type":"d","Alert":"002","Pref":0,"Freq":"-1"}],
				//"HAR3DX002":[{"@type":"d","Alert":"001","Pref":0,"Freq":"-1"},{"@type":"d","Alert":"002","Pref":1, "Freq":"-1"}]})
				rs = stmt.executeQuery("select from (select expand(Preference."+serialNumber+") from MUserAlertPref where UserID='"+subscribedUsers.get(i)+"' and " +
						"CommMode='"+commMode+"' and PrefLevel='AssetID' ) where Alert='"+eventCode+"'");
				while(rs.next())
				{
					prefSet=1;
					pref = rs.getInt("Pref");
					frequency =rs.getString("Freq");
					
					if(pref==0)
					{
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventCode+":"+commMode+": "+commMode+" Preference set to 0 for the User:"+subscribedUsers.get(i));
						continue outer;
					}
					
					
				}
				
				
				//---------------- STEP2.2: Check for the User Subscription at the Alert Level
				if(prefSet==0)
				{
					//Ex. record for the pref set at alert level ('deve0011','SMS','Alert',{"@type":"d","001":{"Pref":1,"Freq":-1},"003":{"Pref":1,"Freq":1440}, ,"004":{"Pref":1}})
					rs = stmt.executeQuery("select '"+eventCode+"'.Pref as Pref, " +
							" '"+eventCode+"'.Freq as Freq from (select expand(Preference) from MUserAlertPref where " +
							"UserID='"+subscribedUsers.get(i)+"' and CommMode='"+commMode+"' and PrefLevel='Alert') ");
					while(rs.next())
					{
						prefSet=1;
						pref = rs.getInt("Pref");
						frequency =rs.getString("Freq");
						
					}
					
					if(prefSet==1 && pref==0)
					{
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventCode+":"+commMode+": "+commMode+" Preference set to 0 for the User:"+subscribedUsers.get(i));
						continue outer;
					}
				}
				
				//---------------- STEP4.3:  Check for the User Subscription at the AlertType Level
				if(prefSet==0)
				{
					//Ex. record for the pref set at alertType level ('deve0011','SMS','AlertType',{"@type":"d","001":{"Pref":1,"Freq":-1},"003":{"Pref":1,"Freq":1440}, ,"004":{"Pref":1}})
					rs = stmt.executeQuery("select '"+eventTypeCode+"'.Pref as Pref, " +
							" '"+eventTypeCode.toString()+"'.Freq as Freq from (select expand(Preference) from MUserAlertPref where " +
							"UserID='"+subscribedUsers.get(i)+"' and CommMode='"+commMode+"' and PrefLevel='AlertType') ");
					while(rs.next())
					{
						prefSet=1;
						pref = rs.getInt("Pref");
						frequency =rs.getString("Freq");
						
					}
					
					if(prefSet==1 && pref==0)
					{
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventCode+":"+commMode+": "+commMode+" Preference set to 0 for the User:"+subscribedUsers.get(i));
						continue outer;
					}
				}
				
				
				 if(prefSet==1 && pref==1)//User has set the preference to 1 for SMS Alerts
				 {
					//-------------------------- Check whether the defined Frequency is met (If defined)
					iLogger.info("frequency:"+frequency);
					 //Frequency not defined for the machine
					if(frequency==null || frequency.equalsIgnoreCase("-1") )
					{
						finalUserList.add(subscribedUsers.get(i));
					}
						
				}
			
			}
			
			long endTime = System.currentTimeMillis();
			iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+eventCode+":"+commMode+":Get the SubscriberList from OrientDB - END: " +
					"SubscriberList:"+finalUserList+"; Total Time in ms:"+(endTime-startTime));
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			status="FAILURE";
			fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+eventCode+":"+commMode+":ORIENTDB:Get SubscriberList:Exception:"+e.getMessage());
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
					fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+eventCode+":"+commMode+":Exception in closing OrientDB Connection:"+e.getMessage());
				}
		 }
		
		return finalUserList;
	}*/
}
