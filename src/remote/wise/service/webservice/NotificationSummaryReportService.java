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
import remote.wise.service.datacontract.NotificationSummaryReportReqContract;
import remote.wise.service.datacontract.NotificationSummaryReportRespContract;
import remote.wise.service.implementation.NotificationSummaryReportImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;
@WebService(name = "NotificationSummaryReportService")
public class NotificationSummaryReportService {
		
	@WebMethod(operationName = "NotificationSummary", action = "NotificationSummary")
	public List<NotificationSummaryReportRespContract> GetNotificationSummary(@WebParam(name="reqObj") NotificationSummaryReportReqContract reqObj) throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("NotificationSummaryReportService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("fromDate:"+reqObj.getFromDate()+"toDate:"+reqObj.getToDate()+"period:"+reqObj.getPeriod()+ "LoginId:"+reqObj.getLoginId()+ "TenancyIdList :"+reqObj.getTenancyIdList()+ "ModelidList :"+reqObj.getModelList()+ "MachineGroupIdList :"+reqObj.getMachineGroupIdList()+ "MachineProfileIdList :"+reqObj.getMachineProfileIdList()+"alertTypeId:"+reqObj.getAlertTypeIdList()+"loginTenancyIdList:"+reqObj.getLoginTenancyIdList());
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
				reqObj.setLoginId(UserID);
				iLogger.info("Decoded userId::"+reqObj.getLoginId());
				
				//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

				if(reqObj.getLoginTenancyIdList()!=null && reqObj.getLoginTenancyIdList().size()>0){
					reqObj.setLoginTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getLoginTenancyIdList()));
				}
				
				if(reqObj.getTenancyIdList()!=null && reqObj.getTenancyIdList().size()>0){
					reqObj.setTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getTenancyIdList()));
				}
		
		List<NotificationSummaryReportRespContract> respObj = new NotificationSummaryReportImpl().GetNotificationSummaryDetails(reqObj);	
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<respObj.size(); i++)
		{
			iLogger.info("Row "+i+": ");
			iLogger.info("serialNumber:"+respObj.get(i).getSerialNumber()+",  "+"tenancy_name:"+respObj.get(i).getTenancy_name()+",   " +
					"machineGroupId:"+respObj.get(i).getMachineGroupId()+",  "+"machineGroupName"+respObj.get(i).getMachineGroupName()+",  "+
				"Count:"+respObj.get(i).getCount()+",  " +"AssetGroupId: "+respObj.get(i).getAssetGroupId()+
					"AssetGroupName:"+respObj.get(i).getAssetGroupName()+",  " +"AssetTypeId:"+respObj.get(i).getAssetTypeId()+
					"AssetTypeName:"+respObj.get(i).getAssetTypeName()+",  " +"TenancyId:"+respObj.get(i).getTenancyId()+
					"NotificationTypeName:"+respObj.get(i).getNotificationTypeName()+",  " +"NickName:"+respObj.get(i).getNickName()+
					"nameCount:"+respObj.get(i).getNameCount()+", "+"DealerName: "+respObj.get(i).getDealerName());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:NotificationSummaryReportService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		return respObj;
	}
}
