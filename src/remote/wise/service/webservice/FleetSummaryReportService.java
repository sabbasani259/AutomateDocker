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
import remote.wise.service.datacontract.FleetSummaryReportReqContract;
import remote.wise.service.datacontract.FleetSummaryReportRespContract;
import remote.wise.service.implementation.FleetSummaryReporImpl;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;

@WebService(name = "FleetSummaryReportService")
public class FleetSummaryReportService
{
	
	/**
	 * This method gets the FleetSummary
	 * @param reqObj sends the request to get summary on fleet
	 * @return response gets the list on fleets
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetFleetSummaryReportService", action = "GetFleetSummaryReportService")
	public List<FleetSummaryReportRespContract> getFleetSummaryReportService(@WebParam(name="reqObj") FleetSummaryReportReqContract reqObj )throws CustomFault
	{	
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("FleetSummaryReportService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("fromDate:"+reqObj.getFromDate()+"toDate:"+reqObj.getToDate()+"period:"+reqObj.getPeriod()+",  "+"machineGroupIdList:"+reqObj.getMachineGroupIdList()+",  "+"machineProfileIdList :" + 
				reqObj.getMachineProfileIdList()+",   "+ "tenancyIdList: "+reqObj.getTenanctIdList()+ ",  "+ 
				"modelIdList"+reqObj.getModelIdList()+",   "+ "isGroupingOnAssetGroup: "+reqObj.isGroupingOnAssetGroup()+",   "+ "loginTenancyIdList: "+reqObj.getLoginTenancyIdList());
		
		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

		if(reqObj.getLoginTenancyIdList()!=null && reqObj.getLoginTenancyIdList().size()>0){
			reqObj.setLoginTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getLoginTenancyIdList()));
		}
		
		if(reqObj.getTenanctIdList()!=null && reqObj.getTenanctIdList().size()>0){
			reqObj.setTenanctIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getTenanctIdList()));
		}
		
		List<FleetSummaryReportRespContract> response=new FleetSummaryReporImpl().getFleetSummaryReportService(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("Row "+i+": ");
			iLogger.info("SerialNumber: " +response.get(i).getSerialNumber()+", MachineName: " +response.get(i).getMachineName() + 
					", averageFuelConsumption: " +response.get(i).getAverageFuelConsumption()+",  fuelused: " +response.get(i).getFuelused() +
					", fuelUsedInIdle: " +response.get(i).getFuelUsedInIdle() +
					", powerBandhigh: " +response.get(i).getPowerBandhigh()+", powerBandMedium: " +response.get(i).getPowerBandMedium() +
					", powerBandLow: " +response.get(i).getPowerBandLow() +
					", WorkingTime: " +response.get(i).getWorkingTime()+",  IdleTime: " +response.get(i).getIdleTime() +
					", EngineOff: " +response.get(i).getEngineOff()+", MachineHours: " +response.get(i).getMachineHours() +
					", TotalMachineLifeHours: " +response.get(i).getTotalMachineLifeHours() +
					", MachineProfile: " +response.get(i).getMachineProfile()+", Profile:" +response.get(i).getProfile() +
					", MachineGroupId: " +response.get(i).getMachineGroupIdList()+", machineGroupName: " +response.get(i).getMachineGroupName() +
					", ModelId: " +response.get(i).getModelIdList() + ", ModelName: " +response.get(i).getModelName() +
					", TenancyId: " +response.get(i).getTenanctIdList() +", TenancyName: " +response.get(i).getTenancyName() +
					", DealerName: " +response.get(i).getDealername());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:FleetSummaryReportService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}



}
