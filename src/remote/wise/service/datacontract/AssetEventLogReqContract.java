package remote.wise.service.datacontract;

public class AssetEventLogReqContract 
{
	String SerialNumber;
	String Period;
	//Added by Rajani Nagaraju - DefectId: 685, 20130703 - To display event Log for LandmarkAlerts properly
	int loginTenancyId;
	
	public AssetEventLogReqContract()
	{
		SerialNumber=null;
		Period=null;
		//Added by Rajani Nagaraju - DefectId: 685, 20130703 - To display event Log for LandmarkAlerts properly
		loginTenancyId=0;
	}

	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return SerialNumber;
	}

	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}

	/**
	 * @return the period
	 */
	public String getPeriod() {
		return Period;
	}

	/**
	 * @param period the period to set
	 */
	public void setPeriod(String period) {
		Period = period;
	}

	//Added by Rajani Nagaraju - DefectId: 685, 20130703 - To display event Log for LandmarkAlerts properly
	public int getLoginTenancyId() {
		return loginTenancyId;
	}

	public void setLoginTenancyId(int loginTenancyId) {
		this.loginTenancyId = loginTenancyId;
	}
	
	
	
}
