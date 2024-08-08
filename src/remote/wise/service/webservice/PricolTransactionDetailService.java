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
import remote.wise.service.datacontract.PricolTransactionDetailReqContract;
import remote.wise.service.datacontract.PricolTransactionDetailRespContract;
import remote.wise.service.implementation.PricolTransactionDetailImpl;
//import remote.wise.util.WiseLogger;

/** Webservice to give the transaction details of the VINs under the given tenancy
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "PricolTransactionDetailService")
public class PricolTransactionDetailService 
{	
	/** This WebMethod provides the transaction details - Log/Event transaction for the given VIN for the given time period
	 * @param reqObj Specifies the VIN and the timePeriod for which the transaction details has to be returned
	 * @return Returns the Log/Event transaction as a HashMap with parameter/Event Name as Key and the corresponding value as HashMap value
	 */
	@WebMethod(operationName = "getTransactionDetails", action = "getTransactionDetails")
	public List<PricolTransactionDetailRespContract> getTransactionDetails(@WebParam(name="reqObj" ) PricolTransactionDetailReqContract reqObj)
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("PricolTransactionDetailService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		List<PricolTransactionDetailRespContract> responseList = new LinkedList<PricolTransactionDetailRespContract>();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Serial Number:"+reqObj.getSerialNumber()+",  "+"TransactionTimestamp:"+reqObj.getTransactionTimestamp());
		responseList = new PricolTransactionDetailImpl().getTransactionDetails(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<responseList.size(); i++)
		{
			iLogger.info("Serial Number:"+responseList.get(i).getSerialNumber()+",  "+"TransactionTimestamp:"+responseList.get(i).getTransactionTimeStamp() +
					",  "+"Transaction Data:"+responseList.get(i).getTransactionData());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:PricolTransactionDetailService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return responseList;
	}
}
