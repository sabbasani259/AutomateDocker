package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
////import org.apache.log4j.Logger;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.businessobject.ReportDetailsBO;
import remote.wise.service.datacontract.MachineRPMBandDataReportReqContract;
import remote.wise.service.datacontract.MachineRPMBandDataReportRespContract;
//import remote.wise.util.WiseLogger;

public class MachineRPMBandDataImpl {
	
	/*static Logger infoLogger = Logger.getLogger("infoLogger");
	static Logger businessError = Logger.getLogger("businessErrorLogger");*/
	
	//DefectId:1337 - Suprava - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("MachineRPMBandDataImpl:","businessError");

	String zoneName;
	 private int maxIdleRPMBand;
	 private int maxWorkingRPMBand;
	Map<String, HashMap<String, String>> trendData;
	
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
	public int getMaxIdleRPMBand() {
		return maxIdleRPMBand;
	}

	public void setMaxIdleRPMBand(int maxIdleRPMBand) {
		this.maxIdleRPMBand = maxIdleRPMBand;
	}

	public int getMaxWorkingRPMBand() {
		return maxWorkingRPMBand;
	}

	public void setMaxWorkingRPMBand(int maxWorkingRPMBand) {
		this.maxWorkingRPMBand = maxWorkingRPMBand;
	}

	public Map<String, HashMap<String, String>> getTrendData() {
		return trendData;
	}

	public void setTrendData(Map<String, HashMap<String, String>> trendData) {
		this.trendData = trendData;
	}

	/**
	 * method to get machine RPM Band trend data for specified zones dealer wise
	 * @param reqObj
	 * @return List<MachineRPMBandDataReportRespContract>
	 * @throws CustomFault
	 */
	public List<MachineRPMBandDataReportRespContract> getMachineRPMBandTrendData(MachineRPMBandDataReportReqContract reqObj) throws CustomFault{
		
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
		
		MachineRPMBandDataImpl impl=null;
		ReportDetailsBO bo = new ReportDetailsBO();
		impl = bo.getMachineRPMBandTrendData(reqObj.getFromDate(),reqObj.getToDate(),reqObj.getTenancyIdList(),reqObj.getPeriod());
		trendData = impl.getTrendData();
		MachineRPMBandDataReportRespContract respObj = null;
		
		List<MachineRPMBandDataReportRespContract> respList = new ArrayList<MachineRPMBandDataReportRespContract>();
		for (String zoneName: trendData.keySet()) {
			respObj = new MachineRPMBandDataReportRespContract();
			respObj.setZoneName(zoneName);
			respObj.setDealerData(trendData.get(zoneName));
			respList.add(respObj);		
		}
		
		return respList;
	}
	/**
	 * method to get machine RPM Band trend data for all zones zone-wise
	 * @param reqObj
	 * @return List<MachineRPMBandDataReportRespContract>
	 * @throws CustomFault
	 */
	public List<MachineRPMBandDataReportRespContract> getMachineRPMBandDataForAllZones(MachineRPMBandDataReportReqContract reqObj) throws CustomFault{
		
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
		
		 List<MachineRPMBandDataImpl> listTrendData=null;
		 MachineRPMBandDataImpl impl = null;
		ReportDetailsBO bo = new ReportDetailsBO();
		listTrendData = bo.getMachineRPMBandTrendDataForAllZones(reqObj.getFromDate(),reqObj.getToDate(),reqObj.getTenancyIdList(),reqObj.getPeriod());
		List<MachineRPMBandDataReportRespContract> respList = new ArrayList<MachineRPMBandDataReportRespContract>();
		MachineRPMBandDataReportRespContract respObj = null;
		Iterator<MachineRPMBandDataImpl> iterList = listTrendData.iterator();
		while(iterList.hasNext()){
			impl = iterList.next();
			respObj = new MachineRPMBandDataReportRespContract();
			respObj.setZoneName(impl.getZoneName());
			respObj.setMaxIdleRPMBand(impl.getMaxIdleRPMBand());
			respObj.setMaxWorkingRPMBand(impl.getMaxWorkingRPMBand());
			respList.add(respObj);			
		}
		return respList;
	}
	
}

