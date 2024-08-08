package com.wipro.mcoreapp.businessobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class UserPreferenceBO 
{
	//Df20171025 @Roopa Changing from orientDB to mysql tables for subscriber details
	public String setUserAlertPrefToMySql(String userId, String commMode, String prefLevel, HashMap<String,String> modePrefMap)
	{
		String setStatus="SUCCESS";
		
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	iLogger.info("UserPreferenceBO : UserID"+userId+" commMode:"+commMode+" prefLevel: "+prefLevel+" modePrefMap "+ modePrefMap);
    	ConnectMySQL connObj = new ConnectMySQL();
		Connection conn = null;
		Statement stmt = null;
		Statement stmt1=null;
		ResultSet rs = null;
		try
		{
			conn = connObj.getConnection();
			stmt = conn.createStatement();
			
			
			//------------------------------ STEP1: Create a JSON String in the Format {"010":{"Pref":1},"011":{"Pref":1}}
			HashMap<String,Object> modePrefObj =  new HashMap<String,Object>();
			for(Map.Entry entry:  modePrefMap.entrySet())
			{
				HashMap<String,String> innerPrefMap = new HashMap<String,String>();
				innerPrefMap.put("Pref", entry.getValue().toString());
				
				modePrefObj.put(entry.getKey().toString(), innerPrefMap);
			}
			
			//Check whether the row exists in MUserAlertPref for the combination userId+commMode+prefLevel in which case,
			//update the JSON values in Preference otherwise insert the received preference
			rs = stmt.executeQuery("select * from MUserAlertPref where UserID='"+userId+"' and CommMode='"+commMode+"'");
			int update=0;
			while(rs.next())
			{
				update=1;
				String prefLevel1 = rs.getString("PrefLevel");
				if(prefLevel1 != null && prefLevel1.equalsIgnoreCase("Alert")){
					setStatus="Failure";
					fLogger.error("PrefLevel for UserID :"+userId+"is already set to Alert level , All preference must be in AlertTypeLevel");
					return setStatus;
					
				}
				HashMap<String,Object >alertPrefMap = new HashMap<String,Object>();
					
				 //Update only the received Preference settings
				Object preference = rs.getObject("Preference");
				if(preference!=null)
				{
					
					String prefJSONString = alertPrefMap.toString();
					alertPrefMap = new Gson().fromJson(prefJSONString, new TypeToken<HashMap<String, Object>>() {}.getType());
				}
				
				for(Map.Entry entry : modePrefObj.entrySet())
				{
					alertPrefMap.put(entry.getKey().toString(), entry.getValue());
				}
				
				alertPrefMap.remove("@version");
				alertPrefMap.remove("@type");
				
				 stmt1 = conn.createStatement();
				String query = "Update MUserAlertPref set Preference='"+new JSONObject(alertPrefMap)+
						"' where UserID='"+userId+"' and CommMode='"+commMode+"' and PrefLevel='"+prefLevel1+"'";
				iLogger.info("setUserAlertPrefToMySql : Query :"+query);
				 int row1  =stmt1.executeUpdate(query);
				if(row1 > 0){
					setStatus="SUCCESS";
				}else{
					setStatus="Failure";
				}
				iLogger.info("setUserAlertPrefToMySql : Status :"+setStatus+" jdbc response code :"+row1);
				//iLogger.debug("MCoreApp:UserAlertPref:setUserAlertPref:userId:"+userId+":Update UserLevel Preference into MUserAlertPref for CommMode:"+commMode+" and PrefLevel:"+prefLevel);
			
			}
			
			if(update==0)//------------- Insert the new record into MUserAlertPref
			{
				/*HashMap userAlertPrefMap = new HashMap();
				userAlertPrefMap.put("UserID", userId);
				userAlertPrefMap.put("CommMode", commMode);
				userAlertPrefMap.put("PrefLevel", prefLevel);
				
				modePrefObj.put("@type", "d");
				userAlertPrefMap.put("Preference", modePrefObj);
				
				String insertQuery = new JSONObject(userAlertPrefMap).toString();
				insertQuery=insertQuery.replaceAll("\\\\","");
				stmt.execute("INSERT INTO MUserAlertPref content "+insertQuery);*/
				
				String insertQuery ="Insert into MUserAlertPref(UserID,CommMode,PrefLevel,Preference) values('"+userId+"','"+commMode+"','"+prefLevel+"','"+new JSONObject(modePrefObj).toString()+"')";
				iLogger.info("setUserAlertPrefToMySql : insertQuery :"+insertQuery);
				
				 int row1 =stmt.executeUpdate(insertQuery);
				if (row1 > 0) {
					setStatus = "SUCCESS";
				} else {
					setStatus = "Failure";

				}
				iLogger.info("setUserAlertPrefToMySql : Status :"+setStatus+" jdbc response code :"+row1);
			//	iLogger.debug("MCoreApp:UserAlertPref:setUserAlertPref:userId:"+userId+":INSERT UserLevel Preference into MUserAlertPref for CommMode:"+commMode+" and PrefLevel:"+prefLevel);
				
			}
			
			
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("MCoreApp:"+"UserAlertPref:setUserAlertPref:userId:"+userId+":Exception"+e.getMessage());
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
				
				if(stmt1!=null && !stmt1.isClosed())
					stmt1.close();
				
				if(conn!=null && !conn.isClosed())
					conn.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("MCoreApp:"+"UserAlertPref:setUserAlertPref:userId:"+userId+":Exception in closing OrientDB Connection:"+e.getMessage());
			}
			
		}
		return setStatus;
		
	}
	
	/*public String setUserAlertPref(String userId, String commMode, String prefLevel, HashMap<String,String> modePrefMap)
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
			
			
			//------------------------------ STEP1: Create a JSON String in the Format {"010":{"Pref":1},"011":{"Pref":1}}
			HashMap<String,Object> modePrefObj =  new HashMap<String,Object>();
			for(Map.Entry entry:  modePrefMap.entrySet())
			{
				HashMap<String,String> innerPrefMap = new HashMap<String,String>();
				innerPrefMap.put("Pref", entry.getValue().toString());
				
				modePrefObj.put(entry.getKey().toString(), innerPrefMap);
			}
			
			//Check whether the row exists in MUserAlertPref for the combination userId+commMode+prefLevel in which case,
			//update the JSON values in Preference otherwise insert the received preference
			rs = stmt.executeQuery("select Preference from MUserAlertPref where UserID='"+userId+"' and CommMode='"+commMode+"' and" +
					" PrefLevel='"+prefLevel+"'");
			int update=0;
			while(rs.next())
			{
				update=1;
				HashMap<String,Object >alertPrefMap = new HashMap<String,Object>();
					
				 //Update only the received Preference settings
				Object preference = (Object) rs.getObject("Preference");
				if(preference==null)
				{
					HashMap<String,String> prefMap = new HashMap<String,String>();
					prefMap.put("@type", "d");
					
					stmt.executeUpdate("Update MUserAlertPref set Preference="+(new JSONObject(prefMap))+" where UserID='"+userId+"' " +
						" and CommMode='"+commMode+"' and PrefLevel='"+prefLevel+"'");
				}
				
				else
				{
					ODocument prefDoc = (ODocument)preference;
					ORecord prefRecord = prefDoc.getRecord();
					String prefJSONString = prefRecord.toJSON();
					alertPrefMap = new Gson().fromJson(prefJSONString, new TypeToken<HashMap<String, Object>>() {}.getType());
				}
				
				for(Map.Entry entry : modePrefObj.entrySet())
				{
					alertPrefMap.put(entry.getKey().toString(), entry.getValue());
				}
				
				alertPrefMap.remove("@version");
				
				stmt.executeUpdate("Update MUserAlertPref set Preference="+new JSONObject(alertPrefMap)+
						" where UserID='"+userId+"' and CommMode='"+commMode+"' and PrefLevel='"+prefLevel+"'");
				
				iLogger.debug("MCoreApp:UserAlertPref:setUserAlertPref:userId:"+userId+":Update UserLevel Preference into MUserAlertPref for CommMode:"+commMode+" and PrefLevel:"+prefLevel);
			
			}
			
			if(update==0)//------------- Insert the new record into MUserAlertPref
			{
				HashMap userAlertPrefMap = new HashMap();
				userAlertPrefMap.put("UserID", userId);
				userAlertPrefMap.put("CommMode", commMode);
				userAlertPrefMap.put("PrefLevel", prefLevel);
				
				modePrefObj.put("@type", "d");
				userAlertPrefMap.put("Preference", modePrefObj);
				
				String insertQuery = new JSONObject(userAlertPrefMap).toString();
				insertQuery=insertQuery.replaceAll("\\\\","");
				stmt.execute("INSERT INTO MUserAlertPref content "+insertQuery);
				
				iLogger.debug("MCoreApp:UserAlertPref:setUserAlertPref:userId:"+userId+":INSERT UserLevel Preference into MUserAlertPref for CommMode:"+commMode+" and PrefLevel:"+prefLevel);
				
			}
			
			
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("MCoreApp:"+"UserAlertPref:setUserAlertPref:userId:"+userId+":Exception"+e.getMessage());
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
				fLogger.fatal("MCoreApp:"+"UserAlertPref:setUserAlertPref:userId:"+userId+":Exception in closing OrientDB Connection:"+e.getMessage());
			}
			
		}
		return setStatus;
		
	}*/
}
