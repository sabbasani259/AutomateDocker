/**
 * CR308 : 20220613 : Dhiraj K : Code Fix for BW service closures from Portal
 */
package remote.wise.service.implementation;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

////import org.apache.log4j.Logger;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetEventEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.businessobject.EventDetailsBO;
import remote.wise.businessobject.ServiceDetailsBO;
import remote.wise.businessobject.TenancyBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.service.datacontract.UserAlertsReqContract;
import remote.wise.service.datacontract.UserAlertsRespContract;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;


/** Implementation class that returns the User Alerts
 * @author Rajani Nagaraju
 *
 */
public class UserAlertsImpl 
{
	//DefectId:1337 - Suprava - 20130923 - Log4j Changes - Using static logger object all throughout the application

	//public static WiseLogger infoLogger = WiseLogger.getLogger("UserAlertsImpl:","info");
	/*public static WiseLogger businessError = WiseLogger.getLogger("UserAlertsImpl:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("UserAlertsImpl:","fatalError");*/

	String serialNumber;
	int alertTypeId;
	String alertTypeName;
	String alertDescription;
	String latestReceivedTime;
	String alertSeverity;
	int alertCounter;
	String remarks;
	int assetEventId;
	//CR308.sn
	String serviceName;
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String string) {
		this.serviceName = string;
	}
	//CR308.en
	
	
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}


	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}


	/**
	 * @return the alertTypeId
	 */
	public int getAlertTypeId() {
		return alertTypeId;
	}


	/**
	 * @param alertTypeId the alertTypeId to set
	 */
	public void setAlertTypeId(int alertTypeId) {
		this.alertTypeId = alertTypeId;
	}


	/**
	 * @return the alertTypeName
	 */
	public String getAlertTypeName() {
		return alertTypeName;
	}


	/**
	 * @param alertTypeName the alertTypeName to set
	 */
	public void setAlertTypeName(String alertTypeName) {
		this.alertTypeName = alertTypeName;
	}


	/**
	 * @return the alertDescription
	 */
	public String getAlertDescription() {
		return alertDescription;
	}


	/**
	 * @param alertDescription the alertDescription to set
	 */
	public void setAlertDescription(String alertDescription) {
		this.alertDescription = alertDescription;
	}


	/**
	 * @return the latestReceivedTime
	 */
	public String getLatestReceivedTime() {
		return latestReceivedTime;
	}


	/**
	 * @param latestReceivedTime the latestReceivedTime to set
	 */
	public void setLatestReceivedTime(String latestReceivedTime) {
		this.latestReceivedTime = latestReceivedTime;
	}


	/**
	 * @return the alertSeverity
	 */
	public String getAlertSeverity() {
		return alertSeverity;
	}


	/**
	 * @param alertSeverity the alertSeverity to set
	 */
	public void setAlertSeverity(String alertSeverity) {
		this.alertSeverity = alertSeverity;
	}


	/**
	 * @return the alertCounter
	 */
	public int getAlertCounter() {
		return alertCounter;
	}


	/**
	 * @param alertCounter the alertCounter to set
	 */
	public void setAlertCounter(int alertCounter) {
		this.alertCounter = alertCounter;
	}


	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}


	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}


	/**
	 * @return the assetEventId
	 */
	public int getAssetEventId() {
		return assetEventId;
	}


	/**
	 * @param assetEventId the assetEventId to set
	 */
	public void setAssetEventId(int assetEventId) {
		this.assetEventId = assetEventId;
	}


	//*************************************** get all Alerts for the user accessible Machines ******************************************
	/** This method returns the List of Alerts accessible to the loggedIn User
	 * @param reqObj Input filters based on which the list of alerts would be returned
	 * @return List of Alert with their details
	 * @throws CustomFault
	 */
	public List<UserAlertsRespContract> getUserAlerts (UserAlertsReqContract reqObj) throws CustomFault
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<UserAlertsRespContract> responseList = new LinkedList<UserAlertsRespContract>();
		List<UserAlertsImpl> redResponseList = new LinkedList<UserAlertsImpl>();
		List<UserAlertsImpl> yellowResponseList = new LinkedList<UserAlertsImpl>();
		ContactEntity contact;
		String userRole;
		Timestamp startTime=null;
		Timestamp endTime=null;
		
		String customerCare = null;
		String admin = null;
		Properties prop = new Properties();
		
		/*Logger businessError = Logger.getLogger("businessErrorLogger");
        Logger fatalError = Logger.getLogger("fatalErrorLogger");
        */
		try
		{
//			Keerthi : 12/02/14 : SEARCH : serial no. should be of 7 digits or 17 digits.
			if(reqObj.getSerialNumber()!=null){
				if(!(reqObj.getSerialNumber().trim().length()>=7 && reqObj.getSerialNumber().trim().length()<=17)){
					bLogger.error("Custom Fault: Invalid Serial Number "+reqObj.getSerialNumber()+". Length should be between 7 and 17");
					throw new CustomFault("Invalid Serial Number "+reqObj.getSerialNumber()+". Length should be between 7 and 17");
				}
				
				//DF20171011: KO369761 - Security Check added for input text fields.
				CommonUtil util = new CommonUtil();
				String isValidinput = util.inputFieldValidation(reqObj.getSerialNumber());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			customerCare= prop.getProperty("CustomerCare");
			admin= prop.getProperty("OEMadmin");
		
			String startDateFormatted = null;
			if(reqObj.getStartDate()!=null)
			{
		
				SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date fomrattedDate = datetimeFormat.parse(reqObj.getStartDate());
				
				if(reqObj.getStartDate()!=null)
					startDateFormatted = datetimeFormat.format(fomrattedDate);
		
			}
		
			//check for the mandatory parameters and validate them
			//1.Contact
			if(reqObj.getLoginId()==null)
			{
				throw new CustomFault("Please provide LoginId");
			}
		
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
				 (reqObj.isOwnTenancyAlerts()==false) &&  
				 (reqObj.getTenancyIdList()==null || reqObj.getTenancyIdList().isEmpty()))
			{
				throw new CustomFault("Please Provide Tenancy List");
			}
		
		
			//4.UserTenancyIdList
			if(reqObj.getUserTenancyIdList()==null || reqObj.getUserTenancyIdList().isEmpty())
			{
				throw new CustomFault("User Tenancy Id should be passed");
			}
			
			//5.Start Date
			if(reqObj.isHistory()==true && reqObj.getStartDate()==null )
			{
				throw new CustomFault("Start Date should be specified");
			}
		
			//6.Page Number
			int pageNumber = reqObj.getPageNumber();
			//check whether the page number is specified
			if(pageNumber==0)
			{
				pageNumber=1;
			}
			//System.out.println("pageNumber:"+pageNumber);
			//get the data from EventDetailsBO
			EventDetailsBO eventDetails = new EventDetailsBO();
			List<UserAlertsImpl> userAlertsList = eventDetails.getUserAlertsNew(contact,userRole,reqObj.getUserTenancyIdList(),reqObj.getTenancyIdList(),
																	reqObj.getSerialNumber(),startDateFormatted,reqObj.getAlertTypeId(),
																	reqObj.getAlertSeverity(),reqObj.isOwnTenancyAlerts(),reqObj.isHistory(),pageNumber);
			
			//Defect ID : 490 - Suprava - 29/07/13 - ordering based on severity and event generated time
			//set the response Object
			/*for(int i=0; i<userAlertsList.size(); i++)
			{
				UserAlertsRespContract response = new UserAlertsRespContract();
				
				response.setAlertCounter(userAlertsList.get(i).getAlertCounter());
				response.setAlertDescription(userAlertsList.get(i).getAlertDescription());
				response.setAlertSeverity(userAlertsList.get(i).getAlertSeverity());
				response.setAlertTypeId(userAlertsList.get(i).getAlertTypeId());
				response.setAlertTypeName(userAlertsList.get(i).getAlertTypeName());
				response.setLatestReceivedTime(userAlertsList.get(i).getLatestReceivedTime());
				response.setRemarks(userAlertsList.get(i).getRemarks());
				response.setSerialNumber(userAlertsList.get(i).getSerialNumber());
				response.setAssetEventId(userAlertsList.get(i).getAssetEventId());
				
				responseList.add(response);
			}*/
			for(int i=0; i<userAlertsList.size(); i++)
			{
				if(userAlertsList.get(i).getAlertSeverity().equalsIgnoreCase("RED")) {
					UserAlertsImpl redresponse = new UserAlertsImpl();
					redresponse.setAlertCounter(userAlertsList.get(i).getAlertCounter());
					redresponse.setAlertDescription(userAlertsList.get(i).getAlertDescription());
					redresponse.setAlertSeverity(userAlertsList.get(i).getAlertSeverity());
					redresponse.setAlertTypeId(userAlertsList.get(i).getAlertTypeId());
					redresponse.setAlertTypeName(userAlertsList.get(i).getAlertTypeName());
					redresponse.setAssetEventId(userAlertsList.get(i).getAssetEventId());
					redresponse.setLatestReceivedTime(userAlertsList.get(i).getLatestReceivedTime());
					redresponse.setRemarks(userAlertsList.get(i).getRemarks());
					redresponse.setSerialNumber(userAlertsList.get(i).getSerialNumber());

					redResponseList.add(redresponse);
				}
				else if(userAlertsList.get(i).getAlertSeverity().equalsIgnoreCase("YELLOW")){
					UserAlertsImpl yellowresponse = new UserAlertsImpl();
					yellowresponse.setAlertCounter(userAlertsList.get(i).getAlertCounter());
					yellowresponse.setAlertDescription(userAlertsList.get(i).getAlertDescription());
					yellowresponse.setAlertSeverity(userAlertsList.get(i).getAlertSeverity());
					yellowresponse.setAlertTypeId(userAlertsList.get(i).getAlertTypeId());
					yellowresponse.setAlertTypeName(userAlertsList.get(i).getAlertTypeName());
					yellowresponse.setAssetEventId(userAlertsList.get(i).getAssetEventId());
					yellowresponse.setLatestReceivedTime(userAlertsList.get(i).getLatestReceivedTime());
					yellowresponse.setRemarks(userAlertsList.get(i).getRemarks());
					yellowresponse.setSerialNumber(userAlertsList.get(i).getSerialNumber());
					yellowResponseList.add(yellowresponse);
				}
			}
			Collections.sort(redResponseList, new LatestReceivedTime());
			Collections.reverse(redResponseList);
			Collections.sort(yellowResponseList, new LatestReceivedTime());
			Collections.reverse(yellowResponseList);
			for(int i=0; i<redResponseList.size(); i++)
			{
				UserAlertsRespContract response = new UserAlertsRespContract();
				response.setAlertCounter(redResponseList.get(i).getAlertCounter());
				response.setAlertDescription(redResponseList.get(i).getAlertDescription());
				response.setAlertSeverity(redResponseList.get(i).getAlertSeverity());
				response.setAlertTypeId(redResponseList.get(i).getAlertTypeId());
				response.setAlertTypeName(redResponseList.get(i).getAlertTypeName());
				response.setLatestReceivedTime(redResponseList.get(i).getLatestReceivedTime());
				response.setRemarks(redResponseList.get(i).getRemarks());
				response.setSerialNumber(redResponseList.get(i).getSerialNumber());
				response.setAssetEventId(redResponseList.get(i).getAssetEventId());

				responseList.add(response);
			}
			for(int i=0; i<yellowResponseList.size(); i++)
			{
				UserAlertsRespContract response = new UserAlertsRespContract();
				response.setAlertCounter(yellowResponseList.get(i).getAlertCounter());
				response.setAlertDescription(yellowResponseList.get(i).getAlertDescription());
				response.setAlertSeverity(yellowResponseList.get(i).getAlertSeverity());
				response.setAlertTypeId(yellowResponseList.get(i).getAlertTypeId());
				response.setAlertTypeName(yellowResponseList.get(i).getAlertTypeName());
				response.setLatestReceivedTime(yellowResponseList.get(i).getLatestReceivedTime());
				response.setRemarks(yellowResponseList.get(i).getRemarks());
				response.setSerialNumber(yellowResponseList.get(i).getSerialNumber());
				response.setAssetEventId(yellowResponseList.get(i).getAssetEventId());

				responseList.add(response);
			}
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}
		
		return responseList;
	}


	public String setUserAlertsCloserDetails(String serialNumber,
			String loginId, String jobCardNumber, String servicedDate,
			String messageId,String eventGeneratedTime) {

		//Logger businessError = Logger.getLogger("businessErrorLogger");
		String status = "SUCCESS-Record Processed";

		ServiceDetailsBO serviceBO = new ServiceDetailsBO();

		//Check for Mandatory Parameters
		if(serialNumber==null || serialNumber.trim()==null || serialNumber.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter SerialNumber is NULL";
			//	businessError.error("EA Processing: AssetServiceDetails: "+messageId+" : Mandatory Parameter SerialNumber is NULL");
			return status;
		}

		if(loginId==null || loginId.trim()==null || loginId.replaceAll("\\s","").length()==0 )
		{
			status = "FAILURE-Mandatory Parameter loginId is NULL";
			//businessError.error("EA Processing: AssetServiceDetails: "+messageId+" : Mandatory Parameter loginId is NULL");
			return status;
		}


		if(servicedDate==null || servicedDate.trim()==null || servicedDate.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter servicedDate is NULL";
			//businessError.error("EA Processing: AssetServiceDetails: "+messageId+" : Mandatory Parameter servicedDate is NULL");
			return status;
		}

		if( jobCardNumber==null || jobCardNumber.trim()==null || jobCardNumber.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter jobCardNumber is NULL";
			//	businessError.error("EA Processing: AssetServiceDetails: "+messageId+" : Mandatory Parameter jobCardNumber is NULL");
			return status;
		}
		if( eventGeneratedTime==null || eventGeneratedTime.trim()==null || eventGeneratedTime.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter eventGeneratedTime is NULL";
			//	businessError.error("EA Processing: AssetServiceDetails: "+messageId+" : Mandatory Parameter jobCardNumber is NULL");
			return status;
		}

		try
		{
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			String dealerCode=null;
			String dbmsPartCode =null;

			AccountEntity AccountEntity=null;
			Object[] resultSet = null;
			//DF20190204 @abhishek---->add partition key for faster retrieval
			Query q = session.createQuery("select b.dbmsPartCode,c.dealerId from AssetEventEntity a,ServiceScheduleEntity b,AssetServiceScheduleEntity c where a.serialNumber='"+serialNumber+"' and a.eventGeneratedTime='"+eventGeneratedTime+"' " +
			" and a.activeStatus=1 and a.partitionKey =1 and a.serviceScheduleId=b.serviceScheduleId and b.serviceScheduleId=c.serviceScheduleId and a.serialNumber=c.serialNumber");
			Iterator it = q.list().iterator();
			while(it.hasNext())
			{
				resultSet = (Object[]) it.next();
				if(resultSet[0]!=null){
					dbmsPartCode = (String)resultSet[0];
				}

				if(resultSet[1]!=null){
					AccountEntity =(AccountEntity)resultSet[1];
					dealerCode =AccountEntity.getAccountCode();
				}
			}
			TenancyBO tenancyBoObj = new TenancyBO();
			String llAccountCode = tenancyBoObj.getLLAccountCode(dealerCode);

			if(llAccountCode==null)
			{
				throw new CustomFault("Data not found in Mapping table for the Dealer AccountCode:"+dealerCode);
			}
			else
			{
				dealerCode = llAccountCode;
			} 
			status = serviceBO.setServiceDetails(serialNumber, dealerCode, jobCardNumber, dbmsPartCode, servicedDate, messageId);
		}

		catch(CustomFault e)
		{
			status = "FAILURE-"+e.getFaultInfo();
			//businessError.error("EA Processing: AssetServiceDetails: "+messageId+" : "+e.getFaultInfo());
		}

		return status;

	}
	 
	//*************************************** END of get all Alerts for the user accessible Machines ******************************************
	
}

class LatestReceivedTime implements Comparator<UserAlertsImpl>
{
	@Override
	public int compare(UserAlertsImpl arg0, UserAlertsImpl arg1) {
		// TODO Auto-generated method stub
		return (arg0.getLatestReceivedTime().compareTo(arg1.getLatestReceivedTime()));
	} 
}
