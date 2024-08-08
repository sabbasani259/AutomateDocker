package remote.wise.service.webservice;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.client.AlertSeverityReportService.AlertSeverityReport;
import remote.wise.exception.CustomFault;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AlertSeverityReportReqContract;
import remote.wise.service.datacontract.AlertSeverityReportRespContract;
import remote.wise.service.implementation.AlertSeverityReportImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ListToStringConversion;
//ME100011411-Sai Divya-20240424-changed AlertSeverityReport to OfflineAlertSeverityReportRESTServiceCsv
@Path("/AlertSeverityReportRESTServiceCsv")
public class OfflineAlertSeverityReportRESTServiceCsv {

	@SuppressWarnings("unchecked")
	@POST
	@Path("getAlertSeverityReport")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String , Object>> getAlertSeverityReport(LinkedHashMap<String, Object> requestObj) throws CustomFault {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: " + startDate);
		long startTime = System.currentTimeMillis();
		List<AlertSeverityReportRespContract> response = new LinkedList<AlertSeverityReportRespContract>();	
		List<HashMap<String , Object>> responseMap = new LinkedList<HashMap<String, Object>>();
		
		String loginId = null;
		String period =null;
		String ownStock =null;
		String activeAlerts =null;
		String flag =null;
		
		try {
			iLogger.info("Webservice input : " + requestObj);
			CommonUtil util = new CommonUtil();
			String isValidinput = null;
			ListToStringConversion convert = new ListToStringConversion();		

			for (int i = 0; i < requestObj.size(); i++) {
				if (requestObj.get("loginTenancyIdList") != null) {
					List<Integer> loginTenancyIdList = (List<Integer>) requestObj.get("loginTenancyIdList");
					String loginTenancyIdListString = convert.getIntegerListString(loginTenancyIdList).toString();
					isValidinput = util.inputFieldValidation(loginTenancyIdListString);
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}
				}
				if (requestObj.get("tenancyIdList") != null) {
					List<Integer> tenancyIdList = (List<Integer>) requestObj.get("tenancyIdList");
					String tenancyIdListString = convert.getIntegerListString(tenancyIdList).toString();
					isValidinput = util.inputFieldValidation(tenancyIdListString);
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}
				}
				if (requestObj.get("assetGroupIdList") != null) {
					List<Integer> assetGroupIdList = (List<Integer>) requestObj.get("assetGroupIdList");
					String assetGroupIdListString = convert.getIntegerListString(assetGroupIdList).toString();
					isValidinput = util.inputFieldValidation(assetGroupIdListString);
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}
				}
				if (requestObj.get("assetTypeIdList") != null) {
					List<Integer> assetTypeIdList = (List<Integer>) requestObj.get("assetTypeIdList");
					String assetTypeIdListString = convert.getIntegerListString(assetTypeIdList).toString();
					isValidinput = util.inputFieldValidation(assetTypeIdListString);
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}
				}
				if (requestObj.get("eventTypeIdList") != null) {

					List<Integer> eventTypeIdList = (List<Integer>) requestObj.get("eventTypeIdList");
					String eventTypeIdListString = convert.getIntegerListString(eventTypeIdList).toString();
					isValidinput = util.inputFieldValidation(eventTypeIdListString);
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}

				}
				
				// s.n : CR-316 : 20220509 : Prafull : Getting the Blocked event Id lists and converting it to a comma separated string 
				if (requestObj.get("BlockedEventIdList") != null) {

					List<Integer> blockedEventIdList = (List<Integer>) requestObj.get("BlockedEventIdList");
					String blockedEventIdListString = convert.getIntegerListString(blockedEventIdList).toString();
					isValidinput = util.inputFieldValidation(blockedEventIdListString);
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}

				}
				// e.n : CR-316 : 20220509 : Prafull : Getting the Blocked event Id lists and converting it to a comma separated string  
				
				if (requestObj.get("eventSeverityList") != null) {
					List<Integer> eventSeverityList = (List<Integer>) requestObj.get("eventSeverityList");
					String eventSeverityListString = convert.getIntegerListString(eventSeverityList).toString();
					isValidinput = util.inputFieldValidation(eventSeverityListString);
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}

				}
				if (requestObj.get("customAssetGroupIdList") != null) {
					List<Integer> customAssetGroupIdList = (List<Integer>) requestObj.get("customAssetGroupIdList");
					String customAssetGroupIdListString = convert.getIntegerListString(customAssetGroupIdList).toString();
					isValidinput = util.inputFieldValidation(customAssetGroupIdListString);
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}

				}
				if (requestObj.get("landmarkCategoryList") != null) {
					List<Integer> landmarkCategoryList = (List<Integer>) requestObj.get("landmarkCategoryList");
					String landmarkCategoryListString = convert.getIntegerListString(landmarkCategoryList).toString();
					isValidinput = util.inputFieldValidation(landmarkCategoryListString);
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}

				}
				if (requestObj.get("landmarkIdList") != null) {
					List<Integer> landmarkIdList = (List<Integer>) requestObj.get("landmarkIdList");
					String landmarkIdListString = convert.getIntegerListString(landmarkIdList).toString();
					isValidinput = util.inputFieldValidation(landmarkIdListString);
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}
				}
				if (requestObj.get("loginId") != null) {
					 loginId = new CommonUtil().getUserId((String) requestObj.get("loginId"));
					isValidinput = util.inputFieldValidation(loginId.toString());
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}

				}
				if (requestObj.get("Period") != null) {					
					isValidinput = util.inputFieldValidation(requestObj.get("Period").toString());
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}

				}
				
				if (requestObj.get("ownStock") != null) {
					isValidinput = util.inputFieldValidation(requestObj.get("ownStock").toString());
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}
				}
				if (requestObj.get("activeAlerts") != null) {
					isValidinput = util.inputFieldValidation(requestObj.get("activeAlerts").toString());
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}
				}
				if (requestObj.get("flag") != null) {
					isValidinput = util.inputFieldValidation(requestObj.get("flag").toString());
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}
				}
			}
			
			
			AlertSeverityReportReqContract request = new AlertSeverityReportReqContract();
			
			List<Integer> loginTenancyIdList = (List<Integer>) requestObj.get("loginTenancyIdList");
			request.setLoginTenancyIdList(loginTenancyIdList);
			List<Integer> TenancyIdList = (List<Integer>) requestObj.get("tenancyIdList");
			request.setTenancyIdList(TenancyIdList);
			List<Integer> AssetGroupIdList = (List<Integer>) requestObj.get("assetGroupIdList");
			request.setAssetGroupIdList(AssetGroupIdList);
			List<Integer> AssetTypeIdList = (List<Integer>) requestObj.get("assetTypeIdList");
			request.setAssetTypeIdList(AssetTypeIdList);
			List<Integer> eventTypeIdList = (List<Integer>) requestObj.get("eventTypeIdList");
			request.setEventTypeIdList(eventTypeIdList);
			
			// s.n : CR-316 : 20220509 : Prafull : adding where clause to exclude the event Ids from the output 
			List<Integer> blockedEventIdList = (List<Integer>) requestObj.get("blockedEventIdList");
			request.setBlockedEventIdList(blockedEventIdList);
			// e.n : CR-316 : 20220509 : Prafull : adding where clause to exclude the event Ids from the output 
			
			List<String> eventSeverityList = (List<String>) requestObj.get("eventSeverityList");
			request.setEventSeverityList(eventSeverityList);
			List<Integer> customAssetGroupIdList = (List<Integer>) requestObj.get("customAssetGroupIdList");
			request.setCustomAssetGroupIdList(customAssetGroupIdList);
			List<Integer> LandmarkCategoryList = (List<Integer>) requestObj.get("landmarkCategoryList");
			request.setLandmarkCategoryList(LandmarkCategoryList);
			List<Integer> LandmarkIdList = (List<Integer>) requestObj.get("landmarkIdList");
			request.setLandmarkIdList(LandmarkIdList);
			loginId = new CommonUtil().getUserId((String) requestObj.get("loginId"));
			iLogger.info(loginId);
			request.setLoginId(loginId);
			if(requestObj.get("period") != null)
			period = requestObj.get("period").toString();
			request.setPeriod(period);
			ownStock = requestObj.get("ownStock").toString();
			request.setOwnStock(Boolean.parseBoolean(ownStock));
			activeAlerts = requestObj.get("activeAlerts").toString();
			request.setActiveAlerts(Boolean.parseBoolean(activeAlerts));			
			flag = requestObj.get("flag").toString();
			request.setFlag(Boolean.parseBoolean(flag));
			
			iLogger.info("AlertSeverityReportService::-------- Request ------");
			iLogger.info("LoginId:"+request.getLoginId()+ "Period:"+request.getPeriod()+ "TenancyIdList :"+request.getTenancyIdList()+ "AssetGroupIdList :"+request.getAssetGroupIdList()+ "AssetTypeIdList :"+request.getAssetTypeIdList()+ "customAssetGroupIdList :"+request.getCustomAssetGroupIdList()
					+"ActiveAlerts :"+request.isActiveAlerts());
			AlertSeverityReportImpl obj=new AlertSeverityReportImpl();
			response = obj.getAlertSeverityReportDetailsNew(request);

			 for(AlertSeverityReportRespContract respContract : response) {
				 ObjectMapper mapper = new ObjectMapper();
				 responseMap.add((HashMap<String, Object>) mapper.convertValue(respContract, Map.class));
			 }
			String sourceDir = null;
			String sourceDirOem = null;
			Properties prop = new Properties();
			try {
				prop.load(getClass().getClassLoader()
						.getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				sourceDir = prop.getProperty("AlertSeverityReportPath");
				sourceDirOem = prop.getProperty("AlertSeverityReportPathForOEM");
				
			} catch (IOException e1) {
				e1.printStackTrace();
				fLogger.fatal("issue in while getting path from configuration path" + e1.getMessage());
			}
			if (request.getLoginTenancyIdList().contains(100)) {
				obj.saveToCopy(response,sourceDirOem);
			}else {
				obj.saveToCopy(response,sourceDir);
			}
			//iLogger.info("AlertSeverityReportService::----- Response -----"+response);
			Calendar cal1 = Calendar.getInstance();
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
			String endDate = sdf1.format(cal1.getTime());
			iLogger.info("AlertSeverityReportService::Current Enddate: "+endDate);
			long endTime=System.currentTimeMillis();
			iLogger.info("AlertSeverityReportService::Webservice Execution Time in ms:"+(endTime-startTime));
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception:" + e.getMessage());
		}
		return responseMap;
	}
}