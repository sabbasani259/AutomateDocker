package remote.wise.businessobject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.util.DateUtil;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class DetailedUsercommunicationReportBO {

	/*public static WiseLogger businessError = WiseLogger.getLogger("DetailedUsercommunicationReportBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("DetailedUsercommunicationReportBO:","fatalError");*/
	
	String serialNumber;
	String eventGeneratedTime;
	String alertDesc;
	
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * @return the eventGeneratedTime
	 */
	public String getEventGeneratedTime() {
		return eventGeneratedTime;
	}

	/**
	 * @param eventGeneratedTime the eventGeneratedTime to set
	 */
	public void setEventGeneratedTime(String eventGeneratedTime) {
		this.eventGeneratedTime = eventGeneratedTime;
	}

	/**
	 * @return the alertDesc
	 */
	public String getAlertDesc() {
		return alertDesc;
	}

	/**
	 * @param alertDesc the alertDesc to set
	 */
	public void setAlertDesc(String alertDesc) {
		this.alertDesc = alertDesc;
	}

	public List<DetailedUsercommunicationReportBO> detailedUsercommunicationList(
			String fromDate, String toDate, String contactId, String phoneNumber, String email,String sms) {
		// TODO Auto-generated method stub
		
		Logger fLogger = FatalLoggerClass.logger;
		List<DetailedUsercommunicationReportBO> detailedUsercommunicationReportListBO = new LinkedList<DetailedUsercommunicationReportBO>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try{
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			} 
			String basicQueryString = null;
			if(contactId==null)
			{
				Query query1 = session.createQuery("SELECT contact_id from ContactEntity where primary_mobile_number='"+phoneNumber+"'");
				Iterator itr1 = query1.list().iterator();
				while(itr1.hasNext())
				{
					contactId = (String)itr1.next();
				}
			}
			if(contactId!=null)
			{
			basicQueryString = "SELECT a.serialNumber,a.eventGeneratedTime,c.eventDescription FROM AssetEventEntity a,EventUsersEntity b,EventEntity c where a.eventId=c.eventId and a.assetEventId=b.assetEventId and a.eventGeneratedTime>='"+fromDate+"' and a.eventGeneratedTime<='"+toDate+"' and b.contactId='"+contactId+"'";
			}
			if(email!=null)
			{
				basicQueryString = basicQueryString +" and b.isEmail=1 ";
			}
			if(sms!=null)
			{
				basicQueryString = basicQueryString +" and b.isSms=1 ";
			}
			Query query = session.createQuery(basicQueryString);
			Iterator itr = query.list().iterator();
			Object[] result = null;
			while(itr.hasNext())
			{
				DetailedUsercommunicationReportBO communicationListBO=new DetailedUsercommunicationReportBO();
				result = (Object[]) itr.next();
				AssetEntity a=(AssetEntity)result[0];
				communicationListBO.setSerialNumber(a.getSerial_number().getSerialNumber());
				communicationListBO.setEventGeneratedTime(String.valueOf((Timestamp)result[1]));
				communicationListBO.setAlertDesc((String)result[2]);
				detailedUsercommunicationReportListBO.add(communicationListBO);
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
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
		return detailedUsercommunicationReportListBO;
	}

}
