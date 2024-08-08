package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.MAServiceDueOverDueCountImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
import remote.wise.util.ListToStringConversion;

@Path("/MAServiceDueOverDueCountRESTService")
public class MAServiceDueOverDueCountRESTService {
	
	@POST
	@Path("getServiceDueCount")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({ MediaType.APPLICATION_JSON })
	public List<HashMap<String, String>> getServiceDueCount(@Context HttpHeaders httpHeaders,LinkedHashMap<String, Object> reqObj) throws CustomFault {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<HashMap<String, String>> response = new LinkedList<HashMap<String, String>>();
		String csrfToken = null;
		boolean isValidCSRF = false;
		CommonUtil util = new CommonUtil();
		try {
			long startTime = System.currentTimeMillis();
			iLogger.info("Webservice input :" + reqObj);

			if (httpHeaders.getRequestHeader("CSRFTOKEN") != null) {
				csrfToken = httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
			}
			iLogger.info("MAServiceDueOverDueCountRESTService:GensetTrendsDataService ::  received csrftoken :: "+ csrfToken);
			if (csrfToken != null) {
				isValidCSRF = util.validateANTICSRFTOKEN((String) reqObj.get("loginId"), csrfToken);
			}
			iLogger.info("MAServiceDueOverDueCountRESTService:GensetTrendsDataService ::   csrftoken isValidCSRF :: "+ isValidCSRF);
			if (!isValidCSRF) {
				iLogger.info("MAServiceDueOverDueCountRESTService:GensetTrendsDataService ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			}
			String isValidinput = null;
			ListToStringConversion convert = new ListToStringConversion();
			String filterListString = "";
			String tenacnyListString = "";
			String userId = "";
			
			//DF20200904 : Zakir : Date Filter --------------------------------------------------------
			String dateFilter = null;
			//-----------------------------------------------------------------------------------------
			
			for (int i = 0; i < reqObj.size(); i++) {
				if (reqObj.get("tenancyIdList") != null) {
					List<Integer> tenacnyList = (List<Integer>) reqObj.get("tenancyIdList");
					tenacnyListString = convert.getIntegerListString(
							tenacnyList).toString();
					isValidinput = util.inputFieldValidation(tenacnyListString);
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}
				}
				if (reqObj.get("filterList") != null) {
					List<String> filterList = (List<String>) reqObj.get("filterList");
					filterListString = convert.getStringWithoutQuoteList(
							filterList).toString();
					isValidinput = util.inputFieldValidation(filterListString);
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}
					filterListString = convert.getStringList(filterList)
							.toString();
				}
				if (reqObj.get("downloadFlag") != null) {
					isValidinput = util.inputFieldValidation(reqObj.get("downloadFlag").toString());
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}
				}
				if (reqObj.get("loginId") != null) {
					userId = new CommonUtil().getUserId((String) reqObj
							.get("loginId"));
					isValidinput = util.inputFieldValidation(userId.toString());
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}
				}
				//DF20200904 : Zakir : Evaluating Date Filter --------------------------------------------------------
				if(reqObj.get("month") != null && reqObj.get("year") != null) {
					/**
					 * Fetching month and year value from req obj and converting it into a String of date format as: yyyy-mm-dd
					 * Example:- if filter is for month 02 and year 2020
					 * 			 then the filter will be applied to fetch all the data for Event_Generated_Time < 2020-03-01
					 */
					if((Integer.parseInt(reqObj.get("month").toString())) < 13 && (Integer.parseInt(reqObj.get("month").toString())) > 0) {
						if((Integer.parseInt(reqObj.get("month").toString())) == 12) {
							dateFilter = (Integer.parseInt(reqObj.get("year").toString()) + 1) + "-01-01";
						}
						else
							dateFilter = reqObj.get("year") + "-" + String.format("%02d", Integer.parseInt(reqObj.get("month").toString()) + 1) +"-01";
					}
				}
				//----------------------------------------------------------------------------------------------------
			}

			List tenancyIdList = (List) reqObj.get("tenancyIdList");
			String accountIdStringList = new DateUtil().getAccountListForTheTenancy(tenancyIdList);
			response = new MAServiceDueOverDueCountImpl().getServiceDueCount(accountIdStringList, filterListString,dateFilter,reqObj.get("downloadFlag").toString(),userId);
			long endTime = System.currentTimeMillis();
			iLogger.info("MAServiceDueOverDueCountRESTService:getServiceDueCount:WebService Output -----> ");
			iLogger.info("serviceName:MAServiceDueOverDueCountRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception:" + e.getMessage());
		}
		return response;
	}

	@POST
	@Path("getServiceOverDueCount")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({ MediaType.APPLICATION_JSON })
	public List<HashMap<String, String>> getServiceOverDueCount(@Context HttpHeaders httpHeaders,LinkedHashMap<String, Object> reqObj) throws CustomFault {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<HashMap<String, String>> response = new LinkedList<HashMap<String, String>>();
		String csrfToken = null;
		boolean isValidCSRF = false;
		CommonUtil util = new CommonUtil();		
		try {
			long startTime = System.currentTimeMillis();
			iLogger.info("Webservice input :" + reqObj);

			if (httpHeaders.getRequestHeader("CSRFTOKEN") != null) {
				csrfToken = httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
			}
			iLogger.info("MAServiceDueOverDueCountRESTService:getServiceOverDueCount ::  received csrftoken :: "+ csrfToken);
			if (csrfToken != null) {
				isValidCSRF = util.validateANTICSRFTOKEN((String) reqObj.get("loginId"), csrfToken);
			}
			iLogger.info("MAServiceDueOverDueCountRESTService:getServiceOverDueCount ::   csrftoken isValidCSRF :: "+ isValidCSRF);
			if (!isValidCSRF) {
				iLogger.info("MAServiceDueOverDueCountRESTService:getServiceOverDueCount ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			}
			String isValidinput = null;
			ListToStringConversion convert = new ListToStringConversion();
			String filterListString = "";
			String tenacnyListString = "";
			String userId = "";
			
			//DF20200904 : Zakir : Date Filter --------------------------------------------------------
			String dateFilter = null;
			//-----------------------------------------------------------------------------------------
			for (int i = 0; i < reqObj.size(); i++) {
				if (reqObj.get("tenancyIdList") != null) {
					List<Integer> tenacnyList = (List<Integer>) reqObj.get("tenancyIdList");
					tenacnyListString = convert.getIntegerListString(
							tenacnyList).toString();
					isValidinput = util.inputFieldValidation(tenacnyListString);
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}
				}
				if (reqObj.get("filterList") != null) {
					List<String> filterList = (List<String>) reqObj.get("filterList");
					filterListString = convert.getStringWithoutQuoteList(filterList).toString();
					isValidinput = util.inputFieldValidation(filterListString);
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}
					filterListString = convert.getStringList(filterList)
							.toString();
				}
				if (reqObj.get("downloadFlag") != null) {
					isValidinput = util.inputFieldValidation(reqObj.get("downloadFlag").toString());
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}
				}
				
				if (reqObj.get("loginId") != null) {					
					userId = new CommonUtil().getUserId((String) reqObj
							.get("loginId"));
					isValidinput = util.inputFieldValidation(userId.toString());
					if (!isValidinput.equals("SUCCESS")) {
						throw new CustomFault(isValidinput);
					}
				}
				//DF20200904 : Zakir : Evaluating Date Filter --------------------------------------------------------
				if(reqObj.get("month") != null && reqObj.get("year") != null) {
					/**
					 * Fetching month and year value from req obj and converting it into a String of date format as: yyyy-mm-dd
					 * Example:- if filter is for month 02 and year 2020
					 * 			 then the filter will be applied to fetch all the data for Event_Generated_Time < 2020-03-01
					 */
					if((Integer.parseInt(reqObj.get("month").toString())) < 13 && (Integer.parseInt(reqObj.get("month").toString())) > 0) {
						if((Integer.parseInt(reqObj.get("month").toString())) == 12) {
							dateFilter = (Integer.parseInt(reqObj.get("year").toString()) + 1) + "-01-01";
						}
						else
							dateFilter = reqObj.get("year") + "-" + String.format("%02d", Integer.parseInt(reqObj.get("month").toString()) + 1) +"-01";
					}
				}
				//----------------------------------------------------------------------------------------------------

			}
			List tenancyIdList = (List) reqObj.get("tenancyIdList");
			String accountIdStringList = new DateUtil().getAccountListForTheTenancy(tenancyIdList);
			response = new MAServiceDueOverDueCountImpl().getServiceOverDueCount(accountIdStringList, filterListString,dateFilter,reqObj.get("downloadFlag").toString(),userId);
			long endTime = System.currentTimeMillis();
			iLogger.info("MAServiceDueOverDueCountRESTService:getServiceDueCount:WebService Output -----> "+ response);
			iLogger.info("serviceName:MAServiceDueOverDueCountRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception:" + e.getMessage());
		}
		return response;
	}

}
