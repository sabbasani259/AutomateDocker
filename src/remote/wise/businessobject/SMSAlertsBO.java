package remote.wise.businessobject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.SmsEventDetailsEntity;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.implementation.SMSAlertsImpl;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class SMSAlertsBO 
{
	//public static WiseLogger fatalError = WiseLogger.getLogger("SMSAlertsBO:","fatalError");
	
	
	public List<SMSAlertsImpl> getSMSAlerts(int isSummary, String serialNumber, String fromDate, String toDate)
	{
		
    	Logger fLogger = FatalLoggerClass.logger;
    	
		List<SMSAlertsImpl> implObjList = new LinkedList<SMSAlertsImpl>();
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
		
		try
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			String queryString = " select a,  " +
					" CAST(GROUP_CONCAT(b.eventId) As string ) as eventIds, "+
					" CAST(GROUP_CONCAT(b.status) As string ) as eventStatus, " +
					" a.eventGeneratedTime "+
				//	" from SmsEventDetailsEntity a, SmsEventEntity b " +
					" from SmsEventEntity b right outer join b.smsEventId a " +
					" where a.smsEventId !=0 ";
			
			if( ! ((serialNumber==null)|| (serialNumber.trim().length()==0) ) )
			{
				//Get the Serial Number if machine number is input
				if(serialNumber.length()==7)
				{
					Query assetQ = session.createQuery(" from AssetEntity where machineNumber='"+serialNumber+"'");
					Iterator assetItr = assetQ.list().iterator();
					while(assetItr.hasNext())
					{
						AssetEntity asset = (AssetEntity)assetItr.next();
						serialNumber= asset.getSerial_number().getSerialNumber();
					}
				}
				
				queryString = queryString+" and a.serialNumber='"+serialNumber+"' ";
			}
			
			if(fromDate!=null && toDate!=null)
			{
				fromDate = dateFormat.format( dateFormat.parse(fromDate));
				toDate = dateFormat.format( dateFormat.parse(toDate));
				
				queryString = queryString+" and a.eventGeneratedTime >='"+fromDate+" 00:00:00' and a.eventGeneratedTime<='"+toDate+" 23:59:00'";
			}
			
			if(isSummary==1)
				queryString = queryString+ " and a.eventGeneratedTime in ( select max(c.eventGeneratedTime) from " +
						" SmsEventDetailsEntity c where a.serialNumber=c.serialNumber group by c.serialNumber) ";
				
			queryString = queryString+" group by a.smsEventId order by a.eventGeneratedTime desc ";
			
			if(isSummary==0)
				queryString = queryString+" limit 20";
			
				
			
			Query smsAlertsQuery = session.createQuery(queryString); 
			Iterator smsAlertsItr = smsAlertsQuery.list().iterator();
			
			List<String> eventIdList = new LinkedList<String>();
			List<String> eventValueList = new LinkedList<String>();
			
			Object[] result = null;
			while(smsAlertsItr.hasNext())
			{
				result = (Object[]) smsAlertsItr.next();
				
				SmsEventDetailsEntity smsEventDetail = (SmsEventDetailsEntity) result[0];
				
				if(result[1]!=null)
					eventIdList = Arrays.asList(result[1].toString().split(","));
				else
					eventIdList = new LinkedList<String>();
				
				if(result[2]!=null)
					eventValueList = Arrays.asList(result[2].toString().split(","));
				else
					eventValueList = new LinkedList<String>();
				
				HashMap<String,String> eventIdValueMap = new HashMap<String,String>();
				for(int i=0; i<eventIdList.size();i++)
				{
					eventIdValueMap.put(eventIdList.get(i), eventValueList.get(i));
				}
				
				Timestamp smsReceivedTime = (Timestamp)result[3];
				
				SMSAlertsImpl implObj = new SMSAlertsImpl();
				if(eventIdValueMap.get("11")!=null)
					implObj.setBlockedAirFilterStatus(Integer.parseInt(eventIdValueMap.get("11")));
				implObj.setCmhr(smsEventDetail.getCmhr());
				
				String gprsFailureReason = null;
				if(smsEventDetail.getGprsNotAvailable()==1)
					gprsFailureReason="GPRS Not Available";
				else if (smsEventDetail.getGprsServerCommFailed()==1)
					gprsFailureReason="GPRS Communication Failed";
				else if(smsEventDetail.getGrpsServiceNotAllowed()==1)
					gprsFailureReason="GPRS Service not allowed";
				implObj.setFailureReason(gprsFailureReason);
				
				if(eventIdValueMap.get("9")!=null)
					implObj.setHighCoolantTempStatus(Integer.parseInt(eventIdValueMap.get("9")));
				if(eventIdValueMap.get("7")!=null)
					implObj.setLowEngineOilPressureStatus(Integer.parseInt(eventIdValueMap.get("7")));
				implObj.setSerialNumber(smsEventDetail.getSerialNumber());
				implObj.setSmsReceivedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(smsReceivedTime));
				if(eventIdValueMap.get("21")!=null)
					implObj.setTowawayStatus(Integer.parseInt(eventIdValueMap.get("21")));
				if(eventIdValueMap.get("10")!=null)
					implObj.setWaterInFuelStatus(Integer.parseInt(eventIdValueMap.get("10")));
				
							
				implObjList.add(implObj);
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
        
		return implObjList;
	}
}
