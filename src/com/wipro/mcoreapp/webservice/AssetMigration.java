package com.wipro.mcoreapp.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import com.wipro.mcoreapp.implementation.AssetPersonalityMigration;
import com.wipro.mcoreapp.implementation.AssetRollOffMigration;

import remote.wise.log.InfoLogging.InfoLoggerClass;

@Path("/AssetMigration")
public class AssetMigration 
{
	@GET
	@Path("migrateAssetData")
	@Produces("text/plain")
	public String migrateAssetData(@QueryParam("VIN") String VIN)
	{
		String status="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		
		iLogger.info("MCoreApp:AssetMigration:migrateAssetData:WebService Input-----> VIN:"+VIN);
		long startTime = System.currentTimeMillis();
		
		//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
		//status = new AssetRollOffMigration().migrateAssetDetails(VIN);
		
		long endTime=System.currentTimeMillis();
		iLogger.info("MCoreApp:AssetMigration:migrateAssetData:WebService Output -----> status:"+status+"; Total Time taken in ms:"+(endTime - startTime));
		
		
		return status;
	}
	
	
	@GET
	@Path("migrateAssetPersData")
	@Produces("text/plain")
	public String migrateAssetPersData(@QueryParam("VIN") String VIN)
	{
		String status="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		
		iLogger.info("MCoreApp:AssetMigration:migrateAssetPersData:WebService Input-----> VIN:"+VIN);
		long startTime = System.currentTimeMillis();
		
		//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
		//status = new AssetPersonalityMigration().migrateAssetPersDetails(VIN);
					
		long endTime=System.currentTimeMillis();
		iLogger.info("MCoreApp:AssetMigration:migrateAssetPersData:WebService Output -----> status:"+status+"; Total Time taken in ms:"+(endTime - startTime));
		
		
		return status;
	}
}
