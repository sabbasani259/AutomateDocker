package remote.wise.service.implementation;

////import org.apache.log4j.Logger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import remote.wise.businessentity.ContactEntity;
import remote.wise.businessobject.EventDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.AlertSummaryRespContract;
import remote.wise.service.datacontract.ServiceDueOverDueReqContract;
import remote.wise.service.datacontract.ServiceDueOverDueRespContract;
//import remote.wise.util.WiseLogger;

public class ServiceDueOverDueImpl {
	
	//static Logger businessError = Logger.getLogger("businessErrorLogger");
	//Defect Id 1337 - Logger changes
		//public static WiseLogger businessError = WiseLogger.getLogger("ServiceDueOverDueImpl:","businessError");

	private int serviceDueCount;
	private int ServiceOverduecount;
	
	public int getServiceDueCount() {
		return serviceDueCount;
	}


	public void setServiceDueCount(int serviceDueCount) {
		this.serviceDueCount = serviceDueCount;
	}


	public int getServiceOverduecount() {
		return ServiceOverduecount;
	}


	public void setServiceOverduecount(int serviceOverduecount) {
		ServiceOverduecount = serviceOverduecount;
	}

	/**
	 * method to get service due overdue details
	 * @param reqObj
	 * @return ServiceDueOverDueRespContract
	 * @throws CustomFault
	 */
	public ServiceDueOverDueRespContract getServiceDueOverdueDetails(ServiceDueOverDueReqContract reqObj) throws CustomFault{
		ServiceDueOverDueRespContract respObj= new ServiceDueOverDueRespContract();
		ContactEntity contact;String userRole;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		String customerCare = null,admin = null;
		try
		{
			DomainServiceImpl domainService = new DomainServiceImpl();
			contact = domainService.getContactDetails(reqObj.getLoginId());
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
				 (reqObj.isOwnStock()==false) &&  
				 (reqObj.getTenancyIdList()==null || reqObj.getTenancyIdList().isEmpty()))
			{
				throw new CustomFault("Please Provide Tenancy List");
			}
		
		
			//4.UserTenancyIdList
			if(reqObj.getLoginTenancyIdList()==null || reqObj.getLoginTenancyIdList().isEmpty())
			{
				throw new CustomFault("User Tenancy Id should be passed");
			}
			if(reqObj.getTenancyIdList()==null)
			{
				throw new CustomFault("Tenancy Id List should be specified");
			}
			
			if(reqObj.getLoginId()==null || reqObj.getLoginId().isEmpty()){
				bLogger.error("The Login Id is not available.");
				throw new CustomFault("The Login Id is not available.");
			}
			if(reqObj.getPeriod()!=null)
	        {
	              if( !(reqObj.getPeriod().equalsIgnoreCase("Week") || reqObj.getPeriod().equalsIgnoreCase("Month") ||
	            		  reqObj.getPeriod().equalsIgnoreCase("Quarter") || reqObj.getPeriod().equalsIgnoreCase("Year") ||
	            		  reqObj.getPeriod().equalsIgnoreCase("Last Week") ||      reqObj.getPeriod().equalsIgnoreCase("Last Month") ||
	            		  reqObj.getPeriod().equalsIgnoreCase("Last Quarter") || reqObj.getPeriod().equalsIgnoreCase("Last Year") ) )
	              {
	                    throw new CustomFault("Invalid Time Period");
	                    
	              }
	  }
			
			
			if(reqObj.getTenancyIdList()==null)
			{
				throw new CustomFault("Tenancy Id List should be specified");
			}
			
			if(reqObj.getLoginId()==null || reqObj.getLoginId().isEmpty()){
				bLogger.error("The Login Id is not available.");
				throw new CustomFault("The Login Id is not available.");
			}
			if(reqObj.getPeriod()!=null)
	        {
	              if( !(reqObj.getPeriod().equalsIgnoreCase("Week") || reqObj.getPeriod().equalsIgnoreCase("Month") ||
	            		  reqObj.getPeriod().equalsIgnoreCase("Quarter") || reqObj.getPeriod().equalsIgnoreCase("Year") ||
	            		  reqObj.getPeriod().equalsIgnoreCase("Last Week") ||      reqObj.getPeriod().equalsIgnoreCase("Last Month") ||
	            		  reqObj.getPeriod().equalsIgnoreCase("Last Quarter") || reqObj.getPeriod().equalsIgnoreCase("Last Year") ) )
	              {
	                    throw new CustomFault("Invalid Time Period");
	                    
	              }
	  }
			
			
			EventDetailsBO eventBO=new EventDetailsBO();
			//if(reqObj.isActiveAlerts())
				respObj=eventBO.getServicedueOverdueDetails(reqObj.getPeriod(), reqObj.getLoginTenancyIdList(), reqObj.getTenancyIdList(), reqObj.getEventTypeIdList(), reqObj.getEventSeverityList(),reqObj.getAssetTypeIdList(),reqObj.getAssetGroupIdList(), reqObj.isOwnStock(), reqObj.isActiveAlerts(),reqObj.getCustomAssetGroupIdList());
	/*			List<Integer> eventTypesIdList = null;
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				int serviceTypeId = Integer.parseInt(prop.getProperty("ServiceEventTypeId"));
				eventTypesIdList = new ArrayList<Integer>();
				eventTypesIdList.add(serviceTypeId);
				
				List<UserAlertsImpl> userAlertsList = eventBO.getUserAlerts(contact,userRole,reqObj.getLoginTenancyIdList(),reqObj.getTenancyIdList(),
						null,null,eventTypesIdList,
						reqObj.getEventSeverityList(),reqObj.isOwnStock(),false);
				int redAlertCount=0,yellowAlertCount=0;
				for(int i=0; i<userAlertsList.size(); i++)
				{
					if(userAlertsList.get(i).getAlertSeverity().equalsIgnoreCase("RED")) {
						++redAlertCount;
					}
					else if(userAlertsList.get(i).getAlertSeverity().equalsIgnoreCase("YELLOW")) {
						++yellowAlertCount;
					}
				}
				respObj = new ServiceDueOverDueRespContract();
				respObj.setServiceOverDueCount(redAlertCount);
				respObj.setServiceDueCount(yellowAlertCount);
				
			}
			else{
				ServiceDueOverDueImpl servicedueOverdueBO  = new ServiceDueOverDueImpl();
				servicedueOverdueBO = eventBO.getServiceDueOverDueDetails(reqObj.getLoginId(), reqObj.getPeriod(), reqObj.getTenancyIdList(), reqObj.getAssetGroupIdList(), reqObj.getAssetTypeIdList(), reqObj.getCustomAssetGroupIdList(), reqObj.getLandmarkCategoryList(),reqObj.getLandmarkIdList(),reqObj.isFlag(),reqObj.getEventTypeIdList(),reqObj.getEventSeverityList(),reqObj.isOwnStock(),reqObj.isActiveAlerts());
				respObj = new ServiceDueOverDueRespContract();
				respObj.setServiceDueCount(servicedueOverdueBO.getServiceDueCount());
				respObj.setServiceOverDueCount(servicedueOverdueBO.getServiceOverduecount());	
			}
*/
				
				
				
			}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		
		System.out.println("respObj is" +respObj);
		return respObj;
		
	}

}
