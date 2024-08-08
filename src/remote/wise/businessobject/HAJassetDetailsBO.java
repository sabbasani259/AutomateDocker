/**
 * 
 */
package remote.wise.businessobject;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetMonitoringDetailEntity;
import remote.wise.businessentity.AssetMonitoringHeaderEntity;
import remote.wise.businessentity.HAJAssetContactSnapshotEntity;
import remote.wise.businessentity.HAJAssetLocationDetailsEntity;
import remote.wise.businessentity.HAJassetSnapshotEntity;
import remote.wise.businessentity.MonitoringParameters;
import remote.wise.businessentity.ProductEntity;
import remote.wise.businessentity.SmsEventEntity;
import remote.wise.dal.DynamicAMS_DAL;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.DetailMachineImpl;
import remote.wise.service.implementation.HAJassetContactDetailsImpl;
import remote.wise.service.implementation.HAJassetDetailsImpl;
import remote.wise.service.implementation.HAJassetLocationDetailsImpl;
import remote.wise.service.implementation.MachineHoursReportImpl;
import remote.wise.service.implementation.MapImpl;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;

/**
 * @author sunayak
 *
 */
public class HAJassetDetailsBO {
	/*public static WiseLogger businessError = WiseLogger.getLogger("MachineInfoDetailsBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("MachineInfoDetailsBO:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("MachineInfoDetailsBO:","info");*/
	
	
	//******************************************Start of getHAJassetDetailService************************************************************
	/** This method returns the HAJassetSnapshotEntity 
	 * @param No input
	 * @return HAJassetSnapshotEntity
	 */
	public List<HAJassetDetailsImpl> getHAJassetDetailService() {
		// TODO Auto-generated method stub
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		List<HAJassetDetailsImpl> listImplObj = new LinkedList<HAJassetDetailsImpl>();

		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		try
		{
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			} 
			String serialNumber =null;
			int primaryOwnerId =0;
			String assetTypeName =null;
			String assetGroupName =null;
			Query query1 = session
			.createQuery("select ase.serial_number, ase.primary_owner_id, ase.asset_type_name,ase.asset_group_name " +
					" from HAJassetSnapshotEntity ase where" +
			" ase.sendFlag=1 ");
			Iterator query4Itr = query1.list().iterator();
			while (query4Itr.hasNext())
			{
				//System.out.println("Get machine details");
				Object[] result = (Object[]) query4Itr.next();
				HAJassetDetailsImpl newimplObj1 = new HAJassetDetailsImpl();
				if(result[0]!=null)
				{					
					serialNumber = (String) result[0];
					newimplObj1.setSerialNumber(serialNumber);
				}
				if(result[1]!=null)
				{
					primaryOwnerId=(Integer) result[1];
					newimplObj1.setPrimaryAccountId(primaryOwnerId);
				}
				if(result[2]!=null)
				{
					assetTypeName=(String) result[2];
					newimplObj1.setAssetTypeName(assetTypeName);
				}
				if(result[3]!=null)
				{
					assetGroupName=(String)result[3];
					newimplObj1.setAssetGroupName(assetGroupName);
				}
				listImplObj.add(newimplObj1);
			}
		}

		catch(Exception e)
		{
			e.printStackTrace();
			fatalError.fatal("Exception :"+e);
		}
		finally
		{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}
			if(session.isOpen()){
				session.flush();
				session.close();
			} 

		}

		return listImplObj;
	}
	//******************************************End of getHAJassetDetailService************************************************************


	//******************************************Start of setHAJassetData************************************************************
	public String setHAJassetData() {
		// TODO Auto-generated method stub
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		try

		{
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			} 
			String serialNumber =null;
			int primaryOwnerId =0;
			String assetTypeName =null;
			String assetGroupName =null;
			HAJassetSnapshotEntity assetHAJentity = null;
			Query query = session
			.createQuery("select a.serial_number, a.primary_owner_id, a.productId " +
					" from AssetEntity a where" +
					" a.serial_number NOT IN (select distinct(ase.serial_number) from HAJassetSnapshotEntity ase)" +
			" and a.productId !=null ");

			Iterator queryItr = query.list().iterator();
			Object[] ResultSet1 = null;
			while(queryItr.hasNext())
			{
				//System.out.println("Set machine details");
				ResultSet1 = (Object[]) queryItr.next();
				assetHAJentity = new HAJassetSnapshotEntity();
				ProductEntity product =null;
				AssetControlUnitEntity AssetControlUnit =null;
				if(ResultSet1[0]!=null)
				{					
					AssetControlUnit = (AssetControlUnitEntity)ResultSet1[0];
					serialNumber = AssetControlUnit.getSerialNumber();
					assetHAJentity.setSerial_number(serialNumber);
				}
				if(ResultSet1[1]!=null)
				{
					primaryOwnerId=(Integer) ResultSet1[1];
					assetHAJentity.setPrimary_owner_id(primaryOwnerId);
				}
				if(ResultSet1[2]!=null)
				{
					product =(ProductEntity)ResultSet1[2];
					assetTypeName = product.getAssetTypeId().getAsset_type_name();
					assetGroupName = product.getAssetGroupId().getAsset_group_name();
					assetHAJentity.setAsset_group_name(assetGroupName);
					assetHAJentity.setAsset_type_name(assetTypeName);
				}
				assetHAJentity.setSendFlag(1);
				session.save(assetHAJentity);
			}

			//To get the Updated machine details
			AssetControlUnitEntity AssetControlUnit =null;
			Query query1 = session
			.createQuery("select a.serial_number, a.primary_owner_id " +
					" from AssetEntity a ,HAJassetSnapshotEntity ass where " +
					" a.serial_number =ass.serial_number and " +
			" a.primary_owner_id !=ass.primary_owner_id ");
			int list_size =query1.list().size();
			if(list_size!=0){
				Iterator query1Itr = query1.list().iterator();
				while (query1Itr.hasNext())
				{
					//System.out.println("Updated machine details");
					Object[] result = (Object[]) query1Itr.next();
					if(result[0]!=null)
					{					
						AssetControlUnit = (AssetControlUnitEntity)result[0];
						serialNumber = AssetControlUnit.getSerialNumber();
					}
					if(result[1]!=null)
					{
						primaryOwnerId=(Integer)result[1];
					}
					HAJassetSnapshotEntity assetControl = null;
					Query q = session.createQuery(" from HAJassetSnapshotEntity where serial_number='"+serialNumber+"'");
					Iterator itr = q.list().iterator();
					while(itr.hasNext())
					{
						assetControl = (HAJassetSnapshotEntity) itr.next();
					}
					if(assetControl!=null)
					{
						assetControl.setPrimary_owner_id(primaryOwnerId);
						assetControl.setSendFlag(1);
						session.update(assetControl);
					}
				}
			}
		}

		catch(Exception e)
		{
			e.printStackTrace();
			fatalError.fatal("Exception :"+e);
		}
		finally
		{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}
			if(session.isOpen()){
				session.flush();
				session.close();
			} 

		}
		return "Success";
	}
	//******************************************End of setHAJassetData************************************************************

	//******************************************Start of updateHAJassetDetailService************************************************************

	public void updateHAJassetDetailService() {
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		// TODO Auto-generated method stub
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		try
		{
			Query fuelUpdateQuery = session.createQuery(" update HAJassetSnapshotEntity set sendFlag=0" +
			" where sendFlag=1");
			int rowsAffected = fuelUpdateQuery.executeUpdate();
			infoLogger.info(rowsAffected+": update the send flag to 0");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fatalError.fatal("Exception :"+e);
		}
		finally
		{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}
			if(session.isOpen()){
				session.flush();
				session.close();
			} 

		}
	}
	//******************************************End of updateHAJassetDetailService************************************************************

	//******************************************Start of getHAJlocationDetail************************************************************

	/** This method returns the HAJAssetLocationDetailsEntity 
	 * @param No input
	 * @return HAJAssetLocationDetailsEntity
	 */
	public List<HAJassetLocationDetailsImpl> getHAJlocationDetail() {
		// TODO Auto-generated method stub
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		List<HAJassetLocationDetailsImpl> listImplObj = new LinkedList<HAJassetLocationDetailsImpl>();

		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		try
		{
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			} 
			String serialNumber =null;
			String latitude =null;
			String longitude =null;
			Query query1 = session
			.createQuery("select ase.serialNumber, ase.Latitude, ase.Longitude " +
					" from HAJAssetLocationDetailsEntity ase where" +
			" ase.sendFlag=1 ");
			Iterator query4Itr = query1.list().iterator();
			while (query4Itr.hasNext())
			{
				//System.out.println("Get machine details");
				Object[] result = (Object[]) query4Itr.next();
				HAJassetLocationDetailsImpl newimplObj1 = new HAJassetLocationDetailsImpl();
				if(result[0]!=null)
				{					
					serialNumber = (String) result[0];
					newimplObj1.setSerialNumber(serialNumber);
				}
				if(result[1]!=null)
				{
					latitude=(String) result[1];
					newimplObj1.setLatitude(latitude);
				}
				if(result[2]!=null)
				{
					longitude=(String) result[2];
					newimplObj1.setLongitude(longitude);
				}
				listImplObj.add(newimplObj1);
			}
		}

		catch(Exception e)
		{
			e.printStackTrace();
			fatalError.fatal("Exception :"+e);
		}
		finally
		{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}
			if(session.isOpen()){
				session.flush();
				session.close();
			} 

		}

		return listImplObj;
	}
	//******************************************End of getHAJlocationDetail************************************************************

	//******************************************Start of updateHAJassetLocationDetail************************************************************
	public void updateHAJassetLocationDetail() {
		// TODO Auto-generated method stub
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		try
		{
			Query fuelUpdateQuery = session.createQuery(" update HAJAssetLocationDetailsEntity set sendFlag=0" +
			" where sendFlag=1");
			int rowsAffected = fuelUpdateQuery.executeUpdate();
			infoLogger.info(rowsAffected+": update the send flag to 0");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fatalError.fatal("Exception :"+e);
		}
		finally
		{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}
			if(session.isOpen()){
				session.flush();
				session.close();
			} 

		}
	}
	//******************************************End of updateHAJassetLocationDetail************************************************************

	//******************************************Start of setHAJassetLocationDetails************************************************************
	
	//DF20160720 @Roopa fetching location details from the new AMS table as part of new Dynamic AMH and AMD
	public String setHAJassetLocationDetails() {
	    Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalError = FatalLoggerClass.logger;
		
		 Connection prodConnection = null;
			Statement statement = null;
			
	
	
	DynamicAMS_DAL amsObj=new DynamicAMS_DAL();
	try
	{
		
		//DF20161222 @Roopa changing asset_monitoring_snapshot_new to asset_monitoring_snapshot which is having txndata as json column	
		
	/*	String HAJLatLongInsertQuery="select a.Serial_Number, a.parameters from asset_monitoring_snapshot_new a where " +
		                " a.Serial_Number NOT IN (select distinct(ase.Serial_Number) from HAJ_location_snapshot ase)" +
				        " and a.parameters is NOT NULL " +
				        " group by a.Serial_Number";*/
		
		String HAJLatLongInsertQuery="select a.Serial_Number, a.TxnData from asset_monitoring_snapshot a where " +
                " a.Serial_Number NOT IN (select distinct(ase.Serial_Number) from HAJ_location_snapshot ase)" +
		        " and a.TxnData is NOT NULL " +
		        " group by a.Serial_Number";
		
		infoLogger.info("HAJLatLongInsertQuery::"+HAJLatLongInsertQuery);
		
		List<HAJAssetLocationDetailsEntity> HAJAssetLocationDetailsEntityList=new LinkedList<HAJAssetLocationDetailsEntity>();
		
		
		try{
		
		HAJAssetLocationDetailsEntityList=amsObj.getQuerySpecificDetailsForHAJAssetDetails(HAJLatLongInsertQuery);
		
		infoLogger.info("insert list size::"+HAJAssetLocationDetailsEntityList.size());
		
		
		
			
				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				
				statement = prodConnection.createStatement();
				prodConnection.setAutoCommit(false);
		
		
		
		
		for(int i=0;i<HAJAssetLocationDetailsEntityList.size();i++){
			
			statement.addBatch("INSERT INTO HAJ_location_snapshot" + "(Serial_Number, Latitude, Longitude, Send_Flag) VALUES"
					+ "('"+HAJAssetLocationDetailsEntityList.get(i).getSerialNumber()+"','"+HAJAssetLocationDetailsEntityList.get(i).getLatitude()+"','"+HAJAssetLocationDetailsEntityList.get(i).getLongitude()+"',1)");
			
		}
		
		int[] insertCount=statement.executeBatch();

		statement.clearBatch();
		
		infoLogger.info("HAJ_location_snapshot insert count::"+insertCount.length);
		
		prodConnection.commit();
		}
		
		catch(BatchUpdateException be){
            be.printStackTrace();
			int[] updateCount = be.getUpdateCounts();
			fatalError.fatal(":setHAJassetLocationDetails BatchUpdateException"+be.getMessage()+"::"+updateCount.length);
			return "FAILURE";
		}

		catch (SQLException e) {

			e.printStackTrace();
			fatalError.fatal(":setHAJassetLocationDetails"+"SQL Exception in inserting records to table::"+"::"+e.getMessage());
			return "FAILURE";
		} 

		catch(Exception e)
		{
			e.printStackTrace();
			fatalError.fatal(":setHAJassetLocationDetails"+"Exception in inserting records to table::"+e.getMessage());
			return "FAILURE";
		}

		finally {
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		//To Update latitude/longitude of machine 
		Calendar now = Calendar.getInstance();
		now = Calendar.getInstance();
		now.add(Calendar.MINUTE, -15);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(now.getTime());
	    
		//DF20161222 @Roopa changing asset_monitoring_snapshot_new to asset_monitoring_snapshot which is having txndata as json column	
		
		/*String HAJLatLongUpdateQuery="select a.Serial_Number, a.parameters from asset_monitoring_snapshot_new a, HAJ_location_snapshot ass where " +
		                      " a.Serial_Number=ass.Serial_Number and " +
				              " a.Latest_Created_Timestamp >='"+currentTime+"' " +
				              " and a.parameters is NOT NULL " +
		                      " group by a.Serial_Number";*/
		
		String HAJLatLongUpdateQuery="select a.Serial_Number, a.TxnData from asset_monitoring_snapshot a, HAJ_location_snapshot ass where " +
                " a.Serial_Number=ass.Serial_Number and " +
	              " a.Latest_Created_Timestamp >='"+currentTime+"' " +
	              " and a.TxnData is NOT NULL " +
                " group by a.Serial_Number";
		
		
		infoLogger.info("HAJLatLongUpdateQuery::"+HAJLatLongUpdateQuery);
		
		List<HAJAssetLocationDetailsEntity> HAJAssetLocationDetailsEntityUpdateList=new LinkedList<HAJAssetLocationDetailsEntity>();
		try{
		
		HAJAssetLocationDetailsEntityUpdateList=amsObj.getQuerySpecificDetailsForHAJAssetDetails(HAJLatLongUpdateQuery);
		
		infoLogger.info("update list size::"+HAJAssetLocationDetailsEntityUpdateList.size());
		
		ConnectMySQL connMySql = new ConnectMySQL();
		prodConnection = connMySql.getConnection();
		
		statement = prodConnection.createStatement();
		prodConnection.setAutoCommit(false);
		
		
			
		for(int i=0;i<HAJAssetLocationDetailsEntityUpdateList.size();i++){
			
			statement.addBatch("UPDATE HAJ_location_snapshot" + " SET Latitude='"+HAJAssetLocationDetailsEntityUpdateList.get(i).getLatitude()+"', Longitude='"+HAJAssetLocationDetailsEntityUpdateList.get(i).getLongitude()+"', Send_Flag=1 where Serial_Number='"+HAJAssetLocationDetailsEntityUpdateList.get(i).getSerialNumber()+"'");
			
			/*if(i==0){
			System.out.println("batch update statement::"+"UPDATE HAJ_location_snapshot" + " SET Latitude='"+HAJAssetLocationDetailsEntityUpdateList.get(i).getLatitude()+"', Longitude='"+HAJAssetLocationDetailsEntityUpdateList.get(i).getLongitude()+"', Send_Flag=1 where Serial_Number='"+HAJAssetLocationDetailsEntityUpdateList.get(i).getSerialNumber()+"'");	
			}*/
		 }
		
		int[] updateCount=statement.executeBatch();

		statement.clearBatch();
		
		infoLogger.info("HAJ_location_snapshot update count::"+updateCount.length);
		
		prodConnection.commit();
		}
		
		catch(BatchUpdateException be){
            be.printStackTrace();
			int[] updateCount = be.getUpdateCounts();
			fatalError.fatal(":setHAJassetLocationDetails BatchUpdateException"+be.getMessage()+"::"+updateCount.length);
			return "FAILURE";
		}

		catch (SQLException e) {

			e.printStackTrace();
			fatalError.fatal(":setHAJassetLocationDetails"+"SQL Exception in inserting records to table::"+"::"+e.getMessage());
			return "FAILURE";
		} 

		catch(Exception e)
		{
			e.printStackTrace();
			fatalError.fatal(":setHAJassetLocationDetails"+"Exception in inserting records to table::"+e.getMessage());
			return "FAILURE";
		}

		finally {
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
		
	}

	catch(Exception e)
	{
		e.printStackTrace();
		fatalError.fatal("Exception :"+e);
	}
	finally
	{
		if(statement!=null)
			try {
				statement.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		if (prodConnection != null) {
			try {
				prodConnection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	

	}
	return "Success";
}
	
	//commenting previous method
	/*public String setHAJassetLocationDetails() {
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		
		
		// 		TODO Auto-generated method stub
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		List<String> assetMonitoringParameters = new LinkedList<String>();
		List<String> assetMonitoringValues = new LinkedList<String>();
		try
		{
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			} 
			String serialNumber =null;
			String parameterValue =null;
			int parameterId =0;
			String prevSerialNumber =null;
			String serialNumber1 =null;
			String prevSerialNumber1 =null;
			HAJAssetLocationDetailsEntity assetLocationentity = null;
			Query query = session
			.createQuery("select a.serialNumber, b.parameterId,b.parameterValue " +
					" from AssetMonitoringSnapshotEntity a,AssetMonitoringDetailEntity b where" +
					" a.serialNumber NOT IN (select distinct(ase.serialNumber) from HAJAssetLocationDetailsEntity ase)" +
			" and b.parameterId in(1,2) and a.transactionNumber=b.transactionNumber ");

			Iterator queryItr = query.list().iterator();
			Object[] ResultSet1 = null;
			while(queryItr.hasNext())
			{
				System.out.println("Set machine details");
				ResultSet1 = (Object[]) queryItr.next();
				assetLocationentity = new HAJAssetLocationDetailsEntity();
				AssetEntity AssetUnit =null;
				MonitoringParameters MonitoringParam =null;
				if(ResultSet1[0]!=null)
				{					
					AssetUnit = (AssetEntity)ResultSet1[0];
					serialNumber = AssetUnit.getSerial_number().getSerialNumber();
				}
				if (!serialNumber.equals(prevSerialNumber)) {
					assetLocationentity.setSerialNumber(serialNumber);
				}
				if(ResultSet1[1]!=null)
				{
					MonitoringParam = (MonitoringParameters)ResultSet1[1];
					parameterId=MonitoringParam.getParameterId();
				}
				if(ResultSet1[2]!=null)
				{
					parameterValue=(String) ResultSet1[2];
				}
				if(parameterId==1){
					assetLocationentity.setLatitude(parameterValue);
				}
				else if(parameterId==2)
				{
					assetLocationentity.setLongitude(parameterValue);
				}
				assetLocationentity.setSendFlag(1);
				if (!serialNumber.equals(prevSerialNumber)) {
					session.save(assetLocationentity);
				}
				prevSerialNumber = serialNumber;
			}
			Query query = session
			.createQuery("select a.serialNumber, CAST(GROUP_CONCAT(amd.parameterId) As string ) as parameterId,  CAST(GROUP_CONCAT(amd.parameterValue) As string ) as parameterValues " +
					" from AssetMonitoringSnapshotEntity a,AssetMonitoringDetailEntity amd where" +
					" a.serialNumber NOT IN (select distinct(ase.serialNumber) from HAJAssetLocationDetailsEntity ase)" +
			        " and amd.parameterId in(1,2) and a.transactionNumber=amd.transactionNumber group by a.serialNumber");

			Iterator queryItr = query.list().iterator();
			Object[] ResultSet1 = null;
			while(queryItr.hasNext())
			{
				//System.out.println("Set machine details");
				ResultSet1 = (Object[]) queryItr.next();
				assetLocationentity = new HAJAssetLocationDetailsEntity();
				AssetEntity AssetUnit =null;
				if(ResultSet1[0]!=null)
				{					
					AssetUnit = (AssetEntity)ResultSet1[0];
					serialNumber = AssetUnit.getSerial_number().getSerialNumber();
				}
				if(ResultSet1[1] !=null )
					assetMonitoringParameters = Arrays.asList(ResultSet1[1].toString().split(","));
				else
					assetMonitoringParameters=new LinkedList<String>();
			
				if(ResultSet1[2] !=null)
					assetMonitoringValues = Arrays.asList(ResultSet1[2].toString().split(","));
				else
					assetMonitoringValues=new LinkedList<String>();
				
				HashMap<String,String> monitoringParametersMap = new HashMap<String,String>();
				for(int i=0; i<assetMonitoringParameters.size();i++)
				{
					monitoringParametersMap.put(assetMonitoringParameters.get(i), assetMonitoringValues.get(i));
				}
				assetLocationentity.setSerialNumber(serialNumber);
				assetLocationentity.setLatitude(monitoringParametersMap.get("1"));
				assetLocationentity.setLongitude(monitoringParametersMap.get("2"));
				assetLocationentity.setSendFlag(1);
				session.save(assetLocationentity);
			}

			//To Update latitude/longitude of machine 
			Calendar now = Calendar.getInstance();
			now = Calendar.getInstance();
			now.add(Calendar.MINUTE, -15);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = sdf.format(now.getTime());
		//	System.out.println("currentTime:"+currentTime);
			AssetEntity AssetUnit1 =null;
			MonitoringParameters MonitoringParam1 =null;
		   Query query1 = session
			.createQuery("select a.serialNumber, b.parameterId, b.parameterValue " +
					" from AssetMonitoringSnapshotEntity a ,AssetMonitoringDetailEntity b,HAJAssetLocationDetailsEntity ass where " +
					" a.serialNumber =ass.serialNumber and " +
					" ass.transactionTime >='"+currentTime+"' " +
			" and b.parameterId in(1,2) and a.transactionTime=b.transactionNumber ");
			Iterator query1Itr = query1.list().iterator();
			while (query1Itr.hasNext())
			{
				System.out.println("Updated machine details");
				Object[] result = (Object[]) query1Itr.next();
				if(result[0]!=null)
				{					
					AssetUnit1 = (AssetEntity)result[0];
					serialNumber1 = AssetUnit1.getSerial_number().getSerialNumber();
				}
				if(result[1]!=null)
				{
					MonitoringParam1 = (MonitoringParameters)result[1];
					parameterId=MonitoringParam1.getParameterId();
				}
				if(result[2]!=null)
				{
					parameterValue=(String) result[2];
				}
				if(parameterId==1){
					assetLocationentity.setLatitude(parameterValue);
				}
				else if(parameterId==2)
				{
					assetLocationentity.setLongitude(parameterValue);
				}
				HAJAssetLocationDetailsEntity assetLocationentity1 = null;
				Query q = session.createQuery(" from HAJAssetLocationDetailsEntity where serialNumber='"+serialNumber1+"'");
				Iterator itr = q.list().iterator();
				while(itr.hasNext())
				{
					assetLocationentity1 = (HAJAssetLocationDetailsEntity) itr.next();
				}
				if(assetLocationentity1!=null)
				{
					if(parameterId==1){
						assetLocationentity1.setLatitude(parameterValue);
					}
					else if(parameterId==2)
					{
						assetLocationentity1.setLongitude(parameterValue);
					}
					assetLocationentity1.setSendFlag(1);
					if (!serialNumber1.equals(prevSerialNumber1)) {
						session.update(assetLocationentity1);
					}
					prevSerialNumber1 = serialNumber1;
				}
			}
			Query query1 = session
			.createQuery("select a.serialNumber, CAST(GROUP_CONCAT(amd.parameterId) As string ) as parameterId,  CAST(GROUP_CONCAT(amd.parameterValue) As string ) as parameterValues " +
					" from AssetMonitoringSnapshotEntity a ,AssetMonitoringDetailEntity amd,HAJAssetLocationDetailsEntity ass where " +
					" a.serialNumber =ass.serialNumber and " +
					" a.latestCreatedTimestamp >='"+currentTime+"' " +
			        " and amd.parameterId in(1,2) and a.transactionNumber=amd.transactionNumber group by a.serialNumber");
			Iterator query1Itr = query1.list().iterator();
			while (query1Itr.hasNext())
			{
				//System.out.println("Updated machine details");
				Object[] result = (Object[]) query1Itr.next();
				if(result[0]!=null)
				{					
					AssetUnit1 = (AssetEntity)result[0];
					serialNumber1 = AssetUnit1.getSerial_number().getSerialNumber();
				}
				if(result[1] !=null )
					assetMonitoringParameters = Arrays.asList(result[1].toString().split(","));
				else
					assetMonitoringParameters=new LinkedList<String>();
			
				if(result[2] !=null)
					assetMonitoringValues = Arrays.asList(result[2].toString().split(","));
				else
					assetMonitoringValues=new LinkedList<String>();
				
				HashMap<String,String> monitoringParametersMap = new HashMap<String,String>();
				for(int i=0; i<assetMonitoringParameters.size();i++)
				{
					monitoringParametersMap.put(assetMonitoringParameters.get(i), assetMonitoringValues.get(i));
				}
				
				HAJAssetLocationDetailsEntity assetLocationentity1 = null;
				Query q = session.createQuery(" from HAJAssetLocationDetailsEntity where serialNumber='"+serialNumber1+"'");
				Iterator itr = q.list().iterator();
				while(itr.hasNext())
				{
					assetLocationentity1 = (HAJAssetLocationDetailsEntity) itr.next();
				}
				if(assetLocationentity1!=null)
				{
						assetLocationentity1.setLatitude(monitoringParametersMap.get("1"));
						assetLocationentity1.setLongitude(monitoringParametersMap.get("2"));
						assetLocationentity1.setSendFlag(1);
						session.update(assetLocationentity1);
				}
				
			}
			
		}

		catch(Exception e)
		{
			e.printStackTrace();
			fatalError.fatal("Exception :"+e);
		}
		finally
		{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}
			if(session.isOpen()){
				session.flush();
				session.close();
			} 

		}
		return "Success";
	}*/
	//******************************************End of setHAJassetLocationDetails************************************************************

	//******************************************Start of updateHAJassetContactDetailService************************************************************
	public void updateHAJassetContactDetailService() {

		// TODO Auto-generated method stub
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		try
		{
			Query fuelUpdateQuery = session.createQuery(" update HAJAssetContactSnapshotEntity set sendFlag=0" +
			" where sendFlag=1");
			int rowsAffected = fuelUpdateQuery.executeUpdate();
			infoLogger.info(rowsAffected+": update the send flag to 0");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fatalError.fatal("Exception :"+e);
		}
		finally
		{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}
			if(session.isOpen()){
				session.flush();
				session.close();
			} 

		}

	}
	//******************************************End of updateHAJassetContactDetailService************************************************************

	//******************************************Start of setHAJassetContactData************************************************************
	public String setHAJassetContactData() {

		// TODO Auto-generated method stub
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		try
		{
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			} 
			String contactId =null;
			int accountId =0;
			String first_Name =null;
			String last_Name =null;
			String mobile_Number=null;
			String email_ID=null;
			String sendFlag=null;
			String password=null;
			HAJAssetContactSnapshotEntity assetHAJContactentity = null;
			AccountEntity accEntity =null;
			Query query = session
			.createQuery("select a.contact_id, a.first_name, a.last_name, a.primary_mobile_number, a.primary_email_id ,ac.account_id ,a.password " +
					" from ContactEntity a,AccountContactMapping ac where" +
					" a.contact_id NOT IN (select distinct(ase.contactID) from HAJAssetContactSnapshotEntity ase)" +
					" and a.is_tenancy_admin=1 and a.active_status=1" +
			" and a.contact_id =ac.contact_id ");

			Iterator queryItr = query.list().iterator();
			Object[] ResultSet1 = null;
			while(queryItr.hasNext())
			{
				//System.out.println("Set machine details");
				ResultSet1 = (Object[]) queryItr.next();
				assetHAJContactentity = new HAJAssetContactSnapshotEntity();
				if(ResultSet1[0]!=null)
				{					
					contactId=(String)ResultSet1[0];
					assetHAJContactentity.setContactID(contactId);
				}
				if(ResultSet1[1]!=null)
				{
					first_Name=(String)ResultSet1[1];
					assetHAJContactentity.setFirst_Name(first_Name);
				}
				if(ResultSet1[2]!=null)
				{
					last_Name=(String)ResultSet1[2];
					assetHAJContactentity.setLast_Name(last_Name);
				}
				if(ResultSet1[3]!=null)
				{
					mobile_Number=(String)ResultSet1[3];
					assetHAJContactentity.setMobile_Number(mobile_Number);
				}
				if(ResultSet1[4]!=null)
				{
					email_ID=(String)ResultSet1[4];
					assetHAJContactentity.setEmail_Id(email_ID);
				}
				if(ResultSet1[5]!=null)
				{
					accEntity=(AccountEntity)ResultSet1[5];
					accountId = accEntity.getAccount_id();
					assetHAJContactentity.setAccountID(accountId);
				}
				if(ResultSet1[6]!=null)
				{
					password=(String)ResultSet1[6];
					assetHAJContactentity.setPassword(password);
				}
				assetHAJContactentity.setSendFlag(1);
				session.save(assetHAJContactentity);
			}

			//To get the Updated contact details when primaryEmailId changed
			String contact_id =null;
			String primary_email_id =null;
			String primary_mobile_number =null;
			Query query1 = session
			.createQuery("select acs.contactID, a.primary_email_id " +
					" from ContactEntity a ,HAJAssetContactSnapshotEntity acs where " +
					" a.contact_id =acs.contactID and " +
			        " a.primary_email_id !=acs.email_Id ");
			int list_size =query1.list().size();
			if(list_size>0){
				Iterator query1Itr = query1.list().iterator();
				while (query1Itr.hasNext())
				{
					//System.out.println("Updated emailId details");
					Object[] result = (Object[]) query1Itr.next();
					if(result[0]!=null)
					{					
						contact_id=(String)result[0];
					}
					if(result[1]!=null)
					{
						primary_email_id=(String)result[1];
					}
					HAJAssetContactSnapshotEntity assetContactEntity = null;
					Query q = session.createQuery(" from HAJAssetContactSnapshotEntity where contactID='"+contact_id+"'");
					Iterator itr = q.list().iterator();
					while(itr.hasNext())
					{
						assetContactEntity = (HAJAssetContactSnapshotEntity) itr.next();
					}
					if(assetContactEntity!=null)
					{
						assetContactEntity.setEmail_Id(primary_email_id);
						assetContactEntity.setSendFlag(1);
						session.update(assetContactEntity);
					}
				}
			}

			//To get the Updated contact details when primaryMobileNumber changed

			Query query2 = session
			.createQuery("select acs.contactID, a.primary_mobile_number " +
					" from ContactEntity a ,HAJAssetContactSnapshotEntity acs where " +
					" a.contact_id =acs.contactID and " +
			" a.primary_mobile_number !=acs.mobile_Number ");
			int list_size1 =query2.list().size();
			if(list_size1>0){
			Iterator query2Itr = query2.list().iterator();
			while (query2Itr.hasNext())
			{
				//System.out.println("Updated primaryMobileNumber details");
				Object[] result1 = (Object[]) query2Itr.next();
				if(result1[0]!=null)
				{					
					contact_id=(String)result1[0];
				}
				if(result1[1]!=null)
				{
					primary_mobile_number=(String)result1[1];
				}
				HAJAssetContactSnapshotEntity assetContactEntity = null;
				Query q = session.createQuery(" from HAJAssetContactSnapshotEntity where contactID='"+contact_id+"'");
				Iterator itr = q.list().iterator();
				while(itr.hasNext())
				{
					assetContactEntity = (HAJAssetContactSnapshotEntity) itr.next();
				}
				if(assetContactEntity!=null)
				{
					assetContactEntity.setMobile_Number(primary_mobile_number);
					assetContactEntity.setSendFlag(1);
					session.update(assetContactEntity);
				}
			}
			}
			
			//To get the Updated contact details when password changed
			String pwd =null;
			Query query3 = session
			.createQuery("select acs.contactID, a.password " +
					" from ContactEntity a ,HAJAssetContactSnapshotEntity acs where " +
					" a.contact_id =acs.contactID and " +
					" a.password !=acs.password");
			int list_size2 =query3.list().size();
			if(list_size2>0){
			Iterator query3Itr = query3.list().iterator();
			while (query3Itr.hasNext())
			{
				//System.out.println("Updated contact details");
				Object[] result1 = (Object[]) query3Itr.next();
				if(result1[0]!=null)
				{					
					contact_id=(String)result1[0];
				}
				if(result1[1]!=null)
				{
					pwd=(String)result1[1];
				}
				HAJAssetContactSnapshotEntity assetContactEntity = null;
				Query q = session.createQuery(" from HAJAssetContactSnapshotEntity where contactID='"+contact_id+"'");
				Iterator itr = q.list().iterator();
				while(itr.hasNext())
				{
					assetContactEntity = (HAJAssetContactSnapshotEntity) itr.next();
				}
				if(assetContactEntity!=null)
				{
					assetContactEntity.setPassword(pwd);
					assetContactEntity.setSendFlag(1);
					session.update(assetContactEntity);
				}
			}
			}
		}

		catch(Exception e)
		{
			e.printStackTrace();
			fatalError.fatal("Exception :"+e);
		}
		finally
		{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}
			if(session.isOpen()){
				session.flush();
				session.close();
			} 

		}
		return "Success";
	}
	//******************************************End of setHAJassetContactData************************************************************

	//******************************************Start of getHAJassetContactDetail************************************************************
	/** This method returns the HAJAssetContactSnapshotEntity 
	 * @param No input
	 * @return HAJAssetContactSnapshotEntity
	 */
	public List<HAJassetContactDetailsImpl> getHAJassetContactDetail() {
		// TODO Auto-generated method stub
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		List<HAJassetContactDetailsImpl> listImplObj = new LinkedList<HAJassetContactDetailsImpl>();

		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		try
		{
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			} 
			String contactId =null;
			int accountId =0;
			String first_Name =null;
			String last_Name =null;
			String mobile_Number=null;
			String email_ID=null;
			String password=null;
			Query query1 = session
			.createQuery("select ase.contactID, ase.accountID, ase.first_Name,ase.last_Name,ase.mobile_Number,ase.email_Id,ase.password " +
					" from HAJAssetContactSnapshotEntity ase where" +
			" ase.sendFlag=1 ");
			Iterator query4Itr = query1.list().iterator();
			while (query4Itr.hasNext())
			{
				//System.out.println("Get machine details");
				Object[] result = (Object[]) query4Itr.next();
				HAJassetContactDetailsImpl newimplObj1 = new HAJassetContactDetailsImpl();
				if(result[0]!=null)
				{					
					contactId = (String) result[0];
					newimplObj1.setContactID(contactId);
				}
				if(result[1]!=null)
				{
					accountId=(Integer) result[1];
					newimplObj1.setAccountID(accountId);
				}
				if(result[2]!=null)
				{
					first_Name=(String) result[2];
					newimplObj1.setFirst_Name(first_Name);
				}
				if(result[3]!=null)
				{
					last_Name=(String)result[3];
					newimplObj1.setLast_Name(last_Name);
				}
				if(result[4]!=null)
				{
					mobile_Number=(String)result[4];
					newimplObj1.setMobile_Number(mobile_Number);
				}
				if(result[5]!=null)
				{
					email_ID=(String)result[5];
					newimplObj1.setEmail_Id(email_ID);
				}
				if(result[6]!=null)
				{
					password=(String)result[6];
					newimplObj1.setPassword(password);
				}
				listImplObj.add(newimplObj1);
			}
		}

		catch(Exception e)
		{
			e.printStackTrace();
			fatalError.fatal("Exception :"+e);
		}
		finally
		{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}
			if(session.isOpen()){
				session.flush();
				session.close();
			} 

		}

		return listImplObj;
	}
	//******************************************End of getHAJassetContactDetail************************************************************
}
