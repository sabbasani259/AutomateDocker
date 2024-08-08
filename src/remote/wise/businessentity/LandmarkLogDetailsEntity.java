package remote.wise.businessentity;

import java.io.Serializable;
import java.sql.Timestamp;

public class LandmarkLogDetailsEntity extends BaseBusinessEntity implements Serializable
{
	AssetEntity serialNumber;
	Timestamp transactionTimestamp;
	LandmarkEntity landmarkId;
	String machineLandmarkStatus;
	AssetEventEntity assetEventId;
	
	
	/**
	 * @return the serialNumber
	 */
	public AssetEntity getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(AssetEntity serialNumber) {
		this.serialNumber = serialNumber;
	}
	/**
	 * @return the transactionTimestamp
	 */
	public Timestamp getTransactionTimestamp() {
		return transactionTimestamp;
	}
	/**
	 * @param transactionTimestamp the transactionTimestamp to set
	 */
	public void setTransactionTimestamp(Timestamp transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
	}
	/**
	 * @return the landmarkId
	 */
	public LandmarkEntity getLandmarkId() {
		return landmarkId;
	}
	/**
	 * @param landmarkId the landmarkId to set
	 */
	public void setLandmarkId(LandmarkEntity landmarkId) {
		this.landmarkId = landmarkId;
	}
	/**
	 * @return the machineLandmarkStatus
	 */
	public String getMachineLandmarkStatus() {
		return machineLandmarkStatus;
	}
	/**
	 * @param machineLandmarkStatus the machineLandmarkStatus to set
	 */
	public void setMachineLandmarkStatus(String machineLandmarkStatus) {
		this.machineLandmarkStatus = machineLandmarkStatus;
	}
	/**
	 * @return the assetEventId
	 */
	public AssetEventEntity getAssetEventId() {
		return assetEventId;
	}
	/**
	 * @param assetEventId the assetEventId to set
	 */
	public void setAssetEventId(AssetEventEntity assetEventId) {
		this.assetEventId = assetEventId;
	}
	
	
	
}
