package remote.wise.businessentity;

public class ServiceScheduleEntity extends BaseBusinessEntity
{
	private int serviceScheduleId;
    private String serviceName;
    private String scheduleName;  
    private int durationSchedule;
    private long engineHoursSchedule;
    private AssetTypeEntity assetTypeId;
    private EngineTypeEntity engineTypeId;
    private AssetGroupEntity assetGroupId;
    private String dbmsPartCode;
    
    //DF20191220:Abhishek:: added colummns for Extended Warranty service .
    private int callTypeId;
    private String CallType;
    private String CallSubType;
    private int MonthlyVisit;
	private String ServiceType;
    
	public String getServiceType() {
		return ServiceType;
	}

	public void setServiceType(String serviceType) {
		ServiceType = serviceType;
	}

    public String getCallType() {
		return CallType;
	}

	public void setCallType(String callType) {
		CallType = callType;
	}

	public String getCallSubType() {
		return CallSubType;
	}

	public void setCallSubType(String callSubType) {
		CallSubType = callSubType;
	}

	public int getMonthlyVisit() {
		return MonthlyVisit;
	}

	public void setMonthlyVisit(int monthlyVisit) {
		MonthlyVisit = monthlyVisit;
	}

	public int getCallTypeId() {
		return callTypeId;
	}

	public void setCallTypeId(int callTypeId) {
		this.callTypeId = callTypeId;
	}
	
	
    public ServiceScheduleEntity(){}
    
    public ServiceScheduleEntity(int serviceScheduleId)
    {
    	super.key=new Integer(serviceScheduleId);
    	ServiceScheduleEntity servicescheduleEntity=(ServiceScheduleEntity)read(this);
    	if(servicescheduleEntity!=null)
    	{
    		setServiceScheduleId(servicescheduleEntity.getServiceScheduleId());
    		setServiceName(servicescheduleEntity.getServiceName());
    		setScheduleName(servicescheduleEntity.getScheduleName());
    		setDurationSchedule(servicescheduleEntity.getDurationSchedule());
    		setEngineHoursSchedule(servicescheduleEntity.getEngineHoursSchedule());
    		setAssetGroupId(servicescheduleEntity.getAssetGroupId());
    		setAssetTypeId(servicescheduleEntity.getAssetTypeId());
    		setEngineTypeId(servicescheduleEntity.getEngineTypeId());
    	}
    }
    
    
	public AssetTypeEntity getAssetTypeId() {
		return assetTypeId;
	}

	public void setAssetTypeId(AssetTypeEntity assetTypeId) {
		this.assetTypeId = assetTypeId;
	}



	public EngineTypeEntity getEngineTypeId() {
		return engineTypeId;
	}

	public void setEngineTypeId(EngineTypeEntity engineTypeId) {
		this.engineTypeId = engineTypeId;
	}

	public AssetGroupEntity getAssetGroupId() {
		return assetGroupId;
	}

	public void setAssetGroupId(AssetGroupEntity assetGroupId) {
		this.assetGroupId = assetGroupId;
	}

	public int getServiceScheduleId() {
		return serviceScheduleId;
	}
	public void setServiceScheduleId(int serviceScheduleId) {
		this.serviceScheduleId = serviceScheduleId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getScheduleName() {
		return scheduleName;
	}
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public int getDurationSchedule() {
		return durationSchedule;
	}

	public void setDurationSchedule(int durationSchedule) {
		this.durationSchedule = durationSchedule;
	}

	public long getEngineHoursSchedule() {
		return engineHoursSchedule;
	}

	public void setEngineHoursSchedule(long engineHoursSchedule) {
		this.engineHoursSchedule = engineHoursSchedule;
	}

	public String getDbmsPartCode() {
		return dbmsPartCode;
	}

	public void setDbmsPartCode(String dbmsPartCode) {
		this.dbmsPartCode = dbmsPartCode;
	}


}
