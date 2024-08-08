package com.wipro.mcoreapp.implementation;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;

public class AlertSubscriptionProxy implements Callable
{
	public String assetId;
	public String subscriberJsonString;
	
	@Override
	public Object call()
	{
		String status ="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		
		try 
		{
			status = new AlertSubscriptionImpl().setSubscriberGroupDetails(this.assetId, "Migration", this.subscriberJsonString);
		} 
		
		catch (Exception e) 
		{
			status="FAILURE";
			e.printStackTrace();
			fLogger.fatal("MCoreApp:AlertSubscription: migrateSubscribersFromMySQL:AlertSubscriptionProxy:VIN:"+this.assetId+":Exception:"+e.getMessage());
		}
		
		//-------------------- Log the message in specific format into Fatal Logger for any Failures - For Reprocessing the same
		if(status.equalsIgnoreCase("FAILURE"))
		{
			fLogger.fatal("MCoreApp:AlertSubscription:MIGRATION FAILURE:assetId:"+this.assetId+":subscriberJsonString:"+this.subscriberJsonString);
		}
		
		return status;
		
	}
}
