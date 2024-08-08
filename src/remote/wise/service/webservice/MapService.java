package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;


import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.MapReqContract;
import remote.wise.service.datacontract.MapRespContract;
import remote.wise.service.implementation.MapImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;
/**
 *  WebService class to get Map Details
 * @author jgupta41
 *
 */
@WebService(name = "MapService")
public class MapService {

	/**
	 * 	This method gets Map Details that belongs specified LoginId and List of SerialNumber and filters if provided any
	 * @param ReqObj:Get Map Details for specified LoginId and List of SerialNumber and filters if provided any
	 * @return respObj:Returns List of Map Details 
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetMap", action = "GetMap")
	public List<MapRespContract> getMap( @WebParam(name="ReqObj") MapReqContract ReqObj) throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("MapService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		//System.out.println("Current Start Time MAPBO: "+startDate);
		long startTime = System.currentTimeMillis();
		int mapMarkerKey = 0;
		
		List<Integer> linkedTenancyList=new ArrayList<Integer>();
		
		List<MapRespContract> respObj=new ArrayList<MapRespContract>();
		iLogger.info("Map Service:"+ReqObj.getLoginId()+":"+ReqObj.getLoginUserTenancyList()+":"+ReqObj.getSerialNumberList()+":---- Webservice Input ------ Map Service Invokation");
		iLogger.info("Map Service:"+ReqObj.getLoginId()+":"+ReqObj.getLoginUserTenancyList()+":"+ReqObj.getSerialNumberList()+":LoginId:"+ReqObj.getLoginId()+","+"SerialNumberList:"+ReqObj.getSerialNumberList()+","+"AlertSeverityList:"+ReqObj.getAlertSeverityList()+","+"AlertTypeIdList:"+ReqObj.getAlertTypeIdList()+","+"Landmark_IdList:"+ReqObj.getLandmark_IdList()+","+"LandmarkCategory_IdList:"+ReqObj.getLandmarkCategory_IdList()+","+"Tenancy_ID:"+ReqObj.getTenancy_ID()+","+"loginUserTenancyList:"+ReqObj.getLoginUserTenancyList()+","+"machineGroupIdList:"+ReqObj.getMachineGroupIdList()+","+"machineProfileIdList:"+ReqObj.getMachineProfileIdList()+","+"modelIdList:"+ReqObj.getModelIdList()+"");
		//List<MapRespContract> respObj = new MapImpl().getMap(ReqObj);

		//DF20171120:KO369761 - Fetching index of map service call from login id for sending map data chunks wise.

		//Df20171218 @Roopa including country code filter in the map service
		String countryCode=null;

		if(ReqObj.getSerialNumberList()==null || ReqObj.getSerialNumberList().size() == 0){
			//DF20181227 - KO369761 - null check added before parsing mapmarker key.
			if(ReqObj.getLoginId().split("\\|").length > 1){
				if(ReqObj.getLoginId().split("\\|")[1] != null && !ReqObj.getLoginId().split("\\|")[1].equalsIgnoreCase("null")){
					mapMarkerKey = Integer.parseInt(ReqObj.getLoginId().split("\\|")[1]);
				}
			}

			if(ReqObj.getLoginId().split("\\|").length > 2){
				countryCode=ReqObj.getLoginId().split("\\|")[2];
			}

			ReqObj.setLoginId(ReqObj.getLoginId().split("\\|")[0]);
		}

		CommonUtil util = new CommonUtil();
		//text field validation to avoid SQL Injection
		if(ReqObj.getSerialNumberList()!=null && ReqObj.getSerialNumberList().size() > 0){
			String isValidText = util.inputFieldValidation(ReqObj.getSerialNumberList().get(0));
			if(!isValidText.equals("SUCCESS")){
				throw new CustomFault(isValidText);
			}

			String serialNumber = util.validateVIN(ReqObj.getLoginUserTenancyList().get(0), ReqObj.getSerialNumberList().get(0));
			if(serialNumber == null || serialNumber.equalsIgnoreCase("FAILURE")){
				throw new CustomFault("Invalid VIN Number");
			}
		}

		//DF20170919 @Roopa getting decoded UserId
		String UserID=util.getUserId(ReqObj.getLoginId());
		ReqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+ReqObj.getLoginId());
		
		
		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

	if(ReqObj.getLoginUserTenancyList()!=null && ReqObj.getLoginUserTenancyList().size()>0){
		ReqObj.setLoginUserTenancyList(new DateUtil().getLinkedTenancyListForTheTenancy(ReqObj.getLoginUserTenancyList()));
	}
	
	
	//DF20180608 @Roopa Handling when there is no account linked to the selected tenancy, return the list instead of taking the user tenancy
	
	if(ReqObj.getTenancy_ID()!=null && ReqObj.getTenancy_ID().size()>0){
		linkedTenancyList=new DateUtil().getLinkedTenancyListForTheTenancy(ReqObj.getTenancy_ID());
		if(linkedTenancyList!=null && linkedTenancyList.size()>0)
		ReqObj.setTenancy_ID(linkedTenancyList);
		else
			return respObj;	
	}

		 respObj = new MapImpl().getNewMapDetails(ReqObj,mapMarkerKey,countryCode);

		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		//System.out.println("Current End Time MAPBO: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("Map Service:"+":"+ReqObj.getLoginUserTenancyList()+":"+ReqObj.getSerialNumberList()+"");

		iLogger.info("serviceName:MapService~executionTime:"+(endTime-startTime)+"~"+ReqObj.getLoginId()+"~");

		//iLogger.info("----- Webservice Output-----");


		/*for(int i=0;i<respObj.size(); i++)
		{
			infoLogger.info("|"+respObj.get(i).getSerialNumber()+"|"+respObj.get(i).getNickname()+"|"+respObj.get(i).getLatitude()+"|"+
					respObj.get(i).getLongitude()+"|"+respObj.get(i).getEngineStatus()+"|"+respObj.get(i).getTotalMachineHours()+
					"|"+respObj.get(i).getProfileName());

		}*/

		return respObj;
	}

}
