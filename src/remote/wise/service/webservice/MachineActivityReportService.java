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
import remote.wise.service.datacontract.MachineActivityReportReqContract;
import remote.wise.service.datacontract.MachineActivityReportRespContract;
import remote.wise.service.implementation.MachineActivityReportImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;
/**
 *  WebService class to get Machine Activity Report
 * @author jgupta41
 *
 */

@WebService(name = "MachineActivityReportService")
public class MachineActivityReportService {

	/**
	 * This method gets Machine Activity Report that belongs specified LoginId,Custom Dates or Period, List of Tenancy ID and for filters if provided
	 * @param reqObj:Get Machine Activity Report for given LoginId, Period, List of Tenancy ID and for filters
	 * @return respObj:Returns List of Machine Activities 
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetMachineActivityReport", action = "GetMachineActivityReport")
	public List<MachineActivityReportRespContract> getMachineActivityReport(@WebParam(name="ReqObj") MachineActivityReportReqContract reqObj) throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("MachineActivityReportService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("---- Webservice Input ------");
		iLogger.info("fromDate:"+reqObj.getFromDate()+"toDate:"+reqObj.getToDate()+"Period:"+reqObj.getPeriod()+ "LoginId:"+reqObj.getLoginId()+ "MachineGroup_ID :"+reqObj.getMachineGroup_ID()+ "MachineProfile_ID :"+reqObj.getMachineProfile_ID()+ "Tenancy_ID :"+reqObj.getTenancy_ID()+ "" +
				" Model_ID :"+reqObj.getModel_ID()+ "MachineGroupType_ID :"+reqObj.getMachineGroupType_ID()+ " isGroupingOnAssetGroup :"+reqObj.isGroupingOnAssetGroup() +
				//DefectID:1406 - Rajani Nagaraju - 20131028 - MachineGrouping issue in Reports and sending Report Totals information
				" LoginTenancyIDList:"+reqObj.getLoginTenancyIdList()+"Min Threshold:"+reqObj.getMinThreshold()+"Max Threshold:"+reqObj.getMaxThreshold());
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
				reqObj.setLoginId(UserID);
				iLogger.info("Decoded userId::"+reqObj.getLoginId());
				
				//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

				if(reqObj.getLoginTenancyIdList()!=null && reqObj.getLoginTenancyIdList().size()>0){
					reqObj.setLoginTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getLoginTenancyIdList()));
				}
				
				if(reqObj.getTenancy_ID()!=null && reqObj.getTenancy_ID().size()>0){
					reqObj.setTenancy_ID(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getTenancy_ID()));
				}
				
		List<MachineActivityReportRespContract> respObj = new MachineActivityReportImpl().getMachineActivityReport(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<respObj.size(); i++)
		{
			iLogger.info("Row "+i+" : ");
			iLogger.info("serialNumber:"+respObj.get(i).getSerialNumber()+",  "+"MachineGroup_Id:"+respObj.get(i).getMachineGroup_Id()+",   " +
					"MachineGroup_Name:"+respObj.get(i).getMachineGroup_Name()+",  "+"AssetGroup_ID"+respObj.get(i).getAssetGroup_ID()+",  "+
					"ProfileName:"+respObj.get(i).getProfileName()+",  " +"TotalMachineLifeHours: "+respObj.get(i).getTotalMachineLifeHours()+
					"AssetTypeId:"+respObj.get(i).getAssetTypeId()+",  " +"AssetTypeName:"+respObj.get(i).getAssetTypeName()+
					"MachineHours:"+respObj.get(i).getMachineHours()+",  " +"Status:"+respObj.get(i).getStatus()+
					"Duration_in_status:"+respObj.get(i).getDuration_in_status()+",  " +"Location:"+respObj.get(i).getLocation()+
					"Tenancy_ID:"+respObj.get(i).getTenancy_ID()+",  " +"tenancyName:"+respObj.get(i).getTenancyName()+", "+
					"Dealername:"+respObj.get(i).getDealerName());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MachineActivityReportService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respObj;
	}

}
