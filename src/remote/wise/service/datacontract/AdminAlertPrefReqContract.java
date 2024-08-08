package remote.wise.service.datacontract;

/**
 * @author Rajani Nagaraju
 *
 */
public class AdminAlertPrefReqContract 
{
	String loginId;
	String eventName;
	int eventTypeId;
	
	
	public AdminAlertPrefReqContract()
	{
		loginId=null;
		eventName=null;
		eventTypeId=0;
	}

	
	/**
	 * @return LoginId of User is returned
	 */
	public String getLoginId() {
		return loginId;
	}
	/**
	 * @param loginId the LoginId of user
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	
	/**
	 * @return Alert Name is returned
	 */
	public String getEventName() {
		return eventName;
	}
	/**
	 * @param eventName the name of the Alert
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	/**
	 * @return Alert type Id is returned
	 */
	public int getEventTypeId() {
		return eventTypeId;
	}
	/**
	 * @param eventTypeId the Alert Type Id
	 */
	public void setEventTypeId(int eventTypeId) {
		this.eventTypeId = eventTypeId;
	}
	
	
}
