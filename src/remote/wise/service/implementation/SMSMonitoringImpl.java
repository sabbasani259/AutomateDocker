package remote.wise.service.implementation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.EventEntity;
import remote.wise.businessentity.SmsLogDetailsEntity;
import remote.wise.service.datacontract.SMSMonitoringRespContract;
import remote.wise.util.HibernateUtil;

public class SMSMonitoringImpl 
{
	public List<SMSMonitoringRespContract> getSMSDetails(String mobileNumber)
	{
		List<SMSMonitoringRespContract> responseList = new LinkedList<SMSMonitoringRespContract>();
		
		try
		{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			try
			{
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String queryString = " select a, b.eventId from SmsLogDetailsEntity a, AssetEventEntity b " +
						" where a.assetEventId=b.assetEventId ";
				
				if(mobileNumber!=null && mobileNumber.trim().length()> 0)
					queryString=queryString+" and a.mobileNumber='"+mobileNumber+"'";
				queryString = queryString+ " order by a.smsSentTime desc ";
				Query query = session.createQuery(queryString);
				query.setMaxResults(5);
				Object[] result=null;
				
				
				Iterator itr = query.list().iterator();
				while(itr.hasNext())
				{
					result = (Object[])itr.next();
					
					SmsLogDetailsEntity smsLogDetails = (SmsLogDetailsEntity)result[0];
					EventEntity event = (EventEntity) result[1];
					
					SMSMonitoringRespContract response = new SMSMonitoringRespContract();
					response.setEventName(event.getEventName());
					response.setMobileNumber(smsLogDetails.getMobileNumber());
					response.setResponseId(smsLogDetails.getResponseId());
					response.setSerialNumber(smsLogDetails.getSerialNumber());
					response.setSmsSentTimestamp(format.format(smsLogDetails.getSmsSentTime()));
					
					responseList.add(response);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
			finally
	
			{
				if (session.getTransaction().isActive())
				{
					session.getTransaction().commit();
				}
				if (session.isOpen()) 
				{
					session.flush();
					session.close();
				}
			}
		
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return responseList;
	}
}
