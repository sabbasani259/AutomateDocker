package remote.wise.service.implementation;

import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.businessobject.ServiceClouserBO;
import remote.wise.businessobject.ServiceDetailsBO;

public class ServiceClosureImpl {
	
	public HashMap<String, Object> getServiceCloserDetails(String assetEventId)
	{
		HashMap<String, Object> result=null;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		if(assetEventId==null)
		{
			bLogger.error("ServiceClouserRESTService:getSubscriptionDetails:Mandatory parameter assetID is null");
			return null;
		}
		result = new ServiceClouserBO().getServiceCloserDetails(assetEventId);
		
		return result;
	}
	
	public String setServiceCloserDetails(HashMap<String, String> inputObj)
	{
		String output;
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger= BusinessErrorLoggerClass.logger;
		
		if(inputObj.get("assetEventId")==null)
		{
			bLogger.error("ServiceDetailsBO:setServiceCloserDetails:Mandatory parameter assetEventId is null");
			return "FAILURE";
		}
		
    	if(inputObj.get("completedBy")==null)
		{
			bLogger.error("ServiceDetailsBO:setServiceCloserDetails:Mandatory parameter completedBy is null");
			return "FAILURE";
		}
    	
    	if(inputObj.get("engineHrs")==null)
		{
			bLogger.error("ServiceDetailsBO:setServiceCloserDetails:Mandatory parameter engineHrs is null");
			return "FAILURE";
		}
    	if(inputObj.get("comments")==null)
		{
			bLogger.error("ServiceDetailsBO:setServiceCloserDetails:Mandatory parameter comments is null");
			return "FAILURE";
		}
    	if(inputObj.get("serviceDate")==null)
		{
			bLogger.error("ServiceDetailsBO:setServiceCloserDetails:Mandatory parameter serviceDate is null");
			return "FAILURE";
		}
		
		//output = new ServiceDetailsBO().setServiceCloserDetails( inputObj);
    	output = new ServiceClouserBO().setServiceCloserDetails(inputObj);

		if(output==null || output.equalsIgnoreCase("FAILURE")||output.contains("FAILURE"))
		{
			fLogger.fatal("ServiceClosureImpl:setServiceCloserDetails Class:Status:FAILURE");
			return "FAILURE";
		}
		
		
		return output;
	}
	
}

