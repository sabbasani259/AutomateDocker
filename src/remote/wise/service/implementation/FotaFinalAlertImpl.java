package remote.wise.service.implementation;

////import org.apache.log4j.Logger;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.FotaFinalAlertBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;

import remote.wise.service.datacontract.FotaFinalAlertReqContract;
import remote.wise.service.datacontract.FotaFinalAlertRespContract;
//import remote.wise.util.WiseLogger;

public class FotaFinalAlertImpl {
	
	//public static WiseLogger infoLogger = WiseLogger.getLogger("FotaFinalAlertImpl:","info");
	private String sessionId;
	private String status;
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public FotaFinalAlertRespContract getFotaFinalAlert(FotaFinalAlertReqContract reqObj ) throws CustomFault
	{
		//Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
    	
		if(reqObj.getImei()== null || reqObj.getImei().equals("")){
			iLogger.info("Please provide IMEI no. !!!");
			throw new CustomFault("Please provide IMEI no. !!!");		
		}
		if(reqObj.getSessionId()== null || reqObj.getSessionId().equals("")){
			iLogger.info("Please provide session id !!!");
			throw new CustomFault("Please provide session id !!!");
		}
		if(reqObj.getStatus()== null || reqObj.getStatus().equals("")){
			iLogger.info("Please provide status !!!");
			throw new CustomFault("Please provide status !!!");		
		}
		FotaFinalAlertRespContract respObj = new FotaFinalAlertRespContract();
		FotaFinalAlertBO  fotaFinalAlertBO = new FotaFinalAlertBO();
		FotaFinalAlertImpl fotaFinalAlert = fotaFinalAlertBO.updateStatus2(reqObj.getImei(),reqObj.getSessionId(), reqObj.getStatus());
		respObj.setStatus(fotaFinalAlert.getStatus());
		respObj.setSessionId(fotaFinalAlert.getSessionId());
		return respObj;
	}

}
