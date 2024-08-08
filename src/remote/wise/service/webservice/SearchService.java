/**
 * 
 */
package remote.wise.service.webservice;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;


import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.MGLandMarkReqContract;
import remote.wise.service.datacontract.MGLandMarkRespContract;
import remote.wise.service.implementation.MGLandMarkDetailsImpl;
//import remote.wise.util.WiseLogger;

/**
 * @author kprabhu5
 *
 */

@WebService(name = "SearchService")
public class SearchService {
		
	@WebMethod(operationName = "getMGLandMarkDetails", action = "getMGLandMarkDetails")	
	public MGLandMarkRespContract getMGLandMarkDetails(@WebParam(name="reqObj") MGLandMarkReqContract reqObj)throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("SearchService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("<---- Webservice Input ------>");
		iLogger.info("LoginTenancyId:"+reqObj.getLoginTenancyId());
		MGLandMarkRespContract response= new MGLandMarkDetailsImpl().getMGLandMarkDetails(reqObj);		
		iLogger.info("<---- Webservice Output ------>");
		iLogger.info("LandmarkCategoryList:"+response.getLandmarkCategoryList()+" , MachineGroupTypeList:"+response.getMachineGroupTypeList()+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:SearchService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		return response;
	}

}
