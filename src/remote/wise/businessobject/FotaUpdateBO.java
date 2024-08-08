package remote.wise.businessobject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;


import remote.wise.businessentity.FotaAuthenticationEntity;
import remote.wise.businessentity.FotaVersionEntity;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.FotaUpdateReqContract;
import remote.wise.service.implementation.FotaUpdateImpl;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class FotaUpdateBO {
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("FotaUpdateBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("FotaUpdateBO:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("FotaUpdateBO:","info");*/
	
	
	public String authenticateSessionId(String sessionId)
	{
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		String status = null; 
		FotaUpdateImpl fotaUpdateImpl = new FotaUpdateImpl();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
		try
		{
			String basicSelectQuery = " SELECT COUNT(a.fotaId) ";
			String basicFromQuery = " FROM FotaAuthenticationEntity  a ";
			String basicWhereQuery = " where a.sessionId  = '"+sessionId+"' and a.status=0" ;
			String finalQuery =basicSelectQuery+ basicFromQuery + basicWhereQuery;
			Query q = session.createQuery(finalQuery);
            Iterator itr = q.list().iterator();
            Long count=(long)0;
            
			if(itr.hasNext())
			{
				count = (Long)itr.next();
				infoLogger.info("Duplicate sessionID   "+sessionId);
				infoLogger.info("Count of Session ID :"+count);
				fotaUpdateImpl.setSessionId(sessionId);
				status = "DUPLICATE_SESSION_ID";				
			}
			if(count == 0)
			{
				fotaUpdateImpl.setSessionId(sessionId);
				status = "VALID_SESSION_ID";
				return status;
			}
			else{
				return status;
			}	
		}
		finally
	    {
			
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
	}
	/**
	 * method to get the maximum version id from the table fota_version 
	 * @param dbVersionList
	 * @return
	 */
	public String getMaxVersion(List<String> dbVersionList){
        String dbMaxVersion=dbVersionList.get(0);
        
        for(int i=1;i<dbVersionList.size();i++)
        {
              if((dbMaxVersion.compareTo(dbVersionList.get(i))<0))
            	  dbMaxVersion=dbVersionList.get(i);
              
              else 
                    continue;
              }
        return dbMaxVersion;
	}
	public FotaVersionEntity getLatestVersion(String FotaVersionId)
	{
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
			
		String versionId = null;
		List<String> dbVersionList = new ArrayList<String>();
		String dbMaxVersion = null;
		FotaVersionEntity fotaUpdateImpl = new FotaVersionEntity();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
		try
		{
			infoLogger.info("FotaVersionId printing: ** " + FotaVersionId );
			String basicSelectQuery = " SELECT v.versionId ";
			String basicFromQuery = " FROM FotaVersionEntity  v ";
			
			String finalQuery =basicSelectQuery+ basicFromQuery ;
			Query versionQuery = session.createQuery(finalQuery);
			Iterator itr = versionQuery.list().iterator();
			while(itr.hasNext())
			{
				versionId = (String) itr.next();
				dbVersionList.add(versionId);
			}
			if(dbVersionList.size()>0){
				//get max version id from table
				dbMaxVersion = getMaxVersion(dbVersionList);
				if(dbMaxVersion!=null){
					if(FotaVersionId.compareTo(dbMaxVersion)>=0){
						fotaUpdateImpl.setVersionId("UPDATE_NOT_REQUIRED") ;
					}		
					else{
						fotaUpdateImpl.setVersionId(dbMaxVersion) ;
					}
				}
				else{//maximum version id is null
					fotaUpdateImpl.setVersionId("UPDATE_NOT_REQUIRED") ;
				}
			}
			else{//fota_version does not have any data
				fotaUpdateImpl.setVersionId("UPDATE_NOT_REQUIRED") ;
			}		
			
				
		}catch (Exception e)
		 {
			fatalError.fatal("Exception :" + e);
		 }
		finally
	    {
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
		return fotaUpdateImpl;	
	}
	public FotaVersionEntity getLatestVersion2(String FotaVersionId)
	{
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
			
			
		int flag = 0;
		String versionId = null;
		FotaVersionEntity fotaUpdateImpl = new FotaVersionEntity();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
		try
		{
			infoLogger.info("FotaVersionId printing: ** " + FotaVersionId );
			String basicSelectQuery = " SELECT v.versionId ";
			String basicFromQuery = " FROM FotaVersionEntity  v ";
			String 	basicWhereQuery = "WHERE v.version_date = (select MAX(b.version_date) " +
					"from FotaVersionEntity b ) AND v.versionId <> '" +FotaVersionId+"'" ;
			infoLogger.info("FotaVersionId printing after : ** " + FotaVersionId );
			String finalQuery =basicSelectQuery+ basicFromQuery + basicWhereQuery;
			Query versionQuery = session.createQuery(finalQuery);
			Iterator itr = versionQuery.list().iterator();
			while(itr.hasNext())
			{
				versionId = (String) itr.next();
				fotaUpdateImpl.setVersionId(versionId);
				infoLogger.info("versionID   "+versionId);
				flag = 1;
			}
			if(flag == 0)
			{
				fotaUpdateImpl.setVersionId("UPDATE_NOT_REQUIRED") ;
				
			}	
		}catch (Exception e)
		 {
			fatalError.fatal("Exception :" + e);
		 }
		finally
	    {
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
		return fotaUpdateImpl;	
	}
	public FotaUpdateImpl getFotaUpdate(FotaUpdateReqContract reqObj)
	{
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
			
			
		int flag = 0;
		boolean sessionOpened=false;
		FotaUpdateImpl fotaUpdateImpl = new FotaUpdateImpl();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
		try
		{
			
			Date currentDate = new java.util.Date();
			Timestamp currentTime = new Timestamp(currentDate.getTime());
			if(! (session.isOpen() ))
            {
                        session = HibernateUtil.getSessionFactory().openSession();
                        session.getTransaction().begin();
            }
			
			fotaUpdateImpl.setStatus(authenticateSessionId(reqObj.getFotaSessionId())) ;
			infoLogger.info(fotaUpdateImpl.getStatus());
			if(!fotaUpdateImpl.getStatus().equalsIgnoreCase("DUPLICATE_SESSION_ID"))
				{
				infoLogger.info("Fota Version ID printing*** " + reqObj.getFotaVersionId());
					FotaVersionEntity versionId = getLatestVersion(reqObj.getFotaVersionId());
					if(!versionId.getVersionId().equalsIgnoreCase("UPDATE_NOT_REQUIRED"))
					{
						FotaAuthenticationEntity FotaUpdateObj = new FotaAuthenticationEntity();
						FotaUpdateObj.setImeiNo(reqObj.getFotaimeiNo());
						FotaUpdateObj.setSessionId(reqObj.getFotaSessionId());
						FotaUpdateObj.setVersionId(versionId);
						FotaUpdateObj.setUpdate_date(currentTime);
						FotaUpdateObj.setStatus(0);
						FotaUpdateObj.save();
						if(! (session.isOpen() ))
			            {
			                        session = HibernateUtil.getSessionFactory().getCurrentSession();
			                        session.getTransaction().begin();
			                        sessionOpened=true;
			            }
						String basicSelectQuery2 = " SELECT v.file_path ";
						String basicFromQuery2 = " FROM FotaVersionEntity  v ";
						String basicWhereQuery2 = " where v.versionId = '"+versionId.getVersionId()+"'" ;
						String finalQuery2 =basicSelectQuery2+ basicFromQuery2 + basicWhereQuery2;
						Query q2 = session.createQuery(finalQuery2);
						Iterator itr = q2.list().iterator();
						if(itr.hasNext())
						{
							String file_path = (String) itr.next();
							infoLogger.info("Updated version file_path "+file_path);
							fotaUpdateImpl.setFile_path(file_path);
							fotaUpdateImpl.setSessionId(reqObj.getFotaSessionId());
							fotaUpdateImpl.setVersionId(versionId.getVersionId());
							flag = 1;
						}
						if(flag == 0)
						{
							fotaUpdateImpl.setFile_path("LATEST_FILE_DOES_NOT_EXIST");
						}				
					}
					else if(versionId.getVersionId().equalsIgnoreCase("UPDATE_NOT_REQUIRED")){
						fotaUpdateImpl.setSessionId(reqObj.getFotaSessionId());
						fotaUpdateImpl.setVersionId("UPDATE_NOT_REQUIRED");
						fotaUpdateImpl.setFile_path("LATEST_FILE_DOES_NOT_EXIST");
					}
				}
			else if(fotaUpdateImpl.getStatus().equalsIgnoreCase("DUPLICATE_SESSION_ID")){
					fotaUpdateImpl.setSessionId(reqObj.getFotaSessionId());
					fotaUpdateImpl.setVersionId("UPDATE_NOT_REQUIRED");
					fotaUpdateImpl.setFile_path("LATEST_FILE_DOES_NOT_EXIST");
				}
			}catch (Exception e)
			{
				fatalError.fatal("Exception :" + e);
			}
		finally
	    {
				if(sessionOpened){
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
				
			}
	          
	
		return fotaUpdateImpl;
	}

}
