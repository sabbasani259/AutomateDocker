/**
 * CR300: 20220720 : Dhiraj K : SOS closure and Report Changes
 */
package remote.wise.businessentity;

import java.io.Serializable;
import java.sql.Timestamp;


public class SOSAlertsHistoryEntity extends BaseBusinessEntity implements Serializable
{
	private int updateId;
	private int assetEventId;
	private String comments;
	private ContactEntity  updatedBy;
	private Timestamp updatedTime;
	//CR300.sn
	private String serialNumber;	
	private String category;
	private String alertCategory;
	private String alert;
	private Timestamp alertGenerationTime;
	private String cmh;
	private Timestamp alertClosureTime;
	private String alertDescription;
	private String resolutionTime;
	private String alertSeverity;
	private String partitionKey ;
	//CR300.en
	
	public int getUpdateId() {
		return updateId;
	}

	public void setUpdateId(int updateId) {
		this.updateId = updateId;
	}

	public int getAssetEventId() {
		return assetEventId;
	}

	public void setAssetEventId(int assetEventId) {
		this.assetEventId = assetEventId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public ContactEntity getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(ContactEntity updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Timestamp getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Timestamp updatedTime) {
		this.updatedTime = updatedTime;
	}
   //CR300.sn
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getAlertCategory() {
		return alertCategory;
	}

	public void setAlertCategory(String alertCategory) {
		this.alertCategory = alertCategory;
	}

	public String getAlert() {
		return alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public Timestamp getAlertGenerationTime() {
		return alertGenerationTime;
	}

	public void setAlertGenerationTime(Timestamp alertGenerationTime) {
		this.alertGenerationTime = alertGenerationTime;
	}

	public String getCmh() {
		return cmh;
	}

	public void setCmh(String cmh) {
		this.cmh = cmh;
	}

	public Timestamp getAlertClosureTime() {
		return alertClosureTime;
	}

	public void setAlertClosureTime(Timestamp alertClosureTime) {
		this.alertClosureTime = alertClosureTime;
	}

	public String getAlertDescription() {
		return alertDescription;
	}

	public void setAlertDescription(String alertDescription) {
		this.alertDescription = alertDescription;
	}

	public String getResolutionTime() {
		return resolutionTime;
	}

	public void setResolutionTime(String resolutionTime) {
		this.resolutionTime = resolutionTime;
	}

	public String getAlertSeverity() {
		return alertSeverity;
	}

	public void setAlertSeverity(String alertSeverity) {
		this.alertSeverity = alertSeverity;
	}

	public String getPartitionKey() {
		return partitionKey;
	}

	public void setPartitionKey(String partitionKey) {
		this.partitionKey = partitionKey;
	}
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	//CR300.en	
}
