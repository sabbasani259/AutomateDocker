/**
 * 
 */
package remote.wise.service.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ExcavatorVisualizationReportImpl;
import remote.wise.util.CommonUtil;

/**
 * @author ROOPN5
 *
 */
@Path("/ExcavatorDashboardRESTService")
public class ExcavatorDashboardRESTService {

	Gson gson = new Gson();

	@GET
	@Path("getExcavatorChartsAndPeriodData")
	@Produces(MediaType.APPLICATION_JSON)
	public String getExcavatorChartsAndPeriodData(@QueryParam("assetID") String assetID, @QueryParam("loginTenancyId") int loginTenancyId,@QueryParam("start_date") String startDate, @QueryParam("end_date") String endDate,@QueryParam("MsgID") String MsgID,@QueryParam("model") String model,@QueryParam("region") String region,@QueryParam("zone") String zone,@QueryParam("dealer") String dealer,@QueryParam("customer") String customer){

		Logger iLogger = InfoLoggerClass.logger;
		HashMap<String,Object> response = null;
		String result=null;

		iLogger.info("ExcavatorDashboardRESTService:getExcavatorChartsAndPeriodData WebService Input-----> serial_number:"+assetID+" start_date:"+startDate+" end_date:"+endDate);
		long startTime = System.currentTimeMillis();

		String msgId=null;
		String VIN=null;

		if(assetID!=null){

			if(loginTenancyId!=0){

				VIN=new CommonUtil().validateVIN(loginTenancyId, assetID);

				if(VIN==null){

					response = new HashMap<String, Object>();

					response.put("Error", "Machine is not under the given user hierarchy.");

					if(response!=null)
						result = gson.toJson(
								response,
								new TypeToken<HashMap<String,Object>>() {
								}.getType()).toString();

					return result;
				}
			}
			else{


				response = new HashMap<String, Object>();

				response.put("Error", "loginTenancyId is mandatory.");

				if(response!=null)
					result = gson.toJson(
							response,
							new TypeToken<HashMap<String,Object>>() {
							}.getType()).toString();

				return result;

			}

			msgId=new CommonUtil().getMsgIdForVIN(assetID);

			if(msgId==null || (! msgId.equalsIgnoreCase("020") && ! msgId.equalsIgnoreCase("002"))){

				response = new HashMap<String, Object>();

				response.put("Error", "The entered VIN is not of provided machine type.");

				if(response!=null)
					result = gson.toJson(
							response,
							new TypeToken<HashMap<String,Object>>() {
							}.getType()).toString();

				return result;
			}

		}

		response = new ExcavatorVisualizationReportImpl().getExcavatorChartsAndPeriodData(assetID,startDate,endDate,MsgID,model,region,zone,dealer,customer);

		if(response!=null)
			result = gson.toJson(
					response,
					new TypeToken<HashMap<String,Object>>() {
					}.getType()).toString();

		long endTime=System.currentTimeMillis();
		iLogger.info("ExcavatorDashboardRESTService:getExcavatorChartsAndPeriodData WebService Output -----> response:"+response+"; Total Time taken in ms:"+(endTime - startTime));
		iLogger.info("serviceName:ExcavatorDashboardRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		
		return result;


	}
	@GET
	@Path("getExcavatorLifeData")
	@Produces(MediaType.APPLICATION_JSON)
	public String getExcavatorLifeData(@QueryParam("assetID") String assetID,@QueryParam("loginTenancyId") int loginTenancyId){

		Logger iLogger = InfoLoggerClass.logger;
		HashMap<String,Object> response = null;

		String result=null;

		iLogger.info("ExcavatorDashboardRESTService:getExcavatorLifeData WebService Input-----> serial_number:"+assetID);
		long startTime = System.currentTimeMillis();
		String msgId=null;
		String VIN=null;

		if(assetID!=null){

			if(loginTenancyId!=0){

				VIN=new CommonUtil().validateVIN(loginTenancyId, assetID);

				if(VIN==null){

					response = new HashMap<String, Object>();

					response.put("Error", "Machine is not under the given user hierarchy.");

					if(response!=null)
						result = gson.toJson(
								response,
								new TypeToken<HashMap<String,Object>>() {
								}.getType()).toString();

					return result;
				}
			}
			else{


				response = new HashMap<String, Object>();

				response.put("Error", "loginTenancyId is mandatory.");

				if(response!=null)
					result = gson.toJson(
							response,
							new TypeToken<HashMap<String,Object>>() {
							}.getType()).toString();

				return result;

			}

			msgId=new CommonUtil().getMsgIdForVIN(assetID);

			if(msgId==null || (! msgId.equalsIgnoreCase("020") && ! msgId.equalsIgnoreCase("002"))){

				response = new HashMap<String, Object>();

				response.put("Error", "The entered VIN is not of provided machine type.");

				if(response!=null)
					result = gson.toJson(
							response,
							new TypeToken<HashMap<String,Object>>() {
							}.getType()).toString();

				return result;
			}

		}

		response = new ExcavatorVisualizationReportImpl().getExcavatorLifeData(assetID);

		if(response!=null)
			result = gson.toJson(
					response,
					new TypeToken<HashMap<String,Object>>() {
					}.getType()).toString();

		long endTime=System.currentTimeMillis();
		iLogger.info("ExcavatorDashboardRESTService:getExcavatorLifeData WebService Output -----> response:"+response+"; Total Time taken in ms:"+(endTime - startTime));
		iLogger.info("serviceName:ExcavatorDashboardRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		
		return result;


	}

	@GET
	@Path("getExcavatorDayWiseData")
	@Produces(MediaType.APPLICATION_JSON)
	public String getExcavatorDayWiseData(@QueryParam("assetID") String assetID,@QueryParam("loginTenancyId") int loginTenancyId, @QueryParam("start_date") String startDate, @QueryParam("end_date") String endDate,@QueryParam("MsgID") String MsgID,@QueryParam("model") String model,@QueryParam("region") String region,@QueryParam("zone") String zone,@QueryParam("dealer") String dealer,@QueryParam("customer") String customer){

		Logger iLogger = InfoLoggerClass.logger;
		List<HashMap<String,Object>> response =new ArrayList<HashMap<String,Object>>();

		String result=null;

		iLogger.info("ExcavatorDashboardRESTService:getExcavatorDayWiseData WebService Input-----> serial_number:"+assetID);
		long startTime = System.currentTimeMillis();

		String msgId=null;
		String VIN=null;

		if(assetID!=null){

			if(loginTenancyId!=0){

				VIN=new CommonUtil().validateVIN(loginTenancyId, assetID);

				if(VIN==null){


					HashMap<String, Object> respObj1  = new HashMap<String, Object>();
					respObj1.put("Error", "Machine is not under the given user hierarchy.");
					response.add(respObj1);

					if(response!=null)
						result = gson.toJsonTree(
								response,
								new TypeToken<List<HashMap<String,Object>>>() {
								}.getType()).toString();

					return result;
				}
			}
			else{


				HashMap<String, Object> respObj1  = new HashMap<String, Object>();
				respObj1.put("Error", "loginTenancyId is mandatory.");
				response.add(respObj1);

				if(response!=null)
					result = gson.toJsonTree(
							response,
							new TypeToken<List<HashMap<String,Object>>>() {
							}.getType()).toString();

				return result;

			}

			msgId=new CommonUtil().getMsgIdForVIN(assetID);

			if(msgId==null || (! msgId.equalsIgnoreCase("020") && ! msgId.equalsIgnoreCase("002"))){


				HashMap<String, Object> respObj1  = new HashMap<String, Object>();
				respObj1.put("Error", "The entered VIN is not of provided machine type.");
				response.add(respObj1);

				if(response!=null)
					result = gson.toJsonTree(
							response,
							new TypeToken<List<HashMap<String,Object>>>() {
							}.getType()).toString();

				return result;
			}

		}

		response = new ExcavatorVisualizationReportImpl().getExcavatorDayWiseData(assetID, startDate, endDate,MsgID,model,region,zone,dealer,customer);

		if(response!=null)
			result = gson.toJsonTree(
					response,
					new TypeToken<List<HashMap<String,Object>>>() {
					}.getType()).toString();

		long endTime=System.currentTimeMillis();
		iLogger.info("ExcavatorDashboardRESTService:getExcavatorDayWiseData WebService Output -----> response:"+response+"; Total Time taken in ms:"+(endTime - startTime));
		iLogger.info("serviceName:ExcavatorDashboardRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		
		return result;


	}

}
