package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;


import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.NotificationReportDetailsReqContract;
import remote.wise.service.datacontract.NotificationReportDetailsRespContract;
import remote.wise.service.implementation.NotificationReportDetailsImpl;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;
@WebService(name = "NotificationReportDetailsService")
public class NotificationReportDetailsService {
	
	@WebMethod(operationName = "GetNotificationReportDetailsService", action = "GetNotificationReportDetailsService")
	public List<NotificationReportDetailsRespContract> getNotificationReportDetails(@WebParam(name="reqObj")NotificationReportDetailsReqContract reqObj)
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("NotificationReportDetailsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		//to get the individual cont of alerts for each VIN...ID20131022..done by smitha on 22nd oct 2013
		iLogger.info("fromDate:"+reqObj.getFromDate()+"toDate:"+reqObj.getToDate()+ "SerialNumber:"+reqObj.getSerialNumberList()+"TenancyIDList:"+reqObj.getTenancyIdList()+" loginTenancyIdList:"+reqObj.getLoginTenancyIdList());
		//ended on oct 22nd 2013...ID20131022
		
		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

				if(reqObj.getLoginTenancyIdList()!=null && reqObj.getLoginTenancyIdList().size()>0){
					reqObj.setLoginTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getLoginTenancyIdList()));
				}
				
				if(reqObj.getTenancyIdList()!=null && reqObj.getTenancyIdList().size()>0){
					reqObj.setTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getTenancyIdList()));
				}
				
				
		List<NotificationReportDetailsRespContract> respObj=new NotificationReportDetailsImpl().getNotificationReportDetails(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<respObj.size(); i++)
		{
			iLogger.info("Row "+i+": ");
			iLogger.info("serialNumber:"+respObj.get(i).getSerialNumber()+",  "+"category:"+respObj.get(i).getCategory()+",   " +
					"alertName:"+respObj.get(i).getAlertName()+",  "+"description"+respObj.get(i).getDescription()+",  "+
					"status:"+respObj.get(i).getStatus()+",  " +"severity: "+respObj.get(i).getSeverity()+
					"dateRaised:"+respObj.get(i).getDateRaised()+",  " +"location:"+respObj.get(i).getLocation()+
					"machineName:"+respObj.get(i).getMachineName()+", "+"DealerName:"+respObj.get(i).getDealerName());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:NotificationReportDetailsService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respObj;
	}
}
