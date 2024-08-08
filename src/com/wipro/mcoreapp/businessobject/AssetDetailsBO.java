package com.wipro.mcoreapp.businessobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

//import com.wipro.mcoreapp.util.OrientAppDbDatasource;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class AssetDetailsBO implements Callable
{
	public AssetDetailsEntity assetEntity;
	
	@Override
	public Object call()
	{
		String status ="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		
		try
		{
			//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
			//status = setMAsset(this.assetEntity);
		}
		
		catch(Exception e)
		{
			status="FAILURE";
			e.printStackTrace();
			fLogger.fatal("MCoreApp:AssetMigration:migrateAssetData:VIN:"+this.assetEntity.assetId+":Exception:"+e.getMessage());
		}
		
		if(status.equalsIgnoreCase("FAILURE"))
		{
			fLogger.fatal("MCoreApp:AssetMigration:migrateAssetData:MIGRATION FAILURE:VIN:"+this.assetEntity.assetId);
		}
		
		return status;
	}
	
	//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
	/*public String setMAsset(AssetDetailsEntity assetDetailsEntity)
	{
		String setRollOffStatus = "SUCCESS";
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		OrientAppDbDatasource connObj = new OrientAppDbDatasource();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try
		{
				iLogger.info("MCoreApp:AssetMigration:migrateAssetData:VIN:"+assetDetailsEntity.assetId+"; machineNumber:"+assetDetailsEntity.machineNum+"" +
						"; engineNumber:"+assetDetailsEntity.engineNum+
						"; imeiNumber:"+assetDetailsEntity.imei+"; iccidNumber:"+assetDetailsEntity.iccidNum+"; rollOffDate:"+assetDetailsEntity.rollOffDate);
				
				//----------------------------------------- STEP1: Create a Vertex for the Asset
	
				conn = connObj.getConnection();
				stmt = conn.createStatement();
				
				rs = stmt.executeQuery(" select AssetID from MAsset where AssetID='"+assetDetailsEntity.assetId+"'");
				int update=0;
				while(rs.next())
				{
					update=1;
					
				}
				
				if(update==1)
				{
					iLogger.info("MCoreApp:AssetMigration:migrateAssetData:VIN:"+assetDetailsEntity.assetId+": Asset already exists in MAsset");
				}
				
				else if(update==0)
				{
					String fieldTypeDecl = "RollOffDate=t";
					
					//Insert the new Asset Node
					HashMap asset = new HashMap();
					asset.put("AssetID", assetDetailsEntity.assetId);
					asset.put("MachineNum", assetDetailsEntity.machineNum);
					asset.put("EngineNum", assetDetailsEntity.engineNum);
					asset.put("IMEI", assetDetailsEntity.imei);
					asset.put("ICCID", assetDetailsEntity.iccidNum);
					asset.put("RollOffDate", assetDetailsEntity.rollOffDate);
					asset.put("@fieldTypes", fieldTypeDecl);
					
					String insertQ = new JSONObject(asset).toString().replaceAll("\\\\","");
					insertQ = "CREATE VERTEX MAsset content "+insertQ;
					stmt.execute(insertQ);
					
					iLogger.info("MCoreApp:AssetMigration:migrateAssetData:VIN:"+assetDetailsEntity.assetId+": New Vertex for Asset created successfully");
				}
				
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			setRollOffStatus="FAILURE";
			fLogger.fatal("MCoreApp:AssetMigration:migrateAssetData:VIN:"+assetDetailsEntity.assetId+":Exception:"+e.getMessage());
			
		}
		
		finally
		{
			try
			{
				if(rs!=null)
					rs.close();
			
				if(stmt!=null)
					stmt.close();
				
				if(conn!=null)
					conn.close();
			}
			catch(Exception e)
			{
				fLogger.fatal("MCoreApp:AssetMigration:migrateAssetData:VIN:"+assetDetailsEntity.assetId+":Exception in closing OrientDB Connection:"+e.getMessage());
			}
			
		}
				
		return setRollOffStatus;
				
	
	
	}*/
}
