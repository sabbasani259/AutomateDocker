package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AlertSummaryDetailedRespContract;
import remote.wise.service.datacontract.AlertSummaryReqContract;
import remote.wise.service.datacontract.AlertSummaryRespContract;
import remote.wise.service.implementation.AlertSummaryImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;
/**
 * 
 * @author tejgm
 * This Implemention gives the summary on alerts
 */

@WebService public class AlertSummaryService 
{

	
	
	/**
	 * 
	 * @param reqObj
	 * @return response
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "AlertSummaryDetails", action = "AlertSummaryDetails")	
	public AlertSummaryRespContract getServiceDueOverDue(@WebParam(name="reqObj") AlertSummaryReqContract request) throws CustomFault{	
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AlertSummaryService:","info");
		
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		
		//iLogger.info("AlertSummaryService -1 ");
		iLogger.info("LoginId:"+request.getLoginId()+ "Period:"+request.getPeriod()+ "TenancyIdList :"+request.getTenancyIdList()+ "AssetGroupIdList :"+request.getAssetGroupIdList()+ "AssetTypeIdList :"+request.getAssetTypeIdList()+ "customAssetGroupIdList :"+request.getCustomAssetGroupIdList()
				+"ActiveAlerts :"+request.isActiveAlerts());
		//iLogger.info("AlertSummaryService -2 ");
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(request.getLoginId());
				request.setLoginId(UserID);
				
		AlertSummaryRespContract response = new AlertSummaryImpl().getServiceDueOverdueDetails(request);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("YellowThresholdValue:"+response.getYellowThresholdValue()+",  "+"RedThresholdValue:"+response.getRedThresholdValue());
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AlertSummaryService~executionTime:"+(endTime - startTime)+"~"+UserID+"~");
		return response;
	}
	@WebMethod(operationName = "DetailedAlertSummary" , action="DetailedAlertSummary")
	public List<AlertSummaryDetailedRespContract> getDetailedAlertSummary(@WebParam(name="reqObj") AlertSummaryReqContract request) throws CustomFault
	{
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("LoginId:"+request.getLoginId()+ "Period:"+request.getPeriod()+ "TenancyIdList :"+request.getTenancyIdList()+ "AssetGroupIdList :"+request.getAssetGroupIdList()+ "AssetTypeIdList :"+request.getAssetTypeIdList()+ "customAssetGroupIdList :"+request.getCustomAssetGroupIdList()
				+"ActiveAlerts :"+request.isActiveAlerts());
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(request.getLoginId());
		request.setLoginId(UserID);

		List<AlertSummaryDetailedRespContract> response = new AlertSummaryImpl().getDetailedAlertSummary(request);
		iLogger.info("----- Webservice Output-----");
		for(int i=0;i<response.size();i++)
		{
		iLogger.info("VIN NUmber:"+response.get(i).getSerialNumber()+",  "+"Event Desciption :"+response.get(i).getEventDescription()+",  "+"Event Severity :"+response.get(i).getEventSeverity());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AlertSummaryService~executionTime:"+(endTime - startTime)+"~"+UserID+"~");
		return response;
	}

}
