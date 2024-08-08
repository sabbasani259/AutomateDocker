//CR321-20220712-Vidya SagarM-CombineToFotaReport to combine required fields to wisetraceability schema .
package remote.wise.service.webservice;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CombineToFotaReport;


@Path("/CombineTotraceablilityFotaReportService")
public class CombineTotraceablilityFotaReportTableService {
	
	@GET
	@Path("updatefotareport")
	@Produces(MediaType.APPLICATION_JSON)
	  public String combineTotraceablilityFotaReportTable() 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - combine To traceablility Fota Report Table");
		CombineToFotaReport cfr=new CombineToFotaReport();
		String output= cfr.processCombineToFotaReport();
		iLogger.info("Exiting Sample WebService -  combine To traceablility Fota Report Table");
		return output;
	}
}
