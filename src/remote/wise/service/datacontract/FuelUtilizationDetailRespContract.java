package remote.wise.service.datacontract;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.TreeMap;

public class FuelUtilizationDetailRespContract {
	private String serialNumber;
	private String period;
	//added by smitha on 13th sept 2013
//	HashMap<Integer,String> hourFuelLevelMap;
	TreeMap<Integer,String> hourFuelLevelMap;
	
	/**
	 * @return the hourFuelLevelMap
	 */
	public TreeMap<Integer, String> getHourFuelLevelMap() {
		return hourFuelLevelMap;
	}
	/**
	 * @param hourFuelLevelMap the hourFuelLevelMap to set
	 */
	public void setHourFuelLevelMap(TreeMap<Integer, String> hourFuelLevelMap) {
		this.hourFuelLevelMap = hourFuelLevelMap;
	}
	//ended on 13th sept 2013
	/*private String timeDuration;
	private String fuelLevel;
	public String getFuelLevel() {
		return fuelLevel;
	}
	public void setFuelLevel(String fuelLevel) {
		this.fuelLevel = fuelLevel;
	}*/
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	
	
	/*public String getTimeDuration() {
		return timeDuration;
	}
	public void setTimeDuration(String timeDuration) {
		this.timeDuration = timeDuration;
	}*/
	
	
}
