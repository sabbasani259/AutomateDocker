package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.SmsListernerImpl;
//import remote.wise.util.WiseLogger;

@Path("/SMSListener")
public class SMSListernerService 
{
	@GET
	@Produces("text/plain")
	
	public String receivePacket(@QueryParam("msg") String msg) 
	{
		String status = "SUCCESS";
		//WiseLogger infoLogger = WiseLogger.getLogger("SMSListernerService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		iLogger.info("Hello - Inside SMS Listener"+msg);
		iLogger.info("---- Webservice Input ------");
		String[] msgString = msg.split(" ");
		String smsListerber_Str = null;
		if(msgString.length>2)
			smsListerber_Str=msgString[2];
		//System.out.println("smsListerber_Str"+smsListerber_Str);
		iLogger.info("SMSListerner String:"+msg);
		iLogger.info("SMSListerner Actual String:"+smsListerber_Str);
		SmsListernerImpl implObj = new SmsListernerImpl();
		status = implObj.SmsListerner(smsListerber_Str);
		
		long endTime=System.currentTimeMillis();
		iLogger.info("---- Webservice Output ------");
		iLogger.info("Output Status:"+status);
		iLogger.info("serviceName:SMSListernerService~executionTime:"+(endTime-startTime)+"~"+""+"~"+status);
		return status;
	}
}
