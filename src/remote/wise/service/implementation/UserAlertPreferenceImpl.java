package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

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

/**
 * CR500 : 20241128 : Dhiraj Kumar : WHatsApp Integration with LL
 */
public class UserAlertPreferenceImpl {
	
	//DefectId:1337 -- 20130923 - Log4j Changes - Using static logger object all throughout the application

	//public static WiseLogger businessError = WiseLogger.getLogger("UserAlertPreferenceImpl:","businessError");


	
	private int eventId;
	private String eventName;
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

	private int EventTypeId;
	private String EventTypeName;
	
	public String getEventTypeName() {
		return EventTypeName;
	}

	public void setEventTypeName(String eventTypeName) {
		EventTypeName = eventTypeName;
	}

	private boolean SMSEvent;
	private boolean EmailEvent;
	private boolean WhatsAppEvent;//CR500.sn
	
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
	//CR500.sn
	public boolean isWhatsAppEvent() {
		return WhatsAppEvent;
	}

	public void setWhatsAppEvent(boolean whatsAppEvent) {
		WhatsAppEvent = whatsAppEvent;
	}
	//CR500.en
	/**
	 * method to get user alert preference
	 * @param alertReq
	 * @return List<UserAlertPreferenceRespContract>
	 * @throws CustomFault
	 */
	public List<UserAlertPreferenceRespContract> getUserAlertPreference(UserAlertPreferenceReqContract alertReq)throws CustomFault
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		if(alertReq.getLoginId()==null){
			bLogger.error("Login Id is invalid");
			throw new CustomFault("Login Id is invalid");
		}
		
		if(alertReq.getRoleName()==null){
			bLogger.error("Provide role of the user.");
			throw new CustomFault("Role of the user is not provided");
		}
		
			
		DomainServiceImpl domainImpl = new DomainServiceImpl();
		ContactEntity contactEntity = domainImpl.getContactDetails(alertReq.getLoginId());
				
		if(contactEntity== null || contactEntity.getContact_id()==null){
			bLogger.error("Login Id is invalid");
			throw new CustomFault("Invalid LoginId** ");
		}
		
		List<UserAlertPreferenceRespContract> respList = new ArrayList<UserAlertPreferenceRespContract>();
		UserDetailsBO userBO=new UserDetailsBO();
		//List<UserAlertPreferenceImpl> userAlertPrefList = userBO.getUserAlertPreference(alertReq.getLoginId(),alertReq.getRoleName());//LL-OPS-139.o	
		List<UserAlertPreferenceImpl> userAlertPrefList = userBO.getUserAlertPreference(alertReq.getLoginId(),alertReq.getRoleName(),alertReq.getPageNumber(),alertReq.getEventTypeId());////LL-OPS-139.n
		UserAlertPreferenceRespContract respObj = null;
		UserAlertPreferenceImpl implObj = null;
		Iterator<UserAlertPreferenceImpl> listIterator=userAlertPrefList.iterator();
		while(listIterator.hasNext()){			
			implObj = listIterator.next();
			respObj = new UserAlertPreferenceRespContract();
			respObj.setEventId(implObj.getEventId());
			respObj.setEventName(implObj.getEventName());			
			respObj.setEventTypeId(implObj.getEventTypeId());
			respObj.setEventTypeName(implObj.getEventTypeName());
			respObj.setSMSEvent(implObj.isSMSEvent());
			respObj.setEmailEvent(implObj.isEmailEvent());
			respObj.setWhatsappEvent(implObj.isWhatsAppEvent());//CR500.n
			respList.add(respObj);
		}		
		return respList;		
	}
	
	public  String setUserAlertPreference(List<UserAlertPreferenceRespContract> respContractList)throws CustomFault
	{
		Logger iLogger = InfoLoggerClass.logger;
		UserDetailsBO userDetBo = new UserDetailsBO();
		iLogger.info("UserAlertPreferenceImpl: Set Details in MySQL - Start");
		String flag = userDetBo.setUserAlertPreference(respContractList);	
		iLogger.info("UserAlertPreferenceImpl: Set Details in MySQL - End");
		
		//DF20160413 - Rajani Nagaraju - Updating UserAlert Preference in OrientDB which will be used by Communication module for Alert Dispatching
		iLogger.info("UserAlertPreferenceImpl: Set Details in OrientDB - Start - Invoking a new thread");
		HashMap<Boolean,String> boolToStringMap = new HashMap<Boolean,String>();
		boolToStringMap.put(true, "1");
		boolToStringMap.put(false, "0");
		
		HashMap<String,String> smsAlertPrefMap = new HashMap<String,String>();
		HashMap<String,String> emailAlertPrefMap = new HashMap<String,String>();
		HashMap<String,String> whatsappAlertPrefMap = new HashMap<String,String>();//CR500.n
		for(int i=0; i<respContractList.size(); i++)
		{
			smsAlertPrefMap.put(String.valueOf(respContractList.get(i).getEventTypeId()), boolToStringMap.get(respContractList.get(i).isSMSEvent()));
			emailAlertPrefMap.put(String.valueOf(respContractList.get(i).getEventTypeId()), boolToStringMap.get(respContractList.get(i).isEmailEvent()));
			//whatsappAlertPrefMap.put(String.valueOf(respContractList.get(i).getEventTypeId()), boolToStringMap.get(respContractList.get(i).isWhatsappEvent()));//CR500.n
			String roleName=String.valueOf(respContractList.get(i).getRoleName());
			String eventID=String.valueOf(respContractList.get(i).getEventTypeId());
			System.out.println("roleName:"+roleName+" :eventID: "+eventID+" ");
			if (roleName.equalsIgnoreCase("Customer") && !(eventID.equals("1")||eventID.equals("2"))) {
				whatsappAlertPrefMap.put(String.valueOf(respContractList.get(i).getEventTypeId()), "0");
			}
			else {
			   whatsappAlertPrefMap.put(String.valueOf(respContractList.get(i).getEventTypeId()), boolToStringMap.get(respContractList.get(i).isWhatsappEvent()));
			}
		}
		
		//--------- Invoke OrientDB Update as a separate Thread as this should happen in background and should not affect the performance on UI
		//new UserAlertPrefProxy(respContractList.get(0).getLoginId(), "AlertType",smsAlertPrefMap, emailAlertPrefMap);//CR500.o
		new UserAlertPrefProxy(respContractList.get(0).getLoginId(), "AlertType",smsAlertPrefMap, emailAlertPrefMap, whatsappAlertPrefMap);//CR500.n
		iLogger.info("UserAlertPreferenceImpl: Set Details in OrientDB - End - Invoking a new thread");
		
		
		return "success";
	}
	
	
	public  String setAdminAlertPreference(List<UserAlertPreferenceRespContract> respContractList)throws CustomFault
	{
		Logger iLogger = InfoLoggerClass.logger;
		UserDetailsBO userDetBo = new UserDetailsBO();
		
		iLogger.info("AdminAlertPreferenceImpl: Set Details in MySQL - Start");
		String flag = userDetBo.setAdminAlertPreference(respContractList);	
		iLogger.info("AdminAlertPreferenceImpl: Set Details in MySQL - End");
	
		//DF20160413 - Rajani Nagaraju - Updating AdminAlert Preference in OrientDB which will be used by Communication module for Alert Dispatching
		iLogger.info("AdminAlertPreferenceImpl: Set Details in OrientDB - Start - Invoking a new thread");
		HashMap<Boolean,String> boolToStringMap = new HashMap<Boolean,String>();
		boolToStringMap.put(true, "1");
		boolToStringMap.put(false, "0");
		
		HashMap<String,String> smsAlertPrefMap = new HashMap<String,String>();
		HashMap<String,String> emailAlertPrefMap = new HashMap<String,String>();
		HashMap<String,String> whatsappAlertPrefMap = new HashMap<String,String>();//CR500.n
		for(int i=0; i<respContractList.size(); i++)
		{
			smsAlertPrefMap.put(String.valueOf(respContractList.get(i).getEventId()), boolToStringMap.get(respContractList.get(i).isSMSEvent()));
			emailAlertPrefMap.put(String.valueOf(respContractList.get(i).getEventId()), boolToStringMap.get(respContractList.get(i).isEmailEvent()));
			whatsappAlertPrefMap.put(String.valueOf(respContractList.get(i).getEventTypeId()), boolToStringMap.get(respContractList.get(i).isWhatsappEvent()));//CR500.n
		}
		//LL-OPS-139 :20250623 :Sai Divya : Alert Preferences Tab Enhancement .sn
		HashMap<String, String> existingsmsAlertPrefMap = new HashMap<>();
		HashMap<String, String> existingemailAlertPrefMap = new HashMap<>();
		HashMap<String, String> existingwhatsappAlertPrefMap = new HashMap<>();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			String query = "SELECT CommMode, Preference FROM MUserAlertPref WHERE UserID = '"
					+ respContractList.get(0).getLoginId() + "'";

			stmt = connection.prepareStatement(query);
			rs = stmt.executeQuery();
			iLogger.info("query" + query);
			while (rs.next()) {
				String commMode = rs.getString("CommMode");
				String preferenceJson = rs.getString("Preference");
				JSONObject preferences = null;
				try {
					preferences = new JSONObject(preferenceJson);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Iterator<String> keys = preferences.keys();
				while (keys.hasNext()) {
					String key = keys.next();
					String value = null;
					try {
						value = preferences.getJSONObject(key).getString("Pref");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (commMode.equals("SMS")) {
						existingsmsAlertPrefMap.put(key, value);
					} else if (commMode.equals("Email")) {
						existingemailAlertPrefMap.put(key, value);
					} else if (commMode.equals("WhatsApp")) {
						existingwhatsappAlertPrefMap.put(key, value);
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		smsAlertPrefMap = compareMaps(smsAlertPrefMap, existingsmsAlertPrefMap);
		emailAlertPrefMap = compareMaps(emailAlertPrefMap, existingemailAlertPrefMap);
		whatsappAlertPrefMap = compareMaps(whatsappAlertPrefMap, existingwhatsappAlertPrefMap);

		// --------- Invoke OrientDB Update as a separate Thread as this should happen
		// in background and should not affect the performance on UI
		// new UserAlertPrefProxy(respContractList.get(0).getLoginId(),
		// "AlertType",smsAlertPrefMap, emailAlertPrefMap);//CR500.o
		if (respContractList.get(0).getRoleName().equalsIgnoreCase("Dealer Admin")
				|| respContractList.get(0).getRoleName().equalsIgnoreCase("JCB Account")
				|| respContractList.get(0).getRoleName().equalsIgnoreCase("MA Manager")
				|| respContractList.get(0).getRoleName().equalsIgnoreCase("Super Admin")
				|| respContractList.get(0).getRoleName().equalsIgnoreCase("Customer Care")
				|| respContractList.get(0).getRoleName().equalsIgnoreCase("Dealer")) {

			new UserAlertPrefProxy(respContractList.get(0).getLoginId(), "Alert", smsAlertPrefMap, emailAlertPrefMap,
					whatsappAlertPrefMap);// CR500.n
		} else {
			new UserAlertPrefProxy(respContractList.get(0).getLoginId(), "AlertType", smsAlertPrefMap,
					emailAlertPrefMap, whatsappAlertPrefMap);
		}
		//LL-OPS-139 :20250623 :Sai Divya : Alert Preferences Tab Enhancement .en
		iLogger.info("AdminAlertPreferenceImpl: Set Details in OrientDB - End - Invoking a new thread");

		return "success";
	}

	private static HashMap<String, String> compareMaps(HashMap<String, String> map1, HashMap<String, String> map2) {
		for (String key : map2.keySet()) {
			if (!map1.containsKey(key)) {
				map1.put(key, map2.get(key));
			}
		}
		return map1;
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
		HashMap<String,String> whatsappAlertPrefMap = new HashMap<String,String>();//CR500.n
		for(int i=0; i<respContractList.size(); i++)
		{
			smsAlertPrefMap.put(String.valueOf(respContractList.get(i).getEventId()), boolToStringMap.get(respContractList.get(i).isSMSEvent()));
			emailAlertPrefMap.put(String.valueOf(respContractList.get(i).getEventId()), boolToStringMap.get(respContractList.get(i).isEmailEvent()));
			whatsappAlertPrefMap.put(String.valueOf(respContractList.get(i).getEventTypeId()), boolToStringMap.get(respContractList.get(i).isWhatsappEvent()));//CR500.n
		}
		
		//--------- Invoke OrientDB Update as a separate Thread as this should happen in background and should not affect the performance on UI
		//new UserAlertPrefProxy(respContractList.get(0).getLoginId(), "AlertType",smsAlertPrefMap, emailAlertPrefMap);//CR500.o
		new UserAlertPrefProxy(respContractList.get(0).getLoginId(), "AlertType",smsAlertPrefMap, emailAlertPrefMap, whatsappAlertPrefMap);//CR500.n
		iLogger.info("DealerAdminAlertPreferenceImpl: Set Details in OrientDB - End - Invoking a new thread");
		
				
		return "success";
	}
}
