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

//import org.apache.log4j.Logger;

import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.ReportMasterListReqContract;
import remote.wise.service.datacontract.ReportMasterListRespContract;
import remote.wise.service.implementation.ReportMasterListImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

@Path( "ReportMasterListServiceRestService")
public class ReportMasterListServiceRestService {
		
	@Path( "GetReportMasterListService")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<ReportMasterListRespContract> getReportMasterListService(ReportMasterListReqContract reqContract)
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("ReportMasterListService:","info");
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		infoLogger.info("---- Webservice Input ------");
		infoLogger.info("loginId:"+reqContract.getLoginId()+"");
		/*ReportMasterListReqContract reqContract=new ReportMasterListReqContract();
		reqContract.setLoginId(loginId);*/
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(reqContract.getLoginId());
				reqContract.setLoginId(UserID);
				infoLogger.info("Decoded userId::"+reqContract.getLoginId());
				
		List<ReportMasterListRespContract> response=new ReportMasterListImpl().getReportMasterListService(reqContract);
		infoLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++){
			infoLogger.info("reportId:"+response.get(i).getReportId()+","+"reportName:"+response.get(i).getReportName()+","+"reportDescription:"+response.get(i).getReportDescription()+"");
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		infoLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		infoLogger.info("serviceName:ReportMasterListService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		return response;
	}

}
