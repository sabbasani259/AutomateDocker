package remote.wise.service.datacontract;

public class ConfigAppRespContract {

	int configuration_id;
	 String services;
	 boolean isStatus;
	 String modifiedBy;
	 String modifiedOn;
	 
	public int getConfiguration_id() {
		return configuration_id;
	}
	public void setConfiguration_id(int configuration_id) {
		this.configuration_id = configuration_id;
	}
	public String getServices() {
		return services;
	}
	public void setServices(String services) {
		this.services = services;
	}
	public boolean isStatus() {
		return isStatus;
	}
	public void setStatus(boolean isStatus) {
		this.isStatus = isStatus;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public String getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	 
}
