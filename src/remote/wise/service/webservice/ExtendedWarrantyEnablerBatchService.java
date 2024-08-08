package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ExtendedWarrantyBatchServiceImpl;

@Path("/ExtendedWarrantyEnablerBatchService")
public class ExtendedWarrantyEnablerBatchService 
{
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/updateExtendedWarrantyServices")
	//CR212 - 20210713 - Rajani Nagaraju - Extended Warranty Changes
	public String updateExtendedWarrantyServices(@QueryParam("serialNumber")String serialNumber)
	{
		String status="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		
		iLogger.info("ExtendedWarrantyEnablerBatchService:updateExtendedWarrantyServices:WebService Input:serialNumber:"+serialNumber);
		new ExtendedWarrantyBatchServiceImpl(serialNumber);
		iLogger.info("ExtendedWarrantyEnablerBatchService:updateExtendedWarrantyServices:WebService Output:"+status);
			
		return status;
	}
}
