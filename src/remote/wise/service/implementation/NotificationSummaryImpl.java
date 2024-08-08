package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessentity.ContactEntity;
import remote.wise.businessobject.EventDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.NotificationSummaryReqContract;
import remote.wise.service.datacontract.NotificationSummaryRespContract;
//import remote.wise.util.WiseLogger;
/**
 * This implemention is to get Notification Summary details for a given period like day, month, year .
 * 
 * @author tejgm
 *
 */
public class NotificationSummaryImpl {
	
	//Defect Id 1337 - Logger changes
	//	public static WiseLogger businessError = WiseLogger.getLogger("NotificationSummaryImpl:","businessError");
	
	private int notificationTypeIdList;
	private String notificationTypeNameList;
	private int notificationcount;
	private String serialNumber;
	  private String tenancy_name;
	  private String machineGroupName;
	  private String machineProfileName;
	  
	public int getNotificationTypeIdList() {
		return notificationTypeIdList;
	}



	public void setNotificationTypeIdList(int notificationTypeIdList) {
		this.notificationTypeIdList = notificationTypeIdList;
	}



	public String getNotificationTypeNameList() {
		return notificationTypeNameList;
	}



	public void setNotificationTypeNameList(String notificationTypeNameList) {
		this.notificationTypeNameList = notificationTypeNameList;
	}



	public int getNotificationcount() {
		return notificationcount;
	}



	public void setNotificationcount(int notificationcount) {
		this.notificationcount = notificationcount;
	}

	public String getSerialNumber() {
		return serialNumber;
	}



	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}



	public String getTenancy_name() {
		return tenancy_name;
	}



	public void setTenancy_name(String tenancy_name) {
		this.tenancy_name = tenancy_name;
	}



	public String getMachineGroupName() {
		return machineGroupName;
	}



	public void setMachineGroupName(String machineGroupName) {
		this.machineGroupName = machineGroupName;
	}



	public String getMachineProfileName() {
		return machineProfileName;
	}



	public void setMachineProfileName(String machineProfileName) {
		this.machineProfileName = machineProfileName;
	}



/**
 * This  method is to get NotificationSummary 
 * @param reqCont is passed to get the summary
 * @return listNotificationSummary return the list of summary on the basis of reqCont
 * 
 */
	
	
	public List<NotificationSummaryRespContract> getNotificationSummary(
			NotificationSummaryReqContract request) {
		// Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalError = FatalLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		ContactEntity contact;
		String userRole;
		String customerCare = null, admin = null;
		List<NotificationSummaryRespContract> listOfNotificationSummary = new LinkedList<NotificationSummaryRespContract>();
		try {

			DomainServiceImpl domainService = new DomainServiceImpl();
			contact = domainService.getContactDetails(request.getContactId());
			if (contact == null || contact.getContact_id() == null) {
				throw new CustomFault("Invalid LoginId");
			}

			if (contact.getRole() != null)
				userRole = contact.getRole().getRole_name();
			else
				throw new CustomFault("User is not assigned a role");

			if ((userRole.equalsIgnoreCase(customerCare) || userRole
					.equalsIgnoreCase(admin))
					&& (request.isOwnStock() == false)
					&& (request.getTenancyIdList() == null || request
							.getTenancyIdList().isEmpty())) {
				throw new CustomFault("Please Provide Tenancy List");
			}

			if (request.getContactId() == null
					|| request.getContactId().isEmpty()) {
				bLogger.error("The Login Id is not available.");
				throw new CustomFault("The Login Id is not available.");
			}

			if (request.getTenancyIdList() == null) {
				throw new CustomFault("Tenancy Id List should be specified");
			}

			if (request.getPeriod() != null) {
				if (!(request.getPeriod().equalsIgnoreCase("Week")
						|| request.getPeriod().equalsIgnoreCase("Month")
						|| request.getPeriod().equalsIgnoreCase("Quarter")
						|| request.getPeriod().equalsIgnoreCase("Year")
						|| request.getPeriod().equalsIgnoreCase("Last Week")
						|| request.getPeriod().equalsIgnoreCase("Last Month")
						|| request.getPeriod().equalsIgnoreCase("Last Quarter") || request
						.getPeriod().equalsIgnoreCase("Last Year"))) {
					throw new CustomFault("Invalid Time Period");

				}
			}

			EventDetailsBO eventDetailsBO = new EventDetailsBO();
			List<NotificationSummaryImpl> respContractImpl = eventDetailsBO
					.getNotificationSummary(request.getPeriod(),
							request.getContactId(),
							request.getNotificationIdList(),
							request.getNotificationTypeIdList(),
							request.getAssetTypeIdList(),
							request.getAssetGroupIdList(),
							request.getTenancyIdList(),
							request.getEventTypeIdList(),
							request.getEventSeverityList(),
							request.isOwnStock(), request.isActiveAlerts());
			NotificationSummaryRespContract respContrt = null;
			for (int i = 0; i < respContractImpl.size(); i++) {

				respContrt = new NotificationSummaryRespContract();
				respContrt.setNotificationTypeIdList(respContractImpl.get(i)
						.getNotificationTypeIdList());
				respContrt.setNotificationTypeNameList(respContractImpl.get(i)
						.getNotificationTypeNameList());
				respContrt.setNotificationcountcount(respContractImpl.get(i)
						.getNotificationcount());
				/*respContrt.setSerialNumber(respContractImpl.get(i)
						.getSerialNumber());
				respContrt.setTenancy_name(respContractImpl.get(i)
						.getTenancy_name());
				respContrt.setMachineGroupName(respContractImpl.get(i)
						.getMachineGroupName());
				respContrt.setMachineProfileName(respContractImpl.get(i)
						.getMachineProfileName());*/
				listOfNotificationSummary.add(respContrt);
			}
		}

		catch (CustomFault e) {
			bLogger.error("Custom Fault: " + e.getFaultInfo());
		}

		return listOfNotificationSummary;
	}

	

}
