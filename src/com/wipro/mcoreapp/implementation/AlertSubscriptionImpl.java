package com.wipro.mcoreapp.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.wipro.mcoreapp.businessobject.SubscriberContactEntity;
import com.wipro.mcoreapp.businessobject.UserSubscriptionBO;

import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class AlertSubscriptionImpl 
{
	public String getSubscriberGroupDetails(String assetId)
	{
		String subscriberJSONArray=null;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		if(assetId==null)
		{
			bLogger.error("MCoreApp:"+"AlertSubscription:getSubscriptionDetails:Mandatory parameter assetID is null");
			return null;
		}
		
		//subscriberJSONArray = new UserSubscriptionBO().getAlertSubscriberGroup(assetId);
		subscriberJSONArray = new UserSubscriptionBO().getAlertSubscriberGroupFromMysql(assetId);
		
		return subscriberJSONArray;
	}
	
	//*****************************************************************************************************************************
	
	
	public String setSubscriberGroupDetails(String assetId, String loginId, String subscriberGroupJSON)
	{
		String setStatus="SUCCESS";
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		if(assetId==null)
		{
			bLogger.error("MCoreApp:"+"AlertSubscription:setSubscriberGroupDetails:Mandatory parameter assetID is null");
			return "FAILURE";
		}
		
		if(loginId==null)
		{
			bLogger.error("MCoreApp:"+"AlertSubscription:setSubscriberGroupDetails:Mandatory parameter loginId is null");
			return "FAILURE";
		}
		
		if(subscriberGroupJSON==null)
		{
			bLogger.error("MCoreApp:"+"AlertSubscription:setSubscriberGroupDetails:Mandatory parameter subscriberGroupJSON is null");
			return "FAILURE";
		}
		
		//*********************** STEP1: Set the Subscription Group Details into MAlertSubsriberGroup Class in orientAppDb
		iLogger.info("MCoreApp:"+"AlertSubscription:setSubscriberGroupDetails:Insert details into MAlertSubsriberGroup Class in AppDB");
		//setStatus = new UserSubscriptionBO().setSubscriberGroupDetails(assetId, subscriberGroupJSON);
		//DF20190321:mani:notification subscriber traceability, adding the loginId parameter,to invoke traceability method from within
			setStatus = new UserSubscriptionBO().setSubscriberGroupDetailsToMysql(assetId, subscriberGroupJSON,loginId);
		/*setStatus = new UserSubscriptionBO().setSubscriberGroupDetailsToMysql(assetId, subscriberGroupJSON);*/
		if(setStatus==null || setStatus.equalsIgnoreCase("FAILURE"))
		{
			fLogger.fatal("MCoreApp:"+"AlertSubscription:setSubscriberGroupDetails:MAlertSubsriberGroup Class:Status:FAILURE");
			return "FAILURE";
		}
		
		//*********************** STEP2: Set the entry in audit log for setting/updating subscription group details
		iLogger.info("MCoreApp:"+"AlertSubscription:setSubscriptionAuditTrial:Insert details into MSubscriptionAuditTrial Class in AppDB");
		//setStatus = new UserSubscriptionBO().setSubscriptionAuditTrial(assetId, loginId, subscriberGroupJSON);
		
		setStatus = new UserSubscriptionBO().setSubscriptionAuditTrialToMysql(assetId, loginId, subscriberGroupJSON);
		
		if(setStatus==null || setStatus.equalsIgnoreCase("FAILURE"))
		{
			fLogger.fatal("MCoreApp:"+"AlertSubscription:setSubscriberGroupDetails:MSubscriptionAuditTrial Class:Status:FAILURE");
			return "FAILURE";
		}
		
		
		//*********************** STEP3: Set the subscription details per communication mode in orientAppDb for DC Module
		iLogger.info("MCoreApp:"+"AlertSubscription:setCommSubscription:Insert details into MAlertSubscribers Class in AppDB");
		//setStatus = new UserSubscriptionBO().setCommSubscription(assetId, subscriberGroupJSON);
		
		setStatus = new UserSubscriptionBO().setCommSubscriptionToMysql(assetId, subscriberGroupJSON);
		if(setStatus==null || setStatus.equalsIgnoreCase("FAILURE"))
		{
			fLogger.fatal("MCoreApp:"+"AlertSubscription:setCommSubscription:MAlertSubscribers Class:Status:FAILURE");
			return "FAILURE";
		}
		
		
		return setStatus;
	}
	
	
	//*****************************************************************************************************************************
	public List<SubscriberContactEntity> getContactDetails(String assetId,String subscriberBlock)
	{
		List<SubscriberContactEntity> contactList = new LinkedList<SubscriberContactEntity>();
		
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		if(assetId==null)
		{
			bLogger.error("MCoreApp:"+"AlertSubscription:getContactDetails:Mandatory parameter assetID is null");
			return contactList;
		}
		
		if(subscriberBlock==null)
		{
			bLogger.error("MCoreApp:"+"AlertSubscription:getContactDetails:Mandatory parameter subscriberBlock is null");
			return contactList;
		}
		
		contactList = new UserSubscriptionBO().getContactDetails(assetId,subscriberBlock);
				
		return contactList;
	}
	
	
	//*****************************************************************************************************************************
	
	public String setDefaultSubscribers(String assetId)
	{
		String status="SUCCESS";
		
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		if(assetId==null)
		{
			bLogger.error("MCoreApp:"+"AlertSubscription:setDefaultSubscribers:Mandatory parameter assetID is null");
			return "FAILURE";
		}
		
		//status = new UserSubscriptionBO().setDefaultSubscribers(assetId);
		
		status = new UserSubscriptionBO().setDefaultSubscribersToMysql(assetId);
		
		return status;
	}
	
	
	//*****************************************************************************************************************************
	public String migrateSubscribersFromMySQL(String assetId, String subscriberGroup)
	{
		String status="SUCCESS";
		
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		if(subscriberGroup==null)
		{
			bLogger.error("MCoreApp:"+"AlertSubscription:migrateSubscribersFromMySQL:Mandatory parameter subscriberGroup is null");
			return "FAILURE";
		}
		
		new UserSubscriptionBO().migrateSubscribersFromMySQL(assetId, subscriberGroup);
		
		return status;
	}
}
