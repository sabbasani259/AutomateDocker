package remote.wise.businessobject;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import java.sql.Connection;

import remote.wise.businessentity.FotaVersionEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.FotaUpdateReqContract;
import remote.wise.service.implementation.FotaUpdateImpl;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class FotaUpdateBO2 {
	
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

		try {
			cstmt = (CallableStatement) con.prepareCall("{call updateActiveSession(?,?,?)}");
			cstmt.setString(1, sessionId);
			cstmt.setString(2, imei);
			cstmt.setString(3, versionId);

			ResultSet rs1 = cstmt.executeQuery();

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try
			 {
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

	

	/**
	 * method to get the maximum version id from the table fota_version
	 * 
	 * @param dbVersionList
	 * @return
	 */
	public String getMaxVersion(List<String> dbVersionList) {
		String dbMaxVersion = dbVersionList.get(0);

		for (int i = 1; i < dbVersionList.size(); i++) {
			if ((dbMaxVersion.compareTo(dbVersionList.get(i)) < 0))
				dbMaxVersion = dbVersionList.get(i);

			else
				continue;
		}
		return dbMaxVersion;
	}

	public FotaVersionEntity getLatestVersion(String FotaVersionId) {
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		
		String versionId = null;
		List<String> dbVersionList = new ArrayList<String>();
		String dbMaxVersion = null;
		FotaVersionEntity fotaUpdateImpl = new FotaVersionEntity();
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		try {
			iLogger.info("FotaVersionId : " + FotaVersionId);
			String basicSelectQuery = " SELECT v.versionId ";
			String basicFromQuery = " FROM FotaVersionEntity  v ";

			String finalQuery = basicSelectQuery + basicFromQuery;
			Query versionQuery = session.createQuery(finalQuery);
			Iterator itr = versionQuery.list().iterator();
			while (itr.hasNext()) {
				versionId = (String) itr.next();
				dbVersionList.add(versionId);
			}
			if (dbVersionList.size() > 0) {
				// get max version id from table
				dbMaxVersion = getMaxVersion(dbVersionList);
				if (dbMaxVersion != null) {
					if (FotaVersionId.compareTo(dbMaxVersion) >= 0) {
						fotaUpdateImpl.setVersionId("UPDATE_NOT_REQUIRED");
					} else {
						fotaUpdateImpl.setVersionId(dbMaxVersion);
					}
				} else {// maximum version id is null
					fotaUpdateImpl.setVersionId("UPDATE_NOT_REQUIRED");
				}
			} else {// fota_version does not have any data
				fotaUpdateImpl.setVersionId("UPDATE_NOT_REQUIRED");
			}

		} catch (Exception e) {
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
		return fotaUpdateImpl;
	}


	public FotaUpdateImpl getFotaUpdate(FotaUpdateReqContract reqObj) {
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		int flag = 0;
		boolean sessionOpened = false;
		FotaUpdateImpl fotaUpdateImpl = new FotaUpdateImpl();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try {

			Date currentDate = new java.util.Date();
			Timestamp currentTime = new Timestamp(currentDate.getTime());
			if (!(session.isOpen())) {
				session = HibernateUtil.getSessionFactory().openSession();
				session.getTransaction().begin();
			}

			
			
			FotaVersionEntity versionId = getLatestVersion(reqObj.getFotaVersionId());
			if (!versionId.getVersionId().equalsIgnoreCase("UPDATE_NOT_REQUIRED")) {
				// call SP to make existing session id status to 0(inactive) and
				// insert a new row with status 1(active)
				updateSessionId(reqObj.getFotaSessionId(), reqObj.getFotaimeiNo(),versionId.getVersionId());
				String basicSelectQuery2 = " SELECT v.file_path ";
				String basicFromQuery2 = " FROM FotaVersionEntity  v ";
				String basicWhereQuery2 = " where v.versionId = '"
						+ versionId.getVersionId() + "'";
				String finalQuery2 = basicSelectQuery2 + basicFromQuery2
						+ basicWhereQuery2;
				Query q2 = session.createQuery(finalQuery2);
				Iterator itr = q2.list().iterator();
				if (itr.hasNext()) {
					String file_path = (String) itr.next();
					iLogger.info("Updated version file_path " + file_path);
					fotaUpdateImpl.setFile_path(file_path);
					fotaUpdateImpl.setSessionId(reqObj.getFotaSessionId());
					fotaUpdateImpl.setVersionId(versionId.getVersionId());
					flag = 1;
				}
				if (flag == 0) {
					fotaUpdateImpl.setSessionId(reqObj.getFotaSessionId());
					fotaUpdateImpl.setVersionId("UPDATE_NOT_REQUIRED");
					fotaUpdateImpl.setFile_path("LATEST_FILE_DOES_NOT_EXIST");
				}
			}

			else if (versionId.getVersionId().equalsIgnoreCase("UPDATE_NOT_REQUIRED")) {
				fotaUpdateImpl.setSessionId(reqObj.getFotaSessionId());
				fotaUpdateImpl.setVersionId("UPDATE_NOT_REQUIRED");
				fotaUpdateImpl.setFile_path("LATEST_FILE_DOES_NOT_EXIST");
			}
		} catch (Exception e) {
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

}
