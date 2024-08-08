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

import remote.wise.service.datacontract.UnderUtilizedMachinesReqContract;
import remote.wise.service.datacontract.UnderUtilizedMachinesRespContract;
import remote.wise.service.implementation.UnderUtilizedMachinesImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
//import remote.wise.util.WiseLogger;

/**
 *  WebService class to get UnderUtilized Machines
 * @author jgupta41
 *
 */
@WebService(name = "UnderUtilizedMachinesService")
public class UnderUtilizedMachinesService {
		
	/**
	 *  This method gets UnderUtilized Machines that belongs specified Period, List of Tenancy ID and for filters if provided
	 * @param reqObj:Get UnderUtilized Machines for given Period, List of Tenancy ID and for filters
	 * @return : respObj Returns List of UnderUtilized Machines
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetUnderUtilizedMachines", action = "GetUnderUtilizedMachines")
	public List<UnderUtilizedMachinesRespContract> getUnderUtilizedMachines(@WebParam(name="reqObj") UnderUtilizedMachinesReqContract reqObj) throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("UnderUtilizedMachinesService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("<---- Webservice Input ------>");
		iLogger.info("Period:"+reqObj.getPeriod()+" , ");
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(reqObj.getLoginID());
				reqObj.setLoginID(UserID);
				iLogger.info("Decoded userId::"+reqObj.getLoginID());
				

				//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

			if(reqObj.getTenancy_ID()!=null && reqObj.getTenancy_ID().size()>0){
				reqObj.setTenancy_ID(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getTenancy_ID()));
			}
				
		List<UnderUtilizedMachinesRespContract> respObj = new UnderUtilizedMachinesImpl().getUnderUtilizedMachines(reqObj);
		iLogger.info("<----- Webservice Output----->");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UpdateAssetEventAddress~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		return respObj;
	}
}
