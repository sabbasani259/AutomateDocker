package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.PricolRollOffReqContract;
import remote.wise.service.datacontract.PricolRollOffRespContract;
import remote.wise.service.implementation.PricolRollOffImpl;
//import remote.wise.util.WiseLogger;

/** Webservice to perform RollOff activity for Pricol Device
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "PricolRollOffService")
public class PricolRollOffService 
{	
	/** This webmethod does the RollOff for the specified list of VINs
	 * @param reqObj specifies the List of VINs
	 * @return Returns the rollOff activity status for each of the VINs
	 */
	@WebMethod(operationName = "rollOffPricolDevice", action = "rollOffPricolDevice")
	public List<PricolRollOffRespContract> rollOffPricolDevice(PricolRollOffReqContract reqObj)
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("PricolRollOffService:","info");
		Logger iLogger = InfoLoggerClass.logger;	
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		List<PricolRollOffRespContract> responseList = new LinkedList<PricolRollOffRespContract>();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Serial Number:"+reqObj.getSerialNumber()+",  "+"TenancyId:"+reqObj.getTenancyId());
		responseList = new PricolRollOffImpl().rollOffPricolDevice(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<responseList.size(); i++)
		{
			iLogger.info("Serial Number:"+responseList.get(i).getSerialNumber()+",  "+
					"RollOffStatus:"+responseList.get(i).getRollOffStatus());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:PricolRollOffService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return responseList;
	}
}
