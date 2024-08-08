package remote.wise.service.datacontract;

//20231127 CR452-AirtelApiIntegration-prasad
public class SimApnVO {
	

    public String attachedApnName;
    public String attachedApnIpType;
    public String attchedApnType;
    
    
	public String getAttachedApnName() {
		return attachedApnName;
	}
	public void setAttachedApnName(String attachedApnName) {
		this.attachedApnName = attachedApnName;
	}
	public String getAttachedApnIpType() {
		return attachedApnIpType;
	}
	public void setAttachedApnIpType(String attachedApnIpType) {
		this.attachedApnIpType = attachedApnIpType;
	}
	public String getAttchedApnType() {
		return attchedApnType;
	}
	public void setAttchedApnType(String attchedApnType) {
		this.attchedApnType = attchedApnType;
	}
	
	@Override
	public String toString() {
		return "SimApnVO [attachedApnName=" + attachedApnName + ", attachedApnIpType=" + attachedApnIpType
				+ ", attchedApnType=" + attchedApnType + "]";
	}
    
    

}
