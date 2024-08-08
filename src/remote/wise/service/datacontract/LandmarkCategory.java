package remote.wise.service.datacontract;

import java.util.List;

public class LandmarkCategory {
	
	int landmarkCategoryId;
	String landmarkCategoryName;
	List<Landmark> landmark;
	/**
	 * @return the landmarkCategoryId
	 */
	public int getLandmarkCategoryId() {
		return landmarkCategoryId;
	}
	/**
	 * @param landmarkCategoryId the landmarkCategoryId to set
	 */
	public void setLandmarkCategoryId(int landmarkCategoryId) {
		this.landmarkCategoryId = landmarkCategoryId;
	}
	/**
	 * @return the landmarkCategoryName
	 */
	public String getLandmarkCategoryName() {
		return landmarkCategoryName;
	}
	/**
	 * @param landmarkCategoryName the landmarkCategoryName to set
	 */
	public void setLandmarkCategoryName(String landmarkCategoryName) {
		this.landmarkCategoryName = landmarkCategoryName;
	}
	/**
	 * @return the landmark
	 */
	public List<Landmark> getLandmark() {
		return landmark;
	}
	/**
	 * @param landmark the landmark to set
	 */
	public void setLandmark(List<Landmark> landmark) {
		this.landmark = landmark;
	}

}
