package remote.wise.businessobject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.FaultDetails;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.service.datacontract.EAfaultMsgDetailsReqContract;
import remote.wise.service.implementation.EAerrorDetailsImpl;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class EnterpriseApplicationDetailsBO 
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger fatalError = WiseLogger.getLogger("EnterpriseApplicationDetailsBO:","fatalError");*/

	public List<EAerrorDetailsImpl> getFaultMessageDetails(String reprocessTimeStamp, String reprocessJobCode, String messageId)
	{
		List<EAerrorDetailsImpl> responseList = new LinkedList<EAerrorDetailsImpl>();
		
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger fLogger = FatalLoggerClass.logger;
		
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
        try
        {
        	String basicQuery = "select a , b.sequence from FaultDetails a, JobDetails b where a.reprocessJobCode = b.reprocessJobCode " +
        			" and a.process = b.process";
        	
        	if(reprocessJobCode!=null)
        		basicQuery = basicQuery+ " and a.reprocessJobCode='"+reprocessJobCode+"'";
        	
        	if(messageId!=null)
        		basicQuery = basicQuery+" and a.messageId='"+messageId+"'";
        	
        	if(reprocessTimeStamp!=null)
        	{
        		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        		Date reprocessDate = formatter.parse(reprocessTimeStamp);
        		String reprocessDateInString = formatter.format(reprocessDate);
        		
        		basicQuery= basicQuery + " and a.reprocessTimeStamp like '"+reprocessDateInString+"%'";
        	}
        	
        	basicQuery = basicQuery +" order by a.reprocessJobCode, a.process, b.sequence, a.reprocessTimeStamp desc ";
        	
        	Query query = session.createQuery(basicQuery);
        	Iterator itr = query.list().iterator();
        	Object[] result = null;
        	
        	while(itr.hasNext())
        	{
        		result = (Object[]) itr.next();
        		FaultDetails faultDetailsObj = (FaultDetails) result[0];
        		int sequence = (Integer) result[1];
        		
        		EAerrorDetailsImpl implObj = new EAerrorDetailsImpl();
        		implObj.setFailureCounter(faultDetailsObj.getFailureCounter());
        		implObj.setFileName(faultDetailsObj.getFileName());
        		implObj.setMessageId(faultDetailsObj.getMessageId());
        		implObj.setMessageString(faultDetailsObj.getMessageString());
        		implObj.setProcess(faultDetailsObj.getProcess());
        		implObj.setReprocessJobCode(faultDetailsObj.getReprocessJobCode());
        		implObj.setReprocessTimeStamp(faultDetailsObj.getReprocessTimeStamp().toString());
        		implObj.setSequence(sequence);
        		
        		responseList.add(implObj);
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
		
		return responseList;
	}
	
	
	public String setFaultReprocessDate(List<EAfaultMsgDetailsReqContract> reqObj)
	{
		String status = "SUCCESS";
		
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger fLogger = FatalLoggerClass.logger;
		
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
        try
        {
        	for(int i=0; i<reqObj.size(); i++)
        	{
        		if(reqObj.get(i).getMessageId()==null)
        			continue;
        		
        		else
        		{
        	      	Query query = session.createQuery("update FaultDetails set reprocessTimeStamp='"+reqObj.get(i).getReprocessTimeStamp()+"' where " +
        	      			" messageId='"+reqObj.get(i).getMessageId()+"'");
        	      	query.executeUpdate();
        		}
        	
        	}
        }
        
        catch(Exception e)
		{
        	status="FAILURE";
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
        
		return status;
	}
}
