package remote.wise.businessentity;

import java.io.Serializable;

public class LandmarkAssetEntity extends BaseBusinessEntity implements Serializable
{
private LandmarkEntity Landmark_id;
private AssetEntity Serial_number;
public LandmarkAssetEntity(){ }
public LandmarkAssetEntity(int Landmark_id, String Serial_number) 
{
	super.key = new Integer(Landmark_id);
	LandmarkAssetEntity e = (LandmarkAssetEntity)read(this);
	if(e!= null)
	{ 
		setLandmark_id(e.getLandmark_id());
		setSerial_number(e.getSerial_number());
	}
}


public LandmarkEntity getLandmark_id() {
	return Landmark_id;
}
public void setLandmark_id(LandmarkEntity landmark_id) {
	Landmark_id = landmark_id;
}
public AssetEntity getSerial_number() {
	return Serial_number;
}
public void setSerial_number(AssetEntity serial_number) {
	Serial_number = serial_number;
}

}
