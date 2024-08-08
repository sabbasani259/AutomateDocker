package remote.wise.AutoReportScheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;

@Path("/AutoReportReprocessService")
public class AutoReportReprocessService 
{

	@GET()
	@Produces("text/plain")
	@Path("/reprocessFailureRecords")
	public String reprocessFailureRecords(@QueryParam("priority") int priority, @QueryParam("subscriptionType") String subscriptionType, 
			@QueryParam("reportingPeriod") int reportingPeriod, @QueryParam("reportingYear") int reportingYear,
			@QueryParam("accountMappingCodeList") String accountMappingCodeList, @QueryParam("reportId") int reportId, 
			@QueryParam("accountType") String accountType)
	{
		String status="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
			
		long startTime = System.currentTimeMillis();
		//Generate RunID
		String DateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String runId = "RRun"+DateTime;
		
		iLogger.info("RAutoReports:"+subscriptionType+"Report:"+runId+":AutoReportReprocessService:" +
				"WebService Input: subscriptionType:"+subscriptionType+"; priority:"+priority+"" +
				"; reportingPeriod:"+reportingPeriod+"; reportingYear:"+reportingYear+"; accountMappingCodeList:"+accountMappingCodeList+"" +
						"; reportId:"+reportId+"; accountType:"+accountType);
		
		status= new ReprocessFaultDetails().reprocessFailureRecords(priority, subscriptionType, reportingPeriod, 
				reportingYear, accountMappingCodeList, reportId, accountType, runId);
		
		long endTime = System.currentTimeMillis();
		iLogger.info("RAutoReports:"+subscriptionType+"Report:"+runId+":AutoReportReprocessService:" +
				"WebService Output:Status:"+status+"; Total Time in ms:"+(endTime - startTime));
		
		return status;
	}

}
