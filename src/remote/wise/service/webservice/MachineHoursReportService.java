package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
//import java.util.logging.Logger;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.MachineHoursReportReqContract;
import remote.wise.service.datacontract.MachineHoursReportRespContract;
import remote.wise.service.implementation.MachineHoursReportImpl;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;

@WebService(name = "MachineHoursReportService")
public class MachineHoursReportService 
{
	/**
	 * 
	 * @param reqObj is passed to get report on machinehours
	 * @return listResponse as a result
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetMachineHoursReportService", action = "GetMachineHoursReportService")
	public List<MachineHoursReportRespContract> getMachineHoursReportService(@WebParam(name="reqObj")MachineHoursReportReqContract reqObj )throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("MachineHoursReportService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("fromDate: "+reqObj.getFromDate()+",  "+"toDate: "+reqObj.getToDate()+",  "+"Period: "+reqObj.getPeriod()+",  "+"MachineGroupIdList: "+reqObj.getMachineGroupIdList()+",  " + 
				"MachineProfileIdList: "+reqObj.getMachineProfileIdList()+",  "+" TenancyIdList: "+reqObj.getTenancyIdList()+",  " +
				"ModelList: "+reqObj.getModelList()+",  "+" isGroupingOnAssetGroup: "+reqObj.isGroupingOnAssetGroup() +
				//DefectID:1406 - Rajani Nagaraju - 20131028 - MachineGrouping issue in Reports and sending Report Totals information
				"LoginTenancyIDList: "+reqObj.getLoginTenancyIdList());
		

		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

			if(reqObj.getLoginTenancyIdList()!=null && reqObj.getLoginTenancyIdList().size()>0){
				reqObj.setLoginTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getLoginTenancyIdList()));
			}
			
			if(reqObj.getTenancyIdList()!=null && reqObj.getTenancyIdList().size()>0){
				reqObj.setTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getTenancyIdList()));
			}
			
		List<MachineHoursReportRespContract> listResponse=new MachineHoursReportImpl().getMachineHoursReportService(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<listResponse.size(); i++)
		{
			iLogger.info("Row "+i+" :");
			iLogger.info("machineGroupName: "+listResponse.get(i).getMachineGroupName()+",  " +
					"machineGroupId: "+listResponse.get(i).getMachineGroupId()+",  " +
					"machineProfileId: "+listResponse.get(i).getMachineProfileId()+",  " +
					"TotalMachineHours: "+listResponse.get(i).getTotalMachineHours()+",  " +
					"MachineHours: "+listResponse.get(i).getMachineHours()+",  " +
					"status: "+listResponse.get(i).getStatus()+",  " +
					"lastEngineRun: "+listResponse.get(i).getLastEngineRun()+",  " +
					"lastReported: "+listResponse.get(i).getLastReported()+",  " +
					"location: "+listResponse.get(i).getLocation()+",  " +
					"machineProfileName: "+listResponse.get(i).getMachineProfileName()+",  " +
					"tenancyName: "+listResponse.get(i).getTenancyName()+",  " +
					"assetGroupName: "+listResponse.get(i).getAssetGroupName()+",  " +
					"durationInCurrentStatus: "+listResponse.get(i).getDurationInCurrentStatus()+",  " +
					"serialNumber: "+listResponse.get(i).getSerialNumber()+"," +
					"dealerName: "+listResponse.get(i).getDealerName());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MachineHoursReportService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return listResponse;
	}

}
