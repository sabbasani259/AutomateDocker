package remote.wise.service.implementation;

import remote.wise.businessobject.NotificationFactDataBO;
import remote.wise.businessobject.NotificationfactDataNewBO;
import remote.wise.exception.CustomFault;
/**
 * This Implementation class will allow to insert data in NotificationFactEntity_DayAgg table for a current day.
 * @author jgupta41
 *
 */
public class NotificationFactDataImpl {
	//******************************************Start of setNotificationFactData************************************************************
	/**
	 *  This method will set to insert data in NotificationFactEntity_DayAgg table for a current day.
	 * @return SUCCESS
	 */
	public String setNotificationFactData()
	{
		NotificationfactDataNewBO notificationFactDataBO=new NotificationfactDataNewBO();
		String flag=notificationFactDataBO.setNotificationData();
		return flag;
		
		
	}
	//******************************************End of setNotificationFactData************************************************************
	//**************************************Start of  updateNotificationFact  *********************************************************

	/**
	 * This method will update AssetClassId in All fact tables
	 * @return SUCCESS
	 * @throws CustomFault
	 */
	public String updateNotificationFact()throws CustomFault
	{
		NotificationFactDataBO notificationFactDataBO=new NotificationFactDataBO();
		String flag=notificationFactDataBO.updateNotificationFact();
		return flag;
	}
	//******************************************End of updateNotificationFact************************************************************
	//**************************************Start of  updateTenancyIdInFactTables  *********************************************************

	/**
	 * This method will update TenancyId in All fact tables
	 * @return SUCCESS
	 * @throws CustomFault
	 */
	public String updateTenancyIdInFactTables()throws CustomFault
	{
		NotificationFactDataBO notificationFactDataBO=new NotificationFactDataBO();
		String flag=notificationFactDataBO.updateTenancyIdInFactTables();
		return flag;
	}
	//******************************************End of updateTenancyIdInFactTables************************************************************
}
