package remote.wise.businessentity;

import java.sql.Timestamp;

public class AssetEventEntity extends BaseBusinessEntity 
{
	int assetEventId;
	AssetEntity serialNumber;
	EventEntity eventId;
	EventTypeEntity eventTypeId;
	String eventSeverity;
	Timestamp eventGeneratedTime;
	int activeStatus;
	String comments;
	Timestamp eventClosedTime;
	//Added by Rajani Nagaraju - 20130822- DefectID 1163
	int serviceScheduleId;
	//Added by smitha - DefectID 20131024
	String address;
	//DF20150601 - Rajani Nagaraju - Updating City and State for a lat long in asset event in addition to address
	String state;
	String city;
	//DF20160504 - Rajani Nagaraju - Required for Landmark Alert Closure
	int landmarkId;
	String Location;
	//DF20160803 - Suresh - required for alerts migration to the db2
	Timestamp created_timestamp;

	//DF20190110@KO369761 New columns added for the partition key and updated sources.
	String updateSource;
	int partitionKey;
	int autoClosure;

	public AssetEventEntity(){}
	
	public AssetEventEntity(int assetEventId)
	{
		super.key = new Integer(assetEventId);
		AssetEventEntity e = (AssetEventEntity)read(this);
		
		if(e!=null)
		{
			setAssetEventId(e.getAssetEventId());
			setSerialNumber(e.getSerialNumber());
			setEventId(e.getEventId());
			setEventTypeId(e.getEventTypeId());
			setEventSeverity(e.getEventSeverity());
			setEventGeneratedTime(e.getEventGeneratedTime());
			setActiveStatus(e.getActiveStatus());
			setComments(e.getComments());
			setEventClosedTime(e.getEventClosedTime());
			//Added by Rajani Nagaraju - 20130822- DefectID 1163
			setServiceScheduleId(e.getServiceScheduleId());
			setAddress(e.getAddress());
			setUpdateSource(e.getUpdateSource());
			setPartitionKey(e.getPartitionKey());
			setAutoClosure(e.getAutoClosure());
		}
		
	}
	
	public String getUpdateSource() {
		return updateSource;
	}

	public void setUpdateSource(String updateSource) {
		this.updateSource = updateSource;
	}

	public int getPartitionKey() {
		return partitionKey;
	}

	public void setPartitionKey(int partitionKey) {
		this.partitionKey = partitionKey;
	}
	
	public int getAutoClosure() {
		return autoClosure;
	}

	public void setAutoClosure(int autoClosure) {
		this.autoClosure = autoClosure;
	}
	
	public String getLocation() {
		return Location;
	}

	public void setLocation(String location) {
		Location = location;
	}

	/**
	 * @return the created_timestamp
	 */
	public Timestamp getCreated_timestamp() {
		return created_timestamp;
	}

	/**
	 * @param created_timestamp the created_timestamp to set
	 */
	public void setCreated_timestamp(Timestamp created_timestamp) {
		this.created_timestamp = created_timestamp;
	}

	public int getAssetEventId() {
		return assetEventId;
	}
	public void setAssetEventId(int assetEventId) {
		this.assetEventId = assetEventId;
	}
	public AssetEntity getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(AssetEntity serialNumber) {
		this.serialNumber = serialNumber;
	}
	public EventEntity getEventId() {
		return eventId;
	}
	public void setEventId(EventEntity eventId) {
		this.eventId = eventId;
	}
	public EventTypeEntity getEventTypeId() {
		return eventTypeId;
	}
	public void setEventTypeId(EventTypeEntity eventTypeId) {
		this.eventTypeId = eventTypeId;
	}
	public String getEventSeverity() {
		return eventSeverity;
	}
	public void setEventSeverity(String eventSeverity) {
		this.eventSeverity = eventSeverity;
	}
	public Timestamp getEventGeneratedTime() {
		return eventGeneratedTime;
	}
	public void setEventGeneratedTime(Timestamp eventGeneratedTime) {
		this.eventGeneratedTime = eventGeneratedTime;
	}
	public int getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(int activeStatus) {
		this.activeStatus = activeStatus;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Timestamp getEventClosedTime() {
		return eventClosedTime;
	}

	public void setEventClosedTime(Timestamp eventClosedTime) {
		this.eventClosedTime = eventClosedTime;
	}
	//Added by Rajani Nagaraju - 20130822- DefectID 1163
	/**
	 * @return the serviceScheduleId
	 */
	public int getServiceScheduleId() {
		return serviceScheduleId;
	}

	/**
	 * @param serviceScheduleId the serviceScheduleId to set
	 */
	public void setServiceScheduleId(int serviceScheduleId) {
		this.serviceScheduleId = serviceScheduleId;
	}
	//Added by smitha...DefectID-20131024
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getLandmarkId() {
		return landmarkId;
	}

	public void setLandmarkId(int landmarkId) {
		this.landmarkId = landmarkId;
	}
	
	
}
