/**
 * 
 */
package remote.wise.businessentity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author kprabhu5
 *
 */
public class LandmarkFactEntity extends BaseBusinessEntity implements Serializable {

	private TenancyDimensionEntity tenacyDimensionId;
	private LandmarkDimensionEntity landmarkDimensionId;
	private Timestamp time_key;
	private String serialNumber;
	private int noOfArrivals;
	private int noOfDepartures;
	private long totalDurationAtLandmark;
	private long longestDurationAtLandmark;
	private int year;
	private int timeCount;	
		
	public TenancyDimensionEntity getTenacyDimensionId() {
		return tenacyDimensionId;
	}
	public void setTenacyDimensionId(TenancyDimensionEntity tenacyDimensionId) {
		this.tenacyDimensionId = tenacyDimensionId;
	}
	public LandmarkDimensionEntity getLandmarkDimensionId() {
		return landmarkDimensionId;
	}
	public void setLandmarkDimensionId(LandmarkDimensionEntity landmarkDimensionId) {
		this.landmarkDimensionId = landmarkDimensionId;
	}
	public Timestamp getTime_key() {
		return time_key;
	}
	public void setTime_key(Timestamp time_key) {
		this.time_key = time_key;
	}
	
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public int getNoOfArrivals() {
		return noOfArrivals;
	}
	public void setNoOfArrivals(int noOfArrivals) {
		this.noOfArrivals = noOfArrivals;
	}
	public int getNoOfDepartures() {
		return noOfDepartures;
	}
	public void setNoOfDepartures(int noOfDepartures) {
		this.noOfDepartures = noOfDepartures;
	}
	public long getTotalDurationAtLandmark() {
		return totalDurationAtLandmark;
	}
	public void setTotalDurationAtLandmark(long totalDurationAtLandmark) {
		this.totalDurationAtLandmark = totalDurationAtLandmark;
	}
	public long getLongestDurationAtLandmark() {
		return longestDurationAtLandmark;
	}
	public void setLongestDurationAtLandmark(long longestDurationAtLandmark) {
		this.longestDurationAtLandmark = longestDurationAtLandmark;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getTimeCount() {
		return timeCount;
	}
	public void setTimeCount(int timeCount) {
		this.timeCount = timeCount;
	}
	
	
	
}
