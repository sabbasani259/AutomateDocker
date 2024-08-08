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
import remote.wise.service.datacontract.NotificationSummaryReqContract;
import remote.wise.service.datacontract.NotificationSummaryRespContract;
import remote.wise.service.implementation.NotificationSummaryImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;
/**
 * 
 * @author tejgm
 * This Webservice class is to get summary on notifications.
 */
@WebService(name = "NotificationSummaryService")

public class NotificationSummaryService {
	
	/**
	 * @param reqCont provides the input to get summary on notification
	 * @return respNotificationSummary returns the summary on notification
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetNotificationSummaryService", action = "GetNotificationSummaryService")
	public List<NotificationSummaryRespContract> getNotificationSummary(@WebParam(name="reqCont") NotificationSummaryReqContract request)throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("NotificationSummaryService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("period: "+request.getPeriod()+",  contactId: "+request.getContactId()+",  " +
				"notificationTypeIdList: "+request.getNotificationTypeIdList() + 
				",  notificationIdList: "+request.getNotificationIdList() +
				",  tenancyIdList: "+request.getTenancyIdList() + 
				",  assetGroupIdList: "+request.getAssetGroupIdList() +
				",  assetTypeIdList: "+request.getAssetTypeIdList() +
				",  ownStock: "+request.isOwnStock()+
				", activealerts:"+request.isActiveAlerts());
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(request.getContactId());
				request.setContactId(UserID);
				iLogger.info("Decoded userId::"+request.getContactId());
				
				//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

				
				if(request.getTenancyIdList()!=null && request.getTenancyIdList().size()>0){
					request.setTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(request.getTenancyIdList()));
				}
		
		List<NotificationSummaryRespContract> listResponse=new NotificationSummaryImpl().getNotificationSummary(request);		
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<listResponse.size(); i++)
		{
			iLogger.info("Row "+i+" :");
			iLogger.info("notificationTypeIdList: "+listResponse.get(i).getNotificationTypeIdList()+",  " +
					"notificationTypeNameList: "+listResponse.get(i).getNotificationTypeNameList()+",  " +
					"Notificationcountcount: "+listResponse.get(i).getNotificationcountcount());
			/*iLogger.info("notificationTypeIdList: "+listResponse.get(i).getNotificationTypeIdList()+",  " +
					"notificationTypeNameList: "+listResponse.get(i).getNotificationTypeNameList()+",  " +
					"Notificationcountcount: "+listResponse.get(i).getNotificationcountcount()+",  " +
					"serialNumber: "+listResponse.get(i).getSerialNumber()+",  " +
					"tenancy_name: "+listResponse.get(i).getTenancy_name()+",  " +
					"machineGroupName: "+listResponse.get(i).getMachineGroupName()+",  " +
					"machineProfileName: "+listResponse.get(i).getMachineProfileName());*/
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:NotificationSummaryService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		return listResponse;
	}
}
