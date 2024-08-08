package remote.wise.service.implementation;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.TenancyBO;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
//import remote.wise.util.WiseLogger;

public class DealerMappingImpl 
{
	/*public static WiseLogger businessError = WiseLogger.getLogger("DealerMappingImpl:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("DealerMappingImpl:","fatalError");*/
	
	public String setDealerMappingDetails(String eccCode, String crmCode, String llCode, String dealerName, String messageId)
	{
		String status ="SUCCESS-Record Processed";
		Logger bLogger = BusinessErrorLoggerClass.logger;
		//Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
		//Remove all spaces
		if(llCode!=null)
			llCode=llCode.replaceAll("\\s","");
		if(eccCode!=null)
			eccCode=eccCode.replaceAll("\\s","");
		if(crmCode!=null)
			crmCode=crmCode.replaceAll("\\s","");
		
		//Check for the Mandatory parameters
		//1. LL Code is a mandatory parameter
		if(llCode==null || llCode.length()==0)
		{
			status = "FAILURE-Mandatory Parameter LL Code is NULL";
			bLogger.error("EA Processing: DealerMapping: "+messageId+" : Mandatory Parameter LL Code is NULL");
			return status;
		}
		
		//2.Either ECC Code or CRM Code has to be there - Both cannot be null
		if ( (eccCode==null || eccCode.length()==0) && (crmCode==null || crmCode.length()==0) )
		{
			status = "FAILURE-Both ECC Code and CRM Code cannot be NULL";
			bLogger.error("EA Processing: DealerMapping: "+messageId+" : Both ECC Code and CRM Code cannot be NULL");
			return status;
		}
		
		try
		{
			status = new TenancyBO().mapDealerCodes(eccCode,crmCode,llCode,dealerName,messageId);
		}
		
		catch(Exception e)
		{
			status = "FAILURE-"+e.getMessage();
			fLogger.error("EA Processing: DealerMapping: "+messageId+" : "+e.getMessage());
		}
		
		return status;
	}
}
