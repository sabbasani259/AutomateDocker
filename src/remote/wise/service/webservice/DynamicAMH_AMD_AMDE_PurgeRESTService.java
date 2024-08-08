/**
 * 
 */
package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.DynamicAMH_AMD_AMDE_TableImpl;

/**
 * @author roopn5
 *
 */
@Path("/DynamicAMH_AMD_AMDE_PurgeRESTService")
public class DynamicAMH_AMD_AMDE_PurgeRESTService {
	
	@GET
	@Path("purgeTable")
	@Produces("text/plain")
	public String purgeTable(@QueryParam("weekNo") int weekNo,@QueryParam("year") int year) 
	{
		String purgeStatus="SUCCESS";
		
		Logger iLogger = InfoLoggerClass.logger;

		long startTime = System.currentTimeMillis();
		
		
		purgeStatus=new DynamicAMH_AMD_AMDE_TableImpl().purgeTable(weekNo,year);
		
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:DynamicAMH_AMD_AMDE_PurgeRESTService~executionTime:"+(endTime - startTime)+"~"+""+"~"+purgeStatus);
		
		return purgeStatus;
		
	}

}
