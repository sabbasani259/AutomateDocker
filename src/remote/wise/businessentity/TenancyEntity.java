package remote.wise.businessentity;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

public class TenancyEntity extends BaseBusinessEntity
{
	private int tenancy_id;
	private String tenancy_name,parent_tenancy_name;
	private TenancyEntity parent_tenancy_id;
	private Set<TenancyEntity> child_tenancies = new HashSet<TenancyEntity>();
	private Set<CustomAssetGroupEntity> asset_group_list;
	private ClientEntity client_id;
	private TenancyTypeEntity tenancy_type_id;
	private Timestamp Operating_Start_Time;
	private Timestamp Operating_End_Time;
	private String createdBy;
	private Timestamp createdDate;
	//DefectId: 839 - Rajani Nagaraju - Handle Machine Movement between tenancies.
	private String tenancyCode;
	
	//Df20180117 @Roopa Multiple BP code changes
		private String mappingCode;
	
	public TenancyEntity(){}
	
	public TenancyEntity(int tenancy_id)
	{
		super.key = new Integer(tenancy_id);
		TenancyEntity t = (TenancyEntity)read(this);
		
		if(t!= null)
		{
			setTenancy_id(t.getTenancy_id());
			setTenancy_name(t.getTenancy_name());
			setParent_tenancy_name(t.getParent_tenancy_name());
			setParent_tenancy_id(t.getParent_tenancy_id());
			setClient_id(t.getClient_id());
			setTenancy_type_id(t.getTenancy_type_id());
			setOperating_Start_Time(t.getOperating_Start_Time());
			setOperating_End_Time(t.getOperating_End_Time());
			setCreatedBy(t.getCreatedBy());
			setCreatedDate(t.getCreatedDate());
			setTenancyCode(t.getTenancyCode());
			setMappingCode(t.getMappingCode());
		}
	}
	
	
		
	//DefectId: 839 - Rajani Nagaraju - Handle Machine Movement between tenancies.
	
	public String getMappingCode() {
		return mappingCode;
	}

	public void setMappingCode(String mappingCode) {
		this.mappingCode = mappingCode;
	}

	public String getTenancyCode() {
		return tenancyCode;
	}

	public void setTenancyCode(String tenancyCode) {
		this.tenancyCode = tenancyCode;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}

	
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public Timestamp getOperating_Start_Time() {
		return Operating_Start_Time;
	}

	public void setOperating_Start_Time(Timestamp operating_Start_Time) {
		Operating_Start_Time = operating_Start_Time;
	}

	public Timestamp getOperating_End_Time() {
		return Operating_End_Time;
	}

	public void setOperating_End_Time(Timestamp operating_End_Time) {
		Operating_End_Time = operating_End_Time;
	}

	public TenancyTypeEntity getTenancy_type_id() {
		return tenancy_type_id;
	}
	public void setTenancy_type_id(TenancyTypeEntity tenancy_type_id) {
		this.tenancy_type_id = tenancy_type_id;
	}
	
	public int getTenancy_id() {
		return tenancy_id;
	}
	public void setTenancy_id(int tenancy_id) {
		this.tenancy_id = tenancy_id;
	}
	
	
	public ClientEntity getClient_id() {
		return client_id;
	}
	public void setClient_id(ClientEntity client_id) {
		this.client_id = client_id;
	}
	
	public String getTenancy_name() {
		return tenancy_name;
	}
	public void setTenancy_name(String tenancy_name) {
		this.tenancy_name = tenancy_name;
	}
	
	public String getParent_tenancy_name() {
		return parent_tenancy_name;
	}
	public void setParent_tenancy_name(String parent_tenancy_name) {
		this.parent_tenancy_name = parent_tenancy_name;
	}
	
	public TenancyEntity getParent_tenancy_id() {
		return parent_tenancy_id;
	}
	public void setParent_tenancy_id(TenancyEntity parent_tenancy_id) {
		this.parent_tenancy_id = parent_tenancy_id;
	}
	
	public Set<TenancyEntity> getChild_tenancies() {
		return child_tenancies;
	}
	public void setChild_tenancies(Set<TenancyEntity> child_tenancies) {
		this.child_tenancies = child_tenancies;
	}
	
	
	public Set<CustomAssetGroupEntity> getAsset_group_list() {
		return asset_group_list;
	}
	public void setAsset_group_list(Set<CustomAssetGroupEntity> asset_group_list) {
		this.asset_group_list = asset_group_list;
	}
	
	public void addChildTenancies(TenancyEntity childTenancy)
	{
		if(childTenancy==null)
			throw new IllegalArgumentException("Trying to add NULL child Tenancy");
		
		if(childTenancy.getParent_tenancy_id() != null)
			childTenancy.getParent_tenancy_id().getChild_tenancies().remove(childTenancy);
		
		childTenancy.setParent_tenancy_id(this);
		child_tenancies.add(childTenancy);
	}
	
	public void addAssetGroups(CustomAssetGroupEntity newAssetGroup)
	{
		if(newAssetGroup==null)
			throw new IllegalArgumentException("Trying to add NULL asset group to tenancy");
		
		if(newAssetGroup.getTenancy_id()!= null)
			newAssetGroup.getTenancy_id().getAsset_group_list().remove(newAssetGroup);
		
		newAssetGroup.setTenancy_id(this);
		asset_group_list.add(newAssetGroup);
	}
}
