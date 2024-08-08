package remote.wise.service.implementation;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetGroupEntity;
import remote.wise.businessentity.AssetTypeEntity;
import remote.wise.businessentity.CatalogValuesEntity;
import remote.wise.businessentity.ClientEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.businessentity.CustomAssetGroupEntity;
import remote.wise.businessentity.EngineTypeEntity;
import remote.wise.businessentity.EventTypeEntity;
import remote.wise.businessentity.IndustryEntity;
import remote.wise.businessentity.LandmarkEntity;
import remote.wise.businessentity.PreferenceEntity;
import remote.wise.businessentity.ReportMasterListEntity;
import remote.wise.businessentity.RoleEntity;
import remote.wise.businessentity.TenancyEntity;
import remote.wise.businessobject.AssetCustomGroupDetailsBO;
import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.businessobject.EventDetailsBO;
import remote.wise.businessobject.IndustryBO;
import remote.wise.businessobject.LandmarkCategoryBO;
import remote.wise.businessobject.ReportDetailsBO;
import remote.wise.businessobject.ServiceDetailsBO;
import remote.wise.businessobject.TenancyBO;
import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;

public class DomainServiceImpl 
{
	public ContactEntity getContactDetails(String login_id)
	{
		UserDetailsBO contactObj= new UserDetailsBO();
		ContactEntity contact=contactObj.getContactEntity(login_id);
		return contact;
	}
	
	public RoleEntity getRoleDetails(int role_id)
	{
		UserDetailsBO roleObj=new UserDetailsBO();
		RoleEntity role=roleObj.getRoleEntity(role_id);
		return role;
	}
	
	public TenancyEntity getTenancyDetails(int tenancy_id)
	{
		TenancyBO tenancyObj= new TenancyBO();
		TenancyEntity tenency=tenancyObj.getTenancyObj(tenancy_id);
		return tenency;
	}
	
	public ClientEntity getClientDetails(int client_id)
	{
		IndustryBO clientObj= new IndustryBO();
		ClientEntity client=clientObj.getClientEntity(client_id);
		return client;
	}
	
	
	public CustomAssetGroupEntity getCustomAssetGroupDetails(int group_id) throws CustomFault
	{
		AssetCustomGroupDetailsBO customAssetGroupObj= new AssetCustomGroupDetailsBO();
		CustomAssetGroupEntity asset_group=customAssetGroupObj.getAssetGroupEntity(group_id);
		return asset_group;
	}
	
	public AssetEntity getAssetDetails(String serialNumber)
	{
		AssetDetailsBO assetObj= new AssetDetailsBO();
		AssetEntity asset=assetObj.getAssetEntity(serialNumber);
		return asset;
	}	
	
	public AccountEntity getAccountObj(int accountId)
	{
		TenancyBO accountObj= new TenancyBO();
		AccountEntity account=accountObj.getAccountObj(accountId);
		return account;
	}
	
	public EventTypeEntity getEventTypeDetails(int eventTypeId)
	{
		EventDetailsBO eventTypeObj = new EventDetailsBO();
		EventTypeEntity eventType =eventTypeObj.getEventTypeDetails(eventTypeId);
		return eventType;
	}
	
	public IndustryEntity getIndustryDetails(int industryId)
	{
		IndustryBO industryObj = new IndustryBO();
		IndustryEntity industry = industryObj.getIndustryEntity(industryId);
		return industry;
	}
	
	public LandmarkEntity getLandmarkEntity(int landmark_id)
	{
		LandmarkCategoryBO landmarkCategoryObj= new LandmarkCategoryBO();
		LandmarkEntity landmark=landmarkCategoryObj.getLandmarkEntity(landmark_id);
		return landmark;
	}
	
	//For User Preference ***** Added on 31st Jan 2013
	
	public PreferenceEntity getUserPreference(int catalogValueId)throws CustomFault
	{
		UserDetailsBO userDetBo=new UserDetailsBO();
		PreferenceEntity UserPreference=userDetBo.getUserPreferenceEntity(catalogValueId);
		return UserPreference;
	}

	public AssetEntity getAssetEntity(String serial_number)
	{
		AssetDetailsBO assetObj= new AssetDetailsBO();
		AssetEntity asset=assetObj.getAssetEntity(serial_number);
		return asset;
	}
	public ReportMasterListEntity getReportDetails(int reportId)
    {
          ReportDetailsBO reportObj= new ReportDetailsBO();
          ReportMasterListEntity report=reportObj.getContactEntity(reportId);
          return report;
    }
	
	public CatalogValuesEntity getcCatalogValues(int catalogValueId)
	{
		ReportDetailsBO reportDetailsBO=new ReportDetailsBO();
		CatalogValuesEntity CatalogValues=reportDetailsBO.getCatalogValues(catalogValueId);
		return CatalogValues;
	}

	public AssetGroupEntity getAssetGroupEntity(int assetGroupId)
	{
		AssetDetailsBO assetDetails=new AssetDetailsBO();
		AssetGroupEntity assetGroup=assetDetails.getAssetGroupEntity(assetGroupId);
		return assetGroup;
	}	
	public EngineTypeEntity getEngineTypeEntity(int engineTypeId)
	{
		ServiceDetailsBO serviceDetails=new ServiceDetailsBO();
		EngineTypeEntity engineType=serviceDetails.getEngineTypeEntity(engineTypeId);
		return engineType;
	}
	public AssetTypeEntity getAssetTypeEntity(int assetTypeId)
	{
		AssetDetailsBO assetDetails=new AssetDetailsBO();
		AssetTypeEntity assetType=assetDetails.getAssetTypeEntity(assetTypeId);
		return assetType;
	}
}

