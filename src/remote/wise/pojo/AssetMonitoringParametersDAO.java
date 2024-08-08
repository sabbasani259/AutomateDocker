package remote.wise.pojo;

import java.sql.Timestamp;



public class AssetMonitoringParametersDAO {

	public AssetMonitoringParametersDAO(){
		
	}
	
	
	private int parameterID;
	private String parameterValue;
	private String parameterName;
	
	

	private Timestamp transactionTS;
	//private String Serial_Number;
	
	private String Serial_Number;
	private double EngineRunningBand1;
	private double EngineRunningBand2;
	private double EngineRunningBand3;
	private double EngineRunningBand4;
	
	/**
	 * @return the parameterName
	 */
	public String getParameterName() {
		return parameterName;
	}
	/**
	 * @param parameterName the parameterName to set
	 */
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	
	public Timestamp getTransactionTS() {
		return transactionTS;
	}
	public void setTransactionTS(Timestamp transactionTS) {
		this.transactionTS = transactionTS;
	}
	public int getParameterID() {
		return parameterID;
	}
	public void setParameterID(int parameterID) {
		this.parameterID = parameterID;
	}
	public String getParameterValue() {
		return parameterValue;
	}
	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}


	private double EngineRunningBand5;
	public String getSerial_Number() {
		return Serial_Number;
	}
	public void setSerial_Number(String serial_Number) {
		Serial_Number = serial_Number;
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
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getEngineHours() {
		return engineHours;
	}
	public void setEngineHours(double engineHours) {
		this.engineHours = engineHours;
	}
	public int getEngoneON() {
		return engoneON;
	}
	public void setEngoneON(int engoneON) {
		this.engoneON = engoneON;
	}

	private double EngineRunningBand6;
	private double EngineRunningBand7;
	private double EngineRunningBand8;
	private double latitude;
	private double longitude;
	private double engineHours;
	private int engoneON;
	
	
	
	
}
