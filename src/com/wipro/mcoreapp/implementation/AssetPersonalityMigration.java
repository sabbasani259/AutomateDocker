package com.wipro.mcoreapp.implementation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.wipro.mcoreapp.businessobject.AssetDetailsBO;
import com.wipro.mcoreapp.businessobject.AssetPersonalityDetailsBO;
import com.wipro.mcoreapp.businessobject.AssetProfileEntity;

import remote.wise.businessentity.AssetEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

public class AssetPersonalityMigration 
{
	public String migrateAssetPersDetails(String assetId)
	{
		String status="SUCCESS";
		
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	long startTime = System.currentTimeMillis();
    	
    	List<AssetProfileEntity> assetProfileList = new LinkedList<AssetProfileEntity>();
    	
    	//-------------------------------- STEP1: Get the List of all active Assets from MySQL
    	iLogger.info("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+assetId+":" +"Get the List of active VINs from MySQL - Start");
    	
    	try
		{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			try
			{
				String assetProfileQString = " select a , c.asset_grp_code, d.assetTypeCode " +
						" from AssetEntity a, ProductEntity b, AssetGroupProfileEntity c, AssetTypeEntity d  " +
						" where a.active_status=1 and a.productId=b.productId and b.assetGroupId= c.asset_grp_id and b.assetTypeId=d.asset_type_id ";
				if(assetId!=null)
				{
					assetProfileQString = assetProfileQString + " and a.serial_number='"+assetId+"'";
				}
				
				Query assetProfileQ = session.createQuery(assetProfileQString);
				Iterator assetProfileItr = assetProfileQ.list().iterator();
				Object[] result = null;
				
				while(assetProfileItr.hasNext())
				{
					result = (Object[])assetProfileItr.next();
					AssetEntity asset = (AssetEntity)result[0];
					
					AssetProfileEntity profileEntity = new AssetProfileEntity();
					profileEntity.setAssetId(asset.getSerial_number().getSerialNumber());
					profileEntity.setEngineCode("JCBEngine");
					profileEntity.setModelCode(result[2].toString());
					profileEntity.setProfileCode(result[1].toString());
					
					assetProfileList.add(profileEntity);
				}
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+assetId+":" +
						"Exception in getting details from MySQL :"+e.getMessage());
			}
			
			finally
			{
				try
				{
					if(session!=null && session.isOpen())  
						if(session.getTransaction().isActive())
						{
							session.getTransaction().commit();
						}
				}
				catch(Exception e)
				{
					fLogger.fatal("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+assetId+":" +
							":Exception in comitting the hibernate session:"+e);
				}
		
				if(session.isOpen())
				{
					if(session.getTransaction().isActive())
						session.flush();
						session.close();
				}
			}
			
		}
    	
    	catch(Exception e)
    	{
    		fLogger.fatal("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+assetId+":" +
					" Exception in getting Hibernate Session:"+e.getMessage());
			return "FAILURE";
			
    	}
    	long endTime = System.currentTimeMillis();
    	iLogger.info("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+assetId+":" +"Get the List of active VINs from MySQL - END : Total Time in ms :"+(endTime-startTime));
    	
    	
    	//-------------------------------- STEP2: Spawn 100 threads in parallel Using Executor service- One thread for updating the Edges for MAsset for each VIN
    	startTime = System.currentTimeMillis();
    	iLogger.info("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+assetId+": Insert the Edge from and to MAsset in OrientAppDB - START");
		
    	ExecutorService executor = Executors.newFixedThreadPool(100);
		
		for(int i=0; i<assetProfileList.size(); i++)
		{
			//-------------Once all the 100 threads are spawned, wait for all 100 to finish before spawning another set of 100 threads	
			if(i!=0 && i%100==0)
			{
				executor.shutdown();  
//				iLogger.debug("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+assetId+":" +"Before spawning thread "+i+", wait for current Executor Threads to finish");
				while (!executor.isTerminated()) {   }  
			
				executor = Executors.newFixedThreadPool(100);
			}
				
//			iLogger.debug("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+assetId+": Spawning thread:"+i);
			
			AssetPersonalityDetailsBO boObj = new AssetPersonalityDetailsBO();
			boObj.assetProfile=assetProfileList.get(i);
				
			Callable<String> worker = boObj;
			Future<String> submit =executor.submit(worker);
		}
			
		executor.shutdown();  
		while (!executor.isTerminated()) {   }  	
			
		endTime = System.currentTimeMillis();
		iLogger.info("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+assetId+": Insertthe Edge from and to MAsset in OrientAppDB  - END : Total Assets:"+assetProfileList.size()+": Total Time in ms:"+(endTime-startTime));
		
		
		return status;
	}
}
