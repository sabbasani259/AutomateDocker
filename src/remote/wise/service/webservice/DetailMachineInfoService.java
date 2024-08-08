/**
 * 
 */
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
import remote.wise.service.datacontract.DetailMachineInfoReqContract;
import remote.wise.service.datacontract.DetailMachineInfoRespContract;
import remote.wise.service.datacontract.MachineActivityReportReqContract;
import remote.wise.service.datacontract.MachineActivityReportRespContract;
import remote.wise.service.implementation.DetailMachineImpl;
import remote.wise.service.implementation.MachineActivityReportImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

/**
 * @author sunayak
 *
 */
@WebService(name = "DetailMachineInfoService")
public class DetailMachineInfoService {

	/**
	 * This method gets Machine Activity Report that belongs specified LoginId,Custom Dates or Period, List of Tenancy ID and for filters if provided
	 * @param reqObj:Get Machine Activity Report for given LoginId, Period, List of Tenancy ID and for filters
	 * @return respObj:Returns List of Machine Activities 
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "getDetailMachineInfo", action = "getDetailMachineInfo")
	public List<DetailMachineInfoRespContract> getDetailMachineInfo(@WebParam(name="reqObj") DetailMachineInfoReqContract reqObj) throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("DetailMachineInfoService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("---- Webservice Input ------");
	//	iLogger.info("");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(reqObj.getContact_id());
				reqObj.setContact_id(UserID);
				iLogger.info("Decoded userId::"+reqObj.getContact_id());
				
		List<DetailMachineInfoRespContract> respObj = new DetailMachineImpl().getMachineInfo(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<respObj.size(); i++)
		{
			iLogger.info("Row "+i+" : ");
			iLogger.info("serialNumber:"+respObj.get(i).getSerialNumber()+",  "+"Current_Owner:"+respObj.get(i).getCurrent_Owner()+",   " +
					"FW_Version_Number:"+respObj.get(i).getFW_Version_Number()+",  "+"Last_Reported"+respObj.get(i).getLast_Reported()+",  "+
					"Machine_Hour:"+respObj.get(i).getMachineHour()+",  " +"RollOff_Date: "+respObj.get(i).getRollOff_Date());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:DetailMachineInfoService~executionTime:"+(endTime - startTime)+"~"+UserID+"~");
		return respObj;
	}

}
