package remote.wise.service.implementation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessentity.ContactEntity;
import remote.wise.businessobject.EventDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.AlertSummaryDetailedRespContract;
import remote.wise.service.datacontract.AlertSummaryReqContract;
import remote.wise.service.datacontract.AlertSummaryRespContract;
import remote.wise.service.datacontract.ServiceDueOverDueRespContract;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;



public class AlertSummaryImpl {
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("AlertSummaryImpl:","businessError");
	
	/**
	 * Implemention get's the summary on the alerts
	 * @param request,Passed to get response as alert summary
	 * @return response as summary on alerts
	 * @throws CustomFault
	 * 
	 */
	
public AlertSummaryRespContract getServiceDueOverdueDetails(AlertSummaryReqContract request) throws CustomFault{		
	//Changes Done by Juhi On 6 May 2013
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		Logger iLogger = InfoLoggerClass.logger;
		ContactEntity contact;String userRole;
		String customerCare = null,admin = null;
		AlertSummaryRespContract response = new AlertSummaryRespContract();	
		int redAlertCount=0,yellowAlertCount=0;
		try
		{
			String currentDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
			
			DomainServiceImpl domainService = new DomainServiceImpl();
			contact = domainService.getContactDetails(request.getLoginId());
			if(contact==null || contact.getContact_id()==null)
			{
				throw new CustomFault("Invalid LoginId");
			}
			
			if(contact.getRole()!=null)
				userRole = contact.getRole().getRole_name();
			else
				throw new CustomFault("User is not assigned a role");
			
			//2.TenancyIdList
			if( (userRole.equalsIgnoreCase(customerCare) || userRole.equalsIgnoreCase(admin)) &&  
				 (request.isOwnStock()==false) &&  
				 (request.getTenancyIdList()==null || request.getTenancyIdList().isEmpty()))
			{
				throw new CustomFault("Please Provide Tenancy List");
			}
		
		
			//4.UserTenancyIdList
			if(request.getLoginTenancyIdList()==null || request.getLoginTenancyIdList().isEmpty())
			{
				throw new CustomFault("User Tenancy Id should be passed");
			}
			if(request.getTenancyIdList()==null)
			{
				throw new CustomFault("Tenancy Id List should be specified");
			}
			
			if(request.getLoginId()==null || request.getLoginId().isEmpty()){
				bLogger.error("The Login Id is not available.");
				throw new CustomFault("The Login Id is not available.");
			}
			//iLogger.info(request.getLoginId()+":"+currentDate+":AlertSummaryService -3 ");
			if(request.getPeriod()!=null)
	        {
	              if( !(request.getPeriod().equalsIgnoreCase("Week") || request.getPeriod().equalsIgnoreCase("Month") ||
	            		  request.getPeriod().equalsIgnoreCase("Quarter") || request.getPeriod().equalsIgnoreCase("Year") ||
	            		  request.getPeriod().equalsIgnoreCase("Last Week") ||      request.getPeriod().equalsIgnoreCase("Last Month") ||
	            		  request.getPeriod().equalsIgnoreCase("Last Quarter") || request.getPeriod().equalsIgnoreCase("Last Year") ) )
	              {
	                    throw new CustomFault("Invalid Time Period");
	                    
	              }
	  }
			//iLogger.info(request.getLoginId()+":"+currentDate+":AlertSummaryService -4 ");
			EventDetailsBO eventBO=new EventDetailsBO();	
			
			response=eventBO.getOverviewAlertSummaryDetails(request.getPeriod(), request.getLoginTenancyIdList(), request.getTenancyIdList(), request.getEventTypeIdList(), request.getEventSeverityList(),request.getAssetTypeIdList(),request.getAssetGroupIdList(), request.isOwnStock(), request.isActiveAlerts(),request.getCustomAssetGroupIdList(),contact.getContact_id());
			
			//DF20170216 @Roopa Performance improvement
			/*if(request.isActiveAlerts()){//call user alerts method
					
				//iLogger.info(request.getLoginId()+":"+currentDate+":AlertSummaryService -5 ");
				eventBO.setCurrentDate(currentDate);
				eventBO.setLoginId(request.getLoginId());
				
		List<UserAlertsImpl> userAlertsList = eventBO.newGetUserAlerts(contact,userRole,request.getLoginTenancyIdList(),request.getTenancyIdList(),			
				null,null,request.getEventTypeIdList(),			
				request.getEventSeverityList(),request.isOwnStock(),false);
		//iLogger.info(request.getLoginId()+":"+currentDate+":AlertSummaryService -33 ");
		
		for(int i=0; i<userAlertsList.size(); i++)
		{
			//S Suresh commenting below line as service alerts are not fetching from the query 
			if(userAlertsList.get(i).getAlertTypeName().equalsIgnoreCase("Service"))
			continue;
			
			if(userAlertsList.get(i).getAlertSeverity().equalsIgnoreCase("RED")) {
				++redAlertCount;
			}
			else if(userAlertsList.get(i).getAlertSeverity().equalsIgnoreCase("YELLOW")) {
				++yellowAlertCount;
			}
			
		}
		iLogger.info(request.getLoginId()+":"+currentDate+":AlertSummaryService -34 ");
		response = new AlertSummaryRespContract();		
		response.setRedThresholdValue(redAlertCount);
		response.setYellowThresholdValue(yellowAlertCount);	
	}

	else{
		ServiceDueOverDueImpl servicedueOverdueBO  = new ServiceDueOverDueImpl();
		List<Integer> eventTypeIdList = new LinkedList<Integer>();	
		if((request.getEventTypeIdList()==null || request.getEventTypeIdList().isEmpty())){
			eventTypeIdList.add(2);
			eventTypeIdList.add(3);
			eventTypeIdList.add(4);
			eventTypeIdList.add(5);
			request.setEventTypeIdList(eventTypeIdList);
		}
		servicedueOverdueBO = eventBO.getNonActiveUserAlerts(request.getLoginId(), request.getPeriod(), request.getTenancyIdList(), request.getAssetGroupIdList(), request.getAssetTypeIdList(), request.getCustomAssetGroupIdList(), request.getLandmarkCategoryList(),request.getLandmarkIdList(),request.isFlag(),request.getEventTypeIdList(),request.getEventSeverityList(),request.isOwnStock(),request.isActiveAlerts());	
		response = new AlertSummaryRespContract();	
		response.setYellowThresholdValue(servicedueOverdueBO.getServiceDueCount());
		response.setRedThresholdValue(servicedueOverdueBO.getServiceOverduecount());		
	}
	*/
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
			return response;
	}

	public List<AlertSummaryDetailedRespContract> getDetailedAlertSummary(
			AlertSummaryReqContract request) {
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		ContactEntity contact;String userRole;
		String customerCare = null,admin = null;
		List<AlertSummaryDetailedRespContract> responseList = new LinkedList<AlertSummaryDetailedRespContract>();
		try
		{

			DomainServiceImpl domainService = new DomainServiceImpl();
			contact = domainService.getContactDetails(request.getLoginId());
			if(contact==null || contact.getContact_id()==null)
			{
				throw new CustomFault("Invalid LoginId");
			}
			
			if(contact.getRole()!=null)
				userRole = contact.getRole().getRole_name();
			else
				throw new CustomFault("User is not assigned a role");
			
			//2.TenancyIdList
			if( (userRole.equalsIgnoreCase(customerCare) || userRole.equalsIgnoreCase(admin)) &&  
				 (request.isOwnStock()==false) &&  
				 (request.getTenancyIdList()==null || request.getTenancyIdList().isEmpty()))
			{
				throw new CustomFault("Please Provide Tenancy List");
			}
		
		
			//4.UserTenancyIdList
			if(request.getLoginTenancyIdList()==null || request.getLoginTenancyIdList().isEmpty())
			{
				throw new CustomFault("User Tenancy Id should be passed");
			}
			if(request.getTenancyIdList()==null)
			{
				throw new CustomFault("Tenancy Id List should be specified");
			}
			
			if(request.getLoginId()==null || request.getLoginId().isEmpty()){
				bLogger.error("The Login Id is not available.");
				throw new CustomFault("The Login Id is not available.");
			}
			if(request.getPeriod()!=null)
	        {
	              if( !(request.getPeriod().equalsIgnoreCase("Week") || request.getPeriod().equalsIgnoreCase("Month") ||
	            		  request.getPeriod().equalsIgnoreCase("Quarter") || request.getPeriod().equalsIgnoreCase("Year") ||
	            		  request.getPeriod().equalsIgnoreCase("Last Week") ||      request.getPeriod().equalsIgnoreCase("Last Month") ||
	            		  request.getPeriod().equalsIgnoreCase("Last Quarter") || request.getPeriod().equalsIgnoreCase("Last Year") ) )
	              {
	                    throw new CustomFault("Invalid Time Period");
	                    
	              }
	  }
			EventDetailsBO eventBO=new EventDetailsBO();	
			if(request.isActiveAlerts()){//call user alerts method
				
				//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

				if(request.getLoginTenancyIdList()!=null && request.getLoginTenancyIdList().size()>0){
					request.setLoginTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(request.getLoginTenancyIdList()));
				}
				
				if(request.getTenancyIdList()!=null && request.getTenancyIdList().size()>0){
					request.setTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(request.getTenancyIdList()));
				}
					
		
		List<UserAlertsImpl> userAlertsList = eventBO.getUserAlerts(contact,userRole,request.getLoginTenancyIdList(),request.getTenancyIdList(),
				null,null,request.getEventTypeIdList(),
				request.getEventSeverityList(),request.isOwnStock(),false);
		
		for(int i=0; i<userAlertsList.size(); i++)
		{
			AlertSummaryDetailedRespContract response =null;
			if(userAlertsList.get(i).getAlertTypeName().equalsIgnoreCase("Service"))
				continue;
			if(userAlertsList.get(i).getSerialNumber()!=null)
			{
				 response = new AlertSummaryDetailedRespContract();	

				response.setSerialNumber(userAlertsList.get(i).getSerialNumber());
				response.setEventDescription(userAlertsList.get(i).getAlertDescription());
				response.setEventSeverity(userAlertsList.get(i).getAlertSeverity());
				responseList.add(response);
			}
			
		}
	}
	}catch(CustomFault e)
	{
		bLogger.error("Custom Fault: "+ e.getFaultInfo());
	}
		return responseList;
	}
}
