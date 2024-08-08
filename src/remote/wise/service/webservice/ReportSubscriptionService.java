package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



//import com.sun.org.apache.regexp.internal.REDebugCompiler;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.ReportMailSubscriptionListReqContract;
import remote.wise.service.datacontract.ReportSubscriptionReqContract;
import remote.wise.service.datacontract.ReportSubscriptionRespContract;
import remote.wise.service.implementation.ReportSubscriptionImpl;
import remote.wise.service.implementation.ReportSubscriptionImplNew;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;
/**
 * 
 * @author tejgm
 * This service provides the subscription on the reports
 */
@WebService(name = "ReportSubscriptionService")
public class ReportSubscriptionService {
		
	/**
	 * 
	 * @param reqObj subscription on the reports is based on teportId,ContactId
	 * @return response is list of reportNames
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetReportSubscriptionService", action = "GetReportSubscriptionService")
	public List<ReportSubscriptionRespContract> getReportSubscriptionService(@WebParam(name="reqObj")ReportSubscriptionReqContract reqObj)throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("ReportSubscriptionService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("contactId:"+reqObj.getContactId()+","+"reportId:"+reqObj.getReportId()+"");
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(reqObj.getContactId());
				reqObj.setContactId(UserID);
				iLogger.info("Decoded userId::"+reqObj.getContactId());
				
		//List<ReportSubscriptionRespContract> response=new ReportSubscriptionImpl().getreportSubscriptionService(reqObj);
		//DF20180619 - Rajani Nagaraju - Auto Report Subscription - New implementation 
		List<ReportSubscriptionRespContract> response=new ReportSubscriptionImplNew().getreportSubscriptionDetails(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++){
			iLogger.info("reportId:"+response.get(i).getReportId()+","+"reportName:"+response.get(i).getReportName()+","+"contactId:"+response.get(i).getContactId()+","+"weeklyReportSubscription:"+response.get(i).isWeeklyReportSubscription()+","+"monthlyReportSubscription:"+response.get(i).isMonthlyReportSubscription()+"");
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:ReportSubscriptionService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		return response;		
	}
	/**
	 * 
	 * @param response is given as input to set the values
	 * @return
	 */
	@WebMethod(operationName = "SetReportSubscriptionService")
	public String setReportSubscriptionService(@WebParam(name="response")List<ReportSubscriptionRespContract> resp)
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("ReportSubscriptionService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		for(int i=0; i<resp.size(); i++){
			iLogger.info("reportId:"+resp.get(i).getReportId()+","+"reportName:"+resp.get(i).getReportName()+","+"contactId:"+resp.get(i).getContactId()+","+"weeklyReportSubscription:"+resp.get(i).isWeeklyReportSubscription()+","+"monthlyReportSubscription:"+resp.get(i).isMonthlyReportSubscription()+"");
			
			String UserID=new CommonUtil().getUserId(resp.get(i).getContactId());
			resp.get(i).setContactId(UserID);
			iLogger.info("Decoded userId::"+resp.get(i).getContactId());
		
		}
		long startTimeTaken=System.currentTimeMillis();
		//String response=new ReportSubscriptionImpl().setreportSubscriptionService(resp);
		//DF20180619 - Rajani Nagaraju - Auto Report Subscription - New implementation 
		String response=new ReportSubscriptionImplNew().setreportSubscriptionDetails(resp);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:ReportSubscriptionService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}
	
	@WebMethod(operationName = "sendReportSubscriptionMails")
	public String sendReportSubscriptionMails(@WebParam(name="reqObj")ReportMailSubscriptionListReqContract reqObj)throws CustomFault{
		
		List<ReportSubscriptionRespContract> responseList =null;
		String message = new ReportSubscriptionImpl().sendReportSubscriptionMails(reqObj);
		return message;
	}
}
