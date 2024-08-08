package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.SubscriberReportServiceImpl;

/**
 * @author Mani
 *
 *DF100001418:20220429:DHIRAJ K:Notification Subscriber Report issue 
 */
@Path("/SubscriberReport")
public class SubscriberReportService {

	@GET
	@Path("/setSubscriberReport")
	//DF100001418.so
	//@Produces(MediaType.APPLICATION_JSON)
	//DF100001418.eo
	//DF100001418.sn
	@Produces(MediaType.TEXT_PLAIN)
	//DF100001418.en
	public String getSubscriberReport() throws CustomFault{
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String result=null;
		try{
			long startTime = System.currentTimeMillis();
			infoLogger.info("Webservice : /SubscriberReport/setSubscriberReport ");
			SubscriberReportServiceImpl impl=new SubscriberReportServiceImpl();
			result=impl.getNotificationSubscriberReport();
			infoLogger.info("Webservice : /SubscriberReport/setSubscriberReport output : "+result);
			long endTime = System.currentTimeMillis();
			infoLogger.info("serviceName:SubscriberReportService~executionTime:"+(endTime-startTime)+"~"+""+"~"+result);
			
		}catch(Exception e){
			e.printStackTrace();
			fLogger.error("Exception in webservice : SubscriberReportService/setSubscriberReport : "+e.getMessage());
		}
		return result;
	}

	@GET
	@Path("/downloadSubscriberReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String downloadSubscriberReport(@QueryParam("tenancy_id")int tenancy_id,@QueryParam("loginId")String loginId){
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String result=null;
		try{
			long startTime = System.currentTimeMillis();
			infoLogger.info("Webservice : /SubscriberReport/getSubscriberReport input :"+tenancy_id);
			SubscriberReportServiceImpl impl=new SubscriberReportServiceImpl();
			result=impl.downloadNotificationSubscriberReport(tenancy_id,loginId);
			infoLogger.info("Webservice : /SubscriberReport/downloadSubscriberReport output : "+result);
			long endTime = System.currentTimeMillis();
			infoLogger.info("serviceName:SubscriberReportService~executionTime:"+(endTime-startTime)+"~"+loginId+"~"+result);
		}catch(Exception e){
			e.printStackTrace();
			fLogger.error("Exception in webservice : SubscriberReportService/downloadSubscriberReport : "+e.getMessage());
		}
		return result;
	}
}
