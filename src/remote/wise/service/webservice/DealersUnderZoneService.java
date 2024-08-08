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
import remote.wise.service.datacontract.DealersUnderZoneReqContract;
import remote.wise.service.datacontract.DealersUnderZoneRespContract;
import remote.wise.service.implementation.DealersUnderZoneImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

/**
 * @author kprabhu5
 *
 */
@WebService(name = "DealersUnderZoneService")
public class DealersUnderZoneService {
	
	
	@WebMethod(operationName = "GetDealers", action = "GetDealers")	
	public List<DealersUnderZoneRespContract> getDealersForZone(@WebParam(name="reqObj") DealersUnderZoneReqContract reqObj)throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("DealersUnderZoneService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//	Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("----- Webservice Input-----");
		iLogger.info("loginId:"+reqObj.getLoginId()+","+"loginTenancyId:"+reqObj.getLoginTenancyId()+"");
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
				reqObj.setLoginId(UserID);
				iLogger.info("Decoded userId::"+reqObj.getLoginId());
				
		List<DealersUnderZoneRespContract> response= new DealersUnderZoneImpl().getDealers(reqObj);	
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++){
			iLogger.info("zoneId:"+response.get(i).getZoneId()+","+"zoneName:"+response.get(i).getZoneName()+","+"dealer:"+response.get(i).getDealer()+","+"dealerMap:"+response.get(i).getDealerMap()+"");
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:DealersUnderZoneService~executionTime:"+(endTime - startTime)+"~"+""+"~");
		return response;
	}
}

