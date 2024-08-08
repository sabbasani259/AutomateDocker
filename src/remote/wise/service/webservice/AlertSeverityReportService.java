/**
 * 
 */
package remote.wise.service.webservice;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AlertSeverityReportReqContract;
import remote.wise.service.datacontract.AlertSeverityReportRespContract;
import remote.wise.service.implementation.AlertSeverityReportImpl;
import remote.wise.util.CommonUtil;
/**
 * @author sunayak
 *
 */
@WebService(name = "AlertSeverityReportService")
public class AlertSeverityReportService {

	/**
	 * 
	 * @param reqObj
	 * @return response
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "AlertSeverityReport", action = "AlertSeverityReport")
	public List<AlertSeverityReportRespContract> getAlertSeverityReport(@WebParam(name="reqObj") AlertSeverityReportReqContract request) throws CustomFault{	
		
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("LoginId:"+request.getLoginId()+ "Period:"+request.getPeriod()+ "TenancyIdList :"+request.getTenancyIdList()+ "AssetGroupIdList :"+request.getAssetGroupIdList()+ "AssetTypeIdList :"+request.getAssetTypeIdList()+ "customAssetGroupIdList :"+request.getCustomAssetGroupIdList()
				+"ActiveAlerts :"+request.isActiveAlerts());
		
		List<AlertSeverityReportRespContract> response= new LinkedList<AlertSeverityReportRespContract>();
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(request.getLoginId());
		request.setLoginId(UserID);
		
		
		//DF20160919 @Roopa AlertSeverityReport performance improvement
			 response = new AlertSeverityReportImpl().getAlertSeverityReportDetailsNew(request);
		

		 // response = new AlertSeverityReportImpl().getAlertSeverityReportDetails(request);
		
		 
		iLogger.info("AlertSeverityReportService::----- Webservice Output-----");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("AlertSeverityReportService::Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AlertSeverityReportService~executionTime:"+(endTime - startTime)+"~"+UserID+"~");
		return response;
	}
}
