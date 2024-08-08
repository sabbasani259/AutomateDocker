package remote.wise.businessobject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.FotaUpdateHistoryEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.FotaFinalAlertImpl;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class FotaFinalAlertBO {
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("FotaFinalAlertBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("FotaFinalAlertBO:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("FotaFinalAlertBO:","info");*/
	
	
	public FotaFinalAlertImpl updateStatus2(String imeiNo,String sessionId,String status){
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		
		FotaFinalAlertImpl fotaFinalAlertImpl = new FotaFinalAlertImpl();
		String finalStatus = null,versionId=null,fileName=null;
		if(status!=null){
			if(status.equalsIgnoreCase("0C8")){
				finalStatus = "SUCCESS";
			}
			else{
				//finalStatus = "FAILURE";
				finalStatus = "FAILURE-"+status;

			}
			iLogger.info("finalStatus   : "+finalStatus);

			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try{
				String selectFileNameQuery = "SELECT fve.versionId,fve.file_path" +
				" FROM FotaAuthenticationEntity fae,FotaVersionEntity fve" +
				" WHERE fae.imeiNo ='"+imeiNo+"' AND fae.status =1 AND fae.versionId =fve.versionId";
				Query fileNameQuery	= session.createQuery(selectFileNameQuery);
				Iterator iterFileName =fileNameQuery.list().iterator();
				Object[] result = null;
				while(iterFileName.hasNext()){
					result = (Object[])iterFileName.next();
					if(result[0]!=null){
						versionId = (String)result[0];
					}
					if(result[1]!=null){
						fileName = (String)result[1];
					}
				}
				iLogger.info("version ID  : "+versionId);
				iLogger.info("file Name   : "+fileName);

				if(versionId != null && fileName!=null){
					Date currentDate = new java.util.Date();
					Timestamp currentTime = new Timestamp(currentDate.getTime());

					//insert into history table

					FotaUpdateHistoryEntity history = new FotaUpdateHistoryEntity();
					history.setImeiNo(imeiNo);
					history.setVersionId(versionId);
					history.setFileName(fileName);
					history.setSessionId(sessionId);
					history.setUpdate_date(currentTime);
					history.setStatus(finalStatus);
					//history.save();
					session.save(history);

					fotaFinalAlertImpl.setSessionId(sessionId);
					fotaFinalAlertImpl.setStatus("STATUS_UPDATED");

				}
				else{//could not fetch version id and filename for this imei
					fotaFinalAlertImpl.setSessionId(sessionId);
					fotaFinalAlertImpl.setStatus("STATUS_UPDATE_FAILED");	
				}

			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally
			{
				if(session.getTransaction().isActive())
				{
					try
					{
						session.getTransaction().commit();
					}
					catch(Exception e)
					{
						fLogger.error("Error in inserting FOTAUpdateHistoryEntity: "+e);
					}
				}

				if(session.isOpen())
				{
					session.flush();
					session.close();
				}

			}
		}
		return fotaFinalAlertImpl;
	}
	public FotaFinalAlertImpl updateStatus(String sessionId,String status)
	{
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		
		FotaFinalAlertImpl fotaFinalAlertImpl = new FotaFinalAlertImpl();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try
		{
			//			int flag =0;
			int status_bit = 1;
			String basicUpdateQuery = " UPDATE FotaAuthenticationEntity a ";
			String basicValuesQuery1 = " SET  a.status =  " +status_bit;
			String basicWhereQuery1 = " WHERE a.sessionId ='"+sessionId+"'";
			String finalQuery =basicUpdateQuery+ basicValuesQuery1+ basicWhereQuery1 ;
			Query q1 = session.createQuery(finalQuery);
			int count = q1.executeUpdate();

			if(count>0)//count holds no. of rows get updated in DB
			{
				iLogger.info("sessionID   "+sessionId);
				fotaFinalAlertImpl.setSessionId(sessionId);
				fotaFinalAlertImpl.setStatus("STATUS_UPDATED");
			}
			else{
				fotaFinalAlertImpl.setSessionId(sessionId);
				fotaFinalAlertImpl.setStatus("STATUS_UPDATE_FAILED");	
			}
		}catch (Exception e)
		{
			fLogger.fatal("Exception :" + e);
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
		return fotaFinalAlertImpl;
	}

}
