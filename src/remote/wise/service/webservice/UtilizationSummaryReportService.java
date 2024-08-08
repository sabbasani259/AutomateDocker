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
import remote.wise.service.datacontract.UtilizationSummaryReportReqContract;
import remote.wise.service.datacontract.UtilizationSummaryReportRespContract;
import remote.wise.service.implementation.UtilizationSummaryReportImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;

/** WebService that pulls out the report for MachineUtilizationSummary for the given time period
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "UtilizationSummaryReportService")
public class UtilizationSummaryReportService 
{	
	/** Webservice method that pulls out utilization summary data in the given period
	 * @param reqObj loginId, period and other filters are specified through this reqObj
	 * @return Returns the utilization summary details for the user accessible list of serial Numbers
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetUtilizationSummaryReport", action = "GetUtilizationSummaryReport")
	public List<UtilizationSummaryReportRespContract> getUtilizationSummaryReport(@WebParam(name="reqObj" ) UtilizationSummaryReportReqContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("UtilizationSummaryReportService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("UtilizationSummaryReportService---- Webservice Input ------");
		iLogger.info("fromDate:"+reqObj.getFromDate()+"toDate:"+reqObj.getToDate()+"LoginId:"+reqObj.getLoginId()+" ,  "+"Period:"+reqObj.getPeriod()+",   " +
				"tenancyIdList:"+reqObj.getTenancyIdList()+",  "+"machineGroupIdList:"+reqObj.getMachineGroupIdList()+",  " +
				"machineProfileIdList:"+/*reqObj.getMachineProfileIdList()*/reqObj.getAssetGroupIdList()+",  " +"modelIdList: "+reqObj.getModelIdList()+",  " +" isGroupingOnAssetGroup: "+reqObj.isGroupingOnAssetGroup());
		
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
		
		List<UtilizationSummaryReportRespContract> response = new UtilizationSummaryReportImpl().getMachineUtilizationSummary(reqObj);
		iLogger.info("UtilizationSummaryReportService----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("Row "+i+": ");
			iLogger.info("tenancyId:"+response.get(i).getTenancyId()+",  "+"tenancyName:"+response.get(i).getTenancyName()+",   " +
					"machineGroupId:"+response.get(i).getMachineGroupId()+",  "+"machineGroupName:"+response.get(i).getMachineGroupName()+",  "+
					"machineProfileId:"+response.get(i).getMachineProfileId()+",  " +
					"machineProfileName: "+response.get(i).getMachineProfileName()+",  "+"modelId:"+response.get(i).getModelId()+",   " +
					"modelName: "+response.get(i).getModelName()+",  "+"machineName:"+response.get(i).getMachineName()+",   " +
					"serialNumber: "+response.get(i).getSerialNumber()+",  "+"engineWorkingDurationInMin:"+response.get(i).getEngineWorkingDurationInMin()+",   " +
					"engineRunDurationInMin: "+response.get(i).getEngineRunDurationInMin()+",  "+"engineOffDurationInMin:"+response.get(i).getEngineOffDurationInMin()+",   " +
					"machineUtilizationPercentage: "+response.get(i).getMachineUtilizationPercentage()+"," +
					"DealerName: "+response.get(i).getDealerName());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UtilizationSummaryReportService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}

}
