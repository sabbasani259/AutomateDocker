package remote.wise.service.datacontract;

import java.util.List;

public class PricolRollOffReqContract 
{
	int tenancyId;
	List<String> serialNumber;
	
	/**
	 * @return the tenancyId
	 */
	public int getTenancyId() {
		return tenancyId;
	}
	/**
	 * @param tenancyId the tenancyId to set
	 */
	public void setTenancyId(int tenancyId) {
		this.tenancyId = tenancyId;
	}
	/**
	 * @return the serialNumber
	 */
	public List<String> getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(List<String> serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	
	
}
