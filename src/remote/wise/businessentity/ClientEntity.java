package remote.wise.businessentity;

import java.util.Set;

public class ClientEntity extends BaseBusinessEntity
{
	private int client_id;
	private String client_name;
	private IndustryEntity industry_id;
	private Set<AccountEntity> account_list;
	private Set<ContactEntity> contact_list;
	private Set<RoleEntity> role_list;
	private Set<TenancyEntity> tenancy_list;
	private Set<CustomAssetGroupEntity> custom_asset_group_list;
	private Set<AssetClassEntity> asset_class_list;
	private Set<AssetTypeEntity> asset_type_list;
	private Set<AssetGroupEntity> asset_group_list;
	private Set<CustomAssetClassEntity> custom_asset_class_list;
	
	public ClientEntity(){}
	
	public ClientEntity(int client_id)
	{
		super.key = new Integer(client_id);
		ClientEntity c=(ClientEntity) read(this);
		if(c!=null)
		{
			setClient_id(c.client_id);
			setClient_name(c.client_name);
			setIndustry_id(c.industry_id);
		}
	}
	public int getClient_id() {
		return client_id;
	}

	public void setClient_id(int client_id) {
		this.client_id = client_id;
	}

	public String getClient_name() {
		return client_name;
	}

	public void setClient_name(String client_name) {
		this.client_name = client_name;
	}

	public IndustryEntity getIndustry_id() {
		return industry_id;
	}

	public void setIndustry_id(IndustryEntity industry_id) {
		this.industry_id = industry_id;
	}

	public Set<AccountEntity> getAccount_list() {
		return account_list;
	}

	public void setAccount_list(Set<AccountEntity> account_list) {
		this.account_list = account_list;
	}

	public Set<ContactEntity> getContact_list() {
		return contact_list;
	}

	public void setContact_list(Set<ContactEntity> contact_list) {
		this.contact_list = contact_list;
	}

	public Set<RoleEntity> getRole_list() {
		return role_list;
	}

	public void setRole_list(Set<RoleEntity> role_list) {
		this.role_list = role_list;
	}

	public Set<TenancyEntity> getTenancy_list() {
		return tenancy_list;
	}

	public void setTenancy_list(Set<TenancyEntity> tenancy_list) {
		this.tenancy_list = tenancy_list;
	}

	public Set<CustomAssetGroupEntity> getCustom_asset_group_list() {
		return custom_asset_group_list;
	}

	public void setCustom_asset_group_list(
			Set<CustomAssetGroupEntity> custom_asset_group_list) {
		this.custom_asset_group_list = custom_asset_group_list;
	}

	public Set<AssetClassEntity> getAsset_class_list() {
		return asset_class_list;
	}

	public void setAsset_class_list(Set<AssetClassEntity> asset_class_list) {
		this.asset_class_list = asset_class_list;
	}

	public Set<AssetTypeEntity> getAsset_type_list() {
		return asset_type_list;
	}

	public void setAsset_type_list(Set<AssetTypeEntity> asset_type_list) {
		this.asset_type_list = asset_type_list;
	}

	public Set<AssetGroupEntity> getAsset_group_list() {
		return asset_group_list;
	}

	public void setAsset_group_list(Set<AssetGroupEntity> asset_group_list) {
		this.asset_group_list = asset_group_list;
	}

	public Set<CustomAssetClassEntity> getCustom_asset_class_entity() {
		return custom_asset_class_list;
	}

	public void setCustom_asset_class_entity(
			Set<CustomAssetClassEntity> custom_asset_class_entity) {
		this.custom_asset_class_list = custom_asset_class_entity;
	}

	public void addnewAccount(AccountEntity newAccount)
	{
		if(newAccount==null)
			throw new IllegalArgumentException("Trying to add NULL Account to the client");
		
		if(newAccount.getClient_id() != null)
			newAccount.getClient_id().getAccount_list().remove(newAccount);
		
		newAccount.setClient_id(this);
		account_list.add(newAccount);
	}
	
	public void addnewCustomAssetGroup(CustomAssetGroupEntity newCustomAssetGroup)
	{
		if(newCustomAssetGroup==null)
			throw new IllegalArgumentException("Trying to add NULL custom asset group to the client");
		
		if(newCustomAssetGroup.getClient_id() != null)
			newCustomAssetGroup.getClient_id().getAsset_group_list().remove(newCustomAssetGroup);
					
		newCustomAssetGroup.setClient_id(this);
		custom_asset_group_list.add(newCustomAssetGroup);
	}
	
	public void addnewContact(ContactEntity newContact)
	{
		if(newContact==null)
			throw new IllegalArgumentException("Trying to add NULL contact to the client");
		
		if(newContact.getClient_id() != null)
			newContact.getClient_id().getContact_list().remove(newContact);
					
		newContact.setClient_id(this);
		contact_list.add(newContact);
	}
	
	public void addnewRole(RoleEntity newRole)
	{
		if(newRole==null)
			throw new IllegalArgumentException("Trying to add NULL role to the client");
		
		if(newRole.getClient_id() != null)
			newRole.getClient_id().getContact_list().remove(newRole);
					
		newRole.setClient_id(this);
		role_list.add(newRole);
	}
	
	public void addnewTenancy(TenancyEntity newTenancy)
	{
		if(newTenancy==null)
			throw new IllegalArgumentException("Trying to add NULL tenancy to the client");
		
		if(newTenancy.getClient_id() != null)
			newTenancy.getClient_id().getContact_list().remove(newTenancy);
					
		newTenancy.setClient_id(this);
		tenancy_list.add(newTenancy);
	}
	
	public void addnewAssetGroup(AssetGroupEntity newAssetGroup)
	{
		if(newAssetGroup==null)
			throw new IllegalArgumentException("Trying to add NULL asset group to the client");
		
		if(newAssetGroup.getClient_id() != null)
			newAssetGroup.getClient_id().getAsset_group_list().remove(newAssetGroup);
					
		newAssetGroup.setClient_id(this);
		asset_group_list.add(newAssetGroup);
	}
	
	public void addnewAssetClass(AssetClassEntity newAssetClass)
	{
		if(newAssetClass==null)
			throw new IllegalArgumentException("Trying to add NULL asset class to the client");
		
		if(newAssetClass.getClient_id() != null)
			newAssetClass.getClient_id().getAsset_class_list().remove(newAssetClass);
					
		newAssetClass.setClient_id(this);
		asset_class_list.add(newAssetClass);
	}
	
	public void addnewAssetType(AssetTypeEntity newAssetType)
	{
		if(newAssetType==null)
			throw new IllegalArgumentException("Trying to add NULL asset type to the client");
		
		if(newAssetType.getClient_id() != null)
			newAssetType.getClient_id().getAsset_type_list().remove(newAssetType);
					
		newAssetType.setClient_id(this);
		asset_type_list.add(newAssetType);
	}
	
	public void addnewCustomAssetClass(CustomAssetClassEntity newCustomAssetClass)
	{
		if(newCustomAssetClass==null)
			throw new IllegalArgumentException("Trying to add NULL custom asset class to the client");
		
		if(newCustomAssetClass.getClient_id() != null)
			newCustomAssetClass.getClient_id().getCustom_asset_class_entity().remove(newCustomAssetClass);
					
		newCustomAssetClass.setClient_id(this);
		custom_asset_class_list.add(newCustomAssetClass);
	}
	
}
 