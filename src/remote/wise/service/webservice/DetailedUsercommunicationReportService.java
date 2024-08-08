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
import remote.wise.service.datacontract.DetailedUsercommunicationReportInputContract;
import remote.wise.service.datacontract.DetailedUsercommunicationReportOutputContract;
import remote.wise.service.datacontract.MachineBillingReportInputContract;
import remote.wise.service.datacontract.MachineBillingReportOutputContract;
import remote.wise.service.implementation.DetailedUsercommunicationReportImpl;
import remote.wise.service.implementation.MachineBillingReportImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

@WebService(name = "DetailedUsercommunicationReportService")
public class DetailedUsercommunicationReportService {

	@WebMethod(operationName = "GetDetailedUsercommunicationReport", action = "GetDetailedUsercommunicationReport")
	
	public List<DetailedUsercommunicationReportOutputContract> getDetailedUsercommunicationReport(@WebParam(name="reqObj") DetailedUsercommunicationReportInputContract reqObj)throws CustomFault{
//WiseLogger infoLogger = WiseLogger.getLogger("DetailedUsercommunicationReportService:","info");
		Logger iLogger = InfoLoggerClass.logger;	
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("FromDate:"+reqObj.getFromDate()+","+"ToDate:"+reqObj.getToDate()+","+"ContactId:"+reqObj.getContactId()+","+"PhoneNumber:"+reqObj.getPhoneNumber()+","+"Email:"+reqObj.getEmail()+","+"Sms:"+reqObj.getSms());
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getContactId());
		reqObj.setContactId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getContactId());
		
		List<DetailedUsercommunicationReportOutputContract> respObj = new DetailedUsercommunicationReportImpl().getDetailedUsercommunication(reqObj);
		for(int i=0; i<respObj.size(); i++){
			iLogger.info("serialNumber:"+respObj.get(i).getSerialNumber());
			iLogger.info("InstallDate:"+respObj.get(i).getEventGeneratedTime());
			iLogger.info("AletType:"+respObj.get(i).getAlertDesc());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:DetailedUsercommunicationReportService~executionTime:"+(endTime - startTime)+"~"+UserID+"~");
		return respObj;
	}




}
