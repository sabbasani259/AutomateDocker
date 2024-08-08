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
import remote.wise.service.datacontract.UtilizationDetailReportReqContract;
import remote.wise.service.datacontract.UtilizationDetailReportRespContract;
import remote.wise.service.implementation.UtilizationDetailReportImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;

/** WebService that pulls out the report for Details MachineUtilization for the given time period
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "UtilizationDetailReportService")
public class UtilizationDetailReportService 
{	
	/** Webservice method that pulls out details of machine utilization in the given period
	 * @param reqObj loginId, period and other filters are specified through this reqObj
	 * @return Returns the utilization details for the user accessible list of serial Numbers
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetUtilizationDetailReport", action = "GetUtilizationDetailReport")
	public List<UtilizationDetailReportRespContract> getUtilizationDetailReport
	(@WebParam(name="reqObj" ) UtilizationDetailReportReqContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("UtilizationDetailReportService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("LoginId:"+reqObj.getLoginId()+",  "+"Period:"+reqObj.getPeriod()+",   " +
				"tenancyIdList:"+reqObj.getTenancyIdList()+",  "+"machineGroupIdList:"+reqObj.getMachineGroupIdList()+",  " +
				"machineProfileIdList:"+reqObj.getMachineProfileIdList()+",  " +"modelIdList: "+reqObj.getModelIdList()+", " +"SerialNumberList: "+reqObj.getSerialNumList());
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
				reqObj.setLoginId(UserID);
				iLogger.info("Decoded userId::"+reqObj.getLoginId());
				

				//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

			
			if(reqObj.getTenancyIdList()!=null && reqObj.getTenancyIdList().size()>0){
				reqObj.setTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getTenancyIdList()));
			}
					
		
		List<UtilizationDetailReportRespContract> response = new UtilizationDetailReportImpl().getMachineUtilizationReportDetails(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("Row "+i+": ");
			iLogger.info("serialNumber:"+response.get(i).getSerialNumber()+",  "+"nickName:"+response.get(i).getNickName()+",   " +
					"dateInString:"+response.get(i).getDateInString()+",  "+"dayInString:"+response.get(i).getDayInString()+",  "+
					"timeMachineStatusMap:"+response.get(i).getTimeMachineStatusMap()+",  " +
					"EngineRunDuration: "+response.get(i).getEngineRunDuration()+",  "+"EngineOffDuration:"+response.get(i).getEngineOffDuration()+",   " +
					"EngineWorkingDuration: "+response.get(i).getEngineWorkingDuration()+",  "+"machineUtilizationPerct:"+response.get(i).getMachineUtilizationPerct());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UtilizationDetailReportService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		return response;
	}
}
