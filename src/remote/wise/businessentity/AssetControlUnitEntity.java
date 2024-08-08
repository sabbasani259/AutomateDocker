package remote.wise.businessentity;

import java.sql.Timestamp;

public class AssetControlUnitEntity extends BaseBusinessEntity
{
	private String serialNumber;
	private ControlUnitEntity controlUnitId;
	private String simNo;
	private String iccidNo;
	private String imeiNo;
	private Timestamp registrationDate;
	
	public AssetControlUnitEntity(){}
	
	public AssetControlUnitEntity(String serialNumber)
	{
		super.key = new String(serialNumber);
		AssetControlUnitEntity e= (AssetControlUnitEntity)read(this);
		if(e!= null)
		{
			setSerialNumber(e.getSerialNumber());
			setControlUnitId(e.getControlUnitId());
			setSimNo(e.getSimNo());
			setIccidNo(e.getIccidNo());
			setImeiNo(e.getImeiNo());
			setRegistrationDate(e.getRegistrationDate());
		}
	}
	
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public ControlUnitEntity getControlUnitId() {
		return controlUnitId;
	}
	public void setControlUnitId(ControlUnitEntity controlUnitId) {
		this.controlUnitId = controlUnitId;
	}
	public String getSimNo() {
		return simNo;
	}
	public void setSimNo(String simNo) {
		this.simNo = simNo;
	}
	public String getIccidNo() {
		return iccidNo;
	}

	public void setIccidNo(String iccidNo) {
		this.iccidNo = iccidNo;
	}
	public String getImeiNo() {
		return imeiNo;
	}
	public void setImeiNo(String imeiNo) {
		this.imeiNo = imeiNo;
	}

	public Timestamp getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Timestamp registrationDate) {
		this.registrationDate = registrationDate;
	}
	
	
}
