package remote.wise.service.webservice;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.PullSmsImpl;
//import remote.wise.util.WiseLogger;

@Path("/PullSMS")
public class HelloWorldResource 
{

	@GET
	@Produces("text/plain")
	public String sayHello(@QueryParam("msg") String msg, @QueryParam("dest_mobileno") String dest_mobileno) 
	{
	//	WiseLogger infoLogger = WiseLogger.getLogger("HelloWorldResource:","info");
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		String status = "SUCCESS";
		
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Message:"+msg+",  "+"Mobile Number:"+dest_mobileno);
		
		PullSmsImpl implObj = new PullSmsImpl();
		status = implObj.pullSMS(dest_mobileno, msg);
		iLogger.info("---- Webservice Output ------");
		iLogger.info("Output Status:"+status);
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:HelloWorldResource~executionTime:"+(endTime-startTime)+"~"+""+"~"+status);
		
		return status;
	}
	@GET()
	@Path("/FromInput")
	@Produces("text/plain")
	public String setLanguagePreference(@QueryParam("msg") String msg,@QueryParam("dest_mobno") String dest_mobno)
	{
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		String status =  "SUCCESS";
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Message:"+msg+",  "+"Mobile Number:"+dest_mobno);
		
		PullSmsImpl implObj = new PullSmsImpl();
		status = implObj.setLanguagePreference(dest_mobno, msg);
		
		iLogger.info("---- Webservice Output ------");
		iLogger.info("Output Status:"+status);
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:HelloWorldResource~executionTime:"+(endTime-startTime)+"~"+""+"~"+status);
		
		return status;
	}
}
