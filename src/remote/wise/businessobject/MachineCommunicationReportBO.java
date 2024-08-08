package remote.wise.businessobject;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetEntity;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.implementation.MachineCommunicationReportImpl;
import remote.wise.service.implementation.MachineProfileImpl;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class MachineCommunicationReportBO {

	/*public static WiseLogger businessError = WiseLogger.getLogger("MachineCommunicationReportBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("MachineCommunicationReportBO:","fatalError");*/
	
	
	String serialNumber;
	String alertType;
	String dateTime;
	String FromDate;


	/**
	 * @return the dateTime
	 */
	public String getDateTime() {
		return dateTime;
	}
	/**
	 * @param dateTime the dateTime to set
	 */
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}


	String ToDate;
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
	 * @return the alertType
	 */
	public String getAlertType() {
		return alertType;
	}
	/**
	 * @param alertType the alertType to set
	 */
	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}
	/**
	 * @return the fromDate
	 */
	public String getFromDate() {
		return FromDate;
	}
	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(String fromDate) {
		FromDate = fromDate;
	}
	/**
	 * @return the toDate
	 */
	public String getToDate() {
		return ToDate;
	}
	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(String toDate) {
		ToDate = toDate;
	}

	
	public List<MachineCommunicationReportBO> getmachineCommunicationDetail(String fromDate,
			 String toDate)throws CustomFault {
    	Logger fLogger = FatalLoggerClass.logger;
    
		// TODO Auto-generated method stub
		List<MachineCommunicationReportBO> MachineReportBoDetails = new LinkedList<MachineCommunicationReportBO>();
		 Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		    session.beginTransaction();
		   try
		    {
		    	if(! (session.isOpen() ))
                {
                            session = HibernateUtil.getSessionFactory().getCurrentSession();
                            session.getTransaction().begin();
                } 
		    	Query query = session.createQuery("select a.serialNumber,a.eventGeneratedTime,b.eventTypeName from AssetEventEntity a,EventTypeEntity b where a.eventGeneratedTime>='"+fromDate+"%' and a.eventGeneratedTime<='"+toDate+"%' and a.eventTypeId=b.eventTypeId ");
				Iterator itr = query.list().iterator();
				Object[] result = null;
				while(itr.hasNext())
				{
					MachineCommunicationReportBO machineImpl=new MachineCommunicationReportBO();
					result = (Object[]) itr.next();
					AssetEntity a=(AssetEntity)result[0];
					String serialnumber=a.getSerial_number().getSerialNumber();
					machineImpl.setSerialNumber(serialnumber);
					Timestamp transTimestamp = (Timestamp)result[1];
					machineImpl.setDateTime(transTimestamp.toString());
					machineImpl.setAlertType((String)result[2]);
					MachineReportBoDetails.add(machineImpl);
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
		return MachineReportBoDetails;
	}
}
