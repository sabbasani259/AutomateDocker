package remote.wise.service.datacontract;


public class MasterServiceScheduleResponseContract {
	private String serviceName;
    private String scheduleName;   
	private int durationSchedule;
	private long engineHoursSchedule;	
	 private String dbmsPartCode;
	  private int assetTypeId;
	    private int engineTypeId;
	    private int assetGroupId;
	    private String engineTypeName;
		private String asset_group_name;
		private String asset_type_name;

	public String getEngineTypeName() {
			return engineTypeName;
		}
		public void setEngineTypeName(String engineTypeName) {
			this.engineTypeName = engineTypeName;
		}
		public String getAsset_group_name() {
			return asset_group_name;
		}
		public void setAsset_group_name(String asset_group_name) {
			this.asset_group_name = asset_group_name;
		}
		public String getAsset_type_name() {
			return asset_type_name;
		}
		public void setAsset_type_name(String asset_type_name) {
			this.asset_type_name = asset_type_name;
		}
	public int getAssetTypeId() {
			return assetTypeId;
		}
		public void setAssetTypeId(int assetTypeId) {
			this.assetTypeId = assetTypeId;
		}
		public int getEngineTypeId() {
			return engineTypeId;
		}
		public void setEngineTypeId(int engineTypeId) {
			this.engineTypeId = engineTypeId;
		}
		public int getAssetGroupId() {
			return assetGroupId;
		}
		public void setAssetGroupId(int assetGroupId) {
			this.assetGroupId = assetGroupId;
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
		public String getDbmsPartCode() {
		return dbmsPartCode;
	}
	public void setDbmsPartCode(String dbmsPartCode) {
		this.dbmsPartCode = dbmsPartCode;
	}	

}
