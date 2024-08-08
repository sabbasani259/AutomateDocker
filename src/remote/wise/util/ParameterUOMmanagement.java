package remote.wise.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.UomMasterEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;

public class ParameterUOMmanagement 
{
	public String getParameterValue(String paramValue)
	{
		String convertedValueString = null;
		double convertedValue =0;
		String query = null;
		
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger fatalError = FatalLoggerClass.logger;
        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
        try
        {
			String[] parValuesArray = paramValue.split(" unit ");
			String[] splitparValue= null;
			String[] splitparUnit=null;
			
		
			if(parValuesArray[0].contains("-"))
			{
				splitparValue = parValuesArray[0].split("-");
				splitparUnit = parValuesArray[1].split("-");
			}
			else
			{
				splitparValue= new String[]{parValuesArray[0]};
				splitparUnit = new String[]{parValuesArray[1]};
			}
		
			for(int y=0; y<splitparValue.length; y++)
			{
				double receivedValue = Double.parseDouble(splitparValue[y].trim());
				String receivedUnit = splitparUnit[y].trim();
			
				query = "from UomMasterEntity where receivedUnit='"+receivedUnit+"'";
				Query q6 = session.createQuery(query);
				Iterator itr6 = q6.list().iterator();
				double conversionFactor=0;
				while(itr6.hasNext())
				{
					UomMasterEntity uomMaster = (UomMasterEntity)itr6.next();
					conversionFactor = uomMaster.getConversionFactor();
				}
				convertedValue = convertedValue+(receivedValue*conversionFactor);
			}
			
			convertedValueString = Double.toString(convertedValue);
        }
        
        catch(Exception e)
		{
        	e.printStackTrace();
        	
			//DF20160401 - Rajani Nagaraju - Adding the StackTrace with extra information for analysis
        	//Exception Caught: Exception :org.hibernate.exception.JDBCConnectionException: could not execute query
        	fatalError.fatal("ParameterUOMmanagement : Received Parameter Value:"+paramValue+": convertedValue:"+convertedValue+" :Query:"+query+" :Exception :"+e);
			Writer result = new StringWriter();
    	    PrintWriter printWriter = new PrintWriter(result);
    	    e.printStackTrace(printWriter);
    	    String err = result.toString();
    	    fatalError.fatal("ParameterUOMmanagement : Received Parameter Value:"+paramValue+": convertedValue:"+convertedValue+" :Query:"+query+":Exception trace: "+err);
    	    try 
    	    {
    	    	printWriter.close();
        	    result.close();
			} 
    	    
    	    catch (IOException e1) 
    	    {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
        
        finally
        {
        	//DF20160401 - Rajani Nagaraju - Check for session open. Exception Caught:  No operations allowed after connection closed
        	if(session.isOpen())  
        		if(session.getTransaction().isActive())
             	{
                    session.getTransaction().commit();
             	}
              
              if(session.isOpen())
              {
            	//DF20160401- Rajani Nagaraju - To Handle the Exception: org.hibernate.HibernateException: flush is not valid without active transaction
					if( (session.getTransaction()!=null ) &&  (session.getTransaction().isActive()) )
						session.flush();
                    
					session.close();
              }
              
        }
			
		return convertedValueString;
	}
}
