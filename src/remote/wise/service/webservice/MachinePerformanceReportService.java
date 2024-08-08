package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;


import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.MachinePerformanceReportReqContract;
import remote.wise.service.datacontract.MachinePerformanceReportRespContract;
import remote.wise.service.implementation.MachinePerformanceReportImpl;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;

@WebService(name = "MachinePerformanceReportService")
public class MachinePerformanceReportService 
{
	/** This method returns the details of the performance of a machine over the given period
	 * @param reqObj Specify the input filters 
	 * @return Returns the performance details of the machine for the given specified time
	 */
	@WebMethod(operationName = "MachinePerformanceReportService", action = "MachinePerformanceReportService")
	public List<MachinePerformanceReportRespContract> getMachPerformanceReport(@WebParam(name="reqObj")MachinePerformanceReportReqContract reqObj)
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("MachinePerformanceReportService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("fromDate:"+reqObj.getFromDate()+"toDate:"+reqObj.getToDate()+"Period"+reqObj.getPeriod()+",  "+"MachineGroupIdList"+reqObj.getMachineGroupIdList()+",  " +
				"MachineProfileIdList:"+reqObj.getMachineProfileIdList()+",  "+"TenancyIdList: "+reqObj.getTenancyIdList()+",  " +
				"ModelIdList:"+reqObj.getModelIdList()+",  "+"isGroupingOnAssetGroup: "+reqObj.isGroupingOnAssetGroup());
		
		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

	
		
		if(reqObj.getTenancyIdList()!=null && reqObj.getTenancyIdList().size()>0){
			reqObj.setTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getTenancyIdList()));
		}
		
		
		List<MachinePerformanceReportRespContract> response=new MachinePerformanceReportImpl().getMachPerformanceReport(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("Row "+i+" : ");
			iLogger.info("EngineOff:"+response.get(i).getEngineOff()+",  "+"EngineOn:"+response.get(i).getEngineOn()+",   " +
					"WorkingTime:"+response.get(i).getWorkingTime()+",  "+"IdleTime:"+response.get(i).getIdleTime()+",  " + 
					"PowerBandLow:"+response.get(i).getPowerBandLow()+",  "+"PowerBandMedium:"+response.get(i).getPowerBandMedium()+",  " + 
					"powerBandHigh:"+response.get(i).getPowerBandHigh()+",  "+"startingEngineRunHours:"+response.get(i).getStartingEngineRunHours()+",  " + 
					"finishEngineRunHours:"+response.get(i).getFinishEngineRunHours()+",  "+"fuelUsedLitres:"+response.get(i).getFuelUsedLitres()+",  " + 
					"fuelUsedIdleLitres:"+response.get(i).getFuelUsedIdleLitres()+",  "+"finishFuelLevel:"+response.get(i).getFinishFuelLevel()+",  " + 
					"overallFuelConsumptionLitres:"+response.get(i).getOverallFuelConsumptionLitres()+",  "  +
					"startingEngineRunHoursLife:"+response.get(i).getStartingEngineRunHoursLife()+",  " + 
					"finishEngineRunHoursLife:"+response.get(i).getFinishEngineRunHoursLife()+",  "  +
					"fuelUsedLitresLife:"+response.get(i).getFuelUsedLitresLife()+",  " + 
					"fuelUsedIdleLitresLife:"+response.get(i).getFuelUsedIdleLitresLife()+",  "  +
					"finishFuelLevelLife:"+response.get(i).getFinishFuelLevelLife()+",  " + 
					"serialNumber:"+response.get(i).getSerialNumber()+",  " + 
					"asset_group_name:"+response.get(i).getAsset_group_name()+",  "+"assetGroupId:"+response.get(i).getAssetGroupId()+",  " + 
					"customMachineGroupName:"+response.get(i).getCustomMachineGroupName()+",  "+"customMachineGroupId:"+response.get(i).getCustomMachineGroupId()+",  " + 
					"tenancyName:"+response.get(i).getTenancyName()+",  "+"tenancyId:"+response.get(i).getTenancyId()+",  " + 
					"modelName:"+response.get(i).getModelName()+",  "+"modelId:"+response.get(i).getModelId()+", "+
					"DealerName:"+response.get(i).getDealerName());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MachinePerformanceReportService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}
}
