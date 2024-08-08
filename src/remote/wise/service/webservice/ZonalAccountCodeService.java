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
import remote.wise.service.datacontract.ZonalAccountReqContract;
import remote.wise.service.datacontract.ZonalAccountRespContract;
import remote.wise.service.implementation.ZonalAccountCodeImpl;
import remote.wise.util.CommonUtil;

/**
 * @author roopn5
 *
 */
@WebService(name = "ZonalAccountCodeService")
public class ZonalAccountCodeService {
	
	
	@WebMethod(operationName = "GetZoneDealerAccounts", action = "GetZoneDealerAccounts")	
	public List<ZonalAccountRespContract> GetZoneDealerAccounts(@WebParam(name="reqObj") ZonalAccountReqContract reqObj)throws CustomFault{

		Logger iLogger = InfoLoggerClass.logger;
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
				
		List<ZonalAccountRespContract> response= new ZonalAccountCodeImpl().getZones(reqObj);	
		iLogger.info("----- Webservice Output-----");
		
		for(int i=0; i<response.size(); i++){
			iLogger.info("ZoneAccountCode:"+response.get(i).getZoneAccountCode()+","+"zoneAccountName:"+response.get(i).getZoneAccountName()+","+"dealer:"+response.get(i).getDealer()+","+"dealerMap:"+response.get(i).getDealerMap()+"");
		}
		
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:ZonalAccountCodeService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		return response;
	}
}

