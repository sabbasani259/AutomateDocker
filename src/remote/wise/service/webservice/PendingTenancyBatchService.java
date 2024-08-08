package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.PendingTenancyBatchImpl;

@Path("/PendingTenancyBatchService")
public class PendingTenancyBatchService 
{
	@GET
	@Path("createPendingTenancies")
	@Produces("text/plain")
	public String createPendingTenancies( @QueryParam("accountId") int accountId)
	{
		String status="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		
		iLogger.info("PendingTenancyBatchService:createPendingTenancies:WebService Input:accountId:"+accountId);
		new PendingTenancyBatchImpl(accountId);		
		iLogger.info("PendingTenancyBatchService:createPendingTenancies:WebService Output:"+status);
		
		return status;
	}
}
