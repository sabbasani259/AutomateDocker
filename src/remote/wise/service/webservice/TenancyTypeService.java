package remote.wise.service.webservice;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.TenancyTypeImpl;

@Path("/TenancyTypeService")
public class TenancyTypeService 
{
	@POST
	@Path("getTenancyType")
	public String getTenancyType( int tenancyId)
	{
		String tenancyType="";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		
		long startTime = System.currentTimeMillis();
		iLogger.info("TenancyTypeService:getTenancyType:WebService Input:"+tenancyId);
		
		if(tenancyId==0)
		{
			fLogger.fatal("TenancyTypeService:getTenancyType:Mandatory parameter tenancyId not received in the input");
		}
		else
		{
			tenancyType = new TenancyTypeImpl().getTenancyType(tenancyId);
		}
		long endTime = System.currentTimeMillis();
		iLogger.info("TenancyTypeService:getTenancyType:WebService Output: tenancyType:"+tenancyType+"; Total time in ms:"+(endTime-startTime));
		
		return tenancyType;
	}
}
