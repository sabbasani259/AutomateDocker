/**
 * CR300: 20220720 : Dhiraj K : SOS closure and Report Changes
 */
package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.SOSAlertResponseContract;
import remote.wise.service.implementation.SOSAlertsImpl;
import remote.wise.util.ListToStringConversion;

/**
 * @author KO369761
 * 
 */
@Path("/SOSAlertsRestService")
public class SOSAlertsRestService {
	
	@GET
	@Path("viewSOSAlert")
	@Produces({ MediaType.APPLICATION_JSON })
	public HashMap<String, Object> viewSOSAlert(@QueryParam("asset_event_id")int assetEventId) {

		Logger iLogger = InfoLoggerClass.logger;
		HashMap<String, Object> response = null;
		iLogger.info("UpdateSOSAlertsRestService:updateSOSAlertsComments WebService START");
		long startTime = System.currentTimeMillis();
		response = new SOSAlertsImpl().viewSOSAlert(assetEventId);
		long endTime = System.currentTimeMillis();
		iLogger.info("UpdateSOSAlertsRestService:updateSOSAlertsComments WebService Output -----> response:"+ response);
		iLogger.info("serviceName:SOSAlertsRestService~executionTime:"+(endTime-startTime)+"~"+""+"~");

		return response;
	}

	@POST
	@Path("editSOSAlerts")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({ MediaType.APPLICATION_JSON })
	public String editSOSAlerts(LinkedHashMap<String, Object> reqObj) {

		Logger iLogger = InfoLoggerClass.logger;
		String response = "SUCCESS";
		iLogger.info("UpdateSOSAlertsRestService:updateSOSAlertsComments WebService START");
		long startTime = System.currentTimeMillis();
		response = new SOSAlertsImpl().editSOSAlerts(reqObj);
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:SOSAlertsRestService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);


		return response;
	}
	
	@POST
	@Path("closeSOSAlerts")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({ MediaType.APPLICATION_JSON })
	public String closeSOSAlerts(LinkedHashMap<String, Object> reqObj) {

		Logger iLogger = InfoLoggerClass.logger;
		String response = "SUCCESS";
		//CR300.sn
		iLogger.info("SOSAlertsRestService:closeSOSAlerts: WebService START");
		iLogger.info("SOSAlertsRestService:closeSOSAlerts : WebService Input ----->" 
		+ "login_id" + reqObj.get("login_id") +":asset_event_id:" + reqObj.get("asset_event_id") 
		+ ":comments:" + reqObj.get("comments") + ":category_id:" + reqObj.get("category_id"));
		//CR300.en
		long startTime = System.currentTimeMillis();
		response = new SOSAlertsImpl().closeSOSAlerts(reqObj);;
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:SOSAlertsRestService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);

		return response;
	}
	
	//CR300.sn
	@SuppressWarnings("unchecked")
	@GET
	@Path("getSOSAlert")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<HashMap<String, Object>> getSOSAlert(
			@QueryParam("accountFilter") String accountFilter,
			@QueryParam("accountIdList")List<String> accountIdlist,
			@QueryParam("profileCodeList") List<String> profileCodeList,
			@QueryParam("modelCodeList") List<String> modelCodeList,
			@QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate,
			@QueryParam("countryCode") String countryCode) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<SOSAlertResponseContract> response = new LinkedList<>();
		List<HashMap<String , Object>> responseMap = new LinkedList<>();
		iLogger.info("SOSAlertsRestService: getSOSAlert : WebService START");
		long startTime = System.currentTimeMillis();
		

		// 1. Validate Account filters and Account Id list
		String accountIds=null;
		if(accountFilter == null || accountFilter.equalsIgnoreCase("null")) {
			accountFilter = null;
		}else {
			if ( accountIdlist == null || accountIdlist.isEmpty()) {
				fLogger.error("AccountIdList is null");
				return responseMap;
			}else {
				accountIds = new ListToStringConversion().getStringList(accountIdlist).toString();
			}
		}
		
		// 2. Validate profileCodeList
		String profileCodes = null;
		if( profileCodeList != null && !profileCodeList.isEmpty()) {
			profileCodes = new ListToStringConversion().getStringList(profileCodeList).toString().trim();
		}
		if(profileCodes == null || profileCodes.replace("'", "").equalsIgnoreCase("null"))
			profileCodes=null;
		
		// 3. Validate modelCodeList
		String modelCodes = null;
		if(modelCodeList != null && !modelCodeList.isEmpty()) {
			modelCodes = new ListToStringConversion().getStringList(modelCodeList).toString().trim();
		}
		if(modelCodes == null || modelCodes.replace("'", "").equalsIgnoreCase("null"))
			modelCodes=null;
		
		// 4. Validate startDate and end Date
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(false);
		try {
			sdf.parse(startDate);
			sdf.parse(endDate);
		}catch(Exception e) {
			fLogger.error("Incorrect startDate or endDate format");
			return responseMap;
		}
		
		//5. Validate country code
		if(countryCode == null || countryCode.equalsIgnoreCase("null"))
			countryCode=null;
		
		iLogger.info("GetSOSAlertsRestService:getSOSAlert : WebService Input ----->");
		iLogger.info("GetSOSAlertsRestService:getSOSAlert : " + "accountFilter:" + accountFilter 
				+ ":accountIdlist:" + accountIds + ":profileCodeList:" + profileCodes 
				+ "modelCodeList" + modelCodes + "startDate" +startDate 
				+ ":endDate:" + endDate + ":countryCode:" +countryCode);
		
		response = new SOSAlertsImpl().getSOSAlert(accountFilter, accountIds, profileCodes, modelCodes,
				startDate, endDate, countryCode);
		for(SOSAlertResponseContract respContract : response) {
			 ObjectMapper mapper = new ObjectMapper();
			 responseMap.add((HashMap<String, Object>) mapper.convertValue(respContract, Map.class));
		}
		
		long endTime = System.currentTimeMillis();
		iLogger.info("GetSOSAlertsRestService:getSOSAlert : WebService Output -----> response:"+ response);
		iLogger.info("serviceName:SOSAlertsRestService~executionTime:"+(endTime-startTime)+"~"+""+"~");

		return responseMap;
	}//CR300.en
	
}
