package remote.wise.businessentity;

import java.sql.Timestamp;

public class ConfigAppEntity extends BaseBusinessEntity {

	 int configuration_id;
	 String services;
	 boolean isStatus;
	 String modifiedBy;
	 Timestamp modifiedOn;
	 
	 public ConfigAppEntity() {}
	 
	 public ConfigAppEntity(int configuration_id)
		{
			super.key = new Integer(configuration_id);
			ConfigAppEntity c = (ConfigAppEntity)read(this);
			if(c!= null)
			{
				setConfiguration_id(c.getConfiguration_id());
				setServices(c.getServices());
				setStatus(c.isStatus());			
				setModifiedBy(c.getModifiedBy());
				setModifiedOn(c.getModifiedOn());
			}
			
		}
	 
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

	public Timestamp getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
		
}
