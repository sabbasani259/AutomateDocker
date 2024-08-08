package remote.wise.service.implementation;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.TenancyBO;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.ZonalInformationReqContract;
//import remote.wise.util.WiseLogger;

public class ZonalInformationImpl 
{
	//public static WiseLogger businessError = WiseLogger.getLogger("ZonalInformationImpl:","businessError");
	
	public String setZonalInformations(String zonalName,String zonalCode, String messageId)
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		String result="SUCCESS-Record Processed";
		
		if(zonalName==null || zonalName.trim()==null)
		{
			result = "FAILURE-Mandatory Parameter Zonal Name is NULL";
			bLogger.error("EA Processing: ZonalInformation: "+messageId+" : Mandatory Parameter Zonal Name is NULL");
			return result;
		}
		
		if(zonalCode==null || zonalCode.trim()==null)
		{
			result = "FAILURE-Mandatory Parameter Zonal Code is NULL";
			bLogger.error("EA Processing: ZonalInformation: "+messageId+" : Mandatory Parameter Zonal Code is NULL");
			return result;
		}
		
		
		TenancyBO tenancyBO = new TenancyBO();
		result = tenancyBO.setZonalDetails(zonalName, zonalCode,messageId);
		
		return result;
		
	}

}
