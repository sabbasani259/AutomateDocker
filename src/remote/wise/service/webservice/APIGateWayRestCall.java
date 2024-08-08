package remote.wise.service.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import remote.wise.businessentity.TenancyEntity;
import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.businessobject.FuelUtilizationDetailBO;
import remote.wise.businessobject.LandmarkCategoryBO;
import remote.wise.businessobject.MachineCountDetailsBO;
import remote.wise.businessobject.ReportDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.FuelUtilizationDetailRespContract;
import remote.wise.service.datacontract.UtilizationDetailServiceRespContract;
import remote.wise.service.implementation.AssetExtendedImpl;
import remote.wise.service.implementation.FuelUtilizationDetailImpl;
import remote.wise.service.implementation.UtilizationDetailReportImpl;
import remote.wise.service.implementation.UtilizationDetailServiceImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ListToStringConversion;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


@Path("/ApiGateway")	
public class APIGateWayRestCall 
{
	Logger iLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;
	//=================================================================================================	
	@GET
	@Path("/EngineUtilization")
	@Produces("text/plain")
	public String EngineUtilization(@QueryParam("loginId")String loginId,@QueryParam("vin")String vin,@QueryParam("period")String period)
	{
		Gson gson = new Gson();
		String result=null;
		try {
			List<String> serNumList = new LinkedList<String>();
			List<UtilizationDetailServiceImpl> implResponseList = new LinkedList<UtilizationDetailServiceImpl>();
			serNumList.add(vin);

			List<UtilizationDetailReportImpl> implList = new ReportDetailsBO().getFleetUtilizationDetails(
					loginId, null, null, null, null, period, serNumList);

			for (int i = 0; i < implList.size(); i++) {
				UtilizationDetailServiceImpl response = new UtilizationDetailServiceImpl();
				response.setSerialNumber(implList.get(i).getSerialNumber());
				response.setTimeMachineStatusMap(implList.get(i)
						.getTimeMachineStatusMap());
				response.setTimeDuration(implList.get(i).getDateInString());					
				implResponseList.add(response);
			}			

			for(int i=0; i<implResponseList.size(); i++)
			{
				UtilizationDetailServiceRespContract response = new UtilizationDetailServiceRespContract();

				response.setPeriod(implResponseList.get(i).getPeriod());
				response.setSerialNumber(implResponseList.get(i).getSerialNumber());
				response.setTimeDuration(implResponseList.get(i).getTimeDuration());
				response.setTimeMachineStatusMap(implResponseList.get(i).getTimeMachineStatusMap());
				response.setWorkingHourPerct(implResponseList.get(i).getWorkingHourPerct());

				result = gson.toJson(
						response,
						new TypeToken<UtilizationDetailServiceRespContract>() {
						}.getType()).toString();
			}

		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.info("Error in ApiGateway EngineUtilization : ",e);
		}
		iLogger.info("ApiGateway EngineUtilization : Result : "+result);
		return result;
	}

	//=================================================================================================	

	@GET
	@Path("/FuelUtilization")
	@Produces("text/plain")
	public String FuelUtilization(@QueryParam("loginId")String loginId,@QueryParam("vin")String vin,@QueryParam("period")String period)
	{
		String result=null;
		Gson gson = new Gson();
		List<FuelUtilizationDetailImpl> fuelUtilizationDetailImpl = new LinkedList<FuelUtilizationDetailImpl>();
		List<FuelUtilizationDetailRespContract> respList = new LinkedList<FuelUtilizationDetailRespContract>();
		try
		{
			fuelUtilizationDetailImpl=new FuelUtilizationDetailBO().getFuelUtilizationDetailList(period, loginId, vin);

			for(int i=0;i<fuelUtilizationDetailImpl.size();i++)
			{	

				FuelUtilizationDetailRespContract  respContractObj=new FuelUtilizationDetailRespContract();
				respContractObj.setSerialNumber(fuelUtilizationDetailImpl.get(i).getSerialNumber());
				respContractObj.setPeriod(fuelUtilizationDetailImpl.get(i).getPeriod());
				respContractObj.setHourFuelLevelMap(fuelUtilizationDetailImpl.get(i).getHourFuelLevelMap());
				respList.add(respContractObj);

			}

			for(int i=0; i<respList.size(); i++)
			{
				FuelUtilizationDetailRespContract response = new FuelUtilizationDetailRespContract();

				response.setPeriod(respList.get(i).getPeriod());
				response.setSerialNumber(respList.get(i).getSerialNumber());
				response.setHourFuelLevelMap(respList.get(i).getHourFuelLevelMap());
				response.setSerialNumber(respList.get(i).getSerialNumber());

				result = gson.toJson(
						response,
						new TypeToken<FuelUtilizationDetailRespContract>() {
						}.getType()).toString();
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.info("Error in ApiGateway FuelUtilization : ",e);
		}
		iLogger.info("ApiGateway FuelUtilization : Result : "+result);
		return result;
	}
	//=================================================================================================	

	@GET
	@Path("/SetLandmarkCategory")
	@Produces("text/plain")
	public String SetLandmarkCategory(@QueryParam("Landmark_Category_ID")int Landmark_Category_ID,@QueryParam("Landmark_Category_Name")String Landmark_Category_Name,
			@QueryParam("Color_Code")String Color_Code,@QueryParam("inputTenancyId")int inputTenancyId,@QueryParam("Landmark_Category_Color_Code")String Landmark_Category_Color_Code)
	{
		String result=null;
		try
		{
			result=new LandmarkCategoryBO().setLandmarkCategory(Landmark_Category_ID, Landmark_Category_Name, Color_Code, inputTenancyId, Landmark_Category_Color_Code);

		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.info("Error in ApiGateway GeoFence : ",e);
		}
		iLogger.info("ApiGateway GeoFence : Result : "+result);
		return result;
	}


	//=================================================================================================	

	@GET
	@Path("/GetLandmarkCategory")
	@Produces("text/plain")
	public String GetLandmarkCategory(@QueryParam("login_id")String login_id,@QueryParam("inputTenancyId")int inputTenancyId)
	{
		List<LandmarkCategoryBO> result=new ArrayList<LandmarkCategoryBO>();
		List<HashMap<String,Object>>b=new ArrayList<HashMap<String,Object>>();
		String jsonStr=null;
//		List<Object> b=new LinkedList<Object>();
		try
		{
			result=new LandmarkCategoryBO().getLandmarkCategoryBOList(login_id, inputTenancyId);
			HashMap<String,Object>a= new HashMap<String,Object>();
//			ObjectMapper mapperObj = new ObjectMapper();
			try 
			{
			for(int i=0;i<result.size();i++)
			{
				TenancyEntity tenanacy = result.get(i).getTenancy_ID();
				a.put("Tenancy_ID", tenanacy.getTenancy_id());
				a.put("Landmark_id", result.get(i).getLandmark_id());
				a.put("Landmark_Category_ID", result.get(i).getLandmark_Category_ID());
				a.put("Landmark_Name", result.get(i).getLandmark_Name());
				a.put("Latitude", result.get(i).getLatitude());
				a.put("Longitude", result.get(i).getLongitude());
				a.put("Radius", result.get(i).getRadius());
				a.put("Address", result.get(i).getAddress());
				a.put("IsArrival", result.get(i).getIsArrival());
				a.put("IsDeparture", result.get(i).getIsDeparture());
				a.put("Landmark_Category_Name", result.get(i).getLandmark_Category_Name());
				a.put("Landmark_Category_Color_Code", result.get(i).getLandmark_Category_Color_Code());
				b.add(a);
			}
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.info("Error in ApiGateway GeoFence : ",e);
		}
		
		iLogger.info("ApiGateway GeoFence : Result : "+b.toString());
		List<JSONObject> jsonObj = new ArrayList<JSONObject>();

		for(HashMap<String, Object> data : b) {
		    JSONObject obj = new JSONObject(data);
		    jsonObj.add(obj);
		}

		JSONArray test = new JSONArray(jsonObj);

		return test.toString();
	}

	//=================================================================================================	

	@GET
	@Path("/SetLandmarkDetails")
	@Produces("text/plain")
	public String setLandmarkDetails(@QueryParam("login_id")String login_id,@QueryParam("Landmark_id")int Landmark_id,
			@QueryParam("Landmark_Category_ID")int Landmark_Category_ID,@QueryParam("Landmark_Name")String Landmark_Name
			,@QueryParam("Landmark_Category_Name")String Landmark_Category_Name,
			@QueryParam("Latitude")String Latitude,@QueryParam("Longitude")String Longitude
			,@QueryParam("Radius")Double Radius,@QueryParam("Address")String Address
			,@QueryParam("IsArrival")int IsArrival,@QueryParam("IsDeparture")int IsDeparture)
	{
		int result=0;
		try
		{
			result=new LandmarkCategoryBO().setLandmarkDetails(login_id, Landmark_id, Landmark_Category_ID, 
					Landmark_Name, Landmark_Category_Name, Latitude, 
					Longitude, Radius, Address, IsArrival, IsDeparture);
		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.info("Error in ApiGateway GeoFence : ",e);
		}
		iLogger.info("ApiGateway GeoFence : Result : "+result);

		return  Integer.toString(result);
	}


	//=================================================================================================	

	@GET
	@Path("/setTimeFence")
	@Produces("text/plain")
	public String setTimeFence(@QueryParam("serial_number")String serial_number,@QueryParam("OperatingStarttime")String OperatingStarttime,
			@QueryParam("OperatingEndtime")String OperatingEndtime)
	{
		String result=null;
		try
		{
			result=new 	AssetDetailsBO().setAssetExtendedDetails(serial_number, OperatingStarttime, OperatingEndtime, 
					null, null, null, null, null, null,"");

		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.info("Error in ApiGateway setTimeFence : ",e);
		}
		iLogger.info("ApiGateway setTimeFence : Result : "+result);

		return  result;
	}

	//=================================================================================================

	@GET
	@Path("/getTimeFence")
	@Produces("text/plain")
	public String getTimeFence(@QueryParam("serial_number")String serial_number)
	{
		AssetExtendedImpl result = null;
		List<HashMap<String,Object>>b=new ArrayList<HashMap<String,Object>>();
		try
		{
			result=new 	AssetDetailsBO().getAssetExtendedDetails(serial_number);
			HashMap<String,Object>a= new HashMap<String,Object>();

			if(result.getOperatingStartTime()!=null && ! result.getOperatingStartTime().equalsIgnoreCase(""))
			{			
				a.put("OperatingStartTime",result.getOperatingStartTime());
			}
			else
			{
				a.put("OperatingStartTime","NA");
			}
			
			if(result.getOperatingEndTime()!=null && ! result.getOperatingEndTime().equalsIgnoreCase(""))
			{
			a.put("OperatingEndTime",result.getOperatingEndTime());
			}
			else
			{
				a.put("OperatingEndTime","NA");
			}
			
			if(result.getUsageCategory()!=null && ! result.getUsageCategory().equalsIgnoreCase(""))
			{
			a.put("UsageCategory",result.getUsageCategory());
			}
			else
			{
				a.put("UsageCategory","NA");
			}
			
			
			if(result.getOffset()!=null && ! result.getOffset().equalsIgnoreCase(""))
			{
			a.put("Offset",result.getOffset());
			}
			else
			{
				a.put("Offset","NA");
			}
			
			if(result.getDriverName()!=null && ! result.getDriverName().equalsIgnoreCase(""))
			{
			a.put("DriverName",result.getDriverName());
			}
			else
			{
				a.put("DriverName","NA");
			}
			
			if(result.getDriverContactNumber()!=null && ! result.getDriverContactNumber().equalsIgnoreCase(""))
			{
			a.put("DriverContactNumber",result.getDriverContactNumber());
			}
			else
			{
				a.put("DriverContactNumber","NA");
			}
			
			if(result.getNotes()!=null && ! result.getNotes().equalsIgnoreCase(""))
			{
			a.put("notes",result.getNotes());
			}
			else
			{
				a.put("notes","NA");
			}
			
			a.put("primaryOwnerId",result.getPrimaryOwnerId());
			
			
			b.add(a);

		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.info("Error in ApiGateway setTimeFence : ",e);
		}
		
		iLogger.info("ApiGateway setTimeFence : Result : "+result);
		
		List<JSONObject> jsonObj = new ArrayList<JSONObject>();

		for(HashMap<String, Object> data : b) {
		    JSONObject obj = new JSONObject(data);
		    jsonObj.add(obj);
		}

		JSONArray test = new JSONArray(jsonObj);

		return test.toString();
		
	}
	
	//DF20190207: ABHISHEK :: TO RETURN COUNT OF MACHINE=========================================================================================		
		@GET
		@Path("/getMachineCount")
		@Produces("text/plain")
		public HashMap<String, Integer> getMachineCount(@QueryParam("accountIdList") List<String> accountIdList) throws CustomFault
		{

			Logger iLogger = InfoLoggerClass.logger;
			CommonUtil util = new CommonUtil();

			//Input validation to avoid sql injection.
			ListToStringConversion convert=new ListToStringConversion();
			String accountListString=convert.getStringList(accountIdList).toString();
			//String accountListString=convert.getIntegerListString(accountIdList).toString();
			String isValidinput = util.inputFieldValidation(accountListString);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}

			HashMap<String,Integer> response = new HashMap<String,Integer>();

			response = new MachineCountDetailsBO().getMachineCount(accountIdList);
			
			iLogger.info("ApiGateway getMachineCount : Result : "+response);
			return response;
		}
		//=================================================================================================	

}


