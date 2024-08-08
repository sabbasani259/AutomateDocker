package com.wipro.mcoreapp.implementation;

import java.util.HashMap;
import org.apache.logging.log4j.Logger;

import com.wipro.mcoreapp.businessobject.UserPreferenceBO;

import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;


public class UserAlertPrefImpl 
{
	public String setUserAlertPref(String userId, String commMode, String prefLevel, HashMap<String,String> modePrefMap )
	{
		String status="SUCCESS";
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		if(userId==null)
		{
			bLogger.error("MCoreApp:"+"UserAlertPref:setUserAlertPref:Mandatory parameter userId is null");
			return "FAILURE";
		}
		if(commMode==null)
		{
			bLogger.error("MCoreApp:"+"UserAlertPref:setUserAlertPref:Mandatory parameter commMode is null");
			return "FAILURE";
		}
		if(prefLevel==null)
		{
			bLogger.error("MCoreApp:"+"UserAlertPref:setUserAlertPref:Mandatory parameter prefLevel is null");
			return "FAILURE";
		}
		
		if(modePrefMap==null || modePrefMap.size()==0)
		{
			bLogger.error("MCoreApp:"+"UserAlertPref:setUserAlertPref:modePrefMap is empty: No Settings to be updated for the user");
			return "SUCCESS";
		}
		
		long startTime = System.currentTimeMillis();
		iLogger.info("MCoreApp:UserAlertPref:setUserAlertPref:Input ---> userId:"+userId+"; commMode:"+commMode+"; prefLevel:"+prefLevel+"; modePrefMap:"+modePrefMap);
		status = new UserPreferenceBO().setUserAlertPrefToMySql(userId, commMode, prefLevel, modePrefMap);
		long endTime = System.currentTimeMillis();
		iLogger.info("MCoreApp:UserAlertPref:setUserAlertPref:Output ---> status:"+status+"; Total Time in ms:"+(endTime-startTime));
		
		
		return status;
	}
}
