package remote.wise.businessobject;

import java.io.File;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

////import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetCustomGroupMapping;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.ClientEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.businessentity.CustomAssetGroupEntity;
import remote.wise.businessentity.GroupUserMapping;
import remote.wise.businessentity.TenancyEntity;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.service.implementation.DomainServiceImpl;
import remote.wise.util.ConnectMySQL;
import org.apache.logging.log4j.Logger;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;

public class AssetCustomGroupDetailsBO extends BaseBusinessObject 
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	
	/*public static WiseLogger businessError = WiseLogger.getLogger("AssetCustomGroupDetailsBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("AssetCustomGroupDetailsBO:","fatalError");*/
	
	String loginId;
	int assetGroupId;
	String assetGroupName;
	String assetGroupDescription;
	int assetGroupTypeId;
	String assetGroupTypeName;
	List<String> serialNumberList;
	int tenancyId;
	

	
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}


	public int getAssetGroupId() {
		return assetGroupId;
	}
	public void setAssetGroupId(int assetGroupId) {
		this.assetGroupId = assetGroupId;
	}


	public String getAssetGroupTypeName() {
		return assetGroupTypeName;
	}
	public void setAssetGroupTypeName(String assetGroupTypeName) {
		this.assetGroupTypeName = assetGroupTypeName;
	}
	public String getAssetGroupName() {
		return assetGroupName;
	}
	public void setAssetGroupName(String assetGroupName) {
		this.assetGroupName = assetGroupName;
	}


	public String getAssetGroupDescription() {
		return assetGroupDescription;
	}
	public void setAssetGroupDescription(String assetGroupDescription) {
		this.assetGroupDescription = assetGroupDescription;
	}


	public int getAssetGroupTypeId() {
		return assetGroupTypeId;
	}
	public void setAssetGroupTypeId(int assetGroupTypeId) {
		this.assetGroupTypeId = assetGroupTypeId;
	}


	public List<String> getSerialNumberList() {
		return serialNumberList;
	}
	public void setSerialNumberList(List<String> serialNumberList) {
		this.serialNumberList = serialNumberList;
	}


	public int getTenancyId() {
		return tenancyId;
	}
	public void setTenancyId(int tenancyId) {
		this.tenancyId = tenancyId;
	}

	
	
	//*********************************************get CustomAssetGroupEntity for a given CustomassetGroupId*******************************************
	/** This method returns AssetGroupEntity for a given Asset group id
	 * @param group_id CustomAssetGroupId as Integer input
	 * @return Returns CustomAssetgroupEntity for a given CustomAssetGroupId
	 * @throws CustomFault
	 */
	public CustomAssetGroupEntity getAssetGroupEntity(int group_id) throws CustomFault
	{
		//Internal call to base business entity
		CustomAssetGroupEntity assetGroupEntityobj=new CustomAssetGroupEntity(group_id);
		
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger=BusinessErrorLoggerClass.logger;
		
		if(assetGroupEntityobj.getGroup_id() == 0)
		{
			bLogger.error("CustomAssetGroupID specified is Invalid");
			throw new CustomFault("CustomAssetGroupID specified is Invalid");
		}
		
		return assetGroupEntityobj;
	} 
	
	
	
	//****************************get AssetControlUnit Entity for Serial Number array*****************************************************
	public ArrayList<AssetControlUnitEntity> getAssetControlUnitEntity(List<String> sl_no)
	{
		ArrayList<AssetControlUnitEntity> assetControlUnitEntity = new ArrayList<AssetControlUnitEntity>();
		
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger fLogger = FatalLoggerClass.logger;
		
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
        try{
		
		Query query= session.createQuery("from AssetControlUnitEntity where serialNumber in (:list)").setParameterList("list",sl_no );
		Iterator itr=query.list().iterator();
		while(itr.hasNext())
		{
			AssetControlUnitEntity assetControlUnit = (AssetControlUnitEntity)itr.next();
			assetControlUnitEntity.add(assetControlUnit);
			
			
			
		}}
		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}
        
        finally
        {
              if(session.getTransaction().isActive())
              {
                    session.getTransaction().commit();
              }
              
              if(session.isOpen())
              {
                    session.flush();
                    session.close();
              }
              
        }
		
		return assetControlUnitEntity;
	}
	
	//****************************End of get AssetControlUnit Entity for Serial Number array*****************************************************
	
	//*********************************************************get AssetEntity for Serial number array**************************************************
	public ArrayList<AssetEntity> getAssetEntity(List<String> sl_no) throws CustomFault
	{
		
		ArrayList<AssetEntity> asset_entity_list= new ArrayList<AssetEntity>();
		//Logger businessError = Logger.getLogger("businessErrorLogger");
       // Logger fatalError = Logger.getLogger("fatalErrorLogger");
        
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger=BusinessErrorLoggerClass.logger;
		
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try{
        	//get Client Details
			Properties prop = new Properties();
			String clientName=null;
				
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");
      
			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
			//END of get Client Details
			
			 if(! (session.isOpen() ))
             {
                         session = HibernateUtil.getSessionFactory().getCurrentSession();
                         session.getTransaction().begin();
             } 


		//get AssetContorlUnit Entity List for a given serial number list
		ArrayList<AssetControlUnitEntity> assetControlUnitEntity=getAssetControlUnitEntity(sl_no);
		
		if(assetControlUnitEntity==null || assetControlUnitEntity.isEmpty())
		{
			throw new CustomFault("Invalid serial number specified");
		}
		
		 
		
		Query query= session.createQuery("from AssetEntity where client_id="+clientEntity.getClient_id()+" and active_status=true and serial_number in (:list)").setParameterList("list",assetControlUnitEntity );
		Iterator itr=query.list().iterator();
		while(itr.hasNext())
		{
			AssetEntity asset = (AssetEntity)itr.next();
			asset_entity_list.add(asset);
			
		}
        }	catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}
        
        finally
        {
              if(session.getTransaction().isActive())
              {
                    session.getTransaction().commit();
              }
              
              if(session.isOpen())
              {
                    session.flush();
                    session.close();
              }
              
        }
		
		return asset_entity_list;
	}
	
	
	//**********************************************************Map assets to asset group************************************************************
	/** This method maps the specified serialNumberList to the given customAssetGroup
	 * @param serialNumberList List of serialNumbers as String input
	 * @param customAssetGroup customAssetGroup entity
	 * @throws CustomFault
	 */
	public void assetGroupSerialNumMapping(List<String> serialNumberList,CustomAssetGroupEntity customAssetGroup) throws CustomFault
	{

		HashMap<String,AssetEntity> assetEntityDetails=getVINAssetEntityMap(serialNumberList);

		HashMap<String,AssetCustomGroupMapping> serialNumAssetGroupMapEntity = new HashMap<String,AssetCustomGroupMapping>();

		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger fLogger = FatalLoggerClass.logger;
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try
		{

			Query query= session.createQuery("from AssetCustomGroupMapping where group_id="+customAssetGroup.getGroup_id());
			Iterator itr=query.list().iterator();
			while(itr.hasNext())
			{
				AssetCustomGroupMapping assetCustomGroupMap = (AssetCustomGroupMapping)itr.next();
				serialNumAssetGroupMapEntity.put(assetCustomGroupMap.getSerial_number().getSerial_number().getSerialNumber(), assetCustomGroupMap);

			}

			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			List<String> newSerialNumberList = new LinkedList<String>();		
			newSerialNumberList.addAll(serialNumberList);

			for(int i=0; i<serialNumberList.size(); i++)
			{
				if( (serialNumAssetGroupMapEntity.containsKey(serialNumberList.get(i))) )
				{
					serialNumAssetGroupMapEntity.remove(serialNumberList.get(i));
					newSerialNumberList.remove(serialNumberList.get(i));
				}
			}


			int j,k;
			for(j=0, k=0; j<newSerialNumberList.size()|| k<serialNumAssetGroupMapEntity.size(); j++,k++)
			{

				//update the assetCustomGroupMapping with different serialNumber for the existing groupId
				if(j<newSerialNumberList.size()&& k<serialNumAssetGroupMapEntity.size())
				{
					AssetCustomGroupMapping customAssetGroupMap = (AssetCustomGroupMapping)serialNumAssetGroupMapEntity.values().toArray()[k];
					Query updateQuery = session.createQuery("update AssetCustomGroupMapping set serial_number='"+newSerialNumberList.get(j)+"' " +
							" where group_id="+customAssetGroupMap.getGroup_id().getGroup_id()+" and serial_number='"+customAssetGroupMap.getSerial_number().getSerial_number().getSerialNumber()+"' ");
					updateQuery.executeUpdate();
				}

				//New AssetCustomGroupMapping
				else if(j<newSerialNumberList.size() && k>=serialNumAssetGroupMapEntity.size())
				{
					AssetCustomGroupMapping newAssetCustomGroupMap = new AssetCustomGroupMapping();
					newAssetCustomGroupMap.setGroup_id(customAssetGroup);
					newAssetCustomGroupMap.setSerial_number(assetEntityDetails.get(newSerialNumberList.get(j)));
					//session.save(newAssetCustomGroupMap);

					session.save("AssetCustomGroupMapping",newAssetCustomGroupMap);
					//					newAssetCustomGroupMap.save();
					/*Query insertQuery = session.createQuery("insert into AssetCustomGroupMapping(group_id,serial_number) values('customAssetGroup','newSerialNumberList.get(j)')"); 
					insertQuery.executeUpdate();*/
				}

				//Delete AssetCustomGroupMapping
				else if (j>=newSerialNumberList.size() && k<serialNumAssetGroupMapEntity.size())
				{
					AssetCustomGroupMapping customAssetGroupMap = (AssetCustomGroupMapping)serialNumAssetGroupMapEntity.values().toArray()[k];
					Query deleteQuery = session.createQuery("delete from AssetCustomGroupMapping where group_id="+customAssetGroupMap.getGroup_id().getGroup_id()+" and serial_number='"+customAssetGroupMap.getSerial_number().getSerial_number().getSerialNumber()+"' ");
					deleteQuery.executeUpdate();
				}

			}


		}

		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
			e.printStackTrace();
		}
		 
		finally
		{
			if(session.getTransaction().isActive())
			{
				session.getTransaction().commit();
			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}

		}
	}
	//**********************************************************END of Map assets to asset group************************************************************
	//*****************************************START OF SET CUSTOM ASSET GROUP**************************************************************************************************
	
	

	//*****************************************START OF SET CUSTOM ASSET GROUP**************************************************************************************************
	/** This method insert/update the custom asset groups
	 * @param loginId userLoginId as String input
	 * @param assetGroupId CustomAssetGroupId as Integer input - If specified updates the same else Inserts a new customAssetgroup
	 * @param assetGroupName Name of the CustomAssetGroup
	 * @param assetGroupDescription Description of CustomAssetgroup
	 * @param assetGroupTypeId AssetGroupTypeId under which the specified assetGroup to be placed - Optional parameter
	 * @param serialNumberList List of serialNumbers to be attached to the specified customAssetGroup
	 * @param tenancyId tenancy under which the specified assetGroup to be placed
	 * @param clientId client under which the specified assetGroup to be placed
	 * @return Returns the assetGroupId of the specified assetGroup
	 * @throws CustomFault customException is thrown if the parameter specified is Invalid
	 */
	public int setAssetGroup(String loginId,int assetGroupId,String assetGroupName,String assetGroupDescription, int assetGroupTypeId,List<String> serialNumberList,int inputTenancyId, int clientId) throws CustomFault
	{
		int customAssetGroupId=0;

		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger=BusinessErrorLoggerClass.logger;
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try
		{
			//get Client Details
			Properties prop = new Properties();
			String clientName=null;
				
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");
      
			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity1 = industryBoObj.getClientEntity(clientName);
			//END of get Client Details
			if(! (session.isOpen() ))
                  {
                              session = HibernateUtil.getSessionFactory().getCurrentSession();
                              session.getTransaction().begin();
                  } 

			//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
			//Get the List of all tenancyIds - Including Pseudo Tenancies
			int tenancyId=0;
			/*Query q = session.createQuery(" from TenancyEntity t where t.tenancyCode = ( select a.tenancyCode from TenancyEntity a where a.tenancy_id='"+inputTenancyId+"') " +
											" order by t.tenancy_id ");*/
			
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			
			Query q = session.createQuery(" from TenancyEntity t where t.mappingCode = ( select a.mappingCode from TenancyEntity a where a.tenancy_id='"+inputTenancyId+"') " +
					" order by t.tenancy_id ");
			
			Iterator it = q.list().iterator();
			if(it.hasNext())
			{
				TenancyEntity tenancy = (TenancyEntity)it.next();
				tenancyId = tenancy.getTenancy_id();
			}
			
			
			//check whether the GroupType specified is under the given tenancy
			CustomAssetGroupEntity assetGroupTypeEntity=null;
			if(assetGroupTypeId != 0)
			{
				assetGroupTypeEntity=getAssetGroupEntity(assetGroupTypeId);
				if(assetGroupTypeEntity == null || assetGroupTypeEntity.getGroup_id()==0)
				{
					throw new CustomFault("Invalid Asset Group Type ID");
				}

				int groupTypeTenancy = assetGroupTypeEntity.getTenancy_id().getTenancy_id();
				if(tenancyId!= groupTypeTenancy)
				{
					throw new CustomFault("Asset Group Type specified is of different Tenancy");
				}

			}

			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			//check if group name already exists
			int duplicateGroupName = 0;
			Query query = session.createQuery("from CustomAssetGroupEntity where level=2 and active_status=1 and client_id="+clientEntity1.getClient_id()+" and group_name='"+assetGroupName+"'"+"and tenancy_id="+tenancyId);
			Iterator itr=query.list().iterator();
			while(itr.hasNext())
			{
				CustomAssetGroupEntity duplicateAssetGroup = (CustomAssetGroupEntity) itr.next();

				if(assetGroupId!=0 && assetGroupId!= duplicateAssetGroup.getGroup_id())
					duplicateGroupName =1;

				if(assetGroupId==0 && duplicateAssetGroup.getGroup_id()!=0)
					duplicateGroupName=1;

			}
			if(duplicateGroupName==1)
			{
				customAssetGroupId =-1;
				// for Defect Id- 1003 by Shriti on 31-07-2013
				throw new CustomFault("Machine Group with the given name already exists.Please give some other name for Machine Group");
			}


			//New record insertion to custom asset group
			if(assetGroupId==0)
			{
				DomainServiceImpl domainService = new DomainServiceImpl();

				CustomAssetGroupEntity assetGroup = new CustomAssetGroupEntity();
				assetGroup.setLevel(2); // Level 1 indicates assetGroupType and level 2 indicated asset group
				assetGroup.setGroup_name(assetGroupName);
				assetGroup.setGroup_description(assetGroupDescription);
				assetGroup.setActive_status(1);

				//get Tenancy and client details
				TenancyEntity tenancyEntity= domainService.getTenancyDetails(tenancyId);
				if(tenancyEntity==null || tenancyEntity.getTenancy_id()==0)
				{
					throw new CustomFault("Invalid Tenancy ID");
				}
				assetGroup.setTenancy_id(tenancyEntity);

				ClientEntity clientEntity= domainService.getClientDetails(clientId);
				if(clientEntity==null || clientEntity.getClient_id()==0)
				{
					throw new CustomFault("Invalid Client ID");
				}
				assetGroup.setClient_id(clientEntity);

				if(assetGroupTypeEntity != null)
				{
					assetGroup.setAsset_group_type(assetGroupTypeEntity);
					assetGroup.setParent_group_name(assetGroupTypeEntity.getGroup_name());
				}

				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}

				session.save(assetGroup);

				//Map assets under this newly created asset group
				if(serialNumberList != null)
					assetGroupSerialNumMapping(serialNumberList,assetGroup);

			}


			//update custom asset group
			else
			{

				CustomAssetGroupEntity customAssetGroup=getAssetGroupEntity(assetGroupId);

				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}

				if(customAssetGroup==null || customAssetGroup.getGroup_id()==0)
				{

					throw new CustomFault("Asset Group ID specified deoesnot exists for updation");
				}
				else
				{
					customAssetGroup.setGroup_name(assetGroupName);
					customAssetGroup.setGroup_description(assetGroupDescription);

					if(assetGroupTypeId!=0)
					{
						customAssetGroup.setAsset_group_type(assetGroupTypeEntity);
						customAssetGroup.setParent_group_name(assetGroupTypeEntity.getGroup_name());
					}
					else if(assetGroupTypeId==0){
						customAssetGroup.setAsset_group_type(null);
						customAssetGroup.setParent_group_name(null);
					}

					session.update(customAssetGroup);

				}

				//Map assets under this  asset group
				if(serialNumberList!= null)
				{
					assetGroupSerialNumMapping(serialNumberList,customAssetGroup);
				}
			}


			//get AssetGroupID and return it
			if(assetGroupId==0)
			{
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}

				Query newQuery = session.createQuery("from CustomAssetGroupEntity where group_name = '"+assetGroupName+"' and level=2 and client_id="+clientId+" and tenancy_id ="+tenancyId +" and active_status=1");
				Iterator newItr = newQuery.list().iterator();
				while(newItr.hasNext())
				{
					CustomAssetGroupEntity newCustomAssetGroup = (CustomAssetGroupEntity)newItr.next();
					customAssetGroupId = newCustomAssetGroup.getGroup_id();
				}


			}

			else
			{
				customAssetGroupId = assetGroupId;
			}

			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
		}

		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}

		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}

		finally
		{
			if(session.getTransaction().isActive())
			{
				session.getTransaction().commit();
			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}

		}

		return customAssetGroupId;
	}

	
	
	
	//************************************************END OF SET CUSTOM ASSET GROUP*************************************************************************
	
	
	
	//*************************************************Get group wise asset list*********************************************************************
	public HashMap<Integer,List<String>> getGroupWiseAssetList(List<String> serial_number) throws CustomFault
	{
		
		HashMap<Integer,List<String>> hashmap = new HashMap<Integer,List<String>>();
		List<String> serial_num_list = new LinkedList<String>();
		//Logger businessError = Logger.getLogger("businessErrorLogger");
        //Logger fatalError = Logger.getLogger("fatalErrorLogger");
        
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger=BusinessErrorLoggerClass.logger;
		
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try{
		//get Asset entities for the list of serial number
		ArrayList<AssetEntity> asset_entity_list = getAssetEntity(serial_number);
		if(asset_entity_list == null || asset_entity_list.isEmpty())
		{
			throw new CustomFault("Invalid Serial Numbers");
		}
		
		//get list of assetgroups to serial number mapping info from AssetCustomGroupMapping
		
		
		Query query= session.createQuery("from AssetCustomGroupMapping where serial_number in (:list) order by group_id").setParameterList("list",asset_entity_list );
		Iterator itr = query.list().iterator();
		ListIterator itrprev = query.list().listIterator();
		int i=1;
		
		while(itr.hasNext())
		{
			
			AssetCustomGroupMapping asset_group_mapping = (AssetCustomGroupMapping) itr.next();
						
			if(i!=1)
			{
				
				AssetCustomGroupMapping asset_group_mapping_prev = (AssetCustomGroupMapping) itrprev.previous();
				
				
				if( (asset_group_mapping_prev.getGroup_id().getGroup_id()) == (asset_group_mapping.getGroup_id().getGroup_id()))
				{
					itrprev.next();
					serial_num_list.add(asset_group_mapping.getSerial_number().getSerial_number().getSerialNumber());
					
					itrprev.next();
					if(!itr.hasNext())
					{
						hashmap.put(asset_group_mapping.getGroup_id().getGroup_id(), serial_num_list);
					}
				}
				else
				{
					itrprev.next();
					hashmap.put(asset_group_mapping_prev.getGroup_id().getGroup_id(), serial_num_list);
					serial_num_list = new LinkedList<String>();
					serial_num_list.add(asset_group_mapping.getSerial_number().getSerial_number().getSerialNumber());
					itrprev.next();
					
					if(!itr.hasNext())
					{
						hashmap.put(asset_group_mapping.getGroup_id().getGroup_id(), serial_num_list);
					}
				}
				
				
			}
			
			
			if(i==1)
			{
							
				itrprev.next();
				serial_num_list.add(asset_group_mapping.getSerial_number().getSerial_number().getSerialNumber());
				if(!itr.hasNext())
				{
					hashmap.put(asset_group_mapping.getGroup_id().getGroup_id(), serial_num_list);
				}
			}
			
			
			i++;
			
		}
		
		
		
        }catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}
        
        finally
        {
              if(session.getTransaction().isActive())
              {
                    session.getTransaction().commit();
              }
              
              if(session.isOpen())
              {
                    session.flush();
                    session.close();
              }
              
        }
		
		return hashmap;
		
	}
	
	//*************************************************END of group wise asset list*********************************************************************
	
	
	
	
	//************************************************START OF GET CUSTOM ASSET GROUP*************************************************************************
	/** DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies 
	 * This method returns the details of all custom AssetGroups of the specified tenancy OR specified customAssetGroup/customAssetGroupType
	 * @param loginId userLoginId
	 * @param assetGroupTypeId assetGroupTypeId for which the customAssetGroup details has to be returned
	 * @param assetGroupId assetGroupId for which the customAssetGroup details has to be returned
	 * @param serialNumberList List of serialNumbers for which the customAssetGroup to which it is mapped has to be returned
	 * @param tenancyIdList customAssetGroup details will be returned for this specified List of tenancyId 
	 * @param isOtherTenancy customAssetGroup details to be returned for the tenancy other than the userLogin tenancy
	 * @return Returns the details of all custom AssetGroups of the specified tenancy OR specified customAssetGroup/customAssetGroupType
	 * @throws CustomFault when the mandatory input parameters are Invalid
	 */
	public List<AssetCustomGroupDetailsBO> getAssetGroup(String loginId, int assetGroupTypeId, int assetGroupId, List<String> serialNumberList,
															List<Integer> tenancyIdList, boolean isOtherTenancy) throws CustomFault
	{
		
		List<AssetCustomGroupDetailsBO> customAssetGroupDetails = new LinkedList<AssetCustomGroupDetailsBO>();
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	    session.beginTransaction();
	    
	    Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger=BusinessErrorLoggerClass.logger;
	    
	    try
	    {
	    	//get Client Details
			Properties prop = new Properties();
			String clientName=null;
				
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");
      
			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
			//END of get Client Details	 
            if(! (session.isOpen() ))
                  {
                              session = HibernateUtil.getSessionFactory().getCurrentSession();
                              session.getTransaction().begin();
                  } 

	    	//check for the correctness of login ID	   

			DomainServiceImpl domainService = new DomainServiceImpl();
			ContactEntity contact=domainService.getContactDetails(loginId);
			
			if(contact.getContact_id()==null)
			{
				throw new CustomFault("Invalid Login ID");
			}
		
			if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        session.getTransaction().begin();
            }
			
			//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
			//Get the List of all tenancyIds - Including Pseudo Tenancies
			List<Integer> newTenancyIdList = new LinkedList<Integer>();
			ListToStringConversion conversion = new ListToStringConversion();
			String tenancyIdListString = conversion.getIntegerListString(tenancyIdList).toString();
			
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			//Query q1 = session.createQuery(" from TenancyEntity where tenancyCode in ( select a.tenancyCode from TenancyEntity a where a.tenancy_id in ("+tenancyIdListString+"))");
			
			Query q1 = session.createQuery(" from TenancyEntity where mappingCode in ( select a.mappingCode from TenancyEntity a where a.tenancy_id in ("+tenancyIdListString+"))");
			
			Iterator it1 = q1.list().iterator();
			while(it1.hasNext())
			{
				TenancyEntity tenancy = (TenancyEntity)it1.next();
				newTenancyIdList.add(tenancy.getTenancy_id());
			}
			
			
			//get the TenancyEntityList for the given list of tenancy Ids
			TenancyBO tenancyBo = new TenancyBO();
			List<TenancyEntity> tenancyEntityList = tenancyBo.getTenancyObjList(newTenancyIdList);
			
			if(tenancyEntityList==null || tenancyEntityList.isEmpty())
			{
				throw new CustomFault("Invalid Tenancy Specified");
			}
			
			//check for the validity of asset group type id
			if(assetGroupTypeId!=0)
			{
				CustomAssetGroupEntity assetGroupTypeEntity = getAssetGroupEntity(assetGroupTypeId);
				if(assetGroupTypeEntity.getGroup_id()==0 || assetGroupTypeEntity.getLevel()!=1)
				{
					throw new CustomFault("Asset Group Type Specified doesnot exists");
				}
			}
			
			if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        session.getTransaction().begin();
            }
		
			//if asset group id is specified
			if(assetGroupId!= 0)
			{
				List<AssetCustomGroupDetailsBO> customGroupBoList = new LinkedList<AssetCustomGroupDetailsBO>();
				AssetCustomGroupDetailsBO customGroupBusinessObject = new AssetCustomGroupDetailsBO();
				
				Query assetGroupQuery =null;
				
				//This means get the customAssetGroup to which the user belongs to
				if(contact.getIs_tenancy_admin()== 0 && isOtherTenancy==false)
				{
					assetGroupQuery = session.createQuery("select a.group_id, a.group_name , a. group_description,  d.group_id, d.group_name, " +
							" a.tenancy_id from CustomAssetGroupEntity a LEFT OUTER JOIN a.asset_group_type d , GroupUserMapping b where " +
							" a.level=2 and a.group_id='"+assetGroupId+"' and a.active_status=1 and a.group_id = b.group_id and b.contact_id='"+contact.getContact_id()+"' " +
							" and a.client_id="+clientEntity.getClient_id()+" and a.tenancy_id in (:list)").setParameterList("list", tenancyEntityList);
				}
				else
				{
					assetGroupQuery = session.createQuery("select a.group_id, a.group_name , a. group_description, d.group_id, d.group_name, " +
							" a.tenancy_id  from CustomAssetGroupEntity a LEFT OUTER JOIN a.asset_group_type d where a.level=2 and a.group_id='"+assetGroupId+"' and a.active_status=1 and " +
								" a.client_id="+clientEntity.getClient_id()+" and a.tenancy_id in (:list)").setParameterList("list", tenancyEntityList);
				}
				
				Iterator itr = assetGroupQuery.list().iterator();
				Object[] result=null;
				
				while(itr.hasNext())
				{
					result = (Object[])itr.next();
					Integer customAssetGroupId = (Integer)result[0];
										
					if(assetGroupTypeId !=0) 
					{
						if( result[3]==null )
						{
							throw new CustomFault("Asset Group specified is not under the given Asset group type");
						}
						else
						{
							Integer groupType = (Integer)result[3];
							if(assetGroupTypeId!=groupType)
							{
								throw new CustomFault("Asset Group specified is not under the given Asset group type");
							}
						}
					}
					
					customGroupBusinessObject.setAssetGroupId(customAssetGroupId);
					customGroupBusinessObject.setAssetGroupName(result[1].toString());
					if(result[2]!=null)
						customGroupBusinessObject.setAssetGroupDescription(result[2].toString());
					if(result[3]!=null)
						customGroupBusinessObject.setAssetGroupTypeId((Integer)result[3]);
					if(result[4]!=null)
						customGroupBusinessObject.setAssetGroupTypeName(result[4].toString());
					customGroupBusinessObject.setLoginId(loginId);
					
					TenancyEntity tenancy = (TenancyEntity)result[5];
					customGroupBusinessObject.setTenancyId(tenancy.getTenancy_id());
					
					
					//get the list of serial numbers associated with the given custom asset group
					Query q =  session.createQuery("from AssetCustomGroupMapping where group_id="+customAssetGroupId);
					Iterator it = q.list().iterator();
					
					List<String> serialNumber = new LinkedList<String>();
					while(it.hasNext())
					{
						AssetCustomGroupMapping assetGroupMapping = (AssetCustomGroupMapping)it.next();
						if(serialNumberList == null|| serialNumberList.isEmpty())
						{
							serialNumber.add(assetGroupMapping.getSerial_number().getSerial_number().getSerialNumber());
						}
						else
						{
							int flag =0;
							for(int p=0; p<serialNumberList.size(); p++)
							{
								if(serialNumberList.get(p).equals(assetGroupMapping.getSerial_number().getSerial_number()))
								{
									flag=1;
								}
							}
							if(flag==1)
							{
								serialNumber.add(assetGroupMapping.getSerial_number().getSerial_number().getSerialNumber());
							}
						}
					}
					
					customGroupBusinessObject.setSerialNumberList(serialNumber);
					
					customGroupBoList.add(customGroupBusinessObject);
				}
							
				customAssetGroupDetails.addAll(customGroupBoList);
			}
			
		
			//else if (assetGroupId == 0 && (serialNumberList==null || serialNumberList.isEmpty()))
			else
			{
				List<AssetCustomGroupDetailsBO> customGroupBo = new LinkedList<AssetCustomGroupDetailsBO>();
				
				Query queryString = null;
				
				String basicSelectQuery = null;
				String basicFromQuery = null;
				String basicWhereQuery = null;
				String basicOrderQuery = null;
				
				basicSelectQuery = " select a.group_id, a.group_name , a. group_description, d.group_id, d.group_name, a.tenancy_id ";
				basicFromQuery = " from CustomAssetGroupEntity a LEFT OUTER JOIN a.asset_group_type d";
				basicWhereQuery = " where a.level=2 and a.active_status=1";
				basicOrderQuery = "order by a.group_id";
				
				if(contact.getIs_tenancy_admin()== 0 && isOtherTenancy==false)
				{
					basicFromQuery = basicFromQuery+ ", GroupUserMapping b ";
					basicWhereQuery = basicWhereQuery + " and a.group_id = b.group_id  and b.contact_id='"+contact.getContact_id()+"' ";
				}
				
				if(assetGroupTypeId!=0)
				{
					basicWhereQuery = basicWhereQuery+ " and a.asset_group_type=" +assetGroupTypeId;
				}
				
				if(! (serialNumberList==null || serialNumberList.isEmpty()) )
				{
					//ListToStringConversion conversion = new ListToStringConversion();
		        	String serialNumberListAsString = conversion.getStringList(serialNumberList).toString();
					
		        	basicSelectQuery = basicSelectQuery + ", c.serial_number ";
		        	basicFromQuery = basicFromQuery + " , AssetCustomGroupMapping c ";
					basicWhereQuery = basicWhereQuery + " and a.group_id= c.group_id and c.serial_number in ("+serialNumberListAsString+") ";
					
				}
				
				basicWhereQuery = basicWhereQuery + " and a.tenancy_id in (:list)";
				
				queryString = session.createQuery(basicSelectQuery+basicFromQuery+basicWhereQuery+basicOrderQuery).setParameterList("list", tenancyEntityList);
				
				Object[] result=null;
				Iterator itrtr = queryString.list().iterator();
				
				HashMap<Integer,AssetCustomGroupDetailsBO> groupIdDetailsMap = new HashMap<Integer,AssetCustomGroupDetailsBO>();
				List<Integer> userAccessibleGroupIdList = new LinkedList<Integer>();
				
				int isFirst=0;
				while(itrtr.hasNext())
				{
					isFirst++;
					result = (Object[])itrtr.next();
					
					Integer customAssetGroupId = (Integer)result[0];
										
					if(! (serialNumberList==null || serialNumberList.isEmpty()) )
					{
						if(groupIdDetailsMap.containsKey(customAssetGroupId))
						{
							AssetCustomGroupDetailsBO updateBO = groupIdDetailsMap.get(customAssetGroupId);
							List<String> serialNumList = updateBO.getSerialNumberList();
							if(serialNumList==null)
							{
								serialNumList = new LinkedList<String>();
							}
							
							AssetEntity asset = (AssetEntity) result[6];
							serialNumList.add(asset.getSerial_number().getSerialNumber());
							updateBO.setSerialNumberList(serialNumList);
							
							continue;
						}
						
					}
					
					AssetCustomGroupDetailsBO customGroupBusinessObject = new AssetCustomGroupDetailsBO();
					customGroupBusinessObject.setAssetGroupId(customAssetGroupId);
					customGroupBusinessObject.setAssetGroupName(result[1].toString());
					if(result[2]!=null)
						customGroupBusinessObject.setAssetGroupDescription(result[2].toString());
					if(result[3]!=null)
						customGroupBusinessObject.setAssetGroupTypeId((Integer)result[3]);
					if(result[4]!=null)
						customGroupBusinessObject.setAssetGroupTypeName(result[4].toString());
					customGroupBusinessObject.setLoginId(loginId);
					
					TenancyEntity tenancy = (TenancyEntity) result[5];
					customGroupBusinessObject.setTenancyId(tenancy.getTenancy_id());
					
					groupIdDetailsMap.put(customAssetGroupId, customGroupBusinessObject);
					userAccessibleGroupIdList.add(customAssetGroupId);
					
					customGroupBo.add(customGroupBusinessObject);
					
					if ( (isFirst==1) && (! (serialNumberList==null || serialNumberList.isEmpty()) ) )
					{
						if(groupIdDetailsMap.containsKey(customAssetGroupId))
						{
							AssetCustomGroupDetailsBO updateBO = groupIdDetailsMap.get(customAssetGroupId);
							List<String> serialNumList = updateBO.getSerialNumberList();
							if(serialNumList==null)
							{
								serialNumList = new LinkedList<String>();
							}
							AssetEntity asset = (AssetEntity) result[6];
							serialNumList.add(asset.getSerial_number().getSerialNumber());
							updateBO.setSerialNumberList(serialNumList);
							
							
						}
					}
				}
				
				//get the list of serialNumbers associated with the assetgroupList
				if ( (! (userAccessibleGroupIdList==null || userAccessibleGroupIdList.isEmpty()) ) && (serialNumberList==null || serialNumberList.isEmpty()) )
				{
				//	ListToStringConversion conversion = new ListToStringConversion();
					String assetGroupIdAsString = conversion.getIntegerListString(userAccessibleGroupIdList).toString();
					
					Query getSerialNumList = session.createQuery(" from AssetCustomGroupMapping where group_id in ("+assetGroupIdAsString+") ");
					Iterator itr = getSerialNumList.list().iterator();
					
					while(itr.hasNext())
					{
						AssetCustomGroupMapping assetCustomGroupMap = (AssetCustomGroupMapping) itr.next();
						
						//place the serialNumber in customAssetGroupDetailsBO
						AssetCustomGroupDetailsBO updateBO = groupIdDetailsMap.get(assetCustomGroupMap.getGroup_id().getGroup_id());
						List<String> serialNumList = updateBO.getSerialNumberList();
						if(serialNumList==null)
						{
							serialNumList = new LinkedList<String>();
						}
						serialNumList.add(assetCustomGroupMap.getSerial_number().getSerial_number().getSerialNumber());
						updateBO.setSerialNumberList(serialNumList);
					}
				}
					
				customAssetGroupDetails.addAll( customGroupBo);
				
			}
			
			if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        session.getTransaction().begin();
            }
	    }
		
	    catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}
        
        finally
        {
              if(session.getTransaction().isActive())
              {
                    session.getTransaction().commit();
              }
              
              if(session.isOpen())
              {
                    session.flush();
                    session.close();
              }
              
        }
				
		return customAssetGroupDetails;
	}
	//************************************************END OF GET CUSTOM ASSET GROUP*************************************************************************


	//**********************************************get serialNumber - AssetEntity Map for Serial number array****************************************
	/** This method returns a Map with serialNumber as key and the corresponding AssetEntity as its value
	 * @param sl_no list of serialNumbers
	 * @return Returns HashMap<serialNumber,AssetEntity>
	 * @throws CustomFault
	 */
	public HashMap<String,AssetEntity> getVINAssetEntityMap(List<String> sl_no) throws CustomFault
	{
		
		HashMap<String,AssetEntity> asset_entity_map= new  HashMap<String,AssetEntity>();
		
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger fLogger = FatalLoggerClass.logger;
		
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	    session.beginTransaction();
		
		try
		{
			//get Client Details
			Properties prop = new Properties();
			String clientName=null;
				
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");
      
			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
			//END of get Client Details					   
			if(! (session.isOpen() ))
                  {
                              session = HibernateUtil.getSessionFactory().getCurrentSession();
                              session.getTransaction().begin();
                  } 

			ListToStringConversion conversion = new ListToStringConversion();
        	String serialNumberListAsString = conversion.getStringList(sl_no).toString();
			
			Query query= session.createQuery("from AssetEntity where serial_number in ("+serialNumberListAsString+") and client_Id="+clientEntity.getClient_id()+" and active_status=true");
			Iterator itr=query.list().iterator();
			while(itr.hasNext())
			{
				AssetEntity asset = (AssetEntity)itr.next();
				asset_entity_map.put(asset.getSerial_number().getSerialNumber(), asset);
							
			}
			
		}
		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}
		finally
        {
              if(session.getTransaction().isActive())
              {
                    session.getTransaction().commit();
              }
              
              if(session.isOpen())
              {
                    session.flush();
                    session.close();
              }
              
        }
		
		return asset_entity_map;
	}
	
	//************************************************* START OF DELETE ASSET GROUP ******************************************************************
	/** This method deletes the specifies CustomAssetGroup
	 * @param assetGroupId customAssetGroupId to be deleted
	 * @return Returns the Status string of deletion
	 * @throws CustomFault custom exception is thrown if assetGroupId is not specified/Invalid
	 */
	public String deleteAssetGroup(int assetGroupId) throws CustomFault
	{
		String status = "SUCCESS";
		
		//Logger businessError = Logger.getLogger("businessErrorLogger");
        //Logger fatalError = Logger.getLogger("fatalErrorLogger");
		
        
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger=BusinessErrorLoggerClass.logger;
		
        CustomAssetGroupEntity customAssetGroup = null;
		try
		{
			//validate the asset group id 
			customAssetGroup = getAssetGroupEntity(assetGroupId);
			
			if(customAssetGroup==null ||customAssetGroup.getGroup_id()==0)
			{
				throw new CustomFault("Invalid Asset Group Id");
			}
		}
		catch(CustomFault e)
		{
	       	status = "FAILURE";
	       	bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		       
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
        try
        {
			Query query1 = session.createQuery("delete from GroupUserMapping where group_id="+assetGroupId);
			int row1= query1.executeUpdate();
			
			
			Query query2 = session.createQuery("delete from AssetCustomGroupMapping where group_id="+assetGroupId);
			int row2= query2.executeUpdate();
			
			//DF20190823:Abhishek: To delete the group from CustomAssetGroupSnapshot Table.
			Query query3 = session.createQuery("delete from CustomAssetGroupSnapshotEntity where Group_ID="+assetGroupId);
			int row3= query3.executeUpdate();
			
			customAssetGroup.setActive_status(0);
			session.update(customAssetGroup);
			
		}
        
       	catch(Exception e)
		{
			status = "FAILURE";
			fLogger.fatal("Exception :"+e);
		}
        
        finally
        {
              if(session.getTransaction().isActive())
              {
                    session.getTransaction().commit();
              }
              
              if(session.isOpen())
              {
                    session.flush();
                    session.close();
              }
              
        }
		
		return status;
	}

	//************************************************* START OF DELETE ASSET GROUP *****************************************************************
	
	
	//************************************************START OF GET ASSET GROUP TYPE*******************************************************************
	/** DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies 
	 * This method returns the detail list of required custom asset group types
	 * @param loginId User LoginId
	 * @param tenancyIdList List of tenancyId for which AssetGroupType details is required
	 * @param assetGroupTypeId AssetGroupTypeId for which the details has to be returned
	 * @param isOtherTenancy specifies whether the assetGroupType of login tenancy OR tenancy specified in tenancyIdList(For CC,Admin) should be returned
	 * @return Returns the details of required AssetGroupType
	 * @throws CustomFault custom exception is thrown if 
	 */
	public List<AssetCustomGroupDetailsBO> getAssetGroupType(String loginId,List<Integer> inputTenancyIdList, int assetGroupTypeId, boolean isOtherTenancy) throws CustomFault
	{
		List<AssetCustomGroupDetailsBO> customassetGroupBoList = new LinkedList<AssetCustomGroupDetailsBO>();
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger=BusinessErrorLoggerClass.logger;
		
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
        
		try
		{
	        DomainServiceImpl domainService = new DomainServiceImpl();
			ContactEntity contact=domainService.getContactDetails(loginId);
			//get Client Details
			Properties prop = new Properties();
			String clientName=null;
				
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");
      
			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
			//END of get Client Details	  
			if(! (session.isOpen() ))
                  {
                              session = HibernateUtil.getSessionFactory().getCurrentSession();
                              session.getTransaction().begin();
                  } 

			//check for the correctness of login ID
			if(contact==null || contact.getContact_id()==null)
			{
				throw new CustomFault("Invalid Login ID");
			}
		
			//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
			//Get the List of all tenancyIds - Including Pseudo Tenancies
			List<Integer> tenancyIdList = new LinkedList<Integer>();
			ListToStringConversion conversion = new ListToStringConversion();
			String tenancyIdListString = conversion.getIntegerListString(inputTenancyIdList).toString();
			
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			//Query q = session.createQuery(" from TenancyEntity where tenancyCode in ( select a.tenancyCode from TenancyEntity a where a.tenancy_id in ("+tenancyIdListString+"))");
			
			
			Query q = session.createQuery(" from TenancyEntity where mappingCode in ( select a.mappingCode from TenancyEntity a where a.tenancy_id in ("+tenancyIdListString+"))");
			
			Iterator it = q.list().iterator();
			while(it.hasNext())
			{
				TenancyEntity tenancy = (TenancyEntity)it.next();
				tenancyIdList.add(tenancy.getTenancy_id());
			}
			
			
			
			//get the TenancyEntityList for the given list of tenancy Ids
			TenancyBO tenancyBo = new TenancyBO();
			List<TenancyEntity> tenancyEntityList = tenancyBo.getTenancyObjList(tenancyIdList);
			
			if(tenancyEntityList==null || tenancyEntityList.isEmpty())
			{
				throw new CustomFault("Invalid Tenancy List Specified");
			}
			
			if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        session.getTransaction().begin();
            }
			
			//if the user is tenancyAdmin get all asset group types specified under the given tenancy
			if(contact.getIs_tenancy_admin()==1 || isOtherTenancy == true)
			{
				Query query;
				
				if(assetGroupTypeId!=0)
				{
					//If assetGroupTypeId is specified
					query = session.createQuery("from CustomAssetGroupEntity where level=1 and active_status=1 and client_Id="+clientEntity.getClient_id()+" and group_id = "+assetGroupTypeId+" and tenancy_id in (:list)").setParameterList("list", tenancyEntityList);
				}
				
				else
				{
					//If assetGroupTypeId is not specified
					query = session.createQuery("from CustomAssetGroupEntity where level=1 and active_status=1 and client_Id="+clientEntity.getClient_id()+" and tenancy_id in (:list)").setParameterList("list", tenancyEntityList);
				}
				Iterator itr = query.list().iterator();
				
				while(itr.hasNext())
				{
					CustomAssetGroupEntity customAssetGroupType = (CustomAssetGroupEntity) itr.next();
					
					AssetCustomGroupDetailsBO customAssetGroupBo = new AssetCustomGroupDetailsBO();
					customAssetGroupBo.setAssetGroupId(customAssetGroupType.getGroup_id());
					customAssetGroupBo.setAssetGroupDescription(customAssetGroupType.getGroup_description());
					customAssetGroupBo.setAssetGroupName(customAssetGroupType.getGroup_name());
					customAssetGroupBo.setTenancyId(customAssetGroupType.getTenancy_id().getTenancy_id());
					
					customassetGroupBoList.add(customAssetGroupBo);
				}
				
				
			}
		
			//else get only the assetGroupTypes to which the user belongs to
			else
			{
				Query query;
				
				if(assetGroupTypeId!=0)
				{
					query = session.createQuery("select distinct a.asset_group_type from CustomAssetGroupEntity a," +
							" GroupUserMapping b where a.group_id = b.group_id and a.asset_group_type = "+assetGroupTypeId+" and b.contact_id='"+loginId+"'");
				}
				
				else
				{
					query = session.createQuery("select distinct a.asset_group_type from CustomAssetGroupEntity a," +
							" GroupUserMapping b where a.group_id = b.group_id and b.contact_id='"+loginId+"' and a.asset_group_type!='NULL'");
				}
				
				Iterator itr = query.list().iterator();
				
				while(itr.hasNext())
				{
					CustomAssetGroupEntity customAssetGroupType = (CustomAssetGroupEntity) itr.next();
					
					AssetCustomGroupDetailsBO customAssetGroupBo = new AssetCustomGroupDetailsBO();
					customAssetGroupBo.setAssetGroupId(customAssetGroupType.getGroup_id());
					customAssetGroupBo.setAssetGroupDescription(customAssetGroupType.getGroup_description());
					customAssetGroupBo.setAssetGroupName(customAssetGroupType.getGroup_name());
					customAssetGroupBo.setTenancyId(customAssetGroupType.getTenancy_id().getTenancy_id());
					
					customassetGroupBoList.add(customAssetGroupBo);
				}
					
			}
		}
		
		catch(CustomFault e)
        {
            //e.printStackTrace();
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
        }
        
		catch(Exception e)
		{
        //	e.printStackTrace();
			fLogger.fatal("Exception :"+e);
		}
		
        finally
        {
              if(session.getTransaction().isActive())
              {
                    session.getTransaction().commit();
              }
              
              if(session.isOpen())
              {
                    session.flush();
                    session.close();
              }
              
        }
        
		return customassetGroupBoList;
	}
	//************************************************END OF GET ASSET GROUP TYPE*******************************************************************
	
	
	
	//************************************************START OF SET ASSET GROUP TYPE*******************************************************************
	/** DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies 
	 * This method create/update custom asset group type
	 * @param tenancyId TenancyId under which custom asset group type has to be created
	 * @param assetGroupTypeId AssetGroupTypeId is specified if the details of existing assetGroupType has to be updated
	 * @param assetGroupTypeName Name of assetGroupType to be set to
	 * @param assetGroupTypeDescription Description of assetGroupType to be set to
	 * @param clientId clientId of CustomAssetGroupType to be created
	 * @return Returns the 
	 * @throws CustomFault
	 */
	public String setAssetGroupType(int inputTenancyId, int assetGroupTypeId, String assetGroupTypeName, String assetGroupTypeDescription, int clientId) throws CustomFault
	{
		String status = "SUCCESS";
		
		//Logger businessError = Logger.getLogger("businessErrorLogger");
        //Logger fatalError = Logger.getLogger("fatalErrorLogger");
        
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger=BusinessErrorLoggerClass.logger;
		
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
		try
		{
			//get Client Details
			Properties prop = new Properties();
			String clientName=null;
				
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");
      
			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
			//END of get Client Details
			if(! (session.isOpen() ))
                  {
                              session = HibernateUtil.getSessionFactory().getCurrentSession();
                              session.getTransaction().begin();
                  } 

			//get the client entity
			DomainServiceImpl domainService = new DomainServiceImpl();
			ClientEntity client = domainService.getClientDetails(clientId);
			if(client==null || client.getClient_id()==0)
			{
				throw new CustomFault("Invalid ClientID");
			}
		
			
			if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        session.getTransaction().begin();
            }
			
			
			//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
			//Get the List of all tenancyIds - Including Pseudo Tenancies
			//Get the List of all tenancyIds - Including Pseudo Tenancies
			int tenancyId=0;
			
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			/*Query q = session.createQuery(" from TenancyEntity t where t.tenancyCode = ( select a.tenancyCode from TenancyEntity a where a.tenancy_id='"+inputTenancyId+"') " +
											" order by t.tenancy_id ");*/
			
			Query q = session.createQuery(" from TenancyEntity t where t.mappingCode = ( select a.mappingCode from TenancyEntity a where a.tenancy_id='"+inputTenancyId+"') " +
					" order by t.tenancy_id ");
			
			Iterator it = q.list().iterator();
			if(it.hasNext())
			{
				TenancyEntity tenancyEnt = (TenancyEntity)it.next();
				tenancyId = tenancyEnt.getTenancy_id();
			}
			
			//validate tenancyId
			TenancyEntity tenancy = domainService.getTenancyDetails(tenancyId);
			if(tenancy==null || tenancy.getTenancy_id()==0)
			{
				throw new CustomFault("Invalid TenancyId");
			}
			

			if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        session.getTransaction().begin();
            }
			
		
			//check for the duplicate AssetGroupTypeName within the same tenancy before insertion/update
			boolean duplicate=false;
			Query q1 = session.createQuery("from CustomAssetGroupEntity where level=1 and client_id="+clientEntity.getClient_id()+" and active_status=1 and group_name='"+assetGroupTypeName+"' and tenancy_id="+tenancyId);
			CustomAssetGroupEntity duplicateAssetGroup = null;
			List list = q1.list();
			if( !(list==null || list.isEmpty()))
			{
				Iterator dupAssetGroupItr = list.iterator();
				while(dupAssetGroupItr.hasNext())
				{
					duplicateAssetGroup = (CustomAssetGroupEntity)dupAssetGroupItr.next();
				}
				
				if(duplicateAssetGroup!=null && duplicateAssetGroup.getGroup_id()!=assetGroupTypeId)
					duplicate=true;
			}
				
			if(duplicate==true)
			{
				throw new CustomFault("Machine Group Type with the same name already exists in the specified tenancy");
			}
			
			
			//New record Insertion
			if(assetGroupTypeId==0)
			{
				CustomAssetGroupEntity customAssetGroupType = new CustomAssetGroupEntity();
				customAssetGroupType.setGroup_name(assetGroupTypeName);
				customAssetGroupType.setGroup_description(assetGroupTypeDescription);
				customAssetGroupType.setLevel(1);
				customAssetGroupType.setTenancy_id(tenancy);
				customAssetGroupType.setActive_status(1);
				customAssetGroupType.setClient_id(client);
				
				session.save(customAssetGroupType);
			}
		
			//update asset group type
			else
			{
				//validate asset group type Id
				CustomAssetGroupEntity customAssetGroupType = getAssetGroupEntity(assetGroupTypeId);
				if(customAssetGroupType.getGroup_id()==0 || customAssetGroupType.getLevel()!=1 )
				{
					throw new CustomFault("Invalid AssetGroup Type");
				}
				
				if(! (session.isOpen() ))
				{
                    session = HibernateUtil.getSessionFactory().getCurrentSession();
                    session.getTransaction().begin();
				}
				
				customAssetGroupType.setGroup_name(assetGroupTypeName);
				customAssetGroupType.setGroup_description(assetGroupTypeDescription);
				session.update(customAssetGroupType);
			}
		}
		
		catch(CustomFault e)
	    {
	       	status = "FAILURE";
	       	bLogger.error("Custom Fault:"+e.getFaultInfo());
	    }
	    catch(Exception e)
		{
	       	status = "FAILURE";
	       	fLogger.fatal("Exception :"+e);
		}
	           
	    finally
	    {
	          if(session.getTransaction().isActive())
	          {
	              session.getTransaction().commit();
	          }
	                 
	          if(session.isOpen())
	          {
	               session.flush();
	               session.close();
	          }
	                  
	    }
	    
		return status;
	}
	
	//************************************************END OF SET ASSET GROUP TYPE*******************************************************************
	
//************************************************* START OF DELETE GROUP TYPE******************************************************************
	
	/** This method deletes the custom Asset group type if no custom asset groups are mapped under it
	 * @param assetGroupTypeId AssetGroupTypeId that has to be deleted
	 * @return returns the status String of AssetGroupType
	 * @throws CustomFault custom Exception is thrown if the specified assetGroupType is invalid OR if any assetGroups exists under the specified AssetGroupType
	 */
	public String deleteAssetGroupType(int assetGroupTypeId) throws CustomFault
	{
		String status ="SUCCESS";
		 
		//Logger businessError = Logger.getLogger("businessErrorLogger");
	    //Logger fatalError = Logger.getLogger("fatalErrorLogger");
	    
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger=BusinessErrorLoggerClass.logger;
		
	    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	    session.beginTransaction();
	    
	    try
	    {
	    	//get Client Details
			Properties prop = new Properties();
			String clientName=null;
				
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");
      
			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
			//END of get Client Details		
			if(! (session.isOpen() ))
                  {
                              session = HibernateUtil.getSessionFactory().getCurrentSession();
                              session.getTransaction().begin();
                  } 

	    	//validate the asset group Type id 
			CustomAssetGroupEntity customAssetGroup = getAssetGroupEntity(assetGroupTypeId);
			if(customAssetGroup.getGroup_id()==0)
			{
				throw new CustomFault("Invalid Asset Group Type");
			}
			
			if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().getCurrentSession();
                        session.getTransaction().begin();
            }
			
			//check if any assetGroups exists under the specified assetGroupType
			Query query = session.createQuery("from CustomAssetGroupEntity where active_status=1 and client_id="+clientEntity.getClient_id()+" and  asset_group_type="+assetGroupTypeId);
			List assetGroupList = query.list();
			
			if(assetGroupList.isEmpty())
			{
				customAssetGroup.setActive_status(0);
				session.update(customAssetGroup);
			}	
			else
			{
				throw new CustomFault("AssetGroup exists for this Asset Group Type and hence cannot be deleted");
			}
	    }
	    
	    catch(CustomFault e)
	    {
        	status = "FAILURE";
        	bLogger.error("Custom Fault:"+e.getFaultInfo());
        }
	    
	    catch(Exception e)
		{
        	status = "FAILURE";
        	fLogger.fatal("Exception :"+e);
		}
            
        finally
        {
            if(session.getTransaction().isActive())
            {
                session.getTransaction().commit();
            }
                
                if(session.isOpen())
                {
                    session.flush();
                    session.close();
                }
             
        }
		
		return status;
		
	}
	//************************************************* END OF DELETE GROUP TYPE******************************************************************
	
	
	
	
	//get the Asset_list for a given asset_group_id
	public List<AssetCustomGroupMapping> getSerial_numbers(List<Integer> asset_group_id)
	{
		
		// Logger businessError = Logger.getLogger("businessErrorLogger");
	    // Logger fatalError = Logger.getLogger("fatalErrorLogger");
	        
		Logger fLogger = FatalLoggerClass.logger;
		
	        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	        session.beginTransaction();
		List<AssetCustomGroupMapping> assetEntityList1 = new LinkedList<AssetCustomGroupMapping>();
		try{
		//Query q = session.createQuery("from AssetCustomGroupMapping where group_id in (:list) ").setParameterList("list", asset_group_id);
		
			//Query q = session.createQuery("from AssetCustomGroupMapping where group_id in (:list) ").setParameter(arg0, arg1)
			ListToStringConversion conversion = new ListToStringConversion();
        	String asset_group_idString = conversion.getIntegerListString(asset_group_id).toString();
			Query q = session.createQuery("from AssetCustomGroupMapping where group_id in ("+asset_group_idString+")");

		
		Iterator it = q.list().iterator();
		while(it.hasNext())
		{
			AssetCustomGroupMapping	 assetEntity = (AssetCustomGroupMapping) it.next();
			assetEntityList1.add(assetEntity);
				
		}}catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}
        
        finally
        {
              if(session.getTransaction().isActive())
              {
                    session.getTransaction().commit();
              }
              
              if(session.isOpen())
              {
                    session.flush();
                    session.close();
              }
              
        }
		return assetEntityList1;
	}
	
	//*********************************************** Start of getMachineGroupUsers***********************************************************************
	
	
	/** This method returns the users attached to a Asset Group  
     
     * @param groupId : Asset Group id
    
     * @return List of users belonging to a Asset Group.
     * @throws CustomFault custom Exception is thrown .
     */
	public List<ContactEntity> getMachineGroupUsers(int groupId) throws CustomFault
	{
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		
		Logger fLogger = FatalLoggerClass.logger;
		
		
	Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
	List<ContactEntity> machineGroupUsers = new LinkedList<ContactEntity>();

		try{
			

		 
				
		
		
		
		Query query = session.createQuery("From GroupUserMapping where group_id = " +groupId);
		Iterator it = query.list().iterator();
		
		while(it.hasNext())
		{
			GroupUserMapping groupUser = (GroupUserMapping)it.next();
			machineGroupUsers.add(groupUser.getContact_id());
		}
		
		
	
	}
	catch(Exception e){
		 fLogger.fatal("Exception :"+e);
	}
	 finally
     {
           if(session.getTransaction().isActive())
           {
                 session.getTransaction().commit();
           }
           
           if(session.isOpen())
           {
                 session.flush();
                 session.close();
           }
           
     }
		return machineGroupUsers;	
}
}
