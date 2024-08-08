package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.SavedReportReqContract;
import remote.wise.service.datacontract.SavedReportRespContract;
import remote.wise.service.datacontract.UserAlertPreferenceRespContract;
import remote.wise.service.implementation.SavedReportImpl;
import remote.wise.service.implementation.UserAlertPreferenceImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;
@WebService(name = "SavedReportService")
public class SavedReportService {
		
	@WebMethod(operationName = "GetSavedReportService", action = "GetSavedReportService")
	public List<SavedReportRespContract> getSavedReportService(@WebParam(name = "reqObj") SavedReportReqContract reqObj)throws CustomFault/*@WebParam(name="reportId")List<Integer> reportId,@WebParam(name="contactId")List<String> contactId*/
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//	WiseLogger infoLogger = WiseLogger.getLogger("SavedReportService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("<-----Webservice Input----->");
		iLogger.info("ContactId:"+reqObj.getContactId()+" , ReportId:"+reqObj.getReportId()+"");
		//DF20170919 @Roopa getting decoded UserId
		
		
				String UserID=new CommonUtil().getUserId(reqObj.getContactId().get(0));
				
				 List<String> contactId = new ArrayList<String>();
				 contactId.add(UserID);
				
				reqObj.setContactId(contactId);
				iLogger.info("Decoded userId::"+reqObj.getContactId());
				
		List<SavedReportRespContract> response=new SavedReportImpl().getSavedReportService(reqObj);
		iLogger.info("<-----Webservice Output----->");
		for(int i=0;i<response.size();i++){
			iLogger.info(i+" ROW");
			iLogger.info("ContactId:"+response.get(i).getContactId()+" , ReportId:"+response.get(i).getReportId()+" , ReportName:"+response.get(i).getReportName()+" , FilterNameFieldValue:"+response.get(i).getFilterNameFieldValue()+" , ReportDescription:"+response.get(i).getReportDescription()+"");
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:SavedReportService~executionTime:"+(endTime-startTime)+"~"+reqObj.getContactId()+"~");
		return response;
	}


	@WebMethod(operationName = "SetSavedReportService", action = "SetSavedReportService")
	public String setSavedReportService(@WebParam(name="respObj") SavedReportRespContract respObj)throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("SavedReportService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("<-----Webservice Input----->");
		iLogger.info("ContactId:"+respObj.getContactId()+" , ReportId:"+respObj.getReportId()+" , ReportName:"+respObj.getReportName()+" , FilterNameFieldValue:"+respObj.getFilterNameFieldValue()+" , ReportDescription:"+respObj.getReportDescription()+"");
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(respObj.getContactId());
				respObj.setContactId(UserID);
				iLogger.info("Decoded userId::"+respObj.getContactId());
		
		String reponse1=new SavedReportImpl().setSavedReportService(respObj);
		iLogger.info("<-----Webservice Output----->");
		iLogger.info("status:"+reponse1);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:SavedReportService~executionTime:"+(endTime-startTime)+"~"+respObj.getContactId()+"~"+reponse1);
		return reponse1;
	}
}

