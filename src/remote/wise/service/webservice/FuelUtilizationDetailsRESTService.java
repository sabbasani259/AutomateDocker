package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.FuelUtilizationDetailReqContract;
import remote.wise.service.datacontract.FuelUtilizationDetailRespContract;
import remote.wise.service.implementation.FuelUtilizationDetailImpl;
import remote.wise.service.implementation.FuelUtilizationDetailsRESTImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

/**
 *  WebService class to get Fuel Utilization Details for the Week Period Filter
 * @author KO369761
 *
 */

@Path("/FuelUtilizationDetailsRESTService")
public class FuelUtilizationDetailsRESTService {
	
	@GET
	@Path("getFuelUtilizationDetails")
	@Produces({ MediaType.APPLICATION_JSON })
	public HashMap<String, TreeMap<Integer, String>> getFuelUtilizationDetails(
			@QueryParam("loginID") String loginId,
			@QueryParam("serialNumber") String serialNumber,
			@QueryParam("period") String period) throws CustomFault {

		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		HashMap<String, TreeMap<Integer, String>> response = null;

		iLogger.info("---- Webservice Input ------");
		iLogger.info("loginId:"+loginId+","+"serialNumber:"+serialNumber+","+"period:"+period+"");

		//DF20180626 @KO369761 getting decoded UserId
		CommonUtil utilObj = new CommonUtil();
		String UserID=utilObj.getUserId(loginId);
		iLogger.info("Decoded userId::"+UserID);

		//DF20180806:KO369761 - Validating VIN hierarchy against login id/tenancy id
		int tenancyId = utilObj.getTenancyIdFromLoginId(UserID);
		String serialNum = utilObj.validateVIN(tenancyId, serialNumber);
		if(serialNum == null || serialNum.equalsIgnoreCase("FAILURE")){
			throw new CustomFault("Invalid VIN Number");
		}
		
		iLogger.info("Decoded userId::"+UserID);
		
		response = new FuelUtilizationDetailsRESTImpl().getFuelUtilizationDetails(UserID, serialNumber, period);
		
		long endTime=System.currentTimeMillis();
		iLogger.info("Webservice Execution Time in ms:"+(endTime-startTime));
		
		//DF20190107 @ Zakir printing size in loggers
		iLogger.info("Webservice FuelUtilizationDetailsRESTService size : "+response.size());
		iLogger.info("serviceName:FuelUtilizationDetailsRESTService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		
		return response;
		
	}
}
