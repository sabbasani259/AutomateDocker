package remote.wise.businessentity;

import java.io.Serializable;
import java.sql.Timestamp;
// DF:2013:12:13 : Converision from Long to Double for all the required fields : Suprava
public class AssetMonitoringFactDataEntity extends BaseBusinessEntity implements Serializable
{
	TenancyDimensionEntity tenancyId;
	AccountDimensionEntity accountDimensionId;
	AssetClassDimensionEntity assetClassDimensionId;
	Timestamp timeKey;
	int timeCount;
	int year;
	String Address;
	String state;
	String city;
	
String aggregate_param_data;
	
	

	public String getAggregate_param_data() {
		return aggregate_param_data;
	}
	public void setAggregate_param_data(String aggregate_param_data) {
		this.aggregate_param_data = aggregate_param_data;
	}
	

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
String serialNumber;
	
	
public double getFuelUsedIdle() {
		return fuelUsedIdle;
	}
	public void setFuelUsedIdle(double fuelUsedIdle) {
		this.fuelUsedIdle = fuelUsedIdle;
	}
	public double getFuelUsedWorking() {
		return fuelUsedWorking;
	}
	public void setFuelUsedWorking(double fuelUsedWorking) {
		this.fuelUsedWorking = fuelUsedWorking;
	}
	public double getEngineRunningBand1() {
		return EngineRunningBand1;
	}
	public void setEngineRunningBand1(double engineRunningBand1) {
		EngineRunningBand1 = engineRunningBand1;
	}
	public double getEngineRunningBand2() {
		return EngineRunningBand2;
	}
	public void setEngineRunningBand2(double engineRunningBand2) {
		EngineRunningBand2 = engineRunningBand2;
	}
	public double getEngineRunningBand3() {
		return EngineRunningBand3;
	}
	public void setEngineRunningBand3(double engineRunningBand3) {
		EngineRunningBand3 = engineRunningBand3;
	}
	public double getEngineRunningBand4() {
		return EngineRunningBand4;
	}
	public void setEngineRunningBand4(double engineRunningBand4) {
		EngineRunningBand4 = engineRunningBand4;
	}
	public double getEngineRunningBand5() {
		return EngineRunningBand5;
	}
	public void setEngineRunningBand5(double engineRunningBand5) {
		EngineRunningBand5 = engineRunningBand5;
	}
	public double getEngineRunningBand6() {
		return EngineRunningBand6;
	}
	public void setEngineRunningBand6(double engineRunningBand6) {
		EngineRunningBand6 = engineRunningBand6;
	}
	public double getEngineRunningBand7() {
		return EngineRunningBand7;
	}
	public void setEngineRunningBand7(double engineRunningBand7) {
		EngineRunningBand7 = engineRunningBand7;
	}
	public double getEngineRunningBand8() {
		return EngineRunningBand8;
	}
	public void setEngineRunningBand8(double engineRunningBand8) {
		EngineRunningBand8 = engineRunningBand8;
	}
	double fuelUsedIdle;
	double fuelUsedWorking;
	double EngineRunningBand1;
	double EngineRunningBand2;
	double EngineRunningBand3;
	double EngineRunningBand4;
	double EngineRunningBand5;
	double EngineRunningBand6;
	double EngineRunningBand7;
	double EngineRunningBand8;
	Timestamp lastEngineRun;
	Timestamp lastReported;
	String location;
	double machineHours;
	double engineOffHours;
	String machineName;
	//DefectId:20150105 add new column engine status
	String engineStatus;
	
	Timestamp Created_Timestamp;
	
	
	
	public Timestamp getCreated_Timestamp() {
		return Created_Timestamp;
	}
	public void setCreated_Timestamp(Timestamp created_Timestamp) {
		Created_Timestamp = created_Timestamp;
	}
	public TenancyDimensionEntity getTenancyId() {
		return tenancyId;
	}
	public void setTenancyId(TenancyDimensionEntity tenancyId) {
		this.tenancyId = tenancyId;
	}
	
	public AccountDimensionEntity getAccountDimensionId() {
		return accountDimensionId;
	}
	public void setAccountDimensionId(AccountDimensionEntity accountDimensionId) {
		this.accountDimensionId = accountDimensionId;
	}
	public AssetClassDimensionEntity getAssetClassDimensionId() {
		return assetClassDimensionId;
	}
	public void setAssetClassDimensionId(
			AssetClassDimensionEntity assetClassDimensionId) {
		this.assetClassDimensionId = assetClassDimensionId;
	}
	public Timestamp getTimeKey() {
		return timeKey;
	}
	public void setTimeKey(Timestamp timeKey) {
		this.timeKey = timeKey;
	}
	public int getTimeCount() {
		return timeCount;
	}
	public void setTimeCount(int timeCount) {
		this.timeCount = timeCount;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	
	

	public Timestamp getLastEngineRun() {
		return lastEngineRun;
	}
	public void setLastEngineRun(Timestamp lastEngineRun) {
		this.lastEngineRun = lastEngineRun;
	}
	public Timestamp getLastReported() {
		return lastReported;
	}
	public void setLastReported(Timestamp lastReported) {
		this.lastReported = lastReported;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	public double getMachineHours() {
		return machineHours;
	}
	public void setMachineHours(double machineHours) {
		this.machineHours = machineHours;
	}
	public double getEngineOffHours() {
		return engineOffHours;
	}
	public void setEngineOffHours(double engineOffHours) {
		this.engineOffHours = engineOffHours;
	}
	
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
	/**
	 * @return the engineStatus
	 */
	public String getEngineStatus() {
		return engineStatus;
	}
	/**
	 * @param engineStatus the engineStatus to set
	 */
	public void setEngineStatus(String engineStatus) {
		this.engineStatus = engineStatus;
	}
			
	
}
