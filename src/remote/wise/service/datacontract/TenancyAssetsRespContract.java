package remote.wise.service.datacontract;

import java.util.List;

public class TenancyAssetsRespContract 
{
	List<String> serialNumberList;

	public TenancyAssetsRespContract()
	{
		serialNumberList = null;
	}
	
	/**
	 * @return VIN List
	 */
	public List<String> getSerialNumberList() {
		return serialNumberList;
	}

	/**
	 * @param serialNumberList List of VINs as String input
	 */
	public void setSerialNumberList(List<String> serialNumberList) {
		this.serialNumberList = serialNumberList;
	}
	
}
