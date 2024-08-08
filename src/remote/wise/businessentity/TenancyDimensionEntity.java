package remote.wise.businessentity;

import java.sql.Timestamp;

public class TenancyDimensionEntity extends BaseBusinessEntity
{
	int tenancyId;
	int tenacy_Dimension_Id;
	
	public int getTenacy_Dimension_Id() {
		return tenacy_Dimension_Id;
	}
	public void setTenacy_Dimension_Id(int tenacy_Dimension_Id) {
		this.tenacy_Dimension_Id = tenacy_Dimension_Id;
	}
	String tenancyName;
	int parentTenancyId;
	int tenancyTypeId;
	String tenancyTypeName;
	String parentTenancyName;
	private Timestamp last_updated;	
	
	public Timestamp getLast_updated() {
		return last_updated;
	}
	public void setLast_updated(Timestamp last_updated) {
		this.last_updated = last_updated;
	}
	public int getTenancyId() {
		return tenancyId;
	}
	public void setTenancyId(int tenancyId) {
		this.tenancyId = tenancyId;
	}
	public String getTenancyName() {
		return tenancyName;
	}
	public void setTenancyName(String tenancyName) {
		this.tenancyName = tenancyName;
	}
	public int getParentTenancyId() {
		return parentTenancyId;
	}
	public void setParentTenancyId(int parentTenancyId) {
		this.parentTenancyId = parentTenancyId;
	}
	public int getTenancyTypeId() {
		return tenancyTypeId;
	}
	public void setTenancyTypeId(int tenancyTypeId) {
		this.tenancyTypeId = tenancyTypeId;
	}
	public String getTenancyTypeName() {
		return tenancyTypeName;
	}
	public void setTenancyTypeName(String tenancyTypeName) {
		this.tenancyTypeName = tenancyTypeName;
	}
	public String getParentTenancyName() {
		return parentTenancyName;
	}
	public void setParentTenancyName(String parentTenancyName) {
		this.parentTenancyName = parentTenancyName;
	}
	
	
}
