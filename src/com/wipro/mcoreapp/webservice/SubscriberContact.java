package com.wipro.mcoreapp.webservice;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

import com.wipro.mcoreapp.businessobject.SubscriberContactEntity;
import com.wipro.mcoreapp.implementation.AlertSubscriptionImpl;

@Path("/SubscriberContact")
public class SubscriberContact 
{
	@GET
	@Path("getContactDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public List<SubscriberContactEntity> getContactDetails(@QueryParam("VIN") String VIN, @QueryParam("loginId") String loginId, @QueryParam("block") String block)
	{
		 List<SubscriberContactEntity> contactList = null;
		 
		 Logger iLogger = InfoLoggerClass.logger;
		 
		 //DF20170919 @Roopa getting decoded UserId
		 
		 loginId=new CommonUtil().getUserId(loginId);
			
		 iLogger.info("MCoreApp:AlertSubscription:getContactDetails:WebService Input-----> VIN:"+VIN+"; loginId:"+loginId+"; block:"+block);
		 long startTime = System.currentTimeMillis();
			
		 contactList = new AlertSubscriptionImpl().getContactDetails(VIN, block);
			
		 long endTime=System.currentTimeMillis();
		 iLogger.info("MCoreApp:AlertSubscription:getContactDetails ; Total Time taken in ms:"+(endTime - startTime));
			
			
		 return contactList;
	}
}
