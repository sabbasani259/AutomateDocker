package remote.wise.service.webservice;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.CustomersUnderDealerImpl;
import remote.wise.service.implementation.LLPremiumReportImpl;
import remote.wise.service.implementation.LLPremiumServiceImpl;
import remote.wise.service.implementation.ReturnMAFlagImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ListToStringConversion;
import remote.wise.service.datacontract.CustomerForDealerRespContract;

@Path("/LLPremiumService")
public class LLPremiumService {
	@SuppressWarnings("unused")
	@GET
	@Path("/getMachineList")
	@Produces(MediaType.APPLICATION_JSON)
	public List<HashMap<String, String>> getPremiumMachines(
			@QueryParam("tenancyId") String tenancyId,
			@QueryParam("vin") String vin,
			@QueryParam("platform") String platform,
			@QueryParam("model") String model,
			@QueryParam("dealerName") String dealerName,
			@QueryParam("customerName") String customerName,
			@QueryParam("premiumFlag") String premiumFlag,
			@QueryParam("premiumStartDate") String premiumStartDate,
			@QueryParam("premiumEndDate") String premiumEndDate,
			@QueryParam("installationStartDate") String installationStartDate,
			@QueryParam("installationEndDate") String installationEndDate,
			@QueryParam("startLimit") int startLimit,
			@QueryParam("endLimit") int endLimit,
			@QueryParam("limitFlag") String limitFlag
			) throws Exception {
		System.out.println("tenancyId .." + tenancyId);
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		infoLogger.info("--------Webservice input------");
		infoLogger.info("tenenacyId .: " + tenancyId, " vin: " + vin, " platform: "
				+ platform + " model: " + model + " dealerName: " + dealerName
				+ " customerName: " + customerName+" premiumFlag: "+premiumFlag
				+ " premiumStartDate: " + premiumStartDate+" premiumEndDate: "+premiumEndDate+" installationStartDate: "+installationStartDate+ " installationEndDate: "+installationEndDate +" limitFlag: "+limitFlag);
		CommonUtil util = new CommonUtil();
		String isValidinput= util.inputFieldValidation(String.valueOf(tenancyId));
		String isValidinput1 = util.inputFieldValidation(String.valueOf(vin));
		String isValidinput2 = util.inputFieldValidation(String.valueOf(platform));
		String isValidinput3 = util.inputFieldValidation(String.valueOf(model));
		String isValidinput4 = util.inputFieldValidation(String.valueOf(dealerName));
		String isValidinput5 = util.inputFieldValidation(String.valueOf(customerName));
		String isValidinput7 = util.inputFieldValidation(String.valueOf(premiumFlag));
		String isValidinput8 = util.inputFieldValidation(String.valueOf(premiumStartDate));
		String isValidinput9 = util.inputFieldValidation(String.valueOf(premiumEndDate));
		String isValidinput10 = util.inputFieldValidation(String.valueOf(installationStartDate));
		String isValidinput11 = util.inputFieldValidation(String.valueOf(installationEndDate));
		String isValidinput12 = util.inputFieldValidation(String.valueOf(limitFlag));
		if(!isValidinput.equals("SUCCESS") || !isValidinput1.equals("SUCCESS")|| !isValidinput2.equals("SUCCESS")|| !isValidinput3.equals("SUCCESS")
				|| !isValidinput4.equals("SUCCESS")|| !isValidinput5.equals("SUCCESS")|| !isValidinput7.equals("SUCCESS")
				|| !isValidinput8.equals("SUCCESS")|| !isValidinput9.equals("SUCCESS")|| !isValidinput10.equals("SUCCESS") || !isValidinput11.equals("SUCCESS") || !isValidinput12.equals("SUCCESS")){
			fLogger.info("Invalid input parameter");
			throw new CustomFault(isValidinput);
		}
		
		if( premiumFlag.equalsIgnoreCase("1") && !premiumStartDate.equalsIgnoreCase("null") && premiumStartDate != null && premiumStartDate != "NA"
				&& (premiumEndDate.equalsIgnoreCase("null") || premiumEndDate == null || premiumEndDate == "NA")){ 
			Timestamp currentTime = new Timestamp(new Date().getTime());
			String currentTimeInString = new SimpleDateFormat("yyyy-MM-dd").format(currentTime);
			premiumEndDate = currentTimeInString ;
		
		}
		if( premiumFlag.equalsIgnoreCase("0") && !installationStartDate.equalsIgnoreCase("null") && installationStartDate != null && installationStartDate != "NA"
				&& (installationEndDate.equalsIgnoreCase("null") || installationEndDate == null || installationEndDate == "NA")){ 
			Timestamp currentTime = new Timestamp(new Date().getTime());
			String currentTimeInString = new SimpleDateFormat("yyyy-MM-dd").format(currentTime);
			installationEndDate = currentTimeInString ;
		
		}
		List<HashMap<String, String>> response = new LLPremiumServiceImpl()
				.getMachineListUnderTenancyId(tenancyId, vin, platform, model,
						dealerName, customerName, premiumFlag ,premiumStartDate,premiumEndDate, installationStartDate , installationEndDate , startLimit , endLimit , limitFlag);
		infoLogger.info("result..response." + response);
		return response;

	}

	@POST
	@Path("/setSubscription")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_JSON)
	public String setSubscription(HashMap<String, String> jsonData)
			throws Exception {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		infoLogger.info("Webservice input : " + jsonData);
		String userId = (String) jsonData.get("userId");

		if (userId == null || userId == "" || userId == "null") {
			infoLogger.info("userId is null");
			return "failure";
		}
		
		String response = new LLPremiumServiceImpl().setSubscription(jsonData);
		infoLogger.info("Webservice output : " + response);
		return response;

	}
	
	@GET
	@Path("/sendEmail")
	@Produces(MediaType.APPLICATION_JSON)
	public String sendEmailToDealer(
			@QueryParam("vin") String vin) throws Exception {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		infoLogger.info("--------Webservice input------");
		infoLogger.info(" vin: " + vin);
		CommonUtil util = new CommonUtil();
		String isValidinput1 = util.inputFieldValidation(String.valueOf(vin));
		if(!isValidinput1.equals("SUCCESS")){
			fLogger.info("Invalid input parameter");
			throw new CustomFault(isValidinput1);
		}
		

		String response = new LLPremiumServiceImpl().sendEmailToDealer(vin);
		return response;

	}
	
	@GET
	@Path("/removeSubscription")
	@Produces(MediaType.APPLICATION_JSON)
	public String removeSubscription(
			@QueryParam("date") String date) throws Exception {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		infoLogger.info("--------Webservice input------");
		infoLogger.info(" removeSubscription date: " + date);
		CommonUtil util = new CommonUtil();
		String isValidinput1 = util.inputFieldValidation(String.valueOf(date));
		if(!isValidinput1.equals("SUCCESS")){
			fLogger.info("Invalid input parameter");
			throw new CustomFault(isValidinput1);
		}
		

		String response = new LLPremiumServiceImpl().removeSubscription(date);
		return response;

	}
	
	@GET
	@Path("/updateSubscription")
	@Produces(MediaType.APPLICATION_JSON)
	public String updateSubscription(
			@QueryParam("date") String date) throws Exception {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		infoLogger.info("--------Webservice input------");
		infoLogger.info(" updateSubscription date: " + date);
		CommonUtil util = new CommonUtil();
		String isValidinput1 = util.inputFieldValidation(String.valueOf(date));
		if(!isValidinput1.equals("SUCCESS")){
			fLogger.info("Invalid input parameter");
			throw new CustomFault(isValidinput1);
		}
		

		String response = new LLPremiumServiceImpl().batchUpdateSubscription(date);
		return response;

	}
	
	
	@GET
	@Path("/getPremiumMachinesCount")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPremiumMachinesCount(
			@QueryParam("tenancyId") String tenancyId) throws Exception {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		infoLogger.info("--------Webservice input------");
		infoLogger.info(" tenancyId: " + tenancyId);
		CommonUtil util = new CommonUtil();
		String isValidinput1 = util.inputFieldValidation(String.valueOf(tenancyId));
		if(!isValidinput1.equals("SUCCESS")){
			fLogger.info("Invalid input parameter");
			throw new CustomFault(isValidinput1);
		}
		

		String response = new LLPremiumServiceImpl().getPremiumMachinesCount(tenancyId);
		return response;

	}

	@GET
	@Path("/getPremiumStatus")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPremiumStatus(
			@QueryParam("tenancyId") String tenancyId,
			@QueryParam("vin") String vin) throws Exception {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		infoLogger.info("--------Webservice input------");
		infoLogger.info(" tenancyId: " + tenancyId+" vin: " + vin);
		CommonUtil util = new CommonUtil();
		String isValidinput1 = util.inputFieldValidation(String.valueOf(tenancyId));
		String isValidinput2 = util.inputFieldValidation(String.valueOf(vin));
		if(!isValidinput1.equals("SUCCESS")){
			fLogger.info("Invalid tenancy input parameter");
			throw new CustomFault(isValidinput1);
		}
		if(!isValidinput2.equals("SUCCESS")){
			fLogger.info("Invalid vin input parameter");
			throw new CustomFault(isValidinput2);
		}

		String response = new LLPremiumServiceImpl().getPremiumStatus(tenancyId,vin);
		return response;

	}
	
	@GET
	@Path("/getPremiumModelsList")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getPremiumModelsList() throws Exception {
		Logger infoLogger = InfoLoggerClass.logger;
		CommonUtil util = new CommonUtil();
		
		infoLogger.info("Webservice start block of getPremiumModelsList");
		List<String> response = new LLPremiumServiceImpl().getPremiumModelsList();
		return response;

	}
	
	@GET
	@Path("/getPremiumModelNamesList")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String , String>  getPremiumModelNamesList(@QueryParam("profileID") int profileID) throws Exception {
		CommonUtil util = new CommonUtil();
		Map<String , String>  response = new LLPremiumServiceImpl().getPremiumModelsNamesList(profileID);
		return response;

	}
	
	@GET
	@Path("/getDelearsNames")
	@Produces(MediaType.APPLICATION_JSON)
	public LinkedHashMap<Integer ,String> getDelearsNames() throws Exception {
		Logger infoLogger = InfoLoggerClass.logger;
		LinkedHashMap<Integer ,String> response = new LLPremiumServiceImpl().getDelearNames();
		return response;

	}

	@GET
	@Path("/getCustomerForDealer")
	@Produces(MediaType.APPLICATION_JSON)
	public LinkedHashMap<Integer, String> getCustomerForDealer(@QueryParam("dealerTenancyId") int dealerTenancyId) throws Exception {
		Logger infoLogger = InfoLoggerClass.logger;
		LinkedHashMap<Integer, String> response = new CustomersUnderDealerImpl().getCustomerForDealerForRest(dealerTenancyId);
		return response;

	}
	
	@GET
	@Path("/getLLPremiumSubsReport")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LinkedHashMap<String, String>> getPremiumSubReport(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate,@QueryParam("dateType") String dateType) throws Exception {
		Logger infoLogger = InfoLoggerClass.logger;
		List<LinkedHashMap<String, String>> response = new LLPremiumServiceImpl().getSubPremiumReport(startDate,endDate,dateType);
		return response;

	}
	
	@GET
	@Path("/getPremiumSubALLReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPremiumSubALLReport() throws Exception {
		Logger infoLogger = InfoLoggerClass.logger;
		String response = new LLPremiumReportImpl().insertIntoCSVFile();
		return response;

	}
	@GET
	@Path("/getPremiumEligibleReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPremiumEligibleReport() throws Exception {
		Logger infoLogger = InfoLoggerClass.logger;
		String response = new LLPremiumReportImpl().reportForLLPremiumEligibleVins();
		return response;

	}
	
	@GET
	@Path("/vinsListForMDAReports")
	@Produces(MediaType.APPLICATION_JSON)
	//Sending VIN list to MDAReports whose Premium Flag is one
	public List<String> vinsListForMDAReports(@QueryParam("startDate") String startDate) throws Exception {
		List<String> response = new LLPremiumServiceImpl().vinsListForMDAReportsIMPL(startDate);
		return response;

	}
	//ME100011624-20240506-LLPremium to online
	@POST
	@Path("/getDealerLLPremiumEligibleReport")
	//@Path("/getDealerLLPremiumSubAllReport")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String,String>> getDealerPremiumEligibleReport(@JsonProperty LinkedHashMap<String,Object> reqObj) throws Exception {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<HashMap<String,String>> response = new LinkedList<HashMap<String,String>>();
		try{
			long startTime = System.currentTimeMillis();
			infoLogger.info("Webservice input :"+reqObj);
			
	//Security checks for all input fields	
			CommonUtil util = new CommonUtil();
			String isValidinput=null;
			ListToStringConversion convert=new ListToStringConversion();

			for(int i=0;i<reqObj.size();i++)
			{
				if(reqObj.get("tenancyIdList")!=null){
					List<Integer> tenacnyList=(List<Integer>) reqObj.get("tenancyIdList");
					String tenacnyListString=convert.getIntegerListString(tenacnyList).toString();
					isValidinput = util.inputFieldValidation(tenacnyListString);
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
			}
		 response = new LLPremiumReportImpl().reportForDealerLLPremiumEligibleVins(reqObj);
		 long endTime = System.currentTimeMillis(); 
	        long executionTime = endTime - startTime; // Calculate the execution time
	        infoLogger.info("serviceName:LLPremiumService~Execution time: " + executionTime + " milliseconds");
	}
		catch(Exception e){
			e.printStackTrace();
			fLogger.error("Exception:"+e.getMessage());
		}
		return response;
	}
	
	@POST
	@Path("/getDealerLLPremiumSubsReport")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String,String>> getDealerPremiumReport(@JsonProperty LinkedHashMap<String,Object> reqObj,@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate,@QueryParam("dateType") String dateType) throws Exception {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		infoLogger.info(startDate);
		List<HashMap<String,String>> response = new LinkedList<HashMap<String,String>>();
		
		try{
			long startTime = System.currentTimeMillis();
			infoLogger.info("Webservice input :"+reqObj);
			CommonUtil util = new CommonUtil();
			String isValidinput=null;
			ListToStringConversion convert= new ListToStringConversion();

			for(int i=0;i<reqObj.size();i++)
			{
				if(reqObj.get("tenancyIdList")!=null){
					List<Integer> tenacnyList=(List<Integer>) reqObj.get("tenancyIdList");
					String tenacnyListString=convert.getIntegerListString(tenacnyList).toString();
					isValidinput = util.inputFieldValidation(tenacnyListString);
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
			}
		 response = new LLPremiumReportImpl().reportForDealerLLPremiumVins(reqObj,startDate,endDate,dateType);
		 long endTime = System.currentTimeMillis(); 
	        long executionTime = endTime - startTime; // Calculate the execution time
	        infoLogger.info("serviceName:LLPremiumService~Execution time: " + executionTime + " milliseconds");
	}
		catch(Exception e){
			e.printStackTrace();
			fLogger.error("Exception:"+e.getMessage());
		}
		return response;
	}	
}
