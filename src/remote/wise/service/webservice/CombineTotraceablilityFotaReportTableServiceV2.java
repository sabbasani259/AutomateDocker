//CR321-20220712-Vidya SagarM-CombineToFotaReport to combine required fields to wisetraceability schema .
package remote.wise.service.webservice;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.Logger;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CombineToFotaReportV2;
//20221130-VidyaSagarM-Fota report to have batch count 1000 CR321
//CR321V2.sn
@Path("/CombineTotraceablilityFotaReportServiceV2")
public class CombineTotraceablilityFotaReportTableServiceV2 {
	
	@GET
	@Path("updatefotareport")
	@Produces(MediaType.APPLICATION_JSON)
	  public String combineTotraceablilityFotaReportTable() 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - combine To traceablility Fota Report Table");
		CombineToFotaReportV2 cfr=new CombineToFotaReportV2();
		String output= cfr.processCombineToFotaReport();
		iLogger.info("Exiting Sample WebService -  combine To traceablility Fota Report Table");
		return output;
	}
}
//CR321V2.en