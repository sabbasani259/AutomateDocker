package remote.wise.service.datacontract;

/**
 * @author Rajani Nagaraju
 *
 */
public class AdminAlertPrefRespContract 
{
	int eventTypeId;
	String eventTypeName;
	String eventName;
	boolean isSMS;
	boolean isEmail;
	
	public AdminAlertPrefRespContract()
	{
		eventTypeId =0;
		eventTypeName =null;
		eventName = null;
		isSMS = false;
		isEmail = false;
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
	
	
	/**
	 * @return Alert Type Name is returned
	 */
	public String getEventTypeName() {
		return eventTypeName;
	}
	/**
	 * @param eventTypeName name of the Alert Type
	 */
	public void setEventTypeName(String eventTypeName) {
		this.eventTypeName = eventTypeName;
	}
	
	
	/**
	 * @return Alert Name is returned
	 */
	public String getEventName() {
		return eventName;
	}
	/**
	 * @param eventName the Alert Name
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	
	
	/**
	 * @return Is SMS selected as notification mode
	 */
	public boolean isSMS() {
		return isSMS;
	}
	/**
	 * @param isSMS sets SMS as notification mode
	 */
	public void setSMS(boolean isSMS) {
		this.isSMS = isSMS;
	}
	
	
	/**
	 * @return Is Email selected as notification mode
	 */
	public boolean isEmail() {
		return isEmail;
	}
	/**
	 * @param isEmail sets Email as notification mode
	 */
	public void setEmail(boolean isEmail) {
		this.isEmail = isEmail;
	}
		
}
