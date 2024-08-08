/*
 * JCB6336 : 20230214 : Dhiraj k : Fault handling for sending data to MoolDA - ownership and personality update
 */
package com.wipro.mda;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.Logger;
import remote.wise.log.InfoLogging.InfoLoggerClass;

/**
 * <h1>ReprocessDAIngestionService</h1>
 * The ReprocessDAIngestionService is a REST service that implements re-processing of data from MOOLDA_FaultDetails
 * @author SU334449
 * @since 2018-01-08
 */

//@Path("/reprocessOwnerDetails")//JCB6336.o
@Path("/reprocessMoolDAFaultDetails")//JCB6336.n
public class ReprocessDAIngestionService {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String reprocessDAIngestionService(@QueryParam("rejectionPoint") String rejectionPoint){

		String result = "SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		iLogger.info("MDA:ReprocessDAIngestionService:input:rejectionPoint:"+rejectionPoint);
		ReprocessDAIngestionImpl implObj = new ReprocessDAIngestionImpl();
		//result = implObj.reprocessOwnerDetails(rejectionPoint); //JCB6336.o
		result = implObj.reprocessMoolDAFaultDetails(rejectionPoint); //JCB6336.n
		long endTime=System.currentTimeMillis();
		iLogger.info("MDA:ReprocessDAIngestionService:reprocessDAIngestionService: " +
				"Total Time taken in ms:"+(endTime - startTime));
		return result;
	}
}
