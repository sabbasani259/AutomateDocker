package remote.wise.businessentity;

import java.io.Serializable;

public class NotificationDimensionEntity implements Serializable{
	private int Notification_Dimension_Id;
	private int Notification_Type_Id;
	private String LastUpdated_Date;
	private String Notification_Name, Notification_Type_Name, Severity;	
	private EventEntity Notification_Id;



	public EventEntity getNotification_Id() {
		return Notification_Id;
	}
	public void setNotification_Id(EventEntity notification_Id) {
		Notification_Id = notification_Id;
	}
	public int getNotification_Type_Id() {
		return Notification_Type_Id;
	}
	public void setNotification_Type_Id(int notification_Type_Id) {
		Notification_Type_Id = notification_Type_Id;
	}
	public String getNotification_Name() {
		return Notification_Name;
	}
	public void setNotification_Name(String notification_Name) {
		Notification_Name = notification_Name;
	}
	public String getNotification_Type_Name() {
		return Notification_Type_Name;
	}
	public void setNotification_Type_Name(String notification_Type_Name) {
		Notification_Type_Name = notification_Type_Name;
	}
	public String getSeverity() {
		return Severity;
	}
	public void setSeverity(String severity) {
		Severity = severity;
	}
	public int getNotification_Dimension_Id() {
		return Notification_Dimension_Id;
	}
	public void setNotification_Dimension_Id(int notification_Dimension_Id) {
		Notification_Dimension_Id = notification_Dimension_Id;
	}

	public String getLastUpdated_Date() {
		return LastUpdated_Date;
	}
	public void setLastUpdated_Date(String lastUpdated_Date) {
		LastUpdated_Date = lastUpdated_Date;
	}
}
