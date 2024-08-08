package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.PricolTransactionSummaryReqContract;
import remote.wise.service.datacontract.PricolTransactionSummaryRespContract;
import remote.wise.service.implementation.PricolTransactionSummaryImpl;
//import remote.wise.util.WiseLogger;

/** Webservice to give the List of VINs registered under the given tenancy
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "PricolTransactionSummaryService")
public class PricolTransactionSummaryService 
{	
	/** This method returns the list of Registered VINs
	 * @param reqObj specifies the VIN/IMEI/SIM as search text corresponding to which data is returned
	 * @return Returns the list of VINs with their IMEI and SIM numbers
	 */
	@WebMethod(operationName = "getVinList", action = "getVinList")
	public List<PricolTransactionSummaryRespContract> getVinList(@WebParam(name="reqObj" ) PricolTransactionSummaryReqContract reqObj)
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("PricolTransactionSummaryService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		List<PricolTransactionSummaryRespContract> responseList = new LinkedList<PricolTransactionSummaryRespContract>();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("SearchCriteria:"+reqObj.getSearchCriteria()+",  "+"SearchText:"+reqObj.getSearchText()+",  " +
				"TenancyId:"+reqObj.getTenancyId()+",  "+"IsPricolTenancy:"+reqObj.isPricolTenancy());
		responseList = new PricolTransactionSummaryImpl().getTransactionSummary(reqObj);
//		iLogger.info("----- Webservice Output-----");
//		for(int i=0; i<responseList.size(); i++)
//		{
//			iLogger.info("Serial Number:"+responseList.get(i).getSerialNumber()+",  "+"ImeiNumber:"+responseList.get(i).getImeiNumber() +
//					",  "+"SimNumber:"+responseList.get(i).getSimNumber()+",  "+"RollOffStatus:"+responseList.get(i).isRollOffStatus());
//		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:PricolTransactionSummaryService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return responseList;
	}
}
