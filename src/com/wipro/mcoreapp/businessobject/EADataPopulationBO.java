package com.wipro.mcoreapp.businessobject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.simple.JSONObject;

import remote.wise.EAintegration.handler.MoolErrorMsgHandler;
import remote.wise.businessentity.AssetServiceScheduleEntity;
import remote.wise.businessentity.ServiceScheduleEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;



public class EADataPopulationBO 
{
	//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
	/*public String  AssetRollOff(String messageId, String fileName, String messageString, int segId)
	{

		String setRollOffStatus = "SUCCESS";
		String failureCause ="";
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		//Get the required details using the string split
		String[] rollOffData =  messageString.split("\\|",-1);
		
		OrientAppDbDatasource connObj = new OrientAppDbDatasource();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try
		{
			if(rollOffData.length < 6)
			{
				fLogger.fatal("MOOL:EADataPopulation:AssetDetails:MAsset:Populate RollOffData to MOOL AppDB: messageString:"+messageString+":Invalid DataSet");
				setRollOffStatus = "FAILURE";
			}
			
			else
			{
			
				iLogger.info("MOOL:EADataPopulation:AssetDetails:MAsset:Populate RollOffData to MOOL AppDB: assetId:"+rollOffData[0]+"; machineNumber:"+rollOffData[1]+"; engineNumber:"+rollOffData[2]+
						"; imeiNumber:"+rollOffData[3]+"; iccidNumber:"+rollOffData[4]+"; rollOffDate:"+rollOffData[5]);
				
				//----------------------------------------- STEP1: Create a Vertex for the Asset
	
				conn = connObj.getConnection();
				stmt = conn.createStatement();
				
				rs = stmt.executeQuery(" select AssetID from MAsset where AssetID='"+rollOffData[0]+"'");
				int update=0;
				while(rs.next())
				{
					update=1;
					
				}
				
				if(update==1)
				{
					//NOTE : No Update of RolledOff Date, since the record may be reprocessed but the rolledoff date should not change
					stmt.executeUpdate("Update MAsset set MachineNum='"+rollOffData[1]+"', EngineNum='"+rollOffData[2]+"', IMEI='"+rollOffData[3]+"', " +
							"ICCID='"+rollOffData[4]+"' where AssetID='"+rollOffData[0]+"'");
					iLogger.info("MOOL:EADataPopulation:AssetDetails:MAsset:setAssetDetails for :"+rollOffData[0]+": Data updated Successfully");
				}
				
				if(update==0)
				{
					String fieldTypeDecl = "RollOffDate=t";
					
					//Insert the new Asset Node
					HashMap asset = new HashMap();
					asset.put("AssetID", rollOffData[0]);
					asset.put("MachineNum", rollOffData[1]);
					asset.put("EngineNum", rollOffData[2]);
					asset.put("IMEI", rollOffData[3]);
					asset.put("ICCID", rollOffData[4]);
					asset.put("RollOffDate", rollOffData[5]);
					asset.put("@fieldTypes", fieldTypeDecl);
					
					String insertQ = new JSONObject(asset).toString();
					insertQ = "CREATE VERTEX MAsset content "+insertQ;
					stmt.execute(insertQ);
					
					iLogger.info("MOOL:EADataPopulation:AssetDetails:MAsset:setAssetDetails for :"+rollOffData[0]+": New Vertex for Asset created successfully");
				}
				
						
			}
		
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			setRollOffStatus="FAILURE";
			fLogger.fatal("MOOL:EADataPopulation:AssetDetails:MAsset:Populate RollOffData to MOOL: assetId:"+rollOffData[0]+":Exception:"+e.getMessage());
			failureCause = e.getMessage();
		}
		
		finally
		{
			iLogger.debug("MOOL:EADataPopulation:AssetDetails:MAsset:setAssetDetails for :"+rollOffData[0]+":OrientDBPersistence:Close Connection - START");
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
				fLogger.fatal("MOOL:EADataPopulation:AssetDetails:MAsset:setAssetDetails for :"+rollOffData[0]+":Exception in closing OrientDB Connection:"+e.getMessage());
			}
			iLogger.debug("MOOL:EADataPopulation:AssetDetails:MAsset:setAssetDetails for :"+rollOffData[0]+":OrientDBPersistence:Close Connection - END");
			
		}
		
		//----------------------------- If any exception is thrown / Failure in persisting the data in MOOL, store the details in mool_fault_details table
		//for later reprocessing of the same
		if(setRollOffStatus.equalsIgnoreCase("FAILURE"))
		{
			String setErrStatus = new MoolErrorMsgHandlerBO().handleErrorMessages(messageId, fileName, "AssetRollOff", messageString, failureCause) ;
					
			//If the error records cannot be persisted to Mysql, place the same into file with MOOLSEP as delimiter
			if(setErrStatus.equalsIgnoreCase("FAILURE"))
				new MoolErrorMsgHandlerBO().fileHandler(messageId, fileName, "AssetRollOff", messageString, failureCause);
		}
		
		
		//------------------------------ Create a Cluster if required, and populate the details into MAssetCluster
		try
		{
			//new AssetClusterDefinition().setClusterDefn(rollOffData[0], segId);
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("MOOL:EADataPopulation:AssetDetails:MAsset:setAssetDetails for :"+rollOffData[0]+": Exception in the invocation of MOOL URL for Cluster Definition");
		}
				
		return setRollOffStatus;
				
	
	}*/

	
	//*****************************************************************************************************************************************
	//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
	/*public String AssetPersonality(String messageId, String fileName, String messageString)
	{
		String setPersonalityStatus = "SUCCESS";
		String failureCause ="";
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		//Get the required details using the string split
		String[] persData =  messageString.split("\\|",-1);
		
		OrientAppDbDatasource connObj = new OrientAppDbDatasource();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try
		{
			if(persData.length < 4)
			{
				fLogger.fatal("MOOL:EADataPopulation:AssetPersonalityImpl: messageString:"+messageString+":Invalid DataSet");
				return "FAILURE";
			}
			
			
			//Check for Mandatory Parameters
			else if(persData[0]==null || persData[0].trim()==null )
			{
				fLogger.fatal("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+": Mandatory parameter AssetID is null");
				return "FAILURE";
			}
			
			else if(persData[1]==null || persData[1].trim()==null)
			{
				fLogger.fatal("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+": Mandatory parameter profileCode is null");
				return "FAILURE";
			}
			else if(persData[2]==null || persData[2].trim() ==null)
			{
				fLogger.fatal("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+": Mandatory parameter modelCode is null");
				return "FAILURE";
			}
			
			else
			{
				iLogger.info("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+"; profileCode:"+persData[1]+"; modelCode:"+persData[2]+
						"; engineCode:"+persData[3]);
			
				conn = connObj.getConnection();
				stmt = conn.createStatement();
				
				//---------------------------  STEP1: Check whether the association already exists from Asset to Profile
				rs = stmt.executeQuery(" select Code from MAssetProfile where out('PROFILE_OF').AssetID IN ('"+persData[0]+"') and Code='"+persData[1]+"'");
				int profileRelationship = 0;
				while(rs.next())
				{
					iLogger.debug("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+":Profile RelationShip present from :"+persData[0]+":"+persData[1]);
					profileRelationship =1;
				}
				
				
				String assetRID=null;
				//-------------------------------------- STEP2: Check for the existence of VIN Node
				rs = stmt.executeQuery(" select from MAsset where AssetID='"+persData[0]+"'");
				while(rs.next())
				{
					ORecordId rec = (ORecordId)rs.getObject("@RID");
					assetRID = rec.getIdentity().toString();
				
				}
						
				if(assetRID==null)
				{
					fLogger.fatal("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+":MAsset Node not defined for the Asset");
					return "FAILURE";
				}
				
				//------------------------------- STEP3: Associate Asset to the received profile data
				if(profileRelationship==0)
				{
					
					iLogger.info("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+":Create Relationship:"+persData[0]+":"+persData[1]);
					//--------------------------------------- STEP3.1: Check for the existence of Profile Node
					String profileRID = null;
					rs = stmt.executeQuery(" select from MAssetProfile where Code='"+persData[1]+"'");
					while(rs.next())
					{
						ORecordId rec = (ORecordId)rs.getObject("@RID");
						profileRID =  rec.getIdentity().toString();
					}
					iLogger.debug("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+":profileRID:"+profileRID);
					
					if(profileRID==null)
					{
						fLogger.fatal("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+": MAssetProfile not defined for "+persData[1]);
						return "FAILURE";
					}
					
					
					//----------------------------------------- STEP3.2: Create a Bidirectional Link from Asset to the given Profile
					stmt.execute("Create Edge BELONGS_TO from "+assetRID+" to "+profileRID);
					iLogger.debug("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+":Create Edge BELONGS_TO from "+assetRID+" to "+profileRID+": Done");
					stmt.execute("Create Edge PROFILE_OF from "+profileRID+" to "+assetRID);
					iLogger.debug("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+":Create Edge PROFILE_OF from "+profileRID+" to "+assetRID+": Done");
						
				}
				
				
				
				//--------------------------- STEP4: Check whether the association already exists from Asset to Model
				rs = stmt.executeQuery(" select Code from MAssetModel where out('MODEL_OF').AssetID IN ('"+persData[0]+"') and Code='"+persData[2]+"'");
				int modelRelationship = 0;
				while(rs.next())
				{
					iLogger.debug("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+":Model RelationShip present:"+persData[0]+":"+persData[2]);
					modelRelationship =1;
				}
				
				//------------------------------- STEP5: Associate Asset to the received Model data
				if(modelRelationship==0)
				{
					
					iLogger.info("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+":Create Relationship:"+persData[0]+":"+persData[2]);
					//--------------------------------------- STEP5.1: Check for the existence of Model Node
					String modelRID = null;
					rs = stmt.executeQuery(" select from MAssetModel where Code='"+persData[2]+"'");
					while(rs.next())
					{
						ORecordId rec = (ORecordId)rs.getObject("@RID");
						modelRID = rec.getIdentity().toString();
					}
					iLogger.debug("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+":modelRID:"+modelRID);
					
					if(modelRID==null)
					{
						fLogger.fatal("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+": MAsset MAssetModel not defined for "+persData[2]);
						return "FAILURE";
					}
					
					
					//----------------------------------------- STEP5.2: Create a Bidirectional Link from Asset to the given Model
					stmt.execute("Create Edge BELONGS_TO from "+assetRID+" to "+modelRID);
					iLogger.debug("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+":Create Edge BELONGS_TO from "+assetRID+" to "+modelRID+": Done");
					stmt.execute("Create Edge MODEL_OF from "+modelRID+" to "+assetRID);
					iLogger.debug("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+":Create Edge MODEL_OF from "+modelRID+" to "+assetRID+": Done");
				}
				
				
				//----------------------------  STEP6: Associate Asset to the received EngineType data
				if(persData[3]!=null)
				{
					//ToDo: Since EngineType is not defined now, this part will be filled later 
				}
				
			}
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+":Exception:"+e.getMessage());
			setPersonalityStatus = "FAILURE";
		}
		
		finally
		{
			iLogger.debug("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+":OrientDBPersistence:Close Connection - START");
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
				fLogger.fatal("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+":Exception in closing OrientDB Connection:"+e.getMessage());
			}
			iLogger.debug("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+":OrientDBPersistence:Close Connection - END");
			
			
			//----------------------------- If any exception is thrown / Failure in persisting the data in MOOL, store the details in mool_fault_details table
			//for later reprocessing of the same
			iLogger.debug("MOOL:EADataPopulation:AssetPersonalityImpl:AssetID:"+persData[0]+":STATUS:"+setPersonalityStatus);
			
			if(setPersonalityStatus.equalsIgnoreCase("FAILURE"))
			{
				String setErrStatus = new MoolErrorMsgHandlerBO().handleErrorMessages(messageId, fileName, "AssetPersonality", messageString, failureCause) ;
				
				//If the error records cannot be persisted to Mysql, place the same into file with MOOLSEP as delimiter
				if(setErrStatus.equalsIgnoreCase("FAILURE"))
					new MoolErrorMsgHandlerBO().fileHandler(messageId, fileName, "AssetPersonality", messageString, failureCause);
			}
			
		}
		
		return setPersonalityStatus;
	
	}*/
	
	
	//********************************************************************************************************************************************
	//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
	/*public String ServiceCompletion(String messageId, String fileName, String messageString)
	{
		String status="SUCCESS";
		String failureCause=null;
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		//Get the required details using the string split
		String[] serviceDetails =  messageString.split("\\|",-1);
		String serialNumber= null;
		List<Integer> servSchIdList = new LinkedList<Integer>();
		
		if(serviceDetails.length < 5 || serviceDetails[0]==null || serviceDetails[3]==null)
		{
			fLogger.fatal("MOOL:EADataPopulation:ServiceCompletion:Update Alerts for Service Completion in MOOL AppDB: messageString:"+messageString+":Invalid DataSet");
			status = "FAILURE";
			return status;
		}
		
		else
		{
			serialNumber=serviceDetails[0];
			String dbmsPartCode = serviceDetails[3];
			
			//----------------------- STEP1: Get the List ServiceScheduleID of the received service completion record
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			
			try
			{
				Query serviceRecQ = session.createQuery("select b from AssetServiceScheduleEntity a, ServiceScheduleEntity b " +
						" where a.serviceScheduleId=b.serviceScheduleId and a.serialNumber='"+serialNumber+"' and " +
						" b.dbmsPartCode='"+dbmsPartCode+"'");
				Iterator serviceRecItr = serviceRecQ.list().iterator();
				long scheduledEngHours = 0L; 
				while(serviceRecItr.hasNext())
				{
					ServiceScheduleEntity serviceSch = (ServiceScheduleEntity) serviceRecItr.next();
					scheduledEngHours = serviceSch.getEngineHoursSchedule();
				}
				
				if(scheduledEngHours==0)
				{
					iLogger.info("MOOL:EADataPopulation:ServiceCompletion:"+"No Schedule defined with dbms partcode:"+dbmsPartCode+" for the VIN:"+serialNumber);
				}
				
				else
				{
					Query oldSchQ = session.createQuery("from AssetServiceScheduleEntity where serialNumber='"+serialNumber+"' " +
							" and engineHoursSchedule <= "+scheduledEngHours);
					Iterator oldSchItr = oldSchQ.list().iterator();
					while(oldSchItr.hasNext())
					{
						AssetServiceScheduleEntity assetSer = (AssetServiceScheduleEntity)oldSchItr.next();
						servSchIdList.add(assetSer.getServiceScheduleId().getServiceScheduleId());
					}
				}
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				status="FAILURE";
				fLogger.fatal("MOOL:EADataPopulation:ServiceCompletion:assetId:"+serialNumber+":dbmsPartCode:"+dbmsPartCode+":Exception:"+e.getMessage());
				failureCause = e.getMessage();
			}
			
			finally
			{
				 try
		         {
		        	if(session.isOpen())
		            {
		          	    if(session.getTransaction().isActive())
		                {
		                      session.getTransaction().commit();
		                }
		            }
		         }
		            
		         catch(Exception e)
		         {
		          	fLogger.fatal("MOOL:EADataPopulation:ServiceCompletion:assetId:"+serialNumber+":dbmsPartCode:"+dbmsPartCode+":Exception in closing the session:"+e.getMessage());
		          	status = " FAILURE";
		         }
		            
		         if(session.isOpen())
		         {
		           	session.flush();
		            session.close();
		         }
			}
			
			iLogger.info("MOOL:EADataPopulation:ServiceCompletion:ServiceScheduleId List to be updated for Alerts:"+servSchIdList);
				
			//----------------------- STEP2: Close the active alerts for service completion in OrientAppDB
			OrientAppDbDatasource connObj = new OrientAppDbDatasource();
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;
			
			try
			{
				conn = connObj.getConnection();
				stmt = conn.createStatement();
				
				//Get the Current Timestamp in GMT
				Date currentDate = new Date();
				DateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    TimeZone gmtTime = TimeZone.getTimeZone("GMT");
			    gmtFormat.setTimeZone(gmtTime);
				String currTimeInGMT = gmtFormat.format(currentDate);
				
				
				for(int i=0; i<servSchIdList.size(); i++)
				{
					rs = stmt.executeQuery(" select TxnKey from TAlertTxn where out(IS_ACTIVE).AssetID IN ('"+serialNumber+"') and " +
							" out(CORRESPONDS_TO).out(BELONGS_TO).Code IN ('001') and Status='Generated' and ServiceScheduleID = "+servSchIdList.get(i) );
					
					while(rs.next())
					{
						String txnKey = rs.getObject("TxnKey").toString();
						String updateQ = "Update TAlertTxn set Status='Closed', TransactionTS='"+currTimeInGMT+"' where TxnKey='"+txnKey+"'";
						stmt.executeUpdate(updateQ);
						iLogger.info("MOOL:EADataPopulation:ServiceCompletion:Update status to Closed for TxnKey:"+txnKey);
					}
					
				}
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				status="FAILURE";
				fLogger.fatal("MOOL:EADataPopulation:ServiceCompletion:assetId:"+serialNumber+":dbmsPartCode:"+dbmsPartCode+":Exception in closing active alerts in orientApp db:"+e.getMessage());
				failureCause = e.getMessage();
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
					fLogger.fatal("MOOL:EADataPopulation:ServiceCompletion:assetId:"+serialNumber+":dbmsPartCode:"+dbmsPartCode+":Exception in closing connection to OrientAppDb:"+e.getMessage());
				}
				
			}
		}
		
		//----------------------------- If any exception is thrown / Failure in persisting the data in MOOL, store the details in mool_fault_details table
		//for later re - processing of the same
		if(status.equalsIgnoreCase("FAILURE"))
		{
			String setErrStatus = new MoolErrorMsgHandlerBO().handleErrorMessages(messageId, fileName, "ServiceCompletion", messageString, failureCause) ;
							
			//If the error records cannot be persisted to Mysql, place the same into file with MOOLSEP as delimiter
			if(setErrStatus.equalsIgnoreCase("FAILURE"))
				new MoolErrorMsgHandlerBO().fileHandler(messageId, fileName, "ServiceCompletion", messageString, failureCause);
		}
				
		//------------------------------ Forward the service completion details to Insight Data
		try
		{
			for(int i=0; i<servSchIdList.size(); i++)
			{
				new ServiceClosureIDUpdate().updateAlertClosureToID(serialNumber, servSchIdList.get(i));
			}
		}
				
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("MOOL:EADataPopulation:ID:ServiceClosureDetails for :"+serialNumber+": Exception in the invocation of MOOL URL for Service completion");
		}
				
		return status;
	}*/
}
