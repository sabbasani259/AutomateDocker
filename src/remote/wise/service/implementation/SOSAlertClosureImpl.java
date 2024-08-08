/**
 * 
 */
package remote.wise.service.implementation;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetEventEntity;
import remote.wise.businessobject.AlertDetailsRESTBO;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;
import remote.wise.util.IstGmtTimeConversion;

/**
 * @author roopn5
 *
 */
public class SOSAlertClosureImpl {

	public String closeSOSAlerts(){

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String response = "SUCCESS";
		Session session = HibernateUtil.getSessionFactory().openSession();
		try{

			session.beginTransaction();
			AssetEventEntity assetEvent = null;
			
			Date currTime = new Date();
			Timestamp currTS = new Timestamp(currTime.getTime());
			IstGmtTimeConversion timeConvObj = new IstGmtTimeConversion();
			Timestamp gmtTS = timeConvObj.convertIstToGmt(currTS);

			Query query = session.createQuery(" from AssetEventEntity ae  where ae.eventId=34 and ae.activeStatus=1 and timestampdiff(HOUR,convert_tz(ae.eventGeneratedTime,'+00:00','+05:30'),NOW()) >= 24");
			Iterator queryItr = query.list().iterator();

			while(queryItr.hasNext()){
				assetEvent = (AssetEventEntity) queryItr.next();
				assetEvent.setEventClosedTime(gmtTS);
				assetEvent.setActiveStatus(0);
				assetEvent.setCreated_timestamp(currTS);
				session.update(assetEvent);
				
			}

		}catch(Exception e){

			response = "FAILURE";
			fLogger.error("Exception Caught: "+e.getMessage());

		}finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}if(session.isOpen()){
				session.flush();
				session.close();
			}
		}

		return response;
	}
}
