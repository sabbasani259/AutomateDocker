package remote.wise.service.implementation;

import java.sql.Timestamp;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessobject.FotaAuthenticationBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.FotaAuthenticationReqContract;
import remote.wise.service.datacontract.FotaAuthenticationRespContract;
//import remote.wise.util.WiseLogger;

public class FotaAuthenticationImpl {
	
	//Logger infoLogger = Logger.getLogger("infoLogger");
	/*public static WiseLogger infoLogger = WiseLogger.getLogger("FotaAuthenticationImpl:","info");
	public static WiseLogger businessError = WiseLogger.getLogger("FotaAuthenticationImpl:","businessError");*/
	
	
	private String imeiNo;
	private String versionId;
	private String sessionId;
	private Timestamp updateDate;
	private String status;
	private String file_path;
	
	public String getImeiNo() {
		return imeiNo;
	}


	public void setImeiNo(String imeiNo) {
		this.imeiNo = imeiNo;
	}


	public String getVersionId() {
		return versionId;
	}


	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}


	public String getSessionId() {
		return sessionId;
	}


	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}


	public Timestamp getUpdateDate() {
		return updateDate;
	}


	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getFile_path() {
		return file_path;
	}


	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}

	public FotaAuthenticationRespContract getFotaAuthentication(FotaAuthenticationReqContract reqObj ) throws CustomFault
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		if(reqObj.getFotaimeiNumber()==null || reqObj.getFotaimeiNumber().equals("") )
		{
			bLogger.error("Please provide IMEI no. !!!");
			throw new CustomFault("Please provide status !!!");		
		}
		if(reqObj.getFotasessionId()==null || reqObj.getFotasessionId().equals("")){
			bLogger.error("Please provide session id. !!!");
			throw new CustomFault("Please provide session id. !!!");
		}
		FotaAuthenticationRespContract respObj = new FotaAuthenticationRespContract();
		FotaAuthenticationBO  fotaAuthenticationBO = new FotaAuthenticationBO();
		FotaAuthenticationImpl fotaAuthenticationImpl = fotaAuthenticationBO.imeiAuthentication(reqObj.getFotaimeiNumber(), reqObj.getFotasessionId());
		respObj.setStatus(fotaAuthenticationImpl.getStatus());
		respObj.setSessionId(fotaAuthenticationImpl.getSessionId());
		return respObj;
	}	

}
