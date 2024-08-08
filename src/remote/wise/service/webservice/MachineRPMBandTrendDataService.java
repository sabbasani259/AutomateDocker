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
import remote.wise.service.datacontract.MachineRPMBandDataReportReqContract;
import remote.wise.service.datacontract.MachineRPMBandDataReportRespContract;
import remote.wise.service.implementation.MachineRPMBandDataImpl;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;

@WebService(name = "MachineRPMBandTrendDataService")
public class MachineRPMBandTrendDataService {

	@WebMethod(operationName = "getMachineRPMBandTrendData", action = "getMachineRPMBandTrendData")	
	public List<MachineRPMBandDataReportRespContract> getMachineRPMBandTrendData(@WebParam(name="reqObj") MachineRPMBandDataReportReqContract reqObj)throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("MachineRPMBandTrendDataService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("fromDate:"+reqObj.getFromDate()+"toDate:"+reqObj.getToDate()+"Period:"+reqObj.getPeriod()+"tenancyIdList :"+reqObj.getTenancyIdList());

		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

	
	if(reqObj.getTenancyIdList()!=null && reqObj.getTenancyIdList().size()>0){
		reqObj.setTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getTenancyIdList()));
	}
		
		List<MachineRPMBandDataReportRespContract> response= new MachineRPMBandDataImpl().getMachineRPMBandTrendData(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("Row "+i+" : ");
			iLogger.info("dealerData:"+response.get(i).getDealerData()+",  "+"zoneName:"+response.get(i).getZoneName());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MachineRPMBandTrendDataService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;		
	}

	@WebMethod(operationName = "getMachineRPMBandTrendDataForAllZones", action = "getMachineRPMBandTrendDataForAllZones")
	public List<MachineRPMBandDataReportRespContract> getMachineRPMBandTrendDataForAllZones(@WebParam(name="reqObj") MachineRPMBandDataReportReqContract reqObj) throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLoggeiLoggerer = WiseLogger.getLogger("MachineRPMBandTrendDataService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger iLogger = Logger.getLogger("iLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("fromDate:"+reqObj.getFromDate()+"toDate:"+reqObj.getToDate()+"Period:"+reqObj.getPeriod());
		List<MachineRPMBandDataReportRespContract> response= new MachineRPMBandDataImpl().getMachineRPMBandDataForAllZones(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("Row "+i+" : ");
			iLogger.info("maxIdleRPMBand:"+response.get(i).getMaxIdleRPMBand()+",maxWorkingRPMBand:"+response.get(i).getMaxWorkingRPMBand()+", zoneName:"+response.get(i).getZoneName());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MachineRPMBandTrendDataService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;		
	}

}


