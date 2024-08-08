package remote.wise.service.webservice;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.DeleteDuplicateFaultDetailsRecordsImpl;

/**
 *  WebService class to delete old user token ids from idMaster table.
 * @author KO369761
 *
 */

@Path("/delete")
public class DeleteDuplicateFaultDetailsRecordsRESTService {
	
	@GET
	@Path("duplicateFaultDetailsRecords")
	@Produces({ MediaType.TEXT_PLAIN })
	public String deleteDuplicateFaultDetailsRecords() {

		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		String response = "SUCCESS";
		
		iLogger.info("DeleteDuplicateFaultDetailsRecordsRESTService:: duplicateFaultDetailsRecords WebService Start");
		
		response = new DeleteDuplicateFaultDetailsRecordsImpl().deleteDuplicateFaultDetailsRecords();
		
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:DeleteDuplicateFaultDetailsRecordsRESTService~executionTime:"+(endTime - startTime)+"~"+""+"~"+response);

		return response;
		
	}

}
