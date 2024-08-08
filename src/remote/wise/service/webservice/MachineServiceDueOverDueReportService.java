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
import remote.wise.service.datacontract.MachineServiceDueOverDueReqContract;
import remote.wise.service.datacontract.MachineServiceDueOverDueRespContract;
import remote.wise.service.implementation.MachineServiceDueOverDueImpl;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;

@WebService(name = "MachineServiceDueOverDueReportService")
public class MachineServiceDueOverDueReportService {
		
	@WebMethod(operationName = "GetMachineServiceDueOverDueCount", action = "GetMachineServiceDueOverDueCount")
	public List<MachineServiceDueOverDueRespContract> getMachineServiceDueOverDueCount
	(@WebParam(name="reqObj" ) MachineServiceDueOverDueReqContract reqObj) throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("MachineServiceDueOverDueReportService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("loggedInTenancyId:"+reqObj.getLoggedInTenancyId()+","+"period:"+reqObj.getPeriod()+"");
		
		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

				if(reqObj.getLoggedInTenancyId()!=null && reqObj.getLoggedInTenancyId().size()>0){
					reqObj.setLoggedInTenancyId(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getLoggedInTenancyId()));
				}
				
				
				
		List<MachineServiceDueOverDueRespContract> responseList =new MachineServiceDueOverDueImpl().getMachineServiceDueOverDueCount(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<responseList.size(); i++)
		{
			iLogger.info("Row "+i+": ");
			iLogger.info("dealerTenancyId:"+responseList.get(i).getDealerTenancyId()+",  "+"dealerName:"+responseList.get(i).getDealerName()+",   " +
					"machineCountDueForService:"+responseList.get(i).getMachineCountDueForService()+",  "+"machineCountOverdueForService:"+responseList.get(i).getMachineCountOverdueForService());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MachineServiceDueOverDueReportService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return responseList;
	}
}
