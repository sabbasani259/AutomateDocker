package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessobject.ReportDetailsBO;
import remote.wise.client.ResetPasswordService.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.MachineHourMeterTrendDataReportReqContract;
import remote.wise.service.datacontract.MachineHourMeterTrendDataReportRespContract;
//import remote.wise.util.WiseLogger;

public class MachineHourMeterTrendDataReportImpl {
	
	/*static Logger infoLogger = Logger.getLogger("infoLogger");
	static Logger businessError = Logger.getLogger("businessErrorLogger");*/
	//Defect Id 1337 : - Suprava -20130923 - Log4j Changes - Using static logger object all throughout the application
	
	//public static WiseLogger infoLogger = WiseLogger.getLogger("MachineHourMeterTrendDataReportImpl:","info");
	//public static WiseLogger businessError = WiseLogger.getLogger("MachineHourMeterTrendDataReportImpl:","businessError");

	String zoneName;
	double totalMachineHours;
	HashMap<String, HashMap<String, Double>> trendData;

	/**
	 * @return the zoneName
	 */
	public String getZoneName() {
		return zoneName;
	}

	/**
	 * @param zoneName the zoneName to set
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	
	
	/**
	 * @return the totalMachineHours
	 */
	public double getTotalMachineHours() {
		return totalMachineHours;
	}

	/**
	 * @param totalMachineHours the totalMachineHours to set
	 */
	public void setTotalMachineHours(double totalMachineHours) {
		this.totalMachineHours = totalMachineHours;
	}

	/**
	 * @return the trendData
	 */
	public HashMap<String, HashMap<String, Double>> getTrendData() {
		return trendData;
	}

	/**
	 * @param trendData the trendData to set
	 */
	public void setTrendData(HashMap<String, HashMap<String, Double>> trendData) {
		this.trendData = trendData;
	}
	/**
	 * method to get machine hour meter trend data for specified zones dealer wise
	 * @param reqObj
	 * @return List<MachineHourMeterTrendDataReportRespContract>
	 * @throws CustomFault
	 */
	public List<MachineHourMeterTrendDataReportRespContract> getMachineHourMeterTrendData(MachineHourMeterTrendDataReportReqContract reqObj) throws CustomFault{
		
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		if(reqObj.getTenancyIdList()==null || reqObj.getTenancyIdList().size()<=0){
			bLogger.error("Tenancy ID list is not provided");
			throw new CustomFault("Tenancy ID list is not provided");
		}
		
		//Custom Dates (fromDate,toDate) added by Juhi on 13-August-2013 
		if(((reqObj.getPeriod()==null))&&((reqObj.getFromDate()==null)&&(reqObj.getToDate()==null)))
		{
			bLogger.error("Please pass either Period or custom dates");
			throw new CustomFault("Either Period or custom dates are not specified");
		}
		
		 MachineHourMeterTrendDataReportImpl impl=null;
		ReportDetailsBO bo = new ReportDetailsBO();
		impl = bo.getMachineHourMeterTrendData(reqObj.getFromDate(),reqObj.getToDate(),reqObj.getTenancyIdList(),reqObj.getPeriod());
		trendData = impl.getTrendData();
		MachineHourMeterTrendDataReportRespContract respObj = null;
		
		List<MachineHourMeterTrendDataReportRespContract> respList = new ArrayList<MachineHourMeterTrendDataReportRespContract>();
		for (String zoneName: trendData.keySet()) {
			respObj = new MachineHourMeterTrendDataReportRespContract();
			respObj.setZoneName(zoneName);
			respObj.setDealerData(trendData.get(zoneName));
			respList.add(respObj);		
		}
		
		return respList;
	}
	/**
	 * method to get machine hour meter trend data for all zones zonewise
	 * @param reqObj
	 * @return List<MachineHourMeterTrendDataReportRespContract>
	 * @throws CustomFault
	 */
	public List<MachineHourMeterTrendDataReportRespContract> getMachineTrendDataForAllZones(MachineHourMeterTrendDataReportReqContract reqObj) throws CustomFault{
		
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		if(reqObj.getTenancyIdList()==null || reqObj.getTenancyIdList().size()<=0){
			bLogger.error("Tenancy ID list is not provided");
			throw new CustomFault("Tenancy ID list is not provided");
		}
		if(((reqObj.getPeriod()==null))&&((reqObj.getFromDate()==null) &&(reqObj.getToDate()==null)))
		{
			bLogger.error("Please pass either Period or custom dates");
			throw new CustomFault("Either Period or custom dates are not specified");
		}
		 List<MachineHourMeterTrendDataReportImpl> listTrendData=null;
		 MachineHourMeterTrendDataReportImpl impl = null;
		ReportDetailsBO bo = new ReportDetailsBO();
		listTrendData = bo.getMachineTrendDataForAllZones(reqObj.getTenancyIdList(),reqObj.getFromDate(),reqObj.getToDate(),reqObj.getPeriod());
		List<MachineHourMeterTrendDataReportRespContract> respList = new ArrayList<MachineHourMeterTrendDataReportRespContract>();
		MachineHourMeterTrendDataReportRespContract respObj = null;
		Iterator<MachineHourMeterTrendDataReportImpl> iterList = listTrendData.iterator();
		while(iterList.hasNext()){
			impl = iterList.next();
			respObj = new MachineHourMeterTrendDataReportRespContract();
			respObj.setZoneName(impl.getZoneName());
			respObj.setMachineHours(impl.getTotalMachineHours());
			respList.add(respObj);			
		}
		return respList;
	}
	
}
