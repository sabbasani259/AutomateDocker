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
import remote.wise.service.datacontract.DealersUnderZoneRespContract;
import remote.wise.service.datacontract.FotaUpgradedHistoryDetailsReqContract;
import remote.wise.service.datacontract.FotaUpgradedHistoryDetailsRespContract;
import remote.wise.service.implementation.DealersUnderZoneImpl;
import remote.wise.service.implementation.FotaUpgradedHistoryDetailsImpl;
//import remote.wise.util.WiseLogger;
import remote.wise.util.CommonUtil;

@WebService(name = "FotaUpgradedHistoryDetailsService")
public class FotaUpgradedHistoryDetailsService {
	@WebMethod(operationName = "getFotaUpgradedDetails", action = "getFotaUpgradedDetails")	
	public List<FotaUpgradedHistoryDetailsRespContract> getFotaUpgradedDetails(@WebParam(name="reqObj") FotaUpgradedHistoryDetailsReqContract reqObj)throws CustomFault{
 
		//WiseLogger infoLogger = WiseLogger.getLogger("FotaUpgradedHistoryDetailsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		
		//DF20180713: KO369761 - Security Check added for input text fields.
		CommonUtil util = new CommonUtil();
		String isValidinput=null;
		
		iLogger.info("----- Webservice Input-----");
		if(reqObj.getSerialNumberList()!=null){
			for(int i=0; i<reqObj.getSerialNumberList().size(); i++){
				iLogger.info("serial No. :"+reqObj.getSerialNumberList().get(i));

				isValidinput = util.inputFieldValidation(reqObj.getSerialNumberList().get(i));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		
		List<FotaUpgradedHistoryDetailsRespContract> response=null;
		response = new FotaUpgradedHistoryDetailsImpl().getFotaUpgradedDetails(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++){
			iLogger.info("serial no :"+response.get(i).getSerialNumber()+","+" upgraded version :"+response.get(i).getUpgradedVersion()+" FW Version :"+response.get(i).getFWVersion());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:FotaUpgradedHistoryDetailsService~executionTime:"+(endTime-startTime)+"~"+""+"~");		
		
		return response;
	}
}
