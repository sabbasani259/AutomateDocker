package remote.wise.businessentity;

import java.util.HashSet;
import java.util.Set;

public class CustomAssetClassEntity extends BaseBusinessEntity
{
	private int Custom_Class_Id;
	private CustomAssetClassEntity Parent_Class_Id;
	private String Custom_class_Name;
	private Set<CustomAssetClassEntity> childCustomClass_list = new HashSet<CustomAssetClassEntity>();
	private ClientEntity client_id;
	
	
	public ClientEntity getClient_id() {
		return client_id;
	}



	public void setClient_id(ClientEntity client_id) {
		this.client_id = client_id;
	}



	public int getCustom_Class_Id() {
		return Custom_Class_Id;
	}



	public void setCustom_Class_Id(int custom_Class_Id) {
		Custom_Class_Id = custom_Class_Id;
	}



	public CustomAssetClassEntity getParent_Class_Id() {
		return Parent_Class_Id;
	}



	public void setParent_Class_Id(CustomAssetClassEntity parent_Class_Id) {
		Parent_Class_Id = parent_Class_Id;
	}



	public String getCustom_class_Name() {
		return Custom_class_Name;
	}



	public void setCustom_class_Name(String custom_class_Name) {
		Custom_class_Name = custom_class_Name;
	}



	public Set<CustomAssetClassEntity> getChildCustomClass_list() {
		return childCustomClass_list;
	}



	public void setChildCustomClass_list(Set<CustomAssetClassEntity> childCustomClass_list) {
		this.childCustomClass_list = childCustomClass_list;
	}



	public void addChildcustomAssetclass(CustomAssetClassEntity childCustomAsset)
	{
		if(childCustomAsset==null)
			throw new IllegalArgumentException("Trying to add NULL Custom AssetClass");
		
		if(childCustomAsset.getParent_Class_Id() != null)
			childCustomAsset.getParent_Class_Id().getChildCustomClass_list().remove(childCustomAsset);
		
		childCustomAsset.setParent_Class_Id(this);
		childCustomClass_list.add(childCustomAsset);
	}
	
	
}
