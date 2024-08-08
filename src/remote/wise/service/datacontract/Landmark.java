package remote.wise.service.datacontract;

public class Landmark {
	


int landmarkID;
String landmarkName;

/**
 * @return the landmarkName
 */
public String getLandmarkName() {
	return landmarkName;
}

/**
 * @param landmarkName the landmarkName to set
 */
public void setLandmarkName(String landmarkName) {
	this.landmarkName = landmarkName;
}

/**
 * @return the landmarkID
 */
public int getLandmarkID() {
	return landmarkID;
}

/**
 * @param landmarkID the landmarkID to set
 */
public void setLandmarkID(int landmarkID) {
	this.landmarkID = landmarkID;
}
}
