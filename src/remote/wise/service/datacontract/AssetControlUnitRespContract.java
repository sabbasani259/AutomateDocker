package remote.wise.service.datacontract;

import java.sql.Timestamp;

/**
 * @author Deepthi
 *
 */
public class AssetControlUnitRespContract 
{
	private String Unit_ID;
	private String SIM_NO;
	private String IMEI;
	private String SerialNumber;
	private Timestamp registrationDate;
	
	/**
	 * @return the unit_ID
	 */
	public String getUnit_ID() {
		return Unit_ID;
	}
	/**
	 * @param unit_ID the unit_ID to set
	 */
	public void setUnit_ID(String unit_ID) {
		Unit_ID = unit_ID;
	}
	/**
	 * @return the sIM_NO
	 */
	public String getSIM_NO() {
		return SIM_NO;
	}
	/**
	 * @param sIM_NO the sIM_NO to set
	 */
	public void setSIM_NO(String sIM_NO) {
		SIM_NO = sIM_NO;
	}
	/**
	 * @return the iMEI
	 */
	public String getIMEI() {
		return IMEI;
	}
	/**
	 * @param iMEI the iMEI to set
	 */
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
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
	 * @return the registrationDate
	 */
	public Timestamp getRegistrationDate() {
		return registrationDate;
	}
	/**
	 * @param registrationDate the registrationDate to set
	 */
	public void setRegistrationDate(Timestamp registrationDate) {
		this.registrationDate = registrationDate;
	}
	
	

}
