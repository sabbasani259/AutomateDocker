package remote.wise.businessentity;

import java.util.Set;

public class TenancyTypeEntity extends BaseBusinessEntity
{
	private int tenancy_type_id;
	private String tenancy_type_name;
	private Set<TenancyEntity> tenancy_list;
	
	public TenancyTypeEntity(){}
	
	public TenancyTypeEntity(int tenancyTypeId)
	{
		super.key = new Integer(tenancyTypeId);
		TenancyTypeEntity t = (TenancyTypeEntity)read(this);
		
		if(t!= null)
		{
			setTenancy_type_id(t.getTenancy_type_id());
			setTenancy_type_name(t.getTenancy_type_name());
		}
	}
	
	
	public int getTenancy_type_id() {
		return tenancy_type_id;
	}
	public void setTenancy_type_id(int tenancy_type_id) {
		this.tenancy_type_id = tenancy_type_id;
	}
	public String getTenancy_type_name() {
		return tenancy_type_name;
	}
	public void setTenancy_type_name(String tenancy_type_name) {
		this.tenancy_type_name = tenancy_type_name;
	}
	
	public Set<TenancyEntity> getTenancy_list() {
		return tenancy_list;
	}
	public void setTenancy_list(Set<TenancyEntity> tenancy_list) {
		this.tenancy_list = tenancy_list;
	}
	
	
}
