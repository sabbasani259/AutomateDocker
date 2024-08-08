/**
 * 
 */
package remote.wise.service.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.MADFleetServiceDetailsImpl;
import remote.wise.service.implementation.MAD_EngineIdlingImpl;
import remote.wise.service.implementation.MAD_MachineParkImpl;
import remote.wise.service.implementation.MAD_MachineUtilizationImpl;
import remote.wise.service.implementation.MAD_OpenAlertsImpl;
import remote.wise.service.implementation.MAD_RPMImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author ROOPN5
 *
 */
@Path("/MADashBoardDetailsRESTService")
public class MADashBoardDetailsRESTService {

	Gson gson = new Gson();

	@POST
	@Path("getMADMachineParkDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({MediaType.APPLICATION_JSON})
	public String getMADMachineParkDetails(LinkedHashMap<String,Object> reqObj){


		Logger iLogger = InfoLoggerClass.logger;
		List<HashMap<String,Object>> response =new ArrayList<HashMap<String,Object>>();
		String result=null;

		List tenancyIdList = (List) reqObj.get("tenancyIdList");
		String loginID = (String)reqObj.get("loginID");
		List profileIdList = (List) reqObj.get("profileIdList");
		String DetailedView = (String)reqObj.get("DetailedView");

		iLogger.info("MADashBoardDetailsRESTService:getMADMachineParkDetails WebService Input-----> loginID:"+loginID+" tenancyIdList:"+tenancyIdList+" profileIdList:"+profileIdList);
		long startTime = System.currentTimeMillis();


		if(loginID==null){


			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "loginID is mandatory.");
			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}
		if(tenancyIdList==null){



			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "tenancyIdList is mandatory.");
			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}

		response = new MAD_MachineParkImpl().getMachineParkDetails(loginID, tenancyIdList, profileIdList,DetailedView);

		if(response!=null)
			result = gson.toJsonTree(
					response,
					new TypeToken<List<HashMap<String,Object>>>() {
					}.getType()).toString();

		long endTime=System.currentTimeMillis();
		iLogger.info("MADashBoardDetailsRESTService:getMADMachineParkDetails WebService Output -----> ");
		iLogger.info("serviceName:MADashBoardDetailsRESTService~executionTime:"+(endTime-startTime)+"~"+loginID+"~"+result);
		return result;



	}


	@POST
	@Path("getMADOpenAlertDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({MediaType.APPLICATION_JSON})
	public String getMADOpenAlertDetails(LinkedHashMap<String,Object> reqObj){


		Logger iLogger = InfoLoggerClass.logger;
		List<HashMap<String,Object>> response =new ArrayList<HashMap<String,Object>>();
		String result=null;

		int pageNumber = 0;
		int pageSize = 0;

		List tenancyIdList = (List) reqObj.get("tenancyIdList");
		String loginID = (String)reqObj.get("loginID");

		if(reqObj.get("pageNumber")!=null)
			pageNumber = (Integer) reqObj.get("pageNumber");
		if(reqObj.get("pageSize")!=null)
			pageSize = (Integer) reqObj.get("pageSize");


		iLogger.info("MADashBoardDetailsRESTService:getMADOpenAlertDetails WebService Input-----> loginID:"+loginID+" tenancyIdList:"+tenancyIdList+" pageNumber:"+pageNumber+ " pageSize:"+pageSize);
		long startTime = System.currentTimeMillis();


		if(loginID==null){


			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "loginID is mandatory.");
			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}
		if(tenancyIdList==null){



			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "tenancyIdList is mandatory.");
			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}

		response = new MAD_OpenAlertsImpl().getMADOpenAlertDetails(loginID, tenancyIdList, pageNumber, pageSize);

		if(response!=null)
			result = gson.toJsonTree(
					response,
					new TypeToken<List<HashMap<String,Object>>>() {
					}.getType()).toString();

		long endTime=System.currentTimeMillis();
		iLogger.info("MADashBoardDetailsRESTService:getMADOpenAlertDetails WebService Output ----->");
		iLogger.info("serviceName:MADashBoardDetailsRESTService~executionTime:"+(endTime-startTime)+"~"+loginID+"~"+result);
		return result;



	}


	@POST
	@Path("getMADMachineUtilizationDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({MediaType.APPLICATION_JSON})
	public String getMADMachineUtilizationDetails(LinkedHashMap<String,Object> reqObj){


		Logger iLogger = InfoLoggerClass.logger;
		List<HashMap<String,Object>> response =new ArrayList<HashMap<String,Object>>();
		String result=null;

		List tenancyIdList = (List) reqObj.get("tenancyIdList");
		String accountType=(String)reqObj.get("accountType");
		String loginID = (String)reqObj.get("loginID");
		List profileCodeList = (List) reqObj.get("profileCodeList");
		String startDate=(String)reqObj.get("startDate");
		String endDate=(String)reqObj.get("endDate");

		String DetailedView = (String)reqObj.get("DetailedView");

		iLogger.info("MADashBoardDetailsRESTService:getMADMachineUtilizationDetails WebService Input-----> loginID:"+loginID+" tenancyIdList:"+tenancyIdList+" profileCodeList:"+profileCodeList+" startDate:"+startDate+" endDate:"+endDate+" accountType:"+accountType);
		long startTime = System.currentTimeMillis();


		if(loginID==null){


			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "loginID is mandatory.");
			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}
		if(tenancyIdList==null){



			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "tenancyIdList is mandatory.");
			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}

		if(startDate==null || endDate==null){



			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "startDate and  endDate is mandatory.");
			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}

		if(accountType==null){



			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "accountType is mandatory.");
			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}

		response = new MAD_MachineUtilizationImpl().getMachineUtilizationDetails(loginID, tenancyIdList, profileCodeList,startDate,endDate,accountType,DetailedView);

		if(response!=null)
			result = gson.toJsonTree(
					response,
					new TypeToken<List<HashMap<String,Object>>>() {
					}.getType()).toString();

		long endTime=System.currentTimeMillis();
		iLogger.info("MADashBoardDetailsRESTService:getMADMachineUtilizationDetails WebService Output -----> ");
		iLogger.info("serviceName:MADashBoardDetailsRESTService~executionTime:"+(endTime-startTime)+"~"+loginID+"~"+result);
		
		return result;



	}

	@POST
	@Path("getMADEngineIdlingDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({MediaType.APPLICATION_JSON})
	public String getMADEngineIdlingDetails(LinkedHashMap<String,Object> reqObj){


		Logger iLogger = InfoLoggerClass.logger;
		List<HashMap<String,Object>> response =new ArrayList<HashMap<String,Object>>();
		String result=null;

		List tenancyIdList = (List) reqObj.get("tenancyIdList");
		String accountType=(String)reqObj.get("accountType");
		String loginID = (String)reqObj.get("loginID");
		List profileCodeList = (List) reqObj.get("profileCodeList");
		String startDate=(String)reqObj.get("startDate");
		String endDate=(String)reqObj.get("endDate");
		String DetailedView = (String)reqObj.get("DetailedView");

		iLogger.info("MADashBoardDetailsRESTService:getMADEngineIdlingDetails WebService Input-----> loginID:"+loginID+" tenancyIdList:"+tenancyIdList+" profileCodeList:"+profileCodeList+" startDate:"+startDate+" endDate:"+endDate+" accountType:"+accountType);
		long startTime = System.currentTimeMillis();


		if(loginID==null){


			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "loginID is mandatory.");
			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}
		if(tenancyIdList==null){



			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "tenancyIdList is mandatory.");
			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}

		if(startDate==null || endDate==null){



			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "startDate and  endDate is mandatory.");
			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}

		if(accountType==null){



			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "accountType is mandatory.");
			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}

		response = new MAD_EngineIdlingImpl().getEngineIdlingDetails(loginID, tenancyIdList, profileCodeList,startDate,endDate,accountType,DetailedView);

		if(response!=null)
			result = gson.toJsonTree(
					response,
					new TypeToken<List<HashMap<String,Object>>>() {
					}.getType()).toString();

		long endTime=System.currentTimeMillis();
		iLogger.info("MADashBoardDetailsRESTService:getMADEngineIdlingDetails WebService Output -----> ");
		iLogger.info("serviceName:MADashBoardDetailsRESTService~executionTime:"+(endTime-startTime)+"~"+loginID+"~"+result);
		
		return result;



	}

	@POST
	@Path("getMADRPMDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({MediaType.APPLICATION_JSON})
	public String getMADRPMDetails(LinkedHashMap<String,Object> reqObj){


		Logger iLogger = InfoLoggerClass.logger;
		List<HashMap<String,Object>> response =new ArrayList<HashMap<String,Object>>();
		String result=null;

		List tenancyIdList = (List) reqObj.get("tenancyIdList");
		String accountType=(String)reqObj.get("accountType");
		String loginID = (String)reqObj.get("loginID");
		List profileCodeList = (List) reqObj.get("profileCodeList");
		String startDate=(String)reqObj.get("startDate");
		String endDate=(String)reqObj.get("endDate");
		String DetailedView = (String)reqObj.get("DetailedView");

		iLogger.info("MADashBoardDetailsRESTService:getMADRPMDetails WebService Input-----> loginID:"+loginID+" tenancyIdList:"+tenancyIdList+" profileCodeList:"+profileCodeList+" startDate:"+startDate+" endDate:"+endDate+" accountType:"+accountType);
		long startTime = System.currentTimeMillis();


		if(loginID==null){


			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "loginID is mandatory.");
			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}
		if(tenancyIdList==null){



			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "tenancyIdList is mandatory.");
			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}

		if(startDate==null || endDate==null){



			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "startDate and  endDate is mandatory.");
			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}

		if(accountType==null){



			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "accountType is mandatory.");
			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}

		response = new MAD_RPMImpl().getRPMDetails(loginID, tenancyIdList, profileCodeList,startDate,endDate,accountType,DetailedView);

		if(response!=null)
			result = gson.toJsonTree(
					response,
					new TypeToken<List<HashMap<String,Object>>>() {
					}.getType()).toString();

		long endTime=System.currentTimeMillis();
		iLogger.info("MADashBoardDetailsRESTService:getMADRPMDetails WebService Output -----> ");
		iLogger.info("serviceName:MADashBoardDetailsRESTService~executionTime:"+(endTime-startTime)+"~"+loginID+"~"+result);
		
		return result;



	}


	@POST
	@Path("getMADFleetServiceDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({MediaType.APPLICATION_JSON})
	public String getMADFleetServiceDetails(LinkedHashMap<String,Object> reqObj){


		Logger iLogger = InfoLoggerClass.logger;
		List<HashMap<String,Object>> response =new ArrayList<HashMap<String,Object>>();
		String result=null;

		List tenancyIdList = (List) reqObj.get("tenancyIdList");
		String loginID = (String)reqObj.get("loginID");
		String DetailedView = (String)reqObj.get("DetailedView");


		iLogger.info("MADashBoardDetailsRESTService:getMADFleetServiceDetails WebService Input-----> loginID:"+loginID+" tenancyIdList:"+tenancyIdList);
		long startTime = System.currentTimeMillis();


		if(loginID==null){


			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "loginID is mandatory.");


			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;

		}
		if(tenancyIdList==null){


			HashMap<String, Object> respObj1  = new HashMap<String, Object>();
			respObj1.put("Error", "tenancyIdList is mandatory.");


			response.add(respObj1);

			if(response!=null)
				result = gson.toJsonTree(
						response,
						new TypeToken<List<HashMap<String,Object>>>() {
						}.getType()).toString();

			return result;



		}




		response = new MADFleetServiceDetailsImpl().getFleetServiceDetails(loginID, tenancyIdList, DetailedView);

		if(response!=null)
			result = gson.toJsonTree(
					response,
					new TypeToken<List<HashMap<String,Object>>>() {
					}.getType()).toString();

		long endTime=System.currentTimeMillis();
		iLogger.info("MADashBoardDetailsRESTService:getMADFleetServiceDetails WebService Output -----> ");
		iLogger.info("serviceName:MADashBoardDetailsRESTService~executionTime:"+(endTime-startTime)+"~"+loginID+"~"+result);
		
		return result;



	}

}
