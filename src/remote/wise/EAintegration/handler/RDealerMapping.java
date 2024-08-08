package remote.wise.EAintegration.handler;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;


import remote.wise.EAintegration.EAclient.DealerMappingClient;
import remote.wise.EAintegration.dataContract.DealerMappingInputContract;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

public class RDealerMapping 
{
	public void reprocessDealerMapping()
	{
		Logger fLogger = FatalLoggerClass.logger;
        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Logger iLogger = InfoLoggerClass.logger;
      //DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
      if(session.getTransaction().isActive() && session.isDirty())
      {
         	iLogger.info("Opening a new session");
         	session = HibernateUtil.getSessionFactory().openSession();
      }
        session.beginTransaction();
        String messgId=null;
        
        try
        {
        	String eccCode =null;
        	String crmCode = null;
        	String llCode=null;
        	String dealerName=null;
        	String response = null;
        	ErrorMessageHandler messageHandlerObj = new ErrorMessageHandler();
        	List<DealerMappingInputContract> inputContractList = new LinkedList<DealerMappingInputContract>();
        	
        	
        	//get the currentDate
        	Date currentDate = new Date();
        	Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
        	
        	//List all the messages scheduled to be reprocessed for the current time - Reprocess the same in the order of defined sequence of processes
        	Query query = session.createQuery("select a.messageId, a.messageString, a.fileName, a.process," +
        			" a.reprocessJobCode from FaultDetails a, JobDetails b where a.reprocessJobCode = b.reprocessJobCode and " +
        			" a.process = b.process and a.reprocessJobCode = 'RDealerMapping' and a.reprocessTimeStamp <= '"+currentTimestamp+"' " +
        					" order by b.process, b.sequence, a.reprocessTimeStamp ");
        	
        	Iterator itr = query.list().iterator();
        	Object[] result = null;
        	
        	while(itr.hasNext())
        	{
        		result = (Object[]) itr.next();
        		
        		eccCode =null;
        		crmCode = null;
        		llCode = null;
        		dealerName = null;
        		
        		String messageString = result[1].toString();
        		String[] messageSplit = messageString.split("\\|");
        		int paramSize =messageSplit.length;
        		
        		if(paramSize>0)
        			eccCode = messageSplit[0];
        		
        		if(paramSize>1)
        			crmCode = messageSplit[1];
        		
        		if(paramSize>2)
        			llCode = messageSplit[2];
        		
        		if(paramSize>3)
        			dealerName = messageSplit[3];
        		
        		//Fill the input contract Object
        		DealerMappingInputContract inputContractObj = new DealerMappingInputContract();
        		inputContractObj.setEccCode(eccCode);
        		inputContractObj.setCrmCode(crmCode);
        		inputContractObj.setLlCode(llCode);
        		inputContractObj.setDealerName(dealerName);
        		
        		if(result[2]!=null)
        			inputContractObj.setFileRef(result[2].toString());
        		if(result[0]!=null)
        		{
        			inputContractObj.setMessageId(result[0].toString());
        			messgId=result[0].toString();
        		}
        		
        		if(result[3]!=null)
        			inputContractObj.setProcess(result[3].toString());
        		if(result[4]!=null)
        			inputContractObj.setReprocessJobCode(result[4].toString());
        		
        		inputContractList.add(inputContractObj);
        		
        	}
        	
        	for(int i=0; i<inputContractList.size(); i++)
        	{
        		DealerMappingClient clientObj = new DealerMappingClient();
        		response = clientObj.invokeDealerMappingService(inputContractList.get(i));
        		
        		
        		/*if(response.equalsIgnoreCase("FAILURE"))
        		{
        			messageHandlerObj.handleErrorMessages(result[0].toString(), result[1].toString(), result[2].toString(), result[3].toString(), result[4].toString());
        		}*/
        		
        		//DF20140715 - Rajani Nagaraju - Handle Robust Logging
        		if(response.split("-").length>1)
        		{
        			response = response.split("-")[0].trim();
        					
        		}
        		
        		if(response.equalsIgnoreCase("SUCCESS"))
        		{
        			messageHandlerObj.deleteErrorMessage(inputContractList.get(i).getMessageId());
        		}
        	}
        	
        	  /*if(! (session.isOpen() ))
              {
                          session = HibernateUtil.getSessionFactory().getCurrentSession();
                          session.getTransaction().begin();
              }*/

        }
        
        catch(Exception e)
		{
        	fLogger.fatal("EA ReProcessing: RDealerMapping: "+messgId+ " Fatal Exception :"+e);
		}
        
        finally
        {
        	//DF20150508 - Rajani Nagaraju - Addig try catch around commit
			try
			{
				if(session.isOpen())
					if(session.getTransaction().isActive())
					{
						session.getTransaction().commit();
					}
			}
			catch(Exception e2)
			{
				fLogger.fatal("Exception :"+e2);
			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}
              
        }
	}
}
