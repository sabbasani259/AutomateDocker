package remote.wise.service.webservice;
//LLOPS-164 : Sai Divya : 20250821 : Soap to Rest
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AssetDashboardReqContract;
import remote.wise.service.datacontract.AssetDashboardRespContract;
import remote.wise.service.implementation.AssetDashboardImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
import remote.wise.util.ListToStringConversion;


/** Webservice to get AssetDashboard details
 * @author Rajani Nagaraju
 *
 */
@Path("AssetDashboardRestService")
public class AssetDashboardRestService 
{
	
	
	/** This method returns the asset dashboard details for the list of serialNumbers accessible to the loggedIn user
	 * @param reqObj Input filters based on which asset dashboard details to be displayed
	 * @return Returns the asset details for the user accessible list of machines and for the given input filters
	 * @throws CustomFault
	 */
	@POST
	@Path("GetAssetDashboardDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	
	public List<AssetDashboardRespContract> getAssetDashboardDetails(AssetDashboardReqContract reqObj) 
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetDashboardService:","info");

		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		List<AssetDashboardRespContract> response=null;
		iLogger.info(reqObj.getLoginId() +"---- Webservice Input Dashboard------" + startDate);
		iLogger.info("Current Startdate: "+startDate);
		//aj20119610 - adding impl for nickName based search query
				String nickName=null;
				if(reqObj!=null){
					if(reqObj.getLoginId()!=null){
						if(reqObj.getLoginId().split("\\|").length>2){
						if(reqObj.getLoginId().split("\\|")[2]!=null)
							nickName=reqObj.getLoginId().split("\\|")[2];
						}
					}
					if(nickName!=null && !nickName.isEmpty())
						nickName=nickName.trim();
				}
		long startTime = System.currentTimeMillis();
		long endTime=System.currentTimeMillis();
		iLogger.info(reqObj.getLoginId() + "Webservice Execution Time in ms Dashboard:"+(endTime-startTime));
	
		iLogger.info("loginId:"+reqObj.getLoginId()+",  "+"userTenancyIdList:"+reqObj.getUserTenancyIdList()+",   " +
				"tenancyIdList:"+reqObj.getTenancyIdList()+",  "+"serialNumber:"+reqObj.getSerialNumber()+",  "+"nickName:"+nickName+",  "+
				"machineProfileIdList:"+reqObj.getMachineProfileIdList()+",  " +
				"modelList: "+reqObj.getModelList()+",  "+"machineGroupTypeIdList:"+reqObj.getMachineGroupTypeIdList()+",   " +
				"machineGroupIdList: "+reqObj.getMachineGroupIdList()+",  "+"alertSeverityList:"+reqObj.getAlertSeverityList()+",   " +
				"alertTypeIdList: "+reqObj.getAlertTypeIdList()+",  "+"pageNumber:"+reqObj.getPageNumber()+", "+"MobileNumber:"+reqObj.getMobilenumber()+",  " +
				"landmarkCategoryIdList: "+reqObj.getLandmarkCategoryIdList()+",  "+"landmarkIdList:"+reqObj.getLandmarkIdList()+", "+"ownStock:"+reqObj.isOwnStock());
		try {
		//DF20181015 - KO369761 - Extracting CSRF Token from login field.
		CommonUtil util = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
//		if(reqObj.getLoginId().split("\\|").length > 1){
//			csrfToken=reqObj.getLoginId().split("\\|")[1];
//			reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]);
//
//			//DF20181011 - KO369761 - Validating the CSRF Token against login id.
//			if(csrfToken != null)
//				isValidCSRF=util.validateANTICSRFTOKEN(reqObj.getLoginId(),csrfToken);
//			if(!isValidCSRF){
//				iLogger.info("AssetDashBoard :: Details ::  Invalid request.");
//				throw new CustomFault("Invalid request.");
//			}
//		}
		
		//DF20180925 ::MA369757 :: Security checks for the response fields
		String isValidinput=null;
		ListToStringConversion convert=new ListToStringConversion();
		String UserTenancyIdListString="",TenancyIdListString="",MachineProfileIdListString="",ModelListString="",MachineGroupTypeIdListString="",MachineGroupIdListString="",AlertSeverityListString="",AlertTypeIdListString="",LandmarkCategoryIdListString="",LandmarkIdListString="";
		if(reqObj.getUserTenancyIdList()!=null && reqObj.getUserTenancyIdList().size()>0)
		{
			UserTenancyIdListString=convert.getIntegerListString(reqObj.getUserTenancyIdList()).toString();
		}
		if(reqObj.getTenancyIdList()!=null && reqObj.getTenancyIdList().size()>0)
		{
			TenancyIdListString=convert.getIntegerListString(reqObj.getTenancyIdList()).toString();
		}
		if(reqObj.getMachineProfileIdList()!=null && reqObj.getMachineProfileIdList().size()>0)
		{
			MachineProfileIdListString=convert.getIntegerListString(reqObj.getMachineProfileIdList()).toString();
		}
		if(reqObj.getModelList()!=null && reqObj.getModelList().size()>0)
		{
			ModelListString=convert.getIntegerListString(reqObj.getModelList()).toString();
		}
		if(reqObj.getMachineGroupTypeIdList()!=null && reqObj.getMachineGroupTypeIdList().size()>0)
		{
			MachineGroupTypeIdListString=convert.getIntegerListString(reqObj.getMachineGroupTypeIdList()).toString();
		}
		if(reqObj.getMachineGroupIdList()!=null && reqObj.getMachineGroupIdList().size()>0)
		{
			MachineGroupIdListString=convert.getIntegerListString(reqObj.getMachineGroupIdList()).toString();
		}
		if(reqObj.getAlertSeverityList()!=null && reqObj.getAlertSeverityList().size()>0)
		{
			AlertSeverityListString=convert.getStringWithoutQuoteList(reqObj.getAlertSeverityList()).toString();
		}
		if(reqObj.getAlertTypeIdList()!=null && reqObj.getAlertTypeIdList().size()>0)
		{
			AlertTypeIdListString=convert.getIntegerListString(reqObj.getAlertTypeIdList()).toString();
		}
		if(reqObj.getLandmarkCategoryIdList()!=null && reqObj.getLandmarkCategoryIdList().size()>0)
		{
			LandmarkCategoryIdListString=convert.getIntegerListString(reqObj.getLandmarkCategoryIdList()).toString();
		}
		if(reqObj.getLandmarkIdList()!=null && reqObj.getLandmarkIdList().size()>0)
		{
			LandmarkIdListString=convert.getIntegerListString(reqObj.getLandmarkIdList()).toString();
		}
		isValidinput = util.inputFieldValidation(reqObj.getPageNumber()+"");
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(UserTenancyIdListString);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(TenancyIdListString);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(MachineProfileIdListString);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(ModelListString);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(MachineGroupTypeIdListString);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(MachineGroupIdListString);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(AlertSeverityListString);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(AlertTypeIdListString);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(LandmarkCategoryIdListString);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(LandmarkIdListString);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);

		iLogger.info("Decoded userId::"+reqObj.getLoginId());

		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

		if(reqObj.getUserTenancyIdList()!=null && reqObj.getUserTenancyIdList().size()>0){
			reqObj.setUserTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getUserTenancyIdList()));
		}

		if(reqObj.getTenancyIdList()!=null && reqObj.getTenancyIdList().size()>0){
			reqObj.setTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getTenancyIdList()));
				}
		
		response = new AssetDashboardImpl().getAssetDashboardDetails(reqObj,nickName);
		
		
		
		
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("serialNumber:"+response.get(i).getSerialNumber()+",  "+"nickName:"+response.get(i).getNickName()+",   " +
					"machineStatus:"+response.get(i).getMachineStatus()+",  "+"lifeHours:"+response.get(i).getLifeHours()+",  "+
					"fuelLevel:"+response.get(i).getFuelLevel()+",  " +
					"dueForService: "+response.get(i).getDueForService()+",  "+"latitude:"+response.get(i).getLatitude()+",   " +
					"longitude: "+response.get(i).getLongitude()+",  "+"notes:"+response.get(i).getNotes()+",   " +
					"connectivityStatus: "+response.get(i).getConnectivityStatus()+",  "+"externalBatteryInVolts:"+response.get(i).getExternalBatteryInVolts()+",   " +
					"highCoolantTemperature: "+response.get(i).getHighCoolantTemperature()+",  "+"lowEngineOilPressure:"+response.get(i).getLowEngineOilPressure()+",   " +
					"modelName: "+response.get(i).getModelName()+",  "+"profileName:"+response.get(i).getProfileName()+",  "+
					"ExternalBatteryStatus:"+response.get(i).getExternalBatteryStatus()+",  "+"EngineTypeName:"+response.get(i).getEngineTypeName() +
					//DefectId:20140206 Engine_Status newParameter added 2014-02-06 @Suprava 
					", engineStatus:"+response.get(i).getEngineStatus() +
					//DefectID: DF20131212 - Rajani Nagaraju - To return the last communicated timestamp of the machine.
					", LastReportedTime:"+response.get(i).getLastReportedTime() +
					//DefectId:20140211 AssetImage newParameter added @suprava
					", AssetImageFileName:"+response.get(i).getAssetImage() + ", LastPktReceivedTime: "+response.get(i).getLastPktReceivedTime()) ;
			
			//DF20180925 ::MA369757 :: Security checks for the response fields
			if(response.get(i).getSerialNumber()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getSerialNumber().split("\\|")[0]);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getNickName()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getNickName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getMachineStatus()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getMachineStatus());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getLifeHours()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getLifeHours());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getFuelLevel()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getFuelLevel());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getDueForService()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getDueForService());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getLatitude()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getLatitude());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getLongitude()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getLongitude());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getNotes()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getNotes());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getConnectivityStatus()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getConnectivityStatus());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getExternalBatteryInVolts()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getExternalBatteryInVolts());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getHighCoolantTemperature()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getHighCoolantTemperature());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getLowEngineOilPressure()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getLowEngineOilPressure());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getModelName()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getModelName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getProfileName()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getProfileName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getExternalBatteryStatus()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getExternalBatteryStatus());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getEngineTypeName()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getEngineTypeName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getEngineStatus()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getEngineStatus());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getLastReportedTime()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getLastReportedTime());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getAssetImage()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getAssetImage());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			if(response.get(i).getLastPktReceivedTime()!=null){
			isValidinput = util.inputFieldValidation(response.get(i).getLastPktReceivedTime());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			}
			Calendar cal1 = Calendar.getInstance();
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
			String endDate = sdf1.format(cal1.getTime());
			iLogger.info("Current Enddate: " + endDate);
			endTime = System.currentTimeMillis();
			iLogger.info("serviceName:AssetDashboardService~executionTime:"+(endTime - startTime)+"~"+UserID+"~");
			
		}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			iLogger.info("Error"+e);
			}
		
		return response;
	}

}
