package remote.wise.pojo;

import java.math.BigDecimal;

public class BreakfastReportPOJO 
{
	String SerialNumber;
	BigDecimal CMH;
	double FuelLevel;
	String location;
	BigDecimal WorkingHrs;
	BigDecimal IdleHrs;
	BigDecimal EngineOffHrs;
	BigDecimal UtilNationalAvg;
	BigDecimal IdleTimeNationalAvg;
	public String getSerialNumber() {
		return SerialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}
	public BigDecimal getCMH() {
		return CMH;
	}
	public void setCMH(BigDecimal cMH) {
		CMH = cMH;
	}
	public double getFuelLevel() {
		return FuelLevel;
	}
	public void setFuelLevel(double fuelLevel) {
		FuelLevel = fuelLevel;
	}
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public BigDecimal getWorkingHrs() {
		return WorkingHrs;
	}
	public void setWorkingHrs(BigDecimal workingHrs) {
		WorkingHrs = workingHrs;
	}
	public BigDecimal getIdleHrs() {
		return IdleHrs;
	}
	public void setIdleHrs(BigDecimal idleHrs) {
		IdleHrs = idleHrs;
	}
	public BigDecimal getEngineOffHrs() {
		return EngineOffHrs;
	}
	public void setEngineOffHrs(BigDecimal engineOffHrs) {
		EngineOffHrs = engineOffHrs;
	}
	public BigDecimal getUtilNationalAvg() {
		return UtilNationalAvg;
	}
	public void setUtilNationalAvg(BigDecimal utilNationalAvg) {
		UtilNationalAvg = utilNationalAvg;
	}
	public BigDecimal getIdleTimeNationalAvg() {
		return IdleTimeNationalAvg;
	}
	public void setIdleTimeNationalAvg(BigDecimal idleTimeNationalAvg) {
		IdleTimeNationalAvg = idleTimeNationalAvg;
	}
	
	
	
}
