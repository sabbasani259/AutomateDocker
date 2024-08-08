package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.wipro.mcoreapp.implementation.UserAlertPrefProxy;
////import org.apache.log4j.Logger;
import remote.wise.businessentity.ContactEntity;
import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.UserAlertPreferenceReqContract;
import remote.wise.service.datacontract.UserAlertPreferenceRespContract;
//import remote.wise.util.WiseLogger;
import remote.wise.util.CommonUtil;

public class UserAlertPreferenceImpl {
	
	//DefectId:1337 -- 20130923 - Log4j Changes - Using static logger object all throughout the application

	//public static WiseLogger businessError = WiseLogger.getLogger("UserAlertPreferenceImpl:","businessError");


	
	private int eventId;
	private String eventName;
	private boolean SMSEvent;
	private boolean EmailEvent;
	private int EventTypeId;
	private String EventTypeName;
//	private String eventTypeCode;
//
//	public String getEventTypeCode() {
//		return eventTypeCode;
//	}
//
//	public void setEventTypeCode(String eventTypeCode) {
//		this.eventTypeCode = eventTypeCode;
//	}
	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}


	
	public String getEventTypeName() {
		return EventTypeName;
	}

	public void setEventTypeName(String eventTypeName) {
		EventTypeName = eventTypeName;
	}

	
	public int getEventTypeId() {
		return EventTypeId;
	}

	public void setEventTypeId(int eventTypeId) {
		EventTypeId = eventTypeId;
	}

	public boolean isSMSEvent() {
		return SMSEvent;
	}

	public void setSMSEvent(boolean sMSEvent) {
		SMSEvent = sMSEvent;
	}

	public boolean isEmailEvent() {
		return EmailEvent;
	}

	public void setEmailEvent(boolean emailEvent) {
		EmailEvent = emailEvent;
	}
	
	/**
	 * method to get user alert preference
	 * @param alertReq
	 * @return List<UserAlertPreferenceRespContract>
	 * @throws CustomFault
	 */
	public List<UserAlertPreferenceRespContract> getUserAlertPreference(UserAlertPreferenceReqContract alertReq)throws CustomFault
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("alertReq::" +alertReq.getLoginId());
		if(alertReq.getLoginId()==null){
			bLogger.error("Login Id is invalid");
			throw new CustomFault("Login Id is invalid");
		}
		
//		if(alertReq.getRoleName()==null){
//			bLogger.error("Provide role of the user.");
//			throw new CustomFault("Role of the user is not provided");
//		}
		
			
		DomainServiceImpl domainImpl = new DomainServiceImpl();
		ContactEntity contactEntity = domainImpl.getContactDetails(alertReq.getLoginId());
				
		if(contactEntity== null || contactEntity.getContact_id()==null){
			bLogger.error("Login Id is invalid");
			throw new CustomFault("Invalid LoginId** ");
		}
		
		List<UserAlertPreferenceRespContract> respList = new ArrayList<UserAlertPreferenceRespContract>();
		UserDetailsBO userBO=new UserDetailsBO();
		List<UserAlertPreferenceImpl> userAlertPrefList = userBO.newGetUserAlertPreference(alertReq.getLoginId());	
		
		UserAlertPreferenceRespContract respObj = null;
		UserAlertPreferenceImpl implObj = null;
		Iterator<UserAlertPreferenceImpl> listIterator=userAlertPrefList.iterator();
		while(listIterator.hasNext()){			
			implObj = listIterator.next();
			respObj = new UserAlertPreferenceRespContract();
			respObj.setLoginId(alertReq.getLoginId());
			respObj.setEventId(implObj.getEventId());
			respObj.setEventName(implObj.getEventName());			
			respObj.setEventTypeId(implObj.getEventTypeId());
			respObj.setEventTypeName(implObj.getEventTypeName());
			respObj.setSMSEvent(implObj.isSMSEvent());
			respObj.setEmailEvent(implObj.isEmailEvent());
			//respObj.setEventTypeCode(implObj.getEventTypeCode());
			respList.add(respObj);
		}		
		return respList;		
	}
	
	public  String setUserAlertPreference(List<UserAlertPreferenceRespContract> respContractList)throws CustomFault
	{
		Logger iLogger = InfoLoggerClass.logger;
		//UserDetailsBO userDetBo = new UserDetailsBO();
		//iLogger.info("UserAlertPreferenceImpl: Set Details in MySQL - Start");
		//String flag = userDetBo.setUserAlertPreference(respContractList);	
		//iLogger.info("UserAlertPreferenceImpl: Set Details in MySQL - End");
		
		//DF20160413 - Rajani Nagaraju - Updating UserAlert Preference in OrientDB which will be used by Communication module for Alert Dispatching
		iLogger.info("UserAlertPreferenceImpl: Set Details in OrientDB - Start - Invoking a new thread");
		HashMap<Boolean,String> boolToStringMap = new HashMap<Boolean,String>();
		boolToStringMap.put(true, "1");
		boolToStringMap.put(false, "0");
		
		HashMap<String,String> smsAlertPrefMap = new HashMap<String,String>();
		HashMap<String,String> emailAlertPrefMap = new HashMap<String,String>();
		for(int i=0; i<respContractList.size(); i++)
		{
			smsAlertPrefMap.put(String.valueOf(respContractList.get(i).getEventTypeId()), boolToStringMap.get(respContractList.get(i).isSMSEvent()));
			emailAlertPrefMap.put(String.valueOf(respContractList.get(i).getEventTypeId()), boolToStringMap.get(respContractList.get(i).isEmailEvent()));
		}
		
		//--------- Invoke OrientDB Update as a separate Thread as this should happen in background and should not affect the performance on UI
		new UserAlertPrefProxy(respContractList.get(0).getLoginId(), "AlertType",smsAlertPrefMap, emailAlertPrefMap);
		iLogger.info("UserAlertPreferenceImpl: Set Details in OrientDB - End - Invoking a new thread");
		
		
		return "success";
	}
	
	
	public  String setAdminAlertPreference(List<UserAlertPreferenceRespContract> respContractList)throws CustomFault
	{
		Logger iLogger = InfoLoggerClass.logger;
//		UserDetailsBO userDetBo = new UserDetailsBO();
		
//		iLogger.info("AdminAlertPreferenceImpl: Set Details in MySQL - Start");
//	     String flag = userDetBo.setAdminAlertPreference(respContractList);	
//		iLogger.info("AdminAlertPreferenceImpl: Set Details in MySQL - End");
//	
		//DF20160413 - Rajani Nagaraju - Updating AdminAlert Preference in OrientDB which will be used by Communication module for Alert Dispatching
		iLogger.info("AdminAlertPreferenceImpl: Set Details in OrientDB - Start - Invoking a new thread");
		HashMap<Boolean,String> boolToStringMap = new HashMap<Boolean,String>();
		boolToStringMap.put(true, "1");
		boolToStringMap.put(false, "0");
		
		HashMap<String,String> smsAlertPrefMap = new HashMap<String,String>();
		HashMap<String,String> emailAlertPrefMap = new HashMap<String,String>();
		for(int i=0; i<respContractList.size(); i++)
		{
			smsAlertPrefMap.put(String.valueOf(respContractList.get(i).getEventTypeId()), boolToStringMap.get(respContractList.get(i).isSMSEvent()));
			emailAlertPrefMap.put(String.valueOf(respContractList.get(i).getEventTypeId()), boolToStringMap.get(respContractList.get(i).isEmailEvent()));
		}
		
		//--------- Invoke OrientDB Update as a separate Thread as this should happen in background and should not affect the performance on UI
		new UserAlertPrefProxy(respContractList.get(0).getLoginId(), "AlertType",smsAlertPrefMap, emailAlertPrefMap);
		iLogger.info("AdminAlertPreferenceImpl: Set Details in OrientDB - End - Invoking a new thread");
		
		
		return "success";
	}
	//DF20170315 Supriya -Dealer Admin alert Preference
	public  String setDealerAdminAlertPreference(List<UserAlertPreferenceRespContract> respContractList)throws CustomFault
	{
		Logger iLogger = InfoLoggerClass.logger;
		UserDetailsBO userDetBo = new UserDetailsBO();
		
		iLogger.info("DealerAdminAlertPreferenceImpl: Set Details in MySQL - Start");
		String flag = userDetBo.setDealerAdminAlertPreference(respContractList);	
		iLogger.info("DealerAdminAlertPreferenceImpl: Set Details in MySQL - End");

		iLogger.info("DealerAdminAlertPreferenceImpl: Set Details in OrientDB - Start - Invoking a new thread");
		HashMap<Boolean,String> boolToStringMap = new HashMap<Boolean,String>();
		boolToStringMap.put(true, "1");
		boolToStringMap.put(false, "0");
		
		HashMap<String,String> smsAlertPrefMap = new HashMap<String,String>();
		HashMap<String,String> emailAlertPrefMap = new HashMap<String,String>();
		for(int i=0; i<respContractList.size(); i++)
		{
			smsAlertPrefMap.put(String.valueOf(respContractList.get(i).getEventId()), boolToStringMap.get(respContractList.get(i).isSMSEvent()));
			emailAlertPrefMap.put(String.valueOf(respContractList.get(i).getEventId()), boolToStringMap.get(respContractList.get(i).isEmailEvent()));
		}
		
		//--------- Invoke OrientDB Update as a separate Thread as this should happen in background and should not affect the performance on UI
		new UserAlertPrefProxy(respContractList.get(0).getLoginId(), "Alert",smsAlertPrefMap, emailAlertPrefMap);
		iLogger.info("DealerAdminAlertPreferenceImpl: Set Details in OrientDB - End - Invoking a new thread");
		
				
		return "success";
	}
}
