package remote.wise.businessobject;

import java.util.Iterator;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.SmsLogDetailsEntity;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class MachineSMSReportBO {
	
/*	public static WiseLogger businessError = WiseLogger.getLogger("MachineSMSReportBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("MachineSMSReportBO:","fatalError");*/
   
	String fromDate;
    String serialNumber;
    String toDate;
    String SMSCount;
    


	/**
 * @return the sMSCount
 */
public String getSMSCount() {
	return SMSCount;
}


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
 * @param sMSCount the sMSCount to set
 */
public void setSMSCount(String sMSCount) {
	SMSCount = sMSCount;
}


	/**
 * @return the fromDate
 */
public String getFromDate() {
	return fromDate;
}


/**
 * @param fromDate the fromDate to set
 */
public void setFromDate(String fromDate) {
	this.fromDate = fromDate;
}



/**
 * @return the toDate
 */
public String getToDate() {
	return toDate;
}


/**
 * @param toDate the toDate to set
 */
public void setToDate(String toDate) {
	this.toDate = toDate;
}


/**
 * @return the count
 */




	public List<MachineSMSReportBO> getMachineSMSCount(String fromDate,
			 String toDate)throws CustomFault {
		// TODO Auto-generated method stub
    	Logger fLogger = FatalLoggerClass.logger;
    	
		List<MachineSMSReportBO> MachineSMSReportBoDetails = new LinkedList<MachineSMSReportBO>();
		 Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		    session.beginTransaction();
		    Long SmsLogDetail = 0l;
		    try
		    {
		    	if(! (session.isOpen() ))
                {
                            session = HibernateUtil.getSessionFactory().getCurrentSession();
                            session.getTransaction().begin();
                } 
		    	Query query = session.createQuery("select serialNumber, count(*) from SmsLogDetailsEntity where smsSentTime >='"+fromDate+"' and smsSentTime<='"+toDate+"' group by serialNumber");
				Iterator itr = query.list().iterator();
				Object[] result = null;
				while(itr.hasNext())
				{
					MachineSMSReportBO machineSMSReportBO=new MachineSMSReportBO();
					result = (Object[]) itr.next();
					SmsLogDetail = (Long) result[1];
					machineSMSReportBO.setSMSCount(Long.toString(SmsLogDetail));
					machineSMSReportBO.setSerialNumber((String)result[0]);
					MachineSMSReportBoDetails.add(machineSMSReportBO);
				}

		    }
			
			catch(Exception e)
			{
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
		return MachineSMSReportBoDetails;
	}

}
