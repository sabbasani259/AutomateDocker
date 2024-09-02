package com.wipro.mcoreapp.implementation;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.UserAlertPreferenceReqContract;
import remote.wise.service.datacontract.UserAlertPreferenceRespContract;
import remote.wise.service.implementation.UserAlertPreferenceImpl;

public class UserAlertPrefExecutor implements Callable
{
	public String userId;
	public String roleName;
	public HashMap<String,String> eventIdCodeMap;
	public HashMap<String,String> eventTypeIdCodeMap;
		
	@Override
	public Object call()
	{
		String status ="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		try 
		{
			long startTime=System.currentTimeMillis();
//			iLogger.debug("MCoreApp:UserAlertPrefMigration:userId:"+this.userId+": Set Details into OrientDB: START");
			
			//---------------------- STEP1: Get the Preference set for the user in MySQL
			UserAlertPreferenceImpl implObj = new UserAlertPreferenceImpl();
			UserAlertPreferenceReqContract reqObj = new UserAlertPreferenceReqContract();
			reqObj.setLoginId(this.userId);
			reqObj.setRoleName(this.roleName);
			
			List<UserAlertPreferenceRespContract> respContractList = implObj.getUserAlertPreference(reqObj);
			HashMap<Boolean,String> boolToStringMap = new HashMap<Boolean,String>();
			boolToStringMap.put(true, "1");
			boolToStringMap.put(false, "0");
			
			String smsStatus="SUCCESS";
			String emailsStatus="SUCCESS";
			
			HashMap<String,String> smsAlertPrefMap = new HashMap<String,String>();
			HashMap<String,String> emailAlertPrefMap = new HashMap<String,String>(); 
			if(roleName.equalsIgnoreCase("JCB Admin") || roleName.equalsIgnoreCase("Customer Care"))
			{
				for(int i=0; i<respContractList.size(); i++)
				{
					smsAlertPrefMap.put(eventIdCodeMap.get( String.valueOf(respContractList.get(i).getEventId()) ), boolToStringMap.get(respContractList.get(i).isSMSEvent()));
					emailAlertPrefMap.put(eventIdCodeMap.get( String.valueOf(respContractList.get(i).getEventId()) ), boolToStringMap.get(respContractList.get(i).isEmailEvent()));
				}
				
				smsStatus = new UserAlertPrefImpl().setUserAlertPref(this.userId, "SMS", "Alert", smsAlertPrefMap);
				emailsStatus= new UserAlertPrefImpl().setUserAlertPref(this.userId, "Email", "Alert", emailAlertPrefMap);
				
			}
			
			else
			{
				for(int i=0; i<respContractList.size(); i++)
				{
					smsAlertPrefMap.put(eventTypeIdCodeMap.get( String.valueOf(respContractList.get(i).getEventTypeId()) ), boolToStringMap.get(respContractList.get(i).isSMSEvent()));
					emailAlertPrefMap.put(eventTypeIdCodeMap.get( String.valueOf(respContractList.get(i).getEventTypeId()) ), boolToStringMap.get(respContractList.get(i).isEmailEvent()));
				}
				
				smsStatus = new UserAlertPrefImpl().setUserAlertPref(this.userId, "SMS", "AlertType", smsAlertPrefMap);
				emailsStatus= new UserAlertPrefImpl().setUserAlertPref(this.userId, "Email", "AlertType", emailAlertPrefMap);
				
			}
		
			if(smsStatus.equalsIgnoreCase("FAILURE") || emailsStatus.equalsIgnoreCase("FAILURE"))
				status="FAILURE"; 
			
			long endTime=System.currentTimeMillis();
//			iLogger.debug("MCoreApp:UserAlertPrefMigration:userId:"+this.userId+": Set Details into OrientDB: END: status:"+status+": TotalTime in ms:"+(endTime-startTime));
			
		} 
		
		catch (Exception e) 
		{
			status="FAILURE";
			e.printStackTrace();
			fLogger.fatal("MCoreApp:UserAlertPrefMigration:userId:"+this.userId+":Exception:"+e.getMessage());
		}
		
		//-------------------- Log the message in specific format into Fatal Logger for any Failures - For Reprocessing the same
		if(status.equalsIgnoreCase("FAILURE"))
		{
			fLogger.fatal("MCoreApp:UserAlertPrefMigration:MIGRATION FAILURE:userId:"+this.userId);
		}
		
		return status;
	}
	
}
