package remote.wise.service.webservice;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.GensetTrendsDataImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

/**
 *  WebService class to get Genset trends charts data
 * @author KO369761
 * DF20181214
 */

@Path("/GensetTrendsDataService")
public class GensetTrendsDataService {
	
	@GET
	@Path("getGensetTrendsData")
	@Produces({ MediaType.APPLICATION_JSON })
	public HashMap<String, HashMap<Integer, HashMap<String, Object>>> getGensetTrendsData(
			@Context HttpHeaders httpHeaders,
			@QueryParam("loginID") String loginId,
			@QueryParam("serialNumber") String serialNumber,
			@QueryParam("period") String period,
			@QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) throws CustomFault {

		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		HashMap<String, HashMap<Integer, HashMap<String, Object>>> response = null;
		String csrfToken = null;
		boolean isValidCSRF=false;
		CommonUtil utilObj = new CommonUtil();

		iLogger.info("---- Webservice Input ------");
		iLogger.info("loginId:"+loginId+","+"serialNumber:"+serialNumber+","+"period:"+period);

		
		if(httpHeaders.getRequestHeader("CSRFTOKEN")!=null)
		{
			csrfToken=httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
		}
		iLogger.info("GensetTrendsDataService ::  received csrftoken :: "+csrfToken);
		if(csrfToken!=null){
			isValidCSRF=utilObj.validateANTICSRFTOKEN(loginId,csrfToken);
		}
		iLogger.info("GensetTrendsDataService ::   csrftoken isValidCSRF :: "+isValidCSRF);
		if(!isValidCSRF)
		{
			iLogger.info("GensetTrendsDataService ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}
		
		//DF20180626 @KO369761 getting decoded UserId
		String UserID=utilObj.getUserId(loginId);
		iLogger.info("Decoded userId::"+UserID);
		
		iLogger.info("Decoded userId::"+UserID);
		
		response = new GensetTrendsDataImpl().getGensetTrendsData(UserID, serialNumber, period, startDate, endDate);
		
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:GensetTrendsDataService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");

		return response;
		
	}
}
