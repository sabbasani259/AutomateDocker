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
@Path("/DynamicAMH_AMD_AMDE_TableCreationRESTService")
public class DynamicAMH_AMD_AMDE_TableCreationRESTService {
	
	@GET
	@Path("createTable")
	@Produces("text/plain")
	public String createTable(@QueryParam("weekNo") int weekNo,@QueryParam("year") int year) 
	{
		String craeteStatus="SUCCESS";
		
		Logger iLogger = InfoLoggerClass.logger;

		long startTime = System.currentTimeMillis();
		
		
		craeteStatus=new DynamicAMH_AMD_AMDE_TableImpl().createTable(weekNo,year);
		
		long endTime = System.currentTimeMillis();
		
		iLogger.info("serviceName:DynamicAMH_AMD_AMDE_TableCreationRESTService~executionTime:"+(endTime - startTime)+"~"+""+"~"+craeteStatus);
		
		return craeteStatus;
		
	}

}
