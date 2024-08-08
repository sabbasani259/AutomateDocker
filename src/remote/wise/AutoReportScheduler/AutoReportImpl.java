package remote.wise.AutoReportScheduler;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;

public class AutoReportImpl implements Runnable
{
	Thread t;
	String subscriptionType;
	String runId;
	int reportId;
	String accountMappingCodeList;
	String accountType;
	
	public AutoReportImpl()
	{
		
	}
	
	public AutoReportImpl(String subscriptionType, String runId,int reportId,String accountMappingCodeList, String accountType)
	{
		t = new Thread(this, "AutoReportImpl");
		
		this.subscriptionType=subscriptionType;
		this.runId=runId;
		this.reportId=reportId;
		this.accountMappingCodeList=accountMappingCodeList;
		this.accountType=accountType;
		
		t.start();
	}
	
	public void run()
	{
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		String status =  new AutoSchedulerImpl().sendSubscribedReport(this.subscriptionType, this.runId, this.reportId, 
				this.accountMappingCodeList, this.accountType);
		long endTime = System.currentTimeMillis();
		iLogger.info("AutoReports:"+subscriptionType+"Report:"+runId+":AutoReportImpl:" +
				"WebService Output:Status:"+status+"; Total Time in ms:"+(endTime - startTime));
	}
}
