package com.wipro.mcoreapp.implementation;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import com.wipro.mcoreapp.businessobject.AssetDetailsEntity;

import remote.wise.businessentity.AssetEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

public class AssetRollOffMigration 
{
	public String migrateAssetDetails(String assetId)
	{
		String status="SUCCESS";
		
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	long startTime = System.currentTimeMillis();
    	
    	List<AssetDetailsEntity> assetEntityList = new LinkedList<AssetDetailsEntity>();
    	
    	//-------------------------------- STEP1: Get the List of all active Assets from MySQL
    	iLogger.info("MCoreApp:AssetMigration:migrateAssetData:VIN:"+assetId+":" +"Get the List of active VINs from MySQL - Start");
    	try
		{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			try
			{
				String assetQString = " select b.serialNumber, a.nick_name, b.simNo, b.imeiNo, a.machineNumber, a.dateTime " +
						" from AssetEntity a, AssetControlUnitEntity b where a.serial_number=b.serialNumber and a.active_status=1";
				if(assetId!=null)
				{
					assetQString = assetQString+" and a.serial_number='"+assetId+"'";
				}
				Query assetQ = session.createQuery(assetQString);
				Iterator assetItr = assetQ.list().iterator();
				Object[] result=null;
				while(assetItr.hasNext())
				{
					result = (Object[])assetItr.next();
					
					AssetDetailsEntity assetDetails = new AssetDetailsEntity();
					assetDetails.setAssetId(result[0].toString());
					
					if(result[1]!=null)
						assetDetails.setEngineNum(result[1].toString());
					
					if(result[2]!=null)
						assetDetails.setIccidNum(result[2].toString());
					
					if(result[3]!=null)
						assetDetails.setImei(result[3].toString());
					
					if(result[4]!=null)
						assetDetails.setMachineNum(result[4].toString());
					
					if(result[5]!=null)
					{
						String rollOffDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Timestamp)result[5]);
						assetDetails.setRollOffDate(rollOffDate);
					}
					
					assetEntityList.add(assetDetails);
				}
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("MCoreApp:"+"AssetMigration:migrateAssetData:VIN:"+assetId+":" +
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
					fLogger.fatal("MCoreApp:"+"AssetMigration:migrateAssetData:VIN:"+assetId+":" +
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
    		fLogger.fatal("MCoreApp:"+"AssetMigration:migrateAssetData:VIN:"+assetId+":" +
					" Exception in getting Hibernate Session:"+e.getMessage());
			return "FAILURE";
			
    	}
    	long endTime = System.currentTimeMillis();
    	iLogger.info("MCoreApp:AssetMigration:migrateAssetData:VIN:"+assetId+":" +"Get the List of active VINs from MySQL - END : Total Time in ms:"+(endTime-startTime));
    	
    	//-------------------------------- STEP2: Spawn 100 threads in parallel Using Executor service- One thread for updating the MAsset for each VIN
    	startTime = System.currentTimeMillis();
    	iLogger.info("MCoreApp:AssetMigration:migrateAssetData:VIN:"+assetId+": Insert MAsset to OrientAppDB - START");
		
    	ExecutorService executor = Executors.newFixedThreadPool(100);
		
		for(int i=0; i<assetEntityList.size(); i++)
		{
			//-------------Once all the 100 threads are spawned, wait for all 100 to finish before spawning another set of 100 threads	
			if(i!=0 && i%100==0)
			{
				executor.shutdown();  
//				iLogger.debug("MCoreApp:AssetMigration:migrateAssetData:VIN:"+assetId+":" +"Before spawning thread "+i+", wait for current Executor Threads to finish");
				while (!executor.isTerminated()) {   }  
			
				executor = Executors.newFixedThreadPool(100);
			}
				
//			iLogger.debug("MCoreApp:AssetMigration:migrateAssetData:VIN:"+assetId+": Spawning thread:"+i);
			
			AssetDetailsBO boObj = new AssetDetailsBO();
			boObj.assetEntity=assetEntityList.get(i);
				
			Callable<String> worker = boObj;
			Future<String> submit =executor.submit(worker);
		}
			
		executor.shutdown();  
		while (!executor.isTerminated()) {   }  	
			
		endTime = System.currentTimeMillis();
		iLogger.info("MCoreApp:AssetMigration:migrateAssetData:VIN:"+assetId+": Insert MAsset to OrientAppDB - END : Total Assets:"+assetEntityList.size()+": Total Time in ms:"+(endTime-startTime));
		
		return status;
	}
}
