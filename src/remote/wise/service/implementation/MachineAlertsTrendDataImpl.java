package remote.wise.service.implementation;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;
////import org.apache.log4j.Logger;
import remote.wise.businessobject.ReportDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.MachineAlertsTrendDataReqContract;
import remote.wise.service.datacontract.MachineAlertsTrendDataRespContract;
//import remote.wise.util.WiseLogger;

public class MachineAlertsTrendDataImpl {
	
	/*static Logger infoLogger = Logger.getLogger("infoLogger");
	static Logger businessError = Logger.getLogger("businessErrorLogger");
	*/
	//DefectId:1337 - Suprava - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("MachineAlertsTrendDataImpl:","businessError");

	String zoneName;
	Long totalAlerts;
	HashMap<String, HashMap<String, Long>> trendData;

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
	 * @return the totalAlerts
	 */
	public Long getTotalAlerts() {
		return totalAlerts;
	}

	/**
	 * @param totalAlerts the totalAlerts to set
	 */
	public void setTotalAlerts(Long totalAlerts) {
		this.totalAlerts = totalAlerts;
	}

	/**
	 * @return the trendData
	 */
	public HashMap<String, HashMap<String, Long>> getTrendData() {
		return trendData;
	}

	/**
	 * @param trendData the trendData to set
	 */
	public void setTrendData(HashMap<String, HashMap<String, Long>> trendData) {
		this.trendData = trendData;
	}
	/**
	 * method to get machine hour alerts trend data for specified zones dealer wise
	 * @param reqObj
	 * @return List<MachineHourMeterTrendDataReportRespContract>
	 * @throws CustomFault
	 */
	public List<MachineAlertsTrendDataRespContract> getMachineAlertsTrendData(MachineAlertsTrendDataReqContract reqObj) throws CustomFault{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		if(reqObj.getTenancyIdList()==null || reqObj.getTenancyIdList().size()<=0){
			bLogger.error("Tenancy ID list is not provided");
			throw new CustomFault("Tenancy ID list is not provided");
		}
		//Custom Dates (fromDate,toDate) added by Juhi on 23-September-2013 
		if(((reqObj.getPeriod()==null))&&((reqObj.getFromDate()==null) &&(reqObj.getToDate()==null)))
		{
			
			bLogger.error("Please pass either Period or custom dates");
			throw new CustomFault("Either Period or custom dates are not specified");
		}
		
		
		MachineAlertsTrendDataImpl impl=null;
		ReportDetailsBO bo = new ReportDetailsBO();
		impl = bo.getMachineAlertsTrendData(reqObj.getFromDate(),reqObj.getToDate(),reqObj.getTenancyIdList(),reqObj.getPeriod());
		trendData = impl.getTrendData();
		MachineAlertsTrendDataRespContract respObj = null;
		
		List<MachineAlertsTrendDataRespContract> respList = new ArrayList<MachineAlertsTrendDataRespContract>();
		for (String zoneName: trendData.keySet()) {
			respObj = new MachineAlertsTrendDataRespContract();
			respObj.setZoneName(zoneName);
			respObj.setDealerData(trendData.get(zoneName));
			respList.add(respObj);		
		}
		
		return respList;
	}
	/**
	 * method to get machine alerts trend data for all zones zone-wise
	 * @param reqObj
	 * @return List<MachineHourMeterTrendDataReportRespContract>
	 * @throws CustomFault
	 */
	public List<MachineAlertsTrendDataRespContract> getMachineAlertsForAllZones(MachineAlertsTrendDataReqContract reqObj) throws CustomFault{
		
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		if(reqObj.getTenancyIdList()==null || reqObj.getTenancyIdList().size()<=0){
			bLogger.error("Tenancy ID list is not provided");
			throw new CustomFault("Tenancy ID list is not provided");
		}
		//Custom Dates (fromDate,toDate) added by Juhi on 23-September-2013 
		if(((reqObj.getPeriod()==null))&&((reqObj.getFromDate()==null) &&(reqObj.getToDate()==null)))
		{
			
			bLogger.error("Please pass either Period or custom dates");
			throw new CustomFault("Either Period or custom dates are not specified");
		}
		
		 List<MachineAlertsTrendDataImpl> listTrendData=null;
		 MachineAlertsTrendDataImpl impl = null;
		ReportDetailsBO bo = new ReportDetailsBO();
		listTrendData = bo.getMachineAlertsTrendDataForAllZones(reqObj.getFromDate(),reqObj.getToDate(),reqObj.getTenancyIdList(),reqObj.getPeriod());
		List<MachineAlertsTrendDataRespContract> respList = new ArrayList<MachineAlertsTrendDataRespContract>();
		MachineAlertsTrendDataRespContract respObj = null;
		Iterator<MachineAlertsTrendDataImpl> iterList = listTrendData.iterator();
		while(iterList.hasNext()){
			impl = iterList.next();
			respObj = new MachineAlertsTrendDataRespContract();
			respObj.setZoneName(impl.getZoneName());
			respObj.setTotalAlerts(impl.getTotalAlerts());
			respList.add(respObj);			
		}
		return respList;
	}
	
}

