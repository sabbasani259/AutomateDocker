package remote.wise.businessentity;

/**
 * @author Rajani Nagaraju
 *
 */
public class ExtendedMonitoringParameters extends BaseBusinessEntity
{
	int extendedParameterId;
	String extendedParameterName;
	
	/**
	 * @return the extendedParameterId
	 */
	public int getExtendedParameterId() {
		return extendedParameterId;
	}
	/**
	 * @param extendedParameterId the extendedParameterId to set
	 */
	public void setExtendedParameterId(int extendedParameterId) {
		this.extendedParameterId = extendedParameterId;
	}
	/**
	 * @return the extendedParameterName
	 */
	public String getExtendedParameterName() {
		return extendedParameterName;
	}
	/**
	 * @param extendedParameterName the extendedParameterName to set
	 */
	public void setExtendedParameterName(String extendedParameterName) {
		this.extendedParameterName = extendedParameterName;
	}
	
	
}
