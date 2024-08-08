package remote.wise.service.webservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.DueOverDueReportReqContract;
import remote.wise.service.datacontract.DueOverDueReportRespContract;
import remote.wise.service.implementation.DueOverDueReportImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;

/**Webservice that returns the service due/overdue details of machines
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "MachineDueOverdueReportService")
public class MachineDueOverdueReportService 
{
	/**Webmethod that returns the List of machines under service due/overdue
	 * @param reqObj Input filters to get the list of VINs
	 * @return due/overdue details of VINs 
	 * @throws CustomFault
	 * @throws ParseException 
	 */
	@WebMethod(operationName = "GetDueOverDueMachines", action = "GetDueOverDueMachines")
	public List<DueOverDueReportRespContract> getDueOverDueMachines
	(@WebParam(name="reqObj" ) DueOverDueReportReqContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("MachineDueOverdueReportService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("loginId:"+reqObj.getLoginId()+",  "+"tenancyIdList:"+reqObj.getTenancyIdList()+",   " +
				"machineGroupIdList:"+reqObj.getMachineGroupIdList()+",  "+"machineProfileIdList:"+reqObj.getMachineProfileIdList()+",  "+"" +
				"modelIdList:"+reqObj.getModelIdList()+",  " +"isOverdueReport: "+reqObj.isOverdueReport());
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());
		
		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

		
		if(reqObj.getTenancyIdList()!=null && reqObj.getTenancyIdList().size()>0){
			reqObj.setTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getTenancyIdList()));
		}
		
		List<DueOverDueReportRespContract> response = new DueOverDueReportImpl().getDueOverDueMachines(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("Row "+i+": ");
			iLogger.info("tenancyId:"+response.get(i).getTenancyId()+",  "+"tenancyName:"+response.get(i).getTenancyName()+",   " +
					"machineGroupId:"+response.get(i).getMachineGroupId()+",  "+"machineGroupName:"+response.get(i).getMachineGroupName()+",  "+
					"machineProfileId:"+response.get(i).getMachineProfileId()+",  " +
					"machineProfileName: "+response.get(i).getMachineProfileName()+",  "+"modelId:"+response.get(i).getModelId()+",  "+
					"modelName: "+response.get(i).getModelName()+",  "+"machineName:"+response.get(i).getMachineName()+",  "+
					"serialNumber: "+response.get(i).getSerialNumber()+",  "+"customerName:"+response.get(i).getCustomerName()+",  "+
					"customerContactNumber: "+response.get(i).getCustomerContactNumber()+",  "+"operatorName:"+response.get(i).getOperatorName()+",  "+
					"operatorContactNumber: "+response.get(i).getOperatorContactNumber()+",  "+"dealerName:"+response.get(i).getDealerName()+",  "+
					"dealerContactNumber: "+response.get(i).getDealerContactNumber()+",  "+"dueOrOverDueHours:"+response.get(i).getDueOrOverDueHours()+",  "+
					"dueOrOverDueDays: "+response.get(i).getDueOrOverDueDays()+",  "+"serviceName:"+response.get(i).getServiceName()+",  "+
					"scheduleName: "+response.get(i).getScheduleName()+",  "+"plannedServiceDate:"+response.get(i).getPlannedServiceDate()+",  "+
					"plannedServiceHours: "+response.get(i).getPlannedServiceHours()+","+"DealerName: "+response.get(i).getDealerName()+"  totalEngineHours: "+response.get(i).getTotalMachineHours());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MachineDueOverdueReportService~executionTime:"+(endTime-startTime)+"~"+reqObj.getLoginId()+"~");
		return response;
	}
}
