package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.jws.WebParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.FleetSummaryReqContract;
import remote.wise.service.datacontract.FleetSummaryRespContract;
import remote.wise.service.implementation.FleetSummaryImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
@Path("/FleetSummaryRESTService")
public class FleetSummaryServiceRESTService {
	
	//DF20190308:Abhishek::RESTService for FleetSummaryService

	@POST
	@Path("getFleetSummaryDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FleetSummaryRespContract>getFleetSummaryService(@JsonProperty HashMap<String,Object> input ) throws CustomFault
	{
		FleetSummaryReqContract request= new FleetSummaryReqContract();
		request.setPeriod((String)input.get("period"));
		request.setAlertSeverity((List<String>)input.get("alertSeverity"));
		request.setAlertTypeIdList((List<Integer>)input.get("alertTypeIdList"));
		request.setAssetGroupIdList((List<Integer>)input.get("assetGroupIdList"));
		request.setAssetTypeIdList((List<Integer>)input.get("assetTypeIdList"));
		//request.setContactId((String)input.get("contactId"));
		request.setCustomAssetGroupIdList((List<Integer>)input.get("customAssetGroupIdList"));
		request.setLandmarkIdList((List<Integer>)input.get("landmarkIdList"));
		request.setMachineGroupId((List<Integer>)input.get("machineGroupId"));
		request.setNotificationDimensionID((List<Integer>)input.get("notificationDimensionID"));
		request.setOwnStock((Boolean)input.get("isOwnStock"));
		request.setTenancyIdList((List<Integer>)input.get("tenancyIdList"));
		

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("FleetSummaryService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("period:"+request.getPeriod()+ "contactId:"+request.getContactId()+ "tenancyIdList :"+request.getTenancyIdList()+ "assetGroupIdList :"+request.getAssetGroupIdList()+ "assetTypeIdList :"+request.getAssetTypeIdList()+ "customAssetGroupIdList :"+request.getCustomAssetGroupIdList()+"landmarkIdList :"+request.getLandmarkIdList()+"alertTypeIdList"+request.getAlertTypeIdList()+"alertSeverity :"+request.getAlertSeverity()+"machineGroupId :"+request.getMachineGroupId()+ "notificationDimensionID :"+request.getNotificationDimensionID());
		
		//DF20170919 @Roopa getting decoded UserId
		//String UserID=new CommonUtil().getUserId(request.getContactId());
		String UserID=new CommonUtil().getUserId((String)input.get("contactId"));
		request.setContactId(UserID);
		iLogger.info("Decoded userId::"+request.getContactId());
		

		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

			
			if(request.getTenancyIdList()!=null && request.getTenancyIdList().size()>0){
				request.setTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(request.getTenancyIdList()));
			}
			
		
		List<FleetSummaryRespContract> response=new FleetSummaryImpl().getFleetSummaryService(request);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("totalIdleHours:"+response.get(i).getTotalIdleHours()+",  "+"totalWorkingHours:"+response.get(i).getTotalWorkingHours()+",   " +
					"totalOffHours:"+response.get(i).getTotalOffHours()+",   " +response.get(i).getResp_Date());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:FleetSummaryRESTService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		return response;
	}

}
