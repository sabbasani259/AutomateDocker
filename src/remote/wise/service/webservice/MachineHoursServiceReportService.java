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
import remote.wise.service.datacontract.MachineHoursServiceReportReqContract;
import remote.wise.service.datacontract.MachineHoursServiceReportRespContract;
import remote.wise.service.implementation.MachineHoursServiceReportImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;
/**
 * 
 * 
 * This method get MachineHoursServiceReport
 */
@WebService(name = "MachineHoursServiceReportService")

public class MachineHoursServiceReportService {
	
	/**
	 * 
	 * @param reqObj is passed to get a report on hours consumed by the asset.
	 * @return response to signify the details on the hours consumed by the machine.
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetMachineHoursServiceReportService", action = "GetMachineHoursServiceReportService")
	public List<MachineHoursServiceReportRespContract>getMachineHoursService(@WebParam(name="reqObj") MachineHoursServiceReportReqContract reqObj)throws CustomFault
	{	
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("MachineHoursServiceReportService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
//		iLogger.info("fromDate:"+reqObj.getFromDate()+"toDate:"+reqObj.getToDate()+"Period:"+reqObj.getPeriod()+",  "+
		iLogger.info("machineGroupIdList:"+reqObj.getMachineGroupIdList()+",  "+"machineProfileIdList:"+reqObj.getMachineProfileIdList()+",  "+
				"tenancyIdList:"+reqObj.getTenancyIdList()+",  " +"modelIdList: "+reqObj.getModelIdList()+",  " +"isGroupingOnAssetGroup: "+reqObj.isGroupingOnAssetGroup());
		
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
		
		List<MachineHoursServiceReportRespContract> response=new MachineHoursServiceReportImpl().getMachineHoursService(reqObj);		
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("Row "+i+": ");
			iLogger.info("tenancyId:"+response.get(i).getTenancyId()+",  "+"tenancyName:"+response.get(i).getTenancyName()+",   " +
					"machineGroupId:"+response.get(i).getMachineGroupId()+",  "+"machineGroupName:"+response.get(i).getMachineGroupName()+",  "+
					"machineProfileId:"+response.get(i).getMachineProfileId()+",  " +
					"machineProfileName: "+response.get(i).getMachineProfileName()+",  "+"modelId:"+response.get(i).getModelId()+",   " +
					"modelName: "+response.get(i).getModelName()+",  "+"SerialNumber:"+response.get(i).getSerialNumber()+",   " +
					"NickName: "+response.get(i).getNickName()+",  "+"TotalMachineLifeHours:"+response.get(i).getTotalMachineLifeHours()+",   " +
//					"durationSchedule: "+response.get(i).getDurationSchedule()+",  "+"durationSchedule1:"+response.get(i).getDurationSchedule1()+",   " +					
					"ServiceName: "+response.get(i).getServiceName()+",  "+"serviceDate:"+response.get(i).getServiceDate()+",   " +
					"Location: "+response.get(i).getLocation()+",  "+"Status:"+response.get(i).getStatus()+",   " +
					"Severity:"+response.get(i).getSeverity()+",   "+"DealerName:"+response.get(i).getDealerName()+", "+
//					"scheduledDateList: "+response.get(i).getScheduledDateList()+",  "+"lastService:"+response.get(i).getLastService()+",   " +
					"nextService: "+response.get(i).getNextService()/*+",  "+"serviceSchedule:"+response.get(i).getServiceSchedule()*/);
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MachineHoursServiceReportService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}
}
