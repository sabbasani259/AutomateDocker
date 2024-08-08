package remote.wise.businessentity;

public class UomMasterEntity extends BaseBusinessEntity
{
	private int uomMasterId;
	private String baseUnit;
	private String receivedUnit;
	private double conversionFactor;
	
	
	public int getUomMasterId() {
		return uomMasterId;
	}
	public void setUomMasterId(int uomMasterId) {
		this.uomMasterId = uomMasterId;
	}
	
	
	public String getBaseUnit() {
		return baseUnit;
	}
	public void setBaseUnit(String baseUnit) {
		this.baseUnit = baseUnit;
	}
	
	
	public String getReceivedUnit() {
		return receivedUnit;
	}
	public void setReceivedUnit(String receivedUnit) {
		this.receivedUnit = receivedUnit;
	}
	
	
	public double getConversionFactor() {
		return conversionFactor;
	}
	public void setConversionFactor(double conversionFactor) {
		this.conversionFactor = conversionFactor;
	}
	
	
}
