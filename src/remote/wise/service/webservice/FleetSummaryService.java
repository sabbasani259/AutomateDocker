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
import remote.wise.service.datacontract.FleetSummaryReqContract;
import remote.wise.service.datacontract.FleetSummaryRespContract;
import remote.wise.service.implementation.FleetSummaryImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;

@WebService(name = "FleetSummaryService")

public class FleetSummaryService 
{
	/*static Logger fatalError = Logger.getLogger("fatalErrorLogger");
	static Logger businessError = Logger.getLogger("businessErrorLogger");*/
	@WebMethod(operationName = "GetFleetSummaryService", action = "GetFleetSummaryService")
	public List<FleetSummaryRespContract>getFleetSummaryService(@WebParam (name="request")FleetSummaryReqContract request ) throws CustomFault
	{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("FleetSummaryService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("period:"+request.getPeriod()+ "contactId:"+request.getContactId()+ "tenancyIdList :"+request.getTenancyIdList()+ "assetGroupIdList :"+request.getAssetGroupIdList()+ "assetTypeIdList :"+request.getAssetTypeIdList()+ "customAssetGroupIdList :"+request.getCustomAssetGroupIdList()+"landmarkIdList :"+request.getLandmarkIdList()+"alertTypeIdList"+request.getAlertTypeIdList()+"alertSeverity :"+request.getAlertSeverity()+"machineGroupId :"+request.getMachineGroupId()+ "notificationDimensionID :"+request.getNotificationDimensionID());
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(request.getContactId());
		request.setContactId(UserID);
		iLogger.info("Decoded userId::"+request.getContactId());
		

		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

			
			if(request.getTenancyIdList()!=null && request.getTenancyIdList().size()>0){
				request.setTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(request.getTenancyIdList()));
			}
			
		
		List<FleetSummaryRespContract> response=new FleetSummaryImpl().getFleetSummaryService(request);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("totalIdleHours:"+response.get(i).getTotalIdleHours()+",  "+"totalWorkingHours:"+response.get(i).getTotalWorkingHours()+",   " +
					"totalOffHours:"+response.get(i).getTotalOffHours());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:FleetSummaryService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		return response;
	}
}
