package com.wipro.mcoreapp.businessobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;


public class AssetPersonalityDetailsBO implements Callable
{
	public AssetProfileEntity assetProfile;
	
	@Override
	public Object call()
	{
		String status ="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		
		try
		{
			//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
			//status = AssetPersonality(this.assetProfile);
		}
		
		catch(Exception e)
		{
			status="FAILURE";
			e.printStackTrace();
			fLogger.fatal("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+this.assetProfile.assetId+":Exception:"+e.getMessage());
		}
		
		if(status.equalsIgnoreCase("FAILURE"))
		{
			fLogger.fatal("MCoreApp:AssetMigration:migrateAssetPersData:MIGRATION FAILURE:VIN:"+this.assetProfile.assetId);
		}
		
		return status;
	}
	
	//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
	/*public String AssetPersonality(AssetProfileEntity profileEntity)
	{
		String setPersonalityStatus = "SUCCESS";
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		OrientAppDbDatasource connObj = new OrientAppDbDatasource();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try
		{
			//Check for Mandatory Parameters
			if(profileEntity.assetId==null )
			{
				fLogger.fatal("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+profileEntity.assetId+": Mandatory parameter AssetID is null");
				return "FAILURE";
			}
			
			else if(profileEntity.profileCode==null)
			{
				fLogger.fatal("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+profileEntity.assetId+": Mandatory parameter profileCode is null");
				return "FAILURE";
			}
			else if(profileEntity.modelCode ==null)
			{
				fLogger.fatal("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+profileEntity.assetId+": Mandatory parameter modelCode is null");
				return "FAILURE";
			}
			
			else
			{
				iLogger.info("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+profileEntity.assetId+"; profileCode:"+profileEntity.profileCode+"; " +
						" modelCode:"+profileEntity.modelCode+"; engineCode:"+profileEntity.engineCode);
			
				conn = connObj.getConnection();
				stmt = conn.createStatement();
				
				//---------------------------  STEP1: Check whether the association already exists from Asset to Profile
				rs = stmt.executeQuery(" select Code from MAssetProfile where out('PROFILE_OF').AssetID IN ('"+profileEntity.assetId+"') and Code='"+profileEntity.profileCode+"'");
				int profileRelationship = 0;
				while(rs.next())
				{
					iLogger.debug("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+profileEntity.assetId+":Profile RelationShip present from :"+profileEntity.assetId+":"+profileEntity.profileCode);
					profileRelationship =1;
				}
				
				
				String assetRID=null;
				//-------------------------------------- STEP2: Check for the existence of VIN Node
				rs = stmt.executeQuery(" select from MAsset where AssetID='"+profileEntity.assetId+"'");
				while(rs.next())
				{
					ORecordId rec = (ORecordId)rs.getObject("@RID");
					assetRID = rec.getIdentity().toString();
				
				}
						
				if(assetRID==null)
				{
					fLogger.fatal("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+profileEntity.assetId+":MAsset Node not defined for the Asset");
					return "FAILURE";
				}
				
				//------------------------------- STEP3: Associate Asset to the received profile data
				if(profileRelationship==0)
				{
					
					//--------------------------------------- STEP3.1: Check for the existence of Profile Node -- Not required for Migration Code
					String profileRID = null;
					rs = stmt.executeQuery(" select from MAssetProfile where Code='"+profileEntity.profileCode+"'");
					while(rs.next())
					{
						ORecordId rec = (ORecordId)rs.getObject("@RID");
						profileRID =  rec.getIdentity().toString();
					}
										
					if(profileRID==null)
					{
						fLogger.fatal("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+profileEntity.assetId+": MAssetProfile not defined for "+profileEntity.profileCode);
						return "FAILURE";
					}
					
					
					//----------------------------------------- STEP3.2: Create a Bidirectional Link from Asset to the given Profile
					stmt.execute("Create Edge BELONGS_TO from "+assetRID+" to "+profileRID);
					stmt.execute("Create Edge PROFILE_OF from "+profileRID+" to "+assetRID);
						
				}
				
				
				
				//--------------------------- STEP4: Check whether the association already exists from Asset to Model
				rs = stmt.executeQuery(" select Code from MAssetModel where out('MODEL_OF').AssetID IN ('"+profileEntity.assetId+"') and Code='"+profileEntity.modelCode+"'");
				int modelRelationship = 0;
				while(rs.next())
				{
					iLogger.debug("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+profileEntity.assetId+":Model RelationShip present:"+profileEntity.assetId+":"+profileEntity.modelCode);
					modelRelationship =1;
				}
				
				//------------------------------- STEP5: Associate Asset to the received Model data
				if(modelRelationship==0)
				{
					
					//--------------------------------------- STEP5.1: Check for the existence of Model Node
					String modelRID = null;
					rs = stmt.executeQuery(" select from MAssetModel where Code='"+profileEntity.modelCode+"'");
					while(rs.next())
					{
						ORecordId rec = (ORecordId)rs.getObject("@RID");
						modelRID = rec.getIdentity().toString();
					}
					
					if(modelRID==null)
					{
						fLogger.fatal("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+profileEntity.assetId+": MAsset MAssetModel not defined for "+profileEntity.modelCode);
						return "FAILURE";
					}
					
					
					//----------------------------------------- STEP5.2: Create a Bidirectional Link from Asset to the given Model
					stmt.execute("Create Edge BELONGS_TO from "+assetRID+" to "+modelRID);
					stmt.execute("Create Edge MODEL_OF from "+modelRID+" to "+assetRID);
					
				}
				
				
				
			}
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+profileEntity.assetId+":Exception:"+e.getMessage());
			setPersonalityStatus = "FAILURE";
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
				e.printStackTrace();
				fLogger.fatal("MCoreApp:AssetMigration:migrateAssetPersData:VIN:"+profileEntity.assetId+":Exception in closing OrientDB Connection:"+e.getMessage());
			}
			
		}
		
		return setPersonalityStatus;
	
	
	}*/
}
