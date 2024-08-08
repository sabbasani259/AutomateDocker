package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.LandmarkActivityReportReqContract;
import remote.wise.service.datacontract.LandmarkActivityReportRespContract;
import remote.wise.service.implementation.LandmarkActivityReportImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;


/** Webservice to get the Machine Arrival/Departure Status at a Landmark
 * @author jgupta41
 *
 */
@WebService(name = "LandmarkActivityReportService")
public class LandmarkActivityReportService 
{
	/** WebMethod that returns the Machine Activity Details at a landmark
	 * @param reqObj Filters the MachineActivity at a specific Landmark OR Landmark Category
	 * @return Returns the Machine Activity Details
	 */
	@WebMethod(operationName = "GetLandmarkActivityReport", action = "GetLandmarkActivityReport")
	public List<LandmarkActivityReportRespContract> getLandmarkActivityReport(@WebParam(name="ReqObj") LandmarkActivityReportReqContract reqObj)
	{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("LandmarkActivityReportService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("fromDate"+reqObj.getFromDate()+"toDate"+reqObj.getToDate()+" Period:"+reqObj.getPeriod()+",  "+" LandmarkCategoryIDList:"+reqObj.getLandmarkCategoryIDList() 
				+ ",  "+" LandmarkIdList :" + reqObj.getLandmarkIdList()+ ",  "+" MachineGroupIdList :" + reqObj.getMachineGroupIdList());
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
				reqObj.setLoginId(UserID);
				iLogger.info("Decoded userId::"+reqObj.getLoginId());
				
		List<LandmarkActivityReportRespContract> response = new LandmarkActivityReportImpl().getLandmarkActivityReport(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("Row "+i+": ");
			iLogger.info("landmarkId: " +response.get(i).getLandmarkId()+", landMarkName: " +response.get(i).getLandMarkName() + 
					", landmarkCategoryId: " +response.get(i).getLandmarkCategoryId()+", landMarkCategoryName: " +response.get(i).getLandMarkCategoryName() +
					", serialNumber: " +response.get(i).getSerialNumber() +
					", nickname: " +response.get(i).getNickname()+
					", numberOfArrivals: " +response.get(i).getNumberOfArrivals() +
					", numberOfdepartures: " +response.get(i).getNumberOfdepartures() +
					", totalDurationAtLandmarkInMinutes: " +response.get(i).getTotalDurationAtLandmarkInMinutes() +
					", longestDurationAtLandmarkInMinutes: " +response.get(i).getLongestDurationAtLandmarkInMinutes()+
					", machineGroupId: " +response.get(i).getMachineGroupId()+
					", machineGroupName: " +response.get(i).getMachineGroupName()+
					", dealerName: " +response.get(i).getDealerName());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:LandmarkActivityReportService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		return response;
	}

}
