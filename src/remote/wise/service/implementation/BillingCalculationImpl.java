package remote.wise.service.implementation;

import java.util.Date;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.EventDetailsBO;
import remote.wise.businessobject.MachineBillingReportBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;

public class BillingCalculationImpl 
{
	//public static WiseLogger businessError = WiseLogger.getLogger("BillingCalculationImpl:","businessError");
	
	//DF20140403 - Rajani Nagaraju - Adding the capability to update billing data for previous months
	public String setBillingCalculationImpl(int month, int year) 
	{
		MachineBillingReportBO MachineBillingReportObj = new MachineBillingReportBO();
		String status="SUCCESS";
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		try
		{
			//DF20140403 - Rajani Nagaraju - Validating input parameters
			if( (month==0 && year!=0) )
			{
				throw new CustomFault("Month is provided but Year i/p is empty");
			}
			
			if( (year==0 && month!=0) )
			{
				throw new CustomFault("Year is provided but Month i/p is empty");
			}
			
			if(month>12 || month <0)
			{
				throw new CustomFault("Invalid month input");
			}
			
			//If Month and Year is not provided, by default it should be the previous Month
			if(month==0 && year==0)
			{
				Date currentDate = new Date();
				
				DateUtil dateUtil1 = new DateUtil();
				DateUtil thisDate =dateUtil1.getCurrentDateUtility(currentDate);
				DateUtil dateUtil2 = new DateUtil();
				DateUtil prevDate = dateUtil2.getPreviousDateUtility(currentDate);
				month =  prevDate.getMonth();
				if(thisDate.getMonth()==1)
				{
					year = prevDate.getYear();
				}
				else
				{
					year = prevDate.getCurrentYear();
				}
			}
			
			String monthInString =""+month;
			if(monthInString.length()==1)
				monthInString = "0"+monthInString;
			String yearInString =""+year;
			status = MachineBillingReportObj.setBillingCalculation(monthInString,yearInString);
		}
		
		catch(CustomFault e)
	    {
	       status = "FAILURE";
	       bLogger.error("Custom Fault: "+ e.getFaultInfo());
	    }
		
		return status;
	}

}
