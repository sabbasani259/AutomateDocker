package remote.wise.businessobject;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetGroupEntity;
import remote.wise.businessentity.AssetTypeEntity;
import remote.wise.businessentity.FotaVersionEntity;
import remote.wise.businessentity.ProductEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.FotaUpdateReqContract;
import remote.wise.service.implementation.FotaUpdateImpl;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class FotaUpdateNewBONEW {

	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("FotaUpdateBO2:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("FotaUpdateBO2:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("FotaUpdateBO2:","info");*/
	
	
	/**
	 * method to inactive session id if it already exists
	 * 
	 * @param sessionId
	 * @return status
	 */

	public void updateSessionId(String sessionId, String imei, String versionId) {
		ConnectMySQL conSqlObj = new ConnectMySQL();
		Connection con = conSqlObj.getConnection();

    	Logger fLogger = FatalLoggerClass.logger;
		
		CallableStatement cstmt = null;
		ResultSet rs1 = null;
		try {
			cstmt = (CallableStatement) con.prepareCall("{call updateActiveSession(?,?,?)}");
			cstmt.setString(1, sessionId);
			cstmt.setString(2, imei);
			cstmt.setString(3, versionId);

			rs1 = cstmt.executeQuery();

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rs1!=null){
					 rs1.close();
				 }
				 if (cstmt != null) 
					 cstmt.close(); 
				 				 
				 if(con!=null)
					 con.close();
			 
			 }
			 
			 catch(SQLException e)
			 {
				fLogger.fatal("SQLException :"+e);
			 }
		}
	}



	public FotaUpdateImpl getFotaUpdate(FotaUpdateReqContract reqObj) {
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		boolean sessionOpened = false;
		FotaUpdateImpl fotaUpdateImpl = new FotaUpdateImpl();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try {

			if (!(session.isOpen())) {
				session = HibernateUtil.getSessionFactory().openSession();
				session.getTransaction().begin();
			}

			//getting SerialNumber, ProductId, Groupid and Typeid.
			AssetEntity asset=getSerialNumber(reqObj.getFotaimeiNo());
			if(asset!=null){
			String serialNumber=((AssetControlUnitEntity)asset.getSerial_number()).getSerialNumber();
			ProductEntity productId=(ProductEntity)asset.getProductId();
			if(productId!=null){
				int assetGroupId=((AssetGroupEntity)productId.getAssetGroupId()).getAsset_group_id();
				int assetTypeId=((AssetTypeEntity)productId.getAssetTypeId()).getAsset_type_id();
			
				
				// calling getUpdateDetails..
				fotaUpdateImpl =	getUpdateDetails(serialNumber,assetGroupId,assetTypeId,reqObj.getFotaVersionId(),reqObj.getFotaSessionId());
				if (fotaUpdateImpl.getVersionId()!=null){
					if (!fotaUpdateImpl.getVersionId().equalsIgnoreCase("UPDATE_NOT_REQUIRED")) {
						
						// call SP to make existing session id status to 0(inactive) and
						updateSessionId(reqObj.getFotaSessionId(), reqObj.getFotaimeiNo(),fotaUpdateImpl.getVersionId());
				   
					}
				}
				else{
					fotaUpdateImpl.setSessionId(reqObj.getFotaSessionId());
					fotaUpdateImpl.setVersionId("UPDATE_NOT_REQUIRED");
					fotaUpdateImpl.setFile_path("LATEST_FILE_DOES_NOT_EXIST");
				}
			}
			else{
				iLogger.info("PIN "+serialNumber+" is NOT associated with the product ID");
				fotaUpdateImpl.setSessionId(reqObj.getFotaSessionId());
				fotaUpdateImpl.setVersionId("UPDATE_NOT_REQUIRED");
				fotaUpdateImpl.setFile_path("LATEST_FILE_DOES_NOT_EXIST");
			}
			
		}
			//when wrong imei num comes
			else{
				fotaUpdateImpl.setSessionId(reqObj.getFotaSessionId());
				fotaUpdateImpl.setVersionId("UPDATE_NOT_REQUIRED");
				fotaUpdateImpl.setFile_path("LATEST_FILE_DOES_NOT_EXIST");
			}
		}
		 catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Exception :" + e);
		} finally {
			if (sessionOpened) {
				if (session.getTransaction().isActive()) {
					session.getTransaction().commit();
				}

				if (session.isOpen()) {
					session.flush();
					session.close();
				}
			}

		}

		return fotaUpdateImpl;
	}


//get serial number
	private AssetEntity getSerialNumber(String fotaimeiNo) {
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		AssetEntity asset=null;

		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
	
		try {
			
			String basicSelectQuery = " SELECT serialNumber ";
			String basicFromQuery = " FROM AssetControlUnitEntity   ";
			String basicWhereQuery2 = " where imeiNo = '"
				+ fotaimeiNo + "'";
			
			String serialNumber=null;
			
			
			// getting serialNumber using imeiNum from AssetControlUnit
			String finalQuery = basicSelectQuery + basicFromQuery + basicWhereQuery2;
			Query versionQuery = session.createQuery(finalQuery);
			Iterator itr = versionQuery.list().iterator();
			while (itr.hasNext()) {
				serialNumber=(String)itr.next();	
			}
			if(serialNumber==null){
				iLogger.info("IMEI "+fotaimeiNo+" does NOT exist in the table asset_control_unit");
			}
			else{
				//getting values from asset Table.
				String FinalQuery="FROM AssetEntity WHERE serial_number='"+serialNumber+"'";
				versionQuery = session.createQuery(FinalQuery);
				itr = versionQuery.list().iterator();
				while (itr.hasNext()) {
					asset=(AssetEntity)itr.next();	
				}
				if(asset==null){
					iLogger.info("PIN "+serialNumber+" with IMEI "+fotaimeiNo+ " does NOT exist in the asset");
				}
			}
			

		}
			catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("Exception :" + e);
			} finally {
				if (session.getTransaction().isActive()) {
					session.getTransaction().commit();
				}

				if (session.isOpen()) {
					session.flush();
					session.close();
				}

			}
			
			return asset;
		}
	
	//getUpdateDetails...(to get maximum version id)
	public FotaUpdateImpl getUpdateDetails(String serialNumber,int assetGroupId,int assetTypeId,String fotaVersionId,String sessionId){
		
    	Logger fLogger = FatalLoggerClass.logger;
		FotaUpdateImpl fupdate = new FotaUpdateImpl();
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		List<FotaUpdateImpl> fotaUpdate = new LinkedList<FotaUpdateImpl>();
	    List resultList = null;
		try {
			//getting the maximum version id and path using the serial number and 'VIN' .
			String basicSelectQuery = "SELECT versionId,file_path FROM FotaVersionEntity WHERE versionId = (SELECT MAX(versionId) FROM FotaVersionEntity WHERE value='"+serialNumber+"' AND level='VIN')";
			
			String versionId=null;
			String filePath=null;
			boolean flag = false;
			Object[] result = null;
			
			Query versionQuery = session.createQuery(basicSelectQuery);
			resultList = versionQuery.list();
			Iterator itr = resultList.iterator();
			
			if(resultList.size()>0){
				while (itr.hasNext()) {
					result = (Object[]) itr.next();	
					versionId=(String)result[0];
					filePath=(String)result[1];
					}
			}
			
		
if(versionId!=null){
	flag = true;
	if(versionId.compareTo(fotaVersionId)>0)
			{
		    flag = true;
			fupdate.setVersionId(versionId);
			fupdate.setFile_path(filePath);
			fupdate.setSessionId(sessionId);
			return fupdate;
			}
	else{
		flag=false;
	}
}	
		
		 if(!flag){
			 
			 String aTypeId=Integer.toString(assetTypeId);
				
			 
			//getting the maximum version id and path using the asset typeId and 'Model' .
				basicSelectQuery = "SELECT versionId,file_path FROM FotaVersionEntity WHERE versionId = (SELECT MAX(versionId) FROM FotaVersionEntity WHERE value='"+aTypeId+"' AND level='Model')";
				
				versionQuery = session.createQuery(basicSelectQuery);
				resultList = versionQuery.list();
				itr = resultList.iterator();
				if(resultList.size()>0){
					while (itr.hasNext()) {
						result = (Object[]) itr.next();	
						versionId=(String)result[0];
						filePath=(String)result[1];
						}
				}
				
				if(versionId!=null){
					flag = true;
					if(versionId.compareTo(fotaVersionId)>0)
							{
						    flag = true;
							fupdate.setVersionId(versionId);
							fupdate.setFile_path(filePath);
							fupdate.setSessionId(sessionId);
							return fupdate;
							}
					else{
						flag=false;
					}
				}	
			
			}
		 if(!flag){
				
			 String aGroupId=Integer.toString(assetGroupId);
			 
			//getting the maximum version id and path using the asset groupId and 'Profile' .
				basicSelectQuery = "SELECT versionId,file_path FROM FotaVersionEntity WHERE versionId = (SELECT MAX(versionId) FROM FotaVersionEntity WHERE value='"+aGroupId+"' AND level='Profile')";
							
				versionQuery = session.createQuery(basicSelectQuery);
				resultList = versionQuery.list();
				itr = resultList.iterator();
				if(resultList.size()>0){
					while (itr.hasNext()) {
						result = (Object[]) itr.next();	
						versionId=(String)result[0];
						filePath=(String)result[1];
						}
				}
				
				if(versionId!=null){
					flag = true;
					if(versionId.compareTo(fotaVersionId)>0)
							{
						    flag = true;
							fupdate.setVersionId(versionId);
							fupdate.setFile_path(filePath);
							fupdate.setSessionId(sessionId);
							return fupdate;
							}
					else{
						flag=false;
					}
				}	
			
			}
		 
		 if(!flag){
				
			//getting the maximum version id and path where the profile ='ALL'
				basicSelectQuery = "SELECT versionId,file_path FROM FotaVersionEntity WHERE versionId = (SELECT MAX(versionId) FROM FotaVersionEntity WHERE level='All')";

				versionQuery = session.createQuery(basicSelectQuery);
				itr = versionQuery.list().iterator();
//				Object[] result3 = null;
				
				while (itr.hasNext()) {
					result = (Object[]) itr.next();	
					versionId=(String)result[0];
					filePath=(String)result[1];
					}
				if(versionId!=null){
					flag = true;
					if(versionId.compareTo(fotaVersionId)>0)
							{
						    flag = true;
							fupdate.setVersionId(versionId);
							fupdate.setFile_path(filePath);
							fupdate.setSessionId(sessionId);
							return fupdate;
							}
					else{
								fupdate.setVersionId("UPDATE_NOT_REQUIRED");
								fupdate.setFile_path("LATEST_FILE_DOES_NOT_EXIST");
								fupdate.setSessionId(sessionId);
								return fupdate;
								
								}
				}	
				
					

	}// when we didnt get higher versionId.
			
		 }
			catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("Exception :" + e);
			} finally {
				if (session.getTransaction().isActive()) {
					session.getTransaction().commit();
				}

				if (session.isOpen()) {
					session.flush();
					session.close();
				}

			}
			
		return fupdate;
	
	
}}