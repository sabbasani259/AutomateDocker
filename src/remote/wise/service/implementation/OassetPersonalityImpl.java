/**
 * 
 */
package remote.wise.service.implementation;

import java.util.Iterator;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetGroupEntity;
import remote.wise.businessentity.AssetGroupProfileEntity;
import remote.wise.businessentity.AssetTypeEntity;
import remote.wise.businessobject.OassetPersonalityBO;
import remote.wise.util.HibernateUtil;

/**
 * @author roopn5
 *
 */
public class OassetPersonalityImpl {
	
	public String setOAssetPersonalityDetails(String OSerialNumber, String OAssetGroupCode, String OassetTypeCode){
		
		String profileName=null;
		String modelName=null;
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		try{
			
		if(OAssetGroupCode!=null){
			Query profileQuery = session.createQuery("from AssetGroupProfileEntity where asset_grp_code='"+OAssetGroupCode+"'");

			Iterator profItr = profileQuery.list().iterator();

			while(profItr.hasNext())
			{
				AssetGroupProfileEntity prof = (AssetGroupProfileEntity)profItr.next();
				
				AssetGroupEntity ag=(AssetGroupEntity)prof.getAsset_grp_id();
				profileName=ag.getAsset_group_name();
				
			}
		}
		
		if(OassetTypeCode!=null){
			
			Query modelQuery = session.createQuery("from AssetTypeEntity where assetTypeCode='"+OassetTypeCode+"'");

			Iterator modItr = modelQuery.list().iterator();

			while(modItr.hasNext())
			{
				AssetTypeEntity mod = (AssetTypeEntity)modItr.next();
				modelName=mod.getAsset_type_name();
			}
		}
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		finally{

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
		
		OassetPersonalityBO assetPersonalityObj= new OassetPersonalityBO();
		
		//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
		String response="SUCCESS";
		//String response=assetPersonalityObj.setOrientAssetPersonalityDetails(OSerialNumber, OAssetGroupCode, OassetTypeCode, profileName, modelName);	
		
		return response;
	}
	
	

}
