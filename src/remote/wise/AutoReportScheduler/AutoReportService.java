package remote.wise.AutoReportScheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@Path("/AutoReportService")
public class AutoReportService 
{
	@GET()
	@Produces("text/plain")
	@Path("/runAutoReporting")
	public String runAutoReporting(@QueryParam("subscriptionType") String subscriptionType, @QueryParam("reportId") int reportId,
			@QueryParam("accountMappingCodeList") String accountMappingCodeList, @QueryParam("accountType") String accountType)
	{
		String status="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		//Generate RunID
		String DateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String runId = "Run"+DateTime;
		
		iLogger.info("AutoReports:"+subscriptionType+"Report:"+runId+":AutoReportService:" +
				"WebService Input: subscriptionType:"+subscriptionType+"; reportId:"+reportId+"" +
				"; accountMappingCodeList:"+accountMappingCodeList+"; accountType:"+accountType);
		//--------------- Check for Mandatory parameters
		if(subscriptionType==null || accountType==null)
		{
			fLogger.fatal("AutoReports:"+subscriptionType+"Report:"+runId+":Invalid input:subscriptionType and accountType are Mandatory parameters");
			return "FAILURE";
		}
		
		//DF20180730 - Rajani Nagaraju - Service gets invoked every 15 mins if not returned. Since this is a long running job, opening it under new thread
		//status= new AutoSchedulerImpl().sendSubscribedReport(subscriptionType, runId, reportId, accountMappingCodeList, accountType);
		
		new AutoReportImpl(subscriptionType, runId, reportId, accountMappingCodeList, accountType);
		
		long endTime = System.currentTimeMillis();
		iLogger.info("AutoReports:"+subscriptionType+"Report:"+runId+":AutoReportService:" +
				"WebService Output:Status:"+status+"; Total Time in ms:"+(endTime - startTime));
		
		return status;
	}
}
